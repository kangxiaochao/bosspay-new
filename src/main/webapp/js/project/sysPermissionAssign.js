


$(function(){
	getHasPermission();
	getNoPermission();
	setMyActive(1,2);
});

function getArr(){
	var selectArr = $("#hasPermission").children("option");
	var datas = "";
	for(var i = 0 ; i < selectArr.length ; i++){
		datas+= $(selectArr[i]).attr("value")+",";
	}
	$("#hasPermissionIds").val(datas);
}

function getHasPermission(){
	//ajax 取已经分配的数据，取到以后将得到的数据填充到右边的select中
	var srId=$('#srId').val();//获取角色ID
	var myDelUrl=basePath+'sysGetHasPermission/'+srId;
	$.ajax({
		type:'GET',
		url :myDelUrl,
		success:function (dt){
			var hasPermission = $("#hasPermission");
			$.each(dt, function(index,val) {
			      var option = $("<option>").text(val.spNick).val(val.spId)
			      hasPermission.append(option);
			    });
			
	    },
	    dataType: 'json'
	});
}

function getNoPermission(){
	//ajax 取未分配的数据，取到以后将得到的数据填充到左边的select中
	var srId=$('#srId').val();
	var myDelUrl=basePath+'sysGetNoPermission/'+srId;
	$.ajax({
		type:'GET',
		url :myDelUrl,
		success:function (dt){
			var noPermission = $("#noPermission");
			$.each(dt, function(index,val) {
			      var option = $("<option>").text(val.spNick).val(val.spId)
			      noPermission.append(option);
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
