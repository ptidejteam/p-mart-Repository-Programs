/* ======================================
 * JFreeChart : a free Java chart library
 * ======================================
 *
 * Project Info:  http://www.jfree.org/jfreechart/index.html
 * Project Lead:  David Gilbert (david.gilbert@object-refinery.com);
 *
 * (C) Copyright 2000-2003, by Object Refinery Limited and Contributors.
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
 * (C) Copyright 2001-2003, by Object Refinery Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   Wolfgang Irler;
 *                   Richard Atkinson (richard_c_atkinson@ntlworld.com);
 *                   Xavier Poinsard;
 *
 * $Id: ChartUtilities.java,v 1.1 2007/10/10 20:07:37 vauchers Exp $
 *
 * Changes
 * -------
 * 11-Dec-2001 : Version 1.  The JPEG method comes from Wolfgang Irler's JFreeChartServletDemo
 *               class (DG);
 * 23-Jan-2002 : Changed saveChartAsXXX(...) methods to pass IOExceptions back to caller (DG);
 * 26-Jun-2002 : Added image map methods (DG);
 * 05-Aug-2002 : Added writeBufferedImage methods
 *               Modified writeImageMap method to support flexible image maps (RA);
 * 26-Aug-2002 : Added saveChartAsJPEG and writeChartAsJPEG methods with info objects (RA);
 * 05-Sep-2002 : Added writeImageMap(...) method to support OverLIB
 *               - http://www.bosrup.com/web/overlib (RA);
 * 26-Sep-2002 : Fixed errors reported by Checkstyle (DG);
 * 17-Oct-2002 : Exposed JPEG quality setting and PNG compression level as parameters (DG);
 * 25-Oct-2002 : Fixed writeChartAsJPEG(...) empty method bug (DG);
 * 13-Mar-2003 : Updated writeImageMap method as suggested by Xavier Poinsard (see
 *               Feature Request 688079) (DG);
 */

package org.jfree.chart;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Iterator;

import org.jfree.chart.entity.ChartEntity;
import org.jfree.chart.entity.EntityCollection;

import com.keypoint.PngEncoder;
import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGEncodeParam;
import com.sun.image.codec.jpeg.JPEGImageEncoder;

/**
 * Utility methods for JFreeChart.  Includes methods for converting charts to
 * image formats (PNG and JPEG) plus creating simple HTML image maps.
 *
 * @author David Gilbert
 */
public class ChartUtilities {

    /** The default JPEG quality setting. */
    private static final float DEFAULT_JPEG_QUALITY = 0.75f;

    /** The default PNG compression level. */
    private static final int DEFAULT_PNG_COMPRESSION = 9;

    /**
     * Writes the chart to the output stream in PNG format.
     *
     * @param out  the output stream.
     * @param chart  the chart.
     * @param width  the image width.
     * @param height  the image height.
     *
     * @throws IOException if there are any I/O errors.
     */
    public static void writeChartAsPNG(OutputStream out,
                                       JFreeChart chart,
                                       int width, int height) throws IOException {

        writeChartAsPNG(out, chart, width, height, null, false, DEFAULT_PNG_COMPRESSION);

    }

    /**
     * Writes the chart to the output stream in PNG format.
     *
     * @param out  the output stream.
     * @param chart  the chart.
     * @param width  the image width.
     * @param height  the image height.
     * @param encodeAlpha  encode alpha?
     * @param compression  the compression level.
     *
     * @throws IOException if there are any I/O errors.
     */
    public static void writeChartAsPNG(OutputStream out,
                                       JFreeChart chart,
                                       int width, int height,
                                       boolean encodeAlpha,
                                       int compression) throws IOException {

        ChartUtilities.writeChartAsPNG(out, chart, width, height, null, encodeAlpha, compression);

    }

    /**
     * Writes the chart to the output stream in PNG format.
     * <P>
     * This method allows you to pass in a ChartRenderingInfo object, to
     * collect information about the chart dimensions/entities.  You will need
     * this info if you want to create an HTML image map.
     *
     * @param out  the output stream.
     * @param chart  the chart.
     * @param width  the image width.
     * @param height  the image height.
     * @param info  the chart rendering info.
     *
     * @throws IOException if there are any I/O errors.
     */
    public static void writeChartAsPNG(OutputStream out,
                                       JFreeChart chart,
                                       int width, int height,
                                       ChartRenderingInfo info) throws IOException {

        writeChartAsPNG(out, chart, width, height, info, false, DEFAULT_PNG_COMPRESSION);

    }

