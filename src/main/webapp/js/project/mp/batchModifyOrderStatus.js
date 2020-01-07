$(function(){
	$(':input').labelauty();
	$("#importTxt").click(function () {
        $("#numberFile").click();
    });
	$("#orderCheck").click(function () {
		orderCheck();
	});
	
	$("#fileForm").ajaxForm({
        success: function (data) {
            $("#agentOrderId").val(data);
        },
        error: function (XmlHttpRequest, textStatus, errorThrown) {
            alert("操作失败:服务器错误");
        }
    });
	
	$("#fileForm1").ajaxForm({
        success: function (data) {
            $("#agentOrderId").val(data);
        },
        error: function (XmlHttpRequest, textStatus, errorThrown) {
            alert("操作失败:服务器错误");
        }
    });
	
	setMyActive(9,1,4); //设置激活页
});

function lockSubmitButton(){
	$("#submitButtonId").attr("disabled", true);
}

// 校验订单合法性
function orderCheck(){
	var agentOrderId = $("#agentOrderId").val().trim().replace(/，/g,",");
	var agentOrderArr = agentOrderId.split(",");
	var agentOrderLength = agentOrderArr.length;
	var count = 0, errorCount = 0;;
	var agentOrderIdStr = "";
	
	if (agentOrderId == '') {
		agentOrderLength = 0;
	} else {
		if (agentOrderLength > 0) {
			for (var int = 0; int < agentOrderLength; int++) {
				if (agentOrderArr[int].trim() == '') {
					errorCount++;
				} else {
					agentOrderIdStr += agentOrderArr[int] + ",";
					count++
				}
			}
		}
	}
	$("#countNumMsg").html('合计订单<span style="color:red">'+agentOrderLength+'</span>个,合法订单<span style="color:red">'+count+'</span>个,非法订单<span style="color:red">'+errorCount+'</span>个');
	// 重新将合法订单赋值
	$("#agentOrderId").val(agentOrderIdStr.substr(0,agentOrderIdStr.length -1 ));
}

function submitOrder() {
	var agentOrderId = $("#agentOrderId").val();
	if (agentOrderId.length == 0) {
		layer.msg('代理商订单号不能为空');
		return false;
	}
	layer.load();
	var radios = document.getElementsByName("stauts");
	var status = 0;
	for (var i = 0; i < radios.length; i++) {
		if (radios[i].checked == true) {
			status = radios[i].value;
		}
	}
	$.ajax({
		type : "post",
		url : basePath + 'batchModifyOrderStatus',
		data : {
			agentOrderId: agentOrderId,
			status:status
		},
		success : function(dt) {
			layer.msg("修改状态成功，修改成功"+dt.succnum+"条");
			layer.closeAll('loading');
		},
		error:function(data){
			layer.closeAll('loading');
			layer.msg('修改状态失败');
 		},
		dataType : 'json'
	});
}