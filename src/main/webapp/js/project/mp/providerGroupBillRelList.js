  var myTbContentId='mytbc1';
  var myJqTbContentId='#'+myTbContentId;
  var myTbId='myt1';
  var myJqTbId='#'+myTbId;
  var myPageId='myp1';
  var myJqPageId='#'+myPageId;
  var id = $('#groupId').val();
$(function () {
  var id = $('#groupId').val();
  var t1=$('<table></table>');
  t1.attr('id',myTbId);
  $(myJqTbContentId).append(t1);
  
  var p1=$('<div></div>');
  p1.attr('id',myPageId);
  
  p1.insertAfter(t1);
  
  $(myJqTbId).jqGrid({
      url: basePath+'providerGroupBillRelList/'+id,
      mtype: "GET",
      datatype: "json",
      caption: "话费运营商组列表",	//设置表标题
      page: 1,
      colNames: ['id', '通道名','运营商','省份','城市','物理通道','修改时间','修改人','创建时间','创建人'],
      colModel: [
          { name: 'id', key: true, sortable: false,hidden:true },
          { name: 'channelName', sortable: false} ,
          { name: 'providerName', sortable: false,index:'providerName' } ,
          { name: 'province_code', sortable: false} ,
          { name: 'city_code', sortable: false} ,
          { name: 'physicalName', sortable: false,index:'physicalName' } ,
          { name: 'update_date', sortable: false} ,
          { name: 'update_user', sortable: false} ,
          { name: 'create_date', sortable: false} ,
          { name: 'userName', sortable: false} 
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

function search(){
	var providerName=$('#providerName').val();
	var provinceCode=$('#provinceCode').val();
    $(myJqTbId).jqGrid('setGridParam',{ 
        url:basePath+'providerGroupBillRelList/'+id,
        postData:{'providerName':providerName,'provinceCode':provinceCode}, //发送数据 
        page:1 
    }).trigger("reloadGrid"); //重新载入
}

function editEx(){
	var myBasePath=basePath+'providerGroupBillRelEditPage/'+id;
	location.href=myBasePath;
}

 