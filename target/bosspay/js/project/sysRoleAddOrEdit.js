


$(function(){
	getHasRole();
	getNoRole();
	setMyActive(1,3);
});

function getArr(){
	var selectArr = $("#hasRole").children("option");
	var datas = "";
	for(var i = 0 ; i < selectArr.length ; i++){
		datas+= $(selectArr[i]).attr("value")+",";
	}
	$("#hasRoleIds").val(datas);
}

function getHasRole(){
	//ajax 取已经分配的数据，取到以后将得到的数据填充到右边的select中
	var suId=$('#suId').val();//获取用户ID
	var myDelUrl=basePath+'sysGetHasRole/'+suId;
	$.ajax({
		type:'GET',
		url :myDelUrl,
		success:function (dt){
			var hasRole = $("#hasRole");
			$.each(dt, function(index,val) {
			      var option = $("<option>").text(val.srName).val(val.srId)
			      hasRole.append(option);
			    });
			
	    },
	    dataType: 'json'
	});
}

function getNoRole(){
	//ajax 取未分配的数据，取到以后将得到的数据填充到左边的select中
	var suId=$('#suId').val();
	var myDelUrl=basePath+'sysGetNoRole/'+suId;
	$.ajax({
		type:'GET',
		url :myDelUrl,
		success:function (dt){
			var noRole = $("#noRole");
			$.each(dt, function(index,val) {
			      var option = $("<option>").text(val.srName).val(val.srId)
			      noRole.append(option);
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
