package com.yws.tcms.service;

import org.springframework.data.domain.Page;

import com.yws.tcms.domain.Project;
import com.yws.tcms.web.vo.ResultJson;

/**
 * 项目servcie层接口
 * @author yws710
 *
 */
public interface ProjectService {
	
	/**
	 * 分页查询，获取项目列表
	 * @param pageNum 页码
	 * @param pageSize 每页显示多少条记录
	 * @return
	 */
	Page<Project> findAll(int pageNum, int pageSize);
	
	/**
	 * 功能描述：保存给定的项目实体对象，并返回保存后的实体对象<br>
	 * 如果project为null，则不保存，并且返回null
	 * 
	 * @param project 待保存的项目实体对象
	 * @return 返回保存后的实体对象
	 */
	Project save(Project project);

	/**
	 * 功能描述：根据id查找实体对象
	 * 
	 * @param id 实体对象的主键
	 * @return 如果id为null或查询结果为空则返回null
	 */
	Project findOne(Integer id);
	
	/**
	 * 保存和User的关联关系
	 * @param projectId 项目ID
	 * @param userIds 员工ID，用英文逗号分隔。
	 */
	void saveUserProject(Integer projectId, String userIds);

	/**
	 * 功能描述：删除指定id的实体对象
	 * 
	 * @param id 实体对象的主键
	 */
	ResultJson delete(Integer id);
}
