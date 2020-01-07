$(function(){
	$(':input').labelauty()	
	//Txt文本导入
	$("#importTxt").click(function () {
        $("#numberFile").click();
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
            alert("操作失败:服务器错误");
        }
    });
	
	//过滤错号
	$("#checkMobile").click(function () {
		var numberMeg = $("#mobilenumbers").val();
		if(numberMeg.length > 1){
			$.ajax({
				type : "post",
				url:basePath+"billQueryBatchCheckPhone",
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
						type: "success",
						confirmButtonText: "ok"
					});
				},
				dataType : "json",
			});
		} else {
			layer.msg('号码不能为空');
		}
	});
	
	setMyActive(9,1,1); //设置激活页
});

function countNum(count,valid,inValid) {
	
    $("#countNumMsg").html("共计号码：<font color=\"red\">" + count + " </font>个" +
    		",有效号码：<font color=\"red\">" + valid + " </font>个" +
    		",无效号码：<font color=\"red\">" + inValid + " </font>个</i>");
    
    if (valid > 0 ) {
		$("#queryButtion").show();
	}else{
		$("#queryButtion").hide();
	}
}

//号码变动时的操作锁定提交按钮
function lockSubmitButton(){
$("#submitButtonId").attr("disabled", true);
}

function checkType(){
	$("#meg").empty();
}

function downLoadTemp(){
	$('#downloadFrame').attr('src',basePath + "downloadFiles/批量充值模板.xlsx");
}

function submitOrder() {
	var numberMeg = $("#mobilenumbers").val();
	if (numberMeg.length == 0) {
		layer.msg('号码不能为空');
		return false;
	}
	layer.load();
	var terminalName = $("#terminalName").val(); 
	var phoneNo = $("#mobilenumbers").val();
	var orderType = "2";
	var scope = "nation";
	var type = "2";
	$.ajax({
		type : "post",
		url : basePath + 'batchQueryPhoneInfoGet',
		data : {
			'terminalName': terminalName,
			'phoneNo': phoneNo,
			'orderType': orderType,
			scope: scope,
			type:type
		},
		success : function(dt) {
			$("#phoneInfo").html('');
			layer.closeAll('loading');
			var html = 	'<fieldset>'+
						'	<legend>查询结果</legend>';
			for (var int = 0; int < dt.length; int++) {
				html +=	'	<div class="col-sm-6 form-group">'+
						'		<span class="col-sm-2">' + dt[int].phoneNo + '</span>'+
						'		<span class="col-sm-3">' + dt[int].provinceCode + dt[int].cityCode + dt[int].providerName + '</span>'+
						'		<span class="col-sm-2">' + dt[int].phoneName + '</span>'+
						'		<span class="col-sm-4">' + dt[int].phoneAmount.replace("元","") + '</span>'+
						'		<span class="col-sm-1"></span>'+
						'	</div>';
			}
			html +=		'</fieldset>';
			$("#phoneInfo").html(html);
			$("#phoneInfo").show();
		},
		error:function(data){
			layer.closeAll('loading');
			layer.msg('查询余额失败');
 		},
		dataType : 'json'
	});
}
