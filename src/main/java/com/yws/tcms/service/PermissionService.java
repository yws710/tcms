package com.yws.tcms.service;

import java.util.List;

import org.springframework.data.domain.Page;

import com.yws.tcms.domain.Permission;
import com.yws.tcms.web.vo.ResultJson;


public interface PermissionService {

	/**
	 * 保存
	 * @param privilege
	 * @return
	 */
	Permission save(Permission privilege);

	/**
	 * 删除
	 * @param id
	 * @return 返回一个Json对象
	 */
	ResultJson delete(Integer id);
	
	/**
	 * 根据id查找
	 * @param id
	 * @return 如果id为null或查询结果为空则返回null
	 */
	Permission findOne(Integer id);
	
	/**
	 * 根据id查找，返回Privilege对象
	 * @param id
	 * @return 返回一个Privilege对象，如果id为null或查询结果为空，则返回null
	 */
	Permission findById(Integer id);
	
	/**
	 * 分页查询
	 * @param pageNum
	 * @param pageSize
	 * @return
	 */
	Page<Permission> findAll(Integer pageNum, Integer pageSize);
	
	/**
	 * 查询所有
	 * @return
	 */
	List<Permission> findAll();
}
