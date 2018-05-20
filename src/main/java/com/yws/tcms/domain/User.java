package com.yws.tcms.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * 员工实体类<br>
 * 根据id来判断两个对象是否相等
 * @author yws710
 *
 */
@Entity
@Table(name="t_user")
public class User implements Serializable {
	
	private static final long serialVersionUID = -8675382197763845411L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Integer id; // 编号
	
	private String name; // 姓名
	
	@Column(unique=true)
	private String empNo; // 中软工号（需要增加唯一性约束）
	
	@JsonIgnore
	private String password; // 登录密码
	
	private String gender; // 性别
	
	private String city; // 办公城市
	
	private String beach; // 在岸/离岸
	
	private String special; // 是否特殊岗位
	
	private String phone; // 联系方式
	
	private String liaisonOfficer; // 华为接口人姓名/工号（在岸必填）
	
	private String connector; //紧急联系人
	
	private String icPhone; // 紧急联系人联系方式
	
	private String address; // 工作地住址
	
	private String office; // 办公地点
	
	private String remark; // 备注
	
	private String jobLevel; // 职级
	
	private String status; // 在职状态
	
	private Double salary; // 薪资
	
	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
	@DateTimeFormat(pattern="yyyy-MM-dd")
	private Date hireDate; // 入职日期
	
	private String hwmail; // 华为邮箱
	
	private String email; // 中软邮箱
	
	private String skill; // 技能
	
	@JsonIgnore
	@ManyToMany(fetch=FetchType.LAZY)
	@JoinTable(name="t_user_role",joinColumns={@JoinColumn(name="user_id",nullable = false)}, inverseJoinColumns = { @JoinColumn(name = "role_id", nullable = false)})
	private Set<Role> roles = new HashSet<Role>();
	
	@JsonIgnore // 禁止序列化（避免懒加载引起的异常，以及循环调用）
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="satrap_id")
	private User satrap; // 中软直接主管（上级）
	
	@JsonIgnore
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "satrap", orphanRemoval=false)
	private Set<User> subordinates = new HashSet<User>(); // 下属员工
	
	@JsonIgnore
	@OneToMany(mappedBy="user",fetch = FetchType.LAZY)
	private Set<ProjectUser> projectUsers = new HashSet<ProjectUser>();
	
	// 以下属性不需要持久化
	@Transient
	private Integer satrapId; // 上级主键
	
	@Transient
	private String satrapName; // 上级姓名
	
	@Transient
	private Set<Integer> roleIds = new HashSet<Integer>(); // 角色id(用于角色回显)
	
	@Transient
	private String roleNames; // 角色名称（多个用逗号分隔）
	
	
	
	
	// ------------------------ getter & setter --------------------------------------
	
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
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getGender() {
		return gender;
	}
	public void setGender(String gender) {
		this.gender = gender;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getBeach() {
		return beach;
	}
	public void setBeach(String beach) {
		this.beach = beach;
	}
	public String getSpecial() {
		return special;
	}
	public void setSpecial(String special) {
		this.special = special;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public User getSatrap() {
		return satrap;
	}
	public void setSatrap(User satrap) {
		this.satrap = satrap;
	}
	public Set<User> getSubordinates() {
		return subordinates;
	}
	public void setSubordinates(Set<User> subordinates) {
		this.subordinates = subordinates;
	}
	public Set<ProjectUser> getProjectUsers() {
		return projectUsers;
	}
	public void setProjectUsers(Set<ProjectUser> projectUsers) {
		this.projectUsers = projectUsers;
	}
	public String getLiaisonOfficer() {
		return liaisonOfficer;
	}
	public void setLiaisonOfficer(String liaisonOfficer) {
		this.liaisonOfficer = liaisonOfficer;
	}
	public String getConnector() {
		return connector;
	}
	public void setConnector(String connector) {
		this.connector = connector;
	}
	public String getIcPhone() {
		return icPhone;
	}
	public void setIcPhone(String icPhone) {
		this.icPhone = icPhone;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getOffice() {
		return office;
	}
	public void setOffice(String office) {
		this.office = office;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public String getJobLevel() {
		return jobLevel;
	}
	public void setJobLevel(String jobLevel) {
		this.jobLevel = jobLevel;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public Set<Role> getRoles() {
		return roles;
	}
	public void setRoles(Set<Role> roles) {
		this.roles = roles;
	}
	public Double getSalary() {
		return salary;
	}
	public void setSalary(Double salary) {
		this.salary = salary;
	}
	public Integer getSatrapId() {
		if(this.satrap != null){
			satrapId = this.satrap.getId();
		}
		return satrapId;
	}
	public void setSatrapId(Integer satrapId) {
		this.satrapId = satrapId;
	}
	public String getSatrapName() {
		if(this.satrap != null){
			satrapName = this.satrap.getName() + " / " + this.satrap.getEmpNo();
		}
		return satrapName;
	}
	public void setSatrapName(String satrapName) {
		this.satrapName = satrapName;
	}
	public Set<Integer> getRoleIds() {
		for(Role role:this.roles){
			roleIds.add(role.getId());
		}
		return roleIds;
	}
	public void setRoleIds(Set<Integer> roleIds) {
		this.roleIds = roleIds;
	}
	public String getRoleNames() {
		String names = "";
		for(Role role:this.roles){
			names += role.getName() + " | ";
		}
		if(names.length()>0){
			names = names.substring(0, names.length()-3);
		}
		roleNames = names;
		return roleNames;
	}
	public void setRoleNames(String roleNames) {
		this.roleNames = roleNames;
	}
	public Date getHireDate() {
		return hireDate;
	}
	public void setHireDate(Date hireDate) {
		this.hireDate = hireDate;
	}
	public String getHwmail() {
		return hwmail;
	}
	public void setHwmail(String hwmail) {
		this.hwmail = hwmail;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getSkill() {
		return skill;
	}
	public void setSkill(String skill) {
		this.skill = skill;
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
		if (getClass() != obj.getClass()) {
			return false;
		}
		User other = (User) obj;
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
