/*
 * SIP Communicator, the OpenSource Java VoIP and Instant Messaging client.
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package net.java.sip.communicator.impl.protocol.sip;

import java.io.*;
import java.text.*;
import java.util.*;

import javax.sip.*;
import javax.sip.address.*;
import javax.sip.header.*;
import javax.sip.message.*;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;

import org.w3c.dom.*;

import net.java.sip.communicator.service.protocol.*;
import net.java.sip.communicator.service.protocol.event.*;
import net.java.sip.communicator.util.*;
import net.java.sip.communicator.util.xml.*;

/**
 * Sip presence implementation (SIMPLE).
 *
 * Compliant with rfc3261, rfc3265, rfc3856, rfc3863, rfc4480 and rfc3903
 *
 * @author Benoit Pradelle
 * @author Lubomir Marinov
 * @author Emil Ivov
 */
public class OperationSetPresenceSipImpl
    extends AbstractOperationSetPersistentPresence<ProtocolProviderServiceSipImpl>
    implements MethodProcessor,
               RegistrationStateChangeListener
{
    /**
     * Our class logger.
     */
    private static final Logger logger
        = Logger.getLogger(OperationSetPresenceSipImpl.class);

    /**
     * The root of the SIP contact list.
     */
    private final ContactGroupSipImpl contactListRoot;

    /**
     * The currently active status message.
     */
    private String statusMessage = "Default Status Message";

    /**
     * Our default presence status.
     */
    private PresenceStatus presenceStatus;

    /**
     * List of all the CallIds to wait before unregister
     * Content : String
     */
    private final List<String> waitedCallIds = new Vector<String>();

    /**
     * Do we have to use a distant presence agent (default initial value)
     */
    private boolean useDistantPA;

    /**
     * Entity tag associated with the current communication with the distant PA
     */
    private String distantPAET = null;

    /**
     * The default expiration value of a request as defined for the presence
     * package in rfc3856. This is the value used when there is no Expires
     * header in the received subscription requests.
     */
    private static final int PRESENCE_DEFAULT_EXPIRE = 3600;

    /**
     * How many seconds before a timeout should we refresh our state
     */
    private static final int REFRESH_MARGIN = 60;

    /**
     * User chosen expiration value of any of our subscriptions.
     * Currently, the value is the default value defined in the rfc.
     */
    private final int subscriptionDuration;

    /**
     * The current CSeq used in the PUBLISH requests
     */
    private static long publish_cseq = 1;

    /**
     * The timer which will handle all the scheduled tasks
     */
    private final TimerScheduler timer = new TimerScheduler();

    /**
     * The re-PUBLISH task if any
     */
    private RePublishTask republishTask = null;

    /**
     * The interval between two execution of the polling task (in ms.)
     */
    private final int pollingTaskPeriod;

    /**
     * The task in charge of polling offline contacts
     */
    private PollOfflineContactsTask pollingTask = null;

    /**
     * If we should be totally silenced, just doing local operations
     */
    private final boolean presenceEnabled;

    /**
     * The document builder factory for generating document builders
     */
    private DocumentBuilderFactory docBuilderFactory = null;

    /**
     * The document builder which produce xml documents
     */
    private DocumentBuilder docBuilder = null;

    /**
     * The transformer factory used to create transformer
     */
    private TransformerFactory transFactory = null;

    /**
     * The transformer used to convert XML documents
     */
    private Transformer transformer = null;

    private final SipStatusEnum sipStatusEnum;

    /**
     * The id used in <tt><tuple></tt>  and <tt><person></tt> elements
     * of pidf documents.
     */
    private static final String TUPLE_ID = "t" + (long)(Math.random() * 10000);
    private static final String PERSON_ID = "p" + (long)(Math.random() * 10000);

    // XML documents types
    private static final String PIDF_XML        = "pidf+xml";

    // pidf elements and attributes
    private static final String PRESENCE_ELEMENT= "presence";
    private static final String NS_ELEMENT      = "xmlns";
    private static final String NS_VALUE        = "urn:ietf:params:xml:ns:pidf";
    private static final String ENTITY_ATTRIBUTE= "entity";
    private static final String TUPLE_ELEMENT   = "tuple";
    private static final String ID_ATTRIBUTE    = "id";
    private static final String STATUS_ELEMENT  = "status";
    private static final String ONLINE_STATUS   = "open";
    private static final String OFFLINE_STATUS  = "closed";
    private static final String BASIC_ELEMENT   = "basic";
    private static final String CONTACT_ELEMENT = "contact";
    private static final String NOTE_ELEMENT    = "note";
    private static final String PRIORITY_ATTRIBUTE  = "priority";

    // rpid elements and attributes
    private static final String RPID_NS_ELEMENT = "xmlns:rpid";
    private static final String RPID_NS_VALUE   =
                                            "urn:ietf:params:xml:ns:pidf:rpid";
    private static final String DM_NS_ELEMENT   = "xmlns:dm";
    private static final String DM_NS_VALUE     =
                                    "urn:ietf:params:xml:ns:pidf:data-model";
    private static final String PERSON_ELEMENT  = "person";
    private static final String NS_PERSON_ELT   = "dm:person";
    private static final String ACTIVITY_ELEMENT= "activities";
    private static final String NS_ACTIVITY_ELT = "rpid:activities";
    private static final String AWAY_ELEMENT    = "away";
    private static final String NS_AWAY_ELT     = "rpid:away";
    private static final String BUSY_ELEMENT    = "busy";
    private static final String NS_BUSY_ELT     = "rpid:busy";
    private static final String OTP_ELEMENT     = "on-the-phone";
    private static final String NS_OTP_ELT      = "rpid:on-the-phone";

    // namespace wildcard
    private static final String ANY_NS          = "*";

    /**
     * The <code>EventPackageNotifier</code> which provides the ability of this
     * instance to act as a notifier for the presence event package.
     */
    private final EventPackageNotifier notifier;

    /**
     * The <code>EventPackageSubscriber</code> which provides the ability of
     * this instance to act as a subscriber for the presence event package.
     */
    private final EventPackageSubscriber subscriber;

    /**
     * Creates an instance of this operation set keeping a reference to the
     * specified parent <tt>provider</tt>.
     * @param provider the ProtocolProviderServiceSipImpl instance that
     * created us.
     * @param presenceEnabled if we are activated or if we don't have to
     * handle the presence informations for contacts
     * @param forceP2PMode if we should start in the p2p mode directly
     * @param pollingPeriod the period between two poll for offline contacts
     * @param subscriptionExpiration the default subscription expiration value
     * to use
     */
    public OperationSetPresenceSipImpl(
        ProtocolProviderServiceSipImpl provider,
        boolean presenceEnabled,
        boolean forceP2PMode,
        int pollingPeriod,
        int subscriptionExpiration)
    {
        super(provider);

        this.contactListRoot = new ContactGroupSipImpl("RootGroup", provider);

        //add our registration listener
        this.parentProvider.addRegistrationStateChangeListener(this);

        this.presenceEnabled = presenceEnabled;

        this.subscriptionDuration
            = (subscriptionExpiration > 0)
                ? subscriptionExpiration
                : PRESENCE_DEFAULT_EXPIRE;

        if (this.presenceEnabled)
        {
            // Subscriber part of the presence event package
            this.subscriber
                = new EventPackageSubscriber(
                        this.parentProvider,
                        "presence",
                        this.subscriptionDuration,
                        PIDF_XML,
                        this.timer,
                        REFRESH_MARGIN);
            this.notifier
                = new EventPackageNotifier(this.parentProvider, "presence",
                        PRESENCE_DEFAULT_EXPIRE, PIDF_XML, this.timer)
                {
                    /**
                     * Creates a new <tt>PresenceNotificationSubscription</tt>
                     * instance.
                     * @param fromAddress our AOR
                     * @param eventId the event id to use.
                     */
                    protected Subscription createSubscription(
                                Address fromAddress, String eventId)
                    {
                        return new PresenceNotifierSubscription(
                                    fromAddress, eventId);
                    }
                };
        }
        else
        {
            this.subscriber = null;
            this.notifier = null;
        }

        // Notifier part of the presence event package and PUBLISH
        this.parentProvider.registerMethodProcessor(Request.SUBSCRIBE, this);
        this.parentProvider.registerMethodProcessor(Request.NOTIFY, this);
        this.parentProvider.registerMethodProcessor(Request.PUBLISH, this);
        this.parentProvider.registerEvent("presence");

        logger.debug(
            "presence initialized with :"
                + presenceEnabled + ", "
                + forceP2PMode + ", "
                + pollingPeriod + ", "
                + subscriptionExpiration
                + " for " + this.parentProvider.getOurDisplayName());

        // retrieve the options for this account
        this.pollingTaskPeriod
            = (pollingPeriod > 0) ? (pollingPeriod * 1000) : 30000;

        // if we force the p2p mode, we start by not using a distant PA
        this.useDistantPA = !forceP2PMode;

        this.sipStatusEnum = parentProvider.getSipStatusEnum();
        this.presenceStatus = sipStatusEnum.getStatus(SipStatusEnum.OFFLINE);
    }

    /**
     * Returns a PresenceStatus instance representing the state this provider is
     * currently in. Note that PresenceStatus instances returned by this method
     * MUST adequately represent all possible states that a provider might
     * enter during its lifecycle, including those that would not be visible
     * to others (e.g. Initializing, Connecting, etc ..) and those that will be
     * sent to contacts/buddies (On-Line, Eager to chat, etc.).
     *
     * @return the PresenceStatus last published by this provider.
     */
    public PresenceStatus getPresenceStatus()
    {
        return this.presenceStatus;
    }

    /**
     * Sets if we should use a distant presence agent.
     *
     * @param useDistantPA
     *            <tt>true</tt> if we should use a distant presence agent
     */
    private void setUseDistantPA(boolean useDistantPA)
    {
        this.useDistantPA = useDistantPA;

        if (!this.useDistantPA && (this.republishTask != null))
        {
            this.republishTask.cancel();
            this.republishTask = null;
        }
    }

    /**
     * Returns the root group of the server stored contact list.
     *
     * @return the root ContactGroup for the ContactList stored by this
     *   service.
     */
    public ContactGroup getServerStoredContactListRoot()
    {
        return this.contactListRoot;
    }

    /**
     * Creates a group with the specified name and parent in the server
     * stored contact list.
     *
     * @param parent the group where the new group should be created
     * @param groupName the name of the new group to create.
     */
    public void createServerStoredContactGroup(ContactGroup parent,
                                               String groupName)
    {
        ContactGroupSipImpl newGroup = new ContactGroupSipImpl(groupName,
                this.parentProvider);

        ((ContactGroupSipImpl) parent).addSubgroup(newGroup);

        this.fireServerStoredGroupEvent(newGroup,
                ServerStoredGroupEvent.GROUP_CREATED_EVENT);
    }

    /**
     * Creates and returns a unresolved contact group from the specified
     * <tt>address</tt> and <tt>persistentData</tt>. The method will not try
     * to establish a network connection and resolve the newly created
     * <tt>ContactGroup</tt> against the server or the contact itself. The
     * protocol provider will later resolve the contact group. When this happens
     * the corresponding event would notify interested subscription listeners.
     *
     * @param groupUID an identifier, returned by ContactGroup's getGroupUID,
     * that the protocol provider may use in order to create the group.
     * @param persistentData a String returned ContactGroups's
     * getPersistentData() method during a previous run and that has been
     * persistently stored locally.
     * @param parentGroup the group under which the new group is to be created
     * or null if this is group directly underneath the root.
     * @return the unresolved <tt>ContactGroup</tt> created from the specified
     * <tt>uid</tt> and <tt>persistentData</tt>
     */
    public ContactGroup createUnresolvedContactGroup(String groupUID,
        String persistentData, ContactGroup parentGroup)
    {
        ContactGroupSipImpl newGroup = new ContactGroupSipImpl(
                ContactGroupSipImpl.createNameFromUID(groupUID),
                this.parentProvider);
        newGroup.setResolved(false);

        //if parent is null then we're adding under root.
        if(parentGroup == null)
        {
            parentGroup = getServerStoredContactListRoot();
        }

        ((ContactGroupSipImpl) parentGroup).addSubgroup(newGroup);

        this.fireServerStoredGroupEvent(
            newGroup, ServerStoredGroupEvent.GROUP_CREATED_EVENT);

        return newGroup;
    }

    /**
     * Renames the specified group from the server stored contact list.
     *
     * @param group the group to rename.
     * @param newName the new name of the group.
     */
    public void renameServerStoredContactGroup(ContactGroup group,
                                               String newName)
    {
        ((ContactGroupSipImpl) group).setGroupName(newName);

        this.fireServerStoredGroupEvent(
            (ContactGroupSipImpl) group,
            ServerStoredGroupEvent.GROUP_RENAMED_EVENT);
    }

    /**
     * Removes the specified contact from its current parent and places it
     * under <tt>newParent</tt>.
     *
     * @param contactToMove the <tt>Contact</tt> to move
     * @param newParent the <tt>ContactGroup</tt> where <tt>Contact</tt>
     *   would be placed.
     */
    public void moveContactToGroup(Contact contactToMove,
                                   ContactGroup newParent)
    {
        if (!(contactToMove instanceof ContactSipImpl))
            return;

        ContactSipImpl sipContact = (ContactSipImpl) contactToMove;
        ContactGroupSipImpl parentSipGroup
            = (ContactGroupSipImpl) sipContact.getParentContactGroup();

        parentSipGroup.removeContact(sipContact);

        ((ContactGroupSipImpl) newParent).addContact(sipContact);

        fireSubscriptionMovedEvent(contactToMove, parentSipGroup, newParent);
    }

    /**
     * Removes the specified group from the server stored contact list.
     *
     * @param group the group to remove.
     *
     * @throws IllegalArgumentException if <tt>group</tt> was not found in this
     * protocol's contact list.
     */
    public void removeServerStoredContactGroup(ContactGroup group)
        throws IllegalArgumentException
    {
        ContactGroupSipImpl sipGroup = (ContactGroupSipImpl) group;
        ContactGroupSipImpl parent = contactListRoot.findGroupParent(sipGroup);

        if(parent == null){
            throw new IllegalArgumentException(
                "group " + group
                + " does not seem to belong to this protocol's contact list.");
        }

        parent.removeSubGroup(sipGroup);

        fireServerStoredGroupEvent(
            sipGroup,
            ServerStoredGroupEvent.GROUP_REMOVED_EVENT);
    }

    /**
     * Requests the provider to enter into a status corresponding to the
     * specified parameters.
     *
     * @param status the PresenceStatus as returned by
     *   getRequestableStatusSet
     * @param statusMsg the message that should be set as the reason to
     *   enter that status
     * @throws IllegalArgumentException if the status requested is not a
     *   valid PresenceStatus supported by this provider.
     * @throws IllegalStateException if the provider is not currently
     *   registered.
     * @throws OperationFailedException with code NETWORK_FAILURE if
     *   publishing the status fails due to a network error.
     */
    public void publishPresenceStatus(
            PresenceStatus status,
            String statusMsg)
        throws IllegalArgumentException,
               IllegalStateException,
               OperationFailedException
    {
        PresenceStatus oldStatus = this.presenceStatus;
        this.presenceStatus = status;
        String oldMessage = this.statusMessage;
        this.statusMessage = statusMsg;

        if (this.presenceEnabled == false)
        {
            return;
        }

        // in the offline status, the protocol provider is already unregistered
        if (!status.equals(sipStatusEnum.getStatus(SipStatusEnum.OFFLINE)))
            assertConnected();

        // now inform our distant presence agent if we have one
        if (this.useDistantPA)
        {
            Request req = null;
            if (status.equals(sipStatusEnum.getStatus(SipStatusEnum.OFFLINE)))
            {
                // unpublish our state
                req = createPublish(0, false);

                // remember the callid to be sure that the publish arrived
                // before unregister
                synchronized (this.waitedCallIds)
                {
                    this.waitedCallIds.add(((CallIdHeader)
                        req.getHeader(CallIdHeader.NAME)).getCallId());
                }
            }
            else
            {
                req = createPublish(this.subscriptionDuration, true);
            }

            ClientTransaction transac = null;
            try
            {
                transac = this.parentProvider
                    .getDefaultJainSipProvider().getNewClientTransaction(req);
            }
            catch (TransactionUnavailableException e)
            {
                logger.error("can't create the client transaction", e);
                throw new OperationFailedException(
                        "can't create the client transaction",
                        OperationFailedException.NETWORK_FAILURE);
            }

            try
            {
                transac.sendRequest();
            }
            catch (SipException e)
            {
                logger.error("can't send the PUBLISH request", e);
                throw new OperationFailedException(
                        "can't send the PUBLISH request",
                        OperationFailedException.NETWORK_FAILURE);
            }
        }
        // no distant presence agent, send notify to everyone
        else
        {
            String subscriptionState;
            String reason;

            if (status.equals(sipStatusEnum.getStatus(SipStatusEnum.OFFLINE)))
            {
                subscriptionState = SubscriptionStateHeader.TERMINATED;
                reason = SubscriptionStateHeader.PROBATION;
            }
            else
            {
                subscriptionState = SubscriptionStateHeader.ACTIVE;
                reason = null;
            }
            notifier.notifyAll(subscriptionState, reason);
        }

        // must be done in last to avoid some problem when terminating a
        // subscription of a contact who is also one of our watchers
        if (status.equals(sipStatusEnum.getStatus(SipStatusEnum.OFFLINE)))
            unsubscribeToAllContact();

        // inform the listeners of these changes
        this.fireProviderStatusChangeEvent(oldStatus);
        this.fireProviderMsgStatusChangeEvent(oldMessage);
    }

    /**
     * Notifies all registered listeners of the new event.
     *
     * @param oldValue the presence status we were in before the change.
     */
    public void fireProviderMsgStatusChangeEvent(String oldValue)
    {
        fireProviderStatusMessageChangeEvent(oldValue, this.statusMessage);
    }

    /**
     * Create a valid PUBLISH request corresponding to the current presence
     * state. The request is forged to be send to the current distant presence
     * agent.
     *
     * @param expires the expires value to send
     * @param insertPresDoc if a presence document has to be added (typically
     * = false when refreshing a publication)
     *
     * @return a valid <tt>Request</tt> containing the PUBLISH
     *
     * @throws OperationFailedException if something goes wrong
     */
    private Request createPublish(int expires, boolean insertPresDoc)
        throws OperationFailedException
    {
        // Call ID
        CallIdHeader callIdHeader = this.parentProvider
            .getDefaultJainSipProvider().getNewCallId();

        // FromHeader and ToHeader
        String localTag = SipMessageFactory.generateLocalTag();
        FromHeader fromHeader = null;
        ToHeader toHeader = null;
        try
        {
            //the publish method can only be used if we have a presence agent
            //so we deliberately use our AOR and do not use the
            //getOurSipAddress() method.
            Address ourAOR = parentProvider.getRegistrarConnection()
                                                    .getAddressOfRecord();
            //FromHeader
            fromHeader = this.parentProvider.getHeaderFactory()
                .createFromHeader(ourAOR,
                                  localTag);

            //ToHeader (it's ourselves)
            toHeader = this.parentProvider.getHeaderFactory()
                .createToHeader(ourAOR, null);
        }
        catch (ParseException ex)
        {
            //these two should never happen.
            logger.error(
                "An unexpected error occurred while"
                + "constructing the FromHeader or ToHeader", ex);
            throw new OperationFailedException(
                "An unexpected error occurred while"
                + "constructing the FromHeader or ToHeader",
                OperationFailedException.INTERNAL_ERROR,
                ex);
        }

        //ViaHeaders
        ArrayList<ViaHeader> viaHeaders = parentProvider.getLocalViaHeaders(
            toHeader.getAddress());

        //MaxForwards
        MaxForwardsHeader maxForwards = this.parentProvider
            .getMaxForwardsHeader();

        // Content params
        byte[] doc = null;

        if (insertPresDoc)
        {
            //this is a publish request so we would use the default
            //getLocalContact that would return a method based on the registrar
            //address
            doc
                = getPidfPresenceStatus(
                    getLocalContactForDst(toHeader.getAddress()));
        }
        else
        {
            doc = new byte[0];
        }

        ContentTypeHeader contTypeHeader;
        try
        {
            contTypeHeader = this.parentProvider.getHeaderFactory()
                .createContentTypeHeader("application",
                                         PIDF_XML);
        }
        catch (ParseException ex)
        {
            //these two should never happen.
            logger.error(
                "An unexpected error occurred while"
                + "constructing the content headers", ex);
            throw new OperationFailedException(
                "An unexpected error occurred while"
                + "constructing the content headers"
                , OperationFailedException.INTERNAL_ERROR
                , ex);
        }

        // eventually add the entity tag
        SIPIfMatchHeader ifmHeader = null;
        try
        {
            if (this.distantPAET != null)
            {
                ifmHeader = this.parentProvider.getHeaderFactory()
                    .createSIPIfMatchHeader(this.distantPAET);
            }
        }
        catch (ParseException e)
        {
            logger.error(
                "An unexpected error occurred while"
                + "constructing the SIPIfMatch header", e);
            throw new OperationFailedException(
                "An unexpected error occurred while"
                + "constructing the SIPIfMatch header",
                OperationFailedException.INTERNAL_ERROR,
                e);
        }

        //CSeq
        CSeqHeader cSeqHeader = null;
        try
        {
            cSeqHeader = this.parentProvider.getHeaderFactory()
                .createCSeqHeader(publish_cseq++, Request.PUBLISH);
        }
        catch (InvalidArgumentException ex)
        {
            //Shouldn't happen
            logger.error(
                "An unexpected error occurred while"
                + "constructing the CSeqHeader", ex);
            throw new OperationFailedException(
                "An unexpected error occurred while"
                + "constructing the CSeqHeader"
                , OperationFailedException.INTERNAL_ERROR
                , ex);
        }
        catch (ParseException ex)
        {
            //shouldn't happen
            logger.error(
                "An unexpected error occurred while"
                + "constructing the CSeqHeader", ex);
            throw new OperationFailedException(
                "An unexpected error occurred while"
                + "constructing the CSeqHeader"
                , OperationFailedException.INTERNAL_ERROR
                , ex);
        }

        // expires
        ExpiresHeader expHeader = null;
        try
        {
            expHeader = this.parentProvider.getHeaderFactory()
                .createExpiresHeader(expires);
        }
        catch (InvalidArgumentException e)
        {
            // will never happen
            logger.error(
                    "An unexpected error occurred while"
                    + "constructing the Expires header", e);
            throw new OperationFailedException(
                    "An unexpected error occurred while"
                    + "constructing the Expires header"
                    , OperationFailedException.INTERNAL_ERROR
                    , e);
        }

        // event
        EventHeader evtHeader = null;
        try
        {
            evtHeader = this.parentProvider.getHeaderFactory()
                .createEventHeader("presence");
        }
        catch (ParseException e)
        {
            // will never happen
            logger.error(
                    "An unexpected error occurred while"
                    + "constructing the Event header", e);
            throw new OperationFailedException(
                    "An unexpected error occurred while"
                    + "constructing the Event header"
                    , OperationFailedException.INTERNAL_ERROR
                    , e);
        }

        Request req = null;
        try
        {
            req = this.parentProvider.getMessageFactory().createRequest(
                toHeader.getAddress().getURI(), Request.PUBLISH, callIdHeader,
                cSeqHeader, fromHeader, toHeader, viaHeaders, maxForwards,
                contTypeHeader, doc);
        }
        catch (ParseException ex)
        {
            //shouldn't happen
            logger.error(
                "Failed to create message Request!", ex);
            throw new OperationFailedException(
                "Failed to create message Request!"
                , OperationFailedException.INTERNAL_ERROR
                , ex);
        }

        req.setHeader(expHeader);
        req.setHeader(evtHeader);

        if (ifmHeader != null)
        {
            req.setHeader(ifmHeader);
        }

        return req;
    }

    /**
     * Returns the set of PresenceStatus objects that a user of this service
     * may request the provider to enter. Note that the provider would most
     * probably enter more states than those returned by this method as they
     * only depict instances that users may request to enter. (e.g. a user
     * may not request a "Connecting..." state - it is a temporary state
     * that the provider enters while trying to enter the "Connected" state).
     *
     * @return Iterator a PresenceStatus array containing "enterable"
     * status instances.
     */
    public Iterator<PresenceStatus> getSupportedStatusSet()
    {
        return sipStatusEnum.getSupportedStatusSet();
    }

    /**
     * Get the PresenceStatus for a particular contact.
     *
     * @param contactIdentifier the identifier of the contact whose status
     *   we're interested in.
     * @return PresenceStatus the <tt>PresenceStatus</tt> of the specified
     *   <tt>contact</tt>
     * @throws IllegalArgumentException if <tt>contact</tt> is not a contact
     *   known to the underlying protocol provider
     * @throws IllegalStateException if the underlying protocol provider is
     *   not registered/signed on a public service.
     * @throws OperationFailedException with code NETWORK_FAILURE if
     *   retrieving the status fails due to errors experienced during
     *   network communication
     */
    public PresenceStatus queryContactStatus(String contactIdentifier)
        throws IllegalArgumentException,
               IllegalStateException,
               OperationFailedException
    {
        Contact contact = resolveContactID(contactIdentifier);

        if (contact == null)
            throw
                new IllegalArgumentException(
                        "contact " + contactIdentifier + " unknown");

        return contact.getPresenceStatus();
    }

    /**
     * Adds a subscription for the presence status of the contact
     * corresponding to the specified contactIdentifier.
     *
     * @param contactIdentifier the identifier of the contact whose status
     *   updates we are subscribing for. <p>
     * @throws IllegalArgumentException if <tt>contact</tt> is not a contact
     *   known to the underlying protocol provider
     * @throws IllegalStateException if the underlying protocol provider is
     *   not registered/signed on a public service.
     * @throws OperationFailedException with code NETWORK_FAILURE if
     *   subscribing fails due to errors experienced during network
     *   communication
     */
    public void subscribe(String contactIdentifier)
        throws IllegalArgumentException,
               IllegalStateException,
               OperationFailedException
    {
        subscribe(this.contactListRoot, contactIdentifier);
    }

    /**
     * Persistently adds a subscription for the presence status of the
     * contact corresponding to the specified contactIdentifier and indicates
     * that it should be added to the specified group of the server stored
     * contact list.
     *
     * @param parentGroup the parent group of the server stored contact list
     *   where the contact should be added. <p>
     * @param contactIdentifier the contact whose status updates we are
     *   subscribing for.
     * @throws IllegalArgumentException if <tt>contact</tt> or
     *   <tt>parent</tt> are not a contact known to the underlying protocol
     *   provider.
     * @throws IllegalStateException if the underlying protocol provider is
     *   not registered/signed on a public service.
     * @throws OperationFailedException with code NETWORK_FAILURE if
     *   subscribing fails due to errors experienced during network
     *   communication
     */
    public void subscribe(ContactGroup parentGroup, String contactIdentifier)
        throws IllegalArgumentException,
               IllegalStateException,
               OperationFailedException
    {
        logger.debug("let's subscribe " + contactIdentifier);

        //if the contact is already in the contact list
        ContactSipImpl contact = resolveContactID(contactIdentifier);

        if (contact != null)
        {
            if(contact.isPersistent())
            {
                throw new OperationFailedException(
                    "Contact " + contactIdentifier + " already exists.",
                    OperationFailedException.SUBSCRIPTION_ALREADY_EXISTS);
            }
            else
            {
                // we will remove it as we will created again
                // this is the case when making a non persistent contact to
                // a persistent one
                ContactGroupSipImpl oldParentGroup =
                    (ContactGroupSipImpl)contact.getParentContactGroup();
                oldParentGroup.removeContact(contact);
                fireSubscriptionEvent(contact, oldParentGroup,
                    SubscriptionEvent.SUBSCRIPTION_REMOVED);
            }
        }

        Address contactAddress;
        try
        {
            contactAddress
                = parentProvider.parseAddressString(contactIdentifier);
        }
        catch (ParseException exc)
        {
            throw new IllegalArgumentException(
                contactIdentifier + " is not a valid string.", exc);
        }

        // create a new contact, marked as resolvable and non resolved
        contact = new ContactSipImpl(contactAddress, this.parentProvider);
        ((ContactGroupSipImpl) parentGroup).addContact(contact);

        fireSubscriptionEvent(contact,
                              parentGroup,
                              SubscriptionEvent.SUBSCRIPTION_CREATED);

        // do not query the presence state
        if (this.presenceEnabled == false)
            return;

        assertConnected();
        subscriber.subscribe(new PresenceSubscriberSubscription(contact));
    }

    /**
     * Utility method throwing an exception if the stack is not properly
     * initialized.
     * @throws java.lang.IllegalStateException if the underlying stack is
     * not registered and initialized.
     */
    private void assertConnected()
        throws IllegalStateException
    {
        if (this.parentProvider == null)
            throw new IllegalStateException(
                "The provider must be non-null and signed on the "
                + "service before being able to communicate.");
        if (!this.parentProvider.isRegistered())
            throw new IllegalStateException(
                "The provider must be signed on the service before "
                + "being able to communicate.");
    }

    /**
     * Removes a subscription for the presence status of the specified contact.
     * @param contact the contact whose status updates we are unsubscribing
     * from.
     *
     * @throws OperationFailedException with code NETWORK_FAILURE if
     * unsubscribing fails due to errors experienced during network
     * communication
     * @throws IllegalArgumentException if <tt>contact</tt> is not a contact
     * known to the underlying protocol provider
     * @throws IllegalStateException if the underlying protocol provider is not
     * registered/signed on a public service.
     */
    public void unsubscribe(Contact contact)
        throws IllegalArgumentException,
               IllegalStateException,
               OperationFailedException
    {
        assertConnected();

        if (!(contact instanceof ContactSipImpl))
            throw
                new IllegalArgumentException(
                        "the contact is not a SIP contact");

        ContactSipImpl sipcontact = (ContactSipImpl) contact;

        /**
         * Does not assert if there is no subscription cause if the user
         * becomes offline he has terminated the subscription and so we have
         * no subscription of this contact but we wont to remove it.
         * Does not assert on connected cause have already has made the check.
         */
        unsubscribe(sipcontact, false);

        ((ContactGroupSipImpl) sipcontact.getParentContactGroup())
            .removeContact(sipcontact);

        // inform the listeners
        fireSubscriptionEvent(
            sipcontact,
            sipcontact.getParentContactGroup(),
            SubscriptionEvent.SUBSCRIPTION_REMOVED);
    }

    /**
     * Removes a subscription for the presence status of the specified contact
     * and optionally asserts that the specified contact has an existing
     * subscription prior to attempting the unregistration.
     *
     * @param sipcontact
     *            the contact whose status updates we are unsubscribing from.
     * @param assertConnectedAndSubscribed
     *            <tt>true</tt> to assert that the specified contact has an
     *            existing subscription prior to attempting the unregistration;
     *            <tt>false</tt> to not perform the respective checks
     * @throws OperationFailedException
     *             with code NETWORK_FAILURE if unsubscribing fails due to
     *             errors experienced during network communication
     * @throws IllegalArgumentException
     *             if <tt>contact</tt> is not a contact known to the underlying
     *             protocol provider
     * @throws IllegalStateException
     *             if the underlying protocol provider is not registered/signed
     *             on a public service.
     */
    private void unsubscribe(
            ContactSipImpl sipcontact,
            boolean assertConnectedAndSubscribed)
        throws IllegalArgumentException,
               IllegalStateException,
               OperationFailedException
    {
        // handle the case of a distant presence agent is used
        // and test if we are subscribed to this contact
        if (this.presenceEnabled && sipcontact.isResolvable())
        {
            if (assertConnectedAndSubscribed)
                assertConnected();

            subscriber
                .unsubscribe(
                    getAddress(sipcontact),
                    assertConnectedAndSubscribed);
        }

        // remove any trace of this contact
        terminateSubscription(sipcontact);
    }

    /**
     * Analyzes the incoming <tt>responseEvent</tt> and then forwards it to the
     * proper event handler.
     *
     * @param responseEvent the responseEvent that we received
     *            ProtocolProviderService.
     * @return <tt>true</tt> if the specified event has been handled by this
     *         processor and shouldn't be offered to other processors registered
     *         for the same method; <tt>false</tt>, otherwise
     */
    public boolean processResponse(ResponseEvent responseEvent)
    {
        if (this.presenceEnabled == false)
            return false;

        ClientTransaction clientTransaction = responseEvent
            .getClientTransaction();
        Response response = responseEvent.getResponse();

        CSeqHeader cseq = (CSeqHeader) response.getHeader(CSeqHeader.NAME);
        if (cseq == null)
        {
            logger.error("An incoming response did not contain a CSeq header");
            return false;
        }
        String method = cseq.getMethod();

        boolean processed = false;

        // PUBLISH
        if (method.equals(Request.PUBLISH))
        {
            // if it's a final response to a PUBLISH, we try to remove it from
            // the list of waited PUBLISH end
            if (response.getStatusCode() != Response.UNAUTHORIZED
                && response.getStatusCode() != Response
                    .PROXY_AUTHENTICATION_REQUIRED
                && response.getStatusCode() != Response.INTERVAL_TOO_BRIEF)
            {
                synchronized (this.waitedCallIds)
                {
                    this.waitedCallIds.remove(((CallIdHeader) response
                        .getHeader(CallIdHeader.NAME)).getCallId());
                }
            }

            // OK (200)
            if (response.getStatusCode() == Response.OK)
            {
                // remember the entity tag
                SIPETagHeader etHeader = (SIPETagHeader)
                    response.getHeader(SIPETagHeader.NAME);

                // must be one (rfc3903)
                if (etHeader == null)
                {
                    logger.debug("can't find the ETag header");
                    return false;
                }

                this.distantPAET = etHeader.getETag();

                // schedule a re-publish task
                ExpiresHeader expires = (ExpiresHeader)
                    response.getHeader(ExpiresHeader.NAME);

                if (expires == null)
                {
                    logger.error("no Expires header in the response");
                    return false;
                }

                // if it's a response to an unpublish request (Expires: 0),
                // invalidate the etag and don't schedule a republish
                if (expires.getExpires() == 0)
                {
                    this.distantPAET = null;
                    return true;
                }

                // just to be sure to not have two refreshing task
                if (this.republishTask != null)
                    this.republishTask.cancel();

                this.republishTask = new RePublishTask();

                int republishDelay = expires.getExpires();
                // keep a margin
                if (republishDelay >= REFRESH_MARGIN)
                    republishDelay -= REFRESH_MARGIN;
                timer.schedule(this.republishTask, republishDelay * 1000);

            // UNAUTHORIZED (401/407)
            }
            else if (response.getStatusCode() == Response.UNAUTHORIZED
                    || response.getStatusCode() == Response
                        .PROXY_AUTHENTICATION_REQUIRED)
            {
                try
                {
                    processAuthenticationChallenge(
                        clientTransaction,
                        response,
                        (SipProvider) responseEvent.getSource());
                }
                catch (OperationFailedException e)
                {
                    logger.error("can't handle the challenge", e);
                    return false;
                }
            // INTERVAL TOO BRIEF (423)
            }
            else if (response.getStatusCode() == Response.INTERVAL_TOO_BRIEF)
            {
                // we get the Min expires and we use it as the interval
                MinExpiresHeader min = (MinExpiresHeader)
                    response.getHeader(MinExpiresHeader.NAME);

                if (min == null)
                {
                    logger.error("can't find a min expires header in the 423" +
                            " error message");
                    return false;
                }

                // send a new publish with the new expires value
                Request req = null;
                try
                {
                    req = createPublish(min.getExpires(), true);
                }
                catch (OperationFailedException e)
                {
                    logger.error("can't create the new publish request", e);
                    return false;
                }

                ClientTransaction transac = null;
                try
                {
                    transac = this.parentProvider
                        .getDefaultJainSipProvider()
                        .getNewClientTransaction(req);
                }
                catch (TransactionUnavailableException e)
                {
                    logger.error("can't create the client transaction", e);
                    return false;
                }

                try
                {
                    transac.sendRequest();
                }
                catch (SipException e)
                {
                    logger.error("can't send the PUBLISH request", e);
                    return false;
                }

            // CONDITIONAL REQUEST FAILED (412)
            }
            else if (response.getStatusCode() == Response
                                                .CONDITIONAL_REQUEST_FAILED)
            {
                // as recommanded in rfc3903#5, we start a totally new
                // publication
                this.distantPAET = null;
                Request req = null;
                try
                {
                    req = createPublish(this.subscriptionDuration, true);
                }
                catch (OperationFailedException e)
                {
                    logger.error("can't create the new publish request", e);
                    return false;
                }

                ClientTransaction transac = null;
                try
                {
                    transac = this.parentProvider
                        .getDefaultJainSipProvider()
                        .getNewClientTransaction(req);
                }
                catch (TransactionUnavailableException e)
                {
                    logger.error("can't create the client transaction", e);
                    return false;
                }

                try
                {
                    transac.sendRequest();
                }
                catch (SipException e)
                {
                    logger.error("can't send the PUBLISH request", e);
                    return false;
                }

            // with every other error, we consider that we have to start a new
            // communication.
            // Enter p2p mode if the distant PA mode fails
            }
            else
            {
                logger.debug("error received from the network" + response);

                this.distantPAET = null;

                if (this.useDistantPA)
                {
                    logger.debug(
                        "we enter into the peer-to-peer mode"
                            + " as the distant PA mode fails");

                    setUseDistantPA(false);

                    // if we are here, we don't have any watcher so no need to
                    // republish our presence state
                }
            }

            processed = true;
        }

        return processed;
    }

    /**
     * Finalize the subscription of a contact and transform the pending contact
     * into a real contact.
     *
     * @param contact the contact concerned
     *
     * @throws NullPointerException if contact is null
     */
    private void finalizeSubscription(ContactSipImpl contact)
        throws NullPointerException
    {
        // remember the dialog created to be able to send SUBSCRIBE
        // refresh and to unsubscribe
        if (contact == null)
            throw new NullPointerException("contact");

        contact.setResolved(true);

        // inform the listeners that the contact is created
        this.fireSubscriptionEvent(
                contact,
                contact.getParentContactGroup(),
                SubscriptionEvent.SUBSCRIPTION_RESOLVED);

        logger.debug("contact " + contact + " resolved");
    }

    /**
     * Terminate the subscription to a contact presence status
     *
     * @param contact the contact concerned
     */
    private void terminateSubscription(ContactSipImpl contact)
    {
        if (contact == null)
        {
            logger.error("null contact provided, can't terminate" +
                    " subscription");
            return;
        }

        // we don't remove the contact
        changePresenceStatusForContact(
            contact,
            sipStatusEnum.getStatus(SipStatusEnum.UNKNOWN));
        contact.setResolved(false);
    }

    /**
     * Process a request from a distant contact
     *
     * @param requestEvent the <tt>RequestEvent</tt> containing the newly
     *            received request.
     * @return <tt>true</tt> if the specified event has been handled by this
     *         processor and shouldn't be offered to other processors registered
     *         for the same method; <tt>false</tt>, otherwise
     */
    public boolean processRequest(RequestEvent requestEvent)
    {
        if (this.presenceEnabled == false)
            return false;

        Request request = requestEvent.getRequest();

        EventHeader eventHeader
            = (EventHeader) request.getHeader(EventHeader.NAME);
        if ((eventHeader == null)
                || !"presence".equalsIgnoreCase(eventHeader.getEventType()))
        {
            // We are not concerned by this request, perhaps another
            // listener is. So don't send a 489 / Bad event answer here.
            return false;
        }

        ServerTransaction serverTransaction
            = EventPackageSupport.getOrCreateServerTransaction(requestEvent);
        if (serverTransaction == null)
            return false;

        boolean processed = false;

        // PUBLISH
        if (request.getMethod().equals(Request.PUBLISH))
        {
            /*
             * We aren't supposed to receive a publish so just say "not
             * implemented". This behavior is useful for SC to SC communication
             * with the PA auto detection feature and a server which proxy the
             * PUBLISH requests.
             */
            Response response;
            try
            {
                response
                    = parentProvider
                        .getMessageFactory()
                            .createResponse(Response.NOT_IMPLEMENTED, request);
            }
            catch (ParseException e)
            {
                logger.error("Error while creating the response 501", e);
                return false;
            }

            try
            {
                serverTransaction.sendResponse(response);
            }
            catch (Exception e)
            {
                logger.error("Error while sending the response 501", e);
                return false;
            }

            processed = true;
        }

        return processed;
    }

    /**
     * Called when a dialog is terminated
     *
     * @param dialogTerminatedEvent DialogTerminatedEvent
     * @return <tt>true</tt> if the specified event has been handled by this
     *         processor and shouldn't be offered to other processors registered
     *         for the same method; <tt>false</tt>, otherwise
     */
    public boolean processDialogTerminated(
            DialogTerminatedEvent dialogTerminatedEvent)
    {
        // never fired
        return false;
    }

    /**
     * Called when an IO error occurs
     *
     * @param exceptionEvent IOExceptionEvent
     * @return <tt>true</tt> if the specified event has been handled by this
     *         processor and shouldn't be offered to other processors registered
     *         for the same method; <tt>false</tt>, otherwise
     */
    public boolean processIOException(IOExceptionEvent exceptionEvent)
    {
        // never fired
        return false;
    }

    /**
     * Called when a transaction is terminated
     *
     * @param transactionTerminatedEvent TransactionTerminatedEvent
     * @return <tt>true</tt> if the specified event has been handled by this
     *         processor and shouldn't be offered to other processors registered
     *         for the same method; <tt>false</tt>, otherwise
     */
    public boolean processTransactionTerminated(
        TransactionTerminatedEvent transactionTerminatedEvent)
    {
        // nothing to do
        return false;
    }

    /**
     * Called when a timeout occur
     *
     * @param timeoutEvent TimeoutEvent
     * @return <tt>true</tt> if the specified event has been handled by this
     *         processor and shouldn't be offered to other processors registered
     *         for the same method; <tt>false</tt>, otherwise
     */
    public boolean processTimeout(TimeoutEvent timeoutEvent)
    {
        logger.error("timeout reached, it looks really abnormal: " +
                timeoutEvent.toString());
        return false;
    }

    /**
     * Attempts to re-generate the corresponding request with the proper
     * credentials.
     *
     * @param clientTransaction
     *            the corresponding transaction
     * @param response
     *            the challenge
     * @param jainSipProvider
     *            the provider that received the challenge
     * @throws OperationFailedException
     *             if processing the authentication challenge fails.
     */
    private void processAuthenticationChallenge(
            ClientTransaction clientTransaction,
            Response response,
            SipProvider jainSipProvider)
        throws OperationFailedException
    {
        EventPackageSupport.processAuthenticationChallenge(
            parentProvider,
            clientTransaction,
            response,
            jainSipProvider);
    }

    /**
     * Sets the presence status of <tt>contact</tt> to <tt>newStatus</tt>.
     *
     * @param contact the <tt>ContactSipImpl</tt> whose status we'd like
     * to set.
     * @param newStatus the new status we'd like to set to <tt>contact</tt>.
     */
    private void changePresenceStatusForContact(
        ContactSipImpl contact,
        PresenceStatus newStatus)
    {
        PresenceStatus oldStatus = contact.getPresenceStatus();

        contact.setPresenceStatus(newStatus);
        fireContactPresenceStatusChangeEvent(
                contact, contact.getParentContactGroup(), oldStatus);
    }

    /**
     * Returns a <code>ContactSipImpl</code> with a specific ID in case we have
     * a subscription for it and <tt>null<tt> otherwise.
     *
     * @param contactID
     *            a String identifier of the contact which is to be retrieved
     * @return the <code>ContactSipImpl</code> with the specified
     *         <code>contactID</code> or <tt>null</tt> if we don't have a
     *         subscription for the specified identifier
     */
    public ContactSipImpl findContactByID(String contactID)
    {
        return this.contactListRoot.findContactByID(contactID);
    }

    /**
     * Returns the protocol specific contact instance representing the local
     * user.
     *
     * @param destination the destination that we would be sending our contact
     * information to.
     *
     * @return a ContactSipImpl instance that represents us.
     */
    public ContactSipImpl getLocalContactForDst(ContactSipImpl destination)
    {
        return getLocalContactForDst(destination.getSipAddress());
    }

    /**
     * Returns the protocol specific contact instance representing the local
     * user.
     *
     * @param destination the destination that we would be sending our contact
     * information to.
     *
     * @return a ContactSipImpl instance that represents us.
     */
    public ContactSipImpl getLocalContactForDst(Address destination)
    {
        Address sipAddress = parentProvider.getOurSipAddress(destination);
        ContactSipImpl res
            = new ContactSipImpl(sipAddress, this.parentProvider);

        res.setPresenceStatus(this.presenceStatus);
        return res;
    }

    /**
     * Handler for incoming authorization requests.
     *
     * @param handler an instance of an AuthorizationHandler for
     *   authorization requests coming from other users requesting
     *   permission add us to their contact list.
     */
    public void setAuthorizationHandler(AuthorizationHandler handler)
    {
        // authorizations aren't supported by this implementation
    }

    /**
     * Returns the status message that was confirmed by the server
     *
     * @return the last status message that we have requested and the server
     * has confirmed.
     */
    public String getCurrentStatusMessage()
    {
        return this.statusMessage;
    }

    /**
     * Creates and returns a unresolved contact from the specified
     * <tt>address</tt> and <tt>persistentData</tt>. The method will not try
     * to establish a network connection and resolve the newly created Contact
     * against the server. The protocol provider may will later try and resolve
     * the contact. When this happens the corresponding event would notify
     * interested subscription listeners.
     *
     * @param address an identifier of the contact that we'll be creating.
     * @param persistentData a String returned Contact's getPersistentData()
     * method during a previous run and that has been persistently stored
     * locally.
     *
     * @return the unresolved <tt>Contact</tt> created from the specified
     * <tt>address</tt> and <tt>persistentData</tt>
     */
    public Contact createUnresolvedContact(
        String address, String persistentData)
    {
        return createUnresolvedContact(address
                , persistentData
                , getServerStoredContactListRoot());
    }

    /**
    * Creates and returns a unresolved contact from the specified
    * <tt>address</tt> and <tt>persistentData</tt>. The method will not try
    * to establish a network connection and resolve the newly created Contact
    * against the server. The protocol provider may will later try and resolve
    * the contact. When this happens the corresponding event would notify
    * interested subscription listeners.
    *
    * @param addressStr an identifier of the contact that we'll be creating.
    * @param persistentData a String returned Contact's getPersistentData()
    * method during a previous run and that has been persistently stored
    * locally.
    * @param parent the group where the unresolved contact is
    * supposed to belong to.
    *
    * @return the unresolved <tt>Contact</tt> created from the specified
    * <tt>address</tt> and <tt>persistentData</tt>
    */
    public Contact createUnresolvedContact(String addressStr,
                         String persistentData,
                         ContactGroup parent)
    {
        Address contactAddress;
        try
        {
            contactAddress = parentProvider.parseAddressString(addressStr);
        }
        catch (ParseException exc)
        {
            throw new IllegalArgumentException(
                addressStr + " is not a valid string.", exc);
        }

        ContactSipImpl contact = new ContactSipImpl(contactAddress,
                                                    this.parentProvider);

        contact.setResolved(false);

        ((ContactGroupSipImpl) parent).addContact(contact);

        fireSubscriptionEvent(contact,
                parent,
                SubscriptionEvent.SUBSCRIPTION_CREATED);

        return contact;
    }

    /**
     * Creates a non persistent contact for the specified address. This would
     * also create (if necessary) a group for volatile contacts that would not
     * be added to the server stored contact list. This method would have no
     * effect on the server stored contact list.
     *
     * @param contactAddress the address of the volatile contact we'd like to
     * create.
     * @return the newly created volatile contact.
     */
    public ContactSipImpl createVolatileContact(Address contactAddress)
    {
        // First create the new volatile contact;
        ContactSipImpl newVolatileContact
            = new ContactSipImpl(contactAddress, this.parentProvider);
        newVolatileContact.setDisplayName(contactAddress.getDisplayName());
        newVolatileContact.setPersistent(false);

        // Check whether a volatile group already exists and if not create one
        ContactGroupSipImpl theVolatileGroup = getNonPersistentGroup();

        // if the parent volatile group is null then we create it
        if (theVolatileGroup == null)
        {
            theVolatileGroup = new ContactGroupSipImpl(
                "NotInContactList",
                this.parentProvider);
            theVolatileGroup.setResolved(false);
            theVolatileGroup.setPersistent(false);

            this.contactListRoot.addSubgroup(theVolatileGroup);

            fireServerStoredGroupEvent(theVolatileGroup
                           , ServerStoredGroupEvent.GROUP_CREATED_EVENT);
        }

        //now add the volatile contact inside it
        theVolatileGroup.addContact(newVolatileContact);
        fireSubscriptionEvent(newVolatileContact
                         , theVolatileGroup
                         , SubscriptionEvent.SUBSCRIPTION_CREATED);

        return newVolatileContact;
    }

    /**
     * Returns the volatile group or null if this group has not yet been
     * created.
     *
     * @return a volatile group existing in our contact list or <tt>null</tt>
     * if such a group has not yet been created.
     */
    private ContactGroupSipImpl getNonPersistentGroup()
    {
        for (int i = 0;
             i < getServerStoredContactListRoot().countSubgroups();
             i++)
        {
            ContactGroupSipImpl gr = (ContactGroupSipImpl)
                getServerStoredContactListRoot().getGroup(i);

            if(!gr.isPersistent())
            {
                return gr;
            }
        }

        return null;
    }

    /**
     * Tries to find a <code>ContactSipImpl</code> which is identified either by
     * a specific <code>contactID</code> or by a derivation of it.
     *
     * @param contactID
     *            the identifier of the <code>ContactSipImpl</code> to retrieve
     *            either by directly using it or by deriving it
     * @return a <code>ContactSipImpl</code> which is identified either by the
     *         specified <code>contactID</code> or by a derivation of it
     */
    ContactSipImpl resolveContactID(String contactID)
    {
        ContactSipImpl res = findContactByID(contactID);

        if (res == null)
        {
            // we try to resolve the conflict by removing "sip:" from the id
            if (contactID.startsWith("sip:"))
                res = findContactByID(contactID.substring(4));

            if (res == null)
            {
                int domainBeginIndex = contactID.indexOf('@');

                // we try to remove the part after the '@'
                if (domainBeginIndex > -1)
                {
                    res
                        = findContactByID(
                            contactID.substring(0, domainBeginIndex));

                    // try the same thing without sip:
                    if ((res == null) && contactID.startsWith("sip:"))
                        res
                            = findContactByID(
                                contactID.substring(4, domainBeginIndex));
                }

                if (res == null)
                {
                    // sip:user_name@ip_address:5060;transport=udp
                    int domainEndIndex = contactID.indexOf(":", 4);

                    // if port is absent try removing the params after ;
                    if (domainEndIndex < 0)
                        domainEndIndex = contactID.indexOf(";", 4);

                    if (domainEndIndex > -1)
                        res
                            = findContactByID(
                                contactID.substring(4, domainEndIndex));
                }
            }
        }
        return res;
    }

    /**
     * Returns a new valid xml document.
     *
     * @return a correct xml document or null if an error occurs
     */
    Document createDocument()
    {
        try
        {
            if (this.docBuilderFactory == null)
                this.docBuilderFactory = DocumentBuilderFactory.newInstance();

            if (this.docBuilder == null)
                this.docBuilder = this.docBuilderFactory.newDocumentBuilder();
        }
        catch (Exception e)
        {
            logger.error("can't create the new xml document", e);
            return null;
        }

        return this.docBuilder.newDocument();
    }

    /**
     * Convert a xml document
     *
     * @param document the document to convert
     *
     * @return a string representing <tt>document</tt> or null if an error
     * occur
     */
    String convertDocument(Document document)
    {
        DOMSource source = new DOMSource(document);
        StringWriter stringWriter = new StringWriter();
        StreamResult result = new StreamResult(stringWriter);

        try
        {
            if (this.transFactory == null)
                this.transFactory = TransformerFactory.newInstance();

            if (this.transformer == null)
                this.transformer = this.transFactory.newTransformer();

            this.transformer.transform(source, result);
        }
        catch (Exception e)
        {
            logger.error("can't convert the xml document into a string", e);
            return null;
        }

        return stringWriter.toString();
    }

    /**
     * Convert a xml document
     *
     * @param document the document as a String
     *
     * @return a <tt>Document</tt> representing the document or null if an
     * error occur
     */
    Document convertDocument(String document)
    {
        StringReader reader = new StringReader(document);
        StreamSource source = new StreamSource(reader);
        Document doc = createDocument();

        if (doc == null)
            return null;

        DOMResult result = new DOMResult(doc);

        try
        {
            if (this.transFactory == null)
                this.transFactory = TransformerFactory.newInstance();

            if (this.transformer == null)
                this.transformer = this.transFactory.newTransformer();

            this.transformer.transform(source, result);
        }
        catch (Exception e)
        {
            logger.error("can't convert the string into a xml document", e);
            return null;
        }

        return doc;
    }

    /**
     * Converts the <tt>PresenceStatus</tt> of <tt>contact</tt> into a PIDF
     * document.
     *
     * @param contact The contact which interest us
     *
     * @return a PIDF document representing the current presence status of
     * this contact or null if an error occurs.
     */
     public byte[] getPidfPresenceStatus(ContactSipImpl contact)
     {
         Document doc = this.createDocument();

         if (doc == null)
             return null;

         String contactUri = contact.getSipAddress().getURI().toString();

         // <presence>
         Element presence = doc.createElement(PRESENCE_ELEMENT);
         presence.setAttribute(NS_ELEMENT, NS_VALUE);
         presence.setAttribute(RPID_NS_ELEMENT, RPID_NS_VALUE);
         presence.setAttribute(DM_NS_ELEMENT, DM_NS_VALUE);
         presence.setAttribute(ENTITY_ATTRIBUTE, contactUri);
         doc.appendChild(presence);

         // <person>
         Element person = doc.createElement(NS_PERSON_ELT);
         person.setAttribute(ID_ATTRIBUTE, PERSON_ID);
         presence.appendChild(person);

         // <activities>
         Element activities = doc.createElement(NS_ACTIVITY_ELT);
         person.appendChild(activities);

         // the correct activity
         if (contact.getPresenceStatus()
                         .equals(sipStatusEnum.getStatus(SipStatusEnum.AWAY)))
         {
             Element away = doc.createElement(NS_AWAY_ELT);
             activities.appendChild(away);
         }
         else if (contact.getPresenceStatus()
                         .equals(sipStatusEnum.getStatus(SipStatusEnum.BUSY)))
         {
             Element busy = doc.createElement(NS_BUSY_ELT);
             activities.appendChild(busy);
         }
         else if (contact.getPresenceStatus()
                 .equals(sipStatusEnum.getStatus(SipStatusEnum.ON_THE_PHONE)))
         {
             Element otp = doc.createElement(NS_OTP_ELT);
             activities.appendChild(otp);
         }

         // <tuple>
         Element tuple = doc.createElement(TUPLE_ELEMENT);
         tuple.setAttribute(ID_ATTRIBUTE, TUPLE_ID);
         presence.appendChild(tuple);

         // <status>
         Element status = doc.createElement(STATUS_ELEMENT);
         tuple.appendChild(status);

         // <basic>
         Element basic = doc.createElement(BASIC_ELEMENT);
         if (contact.getPresenceStatus()
                     .equals(sipStatusEnum.getStatus(SipStatusEnum.OFFLINE)))
         {
             basic.appendChild(doc.createTextNode(OFFLINE_STATUS));
         }
         else
         {
             basic.appendChild(doc.createTextNode(ONLINE_STATUS));
         }
         status.appendChild(basic);

         // <contact>
         Element contactUriEl = doc.createElement(CONTACT_ELEMENT);
         Node cValue = doc.createTextNode(contactUri);
         contactUriEl.appendChild(cValue);
         tuple.appendChild(contactUriEl);

         // <note> we write our real status here, this status SHOULD not be
         // used for automatic parsing but some (bad) IM clients do this...
         // we don't use xml:lang here because it's not really relevant
         Element noteNodeEl = doc.createElement(NOTE_ELEMENT);
         noteNodeEl.appendChild(doc.createTextNode(contact.getPresenceStatus()
                 .getStatusName()));
         tuple.appendChild(noteNodeEl);

         String res = convertDocument(doc);
         if (res == null)
             return null;

         return res.getBytes();
     }

     /**
      * Sets the contact's presence status using the PIDF document provided.
      * In case of conflict (more than one status per contact) the last valid
      * status in the document is used.
      * This implementation is very tolerant to be more compatible with bad
      * implementations of SIMPLE. The limit of the tolerance is defined by
      * the CPU cost: as far as the tolerance costs nothing more in well
      * structured documents, we do it.
      *
      * @param presenceDoc the pidf document to use
      */
     public void setPidfPresenceStatus(String presenceDoc)
     {
         Document doc = convertDocument(presenceDoc);

         if (doc == null)
             return;

         logger.debug("parsing:\n" + presenceDoc);

         // <presence>
         NodeList presList = doc.getElementsByTagNameNS(NS_VALUE,
                 PRESENCE_ELEMENT);

         if (presList.getLength() == 0)
         {
             presList = doc.getElementsByTagNameNS(ANY_NS, PRESENCE_ELEMENT);

             if (presList.getLength() == 0)
             {
                 logger.error("no presence element in this document");
                 return;
             }
         }
         if (presList.getLength() > 1)
         {
             logger.warn("more than one presence element in this document");
         }
         Node presNode = presList.item(0);
         if (presNode.getNodeType() != Node.ELEMENT_NODE)
         {
             logger.error("the presence node is not an element");
             return;
         }
         Element presence = (Element) presNode;

         // RPID area

         // due to a lot of changes in the past years to this functionality,
         // the namespace used by servers and clients are often wrong so we just
         // ignore namespaces here

         PresenceStatus personStatus = null;
         NodeList personList = presence.getElementsByTagNameNS(ANY_NS,
                 PERSON_ELEMENT);

         //if (personList.getLength() > 1) {
         //    logger.error("more than one person in this document");
         //    return;
         //}

         if (personList.getLength() > 0)
         {
             Node personNode = personList.item(0);
             if (personNode.getNodeType() != Node.ELEMENT_NODE)
             {
                 logger.error("the person node is not an element");
                 return;
             }
             Element person = (Element) personNode;

             NodeList activityList =
                 person.getElementsByTagNameNS(ANY_NS, ACTIVITY_ELEMENT);
             if (activityList.getLength() > 0)
             {
                 Element activity = null;
                 // find the first correct activity
                 for (int i = 0; i < activityList.getLength(); i++)
                 {
                     Node activityNode = activityList.item(i);

                     if (activityNode.getNodeType() != Node.ELEMENT_NODE)
                         continue;

                     activity = (Element) activityNode;

                     NodeList statusList = activity.getChildNodes();
                     for (int j = 0; j < statusList.getLength(); j++)
                     {
                         Node statusNode = statusList.item(j);
                         if (statusNode.getNodeType() == Node.ELEMENT_NODE)
                         {
                             String statusname = statusNode.getLocalName();
                             if (statusname.equals(AWAY_ELEMENT))
                             {
                                 personStatus = sipStatusEnum
                                     .getStatus(SipStatusEnum.AWAY);
                                 break;
                             }
                             else if (statusname.equals(BUSY_ELEMENT))
                             {
                                 personStatus = sipStatusEnum
                                     .getStatus(SipStatusEnum.BUSY);
                                 break;
                             }
                             else if (statusname.equals(OTP_ELEMENT))
                             {
                                 personStatus = sipStatusEnum
                                     .getStatus(SipStatusEnum.ON_THE_PHONE);
                                 break;
                             }
                         }
                     }
                     if (personStatus != null)
                         break;
                 }
             }
         }

         // Vector containing the list of status to set for each contact in
         // the presence document ordered by priority (highest first).
         // <SipContact, Float (priority), SipStatusEnum>
         List<Object[]> newPresenceStates = new Vector<Object[]>(3, 2);

         // <tuple>
         NodeList tupleList = getPidfChilds(presence, TUPLE_ELEMENT);
         for (int i = 0; i < tupleList.getLength(); i++)
         {
             Node tupleNode = tupleList.item(i);

             if (tupleNode.getNodeType() != Node.ELEMENT_NODE)
                 continue;

             Element tuple = (Element) tupleNode;

             // <contact>
             NodeList contactList = getPidfChilds(tuple, CONTACT_ELEMENT);

             // we use a vector here and not an unique contact to handle an
             // error case where many contacts are associated with a status
             // Vector<ContactSipImpl>
             List<Object[]> sipcontact = new Vector<Object[]>(1, 3);
             String contactID = null;
             if (contactList.getLength() == 0)
             {
                 // use the entity attribute of the presence node
                 contactID = XMLUtils.getAttribute(
                         presNode, ENTITY_ATTRIBUTE);
                 // also accept entity URIs starting with pres: instead of sip:
                 if (contactID.startsWith("pres:"))
                 {
                     contactID = contactID.substring("pres:".length());
                 }
                 Contact tmpContact = resolveContactID(contactID);

                 if (tmpContact != null)
                 {
                     sipcontact.add(new Object[] { tmpContact, new Float(0f) });
                 }
             }
             else
             {
                 // this is normally not permitted by RFC3863
                 for (int j = 0; j < contactList.getLength(); j++)
                 {
                     Node contactNode = contactList.item(j);

                     if (contactNode.getNodeType() != Node.ELEMENT_NODE)
                         continue;

                     Element contact = (Element) contactNode;

                     contactID = getTextContent(contact);
                     // also accept entity URIs starting with pres: instead
                     // of sip:
                     if (contactID.startsWith("pres:"))
                     {
                         contactID = contactID.substring("pres:".length());
                     }
                     Contact tmpContact = resolveContactID(contactID);
                     if (tmpContact == null)
                         continue;

                     // defines an array containing the contact and its
                     // priority
                     Object tab[] = new Object[2];

                     // search if the contact has a priority
                     String prioStr = contact.getAttribute(PRIORITY_ATTRIBUTE);
                     Float prio = null;
                     try
                     {
                         if (prioStr == null || prioStr.length() == 0)
                         {
                             prio = new Float(0f);
                         }
                         else
                         {
                             prio = Float.valueOf(prioStr);
                         }
                     }
                     catch (NumberFormatException e)
                     {
                         logger.debug("contact priority is not a valid float",
                                 e);
                         prio = new Float(0f);
                     }

                     // 0 <= priority <= 1 according to rfc
                     if (prio.floatValue() < 0)
                     {
                         prio = new Float(0f);
                     }

                     if (prio.floatValue() > 1)
                     {
                         prio = new Float(1f);
                     }

                     tab[0] = tmpContact;
                     tab[1] = prio;

                     // search if the contact hasn't already been added
                     boolean contactAlreadyListed = false;
                     for (int k = 0; k < sipcontact.size(); k++)
                     {
                         Object[] tmp = sipcontact.get(k);

                         if (((Contact) tmp[0]).equals(tmpContact))
                         {
                             contactAlreadyListed = true;

                             // take the highest priority
                             if (((Float) tmp[1]).floatValue() <
                                     prio.floatValue())
                             {
                                 sipcontact.remove(k);
                                 sipcontact.add(tab);
                             }
                             break;
                         }
                     }

                     // add the contact and its priority to the list
                     if (!contactAlreadyListed)
                     {
                         sipcontact.add(tab);
                     }
                 }
             }

             if (sipcontact.isEmpty())
             {
                 logger.debug("no contact found for id: " + contactID);
                 continue;
             }

             // if we use RPID, simply ignore the standard PIDF status
             if (personStatus != null)
             {
                 newPresenceStates = setStatusForContacts(
                         personStatus,
                         sipcontact,
                         newPresenceStates);
                 continue;
             }

             // <status>
             NodeList statusList = getPidfChilds(tuple, STATUS_ELEMENT);

             // in case of many status, just consider the last one
             // this is normally not permitted by RFC3863
             int index = statusList.getLength() - 1;
             Node statusNode = null;
             do
             {
                 Node temp = statusList.item(index);
                 if (temp.getNodeType() == Node.ELEMENT_NODE)
                 {
                     statusNode = temp;
                     break;
                 }
                 index--;
             }
             while (index >= 0);

             Element basic = null;

             if (statusNode == null)
             {
                 logger.debug("no valid status in this tuple");
             }
             else
             {
                 Element status = (Element) statusNode;

                 // <basic>
                 NodeList basicList = getPidfChilds(status, BASIC_ELEMENT);

                 // in case of many basic, just consider the last one
                 // this is normally not permitted by RFC3863
                 index = basicList.getLength() - 1;
                 Node basicNode = null;
                 do
                 {
                     Node temp = basicList.item(index);
                     if (temp.getNodeType() == Node.ELEMENT_NODE)
                     {
                         basicNode = temp;
                         break;
                     }
                     index--;
                 }
                 while (index >= 0);

                 if (basicNode == null)
                 {
                     logger.debug("no valid <basic> in this status");
                 }
                 else
                 {
                     basic = (Element) basicNode;
                 }
             }

             // search for a <note> that can define a more precise
             // status this is not recommended by RFC3863 but some im
             // clients use this.
             NodeList noteList = getPidfChilds(tuple, NOTE_ELEMENT);

             boolean changed = false;
             for (int k = 0; k < noteList.getLength() && !changed; k++)
             {
                 Node noteNode = noteList.item(k);

                 if (noteNode.getNodeType() != Node.ELEMENT_NODE)
                     continue;

                 Element note = (Element) noteNode;

                 String state = getTextContent(note);

                 Iterator<PresenceStatus> states
                     = sipStatusEnum.getSupportedStatusSet();
                 while (states.hasNext())
                 {
                     PresenceStatus current = states.next();

                     if (current.getStatusName().equalsIgnoreCase(state))
                     {
                         changed = true;
                         newPresenceStates = setStatusForContacts(current,
                                 sipcontact,
                                 newPresenceStates);
                         break;
                     }
                 }
             }

             if (changed == false && basic != null)
             {
                 if (getTextContent(basic).equalsIgnoreCase(ONLINE_STATUS))
                 {
                     newPresenceStates = setStatusForContacts(
                             sipStatusEnum.getStatus(SipStatusEnum.ONLINE),
                             sipcontact,
                             newPresenceStates);
                 }
                 else if (getTextContent(basic).equalsIgnoreCase(
                         OFFLINE_STATUS))
                 {
                     newPresenceStates = setStatusForContacts(
                             sipStatusEnum.getStatus(SipStatusEnum.OFFLINE),
                             sipcontact,
                             newPresenceStates);
                 }
             }
             else
             {
                 if (changed == false)
                 {
                     logger.debug("no suitable presence state found in this "
                            + "tuple");
                 }
             }
         } // for each <tuple>

         // Now really set the new presence status for the listed contacts
         // newPresenceStates is ordered so priority order is respected
         for (Object[] tab : newPresenceStates)
         {
             ContactSipImpl contact = (ContactSipImpl) tab[0];
             PresenceStatus status = (PresenceStatus) tab[2];

             changePresenceStatusForContact(contact, status);
         }
     }

     /**
      * Secured call to XMLUtils.getText (no null returned but an empty string)
      *
      * @param node the node with which call <tt>XMLUtils.getText()</tt>
      *
      * @return the string contained in the node or an empty string if there is
      * no text information in the node.
      */
     private String getTextContent(Element node)
     {
         String res = XMLUtils.getText(node);

         if (res == null)
         {
             logger.warn("no text for element '" + node.getNodeName() + "'");
             return "";
         }

         return res;
     }

     /**
      * Gets the list of the descendant of an element in the pidf namespace.
      * If the list is empty, we try to get this list in any namespace.
      * This method is useful for being able to read pidf document without any
      * namespace or with a wrong namespace.
      *
      * @param element the base element concerned.
      * @param childName the name of the descendants to match on.
      *
      * @return The list of all the descendant node.
      */
     private NodeList getPidfChilds(Element element, String childName)
     {
         NodeList res;

         res = element.getElementsByTagNameNS(NS_VALUE, childName);

         if (res.getLength() == 0)
         {
             res = element.getElementsByTagNameNS(ANY_NS, childName);
         }

         return res;
     }

     /**
      * Associate the provided presence state to the contacts considering the
      * current presence states and priorities.
      *
      * @param presenceState The presence state to associate to the contacts
      * @param contacts A list of <contact, priority> concerned by the
      *  presence status.
      * @param curStatus The list of the current presence status ordered by
      *  priority (highest priority first).
      *
      * @return a Vector containing a list of <contact, priority, status>
      *  ordered by priority (highest first). Null if a parameter is null.
      */
     private List<Object[]> setStatusForContacts(
         PresenceStatus presenceState,
         Iterable<Object[]> contacts,
         List<Object[]> curStatus)
     {
         // test parameters
         if (presenceState == null || contacts == null || curStatus == null)
             return null;

         // for each contact in the list
         for (Object[] tab : contacts)
         {
             Contact contact = (Contact) tab[0];
             float priority = ((Float) tab[1]).floatValue();

             // for each existing contact
             int pos = 0;
             boolean skip = false;
             for (int i = 0; i < curStatus.size(); i++)
             {
                 Object tab2[] = curStatus.get(i);
                 Contact curContact = (Contact) tab2[0];
                 float curPriority = ((Float) tab2[1]).floatValue();

                 // save the place where to add this contact in the list
                 if (pos == 0 && curPriority <= priority)
                 {
                     pos = i;
                 }

                 if (curContact.equals(contact))
                 {
                     // same contact but with an higher priority
                     // simply ignore this new status affectation
                     if (curPriority > priority)
                     {
                         skip = true;
                         break;
                     // same contact but with a lower priority
                     // replace the old status with this one
                     }
                     else if (curPriority < priority)
                     {
                         curStatus.remove(i);
                     // same contact and same priority
                     // consider the reachability of the status
                     }
                     else
                     {
                         PresenceStatus curPresence = (PresenceStatus) tab2[2];
                         if (curPresence.getStatus() >=
                             presenceState.getStatus())
                         {
                             skip = true;
                             break;
                         }

                         curStatus.remove(i);
                     }

                     i--;

                 }
             }

             if (skip)
                 continue;

             // insert the new entry
             curStatus.add(
                 pos,
                 new Object[] { contact, new Float(priority), presenceState });
         }

         return curStatus;
     }

     /**
      * Forces the poll of a contact to update its current state.
      *
      * @param contact the contact to poll
      */
     public void forcePollContact(ContactSipImpl contact)
     {
         if (this.presenceEnabled == false || !contact.isResolvable())
             return;

         // Attempt to subscribe.
         try
         {
             subscriber.poll(new PresenceSubscriberSubscription(contact));
         }
         catch (OperationFailedException ex)
         {
             logger.error("Failed to create and send the subcription", ex);
         }
     }

     /**
      * Unsubscribe to every contact.
      */
     public void unsubscribeToAllContact()
     {
         logger.debug("trying to unsubscribe to every contact");

         // send event notifications saying that all our buddies are
         // offline. SIMPLE does not implement top level buddies
         // nor subgroups for top level groups so a simple nested loop
         // is enough.
         Iterator<ContactGroup> groupsIter
             = getServerStoredContactListRoot().subgroups();

         while (groupsIter.hasNext())
         {
             ContactGroup group = groupsIter.next();
             Iterator<Contact> contactsIter = group.contacts();

             while (contactsIter.hasNext())
             {
                 ContactSipImpl contact = (ContactSipImpl) contactsIter.next();

                 try
                 {
                     unsubscribe(contact, false);
                 }
                 catch (OperationFailedException ex)
                 {
                     logger.error(
                         "Failed to unsubscribe to contact " + contact,
                         ex);
                     return;
                 }
             }
         }
     }

     /**
      * Cancels the timer which handles all scheduled tasks and disposes of the
      * currently existing tasks scheduled with it.
      */
     private void cancelTimer()
     {

         /*
          * The timer is being canceled so the tasks schedules with it are being
          * made obsolete.
          */
         if (republishTask != null)
             republishTask = null;
         if (pollingTask != null)
             pollingTask = null;

         timer.cancel();
     }

     /**
      * A <tt>TimerTask</tt> handling refresh of PUBLISH requests.
      */
     private class RePublishTask extends TimerTask
     {
         /**
          * Send a new PUBLISH request to refresh the publication
          */
         public void run()
         {
             Request req = null;
             try
             {
                 if (distantPAET != null)
                 {
                     req = createPublish(subscriptionDuration, false);
                 }
                 else
                 {
                     // if the last publication failed for any reason, send a
                     // new publication, not a refresh
                     req = createPublish(subscriptionDuration, true);
                 }
             }
             catch (OperationFailedException e)
             {
                 logger.error("can't create a new PUBLISH message", e);
                 return;
             }

             ClientTransaction transac = null;
             try
             {
                 transac = parentProvider
                     .getDefaultJainSipProvider().getNewClientTransaction(req);
             }
             catch (TransactionUnavailableException e)
             {
                 logger.error("can't create the client transaction", e);
                 return;
             }

             try
             {
                 transac.sendRequest();
             }
             catch (SipException e)
             {
                 logger.error("can't send the PUBLISH request", e);
                 return;
             }
         }
     }

     /**
      * A task handling polling of offline contacts.
      */
     private class PollOfflineContactsTask extends TimerTask
     {
         /**
          * Check if we can't subscribe to this contact now
          */
         public void run()
         {
             // send a subscription for every contact
             Iterator<ContactGroup> groupsIter
                 = getServerStoredContactListRoot().subgroups();

             while (groupsIter.hasNext())
             {
                 ContactGroup group = groupsIter.next();
                 Iterator<Contact> contactsIter = group.contacts();

                 while (contactsIter.hasNext())
                 {
                     ContactSipImpl contact
                         = (ContactSipImpl) contactsIter.next();

                     // poll this contact
                     forcePollContact(contact);
                 }
             }
         }
     }

     /**
      * The method is called by a ProtocolProvider implementation whenever
      * a change in the registration state of the corresponding provider had
      * occurred. The method is particularly interested in events stating
      * that the SIP provider has unregistered so that it would fire
      * status change events for all contacts in our buddy list.
      *
      * @param evt ProviderStatusChangeEvent the event describing the status
      * change.
      */
      public void registrationStateChanged(RegistrationStateChangeEvent evt)
      {
          if(evt.getNewState() == RegistrationState.UNREGISTERING)
          {
              // stop any task associated with the timer
              cancelTimer();

              // this will not be called by anyone else, so call it
              // the method will terminate every active subscription
              try
              {
                  publishPresenceStatus(
                      sipStatusEnum.getStatus(SipStatusEnum.OFFLINE), "");
              }
              catch (OperationFailedException e)
              {
                  logger.error("can't set the offline mode", e);
              }

              // we wait for every SUBSCRIBE, NOTIFY and PUBLISH transaction
              // to finish before continuing the unsubscription
              for (byte i = 0; i < 10; i++)
              {   // wait 5 s. max
                  synchronized (waitedCallIds)
                  {
                      if (waitedCallIds.size() == 0)
                      {
                          break;
                      }
                  }

                  synchronized (this)
                  {
                      try
                      {
                          wait(500);
                      }
                      catch (InterruptedException e)
                      {
                          logger.debug("abnormal behavior, may cause " +
                                "unnecessary CPU use", e);
                      }
                  }
              }
          }
          else if (evt.getNewState().equals(
                  RegistrationState.REGISTERED))
          {
               logger.debug("enter registered state");

               /*
                * If presence support is enabled and the keep-alive method
                * is REGISTER, we'll get RegistrationState.REGISTERED more
                * than one though we're already registered. If we're
                * receiving such subsequent REGISTERED, we don't have to do
                * anything because we've already set it up in response to
                * the first REGISTERED.
                */
               if ((presenceEnabled == false) || (pollingTask != null))
               {
                   return;
               }

               // send a subscription for every contact
               Iterator<ContactGroup> groupsIter
                   = getServerStoredContactListRoot().subgroups();
               while (groupsIter.hasNext())
               {
                   ContactGroup group = groupsIter.next();
                   Iterator<Contact> contactsIter = group.contacts();

                   while (contactsIter.hasNext())
                   {
                       ContactSipImpl contact
                           = (ContactSipImpl) contactsIter.next();

                       if (contact.isResolved())
                       {
                           logger.debug("contact " + contact
                                   + " already resolved");
                           continue;
                       }

                       // try to subscribe to this contact
                       forcePollContact(contact);
                   }
               }

               // create the new polling task
               pollingTask = new PollOfflineContactsTask();

               // start polling the offline contacts
               timer.schedule(pollingTask, pollingTaskPeriod, pollingTaskPeriod);
           }
           else if(evt.getNewState()
                     == RegistrationState.CONNECTION_FAILED)
           {
                // if connection failed we have lost network connectivity
                // we must fire that all contacts has gone offline
                Iterator<ContactGroup> groupsIter
                    = getServerStoredContactListRoot().subgroups();

                while(groupsIter.hasNext())
                {
                    ContactGroup group = groupsIter.next();
                    Iterator<Contact> contactsIter = group.contacts();

                    while(contactsIter.hasNext())
                    {
                        ContactSipImpl contact
                            = (ContactSipImpl)contactsIter.next();

                        PresenceStatus oldContactStatus
                            = contact.getPresenceStatus();

                        contact.setResolved(false);
                        if (subscriber != null)
                            try
                            {
                                subscriber
                                    .removeSubscription(getAddress(contact));
                            }
                            catch (OperationFailedException ex)
                            {
                                logger.debug(
                                    "Failed to remove subscription to contact "
                                        + contact);
                            }

                        if(!oldContactStatus.isOnline())
                            continue;

                        contact.setPresenceStatus(
                            sipStatusEnum.getStatus(SipStatusEnum.OFFLINE));

                        fireContactPresenceStatusChangeEvent(
                              contact
                            , contact.getParentContactGroup()
                            , oldContactStatus);
                    }
                }

                // stop any task associated with the timer
                cancelTimer();

                waitedCallIds.clear();
           }
      }

    /**
     * Gets the identifying address of a specific <code>ContactSipImpl</code> in
     * the form of a <code>Address</code> value.
     *
     * @param contact
     *            the <code>ContactSipImpl</code> to get the address of
     * @return a new <code>Address</code> instance representing the identifying
     *         address of the specified <code>ContactSipImpl</code>
     *
     * @throws OperationFailedException parsing this contact's address fails.
     */
    private Address getAddress(ContactSipImpl contact)
        throws OperationFailedException
    {
        try
        {
            return parentProvider.parseAddressString(contact.getAddress());
        }
        catch (ParseException ex)
        {
            //Shouldn't happen
            ProtocolProviderServiceSipImpl.throwOperationFailedException(
                "An unexpected error occurred while constructing the address",
                OperationFailedException.INTERNAL_ERROR, ex, logger);
            return null;//unreachable but necessary.
        }
    }

    /**
     * Represents a subscription of a specific <code>ContactSipImpl</code> to
     * our presence event package.
     *
     * @author Lubomir Marinov
     */
    private class PresenceNotifierSubscription
        extends EventPackageNotifier.Subscription
    {

        /**
         * The <code>ContactSipImpl</code> which is the subscriber this
         * subscription notifies.
         */
        private final ContactSipImpl contact;

        /**
         * Initializes a new <code>PresenceNotifierSubscription</code> instance
         * which is to represent a subscription of a <code>ContactSipImpl</code>
         * specified by <code>Address</code> to our presence event package.
         *
         * @param fromAddress
         *            the <code>Address</code> of the
         *            <code>ContactSipImpl</code> subscribed to our presence
         *            event package. If no <code>ContactSipImpl</code> with the
         *            specified <code>Address</code> exists in our contact list,
         *            a new one will be created.
         * @param eventId
         *            the value of the id tag to be placed in the Event headers
         *            of the NOTIFY requests created for the new instance and to
         *            be present in the received Event headers in order to have
         *            the new instance associated with them
         */
        public PresenceNotifierSubscription(Address fromAddress, String eventId)
        {
            super(fromAddress, eventId);

            // if we received a subscribe, our network probably doesn't have
            // a distant PA
            setUseDistantPA(false);

            // try to find which contact is concerned
            ContactSipImpl contact
                = resolveContactID(fromAddress.getURI().toString());

            // if we don't know him, create him
            if (contact == null)
            {
                contact = new ContactSipImpl(fromAddress, parentProvider);

                // <tricky time>
                // this ensure that we will publish our status to this contact
                // without trying to subscribe to him
                contact.setResolved(true);
                contact.setResolvable(false);
                // </tricky time>
            }

            logger.debug(contact + " wants to watch your presence status");

            this.contact = contact;
        }

        /**
         * Determines whether the <tt>Address</tt>/Request URI of this
         * <tt>Subscription</tt> is equal to a specific <tt>Address</tt> in the
         * sense of identifying one and the same resource.
         *
         * @param address the <tt>Address</tt> to be checked for value equality
         * to the <tt>Address</tt>/Request URI of this <tt>Subscription</tt>
         * @return <tt>true</tt> if the <tt>Address</tt>/Request URI of this
         * <tt>Subscription</tt> is equal to the specified <tt>Address</tt> in
         * the sense of identifying one and the same resource
         * @see EventPackageSupport.Subscription#addressEquals(Address)
         */
        @Override
        protected boolean addressEquals(Address address)
        {
            String addressString = address.getURI().toString();
            String id1 = addressString;
            // without sip:
            String id2 = addressString.substring(4);
            int domainBeginIndex = addressString.indexOf('@');
            // without the domain
            String id3 = addressString.substring(0, domainBeginIndex);
            // without sip: and the domain
            String id4 = addressString.substring(4, domainBeginIndex);

            String contactAddressString = contact.getAddress();

            // test by order of probability to be true will probably save 1ms :)
            return
                contactAddressString.equals(id2)
                    || contactAddressString.equals(id1)
                    || contactAddressString.equals(id4)
                    || contactAddressString.equals(id3);
        }

        /**
         * Creates content for a notify request using the specified
         * <tt>subscriptionState</tt> and <tt>reason</tt> string.
         *
         * @param subscriptionState the state that we'd like to deliver in the
         * newly created <tt>Notify</tt> request.
         * @param reason the reason string that  we'd like to deliver in the
         * newly created <tt>Notify</tt> request.
         *
         * @return the String bytes of the newly created content.
         */
        protected byte[] createNotifyContent(String subscriptionState,
                        String reason)
        {
            return getPidfPresenceStatus(getLocalContactForDst(contact));
        }
    }

    /**
     * Represents a subscription to the presence event package of a specific
     * <code>ContactSipImpl</code>.
     *
     * @author Lubomir Marinov
     */
    private class PresenceSubscriberSubscription
        extends EventPackageSubscriber.Subscription
    {

        /**
         * The <code>ContactSipImpl</code> which is the notifier this
         * subscription is subscribed to.
         */
        private final ContactSipImpl contact;

        /**
         * Initializes a new <code>PresenceSubscriberSubscription</code>
         * instance which is to represent a subscription to the presence event
         * package of a specific <code>ContactSipImpl</code>.
         *
         * @param contact the <code>ContactSipImpl</code> which is the notifier
         * the new subscription is to subscribed to
         *
         * @throws OperationFailedException if we fail extracting
         * <tt>contact</tt>'s address.
         */
        public PresenceSubscriberSubscription(ContactSipImpl contact)
            throws OperationFailedException
        {
            super(OperationSetPresenceSipImpl.this.getAddress(contact));

            this.contact = contact;
        }

        /*
         * Implements
         * EventPackageSubscriber.Subscription#processActiveRequest(RequestEvent
         * , byte[]).
         */
        protected void processActiveRequest(
            RequestEvent requestEvent,
            byte[] rawContent)
        {
            if (rawContent != null)
                setPidfPresenceStatus(new String(rawContent));
        }

        /*
         * Implements
         * EventPackageSubscriber.Subscription#processFailureResponse(
         * ResponseEvent, int).
         */
        protected void processFailureResponse(
            ResponseEvent responseEvent,
            int statusCode)
        {
            // we probably won't be able to communicate with the contact
            changePresenceStatusForContact(
                contact, sipStatusEnum.getStatus(
                    (Response.TEMPORARILY_UNAVAILABLE == statusCode)
                        ? SipStatusEnum.OFFLINE
                        : SipStatusEnum.UNKNOWN));

            // we'll never be able to resolve this contact
            if ((Response.UNAUTHORIZED != statusCode)
                    && (Response.PROXY_AUTHENTICATION_REQUIRED != statusCode))
                contact.setResolvable(false);
        }

        /*
         * Implements
         * EventPackageSubscriber.Subscription#processSuccessResponse(
         * ResponseEvent, int).
         */
        protected void processSuccessResponse(
            ResponseEvent responseEvent,
            int statusCode)
        {
            switch (statusCode)
            {
            case Response.OK:
            case Response.ACCEPTED:
                try
                {
                    if (!contact.isResolved())
                    {
                        // if contact is not in the contact list
                        // create it, and add to parent, later will be resolved
                        if(resolveContactID(contact.getAddress()) == null)
                        {
                            ContactGroup parentGroup =
                                contact.getParentContactGroup();
                            ((ContactGroupSipImpl) parentGroup)
                                .addContact(contact);

                            // pretend that the contact is created
                            fireSubscriptionEvent(
                                contact,
                                parentGroup,
                                SubscriptionEvent.SUBSCRIPTION_CREATED);
                        }

                        finalizeSubscription(contact);
                    }
                }
                catch (NullPointerException e)
                {
                    // should not happen
                    logger.debug(
                        "failed to finalize the subscription of the contact",
                        e);
                }
                break;
            }
        }

        /**
         * Implements the corresponding <tt>SipListener</tt> method by
         * terminating the corresponding subsctiption and polling the related
         * contact.
         *
         * @param requestEvent the event containing the request that was \
         * terminated.
         * @param reasonCode a String indicating the reason of the termination.
         */
        protected void processTerminatedRequest(
                            RequestEvent requestEvent, String reasonCode)
        {
            terminateSubscription(contact);

            // if the reason is "de-activated", we immediately re-subscribe
            // to the contact
            if (SubscriptionStateHeader.DEACTIVATED.equals(reasonCode))
                forcePollContact(contact);
        }
    }
}
