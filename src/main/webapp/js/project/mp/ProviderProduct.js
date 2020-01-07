var myTbContentId = 'mytbc1';
var myJqTbContentId = '#' + myTbContentId;
var myTbId = 'myt1';
var myJqTbId = '#' + myTbId;
var myPageId = 'myp1';
var myJqPageId = '#' + myPageId;

$(function() {
	var backMsg = $("#backMsg").val();
	if (backMsg.length > 0) {
		outMessage('success', backMsg, '友情提示');
	}
	var t1 = $('<table></table>');
	t1.attr('id', myTbId);
	$(myJqTbContentId).append(t1);

	var p1 = $('<div></div>');
	p1.attr('id', myPageId);

	p1.insertAfter(t1);

	$(myJqTbId).jqGrid({
		url :'queryProviderProductList',
		mtype : "Post",
		datatype : "json",
		caption : "上家产品列表", //设置表标题
		page : 1,
		colNames : [ 'id', '流量包体', '通道', '运营商物理通道', '上家ID', '省份' ],
		colModel :[
			{
				name : 'id',
				key : true,
				sortable : false,
				hidden : true
			},
			{
				name : 'pkgName',
				sortable : false,
				index : 'spuid'
			}, //查询条件要添加index
			{
				name : 'pName',
				sortable : false,
				index : 'province'
			},
			{
				name : 'pcName',
				sortable : false
			},
			{
				name : 'pkId',
				sortable : false
			},
			{
				name : 'code',
				sortable : false
			}
		],
		//width: 750,
		height : 'auto',
		multiselect : true, //多选框
		multiboxonly : true, //为true时是单选
		autowidth : true,
		shrinkToFit : true,
		hidegrid : false, //隐藏表格右上角的"展开/收缩jqGrid内容的小箭头"
		autoScroll : false,
		rowNum : myRowNum,
		rowList : myRowList,
		viewrecords : true, //显示总记录数
		rownumbers : true,
		pager : myJqPageId
	});
	$.ajax({
		type : "POST",
		url : "pkg",
		dataType : "json",
		scriptCharset : 'utf-8',
		success : function(data) {
			var pId = $("#pkid").val();
			for (var i = 0; i < data.length; i++) {
				var str = "<option value=\"" + data[i].id + "\"";
				if(data[i].id==""+pId+""){
					str+=" selected=\"selected\"";
				}
				str+=">" + data[i].name + "</option>";
				$("#pkgId").append(str);
			}
			setSelectStyle($("#pkgId"));
		}
	});
	$.ajax({
		type : "POST",
		url : "physicalChannel",
		dataType : "json",
		scriptCharset : 'utf-8',
		success : function(data) {
			var pId = $("#pcid").val();
			for (var i = 0; i < data.length; i++) {
				var str = "<option value=\"" + data[i].id + "\"";
				if(data[i].id==""+pId+""){
					str+=" selected=\"selected\"";
				}
				str+=">" + data[i].NAME + "</option>";
				$("#physicalChannelId").append(str);
			}
			setSelectStyle($("#physicalChannelId"));
		}
	});
	setSelectStyle($("#provinceCode"));
	setMyActive(2,6);
});

function add() {
	location.href = "providerProduct";
}

function search() {
	var pkgName = $('#pkgName').val();
	var physicalChannelName = $('#physicalChannelName').val();
	var provinceCode = $('#provinceCodes').val();
	$(myJqTbId).jqGrid('setGridParam', {
		url : 'queryProviderProductList',
		postData : {
			'pkgName' : pkgName,'physicalChannelName':physicalChannelName,'provinceCode':provinceCode
		}, //发送数据 
		page : 1
	}).trigger("reloadGrid"); //重新载入
}

function edit(id){
	var myBasePath=basePath + 'queryById/' + id;
	location.href=myBasePath;
}

function editEx(){
	var id=$(myJqTbId).jqGrid('getGridParam','selrow');
	if(id==null){
		outMessage('warning','没有选中的记录！','友情提示');
	}else{
		edit(id);
	}
}

function del(ids){
	var delFlag=confirm("确认要删除么？");
	if(delFlag){
    	var myDelUrl='delProvider';
		$.ajax({
    		type:'Post',
    		url :myDelUrl,
    		data:{ id: ids },
    		success:function (dt){
    			outMessage('success', dt ,'友情提示');
    			$(myJqTbId).trigger("reloadGrid");
    	    },
    	    dataType: 'html'
    	});
	}
}

function delEx(){
	var id=$(myJqTbId).jqGrid('getGridParam','selrow');
	if(id==null){
		outMessage('warning','没有选中的记录！','友情提示');
	}else{
		del(id);
	}
}