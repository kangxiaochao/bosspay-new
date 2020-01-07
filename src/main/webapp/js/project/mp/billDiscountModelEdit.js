
$(function(){
	$('#name').focus();
	setMyActive(5,5); //设置激活页
});

function put(){
	var id=$('#id').val();
	var name=$('#name').val();
	var myDelUrl=basePath+'dataDiscountModel/'+id;
	$.ajax({
		type:'PUT',
		url :myDelUrl,
		data:{id:id,name:name},
		success:function (dt){
			location.href=basePath+dt;		
	    },
	    dataType: 'html'
	});
}