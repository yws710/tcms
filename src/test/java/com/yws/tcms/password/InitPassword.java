package com.yws.tcms.password;

import org.apache.shiro.authc.credential.DefaultPasswordService;
import org.apache.shiro.authc.credential.PasswordService;
import org.junit.Test;

public class InitPassword {

	@Test
	public void init() {
		PasswordService service = new DefaultPasswordService();
		// 加密新密码
		String npwd = service.encryptPassword("123456");
		System.out.println(npwd);
	}
}
