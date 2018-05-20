layui.use(['jquery','layer','form'],function(){
	var $ = layui.jquery
		,layer = layui.layer
		,form = layui.form;
	
	var layIndex = null; // 弹出层索引
	
	var vue = new Vue({
		el: '#app',
		data: {
			roles: [],
			role: {id: '', name:'', description: ''}
		},
		computed: {
		},
		created: function(){
			Vue.nextTick(function(){
				options.getRoles();
			});
		},
		methods: {
			addUI: function(){ // 增加角色
				options.addUI();
			},
			editUI: function(role){ // 编辑角色
				options.editUI(role);
			},
			del: function(id){ // 删除角色
				options.del(id);
			},
			// 重置表单（每当表单数据发生改变时，都需要重新渲染表单）
			resetForm: function(){
				// id必须保持不变，否则会导致修改变成新增。
				this.role = {id: this.role.id, name:'', description: ''};
				this.$nextTick(function(){
					form.render(); // 重新渲染表单
				});
			}
		}
	});
	
	// 封装了一些操作方法（避免vue中的methods过于臃肿）
	var options = {
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
		addUI: function(){ // 增加角色
			layIndex = layer.open({
            	type: 1,
            	title: '增加角色',
            	area: ['600px', 'auto'],
            	content: $('#form-layer')
			});
			vue.role = {id: '', name:'', description: ''};
			vue.$nextTick(function(){
				form.render(); // layui的表单数据发生改变时，都需要重新渲染表单，否则数据的改变不会在表单上体现出来。
			});
		},
		editUI: function(role){ // 编辑角色
			layIndex = layer.open({
            	type: 1,
            	title: '修改角色',
            	area: ['600px', 'auto'],
            	content: $('#form-layer')
			});
			vue.role = {id: role.id, name: role.name, description: role.description};
			vue.$nextTick(function(){
				form.render(); // layui的表单数据发生改变时，都需要重新渲染表单，否则数据的改变不会在表单上体现出来。
			});
		},
		save: function(){ // 保存一个角色
			$.ajax({
                url : '/role/save',
                type : 'post',
                async: true,
                data: vue.role,
                dataType: 'json',
                success: function(result,strStatus){
					if(result.success){
						options.getRoles(); // 重新加载当前页
						layer.msg("success",{icon:6});
					}else{
						layer.msg(JSON.stringify(result.msg),{icon:5});
					}
				},
				error: function(msg){
					layer.msg('操作失败，与服务器连接断开。',{icon:5});
				}
            });
		},
		del: function(id){ // 删除角色
			layer.msg('确定删除么？',{
				time: 0
				,shade: 0.3
				,shadeClose: true
				,btn: ['确定','取消']
				,yes: function(i){
					$.ajax({
						type: 'POST',
						url: 'role/del',
						dataType: 'json',
						data: {"id": id},
						success: function(result,strStatus){
							if(result.success){
								options.getRoles(); // 重新加载用户列表
								layer.msg("success",{icon:6});
							}else{
								layer.msg(JSON.stringify(result.msg),{icon:5});
							}
						},
						error: function(msg){
							layer.msg('操作失败，与服务器连接断开。',{icon:5});
						}
					});
				}
			});
		}
	};
	
	// 提交表单
	form.on('submit(sb1)', function(data){
		options.save();
		layer.close(layIndex);
        return false;
	});
});