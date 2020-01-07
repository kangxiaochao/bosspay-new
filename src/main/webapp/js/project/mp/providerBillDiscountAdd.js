$(function(){
	initProviderList();	// 初始化运营商下拉列表
	initProvinceCode(); //初始化省份信息
	initRadioStyle();	// 加载i-checks类样式
	setMyActive(6,3); //设置激活页
});

// 初始化运营商下拉列表
function initProviderList(){
	var myUrl=basePath+'providerList';
	$.ajax({
		type:'get',
		url :myUrl,
		async: false,
		data:{
			providerType:'1'
		},
		success:function (dt){
			$.each(dt,function(index,val){
				var option = $("<option>").text(val.name).val(val.id)
		    	$("#providerId").append(option);
			});
			setSelectStyle($("#providerId"));
	    },
	    dataType: 'json'
	});
}

//初始化话费包范围(充值地区)
function initProvinceCode(){
	var selectId = "provinceCode";
//	//初始化省份信息
//	$('#'+selectId).empty();
//	$('#'+selectId).append('<option value="">请选择</option>');
//	$('#'+selectId).append('<option value="全国">全国</option>');
//	$.each(provinceCityBill,function (index,el){
//		$('#'+selectId).append('<option value="'+el.name+'">'+el.name+'</option>');
//	});
	$("#"+selectId).empty();
	var myUrl=basePath+'providerBillDispatcherToProvinceCode';
	var providerPhysicalChannelId = $("#providerPhysicalChannelId").val();	// 获取物理运营商通道id
	var providerId = $("#providerId").val();	//获取运营商id
	var dataType = $("#data_type").val();	// 话费包类型:全国,省
	$.ajax({
		type:'get',
		url :myUrl,
		async: false,
		data:{
			providerId:providerId,
			providerPhysicalChannelId:providerPhysicalChannelId
		},
		success:function (dt){
			var option = $("<option>").text('请选择');
			$("#"+selectId).append(option);
			$.each(dt,function(index,val){
				option = $("<option>").text(val.province_code).val(val.province_code)
		    	$("#"+selectId).append(option);
			});
			if(dataType == 1){
				$("#"+selectId+" option[value='全国']").remove();
			}
			setSelectStyle($("#"+selectId));
		},
		dataType: 'json'
	});
	
	//初始化省份信息下拉列表样式
	setSelectStyle($("#"+selectId));
}

// 获取运营商话费包
function getProviderBillPkg(){
	var providerId = $("#providerId").val();	//获取运营商id
	var dataType = $("#data_type").val();
	var provinceCode = $("#provinceCode").val();
	
	if(providerId != '' && dataType != '' && provinceCode != ''){
		$("#provinceBillDiscount").empty();
		var myUrl=basePath+'providerBillPkg';
		$.ajax({
			type:'get',
			url :myUrl,
			async: false,
			data:{
				providerId:providerId,
				dataType:dataType,
				provinceCode:provinceCode
			},
			success:function (dt){
				if('' != dt){
					var html='';
					$.each(dt,function(index,val){
						html += '<div class="col-sm-3 form-group">';
						html += '	<label class="col-sm-8 control-label" style="padding-left: 0px;padding-right: 0px;">';
						html += '	<input class="i-checks" type="checkbox" value="'+val.name+"-"+val.id+'" name="dId" checked />'+val.name+'</label>';
						html += ' 	<div class="col-sm-4" style="margin-left:50px;padding-left: 0px;padding-right: 0px;">';
						html += '    	返利折扣<input class="form-control" type="number" id="'+val.name+"-"+val.id+'" pkgName="billPkgDiscount" min="0.001" step="0.001">';
						html += '    	上家扣款折扣<input class="form-control" type="number" id="real-'+val.name+"-"+val.id+'" pkgName="billPkgRealDiscount" min="0.001" step="0.001">';
						html += '	</div>';
						html += '</div>';
					});
					$("#provinceBillDiscount").append(html);
					initRadioStyle();	// 加载i-checks类样式
				}
			},
			dataType: 'json'
		});
		// 获取当前物理运营商指定运营商指定地区的折扣包信息
		initBillPkgDiscount();
	}
}

