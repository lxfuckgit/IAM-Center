package com.iamcenter.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.iamcenter.business.PartyBusiness;
import com.iamcenter.business.RBACBusiness;
import com.iamcenter.business.TokenBusiness;
import com.iamcenter.business.ValidateBusiness;
import com.iamcenter.common.code.PartyErrorCode;
import com.iamcenter.domain.apps.AppInfo;
import com.iamcenter.domain.party.Party;
import com.iamcenter.domain.party.PartyGroup;
import com.iamcenter.domain.party.PartyPerson;
import com.iamcenter.domain.party.PartyRelation;
import com.iamcenter.domain.party.PartyRelationId;
import com.iamcenter.domain.security.SysLogin;
import com.iamcenter.repository.PageQueryRepository;
import com.iamcenter.repository.SysLoginRepository;
import com.iamcenter.repository.apps.AppInfoRepository;
import com.iamcenter.repository.party.PartyGroupRepository;
import com.iamcenter.repository.party.PartyPersonRepository;
import com.iamcenter.repository.party.PartyRelationDao;
import com.iamcenter.repository.party.PartyRelationRepository;
import com.iamcenter.repository.party.PartyRepository;
import com.javapai.framework.action.PageResult;
import com.javapai.framework.action.ResultBuilder;
import com.javapai.framework.action.RstResult;
import com.javapai.framework.enums.ErrorCode;
import com.javapai.framework.enums.StatusEnum;
import com.saasapi.contract.party.PartyContract;
import com.saasapi.contract.party.dto.CompanyCreateDTO;
import com.saasapi.contract.party.dto.CompanyListDTO;
import com.saasapi.contract.party.dto.CompanyUpdateDTO;
import com.saasapi.contract.party.dto.DeptListDTO;
import com.saasapi.contract.party.dto.DeptCreateDTO;
import com.saasapi.contract.party.dto.DeptOwnerDTO;
import com.saasapi.contract.party.dto.DeptUpdateDTO;
import com.saasapi.contract.party.dto.GroupCreateDTO;
import com.saasapi.contract.party.dto.PartyStatusChangeDTO;
import com.saasapi.contract.party.dto.PersonCreateDTO;
import com.saasapi.contract.party.dto.PersonListDTO;
import com.saasapi.contract.party.dto.PersonUpdateDTO;
import com.saasapi.contract.party.enums.PartyRelationType;
import com.saasapi.contract.party.enums.PartyType;
import com.saasapi.contract.party.enums.RoleTypeEnum;
import com.saasapi.contract.party.vo.CompanyVO;
import com.saasapi.contract.party.vo.DepartmentVO;
import com.saasapi.contract.party.vo.DeptHierarchy;
import com.saasapi.contract.party.vo.DeptTreeVO;
import com.saasapi.contract.party.vo.PersonVO;
import com.saasapi.contract.party.vo.Supplier;

@Service
public class PartyService implements PartyContract {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private PartyRepository partyDao;
	@Autowired
	private PartyGroupRepository partyGroupRepository;
	@Autowired
	private PartyPersonRepository partyPersonDao;
	@Autowired
	PartyRelationRepository partyRelationRepository;

	@Autowired
	private RBACBusiness rbacBusiness;

	@Autowired
	private PartyBusiness partyBusiness;

	@Autowired
	private ValidateBusiness validateBusiness;

	@Autowired
	private TokenBusiness tokenBusiness;

	@Autowired
	AppInfoRepository appInfoRepository;
	@Autowired
	PageQueryRepository pageQueryRepository;

	@Autowired
	private SysLoginRepository sysLoginRepository;
	
	@Autowired
	private PartyRelationDao partyRelationDao;

	@Override
	public PageResult<CompanyVO> listCompany(CompanyListDTO dto) {
		if (!tokenBusiness.hasSuperAdminRole()) {
			String cId = tokenBusiness.getTokenAssocCompanyId();
			if (StringUtils.isBlank(cId)) {
				return ResultBuilder.buildPageResult(dto.getPageIndex(), dto.getPageSize());
			}
			// 非超管账号只能查询关联的公司
			dto.setCompanyId(cId);
		}
		return pageQueryRepository.listCompany(dto);
	}

	@Override
	public RstResult<String> createCompany(CompanyCreateDTO dto) {
		GroupCreateDTO pg = new GroupCreateDTO();
		pg.setRoleType(RoleTypeEnum.ROLE_COMPANY);
		pg.setCode(dto.getCompanyCode());
		pg.setName(dto.getCompanyName());
		return createPartyGroup(pg);
	}

