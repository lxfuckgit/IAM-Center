package com.iamcenter.domain.security;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import com.javapai.framework.common.domain.AbstractDomain;

/**
 * 系统 登录 日志
 * 
 * @author lx
 * 
 */
@Entity
@Table(name = "sys_login_log")
public class SysLoginLog extends AbstractDomain {
	/**
	 * 主键标识
	 */
	@Id
	@Column(name = "id", length = 32)
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid")
	private String id;
	/**
	 * 用户(用户登录)标识(=SysLogin.loginId=userId)
	 */
	@Column(name = "loginId", length = 32)
	private String loginId;
	/**
	 * 
	 */
	private String loginpwd;
	/**
	 * 登录类型(密码、短信码、oauth...)
	 */
	private String loginType;
	/**
	 * 登录时间(系统自动生成).
	 * 时间格式：yyyy-MM-dd HH:MM:sss
	 */
	private String loginDate;
	/**
	 * 登录结果.成功还是失败.
	 */
	private String loginResult;
	/**
	 * 
	 */
	private String loginDesc;
	/**
	 * 登录来源
	 */
	@Column(name = "comeFrom", length = 16)
	private String comeFrom;
	/**
	 * 登录IP
	 */
	@Column(name = "clientIp", length = 16)
	private String clientIp;
	/**
	 * 渠道标号
	 */
	@Column(name = "chid", length = 16)
	private String chid;
	
//    long user_id;
//    String photos;
//    String phoneos;
//    String device_id_type;
//    String device_id;
//    String phone_number;
//    String phone_model;
//    String phone_name;
//    String phone_ip;
//    String imsi;
//    String mac;
//    String idfa;
//    String imei;
//    String type;
//    String ip;
//    String version;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getLoginId() {
		return loginId;
	}

	public void setLoginId(String loginId) {
		this.loginId = loginId;
	}

	public String getLoginpwd() {
		return loginpwd;
	}

	public void setLoginpwd(String loginpwd) {
		this.loginpwd = loginpwd;
	}

	public String getLoginDate() {
		return loginDate;
	}

	public void setLoginDate(String loginDate) {
		this.loginDate = loginDate;
	}

	public String getLoginType() {
		return loginType;
	}

	public void setLoginType(String loginType) {
		this.loginType = loginType;
	}

	public String getLoginResult() {
		return loginResult;
	}

	public void setLoginResult(String loginResult) {
		this.loginResult = loginResult;
	}

	public String getLoginDesc() {
		return loginDesc;
	}

	public void setLoginDesc(String loginDesc) {
		this.loginDesc = loginDesc;
	}

	public String getComeFrom() {
		return comeFrom;
	}

	public void setComeFrom(String comeFrom) {
		this.comeFrom = comeFrom;
	}

	public String getClientIp() {
		return clientIp;
	}

	public void setClientIp(String clientIp) {
		this.clientIp = clientIp;
	}

	public String getChid() {
		return chid;
	}

	public void setChid(String chid) {
		this.chid = chid;
	}

	@Override
	public String toString() {
		String msg = "用户:" + loginId + "在" + loginDate + "用密码[" + loginpwd + "]登录系统!" + loginDesc;
		return msg;
	}
}
