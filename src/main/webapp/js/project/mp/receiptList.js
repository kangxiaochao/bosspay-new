var myTbContentId = 'mytbc1';
var myJqTbContentId = '#' + myTbContentId;
var myTbId = 'myt1';
var myJqTbId = '#' + myTbId;
var myPageId = 'myp1';
var myJqPageId = '#' + myPageId;

$(function() {
	var t1 = $('<table></table>');
	t1.attr('id', myTbId);
	$(myJqTbContentId).append(t1);

	var p1 = $('<div></div>');
	p1.attr('id', myPageId);

	p1.insertAfter(t1);

	$(myJqTbId).jqGrid({
		url : basePath + 'receiptList',
		mtype : "GET",
		datatype : "json",
		caption : "收据列表", //设置表标题
		page : 1,
		colNames : ['序号','日期', '入账名称', '打款人姓名', '金额（元）', '收款账户'],
		colModel : [
		             {name : 'num',sortable : false,align: 'center'},
		             {name : 'applyDate',sortable : false,align: 'center'},
		             {name : 'agentName',sortable : false,align: 'center'},
		             {name : 'payName',sortable : false,align: 'center'},
		             {name : 'fee',sortable : false,align: 'center'}, 	//金额
		             {name : 'moneyAccount',sortable : false,align: 'center'}	//申请时间
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

    setMyActive(7,3); //设置激活页
    $("#startDate").val(laydate.now() + ' 00:00:00');
    $("#endDate").val(laydate.now(1) + ' 00:00:00');
    setSelectStyle($("#moneyAccount"));
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

function search(){
	var data = $("#searchForm").serialize();
	$('#myt1').jqGrid('setGridParam',{ 
	        url:basePath+'receiptList?'+data, 
	        page:1 
    }).trigger("reloadGrid"); //重新载入
}

function exportReceipt(){
	var data = $("#searchForm").serialize();
	window.location.href = basePath+"exportReceipt?"+data;
}

function exportMergeReceipt(){
	var data = $("#searchForm").serialize();
	window.location.href = basePath+"exportMergeReceipt?"+data;
}