	@Override
	public RstResult<CompanyVO> getCompany(CompanyUpdateDTO dto) {
		Optional<PartyGroup> optional = partyGroupRepository.findById(dto.getCompanyId());
		if (optional.isEmpty()) {
			return ResultBuilder.buildResult(ErrorCode.INVALID_ID);
		}
		CompanyVO vo = new CompanyVO();
		vo.setCompanyId(optional.get().getPartyId());
		vo.setCompanyCode(optional.get().getGroupCode());
		vo.setCompanyName(optional.get().getGroupName());
		vo.setCreateTime(optional.get().getCreateTime().toLocalDateTime());
		return ResultBuilder.normalResult(vo);
	}

	@Override
	public RstResult<String> updateCompany(CompanyUpdateDTO dto) {
		Optional<PartyGroup> optional = partyGroupRepository.findById(dto.getCompanyId());
		if (optional.isEmpty()) {
			return ResultBuilder.buildResult(ErrorCode.INVALID_ID);
		}
		if (StringUtils.isNotBlank(dto.getCompanyCode())) {
			optional.get().setGroupCode(dto.getCompanyCode());
		}
		if (StringUtils.isNotBlank(dto.getCompanyName())) {
			optional.get().setGroupName(dto.getCompanyName());
		}
		if (StringUtils.isNotBlank(dto.getCompanyAddress())) {
			optional.get().setGroupCode(dto.getCompanyCode());
		}
		partyGroupRepository.save(optional.get());
		return ResultBuilder.normalResult();
	}

	@Override
	public RstResult<String> deleteCompany(CompanyUpdateDTO dto) {
		if (StringUtils.isBlank(dto.getCompanyId())) {
			return ResultBuilder.buildResult(ErrorCode.PARAMS_ID);
		}

		List<PartyRelation> prList = partyRelationRepository.findByIdPartyIdFrom(dto.getCompanyId());
		if (null != prList && prList.size() > 0) {
			logger.info("--->[{}]公司信息存在引用关系！", dto.getCompanyId());
		}
		
		partyGroupRepository.deleteById(dto.getCompanyId());
		logger.info("--->[{}]公司信息已删除！", dto.getCompanyId());
		return ResultBuilder.normalResult();
	}
	
	@Override
	public RstResult<List<DeptTreeVO>> treeDepartment(DeptListDTO dto) {
		String companyId = tokenBusiness.getTokenAssocCompanyId();
		if (StringUtils.isEmpty(companyId)) {
			logger.info("--->[{}]公司标识信息不明确！", dto.getCompanyId());
			return ResultBuilder.buildResult(PartyErrorCode.PARAMS_NO_COMPANY);
		}
		List<DeptTreeVO> treeList = new ArrayList<DeptTreeVO>();
		List<DepartmentVO> deptList = partyBusiness.listChildDept(companyId);
		deptList.stream().forEach(action -> {
			DeptTreeVO node = new DeptTreeVO();
			node.setDeptId(action.getDeptId());
			node.setDeptCode(action.getDeptCode());
			node.setDeptName(action.getDeptName());
			node.setParentId(action.getParentId());
			List<DepartmentVO> subList = partyBusiness.listChildDept(action.getDeptId());
			node.setChildList(convertToDepartyTree(subList));
			treeList.add(node);
		});
		return ResultBuilder.normalResult(treeList);
	}

	@Override
	public PageResult<DepartmentVO> listDepartment(DeptListDTO dto) {
		RstResult<String> checkAppResult = validateBusiness.checkLoginAppId(tokenBusiness.getTokenAssocAppId());
		if (!ResultBuilder.RESPONSE_OK.equals(checkAppResult.getCode())) {
			return ResultBuilder.buildPageResult(dto.getPageIndex(), dto.getPageSize());
		}
		dto.setCompanyId(checkAppResult.getData());
		if (StringUtils.isEmpty(dto.getCompanyId())) {
			logger.info("--->[{}]公司标识信息不明确！", dto.getCompanyId());
			return ResultBuilder.buildPageResult(dto.getPageIndex(), dto.getPageSize());
		}

		PageResult<DepartmentVO> pageList = pageQueryRepository.listDepartment(dto);
		pageList.getData().forEach(action -> {
			// 修正数据（当部门有上级部门时才显示parent_id
			if (action.getParentId().equals(dto.getCompanyId())) {
				action.setParentId(null);
			}
			// 查找部门负责人
			PartyPerson person = partyBusiness.getDeptPartyOwner(action.getDeptId());
			if (null != person) {
				action.setHeadPartyId(person.getPartyId());
				action.setHeadPartyName(person.getLastName());
				action.setContactPhone(person.getMobilePhone());
			}
		});
		return pageList;
	}
	
