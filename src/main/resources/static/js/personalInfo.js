// 个人信息
layui.use(['layer','form','jquery', 'laydate'],function(){
	var form = layui.form
		,$ = layui.jquery
		,layer = layui.layer
		,laydate = layui.laydate;
	
	var vue = new Vue({
		el: '#app',
		data: {
			allUsers: [], // 所有员工（只有id,name,empNo属性）
			user: {}, // 员工（用于和表单双向绑定）
			roles: []
		},
		created: function(){
			Vue.nextTick(function(){
				options.getAllUsers();
				options.getRoles();
				options.editUI();
			});
		},
		methods: {
			// 重置表单（每当表单数据发生改变时，都需要重新渲染表单）
			resetForm: function(){
				this.user = {};
				this.$nextTick(function(){
					form.render(); // 重新渲染表单
				});
			}
		}
	});
			
	// 封装了所有操作方法（避免vue中的methods过于臃肿）
	var options = {
		getAllUsers: function(){ // 获取所有用户（添加和修改项目时需要用到）
			$.ajax({
				type: 'GET',
				url: 'user/listAll',
				beforeSend: function(){
					layIndex = layer.load(2);
				},
				success: function(result){
					if(result.success){
						vue.allUsers = result.data;
					}else{
						layer.msg('获取所有员工数据失败');
					}
				},
				error: function(msg){
					layer.msg('获取所有员工数据失败，与服务器连接已断开。');
				},
				complete: function(xmlHttpRequest, strStatus){
					layer.close(layIndex); // 关闭弹出层
				}
			});
		},
		getRoles: function(){ // 获取角色列表
			$.ajax({
				type: 'POST',
				url: 'role/all_ajax_data',
				beforeSend: function(){
					layIndex = layer.load(2);
				},
				success: function(result){
					if(result.success){
						vue.roles = result.data;
					}else{
						layer.msg('获取角色数据失败');
					}
				},
				error: function(msg){
					layer.msg('获取角色数据失败，与服务器连接已断开。');
				},
				complete: function(xmlHttpRequest, strStatus){
					layer.close(layIndex); // 关闭弹出层
				}
			});
		},
		saveuser: function(){
			$.ajax({
				type: 'POST',
				url: 'user/editPersonalInfo',
				async: true,
				dataType: 'json',
				data: $('#form1').serializeArray(),
				success: function(data,strStatus){
					if(data == "success"){
						window.location.reload();
						layer.msg("success",{icon:6});
					}else{
						layer.msg(JSON.stringify(data),{icon:5});
					}
				},
				error: function(XMLHttpRequest, textStatus, errorThrown){
					layer.msg('系统错误或无操作权限',{icon:5});
				}
			});
		},
		editUI: function(){ // 回显数据
			$.ajax({
				type: 'POST',
				url: 'user/personalInfo',
				beforeSend: function(){
					layIndex = layer.load(2);
				},
				success: function(result){
					if(result.success){
						var data = result.data;
						vue.user = data;
						$("input:checkbox").prop('checked', false); // 多选框需要单独重置，使用prop而不是attr
						if(data.roleIds){
							for(var i=0;i<data.roleIds.length;i++){
								$("input:checkbox[value=" + data.roleIds[i] + "]").prop('checked', true);
							}
						}
						vue.$nextTick(function(){ // 在DOM渲染完成之后执行
							form.render(); // 重新渲染表单。更新全部
						});
					}else{
						layer.msg('获取员工信息失败');
					}
				},
				error: function(msg){
					layer.msg('获取员工信息失败，与服务器连接已断开。');
				},
				complete: function(xmlHttpRequest, strStatus){
					layer.close(layIndex); // 关闭弹出层
				}
			});
					
			
		}
	};
	
	var layIndex = null; // 弹出层索引
			
	//监听提交表单
    form.on('submit(sb1)', function(data){
    	options.saveuser();
        layer.close(layIndex);
        return false;
    });
		    
    // 监听性别select下拉选择事件
    form.on('select(gender)', function(data){
	  vue.user.gender = data.value; // 鉴于layui下拉选择框的特殊性，需要手动给vue.user.gender赋值。
	});
    // 监听在岸/离岸select下拉选择事件
    form.on('select(beach)', function(data){
	  vue.user.beach = data.value;
	});
    // 监听是否特殊岗位select下拉选择事件
    form.on('select(special)', function(data){
    	vue.user.special = data.value;
    });
	// 监听状态select下拉选择事件
    form.on('select(status)', function(data){
    	vue.user.status = data.value;
    });
	// 监听上级select下拉选择事件
    form.on('select(satrapId)', function(data){
    	vue.user.satrapId = data.value;
    });
    
	//日期
    laydate.render({
      elem: '#hireDate',
      format: 'yyyy-MM-dd HH:mm:ss'
    });
		    
});	