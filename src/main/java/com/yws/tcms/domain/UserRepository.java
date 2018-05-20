package com.yws.tcms.domain;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface UserRepository extends JpaRepository<User, Integer>,JpaSpecificationExecutor<User> {
	
	/**
	 * 根据登录名（中软工号）查询
	 * @param name 登录名（唯一）
	 * @return
	 */
	User findByEmpNo(String empNo);
	
	/**
	 * 根据员工姓名查询员工列表
	 * @param name 员工姓名
	 * @return
	 */
	List<User> findByName(String name);
	
	/**
	 * 根据工号和密码查询
	 * @param empNo
	 * @param password
	 * @return
	 */
	User findByEmpNoAndPassword(String empNo,String password);
	
	/**
	 * 根据员工姓名模糊查询
	 * @param name 如：刘%
	 * @return
	 */
	List<User> findByNameLike(String name);
}

