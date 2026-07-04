package com.iamcenter.controller;

import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.javapai.framework.action.RstResult;
import com.saasapi.contract.security.AuthContract;
import com.saasapi.contract.security.dto.LogoutDTO;
import com.saasapi.contract.security.dto.PwdLoginDTO;
import com.saasapi.contract.security.dto.RegPwdDTO;
import com.saasapi.contract.security.dto.RegSmsDTO;
import com.saasapi.contract.security.dto.SmsCaptchaDTO;
import com.saasapi.contract.security.vo.LoginVO;

import jakarta.servlet.http.HttpServletRequest;

/**
 * 用户【认证】控制器。<br>
 */
@RestController
@RequestMapping("/iam")
public class AuthenticationController {
	@DubboReference(timeout = 5000, check = false)
	private AuthContract authContract;

	@PostMapping(value = "/sendRegSms.php")
	public RstResult<String> sendRegSms(@RequestBody SmsCaptchaDTO dto) {
		return authContract.sendRegSms(dto.getPhone());
	}

	@PostMapping(value = "/sendLoginSms.php")
	public RstResult<String> sendLoginSms(@RequestBody SmsCaptchaDTO dto) {
		return authContract.sendLoginSms(dto.getPhone());
	}

	@RequestMapping(value = "/pwdRegister.php")
	public RstResult<String> pwdRegister(@RequestBody RegPwdDTO param) {
		return authContract.register(param);
	}

	@RequestMapping(value = "/auth/smsRegister.php")
	public RstResult<String> smsRegister(@RequestBody RegSmsDTO param) {
		return authContract.register(param);
	}

	@PostMapping(value = "/pwdLogin.php")
	public RstResult<LoginVO> pwdLogin(@RequestBody PwdLoginDTO param) {
		return authContract.userLogin(param);
	}

	@RequestMapping(value = "/auth/logout.php")
	public RstResult<String> logout(@RequestParam String token, HttpServletRequest requst) {
		if (StringUtils.isBlank(token)) {
			// 第二优先级：取请求头里的token
			token = requst.getHeader("Authorization");
		}
		return authContract.logout(new LogoutDTO(token));
	}

}
