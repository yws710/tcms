package com.yws.tcms.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * 工时实体类<br>
 * @author yws710
 *
 */
@Entity
@Table(name="t_man_hour")
public class Manhour implements Serializable {

	private static final long serialVersionUID = -1511446796456203238L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Integer id;
	
	// 员工姓名
	private String name;
	
	// 员工中软工号
	private String empNo;
	
	// 年月
	//@DateTimeFormat(pattern="yyyy/mm")
	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
	private Date date;
	
	// 员工所属部门
	private String pdu;
	
	// 员工所属项目组
	private String project;
	
	// 该员工的晚餐补助次数
	private Double buzhu;
	
	// 该员工的超长工时（小时数）
	private Double hours;
	
	// --------------- getter & setter --------------------

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmpNo() {
		return empNo;
	}

	public void setEmpNo(String empNo) {
		this.empNo = empNo;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getPdu() {
		return pdu;
	}

	public void setPdu(String pdu) {
		this.pdu = pdu;
	}

	public String getProject() {
		return project;
	}

	public void setProject(String project) {
		this.project = project;
	}

	public Double getBuzhu() {
		return buzhu;
	}

	public void setBuzhu(Double buzhu) {
		this.buzhu = buzhu;
	}

	public Double getHours() {
		return hours;
	}

	public void setHours(Double hours) {
		this.hours = hours;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
			
		if (obj == null) {
			return false;
		}
			
		if (getClass() != obj.getClass()){
			return false;
		}
			
		Manhour other = (Manhour) obj;
		if (id == null) {
			if (other.id != null) {
				return false;
			}
		} else if (!id.equals(other.id)) {
			return false;
		}
			
		return true;
	}
	
}
