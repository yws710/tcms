layui.use(['jquery','layer','form','laypage'],function(){
	
	var form = layui.form,
		layer = layui.layer,
		laypage = layui.laypage,
		$ = layui.jquery;

	var vue = new Vue({
		el: '#app',
		data: {
			currPage: 1, // 当前页
			pageSize: 15, // 每页显示多少条记录
			privileges: [], // 权限资源列表（用于和表格单向绑定）
			allPrivileges: [], // 所有权限，用于选择上级时的回显
			privilege: {} // 权限资源（用于和表单双向绑定）
		},
		computed: {
		},
		created: function(){
			Vue.nextTick(function(){
				options.getPrivileges(1); // 初始化第一页
				options.getAllPrivileges(); // 用于选择上级时的回显
			});
		},
		methods: {
			del: function(id){
				layer.msg('确定删除么？',{
					time: 0
					,shade: 0.3
					,shadeClose: true
					,btn: ['确定','取消']
					,yes: function(i){
						options.deletePrivilege(id);
						layer.close(i);
					}
				});
			},
			// 信息录入界面
			addUI: function(){
				options.addUI();
			},
			editUI: function(privilege){
				options.editUI(privilege);
			},
			// 重置表单（每当表单数据发生改变时，都需要重新渲染表单）
			resetForm: function(){
				//this.privilege = $.extend(true, {}, initPrivilege);
				this.privilege = {};
				this.$nextTick(function(){
					form.render(); // 重新渲染表单
				});
			}
		}
	});
	
	// 封装了所有操作方法（避免vue中的methods过于臃肿）
	var options = {
		/**
		 * 获取员工列表分页信息，可指定页码；如果调用时没有参数，则为当前页。
		 * 不使用参数的情景：修改数据、删除数据之后，希望停留在当前页面。尤其是修改数据的时候。
		 */
		getPrivileges: function(curr){
			var layIndex;
			$.ajax({
				type: 'POST',
				url: '/privilege/listData',
				async: true, // 异步
				data: {"pageNum": curr || vue.currPage, "pageSize": vue.pageSize},
				dataType: 'json',
				beforeSend: function(){layIndex = layer.load(2);},
				success: function(result){
					if(result.success){
						vue.privileges = result.data.content;
						// 分页功能
						laypage.render({
						    elem: 'layPage'
						    ,count: result.data.totalElements // 总记录数
						    ,curr: vue.currPage // 当前页
						    ,limit: vue.pageSize
						    ,jump: function(obj, first){
						      if(!first){
									vue.currPage = obj.curr;
									options.getPrivileges(obj.curr); // 跳转到指定页
								}
						    }
						  });
					}else{
						layer.msg('获取数据失败');
					}
				},
				error: function(msg){
					layer.msg('获取列表失败，与服务器连接已断开。');
				},
				complete: function(xmlHttpRequest, strStatus){
					layer.close(layIndex); // 关闭弹出层
				}
			});
		},
		getAllPrivileges: function(){ // 获取所有用户（添加和修改项目时需要用到）
			$.ajax({
				type: 'POST',
				url: '/privilege/listAll',
				beforeSend: function(){
					layIndex = layer.load(2);
				},
				success: function(result){
					if(result.success){
						vue.allPrivileges = result.data;
					}else{
						layer.msg('获取数据失败');
					}
				},
				error: function(msg){
					layer.msg('获取数据失败，与服务器连接已断开。');
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
            	title: '新增资源信息',
            	area: ['600px', 'auto'], //宽高
            	content: $("#form-layer")
			});
			vue.privilege = {};
			vue.$nextTick(function(){
				form.render(); // layui的表单数据发生改变时，都需要重新渲染表单，否则数据的改变不会在表单上体现出来。
			});
		},
		
		// 修改界面（首先弹出表单，然后将user的数据绑定到表单中）
		editUI: function(privilege){
			layIndex = layer.open({
            	type: 1,
            	title: '更新资源信息',
            	area: ['600px', 'auto'], //宽高
            	content: $("#form-layer")
			});
			// 回显待修改的权限信息
			vue.privilege = {
					id: privilege.id,
					name: privilege.name,
					url: privilege.url,
					description: privilege.description,
					privilegeType: privilege.privilegeType,
					parentId: privilege.parentId
				};
			
			vue.$nextTick(function(){ // 在DOM渲染完成之后执行
				form.render(); // 重新渲染表单。更新全部
			});
		},
		save: function(){
			$.ajax({
				type: 'POST',
				url: '/privilege/save',
				async: true,
				dataType: 'json',
				data: $('#form1').serializeArray(),
				success: function(result,strStatus){
					if(result.success){
						options.getPrivileges(); // 重新加载当前页
						options.getAllPrivileges(); // 用于选择上级时的回显
						layer.msg("success",{icon:6});
					}else{
						layer.msg("保存失败",{icon:5});
					}
				},
				error: function(msg){
					layer.msg('操作失败，与服务器连接断开。',{icon:5});
				}
			});
		},
		deletePrivilege: function(id){ // 根据id删除一条员工记录
			$.ajax({
				type: 'POST',
				url: '/privilege/del',
				dataType: 'json',
				data: {"id":id},
				success: function(result,strStatus){
					if(result.success){
						options.getPrivileges(); // 重新加载用户列表
						options.getAllPrivileges(); // 用于选择上级时的回显
						layer.msg("success",{icon:6});
					}else{
						layer.msg(result.msg,{icon:5});
					}
				},
				error: function(msg){
					layer.msg('操作失败，与服务器连接断开。',{icon:5});
				}
			});
		}
	};
	
	//监听提交表单
    form.on('submit(sb1)', function(data){
    	options.save();
        layer.close(layIndex);
        return false;
    });
    
});	