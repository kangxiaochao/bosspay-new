$(function(){
	$('#suName').focus();
	verifyCode = new GVerify("authCode");
	$.ajax({
		type : 'get',
		url : 'status/sysNoticeLists?order=1',
		async:false,
		success : function(dt) {
			var li ="";
			$.each(dt, function(index,val) {  
				li += "<li>"+val.content+"</li>"
			});
			$("#FontScroll ul").html(li);
		},
		dataType : 'json'
	});
});

function auths() {
	var oValue = $("#verification").val();
	if(oValue ==""){ 
		$("#backMsg").html("验证码不能为空");
    }
	var res = verifyCode.validate(oValue);
	if(res){
		$("#backMsg").html("");
	}else{
		$("#backMsg").html("验证码输入有误");
		$("#verification").val("");
	}
};

function toLogin(){
	if($('#suName').val()==''){
		$('#suName').focus();
		return false;
	}
	if($('#suPass').val()==''){
		$('#suPass').focus();
		return false;
	}
	if ($('#verification').val() == '') {
		$('#verification').focus();
		return false;
	}
//	if($('#captcha').val()==''){
//		$('#captcha').focus();
//		return false;
//	}
	$('#loginForm').submit();
}

$('#loginWin').bind('keyup', function(event) {/* 增加回车提交功能 */
    if (event.keyCode == '13') {
    	auths();
    	toLogin();
    }
}); 

function change(){
	$('#captchaImage').attr("src", "captcha?timestamp=" + (new Date()).valueOf());
}