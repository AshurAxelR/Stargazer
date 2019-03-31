package com.xrbpowered.stargazer;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import com.xrbpowered.gl.res.StaticMesh;
import com.xrbpowered.gl.res.builder.FastMeshBuilder;
import com.xrbpowered.gl.res.builder.FastMeshBuilder.Vertex;
import com.xrbpowered.gl.res.shaders.ActorShader;
import com.xrbpowered.gl.res.shaders.StandardShader;
import com.xrbpowered.gl.res.shaders.VertexInfo;
import com.xrbpowered.gl.scene.Scene;
import com.xrbpowered.gl.scene.StaticMeshActor;

public class MapGrid {

	public static boolean mapMode = false;

	private static final int PARALLEL_SEGM = 18;
	private static final int MERIDIAN_SEGM = 24;
	
	public static class Shader extends ActorShader {
		public Shader() {
			super(StandardShader.standardVertexInfo, "std_v.glsl", "blank_f.glsl");
		}
		public void setColor(Vector4f color) {
			GL20.glUseProgram(pId);
			uniform(GL20.glGetUniformLocation(pId, "color"), color);
		}
	}

	private StaticMesh mesh;
	private StaticMeshActor actor;
	private StaticMesh eqMesh;
	private StaticMeshActor eqActor;
	private Shader shader;
	
	public MapGrid(Scene scene, StarField starField) {
		shader = new Shader();
		
		FastMeshBuilder mb = gridBuilder(30f, PARALLEL_SEGM, MERIDIAN_SEGM, StandardShader.getInstance().info);
		mesh = mb.create(2);
		actor =  new StaticMeshActor(scene);
		actor.setMesh(mesh);
		actor.setShader(shader);
		actor.rotation = starField.dailyCycle;
		
		eqMesh = createEquator(PARALLEL_SEGM, MERIDIAN_SEGM, StandardShader.getInstance().info, mb.getVertexData());
		eqActor =  new StaticMeshActor(scene);
		eqActor.setMesh(eqMesh);
		eqActor.setShader(shader);
		eqActor.rotation = starField.dailyCycle;
	}
	
	public void destroy() {
		mesh.destroy();
		shader.destroy();
	}
	
	public void updateTransform() {
		actor.updateTransform();
		eqActor.updateTransform();
	}
	
	public void draw() {
		GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
		GL11.glDepthMask(false);
		shader.setColor(new Vector4f(0.25f, 0.25f, 0.25f, 1f));
		actor.draw();
		shader.setColor(new Vector4f(0.75f, 0.75f, 0.75f, 1f));
		eqActor.draw();
		GL11.glDepthMask(true);
		GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
	}
	
	public static FastMeshBuilder gridBuilder(float r, int psegm, int msegm, VertexInfo info) {
		int i, j;
		
		float[] msin = new float[msegm+1];
		float[] mcos = new float[msegm+1];
		float ai;
		float da = (float) Math.PI*2f / (float) msegm;
		for(i=0, ai = 0; i<=msegm; i++, ai += da) {
			msin[i] = (float) Math.sin(ai);
			mcos[i] = (float) Math.cos(ai);
		}
		float[] psin = new float[psegm+1];
		float[] pcos = new float[psegm+1];
		da = (float) Math.PI / (float) psegm;
		for(i=0, ai = 0; i<=psegm; i++, ai += da) {
			psin[i] = (float) Math.sin(ai);
			pcos[i] = (float) Math.cos(ai);
		}
		
		FastMeshBuilder mb = new FastMeshBuilder(info, null, (psegm+1) * (msegm+1), psegm * msegm * 4);
		
		Vector3f v = new Vector3f();
		int index = 0;
		for(i=0; i<=msegm; i++) {
			for(j=0; j<=psegm; j++) {
				Vertex vertex = mb.getVertex(index);
				float r0 = r * psin[j];
				v.x = -r * pcos[j];
				v.y = r0 * mcos[i];
				v.z = r0 * msin[i];
				vertex.setPosition(v);
				index++;
			}
		}
		
		for(i=0; i<msegm; i++) {
			for(j=0; j<psegm; j++) {
				mb.addEdge((i+0) * (psegm+1) + (j+0), (i+0) * (psegm+1) + (j+1));
				mb.addEdge((i+0) * (psegm+1) + (j+0), (i+1) * (psegm+1) + (j+0));
			}
		}
		
		return mb;
	}
	
	public static StaticMesh createEquator(int psegm, int msegm, VertexInfo info, float[] vertexData) {
		FastMeshBuilder mb = new FastMeshBuilder(info, null, vertexData, msegm * 2);
		
		for(int i=0; i<msegm; i++) {
			int j = psegm/2;
			mb.addEdge((i+0) * (psegm+1) + (j+0), (i+1) * (psegm+1) + (j+0));
		}
		
		return mb.create(2);
	}

}
