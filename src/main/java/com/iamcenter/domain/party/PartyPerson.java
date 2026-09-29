package com.iamcenter.domain.party;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

/**
 * person info.<br>
 * 用户信息描述.<br>
 * 
 * @author liu.xiang
 *
 */
@Entity
@Table(name = "HY102")
public class PartyPerson extends Party implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 租户ID（公司ID） <br>
	 * 提示：此字段解决同一个人在不同公司的情况。
	 */
	@Column(name = "company_id", length = 32)
	private String companyId;

	/**
	 * 用户编号
	 */
	@Column(name = "code", length = 16, nullable = false)
	private String code;

	/**
	 * 姓氏
	 */
	@Column(name = "first_name", length = 20)
	private String firstName;

	/**
	 * 名字
	 */
	@Column(name = "last_name", length = 20)
	private String lastName;

	/**
	 * 用户姓名
	 */
	@Column(name = "nick_name", length = 15)
	private String nickName;

	/**
	 * 用户性别(F/M).
	 */
	@Column(name = "sex", length = 1)
	private char sex;

	/**
	 * 用户生日
	 */
	@Column(name = "birthday", length = 16)
	private String birthday;

	/**
	 * 个人头像
	 */
	@Column(name = "icon_url", length = 32)
	private String iconUrl;

	/**
	 * 身份证号
	 */
	@Column(name = "sfz", length = 18)
	private String idCard;

	/**
	 * (当前)手机号
	 */
	@Column(name = "mobile_phone", length = 15)
	private String mobilePhone;

//	/**
//	 * 用户QQ
//	 */
//	@Column(name = "userQQ", length = 15)
//	private String userQQ;

//	/**
//	 * 用户邮箱
//	 */
//	@Column(name = "email", length = 30)
//	private String email;

	public String getCompanyId() {
		return companyId;
	}

	public void setCompanyId(String companyId) {
		this.companyId = companyId;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public char getSex() {
		return sex;
	}

	public void setSex(char sex) {
		this.sex = sex;
	}

	public String getBirthday() {
		return birthday;
	}

	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}

	public String getIconUrl() {
		return iconUrl;
	}

	public void setIconUrl(String iconUrl) {
		this.iconUrl = iconUrl;
	}

	public String getIdCard() {
		return idCard;
	}

	public void setIdCard(String idCard) {
		this.idCard = idCard;
	}

	public String getMobilePhone() {
		return mobilePhone;
	}

	public void setMobilePhone(String mobilePhone) {
		this.mobilePhone = mobilePhone;
	}

}