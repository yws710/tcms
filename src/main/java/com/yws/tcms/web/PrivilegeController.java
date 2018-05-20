package com.yws.tcms.web;

import java.util.List;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.yws.tcms.domain.Permission;
import com.yws.tcms.service.PermissionService;
import com.yws.tcms.web.vo.ResultJson;

/**
 * 权限资源控制器类。<br>
 * @author Administrator
 *
 */
@Controller
@RequestMapping("/privilege")
public class PrivilegeController {
	
	@Autowired
	PermissionService privilegeService;
	
	/**
	 * 权限列表页面
	 * @return 返回一个ModelAndView对象
	 * @throws Exception
	 */
	@RequiresPermissions(value="resource:query")
	@RequestMapping(value="/list")
	public ModelAndView listUI() throws Exception {
		ModelAndView mv = new ModelAndView();
		mv.setViewName("privilege/list");
		
		return mv;
	}
	
	/**
	 * 所有资源列表（用于添加和修改时的回显，选择上级）
	 * @return
	 * @throws Exception
	 */
	@RequiresPermissions(value="resource:query")
	@ResponseBody
	@PostMapping(value="/listAll", produces="application/json;charset=UTF-8")
	public ResultJson listAll() throws Exception {
		List<Permission> vos = privilegeService.findAll();
		ResultJson json = new ResultJson(true);
		json.setData(vos);
		
		return json;
	}
	
	/**
	 * 权限列表数据
	 * @return 返回一个JSON字符串格式的Json对象
	 * @throws Exception
	 */
	@RequiresPermissions(value="resource:query")
	@ResponseBody
	@PostMapping(value="/listData", produces="application/json;charset=UTF-8")
	public ResultJson listData(@RequestParam(defaultValue="1")int pageNum, @RequestParam(defaultValue="10")int pageSize) throws Exception {
		Page<Permission> page = privilegeService.findAll(pageNum-1, pageSize);
		ResultJson json = new ResultJson(true);
		json.setData(page);
		
		return json;
	}
	
	/**
	 * 保存给定的权限资源
	 * @param privilege
	 * @return 返回一个JSON字符串格式的Json对象
	 * @throws Exception
	 */
	@RequiresPermissions(value="resource:add")
	@ResponseBody
	@PostMapping(value="/save", produces="application/json;charset=UTF-8")
	public ResultJson save(Permission privilege) throws Exception {
		privilegeService.save(privilege);
		
		return new ResultJson(true);
	}
	
	/**
	 * 删除指定id的角色
	 * @param id
	 * @return 返回一个JSON字符串格式的Json对象
	 * @throws Exception
	 */
	@RequiresPermissions(value="resource:delete")
	@ResponseBody
	@PostMapping(value="/del", produces="application/json;charset=UTF-8")
	public ResultJson del(Integer id) throws Exception {
		return privilegeService.delete(id);
	}
	
}
