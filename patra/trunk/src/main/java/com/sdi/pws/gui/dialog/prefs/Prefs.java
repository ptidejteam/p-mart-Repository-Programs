/*
Password Tracker (PATRA). An application to safely store your passwords.
Copyright (C) 2006-2009  Bruno Ranschaert, S.D.I.-Consulting BVBA.

For more information contact: nospam@sdi-consulting.com
Visit our website: http://www.sdi-consulting.com

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 2 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
*/

package com.sdi.pws.gui.dialog.prefs;

import com.sdi.pws.generator.Generator;
import com.sdi.pws.gui.compo.generator.change.ChangeViewGenerator;
import com.sdi.pws.gui.compo.generator.view.JGeneratorProps;
import com.sdi.pws.gui.GuiUtil;
import com.sdi.pws.preferences.Preferences;
import com.sdi.pws.preferences.PreferencesException;
import com.sdi.pws.util.PreferencesUtil;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.SupportCode;
import com.intellij.uiDesigner.core.Spacer;

import javax.swing.*;
import javax.help.HelpBroker;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.*;
import java.io.File;
import java.util.ResourceBundle;

public class Prefs
{
    private JRadioButton selectTreeView;
    private JRadioButton selectTableView;

    private JTextField defaultGroup;
    private JCheckBox activateDefaultGroup;

    private JCheckBox activateDefaultUid;
    private JTextField defaultUid;

    private JRadioButton convertAuto;
    private JRadioButton convertNone;
    private JRadioButton convertAsk;

    private JCheckBox leftSide;
    private JCheckBox rightSide;
    private JTabbedPane mainPanel;

    private JButton browseButton;
    private JTextField databasePath;
    private JCheckBox showPassword;
    private JCheckBox suggestDefaultDb;
    private JCheckBox addExtension;
    private JSpinner nrBackups;
    private JCheckBox warnExport;
    private JCheckBox stayPut;

