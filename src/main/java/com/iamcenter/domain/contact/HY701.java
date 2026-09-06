package com.iamcenter.domain.contact;

import java.io.Serializable;

import com.javapai.framework.common.domain.TopBaseDomain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Postal Address(邮寄地址、收货地址、送货地址). <br>
 * 
 * @author liu.xiang
 * 
 */
@Entity
@Table(name = "hy701")
public class HY701 extends TopBaseDomain implements Serializable {
	private static final long serialVersionUID = 1L;
	/**
	 * 主键标识.<br>
	 */
	@Id
	@Column(name = "id", length = 18)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;// 主键自增原因：业务上看来，我们一直关心收货地址及联系方式，不怎么关心id,订单货运时也是按订单送货信息进行货运。
	/**
	 * 发货人 Shipper<br>
	 * 承运人 carrier<br>
	 */
	/**
	 * 收货人.<br>
	 */
	@Column(name = "consignee", length = 50)
	private String consignee;
	/**
	 * 国家(三级)区域码.<br>
	 * 收货省、市、县(区).<br>
	 */
	@Column(name = "geo_code", length = 10)
	private String geoCode;
	/**
	 * 邮政编码.
	 */
	@Column(name = "zip_code", length = 10)
	private String zipCode;
	/**
	 * 收货人手机.<br>
	 */
	@Column(name = "mobile", length = 15)
	private String mobile;
//	/**
//	 * 收货人电话.<br>
//	 */
//	@Column(name = "telephone", length = 15)
//	private String telephone;
	/**
	 * 收货人邮箱.<br>
	 * 用来接收订单提醒邮件，便于您及时了解订单状态
	 */
	@Column(name = "emaill", length = 60)
	private String emaill;
	/**
	 * 收货地址.<br>
	 * <br>
	 */
	@Column(name = "address", length = 200)
	private String address;
	/**
	 * 是否默认地址(Y/N).<br>
	 */
	@Column(name = "sfmrdz", length = 1, nullable = false)
	private String isDefault;
	/**
	 * 状态标志(可用性).<br>
	 */
	@Column(name = "status", length = 10, nullable = false)
	private String status;
	/**
	 * 所属登录用户.<br>
	 */
	@Column(name = "login_id", length = 32, nullable = false)
	private String loginId;
	/**
	 * 收货地址备注描述.<br>
	 */
	private String description;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getConsignee() {
		return consignee;
	}

	public void setConsignee(String consignee) {
		this.consignee = consignee == null ? null : consignee.trim();
	}

	public String getGeoCode() {
		return geoCode;
	}

	public void setGeoCode(String geoCode) {
		this.geoCode = geoCode;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address == null ? null : address.trim();
	}

	public String getEmaill() {
		return emaill;
	}

	public void setEmaill(String emaill) {
		this.emaill = emaill;
	}

	public String getZipCode() {
		return zipCode;
	}

	public void setZipCode(String zipCode) {
		this.zipCode = zipCode;
	}

	public String getLoginId() {
		return loginId;
	}

	public void setLoginId(String loginId) {
		this.loginId = loginId;
	}

	public String getIsDefault() {
		if (isDefault == null) {
			return "N";
		} else {
			return isDefault;
		}
	}

	public void setIsDefault(String isDefault) {
		this.isDefault = isDefault;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}
