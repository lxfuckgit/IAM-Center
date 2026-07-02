package com.iamcenter.domain.security;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;

import com.javapai.framework.common.domain.TopBaseDomain;

@Entity
@Table(name = "sys_login_role")
@IdClass(SysLRolePK.class)
public final class SysLoginRole extends TopBaseDomain {
	@Id
	@Column(name = "login_id", length = 32)
//	@javax.persistence.OneToOne
//	private SysLogin loginId;
	private Long loginId;

	@Id
	@Column(name = "role_id", length = 32)
	private String roleId;

	public SysLoginRole() {
		super();
	}

	public SysLoginRole(Long loginId, String roleId) {
		super();
		this.loginId = loginId;
		this.roleId = roleId;
	}

	public Long getLoginId() {
		return loginId;
	}

	public void setLoginId(Long loginId) {
		this.loginId = loginId;
	}

	public String getRoleId() {
		return roleId;
	}

	public void setRoleId(String roleId) {
		this.roleId = roleId;
	}

}

@SuppressWarnings("serial")
@Embeddable
class SysLRolePK implements Serializable {
	private Long loginId;
	private String roleId;

	public Long getLoginId() {
		return loginId;
	}

	public void setLoginId(Long loginId) {
		this.loginId = loginId;
	}

	public String getRoleId() {
		return roleId;
	}

	public void setRoleId(String roleId) {
		this.roleId = roleId;
	}
}
