package com.xrbpowered.stargazer.data;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class Hipparcos extends World {

	public String dataPath = "hip2.bin";
	
	public Hipparcos() {
		seed = 0L;
	}
	
	@Override
	public void setOption(String key, String value) {
		switch(key) {
			case "dataPath":
				dataPath = value;
				break;
		}
	}
	
	@Override
	public float[] createStarData() {
		return starsToData(load(dataPath, 8));
	}

	public static ArrayList<Star> load(String path, double minMag) {
		try {
			Scanner in = new Scanner(new File(path));
			System.out.println("Loading...");
			
			ArrayList<Star> stars = new ArrayList<>();
			while(in.hasNextLine()) {
				String[] s = in.nextLine().split("\\s+", 28);
				int offs = s[0].isEmpty() ? 0 : -1;
				double asc = Double.parseDouble(s[5+offs]);
				double decl = Double.parseDouble(s[6+offs]);
				double mag = Float.parseFloat(s[20+offs]);
				float c = Float.parseFloat(s[24+offs]);
				c *= 1.615f;
				double temp = 4600f*(1f/(0.92f*c+1.7f)+1f/(0.92f*c+0.62f));
				if(mag<minMag)
					stars.add(new Star(asc, decl, mag, temp));
			}
			
			in.close();
			System.out.println("Done");
			return stars;
		}
		catch(NumberFormatException e) {
			e.printStackTrace();
			return null;
		}
		catch(IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public ArrayList<Star> listStars(double minMag) {
		return load(dataPath, minMag);
	}
}
