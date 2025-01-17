/*
 * SIP Communicator, the OpenSource Java VoIP and Instant Messaging client.
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package net.java.sip.communicator.impl.protocol.icq;

import java.util.*;

import net.java.sip.communicator.service.protocol.*;

/**
 * A dummy ContactGroup implementation representing the ContactList root for
 * ICQ contact lists.
 * @author Emil Ivov
 */
public class RootContactGroupIcqImpl
    extends AbstractContactGroupIcqImpl
{
    private String ROOT_CONTACT_GROUP_NAME = "ContactListRoot";
    private List<ContactGroup> subGroups = new LinkedList<ContactGroup>();
    private boolean isResolved = false;

    /**
     * An empty list that we use when returning an iterator.
     */
    private List<Contact> dummyContacts = new LinkedList<Contact>();

    private final ProtocolProviderServiceIcqImpl protocolProvider;

    /**
     * Creates a ContactGroup instance.
     */
    RootContactGroupIcqImpl(ProtocolProviderServiceIcqImpl protocolProvider)
    {
        this.protocolProvider = protocolProvider;
    }

    /**
     * The ContactListRoot in ICQ is the only group that can contain subgroups.
     *
     * @return true (always)
     */
    public boolean canContainSubgroups()
    {
        return true;
    }

    /**
     * Returns the name of this group which is always
     * <tt>ROOT_CONTACT_GROUP_NAME</tt>.
     *
     * @return a String containing the name of this group.
     */
    public String getGroupName()
    {
        return ROOT_CONTACT_GROUP_NAME;
    }

    /**
     * Adds the specified group at the specified position in the list of sub
     * groups.
     *
     * @param index the position at which the specified group should be added.
     * @param group the ContactGroup to add
     */
    void addSubGroup(int index, ContactGroupIcqImpl group)
    {
        subGroups.add(index, group);
    }

    /**
     * Adds the specified group to the end of the list of sub groups.
     * @param group the group to add.
     */
    void addSubGroup(ContactGroupIcqImpl group)
    {
        addSubGroup(countContacts() + countSubgroups(), group);
    }

    /**
     * Removes the specified from the list of sub groups
     * @param group the group to remove.
     */
    void removeSubGroup(ContactGroupIcqImpl group)
    {
        removeSubGroup(subGroups.indexOf(group));
    }

    /**
     * Removes the sub group with the specified index.
     * @param index the index of the group to remove
     */
    void removeSubGroup(int index)
    {
        subGroups.remove(index);
    }

    /**
     * Removes all contact sub groups and reinsterts them as specified
     * by the <tt>newOrder</tt> param. Contact groups not contained in the
     * newOrder list are left at the end of this group.
     *
     * @param newOrder a list containing all contact groups in the order that is
     * to be applied.
     *
     */
    void reorderSubGroups(List<ContactGroupIcqImpl> newOrder)
    {
        subGroups.removeAll(newOrder);
        subGroups.addAll(0, newOrder);
    }

    /**
     * Returns the number of subgroups contained by this
     * <tt>RootContactGroupIcqImpl</tt>.
     *
     * @return an int indicating the number of subgroups that this
     *   ContactGroup contains.
     */
    public int countSubgroups()
    {
        return subGroups.size();
    }

    /**
     * Returns null as this is the root contact group.
     * @return null as this is the root contact group.
     */
    public ContactGroup getParentContactGroup()
    {
        return null;
    }

    /**
     * Returns the subgroup with the specified index.
     *
     * @param index the index of the <tt>ContactGroup</tt> to retrieve.
     * @return the <tt>ContactGroup</tt> with the specified index.
     */
    public ContactGroup getGroup(int index)
    {
        return (ContactGroupIcqImpl)subGroups.get(index);
    }

    /**
     * Returns the subgroup with the specified name.
     * @param groupName the name of the <tt>ContactGroup</tt> to retrieve.
     * @return the <tt>ContactGroup</tt> with the specified index.
     */
    public ContactGroup getGroup(String groupName)
    {
        Iterator<ContactGroup> subgroups = subgroups();
        while (subgroups.hasNext())
        {
            ContactGroup grp = subgroups.next();

            if (grp.getGroupName().equals(groupName))
                return grp;
        }

        return null;
    }

    /**
     * Returns the <tt>Contact</tt> with the specified address or
     * identifier.
     * @param id the addres or identifier of the <tt>Contact</tt> we are
     * looking for.
     * @return the <tt>Contact</tt> with the specified id or address.
     */
    public Contact getContact(String id)
    {
        //no contacts in the root group for this icq impl.
        return null;
    }

    /**
     * Returns an iterator over the sub groups that this
     * <tt>ContactGroup</tt> contains.
     *
     * @return a java.util.Iterator over the <tt>ContactGroup</tt>
     *   children of this group (i.e. subgroups).
     */
    public Iterator<ContactGroup> subgroups()
    {
        return subGroups.iterator();
    }

    /**
     * Returns the number, which is always 0, of <tt>Contact</tt> members
     * of this <tt>ContactGroup</tt>
     * @return an int indicating the number of <tt>Contact</tt>s, members
     * of this <tt>ContactGroup</tt>.
     */
    public int countContacts()
    {
        return 0;
    }

    /**
     * Returns an Iterator over all contacts, member of this
     * <tt>ContactGroup</tt>.
     * @return a java.util.Iterator over all contacts inside this
     * <tt>ContactGroup</tt>
     */
    public Iterator<Contact> contacts()
    {
        return dummyContacts.iterator();
    }

    /**
     * Returns a string representation of the root contact group that contains
     * all subgroups and subcontacts of this group.
     *
     * @return  a string representation of this root contact group.
     */
    public String toString()
    {
        StringBuffer buff = new StringBuffer(getGroupName());
        buff.append(".subGroups="+countSubgroups()+":\n");

        Iterator<ContactGroup> subGroups = subgroups();
        while (subGroups.hasNext())
        {
            ContactGroup group = subGroups.next();
            buff.append(group.toString());
            if(subGroups.hasNext())
                buff.append("\n");
        }
        return buff.toString();
    }

    /**
     * Returns the protocol provider that this group belongs to.
     * @return a regerence to the ProtocolProviderService instance that this
     * ContactGroup belongs to.
     */
    public ProtocolProviderService getProtocolProvider()
    {
        return protocolProvider;
    }

    /**
     * Determines whether or not this contact group is being stored by the
     * server. Non persistent contact groups exist for the sole purpose of
     * containing non persistent contacts.
     * @return true if the contact group is persistent and false otherwise.
     */
    public boolean isPersistent()
    {
        return true;
    }

    /**
     * Returns null as no persistent data is required and the group name is
     * sufficient for restoring the contact.
     * <p>
     * @return null as no such data is needed.
     */
    public String getPersistentData()
    {
        return null;
    }

    /**
     * Determines whether or not this group has been resolved against the
     * server. Unresolved groups are used when initially loading a contact
     * list that has been stored in a local file until the presence operation
     * set has managed to retrieve all the contact list from the server and has
     * properly mapped groups to their on-line buddies.
     * @return true if the group has been resolved (mapped against a buddy)
     * and false otherwise.
     */
    public boolean isResolved()
    {
        return isResolved;
    }

    /**
     * Returns a <tt>String</tt> that uniquely represnets the group. In this we
     * use the name of the group as an identifier. This may cause problems
     * though, in clase the name is changed by some other application between
     * consecutive runs of the sip-communicator.
     *
     * @return a String representing this group in a unique and persistent
     * way.
     */
    public String getUID()
    {
        return getGroupName();
    }

}
