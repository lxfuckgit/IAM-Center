package com.iamcenter.business;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.javapai.framework.action.PageResult;
import com.javapai.framework.common.service.AbstractBizService;
import com.saasapi.contract.security.dto.LoginListDTO;
import com.saasapi.contract.security.dto.ResourceListDTO;
import com.saasapi.contract.security.dto.RoleListDTO;
import com.saasapi.contract.security.vo.LoginListVO;
import com.saasapi.contract.security.vo.PrivilegeVO;
import com.saasapi.contract.security.vo.ResourceVO;
import com.saasapi.contract.security.vo.RoleVO;

@Component
public class RBACBusiness extends AbstractBizService {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	public PageResult<RoleVO> listRole(RoleListDTO dto) {
		List<Object> params = new ArrayList<Object>();
		StringBuffer sb = new StringBuffer();
		sb.append("select *,id as role_id from sys_role where 1=1");
		if (StringUtils.isNotBlank(dto.getAppId())) {
			sb.append(" and app_id=?");
			params.add(dto.getAppId());
		}
		if (StringUtils.isNotBlank(dto.getRoleCode())) {
			sb.append(" and role_code=?");
			params.add(dto.getRoleCode());
		}
		if (StringUtils.isNotBlank(dto.getRoleName())) {
			sb.append(" and role_name like ?");
			params.add("%" + dto.getRoleName() + "%");
		}
		return getPage(sb.toString(), params, dto.getPageIndex(), dto.getPageSize(), RoleVO.class);
	}
	
	public PageResult<ResourceVO> listResource(ResourceListDTO dto) {
		List<Object> params = new ArrayList<Object>();
		params.add(dto.getAppId());
		
		StringBuffer sb = new StringBuffer();
		sb.append("select *,id as res_id from sys_resource where app_id=?");
//		if (StringUtils.isNotBlank(dto.getAppId())) {
//			sb.append(" and app_id=?");
//			params.add(dto.getAppId());
//		}
		if (StringUtils.isNotBlank(dto.getResCode())) {
			sb.append(" and code=?");
			params.add(dto.getResCode());
		}
		if (StringUtils.isNotBlank(dto.getResName())) {
			sb.append(" and name like ?");
			params.add("%" + dto.getResName() + "%");
		}
		if (StringUtils.isNotBlank(dto.getResType())) {
			sb.append(" and type=?");
			params.add(dto.getResType());
		}
		sb.append(" order by sequence asc");
		return getPage(sb.toString(), params, dto.getPageIndex(), dto.getPageSize(), ResourceVO.class);
	}
	
	public PageResult<LoginListVO> listLogin(LoginListDTO dto) {
		List<Object> params = new ArrayList<Object>();
		params.add(dto.getAppId());

		StringBuffer sb = new StringBuffer();
		sb.append("select * from sys_login where app_id=?");
		if (StringUtils.isNotBlank(dto.getLoginName())) {
			sb.append(" and login_name like ?");
			params.add("%" + dto.getLoginName() + "%");
		}
		if (StringUtils.isNotBlank(dto.getStatusId())) {
			sb.append(" and login_status=?");
			params.add(dto.getStatusId());
		}
//		sb.append(" order by id asc");
		return getPage(sb.toString(), params, dto.getPageIndex(), dto.getPageSize(), LoginListVO.class);
	}
	
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

}
