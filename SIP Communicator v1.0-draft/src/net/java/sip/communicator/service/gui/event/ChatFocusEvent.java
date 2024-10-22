/*
 * SIP Communicator, the OpenSource Java VoIP and Instant Messaging client.
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package net.java.sip.communicator.service.gui.event;

import java.util.*;

import net.java.sip.communicator.service.gui.*;

/**
 * The <tt>ChatFocusEvent</tt> indicates that a <tt>Chat</tt> has gained or lost
 * the current focus.
 * 
 * @author Yana Stamcheva
 */
public class ChatFocusEvent
    extends EventObject
{
    private int eventID = -1;

    /**
     * Indicates that the ChatFocusEvent instance was triggered by
     * <tt>Chat</tt> gaining the focus.
     */
    public static final int FOCUS_GAINED = 1;

    /**
     * Indicates that the ChatFocusEvent instance was triggered by
     * <tt>Chat</tt> losing the focus.
     */
    public static final int FOCUS_LOST = 2;

    /**
     * Creates a new <tt>ChatFocusEvent</tt> according to the
     * specified parameters.
     * @param source The <tt>Chat</tt> that triggers the event.
     * @param eventID one of the FOCUS_XXX static fields indicating the
     * nature of the event.
     */
    public ChatFocusEvent(Object source, int eventID)
    {
        super(source);
        this.eventID = eventID;
    }
    
    /**
     * Returns an event id specifying what is the type of this event 
     * (FOCUS_GAINED or FOCUS_LOST)
     * @return one of the REGISTRATION_XXX int fields of this class.
     */
    public int getEventID(){
        return eventID;
    }
    
    /**
     * Returns the <tt>Chat</tt> object that corresponds to this event.
     * 
     * @return the <tt>Chat</tt> object that corresponds to this event
     */
    public Chat getChat()
    {
        return (Chat) source;
    }
}
