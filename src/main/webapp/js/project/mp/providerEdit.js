$(function(){
	$('#name').focus();
	setMyActive(6,1); //设置激活页
});

function submitEdit(){
	var myFormActionUrl=$('#fm').attr("action");
	var id=$('#id').val();
	var name=$('#name').val();
	var short_name=$('#short_name').val();
	
	$.ajax({
		type:'PUT',
		url :myFormActionUrl,
		data:{ id: id,name:name,short_name:short_name },
		success:function (dt){
			location.href=basePath+dt;
	    },
	    dataType: 'html'
	});
		
}