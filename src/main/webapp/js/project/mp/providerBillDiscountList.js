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
		url : basePath + 'providerBillDiscount',
		mtype : "GET",
		datatype : "json",
		// caption: "运营商列表", //设置表标题
		postData : {
			providerPhysicalChannelId:$("#providerPhysicalChannelId").val()
		},
		page : 1,
		colNames : [ 'id', '运营商', '话费包名称', '折扣','地区'],
		colModel : [ {
			name : 'id',
			key : true,
			sortable : false,
			hidden : true
		}, {
			name : 'providerName',
			sortable : false
		}, // 查询条件要添加index
		{
			name : 'name',
			sortable : false
		}, // 查询条件要添加index
		{
			name : 'discount',
			sortable : false
		},
		{
			name : 'province_code',
			sortable : false
		}],
		// width: 750,
		height : 'auto',
		multiselect:true,//多选框
		multiboxonly:true,//为true时是单选
		autowidth : true,
		shrinkToFit : true,
		hidegrid : false, // 隐藏表格右上角的"展开/收缩jqGrid内容的小箭头"
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

	setMyActive(6, 3); // 设置激活页

	setSelectStyle($("#channel_type"));

	message();
});

function setProviderBillDiscount(){
	var id=$("#providerPhysicalChannelId").val();
	var myBasePath=basePath + 'providerBillDiscountEditPage/'+id;
	location.href=myBasePath;
}



function search() {
	var name = $('#name').val();
	var providerName = $('#providerName').val();
	var providerPhysicalChannelId = $("#providerPhysicalChannelId").val()
	
	$(myJqTbId).jqGrid('setGridParam', {
		url : basePath + 'providerBillDiscount',
		postData : {
			'name' : name,
			'providerName' : providerName,
			'providerPhysicalChannelId' : providerPhysicalChannelId
		}, // 发送数据
		page : 1
	}).trigger("reloadGrid"); // 重新载入
}
