/*
 * SIP Communicator, the OpenSource Java VoIP and Instant Messaging client.
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package net.java.sip.communicator.service.protocol.event;

import java.util.*;

/**
 * CallPeerSecurityListener interface extends EventListener. This is the
 * listener interface used to handle an event related with a change in security
 * status.
 *
 * The change in security status is triggered at the protocol level, which
 * signal security state changes to the GUI. This modifies the current security
 * status indicator for the call sessions.
 *
 * @author Werner Dittmann
 * @author Yana Stamcheva
 */
public interface CallPeerSecurityListener
    extends EventListener
{
    /**
     * The handler for the security event received. The security event
     * represents an indication of change in the security status.
     *
     * @param securityEvent
     *            the security event received
     */
    public void securityOn(
        CallPeerSecurityOnEvent securityEvent);

    /**
     * The handler for the security event received. The security event
     * represents an indication of change in the security status.
     *
     * @param securityEvent
     *            the security event received
     */
    public void securityOff(
        CallPeerSecurityOffEvent securityEvent);

    /**
     * The handler of the security message event.
     *
     * @param event the security message event.
     */
    public void securityMessageRecieved(
        CallPeerSecurityMessageEvent event);
}
