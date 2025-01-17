/* ===========================================================
 * JFreeChart : a free chart library for the Java(tm) platform
 * ===========================================================
 *
 * (C) Copyright 2000-2006, by Object Refinery Limited and Contributors.
 *
 * Project Info:  http://www.jfree.org/jfreechart/index.html
 *
 * This library is free software; you can redistribute it and/or modify it 
 * under the terms of the GNU Lesser General Public License as published by 
 * the Free Software Foundation; either version 2.1 of the License, or 
 * (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but 
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY 
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public 
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, 
 * USA.  
 *
 * [Java is a trademark or registered trademark of Sun Microsystems, Inc. 
 * in the United States and other countries.]
 *
 * ---------------
 * LabelBlock.java
 * ---------------
 * (C) Copyright 2004-2006, by Object Refinery Limited.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   Pierre-Marie Le Biot;
 *
 * $Id: LabelBlock.java,v 1.1 2007/10/10 20:22:59 vauchers Exp $
 *
 * Changes:
 * --------
 * 22-Oct-2004 : Version 1 (DG);
 * 19-Apr-2005 : Added optional tooltip and URL text items,
 *               draw() method now returns entities if 
 *               requested (DG);
 * 13-May-2005 : Added methods to set the font (DG);
 * 01-Sep-2005 : Added paint management (PMLB);
 *               Implemented equals() and clone() (PublicCloneable) (DG);
 * ------------- JFREECHART 1.0.0 ---------------------------------------------
 * 20-Jul-2006 : Fixed entity area in draw() method (DG);
 * 
 */

package org.jfree.chart.block;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;

import org.jfree.chart.entity.ChartEntity;
import org.jfree.chart.entity.StandardEntityCollection;
import org.jfree.text.TextBlock;
import org.jfree.text.TextBlockAnchor;
import org.jfree.text.TextUtilities;
import org.jfree.ui.Size2D;
import org.jfree.util.ObjectUtilities;
import org.jfree.util.PaintUtilities;
import org.jfree.util.PublicCloneable;

/**
 * A block containing a label.
 */
