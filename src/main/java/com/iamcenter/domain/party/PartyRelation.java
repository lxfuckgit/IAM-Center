package com.iamcenter.domain.party;

import com.javapai.framework.common.domain.TopBaseDomain;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "party_relation")
public class PartyRelation extends TopBaseDomain {
//	private String partyIdFrom;
//	private String partyIdTo;
	/**
	 * 关系标识
	 */
	@EmbeddedId
	private PartyRelationId id;

	/**
	 * 关系类型
	 */
	@Column(name = "relation_type_id", length = 12)
	private String relationTypeId;

	public PartyRelationId getId() {
		return id;
	}

	public void setId(PartyRelationId id) {
		this.id = id;
	}

	public String getRelationTypeId() {
		return relationTypeId;
	}

	public void setRelationTypeId(String relationTypeId) {
		this.relationTypeId = relationTypeId;
	}

}
