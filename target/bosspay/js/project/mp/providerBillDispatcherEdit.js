
var allPhysicalChannelFlag=true;
var allCountryFlag=true;
var allProvinceFlag=true;

$(function(){
	initProviderList();
	initProviderDataDispatcher();
	setMyActive(6,1); //设置激活页
});

//[{provider_id:'1',providerPhysical_channel_id:'2',province_code:'全国',weight:''}]
function submitEdit(){
	var provider_id=$('#provider_id').val();
	var dataDispatcherList=new Array();
	$('input[name="physicalChannel"]').each(function(index,el){		
		if(el.checked){
			var physicalChannelId=el.value;
			//选中全国权重
			if($('#channeltype1'+physicalChannelId).is(':checked')){
				var countryWeight=$('#countryWeight'+physicalChannelId).val();
				if(countryWeight.replace(/ /g,' ').length>0){
				dataDispatcherList.push(JSON.stringify({provider_id:provider_id,provider_physical_channel_id:physicalChannelId,province_code:'全国',weight:countryWeight}));
				}
				
			}
			//选中省份权重
			if($('#channeltype2'+physicalChannelId).is(':checked')){
				//-------------------------
				$('input[name="provinceCityes'+physicalChannelId+'"]').each(function(index,el){
					if(el.value.replace(/ /g,' ').length>0){
					dataDispatcherList.push(JSON.stringify({provider_id:provider_id,provider_physical_channel_id:physicalChannelId,province_code:el.id,weight:el.value}));
					}
				});
				//-------------------------
			}
		}
	}); //end each
	

	var myData=dataDispatcherList.join(',');
	myData='['+myData+']';
	var myFormActionUrl=$('#fm').attr("action");
	$.ajax({
		type:'PUT',
		url :myFormActionUrl,
		data:{ dataDispatcherList: myData},
		success:function (dt){
			location.href=basePath+dt;
	    },
	    dataType: 'html'
	});
}


//初始化运营商物理通道
function initProviderList(){
	var myUrl=basePath+'providerPhysicalChannelAll';
	$.ajax({
		type:'get',
		url :myUrl,
		async: false,
		data:{  },
		success:function (dt){
			$('#providerPhysicalChannelDiv').empty();
			$.each(dt,function(index,val){
				var myDiscountDiv='';
				myDiscountDiv += ''+(index.length>=2?'':'0')+(index+1)+'. <input class="i-checks" type="checkbox" '+
											'id="physicalchannela'+(index+1)+'" name="physicalChannel" value="'+val.id+'" checked onclick="physicalChannelProc(this.checked,\''+(val.id)+'\')" > <i></i> '+val.name;
				myDiscountDiv += '<div id="physicalChannel'+(val.id)+'">';
				myDiscountDiv += '通道类型';
				myDiscountDiv += '<input type="checkbox" id="channeltype1'+(val.id)+'" name="channeltype'+(val.id)+'" value="1" onclick="channelTypeProc(\''+(val.id)+'\')" checked  >全国';
				myDiscountDiv += '<input type="checkbox" id="channeltype2'+(val.id)+'" name="channeltype'+(val.id)+'" value="2" onclick="channelTypeProc(\''+(val.id)+'\')" checked >省份 <br>';
				myDiscountDiv += '<div id="ctd'+(val.id)+'">';
				myDiscountDiv += '';
				myDiscountDiv += '全国权重:<input id="countryWeight'+(val.id)+'"><br>';
				myDiscountDiv += '</div>';
				myDiscountDiv += '<div id="ptd'+(val.id)+'">';
				var pcount=1;
				$.each(provinceCityData,function (pindex,el){					
					myDiscountDiv+='';
					myDiscountDiv+=''+pcount+'.'+el.name+'';
					myDiscountDiv+='<input id="'+el.name+'" name="provinceCityes'+val.id+'">';
					myDiscountDiv+=''+(pcount%3==0?'<br>':'');
					myDiscountDiv+='';
					myDiscountDiv+='';
					pcount++;
				});
				
				myDiscountDiv += '</div>';
				myDiscountDiv += '</div>';
				myDiscountDiv+='<br>';
				
				$('#providerPhysicalChannelDiv').append(myDiscountDiv);
			});
	    },
	    dataType: 'json'
	});
}

function channelTypeProc(a){
//	console.log($('#channeltype1'+a).is(':checked'))
	if($('#channeltype1'+a).is(':checked')){
		$('#ctd'+a).show();
	}else{
		$('#ctd'+a).hide();
	}
	if($('#channeltype2'+a).is(':checked')){
		$('#ptd'+a).show();
	}else{
		$('#ptd'+a).hide();
	}
}

function physicalChannelProc(a,b){
	if(a){
		$('#physicalChannel'+b).show();
	}else{
		$('#physicalChannel'+b).hide();
	}
}

function initProviderDataDispatcher(){
	var provider_id=$('#provider_id').val();	
	
	var myUrl=basePath+'providerDataDispatcherByProviderId';
	$.ajax({
		type:'get',
		url :myUrl,
		data:{provider_id:provider_id  },
		success:function (dt){
			$.each(dt,function(index,val){
				if(val.province_code=='全国'){
				$('#countryWeight'+val.provider_physical_channel_id).val(val.weight);
				}
				//------------------------
				$('input[name="provinceCityes'+val.provider_physical_channel_id+'"]').each(function(index,el){
					if(el.id==val.province_code){
						el.value=val.weight;
					}
				});
				//------------------------
				
			});
	    },
	    dataType: 'json'
	});
	
}

function setAllPhysicalChannel(){
	allPhysicalChannelFlag=!allPhysicalChannelFlag;
	$('input[name="physicalChannel"]').each(function(index,el){
		el.checked=allPhysicalChannelFlag;
		physicalChannelProc(allPhysicalChannelFlag,el.value);
	});
}

function setAllCountry(){
	allCountryFlag=!allCountryFlag;
	$('input[name="physicalChannel"]').each(function(index,el){
//		if(el.checked){
			if(allCountryFlag){
				$('#channeltype1'+el.value).prop("checked",allCountryFlag);//全选  
				$('#ctd'+el.value).show();
			}else{
				$('#channeltype1'+el.value).prop("checked",allCountryFlag);//取消全选  
				$('#ctd'+el.value).hide();
			}
//		}

	});
	
}

function setAllProvince(){
	allProvinceFlag=!allProvinceFlag;
	$('input[name="physicalChannel"]').each(function(index,el){
//		if(el.checked){
			if(allProvinceFlag){
				$('#channeltype2'+el.value).prop("checked",allProvinceFlag);//全选  
				$('#ptd'+el.value).show();
			}else{
				$('#channeltype2'+el.value).prop("checked",allProvinceFlag);//全选  
				$('#ptd'+el.value).hide();
			}
//		}
	});
}