public class LabelBlock extends AbstractBlock 
                                implements Block, PublicCloneable {
    
    /** 
     * The text for the label - retained in case the label needs 
     * regenerating (for example, to change the font). 
     */
    private String text;
    
    /** The label. */
    private TextBlock label;
    
    /** The font. */
    private Font font;
    
    /** The tool tip text (can be <code>null</code>). */
    private String toolTipText;
    
    /** The URL text (can be <code>null</code>). */
    private String urlText;
    
    /** The default color. */
    public static final Paint DEFAULT_PAINT = Color.black;

    /** The paint. */
    private Paint paint;
    
    /**
     * Creates a new label block.
     * 
     * @param label  the label (<code>null</code> not permitted).
     */
    public LabelBlock(String label) {
        this(label, new Font("SansSerif", Font.PLAIN, 10), DEFAULT_PAINT);
    }
    
    /**
     * Creates a new label block.
     * 
     * @param text  the text for the label (<code>null</code> not permitted).
     * @param font  the font (<code>null</code> not permitted).
     */
    public LabelBlock(String text, Font font) {        
        this(text, font, DEFAULT_PAINT);
    }
    
    /**
     * Creates a new label block.
     *
     * @param text  the text for the label (<code>null</code> not permitted).
     * @param font  the font (<code>null</code> not permitted).
     * @param paint the paint (<code>null</code> not permitted).
     */
    public LabelBlock(String text, Font font, Paint paint) {        
        this.text = text;
        this.paint = paint; 
        this.label = TextUtilities.createTextBlock(text, font, this.paint); 
        this.font = font;
        this.toolTipText = null;
        this.urlText = null;
    }
    
    /**
     * Returns the font.
     *
     * @return The font (never <code>null</code>).
     */
    public Font getFont() {
        return this.font;    
    }
    
    /**
     * Sets the font and regenerates the label.
     *
     * @param font  the font (<code>null</code> not permitted).
     */
    public void setFont(Font font) {
        if (font == null) {
            throw new IllegalArgumentException("Null 'font' argument.");
        }
        this.font = font;
        this.label = TextUtilities.createTextBlock(this.text, font, this.paint);
    }
   
    /**
     * Returns the paint.
     *
     * @return The paint (never <code>null</code>).
     */
    public Paint getPaint() {
        return this.paint;   
    }
   
    /**
     * Sets the paint and regenerates the label.
     *
     * @param paint  the paint (<code>null</code> not permitted).
     */
    public void setPaint(Paint paint) {
        if (paint == null) {
            throw new IllegalArgumentException("Null 'paint' argument.");
        }
        this.paint = paint;
        this.label = TextUtilities.createTextBlock(this.text, font, this.paint);
    }

    /**
     * Returns the tool tip text.
     * 
     * @return The tool tip text (possibly <code>null</code>).
     */
    public String getToolTipText() {
        return this.toolTipText;
    }
    
    /**
     * Sets the tool tip text.
     * 
     * @param text  the text (<code>null</code> permitted).
     */
    public void setToolTipText(String text) {
        this.toolTipText = text;   
    }
    
    /**
     * Returns the URL text.
     * 
     * @return The URL text (possibly <code>null</code>).
     */
    public String getURLText() {
        return this.urlText;
    }
    
    /**
     * Sets the URL text.
     * 
     * @param text  the text (<code>null</code> permitted).
     */
    public void setURLText(String text) {
        this.urlText = text;   
    }
    
    /**
     * Arranges the contents of the block, within the given constraints, and 
     * returns the block size.
     * 
     * @param g2  the graphics device.
     * @param constraint  the constraint (<code>null</code> not permitted).
     * 
     * @return The block size (in Java2D units, never <code>null</code>).
     */
    public Size2D arrange(Graphics2D g2, RectangleConstraint constraint) {
        g2.setFont(this.font);
        Size2D s = this.label.calculateDimensions(g2);
        return new Size2D(calculateTotalWidth(s.getWidth()), 
                calculateTotalHeight(s.getHeight()));
    }
    
    /**
     * Draws the block.
     * 
     * @param g2  the graphics device.
     * @param area  the area.
     */
    public void draw(Graphics2D g2, Rectangle2D area) {
        draw(g2, area, null);
    }
    
    /**
     * Draws the block within the specified area.
     * 
     * @param g2  the graphics device.
     * @param area  the area.
     * @param params  ignored (<code>null</code> permitted).
     * 
     * @return Always <code>null</code>.
     */
    public Object draw(Graphics2D g2, Rectangle2D area, Object params) {
        area = trimMargin(area);
        drawBorder(g2, area);
        area = trimBorder(area);
        area = trimPadding(area);
        
        // check if we need to collect chart entities from the container
        EntityBlockParams ebp = null;
        StandardEntityCollection sec = null;
        Shape entityArea = null;
        if (params instanceof EntityBlockParams) {
            ebp = (EntityBlockParams) params;
            if (ebp.getGenerateEntities()) {
                sec = new StandardEntityCollection();
                entityArea = (Shape) area.clone();
            }
        }
        g2.setPaint(this.paint);
        g2.setFont(this.font);
        this.label.draw(g2, (float) area.getX(), (float) area.getY(), 
                TextBlockAnchor.TOP_LEFT);
        BlockResult result = null;
        if (ebp != null && sec != null) {
            if (this.toolTipText != null || this.urlText != null) {
                ChartEntity entity = new ChartEntity(entityArea, 
                        this.toolTipText, this.urlText);   
                sec.add(entity);
                result = new BlockResult();
                result.setEntityCollection(sec);
            }
        }
        return result;
    }
    
    /**
     * Tests this <code>LabelBlock</code> for equality with an arbitrary 
     * object.
     * 
     * @param obj  the object (<code>null</code> permitted).
     * 
     * @return A boolean.
     */
    public boolean equals(Object obj) {
        if (!(obj instanceof LabelBlock)) {
            return false;
        }
        LabelBlock that = (LabelBlock) obj;
        if (!this.text.equals(that.text)) {
            return false;
        }
        if (!this.font.equals(that.font)) {
            return false;
        }
        if (!PaintUtilities.equal(this.paint, that.paint)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.toolTipText, that.toolTipText)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.urlText, that.urlText)) {
            return false;
        }
        if (!super.equals(obj)) {
            return false;
        }
        return true;
    }

    /**
     * Returns a clone of this <code>LabelBlock</code> instance.
     * 
     * @return A clone.
     * 
     * @throws CloneNotSupportedException if there is a problem cloning.
     */
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
