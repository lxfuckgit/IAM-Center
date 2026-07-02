package com.iamcenter.domain.party;

import java.io.Serializable;
import java.sql.Timestamp;

import org.hibernate.annotations.GenericGenerator;

import com.javapai.framework.common.domain.TopBaseDomain;
import com.javapai.framework.enums.StatusEnum;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Table;

/**
 * Party是一个极度抽象的表述。 <br>
 * Party抽象极了，不同的<a>PartyType</a>可表示不同对象。
 * 
 * @author liu.xiang
 * 
 */
@Entity
@Table(name = "HY100")
@Inheritance(strategy = InheritanceType.JOINED)
public class Party extends TopBaseDomain implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 会员标识<br>
	 */
	@Id
	@Column(name = "id", length = 32)
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid")
	private String partyId;
	/**
	 * 外部ID标识.
	 */
	@Column(name = "externalId", length = 32)
	private String externalId;

	/**
	 * 登录号/userLoginId
	 */
	// @Column(name = "userName", length = 15)
	// private String userName;

	/**
	 * 密码
	 */
	// @Column(name = "password", length = 32)
	// private String password;

	/**
	 * 用户类型<br>
	 * 取值参考PartyTypeEnum.
	 */
	@Column(name = "partyTypeId", length = 32)
	private String partyTypeId;
	// private PartyType partyTypeId;

	/**
	 * 用户当前状态<br>
	 * 取值参考:StatusEnum.java<br>
	 */
	// @Enumerated(StatusUserEnum.USER_ENABLE)
	@Column(name = "status_id", nullable = false, length = 32)
	private String statusId;

	/**
	 * 注册来源<br>
	 * 取值参考FromEnum.
	 */
	@Column(name = "comeFrom", length = 16)
	private String comeFrom;

	/**
	 * 推广码/邀请码
	 */
	@Column(name = "invate_code", unique = true, length = 16)
	private String invateCode;

	/**
	 * 主要角色(当前角色).<br>
	 * 取值参考RoleEnum
	 */
	@Column(name = "roleTypeId", length = 32)
	private String roleTypeId;
	// private String primaryRoleTypId;
	/**
	 * 最后操作人
	 */
	@Column(name = "updateUserId")
	private String updateUserId;

	/**
	 * 创建时间
	 */
	@Column(name = "create_time")
	private Timestamp createTime;

	/**
	 * 更新时间(数据版本锁)
	 */
	@Column(name = "update_time")
	private Timestamp updateTime;

	public String getPartyId() {
		return partyId;
	}

	public void setPartyId(String partyId) {
		this.partyId = partyId;
	}

	public String getExternalId() {
		return externalId;
	}

	public void setExternalId(String externalId) {
		this.externalId = externalId;
	}

	public String getPartyTypeId() {
		return partyTypeId;
	}

	public void setPartyTypeId(String partyTypeId) {
		this.partyTypeId = partyTypeId;
	}

	public String getStatusId() {
		if (null == statusId || "".equals(statusId)) {
			return StatusEnum.INIT.getValue();
		} else {
			return statusId;
		}
	}

	public void setStatusId(String statusId) {
		this.statusId = statusId;
	}

	public String getComeFrom() {
		return comeFrom;
	}

	public void setComeFrom(String comeFrom) {
		this.comeFrom = comeFrom;
	}

	public String getRoleTypeId() {
		return roleTypeId;
	}

	public void setRoleTypeId(String roleTypeId) {
		this.roleTypeId = roleTypeId;
	}

	public String getUpdateUserId() {
		return updateUserId;
	}

	public void setUpdateUserId(String updateUserId) {
		this.updateUserId = updateUserId;
	}

	public Timestamp getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Timestamp createTime) {
		this.createTime = createTime;
	}

	public Timestamp getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Timestamp updateTime) {
		this.updateTime = updateTime;
	}

}
