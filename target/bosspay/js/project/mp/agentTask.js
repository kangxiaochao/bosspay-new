$(function(){
	var backMsg = $("#backMsg").val();
	if (backMsg.length > 0 && backMsg.length < 5) {
		outMessage('success', backMsg, '友情提示');
	} 
	if(backMsg.length > 6){
		outMessage('warning', backMsg, '友情提示');
	}
})