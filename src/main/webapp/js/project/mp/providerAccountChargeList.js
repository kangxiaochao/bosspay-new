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
		url : basePath + 'providerAccountChargeList',
		mtype : "GET",
		datatype : "json",
		caption : "运营商加款列表", //设置表标题
		page : 1,
		colNames : [ 'id',  '运营商ID','运营商名称', '金额', '加款前', '加款后', '提交时间' , '备注'],
		colModel : [ {name : 'id',key : true,sortable : false,hidden : true}, 
		             {name : 'agent_id',sortable : false,hidden : true},//代理商ID
		             {name : 'providerName',sortable : false,align: 'center'}, 		//运营商名称
		             {name : 'fee',sortable : false,align: 'center'}, 	//金额
		             {name : 'balance_before',sortable : false,align: 'center'}, 	//加款前
		             {name : 'balance_after',sortable : false,align: 'center'},	//加款后
		             {name : 'apply_data',sortable : false,align: 'center'},	//提交时间
		             {name : 'remarks',sortable : false,align: 'center'},	//备注
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
		rownumbers : true,
		pager : myJqPageId
	});

	$('#name').focus();
	setTimeout(function() {
		$('.wrapper-content').removeClass('animated fadeInRight');
	}, 700);
	
    setMyActive(6,2); //设置激活页
    
    message();
});

function search(){
	var providerName = $('#providerName').val();

	$(myJqTbId).jqGrid('setGridParam', {
		url : basePath + 'providerAccountChargeList',
		postData : {
			'providerName' :providerName
		}, //发送话费 
		page : 1
	}).trigger("reloadGrid"); //重新载入
}