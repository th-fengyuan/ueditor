(function(window, document) {
	window.UEDITOR_CONFIG.toolbars[0].push('importword');
	UE.I18N['zh-cn'].labelMap['importword'] = '导入Word文档';
	var editorui = baidu.editor.ui;
	UE.commands['importword'] = {
		execCommand : function() {
			importword(this);
		}
	}

	function importword(ue) {
		var input = document.createElement('input');
		input.setAttribute('type', 'file');
		input.setAttribute('accept', 'application/msword,application/vnd.openxmlformats-officedocument.wordprocessingml.document');
		var formData = new FormData();
		input.onchange = function() {
			var fileName = this.value;
			if(/.*\.(doc|docx)/.test(fileName)){
				formData.append('myfile', this.files[0]);
				formData.append('action', 'importword');
				loading.message="转换中..."
				loading.show();
				$.ajax({
					type : 'post',
					url : "ueditor/jsp/controller.jsp",
					data : formData,
					dataType : 'json',
					cache : false,
					processData : false,
					contentType : false,
					success : function(data) {
						if (data.state=="SUCCESS") {
							ue.setContent(data.content);
						} else {
							alert(data.content);
						}
						loading.close();
					},
					error:function(response,msg){
						alert('服务器异常，请稍后再试!');
						loading.close();
					}
				});
			}else{
				alert("非法的文件类型!");
			}
		}
		input.click();
	}
	editorui['importword'] = function(cmd) {
		return function(editor) {
			var ui = new editorui.Button({
				className : 'edui-for-' + cmd,
				title : "导入Word文档",
				onclick : function() {
					editor.execCommand(cmd);
				},
				theme : editor.options.theme,
				showText : false
			});
			editorui.buttons[cmd] = ui;
			editor.addListener('selectionchange', function(type, causeByUi, uiReady) {
				var state = editor.queryCommandState(cmd);
				if (state == -1) {
					ui.setDisabled(true);
					ui.setChecked(false);
				} else {
					if (!uiReady) {
						ui.setDisabled(false);
						ui.setChecked(state);
					}
				}
			});
			return ui;
		};
	}('importword');
	var style = document.createElement('style');
	(document.head || document.body).appendChild(style);
	style.type = 'text/css';
	style.textContent = '.edui-default .edui-for-importword .edui-icon{background-position: -300px -40px;}';
})(window, document);

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