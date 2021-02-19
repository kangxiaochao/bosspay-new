var myTbContentId = 'mytbc1';
var myJqTbContentId = '#' + myTbContentId;

var myTbId = 'myt1';
var myJqTbId = '#' + myTbId;

var myPageId = 'myp1';
var myJqPageId = '#' + myPageId;


$(function () {
    var orderid = $('#orderid').val();
    var dispatcher_provider_id = $('#dispatcher_provider_id').val();
    var t1 = $('<table></table>');
    t1.attr('id', myTbId);
    $(myJqTbContentId).append(t1);

    var p1 = $('<div></div>');
    p1.attr('id', myPageId);

    p1.insertAfter(t1);

    $(myJqTbId).jqGrid({
        url: basePath + 'findbytask',
        mtype: "GET",
        datatype: "json",
        caption: "通道号段列表", //设置表标题
        page: 1,
        colNames: ['id', "平台订单号","代理商订单号", "上家订单号","手机号", '充值金额', '状态', "查单结果",],
        colModel: [{name: 'id', key: true, sortable: false, hidden: true},//id
            {name: 'orderId', sortable: false},//平台订单号
            {name: 'agentOrderId', sortable: false},//代理商订单号
            {name: 'providerOrderId', sortable: false},//上家订单号
            {name: 'phone', sortable: false},//手机号
            {name: 'fee', sortable: false},//充值金额
            {name: 'status', sortable: false, formatter: formatStatus}, //状态
            {name: 'result_code', sortable: false},//查单结果
        ],
        //width: 750,
        height: 'auto',
        multiselect: true,//多选框
        multiboxonly: true,//为true时是单选
        autowidth: true,
        shrinkToFit: true,
        hidegrid: false, //隐藏表格右上角的"展开/收缩jqGrid内容的小箭头"
        autoScroll: false,
        rowNum: myRowNum,
        rowList: myRowList,
        viewrecords: true,//显示总记录数
        rownumbers: true,
        rownumWidth: 75,
        pager: myJqPageId
    });

    $('#name').focus();
    setTimeout(function () {
        $('.wrapper-content').removeClass('animated fadeInRight');
    }, 700);

    setSelectStyle($("#carrierType"));
    message();
    setMyActive(3, 9); //设置激活页


    getPhysicalId();
});


function formatStatus(cellvalue) {
    if (cellvalue == "1") {
        return "提交成功";
    } else if (cellvalue == "2") {
        return "提交失败";
    } else if (cellvalue == "3") {
        return "充值成功";
    } else if (cellvalue == "4") {
        return "充值失败";
    } else {
        return "处理中";
    }
}

/*获取话费通道组列表*/
// function getPhysicalId() {
//     var dispatcher_provider_id = $("#dispatcher_provider_id");
//     var myDelUrl = basePath + 'physicalList';
//     $.ajax({
//         type: 'get',
//         url: myDelUrl,
//         success: function (dt) {
//             //var option = $("<option>").text("").val("")
//             //dispatcher_provider_id.append(option);
//             $.each(dt, function (index, val) {
//                 option = $("<option>").text(val.name).val(val.id)
//                 dispatcher_provider_id.append(option);
//             });
//             setSelectStyle(dispatcher_provider_id);
//         },
//         dataType: 'json'
//     });
// }

//根据条件查询号段信息
// function search() {
//     var orderid = $('#orderid').val();
//     var dispatcher_provider_id = $('#dispatcher_provider_id').val();
//     $.ajax({
//         type: 'post',
//         url: basePath + 'findbytask',
//         data: {'orderid': $.trim(orderid),'dispatcher_provider_id': $.trim(dispatcher_provider_id)},
//         dataType: "json",
//         success: function (dt) {
//                 console.log(dt)
//         },
//     });
// }

//根据条件查询号段信息
function search() {
    var orderid = $('#orderid').val();
    var dispatcher_provider_id = $('#dispatcher_provider_id').val();
    $(myJqTbId).jqGrid('setGridParam', {
        url: basePath + 'findbytask',
        mtype: "post",
        postData: {
            'orderid': $.trim(orderid),
            'dispatcher_provider_id': $.trim(dispatcher_provider_id),
        },
        page: 1
    }).trigger("reloadGrid"); //重新载入

}

function alldaoru() {
    var index = layer.open({
        type: 1,
        title: false,
        skin: 'layui-layer-rim', //加上边框
        area: ['400', '250px'], //宽高
        content: '<form id="statusForm"  method="post" action="" enctype="multipart/form-data">' +
            '<div class="col-md-12">' +
            '<label class="col-md-4 control-label">批量上传：&nbsp;</label>' +
            '<div class="col-md-8">' +
            '<input type="file" name="file" />' +
            '</div>' +
            '</div>' +
            '</from>',
        btn: ["确定", "取消"],

        yes: function () {
            $.ajax({
                type: "post",
                url: basePath + "findbytaskbyexcel",
                data: new FormData(document.getElementById("statusForm")),
                processData: false,
                contentType: false,
                dataType: 'json',
                success: function (data) {
                    $(myJqTbId).jqGrid("clearGridData");
                    //var resultList = data.resultList;
                    var finddata = data.rows
                    var length =  finddata.length;
                    for (var i = 0; i < length; i++) {
                        var temp = (finddata[i]);
                        $(myJqTbId).setGridParam({cellEdit: false});
                        var rowData = {
                            'orderId': temp.orderId,
                            'providerOrderId': temp.providerOrderId,
                            'agentOrderId': temp.agentOrderId,
                            'phone': temp.phone,
                            'fee': temp.fee,
                            'status': temp.status,
                            'result_code': temp.result_code,
                        }
                        $(myJqTbId).jqGrid("addRowData", i, rowData);
                        $(myJqTbId).jqGrid().trigger("reloadGrid");
                    }
                }
            });
            layer.close(index);
        },
        btn2: function () {

        }
    });
}
