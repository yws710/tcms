package com.yws.tcms.web.vo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 用于封装前端框架layui的数据表格信息
 * @author Administrator
 *
 * @param <T>
 */
public class PageVO<T> implements Serializable {

	private static final long serialVersionUID = -52039198446886933L;
	
	private Integer code = 0;
	private String msg = "";
	private Long count = 0L;
	private List<T> data = new ArrayList<T>();
	
	public Integer getCode() {
		return code;
	}
	public void setCode(Integer code) {
		this.code = code;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public Long getCount() {
		return count;
	}
	public void setCount(Long count) {
		this.count = count;
	}
	public List<T> getData() {
		return data;
	}
	public void setData(List<T> data) {
		this.data = data;
	}
}