// 获取运营商信息
//function getProviderIdes(){
//	var providerIdes = "";
//    $('#providerId input[name="providerIdes"]:checked').each(function(){//遍历每一个名字为interest的复选框，其中选中的执行函数      
//    	providerIdes += $(this).val() + ",";
//    });
//    return providerIdes.substr(0,providerIdes.length-1);
//}

// 获取话费包和折扣
function getProvincesAndDiscount(){
	var isNull = false;
	var provinceValue = "";
	var provinces = "";
	$('#provinceBillDiscount input[pkgName="billPkgDiscount"]').each(function(){//遍历每一个名字为interest的复选框，其中选中的执行函数      
		provinceValue = $(this).val();
		if(provinceValue == 0){
			provinceValue = '';
			isNull = true;
		}
		if ('' != provinceValue) {
			provinces += $(this).attr("id") + "-";
			provinces += $(this).val() + ",";
		}
	});
	if(isNull = true){
		layer.confirm('确认要保存吗?', {skin: 'layui-layer-molv',icon: 3, title:'提示'}, function(index){
			return provinces.substr(0,provinces.length-1);
		});
	}
}

// 更新运营商折扣信息
function setProvinceBillDicount(){
	var askMsg = "确认要保存吗?";
	var isNull = false;
	var discountValue = "";
	var billPkgDiscount = "";
	$('#provinceBillDiscount input[pkgName="billPkgDiscount"]').each(function(){//遍历每一个名字为interest的复选框，其中选中的执行函数      
		var realDis = $(this).next();
		discountValue = $(this).val();
		if(discountValue == "" || discountValue == 0){
			discountValue = '';
			isNull = true;
			askMsg = "有未设置的折扣信息,确认要保存吗?";
		}
		if (discountValue !=  '') {
			billPkgDiscount += $(this).attr("id") + "-";
			if(realDis.val()==''||realDis.val()==0){
				outMessage('error', '存在没有设置上家扣款折扣信息,请先设置折扣信息！', '友情提示');
				return;
			}
			billPkgDiscount += $(this).val() + "-" + realDis.val() + ",";
		}
	});
	
	if(billPkgDiscount == ''){
		outMessage('error', '没有设置任何折扣信息,请设置折扣信息！', '友情提示');
	}else{
		billPkgDiscount = billPkgDiscount.substr(0,billPkgDiscount.length-1);
		
		layer.confirm(askMsg, {skin: 'layui-layer-molv',icon: 3, title:'提示'}, function(index){
			var myUrl=basePath+'providerBillDiscountAdd';
			var providerPhysicalChannelId = $("#providerPhysicalChannelId").val();	//获取运营商物理通道id
			var providerId = $("#providerId").val();	//获取运营商id
			var provinceCode = $("#provinceCode").val();	//获取话费包地区
			if(provinceCode=="请选择"){
				outMessage('error', '话费包范围未选择,请选择话费包范围！', '友情提示');
			}else{
				$.ajax({
					type:'post',
					url :myUrl,
					data:{
						providerId:providerId,
						provinceCode:provinceCode,
						billPkgDiscount:billPkgDiscount,
						providerPhysicalChannelId:providerPhysicalChannelId
					},
					success:function (dt){
						if(dt=='true'){
							outMessage('success', '设置折扣成功！', '友情提示');
							$("#showProviderBillDiscountList").css("visibility","visible");
						}else{
							outMessage('warning', '设置折扣错误！', '友情提示');
						}
					},
					dataType: 'html'
				});
			}
			
			layer.closeAll('dialog');
		});
	}
}

