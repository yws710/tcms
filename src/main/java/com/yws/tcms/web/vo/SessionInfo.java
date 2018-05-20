package com.yws.tcms.web.vo;

import java.util.HashSet;
import java.util.Set;

/**
 * 用于存放Session信息
 * @author Administrator
 *
 */
public class SessionInfo {

	private Integer id;// 用户ID
	private String name; // 姓名
	private String account; // 账号（登录名、工号）
	private String ip; // 用户IP
	
	// 用户可以访问的资源地址列表
	private Set<String> resources = new HashSet<String>();
	// 所有资源列表
	private Set<String> allResources = new HashSet<String>();
	
	public String getName() {
		return name;
	}

	public Integer getId() {
		return id;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAccount() {
		return account;
	}
	
	public void setAccount(String account) {
		this.account = account;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getIp() {
		return ip;
	}

	public Set<String> getResources() {
		return resources;
	}

	public void setResources(Set<String> resources) {
		this.resources = resources;
	}

	public Set<String> getAllResources() {
		return allResources;
	}

	public void setAllResources(Set<String> allResources) {
		this.allResources = allResources;
	}	
}
