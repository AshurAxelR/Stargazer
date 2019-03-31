package com.xrbpowered.stargazer;

import java.awt.Graphics2D;
import java.awt.image.WritableRaster;
import java.util.Random;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL32;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import com.xrbpowered.gl.res.StaticMesh;
import com.xrbpowered.gl.res.shaders.Shader;
import com.xrbpowered.gl.res.shaders.VertexInfo;
import com.xrbpowered.gl.res.textures.BufferTexture;
import com.xrbpowered.gl.res.textures.Texture;
import com.xrbpowered.gl.scene.Actor;
import com.xrbpowered.gl.scene.Scene;
import com.xrbpowered.stargazer.data.World;

public class StarField {

	public static final int SKIP = 5;
	
	public static float defExposure = 0.85f;
	public static float defContrast = 0.8f;
	public static float circleMultiplier = 12f;

	private Shader starShader;
	private StaticMesh stars = null;
	
	private Texture spectrumTexture;
	
	private int exposureStep = 0;
	private float exposure = defExposure;
	private float contrast = defContrast;

	private Matrix4f dailyCycleMatrix = new Matrix4f();

	public Vector3f dailyCycle = new Vector3f();
	public float timeOfDay;

	public StarField(final Scene scene) {
		VertexInfo starInfo = new VertexInfo().addAttrib("in_Position", 3).addAttrib("in_Luminosity", 1).addAttrib("in_Temperature", 1);
		starShader = new Shader(starInfo, "stars_v.glsl", "stars_f.glsl") {
			private int projectionMatrixLocation;
			private int viewMatrixLocation;
			private int cycleMatrixLocation;
			@Override
			protected void storeUniformLocations() {
				projectionMatrixLocation = GL20.glGetUniformLocation(pId, "projectionMatrix");
				viewMatrixLocation = GL20.glGetUniformLocation(pId, "viewMatrix");
				cycleMatrixLocation = GL20.glGetUniformLocation(pId, "cycleMatrix");
			}
			@Override
			public void updateUniforms() {
				GL11.glDepthMask(false);
				GL11.glEnable(GL32.GL_PROGRAM_POINT_SIZE);
				GL11.glEnable(GL11.GL_BLEND);
				GL11.glBlendFunc(GL11.GL_ONE, GL11.GL_ONE);
				uniform(projectionMatrixLocation, scene.activeCamera.getProjection());
				uniform(viewMatrixLocation, scene.activeCamera.getView());
				uniform(cycleMatrixLocation, dailyCycleMatrix);
				GL20.glUniform1f(GL20.glGetUniformLocation(pId, "exposure"), exposure);
				GL20.glUniform1f(GL20.glGetUniformLocation(pId, "contrast"), contrast);
				GL20.glUniform1f(GL20.glGetUniformLocation(pId, "circles"), MapGrid.mapMode ? circleMultiplier : 0);
			}
			@Override
			public void unuse() {
				GL11.glDisable(GL11.GL_BLEND);
				GL11.glDepthMask(true);
				super.unuse();
			}
		};
		
		spectrumTexture = new BufferTexture(1024, 4, false, false, false) {
			@Override
			protected boolean updateBuffer(Graphics2D g2, int w, int h) {
				int[] rgb = BlackBodySpectrum.generate(w, h, true);
				WritableRaster raster = imgBuffer.getRaster();
				raster.setPixels(0, 0, w, h, rgb);
				return true;
			}
		};
	}
	
	public void destroy() {
		releaseStars();
		starShader.destroy();
	}
	
	public void update(float dt) {
		timeOfDay += Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) ? 0.02f*dt : 0.001f*dt;
		Matrix4f m = dailyCycleMatrix;
		Matrix4f.setIdentity(m);
		dailyCycle.set(-timeOfDay, 0, Stargazer.observatory.latitudeRadians);
		Actor.rotateYawPitchRoll(dailyCycle, m);
	}
	
	public void adjustExposure(int delta) {
		exposureStep += delta;
		exposure = defExposure*(float)Math.pow(1.025, exposureStep);
	}
	
	public void reserExposure() {
		exposureStep = 0;
		exposure = defExposure;
	}
	
	public void draw() {
		starShader.use();
		spectrumTexture.bind(0);
		stars.draw();
		starShader.unuse();
	}
	
	public void setWorld(World world) {
		if(stars!=null)
			releaseStars();
		createStars(world);
		
		Stargazer.titlePane.reset(world.title);
		
		Random random = new Random();
		timeOfDay = random.nextFloat()*2f*(float)Math.PI;
		Stargazer.observatory.setLocation(world, random);
	}
	
	private void createStars(World world) {
		stars = new StaticMesh(starShader.info, world.createStarData(), 1, world.numStars, false);
	}
	
	private void releaseStars() {
		stars.destroy();
	}
	
}
