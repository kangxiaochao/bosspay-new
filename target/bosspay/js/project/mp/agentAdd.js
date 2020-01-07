$(function(){
	$('#name').focus();
	//getAgentList();
	getBillGroupList();
	getDataGroupList();
	getBillDiscountModelList();
	getDataDiscountModelList();
	getChannelPersonList();
	setMyActive(5,1); //设置激活页
//	alert($("#agentRole").attr("data-agentRole"))
});
$('[type="checkbox"]').bootstrapSwitch('state', true);

/*绑定日期控件*/
laydate({
	elem: '#startDate',//目标元素。由于laydate.js封装了一个轻量级的选择器引擎，因此elem还允许你传入class、tag但必须按照这种方式 '#id .class'
	event: 'focus', 	//响应事件。如果没有传入event，则按照默认的click
	min: laydate.now(), //设定最小日期为当前日期
});

/*绑定日期控件*/
laydate({
	elem: '#endDate',	//目标元素。由于laydate.js封装了一个轻量级的选择器引擎，因此elem还允许你传入class、tag但必须按照这种方式 '#id .class'
	event: 'focus',		//响应事件。如果没有传入event，则按照默认的click
	min: laydate.now(), //设定最小日期为当前日期
});

/*设置授信额文本域样式*/
$("#credit").TouchSpin({
    buttondown_class: 'btn btn-white',
    buttonup_class: 'btn btn-white'
});

/*检测代理商名字是否重复*/
function checkAgentName(name){
	$.ajax({
		type:'get',
		url:basePath+'agentNameCheck',
		data:{name:name},
		success:function(dt){
			if(dt=='yes'){
				$("#agentNameMsg").html('代理商已存在');
			}else{
				$("#agentNameMsg").empty();
			}
		}
	});
}

/*获取代理商列表*/
function getAgentList(){
	var parentId = $("#parentId");
	var option = $("<option>").text('无').val('');
	parentId.append(option);
	
	var myDelUrl = basePath + 'agentAll';
	$.ajax({
		type:'get',
		url :myDelUrl,
		data:{ status: '1' },
		success:function (dt){
			$.each(dt, function(index,val) {
		    	option = $("<option>").text(val.name).val(val.id)
		    	parentId.append(option);
		    });
			setSelectStyle(parentId);
	    },
	    dataType: 'json'
	});
}

/*获取话费通道组列表*/
function getBillGroupList(){
	var billGroupId = $("#billGroupId");
	var myDelUrl = basePath + 'providerBillGroupAll';
	var option = $("<option>").text('无').val('');
	billGroupId.append(option);
	
	$.ajax({
		type:'get',
		url :myDelUrl,
		success:function (dt){
			$.each(dt, function(index,val) {
				option = $("<option>").text(val.name).val(val.id)
				billGroupId.append(option);
			});
			setSelectStyle(billGroupId);
		},
		dataType: 'json'
	});
}

/*获取话费通道组列表*/
function getDataGroupList(){
	var dataGroupId = $("#dataGroupId");
	var myDelUrl = basePath + 'providerDataGroupAll';
	var option = $("<option>").text('无').val('');
	dataGroupId.append(option);
	$.ajax({
		type:'get',
		url :myDelUrl,
		success:function (dt){
			$.each(dt, function(index,val) {  
				option = $("<option>").text(val.name        ).val(val.id)
				dataGroupId.append(option);
			});
			setSelectStyle(dataGroupId);
		},
		dataType: 'json'
	});
}
function getChannelPersonList(){
	var dataGroupId = $("#channelPerson");
	var myDelUrl = basePath + 'getAllChannelPerson';
	var option = $("<option>").text('无').val('');
	dataGroupId.append(option);
	$.ajax({
		type:'get',
		url :myDelUrl,
		success:function (dt){
			$.each(dt, function(index,val) {  
				option = $("<option>").text(val.suName).val(val.suId)
				dataGroupId.append(option);
			});
			setSelectStyle(dataGroupId);
		},
		dataType: 'json'
	});
}
/*获取代理商话费折扣模板列表*/
function getBillDiscountModelList(){
	var billModelId = $("#billModelId");
	var myDelUrl = basePath + 'billDiscountModelAll';
	var option = $("<option>").text('无').val('');
	billModelId.append(option);
	$.ajax({
		type:'get',
		url :myDelUrl,
		success:function (dt){
			$.each(dt, function(index,val) {
				option = $("<option>").text(val.name).val(val.id)
				billModelId.append(option);
			});
			setSelectStyle(billModelId);
		},
		dataType: 'json'
	});
}

/*获取代理商话费折扣模板列表*/
function getDataDiscountModelList(){
	var dataModelId = $("#dataModelId");
	var myDelUrl = basePath + 'dataDiscountModelAll';
	var option = $("<option>").text('无').val('');
	dataModelId.append(option);
	$.ajax({
		type:'get',
		url :myDelUrl,
		success:function (dt){
			$.each(dt, function(index,val) {
				option = $("<option>").text(val.name).val(val.id)
				dataModelId.append(option);
			});
			setSelectStyle(dataModelId);
		},
		dataType: 'json'
	});
}

// 表单验证
function check(){
	var suNameMsg = $("#suNameMsg").html();
	var suPass1Msg = $("#suPass1Msg").html();
	var agentNameMsg = $("#agentNameMsg").html();
	if(suNameMsg.length > 0 ){
		outMessage('error', suNameMsg, '友情提示');
		return false;
	}
	if(suPass1Msg.length > 0 ){
		outMessage('error', suPass1Msg, '友情提示');
		return false;
	}
	if(agentNameMsg.length > 0 ){
		outMessage('error', agentNameMsg, '友情提示');
		return false;
	}
}

// 验证用户名是否已存在
function checkSuName(){
	var myDelUrl = basePath + 'sysUserCount';
	var suName = $("#suName").val();
	if(suName.length > 0){
		$.ajax({
			type:'get',
			url :myDelUrl,
			data:{suName:suName},
			success:function (dt){
				if(dt > 0){
					$("#suNameMsg").html('用户名已存在');
				}else{
					$("#suNameMsg").empty();
				}
			}
		});
	}
}

// 验证用户密码是否一致
function checkSuPass(){
	var suPass = $("#suPass").val();
	var suPass1 = $("#suPass1").val();
	if(suPass.length > 0 && suPass1.length > 0){
		if(suPass==suPass1){
			$("#suPass1Msg").empty();
		}else{
			$("#suPass1Msg").html('两次输入的密码不一致');
		}
	}
}