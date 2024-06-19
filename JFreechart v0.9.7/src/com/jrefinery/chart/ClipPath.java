package com.jrefinery.chart;

import java.awt.BasicStroke;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;

import com.jrefinery.chart.axis.ValueAxis;

/**
 * This class would typically be used with the ContourPlot type.  It allows the user to define a
 * GeneralPath curve in plot coordinates.  This curve can then be used mask off or define regions
 * within the contour plot.  The data must be sorted.
 * @author dmo
 *
 */
public class ClipPath {
	
	private double[] xValue = null;
	private double[] yValue = null;
	
    /** Controls whether drawing will be clipped (
     * false would still allow the drawing or filling of path */
    private boolean clip = true;
    
    /** Controls whether the path is drawn as an outline. */
    private boolean drawPath = false;
    
    /** Controls whether the path is filled. */
    private boolean fillPath = false;
	
	private Paint fillPaint = null;
	private Paint drawPaint = null;
	private Stroke drawStroke = null;
	private Composite composite = null;
	
	
	/**
	 * Constructor for ClipPath.
	 */
	public ClipPath() {
		super();
	}
	
	/**
	 * Constructor for ClipPath.
	 * Default values are assumed for the fillPath and drawPath options as false and true respectively.
	 * The fillPaint is set to Color.GRAY, the drawColor is Color.BLUE, the stroke is BasicStroke(1)
	 * and the composite is AlphaComposite.Src.
	 * @param x coordinates of curved to be created
	 * @param y coordinates of curved to be created
	 */
	public ClipPath(double[] xValue, double[] yValue) {
		this(xValue, yValue, true, false, true);
	}
	
	
	/**
	 * Constructor for ClipPath.
	 * The fillPaint is set to Color.GRAY, the drawColor is Color.BLUE, the stroke is BasicStroke(1)
	 * and the composite is AlphaComposite.Src.
	 * @param x coordinates of curved to be created
	 * @param y coordinates of curved to be created
	 * @param whether the path is to filled
	 * @param whether the path is to drawn as an outline
	 */
	public ClipPath(double[] xValue, double[] yValue, boolean clip, boolean fillPath, boolean drawPath) {
		this.xValue = xValue;	                 
		this.yValue = yValue;
	
		this.clip = clip;
		this.fillPath = fillPath;
		this.drawPath = drawPath;
	
		this.fillPaint = java.awt.Color.gray;
		this.drawPaint = java.awt.Color.blue;
		this.drawStroke = new BasicStroke(1);
		this.composite = java.awt.AlphaComposite.Src;
	}
	
	/**
	 * Constructor for ClipPath.
	 * @param x coordinates of curved to be created
	 * @param y coordinates of curved to be created
	 * @param whether the path is to filled
	 * @param whether the path is to drawn as an outline
	 * @param the fill paint
	 * @param the outline stroke color
	 * @param the stroke style
	 * @param the composite rule
	 */

	public ClipPath(double[] xValue, double[] yValue, boolean fillPath, boolean drawPath,
	                 Paint fillPaint, Paint drawPaint, Stroke drawStroke, Composite composite) {
	                 	
		this.xValue = xValue;	                 
		this.yValue = yValue;
	
		this.fillPath = fillPath;
		this.drawPath = drawPath;
	
		this.fillPaint = fillPaint;
		this.drawPaint = drawPaint;
		this.drawStroke = drawStroke;
		this.composite = composite;
	}
	
	/**
	 * Draws the clip path.
	 *
	 * @param current graphics2D
	 * @param the dataArea that the plot is being draw in
	 * @param the horizontal axis
	 * @param the vertical axis
	 * @return the GeneralPath defining the outline 
	 */
    public GeneralPath draw(Graphics2D g2, Rectangle2D dataArea, ValueAxis horizontalAxis, ValueAxis verticalAxis) {
		GeneralPath generalPath = generateClipPath(dataArea, horizontalAxis, verticalAxis);
            if (fillPath || drawPath) {
            	Composite comp_old = g2.getComposite();
				Paint p_old = g2.getPaint();
				Stroke stroke_old =g2.getStroke();
				
				if (fillPaint!=null) g2.setPaint(fillPaint);
				if (composite!=null) g2.setComposite(composite);		
				if (fillPath) g2.fill(generalPath);
				
				if (drawStroke!=null) g2.setStroke(drawStroke);
				
				if (drawPath) {
					g2.draw(generalPath);
				}
				g2.setPaint(p_old);
				g2.setComposite(comp_old);
				g2.setStroke(stroke_old);
            } 
		return generalPath;	
	}
	
