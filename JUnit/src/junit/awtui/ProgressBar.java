package junit.awtui;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.SystemColor;

public class ProgressBar extends Canvas {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public boolean fError= false;
	public int fTotal= 0;
	public int fProgress= 0;
	public int fProgressX= 0;

	public ProgressBar() {
		super();
		setSize(20, 30);
	}
	
	private Color getStatusColor() {
		if (this.fError)
			return Color.red;
		return Color.green;
	}
	
	public void paint(Graphics g) {
		paintBackground(g);
		paintStatus(g);
	}
	
	public void paintBackground(Graphics g) {
		g.setColor(SystemColor.control);
		Rectangle r= getBounds();
		g.fillRect(0, 0, r.width, r.height);
		g.setColor(Color.darkGray);
		g.drawLine(0, 0, r.width-1, 0);
		g.drawLine(0, 0, 0, r.height-1);
		g.setColor(Color.white);
		g.drawLine(r.width-1, 0, r.width-1, r.height-1);
		g.drawLine(0, r.height-1, r.width-1, r.height-1);
	}
	
	public void paintStatus(Graphics g) {
		g.setColor(getStatusColor());
		Rectangle r= new Rectangle(0, 0, this.fProgressX, getBounds().height);
		g.fillRect(1, 1, r.width-1, r.height-2);
	}
	
	private void paintStep(int startX, int endX) {
		repaint(startX, 1, endX-startX, getBounds().height-2);
	}
	
	public void reset() {
		this.fProgressX= 1;
		this.fProgress= 0;
		this.fError= false;
		paint(getGraphics());
	}
	
	public int scale(int value) {
		if (this.fTotal > 0)
			return Math.max(1, value*(getBounds().width-1)/this.fTotal);
		return value; 
	}
	
	public void setBounds(int x, int y, int w, int h) {
		super.setBounds(x, y, w, h);
		this.fProgressX= scale(this.fProgress);
	}
	
	public void start(int total) {
		this.fTotal= total;
		reset();
	}
	
	public void step(boolean successful) {
		this.fProgress++;
		int x= this.fProgressX;

		this.fProgressX= scale(this.fProgress);

		if (!this.fError && !successful) {
			this.fError= true;
			x= 1;
		}
		paintStep(x, this.fProgressX);
	}
}