$(function() {
	setMyActive(9, 3); // 设置激活页

	message();
});

/*绑定日期控件*/
laydate({
	elem: '#startDate',//目标元素。由于laydate.js封装了一个轻量级的选择器引擎，因此elem还允许你传入class、tag但必须按照这种方式 '#id .class'
	event: 'focus', 	//响应事件。如果没有传入event，则按照默认的click
	format: 'YYYY-MM-DD hh:mm:ss', //日期格式
	istime: true, //是否开启时间选择
});

/*绑定日期控件*/
laydate({
	elem: '#endDate',	//目标元素。由于laydate.js封装了一个轻量级的选择器引擎，因此elem还允许你传入class、tag但必须按照这种方式 '#id .class'
	event: 'focus',		//响应事件。如果没有传入event，则按照默认的click
	format: 'YYYY-MM-DD hh:mm:ss', //日期格式
	istime: true, //是否开启时间选择
});

// 切换海航币账号
function initTimer(obj) {
	layer.confirm('您确认要切换海航币账号信息吗?', {skin: 'layui-layer-molv',icon: 3, title:'提示'}, function(index){
		var myDelUrl = basePath + 'shortcut/haihangbi/initTimer';
		$.ajax({
			type : 'get',
			url : myDelUrl,
			success : function(dt) {
				outMessage('success', dt, '友情提示');
			},
		});
		layer.close(index);
//		disabledSubmitButton(obj.id);
		disabledSubmitButton02(obj.id, obj.innerText);
	});
}

// 刷新兔兔币cookies
function refreshCookies(obj){
	$.ajax({
		type : 'get',
		url : basePath + "tutubiCookies",
		success : function(dt) {
			outMessage('success', dt, '友情提示');
		},
	});
	disabledSubmitButton02(obj.id, obj.innerText);
}
// 刷新用友cookies
function refreshYongYouCookies(obj){
	$.ajax({
		type : 'get',
		url : basePath + "yongYouCookies",
		success : function(dt) {
			outMessage('success', dt, '友情提示');
		},
	});
	disabledSubmitButton02(obj.id, obj.innerText);
}

//切换海航币账号
function updateKey(obj) {
	layer.confirm('您确认要更新迪信通密钥信息吗?', {skin: 'layui-layer-molv',icon: 3, title:'提示'}, function(index){
		var myDelUrl = basePath + 'shortcut/dixintong/updateKey';
		$.ajax({
			type : 'get',
			url : myDelUrl,
			success : function(dt) {
				outMessage('success', dt, '友情提示');
			},
		});
		layer.close(index);
//		disabledSubmitButton(obj.id);
		disabledSubmitButton02(obj.id, obj.innerText);
	});
}

/*按钮禁用10秒*/
function disabledSubmitButton(submitButtonName){
	$("#"+submitButtonName).attr({"disabled":"disabled"});	   //控制按钮为禁用
	var timeoutObj = setTimeout(function () {
		$("#"+submitButtonName).removeAttr("disabled");//将按钮可用
		/* 清除已设置的setTimeout对象 */
		clearTimeout(timeoutObj)
    }, 10000);
}

/*按钮禁用10秒,并显示倒计时*/
function disabledSubmitButton02(submitButtonName, submitButtonText){
	$("#"+submitButtonName).attr({"disabled":"disabled"});	   //控制按钮为禁用
	var second = 10;
	var intervalObj = setInterval(function () {
		$("#"+submitButtonName).text(submitButtonText + "(" + second + ")");
		if(second == 0){
			$("#"+submitButtonName).text(submitButtonText);
			$("#"+submitButtonName).removeAttr("disabled");//将按钮可用
			/* 清除已设置的setInterval对象 */
			clearInterval(intervalObj);
		}
		second--;
	}, 1000 ); 
}

/* 导出月度新加款客户*/
function exportNewCustomerAgent(obj){
	location.href = basePath + "/exportNewCustomerAgent?startDate=" + $("#startDate").val() + "&endDate=" + $("#endDate").val();
	disabledSubmitButton02(obj.id, obj.innerText);
}

/* 导出月度加款客户*/
function exportMonthlyAddMoneyAgent(obj){
	location.href = basePath + "/exportMonthlyAddMoneyAgent?startDate=" + $("#startDate").val() + "&endDate=" + $("#endDate").val();
	disabledSubmitButton02(obj.id, obj.innerText);
}

