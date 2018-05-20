package com.yws.tcms.shiro;

import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authc.credential.DefaultPasswordService;
import org.apache.shiro.authc.credential.PasswordService;
import org.apache.shiro.authc.credential.SimpleCredentialsMatcher;

/**
 * 自定义凭证（密码）验证<br>
 * 
 * @author Administrator
 *
 */
public class CredentialsMatcher extends SimpleCredentialsMatcher{

	// 在subject.login()方法执行的过程中调用该方法。
    @Override
    public boolean doCredentialsMatch(AuthenticationToken token, AuthenticationInfo info) {
    	// 获得用户输入的密码（明文）
    	UsernamePasswordToken utoken = (UsernamePasswordToken) token;
    	String submittedPlaintext = new String(utoken.getPassword());
    	// 获得数据库中的密码（密文）
    	String encrypted = (String) info.getCredentials();
    	
    	// 进行密码的比对。
    	PasswordService passwordService = new DefaultPasswordService();
    	
    	return passwordService.passwordsMatch(submittedPlaintext, encrypted);
    }
    
}