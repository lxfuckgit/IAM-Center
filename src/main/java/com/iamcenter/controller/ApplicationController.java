package com.iamcenter.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.iamcenter.controller.dto.ChangeAppStatusDTO;
import com.iamcenter.domain.apps.AppInfo;
import com.iamcenter.service.AppInfoService;
import com.javapai.framework.action.PageResult;
import com.javapai.framework.action.RstResult;
import com.javapai.framework.common.dto.AppBaseDTO;
import com.saasapi.contract.apps.dto.AppListDTO;
import com.saasapi.contract.apps.enums.OSEnum;
import com.saasapi.contract.apps.vo.AppUpgrade;

/**
 * 应用管理接口清单。
 * 
 * @author pooja
 *
 */
@RestController
@RequestMapping("/iam")
public class ApplicationController {

	@Autowired(required = false)
	private AppInfoService appsService;

//	/**
//	 * 获取应用的版本号
//	 * 
//	 * @param appId
//	 * @return
//	 */
//	@PostMapping(value = "/getAppVersion", produces = MediaType.APPLICATION_JSON_VALUE)
//	public RstResult<AppUpgrade> getAppVersion(String appId) {
//		RstResult<AppsInfo> result = appsService.getAppsInfo(appId);
//		AppsInfo ss=result.getData();
//		return appsService.checkAppsVersion(appId, OSEnum.OS_ANDRIOD);
//	}

	/**
	 * 获取安卓应用的版本号
	 * 
	 * @param appId
	 * @return
	 */
	@PostMapping(value = "getAndroidAppVersion", produces = MediaType.APPLICATION_JSON_VALUE)
	public RstResult<AppUpgrade> getAndroidAppVersion(String appId) {
		return appsService.checkAppsVersion(appId, OSEnum.OS_ANDRIOD);
	}

	/**
	 * 获取IOS应用的版本号
	 * 
	 * @param appId
	 * @return
	 */
	@PostMapping(value = "getIosAppVersion", produces = MediaType.APPLICATION_JSON_VALUE)
	public RstResult<AppUpgrade> getIosAppVersion(String appId) {
		return appsService.checkAppsVersion(appId, OSEnum.OS_IOS);
	}

	/**
	 * 应用列表查询
	 *
	 * @param dto
	 * @return
	 */
	@PreAuthorize("hasRole('super_admin')")
	@RequestMapping("listAppInfo.php")
	public PageResult<AppInfo> listAppInfo(@RequestBody AppListDTO dto) {
		return appsService.listAppInfo(dto);
	}

	/**
	 * 应用注册
	 *
	 * @param dto
	 * @return
	 */
	@PreAuthorize("hasRole('super_admin')")
	@RequestMapping("addAppInfo.php")
	public RstResult<String> addAppInfo(@RequestBody AppInfo dto) {
		return appsService.addAppInfo(dto);
	}
	
	/**
	 * 应用读取
	 *
	 * @param dto
	 * @return
	 */
	@RequestMapping("getAppInfo.php")
	public RstResult<com.saasapi.contract.apps.vo.AppInfo> getAppInfo(@RequestBody AppBaseDTO dto) {
		return appsService.getAppInfo(dto.getAppId());
	}

	/**
	 * 应用修改
	 *
	 * @param dto
	 * @return
	 */
	@PreAuthorize("hasRole('super_admin')")
	@RequestMapping("updateAppInfo.php")
	public RstResult<String> updateAppInfo(@RequestBody AppInfo dto) {
		return appsService.updateAppInfo(dto);
	}

	/**
	 * 应用启动/停用
	 *
	 * @param dto
	 * @return
	 */
	@PreAuthorize("hasRole('super_admin')")
	@RequestMapping("changeAppStatus.php")
	public RstResult<String> changeAppStatus(@RequestBody ChangeAppStatusDTO dto) {
		return appsService.changeAppStatus(dto);
	}
}
