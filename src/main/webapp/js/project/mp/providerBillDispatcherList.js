  var myTbContentId='mytbc1';
  var myJqTbContentId='#'+myTbContentId;
  var myTbId='myt1';
  var myJqTbId='#'+myTbId;
  var myPageId='myp1';
  var myJqPageId='#'+myPageId;
  var id = $('#queryDiv').attr('data-physicalId');
$(function () {
  var id = $('#queryDiv').attr('data-physicalId');
  var t1=$('<table></table>');
  t1.attr('id',myTbId);
  $(myJqTbContentId).append(t1);
  
  var p1=$('<div></div>');
  p1.attr('id',myPageId);
  
  p1.insertAfter(t1);
  
  $(myJqTbId).jqGrid({
      url: basePath+'providerBillDispatcherListBill/'+id,
      mtype: "GET",
      datatype: "json",
      caption: "运营商物理通道拆分通道",	//设置表标题
      page: 1,
      colNames: ['id', '运营商','地区','当前状态','操作'],
      colModel: [
          { name: 'id', key: true, sortable: false,hidden:true },
          { name: 'name',width:'30',align: 'center', sortable: false} ,
          { name: 'provinceCode', sortable: false },
          { name : 'delFlag',sortable : false,width:'30',align: 'center',formatter: function(delFlag,obj){
          	 if(delFlag==0){
          		 return '停用';
          	 }else if (delFlag==1){
          		 return '启用';
          	 }
           }},
          { name : 'delFlag',sortable : false,width:'30',align: 'center',formatter: function(delFlag,obj){
         	 if(delFlag==0){
         		 return '<button type="button" class="btn btn-primary" onclick="confirm(\''+delFlag+'\',\''+obj.rowId+'\')">启用</button>';
         	 }else if (delFlag==1){
         		 return '<button type="button" class="btn btn-danger" onclick="confirm(\''+delFlag+'\',\''+obj.rowId+'\')">停用</button>';
         	 }
          }}
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
  message();
});

function search(){
	var providerName=$('#providerName').val();
	var provinceCode=$('#provinceCode').val();
    $(myJqTbId).jqGrid('setGridParam',{ 
        url:basePath+'providerBillDispatcherListBill/'+id,
        postData:{'providerName':providerName,'provinceCode':provinceCode}, //发送数据 
        page:1 
    }).trigger("reloadGrid"); //重新载入
}

function confirm(flag,id){
		$.ajax({
			type:'GET',
			url:basePath+'changeDispatcherStatus',
			data:{
				id:id,
				flag:flag
			},
			dataType:'html',
			success:function(dt){
				debugger;
				search();
				/*if(dt=="success"){
					search();
				}else{
					
				}*/
			}
		});
}

function editEx(){
	var myBasePath=basePath+'providerPhysicalChannelDispatcherEdit/'+id;
	location.href=myBasePath;
}