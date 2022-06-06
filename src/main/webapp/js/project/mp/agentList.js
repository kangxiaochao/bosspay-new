var myTbContentId = 'mytbc1';
var myJqTbContentId = '#' + myTbContentId;
var myTbId = 'myt1';
var myJqTbId = '#' + myTbId;
var myPageId = 'myp1';
var myJqPageId = '#' + myPageId;

$(function() {
	var userId = $('#leftMenuDiv').attr('data-suId');
	
	$.ajax({
		type:'get',
		url :basePath + 'sysGetHasRole/'+userId,
		async: false,
		data:{},
		dataType: 'json',
		success:function (dt){
			var flag = false;
			for(var i in dt){
				var roleName = dt[i]["srName"]
				if(roleName=="代理商"){
					flag = true;
				}
			}
			if(flag){
				var t1 = $('<table></table>');
				t1.attr('id', myTbId);
				$(myJqTbContentId).append(t1);

				var p1 = $('<div></div>');
				p1.attr('id', myPageId);

				p1.insertAfter(t1);

				$(myJqTbId).jqGrid({
					url : basePath + 'agent',
					mtype : "GET",
					datatype : "json",
					caption : "代理商列表<input type=\"button\" value=\"显示/隐藏 列\" onclick=\"showColumnDialog()\" />", //设置表标题
					page : 1,
					colNames : [ 'id', '接口编号', '代理商名称', '余额', '利润', '生效日期', '截止日期', '状态' ],
					colModel : [ {name : 'id',key : true,sortable : false,hidden : true}, 
//					             {name : 'suName',sortable : false},	//用户名称
					             {name : 'name',sortable : false}, 
					             {name : 'nickname',sortable : false}, //代理商名称
					             {name : 'balance',sortable : false}, 	//余额
					             {name : 'profit',sortable : false}, 	//利润
//					             {name : 'credit',sortable : false}, 	//授信额
//					             {name : 'billGroupName',sortable : false},	//话费通道组
//					             {name : 'billDiscountModelName',sortable : false},	//话费折扣模板名称
//					             {name : 'dataGroupName',sortable : false},	//话费通道组
//					             {name : 'dataDiscountModelName',sortable : false},	//话费折扣模板名称
//					             {name : 'level',sortable : false},		//代理商等级
//					             {name : 'parentName',sortable : false},		//上级代理商
					             {name : 'start_date',sortable : false},	//生效日期
					             {name : 'end_date',sortable : false},		//结束日期
								//          { name: 'suQq', sortable: false },
								//          { name: 'suEmail', sortable: false },
								// { name: '_opera', sortable: false ,width:'80',align:'center',title : false},
					             {name : 'status',sortable : false,align: 'center',formatter: 'select', editoptions:{value:'0:禁用;1:启用'}}
					           ],
					//width: 750,
					height : 'auto',
					multiselect:true,//多选框
					multiboxonly:true,//为true时是单选
					autowidth : true,
					shrinkToFit : true,
					hidegrid : false, //隐藏表格右上角的"展开/收缩jqGrid内容的小箭头"
					autoScroll : false,
					rowNum : myRowNum,
					rowList : myRowList,
					viewrecords : true,//显示总记录数
					rownumbers : true,
					pager : myJqPageId
				});
			}else{
				var t1 = $('<table></table>');
				t1.attr('id', myTbId);
				$(myJqTbContentId).append(t1);

				var p1 = $('<div></div>');
				p1.attr('id', myPageId);

				p1.insertAfter(t1);

				$(myJqTbId).jqGrid({
					url : basePath + 'agent',
					mtype : "GET",
					datatype : "json",
					caption : "代理商列表<input type=\"button\" value=\"显示/隐藏 列\" onclick=\"showColumnDialog()\" />", //设置表标题
					page : 1,
					colNames : [ 'id', '用户名称', '接口编号', '代理商名称', '余额', '授信额', '利润', '话费通道组', '话费折扣模板', '话费通道组', '话费折扣模板', '代理商等级','上级代理商','渠道人员', '生效日期', '截止日期', '状态' ],
					colModel : [ {name : 'id',key : true,sortable : false,hidden : true}, 
					             {name : 'suName',sortable : false},	//用户名称
					             {name : 'name',sortable : false}, 		//代理商名称
					             {name : 'nickname',sortable : false}, 		//代理商名称
					             {name : 'balance',sortable : false}, 	//余额
					             {name : 'credit',sortable : false}, 	//授信额
					             {name : 'profit',sortable : false}, 	//利润
					             {name : 'billGroupName',sortable : false},	//话费通道组
					             {name : 'billDiscountModelName',sortable : false},	//话费折扣模板名称
					             {name : 'dataGroupName',sortable : false},	//话费通道组
					             {name : 'dataDiscountModelName',sortable : false},	//话费折扣模板名称
					             {name : 'level',sortable : false},		//代理商等级
					             {name : 'parentName',sortable : false},		//上级代理商
					             {name : 'channelPerson',sortable : false},		//上级代理商
					             {name : 'start_date',sortable : false},	//生效日期
					             {name : 'end_date',sortable : false},		//结束日期
								//          { name: 'suQq', sortable: false },
								//          { name: 'suEmail', sortable: false },
								// { name: '_opera', sortable: false ,width:'80',align:'center',title : false},
					             {name : 'status',sortable : false,align: 'center',formatter: 'select', editoptions:{value:'0:禁用;1:启用'}}
					           ],
					//width: 750,
					height : 'auto',
					multiselect:true,//多选框
					multiboxonly:true,//为true时是单选
					autowidth : true,
					shrinkToFit : true,
					hidegrid : false, //隐藏表格右上角的"展开/收缩jqGrid内容的小箭头"
					autoScroll : false,
					rowNum : myRowNum,
					rowList : myRowList,
					viewrecords : true,//显示总记录数
					rownumbers : true,
					pager : myJqPageId
				});
			}
		}
	})
	
	

	$('#name').focus();
	setTimeout(function() {
		$('.wrapper-content').removeClass('animated fadeInRight');
	}, 700);
	
	message();
	
	  setMyActive(5,1); //设置激活页
});

