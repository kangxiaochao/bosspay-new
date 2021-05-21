var myTbContentId = 'mytbc1';
var myJqTbContentId = '#' + myTbContentId;
var myTbId = 'myt1';
var myJqTbId = '#' + myTbId;
var myPageId = 'myp1';
var myJqPageId = '#' + myPageId;

$(function () {
    var t1 = $('<table></table>');
    t1.attr('id', myTbId);
    $(myJqTbContentId).append(t1);

    var p1 = $('<div></div>');
    p1.attr('id', myPageId);

    p1.insertAfter(t1);

    $(myJqTbId).jqGrid({
        url: basePath + 'cookies',
        mtype: "GET",
        datatype: "json",
        caption: "公告列表", // 设置表标题
        page: 1,
        colNames: ['id', 'cookies', '创建时间', '类型'],
        colModel: [{
            name: 'ids',
            key: true,
            sortable: false,
            hidden: true
        }, {
            name: 'cookies',
            sortable: false,
            index: 'cookies'
        }, {
            name: 'updatetime', index: 'updatetime', align: 'center', sortable: false
        },
            {
                name: 'bz',
                sortable: false,
                index: 'bz'
            }],
        // width: 750,
        height: 'auto',
        multiselect: false,// 多选框
        multiboxonly: true,// 为true时是单选
        autowidth: true,
        shrinkToFit: true,
        hidegrid: false, // 隐藏表格右上角的"展开/收缩jqGrid内容的小箭头"
        autoScroll: false,
        rowNum: myRowNum,
        rowList: myRowList,
        viewrecords: true,// 显示总记录数
        rownumbers: true,
        pager: myJqPageId
    });

    $('#name').focus();
    setTimeout(function () {
        $('.wrapper-content').removeClass('animated fadeInRight');
    }, 700);

    setMyActive(2, 13); // 设置激活页

    message();
});

function search() {
    var agentName = $('#agentName').val();

    $(myJqTbId).jqGrid('setGridParam', {
        url: basePath + 'cookies',
        postData: {
            'agentName': agentName
        }, // 发送数据
        page: 1
    }).trigger("reloadGrid"); // 重新载入
}


var dateTime = new Date().getTime();


function add0(m) {
    return m < 10 ? '0' + m : m
}

function format(shijianchuo) {
    //shijianchuo是整数，否则要parseInt转换
    var time = new Date(shijianchuo);
    var y = time.getFullYear();
    var m = time.getMonth() + 1;
    var d = time.getDate() + 1;
    var h = time.getHours() + 1;
    var mm = time.getMinutes() + 1;
    var s = time.getSeconds() + 1;
    return y + '-' + add0(m) + '-' + add0(d) + ' ' + add0(h) + ':' + add0(mm) + ':' + add0(s);
}

var date = format(dateTime);

