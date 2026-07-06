package com.iamcenter.business;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.iamcenter.domain.party.PartyRelation;
import com.iamcenter.domain.party.PartyRelationId;
import com.iamcenter.repository.party.PartyRelationRepository;
import com.javapai.framework.action.PageResult;
import com.javapai.framework.common.service.AbstractBizService;
import com.saasapi.contract.party.dto.CompanyListDTO;
import com.saasapi.contract.party.dto.DepartmentListDTO;
import com.saasapi.contract.party.dto.PersonListDTO;
import com.saasapi.contract.party.vo.CompanyVO;
import com.saasapi.contract.party.vo.DepartmentVO;
import com.saasapi.contract.party.vo.PersonVO;

@Component
public class PartyBusiness extends AbstractBizService {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	PartyRelationRepository partyRelationRepository;
	
	/**
	 * 
	 * @param dto
	 * @return
	 */
	public PageResult<PersonVO> listPerson(PersonListDTO dto) {
		StringBuffer sb = new StringBuffer();
		sb.append("select * from hy102 where 1=1");
		if (StringUtils.isNotBlank(dto.getAppId())) {

		}
		return getPage(sb.toString(), dto.getPageIndex(), dto.getPageSize(), PersonVO.class);
	}
	
	/**
	 * 
	 * @param dto
	 * @return
	 */
	public PageResult<CompanyVO> listCompany(CompanyListDTO dto) {
		List<Object> params = new ArrayList<Object>();
		StringBuffer sb = new StringBuffer();
		sb.append("select a.id,a.group_name as name ");
		sb.append(" from hy101 a left join hy100 b on a.id=b.id where role_type_id='ROLE_COMPANY'");
		if (StringUtils.isNotBlank(dto.getAppId())) {
			sb.append(" and app_id=?");
			params.add(dto.getAppId());
		}
		return getPage(sb.toString(), params, dto.getPageIndex(), dto.getPageSize(), CompanyVO.class);
	}
	
	/**
	 * 
	 * @param dto
	 * @return
	 */
	public PageResult<DepartmentVO> listDepartment(DepartmentListDTO dto) {
		List<Object> params = new ArrayList<Object>();
		StringBuffer sb = new StringBuffer();
		sb.append("select a.id,a.group_name as name ");
		sb.append(" from hy101 a left join party_relation b on a.id=b.party_id_to left join hy100 c on a.id=c.id");
		sb.append(" where c.role_type_id='ROLE_DETP'");
		if (StringUtils.isNotBlank(dto.getCompanyId())) {
			sb.append(" and b.party_id_from=?");
			params.add(dto.getCompanyId());
		}
		return getPage(sb.toString(), params, dto.getPageIndex(), dto.getPageSize(), DepartmentVO.class);
	}

	public void createRelation(String partyIdFrom, String roleIdFrom, String partyIdTo, String roleIdTo, String relationTypeId) {
		PartyRelation relation = new PartyRelation();
		relation.setId(new PartyRelationId(partyIdFrom, roleIdFrom, partyIdTo, roleIdTo));
		relation.setRelationTypeId(relationTypeId);
		partyRelationRepository.save(relation);
		logger.info("--->关系创建完成: {} -> {} [{}]", partyIdFrom, partyIdTo, relationTypeId);
	}

}