// 获取当前物理运营商指定运营商指定地区的折扣包信息
function initBillPkgDiscount(){
	var myUrl=basePath+'providerBillDiscountInfo';
	var providerPhysicalChannelId = $("#providerPhysicalChannelId").val();	//获取运营商物理通道id
	var providerId = $("#providerId").val();	//获取运营商id
	var provinceCode = $("#provinceCode").val();	//获取话费包地区
	
	$.ajax({
		type:'get',
		url :myUrl,
		data:{  
			providerPhysicalChannelId : providerPhysicalChannelId,
			providerId : providerId,
			provinceCode : provinceCode
		},
		success:function (dt){
			$.each(dt,function(index,el){
				var inputId=$('#'+el.name+'-'+el.bill_pkg_id);
				var realDis = $('#real-'+el.name+'-'+el.bill_pkg_id);
				inputId.val(el.discount);
				realDis.val(el.real_discount);
			});
	    },
	    dataType: 'json'
	});
}

// 上传折扣模板
function uploadFile(){
	outMessage('info','上传中...请稍等片刻','处理状态');
	
	var provincesAndDiscountes = getProvincesAndDiscount();	// 获取省份信息和折扣
	var myUrl=basePath+'providerBillDiscountAddEx1';
	var providerId = $("#providerId").val();	//获取运营商id
	var providerPhysicalChannelId = $("#providerPhysicalChannelId").val();	//获取运营商物理通道id
	var dataType = $("#data_type").val();
	
	var fm=$('#uploadForm1');
	var options={
			url:myUrl,
			type:'post',
			data:{
				'providerId':providerId,
				'dataType':dataType,
				'provincesAndDiscountes':provincesAndDiscountes,
				'providerPhysicalChannelId':providerPhysicalChannelId
			},
			success:function(dt){
				outMessage('info','上传完成','处理状态');
				$("#showProviderBillDiscountList").css("visibility","visible");
			}
		}
		fm.ajaxSubmit(options);
}

// 加载i-checks类样式
function initRadioStyle(){  
    $('.i-checks').iCheck({
        checkboxClass: 'icheckbox_square-green',
        radioClass: 'iradio_square-green',
    });
    $('#choose').on('ifChanged', function(event){ //ifCreated 事件应该在插件初始化之前绑定 
		  show();
	});
    //初始化单选钮单击事件
    $('input[name=dataType]').on('ifChecked', function(event){  
    	$("#data_type").val(this.value);
    	initProvinceCode();
    	getProviderBillPkg();
    });
}

function show(){
	var flag=document.getElementById("choose").checked;
	if(flag){
		selectAllItem();
	}else{
		unSelectAll();
	}
}

function updataDiscount() {
	var obj = document.getElementsByName('dId');
	var dis = $("#dis").val();
	
	var s = '';
	for (var i = 0; i < obj.length; i++) {
		if (obj[i].checked) {
			s = obj[i].value; // 如果选中，将value添加到变量s中
			$("#" + s + "").val(dis);
		}
	}
}

function updataRealDiscount() {
	var obj = document.getElementsByName('dId');
	var dis = $("#realDis").val();
	
	var s = '';
	for (var i = 0; i < obj.length; i++) {
		if (obj[i].checked) {
			s = obj[i].value; // 如果选中，将value添加到变量s中
			$("#real-" + s + "").val(dis);
		}
	}
}

//全选
function selectAllItem(){
    var items = document.getElementsByName("dId");
    for(var i = 0; i < items.length; i++){
     	$("input[name='"+items[i].name+"']").iCheck('check');
    }
}
//反选
function unSelectAll() {
	var items = document.getElementsByName("dId");
	for (var i = 0; i < items.length; i++) {
		$("input[name='"+items[i].name+"']").iCheck('uncheck');
	}
}

// 清除所有折扣信息
//function selectProviderId(){
//	$('#provinceBillDiscount input[proName="provinceCityes"]').each(function(){//遍历每一个名字为interest的复选框，其中选中的执行函数      
//		$(this).val('');
//	});
//}

//上传折扣模板下载
function downLoadTemp(){
	$('#downloadFrame').attr('src',basePath + "downloadFiles/运营商物理通道话费包折扣模板.xlsx");
}


