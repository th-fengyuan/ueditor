package com.baidu.ueditor.importword;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.converter.PicturesManager;
import org.apache.poi.hwpf.converter.WordToHtmlConverter;
import org.apache.poi.hwpf.usermodel.Picture;
import org.apache.poi.hwpf.usermodel.PictureType;
import org.apache.poi.xwpf.converter.xhtml.XHTMLConverter;
import org.apache.poi.xwpf.converter.xhtml.XHTMLOptions;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFPictureData;
import org.jsoup.Jsoup;
import org.jsoup.helper.W3CDom;
import org.jsoup.select.Elements;
import org.w3c.css.sac.InputSource;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.css.CSSRule;
import org.w3c.dom.css.CSSRuleList;
import org.w3c.dom.css.CSSStyleRule;
import org.w3c.dom.css.CSSStyleSheet;

import com.steadystate.css.parser.CSSOMParser;

/**
 * Created by 天涯06 on 2017-02-24. 使用的jar包： jsoup-1.10.2.jar
 * ooxml-schemas-1.1.jar openxml4j-1.0-beta.jar
 * org.apache.poi.xwpf.converter.core-1.0.6.jar
 * org.apache.poi.xwpf.converter.xhtml-1.0.6.jar poi-3.13-20150929.jar
 * poi-ooxml-3.13-20150929.jar poi-scratchpad-3.13-20150929.jar
 * xmlbeans-2.6.0.jar xml-apis-1.0.b2.jar jsoup-1.8.3.jar cssparser-0.9.25.jar
 * sac-1.3.jar commons-io-2.4.jar
 */
public class Word2HtmlUtil {

	private InputStream is;
	private boolean isDoc;
	private Document document;
	private boolean isImgFile;
	private File folder;
	private String webPath;
	private Map<String, String> picMap = new HashMap<String, String>();
	private Word2HtmlUtil() {
	}

	/**
	 * 
	 * @param file
	 *            word文件
	 * @return
	 * @throws IOException
	 */
	public static Word2HtmlUtil getInstance(File file) throws IOException {
		if (!file.exists()) {
			throw new RuntimeException("文件不存在：" + file.getPath());
		}
		String fileName = file.getName();
		byte[] data = FileUtils.readFileToByteArray(file);
		return getInstance(data, getFileType(fileName));
	}

	/**
	 * 
	 * @param file
	 *            word文件
	 * @param folderPath
	 *            Word文档中的图片保存地址
	 * @param webPath
	 *            用于web显示的地址
	 * @return
	 * @throws IOException
	 */
	public static Word2HtmlUtil getInstance(File file, String folderPath, String webPath) throws IOException {
		Word2HtmlUtil word2HtmlUtil = getInstance(file);
		word2HtmlUtil.setFloder(folderPath, webPath);
		return word2HtmlUtil;
	}

	/**
	 * 
	 * @param data
	 *            Word文档
	 * @param fileType
	 *            Word文档类型 doc|docx
	 * @return
	 */
	public static Word2HtmlUtil getInstance(byte[] data, String fileType) {
		return getInstance(new ByteArrayInputStream(data), fileType);
	}

	/**
	 * 
	 * @param data
	 *            Word文档
	 * @param fileType
	 *            Word文档类型 doc|docx
	 * @param folderPath
	 *            Word文档中的图片保存地址
	 * @param webPath
	 *            用于web显示的地址
	 * @return
	 */
	public static Word2HtmlUtil getInstance(byte[] data, String fileType, String folderPath, String webPath) {
		return getInstance(new ByteArrayInputStream(data), fileType, folderPath, webPath);
	}

	/**
	 * 
	 * @param is
	 *            Word文档
	 * @param fileType
	 *            Word文档类型 doc|docx
	 * @return
	 */
	public static Word2HtmlUtil getInstance(InputStream is, String fileType) {
		return getInstance(is, fileType, null, null);
	}

