package com.iamcenter.config.security;

import java.io.IOException;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Spring Security官方推荐的方式，专门用于处理认证异常。
 */
public class IAMAuthenticationEntryPoint implements AuthenticationEntryPoint {

	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
		//现在直接在登录那里手工捕获异常，对应返回信息。
		System.out.println("认证失败: "+ authException.getMessage());
	}

}