	/**
	 * Generates the clip path.
	 *
	 * @param the dataArea that the plot is being draw in
	 * @param the horizontal axis
	 * @param the vertical axis
	 * @return the GeneralPath defining the outline 
	 */
    public GeneralPath generateClipPath(Rectangle2D dataArea, ValueAxis horizontalAxis, ValueAxis verticalAxis) {
			
		GeneralPath generalPath = new GeneralPath();
		double transX = horizontalAxis.translateValueToJava2D(xValue[0], dataArea);
		double transY = verticalAxis.translateValueToJava2D(yValue[0], dataArea);
		generalPath.moveTo((float)transX, (float)transY);
		for (int k=0;k<yValue.length;k++) {
			transX = horizontalAxis.translateValueToJava2D(xValue[k], dataArea);
			transY = verticalAxis.translateValueToJava2D(yValue[k], dataArea);
			generalPath.lineTo((float)transX, (float)transY);
		}
		generalPath.closePath();
		
		return generalPath;
	
	}

	/**
	 * Returns the composite.
	 * @return Composite
	 */
	public Composite getComposite() {
		return composite;
	}

	/**
	 * Returns the drawPaint.
	 * @return Paint
	 */
	public Paint getDrawPaint() {
		return drawPaint;
	}

	/**
	 * Returns the drawPath.
	 * @return boolean
	 */
	public boolean isDrawPath() {
		return drawPath;
	}

	/**
	 * Returns the drawStroke.
	 * @return Stroke
	 */
	public Stroke getDrawStroke() {
		return drawStroke;
	}

	/**
	 * Returns the fillPaint.
	 * @return Paint
	 */
	public Paint getFillPaint() {
		return fillPaint;
	}

	/**
	 * Returns the fillPath.
	 * @return boolean
	 */
	public boolean isFillPath() {
		return fillPath;
	}

	/**
	 * Returns the xValue.
	 * @return double[]
	 */
	public double[] getXValue() {
		return xValue;
	}

	/**
	 * Returns the yValue.
	 * @return double[]
	 */
	public double[] getYValue() {
		return yValue;
	}

	/**
	 * Sets the composite.
	 * @param composite The composite to set
	 */
	public void setComposite(Composite composite) {
		this.composite = composite;
	}

	/**
	 * Sets the drawPaint.
	 * @param drawPaint The drawPaint to set
	 */
	public void setDrawPaint(Paint drawPaint) {
		this.drawPaint = drawPaint;
	}

	/**
	 * Sets the drawPath.
	 * @param drawPath The drawPath to set
	 */
	public void setDrawPath(boolean drawPath) {
		this.drawPath = drawPath;
	}

	/**
	 * Sets the drawStroke.
	 * @param drawStroke The drawStroke to set
	 */
	public void setDrawStroke(Stroke drawStroke) {
		this.drawStroke = drawStroke;
	}

	/**
	 * Sets the fillPaint.
	 * @param fillPaint The fillPaint to set
	 */
	public void setFillPaint(Paint fillPaint) {
		this.fillPaint = fillPaint;
	}

	/**
	 * Sets the fillPath.
	 * @param fillPath The fillPath to set
	 */
	public void setFillPath(boolean fillPath) {
		this.fillPath = fillPath;
	}

	/**
	 * Sets the xValue.
	 * @param xValue The xValue to set
	 */
	public void setXValue(double[] xValue) {
		this.xValue = xValue;
	}

	/**
	 * Sets the yValue.
	 * @param yValue The yValue to set
	 */
	public void setYValue(double[] yValue) {
		this.yValue = yValue;
	}

	/**
	 * Returns the clip.
	 * @return boolean
	 */
	public boolean isClip() {
		return clip;
	}

	/**
	 * Sets the clip.
	 * @param clip The clip to set
	 */
	public void setClip(boolean clip) {
		this.clip = clip;
	}

}
