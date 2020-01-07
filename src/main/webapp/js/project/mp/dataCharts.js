$(function(){
	$("#startDate").val(laydate.now());
	$("#endDate").val(laydate.now(1));
	setMyActive(7,7); //设置激活页
});
// 折线图
function paintCharts(){
	var data = $("#searchForm").serialize();
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
		url:basePath+"getChartsData?"+data,
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

//折线图
function paintChannelCharts(){
	var data = $("#searchForm").serialize();
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
		url:basePath+"getChannelChartsData?"+data,
		type:"GET",
		dataType:"json",
		async:false,
		success:function(data){
			 myChart.setOption({  
				 legend: {
				        data:data.channelNameData
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
	elem : '#startDate',// 目标元素。由于laydate.js封装了一个轻量级的选择器引擎，因此elem还允许你传入class、tag但必须按照这种方式
	event : 'focus', // 响应事件。如果没有传入event，则按照默认的click
	format : 'YYYY-MM-DD', // 日期格式
	istime : true, // 是否开启时间选择
});

/* 绑定日期控件 */
laydate({
	elem : '#endDate', // 目标元素。由于laydate.js封装了一个轻量级的选择器引擎，因此elem还允许你传入class、tag但必须按照这种方式
	event : 'focus', // 响应事件。如果没有传入event，则按照默认的click
	format : 'YYYY-MM-DD', // 日期格式
	istime : true, // 是否开启时间选择
});