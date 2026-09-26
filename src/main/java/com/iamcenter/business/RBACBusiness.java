package com.iamcenter.business;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.iamcenter.repository.SysLoginRoleDao;
import com.javapai.framework.common.service.AbstractBizService;
import com.saasapi.contract.security.vo.PrivilegeVO;

@Component
public class RBACBusiness extends AbstractBizService {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private SysLoginRoleDao sysLoginRoleDao;
	
	@Transactional
	public boolean restoreLoginRoles(String appId, Long loginId, List<Long> roleIdList) {
		// 撤销原角色
		int deleteSize = jdbcTemplate.update("delete from sys_login_role where login_id=?", loginId);
		logger.info("--->[{}]账户角色撤销：{}", loginId, deleteSize);

		// 授予新角色
		List<Object[]> batchArgs = new ArrayList<>();
		for (Long roleId : roleIdList) {
			batchArgs.add(new Object[] { loginId, roleId });
		}
		String insertSQL = "insert into sys_login_role (login_id, role_id) values (?, ?)";
		int[] updateSize = jdbcTemplate.batchUpdate(insertSQL, batchArgs);
		logger.info("--->[{}]账户角色授予：{}", loginId, updateSize);

		return true;
	}
	
	@Transactional
	public boolean restoreRoleResourceList(String appId, Long roleId, List<Long> resIdList) {
		// 撤销原权限
		int deleteSize = jdbcTemplate.update("delete from sys_role_resource where role_id=?", roleId);
		logger.info("--->[{}]角色权限撤销：{}", roleId, deleteSize);

		// 授予新权限
		List<Object[]> batchArgs = new ArrayList<>();
		for (Long resId : resIdList) {
			batchArgs.add(new Object[] { roleId, resId });
		}
		String insertSQL = "insert into sys_role_resource (role_id, resource_id) values (?, ?)";
		int[] updateSize = jdbcTemplate.batchUpdate(insertSQL, batchArgs);
		logger.info("--->[{}]角色权限授予：{}", roleId, updateSize);
		
		return true;
	}
	
	public List<PrivilegeVO> listRoleResource(Long roleId) {
		String sql = "select b.code,b.name from sys_role_resource a left join sys_resource b on a.id=b.id where a.role_id=?";
		return jdbcTemplate.query(sql, new BeanPropertyRowMapper<PrivilegeVO>(PrivilegeVO.class), new Object[] { roleId });
	}
	
	public List<PrivilegeVO> listRoleResource(String appId, Long roleId) {
		String sql = "select b.code,b.name from sys_role_resource a left join sys_resource b on a.id=b.id where a.role_id=? and b.app_id=?";
		return jdbcTemplate.query(sql, new BeanPropertyRowMapper<PrivilegeVO>(PrivilegeVO.class), new Object[] { roleId, appId });
	}
	
	@Cacheable(value = "listRoleCodeByLoginId", key = "#loginId")
	public List<String> listRoleCodeByLoginId(Long loginId) {
		// 优先读缓存再读本地库
		return sysLoginRoleDao.listRoleCodeByLoginId(loginId);
	}

}
