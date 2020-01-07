

$(function(){
	$('#suPass').focus();
	setMyActive(1,1);
});

function submitChangePass(){
	var myFormActionUrl=$('#fm').attr("action");
	var suId=$('#suId').val();
	var suPass=$('#suPass').val();
	var suPass1=$('#suPass1').val();
	
	if(suPass!=suPass1){
		outMessage('warning','请确认两次密码输入是否一致！','友情提示');
		return false;
	}else{
	
	$.ajax({
		type:'PATCH',
		url :myFormActionUrl,
		data:{ suId: suId,suPass:suPass },
		success:function (dt){
			location.href=basePath+dt;
	    },
	    error:function (rs){
	    	if(XMLHttpRequest.status=405){
	    		outMessage('warning','您没有权限操作该资源请联系管理员！','友情提示');
	    	}
	    },
	    dataType: 'html'
	});
	}
		
}
