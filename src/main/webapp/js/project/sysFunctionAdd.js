$(function(){
	getPermission();
	setMyActive(1,4);
});

function getPermission(){
	$("#Permission").empty();
	$("#Roles").empty();
	var Permission =$('#Permission');
	//ajax 取已经分配的数据，取到以后将得到的数据填充到select中
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

function getArr(){
	var srId = $("#Roles").val();
	$("#hasSrIds").val(srId);
	var spId = $("#Permission").val();
	$("#hasSpIds").val(spId);
}