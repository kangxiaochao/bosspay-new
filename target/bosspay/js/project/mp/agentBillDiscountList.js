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
	var id=$('#agentId').val();
	var p1 = $('<div></div>');
	p1.attr('id', myPageId);

	p1.insertAfter(t1);

	$(myJqTbId).jqGrid({
		url : basePath + 'agentDataDiscount/'+id,
		mtype : "GET",
		datatype : "json",
		caption : "话费折扣模板列表", //设置表标题
		page : 1,
		colNames : [ 'id', '运营商', '范围', '折扣', '话费包',],
		colModel : [ {name : 'id',key : true,sortable : false,hidden : true}, 
		             {name : 'providerName',sortable : false,width : '75'}, 
		             {name : 'province_code',sortable : false,width : '75'},
		             {name : 'discount',sortable : false,width : '50'}, 
		             {name : 'dataPkgName',sortable : false,width : '600'},
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
	getProviderList();
	$('#name').focus();
	setTimeout(function() {
		$('.wrapper-content').removeClass('animated fadeInRight');
	}, 700);
	setMyActive(5,1); //设置激活页
	message();
});

// 跳转到折扣添加页面
function add() {
	var id=$('#agentId').val();
	location.href = basePath + "agentDataDiscountAddPage/"+id;
}

function detail() {
	var id=$('#agentId').val();
	var myBasePath = basePath + 'agentDataDiscountDetailPage/' + id;
	location.href = myBasePath;
}

function edit(ids) {
	var agentId=$('#agentId').val();
	var myBasePath = basePath + 'agentDataDiscountEditPage/' + agentId;
	$.ajax({
		type : 'GET',
		url : myBasePath,
		data : {
			ids: ids
		},
		traditional: true,
		success : function(dt) {
			location.href = basePath+dt;
		}
	});
}

function editEx() {
	var ids = $(myJqTbId).jqGrid('getGridParam', 'selarrrow');
	if (ids == null) {
		outMessage('warning', '没有选中的记录！', '友情提示');
	} else {
		edit(ids);
	}
}

//function delEx() {
//	var ids=jQuery(myJqTbId).jqGrid("getGridParam", "selarrrow");
//	if (ids == null) {
//		outMessage('warning', '没有选中的记录！', '友情提示');
//	} else {
//		del(ids);
//	}
//}
//
//function del(ids) {
//	var agentId = $('#agentId').val();
//	var delFlag = confirm("确认要删除么？");
//	if (delFlag) {
//		var myDelUrl = basePath + 'agentDataDiscount/'+agentId;
//		$.ajax({
//			type : 'DELETE',
//			url : myDelUrl,
//			data : {
//				ids : ids
//			},
//			traditional: true,
//			success : function(dt) {
//				location.href = basePath + dt;
//			},
//			dataType : 'html'
//		});
//	}
//}

function search() {
	var id=$('#agentId').val();
	var providerId = document.getElementById("providerId").value;
	var provinceCode = $('#provinceCode').val();

	$(myJqTbId).jqGrid('setGridParam', {
		url : basePath + 'agentDataDiscount/'+id,
		postData : {
			'providerId':providerId,
			'provinceCode':provinceCode
		}, //发送话费 
		page : 1
	}).trigger("reloadGrid"); //重新载入
}

/*获取运营商列表*/
function getProviderList(){
	var provider = $("#providerId");
	var option = $("<option>").text('请选择').val('');
	provider.append(option);	
	var myDelUrl = basePath + 'providerList';
	var providerType='1';
	$.ajax({
		type:'get',
		url :myDelUrl,
		data:{
			providerType:providerType
		},
		success:function (dt){
			$.each(dt, function(index,val) {
		    	option = $("<option id='proId"+index+"'>").text(val.name).val(val.id)
		    	provider.append(option);
		    });
			setSelectStyle(provider);
			
			var dataType = $("#providerId");
			setSelectStyle(dataType);
	    },
	    dataType: 'json'
	});
}
