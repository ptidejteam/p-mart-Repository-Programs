/*
 * SIP Communicator, the OpenSource Java VoIP and Instant Messaging client.
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package net.java.sip.communicator.impl.gui.main.call;

import java.awt.event.*;
import java.text.*;
import java.util.*;

import javax.swing.Timer;

import org.osgi.framework.*;

import net.java.sip.communicator.impl.gui.*;
import net.java.sip.communicator.impl.gui.customcontrols.*;
import net.java.sip.communicator.impl.gui.utils.*;
import net.java.sip.communicator.service.protocol.*;
import net.java.sip.communicator.service.protocol.event.*;
import net.java.sip.communicator.util.*;

/**
 * The <tt>CallManager</tt> is the one that handles calls. It contains also
 * the "Call" and "Hang up" buttons panel. Here are handles incoming and
 * outgoing calls from and to the call operation set.
 *
 * @author Yana Stamcheva
 */
public class CallManager
{
    private static final Logger logger = Logger.getLogger(CallManager.class);

    /**
     * A table mapping protocol <tt>Call</tt> objects to the GUI dialogs
     * that are currently used to display them.
     */
    private static Hashtable<Call, CallDialog> activeCalls
                                            = new Hashtable<Call, CallDialog>();

    /**
     * A call listener.
     */
    public static class GuiCallListener implements CallListener
    {
        /**
         * Implements CallListener.incomingCallReceived. When a call is received
         * creates a <tt>ReceivedCallDialog</tt> and plays the
         * ring phone sound to the user.
         * @param event the <tt>CallEvent</tt>
         */
        public void incomingCallReceived(CallEvent event)
        {
            Call sourceCall = event.getSourceCall();

            ReceivedCallDialog receivedCallDialog
                = new ReceivedCallDialog(sourceCall);

            receivedCallDialog.pack();
            receivedCallDialog.setVisible(true);

            // FIXME: I18N
            NotificationManager.fireNotification(
                NotificationManager.INCOMING_CALL,
                "",
                "Incoming call received from: "
                    + sourceCall.getCallPeers().next());
        }

        /**
         * Implements CallListener.callEnded. Stops sounds that are playing at
         * the moment if there're any. Removes the call panel and disables the
         * hang up button.
         * @param event the <tt>CallEvent</tt>
         */
        public void callEnded(CallEvent event)
        {
            Call sourceCall = event.getSourceCall();

            // Stop all telephony related sounds.
            stopAllSounds();

            if (activeCalls.get(sourceCall) != null)
            {
                CallDialog callDialog = activeCalls.get(sourceCall);

                disposeCallDialogWait(callDialog);
            }
        }

        /**
         * Creates and opens a call dialog. Implements
         * CallListener.outGoingCallCreated.
         * @param event the <tt>CallEvent</tt>
         */
        public void outgoingCallCreated(CallEvent event)
        {
            Call sourceCall = event.getSourceCall();

            CallDialog callDialog
                = CallManager.openCallDialog(sourceCall);

            activeCalls.put(sourceCall, callDialog);
        }
    }

    /**
     * Removes the given call panel tab.
     *
     * @param callDialog the CallDialog to remove
     */
    public static void disposeCallDialogWait(CallDialog callDialog)
    {
        Timer timer
            = new Timer(5000, new DisposeCallDialogListener(callDialog));

        timer.setRepeats(false);
        timer.start();
    }

    /**
     * Removes the given CallPanel from the main tabbed pane.
     */
    private static class DisposeCallDialogListener
        implements ActionListener
    {
        private final CallDialog callDialog;

        public DisposeCallDialogListener(CallDialog callDialog)
        {
            this.callDialog = callDialog;
        }

        public void actionPerformed(ActionEvent e)
        {
            callDialog.dispose();

            Call call = callDialog.getCall();

            if(call != null)
                activeCalls.remove(call);
        }
    }

    /**
     * Answers the given call.
     *
     * @param call the call to answer
     */
    public static void answerCall(final Call call)
    {
        CallManager.openCallDialog(call);

        new AnswerCallThread(call).start();
    }

    /**
     * Hang ups the given call.
     *
     * @param call the call to hang up
     */
    public static void hangupCall(final Call call)
    {
        new HangupCallThread(call).start();
    }

    /**
     * Hang ups the given <tt>callPeer</tt>.
     *
     * @param callPeer the <tt>CallPeer</tt> to hang up
     */
    public static void hangupCallPeer(final CallPeer callPeer)
    {
        stopAllSounds();

        new HangupCallPeerThread(callPeer).start();
    }

