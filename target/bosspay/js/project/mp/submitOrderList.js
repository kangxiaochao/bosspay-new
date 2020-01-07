$(function(){
	$("#startDate").val(laydate.now() + ' 00:00:00');
	$("#endDate").val(laydate.now(1) + ' 00:00:00');
	getList();
	setMyActive(3,3); //设置激活页
	setSelectStyle($("#resultCode"));
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
	var t1=$('<table id = "myt1"></table>');
	  $('#mytbc1').append(t1);
	  var p1=$('<div id = "myp1"></div>');
	  p1.insertAfter(t1);
	  
	  var data = $("#searchForm").serialize();
	  
	  $('#myt1').jqGrid({
	      url: basePath+'submitOrderList?'+data,
	      mtype: "GET",
	      datatype: "json",
	      caption: "订单提交记录列表",	//设置表标题
	      page: 1,
	      colNames: ['id','代理商名称', '代理商订单号','手机号','提交参数','提交结果','提交类型','提交时间'],
	      colModel: [
	          { name: 'id', key: true, sortable: false,hidden:true },
	          { name: 'agent_name', sortable: false ,index:'agent_name'},
	          { name: 'order_id', sortable: false ,index:'order_id'},
	          { name: 'phone', sortable: false  ,index:'phone'},
	          { name: 'submit_param', sortable: false ,index:'submit_param' },
	          { name: 'result_code', sortable: false  ,index:'result_code'},
	          { name: 'biz_type', sortable: false ,index:'biz_type',width : '80',formatter:function(cellvalue){if(cellvalue=="1"){return "话费";}if(cellvalue=="2"){return "话费";}} },
	          { name: 'submit_date', sortable: false ,index:'submit_date' }
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
	      pager: '#myp1',
	      ondblClickRow:function (rowid,iRow,iCol,e){
	    	  var rowData = $('#myt1').jqGrid('getRowData',rowid);
	    	  layer.open({
	    		  skin: 'layui-layer-molv',
	    		  type: 0,
	    		  title: '订单提交记录详情',
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
}

function showOrderDetail(rowData){
	var orderDetailHtml='<table width="100%" class="table table-striped table-bordered table-hover dataTables-example" >'+
						'    <tr class="gradeX">'+
						'        <td width="150px;" align="right">代理商名称</td>'+
						'        <td>'+rowData.agent_name+'</td></tr><tr>'+
						'        <td width="150px;" align="right">代理商订单号</td>'+
						'        <td>'+rowData.order_id+'</td>'+
						'  </tr>'+
						'    <tr class="gradeC">'+
						'        <td align="right">手机号</td>'+
						'        <td>'+rowData.phone+'</td></tr><tr>'+
						'        <td align="right">充值类型</td>'+
						'        <td>'+rowData.biz_type+'</td>'+
						'    </tr>'+
						'    <tr class="gradeA">'+
						'        <td align="right">提交时间</td>'+
						'        <td>'+rowData.submit_date+'</td></tr><tr>'+
						'        <td align="right">返回下家的状态</td>'+
						'        <td style="color:red">'+rowData.result_code+'</td>'+
						'    </tr>'+
						'    <tr class="gradeA">'+
						'        <td align="right">提交参数</td>'+
						'        <td colspan="3" >'+rowData.submit_param.replace(/\|/g,"<br/>")+'</td>'+
						'    </tr>'+
						'</table>';
	
	return orderDetailHtml;
}


function search(){
	var data = $("#searchForm").serialize();
	$('#myt1').jqGrid('setGridParam',{ 
	        url:basePath+'submitOrderList?'+data, 
	        page:1 
    }).trigger("reloadGrid"); //重新载入
}

function orderReport(){
	var data = $("#searchForm").serialize();
	location.href = basePath + 'expsubmitOrder?' + data;
}
