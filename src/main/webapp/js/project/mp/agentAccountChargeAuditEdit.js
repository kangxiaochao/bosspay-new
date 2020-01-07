function submitEdit(){
	var myFormActionUrl = basePath + 'agentAccountChargeEditName';
	var data = $("#fm").serialize();
	
	$.ajax({
		type:'PUT',
		url :myFormActionUrl,
		data:data,
		success:function (dt){
			location.href=basePath+dt;
	    },
	    dataType: 'html'
	});
	setMyActive(5,3); //设置激活页
} 