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
      url: basePath+'tutubiOrder',
      mtype: "GET",
      datatype: "json",
      caption: "兔兔币列表",	//设置表标题
      page: 1,
      colNames: ['id', '蜗牛订单号','消费账号','转出账号','转入账号','号码归属地','资源类型','充值面额(元)','缴费金额','缴费时间','创建时间'],
      colModel: [
          { name: 'id', key: true, sortable: false,hidden:true },
          { name: 'orderId', sortable: false}, //查询条件要添加index
          { name: 'account', sortable: false },
          { name: 'accountSrc', sortable: false },
          { name: 'accountDest', sortable: false , index:'accountDest'},
          { name: 'city', sortable: false },
          { name: 'resource', sortable: false },
          { name: 'money', sortable: false },
          { name: 'rechargeAmount', sortable: false },
          { name: 'rechargeTime', sortable: false },
          { name: 'createTime', sortable: false }
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
  
  setMyActive(3,5); //设置激活页
  $("#creatwTime").val(laydate.now() + ' 00:00:00');
  $("#endCreatwTime").val(laydate.now(1) + ' 00:00:00');
  message();
});

function search(){
	var creatwTime=$('#creatwTime').val();
	var accountDest=$('#accountDest').val();
	var endCreatwTime=$('#endCreatwTime').val();
	
    $(myJqTbId).jqGrid('setGridParam',{ 
        url:basePath+'tutubiOrder', 
        postData:{'creatwTime':creatwTime,'accountDest':accountDest,'endCreatwTime':endCreatwTime}, //发送数据 
        page:1 
    }).trigger("reloadGrid"); //重新载入
}

function searchNet(){
	var creatwTime=$('#creatwTime').val();
	var accountDest=$('#accountDest').val();
	var endCreatwTime=$('#endCreatwTime').val();
	
    $(myJqTbId).jqGrid('setGridParam',{ 
        url:basePath+'tutubiOrderNet', 
        postData:{'creatwTime':creatwTime,'accountDest':accountDest,'endCreatwTime':endCreatwTime}, //发送数据 
        page:1 
    }).trigger("reloadGrid"); //重新载入
}

/*绑定日期控件*/
laydate({
	elem: '#creatwTime',//目标元素。由于laydate.js封装了一个轻量级的选择器引擎，因此elem还允许你传入class、tag但必须按照这种方式 '#id .class'
	event: 'focus', 	//响应事件。如果没有传入event，则按照默认的click
	format: 'YYYY-MM-DD hh:mm:ss', //日期格式
	istime: true, //是否开启时间选择
});

/*绑定日期控件*/
laydate({
	elem: '#endCreatwTime',//目标元素。由于laydate.js封装了一个轻量级的选择器引擎，因此elem还允许你传入class、tag但必须按照这种方式 '#id .class'
	event: 'focus', 	//响应事件。如果没有传入event，则按照默认的click
	format: 'YYYY-MM-DD hh:mm:ss', //日期格式
	istime: true, //是否开启时间选择
});

function deriveExcel(){
	var creatwTime=$('#creatwTime').val();
	var accountDest=$('#accountDest').val();
	var endCreatwTime=$('#endCreatwTime').val();
	var src = basePath+"derive?";
	
	if(creatwTime.length>0){
		src+="&creatwTime="+creatwTime;
	}
	if(accountDest.length>0){
		src+="&accountDest="+accountDest;
	}
	if(endCreatwTime.length>0){
		src+="&endCreatwTime="+endCreatwTime;
	}
	window.location.href = src;
}