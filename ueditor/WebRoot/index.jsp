<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<base href="<%=basePath%>">

<title>My JSP 'index.jsp' starting page</title>
<meta http-equiv="pragma" content="no-cache">
<meta http-equiv="cache-control" content="no-cache">
<meta http-equiv="expires" content="0">
<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
<meta http-equiv="description" content="This is my page">
<!--
	<link rel="stylesheet" type="text/css" href="styles.css">
	-->
<!-- 配置文件 -->
<script type="text/javascript" src="ueditor/ueditor.config.js"></script>
<!-- 编辑器源码文件 -->
<script type="text/javascript" src="ueditor/ueditor.all.js"></script>
<script type="text/javascript" src="ueditor/lang/zh-cn/zh-cn.js"></script>
<script type="text/javascript" src="js/jquery-3.3.1.js"></script>
<!-- <script type="text/javascript" src="js/importword.js"></script> -->
</head>

<body>
	<!-- 加载编辑器的容器 -->
	<script id="container" name="content" type="text/plain" style="width:1200px;"></script>
	<!-- 实例化编辑器 -->
	<script type="text/javascript">
		var ue = UE.getEditor('container');
		ue.ready(function() {
		ue.setHeight(500);
		//$("edui1_toolbarbox");
		var t = document.getElementById("")
		var load = document.createElement('div')
		load.style = 'top: 82px; z-index: 1000;';
		load.className = 'edui-editor-messageholder edui-default';
		load.innerHTML = '<div class="edui-message  edui-default" style="display: block;"><div class="edui-message-body edui-message-type-info edui-default"> <iframe style="position:absolute;z-index:-1;left:0;top:0;background-color: transparent;" frameborder="0" width="100%" height="100%" src="about:blank" class="edui-default"></iframe> <div class="edui-shadow edui-default"></div> <div id="edui284_content" class="edui-message-content edui-default">转换中...</div> </div></div>';
		})
	</script>
</body>
</html>
