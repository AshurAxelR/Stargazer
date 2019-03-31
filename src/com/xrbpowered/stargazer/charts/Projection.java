package com.xrbpowered.stargazer.charts;

import java.awt.geom.Point2D;

public interface Projection {
	
	public double getPageWidth();
	public double getPageHeight();
	public double getTx();
	public double getTy();
	
	public void writeClip(SvgWriter svg);
	public void writeGrid(SvgWriter svg);
	
	public Point2D.Double pos(double a, double d);
	public boolean isOutside(double a, double d);
	
	public void line(SvgWriter svg, Point2D.Double pos1, Point2D.Double pos2, int level);
	public void vline(SvgWriter svg, double a, double d1, double d2);
	public void hline(SvgWriter svg, double a1, double a2, double d);
	
}