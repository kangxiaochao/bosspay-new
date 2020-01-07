$(function(){
	getAgentId();
	setMyActive(2,9); //设置激活页
});

function getAgentId() {
	var dataGroupId = $("#agentId");
	var myDelUrl = basePath + 'agentAll';
	// var option = $("<option>").text('无').val('');
	// dataGroupId.append(option);
	$.ajax({
		type : 'get',
		url : myDelUrl,
		success : function(dt) {
			$.each(dt, function(index, val) {
				var option = $("<option>").text(val.name).val(val.id)
				dataGroupId.append(option);
			});
			setSelectStyle(dataGroupId);
		},
		dataType : 'json'
	});
}