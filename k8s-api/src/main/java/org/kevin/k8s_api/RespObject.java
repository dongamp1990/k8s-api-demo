package org.kevin.k8s_api;

public class RespObject {
	private Object result;
	private int code;
	
	public RespObject(Object res) {
		this.code = 0;
		this.result = res;
	}
	
	public RespObject(Object res, int code) {
		this.code = code;
		this.result = res;
	}

	public Object getResult() {
		return result;
	}

	public void setResult(Object result) {
		this.result = result;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}
}
