package com.xrbpowered.stargazer.charts;

import java.awt.geom.Point2D;

public class HemisphereProjection implements Projection {
	
	private final int dspan;
	private final int dir;
	private final double margin, size, sized, psize, tx, ty; 
	
	public HemisphereProjection(int dir, int dspan) {
		this.dspan = dspan;
		this.dir = dir;
		margin = StarChart.margin;
		size = StarChart.dDecl*dspan*2.0;
		sized = size/2.0;
		psize = size + margin*2.0;
		tx = psize/2.0;
		ty = psize/2.0;
	}
	
	@Override
	public double getPageWidth() {
		return psize;
	}
	
	@Override
	public double getPageHeight() {
		return psize;
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
		svg.circle(0, 0, sized);
	}
	
	@Override
	public void writeGrid(SvgWriter svg) {
		svg.circle(0, 0, sized, "bg", null, null);
		String gcls = StarChart.devMode ? "gd" : "g";
		for(int a=0; a<24; a++) {
			double aa = a * Math.PI / 12.0;
			double x = sized*Math.sin(aa);
			double y = -sized*Math.cos(aa);
			svg.line(0, 0, x, y, gcls, null);
			x = (sized+margin/2.0)*Math.sin(aa);
			y = -(sized+margin/2.0)*Math.cos(aa);
			String s = String.format("%dh", (24+a*dir)%24);
			svg.text(x, y, s);
			if(StarChart.devMode) {
				for(int ai=1; ai<12; ai++) {
					aa = (a + ai/12.0) * Math.PI / 12.0;
					x = sized*Math.sin(aa);
					y = -sized*Math.cos(aa);
					svg.line(0, 0, x, y, "g0", null);
				}
			}
		}
		for(int d=90; d>90-dspan; d-=10) {
			double r =(90-d)*StarChart.dDecl;
			if(d<90) {
				svg.circle(0, 0, r, gcls, null);
				String s = String.format("%+d&#x00B0;", d*dir);
				svg.text(0, -r, s);
				svg.text(0, r, s);
				svg.text(-r, 0, s);
				svg.text(r, 0, s);
			}
			if(StarChart.devMode) {
				for(int di=1; di<10; di++) {
					r =(90-d+di)*StarChart.dDecl; 
					svg.circle(0, 0, r, "g0", null);
				}
			}
		}
		svg.circle(0, 0, sized, "frame", null, null);
	}
	
	@Override
	public Point2D.Double pos(double a, double d) {
		double r = (90-d*dir)*StarChart.dDecl;
		double aa = dir* a * Math.PI / 12.0;
		return new Point2D.Double(
			r*Math.sin(aa),
			-r*Math.cos(aa)
		);
	}
	
	@Override
	public boolean isOutside(double a, double d) {
		return d*dir<90-dspan;
	}
	
	@Override
	public void line(SvgWriter svg, java.awt.geom.Point2D.Double pos1, java.awt.geom.Point2D.Double pos2, int level) {
		svg.line(pos1.x, pos1.y, pos2.x, pos2.y, "l"+level, null);
	}
	
	@Override
	public void vline(SvgWriter svg, double a, double d1, double d2) {
		double aa = dir* a * Math.PI / 12.0;
		double r = (90-d1*dir)*StarChart.dDecl;
		double x1 = r*Math.sin(aa);
		double y1 = -r*Math.cos(aa);
		r = (90-d2*dir)*StarChart.dDecl;
		double x2 = r*Math.sin(aa);
		double y2 = -r*Math.cos(aa);
		svg.line(x1, y1, x2, y2);
	}
	
	@Override
	public void hline(SvgWriter svg, double a1, double a2, double d) {
		double r = (90-d*dir)*StarChart.dDecl;
		double aa1 = dir* a1 * Math.PI / 12.0;
		double x1 = r*Math.sin(aa1);
		double y1 = -r*Math.cos(aa1);
		double aa2 = dir* a2 * Math.PI / 12.0;
		double x2 = r*Math.sin(aa2);
		double y2 = -r*Math.cos(aa2);
		svg.out.printf("<path d=\"M %.3f,%.3f A %.3f,%.3f,%.3f,%d,%d,%.3f,%.3f\" />",
				x1, y1, r, r, a2-a1, 0, dir>0 ? 1 : 0, x2, y2);
	}
}