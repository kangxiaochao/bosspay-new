var id=$('#groupId').val();

$(function(){
	var groupId=$('#groupId').val();
	$('#name').focus();
	getProviderList();
	initSelected(groupId);
	
	setMyActive(2,4); //设置激活页
});

function initSelected(groupId){
	var url=basePath+'querySelectGroupByGroupId/'+groupId;
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
					clickQueryPhysicalChennal(providerId);
				}
				
				var physicalId = vals.physicalId;
				if((providerId+physicalId)!=beforePhysical){
					$("#physical"+providerId+physicalId).attr('checked',true);
					clickQueryProvince(providerId,physicalId);
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
				var myDiscountDiv='<div class="col-sm-12 form-group" style="margin-bottom:-15px;"><h4><input type="checkbox" id="provider'+val.id+'" name="provider"'+ 
				'onclick="clickQueryPhysicalChennal(\''+val.id+'\')" value="'+val.id+'"/>'+
				'<span style="padding-left:10px;">'+val.name+'</span></h4>'+    		
				'<div class="col-sm-12 form-group" style="padding-left:40px;" id="physical'+val.id+'">';
				myDiscountDiv += '</div></div>';
		    	provider.append(myDiscountDiv);
		    });
	    },
	    dataType: 'json'
	});
}

//二级
function clickQueryPhysicalChennal(providerId){
	if($("#provider"+providerId).is(':checked')){
		var physicalChannel = $('#physical'+providerId);
		var myDelUrl = basePath + 'getPhysicalByDispatcher/'+providerId;
		$.ajax({
			type:'get',
			url :myDelUrl,
			async: false,
			data:{},
			success:function (dt){
				$.each(dt, function(index,val) {
					var myDiscountDiv='<div class="col-sm-12 form-group" style="margin-bottom:-15px;"><h4><input type="checkbox" id="physical'+providerId+val.physicalId+'" name="physical'+providerId+'"'+ 
					'onclick="clickQueryProvince(\''+providerId+'\',\''+val.physicalId+'\')" value="'+val.physicalId+'"/>'+
					'<span style="padding-left:10px;">'+val.name+'</span></h4>'+    		
					'<div class="col-sm-12 form-group" style="padding-left:40px;" id="province'+providerId+val.physicalId+'">';
					myDiscountDiv += '</div></div>';
					physicalChannel.append(myDiscountDiv);
				});
		    },
		    dataType: 'json'
		});
	}else{
		$("#provider"+providerId).attr('check',false);
		$('#physical'+providerId).empty();
	}
	
}

//三级
function clickQueryProvince(providerId,physicalId){
	if($("#physical"+providerId+physicalId).is(':checked')){
		var physicalChannel = $('#province'+providerId+physicalId);
		var myDiscountDiv='';
		myDiscountDiv+='<div class="col-sm-2 form-group ">';
		myDiscountDiv += '	<label ><input type="checkbox" name="province'+providerId+physicalId+'" class="provinceCity'+providerId+physicalId+'"'+
						 'id="province'+providerId+physicalId+'全国" onclick="checkAll(\''+providerId+'\',\''+physicalId+'\')" value="全国"> <i></i>全国</label>';
		myDiscountDiv+='</div>';
		physicalChannel.empty();
		physicalChannel.append(myDiscountDiv);
		var myDelUrl = basePath + 'getProvinceByDispatcher/'+providerId+'/'+physicalId;
		$.ajax({
			type:'get',
			url :myDelUrl,
			async: false,
			data:{},
			success:function (dt){
				$.each(dt, function(index,val) {
					myDiscountDiv='<div class="col-sm-2 form-group"><label ><input class="provinceCity'+providerId+physicalId+'" type="checkbox" id="province'+providerId+physicalId+val.provinceCode+'" name="province'+providerId+physicalId+'"'+ 
					' value="'+val.provinceCode+'"/>'+
				'<span style="padding-right:10px;">'+val.provinceCode+'</span></label>';
				myDiscountDiv += '</div></div>';
				physicalChannel.append(myDiscountDiv);
				});
		    },
		    dataType: 'json'
		});
	}else{
		$("#physical"+providerId+physicalId).attr('check',false);
		$("#province"+providerId+physicalId).empty();
	}
	
}

function checkAll(providerId,physicalId){
	if($("#province"+providerId+physicalId+"country").is(':checked')){
		$(".provinceCity"+providerId+physicalId).click();
	}else{
		$(".provinceCity"+providerId+physicalId).attr('check',false);
//		$("#province"+providerId).empty();
	}
}

function submitData(){
	var dataMap={};
	var groupId=$('#groupId').val();
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
	        text: "点击确认后，通道组原有设置将自动删除。",
	        type: "warning",
	        showCancelButton: true,
	        confirmButtonColor: "#DD6B55",
	        confirmButtonText: "提交",
	        cancelButtonText: "取消",
	        closeOnConfirm: true,
	        animation: "slide-from-top"
	    },function(){
	    	var data=JSON.stringify(dataMap);
			var url=basePath+'submitBillGroupRelBill';
			$.ajax({
				type:'post',
				url :url,
				async: false,
				data:{'dataMap':data,'groupId':groupId},
				dataType: 'json',
				success:function (dt){
					if(dt){
						var myBasePath=basePath+'providerBillGroupDetailPage/'+groupId;
						location.href=myBasePath;
					}else{
						outMessage('warning','提交失败！','友情提示');
					}
			    }
			});
	    });
	}
}

