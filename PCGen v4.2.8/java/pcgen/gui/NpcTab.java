/*
 * NpcTab.java
 * Copyright 2001 (C) Mario Bonassin
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
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.Map;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import pcgen.core.Globals;
import pcgen.core.PCClass;
import pcgen.core.Race;

/**
 * <code>NpcTab</code>.
 *
 * @author Mario Bonassin <zebuleon@users.sourceforge.net>
 * @version $Revision: 1.1 $
 */

final class NpcTab extends JPanel
{
	JLabel raceL = new JLabel("Race: ");
	JLabel prleL = new JLabel("Level: ");
	JLabel secleL = new JLabel("Level: ");
	JLabel trileL = new JLabel("Level: ");
	JLabel alignL = new JLabel("Alignment: ");
	JLabel primL = new JLabel("Primary: ");
	JLabel secL = new JLabel("Secondary: ");
	JLabel triL = new JLabel("Tertiary: ");

	JLabel gendL = new JLabel("Gender: ");
	JLabel blank = new JLabel(" ");
	JLabel blank2 = new JLabel(" ");
	JComboBox raceC = new JComboBox();
	JComboBox prleC = new JComboBox();
	JComboBox alignC = new JComboBox();
	JComboBox primC = new JComboBox();
	JComboBox gendC = new JComboBox();
	JComboBox secC = new JComboBox();
	JComboBox triC = new JComboBox();
	JComboBox secleC = new JComboBox();
	JComboBox trileC = new JComboBox();
	private NameGui nameFrame = null;
	JPanel north = new JPanel();
	JPanel center = new JPanel();
	JButton gen = new JButton("Generate");
	JButton clear = new JButton("Clear");
	JTextArea result = new JTextArea();
	JList resultList;
	DefaultListModel listModel = new DefaultListModel();
	private ArrayList class1List;
	private ArrayList class2List;
	private ArrayList class3List;
	String raceChoice;
	String alignChoice;
	String primChoice;
	String gendChoice;
	String secChoice;
	String triChoice;
	String prleChoice;
	String secleChoice;
	String trileChoice;
	String[] alignmentStrings;

	public NpcTab()
	{
		initComponents();
	}

	private String[] populateAlignmentStrings()
	{
		alignmentStrings = new String[Globals.getAlignmentList().size() + 1];
		alignmentStrings[0] = "Any";
		for (int i = 0; i < Globals.getAlignmentList().size(); i++)
		{
			alignmentStrings[i + 1] = Globals.getLongAlignmentAtIndex(i);
		}
		return alignmentStrings;
	}

