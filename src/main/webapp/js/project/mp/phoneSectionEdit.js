
$(function(){
	$('#name').focus();
	getProviderList();
	initProvinceCode();	//初始化省份信息并设置默认省份
	setCityCode();	// 初始化城市信息并设置默认城市
//	setCarrierType();
	
	initCarrierType();
	initRadioStyle();
	setMyActive(2,5); //设置激活页
});

/*获取代理商列表*/
function getProviderList(){
	var oldProviderId = $("#oldProviderId").val();
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
			providerId.val(oldProviderId);
			setSelectStyle(providerId);
//			setSelectStyle($("#carrierType"));
		},
		dataType: 'json'
	});
}

function submitEdit(){
	var id = $('#id').val();
	var myFormActionUrl = basePath + 'phoneSection/' + id;
	var data = $("form").serialize();
	$.ajax({
		type:'PUT',
		url :myFormActionUrl,
		data:data,
		success:function (dt){
			location.href=basePath+dt;
	    },
	    dataType: 'html'
	});
}

//设置默认号段类型
//function setCarrierType(){
//	var oldCarrierType = $("#oldCarrierType").val();
//	$("#carrierType").val(oldCarrierType);
//}

function initCarrierType(){
	$("#carrierType"+$('#myCarrierType').val()).attr("checked","checked");
}

//初始化省份信息并设置默认省份
function initProvinceCode(){
	var selectId = "provinceCode";
	var oldProviderCode = $("#oldProviderCode").val();
	
	//初始化省份信息
	$('#'+selectId).empty();
	$('#'+selectId).append('<option value="">请选择</option>');
	$.each(provinceCityData,function (index,el){
		$('#'+selectId).append('<option value="'+el.name+'">'+el.name+'</option>');
	});
	$("#"+selectId).val(oldProviderCode);
	//初始化省份信息下拉列表样式
	setSelectStyle($("#"+selectId));
}

//初始化地市下拉列表样式
function initCityCodeStyle(){
	setSelectStyle($("#cityCode"));
}

// 初始化城市信息并设置默认城市
function setCityCode(){
	var oldProviderCode = $("#oldProviderCode").val();
	var oldCityCode = $("#oldCityCode").val();
	initCity(oldProviderCode,'cityCode');
	$("#cityCode").val(oldCityCode);
	initCityCodeStyle();
}