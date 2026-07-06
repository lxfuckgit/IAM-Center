package com.iamcenter.repository.party;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.iamcenter.domain.party.Party;

@Repository
public interface PartyRepository extends JpaRepository<Party, String> {

}
