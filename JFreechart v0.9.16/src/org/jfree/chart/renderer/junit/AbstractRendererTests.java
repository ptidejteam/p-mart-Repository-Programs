/* ===========================================================
 * JFreeChart : a free chart library for the Java(tm) platform
 * ===========================================================
 *
 * (C) Copyright 2000-2004, by Object Refinery Limited and Contributors.
 *
 * Project Info:  http://www.jfree.org/jfreechart/index.html
 *
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA 02111-1307, USA.
 *
 * [Java is a trademark or registered trademark of Sun Microsystems, Inc. 
 * in the United States and other countries.]
 *
 * --------------------------
 * AbstractRendererTests.java
 * --------------------------
 * (C) Copyright 2003, 2004, by Object Refinery Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * $Id: AbstractRendererTests.java,v 1.1 2007/10/10 19:25:33 vauchers Exp $
 *
 * Changes
 * -------
 * 23-Oct-2003 : Version 1 (DG);
 *
 */

package org.jfree.chart.renderer.junit;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.geom.Rectangle2D;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.jfree.chart.renderer.BarRenderer;
import org.jfree.chart.renderer.ItemLabelAnchor;
import org.jfree.chart.renderer.ItemLabelPosition;
import org.jfree.ui.TextAnchor;

/**
 * Tests for the {@link AbstractRenderer} class.
 *
 * @author David Gilbert
 */
public class AbstractRendererTests extends TestCase {

    /**
     * Returns the tests as a test suite.
     *
     * @return the test suite.
     */
    public static Test suite() {
        return new TestSuite(AbstractRendererTests.class);
    }

    /**
     * Constructs a new set of tests.
     *
     * @param  name the name of the tests.
     */
    public AbstractRendererTests(String name) {
        super(name);
    }

