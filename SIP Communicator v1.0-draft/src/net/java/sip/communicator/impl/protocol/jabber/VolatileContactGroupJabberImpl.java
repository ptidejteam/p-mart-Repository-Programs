/*
 * SIP Communicator, the OpenSource Java VoIP and Instant Messaging client.
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package net.java.sip.communicator.impl.protocol.jabber;

import java.util.*;

import net.java.sip.communicator.service.protocol.*;

import org.jivesoftware.smack.*;

/**
 * The Jabber implementation of the Volatile ContactGroup interface.
 *
 * @author Damian Minkov
 */
public class VolatileContactGroupJabberImpl
    extends ContactGroupJabberImpl
{
    /**
     * This contact group name
     */
    private final String contactGroupName;

    /**
     * Creates an Jabber group using the specified group name
     * @param groupName String groupname
     * @param ssclCallback a callback to the server stored contact list
     * we're creating.
     */
    VolatileContactGroupJabberImpl(
                        String groupName,
                        ServerStoredContactListJabberImpl ssclCallback)
    {
        super(null, new Vector<RosterEntry>().iterator(), ssclCallback, false);

        this.contactGroupName = groupName;
    }

    /**
     * Returns the name of this group.
     * @return a String containing the name of this group.
     */
    public String getGroupName()
    {
        return contactGroupName;
    }

    /**
     * Returns a string representation of this group, in the form
     * JabberGroup.GroupName[size]{ buddy1.toString(), buddy2.toString(), ...}.
     * @return  a String representation of the object.
     */
    public String toString()
    {
        StringBuffer buff = new StringBuffer("VolatileJabberGroup.");
        buff.append(getGroupName());
        buff.append(", childContacts="+countContacts()+":[");

        Iterator<Contact> contacts = contacts();
        while (contacts.hasNext())
        {
            Contact contact = contacts.next();
            buff.append(contact.toString());
            if(contacts.hasNext())
                buff.append(", ");
        }
        return buff.append("]").toString();
    }

    /**
     * Determines whether or not this contact group is being stored by the
     * server. Non persistent contact groups exist for the sole purpose of
     * containing non persistent contacts.
     * @return true if the contact group is persistent and false otherwise.
     */
    public boolean isPersistent()
    {
        return false;
    }
}
