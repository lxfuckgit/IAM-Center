package com.iamcenter.domain.apps;

import com.javapai.framework.common.domain.TopBaseDomain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "app_info")
public class AppInfo extends TopBaseDomain {
	/**
	 * 应用标识
	 */
	@Id
	@Column(name = "app_id")
	private Long appId;
	/**
	 * 应用编号
	 */
	@Column(name = "app_code", length = 32, nullable = false)
	private String appCode;
	/**
	 * 应用名称
	 */
	@Column(name = "app_name", length = 60, nullable = false)
	private String appName;
	/**
	 * 应用状态
	 */
	@Column(name = "app_status", length = 32)
	private String appStatus;
	/**
	 * 应用联系人
	 */
	@Column(name = "app_provider", length = 32)
	private String appProvider;
	/**
	 * 应用联系方式
	 */
	@Column(name = "app_contact", length = 32)
	private String appContact;

	public Long getAppId() {
		return appId;
	}

	public void setAppId(Long appId) {
		this.appId = appId;
	}

	public String getAppCode() {
		return appCode;
	}

	public void setAppCode(String appCode) {
		this.appCode = appCode;
	}

	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

	public String getAppStatus() {
		return appStatus;
	}

	public void setAppStatus(String appStatus) {
		this.appStatus = appStatus;
	}

	public String getAppProvider() {
		return appProvider;
	}

	public void setAppProvider(String appProvider) {
		this.appProvider = appProvider;
	}

	public String getAppContact() {
		return appContact;
	}

	public void setAppContact(String appContact) {
		this.appContact = appContact;
	}

}
