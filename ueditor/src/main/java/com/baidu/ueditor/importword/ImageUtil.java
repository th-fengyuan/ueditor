package com.baidu.ueditor.importword;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.GZIPOutputStream;

import javax.imageio.ImageIO;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.ImageTranscoder;
import org.apache.batik.transcoder.image.JPEGTranscoder;
import org.apache.batik.transcoder.image.PNGTranscoder;
import org.apache.commons.io.FileUtils;
import org.freehep.graphicsio.emf.EMFInputStream;
import org.freehep.graphicsio.emf.EMFRenderer;
import org.w3c.dom.Document;

import net.arnx.wmf2svg.gdi.svg.SvgGdi;
import net.arnx.wmf2svg.gdi.wmf.WmfParser;

public class ImageUtil {

	public static ByteArrayOutputStream emfToPng(InputStream in) throws IOException {
		// 写入到磁盘中(格式设置为png背景不会变为橙色)
		ByteArrayOutputStream out;
		try {
			EMFInputStream eis = new EMFInputStream(in, EMFInputStream.DEFAULT_VERSION);
			EMFRenderer emfRenderer = new EMFRenderer(eis);
			final int width = (int) eis.readHeader().getBounds().getWidth();
			final int height = (int) eis.readHeader().getBounds().getHeight();
			// 设置图片的大小和样式
			final BufferedImage result = new BufferedImage(width + 60, height + 40, BufferedImage.TYPE_4BYTE_ABGR);
			Graphics2D g2 = (Graphics2D) result.createGraphics();
			emfRenderer.paint(g2);
			out = new ByteArrayOutputStream();
			ImageIO.write(result, "png", out);
		} finally {
			if (in != null) {
				in.close();
			}
		}
		return out;
	}

	public static byte[] emfToPngByteArray(InputStream in) throws IOException {
		ByteArrayOutputStream out = null;
		try {
			out = emfToPng(in);
			return out.toByteArray();
		} finally {
			if (out != null) {
				out.close();
			}
		}
	}

	public static byte[] emfToPng(byte[] data) throws IOException {
		ByteArrayOutputStream out = null;
		try {
			out = emfToPng(new ByteArrayInputStream(data));
			return out.toByteArray();
		} finally {
			if (out != null) {
				out.close();
			}
		}
	}

	public static ByteArrayOutputStream wmfToSvg(InputStream in) throws Exception {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		wmfToSvg(in, out);
		return out;
	}

	public static byte[] wmfToSvgByteArray(InputStream in) throws Exception {
		ByteArrayOutputStream out = null;
		try {
			out = wmfToSvg(in);
			return out.toByteArray();
		} finally {
			if (out != null) {
				out.close();
			}
		}
	}

	public static byte[] wmfToSvg(byte[] data) throws Exception {
		ByteArrayOutputStream out = null;
		try {
			out = wmfToSvg(new ByteArrayInputStream(data));
			return out.toByteArray();
		} finally {
			if (out != null) {
				out.close();
			}
		}
	}

	public static ByteArrayOutputStream wmfToSvgz(InputStream in) throws Exception {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		wmfToSvg(in, new GZIPOutputStream(out));
		return out;
	}

	public static byte[] wmfToSvgzByteArray(InputStream in) throws Exception {
		ByteArrayOutputStream out = null;
		try {
			out = wmfToSvgz(in);
			return out.toByteArray();
		} finally {
			if (out != null) {
				out.close();
			}
		}
	}

	public static byte[] wmfToSvgz(byte[] data) throws Exception {
		ByteArrayOutputStream out = null;
		try {
			out = wmfToSvgz(new ByteArrayInputStream(data));
			return out.toByteArray();
		} finally {
			if (out != null) {
				out.close();
			}
		}
	}

	private static void wmfToSvg(InputStream in, OutputStream out) throws Exception {
		try {
			WmfParser parser = new WmfParser();
			final SvgGdi gdi = new SvgGdi(false);
			parser.parse(in, gdi);
			Document doc = gdi.getDocument();
			TransformerFactory factory = TransformerFactory.newInstance();
			Transformer transformer = factory.newTransformer();
			transformer.setOutputProperty(OutputKeys.METHOD, "xml");
			transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty(OutputKeys.DOCTYPE_PUBLIC, "-//W3C//DTD SVG 1.0//EN");
			transformer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM,
					"http://www.w3.org/TR/2001/REC-SVG-20010904/DTD/svg10.dtd");
			transformer.transform(new DOMSource(doc), new StreamResult(out));
		} finally {
			if (in != null) {
				in.close();
			}
		}
	}

	public static ByteArrayOutputStream svgToJpg(InputStream in) throws IOException, TranscoderException {
		ByteArrayOutputStream out;
		try {
			out = new ByteArrayOutputStream();
			ImageTranscoder it = new PNGTranscoder();
			it.addTranscodingHint(JPEGTranscoder.KEY_QUALITY, new Float(1f));
			it.addTranscodingHint(ImageTranscoder.KEY_WIDTH, new Float(500));
			it.transcode(new TranscoderInput(in), new TranscoderOutput(out));
		} finally {
			if (in != null) {
				in.close();
			}
		}
		return out;
	}

	public static byte[] svgToJpgByteArray(InputStream in) throws Exception {
		ByteArrayOutputStream out = null;
		try {
			out = svgToJpg(in);
			return out.toByteArray();
		} finally {
			if (out != null) {
				out.close();
			}
		}
	}
	public static byte[] svgToJpg(byte[] data) throws Exception {
		ByteArrayOutputStream out = null;
		try {
			out = svgToJpg(new ByteArrayInputStream(data));
			return out.toByteArray();
		} finally {
			if (out != null) {
				out.close();
			}
		}
	}

	public static ByteArrayOutputStream wmfToJpg(InputStream in) throws Exception {
		byte[] wmfToSvgByteArray = wmfToSvgByteArray(in);
		return svgToJpg(new ByteArrayInputStream(wmfToSvgByteArray));

	}

	public static byte[] wmfToJpgByteArray(InputStream in) throws Exception {
		byte[] wmfToSvgByteArray = wmfToSvgByteArray(in);
		return svgToJpgByteArray(new ByteArrayInputStream(wmfToSvgByteArray));

	}
	public static byte[] wmfToJpg(byte[] data) throws Exception {
		byte[] wmfToSvgByteArray = wmfToSvg(data);
		return svgToJpg(wmfToSvgByteArray);
		
	}

	public static void main(String[] args) throws Exception {
		FileInputStream in = new FileInputStream("E:/Test/0002.wmf");
		ByteArrayOutputStream out = wmfToSvg(in);
		ByteArrayOutputStream jpg = svgToJpg(new ByteArrayInputStream(out.toByteArray()));
		FileUtils.writeByteArrayToFile(new File("E:/Test/0002.jpg"), jpg.toByteArray());
	}
}
