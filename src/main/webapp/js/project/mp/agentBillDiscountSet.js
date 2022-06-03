var myTbContentId = 'mytbc1';
var myJqTbContentId = '#' + myTbContentId;
var myTbId = 'myt1';
var myJqTbId = '#' + myTbId;
var myPageId = 'myp1';
var myJqPageId = '#' + myPageId;

$(function() {
//	alert($("#colNamesH").html());
	getAgentId();
	agentIdSource();
	message();
});

/* 获取代理商列表 */
function getAgentId() {
	var dataGroupId = $("#agentId");
	var myDelUrl = basePath + 'agentDiscountList';
	// var option = $("<option>").text('无').val('');
	// dataGroupId.append(option);
	$.ajax({
		type : 'get',
		url : myDelUrl,
		success : function(dt) {
			$.each(dt, function(index, val) {
				var option = $("<option>").text(val.name).val(val.id)
				dataGroupId.append(option);
			});
			setSelectStyle(dataGroupId);
			getProviderId();
		},
		dataType : 'json'
	});
}

/* 获取复制折扣模板的代理商列表 */
function agentIdSource() {
	var agentIdSource = $("#agentIdSource");
	var myDelUrl = basePath + 'agentDiscountList';
	// var option = $("<option>").text('无').val('');
	// dataGroupId.append(option);
	$.ajax({
		type : 'get',
		url : myDelUrl,
		success : function(dt) {
			$.each(dt, function(index, val) {
				var option = $("<option>").text(val.name).val(val.id)
				agentIdSource.append(option);
			});
			setSelectStyle(agentIdSource);
		},
		dataType : 'json'
	});
}

/* 获取话费通道组列表 */
function getProviderId() {
	var dataGroupId = $("#providerId");
	var myDelUrl = basePath + 'agentProviderList';
	// var option = $("<option>").text('无').val('');
	// dataGroupId.append(option);
	$.ajax({
		type : 'get',
		url : myDelUrl,
		success : function(dt) {
			$.each(dt, function(index, val) {
				var option = $("<option>").text(val.name).val(val.id)
				dataGroupId.append(option);
			});
			setSelectStyle(dataGroupId);
			searcher();
		},
		dataType : 'json'
	});
}

function searcher() {
	var agentId = $('#agentId').val();
	var providerId = $('#providerId').val();
	$(myJqTbContentId).empty();
	if (providerId == "0000000001" || providerId == "0000000002"
		|| providerId == "0000000003") {
		$("#uploadButton").show();
		$("#discountDiv").hide();
		var colNameList = "";
		var colModelList = "";
		var id = $('#selectId').attr('data-id');
		var t1 = $('<table></table>');
		t1.attr('id', myTbId);
		$(myJqTbContentId).append(t1);

		var p1 = $('<div></div>');
		p1.attr('id', myPageId);

		p1.insertAfter(t1);

		$.ajax({
			type : 'get',
			url : basePath + 'getColModel/' + agentId + '/'+ providerId,
			async: false,
			dataType : 'json',
			success : function(data) {
				colNameList = data.colNameList;
				colModelList = data.colModelList;
			}
		});
		
		if(colNameList.length > 1){
			$(myJqTbId).jqGrid(
					{
						url : basePath + 'agentBillDiscountList/' + agentId + '/'
								+ providerId,
						mtype : "GET",
						datatype : "json",
						caption : "话费包列表", // 设置表标题
						page : 1,
						colNames :colNameList,
						colModel : colModelList,
						width : 750,
						height : 'auto',
						autowidth : true,
						shrinkToFit : false,
						hidegrid : false, // 隐藏表格右上角的"展开/收缩jqGrid内容的小箭头"
						autoScroll : false,
						cellEdit:true,//是否开启单元格的编辑功能  
						cellsubmit:'remote',//or 'clientArray',remote代表每次编辑提交后进行服务器保存，clientArray只保存到数据表格不保存到服务器  
						cellurl:basePath +'editCellDiscount',//cellsubmit要提交的后台路径  
						beforeSubmitCell : function(rowid,celname,value,iRow,iCol) {
							var province = $(myJqTbId).jqGrid("getCell",rowid,"provinceCode");
							return {'agentId':agentId,'providerId':providerId,'province':province}
						},
						rowNum : myRowNum,
						rowList : myRowList,
						viewrecords : true,// 显示总记录数
						rownumbers : true,
						pager : myJqPageId

					});
			// 合并表头单元格
			$(myJqTbId).jqGrid('setGroupHeaders', {
				useColSpanStyle : true,
				groupHeaders : [ {
					startColumnName : 'fee10',
					numberOfColumns : colModelList.length,
					titleText : '<div align="center">单位(元)</div>'
				} ]
			})
		}
	} else {
		$("#uploadButton").hide();
		$("#discountDiv").show();
//		$("#virtualProviderDiscount").val("");
//		$.ajax({
//			type : 'get',
//			url : basePath + "selectVpd",
//			data:{
//				agentId :agentId,
//				providerId : providerId
//			},
//			success : function(dt) {
//				if(dt != 0){
//					$("#virtualProviderDiscount").val(dt);
//				}
//			}
//		});
		initBillPkg();
	}
	// $('#name').focus();
	setTimeout(function() {
		$('.wrapper-content').removeClass('animated fadeInRight');
	}, 700);
	setMyActive(5, 6); // 设置激活页
}

