$(function(){
	$(':input').labelauty()	
	//Txt文本导入
	$("#importTxt").click(function () {
        $("#numberFile").click();
    });
	
	//Txt文本导入
	$("#importExcel").click(function () {
        $("#numberFile2").click();
    });
	
	$("#fileForm").ajaxForm({
        success: function (data) {
            $("#mobilenumbers").val(data);
        },
        error: function (XmlHttpRequest, textStatus, errorThrown) {
            alert("操作失败:服务器错误");
        }
    });
	
	$("#fileForm1").ajaxForm({
        success: function (data) {
            $("#mobilenumbers").val(data);
        },
        error: function (XmlHttpRequest, textStatus, errorThrown) {
            alert("操作失败:模板格式错误,请检查是否有空格或者空字符");
        }
    });
	
	//话费批量充值检测手机号
	$("#checkMobile").click(function () {
		var numberDetectionPath = 'billChargeBatchCheckPhone';
		numberDetection(numberDetectionPath);
	});
	
	//同面值话费批量充值检测手机号
	$("#checkMobileTwo").click(function () {
		var numberDetectionPath = 'billChargeBatchCheckPhoneTwo';
		numberDetection(numberDetectionPath);
	});
	setMyActive(4,2,2); //设置激活页
});
//对输入的手机号进行验证
function numberDetection(path){
	$("#pkg").empty();
	var numberMeg = $("#mobilenumbers").val();
	if(numberMeg.length > 1){
		$.ajax({
			type : "post",
			url:basePath+path,
			data:{
				numberMeg:numberMeg
			},
			success:function(data){
				if (data != "" && data != null) {
					if(data.code == '1'){
						$("#terminalName").val(data.agentName);
						$("#mobilenumbers").val(data.phone);
						var count = data.count;
						var valid = data.valid;
						var inValid = data.inValid;
						countNum(count,valid,inValid);
						var meg=$("#meg");
						var dt=data.phoneMeg;
						for(var key in dt) {
							var pkgData=dt[key];
							var li='<li>'+pkgData+'</li>'
							var ul='<ul class="dowebok">'+li+'</ul>';
							var div2='<div class="col-sm-8">'+ul+'</div>';
							var label='<label class="col-sm-2 control-label">'+key+'</label>';
							var div='<div class="form-group ">'+label+div2+'</div>';	
							meg.append(div);
						}
					}else{
						swal({
							title: data.msg,
							type: "success",
							confirmButtonText: "ok"
						});
					}
				}else{
					swal({
						title: "系统异常",
						type: "success",
						confirmButtonText: "ok"
					});
				}
			},
			error:function(data){
				swal({
					title: "系统异常",
					confirmButtonText: "ok"
				});
			},
			dataType : "json",
		});
	}
	
}

//同面值批量充值，任意充值金额超出范围禁止充值
function amountOfProcessing(){
	var fee =  $("#anyAmount").val();
	if(fee >= 0.01 && fee <= 500){
		$("#span").text("");
		$("#subs").attr("disabled",false);
	}else if(fee < 0.01 && fee > 500){
		$("#span").text("输入金额超出充值范围,请修改充值金额！");
		$("#subs").attr("disabled",true);
	}else{
		$("#span").text("输入金额有误,请核对输入金额！");
		$("#subs").attr("disabled",true);
	}
}

function countNum(count,valid,inValid) {
	
    $("#countNumMsg").html("共计号码：<font color=\"red\">" + count + " </font>个" +
    		",有效号码：<font color=\"red\">" + valid + " </font>个" +
    		",无效号码：<font color=\"red\">" + inValid + " </font>个</i>");
}


//同面值批量充值，选中其他金额显示金额输入框
function showMoney(){
	$("#divAmount").show();
	$("#subs").attr("disabled",true);
}
//同面值批量充值，选中已有话费包时隐藏金额输入框&金额清空
function hideMoney(){
	$("#subs").attr("disabled",false);
	$("#anyAmount").val("");
	$("#span").text("");
	$("#divAmount").hide();
}

//号码变动时的操作锁定提交按钮
function lockSubmitButton(){
$("#submitButtonId").attr("disabled", true);
}

function checkType(){
	$("#meg").empty();
}

function downLoadTemp2(){
	$('#downloadFrame').attr('src',basePath + "downloadFiles/批量充值模板.xlsx");
}
function downLoadTemp(){
	$('#downloadFrame').attr('src',basePath + "downloadFiles/同面值批量充值模板.xlsx");
}

/*相同金额批量充值 */
function submitTheSameValueOrder() {
	$("#subs").attr("disabled", true);
	layer.load();
	var terminalName = $("#terminalName").val(); 
	var phoneNo = $("#mobilenumbers").val();
	var fee = $("input[name='money']:checked").val();
	if(fee == 'anyAmount'){
		fee = $("#anyAmount").val();
		fee =  Math.round(fee * 100) / 100;		//输入其他金额只保留2位小数
	}
	var orderType = "2";
	var scope = "nation";
	var type = "3";								//type = 1 单冲   |  type = 2 指定金额批量充值 | type = 3 同一金额批量充值
	$.ajax({
		type : "post",
		url : basePath + 'order/bitchWebSubmit',
		data : {
			'terminalName': terminalName,
			'phoneNo': phoneNo,
			'fee': fee,
			'orderType': orderType,
			scope: scope,
			type:type
		},
		success : function(dt) {
			layer.closeAll('loading');
			swal({
		        title:"返回信息",
		        text:"提交成功"+dt.success+"个,提交失败"+dt.error+"个,失败信息:\n"+dt.errorMessage,
		        type: "success",
		        confirmButtonText: "ok"
			});
			$("#subs").attr("disabled", false);
		},
		error:function(data){
			layer.closeAll('loading');
				swal({
 			        title: "系统异常",
 			        type: "success",
 			        confirmButtonText: "ok"
 				});
 			},
		dataType : 'json'
	});
}

/* 指定金额充值 （模板：  18888888888-10） */
function submitOrder() {
	$("#subs").attr("disabled", true);
	layer.load();
	var terminalName = $("#terminalName").val(); 
	var phoneNo = $("#mobilenumbers").val();
	var orderType = "2";
	var scope = "nation";
	var type = "2";
	$.ajax({
		type : "post",
		url : basePath + 'order/bitchWebSubmit',
		data : {
			'terminalName': terminalName,
			'phoneNo': phoneNo,
			'orderType': orderType,
			scope: scope,
			type:type
		},
		success : function(dt) {
			layer.closeAll('loading');
			swal({
		        title:"返回信息",
		        text:"提交成功"+dt.success+"个,提交失败"+dt.error+"个,失败信息:\n"+dt.errorMessage,
		        type: "success",
		        confirmButtonText: "ok",
                closeOnConfirm: false,
			},function(isConfirm){
                if (isConfirm) {
                    setTimeout(function(){
                        location.reload();
                    },1000);
                } else {
                   // swal("Cancelled", "Your imaginary file is safe :)", "error");
                }
            });


		},
		error:function(data){
			layer.closeAll('loading');
				swal({
 			        title: "系统异常",
 			        type: "success",
 			        confirmButtonText: "ok"
 				});
 			},
		dataType : 'json'
	});
}
