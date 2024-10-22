/*
 * SIP Communicator, the OpenSource Java VoIP and Instant Messaging client.
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package net.java.sip.communicator.impl.contactlist;

import java.io.*;
import java.util.*;

import net.java.sip.communicator.service.contactlist.*;
import net.java.sip.communicator.service.contactlist.event.*;
import net.java.sip.communicator.service.protocol.*;
import net.java.sip.communicator.util.*;

/**
 * A default implementation of the <code>MetaContact</code> interface.
 *
 * @author Emil Ivov
 * @author Lubomir Marinov
 */
public class MetaContactImpl
    implements MetaContact
{
    /**
     * Logger for <tt>MetaContactImpl</tt>.
     */
    private static final Logger logger
        = Logger.getLogger(MetaContactImpl.class);

    /**
     * A vector containing all protocol specific contacts merged in this
     * MetaContact.
     */
    private final List<Contact> protoContacts = new Vector<Contact>();

    /**
     * The number of contacts online in this meta contact.
     */
    private int contactsOnline = 0;

    /**
     * An id uniquely identifying the meta contact in this contact list.
     */
    private final String uid;

    /**
     * Returns a human readable string used by the UI to display the contact.
     */
    private String displayName = "";

    /**
     * The contact that should be chosen by default when communicating with this
     * meta contact.
     */
    private Contact defaultContact = null;

    /**
     * A locally cached copy of an avatar that we should return for lazy calls
     * to the getAvatarMethod() in order to speed up display.
     */
    private byte[] cachedAvatar = null;

    /**
     * A flag that tells us whether or not we have already tried to restore
     * an avatar from cache. We need this to know whether a <tt>null</tt> cached
     * avatar implies that there is no locally stored avatar or that we simply
     * haven't tried to retrieve it. This should allow us to only interrogate
     * the file system if haven't done so before.
     */
    private boolean avatarFileCacheAlreadyQueried = false;

    /**
     * A callback to the meta contact group that is currently our parent. If
     * this is an orphan meta contact that has not yet been added or has been
     * removed from a group this callback is going to be null.
     */
    private MetaContactGroupImpl parentGroup = null;

    /**
     * Hashtable containing the contact details.
     * Name -> Value or Name -> (List of values).
     */
    private Map<String, List<String>> details;

    private final static String AVATAR_DIR = "avatarcache";

    /**
     *  Characters and their replacement in created folder names
     */
    private final static String[][] ESCAPE_SEQUENCES = new String[][]
    {
        {"&", "&_amp"},
        {"/", "&_sl"},
        {"\\\\", "&_bs"},   // the char \
        {":", "&_co"},
        {"\\*", "&_as"},    // the char *
        {"\\?", "&_qm"},    // the char ?
        {"\"", "&_pa"},     // the char "
        {"<", "&_lt"},
        {">", "&_gt"},
        {"\\|", "&_pp"}     // the char |
    };

    /**
     * The user-specific key-value associations stored in this instance.
     * <p>
     * Like the Widget implementation of Eclipse SWT, the storage type takes
     * into account that there are likely to be many
     * <code>MetaContactImpl</code> instances and <code>Map</code>s are thus
     * likely to impose increased memory use. While an array may very well
     * perform worse than a <code>Map</code> with respect to search, the
     * mechanism of user-defined key-value associations explicitly states that
     * it is not guaranteed to be optimized for any particular use and only
     * covers the most basic cases and performance-savvy code will likely
     * implement a more optimized solution anyway.
     * </p>
     */
    private Object[] data;

    /**
     * Creates new meta contact with a newly generated meta contact UID.
     */
    MetaContactImpl()
    {
        //create the uid
        this.uid = String.valueOf(System.currentTimeMillis())
                   + String.valueOf(hashCode());
        this.details = null;
    }

    /**
     * Creates a new meta contact with the specified UID. This constructor
     * MUST ONLY be used when restoring contacts stored in the contactlist.xml.
     *
     * @param metaUID the meta uid that this meta contact should have.
     * @param details the already stored details for the contact.
     */
    MetaContactImpl(String metaUID, Map<String, List<String>> details)
    {
        this.uid = metaUID;
        this.details = details;
    }

    /**
     * Returns the number of protocol specific <tt>Contact</tt>s that this
     * <tt>MetaContact</tt> contains.
     *
     * @return an int indicating the number of protocol specific contacts
     *   merged in this <tt>MetaContact</tt>
     */
    public int getContactCount()
    {
        return protoContacts.size();
    }

    /**
     * Returns a Contact, encapsulated by this MetaContact and coming from
     * the specified ProtocolProviderService.
     * <p>
     * In order to prevent problems with concurrency, the <tt>Iterator</tt>
     * returned by this method is not be over the actual list of contacts but
     * over a copy of that list.
     *
     * @param provider a reference to the <tt>ProtocolProviderService</tt>
     *   that we'd like to get a <tt>Contact</tt> for.
     * @return a <tt>Contact</tt> encapsulated in this <tt>MetaContact</tt>
     *   and originating from the specified provider.
     */
    public Iterator<Contact> getContactsForProvider(
                                    ProtocolProviderService provider)
    {
        LinkedList<Contact> providerContacts = new LinkedList<Contact>();

        for (Contact contact : protoContacts)
        {
            if(contact.getProtocolProvider() == provider)
                providerContacts.add( contact );
        }

        return providerContacts.iterator();
    }

    /**
     * Returns all protocol specific Contacts, encapsulated by this MetaContact
     * and supporting the given <tt>opSetClass</tt>. If none of the
     * contacts encapsulated by this MetaContact is supporting the specified
     * <tt>OperationSet</tt> class then an empty iterator is returned.
     * <p>
     * @param opSetClass the operation for which the default contact is needed
     * @return a <tt>List</tt> over all contacts encapsulated in this
     * <tt>MetaContact</tt> and supporting the specified <tt>OperationSet</tt>
     */
    public List<Contact> getContactsForOperationSet(
                                    Class<? extends OperationSet> opSetClass)
    {
        LinkedList<Contact> opSetContacts = new LinkedList<Contact>();

        for (Contact contact : protoContacts)
        {
            if(contact.getProtocolProvider()
                    .getOperationSet(opSetClass) != null)
                opSetContacts.add( contact );
        }

        return opSetContacts;
    }

    /**
     * Returns contacts, encapsulated by this MetaContact and belonging to
     * the specified protocol ContactGroup.
     * <p>
     * In order to prevent problems with concurrency, the <tt>Iterator</tt>
     * returned by this method is not be over the actual list of contacts but
     * over a copy of that list.
     *
     * @param parentProtoGroup a reference to the <tt>ContactGroup</tt>
     *   whose children we'd like removed..
     * @return an Iterator over all <tt>Contact</tt>s encapsulated in this
     * <tt>MetaContact</tt> and belonging to the specified proto ContactGroup.
     */
    public Iterator<Contact> getContactsForContactGroup(
                                            ContactGroup parentProtoGroup)
    {
        List<Contact> providerContacts = new LinkedList<Contact>();

        for (Contact contact : protoContacts)
        {
            if(contact.getParentContactGroup() == parentProtoGroup)
                providerContacts.add( contact );
        }

        return providerContacts.iterator();
    }


    /**
     * Returns a contact encapsulated by this meta contact, having the specified
     * contactAddress and coming from the indicated ownerProvider.
     * <p>
     * @param contactAddress the address of the contact who we're looking for.
     * @param ownerProvider a reference to the ProtocolProviderService that
     * the contact we're looking for belongs to.
     * @return a reference to a <tt>Contact</tt>, encapsulated by this
     * MetaContact, carrying the specified address and originating from the
     * specified ownerProvider or null if no such contact exists..
     */
    public Contact getContact(String contactAddress,
                              ProtocolProviderService ownerProvider)
    {
        for (Contact contact : protoContacts)
        {
            if(   contact.getProtocolProvider() == ownerProvider
               && contact.getAddress().equals(contactAddress))
                return contact;
        }

        return null;
    }

    /**
     * Returns a contact encapsulated by this meta contact, having the specified
     * contactAddress and coming from a provider with a mathing
     * <tt>accountID</tt>. The method returns null if no such contact exists.
     * <p>
     * @param contactAddress the address of the contact who we're looking for.
     * @param accountID the identifier of the provider that the contact we're
     * looking for must belong to.
     * @return a reference to a <tt>Contact</tt>, encapsulated by this
     * MetaContact, carrying the specified address and originating from the
     * ownerProvider carryign <tt>accountID</tt>.
     */
    public Contact getContact(String contactAddress,
                              String accountID)
    {
        for (Contact contact : protoContacts)
        {
            if(  contact.getProtocolProvider().getAccountID()
                    .getAccountUniqueID().equals(accountID)
               && contact.getAddress().equals(contactAddress))
                return contact;
        }

        return null;
    }

    /**
     * Returns <tt>true</tt> if the given <tt>protocolContact</tt> is contained
     * in this <tt>MetaContact</tt>, otherwise - returns <tt>false</tt>.
     * @param protocolContact the <tt>Contact</tt> we're looking for
     * @return <tt>true</tt> if the given <tt>protocolContact</tt> is contained
     * in this <tt>MetaContact</tt>, otherwise - returns <tt>false</tt>
     */
    public boolean containsContact(Contact protocolContact)
    {
        return protoContacts.contains(protocolContact);
    }

    /**
     * Returns a <tt>java.util.Iterator</tt> over all protocol specific
     * <tt>Contacts</tt> encapsulated by this <tt>MetaContact</tt>.
     * <p>
     * In order to prevent problems with concurrency, the <tt>Iterator</tt>
     * returned by this method is not over the actual list of contacts but over
     * a copy of that list.
     * <p>
     * @return a <tt>java.util.Iterator</tt> over all protocol specific
     * <tt>Contact</tt>s that were registered as subcontacts for this
     * <tt>MetaContact</tt>
     */
    public Iterator<Contact> getContacts()
    {
        return new LinkedList<Contact>(protoContacts).iterator();
    }

    /**
     * Currently simply returns the most connected protocol contact. We should
     * add the possibility to choose it also according to preconfigured
     * preferences.
     *
     * @return the default <tt>Contact</tt> to use when communicating with
     *   this <tt>MetaContact</tt>
     */
    public Contact getDefaultContact()
    {
        if(defaultContact == null)
        {
            PresenceStatus currentStatus = null;
            for (Contact protoContact : protoContacts)
            {
                PresenceStatus contactStatus = protoContact.getPresenceStatus();

                if (currentStatus != null)
                {
                    if (currentStatus.getStatus() < contactStatus.getStatus())
                    {
                        currentStatus = contactStatus;
                        defaultContact = protoContact;
                    }
                }
                else
                {
                    currentStatus = contactStatus;
                    defaultContact = protoContact;
                }
            }
        }
        return defaultContact;
    }

    /**
     * Returns a default contact for a specific operation (call,
     * file transfer, IM ...)
     *
     * @param operationSet the operation for which the default contact is needed
     * @return the default contact for the specified operation.
     */
    public Contact getDefaultContact(Class<? extends OperationSet> operationSet)
    {
        Contact defaultOpSetContact = null;

        Contact defaultContact = getDefaultContact();

        // if the current default contact supports the requested operationSet
        // we use it
        if (defaultContact != null
            && defaultContact.getProtocolProvider()
                .getOperationSet(operationSet) != null)
        {
            defaultOpSetContact = defaultContact;
        }
        else
        {
            PresenceStatus currentStatus = null;

            for (Contact protoContact : protoContacts)
            {
                // we filter to care only about contact which support
                // the needed opset.
                if (protoContact.getProtocolProvider()
                        .getOperationSet(operationSet) != null)
                {
                    PresenceStatus contactStatus
                            = protoContact.getPresenceStatus();

                    if (currentStatus != null)
                    {
                        if (currentStatus.getStatus()
                                < contactStatus.getStatus())
                        {
                            currentStatus = contactStatus;
                            defaultOpSetContact = protoContact;
                        }
                    }
                    else
                    {
                        currentStatus = contactStatus;
                        defaultOpSetContact = protoContact;
                    }
                }
            }
        }
        return defaultOpSetContact;
    }

    /**
     * Returns a String identifier (the actual contents is left to
     * implementations) that uniquely represents this <tt>MetaContact</tt> in
     * the containing <tt>MetaContactList</tt>
     *
     * @return a String uniquely identifying this meta contact.
     */
    public String getMetaUID()
    {
        return uid;
    }

    /**
     * Compares this meta contact with the specified object for order.  Returns
     * a negative integer, zero, or a positive integer as this meta contact is
     * less than, equal to, or greater than the specified object.
     * <p>
     * The result of this method is calculated the following way:
     * <p>
     * (contactsOnline - o.contactsOnline) * 1 000 000  <br>
     * + getDisplayName().compareTo(o.getDisplayName()) * 100 000
     * + getMetaUID().compareTo(o.getMetaUID())<br>
     * <p>
     * Or in other words ordering of meta accounts would be first done by
     * presence status, then display name, and finally (in order to avoid
     * equalities) be the fairly random meta contact metaUID.
     * <p>
     * @param   o the <code>MetaContact</code> to be compared.
     * @return  a negative integer, zero, or a positive integer as this object
     *      is less than, equal to, or greater than the specified object.
     *
     * @throws ClassCastException if the specified object is not
     *          a MetaContactListImpl
     */
    public int compareTo(MetaContact o)
    {
        MetaContactImpl target = (MetaContactImpl) o;

        int isOnline
            = (contactsOnline > 0)
            ? 1
            : 0;
        int targetIsOnline
            = (target.contactsOnline > 0)
            ? 1
            : 0;

        return ( (10 - isOnline) - (10 - targetIsOnline)) * 100000000
            + getDisplayName().compareToIgnoreCase(target.getDisplayName())
            * 10000
            + getMetaUID().compareTo(target.getMetaUID());
    }

    /**
     * Returns a string representation of this contact, containing most of its
     * representative details.
     *
     * @return  a string representation of this contact.
     */
    public String toString()
    {
        StringBuffer buff = new StringBuffer("MetaContact[ DisplayName=")
            .append(getDisplayName()).append("]");

        return buff.toString();
    }


    /**
     * Returns a characteristic display name that can be used when including
     * this <tt>MetaContact</tt> in user interface.
     * @return a human readable String that represents this meta contact.
     */
    public String getDisplayName()
    {
        return displayName;
    }

    /**
     * Queries all protoco contacts in this meta contact for their avatars.
     * Beware that this method could cause multiple network operations.
     * Use with caution.
     *
     * @return a byte array containing the first avatar returned by any of
     * this metacontact's child contacts or <tt>null</tt> if none of them
     * returned an avatar.
     */
    private byte[] queryProtoContactAvatar(Contact contact)
    {
        try
        {
            byte[] contactImage = contact.getImage();

            if (contactImage != null && contactImage.length > 0)
            {
                return contactImage;
            }
        }
        catch (Exception ex)
        {
            logger.error("Failed to load contact photo.", ex);
        }

        return null;
    }

    /**
     * Returns the avatar of this contact, that can be used when including this
     * <tt>MetaContact</tt> in user interface. The isLazy parameter would tell
     * the implementation if it could return the locally stored avatar or it
     * should obtain the avatar right from the server.
     *
     * @param isLazy Indicates if this method should return the locally stored
     * avatar or it should obtain the avatar right from the server.
     * @return an avatar (e.g. user photo) of this contact.
     */
    public byte[] getAvatar(boolean isLazy)
    {
        byte[] result = null;
        if (!isLazy)
        {
            // the caller is willing to perform a lengthy operation so let's
            // query the proto contacts for their avatars.
            Iterator<Contact> protoContacts = getContacts();

            while( protoContacts.hasNext())
            {
                Contact contact = protoContacts.next();
                result = queryProtoContactAvatar(contact);

                // if we got a result from the above, then let's cache and
                // return it.
                if (result != null && result.length > 0)
                {
                    cacheAvatar(contact, result);
                    return result;
                }
            }
        }

        //if we get here then the caller is probably not willing to perform
        //network operations and opted for a lazy retrieve (... or the
        //queryAvatar method returned null because we are calling it too often)
        if(cachedAvatar != null
           && cachedAvatar.length > 0)
        {
            //we already have a cached avatar, so let's return it
            return cachedAvatar;
        }

        //no cached avatar. let'r try the file system for previously stored
        //ones. (unless we already did this)
        if ( avatarFileCacheAlreadyQueried )
            return null;

        avatarFileCacheAlreadyQueried = true;

        Iterator<Contact> iter = this.getContacts();
        while (iter.hasNext())
        {
            Contact protoContact = iter.next();

            String accountUID = escapeSpecialCharacters(protoContact
                .getProtocolProvider().getAccountID().getAccountUniqueID());

            String avatarPath = AVATAR_DIR
                + File.separator
                + accountUID
                + File.separator
                + escapeSpecialCharacters(protoContact.getAddress());

            cachedAvatar = getLocallyStoredAvatar(avatarPath);

            if(cachedAvatar != null)
                return cachedAvatar;
        }

        return null;
    }

    /**
     * Returns an avatar that can be used when presenting this
     * <tt>MetaContact</tt> in user interface. The method would also make sure
     * that we try the network for new versions of avatars.
     *
     * @return an avatar (e.g. user photo) of this contact.
     */
    public byte[] getAvatar()
    {
        return getAvatar(false);
    }

    /**
     * Sets a name that can be used when displaying this contact in user
     * interface components.
     * @param displayName a human readable String representing this
     * <tt>MetaContact</tt>
     */
    void setDisplayName(String displayName)
    {
        synchronized (getParentGroupModLock())
        {
            if (parentGroup != null)
            {
                parentGroup.lightRemoveMetaContact(this);
            }

            this.displayName = (displayName == null) ? "" : displayName;

            if (parentGroup != null)
            {
                parentGroup.lightAddMetaContact(this);
            }
        }
    }

    /**
     * Adds the specified protocol specific contact to the list of contacts
     * merged in this meta contact. The method also keeps up to date the
     * contactsOnline field which is used in the compareTo() method.
     *
     * @param contact the protocol specific Contact to add.
     */
    void addProtoContact(Contact contact)
    {
        synchronized (getParentGroupModLock())
        {
            if (parentGroup != null)
            {
                parentGroup.lightRemoveMetaContact(this);
            }
            contactsOnline += contact.getPresenceStatus().isOnline() ? 1 : 0;

            this.protoContacts.add(contact);

            // Re-init the default contact.
            defaultContact = null;

            // if this is our firt contact and we don't already have a display
            // name, use theirs.
            if (this.protoContacts.size() == 1
                && (this.displayName == null || this.displayName.trim()
                    .length() == 0))
            {
                // be careful not to use setDisplayName() here cause this will
                // bring us into a deadlock.
                this.displayName = contact.getDisplayName();
            }

            if (parentGroup != null)
            {
                parentGroup.lightAddMetaContact(this);
            }
        }
    }

    /**
     * Called by MetaContactListServiceImpl after a contact has changed its
     * status, so that ordering in the parent group is updated. The method also
     * elects the most connected contact as default contact.
     *
     * @return the new index at which the contact was added.
     */
    int reevalContact()
    {
        synchronized (getParentGroupModLock())
        {
            //first lightremove or otherwise we won't be able to get hold of the
            //contact
            if (parentGroup != null)
            {
                parentGroup.lightRemoveMetaContact(this);
            }

            this.contactsOnline = 0;
            int maxContactStatus = 0;

            for (Contact contact : protoContacts)
            {
                int contactStatus = contact.getPresenceStatus()
                        .getStatus();

                if(maxContactStatus < contactStatus)
                {
                    maxContactStatus = contactStatus;
                    this.defaultContact = contact;
                }
                if (contact.getPresenceStatus().isOnline())
                    contactsOnline++;
            }
            //now read it and the contact would be automatically placed
            //properly by the containing group
            if (parentGroup != null)
            {
                return parentGroup.lightAddMetaContact(this);
            }
        }

        return -1;
    }

    /**
     * Removes the specified protocol specific contact from the contacts
     * encapsulated in this <code>MetaContact</code>. The method also updates
     * the total status field accordingly. And updates its ordered position
     * in its parent group. If the display name of this <code>MetaContact</code>
     * was the one of the removed contact, we update it.
     *
     * @param contact the contact to remove
     */
    void removeProtoContact(Contact contact)
    {
        synchronized (getParentGroupModLock())
        {
            if (parentGroup != null)
            {
                parentGroup.lightRemoveMetaContact(this);
            }
            contactsOnline -= contact.getPresenceStatus().isOnline() ? 1 : 0;
            this.protoContacts.remove(contact);

            if (defaultContact == contact)
            {
                defaultContact = null;
            }

            if ((protoContacts.size() > 0)
                    && displayName.equals(contact.getDisplayName()))
            {
                displayName = getDefaultContact().getDisplayName();
            }

            if (parentGroup != null)
            {
                parentGroup.lightAddMetaContact(this);
            }
        }
    }

    /**
     * Removes all proto contacts that belong to the specified provider.
     *
     * @param provider the provider whose contacts we want removed.
     *
     * @return true if this <tt>MetaContact</tt> was modified and false
     * otherwise.
     */
    boolean removeContactsForProvider(ProtocolProviderService provider)
    {
        boolean modified = false;
        Iterator<Contact> contactsIter = protoContacts.iterator();

        while(contactsIter.hasNext())
        {
            Contact contact = contactsIter.next();

            if (contact.getProtocolProvider() == provider)
            {
                contactsIter.remove();
                modified = true;
            }
        }

        // if the default contact has been modified, set it to null
        if (modified && !protoContacts.contains(defaultContact))
        {
            defaultContact = null;
        }

        return modified;
    }

    /**
     * Removes all proto contacts that belong to the specified protocol group.
     *
     * @param protoGroup the group whose children we want removed.
     *
     * @return true if this <tt>MetaContact</tt> was modified and false
     * otherwise.
     */
    boolean removeContactsForGroup(ContactGroup protoGroup)
    {
        boolean modified = false;
        Iterator<Contact> contactsIter = protoContacts.iterator();

        while(contactsIter.hasNext())
        {
            Contact contact = contactsIter.next();

            if (contact.getParentContactGroup() == protoGroup)
            {
                contactsIter.remove();
                modified = true;
            }
        }

        // if the default contact has been modified, set it to null
        if (modified && !protoContacts.contains(defaultContact))
        {
            defaultContact = null;
        }

        return modified;
    }


    /**
     * Sets <tt>parentGroup</tt> as a parent of this meta contact. Do not
     * call this method with a null argument even if a group is removing
     * this contact from itself as this could lead to race conditions (imagine
     * another group setting itself as the new parent and you removing it).
     * Use unsetParentGroup instead.
     *
     * @param parentGroup the <tt>MetaContactGroupImpl</tt> that is currently a
     * parent of this meta contact.
     * @throws NullPointerException if <tt>parentGroup</tt> is null.
     */
    void setParentGroup(MetaContactGroupImpl parentGroup)
    {
        if (parentGroup == null)
            throw new NullPointerException("Do not call this method with a "
                + "null argument even if a group is removing this contact "
                + "from itself as this could lead to race conditions "
                + "(imagine another group setting itself as the new "
                + "parent and you  removing it). Use unsetParentGroup "
                + "instead.");

        synchronized (getParentGroupModLock())
        {
            this.parentGroup = parentGroup;
        }
    }

    /**
     * If <tt>parentGroup</tt> was the parent of this meta contact then it
     * sets it to null. Call this method when removing this contact from a
     * meta contact group.
     * @param parentGrp the <tt>MetaContactGroupImpl</tt> that we don't want
     * considered as a parent of this contact any more.
     */
    void unsetParentGroup(MetaContactGroupImpl parentGrp)
    {
        synchronized(getParentGroupModLock())
        {
            if (parentGroup == parentGrp)
                parentGroup = null;
        }
    }

    /**
     * Returns the group that is currently holding this meta contact.
     *
     * @return the group that is currently holding this meta contact.
     */
    MetaContactGroupImpl getParentGroup()
    {
        return parentGroup;
    }

    /**
     * Returns the MetaContactGroup currently containing this meta contact
     * @return a reference to the MetaContactGroup currently containing this
     * meta contact.
     */
    public MetaContactGroup getParentMetaContactGroup()
    {
        return getParentGroup();
    }

    /**
     * Adds a custom detail to this contact.
     * @param name name of the detail.
     * @param value the value of the detail.
     */
    public void addDetail(String name, String value)
    {
        if (details == null)
            details = new Hashtable<String, List<String>>();

        List<String> values = details.get(name);

        if(values == null)
        {
            values = new ArrayList<String>();
            details.put(name, values);
        }

        values.add(value);

        fireMetaContactModified(name, null, value);
    }

    /**
     * Remove the given detail.
     * @param name of the detail to be removed.
     * @param value value of the detail to be removed.
     */
    public void removeDetail(String name, String value)
    {
        if (details == null)
            return;

        List<String> values = details.get(name);
        if(values == null)
            return;

        values.remove(value);

        fireMetaContactModified(name, value, null);
    }

    /**
     * Remove all details with given name.
     * @param name of the details to be removed.
     */
    public void removeDetails(String name)
    {
        if (details == null)
            return;

        Object removed = details.remove(name);

        fireMetaContactModified(name, removed, null);
    }

    /**
     * Change the detail.
     * @param name of the detail to be changed.
     * @param oldValue the old value of the detail.
     * @param newValue the new value of the detail.
     */
    public void changeDetail(String name, String oldValue, String newValue)
    {
        if (details == null)
            return;

        List<String> values = details.get(name);
        if(values == null)
            return;

        int changedIx = values.indexOf(oldValue);
        if(changedIx == -1)
            return;

        values.set(changedIx, newValue);

        fireMetaContactModified(name, oldValue, newValue);
    }

    private void fireMetaContactModified(
            String modificationName,
            Object oldValue,
            Object newValue)
    {
        MetaContactGroupImpl parentGroup = getParentGroup();

        if (parentGroup != null)
            parentGroup
                .getMclServiceImpl()
                    .fireMetaContactEvent(
                        new MetaContactModifiedEvent(
                                this,
                                modificationName,
                                oldValue,
                                newValue));
    }

    /**
     * Get all details with given name.
     * @param name the name of the details we are searching.
     */
    public List<String> getDetails(String name)
    {
        List<String> values = (details == null) ? null : details.get(name);

        if(values == null)
            values = new ArrayList<String>();
        else
            values = new ArrayList<String>(values);

        return values;
    }

    /**
     * Stores avatar bytes in the given <tt>Contact</tt>.
     *
     * @param protoContact The contact in which we store the avatar.
     * @param avatarBytes The avatar image bytes.
     */
    public void cacheAvatar( Contact protoContact,
                             byte[] avatarBytes)
    {
        this.cachedAvatar = avatarBytes;
        this.avatarFileCacheAlreadyQueried = true;

        String accountUID = protoContact.getProtocolProvider()
            .getAccountID().getAccountUniqueID();

        accountUID = escapeSpecialCharacters(accountUID);

        String avatarDirPath = AVATAR_DIR
            + File.separator
            + accountUID;

        String escapedProtocolId
            = escapeSpecialCharacters(protoContact.getAddress());

        File avatarDir = null;
        File avatarFile = null;
        try
        {
            avatarDir = ContactlistActivator.getFileAccessService()
                .getPrivatePersistentDirectory(avatarDirPath);

            avatarFile
                = ContactlistActivator.getFileAccessService()
                    .getPrivatePersistentFile(
                        avatarDirPath
                        + File.separator
                        + escapedProtocolId);

            if(!avatarFile.exists())
            {
                if (!avatarDir.exists())
                    if (!avatarDir.mkdirs())
                        throw new IOException("Failed to create directory: "
                            + avatarDir.getAbsolutePath());

                if (!avatarFile.createNewFile())
                    throw new IOException("Failed to create file"
                                          + avatarFile.getAbsolutePath());
            }

            FileOutputStream fileOutStream = new FileOutputStream(avatarFile);

            fileOutStream.write(avatarBytes);
            fileOutStream.flush();
            fileOutStream.close();
        }
        catch (Exception e)
        {
            logger.error("Failed to store avatar. dir ="
                            + avatarDir
                            + " file="
                            + avatarFile, e);
        }
    }

    /**
     * Returns the avatar image corresponding to the given avatar path.
     *
     * @param avatarPath The path to the lovally stored avatar.
     * @return the avatar image corresponding to the given avatar path.
     */
    private byte[] getLocallyStoredAvatar(String avatarPath)
    {
        try
        {
            File avatarFile = ContactlistActivator.getFileAccessService()
                .getPrivatePersistentFile(avatarPath);

            if(avatarFile.exists())
            {
                FileInputStream fileInStream = new FileInputStream(avatarFile);

                byte[] bs = new byte[fileInStream.available()];
                fileInStream.read(bs);
                fileInStream.close();

                return bs;
            }
        }
        catch (Exception e)
        {
            logger.error("Could not read avatar image", e);
        }

        return null;
    }

    /**
     * Replacing the characters that we must escape
     * used for the created filename.
     *
     * @param id
     */
    private String escapeSpecialCharacters(String id)
    {
        String resultId = id;
        for (int j = 0; j < ESCAPE_SEQUENCES.length; j++)
        {
            resultId = resultId.
                replaceAll(ESCAPE_SEQUENCES[j][0], ESCAPE_SEQUENCES[j][1]);
        }

        return resultId;
    }

    /*
     * Implements MetaContact#getData(Object).
     */
    public Object getData(Object key)
    {
        if (key == null)
            throw new NullPointerException("key");

        int index = dataIndexOf(key);

        return (index == -1) ? null : data[index + 1];
    }

    /*
     * Implements MetaContact#setData(Object, Object).
     */
    public void setData(Object key, Object value)
    {
        if (key == null)
            throw new NullPointerException("key");

        int index = dataIndexOf(key);

        if (index == -1)
        {

            /*
             * If value is null, remove the association with key (or just don't
             * add it).
             */
            if (data == null)
                if (value != null)
                    data = new Object[] { key, value };
            else if (value == null)
            {
                int length = data.length - 2;

                if (length > 0)
                {
                    Object[] newData = new Object[length];

                    System.arraycopy(data, 0, newData, 0, index);
                    System.arraycopy(
                        data, index + 2, newData, index, length - index);
                    data = newData;
                }
                else
                    data = null;
            }
            else
            {
                int length = data.length;
                Object[] newData = new Object[length + 2];

                System.arraycopy(data, 0, newData, 0, length);
                data[length++] = key;
                data[length++] = value;
                data = newData;
            }
        }
        else
            data[index + 1] = value;
    }

    /**
     * Determines the index in <code>#data</code> of a specific key.
     *
     * @param key
     *            the key to retrieve the index in <code>#data</code> of
     * @return the index in <code>#data</code> of the specified <code>key</code>
     *         if it is contained; <tt>-1</tt> if <code>key</code> is not
     *         contained in <code>#data</code>
     */
    private int dataIndexOf(Object key)
    {
        if (data != null)
            for (int index = 0; index < data.length; index += 2)
                if (key.equals(data[index]))
                    return index;
        return -1;
    }

    /**
     * Gets the sync lock for use when modifying {@link #parentGroup}.
     *
     * @return the sync lock for use when modifying {@link #parentGroup}
     */
    private Object getParentGroupModLock()
    {
        /*
         * XXX The use of uid as parentGroupModLock is a bit unusual but a
         * dedicated lock enlarges the shallow runtime size of this instance and
         * having hundreds of MetaContactImpl instances is not unusual for a
         * multi-protocol application. With respect to parentGroupModLock being
         * unique among the MetaContactImpl instances, uid is fine because it is
         * also supposed to be unique in the same way.
         */
        return uid;
    }
}
