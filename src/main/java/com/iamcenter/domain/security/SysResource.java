package com.iamcenter.domain.security;

import java.io.Serializable;

import com.javapai.framework.common.domain.TopBaseDomain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * 实体：系统资源.<br>
 * 备注：之前有想过命名为privilege权限实体，后改名为resource实体，最终SysPrivilege已被SysResource取代.。<br>
 * <br>
 * 论点：合并和并存的选择；<br>
 * 1、以前是想让菜单权限与业务级权限独立分开,这样方便管理，但使用过程中觉得过于加重且重复设计(设计出好的api完全可屏弊上层使用上或底层安全上的问题)。<br>
 * 2、自我觉得由于privilege命名过于局限(无法表达出更多抽象东西)，便利用resource通过type属性完全可以表达出privilege权限这一实体存在的必要性。<br>
 * 
 * @author lx
 * 
 */
@Entity
@Table(name = "sys_resource")
public class SysResource extends TopBaseDomain implements Serializable {
	private static final long serialVersionUID = 1L;

	private static final int default_sort = 99;

	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "app_id", length = 30, nullable = false)
	private String appId;
	/**
	 * 上源资源类型
	 */
	@Column(name = "parent_id", length = 150)
	private Long parentId;
	/**
	 * 资源类型.<br>
	 * [按纽Button、菜单Menu、模块Moudule、子系统System、默认根Root]<br>
	 * <strong>提示：</strong>菜单类型的资源必有上级节点（即使没有父菜单节点也必要会有关联一个模块或是子系统）。
	 */
	@Column(name = "res_type", length = 150, nullable = false)
	private String resType;
	/**
	 * 资源编号.
	 */
	@Column(name = "res_code", length = 50, unique = true, nullable = false)
	private String resCode;
	/**
	 * 资源名称.
	 */
	@Column(name = "res_name", length = 60, nullable = false)
	private String resName;
	/**
	 * 资源icon.
	 */
	@Column(name = "res_icon", length = 100)
	private String resIcon;
	/**
	 * 资源入口地址.
	 */
	@Column(name = "res_url", length = 150)
	private String resUrl;
	/**
	 * 模块描述
	 */
	@Column(name = "res_remark", length = 200)
	private String resRemark;
	/**
	 * 资源排序号。<br>
	 * <strong>提示：</strong>在未指定排序号时，系统将指定默认排序号{@linkplain this#default_sort}。<br>
	 */
	@Column(name = "sequence", length = 2)
	private Integer sequence;
	/**
	 * 状态标识
	 */
	@Column(name = "status_id", length = 30, nullable = false)
	private String statusId;

	public Long getParentId() {
		return parentId;
	}

	public void setParentId(Long parentId) {
		this.parentId = parentId;
	}

	public String getResType() {
		return resType;
	}

	public void setResType(String resType) {
		this.resType = resType;
	}

	public String getResCode() {
		return resCode;
	}

	public void setResCode(String resCode) {
		this.resCode = resCode;
	}

	public String getResName() {
		return resName;
	}

	public void setResName(String resName) {
		this.resName = resName;
	}

	public String getResIcon() {
		return resIcon;
	}

	public void setResIcon(String resIcon) {
		this.resIcon = resIcon;
	}

	public String getResUrl() {
		return resUrl;
	}

	public void setResUrl(String resUrl) {
		this.resUrl = resUrl;
	}

	public String getResRemark() {
		return resRemark;
	}

	public void setResRemark(String resRemark) {
		this.resRemark = resRemark;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public Integer getSequence() {
		return null == sequence ? default_sort : sequence;
	}

	public void setSequence(Integer sequence) {
		if (null == sequence) {
			this.sequence = default_sort;
		} else {
			this.sequence = sequence;
		}
	}
	
	public String getStatusId() {
		return statusId;
	}

	public void setStatusId(String statusId) {
		this.statusId = statusId;
	}

}
