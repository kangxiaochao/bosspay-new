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
      url: basePath+'sysFunction', //修改这里1
      mtype: "GET",
      datatype: "json",
      caption: "功能列表",	//设置表标题
      page: 1,
      colNames: ['sfId', '功能地址',  '权限表达式','权限别名','角色名称','访问类型','添加时间'], //修改这里2
      colModel: [                               //修改这里3
          { name: 'sfId', key: true, sortable: false,hidden:true },
          { name: 'sfValue', sortable: false,index:'sfValue' }, //查询条件要添加index
          { name: 'spName', sortable: false },
          { name: 'spNick', sortable: false },
          { name: 'srName', sortable: false },
          { name: 'sfType', sortable: false },
          { name: 'sfAddTime', sortable: false }
      ],
      //width: 750,
      height: 'auto',
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
  
  $('#sfValue').focus(); //修改这里4
  
  setTimeout(function(){
      $('.wrapper-content').removeClass('animated fadeInRight');
  },700);
  setMyActive(1,4); //设置激活页
  
  message();
});

function add(){
	location.href=basePath+"sysFunctionAddPage"; //修改这里5
}

function detail(id){
	var myBasePath=basePath+'sysFunction/'+id; //修改这里6
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
	var myBasePath=basePath+'sysFunctionChangePassPage/'+id; //修改这里7
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
	var myBasePath=basePath+'sysFunctionEditPage/'+id; //修改这里8
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
    	var myDelUrl=basePath+'sysFunction/'+id; //修改这里9
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
	var sfValue=$('#sfValue').val();  //修改这里10
	
    $(myJqTbId).jqGrid('setGridParam',{ 
        url:basePath+'sysFunction', //修改这里11
        postData:{'sfValue':sfValue}, //修改这里12 
        page:1 
    }).trigger("reloadGrid"); //重新载入
}