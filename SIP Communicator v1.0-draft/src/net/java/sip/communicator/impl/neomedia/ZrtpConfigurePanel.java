/*
 * SIP Communicator, the OpenSource Java VoIP and Instant Messaging client.
 *
 * Distributable under LGPL license. See terms of license at gnu.org.
 */
package net.java.sip.communicator.impl.neomedia;

import gnu.java.zrtp.ZrtpConfigure;
import gnu.java.zrtp.ZrtpConstants;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.event.*;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import net.java.sip.communicator.util.swing.*;

/**
 */
@SuppressWarnings("serial")
public class ZrtpConfigurePanel
    extends TransparentPanel
{
    private static String TRUSTED_PROP = "net.java.sip.communicator.gnu.java.zrtp.trustedmitm";
    private static String SASSIGN_PROP = "net.java.sip.communicator.gnu.java.zrtp.sassignature";

    private ZrtpConfigure active = new ZrtpConfigure();

    private ZrtpConfigure inActive = new ZrtpConfigure();

    PublicKeyControls pkc = new PublicKeyControls();
    HashControls hc = new HashControls();
    CipherControls cc = new CipherControls();
    SasControls sc = new SasControls();
    LengthControls lc = new LengthControls();
    
    public ZrtpConfigurePanel() {

        JPanel mainPanel = new TransparentPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        
        JPanel panel = new TransparentPanel();

        panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        panel.setLayout(new GridLayout(5, 1));

        final JButton stdButton = new JButton(NeomediaActivator.getResources()
                .getI18NString("impl.media.security.zrtp.STANDARD"));
        stdButton.setOpaque(false);

        final JButton mandButton = new JButton(NeomediaActivator.getResources()
                .getI18NString("impl.media.security.zrtp.MANDATORY"));
        mandButton.setOpaque(false);

        final JButton saveButton = new JButton(NeomediaActivator.getResources()
                .getI18NString("service.gui.SAVE"));
        saveButton.setOpaque(false);

        JPanel buttonBar = new TransparentPanel(new GridLayout(1, 7));
        buttonBar.add(stdButton);
        buttonBar.add(mandButton);
        buttonBar.add(Box.createHorizontalStrut(10));
        buttonBar.add(saveButton);
        mainPanel.add(buttonBar);
        mainPanel.add(Box.createVerticalStrut(7));

        boolean trusted = NeomediaActivator.getConfigurationService().getBoolean(TRUSTED_PROP, false);
        boolean sasSign = NeomediaActivator.getConfigurationService().getBoolean(SASSIGN_PROP, false);
        
        JPanel checkBar = new JPanel(new GridLayout(1,2));
        final JCheckBox trustedMitM = new JCheckBox(NeomediaActivator.getResources()
                .getI18NString("impl.media.security.zrtp.TRUSTED"), trusted);
        final JCheckBox sasSignature = new JCheckBox(NeomediaActivator.getResources()
                .getI18NString("impl.media.security.zrtp.SASSIGNATURE"), sasSign);
        checkBar.add(trustedMitM);
        checkBar.add(sasSignature);
        mainPanel.add(checkBar);
        mainPanel.add(Box.createVerticalStrut(7));

        ActionListener buttonListener = new ActionListener() {

            public void actionPerformed(ActionEvent event) {
                Object source = event.getSource();
                if (source == stdButton) {
                    inActive.clear();
                    active.setStandardConfig();
                    pkc.setStandard();
                    hc.setStandard();
                    sc.setStandard();
                    cc.setStandard();
                    lc.setStandard();
                }
                else if (source == mandButton) {
                    inActive.clear();
                    active.setMandatoryOnly();
                    pkc.setStandard();
                    hc.setStandard();
                    sc.setStandard();
                    cc.setStandard();
                    lc.setStandard();
                }
                else if (source == saveButton) {
                    Boolean t = new Boolean(active.isTrustedMitM());
                    Boolean s = new Boolean(active.isSasSignature());
                    NeomediaActivator.getConfigurationService().setProperty(TRUSTED_PROP, t);
                    NeomediaActivator.getConfigurationService().setProperty(SASSIGN_PROP, s);
                    pkc.saveConfig();
                    hc.saveConfig();
                    sc.saveConfig();
                    cc.saveConfig();
                    lc.saveConfig();
                }
                else
                    return;

            }
        };
        stdButton.addActionListener(buttonListener);
        mandButton.addActionListener(buttonListener);
        saveButton.addActionListener(buttonListener);

        ItemListener itemListener = new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                Object source = e.getItemSelectable();

                if (source == trustedMitM) {
                     active.setTrustedMitM(trustedMitM.isSelected());
                } else if (source == sasSignature) {
                    active.setSasSignature(sasSignature.isSelected());
                }
            }
        };
        trustedMitM.addItemListener(itemListener);
        sasSignature.addItemListener(itemListener);
        
        panel.add(pkc);
        panel.add(hc);
        panel.add(cc);
        panel.add(sc);
        panel.add(lc);
        mainPanel.add(panel);
        add(mainPanel);

        setSize(panel.getPreferredSize());

        setVisible(true);

    }
    
    private <T extends Enum<T>>String getPropertyValue(T algo) {
        StringBuffer strb = new StringBuffer();
        for (T it : active.algos(algo)) {
            strb.append(it.name());
            strb.append(';');
        }
        return strb.toString();
    }
    
    class PublicKeyControls extends JPanel {

        private final ZrtpConfigureTableModel<ZrtpConstants.SupportedPubKeys> dataModel;
        
        PublicKeyControls() {
            String id = ZrtpConfigureUtils.getPropertyID(ZrtpConstants.SupportedPubKeys.DH2K);
            String savedConf = NeomediaActivator.getConfigurationService().getString(id);
            if (savedConf == null)
                savedConf = "";
            
            dataModel = new ZrtpConfigureTableModel<ZrtpConstants.SupportedPubKeys>(
                    ZrtpConstants.SupportedPubKeys.DH2K, active, inActive, savedConf);
            createControls(this, dataModel, NeomediaActivator.getResources()
                    .getI18NString("impl.media.security.zrtp.PUB_KEYS"));
        }
        
        void setStandard() {
            dataModel.setStandardConfig();
        }
        
        void saveConfig() {
            String value = getPropertyValue(ZrtpConstants.SupportedPubKeys.DH2K);
            String id = ZrtpConfigureUtils.getPropertyID(ZrtpConstants.SupportedPubKeys.DH2K);
            NeomediaActivator.getConfigurationService().setProperty(id, value);
        }
    }

    class HashControls extends JPanel {

        private final ZrtpConfigureTableModel<ZrtpConstants.SupportedHashes> dataModel;
        
        HashControls() {
            String id = ZrtpConfigureUtils.getPropertyID(ZrtpConstants.SupportedHashes.S256);
            String savedConf = NeomediaActivator.getConfigurationService().getString(id);
            if (savedConf == null)
                savedConf = "";
            
            dataModel = new ZrtpConfigureTableModel<ZrtpConstants.SupportedHashes>(
                    ZrtpConstants.SupportedHashes.S256, active, inActive, savedConf);
            createControls(this, dataModel, NeomediaActivator.getResources()
                    .getI18NString("impl.media.security.zrtp.HASHES"));
        }

        void setStandard() {
            dataModel.setStandardConfig();
        }

        void saveConfig() {
            String value = getPropertyValue(ZrtpConstants.SupportedHashes.S256);
            String id = ZrtpConfigureUtils.getPropertyID(ZrtpConstants.SupportedHashes.S256);
            NeomediaActivator.getConfigurationService().setProperty(id, value);
        }
    }

    class CipherControls extends JPanel {

        private final ZrtpConfigureTableModel<ZrtpConstants.SupportedSymCiphers> dataModel;
        
        CipherControls() {
            String id = ZrtpConfigureUtils.getPropertyID(ZrtpConstants.SupportedSymCiphers.AES1);
            String savedConf = NeomediaActivator.getConfigurationService().getString(id);
            if (savedConf == null)
                savedConf = "";
            
            dataModel = new ZrtpConfigureTableModel<ZrtpConstants.SupportedSymCiphers>(
                    ZrtpConstants.SupportedSymCiphers.AES1, active, inActive, savedConf);
            createControls(this, dataModel, NeomediaActivator.getResources()
                    .getI18NString("impl.media.security.zrtp.SYM_CIPHERS"));
        }

        void setStandard() {
            dataModel.setStandardConfig();
        }

        void saveConfig() {
            String value = getPropertyValue(ZrtpConstants.SupportedSymCiphers.AES1);
            String id = ZrtpConfigureUtils.getPropertyID(ZrtpConstants.SupportedSymCiphers.AES1);
            NeomediaActivator.getConfigurationService().setProperty(id, value);
        }
    }

    class SasControls extends JPanel {
        
        private final ZrtpConfigureTableModel<ZrtpConstants.SupportedSASTypes> dataModel;
        
        SasControls() {
            String id = ZrtpConfigureUtils.getPropertyID(ZrtpConstants.SupportedSASTypes.B32);
            String savedConf = NeomediaActivator.getConfigurationService().getString(id);
            if (savedConf == null)
                savedConf = "";
            
            dataModel = new ZrtpConfigureTableModel<ZrtpConstants.SupportedSASTypes>(
                    ZrtpConstants.SupportedSASTypes.B32, active, inActive, savedConf);
            createControls(this, dataModel, NeomediaActivator.getResources()
                    .getI18NString("impl.media.security.zrtp.SAS_TYPES"));
        }

        void setStandard() {
            dataModel.setStandardConfig();
        }

        void saveConfig() {
            String value = getPropertyValue(ZrtpConstants.SupportedSASTypes.B32);
            String id = ZrtpConfigureUtils.getPropertyID(ZrtpConstants.SupportedSASTypes.B32);
            NeomediaActivator.getConfigurationService().setProperty(id, value);
        }
    }

    class LengthControls extends JPanel {

        private final ZrtpConfigureTableModel<ZrtpConstants.SupportedAuthLengths> dataModel;
        
        LengthControls() {
            String id = ZrtpConfigureUtils.getPropertyID(ZrtpConstants.SupportedAuthLengths.HS32);
            String savedConf = NeomediaActivator.getConfigurationService().getString(id);
            if (savedConf == null)
                savedConf = "";
            
            dataModel = new ZrtpConfigureTableModel<ZrtpConstants.SupportedAuthLengths>(
                    ZrtpConstants.SupportedAuthLengths.HS32, active, inActive, savedConf);
            createControls(this, dataModel, NeomediaActivator.getResources()
                    .getI18NString("impl.media.security.zrtp.SRTP_LENGTHS"));
        }

        void setStandard() {
            dataModel.setStandardConfig();
        }

        void saveConfig() {
            String value = getPropertyValue(ZrtpConstants.SupportedAuthLengths.HS32);
            String id = ZrtpConfigureUtils.getPropertyID(ZrtpConstants.SupportedAuthLengths.HS32);
            NeomediaActivator.getConfigurationService().setProperty(id, value);
        }
    }

    private <T extends Enum<T>> void createControls(JPanel panel,
            ZrtpConfigureTableModel<T> model, String title) {

        final JButton upButton = new JButton(NeomediaActivator.getResources()
                .getI18NString("impl.media.configform.UP"));
        upButton.setOpaque(false);

        final JButton downButton = new JButton(NeomediaActivator.getResources()
                .getI18NString("impl.media.configform.DOWN"));
        downButton.setOpaque(false);

        Container buttonBar = new JPanel(new GridLayout(0, 1));
        buttonBar.add(upButton);
        buttonBar.add(downButton);

        panel.setLayout(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder(BorderFactory
                .createEtchedBorder(EtchedBorder.LOWERED), title));

        final JTable table = new JTable(model.getRowCount(), 2);
        table.setShowGrid(false);
        table.setTableHeader(null);
        table.setModel(model);
        table.setPreferredScrollableViewportSize(new Dimension(400, 60));
        // table.setFillsViewportHeight(true); // Since 1.6 only - nicer view

        /*
         * The first column contains the check boxes which enable/disable their
         * associated encodings and it doesn't make sense to make it wider than
         * the check boxes.
         */
        TableColumnModel tableColumnModel = table.getColumnModel();
        TableColumn tableColumn = tableColumnModel.getColumn(0);
        tableColumn.setMaxWidth(tableColumn.getMinWidth() + 5);
        table.doLayout();
        
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.CENTER;
        constraints.fill = GridBagConstraints.BOTH;
        constraints.gridwidth = 1;
        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.weightx = 1;
        constraints.weighty = 1;
        panel.add(new JScrollPane(table), constraints);

        constraints.anchor = GridBagConstraints.NORTHEAST;
        constraints.fill = GridBagConstraints.NONE;
        constraints.gridwidth = 1;
        constraints.gridx = 1;
        constraints.gridy = 1;
        constraints.weightx = 0;
        constraints.weighty = 0;
        panel.add(buttonBar, constraints);

        ListSelectionListener tableSelectionListener = new ListSelectionListener() {
            @SuppressWarnings("unchecked")
            public void valueChanged(ListSelectionEvent event) {
                if (table.getSelectedRowCount() == 1) {
                    int selectedRow = table.getSelectedRow();
                    if (selectedRow > -1) {
                        ZrtpConfigureTableModel<T> model = (ZrtpConfigureTableModel<T>) table
                                .getModel();
                        upButton.setEnabled(selectedRow > 0
                                && model.checkEnableUp(selectedRow));
                        downButton.setEnabled(selectedRow < (table
                                .getRowCount() - 1)
                                && model.checkEnableDown(selectedRow));
                        return;
                    }
                }
                upButton.setEnabled(false);
                downButton.setEnabled(false);
            }
        };
        table.getSelectionModel().addListSelectionListener(
                tableSelectionListener);

        TableModelListener tableListener = new TableModelListener() {
            @SuppressWarnings("unchecked")
            public void tableChanged(TableModelEvent e) {
                if (table.getSelectedRowCount() == 1) {
                    int selectedRow = table.getSelectedRow();
                    if (selectedRow > -1) {
                        ZrtpConfigureTableModel<T> model = (ZrtpConfigureTableModel<T>) table
                                .getModel();
                        upButton.setEnabled(selectedRow > 0
                                && model.checkEnableUp(selectedRow));
                        downButton.setEnabled(selectedRow < (table
                                .getRowCount() - 1)
                                && model.checkEnableDown(selectedRow));
                        return;
                    }
                }
                upButton.setEnabled(false);
                downButton.setEnabled(false);
            }
        };
        table.getModel().addTableModelListener(tableListener);

        tableSelectionListener.valueChanged(null);

        ActionListener buttonListener = new ActionListener() {
            @SuppressWarnings("unchecked")
            public void actionPerformed(ActionEvent event) {
                Object source = event.getSource();
                boolean up;
                if (source == upButton)
                    up = true;
                else if (source == downButton)
                    up = false;
                else
                    return;

                int index = ((ZrtpConfigureTableModel<T>) table.getModel())
                        .move(table.getSelectedRow(), up, up);
                table.getSelectionModel().setSelectionInterval(index, index);
            }
        };
        upButton.addActionListener(buttonListener);
        downButton.addActionListener(buttonListener);
    }
}
