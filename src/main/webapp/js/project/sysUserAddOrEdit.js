


$(function(){
	getHasUser();
	getNoUser();	
	setMyActive(1,1);
});

function getArr(){
	var selectArr = $("#hasUser").children("option");
	var datas = "";
	for(var i = 0 ; i < selectArr.length ; i++){
		datas+= $(selectArr[i]).attr("value")+",";
	}
	$("#hasUserIds").val(datas);
}

function getHasUser(){
	//ajax 取已经分配的数据，取到以后将得到的数据填充到右边的select中
	var srId=$('#srId').val();//获取角色ID
	var myDelUrl=basePath+'sysGetHasUser/'+srId;
	$.ajax({
		type:'GET',
		url :myDelUrl,
		success:function (dt){
			var hasUser = $("#hasUser");
			$.each(dt, function(index,val) {
			      var option = $("<option>").text(val.suName).val(val.suId)
			      hasUser.append(option);
			    });
			
	    },
	    dataType: 'json'
	});
}



function getNoUser(){
	//ajax 取未分配的数据，取到以后将得到的数据填充到左边的select中
	var srId=$('#srId').val();
	var myDelUrl=basePath+'sysGetNoUser/'+srId;
	$.ajax({
		type:'GET',
		url :myDelUrl,
		success:function (dt){
			var noUser = $("#noUser");
			$.each(dt, function(index,val) {
			      var option = $("<option>").text(val.suName).val(val.suId)
			      noUser.append(option);
			    });
			
	    },
	    dataType: 'json'
	});
}

function sourceToTargetSe(sourceId,targetId,isSelected){
 	$('#'+sourceId+' option'+(isSelected?':selected':'')).each(function(){
	$('#'+targetId).append('<option value="'+$(this).val()+'">'+$(this).html()+'</option>');
	$(this).remove();
	});
 }
