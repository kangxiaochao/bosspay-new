
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

function submits() {
	layer.load();
	$("#submit").attr("disabled", true);
	var form = new FormData(document.getElementById("upload"));
	$.ajax({
		type : "post",
		url  : "batchAddPage",
		data : form,
		processData : false,
		contentType : false,
		dataType : 'json',
		success : function(data) {
			layer.closeAll('loading');
			if(data == '-1') {
				layer.msg('文件格式错误，请联系客服人员进行核对', {
					 time: 20000, //20s后自动关闭
					 btn: ['明白了', '知道了', '哦']
				 });
			}else {
				layer.msg(data + '个号段添加成功', {
					 time: 20000, //20s后自动关闭
					 btn: ['明白了', '知道了', '哦']
				});
			}
		}
	});
}
