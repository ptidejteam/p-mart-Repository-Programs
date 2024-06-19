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

//Title:
//Version:
//Copyright:
//Author:
//Company:
//Description:

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import pcgen.core.Globals;

/**
 * <code>CharacterInfo</code> creates a new tabbed panel.
 *
 * @author Bryan McRoberts <merton_monk@users.sourceforge.net>
 * @version $Revision: 1.1 $
 */
public class CharacterInfo extends JPanel
{
	private JTabbedPane jTabbedPane = new JTabbedPane();
	private InfoStats infoStats = new InfoStats();
	private InfoClasses infoClasses = new InfoClasses();
	private InfoTemplates infoTemplates = new InfoTemplates();
	private InfoAbilities infoAbilities = new InfoAbilities();
	private InfoSkills infoSkills = new InfoSkills();
	private InfoFeats infoFeats = new InfoFeats();
	private InfoDomain infoDomains = new InfoDomain();
	private InfoSpells infoSpells = new InfoSpells();
	private InfoProfile infoProfile = new InfoProfile();
	private InfoTraits infoTraits = new InfoTraits();
//  JPanel infoItems = new InfoItems();
//  JPanel infoBuying = new InfoBuying();
	private InfoMisc infoMisc = new InfoMisc();
	private JPanel infoEquip = new InfoEquipment();
	private JPanel infoCompanions = new InfoCompanions();
	private BorderLayout borderLayout1 = new BorderLayout();
	private JPanel panelNorth = new JPanel();
	private JLabel lblName = new JLabel();
	private JTextField txtName = new JTextField();
	private int previousIndex = 0;
	private static int tabPlace = -1; // default

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

	public static void setTabPlacement(int placement)
	{
		tabPlace = placement;
	}

	private void jbInit() throws Exception
	{
		this.setLayout(borderLayout1);
		jTabbedPane.setPreferredSize(new Dimension(550, 350));
		this.setMinimumSize(new Dimension(550, 350));
		this.setPreferredSize(new Dimension(550, 350));
		this.add(jTabbedPane, BorderLayout.CENTER);
		this.add(panelNorth, BorderLayout.NORTH);
		panelNorth.add(lblName, null);
		panelNorth.add(txtName, null);
		lblName.setText("Name:");
		txtName.setMinimumSize(new Dimension(300, 21));
		txtName.setPreferredSize(new Dimension(300, 21));
		txtName.setBackground(Color.white);
		if (Globals.getCurrentPC() != null)
			txtName.setText(Globals.getCurrentPC().getName());

		int x = 0;
		//Default tabPlacement
		if (tabPlace != -1)
			jTabbedPane.setTabPlacement(tabPlace);

		jTabbedPane.add(infoStats, "Stats", x++);
		jTabbedPane.add(infoClasses, "Classes", x++);
//		if (Globals.getTemplateList().size() != 0)
		jTabbedPane.add(infoTemplates, "Templates", x++);
		jTabbedPane.add(infoAbilities, "Abilities", x++);
		jTabbedPane.add(infoSkills, "Skills", x++);
		jTabbedPane.add(infoFeats, "Feats", x++);
		if (Globals.isDndMode())
		{
			jTabbedPane.add(infoDomains, "Domains", x++); // should be 5
			jTabbedPane.add(infoSpells, "Spells", x++); // should be 6
		}
		jTabbedPane.add(infoEquip, "Equipment", x++);
		jTabbedPane.add(infoCompanions, "Companions", x++);
		//    jTabbedPane.add(infoItems, "Items",x++);
		//    jTabbedPane.add(infoBuying, "Buying",x++);
		jTabbedPane.add(infoProfile, "Profile", x++);
		jTabbedPane.add(infoTraits, "Traits", x++);
		jTabbedPane.add(infoMisc, "Misc.", x++);
		if (Globals.isPreviewTabShown())
		{
			InfoPreview infoPreview = new InfoPreview();
			jTabbedPane.add(infoPreview, "Preview", x++);
		}
		jTabbedPane.addChangeListener(new javax.swing.event.ChangeListener()
		{
			public void stateChanged(javax.swing.event.ChangeEvent evt)
			{
				final int selectedIndex = jTabbedPane.getSelectedIndex();
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
								jTabbedPane.setSelectedIndex(0);
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


		txtName.addActionListener(
			new java.awt.event.ActionListener()
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
					lblName.requestFocus();
				}
			});

		txtName.addFocusListener(new java.awt.event.FocusAdapter()
		{
			public void focusLost(java.awt.event.FocusEvent evt)
			{
				txtName_Changed(evt);
			}
		});

	}

	public InfoSpells infoSpells()
	{
		return infoSpells;
	}

	public InfoDomain infoDomains()
	{
		return infoDomains;
	}

	/**
	 *  This method takes the name entered in the txtName field and makes it the
	 *  name of the active tab.
	 *
	 * @param  e  The ActionEvent
	 */
	void txtName_Changed(java.awt.AWTEvent e)
	{
		if (Globals.getCurrentPC() != null)
		{
			Globals.getCurrentPC().setName(txtName.getText());
			PCGen_Frame1.setTabName(txtName.getText());
		}
	}

}
