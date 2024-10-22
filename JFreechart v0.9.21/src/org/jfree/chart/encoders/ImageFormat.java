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
 * -------------------
 * ImageFormat.java
 * -------------------
 * (C) Copyright 2004, by Richard Atkinson and Contributors.
 *
 * Original Author:  Richard Atkinson;
 * Contributor(s):   -;
 *
 * $Id: ImageFormat.java,v 1.1 2007/10/10 19:50:22 vauchers Exp $
 *
 * Changes
 * -------
 * 01-Aug-2004 : Initial version (RA);
 *
 */

package org.jfree.chart.encoders;

/**
 * Interface used for referencing different image formats.
 *
 * @author Richard Atkinson
 */
public interface ImageFormat {

    /** Portable Network Graphics - lossless */
    public static String PNG = "png";

    /** Joint Photographic Experts Group format - lossy */
    public static String JPEG = "jpeg";

    /** Graphics Interchange Format - lossless, but 256 colour restriction */
    public static String GIF = "gif";

}
