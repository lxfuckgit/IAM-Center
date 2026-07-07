package com.iamcenter.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.javapai.framework.action.PageResult;
import com.javapai.framework.action.ResultBuilder;
import com.javapai.framework.action.RstResult;
import com.saasapi.contract.security.SecurityContract;
import com.saasapi.contract.security.dto.AddRolePrivilegeDTO;
import com.saasapi.contract.security.dto.LoginRoleDTO;
import com.saasapi.contract.security.dto.ResourceCreateDTO;
import com.saasapi.contract.security.dto.ResourceDeleteDTO;
import com.saasapi.contract.security.dto.ResourceListDTO;
import com.saasapi.contract.security.dto.ResourceUpdateDTO;
import com.saasapi.contract.security.dto.RoleDTO;
import com.saasapi.contract.security.dto.RoleDeleteDTO;
import com.saasapi.contract.security.dto.RoleListDTO;
import com.saasapi.contract.security.dto.RoleUpdateDTO;
import com.saasapi.contract.security.vo.LoginVO;
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
	
	@RequestMapping("addRole.php")
	public RstResult<String> addRole(@RequestBody RoleDTO dto) {
		return securityService.addRole(dto);
	}
	
	@RequestMapping("deleteRole.php")
	public RstResult<String> deleteRole(@RequestBody RoleDeleteDTO dto) {
		return securityService.deleteRole(dto);
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
//	
//	@RequestMapping("listPrivilegeTree.php")
//	public RstResult<List<SysPrivilege>> listPrivilegeTree() {
//		SecurityService ss = new SecurityService();
//		return RstResultBuilder.buildResult(ss.listPrivilege());
//	}
	
	@PostMapping("addRolePrivilege.php")
	public RstResult<Boolean> addRolePrivilege(@RequestBody AddRolePrivilegeDTO dto) {
		return securityService.addRolePrivilege(dto.getRoleId(), dto.getPrivilegeId());
	}
	
	@PostMapping("removeRolePrivilege.php")
	public RstResult<Boolean> removeRolePrivilege(@RequestBody AddRolePrivilegeDTO dto) {
		return securityService.removeRolePrivilege(dto.getRoleId(), dto.getPrivilegeId());
	}
	
	@RequestMapping("listLogin.php")
	public RstResult<List<LoginVO>> listLogin() {
		return securityService.listLogin();
	}

}
