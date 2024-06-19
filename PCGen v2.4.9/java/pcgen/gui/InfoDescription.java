/*
 * InfoDescription.java
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

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import pcgen.core.Globals;
import pcgen.core.PCClass;
import pcgen.core.PlayerCharacter;
import pcgen.core.Race;
import pcgen.util.Delta;

/**
 * <code>InfoDescription</code> creates a new tabbed panel.
 *
 * @author Mario Bonassin <zebuleon@users.sourceforge.net>
 * @version $Revision: 1.1 $
 */
class InfoDescription extends JPanel
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
	//JCheckBox wtBox = new JCheckBox();
	//JCheckBox htBox = new JCheckBox();
	JCheckBox htwtBox = new JCheckBox();
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

	private JButton randName;
	private JButton randAll;
	private JButton checkAll;
	private JButton uncheckAll;


	private NameGui nameFrame = null;
	Border etched;
	TitledBorder titled;

	private static String in_nameLabel;
	private static String in_randomButton;
	private static String in_checkButton;
	private static String in_uncheckButton;
	private static String in_playerString;
	private static String in_genderString;
	private static String in_genderMale;
	private static String in_genderFemale;
	private static String in_genderNeuter;
	private static String in_comboNone;
	private static String in_comboOther;
	private static String in_handString;
	private static String in_handRight;
	private static String in_handLeft;
	private static String in_handBoth;
	private static String in_heightString;
	private static String in_weightString;
	private static String in_ageString;
	private static String in_skinString;
	private static String in_phobiasString;
	private static String in_personalityString;
	private static String in_eyeString;
	private static String in_interestString;
	private static String in_hairString;
	private static String in_regionString;
	private static String in_speechString;
	private static String in_styleString;
	private static String in_locationString;
	private static String in_phraseString;
	private static String in_bioInfoString;
	private static String in_descripInfoString;
	private static String in_randCheckTipString;
	private static String in_randNameTipString;
	private static String in_randTraitTipString;
	private static String in_checkTipString;
	private static String in_uncheckTipString;

	/**
	 * Resrouce bundles
	 */
	static
	{
		ResourceBundle descriptionTabProperties;
		Locale currentLocale = new Locale(Globals.getLanguage(), Globals.getCountry());
		try
		{
			descriptionTabProperties = ResourceBundle.getBundle("pcgen/gui/properities/LanguageBundle", currentLocale);
			in_nameLabel = descriptionTabProperties.getString("in_nameLabel");
			in_randomButton = descriptionTabProperties.getString("in_randomButton");
			in_checkButton = descriptionTabProperties.getString("in_checkButton");
			in_uncheckButton = descriptionTabProperties.getString("in_uncheckButton");
			in_playerString = descriptionTabProperties.getString("in_playerString");
			in_genderString = descriptionTabProperties.getString("in_genderString");
			in_genderMale = descriptionTabProperties.getString("in_genderMale");
			in_genderFemale = descriptionTabProperties.getString("in_genderFemale");
			in_genderNeuter = descriptionTabProperties.getString("in_genderNeuter");
			in_comboNone = descriptionTabProperties.getString("in_comboNone");
			in_comboOther = descriptionTabProperties.getString("in_comboOther");
			in_handString = descriptionTabProperties.getString("in_handString");
			in_handRight = descriptionTabProperties.getString("in_handRight");
			in_handLeft = descriptionTabProperties.getString("in_handLeft");
			in_handBoth = descriptionTabProperties.getString("in_handBoth");
			in_heightString = descriptionTabProperties.getString("in_heightString");
			in_weightString = descriptionTabProperties.getString("in_weightString");
			in_ageString = descriptionTabProperties.getString("in_ageString");
			in_skinString = descriptionTabProperties.getString("in_skinString");
			in_phobiasString = descriptionTabProperties.getString("in_phobiasString");
			in_personalityString = descriptionTabProperties.getString("in_personalityString");
			in_eyeString = descriptionTabProperties.getString("in_eyeString");
			in_interestString = descriptionTabProperties.getString("in_interestString");
			in_hairString = descriptionTabProperties.getString("in_hairString");
			in_regionString = descriptionTabProperties.getString("in_regionString");
			in_speechString = descriptionTabProperties.getString("in_speechString");
			in_styleString = descriptionTabProperties.getString("in_styleString");
			in_locationString = descriptionTabProperties.getString("in_locationString");
			in_phraseString = descriptionTabProperties.getString("in_phraseString");
			in_bioInfoString = descriptionTabProperties.getString("in_bioInfoString");
			in_descripInfoString = descriptionTabProperties.getString("in_descripInfoString");
			in_randCheckTipString = descriptionTabProperties.getString("in_randCheckTipString");
			in_randNameTipString = descriptionTabProperties.getString("in_randNameTipString");
			in_randTraitTipString = descriptionTabProperties.getString("in_randTraitTipString");
			in_checkTipString = descriptionTabProperties.getString("in_checkTipString");
			in_uncheckTipString = descriptionTabProperties.getString("in_uncheckTipString");
		}
		catch (MissingResourceException mrex)
		{
			mrex.printStackTrace();
		}
		finally
		{
			descriptionTabProperties = null;
		}
	}

	//original code for comboboxes
	ActionListener al1 = new ActionListener()
	{
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

	ActionListener al2 = new ActionListener()
	{
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

	public InfoDescription()
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
		final JLabel labelName = new JLabel(in_nameLabel + ": ");
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
		randName = new JButton(in_randomButton);
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
		JLabel label = new JLabel(in_playerString + ": ");
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
		label = new JLabel(in_genderString + ": ");
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

		genderComboBox.addItem(in_genderMale);
		genderComboBox.addItem(in_genderFemale);
		genderComboBox.addItem(in_genderNeuter);
		genderComboBox.addItem(in_comboNone);
		genderComboBox.addItem(in_comboOther);

		buildConstraints(c, 6, 1, 1, 1, 5, 10);
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.EAST;
		label = new JLabel(in_handString + ": ");
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

		handedComboBox.addItem(in_handRight);
		handedComboBox.addItem(in_handLeft);
		handedComboBox.addItem(in_handBoth);
		handedComboBox.addItem(in_comboNone);
		handedComboBox.addItem(in_comboOther);

		buildConstraints(c, 0, 2, 1, 1, 5, 10);
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.EAST;
		label = new JLabel(in_heightString + ": ");
		gridbag.setConstraints(label, c);
		northPanel.add(label);

		buildConstraints(c, 1, 2, 1, 1, 5, 10);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.WEST;
		gridbag.setConstraints(htText, c);
		northPanel.add(htText);

		buildConstraints(c, 2, 2, 1, 2, 5, 10);
		c.fill = GridBagConstraints.CENTER;
		c.anchor = GridBagConstraints.WEST;
		//gridbag.setConstraints(htBox, c);
		//northPanel.add(htBox);
		gridbag.setConstraints(htwtBox, c);
		northPanel.add(htwtBox);

		buildConstraints(c, 0, 3, 1, 1, 5, 10);
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.EAST;
		label = new JLabel(in_weightString + ": ");
		gridbag.setConstraints(label, c);
		northPanel.add(label);

		buildConstraints(c, 1, 3, 1, 1, 5, 10);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.WEST;
		gridbag.setConstraints(wtText, c);
		northPanel.add(wtText);

		//buildConstraints(c, 2, 3, 1, 1, 5, 10);
		//c.fill = GridBagConstraints.NONE;
		//c.anchor = GridBagConstraints.WEST;
		//gridbag.setConstraints(wtBox, c);
		//northPanel.add(wtBox);

		buildConstraints(c, 0, 4, 1, 1, 5, 10);
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.EAST;
		label = new JLabel(in_ageString + ": ");
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
		label = new JLabel(in_skinString + ": ");
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
		label = new JLabel(in_phobiasString + ": ");
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
		label = new JLabel(in_personalityString + ": ");
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
		label = new JLabel(in_eyeString + ": ");
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
		label = new JLabel(in_interestString + ": ");
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
		label = new JLabel(in_personalityString + ": ");
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
		label = new JLabel(in_hairString + ": ");
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
		label = new JLabel(in_regionString + ": ");
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
		label = new JLabel(in_speechString + ": ");
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
		label = new JLabel(in_styleString + ": ");
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
		label = new JLabel(in_locationString + ": ");
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
		label = new JLabel(in_phraseString + ": ");
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

		checkAll = new JButton(in_checkButton);
		pane.add(checkAll);
		checkAll.addActionListener(new ActionListener()
		{
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

		randAll = new JButton(in_randomButton);
		pane.add(randAll);
		randAll.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				randAll_click();
				updateTextFields();
				Globals.getCurrentPC().setDirty(true);
			}
		});

		uncheckAll = new JButton(in_uncheckButton);
		pane.add(uncheckAll);
		uncheckAll.addActionListener(new ActionListener()
		{
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
		TitledBorder title1 = BorderFactory.createTitledBorder(etched, in_bioInfoString);
		title1.setTitleJustification(TitledBorder.CENTER);
		pane1.setBorder(title1);
		pane1.setLayout(new BoxLayout(pane1, BoxLayout.Y_AXIS));

		TitledBorder title2 = BorderFactory.createTitledBorder(etched, in_descripInfoString);
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
		java.awt.event.FocusListener fl = new java.awt.event.FocusAdapter()
		{
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

		ageBox.setToolTipText(in_randCheckTipString);
		//wtBox.setToolTipText(in_randCheckTipString);
		//htBox.setToolTipText(in_randCheckTipString);
		htwtBox.setToolTipText(in_randCheckTipString);
		skinBox.setToolTipText(in_randCheckTipString);
		hairColorBox.setToolTipText(in_randCheckTipString);
		hairStyleBox.setToolTipText(in_randCheckTipString);
		eyeColorBox.setToolTipText(in_randCheckTipString);
		speechPatternBox.setToolTipText(in_randCheckTipString);
		phobiaBox.setToolTipText(in_randCheckTipString);
		interestsBox.setToolTipText(in_randCheckTipString);
		catchPhraseBox.setToolTipText(in_randCheckTipString);
		personality1Box.setToolTipText(in_randCheckTipString);
		personality2Box.setToolTipText(in_randCheckTipString);
		residenceBox.setToolTipText(in_randCheckTipString);
		locationBox.setToolTipText(in_randCheckTipString);
		randName.setToolTipText(in_randNameTipString);
		randAll.setToolTipText(in_randTraitTipString);
		checkAll.setToolTipText(in_checkTipString);
		uncheckAll.setToolTipText(in_uncheckTipString);


		//Unsure Name code
		txtName.addActionListener(new java.awt.event.ActionListener()
		{
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

		txtName.addFocusListener(new java.awt.event.FocusAdapter()
		{
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
			PCGen_Frame1.setTabName(aPC, txtName.getText());
		}
	}
	//end Unsure Name code

	/* This function is called when the "Check All" button is clicked and
	 */
	private void checkAll_click()
	{
		ageBox.setSelected(true);
		//wtBox.setSelected(true);
		//htBox.setSelected(true);
		htwtBox.setSelected(true);
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
		//wtBox.setSelected(false);
		//htBox.setSelected(false);
		htwtBox.setSelected(false);
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
			//System.out.println("phobia");
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
		if (htwtBox.isSelected())
		{
			final PlayerCharacter aPC = Globals.getCurrentPC();
			if (aPC.getRace() == null)
			{
				return;
			}
			aPC.getRace().rollHeightWeight();
			htText.setText(String.valueOf(aPC.getHeight()));
			wtText.setText(String.valueOf(aPC.getWeight()));
		}
//if (htBox.isSelected())
//{
		//final PlayerCharacter aPC = Globals.getCurrentPC();
		//if (aPC.getRace() == null)
		//return;
		//aPC.getRace().rollHeightWeight();
		//htText.setText(String.valueOf(aPC.getHeight()));
//}
//if (wtBox.isSelected())
//{
		//final PlayerCharacter aPC = Globals.getCurrentPC();
		//if (aPC.getRace() == null)
		//return;
		//aPC.getRace().rollHeightWeight();
		//wtText.setText(String.valueOf(aPC.getWeight()));
//}
		if (ageBox.isSelected())
		{
			final PlayerCharacter aPC = Globals.getCurrentPC();
// 0-level PC support.
			//if (aPC.getClassList().size() == 0 || aPC.getRace() == null)
			if (aPC.getRace() == null)
				return;
			aPC.setDirty(true);

			if (aPC.getClassList().size() == 0)
			{
// Without a class, everone is young
				aPC.getRace().rollAgeForAgeSet(0);

			}
			else
			{
				PCClass aClass = (PCClass)aPC.getClassList().get(0);
				aPC.getRace().rollAgeForAgeSet(aClass.getAgeSet());
			}

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
			htAdapter = new FocusAdapter()
			{
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
			wtAdapter = new FocusAdapter()
			{
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