    /**
     * Creates a call to the contact represented by the given string.
     *
     * @param protocolProvider the protocol provider to which this call belongs.
     * @param contact the contact to call to
     */
    public static void createCall(  ProtocolProviderService protocolProvider,
                                    String contact)
    {
        new CreateCallThread(protocolProvider, contact).start();
    }

    /**
     * Creates a call to the contact represented by the given string.
     *
     * @param protocolProvider the protocol provider to which this call belongs.
     * @param contact the contact to call to
     */
    public static void createCall(  ProtocolProviderService protocolProvider,
                                    Contact contact)
    {
        new CreateCallThread(protocolProvider, contact).start();
    }

    /**
     * Creates a call to the contact represented by the given string through the
     * default (most connected) protocol provider. If none of the providers is
     * registered or online does nothing.
     *
     * @param contact the contact to call to
     */
    public static void createCall(String contact)
    {
        ProtocolProviderService telProvider = null;
        int status = 0;

        Vector<ProtocolProviderService> telProviders = getTelephonyProviders();

        for (ProtocolProviderService provider : telProviders)
        {
            if (!provider.isRegistered())
                continue;

            OperationSetPresence presence
                = provider.getOperationSet(OperationSetPresence.class);

            int presenceStatus
                = (presence == null)
                    ? PresenceStatus.AVAILABLE_THRESHOLD
                    : presence.getPresenceStatus().getStatus();

            if (status < presenceStatus)
            {
                status = presenceStatus;
                telProvider = provider;
            }
        }

        if (status >= PresenceStatus.ONLINE_THRESHOLD)
            new CreateCallThread(telProvider, contact).start();
        else
        {
            logger.error("There's no online telephony"
                        + " provider to create this call.");

            new ErrorDialog(
                    null,
                    GuiActivator.getResources()
                        .getI18NString("service.gui.WARNING"),
                    GuiActivator.getResources()
                        .getI18NString(
                            "service.gui.NO_ONLINE_TELEPHONY_ACCOUNT"),
                    ErrorDialog.WARNING)
                .showDialog();
        }
    }

    /**
     * Creates a call to the given list of contacts.
     *
     * @param protocolProvider the protocol provider to which this call belongs.
     * @param callees the list of contacts to call to
     */
    public static void createConferenceCall(
        String[] callees,
        ProtocolProviderService protocolProvider)
    {
        new CreateConferenceCallThread(callees, protocolProvider).start();
    }

    /**
     * Invites the given list of <tt>callees</tt> to the given conference
     * <tt>call</tt>.
     *
     * @param callees the list of contacts to invite
     * @param call the protocol provider to which this call belongs
     */
    public static void inviteToConferenceCall(  String[] callees,
                                                Call call)
    {
        new InviteToConferenceCallThread(callees, call).start();
    }

    /**
     * Puts on or off hold the given <tt>callPeer</tt>.
     * @param callPeer the peer to put on/off hold
     * @param isOnHold indicates the action (on hold or off hold)
     */
    public static void putOnHold(CallPeer callPeer, boolean isOnHold)
    {
        new PutOnHoldCallPeerThread(callPeer, isOnHold).start();
    }

    /**
     * Mutes the given <tt>callPeer</tt>.
     * @param callPeer the peer to mute
     * @param isMute indicates the action (disable or enable mute)
     */
    public static void mute(CallPeer callPeer, boolean isMute)
    {
        new MuteCallPeerThread(callPeer, isMute).start();
    }

    /**
     * Transfers the given <tt>callPeer</tt>.
     * @param callPeer the <tt>CallPeer</tt> to transfer
     */
    public static void transferCall(CallPeer callPeer)
    {
        final Call call = callPeer.getCall();

        if (call != null)
        {
            OperationSetAdvancedTelephony telephony
                = call.getProtocolProvider()
                    .getOperationSet(OperationSetAdvancedTelephony.class);

            if (telephony != null)
            {
                final TransferCallDialog dialog = new TransferCallDialog(null);

                /*
                 * Transferring a call works only when the call is in progress
                 * so close the dialog (if it's not already closed, of course)
                 * once the dialog ends.
                 */
                CallChangeListener callChangeListener = new CallChangeAdapter()
                {
                    /*
                     * Implements
                     * CallChangeAdapter#callStateChanged(CallChangeEvent).
                     */
                    public void callStateChanged(CallChangeEvent evt)
                    {
                        // we are interested only in CALL_STATE_CHANGEs
                        if(!evt.getEventType().equals(
                                CallChangeEvent.CALL_STATE_CHANGE))
                            return;

                        if (!CallState.CALL_IN_PROGRESS.equals(call
                            .getCallState()))
                        {
                            dialog.setVisible(false);
                            dialog.dispose();
                        }
                    }
                };
                call.addCallChangeListener(callChangeListener);
                try
                {
                    dialog.setModal(true);
                    dialog.pack();
                    dialog.setVisible(true);
                }
                finally
                {
                    call.removeCallChangeListener(callChangeListener);
                }

                String target = dialog.getTarget();
                if ((target != null) && (target.length() > 0))
                {
                    try
                    {
                        CallPeer targetPeer = findCallPeer(target);

                        if (targetPeer == null)
                            telephony.transfer(callPeer, target);
                        else
                            telephony.transfer(callPeer, targetPeer);
                    }
                    catch (OperationFailedException ex)
                    {
                        logger.error("Failed to transfer call " + call + " to "
                            + target, ex);
                    }
                }
            }
        }
    }

