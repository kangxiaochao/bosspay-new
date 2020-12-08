var id=$('#providerId').val();
var zTree = "";

$(function(){
	$('#name').focus();
	zTree = $.fn.zTree.init($("#treeDemo"), setting, zNodes);
	checkProvince();
	initBillPkgList();
	setMyActive(6,1); //设置激活页
});

	var setting = {
		check : {
			enable : true,
			chkStyle : "checkbox",
			chkboxType :  { "Y" : "p", "N" : "s" }
		},
		data : {
			simpleData : {
				enable : true
			}
		},
		view: {
			showIcon: false
		},
		callback:{
            onClick:function zTreeOnClick(event, treeId, treeNode){
            	var checkedFlag = treeNode.checked;
                if (checkedFlag){
                	// 如果已经选中,则取消选中状态
                	setChildrenCheckedState(treeNode,false);
                } else {
                	// 如果没有选中,则设置为选中状态
                	setChildrenCheckedState(treeNode,true);
                }
            }
        }
	};

var zNodes = $.parseJSON(initZTreeNodes());

//设置选中的节点treeNode下所有的子节点的选中状态
function setChildrenCheckedState(treeNode,flag){
	// 设置被点击的节点选中状态
	treeNode.checked = flag;
	// 需要更新才能正确显示更新后的状态
	zTree.updateNode(treeNode);
	
	if (treeNode.isParent){
		for(var obj in treeNode.children){
			treeNode.children[obj].checked = flag;
			zTree.updateNode(treeNode.children[obj]);
		}
	}
}

function checkProvince(){
	var treeObj = $.fn.zTree.getZTreeObj("treeDemo");
	treeObj.checkAllNodes(false);
	$.ajax({
		type:'GET',
		url :basePath +'providerBillPkgDetail/'+id,
		success:function (data){
			$.each(data, function(index,val) {
				var initProvince = treeObj.getNodeByParam("id", val.province_code, null);
				var initCity = treeObj.getNodeByParam("id", val.province_code+'-'+val.city_code, null);
				initProvince.checked = true; 
				treeObj.updateNode(initProvince); 
				if(initCity!=null){
					initCity.checked = true; 
					treeObj.updateNode(initCity); 
				}
			});
	    },
	    dataType: 'json'
	});
}

function saveProvince(){
	var treeObj = $.fn.zTree.getZTreeObj("treeDemo");
	var nodes = treeObj.getCheckedNodes(true);
	var meg='';
	if(nodes.length>0){
		for(var i=0;i<nodes.length;i++){
			var m =nodes[i].id;
			meg+=m+',';
		}
		meg = meg.substr(0, meg.length-1);
		//meg = meg.replace("全国,", "");	//去掉全国
	}
	$('#provinceMeg').val(meg);
	if(meg == ""){
		outMessage('error', "请选择地区", '友情提示');
		return false;
	}
	if(getCheckedBillPkg() == ""){
		outMessage('error', "请选择话费包", '友情提示');
		return false;
	};
}

//加载单选按钮providerId的样式
function initCheckBoxStyle(){  
	$('input[type=checkbox]').iCheck({
		checkboxClass: 'icheckbox_square-green',
		radioClass: 'iradio_square-green',
	});
}

//初始化话费包
function initBillPkgList(){
	$.ajax({
		type:'GET',
		url:basePath+'billPkgList',
		async:false,
		dataType:'json',
		success:function(data){
			$.each(data,function (index,el){
				var myDiscountDiv='';
				myDiscountDiv+='<div class="col-sm-3 form-group ">';
				if(index < 8){
					myDiscountDiv += '	<label class="col-sm-8 control-label" style="padding-left: 0px;padding-right: 0px;"> 0'+(index+1)+'.';
					myDiscountDiv += '	<input class="i-checks" type="checkbox" id="'+el.id+'" name="billPkgId" value="'+el.id+'" checked />'+el.name+'</label>';
				}else{
					myDiscountDiv += '	<label class="col-sm-8 control-label" style="padding-left: 0px;padding-right: 0px;"> '+(index+1)+'.';
					myDiscountDiv += '	<input class="i-checks" type="checkbox" id="'+el.id+'" name="billPkgId" value="'+el.id+'" checked />'+el.name+'</label>';
				}
				myDiscountDiv+='</div>';
				$('#billPkg').append(myDiscountDiv);
			});
		}
	});
	initCheckBillPkg();
	initRadioStyle();
}

//初始已经拥有的话费包
function initCheckBillPkg(){
	$.ajax({
		type:'GET',
		url:basePath+'providerBillPkgByProviderId',
		async:false,
		dataType:'json',
		data:{
			providerId:$("#providerId").val()
		},
		success:function(data){
			if(null != data && ""!= data){
				// 将复选框组设置为不选中
				$("input:checkbox[name='billPkgId']").iCheck('uncheck');
				$.each(data,function (index,el){
					// 设置复选框选中
					$("#"+el.bill_pkg_id).iCheck('check');
				});
			}else {
                $("input:checkbox[name='billPkgId']").iCheck('uncheck');
			}
		}
	});
}

// 获取选中的话费包
function getCheckedBillPkg(){
	var discountValue = "";
	var billPkgIdes = "";
	$('#billPkg input[name="billPkgId"]:checked').each(function(){//遍历每一个name为billPkgId的被选中的复选框，其中选中的执行函数      
		discountValue = $(this).val();
		if (discountValue !=  '') {
			billPkgIdes += discountValue + ",";
		}
	});
	billPkgIdes = billPkgIdes.substr(0, billPkgIdes.length-1);
	
	return billPkgIdes;
}
