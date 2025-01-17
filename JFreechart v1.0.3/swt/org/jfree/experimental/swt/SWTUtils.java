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
 * -------------
 * SWTUtils.java
 * -------------
 * (C) Copyright 2006, by Henry Proudhon and Contributors.
 *
 * Original Author:  Henry Proudhon (henry.proudhon AT insa-lyon.fr);
 * Contributor(s):
 *
 * Changes
 * -------
 * 1 Aug 2006 : New class (HP);
 * 
 */

package org.jfree.experimental.swt;

import javax.swing.JPanel;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.GC;

/**
 * Utility class gathering some useful and general method.
 * Mainly convert forth and back graphical stuff between 
 * awt and swt.
 */
public class SWTUtils {
    
    private final static String Az = "ABCpqr";

    /**
     * Create a <code>FontData</code> object which encapsulate 
     * the essential data to create a swt font. The data is taken 
     * from the provided awt Font.
     * <p>Generally speaking, given a font size, the returned swt font 
     * will display differently on the screen than the awt one.
     * Because the SWT toolkit use native graphical ressources whenever 
     * it is possible, this fact is plateform dependent. To address 
     * this issue, it is possible to enforce the method to return 
     * a font with the same size (or at least as close as possible) 
     * as the awt one.
     * <p>When the object is no more used, the user must explicitely 
     * call the dispose method on the returned font to free the 
     * operating system resources (the garbage collector won't do it).
     * 
     * @param device The swt device to draw on (display or gc device).
     * @param font The awt font from which to get the data.
     * @param ensureSameSize A boolean used to enforce the same size 
     * (in pixels) between the awt font and the newly created swt font.
     * @return a <code>FontData</code> object.
     */
    public static FontData toSwtFontData(Device device, java.awt.Font font, 
            boolean ensureSameSize) {
        FontData fontData = new FontData();
        fontData.setName(font.getFamily());
        int style = SWT.NORMAL;
        switch (font.getStyle()) {
            case java.awt.Font.PLAIN:
                style |= SWT.NORMAL;
                break;
            case java.awt.Font.BOLD:
                style |= SWT.BOLD;
                break;
            case java.awt.Font.ITALIC:
                style |= SWT.ITALIC;
                break;
            case (java.awt.Font.ITALIC + java.awt.Font.BOLD):
                style |= SWT.ITALIC | SWT.BOLD;
                break;
        }
        fontData.setStyle(style);
        // convert the font size (in pt for awt) to height in pixels for swt
        int height = (int) Math.round(font.getSize() * 72.0 
                / device.getDPI().y);
        fontData.setHeight(height);
        // hack to ensure the newly created swt fonts will be rendered with the
        // same height as the awt one
        if (ensureSameSize) {            
            GC tmpGC = new GC(device);
            JPanel DUMMY_PANEL = new JPanel();
            Font tmpFont = new Font(device, fontData);
            tmpGC.setFont(tmpFont);
            if (tmpGC.textExtent(Az).x 
                    > DUMMY_PANEL.getFontMetrics(font).stringWidth(Az)) {
                while (tmpGC.textExtent(Az).x 
                        > DUMMY_PANEL.getFontMetrics(font).stringWidth(Az)) {
                    tmpFont.dispose();
                    height--;
                    fontData.setHeight(height);
                    tmpFont = new Font(device, fontData);
                    tmpGC.setFont(tmpFont);
                }
            }
            else if (tmpGC.textExtent(Az).x 
                    < DUMMY_PANEL.getFontMetrics(font).stringWidth(Az)) {
                while (tmpGC.textExtent(Az).x 
                        < DUMMY_PANEL.getFontMetrics(font).stringWidth(Az)) {
                    tmpFont.dispose();
                    height++;
                    fontData.setHeight(height);
                    tmpFont = new Font(device, fontData);
                    tmpGC.setFont(tmpFont);
                }
            }
            tmpFont.dispose();
            tmpGC.dispose();
        }
        return fontData;
    }
    
