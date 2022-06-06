var myTbContentId='mytbc1';
var myJqTbContentId='#'+myTbContentId;
var myTbId='myt1';
var myJqTbId='#'+myTbId;
var myPageId='myp1';
var myJqPageId='#'+myPageId;
$(function(){
	var roleFlag;
	$("#startDate").val(laydate.now() + ' 00:00:00');
	$("#endDate").val(laydate.now(1) + ' 00:00:00');
	var suId = $('#leftMenuDiv').attr('data-suId');
	$.ajax({
		type:'get',
		url :basePath+'getAgentRoleFlagBySuId/'+suId,
		async:false, 
		success:function (dt){
			roleFlag = dt["roleFlag"];
			if(roleFlag){
				$("#charts").hide();
				getAgentAccount();
				getAgentProfit();
				showAgentDiscountList()
				showAgentBillDiscountList();
			}else{
				$("#charts").hide();
// paintCharts();
				countPhysicalChannelProfit();
				countProfit();
			}
	    },
	    dataType: 'json'
	});
	setMyActive(0,1); // 设置激活页
	if(roleFlag){
		setInterval("getAgentAccount()",5000);
		setInterval("getAgentProfit()",5000);
	}
});
// function getAccount(){
// $("#startDate").val(laydate.now() + ' 00:00:00');
// $("#endDate").val(laydate.now(1) + ' 00:00:00');
// var suId = $('#leftMenuDiv').attr('data-suId');
// $.ajax({
// type:'get',
// url :basePath+'getAgentRoleFlagBySuId/'+suId,
// data:{},
// success:function (dt){
// var roleFlag = dt["roleFlag"];
// if(roleFlag){
// getAgentAccount();
// }
// },
// dataType: 'json'
// });
// setMyActive(0,1); //设置激活页
// }
function getAgentAccount(){
	var suId = $('#leftMenuDiv').attr('data-suId');
	$.ajax({
		url:basePath+"getAgentAccount/"+suId,
		type:"get",
		dataType:"json",
		success:function(data){
			$('#agentAccount').text(data)
		}
	});
}
function getAgentProfit(){
	var suId = $('#leftMenuDiv').attr('data-suId');
	$.ajax({
		url:basePath+"getAgentProfit/"+suId,
		type:"get",
		dataType:"json",
		success:function(data){
			$('#agentProfit').text(data)
		}
	});
}

// 统计利润总金额
function countProfit(){
	$.ajax({
		url:basePath+"countProfit",
		type:"get",
		dataType:"json",
		success:function(data){
				$("#todayAmount").html(data.today.amount);
				$("#todayProfit").html(data.today.profit);
				$("#todayRate").html(data.today.rate);
				$("#todaySumNum").html(data.today.sumNum);
				$("#todaySuccNum").html(data.today.succNum);
				$("#todayFailNum").html(data.today.failNum);
				$("#amount").html(data.all.amount);
				$("#profit").html(data.all.profit);
				$("#rate").html(data.all.rate);
				$("#sumNum").html(data.all.sumNum);
				$("#succNum").html(data.all.succNum);
				$("#failNum").html(data.all.failNum);
		}
	});
}
//// 统计上家利润
//function countPhysicalChannelProfit(){
//	$.ajax({
//		url:basePath+"countPhysicalChannelProfit?"+$("#dateForm").serialize(),
//		type:"get",
//		dataType:"json",
//		async:false,
//		success:function(data){
//			$("#channelProfit").empty();
//			for(var i = 0; i < data.data.length; i++ ){
//				var d = data.data[i];
//				$("#channelProfit").append("<tr>" +
//						"<td>"+d.dispatcher_provider_id+"</td>" +
//						"<td>"+d.name+"</td>" +
//						"<td><span class='pie'>"+d.num+"/"+d.sum+"</span>&nbsp;&nbsp;"+Math.round((d.num/(d.sum))*10000)/100+"%</td>" +
//						"<td>"+d.num+"</td>" +
//						"<td>"+d.num_3+"</td>" +
//						"<td class='text-navy'>"+Math.round((d.num_3/(d.num))*10000)/100+"%</td>" +
//						"<td>"+d.profit+"</td></tr>");
//			}
//			$(".pie").peity("pie",{
//				  fill: ["#4d89f9","#c6d9fd"]
//			});
//		}
//	});
//}

