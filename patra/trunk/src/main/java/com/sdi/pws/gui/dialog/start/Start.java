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

package com.sdi.pws.gui.dialog.start;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import com.sdi.pws.gui.CyclicFocusPolicy;
import com.sdi.pws.gui.DatabaseHolder;
import com.sdi.pws.gui.GuiUtil;
import com.sdi.pws.gui.action.FileOpenOrNew;
import com.sdi.pws.preferences.Preferences;

import javax.help.CSH;
import javax.help.HelpBroker;
import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.ResourceBundle;

public class Start
{
    private JButton openButton;
    private JPasswordField password;
    private JPanel startPanel;
    private JTextField defaultPath;
    private JButton browseButton;
    private JButton cancelButton;
    private JButton helpButton;
    private JLabel logo;
    private JPanel headerPanel;
    private JTextPane headerText;

    // This class is a small modification on the FileOpenOrNew action. In this
    // class the password field is cleared each time the button field is pressed.
    // If an error occured during the opening of the database, the password field is reset.
    // Since this is a GUI issue, I choose to override the class here.
    private class MyGoAction
        extends FileOpenOrNew
    {
        public MyGoAction(JComponent aApplication, JPasswordField aPwdfield, DatabaseHolder aHolder, JTextComponent aPath, Preferences aPrefs)
        {
            super(aApplication, aPwdfield, aHolder, aPath, aPrefs);
        }

        public void actionPerformed(ActionEvent e)
        {
            super.actionPerformed(e);
            password.setText("");
        }
    }