// 跳转到代理商添加页面
function add() {
	var userId = $('#leftMenuDiv').attr('data-suId');
	location.href = basePath + "agentAddPage/"+userId;
}

//跳转到代理商详情页面
function detail(id) {
	var myBasePath = basePath + 'agentDetail/' + id;
	location.href = myBasePath;
}

function detailEx(id) {
	var id = $(myJqTbId).jqGrid('getGridParam', 'selrow');
	if (id == null) {
		outMessage('warning', '没有选中的记录！', '友情提示');
	} else {
		detail(id);
	}
}

//代理商加款页面
function agentAccount(id) {
	var myBasePath = basePath + 'agentAccountEditPage/' + id;
	location.href = myBasePath;
}

function agentAccountEx() {
	var id = $(myJqTbId).jqGrid('getGridParam', 'selrow');
	if (id == null) {
		outMessage('warning', '没有选中的记录！', '友情提示');
	} else {
		agentAccount(id);
	}
}

//跳转到代理商密钥设置页面
function setAppKey(id) {
	var myBasePath = basePath + 'agentKeyEditPage/' + id;
	location.href = myBasePath;
}

function setAppKeyEx() {
	var id = $(myJqTbId).jqGrid('getGridParam', 'selrow');
	if (id == null) {
		outMessage('warning', '没有选中的记录！', '友情提示');
	} else {
		var suId = $('#leftMenuDiv').attr('data-suId');
		$.ajax({
			type:'get',
			url :basePath+'getAgentRoleFlag/'+suId+'/'+id,
			data:{},
			success:function (dt){
				var roleFlag = dt["roleFlag"];
				var queryFlag = dt["queryFlag"];
				if(roleFlag){
					if(queryFlag){
						setAppKey(id);
					}else{
						outMessage('warning','非本用户创建，无法修改！','友情提示');
					}
				}else{
					setAppKey(id);
				}
		    },
		    dataType: 'json'
		});
		
	}
}

function bill() {
	var id = $(myJqTbId).jqGrid('getGridParam', 'selrow');
	if (id == null) {
		outMessage('warning', '没有选中的记录！', '友情提示');
	} else {
		var suId = $('#leftMenuDiv').attr('data-suId');
		$.ajax({
			type:'get',
			url :basePath+'getAgentRoleFlag/'+suId+'/'+id,
			data:{},
			success:function (dt){
				var roleFlag = dt["roleFlag"];
				var queryFlag = dt["queryFlag"];
				if(roleFlag){
					if(queryFlag){
						billDiscount(id);
					}else{
						outMessage('warning','非本用户创建，无法修改！','友情提示');
					}
				}else{
					billDiscount(id);
				}
		    },
		    dataType: 'json'
		});
		
	}
}

