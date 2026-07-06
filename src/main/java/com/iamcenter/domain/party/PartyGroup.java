package com.iamcenter.domain.party;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;

/**
 * team、organization、company...etc
 * 
 * @author liu.xiang
 *
 */
@Entity
@Table(name = "HY101")
@PrimaryKeyJoinColumn(name = "id")
public class PartyGroup extends Party implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 团体编号
	 */
	@Column(name = "groupCode", length = 30, nullable = true)
	private String groupCode;
	
	/**
	 * 团体名称
	 */
	@Column(name = "groupName", length = 60, nullable = false)
	private String groupName;

	/**
	 * 公司法人(legalperson)
	 */
	@Column(name = "legalEntity", length = 25)
	private String legalEntity;

	/**
	 * 企业传真
	 */
	@Column(name = "fax", length = 15)
	private String fax;

	/**
	 * 企业邮箱
	 */
	@Column(name = "email", length = 30)
	private String email;

	/**
	 * 座机
	 */
	@Column(name = "telephone", length = 30)
	private String telephone;
	
	/**
	 * 手机/联系人电话
	 */
	@Column(name = "mobilePhone", length = 15)
	private String mobilePhone;

	/**
	 * 营业执照编号
	 */
	@Column(name = "licenseNo", length = 60)
	private String licenseNo;
	
	/**
	 * 三证(1)<br>
	 * 营业执照盖章复印件的图片ID.
	 */
	@Column(name = "licenseImage", length = 60)
	private String licenseImage;

	/**
	 * 组织机构编号
	 */
	@Column(name = "organizationCode", length = 60)
	private String organizationCode;
	
	/**
	 * 三证(2)<br>
	 * 组织机构代码证盖章复印件的图片ID.
	 */
	@Column(name = "organizationImage", length = 60)
	private String organizationImage;
	
	/**
	 * 税务登记编号
	 */
	@Column(name = "registrationNo", length = 60)
	private String registrationNo;
	
	/**
	 * 三证(3)<br>
	 * 税务登记证盖章复印件的图片ID.
	 */
	@Column(name = "registrationImage", length = 60)
	private String registrationImage;

	/**
	 * 企业认证状态. <br>
	 * 0-未审核，1-审核通过，2-审核不通过，3-再次提交审核
	 */
	@Column(name = "authStatusId", length = 32)
	private String authStatusId;

	/**
	 * 网站主页
	 */
	@Column(name = "website", length = 50)
	private String website;

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public String getGroupCode() {
		return groupCode;
	}

	public void setGroupCode(String groupCode) {
		this.groupCode = groupCode;
	}

	public String getFax() {
		return fax;
	}

	public void setFax(String fax) {
		this.fax = fax;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getTelephone() {
		return telephone;
	}

	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}

	public String getMobilePhone() {
		return mobilePhone;
	}

	public void setMobilePhone(String mobilePhone) {
		this.mobilePhone = mobilePhone;
	}

	public String getAuthStatusId() {
		return authStatusId;
	}

	public void setAuthStatusId(String authStatusId) {
		this.authStatusId = authStatusId;
	}

	public String getLegalEntity() {
		return legalEntity;
	}

	public void setLegalEntity(String legalEntity) {
		this.legalEntity = legalEntity;
	}

	public String getLicenseNo() {
		return licenseNo;
	}

	public void setLicenseNo(String licenseNo) {
		this.licenseNo = licenseNo;
	}

	public String getLicenseImage() {
		return licenseImage;
	}

	public void setLicenseImage(String licenseImage) {
		this.licenseImage = licenseImage;
	}

	public String getOrganizationCode() {
		return organizationCode;
	}

	public void setOrganizationCode(String organizationCode) {
		this.organizationCode = organizationCode;
	}

	public String getOrganizationImage() {
		return organizationImage;
	}

	public void setOrganizationImage(String organizationImage) {
		this.organizationImage = organizationImage;
	}

	public String getRegistrationNo() {
		return registrationNo;
	}

	public void setRegistrationNo(String registrationNo) {
		this.registrationNo = registrationNo;
	}

	public String getRegistrationImage() {
		return registrationImage;
	}

	public void setRegistrationImage(String registrationImage) {
		this.registrationImage = registrationImage;
	}

	public String getWebsite() {
		return website;
	}

	public void setWebsite(String website) {
		this.website = website;
	}
	
}
