/*
 * Description.java
 * Copyright 2001 (C) Bryan McRoberts <mocha@mcs.net>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * Created on April 21, 2001, 2:15 PM
 */

package pcgen.gui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JViewport;
import javax.swing.JCheckBox;
import java.util.ArrayList;
import java.util.Iterator;
import javax.swing.BorderFactory; 
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.Box;
import javax.swing.BoxLayout;

import pcgen.core.Globals;
import pcgen.core.PCClass;
import pcgen.core.PlayerCharacter;
import pcgen.core.Race;
import pcgen.util.Delta;
import pcgen.core.Names;

/**
 * <code>Description</code> creates a new tabbed panel.
 *
 * @author Mario Bonassin <zebuleon@users.sourceforge.net>
 * @version $Revision: 1.1 $
 */
class Description extends JPanel
{
        JPanel northPanel = new JPanel();
        JPanel centerPanel = new JPanel();
        JPanel centerNorthPanel = new JPanel();
        JPanel centerCenterPanel = new JPanel();

        JTextField txtName = new JTextField(Globals.getCurrentPC().getName());

        JTextField playerNameText = new JTextField(Globals.getCurrentPC().getPlayersName());
        JTextField skinText = new JTextField(Globals.getCurrentPC().getSkinColor());
        JTextField hairColorText = new JTextField(Globals.getCurrentPC().getHairColor());
        JTextField hairStyleText = new JTextField(Globals.getCurrentPC().getHairStyle());
        JTextField eyeColorText = new JTextField(Globals.getCurrentPC().getEyeColor());
        JTextField speechPatternText = new JTextField(Globals.getCurrentPC().getSpeechTendency());
        JTextField phobiaText = new JTextField(Globals.getCurrentPC().getPhobias());
        JTextField interestsText = new JTextField(Globals.getCurrentPC().getInterests());
        JTextField catchPhraseText = new JTextField(Globals.getCurrentPC().getCatchPhrase());
        JTextField personality1Text = new JTextField(Globals.getCurrentPC().getTrait1());
        JTextField personality2Text = new JTextField(Globals.getCurrentPC().getTrait2());
        JTextField residenceText = new JTextField(Globals.getCurrentPC().getResidence());
        JTextField locationText = new JTextField(Globals.getCurrentPC().getLocation());
        JTextArea bioText = new JTextArea(Globals.getCurrentPC().getBio());
        JTextArea descriptionText = new JTextArea(Globals.getCurrentPC().getDescription());

        private boolean d_shown = false;

        JComboBox handedComboBox = new JComboBox();
        JComboBox genderComboBox = new JComboBox(); 
        FocusAdapter ageAdapter = null;
        FocusAdapter htAdapter = null;
        FocusAdapter wtAdapter = null;
        FocusAdapter playerNameAdapter = null;
        WholeNumberField ageText = new WholeNumberField(0, 0);
        WholeNumberField wtText = new WholeNumberField(0, 0);
        WholeNumberField htText = new WholeNumberField(0, 0);
        JCheckBox ageBox = new JCheckBox();
        JCheckBox wtBox = new JCheckBox();
        JCheckBox htBox = new JCheckBox();
        JCheckBox skinBox = new JCheckBox();
        JCheckBox hairColorBox = new JCheckBox();
        JCheckBox hairStyleBox = new JCheckBox();
        JCheckBox eyeColorBox = new JCheckBox();
        JCheckBox speechPatternBox = new JCheckBox();
        JCheckBox phobiaBox = new JCheckBox();
        JCheckBox interestsBox = new JCheckBox();
        JCheckBox catchPhraseBox = new JCheckBox();
        JCheckBox personality1Box = new JCheckBox();
        JCheckBox personality2Box = new JCheckBox();
        JCheckBox residenceBox = new JCheckBox();
        JCheckBox locationBox = new JCheckBox();
        
        private Names names = new Names();
        private JButton randName = new JButton("Random");
        private JButton randAll = new JButton("Random");
        private JButton checkAll = new JButton("Check All");
        private JButton uncheckAll = new JButton("Uncheck All");
        final JLabel labelName = new JLabel("Name: ");
  
        private NameGui nameFrame = null;
        Border etched;
        TitledBorder titled;
        
        //original code for comboboxes
        ActionListener al1 = new ActionListener() {
                        /**
                         *  Anonymous event handler
                         *
                         * @param  evt  The ActionEvent
                         * @since
                         */
                        public void actionPerformed(ActionEvent evt)
                        {
                                if (handedComboBox != null && handedComboBox.getSelectedItem() != null)
                                {
                                        final PlayerCharacter aPC = Globals.getCurrentPC();
                                        aPC.setDirty(true);
                                        aPC.setHanded(handedComboBox.getSelectedItem().toString());
                                }
                        }
                };
        
        ActionListener al2 = new ActionListener() {
                        /**
                         *  Anonymous event handler
                         *
                         * @param  evt  The ActionEvent
                         * @since
                         */
                        public void actionPerformed(ActionEvent evt)
                        {
                                if (genderComboBox != null && genderComboBox.getSelectedItem() != null)
                                {
                                        final PlayerCharacter aPC = Globals.getCurrentPC();
                                        aPC.setDirty(true);
                                        aPC.setGender(genderComboBox.getSelectedItem().toString());
                                }
                        }
                };
        //end original code for comboboxes
        
        
        //Set up GridBag Constraints
        void buildConstraints(GridBagConstraints gbc, int gx, int gy, int gw, int gh, int wx, int wy)
        {
                gbc.gridx = gx;
                gbc.gridy = gy;
                gbc.gridwidth = gw;
                gbc.gridheight = gh;
                gbc.weightx = wx;
                gbc.weighty = wy;
        }  
        
