var um;

$(function(){
	$('#title').focus();
	initUMditor();	  // 初始化文本编辑器
	setMyActive(6,1); //设置激活页
});

// 初始化文本编辑器
function initUMditor() {
	//实例化编辑器
	um = UM.getEditor("myEditor",{
		toolbar:[
              'undo redo | bold italic underline strikethrough | forecolor backcolor |',
              'insertorderedlist insertunorderedlist | selectall cleardoc paragraph | fontfamily fontsize',
              '| justifyleft justifycenter justifyright justifyjustify source |'
        ]
	});
}

// 设置编辑的内容信息到隐藏域以保存到数据库
function setContent(){
	var content = UM.getEditor('myEditor').getContent();
	$("#content").val(content);
}

// 重置内容信息
function resetCotent(){
	var content = $("#content").val();
	UM.getEditor('myEditor').setContent(content);
}

// 更新公告信息到数据库
function submitEdit(){
	setContent();
	var myFormActionUrl = $('#fm').attr("action");
	var data = $("form").serialize();
	
	$.ajax({ 
		type:'PUT',
		url :myFormActionUrl,
		data:data,
		success:function (dt){
			location.href=basePath+dt;
	    },
	    dataType: 'html'
	});
}