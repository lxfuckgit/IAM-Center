package com.iamcenter.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.iamcenter.repository.SysLoginRoleDao;
import com.javapai.framework.action.PageResult;
import com.javapai.framework.action.ResultBuilder;
import com.javapai.framework.action.RstResult;
import com.javapai.framework.common.vo.tree.TreeVO;
import com.saasapi.contract.security.SecurityContract;
import com.saasapi.contract.security.dto.RoleResourceUpdateDTO;
import com.saasapi.contract.security.dto.LoginListDTO;
import com.saasapi.contract.security.dto.LoginRoleDTO;
import com.saasapi.contract.security.dto.LoginRoleUpdateDTO;
import com.saasapi.contract.security.dto.ResourceCreateDTO;
import com.saasapi.contract.security.dto.ResourceDeleteDTO;
import com.saasapi.contract.security.dto.ResourceListDTO;
import com.saasapi.contract.security.dto.ResourceUpdateDTO;
import com.saasapi.contract.security.dto.RoleDTO;
import com.saasapi.contract.security.dto.RoleDeleteDTO;
import com.saasapi.contract.security.dto.RoleListDTO;
import com.saasapi.contract.security.dto.RoleUpdateDTO;
import com.saasapi.contract.security.vo.LoginListVO;
import com.saasapi.contract.security.vo.ResourceVO;
import com.saasapi.contract.security.vo.RoleVO;

/**
 * 用户【授权】控制器。<br>
 */
@RestController
@RequestMapping("/iam")
public class AuthorizationController {

	@Autowired
	private SecurityContract securityService;
	
	@Autowired
	private SysLoginRoleDao sysLoginRoleDao;
	
	@RequestMapping("addRole.php")
	public RstResult<String> addRole(@RequestBody RoleDTO dto) {
		return securityService.addRole(dto);
	}
	
	@RequestMapping("deleteRole.php")
	public RstResult<String> deleteRole(@RequestBody RoleDeleteDTO dto) {
		return securityService.deleteRole(dto);
	}
	
	@RequestMapping("getRole.php")
	public RstResult<RoleVO> getRole(@RequestBody RoleDeleteDTO dto) {
		return securityService.getRole(dto);
	}
	
	@RequestMapping("updateRole.php")
	public RstResult<String> updateRole(@RequestBody RoleUpdateDTO dto) {
		return securityService.updateRole(dto);
	}
	
	@RequestMapping("listRole.php")
	public PageResult<RoleVO> listRole(@RequestBody RoleListDTO dto) {
		return securityService.listRole(dto);
	}

	@PostMapping(value = "/addLoginRole.php")
	public RstResult<Boolean> addLoginRole(@RequestBody LoginRoleDTO dto) {
		return securityService.addLoginRole(dto.getAppId(), dto.getLoginId(), dto.getRoleId());
	}

	@PostMapping(value = "/removeLoginRole.php")
	public RstResult<String> removeLoginRole(@RequestBody LoginRoleDTO dto) {
		securityService.removeLoginRole(dto.getAppId(), dto.getLoginId(), dto.getRoleId());
		return ResultBuilder.normalResult();
	}
	
	@PostMapping(value = "/createResource.php")
	public RstResult<String> addResource(@RequestBody ResourceCreateDTO dto) {
		return securityService.createResource(dto);
	}
	
	@PostMapping(value = "/deleteResource.php")
	public RstResult<String> deleteResource(@RequestBody ResourceDeleteDTO dto) {
		return securityService.deleteResource(dto);
	}
	
	@PostMapping(value = "/updateResource.php")
	public RstResult<String> updateResource(@RequestBody ResourceUpdateDTO dto) {
		return securityService.updateResource(dto);
	}
	
	@RequestMapping("/listResource.php")
	public PageResult<ResourceVO> listResource(@RequestBody ResourceListDTO dto) {
		return securityService.listResource(dto);
	}
	
	@RequestMapping("/treeResource.php")
	public RstResult<List<TreeVO>> treePrivilege(@RequestBody ResourceListDTO dto) {
		return securityService.treeResource(dto);
	}
	
	@PostMapping("/listRoleResourceIds.php")
	public RstResult<List<Long>> listRoleResourceIds(@RequestBody RoleResourceUpdateDTO dto) {
		return securityService.listRoleResourceIds(dto.getAppId(), dto.getRoleId());
	}
	
	@PostMapping("/restoreRoleResourceList.php")
	public RstResult<Boolean> restoreRoleResourceList(@RequestBody RoleResourceUpdateDTO dto) {
		return securityService.restoreRoleResources(dto.getAppId(), dto.getRoleId(),dto.getResourceIdList());
	}
	
	@PostMapping("grantRolePrivilege.php")
	public RstResult<Boolean> grantRolePrivilege(@RequestBody RoleResourceUpdateDTO dto) {
		return securityService.grantRolePrivilege(dto.getAppId(), dto.getRoleId(), dto.getResourceIdList());
	}

	@PostMapping("revokeRolePrivilege.php")
	public RstResult<Boolean> revokeRolePrivilege(@RequestBody RoleResourceUpdateDTO dto) {
		return securityService.revokeRolePrivilege(dto.getAppId(), dto.getRoleId(), dto.getResourceIdList());
	}
	
	@RequestMapping("listLogin.php")
	public PageResult<LoginListVO> listLogin(@RequestBody LoginListDTO dto) {
		return securityService.listLogin(dto);
	}
	
	@PostMapping("/listLoginRoleIds.php")
	public RstResult<List<Long>> listLoginRoleIds(@RequestBody LoginRoleUpdateDTO dto) {
		List<Long> roleIdList = sysLoginRoleDao.findByLoginId(dto.getLoginId()).stream().map(mapper -> {
			return mapper.getRoleId();
		}).collect(Collectors.toList());
		return ResultBuilder.normalResult(roleIdList);
	}
	
	@PostMapping("/restoreLoginRoles.php")
	public RstResult<Boolean> restoreLoginRoles(@RequestBody LoginRoleUpdateDTO dto) {
		return securityService.restoreLoginRoles(dto.getAppId(), dto.getLoginId(), dto.getRoleIdList());
	}

}
