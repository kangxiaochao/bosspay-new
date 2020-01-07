

$(function(){
	$('#srName').focus();
	setMyActive(1,3);
});

function submitEdit(){
	var myFormActionUrl=$('#fm').attr("action");
	var srId=$('#srId').val();
	var srName=$('#srName').val();
	
	$.ajax({
		type:'PUT',
		url :myFormActionUrl,
		data:{ srId: srId,srName:srName },
		success:function (dt){
			location.href=basePath+dt;
	    },
	    dataType: 'html'
	});
		
}