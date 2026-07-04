package com.iamcenter.config.security;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.iamcenter.config.jwt.JwtUtil;

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

	private List<String> ignoreUrlList = new ArrayList<String>();

	@Autowired
	private JwtUtil jwtUtil;
	
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
		if (!request.getContentType().startsWith("application/json")) {
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
		String token = authorization.substring(7);
		if (!jwtUtil.validateToken(token)) {
			logger.warn("--->Token令牌过期，请重新登录！");
			// 这里直接返回，不尝试用 Refresh Token 刷新
			response.setStatus(HttpServletResponse.SC_OK);
	        response.setContentType("application/json;charset=UTF-8");
			response.getWriter().write("{\"code\":\"40000111\",\"message\":无效的token登录授权码!}");
			return;
		}
//		io.jsonwebtoken.Claims claims = jwtUtil.parseToken(token);
		List<org.springframework.security.core.GrantedAuthority> authorities = new ArrayList<>();
		authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
		UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(jwtUtil.extractSubject(token), null,authorities);
		SecurityContextHolder.getContext().setAuthentication(authentication);
		/*
		// 查询Token关联用户(高性能的内存取对象)
		String token = authorization.substring(7);
//		SysLogin login = sysLoginRepository.findByLoginToken(token);
//		if (null == login || StatusEnum.DISABLE.getValue().equals(login.getLoginState())) {
//			logger.warn("--->提示：[{}]用户的令牌已禁用！", login.getLoginName());
//			filterChain.doFilter(request, response);
//			return;
//		}
		// 重构Request报文结构
		Long userId = Long.valueOf(jwtUtil.extractSubject(token));
		String requestIp = request.getRemoteAddr();
		XTokenRequestWrapper xTokenRequestWrapper = new XTokenRequestWrapper(request, userId, requestIp);
		*/
		filterChain.doFilter(request, response);
	}
}
