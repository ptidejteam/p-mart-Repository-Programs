/*
 * ToolsCombat.java
 * Copyright 2001 (C) Jason Buchanan <lonejedi70@hotmail.com>
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
 */

package pcgen.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;
import java.util.Vector;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.TableModelEvent;
import javax.swing.table.AbstractTableModel;
import pcgen.core.Globals;
import pcgen.core.PCClass;
import pcgen.core.PlayerCharacter;
import pcgen.core.RollingMethods;


/**
 * @author  Jason Buchanan <lonejedi70@hotmail.com>
 * @version $Revision: 1.1 $
 */

public class ToolsCombat extends JPanel
{
	private final String[] dndAttributes = {"HP", "Init"};
	private final String[] swAttributes = {"VP", "WP", "Init"};
	private final String[] operations = {"Add", "Subtract", "Change"};
	private final String[] dice = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10"};
	private final String[] sides = {"2", "3", "4", "6", "8", "10", "12", "20", "30", "100"};
	private final String[] simpOps = {"+", "-"};

	JTableEx d_CombatTable;
	JButton d_RollInit = new JButton("Roll Init");
	JButton d_ResetDamage = new JButton("Reset Damage");
	JComboBox d_Operation = new JComboBox(operations);
	JTextField d_Damage = new JTextField(2);
	JLabel d_Prep = new JLabel("to");
	JComboBox d_Attribute = new JComboBox();
	JButton d_Modify = new JButton("Modify");
	JButton d_StartCombat = new JButton("Start Combat");
	JButton d_NextChar = new JButton("Next");
	JComboBox d_Dice = new JComboBox(dice);
	JComboBox d_Sides = new JComboBox(sides);
	JComboBox d_SimpOp = new JComboBox(simpOps);
	JTextField d_Mod = new JTextField(2);
	JButton d_Roll = new JButton("=");
	JLabel d_Results = new JLabel();
	TableSorter sorter = new TableSorter(new CombatTableModel());

	private Vector d_InitRolls = new Vector();
	private Vector d_HitPoints = new Vector();
	private Vector d_WoundPoints = new Vector();
	private Vector d_Rounds = new Vector();
	private boolean inSWMode = Globals.isStarWarsMode();  //force a refresh the first time through refreshTable()

	public ToolsCombat()
	{
		initComponents();
	}

	public void refreshTable()
	{
		if (inSWMode != Globals.isStarWarsMode())
		{
			inSWMode = Globals.isStarWarsMode();
			initDamage();
			initAttributes();
		}

		//setPreferredWidths();
		d_CombatTable.updateUI();
	}

	private void setPreferredWidths()
	{
		d_CombatTable.getColumnModel().getColumn(0).setPreferredWidth(150);  //name
		d_CombatTable.getColumnModel().getColumn(1).setPreferredWidth(125);  //class
		d_CombatTable.getColumnModel().getColumn(2).setPreferredWidth(75);   //race
		d_CombatTable.getColumnModel().getColumn(3).setPreferredWidth(75);   //Initiative Roll
		d_CombatTable.getColumnModel().getColumn(4).setPreferredWidth(75);   //Round
		d_CombatTable.getColumnModel().getColumn(5).setPreferredWidth(85);   //HP or VP (current/max)
		d_CombatTable.getColumnModel().getColumn(6).setPreferredWidth(85);   //Flat-Footed AC or WP (current/max)
		d_CombatTable.getColumnModel().getColumn(7).setPreferredWidth(75);   //AC or Defense
		d_CombatTable.getColumnModel().getColumn(8).setPreferredWidth(75);   //Melee BAB
		d_CombatTable.getColumnModel().getColumn(9).setPreferredWidth(75);   //Ranged BAB
		d_CombatTable.getColumnModel().getColumn(10).setPreferredWidth(75);  //Fort Save
		d_CombatTable.getColumnModel().getColumn(11).setPreferredWidth(75);  //Ref Save
		d_CombatTable.getColumnModel().getColumn(12).setPreferredWidth(75);  //Will Save
		d_CombatTable.getColumnModel().getColumn(13).setPreferredWidth(50);  //size
		d_CombatTable.getColumnModel().getColumn(14).setPreferredWidth(75);  //Move

		//int[] cols = {0,1,2,3,4,5,6,7,8,9,10,11,12,13,14};
		//d_CombatTable.setOptimalColumnWidths(cols);

		for (int count = 0; count < 14; count++)
		{
			d_CombatTable.getColumnModel().getColumn(count).setHeaderValue(d_CombatTable.getColumnName(count));
		}
	}

