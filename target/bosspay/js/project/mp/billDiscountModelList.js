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
		url : basePath + 'dataDiscountModel',
		mtype : "GET",
		datatype : "json",
		caption : "代理商话费折扣模板列表", //设置表标题
		page : 1,
		colNames : [ 'id', '折扣名称', '修改人', '修改时间', '创建人', '创建时间' ],
		colModel : [ {name : 'id',key : true,sortable : false,hidden : true}, 
		             {name : 'name',sortable : false},	//折扣名称
		             {name : 'update_user',sortable : false}, 		//修改人
		             {name : 'update_date',sortable : false}, 	//修改时间
		             {name : 'create_user',sortable : false}, 	//创建人
		             {name : 'create_date',sortable : false},	//创建时间
		            
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
	
	message();
    setMyActive(5,4); //设置激活页
});

// 跳转到折扣模板添加页面
function add() {
	location.href = basePath + "dataDiscountModelAddPage";
}

function add() {
	location.href = basePath + "dataDiscountModelAddPage";
}
//跳转到详情页面
function detailEx() {
	var id = $(myJqTbId).jqGrid('getGridParam', 'selrow');
	if (id == null) {
		outMessage('warning', '没有选中的记录！', '友情提示');
	} else {
		detail(id);
	}
}

function detail(id) {
	var myBasePath = basePath + 'dataDiscountModelDetailListPage/' + id;
	location.href = myBasePath;
}

function edit(id) {
	var myBasePath = basePath + 'dataDiscountModelEditPage/' + id;
	location.href = myBasePath;
}

function editEx() {
	var id = $(myJqTbId).jqGrid('getGridParam', 'selrow');
	if (id == null) {
		outMessage('warning', '没有选中的记录！', '友情提示');
	} else {
		edit(id);
	}
}

function del(id) {
	var delFlag = confirm("确认要删除么？");
	if (delFlag) {
		var myDelUrl = basePath + 'dataDiscountModel/' + id;
		$.ajax({
			type : 'DELETE',
			url : myDelUrl,
			data : {
				id : id
			},
			success : function(dt) {
				location.href = basePath + dt;
			},
			dataType : 'html'
		});
	}
}

function delEx() {
	var id = $(myJqTbId).jqGrid('getGridParam', 'selrow');
	if (id == null) {
		outMessage('warning', '没有选中的记录！', '友情提示');
	} else {
		del(id);
	}
}

function search() {
	var name = $('#pname').val();

	$(myJqTbId).jqGrid('setGridParam', {
		url : basePath + 'dataDiscountModelAll',
		postData : {
			'name' : name,
		}, //发送话费 
		page : 1
	}).trigger("reloadGrid"); //重新载入
}