    /**
     * Create an awt font by converting as much information 
     * as possible from the provided swt <code>FontData</code>.
     * <p>Generally speaking, given a font size, an swt font will 
     * display differently on the screen than the corresponding awt 
     * one. Because the SWT toolkit use native graphical ressources whenever 
     * it is possible, this fact is plateform dependent. To address 
     * this issue, it is possible to enforce the method to return 
     * an awt font with the same height as the swt one.
     * 
     * @param device The swt device being drawn on (display or gc device).
     * @param fontData The swt font to convert.
     * @param ensureSameSize A boolean used to enforce the same size 
     * (in pixels) between the swt font and the newly created awt font.
     * @return An awt font converted from the provided swt font.
     */
    public static java.awt.Font toAwtFont(Device device, FontData fontData, 
            boolean ensureSameSize) {
        int style;
        switch (fontData.getStyle()) {
            case SWT.NORMAL:
                style = java.awt.Font.PLAIN;
                break;
            case SWT.ITALIC:
                style = java.awt.Font.ITALIC;
                break;
            case SWT.BOLD:
                style = java.awt.Font.BOLD;
                break;
            default:
                style = java.awt.Font.PLAIN;
                break;
        }
        int height = (int) Math.round(fontData.height * device.getDPI().y 
                / 72.0);
        // hack to ensure the newly created awt fonts will be rendered with the
        // same height as the swt one
        if (ensureSameSize) {
            GC tmpGC = new GC(device);
            Font tmpFont = new Font(device, fontData);
            tmpGC.setFont(tmpFont);
            JPanel DUMMY_PANEL = new JPanel();
            java.awt.Font tmpAwtFont = new java.awt.Font(fontData.getName(), 
                    style, height);
            if (DUMMY_PANEL.getFontMetrics(tmpAwtFont).stringWidth(Az) 
                    > tmpGC.textExtent(Az).x) {
                while (DUMMY_PANEL.getFontMetrics(tmpAwtFont).stringWidth(Az) 
                        > tmpGC.textExtent(Az).x) {
                    height--;                
                    tmpAwtFont = new java.awt.Font(fontData.getName(), style, 
                            height);
                }
            }
            else if (DUMMY_PANEL.getFontMetrics(tmpAwtFont).stringWidth(Az) 
                    < tmpGC.textExtent(Az).x) {
                while (DUMMY_PANEL.getFontMetrics(tmpAwtFont).stringWidth(Az) 
                        < tmpGC.textExtent(Az).x) {
                    height++;
                    tmpAwtFont = new java.awt.Font(fontData.getName(), style, 
                            height);
                }
            }
            tmpFont.dispose();
            tmpGC.dispose();
        }
        return new java.awt.Font(fontData.getName(), style, height);
    }

    /**
     * Create an awt font by converting as much information 
     * as possible from the provided swt <code>Font</code>.
     * 
     * @param device The swt device to draw on (display or gc device).
     * @param font The swt font to convert.
     * @return An awt font converted from the provided swt font.
     */
    public static java.awt.Font toAwtFont(Device device, Font font) {
        FontData fontData = font.getFontData()[0]; 
        return toAwtFont(device, fontData, true);
    }

    /**
     * Creates an awt color instance to match the rgb values 
     * of the specified swt color.
     * 
     * @param color The swt color to match.
     * @return an awt color abject.
     */
    public static java.awt.Color toAwtColor(Color color) {
        return new java.awt.Color(color.getRed(), color.getGreen(), 
                color.getBlue());
    }
    
    /**
     * Creates a swt color instance to match the rgb values 
     * of the specified awt paint. For now, this method test 
     * if the paint is a color and then return the adequate 
     * swt color. Otherwise plain black is assumed.
     * 
     * @param device The swt device to draw on (display or gc device).
     * @param paint The awt color to match.
     * @return a swt color object.
     */
    public static Color toSwtColor(Device device, java.awt.Paint paint) {
        java.awt.Color color;
        if (paint instanceof java.awt.Color) {
            color = (java.awt.Color) paint;
        }
        else {
            try {
                throw new Exception("only color is supported at present... " 
                        + "setting paint to uniform black color" );
            } 
            catch (Exception e) {
                e.printStackTrace();
                color = new java.awt.Color(0, 0, 0);
            }
        }
        return new org.eclipse.swt.graphics.Color(device,
                color.getRed(), color.getGreen(), color.getBlue());
    }

    /**
     * Creates a swt color instance to match the rgb values 
     * of the specified awt color. alpha channel is not supported.
     * Note that the dispose method will need to be called on the 
     * returned object.
     * 
     * @param device The swt device to draw on (display or gc device).
     * @param color The awt color to match.
     * @return a swt color object.
     */
    public static Color toSwtColor(Device device, java.awt.Color color) {
        return new org.eclipse.swt.graphics.Color(device,
                color.getRed(), color.getGreen(), color.getBlue());
    }
}
