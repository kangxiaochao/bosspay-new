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
      url: basePath+'sysUser',
      mtype: "GET",
      datatype: "json",
      caption: "用户列表",	//设置表标题
      page: 1,
      colNames: ['suId', '用户名',  '手机号','别名'],
      colModel: [
          { name: 'suId', key: true, sortable: false,hidden:true },
          { name: 'suName', sortable: false,index:'suName' }, //查询条件要添加index
//          { name: 'suPass', sortable: false },
//          { name: 'suCredits', sortable: false },
          { name: 'suMobile', sortable: false },
          { name: 'suNick', sortable: false },
//          { name: 'suQq', sortable: false },
//          { name: 'suEmail', sortable: false },
         // { name: '_opera', sortable: false ,width:'80',align:'center',title : false},
      ],
      //width: 750,
      height: 'auto',
      autowidth:true,
      shrinkToFit:true,
      hidegrid: false,	//隐藏表格右上角的"展开/收缩jqGrid内容的小箭头"
      autoScroll: false,
      rowNum: myRowNum,
      rowList : myRowList,
      viewrecords : true,//显示总记录数
      rownumbers: true,
      pager: myJqPageId
  });
  
  $('#suName').focus();
  setTimeout(function(){
      $('.wrapper-content').removeClass('animated fadeInRight');
  },700);
  setMyActive(1,1); //设置激活页
  
  message();
});

function add(){
	location.href=basePath+"sysUserAddPage";
}

function detail(id){
	var myBasePath=basePath+'sysUser/'+id;
    location.href=myBasePath;
}

function assignUser(id){
	var myBasePath=basePath+'sysUserRoleList/'+id; //角色分配页面
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

function changePass(id){
	var myBasePath=basePath+'sysUserChangePassPage/'+id;
    location.href=myBasePath;

}

function changePassEx(){
	var id=$(myJqTbId).jqGrid('getGridParam','selrow');
	if(id==null){
		outMessage('warning','没有选中的记录！','友情提示');
	}else{
		changePass(id);
	}
}


function edit(id){
	var myBasePath=basePath+'sysUserEditPage/'+id;
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

function assign(){
	var id=$(myJqTbId).jqGrid('getGridParam','selrow');
	if(id==null){
		outMessage('warning','没有选中的记录！','友情提示');
	}else{
		assignUser(id);
	}
}     

function del(id){
	var delFlag=confirm("确认要删除么？");
	if(delFlag){
    	var myDelUrl=basePath+'sysUser/'+id;
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
	var suName=$('#suName').val();
	
    $(myJqTbId).jqGrid('setGridParam',{ 
        url:basePath+'sysUser', 
        postData:{'suName':suName}, //发送数据 
        page:1 
    }).trigger("reloadGrid"); //重新载入
}
