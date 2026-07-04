package com.iamcenter.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.iamcenter.domain.security.SysLogin;

@Repository
public interface SysLoginRepository extends JpaRepository<SysLogin, Long> {
	public SysLogin findByLoginName(String loginName);

	public SysLogin findByLoginToken(String loginToken);

	public SysLogin findByAppIdAndLoginName(String appId, String loginName);

	public SysLogin findByAppIdAndExtLoginId(String appId, String extLoginId);
	
	public int deleteByLoginToken(String loginToken);

	/**
	 * 检查是否存的当前登录账号
	 * 
	 * @param appId
	 * @param loginName 登录账号
	 * @return
	 */
	@Query("SELECT EXISTS (SELECT 1 FROM SysLogin e WHERE e.appId = :appId AND e.loginName = :loginName)")
	public boolean existsByAppIdAndLoginName(@Param("appId") String appId, @Param("loginName") String loginName);
//	public boolean checkloginName(String productLine, String appId, String loginName) {
//		String sql = "select count(loginId) as kk from sys_login where appId=? and loginName=?";
//		return jdbcTemplate.queryForObject(sql, boolean.class, new Object[] { appId, loginName });
//	}
	
	
//	/**
//	 * 读取用户名关联登录标识。<br>
//	 * 
//	 * @param appId
//	 *            应用标识.<br>
//	 * @param loginName
//	 *            登录名(例如:手机号). <br>
//	 * @return
//	 */
//	public java.util.List<Long> listSysLoginIds(String appId, String loginName) {
//		String sql = "select loginId from sys_login where appId=? and loginName=?";
//		return jdbcTemplate.queryForList(sql, Long.class, appId, loginName);
//	}

}
