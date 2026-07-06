package com.iamcenter.repository.party;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.iamcenter.domain.party.PartyPerson;

@Repository
public interface PartyPersonRepository extends JpaRepository<PartyPerson, String> {

	PartyPerson findByCode(String code);

	PartyPerson findByIdCard(String idCard);
}
