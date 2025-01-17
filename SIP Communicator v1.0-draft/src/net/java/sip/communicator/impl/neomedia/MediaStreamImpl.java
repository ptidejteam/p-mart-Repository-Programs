/*
 * SIP Communicator, the OpenSource Java VoIP and Instant Messaging client.
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package net.java.sip.communicator.impl.neomedia;

import java.io.*;
import java.net.*;
import java.util.*;
import java.beans.*;

import javax.media.*;
import javax.media.control.*;
import javax.media.format.*;
import javax.media.protocol.*;
import javax.media.rtp.*;
import javax.media.rtp.event.*;

import com.sun.media.rtp.*;

import net.java.sip.communicator.impl.neomedia.device.*;
import net.java.sip.communicator.impl.neomedia.format.*;
import net.java.sip.communicator.impl.neomedia.transform.*;
import net.java.sip.communicator.impl.neomedia.transform.csrc.*;
import net.java.sip.communicator.impl.neomedia.transform.dtmf.*;
import net.java.sip.communicator.impl.neomedia.transform.zrtp.*;
import net.java.sip.communicator.service.neomedia.*;
import net.java.sip.communicator.service.neomedia.device.*;
import net.java.sip.communicator.service.neomedia.format.*;
import net.java.sip.communicator.util.*;

/**
 * Implements <tt>MediaStream</tt> using JMF.
 *
 * @author Lubomir Marinov
 * @author Emil Ivov
 */
