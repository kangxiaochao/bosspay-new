
$(function(){
	$('#section').focus();
	initRadioStyle();
	getProviderList();
	initProvinceCode();	//初始化省份信息
	initCityCodeStyle();//初始化地市下拉列表样式
	setMyActive(2,5); //设置激活页
});

/*获取代理商列表*/
function getProviderList(){
	var providerId = $("#providerId");
	var myDelUrl = basePath + 'providerList';
	
	$.ajax({
		type:'get',
		url :myDelUrl,
		success:function (dt){
			$.each(dt, function(index,val) {
				var option = $("<option>").text(val.name).val(val.id);
				providerId.append(option);
			});
			setSelectStyle(providerId);
		},
		dataType: 'json'
	});
}

//初始化省份信息
function initProvinceCode(){
	var selectId = "provinceCode";
	
	//初始化省份信息
	$('#'+selectId).empty();
	$('#'+selectId).append('<option value="">请选择</option>');
	$.each(provinceCityData,function (index,el){
		$('#'+selectId).append('<option value="'+el.name+'">'+el.name+'</option>');
	});
	//初始化省份信息下拉列表样式
	setSelectStyle($("#"+selectId));
}

//初始化地市下拉列表样式
function initCityCodeStyle(){
	setSelectStyle($("#cityCode"));
}

/*检测代理商名字是否重复*/
function checkPhone(phone){
	if (phone.length == 7) {
		$.ajax({
			type:'get',
			url:basePath+'phoneCheck/'+phone,
			success:function(dt){
				if(dt=='true'){
					$("#phoneMsg").html('号码已存在');
					$("#submit").attr('disabled',"true");
				}else{
					$("#phoneMsg").html('长度必须7位');
					$("#submit").removeAttr("disabled");
				}
			}
		});
	}
}