    /**
     * Writes the chart to the output stream in PNG format.
     * <P>
     * This method allows you to pass in a ChartRenderingInfo object, to
     * collect information about the chart dimensions/entities.  You will need
     * this info if you want to create an HTML image map.
     *
     * @param out  the output stream.
     * @param chart  the chart.
     * @param width  the image width.
     * @param height  the image height.
     * @param info  the chart rendering info.
     * @param encodeAlpha  encode alpha?
     * @param compression  the PNG compression level.
     *
     * @throws IOException if there are any I/O errors.
     */
    public static void writeChartAsPNG(OutputStream out,
                                       JFreeChart chart,
                                       int width, int height,
                                       ChartRenderingInfo info,
                                       boolean encodeAlpha,
                                       int compression) throws IOException {

        BufferedImage chartImage = chart.createBufferedImage(width, height, info);
        ChartUtilities.writeBufferedImageAsPNG(out, chartImage, encodeAlpha, compression);

    }

    /**
     * Writes a scaled version of a chart to an output stream in PNG format.
     *
     * @param out  the output stream.
     * @param chart  the chart.
     * @param width  the unscaled chart width.
     * @param height  the unscaled chart height.
     * @param widthScaleFactor  the horizontal scale factor.
     * @param heightScaleFactor  the vertical scale factor.
     *
     * @throws IOException if there are any I/O problems.
     */
    public static void writeScaledChartAsPNG(OutputStream out,
                                             JFreeChart chart,
                                             int width,
                                             int height,
                                             int widthScaleFactor,
                                             int heightScaleFactor) throws IOException {

        double desiredWidth = width * widthScaleFactor;
        double desiredHeight = height * heightScaleFactor;
        double defaultWidth = width;
        double defaultHeight = height;
        boolean scale = false;

        // get desired width and height from somewhere then...
        if ((widthScaleFactor != 1) || (heightScaleFactor != 1)) {
            scale = true;
        }

        double scaleX = desiredWidth / defaultWidth;
        double scaleY = desiredHeight / defaultHeight;

        //new BufferedImage()
        BufferedImage image = new BufferedImage((int) desiredWidth, (int) desiredHeight,
                                                BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = image.createGraphics();

        if (scale) {
            AffineTransform saved = g2.getTransform();
            g2.transform(AffineTransform.getScaleInstance(scaleX, scaleY));
            chart.draw(g2, new Rectangle2D.Double(0, 0, defaultWidth, defaultHeight), null);
            g2.setTransform(saved);
            g2.dispose();
        }
        else {
            chart.draw(g2, new Rectangle2D.Double(0, 0, defaultWidth, defaultHeight), null);
        }

        //return image;
        PngEncoder encoder = new PngEncoder(image, false, 0, 9);
        byte[] pngData = encoder.pngEncode();
        out.write(pngData);
    }

    /**
     * Saves the chart as a PNG format image file.
     *
     * @param file  the file name.
     * @param chart  the chart.
     * @param width  the image width.
     * @param height  the image height.
     *
     * @throws IOException if there are any I/O errors.
     */
    public static void saveChartAsPNG(File file,
                                      JFreeChart chart,
                                      int width, int height) throws IOException {

        saveChartAsPNG(file, chart, width, height, null);

    }

    /**
     * Saves the chart as a PNG format image file.
     * <P>
     * This method allows you to pass in a ChartRenderingInfo object, to collect
     * information about the chart dimensions/entities.  You will need this info
     * if you want to create an HTML image map.
     *
     * @param file  the file.
     * @param chart  the chart.
     * @param width  the image width.
     * @param height  the image height.
     * @param info  the chart rendering info.
     *
     * @throws IOException if there are any I/O errors.
     */
    public static void saveChartAsPNG(File file,
                                      JFreeChart chart,
                                      int width, int height,
                                      ChartRenderingInfo info) throws IOException {

        saveChartAsPNG(file, chart, width, height, info, false, DEFAULT_PNG_COMPRESSION);

    }

    /**
     * Saves the chart as a PNG format image file.
     * <P>
     * This method allows you to pass in a ChartRenderingInfo object, to collect
     * information about the chart dimensions/entities.  You will need this info
     * if you want to create an HTML image map.
     *
     * @param file  the file.
     * @param chart  the chart.
     * @param width  the image width.
     * @param height  the image height.
     * @param info  the chart rendering info.
     * @param encodeAlpha  encode alpha?
     * @param compression  the PNG compression level.
     *
     * @throws IOException if there are any I/O errors.
     */
    public static void saveChartAsPNG(File file,
                                      JFreeChart chart,
                                      int width, int height,
                                      ChartRenderingInfo info,
                                      boolean encodeAlpha,
                                      int compression) throws IOException {

        OutputStream out = new BufferedOutputStream(new FileOutputStream(file));
        writeChartAsPNG(out, chart, width, height, info, encodeAlpha, compression);
        out.close();

    }

    /**
     * Writes the chart to the output stream in JPEG format.
     *
     * @param out  the output stream.
     * @param chart  the chart.
     * @param width  the image width.
     * @param height  the image height.
     *
     * @throws IOException if there are any I/O errors.
     */
    public static void writeChartAsJPEG(OutputStream out,
                                        JFreeChart chart,
                                        int width, int height) throws IOException {

        writeChartAsJPEG(out, DEFAULT_JPEG_QUALITY, chart, width, height, null);

    }

    /**
     * Writes the chart to the output stream in JPEG format.
     *
     * @param out  the output stream.
     * @param quality  the quality setting.
     * @param chart  the chart.
     * @param width  the image width.
     * @param height  the image height.
     *
     * @throws IOException if there are any I/O errors.
     */
    public static void writeChartAsJPEG(OutputStream out, float quality,
                                        JFreeChart chart,
                                        int width, int height) throws IOException {

        ChartUtilities.writeChartAsJPEG(out, quality, chart, width, height, null);

    }

    /**
     * Writes the chart to the output stream in JPEG format.
     * <P>
     * This method allows you to pass in a ChartRenderingInfo object, to
     * collect information about the chart dimensions/entities.  You will need
     * this info if you want to create an HTML image map.
     *
     * @param out  the output stream.
     * @param chart  the chart.
     * @param width  the image width.
     * @param height  the image height.
     * @param info  the chart rendering info.
     *
     * @throws IOException if there are any I/O errors.
     */
    public static void writeChartAsJPEG(OutputStream out,
                                        JFreeChart chart,
                                        int width, int height,
                                        ChartRenderingInfo info) throws IOException {

        writeChartAsJPEG(out, DEFAULT_JPEG_QUALITY, chart, width, height, info);

    }

    /**
     * Writes the chart to the output stream in JPEG format.
     * <P>
     * This method allows you to pass in a ChartRenderingInfo object, to
     * collect information about the chart dimensions/entities.  You will need
     * this info if you want to create an HTML image map.
     *
     * @param out  the output stream.
     * @param quality  the output quality (0.0f to 1.0f).
     * @param chart  the chart.
     * @param width  the image width.
     * @param height  the image height.
     * @param info  the chart rendering info.
     *
     * @throws IOException if there are any I/O errors.
     */
    public static void writeChartAsJPEG(OutputStream out, float quality,
                                        JFreeChart chart,
                                        int width, int height,
                                        ChartRenderingInfo info) throws IOException {

        BufferedImage chartImage = chart.createBufferedImage(width, height, info);
        ChartUtilities.writeBufferedImageAsJPEG(out, quality, chartImage);

    }

    /**
     * Saves the chart as a JPEG format image file.
     *
     * @param file  the file.
     * @param chart  the chart.
     * @param width  the image width.
     * @param height  the image height.
     *
     * @throws IOException if there are any I/O errors.
     */
    public static void saveChartAsJPEG(File file,
                                       JFreeChart chart,
                                       int width, int height) throws IOException {

        saveChartAsJPEG(file, DEFAULT_JPEG_QUALITY, chart, width, height, null);

    }

    /**
     * Saves the chart as a JPEG format image file.
     *
     * @param file  the file.
     * @param quality  the JPEG quality setting.
     * @param chart  the chart.
     * @param width  the image width.
     * @param height  the image height.
     *
     * @throws IOException if there are any I/O errors.
     */
    public static void saveChartAsJPEG(File file, float quality,
                                       JFreeChart chart,
                                       int width, int height) throws IOException {

        saveChartAsJPEG(file, quality, chart, width, height, null);

    }

    /**
     * Saves the chart as a JPEG format image file.
     * <P>
     * This method allows you to pass in a ChartRenderingInfo object, to collect
     * information about the chart dimensions/entities.  You will need this info
     * if you want to create an HTML image map.
     *
     * @param file  the file name.
     * @param chart  the chart.
     * @param width  the image width.
     * @param height  the image height.
     * @param info  the chart rendering info.
     *
     * @throws IOException if there are any I/O errors.
     */
    public static void saveChartAsJPEG(File file,
                                       JFreeChart chart,
                                       int width, int height,
                                       ChartRenderingInfo info) throws IOException {

        saveChartAsJPEG(file, DEFAULT_JPEG_QUALITY, chart, width, height, info);

    }

    /**
     * Saves the chart as a JPEG format image file.
     * <P>
     * This method allows you to pass in a ChartRenderingInfo object, to collect
     * information about the chart dimensions/entities.  You will need this info
     * if you want to create an HTML image map.
     *
     * @param file  the file name.
     * @param quality  the quality setting.
     * @param chart  the chart.
     * @param width  the image width.
     * @param height  the image height.
     * @param info  the chart rendering info.
     *
     * @throws IOException if there are any I/O errors.
     */
    public static void saveChartAsJPEG(File file, float quality,
                                       JFreeChart chart,
                                       int width, int height,
                                       ChartRenderingInfo info
                                       ) throws IOException {

        OutputStream out = new BufferedOutputStream(new FileOutputStream(file));
        writeChartAsJPEG(out, quality, chart, width, height, info);
        out.close();

    }

    /**
     * Writes the BufferedImage to the output stream in JPEG format.
     * <P>
     * @param out  the output stream.
     * @param image  the buffered image to be written to the OutputStream
     *
     * @throws IOException if there are any I/O errors.
     */
    public static void writeBufferedImageAsJPEG(OutputStream out, BufferedImage image)
        throws IOException {

        writeBufferedImageAsJPEG(out, 0.75f, image);

    }

    /**
     * Writes the BufferedImage to the output stream in JPEG format.
     * <P>
     * @param out  the output stream.
     * @param quality  the image quality (0.0f to 1.0f).
     * @param image  the buffered image to be written to the OutputStream
     *
     * @throws IOException if there are any I/O errors.
     */
    public static void writeBufferedImageAsJPEG(OutputStream out, float quality,
                                                BufferedImage image) throws IOException {

        JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);
        JPEGEncodeParam param = encoder.getDefaultJPEGEncodeParam(image);
        param.setQuality(quality, true);
        encoder.encode(image, param);

    }

