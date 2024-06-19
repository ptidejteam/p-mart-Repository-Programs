/*
 * InfoProfile.java
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
import pcgen.core.Globals;
import pcgen.core.PCClass;
import pcgen.core.PlayerCharacter;
import pcgen.core.Race;
import pcgen.util.Delta;

/**
 * <code>InfoProfile</code> creates a new tabbed panel.
 *
 * @author Bryan McRoberts <merton_monk@users.sourceforge.net>
 * @version $Revision: 1.1 $
 */
class InfoProfile extends JPanel
{
	private boolean d_shown = false;
	JLabel movementLabel = new JLabel("300 ");
	JLabel initLabel = new JLabel("+00 ");
	JLabel acLabel = new JLabel("00 ");
	JLabel sizeLabel = new JLabel("Medium ");
	JLabel strBonusLabel = new JLabel("+00 ");
	JLabel dexBonusLabel = new JLabel("+00 ");
	JLabel UABLabel = new JLabel("+00 ");
	JLabel BABLabel = new JLabel("+00 ");
	JLabel UDLabel = new JLabel("1d20 ");
	JLabel visionLabel = new JLabel("Normal");
	JComboBox handedComboBox = new JComboBox();
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
	JComboBox genderComboBox = new JComboBox();
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
	FocusAdapter ageAdapter = null;
	FocusAdapter htAdapter = null;
	FocusAdapter wtAdapter = null;
	FocusAdapter playerNameAdapter = null;
	WholeNumberField ageText = new WholeNumberField(5000, 0);
	WholeNumberField wtText = new WholeNumberField(5000, 0);
	WholeNumberField htText = new WholeNumberField(5000, 0);
	JButton rollHtWtButton = new JButton("Random Weight");
	JButton rollAgeButton = new JButton("Random Age");
	JTextField playerNameText = new JTextField();

	//Sets up all constraints for GridBags
	void buildConstraints(GridBagConstraints gbc, int gx, int gy, int gw, int gh, int wx, int wy)
	{
		gbc.gridx = gx;
		gbc.gridy = gy;
		gbc.gridwidth = gw;
		gbc.gridheight = gh;
		gbc.weightx = wx;
		gbc.weighty = wy;
	}

	public InfoProfile()
	{
		initComponents();
		refreshDisplay();
	}

