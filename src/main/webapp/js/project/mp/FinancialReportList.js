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

/*绑定日期控件*/
laydate({
	elem: '#a_startDate',//目标元素。由于laydate.js封装了一个轻量级的选择器引擎，因此elem还允许你传入class、tag但必须按照这种方式 '#id .class'
	event: 'focus', 	//响应事件。如果没有传入event，则按照默认的click
	format: 'YYYY-MM-DD hh:mm:ss', //日期格式
	istime: true, //是否开启时间选择
});

/*绑定日期控件*/
laydate({
	elem: '#a_endDate',	//目标元素。由于laydate.js封装了一个轻量级的选择器引擎，因此elem还允许你传入class、tag但必须按照这种方式 '#id .class'
	event: 'focus',		//响应事件。如果没有传入event，则按照默认的click
	format: 'YYYY-MM-DD hh:mm:ss', //日期格式
	istime: true, //是否开启时间选择
});

/*绑定日期控件*/
laydate({
	elem: '#b_startDate',//目标元素。由于laydate.js封装了一个轻量级的选择器引擎，因此elem还允许你传入class、tag但必须按照这种方式 '#id .class'
	event: 'focus', 	//响应事件。如果没有传入event，则按照默认的click
	format: 'YYYY-MM-DD hh:mm:ss', //日期格式
	istime: true, //是否开启时间选择
});

/*绑定日期控件*/
laydate({
	elem: '#b_endDate',	//目标元素。由于laydate.js封装了一个轻量级的选择器引擎，因此elem还允许你传入class、tag但必须按照这种方式 '#id .class'
	event: 'focus',		//响应事件。如果没有传入event，则按照默认的click
	format: 'YYYY-MM-DD hh:mm:ss', //日期格式
	istime: true, //是否开启时间选择
});

$(function() {
	chennelList();
//	agentList();
    setMyActive(7,5); //设置激活页
});

function chennelList(){
	var myTbContentId = 'mytbc1';
	var myJqTbContentId = '#' + myTbContentId;
	var myTbId = 'myt1';
	var myJqTbId = '#' + myTbId;
	var myPageId = 'myp1';
	var myJqPageId = '#' + myPageId;
	
	$("#startDate").val(laydate.now(-1) + ' 00:00:00');
	$("#endDate").val(laydate.now() + ' 00:00:00');
	var t1 = $('<table></table>');
	t1.attr('id', myTbId);
	$(myJqTbContentId).append(t1);

	var p1 = $('<div></div>');
	p1.attr('id', myPageId);

	p1.insertAfter(t1);
	var data = $("#searchForm").serialize();
	$(myJqTbId).jqGrid({
		url : basePath + 'FinancialChannelReportList?'+data,
		mtype : "GET",
		datatype : "json",
		caption : "上游数据列表", //设置表标题
		page : 1,
		colNames : ['通道名','运营商', '数量','原价合计','代理商折扣价合计','上家折扣价合计','毛利'],
		colModel : [
		             {name : 'channelname',sortable : false,align: 'center'},
		             {name : 'providername',sortable : false,align: 'center'},
		             {name : 'count',sortable : false,align: 'center'},
		             {name : 'fee',sortable : false,align: 'center'},
		             {name : 'agentfee',sortable : false,align: 'center'},
		             {name : 'providerfee',sortable : false,align: 'center'},
		             {name : 'profit',sortable : false,align: 'center'}
		           ],
		height : 'auto',
		multiselect:true,//多选框
		multiboxonly:true,//为true时是单选
		autowidth : true,
		shrinkToFit : true,
		hidegrid : false, //隐藏表格右上角的"展开/收缩jqGrid内容的小箭头"
		autoScroll : false,
		rowNum : myRowNum,
		rowList : myRowList,
		viewrecords : true,//显示总记录数
		pager : myJqPageId
	});
}

