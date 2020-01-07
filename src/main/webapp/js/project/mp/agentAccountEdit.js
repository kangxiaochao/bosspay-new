$(function(){
	setSelectStyle($("#moneyAccount"));
	setSelectStyle($("#type"));
	setMyActive(5,1); //设置激活页
});

function chcekType(type){
	if(type==1){
		$("#addMoney").css("display","block");
	}
	if(type==2){
		$("#addMoney").css("display","none");
	}
}

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
			for(var i in dt){
				roleName = dt[i]["srName"];
				if(roleName == "超级管理员"){
					break;
				}
			}
			if(roleName == "代理商"){
				$("#fm").attr("action",basePath+"allotBalance");
			}else if(roleName == "超级管理员"){
				$("#fm").attr("action",basePath+"agentAccountChargeAudit");
			}
			$("#fm").submit();
		}
	})
}