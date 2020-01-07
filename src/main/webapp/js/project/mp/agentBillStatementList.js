var myTbContentId = 'mytbc1';
var myJqTbContentId = '#' + myTbContentId;
var myTbId = 'myt1';
var myJqTbId = '#' + myTbId;
var myPageId = 'myp1';
var myJqPageId = '#' + myPageId;

$(function(){
	$("#startDate").val(laydate.now() + ' 00:00:00');
	$("#endDate").val(laydate.now(1) + ' 00:00:00');
	getList();
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

function getList(){
	var t1 = $('<table></table>');
	t1.attr('id', myTbId);
	$(myJqTbContentId).append(t1);

	var p1 = $('<div></div>');
	p1.attr('id', myPageId);

	p1.insertAfter(t1);
	  
	  $(myJqTbId).jqGrid({
	      url: basePath+'agentBillStatementList?'+$("#searchForm").serialize(),
	      mtype: "POST",
	      datatype: "json",
	      caption: "订单提交记录列表",	//设置表标题
	      page: 1,
	      colNames: ['代理商名称', '个数','原价/折扣价', '个数','原价/折扣价', '个数','原价/折扣价','个数','原价/折扣价','操作'],
	      colModel: [
//	          { name: 'agent_id', sortable: false },
	          { name: 'agent_name', sortable: false ,index:'channel_name'},
	          { name: 'num_1',width:'50px', sortable: false ,index:'num_1'},
	          { name: 'fee_1', sortable: false  ,index:'fee_1',formatter: formatterFee1},
	          { name: 'num_2',width:'50px', sortable: false ,index:'num_2'},
	          { name: 'fee_2', sortable: false  ,index:'fee_2',formatter: formatterFee2},
	          { name: 'num_3',width:'50px', sortable: false ,index:'num_3'},
	          { name: 'fee_3', sortable: false  ,index:'fee_3',formatter: formatterFee3},
	          { name: 'num_4',width:'50px', sortable: false ,index:'num_4'},
	          { name: 'fee_4', sortable: false  ,index:'fee_4',formatter: formatterFee4},
	          { name: 'active', sortable: false , formatter: getButton}
	      ],
	      //width: 750,
	      height: 'auto',
	      multiselect:true,//多选框
	      multiboxonly:false,//为true时是单选
	      autowidth:true,
	      shrinkToFit:true,
	      hidegrid: false,	//隐藏表格右上角的"展开/收缩jqGrid内容的小箭头"
	      autoScroll: false,
	      rowNum : myRowNum,
	      rowList : myRowList,
	      viewrecords : true,//显示总记录数
	      rownumbers: true,
	      pager: myJqPageId
	  });
	  //合并表头单元格
	  $(myJqTbId).jqGrid('setGroupHeaders', {
		    useColSpanStyle: true,
		    groupHeaders:[
				{startColumnName:'num_1', numberOfColumns:2, titleText: '<div align="center">提交成功</div>'},
				{startColumnName:'num_2', numberOfColumns:2, titleText: '<div align="center">提交失败</div>'}, 
		        {startColumnName:'num_3', numberOfColumns:2, titleText: '<div align="center">充值成功</div>'},
		        {startColumnName:'num_4', numberOfColumns:2, titleText: '<div align="center">充值失败</div>'} 
		    ] 
		})
		setMyActive(7, 2); // 设置激活页
}

//获取操作按钮
function getButton(cellvalue, options, rowObject){
	var agentId = rowObject['agent_id'];
	return "<div align='center'><button type='button' agentId='"+agentId+"' class='btn btn-success' onclick='detail(this)'>详情</button>&nbsp;"+
				"<button type='button' agentId='"+agentId+"' class='btn btn-warning' onclick='exportExcel(this)'>导出Excel</button>";
}

//---------格式化金额
function formatterFee1(cellvalue, options, rowObject){
	return rowObject['fee_1']+"/"+rowObject['agent_fee_1'];
}
function formatterFee2(cellvalue, options, rowObject){
	return rowObject['fee_2']+"/"+rowObject['agent_fee_2'];
}
function formatterFee3(cellvalue, options, rowObject){
	return rowObject['fee_3']+"/"+rowObject['agent_fee_3'];
}
function formatterFee4(cellvalue, options, rowObject){
	return rowObject['fee_4']+"/"+rowObject['agent_fee_4'];
}

//详情方法
function detail(obj){
	var agentId = $(obj).attr("agentId");
	window.location.href = basePath+"toDetailBillOrderList?"+"agentId="+agentId+"&"+$("#searchForm").serialize();
}

//导出方法
function exportExcel(obj){
	var agentId = $(obj).attr("agentId");
	window.location.href = basePath+"exportBillOrderExcel?"+"agentId="+agentId+"&"+$("#searchForm").serialize();
	outMessage('success', "由于数据量较大,请耐心等候,勿要刷新页面", '友情提示');
}

function search(){
	var data = $("#searchForm").serialize();
	$(myJqTbId).jqGrid('setGridParam',{ 
	        url:basePath+'agentBillStatementList?'+data,
	        mtype: "POST",
	        page:1 
    }).trigger("reloadGrid"); //重新载入
}