//统计上家利润
function countPhysicalChannelProfit(){
	$.ajax({
		url:basePath+"countPhysicalChannelProfit?"+$("#dateForm").serialize(),
		type:"get",
		dataType:"json",
		async:false,
		success:function(data){
			$("#channelProfit").empty();
			var sum = data.sum;
			for(var i = 0; i < data.data.length; i++ ){
				var d = data.data[i];
				$("#channelProfit").append("<tr>" +
						"<td>"+d.dispatcher_provider_id+"</td>" +
						"<td>"+d.name+"</td>" +
						"<td><span class='pie'>"+d.num+"/"+sum+"</span>&nbsp;&nbsp;"+Math.round((d.num/(sum))*10000)/100+"%</td>" +
						"<td>"+d.num+"</td>" +
						"<td>"+d.succnum+"</td>" +
						"<td>"+d.succfee+"</td>" +
						"<td class='text-navy'>"+Math.round((d.succnum/(d.num))*10000)/100+"%</td>" +
						"<td>"+d.profit+"</td></tr>");
			}
			$(".pie").peity("pie",{
				  fill: ["#4d89f9","#c6d9fd"]
			});
		}
	});
}
// 折线图
function paintCharts(){
	var myChart = echarts.init(document.getElementById('charts'));
	var option = {
		    tooltip : {
		        trigger: 'axis'
		    },
		    legend: {
		        data:[]
		    },
		    toolbox: {
		        show : true,
		        feature : {
		            mark : {show: false},
		            magicType : {show: true, type: ['line', 'bar', 'stack', 'tiled']},
		            restore : {show: true},
		            saveAsImage : {show: true}
		        }
		    },
		    calculable : true,
		    xAxis : [
		        {
		            type : 'category',
		            boundaryGap : false,
		            data : []
		        }
		    ],
		    yAxis : [
		        {
		            type : 'value'
		        }
		    ],
		    series : []
		};
	myChart.showLoading();
	$.ajax({
		url:basePath+"getChartsData",
		type:"GET",
		dataType:"json",
		async:false,
		success:function(data){
			 myChart.setOption({  
				 legend: {
				        data:data.agentNameData
				    },
                 xAxis:{  
                     data:data.dateData  
                 },  
                 series: data.seriesData  
             });  
		}
	});
	myChart.hideLoading();
	myChart.setOption(option);
}

/* 绑定日期控件 */
laydate({
	elem: '#startDate',// 目标元素。由于laydate.js封装了一个轻量级的选择器引擎，因此elem还允许你传入class、tag但必须按照这种方式
						// '#id .class'
	event: 'focus', 	// 响应事件。如果没有传入event，则按照默认的click
	format: 'YYYY-MM-DD hh:mm:ss', // 日期格式
	istime: true, // 是否开启时间选择
});

/* 绑定日期控件 */
laydate({
	elem: '#endDate',	// 目标元素。由于laydate.js封装了一个轻量级的选择器引擎，因此elem还允许你传入class、tag但必须按照这种方式
						// '#id .class'
	event: 'focus',		// 响应事件。如果没有传入event，则按照默认的click
	format: 'YYYY-MM-DD hh:mm:ss', // 日期格式
	istime: true, // 是否开启时间选择
});

