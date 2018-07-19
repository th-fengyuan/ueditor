<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ page import="java.io.File" %>
<%@ page import="com.fengyuan.util.ImportWordUtil"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <base href="<%=basePath%>">
    
    <title>My JSP 'test.jsp' starting page</title>
    
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	<!--
	<link rel="stylesheet" type="text/css" href="styles.css">
	-->
	<script type="text/javascript" src="js/jquery-3.3.1.js"></script>
	<script type="text/javascript">
	//加载框
	loading = {
		load : null,
		initLoad : function(width, height) {
			// 加载中...
			var loading = $('<div id="loading"><b>'+this.message+'</b></div>');
			loading.css({
				position : "fixed",
				width : width,
				height : height,
				top : 0,
				'font-size' : '28px',
				'text-align' : 'center',
				'line-height' : height + 'px',
				'z-index' : '99999',
				'background-color' : 'rgba(204,204,204,0.3)',
				'margin-left' : '-10px'
			//opacity : '0.2'
			});
			loading.appendTo($('body'));
			this.load = loading;
			$(window).resize(function() {
				var width = $(window).width() + 10;
				var height = $(window).height();
				loading.css({
					width : width,
					height : height,
					'line-height' : height + 'px'
				});
			});
		},
		show : function() {
			var width = $(window).width() + 10;
			var height = $(window).height();
			if (this.load) {
				this.load.css({
					width : width,
					height : height,
					'line-height' : height + 'px'
				});
				this.load.show();
			} else {
				this.initLoad(width, height);
			}
		},
		close : function() {
			if (this.load) {
				this.load.hide();
			}
		},
		message:'加载中...'
	}
	
	
	</script>
	<script type="text/javascript">
	function importword(){
		var input = document.createElement('input');
		input.setAttribute('type', 'file');
		input.setAttribute('accept', 'application/msword,application/vnd.openxmlformats-officedocument.wordprocessingml.document');
		input.onchange = function(){
		var formData = new FormData();
			formData.append('myfile',this.files[0]);
			formData.append("test","123456");
			$.ajax({
				type: 'post',
                url: "ueditor/jsp/importword.jsp",
                data: formData,
                dataType:'json',
                cache: false,
                processData: false,
                contentType: false,
                success:function(data){
                	if(data.success){
                		$('#content').html(data.content);
                	}else{
                		alert(data.content);
                	}
                }
			});
		}
		input.click();
	}
	</script>
  </head>
  
  <body>
    <form action="ueditor/jsp/importword.jsp?tt=0002"  method="post" enctype="multipart/form-data">
        <input type="file" name="filename" size="45" accept="application/msword,application/vnd.openxmlformats-officedocument.wordprocessingml.document"><br>
        <input type="submit" name="submitss" value="submit">
    </form>
<input type="button" value="上传" onclick="importword()">
<div id="content">
</div>
<script type="text/javascript">
loading.show();
</script>
  </body>
</html>