	@Override
	public RstResult<List<DeptHierarchy>> hierarchyDepartment(DeptListDTO dto) {
		RstResult<String> checkAppResult = validateBusiness.checkLoginAppId(tokenBusiness.getTokenAssocAppId());
		if (!ResultBuilder.RESPONSE_OK.equals(checkAppResult.getCode())) {
			return ResultBuilder.buildResult(ErrorCode.EXCEPTION_SELECT);
		}
		dto.setCompanyId(checkAppResult.getData());
		if (StringUtils.isEmpty(dto.getCompanyId())) {
			logger.info("--->[{}]公司标识信息不明确！", dto.getCompanyId());
			return ResultBuilder.buildResult(PartyErrorCode.PARAMS_NO_COMPANY);
		}

		List<DepartmentVO> deptList = partyBusiness.listChildDept(dto.getCompanyId());
		/* 判断公司部门（顶级） */
		if(null == deptList || deptList.size() == 0) {
			return ResultBuilder.normalResult();
		}
		List<DeptHierarchy> treeList = new ArrayList<DeptHierarchy>();
		deptList.stream().forEach(action -> {
			// 修正数据（当部门有上级部门时才显示parent_id
			if (action.getParentId().equals(dto.getCompanyId())) {
				action.setParentId(null);
			}
			DeptHierarchy hierarchy = convertToHierarchy(action);
			List<DepartmentVO> tempList = partyBusiness.listChildDept(action.getDeptId());
			if (null == deptList || deptList.size() == 0) {
				hierarchy.setChildList(List.of());
			} else {
				hierarchy.setChildList(tempList.stream().map(vo -> {
					return convertToHierarchy(vo);
				}).collect(Collectors.toList()));
			}
			treeList.add(hierarchy);
		});
		return ResultBuilder.normalResult(treeList);
	}

	@Override
	@Transactional
	public RstResult<String> createDepartment(DeptCreateDTO dto) {
		RstResult<AppInfo> checkAppResult = validateBusiness.getLoginAppInfo(tokenBusiness.getTokenAssocAppId());
		if (!ResultBuilder.RESPONSE_OK.equals(checkAppResult.getCode())) {
			return ResultBuilder.buildResult(PartyErrorCode.PARAMS_NO_COMPANY);
		}
		if (StringUtils.isBlank(checkAppResult.getData().getCompanyId())) {
			return ResultBuilder.buildResult(PartyErrorCode.PARAMS_NO_COMPANY);
		}
		if (partyBusiness.checkCompanyDeptCode(checkAppResult.getData().getCompanyId(), dto.getDeptCode())) {
			return ResultBuilder.buildResult(ErrorCode.EXIST_CODE);
		}
		if (partyBusiness.checkCompanyDeptName(checkAppResult.getData().getCompanyId(), dto.getDeptName())) {
			return ResultBuilder.buildResult(ErrorCode.EXIST_NAME);
		}
		GroupCreateDTO pg = new GroupCreateDTO();
		pg.setRoleType(RoleTypeEnum.ROLE_DETP);
		pg.setCode(dto.getDeptCode());
		pg.setName(dto.getDeptName());
		pg.setHeadPartyId(dto.getHeadPartyId());
		if (StringUtils.isNotBlank(dto.getParentId())) {
			pg.setParentId(dto.getParentId());
		} else {
			pg.setParentId(checkAppResult.getData().getCompanyId());
		}
		pg.setParentSeq(dto.getDeptSeq());
		return createPartyGroup(pg);
	}
	
	@Override
	public RstResult<DepartmentVO> getDepartment(DeptUpdateDTO dto) {
		Optional<PartyGroup> optional = partyGroupRepository.findById(dto.getDeptId());
		if (optional.isEmpty()) {
			return ResultBuilder.buildResult(ErrorCode.INVALID_ID);
		}
		DepartmentVO vo = new DepartmentVO();
		vo.setDeptId(optional.get().getPartyId());
		vo.setDeptCode(optional.get().getGroupCode());
		vo.setDeptName(optional.get().getGroupName());
		vo.setCreateTime(optional.get().getCreateTime().toLocalDateTime());
		return ResultBuilder.normalResult(vo);
	}

