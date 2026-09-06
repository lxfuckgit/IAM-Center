package com.iamcenter.controller.dto;

/**
 * 应用启动/停用参数。
 */
public class ChangeAppStatusDTO {
	/** 应用标识 */
	private String appId;
	/** 目标应用状态 */
	private String appStatus;

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public String getAppStatus() {
		return appStatus;
	}

	public void setAppStatus(String appStatus) {
		this.appStatus = appStatus;
	}

}
