package com.yws.tcms.web.vo;

import java.io.Serializable;

/**
 * 返回到页面的数据，封装为Json格式
 * @author Administrator
 *
 */
public class ResultJson implements Serializable {

	private static final long serialVersionUID = -6082372860629967333L;

	private boolean success = false; // 成功标识
	private String msg = ""; // 文本信息
	private Object data = null; // 数据对象
	
	public ResultJson(){}
	
	public ResultJson(boolean success){
		this.success = success;
	}
	
	public ResultJson(boolean success, Object obj){
		this.success = success;
		this.data = obj;
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}

}
