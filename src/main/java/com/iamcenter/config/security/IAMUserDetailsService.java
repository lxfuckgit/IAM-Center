package com.iamcenter.config.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import com.iamcenter.domain.security.SysLogin;
import com.iamcenter.repository.SysLoginRepository;
import com.javapai.framework.enums.StatusEnum;

@Component
public class IAMUserDetailsService implements UserDetailsService {
	@Autowired
	private SysLoginRepository sysLoginRepository;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		String[] array = username.split("");
//		if (null == array && array.length != 2) {
//			throw new UsernameNotFoundException("--->用户不存在: " + username);
//		}
		// 检查登录信息
		SysLogin login = sysLoginRepository.findByAppIdAndLoginName(array[0], array[1]);
		if (null == login) {
			throw new UsernameNotFoundException("--->用户不存在: " + username);
		}
		if (StatusEnum.DISABLE.name().equals(login.getLoginState())) {
			throw new UsernameNotFoundException("--->用户限制登录！ " + username);
		}
		return User.builder().username(login.getLoginName()).password(login.getLoginPwd()).build();
	}

}
