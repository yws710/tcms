package com.yws.tcms.service.impl;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.yws.tcms.domain.Project;
import com.yws.tcms.domain.ProjectRepository;
import com.yws.tcms.domain.ProjectUser;
import com.yws.tcms.domain.ProjectUserRepository;
import com.yws.tcms.domain.User;
import com.yws.tcms.domain.UserRepository;
import com.yws.tcms.service.ProjectService;
import com.yws.tcms.web.vo.ResultJson;

/**
 * 项目service层实现类
 * @author Administrator
 *
 */
@Service
public class ProjectServiceImpl implements ProjectService {
	
	@Autowired
	private ProjectRepository projectRepository;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private ProjectUserRepository projectUserRepository;
	
	public Page<Project> findAll(int pageNum, int pageSize) {
		Pageable pageable = PageRequest.of(pageNum, pageSize);
		
		return projectRepository.findAll(pageable);
	}

	public Project save(Project project) {
		if(project == null){
			return null;
		}else{
			return projectRepository.save(project);
		}
	}

	public Project findOne(Integer id) {
		if(id == null) {
			return null;
		} else {
			return projectRepository.findById(id).orElse(null);
		}
	}
	
	public void saveUserProject(Integer projectId, String userIds) {
		Project project = projectRepository.findById(projectId).orElse(null);
		
		// 先清空Project和ProjectUser的关系
		projectUserRepository.deleteAll(project.getProjectUsers());
		
		// 再添加新的关系
		String[] ids = userIds.split(",");
		if(ids != null && ids.length>0){
			for(int i=0;i<ids.length;i++){
				if(!"".equals(ids[i].trim())){
					Integer id = Integer.parseInt(ids[i]);
					User user = userRepository.findById(id).orElse(null);
					if(user != null){ // 初始化组员信息
						ProjectUser pu = new ProjectUser();
						pu.setProject(project);
						pu.setUser(user);
						pu.setInsertDate(new Date()); // 入项日期
						pu.setJobLevel(user.getJobLevel()); // 职级
						pu.setSalary(user.getSalary()); // 薪资
						projectUserRepository.save(pu);
					}
				}
			}
		}
	}

	public ResultJson delete(Integer id) {
		ResultJson json = new ResultJson();
		Project project = this.findOne(id);
		if(project != null){
			if(project.getProjectUsers().size() == 0){
				projectRepository.delete(project);
				json.setSuccess(true);
			}else{
				json.setSuccess(false);
				json.setMsg("该项目组员不为空，不能删除。");
			}
		}else{
			json.setSuccess(false);
			json.setMsg("要删除的数据不存在");
		}
		
		return json;
	}
}