function showAgentDiscountList(){
	var suId = $('#leftMenuDiv').attr('data-suId');
//	var myTbContentId='mytbc1';
//	  var myJqTbContentId='#'+myTbContentId;
//	  var myTbId='myt1';
//	  myJqTbId='#'+myTbId;
//	  var myPageId='myp1';
//	  var myJqPageId='#'+myPageId;
	var t1=$('<table></table>');
	  t1.attr('id',myTbId);
	  $(myJqTbContentId).append(t1);
	  
	  var p1=$('<div></div>');
	  p1.attr('id',myPageId);
	  
	  p1.insertAfter(t1);
	  
	  $(myJqTbId).jqGrid({
	      url: basePath+'queryAgentDiscountBySuId/'+suId,
// postData:{'suId':suId},
	      mtype: "GET",
	      datatype: "json",
	      caption: "话费折扣列表",	// 设置表标题
	      page: 1,
	      colNames: ['id', '运营商','地区','话费包','折扣'],
	      colModel: [
	          { name: 'id', key: true, sortable: false,hidden:true },
	          { name: 'providerId', sortable: false} ,// 查询条件要添加index
	          { name: 'provinceCode', sortable: false} ,
	          { name: 'pkgName', sortable: false} ,
	          { name: 'discount', sortable: false}
	      ],
	      // width: 750,
	      height: 'auto',
	      multiselect:true,// 多选框
	      multiboxonly:true,// 为true时是单选
	      autowidth:true,
	      shrinkToFit:true,
	      hidegrid: false,	// 隐藏表格右上角的"展开/收缩jqGrid内容的小箭头"
	      autoScroll: false,
	      rowNum : 5,
	      rowList : [5,10,15,20,30,50,100],
	      viewrecords : true,// 显示总记录数
	      rownumbers: true,
	      pager: myJqPageId
	  });
	  
	  $('#name').focus();
	  setTimeout(function(){
	      $('.wrapper-content').removeClass('animated fadeInRight');
	  },700);
// setMyActive(0,1); //设置激活页
	  
	  message();
}

function showAgentBillDiscountList(){
	var suId = $('#leftMenuDiv').attr('data-suId');
	 myTbContentId='mytbc2';
	 myJqTbContentId='#'+myTbContentId;
	 myTbId='myt2';
	 myJqTbId='#'+myTbId;
	 myPageId='myp2';
	 myJqPageId='#'+myPageId;
	var t1=$('<table></table>');
	  t1.attr('id',myTbId);
	  $(myJqTbContentId).append(t1);
	  
	  var p1=$('<div></div>');
	  p1.attr('id',myPageId);
	  
	  p1.insertAfter(t1);
	  
	  $(myJqTbId).jqGrid({
	      url: basePath+'queryAgentBillDiscountBySuId/'+suId,
// postData:{'suId':suId},
	      mtype: "GET",
	      datatype: "json",
	      caption: "话费折扣列表",	// 设置表标题
	      page: 1,
	      colNames: ['id', '运营商','省份','城市','话费包','折扣'],
	      colModel: [
	          { name: 'id', key: true, sortable: false,hidden:true },
	          { name: 'providerName', sortable: false} ,// 查询条件要添加index
	          { name: 'provinceCode', sortable: false} ,
	          { name: 'city', sortable: false} ,
	          { name: 'billPkgName', sortable: false} ,
	          { name: 'discount', sortable: false}
	      ],
	      // width: 750,
	      height: 'auto',
	      multiselect:true,// 多选框
	      multiboxonly:true,// 为true时是单选
	      autowidth:true,
	      shrinkToFit:true,
	      hidegrid: false,	// 隐藏表格右上角的"展开/收缩jqGrid内容的小箭头"
	      autoScroll: false,
	      rowNum : 5,
	      rowList : [5,10,15,20,30,50,100],
	      viewrecords : true,// 显示总记录数
	      rownumbers: true,
	      pager: myJqPageId
	  });
	  
	  $('#name').focus();
	  setTimeout(function(){
	      $('.wrapper-content').removeClass('animated fadeInRight');
	  },700);
// setMyActive(0,1); //设置激活页
	  
	  message();
}