public class MediaStreamImpl
    extends AbstractMediaStream
    implements ReceiveStreamListener,
               SendStreamListener,
               SessionListener
{
    /**
     * The <tt>Logger</tt> used by the <tt>MediaStreamImpl</tt> class and its
     * instances for logging output.
     */
    private static final Logger logger
        = Logger.getLogger(MediaStreamImpl.class);

    /**
     * The name of the property indicating the length of our receive buffer.
     */
    protected static final String PROPERTY_NAME_RECEIVE_BUFFER_LENGTH
        = "net.java.sip.communicator.impl.neomedia.RECEIVE_BUFFER_LENGTH";

    /**
     * The session with the <tt>MediaDevice</tt> this instance uses for both
     * capture and playback of media.
     */
    protected MediaDeviceSession deviceSession;

    /**
     * The <tt>PropertyChangeListener</tt> which listens to
     * {@link #deviceSession} and changes in the values of its
     * {@link MediaDeviceSession#OUTPUT_DATA_SOURCE} property.
     */
    private final PropertyChangeListener deviceSessionPropertyChangeListener =
        new PropertyChangeListener()
        {
            public void propertyChange(PropertyChangeEvent event)
            {
                if (MediaDeviceSession
                        .OUTPUT_DATA_SOURCE.equals(event.getPropertyName()))
                    deviceSessionOutputDataSourceChanged();
                else if (MediaDeviceSession
                                .SSRC_LIST.equals(event.getPropertyName()))
                                deviceSessionSsrcListChanged(event);
            }
        };

    /**
     * The <tt>MediaDirection</tt> in which this <tt>MediaStream</tt> is allowed
     * to stream media.
     */
    private MediaDirection direction;

    /**
     * The <tt>Map</tt> of associations in this <tt>MediaStream</tt> and the
     * <tt>RTPManager</tt> it utilizes of (dynamic) RTP payload types to
     * <tt>MediaFormat</tt>s.
     */
    private final Map<Byte, MediaFormat> dynamicRTPPayloadTypes
        = new HashMap<Byte, MediaFormat>();

    /**
     * The <tt>ReceiveStream</tt> this instance plays back on its associated
     * <tt>MediaDevice</tt>.
     */
    private ReceiveStream receiveStream;

    /**
     * The <tt>Object</tt> which synchronizes the access to
     * {@link #receiveStream} and its registration with {@link #deviceSession}.
     */
    private final Object receiveStreamSyncRoot = new Object();

    /**
     * The <tt>RTPConnector</tt> through which this instance sends and receives
     * RTP and RTCP traffic. The instance is a <tt>TransformConnector</tt> in
     * order to also enable packet transformations.
     */
    private final RTPTransformConnector rtpConnector;

    /**
     * The one and only <tt>MediaStreamTarget</tt> this instance has added as a
     * target in {@link #rtpConnector}.
     */
    private MediaStreamTarget rtpConnectorTarget;

    /**
     * The <tt>RTPManager</tt> which utilizes {@link #rtpConnector} and sends
     * and receives RTP and RTCP traffic on behalf of this <tt>MediaStream</tt>.
     */
    private RTPManager rtpManager;

    /**
     * The indicator which determines whether {@link #createSendStreams()} has
     * been executed for {@link #rtpManager}. If <tt>true</tt>, the
     * <tt>SendStream</tt>s have to be recreated when the <tt>MediaDevice</tt>,
     * respectively the <tt>MediaDeviceSession</tt>, of this instance is
     * changed.
     */
    private boolean sendStreamsAreCreated = false;

    /**
     * The indicator which determines whether {@link #start()} has been called
     * on this <tt>MediaStream</tt> without {@link #stop()} or {@link #close()}.
     */
    private boolean started = false;

    /**
     * The <tt>MediaDirection</tt> in which this instance is started. For
     * example, {@link MediaDirection#SENDRECV} if this instances is both
     * sending and receiving data (e.g. RTP and RTCP) or
     * {@link MediaDirection#SENDONLY} if this instance is only sending data.
     */
    private MediaDirection startedDirection;

    /**
     * The SSRC identifier of the party that we are exchanging media with.
     */
    private long remoteSourceID = -1;

    /**
     * Our own SSRC identifier.
     */
    private long localSourceID = -1;

    /**
     * The list of CSRC IDs contributing to the media that this
     * <tt>MediaStream</tt> is sending to its remote party.
     */
    private long[] localContributingSourceIDList = null;

    /**
     * The indicator which determines whether this <tt>MediaStream</tt> is set
     * to transmit "silence" instead of the actual media fed from its
     * <tt>MediaDevice</tt>.
     */
    private boolean mute = false;

    /**
     * The current <tt>ZrtpControl</tt>
     */
    private ZrtpControlImpl zrtpControl = null;

    /**
     * The map of currently active <tt>RTPExtension</tt>s and the IDs that they
     * have been assigned for the lifetime of this <tt>MediaStream</tt>.
     */
    private final Map<Byte, RTPExtension> activeRTPExtensions
        = new Hashtable<Byte, RTPExtension>();

    /**
     * The engine that we are using in order to add CSRC lists in conference
     * calls, send CSRC sound levels, and handle incoming levels and CSRC lists.
     */
    private CsrcTransformEngine csrcEngine;

    /**
     * Initializes a new <tt>MediaStreamImpl</tt> instance which will use the
     * specified <tt>MediaDevice</tt> for both capture and playback of media
     * exchanged via the specified <tt>StreamConnector</tt>.
     *
     * @param connector the <tt>StreamConnector</tt> the new instance is to use
     * for sending and receiving media
     * @param device the <tt>MediaDevice</tt> the new instance is to use for
     * both capture and playback of media exchanged via the specified
     * <tt>StreamConnector</tt>
     * @param zrtpControl a control which is already created, used to control
     * the zrtp operations.
     */
    public MediaStreamImpl(StreamConnector connector, MediaDevice device,
        ZrtpControlImpl zrtpControl)
    {
        /*
         * XXX Set the device early in order to make sure that its of the right
         * type because we do not support just about any MediaDevice yet.
         */
        setDevice(device);

        this.rtpConnector = new RTPTransformConnector(connector);

        if(zrtpControl != null)
        {
            this.zrtpControl = zrtpControl;
        }
        else
            this.zrtpControl = new ZrtpControlImpl();

        this.zrtpControl.setConnector(rtpConnector);

        //register the transform engines that we will be using in this stream.
        TransformEngineChain engineChain = createTransformEngineChain();

        rtpConnector.setEngine(engineChain);
    }

    /**
     * Performs any optional configuration on the <tt>BufferControl</tt> of the
     * specified <tt>RTPManager</tt> which is to be used as the
     * <tt>RTPManager</tt> of this <tt>MediaStreamImpl</tt>. Allows extenders to
     * override.
     *
     * @param rtpManager the <tt>RTPManager</tt> which is to be used by this
     * <tt>MediaStreamImpl</tt>
     * @param bufferControl the <tt>BufferControl</tt> of <tt>rtpManager</tt> on
     * which any optional configuration is to be performed
     */
    protected void configureRTPManagerBufferControl(
            RTPManager rtpManager,
            BufferControl bufferControl)
    {
    }

    /**
     * Creates a chain of transform engines for use with this stream. Note
     * that this is the only place where the <tt>TransformEngineChain</tt> is
     * and should be manipulated to avoid problems with the order of the
     * transformers.
     *
     * @return the <tt>TransformEngineChain</tt> that this stream should be
     * using.
     */
    private TransformEngineChain createTransformEngineChain()
    {
        ArrayList<TransformEngine> engineChain
                                        = new ArrayList<TransformEngine>(3);

        //CSRCs and audio levels
        csrcEngine = new CsrcTransformEngine(this);
        engineChain.add(csrcEngine);

                //DTMF
        DtmfTransformEngine dtmfEngine = createDtmfTransformEngine();

        if(dtmfEngine != null)
            engineChain.add(dtmfEngine);

        //ZRTP
        engineChain.add(zrtpControl.getZrtpEngine());

        return new TransformEngineChain( engineChain.toArray(
                                    new TransformEngine[engineChain.size()]));
    }

    /**
     * A stub that allows audio oriented streams to create and keep a reference
     * to a <tt>DtmfTransformEngine</tt>.
     *
     * @return a <tt>DtmfTransformEngine</tt> if this is an audio oriented
     * stream and <tt>null</tt> otherwise.
     */
    protected DtmfTransformEngine createDtmfTransformEngine()
    {
        return null;
    }

    /**
     * Adds a new association in this <tt>MediaStream</tt> of the specified RTP
     * payload type with the specified <tt>MediaFormat</tt> in order to allow it
     * to report <tt>rtpPayloadType</tt> in RTP flows sending and receiving
     * media in <tt>format</tt>. Usually, <tt>rtpPayloadType</tt> will be in the
     * range of dynamic RTP payload types.
     *
     * @param rtpPayloadType the RTP payload type to be associated in this
     * <tt>MediaStream</tt> with the specified <tt>MediaFormat</tt>
     * @param format the <tt>MediaFormat</tt> to be associated in this
     * <tt>MediaStream</tt> with <tt>rtpPayloadType</tt>
     * @see MediaStream#addDynamicRTPPayloadType(byte, MediaFormat)
     */
    public void addDynamicRTPPayloadType(
            byte rtpPayloadType,
            MediaFormat format)
    {
        @SuppressWarnings("unchecked")
        MediaFormatImpl<? extends Format> mediaFormatImpl
            = (MediaFormatImpl<? extends Format>) format;

        synchronized (dynamicRTPPayloadTypes)
        {
            dynamicRTPPayloadTypes.put(Byte.valueOf(rtpPayloadType), format);

            if (rtpManager != null)
                rtpManager
                    .addFormat(mediaFormatImpl.getFormat(), rtpPayloadType);
        }
    }

    /**
     * Maps or updates the mapping between <tt>extensionID</tt> and
     * <tt>rtpExtension</tt>. If <tt>rtpExtension</tt>'s <tt>MediaDirection</tt>
     * attribute is set to <tt>INACTIVE</tt> the mapping is removed from the
     * local extensions table and the extension would not be transmitted or
     * handled by this stream's <tt>RTPConnector</tt>.
     *
     * @param extensionID the ID that is being mapped to <tt>rtpExtension</tt>
     * @param rtpExtension the <tt>RTPExtension</tt> that we are mapping.
     */
    public void addRTPExtension(byte extensionID, RTPExtension rtpExtension)
    {
        synchronized (activeRTPExtensions)
        {
            if(rtpExtension.getDirection() == MediaDirection.INACTIVE)
                activeRTPExtensions.remove(extensionID);
            else
                activeRTPExtensions.put(extensionID, rtpExtension);
        }
    }

    /**
     * Returns a map containing all currently active <tt>RTPExtension</tt>s in
     * use by this stream.
     *
     * @return a map containing all currently active <tt>RTPExtension</tt>s in
     * use by this stream.
     */
    public Map<Byte, RTPExtension> getActiveRTPExtensions()
    {
        synchronized (activeRTPExtensions)
        {
            return new HashMap<Byte, RTPExtension>(activeRTPExtensions);
        }
    }

    /**
     * Returns the ID currently assigned to a specific RTP extension.
     *
     * @param rtpExtension the RTP extension to get the currently assigned ID of
     * @return the ID currently assigned to the specified RTP extension or
     * <tt>-1</tt> if no ID has been defined for this extension so far
     */
    public byte getActiveRTPExtensionID(RTPExtension rtpExtension)
    {
        synchronized (activeRTPExtensions)
        {
            Set<Map.Entry<Byte, RTPExtension>> extSet
                = this.activeRTPExtensions.entrySet();

            for (Map.Entry<Byte, RTPExtension> entry : extSet)
            {
                if (entry.getValue().equals(rtpExtension))
                    return entry.getKey();
            }
        }

        return -1;
    }

    /**
     * Returns the engine that is responsible for adding the list of CSRC
     * identifiers to outgoing RTP packets during a conference.
     *
     * @return the engine that is responsible for adding the list of CSRC
     * identifiers to outgoing RTP packets during a conference.
     */
    protected CsrcTransformEngine getCsrcEngine()
    {
        return csrcEngine;
    }

    /**
     * Releases the resources allocated by this instance in the course of its
     * execution and prepares it to be garbage collected.
     *
     * @see MediaStream#close()
     */
    public void close()
    {
        stop();
        closeSendStreams();

        ZRTPTransformEngine engine = zrtpControl.getZrtpEngine();

        if(engine != null)
        {
            engine.stopZrtp();
            engine.cleanup();
        }

        if(csrcEngine != null)
            csrcEngine.stop();

        rtpConnector.removeTargets();
        rtpConnectorTarget = null;

        if (rtpManager != null)
        {
            rtpManager.removeReceiveStreamListener(this);
            rtpManager.removeSendStreamListener(this);
            rtpManager.removeSessionListener(this);
            rtpManager.dispose();
            rtpManager = null;
        }

        if (deviceSession != null)
            deviceSession.close();
    }

    /**
     * Closes the <tt>SendStream</tt>s this instance is sending to its remote
     * peer.
     */
    private void closeSendStreams()
    {
        stopSendStreams(true);
    }

    /**
     * Creates new <tt>SendStream</tt> instances for the streams of
     * {@link #deviceSession} through {@link #rtpManager}.
     */
    private void createSendStreams()
    {
        RTPManager rtpManager = getRTPManager();
        MediaDeviceSession deviceSession = getDeviceSession();
        DataSource dataSource = deviceSession.getOutputDataSource();
        int streamCount;

        if (dataSource instanceof PushBufferDataSource)
        {
            PushBufferStream[] streams
                = ((PushBufferDataSource) dataSource).getStreams();

            streamCount = (streams == null) ? 0 : streams.length;
        }
        else if (dataSource instanceof PushDataSource)
        {
            PushSourceStream[] streams
                = ((PushDataSource) dataSource).getStreams();

            streamCount = (streams == null) ? 0 : streams.length;
        }
        else if (dataSource instanceof PullBufferDataSource)
        {
            PullBufferStream[] streams
                = ((PullBufferDataSource) dataSource).getStreams();

            streamCount = (streams == null) ? 0 : streams.length;
        }
        else if (dataSource instanceof PullDataSource)
        {
            PullSourceStream[] streams
                = ((PullDataSource) dataSource).getStreams();

            streamCount = (streams == null) ? 0 : streams.length;
        }
        else
            streamCount = (dataSource == null) ? 0 : 1;

        for (int streamIndex = 0; streamIndex < streamCount; streamIndex++)
        {
            Throwable exception = null;

            try
            {
                SendStream sendStream
                    = rtpManager.createSendStream(dataSource, streamIndex);

                if (logger.isTraceEnabled())
                    logger
                        .trace(
                            "Created SendStream"
                                + " with hashCode "
                                + sendStream.hashCode()
                                + " for "
                                + toString(dataSource)
                                + " and streamIndex "
                                + streamIndex
                                + " in RTPManager with hashCode "
                                + rtpManager.hashCode());

                // If a ZRTP engine is availabe then set the SSRC of this stream
                // currently ZRTP supports only one SSRC per engine
                ZRTPTransformEngine engine = zrtpControl.getZrtpEngine();

                if (engine != null)
                    engine.setOwnSSRC(sendStream.getSSRC());
            }
            catch (IOException ioe)
            {
                exception = ioe;
            }
            catch (UnsupportedFormatException ufe)
            {
                exception = ufe;
            }

            if (exception != null)
            {
                // TODO
                logger
                    .error(
                        "Failed to create send stream for data source "
                            + dataSource
                            + " and stream index "
                            + streamIndex,
                        exception);
            }
        }
        sendStreamsAreCreated = true;

        if (logger.isTraceEnabled())
        {
            @SuppressWarnings("unchecked")
            Vector<SendStream> sendStreams = rtpManager.getSendStreams();
            int sendStreamCount
                = (sendStreams == null) ? 0 : sendStreams.size();

            logger
                .trace(
                    "Total number of SendStreams in RTPManager with hashCode "
                        + rtpManager.hashCode()
                        + " is "
                        + sendStreamCount);
        }
    }

    /**
     * Notifies this <tt>MediaStream</tt> that the <tt>MediaDevice</tt> (and
     * respectively the <tt>MediaDeviceSession</tt> with it) which this instance
     * uses for capture and playback of media has been changed. Allows extenders
     * to override and provide additional processing of <tt>oldValue</tt> and
     * <tt>newValue</tt>.
     *
     * @param oldValue the <tt>MediaDeviceSession</tt> with the
     * <tt>MediaDevice</tt> this instance used work with
     * @param newValue the <tt>MediaDeviceSession</tt> with the
     * <tt>MediaDevice</tt> this instance is to work with
     */
    protected void deviceSessionChanged(
            MediaDeviceSession oldValue,
            MediaDeviceSession newValue)
    {
        recreateSendStreams();
    }

    /**
     * Notifies this instance that the output <tt>DataSource</tt> of its
     * <tt>MediaDeviceSession</tt> has changed. Recreates the
     * <tt>SendStream</tt>s of this instance as necessary so that it, for
     * example, continues streaming after the change if it was streaming before
     * the change.
     */
    private void deviceSessionOutputDataSourceChanged()
    {
        recreateSendStreams();
    }

    /**
     * Recalculates the list of CSRC identifiers that this <tt>MediaStream</tt>
     * needs to include in RTP packets bound to its interlocutor. The method
     * uses the list of SSRC identifiers currently handled by our device
     * (possibly a mixer), then removes the SSRC ID of this stream's
     * interlocutor. If this turns out to be the only SSRC currently in the list
     * we set the list of local CSRC identifiers to null since this is obviously
     * a non-conf call and we don't need to be advertising CSRC lists. If that's
     * not the case, we also add our own SSRC to the list of IDs and cache the
     * entire list.
     *
     * @param evt the <tt>PropetyChangeEvent</tt> containing the list of SSRC
     * identifiers handled by our device session before and after it changed.
     *
     */
    private void deviceSessionSsrcListChanged(PropertyChangeEvent evt)
    {
        long[] ssrcArray = (long[])evt.getNewValue();

        // the list is empty
        if(ssrcArray == null)
        {
            this.localContributingSourceIDList = null;
            return;
        }

        int elementsToRemove = 0;
        long remoteSrcID = this.getRemoteSourceID();

        //in case of a conf call the mixer would return all SSRC IDs that are
        //currently contributing including this stream's counterpart. We need
        //to remove that last one since that's where we will be sending our
        //csrc list
        for(long csrc : ssrcArray)
        {
            if (csrc == remoteSrcID)
            {
                elementsToRemove ++;
            }
        }

        //we don't seem to be in a conf call since the list only contains the
        //SSRC id of the party that we are directly interacting with.
        if (elementsToRemove >= ssrcArray.length)
        {
            this.localContributingSourceIDList = null;
            return;
        }

        //prepare the new array. make it big enough to also add the local
        //SSRC id but do not make it bigger than 15 since that's the maximum
        //for RTP.
        int cc = Math.min(ssrcArray.length - elementsToRemove + 1, 15);

        long[] csrcArray = new long[cc];

        for (int i = 0,j = 0;
                i < ssrcArray.length
             && j < csrcArray.length - 1;
             i++)
        {
            long ssrc = ssrcArray[i];
            if (ssrc != remoteSrcID)
            {
                csrcArray[j] = ssrc;
                j++;
            }
        }

        csrcArray[csrcArray.length - 1] = getLocalSourceID();
        this.localContributingSourceIDList = csrcArray;
    }

    /**
     * Gets the <tt>MediaDevice</tt> that this stream uses to play back and
     * capture media.
     *
     * @return the <tt>MediaDevice</tt> that this stream uses to play back and
     * capture media
     * @see MediaStream#getDevice()
     */
    public AbstractMediaDevice getDevice()
    {
        return getDeviceSession().getDevice();
    }

    /**
     * Gets the <tt>MediaDeviceSession</tt> which represents the work of this
     * <tt>MediaStream</tt> with its associated <tt>MediaDevice</tt>.
     *
     * @return the <tt>MediaDeviceSession</tt> which represents the work of this
     * <tt>MediaStream</tt> with its associated <tt>MediaDevice</tt>
     */
    protected MediaDeviceSession getDeviceSession()
    {
        return deviceSession;
    }

    /**
     * Gets the direction in which this <tt>MediaStream</tt> is allowed to
     * stream media.
     *
     * @return the <tt>MediaDirection</tt> in which this <tt>MediaStream</tt> is
     * allowed to stream media
     * @see MediaStream#getDirection()
     */
    public MediaDirection getDirection()
    {
        if (direction != null)
            return direction;

        MediaDeviceSession deviceSession = getDeviceSession();

        return (deviceSession == null)
                ? MediaDirection.INACTIVE
                : deviceSession.getDevice().getDirection();
    }

    /**
     * Gets the existing associations in this <tt>MediaStream</tt> of RTP
     * payload types to <tt>MediaFormat</tt>s. The returned <tt>Map</tt>
     * only contains associations previously added in this instance with
     * {@link #addDynamicRTPPayloadType(byte, MediaFormat)} and not globally or
     * well-known associations reported by
     * {@link MediaFormat#getRTPPayloadType()}.
     *
     * @return a <tt>Map</tt> of RTP payload type expressed as <tt>Byte</tt> to
     * <tt>MediaFormat</tt> describing the existing (dynamic) associations in
     * this instance of RTP payload types to <tt>MediaFormat</tt>s. The
     * <tt>Map</tt> represents a snapshot of the existing associations at the
     * time of the <tt>getDynamicRTPPayloadTypes()</tt> method call and
     * modifications to it are not reflected on the internal storage
     * @see MediaStream#getDynamicRTPPayloadTypes()
     */
    public Map<Byte, MediaFormat> getDynamicRTPPayloadTypes()
    {
        synchronized (dynamicRTPPayloadTypes)
        {
            return new HashMap<Byte, MediaFormat>(dynamicRTPPayloadTypes);
        }
    }

    /**
     * Returns the payload type number that has been negotiated for the
     * specified <tt>encoding</tt> or <tt>-1</tt> if no payload type has been
     * negotiated for it. If multiple formats match the specified
     * <tt>encoding</tt>, then this method would return the first one it
     * encounters while iterating through the map.
     *
     * @param encoding the encoding whose payload type we are trying to obtain.
     *
     * @return the payload type number that has been negotiated for the
     * specified <tt>encoding</tt> or <tt>-1</tt> if no payload type has been
     * negotiated for it.
     */
    public byte getDynamicRTPPayloadType(String encoding)
    {
        synchronized (dynamicRTPPayloadTypes)
        {
            for (Map.Entry<Byte, MediaFormat> entry
                                        : dynamicRTPPayloadTypes.entrySet())
            {
                if (entry.getValue().getEncoding().equals(encoding))
                    return entry.getKey().byteValue();
            }

            return -1;
        }
    }

    /**
     * Gets the <tt>MediaFormat</tt> that this stream is currently transmitting
     * in.
     *
     * @return the <tt>MediaFormat</tt> that this stream is currently
     * transmitting in
     * @see MediaStream#getFormat()
     */
    public MediaFormat getFormat()
    {
        return getDeviceSession().getFormat();
    }

    /**
     * Gets the synchronization source (SSRC) identifier of the local peer or
     * <tt>-1</tt> if it is not yet known.
     *
     * @return  the synchronization source (SSRC) identifier of the local peer
     * or <tt>-1</tt> if it is not yet known
     * @see MediaStream#getLocalSourceID()
     */
    public long getLocalSourceID()
    {
        return this.localSourceID;
    }

    /**
     * Gets the address that this stream is sending RTCP traffic to.
     *
     * @return an <tt>InetSocketAddress</tt> instance indicating the address
     * that this stream is sending RTCP traffic to
     * @see MediaStream#getRemoteControlAddress()
     */
    public InetSocketAddress getRemoteControlAddress()
    {
        return (InetSocketAddress)
                rtpConnector.getControlSocket().getRemoteSocketAddress();
    }

    /**
     * Gets the address that this stream is sending RTP traffic to.
     *
     * @return an <tt>InetSocketAddress</tt> instance indicating the address
     * that this stream is sending RTP traffic to
     * @see MediaStream#getRemoteDataAddress()
     */
    public InetSocketAddress getRemoteDataAddress()
    {
        return (InetSocketAddress)
                rtpConnector.getDataSocket().getRemoteSocketAddress();
    }

    /**
     * Get the synchronization source (SSRC) identifier of the remote peer or
     * <tt>-1</tt> if it is not yet known.
     *
     * @return  the synchronization source (SSRC) identifier of the remote
     * peer or <tt>-1</tt> if it is not yet known
     * @see MediaStream#getRemoteSourceID()
     */
    public long getRemoteSourceID()
    {
        return remoteSourceID;
    }

    /**
     * Gets the <tt>RTPManager</tt> instance which sends and receives RTP and
     * RTCP traffic on behalf of this <tt>MediaStream</tt>.
     *
     * @return the <tt>RTPManager</tt> instance which sends and receives RTP and
     * RTCP traffic on behalf of this <tt>MediaStream</tt>
     */
    private RTPManager getRTPManager()
    {
        if (rtpManager == null)
        {
            rtpManager = RTPManager.newInstance();

            registerCustomCodecFormats(rtpManager);

            rtpManager.addReceiveStreamListener(this);
            rtpManager.addSendStreamListener(this);
            rtpManager.addSessionListener(this);

            BufferControl bc
                = (BufferControl)
                    rtpManager.getControl(BufferControl.class.getName());
            if (bc != null)
                configureRTPManagerBufferControl(rtpManager, bc);

            //Emil: if you replace this method with another init method make
            //sure you check that the line below still works.
            rtpManager.initialize(rtpConnector);

            //JMF inits the local SSRC upon initialize(RTPConnector) so now's
            //the time to ask:
            setLocalSourceID(((RTPSessionMgr)rtpManager).getLocalSSRC());
        }
        return rtpManager;
    }

    /**
     * Determines whether this <tt>MediaStream</tt> is set to transmit "silence"
     * instead of the media being fed from its <tt>MediaDevice</tt>. "Silence"
     * for video is understood as video data which is not the captured video
     * data and may represent, for example, a black image.
     *
     * @return <tt>true</tt> if this <tt>MediaStream</tt> is set to transmit
     * "silence" instead of the media fed from its <tt>MediaDevice</tt>;
     * <tt>false</tt>, otherwise
     * @see MediaStream#isMute()
     */
    public boolean isMute()
    {
        MediaDeviceSession deviceSession = getDeviceSession();

        return (deviceSession == null) ? mute : deviceSession.isMute();
    }

    /**
     * Determines whether {@link #start()} has been called on this
     * <tt>MediaStream</tt> without {@link #stop()} or {@link #close()}
     * afterwards.
     *
     * @return <tt>true</tt> if {@link #start()} has been called on this
     * <tt>MediaStream</tt> without {@link #stop()} or {@link #close()}
     * afterwards
     * @see MediaStream#isStarted()
     */
    public boolean isStarted()
    {
        return started;
    }

    /**
     * Recreates the <tt>SendStream</tt>s of this instance (i.e. of its
     * <tt>RTPManager</tt>) as necessary. For example, if there was no attempt
     * to create the <tt>SendStream</tt>s prior to the call, does nothing. If
     * they were created prior to the call, closes them and creates them again.
     * If they were not started prior to the call, does not start them after
     * recreating them.
     */
    private void recreateSendStreams()
    {
        if (sendStreamsAreCreated)
        {
            closeSendStreams();
            if ((getDeviceSession() != null) && (rtpManager != null))
            {
                if (MediaDirection.SENDONLY.equals(startedDirection)
                        || MediaDirection.SENDRECV.equals(startedDirection))
                    startSendStreams();
            }
        }
    }

    /**
     * Registers any custom JMF <tt>Format</tt>s with a specific
     * <tt>RTPManager</tt>. Extenders should override in order to register their
     * own customizations and should call back to this super implementation
     * during the execution of their override in order to register the
     * associations defined in this instance of (dynamic) RTP payload types to
     * <tt>MediaFormat</tt>s.
     *
     * @param rtpManager the <tt>RTPManager</tt> to register any custom JMF
     * <tt>Format</tt>s with
     */
    protected void registerCustomCodecFormats(RTPManager rtpManager)
    {
        synchronized (dynamicRTPPayloadTypes)
        {
            for (Map.Entry<Byte, MediaFormat> dynamicRTPPayloadType
                    : dynamicRTPPayloadTypes.entrySet())
            {
                @SuppressWarnings("unchecked")
                MediaFormatImpl<? extends Format> mediaFormatImpl
                    = (MediaFormatImpl<? extends Format>)
                        dynamicRTPPayloadType.getValue();

                rtpManager.addFormat( mediaFormatImpl.getFormat(),
                                      dynamicRTPPayloadType.getKey());
            }
        }
    }

    /**
     * Sets the <tt>MediaDevice</tt> that this stream should use to play back
     * and capture media.
     * <p>
     * <b>Note</b>: Also resets any previous direction set with
     * {@link #setDirection(MediaDirection)} to the direction of the specified
     * <tt>MediaDevice</tt>.
     * </p>
     *
     * @param device the <tt>MediaDevice</tt> that this stream should use to
     * play back and capture media
     * @see MediaStream#setDevice(MediaDevice)
     */
    public void setDevice(MediaDevice device)
    {
        if (device == null)
            throw new NullPointerException("device");

        // Require AbstractMediaDevice for MediaDeviceSession support.
        AbstractMediaDevice abstractMediaDevice = (AbstractMediaDevice) device;

        if ((deviceSession == null) || (deviceSession.getDevice() != device))
        {
            MediaDeviceSession oldValue = deviceSession;
            MediaDirection startedDirection;

            if (deviceSession != null)
            {
                startedDirection = deviceSession.getStartedDirection();

                deviceSession.removePropertyChangeListener(
                    deviceSessionPropertyChangeListener);

                deviceSession.close();
                deviceSession = null;
            }
            else
                startedDirection = MediaDirection.INACTIVE;

            deviceSession = abstractMediaDevice.createSession();

            deviceSession.addPropertyChangeListener(
                deviceSessionPropertyChangeListener);

            /*
             * Setting a new device resets any previously-set direction.
             * Otherwise, we risk not being able to set a new device if it is
             * mandatory for the new device to fully cover any previously-set
             * direction.
             */
            direction = null;

            MediaDeviceSession newValue = deviceSession;

            deviceSessionChanged(oldValue, newValue);

            if (deviceSession != null)
            {
                deviceSession.setMute(mute);
                deviceSession.start(startedDirection);

                synchronized (receiveStreamSyncRoot)
                {
                    if (receiveStream != null)
                        deviceSession.setReceiveStream(receiveStream);
                }
            }
        }
    }

    /**
     * Sets the direction in which media in this <tt>MediaStream</tt> is to be
     * streamed. If this <tt>MediaStream</tt> is not currently started, calls to
     * {@link #start()} later on will start it only in the specified
     * <tt>direction</tt>. If it is currently started in a direction different
     * than the specified, directions other than the specified will be stopped.
     *
     * @param direction the <tt>MediaDirection</tt> in which this
     * <tt>MediaStream</tt> is to stream media when it is started
     * @see MediaStream#setDirection(MediaDirection)
     */
    public void setDirection(MediaDirection direction)
    {
        if (direction == null)
            throw new NullPointerException("direction");

        /*
         * Make sure that the specified direction is in accord with the
         * direction of the MediaDevice of this instance.
         */
        MediaDeviceSession deviceSession = getDeviceSession();
        MediaDirection deviceDirection
            = (deviceSession == null)
                ? MediaDirection.INACTIVE
                : deviceSession.getDevice().getDirection();

        if (!deviceDirection.and(direction).equals(direction))
            throw new IllegalArgumentException("direction");

        this.direction = direction;

        switch (this.direction)
        {
        case INACTIVE:
            stop(MediaDirection.SENDRECV);
            return;
        case RECVONLY:
            stop(MediaDirection.SENDONLY);
            break;
        case SENDONLY:
            stop(MediaDirection.RECVONLY);
            break;
        case SENDRECV:
            break;
        default:
            // Don't know what it may be (in the future) so ignore it.
            return;
        }
        if (started)
            start(this.direction);
    }

    /**
     * Sets the <tt>MediaFormat</tt> that this <tt>MediaStream</tt> should
     * transmit in.
     *
     * @param format the <tt>MediaFormat</tt> that this <tt>MediaStream</tt>
     * should transmit in
     * @see MediaStream#setFormat(MediaFormat)
     */
    public void setFormat(MediaFormat format)
    {
        getDeviceSession().setFormat(format);
    }

    /**
     * Causes this <tt>MediaStream</tt> to stop transmitting the media being fed
     * from this stream's <tt>MediaDevice</tt> and transmit "silence" instead.
     * "Silence" for video is understood as video data which is not the captured
     * video data and may represent, for example, a black image.
     *
     * @param mute <tt>true</tt> to have this <tt>MediaStream</tt> transmit
     * "silence" instead of the actual media data that it captures from its
     * <tt>MediaDevice</tt>; <tt>false</tt> to transmit actual media data
     * captured from the <tt>MediaDevice</tt> of this <tt>MediaStream</tt>
     * @see MediaStream#setMute(boolean)
     */
    public void setMute(boolean mute)
    {
        if (this.mute != mute)
        {
            this.mute = mute;

            MediaDeviceSession deviceSession = getDeviceSession();

            if (deviceSession != null)
                deviceSession.setMute(this.mute);
        }
    }

    /**
     * Sets the target of this <tt>MediaStream</tt> to which it is to send and
     * from which it is to receive data (e.g. RTP) and control data (e.g. RTCP).
     *
     * @param target the <tt>MediaStreamTarget</tt> describing the data
     * (e.g. RTP) and the control data (e.g. RTCP) locations to which this
     * <tt>MediaStream</tt> is to send and from which it is to receive
     * @see MediaStream#setTarget(MediaStreamTarget)
     */
    public void setTarget(MediaStreamTarget target)
    {
        // Short-circuit if setting the same target.
        if (target == null)
        {
            if (rtpConnectorTarget == null)
                return;
        }
        else if (target.equals(rtpConnectorTarget))
            return;

        rtpConnector.removeTargets();
        rtpConnectorTarget = null;

        boolean targetIsSet;

        if (target != null)
        {
            InetSocketAddress dataAddr = target.getDataAddress();
            InetSocketAddress controlAddr = target.getControlAddress();

            try
            {
                rtpConnector
                    .addTarget(
                        new SessionAddress(
                                dataAddr.getAddress(),
                                dataAddr.getPort(),
                                controlAddr.getAddress(),
                                controlAddr.getPort()));
                targetIsSet = true;
            }
            catch (IOException ioe)
            {
                // TODO
                targetIsSet = false;
                logger.error("Failed to set target " + target, ioe);
            }
        }
        else
            targetIsSet = true;

        if (targetIsSet)
        {
            rtpConnectorTarget = target;

            if (logger.isTraceEnabled())
                logger
                    .trace(
                        "Set target of "
                            + getClass().getSimpleName()
                            + " with hashCode "
                            + hashCode()
                            + " to "
                            + target);
        }
    }

    /**
     * Starts capturing media from this stream's <tt>MediaDevice</tt> and then
     * streaming it through the local <tt>StreamConnector</tt> toward the
     * stream's target address and port. Also puts the <tt>MediaStream</tt> in a
     * listening state which make it play all media received from the
     * <tt>StreamConnector</tt> on the stream's <tt>MediaDevice</tt>.
     *
     * @see MediaStream#start()
     */
    public void start()
    {
        start(getDirection());
        started = true;
    }

    /**
     * Starts the processing of media in this instance in a specific direction.
     *
     * @param direction a <tt>MediaDirection</tt> value which represents the
     * direction of the processing of media to be started. For example,
     * {@link MediaDirection#SENDRECV} to start both capture and playback of
     * media in this instance or {@link MediaDirection#SENDONLY} to only start
     * the capture of media in this instance
     */
    @SuppressWarnings("unchecked")
    private void start(MediaDirection direction)
    {
        if (direction == null)
            throw new NullPointerException("direction");

        if (direction.allowsSending()
                && ((startedDirection == null)
                        || !startedDirection.allowsSending()))
        {
            startSendStreams();

            getDeviceSession().start(MediaDirection.SENDONLY);

            if (MediaDirection.RECVONLY.equals(startedDirection))
                startedDirection = MediaDirection.SENDRECV;
            else if (startedDirection == null)
                startedDirection = MediaDirection.SENDONLY;
        }

        if (direction.allowsReceiving()
                && ((startedDirection == null)
                        || !startedDirection.allowsReceiving()))
        {
            RTPManager rtpManager = getRTPManager();
            Iterable<ReceiveStream> receiveStreams;

            try
            {
                receiveStreams = rtpManager.getReceiveStreams();
            }
            catch (Exception e)
            {
                /*
                 * it appears that in early call states, when there are no
                 * streams this method could throw a null pointer exception.
                 * Make sure we handle it gracefully
                 */
                logger.trace("Failed to retrieve receive streams", e);
                receiveStreams = null;
            }

            if (receiveStreams != null)
                for (ReceiveStream receiveStream : receiveStreams)
                    try
                    {
                        DataSource receiveStreamDataSource
                            = receiveStream.getDataSource();

                        /*
                         * For an unknown reason, the stream DataSource can be
                         * null at the end of the Call after re-INVITEs have
                         * been handled.
                         */
                        if (receiveStreamDataSource != null)
                            receiveStreamDataSource.start();
                    }
                    catch (IOException ioe)
                    {
                        logger
                            .warn(
                                "Failed to start stream " + receiveStream,
                                ioe);
                    }

            getDeviceSession().start(MediaDirection.RECVONLY);

            if (MediaDirection.SENDONLY.equals(startedDirection))
                startedDirection = MediaDirection.SENDRECV;
            else if (startedDirection == null)
                startedDirection = MediaDirection.RECVONLY;
        }
    }

    /**
     * Starts the <tt>SendStream</tt>s of the <tt>RTPManager</tt> of this
     * <tt>MediaStream</tt>.
     */
    private void startSendStreams()
    {
        /*
         * Until it's clear that the SendStreams are required (i.e. we've
         * negotiated to send), they will not be created. Otherwise, their
         * creation isn't only illogical but also causes the CaptureDevice to
         * be used.
         */
        if (!sendStreamsAreCreated)
            createSendStreams();

        RTPManager rtpManager = getRTPManager();
        @SuppressWarnings("unchecked")
        Iterable<SendStream> sendStreams = rtpManager.getSendStreams();

        if (sendStreams != null)
            for (SendStream sendStream : sendStreams)
                try
                {
                    // TODO Are we sure we want to connect here?
                    sendStream.getDataSource().connect();
                    sendStream.start();
                    sendStream.getDataSource().start();

                    if (logger.isTraceEnabled())
                        logger
                            .trace(
                                "Started SendStream"
                                    + " with hashCode "
                                    + sendStream.hashCode());
                }
                catch (IOException ioe)
                {
                    logger
                        .warn("Failed to start stream " + sendStream, ioe);
                }
    }

    /**
     * Stops all streaming and capturing in this <tt>MediaStream</tt> and closes
     * and releases all open/allocated devices/resources. Has no effect if this
     * <tt>MediaStream</tt> is already closed and is simply ignored.
     *
     * @see MediaStream#stop()
     */
    public void stop()
    {
        stop(MediaDirection.SENDRECV);
        started = false;
    }

    /**
     * Stops the processing of media in this instance in a specific direction.
     *
     * @param direction a <tt>MediaDirection</tt> value which represents the
     * direction of the processing of media to be stopped. For example,
     * {@link MediaDirection#SENDRECV} to stop both capture and playback of
     * media in this instance or {@link MediaDirection#SENDONLY} to only stop
     * the capture of media in this instance
     */
    @SuppressWarnings("unchecked")
    private void stop(MediaDirection direction)
    {
        if (direction == null)
            throw new NullPointerException("direction");

        if (rtpManager == null)
            return;

        if ((MediaDirection.SENDRECV.equals(direction)
                    || MediaDirection.SENDONLY.equals(direction))
                && (MediaDirection.SENDRECV.equals(startedDirection)
                        || MediaDirection.SENDONLY.equals(startedDirection)))
        {
            stopSendStreams(false);

            if (deviceSession != null)
                deviceSession.stop(MediaDirection.SENDONLY);

            if (MediaDirection.SENDRECV.equals(startedDirection))
                startedDirection = MediaDirection.RECVONLY;
            else if (MediaDirection.SENDONLY.equals(startedDirection))
                startedDirection = null;
        }

        if ((MediaDirection.SENDRECV.equals(direction)
                || MediaDirection.RECVONLY.equals(direction))
            && (MediaDirection.SENDRECV.equals(startedDirection)
                    || MediaDirection.RECVONLY.equals(startedDirection)))
        {
            Iterable<ReceiveStream> receiveStreams;

            try
            {
                receiveStreams = rtpManager.getReceiveStreams();
            }
            catch (Exception e)
            {
                /*
                 * it appears that in early call states, when there are no
                 * streams this method could throw a null pointer exception.
                 * Make sure we handle it gracefully
                 */
                logger.trace("Failed to retrieve receive streams", e);
                receiveStreams = null;
            }

            if (receiveStreams != null)
                for (ReceiveStream receiveStream : receiveStreams)
                    try
                    {
                        DataSource receiveStreamDataSource
                            = receiveStream.getDataSource();

                        /*
                         * For an unknown reason, the stream DataSource can be
                         * null at the end of the Call after re-INVITEs have
                         * been handled.
                         */
                        if (receiveStreamDataSource != null)
                            receiveStreamDataSource.stop();
                    }
                    catch (IOException ioe)
                    {
                        logger
                            .warn(
                                "Failed to stop stream " + receiveStream,
                                ioe);
                    }

            if (deviceSession != null)
                deviceSession.stop(MediaDirection.RECVONLY);

            if (MediaDirection.SENDRECV.equals(startedDirection))
                startedDirection = MediaDirection.SENDONLY;
            else if (MediaDirection.RECVONLY.equals(startedDirection))
                startedDirection = null;
        }
    }

    /**
     * Stops the <tt>SendStream</tt> that this instance is sending to its
     * remote peer and optionally closes them.
     *
     * @param close <tt>true</tt> to close the <tt>SendStream</tt>s that this
     * instance is sending to its remote peer after stopping them;
     * <tt>false</tt> to only stop them
     * @return the <tt>SendStream</tt>s which were stopped
     */
    private Iterable<SendStream> stopSendStreams(boolean close)
    {
        if (rtpManager == null)
            return null;

        @SuppressWarnings("unchecked")
        Iterable<SendStream> sendStreams = rtpManager.getSendStreams();
        Iterable<SendStream> stoppedSendStreams
            = stopSendStreams(sendStreams, close);

        if (close)
            sendStreamsAreCreated = false;

        return stoppedSendStreams;
    }

    /**
     * Stops specific <tt>SendStream</tt>s and optionally closes them.
     *
     * @param sendStreams the <tt>SendStream</tt>s to be stopped and optionally
     * closed
     * @param close <tt>true</tt> to close the specified <tt>SendStream</tt>s
     * after stopping them; <tt>false</tt> to only stop them
     * @return the stopped <tt>SendStream</tt>s
     */
    private Iterable<SendStream> stopSendStreams(
            Iterable<SendStream> sendStreams,
            boolean close)
    {
        if (sendStreams == null)
            return null;

        for (SendStream sendStream : sendStreams)
            try
            {
                sendStream.getDataSource().stop();
                sendStream.stop();

                if (close)
                    try
                    {
                        sendStream.close();
                    }
                    catch (NullPointerException npe)
                    {
                        /*
                         * Sometimes com.sun.media.rtp.RTCPTransmitter#bye() may
                         * throw NullPointerException but it does not seem to be
                         * guaranteed because it does not happen while debugging
                         * and stopping at a breakpoint on SendStream#close().
                         * One of the cases in which it appears upon call
                         * hang-up is if we do not close the "old" SendStreams
                         * upon reinvite(s). Though we are now closing such
                         * SendStreams, ignore the exception here just in case
                         * because we already ignore IOExceptions.
                         */
                        logger
                            .error(
                                "Failed to close stream " + sendStream,
                                npe);
                    }
            }
            catch (IOException ioe)
            {
                logger.warn("Failed to stop stream " + sendStream, ioe);
            }
        return sendStreams;
    }

    /**
     * Returns a human-readable representation of a specific <tt>DataSource</tt>
     * instance in the form of a <tt>String</tt> value.
     *
     * @param dataSource the <tt>DataSource</tt> to return a human-readable
     * representation of
     * @return a <tt>String</tt> value which gives a human-readable
     * representation of the specified <tt>dataSource</tt>
     */
    public static String toString(DataSource dataSource)
    {
        StringBuffer str = new StringBuffer();

        str.append(dataSource.getClass().getSimpleName());
        str.append(" with hashCode ");
        str.append(dataSource.hashCode());

        MediaLocator locator = dataSource.getLocator();

        if (locator != null)
        {
            str.append(" and locator ");
            str.append(locator);
        }
        return str.toString();
    }

    /**
     * Notifies this <tt>ReceiveStreamListener</tt> that the <tt>RTPManager</tt>
     * it is registered with has generated an event related to a <tt>ReceiveStream</tt>.
     *
     * @param event the <tt>ReceiveStreamEvent</tt> which specifies the
     * <tt>ReceiveStream</tt> that is the cause of the event and the very type
     * of the event
     * @see ReceiveStreamListener#update(ReceiveStreamEvent)
     */
    public void update(ReceiveStreamEvent event)
    {
        if (event instanceof NewReceiveStreamEvent)
        {
            ReceiveStream receiveStream = event.getReceiveStream();

            if (receiveStream != null)
            {
                long receiveStreamSSRC = receiveStream.getSSRC();

                if (logger.isTraceEnabled())
                    logger
                        .trace(
                            "Received new ReceiveStream with ssrc "
                                + receiveStreamSSRC);

                setRemoteSourceID(receiveStreamSSRC);

                synchronized (receiveStreamSyncRoot)
                {
                    if (this.receiveStream != receiveStream)
                    {
                        this.receiveStream = receiveStream;

                        MediaDeviceSession deviceSession = getDeviceSession();

                        if (deviceSession != null)
                            deviceSession.setReceiveStream(this.receiveStream);
                    }
                }
            }
        }
        else if (event instanceof TimeoutEvent)
        {
            ReceiveStream receiveStream = event.getReceiveStream();

            if (receiveStream != null)
                synchronized (receiveStreamSyncRoot)
                {
                    if (this.receiveStream == receiveStream)
                    {
                        this.receiveStream = null;

                        MediaDeviceSession deviceSession = getDeviceSession();

                        if (deviceSession != null)
                            deviceSession.setReceiveStream(null);
                    }
                }
        }
    }

    /**
     * Notifies this <tt>SendStreamListener</tt> that the <tt>RTPManager</tt> it
     * is registered with has generated an event related to a <tt>SendStream</tt>.
     *
     * @param event the <tt>SendStreamEvent</tt> which specifies the
     * <tt>SendStream</tt> that is the cause of the event and the very type of
     * the event
     * @see SendStreamListener#update(SendStreamEvent)
     */
    public void update(SendStreamEvent event)
    {
        // TODO Auto-generated method stub
    }


    /**
     * Notifies this <tt>SessionListener</tt> that the <tt>RTPManager</tt> it is
     * registered with has generated an event which pertains to the session as a
     * whole and does not belong to a <tt>ReceiveStream</tt> or a
     * <tt>SendStream</tt> or a remote participant necessarily.
     *
     * @param event the <tt>SessionEvent</tt> which specifies the source and the
     * very type of the event
     * @see SessionListener#update(SessionEvent)
     */
    public void update(SessionEvent event)
    {
        // TODO Auto-generated method stub
    }

    /**
     * Sets the local SSRC identifier and fires the corresponding
     * <tt>PropertyChangeEvent</tt>.
     *
     * @param ssrc the SSRC identifier that this stream will be using in
     * outgoing RTP packets from now on.
     */
    private void setLocalSourceID(long ssrc)
    {
        Long oldValue = this.localSourceID;

        this.localSourceID = ssrc;

        firePropertyChange(PNAME_LOCAL_SSRC, oldValue, ssrc);
    }

    /**
     * Sets the remote SSRC identifier and fires the corresponding
     * <tt>PropertyChangeEvent</tt>.
     *
     * @param ssrc the SSRC identifier that this stream will be using in
     * outgoing RTP packets from now on.
     */
    private void setRemoteSourceID(long ssrc)
    {
        Long oldValue = this.remoteSourceID;
        this.remoteSourceID = ssrc;

        firePropertyChange(PNAME_REMOTE_SSRC, oldValue, ssrc);
    }

    /**
     * Returns the list of CSRC identifiers for all parties currently known
     * to contribute to the media that this stream is sending toward its remote
     * counter part. In other words, the method returns the list of CSRC IDs
     * that this stream will include in outgoing RTP packets. This method will
     * return an <tt>null</tt> in case this stream is not part of a mixed
     * conference call.
     *
     * @return a <tt>long[]</tt> array of CSRC IDs representing parties that are
     * currently known to contribute to the media that this stream is sending
     * or an <tt>null</tt> in case this <tt>MediaStream</tt> is not part of a
     * conference call.
     */
    public long[] getLocalContributingSourceIDs()
    {
        return localContributingSourceIDList;
    }

    /**
     * Returns the <tt>List</tt> of CSRC identifiers representing the parties
     * contributing to the stream that we are receiving from this
     * <tt>MediaStream</tt>'s remote party.
     *
     * @return a <tt>List</tt> of CSRC identifiers representing the parties
     * contributing to the stream that we are receiving from this
     * <tt>MediaStream</tt>'s remote party.
     */
    public long[] getRemoteContributingSourceIDs()
    {
        long[] remoteSsrcList = getDeviceSession().getRemoteSSRCList();

        // TODO implement

        return remoteSsrcList;
    }

    /**
     * The <tt>ZrtpControl</tt> which controls the ZRTP for the current stream.
     * @return the <tt>ZrtpControl</tt> for the current stream.
     */
    public ZrtpControl getZrtpControl()
    {
        return zrtpControl;
    }
}