function data() {
	var id = $(myJqTbId).jqGrid('getGridParam', 'selrow');
	if (id == null) {
		outMessage('warning', '没有选中的记录！', '友情提示');
	} else {
		var suId = $('#leftMenuDiv').attr('data-suId');
		$.ajax({
			type:'get',
			url :basePath+'getAgentRoleFlag/'+suId+'/'+id,
			data:{},
			success:function (dt){
				var roleFlag = dt["roleFlag"];
				var queryFlag = dt["queryFlag"];
				if(roleFlag){
					if(queryFlag){
						dataDiscount(id);
					}else{
						outMessage('warning','非本用户创建，无法修改！','友情提示');
					}
				}else{
					dataDiscount(id);
				}
		    },
		    dataType: 'json'
		});
		
	}
}
//start---设置代理商通道
function settingAgentPreferentialChannel(id){
	var myBasePath = basePath + 'agentChannelRelListPage/' + id;
	location.href = myBasePath;
}

function agentPreferentialChannel() {
	var id = $(myJqTbId).jqGrid('getGridParam', 'selrow');
	if (id == null) {
		outMessage('warning', '没有选中的记录！', '友情提示');
	} else {
		settingAgentPreferentialChannel(id);
	}
}

//end


function billDiscount(id) {
	var myBasePath = basePath + 'agentBillDiscountListPage/' + id;
	location.href = myBasePath;
}

function dataDiscount(id) {
	var myBasePath = basePath + 'agentDataDiscountListPage/' + id;
	location.href = myBasePath;
}

//跳转到代理商编辑页面
function edit(id) {
	var userId = $('#leftMenuDiv').attr('data-suId');
	var myBasePath = basePath + 'agentEditPage/' + id+'/'+userId;
	location.href = myBasePath;
}

function editEx() {
	var id = $(myJqTbId).jqGrid('getGridParam', 'selrow');
	if (id == null) {
		outMessage('warning', '没有选中的记录！', '友情提示');
	} else{
		var suId = $('#leftMenuDiv').attr('data-suId');
		$.ajax({
			type:'get',
			url :basePath+'getAgentRoleFlag/'+suId+'/'+id,
			data:{},
			success:function (dt){
				var roleFlag = dt["roleFlag"];
				var queryFlag = dt["queryFlag"];
				if(roleFlag){
					if(queryFlag){
						edit(id);
					}else{
						outMessage('warning','非本用户创建，无法修改！','友情提示');
					}
				}else{
					edit(id);
				}
		    },
		    dataType: 'json'
		});
		
	}
}

//跳转到代理商删除页面
function del(id) {
	var delFlag = confirm("确认要删除么？");
	if (delFlag) {
		var myDelUrl = basePath + 'agent/' + id;
		$.ajax({
			type : 'DELETE',
			url : myDelUrl,
			data : {
				id : id
			},
			success : function(dt) {
				location.href = basePath + dt;
			},
			dataType : 'html'
		});
	}
}

function delEx() {
	var id = $(myJqTbId).jqGrid('getGridParam', 'selrow');
	if (id == null) {
		outMessage('warning', '没有选中的记录！', '友情提示');
	} else {
		var suId = $('#leftMenuDiv').attr('data-suId');
		$.ajax({
			type:'get',
			url :basePath+'getAgentRoleFlag/'+suId+'/'+id,
			data:{},
			success:function (dt){
				var roleFlag = dt["roleFlag"];
				var queryFlag = dt["queryFlag"];
				if(roleFlag){
					if(queryFlag){
						del(id);
					}else{
						outMessage('warning','非本用户创建，无法修改！','友情提示');
					}
				}else{
					del(id);
				}
		    },
		    dataType: 'json'
		});
		
		
	}
}

//根据条件查询代理商信息
function search() {
	var name = $('#name').val();
	var nickname = $('#nickname').val();
	var agentParentId = $('#agentParentId').val();

	$(myJqTbId).jqGrid('setGridParam', {
		url : basePath + 'agent',
		postData : {
			'name' : name,
			'nickname' :nickname,
			'agentParentId' :agentParentId
		}, //发送话费
		page : 1
	}).trigger("reloadGrid"); //重新载入
}


//生成密钥
function alertAndAddKey() {
	var id = $(myJqTbId).jqGrid('getGridParam', 'selrow');
	if (id == null) {
		outMessage('warning', '没有选中的记录！', '友情提示');
	} else {
		$.get(basePath + 'validate', {id:id}, function (data, textStatus){
			if(data == 0){
				outMessage('warning', '已存在，不可更改！', '友情提示');
			}else{
				layer.open({
					title: '私钥为'
					 ,content: data
				});
			}
		});
	}
}

//为代理商设定限额
function balance(){
	var id = $(myJqTbId).jqGrid('getGridParam', 'selrow');
	location.href=basePath+"/addBalance/"+id;
}

//利润加款
function profitsPlusMoney() {
	var myBasePath = basePath + 'agentProfitEditPage';
	location.href = myBasePath;
}