function agentList(){
	var myTbContentId = 'a_mytbc1';
	var myJqTbContentId = '#' + myTbContentId;
	var myTbId = 'a_myt1';
	var myJqTbId = '#' + myTbId;
	var myPageId = 'a_myp1';
	var myJqPageId = '#' + myPageId;
	
	$("#a_startDate").val(laydate.now(-1) + ' 00:00:00');
	$("#a_endDate").val(laydate.now() + ' 00:00:00');
	var t1 = $('<table></table>');
	t1.attr('id', myTbId);
	$(myJqTbContentId).append(t1);

	var p1 = $('<div></div>');
	p1.attr('id', myPageId);

	p1.insertAfter(t1);
	var data = $("#a_searchForm").serialize();
	$(myJqTbId).jqGrid({
		url : basePath + 'FinancialAgentReportList?'+data,
		mtype : "GET",
		datatype : "json",
		caption : "下游数据列表", //设置表标题
		page : 1,
		colNames : ['登录账号', '代理商名称', '运营商', '数量','原价合计','代理商折扣价合计','上家折扣价合计','毛利'],
		colModel : [
		             {name : 'agentname',sortable : false,align: 'center'},
		             {name : 'nickname',sortable : false,align: 'center'},
		             {name : 'providername',sortable : false,align: 'center'},
		             {name : 'count',sortable : false,align: 'center'},
		             {name : 'fee',sortable : false,align: 'center'},
		             {name : 'agentFee',sortable : false,align: 'center'},
		             {name : 'providerFee',sortable : false,align: 'center'},
		             {name : 'profit',sortable : false,align: 'center'}
		           ],
		height : 'auto',
		width : $(window).width()-310 + 'px',
		multiselect:true,//多选框
		multiboxonly:true,//为true时是单选
		autowidth : true,
		shrinkToFit : true,
		hidegrid : false, //隐藏表格右上角的"展开/收缩jqGrid内容的小箭头"
		autoScroll : false,
		rowNum : myRowNum,
		rowList : myRowList,
		viewrecords : true,//显示总记录数
		pager : myJqPageId
	});
	$(myJqTbId).setGridWidth($(window).width()-350);
}

function BusinessList(){
	var myTbContentId = 'b_mytbc1';
	var myJqTbContentId = '#' + myTbContentId;
	var myTbId = 'b_myt1';
	var myJqTbId = '#' + myTbId;
	var myPageId = 'b_myp1';
	var myJqPageId = '#' + myPageId;
	
	$("#b_startDate").val(laydate.now(-1) + ' 00:00:00');
	$("#b_endDate").val(laydate.now() + ' 00:00:00');
	var t1 = $('<table></table>');
	t1.attr('id', myTbId);
	$(myJqTbContentId).append(t1);

	var p1 = $('<div></div>');
	p1.attr('id', myPageId);

	p1.insertAfter(t1);
	var data = $("#b_searchForm").serialize();
	$(myJqTbId).jqGrid({
		url : basePath + 'BusinessList?'+data,
		mtype : "GET",
		datatype : "json",
		caption : "经营分析数据列表", //设置表标题
		page : 1,
		colNames : ['运营商', '通道名称', '代理商账号', '代理商名称','充值成功笔数','充值金额','代理商折扣金额','通道折扣金额','利润'],
		colModel : [
		             {name : 'providerName',sortable : false,align: 'center'},
		             {name : 'channelName',sortable : false,align: 'center'},
		             {name : 'agentName',sortable : false,align: 'center'},
		             {name : 'agentNickName',sortable : false,align: 'center'},
		             {name : 'count',sortable : false,align: 'center'},
		             {name : 'fee',sortable : false,align: 'center'},
		             {name : 'agentFee',sortable : false,align: 'center'},
		             {name : 'providerFee',sortable : false,align: 'center'},
		             {name : 'profit',sortable : false,align: 'center'}
		           ],
		height : 'auto',
		width : $(window).width()-310 + 'px',
		multiselect:true,//多选框
		multiboxonly:true,//为true时是单选
		autowidth : true,
		shrinkToFit : true,
		hidegrid : false, //隐藏表格右上角的"展开/收缩jqGrid内容的小箭头"
		autoScroll : false,
		rowNum : myRowNum,
		rowList : myRowList,
		viewrecords : true,//显示总记录数
		pager : myJqPageId
	});
	$(myJqTbId).setGridWidth($(window).width()-350);
}

function search(){
	var data = $("#searchForm").serialize();
	$('#myt1').jqGrid('setGridParam',{ 
	        url:basePath+'FinancialChannelReportList?'+data, 
	        page:1 
    }).trigger("reloadGrid"); //重新载入
}

function a_search(){
	var data = $("#a_searchForm").serialize();
	$('#a_myt1').jqGrid('setGridParam',{ 
	        url:basePath+'FinancialAgentReportList?'+data, 
	        page:1 
    }).trigger("reloadGrid"); //重新载入
}

function b_search(){
	var data = $("#b_searchForm").serialize();
	$('#b_myt1').jqGrid('setGridParam',{ 
	        url:basePath+'BusinessList?'+data, 
	        page:1 
    }).trigger("reloadGrid"); //重新载入
}

function exportFinancialChannelReport(){
	var data = $("#searchForm").serialize();
	window.location.href = basePath+"exportFinancialChannelReport?"+data;
}

function exportFinancialAgentReport(){
	var data = $("#a_searchForm").serialize();
	window.location.href = basePath+"exportFinancialAgentReport?"+data;
}
//导出经营分析报表
function exportBusinessReport(){
	var data = $("#b_searchForm").serialize();
	window.location.href = basePath+"exportBusinessReport?"+data;
}