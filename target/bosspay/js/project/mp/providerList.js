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
      url: basePath+'provider',
      mtype: "GET",
      datatype: "json",
      caption: "运营商列表",	//设置表标题
      page: 1,
      colNames: ['id', '名称',  '简称'],
      colModel: [
          { name: 'id', key: true, sortable: false,hidden:true },
          { name: 'name', sortable: false,index:'name' }, //查询条件要添加index
          { name: 'short_name', sortable: false ,index:'short_name' }/*,
          //formatter: "currency",formatoptions: {thousandsSeparator:",", defaulValue:"",decimalPlaces:4} 是科学显示金额并保留4位小数
          { name: 'balance', sortable: false,formatter: "currency",formatoptions: {thousandsSeparator:",", defaulValue:"",decimalPlaces:4}  }*/
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
  
  setMyActive(6,1); //设置激活页
  
  message();
});

function add(){
	location.href=basePath+"providerAddPage";
}

function edit(id){
	var myBasePath=basePath+'providerEditPage/'+id;
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

function selectProviderDataPkg(id){
	var myBasePath=basePath+'providerDataPkgList/'+id;
	location.href=myBasePath;
}

function previewDataProvider(){
	var id=$(myJqTbId).jqGrid('getGridParam','selrow');
	if(id==null){
		outMessage('warning','没有选中的记录！','友情提示');
	}else{
		selectProviderDataPkg(id);
	}
}

function selectProviderBillPkg(id){
	var myBasePath=basePath+'providerBillPkgList/'+id;
	location.href=myBasePath;
}

function previewBillProvider(){
	var id=$(myJqTbId).jqGrid('getGridParam','selrow');
	if(id==null){
		outMessage('warning','没有选中的记录！','友情提示');
	}else{
		selectProviderBillPkg(id);
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

function del(id){
	var delFlag=confirm("确认要删除么？");
	if(delFlag){
    	var myDelUrl=basePath+'provider/'+id;
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

function search(){
	var name=$('#name').val();
	var short_name=$('#short_name').val();
	
    $(myJqTbId).jqGrid('setGridParam',{ 
        url:basePath+'provider', 
        postData:{'name':name,'short_name':short_name}, //发送数据 
        page:1 
    }).trigger("reloadGrid"); //重新载入
}

/**
 * 设置权重加强版本
 */
function setWeightEx(){
	var id=$(myJqTbId).jqGrid('getGridParam','selrow');
	if(id==null){
		outMessage('warning','没有选中的记录！','友情提示');
	}else{
		setWeight(id);
	}
}

/**
 * 设置权重
 * @param id 运营商编号
 */
function setWeight(id){
	var myBasePath=basePath+'providerDataDispatcherEditPage/'+id;
	location.href=myBasePath;
}
