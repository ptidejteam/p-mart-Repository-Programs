/*
 * CharacterInfo.java
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

// This snippet creates a new tabbed panel

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import pcgen.core.Globals;
import pcgen.core.Names;
import pcgen.core.PlayerCharacter;

/**
 * <code>CharacterInfo</code> creates a new tabbed panel.
 *
 * @author Bryan McRoberts <merton_monk@users.sourceforge.net>
 * @version $Revision: 1.1 $
 */
public class CharacterInfo extends JPanel
{
	private JTabbedPane characterInfoTabbedPane = new JTabbedPane();
	private InfoStats infoStats = new InfoStats();
	private InfoClasses infoClasses = new InfoClasses();
	private InfoTemplates infoTemplates = new InfoTemplates();
	private InfoAbilities infoAbilities = new InfoAbilities();
	private InfoSkills infoSkills = new InfoSkills();
	private InfoFeats infoFeats = new InfoFeats();
	private InfoDomain infoDomains = new InfoDomain();
	private InfoSpells infoSpells = new InfoSpells();
	private InfoProfile infoProfile = new InfoProfile();
	//private InfoTraits infoTraits = new InfoTraits();
	private Description description = new Description();

	//  JPanel infoItems = new InfoItems();
//          JPanel infoBuying = new InfoBuying();
//          private JPanel infoEquip = new InfoEquipment();
	private InfoMisc infoMisc = new InfoMisc();
	private InfoEquipment infoEquip = new InfoEquipment();
	private JPanel infoCompanions = new InfoCompanions();
	private BorderLayout borderLayout1 = new BorderLayout();
	private Names names = new Names();
	private NameGui nameFrame = null;
	private int previousIndex = 0;

	/**
         * Needs to be non-static to address bug #495558
         * (multiple character sheets lose name field).
         *
         * removed txtName since zebulon's description tab is done.
         * code remains as comments, might be we'll need it yet again ;-)
         * 
         * author: Thomas Behr 03-01-02
         */ 
//          public JTextField txtName = new JTextField();
//          private JLabel lblName = new JLabel();
//          private JButton randName = new JButton();
//          private JPanel panelNorth = new JPanel();

