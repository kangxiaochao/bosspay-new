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
      url: basePath+'queryTmallProductList',
      mtype: "GET",
      datatype: "json",
      caption: "天猫产品列表",	//设置表标题
      page: 1,
      colNames: ['id', '产品ID',  '省份','话费包大小','基础售价','运营商','话费类型'],
      colModel: [
          { name: 'ids', key: true, sortable: false,hidden:true },
          { name: 'spuid', sortable: false,index:'spuid' }, //查询条件要添加index
          { name: 'province', sortable: false ,index:'province' },
          { name: 'pkgs', sortable: false  },
          { name: 'price', sortable: false  },
          { name: 'carr', sortable: false  },
          { name: 'flowtype', sortable: false  }
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
  
  $('#spuid').focus();
  setTimeout(function(){
      $('.wrapper-content').removeClass('animated fadeInRight');
  },700);
  
  setMyActive(2,6); //设置激活页
  
  message();
});

function add(){
	location.href=basePath+"tmallProductAddPage";
}

function edit(id){
	var myBasePath=basePath+'tmallProductEditPage/'+id;
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
    	var myDelUrl=basePath+'tmallProductDel/'+id;
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
	var spuid=$('#spuid').val();
    $(myJqTbId).jqGrid('setGridParam',{ 
        url:basePath+'queryTmallProductList', 
        postData:{'spuid':spuid}, //发送数据 
        page:1 
    }).trigger("reloadGrid"); //重新载入
}


