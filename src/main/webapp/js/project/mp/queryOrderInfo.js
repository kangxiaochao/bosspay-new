$(function(){
	setMyActive(3,7);
});
//查询方法
function search(){
	var data = $("#searchForm").serialize();
	if(($("#agentOrderId").val() == null || $("#agentOrderId").val() == '') && ($("#phone").val() == null || $("#phone").val() == '')){
		outMessage('warning', '查询条件不能为空', '友情提示');
	}else {
		$("#content").show();
		$.ajax({
			type:'get',
			url:basePath + 'queryOrderInfo?'+data,
			dataType:'json',
			success:function(data){
				if(data.submitOrder != null && data.submitOrder.length > 0){
					$("#submitOrder_tbody").empty();
					var submitOrder = data.submitOrder;
					for(var i = 0 ; i < submitOrder.length ; i++){
						//订单提交记录
						$("#submitOrder_tbody").append(
								"<tr><td>" + submitOrder[i].agentName + "</td>"
										+ "<td>" + submitOrder[i].agentOrderId
										+ "</td>" + "<td>"
										+ submitOrder[i].phone + "</td>"
										+ "<td>" + submitOrder[i].resultCode
										+ "</td>" + "<td>"
										+ submitOrder[i].submitDate + "</td></tr>");
					}
				}else{
					$("#submitOrder_div").empty();
					$("#submitOrder_div").append("未查到相关记录");
				}
				if(data.order != null && data.order.length > 0){
					$("#order_tbody").empty();
					var order = data.order;
					for(var i = 0 ; i < order.length ; i++){
						//订单
						$("#order_tbody").append(
								"<tr><td>" + order[i].orderId + "</td>" + "<td>"
										+ order[i].agentName + "</td>" + "<td>"
										+ order[i].agentOrderId + "</td>" + "<td>"
										+ order[i].channelName + "</td>" + "<td>"
										+ order[i].providerOrderId + "</td>" + "<td>"
										+ order[i].providerName + "</td>" + "<td>"
										+ order[i].phone + "</td>" + "<td>"
										+ order[i].fee + "</td>" + "<td>"
										+ getStatus(order[i].status) + "</td>" + "<td>"
										+ order[i].resultCode + "</td>" + "<td>"
										+ order[i].applyDate + "</td>" + "<td>"
										+ order[i].endDate + "</td></tr>");
					}
				}else{
					$("#order_div").empty();
					$("#order_div").append("未查到相关记录");
				}
				if(data.exceptionOrder != null && data.exceptionOrder.length > 0){
					$("#exceptionOrder_tbody").empty();
					var exceptionOrder = data.exceptionOrder;
					for(var i = 0 ; i < exceptionOrder.length ; i++){
						//订单
						$("#exceptionOrder_tbody").append(
								"<tr><td>" + exceptionOrder[i].orderId + "</td>" + "<td>"
										+ exceptionOrder[i].agentName + "</td>" + "<td>"
										+ exceptionOrder[i].agentOrderId + "</td>" + "<td>"
										+ exceptionOrder[i].channelName + "</td>" + "<td>"
										+ exceptionOrder[i].providerOrderId + "</td>" + "<td>"
										+ exceptionOrder[i].providerName + "</td>" + "<td>"
										+ exceptionOrder[i].phone + "</td>" + "<td>"
										+ exceptionOrder[i].fee + "</td>" + "<td>"
										+ getStatus(exceptionOrder[i].status) + "</td>" + "<td>"
										+ exceptionOrder[i].resultCode + "</td>" + "<td>"
										+ exceptionOrder[i].applyDate + "</td>" + "<td>"
										+ exceptionOrder[i].endDate + "</td></tr>");
					}
				}else{
					$("#exceptionOrder_div").empty();
					$("#exceptionOrder_div").append("未查到相关记录");
				}
				if(data.orderPathRecord != null && data.orderPathRecord.length > 0){
					$("#orderPC_tbody").empty();
					var orderPathRecord = data.orderPathRecord;
					for(var i = 0 ; i < orderPathRecord.length ; i++){
						//订单
						$("#orderPC_tbody").append(
								"<tr><td>" + orderPathRecord[i].orderId + "</td>" + "<td>"
										+ orderPathRecord[i].agentName + "</td>" + "<td>"
										+ orderPathRecord[i].agentOrderId + "</td>" + "<td>"
										+ orderPathRecord[i].channelName + "</td>" + "<td>"
										+ orderPathRecord[i].providerOrderId + "</td>" + "<td>"
										+ orderPathRecord[i].providerName + "</td>" + "<td>"
										+ orderPathRecord[i].phone + "</td>" + "<td>"
										+ orderPathRecord[i].fee + "</td>" + "<td>"
										+ getStatus(orderPathRecord[i].status) + "</td>" + "<td>"
										+ orderPathRecord[i].resultCode + "</td>" + "<td>"
										+ orderPathRecord[i].applyDate + "</td>" + "<td>"
										+ orderPathRecord[i].endDate + "</td></tr>");
					}
				}else{
					$("#orderPC_div").empty();
					$("#orderPC_div").append("未查到相关记录");
				}
				if(data.agentAccountCharge != null && data.agentAccountCharge.length > 0){
					$("#agentAC_tbody").empty();
					var agentAccountCharge = data.agentAccountCharge;
					for(var i = 0 ; i < agentAccountCharge.length ; i++){
						//订单提交记录
						$("#agentAC_tbody").append(
								"<tr><td>" + agentAccountCharge[i].agentName + "</td>"
										+ "<td>" + agentAccountCharge[i].agentOrderId
										+ "</td>" + "<td>"
										+ agentAccountCharge[i].fee + "</td>"
										+ "<td>" + agentAccountCharge[i].applyDate
										+ "</td>" + "<td>"
										+ agentAccountCharge[i].endDate + "</td></tr>");
					}
				}else{
					$("#agentAC_div").empty();
					$("#agentAC_div").append("未查到相关记录");
				}
			}
		});
	}
}

function getStatus(status){
	if(status == 0){
		return "处理中";
	}else if(status == 1){
		return "提交成功";
	}else if(status == 2){
		return "提交失败";
	}else if(status == 3){
		return "充值成功";
	}else if(status == 4){
		return "充值失败";
	}
}