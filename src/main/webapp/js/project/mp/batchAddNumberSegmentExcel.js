
$(function(){

});

function submits() {
    layer.load();
    $("#submit").attr("disabled", true);
    var form = new FormData(document.getElementById("upload"));
    $.ajax({
        type : "post",
        url  : "batchAddPageExcel",
        data : form,
        processData : false,
        contentType : false,
        dataType : 'json',
        success : function(data) {
            layer.closeAll('loading');
            if(data == '-1') {
                layer.msg('文件格式错误，请联系客服人员进行核对', {
                    time: 20000, //20s后自动关闭
                    btn: ['明白了', '知道了', '哦']
                });
            }else {
                layer.msg(data + '个号段添加成功', {
                    time: 20000, //20s后自动关闭
                    btn: ['明白了', '知道了', '哦']
                });
            }
        }
    });
}
