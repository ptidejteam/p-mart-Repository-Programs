/*
 * InfoTraits.java
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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;
import java.util.Random;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JViewport;
import pcgen.core.Globals;
import pcgen.core.PlayerCharacter;

/**
 * <code>InfoTraits</code> creates a new tabbed panel.
 *
 * @author Bryan McRoberts <merton_monk@users.sourceforge.net>
 * @version $Revision: 1.1 $
 */
class InfoTraits extends JPanel
{
  JPanel northPanel = new JPanel();
  JPanel centerPanel = new JPanel();
  JTextField skinText = new JTextField(Globals.currentPC.skinColor());
  JTextField hairColorText = new JTextField(Globals.currentPC.hairColor());
  JTextField hairLengthText = new JTextField(Globals.currentPC.hairLength());
  JTextField eyeColorText = new JTextField(Globals.currentPC.eyeColor());
  JTextField speechPatternText = new JTextField(Globals.currentPC.speechTendency());
  JTextField phobiaText = new JTextField(Globals.currentPC.phobias());
  JTextField interestsText = new JTextField(Globals.currentPC.interests());
  JTextField catchPhraseText = new JTextField(Globals.currentPC.catchPhrase());
  JTextField personality1Text = new JTextField(Globals.currentPC.trait1());
  JTextField personality2Text = new JTextField(Globals.currentPC.trait2());
  JTextField residenceText = new JTextField(Globals.currentPC.residence());
  JTextField locationText = new JTextField(Globals.currentPC.location());
  JTextArea bioText = new JTextArea(Globals.currentPC.bio());
  JTextArea descriptionText = new JTextArea(Globals.currentPC.description());

  void buildConstraints(GridBagConstraints gbc, int gx, int gy, int gw, int gh, int wx, int wy)
  {
    gbc.gridx = gx;
    gbc.gridy = gy;
    gbc.gridwidth = gw;
    gbc.gridheight = gh;
    gbc.weightx = wx;
    gbc.weighty = wy;
  }

  public InfoTraits()
  {
    initComponents();
  }

