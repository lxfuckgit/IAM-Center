package com.iamcenter.domain.party;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class PartyRelationId {
	@Column(name = "party_id_from", length = 32)
	private String partyIdFrom;

	@Column(name = "party_id_to", length = 32)
	private String partyIdTo;

	@Column(name = "role_id_from", length = 20)
	private String roleIdFrom;

	@Column(name = "role_id_to", length = 20)
	private String roleIdTo;

	public PartyRelationId() {
	}

	public PartyRelationId(String partyIdFrom, String roleIdFrom, String partyIdTo, String roleIdTo) {
		this.partyIdFrom = partyIdFrom;
		this.roleIdFrom = roleIdFrom;
		this.partyIdTo = partyIdTo;
		this.roleIdTo = roleIdTo;
	}

	public String getPartyIdFrom() {
		return partyIdFrom;
	}

	public void setPartyIdFrom(String partyIdFrom) {
		this.partyIdFrom = partyIdFrom;
	}

	public String getPartyIdTo() {
		return partyIdTo;
	}

	public void setPartyIdTo(String partyIdTo) {
		this.partyIdTo = partyIdTo;
	}

	public String getRoleIdFrom() {
		return roleIdFrom;
	}

	public void setRoleIdFrom(String roleIdFrom) {
		this.roleIdFrom = roleIdFrom;
	}

	public String getRoleIdTo() {
		return roleIdTo;
	}

	public void setRoleIdTo(String roleIdTo) {
		this.roleIdTo = roleIdTo;
	}

}