$(function(){
	initChannel_type();
	initRadioStyle();
	$('#name').focus();
	getProviderList();
	setMyActive(6,3); //设置激活页
});

/*获取话费运营商列表*/
function getProviderList(){
	var id = $("#id").val();
	var myprovider_id = $("#myprovider_id").val();
	var providerId = $("#providerId");
	var myDelUrl = basePath + 'surplusProvider?providerPhysicalChannelId='+id;
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
			providerId.val(myprovider_id);
			setSelectStyle(providerId);
		},
		dataType: 'json'
	});
}

function submitEdit(){
	
	var myFormActionUrl=$('#fm').attr("action");
	var id=$('#id').val();
	var name=$('#name').val();
	
	if(name.replace(/ /g,'')==''){
		outMessage('warning','名称不能为空！','友情提示');
		return false;
	}
	
	var provider_id=$('#providerId').val();
	var link_url=$('#link_url').val();
	var callback_url=$('#callback_url').val();
	var queue_name=$('#queue_name').val();
	var provider_mark=$('#provider_mark').val();
	var channel_type= $('input[name="channel_type"]:checked ').val();
	var operator=$('#operator').val();
	var parameter_list=$('#parameter_list').val();
	var default_parameter=$('#default_parameter').val();
	var priority=$('#priority').val();
	
	if(priority.replace(/ /g,'')==''){
		outMessage('warning','优先级不能为空！','友情提示');
		return false;
	}

	$.ajax({
		type:'PUT',
		url :myFormActionUrl,
		data:{ id: id,name:name,provider_id:provider_id,link_url:link_url,callback_url:callback_url,
			queue_name:queue_name,provider_mark:provider_mark,channel_type:channel_type,operator:operator,
			parameter_list:parameter_list,default_parameter:default_parameter,priority:priority },
		success:function (dt){
			location.href=basePath+dt;
	    },
	    dataType: 'html'
	});
	
}

function initChannel_type(){
	$("#channel_type"+$('#mychannel_type').val()).attr("checked","checked");
}

function initRadioStyle(){  
    $('.i-checks').iCheck({
        checkboxClass: 'icheckbox_square-green',
        radioClass: 'iradio_square-green',
    });
}

function outMessage(type,message,title){
    toastr.options = {
	    closeButton: true,
	    progressBar: true,
	    showMethod: 'slideDown',
	    positionClass: "toast-bottom-right",
	    timeOut: 4000
    };
    if(type == 'success'){
    	toastr.success(message, title);
    }
    if(type == 'info'){
    	toastr.info(message, title);
    }
    if(type == 'warning'){
    	toastr.warning(message, title);
    }
    if(type == 'error'){
    	toastr.error(message, title);
    }
}