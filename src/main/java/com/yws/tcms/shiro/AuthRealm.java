package com.yws.tcms.shiro;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;

import com.yws.tcms.domain.Permission;
import com.yws.tcms.domain.Role;
import com.yws.tcms.domain.User;
import com.yws.tcms.service.UserService;

/**
 * 自定义Realm类
 * 
 * @author Administrator
 *
 */
public class AuthRealm extends AuthorizingRealm {
	
	@Autowired
    private UserService userService;
    
    // 认证（登录）
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        UsernamePasswordToken utoken = (UsernamePasswordToken) token;//获取用户输入的token
        String username = utoken.getUsername();
        User user = userService.findByUsername(username);
        if(user != null){
        	return new SimpleAuthenticationInfo(
            		user.getEmpNo(), // 建议用登录名，而不是user对象（那些额外的、无用的信息可能会成为负资产）。
            		user.getPassword(),
            		this.getClass().getName()
            	);//放入shiro.调用CredentialsMatcher检验密码
        }else{
        	return null;
        }
        
    }
    
    // 授权（在subject.login()方法登录成功之后执行）
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        String username = (String) principals.getPrimaryPrincipal();
        User user = userService.findByUsername(username);
        List<String> permissions = new ArrayList<String>();
        List<String> roleNames = new ArrayList<String>();
        Set<Role> roles = user.getRoles();
        
        if(roles.size()>0) {
            for(Role role : roles) {
            	roleNames.add(role.getName());
                Set<Permission> modules = role.getPermissions();
                if(modules.size()>0) {
                    for(Permission module : modules) {
                        permissions.add(module.getName());
                    }
                }
            }
        }
        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
        info.addRoles(roleNames);
        info.addStringPermissions(permissions);//将权限放入shiro中
        
        return info;
    }

}
