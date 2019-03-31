package com.xrbpowered.stargazer.charts;

import java.awt.geom.Point2D;
import java.io.File;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.math.MathContext;
import java.security.InvalidParameterException;
import java.util.ArrayList;

import org.w3c.dom.Element;

import com.xrbpowered.stargazer.BlackBodySpectrum;
import com.xrbpowered.stargazer.StarField;
import com.xrbpowered.stargazer.Stargazer;
import com.xrbpowered.stargazer.data.Constellation;
import com.xrbpowered.stargazer.data.OptionParser;
import com.xrbpowered.stargazer.data.Star;
import com.xrbpowered.stargazer.data.World;
import com.xrbpowered.stargazer.data.XmlReader;
import com.xrbpowered.utils.assets.AssetManager;

public class StarChart {

	public static final String[] greekLetters = {
		"&#x03B1;", "&#x03B2;", "&#x03B3;", "&#x03B4;", "&#x03B5;",
		"&#x03B6;", "&#x03B7;", "&#x03B8;", "&#x03B9;", "&#x03BA;", "&#x03BB;",
		"&#x03BC;", "&#x03BD;", "&#x03BE;", "&#x03BF;", "&#x03C0;",
		"&#x03C1;", "&#x03C2;", "&#x03C4;", "&#x03C5;", "&#x03C6;",
		"&#x03C7;", "&#x03C8;", "&#x03C9;"
	};
	
	public static String htmlName = "stars.html";
	public static String devPrefix = "dev_";
	public static String svgCylinderName = "map_cylinder.svg";
	public static String svgNHemiName = "map_nhemi.svg";
	public static String svgSHemiName = "map_shemi.svg";
	
	public static double circleScale = 12.0 * 0.5 * 0.85;
	public static double dAsc = 690.0 * 0.5;
	public static double dDecl = 45.5 * 0.5;
	public static double margin = 40.0;
	public static double starLabelGap = 3.0;

	public static boolean devMode = false;
	public static double starIdMag = 4.4;

	public final World world;
	
	public final ArrayList<Star> stars = new ArrayList<>();
	
	public StarChart(World world, double minMag) {
		this.world = world;
		
		float[] data = world.createStarData();
		for(int offs=0; offs<data.length; offs+=StarField.SKIP) {
			double mag = Star.apMag(data[offs+3]);
			if(mag<minMag) {
				stars.add(new Star(mag, data, offs));
			}
		}
		stars.sort(null);
		int rank = 1;
		for(Star s : stars) {
			s.rank = rank;
			
			Star ks = world.knownStars.get(rank);
			if(ks!=null) {
				s.name = ks.name;
				s.altName = ks.altName;
			}
			s.con = world.findConstellation(s.asc, s.decl);
			if(s.con!=null) {
				s.con.stars.add(s);
				s.conRank = s.con.stars.size(); 
			}
			
			rank++;
		}
		System.out.printf("Star data created: %d stars\n", stars.size());
	}
	
	public Star star(int id) {
		return id<=0 || id>stars.size() ? null : stars.get(id-1);
	}
	
	public static double round(double x, int signDigits) {
		BigDecimal bd = new BigDecimal(x);
		bd = bd.round(new MathContext(signDigits));
		return bd.doubleValue();
	}
	
	public String htmlTableBrightest(int num) {
		StringBuilder sb = new StringBuilder();

		sb.append(String.format("<p>List of %d brightest stars:</p>\n", num));

		sb.append("<table>\n");
		sb.append("<thead><tr>");
		sb.append("<th>Rank</th>");
		sb.append("<th>AMag</th>");
		sb.append("<th>Proper name</th>");
		sb.append("<th>Designation</th>");
		sb.append("<th style=\"text-align:right\">RA</th>");
		sb.append("<th style=\"text-align:right\">Dec</th>");
		sb.append("<th style=\"text-align:right\">Temp</th>");
		sb.append("<th style=\"text-align:center\">Class</th>");
		sb.append("</tr></thead>\n<tbody>\n\n");
		
		for(int i=0; i<num; i++) {
			Star s = stars.get(i);
			sb.append("<tr>\n");
			sb.append(String.format("<td>%d</td>", i+1));
			sb.append(String.format("<td>%+.2f</td>\n", s.mag));
			if(s.name!=null) {
				if(s.altName!=null)
					sb.append(String.format("<td>%s (%s)</td>", s.name, s.altName));
				else
					sb.append(String.format("<td>%s</td>", s.name));
			}
			else
				sb.append("<td>  </td>");
			if(s.con!=null) {
				if(s.conRank<=greekLetters.length)
					sb.append(String.format("<td>%s %s</td>\n", greekLetters[s.conRank-1], s.con.name));
				else
					sb.append(String.format("<td>- %s</td>\n", s.con.name));
			}
			else
				sb.append("<td>  </td>\n");
			int asc = (int)Math.round(s.asc*60.0);
			sb.append(String.format("<td style=\"text-align:right\">%dh&nbsp;%dm</td>", asc/60, asc%60));
			sb.append(String.format("<td style=\"text-align:right\">%+.2f&deg;</td>", s.decl));
			sb.append(String.format("<td style=\"text-align:right\">%.0f</td>", round(s.temp, 2)));
			sb.append(String.format("<td style=\"text-align:center\">%s</td>\n", BlackBodySpectrum.getSpectralClass(s.temp)));
			sb.append("</tr>\n\n");
		}
		sb.append("</tbody>\n</table>\n");
		return sb.toString();
	}
	
