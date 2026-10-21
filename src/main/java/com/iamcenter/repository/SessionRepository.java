package com.iamcenter.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.iamcenter.domain.security.SysSession;

public interface SessionRepository extends JpaRepository<SysSession, String> {
	
}