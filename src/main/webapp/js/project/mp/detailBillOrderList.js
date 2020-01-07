var myTbContentId = 'mytbc1';
var myJqTbContentId = '#' + myTbContentId;
var myTbId = 'myt1';
var myJqTbId = '#' + myTbId;
var myPageId = 'myp1';
var myJqPageId = '#' + myPageId;

$(function(){
	setSelectStyle($("#status"));
	var t1 = $('<table></table>');
	t1.attr('id', myTbId);
	$(myJqTbContentId).append(t1);
	var p1 = $('<div></div>');
	p1.attr('id', myPageId);
	p1.insertAfter(t1);
	var data = $("#searchForm").serialize();
	
	var userId = $('#leftMenuDiv').attr('data-suId');
	$.ajax({
		type:'get',
		url :basePath + 'sysGetHasRole/'+userId,
		async: false,
		data:{},
		dataType: 'json',
		success:function (dt){
			var roleName;
			for(var i in dt){
				roleName = dt[i]["srName"];
				if(roleName == "代理商"){
					break;
				}
			}
			for(var i in dt){
				roleName = dt[i]["srName"];
				if(roleName == "渠道"){
					break;
				}
			}
			for(var i in dt){
				roleName = dt[i]["srName"];
				if(roleName == "超级管理员"){
					break;
				}
			}
			if(roleName == "代理商"){
				$(myJqTbId).jqGrid(
						{
							url : basePath + 'detailBillOrderList?'+data,
							mtype : "Post",
							datatype : "json",
							caption : "订单列表", // 设置表标题
							page : 1,
							colNames : [ '代理商订单号', '运营商', '归属地',
									'手机号', '话费包', '代理商名', '提交时间', '结束时间','状态', '原价(元)', 
									'代理商折扣价(元)'],
							colModel : [ {
								name : 'agent_order_id',
								sortable : false,
								index : 'agent_order_id'
							}, {
								name : 'provider',
								sortable : false,
								index : 'provider'
							}, {
								name : 'province_code',
								sortable : false,
								index : 'province_code'
							}, {
								name : 'phone',
								sortable : false,
								index : 'phone'
							}, {
								name : 'pkg_name',
								sortable : false,
								index : 'pkg_name'
							}, {
								name : 'agent_name',
								sortable : false,
								index : 'agent_name'
							}, {
								name : 'apply_date',
								sortable : false,
								index : 'apply_date'
							}, {
								name : 'end_date',
								sortable : false,
								index : 'end_date'
							}, {
								name : 'status',
								sortable : false,
								index : 'status',
								width : '80',
//								formatter : formatStatus
							}, {
								name : 'fee',
								sortable : false,
								index : 'fee',
								width : '85'
							}, {
								name : 'agent_fee',
								sortable : false,
								index : 'agent_fee',
							} ],
							// width: 750,
							height : 'auto',
							multiselect:true,//多选框
							multiboxonly:false,//为true时是单选
							autowidth : true,
							shrinkToFit : false,
							hidegrid : false, // 隐藏表格右上角的"展开/收缩jqGrid内容的小箭头"
							autoScroll : false,
							rowNum : myRowNum,
							rowList : myRowList,
							viewrecords : true,//显示总记录数
							rownumbers : true,
							pager : myJqPageId
						});
			}else if(roleName == "超级管理员"){
				$(myJqTbId).jqGrid(
						{
							url : basePath + 'detailBillOrderList?'+data,
							mtype : "Post",
							datatype : "json",
							caption : "订单列表", // 设置表标题
							page : 1,
							colNames : ['平台订单号', '代理商订单号', '上家订单号', '通道', '归属地',
									'手机号', '话费包', '代理商名', '提交时间', '结束时间','状态', '原价(元)', 
									'客户折扣价(元)','上家折扣价(元)'],
							colModel : [ {
								name : 'order_id',
								key : true,
								sortable : false,
							}, {
								name : 'agent_order_id',
								sortable : false,
								index : 'agent_order_id'
							}, {
								name : 'provider_order_id',
								sortable : false,
								index : 'provider_order_id'
							}, {
								name : 'provider_name',
								sortable : false,
								index : 'provider_name'
							}, {
								name : 'province_code',
								sortable : false,
								index : 'province_code'
							}, {
								name : 'phone',
								sortable : false,
								index : 'phone'
							}, {
								name : 'pkg_name',
								sortable : false,
								index : 'pkg_name'
							}, {
								name : 'agent_name',
								sortable : false,
								index : 'agent_name'
							}, {
								name : 'apply_date',
								sortable : false,
								index : 'apply_date'
							}, {
								name : 'end_date',
								sortable : false,
								index : 'end_date'
							}, {
								name : 'status',
								sortable : false,
								index : 'status',
								width : '80',
//								formatter : formatStatus
							}, {
								name : 'fee',
								sortable : false,
								index : 'fee',
								width : '85'
							}, {
								name : 'agent_fee',
								sortable : false,
								index : 'agent_fee',
							}, {
								name : 'provider_fee',
								sortable : false,
								index : 'provider_fee',
							} ],
							// width: 750,
							height : 'auto',
							multiselect:true,//多选框
							multiboxonly:false,//为true时是单选
							autowidth : true,
							shrinkToFit : false,
							hidegrid : false, // 隐藏表格右上角的"展开/收缩jqGrid内容的小箭头"
							autoScroll : false,
							rowNum : myRowNum,
							rowList : myRowList,
							viewrecords : true,//显示总记录数
							rownumbers : true,
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
			}
		}
	});
});


function formatStatus(cellvalue) {
	if (cellvalue == "1") {
		return "提交成功";
	} else if (cellvalue == "2") {
		return "提交失败";
	} else if (cellvalue == "3") {
		return "充值成功";
	} else if (cellvalue == "4") {
		return "充值失败";
	} else {
		return "订单异常";
	}
}

function search(){
	var data = $("#searchForm").serialize();
	$(myJqTbId).jqGrid('setGridParam',{ 
	        url:basePath+'detailBillOrderList?'+data,
	        mtype: "POST",
	        page:1 
    }).trigger("reloadGrid"); //重新载入
}