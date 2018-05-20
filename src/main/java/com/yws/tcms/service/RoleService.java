package com.yws.tcms.service;

import java.util.List;

import com.yws.tcms.domain.Role;

public interface RoleService {

	/**
	 * 功能描述：获取所有Role实体对象
	 * 
	 * @return 返回所有Role实体对象
	 */
	List<Role> findAll();

	/**
	 * 功能描述：根据给定的id查询Role实体对象
	 * 
	 * @param id 实体对象的主键
	 * @return 如果id为null或查询结果为空，则返回null
	 */
	Role findOne(Integer id);

	/**
	 * 功能描述：保存给定的Role实体对象
	 * 
	 * @param role 待保存的实体对象
	 */
	void save(Role role);

	/**
	 * 根据给定的id删除实体对象
	 * 
	 * @param id 实体对象的主键
	 */
	void delete(Integer id);
	
	/**
	 * 为指定id的角色设置权限
	 * @param id
	 * @param privilegeIds
	 */
	void setPrivilege(Integer id, Integer[] privilegeIds);
}
