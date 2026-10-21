package com.iamcenter.business;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.iamcenter.domain.party.PartyGroup;
import com.iamcenter.domain.party.PartyPerson;
import com.iamcenter.domain.party.PartyRelation;
import com.iamcenter.domain.party.PartyRelationId;
import com.iamcenter.repository.party.PartyGroupRepository;
import com.iamcenter.repository.party.PartyPersonRepository;
import com.iamcenter.repository.party.PartyRelationDao;
import com.iamcenter.repository.party.PartyRelationRepository;
import com.javapai.framework.common.service.AbstractBizService;
import com.saasapi.contract.party.enums.PartyRelationType;
import com.saasapi.contract.party.vo.DepartmentVO;

@Component
public class PartyBusiness extends AbstractBizService {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private PartyPersonRepository partyPersonDao;
	
	@Autowired
	PartyRelationRepository partyRelationRepository;
	
	@Autowired
	PartyPersonRepository partyPersonRepository;
	
	@Autowired
	PartyGroupRepository partyGroupRepository;
	
	@Autowired
	PartyRelationDao partyRelationDao;
	
	public void createRelation(String partyIdFrom, String roleIdFrom, String partyIdTo, String roleIdTo, String relationTypeId) {
		PartyRelation relation = new PartyRelation();
		relation.setId(new PartyRelationId(partyIdFrom, roleIdFrom, partyIdTo, roleIdTo));
		relation.setRelationTypeId(relationTypeId);
		partyRelationRepository.save(relation);
		logger.info("--->关系创建完成: {} -> {} [{}]", partyIdFrom, partyIdTo, relationTypeId);
	}
	
	/**
	 * 查询公司是否存指定部门编号。
	 * 
	 * @param companyId 公司标识
	 * @param deptCode  部门编号
	 * @return
	 */
	public boolean checkCompanyDeptCode(String companyId, String deptCode) {
//		List<PartyRelation> list = partyRelationDao.findByIdPartyIdFromAndRelationTypeId(companyId, PartyRelationType.PARENT_CHILD.getKey());
//		if (null != list && !list.isEmpty()) {
//			
//		} else {
//			return false;
//		}
		//重点优化：借助party_relation(party_id_from, relation_type_id, party_id_to)索引，避免无效回表和中间结果。
		String sql = "select count(1) from party_relation a where a.party_id_from=? and a.relation_type_id='PARENT_CHILD' and exists(select 1 from hy101 b where b.id=a.party_id_to and b.group_code=?)";
		return jdbcTemplate.queryForObject(sql, Boolean.class, companyId, deptCode);
	}
	
	/**
	 * 查询公司是否存指定部门名称。
	 * 
	 * @param companyId 公司标识
	 * @param deptName  部门名称
	 * @return
	 */
	public boolean checkCompanyDeptName(String companyId, String deptName) {
		String sql = "select count(1) from party_relation a where a.party_id_from=? and a.relation_type_id='PARENT_CHILD' and exists(select 1 from hy101 b where b.id=a.party_id_to and b.group_name=?)";
		return jdbcTemplate.queryForObject(sql, Boolean.class, companyId, deptName);
	}
	
	/**
	 * 查询公司是否存指定人员编号。
	 * 
	 * @param companyId 公司标识
	 * @param deptCode  部门编号
	 * @return
	 */
	public boolean checkCompanyPartyCode(String companyId, String partyCode) {
		//重点优化：借助party_relation(party_id_from, relation_type_id, party_id_to)索引，避免无效回表和中间结果。
		String sql = "select count(1) from party_relation a where a.party_id_from=? and a.relation_type_id='EMPLOYMENT' and exists(select 1 from hy102 b where b.id=a.party_id_to and b.code=?)";
		return jdbcTemplate.queryForObject(sql, Boolean.class, companyId, partyCode);
	}

	/**
	 * 查询部门的子部门信息。
	 * 
	 * @param deptId 部门标识
	 * @return
	 */
	public List<DepartmentVO> listChildDept(String deptId) {
		StringBuffer sb = new StringBuffer();
		sb.append("select a.id as dept_id,a.group_code as dept_code,a.group_name as dept_name,b.sequence as dept_seq,b.party_id_from as parent_id,c.status_id,c.create_time ");
		sb.append(" from hy101 a");
		sb.append(" left join party_relation b on a.id=b.party_id_to");
		sb.append(" left join hy100 c on a.id=c.id");
		sb.append(" where c.role_type_id='ROLE_DETP' and b.party_id_from=? ");
		sb.append(" order by b.sequence asc,c.create_time desc");
		return jdbcTemplate.query(sb.toString(), new BeanPropertyRowMapper<DepartmentVO>(DepartmentVO.class), deptId);
	}
	
