package com.xrbpowered.stargazer.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;
import org.w3c.dom.Element;

import com.xrbpowered.gl.scene.Actor;
import com.xrbpowered.stargazer.BlackBodySpectrum;
import com.xrbpowered.stargazer.StarField;

public class World implements OptionParser {
	
	public String title;
	public Long seed = null;

	public int numStars = 1000000;
	public float distributionSigma = 0.05f;
	public float distributionOffset= 0.6f;
	public float brightnessLambda = 8f;
	public float brightnessOffset = 0.05f;
	public boolean sphere = true;
	
	public HashMap<Integer, Star> knownStars = new HashMap<>();
	public ArrayList<Constellation> constellations = new ArrayList<>();

	public World() {
	}

	public World(String title, long seed) {
		this.title = title;
		this.seed = seed;
	}
	
	@Override
	public void setOption(String key, String value) {
		switch(key) {
			case "numStars":
				numStars = XmlReader.toInt(value, numStars);
				break;
			case "distributionSigma":
				distributionSigma = XmlReader.toFloat(value, distributionSigma);
				break;
			case "distributionOffset":
				distributionOffset = XmlReader.toFloat(value, distributionOffset);
				break;
			case "brightnessLambda":
				brightnessLambda = XmlReader.toFloat(value, brightnessLambda);
				break;
			case "brightnessOffset":
				brightnessOffset = XmlReader.toFloat(value, brightnessOffset);
				break;
			case "sphere":
				sphere = XmlReader.toBool(value, sphere);
				break;
		}
	}
	
	public float[] createStarData() {
		float[] starData = new float [numStars*StarField.SKIP];
		Random random = new Random(seed);

		Matrix4f m = new Matrix4f();
		Actor.rotateYawPitchRoll(new Vector3f(
				random.nextFloat()*(float)Math.PI,
				random.nextFloat()*(float)Math.PI,
				random.nextFloat()*(float)Math.PI), m);
		Vector4f v = new Vector4f();
		int offs = 0;
		for(int i=0; i<numStars; i++) {
			do {
				v.w = 0;
				v.x = random.nextFloat()*2f-1f;
				v.z = random.nextFloat()*2f-1f;
				
				float yg = (float)random.nextGaussian()*2f*distributionSigma;
				if(random.nextFloat()>(1f-distributionOffset) || yg<-1f || yg>1f)
					v.y = random.nextFloat()*2f-1f;
				else
					v.y = yg;

			} while(v.length()<=0f || sphere && v.length()>1);

			v.normalise();
			v.scale(30f);
			v.w = 1;
			Matrix4f.transform(m, v, v);
			
			float rf = (float)Math.log(1-random.nextDouble())/(-brightnessLambda)+brightnessOffset;
			//rf = exposure * rf * rf;
			//float c = (float)Math.pow(rf, contrast+1.0);
			//float r = (float)(2f*rf/Math.sqrt(Math.PI));
			
			starData[offs++] = v.x;
			starData[offs++] = v.y;
			starData[offs++] = v.z;
			starData[offs++] = rf;
			offs++; // skip temperature at this pass to preserve random seed consistency with older version 
		}
		
		offs = 4;
		for(int i=0; i<numStars; i++) {
			starData[offs] = (float) BlackBodySpectrum.randomTemp(random);
			offs+=StarField.SKIP;
		}
		return starData;
	}
	
	public Constellation findConstellation(double a, double d) {
		for(Constellation con : constellations)
			if(con.isInside(a, d))
				return con;
		return null;
	}
	
	public static boolean isAsc(String s) {
		return s.indexOf(':')>=0;
	}
	
	public static double parseAsc(String s) {
		String[] vs = s.split(":", 2);
		boolean neg = s.charAt(0)=='-';
		int h = Integer.parseInt(vs[0]);
		if(vs.length==1)
			return h;
		double m = Integer.parseInt(vs[1]) / 60.0;
		return neg ? h-m : h+m;
	}
	
	public static double parseDecl(String s) {
		return Double.parseDouble(s);
	}
	
	public static World load(World world, Element root, boolean recursive, boolean chartData) {
		try {
			if(root==null)
				return null;
			if(world==null)
				world = new World();
			
			if(root.hasAttribute("seed")) {
				String seed = XmlReader.attr(root, "seed", "");
				if(!seed.isEmpty()) {
					try {
						world.seed = Long.parseLong(seed);
					}
					catch(Exception e) {
						world.seed = (long)seed.hashCode();
					}
				}
			}
			world.title = XmlReader.attr(root, "title", "Unknown World");
			
			
			XmlReader.parseOptions(XmlReader.element(root, "gen"), world);
			
			if(chartData) {
				for(Element se : XmlReader.elements(XmlReader.element(root, "stars"), "s")) {
					int id = XmlReader.attrInt(se, "id", 0);
					String name = XmlReader.attr(se, "name", null);
					String altName = XmlReader.attr(se, "altname", null);
					if(id>0 && name!=null) {
						Star star = new Star();
						star.rank = id;
						star.name = name;
						star.altName = altName;
						world.knownStars.put(id, star);
					}
				}
	
				for(Element ce : XmlReader.elements(XmlReader.element(root, "constellations"), "con")) {
					String name = XmlReader.attr(ce, "name", null);
					if(name!=null) {
						Constellation con = new Constellation();
						con.name = name;
						con.minor = XmlReader.attrBool(ce, "minor", false);
						con.labelStars = XmlReader.attrInt(ce, "labelstars", 1);
						con.parseLines(XmlReader.element(ce, "lines"));
						con.parseBorder(XmlReader.element(ce, "border"));
						if(ce.hasAttribute("an")) con.an = parseAsc(ce.getAttribute("an"));
						if(ce.hasAttribute("dn")) con.dn = parseDecl(ce.getAttribute("dn"));
						world.constellations.add(con);
					}
				}
			}

			if(recursive && root.hasAttribute("info"))
				world = load(world, root.getAttribute("info"), chartData);
				
			if(world.seed==null)
				throw new RuntimeException("No world seed");
			return world;
		}
		catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static World load(World world, String path, boolean chartData) {
		try {
			return load(world, XmlReader.load(path), false, chartData);
		}
		catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static World load(String path) {
		return load(null, path, true);
	}
}
