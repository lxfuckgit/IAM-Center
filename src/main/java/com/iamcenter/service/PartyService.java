package com.iamcenter.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.iamcenter.business.PartyBusiness;
import com.iamcenter.business.RBACBusiness;
import com.iamcenter.business.TokenBusiness;
import com.iamcenter.business.ValidateBusiness;
import com.iamcenter.common.code.PartyErrorCode;
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
import com.saasapi.contract.party.dto.DepartmentListDTO;
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
	PartyRelationRepository partyRelationDao;

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
		if(StringUtils.isBlank(dto.getCompanyId())) {
			return ResultBuilder.buildResult(ErrorCode.PARAMS_ID);
		}
		
		List<PartyRelation> prList = partyRelationDao.findByIdPartyIdFrom(dto.getCompanyId());
		if (null != prList && prList.size() > 0) {
			logger.info("--->[{}]公司信息存在引用关系！", dto.getCompanyId());
		}
		
		partyGroupRepository.deleteById(dto.getCompanyId());
		logger.info("--->[{}]公司信息已删除！", dto.getCompanyId());
		return ResultBuilder.normalResult();
	}
	
	@Override
	public PageResult<DepartmentVO> listDepartment(DepartmentListDTO dto) {
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
		// 查找部门负责人
		pageList.getData().forEach(action -> {
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
	@Transactional
	public RstResult<String> createDepartment(DeptCreateDTO dto) {
		RstResult<String> checkAppResult = validateBusiness.checkLoginAppId(tokenBusiness.getTokenAssocAppId());
		if (!ResultBuilder.RESPONSE_OK.equals(checkAppResult.getCode())) {
			return checkAppResult;
		}
		if (StringUtils.isBlank(checkAppResult.getData())) {
			return ResultBuilder.buildResult(PartyErrorCode.PARAMS_NO_COMPANY);
		}
		GroupCreateDTO pg = new GroupCreateDTO();
		pg.setRoleType(RoleTypeEnum.ROLE_DETP);
		pg.setCode(dto.getDeptCode());
		pg.setName(dto.getDeptName());
		pg.setHeadPartyId(dto.getHeadPartyId());
		pg.setParentId(checkAppResult.getData());
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
		Optional<PartyGroup> optional = partyGroupRepository.findById(dto.getDeptId());
		if (optional.isEmpty()) {
			return ResultBuilder.buildResult(ErrorCode.INVALID_ID);
		}
		if (StringUtils.isNotBlank(dto.getDeptCode())) {
			optional.get().setGroupCode(dto.getDeptCode());
		}
		if (StringUtils.isNotBlank(dto.getDeptName())) {
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
		
		return ResultBuilder.normalResult();
	}
	
	@Override
	@Transactional
	public RstResult<String> deleteDepartment(DeptUpdateDTO dto) {
		if(StringUtils.isBlank(dto.getDeptId())) {
			return ResultBuilder.buildResult(ErrorCode.PARAMS_ID);
		}
		
		List<PartyRelation> prList = partyRelationDao.findByIdPartyIdFrom(dto.getDeptId());
		if (null != prList && prList.size() > 0) {
			logger.info("--->[{}]部门信息存在引用关系！", dto.getDeptId());
		}
		
		partyRelationDao.deleteAll(partyRelationDao.findByIdPartyIdTo(dto.getDeptId()));
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
		if (null != getPersonBySfz(dto.getIdCard())) {
			logger.error("--->身份证号[{}]重复!.", dto.getIdCard());
			return ResultBuilder.buildResult(ErrorCode.ERROR_USER_IDCARD_INVALID, "身份证号重复!");
		} else if (null == dto.getName()) {
			logger.warn("用户姓名有误，请检查数据完整性!");
			return ResultBuilder.buildResult(ErrorCode.PARAMS_EMPTY);
//		} else if (null == dto.getMobilePhone()) {
//			logger.warn("用户身机号有误，请检查数据完整性!");
//			return ResultBuilder.buildResult(ErrorCode.PARAMS_EMPTY);
		}
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
			String roleIdFrom = RoleTypeEnum.ROLE_DETP.getKey();
			String relationType = PartyRelationType.PARENT_CHILD.getKey();
			partyBusiness.createRelation(dto.getDeptId(), roleIdFrom, person.getPartyId(), person.getRoleTypeId(), relationType);
			logger.info("--->[{}]成员关系创建完成 ...", person.getLastName());
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
		optional.get().setCode(dto.getCode());
		optional.get().setFirstName(dto.getName());
		optional.get().setLastName(dto.getName());
		optional.get().setNickName(dto.getNickName());
		optional.get().setIdCard(dto.getIdCard());
		optional.get().setSex(dto.getSex());
		optional.get().setMobilePhone(dto.getMobilePhone());
		optional.get().setBirthday(dto.getBirthday());
//		optional.get().setIconId(dto.getIconId());
		partyPersonDao.save(optional.get());

		/*2、update contact info*/
		// contactBusiness.updateContact(xxx);
		
		/*3、update dept info*/
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
		if(null != object) {
			logger.error("----------->当前团体名称{}已存在，请更换新名称!", dto.getName());
			return ResultBuilder.buildResult(ErrorCode.EXIST_NAME);
		}
		if (StringUtils.isNotBlank(dto.getParentId())) {
			Optional<Party> optional = partyDao.findById(dto.getParentId());
			if(!optional.isPresent()) {
				logger.error("----------->团体关联上级不存在，请检查并更换!", dto.getName());
				return ResultBuilder.buildResult(ErrorCode.EXIST_NAME);	
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
			PartyRelation relation = new PartyRelation();
			relation.setId(new PartyRelationId(dto.getParentId(), RoleTypeEnum.ROLE_COMPANY.getKey(), pg.getPartyId(), dto.getRoleType().name()));
			relation.setRelationTypeId(PartyRelationType.PARENT_CHILD.getKey());
			relation.setSequence(dto.getParentSeq());
			partyRelationDao.save(relation);
			logger.info("--->[{}]团体关系1创建完成 ...", pg.getGroupName());
		}
		
		/* 保存负责人关系 */
		if (StringUtils.isNotBlank(dto.getHeadPartyId())) {
			PartyRelation relation = new PartyRelation();
			relation.setId(new PartyRelationId(pg.getPartyId(), dto.getRoleType().name(), dto.getHeadPartyId(), RoleTypeEnum.ROLE_EMPLOYEE.getKey()));
			relation.setRelationTypeId(PartyRelationType.PARTY_OWNER.getKey());
			relation.setSequence(0);
			partyRelationDao.save(relation);
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
		if (StringUtils.isNotBlank(dto.getPartyId()) || StringUtils.isNotBlank(dto.getStatusId())) {
			return ResultBuilder.buildResult(ErrorCode.PARAMS_EMPTY);
		}
		/* 查询组织信息 */
		Optional<Party> optional = partyDao.findById(dto.getPartyId());
		if (optional.isEmpty()) {
			return ResultBuilder.buildResult(ErrorCode.INVALID_ID);
		}
		optional.get().setStatusId(dto.getStatusId());
		partyDao.save(optional.get());
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
		if (StringUtils.isEmpty(partyIdFrom)||StringUtils.isEmpty(partyIdTo)) {
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

}
