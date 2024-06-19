package junit.swingui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;

/**
 * A simple progress bar showing the green/red status
 */
class ProgressBar extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	boolean fError= false;
	int fTotal= 0;
	int fProgress= 0;
	int fProgressX= 0;
	
	public ProgressBar() {
		super();
		setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
	}
	
	private Color getStatusColor() {
		if (this.fError)
			return Color.red;
		return Color.green;
	}
	
	public void paintBackground(Graphics g) {
		g.setColor(getBackground());
		g.fillRect(0,0,getWidth(),getHeight());
	}
	
	public void paintComponent(Graphics g) {
		paintBackground(g);
		paintStatus(g);
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
		repaint();
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