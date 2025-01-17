/*
 * SIP Communicator, the OpenSource Java VoIP and Instant Messaging client.
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package net.java.sip.communicator.impl.protocol.jabber;

import java.io.*;
import java.net.*;
import java.security.*;
import java.security.cert.*;
import java.text.*;
import java.util.*;
import javax.net.ssl.*;

import net.java.sip.communicator.service.protocol.*;
import net.java.sip.communicator.service.protocol.event.*;
import net.java.sip.communicator.service.protocol.jabberconstants.*;
import net.java.sip.communicator.util.*;
import net.java.sip.communicator.impl.protocol.jabber.sasl.*;
import net.java.sip.communicator.service.gui.*;

import org.jivesoftware.smack.*;
import org.jivesoftware.smack.packet.*;
import org.jivesoftware.smack.util.*;
import org.jivesoftware.smackx.*;
import org.jivesoftware.smackx.packet.*;

import org.osgi.framework.*;

/**
 * An implementation of the protocol provider service over the Jabber protocol
 *
 * @author Damian Minkov
 * @author Symphorien Wanko
 * @author Lubomir Marinov
 * @author Yana Stamcheva
 */
public class ProtocolProviderServiceJabberImpl
    extends AbstractProtocolProviderService
{
    /**
     * Logger of this class
     */
    private static final Logger logger =
        Logger.getLogger(ProtocolProviderServiceJabberImpl.class);

    /**
     * The name of the property that tells us whether we are supposed to start
     * experimental support for Jingle.
     */
    private static final String PNAME_ENABLE_JINGLE
        = "net.java.sip.communicator"
            + ".impl.protocol.jabber.ENABLE_EXPERIMENTAL_JINGLE";

    /**
     * Used to connect to a XMPP server.
     */
    private XMPPConnection connection = null;

    /**
     * Indicates whether or not the provider is initialized and ready for use.
     */
    private boolean isInitialized = false;

    /**
     * We use this to lock access to initialization.
     */
    private final Object initializationLock = new Object();

    /**
     * The identifier of the account that this provider represents.
     */
    private AccountID accountID = null;

    /**
     * Used when we need to re-register
     */
    private SecurityAuthority authority = null;

    /**
     * True if we are reconnecting, false otherwise.
     */
    private boolean reconnecting = false;

    /**
     * The icon corresponding to the jabber protocol.
     */
    private ProtocolIconJabberImpl jabberIcon;

    /**
     * A set of features supported by our Jabber implementation.
     * In general, we add new feature(s) when we add new operation sets.
     * (see xep-0030 : http://www.xmpp.org/extensions/xep-0030.html#info).
     * Example : to tell the world that we support jingle, we simply have
     * to do :
     * supportedFeatures.add("http://www.xmpp.org/extensions/xep-0166.html#ns");
     * Beware there is no canonical mapping between op set and jabber features
     * (op set is a SC "concept"). This means that one op set in SC can
     * correspond to many jabber features. It is also possible that there is no
     * jabber feature corresponding to a SC op set or again,
     * we can currently support some features wich do not have a specific
     * op set in SC (the mandatory feature :
     * http://jabber.org/protocol/disco#info is one example).
     * We can find features corresponding to op set in the xep(s) related
     * to implemented functionality.
     */
    private final List<String> supportedFeatures = new ArrayList<String>();

    /**
     * The <tt>ServiceDiscoveryManager</tt> is responsible for advertising
     * <tt>supportedFeatures</tt> when asked by a remote client. It can also
     * be used to query remote clients for supported features.
     */
    private ServiceDiscoveryManager discoveryManager = null;

    /**
     * The statuses.
     */
    private JabberStatusEnum jabberStatusEnum;

    /**
     * The service we use to interact with user.
     */
    private CertificateVerificationService guiVerification;

    /**
     * 
     */
    private boolean additionalCertificateChecks = true;

    /**
     * The key store holding stored certificate during previous sessions.
     */
    private KeyStore keyStore;

    /**
     * The property for the configuration value to store the
     * KeyStore file location.
     */
    private static final String KEYSTORE_FILE_PROP =
        "net.java.sip.communicator.impl.protocol.sip.net.KEYSTORE";

    /**
     * The default password used for the keystore.
     */
    private char[] defaultPassword = new char[0];

    /**
     * Used with tls connecting when certificates are not trusted
     * and we ask the user to confirm connection. When some timeout expires
     * connect method returns, and we use abortConnecting to abort further
     * execution cause after user chooses we make further processing from there.
     */
    private boolean abortConnecting = false;

    /**
     * Used with tls. When certificate was confirmed we abort current
     * connecting and try connecting again.
     */
    private boolean abortConnectingAndReconnect = false;

    /**
     * This are the certificates which are temporally allowed
     * only for this session.
     */
    private ArrayList<X509Certificate> temporalyAllowed =
            new ArrayList<X509Certificate>();

    /**
     * Returns the state of the registration of this protocol provider
     * @return the <tt>RegistrationState</tt> that this provider is
     * currently in or null in case it is in a unknown state.
     */
    public RegistrationState getRegistrationState()
    {
        if(connection == null)
            return RegistrationState.UNREGISTERED;
        else if(connection.isConnected() && connection.isAuthenticated())
            return RegistrationState.REGISTERED;
        else
            return RegistrationState.UNREGISTERED;
    }

    /**
     * Return the certificate verification service impl.
     * @return the CertificateVerification service.
     */
    private CertificateVerificationService getCertificateVerificationService()
    {
        if(guiVerification == null)
        {
            ServiceReference guiVerifyReference
                = JabberActivator.getBundleContext().getServiceReference(
                    CertificateVerificationService.class.getName());
            if(guiVerifyReference != null)
                guiVerification = (CertificateVerificationService)
                    JabberActivator.getBundleContext().getService(
                        guiVerifyReference);
        }

        return guiVerification;
    }
        

    /**
     * Starts the registration process. Connection details such as
     * registration server, user name/number are provided through the
     * configuration service through implementation specific properties.
     *
     * @param authority the security authority that will be used for resolving
     *        any security challenges that may be returned during the
     *        registration or at any moment while we're registered.
     * @throws OperationFailedException with the corresponding code it the
     * registration fails for some reason (e.g. a networking error or an
     * implementation problem).
     */
    public void register(final SecurityAuthority authority)
        throws OperationFailedException
    {
        if(authority == null)
            throw new IllegalArgumentException(
                "The register method needs a valid non-null authority impl "
                + " in order to be able and retrieve passwords.");

        this.authority = authority;

        try
        {
            connectAndLogin(authority,
                            SecurityAuthority.AUTHENTICATION_REQUIRED);
        }
        catch (XMPPException ex)
        {
            logger.error("Error registering", ex);

            int reason
                = RegistrationStateChangeEvent.REASON_NOT_SPECIFIED;

            RegistrationState regState = RegistrationState.UNREGISTERED;

            if(ex.getWrappedThrowable() instanceof UnknownHostException)
            {
                reason
                    = RegistrationStateChangeEvent.REASON_SERVER_NOT_FOUND;
                regState = RegistrationState.CONNECTION_FAILED;
            }
            else
                if((connection.getSASLAuthentication() != null &&
                    connection.getSASLAuthentication().isAuthenticated()) ||
                    !connection.isAuthenticated())
                {
                    JabberActivator.getProtocolProviderFactory().
                        storePassword(getAccountID(), null);
                    reason = RegistrationStateChangeEvent
                        .REASON_AUTHENTICATION_FAILED;

                    regState = RegistrationState.AUTHENTICATION_FAILED;

                    // Try to reregister and to ask user for a new password.
                    reregister(SecurityAuthority.WRONG_PASSWORD);
                }

            fireRegistrationStateChanged(
                getRegistrationState(), regState, reason, null);
        }
    }

    /**
     * Connects and logins again to the server.
     *
     * @param authReasonCode indicates the reason of the re-authentication.
     */
    void reregister(int authReasonCode)
    {
        try
        {
            logger.trace("Trying to reregister us!");

            // sets this if any is tring to use us through registration
            // to know we are not registered
            this.unregister(false);

            this.reconnecting = true;

            connectAndLogin(authority,
                            authReasonCode);
        }
        catch(OperationFailedException ex)
        {
            logger.error("Error ReRegistering", ex);

            fireRegistrationStateChanged(getRegistrationState(),
                RegistrationState.CONNECTION_FAILED,
                RegistrationStateChangeEvent.REASON_INTERNAL_ERROR, null);
        }
        catch (XMPPException ex)
        {
            logger.error("Error ReRegistering", ex);

            int reason =
                RegistrationStateChangeEvent.REASON_NOT_SPECIFIED;

            if(ex.getWrappedThrowable() instanceof UnknownHostException)
                reason =
                    RegistrationStateChangeEvent.REASON_SERVER_NOT_FOUND;

            fireRegistrationStateChanged(getRegistrationState(),
                RegistrationState.CONNECTION_FAILED, reason, null);
        }
    }

    /**
     * Connects and logins to the server
     * @param authority SecurityAuthority
     * @param reasonCode the authentication reason code. Indicates the reason of
     * this authentication.
     * @throws XMPPException if we cannot connect to the server - network problem
     * @throws  OperationFailedException if login parameters
     *          as server port are not correct
     */
    private synchronized void connectAndLogin(SecurityAuthority authority,
                                              int reasonCode)
        throws XMPPException, OperationFailedException
    {
        synchronized(initializationLock)
        {
            //verify whether a password has already been stored for this account
            String password = JabberActivator.
                getProtocolProviderFactory().loadPassword(getAccountID());

            //decode
            if (password == null)
            {
                //create a default credentials object
                UserCredentials credentials = new UserCredentials();
                credentials.setUserName(getAccountID().getUserID());

                //request a password from the user
                credentials = authority.obtainCredentials(
                    ProtocolNames.JABBER,
                    credentials,
                    reasonCode);

                // in case user has canceled the login window
                if(credentials == null)
                {
                    fireRegistrationStateChanged(
                        getRegistrationState(),
                        RegistrationState.UNREGISTERED,
                        RegistrationStateChangeEvent.REASON_USER_REQUEST, "");
                    return;
                }

                //extract the password the user passed us.
                char[] pass = credentials.getPassword();

                // the user didn't provide us a password (canceled the operation)
                if(pass == null)
                {
                    fireRegistrationStateChanged(
                        getRegistrationState(),
                        RegistrationState.UNREGISTERED,
                        RegistrationStateChangeEvent.REASON_USER_REQUEST, "");
                    return;
                }
                password = new String(pass);


                if (credentials.isPasswordPersistent())
                {
                    JabberActivator.getProtocolProviderFactory()
                        .storePassword(getAccountID(), password);
                }
            }

            //init the necessary objects
            try
            {
                //XMPPConnection.DEBUG_ENABLED = true;
                String userID =
                    StringUtils.parseName(getAccountID().getUserID());
                String serviceName =
                    StringUtils.parseServer(getAccountID().getUserID());

                String serverAddress =
                    getAccountID().getAccountPropertyString(
                        ProtocolProviderFactory.SERVER_ADDRESS);

                String serverPort =
                    getAccountID().getAccountPropertyString(
                        ProtocolProviderFactory.SERVER_PORT);

                String accountResource =
                    getAccountID().getAccountPropertyString(
                        ProtocolProviderFactory.RESOURCE);

                // check to see is there SRV records for this server domain
                try
                {
                    InetSocketAddress srvAddress = NetworkUtils
                        .getSRVRecord("xmpp-client", "tcp", serviceName);

                    if (srvAddress != null)
                        serverAddress = srvAddress.getHostName();

                }
                catch (ParseException ex1)
                {
                    logger.error("Domain not resolved " + ex1.getMessage());
                }

                Roster.setDefaultSubscriptionMode(Roster.SubscriptionMode.manual);

                try
                {
                    //Getting global proxy information from configuration files
                    org.jivesoftware.smack.proxy.ProxyInfo proxy = null;
                    String globalProxyType =
                        JabberActivator.getConfigurationService()
                        .getString(ProxyInfo.CONNECTON_PROXY_TYPE_PROPERTY_NAME);
                    if(globalProxyType == null || 
                       globalProxyType.equals(ProxyInfo.ProxyType.NONE.name()))
                    {
                        proxy = org.jivesoftware.smack.proxy.ProxyInfo
                            .forNoProxy();
                    }
                    else
                    {
                        String globalProxyAddress =
                            JabberActivator.getConfigurationService().getString(
                            ProxyInfo.CONNECTON_PROXY_ADDRESS_PROPERTY_NAME);
                        String globalProxyPortStr =
                            JabberActivator.getConfigurationService().getString(
                            ProxyInfo.CONNECTON_PROXY_PORT_PROPERTY_NAME);
                        int globalProxyPort;
                        try
                        {
                            globalProxyPort = Integer.parseInt(
                                globalProxyPortStr);
                        }
                        catch(NumberFormatException ex)
                        {
                            throw new OperationFailedException("Wrong port",
                                OperationFailedException.INVALID_ACCOUNT_PROPERTIES,
                                ex);
                        }
                        String globalProxyUsername =
                            JabberActivator.getConfigurationService().getString(
                            ProxyInfo.CONNECTON_PROXY_USERNAME_PROPERTY_NAME);
                        String globalProxyPassword =
                            JabberActivator.getConfigurationService().getString(
                            ProxyInfo.CONNECTON_PROXY_PASSWORD_PROPERTY_NAME);
                        if(globalProxyAddress == null ||
                            globalProxyAddress.length() <= 0)
                        {
                            throw new OperationFailedException(
                                "Missing Proxy Address",
                                OperationFailedException.INVALID_ACCOUNT_PROPERTIES);
                        }
                        if(globalProxyType.equals(
                            ProxyInfo.ProxyType.HTTP.name()))
                        {
                            proxy = org.jivesoftware.smack.proxy.ProxyInfo
                                .forHttpProxy(
                                    globalProxyAddress,
                                    globalProxyPort,
                                    globalProxyUsername,
                                    globalProxyPassword);
                        }
                        else if(globalProxyType.equals(
                            ProxyInfo.ProxyType.SOCKS4.name()))
                        {
                             proxy = org.jivesoftware.smack.proxy.ProxyInfo
                                 .forSocks4Proxy(
                                    globalProxyAddress,
                                    globalProxyPort,
                                    globalProxyUsername,
                                    globalProxyPassword);
                        }
                        else if(globalProxyType.equals(
                            ProxyInfo.ProxyType.SOCKS5.name()))
                        {
                             proxy = org.jivesoftware.smack.proxy.ProxyInfo
                                 .forSocks5Proxy(
                                    globalProxyAddress,
                                    globalProxyPort,
                                    globalProxyUsername,
                                    globalProxyPassword);
                        }
                    }

                    ConnectionConfiguration confConn =
                    new ConnectionConfiguration(
                            serverAddress,
                            Integer.parseInt(serverPort),
                            serviceName,
                            proxy
                    );
                    confConn.setReconnectionAllowed(false);
                    confConn.setExpiredCertificatesCheckEnabled(
                        additionalCertificateChecks);
                    confConn.setNotMatchingDomainCheckEnabled(
                        additionalCertificateChecks);

                    TrustManagerFactory tmFactory = null;
                    try
                    {
                        keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
                        keyStore.load(null, defaultPassword);

                        String keyStoreFile =
                            JabberActivator.getConfigurationService()
                            .getString(KEYSTORE_FILE_PROP);

                        if(keyStoreFile == null || keyStoreFile.length() == 0)
                        {
                            File f = JabberActivator.getFileAccessService()
                                .getPrivatePersistentFile("jssecacerts");
                            keyStoreFile = f.getCanonicalPath();

                            JabberActivator.getConfigurationService()
                                .setProperty(KEYSTORE_FILE_PROP, keyStoreFile);

                            keyStore.store(
                                new FileOutputStream(f), defaultPassword);
                        }
                        else
                        {
                            File f = new File(keyStoreFile);
                            if(!f.exists())
                            {
                                // if for some reason file is missing, create it
                                // by saving the empty store
                                keyStore.store(
                                    new FileOutputStream(f), defaultPassword);
                            }

                            keyStore.load(new FileInputStream(keyStoreFile), null);
                        }

                        String algorithm = KeyManagerFactory.getDefaultAlgorithm();
                        tmFactory = TrustManagerFactory.getInstance(algorithm);
                        tmFactory.init(keyStore);
                        confConn.setKeystorePath(keyStoreFile);
                    } catch (Exception e)
                    {
                        logger.error("Cannot create key store", e);
                    }

                    connection = new XMPPConnection(confConn);
                    if(tmFactory != null)
                    {
                        connection.setCustomTrustManager(
                            new HostTrustManager(
                            (X509TrustManager)tmFactory.getTrustManagers()[0],
                            serverAddress,
                            Integer.parseInt(serverPort)));
                    }

                    connection.connect();

                    connection.addConnectionListener(
                        new JabberConnectionListener());
                }
                catch (XMPPException exc)
                {
                    if (logger.isInfoEnabled()) 
                    {
                        logger.info("Failed to establish a Jabber connection for "
                                + getAccountID().getAccountUniqueID(), exc);
                    }

                    throw new OperationFailedException(
                        "Failed to establish a Jabber connection for "
                        + getAccountID().getAccountUniqueID()
                        , OperationFailedException.NETWORK_FAILURE
                        , exc);
                }

                if(abortConnecting)
                {
                    abortConnecting = false;
                    if(abortConnectingAndReconnect)
                    {
                        abortConnectingAndReconnect = false;
                        reregister(SecurityAuthority.CONNECTION_FAILED);
                    }
                    else
                    {
                        connection.disconnect();
                    }
                    return;
                }

                fireRegistrationStateChanged(
                        getRegistrationState()
                        , RegistrationState.REGISTERING
                        , RegistrationStateChangeEvent.REASON_NOT_SPECIFIED
                        , null);

                if(accountResource == null || accountResource.equals(""))
                    accountResource = "sip-comm";

                SASLAuthentication.supportSASLMechanism("PLAIN", 0);

                // Insert our sasl mechanism implementation
                // in order to support some incompatable servers
                SASLAuthentication.unregisterSASLMechanism("DIGEST-MD5");
                SASLAuthentication.registerSASLMechanism("DIGEST-MD5",
                    SASLDigestMD5Mechanism.class);
                SASLAuthentication.supportSASLMechanism("DIGEST-MD5");

                try
                {
                    connection.login(userID, password, accountResource);
                } catch (XMPPException e1)
                {
                    // after updating to new smack lib
                    // login mechanisum changed
                    // this is a way to avoid the problem
                    try
                    {
                        // server disconnect us after such un error
                        // cleanup
                        try
                        {
                            connection.disconnect();
                        } catch (Exception e)
                        {}
                        // and connect again
                        connection.connect();

                        // as disconnect clears all listeners lets add it again
                        connection.addConnectionListener(
                            new JabberConnectionListener());

                        if(abortConnecting)
                        {
                            abortConnecting = false;
                            if(abortConnectingAndReconnect)
                            {
                                abortConnectingAndReconnect = false;
                                reregister(SecurityAuthority.CONNECTION_FAILED);
                            }
                            else
                            {
                                connection.disconnect();
                            }
                            return;
                        }

                        fireRegistrationStateChanged(
                            getRegistrationState()
                            , RegistrationState.REGISTERING
                            , RegistrationStateChangeEvent.REASON_NOT_SPECIFIED
                            , null);

                        // logging in to google need and service name
                        connection.login(userID + "@" + serviceName,
                                password, accountResource);
                    } catch (XMPPException e2)
                    {
                        // if it happens once again throw the original exception
                        throw e1;
                    }
                }

                if(connection.isAuthenticated())
                {
                    this.reconnecting = false;

                    connection.getRoster().
                        setSubscriptionMode(Roster.SubscriptionMode.manual);

                    fireRegistrationStateChanged(
                        getRegistrationState(),
                        RegistrationState.REGISTERED,
                        RegistrationStateChangeEvent.REASON_NOT_SPECIFIED, null);
                }
                else
                {
                    fireRegistrationStateChanged(
                        getRegistrationState()
                        , RegistrationState.UNREGISTERED
                        , RegistrationStateChangeEvent.REASON_NOT_SPECIFIED
                        , null);
                }

            }
            catch (NumberFormatException ex)
            {
                throw new OperationFailedException("Wrong port",
                    OperationFailedException.INVALID_ACCOUNT_PROPERTIES, ex);
            }
        }

        // we setup supported features
        // List of features that smack already supports:
        // http://jabber.org/protocol/xhtml-im
        // http://jabber.org/protocol/muc
        // http://jabber.org/protocol/commands
        // http://jabber.org/protocol/chatstates
        // http://jabber.org/protocol/si/profile/file-transfer
        // http://jabber.org/protocol/si
        // http://jabber.org/protocol/bytestreams
        // http://jabber.org/protocol/ibb
        if (getRegistrationState() == RegistrationState.REGISTERED)
        {
            discoveryManager = ServiceDiscoveryManager.
                    getInstanceFor(connection);

            ServiceDiscoveryManager.setIdentityName("sip-comm");
            ServiceDiscoveryManager.setIdentityType("pc");
            Iterator<String> it = supportedFeatures.iterator();

            // Remove features supported by smack, but not supported in
            // SIP Communicator.
            discoveryManager.removeFeature(
                "http://jabber.org/protocol/commands");

            // Add features the SIP Communicator supports in plus of smack.
            while (it.hasNext())
            {
                String feature = it.next();

                if (!discoveryManager.includesFeature(feature))
                    discoveryManager.addFeature(feature);
            }
        }

    }

    /**
     * Ends the registration of this protocol provider with the service.
     */
    public void unregister()
    {
        unregister(true);
    }

    /**
     * Unregister and fire the event if requested
     * @param fireEvent boolean
     */
    private void unregister(boolean fireEvent)
    {
        RegistrationState currRegState = getRegistrationState();

        if(connection != null && connection.isConnected())
            connection.disconnect();

        if(fireEvent)
        {
            fireRegistrationStateChanged(
                currRegState,
                RegistrationState.UNREGISTERED,
                RegistrationStateChangeEvent.REASON_USER_REQUEST, null);
        }
    }

    /**
     * Returns the short name of the protocol that the implementation of this
     * provider is based upon (like SIP, Jabber, ICQ/AIM, or others for
     * example).
     *
     * @return a String containing the short name of the protocol this
     *   service is taking care of.
     */
    public String getProtocolName()
    {
        return ProtocolNames.JABBER;
    }

    /**
     * Initialized the service implementation, and puts it in a sate where it
     * could interoperate with other services. It is strongly recommended that
     * properties in this Map be mapped to property names as specified by
     * <tt>AccountProperties</tt>.
     *
     * @param screenname the account id/uin/screenname of the account that
     * we're about to create
     * @param accountID the identifier of the account that this protocol
     * provider represents.
     *
     * @see net.java.sip.communicator.service.protocol.AccountID
     */
    protected void initialize(String screenname,
                              AccountID accountID)
    {
        synchronized(initializationLock)
        {
            this.accountID = accountID;

            String protocolIconPath =
                accountID
                    .getAccountPropertyString(ProtocolProviderFactory.PROTOCOL_ICON_PATH);
            if (protocolIconPath == null)
            {
                protocolIconPath = "resources/images/protocol/jabber";
            }

            jabberIcon = new ProtocolIconJabberImpl(protocolIconPath);

            jabberStatusEnum
                = JabberStatusEnum.getJabberStatusEnum(protocolIconPath);

            //this feature is mandatory to be compliant with Service Discovery
            supportedFeatures.add("http://jabber.org/protocol/disco#info");

            String keepAliveStrValue =
                accountID.getAccountPropertyString("SEND_KEEP_ALIVE");

            String resourcePriority
                = accountID
                    .getAccountPropertyString(
                        ProtocolProviderFactory.RESOURCE_PRIORITY);

            //initialize the presence operationset
            OperationSetPersistentPresenceJabberImpl persistentPresence =
                new OperationSetPersistentPresenceJabberImpl(this);

            if(resourcePriority != null)
            {
                persistentPresence
                    .setResourcePriority(Integer.parseInt(resourcePriority));
                // TODO : is this resource priority related to xep-0168
                // (Resource Application Priority) ?
                // see http://www.xmpp.org/extensions/xep-0168.html
                // If the answer is no, comment the following lines please
                supportedFeatures.add(
                        "http://www.xmpp.org/extensions/xep-0168.html#ns");
            }

            addSupportedOperationSet(
                OperationSetPersistentPresence.class,
                persistentPresence);
            // TODO: add the feature, if any, corresponding to persistent
            // presence, if someone knows
            // supportedFeatures.add(_PRESENCE_);

            //register it once again for those that simply need presence
            addSupportedOperationSet(
                OperationSetPresence.class,
                persistentPresence);

            //initialize the IM operation set
            OperationSetBasicInstantMessagingJabberImpl basicInstantMessaging =
                new OperationSetBasicInstantMessagingJabberImpl(this);

            if (keepAliveStrValue != null)
                basicInstantMessaging.setKeepAliveEnabled(Boolean
                    .parseBoolean(keepAliveStrValue));

            addSupportedOperationSet(
                OperationSetBasicInstantMessaging.class,
                basicInstantMessaging);

            // The http://jabber.org/protocol/xhtml-im feature is included
            // already in smack.

            //initialize the Whiteboard operation set
            addSupportedOperationSet(
                OperationSetWhiteboarding.class,
                new OperationSetWhiteboardingJabberImpl(this));

            //initialize the typing notifications operation set
            addSupportedOperationSet(
                OperationSetTypingNotifications.class,
                new OperationSetTypingNotificationsJabberImpl(this));

            // The http://jabber.org/protocol/chatstates feature implemented in
            // OperationSetTypingNotifications is included already in smack.

            //initialize the multi user chat operation set
            addSupportedOperationSet(
                OperationSetMultiUserChat.class,
                new OperationSetMultiUserChatJabberImpl(this));

            InfoRetreiver infoRetreiver = new InfoRetreiver(this, screenname);

            addSupportedOperationSet(
                OperationSetServerStoredContactInfo.class,
                new OperationSetServerStoredContactInfoJabberImpl(
                        infoRetreiver));

            OperationSetServerStoredAccountInfo accountInfo =
                new OperationSetServerStoredAccountInfoJabberImpl(this,
                        infoRetreiver,
                        screenname);
            
            addSupportedOperationSet(OperationSetServerStoredAccountInfo.class,
                accountInfo);

            // Initialize avatar operation set
            OperationSetAvatar avatarOpSet =
                new OperationSetAvatarJabberImpl(this, accountInfo);
            addSupportedOperationSet(OperationSetAvatar.class, avatarOpSet);
            
            // initialize the file transfer operation set
            addSupportedOperationSet(
                OperationSetFileTransfer.class,
                new OperationSetFileTransferJabberImpl(this));

            addSupportedOperationSet(
                OperationSetInstantMessageTransform.class,
                new OperationSetInstantMessageTransformImpl());

            // Include features we're supporting in plus of the four that
            // included by smack itself:
            // http://jabber.org/protocol/si/profile/file-transfer
            // http://jabber.org/protocol/si
            // http://jabber.org/protocol/bytestreams
            // http://jabber.org/protocol/ibb
            supportedFeatures.add("urn:xmpp:thumbs:0");
            supportedFeatures.add("urn:xmpp:bob");

            // initialize the thumbnailed file factory operation set
            addSupportedOperationSet(
                OperationSetThumbnailedFileFactory.class,
                new OperationSetThumbnailedFileFactoryImpl());

            // TODO: this is the "main" feature to advertise when a client
            // support muc. We have to add some features for
            // specific functionality we support in muc.
            // see http://www.xmpp.org/extensions/xep-0045.html

            // The http://jabber.org/protocol/muc feature is already included in
            // smack.
            supportedFeatures.add("http://jabber.org/protocol/muc#rooms");
            supportedFeatures.add("http://jabber.org/protocol/muc#traffic");

            //check if we are supposed to start telephony

            //initialize the telephony opset
//            boolean enableJingle
//                = JabberActivator
//                    .getConfigurationService()
//                        .getBoolean(PNAME_ENABLE_JINGLE, false);
//            if(enableJingle && JabberActivator.getMediaService() != null)
//            {
//                addSupportedOperationSet(
//                    OperationSetBasicTelephony.class,
//                    new OperationSetBasicTelephonyJabberImpl(this));
//
//                // Add Jingle features to supported features.
//                supportedFeatures.add("urn:xmpp:jingle:1");
//                supportedFeatures.add("urn:xmpp:jingle:apps:rtp:1");
//                supportedFeatures.add("urn:xmpp:jingle:apps:rtp:audio");
//
//                //one day:
//                //supportedFeatures.add("urn:xmpp:jingle:apps:rtp:video");
//            }

            isInitialized = true;
        }
    }

    /**
     * Makes the service implementation close all open sockets and release
     * any resources that it might have taken and prepare for
     * shutdown/garbage collection.
     */
    public void shutdown()
    {
        synchronized(initializationLock)
        {
            logger.trace("Killing the Jabber Protocol Provider.");

            //kill all active calls
            OperationSetBasicTelephonyJabberImpl telephony
                = (OperationSetBasicTelephonyJabberImpl)getOperationSet(
                    OperationSetBasicTelephony.class);
            if (telephony != null)
            {
                telephony.shutdown();
            }

            if(connection != null)
            {
                connection.disconnect();
                connection = null;
            }
            isInitialized = false;
        }
    }

    /**
     * Returns true if the provider service implementation is initialized and
     * ready for use by other services, and false otherwise.
     *
     * @return true if the provider is initialized and ready for use and false
     * otherwise
     */
    public boolean isInitialized()
    {
        return isInitialized;
    }

    /**
     * Returns the AccountID that uniquely identifies the account represented
     * by this instance of the ProtocolProviderService.
     * @return the id of the account represented by this provider.
     */
    public AccountID getAccountID()
    {
        return accountID;
    }

    /**
     * Returns the <tt>XMPPConnection</tt>opened by this provider
     * @return a reference to the <tt>XMPPConnection</tt> last opened by this
     * provider.
     */
    protected XMPPConnection getConnection()
    {
        return connection;
    }

    /**
     * Enable to listen for jabber connection events
     */
    private class JabberConnectionListener
        implements ConnectionListener
    {
        /**
         * Implements <tt>connectionClosed</tt> from <tt>ConnectionListener</tt>
         */
        public void connectionClosed()
        {
            OperationSetPersistentPresenceJabberImpl opSetPersPresence =
                (OperationSetPersistentPresenceJabberImpl)
                    getOperationSet(OperationSetPersistentPresence.class);

            opSetPersPresence.fireProviderStatusChangeEvent(
                opSetPersPresence.getPresenceStatus(),
                getJabberStatusEnum().getStatus(JabberStatusEnum.OFFLINE));
        }

        /**
         * Implements <tt>connectionClosedOnError</tt> from
         * <tt>ConnectionListener</tt>.
         *
         * @param exception contains information on the error.
         */
        public void connectionClosedOnError(Exception exception)
        {
            logger.error("connectionClosedOnError " +
                         exception.getLocalizedMessage());

            if(exception instanceof XMPPException)
            {
                StreamError err = ((XMPPException)exception).getStreamError();

                if(err != null && err.getCode().equals(
                    XMPPError.Condition.conflict.toString()))
                {
                    fireRegistrationStateChanged(getRegistrationState(),
                        RegistrationState.CONNECTION_FAILED,
                        RegistrationStateChangeEvent.REASON_MULTIPLE_LOGINS,
                        "Connecting multiple times with the same resource");
                    return;
                }
            } // Ignore certificate exceptions as we handle them elsewhere
            else if(exception instanceof SSLHandshakeException &&
                exception.getCause() instanceof CertificateException)
            {
                return;
            }

            if(!reconnecting)
                reregister(SecurityAuthority.CONNECTION_FAILED);
            else
                reconnecting = false;
        }

        /**
         * Implements <tt>reconnectingIn</tt> from <tt>ConnectionListener</tt>
         *
         * @param i delay in seconds for reconnection.
         */
        public void reconnectingIn(int i)
        {
            logger.info("reconnectingIn " + i);
        }

        /**
         * Implements <tt>reconnectingIn</tt> from <tt>ConnectionListener</tt>
         */
        public void reconnectionSuccessful()
        {
            logger.info("reconnectionSuccessful");
        }

        /**
         * Implements <tt>reconnectionFailed</tt> from
         * <tt>ConnectionListener</tt>.
         *
         * @param exception description of the failure
         */
        public void reconnectionFailed(Exception exception)
        {
            logger.info("reconnectionFailed ", exception);
        }
    }

    /**
     * Returns the jabber protocol icon.
     * @return the jabber protocol icon
     */
    public ProtocolIcon getProtocolIcon()
    {
        return jabberIcon;
    }

    /**
     * Returns the current instance of <tt>JabberStatusEnum</tt>.
     *
     * @return the current instance of <tt>JabberStatusEnum</tt>.
     */
    JabberStatusEnum getJabberStatusEnum()
    {
        return jabberStatusEnum;
    }

    /**
     * Determines if the given list of <tt>features</tt> is supported by the
     * specified jabber id.
     *
     * @param jid the jabber id for which to check
     * @param features the list of features to check for
     *
     * @return <code>true</code> if the list of features is supported, otherwise
     * returns <code>false</code>
     */
    public boolean isFeatureListSupported(String jid, String[] features)
    {
        boolean isFeatureListSupported = true;

        ServiceDiscoveryManager disco = ServiceDiscoveryManager
            .getInstanceFor(getConnection());
        try
        {
            DiscoverInfo featureInfo = disco.discoverInfo(jid);

            for (String feature : features)
            {
                if (!featureInfo.containsFeature(feature))
                {
                    // If one is not supported we return false and don't check
                    // the others.
                    isFeatureListSupported = false;
                    break;
                }
            }
        }
        catch (XMPPException e)
        {
            logger.debug("Failed to discover info.", e);
        }

        return isFeatureListSupported;
    }

    /**
     * Determines if the given list of <tt>features</tt> is supported by the
     * specified jabber id.
     *
     * @param jid the jabber id that we'd like to get information about
     * @param feature the feature to check for
     *
     * @return <tt>true</tt> if the list of features is supported, otherwise
     * returns <tt>false</tt>
     */
    public boolean isFeatureSupported(String jid, String feature)
    {
        return isFeatureListSupported(jid, new String[]{feature});
    }

    /**
     * Returns the full jabber id (jid) corresponding to the given contact.
     *
     * @param contact the contact, for which we're looking for a jid
     * @return the jid of the specified contact;
     */
    String getFullJid(Contact contact)
    {
        Roster roster = getConnection().getRoster();
        Presence presence = roster.getPresence(contact.getAddress());

        return presence.getFrom();
    }

    /**
     * The trust manager which asks the client whether to trust particular
     * certificate which is not globally trusted.
     */
    private class HostTrustManager
        implements X509TrustManager
    {
        /**
         * The address we connect to.
         */
        String address;

        /**
         * The port we connect to.
         */
        int port;

        /**
         * The default trust manager.
         */
        private final X509TrustManager tm;

        /**
         * Creates the custom trust manager.
         * @param tm the default trust manager.
         * @param address the address we are connecting to.
         * @param port the port.
         */
        HostTrustManager(X509TrustManager tm, String address, int port)
        {
            this.tm = tm;
            this.port = port;
            this.address = address;
        }

        /**
         * Not used.
         * @return
         */
        public X509Certificate[] getAcceptedIssuers() {
            throw new UnsupportedOperationException();
        }

        /**
         * Not used.
         * @param chain the cert chain.
         * @param authType authentication type like: RSA.
         * @throws CertificateException
         */
        public void checkClientTrusted(X509Certificate[] chain, String authType)
            throws CertificateException
        {
            throw new UnsupportedOperationException();
        }

        /**
         * Check whether a certificate is trusted, if not as user whether he
         * trust it.
         * @param chain the certificate chain.
         * @param authType authentication type like: RSA.
         * @throws CertificateException not trusted.
         */
        public void checkServerTrusted(X509Certificate[] chain, String authType)
            throws CertificateException
        {
            if(JabberActivator.getConfigurationService().getBoolean(
                CertificateVerificationService.ALWAYS_TRUST_MODE_ENABLED_PROP_NAME,
                false))
                return;
            try
            {
                tm.checkServerTrusted(chain, authType);
            } catch (Throwable certificateException)
            {
                try
                {
                    for (int i = 0; i < chain.length; i++)
                    {
                        X509Certificate c = chain[i];

                        // check for temporaly allowed certs
                        if(temporalyAllowed.contains(c))
                        {
                            return;
                        }

                        // now check for permanent allow of certs
                        String alias = keyStore.getCertificateAlias(c);
                        if(alias != null)
                            return;
                    }

                    CertificateVerificationService guiVerification =
                        getCertificateVerificationService();

                    if(guiVerification == null)
                        throw new CertificateException(certificateException.getMessage());

                    abortConnecting = true;
                    int result = guiVerification
                        .verificationNeeded(chain, address, port);

                    if(result == CertificateVerificationService.DO_NOT_TRUST)
                    {
                        fireRegistrationStateChanged(getRegistrationState(),
                            RegistrationState.CONNECTION_FAILED,
                            RegistrationStateChangeEvent.REASON_USER_REQUEST,
                            "Not trusted certificate");
                        return;
                    }
                    else if(result
                        == CertificateVerificationService.TRUST_THIS_SESSION_ONLY)
                    {
                        for (X509Certificate c : chain)
                            temporalyAllowed.add(c);  
                    }
                    else if(result == CertificateVerificationService.TRUST_ALWAYS)
                    {
                        for (X509Certificate c : chain)
                            keyStore.setCertificateEntry(
                                String.valueOf(System.currentTimeMillis()), c);
                    }

                    try
                    {
                        String keyStoreFile = JabberActivator.getConfigurationService()
                            .getString(KEYSTORE_FILE_PROP);
                        keyStore.store(
                            new FileOutputStream(keyStoreFile), defaultPassword);
                    } catch (Exception e)
                    {
                        logger.error("Error saving keystore.", e);
                    }

                    if(abortConnecting)
                    {
                        abortConnectingAndReconnect = true;
                        return;
                    }
                    else
                    {
                        // register.connect in new thread so we can release the
                        // current connecting thread, otherwise this blocks
                        // jabber
                        new Thread(new Runnable()
                        {
                            public void run()
                            {
                                reregister(SecurityAuthority.CONNECTION_FAILED);
                            }
                        }).start();
                        return;
                    }
                } catch (KeyStoreException e)
                {
                    // something happend
                    logger.error("Error trying to " +
                        "show certificate to user", e);

                    throw new CertificateException(certificateException.getMessage());
                }
            }
        }
    }
}