	@Override
	@Transactional
	public RstResult<String> updateDepartment(DeptUpdateDTO dto) {
		RstResult<AppInfo> checkAppResult = validateBusiness.getLoginAppInfo(tokenBusiness.getTokenAssocAppId());
		if (!ResultBuilder.RESPONSE_OK.equals(checkAppResult.getCode())) {
			return ResultBuilder.buildResult(PartyErrorCode.PARAMS_NO_COMPANY);
		}
		if (StringUtils.isBlank(checkAppResult.getData().getCompanyId())) {
			return ResultBuilder.buildResult(PartyErrorCode.PARAMS_NO_COMPANY);
		}
		Optional<PartyGroup> optional = partyGroupRepository.findById(dto.getDeptId());
		if (optional.isEmpty()) {
			return ResultBuilder.buildResult(ErrorCode.INVALID_ID);
		}
		if (StringUtils.isNotBlank(dto.getDeptCode()) && !optional.get().getGroupCode().equals(dto.getDeptCode())) {
			if (partyBusiness.checkCompanyDeptCode(checkAppResult.getData().getCompanyId(), dto.getDeptCode())) {
				return ResultBuilder.buildResult(ErrorCode.EXIST_CODE);
			}
			optional.get().setGroupCode(dto.getDeptCode());
		}
		if (StringUtils.isNotBlank(dto.getDeptName()) && !optional.get().getGroupName().equals(dto.getDeptName())) {
			if (partyBusiness.checkCompanyDeptName(checkAppResult.getData().getCompanyId(), dto.getDeptName())) {
				return ResultBuilder.buildResult(ErrorCode.EXIST_NAME);
			}
			optional.get().setGroupName(dto.getDeptName());
		}
		if (StringUtils.isNotBlank(dto.getStatusId())) {
			optional.get().setStatusId(dto.getStatusId());
		}
		partyGroupRepository.save(optional.get());
		
		/* 修改部门负责人(启用assignDepartmentOwner方法单独处理） */
//		PartyPerson person = partyBusiness.getDeptPartyOwner(optional.get().getPartyId());
//		if (null == person && StringUtils.isNotBlank(dto.getHeadPartyId())) {
//			// 当部门没有负责人时进行创建
//			partyBusiness.createDeptPartyOwner(dto.getHeadPartyId(), optional.get().getPartyId());
//		}
//		if (null != person && (!person.getPartyId().equals(dto.getHeadPartyId()))) {
//			// 当部门负责人不一致时进行更新
//			partyBusiness.updateDeptPartyOwner(optional.get().getPartyId(), dto.getHeadPartyId(), person.getPartyId());
//		}
//		
		/* 修改部门上下级 */
		if (StringUtils.isNotBlank(dto.getParentId())) {
			Integer depSeq = null == dto.getDeptSeq() ? 0 : dto.getDeptSeq();
			String relationTypeId = PartyRelationType.PARENT_CHILD.getKey();
			List<PartyRelation> list = partyRelationRepository.findByIdPartyIdToAndRelationTypeId(dto.getDeptId(), relationTypeId);
			if (null == list || list.size() == 0) {
				logger.info("--->[{}]部门未关联子部门信息！", dto.getDeptId());
				String roleId = RoleTypeEnum.ROLE_DETP.getKey();
				partyRelationDao.createPartyParentChildRelation(dto.getParentId(), roleId, dto.getDeptId(), roleId, depSeq);
			} else if (!dto.getParentId().equals(list.get(0).getId().getPartyIdFrom())) {
				// 否则检查上级（这里只保留一个上级）
				partyRelationRepository.delete(list.get(0));
				logger.info("--->[{}]部门原父节点[{}]更新完毕！", dto.getDeptId(), dto.getParentId());
				String roleId = RoleTypeEnum.ROLE_DETP.getKey();
				partyRelationDao.createPartyParentChildRelation(dto.getParentId(), roleId, dto.getDeptId(), roleId, depSeq);
				logger.info("--->[{}]部门新父节点[{}]更新完毕！", dto.getDeptId(), dto.getParentId());
//			} else if (null != dto.getDeptSeq() && dto.getDeptSeq() > 0 && !dto.getDeptSeq().equals(list.get(0).getSequence())) {
			} else if (!depSeq.equals(list.get(0).getSequence())) {
				// 修改部门排序号(必需指定上级才能修改排序号）
				list.get(0).setSequence(dto.getDeptSeq());
				partyRelationRepository.save(list.get(0));
				logger.info("--->[{}]部门排序号[{}]更新完毕！", dto.getDeptId(), dto.getDeptSeq());
			} else {
				// 其它情况，暂不处理
			}
		}

		return ResultBuilder.normalResult();
	}

	@Override
	@Transactional
	public RstResult<String> deleteDepartment(DeptUpdateDTO dto) {
		if (StringUtils.isBlank(dto.getDeptId())) {
			return ResultBuilder.buildResult(ErrorCode.PARAMS_ID);
		}

		List<PartyRelation> prList = partyRelationRepository.findByIdPartyIdFrom(dto.getDeptId());
		if (null != prList && prList.size() > 0) {
			logger.info("--->[{}]部门信息存在引用关系！", dto.getDeptId());
			return ResultBuilder.buildResult(PartyErrorCode.ERROR_HAS_CHILD);
		}
		
		partyRelationRepository.deleteAll(partyRelationRepository.findByIdPartyIdTo(dto.getDeptId()));
		logger.info("--->[{}]部门关联信息已删除！", dto.getDeptId());
		partyGroupRepository.deleteById(dto.getDeptId());
		logger.info("--->[{}]部门信息已删除！", dto.getDeptId());
		return ResultBuilder.normalResult();
	}

