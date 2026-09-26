package com.iamcenter.common.filter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.iamcenter.business.RBACBusiness;
import com.iamcenter.config.jwt.JwtUtil;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * 第三方JWT认证过滤器。
 */
@Component
public class JWTAuthenticationFilter extends OncePerRequestFilter {
	private static final Logger logger = LoggerFactory.getLogger(JWTAuthenticationFilter.class);
	
	@Value("${config.auth.ignoreUrls:}")
	private String ignoreUrlString;
	
	@Value("${jwt.refreshThreshold:1800000}")
	private Long refreshThreshold;

	private List<String> ignoreUrlList = new ArrayList<String>();

	@Autowired
	private JwtUtil jwtUtil;
	
	@Autowired
	private RBACBusiness rbacBusiness;
	
	@Override
	public void afterPropertiesSet() throws ServletException {
		if (!ObjectUtils.isEmpty(ignoreUrlString)) {
			String[] urls = ignoreUrlString.split(",");
			for (String url : urls) {
				ignoreUrlList.add(url.trim());
			}
		}
	}
	
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
		if (ignoreUrlList.contains(request.getRequestURI())) {
			logger.warn("--->系统内置安全请求，跳过jwt token authentication！");
			filterChain.doFilter(request, response);
			return;
		}
		if (null == request.getContentType() || !request.getContentType().startsWith("application/json")) {
			logger.warn("--->非指定请求类型，跳过处理！");
			filterChain.doFilter(request, response);
			return;
		}
		String authorization = request.getHeader("Authorization");
		if (ObjectUtils.isEmpty(authorization) || !authorization.startsWith("Bearer ")) {
			logger.warn("--->非指定令牌格式，跳过处理！");
			filterChain.doFilter(request, response);
			return;
		}
		/* 验证Token有效性 */
		String token = authorization.substring(7);
		if (!validateTokenAndGrantedAuthority(token)) {
			logger.warn("--->Token令牌过期，请重新登录！");
			response.setStatus(HttpServletResponse.SC_OK);
			response.setContentType("application/json;charset=UTF-8");
			response.getWriter().write("{\"code\":\"40000111\",\"message\":\"token登录授权码无效或已过期!\"}");
			return;
		}
		
		/* 验证Token有效性并自动刷新Token（这种后端自动刷新机制，不仅浪费后端性能且不符合freshToken的设计，间接增加token盗用有效期） */
//		io.jsonwebtoken.Claims claims = jwtUtil.parseToken(token);
//		java.util.Date expiration = claims.getExpiration();
//		java.util.Date now = new java.util.Date();
//		if (expiration.before(now)) {
//			logger.warn("--->Token令牌已过期！");
//			response.setStatus(HttpServletResponse.SC_OK);
//			response.setContentType("application/json;charset=UTF-8");
//			response.getWriter().write("{\"code\":\"40000111\",\"message\":\"token登录授权码无效或已过期!\"}");
//			return;
//		}
		// 当剩余有效期小于refreshThreshold值时自动刷新Token
//		if (expiration.getTime() - now.getTime() < refreshThreshold) {
//			String userId = claims.getSubject();
//			java.util.Map<String, Object> newClaims = claims;
//			String newToken = jwtUtil.generateToken(userId, newClaims);
//			logger.info("--->Token即将过期，已自动刷新！");
//			tokenBusiness.updateLoginToken(Long.valueOf(userId), newToken);
//			response.setHeader("Authorization", "Bearer " + newToken);
//		}
//		// 设置authentication并结束当前filter
//		List<org.springframework.security.core.GrantedAuthority> authorities = new ArrayList<>();
//		authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
//		UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(jwtUtil.extractSubject(token), null, authorities);
//		SecurityContextHolder.getContext().setAuthentication(authentication);
		
		// 结束当前filter
		filterChain.doFilter(request, response);
	}

	private boolean validateTokenAndGrantedAuthority(String token) {
		Claims claims = jwtUtil.parseToken(token);
		if (null == claims) {
			return false;
		}
		Date expiration = claims.getExpiration();
		Date now = new Date();
		if (expiration.before(now)) {
			logger.warn("--->Token令牌已过期！");
			return false;
		}
		/* 设置当前用户的authentication */
		// 改读取源(优先读缓存）
//		List<String> roleList = claims.get("roles", List.class);
		List<String> roleList = rbacBusiness.listRoleCodeByLoginId(Long.valueOf(claims.getSubject()));
		List<GrantedAuthority> authorities = roleList == null ? Collections.emptyList() : roleList.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());
		UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(claims.getSubject(), null, authorities);
		SecurityContextHolder.getContext().setAuthentication(authentication);
		return true;
	}
	
}