  private void initComponents()
  {
    GridBagLayout gridbag = new GridBagLayout();
    GridBagConstraints c = new GridBagConstraints();
    northPanel.setLayout(gridbag);
    buildConstraints(c, 0, 0, 1, 1, 5, 10);
    c.fill = GridBagConstraints.NONE;
    c.anchor = GridBagConstraints.EAST;
    JLabel label = new JLabel("Skin: ");
    gridbag.setConstraints(label, c);
    northPanel.add(label);
    buildConstraints(c, 1, 0, 1, 1, 45, 0);
    c.fill = GridBagConstraints.HORIZONTAL;
    c.anchor = GridBagConstraints.WEST;
    gridbag.setConstraints(skinText, c);
    northPanel.add(skinText);
    buildConstraints(c, 2, 0, 1, 1, 5, 0);
    c.fill = GridBagConstraints.NONE;
    c.anchor = GridBagConstraints.EAST;
    label = new JLabel("Eye Color: ");
    gridbag.setConstraints(label, c);
    northPanel.add(label);
    buildConstraints(c, 3, 0, 1, 1, 45, 0);
    c.fill = GridBagConstraints.HORIZONTAL;
    c.anchor = GridBagConstraints.WEST;
    gridbag.setConstraints(eyeColorText, c);
    northPanel.add(eyeColorText);
    buildConstraints(c, 0, 1, 1, 1, 0, 10);
    c.fill = GridBagConstraints.NONE;
    c.anchor = GridBagConstraints.EAST;
    label = new JLabel("Hair Color: ");
    gridbag.setConstraints(label, c);
    northPanel.add(label);
    buildConstraints(c, 1, 1, 1, 1, 0, 0);
    c.fill = GridBagConstraints.HORIZONTAL;
    c.anchor = GridBagConstraints.WEST;
    gridbag.setConstraints(hairColorText, c);
    northPanel.add(hairColorText);
    buildConstraints(c, 2, 1, 1, 1, 0, 0);
    c.fill = GridBagConstraints.NONE;
    c.anchor = GridBagConstraints.EAST;
    label = new JLabel("Hair Length: ");
    gridbag.setConstraints(label, c);
    northPanel.add(label);
    buildConstraints(c, 3, 1, 1, 1, 0, 0);
    c.fill = GridBagConstraints.HORIZONTAL;
    c.anchor = GridBagConstraints.WEST;
    gridbag.setConstraints(hairLengthText, c);
    northPanel.add(hairLengthText);
    buildConstraints(c, 0, 2, 1, 1, 0, 10);
    c.fill = GridBagConstraints.NONE;
    c.anchor = GridBagConstraints.EAST;
    label = new JLabel("Speech Pattern: ");
    gridbag.setConstraints(label, c);
    northPanel.add(label);
    buildConstraints(c, 1, 2, 1, 1, 0, 0);
    c.fill = GridBagConstraints.HORIZONTAL;
    c.anchor = GridBagConstraints.WEST;
    gridbag.setConstraints(speechPatternText, c);
    northPanel.add(speechPatternText);
    buildConstraints(c, 2, 2, 1, 1, 0, 0);
    c.fill = GridBagConstraints.NONE;
    c.anchor = GridBagConstraints.EAST;
    label = new JLabel("Phobias: ");
    gridbag.setConstraints(label, c);
    northPanel.add(label);
    buildConstraints(c, 3, 2, 1, 1, 0, 0);
    c.fill = GridBagConstraints.HORIZONTAL;
    c.anchor = GridBagConstraints.WEST;
    gridbag.setConstraints(phobiaText, c);
    northPanel.add(phobiaText);
    buildConstraints(c, 0, 3, 1, 1, 0, 10);
    c.fill = GridBagConstraints.NONE;
    c.anchor = GridBagConstraints.EAST;
    label = new JLabel("Interests: ");
    gridbag.setConstraints(label, c);
    northPanel.add(label);
    buildConstraints(c, 1, 3, 1, 1, 0, 0);
    c.fill = GridBagConstraints.HORIZONTAL;
    c.anchor = GridBagConstraints.WEST;
    gridbag.setConstraints(interestsText, c);
    northPanel.add(interestsText);
    buildConstraints(c, 2, 3, 1, 1, 0, 0);
    c.fill = GridBagConstraints.NONE;
    c.anchor = GridBagConstraints.EAST;
    label = new JLabel("Catch Phrase: ");
    gridbag.setConstraints(label, c);
    northPanel.add(label);
    buildConstraints(c, 3, 3, 1, 1, 0, 0);
    c.fill = GridBagConstraints.HORIZONTAL;
    c.anchor = GridBagConstraints.WEST;
    gridbag.setConstraints(catchPhraseText, c);
    northPanel.add(catchPhraseText);
    buildConstraints(c, 0, 4, 1, 1, 0, 10);
    c.fill = GridBagConstraints.NONE;
    c.anchor = GridBagConstraints.EAST;
    label = new JLabel("Personality Trait: ");
    gridbag.setConstraints(label, c);
    northPanel.add(label);
    buildConstraints(c, 1, 4, 1, 1, 0, 0);
    c.fill = GridBagConstraints.HORIZONTAL;
    c.anchor = GridBagConstraints.WEST;
    gridbag.setConstraints(personality1Text, c);
    northPanel.add(personality1Text);
    buildConstraints(c, 2, 4, 1, 1, 0, 0);
    c.fill = GridBagConstraints.NONE;
    c.anchor = GridBagConstraints.EAST;
    label = new JLabel("Personality Trait: ");
    gridbag.setConstraints(label, c);
    northPanel.add(label);
    buildConstraints(c, 3, 4, 1, 1, 0, 0);
    c.fill = GridBagConstraints.HORIZONTAL;
    c.anchor = GridBagConstraints.WEST;
    gridbag.setConstraints(personality2Text, c);
    northPanel.add(personality2Text);
    buildConstraints(c, 0, 5, 1, 1, 0, 10);
    c.fill = GridBagConstraints.NONE;
    c.anchor = GridBagConstraints.EAST;
    label = new JLabel("Residence: ");
    gridbag.setConstraints(label, c);
    northPanel.add(label);
    buildConstraints(c, 1, 5, 1, 1, 0, 0);
    c.fill = GridBagConstraints.HORIZONTAL;
    c.anchor = GridBagConstraints.WEST;
    gridbag.setConstraints(residenceText, c);
    northPanel.add(residenceText);
    buildConstraints(c, 2, 5, 1, 1, 0, 0);
    c.fill = GridBagConstraints.NONE;
    c.anchor = GridBagConstraints.EAST;
    label = new JLabel("Location: ");
    gridbag.setConstraints(label, c);
    northPanel.add(label);
    buildConstraints(c, 3, 5, 1, 1, 0, 0);
    c.fill = GridBagConstraints.HORIZONTAL;
    c.anchor = GridBagConstraints.WEST;
    gridbag.setConstraints(locationText, c);
    northPanel.add(locationText);
    JButton aButton = new JButton("Random");
    aButton.addActionListener(
      new ActionListener()
      {
        /**
         *  Anonymous event handler
         *
         * @param  evt  The ActionEvent
         * @since
         */
        public void actionPerformed(ActionEvent evt)
        {
          randomTraits_click();
          updateTextFields();
          Globals.currentPC.setDirty(true);
        }
      });
    buildConstraints(c, 0, 6, 2, 1, 0, 0);
    c.fill = GridBagConstraints.NONE;
    c.anchor = GridBagConstraints.CENTER;
    gridbag.setConstraints(aButton, c);
    northPanel.add(aButton);
    aButton = new JButton("Clear");
    aButton.addActionListener(
      new ActionListener()
      {
        /**
         *  Anonymous event handler
         *
         * @param  evt  The ActionEvent
         * @since
         */
        public void actionPerformed(ActionEvent evt)
        {
          clearTraits_click();
          updateTextFields();
          Globals.currentPC.setDirty(true);
        }
      });
    buildConstraints(c, 2, 6, 2, 1, 0, 0);
    c.fill = GridBagConstraints.NONE;
    c.anchor = GridBagConstraints.CENTER;
    gridbag.setConstraints(aButton, c);
    northPanel.add(aButton);
    buildConstraints(c, 0, 7, 2, 1, 0, 0);
    c.fill = GridBagConstraints.NONE;
    c.anchor = GridBagConstraints.CENTER;
    label = new JLabel("Bio: ");
    gridbag.setConstraints(label, c);
    northPanel.add(label);
    buildConstraints(c, 2, 7, 2, 1, 0, 0);
    c.fill = GridBagConstraints.NONE;
    c.anchor = GridBagConstraints.CENTER;
    label = new JLabel("Description: ");
    gridbag.setConstraints(label, c);
    northPanel.add(label);
    skinText.setMinimumSize(new Dimension(50, 21));
    skinText.setPreferredSize(new Dimension(200, 21));
    eyeColorText.setMinimumSize(new Dimension(50, 21));
    eyeColorText.setPreferredSize(new Dimension(200, 21));
    hairLengthText.setMinimumSize(new Dimension(50, 21));
    hairLengthText.setPreferredSize(new Dimension(200, 21));
    speechPatternText.setMinimumSize(new Dimension(50, 21));
    speechPatternText.setPreferredSize(new Dimension(200, 21));
    phobiaText.setMinimumSize(new Dimension(50, 21));
    phobiaText.setPreferredSize(new Dimension(200, 21));
    interestsText.setMinimumSize(new Dimension(50, 21));
    interestsText.setPreferredSize(new Dimension(200, 21));
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
    centerPanel.setLayout(new GridLayout(1, 2));
    centerPanel.add(scroll);
    centerPanel.add(scroll2);
    this.setLayout(new BorderLayout());
    this.add(northPanel, BorderLayout.NORTH);
    this.add(centerPanel, BorderLayout.CENTER);
    java.awt.event.FocusListener fl = new java.awt.event.FocusAdapter()
    {
      public void focusLost(java.awt.event.FocusEvent evt)
      {
        updateTextFields();
      }
    };
    skinText.addFocusListener(fl);
    hairColorText.addFocusListener(fl);
    hairLengthText.addFocusListener(fl);
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

    addComponentListener(new java.awt.event.ComponentAdapter()
    {
      public void componentShown(java.awt.event.ComponentEvent evt)
      {
        requestDefaultFocus();
      }
    });
  }

