package com.iamcenter.domain.security;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import com.javapai.framework.common.domain.AbstractDomain;

/**
 * 用户密码(修改)记录;<br>
 * 
 * @author pooja
 *
 */
@Entity
@Table(name = "sys_pwd_history")
public class SysPwdHistory extends AbstractDomain {
	@Id
	@Column(name = "id")
	private long logId;
	/**
	 * 
	 */
	@Column(name = "login_id", length = 32, nullable = false)
	private Long loginId;
	/**
	 * 
	 */
	@Column(name = "password", length = 32, nullable = false)
	private String password;

	public long getLogId() {
		return logId;
	}

	public void setLogId(long logId) {
		this.logId = logId;
	}

	public Long getLoginId() {
		return loginId;
	}

	public void setLoginId(Long loginId) {
		this.loginId = loginId;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

}
