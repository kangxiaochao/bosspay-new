$(function(){
	$('#name').focus();
	setStatus();
//	getUserList();
	getAgentList();
	getBillGroupList();
	getDataGroupList();
	getChannelPersonList();
	getBillDiscountModelList();
	getDataDiscountModelList();
	setMyActive(5,1); //设置激活页
});
function getChannelPersonList(){
	var oldChannelPerson = $("#oldChannelPerson").val();
	var channelPerson = $("#channelPerson");
	var myDelUrl = basePath + 'getAllChannelPerson';
	var option = $("<option>").text('无').val('');
	channelPerson.append(option);
	$.ajax({
		type:'get',
		url :myDelUrl,
		success:function (dt){
			$.each(dt, function(index,val) {  
				option = $("<option>").text(val.suName).val(val.suId)
				channelPerson.append(option);
			});
			channelPerson.val(oldChannelPerson);
			setSelectStyle(channelPerson);
		},
		dataType: 'json'
	});
}
/*设定启用状态*/
function setStatus(){
	var oldStatus = $("#oldStatus").val();
	if(oldStatus == 0){
		$('[type="checkbox"]').bootstrapSwitch('state', false);
	}else{
		$('[type="checkbox"]').bootstrapSwitch();
	}
}

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

/*获取用户列表*/
function getUserList(){
	var oldUserId = $("#oldUserId").val();
	var userId = $("#userId");
	var option = $("<option>").text('无').val('');
//	parentId.append(option);
	
	var myDelUrl = basePath + 'sysUsers';
	$.ajax({
		type:'get',
		url :myDelUrl,
		success:function (dt){
			$.each(dt, function(index,val) {
				option = $("<option>").text(val.suName).val(val.suId)
				userId.append(option);
			});
			userId.val(oldUserId);
			setSelectStyle(userId);
		},
		dataType: 'json'
	});
}

/*获取代理商列表*/
function getAgentList(){
//	var oldParentId = $("#oldParentId").val();
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
//			parentId.val(oldParentId);
			setSelectStyle(parentId);
	    },
	    dataType: 'json'
	});
}

/*获取话费通道组列表*/
function getBillGroupList(){
	var oldBillGroupId = $("#oldBillGroupId").val();
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
			billGroupId.val(oldBillGroupId);
			setSelectStyle(billGroupId);
		},
		dataType: 'json'
	});
}

/*获取话费通道组列表*/
function getDataGroupList(){
	var oldDataGroupId = $("#oldDataGroupId").val();
	var dataGroupId = $("#dataGroupId");
	var myDelUrl = basePath + 'providerDataGroupAll';
	var option = $("<option>").text('无').val('');
	dataGroupId.append(option);
	
	$.ajax({
		type:'get',
		url :myDelUrl,
		success:function (dt){
			$.each(dt, function(index,val) {
				option = $("<option>").text(val.name).val(val.id)
				dataGroupId.append(option);
			});
			dataGroupId.val(oldDataGroupId);
			setSelectStyle(dataGroupId);
		},
		dataType: 'json'
	});
}

/*获取代理商话费折扣模板列表*/
function getBillDiscountModelList(){
	var oldBillModelId = $("#oldBillModelId").val();
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
			billModelId.val(oldBillModelId);
			setSelectStyle(billModelId);
		},
		dataType: 'json'
	});
}

/*获取代理商话费折扣模板列表*/
function getDataDiscountModelList(){
	var oldDataModelId = $("#oldDataModelId").val();
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
			dataModelId.val(oldDataModelId);
			setSelectStyle(dataModelId);
		},
		dataType: 'json'
	});
}

function submitEdit(){
	var id = $('#id').val();
	var myFormActionUrl = basePath + 'agent/' + id;
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