    /**
     * Opens a call dialog.
     *
     * @param call the call object to pass to the call dialog
     * @return the opened call dialog
     */
    public static CallDialog openCallDialog(Call call)
    {
        CallDialog callDialog = new CallDialog(call);

        callDialog.setVisible(true, true);

        return callDialog;
    }

    /**
     * Returns a list of all currently registered telephony providers.
     * @return a list of all currently registered telephony providers
     */
    public static Vector<ProtocolProviderService> getTelephonyProviders()
    {
        Vector<ProtocolProviderService> telephonyProviders
            = new Vector<ProtocolProviderService>();

        for (ProtocolProviderFactory providerFactory : GuiActivator
            .getProtocolProviderFactories().values())
        {
            ServiceReference serRef;
            ProtocolProviderService protocolProvider;

            for (AccountID accountID : providerFactory.getRegisteredAccounts())
            {
                serRef = providerFactory.getProviderForAccount(accountID);

                protocolProvider
                    = (ProtocolProviderService) GuiActivator.bundleContext
                        .getService(serRef);

                if (protocolProvider.getOperationSet(
                        OperationSetBasicTelephony.class) != null
                    && protocolProvider.isRegistered())
                {
                    telephonyProviders.add(protocolProvider);
                }
            }
        }
        return telephonyProviders;
    }

    /**
     * Creates a call from a given Contact or a given String.
     */
    private static class CreateCallThread
        extends Thread
    {
        private final String stringContact;

        private final Contact contact;

        private final ProtocolProviderService protocolProvider;

        public CreateCallThread(ProtocolProviderService protocolProvider,
                                String contact)
        {
            this.protocolProvider = protocolProvider;
            this.stringContact = contact;
            this.contact = null;
        }

        public CreateCallThread(ProtocolProviderService protocolProvider,
                                Contact contact)
        {
            this.protocolProvider = protocolProvider;
            this.contact = contact;
            this.stringContact = null;
        }

        public void run()
        {
            OperationSetBasicTelephony telephonyOpSet
                = protocolProvider
                    .getOperationSet(OperationSetBasicTelephony.class);

            /*
             * XXX If we are here and we just discover that
             * OperationSetBasicTelephony is not supported, then we're already
             * in trouble. At the very least, we've already started a whole new
             * thread just to check that a reference is null.
             */
            if (telephonyOpSet == null)
                return;

            Throwable exception = null;

            try
            {
                if (contact != null)
                    telephonyOpSet.createCall(contact);
                else if (stringContact != null)
                    telephonyOpSet.createCall(stringContact);
            }
            catch (OperationFailedException e)
            {
                exception = e;
            }
            catch (ParseException e)
            {
                exception = e;
            }
            if (exception != null)
            {
                logger.error("The call could not be created: " + exception);

                new ErrorDialog(
                        null,
                        GuiActivator.getResources()
                            .getI18NString("service.gui.ERROR"),
                        exception.getMessage(),
                        ErrorDialog.ERROR)
                    .showDialog();
            }
        }
    }

    /**
     * Answers all call peers in the given call.
     */
    private static class AnswerCallThread
        extends Thread
    {
        private final Call call;

        public AnswerCallThread(Call call)
        {
            this.call = call;
        }

        public void run()
        {
            ProtocolProviderService pps = call.getProtocolProvider();
            Iterator<? extends CallPeer> peers = call.getCallPeers();

            while (peers.hasNext())
            {
                CallPeer peer = peers.next();
                OperationSetBasicTelephony telephony =
                    pps.getOperationSet(OperationSetBasicTelephony.class);

                try
                {
                    telephony.answerCallPeer(peer);
                }
                catch (OperationFailedException e)
                {
                    logger.error("Could not answer to : " + peer
                        + " caused by the following exception: " + e);
                }
            }
        }
    }

