package com.iamcenter.repository.party;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.iamcenter.domain.party.PartyRelation;
import com.iamcenter.domain.party.PartyRelationId;

@Repository
public interface PartyRelationRepository extends JpaRepository<PartyRelation, PartyRelationId> {

	List<PartyRelation> findByIdPartyIdFrom(String partyIdFrom);

	List<PartyRelation> findByIdPartyIdTo(String partyIdTo);

	List<PartyRelation> findByRelationTypeId(String relationTypeId);

	List<PartyRelation> findByIdPartyIdFromAndRelationTypeId(String partyIdFrom, String relationTypeId);

	List<PartyRelation> findByIdPartyIdToAndRelationTypeId(String partyIdTo, String relationTypeId);
}