    /**
     * Tests each setter method to ensure that it sends an event notification.
     */
    public void testEventNotification() {
        
        RendererChangeDetector detector = new RendererChangeDetector();
        BarRenderer r1 = new BarRenderer();  // have to use a subclass of AbstractRenderer
        r1.addChangeListener(detector);
        
        // PAINT
        detector.setNotified(false);
        r1.setPaint(Color.red);
        assertTrue(detector.getNotified());

        detector.setNotified(false);
        r1.setSeriesPaint(0, Color.red);
        assertTrue(detector.getNotified());

        detector.setNotified(false);
        r1.setBasePaint(Color.red);
        assertTrue(detector.getNotified());

        // OUTLINE PAINT
        detector.setNotified(false);
        r1.setOutlinePaint(Color.red);
        assertTrue(detector.getNotified());

        detector.setNotified(false);
        r1.setSeriesOutlinePaint(0, Color.red);
        assertTrue(detector.getNotified());

        detector.setNotified(false);
        r1.setBaseOutlinePaint(Color.red);
        assertTrue(detector.getNotified());
        
        // STROKE
        detector.setNotified(false);
        r1.setStroke(new BasicStroke(1.0f));
        assertTrue(detector.getNotified());

        detector.setNotified(false);
        r1.setSeriesStroke(0, new BasicStroke(1.0f));
        assertTrue(detector.getNotified());

        detector.setNotified(false);
        r1.setBaseStroke(new BasicStroke(1.0f));
        assertTrue(detector.getNotified());

        // OUTLINE STROKE
        detector.setNotified(false);
        r1.setOutlineStroke(new BasicStroke(1.0f));
        assertTrue(detector.getNotified());

        detector.setNotified(false);
        r1.setSeriesOutlineStroke(0, new BasicStroke(1.0f));
        assertTrue(detector.getNotified());

        detector.setNotified(false);
        r1.setBaseOutlineStroke(new BasicStroke(1.0f));
        assertTrue(detector.getNotified());

        // SHAPE
        detector.setNotified(false);
        r1.setShape(new Rectangle2D.Float());
        assertTrue(detector.getNotified());

        detector.setNotified(false);
        r1.setSeriesShape(0, new Rectangle2D.Float());
        assertTrue(detector.getNotified());

        detector.setNotified(false);
        r1.setBaseShape(new Rectangle2D.Float());
        assertTrue(detector.getNotified());

        // ITEM_LABELS_VISIBLE
        detector.setNotified(false);
        r1.setItemLabelsVisible(Boolean.TRUE);
        assertTrue(detector.getNotified());

        detector.setNotified(false);
        r1.setSeriesItemLabelsVisible(0, Boolean.TRUE);
        assertTrue(detector.getNotified());

        detector.setNotified(false);
        r1.setBaseItemLabelsVisible(Boolean.TRUE);
        assertTrue(detector.getNotified());
        
        // ITEM_LABEL_FONT
        detector.setNotified(false);
        r1.setItemLabelFont(new Font("Serif", Font.PLAIN, 12));
        assertTrue(detector.getNotified());

        detector.setNotified(false);
        r1.setSeriesItemLabelFont(0, new Font("Serif", Font.PLAIN, 12));
        assertTrue(detector.getNotified());

        detector.setNotified(false);
        r1.setBaseItemLabelFont(new Font("Serif", Font.PLAIN, 12));
        assertTrue(detector.getNotified());
        
        // ITEM_LABEL_PAINT
        detector.setNotified(false);
        r1.setItemLabelPaint(Color.blue);
        assertTrue(detector.getNotified());

        detector.setNotified(false);
        r1.setSeriesItemLabelPaint(0, Color.blue);
        assertTrue(detector.getNotified());

        detector.setNotified(false);
        r1.setBaseItemLabelPaint(Color.blue);
        assertTrue(detector.getNotified());
        
        // POSITIVE ITEM LABEL POSITION
        detector.setNotified(false);
        r1.setPositiveItemLabelPosition(new ItemLabelPosition(ItemLabelAnchor.CENTER,
                                                              TextAnchor.CENTER,
                                                              TextAnchor.CENTER,
                                                              0.0));
        assertTrue(detector.getNotified());

        detector.setNotified(false);
        r1.setSeriesPositiveItemLabelPosition(0, new ItemLabelPosition(ItemLabelAnchor.CENTER,
                                                                       TextAnchor.CENTER,
                                                                       TextAnchor.CENTER,
                                                                       0.0));
        assertTrue(detector.getNotified());

        detector.setNotified(false);
        r1.setBasePositiveItemLabelPosition(new ItemLabelPosition(ItemLabelAnchor.CENTER,
                                            TextAnchor.CENTER,
                                            TextAnchor.CENTER,
                                            0.0));
        assertTrue(detector.getNotified());
        
//        // POSITIVE ITEM LABEL TEXT ANCHOR
//        detector.setNotified(false);
//        r1.setPositiveItemLabelTextAnchor(TextAnchor.TOP_LEFT);
//        assertTrue(detector.getNotified());
//
//        detector.setNotified(false);
//        r1.setSeriesPositiveItemLabelTextAnchor(0, TextAnchor.TOP_LEFT);
//        assertTrue(detector.getNotified());
//
//        detector.setNotified(false);
//        r1.setBasePositiveItemLabelTextAnchor(TextAnchor.TOP_LEFT);
//        assertTrue(detector.getNotified());
//        
//        // POSITIVE ITEM LABEL ROTATION ANCHOR
//        detector.setNotified(false);
//        r1.setPositiveItemLabelRotationAnchor(TextAnchor.TOP_LEFT);
//        assertTrue(detector.getNotified());
//
//        detector.setNotified(false);
//        r1.setSeriesPositiveItemLabelRotationAnchor(0, TextAnchor.TOP_LEFT);
//        assertTrue(detector.getNotified());
//
//        detector.setNotified(false);
//        r1.setBasePositiveItemLabelRotationAnchor(TextAnchor.TOP_LEFT);
//        assertTrue(detector.getNotified());
//        
//        // POSITIVE ITEM LABEL ANGLE
//        detector.setNotified(false);
//        r1.setPositiveItemLabelAngle(new Double(Math.PI));
//        assertTrue(detector.getNotified());
//
//        detector.setNotified(false);
//        r1.setSeriesPositiveItemLabelAngle(0, new Double(Math.PI));
//        assertTrue(detector.getNotified());
//
//        detector.setNotified(false);
//        r1.setBasePositiveItemLabelAngle(new Double(Math.PI));
//        assertTrue(detector.getNotified());
        
        // NEGATIVE ITEM LABEL ANCHOR
        detector.setNotified(false);
        r1.setNegativeItemLabelPosition(new ItemLabelPosition(ItemLabelAnchor.CENTER,
                                                              TextAnchor.CENTER,
                                                              TextAnchor.CENTER,
                                                              0.0));
        assertTrue(detector.getNotified());

        detector.setNotified(false);
        r1.setSeriesNegativeItemLabelPosition(0, new ItemLabelPosition(ItemLabelAnchor.CENTER,
                                                                       TextAnchor.CENTER,
                                                                       TextAnchor.CENTER,
                                                                       0.0));
        assertTrue(detector.getNotified());

        detector.setNotified(false);
        r1.setBaseNegativeItemLabelPosition(new ItemLabelPosition(ItemLabelAnchor.CENTER,
                                                                  TextAnchor.CENTER,
                                                                  TextAnchor.CENTER,
                                                                  0.0));
        assertTrue(detector.getNotified());
        
//        // NEGATIVE ITEM LABEL TEXT ANCHOR
//        detector.setNotified(false);
//        r1.setNegativeItemLabelTextAnchor(TextAnchor.TOP_LEFT);
//        assertTrue(detector.getNotified());
//
//        detector.setNotified(false);
//        r1.setSeriesNegativeItemLabelTextAnchor(0, TextAnchor.TOP_LEFT);
//        assertTrue(detector.getNotified());
//
//        detector.setNotified(false);
//        r1.setBaseNegativeItemLabelTextAnchor(TextAnchor.TOP_LEFT);
//        assertTrue(detector.getNotified());
//        
//        // NEGATIVE ITEM LABEL ROTATION ANCHOR
//        detector.setNotified(false);
//        r1.setNegativeItemLabelRotationAnchor(TextAnchor.TOP_LEFT);
//        assertTrue(detector.getNotified());
//
//        detector.setNotified(false);
//        r1.setSeriesNegativeItemLabelRotationAnchor(0, TextAnchor.TOP_LEFT);
//        assertTrue(detector.getNotified());
//
//        detector.setNotified(false);
//        r1.setBaseNegativeItemLabelRotationAnchor(TextAnchor.TOP_LEFT);
//        assertTrue(detector.getNotified());
//        
//        // NEGATIVE ITEM LABEL ANGLE
//        detector.setNotified(false);
//        r1.setNegativeItemLabelAngle(new Double(Math.PI));
//        assertTrue(detector.getNotified());
//
//        detector.setNotified(false);
//        r1.setSeriesNegativeItemLabelAngle(0, new Double(Math.PI));
//        assertTrue(detector.getNotified());
//
//        detector.setNotified(false);
//        r1.setBaseNegativeItemLabelAngle(new Double(Math.PI));
//        assertTrue(detector.getNotified());

    }

}
