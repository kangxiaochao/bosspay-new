var myTbContentId = 'mytbc1';
var myJqTbContentId = '#' + myTbContentId;

var myTbId = 'myt1';
var myJqTbId = '#' + myTbId;

var myPageId = 'myp1';
var myJqPageId = '#' + myPageId;

$(function () {
    $("#startDate").val(laydate.now() + ' 00:00:00');
    $("#endDate").val(laydate.now(1) + ' 00:00:00');
    getPhysicalId();
    var t1 = $('<table></table>');
    t1.attr('id', myTbId);
    $(myJqTbContentId).append(t1);

    var p1 = $('<div></div>');
    p1.attr('id', myPageId);

    p1.insertAfter(t1);
    $(myJqTbId).jqGrid({
        url: basePath + 'queryOrderStatusInq',
        mtype: "GET",
        datatype: "json",
        caption: "异常订单查询", //设置表标题
        page: 1,
        colNames: ["平台订单号","代理商订单号", "上家订单号","手机号", '充值金额', '状态', "备注",],
        colModel: [
            {name: 'orderId', sortable: false},//平台订单号
            {name: 'agentOrderId', sortable: false},//代理商订单号
            {name: 'providerOrderId', sortable: false},//上家订单号
            {name: 'phone', sortable: false},//手机号
            {name: 'fee', sortable: false},//充值金额
            {name: 'status', sortable: false, formatter: formatStatus}, //状态
            {name: 'resultCode', sortable: false},//查单结果
        ],
        //width: 750,
        height: 'auto',
        multiselect:false,//多选框
        multiboxonly:true,//为true时是单选
        autowidth:true,
        shrinkToFit:true,
        hidegrid: false,	//隐藏表格右上角的"展开/收缩jqGrid内容的小箭头"
        autoScroll: false,
        rowNum : myRowNum,
        rowList : myRowList,
        viewrecords : true,//显示总记录数
        rownumbers: true,
        pager: myJqPageId
    });
    setSelectStyle($("#dispatcherProviderId"));
    message();
    setMyActive(3, 8); //设置激活页
});

function search(){
    var data = $("#searchForm").serialize();
    $('#myt1').jqGrid('setGridParam',{
        url:basePath+'queryOrderStatusInq?'+data,
        page:1
    }).trigger("reloadGrid"); //重新载入
}

/**
 * 获取话费通道组列表
 **/
function getPhysicalId() {
    var dispatcher_provider_id = $("#dispatcherProviderId");
    $.ajax({
        type: 'get',
        url:  basePath + 'physicalList',
        success: function (dt) {
            // var option = $("<option>").text("").val("")
            //dispatcher_provider_id.append(option);
            var option = "";
            $.each(dt, function (index, val) {
                if(val.provider_task_mark != null && val.provider_task_mark.length > 0){
                    option = $("<option>").text(val.name).val(val.id)
                    dispatcher_provider_id.append(option);
                }
            });
            setSelectStyle(dispatcher_provider_id);
            search()
        },
        dataType: 'json'
    });
}

/* 绑定日期控件 */
laydate({
    elem: '#startDate',// 目标元素。由于laydate.js封装了一个轻量级的选择器引擎，因此elem还允许你传入class、tag但必须按照这种方式
    event: 'focus', 	// 响应事件。如果没有传入event，则按照默认的click
    format: 'YYYY-MM-DD hh:mm:ss', // 日期格式
    istime: true, // 是否开启时间选择
});

/* 绑定日期控件 */
laydate({
    elem: '#endDate',	// 目标元素。由于laydate.js封装了一个轻量级的选择器引擎，因此elem还允许你传入class、tag但必须按照这种方式
    event: 'focus',		// 响应事件。如果没有传入event，则按照默认的click
    format: 'YYYY-MM-DD hh:mm:ss', // 日期格式
    istime: true, // 是否开启时间选择
});

function formatStatus(cellvalue) {
    if (cellvalue == "1") {
        return "充值成功";
    } else if (cellvalue == "0") {
        return "充值失败";
    } else if (cellvalue == "2")  {
        return "充值中";
    } else {
        return "查询失败";
    }
}

