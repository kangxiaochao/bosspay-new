var myTbContentId = 'mytbc1';
var myJqTbContentId = '#' + myTbContentId;
var myTbId = 'myt1';
var myJqTbId = '#' + myTbId;
var myPageId = 'myp1';
var myJqPageId = '#' + myPageId;

$(function(){
	getAgentId();
});

function submit(){
	var money = $("#money").val();
	var agentId = $("#agentId").val();
	if(money >= 100){
		$.ajax({
			type : "get",
			url : "WeChatPay",
			data:{'money':money,'agentId':agentId},
			scriptCharset : 'utf-8',
			success : function(data) {
				$("#ds").html("<img src='"+data+"'/><button type='button' onclick='submits()' style='margin:0 800px;' class='btn btn-primary'>完成支付</button>");
			}
		});
	}else{
		outMessage('warning','加款金额低于一百,不予处理！','友情提示');
	}
}

function submits(){
	var money = $("#money").val();
	var agentId = $("#agentId").val();
	$.ajax({
		type : "get",
		url : "addAgentMoney",
		data:{'money':money,'agentId':agentId},
		scriptCharset : 'utf-8',
		success : function(data) {
			if(data == 0){
				location.href="mainPage";
			}else{
				outMessage('warning','加款失败,如已付款成功请联系值班客服！','友情提示');
			}
		}
	});
}
