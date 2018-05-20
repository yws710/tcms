package com.yws.tcms.web;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.yws.tcms.constant.GlobalConstant;
import com.yws.tcms.domain.Role;
import com.yws.tcms.domain.User;
import com.yws.tcms.service.RoleService;
import com.yws.tcms.service.UserService;
import com.yws.tcms.web.vo.PageVO;
import com.yws.tcms.web.vo.ResultJson;
import com.yws.tcms.web.vo.SessionInfo;

/**
 * 员工控制器类。<br>
 * 这里只应该有视图层对象，而不应该出现实体层对象。
 * @author Administrator
 *
 */
@Controller
@RequestMapping("/user")
public class UserController {
	
	@Autowired
	UserService userService;
	
	@Autowired
	RoleService roleService;
	
	@ResponseBody
	@GetMapping(value="/test1", produces="application/json;charset=UTF-8")
	public String test1() throws Exception {
		return GlobalConstant.SUCCESS;
	}
	
	/**
	 * 指向员工管理页面<br>
	 * 只有HRBP有权访问
	 * @return
	 * @throws Exception
	 */
	@RequiresPermissions(value="user:query")
	@RequestMapping("/list")
	public ModelAndView list() throws Exception {
		ModelAndView mv = new ModelAndView();
		// viewName不要以斜杠开头，否则在部署为jar包时会报错。
		mv.setViewName("user/list");
		return mv;
	}
	
	/**
	 * 员工列表分页查询，根据员工姓名查找
	 * @param name 员工姓名
	 * @param pageNum 当前页码，设置默认值为1
	 * @param pageSize 每页显示多少条记录，设置默认值为10
	 * @return
	 * @throws Exception
	 */
	@RequiresPermissions(value="user:query")
	@ResponseBody
	@GetMapping(value="/listData", produces="application/json;charset=UTF-8")
	public PageVO<User> userPage(User user, @RequestParam(defaultValue="1")int page, @RequestParam(defaultValue="10")int limit) throws Exception{
		// 前端页面使用了layui的table.reload，提交的中文需要转码
		//name = new String(name.getBytes("iso-8859-1"),"utf-8");
		Page<User> usersPage = userService.search(user, page, limit);
		
		PageVO<User> pagevo = new PageVO<User>();
		pagevo.setCount(usersPage.getTotalElements());
		List<User> users = usersPage.getContent();
		// 判断有没有查看薪资的权限，只有Manager角色才能查看
		Subject subject = SecurityUtils.getSubject();
		if(!subject.hasRole("Manager")) {
			for(User u:users){
				u.setSalary(null);
			}
		}
		pagevo.setData(users);
		
		return pagevo;
	}
	
	/**
	 * 所有员工列表（只有id,name,empNo这三个属性，给项目分配PM、QA时用到）
	 * @return
	 * @throws Exception
	 */
	@RequiresPermissions(value="user:query")
	@Cacheable(value="userListAllCache")
	@ResponseBody
	@GetMapping(value="/listAll", produces="application/json;charset=UTF-8")
	public ResultJson findAll() throws Exception {
		List<User> uservos = userService.listAll();
		ResultJson json = new ResultJson();
		json.setSuccess(true);
		json.setData(uservos);
		
		return json;
	}
	
	/**
	 * 指向个人信息页面
	 * 只有HRBP有权访问
	 * @return
	 * @throws Exception
	 */
	@RequiresAuthentication
	@RequestMapping("/personalInfoUI")
	public ModelAndView personalInfoUI() throws Exception {
		ModelAndView mv = new ModelAndView();
		mv.setViewName("user/personalInfo");
		return mv;
	}
	
	/**
	 * 回显个人信息
	 * @return
	 * @throws Exception
	 */
	@RequiresAuthentication
	@ResponseBody
	@PostMapping(value="/personalInfo", produces="application/json;charset=UTF-8")
	public ResultJson personalInfo(HttpSession session) throws Exception {
		ResultJson json = new ResultJson();
		SessionInfo sessionInfo = (SessionInfo) session.getAttribute(GlobalConstant.SESSION_INFO);
		if(sessionInfo != null){
			User user = userService.findById(sessionInfo.getId());
			json.setSuccess(true);
			json.setData(user);
		}
		
		return json;
	}
	
