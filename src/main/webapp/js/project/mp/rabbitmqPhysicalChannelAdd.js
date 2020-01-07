$(function(){
	getPhysicalId();
	getRabbitmqPhysicalChannel();
	setSelectStyle($("#mqqueuename"));
	setMyActive(2,10); //设置激活页
});

/*获取物理通道*/
function getPhysicalId(){
	var dataGroupId = $("#providerPhysicalChannelId");
	var myDelUrl = basePath + 'physicalList';
	$.ajax({
		type:'get',
		async: false,
		url :myDelUrl,
		success:function (dt){
			$.each(dt, function(index,val) {  
				var option = $("<option>").text(val.name).val(val.id)
				dataGroupId.append(option);
			});
			setSelectStyle(dataGroupId);
		},
		dataType: 'json'
	});
}

function setMqQueueDisplayName(){
	var displayName = $("#mqqueuename").find("option:selected").text();
	$("#mqqueuedisplayname").val(displayName);
}

function getRabbitmqPhysicalChannel(){
	var myDelUrl = basePath + 'rabbitmqPhysicalChannelAll';
	$.ajax({
		type:'get',
		url :myDelUrl,
		success:function (dt){
			if (null != dt && "" != dt) {
				$.each(dt, function(index,val) {
					$("#providerPhysicalChannelId option[value='"+val.provider_physical_channel_id+"']").remove();
				});
				var dataGroupId = $("#providerPhysicalChannelId");
				setSelectStyle(dataGroupId);
			}
		},
		dataType: 'json'
	});
}
