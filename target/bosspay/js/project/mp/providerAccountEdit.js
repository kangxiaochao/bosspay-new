$(function(){
	$('#balance').focus();
	 setMyActive(6,1); //设置激活页
});

function submitEdit(){
	var myFormActionUrl=$('#fm').attr("action");
	var providerId=$('#providerId').val();
	var fee=$('#fee').val();
	var remarks = $("#remarks").val();
	$.ajax({
		type:'PUT',
		url :myFormActionUrl,
		data:{ providerId: providerId,fee:fee ,remarks:remarks },
		success:function (dt){
			location.href=basePath+dt;
	    },
	    dataType: 'html'
	});
		
}