$(function() {
	$('#name').focus();
	setMyActive(5,1); //设置激活页
});

function submitEdit() {
	var myFormActionUrl=$('#fm').attr("action");
	var id=$('#agentDataDiscountId').val();
	var agentId=$('#agentId').val();
	var discount=$('#discount').val();
	$.ajax({
		type:'PUT',
		url :myFormActionUrl,
		data:{ id: id,agentId:agentId,discount:discount},
		success:function (dt){
			location.href=basePath+dt;
	    },
	    dataType: 'html'
	});
}
