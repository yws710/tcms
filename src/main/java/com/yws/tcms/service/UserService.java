package com.yws.tcms.service;

import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import com.yws.tcms.domain.User;
import com.yws.tcms.web.vo.ResultJson;

public interface UserService {
	
	/**
	 * 多条件分页查询
	 * @param user
	 * @param page
	 * @param size
	 * @return
	 */
	Page<User> search(final User user, int page, int size);

	/**
	 * 根据登录名获取用户
	 * @param username 登录名
	 * @return
	 */
	User findByUsername(String username);
	
	/**
	 * 根据用户id获取所有下属
	 * @param id 用户id
	 * @return 返回一个无序集合
	 */
	Set<User> getGroupMember(Integer id);

	/**
	 * 所有用户列表<br>
	 * 只提供id,name,empNo这三个属性的数据
	 * 
	 * @return
	 */
	List<User> listAll();
	
	/**
	 * 处理上传的excel文件。（将数据行转换为Emp对象，再讲Emp对象转换为实体对象User然后存储到数据库表中。）
	 * @param file
	 */
	void uploadFile(String rootpath, MultipartFile file);

	/**
	 * 保存一条员工记录
	 * @param user
	 */
	void save(User user);
	
	/**
	 * 根据用户名获取用户列表，如果name为null，则查询所有
	 * @param name 用户名
	 */
	List<User> export2Excel(String name);

	/**
	 * 根据id删除一条记录
	 * @param id
	 * @return 返回提示信息（ResultJson对象）
	 */
	ResultJson deleteById(Integer id);

	/**
	 * 根据员工id查找
	 * @param id 员工id
	 * @return 如果id为null或查找结果为空，返回null
	 */
	User findById(Integer id);

	/**
	 * 修改给定id的员工的登录密码
	 * @param id 员工id
	 * @param oldPwd 旧密码
	 * @param newPwd 新密码
	 * @return 
	 */
	ResultJson editPwd(Integer id, String oldPwd, String newPwd);
	
}
