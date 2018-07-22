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

import com.baidu.ueditor.PathFormat;
import com.baidu.ueditor.define.AppInfo;
import com.baidu.ueditor.define.BaseState;
import com.baidu.ueditor.define.State;

/**
 * 
 * @author TH_FengYuan
 *
 */
public class ConvertWordToHTML {
	private HttpServletRequest request;
	private Map<String, Object> config;
	private String fileType;

	public ConvertWordToHTML(HttpServletRequest request, Map<String, Object> config) {
		this.request = request;
		this.config = config;
	}

	public State convert() {
		try {
			String rootPath = config.get("rootPath").toString();
			String savePath = PathFormat.parse(config.get("savePath").toString());
			FileItem fileItem = getFile();
			if(fileItem==null){
				return new BaseState(false,AppInfo.NOTFOUND_UPLOAD_DATA);
			}
			State state = check(fileItem);
			if(!state.isSuccess()){
				return state;
			}
			Word2HtmlUtil word2HtmlUtil = Word2HtmlUtil.getInstance(fileItem.getInputStream(), fileType, rootPath + savePath, savePath);
			String html = word2HtmlUtil.getHTML();
			html = parseHTML(html);
			state.putInfo("content", html);
			return state;
		} catch (Exception e) {
			e.printStackTrace();
			return new BaseState(false,"解析失败，请稍后再试!");
		}
	}

	private FileItem getFile() throws FileUploadException, IOException {
		DiskFileItemFactory factory = new DiskFileItemFactory();
		ServletFileUpload uploader = new ServletFileUpload(factory);
		List<FileItem> fileItems = uploader.parseRequest(request);
		for (FileItem item : fileItems) {
			if (!item.isFormField()) {
				return item;
			}
		}
		return null;
	}

	public State check(FileItem fileItem) {
		BaseState state = new BaseState(false);
		String fileName = fileItem.getName();
		int fileTypeIndex = fileName.lastIndexOf(".");
		if (fileTypeIndex != -1) {
			fileType = fileName.substring(fileTypeIndex);
		}
		boolean isCheckFileType = ".doc".equals(fileType) || ".docx".equals(fileType);
		long fileSize = fileItem.getSize();
		long maxSize = ((Long) config.get("importwordMaxSize")).longValue();
		if (isCheckFileType) {
			if (fileSize <= maxSize) {
				state.setState(true);
			} else {
				state.setInfo(AppInfo.MAX_SIZE);
			}
		} else {
			state.setInfo(AppInfo.NOT_ALLOW_FILE_TYPE);
		}
		return state;
	}

	public String parseHTML(String html) {
		html = html.replaceAll("(<|&lt;)", "#<#").replaceAll("(>|&gt;)", "#>#").replaceAll("(\"|&quot;)", "#@@#")
				.replaceAll("('|&acute;)", "#@#").replaceAll("\r\n\t", "");
		return html.toString();
	}
}
