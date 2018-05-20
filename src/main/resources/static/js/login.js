layui.use(['form', 'layer'], function () {
    var $ = layui.jquery
    	,form = layui.form
    	,layer = layui.layer;
    
    
    
    // 监听表单提交
    form.on('submit(sub)', function (data) {
        $.ajax({
        	type: 'POST',
        	url: 'loginUser',
        	data: data.field,
        	DataType: 'json',
        	success: function(result,strStatus){
        		if(result.success){
        			layer.msg("success",{icon:6});
        			window.location.href = "index";
        		}else{
        			layer.msg(result.msg,{icon:5,time: 3000,shade:0.3,anim: 6});
        		}
        	},
        	error: function(xmlHttpRequest, strError,strObject){
        		layer.msg('操作失败，与服务器连接已断开。',{icon:5});
        	}
        });
        
        return false;
    })
})