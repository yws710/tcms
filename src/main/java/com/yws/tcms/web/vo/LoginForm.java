package com.yws.tcms.web.vo;

import java.io.Serializable;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import javax.validation.constraints.NotEmpty;

/**
 * 登录表单信息
 * @author Administrator
 *
 */
public class LoginForm implements Serializable {
	
	private static final long serialVersionUID = 620511791140654995L;

	@NotNull(message="账号不能为null")
	@NotEmpty(message="账号不能为空")
	@Pattern(regexp="^\\d{5}$", message="账号只能为5位整数")
	private String username; // 账号
	
	@NotNull(message="密码不能为null")
	@NotEmpty(message="密码不能为空")
	@Size(min=6, max=16, message="密码只能为6到16位字符")
	private String password; // 密码
	
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
}
