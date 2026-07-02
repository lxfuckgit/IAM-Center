package com.iamcenter.domain.security;

import java.io.Serializable;
import java.util.Set;

import com.javapai.framework.common.domain.TopBaseDomain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;

/**
 * 系统 用户角色 实体
 * 
 * @author lx
 * 
 */
@Entity
@Table(name = "sys_role")
public class SysRole extends TopBaseDomain implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "app_id", length = 30, nullable = false)
	private String appId;
	/**
	 * 角色编号
	 */
	@Column(name = "role_code", length = 30, unique = true, nullable = false)
	private String code;
	/**
	 * 角色名称
	 */
	@Column(name = "role_name", length = 50, nullable = false)
	private String name;
	/**
	 * 角色备注/角色描述
	 */
	@Column(name = "role_remark", length = 100)
	private String remark;
	/**
	 * 角色拥有权限列表
	 */
	@ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
	@JoinTable(name = "sys_role_resource", joinColumns = {
			@jakarta.persistence.JoinColumn(name = "role_id", table = "sys_role") }, inverseJoinColumns = {
					@jakarta.persistence.JoinColumn(name = "respirce_id", table = "sys_resource") })
	private Set<SysResource> resources;
//	<!--映射集合属性，t_role_privilege是连接表表名 -->
//	<set name="privilege" table="sys_role_privilege">
//		<!-- role_id 是 role表对t_role_privilegek中间表的列名，fk_role_privilege是对SysRole对象的外键引用名 -->
//		<key column="role_id" foreign-key="fk_role_privilege" />
//		<!-- id是 sys_resource中间表的列名，同时也是一个对SysPrivilege对象的外键引用名 -->
//		<many-to-many column="id" class="SysResource" />
//	</set>

	/**
	 * 创建人
	 */
	@Column(name = "creator_id", length = 32)
	private String creatorId;
	/**
	 * 修改人
	 */
	@Column(name = "update_id", length = 32)
	private String updateId;

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public Set<SysResource> getResources() {
		return resources;
	}

	public void setResources(Set<SysResource> resources) {
		this.resources = resources;
	}

	public String getCreatorId() {
		return creatorId;
	}

	public void setCreatorId(String creatorId) {
		this.creatorId = creatorId;
	}

	public String getUpdateId() {
		return updateId;
	}

	public void setUpdateId(String updateId) {
		this.updateId = updateId;
	}

}
