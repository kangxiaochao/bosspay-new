  var myTbContentId='mytbc1';
  var myJqTbContentId='#'+myTbContentId;
  var myTbId='myt1';
  var myJqTbId='#'+myTbId;
  var myPageId='myp1';
  var myJqPageId='#'+myPageId;
  
  getAgentId();
  /*获取话费通道组列表*/
  function getAgentId(){
  	var dataGroupId = $("#agentId");
  	var myDelUrl = basePath + 'agentDiscountList';
//  	var option = $("<option>").text('无').val('');
//  	dataGroupId.append(option);
  	$.ajax({
  		type:'get',
  		url :myDelUrl,
  		success:function (dt){
  			$.each(dt, function(index,val) {  
  				var option = $("<option>").text(val.name        ).val(val.id)
  				dataGroupId.append(option);
  			});
  			setSelectStyle(dataGroupId);
  			getProviderId();
  		},
  		dataType: 'json'
  	});
  }
  
  /*获取话费通道组列表*/
  function getProviderId(){
  	var dataGroupId = $("#providerId");
  	var myDelUrl = basePath + 'agentProviderList';
//  	var option = $("<option>").text('无').val('');
//  	dataGroupId.append(option);
  	$.ajax({
  		type:'get',
  		url :myDelUrl,
  		success:function (dt){
  			$.each(dt, function(index,val) {  
  				var option = $("<option>").text(val.name).val(val.id)
  				dataGroupId.append(option);
  			});
  			setSelectStyle(dataGroupId);
  			searcher()
  		},
  		dataType: 'json'
  	});
  }
  
  function searcher(){
	  var id = $('#selectId').attr('data-id');
	  var t1=$('<table></table>');
	  t1.attr('id',myTbId);
	  $(myJqTbContentId).append(t1);
	  
	  var p1=$('<div></div>');
	  p1.attr('id',myPageId);
	  
	  p1.insertAfter(t1);
	  
	  	var agentId=$('#agentId').val();
		var providerId=$('#providerId').val();
	  $(myJqTbId).jqGrid({
	      url: basePath+'agentBillDiscountList/'+agentId+'/'+providerId,
	      mtype: "GET",
	      datatype: "json",
	      caption: "话费包列表",	//设置表标题
	      page: 1,
	      colNames: ['地区',  '10','20','30','50','100','200','300','500'],
	      colModel: [
	          { name: 'provinceCode', sortable: false,index:'provinceCode',align: "center"}, 
	          { name: 'fee10', sortable: false ,width : '180',index:'fee10',align: "center",editable: true}, 
	          { name: 'fee20', sortable: false ,width : '180',index:'fee20',align: "center",editable: true}, 
	          { name: 'fee30', sortable: false ,width : '180',index:'fee30',align: "center" }, 
	          { name: 'fee50', sortable: false ,width : '180',index:'fee50',align: "center" }, 
	          { name: 'fee100', sortable: false ,width : '180',index:'fee100',align: "center" }, 
	          { name: 'fee200', sortable: false ,width : '180',index:'fee200',align: "center" }, 
	          { name: 'fee300', sortable: false ,width : '180',index:'fee300',align: "center" }, 
	          { name: 'fee500', sortable: false ,width : '170',index:'fee500',align: "center" }
	      ],
	      width: 750,
	      height: 'auto',
	      autowidth:true,
	      shrinkToFit:false,
	      hidegrid: false,	//隐藏表格右上角的"展开/收缩jqGrid内容的小箭头"
	      autoScroll: false,
	      cellEdit:true,//是否开启单元格的编辑功能  
	      cellsubmit:'remote',//or 'clientArray',remote代表每次编辑提交后进行服务器保存，clientArray只保存到数据表格不保存到服务器  
	      cellurl:'xxx',//cellsubmit要提交的后台路径  
	      rowNum : myRowNum,
	      rowList : myRowList,
	      viewrecords : true,//显示总记录数
	      rownumbers: true,
	      pager: myJqPageId
	      
	  });
	//合并表头单元格
	  $(myJqTbId).jqGrid('setGroupHeaders', {
		    useColSpanStyle: true,
		    groupHeaders:[
				{startColumnName:'fee10', numberOfColumns:8, titleText: '<div align="center">单位(元)</div>'}
		    ] 
		})
	//  $('#name').focus();
	  setTimeout(function(){
	      $('.wrapper-content').removeClass('animated fadeInRight');
	  },700);
	  setMyActive(5,5); //设置激活页
  }
  