    /**
     * Creates a conference call from a given list of contact addresses
     */
    private static class CreateConferenceCallThread
        extends Thread
    {
        private final String[] callees;

        private final ProtocolProviderService protocolProvider;

        public CreateConferenceCallThread(
                String[] callees,
                ProtocolProviderService protocolProvider)
        {
            this.callees = callees;
            this.protocolProvider = protocolProvider;
        }

        @Override
        public void run()
        {
            OperationSetTelephonyConferencing confOpSet
                = protocolProvider.getOperationSet(
                    OperationSetTelephonyConferencing.class);

            /*
             * XXX If we are here and we just discover that
             * OperationSetTelephonyConferencing is not supported, then we're
             * already in trouble. At the very least, we've already started a
             * whole new thread just to check that a reference is null.
             */
            if (confOpSet == null)
                return;

            Throwable exception = null;

            try
            {
                confOpSet.createConfCall(callees);
            }
            catch (OperationFailedException ofe)
            {
                exception = ofe;
            }
            catch (OperationNotSupportedException onse)
            {
                exception = onse;
            }
            catch (IllegalArgumentException iae)
            {
                exception = iae;
            }
            if (exception != null)
            {
                logger.error("Failed to create conference call. " + exception);

                new ErrorDialog(
                        null,
                        GuiActivator
                            .getResources().getI18NString("service.gui.ERROR"),
                        exception.getMessage(),
                        ErrorDialog.ERROR)
                    .showDialog();
            }
        }
    }

    /**
     * Invites a list of callees to a conference call.
     */
    private static class InviteToConferenceCallThread
        extends Thread
    {
        private final String[] callees;

        private final Call call;

        public InviteToConferenceCallThread(String[] callees, Call call)
        {
            this.callees = callees;
            this.call = call;
        }

        @Override
        public void run()
        {
            OperationSetTelephonyConferencing confOpSet
                = call.getProtocolProvider()
                    .getOperationSet(
                            OperationSetTelephonyConferencing.class);

            /*
             * XXX If we are here and we just discover that
             * OperationSetTelephonyConferencing is not supported, then we're
             * already in trouble. At the very least, we've already started a
             * whole new thread just to check that a reference is null.
             */
            if (confOpSet == null)
                return;

            for (String callee : callees)
            {
                Throwable exception = null;

                try
                {
                    confOpSet.inviteCalleeToCall(callee, call);
                }
                catch (OperationFailedException ofe)
                {
                    exception = ofe;
                }
                catch (OperationNotSupportedException onse)
                {
                    exception = onse;
                }
                catch (IllegalArgumentException iae)
                {
                    exception = iae;
                }
                if (exception != null)
                {
                    logger
                        .error("Failed to invite callee: " + callee, exception);

                    new ErrorDialog(
                            null,
                            GuiActivator
                                .getResources()
                                    .getI18NString("service.gui.ERROR"),
                            exception.getMessage(),
                            ErrorDialog.ERROR)
                        .showDialog();
                }
            }
        }
    }

    /**
     * Hang-ups all call peers in the given call.
     */
    private static class HangupCallThread
        extends Thread
    {
        private final Call call;

        public HangupCallThread(Call call)
        {
            this.call = call;
        }

        public void run()
        {
            ProtocolProviderService pps = call.getProtocolProvider();
            Iterator<? extends CallPeer> peers = call.getCallPeers();

            while (peers.hasNext())
            {
                CallPeer peer = peers.next();
                OperationSetBasicTelephony telephony
                    = pps.getOperationSet(OperationSetBasicTelephony.class);

                try
                {
                    telephony.hangupCallPeer(peer);
                }
                catch (OperationFailedException e)
                {
                    logger.error("Could not hang up : " + peer
                        + " caused by the following exception: " + e);
                }
            }
        }
    }

    /**
     * Hang-ups the given <tt>CallPeer</tt>.
     */
    private static class HangupCallPeerThread
        extends Thread
    {
        private final CallPeer callPeer;

        public HangupCallPeerThread(CallPeer callPeer)
        {
            this.callPeer = callPeer;
        }

        public void run()
        {
            ProtocolProviderService pps = callPeer.getProtocolProvider();

            OperationSetBasicTelephony telephony =
                pps.getOperationSet(OperationSetBasicTelephony.class);

            try
            {
                telephony.hangupCallPeer(callPeer);
            }
            catch (OperationFailedException e)
            {
                logger.error("Could not hang up : " + callPeer
                    + " caused by the following exception: " + e);
            }
        }
    }

