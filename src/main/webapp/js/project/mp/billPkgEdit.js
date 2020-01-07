$(function(){
	$('#name').focus();
	setMyActive(2,1); //设置激活页
});

function submitEdit(){
	var myFormActionUrl=$('#fm').attr("action");
	var id=$('#id').val();
	var name=$('#name').val();
	var value=$('#value').val();
	var provider_id=$('#provider_id').val();
	
	$.ajax({
		type:'PUT',
		url :myFormActionUrl,
		data:{ id: id,name:name,value:value,provider_id:provider_id },
		success:function (dt){
			location.href=basePath+dt;
	    },
	    dataType: 'html'
	});
		
}