function uploadProviderPkg() {
	$('#commitBox').val('');
	$('#commitBox').click();
}
function commitBoxChange() {
	var agentId = $('#agentId').val();
	var providerId = $('#providerId').val();
	if ("" == agentId) {
		outMessage('warning', '存在未选择的项！', '友情提示');
	} else {
		outMessage('info', '正在导入,请耐心等候！', '友情提示');
		$("#depEditForm").ajaxSubmit({
			url : basePath + "commitAgentBillDiscount/" + agentId,
			dataType : 'json',
			success : function(data) {
				if (data.length <= 0) {
					swal({
						title : "成功!",
						type : "success",
						confirmButtonText : "ok"
					}, function() {
						searcher();
					});
				} else {
					var errorMsg = "";
					for (var i = 0; i < data.length; i++) {
						errorMsg += data[i] + "<br/>";
					}
					outMessage('warning', errorMsg, '友情提示');
					searcher();
				}
			}
		})
	}

}

function downLoadTemp() {
	$('#downloadFrame').attr('src', basePath + "downloadFiles/代理商折扣模板.xlsx");
}

function submitVpd(){
	// 获取所有有效折扣信息
	var boolean = getBillPkgDiscount();
	if (boolean) {
		var discountInfoAarry = $("#virtualProviderDiscount").val();
		if(null != discountInfoAarry && "" != discountInfoAarry){
			setAgentBillPkgDiscount(discountInfoAarry);
		}else{
			layer.confirm('没有设置任何折扣,此操作将清空所有折扣信息,确认进行此操作吗?', {skin: 'layui-layer-molv',icon: 3, title:'提示'}, function(index){
				setAgentBillPkgDiscount(discountInfoAarry);
				layer.closeAll();
			});
		}
	} else {
		outMessage('error', "不能有小于或等于0的折扣,请修改后重新提交", '友情提示');
	}
}

/*设置代理商话费包折扣*/
function setAgentBillPkgDiscount(discountInfoAarry){
	var agentId = $('#agentId').val();
	var providerId = $('#providerId').val();
	$.ajax({
		type : 'get',
		url : basePath + "saveVpd",
		data:{
			agentId :agentId,
			providerId : providerId,
			discountInfoAarry : discountInfoAarry
		},
		success : function(dt) {
			var data = eval("("+dt+")");
			if (data.state == true) {
				outMessage('success', data.msg, '友情提示');
			} else {
				outMessage('error', data.msg, '友情提示');
			}
		}
	});
}

