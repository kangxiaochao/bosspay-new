$(function(){
	initProviderList();
	initPhysicalChannelList();
	initSelectProvince('provinceCode',$('#provinceCode').attr('data-id'));
	$('#providerId').focus();
	setMyActive(2,6); //设置激活页
});

function submitEdit(){
	var myFormActionUrl=$('#fm').attr("action");
	var id=$('#fm').attr('data-id');
	var providerId=$('#providerId').val();
	var physicalChannel=$('#physicalChannel').val();
	var provinceCode = $('#provinceCode').val();
	$.ajax({
		type:'PUT',
		url :myFormActionUrl,
		data:{ id:id,providerId:providerId,physicalChannel:physicalChannel,provinceCode:provinceCode },
		success:function (dt){
			location.href=basePath+dt;
	    },
	    dataType: 'html'
	});
		
}

function initProviderList(){
	var myUrl=basePath+'providerList';
	var providerId=$('#providerId').attr('data-id');
	$.ajax({
		type:'get',
		url :myUrl,
		data:{  },
		success:function (dt){
			$('#providerId').append('<option value="">请选择</option>');
			$.each(dt,function(index,val){
				$('#providerId').append('<option value="'+val.id+'" '+(val.id==providerId?' selected ':' ')+'>'+val.name+'</option>');
			});
			setSelectStyle($('#providerId'));
			setSelectStyle($('#provinceCode'));
	    },
	    dataType: 'json'
	});
}

function initPhysicalChannelList(){
	var myUrl=basePath+'providerPhysicalChannelAll';
	var physicalChannel=$('#physicalChannel').attr('data-id');
	$.ajax({
		type:'get',
		url :myUrl,
		data:{  },
		success:function (dt){
			$('#physicalChannel').append('<option value="">请选择</option>');
			$.each(dt,function(index,val){
				$('#physicalChannel').append('<option value="'+val.id+'" '+(val.id==physicalChannel?' selected ':' ')+'>'+val.name+'</option>');
			});
			setSelectStyle($('#physicalChannel'));
	    },
	    dataType: 'json'
	});
}