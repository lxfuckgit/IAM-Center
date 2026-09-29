package com.iamcenter.business;

import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.iamcenter.domain.apps.AppInfo;
import com.iamcenter.domain.security.SysLogin;
import com.iamcenter.repository.SysLoginRepository;
import com.iamcenter.repository.apps.AppInfoRepository;

@Component
public class TokenBusiness {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private SysLoginRepository sysLoginRepository;
	
	@Autowired
	private AppInfoRepository appInfoRepository;
	
	@Value("${jwt.expiration:3600000}")
	private Long expiration;

	@Transactional
	public boolean updateLoginToken(Long loginId, String newToken) {
		Optional<SysLogin> optional = sysLoginRepository.findById(loginId);
		if (!optional.isPresent() || StringUtils.isBlank(newToken)) {
			logger.info("--->用户[{}]Token更新异常：[{}]Login信息或[{}]Token信息未找到!", loginId, newToken);
			return false;
		}
		if (optional.get().getLoginToken().equals(newToken)) {
			logger.info("--->用户[{}]Token已存在!", loginId, newToken);
			return false;
		}
		optional.get().setLoginToken(newToken);
		optional.get().setLoginExpire(System.currentTimeMillis() + expiration);
		sysLoginRepository.save(optional.get());
		logger.info("--->用户[{}]token已更新完成!", loginId);
		return true;
	}
	
	public String getTokenAssocAppId() {
		return SecurityContextHolder.getContext().getAuthentication().getDetails().toString();
	}
	
	public String getTokenAssocCompanyId() {
		String appId = getTokenAssocAppId();
		AppInfo appInfo = appInfoRepository.findByAppId(Long.valueOf(appId));
		if (null == appInfo || StringUtils.isBlank(appInfo.getCompanyId())) {
			return null;
		} else {
			return appInfo.getCompanyId();
		}
	}

}