	private void initComponents()
	{
		this.setLayout(new BorderLayout());
		//TableSorter sorter = new TableSorter(new CombatTableModel());
		d_CombatTable = new JTableEx(sorter);
		sorter.addMouseListenerToHeaderInTable(d_CombatTable);
		d_CombatTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		JScrollPane d_StatPane = new JScrollPane(d_CombatTable);
		d_StatPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		d_StatPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		d_StatPane.setDoubleBuffered(true);
		d_StatPane.setPreferredSize(new Dimension(446, 200));

		JPanel d_Interact = new JPanel(new BorderLayout());
		d_RollInit.addActionListener(new ActionListener()
		{
			// Anonymous event handler
			public void actionPerformed(ActionEvent evt)
			{
				d_InitRolls.clear();
				for (int count = 0; count < Globals.getPcList().size(); count++)
				{
					PlayerCharacter tempChar = (PlayerCharacter)Globals.getPcList().get(count);
					d_InitRolls.add(count, new Integer(tempChar.initiativeMod() + RollingMethods.roll(20)));
				}
				refreshTable();
			}
		});
		d_ResetDamage.addActionListener(new ActionListener()
		{
			// Anonymous event handler
			public void actionPerformed(ActionEvent evt)
			{
				initDamage();
				refreshTable();
			}
		});
		d_Operation.addActionListener(new ActionListener()
		{
			// Anonymous event handler
			public void actionPerformed(ActionEvent evt)
			{
				String opSelected = (String)d_Operation.getSelectedItem();
				if (opSelected.equals("Add") || opSelected.equals("Change"))
				{
					d_Prep.setText("to");
				}
				else
				{
					//"Subtract"
					d_Prep.setText("from");
				}
			}
		});
		d_Modify.addActionListener(new ModifyListener());
		d_StartCombat.addActionListener(new StartCombatListener());
		d_NextChar.addActionListener(new NextCharListener());
		d_Roll.addActionListener(new RollListener());
		initAttributes();
		initDamage();

		JPanel d_Stats = new JPanel(new FlowLayout());
		JPanel d_Roller = new JPanel(new FlowLayout());
		d_Stats.add(d_RollInit);
		d_Stats.add(d_ResetDamage);
		d_Stats.add(Box.createHorizontalStrut(20));
		d_Stats.add(d_Operation);
		d_Stats.add(d_Damage);
		d_Stats.add(d_Prep);
		d_Stats.add(d_Attribute);
		d_Stats.add(d_Modify);
		d_Roller.add(d_StartCombat);
		d_Roller.add(d_NextChar);
		d_Roller.add(Box.createHorizontalStrut(20));
		d_Roller.add(d_Dice);
		d_Roller.add(new JLabel("d"));
		d_Roller.add(d_Sides);
		d_Roller.add(d_SimpOp);
		d_Roller.add(d_Mod);
		d_Roller.add(d_Roll);
		d_Roller.add(Box.createHorizontalStrut(10));
		d_Roller.add(d_Results);
		d_Interact.add(d_Stats, BorderLayout.NORTH);
		d_Interact.add(d_Roller, BorderLayout.SOUTH);

		this.add(d_StatPane, BorderLayout.CENTER);
		this.add(d_Interact, BorderLayout.SOUTH);

		setPreferredWidths();
	}

