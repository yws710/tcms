package com.yws.tcms.web;

import java.util.List;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.yws.tcms.constant.GlobalConstant;
import com.yws.tcms.domain.User;
import com.yws.tcms.service.UserService;
import com.yws.tcms.web.vo.LoginForm;
import com.yws.tcms.web.vo.ResultJson;
import com.yws.tcms.web.vo.SessionInfo;

/**
 * 与登录相关的控制器类
 * @author Administrator
 *
 */
@Controller
public class LoginController {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(LoginController.class);
	
	@Autowired
	private UserService userService;
	
	/**
	 * 如果未登录，则跳转到登录页面；否则跳转到首页。
	 * @return
	 */
    @RequestMapping(value={"/login","/index"})
    public ModelAndView login() {
    	ModelAndView mv = new ModelAndView();
    	Subject subject = SecurityUtils.getSubject();
    	if(subject.isAuthenticated()){ // 如果已经登录
        	mv.setViewName("index");
        }else {
        	mv.setViewName("login");
        }
    	
        return mv;
    }
    
    /**
     * 登录<br>
     * @param session
     * @param loginForm 表单对象，封装了username和password
     * @param errors 封装了表单验证失败的信息
     * @return 返回ResultJson对象的json数据
     */
    @ResponseBody
    @PostMapping(value="/loginUser", produces="application/json;charset=UTF-8")
    public ResultJson loginUser(HttpSession session, @Valid LoginForm loginForm, Errors errors) {
    	ResultJson result = new ResultJson();
    	
    	if (errors.hasErrors()) { // 表单验证不通过
			List<FieldError> fieldErrors = errors.getFieldErrors();
			String msg = "";
			for(FieldError fe:fieldErrors){
				msg += fe.getDefaultMessage() + "<br>";
			}
			result.setMsg(msg);
		}else {
			UsernamePasswordToken token = new UsernamePasswordToken(loginForm.getUsername(),loginForm.getPassword());
	        Subject subject = SecurityUtils.getSubject();
	        try {
	            subject.login(token); // 完成登录
	            String principal = (String) subject.getPrincipal(); // 自定义Realm中的doGetAuthenticationInfo方法返回值的第一个参数。
	            User user = userService.findByUsername(principal);
	            SessionInfo sessionInfo = new SessionInfo();
	            sessionInfo.setId(user.getId());
	            sessionInfo.setAccount(user.getEmpNo());
	            sessionInfo.setName(user.getName());
	            
	            session.setAttribute(GlobalConstant.SESSION_INFO, sessionInfo);
	            result.setSuccess(true);
	        } catch(Exception e) {
	        	LOGGER.error("登录失败", e);
	        	result.setMsg("登录失败");
	        }
		}
        
        return result;
    }
    
    /**
     * 登出
     * @return
     */
    @RequestMapping("/logout")
    public String logOut() throws Exception {
        Subject subject = SecurityUtils.getSubject();
        subject.logout();
        return "redirect:login"; // 重定向到登录页面
    }
    
	/**
	 * 功能描述：修改用户登录密码
	 * @param id 员工id
	 * @param oldPwd 旧密码
	 * @param newPwd 新密码
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@PostMapping(value="/editPwd", produces="application/json;charset=UTF-8")
	public ResultJson editPwd(Integer id, String oldPwd, String newPwd) throws Exception {
		ResultJson json = userService.editPwd(id,oldPwd,newPwd);
		if(json.isSuccess()){// 退出登录
			Subject subject = SecurityUtils.getSubject();
	        subject.logout();
		}
		
		return json;
	}
}
