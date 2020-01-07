$(function(){
	setMyActive(3,5); //设置激活页
});

function submit1(){
	var myFormActionUrl=$('#fm').attr("action");
	var orderIds=$('#orderIds').val();
	$.ajax({
		url :myFormActionUrl,
		type:'post',
		data:{
			orderIds:orderIds
		},
		success:function (dt){
			layer.alert(dt);
	    },
	});
}