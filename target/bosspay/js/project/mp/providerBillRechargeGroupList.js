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
      url: basePath+'providerDataRechargeData',
      mtype: "GET",
      datatype: "json",
      caption: "复充话费通道组列表",	//设置表标题
      page: 1,
      colNames: ['id', '运营商','运营商物理通道','地区','优先级','优先级调整'],
      colModel: [
          { name: 'id', key: true, sortable: false,hidden:true },
          { name: 'name', sortable: false,index:'name' } ,//查询条件要添加index
          { name: 'physicalChannel', sortable: false,index:'physicalChannel' }, //查询条件要添加index
          { name: 'provinceCode', sortable: false,index:'provinceCode' },
          { name: 'priority', sortable: false,index:'priority' },
//          { name: 'createDate', sortable: false,index:'createDate' },
          {    name : 'sy',
              width : 150,
              formatter: function (value, grid, rows, state) {
            	  var id = rows.id;
                  var pt = rows.priority;
                  var pvId = rows.providerId;
                  var pc = rows.provinceCode;
                  var up = "up('" + id + "','" + pvId + "','" + pc + "','" + pt + "')";
                  var down = "down('" + id + "','" + pvId + "','" + pc + "','" + pt + "')";
//                  var upTop = "upTop('" + id + "','"+pt+"')";
//                  var downBottom = "downBottom('" + id + "','"+pt+"')";
                  
              return '<input type="button" class="btn btn-xs btn-primary" value="上移"  onclick="'+ up + '"/>'
              +'&nbsp;'
              +'<input type="button" class="btn btn-xs btn-warning" value="下移" onclick="'+ down + '"/>'
//              +'&nbsp;'
//              +'<input type="button" class="btn btn-xs btn-success" value="置顶" onclick="'+ upTop + '"/>'
//              +'&nbsp;'
//              +'<input type="button" class="btn btn-xs btn-danger" value="置后" onclick="'+ downBottom + '"/>'
              ; }
          }
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
  setMyActive(2,6); //设置激活页
  
  message();
});

function up(id,providerId,provinceCode,priority){
	priority=parseInt(priority);
	var up=1;
	if(priority==1){
		outMessage('warning','该复充通道优先级已是最高，无法上移！','友情提示');
	}else{
		$.ajax({
			type:'get',
			url :basePath+'updateRechargePriorityUp/'+up,
			data:{id:id,providerId:providerId,provinceCode:provinceCode,priority:priority},
			success:function (dt){
				if(dt[0]=="success"){
					search();
				}else{
					outMessage('warning','上移优先级失败！','友情提示');
				}
		    },
		    dataType: 'json'
		});
	}
};

function down(id,providerId,provinceCode,priority){
	var down = 2;
	priority=parseInt(priority);
//	if(priority==1){
//		outMessage('warning','该复充通道优先级已是最高，无法上移！','友情提示');
//	}else{
	$.ajax({
		type:'get',
		url :basePath+'updateRechargePriorityUp/'+down,
		data:{id:id,providerId:providerId,provinceCode:provinceCode,priority:priority},
		success:function (dt){
			if(dt[0]=="success"){
				search();
			}else if(dt[0]=="maxPr"){
				outMessage('warning','该复充通道优先级已是最低，无法下移！','友情提示');
			}else{
				outMessage('warning','上移优先级失败！','友情提示');
			}
	    },
	    dataType: 'json'
	});
//	}
};

function add(){
	location.href=basePath+"providerDataRechargeGroupAddPage";
}

function edit(id){
	var myBasePath=basePath+'providerDataRechargeGroupEditPage/'+id;
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
	 swal({
         title: "请确认",
         text: "是否删除？",
         type: "warning",
         showCancelButton: true,
         confirmButtonColor: "#DD6B55",
         confirmButtonText: "是",
         cancelButtonText: "否",
         closeOnConfirm: true,
         animation: "slide-from-top"
     }, function () {
    	 var myDelUrl=basePath+'deleteRechargeGroupById/'+id;
 		$.ajax({
     		type:'DELETE',
     		url :myDelUrl,
     		data:{ suId: id },
     		success:function (dt){
     			if(dt[0]=="success"){
    				search();
    			}else{
    				outMessage('warning','删除失败！','友情提示');
    			}
     	    }
     	    
     	});
     });
}

function delEx(){
	var id=$(myJqTbId).jqGrid('getGridParam','selrow');
//	var rowData = $(myJqTbId).jqGrid('getRowData',id);
	if(id==null){
		outMessage('warning','没有选中的记录！','友情提示');
	}else{
		del(id);
	}
}

function search(){
	var name=$('#name').val();
	var physicalChannel=$('#physicalChannel').val();
	
    $(myJqTbId).jqGrid('setGridParam',{ 
        url:basePath+'providerDataRechargeData', 
        postData:{'name':name,'physicalChannel':physicalChannel}, //发送数据 
        page:1 
    }).trigger("reloadGrid"); //重新载入
}

function dispatchProvider(){
	var id=$(myJqTbId).jqGrid('getGridParam','selrow');
	if(id==null){
		outMessage('warning','没有选中的记录！','友情提示');
	}else{
		var myBasePath=basePath+'providerGroupDataRelEditPage/'+id;
		location.href=myBasePath;
	}
}
