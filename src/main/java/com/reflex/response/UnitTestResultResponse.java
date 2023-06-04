package com.reflex.response;

public class UnitTestResultResponse {
	
	private String report;
	
	private boolean compileIsOk;
	
	public UnitTestResultResponse() {

	}
	
	public UnitTestResultResponse(String report, boolean compileIsOk) {
		this.report = report;
		this.compileIsOk = compileIsOk;
	}

	public String getResult() {
		return report;
	}

	public void setResult(String result) {
		this.report = result;
	}

	public boolean isCompileIsOk() {
		return compileIsOk;
	}

	public void setCompileIsOk(boolean compileIsOk) {
		this.compileIsOk = compileIsOk;
	}
	
}
