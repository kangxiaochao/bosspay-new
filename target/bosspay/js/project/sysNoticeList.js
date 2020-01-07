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
		url : basePath + 'sysNotice',
		mtype : "GET",
		datatype : "json",
		caption : "公告列表", // 设置表标题
		page : 1,
		colNames : [ 'id', '标题', '公告类型', '更新时间', '发布时间'],
		colModel : [ {
			name : 'id',
			key : true,
			sortable : false,
			hidden : true
		}, {
			name : 'title',
			sortable : false,
			index : 'title'
		}, {
			name : 'order',
			sortable : false,
			index : 'order'
		}, {
			name : 'update_date',
			sortable : false,
			index : 'update_date'
		}, {
			name : 'create_date',
			sortable : false,
			index : 'create_date'
		}],
		// width: 750,
		height : 'auto',
		multiselect : false,// 多选框
		multiboxonly : true,// 为true时是单选
		autowidth : true,
		shrinkToFit : true,
		hidegrid : false, // 隐藏表格右上角的"展开/收缩jqGrid内容的小箭头"
		autoScroll : false,
		rowNum : myRowNum,
		rowList : myRowList,
		viewrecords : true,// 显示总记录数
		rownumbers : true,
		pager : myJqPageId
	});

	$('#name').focus();
	setTimeout(function() {
		$('.wrapper-content').removeClass('animated fadeInRight');
	}, 700);

	setMyActive(1, 6); // 设置激活页

	message();
});

function add() {
	location.href = basePath + "sysNoticeAddPage";
}

function edit(id) {
	var myBasePath = basePath + 'sysNoticeEditPage/' + id;
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
	layer.confirm('确认要删除吗?', {skin: 'layui-layer-molv',icon: 3, title:'提示'}, function(index){
		var myDelUrl = basePath + 'sysNotice/' + id;
		$.ajax({
			type : 'DELETE',
			url : myDelUrl,
			data : {
				suId : id
			},
			success : function(dt) {
				location.href = basePath + dt;
			},
			dataType : 'html'
		});
	});
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
	var title = $('#title').val();

	$(myJqTbId).jqGrid('setGridParam', {
		url : basePath + 'sysNotice',
		postData : {
			'title' : title
		}, // 发送数据
		page : 1
	}).trigger("reloadGrid"); // 重新载入
}

