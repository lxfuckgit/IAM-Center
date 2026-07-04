package com.iamcenter.domain.security;

import java.io.Serializable;
import java.sql.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import com.javapai.framework.common.domain.AbstractDomain;

/**
 * 系统登录 实体.<br>
 * 
 * 1：此"对象表"目的在于支持单用户(Party)支持多账号(Login)的场景.<br>
 * 2：独立于Party对象与Login对象，与方便在不同场景上的分离应用（有些只需要登录信息、有些只需要用户信息、大部分应用还是两部分都需要）。<br>
 * 
 * @author lx
 * 
 */
@Entity
@Table(name = "sys_login")
public class SysLogin extends AbstractDomain implements Serializable {
	private static final long serialVersionUID = 1L;
	/**
	 * 登录记录标识.<br>
	 * 很多情况下原因下，我们没有用此标识作登录名.
	 */
	@Id
	@Column(name = "login_id", length = 20)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long loginId;

	/**
	 * 用户账号登录.<br>
	 * 可自定义输入用户名.
	 */
	@Column(name = "login_name", length = 30, unique = true, nullable = false)
	private String loginName;
	/**
	 * 登录密码(建议使用md5加密后的密码串).<br>
	 * 账号密码 transient可用来指定当前不想被系列化的成员对象
	 */
	@Column(name = "login_pwd", length = 128, nullable = false)
	private String loginPwd;
	/**
	 * 登录授权token（为兼容jwt放宽长度）
	 */
	@Column(name = "login_token", length = 255)
	private String loginToken;

	/**
	 * 登录过期时间（毫秒数）。
	 */
	@Column(name = "login_expire")
	private Long loginExpire;

	/**
	 * 账号状态(停用)时间
	 */
	private Date expireTime;
	/**
	 * 账号状态(在线、停用、禁登录).<br>
	 * 取值参考:{@link com.javapai.framework.enums.StatusEnum}
	 */
	@Column(name = "login_state", length = 12, nullable = false)
	private String loginState;
	/**
	 * 账号关联用户.<br>
	 */
	// private Party partyId;
	@Column(name = "party_id", length = 32)
	private String partyId;
	/**
	 * 三方登录标识（External Login Id）
	 */
	@Column(name = "ext_login_id", length = 32)
	private String extLoginId;
	/**
	 * 三方登录密钥（External Login PWD)
	 */
	@Column(name = "ext_login_pwd", length = 128)
	private String extLoginPwd;
	/**
	 * 注册邀请码.<br>
	 */
	@Column(name = "invite_code", length = 50)
	private String inviteCode;
	/**
	 * 登录用户昵称
	 */
	@Column(name = "nick_name", length = 60)
	private String nickName;

	@Column(name = "icon_url", length = 200)
	private String iconUrl;

	/**
	 * 请求应用来源市场。
	 */
	private String rstSource;

	/* 1账号N角色 */
	// private Set<SysRole> roles = new HashSet<SysRole>();

	public Long getLoginId() {
		return loginId;
	}

	public void setLoginId(Long loginId) {
		this.loginId = loginId;
	}

	public String getLoginName() {
		return loginName;
	}

	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}

	public String getLoginPwd() {
		return loginPwd;
	}

	public void setLoginPwd(String loginPwd) {
		this.loginPwd = loginPwd;
	}

	public String getLoginToken() {
		return loginToken;
	}

	public void setLoginToken(String loginToken) {
		this.loginToken = loginToken;
	}

	public Long getLoginExpire() {
		return loginExpire;
	}

	public void setLoginExpire(Long loginExpire) {
		this.loginExpire = loginExpire;
	}

	public Date getExpireTime() {
		return expireTime;
	}

	public void setExpireTime(Date expireTime) {
		this.expireTime = expireTime;
	}

	public String getLoginState() {
		return loginState;
	}

	public void setLoginState(String loginState) {
		this.loginState = loginState;
	}

	public String getPartyId() {
		return partyId;
	}

	public void setPartyId(String partyId) {
		this.partyId = partyId;
	}

	public String getExtLoginId() {
		return extLoginId;
	}

	public void setExtLoginId(String extLoginId) {
		this.extLoginId = extLoginId;
	}

	public String getExtLoginPwd() {
		return extLoginPwd;
	}

	public void setExtLoginPwd(String extLoginPwd) {
		this.extLoginPwd = extLoginPwd;
	}

	public String getInviteCode() {
		return inviteCode;
	}

	public void setInviteCode(String inviteCode) {
		this.inviteCode = inviteCode;
	}

	public String getRstSource() {
		return rstSource;
	}

	public void setRstSource(String rstSource) {
		this.rstSource = rstSource;
	}

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public String getIconUrl() {
		return iconUrl;
	}

	public void setIconUrl(String iconUrl) {
		this.iconUrl = iconUrl;
	}

}
