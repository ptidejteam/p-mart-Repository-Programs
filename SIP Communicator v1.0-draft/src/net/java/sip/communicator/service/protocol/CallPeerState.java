/*
 * SIP Communicator, the OpenSource Java VoIP and Instant Messaging client.
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package net.java.sip.communicator.service.protocol;

/**
 * The CallPeerState class reflects the current state of a call
 * peer. In other words when you start calling your grand mother she will
 * be in a INITIATING_CALL state, when her phone rings her state will change to
 * ALERTING_REMOTE_SIDE, and when she replies she will enter a CONNCECTED state.
 *
 * <p>Though not mandatory CallPeerState would generally have one of the
 * following life cycles
 *
 * <p> In the case with your grand mother that we just described we have:
 * <br>INITIATING_CALL -> CONNECTING -> ALERTING_REMOTE_USER -> CONNECTED -> DISCONNECTED
 *
 * <p> If your granny was already on the phone we have:
 * <br>INITIATING_CALL -> CONNECTING -> BUSY -> DISCONNECTED
 *
 * <p>Whenever someone tries to reach you:
 * <br>INCOMING_CALL -> CONNECTED -> DISCONNECTED
 *
 * <p>A FAILED state is prone to appear at any place in the above diagram and is
 * generally followed by a disconnected state.
 *
 * <p>Information on call peer is shown in the phone user interface until
 * they enter the DISCONNECTED state. At that point call peer information
 * is automatically removed from the user interface and the call is considered
 * terminated.
 *
 * @author Emil Ivov
 * @author Lubomir Marinov
 */
public class CallPeerState
{
    /**
     * This constant value indicates a String representation of the UNKNOWN
     * call state.
     * <br>This constant has the String value "Unknown".
     */
    public static final String _UNKNOWN      = "Unknown";

    /**
     * This constant value indicates that the state of the call peer is
     * is UNKNOWN - which means that there is no information on the state for
     * the time being (this constant should be used as a default value for
     * newly created call peer that don't yet have an attributed call
     * state.
     */
    public static final CallPeerState UNKNOWN =
                                        new CallPeerState(_UNKNOWN);

    /**
     * This constant value indicates a String representation of the
     * INITIATING_CALL call state.
     * <br>This constant has the String value "Initiating Call".
     */
    public static final String _INITIATING_CALL      = "Initiating Call";

    /**
     * This constant value indicates that the state of the call peer is
     * is INITIATING_CALL - which means that we're currently trying to open a
     * socket and send our request. In the case of SIP for example we will leave
     * this state the moment we receive a "100 Trying" request from a proxy or
     * the remote side.
     */
    public static final CallPeerState INITIATING_CALL =
                                        new CallPeerState(_INITIATING_CALL);

    /**
     * This constant value indicates a String representation of the CONNECTING
     * call state.
     * <br>This constant has the String value "Connecting".
     */
    public static final String _CONNECTING      = "Connecting";

    /**
     * This constant value indicates that the state of the call peer is
     * CONNECTING - which means that a network connection to that peer
     * is currently being established.
     */
    public static final CallPeerState CONNECTING =
                                        new CallPeerState(_CONNECTING);

    /**
     * This constant value indicates a String representation of the CONNECTING
     * call state but in cases where early media is being exchanged.
     * <br>This constant has the String value "Connecting".
     */
    public static final String _CONNECTING_WITH_EARLY_MEDIA = "Connecting*";

    /**
     * This constant value indicates that the state of the call peer is
     * CONNECTING - which means that a network connection to that peer
     * is currently being established.
     */
    public static final CallPeerState CONNECTING_WITH_EARLY_MEDIA =
                       new CallPeerState( _CONNECTING_WITH_EARLY_MEDIA );

    /**
     * This constant value indicates that the state of the  incoming call peer
     * is CONNECTING - which means that a network connection to that peer
     * is currently being established.
     */
    public static final CallPeerState CONNECTING_INCOMING_CALL =
                       new CallPeerState( _CONNECTING_WITH_EARLY_MEDIA );

    /**
     * This constant value indicates that the state of the incoming call peer
     * is CONNECTING - which means that a network connection to that peer
     * is currently being established and during the process before hearing
     * the other peer we can still can hear media coming from the 
     * server for example.
     */
    public static final CallPeerState CONNECTING_INCOMING_CALL_WITH_MEDIA =
                       new CallPeerState( _CONNECTING_WITH_EARLY_MEDIA );

    /**
     * This constant value indicates a String representation of the
     * ALERTING_REMOTE_SIDE call state.
     * <br>This constant has the String value "Alerting Remote User".
     */
    public static final String _ALERTING_REMOTE_SIDE
                                             = "Alerting Remote User (Ringing)";

    /**
     * This constant value indicates that the state of the call peer is
     * is ALERTING_REMOTE_SIDE - which means that a network connection to that
     * peer has been established and peer's phone is currently alerting the
     * remote user of the current call.
     */
    public static final CallPeerState ALERTING_REMOTE_SIDE =
                                new CallPeerState(_ALERTING_REMOTE_SIDE);

    /**
     * This constant value indicates a String representation of the
     * INCOMING_CALL call state.
     * <br>This constant has the String value "Incoming Call".
     */
    public static final String _INCOMING_CALL         = "Incoming Call";

