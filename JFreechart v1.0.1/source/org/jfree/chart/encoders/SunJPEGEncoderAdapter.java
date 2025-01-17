/* ===========================================================
 * JFreeChart : a free chart library for the Java(tm) platform
 * ===========================================================
 *
 * (C) Copyright 2000-2005, by Object Refinery Limited and Contributors.
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
 * --------------------------
 * SunJPEGEncoderAdapter.java
 * --------------------------
 * (C) Copyright 2004, 2005, by Richard Atkinson and Contributors.
 *
 * Original Author:  Richard Atkinson;
 * Contributor(s):   David Gilbert (for Object Refinery Limited);
 *
 * $Id: SunJPEGEncoderAdapter.java,v 1.1 2007/10/10 20:17:22 vauchers Exp $
 *
 * Changes
 * -------
 * 01-Aug-2004 : Initial version (RA);
 * 01-Nov-2005 : To remove the dependency on non-supported APIs, use ImageIO 
 *               instead of com.sun.image.codec.jpeg.JPEGImageEncoder - this 
 *               adapter will only be available on JDK 1.4 or later (DG);
 *
 */

package org.jfree.chart.encoders;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.imageio.ImageIO;

/**
 * Adapter class for the Sun JPEG Encoder.  The ImageEncoderFactory will only 
 * return a reference to this class by default if the library has been compiled 
 * under a JDK 1.4+ and is being run using a JDK 1.4+.
 *
 * @author Richard Atkinson
 */
public class SunJPEGEncoderAdapter implements ImageEncoder {
    
    private float quality = 0.75f;

    /**
     * Default constructor.
     */
    public SunJPEGEncoderAdapter() {
    }

    /**
     * Get the quality of the image encoding.
     *
     * @return A float representing the quality.
     */
    public float getQuality() {
        return this.quality;
    }

    /**
     * Set the quality of the image encoding (ignored).
     *
     * @param quality  A float representing the quality.
     */
    public void setQuality(float quality) {
        this.quality = quality;
    }

    /**
     * Get whether the encoder encodes alpha transparency (always false).
     *
     * @return Whether the encoder is encoding alpha transparency.
     */
    public boolean isEncodingAlpha() {
        return false;
    }

    /**
     * Set whether the encoder should encode alpha transparency (not supported 
     * for JPEG).
     *
     * @param encodingAlpha  Whether the encoder should encode alpha 
     *                       transparency.
     */
    public void setEncodingAlpha(boolean encodingAlpha) {
        //  No op
    }

    /**
     * Encodes an image in JPEG format.
     *
     * @param bufferedImage  The image to be encoded.
     * 
     * @return The byte[] that is the encoded image.
     * 
     * @throws IOException
     */
    public byte[] encode(BufferedImage bufferedImage) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        encode(bufferedImage, outputStream);
        return outputStream.toByteArray();
    }

    /**
     * Encodes an image in JPEG format and writes it to an OutputStream.
     *
     * @param bufferedImage  The image to be encoded.
     * @param outputStream  The OutputStream to write the encoded image to.
     * @throws IOException
     */
    public void encode(BufferedImage bufferedImage, OutputStream outputStream) 
        throws IOException {
        if (bufferedImage == null) {
            throw new IllegalArgumentException("Null 'image' argument.");
        }
        if (outputStream == null) {
            throw new IllegalArgumentException("Null 'outputStream' argument.");
        }
        ImageIO.write(bufferedImage, ImageFormat.JPEG, outputStream);
    }

}