	/**
	 * 
	 * @param is
	 *            Word文档
	 * @param fileType
	 *            Word文档类型 doc|docx
	 * @param folderPath
	 *            Word文档中的图片保存地址
	 * @param webPath
	 *            用于web显示的地址
	 * @return
	 */
	public static Word2HtmlUtil getInstance(InputStream is, String fileType, String folderPath, String webPath) {
		Word2HtmlUtil word2HtmlUtil = new Word2HtmlUtil();
		word2HtmlUtil.checkFileType(fileType);
		word2HtmlUtil.setFloder(folderPath, webPath);
		word2HtmlUtil.is = is;
		return word2HtmlUtil;
	}

	/**
	 * 设置图片的上传路径和web显示路径
	 * 
	 * @param folderPath
	 * @param webPath
	 */
	public void setFloder(String folderPath, String webPath) {
		if (folderPath == null) {
			return;
		}
		File folder = new File(folderPath);
		if (!folder.exists()) {
			folder.mkdirs();
		}
		if (webPath == null) {
			webPath = "";
		}
		if (!"".equals(webPath)) {
			if (!webPath.endsWith("/")) {
				webPath += "/";
			}
		}
		this.folder = folder;
		this.webPath = webPath;
		this.isImgFile = true;
	}

	/**
	 * 生成文件名
	 * 
	 * @param fileType
	 *            文件类型
	 * @param length
	 *            随机数的位数
	 * @return
	 */
	public String getNewFileName(String fileType, int length) {
		if (fileType.indexOf(".") == -1) {
			fileType = "." + fileType;
		}
		String fileName = String.valueOf(System.currentTimeMillis());
		fileName += String.valueOf(Math.random()).replace(".", "").substring(0, length) + fileType;
		return fileName;
	}

	/**
	 * 校验文件类型
	 * 
	 * @param fileType
	 *            文件的类型
	 */
	private void checkFileType(String fileType) {
		fileType = fileType.replace(".", "");
		if ("doc".equals(fileType)) {
			isDoc = true;
		} else if ("docx".equals(fileType)) {
			isDoc = false;
		} else {
			throw new RuntimeException("不支持的类型");
		}
	}

	/**
	 * 解析doc文档
	 * 
	 * @throws Exception
	 */
	private void parseDoc() throws Exception {
		try {
			HWPFDocument wordDocument = new HWPFDocument(is);
			// 将doc转换成XML的转换器
			WordToHtmlConverter wordToHtmlConverter = new WordToHtmlConverter(
					DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument());
			wordToHtmlConverter.setPicturesManager(new PicturesManager() {
				@Override
				public String savePicture(byte[] content, PictureType pictureType, String suggestedName,
						float widthInches, float heightInches) {
					String fileType = getFileType(suggestedName);
					if(isImgFile){
						String fileName = getNewFileName(fileType, 6);
						File file = new File(folder, fileName);
						try {
							OutputStream os = null;
							try {
								os = new FileOutputStream(file);
								os.write(content);
							} finally {
								if (os != null) {
									os.close();
								}
							}
						} catch (Exception e) {
							throw new RuntimeException(e);
						}
						return webPath+fileName;
					}else{
						String pic64 = Base64.getEncoder().encodeToString(content);// 转成Base64
						return  "data:image/" + fileType + ";base64," + pic64;
					}
				}
			});
			wordToHtmlConverter.processDocument(wordDocument);
			// 获取xml文档
			document = wordToHtmlConverter.getDocument();
			parseImg();
		} finally {
			if (is != null) {
				is.close();
			}
		}
	}
	
	/**
	 * 解析docx文档
	 * 
	 * @throws Exception
	 */
	private void parseDocx() throws Exception {
		ByteArrayOutputStream baOut = null;
		try {
			XWPFDocument xwpfDocument = new XWPFDocument(is);
			// 创建html转换器并设置缩进
			XHTMLOptions options = XHTMLOptions.create();
			// 获取html数据
			baOut = new ByteArrayOutputStream();
			XHTMLConverter.getInstance().convert(xwpfDocument, baOut, options);
			document = new W3CDom().fromJsoup(Jsoup.parse(baOut.toString()));
			parseDocxImg(xwpfDocument);
		} finally {
			if (is != null) {
				is.close();
			}
			if (baOut != null) {
				baOut.close();
			}
		}
	}

