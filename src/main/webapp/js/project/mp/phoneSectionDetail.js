$(function(){
	carrierTypeFormatter();
	setMyActive(2,5); //设置激活页
});

//格式化号段类型显示信息
function carrierTypeFormatter(){
	var carrierDetail;
	var carrierType = $("#carrierType").val();
//	if("1" == carrierType){
//		carrierDetail = "中国移动";
//	}else if("2" == carrierType){
//		carrierDetail = "中国联通";
//	}else if("3" == carrierType){
//		carrierDetail = "中国电信";
//	}else if("4" == carrierType){
//		carrierDetail = "虚拟运营商移动";
//	}else if("5" == carrierType){
//		carrierDetail = "虚拟运营商联通";
//	}else if("6" == carrierType){
//		carrierDetail = "虚拟运营商电信";
//	}else if("7" == carrierType){
//		carrierDetail = "物联网卡";
//	}else{
//		carrierDetail = "普卡";
//	}
	if("1" == carrierType){
		carrierDetail = "普卡";
	}else if("7" == carrierType){
		carrierDetail = "物联网卡";
	}
	$("#carrierDetail").val(carrierDetail);
}
