$(function(){
	setStatus();
	setMyActive(5,1); //设置激活页
});

/*设定启用状态*/
function setStatus(){
	var oldStatus = $("#oldStatus").val();
	if(oldStatus == 0){
		$('[type="checkbox"]').bootstrapSwitch('state', false);
	}else{
		$('[type="checkbox"]').bootstrapSwitch();
	}
}
