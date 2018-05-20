package com.yws.tcms.web;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.yws.tcms.domain.Permission;
import com.yws.tcms.domain.Role;
import com.yws.tcms.service.PermissionService;
import com.yws.tcms.service.RoleService;
import com.yws.tcms.web.vo.ResultJson;

/**
 * 角色控制器类。<br>
 * @author Administrator
 *
 */
@Controller
@RequestMapping("/role")
public class RoleController {
	
	@Autowired
	RoleService roleService;
	
	@Autowired
	PermissionService privilegeService;
	
	/**
	 * 角色列表页面<br>
	 * 要求具备管理员角色才能访问
	 * @return 返回一个ModelAndView对象
	 * @throws Exception
	 */
	@RequiresPermissions(value="role:query")
	@RequestMapping(value="/list")
	public ModelAndView listUI() throws Exception {
		ModelAndView mv = new ModelAndView();
		mv.setViewName("role/list");
		
		return mv;
	}
	
	/**
	 * 角色列表数据<br>
	 * 角色列表页面、添加和修改员工信息的时候用到
	 * @return 返回一个JSON字符串格式的Json对象
	 * @throws Exception
	 */
	@RequiresPermissions(value="role:query")
	@ResponseBody
	@RequestMapping(value="/all_ajax_data", method= {RequestMethod.GET, RequestMethod.POST}, produces="application/json;charset=UTF-8")
	public ResultJson findAll() throws Exception {
		List<Role> roles = roleService.findAll();
		ResultJson json = new ResultJson();
		json.setSuccess(true);
		json.setData(roles);
		
		return json;
	}
	
	/**
	 * 保存给定的角色<br>
	 * 要求具备管理员角色才能访问
	 * @param role
	 * @return 返回一个JSON字符串格式的Json对象
	 * @throws Exception
	 */
	@RequiresPermissions(value="role:add")
	@ResponseBody
	@PostMapping(value="/save", produces="application/json;charset=UTF-8")
	public ResultJson save(Role role) throws Exception {
		roleService.save(role);
		
		return new ResultJson(true);
	}
	
	/**
	 * 删除指定id的角色
	 * @param id
	 * @return 返回一个JSON字符串格式的Json对象
	 * @throws Exception
	 */
	@RequiresPermissions(value="role:delete")
	@ResponseBody
	@PostMapping(value="/del", produces="application/json;charset=UTF-8")
	public ResultJson del(Integer id) throws Exception {
		ResultJson json = new ResultJson();
		if(id == 1){
			json.setMsg("不能删除系统管理员！");
		}else{
			roleService.delete(id);
			json.setSuccess(true);
		}
		
		
		return json;
	}
	
	/**
	 * 功能描述：根据给定id的角色跳转到授权页面
	 * @param id
	 * @return
	 * @throws Exception
	 */
	@RequiresPermissions(value="role:add")
	@RequestMapping("setPrivilegeUI")
	public ModelAndView setPrivilegeUI(Integer id) throws Exception {
		
		ModelAndView mv = new ModelAndView();
		// 得到要操作的角色
		Role role = roleService.findOne(id);
		mv.addObject("role", role);
		List<Integer> permIds = new ArrayList<Integer>();
		Set<Permission> permissions = role.getPermissions();
		for(Permission p:permissions){
			permIds.add(p.getId());
		}
		mv.addObject("permIds", permIds);
		
		// 得到所有顶级权限
		List<Permission> topPrivilegeList = privilegeService.findAll();
		mv.addObject("topPrivilegeList", topPrivilegeList);
		
		mv.setViewName("role/setPrivilegeUI");
		
		return mv;
	}
	
	/**
	 * 功能描述：为给定id的角色设置权限
	 * @param id
	 * @param privilegeIds
	 * @return
	 * @throws Exception
	 */
	@RequiresPermissions(value="role:add")
	@RequestMapping("setPrivilege")
	public ModelAndView setPrivilege(Integer id, Integer[] privilegeIds) throws Exception {
		roleService.setPrivilege(id, privilegeIds);
		ModelAndView mv = new ModelAndView();
		mv.setViewName("redirect:/role/list");
		
		return mv;
	}
}
