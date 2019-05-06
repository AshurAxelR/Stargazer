package com.xrbpowered.stargazer;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.io.File;
import java.util.ArrayList;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;
import org.w3c.dom.Element;

import com.xrbpowered.gl.SystemSettings.WindowMode;
import com.xrbpowered.gl.examples.ExampleClient;
import com.xrbpowered.gl.res.buffers.RenderTarget;
import com.xrbpowered.gl.res.textures.BufferTexture;
import com.xrbpowered.gl.scene.Controller;
import com.xrbpowered.gl.scene.Screenshot;
import com.xrbpowered.gl.ui.UIPane;
import com.xrbpowered.stargazer.charts.StarChart;
import com.xrbpowered.stargazer.data.OptionParser;
import com.xrbpowered.stargazer.data.World;
import com.xrbpowered.stargazer.data.XmlReader;
import com.xrbpowered.utils.assets.AssetManager;
import com.xrbpowered.utils.assets.CPAssetManager;

public class Stargazer extends ExampleClient {

	public static final String version = "1.0.0";
	
	public static String configPath = "config/options.xml";
	public static String screenshotPath = "screenshots";
	
	public static StarField stars;
	public static MapGrid grid;
	public static Observatory observatory;

	public static PopupUIPane titlePane;
	public static PopupUIPane infoPane;

	private static ArrayList<World> worlds = new ArrayList<>();
	private static int worldIndex = -1;
	private static World world = new World("Stargazer I", 1L);

	private boolean mouseLook = true;
	private Font titleFont =  new Font("Times New Roman", Font.PLAIN, 44);
	private Font infoFont = titleFont.deriveFont(24f);
	private UIPane bottomInfoPane;
	
	private Color skyColor = new Color(0x000a14);
	
	public Stargazer() {
		CLEAR_COLOR = skyColor;
		settings.fov = 45f;
		settings.multisample = 4;
		settings.windowMode = WindowMode.borderless;
		init("Stargazer").run();
	}
	
	@Override
	protected void setupResources() {
		super.setupResources();
		uiDebugPane.setVisible(false);
		
		titlePane = new PopupUIPane(ui, 600, 80, titleFont);
		infoPane = new PopupUIPane(ui, 400, 80, infoFont);

		stars = new StarField(scene);
		grid = new MapGrid(scene, stars);
		observatory = new Observatory(this);
		
		scene.activeCamera.position = new Vector3f(0, 0, 0);
		lightActor.rotation.x = (float) Math.PI / 6f;
		lightActor.updateTransform();
		
		setWorld(worldIndex);
		
		controller = new Controller() {
			@Override
			protected void applyVelocity(Vector3f position, Vector4f v) {
			}
		}.setActor(scene.activeCamera).setLookController(true);
		controller.setMouseLook(true);
		controller.rotateSpeed *= 0.2f;
		controller.limitRotation = true;
		
		bottomInfoPane = new UIPane(ui, new BufferTexture(1600, 60, false, false, true) {
			@Override
			protected boolean updateBuffer(Graphics2D g2, int w, int h) {
				double azimuth = -scene.activeCamera.rotation.y*180.0/Math.PI-90.0;
				if(azimuth<0.0)
					azimuth += 360.0*(-Math.floor(azimuth/360.0)+1.0);
				azimuth = azimuth%360.0;
				double pitch = scene.activeCamera.rotation.x*180.0/Math.PI;
				double t = stars.timeOfDay/Math.PI/2.0;
				int time = (int)((t-Math.floor(t))*24.0*60.0);

				g2.setBackground(Color.BLACK);
				return PopupUIPane.updateInfo(g2, w, h, infoFont,
						String.format("%s \u00b7 %02d:%02d \u00b7 %s \u00b7 Azimuth %.1f\u00b0 \u00b7 Pitch %.1f\u00b0",
						world.title, time/60, time%60, observatory.getLatitudeString(), azimuth, pitch));
			}
		});
		setMapMode(false);
	}
	
	@Override
	protected void destroyResources() {
		super.destroyResources();
		observatory.destroy();
		grid.destroy();
		stars.destroy();
	}
	
	@Override
	protected void resizeResources() {
		super.resizeResources();
		PopupUIPane.align(titlePane, 0.5f, 0);
		PopupUIPane.align(infoPane, 0.75f, 0);
		PopupUIPane.align(bottomInfoPane, 1f, -40);
	}
	
	public void setMapMode(boolean mapMode) {
		MapGrid.mapMode = mapMode;
		CLEAR_COLOR = mapMode ? Color.BLACK : skyColor;
		bottomInfoPane.setVisible(mapMode);
	}
	
