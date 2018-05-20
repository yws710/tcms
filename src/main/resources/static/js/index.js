layui.use(['layer', 'element', 'util', 'form'], function () {
    var element = layui.element, layer = layui.layer, $ = layui.jquery, util = layui.util, form = layui.form; //导航的hover效果、二级菜单等功能，需要依赖element模块
    var side = $('.my-side');
    var body = $('.my-body');
    
    var layIndex = null; // 弹出层索引
    
    // 修改密码
    $('#editPwd').click(function(){
    	layIndex = layer.open({
        	type: 1,
        	title: '修改登录密码',
        	area: ['750px', 'auto'],
        	content: $('#form-layer')
		});
    });
    
	//自定义验证规则  
    form.verify({  
    	password: [/^[\S]{6,16}$/, '密码必须6到16位，且不能出现空格'],
    	password2: function(value){
            if(value !== $('#newPwd').val()){
              return '两次密码输入不一致！';
            }
		}
    });
	
    var options = {
    	save: function(){
			$.ajax({
                url : 'editPwd',
                type : 'post',
                async: true,
                data: $('#form1').serializeArray(),
                dataType: 'json',
                success: function(result,strStatus){
					if(result.success){
						//layer.msg("密码修改成功，请重新登录。",{icon:6});
						 //location.reload(); // 刷新当前页
						layer.msg('密码修改成功，请重新登录。',{
							time: 0
							,shade: 0.3
							,skin: 'layui-layer-molv'
							,shadeClose: false
							,btn: ['确定']
							,btnAlign: 'c' // 按钮居中对齐
							,yes: function(i){
								location.reload(); // 刷新当前页
							}
						});
					}else{
						layer.msg(JSON.stringify(result.msg),{icon:5});
					}
				},
				error: function(msg){
					console.log(JSON.stringify(msg));
					layer.msg('操作失败，与服务器连接断开。',{icon:5});
				}
            });
    	}
    }
	
	// 提交表单
	form.on('submit(sb1)', function(data){
		options.save();
		layer.close(layIndex);
        return false;
	});

   

   

	//监听导航(side)点击切换页面
    element.on('nav(side)', function (elem) {
        var card    = 'card';                                   // 选项卡对象
        var title   = elem.text();                              // 导航栏text
        var src     = elem.children('a').attr('href-url');      // 导航栏跳转URL
        var id      = new Date().getTime();                     // ID
        var flag    = getTitleId(card, title);                  // 是否有该选项卡存在
        // 大于0就是有该选项卡了
        if(flag > 0){
            id = flag;
        }else{
            //新增
            element.tabAdd(card, {
                title: '<span>'+title+'</span>'
                , content: '<iframe src="' + src + '" frameborder="0"></iframe>'
                , id: id
            });
        }
        // 切换相应的ID tab
        element.tabChange(card, id);
    });

	// 根据导航栏text获取lay-id
    function getTitleId(card,title) {
        var id = -1;
        $(document).find(".layui-tab[lay-filter=" + card + "] ul li").each(function(){
            if(title === $(this).find('span').text()){
                id = $(this).attr('lay-id');
            }
        });
        return id;
    }

});