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
	
	@Override
	public void setOption(String key, String value) {
		switch(key) {
			case "legacyRandom":
				legacyRandom = XmlReader.toBool(value, legacyRandom);
				break;
			default:
				super.setOption(key, value);
		}
	}
	
	public float[] createStarData() {
		throw new UnsupportedOperationException();
	}
	
	public ArrayList<Star> listStars(double minMag) {
		Rand rand = legacyRandom ? new LegacyRand(seed) : new MmixRand(seed);
		return AetherStarData.generate(rand, minMag);
	}
}
