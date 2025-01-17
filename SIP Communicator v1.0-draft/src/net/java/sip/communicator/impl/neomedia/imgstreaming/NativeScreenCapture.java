/*
 * SIP Communicator, the OpenSource Java VoIP and Instant Messaging client.
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package net.java.sip.communicator.impl.neomedia.imgstreaming;

import java.awt.image.*;

/**
 * This class uses native code to capture desktop screen.
 * 
 * It should work for Windows, Mac OS X and X11-based Unix such as Linux 
 * and FreeBSD.
 *
 * @author Sebastien Vincent
 */
public class NativeScreenCapture
{
    static
    {
        System.loadLibrary("screencapture");
    }

    /**
     * Grab desktop screen and get raw bytes.
     * 
     * @param x x position to start capture
     * @param y y position to start capture
     * @param width capture width
     * @param height capture height
     * @param output output buffer to store screen bytes
     * @return true if grab success, false otherwise
     */
    public static native boolean grabScreen(int x, int y, int width, int height, byte output[]);
}