        public Description()
        {
                initComponents();
                refreshDisplay();
        }
 
        private void initComponents()
        {
                GridBagLayout gridbag = new GridBagLayout();
                GridBagConstraints c = new GridBagConstraints();
                northPanel.setLayout(gridbag);
                
                buildConstraints(c, 0, 0, 1, 1, 5, 10);
                c.fill = GridBagConstraints.NONE;
                c.anchor = GridBagConstraints.EAST;
                
                gridbag.setConstraints(labelName, c);
                northPanel.add(labelName);
                
                buildConstraints(c, 1, 0, 1, 1, 5, 10);
                c.fill = GridBagConstraints.HORIZONTAL;
                c.anchor = GridBagConstraints.WEST;
                gridbag.setConstraints(txtName, c);
                northPanel.add(txtName);
                
                buildConstraints(c, 2, 0, 2, 1, 5, 10);
                c.fill = GridBagConstraints.NONE;
                c.anchor = GridBagConstraints.WEST;
                gridbag.setConstraints(randName, c);
                northPanel.add(randName);
                randName.addActionListener(new ActionListener()
                        {
                                public void actionPerformed(ActionEvent evt)
                                {
                                        if (nameFrame == null)
                                        {
                                                nameFrame = new NameGui();
                                        }
                                        nameFrame.setVisible(true);
                                }
                        });
    
                buildConstraints(c, 0, 1, 1, 1, 5, 10);
                c.fill = GridBagConstraints.NONE;
                c.anchor = GridBagConstraints.EAST;
                JLabel label = new JLabel ("Player: ");
                gridbag.setConstraints(label, c);
                northPanel.add(label);
                
                buildConstraints(c, 1, 1, 1, 1, 5, 10);
                c.fill = GridBagConstraints.HORIZONTAL;
                c.anchor = GridBagConstraints.WEST;
                gridbag.setConstraints(playerNameText, c);
                northPanel.add(playerNameText);
                
                buildConstraints(c, 6, 0, 1, 1, 5, 10);
                c.fill = GridBagConstraints.NONE;
                c.anchor = GridBagConstraints.EAST;
                label = new JLabel ("Gender: ");
                gridbag.setConstraints(label, c);
                northPanel.add(label);
                
                buildConstraints(c, 7, 0, 1, 1, 5, 10);
                c.fill = GridBagConstraints.HORIZONTAL;
                c.anchor = GridBagConstraints.WEST;
                gridbag.setConstraints(genderComboBox, c);
                northPanel.add(genderComboBox);
                /*genderComboBox.addActionListener(new ActionListener()
                  {
                  public void actionPerformed(ActionEvent evt)
                  {
                  if (genderComboBox != null && genderComboBox.getSelectedItem() != null)
                  {
                  final PlayerCharacter aPC = Globals.getCurrentPC();
                  aPC.setDirty(true);
                  aPC.setGender(genderComboBox.getSelectedItem().toString());
                  }
                  }
                  });*/
                
                genderComboBox.addItem("Male");
                genderComboBox.addItem("Female");
                genderComboBox.addItem("Nueter");
                genderComboBox.addItem("None");
                genderComboBox.addItem("Other");

                buildConstraints(c, 6, 1, 1, 1, 5, 10);
                c.fill = GridBagConstraints.NONE;
                c.anchor = GridBagConstraints.EAST;
                label = new JLabel ("Handed: ");
                gridbag.setConstraints(label, c);
                northPanel.add(label);
                
                buildConstraints(c, 7, 1, 1, 1, 5, 10);
                c.fill = GridBagConstraints.HORIZONTAL;
                c.anchor = GridBagConstraints.WEST;
                gridbag.setConstraints(handedComboBox, c);
                northPanel.add(handedComboBox);
                /*handedComboBox.addActionListener(new ActionListener()
                  {
                  public void actionPerformed(ActionEvent evt)
                  {
                  if (handedComboBox != null && handedComboBox.getSelectedItem() != null)
                  {
                  final PlayerCharacter aPC = Globals.getCurrentPC();
                  aPC.setDirty(true);
                  aPC.setHanded(handedComboBox.getSelectedItem().toString());
                  }
                  }
                  });*/
                
                handedComboBox.addItem("Right");
                handedComboBox.addItem("Left");
                handedComboBox.addItem("Ambidextrous");
                handedComboBox.addItem("None");
                handedComboBox.addItem("Other");
                
                buildConstraints(c, 0, 2, 1, 1, 5, 10);
                c.fill = GridBagConstraints.NONE;
                c.anchor = GridBagConstraints.EAST;
                label = new JLabel ("Height: ");
                gridbag.setConstraints(label, c);
                northPanel.add(label);
                
                buildConstraints(c, 1, 2, 1, 1, 5, 10);
                c.fill = GridBagConstraints.HORIZONTAL;
                c.anchor = GridBagConstraints.WEST;
                gridbag.setConstraints(htText, c);
                northPanel.add(htText);
                
                buildConstraints(c, 2, 2, 1, 1, 5, 10);
                c.fill = GridBagConstraints.NONE;
                c.anchor = GridBagConstraints.WEST;
                gridbag.setConstraints(htBox, c);
                northPanel.add(htBox);
                
                buildConstraints(c, 0, 3, 1, 1, 5, 10);
                c.fill = GridBagConstraints.NONE;
                c.anchor = GridBagConstraints.EAST;
                label = new JLabel ("Weight: ");
                gridbag.setConstraints(label, c);
                northPanel.add(label);
                
                buildConstraints(c, 1, 3, 1, 1, 5, 10);
                c.fill = GridBagConstraints.HORIZONTAL;
                c.anchor = GridBagConstraints.WEST;
                gridbag.setConstraints(wtText, c);
                northPanel.add(wtText);
                
                buildConstraints(c, 2, 3, 1, 1, 5, 10);
                c.fill = GridBagConstraints.NONE;
                c.anchor = GridBagConstraints.WEST;
                gridbag.setConstraints(wtBox, c);
                northPanel.add(wtBox);
                
                buildConstraints(c, 0, 4, 1, 1, 5, 10);
                c.fill = GridBagConstraints.NONE;
                c.anchor = GridBagConstraints.EAST;
                label = new JLabel ("Age: ");
                gridbag.setConstraints(label, c);
                northPanel.add(label);
                
                buildConstraints(c, 1, 4, 1, 1, 5, 10);
                c.fill = GridBagConstraints.HORIZONTAL;
                c.anchor = GridBagConstraints.WEST;
                gridbag.setConstraints(ageText, c);
                northPanel.add(ageText);
                
                buildConstraints(c, 2, 4, 1, 1, 5, 10);
                c.fill = GridBagConstraints.NONE;
                c.anchor = GridBagConstraints.WEST;
                gridbag.setConstraints(ageBox, c);
                northPanel.add(ageBox);
                
                
                buildConstraints(c, 6, 2, 1, 1, 5, 10);
                c.fill = GridBagConstraints.NONE;
                c.anchor = GridBagConstraints.EAST;
                label = new JLabel ("Skin: ");
                gridbag.setConstraints(label, c);
                northPanel.add(label);
                
                buildConstraints(c, 7, 2, 1, 1, 5, 10);
                c.fill = GridBagConstraints.HORIZONTAL;
                c.anchor = GridBagConstraints.WEST;
                gridbag.setConstraints(skinText, c);
                northPanel.add(skinText);
                
                buildConstraints(c, 8, 2, 1, 1, 5, 10);
                c.fill = GridBagConstraints.NONE;
                c.anchor = GridBagConstraints.WEST;
                gridbag.setConstraints(skinBox, c);
                northPanel.add(skinBox);
                
                buildConstraints(c, 3, 4, 1, 1, 5, 10);
                c.fill = GridBagConstraints.NONE;
                c.anchor = GridBagConstraints.EAST;
                label = new JLabel ("Phobias: ");
                gridbag.setConstraints(label, c);
                northPanel.add(label);
                
                buildConstraints(c, 4, 4, 1, 1, 5, 10);
                c.fill = GridBagConstraints.HORIZONTAL;
                c.anchor = GridBagConstraints.WEST;
                gridbag.setConstraints(phobiaText, c);
                northPanel.add(phobiaText);
                
                buildConstraints(c, 5, 4, 1, 1, 5, 10);
                c.fill = GridBagConstraints.NONE;
                c.anchor = GridBagConstraints.WEST;
                gridbag.setConstraints(phobiaBox, c);
                northPanel.add(phobiaBox);
                
                buildConstraints(c, 3, 2, 1, 1, 5, 10);
                c.fill = GridBagConstraints.NONE;
                c.anchor = GridBagConstraints.EAST;
                label = new JLabel ("Personality Trait: ");
                gridbag.setConstraints(label, c);
                northPanel.add(label);
                
                buildConstraints(c, 4, 2, 1, 1, 5, 10);
                c.fill = GridBagConstraints.HORIZONTAL;
                c.anchor = GridBagConstraints.WEST;
                gridbag.setConstraints(personality1Text, c);
                northPanel.add(personality1Text);
    
                buildConstraints(c, 5, 2, 1, 1, 5, 10);
                c.fill = GridBagConstraints.NONE;
                c.anchor = GridBagConstraints.WEST;
                gridbag.setConstraints(personality1Box, c);
                northPanel.add(personality1Box);
                
                buildConstraints(c, 6, 3, 1, 1, 5, 10);
                c.fill = GridBagConstraints.NONE;
                c.anchor = GridBagConstraints.EAST;
                label = new JLabel ("Eye Color: ");
                gridbag.setConstraints(label, c);
                northPanel.add(label);
                
                buildConstraints(c, 7, 3, 1, 1, 5, 10);
                c.fill = GridBagConstraints.HORIZONTAL;
                c.anchor = GridBagConstraints.WEST;
                gridbag.setConstraints(eyeColorText, c);
                northPanel.add(eyeColorText);
                
                buildConstraints(c, 8, 3, 1, 1, 5, 10);
                c.fill = GridBagConstraints.NONE;
                c.anchor = GridBagConstraints.WEST;
                gridbag.setConstraints(eyeColorBox, c);
                northPanel.add(eyeColorBox);

                buildConstraints(c, 3, 5, 1, 1, 5, 10);
                c.fill = GridBagConstraints.NONE;
                c.anchor = GridBagConstraints.EAST;
                label = new JLabel ("Interests: ");
                gridbag.setConstraints(label, c);
                northPanel.add(label);

                buildConstraints(c, 4, 5, 1, 1, 5, 10);
                c.fill = GridBagConstraints.HORIZONTAL;
                c.anchor = GridBagConstraints.WEST;
                gridbag.setConstraints(interestsText, c);
                northPanel.add(interestsText);

                buildConstraints(c, 5, 5, 1, 1, 5, 10);
                c.fill = GridBagConstraints.NONE;
                c.anchor = GridBagConstraints.WEST;
                gridbag.setConstraints(interestsBox, c);
                northPanel.add(interestsBox);
                
                buildConstraints(c, 3, 3, 1, 1, 5, 10);
                c.fill = GridBagConstraints.NONE;
                c.anchor = GridBagConstraints.EAST;
                label = new JLabel ("Personality Trait: ");
                gridbag.setConstraints(label, c);
                northPanel.add(label);
                
                buildConstraints(c, 4, 3, 1, 1, 5, 10);
                c.fill = GridBagConstraints.HORIZONTAL;
                c.anchor = GridBagConstraints.WEST;
                gridbag.setConstraints(personality2Text, c);
                northPanel.add(personality2Text);
                
                buildConstraints(c, 5, 3, 1, 1, 5, 10);
                c.fill = GridBagConstraints.NONE;
                c.anchor = GridBagConstraints.WEST;
                gridbag.setConstraints(personality2Box, c);
                northPanel.add(personality2Box);
    
                buildConstraints(c, 6, 4, 1, 1, 5, 10);
                c.fill = GridBagConstraints.NONE;
                c.anchor = GridBagConstraints.EAST;
                label = new JLabel ("Hair Color: ");
                gridbag.setConstraints(label, c);
                northPanel.add(label);

                buildConstraints(c, 7, 4, 1, 1, 5, 10);
                c.fill = GridBagConstraints.HORIZONTAL;
                c.anchor = GridBagConstraints.WEST;
                gridbag.setConstraints(hairColorText, c);
                northPanel.add(hairColorText);

                buildConstraints(c, 8, 4, 1, 1, 5, 10);
                c.fill = GridBagConstraints.NONE;
                c.anchor = GridBagConstraints.WEST;
                gridbag.setConstraints(hairColorBox, c);
                northPanel.add(hairColorBox);
                
                buildConstraints(c, 0, 6, 1, 1, 5, 10);
                c.fill = GridBagConstraints.NONE;
                c.anchor = GridBagConstraints.EAST;
                label = new JLabel ("Region: ");
                gridbag.setConstraints(label, c);
                northPanel.add(label);
    
////need to get Region info
                buildConstraints(c, 1, 6, 1, 1, 5, 10);
                c.fill = GridBagConstraints.HORIZONTAL;
                c.anchor = GridBagConstraints.WEST;
                gridbag.setConstraints(residenceText, c);
                northPanel.add(residenceText);
                
                // buildConstraints(c, 2, 6, 1, 1, 5, 10);
                // c.fill = GridBagConstraints.NONE;
                // c.anchor = GridBagConstraints.WEST;
                // gridbag.setConstraints(residenceBox, c);
                // northPanel.add(residenceBox);

                buildConstraints(c, 6, 6, 1, 1, 5, 10);
                c.fill = GridBagConstraints.NONE;
                c.anchor = GridBagConstraints.EAST;
                label = new JLabel ("Speech Pattern: ");
                gridbag.setConstraints(label, c);
                northPanel.add(label);

                buildConstraints(c, 7, 6, 1, 1, 5, 10);
                c.fill = GridBagConstraints.HORIZONTAL;
                c.anchor = GridBagConstraints.WEST;
                gridbag.setConstraints(speechPatternText, c);
                northPanel.add(speechPatternText);
    
                buildConstraints(c, 8, 6, 1, 1, 5, 10);
                c.fill = GridBagConstraints.NONE;
                c.anchor = GridBagConstraints.WEST;
                gridbag.setConstraints(speechPatternBox, c);
                northPanel.add(speechPatternBox);
    
                buildConstraints(c, 6, 5, 1, 1, 5, 10);
                c.fill = GridBagConstraints.NONE;
                c.anchor = GridBagConstraints.EAST;
                label = new JLabel ("Hair Style: ");
                gridbag.setConstraints(label, c);
                northPanel.add(label);
                
                buildConstraints(c, 7, 5, 1, 1, 5, 10);
                c.fill = GridBagConstraints.HORIZONTAL;
                c.anchor = GridBagConstraints.WEST;
                gridbag.setConstraints(hairStyleText, c);
                northPanel.add(hairStyleText);
                
                buildConstraints(c, 8, 5, 1, 1, 5, 10);
                c.fill = GridBagConstraints.NONE;
                c.anchor = GridBagConstraints.WEST;
                gridbag.setConstraints(hairStyleBox, c);
                northPanel.add(hairStyleBox);
    
                buildConstraints(c, 0, 5, 1, 1, 5, 10);
                c.fill = GridBagConstraints.NONE;
                c.anchor = GridBagConstraints.EAST;
                label = new JLabel ("Location: ");
                gridbag.setConstraints(label, c);
                northPanel.add(label);

                buildConstraints(c, 1, 5, 1, 1, 5, 10);
                c.fill = GridBagConstraints.HORIZONTAL;
                c.anchor = GridBagConstraints.WEST;
                gridbag.setConstraints(locationText, c);
                northPanel.add(locationText);

                buildConstraints(c, 2, 5, 1, 1, 5, 10);
                c.fill = GridBagConstraints.NONE;
                c.anchor = GridBagConstraints.WEST;
                gridbag.setConstraints(locationBox, c);
                northPanel.add(locationBox);

                buildConstraints(c, 3, 6, 1, 1, 5, 10);
                c.fill = GridBagConstraints.NONE;
                c.anchor = GridBagConstraints.EAST;
                label = new JLabel ("Catch Phrase: ");
                gridbag.setConstraints(label, c);
                northPanel.add(label);

                buildConstraints(c, 4, 6, 1, 1, 5, 10);
                c.fill = GridBagConstraints.HORIZONTAL;
                c.anchor = GridBagConstraints.WEST;
                gridbag.setConstraints(catchPhraseText, c);
                northPanel.add(catchPhraseText);
                
                buildConstraints(c, 5, 6, 1, 1, 5, 10);
                c.fill = GridBagConstraints.NONE;
                c.anchor = GridBagConstraints.WEST;
                gridbag.setConstraints(catchPhraseBox, c);
                northPanel.add(catchPhraseBox); 
    
    
                centerNorthPanel.setLayout(new BorderLayout());
                JPanel pane = new JPanel(new FlowLayout());
                
                pane.add(checkAll);
                checkAll.addActionListener( new ActionListener() {
                                /**
                                 *  Anonymous event handler
                                 *
                                 * @param  evt  The ActionEvent
                                 * @since
                                 */
                                public void actionPerformed(ActionEvent evt)
                                {
                                        checkAll_click();
                                        }
                        });
                
                pane.add(randAll);
                randAll.addActionListener( new ActionListener() {
                                public void actionPerformed(ActionEvent evt)
                                {
                                        randAll_click();
                                        updateTextFields();
                                        Globals.getCurrentPC().setDirty(true);
                                }
                        });
                

                pane.add(uncheckAll);
                uncheckAll.addActionListener( new ActionListener() {
                                /**
                                 *  Anonymous event handler
                                 *
                                 * @param  evt  The ActionEvent
                                 * @since
                                 */
                                public void actionPerformed(ActionEvent evt)
                                {
                                        uncheckAll_click();
                                }
                        });
                
                centerNorthPanel.add(pane, BorderLayout.NORTH);
                
                // Set Sizes of everything
                txtName.setMinimumSize(new Dimension(110, 21));
                txtName.setPreferredSize(new Dimension(200, 21));
                playerNameText.setMinimumSize(new Dimension(110, 21));
                playerNameText.setPreferredSize(new Dimension(200, 21));
                htText.setMinimumSize(new Dimension(110, 21));
                htText.setPreferredSize(new Dimension(200, 21));
                wtText.setMinimumSize(new Dimension(110, 21));
                wtText.setPreferredSize(new Dimension(200, 21));
                ageText.setMinimumSize(new Dimension(110, 21));
                ageText.setPreferredSize(new Dimension(200, 21));
                genderComboBox.setMinimumSize(new Dimension(110, 21));
                genderComboBox.setPreferredSize(new Dimension(200, 21));
                handedComboBox.setMinimumSize(new Dimension(110, 21));
                handedComboBox.setPreferredSize(new Dimension(200, 21));
                skinText.setMinimumSize(new Dimension(110, 21));
                skinText.setPreferredSize(new Dimension(200, 21));
                eyeColorText.setMinimumSize(new Dimension(110, 21));
                eyeColorText.setPreferredSize(new Dimension(200, 21));
                hairColorText.setMinimumSize(new Dimension(110, 21));
                hairColorText.setPreferredSize(new Dimension(200, 21));
                hairStyleText.setMinimumSize(new Dimension(110, 21));
                hairStyleText.setPreferredSize(new Dimension(200, 21));
                speechPatternText.setMinimumSize(new Dimension(110, 21));
                speechPatternText.setPreferredSize(new Dimension(200, 21));
                phobiaText.setMinimumSize(new Dimension(110, 21));
                phobiaText.setPreferredSize(new Dimension(200, 21));
                interestsText.setMinimumSize(new Dimension(110, 21));
                interestsText.setPreferredSize(new Dimension(200, 21));
                catchPhraseText.setMinimumSize(new Dimension(110, 21));
                catchPhraseText.setPreferredSize(new Dimension(200, 21));
                personality1Text.setMinimumSize(new Dimension(110, 21));
                personality1Text.setPreferredSize(new Dimension(200, 21));
                personality2Text.setMinimumSize(new Dimension(110, 21));
                personality2Text.setPreferredSize(new Dimension(200, 21));
                residenceText.setMinimumSize(new Dimension(110, 21));
                residenceText.setPreferredSize(new Dimension(200, 21));
                locationText.setMinimumSize(new Dimension(110, 21));
                locationText.setPreferredSize(new Dimension(200, 21));
                randAll.setMinimumSize(new Dimension(90, 25));
                randAll.setPreferredSize(new Dimension(90, 25));
                checkAll.setMinimumSize(new Dimension(90, 25));
                checkAll.setPreferredSize(new Dimension(90, 25));
                uncheckAll.setMinimumSize(new Dimension(110, 25));
                uncheckAll.setPreferredSize(new Dimension(110, 25));
                randName.setMinimumSize(new Dimension(90, 21));
                randName.setPreferredSize(new Dimension(90, 21));
    
    
                bioText.setLineWrap(true);
                bioText.setWrapStyleWord(true);
                descriptionText.setLineWrap(true);
                descriptionText.setWrapStyleWord(true);
                JScrollPane scroll = new JScrollPane();
                scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
                scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
                scroll.setViewportView(bioText);
                scroll.getViewport().setScrollMode(JViewport.BLIT_SCROLL_MODE);
                scroll.setBackground(new Color(255, 255, 255));
                JScrollPane scroll2 = new JScrollPane();
                scroll2.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
                scroll2.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
                scroll2.setViewportView(descriptionText);
                scroll2.getViewport().setScrollMode(JViewport.BLIT_SCROLL_MODE);
                scroll2.setBackground(new Color(255, 255, 255));
                JPanel pane1 = new JPanel();
                JPanel pane2 = new JPanel();
                pane1.add(scroll);
                pane2.add(scroll2);
                TitledBorder title1 = BorderFactory.createTitledBorder(etched, "Bio:");
                title1.setTitleJustification(TitledBorder.CENTER);
                pane1.setBorder(title1);
                pane1.setLayout(new BoxLayout(pane1, BoxLayout.Y_AXIS));
                
                TitledBorder title2 = BorderFactory.createTitledBorder(etched, "Description:");
                title2.setTitleJustification(TitledBorder.CENTER);
                pane2.setBorder(title2);
                pane2.setLayout(new BoxLayout(pane2, BoxLayout.Y_AXIS));
                
                centerCenterPanel.setLayout(new GridLayout(1, 2));
                centerCenterPanel.add(pane1);
                centerCenterPanel.add(pane2);
                centerPanel.setLayout(new BorderLayout());
                centerPanel.add(centerCenterPanel, BorderLayout.CENTER);
                centerPanel.add(centerNorthPanel, BorderLayout.NORTH);
                this.setLayout(new BorderLayout());
                this.add(northPanel, BorderLayout.NORTH);
                this.add(centerPanel, BorderLayout.CENTER);
                java.awt.event.FocusListener fl = new java.awt.event.FocusAdapter() {
                                public void focusLost(java.awt.event.FocusEvent evt)
                                {
                                        updateTextFields();
                                }
                        };
                skinText.addFocusListener(fl);
                hairColorText.addFocusListener(fl);
                hairStyleText.addFocusListener(fl);
                eyeColorText.addFocusListener(fl);
                speechPatternText.addFocusListener(fl);
                phobiaText.addFocusListener(fl);
                interestsText.addFocusListener(fl);
                catchPhraseText.addFocusListener(fl);
                personality1Text.addFocusListener(fl);
                personality2Text.addFocusListener(fl);
                residenceText.addFocusListener(fl);
                locationText.addFocusListener(fl);
                bioText.addFocusListener(fl);
                descriptionText.addFocusListener(fl);
                
                ageBox.setToolTipText("Check for Randomization");
                wtBox.setToolTipText("Check for Randomization");
                htBox.setToolTipText("Check for Randomization");
                skinBox.setToolTipText("Check for Randomization");
                hairColorBox.setToolTipText("Check for Randomization");
                hairStyleBox.setToolTipText("Check for Randomization");
                eyeColorBox.setToolTipText("Check for Randomization");
                speechPatternBox.setToolTipText("Check for Randomization");
                phobiaBox.setToolTipText("Check for Randomization");
                interestsBox.setToolTipText("Check for Randomization");
                catchPhraseBox.setToolTipText("Check for Randomization");
                personality1Box.setToolTipText("Check for Randomization");
                personality2Box.setToolTipText("Check for Randomization");
                residenceBox.setToolTipText("Check for Randomization");
                locationBox.setToolTipText("Check for Randomization");
                randName.setToolTipText("Click to get Random Name");
                randAll.setToolTipText("Click to get Random Traits");
                checkAll.setToolTipText("Click to select all Traits");
                uncheckAll.setToolTipText("Click to deselect all Traits");
                
                
                //Unsure Name code
                txtName.addActionListener( new java.awt.event.ActionListener() {
                                        /**
                                         *  Anonymous event handler
                                         *
                                         * @param  e  The ActionEvent
                                         * @since
                                         */
                                public void actionPerformed(ActionEvent e)
                                {
                                        txtName_Changed(e);
                                        labelName.requestFocus();
                                }
                        });
                
                txtName.addFocusListener(new java.awt.event.FocusAdapter() {
                                public void focusLost(java.awt.event.FocusEvent evt)
                                {
                                        txtName_Changed(evt);
                                }
                        });
                //end Unsure Name Code
                
        }
        
        
        private final void updateTextFields()
        {
                final PlayerCharacter aPC = Globals.getCurrentPC();
                aPC.setSkinColor(skinText.getText());
                aPC.setHairColor(hairColorText.getText());
                aPC.setHairStyle(hairStyleText.getText());
                aPC.setEyeColor(eyeColorText.getText());
                aPC.setSpeechTendency(speechPatternText.getText());
                aPC.setPhobias(phobiaText.getText());
                aPC.setInterests(interestsText.getText());
                aPC.setCatchPhrase(catchPhraseText.getText());
                aPC.setTrait1(personality1Text.getText());
                aPC.setTrait2(personality2Text.getText());
                aPC.setResidence(residenceText.getText());
                aPC.setLocation(locationText.getText());
                aPC.setBio(bioText.getText());
                aPC.setDescription(descriptionText.getText());
                aPC.setAge(Delta.parseInt("0" + ageText.getText()));
                aPC.setHeight(Delta.parseInt("0" + htText.getText()));
                aPC.setWeight(Delta.parseInt("0" + wtText.getText()));
        }  
  
