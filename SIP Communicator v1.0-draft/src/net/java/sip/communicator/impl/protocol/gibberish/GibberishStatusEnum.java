/*
 * SIP Communicator, the OpenSource Java VoIP and Instant Messaging client.
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package net.java.sip.communicator.impl.protocol.gibberish;

import java.util.*;

import net.java.sip.communicator.service.protocol.*;
import net.java.sip.communicator.util.*;
import java.io.*;

/**
 * An implementation of <tt>PresenceStatus</tt> that enumerates all states that
 * a Gibberish contact can fall into.
 *
 * @author Emil Ivov
 */
public class GibberishStatusEnum
    extends PresenceStatus
{
    private static final Logger logger
        = Logger.getLogger(GibberishStatusEnum.class);

    /**
     * Indicates an Offline status or status with 0 connectivity.
     */
    public static final GibberishStatusEnum OFFLINE
        = new GibberishStatusEnum(
            0,
            "Offline",
            getImageInBytes("service.protocol.gibberish.OFFLINE_STATUS_ICON"));

    /**
     * An Occupied status. Indicates that the user has connectivity and
     * communication is particularly unwanted.
     */
    public static final GibberishStatusEnum OCCUPIED
        = new GibberishStatusEnum(
            20,
            "Occupied",
            getImageInBytes("service.protocol.gibberish.OCCUPIED_STATUS_ICON"));

    /**
     * The DND status. Indicates that the user has connectivity but prefers
     * not to be contacted.
     */
    public static final GibberishStatusEnum DO_NOT_DISTURB
        = new GibberishStatusEnum(
            30,
            "Do Not Disturb",
            getImageInBytes("service.protocol.gibberish.DND_STATUS_ICON"));

    /**
     * The Not Available status. Indicates that the user has connectivity
     * but might not be able to immediately act (i.e. even less immediately than
     * when in an Away status ;-P ) upon initiation of communication.
     *
     */
    public static final GibberishStatusEnum NOT_AVAILABLE
        = new GibberishStatusEnum(
            35,
            "Not Available",
            getImageInBytes("service.protocol.gibberish.NA_STATUS_ICON"));

    /**
     * The Away status. Indicates that the user has connectivity but might
     * not be able to immediately act upon initiation of communication.
     */
    public static final GibberishStatusEnum AWAY
        = new GibberishStatusEnum(
            40,
            "Away",
            getImageInBytes("service.protocol.gibberish.AWAY_STATUS_ICON"));

    /**
     * The Invisible status. Indicates that the user has connectivity even
     * though it may appear otherwise to others, to whom she would appear to be
     * offline.
     */
    public static final GibberishStatusEnum INVISIBLE
        = new GibberishStatusEnum(
            45,
            "Invisible",
            getImageInBytes("service.protocol.gibberish.INVISIBLE_STATUS_ICON"));

    /**
     * The Online status. Indicate that the user is able and willing to
     * communicate.
     */
    public static final GibberishStatusEnum ONLINE
        = new GibberishStatusEnum(
            65,
            "Online",
            getImageInBytes("service.protocol.gibberish.ONLINE_STATUS_ICON"));

    /**
     * The Free For Chat status. Indicates that the user is eager to
     * communicate.
     */
    public static final GibberishStatusEnum FREE_FOR_CHAT
        = new GibberishStatusEnum(
            85,
            "Free For Chat",
            getImageInBytes("service.protocol.gibberish.FFC_STATUS_ICON"));

    /**
     * Initialize the list of supported status states.
     */
    private static List<PresenceStatus> supportedStatusSet = new LinkedList<PresenceStatus>();
    static
    {
        supportedStatusSet.add(OFFLINE);
        supportedStatusSet.add(OCCUPIED);
        supportedStatusSet.add(DO_NOT_DISTURB);
        supportedStatusSet.add(NOT_AVAILABLE);
        supportedStatusSet.add(AWAY);
        supportedStatusSet.add(INVISIBLE);
        supportedStatusSet.add(ONLINE);
        supportedStatusSet.add(FREE_FOR_CHAT);
    }

    /**
     * Creates an instance of <tt>GibberishPresneceStatus</tt> with the
     * specified parameters.
     * @param status the connectivity level of the new presence status instance
     * @param statusName the name of the presence status.
     * @param statusIcon the icon associated with this status
     */
    private GibberishStatusEnum(int status,
                                String statusName,
                                byte[] statusIcon)
    {
        super(status, statusName, statusIcon);
    }

    /**
     * Returns an iterator over all status instances supproted by the gibberish
     * provider.
     * @return an <tt>Iterator</tt> over all status instances supported by the
     * gibberish provider.
     */
    static Iterator<PresenceStatus> supportedStatusSet()
    {
        return supportedStatusSet.iterator();
    }

    /**
     * Returns the byte representation of the image corresponding to the given
     * identifier.
     * 
     * @param imageID the identifier of the image
     * @return the byte representation of the image corresponding to the given
     * identifier.
     */
    private static byte[] getImageInBytes(String imageID) 
    {
        InputStream in = GibberishActivator.getResources().
            getImageInputStream(imageID);

        if (in == null)
            return null;
        byte[] image = null;
        try 
        {
            image = new byte[in.available()];

            in.read(image);
        }
        catch (IOException e) 
        {
            logger.error("Failed to load image:" + imageID, e);
        }

        return image;
    }
}
