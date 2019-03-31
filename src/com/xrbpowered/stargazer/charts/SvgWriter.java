package com.xrbpowered.stargazer.charts;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

public class SvgWriter {

	public final PrintStream out;
	
	public SvgWriter(File file) throws IOException {
		out = new PrintStream(file);
	}
	
	public void beginSvg(double pageWidth, double pageHeight) {
		out.println("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>");
		out.printf("<svg xmlns=\"http://www.w3.org/2000/svg\"\n"+
				"\txmlns:inkscape=\"http://www.inkscape.org/namespaces/inkscape\"\n"+
				"\twidth=\"%.0f\" height=\"%.0f\" viewBox=\"0 0 %.0f %.0f\">\n",
				pageWidth, pageHeight, pageWidth, pageHeight);
	}
	
	public void endSvg() {
		out.print("</svg>\n");
		out.close();
	}
	
	public void beginLayer(String id, double tx, double ty, String clip) {
		out.printf("<g id=\"%s\" inkscape:label=\"%s\" inkscape:groupmode=\"layer\" transform=\"translate(%.3f,%.3f)\"%s>\n", id, id, tx, ty,
				clip==null ? "" : String.format(" clip-path=\"url(#%s)\"", clip));
	}

	public void beginLayer(String id, double tx, double ty) {
		beginLayer(id, tx, ty, null);
	}

	public void endLayer() {
		out.print("</g>\n");
	}
	
	private String css(String id, String cls, String style) {
		String s = "";
		if(id!=null)
			s += String.format(" id=\"%s\"", id);
		if(cls!=null)
			s += String.format(" class=\"%s\"", cls);
		if(style!=null)
			s += String.format(" style=\"%s\"", style);
		return s;
	}
	
	public void rect(double x, double y, double w, double h, String id, String cls, String style) {
		out.printf("<rect%s x=\"%.3f\" y=\"%.3f\" width=\"%.3f\" height=\"%.3f\"/>\n", css(id, cls, style), x, y, w, h);
	}

	public void rect(double x, double y, double w, double h, String cls, String style) {
		rect(x, y, w, h, null, cls, style);
	}

	public void rect(double x, double y, double w, double h) {
		rect(x, y, w, h, null, null);
	}

	public void line(double x1, double y1, double x2, double y2, String id, String cls, String style) {
		out.printf("<line%s x1=\"%.3f\" y1=\"%.3f\" x2=\"%.3f\" y2=\"%.3f\"/>\n", css(id, cls, style), x1, y1, x2, y2);
	}

	public void line(double x1, double y1, double x2, double y2, String cls, String style) {
		line(x1, y1, x2, y2, null, cls, style);
	}

	public void line(double x1, double y1, double x2, double y2) {
		line(x1, y1, x2, y2, null, null);
	}

	public void circle(double cx, double cy, double  r, String id, String cls, String style) {
		out.printf("<circle%s cx=\"%.3f\" cy=\"%.3f\" r=\"%.3f\"/>\n", css(id, cls, style), cx, cy, r);
	}

	public void circle(double cx, double cy, double  r, String cls, String style) {
		circle(cx, cy, r, null, cls, style);
	}

	public void circle(double cx, double cy, double  r) {
		circle(cx, cy, r, null, null);
	}

	public void text(double x, double y, double dy, String text, String id, String cls, String style) {
		out.printf("<text%s x=\"%.3f\" y=\"%.3f\" dy=\"%.1fem\">%s</text>\n", css(id, cls, style), x, y, dy, text);
	}

	public void text(double x, double y, double dy, String text, String cls, String style) {
		text(x, y, dy, text, null, cls, style);
	}

	public void text(double x, double y, double dy, String text) {
		text(x, y, dy, text, null, null);
	}

	public void text(double x, double y, String text, String id, String cls, String style) {
		text(x, y, 0.33, text, id, cls, style);
	}

	public void text(double x, double y, String text, String cls, String style) {
		text(x, y, 0.33, text, null, cls, style);
	}

	public void text(double x, double y, String text) {
		text(x, y, 0.33, text, null, null);
	}

}
