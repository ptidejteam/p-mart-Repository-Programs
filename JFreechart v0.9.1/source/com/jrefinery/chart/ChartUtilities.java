/* =======================================
 * JFreeChart : a Java Chart Class Library
 * =======================================
 *
 * Project Info:  http://www.object-refinery.com/jfreechart/index.html
 * Project Lead:  David Gilbert (david.gilbert@object-refinery.com);
 *
 * (C) Copyright 2000-2002, by Simba Management Limited and Contributors.
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
 * -------------------
 * ChartUtilities.java
 * -------------------
 * (C) Copyright 2001, 2002, by Simba Management Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   Wolfgang Irler;
 *
 * $Id: ChartUtilities.java,v 1.1 2007/10/10 19:02:26 vauchers Exp $
 *
 * Changes
 * -------
 * 11-Dec-2001 : Version 1.  The JPEG method comes from Wolfgang Irler's JFreeChartServletDemo
 *               class (DG);
 * 23-Jan-2002 : Changed saveChartAsXXX(...) methods to pass IOExceptions back to caller (DG);
 *
 */

package com.jrefinery.chart;

import java.awt.image.*;
import java.io.*;
import com.keypoint.*;
import com.sun.image.codec.jpeg.*;

/**
 * Utility methods for JFreeChart.
 */
public class ChartUtilities {

    /**
     * Writes the chart to the output stream in PNG format.
     * @param out The output stream.
     * @param chart The chart.
     * @param width The image width.
     * @param height The image height.
     */
    public static void writeChartAsPNG(OutputStream out, JFreeChart chart, int width, int height)
        throws IOException {

        BufferedImage chartImage = chart.createBufferedImage(width, height);
        PngEncoder encoder = new PngEncoder(chartImage, false, 0, 9);
        byte[] pngData = encoder.pngEncode();
        out.write(pngData);

    }

    /**
     * Saves the chart as a PNG format image file.
     * @param chart The chart.
     * @param width The image width.
     * @param height The image height.
     * @param file The file.
     */
    public static void saveChartAsPNG(File file, JFreeChart chart, int width, int height)
        throws IOException {

        DataOutputStream out = new DataOutputStream(
                                   new BufferedOutputStream(new FileOutputStream(file)));
        writeChartAsPNG(out, chart, width, height);
        out.close();

    }

    /**
     * Writes the chart to the output stream in JPEG format.
     * @param out The output stream.
     * @param chart The chart.
     * @param width The image width.
     * @param height The image height.
     */
    public static void writeChartAsJPEG(OutputStream out, JFreeChart chart, int width, int height)
        throws IOException {

        BufferedImage image = chart.createBufferedImage(width, height);
        JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);
        JPEGEncodeParam param = encoder.getDefaultJPEGEncodeParam(image);
        param.setQuality(1.0f, true);
        encoder.encode(image, param);

    }

    /**
     * Saves the chart as a JPEG format image file.
     * @param file The file.
     * @param chart The chart.
     * @param width The image width.
     * @param height The image height.
     */
    public static void saveChartAsJPEG(File file, JFreeChart chart, int width, int height)
        throws IOException {

        OutputStream out = new BufferedOutputStream(new FileOutputStream(file));
        writeChartAsJPEG(out, chart, width, height);
        out.close();

    }

}