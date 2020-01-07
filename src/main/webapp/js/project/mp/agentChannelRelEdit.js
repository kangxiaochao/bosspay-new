

$(function(){
	var id = $('#agentId').attr('data-id');
	initSelected(id);
	$('#providerId').focus();
//	initProviderList();
	setMyActive(5,1); //设置激活页
});

function initSelected(id){
	var url=basePath+'queryAgentChannelRelByAgentId/'+id;
	$.ajax({
		type:'get',
		url :url,
		async: false,
		data:{},
		dataType: 'json',
		success:function (dt){
			var beforeProvider = null;
			var beforePhysical = null;
			var beforeProcince = null;
			$.each(dt,function(index,vals){
				var providerId = vals.providerId;
				if(providerId!=beforeProvider){
					$("#provider"+providerId).attr('checked',true);
					clickProvider(providerId);
				}
				
				var physicalId = vals.physicalId;
				if((providerId+physicalId)!=beforePhysical){
					$("#physical"+providerId+physicalId).attr('checked',true);
					clickPhysical(providerId,physicalId);
				}
				
				var provinceCode = vals.provinceCode;
				if((providerId+physicalId+provinceCode)!=beforeProcince){
					$("#province"+providerId+physicalId+provinceCode).attr('checked',true);
				}
				beforeProvider = providerId
				beforePhysical=providerId+physicalId;
				beforeProcince = providerId+physicalId+provinceCode;
			})
	    }
	    
	});
//	clickProvider(val);
	
}


//点击运营商操作
function clickProvider(val){
	if($("#provider"+val).is(':checked')){
		$("#provider"+val).attr('check','true');
		var groupId = $('#groupId').attr('data-id');
		var myUrl=basePath+'getPhysicalIdByProviderId/'+groupId+'/'+val;
		$.ajax({
			type:'get',
			url :myUrl,
			async: false,
			data:{},
			success:function (dt){
				$.each(dt,function(index,vals){
					var myDiscountDiv='<h4><input type="checkbox" id="physical'+val+vals.physicalId+'" name="physical'+val+'"'+ 
    					'onclick="clickPhysical(\''+val+'\',\''+vals.physicalId+'\')" value="'+vals.physicalId+'"/>'+
        			'<span style="padding-left:10px;">'+vals.name+'</span></h4>'+    		
        			'<div style="padding-left:20px;" id="province'+val+vals.physicalId+'">';
					myDiscountDiv += '</div>';
					
					$('#physical'+val).append(myDiscountDiv);
				});
		    },
		    dataType: 'json'
		});
	}else{
		$("#provider"+val).attr('check',false);
		$('#physical'+val).empty();
	}
}
//点击通道操作
function clickPhysical(el,val){
	if($("#physical"+el+val).is(':checked')){
		$("#physical"+el+val).attr('check','true');
		var groupId = $('#groupId').attr('data-id');
		var myUrl=basePath+'getProvinceCodeByPhysicalId/'+groupId+'/'+el+'/'+val;
		
		$.ajax({
			type:'get',
			url :myUrl,
			async: false,
			data:{},
			success:function (dt){
				$.each(dt,function(index,vals){
					var myDiscountDiv='<input type="checkbox" id="province'+el+val+vals.provinceCode+'" name="province'+el+val+'"'+ 
    					' value="'+vals.provinceCode+'"/>'+
        			'<span style="padding-right:10px;">'+vals.provinceCode+'</span>';
					myDiscountDiv += '</div>';
					
					$('#province'+el+val).append(myDiscountDiv);
				});
		    },
		    dataType: 'json'
		});
	}else{
		$("#physical"+el+val).attr('check',false);
		$('#province'+el+val).empty();
	}
}


function submitData(){
	var dataMap={};
	var id = $('#agentId').attr('data-id');
	var providerArr = $('input[name="provider"]');
	var s=0;
	var p=0;
	providerArr.each(function(index,el){
		var providerId=el.value;
		if($('#provider'+providerId).is(':checked')){
			//var providerData = {};
			var physicalMap={};
			var i=0;
			$('input[name="physical'+providerId+'"]').each(function(pgIdx,pg){
				var physicalId = pg.value;
				if($('#physical'+providerId+physicalId).is(':checked')){
					i++
					var provinceList = [];
					var j=0;
					$('input[name="province'+providerId+physicalId+'"]').each(function(pcIdx,pc){
						var provinceCode = pc.value;
						if($('#province'+providerId+physicalId+provinceCode).is(':checked')){
							provinceList.push(provinceCode);
							j++;
						}
					});
					if(j==0){
						s++;
					}
					physicalMap[physicalId]=provinceList;
				}
			})
			if(i==0){
				p++;
			}
			dataMap[providerId]=physicalMap;
		}
	});
	if(p>0||s>0){
		outMessage('warning','存在二级或三级未选择的记录，不允许提交！','友情提示');
	}else{
		swal({
	        title: "是否确认提交",
	        text: "点击确认后，代理商原有特惠通道将自动删除。",
	        type: "warning",
	        showCancelButton: true,
	        confirmButtonColor: "#DD6B55",
	        confirmButtonText: "提交",
	        cancelButtonText: "取消",
	        closeOnConfirm: true,
	        animation: "slide-from-top"
	    },function(){
	    	var data=JSON.stringify(dataMap);
			var url=basePath+'submitAgentChanneRelBill';
			$.ajax({
				type:'post',
				url :url,
				async: false,
				data:{
					'agentId' : id,
					'billMap':data
				},
				dataType: 'json',
				success:function (dt){
					if(dt){
						var myBasePath = basePath + 'agentChannelRelListPage/' + id;
						location.href = myBasePath;
					}else{
						outMessage('warning','提交失败！','友情提示');
					}
			    }
			});
	    });
	}
}

