package com.yws.tcms.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

import com.yws.tcms.domain.Permission;
import com.yws.tcms.domain.PermissionRepository;
import com.yws.tcms.service.PermissionService;
import com.yws.tcms.web.vo.ResultJson;

@Service
public class PermissionServiceImpl implements PermissionService {

	@Autowired
	PermissionRepository permissionRepository;

	public Permission save(Permission privilege){
		if(privilege == null){
			return null;
		}else{
			return permissionRepository.save(privilege);
		}
	}
	
	public Permission findById(Integer id){
		if(id == null){
			return null;
		}else{
			return permissionRepository.findById(id).orElse(null);
		}
	}
	
	public ResultJson delete(Integer id){
		ResultJson json = new ResultJson();
		Permission entity = this.findById(id);
		if(entity != null){
			permissionRepository.delete(entity);
			json.setSuccess(true);
		}else{
			json.setSuccess(false);
			json.setMsg("要删除的数据不存在");
		}
		
		return json;
	}
	
	public Permission findOne(Integer id){
		if(id == null){
			return null;
		}else{
			return permissionRepository.findById(id).orElse(null);
		}
	}
	
	public Page<Permission> findAll(Integer pageNum, Integer pageSize) {
		Sort sort = new Sort(Direction.ASC, "id");
		Pageable pageable = PageRequest.of(pageNum, pageSize, sort);
		return permissionRepository.findAll(pageable);
	}

	public List<Permission> findAll() {
		return permissionRepository.findAll();
	}

}
