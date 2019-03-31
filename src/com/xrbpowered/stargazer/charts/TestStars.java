package com.xrbpowered.stargazer.charts;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.JPanel;

import com.xrbpowered.stargazer.BlackBodySpectrum;
import com.xrbpowered.utils.ColorUtils;

public class TestStars extends JPanel {

	private static double defaultExposure = 0.9;
	private static double defaultCutoff = 1.5;

	private static double exposureScale = 1.05;
	private static double cutoffStep = 0.5;
	
	private Random random = new Random();
	private long seed = System.currentTimeMillis();
	private double exposure = defaultExposure;
	private double cutoff = defaultCutoff;
	private boolean showExposure = false;
	
	private static Font font = new Font("Verdana", Font.PLAIN, 15);
	
	private BufferedImage spectrum = null;
	
	public TestStars() {
		setFocusable(true);
		setFocusTraversalKeysEnabled(false);
		grabFocus();
		
		addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				switch(e.getKeyCode()) {
					case KeyEvent.VK_ENTER:
						exposure = defaultExposure;
						cutoff = defaultCutoff;
						seed = System.currentTimeMillis();
						repaint();
						break;
					case KeyEvent.VK_ESCAPE:
						System.exit(0);
						break;
					case KeyEvent.VK_TAB:
						showExposure = !showExposure;
						repaint();
						break;
					case KeyEvent.VK_HOME:
						exposure = defaultExposure;
						cutoff = defaultCutoff;
						repaint();
						break;
					case KeyEvent.VK_PAGE_UP:
						cutoff += cutoffStep;
						repaint();
						break;
					case KeyEvent.VK_PAGE_DOWN:
						cutoff -= cutoffStep;
						if(cutoff<0.0) cutoff = 0.0;
						repaint();
						break;
					case KeyEvent.VK_UP:
						exposure *= exposureScale;
						repaint();
						break;
					case KeyEvent.VK_DOWN:
						exposure /= exposureScale;
						repaint();
						break;
				}
			}
		});
		
		BufferedImage cursorImg = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
		Cursor blankCursor = Toolkit.getDefaultToolkit().createCustomCursor(cursorImg, new Point(0, 0), null);
		setCursor(blankCursor);
	}
	
	@Override
	public void paint(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		int w = getWidth();
		int h = getHeight();
		
		g2.setColor(Color.BLACK);
		g2.fillRect(0, 0, w, h);
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
		g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
		
		random.setSeed(seed);
		for(int i=0; i<50000; i++) {
			int x = random.nextInt(w);
			//int y = random.nextInt(h);
			double yf = random.nextGaussian()*h*0.2+h/2.0;
			int y = yf>=0 && yf<=h ? (int)yf : random.nextInt(h);
			double rf = exposure * Math.log(1-random.nextDouble())/(-7f);
			double c = Math.pow(rf, cutoff+2.0);
			int r = (int)Math.round(2.0*rf/Math.sqrt(Math.PI));
			
			double t = BlackBodySpectrum.randomTemp(random);
			Color color = BlackBodySpectrum.getColor(t);
			color = ColorUtils.blend(Color.WHITE, color, (rf-0.75)/1.25);
			if(c<1f) {
				color = ColorUtils.alpha(color, c);
			}
			g2.setColor(color);
			
			if(r<=1)
				g2.fillRect(x, y, 1, 1);
			else
				g2.fillOval(x, y, r, r);
		}
		
		if(showExposure) {
			g2.setFont(font);
			String s = String.format("EXP:%.4f CO:%.4f", exposure, cutoff);
			int tw = g2.getFontMetrics().stringWidth(s);
			g2.setColor(Color.DARK_GRAY);
			g2.drawString(s, w/2-tw/2, h-20);
		}
		
		if(spectrum==null)
			spectrum = BlackBodySpectrum.generateImage(w, 20);
		g2.drawImage(spectrum, 0, 0, null);
	}

	public static void main(String[] args) {
		JFrame frame = new JFrame("Stars");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setUndecorated(true);
		frame.setResizable(false);
		
		frame.setContentPane(new TestStars());
		
		frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
		frame.setVisible(true);
	}

}
