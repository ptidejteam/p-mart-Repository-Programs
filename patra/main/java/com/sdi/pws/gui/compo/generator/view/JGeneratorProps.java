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

package com.sdi.pws.gui.compo.generator.view;

import com.sdi.pws.gui.compo.generator.change.ChangeViewGenerator;
import com.sdi.pws.gui.dialog.generate.Generate;
import com.sdi.pws.gui.GuiUtil;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.SupportCode;
import com.intellij.uiDesigner.core.Spacer;

import javax.swing.*;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import javax.help.HelpBroker;
import java.util.Locale;
import java.util.ResourceBundle;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.*;
import java.text.MessageFormat;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;

public class JGeneratorProps
    extends JPanel
    implements PropertyChangeListener
{
    private ChangeViewGenerator generator;
    private boolean blockGeneratorEvents = false;

    private JPanel mainPanel;
    private JTextField qualityDescription;
    private JPanel alphabetPanel;
    private JPanel basicPanel;
    private JPanel lengthPanel;
    private JLabel lengthLabel;
    private JSpinner lengthSpinner;
    private JRadioButton randomSelection;
    private JRadioButton readableSelection;
    private JCheckBox mixedOption;
    private JCheckBox punctuationOption;
    private JCheckBox digitsOption;

    public JGeneratorProps(ChangeViewGenerator aGenerator)
    {
        generator = aGenerator;
        generator.addPropertyChangeListener(this);
        wireFormElements();
    }

    public void setModel(ChangeViewGenerator aGenerator)
    {
        generator.removePropertyChangeListener(this);
        generator = aGenerator;
        generator.addPropertyChangeListener(this);
    }

    public ChangeViewGenerator getModel()
    {
        return generator;
    }

    private void wireFormElements()
    {
        // Add the main panel of the dialog to our own.
        this.add(mainPanel);

        // Now we populate the dialog with the generator data.
        // Restrict the spinner to some reasonable limits.
        final SpinnerNumberModel lLengthModel = new SpinnerNumberModel(new Integer(generator.getLength()), new Integer(1), new Integer(100), new Integer(1));
        lengthSpinner.setModel(lLengthModel);
        lengthSpinner.addChangeListener(new ChangeListener()
        {
            public void stateChanged(ChangeEvent e)
            {
                unListen();
                generator.setLength(((Number) lLengthModel.getValue()).intValue());
                listen();
                qualityDescription.setText(formatQualityMessage(generator.getQualityCategory(), Locale.ENGLISH));
            }
        });
        // Enable the random/readable radio buttons.
        final ButtonGroup lGroup = new ButtonGroup();
        lGroup.add(randomSelection);
        lGroup.add(readableSelection);
        updateAlphabet();
        final ActionListener lRadioListener = new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                final boolean lReadable = readableSelection.isSelected();
                unListen();
                generator.setReadable(lReadable);
                listen();
                updateAlphabet();
            }
        };
        readableSelection.addActionListener(lRadioListener);
        randomSelection.addActionListener(lRadioListener);
        updateTypePwd();
        // Connect the check boxes.
        punctuationOption.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                unListen();
                generator.setPunctuationIncluded(punctuationOption.isSelected());
                listen();
                qualityDescription.setText(formatQualityMessage(generator.getQualityCategory(), Locale.ENGLISH));
            }
        });
        mixedOption.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                unListen();
                generator.setMixedCase(mixedOption.isSelected());
                listen();
                qualityDescription.setText(formatQualityMessage(generator.getQualityCategory(), Locale.ENGLISH));
            }
        });
        digitsOption.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                unListen();
                generator.setNumbersIncluded(digitsOption.isSelected());
                listen();
                qualityDescription.setText(formatQualityMessage(generator.getQualityCategory(), Locale.ENGLISH));
            }
        });
        // Initialize the comment box
        qualityDescription.setText(formatQualityMessage(generator.getQualityCategory(), Locale.ENGLISH));

        // Install Help.
        // Listen for help from the F1 key.
        final HelpBroker lBroker = GuiUtil.getHelpBroker();
        lBroker.enableHelpKey(mainPanel, "generator_html", null);
    }

    private static String formatQualityMessage(int aQualityCategory, Locale aLocale)
    {
        ResourceBundle lBundle = ResourceBundle.getBundle("generatorBundle", aLocale, Generate.class.getClassLoader());
        final String lMsg = lBundle.getString("catdesc");
        String lDesc = lBundle.getString("cat" + aQualityCategory);
        return MessageFormat.format(lMsg, new Object[]{new Integer(aQualityCategory), lDesc});
    }

    private void listen()
    {
        blockGeneratorEvents = false;
    }

    private void unListen()
    {
        blockGeneratorEvents = true;
    }

    public void propertyChange(PropertyChangeEvent aEvt)
    {
        if(!blockGeneratorEvents) return;
        {
            final String lProp = aEvt.getPropertyName();
            if("length".equals(lProp))
            {
                updateLength(aEvt.getNewValue());
            }
            else if("numbersIncluded".equals(lProp) || "punctuationIncluded".equals(lProp) || "mixedCase".equals(lProp))
            {
                updateAlphabet();
            }
            else if("readable".equals(lProp))
            {
                updateTypePwd();
            }
            else if("entropy".equals(lProp))
            {
                // This implementation does not allow to change entropy.
            }
        }
    }

    private void updateLength(Object aValue)
    {
        final SpinnerNumberModel lSpinnerModel = (SpinnerNumberModel) lengthSpinner.getModel();
        lSpinnerModel.setValue(aValue);
        qualityDescription.setText(formatQualityMessage(generator.getQualityCategory(), Locale.ENGLISH));
    }

    private void updateAlphabet()
    {
        boolean lReadable = generator.isReadable();
        digitsOption.setEnabled(!lReadable);
        punctuationOption.setEnabled(!lReadable);
        mixedOption.setEnabled(!lReadable);
        punctuationOption.setSelected(generator.isPunctuationIncluded());
        mixedOption.setSelected(generator.isMixedCase());
        digitsOption.setSelected(generator.isNumbersIncluded());
        qualityDescription.setText(formatQualityMessage(generator.getQualityCategory(), Locale.ENGLISH));
    }

    private void updateTypePwd()
    {
        readableSelection.setSelected(generator.isReadable());
        randomSelection.setSelected(!generator.isReadable());
        qualityDescription.setText(formatQualityMessage(generator.getQualityCategory(), Locale.ENGLISH));
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
        mainPanel.setLayout(new GridLayoutManager(3, 1, new Insets(2, 2, 2, 2), -1, -1));
        basicPanel = new JPanel();
        basicPanel.setLayout(new GridLayoutManager(3, 1, new Insets(0, 0, 0, 0), -1, -1));
        mainPanel.add(basicPanel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, new Dimension(300, -1), null, null, 0, false));
        basicPanel.setBorder(BorderFactory.createTitledBorder(ResourceBundle.getBundle("guiBundle").getString("generator.props")));
        readableSelection = new JRadioButton();
        this.$$$loadButtonText$$$(readableSelection, ResourceBundle.getBundle("guiBundle").getString("generator.props.readable"));
        basicPanel.add(readableSelection, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        randomSelection = new JRadioButton();
        this.$$$loadButtonText$$$(randomSelection, ResourceBundle.getBundle("guiBundle").getString("generator.props.random"));
        basicPanel.add(randomSelection, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        lengthPanel = new JPanel();
        lengthPanel.setLayout(new GridLayoutManager(1, 3, new Insets(0, 0, 0, 0), -1, -1));
        basicPanel.add(lengthPanel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final Spacer spacer1 = new Spacer();
        lengthPanel.add(spacer1, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        lengthSpinner = new JSpinner();
        lengthPanel.add(lengthSpinner, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, new Dimension(50, -1), null, null, 0, false));
        lengthLabel = new JLabel();
        lengthLabel.setHorizontalAlignment(11);
        lengthLabel.setHorizontalTextPosition(4);
        this.$$$loadLabelText$$$(lengthLabel, ResourceBundle.getBundle("guiBundle").getString("generator.props.length"));
        lengthPanel.add(lengthLabel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, new Dimension(45, -1), null, null, 0, false));
        alphabetPanel = new JPanel();
        alphabetPanel.setLayout(new GridLayoutManager(3, 1, new Insets(0, 0, 0, 0), -1, -1));
        mainPanel.add(alphabetPanel, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        alphabetPanel.setBorder(BorderFactory.createTitledBorder(ResourceBundle.getBundle("guiBundle").getString("generator.alphabet")));
        punctuationOption = new JCheckBox();
        this.$$$loadButtonText$$$(punctuationOption, ResourceBundle.getBundle("guiBundle").getString("generator.alphabet.punctuation"));
        alphabetPanel.add(punctuationOption, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        digitsOption = new JCheckBox();
        this.$$$loadButtonText$$$(digitsOption, ResourceBundle.getBundle("guiBundle").getString("generator.alphabet.digits"));
        alphabetPanel.add(digitsOption, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        mixedOption = new JCheckBox();
        this.$$$loadButtonText$$$(mixedOption, ResourceBundle.getBundle("guiBundle").getString("generator.alphabet.case"));
        alphabetPanel.add(mixedOption, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
        mainPanel.add(panel1, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        qualityDescription = new JTextField();
        qualityDescription.setEditable(false);
        qualityDescription.setText("Safe against: script kiddies");
        panel1.add(qualityDescription, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final Spacer spacer2 = new Spacer();
        panel1.add(spacer2, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
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
