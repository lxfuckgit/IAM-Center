package com.iamcenter.business;

import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.iamcenter.domain.party.PartyGroup;
import com.iamcenter.domain.party.PartyPerson;
import com.iamcenter.domain.party.PartyRelation;
import com.iamcenter.domain.party.PartyRelationId;
import com.iamcenter.repository.party.PartyGroupRepository;
import com.iamcenter.repository.party.PartyPersonRepository;
import com.iamcenter.repository.party.PartyRelationRepository;
import com.javapai.framework.common.service.AbstractBizService;
import com.saasapi.contract.party.enums.PartyRelationType;
import com.saasapi.contract.party.enums.RoleTypeEnum;

@Component
public class PartyBusiness extends AbstractBizService {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	PartyRelationRepository partyRelationDao;
	
	@Autowired
	PartyPersonRepository partyPersonRepository;
	
	@Autowired
	PartyGroupRepository partyGroupRepository;
	
	public void createRelation(String partyIdFrom, String roleIdFrom, String partyIdTo, String roleIdTo, String relationTypeId) {
		PartyRelation relation = new PartyRelation();
		relation.setId(new PartyRelationId(partyIdFrom, roleIdFrom, partyIdTo, roleIdTo));
		relation.setRelationTypeId(relationTypeId);
		partyRelationDao.save(relation);
		logger.info("--->关系创建完成: {} -> {} [{}]", partyIdFrom, partyIdTo, relationTypeId);
	}
	
	/**
	 * 查询部门的负责人。
	 * 
	 * @param deptId 部门标识
	 */
	public PartyPerson getDeptPartyOwner(String deptId) {
		String relationTypeId = PartyRelationType.PARTY_OWNER.getKey();
		List<PartyRelation> list = partyRelationDao.findByIdPartyIdToAndRelationTypeId(deptId, relationTypeId);
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
	 * 创建部门的负责人。
	 * 
	 * @param deptId  部门标识
	 * @param partyId 负责人标识
	 */
	@Transactional
	public void createDeptPartyOwner(String deptId, String partyId) {
		String roleIdFrom = RoleTypeEnum.ROLE_EMPLOYEE.getKey();
		String roleIdTo = RoleTypeEnum.ROLE_DETP.getKey();
		String relationType = PartyRelationType.PARTY_OWNER.getKey();
		createRelation(partyId, roleIdFrom, deptId, roleIdTo, relationType);
	}
	
	/**
	 * 删除部门的负责人。
	 * 
	 * @param deptId     部门标识
	 * @param newPartyId 新负责人标识
	 * @param oldPartyId 老负责人标识
	 */
	@Transactional
	public void deleteDeptPartyOwner(String deptId, String newPartyId, String oldPartyId) {
		String roleIdFrom = RoleTypeEnum.ROLE_EMPLOYEE.getKey();
		String roleIdTo = RoleTypeEnum.ROLE_DETP.getKey();
		partyRelationDao.deleteById(new PartyRelationId(oldPartyId, roleIdFrom, deptId, roleIdTo));
		logger.info("--->关系移除完成: {} -> {} [{}]", oldPartyId, deptId, PartyRelationType.PARTY_OWNER.getKey());
	}
	
	/**
	 * 更新部门的负责人。
	 * 
	 * @param deptId     部门标识
	 * @param newPartyId 新负责人标识
	 * @param oldPartyId 老负责人标识
	 */
	@Transactional
	public void updateDeptPartyOwner(String deptId, String newPartyId, String oldPartyId) {
		deleteDeptPartyOwner(deptId, newPartyId, oldPartyId);
		String roleIdFrom = RoleTypeEnum.ROLE_EMPLOYEE.getKey();
		String roleIdTo = RoleTypeEnum.ROLE_DETP.getKey();
		createRelation(newPartyId, roleIdFrom, deptId, roleIdTo, PartyRelationType.PARTY_OWNER.getKey());
	}
	
	/**
	 * 读取用户的部门信息。
	 * 
	 * @param partyId 用户标识
	 */
	public PartyGroup getPersonDepartyment(String partyId) {
		String relationTypeId = PartyRelationType.PARENT_CHILD.getKey();
		List<PartyRelation> list = partyRelationDao.findByIdPartyIdToAndRelationTypeId(partyId, relationTypeId);
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
		/* 1、查询现有人员的部门列表 */
		String relationTypeId = PartyRelationType.PARENT_CHILD.getKey();
		List<PartyRelation> list = partyRelationDao.findByIdPartyIdToAndRelationTypeId(partyId, relationTypeId);
		if (null != list && list.size() > 0) {
			// 清除老部门
			Long dSize = partyRelationDao.deleteByIdPartyIdToAndRelationTypeId(partyId, relationTypeId);
			logger.info("--->已删除人员[{}]关联的部门记录[{}]！", partyId, dSize);
		}
		/* 2、将人员关联到新部门 */
		String roleIdTo = RoleTypeEnum.ROLE_EMPLOYEE.getKey();
		String roleIdFrom = RoleTypeEnum.ROLE_DETP.getKey();
		createRelation(deptId, roleIdFrom, partyId, roleIdTo, PartyRelationType.PARENT_CHILD.getKey());
	}
	
	/**
	 * 将指定【人员】加入指定到【部门】。<br>
	 * <strong>提示：此方法支持[人员]多[部门]的组织形式。<br>
	 * 
	 * @param partyId 人员标识
	 * @param deptId  部门标识
	 */
	@Transactional
	public boolean appendPartyToDept(String partyId, String deptId) {
		if (StringUtils.isBlank(deptId) || StringUtils.isBlank(partyId)) {
			logger.info("--->提示：部门[{}]或人员[{}]信息为空！", deptId, partyId);
			return false;
		}
		Long checkValue = partyRelationDao.checkPartyRelationWithParentChild(deptId, partyId);
		if (null != checkValue && checkValue > 0) {
			logger.info("--->提示：部门[{}]的人员[{}]信息已存在！", deptId, partyId);
			return true;
		}
		String roleIdFrom = RoleTypeEnum.ROLE_DETP.getKey();
		String roleIdTo = RoleTypeEnum.ROLE_EMPLOYEE.getKey();
		createRelation(deptId, roleIdFrom, partyId, roleIdTo, PartyRelationType.PARENT_CHILD.getKey());
		return true;
	}

}
