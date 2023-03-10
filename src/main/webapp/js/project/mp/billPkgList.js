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
      url: basePath+'billPkg',
      mtype: "GET",
      datatype: "json",
      caption: "话费包列表",	//设置表标题
      page: 1,
      colNames: ['id', '话费包名称','充值金额(元)'],
      colModel: [
          { name: 'id', key: true, sortable: false,hidden:true },
          { name: 'name', sortable: false,index:'billPkgName' }, //查询条件要添加index
          { name: 'value', sortable: false  }
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
  
  $('#billPkgName').focus();
  setTimeout(function(){
      $('.wrapper-content').removeClass('animated fadeInRight');
  },700);
  
  setMyActive(2,2); //设置激活页
  
  message();
});

function add(){
	location.href=basePath+"billPkgAddPage";
}

function edit(id){
	var myBasePath=basePath+'billPkgEditPage/'+id;
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

function setBillPkgDiscount(){
	var id=$(myJqTbId).jqGrid('getGridParam','selrow');
	if(id==null){
		outMessage('warning','没有选中的记录！','友情提示');
	}else{
		var myBasePath=basePath+'providerBillDiscountEditPage/'+id;
		location.href=myBasePath;
	}
}

function del(ids){
	layer.confirm('确认要删除吗?', {skin: 'layui-layer-molv',icon: 3, title:'提示'}, function(index){
		var myDelUrl=basePath+'billPkg/'+ids;
		$.ajax({
    		type:'DELETE',
    		url :myDelUrl,
    		success:function (dt){
    			location.href=basePath+dt;
    	    },
    	    dataType: 'html'
    	});
	});      
}

function delEx(){
	var ids = $(myJqTbId).jqGrid('getGridParam', 'selarrrow');	// 获取所有选中行id
	if(ids == null || ids == ''){
		outMessage('warning','没有选中的记录！','友情提示');
	}else{
		del(ids);
	}
}

function search(){
	var billPkgName=$('#billPkgName').val();
	
    $(myJqTbId).jqGrid('setGridParam',{ 
        url:basePath+'billPkg', 
        postData:{'billPkgName':billPkgName}, //发送数据 
        page:1 
    }).trigger("reloadGrid"); //重新载入
}