	/**
	 * 查询部门的负责人。
	 * 
	 * @param deptId 部门标识
	 */
	public PartyPerson getDeptPartyOwner(String deptId) {
		String relationTypeId = PartyRelationType.PARTY_OWNER.getKey();
		List<PartyRelation> list = partyRelationRepository.findByIdPartyIdToAndRelationTypeId(deptId, relationTypeId);
		if (null == list || list.size() == 0) {
			return null;
		}
		Optional<PartyPerson> optional = partyPersonRepository.findById(list.get(0).getId().getPartyIdFrom());
		if (optional.isEmpty()) {
			return null;
		} else {
			return optional.get();
		}
	}
	
	/**
	 * 读取用户的部门信息。
	 * 
	 * @param partyId 用户标识
	 */
	public PartyGroup getPersonDepartyment(String partyId) {
		String relationTypeId = PartyRelationType.GROUP_MEMBER.getKey();
		List<PartyRelation> list = partyRelationRepository.findByIdPartyIdToAndRelationTypeId(partyId, relationTypeId);
		if (null == list || list.size() == 0) {
			return null;
		}
		Optional<PartyGroup> optional = partyGroupRepository.findById(list.get(0).getId().getPartyIdFrom());
		if (optional.isEmpty()) {
			return null;
		} else {
			return optional.get();
		}
	}
	
	/**
	 * 更新指定【人员】的【部门】信息。<br>
	 * <strong>提示：此方法只适用于人员单部门模式（操作过程：清除此用户关联的所有部门，再设置指定的唯一部门）。<br>
	 * 
	 * @param partyId 人员标识
	 * @param deptId  部门标识
	 */
	@Transactional
	public void updatePartyToDept(String partyId, String deptId) {
		/* 1、清除老部门 */
		String relationTypeId = PartyRelationType.GROUP_MEMBER.getKey();
		List<PartyRelation> list = partyRelationRepository.findByIdPartyIdToAndRelationTypeId(partyId, relationTypeId);
		if (null != list && list.size() > 0) {
			partyRelationDao.deleteDeptEmployment(list.get(0).getId().getPartyIdFrom(), partyId);
		}
		/* 2、添加新部门 */
		partyRelationDao.createDeptEmployment(deptId, partyId);
	}
	
	/**
	 * 将指定【人员】加入指定到【部门】。<br>
	 * <strong>提示：此方法支持[人员]多[部门]的组织形式。<br>
	 * 
	 * @param partyId 人员标识
	 * @param deptId  部门标识
	 */
//	@Transactional
//	public boolean appendPartyToDept(String partyId, String deptId) {
//		if (StringUtils.isBlank(deptId) || StringUtils.isBlank(partyId)) {
//			logger.info("--->提示：部门[{}]或人员[{}]信息为空！", deptId, partyId);
//			return false;
//		}
//		Long checkValue = partyRelationRepository.checkPartyRelationWithParentChild(deptId, partyId);
//		if (null != checkValue && checkValue > 0) {
//			logger.info("--->提示：部门[{}]的人员[{}]信息已存在！", deptId, partyId);
//			return true;
//		}
//		String roleIdFrom = RoleTypeEnum.ROLE_DETP.getKey();
//		String roleIdTo = RoleTypeEnum.ROLE_EMPLOYEE.getKey();
//		createRelation(deptId, roleIdFrom, partyId, roleIdTo, PartyRelationType.PARENT_CHILD.getKey());
//		return true;
//	}
	
	/**
	 * 删除人员的所有信息。
	 * 
	 * @param loginId
	 * @return
	 */
	public boolean deleteAllByPartyId(String partyId) {
		partyPersonDao.deleteById(partyId);
		partyRelationRepository.deleteByIdPartyIdTo(partyId);
		partyRelationRepository.deleteByIdPartyIdFrom(partyId);
		logger.info("--->提示：人员[{}]信息删除完毕！", partyId);
		return true;
	}

}
