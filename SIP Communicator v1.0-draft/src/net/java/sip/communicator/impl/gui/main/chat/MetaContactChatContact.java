/*
 * SIP Communicator, the OpenSource Java VoIP and Instant Messaging client.
 * 
 * Distributable under LGPL license. See terms of license at gnu.org.
 */
package net.java.sip.communicator.impl.gui.main.chat;

import net.java.sip.communicator.impl.gui.*;
import net.java.sip.communicator.service.contactlist.*;

/**
 * The <tt>MetaContactChatContact</tt> represents a <tt>ChatContact</tt> in a
 * user-to-user chat.
 * 
 * @author Yana Stamcheva
 * @author Lubomir Marinov
 */
public class MetaContactChatContact
    extends ChatContact<MetaContact>
{

    /**
     * Creates an instance of <tt>ChatContact</tt> by passing to it the
     * corresponding <tt>MetaContact</tt> and <tt>Contact</tt>.
     *
     * @param metaContact the <tt>MetaContact</tt> encapsulating the given
     * <tt>Contact</tt>
     */
    public MetaContactChatContact(MetaContact metaContact)
    {
        super(metaContact);
    }

    /*
     * Implements ChatContact#getAvatarBytes(). Delegates to metaContact.
     */
    public byte[] getAvatarBytes()
    {
        return descriptor.getAvatar();
    }

    /**
     * Returns the contact name.
     *
     * @return the contact name
     */
    public String getName()
    {
        String name = descriptor.getDisplayName();

        if (name == null || name.length() < 1)
            name
                = GuiActivator.getResources()
                        .getI18NString("service.gui.UNKNOWN");

        return name;
    }

    /*
     * Implements ChatContact#getUID(). Delegates to MetaContact#getMetaUID()
     * because it's known to be unique.
     */
    public String getUID()
    {
        return descriptor.getMetaUID();
    }
}
