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

package com.sdi.pws.gui.dialog.edit;

import com.sdi.pws.db.*;
import com.sdi.pws.generator.Generator;
import com.sdi.pws.gui.dialog.generate.Generate;
import com.sdi.pws.gui.GuiUtil;
import com.sdi.pws.gui.CyclicFocusPolicy;
import com.sdi.pws.preferences.Preferences;
import com.sdi.pws.preferences.PreferencesException;
import com.sdi.pws.util.PreferencesUtil;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.SupportCode;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.Spacer;

import javax.swing.*;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import javax.help.HelpBroker;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.*;
import java.util.*;

public class Editor2
{
    private JComboBox group;
    private JTextField title;
    private JCheckBox showCheck;
    private JButton generateButton;
    private JTextPane notes;
    private JPasswordField pwd;
    private JTextField uid;
    private JPanel mainPanel;
    private JButton customButton;

    private String origGroup = null;
    private String origTitle = null;
    private String origUid = null;
    private String origPwd = null;
    private String origNotes = null;

    // Static entry methods.

    public static void createNew(JComponent aApp, PwsDatabase aDb, Preferences aPrefs)
    {
        final PwsRecord lNewRecord = new PwsRecordImpl();

        // Fill it with default values if needed.
        try
        {
            if(aPrefs.getBoolPref(Preferences.PREF_ACTIVEDEFAULTUID) && aPrefs.hasPreference(Preferences.PREF_DEFAULT_UID))
            {
                final PwsField lDefaultUid = new PwsFieldImpl(PwsField.FIELD_UID, aPrefs.getPref(Preferences.PREF_DEFAULT_UID).getBytes("utf8"));
                lNewRecord.put(lDefaultUid);
            }
            if(aPrefs.getBoolPref(Preferences.PREF_ACTIVEDEFAULTGROUP) && aPrefs.hasPreference(Preferences.PREF_DEFAULT_GROUP))
            {
                final PwsField lDefaultGroup = new PwsFieldImpl(PwsField.FIELD_GROUP, aPrefs.getPref(Preferences.PREF_DEFAULT_GROUP).getBytes("utf8"));
                lNewRecord.put(lDefaultGroup);
            }
        }
        catch(Exception eIgnore)
        {
            ;
        }

        if(edit(aApp, aDb, lNewRecord, aPrefs)) aDb.add(lNewRecord);
    }

