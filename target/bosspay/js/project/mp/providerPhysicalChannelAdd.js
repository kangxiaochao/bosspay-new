

$(function(){
	initRadioStyle();
	$('#name').focus();
	getProviderList();
	setMyActive(6,3); //设置激活页
});


/*获取话费运营商列表*/
function getProviderList(){
	var providerId = $("#providerId");
	var myDelUrl = basePath + 'surplusProvider';
	var option = $("<option>").text('无').val('');
	providerId.append(option);
	$.ajax({
		type:'get',
		url :myDelUrl,
		success:function (dt){
			$.each(dt, function(index,val) {
				option = $("<option>").text(val.name).val(val.id)
				providerId.append(option);
			});
			setSelectStyle(providerId);
		},
		dataType: 'json'
	});
}