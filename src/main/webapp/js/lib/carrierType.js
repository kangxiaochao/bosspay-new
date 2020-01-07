/**
 * 数据不是最新的需要重新整理后再上线
 */

//TODO fixed new data

carrierTypeData= 
	[
		{ key:"1",name: "中国移动"},
		{ key:"2",name: "中国联通"},
		{ key:"3",name: "中国电信"},
		{ key:"4",name: "虚拟运营商 移动"},
		{ key:"5",name: "虚拟运营商 联通"},
		{ key:"6",name: "虚拟运营商 电信"},
		{ key:"7",name: "物联网卡"}
	] ;

// 添加运营商类型下拉列表
function initCarrierType(carrierTypeSelectId){
	$('#'+carrierTypeSelectId).empty();
	$.each(carrierTypeData,function (index,el){
		$('#'+carrierTypeSelectId).append('<option value="'+el.key+'">'+el.name+'</option>');
	});
}

//将对应的运营商类型key转换成name
function carrierTypeFormat(cellvalue, options, rowObject){
	var carrierTypeName;
	$.each(carrierTypeData,function (index,el){
		if(el.key == cellvalue){
			carrierTypeName  = el.name;
		}
	});
	return carrierTypeName;
}
