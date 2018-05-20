package com.yws.tcms.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * 项目实体类
 * @author yws710
 *
 */
@Entity
@Table(name="t_project")
public class Project implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7248817938406089122L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Integer id;
	
	private String name; // 项目名称
	
	// 该注解用于日期时间格式校验
	@DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	private Date beginDate; // 项目开始时间
	
	@DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	private Date endDate; //项目结束时间
	
	@JsonIgnore
	@ManyToOne
	@JoinColumn(name="pm_id")
	private User pm; // 合作方PM（和User是单向的多对一关系）
	
	private String customerPM; // 华为方PM
	
	@JsonIgnore
	@ManyToOne
	@JoinColumn(name="qa_id")
	private User qa; // 合作方QA（和User是单向的多对一关系）
	
	private String customerQA; // 华为方QA
	
	private String member; // 人员情况
	
	private String content; // 工作内容
	
	private Boolean active = Boolean.TRUE; // 是否当前项目
	
	@JsonIgnore
	@OneToMany(mappedBy="project", fetch=FetchType.LAZY)
	private Set<ProjectUser> projectUsers = new HashSet<ProjectUser>(); // 组员集合
	
	
	@Transient
	private Integer pmId; // 合作方PM主键
	
	@Transient
	private String pmName; // 合作方PM姓名工号
	
	@Transient
	private Integer qaId; // 合作方QA主键
	
	@Transient
	private String qaName; // 合作方QA姓名工号
	
	
	
	
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
	public Date getBeginDate() {
		return beginDate;
	}
	public void setBeginDate(Date beginDate) {
		this.beginDate = beginDate;
	}
	public Date getEndDate() {
		return endDate;
	}
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	public User getPm() {
		return pm;
	}
	public void setPm(User pm) {
		this.pm = pm;
	}
	public String getCustomerPM() {
		return customerPM;
	}
	public void setCustomerPM(String customerPM) {
		this.customerPM = customerPM;
	}
	public User getQa() {
		return qa;
	}
	public void setQa(User qa) {
		this.qa = qa;
	}
	public String getCustomerQA() {
		return customerQA;
	}
	public void setCustomerQA(String customerQA) {
		this.customerQA = customerQA;
	}
	public String getMember() {
		return member;
	}
	public void setMember(String member) {
		this.member = member;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public Boolean getActive() {
		return active;
	}
	public void setActive(Boolean active) {
		this.active = active;
	}
	public Set<ProjectUser> getProjectUsers() {
		return projectUsers;
	}
	public void setProjectUsers(Set<ProjectUser> projectUsers) {
		this.projectUsers = projectUsers;
	}
	public Integer getPmId() {
		if(this.pm != null){
			pmId = this.pm.getId();
		}
		return pmId;
	}
	public void setPmId(Integer pmId) {
		this.pmId = pmId;
	}
	public String getPmName() {
		if(this.pm != null){
			pmName = this.pm.getName() + " / " + this.pm.getEmpNo();
		}
		return pmName;
	}
	public void setPmName(String pmName) {
		this.pmName = pmName;
	}
	public Integer getQaId() {
		if(this.qa != null){
			qaId = this.qa.getId();
		}
		return qaId;
	}
	public void setQaId(Integer qaId) {
		this.qaId = qaId;
	}
	public String getQaName() {
		if(this.qa != null){
			qaName = this.qa.getName() + " / " + this.qa.getEmpNo();
		}
		return qaName;
	}
	public void setQaName(String qaName) {
		this.qaName = qaName;
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
		if (this == obj){
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		Project other = (Project) obj;
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
