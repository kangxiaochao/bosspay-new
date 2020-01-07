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
      url: basePath+'providerBillGroup',
      mtype: "GET",
      datatype: "json",
      caption: "话费通道组列表",	//设置表标题
      page: 1,
      colNames: ['id', '话费通道组名','创建时间','修改时间','创建人'],
      colModel: [
          { name: 'id', key: true, sortable: false,hidden:true },
          { name: 'name', sortable: false,index:'name' } ,//查询条件要添加index
          { name: 'createDate', sortable: false} ,
          { name: 'updateDate', sortable: false} ,
          { name: 'crtName', sortable: false,index:'crtName'}
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
  setMyActive(2,4); //设置激活页
  
  message();
});

function add(){
	location.href=basePath+"providerBillGroupAddPage";
}

function edit(id){
	var myBasePath=basePath+'providerBillGroupEditPage/'+id;
	location.href=myBasePath;
}

function editEx(){
//	var id=$(myJqTbId).jqGrid('getGridParam','selrow');
//	if(id==null){
//		outMessage('warning','没有选中的记录！','友情提示');
//	}else{
//		edit(id);
//	}
	
	var id=$(myJqTbId).jqGrid('getGridParam','selrow');
	if(id==null){
		outMessage('warning','没有选中的记录！','友情提示');
	}else{
		var suId = $('#leftMenuDiv').attr('data-suId');
		$.ajax({
			type:'get',
			url :basePath+'getBillGroupRoleFlag/'+suId+'/'+id,
			data:{},
			success:function (dt){
				var roleFlag = dt["roleFlag"];
				var queryFlag = dt["queryFlag"];
				if(roleFlag){
					if(queryFlag){
						edit(id);
					}else{
						outMessage('warning','非本用户创建，无法修改！','友情提示');
					}
				}else{
					edit(id);
				}
		    },
		    dataType: 'json'
		});
		
	}
}

function detail(id){
	var myBasePath=basePath+'providerBillGroupDetailPage/'+id;
	location.href=myBasePath;
}

function detailEx(){
	var id=$(myJqTbId).jqGrid('getGridParam','selrow');
	if(id==null){
		outMessage('warning','没有选中的记录！','友情提示');
	}else{
		var suId = $('#leftMenuDiv').attr('data-suId');
		$.ajax({
			type:'get',
			url :basePath+'getBillGroupRoleFlag/'+suId+'/'+id,
			data:{},
			success:function (dt){
				var roleFlag = dt["roleFlag"];
				var queryFlag = dt["queryFlag"];
				if(roleFlag){
					if(queryFlag){
						detail(id);
					}else{
						outMessage('warning','非本用户创建，无法修改！','友情提示');
					}
				}else{
					detail(id);
				}
		    },
		    dataType: 'json'
		});
	}
}

function del(id){
	var delFlag=confirm("确认要删除么？");
	if(delFlag){
    	var myDelUrl=basePath+'providerBillGroup/'+id;
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
    $(myJqTbId).jqGrid('setGridParam',{ 
        url:basePath+'providerBillGroup', 
        postData:{'name':name}, //发送数据 
        page:1 
    }).trigger("reloadGrid"); //重新载入
}

function dispatchProvider(){
	var id=$(myJqTbId).jqGrid('getGridParam','selrow');
	if(id==null){
		outMessage('warning','没有选中的记录！','友情提示');
	}else{
		var myBasePath=basePath+'providerGroupBillRelEditPage/'+id;
		location.href=myBasePath;
	}
}