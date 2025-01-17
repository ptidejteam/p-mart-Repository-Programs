/*
 * SIP Communicator, the OpenSource Java VoIP and Instant Messaging client.
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package net.java.sip.communicator.service.protocol.event;

import java.util.*;

import net.java.sip.communicator.service.protocol.*;
import net.java.sip.communicator.service.protocol.whiteboardobjects.*;

/**
 * <tt>WhiteboardObjectDeliveredEvent</tt>s confirms successful delivery
 * of a WhiteboardObject.
 *
 * @author Julien Waechter
 * @author Emil Ivov
 */
public class WhiteboardObjectDeliveredEvent
        extends EventObject
{
    /**
     * The contact that has sent this wbObject.
     */
    private Contact to = null;

    /**
     * A timestamp indicating the exact date when the event occurred.
     */
    private Date timestamp = null;

    /**
     * The whiteboard object that has just been delivered.
     */
    private  WhiteboardObject obj;

    /**
     * Creates a <tt>WhiteboardObjectDeliveredEvent</tt> representing delivery
     * of the <tt>source</tt> whiteboardObject to the specified <tt>to</tt>
     * contact.
     *
     * @param source the <tt>WhiteboardSession</tt> whose delivery
     * this event represents.
     * @param obj the <tt>WhiteboardObject</tt>
     * @param to the <tt>Contact</tt> that this whiteboardObject was sent to.
     * @param timestamp a date indicating the exact moment when the event
     * ocurred
     */
    public WhiteboardObjectDeliveredEvent ( WhiteboardSession source,
                                            WhiteboardObject obj,
                                            Contact to,
                                            Date timestamp)
    {
        super(source);
        this.obj = obj;
        this.to = to;
        this.timestamp = timestamp;
    }

    /**
     * Returns a reference to the <tt>Contact</tt> that the source
     * <tt>WhiteboardObject</tt> was sent to.
     *
     * @return a reference to the <tt>Contact</tt> that has send the
     * <tt>WhiteboardObject</tt> whose reception this event represents.
     */
    public Contact getDestinationContact()
    {
        return to;
    }

    /**
     * Returns the whiteboardObject that triggered this event
     *
     * @return the <tt>WhiteboardObject</tt> that triggered this event.
     */
    public WhiteboardObject getSourceWhiteboardObject()
    {
        return obj;
    }

    /**
     * A timestamp indicating the exact date when the event ocurred.
     *
     * @return a Date indicating when the event ocurred.
     */
    public Date getTimestamp()
    {
        return timestamp;
    }
}
