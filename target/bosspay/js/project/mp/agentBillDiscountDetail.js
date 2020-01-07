$(function(){
	getProviderList();
	initRadioStyle();	// 加载i-checks类样式
	setMyActive(5,1); //设置激活页
});
/*获取运营商列表*/
function getProviderList(){
	$("#choose").hide();
	var provider = $("#providerCheck");
	var myDelUrl = basePath + 'providerList';
	$.ajax({
		type:'get',
		url :myDelUrl,
		data:{ providerType: '1' },
		success:function (dt){
			$.each(dt, function(index,val) {
		    	pro = '<input name="providerId" type="radio" class="i-checks" value='+val.id+' />'+val.name+'&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;';
		    	provider.append(pro);
		    });
			initRadioStyle();
	    },
	    dataType: 'json'
	});
}
/*查询该代理商可用话费通道*/
function checkProvince(){
	$("#choose").hide();
	$('#province').empty();
	$("#agentDataDiscount").empty();
	var agentId = $('#agentId').val();
	var providerId = $("input[name='providerId']:checked").val();
	 if(providerId==''||providerId==null){
			swal({
		        title: "请选择运营商",
		        type: "success",
		        confirmButtonText: "ok"
			});
		 return false;
		}
	var dataType = $("input[name='dataType']:checked").val();
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
			if(!data){
				$('#province').html('未查询到可用通道');
			}else{
				var province=$('#province');
				for(var i =0;i<data.length;i++){ 
						pro='<input name="provinceCode" type="radio" class="i-checks" value="'+data[i]+'"/>'+data[i]+'&nbsp;&nbsp;&nbsp;&nbsp;';
						province.append(pro);
				} 
			}
			initRadioStyle();
		}
	});
}

function getDiscountMeg(){
	var id=$('#agentId').val();
	var providerId = $("input[name='providerId']:checked").val();
	var province = $("input[name='provinceCode']:checked").val();
	var dataType = $("input[name='dataType']:checked").val();
	
	$.ajax({
		type:'GET',
		url:basePath + 'agentDataDiscountDetail/' + id,
		data:{
			providerId:providerId,
			provinceCode:province,
			dataType:dataType
		},
		dataType:'json',
		success:function(data){
				$.each(data, function(index,val) {
					$('#'+val.dataPkgId+'').val(val.discount);
				});
			}
		});
}

function getDiscountPkgMeg(){
	$("#choose").show();
	$("#dis").val("");
	$("#agentDataDiscount").empty();
	var id=$('#agentId').val();
	var providerId = $("input[name='providerId']:checked").val();
	var province = $("input[name='provinceCode']:checked").val();
	var dataType = $("input[name='dataType']:checked").val();
	 if(providerId==''||providerId==null){
			swal({
		        title: "请选择运营商",
		        type: "success",
		        confirmButtonText: "ok"
			});
		 return false;
		}
	 if(province==''||province==null){
			swal({
		        title: "请选择省份",
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
	$.ajax({
		type:'GET',
		url:basePath + 'agentDataDiscountDetailPkgList/' + id,
		data:{
			providerId:providerId,
			provinceCode:province,
			dataType:dataType
		},
		dataType:'json',
		success:function(data){
			var agentDataDiscount = $("#agentDataDiscount");
			if(!data){
				agentDataDiscount.html('未查询到所在地的话费包');
				}else{
					$.each(data, function(index,val) {
						myDiscountDiv='';
						myDiscountDiv+='<div class="col-sm-4 form-group ">';
						myDiscountDiv+='	<label class="col-sm-6 control-label"><input class="hide" type="checkbox" value="'+val.id+'" name="dId"/>'+val.dataPkgName+'</label>';
						myDiscountDiv+='    <div class="col-sm-6">';
						myDiscountDiv+='    	<input class="form-control" type="number" id="'+val.id+'" name="discount" value=""/>';
						myDiscountDiv+='	</div>';
						myDiscountDiv+='</div>';
						agentDataDiscount.append(myDiscountDiv);
				    });
				}
			getDiscountMeg();
			initRadioStyle();
			}
		});
}


function updataDiscount(){
	 var obj=document.getElementsByName('dId');
	 var dis = $("#dis").val();
	 var s=''; 
	 for(var i=0; i<obj.length; i++){    
		    if(obj[i].checked) {
		    	s=obj[i].value;  //如果选中，将value添加到变量s中 
		    	$("#"+s+"").val(dis);
		    }
	 	}    
}

function checkUpdata(){
	var id=$('#agentId').val();
	var providerId = $("input[name='providerId']:checked").val();
	var province = $("input[name='provinceCode']:checked").val();
	var dataType = $("input[name='dataType']:checked").val();
	 if(providerId==''||providerId==null){
			swal({
		        title: "请选择运营商",
		        type: "success",
		        confirmButtonText: "ok"
			});
		 return false;
		}
	 if(province==''||province==null){
			swal({
		        title: "请选择省份",
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
	var inputArray=document.getElementsByName('discount');
	var meg='';
	for(var i=0;i<inputArray.length;i++){//循环整个input数组
        var input =inputArray[i];//取到每一个input
       var m =input.id+'-'+input.value;
       meg+=m+',';
    }
	swal({  
		title: "确定要修改吗?", 
		text: "此操作将会修改该代理商的折扣信息",  
		type: "warning",  
		showCancelButton: true,  
		confirmButtonColor: "#DD6B55",  
		confirmButtonText: "确定",  
		cancelButtonText: "取消", 
		closeOnConfirm: false,
		closeOnCancel:false
		},
		function(isConfirm){  
			if (isConfirm) {    
				$.ajax({
					type:'PUT',
					url :basePath +'agentDataDiscountUpdate',
					data:{ 
						agentId:id,
						providerId:providerId,
						provinceCode:province,
						type:dataType,
						discountMeg:meg
					},
					success:function (dt){
						if(dt=='更新成功'){
							swal("友情提示", "更新折扣成功！", "success");
						}else{
							swal("友情提示", "更新折扣失败,", "error");
						}
				    },
				    dataType: 'html'
				});
			} else {     
				swal("取消", "折扣未修改", "error");   
				} 
			});
}

//加载i-checks类样式
function initRadioStyle(){  
    $('.i-checks').iCheck({
        checkboxClass: 'icheckbox_square-green',
        radioClass: 'iradio_square-green',
    });
    
    bindProviderIdEvent();
    bindDataTypeEvent();
    bindProvinceCodeEvent();
}

//初始化单选钮单击事件,在插件初始化之前绑定 
function bindProviderIdEvent(){
	$('input[name=providerId]').on('ifChecked', function(event){
		checkProvince();
	});
}

//初始化单选钮单击事件,在插件初始化之前绑定 
function bindDataTypeEvent(){
	$('input[name=dataType]').on('ifChecked', function(event){
		checkProvince();
	});
}

//初始化单选钮单击事件,在插件初始化之前绑定 
function bindProvinceCodeEvent(){
	$('input[name=provinceCode]').on('ifChecked', function(event){
		getDiscountPkgMeg();
	});
}

//function show(){
//	alert(1);
//	var flag=document.getElementById("choose").checked;
//	if(flag){
//		selectAllItem();
//	}else{
//		unSelectAll();
//	}
//}
//
//全选
function selectAllItem(){
    var items = document.getElementsByName("dId");
    for(var i = 0; i < items.length; i++){
     items[i].checked = true;
    }
    updataDiscount();
}
////反选
//function unSelectAll(){
//    var items = document.getElementsByName("dId");
//    for(var i = 0; i < items.length; i++){    
//     items[i].checked = false;
//    }  
//}