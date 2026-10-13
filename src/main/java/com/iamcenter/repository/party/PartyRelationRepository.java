package com.iamcenter.repository.party;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
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

	void deleteByIdPartyIdFrom(String partyIdFrom);

	void deleteByIdPartyIdTo(String partyIdTo);
	
	Long deleteByIdPartyIdToAndRelationTypeId(String partyIdTo, String relationTypeId);

	/**
	 * 查询部门是否存指定人员。
	 * 
	 * @param deptId  部门标识
	 * @param partyId 人员标识
	 * @return
	 */
	@Query(value = "select * from party_relation where party_id_from = :deptId and party_id_to = :partyId and relation_type_id='PARENT_CHILD'", nativeQuery = true)
	Long checkPartyRelationWithParentChild(String deptId, String partyId);
}
