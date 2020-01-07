$(function(){
	$('#name').focus();
	setMyActive(2,4); //设置激活页
});

function submitEdit(){
	var myFormActionUrl=$('#fm').attr("action");
	var id=$('#id').val();
	var name=$('#name').val();
	
	$.ajax({
		type:'PUT',
		url :myFormActionUrl,
		data:{ id: id,name:name },
		success:function (dt){
			location.href=basePath+dt;
	    },
	    dataType: 'html'
	});
		
}