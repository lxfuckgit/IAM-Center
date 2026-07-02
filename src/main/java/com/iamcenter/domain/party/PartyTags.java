package com.iamcenter.domain.party;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "HY900")
public class PartyTags {
	// private String id;
	@Id
	@Column(name = "party_id", length = 32)
	private String partyId;
	/**
	 * 标签分类.
	 */
	@Column(name = "tag_type", length = 16)
	private String tagType;
	/**
	 * 标签名称.
	 */
	@Column(name = "tags", length = 50)
	private String tags;
	/**
	 * 权重.
	 */
	@Column(name = "weight", length = 5)
	private int weight;

	public String getPartyId() {
		return partyId;
	}

	public void setPartyId(String partyId) {
		this.partyId = partyId;
	}

	public String getTagType() {
		return tagType;
	}

	public void setTagType(String tagType) {
		this.tagType = tagType;
	}

	public String getTags() {
		return tags;
	}

	public void setTags(String tags) {
		this.tags = tags;
	}

	public int getWeight() {
		return weight;
	}

	public void setWeight(int weight) {
		this.weight = weight;
	}

}
