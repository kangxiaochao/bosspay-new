/**
 * 数据不是最新的需要重新整理后再上线
 */

//TODO fixed new data

provinceCityData= [
          { name: "云南", cities: ["昆明", "丽江", "昭通", "玉溪", "临沧", "文山", "红河", "楚雄", "大理","曲靖"] },
          { name: "安徽", cities: ["合肥", "马鞍山", "蚌埠", "黄山", "芜湖", "淮南", "铜陵", "阜阳", "宣城", "安庆"] },
          { name: "贵州", cities: ["贵阳", "遵义", "安顺", "黔西南", "都匀"] },
          { name: "四川", cities: ["成都", "达州", "南充", "乐山", "绵阳", "德阳", "内江", "遂宁", "宜宾", "巴中", "自贡", "康定", "攀枝花"] },
          { name: "浙江", cities: ["杭州", "金华", "宁波", "温州", "嘉兴", "绍兴", "丽水", "湖州", "台州", "舟山", "衢州"] },
          { name: "北京", cities: ["西城", "东城", "朝阳", "海淀", "丰台", "石景山", "门头沟", "房山", "通州", "顺义", "大兴", "昌平", "平谷", "怀柔", "密云", "延庆"] },
          { name: "重庆", cities: ["渝中", "大渡口", "江北", "沙坪坝", "九龙坡", "南岸", "北碚", "万盛", "双桥", "渝北", "巴南", "万州", "涪陵", "黔江", "长寿"] },
          { name: "上海", cities: ["浦东", "杨浦", "徐汇", "静安", "卢湾", "黄浦", "普陀", "闸北", "虹口", "长宁", "宝山", "闵行", "嘉定", "金山", "松江", "青浦", "崇明", "奉贤", "南汇"] },
          { name: "江苏", cities: ["南京", "苏州", "无锡", "常州", "扬州", "徐州", "南通", "镇江", "泰州", "淮安", "连云港", "宿迁", "盐城", "淮阴", "沐阳", "张家港"] },
          { name: "江西", cities: ["南昌", "景德镇", "上饶", "萍乡", "九江", "吉安", "宜春", "鹰潭", "新余", "赣州"] },
          { name: "山东", cities: ["青岛", "济南", "淄博", "烟台", "泰安", "临沂", "日照","莱芜", "德州", "威海", "东营", "菏泽", "济宁", "潍坊", "枣庄", "聊城","滨州"] },
          { name: "广东", cities: ["广州", "深圳", "东莞", "佛山", "珠海", "汕头", "韶关", "江门", "梅州", "揭阳", "中山", "河源", "惠州", "茂名", "湛江", "阳江", "潮州", "云浮", "汕尾", "潮阳", "肇庆", "顺德", "清远"] },
          { name: "甘肃", cities: ["兰州", "金昌", "天水", "武威", "张掖", "平凉", "酒泉"] },
          { name: "福建", cities: ["福州", "厦门", "泉州", "漳州", "南平", "龙岩", "莆田", "三明", "宁德"] },
          { name: "广西", cities: ["南宁", "桂林", "柳州", "梧州", "来宾", "贵港", "玉林", "贺州"] },
          { name: "海南", cities: ["海口", "三亚"] },
          { name: "河北", cities: ["石家庄", "秦皇岛", "廊坊", "保定", "邯郸", "唐山", "邢台", "衡水", "张家口", "承德", "沧州"] },
          { name: "河南", cities: ["郑州", "洛阳", "开封", "平顶山", "濮阳", "安阳", "许昌", "南阳", "信阳", "周口", "新乡", "焦作", "三门峡", "商丘","驻马店"] },
          { name: "黑龙江", cities: ["哈尔滨", "齐齐哈尔", "大庆", "牡丹江", "鹤岗", "佳木斯", "绥化","黑河"] },
          { name: "湖北", cities: ["武汉", "襄樊", "孝感", "十堰", "荆州", "黄石", "宜昌", "黄冈", "恩施", "鄂州", "江汉", "随枣", "荆沙", "咸宁"] },
          { name: "湖南", cities: ["长沙", "湘潭", "岳阳", "株洲", "怀化", "永州", "益阳", "张家界", "常德", "衡阳", "湘西", "邵阳", "娄底", "郴州"] },
          { name: "吉林", cities: ["长春", "吉林", "四平", "辽源", "通化", "延吉","白山", "白城", "辽源", "松原", "临江", "珲春"] },
          { name: "辽宁", cities: ["大连", "沈阳", "鞍山", "抚顺", "营口", "锦州", "丹东", "朝阳", "辽阳", "阜新", "铁岭", "盘锦", "本溪", "葫芦岛"] },
          { name: "内蒙古", cities: ["呼和浩特", "包头","乌海","赤峰", "通辽","鄂尔多斯","呼伦贝尔", "巴彦淖尔","乌兰察布","锡林郭勒", "兴安","阿拉善"] },
          { name: "宁夏", cities: ["银川","石嘴山", "吴忠","固原","中卫","灵武","青铜峡"] },
          { name: "青海", cities: ["黄南", "海南", "西宁", "海东", "海西", "海北", "果洛", "玉树"] },
          { name: "山西", cities: ["太原", "大同", "长治", "晋城","晋中", "阳泉", "朔州", "运城", "临汾","忻州","吕梁"] },
          { name: "陕西", cities: ["西安", "宝鸡", "咸阳", "铜川", "渭南", "延安", "榆林", "汉中", "安康", "商洛"] },          
          { name: "天津", cities: ["和平","河东", "河西", "南开", "河北", "红桥","东丽", "西青", "北辰", "津南", "武清", "宝坻", "静海", "宁河", "蓟州", "滨海新"] },
          { name: "西藏", cities: ["拉萨", "林芝", "日喀则", "昌都"] },
          { name: "新疆", cities: ["乌鲁木齐", "哈密", "喀什", "巴音郭楞", "昌吉", "伊犁哈萨克自治州", "阿勒泰", "克拉玛依", "博尔塔拉"] }
          ] ;
         
