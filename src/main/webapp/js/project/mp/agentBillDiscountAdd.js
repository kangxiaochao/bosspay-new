$(function(){
	$('#name').focus();
	getProviderList();
	setMyActive(5,1); //设置激活页
});

/*获取运营商列表*/
function getProviderList(){
	var provider = $("#providerId");
	var option = $("<option>").text('请选择').val('');
	provider.append(option);	
	var myDelUrl = basePath + 'providerList';
	var providerType='1';
	$.ajax({
		type:'get',
		url :myDelUrl,
		data:{
			providerType:providerType
		},
		success:function (dt){
			$.each(dt, function(index,val) {
		    	option = $("<option id='proId"+index+"'>").text(val.name).val(val.id)
		    	provider.append(option);
		    });
			setSelectStyle(provider);
			
			var dataType = $("#providerId");
			setSelectStyle(dataType);
			setSelectStyle($("#dataType"));
	    },
	    dataType: 'json'
	});
}

/*查询该代理商可用通道*/
function checkProvince(){
	$('#provinceDataDiscount').empty();
	 var providerId = document.getElementById("providerId").value;
	$('#province').empty();
	var agentId = $('#agentId').val();
	 var dataType = document.getElementById("dataType").value;
	$.ajax({
		type:'GET',
		url:basePath+'agentDataGroupRelGet',
		data:{
			agentId:agentId,
			providerId:providerId,
			dataType:dataType
		},
		dataType:'json',
		success:function(data){
			var myDiscountDiv='';
			var pcount=1;
			$.each(data,function (index,el){
				myDiscountDiv='';
				myDiscountDiv+='<div class="col-sm-3 form-group">';
				myDiscountDiv+='	<label class="col-sm-6 control-label" style="margin-right: -20px;">'+pcount+'.'+el+'</label>';
				myDiscountDiv+='    <div class="col-sm-6">';
				myDiscountDiv+='    	<input class="form-control"  name="'+el+'" id="'+el+'">';
				myDiscountDiv+='	</div>';
				myDiscountDiv+='</div>';
				$('#provinceDataDiscount').append(myDiscountDiv);
				pcount++;
			});
		}
	});
}

function checkDis(){
	 var providerId = document.getElementById("providerId").value;
	 var dataType = document.getElementById("dataType").value;
	 if(providerId==''||providerId==null){
			swal({
		        title: "请选择运营商",
		        type: "success",
		        confirmButtonText: "ok"
			});
		 return false;
		}
	if(dataType==''||dataType==null){
		swal({
	        title: "请选择话费类型",
	        type: "success",
	        confirmButtonText: "ok"
		});
	 return false;
	}
}

//上传折扣模板
function uploadFile(){
	var file=$('#DataDiscountFile').val();
	if(file!=""){
		// 获取运营商信息
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
						outMessage('info','上传完成','处理成功');
						
					}else{
						outMessage('info','上传失败','');
					}
				}
		}
		fm.ajaxSubmit(options);
	}else{
		outMessage('error', '请先选择上传的文件！', '友情提示');
	}
}

//上传折扣模板下载
function downLoadTemp(){
	$('#downloadFrame').attr('src',basePath + "downloadFiles/代理商话费折扣模板.xlsx");
}