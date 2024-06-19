/* ============================================
 * JFreeChart : a free Java chart class library
 * ============================================
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
 *                   Richard Atkinson (richard_c_atkinson@ntlworld.com);
 *
 * $Id: ChartUtilities.java,v 1.1 2007/10/10 19:52:15 vauchers Exp $
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
 *
 */

package com.jrefinery.chart;

import com.jrefinery.chart.entity.EntityCollection;
import com.jrefinery.chart.entity.ChartEntity;
import com.jrefinery.chart.entity.PieSectionEntity;
import com.jrefinery.chart.entity.CategoryItemEntity;
import com.jrefinery.chart.entity.XYItemEntity;
import java.awt.image.BufferedImage;
import java.io.OutputStream;
import java.io.FileOutputStream;
import java.io.BufferedOutputStream;
import java.io.PrintWriter;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import com.keypoint.PngEncoder;
import com.sun.image.codec.jpeg.JPEGImageEncoder;
import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGEncodeParam;

/**
 * Utility methods for JFreeChart.  Includes methods for converting charts to
 * image formats (PNG and JPEG) plus creating simple HTML image maps.
 */
public class ChartUtilities {

	/**
	 * Writes the chart to the output stream in PNG format.
	 *
	 * @param out   The output stream.
	 * @param chart     The chart.
	 * @param width     The image width.
	 * @param height    The image height.
	 * @throws IOException
	 */
	public static void writeChartAsPNG(OutputStream out, JFreeChart chart,
			int width, int height)
		throws IOException {

		ChartUtilities.writeChartAsPNG(out, chart, width, height, null);
	}

	/**
	 * Writes the chart to the output stream in PNG format.
	 * <P>
	 * This method allows you to pass in a ChartRenderingInfo object, to
	 * collect information about the chart dimensions/entities.  You will need
	 * this info if you want to create an HTML image map.
	 *
	 * @param out   The output stream.
	 * @param chart     The chart.
	 * @param width     The image width.
	 * @param height    The image height.
	 * @param info      The chart rendering info.
	 * @throws IOException
	 */
	public static void writeChartAsPNG(OutputStream out, JFreeChart chart,
			int width, int height, ChartRenderingInfo info)
		throws IOException {

		BufferedImage chartImage = chart.createBufferedImage(width, height, info);
		ChartUtilities.writeBufferedImageAsPNG(out, chartImage);
	}

	/**
	 * Saves the chart as a PNG format image file.
	 *
	 * @param chart     The chart.
	 * @param width     The image width.
	 * @param height    The image height.
	 * @param file      The file.
	 * @throws IOException
	 */
	public static void saveChartAsPNG(File file, JFreeChart chart,
			int width, int height)
		throws IOException {

		saveChartAsPNG(file, chart, width, height, null);
	}

	/**
	 * Saves the chart as a PNG format image file.
	 * <P>
	 * This method allows you to pass in a ChartRenderingInfo object, to collect
	 * information about the chart dimensions/entities.  You will need this info
	 * if you want to create an HTML image map.
	 *
	 * @param chart     The chart.
	 * @param width     The image width.
	 * @param height    The image height.
	 * @param file      The file.
	 * @param info      The chart rendering info.
	 * @throws IOException
	 */
	public static void saveChartAsPNG(File file, JFreeChart chart,
			int width, int height, ChartRenderingInfo info)
		throws IOException {

		OutputStream out = new BufferedOutputStream(new FileOutputStream(file));
		writeChartAsPNG(out, chart, width, height, info);
		out.close();
	}

	/**
	 * Writes the chart to the output stream in JPEG format.
	 *
	 * @param out   The output stream.
	 * @param chart     The chart.
	 * @param width     The image width.
	 * @param height    The image height.
	 * @throws IOException
	 */
	public static void writeChartAsJPEG(OutputStream out, JFreeChart chart,
			int width, int height)
		throws IOException {

		ChartUtilities.writeChartAsJPEG(out, chart, width, height, null);
	}

