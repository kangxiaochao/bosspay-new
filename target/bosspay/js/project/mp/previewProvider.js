  var myTbContentId='mytbc1';
  var myJqTbContentId='#'+myTbContentId;
  var myTbId='myt1';
  var myJqTbId='#'+myTbId;
  var myPageId='myp1';
  var myJqPageId='#'+myPageId;

$(function () {
	var id = $('#selectId').attr('data-id');
  var t1=$('<table></table>');
  t1.attr('id',myTbId);
  $(myJqTbContentId).append(t1);
  
  var p1=$('<div></div>');
  p1.attr('id',myPageId);
  
  p1.insertAfter(t1);
  
  
  $(myJqTbId).jqGrid({
      url: basePath+'providerPkgData/'+id,
      mtype: "GET",
      datatype: "json",
      caption: "话费包列表",	//设置表标题
      page: 1,
      colNames: ['id', '地区',  '话费包'],
      colModel: [
          { name: 'id', key: true, sortable: false,hidden:true },
          { name: 'province_code', sortable: false,index:'province_code' }, //查询条件要添加index
          { name: 'name', sortable: false ,index:'name' }
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
  
//  $('#name').focus();
  setTimeout(function(){
      $('.wrapper-content').removeClass('animated fadeInRight');
  },700);
  setMyActive(6,1); //设置激活页
});

function uploadProviderPkg(){
	$('#commitBox').val('');
	$('#commitBox').click();
}
function commitBoxChange(){
	var id=$('#commitBox').attr('data-id');
	$("#depEditForm").ajaxSubmit({
        url: basePath + "commitProviderPkg/"+id,
        dataType: 'json',
        success: function (data) {
        	if(data.length<=0){
        		swal({
        	        title: "成功!",
        	        type: "success",
        	        confirmButtonText: "ok"
        	    },function(){
        	    	search(id);
        	    });
        	}else{
        		var errorMsg="";
        		for(var i=0;i<data.length;i++){
        			errorMsg+=data[i]+"<br/>";
        		}
        		outMessage('warning',errorMsg,'友情提示');
        	}
        }
	})
}

function search(id){
	$(myJqTbId).jqGrid('setGridParam',{ 
        url:basePath+'providerPkgData/'+id, 
//        postData:{'id':id}, //发送数据 
        page:1 
    }).trigger("reloadGrid"); //重新载入
}

function downLoadTemp(){
	$('#downloadFrame').attr('src',basePath + "downloadFiles/dataPkgUploadTemp.xlsx");
}
