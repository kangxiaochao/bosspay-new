

var myTbContentId = 'mytbc1';
var myJqTbContentId = '#' + myTbContentId;
var myTbId = 'myt1';
var myJqTbId = '#' + myTbId;
var myPageId = 'myp1';
var myJqPageId = '#' + myPageId;

$(function() {
	var id=$("#modelId").val();
	var t1 = $('<table></table>');
	t1.attr('id', myTbId);
	$(myJqTbContentId).append(t1);

	var p1 = $('<div></div>');
	p1.attr('id', myPageId);

	p1.insertAfter(t1);

	$(myJqTbId).jqGrid({
		url : basePath + 'dataDiscountModelDetail/'+id,
		mtype : "GET",
		datatype : "json",
		caption : "折扣详情", //设置表标题
		page : 1,
		colNames : [ 'id', '折扣名称', '运营商', '话费包', '折扣', '省份编码','城市编码','修改时间','修改人','创建时间','创建人' ],
		colModel : [ {name : 'id',key : true,sortable : false,hidden : true}, 
		             {name : 'name',sortable : false},	//折扣名称
		             {name : 'providername',sortable : false}, 		//运营商
		             {name : 'datapkgname',sortable : false}, 	//话费包
		             {name : 'discount',sortable : false}, 	//折扣
		             {name : 'province_code',sortable : false},	//省份编码
		             {name : 'city_code',sortable : false},	//城市编码
		             {name : 'update_date',sortable : false},	//修改时间
		             {name : 'update_user',sortable : false},	//修改人
		             {name : 'create_date',sortable : false},	//创建时间
		             {name : 'create_user',sortable : false},	//创建人
		           ],
		height : 'auto',
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
	setMyActive(5,5); //设置激活页
	message();
});
