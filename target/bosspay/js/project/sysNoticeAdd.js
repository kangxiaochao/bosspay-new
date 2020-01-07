var um;

$(function(){
	$('#title').focus();
	initUMditor();	  // 初始化文本编辑器
	setMyActive(1,6); //设置激活页
	setSelectStyle($("#order"));
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
	um.execCommand('cleardoc');
	$("#content").val("");
}