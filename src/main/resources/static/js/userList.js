layui.use(['table','layer','form','jquery', 'laydate','upload'],function(){
	var table = layui.table
		,form = layui.form
		,$ = layui.jquery
		,layer = layui.layer
		,laydate = layui.laydate
		,upload = layui.upload;
	
	// 监听工具条（表格右侧的“修改”、“删除”按钮）
	table.on('tool(test)', function(obj){ //注：tool是工具条事件名，test是table原始容器的属性 lay-filter="对应的值"
		var data = obj.data; //获得当前行数据
		var layEvent = obj.event; //获得 lay-event 对应的值
		 
		if(layEvent === 'del'){ //删除
		    layer.confirm('真的要删除么？', function(index){
		    	$.ajax({
					type: 'POST',
					url: '/user/delete',
					dataType: 'json',
					data: {"id":data.id,_method: 'DELETE'},
					success: function(result,strStatus){
						if(result.success){
							$(".layui-laypage-btn").click();
							layer.msg("success",{icon:6});
						}else{
							layer.msg(result.msg,{icon:5});
						}
					},
					error: function(msg){
						layer.msg('操作失败，与服务器连接断开。',{icon:5});
					}
				});
		    	layer.close(index);
		    });
		} else if(layEvent === 'edit'){ //编辑
		  	vue.user = data;
		    layIndex = layer.open({
				type: 1,
				maxmin: true,
				title: '保存用户',
				area: ['auto', '450px'],
				content: $("#layuser")
			});
			$("input:checkbox").prop('checked', false); // 多选框需要单独重置，使用prop而不是attr
			if(data.roleIds){
				for(var i=0;i<data.roleIds.length;i++){
					$("input:checkbox[value=" + data.roleIds[i] + "]").prop('checked', true);
				}
			}
					
			vue.$nextTick(function(){ // 在DOM渲染完成之后执行
				form.render(); // 重新渲染表单。更新全部
			});
		}
	});
	
	var initUser = {
		id: '',
		name: '',
		empNo: '',
		jobLevel: '',
		salary: '',
		status: '在职',
		gender: '男',
		city: '',
		beach: '在岸',
		special: '否',
		phone: '',
		satrap: '',
		satrapPhone: '',
		liaisonOfficer: '',
		connector: '',
		icPhone: '',
		address: '',
		office: '',
		hireDate: '',
		hwmail: '',
		email: '',
		remark: ''
	}
				
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
			});
		},
		computed: {
			
		},
		methods: {
			// 信息录入界面
			addUI: function(){
				options.addUI();
			},
			
			// 重置表单（每当表单数据发生改变时，都需要重新渲染表单）
			resetForm: function(){
				this.user = $.extend(true, {}, initUser);
				this.$nextTick(function(){
					form.render(); // 重新渲染表单
				});
			},
			
			// 查询（根据姓名查询）
			search: function(){
				table.reload('userList', { // userList为table.render中的id
					where: { //设定异步数据接口的额外参数，任意设
						name: $("#searchName").val()
					}
				});
			},
			exportExcel: function(){
				$.ajax({
					type: 'POST',
					url: '/user/exportExcel',
					data: {name:$("#searchName").val()},
					dataType: 'json',
					beforeSend: function(){
						layIndex = layer.load(2);
					},
					success: function(result){
						if(result.success){
							var str = "姓名,工号,性别,职级,在职状态,薪资,角色,办公城市,在岸/离岸,是否特殊岗位,联系方式,中软直接主管,华为接口人,紧急联系人,紧急联系人联系方式,工作地住址,办公地点,入职日期,华为邮箱,中软邮箱,技能,备注";
							//console.log(JSON.stringify(result.data));
							
							for(var i=0;i<result.data.length;i++){
								var obj = result.data[i];
								str += "\n" + obj.name + "," + obj.empNo + "," + obj.gender + "," + obj.jobLevel + "," + obj.status + "," + obj.salary + "," + obj.roleNames + "," + obj.city + ","
									+ obj.beach + "," + obj.special + "," + obj.phone + "," + obj.satrapName + "," + obj.liaisonOfficer + "," + obj.connector + "," + obj.icPhone + ","
									+ obj.address + "," + obj.office + "," + obj.hireDate + "," + obj.hwmail + "," + obj.email + "," + obj.skill + "," + obj.remark;
							}
							
							var exportContent = "\uFEFF";
							var blob = new Blob([exportContent + str],{type: "text/plain;charset=utf-8"});
							saveAs(blob, "employees.csv");
							
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
			}
		}
	});
			
	// 封装了所有操作方法（避免vue中的methods过于臃肿）
	var options = {
		getAllUsers: function(){ // 获取所有用户（添加和修改项目时需要用到）
			$.ajax({
				type: 'GET',
				url: '/user/listAll',
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
				url: '/role/all_ajax_data',
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
		// 添加界面（首先弹出表单，然后将initUser的数据绑定到表单中）
		addUI: function(){
			layIndex = layer.open({
            	type: 1,
            	title: '新增员工信息',
            	area: ['auto', '450px'], //宽高
            	content: $("#layuser")
			});
			// vue.user使用初始化员工对象，由于使用了双向数据绑定，因此表单也会初始化。
			vue.user = $.extend(true, {}, initUser);
			$("input:checkbox").prop('checked', false); // 多选框需要单独重置，使用prop而不是attr
			vue.$nextTick(function(){
				form.render(); // layui的表单数据发生改变时，都需要重新渲染表单，否则数据的改变不会在表单上体现出来。
			});
		},
		
		saveuser: function(){
			$.ajax({
				type: 'POST',
				url: '/user/save',
				async: true,
				dataType: 'json',
				data: $('#form1').serializeArray(),
				success: function(data,strStatus){
					if(data == "success"){
						//window.location.reload();
						//table.reload('userList', {});
						// 在操作成功后调用分页的确定按钮 $(".layui-laypage-btn")[0].click();就能保证当前页刷新了，
						$(".layui-laypage-btn").click();
						layer.msg("success",{icon:6});
					}else{
						layer.msg(JSON.stringify(data),{icon:5});
					}
				},
				error: function(msg){
					console.log(JSON.stringify(msg));
					layer.msg('操作失败，与服务器连接断开。',{icon:5});
				}
			});
		}
	};
	
	var layIndex = null; // 弹出层索引
			
	var uploadInst = upload.render({
	    elem: '#file1' //绑定元素
	    ,accept: 'file'		// 允许上传所有类型的文件
	    ,exts: 'xlsx|xls'   // 指定上传文件的后缀名
	    ,url: '/user/import' //上传接口
	    ,before: function(obj){ //obj参数包含的信息，跟 choose回调完全一致，可参见上文。
	        layer.load(); //上传loading
	    }
	    ,done: function(res){
	    	if(res.code == 0){
	    		$(".layui-laypage-btn").click();
				layer.closeAll('loading');
				layer.msg("导入成功",{icon:6});
	    	}
	    }
	    ,error: function(){
	    	layer.msg('操作失败',{icon:5});
	    }
	  });
			
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