package com.iamcenter.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.javapai.framework.action.ResultBuilder;
import com.javapai.framework.action.RstResult;
import com.saasapi.contract.security.SecurityContract;
import com.saasapi.contract.security.dto.AddRolePrivilegeDTO;
import com.saasapi.contract.security.dto.LoginRoleDTO;
import com.saasapi.contract.security.dto.RoleDTO;
import com.saasapi.contract.security.vo.LoginVO;
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
	public RstResult<String> addRole(@RequestBody RoleDTO role) {
		return securityService.addRole(role);
	}
	
//	@RequestMapping("deleteRole.php")
//	public RstResult<String> deleteRole(@RequestBody RoleDTO role) {
//		return securityService.deladdRole(role);
//	}
	
	@RequestMapping("listRole")
	public RstResult<List<RoleVO>> listRole() {
		return securityService.listRole();
	}

	@PostMapping(value = "/addLoginRole")
	public RstResult<Boolean> addLoginRole(@RequestBody LoginRoleDTO param) {
		return securityService.addLoginRole(param.getLoginId(), param.getRoleId());
	}

	@PostMapping(value = "/removeLoginRole")
	public RstResult<String> removeLoginRole(@RequestBody LoginRoleDTO param) {
		securityService.removeLoginRole(param.getLoginId(), param.getRoleId());
		return ResultBuilder.normalResult();
	}
	
//	@PostMapping(value = "/addPrivilege")
//	public RstResult<String> addPrivilege(@RequestBody SysPrivilege privilege) {
//		return securityService.addPrivilege(privilege.getPrivilegeCode(), privilege.getPrivilegeName());
//	}
//	
//	@RequestMapping("listPrivilege")
//	public RstResult<List<SysPrivilege>> listPrivilege() {
//		SecurityService ss = new SecurityService();
//		return RstResultBuilder.buildResult(securityService.l.listPrivilege());
//	}
//	
//	@RequestMapping("listPrivilegeTree")
//	public RstResult<List<SysPrivilege>> listPrivilegeTree() {
//		SecurityService ss = new SecurityService();
//		return RstResultBuilder.buildResult(ss.listPrivilege());
//	}
	
	@PostMapping("addRolePrivilege")
	public RstResult<Boolean> addRolePrivilege(@RequestBody AddRolePrivilegeDTO dto) {
		return securityService.addRolePrivilege(dto.getRoleId(), dto.getPrivilegeId());
	}
	
	@PostMapping("removeRolePrivilege")
	public RstResult<Boolean> removeRolePrivilege(@RequestBody AddRolePrivilegeDTO dto) {
		return securityService.removeRolePrivilege(dto.getRoleId(), dto.getPrivilegeId());
	}
	
	
	@RequestMapping("listLogin")
	public RstResult<List<LoginVO>> listLogin() {
		return securityService.listLogin();
	}



}
