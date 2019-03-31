package com.xrbpowered.stargazer.charts;

import java.awt.geom.Point2D;

public class CylinderProjection implements Projection{
	
	private final int dspan;
	private final double margin, w, h, hd, pw, ph, tx, ty;

	public CylinderProjection(int dspan) {
		this.dspan = dspan;
		margin = StarChart.margin;
		w = StarChart.dAsc*24.0;
		h = StarChart.dDecl*dspan*2.0;
		hd = h/2.0;
		pw = w + margin*2.0;
		ph = h + margin*2.0;
		tx = margin;
		ty = ph/2.0;
	}

	@Override
	public double getPageWidth() {
		return pw;
	}
	
	@Override
	public double getPageHeight() {
		return ph;
	}
	
	@Override
	public double getTx() {
		return tx;
	}
	
	@Override
	public double getTy() {
		return ty;
	}
	
	@Override
	public void writeClip(SvgWriter svg) {
		svg.rect(0, -hd, w, h);
	}
	
	@Override
	public void writeGrid(SvgWriter svg) {
		svg.rect(0, -hd, w, h, "bg", null, null);
		String gcls = StarChart.devMode ? "gd" : "g";
		for(int a=0; a<=24; a++) {
			double x = a*StarChart.dAsc; 
			svg.line(x, -hd, x, hd, gcls, null);
			String s = String.format("%dh", (24-a)%24);
			svg.text(x, -hd - margin/2.0, s);
			svg.text(x, hd + margin/2.0, s);
			if(StarChart.devMode) {
				for(int ai=1; ai<12; ai++) {
					x = (a+ai/12.0)*StarChart.dAsc; 
					svg.line(x, -hd, x, hd, "g0", null);
				}
			}
		}
		for(int d=-dspan; d<dspan; d+=10) {
			double y =d*StarChart.dDecl; 
			svg.line(0.0, y, w, y, d==0 ? "geq" : gcls, null);
			String s = d==0 ? "0&#x00B0;" : String.format("%+d&#x00B0;", -d);
			svg.text(-margin/2.0, y, s);
			svg.text(w+margin/2.0, y, s);
			if(StarChart.devMode) {
				for(int di=1; di<10; di++) {
					y =(d+di)*StarChart.dDecl; 
					svg.line(0.0, y, w, y, "g0", null);
				}
			}
		}
		svg.rect(0, -hd, w, h, "frame", null, null);
	}
	
	@Override
	public Point2D.Double pos(double a, double d) {
		return new Point2D.Double(
			(24.0-a)*StarChart.dAsc,
			-d*StarChart.dDecl
		);
	}
	
	@Override
	public boolean isOutside(double a, double d) {
		return d<-dspan || d>dspan;
	}
	
	@Override
	public void line(SvgWriter svg, java.awt.geom.Point2D.Double pos1, java.awt.geom.Point2D.Double pos2, int level) {
		String cls = "l"+level;
		if(Math.abs(pos1.x-pos2.x)>w/2.0) {
			if(pos1.x>pos2.x) {
				svg.line(pos1.x-w, pos1.y, pos2.x, pos2.y, cls, null);
				svg.line(pos1.x, pos1.y, pos2.x+w, pos2.y, cls, null);
			}
			else {
				svg.line(pos1.x+w, pos1.y, pos2.x, pos2.y, cls, null);
				svg.line(pos1.x, pos1.y, pos2.x-w, pos2.y, cls, null);
			}
		}
		else {
			svg.line(pos1.x, pos1.y, pos2.x, pos2.y, cls, null);
		}
	}
	
	@Override
	public void vline(SvgWriter svg, double a, double d1, double d2) {
		double x = (24.0-a)*StarChart.dAsc;
		double y1 = -d1*StarChart.dDecl;
		double y2 = -d2*StarChart.dDecl;
		svg.line(x, y1, x, y2);
	}
	
	@Override
	public void hline(SvgWriter svg, double a1, double a2, double d) {
		double x1 = (24.0-a1)*StarChart.dAsc;
		double x2 = (24.0-a2)*StarChart.dAsc;
		double y = -d*StarChart.dDecl;
		svg.line(x1, y, x2, y);
	}
}