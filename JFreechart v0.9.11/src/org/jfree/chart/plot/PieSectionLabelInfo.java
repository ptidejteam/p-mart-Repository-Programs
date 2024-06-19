package org.jfree.chart.plot;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
/**
 * @author bschaeff
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class PieSectionLabelInfo {

    private int section;
    private String label;
    private Point2D.Double linePoint;
    private Rectangle2D labelBounds;
    private double ascent;
    
    // don't forget to incoporate sectionLabelGap when calculating label positions
    
    // idea is to store this info in a collection
    // draw the pie sections first, creating a PieSectionLabelInfo object as you go
    // when pie finished, draw labels
    // draw exploded sections first, then unexploded
    // drawing this way, can keep labels from overlapping exploded pie sections
    // and eachother.
    
    /**
     * Constructor for PieSection.
     */
    public PieSectionLabelInfo() {
        super();
    }


    /**
     * Sets the sectionNum.
     * @param sectionNum The sectionNum to set
     */
    public void setSection(int section) {
        this.section = section;
    }


    /**
     * Returns the section.
     * @return int
     */
    public int getSection() {
        return section;
    }


    /**
     * Returns the label.
     * @return String
     */
    public String getLabel() {
        return label;
    }

    /**
     * Sets the label.
     * @param label The label to set
     */
    public void setLabel(String label) {
        this.label = label;
    }
    
    public void setLabelBounds(Font font, Graphics2D g){
        this.labelBounds = font.getStringBounds(this.label, g.getFontRenderContext());
    }

    /**
     * Returns the labelBounds.
     * @return Rectangle2D
     */
    public Rectangle2D getLabelBounds() {
        return labelBounds;
    }
    
    public void setAscent(Font font, Graphics2D g){
        this.ascent = (font.getLineMetrics(this.label, g.getFontRenderContext())).getAscent();  
    }

    /**
     * Returns the ascent.
     * @return double
     */
    public double getAscent() {
        return ascent;
    }

    /**
     * Returns the linePoint.
     * @return Point2D.Double
     */
    public Point2D.Double getLinePoint() {
        return linePoint;
    }

    /**
     * Sets the linePoint.
     * @param linePoint The linePoint to set
     */
    public void setLinePoint(Point2D.Double linePoint) {
        this.linePoint = linePoint;
    }

}
