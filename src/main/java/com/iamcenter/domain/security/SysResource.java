package com.iamcenter.domain.security;

import java.io.Serializable;

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
public class SysResource implements Serializable {
	private static final long serialVersionUID = 1L;

	private static final int default_sort = 99;

	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "app_id", length = 30, nullable = false)
	private String appId;

	/**
	 * 资源编号.
	 */
	@Column(name = "code", length = 50, unique = true, nullable = true)
	private String code;
	/**
	 * 资源名称.
	 */
	@Column(name = "name", length = 60, nullable = false)
	private String name;
	/**
	 * 资源icon.
	 */
	@Column(name = "icon", length = 100)
	private String icon;
	/**
	 * 资源入口地址.
	 */
	@Column(name = "url", length = 150, nullable = true)
	private String url;
	/**
	 * 资源类型.<br>
	 * [按纽Button、菜单Menu、模块Moudule、子系统System、默认根Root]<br>
	 * <strong>提示：</strong>菜单类型的资源必有上级节点（即使没有父菜单节点也必要会有关联一个模块或是子系统）。
	 */
	@Column(name = "type", length = 150, nullable = false)
	private String type;
	/**
	 * 
	 */
	@Column(name = "parent", length = 150)
	private String parent;
	/**
	 * 资源排序号。<br>
	 * <strong>提示：</strong>在未指定排序号时，系统将指定默认排序号{@linkplain this#default_sort}。<br>
	 */
	@Column(name = "sort", length = 2)
	private Integer sort;
	/**
	 * 模块描述
	 */
	@Column(name = "remark", length = 222)
	private String remark;

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

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getParent() {
		return parent;
	}

	public void setParent(String parent) {
		this.parent = parent;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Integer getSort() {
		if (null == sort) {
			sort = default_sort;
		}
		return sort;
	}

	public void setSort(Integer sort) {
		if (null == sort) {
			sort = default_sort;
		}
		this.sort = sort;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

}
