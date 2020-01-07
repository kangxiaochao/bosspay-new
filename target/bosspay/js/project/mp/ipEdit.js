
$(function(){
	$('#title').focus();
	setMyActive(2,9); //设置激活页
});

// 更新公告信息到数据库
function submitEdit(){
	var myFormActionUrl = $('#fm').attr("action");
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