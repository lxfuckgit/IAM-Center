package com.iamcenter.repository.party;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.iamcenter.domain.party.PartyGroup;

@Repository
public interface PartyGroupRepository extends JpaRepository<PartyGroup, String> {

	PartyGroup findByGroupCode(String groupCode);

	PartyGroup findByAppIdAndGroupName(String appId, String groupName);
}