	/**
	 * 解析文档图片
	 * 
	 * @param picMap
	 */
	private void parseImg() {
		NodeList imgs = document.getElementsByTagName("img");
		for (int i = 0; i < imgs.getLength(); i++) {
			Element item = (Element) imgs.item(i);
			if(!isDoc){
				String src = item.getAttribute("src");
				String imgPath = picMap.get(src);
				if(imgPath!=null && !"".equals(imgPath)){
					item.setAttribute("src",imgPath);
				}
			}
			String height;
			String width;
			String temp;
			if (isDoc) {
				String style = item.getAttribute("style");
				String[] split = style.split(";");
				String[] heightStyle = split[0].split(":");
				String[] widthStyle = split[1].split(":");
				height = Math.ceil(Double.parseDouble(heightStyle[1].replace("in", "")) * 96) + "px";
				width = Math.ceil(Double.parseDouble(widthStyle[1].replace("in", "")) * 96) + "px";
				if (!"height".equals(heightStyle[0])) {
					temp = height;
					height = width;
					width = temp;
				}
			} else {
				height = Math.ceil(Double.parseDouble(item.getAttribute("height").replace("pt", "")) * 4 / 3) + "px";
				width = Math.ceil(Double.parseDouble(item.getAttribute("width").replace("pt", "")) * 4 / 3) + "px";
			}
			item.setAttribute("height", height);
			item.setAttribute("width", width);
		}
	}

	private void parseDocxImg(XWPFDocument xwpfDocument) throws Exception {
		// 获取文档上的图片数据
		List<XWPFPictureData> pics = xwpfDocument.getAllPictures();
		if (pics == null) {
			return;
		}
		for (XWPFPictureData picData : pics) {
			String picType = picData.getPackagePart().getPartName().getExtension();
			String imgKey = picData.getPackagePart().getPartName().getName().substring(1);
			if (isImgFile) {
				String fileName = this.getNewFileName(picType, 6);
				File imgFile = new File(folder, fileName);
				FileUtils.writeByteArrayToFile(imgFile, picData.getData());
				picMap.put(imgKey, webPath + fileName);
			} else {
				String pic64 = Base64.getEncoder().encodeToString(picData.getData());// 转成Base64
				picMap.put(imgKey, "data:image/" + picType + ";base64," + pic64);
			}
		}
		parseImg();
	}

//	/**
//	 * 处理Doc的图片 将图片转换成64位编码，插入的文档中
//	 * 
//	 * @param pics
//	 * @param document
//	 * @throws Exception
//	 */
//	private void parseDocImg(HWPFDocument wordDocument) throws Exception {
//		// 获取文档上的所有图片
//		List<Picture> pics = wordDocument.getPicturesTable().getAllPictures();
//		if (pics == null) {
//			return;
//		}
//		ByteArrayOutputStream baops = new ByteArrayOutputStream();
//		for (Picture pic : pics) {
//			baops.reset();// 清空流
//			pic.writeImageContent(baops);// 将图片写入二进制流中
//			String picName = pic.suggestFullFileName();// 获取文件名
//			String fileType = getFileType(picName);
//			if (isImgFile) {
//				String fileName = getNewFileName(fileType, 6);
//				File imgFile = new File(folder, fileName);
//				FileUtils.writeByteArrayToFile(imgFile, baops.toByteArray());
//				picMap.put(picName, webPath + fileName);
//			} else {
//				String pic64 = Base64.getEncoder().encodeToString(baops.toByteArray());// 转成Base64
//				picMap.put(picName, "data:image/" + fileType + ";base64," + pic64);
//			}
//		}
//		if (picMap.size() > 0) {
//			parseImg();
//		}
//	}

	/**
	 * 获取文件类型
	 * 
	 * @param picName
	 *            文件名称
	 * @return
	 */
	public static String getFileType(String picName) {
		int i = picName.lastIndexOf(".");
		return picName.substring(i + 1);
	}

