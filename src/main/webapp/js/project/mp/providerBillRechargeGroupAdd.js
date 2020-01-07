

$(function(){
	initProviderList();
	initPhysicalChannelList();
	initCountryProvince('provinceCode')
	$('#providerId').focus();
	setMyActive(2,6); //设置激活页
});

function initProviderList(){
	var myUrl=basePath+'providerList';
	$.ajax({
		type:'get',
		url :myUrl,
		data:{  },
		success:function (dt){
			$('#providerId').append('<option value="">请选择</option>');
			$.each(dt,function(index,val){
				$('#providerId').append('<option value="'+val.id+'">'+val.name+'</option>');
			});
			setSelectStyle($('#providerId'));
	    },
	    dataType: 'json'
	});
}

function initPhysicalChannelList(){
	var myUrl=basePath+'providerPhysicalChannelAll';
	$.ajax({
		type:'get',
		url :myUrl,
		data:{  },
		success:function (dt){
			$('#physicalChannel').append('<option value="">请选择</option>');
			$.each(dt,function(index,val){
				$('#physicalChannel').append('<option value="'+val.id+'">'+val.name+'</option>');
			});
			setSelectStyle($('#physicalChannel'));
			setSelectStyle($('#provinceCode'));
			
	    },
	    dataType: 'json'
	});
}

