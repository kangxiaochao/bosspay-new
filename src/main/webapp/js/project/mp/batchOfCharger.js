var myTbContentId = 'mytbc1';
var myJqTbContentId = '#' + myTbContentId;
var myTbId = 'myt1';
var myJqTbId = '#' + myTbId;
var myPageId = 'myp1';
var myJqPageId = '#' + myPageId;

$(function() {
	$("#submit").attr("disabled", true);
	setSelectStyle($("#state"));
	setSelectStyle($("#type"));

	var t1 = $('<table></table>');
	t1.attr('id', myTbId);
	$(myJqTbContentId).append(t1);

	var p1 = $('<div></div>');
	p1.attr('id', myPageId);

	p1.insertAfter(t1);

	$(myJqTbId).jqGrid( {
		url : basePath + 'batchOfChargerOrder',
		mtype : "GET",
		datatype : "json",
		caption : "小面额话费批充列表", // 设置表标题
		page : 1,
		colNames : [ 'id','手机号', '充值金额', '充值时间', '充值状态', '运营商通道','充值账号'],
		colModel : [ 
				{ name: 'id', key: true, sortable: false,hidden:true },
	          	{ name: 'phone', sortable: false}, //查询条件要添加index
	          	{ name: 'money', sortable: false },
	          	{ name: 'dateTime', sortable: false },
	          	{ name: 'state', sortable: false },
	          	{ name: 'type', sortable: false },
	          	{ name: 'account', sortable: false }
	     ],
		// width: 750,
		height : 'auto',
		multiselect : true,// 多选框
		multiboxonly : true,// 为true时是单选
		autowidth : true,
		shrinkToFit : true,
		hidegrid : false, // 隐藏表格右上角的"展开/收缩jqGrid内容的小箭头"
		autoScroll : false,
		rowNum : myRowNum,
		rowList : myRowList,
		viewrecords : true,// 显示总记录数
		rownumbers : true,
		pager : myJqPageId
	});

	$('#billPkgName').focus();
	setTimeout(function() {
		$('.wrapper-content').removeClass('animated fadeInRight');
	}, 700);

	message();
	
	getAgentId();
	
	 $("#creatTime").val(laydate.now() + ' 00:00:00');
	 $("#endTime").val(laydate.now(1) + ' 00:00:00');
});

//获取所有代理商
function getAgentId() {
	var dataGroupId = $("#agentId");
	var myDelUrl = basePath + 'selectAgentList';
	$.ajax({
		type : 'get',
		url : myDelUrl,
		success : function(dt) {
			$.each(dt, function(index,val) {  
				option = $("<option>").text(val.name).val(val.id)
				dataGroupId.append(option);
			});
			setSelectStyle(dataGroupId);
		},
		dataType : 'json'
	});
	
}

function search() {
	var creatTime = $('#creatTime').val();
	var endTime = $('#endTime').val();
	var state = $('#state').val();
	var type = $('#type').val();
	var account =  $('#account').val();
	var phone =  $('#phone').val();
	$(myJqTbId).jqGrid('setGridParam', {
		url : basePath + 'batchOfChargerOrder',
		postData : {
			'creatTime' : creatTime,
			'endTime'   : endTime,
			'state'     : state,
			'type'      : type,
			'account'   : account,
			'phone'     : phone
		}, // 发送数据
		page : 1
	}).trigger("reloadGrid"); // 重新载入
}

/**
 * 提交充值信息
 * 
 * @returns
 */
function submits() {
	layer.load();
	$("#submit").attr("disabled", true);
	var form = new FormData(document.getElementById("upload"));
	$.ajax({
		type : "post",
		url  : "charge",
		data : form,
		processData : false,
		contentType : false,
		dataType : 'json',
		success : function(data) {
			layer.closeAll('loading');
			if(data == '-1') {
				layer.msg('文件格式错误，请联系客服人员进行核对', {
					 time: 20000, //20s后自动关闭
					 btn: ['明白了', '知道了', '哦']
				 });
			}else if(data == '-2') {
				layer.msg('充值最多三万笔，请查看充值笔数', {
					 time: 20000, //20s后自动关闭
					 btn: ['明白了', '知道了', '哦']
				 });
			}else if(data == '-3'){
				layer.msg('余额不足，请加款后在进行操作', {
					 time: 20000, //20s后自动关闭
					 btn: ['明白了', '知道了', '哦']
				 });
			}else {
				layer.msg('提交成功' + data + '笔', {
					 time: 20000, //20s后自动关闭
					 btn: ['明白了', '知道了', '哦']
				 });
			}
		}
	});
}

/**
 * 验证登录密码
 * 
 * @returns
 */
function sub() {
	var password = $("input[name=password]").val();
	$.ajax({
		type : "post",
		url  : "validate",
		data : {
			pass : password
		},
		success : function(data) {
			if (data == '1') {
				$("#submit").attr("disabled", false);
			} else {
				$("#submit").attr("disabled", true);
			}
		}
	});
}

function gradeChange() {
	var state = $("#state").val();
	if (state == 2) {
		$("#yan").css("display", "block");
	} else {
		$("#yan").css("display", "none");
	}
}

/**
 * 发送验证码
 * 
 * @returns
 */
function login4Cookie() {
	var password = $("input[name=pass]").val();
	var account = $("input[name=account]").val();
	if (account.length > 0 && password.length > 0) {
		$.ajax({
			type : "post",
			url : "login4Cookie",
			data : {
				password : password,
				account : account
			},
			success : function(data) {
				if (data == '1') {
					$("#sd").css("display", "none");
					$("#msg").text("等待获取验证码，时间两分钟");
					sub();
				} else if (data == '2') {
					$("#msg").text("验证码发送失败");
					sub();
				} else {
					$("#sd").css("display", "none");
					$("#msg").text("验证码发送成功");
					sub();
				}
			}
		});
	} else {
		alert("请输入充值工号及密码");
	}
}


/**
 * 绑定日期控件
 */
laydate({
	elem : '#creatTime',// 目标元素。由于laydate.js封装了一个轻量级的选择器引擎，因此elem还允许你传入class、tag但必须按照这种方式
						// '#id .class'
	event : 'focus', // 响应事件。如果没有传入event，则按照默认的click
	format : 'YYYY-MM-DD hh:mm:ss', //日期格式
	istime : true, //是否开启时间选择
});

/**
 * 绑定日期控件
 */
laydate({
	elem : '#endTime',//目标元素。由于laydate.js封装了一个轻量级的选择器引擎，因此elem还允许你传入class、tag但必须按照这种方式 '#id .class'
	event : 'focus', //响应事件。如果没有传入event，则按照默认的click
	format : 'YYYY-MM-DD hh:mm:ss', //日期格式
	istime : true, //是否开启时间选择
});

function deriveExcel(){
	var creatTime=$('#creatTime').val();
	var endTime=$('#endTime').val();
	var type=$('#type').val();
	var state=$('#state').val();
	var account =  $('#account').val();
	var phone =  $('#phone').val();
	
	var src = basePath+"deriveBatch?";
	
	if(creatTime.length>0){
		src+="&creatTime="+creatTime;
	}
	if(endTime.length>0){
		src+="&endTime="+endTime;
	}
	if(type.length>0){
		src+="&type="+type;
	}
	if(state.length>0){
		src+="&state="+state;
	}
	if(account.length>0){
		src+="&account="+account;
	}
	if(phone.length>0){
		src+="&phone="+phone;
	}
	window.location.href = src;
}