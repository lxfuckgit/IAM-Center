package com.iamcenter.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.iamcenter.domain.party.PartyGroup;
import com.iamcenter.repository.party.PartyGroupRepository;
import com.iamcenter.service.PartyService;
import com.javapai.framework.action.PageResult;
import com.javapai.framework.action.ResultBuilder;
import com.javapai.framework.action.RstResult;
import com.saasapi.contract.party.dto.CompanyListDTO;
import com.saasapi.contract.party.dto.DepartmentDTO;
import com.saasapi.contract.party.dto.DepartmentListDTO;
import com.saasapi.contract.party.dto.PartyGroupDTO;
import com.saasapi.contract.party.dto.PersonCreateDTO;
import com.saasapi.contract.party.dto.PersonListDTO;
import com.saasapi.contract.party.dto.PersonUpdateDTO;
import com.saasapi.contract.party.enums.RoleTypeEnum;
import com.saasapi.contract.party.vo.CompanyVO;
import com.saasapi.contract.party.vo.DepartmentVO;
import com.saasapi.contract.party.vo.PersonVO;

@RestController
@RequestMapping("/organization")
public class OrganizationController {

	@Autowired
	private PartyGroupRepository partyGroupRepository;

	@Autowired
	private PartyService partyService;
	
	@RequestMapping("/listCompany")
	public PageResult<CompanyVO> listCompany(@RequestBody CompanyListDTO dto) {
		return partyService.listCompany(dto);
	}
	
	@PostMapping("/createCompany")
	public RstResult<String> createCompany(@RequestBody PartyGroupDTO dto) {
		dto.setRoleType(RoleTypeEnum.ROLE_COMPANY);
		return partyService.createPartyGroup(dto);
	}
	
	@RequestMapping("/listDepartment")
	public PageResult<DepartmentVO> listDepartment(@RequestBody DepartmentListDTO dto) {
		return partyService.listDepartment(dto);
	}
	
	@PostMapping("/createDepartment")
	public RstResult<String> createDepartment(@RequestBody DepartmentDTO dto) {
		PartyGroupDTO request = new PartyGroupDTO();
		request.setRoleType(dto.getRoleType());
		request.setAppId(dto.getAppId());
		request.setName(dto.getName());
		request.setParentId(dto.getCompanyId());
		return partyService.createPartyGroup(request);
	}
	
	@GetMapping("/getGroup/{partyId}")
	public RstResult<PartyGroup> getGroup(@PathVariable String partyId) {
		Optional<PartyGroup> optional = partyGroupRepository.findById(partyId);
		if (optional.isPresent()) {
			return ResultBuilder.normalResult(optional.get());
		} else {
			return ResultBuilder.buildResult("40000002", "团体不存在");
		}
	}

	@GetMapping("/listGroup")
	public RstResult<List<PartyGroup>> listGroup() {
		return ResultBuilder.normalResult(partyGroupRepository.findAll());
	}

	@PutMapping("/updateGroup")
	public RstResult<String> updateGroup(@RequestBody PartyGroup group) {
		if (group.getPartyId() == null || group.getPartyId().isEmpty()) {
			return ResultBuilder.buildResult("40000003", "团体标识不能为空");
		}
		Optional<PartyGroup> optional = partyGroupRepository.findById(group.getPartyId());
		if (!optional.isPresent()) {
			return ResultBuilder.buildResult("40000002", "团体不存在");
		}
		PartyGroup existing = optional.get();
		if (group.getGroupName() != null) {
			existing.setGroupName(group.getGroupName());
		}
		if (group.getGroupCode() != null) {
			existing.setGroupCode(group.getGroupCode());
		}
		if (group.getLegalEntity() != null) {
			existing.setLegalEntity(group.getLegalEntity());
		}
		if (group.getFax() != null) {
			existing.setFax(group.getFax());
		}
		if (group.getEmail() != null) {
			existing.setEmail(group.getEmail());
		}
		if (group.getTelephone() != null) {
			existing.setTelephone(group.getTelephone());
		}
		if (group.getMobilePhone() != null) {
			existing.setMobilePhone(group.getMobilePhone());
		}
		if (group.getLicenseNo() != null) {
			existing.setLicenseNo(group.getLicenseNo());
		}
		if (group.getLicenseImage() != null) {
			existing.setLicenseImage(group.getLicenseImage());
		}
		if (group.getOrganizationCode() != null) {
			existing.setOrganizationCode(group.getOrganizationCode());
		}
		if (group.getOrganizationImage() != null) {
			existing.setOrganizationImage(group.getOrganizationImage());
		}
		if (group.getRegistrationNo() != null) {
			existing.setRegistrationNo(group.getRegistrationNo());
		}
		if (group.getRegistrationImage() != null) {
			existing.setRegistrationImage(group.getRegistrationImage());
		}
		if (group.getAuthStatusId() != null) {
			existing.setAuthStatusId(group.getAuthStatusId());
		}
		if (group.getWebsite() != null) {
			existing.setWebsite(group.getWebsite());
		}
		partyGroupRepository.save(existing);
		return ResultBuilder.normalResult();
	}

	@DeleteMapping("/deleteGroup/{partyId}")
	public RstResult<String> deleteGroup(@PathVariable String partyId) {
		if (!partyGroupRepository.existsById(partyId)) {
			return ResultBuilder.buildResult("40000002", "团体不存在");
		}
		partyGroupRepository.deleteById(partyId);
		return ResultBuilder.normalResult();
	}

	@PostMapping("/createPerson")
	public RstResult<String> createPerson(@RequestBody PersonCreateDTO dto) {
		return partyService.createPerson(dto);
	}

	@GetMapping("/getPerson/{partyId}")
	public RstResult<PersonVO> getPerson(@PathVariable String partyId) {
		return partyService.getPersonById(partyId);
	}

	@RequestMapping("/listPerson")
	public PageResult<PersonVO> listPerson(@RequestBody PersonListDTO dto) {
		return partyService.listPerson(dto);
	}

	@PutMapping("/updatePerson")
	public RstResult<String> updatePerson(@RequestBody PersonUpdateDTO dto) {
		return partyService.updatePerson(dto);
	}

//	@DeleteMapping("/deletePerson/{partyId}")
//	public RstResult<String> deletePerson(@PathVariable String partyId) {
//		if (!partyPersonRepository.existsById(partyId)) {
//			return ResultBuilder.buildResult("40000002", "个人不存在");
//		}
//		partyPersonRepository.deleteById(partyId);
//		return ResultBuilder.normalResult();
//	}

//	@PostMapping("/addRelation")
//	public RstResult<String> addRelation(@RequestBody com.saasapi.contract.party.dto.RelationDTO dto) {
//		return partyService.addRelation(dto.getPartyIdFrom(), dto.getPartyIdTo(), dto.getRelationTypeId());
//	}
//
//	@DeleteMapping("/deleteRelation/{partyIdFrom}/{partyIdTo}")
//	public RstResult<String> deleteRelation(@PathVariable String partyIdFrom, @PathVariable String partyIdTo) {
//		return partyService.deleteRelation(partyIdFrom, partyIdTo);
//	}
}
