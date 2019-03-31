package com.xrbpowered.stargazer;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import org.lwjgl.opengl.Display;

import com.xrbpowered.gl.res.textures.BufferTexture;
import com.xrbpowered.gl.ui.UIManager;
import com.xrbpowered.gl.ui.UIPane;
import com.xrbpowered.utils.TextUtils;

public class PopupUIPane extends UIPane {

	public float delay = 1f;
	public float fadeOut = 4f;
	
	public String label = "";
	public float timer = 0f;

	public PopupUIPane(UIManager ui, int w, int h, final Font font) {
		super(ui);
		setTexture(new BufferTexture(600, 80, false, false, false) {
			@Override
			protected boolean updateBuffer(Graphics2D g2, int w, int h) {
				g2.setBackground(BufferTexture.CLEAR_COLOR);
				return updateInfo(g2, w, h, font, label);
			}
		});
	}
	
	public void reset(String label) {
		this.label = label;
		repaint();
		
		timer = 0f;
		alpha = 1f;
		setVisible(true);
	}
	
	public void update(float dt) {
		if(timer>=delay+fadeOut)
			setVisible(false);
		else {
			setVisible(true);
			if(timer>delay && fadeOut>0f)
				alpha = 1f - (timer-1f)/fadeOut;
			timer += dt;
		}
	}
	
	public static boolean updateInfo(Graphics2D g2, int w, int h, Font font, String label) {
		g2.clearRect(0, 0, w, h);
		g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_GASP);
		g2.setFont(font);
		g2.setColor(Color.WHITE);
		TextUtils.drawString(g2, label, w/2, h/2, TextUtils.CENTER, TextUtils.CENTER);
		return true;
	}
	
	public static void align(UIPane pane, float hfrac, int dy) {
		pane.setAnchor(Display.getWidth()/2f-pane.getWidth()/2f, Display.getHeight()*hfrac+dy-pane.getHeight()/2f);
	}
}