	public void saveStarsHtml(String path, int num) {
		try {
			File file = new File(path, htmlName);
			
			String html = AssetManager.defaultAssets.loadString("config/stars_template.html");
			String table = htmlTableBrightest(num);
			html = html.replaceAll("%TITLE%", world.title);
			html = html.replaceAll("%SEED%", Long.toString(world.seed));
			html = html.replaceAll("%TABLE_STARS%", table);
			
			PrintStream out = new PrintStream(file);
			out.print(html);
			out.close();
			
			System.out.println(htmlName+" saved.");
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}

	public void saveSvg(String path, String name, Projection proj) {
		try {
			if(devMode)
				name = devPrefix + name;
			File file = new File(path, name);
			
			SvgWriter svg = new SvgWriter(file);
			svg.beginSvg(proj.getPageWidth(), proj.getPageHeight());

			svg.out.printf("<style>\n");
			String css = AssetManager.defaultAssets.loadString("config/map_svg.css");
			svg.out.print(css);
			svg.out.printf("</style>\n");
			
			svg.out.printf("<clipPath id=\"board\">\n");
			proj.writeClip(svg);
			svg.out.printf("</clipPath>\n");
			
			svg.beginLayer("grid", proj.getTx(), proj.getTy());
			proj.writeGrid(svg);
			svg.endLayer();
			
			for(Star s : stars) {
				s.outside = proj.isOutside(s.asc, s.decl);
				s.pos = proj.pos(s.asc, s.decl);
			}
			
			svg.beginLayer("lines", proj.getTx(), proj.getTy(), "board");
			for(Constellation con : world.constellations)
				for(Constellation.Line l : con.lines) {
					Star s1 = star(l.id1);
					Star s2 = star(l.id2);
					if(s1!=null && s2!=null && !(s1.outside && s2.outside)) {
						proj.line(svg, s1.pos, s2.pos, Math.min(s1.magLevel(), s2.magLevel()));
					}
				}
			svg.endLayer();
			
			svg.beginLayer("stars", proj.getTx(), proj.getTy());
			for(Star s : stars) {
				if(s.outside)
					continue;
				svg.circle(s.pos.x, s.pos.y, s.r, "s"+s.rank, null, devMode && s.con==null ? "fill:#a00" : null);
				if(devMode && s.mag<starIdMag) {
					svg.text(s.pos.x, s.pos.y + s.r + starLabelGap, 0.67, Integer.toString(s.rank), "t"+s.magLevel(), null);
				}
			}
			svg.endLayer();

			svg.beginLayer("borders", proj.getTx(), proj.getTy(), "board");
			for(Constellation con : world.constellations)
				if(con.hasBorders()) {
					for(Constellation.HBorder b : con.hborders) {
						if(proj.isOutside(b.a1, b.d) && proj.isOutside(b.a1, b.d))
							continue;
						proj.hline(svg, b.a1, b.a2, b.d);
					}
					for(Constellation.VBorder b : con.vborders) {
						if(proj.isOutside(b.a, b.d1) && proj.isOutside(b.a, b.d2))
							continue;
						proj.vline(svg, b.a, b.d1, b.d2);
					}
				}
			svg.endLayer();

			if(!devMode) {
				svg.beginLayer("greek", proj.getTx(), proj.getTy());
				for(Star s : stars) {
					if(s.outside || s.con==null || s.conRank>s.con.labelStars || s.conRank>greekLetters.length)
						continue;
					svg.text(s.pos.x, s.pos.y + s.r + starLabelGap, 0.67, greekLetters[s.conRank-1], s.magLevel()>2 ? "t3" : "t2", null);
				}
				svg.endLayer();
			}

			svg.beginLayer("names", proj.getTx(), proj.getTy());
			for(Star s : stars) {
				if(s.outside || s.name==null)
					continue;
				svg.text(s.pos.x, s.pos.y - s.r - starLabelGap, 0.0, s.name, "s", null);
			}
			for(Constellation con : world.constellations) {
				if(proj.isOutside(con.an, con.dn))
					continue;
				Point2D.Double pos = proj.pos(con.an, con.dn);
				svg.text(pos.x, pos.y, con.name.toUpperCase(), con.minor ? "c t2" : "c t3", null);
			}
			svg.endLayer();

			svg.endSvg();
			System.out.println(name+" saved.");
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void saveCylinderSvg(String path) {
		saveSvg(path, svgCylinderName, new CylinderProjection(70));
	}
	
	public void saveHemiSvg(String path, int dir) {
		saveSvg(path, dir>0 ? svgNHemiName : svgSHemiName, new HemisphereProjection(dir, 90));
	}
	
	private static final OptionParser chartOptionParser = new OptionParser() {
		@Override
		public void setOption(String key, String value) {
			switch(key) {
				case "htmlName":
					htmlName = value;
					break;
				case "svgCylinderMap":
					svgCylinderName = value;
					break;
				case "svgNHemiMap":
					svgNHemiName = value;
					break;
				case "svgSHemiMap":
					svgSHemiName = value;
					break;
				case "devPrefix":
					devPrefix = value;
					break;
					
				case "circle":
					circleScale = XmlReader.toDouble(value, circleScale);
					break;
				case "sizeRA":
					dAsc = XmlReader.toDouble(value, dAsc);
					break;
				case "sizeDec":
					dDecl = XmlReader.toDouble(value, dDecl);
					break;
				case "margin":
					margin = XmlReader.toDouble(value, margin);
					break;
				case "starLabelGap":
					starLabelGap = XmlReader.toDouble(value, starLabelGap);
					break;
			}
		}
	};
	
	private static void loadConfig() {
		try {
			Element root = XmlReader.load(Stargazer.configPath);
			XmlReader.parseOptions(XmlReader.element(root, "chartOptions"), StarChart.chartOptionParser);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		loadConfig();
		
		String infoPath = null;
		String outPath = ".";
		
		int stars = 0;
		boolean nhemi = false;
		boolean shemi = false;
		boolean cylinder = false;
		
		double mag = 5.0;
		
		try {
			for(int i=0; i<args.length; i++) {
				switch(args[i]) {
					case "-C":
						break;
					case "-in":
						infoPath = args[++i];
						break;
					case "-out":
						outPath = args[++i];
						break;
					case "-dev":
						devMode = true;
						break;
					case "-mag":
						mag = Double.parseDouble(args[++i]);
						break;
					case "-idmag":
						starIdMag = Double.parseDouble(args[++i]);
						break;
					case "-stars":
						stars = Integer.parseInt(args[++i]);
						break;
					case "-nhemi":
						nhemi = true;
						break;
					case "-shemi":
						shemi = true;
						break;
					case "-cylinder":
						cylinder = true;
						break;
					case "-maps":
						nhemi = true;
						shemi = true;
						cylinder = true;
						break;
					default:
						throw new InvalidParameterException("Unknown option");
				}
			}
			if(infoPath==null)
				throw new InvalidParameterException("World info XML required");
			if(stars<1 && !nhemi && !shemi && !cylinder)
				throw new InvalidParameterException("No output?");
		}
		catch(Exception e) {
			System.err.printf("Option error [%s: %s]\n\n", e.getClass().getSimpleName(), e.getMessage());
			// TODO print chart mode usage
			System.exit(1);
			return;
		}
		
		World world = World.load(infoPath);
		if(world==null) {
			System.err.println("Bad world info");
			System.exit(1);
		}
		System.out.printf("World info loaded: %s\n", world.title);
		
		StarChart chart = new StarChart(world, mag);
		
		if(stars>0) chart.saveStarsHtml(outPath, stars);
		if(cylinder) chart.saveCylinderSvg(outPath);
		if(nhemi) chart.saveHemiSvg(outPath, 1);
		if(shemi) chart.saveHemiSvg(outPath, -1);
	}
}
