package com.iamcenter.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.iamcenter.domain.security.SysRole;

public interface SysRoleRepository extends JpaRepository<SysRole, Long> {
//	public SysRole findByCode(String roleCode);

	public SysRole findByAppIdAndCode(String appId, String roleCode);
	
	@Query(value = "select resource_id from sys_role_resource where role_id=?1", nativeQuery = true)
	public List<Long> listRoleResourceIds(Long roleId);

	@Query(value = "select resource_id from sys_role_resource a left join sys_resource b on a.id=b.id where a.role_id=?2 and b.app_id=?1", nativeQuery = true)
	public List<Long> listRoleResourceIds(String appId, Long roleId);
}
