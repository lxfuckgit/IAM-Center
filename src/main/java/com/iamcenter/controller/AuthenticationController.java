package com.iamcenter.controller;

import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.javapai.framework.action.RstResult;
import com.saasapi.contract.security.AuthContract;
import com.saasapi.contract.security.dto.RegPwdDTO;

/**
 * 用户【认证】控制器。<br>
 */
@RestController
@RequestMapping("/iam")
public class AuthenticationController {
	@DubboReference(timeout = 5000, check = false)
	private AuthContract authContract;

	@RequestMapping(value = "/register.php")
	public RstResult<String> register(@RequestBody RegPwdDTO param) {
		return authContract.register(param);
	}

}
