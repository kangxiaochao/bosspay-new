  var myTbContentId='mytbc1';
  var myJqTbContentId='#'+myTbContentId;
  var myTbId='myt1';
  var myJqTbId='#'+myTbId;
  var myPageId='myp1';
  var myJqPageId='#'+myPageId;
  
  getPhysicalId();
  /*获取话费通道组列表*/
  function getPhysicalId(){
  	var dataGroupId = $("#physicalId");
  	var myDelUrl = basePath + 'physicalList';
//  	var option = $("<option>").text('无').val('');
//  	dataGroupId.append(option);
  	$.ajax({
  		type:'get',
  		url :myDelUrl,
  		success:function (dt){
  			$.each(dt, function(index,val) {  
  				var option = $("<option>").text(val.name        ).val(val.id)
  				dataGroupId.append(option);
  			});
  			setSelectStyle(dataGroupId);
  			getProviderId();
  		},
  		dataType: 'json'
  	});
  }
  
  /*获取话费通道组列表*/
  function getProviderId(){
  	var dataGroupId = $("#providerId");
  	var myDelUrl = basePath + 'agentProviderList';
//  	var option = $("<option>").text('无').val('');
//  	dataGroupId.append(option);
  	$.ajax({
  		type:'get',
  		url :myDelUrl,
  		success:function (dt){
  			$.each(dt, function(index,val) {  
  				var option = $("<option>").text(val.name        ).val(val.id)
  				dataGroupId.append(option);
  			});
  			setSelectStyle(dataGroupId);
  			searchList()
  		},
  		dataType: 'json'
  	});
  }
  
  function searchList(){
	  var physicalId=$('#physicalId').val();
	  var providerId=$('#providerId').val();
	  $(myJqTbContentId).empty();
	  if (providerId == "0000000001" || providerId == "0000000002"
			|| providerId == "0000000003") {
		  $("#uploadButton").show();
		  $("#discountDiv").hide();
		  $("#realDiscountDiv").hide();
		  var id = $('#selectId').attr('data-id');
			var t1 = $('<table></table>');
			t1.attr('id', myTbId);
			$(myJqTbContentId).append(t1);

			var p1 = $('<div></div>');
			p1.attr('id', myPageId);

			p1.insertAfter(t1);

			$.ajax({
				type : 'get',
				url : basePath + 'getColModels/' + physicalId + '/'+ providerId,
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
							url : basePath + 'providerBillDiscountList/'+physicalId+'/'
									+ providerId,
							mtype : "GET",
							datatype : "json",
							caption : "话费折扣列表", // 设置表标题
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
							cellurl:basePath +'editCellDiscounts',//cellsubmit要提交的后台路径  
							beforeSubmitCell : function(rowid,celname,value,iRow,iCol) {
								var province = $(myJqTbId).jqGrid("getCell",rowid,"provinceCode");
								return {'physicalId':physicalId,'providerId':providerId,'province':province}
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
	  }else{
		  $("#virtualProviderDiscount").val("");
		  $("#virtualProviderRealDiscount").val("");
		  $("#uploadButton").hide();
		  $("#discountDiv").show();
		  $("#realDiscountDiv").show();
		  /*$.ajax({
				type : 'get',
				url : basePath + "selectChannelVpd",
				data:{
					physicalId : physicalId,
					providerId : providerId
				},
				dataType:"json",
				success : function(dt) {
					if(dt.discount != null){
						$("#virtualProviderDiscount").val(dt.discount);
					}
					if(dt.realDiscount != null){
						$("#virtualProviderRealDiscount").val(dt.realDiscount);
					}
				}
			});*/
		  initBillPkg();
	  }
	  
	//  $('#name').focus();
	  setTimeout(function(){
	      $('.wrapper-content').removeClass('animated fadeInRight');
	  },700);
	  setMyActive(6,4); //设置激活页
  }
  
$(function () {
  
});

function uploadProviderPkg(){
	$('#commitBox').val('');
	$('#commitBox').click();
}
function commitBoxChange(){
	var physicalId=$('#physicalId').val();
//	var providerId=$('#providerId').val();
	if(""==physicalId){
		outMessage('warning','存在未选择的项！','友情提示');
	}else{
		outMessage('info','正在导入,请耐心等候！','友情提示');
		$("#depEditForm").ajaxSubmit({
	        url: basePath + "commitProviderBillDiscount/"+physicalId,
	        dataType: 'json',
	        success: function (data) {
	        	if(data.length<=0){
	        		swal({
	        	        title: "成功!",
	        	        type: "success",
	        	        confirmButtonText: "ok"
	        	    },function(){
	        	    	search();
	        	    });
	        	}else{
	        		var errorMsg="";
	        		for(var i=0;i<data.length;i++){
	        			errorMsg+=data[i]+"<br/>";
	        		}
	        		outMessage('warning',errorMsg,'友情提示');
	        	}
	        }
		})
	}
	
}

function downLoadTemp(){
	$('#downloadFrame').attr('src',basePath + "downloadFiles/物理通道折扣模板.xlsx");
}

function submitChannelVpd(){
	// 获取所有有效折扣信息
	var boolean = getBillPkgDiscount();
	if (boolean) {
		var discount = $("#virtualProviderDiscount").val();
		if("" != discount){
			setProviderBillPkgDiscount(discount);
		}else{
			layer.confirm('没有设置任何折扣,此操作将清空所有折扣信息,确认进行此操作吗?', {skin: 'layui-layer-molv',icon: 3, title:'提示'}, function(index){
				setProviderBillPkgDiscount(discount);
				layer.closeAll();
			});
		}	
	} else {
		outMessage('error', "扣款折扣与返利折扣必须要匹配填写,且不能有小于或等于0的折扣,请修改后重新提交", '友情提示');
	}
}

/*设置通道话费包折扣*/
function setProviderBillPkgDiscount(discount){
	var physicalId=$('#physicalId').val();
	var providerId = $('#providerId').val();
	$.ajax({
		type : 'post',
		url : basePath + "saveChannelVpd",
		data:{
			physicalId :physicalId,
			providerId : providerId,
			discount : discount,
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
	$('#realDiscountDiv').html('');
	$.ajax({
		type:'GET',
		url:basePath+'providerBillPkg',
		async:false,
		dataType:'json',
		data:{
			providerId:providerId
		},
		success:function(data){
			var myDiscountDiv='<fieldset><legend>扣款折扣设置</legend>';
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
									'		<label class="col-md-4 control-label" style="margin-right: -50px;">'+el.province_code + el.name +'<br/>扣款折扣</label>'+
									'		<div class="col-md-8">'+
									'			<input id="'+el.province_code+'-'+el.id+'-ptzk" name = "billPkgDiscout" type="number" min="0.000" step="0.001" placeholder="请输入' + el.province_code + el.name + '扣款折扣" class="form-control">'+
									'		</div>'+
									'	</div>'+
									'</div>';
					if ((index != 0 && (index+1)%4 == 0) || data.length-1 == index) {
						myDiscountDiv +='</div>';
					}
				});
			}
			$('#discountDiv').append(myDiscountDiv);
			
			var reg = new RegExp("扣款","g");//g,表示全部替换。
			myDiscountDiv = myDiscountDiv.replace(reg,'返利');
			reg = new RegExp("billPkgDiscout","g")
			myDiscountDiv = myDiscountDiv.replace(reg,'billPkgRealDiscout');
			reg = new RegExp("ptzk","g")
			myDiscountDiv = myDiscountDiv.replace(reg,'flzk');
			
			if (data != "") {
				myDiscountDiv +='	<div class="row form-group">'+
								'		<div class="col-md-12">'+
								'			<div class="form-group">'+
								'				<div class="text-center">'+
								'					<button type="button" class="btn btn-primary" onclick="submitChannelVpd()">提交</button>'+
								'				</div>'+
								'			</div>'+
								'		</div>'+
								'	</div>'+
								'</fieldset>';
			}
			$('#realDiscountDiv').append(myDiscountDiv);
			initProviderBillPkgDiscount();
		}
	});
}

/*初始化代理商话费包折扣*/
function initProviderBillPkgDiscount(){
	var physicalId=$('#physicalId').val();
	var providerId = $('#providerId').val();
	$.ajax({
		type : 'get',
		url : basePath + "providerBillDiscountInfo",
		dataType:'json',
		data:{
			physicalId :physicalId,
			providerId : providerId
		},
		success : function(dt) {
			if (null != dt) {
				$.each(dt,function (index,el){
					$("#"+el.province_code + '-' + el.bill_pkg_id + '-ptzk').val(el.discount);
					$("#"+el.province_code + '-' + el.bill_pkg_id + '-flzk').val(el.real_discount);
				});
			}
		}
	});
}

/*获取所有有效折扣信息*/
function getBillPkgDiscount(){
	var billPkgDiscountAll = "";
	var value = "";
	var realValue = "";
	var boolean = true;
	var id = "";
	$("#discountDiv input[name='billPkgDiscout']").each(function(){
		id = $(this).attr("id").replace("-ptzk",'');
		value = $.trim($(this).val());	//扣款折扣
		realValue = $.trim($("#"+id+"-flzk").val()); // 普通折扣
		if (value != "" || realValue != "") {
			// 此处的"-temp"没有实际意义,仅仅是为了后台转换成数组时其长度保持统一,便于数据处理
			billPkgDiscountAll += id+"-"+value+"-"+realValue+"-temp,";
			if (value < 0.0000001 || realValue < 0.0000001) {
				boolean = false;
			}
		}
	});
	billPkgDiscountAll = billPkgDiscountAll.substring(0,billPkgDiscountAll.lastIndexOf(","));
	$("#virtualProviderDiscount").val(billPkgDiscountAll);
	return boolean;
}