package com.yws.tcms.web;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
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
import com.yws.tcms.domain.Manhour;
import com.yws.tcms.service.ManhourService;
import com.yws.tcms.web.vo.PageVO;
import com.yws.tcms.web.vo.ResultJson;

/**
 * 超长工时控制器类。<br>
 * 这里只应该有视图层对象，而不应该出现实体层对象。
 * @author Administrator
 *
 */
@Controller
@RequestMapping("/manhour")
public class ManhourController {
	
	@Autowired
	ManhourService manhourService;
	
	/**
	 * 指向超长工时管理页面（数据列表）<br>
	 * @return
	 * @throws Exception
	 */
	@RequiresPermissions(value="manhour:query")
	@RequestMapping("/list")
	public ModelAndView list() throws Exception {
		ModelAndView mv = new ModelAndView();
		mv.setViewName("manhour/list");
		return mv;
	}
	
	/**
	 * 超长工时数据列表分页显示
	 * @param name 员工姓名
	 * @param pageNum 当前页码，设置默认值为1
	 * @param pageSize 每页显示多少条记录，设置默认值为10
	 * @return
	 * @throws Exception
	 */
	@RequiresPermissions(value="manhour:query")
	@ResponseBody
	@GetMapping(value="/listData", produces="application/json;charset=UTF-8")
	public PageVO<Manhour> userPage(@RequestParam(defaultValue="1")int page, @RequestParam(defaultValue="10")int limit) throws Exception{
		// 前端页面使用了layui的table.reload，提交的中文需要转码
		//name = new String(name.getBytes("iso-8859-1"),"utf-8");
		Page<Manhour> usersPage = manhourService.findAll(page-1, limit);
		
		PageVO<Manhour> pagevo = new PageVO<Manhour>();
		pagevo.setCount(usersPage.getTotalElements());
		List<Manhour> users = usersPage.getContent();
		pagevo.setData(users);
		
		return pagevo;
	}
	
	/**
	 * 上传文件，并读取文件内容（员工信息）存储到数据库中。
	 * @param file Excel文件，后缀名为xlsx
	 * @return
	 * @throws Exception
	 */
	@RequiresPermissions(value="manhour:add")
	@ResponseBody
	@RequestMapping(value="/import", produces="application/json;charset=UTF-8")
	public Map<String,Object> uploadFile(HttpServletRequest request, @RequestParam(value="file") MultipartFile file) throws Exception {
		String rootpath = request.getSession().getServletContext().getRealPath("/");
		
		// 批量导入
		manhourService.batchImport(rootpath, file);
		Map<String,Object> result = new HashMap<String,Object>();
		result.put("code", 0);
		result.put("msg", GlobalConstant.SUCCESS);
		return result;
	}
	
	/**
	 * 删除给定id的项目
	 * @param id
	 * @return
	 * @throws Exception
	 */
	@RequiresPermissions(value="manhour:delete")
	@ResponseBody
	@DeleteMapping(value="/delete", produces="application/json;charset=UTF-8")
	public ResultJson del(Integer id) throws Exception {
		return manhourService.deleteById(id);
	}
	
	/**
	 * 部门餐补&超长工时统计页面
	 * @return
	 * @throws Exception
	 */
	@RequiresPermissions(value="manhour:query")
	@RequestMapping("/department")
	public ModelAndView department() throws Exception {
		ModelAndView mv = new ModelAndView();
		List<String> pduList = manhourService.pduList();
		mv.addObject("pduList", pduList);
		mv.setViewName("manhour/department");
		return mv;
	}
	
	/**
	 * 统计某部门餐补和超长工时<br>
	 * 包括月度、季度和年度统计
	 * @param pdu
	 * @return
	 * @throws Exception
	 */
	@RequiresPermissions(value="manhour:query")
	@ResponseBody
	@PostMapping(value="/byPdu", produces="application/json;charset=UTF-8")
	public Map<String,Object> byPdu(String pdu) throws Exception {
		return manhourService.byPdu(pdu);
	}
	
	/**
	 * 项目组餐补&超长工时统计页面
	 * @return
	 * @throws Exception
	 */
	@RequiresPermissions(value="manhour:query")
	@RequestMapping("/project")
	public ModelAndView project() throws Exception {
		ModelAndView mv = new ModelAndView();
		List<String> projectList = manhourService.projectList();
		mv.addObject("projectList", projectList);
		mv.setViewName("manhour/project");
		return mv;
	}
	
	/**
	 * 统计某项目组的餐补和超长工时<br>
	 * 包括月度、季度和年度统计
	 * @param project 项目组名称
	 * @return
	 * @throws Exception
	 */
	@RequiresPermissions(value="manhour:query")
	@ResponseBody
	@PostMapping(value="/byProject", produces="application/json;charset=UTF-8")
	public Map<String,Object> byProject(String project) throws Exception {
		return manhourService.byProject(project);
	}
	
	/**
	 * 个人餐补&超长工时统计页面
	 * @return
	 * @throws Exception
	 */
	@RequiresPermissions(value="manhour:query")
	@RequestMapping("/person")
	public ModelAndView person() throws Exception {
		ModelAndView mv = new ModelAndView();
		List<Map<String,String>> personList = manhourService.personList();
		mv.addObject("personList", personList);
		mv.setViewName("manhour/person");
		return mv;
	}
	
	/**
	 * 统计某员工的餐补和超长工时<br>
	 * 包括月度、季度和年度统计
	 * @param empNo 员工工号
	 * @return
	 * @throws Exception
	 */
	@RequiresPermissions(value="manhour:query")
	@ResponseBody
	@PostMapping(value="/byPerson", produces="application/json;charset=UTF-8")
	public Map<String,Object> byPerson(String empNo) throws Exception {
		return manhourService.byPerson(empNo);
	}
}
