layui.use(['jquery','layer','form','laydate','laypage'],function(){
	
	var layer = layui.layer
		,$ = layui.jquery
		,form = layui.form
		,laypage = layui.laypage
		,laydate = layui.laydate;
	
	var layIndex = -1;
	
	var vue = new Vue({
		el: '#app',
		data: {
			currPage: 1, // 当前页
			pageSize: 15, // 每页显示多少条记录
			project: {},
			projects: [],
			allUsers: []
		},
		computed: {
		},
		created: function(){
			Vue.nextTick(function(){
				options.getProjects(1); // 初始化第一页
				options.getAllUsers();
			});
		},
		methods: {
			editEmp: function(projectId){ // 编辑组员
				options.editEmp(projectId);
			},
			addUI: function(){ // 增加项目（弹出表单）
				options.addUI();
			},
			editUI: function(project){ // 编辑项目
				options.editUI(project);
			},
			del: function(id){ // 删除项目
				layer.msg('确定删除么？',{
					time: 0
					,shade: 0.3
					,shadeClose: true
					,btn: ['确定','取消']
					,yes: function(i){
						options.delProject(id);
						layer.close(i);
					}
				});
			}
		}
	});
	
	
	var options = {
		getProjects: function(curr){ // 获取项目列表
			$.ajax({
				type: 'POST',
				url: '/project/projectsPage',
				data: {"pageNum": curr-1 || vue.currPage-1, "pageSize": vue.pageSize},
				dataType: 'json',
				beforeSend: function(){
					layIndex = layer.load(2);
				},
				success: function(result){
					if(result.success){
						vue.projects = result.data.content;
					}else{
						layer.msg('获取数据失败');
					}
					laypage.render({
					    elem: 'layPage'
					    ,count: result.data.totalElements // 总记录数
					    ,curr: vue.currPage // 当前页
					    ,limit: vue.pageSize
					    ,jump: function(obj, first){
					      if(!first){
								vue.currPage = obj.curr;
								options.getProjects(obj.curr); // 跳转到指定页
							}
					    }
					  });
				},
				error: function(msg){
					layer.msg('获取数据失败，与服务器连接已断开。');
				},
				complete: function(xmlHttpRequest, strStatus){
					layer.close(layIndex); // 关闭弹出层
				}
			});
		},
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
		save: function(){ // 保存项目信息
			$.ajax({
	            url : '/project/save',
	            type : 'post',
	            async: true,
	            data: $('#form1').serializeArray(),
	            dataType: 'json',
	            success : function(data) {
	                if(data == "success"){
	                	layer.msg("success",{icon:6});
	                	options.getProjects();
	                	options.getAllUsers();
	                }else{
	                	layer.msg(JSON.stringify(data),{icon:5});
	                }
	            },error:function(data){
	            	layer.msg('操作失败，与服务器连接断开。',{icon:5});
	            }
	        });
		},
		editEmp: function(projectId){ // 编辑组员
			layIndex = layer.open({
            	type: 2,
            	title: '配置组员',
            	maxmin: false,
            	area: ['500px', '450px'],
            	content: '/project/allocateUsers?projectId=' + projectId
			});
		},
		addUI: function(){ // 增加项目（弹出表单）
			layIndex = layer.open({
            	type: 1,
            	title: '增加项目',
            	area: ['810px', '580px'],
            	content: $('#form-layer')
			});
			// 使用初始化项目对象，由于使用了双向数据绑定，因此表单也会初始化。
			//vue.project = $.extend(true, {}, initProject);
			vue.project = {};
			vue.$nextTick(function(){
				form.render(); // layui的表单数据发生改变时，都需要重新渲染表单，否则数据的改变不会在表单上体现出来。
			});
		},
		editUI: function(project){ // 编辑项目
			layIndex = layer.open({
            	type: 1,
            	title: '修改项目',
            	area: ['810px', '580px'], //宽高
            	content: $('#form-layer')
			});
			vue.project = project;
			vue.$nextTick(function(){ // 在DOM渲染完成之后执行
				form.render(); // 重新渲染表单。更新全部
			});
		},
		delProject: function(id){
			$.ajax({
				type: 'POST',
				url: '/project/del',
				dataType: 'json',
				data: {"id":id},
				success: function(result,strStatus){
					if(result.success){
						//location.reload();
						options.getProjects(); // 只需要重新获取项目列表
						options.getAllUsers();
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
	
	//日期
    laydate.render({
      elem: '#beginDate',
      format: 'yyyy-MM-dd HH:mm:ss'
    });
    
	//日期
    laydate.render({
      elem: '#endDate',
      format: 'yyyy-MM-dd HH:mm:ss'
    });

	
	// 提交表单
	form.on('submit(sb1)', function(data){
		options.save();
		layer.close(layIndex);
        return false;
	});
	
	// 监听性别select下拉选择事件
    form.on('select(pmId)', function(data){
	  vue.project.pmName = data.value; // 鉴于layui下拉选择框的特殊性，需要手动给vue.user.gender赋值。
	});
    
	// 监听性别select下拉选择事件
    form.on('select(qaId)', function(data){
	  vue.project.pmName = data.value; // 鉴于layui下拉选择框的特殊性，需要手动给vue.user.gender赋值。
	});
    
	// 监听是否未结项select下拉选择事件
    form.on('select(active)', function(data){
	  vue.project.active = data.value; // 鉴于layui下拉选择框的特殊性，需要手动给vue.user.gender赋值。
	});
	
});