package com.iamcenter.repository;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.iamcenter.business.TokenBusiness;
import com.javapai.framework.action.PageResult;
import com.javapai.framework.common.service.AbstractBizService;
import com.saasapi.contract.party.dto.CompanyListDTO;
import com.saasapi.contract.party.dto.DepartmentListDTO;
import com.saasapi.contract.party.dto.PersonListDTO;
import com.saasapi.contract.party.vo.CompanyVO;
import com.saasapi.contract.party.vo.DepartmentVO;
import com.saasapi.contract.party.vo.PersonVO;
import com.saasapi.contract.security.dto.LoginListDTO;
import com.saasapi.contract.security.dto.ResourceListDTO;
import com.saasapi.contract.security.dto.RoleListDTO;
import com.saasapi.contract.security.vo.LoginListVO;
import com.saasapi.contract.security.vo.ResourceVO;
import com.saasapi.contract.security.vo.RoleVO;

@Component
public class PageQueryRepository extends AbstractBizService {
	@Autowired
	TokenBusiness tokenBusiness;

	/**
	 * 登录信息。
	 * 
	 * @param dto
	 * @return
	 */
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
		sb.append(" order by create_time desc");
		return getPage(sb.toString(), params, dto.getPageIndex(), dto.getPageSize(), LoginListVO.class);
	}

	/**
	 * 角色信息。
	 * 
	 * @param dto
	 * @return
	 */
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


	/**
	 * 资源信息。
	 * 
	 * @param dto
	 * @return
	 */
	public PageResult<ResourceVO> listResource(ResourceListDTO dto) {
		List<Object> params = new ArrayList<Object>();
		params.add(dto.getAppId());

		StringBuffer sb = new StringBuffer();
		sb.append("select *,id as res_id from sys_resource where app_id=?");
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

	/**
	 * 人员信息。
	 * 
	 * @param dto
	 * @return
	 */
	public PageResult<PersonVO> listPerson(PersonListDTO dto) {
		List<Object> params = new ArrayList<Object>();
		params.add(tokenBusiness.getTokenAssocCompanyId());
		
		StringBuffer sb = new StringBuffer();
		sb.append("select a.id as party_id,a.code as party_code,a.last_name as realname,sex,sfz as id_card,a.mobile_phone,status_id,c.party_id_from as dept_id,d.group_name as dept_name ");
		sb.append(" from hy102 a ");
		sb.append(" left join hy100 b on a.id=b.id");
		sb.append(" left join party_relation c on a.id=c.party_id_to and c.role_id_from='ROLE_DEPT'");
		sb.append(" left join hy101 d on d.id=c.party_id_from");
		sb.append(" where a.company_id=?");
//		if (StringUtils.isNotBlank(dto.getAppId())) {
//			sb.append(" and b.app_id=?");
//			params.add(dto.getAppId());
//		}
		if (StringUtils.isNotBlank(dto.getName())) {
			sb.append(" and a.name like ?");
			params.add("%" + dto.getName() + "%");
		}
		return getPage(sb.toString(), params, dto.getPageIndex(), dto.getPageSize(), PersonVO.class);
	}

	/**
	 * 部门信息。
	 * 
	 * @param dto
	 * @return
	 */
	public PageResult<DepartmentVO> listDepartment(DepartmentListDTO dto) {
		List<Object> params = new ArrayList<Object>();
		StringBuffer sb = new StringBuffer();
		sb.append("select a.id as dept_id,a.group_code as dept_code,a.group_name as dept_name,c.status_id,c.create_time from hy101 a");
		sb.append(" left join party_relation b on a.id=b.party_id_to left join hy100 c on a.id=c.id");
		sb.append(" where c.role_type_id='ROLE_DETP'");
		if (StringUtils.isNotBlank(dto.getCompanyId())) {
			sb.append(" and b.party_id_from=?");
			params.add(dto.getCompanyId());
		}
		return getPage(sb.toString(), params, dto.getPageIndex(), dto.getPageSize(), DepartmentVO.class);
	}
	
	/**
	 * 公司信息。
	 * 
	 * @param dto
	 * @return
	 */
	public PageResult<CompanyVO> listCompany(CompanyListDTO dto) {
		List<Object> params = new ArrayList<Object>();
		StringBuffer sb = new StringBuffer();
		sb.append("select a.id as company_id, a.group_code as company_code, a.group_name as company_name,create_time ");
		sb.append(" from hy101 a left join hy100 b on a.id=b.id where role_type_id='ROLE_COMPANY'");
//		if (StringUtils.isNotBlank(dto.getAppId())) {
//			sb.append(" and app_id=?");
//			params.add(dto.getAppId());
//		}
		return getPage(sb.toString(), params, dto.getPageIndex(), dto.getPageSize(), CompanyVO.class);
	}

}
