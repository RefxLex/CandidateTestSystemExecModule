package com.reflex.request;

import jakarta.validation.constraints.NotBlank;

public class UnitTestRequest {
	
	@NotBlank
	private String code;

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

}
