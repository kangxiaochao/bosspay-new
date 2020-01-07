var physicalId=$('#physicalId').val();

$(function(){
	$('#name').focus();
	getProviderList();
	initSelected(physicalId);
	setMyActive(6,3); //设置激活页
});

function initSelected(physicalId){
	var url=basePath+'querySelectDispatcherByPhysicalId/'+physicalId;
	$.ajax({
		type:'get',
		url :url,
		async: false,
		data:{},
		dataType: 'json',
		success:function (dt){
			var beforeProvider = null;
			var beforeProcince = null;
			$.each(dt,function(index,vals){
				var providerId = vals.providerId;
				if(providerId!=beforeProvider){
					if(!$("#provider"+providerId).is(':checked')){
						$("#provider"+providerId).attr('checked',true);
						clickQueryProvince(providerId);
					}
				}
				var provinceCode = vals.provinceCode;
				if((providerId+provinceCode)!=beforeProcince){
					$("#province"+providerId+provinceCode).attr('checked',true);
				}
				beforeProvider = providerId
				beforeProcince = providerId+provinceCode;
			})
	    }
	    
	});
//	clickProvider(val);
	
}

function getProviderList(){
	var provider = $("#providerCheck");
	var myDelUrl = basePath + 'providerList';
	$.ajax({
		type:'get',
		url :myDelUrl,
		async: false,
		data:{},
		success:function (dt){
			$.each(dt, function(index,val) {
				var myDiscountDiv='<div class="col-sm-12 form-group " style="margin-bottom:-10px;">'
				myDiscountDiv+='<h4><input type="checkbox" id="provider'+val.id+'" name="provider"'+ 
				'onclick="clickQueryProvince(\''+val.id+'\')" value="'+val.id+'"/>'+
				'<span style="padding-left:10px;">'+val.name+'</span></h4>'+    		
				'<div style="padding-left:20px;" id="province'+val.id+'">';
				myDiscountDiv += '</div>';
				myDiscountDiv += '</div>';
		    	provider.append(myDiscountDiv);
		    });
	    },
	    dataType: 'json'
	});
}

function clickQueryProvince(providerId){
	if($("#provider"+providerId).is(':checked')){
//		var physicalChannel = $('#provider'+providerId);
		
		var dataDiv='province'+providerId;
		var myDiscountDiv='';
		myDiscountDiv+='<div class="col-sm-3 form-group ">';
		myDiscountDiv += '	<label > 01 <input type="checkbox" '+
						 'id="province'+providerId+'country" onclick="checkAll(\''+providerId+'\')" value="全国"> <i></i>全国</label>';
		myDiscountDiv+='</div>';
		
		$('#'+dataDiv).empty();
		$('#'+dataDiv).append(myDiscountDiv);
		$.each(provinceCityData,function (index,el){
			myDiscountDiv='';
			myDiscountDiv+='<div class="col-sm-3 form-group ">';
			if(index < 8){
				myDiscountDiv +='	<label > 0'+(index+2)+' <input type="checkbox" class="provinceCity'+providerId+'" value="'+el.name+'"'+
								'id="province'+providerId+el.name+'" name="province'+providerId+'"> <i></i>'+el.name+'</label>';
			}else{
				myDiscountDiv +='	<label > '+(index+2)+' <input type="checkbox" class="provinceCity'+providerId+'" value="'+el.name+'" '+
			 					'id="province'+providerId+el.name+'" name="province'+providerId+'"> <i></i>'+el.name+'</label>';
			}
			myDiscountDiv+='</div>';
			$('#'+dataDiv).append(myDiscountDiv);
		});
//		initCheckBoxStyle();
//		setProvindeCode();
	}else{
		$("#provider"+providerId).attr('check',false);
		$("#province"+providerId).empty();
	}
	
}

function checkAll(providerId){
	if($("#province"+providerId+"country").is(':checked')){
		$(".provinceCity"+providerId).click();
	}else{
		$(".provinceCity"+providerId).attr('check',false);
//		$("#province"+providerId).empty();
	}
}

function submitData(){
	var physicalId=$('#physicalId').val();
	var providerArr = $('input[name="provider"]');
	var providerMap={};
	var s=0;
	$('input[name="provider"]').each(function(idx,val){
		var providerId = val.value;
		if($('#provider'+providerId).is(':checked')){
			var provinceList = [];
			var j=0;
			$('input[name="province'+providerId+'"]').each(function(pcIdx,pc){
				var provinceCode = pc.value;
				if($('#province'+providerId+provinceCode).is(':checked')){
					provinceList.push(provinceCode);
					j++;
				}
			});
			if(j==0){
//				s++;
				provinceList.push("全国");
			}
			providerMap[providerId]=provinceList;
		}
	})
	if(s>0){
		outMessage('warning','存在二级或三级未选择的记录，不允许提交！','友情提示');
	}else{
		swal({
	        title: "是否确认提交",
	        text: "点击确认后，原有设置将自动删除。",
	        type: "warning",
	        showCancelButton: true,
	        confirmButtonColor: "#DD6B55",
	        confirmButtonText: "提交",
	        cancelButtonText: "取消",
	        closeOnConfirm: true,
	        animation: "slide-from-top"
	    },function(){
	    	var data=JSON.stringify(providerMap);
			var url=basePath+'submitPoviderDispatcherBill/'+physicalId;
			$.ajax({
				type:'get',
				url :url,
				async: false,
				data:{'dataMap':data},
				dataType: 'json',
				success:function (dt){
					if(dt){
						var myBasePath=basePath+'providerBillDispatcherList/'+physicalId;
						location.href=myBasePath;
					}else{
						outMessage('warning','提交失败！','友情提示');
					}
			    }
			});
	    });
	}
}


