/*
 * SIP Communicator, the OpenSource Java VoIP and Instant Messaging client.
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package net.java.sip.communicator.plugin.ircaccregwizz;

import net.java.sip.communicator.service.resources.*;

/**
 * The Messages class manages the access to the internationalization
 * properties files.
 *
 * @author Lionel Ferreira & Michael Tarantino
 */
public class Resources
{
    private static ResourceManagementService resourcesService;
    
    /**
     * A constant pointing to the IRC protocol logo image.
     */
    public static ImageID IRC_LOGO
        = new ImageID("service.protocol.irc.IRC_16x16");

    /**
     * A constant pointing to the IRC protocol wizard page image.
     */
    public static ImageID PAGE_IMAGE
        = new ImageID("service.protocol.irc.IRC_64x64");

    /**
     * Returns an internationalized string corresponding to the given key.
     * @param key The key of the string.
     * @return An internationalized string corresponding to the given key.
     */
    public static String getString(String key)
    {
        return getResources().getI18NString(key);
    }

    /**
     * Loads an image from a given image identifier.
     * @param imageID The identifier of the image.
     * @return The image for the given identifier.
     */
    public static byte[] getImage(ImageID imageID)
    {
        return getResources().getImageInBytes(imageID.getId());
    }

    public static ResourceManagementService getResources()
    {
        if (resourcesService == null)
            resourcesService =
                ResourceManagementServiceUtils
                    .getService(IrcAccRegWizzActivator.bundleContext);
        return resourcesService;
    }
}
