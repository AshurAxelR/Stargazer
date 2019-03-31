package com.xrbpowered.stargazer.data;

import java.awt.geom.Point2D;

import org.lwjgl.util.vector.Vector3f;

import com.xrbpowered.stargazer.charts.StarChart;

public class Star implements Comparable<Star> {
	
	public int rank = 0;
	public Constellation con = null;
	public int conRank = 0;
	
	public String name = null;
	public String altName = null;
	
	public double asc; // right ascention (0h .. 23h)
	public double decl; // declination (-90 .. +90)
	public double r;
	public double mag;
	public double temp;
	
	public boolean outside;
	public Point2D.Double pos;
	
	public Star() {
	}
	
	public Star(double mag, float[] data, int offs) {
		this.mag = mag;
		this.temp = data[offs+4];
		
		Vector3f v = new Vector3f(data[offs], data[offs+1], data[offs+2]);
		v.scale(1f/30f);
		this.asc = Math.atan2(v.z, v.y) * 12.0 / Math.PI + 12.0;
		this.decl = 90.0 - Math.acos(Vector3f.dot(v, new Vector3f(1, 0, 0))) * 180.0 / Math.PI;
		
		double rf = data[offs+3];
		rf = StarChart.circleScale * rf * rf;
		this.r = rf / Math.sqrt(Math.PI);
	}
	
	@Override
	public int compareTo(Star o) {
		return Double.compare(this.mag, o.mag);
	}
	
	public int magLevel() {
		return mag<=3.0 ? 3 : mag<=3.85 ? 2 : 1;
	}
	
	public static double apMag(double rf) {
		return 7.0-Math.pow(rf, 1.5)*3.4;
	}

}