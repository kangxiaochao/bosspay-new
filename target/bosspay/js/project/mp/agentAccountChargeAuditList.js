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
		url : basePath + 'agentAccountChargeAuditList',
		mtype : "GET",
		datatype : "json",
		caption : "代理商冲扣款列表", //设置表标题
		page : 1,
		colNames : [ 'id',  '代理商ID','代理商名称','打款人姓名', '金额', '进款账户','申请时间', '申请人','类型', '备注','余额','渠道','审核时间', '审核人',  '审核','状态' ],
		colModel : [ {name : 'id',key : true,sortable : false,hidden : true}, 
		             {name : 'agent_id',sortable : false,hidden : true},//代理商ID
		             {name : 'agentName',sortable : false,align: 'center'},//代理商名称
		             {name : 'paymoney_name',sortable : false,align: 'center'},//打款人姓名
		             {name : 'fee',sortable : false,width:'100',align: 'center'}, 	//金额
		             {name : 'money_account',sortable : false,width:'100',align: 'center'},//进款账户
		             {name : 'applyDate',sortable : false,align: 'center'}, 	//申请时间
		             {name : 'apply_user',sortable : false,width:'100',align: 'center'},	//申请人
		             {name : 'type',sortable : false,width:'80',align: 'center'},
		             {name : 'remark',sortable : false,align: 'center'},	//备注
		             {name : 'yue',sortable : false,align: 'center'},	//余额
		             {name : 'qudao',sortable : false,align: 'center'},	//渠道
		             {name : 'confirmDate',sortable : false,align: 'center'},	//审核时间
		             {name : 'confirm_user',sortable : false,width:'100',align: 'center'},	//审核人
		             {name : 'flag',sortable : false,width:'80',align: 'center'},		//审核状态
		             {name : 'state',sortable : false,width:'80',align: 'center',formatter: function(state,a,b){
		            	 if(state==0){
		            		 return '<button type="button" class="btn btn-primary" onclick="confirm(\''+b.id+'\',\''+b.agent_id+'\',\''+b.fee+'\',1)">通过</button>&nbsp;<button type="button" class="btn btn-danger btn-outline" onclick="confirm(\''+b.id+'\',\''+b.agent_id+'\',\''+b.fee+'\',2)">拒绝</button>';
		            	 }else{
		            		 return state;
		            	 }
		             }}
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
    setMyActive(5,3); //设置激活页
    setSelectStyle($("#moneyAccount"));
    $("#applyDate").val(laydate.now() + ' 00:00:00');
    $("#confirmDate").val(laydate.now(1) + ' 00:00:00');
    message();
});

function confirm(id,agentId,fee,flag){
	var url=basePath + 'agentAccountChargeAuditEdit';
	$.ajax({
		type:'GET',
		url:url,
		data:{
			id:id,
			agentId:agentId,
			fee:fee,
			flag:flag
		},
		dataType:'html',
		success:function(dt){
			location.href = basePath + dt;
		}
	});
}

/* 绑定日期控件 */
laydate({
	elem : '#applyDate',// 目标元素。由于laydate.js封装了一个轻量级的选择器引擎，因此elem还允许你传入class、tag但必须按照这种方式
							// '#id .class'
	event : 'focus', // 响应事件。如果没有传入event，则按照默认的click
	format : 'YYYY-MM-DD hh:mm:ss', // 日期格式
	istime : true, // 是否开启时间选择
});

/* 绑定日期控件 */
laydate({
	elem : '#confirmDate', // 目标元素。由于laydate.js封装了一个轻量级的选择器引擎，因此elem还允许你传入class、tag但必须按照这种方式
						// '#id .class'
	event : 'focus', // 响应事件。如果没有传入event，则按照默认的click
	format : 'YYYY-MM-DD hh:mm:ss', // 日期格式
	istime : true, // 是否开启时间选择
});

function editEx(id) {
	var id = $(myJqTbId).jqGrid('getGridParam', 'selrow');
	if (id == null) {
		outMessage('warning', '没有选中的记录！', '友情提示');
	} else {
		edit(id);
	}
}

function edit(id){
	var myBasePath = basePath + 'agentAccountChargeAuditEditPage/' + id;
	location.href = myBasePath;
}

function search(){
	var data = $("#searchForm").serialize();
	
	$(myJqTbId).jqGrid('setGridParam', {
		url : basePath + 'agentAccountChargeAuditList?'+data,
		postData : {
			
		},// 发送数据
		page : 1
	}).trigger("reloadGrid"); // 重新载入
}

/*导出*/
function orderReport(){
	var data = $("#searchForm").serialize();
	location.href = basePath + 'agentAccountChargeAuditListExport?' + data;
}