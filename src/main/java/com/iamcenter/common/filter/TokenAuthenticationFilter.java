//package com.iamcenter.config.security;
//
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.List;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Component;
//import org.springframework.util.ObjectUtils;
//import org.springframework.web.filter.OncePerRequestFilter;
//
//import com.iamcenter.business.SecurityBusiness;
//import com.iamcenter.domain.security.SysLogin;
//import com.iamcenter.repository.SysLoginRepository;
//import com.javapai.framework.enums.StatusEnum;
//import com.javapai.framework.wrapper.XTokenRequestWrapper;
//
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//
///**
// * 常规Token认证过滤器。
// */
//@Component
//public class TokenAuthenticationFilter extends OncePerRequestFilter {
//	private static final Logger logger = LoggerFactory.getLogger(TokenAuthenticationFilter.class);
//
//	@Value("${config.auth.ignoreUrls:}")
//	private String ignoreUrlString;
//
//	private List<String> urlList = new ArrayList<String>();
//
//	private SysLoginRepository sysLoginRepository;
//
//	public TokenAuthenticationFilter(SysLoginRepository sysLoginRepository, SecurityBusiness securityBusiness) {
//		this.sysLoginRepository = sysLoginRepository;
//	}
//
//	@Override
//	public void afterPropertiesSet() throws ServletException {
//		if (!ObjectUtils.isEmpty(ignoreUrlString)) {
//			String[] urls = ignoreUrlString.split(",");
//			for (String url : urls) {
//				urlList.add(url.trim());
//			}
//		}
//	}
//
//	@Override
//	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
//		if (request.getContentType().startsWith("application/json")) {
//			String token = request.getHeader("token");
//			String requestURI = request.getRequestURI();
//			if (ObjectUtils.isEmpty(token) || urlList.contains(requestURI)) {
//				filterChain.doFilter(request, response);
//				return;
//			}
//			// 查询Token关联用户
//			SysLogin login = sysLoginRepository.findByLoginToken(token);
//			if (null == login || StatusEnum.DISABLE.getValue().equals(login.getLoginState())) {
//				logger.warn("--->提示：[{}]用户的令牌已禁用！", login.getLoginName());
//				filterChain.doFilter(request, response);
//				return;
//			}
//			// 重构Request报文结构
//			Long userId = login.getLoginId();
//			String requestIp = request.getRemoteAddr();
//			XTokenRequestWrapper xTokenRequestWrapper = new XTokenRequestWrapper(request, userId, requestIp);
//			filterChain.doFilter(xTokenRequestWrapper, response);
//		} else {
//			logger.warn("--->非指定请求内型，跳过处理！");
//			filterChain.doFilter(request, response);
//		}
//	}
//}
