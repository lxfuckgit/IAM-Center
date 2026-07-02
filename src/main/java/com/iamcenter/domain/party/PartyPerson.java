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
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 用户编号
	 */
	@Column(name = "code", length = 16, unique = true, nullable = false)
	private String code;

	/**
	 * 用户姓名
	 */
	@Column(name = "xm", length = 15)
	private String personName;

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
	 * 用户性别(F/M).
	 */
	@Column(name = "xb", length = 1)
	private char sex;
	
	/**
	 * 用户生日
	 */
	@Column(name = "sr", length = 16)
	private String birthday;

	/**
	 * 个人头像
	 */
	@Column(name = "iconId", length = 32)
	private String iconId;

	/**
	 * 身份证号
	 */
	@Column(name = "sfz", length = 18)
	private String idCard;

//	/**
//	 * 用户QQ
//	 */
//	@Column(name = "userQQ", length = 15)
//	private String userQQ;

//	/**
//	 * (当前)手机号.
//	 */
//	@Column(name = "dqsjh", length = 15)
//	private String userMobil;

//	/**
//	 * 用户邮箱
//	 */
//	@Column(name = "email", length = 30)
//	private String email;
	
	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getPersonName() {
		return personName;
	}

	public void setPersonName(String personName) {
		this.personName = personName;
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

	public String getIconId() {
		return iconId;
	}

	public void setIconId(String iconId) {
		this.iconId = iconId;
	}

	public String getIdCard() {
		return idCard;
	}

	public void setIdCard(String idCard) {
		this.idCard = idCard;
	}

//	public String getUserQQ() {
//		return userQQ;
//	}
//
//	public void setUserQQ(String userQQ) {
//		this.userQQ = userQQ;
//	}
//
//	public String getUserMobil() {
//		return userMobil;
//	}
//
//	public void setUserMobil(String userMobil) {
//		this.userMobil = userMobil;
//	}
//
//	public String getEmail() {
//		return email;
//	}
//
//	public void setEmail(String email) {
//		this.email = email;
//	}
//
//	public String getStatusId() {
//		return statusId;
//	}
//
//	public void setStatusId(String statusId) {
//		this.statusId = statusId;
//	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
}