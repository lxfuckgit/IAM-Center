package com.iamcenter.business;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.iamcenter.domain.apps.AppInfo;
import com.iamcenter.repository.apps.AppInfoRepository;
import com.javapai.framework.action.ResultBuilder;
import com.javapai.framework.action.RstResult;
import com.javapai.framework.enums.ErrorCode;
import com.javapai.framework.enums.StatusEnum;

@Component
public class ValidateBusiness {
	@Value("${spring.application.name}")
	private String applicationName;

	@Autowired
	AppInfoRepository appInfoRepository;

	/**
	 * 检查当前登录账号的应用的可用性。<br>
	 *
	 * @param appId 应用标识。<br>
	 * 
	 */
	public RstResult<String> checkLoginAppId(String appId) {
		if (StringUtils.isBlank(appId)) {
			return ResultBuilder.buildResult(ErrorCode.PARAMS_APPID);
		}
		if (applicationName.equals(appId.trim())) {
			return ResultBuilder.normalResult();
		}
		AppInfo appInfo = appInfoRepository.findByAppId(Long.valueOf(appId));
		if (null == appInfo) {
			return ResultBuilder.buildResult("40000002", "应用标识不存在!");
		}
		if (!appInfo.getAppStatus().equals(StatusEnum.ENABLE.getValue())) {
			return ResultBuilder.buildResult("40000003", "此应用已被停用!");
		}

		RstResult<String> result = ResultBuilder.normalResult();
		result.setData(appInfo.getCompanyId());
		return result;
	}
	
}
