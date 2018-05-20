package com.yws.tcms.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * 角色实体类
 * @author yws710
 *
 */
@Entity
@Table(name="t_role")
public class Role implements Serializable {

	private static final long serialVersionUID = 4718793964944680444L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Integer id; // 编号
	
	private String name; // 角色名称
	
	private String description; // 描述
	
	// 禁止序列化（避免懒加载引起的异常）
	@JsonIgnore
	@ManyToMany(mappedBy="roles",fetch=FetchType.LAZY)
	private List<User> users = new ArrayList<User>();
	
	// 禁止序列化（避免懒加载引起的异常）
	@JsonIgnore
	@ManyToMany(fetch=FetchType.LAZY)
	@JoinTable(name="t_role_permission",joinColumns={@JoinColumn(name="role_id",nullable = false)}, inverseJoinColumns = { @JoinColumn(name = "permission_id", nullable = false)})
	private Set<Permission> permissions = new HashSet<Permission>();
	
	public Role(){}
	
	public Role(Integer id){
		this.id = id;
	}
	
	
	
	
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
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public List<User> getUsers() {
		return users;
	}
	public void setUsers(List<User> users) {
		this.users = users;
	}
	public Set<Permission> getPermissions() {
		return permissions;
	}
	public void setPermissions(Set<Permission> permissions) {
		this.permissions = permissions;
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
		Role other = (Role) obj;
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
