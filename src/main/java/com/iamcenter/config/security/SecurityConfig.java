package com.iamcenter.config.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.util.StringUtils;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
	@Value("${config.auth.ignoreUrls}")
	private String ignoreUrlString;

	@Autowired
	private JWTAuthenticationFilter jwtAuthenticationFilter;

	// 1. 配置 PasswordEncoder
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	// 2. 配置 AuthenticationManager
	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
		return config.getAuthenticationManager();
	}

	// 3. 配置 SecurityFilterChain
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http.authorizeHttpRequests(auth -> auth
				// 公开路径，如登录页面、静态资源等
				.requestMatchers(StringUtils.tokenizeToStringArray(ignoreUrlString, ",")).permitAll()
//				.requestMatchers("/", "/iam/pwdLogin.php", "/iam/pwdRegister.php", "/static/**").permitAll()
				// 需要特定角色才能访问
//				.requestMatchers("/admin/**").hasRole("ADMIN")
				// 其他任何请求都需要认证
				.anyRequest().authenticated())
		// 启用表单登录（默认提供一个登录页面）
//				.formLogin(Customizer.withDefaults())
				// 如果前后端分离，禁用表单登录和CSRF
				.formLogin(form -> form.disable())
				// 启用 HTTP Basic 认证（适用于 RESTful API）
//				.httpBasic(Customizer.withDefaults())
				.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
				// 默认开启 CSRF 保护，如果前后端分离可考虑关闭
				.csrf(csrf -> csrf.disable());

		return http.build();
	}

}
