  var myTbContentId='mytbc1';
  var myJqTbContentId='#'+myTbContentId;
  var myTbId='myt1';
  var myJqTbId='#'+myTbId;
  var myPageId='myp1';
  var myJqPageId='#'+myPageId;
$(function () {

  var t1=$('<table></table>');
  t1.attr('id',myTbId);
  $(myJqTbContentId).append(t1);
  
  var p1=$('<div></div>');
  p1.attr('id',myPageId);
  
  p1.insertAfter(t1);
  
  $(myJqTbId).jqGrid({
      url: basePath+'providerPhysicalChannel',
      mtype: "GET",
      datatype: "json",
      caption: "运营商物理通道列表",	//设置表标题
      page: 1,
      colNames: ['id', '物理通道名称','运营商维护','通道类型',  '余额'],
      colModel: [
          { name: 'id', key: true, sortable: false,index:'id' },
          { name: 'NAME', sortable: false,index:'NAME' }, //查询条件要添加index
          { name: 'stProvider',sortable: false,index:'stProvider' },
          { name: 'channel_type', sortable: false ,formatter:channel_typeFormat}, //查询条件要添加index
          { name: 'balance', sortable: false ,formatter: "currency",formatoptions: {thousandsSeparator:",", decimalPlaces:4}}
      ],
      //width: 750,
      height: 'auto',
      multiselect:true,//多选框
      multiboxonly:true,//为true时是单选
      autowidth:true,
      shrinkToFit:true,
      hidegrid: false,	//隐藏表格右上角的"展开/收缩jqGrid内容的小箭头"
      autoScroll: false,
      rowNum : myRowNum,
      rowList : myRowList,
      viewrecords : true,//显示总记录数
      rownumbers: true,
      pager: myJqPageId
  });
  
  $('#name').focus();
  setTimeout(function(){
      $('.wrapper-content').removeClass('animated fadeInRight');
  },700);
  
  setMyActive(6,3); //设置激活页
  
  setSelectStyle($("#channel_type"));
  
  message();
  
});


function add(){
	location.href=basePath+"providerPhysicalChannelAddPage";
}

function edit(id){
	var myBasePath=basePath+'providerPhysicalChannelEditPage/'+id;
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

function del(id){
	var delFlag=confirm("确认要删除么？");
	if(delFlag){
    	var myDelUrl=basePath+'providerPhysicalChannel/'+id;
		$.ajax({
    		type:'DELETE',
    		url :myDelUrl,
    		data:{ suId: id },
    		success:function (dt){
    			location.href=basePath+dt;
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

function detail(id){
	var myBasePath=basePath+'providerPhysicalChannel/'+id;
    location.href=myBasePath;
}

function detailEx(){
	var id=$(myJqTbId).jqGrid('getGridParam','selrow');
	if(id==null){
		outMessage('warning','没有选中的记录！','友情提示');
	}else{
		detail(id);
	}
}

function getDispatcherData(id){
	var myBasePath=basePath+'providerBillDispatcherList/'+id;
    location.href=myBasePath;
}
function providerBillDispatcherList(){
	var id=$(myJqTbId).jqGrid('getGridParam','selrow');
	if(id==null){
		outMessage('warning','没有选中的记录！','友情提示');
	}else{
		getDispatcherData(id);
	}
}

function search(){
	var name=$('#name').val();
	var providerName=$('#providerName').val();
	var channel_type=$('#channel_type').val();
	
    $(myJqTbId).jqGrid('setGridParam',{ 
        url:basePath+'providerPhysicalChannel', 
        postData:{'name':name,'providerName':providerName,channel_type:channel_type}, //发送数据 
        page:1 
    }).trigger("reloadGrid"); //重新载入
}

function channel_typeFormat(cellvalue, options, rowObject){
	cellvalue=cellvalue=='2'?'话费':'物联网卡';
	return cellvalue;
}

function showProviderDataDiscountList(){
	var id=$(myJqTbId).jqGrid('getGridParam','selrow');
	if(id==null){
		outMessage('warning','没有选中的记录！','友情提示');
	}else{
		var myBasePath=basePath+'providerBillDiscountListPage/'+id;
		location.href=myBasePath;
	}
}

function showProviderBillDiscountList(){
	var id=$(myJqTbId).jqGrid('getGridParam','selrow');
	if(id==null){
		outMessage('warning','没有选中的记录！','友情提示');
	}else{
		var myBasePath=basePath+'providerBillDiscountListPage/'+id;
		location.href=myBasePath;
	}
}
function balanceEdit(){
	var id=$(myJqTbId).jqGrid('getGridParam','selrow');
	if(id==null){
		outMessage('warning','没有选中的记录！','友情提示');
	}else{
		var myBasePath=basePath+'providerAccountEditPage/'+id;
		location.href=myBasePath;
	}
}