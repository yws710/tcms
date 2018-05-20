package com.yws.tcms.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.shiro.authc.credential.DefaultPasswordService;
import org.apache.shiro.authc.credential.PasswordService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.yws.tcms.constant.GlobalConstant;
import com.yws.tcms.domain.User;
import com.yws.tcms.domain.UserRepository;
import com.yws.tcms.service.UserService;
import com.yws.tcms.web.vo.ResultJson;

@Service
public class UserServiceImpl implements UserService {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);

	@Autowired
	UserRepository userRepository;
	
	@SuppressWarnings("serial")
	public Page<User> search(final User user,int page,int size){
		return userRepository.findAll(new Specification<User>() {
			public Predicate toPredicate(Root<User> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				List<Predicate> predicates = new ArrayList<Predicate>();
				if(null != user && !StringUtils.isEmpty(user.getName())){
					// 根据姓名模糊匹配
					predicates.add(cb.like(root.<String> get("name"), "%" + user.getName() + "%"));
				}
				if(null != user && !StringUtils.isEmpty(user.getEmpNo())){
					// 根据工号精确查找
					predicates.add(cb.equal(root.<String> get("empNo"), user.getEmpNo()));
				}
				Predicate[] arr = new Predicate[predicates.size()];
				return query.where(predicates.toArray(arr)).getRestriction();
			}
		}, PageRequest.of(page-1, size, new Sort(Direction.ASC, "id")));
	}

	public User findByUsername(String username) {
		return userRepository.findByEmpNo(username);
	}

	// 递归获取给定员工的所有下级
	private Set<User> getSub(User user){
		Set<User> s = new HashSet<User>();
		s.add(user);
		if(user.getSubordinates().size()>0){
			for(User u:user.getSubordinates()){
				Set<User> set = getSub(u);
				s.addAll(set);
			}
		}
		
		return s;
	}
	
	public Set<User> getGroupMember(Integer id){
		Set<User> set = new HashSet<User>();
		User user = userRepository.findById(id).orElse(null);
		if(user != null){
			// 递归获取所有下级
			set = getSub(user);
		}
		return set;
	}

	public Page<User> findAll(int page, int size) {
		Sort sort = new Sort(Direction.ASC, "id");
		Pageable pageable = PageRequest.of(page, size, sort);
		
		return userRepository.findAll(pageable);
	}
	
	public void save(User user) {
		if(user.getId() != null && !"".equals(user.getId())){ // 修改
			User u = userRepository.findById(user.getId()).orElse(null);
			user.setPassword(u.getPassword());
		}else{ // 新增
			// 加密初始密码
			PasswordService service = new DefaultPasswordService();
			String pwd = service.encryptPassword(GlobalConstant.INIT_PASSWORD); // 加密
			user.setPassword(pwd); // 设置初始密码
		}
		
		userRepository.save(user);
	}

	public void uploadFile(String rootpath, MultipartFile file) {
		if (!file.isEmpty()) {
			// 取文件格式后缀名
			String type = file.getOriginalFilename().substring(file.getOriginalFilename().indexOf("."));
			String fileName = System.currentTimeMillis() + type; // 取当前时间戳作为文件名
			String path = rootpath + "upload/" + fileName;

			File destFile = new File(path);
			try {
				file.transferTo(destFile);
			} catch (IllegalStateException e) {
				LOGGER.error("当前对客户端的响应已经结束，不能在响应已经结束（或说消亡）后再向客户端（实际上是缓冲区）输出任何内容。" + e);
			} catch (IOException e) {
				LOGGER.error("IOException: " + e);
			}

			List<User> users = importXlsx(destFile);
			// 使用集合或数组之前先判断是否为空。
			if (!CollectionUtils.isEmpty(users)) {
				for (User user : users) {
					// 保存之前先根据工号查询，如果该工号的员工已经存在，则不保存。
					User exist = userRepository.findByEmpNo(user.getEmpNo());
					if(exist == null){
						userRepository.save(user);
					}
				}
			}
		}
	}

	/**
	 * 将Excel文件中的数据行转换为Emp对象。
	 * 
	 * @param file
	 *            Excel文件，后缀名为xlsx
	 * @return
	 */
	private List<User> importXlsx(File file) {
		List<User> empList = new ArrayList<User>();

		InputStream is = null;
		XSSFWorkbook xWorkbook = null;
		try {
			is = new FileInputStream(file);
			xWorkbook = new XSSFWorkbook(is);
			XSSFSheet xSheet = xWorkbook.getSheetAt(0);

			if (null != xSheet) {
				for (int i = 1; i < xSheet.getPhysicalNumberOfRows(); i++) {
					User emp = new User();
					XSSFRow xRow = xSheet.getRow(i);
					if(xRow != null && xRow.getCell(0) != null){ // 如果该行的第一个单元格为null，则视为该行没数据。
						emp.setName(xRow.getCell(0).toString().trim()); // 姓名
						if(xRow.getCell(1) != null){
							emp.setEmpNo(xRow.getCell(1).toString().trim()); // 中软工号
						}
						if(xRow.getCell(2) != null){
							emp.setGender(xRow.getCell(2).getStringCellValue()); // 性别
						}
						if(xRow.getCell(3) != null){
							emp.setJobLevel(xRow.getCell(3).getStringCellValue()); // 职级
						}
						if(xRow.getCell(4) != null){
							emp.setStatus(xRow.getCell(4).getStringCellValue()); // 在职状态
						}
						if(xRow.getCell(5) != null){
							emp.setCity(xRow.getCell(5).getStringCellValue()); // 办公城市
						}
						if(xRow.getCell(6) != null){
							emp.setBeach(xRow.getCell(6).getStringCellValue()); // 在岸or离岸
						}
						if(xRow.getCell(7) != null){
							emp.setSpecial(xRow.getCell(7).getStringCellValue()); // 是否特殊岗位
						}
						if(xRow.getCell(8) != null){
							emp.setPhone(xRow.getCell(8).getStringCellValue()); // 联系方式
						}
						if(xRow.getCell(9) != null){
							String satrapEmpNo = xRow.getCell(9).getStringCellValue();
							// 根据工号查找员工
							User satrap = userRepository.findByEmpNo(satrapEmpNo);
							if(satrap != null) {
								emp.setSatrap(satrap); // 中软直接主管
							}
						}
						if(xRow.getCell(10) != null){
							emp.setLiaisonOfficer(xRow.getCell(10).getStringCellValue()); // 华为接口人姓名&工号
						}
						if(xRow.getCell(11) != null){
							emp.setConnector(xRow.getCell(11).getStringCellValue()); // 紧急联系人
						}
						if(xRow.getCell(12) != null){
							emp.setIcPhone(xRow.getCell(12).getStringCellValue()); // 紧急联系人联系方式
						}
						if(xRow.getCell(13) != null){
							emp.setAddress(xRow.getCell(13).getStringCellValue()); // 工作地住址
						}
						if(xRow.getCell(14) != null){
							emp.setOffice(xRow.getCell(14).getStringCellValue()); // 办公地点
						}
						if(xRow.getCell(15) != null){
							double cellValue = xRow.getCell(15).getNumericCellValue();
							Date date = HSSFDateUtil.getJavaDate(cellValue);
							emp.setHireDate(date); // 入职日期
						}
						if(xRow.getCell(16) != null){
							emp.setHwmail(xRow.getCell(16).getStringCellValue()); // 华为邮箱
						}
						if(xRow.getCell(17) != null){
							emp.setEmail(xRow.getCell(17).getStringCellValue()); // 中软邮箱
						}
						if(xRow.getCell(18) != null){
							emp.setSkill(xRow.getCell(18).getStringCellValue()); // 技能
						}
						if(xRow.getCell(19) != null){
							emp.setRemark(xRow.getCell(19).getStringCellValue()); // 备注
						}

						// 加密初始密码
						PasswordService service = new DefaultPasswordService();
						String pwd = service.encryptPassword(GlobalConstant.INIT_PASSWORD); // 加密
						emp.setPassword(pwd); // 设置初始密码
						empList.add(emp);
					}
				}
			}
		} catch (FileNotFoundException e) {
			LOGGER.error("FileNotFoundException: " + e);
		} catch (IOException e) {
			LOGGER.error("IOException: " + e);
		} finally {
			if (null != is) {
				try {
					is.close();
				} catch (Exception e) {
					LOGGER.error("关闭InputStream异常: " + e);
				}
			}

			if (null != xWorkbook) {
				try {
					xWorkbook.close();
				} catch (Exception e) {
					LOGGER.error("关闭文件异常: " + e);
				}
			}
			// 删除上传的文件 
			if(file.exists()){
				file.delete();
			}
		}
		
		

		return empList;
	}
	public List<User> export2Excel(String name) {
		if(name == null || "".equals(name)){
			return userRepository.findAll();
		}else{
			return userRepository.findByName(name);
		}
	}

	public ResultJson deleteById(Integer id) {
		ResultJson json = new ResultJson();
		User user = this.findById(id);
		if(user != null){
			if(user.getSubordinates().size() >0){
				json.setSuccess(false);
				json.setMsg("该员工是其他员工的上级，不能删除。");
			}else if(user.getProjectUsers().size() > 0){
				json.setSuccess(false);
				json.setMsg("该员工尚在项目中，不能删除。");
			}else{
				userRepository.delete(user);
				json.setSuccess(true);
			}
		}else {
			json.setSuccess(false);
			json.setMsg("要删除的数据不存在");
		}
		
		return json;
	}

	public User findById(Integer id) {
		if(id == null){
			return null;
		}
		
		return userRepository.findById(id).orElse(null);
	}

	public List<User> listAll() {
		List<User> users = userRepository.findAll();
		List<User> vos = new ArrayList<User>();
		for(User user:users){
			User vo = new User();
			vo.setId(user.getId());
			vo.setName(user.getName());
			vo.setEmpNo(user.getEmpNo());
			vos.add(vo);
		}
		return vos;
	}

	public ResultJson editPwd(Integer id, String oldPwd, String newPwd) {
		ResultJson json = new ResultJson(false);
		PasswordService service = new DefaultPasswordService();
		// 加密新密码
		String npwd = service.encryptPassword(newPwd);
		
		User user = this.findById(id);
		
		if(user != null){
			PasswordService passwordService = new DefaultPasswordService();
	    	if(passwordService.passwordsMatch(oldPwd, user.getPassword())){ // 密码验证成功
				user.setPassword(npwd);
				userRepository.save(user);
				json.setSuccess(true);
			}else { // 密码输入不正确
				json.setMsg("密码输入不正确");
			}
		}else{ // 超级管理员的id为空
			json.setMsg("Session已失效或该用户不存在");
		}
		
		return json;
	}

}
