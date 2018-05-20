package com.yws.tcms.web;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.yws.tcms.constant.GlobalConstant;
import com.yws.tcms.domain.Project;
import com.yws.tcms.domain.ProjectUser;
import com.yws.tcms.domain.User;
import com.yws.tcms.service.ProjectService;
import com.yws.tcms.service.UserService;
import com.yws.tcms.web.vo.ResultJson;

/**
 * 项目控制器类。<br>
 * @author Administrator
 *
 */
@Controller
@RequestMapping("/project")
public class ProjectController {
	
	@Autowired
	ProjectService projectService;
	@Autowired
	UserService userService;
	
	/**
	 * 项目列表页面
	 * @return
	 * @throws Exception
	 */
	@RequiresPermissions(value="project:query")
	@RequestMapping("/list")
	public ModelAndView listUI() throws Exception {
		ModelAndView mv = new ModelAndView();
		mv.setViewName("project/list");
		
		return mv;
	}
	
	/**
	 * 项目列表分页数据
	 * @param pageNum
	 * @param pageSize
	 * @return
	 * @throws Exception
	 */
	@RequiresPermissions(value="project:query")
	@ResponseBody
	@RequestMapping(value="/projectsPage", produces="application/json;charset=UTF-8")
	public ResultJson list(@RequestParam(defaultValue="0")int pageNum, @RequestParam(defaultValue="10")int pageSize) throws Exception {
		Page<Project> projectsPage = projectService.findAll(pageNum, pageSize);
		ResultJson json = new ResultJson();
		json.setSuccess(true);
		json.setData(projectsPage);
		
		return json;
	}
	
	/**
	 * 向数据库中保存一个项目（新增或修改，当有id并且在数据库中存在时则为修改）
	 * @param user
	 * @return ModelAndView对象
	 */
	@RequiresPermissions(value="project:add")
	@ResponseBody
	@PostMapping(value="/save", produces="application/json;charset=UTF-8")
	public String saveUser(Project project, @RequestParam(required=false)Integer pmId, @RequestParam(required=false)Integer qaId){
		if(pmId != null && !"".equals(pmId)){
			User pm = userService.findById(pmId);
			if(pm != null){
				project.setPm(pm);
			}
		}
		if(qaId != null && !"".equals(qaId)){
			User qa = userService.findById(qaId);
			if(qa != null){
				project.setQa(qa);
			}
		}
		projectService.save(project);
		
		return GlobalConstant.SUCCESS;
	}
	
	/**
	 * 项目看板
	 * @param projectId 项目id
	 * @return
	 */
	@RequiresPermissions(value="project:query")
	@RequestMapping(value="/details")
	public ModelAndView findGroupNumbers(Integer projectId){
		ModelAndView mv = new ModelAndView();
		Project project = projectService.findOne(projectId);
		mv.addObject("project",project);
		mv.setViewName("project/details");
		return mv;
	}
	
	/**
	 * 为指定id的项目分配组员
	 * @param projectId 项目ID
	 * @return
	 * @throws Exception
	 */
	@RequiresPermissions(value="project:add")
	@RequestMapping("/allocateUsers")
	public ModelAndView allocateUsers(Integer projectId) throws Exception {
		Project project = projectService.findOne(projectId);
		ModelAndView mv = new ModelAndView();
		mv.addObject("projectId", projectId);
		// 放入员工列表数据（除去该项目已分配的员工）、放入已分配的员工列表
		List<User> allUsers = userService.listAll();
		
		// 该项目已有的组员 
		Set<ProjectUser> projectUsers = project.getProjectUsers();
		List<User> users = new ArrayList<User>();
		for(ProjectUser pu:projectUsers){
			users.add(pu.getUser());
		}
		if(users.size()>0){
			allUsers.removeAll(users); // 根据user的id来判断
		}
		
		Comparator<User> userComparator = new Comparator<User>() {
			public int compare(User u1, User u2) {
	            return new Double(u1.getId()).compareTo(new Double(u2.getId()));  
	        }
		};
		
		Collections.sort(allUsers, userComparator);
		Collections.sort(users, userComparator);
		mv.addObject("allUsers", allUsers); // 可分配的组员
		mv.addObject("users", users); // 已分配的组员
		mv.setViewName("project/allocateUsers");
		
		return mv;
	}
	
	/**
	 * 保存与员工的关联关系
	 * @param projectId 项目ID
	 * @param userIds 员工ID，多个id用英文逗号分隔
	 * @return
	 */
	@RequiresPermissions(value="project:add")
	@ResponseBody
	@PostMapping(value="/ajax_save_user_project", produces="application/json;charset=UTF-8")
	public ResultJson ajaxSaveUserProject(Integer projectId, String userIds){
		projectService.saveUserProject(projectId, userIds);
		ResultJson json = new ResultJson(true);
		return json;
	}
	
	/**
	 * 删除给定id的项目
	 * @param id
	 * @return
	 * @throws Exception
	 */
	@RequiresPermissions(value="project:delete")
	@ResponseBody
	@PostMapping(value="/del", produces="application/json;charset=UTF-8")
	public ResultJson del(Integer id) throws Exception {
		return projectService.delete(id);
	}
	
}