	private void initComponents()
	{
		GridBagLayout gridbag = new GridBagLayout();
		this.setLayout(gridbag);
		GridBagConstraints c = new GridBagConstraints();
		buildConstraints(c, 0, 0, 1, 1, 16, 10);
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.EAST;
		JLabel label = new JLabel("Player's Name: ");
		gridbag.setConstraints(label, c);
		this.add(label);
		buildConstraints(c, 1, 0, 5, 1, 0, 0);
		c.fill = GridBagConstraints.HORIZONTAL;
		gridbag.setConstraints(playerNameText, c);
		this.add(playerNameText);
		buildConstraints(c, 0, 1, 1, 1, 0, 10);
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.EAST;
		label = new JLabel("Movement: ");
		gridbag.setConstraints(label, c);
		this.add(label);
		buildConstraints(c, 1, 1, 3, 1, 20, 0);
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.WEST;
		gridbag.setConstraints(movementLabel, c);
		this.add(movementLabel, c);
		buildConstraints(c, 4, 1, 1, 1, 16, 0);
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.EAST;
		label = new JLabel("Initiative Mod: ");
		gridbag.setConstraints(label, c);
		this.add(label, c);
		buildConstraints(c, 5, 1, 1, 1, 16, 0);
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.WEST;
		gridbag.setConstraints(initLabel, c);
		this.add(initLabel);
		label = new JLabel("AC Mod: ");
		buildConstraints(c, 6, 1, 1, 1, 16, 0);
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.EAST;
		gridbag.setConstraints(label, c);
		this.add(label, c);
		buildConstraints(c, 7, 1, 1, 1, 16, 0);
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.WEST;
		gridbag.setConstraints(acLabel, c);
		this.add(acLabel);
		buildConstraints(c, 0, 2, 1, 1, 0, 10);
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.EAST;
		label = new JLabel("Size: ");
		gridbag.setConstraints(label, c);
		this.add(label);
		buildConstraints(c, 1, 2, 3, 1, 0, 0);
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.WEST;
		gridbag.setConstraints(sizeLabel, c);
		this.add(sizeLabel);
		buildConstraints(c, 4, 2, 3, 1, 0, 0);
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.SOUTH;
		label = new JLabel("Str Bonus ");
		gridbag.setConstraints(label, c);
		this.add(label);
		buildConstraints(c, 7, 2, 1, 2, 0, 0);
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.WEST;
		gridbag.setConstraints(strBonusLabel, c);
		this.add(strBonusLabel);
		buildConstraints(c, 4, 3, 3, 1, 0, 0);
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.NORTH;
		label = new JLabel("to hit/damage with melee/thrown weapons: ");
		gridbag.setConstraints(label, c);
		this.add(label);
		buildConstraints(c, 0, 3, 1, 1, 0, 10);
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.EAST;
		label = new JLabel("Height: ");
		gridbag.setConstraints(label, c);
		this.add(label);
		buildConstraints(c, 1, 3, 1, 1, 10, 0);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.WEST;
		gridbag.setConstraints(htText, c);
		this.add(htText);
		buildConstraints(c, 2, 3, 1, 1, 5, 0);
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.WEST;
		label = new JLabel(" " + Globals.getHeightDisplay());
		gridbag.setConstraints(label, c);
		this.add(label);
		buildConstraints(c, 3, 3, 1, 2, 0, 0);
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.CENTER;
		gridbag.setConstraints(rollHtWtButton, c);
		this.add(rollHtWtButton);
		rollHtWtButton.setMnemonic('W');
		buildConstraints(c, 0, 4, 1, 1, 0, 10);
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.EAST;
		label = new JLabel("Weight: ");
		gridbag.setConstraints(label, c);
		this.add(label);
		buildConstraints(c, 1, 4, 1, 1, 0, 0);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.WEST;
		gridbag.setConstraints(wtText, c);
		this.add(wtText);
		buildConstraints(c, 2, 4, 1, 1, 0, 0);
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.WEST;
		label = new JLabel(" " + Globals.getWeightDisplay());
		gridbag.setConstraints(label, c);
		this.add(label);
		buildConstraints(c, 4, 4, 3, 1, 0, 0);
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.CENTER;
		label = new JLabel("Dex Bonus (to hit with ranged weapons): ");
		gridbag.setConstraints(label, c);
		this.add(label);
		buildConstraints(c, 7, 4, 1, 1, 0, 0);
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.WEST;
		gridbag.setConstraints(dexBonusLabel, c);
		this.add(dexBonusLabel);
		buildConstraints(c, 0, 5, 1, 1, 0, 10);
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.EAST;
		label = new JLabel("Age: ");
		gridbag.setConstraints(label, c);
		this.add(label);
		buildConstraints(c, 1, 5, 1, 1, 0, 0);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.WEST;
		gridbag.setConstraints(ageText, c);
		this.add(ageText);
		buildConstraints(c, 2, 5, 1, 1, 0, 0);
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.WEST;
		label = new JLabel(" yrs");
		gridbag.setConstraints(label, c);
		this.add(label);
		buildConstraints(c, 3, 5, 1, 1, 0, 0);
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.CENTER;
		gridbag.setConstraints(rollAgeButton, c);
		rollAgeButton.setMnemonic('A');
		this.add(rollAgeButton);
		buildConstraints(c, 0, 6, 1, 1, 0, 10);
		label = new JLabel("Gender: ");
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.EAST;
		gridbag.setConstraints(label, c);
		this.add(label);
		buildConstraints(c, 1, 6, 3, 1, 0, 0);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.WEST;
		gridbag.setConstraints(genderComboBox, c);
		this.add(genderComboBox);
		buildConstraints(c, 4, 5, 3, 1, 0, 0);
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.SOUTH;
		label = new JLabel("Attack Bonus");
		gridbag.setConstraints(label, c);
		this.add(label);
		buildConstraints(c, 0, 7, 1, 1, 0, 10);
		label = new JLabel("Handed: ");
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.EAST;
		gridbag.setConstraints(label, c);
		this.add(label);
		buildConstraints(c, 1, 7, 3, 1, 0, 0);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.WEST;
		gridbag.setConstraints(handedComboBox, c);
		this.add(handedComboBox);
		buildConstraints(c, 5, 6, 1, 1, 0, 0);
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.EAST;
		label = new JLabel("Armed: ");
		gridbag.setConstraints(label, c);
		this.add(label);
		buildConstraints(c, 6, 6, 1, 1, 0, 0);
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.WEST;
		gridbag.setConstraints(BABLabel, c);
		this.add(BABLabel);
		buildConstraints(c, 0, 8, 1, 1, 0, 10);
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.EAST;
		label = new JLabel("Vision: ");
		gridbag.setConstraints(label, c);
		this.add(label);
		buildConstraints(c, 1, 8, 3, 1, 0, 0);
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.WEST;
		gridbag.setConstraints(visionLabel, c);
		this.add(visionLabel);
		buildConstraints(c, 5, 7, 1, 1, 0, 0);
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.EAST;
		label = new JLabel("Unarmed: ");
		gridbag.setConstraints(label, c);
		this.add(label);
		buildConstraints(c, 6, 7, 1, 1, 0, 0);
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.WEST;
		gridbag.setConstraints(UABLabel, c);
		this.add(UABLabel);
		buildConstraints(c, 4, 8, 2, 1, 0, 10);
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.EAST;
		label = new JLabel("Unarmed Damage: ");
		gridbag.setConstraints(label, c);
		this.add(label);
		buildConstraints(c, 6, 8, 1, 1, 0, 0);
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.WEST;
		gridbag.setConstraints(UDLabel, c);
		this.add(UDLabel);
		genderComboBox.addItem("M");
		genderComboBox.addItem("F");
		handedComboBox.addItem("Right");
		handedComboBox.addItem("Left");
		handedComboBox.addItem("Ambidextrous");
		rollHtWtButton.addActionListener(
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
					final PlayerCharacter aPC = Globals.getCurrentPC();
					if (aPC.getRace() == null)
						return;
					aPC.getRace().rollHeightWeight();
					wtText.setText(String.valueOf(aPC.getWeight()));
					htText.setText(String.valueOf(aPC.getHeight()));

				}
			});
		rollAgeButton.addActionListener(
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
					final PlayerCharacter aPC = Globals.getCurrentPC();
					if (aPC.getClassList().size() == 0 || aPC.getRace() == null)
						return;
					aPC.setDirty(true);
					PCClass aClass = (PCClass)aPC.getClassList().get(0);
					aPC.getRace().rollAgeForAgeSet(aClass.getAgeSet());
					ageText.setText(String.valueOf(aPC.getAge()));
				}
			});
		this.addComponentListener(new ComponentAdapter()
		{
			public void componentShown(ComponentEvent evt)
			{
				final PlayerCharacter aPC = Globals.getCurrentPC();
				aPC.setAggregateFeatsStable(true);
				aPC.setAutomaticFeatsStable(true);
				aPC.setVirtualFeatsStable(true);

				requestDefaultFocus();
				refreshDisplay();
				d_shown = true;
			}

			public void componentHidden(ComponentEvent evt)
			{
				// called when the panel is hidden
				if (d_shown)
				{
					storeDisplay();
				}
			}
		});
	}

	public boolean isFocusTraversable()
	{
		return true;
	}

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

	public void storeDisplay()
	{
		final PlayerCharacter aPC = Globals.getCurrentPC();
		aPC.setAge(Delta.parseInt(ageText.getText()));
		aPC.setHeight(Delta.parseInt(htText.getText()));
		aPC.setWeight(Delta.parseInt(wtText.getText()));
		aPC.setPlayersName(playerNameText.getText());
	}

	public void refreshDisplay()
	{
		stopListeners();
		final PlayerCharacter aPC = Globals.getCurrentPC();
		final Race pcRace = aPC.getRace();
		if (!pcRace.equals(Globals.s_EMPTYRACE))
		{
			String movelabel = pcRace.getMovementType(0) + " " + aPC.movement(0) +
				" " + Globals.getMovementDisplay();

			final Integer[] movements = pcRace.getMovements();
			if (movements != null)
			{
				for (int x = 1; x < movements.length; x++)
				{
					movelabel += ", " + pcRace.getMovementType(x) + " " + aPC.movement(x) +
						" " + Globals.getMovementDisplay();
				}
			}
			else
			{
				movelabel = "Race does not have movement defined. It must have a MOVE tag.";
				javax.swing.JOptionPane.showMessageDialog(null, "Race does not have movement defined.", "PCGen", javax.swing.JOptionPane.ERROR_MESSAGE);
			}
			movementLabel.setText(movelabel);
			initLabel.setText(String.valueOf(aPC.initiativeMod()));
			acLabel.setText(String.valueOf(aPC.acMod()));
			sizeLabel.setText(aPC.getSize());
			strBonusLabel.setText(String.valueOf(aPC.calcStatMod(Globals.STRENGTH)));
			dexBonusLabel.setText(String.valueOf(aPC.calcStatMod(Globals.DEXTERITY)));
			UABLabel.setText(aPC.getAttackString(2));
			BABLabel.setText(aPC.getAttackString(0));
			UDLabel.setText(aPC.getUnarmedDamageString(true, true));
			visionLabel.setText(aPC.getVision());
			handedComboBox.setSelectedItem(aPC.getHanded());
			genderComboBox.setSelectedItem(aPC.getGender());
			ageText.setText(String.valueOf(aPC.getAge()));
			wtText.setText(String.valueOf(aPC.getWeight()));
			htText.setText(String.valueOf(aPC.getHeight()));
		}
		playerNameText.setText(aPC.getPlayersName());
		startListeners();
	}
}