	public void screenshotMap() {
		boolean mapMode = MapGrid.mapMode; 
		setMapMode(true);
		File path = new File(screenshotPath);
		if(!path.isDirectory())
			path.mkdirs();
		new Screenshot(this).save(screenshotPath);
		setMapMode(mapMode);
	}
	
	public void setRandomWorld() {
		worldIndex = -1;
		world = new World("Random World", System.currentTimeMillis());
		stars.setWorld(world);
	}
	
	public void setWorld(int index) {
		int size = worlds.size();
		if(size>0) {
			if(index<0)
				index = size-1;
			else if(index>=size)
				index = 0;
			worldIndex = index;
			world = worlds.get(index);
			stars.setWorld(world);
		}
		else
			setRandomWorld();
	}
	
	@Override
	protected void keyDown(int key) {
		switch (Keyboard.getEventKey()) {
			case Keyboard.KEY_TAB:
				setMapMode(!MapGrid.mapMode);
				break;
			case Keyboard.KEY_GRAVE:
				screenshotMap();
				break;
			case Keyboard.KEY_ESCAPE:
				exit();
				break;

			case Keyboard.KEY_LBRACKET:
				setWorld(worldIndex-1);
				break;
			case Keyboard.KEY_RBRACKET:
				setWorld(worldIndex+1);
				break;
			case Keyboard.KEY_BACK:
				stars.setWorld(world);
				break;
			case Keyboard.KEY_RETURN:
				setRandomWorld();
				break;


			case Keyboard.KEY_MINUS:
				observatory.changeLatitude(-5);
				break;
			case Keyboard.KEY_EQUALS:
				observatory.changeLatitude(5);
				break;
			case Keyboard.KEY_0:
				observatory.poleLatitude();
				break;
			case Keyboard.KEY_9:
				stars.timeOfDay += (float)Math.PI;
				break;
			case Keyboard.KEY_8:
				stars.timeOfDay = 0f;
				break;
				
			default:
				super.keyDown(key);
		}
	}
	
	protected void updateControllers(float dt) {
		while(Mouse.next()) {
			stars.adjustExposure(Mouse.getEventDWheel()/120);
			if(Mouse.getEventButton()==2 && Mouse.getEventButtonState())
				stars.reserExposure();
			if(Mouse.getEventButton()==1 && Mouse.getEventButtonState()) {
				mouseLook = !mouseLook;
				controller.setMouseLook(mouseLook);
			}
		}
		if(mouseLook)
			controller.update(dt);
		
		
		stars.update(dt);
		grid.updateTransform();
		titlePane.update(dt);
		infoPane.update(dt);
		bottomInfoPane.repaint();
	}
	
	@Override
	protected void drawObjects(RenderTarget target) {
		if(MapGrid.mapMode)
			grid.draw();
		
		stars.draw();
		
		if(!MapGrid.mapMode)
			observatory.draw();
	}
	
	private static final OptionParser configParser = new OptionParser() {
		@Override
		public void setOption(String key, String value) {
			switch(key) {
				case "screenshotPath":
					screenshotPath = value;
					break;
				case "exposure":
					StarField.defExposure = XmlReader.toFloat(value, StarField.defExposure);
					break;
				case "contrast":
					StarField.defContrast= XmlReader.toFloat(value, StarField.defContrast);
					break;
				case "circle":
					StarField.circleMultiplier= XmlReader.toFloat(value, StarField.circleMultiplier);
					break;
				case "startLatitudeMin":
					Observatory.startMinLat = XmlReader.toInt(value, Observatory.startMinLat);
					break;
				case "startLatitudeMax":
					Observatory.startMaxLat = XmlReader.toInt(value, Observatory.startMaxLat);
					break;
			}
		}
	};
	
	private static void loadConfig() {
		try {
			Element root = XmlReader.load(configPath);
			XmlReader.parseOptions(XmlReader.element(root, "options"), configParser);

			worlds.clear();
			for(Element we : XmlReader.elements(XmlReader.element(root, "worlds"), "world")) {
				World w = World.load(null, we, true, false);
				if(w!=null) {
					System.out.printf("World %s loaded\n", w.title);
					worlds.add(w);
				}
			}
			worldIndex = 0;
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		System.out.println("Stargazer: version "+version);
		
		if(args.length>0 && args[0].equals("-C")) {
			StarChart.main(args);
			return;
		}
		
		AssetManager.defaultAssets = new CPAssetManager("com/xrbpowered/stargazer", AssetManager.defaultAssets);
		loadConfig();
		new Stargazer();
	}

}
