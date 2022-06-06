$(function(){
	setMyActive(5,1); //设置激活页
});

function submitForm(){
	$("#submit").hide();
	var userId = $('#leftMenuDiv').attr('data-suId');
	
	$.ajax({
		type:'get',
		url :basePath + 'sysGetHasRole/'+userId,
		async: false,
		data:{},
		dataType: 'json',
		success:function (dt){
			var roleName;
			for(var i in dt){
				roleName = dt[i]["srName"];
				if(roleName == "代理商"){
					break;
				}
			}
			if(roleName == "代理商"){
				$("#fm").attr("action",basePath+"allotProfit");
			}
			$("#fm").submit();
		}
	})
}