    /**
     * Puts on hold the given <tt>CallPeer</tt>.
     */
    private static class PutOnHoldCallPeerThread
        extends Thread
    {
        private final CallPeer callPeer;

        private final boolean isOnHold;

        public PutOnHoldCallPeerThread(CallPeer callPeer, boolean isOnHold)
        {
            this.callPeer = callPeer;
            this.isOnHold = isOnHold;
        }

        public void run()
        {
            OperationSetBasicTelephony telephony =
                callPeer.getProtocolProvider()
                    .getOperationSet(OperationSetBasicTelephony.class);

            try
            {
                if (isOnHold)
                    telephony.putOnHold(callPeer);
                else
                    telephony.putOffHold(callPeer);
            }
            catch (OperationFailedException ex)
            {
                String callPeerAddress = callPeer.getAddress();

                if (isOnHold)
                    logger.error("Failed to put"
                        + callPeerAddress + " on hold.", ex);
                else
                    logger.error("Failed to put"
                        + callPeerAddress + " off hold.", ex);
            }
        }
    }

    /**
     * Mutes the given <tt>CallPeer</tt>.
     */
    private static class MuteCallPeerThread
        extends Thread
    {
        private final CallPeer callPeer;

        private final boolean isMute;

        public MuteCallPeerThread(CallPeer callPeer, boolean isMute)
        {
            this.callPeer = callPeer;
            this.isMute = isMute;
        }

        public void run()
        {
            OperationSetBasicTelephony telephony =
                callPeer.getProtocolProvider()
                    .getOperationSet(OperationSetBasicTelephony.class);

            telephony.setMute(callPeer, isMute);
        }
    }

    /**
     * Stops all telephony related sounds.
     */
    private static void stopAllSounds()
    {
        NotificationManager.stopSound(NotificationManager.DIALING);
        NotificationManager.stopSound(NotificationManager.BUSY_CALL);
        NotificationManager.stopSound(NotificationManager.INCOMING_CALL);
        NotificationManager.stopSound(NotificationManager.OUTGOING_CALL);
    }

    /**
     * Returns the first <tt>CallPeer</tt> among all existing ones
     * who has a specific address.
     *
     * @param address the address of the <tt>CallPeer</tt> to be located
     * @return the first <tt>CallPeer</tt> among all existing ones
     * who has the specified <tt>address</tt>
     *
     * @throws OperationFailedException in case we fail retrieving a reference
     * to <tt>ProtocolProviderService</tt>s
     */
    private static CallPeer findCallPeer(String address)
        throws OperationFailedException
    {
        BundleContext bundleContext = GuiActivator.bundleContext;
        ServiceReference[] serviceReferences;

        try
        {
            serviceReferences =
                bundleContext.getServiceReferences(
                    ProtocolProviderService.class.getName(), null);
        }
        catch (InvalidSyntaxException ex)
        {
            throw new OperationFailedException(
                "Failed to retrieve ProtocolProviderService references.",
                OperationFailedException.INTERNAL_ERROR, ex);
        }

        Class<OperationSetBasicTelephony> telephonyClass
            = OperationSetBasicTelephony.class;
        CallPeer peer = null;

        for (ServiceReference serviceReference : serviceReferences)
        {
            ProtocolProviderService service = (ProtocolProviderService)
                bundleContext.getService(serviceReference);
            OperationSetBasicTelephony telephony =
                service.getOperationSet(telephonyClass);

            if (telephony != null)
            {
                peer = findCallPeer(telephony, address);
                if (peer != null)
                    break;
            }
        }
        return peer;
    }

    /**
     * Returns the first <tt>CallPeer</tt> known to a specific
     * <tt>OperationSetBasicTelephony</tt> to have a specific address.
     *
     * @param telephony the <tt>OperationSetBasicTelephony</tt> to have its
     * <tt>CallPeer</tt>s examined in search for one which has a specific
     * address
     * @param address the address to locate the associated <tt>CallPeer</tt> of
     * @return the first <tt>CallPeer</tt> known to the specified
     * <tt>OperationSetBasicTelephony</tt> to have the specified address
     */
    private static CallPeer findCallPeer(
        OperationSetBasicTelephony telephony, String address)
    {
        for (Iterator<? extends Call> callIter = telephony.getActiveCalls();
                callIter.hasNext();)
        {
            Call call = callIter.next();

            for (Iterator<? extends CallPeer> peerIter = call.getCallPeers();
                    peerIter.hasNext();)
            {
                CallPeer peer = peerIter.next();

                if (address.equals(peer.getAddress()))
                    return peer;
            }
        }
        return null;
    }
}
