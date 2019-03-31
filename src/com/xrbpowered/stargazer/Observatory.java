package com.xrbpowered.stargazer;

import java.awt.Color;
import java.util.Random;

import org.lwjgl.util.vector.Vector4f;

import com.xrbpowered.gl.examples.ExampleClient;
import com.xrbpowered.gl.examples.HeightMap;
import com.xrbpowered.gl.res.StaticMesh;
import com.xrbpowered.gl.res.builder.FastMeshBuilder;
import com.xrbpowered.gl.res.shaders.StandardShader;
import com.xrbpowered.gl.res.textures.BufferTexture;
import com.xrbpowered.gl.res.textures.Texture;
import com.xrbpowered.gl.scene.Scene;
import com.xrbpowered.gl.scene.StaticMeshActor;
import com.xrbpowered.stargazer.data.World;
import com.xrbpowered.utils.MathUtils;

public class Observatory {

	private static final int TERRAIN_SIZE = 128;

	public static int startMinLat = 20;
	public static int startMaxLat = 70;

	private Scene scene;
	private Texture terrainTexture;
	private Texture specularTexture;
	private Texture normalTexture;

	private StaticMesh terrain = null;
	private StaticMeshActor terrainActor;

	private World world = null;
	public int latitude = 45;
	public float latitudeRadians;

	public Observatory(ExampleClient client) {
		this.scene = client.getScene();
		terrainTexture = BufferTexture.createPlainColor(4, 4, new Color(0.5f, 0.6f, 0.4f));
		specularTexture = client.noSpecularTexture;
		normalTexture = client.plainNormalTexture;
	}
	
	public void destroy() {
		releaseTerrain();
		terrainTexture.destroy();
	}
	
	public void draw() {
		terrainActor.draw();
	}
	
	public void changeLatitude(int delta) {
		setLocation(world, latitude+delta);
	}

	public void poleLatitude() {
		if(latitude==90)
			setLocation(world, -90);
		else if(latitude==-90)
			setLocation(world, 90);
		else if(latitude>=0)
			setLocation(world, 90);
		else
			setLocation(world, -90);
	}
	
	public void setLocation(World world, Random random) {
		int startLat = random.nextInt(startMaxLat-startMinLat)+startMinLat;
		setLocation(world, startLat);
	}

	public void setLocation(World world, int latitude) {
		latitude = MathUtils.snap(latitude, -90, 90);
		if(this.world==world && this.latitude==latitude)
			return;
		
		if(terrain!=null)
			releaseTerrain();
		
		this.world = world;
		this.latitude = latitude;
		this.latitudeRadians = latitude/180f*(float)Math.PI;

		createTerrain(world.seed);
		
		StandardShader.environment.setFog(10f, 20f, new Vector4f(0, 0, 0, 1f));
		StandardShader.environment.ambientColor.set(0.0f, 0.02f, 0.04f);
		StandardShader.environment.lightColor.set(0.1f, 0.1f, 0.1f);
		
		Stargazer.infoPane.reset(getLatitudeString());
	}
	
	public String getLatitudeString() {
		return String.format("Latitude %d\u00b0%s", Math.abs(this.latitude), this.latitude>0 ? "N" : this.latitude<0 ? "S" : "");
	}
	
	private void createTerrain(long seed) {
		HeightMap hmap = new HeightMap(TERRAIN_SIZE, seed+9523443L);
		hmap.setBaseXY(latitude, 0);
		hmap.generatePerlin(0f, 1.0f, 0.2f, true);
		terrain = FastMeshBuilder.terrain(TERRAIN_SIZE, hmap.hmap, TERRAIN_SIZE/2, StandardShader.standardVertexInfo, null);
		
		terrainActor = StaticMeshActor.make(scene, terrain, StandardShader.getInstance(), terrainTexture, specularTexture, normalTexture);
		terrainActor.position.y = -hmap.hmap[TERRAIN_SIZE/2][TERRAIN_SIZE/2]-0.5f;
		terrainActor.updateTransform();
	}
	
	private void releaseTerrain() {
		terrain.destroy();
	}
	
}
