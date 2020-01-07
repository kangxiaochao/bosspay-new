
$(function(){
	$('#title').focus();
	init();
	setSelectStyle($("#mqqueuename"));
	setMyActive(2,10); //设置激活页
});

// 更新公告信息到数据库
function submitEdit(){
	var displayName = $("#mqqueuename").find("option:selected").text();
	$("#mqqueuedisplayname").val(displayName);
	
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

/*初始化已经被选中的MQ通道*/
function init(){
	var oldMqqueuename = $("#oldMqqueuename").val();
	$("#mqqueuename").val(oldMqqueuename);
}