	public CharacterInfo()
	{
		try
		{
			jbInit();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	private void jbInit() throws Exception
	{
		this.setLayout(borderLayout1);
		characterInfoTabbedPane.setPreferredSize(new Dimension(550, 350));
		this.setMinimumSize(new Dimension(550, 350));
		this.setPreferredSize(new Dimension(550, 350));
		this.add(characterInfoTabbedPane, BorderLayout.CENTER);

		PToolBar toolbar = new PToolBar();
		this.add(toolbar, BorderLayout.NORTH);

//  		this.add(panelNorth, BorderLayout.NORTH);
//  		panelNorth.add(toolbar, null);
//  		panelNorth.add(lblName, null);
//  		panelNorth.add(txtName, null);
//  		panelNorth.add(randName, null);
//  		lblName.setText("Name:");
//  		txtName.setMinimumSize(new Dimension(300, 21));
//  		txtName.setPreferredSize(new Dimension(300, 21));
//  		txtName.setBackground(Color.white);
//  		final PlayerCharacter aPC = Globals.getCurrentPC();
//  		if (aPC != null) txtName.setText(aPC.getName());
//  		randName.setText("Roll Name");
//  		randName.setMnemonic('N');
//  		randName.setPreferredSize(new Dimension(75, 25));
//  		randName.setMaximumSize(new Dimension(100, 25));
//  		randName.setMargin(new Insets(2, 5, 2, 5));
//  		randName.setMinimumSize(new Dimension(50, 25));
//  		randName.addActionListener(new java.awt.event.ActionListener()
//  		{
//  			public void actionPerformed(ActionEvent evt)
//  			{
//  				if (nameFrame == null)
//  					nameFrame = new NameGui();
//  				nameFrame.setVisible(true);
//  			}
//  		});

		int x = 0;

		if (Globals.getChaTabPlacement() == 0)
			characterInfoTabbedPane.setTabPlacement(JTabbedPane.TOP);
		else if (Globals.getChaTabPlacement() == 1)
			characterInfoTabbedPane.setTabPlacement(JTabbedPane.LEFT);
		else if (Globals.getChaTabPlacement() == 2)
			characterInfoTabbedPane.setTabPlacement(JTabbedPane.BOTTOM);
		else if (Globals.getChaTabPlacement() == 3)
			characterInfoTabbedPane.setTabPlacement(JTabbedPane.RIGHT);

		characterInfoTabbedPane.add(infoStats, "Stats", x++);
		characterInfoTabbedPane.add(infoClasses, "Classes", x++);
//		if (Globals.getTemplateList().size() != 0)
		characterInfoTabbedPane.add(infoTemplates, "Templates", x++);
		characterInfoTabbedPane.add(infoAbilities, "Abilities", x++);
		characterInfoTabbedPane.add(infoSkills, "Skills", x++);
		characterInfoTabbedPane.add(infoFeats, "Feats", x++);
		if (Globals.isDndMode())
		{
			characterInfoTabbedPane.add(infoDomains, "Domains", x++); // should be 5
			characterInfoTabbedPane.add(infoSpells, "Spells", x++); // should be 6
		}
		characterInfoTabbedPane.add(infoEquip, "Equipment", x++);
		characterInfoTabbedPane.add(infoCompanions, "Companions", x++);
		//    characterInfoTabbedPane.add(infoItems, "Items",x++);
		//    characterInfoTabbedPane.add(infoBuying, "Buying",x++);
		//characterInfoTabbedPane.add(infoProfile, "Profile", x++);
		//characterInfoTabbedPane.add(infoTraits, "Traits", x++);
		characterInfoTabbedPane.add(description, "Description", x++);
		characterInfoTabbedPane.add(infoMisc, "Misc.", x++);
		if (Globals.isPreviewTabShown())
		{
			InfoPreview infoPreview = new InfoPreview();
			characterInfoTabbedPane.add(infoPreview, "Preview", x++);
		}
		characterInfoTabbedPane.addChangeListener(new javax.swing.event.ChangeListener()
		{
			public void stateChanged(javax.swing.event.ChangeEvent evt)
			{
				final int selectedIndex = characterInfoTabbedPane.getSelectedIndex();
				if (previousIndex == 0 && selectedIndex > 0)
				{
					infoStats.showAlignment();
					if (infoStats.allowLeaving())
					{
						previousIndex = selectedIndex;
					}
					else
					{
						Runnable doWorkRunnable = new Runnable()
						{
							public void run()
							{
								characterInfoTabbedPane.setSelectedIndex(0);
							}
						};
						javax.swing.SwingUtilities.invokeLater(doWorkRunnable);
					}
				}
				else
				{
					previousIndex = selectedIndex;
				}
			}
		});


//  		txtName.addActionListener(
//  			new java.awt.event.ActionListener()
//  			{
//  				/**
//  				 *  Anonymous event handler
//  				 *
//  				 * @param  e  The ActionEvent
//  				 * @since
//  				 */
//  				public void actionPerformed(ActionEvent e)
//  				{
//  					txtName_Changed(e);
//  					lblName.requestFocus();
//  				}
//  			});

//  		txtName.addFocusListener(new java.awt.event.FocusAdapter()
//  		{
//  			public void focusLost(java.awt.event.FocusEvent evt)
//  			{
//  				txtName_Changed(evt);
//  			}
//  		});

	}

	public void setTxtName(String aString)
	{
//  		txtName.setText(aString);
		description.txtName.setText(aString);
	}

	public InfoSpells infoSpells()
	{
		return infoSpells;
	}

	public InfoDomain infoDomains()
	{
		return infoDomains;
	}
	
//  	/**
//  	 *  This method takes the name entered in the txtName field and makes it the
//  	 *  name of the active tab.
//  	 *
//  	 * @param  e  The ActionEvent
//  	 */
//  	void txtName_Changed(java.awt.AWTEvent e)
//  	{
//  		final PlayerCharacter aPC = Globals.getCurrentPC();
//  		if (aPC != null)
//  		{
//  			aPC.setName(txtName.getText());
//  			PCGen_Frame1.setTabName(txtName.getText());
//  		}
//  	}

	public void eqList_Changed()
	{
		infoEquip.changeEquipmentListVoid();
		infoEquip.fillEquipTypeCombo();
	}
}
