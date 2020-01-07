$(function(){
	$('#suName').focus();
	setMyActive(1,1);
});

function check(){
	var suPass = $("#suPass").val();
	var suPass1 = $("#suPass1").val();
	if(suPass==suPass1){
		return true;
	}else{
		alert("两次输入的密码不一致，请重新输入");
		return false;
	}
}