	private void initAttributes()
	{
		String[] newAttributes;
		d_Attribute.removeAllItems();
		if (Globals.isStarWarsMode())
		{
			newAttributes = swAttributes;
		}
		else
		{
			newAttributes = dndAttributes;
		}
		for (int count = 0; count < newAttributes.length; count++)
		{
			d_Attribute.addItem(newAttributes[count]);
		}
	}

	private void initDamage()
	{
		d_HitPoints.clear();
		d_WoundPoints.clear();
		for (int count = 0; count < Globals.getPcList().size(); count++)
		{
			PlayerCharacter tempChar = (PlayerCharacter)Globals.getPcList().get(count);
			d_HitPoints.add(count, new Integer(tempChar.hitPoints()));
			if (Globals.isStarWarsMode())
			{
				d_WoundPoints.add(count, tempChar.woundPoints());
			}
		}
	}

	public class CombatTableModel extends AbstractTableModel
	{
		private final String[] dndColNameList = {"Name", "Class(es)", "Race", "Init", "Round", "HP (Cur/Max)", "Flat", "AC", "Melee BAB", "Ranged BAB", "Fort", "Ref", "Will", "Size", "Move"};
		private final String[] swColNameList = {"Name", "Class(es)", "Race", "Init", "Round", "VP (Cur/Max)", "WP (Cur/Max)", "Defense", "Melee BAB", "Ranged BAB", "Fort", "Ref", "Will", "Size", "Move"};
		private int lastRowCount = 0;

		public int getRowCount()
		{
			if (Globals.getPcList().size() != lastRowCount)
			{
				lastRowCount = Globals.getPcList().size();
				fireTableChanged(new TableModelEvent(this));
			}
			return Globals.getPcList().size();
		}

		public int getColumnCount()
		{
			return Globals.isStarWarsMode() ? swColNameList.length : dndColNameList.length;
		}

		public Class getColumnClass(int columnIndex)
		{
			if (columnIndex <= 2 || columnIndex == 5 || ((columnIndex == 6) && Globals.isStarWarsMode()) || columnIndex == 8 || columnIndex == 9 || columnIndex >= 13)
			{
				return String.class;
			}
			else
			{
				return Integer.class;
			}
		}

		public String getColumnName(int columnIndex)
		{
			return Globals.isStarWarsMode() ? swColNameList[columnIndex] : dndColNameList[columnIndex];
		}

