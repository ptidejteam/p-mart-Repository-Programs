/*
 * SIP Communicator, the OpenSource Java VoIP and Instant Messaging client.
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package net.java.sip.communicator.impl.neomedia.quicktime;

/**
 * Represents a QTKit <tt>QTCaptureDecompressedVideoOutput</tt> object.
 *
 * @author Lubomir Marinov
 */
public class QTCaptureDecompressedVideoOutput
    extends QTCaptureOutput
{

    /**
     * Initializes a new <tt>QTCaptureDecompressedVideoOutput</tt> which
     * represents a new QTKit <tt>QTCaptureDecompressedVideoOutput</tt> object.
     */
    public QTCaptureDecompressedVideoOutput()
    {
        this(allocAndInit());
    }

    /**
     * Initializes a new <tt>QTCaptureDecompressedVideoOutput</tt> which is to
     * represent a new QTKit <tt>QTCaptureDecompressedVideoOutput</tt> object.
     *
     * @param ptr the pointer to the QTKit
     * <tt>QTCaptureDecompressedVideoOutput</tt> object to be represented by the
     * new instance
     */
    public QTCaptureDecompressedVideoOutput(long ptr)
    {
        super(ptr);
    }

    private static native long allocAndInit();

    /**
     * Called by the garbage collector to release system resources and perform
     * other cleanup.
     *
     * @see Object#finalize()
     */
    @Override
    protected void finalize()
    {
        release();
    }

    public NSDictionary pixelBufferAttributes()
    {
        long pixelBufferAttributesPtr = pixelBufferAttributes(getPtr());

        return
            (pixelBufferAttributesPtr == 0)
                ? null
                : new NSDictionary(pixelBufferAttributesPtr);
    }

    private static native long pixelBufferAttributes(long ptr);

    public void setAutomaticallyDropsLateVideoFrames(
            boolean automaticallyDropsLateVideoFrames)
    {
        setAutomaticallyDropsLateVideoFrames(
                getPtr(),
                automaticallyDropsLateVideoFrames);
    }

    private static native void setAutomaticallyDropsLateVideoFrames(
            long ptr,
            boolean automaticallyDropsLateVideoFrames);

    public void setDelegate(Delegate delegate)
    {
        setDelegate(getPtr(), delegate);
    }

    private static native void setDelegate(long ptr, Delegate delegate);

    public void setPixelBufferAttributes(NSDictionary pixelBufferAttributes)
    {
        setPixelBufferAttributes(getPtr(), pixelBufferAttributes.getPtr());
    }

    private static native void setPixelBufferAttributes(
            long ptr,
            long pixelBufferAttributesPtr);

    /**
     * Represents the receiver of <tt>CVImageBuffer</tt> video frames and their
     * associated <tt>QTSampleBuffer</tt>s captured by a
     * <tt>QTCaptureDecompressedVideoOutput</tt>.
     */
    public static abstract class Delegate
    {
        private MutableQTSampleBuffer sampleBuffer;

        private MutableCVPixelBuffer videoFrame;

        /**
         * Notifies this <tt>Delegate</tt> that the <tt>QTCaptureOutput</tt> to
         * which it is set has output a specific <tt>CVImageBuffer</tt>
         * representing a video frame with a specific <tt>QTSampleBuffer</tt>.
         *
         * @param videoFrame the <tt>CVImageBuffer</tt> which represents the
         * output video frame
         * @param sampleBuffer the <tt>QTSampleBuffer</tt> which represents
         * additional details about the output video samples
         */
        public abstract void outputVideoFrameWithSampleBuffer(
                CVImageBuffer videoFrame,
                QTSampleBuffer sampleBuffer);

        void outputVideoFrameWithSampleBuffer(
                long videoFramePtr,
                long sampleBufferPtr)
        {
            if (videoFrame == null)
                videoFrame = new MutableCVPixelBuffer(videoFramePtr);
            else
                videoFrame.setPtr(videoFramePtr);

            if (sampleBuffer == null)
                sampleBuffer = new MutableQTSampleBuffer(sampleBufferPtr);
            else
                sampleBuffer.setPtr(sampleBufferPtr);

            outputVideoFrameWithSampleBuffer(videoFrame, sampleBuffer);
        }
    }

    /**
     * Represents a <tt>CVPixelBuffer</tt> which allows public changing of the
     * CoreVideo <tt>CVPixelBufferRef</tt> it represents.
     */
    private static class MutableCVPixelBuffer
        extends CVPixelBuffer
    {
        /**
         * Initializes a new <tt>MutableCVPixelBuffer</tt> which is to represent
         * a specific CoreVideo <tt>CVPixelBufferRef</tt>.
         *
         * @param ptr the CoreVideo <tt>CVPixelBufferRef</tt> to be represented
         * by the new instance
         */
        private MutableCVPixelBuffer(long ptr)
        {
            super(ptr);
        }

        /**
         * Sets the CoreVideo <tt>CVImageBufferRef</tt> represented by this
         * instance.
         *
         * @param ptr the CoreVideo <tt>CVImageBufferRef</tt> to be represented
         * by this instance
         * @see CVPixelBuffer#setPtr(long)
         */
        @Override
        public void setPtr(long ptr)
        {
            super.setPtr(ptr);
        }
    }

    /**
     * Represents a <tt>QTSampleBuffer</tt> which allows public changing of the
     * QTKit <tt>QTSampleBuffer</tt> object it represents.
     */
    private static class MutableQTSampleBuffer
        extends QTSampleBuffer
    {
        /**
         * Initializes a new <tt>MutableQTSampleBuffer</tt> instance which is to
         * represent a specific QTKit <tt>QTSampleBuffer</tt> object.
         *
         * @param ptr the pointer to the QTKit <tt>QTSampleBuffer</tt> object to
         * be represented by the new instance
         */
        private MutableQTSampleBuffer(long ptr)
        {
            super(ptr);
        }

        /**
         * Sets the pointer to the Objective-C object represented by this
         * instance.
         *
         * @param ptr the pointer to the Objective-C object to be represented by
         * this instance
         * @see QTSampleBuffer#setPtr(long)
         */
        @Override
        public void setPtr(long ptr)
        {
            super.setPtr(ptr);
        }
    }
}
