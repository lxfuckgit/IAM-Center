package com.iamcenter.common.code;

import com.javapai.framework.enums.Enums;

public enum PartyErrorCode implements Enums<String, String> {
	PARAMS_NO_COMPANY("10200001", "参数（公司标识）不能为空!"), 
	ERROR_NO_PARTY("10020001", "业务错误：目标会员不存在!"),
	ERROR_HAS_CHILD("10020011", "业务错误：目标数据存在子节点!"),;

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