	private void initComponents()
	{
		populateAlignmentStrings();
		prleC.addItem("Any  ");
		secleC.addItem("Any  ");
		trileC.addItem("Any  ");
		for (int i = 1; i < 21; i++)
		{
			prleC.addItem("" + i);
			secleC.addItem("" + i);
			trileC.addItem("" + i);
		}

		alignC.setModel(new DefaultComboBoxModel(alignmentStrings));

		gendC.addItem("Any");
		gendC.addItem("Male");
		gendC.addItem("Female");

		this.setLayout(new BorderLayout());
		GridBagLayout gridbag = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();

		addComponentListener(new ComponentAdapter()
		{
			public void componentShown(ComponentEvent evt)
			{
				formComponentShown(evt);
			}
		});

		north.setLayout(gridbag);
		center.setLayout(new BorderLayout());

		Utility.buildConstraints(c, 0, 0, 1, 1, 1, 1);
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.EAST;
		gridbag.setConstraints(raceL, c);
		north.add(raceL);

		Utility.buildConstraints(c, 1, 0, 1, 1, 1, 1);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.WEST;
		raceC.addItemListener(new ItemListener()
		{
			public void itemStateChanged(ItemEvent e)
			{
				raceChoice = e.getItem().toString();
			}
		});
		gridbag.setConstraints(raceC, c);
		north.add(raceC);

		Utility.buildConstraints(c, 0, 2, 1, 1, 1, 1);
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.EAST;
		gridbag.setConstraints(alignL, c);
		north.add(alignL);

		Utility.buildConstraints(c, 1, 2, 1, 1, 1, 1);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.WEST;
		gridbag.setConstraints(alignC, c);
		north.add(alignC);
		alignC.addItemListener(new ItemListener()
		{
			public void itemStateChanged(ItemEvent e)
			{
				alignChoice = e.getItem().toString();
			}
		});

		Utility.buildConstraints(c, 0, 4, 1, 1, 1, 1);
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.EAST;
		gridbag.setConstraints(gendL, c);
		north.add(gendL);

		Utility.buildConstraints(c, 1, 4, 1, 1, 1, 1);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.WEST;
		gridbag.setConstraints(gendC, c);
		north.add(gendC);
		gendC.addItemListener(new ItemListener()
		{
			public void itemStateChanged(ItemEvent e)
			{
				gendChoice = e.getItem().toString();
			}
		});

		Utility.buildConstraints(c, 0, 1, 6, 1, 1, 1);
		gridbag.setConstraints(blank, c);
		north.add(blank);

		Utility.buildConstraints(c, 2, 0, 1, 1, 1, 1);
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.EAST;
		gridbag.setConstraints(primL, c);
		north.add(primL);

		Utility.buildConstraints(c, 3, 0, 1, 1, 1, 1);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.WEST;
		gridbag.setConstraints(primC, c);
		north.add(primC);
		primC.addItemListener(new ItemListener()
		{
			public void itemStateChanged(ItemEvent e)
			{
				primChoice = e.getItem().toString();
			}
		});

		Utility.buildConstraints(c, 2, 2, 1, 1, 1, 1);
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.EAST;
		gridbag.setConstraints(secL, c);
		north.add(secL);

		Utility.buildConstraints(c, 3, 2, 1, 1, 1, 1);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.WEST;
		gridbag.setConstraints(secC, c);
		north.add(secC);
		secC.addItemListener(new ItemListener()
		{
			public void itemStateChanged(ItemEvent e)
			{
				secChoice = e.getItem().toString();
			}
		});

		Utility.buildConstraints(c, 2, 4, 1, 1, 1, 1);
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.EAST;
		gridbag.setConstraints(triL, c);
		north.add(triL);

		Utility.buildConstraints(c, 3, 4, 1, 1, 1, 1);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.WEST;
		gridbag.setConstraints(triC, c);
		north.add(triC);
		triC.addItemListener(new ItemListener()
		{
			public void itemStateChanged(ItemEvent e)
			{
				triChoice = e.getItem().toString();
			}
		});

		Utility.buildConstraints(c, 0, 3, 6, 1, 1, 1);
		gridbag.setConstraints(blank2, c);
		north.add(blank2);

		Utility.buildConstraints(c, 4, 0, 1, 1, 1, 1);
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.EAST;
		gridbag.setConstraints(prleL, c);
		north.add(prleL);

		Utility.buildConstraints(c, 5, 0, 1, 1, 1, 1);
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.WEST;
		gridbag.setConstraints(prleC, c);
		north.add(prleC);
		prleC.addItemListener(new ItemListener()
		{
			public void itemStateChanged(ItemEvent e)
			{
				prleChoice = e.getItem().toString();
			}
		});

		Utility.buildConstraints(c, 4, 2, 1, 1, 1, 1);
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.EAST;
		gridbag.setConstraints(secleL, c);
		north.add(secleL);

		Utility.buildConstraints(c, 5, 2, 1, 1, 1, 1);
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.WEST;
		gridbag.setConstraints(secleC, c);
		north.add(secleC);
		secleC.addItemListener(new ItemListener()
		{
			public void itemStateChanged(ItemEvent e)
			{
				secleChoice = e.getItem().toString();
			}
		});

		Utility.buildConstraints(c, 4, 4, 1, 1, 1, 1);
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.EAST;
		gridbag.setConstraints(trileL, c);
		north.add(trileL);

		Utility.buildConstraints(c, 5, 4, 1, 1, 1, 1);
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.WEST;
		gridbag.setConstraints(trileC, c);
		north.add(trileC);
		trileC.addItemListener(new ItemListener()
		{
			public void itemStateChanged(ItemEvent e)
			{
				trileChoice = e.getItem().toString();
			}
		});

		Utility.buildConstraints(c, 1, 5, 2, 1, 1, 1);
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.CENTER;
		gen.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				genActionPerformed(evt);
			}
		});

		gridbag.setConstraints(gen, c);
		north.add(gen);

		Utility.buildConstraints(c, 3, 5, 2, 1, 1, 1);
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.CENTER;
		gridbag.setConstraints(clear, c);
		north.add(clear);
		clear.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				clearActionPerformed(evt);
			}
		});

		center.add(result, BorderLayout.CENTER);
		this.add(north, BorderLayout.NORTH);
		this.add(center, BorderLayout.CENTER);
	}

	protected void showAlignment()
	{
		if (Globals.getGameModeAlignmentText().length() == 0)
		{
			alignL.setVisible(false);
			alignC.setVisible(false);
		}
		else
		{
			alignL.setVisible(true);
			alignC.setVisible(true);
		}
	}

	private void formComponentShown(ComponentEvent evt)
	{
		requestFocus();
		if (!Globals.displayListsHappy())
		{
			PCGen_Frame1.getStatusBar().setText("You must load a Campaign before generating an NPC.");
		}
		boolean rebuild = false;
		if (alignmentStrings.length != Globals.getAlignmentList().size() - 1)
			rebuild = true;
		else
		{
			String[] al = Globals.getAlignmentListStrings(true);
			for (int i = 0; i < Math.min(alignmentStrings.length - 1, al.length); i++)
			{
				if (!alignmentStrings[i + 1].equals(al[i]))
				{
					rebuild = true;
				}
			}
		}

		if (rebuild)
		{
			populateAlignmentStrings();
			alignC.setModel(new DefaultComboBoxModel(alignmentStrings));
		}
		showAlignment();
		fillLists();
	}

	private void fillLists()
	{
		raceC.removeAllItems();
		primC.removeAllItems();
		secC.removeAllItems();
		triC.removeAllItems();
		class1List = new ArrayList();
		class2List = new ArrayList();
		class3List = new ArrayList();
		raceC.addItem("Any");
		class2List.add("None");
		class3List.add("None");
		class1List.add("Any");
		class2List.add("Any");
		class3List.add("Any");
		/**
		 * Populate the ComboBoxs
		 */
		//Races
		for (int i = 1; i < Globals.getRaceMap().size(); i++)
		{
			Map raceMap = Globals.getRaceMap();
			ArrayList raceList = new ArrayList(raceMap.values());
			Race race = (Race) raceList.get(i);
			raceC.addItem(race);
		}
		//Classes
		for (int i = 0; i < Globals.getClassList().size(); i++)
		{
			PCClass bClass = (PCClass) Globals.getClassList().get(i);
			class1List.add(bClass.toString());
		}
		for (int i = 0; i != class1List.size(); i++)
		{
			primC.addItem(class1List.get(i));
		}

		for (int i = 0; i < Globals.getClassList().size(); i++)
		{
			PCClass bClass = (PCClass) Globals.getClassList().get(i);
			class2List.add(bClass.toString());
		}
		for (int i = 0; i != class2List.size(); i++)
		{
			secC.addItem(class2List.get(i));
		}

		for (int i = 0; i < Globals.getClassList().size(); i++)
		{
			PCClass bClass = (PCClass) Globals.getClassList().get(i);
			class3List.add(bClass.toString());
		}
		for (int i = 0; i != class3List.size(); i++)
		{
			triC.addItem(class3List.get(i));
		}

	}

	/**
	 * Generates a character based on the chices
	 */

	private void genActionPerformed(ActionEvent evt)
	{
		if (nameFrame == null)
		{
			nameFrame = new NameGui();
		}
		nameFrame.setVisible(true);
	}

	/**Race
	 if (!raceChoice.equals("Any"))
	 {
	 int index = (raceC.getSelectedIndex() -1);
	 Map raceMap = Globals.getRaceMap();
	 ArrayList raceList = new ArrayList(raceMap.values());
	 Race race = (Race)raceList.get(index);
	 Globals.getCurrentPC().setRace(race);
	 if (Globals.getCurrentPC().getRace().hitDice() != 0)
	 {
	 Globals.getCurrentPC().getRace().rollHP();
	 }
	 }
	 else
	 {

	 }
	 //ALignment
	 if (!alignChoice.equals("Any"))
	 {
	 int alignment = (alignC.getSelectedIndex() - 1);
	 Globals.getCurrentPC().setAlignment(alignment, false);
	 }
	 else
	 {

	 }
	 //Gender
	 if (!gendChoice.equals("Any"))
	 {
	 Globals.getCurrentPC().setGender(gendChoice);
	 }
	 else
	 {

	 }
	 //Primary Class
	 if (!primChoice.equals("Any") && !prleChoice.equals("Any"))
	 {
	 Globals.getCurrentPC().setDirty(true);
	 PCClass theClass = new PCClass(primChoice);
	 if ((theClass != null) && theClass.isQualified())
	 {
	 PCClass aClass = Globals.getCurrentPC().getClassNamed(theClass.getName());
	 if (aClass == null || Globals.isIgnoreLevelCap() || (!Globals.isIgnoreLevelCap() && aClass.getLevel() < aClass.getMaxLevel()))
	 Globals.getCurrentPC().incrementClassLevel(prleC.getSelectedIndex(), theClass);
	 else
	 JOptionPane.showMessageDialog(null, "Maximum level reached.", "PCGen", JOptionPane.INFORMATION_MESSAGE);

	 }
	 }
	 else
	 {

	 }
	 //Secondary Class
	 if (!secChoice.equals("None"))
	 {
	 if (!secChoice.equals("Any")&& !trileChoice.equals("Any"))
	 {
	 Globals.getCurrentPC().setDirty(true);
	 PCClass theClass = new PCClass(secChoice);
	 if ((theClass != null) && theClass.isQualified())
	 {
	 PCClass aClass = Globals.getCurrentPC().getClassNamed(theClass.getName());
	 if (aClass == null || Globals.isIgnoreLevelCap() || (!Globals.isIgnoreLevelCap() && aClass.getLevel() < aClass.getMaxLevel()))
	 Globals.getCurrentPC().incrementClassLevel(secleC.getSelectedIndex(), theClass);
	 else
	 JOptionPane.showMessageDialog(null, "Maximum level reached.", "PCGen", JOptionPane.INFORMATION_MESSAGE);
	 }
	 }
	 else
	 {

	 }
	 }
	 //Tertiary Class
	 if (!triChoice.equals("None"))
	 {
	 if (!triChoice.equals("Any")&& !trileChoice.equals("Any"))
	 {
	 Globals.getCurrentPC().setDirty(true);
	 PCClass theClass = new PCClass(triChoice);
	 if ((theClass != null) && theClass.isQualified())
	 {
	 PCClass aClass = Globals.getCurrentPC().getClassNamed(theClass.getName());
	 if (aClass == null || Globals.isIgnoreLevelCap() || (!Globals.isIgnoreLevelCap() && aClass.getLevel() < aClass.getMaxLevel()))
	 Globals.getCurrentPC().incrementClassLevel(trileC.getSelectedIndex(), theClass);
	 else
	 JOptionPane.showMessageDialog(null, "Maximum level reached.", "PCGen", JOptionPane.INFORMATION_MESSAGE);
	 }
	 }
	 else
	 {

	 }
	 }


	 }*/

	/**
	 * Clears the text field
	 */
	private void clearActionPerformed(ActionEvent evt)
	{
		//PlayerCharacter aPC = new PlayerCharacter();

	}
}
