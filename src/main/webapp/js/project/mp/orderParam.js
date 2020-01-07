
$(function() {
	
});

/*获取代理商列表*/
function orderParamQuery(){
	var agentOrderId = $("#agentOrderId").val().replace(/[ ]/g,"");
	var phone = $("#phone").val().replace(/[ ]/g,"");	//获取电话号码并去除其中的空格
	var orderType = $("#orderType").val();
	
	var myDelUrl = basePath + "orderParam";	// 订单列表参数
	if(orderType == "2"){
		myDelUrl = basePath + "exceptionOrderParam";	// 异常订单列表参数
	}else if(orderType == "3"){
		myDelUrl = basePath + "orderPathRecordParam";	// 订单流水列表参数
	}
	
	if(agentOrderId != "" || phone != ""){
		$("#phoneLable").empty();
		$("#agentNameLable").empty();
		$("#agentDiscountLable").empty();
		$("#areaLable").empty();
		$("#providerNameLable").empty();
		$("#groupNameLable").empty();
		$("#yesOrNoLable").empty();
		$("#pkgNameLable").empty();
		$("#chanleNamesLable").empty();
		
		$.ajax({
			type:'get',
			url :myDelUrl,
			data:{ agentOrderId: agentOrderId, phone:phone},
			success:function (dt){
				if(dt == null){
					outMessage('error','没有获取到数据信息！','友情提示');
				}else{
					$("#phoneLable").html(dt.phone);
					$("#agentNameLable").html(dt.agentName);
					$("#agentDiscountLable").html(dt.agentDiscount);
					$("#areaLable").html(dt.provinceCode+' '+dt.cityCode);
					$("#providerNameLable").html(dt.providerName);
					$("#groupNameLable").html(dt.groupName);
					$("#yesOrNoLable").html(dt.yesOrNo);
					$("#pkgNameLable").html(dt.pkg.name);
					
					var dispatcherParam = dt.dispatcherParam;
					var chanleName = "";
					for (var int = 0; int < dispatcherParam.length; int++) {
						chanleName = '['+dispatcherParam[int].province_code+'-'+
									 dispatcherParam[int].name+'-'+
									 dispatcherParam[int].discount.toFixed(3)+']&nbsp;&nbsp;';
						$("#chanleNamesLable").append(chanleName);
					}
				}
			},
			dataType: 'json'
		});
	}else{
		outMessage('warning','请输入查询条件','友情提示');
	}
}

