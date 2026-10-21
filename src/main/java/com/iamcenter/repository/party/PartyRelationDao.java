package com.iamcenter.repository.party;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.iamcenter.domain.party.PartyRelation;
import com.iamcenter.domain.party.PartyRelationId;
import com.javapai.framework.common.service.AbstractBizService;
import com.saasapi.contract.party.enums.PartyRelationType;
import com.saasapi.contract.party.enums.RoleTypeEnum;

@Component
public class PartyRelationDao extends AbstractBizService {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	PartyRelationRepository partyRelationRepository;
	
	/**
	 * 创建【公司】与【部门】的关系。
	 * 
	 * @param companyId 公司标识
	 * @param deptId    部门标识
	 */
//	@Transactional
//	public void createCompanyDepartyment(String companyId, String deptId) {
//		String roleIdFrom = RoleTypeEnum.ROLE_COMPANY.getKey();
//		String roleIdTo = RoleTypeEnum.ROLE_EMPLOYEE.getKey();
//		String relationType = PartyRelationType.EMPLOYMENT.getKey();
//		createRelation(companyId, roleIdFrom, partyId, roleIdTo, relationType);
//		logger.info("--->公司[{}]和部门[{}]关系创建完成 ...", companyId, partyId);
//	}
	
	/**
	 * 创建【公司】与【员工】的关系。
	 * 
	 * @param companyId 公司标识
	 * @param partyId   人员标识
	 */
	@Transactional
	public void createCompanyEmployment(String companyId, String partyId) {
		String roleIdFrom = RoleTypeEnum.ROLE_COMPANY.getKey();
		String roleIdTo = RoleTypeEnum.ROLE_EMPLOYEE.getKey();
		String relationType = PartyRelationType.EMPLOYMENT.getKey();
		createRelation(companyId, roleIdFrom, partyId, roleIdTo, relationType);
		logger.info("--->公司[{}]和人员[{}]关系创建完成 ...", companyId, partyId);
	}
	
	/**
	 * 创建【部门】与【人员】的关系。
	 * 
	 * @param deptId  部门标识
	 * @param partyId 人员标识
	 */
	@Transactional
	public void createDeptEmployment(String deptId, String partyId) {
		String roleIdFrom = RoleTypeEnum.ROLE_DETP.getKey();
		String roleIdTo = RoleTypeEnum.ROLE_EMPLOYEE.getKey();
		String relationType = PartyRelationType.GROUP_MEMBER.getKey();
		createRelation(deptId, roleIdFrom, partyId, roleIdTo, relationType);
		logger.info("--->部门[{}]和人员[{}]关系创建完成 ...", deptId, partyId);
	}
	
	/**
	 * 删除【部门】与【人员】的关系。
	 * 
	 * @param deptId  部门标识
	 * @param partyId 人员标识
	 */
	@Transactional
	public void deleteDeptEmployment(String deptId, String partyId) {
		String roleIdFrom = RoleTypeEnum.ROLE_DETP.getKey();
		String roleIdTo = RoleTypeEnum.ROLE_EMPLOYEE.getKey();
		String relationType = PartyRelationType.GROUP_MEMBER.getKey();
		// 组装对象（精准删除）
		PartyRelation relation = new PartyRelation();
		relation.setId(new PartyRelationId(deptId, roleIdFrom, partyId, roleIdTo));
		relation.setRelationTypeId(relationType);
		partyRelationRepository.delete(relation);
//		partyRelationRepository.deleteByIdPartyIdFromAndPartyIdToAndRelationTypeId(deptId, partyId, relationType);
		logger.info("--->部门[{}]和人员[{}]关系删除完成 ...", deptId, partyId);
	}
	
	/**
	 * 读取【部门】与【人员】的关系。
	 * 
	 * @param deptId  部门标识
	 * @param partyId 人员标识
	 */
	public PartyRelation findDeptEmployment(String deptId, String partyId) {
		String roleIdFrom = RoleTypeEnum.ROLE_DETP.getKey();
		String roleIdTo = RoleTypeEnum.ROLE_EMPLOYEE.getKey();
		Optional<PartyRelation> pr = partyRelationRepository.findById(new PartyRelationId(deptId, roleIdFrom, partyId, roleIdTo));
		if (pr.isPresent()) {
			return pr.get();
		} else {
			return null;
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
		createRelation(partyId, roleIdFrom, deptId, roleIdTo, PartyRelationType.PARTY_OWNER.getKey());
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
		partyRelationRepository.deleteById(new PartyRelationId(oldPartyId, roleIdFrom, deptId, roleIdTo));
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
	 * 创建【Party】与【Party】的父子关系。
	 * 
	 * @param pPartyId 人员标识(父）
	 * @param pRoleId  角色标识(父）
	 * @param cPartyId 人员标识(子）
	 * @param cRoleId  角色标识(子）
	 * @param sequence 排序号
	 */
	@Transactional
	public void createPartyParentChildRelation(String pPartyId, String pRoleId, String cPartyId, String cRoleId, Integer sequence) {
		String relationTypeId = PartyRelationType.PARENT_CHILD.getKey();
		PartyRelation relation = new PartyRelation();
		relation.setId(new PartyRelationId(pPartyId, pRoleId, cPartyId, cRoleId));
		relation.setRelationTypeId(relationTypeId);
		relation.setSequence(sequence);
		partyRelationRepository.save(relation);
		logger.info("--->关系创建完成: {} -> {} [{}]", pPartyId, cPartyId, relationTypeId);
	}
	
	/**
	 * 删除【公司】与【员工】的关系。
	 * 
	 * @param deptId     部门标识
	 * @param newPartyId 新负责人标识
	 * @param oldPartyId 老负责人标识
	 */
//	@Transactional
//	public void deleteCompanyEmployment(String deptId, String newPartyId, String oldPartyId) {
//		deleteDeptPartyOwner(deptId, newPartyId, oldPartyId);
//		String roleIdFrom = RoleTypeEnum.ROLE_EMPLOYEE.getKey();
//		String roleIdTo = RoleTypeEnum.ROLE_DETP.getKey();
//		createRelation(newPartyId, roleIdFrom, deptId, roleIdTo, PartyRelationType.PARTY_OWNER.getKey());
//	}
	
	private void createRelation(String partyIdFrom, String roleIdFrom, String partyIdTo, String roleIdTo, String relationTypeId) {
		PartyRelation relation = new PartyRelation();
		relation.setId(new PartyRelationId(partyIdFrom, roleIdFrom, partyIdTo, roleIdTo));
		relation.setRelationTypeId(relationTypeId);
		partyRelationRepository.save(relation);
		logger.info("--->关系创建完成: {} -> {} [{}]", partyIdFrom, partyIdTo, relationTypeId);
	}
}
