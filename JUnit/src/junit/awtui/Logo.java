package junit.awtui;

import java.awt.Canvas;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.SystemColor;
import java.awt.Toolkit;
import java.awt.image.ImageProducer;
import java.net.URL;

import junit.runner.BaseTestRunner;

public class Logo extends Canvas {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Image fImage;
	private int fWidth;
	private int fHeight;
	
	public Logo() {
		this.fImage= loadImage("logo.gif");
		MediaTracker tracker= new MediaTracker(this);
	  	tracker.addImage(this.fImage, 0);
		try {
			tracker.waitForAll();
		} catch (Exception e) {
		}

		if (this.fImage != null) {
			this.fWidth= this.fImage.getWidth(this);
			this.fHeight= this.fImage.getHeight(this);
		} else {
			this.fWidth= 20;
			this.fHeight= 20;
		}
		setSize(this.fWidth, this.fHeight);
	}
	
	public Image loadImage(String name) {
		Toolkit toolkit= Toolkit.getDefaultToolkit();
		try {
			URL url= BaseTestRunner.class.getResource(name);
			return toolkit.createImage((ImageProducer) url.getContent());
		} catch (Exception ex) {
		}
		return null;
	}
	
	public void paint(Graphics g) {
		paintBackground(g);
		if (this.fImage != null)
			g.drawImage(this.fImage, 0, 0, this.fWidth, this.fHeight, this);
	}
	
	public void paintBackground( java.awt.Graphics g) {
		g.setColor(SystemColor.control);
		g.fillRect(0, 0, getBounds().width, getBounds().height);
	}
}