	/**
	 * Writes the chart to the output stream in JPEG format.
	 * <P>
	 * This method allows you to pass in a ChartRenderingInfo object, to
	 * collect information about the chart dimensions/entities.  You will need
	 * this info if you want to create an HTML image map.
	 *
	 * @param out   The output stream.
	 * @param chart     The chart.
	 * @param width     The image width.
	 * @param height    The image height.
	 * @param info      The chart rendering info.
	 * @throws IOException
	 */
	public static void writeChartAsJPEG(OutputStream out, JFreeChart chart,
			int width, int height, ChartRenderingInfo info)
		throws IOException {

		BufferedImage chartImage = chart.createBufferedImage(width, height, info);
		ChartUtilities.writeBufferedImageAsJPEG(out, chartImage);
	}

	/**
	 * Saves the chart as a JPEG format image file.
	 *
	 * @param file      The file.
	 * @param chart     The chart.
	 * @param width     The image width.
	 * @param height    The image height.
	 * @throws IOException
	 */
	public static void saveChartAsJPEG(File file, JFreeChart chart,
			int width, int height)
		throws IOException {

		saveChartAsJPEG(file, chart, width, height, null);
	}

	/**
	 * Saves the chart as a JPEG format image file.
	 * <P>
	 * This method allows you to pass in a ChartRenderingInfo object, to collect
	 * information about the chart dimensions/entities.  You will need this info
	 * if you want to create an HTML image map.
	 *
	 * @param chart     The chart.
	 * @param width     The image width.
	 * @param height    The image height.
	 * @param file      The file.
	 * @param info      The chart rendering info.
	 * @throws IOException
	 */
	public static void saveChartAsJPEG(File file, JFreeChart chart,
			int width, int height, ChartRenderingInfo info)
		throws IOException {

		OutputStream out = new BufferedOutputStream(new FileOutputStream(file));
		writeChartAsJPEG(out, chart, width, height, info);
		out.close();
	}

	/**
	 * Writes the BufferedImage to the output stream in JPEG format.
	 * <P>
	 * @param out The output stream.
	 * @param bufferedImage The buffered image to be written to the OutputStream
	 */
	public static void writeBufferedImageAsJPEG(OutputStream out, BufferedImage chartImage)
			throws IOException {

		JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);
		JPEGEncodeParam param = encoder.getDefaultJPEGEncodeParam(chartImage);
		param.setQuality(1.0f, true);
		encoder.encode(chartImage, param);
	}

	/**
	 * Writes the BufferedImage to the output stream in PNG format.
	 * <P>
	 * @param out The output stream.
	 * @param bufferedImage The buffered image to be written to the OutputStream
	 */
	public static void writeBufferedImageAsPNG(OutputStream out, BufferedImage chartImage)
			throws IOException {

		PngEncoder encoder = new PngEncoder(chartImage, false, 0, 9);
		byte[] pngData = encoder.pngEncode();
		out.write(pngData);
	}

	/**
	 * Writes an image map to the output stream.
	 * <P>
	 * Note: this initial implementation is experimental...feedback is welcome.
	 *
	 * @param writer       The writer.
	 * @param name         The map name.
	 * @param hrefPrefix   URL to use to prefix the image maps href attribute.
	 * @param info         The chart rendering info.
	 * @throws IOException
	 */
	public static void writeImageMap(PrintWriter writer, String name,
			ChartRenderingInfo info)
		throws IOException {

		writer.println("<MAP NAME=\""+name+"\">");

		EntityCollection entities = info.getEntityCollection();
		Iterator iterator = entities.iterator();
		while (iterator.hasNext()) {
			ChartEntity entity = (ChartEntity)iterator.next();
			if (entity instanceof PieSectionEntity) {
				PieSectionEntity pse = (PieSectionEntity)entity;
				writer.println(pse.getImageMapAreaTag());
			}
			else if (entity instanceof CategoryItemEntity) {
				CategoryItemEntity cie = (CategoryItemEntity)entity;
				writer.println(cie.getImageMapAreaTag());
			}
			else if (entity instanceof XYItemEntity) {
				XYItemEntity xyie = (XYItemEntity)entity;
				writer.println(xyie.getImageMapAreaTag());
			}

		}
		writer.println("</MAP>");
	}

}