	@Override
	public RstResult<String> assignDepartmentOwner(DeptOwnerDTO dto) {
		if (StringUtils.isBlank(dto.getDeptId()) || StringUtils.isBlank(dto.getPartyId())) {
			return ResultBuilder.buildResult(ErrorCode.PARAMS_ID);
		}
		PartyPerson person = partyBusiness.getDeptPartyOwner(dto.getDeptId());
		if (null == person || StringUtils.isBlank(person.getPartyId())) {
			// 当部门没有负责人时进行创建
			partyBusiness.createDeptPartyOwner(dto.getDeptId(), dto.getPartyId());
		} else {
			// 当部门负责人不一致时进行更新
			partyBusiness.updateDeptPartyOwner(dto.getDeptId(), dto.getPartyId(), person.getPartyId());
		}
		return ResultBuilder.normalResult();
	}

	@Override
	public PageResult<PersonVO> listPerson(PersonListDTO dto) {
		PageResult<PersonVO> pageList = pageQueryRepository.listPerson(dto);
		// 查找部门信息
//		pageList.getData().forEach(action -> {
//			PartyPerson person = partyBusiness.getDeptPartyOwner(action.getDeptId());
//			if (null != person) {
//				action.setDeptId(person.getd);
//				action.setContactPhone(person.getMobilePhone());
//			}
//		});
		return pageList;
	}

	@Override
	@Transactional
	public RstResult<String> createPerson(PersonCreateDTO dto) {
		String companyId = tokenBusiness.getTokenAssocCompanyId();
		if (StringUtils.isBlank(companyId)) {
			return ResultBuilder.buildResult(ErrorCode.EXCEPTION_CREATE);
		}
		if (dto.getCode() == null || dto.getCode().isEmpty()) {
			return ResultBuilder.buildResult("40000001", "用户编号不能为空");
		}
		if (dto.getName() == null || dto.getName().isEmpty()) {
			return ResultBuilder.buildResult("40000001", "用户姓名不能为空");
		}
		if (partyBusiness.checkCompanyPartyCode(companyId, dto.getCode())) {
			logger.error("--->人员编号[{}]重复!.", dto.getCode());
			return ResultBuilder.buildResult(ErrorCode.EXIST_CODE);
		}
		partyPersonDao.findByCode(companyId);
		if (null != getPersonBySfz(dto.getIdCard())) {
			logger.error("--->身份证号[{}]重复!.", dto.getIdCard());
			return ResultBuilder.buildResult(ErrorCode.ERROR_USER_IDCARD_INVALID, "身份证号重复!");
		} 
//		if (null == dto.getName()) {
//			logger.warn("用户姓名有误，请检查数据完整性!");
//			return ResultBuilder.buildResult(ErrorCode.PARAMS_EMPTY);
//		} else if (null == dto.getMobilePhone()) {
//			logger.warn("用户身机号有误，请检查数据完整性!");
//			return ResultBuilder.buildResult(ErrorCode.PARAMS_EMPTY);
//		}
		if (StringUtils.isNotBlank(dto.getDeptId())) {
			if (!partyGroupRepository.existsById(dto.getDeptId())) {
				return ResultBuilder.buildResult("40000001", "部门信息不存在！");
			}
		}

		/* 1、人员信息创建 */
		PartyPerson person = new PartyPerson();
		person.setPartyTypeId(PartyType.PPERSON.getKey());
		person.setRoleTypeId(RoleTypeEnum.ROLE_EMPLOYEE.getKey());
		person.setCompanyId(companyId);
		person.setCode(dto.getCode());
		person.setLastName(dto.getName());
		person.setNickName(dto.getNickName());
		person.setIdCard(dto.getIdCard());
		person.setMobilePhone(dto.getMobilePhone());
		person.setSex(dto.getSex());
		person.setStatusId(dto.getStatusId());
		partyPersonDao.save(person);
		
		/* 2、人员归属部门信息创建 */
		if (StringUtils.isNotBlank(dto.getDeptId())) {
			partyRelationDao.createDeptEmployment(dto.getDeptId(), person.getPartyId());
		}
		/* 3、人员归属公司信息创建 */
		if (StringUtils.isNotBlank(companyId)) {
			partyRelationDao.createCompanyEmployment(companyId, person.getPartyId());
		}

//		if (null != dto.getPhone()) {
//			MyContactDTO contact = new MyContactDTO(party.getPartyId(), ContactType.CONTACT_PHONE, dto.getPhone());
////			hy700Repository.save(contact);
//		}
//		if (null != dto.getEmail()) {
//			MyContactDTO contact = new MyContactDTO(party.getPartyId(), ContactType.CONTACT_EMAIL, dto.getPhone());
////			hy700Repository.save(contact);
//		}

		if (null != dto.getTags() && dto.getTags().size() > 0) {

		}

		if (null != dto.getLoginId()) {
			// bind pId to loginId(mq sent to:);
//			personBusines.bindPartyId2LoginId();
		}

		RstResult<String> result = ResultBuilder.normalResult();
		result.setData(person.getPartyId());
		return result;
	}