	/**
	 * 修改个人信息<br>
	 * 需要确定员工能修改自己的哪些信息
	 * @param user
	 * @return ModelAndView对象
	 */
	@RequiresAuthentication
	@ResponseBody
	@PostMapping(value="/editPersonalInfo", produces="application/json;charset=UTF-8")
	public String editPersonalInfo(HttpServletRequest request, User user, @RequestParam(required=false)Integer satrapId){
		// 从数据库中取出原数据
		User u = userService.findById(user.getId());
		if(u != null){
			if(satrapId != null && !"".equals(satrapId)){ // 如果上级id不为空，则设置上级
				User satrap = userService.findById(satrapId);
				user.setSatrap(satrap);
			}
			
			Enumeration<String> names = request.getParameterNames();
			while(names.hasMoreElements()){
				String name = names.nextElement();
				if(name.startsWith("role")){
					Integer roleId = Integer.valueOf(request.getParameter(name));
					Role role = roleService.findOne(roleId);
					if(role != null){
						user.getRoles().add(role);
					}
				}
			}
			user.setRoles(u.getRoles()); // 自己不能修改自己的角色，该怎么实现？
			userService.save(user);
		}
		
		
		return GlobalConstant.SUCCESS;
	}
	
	/**
	 * 上传文件，并读取文件内容（员工信息）存储到数据库中。
	 * @param file Excel文件，后缀名为xlsx
	 * @return
	 * @throws Exception
	 */
	@RequiresPermissions(value="user:add")
	@ResponseBody
	@RequestMapping(value="/import", produces="application/json;charset=UTF-8")
	public Map<String,Object> uploadFile(HttpServletRequest request, @RequestParam(value="file") MultipartFile file) throws Exception {
		String rootpath = request.getSession().getServletContext().getRealPath("/");
		userService.uploadFile(rootpath,file);
		Map<String,Object> result = new HashMap<String,Object>();
		result.put("code", 0);
		result.put("msg", GlobalConstant.SUCCESS);
		return result;
	}
	
	/**
	 * 向数据库中保存一个用户（新增或修改，当有id并且在数据库中存在时则为修改）
	 * @param user
	 * @return ModelAndView对象
	 */
	@RequiresPermissions(value="user:add")
	@ResponseBody
	@PostMapping(value="/save", produces="application/json;charset=UTF-8")
	public String saveUser(HttpServletRequest request, User user, @RequestParam(required=false)Integer satrapId){
		if(satrapId != null && !"".equals(satrapId)){ // 如果上级id不为空，则设置上级
			User satrap = userService.findById(satrapId);
			user.setSatrap(satrap);
		}
		
		Enumeration<String> names = request.getParameterNames();
		while(names.hasMoreElements()){
			String name = names.nextElement();
			if(name.startsWith("role")){
				Integer roleId = Integer.valueOf(request.getParameter(name));
				Role role = roleService.findOne(roleId);
				if(role != null){
					user.getRoles().add(role);
				}
			}
		}
		// 判断有没有查看薪资的权限，只有Manager角色才能查看
		Subject subject = SecurityUtils.getSubject();
		if(!subject.hasRole("Manager")) {
			User u = userService.findById(user.getId());
			if(u != null){
				// 修改，使用原始数据
				user.setSalary(u.getSalary());
			}
		}
		userService.save(user);
		
		return GlobalConstant.SUCCESS;
	}
    
	/**
	 * 导出数据<br>
	 * 在前端页面利用js导出为csv文件，这里只需要提供json格式的数据
	 * @param name 员工姓名，可为空，为空时查询所有
	 * @return
	 */
	@RequiresPermissions(value="user:add")
    @ResponseBody
    @PostMapping(value="/exportExcel", produces="application/json;charset=UTF-8")
	public ResultJson exportExcel(String name) {
    	List<User> list = userService.export2Excel(name);
    	// 判断有没有查看薪资的权限，只有Manager角色才能查看
		Subject subject = SecurityUtils.getSubject();
		if(!subject.hasRole("Manager")) {
			for(User user:list){
				user.setSalary(null);
			}
		}
    	ResultJson json = new ResultJson(true);
    	json.setData(list);
    	return json;
	}
    
	/**
	 * 删除给定id的项目
	 * @param id
	 * @return
	 * @throws Exception
	 */
	@RequiresPermissions(value="user:delete")
	@ResponseBody
	@DeleteMapping(value="/delete", produces="application/json;charset=UTF-8")
	public ResultJson del(Integer id) throws Exception {
		return userService.deleteById(id);
	}
}