//初始话运营商拥有的话费包
function initBillPkg(){
	var providerId = $('#providerId').val();
	$('#discountDiv').html('');
	$.ajax({
		type:'GET',
		url:basePath+'providerBillPkg',
		async:false,
		dataType:'json',
		data:{
			providerId:providerId
		},
		success:function(data){
			var myDiscountDiv='';
			if (data == "") {
				myDiscountDiv +='<div class="row form-group">'+
								'	<div class="col-md-12">'+
								'		<div class="form-group">'+
								'			<div class="text-center">'+
								'				<span style="color:red">还未配置话费包,请先配置话费包后在进行此操作</span>'+
								'			</div>'+
								'		</div>'+
								'	</div>'+
								'</div>';
			} else {
				$.each(data,function (index,el){
					if (index == 0 || index%4 == 0) {
						myDiscountDiv +='<div class="row form-group">';
					}
					myDiscountDiv += '<div class="col-md-3">'+
									'	<div class="form-group">'+
									'		<label class="col-md-4 control-label" style="margin-right: -50px;">'+el.province_code + '<br/>'+ el.name +'折扣</label>'+
									'		<div class="col-md-8">'+
									'			<input id="'+el.province_code+'-'+el.id+'" name = "billPkgDiscout" type="number" min="0.000" step="0.001" placeholder="请输入' + el.province_code + el.name + '折扣" class="form-control">'+
									'		</div>'+
									'	</div>'+
									'</div>';
					if ((index != 0 && (index+1)%4 == 0) || data.length-1 == index) {
						myDiscountDiv +='</div>';
					}
				});
			}
			if (data != "") {
				myDiscountDiv +='<div class="row form-group">'+
								'	<div class="col-md-12">'+
								'		<div class="form-group">'+
								'			<div class="text-center">'+
								'				<button type="button" class="btn btn-primary" onclick="submitVpd()">提交</button>'+
								'			</div>'+
								'		</div>'+
								'	</div>'+
								'</div>';
			}
			$('#discountDiv').append(myDiscountDiv);
			initBillPkgDiscount();
		}
	});
}

/*初始化代理商话费包折扣*/
function initBillPkgDiscount(){
	var agentId = $('#agentId').val();
	var providerId = $('#providerId').val();
	$.ajax({
		type : 'get',
		url : basePath + "agentBillDiscountAll",
		dataType:'json',
		data:{
			agentId :agentId,
			providerId : providerId
		},
		success : function(dt) {
			if (null != dt) {
				$.each(dt,function (index,el){
					$("#"+el.province_code + '-' + el.bill_pkg_id).val(el.discount);
				});
			}
		}
	});
}

/*获取所有有效折扣信息*/
function getBillPkgDiscount(){
	var virtualProviderDiscount = "";
	var value = "";
	var boolean = true;
	$("#discountDiv input[type='number']").each(function(){
		value = $.trim($(this).val());
		if (value != "") {
			virtualProviderDiscount += $(this).attr("id")+"-"+$(this).val()+",";
			if (value < 0 || value == 0) {
				boolean = false;
			}
		}
	});
	virtualProviderDiscount = virtualProviderDiscount.substring(0,virtualProviderDiscount.lastIndexOf(","));
	$("#virtualProviderDiscount").val(virtualProviderDiscount);
	return boolean;
}

// 复制折扣
function copyBillPkgDiscout(){
	var agentId = $('#agentId').val();
	var agentIdSource = $('#agentIdSource').val();
	
	var agentName = $("#agentId").find("option:selected").text();
	var agentIdSourceName = $("#agentIdSource").find("option:selected").text();
	
	if (null == agentId && agentId == "") {
		outMessage('error', "目标代理商为空,请选择目标代理商", '友情提示');
		return false;
	}
	if (null == agentIdSource && agentIdSource == "") {
		outMessage('error', "折扣来源代理商为空,请选择折扣来源代理商", '友情提示');
		return false;
	}
	
	var myDelUrl = basePath + 'agentBillDiscountCopy';
	layer.confirm('确认要将代理商['+agentIdSourceName+']的折扣复制给['+agentName+']吗?', {skin: 'layui-layer-molv',icon: 3, title:'提示'}, function(index){
		$.ajax({
			type : 'post',
			url : myDelUrl,
			data :{
				agentId :agentId,
				agentIdSource : agentIdSource
			},
			success : function(dt) {
				if (dt.state == true) {
					window.location.reload();
				} else {
					layer.closeAll();
					outMessage('error', dt.msg, '友情提示');
				}
			},
			dataType : 'json'
		});
		layer.closeAll();
		layer.msg('请稍后,数据正在处理中...', { icon: 16, shade: [0.5], scrollbar: false,time: 3600000});
	});
}