		public Object getValueAt(int row, int column)
		{
			PlayerCharacter curChar = (PlayerCharacter)Globals.getPcList().get(row);
			boolean isIncomplete = curChar.getClassList().isEmpty();  //if the character has no levels then it isn't complete enough for most stats
			switch (column)
			{
				case 0: //name
					return curChar.getName();
				case 1: //class(es)
					String classes = "";
					if (isIncomplete)
					{
						return "";
					}  //if the character has no levels then don't bother getting data
					Iterator classIter = curChar.getClassList().iterator();
					boolean useAbbreviations = (curChar.getClassList().size() > 1); //if multi-classed then abbreviate
					while (classIter.hasNext())
					{
						PCClass theClass = (PCClass)classIter.next();
						if (!theClass.getName().startsWith("Domain"))
						{
							if (useAbbreviations)
								classes = classes + theClass.getAbbrev() + theClass.getLevel() + "/";
							else
								classes = classes + theClass.classLevelString() + "/";
						}
					}
					if (classes.endsWith("/"))
					{
						classes = classes.substring(0, classes.length() - 1);
					}
					return classes;
				case 2: //race
					if (isIncomplete)
					{
						return "";
					}  //if the character has no levels then don't bother getting data
					return curChar.getRace().getName();
				case 3: //Initiative
					if (isIncomplete || d_InitRolls.isEmpty() || d_InitRolls.size() != Globals.getPcList().size())
					{
						return new Integer(0);
					}  //if the character has no levels then don't bother getting data
					return d_InitRolls.get(row);
				case 4: //Round
					if (isIncomplete || d_Rounds.isEmpty() || d_Rounds.size() != Globals.getPcList().size())
					{
						return new Integer(0);
					}  //if the character has no levels then don't bother getting data
					return d_Rounds.get(row);
				case 5: //HP or VP
					if (isIncomplete)
					{
						return "";
					}  //if the character has no levels then don't bother getting data
					if (d_HitPoints.size() != Globals.getPcList().size())
					{
						initDamage();
					}
					String hpDisplay = d_HitPoints.get(row) + "/" + curChar.hitPoints();
					return hpDisplay;
				case 6: //Flat-Footed AC or WP
					if (isIncomplete)
					{
						if (Globals.isStarWarsMode())
						{
							return "";
						}
						return new Integer(0);
					}  //if the character has no levels then don't bother getting data
					if (Globals.isStarWarsMode())
					{
						if (d_WoundPoints.size() != Globals.getPcList().size())
						{
							initDamage();
						}
						String wpDisplay = d_WoundPoints.get(row) + "/" + curChar.woundPoints();
						return wpDisplay;
					}
					return new Integer(curChar.flatFootedAC());
				case 7: //AC or Defense
					if (isIncomplete)
					{
						return new Integer(0);
					}  //if the character has no levels then don't bother getting data
					return Globals.isStarWarsMode() ? curChar.defense() : new Integer(curChar.totalAC());
				case 8: //Melee BAB
					if (isIncomplete)
					{
						return new Integer(0);
					}  //if the character has no levels then don't bother getting data
					return curChar.getAttackString(0, curChar.calcStatMod(Globals.STRENGTH));
				case 9: //Ranged BAB
					if (isIncomplete)
					{
						return new Integer(0);
					}  //if the character has no levels then don't bother getting data
					return curChar.getAttackString(1, curChar.calcStatMod(Globals.DEXTERITY));
				case 10: //Fort Save
					if (isIncomplete)
					{
						return new Integer(0);
					}  //if the character has no levels then don't bother getting data
					return new Integer(curChar.getBonus(1, true) + curChar.calcStatMod(Globals.CONSTITUTION));
				case 11: //Ref Save
					if (isIncomplete)
					{
						return new Integer(0);
					}  //if the character has no levels then don't bother getting data
					return new Integer(curChar.getBonus(2, true) + curChar.calcStatMod(Globals.DEXTERITY));
				case 12: //Will Save
					if (isIncomplete)
					{
						return new Integer(0);
					}  //if the character has no levels then don't bother getting data
					return new Integer(curChar.getBonus(3, true) + curChar.calcStatMod(Globals.WISDOM));
				case 13: //size
					if (isIncomplete)
					{
						return "";
					}  //if the character has no levels then don't bother getting data
					return curChar.getSize();
				case 14: //Move
					if (isIncomplete)
					{
						return "";
					}  //if the character has no levels then don't bother getting data
					String movelabel = curChar.getRace().movementType(0) + " " + curChar.movement(0) + "'";
					for (int x = 1; x < curChar.getRace().getMovements().length; x++)
						movelabel += ", " + curChar.getRace().movementType(x) + " " + curChar.movement(x) + "'";
					return movelabel;
			}
			return null;
		}
	}

