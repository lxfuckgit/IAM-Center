package com.iamcenter.config.security;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import com.iamcenter.domain.security.SysLogin;
import com.iamcenter.repository.SysLoginRepository;
import com.iamcenter.repository.SysLoginRoleDao;
import com.javapai.framework.enums.StatusEnum;

@Component
public class IAMUserDetailsService implements UserDetailsService {
	@Autowired
	private SysLoginRepository sysLoginRepository;
	@Autowired
	private SysLoginRoleDao sysLoginRoleDao;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		String[] array = username.split("#");
		if (null == array || array.length != 2) {
			throw new UsernameNotFoundException("--->用户格式错误: " + username);
		}
		// 检查登录信息
		SysLogin login = sysLoginRepository.findByAppIdAndLoginName(array[0], array[1]);
		if (null == login) {
			throw new UsernameNotFoundException("--->用户不存在: " + username);
		}
		if (StatusEnum.DISABLE.name().equals(login.getLoginState())) {
			throw new UsernameNotFoundException("--->用户限制登录！ " + username);
		}
		// 检查角色信息
		List<String> roleList = sysLoginRoleDao.listLoginRoleCode(login.getLoginId());
		// 返回UserDetails
		return User.builder().username(login.getLoginName()).password(login.getLoginPwd()).roles(roleList.toArray(new String[0])).build();
	}

}