	/**
	 * 格式化Document到HTML
	 * 
	 * @return
	 * @throws TransformerFactoryConfigurationError
	 * @throws TransformerException
	 * @throws IOException
	 */
	private String formatDocument() throws TransformerFactoryConfigurationError, TransformerException, IOException {
		StringWriter sw = new StringWriter();
		try {
			// 获取XML转换器
			Transformer transFormer = TransformerFactory.newInstance().newTransformer();
			// 设置转换的输出属性。
			transFormer.setOutputProperty(OutputKeys.METHOD, "html");
			transFormer.setOutputProperty(OutputKeys.INDENT, "yes");
			transFormer.setOutputProperty(OutputKeys.ENCODING, "utf-8");
			// XSLT 要求名称空间支持
			DOMSource domSource = new DOMSource(document);
			transFormer.transform(domSource, new StreamResult(sw));
			return sw.toString();
		} finally {
			if (sw != null) {
				sw.close();
			}
		}
	}

	/**
	 * 获取css
	 * 
	 * @param jdom
	 * @return
	 * @throws IOException
	 */
	private Map<String, String> parseCss(org.jsoup.nodes.Document jdom) throws IOException {
		Elements styleDoms = jdom.getElementsByTag("style");
		Map<String, String> cssMap = new HashMap<String, String>();
		StringBuffer styleText = new StringBuffer();
		for (org.jsoup.nodes.Element styleDom : styleDoms) {
			styleText.append(styleDom.html());
		}
		ByteArrayInputStream bis = new ByteArrayInputStream(styleText.toString().getBytes("UTF-8"));
		InputSource source = new InputSource();
		source.setByteStream(bis);
		source.setEncoding("UTF-8");
		final CSSOMParser parser = new CSSOMParser();
		CSSStyleSheet sheet = parser.parseStyleSheet(source, null, null);
		CSSRuleList cr = sheet.getCssRules();
		for (int i = 0; i < cr.getLength(); i++) {
			CSSRule rule = cr.item(i);
			if (rule instanceof CSSStyleRule) {
				CSSStyleRule cssrule = (CSSStyleRule) rule;
				cssMap.put(cssrule.getSelectorText(), cssrule.getStyle().toString());
			}
		}
		return cssMap;
	}

	/**
	 * 格式化HTML处理style
	 * 
	 * @param html
	 * @return
	 * @throws IOException
	 */
	private String parseHTML(String html) throws IOException {
		org.jsoup.nodes.Document jdom = Jsoup.parse(html);
		Elements contentDom = jdom.getElementsByTag("body");
		Map<String, String> cssMap = parseCss(jdom);
		Set<String> keySet = cssMap.keySet();
		for (String key : keySet) {
			Elements doms = contentDom.select(key);
			for (org.jsoup.nodes.Element dom : doms) {
				String style = dom.attr("style");
				if (style == null) {
					style = "";
				}
				dom.attr("style", style + cssMap.get(key));
			}
		}
		if (isDoc) {
			html = contentDom.html();
		} else {
			Elements pdoms = jdom.getElementsByTag("p");
			pdoms.attr("style", "text-align: start;");
			html = contentDom.select("div").get(0).html();
		}
		return html;
	}

	/**
	 * 获取转换好的HTML文档的内容
	 * 
	 * @return
	 * @throws Exception
	 */
	public String getHTML() throws Exception {
		if (isDoc) {
			parseDoc();
		} else {
			parseDocx();
		}
		String html = formatDocument();
		return parseHTML(html);
	}

	/**
	 * 写入HTML到文件
	 * 
	 * @param file
	 * @throws Exception
	 */
	public void writeHTMLToFile(File file) throws Exception {
		String html = getHTML();
		FileUtils.write(file, html);
	}

	/**
	 * 写入HTML到输出流
	 * 
	 * @param os
	 * @throws Exception
	 */
	public void writeHTMLToOutputStream(OutputStream os) throws Exception {
		String html = getHTML();
		IOUtils.write(html, os);
	}

	public static void main(String[] args) throws Exception {
		File file = new File("E:/Test/2.docx");
		File file1 = new File("E:/Test/3.html");
		File file2 = new File("E:/Test/2.doc");
		File file3 = new File("E:/Test/2.html");
		Word2HtmlUtil instance = Word2HtmlUtil.getInstance(file);
		instance.writeHTMLToFile(file1);
		Word2HtmlUtil instance2 = Word2HtmlUtil.getInstance(file2);
		instance2.writeHTMLToFile(file3);
	}
}