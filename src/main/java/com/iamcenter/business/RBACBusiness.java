package com.iamcenter.business;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import com.javapai.framework.action.PageResult;
import com.javapai.framework.common.service.AbstractBizService;
import com.saasapi.contract.security.dto.RoleListDTO;
import com.saasapi.contract.security.vo.RoleVO;

@Component
public class RBACBusiness extends AbstractBizService {
	
	public PageResult<RoleVO> listRole(RoleListDTO dto) {
		List<Object> params = new ArrayList<Object>();
		StringBuffer sb = new StringBuffer();
		sb.append("select * from sys_role where 1=1");
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
}
