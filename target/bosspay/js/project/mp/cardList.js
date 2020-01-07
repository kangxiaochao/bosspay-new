var myTbContentId = 'mytbc1';
var myJqTbContentId = '#' + myTbContentId;
var myTbId = 'myt1';
var myJqTbId = '#' + myTbId;
var myPageId = 'myp1';
var myJqPageId = '#' + myPageId;
var cardId = "";

$(function() {
	var t1 = $('<table></table>');
	t1.attr('id', myTbId);
	$(myJqTbContentId).append(t1);

	var p1 = $('<div></div>');
	p1.attr('id', myPageId);

	p1.insertAfter(t1);

	$(myJqTbId).jqGrid({
		url : basePath + 'cardList',
		mtype : "GET",
		datatype : "json",
		caption : "卡密列表", //设置表标题
		page : 1,
		colNames : ['卡号','密码', '过期时间', '金额（元）','使用标识', '使用时间', '订单号', '种类', '状态信息'],
		colModel : [
		             {name : 'cardId',sortable : false,align: 'center'},
		             {name : 'cardPass',sortable : false,align: 'center',formatter:function(cellvalue){return cellvalue.substring(0,cellvalue.length-6)+"******"}},
		             {name : 'expireTime',sortable : false,align: 'center'},
		             {name : 'price',sortable : false,align: 'center'},
		             {name : 'useState',sortable : false,align: 'center'}, 	//金额
		             {name : 'useTime',sortable : false,align: 'center'},	//使用时间
		             {name : 'orderId',sortable : false,align: 'center'},
		             {name : 'type',sortable : false,align: 'center'},
		             {name : 'resultMsg',sortable : false,align: 'center'}
		           ],
		height : 'auto',
		multiselect:true,//多选框
		multiboxonly:false,//为true时是单选
		autowidth : true,
		shrinkToFit : true,
		hidegrid : false, //隐藏表格右上角的"展开/收缩jqGrid内容的小箭头"
		autoScroll : false,
		rowNum : myRowNum,
		rowList : myRowList,
		viewrecords : true,//显示总记录数
		pager : myJqPageId,
		ondblClickRow:function (rowid,iRow,iCol,e){
	    	  var rowData = $(myJqTbId).jqGrid('getRowData',rowid);
	    	  layer.open({
	    		  skin: 'layui-layer-molv',
	    		  type: 0,
	    		  title: '订单详情',
	    		  closeBtn: false,
	    		  area: '800px;',
	    		  shade: 0.8,
	    		  id: 'LAY_layuipro', //设定一个id，防止重复弹出
	    		  resize: false,	//是否允许拉伸
	    		  btn: ['关闭'],
	    		  btnAlign: 'c',	
	    		  moveType: 1, //拖拽模式，0或者1
	    		  content: showOrderDetail(rowData),
	    	  });
	      }
	});

    setMyActive(2,9); //设置激活页
    $("#startDate").val(laydate.now() + ' 00:00:00');
    $("#endDate").val(laydate.now(1) + ' 00:00:00');
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
	        url:basePath+'cardList?'+data, 
	        page:1 
    }).trigger("reloadGrid"); //重新载入
}

function toImportCard(){
	layer.open({
		  type: 1,
		  skin: 'layui-layer-rim', //加上边框
		  area: ['300px', '210px'], //宽高
		  content: '<form id="importForm" action="importCard" enctype="multipart/form-data" method="post"><br/><br/>' +
					  '<input style="margin-left:20px" type="file" name="file"/><br/><button type="button" class="btn btn-primary" style="margin-left:30%" onclick="importCard(this)">导入卡密</button>' +
				'</form>'
		});
}

function importCard(item){
	var form = $(item).parent();
	$(form).ajaxSubmit(function(data){
		var json = eval("("+data+")");
		layer.msg(json.message);  
	});          
}

function showOrderDetail(rowData){
	var orderId = rowData.orderId;
	var orderDetailHtml = "";
	$.ajax({
		url :basePath+"/detailOrder/"+orderId,
		type:'get',
		async: false,
		dataType: 'json',
		success:function (dt){
			orderDetailHtml='<table width="100%" class="table table-striped table-bordered table-hover dataTables-example" >'+
			'    <tr class="gradeX">'+
			'        <td width="150px;" align="right">平台订单号</td>'+
			'        <td>'+dt.orderId+'</td>'+
			'        <td width="150px;" align="right">代理商订单号</td>'+
			'        <td>'+dt.agentOrderId+'</td>'+
			'  </tr>'+
			'    <tr class="gradeA">'+
			'        <td align="right">手机号</td>'+
			'        <td>'+dt.phone+'</td>'+
			'        <td align="right">类型</td>'+
			'        <td>'+dt.bizType+'</td>'+
			'    </tr>'+
			'    <tr class="gradeA">'+
			'        <td align="right">状态</td>'+
			'        <td>'+dt.status+'</td>'+
			'        <td align="right">面值(元)</td>'+
			'        <td>'+dt.fee+'</td>'+
			'    </tr>'+
			'    <tr class="gradeA">'+
			'        <td align="right">复冲次数</td>'+
			'        <td>'+dt.dealCount+'</td>'+
			'        <td align="right">请求时间</td>'+
			'        <td>'+dt.applyDate+'</td>'+
			'    </tr>'+
			'    <tr class="gradeA">'+
			'        <td align="right">处理时间</td>'+
			'        <td>'+dt.endDate+'</td>'+
			'        <td align="right">消耗时间</td>'+
			'        <td>'+dt.consumedTime+'</td>'+
			'    </tr>'+
			'    <tr class="gradeA">'+
			'        <td align="right">结果描述</td>'+
			'        <td colspan="3" >'+dt.resultCode+'</td>'+
			'    </tr>'+
			'</table>';
	    }
	});
	return orderDetailHtml;
}
function updateCard(){
//	alert(cardId);
	var id = getSelectedOrderIds();
	$.ajax({
		url :basePath+"updateCard",
		type:'post',
		data:{'cardId':id},
		success:function (dt){
			layer.alert("已成功修改"+dt+"条");
	    },
	});
}
function getSelectedOrderIds(){
	var ids = jQuery(myJqTbId).jqGrid("getGridParam", "selarrrow");
	var id = "";
	for(var i = 0 ; i < ids.length ; i++){
		var rowData = $(myJqTbId).jqGrid('getRowData',ids[i]);
		id += rowData.cardId+",";
	}
	return id;
}

function exportCard(){
	var card = basePath+"exportCard?";
	var type = $("#type").val();
	var useState = $("#useState").val();
	var startDate = $("#startDate").val();
	var endDate = $("#endDate").val();
	if(type.length > 0){
		card+="&type="+type;
	}
	if(useState.length > 0){
		card += "&useState="+useState;
	}
	if(startDate.length > 0){
		card += "&startDate="+startDate;
	}
	if(endDate.length > 0){
		card += "&endDate="+endDate;
	}
	window.location.href = card;
}