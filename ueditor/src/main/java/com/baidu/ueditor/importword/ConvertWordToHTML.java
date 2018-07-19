package com.baidu.ueditor.importword;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.json.JSONArray;
import org.json.JSONWriter;

import com.baidu.ueditor.PathFormat;
import com.baidu.ueditor.define.BaseState;
import com.baidu.ueditor.define.State;

public class ConvertWordToHTML {
	private BaseState state;
	private HttpServletRequest request;
	private Map<String, Object> config;
	private String fileType;

	public ConvertWordToHTML(HttpServletRequest request, Map<String, Object> config) {
		this.request = request;
		this.config = config;
	}

	public State convert() {
		state = new BaseState();
		try {
			String rootPath = config.get("rootPath").toString();
			String savePath = PathFormat.parse(config.get("savePath").toString());
			InputStream is = getFile();
			Word2HtmlUtil word2HtmlUtil = Word2HtmlUtil.getInstance(is, fileType, rootPath + savePath, savePath);
			String html = word2HtmlUtil.getHTML();
			html = parseHTML(html);
			System.out.println(html);
			state.setState(true);
			state.putInfo("content", html);
		} catch (Exception e) {
			e.printStackTrace();
			state.setState(false);
			state.putInfo("content", "解析失败，请稍后再试!");
		}
		return state;
	}

	private InputStream getFile() throws FileUploadException, IOException {
		DiskFileItemFactory factory = new DiskFileItemFactory();
		ServletFileUpload uploader = new ServletFileUpload(factory);
		List<FileItem> FileItems = uploader.parseRequest(request);
		FileItem fileItem = null;
		String fileName = null;
		for (FileItem item : FileItems) {
			if(!item.isFormField()){
				fileItem = item;
				fileName = item.getName();
				break;
			}
		}
		int fileTypeIndex = fileName.lastIndexOf(".");
		if (fileTypeIndex != -1) {
			fileType = fileName.substring(fileTypeIndex);
		}
		boolean isCheckFileType = ".doc".equals(fileType) || ".docx".equals(fileType);
		if (isCheckFileType) {
			return fileItem.getInputStream();
		}
		state.setState(false);
		state.putInfo("content", "不支持的文件类型!");
		return null;
	}
	
	
	public String parseHTML(String html){
		html = html.replaceAll("(<|&lt;)", "#<#")
					.replaceAll("(>|&gt;)", "#>#")
					.replaceAll("(\"|&quot;)", "#@@#")
					.replaceAll("('|&acute;)", "#@#")
					.replaceAll("\r\n\t", "");
		return html.toString();
	}
}
