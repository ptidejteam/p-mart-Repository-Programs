/*
 * SIP Communicator, the OpenSource Java VoIP and Instant Messaging client.
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package net.java.sip.communicator.impl.protocol.sip;

import java.util.*;

import org.osgi.framework.*;

import net.java.sip.communicator.service.configuration.*;
import net.java.sip.communicator.service.fileaccess.*;
import net.java.sip.communicator.service.gui.*;
import net.java.sip.communicator.service.neomedia.*;
import net.java.sip.communicator.service.netaddr.*;
import net.java.sip.communicator.service.protocol.*;
import net.java.sip.communicator.service.version.*;
import net.java.sip.communicator.util.*;

/**
 * Activates the SIP package
 * @author Emil Ivov
 */
public class SipActivator
    implements BundleActivator
{
    private Logger logger = Logger.getLogger(SipActivator.class.getName());

    private        ServiceRegistration  sipPpFactoryServReg   = null;
            static BundleContext        bundleContext         = null;
    private static ConfigurationService configurationService  = null;
    private static NetworkAddressManagerService networkAddressManagerService
                                                              = null;
    private static FileAccessService    fileService           = null;
    private static MediaService         mediaService          = null;
    private static VersionService       versionService        = null;
    private static UIService            uiService             = null;

    private static ProtocolProviderFactorySipImpl sipProviderFactory = null;

    private UriHandlerSipImpl uriHandlerSipImpl = null;

    /**
     * Called when this bundle is started so the Framework can perform the
     * bundle-specific activities necessary to start this bundle.
     *
     * @param context The execution context of the bundle being started.
     * @throws Exception If this method throws an exception, this bundle is
     *   marked as stopped and the Framework will remove this bundle's
     *   listeners, unregister all services registered by this bundle, and
     *   release all services used by this bundle.
     */
    public void start(BundleContext context) throws Exception
    {
        logger.debug("Started.");

        SipActivator.bundleContext = context;

        sipProviderFactory = new ProtocolProviderFactorySipImpl();

        /*
         * Install the UriHandler prior to registering the factory service in
         * order to allow it to detect when the stored accounts are loaded
         * (because they may be asynchronously loaded).
         */
        uriHandlerSipImpl = new UriHandlerSipImpl(sipProviderFactory);

        //reg the sip account man.
        Dictionary<String, String> properties = new Hashtable<String, String>();
        properties.put(ProtocolProviderFactory.PROTOCOL, ProtocolNames.SIP);
        sipPpFactoryServReg =  context.registerService(
                    ProtocolProviderFactory.class.getName(),
                    sipProviderFactory,
                    properties);

        logger.debug("SIP Protocol Provider Factory ... [REGISTERED]");
    }

    /**
     * Returns a reference to a ConfigurationService implementation currently
     * registered in the bundle context or null if no such implementation was
     * found.
     *
     * @return a currently valid implementation of the ConfigurationService.
     */
    public static ConfigurationService getConfigurationService()
    {
        if(configurationService == null)
        {
            ServiceReference confReference
                = bundleContext.getServiceReference(
                    ConfigurationService.class.getName());
            configurationService
                = (ConfigurationService) bundleContext.getService(confReference);
        }
        return configurationService;
    }

    /**
     * Returns a reference to a FileAccessService implementation currently
     * registered in the bundle context or null if no such implementation was
     * found.
     *
     * @return a currently valid implementation of the FileAccessService.
     */
    public static FileAccessService getFileAccessService()
    {
        if(fileService == null)
        {
            ServiceReference fileReference
                = bundleContext.getServiceReference(
                    FileAccessService.class.getName());
            fileService
                = (FileAccessService) bundleContext.getService(fileReference);
        }
        return fileService;
    }

    /**
     * Returns a reference to a NetworkAddressManagerService implementation
     * currently registered in the bundle context or null if no such
     * implementation was found.
     *
     * @return a currently valid implementation of the
     * NetworkAddressManagerService .
     */
    public static NetworkAddressManagerService getNetworkAddressManagerService()
    {
        if(networkAddressManagerService == null)
        {
            ServiceReference confReference
                = bundleContext.getServiceReference(
                    NetworkAddressManagerService.class.getName());
            networkAddressManagerService = (NetworkAddressManagerService)
                bundleContext.getService(confReference);
        }
        return networkAddressManagerService;
    }


    /**
     * Returns a reference to the bundle context that we were started with.
     * @return a reference to the BundleContext instance that we were started
     * witn.
     */
    public static BundleContext getBundleContext()
    {
        return bundleContext;
    }

    /**
     * Retrurns a reference to the protocol provider factory that we have
     * registered.
     * @return a reference to the <tt>ProtocolProviderFactorySipImpl</tt>
     * instance that we have registered from this package.
     */
    public static ProtocolProviderFactorySipImpl getProtocolProviderFactory()
    {
        return sipProviderFactory;
    }

    /**
     * Returns a reference to a MediaService implementation currently registered
     * in the bundle context or null if no such implementation was found.
     *
     * @return a reference to a MediaService implementation currently registered
     * in the bundle context or null if no such implementation was found.
     */
    public static MediaService getMediaService()
    {
        if(mediaService == null)
        {
            ServiceReference mediaServiceReference
                = bundleContext.getServiceReference(
                    MediaService.class.getName());
            mediaService = (MediaService)bundleContext
                .getService(mediaServiceReference);
        }
        return mediaService;
    }

    /**
     * Returns a reference to a VersionService implementation currently registered
     * in the bundle context or null if no such implementation was found.
     *
     * @return a reference to a VersionService implementation currently registered
     * in the bundle context or null if no such implementation was found.
     */
    public static VersionService getVersionService()
    {
        if(versionService == null)
        {
            ServiceReference versionServiceReference
                = bundleContext.getServiceReference(
                    VersionService.class.getName());
            versionService = (VersionService)bundleContext
                .getService(versionServiceReference);
        }
        return versionService;
    }

    /**
     * Returns a reference to the UIService implementation currently registered
     * in the bundle context or null if no such implementation was found.
     *
     * @return a reference to a UIService implementation currently registered
     * in the bundle context or null if no such implementation was found.
     */
    public static UIService getUIService()
    {
        if(uiService == null)
        {
            ServiceReference uiServiceReference
                = bundleContext.getServiceReference(
                    UIService.class.getName());
            uiService = (UIService)bundleContext
                .getService(uiServiceReference);
        }
        return uiService;
    }

    /**
     * Called when this bundle is stopped so the Framework can perform the
     * bundle-specific activities necessary to stop the bundle.
     *
     * @param context The execution context of the bundle being stopped.
     * @throws Exception If this method throws an exception, the bundle is
     *   still marked as stopped, and the Framework will remove the bundle's
     *   listeners, unregister all services registered by the bundle, and
     *   release all services used by the bundle.
     */
    public void stop(BundleContext context) throws Exception
    {
        sipProviderFactory.stop();
        sipPpFactoryServReg.unregister();

        if (uriHandlerSipImpl != null)
        {
            uriHandlerSipImpl.dispose();
            uriHandlerSipImpl = null;
        }
    }
}
