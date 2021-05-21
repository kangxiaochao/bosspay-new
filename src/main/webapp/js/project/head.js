
var basePath=document.getElementsByTagName('base')[0].href;

var myRowNum;
var myRowList;

$(function(){
	ajaxError();
	setJqGridHeight();
	setJqGridWidth();
});

/**
 * 设置全局ajax请求发生错误,如果是登录超时,则跳转到登录页面
 */
function ajaxError(){
	$(document).ajaxError(function(e, xhr, opt) {
		var str = xhr.responseText;
		if (str.indexOf('onclick="toLogin()"') != -1) {
			window.location.href = basePath;
		}
	}); 
}

function deleteMethod(myUrl){
	$.ajax({		
		type:'DELETE',
		url :myUrl,
		data:{ myName: '' },
		success:function (dt){
			location.href=basePath+dt;
	    },
	    dataType: 'html'
		});
}

function loadPage(url,obj){
	if(obj!=null){
	$('#leftMenu li').each(function(index,el){
		$(el).removeClass('active');
	});
	$myActive=$(obj);
	$myActive.parent().addClass('active');
	}
	$('#mainContent').load(url);
}

function initRadioStyle(){  
    $('.i-checks').iCheck({
        checkboxClass: 'icheckbox_square-green',
        radioClass: 'iradio_square-green',
    });
}

//输出提示信息
function message(){
	var backMsg = $('#backMsg').val();
	if(backMsg != ''){
		outMessage('info', backMsg, '友情提示');
	}
}

//输出提示信息
function outMessage(type, message, title) {
	toastr.options = {
		closeButton : true,
		progressBar : true,
		showMethod : 'slideDown',
		positionClass : "toast-bottom-right",
		timeOut : 4000
	};
	if (type == 'success') {
		toastr.success(message, title);
	}
	if (type == 'info') {
		toastr.info(message, title);
	}
	if (type == 'warning') {
		toastr.warning(message, title);
	}
	if (type == 'error') {
		toastr.error(message, title);
	}
}

function setMyActive(m1,m2,m3){
	console.log(m1+'|'+m2+'|'+m3)
	$('#side-menu li').each(function(index,el){
		$(this).removeClass('active');
		$(this).removeClass('in');
	})
	$('#side-menu ul').each(function(index,el){
		$(this).removeClass('active');
		$(this).removeClass('in');
	})
		
	if(m2!=undefined){
		//处理三级
		$('#p'+m1).addClass('nav nav-second-level collapse in active');
		$('#p'+m1+m2).addClass('nav nav-second-level active collapse in');
		$('#p'+m1+'u').addClass('nav nav-second-level active collapse in');
		$('#p'+m1+m2+'v'+m3).addClass('active');
		$('#p'+m1+m2+'v').addClass('active');
	}else{
		$('#p'+m1).addClass('nav nav-second-level collapse in active');
		$('#p'+m1+m2).addClass('active');
		$('#p'+m1+'u').addClass('nav nav-second-level active collapse in');
	}
}

// 设置select下拉列表的样式并重新加载
function setSelectStyle(selectId){
	// 初始化chosen-select样式
	selectId.chosen({no_results_text: "没有搜索到信息"});
	// 因为chosen-select中的option是动态加载的,因此在加载完毕样式之后需要重新刷新一下
	selectId.trigger("chosen:updated");
}

// 动态显示/隐藏列信息
function showColumnDialog() { 
	//获取表格列数量
	var colNamesLength = $(myJqTbId).jqGrid('getGridParam','colNames').length-1;
	var dialogHeight= 25 * colNamesLength + 100;	//25是行高,colNamesLength是列数,100是预留高度
	var dialogWidth = 400;
	var winWidth =  $(window).width();	//浏览器时下窗口可视区域宽度
	var winHeight = $(window).height(); //浏览器时下窗口可视区域高度  
	
	var winLeft = winWidth/2-dialogWidth/2;
	var winTop = winHeight/2-dialogHeight/2;
	
	$(myJqTbId).jqGrid('setColumns', {
		left : winLeft,
		top : winTop,
		modal: true,
		width : dialogWidth,
		height: dialogHeight,
		updateAfterCheck: true,
		jqModal: true
	});
}

//设置JqGrid随浏览器大小自动宽度
function setJqGridWidth(){
	$(window).resize(function(){  
		$(myJqTbId).setGridWidth($(window).width()-310);
	});
}

//设置JqGrid宽度随左侧菜单收缩而自动调整
function setJqGridWidthTwo(){
	var leftMenuDivWidth = $("#leftMenuDiv").width();
	if(leftMenuDivWidth > 200){
		$(myJqTbId).setGridWidth($(window).width()-(70+90));
	}else{
		$(myJqTbId).setGridWidth($(window).width()-(220+90));
	}
}

//设置JqGrid自动高度
function setJqGridHeight()
{
	if($("#mytbc1").length > 0){
		var winHeight = $(window).height(); //浏览器时下窗口可视区域高度  
		var topNavDiv = $("#topNavDiv").height();
		var breadcrumbsDiv = $("#breadcrumbsDiv").height();
		var queryDiv = $("#queryDiv").height();
		var divHeight = winHeight - topNavDiv -breadcrumbsDiv - queryDiv - 190;
//		if(divHeight < 400){
//			divHeight = 400
//		}
		myRowNum = parseInt((divHeight-40-23)/29)-1;
		if(myRowNum < 10){
			myRowNum = 10;
		}else {
            myRowNum = 10;
		}
		myRowList = [ myRowNum ,10, 20, 30, 50, 100];
		//排序
		myRowList.sort(function(a,b){  
			return a-b;  
		});
		//去重
		myRowList = $.unique(myRowList);
	}
}

function countTime(time){
	var days    = time / 1000 / 60 / 60 / 24;
	var daysRound   = Math.floor(days);
	var hours    = time/ 1000 / 60 / 60 - (24 * daysRound);
	var hoursRound   = Math.floor(hours);
	var minutes   = time / 1000 /60 - (24 * 60 * daysRound) - (60 * hoursRound);
	var minutesRound  = Math.floor(minutes);
	var seconds   = time/ 1000 - (24 * 60 * 60 * daysRound) - (60 * 60 * hoursRound) - (60 * minutesRound);
//	alert(daysRound+"天"+hoursRound+"小时"+minutesRound+"分"+seconds+"秒");
	var timeCount="";
	if(daysRound > 0){
		timeCount += daysRound+"天";
	}
	if(hoursRound > 0){
		timeCount += hoursRound+"小时";
	}
	if(minutesRound > 0){
		timeCount += minutesRound+"分";
	}
	if(seconds > 0){
		timeCount += seconds.toFixed(2)+"秒";
	}
	return timeCount;
}