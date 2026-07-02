package com.iamcenter.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.iamcenter.domain.security.SysRole;

public interface SysRoleRepository extends JpaRepository<SysRole, String> {
//	public SysRole findByCode(String roleCode);

	public SysRole findByAppIdAndCode(String appId, String roleCode);

}