$(function () {
//  var id = $('#selectId').attr('data-id');
//  var t1=$('<table></table>');
//  t1.attr('id',myTbId);
//  $(myJqTbContentId).append(t1);
//  
//  var p1=$('<div></div>');
//  p1.attr('id',myPageId);
//  
//  p1.insertAfter(t1);
//  
//  	var agentId=$('#agentId').val();
//	var providerId=$('#providerId').val();
//  $(myJqTbId).jqGrid({
//      url: basePath+'agentBillDiscountList/'+agentId+'/'+providerId,
//      mtype: "GET",
//      datatype: "json",
//      caption: "话费包列表",	//设置表标题
//      page: 1,
//      colNames: ['地区',  '10','20','30','50','100','200','300','500'],
//      colModel: [
//          { name: 'provinceCode', sortable: false,index:'provinceCode',align: "center" }, 
//          { name: 'fee10', sortable: false ,width : '180',index:'fee10',align: "center" }, 
//          { name: 'fee20', sortable: false ,width : '180',index:'fee20',align: "center" }, 
//          { name: 'fee30', sortable: false ,width : '180',index:'fee30',align: "center" }, 
//          { name: 'fee50', sortable: false ,width : '180',index:'fee50',align: "center" }, 
//          { name: 'fee100', sortable: false ,width : '180',index:'fee100',align: "center" }, 
//          { name: 'fee200', sortable: false ,width : '180',index:'fee200',align: "center" }, 
//          { name: 'fee300', sortable: false ,width : '180',index:'fee300',align: "center" }, 
//          { name: 'fee500', sortable: false ,width : '170',index:'fee500',align: "center" }
//      ],
//      width: 750,
//      height: 'auto',
//      autowidth:true,
//      shrinkToFit:false,
//      hidegrid: false,	//隐藏表格右上角的"展开/收缩jqGrid内容的小箭头"
//      autoScroll: false,
//      rowNum : myRowNum,
//      rowList : myRowList,
//      viewrecords : true,//显示总记录数
//      rownumbers: true,
//      pager: myJqPageId
//      
//  });
////合并表头单元格
//  $(myJqTbId).jqGrid('setGroupHeaders', {
//	    useColSpanStyle: true,
//	    groupHeaders:[
//			{startColumnName:'fee10', numberOfColumns:8, titleText: '<div align="center">单位(元)</div>'}
//	    ] 
//	})
////  $('#name').focus();
//  setTimeout(function(){
//      $('.wrapper-content').removeClass('animated fadeInRight');
//  },700);
//  setMyActive(5,6); //设置激活页
});

function uploadProviderPkg(){
	$('#commitBox').val('');
	$('#commitBox').click();
}
function commitBoxChange(){
	var agentId=$('#agentId').val();
	var providerId=$('#providerId').val();
	if(""==agentId){
		outMessage('warning','存在未选择的项！','友情提示');
	}else{
		outMessage('info','正在导入,请耐心等候！','友情提示');
		$("#depEditForm").ajaxSubmit({
	        url: basePath + "commitAgentBillDiscount/"+agentId,
	        dataType: 'json',
	        success: function (data) {
	        	if(data.length<=0){
	        		swal({
	        	        title: "成功!",
	        	        type: "success",
	        	        confirmButtonText: "ok"
	        	    },function(){
	        	    	search();
	        	    });
	        	}else{
	        		var errorMsg="";
	        		for(var i=0;i<data.length;i++){
	        			errorMsg+=data[i]+"<br/>";
	        		}
	        		outMessage('warning',errorMsg,'友情提示');
	        		search();
	        	}
	        }
		})
	}
	
}

function search(){
	var agentId=$('#agentId').val();
	var providerId=$('#providerId').val();
	if(""==agentId||""==providerId){
		outMessage('warning','存在未选择的项！','友情提示');
	}else{
		$(myJqTbId).jqGrid('setGridParam',{ 
	        url:basePath+'agentBillDiscountList/'+agentId+'/'+providerId, 
	        page:1 
	    }).trigger("reloadGrid"); //重新载入
	}
	
}

function downLoadTemp(){
	$('#downloadFrame').attr('src',basePath + "downloadFiles/代理商折扣模板.xlsx");
}