    /**
     * This constant value indicates that the state of the call peer is
     * is INCOMING_CALL - which means that the peer is willing to start
     * a call with us. At that point local side should be playing a sound or a
     * graphical alert (the phone is ringing).
     */
    public static final CallPeerState INCOMING_CALL
                                         = new CallPeerState(_INCOMING_CALL);

    /**
     * This constant value indicates a String representation of the CONNECTED
     * call state.
     * <br>This constant has the String value "Connected".
     */
    public static final String _CONNECTED       = "Connected";

    /**
     * This constant value indicates that the state of the call peer is
     * is CONNECTED - which means that there is an ongoing call with that
     * peer.
     */
    public static final CallPeerState CONNECTED
                                       = new CallPeerState(_CONNECTED);

    /**
     * This constant value indicates a String representation of the DISCONNECTED
     * call state.
     * <br>This constant has the String value "Disconnected".
     */
    public static final String _DISCONNECTED    = "Disconnected";

    /**
     * This constant value indicates that the state of the call peer is
     * is DISCONNECTED - which means that this peer is not participating :)
     * in the call any more.
     */
    public static final CallPeerState DISCONNECTED    =
                                      new CallPeerState(_DISCONNECTED);

    /**
     * This constant value indicates a String representation of the BUSY
     * call state.
     * <br>This constant has the String value "Busy".
     */
    public static final String _BUSY            = "Busy";

    /**
     * This constant value indicates that the state of the call peer is
     * is BUSY - which means that an attempt to establish a call with that
     * peer has been made and that it has been turned down by them (e.g.
     * because they were already in a call).
     */
    public static final CallPeerState BUSY
                                            = new CallPeerState(_BUSY);

    /**
     * This constant value indicates a String representation of the FAILED
     * call state.
     * <br>This constant has the String value "Failed".
     */
    public static final String _FAILED          = "Failed";
    /**
     * This constant value indicates that the state of the call peer is
     * is ON_HOLD - which means that an attempt to establish a call with that
     * peer has failed for an unexpected reason.
     */
    public static final CallPeerState FAILED
                                          = new CallPeerState(_FAILED);

    /**
     * The constant value being a String representation of the ON_HOLD_LOCALLY
     * call peer state.
     * <p>
     * This constant has the String value "Locally On Hold".
     * </p>
     */
    public static final String _ON_HOLD_LOCALLY = "Locally On Hold";
    /**
     * The constant value indicating that the state of a call peer is
     * locally put on hold.
     */
    public static final CallPeerState ON_HOLD_LOCALLY
                                = new CallPeerState(_ON_HOLD_LOCALLY);

    /**
     * The constant value being a String representation of the ON_HOLD_MUTUALLY
     * call peer state.
     * <p>
     * This constant has the String value "Mutually On Hold".
     * </p>
     */
    public static final String _ON_HOLD_MUTUALLY = "Mutually On Hold";
    /**
     * The constant value indicating that the state of a call peer is
     * mutually - locally and remotely - put on hold.
     */
    public static final CallPeerState ON_HOLD_MUTUALLY
                                = new CallPeerState(_ON_HOLD_MUTUALLY);

    /**
     * The constant value being a String representation of the ON_HOLD_REMOTELY
     * call peer state.
     * <p>
     * This constant has the String value "Remotely On Hold".
     * </p>
     */
    public static final String _ON_HOLD_REMOTELY = "Remotely On Hold";

    /**
     * The constant value indicating that the state of a call peer is
     * remotely put on hold.
     */
    public static final CallPeerState ON_HOLD_REMOTELY
                                = new CallPeerState(_ON_HOLD_REMOTELY);

    /**
     * Determines whether a specific <tt>CallPeerState</tt> value
     * signal a call hold regardless of the issuer (which may be local and/or
     * remote).
     *
     * @param state
     *            the <tt>CallPeerState</tt> value to be checked
     *            whether it signals a call hold
     * @return <tt>true</tt> if the specified <tt>state</tt> signals a call
     *         hold; <tt>false</tt>, otherwise
     */
    public static final boolean isOnHold(CallPeerState state)
    {
        return CallPeerState.ON_HOLD_LOCALLY.equals(state)
                || CallPeerState.ON_HOLD_MUTUALLY.equals(state)
                || CallPeerState.ON_HOLD_REMOTELY.equals(state);
    }

    /**
     * A string representation of this peer's Call State. Could be
     * _CONNECTED, _FAILED, _CALLING and etc.
     */
    private String callStateStr;

    /**
     * Create a peer call state object with a value corresponding to the
     * specified string.
     * @param callPeerState a string representation of the state.
     */
    private CallPeerState(String callPeerState)
    {
        this.callStateStr = callPeerState;
    }

    /**
     * Returns a String representation of tha CallPeerState.
     *
     * @return A string value (one of the _BUSY, _CALLING, _CONNECTED,
     * _CONNECTING, _DISCONNECTED, _FAILED, _RINGING constants) representing
     * this call peer state).
     */
    public String getStateString()
    {
        return callStateStr;
    }

    /**
     * Returns a string representation of this call state. Strings returned
     * by this method have the following format:
     * CallPeerState:<STATE_STRING>
     * and are meant to be used for logging/debugging purposes.
     * @return a string representation of this object.
     */
    public String toString()
    {
        return getClass().getName()+":"+getStateString();
    }
}