	@Override
	@Transactional
	public RstResult<String> deletePerson(String partyId) {
		if (StringUtils.isBlank(partyId)) {
			return ResultBuilder.buildResult(ErrorCode.PARAMS_EMPTY);
		}
		partyBusiness.deleteAllByPartyId(partyId);
		List<SysLogin> loginList = sysLoginRepository.findByPartyId(partyId);
		if (null != loginList && loginList.size() > 0) {
			loginList.forEach(login -> rbacBusiness.deleteAllByLoginId(login.getLoginId()));
		}
		return ResultBuilder.normalResult();
	}

	@Override
	@Transactional
	public RstResult<String> updatePerson(PersonUpdateDTO dto) {
		if (StringUtils.isBlank(dto.getPartyId())) {
			return ResultBuilder.buildResult("40000003", "个人标识不能为空");
		}
		Optional<PartyPerson> optional = partyPersonDao.findById(dto.getPartyId());
		if (!optional.isPresent()) {
			return ResultBuilder.buildResult("40000002", "个人不存在");
		}
		
		/* 1、update base info */
		if (dto.getCode() != null) {
			optional.get().setCode(dto.getCode());
		}
		if (dto.getName() != null) {
			optional.get().setFirstName(dto.getName());
		}
		if (dto.getName() != null) {
			optional.get().setLastName(dto.getName());
		}
		if (dto.getNickName() != null) {
			optional.get().setNickName(dto.getNickName());
		}
		if (dto.getIdCard() != null) {
			optional.get().setIdCard(dto.getIdCard());
		}
		if (dto.getSex() != null) {
			optional.get().setSex(dto.getSex());
		}
		if (dto.getMobilePhone() != null) {
			optional.get().setMobilePhone(dto.getMobilePhone());
		}
		if (dto.getBirthday() != null) {
			optional.get().setBirthday(dto.getBirthday());
		}
		// if (dto.getIconId() != null) {
//		     optional.get().setIconId(dto.getIconId());
		// }
		partyPersonDao.save(optional.get());

		/* 2、update contact info */
		// contactBusiness.updateContact(xxx);

		/* 3、update dept info */
		if (StringUtils.isNotBlank(dto.getDeptId())) {
			partyBusiness.updatePartyToDept(dto.getPartyId(), dto.getDeptId());
		}

		return ResultBuilder.normalResult();
	}

	@Override
	public RstResult<PersonVO> getPersonById(String partyId) {
		Optional<PartyPerson> optional = partyPersonDao.findById(partyId);
		if (!optional.isPresent()) {
			return ResultBuilder.buildResult(ErrorCode.INVALID_ID);
		}

//		List<String> set = Arrays.asList("phone", "code", "realname", "realnameAllStatus");
//		Map<String, String> data = getPerson(partyId, new HashSet<>(set));
//		if (data != null) {
//			vo.setUserId(Long.valueOf(data.get("userId")));
//			vo.setUsername(data.get("phone"));
//			vo.setRealname(data.get("realname"));
//			vo.setRealnameStatus(data.get("realnameAllStatus"));
//			return vo;
//		}
		return ResultBuilder.normalResult(convertPersonVO(optional.get()));
	}

	@Override
	public PersonVO getPersonBySfz(String idCard) {
		PartyPerson person = partyPersonDao.findByIdCard(idCard);
		if (null == person) {
			return null;
		}
		return convertPersonVO(person);
	}

	private PersonVO convertPersonVO(PartyPerson person) {
		PersonVO vo = new PersonVO();
		vo.setPartyId(person.getPartyId());
		vo.setPartyCode(person.getCode());
		vo.setPartyName(person.getLastName());
		vo.setMobilePhone(person.getMobilePhone());
		vo.setSex(person.getSex());
		vo.setIdCard(person.getIdCard());
		return vo;
	}

	@Override
	public PersonVO getPersonByCode(String code) {
		return null;
	}