    public static boolean edit(final JComponent aApp, PwsDatabase aDb, PwsRecord aRecord, final Preferences aPrefs)
    {
        final Editor2 lEditor = new Editor2();

        if(aPrefs.hasPreference(Preferences.PREF_VISIBLE_PWD))
            try
            {
                lEditor.showCheck.setSelected(aPrefs.getBoolPref(Preferences.PREF_VISIBLE_PWD));
            }
            catch(PreferencesException e)
            {
                ;
            }

        lEditor.populate(aRecord, aDb);
        lEditor.generateButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                final Generator lGen = PreferencesUtil.getDefaultGenerator(aPrefs);
                lEditor.pwd.setText(lGen.generate());
            }
        });
        lEditor.customButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                final String lNewPassword = Generate.generateNewPassword(lEditor.mainPanel, aPrefs);
                if(lNewPassword != null) lEditor.pwd.setText(lNewPassword);
            }
        });

        final JOptionPane lPane = new JOptionPane(lEditor.mainPanel, JOptionPane.PLAIN_MESSAGE, JOptionPane.OK_CANCEL_OPTION);
        final JDialog lDialog = lPane.createDialog(aApp, GuiUtil.getText("editor2.title"));
        lEditor.group.setFocusTraversalKeysEnabled(true);
        lDialog.setFocusTraversalPolicy(new CyclicFocusPolicy(new Component[]{lEditor.group, lEditor.title, lEditor.uid, lEditor.pwd, lEditor.generateButton, lEditor.customButton, lEditor.showCheck, lEditor.notes}));
        lDialog.setVisible(true);

        final Object lUserResponseObj = lPane.getValue();
        if(lUserResponseObj instanceof Integer)
        {
            final int lUserResponse = ((Integer) lUserResponseObj).intValue();
            switch(lUserResponse)
            {
                case JOptionPane.OK_OPTION:
                    lEditor.applyChanges(aRecord);
                    return true;
                case JOptionPane.CANCEL_OPTION:
                case JOptionPane.CLOSED_OPTION:
                default:
                    return false;
            }
        }
        else return false;
    }

    // Instance methods.

    public Editor2()
    {
        // Add a listener for the checkbox to change the appearance of the
        // password editing field.
        showCheck.addChangeListener(new ChangeListener()
        {
            public void stateChanged(ChangeEvent e)
            {
                if(((JCheckBox) e.getSource()).getModel().isSelected())
                    pwd.setEchoChar((char) 0);
                else
                    pwd.setEchoChar('*');
            }
        });

        group.setFont(title.getFont());

        // Install Help.
        // Listen for help from the F1 key.
        final HelpBroker lBroker = GuiUtil.getHelpBroker();
        lBroker.enableHelpKey(mainPanel, "editentry_html", null);
    }

    private void populate(PwsRecord aRecord, PwsDatabase aDb)
    {
        if(aRecord.hasType(PwsField.FIELD_GROUP)) try
        {
            origGroup = aRecord.get(PwsField.FIELD_GROUP).getAsString();
        }
        catch(Exception eIgnore)
        {
            ;
        }
        ;
        if(aRecord.hasType(PwsField.FIELD_TITLE)) try
        {
            origTitle = aRecord.get(PwsField.FIELD_TITLE).getAsString();
        }
        catch(Exception eIgnore)
        {
            ;
        }
        ;
        if(aRecord.hasType(PwsField.FIELD_UID)) try
        {
            origUid = aRecord.get(PwsField.FIELD_UID).getAsString();
        }
        catch(Exception eIgnore)
        {
            ;
        }
        ;
        if(aRecord.hasType(PwsField.FIELD_PWD)) try
        {
            origPwd = aRecord.get(PwsField.FIELD_PWD).getAsString();
        }
        catch(Exception eIgnore)
        {
            ;
        }
        ;
        if(aRecord.hasType(PwsField.FIELD_NOTES)) try
        {
            origNotes = aRecord.get(PwsField.FIELD_NOTES).getAsString();
        }
        catch(Exception eIgnore)
        {
            ;
        }
        ;

        group.setSelectedItem(EditorUtil.expandNull(origGroup));
        title.setText(EditorUtil.expandNull(origTitle));
        uid.setText(EditorUtil.expandNull(origUid));
        pwd.setText(EditorUtil.expandNull(origPwd));
        notes.setText(EditorUtil.expandNull(origNotes));

        // Populate the combo dropdown with all existing group names.
        final Set lGroupNames = new TreeSet();
        final Iterator lIter = aDb.iterator();
        while(lIter.hasNext())
        {
            final PwsRecord lRec = (PwsRecord) lIter.next();
            if(lRec.hasType(PwsField.FIELD_GROUP))
            {
                try
                {
                    lGroupNames.add(lRec.get(PwsField.FIELD_GROUP).getAsString());
                }
                catch(ModelException eIgnore)
                {
                    ;
                }
            }
        }
        final Iterator lNamesIter = lGroupNames.iterator();
        while(lNamesIter.hasNext()) group.addItem(lNamesIter.next());
    }

    private void applyChanges(PwsRecord aRecord)
    {
        final String lNewGroup = EditorUtil.trimAndReduceToNull((String) group.getSelectedItem());
        final String lNewTitle = EditorUtil.trimAndReduceToNull(title.getText());
        final String lNewUid = EditorUtil.trimAndReduceToNull(uid.getText());
        final String lNewPwd = EditorUtil.trimAndReduceToNull(new String(pwd.getPassword()));
        final String lNewNotes = EditorUtil.trimAndReduceToNull(notes.getText());

        EditorUtil.handleDifference(aRecord, origGroup, lNewGroup, PwsField.FIELD_GROUP);
        EditorUtil.handleDifference(aRecord, origTitle, lNewTitle, PwsField.FIELD_TITLE);
        EditorUtil.handleDifference(aRecord, origUid, lNewUid, PwsField.FIELD_UID);
        EditorUtil.handleDifference(aRecord, origPwd, lNewPwd, PwsField.FIELD_PWD);
        EditorUtil.handleDifference(aRecord, origNotes, lNewNotes, PwsField.FIELD_NOTES);
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
        mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayoutManager(6, 4, new Insets(0, 5, 5, 5), -1, -1));
        final JLabel label1 = new JLabel();
        label1.setHorizontalAlignment(4);
        label1.setHorizontalTextPosition(4);
        this.$$$loadLabelText$$$(label1, ResourceBundle.getBundle("guiBundle").getString("editor2.uid"));
        mainPanel.add(label1, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, new Dimension(40, -1), null, null, 0, false));
        uid = new JTextField();
        uid.setText("");
        mainPanel.add(uid, new GridConstraints(2, 1, 1, 3, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JLabel label2 = new JLabel();
        label2.setHorizontalAlignment(4);
        label2.setHorizontalTextPosition(4);
        this.$$$loadLabelText$$$(label2, ResourceBundle.getBundle("guiBundle").getString("editor2.pwd"));
        mainPanel.add(label2, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, new Dimension(40, -1), null, null, 0, false));
        pwd = new JPasswordField();
        mainPanel.add(pwd, new GridConstraints(3, 1, 1, 3, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JScrollPane scrollPane1 = new JScrollPane();
        mainPanel.add(scrollPane1, new GridConstraints(5, 1, 1, 3, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, new Dimension(80, 80), null, 0, false));
        notes = new JTextPane();
        scrollPane1.setViewportView(notes);
        final JLabel label3 = new JLabel();
        label3.setHorizontalAlignment(4);
        label3.setHorizontalTextPosition(4);
        this.$$$loadLabelText$$$(label3, ResourceBundle.getBundle("guiBundle").getString("editor2.notes"));
        mainPanel.add(label3, new GridConstraints(5, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, new Dimension(40, -1), null, null, 0, false));
        final Spacer spacer1 = new Spacer();
        mainPanel.add(spacer1, new GridConstraints(4, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final Spacer spacer2 = new Spacer();
        mainPanel.add(spacer2, new GridConstraints(4, 0, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final JLabel label4 = new JLabel();
        label4.setHorizontalAlignment(4);
        label4.setHorizontalTextPosition(4);
        this.$$$loadLabelText$$$(label4, ResourceBundle.getBundle("guiBundle").getString("editor2.titfld"));
        mainPanel.add(label4, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, new Dimension(40, -1), null, null, 0, false));
        title = new JTextField();
        title.setEditable(true);
        title.setMargin(new Insets(1, 1, 1, 1));
        title.setText("");
        mainPanel.add(title, new GridConstraints(1, 1, 1, 3, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(250, -1), null, 0, false));
        final JLabel label5 = new JLabel();
        label5.setHorizontalAlignment(4);
        label5.setHorizontalTextPosition(4);
        this.$$$loadLabelText$$$(label5, ResourceBundle.getBundle("guiBundle").getString("editor2.group"));
        mainPanel.add(label5, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, new Dimension(40, -1), null, null, 0, false));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(1, 3, new Insets(0, 0, 0, 0), -1, -1));
        mainPanel.add(panel1, new GridConstraints(4, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        showCheck = new JCheckBox();
        this.$$$loadButtonText$$$(showCheck, ResourceBundle.getBundle("guiBundle").getString("editor2.show"));
        panel1.add(showCheck, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        customButton = new JButton();
        this.$$$loadButtonText$$$(customButton, ResourceBundle.getBundle("guiBundle").getString("editor2.generate2"));
        panel1.add(customButton, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        generateButton = new JButton();
        this.$$$loadButtonText$$$(generateButton, ResourceBundle.getBundle("guiBundle").getString("editor2.generate1"));
        panel1.add(generateButton, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        group = new JComboBox();
        group.setEditable(true);
        mainPanel.add(group, new GridConstraints(0, 1, 1, 3, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
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

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$()
    {
        return mainPanel;
    }
}