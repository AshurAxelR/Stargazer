package com.xrbpowered.stargazer.data;

import java.util.ArrayList;

import org.w3c.dom.Element;

public class Constellation {

	public static class Line {
		public int id1, id2;
		public Line(int id1, int id2) {
			this.id1 = id1;
			this.id2 = id2;
		}
	}
	
	public static class VBorder {
		public double a, d1, d2;
		public VBorder(double a, double d1, double d2) {
			this.a = a;
			this.d1 = Math.min(d1, d2);
			this.d2 = Math.max(d1, d2);
		}
	}

	public static class HBorder {
		public double a1, a2, d;
		public HBorder(double a1, double a2, double d) {
			this.a1 = Math.min(a1, a2);
			this.a2 = Math.max(a1, a2);
			this.d = d;
		}
		public boolean isInside(double a) {
			return a>=a1 && a<=a2;
		}
	}

	public String name = "?";
	public boolean minor = false;
	public int labelStars = 1;
	
	public ArrayList<Star> stars = new ArrayList<>();
	
	public ArrayList<Line> lines = new ArrayList<>();

	public ArrayList<HBorder> hborders = null;
	public ArrayList<VBorder> vborders = null;
	public int pole = 0;
	public boolean awrap = false;
	public double amin, amax, dmin, dmax;
	
	public double an, dn;
	
	public boolean hasBorders() {
		return hborders!=null;
	}
	
	public boolean isInside(double a, double d) {
		if(!awrap && (a<amin || a>amax))
			return false;
		int cn = 0;
		int cp = 0;
		for(HBorder b : hborders) {
			if(b.isInside(a)) {
				if(d>=b.d)
					cp++;
				else
					cn++;
			}
		}
		if(pole>0) cn++;
		else if(pole<0) cp++;
		return cn%2==1 && cp%2==1;
	}
	
	public void parseLines(Element le) {
		if(le==null)
			return;
		String[] strips = le.getTextContent().split("\\;\\s*");
		for(String strip : strips) {
			String s[] = strip.trim().split("\\s+");
			if(s.length<2)
				continue;
			int prev = Integer.parseInt(s[0]);
			for(int i=1; i<s.length; i++) {
				int id = Integer.parseInt(s[i]);
				lines.add(new Line(prev, id));
				prev = id;
			}
		}
	}
	
	public void parseBorder(Element be) {
		if(be==null)
			return;
		String[] items = be.getTextContent().trim().split("\\s+");
		if(items.length<2)
			return;
		
		hborders = new ArrayList<>();
		vborders = new ArrayList<>();
		double as = World.parseAsc(be.getAttribute("as"));
		double ds = World.parseDecl(be.getAttribute("ds"));
		
		String poleAttr = XmlReader.attr(be, "pole", "");
		if(poleAttr.equalsIgnoreCase("N"))
			pole = 1;
		else if(poleAttr.equalsIgnoreCase("S"))
			pole = -1;
		
		amin = amax = as;
		dmin = dmax = ds;
		double a = as;
		double d = ds;
		boolean h = World.isAsc(items[0]);
		for(String s : items) {
			if(World.isAsc(s)!=h)
				throw new RuntimeException("Asc/decl ordering mismatch");
			if(h) {
				double a2 = a + World.parseAsc(s);
				if(a2>=24.0) {
					a2 -= 24.0;
					awrap = true;
					hborders.add(new HBorder(a, 24.0, d));
					hborders.add(new HBorder(0.0, a2, d));
				}
				else if(a2<0.0) {
					a2 += 24.0;
					awrap = true;
					hborders.add(new HBorder(a, 0.0, d));
					hborders.add(new HBorder(24.0, a2, d));
				}
				else
					hborders.add(new HBorder(a, a2, d));
				a = a2;
			}
			else {
				double d2 = d + World.parseDecl(s);
				vborders.add(new VBorder(a, d, d2));
				d = d2;
			}
			if(a<amin) amin = a;
			if(a>amax) amax = a;
			if(d<dmin) dmin = d;
			if(d>dmax) dmax = d;
			h = !h;
		}
		if(h) {
			if(a!=as) hborders.add(new HBorder(a, as, d));
			if(d!=ds) vborders.add(new VBorder(as, d, ds));
		}
		else {
			if(d!=ds) vborders.add(new VBorder(a, d, ds));
			if(a!=as) hborders.add(new HBorder(a, as, ds));
		}
		an = (amax+amin)/2.0;
		dn = (dmax+dmin)/2.0;
	}
	
}
