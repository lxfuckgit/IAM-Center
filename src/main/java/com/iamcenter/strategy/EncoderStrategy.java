package com.iamcenter.strategy;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * 编码器策略。<br>
 * 
 * <strong>提示：</strong>先简单写一个，后面修改为真正的策略模式。
 */
@Component
public class EncoderStrategy {
	public final static String MD5 = "md5";
	public final static String BCrypt = "bcrypt";

	@Autowired
	PasswordEncoder passwordEncoder;

	/**
	 * 加密原始密码
	 * 
	 * @param encoderType 加密码类型。
	 * @param rawPassword 原始密码。
	 * @return
	 */
	public String encoderPassword(String encoderType, String rawPassword) {
		if ("bcrypt".equals(encoderType)) {
			// 采用spring-security框架的算法
			return bcryptEncoderPassword(rawPassword);
		} else {
			// 采用自定义md5算法（其实spring-security也有md5算法）
			return md5EncoderPassword(rawPassword);
		}
	}

	private String bcryptEncoderPassword(String rawPassword) {
		return passwordEncoder.encode(rawPassword);
	}

	private String md5EncoderPassword(String rawPassword) {
		return DigestUtils.md5Hex(rawPassword);
	}

}
