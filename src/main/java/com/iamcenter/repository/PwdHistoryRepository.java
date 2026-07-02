package com.iamcenter.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.iamcenter.domain.security.SysPwdHistory;

public interface PwdHistoryRepository extends JpaRepository<SysPwdHistory, Long> {

}
