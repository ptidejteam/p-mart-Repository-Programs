/*
 * SIP Communicator, the OpenSource Java VoIP and Instant Messaging client.
 * 
 * Distributable under LGPL license. See terms of license at gnu.org.
 */
package net.java.sip.communicator.plugin.otr;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.table.*;

import org.osgi.framework.*;

import net.java.otr4j.*;
import net.java.sip.communicator.service.contactlist.*;
import net.java.sip.communicator.service.protocol.*;
import net.java.sip.communicator.util.swing.*;

/**
 * A special {@link Panel} that manages the OTR configuration.
 * 
 * @author George Politis
 */
@SuppressWarnings("serial")
public class OtrConfigurationPanel
    extends TransparentPanel
{

    /**
     * A special {@link Panel} for Private Keys display.
     * 
     * @author George Politis
     */
    private static class PrivateKeysPanel
        extends TransparentPanel
    {
        /**
         * A special {@link JComboBox} for {@link AccountID} enumeration.
         * 
         * @author George Politis
         */
        private static class AccountsComboBox
            extends JComboBox
        {

            /**
             * A class hosted in an {@link AccountsComboBox} that holds a single
             * {@link AccountID}.
             * 
             * @author George Politis
             */
            private static class AccountsComboBoxItem
            {
                public final AccountID accountID;

                public AccountsComboBoxItem(AccountID accountID)
                {
                    this.accountID = accountID;
                }

                public String toString()
                {
                    return accountID.getDisplayName();
                }
            }

            public AccountsComboBox()
            {
                List<AccountID> accountIDs = OtrActivator.getAllAccountIDs();

                if (accountIDs == null)
                    return;

                for (AccountID accountID : accountIDs)
                    this.addItem(new AccountsComboBoxItem(accountID));
            }

            /**
             * Gets the selected {@link AccountID} for this
             * {@link AccountsComboBox}.
             * 
             * @return
             */
            public AccountID getSelectedAccountID()
            {
                Object selectedItem = this.getSelectedItem();
                if (selectedItem instanceof AccountsComboBoxItem)
                    return ((AccountsComboBoxItem) selectedItem).accountID;
                else
                    return null;
            }
        }

        private AccountsComboBox cbAccounts;

        private JLabel lblFingerprint;

        private JButton btnGenerate;

        public PrivateKeysPanel()
        {
            this.initComponents();

            this.openAccount(cbAccounts.getSelectedAccountID());
        }

        /**
         * Sets up the {@link PrivateKeysPanel} components so that they reflect
         * the {@link AccountID} param.
         * 
         * @param account the {@link AccountID} to setup the components for.
         */
        private void openAccount(AccountID account)
        {
            if (account == null)
            {
                lblFingerprint.setEnabled(false);
                btnGenerate.setEnabled(false);

                lblFingerprint.setText(OtrActivator.resourceService
                    .getI18NString("plugin.otr.configform.NO_KEY_PRESENT"));
                btnGenerate.setText(OtrActivator.resourceService
                    .getI18NString("plugin.otr.configform.GENERATE"));
            }
            else
            {
                lblFingerprint.setEnabled(true);
                btnGenerate.setEnabled(true);

                String fingerprint =
                    OtrActivator.scOtrKeyManager.getLocalFingerprint(account);

                if (fingerprint == null || fingerprint.length() < 1)
                {
                    lblFingerprint.setText(OtrActivator.resourceService
                        .getI18NString("plugin.otr.configform.NO_KEY_PRESENT"));
                    btnGenerate.setText(OtrActivator.resourceService
                        .getI18NString("plugin.otr.configform.GENERATE"));
                }
                else
                {
                    lblFingerprint.setText(fingerprint);
                    btnGenerate.setText(OtrActivator.resourceService
                        .getI18NString("plugin.otr.configform.REGENERATE"));
                }
            }
        }

        /**
         * Initializes the {@link PrivateKeysPanel} components.
         */
        private void initComponents()
        {
            this.setBorder(BorderFactory.createTitledBorder(BorderFactory
                .createEtchedBorder(EtchedBorder.LOWERED),
                OtrActivator.resourceService
                    .getI18NString("plugin.otr.configform.MY_PRIVATE_KEYS")));
            this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

            JPanel pnlAccounts = new TransparentPanel();
            this.add(pnlAccounts);

            pnlAccounts.add(new JLabel(OtrActivator.resourceService
                .getI18NString("service.gui.ACCOUNT")
                + ": "));

            cbAccounts = new AccountsComboBox();
            cbAccounts.addActionListener(new ActionListener()
            {
                public void actionPerformed(ActionEvent e)
                {
                    openAccount(((AccountsComboBox) e.getSource())
                        .getSelectedAccountID());
                }
            });
            pnlAccounts.add(cbAccounts);

            JPanel pnlFingerprint = new TransparentPanel();
            this.add(pnlFingerprint);

            pnlFingerprint.add(new JLabel(OtrActivator.resourceService
                .getI18NString("plugin.otr.configform.FINGERPRINT")
                + ": "));

            lblFingerprint = new JLabel();
            pnlFingerprint.add(lblFingerprint);

            btnGenerate = new JButton();
            btnGenerate.addActionListener(new ActionListener()
            {
                public void actionPerformed(ActionEvent e)
                {
                    AccountID account = cbAccounts.getSelectedAccountID();
                    if (account == null)
                        return;
                    OtrActivator.scOtrKeyManager.generateKeyPair(account);
                    openAccount(account);
                }
            });
            pnlFingerprint.add(btnGenerate);
        }
    }

    /**
     * A special {@link Panel} for fingerprints display.
     * 
     * @author George Politis
     */
    private static class KnownFingerprintsPanel
        extends TransparentPanel
    {

        private static class ContactsTableModel
            extends AbstractTableModel
        {
            public final java.util.List<Contact> allContacts =
                new Vector<Contact>();

            public ContactsTableModel()
            {
                // Get the protocolproviders
                ServiceReference[] protocolProviderRefs = null;
                try
                {
                    protocolProviderRefs =
                        OtrActivator.bundleContext.getServiceReferences(
                            ProtocolProviderService.class.getName(), null);
                }
                catch (InvalidSyntaxException ex)
                {
                    return;
                }

                if (protocolProviderRefs == null
                    || protocolProviderRefs.length < 1)
                    return;

                // Get the metacontactlist service.
                ServiceReference ref =
                    OtrActivator.bundleContext
                        .getServiceReference(MetaContactListService.class
                            .getName());

                MetaContactListService service =
                    (MetaContactListService) OtrActivator.bundleContext
                        .getService(ref);

                // Populate contacts.
                for (int i = 0; i < protocolProviderRefs.length; i++)
                {
                    ProtocolProviderService provider =
                        (ProtocolProviderService) OtrActivator.bundleContext
                            .getService(protocolProviderRefs[i]);

                    Iterator<MetaContact> metaContacts =
                        service.findAllMetaContactsForProvider(provider);
                    while (metaContacts.hasNext())
                    {
                        MetaContact metaContact = metaContacts.next();
                        Iterator<Contact> contacts = metaContact.getContacts();
                        while (contacts.hasNext())
                        {
                            allContacts.add(contacts.next());
                        }
                    }
                }
            }

            public static final int CONTACTNAME_INDEX = 0;

            public static final int VERIFIED_INDEX = 1;

            public static final int FINGERPRINT_INDEX = 2;

            /*
             * Implements AbstractTableModel#getColumnName(int).
             */
            public String getColumnName(int column)
            {
                switch (column)
                {
                case CONTACTNAME_INDEX:
                    return OtrActivator.resourceService
                        .getI18NString("plugin.otr.configform.COLUMN_NAME_CONTACT");
                case VERIFIED_INDEX:
                    return OtrActivator.resourceService
                        .getI18NString("plugin.otr.configform.COLUMN_NAME_VERIFIED_STATUS");
                case FINGERPRINT_INDEX:
                    return OtrActivator.resourceService
                        .getI18NString("plugin.otr.configform.FINGERPRINT");
                default:
                    return null;
                }
            }

            /*
             * Implements AbstractTableModel#getValueAt(int,int).
             */
            public Object getValueAt(int row, int column)
            {
                if (row < 0)
                    return null;

                Contact contact = allContacts.get(row);
                switch (column)
                {
                case CONTACTNAME_INDEX:
                    return contact.getDisplayName();
                case VERIFIED_INDEX:
                    // TODO: Maybe use a CheckBoxColumn?
                    return (OtrActivator.scOtrKeyManager.isVerified(contact)) ? OtrActivator.resourceService
                        .getI18NString("plugin.otr.configform.COLUMN_VALUE_VERIFIED_TRUE")
                        : OtrActivator.resourceService
                            .getI18NString("plugin.otr.configform.COLUMN_VALUE_VERIFIED_FALSE");
                case FINGERPRINT_INDEX:
                    return OtrActivator.scOtrKeyManager
                        .getRemoteFingerprint(contact);
                default:
                    return null;
                }
            }

            /*
             * Implements AbstractTableModel#getRowCount().
             */
            public int getRowCount()
            {
                return allContacts.size();
            }

            /*
             * Implements AbstractTableModel#getColumnCount().
             */
            public int getColumnCount()
            {
                return 3;
            }
        }

        public KnownFingerprintsPanel()
        {
            this.initComponents();

            openContact(getSelectedContact());
        }

        /**
         * Gets the selected {@link Contact} for this
         * {@link KnownFingerprintsPanel}.
         * 
         * @return the selected {@link Contact}
         */
        private Contact getSelectedContact()
        {
            ContactsTableModel model =
                (ContactsTableModel) contactsTable.getModel();
            int index = contactsTable.getSelectedRow();
            if (index < 0 || index > model.allContacts.size())
                return null;

            return model.allContacts.get(index);
        }

        /**
         * Sets up the {@link KnownFingerprintsPanel} components so that they
         * reflect the {@link Contact} param.
         * 
         * @param contact the {@link Contact} to setup the components for.
         */
        private void openContact(Contact contact)
        {
            if (contact == null)
            {
                btnForgetFingerprint.setEnabled(false);
                btnVerifyFingerprint.setEnabled(false);
            }
            else
            {
                boolean verified =
                    OtrActivator.scOtrKeyManager.isVerified(contact);

                btnForgetFingerprint.setEnabled(verified);
                btnVerifyFingerprint.setEnabled(!verified);
            }
        }

        JButton btnVerifyFingerprint;

        JButton btnForgetFingerprint;

        JTable contactsTable;

        /**
         * Initializes the {@link KnownFingerprintsPanel} components.
         */
        private void initComponents()
        {
            this
                .setBorder(BorderFactory
                    .createTitledBorder(
                        BorderFactory.createEtchedBorder(EtchedBorder.LOWERED),
                        OtrActivator.resourceService
                            .getI18NString("plugin.otr.configform.KNOWN_FINGERPRINTS")));
            this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

            contactsTable = new JTable();
            contactsTable.setModel(new ContactsTableModel());
            contactsTable
                .setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
            contactsTable.setCellSelectionEnabled(false);
            contactsTable.setColumnSelectionAllowed(false);
            contactsTable.setRowSelectionAllowed(true);
            contactsTable.getSelectionModel().addListSelectionListener(
                new ListSelectionListener()
                {
                    public void valueChanged(ListSelectionEvent e)
                    {
                        if (e.getValueIsAdjusting())
                            return;

                        openContact(getSelectedContact());

                    }
                });

            JScrollPane pnlContacts = new JScrollPane(contactsTable);
            this.add(pnlContacts);

            JPanel pnlButtons = new TransparentPanel();
            this.add(pnlButtons);

            btnVerifyFingerprint = new JButton();
            btnVerifyFingerprint.setText(OtrActivator.resourceService
                .getI18NString("plugin.otr.configform.VERIFY_FINGERPRINT"));
            btnVerifyFingerprint.addActionListener(new ActionListener()
            {
                public void actionPerformed(ActionEvent arg0)
                {
                    OtrActivator.scOtrKeyManager.verify(getSelectedContact());

                }
            });
            pnlButtons.add(btnVerifyFingerprint);

            btnForgetFingerprint = new JButton();
            btnForgetFingerprint.setText(OtrActivator.resourceService
                .getI18NString("plugin.otr.configform.FORGET_FINGERPRINT"));
            btnForgetFingerprint.addActionListener(new ActionListener()
            {
                public void actionPerformed(ActionEvent arg0)
                {
                    OtrActivator.scOtrKeyManager.unverify(getSelectedContact());
                }
            });
            pnlButtons.add(btnForgetFingerprint);
        }
    }

    /**
     * A special {@link Panel} for OTR policy display.
     * 
     * @author George Politis
     */
    private static class DefaultOtrPolicyPanel
        extends TransparentPanel
    {
        // TODO We should listen for configuration value changes.
        public DefaultOtrPolicyPanel()
        {
            this.initComponents();
            this.loadPolicy();
        }

        /**
         * Sets up the {@link DefaultOtrPolicyPanel} components so that they
         * reflect the global OTR policy.
         * 
         */
        public void loadPolicy()
        {
            OtrPolicy otrPolicy = OtrActivator.scOtrEngine.getGlobalPolicy();

            boolean otrEnabled = otrPolicy.getEnableManual();
            cbEnable.setSelected(otrEnabled);
            cbAutoInitiate.setEnabled(otrEnabled);
            cbRequireOtr.setEnabled(otrEnabled);
            cbAutoInitiate.setSelected(otrPolicy.getEnableAlways());
            cbRequireOtr.setSelected(otrPolicy.getRequireEncryption());
        }

        private SIPCommCheckBox cbEnable;

        private SIPCommCheckBox cbAutoInitiate;

        private SIPCommCheckBox cbRequireOtr;

        /**
         * Initializes the {@link DefaultOtrPolicyPanel} components.
         */
        private void initComponents()
        {
            this.setBorder(BorderFactory.createTitledBorder(BorderFactory
                .createEtchedBorder(EtchedBorder.LOWERED),
                OtrActivator.resourceService
                    .getI18NString("plugin.otr.configform.DEFAULT_SETTINGS")));
            this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

            cbEnable =
                new SIPCommCheckBox(OtrActivator.resourceService
                    .getI18NString("plugin.otr.configform.CB_ENABLE"));
            cbEnable.addActionListener(new ActionListener()
            {
                public void actionPerformed(ActionEvent e)
                {
                    OtrPolicy otrPolicy =
                        OtrActivator.scOtrEngine.getGlobalPolicy();

                    otrPolicy.setEnableManual(((JCheckBox) e.getSource())
                        .isSelected());

                    OtrActivator.scOtrEngine.setGlobalPolicy(otrPolicy);

                    DefaultOtrPolicyPanel.this.loadPolicy();
                }
            });
            this.add(cbEnable);

            cbAutoInitiate =
                new SIPCommCheckBox(OtrActivator.resourceService
                    .getI18NString("plugin.otr.configform.CB_AUTO"));
            cbAutoInitiate.addActionListener(new ActionListener()
            {
                public void actionPerformed(ActionEvent e)
                {
                    OtrPolicy otrPolicy =
                        OtrActivator.scOtrEngine.getGlobalPolicy();

                    otrPolicy.setEnableAlways(((JCheckBox) e.getSource())
                        .isSelected());

                    OtrActivator.scOtrEngine.setGlobalPolicy(otrPolicy);

                    DefaultOtrPolicyPanel.this.loadPolicy();

                }
            });

            this.add(cbAutoInitiate);

            cbRequireOtr =
                new SIPCommCheckBox(OtrActivator.resourceService
                    .getI18NString("plugin.otr.configform.CB_REQUIRE"));
            cbRequireOtr.addActionListener(new ActionListener()
            {
                public void actionPerformed(ActionEvent e)
                {
                    OtrPolicy otrPolicy =
                        OtrActivator.scOtrEngine.getGlobalPolicy();

                    otrPolicy.setRequireEncryption(((JCheckBox) e.getSource())
                        .isSelected());

                    OtrActivator.scOtrEngine.setGlobalPolicy(otrPolicy);

                    DefaultOtrPolicyPanel.this.loadPolicy();

                }
            });
            this.add(cbRequireOtr);
        }
    }

    public OtrConfigurationPanel()
    {
        this.initComponents();
    }

    /**
     * Initializes all 3 panels of the {@link OtrConfigurationPanel}
     */
    private void initComponents()
    {
        this.setLayout(new GridBagLayout());

        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1.0;
        c.anchor = GridBagConstraints.PAGE_START;

        JPanel pnlPrivateKeys = new PrivateKeysPanel();
        c.gridy = 0;
        this.add(pnlPrivateKeys, c);

        JPanel pnlPolicy = new DefaultOtrPolicyPanel();
        c.gridy = 1;
        this.add(pnlPolicy, c);

        JPanel pnlFingerprints = new KnownFingerprintsPanel();
        pnlFingerprints.setMinimumSize(new Dimension(Short.MAX_VALUE,
            Short.MAX_VALUE));
        c.weighty = 1.0;
        c.gridy = 2;
        this.add(pnlFingerprints, c);
    }
}