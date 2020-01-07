$(function(){
	$('#name').focus();
	getProviderList();
	initProvince();
	getMessage();
	setMyActive(5,5); //设置激活页
});

/*获取运营商列表*/
function getProviderList(){
	var provider = $("#providerCheck");
	var myDelUrl = basePath + 'providerList';
	$.ajax({
		type:'get',
		url :myDelUrl,
		data:{ providerType: '1' },
		success:function (dt){
			$.each(dt, function(index,val) {
		    	pro = '<input name="provider" type="radio" value='+val.id+' />'+val.name+'';
		    	provider.append(pro);
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
		$('#nation').hide();
		$('#provinceDiscount').show();
	}else{
		$('#nation').show();
		$('#provinceDiscount').show();
	}
}


function getMessage(){
	var modelId=$('#modelId').val();
	$.ajax({
		type:'get',
		url: basePath+'dataDiscountModelDetailEditPageDetail',
		data:{
			modelId:modelId
		},
		dataType:'json',
		success:function(data){
				$("#name").val(data.name);
				var providerId = data.providerId;
				$("input[name=provider][value="+providerId+"]").attr("checked",true);
				var dataType=data.dataType;
				getType(dataType);
				$("input[name=dataType][value="+dataType+"]").attr("checked",true);
				var dataDiscountList = data.discountMeg;
				for(var i = 0;i<dataDiscountList.length;i++){
					var dataMeg= dataDiscountList[i];
					var provindeCode= dataMeg.province_code;
					var discount = dataMeg.discount;
					$("#"+provindeCode+"").val(discount);
			}
		}
	});
};

//上传折扣模板
function uploadFile(){
	var file=$('#DataDiscountFile').val();
	if(file!=""){
		// 获取运营商信息
		$('#uploadResult').html('上传中...');
		outMessage('info','上传中...请稍等片刻','处理状态');
		var myUrl=basePath+'DataDiscountModelEx1';
		var fm=$('#uploadForm1');
		var options={
				url:myUrl,
				type:'post',
				data:{
				},
				dataType:'json',
				success:function(dt){
					if(dt!=null&&dt!=""){
						var data=dt.data;
						for(var key in data)
         				{
							$("#"+key+"").val(data[key]);
         				}
						$('#uploadResult').html('上传完成 !');
						outMessage('info','上传完成','处理成功');
						
					}else{
						$('#uploadResult').html('上传失败 - - !');
						outMessage('info','上传失败','');
					}
				}
		}
		fm.ajaxSubmit(options);
	}else{
		outMessage('error', '请先选择上传的文件！', '友情提示');
	}
	
		
}

