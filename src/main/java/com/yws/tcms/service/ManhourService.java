package com.yws.tcms.service;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import com.yws.tcms.domain.Manhour;
import com.yws.tcms.web.vo.ResultJson;

/**
 * 餐补&超长工时服务接口类
 * @author yws710
 *
 */
public interface ManhourService {

	/**
	 * 分页查询
	 * @param page 页码
	 * @param size 每页显示多少条记录
	 * @return
	 */
	Page<Manhour> findAll(int page, int size);
	
	
	/**
	 * 处理上传的excel文件，批量导入。（将数据行转换为Manhour对象，然后存储到数据库表中。）
	 * @param file
	 */
	void batchImport(String rootpath, MultipartFile file);


	/**
	 * 根据id删除一条记录
	 * @param id
	 * @return 返回提示信息（ResultJson对象）
	 */
	ResultJson deleteById(Integer id);
	
	/**
	 * 部门名称列表
	 * @return
	 */
	List<String> pduList();
	
	/**
	 * 项目组列表
	 * @return
	 */
	List<String> projectList();
	
	/**
	 * 员工列表
	 * @return
	 */
	List<Map<String,String>> personList();
	
	/**
	 * 查询某部门的超长工时数据<br>
	 * 包括月份、季度、年度统计数据
	 * @param pdu 部门名称
	 * @return
	 */
	Map<String,Object> byPdu(String pdu);
	
	/**
	 * 查询某项目组的超长工时数据<br>
	 * 包括月份、季度、年度统计数据
	 * @param pdu 项目组名称
	 * @return
	 */
	Map<String,Object> byProject(String project);
	
	/**
	 * 查询某员工的超长工时数据<br>
	 * 包括月份、季度、年度统计数据
	 * @param empNo 员工工号
	 * @return
	 */
	Map<String,Object> byPerson(String empNo);
	
}
