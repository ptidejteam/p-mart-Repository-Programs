/*
 * SIP Communicator, the OpenSource Java VoIP and Instant Messaging client.
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package net.java.sip.communicator.impl.protocol.mock;

import java.util.*;

import net.java.sip.communicator.service.protocol.*;
import net.java.sip.communicator.service.protocol.event.*;
import net.java.sip.communicator.util.*;

/**
 * @author Damian Minkov
 */
public class MockCall
    extends Call
    implements CallPeerListener
{
    private static final Logger logger = Logger.getLogger(MockCall.class);

    /**
     * A list containing all <tt>CallPeer</tt>s of this call.
     */
    private Vector<CallPeer> callPeers = new Vector<CallPeer>();


    public MockCall(MockProvider sourceProvider)
    {
        super(sourceProvider);
    }

    /**
     * Returns an iterator over all call peers.
     *
     * @return an Iterator over all peers currently involved in the
     *   call.
     */
    public Iterator<CallPeer> getCallPeers()
    {
        return callPeers.iterator();
    }

    /**
     * Returns the number of peers currently associated with this call.
     *
     * @return an <tt>int</tt> indicating the number of peers
     *   currently associated with this call.
     */
    public int getCallPeerCount()
    {
        return callPeers.size();
    }

    /**
     * Adds <tt>callPeer</tt> to the list of peers in this call.
     * If the call peer is already included in the call, the method has
     * no effect.
     *
     * @param callPeer the new <tt>CallPeer</tt>
     */
    public void addCallPeer(MockCallPeer callPeer)
    {
        if(callPeers.contains(callPeer))
            return;

        callPeer.addCallPeerListener(this);

        this.callPeers.add(callPeer);

        logger.info("Will fire peer added");

        fireCallPeerEvent(
            callPeer, CallPeerEvent.CALL_PEER_ADDED);
    }

    /**
     * Removes <tt>callPeer</tt> from the list of peers in this
     * call. The method has no effect if there was no such peer in the
     * call.
     *
     * @param callPeer the <tt>CallPeer</tt> leaving the call;
     */
    public void removeCallPeer(MockCallPeer callPeer)
    {
        if(!callPeers.contains(callPeer))
            return;

        this.callPeers.remove(callPeer);
        callPeer.removeCallPeerListener(this);

        fireCallPeerEvent(
            callPeer, CallPeerEvent.CALL_PEER_REMOVED);

        if(callPeers.size() == 0)
            setCallState(CallState.CALL_ENDED);
    }

    public void peerStateChanged(CallPeerChangeEvent evt)
    {
        if ( ( (CallPeerState) evt.getNewValue())
            == CallPeerState.DISCONNECTED
            || ( (CallPeerState) evt.getNewValue())
            == CallPeerState.FAILED)
        {
            removeCallPeer(
                (MockCallPeer) evt.getSourceCallPeer());
        }
        else if ( ( (CallPeerState) evt.getNewValue())
                 == CallPeerState.CONNECTED
                 && getCallState().equals(CallState.CALL_INITIALIZATION))
        {
            setCallState(CallState.CALL_IN_PROGRESS);
        }
    }

    public void peerDisplayNameChanged(CallPeerChangeEvent evt)
    {
    }

    public void peerAddressChanged(CallPeerChangeEvent evt)
    {
    }

    public void peerImageChanged(CallPeerChangeEvent evt)
    {
    }

    public void peerTransportAddressChanged(CallPeerChangeEvent
        evt)
    {
    }

    /**
     * Gets the indicator which determines whether the local peer represented by
     * this <tt>Call</tt> is acting as a conference focus and thus should send
     * the &quot;isfocus&quot; parameter in the Contact headers of its outgoing
     * SIP signaling.
     *
     * @return <tt>true</tt> if the local peer represented by this <tt>Call</tt>
     * is acting as a conference focus; otherwise, <tt>false</tt>
     */
    public boolean isConferenceFocus()
    {
        return false;
    }

    /**
     * Adds a specific <tt>SoundLevelListener</tt> to the list of
     * listeners interested in and notified about changes in local sound level
     * related information.
     * @param l the <tt>SoundLevelListener</tt> to add
     */
    public void addLocalUserSoundLevelListener(
        SoundLevelListener l)
    {
        
    }

    /**
     * Removes a specific <tt>SoundLevelListener</tt> of the list of
     * listeners interested in and notified about changes in local sound level
     * related information.
     * @param l the <tt>SoundLevelListener</tt> to remove
     */
    public void removeLocalUserSoundLevelListener(
        SoundLevelListener l)
    {

    }
}
