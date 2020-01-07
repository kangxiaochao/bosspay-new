$(function(){
	$('#appKey').focus();
	setMyActive(5,1); //设置激活页
});

/*设置代理商密钥信息*/
function submitEdit(){
	var id = $('#id').val();
	var myFormActionUrl = basePath + 'agentKey/' + id;
	var data = $("form").serialize();
	
	$.ajax({
		type:'PUT',
		url :myFormActionUrl,
		data:data,
		success:function (dt){
			location.href=basePath+dt;
	    },
	    dataType: 'html'
	});
}