	private class ModifyListener implements ActionListener
	{
		public void actionPerformed(ActionEvent evt)
		{
			int row = d_CombatTable.getSelectedRow();

			PlayerCharacter curChar = (PlayerCharacter)Globals.getPcList().get(row);
			row = getActualPCRow(row);
			if (row >= 0 && d_Damage.getText().length() > 0)
			{
				int mod = d_Operation.getSelectedIndex() == 1 ? -1 : 1;
				int value = Integer.valueOf(d_Damage.getText()).intValue();
				int modvalue = mod * value;
				int newValue;
				String target = (String)d_Attribute.getSelectedItem();

				if (target.equals("Init") && d_InitRolls.size() == Globals.getPcList().size())
				{
					Integer oldValue = (Integer)d_InitRolls.get(row);
					if (d_Operation.getSelectedIndex() == 2)
						newValue = modvalue;
					else
						newValue = oldValue.intValue() + modvalue;
					d_InitRolls.set(row, new Integer(newValue));
				}
				else if (target.equals("WP") && d_WoundPoints.size() == Globals.getPcList().size())
				{
					Integer oldValue = (Integer)d_WoundPoints.get(row);
					if (d_Operation.getSelectedIndex() == 2)
						newValue = modvalue;
					else
						newValue = oldValue.intValue() + modvalue;
					d_WoundPoints.set(row, new Integer(newValue));
				}
				else if ((target.equals("HP") || target.equals("VP")) && d_HitPoints.size() == Globals.getPcList().size())
				{
					//HP or VP
					Integer oldValue = (Integer)d_HitPoints.get(row);
					if (d_Operation.getSelectedIndex() == 2)
						newValue = modvalue;
					else
						newValue = oldValue.intValue() + modvalue;
					d_HitPoints.set(row, new Integer(newValue));
				}
				refreshTable();
			}
		}
	}

	private class StartCombatListener implements ActionListener
	{
		public void actionPerformed(ActionEvent evt)
		{
			d_Rounds.clear();
			for (int count = 0; count < Globals.getPcList().size(); count++)
			{
				d_Rounds.add(count, new Integer(1));
			}
			sorter.sortByColumn(3, false);
			d_CombatTable.clearSelection();
			d_CombatTable.addRowSelectionInterval(0, 0);
			refreshTable();
		}
	}

	private class NextCharListener implements ActionListener
	{
		public void actionPerformed(ActionEvent evt)
		{
			Integer IntCurRound = (Integer)d_Rounds.get(getActualPCRow(0));
			int curRound = IntCurRound.intValue();
			int pcRow = getActualPCRow(0);
			int selected = 0;
			for (int count = 0; count < Globals.getPcList().size(); count++)
			{
				Integer countCurRound = (Integer)d_Rounds.get(getActualPCRow(count));
				if (curRound > countCurRound.intValue())
				{
					pcRow = getActualPCRow(count);
					selected = count;
					curRound--;
				}
			}

			curRound++;
			// Sets the previous person to the next round.
			d_Rounds.set(pcRow, new Integer(curRound));
			selected++;
			if (selected >= Globals.getPcList().size())
				selected = 0;
			sorter.sortByColumn(3, false);
			d_CombatTable.clearSelection();
			d_CombatTable.addRowSelectionInterval(selected, selected);
			refreshTable();
		}
	}

	private class RollListener implements ActionListener
	{
		public void actionPerformed(ActionEvent evt)
		{
			int numDice = d_Dice.getSelectedIndex() + 1;
			int numSides = Integer.valueOf((String)d_Sides.getSelectedItem()).intValue();
			int multMod = (d_SimpOp.getSelectedIndex() == 0) ? 1 : -1;
			int addition = (d_Mod.getText().length() > 0) ? multMod * Integer.valueOf(d_Mod.getText()).intValue() : 0;
			Integer result = new Integer(RollingMethods.roll(numDice, numSides, addition));

			d_Results.setText(result.toString());
			d_Damage.setText(result.toString());
		}
	}

	// Used to figure out what the actual row is for all the various operations.
	// Needed because when people sort the table, the order of Globals.getPcList() doesnt' change.
	// note that this will *not* work with characters that have duplicate names. (the "first" character will always win.)
	private int getActualPCRow(int inRow)
	{
		String charName = (String)d_CombatTable.getValueAt(inRow, 0);
		for (int outRow = 0; outRow < Globals.getPcList().size(); outRow++)
		{
			PlayerCharacter tempChar = (PlayerCharacter)Globals.getPcList().get(outRow);
			if (tempChar.getName().compareTo(charName) == 0)
				return outRow;
		}
		System.out.println("NO MATCH");
		return 0;
	}
}
