

$(function(){
	$('#suName').focus();
	setMyActive(1,1);
});

function submitEdit(){
	var myFormActionUrl=$('#fm').attr("action");
	var suId=$('#suId').val();
	var suName=$('#suName').val();
	var suMobile=$('#suMobile').val();
	
	$.ajax({
		type:'PUT',
		url :myFormActionUrl,
		data:{ suId: suId,suName:suName,suMobile:suMobile },
		success:function (dt){
			location.href=basePath+dt;
	    },
	    dataType: 'html'
	});
		
}

