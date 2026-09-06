package com.iamcenter.domain.apps;

import com.javapai.framework.common.domain.TopBaseDomain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "app_update")
public class AppUpdate extends TopBaseDomain {
	/**
	 * 更新日志id.<br>
	 */
	@Id
	@Column(name = "id", length = 32)
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	/**
	 * 关联应用标识.<br>
	 */
	@Column(name = "app_id", length = 32)
	private int appId;

	/**
	 * 关联应用系统.<br>
	 */
	@Column(name = "app_sys", length = 32)
	private String appSystem;

	/**
	 * 当前更新版本号。<br>
	 */
	private String version;
	/**
	 * 当前更新版本号(数字)。<br>
	 */
	private int versNum;
	/**
	 * 更新渠道.<br>
	 */
	private String updateChannel;
	/**
	 * 更新提示文案.<br>
	 */
	private String updateContnet;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public int getAppId() {
		return appId;
	}

	public void setAppId(int appId) {
		this.appId = appId;
	}

	public String getAppSystem() {
		return appSystem;
	}

	public void setAppSystem(String appSystem) {
		this.appSystem = appSystem;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public int getVersNum() {
		return versNum;
	}

	public void setVersNum(int versNum) {
		this.versNum = versNum;
	}

	public String getUpdateChannel() {
		return updateChannel;
	}

	public void setUpdateChannel(String updateChannel) {
		this.updateChannel = updateChannel;
	}

	public String getUpdateContnet() {
		return updateContnet;
	}

	public void setUpdateContnet(String updateContnet) {
		this.updateContnet = updateContnet;
	}

	// private Long id;
	//
	// private String name;
	//
	// private Integer updateType;
	//
	// private String channelName;
	//
	// private String channelType;
	//
	// private String updateTitle;
	//
	// private String updateContent;
	//
	// private String downUrl;
	//
	// private Date appUpdateTime;
	//
	// private Integer status;
	//
	// private Long createId;
	//
	// private Long updateId;
	//
	// private Date updateTime;
	//
	// private Long deleteId;
	//
	// private Date deleteTime;
	//
	// private static final long serialVersionUID = 1L;
	//
	// public Long getId() {
	// return id;
	// }
	//
	// public void setId(Long id) {
	// this.id = id;
	// }
	//
	// public String getName() {
	// return name;
	// }
	//
	// public void setName(String name) {
	// this.name = name == null ? null : name.trim();
	// }
	//
	// public Integer getUpdateType() {
	// return updateType;
	// }
	//
	// public void setUpdateType(Integer updateType) {
	// this.updateType = updateType;
	// }
	//
	// public String getChannelName() {
	// return channelName;
	// }
	//
	// public void setChannelName(String channelName) {
	// this.channelName = channelName == null ? null : channelName.trim();
	// }
	//
	// public String getChannelType() {
	// return channelType;
	// }
	//
	// public void setChannelType(String channelType) {
	// this.channelType = channelType == null ? null : channelType.trim();
	// }
	//
	// public String getUpdateTitle() {
	// return updateTitle;
	// }
	//
	// public void setUpdateTitle(String updateTitle) {
	// this.updateTitle = updateTitle == null ? null : updateTitle.trim();
	// }
	//
	// public String getUpdateContent() {
	// return updateContent;
	// }
	//
	// public void setUpdateContent(String updateContent) {
	// this.updateContent = updateContent == null ? null : updateContent.trim();
	// }
	//
	// public String getDownUrl() {
	// return downUrl;
	// }
	//
	// public void setDownUrl(String downUrl) {
	// this.downUrl = downUrl == null ? null : downUrl.trim();
	// }
	//
	// public Date getAppUpdateTime() {
	// return appUpdateTime;
	// }
	//
	// public void setAppUpdateTime(Date appUpdateTime) {
	// this.appUpdateTime = appUpdateTime;
	// }
	//
	// public Integer getStatus() {
	// return status;
	// }
	//
	// public void setStatus(Integer status) {
	// this.status = status;
	// }
	//
	// public Long getCreateId() {
	// return createId;
	// }
	//
	// public void setCreateId(Long createId) {
	// this.createId = createId;
	// }
	//
	// public Long getUpdateId() {
	// return updateId;
	// }
	//
	// public void setUpdateId(Long updateId) {
	// this.updateId = updateId;
	// }
	//
	// public Date getUpdateTime() {
	// return updateTime;
	// }
	//
	// public void setUpdateTime(Date updateTime) {
	// this.updateTime = updateTime;
	// }
	//
	// public Long getDeleteId() {
	// return deleteId;
	// }
	//
	// public void setDeleteId(Long deleteId) {
	// this.deleteId = deleteId;
	// }
	//
	// public Date getDeleteTime() {
	// return deleteTime;
	// }
	//
	// public void setDeleteTime(Date deleteTime) {
	// this.deleteTime = deleteTime;
	// }
	//
	// @Override
	// public String toString() {
	// StringBuilder sb = new StringBuilder();
	// sb.append(getClass().getSimpleName());
	// sb.append(" [");
	// sb.append("Hash = ").append(hashCode());
	// sb.append(", id=").append(id);
	// sb.append(", type=").append(type);
	// sb.append(", name=").append(name);
	// sb.append(", versionName=").append(versionName);
	// sb.append(", versionCode=").append(versionCode);
	// sb.append(", updateType=").append(updateType);
	// sb.append(", channelName=").append(channelName);
	// sb.append(", channelType=").append(channelType);
	// sb.append(", updateTitle=").append(updateTitle);
	// sb.append(", updateContent=").append(updateContent);
	// sb.append(", downUrl=").append(downUrl);
	// sb.append(", appUpdateTime=").append(appUpdateTime);
	// sb.append(", status=").append(status);
	// sb.append(", createId=").append(createId);
	// sb.append(", createTime=").append(createTime);
	// sb.append(", updateId=").append(updateId);
	// sb.append(", updateTime=").append(updateTime);
	// sb.append(", deleteId=").append(deleteId);
	// sb.append(", deleteTime=").append(deleteTime);
	// sb.append(", serialVersionUID=").append(serialVersionUID);
	// sb.append("]");
	// return sb.toString();
	// }

}
