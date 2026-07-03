package com.iamcenter.config.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
	// 1. 配置 PasswordEncoder
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	// 2. 配置 UserDetailsService（从数据库加载）
	@Bean
	public UserDetailsService userDetailsService() {
		return new IAMUserDetailsService();
	}

	// 3. 配置 SecurityFilterChain
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http.authorizeHttpRequests(auth -> auth
				// 公开路径，如登录页面、静态资源等
				.requestMatchers("/", "/iam/pwdLogin.php", "/iam/pwdRegister.php", "/static/**").permitAll()
				// 需要特定角色才能访问
				.requestMatchers("/admin/**").hasRole("ADMIN")
				// 其他任何请求都需要认证
				.anyRequest().authenticated())
		// 启用表单登录（默认提供一个登录页面）
//				.formLogin(Customizer.withDefaults())
				// 如果前后端分离，禁用表单登录和CSRF
				.formLogin(form -> form.disable())
				// 启用 HTTP Basic 认证（适用于 RESTful API）
//				.httpBasic(Customizer.withDefaults())
				// 默认开启 CSRF 保护，如果前后端分离可考虑关闭
				.csrf(csrf -> csrf.disable());

		return http.build();
	}

}