    /**
     * Writes the BufferedImage to the output stream in PNG format.
     * <P>
     * @param out  the output stream.
     * @param image  the buffered image to be written to the OutputStream
     *
     * @throws IOException if there are any I/O errors.
     */
    public static void writeBufferedImageAsPNG(OutputStream out, BufferedImage image)
        throws IOException {

        writeBufferedImageAsPNG(out, image, false, DEFAULT_PNG_COMPRESSION);

    }

    /**
     * Writes the BufferedImage to the output stream in PNG format.
     * <P>
     * @param out  the output stream.
     * @param image  the buffered image to be written to the OutputStream.
     * @param encodeAlpha  encode alpha?
     * @param compression  the compression level.
     *
     * @throws IOException if there are any I/O errors.
     */
    public static void writeBufferedImageAsPNG(OutputStream out, BufferedImage image,
                                               boolean encodeAlpha,
                                               int compression) throws IOException {

        PngEncoder encoder = new PngEncoder(image, encodeAlpha, 0, compression);
        byte[] pngData = encoder.pngEncode();
        out.write(pngData);

    }

    /**
     * Writes an image map to the output stream.
     *
     * @param writer  the writer.
     * @param name  the map name.
     * @param info  the chart rendering info.
     *
     * @throws IOException if there are any I/O errors.
     */
    public static void writeImageMap(PrintWriter writer, String name, ChartRenderingInfo info)
        throws IOException {

        ChartUtilities.writeImageMap(writer, name, info, false);

    }

    /**
     * Writes an image map to the output stream.
     *
     * @param writer  the writer.
     * @param name  the map name.
     * @param info  the chart rendering info.
     * @param useOverLibForToolTips  whether to use OverLIB for tooltips
     *                               (http://www.bosrup.com/web/overlib/).
     *
     * @throws IOException if there are any I/O errors.
     */
    public static void writeImageMap(PrintWriter writer, String name, ChartRenderingInfo info,
                                     boolean useOverLibForToolTips) throws IOException {

        writer.println("<MAP NAME=\"" + name + "\">");
        EntityCollection entities = info.getEntityCollection();
        Iterator iterator = entities.iterator();
        while (iterator.hasNext()) {
            ChartEntity entity = (ChartEntity) iterator.next();
            String area = entity.getImageMapAreaTag(useOverLibForToolTips);
            if (area.length() > 0) {
                writer.println(area);
            }
        }
        writer.println("</MAP>");

    }

}
