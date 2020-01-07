$(function() {
	$(':input').labelauty();
	setMyActive(4,2,1); //设置激活页
});

// 号码变动时的操作 修改样式
function changemobileExt1(obj) {
	$("#originalprice").empty();
	$("#disprice").empty();
	$("#billpkgids").empty();
	var searchStatus = '查询中请稍后';
	var mobilenumber = $("#mobilenumber").val();
	platform_verify.onblurVali(obj);
	var length = mobilenumber.length;
	$("#disprice").val("");

	var isNumV1Flag = isNumberV1(mobilenumber);
	if (!isNumV1Flag) {
		$("#phoneMessage").html('手机号格式不正确');
	}

	if (length == 11 || length == 13 && isNumV1Flag) {
		$("#phoneMessage").html(searchStatus);
		var dataType = "nation";
		$.ajax({
			type : "get",
			url : basePath + 'sectionAndBillPkgGet',
			data : {
				"phone" : mobilenumber,
				"dataType" : dataType
			},
			dataType : 'json',
			success : function(dt) {
				if (dt != "" && dt != null) {
					$("#provinceCode").empty();
					$("#providerId").empty();
					$("#providerId").val(dt.provider_id);
					$("#provinceCode").val(dt.province_code);
					$("#phoneName").html(dt.phoneName);
					$("#phoneAmount").html(dt.phoneAmount);
					$("#phoneMsg").show();
					console.log(dt.province_code);
					$("#phoneMessage").html(dt.province_code + "-" + dt.city_code+"-"+dt.providerName);
					$("#terminalName").val(dt.agentName);
					var billpkgids = $("#billpkgids");
					$("#billpkgids").empty();
					$.each(dt.billPkgMessage, function(index,val) {
						if(val.name == "任意充"){
							var li="<li>"+"<input type='radio' " +
				     		"name='billpkgs' onclick='changeBillPkgExt(this);' pkgId="+val.id+" price='任意充' "+
				     		" value='任意充' data-labelauty='任意充'/>"+"</li>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;";
						}else{
							var li="<li>"+"<input type='radio' " +
				     		"name='billpkgs' onclick='changeBillPkgExt(this);' pkgId="+val.id+" price="+val.price+""+
				     		" value="+val.value+" data-labelauty='"+val.value+"元'/>"+"</li>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;";
						}
						billpkgids.append(li);
					    });	
				} else {
					$("#phoneMessage").html('未知');
				}
				$('input[name=billpkgs]').labelauty();

			},
			error:function(data){
				swal({
 			        title: "系统异常",
 			        type: "success",
 			        confirmButtonText: "ok"
 				});
 			},
		});
	}
	if (length == 0) {
		$("#disprice").val('');
		$("#terminalName").val('');
		$("#phoneMessage").html('');
	}
}

function changeBillPkgExt(obj) {
	$("#feeDiv").hide();
	$("#originalprice").val('');
	$("#disprice").val('');
	var pkgId= $(obj).attr("pkgId");
	var price= $(obj).attr("price");
	var providerId=$("#providerId").val();
	var provinceCode=$("#provinceCode").val();

	if(price == "任意充"){
		$("#feeDiv").show();
		$("#fee").attr("pkgId",pkgId);
	}else{
		$.ajax({
			type : "get",
			url : basePath + 'agentBillDiscountGet',
			data : {
				"billPkgId" : pkgId,
				"providerId" : providerId,
				"provinceCode":provinceCode,
				"price":price
			},
			dataType : "json",
			success:function(dt){
				if (dt != "0") {
					$("#originalprice").val(price);
					$("#disprice").val(dt);
				}else{
					$("#originalprice").val(price);
					$("#disprice").val('未查询到折扣');
					$("#sub").attr("disabled", true);
				}
			}	
		});
	}
		
}

function changeFee(){
	var fee = $("#fee").val();
	if(fee > 1000){
		$("#originalprice").val('不允许充值该面值');
		$("#disprice").val('未查询到折扣');
		$("#sub").attr("disabled", true);
	}else{
		if(fee != ''){
			var pkgId = $("#fee").attr("pkgId");
			var providerId=$("#providerId").val();
			var provinceCode=$("#provinceCode").val();
			$("#originalprice").val('');
			$("#disprice").val('');
			$.ajax({
				type : "get",
				url : basePath + 'agentBillDiscountGet',
				data : {
					"billPkgId" : pkgId,
					"providerId" : providerId,
					"provinceCode":provinceCode,
					"price":fee
				},
				dataType : "json",
				success:function(dt){
					if (dt != "0") {
						$("#originalprice").val(fee);
						$("#disprice").val(dt);
						$("#sub").removeAttr("disabled");
					}else{
						$("#originalprice").val(fee);
						$("#disprice").val('未查询到折扣');
						$("#sub").attr("disabled", true);
					}
				}	
			});
		}
	}
}

function isNumberV1(str) {
	var myNumber = /^[0-9]+$/;
	return myNumber.test(str);
}


function submitOrder(){
	var spec = $("input[name='billpkgs']:checked").val();
	var terminalName = $("#terminalName").val();
	var phoneNo = $("#mobilenumber").val();
	var originalprice = $("#originalprice").val();
	var orderType = "2";//话费
	var dataType='nation';
	var type = "1";
	layer.confirm('充值号码：'+phoneNo+'<br/>充值金额：'+originalprice,
	{
        btn : [ '确定充值', '取消充值' ]//按钮
    }, function(index) {
    	$("#sub").attr("disabled", true);
    	layer.closeAll('dialog');
    	$.ajax({
    		type : "get",
    		url : basePath + 'order/webSubmit',
    		data : {
    			"terminalName" : terminalName,
    			"phoneNo" : phoneNo,
    			"type" : type,
    			"spec" : parseInt(originalprice * 100),
    			"orderType" : orderType,
    			"scope" : dataType
    		},
    		success : function(dt) {
    			swal({
    		        title:dt.message,
    		        type: "success",
    		        confirmButtonText: "ok"
    			},
    			function(isConfirm){
    				  if (isConfirm) {
    					  location.reload();
    				  }
    			});
    			$("#sub").attr("disabled", false);
    		},
    		error:function(data){
    			swal({
    			        title: "系统异常",
    			        type: "success",
    			        confirmButtonText: "ok"
    				});
    			},
    			dataType : 'json'
    	});
    });
}