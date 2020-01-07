$(function(){
	$('#spuid').focus();
	setMyActive(2,6); //设置激活页
});

function submitEdit(){
	var spuid=$('#spuid').val();
	var province=$('#province').val();
	var pkgs=$('#pkgs').val();
	var price=$('#price').val();
	var carr=$('#carr').val();
	var flowtype=$('#flowtype').val();
	
	var url = $('#fm').attr('action');
	$.ajax({
		type:'PUT',
		url :url,
		data:{"spuid":spuid,"province":province,"pkgs":pkgs,"price":price,"carr":carr,"flowtype":flowtype},
		success:function (dt){
			location.href=basePath+dt;
	    },
	    dataType: 'html'
	});
		
}