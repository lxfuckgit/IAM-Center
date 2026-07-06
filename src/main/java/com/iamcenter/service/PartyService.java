package com.iamcenter.service;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.iamcenter.business.PartyBusiness;
import com.iamcenter.domain.party.Party;
import com.iamcenter.domain.party.PartyGroup;
import com.iamcenter.domain.party.PartyPerson;
import com.iamcenter.domain.party.PartyRelationId;
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
import com.saasapi.contract.party.dto.CompanyListDTO;
import com.saasapi.contract.party.dto.DepartmentListDTO;
import com.saasapi.contract.party.dto.PartyGroupDTO;
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

@DubboService
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
	private PartyBusiness partyBusiness;

//	@Autowired
//	private ContactContract contactService;

//	@Autowired
//	private HY700Repository hy700Repository;
	
	@Override
	public PageResult<CompanyVO> listCompany(CompanyListDTO dto) {
		return partyBusiness.listCompany(dto);
//		return ResultBuilder.buildResult(hy101Repository.findAll().stream().map(mapper -> {
//			CompanyVO vo = new CompanyVO();
//			BeanUtils.copyProperties(mapper, vo);
//			return vo;
//		}).collect(Collectors.toList()));
	}
	
	@Override
	public PageResult<PersonVO> listPerson(PersonListDTO dto) {
		return partyBusiness.listPerson(dto);
//		return ResultBuilder.buildResult(partyPersonDao.findAll().stream().map(mapper -> {
//			PersonVO vo = new PersonVO();
//			vo.setPartyId(mapper.getPartyId());
//			vo.setPartyCode(mapper.getCode());
//			vo.setRealname(mapper.getPersonName());
//			vo.setSex(mapper.getSex());
//			return vo;
//		}).collect(Collectors.toList()));
	}

	@Override
	public RstResult<String> createPerson(PersonCreateDTO dto) {
//		if (dto.getCode() == null || dto.getCode().isEmpty()) {
//			return ResultBuilder.buildResult("40000001", "用户编号不能为空");
//		}
		if (dto.getName() == null || dto.getName().isEmpty()) {
			return ResultBuilder.buildResult("40000001", "用户姓名不能为空");
		}
		if (null != getPersonBySfz(dto.getIdcard())) {
			logger.error("--->身份证号[{}]重复!.", dto.getIdcard());
			return ResultBuilder.buildResult(ErrorCode.ERROR_USER_IDCARD_INVALID);
		} else if (null == dto.getName() || null == dto.getPhone()) {
			logger.warn("用户姓名或用户身机号有误，请检查数据完整性!");
			return ResultBuilder.buildResult(ErrorCode.PARAMS_EMPTY);
		} else {
			// personBusiness.findByPerson(dto.getName(), dto.getPhone());
//			if (personBusiness.findByPerson(dto.getName(), dto.getPhone()).size() >= 1) {
//				return ResultBuilder.buildErrorResponse(ErrorCode.ERROR_EXIST_USER);
//			}
		}

		Party party = new Party();
		party.setAppId(dto.getAppId());
		party.setPartyTypeId(PartyType.PPERSON.getKey());
		partyDao.save(party);

		PartyPerson person = new PartyPerson();
		person.setPersonName(dto.getName());
		person.setIdCard(dto.getIdcard());
		partyPersonDao.save(person);

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

		return ResultBuilder.normalResult(party.getPartyId());
	}

	@Override
	@Transactional
	public RstResult<String> updatePerson(PersonUpdateDTO dto) {
		if (StringUtils.isBlank(dto.getPersonId())) {
			return ResultBuilder.buildResult("40000003", "个人标识不能为空");
		}
		Optional<PartyPerson> optional = partyPersonDao.findById(dto.getPersonId());
		if (!optional.isPresent()) {
			return ResultBuilder.buildResult("40000002", "个人不存在");
		}
		
//		PartyPerson existing = optional.get();
//		if (person.getCode() != null) {
//			existing.setCode(person.getCode());
//		}
//		if (person.getPersonName() != null) {
//			existing.setPersonName(person.getPersonName());
//		}
//		if (person.getFirstName() != null) {
//			existing.setFirstName(person.getFirstName());
//		}
//		if (person.getLastName() != null) {
//			existing.setLastName(person.getLastName());
//		}
//		if (person.getSex() != '\0') {
//			existing.setSex(person.getSex());
//		}
//		if (person.getBirthday() != null) {
//			existing.setBirthday(person.getBirthday());
//		}
//		if (person.getIconId() != null) {
//			existing.setIconId(person.getIconId());
//		}
//		if (person.getIdCard() != null) {
//			existing.setIdCard(person.getIdCard());
//		}
//		partyPersonRepository.save(existing);
		
		/* 1、update base info */
		// personBusiness.updatePerson(xxx);

		/*2、update contact info*/
		// contactBusiness.updateContact(xxx);

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
		PersonVO vo = new PersonVO();
		vo.setRealname(optional.get().getPersonName());
		return ResultBuilder.normalResult(vo);
	}

	@Override
	public PersonVO getPersonBySfz(String idcard) {
		return null;
	}

	@Override
	public PersonVO getPersonByCode(String code) {
		return null;
	}

	@Override
	public PageResult<DepartmentVO> listDepartment(DepartmentListDTO dto) {
//		if (StringUtils.isBlank(dto.getCompanyId())) {
//			return ResultBuilder.buildResult(ErrorCode.PARAMS_EMPTY);
//		}
		return partyBusiness.listDepartment(dto);
//		return ResultBuilder.buildResult(hy101Repository.findAll().stream().map(mapper -> {
//			DepartmentVO vo = new DepartmentVO();
//			vo.setCode(mapper.getGroupCode());
//			vo.setName(mapper.getGroupName());
////			BeanUtils.copyProperties(mapper, vo);
//			return vo;
//		}).collect(Collectors.toList()));
	}

	@Override
	@Transactional
	public RstResult<String> createPartyGroup(PartyGroupDTO dto) {
		if (StringUtils.isEmpty(dto.getAppId())) {
			return ResultBuilder.buildResult(ErrorCode.PARAMS_APPID);
		}
		if (dto.getName() == null || dto.getName().isEmpty()) {
			return ResultBuilder.buildResult("40000001", "团体名称不能为空");
		}
		if (dto.getRoleType() == null) {
			return ResultBuilder.buildResult("40000001", "团体类型不能为空");
		}
		PartyGroup object = partyGroupRepository.findByAppIdAndGroupName(dto.getAppId(), dto.getName());
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
		pg.setGroupName(dto.getName());
		pg.setStatusId(StatusEnum.ENABLE.name());
		pg.setPartyTypeId(PartyType.PGROUP.getKey());
		pg.setRoleTypeId(dto.getRoleType().name());
		partyGroupRepository.save(pg);
		logger.info("--->[{}]团体信息创建完成 ...", pg.getGroupName());
		
		/* 保存会员组关系 */
		if (StringUtils.isNotBlank(dto.getParentId())) {
			String roleIdFrom = RoleTypeEnum.ROLE_COMPANY.getKey();
			String roleIdTo = dto.getRoleType().name();
			String relationType=PartyRelationType.PARENT_CHILD.getKey();
			partyBusiness.createRelation(dto.getParentId(), roleIdFrom, pg.getPartyId(), roleIdTo, relationType);
			logger.info("--->[{}]团体关系创建完成 ...", pg.getGroupName());
		}
		
		/* 返回结果 */
		RstResult<String> result = ResultBuilder.normalResult();
		result.setData(pg.getPartyId());
		return result;
	}

//	@Override
//	public List<Supplier> listSupplier() {
//		return null;
//	}

	@Override
	public RstResult<List<PartyGroupDTO>> listPartyGroup() {
//		return ResultBuilder.buildResult(hy101Repository.findAll().stream().map(mapper -> {
//			PartyGroupDTO vo = new PartyGroupDTO();
//			BeanUtils.copyProperties(mapper, vo);
//			return vo;
//		}).collect(Collectors.toList()));
		return null;
	}

	@Override
	public Map<String, String> getPerson(String partyId, Set<String> filed) {
		return null;
	}

	@Override
	public List<Supplier> listSupplier() {
		// TODO Auto-generated method stub
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