  private final void updateTextFields()
  {
    PlayerCharacter aPC = Globals.currentPC;
    aPC.setSkinColor(skinText.getText());
    aPC.setHairColor(hairColorText.getText());
    aPC.setHairLength(hairLengthText.getText());
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
  }

  /* This function is called when the "Random" button is clicked and
   * randomly generates traits/colors for certain fields. */
  private void randomTraits_click()
  {
    int i = 0;
    Random roller = new Random();
    int roll = 0;
    int colorNum = Globals.colorList.size();
    int traitNum = Globals.traitList.size();
    Iterator e1 = null;
    String aString = null;
    if (eyeColorText.getText().equals(""))
    {
      roll = roller.nextInt();
      if (roll < 0) roll = -roll;
      roll = roll % Globals.colorList.size() + 1;
      for (e1 = Globals.colorList.iterator(); e1.hasNext();)
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
    if (hairColorText.getText().equals(""))
    {
      roll = roller.nextInt();
      if (roll < 0) roll = -roll;
      roll = roll % Globals.colorList.size() + 1;
      for (e1 = Globals.colorList.iterator(); e1.hasNext();)
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
    if (skinText.getText().equals(""))
    {
      roll = roller.nextInt();
      if (roll < 0) roll = -roll;
      roll = roll % Globals.colorList.size() + 1;
      for (e1 = Globals.colorList.iterator(); e1.hasNext();)
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
    if (personality1Text.getText().equals(""))
    {
      roll = roller.nextInt();
      if (roll < 0) roll = -roll;
      roll = roll % Globals.traitList.size() + 1;
      for (e1 = Globals.traitList.iterator(); e1.hasNext();)
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
    if (personality2Text.getText().equals(""))
    {
      roll = roller.nextInt();
      if (roll < 0) roll = -roll;
      roll = roll % Globals.traitList.size() + 1;
      for (e1 = Globals.traitList.iterator(); e1.hasNext();)
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
  }

  /* This function clears out all the randomly generatable trait fields */
  private void clearTraits_click()
  {
    skinText.setText("");
    eyeColorText.setText("");
    hairColorText.setText("");
    hairLengthText.setText("");
    speechPatternText.setText("");
    phobiaText.setText("");
    interestsText.setText("");
    catchPhraseText.setText("");
    personality1Text.setText("");
    personality2Text.setText("");
    residenceText.setText("");
    locationText.setText("");
  }
}