    public Prefs(final Preferences aPrefs)
    {
        final ButtonGroup lViewGroup = new ButtonGroup();
        lViewGroup.add(selectTreeView);
        lViewGroup.add(selectTableView);

        final ButtonGroup lConvertGroup = new ButtonGroup();
        lConvertGroup.add(convertAsk);
        lConvertGroup.add(convertAuto);
        lConvertGroup.add(convertNone);

        nrBackups.setModel(new SpinnerNumberModel(5, 0, 99, 1));

        activateDefaultUid.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                defaultUid.setEnabled(activateDefaultUid.isSelected());
            }
        });

        activateDefaultGroup.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                defaultGroup.setEnabled(activateDefaultGroup.isSelected());
            }
        });

        browseButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                final File lFile = GuiUtil.browseForSafe(mainPanel, aPrefs);
                if(lFile != null)
                {
                    databasePath.setText(lFile.getAbsolutePath());
                }
            }
        });
        browseButton.setIcon(new ImageIcon(Prefs.class.getClassLoader().getResource("assets/find.png")));

        // Install Help.
        // Listen for help from the F1 key.
        final HelpBroker lBroker = GuiUtil.getHelpBroker();
        lBroker.enableHelpKey(mainPanel, "preferences_html", null);
    }

    public static boolean editPreferences(JComponent aApp, Preferences aGlobalPreferences)
    {
        // Create the preferences component.
        final Prefs lPrefs = new Prefs(aGlobalPreferences);

        // Create a generator from the preferences.
        final Generator lDefaultGen = PreferencesUtil.getDefaultGenerator(aGlobalPreferences);
        final ChangeViewGenerator lGenModel = new ChangeViewGenerator(lDefaultGen);

        // Populate the dialog.
        lPrefs.populate(aGlobalPreferences, lGenModel);

        final int lUserResponse = JOptionPane.showOptionDialog(aApp, lPrefs.mainPanel, GuiUtil.getText("preferences.title"),
                                                               JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE,
                                                               null, null, null);

        switch(lUserResponse)
        {
            case JOptionPane.OK_OPTION:
                lPrefs.applyChanges(aGlobalPreferences, lGenModel);
                return true;
            case JOptionPane.CANCEL_OPTION:
            case JOptionPane.CLOSED_OPTION:
            default:
                return false;
        }
    }

    private void applyChanges(Preferences aPrefs, ChangeViewGenerator lGen)
    {
        // Fetch general properties.
        String lConvertMode = Preferences.CONVERT_NONE;
        if(convertAsk.isSelected()) lConvertMode = Preferences.CONVERT_ASK;
        else if(convertAuto.isSelected()) lConvertMode = Preferences.CONVERT_AUTO;
        aPrefs.setPreference(Preferences.PREF_CONVERT_MODE, lConvertMode);

        String lPreferredView = Preferences.VIEW_TABLE;
        if(selectTreeView.isSelected()) lPreferredView = Preferences.VIEW_TREE;
        aPrefs.setPreference(Preferences.PREF_DEFAULT_VIEW, lPreferredView);

        aPrefs.setPreference(Preferences.PREF_BUTTONS_LEFT, "" + leftSide.isSelected());
        aPrefs.setPreference(Preferences.PREF_BUTTONS_RIGHT, "" + rightSide.isSelected());
        aPrefs.setPreference(Preferences.PREF_ACTIVEDEFAULTUID, "" + activateDefaultUid.isSelected());
        aPrefs.setPreference(Preferences.PREF_DEFAULT_UID, defaultUid.getText());
        aPrefs.setPreference(Preferences.PREF_ACTIVEDEFAULTGROUP, "" + activateDefaultGroup.isSelected());
        aPrefs.setPreference(Preferences.PREF_DEFAULT_GROUP, defaultGroup.getText());
        aPrefs.setPreference(Preferences.PREF_DEFAULT_DB, databasePath.getText());
        aPrefs.setPreference(Preferences.PREF_VISIBLE_PWD, "" + showPassword.isSelected());
        aPrefs.setPreference(Preferences.PREF_SUGGEST_DEFAULT_DB, "" + suggestDefaultDb.isSelected());
        aPrefs.setPreference(Preferences.PREF_ADD_EXTENSION, "" + addExtension.isSelected());
        aPrefs.setPreference(Preferences.PREF_NR_BACKUPS, "" + nrBackups.getValue());
        aPrefs.setPreference(Preferences.PREF_WARN_ON_EXPORT, "" + warnExport.isSelected());
        aPrefs.setPreference(Preferences.PREF_STAY_ON_TOP, "" + stayPut.isSelected());

        // Retrieve the generator settings indirectly from the
        // generator model.
        if(lGen.isChanged())
        {
            PreferencesUtil.setPrefsFromGenerator(aPrefs, lGen);
        }
    }

    private void populate(Preferences aPrefs, ChangeViewGenerator lGen)
    {
        // Add a generator component tab.
        final JGeneratorProps lGenProps = new JGeneratorProps(lGen);
        mainPanel.addTab(GuiUtil.getText("preferences.generator"), lGenProps);

        // Populate the other fields.
        final String lConvertMode = aPrefs.getPref(Preferences.PREF_CONVERT_MODE);
        convertAsk.setSelected(Preferences.CONVERT_ASK.equals(lConvertMode));
        convertAuto.setSelected(Preferences.CONVERT_AUTO.equals(lConvertMode));
        convertNone.setSelected(Preferences.CONVERT_NONE.equals(lConvertMode));

        final String lPreferredView = aPrefs.getPref(Preferences.PREF_DEFAULT_VIEW);
        selectTableView.setSelected(Preferences.VIEW_TABLE.equals(lPreferredView));
        selectTreeView.setSelected(Preferences.VIEW_TREE.equals(lPreferredView));

        try
        {
            leftSide.setSelected(aPrefs.getBoolPref(Preferences.PREF_BUTTONS_LEFT));
            rightSide.setSelected(aPrefs.getBoolPref(Preferences.PREF_BUTTONS_RIGHT));

            activateDefaultUid.setSelected(aPrefs.getBoolPref(Preferences.PREF_ACTIVEDEFAULTUID));
            defaultUid.setText(aPrefs.getPref(Preferences.PREF_DEFAULT_UID));
            defaultUid.setEnabled(activateDefaultUid.isSelected());

            activateDefaultGroup.setSelected(aPrefs.getBoolPref(Preferences.PREF_ACTIVEDEFAULTGROUP));
            defaultGroup.setText(aPrefs.getPref(Preferences.PREF_DEFAULT_GROUP));
            defaultGroup.setEnabled(activateDefaultGroup.isSelected());

            databasePath.setText(aPrefs.getPref(Preferences.PREF_DEFAULT_DB));
            showPassword.setSelected(aPrefs.getBoolPref(Preferences.PREF_VISIBLE_PWD));
            suggestDefaultDb.setSelected(aPrefs.getBoolPref(Preferences.PREF_SUGGEST_DEFAULT_DB));
            addExtension.setSelected(aPrefs.getBoolPref(Preferences.PREF_ADD_EXTENSION));
            nrBackups.setValue(new Integer(aPrefs.getIntPref(Preferences.PREF_NR_BACKUPS)));
            warnExport.setSelected(aPrefs.getBoolPref(Preferences.PREF_WARN_ON_EXPORT));
            stayPut.setSelected(aPrefs.getBoolPref(Preferences.PREF_STAY_ON_TOP));
        }
        catch(PreferencesException eIgnore)
        {
            ;
        }
    }

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$()
    {
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        mainPanel = new JTabbedPane();
        panel1.add(mainPanel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, new Dimension(300, 200), null, 0, false));
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridLayoutManager(4, 2, new Insets(0, 0, 0, 0), -1, -1));
        mainPanel.addTab(ResourceBundle.getBundle("guiBundle").getString("preferences.general"), panel2);
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel2.add(panel3, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        panel3.setBorder(BorderFactory.createTitledBorder(ResourceBundle.getBundle("guiBundle").getString("preferences.defaultview")));
        selectTableView = new JRadioButton();
        selectTableView.setSelected(true);
        this.$$$loadButtonText$$$(selectTableView, ResourceBundle.getBundle("guiBundle").getString("preferences.defaultview.table"));
        panel3.add(selectTableView, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        selectTreeView = new JRadioButton();
        this.$$$loadButtonText$$$(selectTreeView, ResourceBundle.getBundle("guiBundle").getString("preferences.defaultview.tree"));
        panel3.add(selectTreeView, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel4 = new JPanel();
        panel4.setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel2.add(panel4, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        panel4.setBorder(BorderFactory.createTitledBorder(ResourceBundle.getBundle("guiBundle").getString("preferences.easyaccess")));
        leftSide = new JCheckBox();
        this.$$$loadButtonText$$$(leftSide, ResourceBundle.getBundle("guiBundle").getString("preferences.easyaccess.left"));
        panel4.add(leftSide, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel5 = new JPanel();
        panel5.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel4.add(panel5, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        rightSide = new JCheckBox();
        rightSide.setSelected(true);
        this.$$$loadButtonText$$$(rightSide, ResourceBundle.getBundle("guiBundle").getString("preferences.easyaccess.right"));
        panel5.add(rightSide, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel6 = new JPanel();
        panel6.setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel2.add(panel6, new GridConstraints(0, 0, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        panel6.setBorder(BorderFactory.createTitledBorder(ResourceBundle.getBundle("guiBundle").getString("preferences.defaultsafe")));
        final JPanel panel7 = new JPanel();
        panel7.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        panel6.add(panel7, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        browseButton = new JButton();
        this.$$$loadButtonText$$$(browseButton, ResourceBundle.getBundle("guiBundle").getString("general.browse"));
        panel7.add(browseButton, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        databasePath = new JTextField();
        databasePath.setEditable(true);
        databasePath.setText("");
        panel7.add(databasePath, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final Spacer spacer1 = new Spacer();
        panel2.add(spacer1, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final JPanel panel8 = new JPanel();
        panel8.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel2.add(panel8, new GridConstraints(1, 0, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        panel8.setBorder(BorderFactory.createTitledBorder(ResourceBundle.getBundle("guiBundle").getString("preferences.defaultvalues")));
        final JPanel panel9 = new JPanel();
        panel9.setLayout(new GridLayoutManager(2, 3, new Insets(0, 4, 0, 0), -1, -1, false, true));
        panel8.add(panel9, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        defaultUid = new JTextField();
        defaultUid.setEnabled(false);
        panel9.add(defaultUid, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        activateDefaultUid = new JCheckBox();
        this.$$$loadButtonText$$$(activateDefaultUid, ResourceBundle.getBundle("guiBundle").getString("general.enabled"));
        panel9.add(activateDefaultUid, new GridConstraints(1, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        defaultGroup = new JTextField();
        defaultGroup.setEnabled(false);
        panel9.add(defaultGroup, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        activateDefaultGroup = new JCheckBox();
        this.$$$loadButtonText$$$(activateDefaultGroup, ResourceBundle.getBundle("guiBundle").getString("general.enabled"));
        panel9.add(activateDefaultGroup, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label1 = new JLabel();
        label1.setHorizontalAlignment(11);
        this.$$$loadLabelText$$$(label1, ResourceBundle.getBundle("guiBundle").getString("preferences.defaultvalues.group"));
        panel9.add(label1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, new Dimension(35, -1), null, null, 0, false));
        final JLabel label2 = new JLabel();
        label2.setHorizontalAlignment(11);
        this.$$$loadLabelText$$$(label2, ResourceBundle.getBundle("guiBundle").getString("preferences.defaultvalues.uid"));
        panel9.add(label2, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, new Dimension(15, -1), new Dimension(35, -1), null, 0, false));
        final JPanel panel10 = new JPanel();
        panel10.setLayout(new GridLayoutManager(4, 1, new Insets(0, 0, 0, 0), -1, -1));
        mainPanel.addTab(ResourceBundle.getBundle("guiBundle").getString("preferences.options"), panel10);
        final JPanel panel11 = new JPanel();
        panel11.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel10.add(panel11, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        panel11.setBorder(BorderFactory.createTitledBorder(ResourceBundle.getBundle("guiBundle").getString("preferences.backup")));
        final JPanel panel12 = new JPanel();
        panel12.setLayout(new GridLayoutManager(1, 3, new Insets(0, 0, 0, 0), -1, -1));
        panel11.add(panel12, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final Spacer spacer2 = new Spacer();
        panel12.add(spacer2, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        nrBackups = new JSpinner();
        panel12.add(nrBackups, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(35, -1), new Dimension(35, -1), 0, false));
        final JLabel label3 = new JLabel();
        this.$$$loadLabelText$$$(label3, ResourceBundle.getBundle("guiBundle").getString("preferences.backup.copies"));
        panel12.add(label3, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel13 = new JPanel();
        panel13.setLayout(new GridLayoutManager(1, 4, new Insets(0, 0, 0, 0), -1, -1));
        panel10.add(panel13, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        panel13.setBorder(BorderFactory.createTitledBorder(ResourceBundle.getBundle("guiBundle").getString("preferences.conversion")));
        convertAuto = new JRadioButton();
        this.$$$loadButtonText$$$(convertAuto, ResourceBundle.getBundle("guiBundle").getString("preferences.conversion.auto"));
        panel13.add(convertAuto, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        convertAsk = new JRadioButton();
        this.$$$loadButtonText$$$(convertAsk, ResourceBundle.getBundle("guiBundle").getString("preferences.conversion.ask"));
        panel13.add(convertAsk, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        convertNone = new JRadioButton();
        convertNone.setSelected(true);
        this.$$$loadButtonText$$$(convertNone, ResourceBundle.getBundle("guiBundle").getString("preferences.conversion.never"));
        panel13.add(convertNone, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer3 = new Spacer();
        panel13.add(spacer3, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final JPanel panel14 = new JPanel();
        panel14.setLayout(new GridLayoutManager(5, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel10.add(panel14, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        panel14.setBorder(BorderFactory.createTitledBorder(ResourceBundle.getBundle("guiBundle").getString("preferences.otheroptions")));
        showPassword = new JCheckBox();
        this.$$$loadButtonText$$$(showPassword, ResourceBundle.getBundle("guiBundle").getString("preferences.otheroptions.showpwd"));
        panel14.add(showPassword, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        suggestDefaultDb = new JCheckBox();
        this.$$$loadButtonText$$$(suggestDefaultDb, ResourceBundle.getBundle("guiBundle").getString("preferences.otheroptions.defaultdb"));
        panel14.add(suggestDefaultDb, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        addExtension = new JCheckBox();
        this.$$$loadButtonText$$$(addExtension, ResourceBundle.getBundle("guiBundle").getString("preferences.otheroptions.fileext"));
        panel14.add(addExtension, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        warnExport = new JCheckBox();
        this.$$$loadButtonText$$$(warnExport, ResourceBundle.getBundle("guiBundle").getString("preferences.otheroptions.exportwarn"));
        panel14.add(warnExport, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        stayPut = new JCheckBox();
        this.$$$loadButtonText$$$(stayPut, ResourceBundle.getBundle("guiBundle").getString("preferences.otheroptions.stayontop"));
        panel14.add(stayPut, new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer4 = new Spacer();
        panel10.add(spacer4, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    private void $$$loadLabelText$$$(JLabel component, String text)
    {
        StringBuffer result = new StringBuffer();
        boolean haveMnemonic = false;
        char mnemonic = '\0';
        int mnemonicIndex = -1;
        for(int i = 0; i < text.length(); i++)
        {
            if(text.charAt(i) == '&')
            {
                i++;
                if(i == text.length()) break;
                if(!haveMnemonic && text.charAt(i) != '&')
                {
                    haveMnemonic = true;
                    mnemonic = text.charAt(i);
                    mnemonicIndex = result.length();
                }
            }
            result.append(text.charAt(i));
        }
        component.setText(result.toString());
        if(haveMnemonic)
        {
            component.setDisplayedMnemonic(mnemonic);
            component.setDisplayedMnemonicIndex(mnemonicIndex);
        }
    }

    /**
     * @noinspection ALL
     */
    private void $$$loadButtonText$$$(AbstractButton component, String text)
    {
        StringBuffer result = new StringBuffer();
        boolean haveMnemonic = false;
        char mnemonic = '\0';
        int mnemonicIndex = -1;
        for(int i = 0; i < text.length(); i++)
        {
            if(text.charAt(i) == '&')
            {
                i++;
                if(i == text.length()) break;
                if(!haveMnemonic && text.charAt(i) != '&')
                {
                    haveMnemonic = true;
                    mnemonic = text.charAt(i);
                    mnemonicIndex = result.length();
                }
            }
            result.append(text.charAt(i));
        }
        component.setText(result.toString());
        if(haveMnemonic)
        {
            component.setMnemonic(mnemonic);
            component.setDisplayedMnemonicIndex(mnemonicIndex);
        }
    }
}
