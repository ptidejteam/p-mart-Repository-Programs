/*
 * SIP Communicator, the OpenSource Java VoIP and Instant Messaging client.
 *
 * Distributable under LGPL license. See terms of license at gnu.org.
 */
package net.java.sip.communicator.impl.gui.main.account;

import java.awt.event.*;
import java.beans.*;
import java.util.*;

import javax.swing.*;

import net.java.sip.communicator.impl.gui.*;
import net.java.sip.communicator.service.protocol.*;
import net.java.sip.communicator.service.protocol.event.*;

import org.osgi.framework.*;

/**
 * The <tt>AccountList</tt> is the list of currently registered accounts shown
 * in the options form.
 * 
 * @author Yana Stamcheva
 */
public class AccountList
    extends JList
    implements  ProviderPresenceStatusListener,
                RegistrationStateChangeListener,
                ServiceListener,
                MouseListener
{
    private final AccountListModel accountListModel = new AccountListModel();

    private final JButton editButton;

    /**
     * Creates an instance of this account list by specifying the parent
     * container of the list.
     * 
     * @param parentConfigPanel the container where this list is added.
     */
    public AccountList(AccountsConfigurationPanel parentConfigPanel)
    {
        this.setModel(accountListModel);
        this.setCellRenderer(new AccountListCellRenderer());

        this.addMouseListener(this);

        this.accountsInit();

        GuiActivator.bundleContext.addServiceListener(this);

        this.editButton = parentConfigPanel.getEditButton();
    }

    /**
     * Initializes the accounts table.
     */
    private void accountsInit()
    {
        for (ProtocolProviderFactory providerFactory : GuiActivator
            .getProtocolProviderFactories().values())
        {
            ServiceReference serRef;
            ProtocolProviderService protocolProvider;

            for (AccountID accountID : providerFactory.getRegisteredAccounts())
            {
                boolean isHidden
                    = (accountID.getAccountProperty
                        (ProtocolProviderFactory.IS_PROTOCOL_HIDDEN) != null);

                if (isHidden)
                    continue;

                serRef = providerFactory.getProviderForAccount(accountID);

                protocolProvider =
                    (ProtocolProviderService) GuiActivator.bundleContext
                        .getService(serRef);

                protocolProvider.addRegistrationStateChangeListener(this);

                OperationSetPresence presence
                    = protocolProvider
                        .getOperationSet(OperationSetPresence.class);

                if (presence != null)
                {
                    presence.addProviderPresenceStatusListener(this);
                }

                accountListModel.addElement(new Account(protocolProvider));
            }
        }
    }

    public Account getSelectedAccount()
    {
        return (Account) this.getSelectedValue();
    }

    /**
     * Refreshes the account status icon, when the status has changed.
     */
    public void providerStatusChanged(ProviderPresenceStatusChangeEvent evt)
    {
        accountListModelContentChanged(evt.getProvider());
    }

    public void providerStatusMessageChanged(PropertyChangeEvent evt) {}

    /**
     * Implements the <tt>ServiceListener</tt> method. Verifies whether the
     * passed event concerns a <tt>ProtocolProviderService</tt> and adds the
     * corresponding UI controls.
     *
     * @param event The <tt>ServiceEvent</tt> object.
     */
    public void serviceChanged(ServiceEvent event)
    {
        // if the event is caused by a bundle being stopped, we don't want to
        // know
        if (event.getServiceReference().getBundle().getState()
                == Bundle.STOPPING)
        {
            return;
        }
        Object sourceService =
            GuiActivator.bundleContext.getService(event.getServiceReference());

        // we don't care if the source service is not a protocol provider
        if (!(sourceService instanceof ProtocolProviderService))
        {
            return;
        }

        ProtocolProviderService protocolProvider
            = (ProtocolProviderService) sourceService;

        // If the protocol provider is hidden we don't want to show it in the
        // list.
        boolean isHidden
            = (protocolProvider.getAccountID().getAccountProperty
                (ProtocolProviderFactory.IS_PROTOCOL_HIDDEN) != null);

        if (isHidden)
            return;

        // Add or remove the protocol provider from our accounts list.
        if (event.getType() == ServiceEvent.REGISTERED)
        {
            // Add a presence listener in order to listen for any status
            // changes.
            OperationSetPresence presence
                = protocolProvider
                    .getOperationSet(OperationSetPresence.class);

            if (presence != null)
            {
                presence.addProviderPresenceStatusListener(this);
            }

            accountListModel.addElement(new Account(protocolProvider));
        }
        else if (event.getType() == ServiceEvent.UNREGISTERING)
        {
            for (Object accountListModelElement : accountListModel.toArray())
            {
                Account account = (Account) accountListModelElement;

                if (account.getProtocolProvider().equals(protocolProvider))
                    accountListModel.removeElement(account);
            }
        }
    }

    /**
     * Listens for double mouse click events in order to open the edit form.
     */
    public void mouseClicked(MouseEvent e)
    {
        if (e.getClickCount() > 1)
        {
            editButton.doClick();
        }
    }

    public void mouseEntered(MouseEvent e)
    {}

    public void mouseExited(MouseEvent e)
    {}

    public void mousePressed(MouseEvent e)
    {}

    public void mouseReleased(MouseEvent e)
    {}

    /**
     * Refreshes the account status icon, when the status has changed.
     */
    public void registrationStateChanged(RegistrationStateChangeEvent evt)
    {
        accountListModelContentChanged(evt.getProvider());
    }

    /**
     * Notifies <code>accountListModel</code> that the <code>Account</code>s of
     * a specific <code>ProtocolProviderService</code> have changed.
     * 
     * @param protocolProvider
     *            the <code>ProtocolProviderService</code> which had its
     *            <code>Account</code>s changed
     */
    private void accountListModelContentChanged(
        ProtocolProviderService protocolProvider)
    {
        Enumeration<?> accounts = accountListModel.elements();

        while (accounts.hasMoreElements())
        {
            Account account = (Account) accounts.nextElement();

            if (account.getProtocolProvider().equals(protocolProvider))
                accountListModel.contentChanged(account);
        }
    }

    /**
     * A custom list model that allows us to refresh the content of a single
     * row.
     */
    private class AccountListModel
        extends DefaultListModel
    {
        public void contentChanged(Account account)
        {
            int index = this.indexOf(account);
            this.fireContentsChanged(this, index, index);
        }
    }
}
