

$(function(){
	$('#name').focus();
	getProviderList();
	initProvince();
	setMyActive(5,5); //设置激活页
});

/*获取运营商列表*/
function getProviderList(){
	
	var provider = $("#provider");
	var option = $("<option>").text('无').val('');
	provider.append(option);	
	var myDelUrl = basePath + 'providerList';
	$.ajax({
		type:'get',
		url :myDelUrl,
		data:{ providerType: '1' },
		success:function (dt){
			$.each(dt, function(index,val) {
		    	option = $("<option id='proId"+index+"'>").text(val.name).val(val.id)
		    	provider.append(option);
		    });
	    },
	    dataType: 'json'
	});
}

function initProvince(){
	var dataDiv='provinceDataDiscount';
	var pcount=1;
	var myDiscountDiv='';
	myDiscountDiv+='<div class="col-sm-3 form-group " id="nation" >';
	myDiscountDiv+='	<label class="col-sm-6 control-label">0.全国</label>';
	myDiscountDiv+='    <div class="col-sm-6">';
	myDiscountDiv+='    	<input class="form-control" id="全国" name="全国" proName="provinceCityes">';
	myDiscountDiv+='	</div>';
	myDiscountDiv+='</div>';
	$('#'+dataDiv).append(myDiscountDiv);
	$.each(provinceCityData,function (index,el){
		myDiscountDiv='';
		myDiscountDiv+='<div class="col-sm-3 form-group ">';
		myDiscountDiv+='	<label class="col-sm-6 control-label">'+pcount+'.'+el.name+'</label>';
		myDiscountDiv+='    <div class="col-sm-6">';
		myDiscountDiv+='    	<input class="form-control" id="'+el.name+'" name="'+el.name+'" proName="provinceCityes">';
		myDiscountDiv+='	</div>';
		myDiscountDiv+='</div>';
		$('#'+dataDiv).append(myDiscountDiv);
		pcount++;
	});
}

function getType(type){
	$('#provinceDiscount').hide();
	if(type=='1'){
		$('#nation').empty();
		$('#nation').hide();
		$('#provinceDiscount').show();
	}else{
		$('#nation').show();
		$('#provinceDiscount').show();
	}
}



