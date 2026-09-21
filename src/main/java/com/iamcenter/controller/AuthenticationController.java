package com.iamcenter.controller;

import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.javapai.framework.action.RstResult;
import com.saasapi.contract.security.AuthContract;
import com.saasapi.contract.security.dto.ChangePwdDTO;
import com.saasapi.contract.security.dto.LogoutDTO;
import com.saasapi.contract.security.dto.PwdLoginDTO;
import com.saasapi.contract.security.dto.RegPwdDTO;
import com.saasapi.contract.security.dto.RegSmsDTO;
import com.saasapi.contract.security.dto.ResetPwdDTO;
import com.saasapi.contract.security.dto.SmsCaptchaDTO;
import com.saasapi.contract.security.vo.LoginVO;

import jakarta.servlet.http.HttpServletRequest;

/**
 * 用户【认证】控制器。<br>
 */
@RestController
@RequestMapping("/iam")
//@RequestMapping("/iam/auth")
public class AuthenticationController {
	@DubboReference(timeout = 5000, check = false)
	private AuthContract authContract;

	@PostMapping(value = "/sendRegSms")
	public RstResult<String> sendRegSms(@RequestBody SmsCaptchaDTO dto) {
		return authContract.sendRegSms(dto.getPhone());
	}

	@PostMapping(value = "/sendLoginSms")
	public RstResult<String> sendLoginSms(@RequestBody SmsCaptchaDTO dto) {
		return authContract.sendLoginSms(dto.getPhone());
	}

	@RequestMapping(value = "/pwdRegister")
	public RstResult<String> pwdRegister(@RequestBody RegPwdDTO param) {
		return authContract.register(param);
	}

	@RequestMapping(value = "/smsRegister")
	public RstResult<String> smsRegister(@RequestBody RegSmsDTO param) {
		return authContract.register(param);
	}

	@PostMapping(value = "/pwdLogin")
	public RstResult<LoginVO> pwdLogin(@RequestBody PwdLoginDTO param) {
		return authContract.userLogin(param);
	}

	@RequestMapping(value = "/logout")
	public RstResult<String> logout(HttpServletRequest requst) {
		String token = requst.getHeader("Authorization");
		return authContract.logout(new LogoutDTO(token));
	}
	
	@RequestMapping(value = "/resetPassword")
	public RstResult<String> resetPassword(@RequestBody ResetPwdDTO dto) {
		return authContract.resetPassword(dto);
	}
	
	@RequestMapping(value = "/changePassword")
	public RstResult<String> changePassword(@RequestBody ChangePwdDTO dto) {
		return authContract.changePassword(dto);
	}
	
//	@RequestMapping(value = "/auth/validateToken")
//	public RstResult<String> validateToken(@RequestBody ValidateTokenDTO dto) {
//		return authContract.validateToken(dto);
//	}
//	
//	@RequestMapping(value = "/auth/refreshToken")
//	public RstResult<String> refreshToken(@RequestBody ValidateTokenDTO dto) {
//		return authContract.refreshToken(dto);
//	}

}
