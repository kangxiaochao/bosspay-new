var myTbContentId = 'mytbc1';
var myJqTbContentId = '#' + myTbContentId;

var myTbId = 'myt1';
var myJqTbId = '#' + myTbId;

var myPageId = 'myp1';
var myJqPageId = '#' + myPageId;

$(function () {
    var section = $('#section').val();
    var dispatcher_provider_id = $('#dispatcher_provider_id').val();
    var t1 = $('<table></table>');
    t1.attr('id', myTbId);
    $(myJqTbContentId).append(t1);

    var p1 = $('<div></div>');
    p1.attr('id', myPageId);

    p1.insertAfter(t1);

    //div里面有table,table里面还有div

    $(myJqTbId).jqGrid({
        url: basePath + 'phonePhysicalchannel',
        mtype: "GET",
        datatype: "json",
        caption: "通道号段列表", //设置表标题
        page: 1,
        colNames: ['id', '物理通道名称', '号段', '物理通道id', "创建时间"],
        colModel: [{name: 'id', key: true, sortable: false, hidden: true},//id
            {name: 'name', sortable: false},//通道名称,
            {name: 'section', sortable: false},//号段
            {name: 'dispatcher_provider_id', sortable: false, hidden: true}, //物理通道id
            {name: 'create_time', sortable: false},//创建时间
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
    setMyActive(2, 11); //设置激活页

    getPhysicalId();

});



//格式化号段类型显示信息
function phontSelectformatter(cellvalue, options, rowObject) {
//	if("1" == cellvalue){
//		return "中国移动";
//	}else if("2" == cellvalue){
//		return "中国联通";
//	}else if("3" == cellvalue){
//		return "中国电信";
//	}else if("4" == cellvalue){
//		return "虚拟运营商移动";
//	}else if("5" == cellvalue){
//		return "虚拟运营商联通";
//	}else if("6" == cellvalue){
//		return "虚拟运营商电信";
//	}else if("7" == cellvalue){
//		return "物联网卡";
//	}else{
//		return "普卡";
//	}
//     if("1" == cellvalue){
//         return "普卡";
//     }else if("7" == cellvalue){
//         return "物联网卡";
//     }else{
//         return "";
//     }
}

//跳转到号段添加页面
function add() {
    location.href = basePath + "phonePhysicalchannelAddPage";
}

function batchAdd() {
    location.href = basePath + "batchAddPageExcel";
}

//跳转到号段详情页面
function detail(id) {
    var myBasePath = basePath + 'phonePhysicalchannelDetail/' + id;
    location.href = myBasePath;
}

function detailEx(id) {
    var id = $(myJqTbId).jqGrid('getGridParam', 'selrow');
    if (id == null) {
        outMessage('warning', '没有选中的记录！', '友情提示');
    } else {
        detail(id);
    }
}

//跳转到号段编辑页面
function edit(id) {
    var myBasePath = basePath + 'phonePhysicalchannelEditPage/' + id;
    location.href = myBasePath;
}

function editEx() {
    var id = $(myJqTbId).jqGrid('getGridParam', 'selrow');
    if (id == null) {
        outMessage('warning', '没有选中的记录！', '友情提示');
    } else {
        edit(id);
    }
}

//跳转到号段删除页面
function del(id) {
    var delFlag = confirm("确认要删除么？");
    if (delFlag) {
        var myDelUrl = basePath + 'phonePhysicalchanneldelate/' + id;
        $.ajax({
            type: 'DELETE',
            url: myDelUrl,
            data: {
                id: id
            },
            success: function (dt) {
                location.href = basePath + dt;
            },
            dataType: 'html'
        });
    }
}

function delEx() {
    var id = $(myJqTbId).jqGrid('getGridParam', 'selrow');
    alert(id)
    if (id == null) {
        outMessage('warning', '没有选中的记录！', '友情提示');
    } else {
        del(id);
    }
}





/*获取话费通道组列表*/
function getPhysicalId() {
    var dispatcher_provider_id = $("#dispatcher_provider_id");
    var myDelUrl = basePath + 'physicalList';
    $.ajax({
        type: 'get',
        url: myDelUrl,
        success: function (dt) {
            //var option = $("<option>").text("").val("")
            //dispatcher_provider_id.append(option);
            $.each(dt, function (index, val) {
                option = $("<option>").text(val.name).val(val.id)
                dispatcher_provider_id.append(option);
            });
            setSelectStyle(dispatcher_provider_id);
           search()
        },
        dataType: 'json'
    });
   // search();
}


//根据条件查询号段信息
function search() {

    var section = $('#section').val();
    var dispatcher_provider_id = $('#dispatcher_provider_id').val();
    // var dispatcher_provider_name = $('#dispatcher_provider_name').val();
    $(myJqTbId).jqGrid('setGridParam', {
        url: basePath + 'phonePhysicalchannel',
        postData: {
            'section': $.trim(section),
            'dispatcher_provider_id': $.trim(dispatcher_provider_id),
            //'dispatcher_provider_name': $.trim(dispatcher_provider_name),
        },
        page: 1
    }).trigger("reloadGrid"); //重新载入

}