	@Override
	@Transactional
	public RstResult<String> createPartyGroup(GroupCreateDTO dto) {
//		if (StringUtils.isEmpty(dto.getAppId())) {
//			return ResultBuilder.buildResult(ErrorCode.PARAMS_APPID);
//		}
		if (dto.getName() == null || dto.getName().isEmpty()) {
			return ResultBuilder.buildResult("40000001", "团体名称不能为空");
		}
		if (dto.getRoleType() == null) {
			return ResultBuilder.buildResult("40000001", "团体类型不能为空");
		}
		PartyGroup object = partyGroupRepository.findByGroupCodeAndGroupName(dto.getCode(), dto.getName());
		if (null != object) {
			logger.error("----------->当前团体名称{}已存在，请更换新名称!", dto.getName());
			return ResultBuilder.buildResult(ErrorCode.EXIST_NAME);
		}
		String parentRoleId = RoleTypeEnum.ROLE_ORG.getKey();
		if (StringUtils.isNotBlank(dto.getParentId())) {
			Optional<Party> optional = partyDao.findById(dto.getParentId());
			if (!optional.isPresent()) {
				logger.error("----------->团体关联上级不存在，请检查并更换!", dto.getName());
				return ResultBuilder.buildResult(ErrorCode.EXIST_NAME);
			} else {
				parentRoleId = optional.get().getRoleTypeId();
				logger.error("----------->团体ParentRole重置为：{}", parentRoleId);
			}
		}

		/* 保存数据 */
		PartyGroup pg = new PartyGroup();
		pg.setAppId(dto.getAppId());
		pg.setGroupCode(dto.getCode());
		pg.setGroupName(dto.getName());
		pg.setStatusId(StatusEnum.ENABLE.name());
		pg.setPartyTypeId(PartyType.PGROUP.getKey());
		pg.setRoleTypeId(dto.getRoleType().name());
		partyGroupRepository.save(pg);
		logger.info("--->[{}]团体信息创建完成 ...", pg.getGroupName());
		
		/* 保存上下级关系 */
		if (StringUtils.isNotBlank(dto.getParentId())) {
			partyRelationDao.createPartyParentChildRelation(dto.getParentId(), parentRoleId, pg.getPartyId(), dto.getRoleType().name(), dto.getParentSeq());
			logger.info("--->[{}]团体关系1创建完成 ...", pg.getGroupName());
		}
		
		/* 保存负责人关系 */
		if (StringUtils.isNotBlank(dto.getHeadPartyId())) {
			PartyRelation relation = new PartyRelation();
			relation.setId(new PartyRelationId(pg.getPartyId(), dto.getRoleType().name(), dto.getHeadPartyId(), RoleTypeEnum.ROLE_EMPLOYEE.getKey()));
			relation.setRelationTypeId(PartyRelationType.PARTY_OWNER.getKey());
			relation.setSequence(0);
			partyRelationRepository.save(relation);
			logger.info("--->[{}]团体关系2创建完成 ...", pg.getGroupName());
		}
		
		/* 返回结果 */
		RstResult<String> result = ResultBuilder.normalResult();
		result.setData(pg.getPartyId());
		return result;
	}

	@Override
	public RstResult<List<GroupCreateDTO>> listPartyGroup() {
//		return ResultBuilder.buildResult(hy101Repository.findAll().stream().map(mapper -> {
//			PartyGroupDTO vo = new PartyGroupDTO();
//			BeanUtils.copyProperties(mapper, vo);
//			return vo;
//		}).collect(Collectors.toList()));
		return null;
	}

	@Override
	public RstResult<String> changePartyStatus(PartyStatusChangeDTO dto) {
		if (StringUtils.isBlank(dto.getPartyId()) || StringUtils.isBlank(dto.getStatusId())) {
			return ResultBuilder.buildResult(ErrorCode.PARAMS_EMPTY);
		}
		if (!StatusEnum.ENABLE.getValue().equals(dto.getStatusId()) && !StatusEnum.DISABLE.getValue().equals(dto.getStatusId())) {
			return ResultBuilder.buildResult(ErrorCode.INVALID_CODE, "无效状态码，请检查！");
		}
		/* 查询组织信息 */
		Optional<Party> optional = partyDao.findById(dto.getPartyId());
		if (optional.isEmpty()) {
			return ResultBuilder.buildResult(ErrorCode.INVALID_ID);
		}
//		String appId = tokenBusiness.getTokenAssocAppId();
//		if (StringUtils.isNotBlank(appId) && (!optional.get().getAppId().equals(appId))) {
//			logger.info("--->警告：用户({})非法访问数据！", dto.getPartyId());
//			return ResultBuilder.buildResult(ErrorCode.INVALID_ID);
//		}
		/* 设置人员状态 */
		optional.get().setStatusId(dto.getStatusId());
		partyDao.save(optional.get());
		/* 设置账号状态 */
		List<SysLogin> loginList = sysLoginRepository.findByPartyId(dto.getPartyId());
		if (null != loginList && loginList.size() > 0) {
			loginList.forEach(login -> {
				login.setLoginState(dto.getStatusId());
				sysLoginRepository.save(login);
			});
		}
		/* 返回请求结果 */
		return ResultBuilder.normalResult();
	}

