package com.yws.tcms.service.impl;

import java.util.HashSet;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yws.tcms.domain.Permission;
import com.yws.tcms.domain.Role;
import com.yws.tcms.domain.RoleRepository;
import com.yws.tcms.service.PermissionService;
import com.yws.tcms.service.RoleService;

@Service
public class RoleServiceImpl implements RoleService {

	@Autowired
	RoleRepository roleRepository;

	@Autowired
	PermissionService privilegeService;
	
	public List<Role> findAll() {
		return roleRepository.findAll();
	}

	public Role findOne(Integer id) {
		if(id == null){
			return null;
		}else{
			return roleRepository.findById(id).orElse(null);
		}
	}

	public void save(Role role) {
		if(role != null){
			roleRepository.save(role);
		}
	}

	public void delete(Integer id) {
		Role role = this.findOne(id);
		if(role != null){
			roleRepository.delete(role);
		}
	}

	public void setPrivilege(Integer id, Integer[] privilegeIds) {
		Role role = this.findOne(id);
		if(role != null){
			role.setPermissions(new HashSet<Permission>()); // 先要清空该角色的资源集合(不要使用null哦)
			if(privilegeIds != null && privilegeIds.length>0){
				for(Integer privilegeId:privilegeIds){
					role.getPermissions().add(privilegeService.findById(privilegeId));
				}
			}
			
			roleRepository.save(role);
		}
	}
}