        //Unsure Name code
        /**
         *  This method takes the name entered in the txtName field and makes it the
         *  name of the active tab.
         *
         * @param  e  The ActionEvent
         */
        void txtName_Changed(java.awt.AWTEvent e)
        {
                final PlayerCharacter aPC = Globals.getCurrentPC();
                if (aPC != null)
                {
                        aPC.setName(txtName.getText());
                        PCGen_Frame1.setTabName(txtName.getText());
                }
        }
        //end Unsure Name code
  
        /* This function is called when the "Check All" button is clicked and
         */
        private void checkAll_click()
        {
                ageBox.setSelected(true);
                wtBox.setSelected(true);
                htBox.setSelected(true);
                skinBox.setSelected(true);
                hairColorBox.setSelected(true);
                hairStyleBox.setSelected(true);
                eyeColorBox.setSelected(true);
                speechPatternBox.setSelected(true);
                phobiaBox.setSelected(true);
                interestsBox.setSelected(true);
                catchPhraseBox.setSelected(true);
                personality1Box.setSelected(true);
                personality2Box.setSelected(true);
                residenceBox.setSelected(true);
                locationBox.setSelected(true);
        }
        
        /* This function is called when the "Uncheck All" button is clicked and
         */
        private void uncheckAll_click()
        {
                ageBox.setSelected(false);
                wtBox.setSelected(false);
                htBox.setSelected(false);
                skinBox.setSelected(false);
                hairColorBox.setSelected(false);
                hairStyleBox.setSelected(false);
                eyeColorBox.setSelected(false);
                speechPatternBox.setSelected(false);
                phobiaBox.setSelected(false);
                interestsBox.setSelected(false);
                catchPhraseBox.setSelected(false);
                personality1Box.setSelected(false);
                personality2Box.setSelected(false);
                residenceBox.setSelected(false);
                locationBox.setSelected(false);
        }
        
  
        /* This function is called when the "Random" button is clicked and
         * randomly generates traits/colors for certain fields.
         * I will simplify and add more functionality for this later
         * with the other fields. 
         */
        private void randAll_click()
        {
                int i = 0;
                int roll = 0;
                final ArrayList globalColorList = pcgen.core.Globals.getColorList();
                final int colorNum = globalColorList.size();
                final ArrayList globalTraitList = pcgen.core.Globals.getTraitList();
                final ArrayList globalPhobiaList = pcgen.core.Globals.getPhobiaList();
                final ArrayList globalLocationList = pcgen.core.Globals.getLocationList();
                final ArrayList globalInterestsList = pcgen.core.Globals.getInterestsList();
                final ArrayList globalPhraseList = pcgen.core.Globals.getPhraseList();
                final ArrayList globalHairStyleList = pcgen.core.Globals.getHairStyleList();
                final ArrayList globalSpeechList = pcgen.core.Globals.getSpeechList();



                final int traitNum = globalTraitList.size();
                
                Iterator e1 = null;
                String aString = null;
                if (eyeColorBox.isSelected())
                {
                        roll = Globals.getRandomInt();
                        if (roll < 0) roll = -roll;
                        roll = roll % colorNum + 1;
                        for (e1 = globalColorList.iterator(); e1.hasNext();)
                        {
                                aString = (String)e1.next();
                                if (aString.substring(aString.lastIndexOf('\t')).lastIndexOf('E') >= 0)
                                        roll--;
                                if (roll == 0)
                                {
                                        eyeColorText.setText(aString.substring(0, aString.lastIndexOf('\t')));
                                        break;
                                }
                        }
                }
                if (hairColorBox.isSelected())
                {
                        roll = Globals.getRandomInt();
                        if (roll < 0) roll = -roll;
                        roll = roll % colorNum + 1;
                        for (e1 = globalColorList.iterator(); e1.hasNext();)
                        {
                                aString = (String)e1.next();
                                if (aString.substring(aString.lastIndexOf('\t')).lastIndexOf('H') >= 0)
                                        roll--;
                                if (roll == 0)
                                {
                                        hairColorText.setText(aString.substring(0, aString.lastIndexOf('\t')));
                                        break;
                                }
                        }
                }
                if (skinBox.isSelected())
                {
                        roll = Globals.getRandomInt();
                        if (roll < 0) roll = -roll;
                        roll = roll % colorNum + 1;
                        for (e1 = globalColorList.iterator(); e1.hasNext();)
                        {
                                aString = (String)e1.next();
                                if (aString.substring(aString.lastIndexOf('\t')).lastIndexOf('S') >= 0)
                                        roll--;
                                if (roll == 0)
                                {
                                        skinText.setText(aString.substring(0, aString.lastIndexOf('\t')));
                                        break;
                                }
                        }
                }
                if (personality1Box.isSelected())
                {
                        roll = Globals.getRandomInt();
                        if (roll < 0) roll = -roll;
                        roll = roll % traitNum + 1;
                        for (e1 = globalTraitList.iterator(); e1.hasNext();)
                        {
                                aString = (String)e1.next();
                                roll--;
                                if (roll == 0)
                                {
                                        personality1Text.setText(aString);
                                        break;
                                }
                        }
                }
                if (personality2Box.isSelected())
                {
                        roll = Globals.getRandomInt();
                        if (roll < 0) roll = -roll;
                        roll = roll % traitNum + 1;
                        for (e1 = globalTraitList.iterator(); e1.hasNext();)
                        {
                                aString = (String)e1.next();
                                roll--;
                                if (roll == 0)
                                {
                                        personality2Text.setText(aString);
                                        break;
                                }
                        }
                }
                if (phobiaBox.isSelected())
                {
                  System.out.println("phobia");
                        roll = Globals.getRandomInt();
                        if (roll < 0) roll = -roll;
                        roll = roll % globalPhobiaList.size() + 1;
                        for (e1 = globalPhobiaList.iterator(); e1.hasNext();)
                        {
                                aString = (String)e1.next();
                                roll--;
                                if (roll == 0)
                                {
                                        phobiaText.setText(aString);
                                        break;
                                }
                        }
                }
                if (locationBox.isSelected())
                {
                        roll = Globals.getRandomInt();
                        if (roll < 0) roll = -roll;
                        roll = roll % globalLocationList.size() + 1;
                        for (e1 = globalLocationList.iterator(); e1.hasNext();)
                        {
                                aString = (String)e1.next();
                                roll--;
                                if (roll == 0)
                                {
                                        locationText.setText(aString);
                                        break;
                                }
                        }
                }
                if (interestsBox.isSelected())
                {
                        roll = Globals.getRandomInt();
                        if (roll < 0) roll = -roll;
                        roll = roll % globalInterestsList.size() + 1;
                        for (e1 = globalInterestsList.iterator(); e1.hasNext();)
                        {
                                aString = (String)e1.next();
                                roll--;
                                if (roll == 0)
                                {
                                        interestsText.setText(aString);
                                        break;
                                }
                        }
                }
                if (catchPhraseBox.isSelected())
                {
                        roll = Globals.getRandomInt();
                        if (roll < 0) roll = -roll;
                        roll = roll % globalPhraseList.size() + 1;
                        for (e1 = globalPhraseList.iterator(); e1.hasNext();)
                        {
                                aString = (String)e1.next();
                                roll--;
                                if (roll == 0)
                                {
                                        catchPhraseText.setText(aString);
                                        break;
                                }
                        }
                }
                if (hairStyleBox.isSelected())
                {
                        roll = Globals.getRandomInt();
                        if (roll < 0) roll = -roll;
                        roll = roll % globalHairStyleList.size() + 1;
                        for (e1 = globalHairStyleList.iterator(); e1.hasNext();)
                        {
                                aString = (String)e1.next();
                                roll--;
                                if (roll == 0)
                                {
                                        hairStyleText.setText(aString);
                                        break;
                                }
                        }
                }
                if (speechPatternBox.isSelected())
                {
                        roll = Globals.getRandomInt();
                        if (roll < 0) roll = -roll;
                        roll = roll % globalSpeechList.size() + 1;
                        for (e1 = globalSpeechList.iterator(); e1.hasNext();)
                        {
                                aString = (String)e1.next();
                                roll--;
                                if (roll == 0)
                                {
                                        speechPatternText.setText(aString);
                                        break;
                                }
                        }
                }
                if (htBox.isSelected())
                {
                        final PlayerCharacter aPC = Globals.getCurrentPC();
                        if (aPC.getRace() == null)
                                return;
                        aPC.getRace().rollHeightWeight();
                        htText.setText(String.valueOf(aPC.getHeight()));

                }
                if (wtBox.isSelected())
                {
                        final PlayerCharacter aPC = Globals.getCurrentPC();
                        if (aPC.getRace() == null)
                                return;
                        aPC.getRace().rollHeightWeight();
                        wtText.setText(String.valueOf(aPC.getWeight()));
                }
                if (ageBox.isSelected())
                {
                        final PlayerCharacter aPC = Globals.getCurrentPC();
                        if (aPC.getClassList().size() == 0 || aPC.getRace() == null)
                                return;
                        aPC.setDirty(true);
                        PCClass aClass = (PCClass)aPC.getClassList().get(0);
                        aPC.getRace().rollAgeForAgeSet(aClass.getAgeSet());
                        ageText.setText(String.valueOf(aPC.getAge()));
                }
                
                
        }
  
  
        /*  
         *  Unsure code.  I think Bryan wrote it and it seems
         *  nescessary to save the right info.  I don't think I 
         *  hooked it up correctly.
         */
        public void startListeners()
        {
                handedComboBox.addActionListener(al1);
                genderComboBox.addActionListener(al2);
                if (ageAdapter == null)
                {
                        ageAdapter = new FocusAdapter()
                                {
                                        public void focusLost(FocusEvent evt)
                                        {
                                                final PlayerCharacter aPC = Globals.getCurrentPC();
                                                aPC.setDirty(true);
                                                aPC.setAge(Delta.parseInt("0" + ageText.getText()));
                                        }
                                };
                }
                ageText.addFocusListener(ageAdapter);
                if (htAdapter == null)
                {
                        htAdapter = new FocusAdapter() {
                                        public void focusLost(FocusEvent evt)
                                        {
                                                final PlayerCharacter aPC = Globals.getCurrentPC();
                                                aPC.setDirty(true);
                                                aPC.setHeight(Delta.parseInt("0" + htText.getText()));
                                        }
                                };
                }
                htText.addFocusListener(htAdapter);
                if (wtAdapter == null)
                {
                        wtAdapter = new FocusAdapter() {
                                        public void focusLost(FocusEvent evt)
                                        {
                                                final PlayerCharacter aPC = Globals.getCurrentPC();
                                                aPC.setDirty(true);
                                                aPC.setWeight(Delta.parseInt("0" + wtText.getText()));
                                        }
                                };
                }
                wtText.addFocusListener(wtAdapter);
                if (playerNameAdapter == null)
                {
                        playerNameAdapter = new FocusAdapter()
                                {
                                        public void focusLost(FocusEvent evt)
                                        {
                                                Globals.getCurrentPC().setPlayersName(playerNameText.getText());
                                        }
                                };
                }
                playerNameText.addFocusListener(playerNameAdapter);
        }
        
        
        public void stopListeners()
        {
                handedComboBox.removeActionListener(al1);
                genderComboBox.removeActionListener(al2);
                ageText.removeFocusListener(ageAdapter);
                htText.removeFocusListener(htAdapter);
                wtText.removeFocusListener(wtAdapter);
                playerNameText.removeFocusListener(playerNameAdapter);
        }
  
        public void refreshDisplay()
        {
                stopListeners();
                final PlayerCharacter aPC = Globals.getCurrentPC();
                final Race pcRace = aPC.getRace();
                if (!pcRace.equals(Globals.s_EMPTYRACE))
                {
                        handedComboBox.setSelectedItem(aPC.getHanded());
                        genderComboBox.setSelectedItem(aPC.getGender());
                        ageText.setText(String.valueOf(aPC.getAge()));
                        wtText.setText(String.valueOf(aPC.getWeight()));
                        htText.setText(String.valueOf(aPC.getHeight()));
                }
                playerNameText.setText(aPC.getPlayersName());
                startListeners();
        }
        //end unsure code
}
