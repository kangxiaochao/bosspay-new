  var myTbContentId='mytbc1';
  var myJqTbContentId='#'+myTbContentId;
  var myTbId='myt1';
  var myJqTbId='#'+myTbId;
  var myPageId='myp1';
  var myJqPageId='#'+myPageId;
  var agentId = $('#queryDiv').attr('data-agentId');
  var groupId = $('#queryDiv').attr('data-groupId');
$(function () {
  var agentId = $('#queryDiv').attr('data-agentId');
  var groupId = $('#queryDiv').attr('data-groupId');
  var t1=$('<table></table>');
  t1.attr('id',myTbId);
  $(myJqTbContentId).append(t1);
  var p1=$('<div></div>');
  p1.attr('id',myPageId);
  
  p1.insertAfter(t1);
  
  $(myJqTbId).jqGrid({
      url: basePath+'agentChannelRelList/'+agentId,
      mtype: "GET",
      datatype: "json",
      caption: "代理商特惠通道",	//设置表标题
      page: 1,
      colNames: ['id','运营商', '物理通道','地区'],
      colModel: [
          { name: 'id', key: true, sortable: false,hidden:true },
          { name: 'providerName', sortable: false} ,
          { name: 'name', sortable: false} ,
          { name: 'provinceCode', sortable: false } 
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
  setMyActive(5,1); //设置激活页
  message();
});

function search(){
	var physicalName=$('#physicalName').val();
	var provinceCode=$('#provinceCode').val();
    $(myJqTbId).jqGrid('setGridParam',{ 
        url:basePath+'agentChannelRelList/'+agentId,
        postData:{'physicalName':physicalName,'provinceCode':provinceCode}, //发送数据 
        page:1 
    }).trigger("reloadGrid"); //重新载入
}

function editEx(){
	var myBasePath=basePath+'settingAgentChannelRelByAgentId/'+agentId+'/'+groupId;
	location.href=myBasePath;
}