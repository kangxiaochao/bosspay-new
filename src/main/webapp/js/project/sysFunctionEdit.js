$(function(){
	getPermission();
	setMyActive(1,4);
});

function submitEdit(){
	var myFormActionUrl=$('#fm').attr("action");
	var sfValue=$('#sfValue').val();
	var sfId=$('#sfId').val();
	var srId = $("#Roles").val();
	var spId = $("#Permission").val();
	$.ajax({
		type:'PUT',
		url :myFormActionUrl,
		data:{ 	sfId: sfId,
				sfValue:sfValue,
				srId:srId,
				spId:spId
		},
		dataType: 'html',
		success:function (dt){
			location.href=basePath+dt;
	    }
	});
}

function getPermission(){
	$("#Permission").empty();
	$("#Roles").empty();
	//ajax 取已经分配的数据，取到以后将得到的数据填充到select中
	var Permission =$('#Permission');
	var myDelUrl=basePath+'sysGetPermissionList';
	$.ajax({
		type:'GET',
		url :myDelUrl,
		success:function (dt){
			var Permission = $("#Permission");
			$.each(dt, function(index,val) {
			      var option = $("<option>").text(val.spNick).val(val.spId)
			      Permission.append(option);
			    });			
			setSelectStyle(Permission);
	    },
	    dataType: 'json'
	});
}
