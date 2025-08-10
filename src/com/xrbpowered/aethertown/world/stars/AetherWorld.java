package com.xrbpowered.aethertown.world.stars;

import java.util.ArrayList;

import com.xrbpowered.aethertown.utils.LegacyRand;
import com.xrbpowered.aethertown.utils.MmixRand;
import com.xrbpowered.aethertown.utils.Rand;
import com.xrbpowered.stargazer.data.Star;
import com.xrbpowered.stargazer.data.World;
import com.xrbpowered.stargazer.data.XmlReader;

public class AetherWorld extends World {

	public boolean legacyRandom = false;
	public boolean improved = false;
	
	public AetherWorld() {
		seed = 0L;
	}
	
	public AetherWorld(long seed, boolean legacy, boolean improved) {
		this.seed = seed;
		this.legacyRandom = legacy;
		this.improved = improved;
	}
	
	@Override
	public void setOption(String key, String value) {
		switch(key) {
			case "legacyRandom":
				legacyRandom = XmlReader.toBool(value, legacyRandom);
				break;
			case "improved":
				improved = XmlReader.toBool(value, improved);
				break;
		}
	}
	
	public float[] createStarData() {
		return starsToData(listStars(8));
	}
	
	public ArrayList<Star> listStars(double minMag) {
		Rand rand = legacyRandom ? new LegacyRand(seed) : new MmixRand(seed);
		return AetherStarData.generate(rand, minMag, improved);
	}
}