	@Override
	public Map<String, String> getPerson(String partyId, Set<String> filed) {
		return null;
	}

	@Override
	public List<Supplier> listSupplier() {
		return null;
	}

	public RstResult<String> addRelation(String partyIdFrom, String partyIdTo, String relationTypeId) {
		if (StringUtils.isEmpty(partyIdFrom) || StringUtils.isEmpty(partyIdTo)) {
			return ResultBuilder.buildResult(ErrorCode.PARAMS_EMPTY);
		}
		if (StringUtils.isEmpty(relationTypeId)) {
			return ResultBuilder.buildResult(ErrorCode.PARAMS_EMPTY);
		}
//		partyBusiness.createRelation(partyIdFrom, partyIdTo, relationTypeId);
		return ResultBuilder.normalResult();
	}

	public RstResult<String> deleteRelation(String partyIdFrom, String partyIdTo) {
		if (StringUtils.isEmpty(partyIdFrom) || StringUtils.isEmpty(partyIdTo)) {
			return ResultBuilder.buildResult(ErrorCode.PARAMS_EMPTY);
		}
//		PartyRelationId id = new PartyRelationId(partyIdFrom, partyIdTo);
//		if (!partyRelationDao.existsById(id)) {
//			return ResultBuilder.buildResult("40000002", "关系不存在");
//		}
//		partyRelationDao.deleteById(id);
		logger.info("--->关系删除完成: {} -> {}", partyIdFrom, partyIdTo);
		return ResultBuilder.normalResult();
	}

//  @Override
//  public ResponseResult<UserInfoVO> getUserInfoByUserName(String userName) {
//      Transaction trans = EE.newTransaction("Service", "UserInfoService#getUserInfoByUserName");
//      UserInfoVO user = new UserInfoVO();
//      user.setUsername(userName);
//      try {
//          Set<String> field = new HashSet<>();
//          field.add("phone");
//          field.add("idCard");
//          List<Map<String, String>> userList = profileBusiness.getUserInfo(userName, field);
//          if (userList == null) {
//              EE.logEvent("Service", "userRegister");
//              return ResponseResultBuilder.buildErrorResponse(CenterConstants.ERROR_CODE_DEFAULT,"用户标识无效,请核对!");
//          } else {
//              if (userList.size() > 1) {
//                  EE.logEvent("Service", "userRegister");
//                  return ResponseResultBuilder.buildErrorResponse(CenterConstants.ERROR_CODE_DEFAULT,"用户("+userName+")存在多条记录!");
//              } else {
//                  Map<String, String> map = userList.get(0);
//                  String idCard = map.get("idCard");
//                  if(StringUtils.isNotBlank(idCard)){
//                      user.setIdCard(idCard);
//                  }
//                  return ResponseResultBuilder.buildNormalResponse(user);
//              }
//          }
//      } catch (BizException e) {
//          EE.logError("UserInfoService#getUserInfoByUserName",e);
//          logger.error("UserInfoService#getUserInfoByUserName", e);
//      } finally {
//          trans.complete();
//      }
//      return ResponseResultBuilder.buildNormalResponse(user);
//  }

	private List<DeptTreeVO> convertToDepartyTree(List<DepartmentVO> deptList) {
		if (null == deptList || deptList.size() == 0) {
			return List.of();
		}
		return deptList.stream().map(vo -> {
			DeptTreeVO tree = new DeptTreeVO();
			tree.setDeptId(vo.getDeptId());
			tree.setParentId(vo.getParentId());
			tree.setDeptCode(vo.getDeptCode());
			tree.setDeptName(vo.getDeptName());
			return tree;
		}).collect(Collectors.toList());
	}
	
	/**
	 * DepartmentVO -> DeptHierarchy 转换，并填充负责人信息
	 */
	private DeptHierarchy convertToHierarchy(DepartmentVO dept) {
		DeptHierarchy hierarchy = new DeptHierarchy();
		BeanUtils.copyProperties(dept, hierarchy);
		// 查找部门负责人
		PartyPerson person = partyBusiness.getDeptPartyOwner(dept.getDeptId());
		if (null != person) {
			hierarchy.setHeadPartyId(person.getPartyId());
			hierarchy.setHeadPartyName(person.getLastName());
			hierarchy.setContactPhone(person.getMobilePhone());
		}
		return hierarchy;
	}

}
