getPhysicalId();
/*获取话费通道组列表*/
function getPhysicalId() {
    var dispatcher_provider_id = $("#dispatcher_provider_id");
    var myDelUrl = basePath + 'physicalList';
    $.ajax({
        type: 'get',
        url: myDelUrl,
        success: function (dt) {
            $.each(dt, function (index, val) {
                var option = $("<option>").text(val.name).val(val.id)
                dispatcher_provider_id.append(option);
            });
            setSelectStyle(dispatcher_provider_id);
            //getProviderId();
        },
        dataType: 'json'
    });
}