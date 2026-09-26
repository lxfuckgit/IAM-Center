package com.iamcenter.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.iamcenter.domain.security.SysLoginRole;

public interface SysLoginRoleDao extends JpaRepository<SysLoginRole, String> {
	public List<SysLoginRole> findByLoginId(Long loginId);
	
	public List<SysLoginRole> findByRoleId(Long roleId);
	
	@Query(value = "select b.id,b.role_code,b.role_name,b.role_desc from sys_login_role a left join sys_role b on a.role_id=b.id where login_id=?1", nativeQuery = true)
	public List<Object[]> listLoginRole(Long loginId);
	
	@Query(value = "select b.role_code from sys_login_role a left join sys_role b on a.role_id=b.id where a.login_id=?1", nativeQuery = true)
	public List<String> listRoleCodeByLoginId(Long loginId);

//	@Query(value = "delete from sys_login_role where login_id=?1 and role_id=?2", nativeQuery = true)
	public int deleteByLoginIdAndRoleId(Long loginId, Long roleId);
	
	/**
	 * 检查【登录标识】是否具有指定的【超管角色】
	 * 
	 * @param loginId
	 * @return
	 */
	@Query(value = "select count(1) from sys_login_role a left join sys_role b on a.role_id=b.id where a.login_id=? and b.role_code='super_admin'", nativeQuery = true)
	Long countLoginRole(Long loginId);
	
}