function initProvince(provinceSelectId){
	$('#'+provinceSelectId).empty();
	$('#'+provinceSelectId).append('<option value="">请选择</option>');
	$.each(provinceCityData,function (index,el){
		$('#'+provinceSelectId).append('<option value="'+el.name+'">'+el.name+'</option>');
	});
	
}

function initCountryProvince(provinceSelectId){
	$('#'+provinceSelectId).empty();
	$('#'+provinceSelectId).append('<option value="">请选择</option>');
	$('#'+provinceSelectId).append('<option value="全国">全国</option>');
	$.each(provinceCityData,function (index,el){
		$('#'+provinceSelectId).append('<option value="'+el.name+'">'+el.name+'</option>');
	});
	
}

function initSelectProvince(provinceSelectId,name){
//	$('#'+provinceSelectId).empty();
	$('#'+provinceSelectId).append('<option value="">请选择</option>');
	$('#'+provinceSelectId).append('<option value="全国"'+("全国"==name?' selected ':' ')+'>全国</option>');
	$.each(provinceCityData,function (index,el){
		$('#'+provinceSelectId).append('<option value="'+el.name+'" '+(el.name==name?' selected ':' ')+'>'+el.name+'</option>');
	});
	
}

function initCity(pro,csid){
  $('#'+csid).empty();
	var cityArry=getCityData(pro);
		
	$('#'+csid).append('<option value="">请选择</option>');
	$.each(cityArry,function (index,el){
		$('#'+csid).append('<option value="'+el+'">'+el+'</option>');
	});	
	
}

function getCityData(pro){
	var myCities=[];
	$.each(provinceCityData,function (index,el){
		if(pro==el.name){
			myCities=el.cities;
		}
	});
	return myCities;
}

function pcModeProc(tp){
	if(tp==1){
		$('#citySpan').hide();
	}
	if(tp==2){
		$('#citySpan').show();
	}
}

// 创建zTree所需省份信息
function initZTreeNodes(){
	var zTreeNodes = "[";
	zTreeNodes += '{"name":"全国","id":"全国","open":"true","children":[';
	$.each(provinceCityData,function (index,el){
		zTreeNodes += '{"name": "' + el.name + '",';
		zTreeNodes += '"id": "' + el.name + '",';
		zTreeNodes += '"children": ' + initZTreeCities(el.name,el.cities) + '},';
	});
	zTreeNodes = zTreeNodes.substring(0, zTreeNodes.length -1);
	zTreeNodes += "]}";
	zTreeNodes += "]";
	return zTreeNodes;
}

//创建zTree所需城市信息
function initZTreeCities(province,cities){
	var city = "[";
	$.each(cities,function (index,el){
		city += '{';
		city += '"name": "' + el + '",';
		city += '"id": "' + province +'-'+ el + '"';
		city += '},';
	});
	city = city.substring(0, city.length -1);
	city += "]";
	return city;
}