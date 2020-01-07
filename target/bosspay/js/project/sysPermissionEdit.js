

$(function(){
	$('#spName').focus();
	setMyActive(1,2);
});

function submitEdit(){
	var myFormActionUrl=$('#fm').attr("action");
	var spId=$('#spId').val();
	var spName=$('#spName').val();
	var spNick=$('#spNick').val();
	
	$.ajax({
		type:'PUT',
		url :myFormActionUrl,
		data:{ spId: spId,spName:spName,spNick:spNick },
		success:function (dt){
			location.href=basePath+dt;
	    },
	    dataType: 'html'
	});
		
}

