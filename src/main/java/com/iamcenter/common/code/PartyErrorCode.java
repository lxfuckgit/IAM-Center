package com.iamcenter.common.code;

import com.javapai.framework.enums.Enums;

public enum PartyErrorCode implements Enums<String, String> {
	PARAMS_NO_COMPANY("10000001", "参数（公司标识）不能为空!"),;

	private String code;
	private String message;

	private PartyErrorCode(String code, String message) {
		this.code = code;
		this.message = message;
	}

	@Override
	public String getKey() {
		return code;
	}

	@Override
	public String getValue() {
		return message;
	}

}
