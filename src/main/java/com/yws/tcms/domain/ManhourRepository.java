package com.yws.tcms.domain;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ManhourRepository extends JpaRepository<Manhour, Integer> {
	
	/**
	 * 获取部门列表
	 * @return
	 */
	@Query(value="SELECT DISTINCT pdu FROM t_man_hour",nativeQuery=true)
	List<String> pduList();
	
	/**
	 * 获取项目组列表
	 * @return
	 */
	@Query(value="SELECT DISTINCT project FROM t_man_hour",nativeQuery=true)
	List<String> projectList();
	
	/**
	 * 获取员工工号列表
	 * @return
	 */
	@Query(value="SELECT DISTINCT emp_no,name FROM t_man_hour",nativeQuery=true)
	List<Object[]> empNoList();
	
	/**
	 * 按月份查询某部门的超长工时数据<br>
	 * Object[]: 日期,餐补次数,超长工时
	 * @param pdu 部门名称
	 * @return
	 */
	@Query(value="SELECT DATE_FORMAT(date, '%Y年%m月') as date, SUM(buzhu) as buzhu,SUM(hours) as hours FROM t_man_hour WHERE pdu=?1 GROUP BY DATE_FORMAT(date, '%Y-%m') ORDER BY date;",
			nativeQuery=true)
	List<Object[]> byPduMonth(String pdu);
	
	/**
	 * 按季度查询某部门的超长工时数据
	 * @param pdu 部门名称
	 * @return
	 */
	@Query(value="SELECT concat(date_format(date, '%Y年'),FLOOR((date_format(date, '%m')+2)/3),'季度') as date, SUM(buzhu) as buzhu,SUM(hours) as hours FROM t_man_hour WHERE pdu=?1 GROUP BY concat(date_format(date, '%Y'),FLOOR((date_format(date, '%m')+2)/3)) ORDER BY date;",
			nativeQuery=true)
	List<Object[]> byPduQuarter(String pdu);
	
	/**
	 * 按年份查询某部门的超长工时数据
	 * @param pdu 部门名称
	 * @return
	 */
	@Query(value="SELECT DATE_FORMAT(date, '%Y年') as date, SUM(buzhu) as buzhu,SUM(hours) as hours FROM t_man_hour WHERE pdu=?1 GROUP BY DATE_FORMAT(date, '%Y') ORDER BY date;",
			nativeQuery=true)
	List<Object[]> byPduYear(String pdu);
	
	/**
	 * 按月份查询某项目组的超长工时数据
	 * @param project 项目组名称
	 * @return
	 */
	@Query(value="SELECT DATE_FORMAT(date, '%Y年%m月') as date, SUM(buzhu) as buzhu,SUM(hours) as hours FROM t_man_hour WHERE project=?1 GROUP BY DATE_FORMAT(date, '%Y-%m') ORDER BY date;",
			nativeQuery=true)
	List<Object[]> byProjectMonth(String project);
	
	/**
	 * 按季度查询某项目组的超长工时数据
	 * @param project 项目组名称
	 * @return
	 */
	@Query(value="SELECT concat(date_format(date, '%Y年'),FLOOR((date_format(date, '%m')+2)/3),'季度') as date, SUM(buzhu) as buzhu,SUM(hours) as hours FROM t_man_hour WHERE project=?1 GROUP BY concat(date_format(date, '%Y'),FLOOR((date_format(date, '%m')+2)/3)) ORDER BY date;",
			nativeQuery=true)
	List<Object[]> byProjectQuarter(String project);
	
	/**
	 * 按年份查询某项目组的超长工时数据
	 * @param project 项目组名称
	 * @return
	 */
	@Query(value="SELECT DATE_FORMAT(date, '%Y年') as date, SUM(buzhu) as buzhu,SUM(hours) as hours FROM t_man_hour WHERE project=?1 GROUP BY DATE_FORMAT(date, '%Y') ORDER BY date;",
			nativeQuery=true)
	List<Object[]> byProjectYear(String project);
	
	/**
	 * 按月份查询某员工的超长工时数据
	 * @param empNo 工号
	 * @return
	 */
	@Query(value="SELECT DATE_FORMAT(date, '%Y年%m月') as date, SUM(buzhu) as buzhu,SUM(hours) as hours FROM t_man_hour WHERE emp_no=?1 GROUP BY DATE_FORMAT(date, '%Y-%m') ORDER BY date;",
			nativeQuery=true)
	List<Object[]> byPersonMonth(String empNo);
	
	/**
	 * 按季度查询某员工的超长工时数据
	 * @param empNo 工号
	 * @return
	 */
	@Query(value="SELECT concat(date_format(date, '%Y年'),FLOOR((date_format(date, '%m')+2)/3),'季度') as date, SUM(buzhu) as buzhu,SUM(hours) as hours FROM t_man_hour WHERE emp_no=?1 GROUP BY concat(date_format(date, '%Y'),FLOOR((date_format(date, '%m')+2)/3)) ORDER BY date;",
			nativeQuery=true)
	List<Object[]> byPersonQuarter(String empNo);
	
	/**
	 * 按年份查询某员工的超长工时数据
	 * @param empNo 工号
	 * @return
	 */
	@Query(value="SELECT DATE_FORMAT(date, '%Y年') as date, SUM(buzhu) as buzhu,SUM(hours) as hours FROM t_man_hour WHERE emp_no=?1 GROUP BY DATE_FORMAT(date, '%Y') ORDER BY date;",
			nativeQuery=true)
	List<Object[]> byPersonYear(String empNo);
}