    public static void startPws(JFrame aApp, DatabaseHolder aHolder, final Preferences aPrefs)
    {
        // Get the default file.
        String lFileName = aPrefs.getPref(Preferences.PREF_DEFAULT_DB);
        File lFile = lFileName == null ? null : new File(lFileName);

        // Create a frame to show our panel.
        final JDialog lStartDialog = new JDialog(aApp);
        lStartDialog.setModal(true);
        // Set minimum dimensions so that the dialog does not shrink below workable size.
        final int lMaxHeight = 210;
        lStartDialog.setMinimumSize(new Dimension(500, lMaxHeight));
        lStartDialog.setPreferredSize(new Dimension(600, lMaxHeight));
        lStartDialog.setMaximumSize(new Dimension(Integer.MAX_VALUE, lMaxHeight));
        lStartDialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        lStartDialog.setTitle(GuiUtil.getText("start.title"));

        // Create the panel and add it to the frame.
        final Start lStart = new Start();
        lStartDialog.setContentPane(lStart.startPanel);

        // Load the logo
        lStart.logo.setIcon(new ImageIcon(Start.class.getClassLoader().getResource("assets/pwt-logo-small.gif")));
        lStart.headerPanel.setBackground(Color.white);
        lStart.headerText.setText(GuiUtil.getText("start.bannertext"));

        // Initialize the panel data.
        if(lFile == null)
        {
            // There is no default database.
            lStart.defaultPath.setText(GuiUtil.getText("start.initialpath"));
        }
        else
        {
            lStart.defaultPath.setText(lFileName);
        }

        // Install a Swing action to do the actual work in the go-button.
        final Action lOpenDefaultAction = lStart.new MyGoAction(lStart.startPanel, lStart.password, aHolder, lStart.defaultPath, aPrefs);
        lStart.openButton.setAction(lOpenDefaultAction);
        lStartDialog.getRootPane().setDefaultButton(lStart.openButton);
        lStartDialog.setFocusTraversalPolicy(new CyclicFocusPolicy(new Component[]{lStart.password, lStart.defaultPath}));

        // Listen to the browse button.
        lStart.browseButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                final File lSafe = GuiUtil.browseForSafe(lStart.startPanel, aPrefs);
                if(lSafe != null)
                {
                    lStart.defaultPath.setText(lSafe.getAbsolutePath());
                }
            }
        });

        // Install Help.
        //////////////////////////////////////////////////////////////////////
        // Listen for help from the help button.
        final HelpBroker lBroker = GuiUtil.getHelpBroker();
        lBroker.setCurrentID("startscreen_html");
        lStart.helpButton.addActionListener(new CSH.DisplayHelpFromSource(lBroker));
        // Listen for help from the F1 key.
        lBroker.enableHelpKey(lStart.startPanel, "startscreen_html", null);
        //////////////////////////////////////////////////////////////////////

        // If the database is created/opened we can quit the inital dialog.
        final PropertyChangeListener lDbListener = new PropertyChangeListener()
        {
            public void propertyChange(PropertyChangeEvent evt)
            {
                lStartDialog.setVisible(false);
            }
        };

        // If the dialog box close button is pressed the application terminates
        // immediately without further notice.
        lStartDialog.addWindowListener(new WindowAdapter()
        {
            public void windowClosing(WindowEvent e)
            {
                System.exit(0);
            }
        });

        // Listen to the cancel button.
        lStart.cancelButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                System.exit(0);
            }
        });

        lStart.startPanel.registerKeyboardAction(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                System.exit(0);
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        // Listen to the database holder in order to know if we created a db.
        aHolder.addPropertyChangeListener(lDbListener);
        // Prepare and show the initial dialog.
        lStartDialog.pack();

        GuiUtil.centerComponent(lStartDialog, null);
        lStartDialog.setVisible(true);
        // Remove the listener for the database holder.
        aHolder.removePropertyChangeListener(lDbListener);
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
        startPanel = new JPanel();
        startPanel.setLayout(new GridLayoutManager(4, 1, new Insets(0, 0, 0, 0), -1, -1));
        startPanel.setMinimumSize(new Dimension(-1, -1));
        startPanel.setPreferredSize(new Dimension(600, 200));
        startPanel.setRequestFocusEnabled(false);
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(1, 4, new Insets(0, 0, 0, 0), -1, -1));
        startPanel.add(panel1, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        helpButton = new JButton();
        helpButton.setHorizontalAlignment(2);
        helpButton.setIcon(new ImageIcon(getClass().getResource("/assets/help.png")));
        this.$$$loadButtonText$$$(helpButton, ResourceBundle.getBundle("guiBundle").getString("general.help"));
        panel1.add(helpButton, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        cancelButton = new JButton();
        cancelButton.setHorizontalAlignment(2);
        cancelButton.setIcon(new ImageIcon(getClass().getResource("/assets/cancel.png")));
        this.$$$loadButtonText$$$(cancelButton, ResourceBundle.getBundle("guiBundle").getString("general.cancel"));
        panel1.add(cancelButton, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer1 = new Spacer();
        panel1.add(spacer1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, 1, null, null, null, 0, false));
        final Spacer spacer2 = new Spacer();
        panel1.add(spacer2, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, 1, null, null, null, 0, false));
        final Spacer spacer3 = new Spacer();
        startPanel.add(spacer3, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, new Dimension(-1, 3), new Dimension(-1, 3), null, 0, false));
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridLayoutManager(2, 3, new Insets(10, 5, 5, 5), -1, -1));
        startPanel.add(panel2, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label1 = new JLabel();
        this.$$$loadLabelText$$$(label1, ResourceBundle.getBundle("guiBundle").getString("start.uno"));
        panel2.add(label1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        defaultPath = new JTextField();
        defaultPath.setEditable(true);
        defaultPath.setText("");
        panel2.add(defaultPath, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_NORTHWEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, new Dimension(250, -1), new Dimension(250, -1), null, 0, false));
        browseButton = new JButton();
        browseButton.setHorizontalAlignment(2);
        browseButton.setIcon(new ImageIcon(getClass().getResource("/assets/find.png")));
        this.$$$loadButtonText$$$(browseButton, ResourceBundle.getBundle("guiBundle").getString("general.browse"));
        panel2.add(browseButton, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label2 = new JLabel();
        this.$$$loadLabelText$$$(label2, ResourceBundle.getBundle("guiBundle").getString("start.duo"));
        panel2.add(label2, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        password = new JPasswordField();
        password.setEnabled(true);
        panel2.add(password, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_NORTHWEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, new Dimension(250, -1), new Dimension(250, -1), null, 0, false));
        openButton = new JButton();
        openButton.setEnabled(true);
        openButton.setHorizontalAlignment(2);
        openButton.setIcon(new ImageIcon(getClass().getResource("/assets/go.png")));
        this.$$$loadButtonText$$$(openButton, ResourceBundle.getBundle("guiBundle").getString("start.gobutton"));
        panel2.add(openButton, new GridConstraints(1, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        headerPanel = new JPanel();
        headerPanel.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), 0, 0));
        headerPanel.setBackground(new Color(-1));
        startPanel.add(headerPanel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, new Dimension(-1, 66), new Dimension(-1, 66), new Dimension(-1, 66), 0, false));
        logo = new JLabel();
        logo.setIconTextGap(0);
        logo.setText("");
        logo.setVerticalAlignment(1);
        logo.setVerticalTextPosition(1);
        headerPanel.add(logo, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_CAN_GROW, new Dimension(143, 61), new Dimension(143, 61), null, 0, false));
        headerText = new JTextPane();
        headerText.setContentType("text/html");
        headerText.setEditable(false);
        headerText.setText("<html>\n  <head>\n    \n  </head>\n  <body>\n  </body>\n</html>\n");
        headerPanel.add(headerText, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_NORTHWEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, new Dimension(350, -1), new Dimension(350, -1), null, 0, false));
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
        return startPanel;
    }
}
