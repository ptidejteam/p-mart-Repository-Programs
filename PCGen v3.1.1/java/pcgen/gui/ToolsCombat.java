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

// TODO list: Why not scrolls that are containers for spells? this if
// working makes the spellbook item far more possible.  Changing the
// spell system: there should be 3 types of "spellbooks" - that name
// needs to be changed. spell collections? Anyways, 3 types, known,
// memorized, and spellbook. Cleric types can have multiple type
// memorized. sorcerers can have 1 known. wizards can have multiple
// type memorized, and multiple type spellbook, and one type known
// (only from spell mastery feat). When a new character is created,
// they get 1 of each of the types they can have, called
// "Default". Each different "spellbook" is a different line item that
// can be added/removed - the ones where there can be multiple copies,
// can have their name changed. Otherwise, the add/remove is greyed
// out. (this is only true for sorcerers. Disallow other classes to
// remove their last one? Or no?). If clerics, memorized pulls from
// the full list. Otherwise, memorized pulls from the combined total
// of known and all spellbooks. At some point, we should probably add
// a flag for spellbooks, so individual ones aren't used to build the
// memorizable list. Metamagic feats only apply to memorized
// spellbooks, and bump the spell up the appropriate levels (and it
// uses the appropriate slot.). All character sheets should handle
// this by looping through all the spell casting classes, and for each
// looping thorugh all the known, followed by all the spellbooks,
// followed by all the memorized.  two things - one, how to handle
// multiple rows selected? Should we allow this? Two, how to handle
// pcs selected that aren't supposed to go - should we allow
// initiative things? Change the next button to "current" pc, perhaps?
// - I like this.  Okay, here's the plan. If multiple rows are
// selected, next turns into Current, and you can't delay.  unloading
// of parties - when you load a party, it sets all characters in the
// party as belonging to that party, and you can unload a given party,
// where it will remove all the chars from that party.  Add flatfooted
// to statuses (every character is flatfooted at the start of
// combat. On their first action, they're no longer flat footed).
// deal with surprise? How?  deal with delay limits - -10 + initative
// mod?
package pcgen.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import pcgen.core.Constants;
import pcgen.core.Globals;
import pcgen.core.PCClass;
import pcgen.core.PlayerCharacter;
import pcgen.core.RollingMethods;

/**
 * @author  Jason Buchanan <lonejedi70@hotmail.com>
 * @version $Revision: 1.1 $
 */

class ToolsCombat extends JPanel
{
	private final String[] dndAttributes = {"Init", "HP"};
	private final String[] swAttributes = {"VP", "WP", "Init"};
	private final String[] sidewAttributes = {"Vigor", "Body", "Init"};
	private final String[] operations = {"Add", "Subtract", "Change"};
	private final String[] initOperations = {"Delay", "Ready", "Refocus"};
	private final String statusNormal = "-";
	private final String statusDelayed = "Delayed";
	private final String statusReadied = "Readied";
	private final String statusFlatfooted = "Flatfooted";
	private final String statusIncapacitated = "Incapacitated";
	private final String statusBleeding = "Bleeding";
	private final String statusStabilized = "Stabilized";
	private final String statusDead = "Dead";

/////////////////////////////////////////////////
// Yanked for WotC compliance
//	JButton d_RollInit = new JButton("Roll Init");
//	private final String[] dice = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10"};
//	private final String[] sides = {"2", "3", "4", "6", "8", "10", "12", "20", "30", "100"};
//	private final String[] simpOps = {"+", "-"};
//
//	JComboBox d_Dice = new JComboBox(dice);
//	JComboBox d_Sides = new JComboBox(sides);
//	JComboBox d_SimpOp = new JComboBox(simpOps);
//	JButton d_Roll = new JButton("=");
	JButton d_RollInit = new JButton("Reset Init");
/////////////////////////////////////////////////

	JTableEx d_CombatTable;
	JButton d_ResetDamage = new JButton("Reset Damage");
	JComboBox d_Operation = new JComboBox(operations);
	JTextField d_Damage = new JTextField(2);
	JLabel d_Prep = new JLabel("to");
	JComboBox d_Attribute = new JComboBox();
	JButton d_Modify = new JButton("Modify");
	JButton d_StartCombat = new JButton("Start Combat");
	JButton d_NextChar = new JButton("Next");
	JComboBox d_initOperations = new JComboBox(initOperations);
	JButton d_applyInitOp = new JButton("Apply");
	JTextField d_Mod = new JTextField(2);
	JLabel d_Results = new JLabel();
	JButton d_QS = new JButton("Quick Stats");
	ToolsQStats d_qsPanel = new ToolsQStats();
	JButton d_XP = new JButton("XP Tracker");
	ToolsXP d_xpPanel = new ToolsXP();

	CombatTableSorter sorter = new CombatTableSorter(new CombatTableModel());
	//TableSorter sorter = new TableSorter(new CombatTableModel());
	//ListSelectionModel selectionmodel;

	private ArrayList d_InitRolls = new ArrayList();
	private ArrayList d_OldInitRolls = new ArrayList();
	private ArrayList d_HitPoints = new ArrayList();
	private ArrayList d_WoundPoints = new ArrayList();
	private ArrayList d_Rounds = new ArrayList();
	private ArrayList d_Status = new ArrayList();
	//force a refresh the first time through refreshTable()
	private boolean inSWMode = Globals.isStarWarsMode() || Globals.isSidewinderMode() || Globals.isSpycraftMode();
	private boolean isInitOperationsDirty = false;
	// Used to track the PC that's supposed to be selected.
	private int currentPCRow = -1;
	// Used to track the current round of combat.
	private int currentRound = 0;

	public ToolsCombat()
	{
		initComponents();
	}

	public void refreshTable()
	{
		if (inSWMode != Globals.isStarWarsMode() || Globals.isSidewinderMode() || Globals.isSpycraftMode())
		{
			inSWMode = Globals.isStarWarsMode() || Globals.isSidewinderMode() || Globals.isSpycraftMode();
			initDamage();
			initAttributes();
		}

		d_qsPanel.refreshTable();
		d_xpPanel.refreshTable();
		//setPreferredWidths();
		d_CombatTable.updateUI();
	}

	private void setPreferredWidths()
	{
		d_CombatTable.getColumnModel().getColumn(0).setPreferredWidth(150);  //name
		d_CombatTable.getColumnModel().getColumn(1).setPreferredWidth(125);  //class
		d_CombatTable.getColumnModel().getColumn(2).setPreferredWidth(70);   //race
		d_CombatTable.getColumnModel().getColumn(3).setPreferredWidth(40);   //Initiative Roll
		d_CombatTable.getColumnModel().getColumn(4).setPreferredWidth(60);   //Round
		d_CombatTable.getColumnModel().getColumn(5).setPreferredWidth(70);   //Status
		d_CombatTable.getColumnModel().getColumn(6).setPreferredWidth(85);   //HP or VP (current/max)
		d_CombatTable.getColumnModel().getColumn(7).setPreferredWidth(40);   //Flat-Footed AC or WP (current/max)
		d_CombatTable.getColumnModel().getColumn(8).setPreferredWidth(40);   //AC or Defense
		d_CombatTable.getColumnModel().getColumn(9).setPreferredWidth(75);   //Melee BAB
		d_CombatTable.getColumnModel().getColumn(10).setPreferredWidth(75);   //Ranged BAB
		d_CombatTable.getColumnModel().getColumn(11).setPreferredWidth(30);  //Check1
		d_CombatTable.getColumnModel().getColumn(12).setPreferredWidth(30);  //Check2
		d_CombatTable.getColumnModel().getColumn(13).setPreferredWidth(30);  //Check3
		d_CombatTable.getColumnModel().getColumn(14).setPreferredWidth(30);  //size
		d_CombatTable.getColumnModel().getColumn(15).setPreferredWidth(75);  //Move

		//int[] cols = {0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15};
		//d_CombatTable.setOptimalColumnWidths(cols);

		for (int count = 0; count < 16; count++)
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
		ListSelectionModel selectionmodel = d_CombatTable.getSelectionModel();
		selectionmodel.addListSelectionListener(new initOperationsLSListener());
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
				d_OldInitRolls.clear();
				final PlayerCharacter oldPC = Globals.getCurrentPC();
				for (int count = 0; count < Globals.getPCList().size(); count++)
				{
					final PlayerCharacter tempChar = (PlayerCharacter)Globals.getPCList().get(count);
					Globals.setCurrentPC(tempChar);
/////////////////////////////////////////////////
// Yanked for WotC compliance
//					d_InitRolls.add(count, new Integer(tempChar.initiativeMod() + RollingMethods.roll(20)));
					d_InitRolls.add(count, new Integer(tempChar.initiativeMod()));
/////////////////////////////////////////////////
					d_OldInitRolls.add(count, new Integer(0));
				}
				Globals.setCurrentPC(oldPC);
				refreshTable();
			}
		});
		d_ResetDamage.addActionListener(new ActionListener()
		{
			// Anonymous event handler
			public void actionPerformed(ActionEvent evt)
			{
				initDamage();
				initStatus();
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
		d_applyInitOp.addActionListener(new applyInitOpListener());
/////////////////////////////////////////////////
// Yanked for WotC compliance
//		d_Roll.addActionListener(new RollListener());
/////////////////////////////////////////////////
		initAttributes();
		initDamage();
		initStatus();

		JPanel d_Stats = new JPanel(new FlowLayout());
		d_Stats.add(d_RollInit);
		d_Stats.add(d_ResetDamage);
		d_Stats.add(Box.createHorizontalStrut(20));
		d_Stats.add(d_Operation);
		d_Stats.add(d_Damage);
		d_Stats.add(d_Prep);
		d_Stats.add(d_Attribute);
		d_Stats.add(d_Modify);

		JPanel d_Roller = new JPanel(new FlowLayout());
		d_Roller.add(d_StartCombat);
		d_Roller.add(d_NextChar);
		d_Roller.add(d_initOperations);
		d_Roller.add(d_applyInitOp);
/////////////////////////////////////////////////
// Yanked for WotC compliance
//		d_Roller.add(Box.createHorizontalStrut(20));
//		d_Roller.add(d_Dice);
//		d_Roller.add(new JLabel("d"));
//		d_Roller.add(d_Sides);
//		d_Roller.add(d_SimpOp);
//		d_Roller.add(d_Mod);
//		d_Roller.add(d_Roll);
//		d_Roller.add(Box.createHorizontalStrut(10));
//		d_Roller.add(d_Results);
/////////////////////////////////////////////////

		JPanel d_Quickies = new JPanel(new BorderLayout());
		d_Quickies.add(d_QS, BorderLayout.NORTH);
		d_QS.addActionListener(new QSListener());
		d_Quickies.add(d_XP, BorderLayout.SOUTH);
		d_XP.addActionListener(new XPListener());

		d_Interact.add(d_Stats, BorderLayout.CENTER);
		d_Interact.add(d_Roller, BorderLayout.SOUTH);
		d_Interact.add(d_Quickies, BorderLayout.EAST);

		this.add(d_StatPane, BorderLayout.CENTER);
		this.add(d_Interact, BorderLayout.SOUTH);

		setPreferredWidths();
	}

	private void initAttributes()
	{
		String[] newAttributes;
		d_Attribute.removeAllItems();
		if (Globals.isStarWarsMode() || Globals.isSpycraftMode())
		{
			newAttributes = swAttributes;
		}
		else if (Globals.isSidewinderMode())
			newAttributes = sidewAttributes;
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
		final PlayerCharacter oldPC = Globals.getCurrentPC();
		for (int count = 0; count < Globals.getPCList().size(); count++)
		{
			final PlayerCharacter tempChar = (PlayerCharacter)Globals.getPCList().get(count);
			Globals.setCurrentPC(tempChar);
			d_HitPoints.add(count, new Integer(tempChar.hitPoints()));
			if (Globals.isStarWarsMode() || Globals.isSidewinderMode() || Globals.isSpycraftMode())
			{
				d_WoundPoints.add(count, tempChar.woundPoints());
			}
		}
		Globals.setCurrentPC(oldPC);
	}

	private void initRounds()
	{
		d_Rounds.clear();
		for (int count = 0; count < Globals.getPCList().size(); count++)
			d_Rounds.add(count, new Integer(1));
		currentRound = 1;
	}

	private void initStatus()
	{
		d_Status.clear();
		for (int count = 0; count < Globals.getPCList().size(); count++)
			d_Status.add(count, statusNormal);
		d_OldInitRolls.clear();
		for (int count = 0; count < Globals.getPCList().size(); count++)
			d_OldInitRolls.add(count, new Integer(0));
	}

	class CombatTableModel extends AbstractTableModel
	{
		private final String[] dndColNameList = {"Name", "Class(es)", "Race", "Init", "Round", "Status", "HP (Cur/Max)", "Flat", "AC", "Melee BAB", "Ranged BAB", "", "", "", "Size", "Move"};
		private final String[] swColNameList = {"Name", "Class(es)", "Race", "Init", "Round", "Status", "VP (Cur/Max)", "WP (Cur/Max)", "Defense", "Melee BAB", "Ranged BAB", "", "", "", "Size", "Move"};
		private final String[] sidewColNameList = {"Name", "Class(es)", "Race", "Init", "Round", "Status", "Vigor (Cur/Max)", "Body (Cur/Max)", "AC", "Melee BAB", "Ranged BAB", "", "", "", "Size", "Move"};
		private int lastRowCount = 0;

		public int getRowCount()
		{
			final int pcListSize = Globals.getPCList().size();
			if (pcListSize != lastRowCount)
			{
				lastRowCount = pcListSize;
				fireTableChanged(new TableModelEvent(this));
			}
			return pcListSize;
		}

		public int getColumnCount()
		{
			if (Globals.isStarWarsMode() || Globals.isSpycraftMode())
			{
				return swColNameList.length;
			}
			else if (Globals.isSidewinderMode())
			{
				return sidewColNameList.length;
			}
			return dndColNameList.length;
		}

		public Class getColumnClass(int columnIndex)
		{
			if (columnIndex <= 2 || columnIndex == 5 || columnIndex == 6 || ((columnIndex == 7) && (Globals.isStarWarsMode() || Globals.isSidewinderMode() || Globals.isSpycraftMode())) || columnIndex == 9 || columnIndex == 10 || columnIndex >= 14)
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
			if (columnIndex == 11 && Globals.getCheckList().size() > 0)
				return Globals.getCheckList().get(0).toString();
			if (columnIndex == 12 && Globals.getCheckList().size() > 1)
				return Globals.getCheckList().get(1).toString();
			if (columnIndex == 13 && Globals.getCheckList().size() > 2)
				return Globals.getCheckList().get(2).toString();

			if (Globals.isStarWarsMode() || Globals.isSpycraftMode())
			{
				return swColNameList[columnIndex];
			}
			else if (Globals.isSidewinderMode())
			{
				return sidewColNameList[columnIndex];
			}
			return dndColNameList[columnIndex];
		}

		public Object getValueAt(int row, int column)
		{
			final PlayerCharacter oldPC = Globals.getCurrentPC();
			Object retVal = null;
			final PlayerCharacter curChar = (PlayerCharacter)Globals.getPCList().get(row);
			Globals.setCurrentPC(curChar);
			boolean isIncomplete = (curChar.hitPoints() < 1);  //if the character hasn't acquired HP, then it isn't complete enough for most stats
			switch (column)
			{
				case 0: //name
					retVal = curChar.getName();
					break;
				case 1: //class(es)
					StringBuffer classes = new StringBuffer();
					if (isIncomplete)
					{
						retVal = "";
					}  //if the character has no levels then don't bother getting data
					else
					{
						Iterator classIter = curChar.getClassList().iterator();
						boolean useAbbreviations = (curChar.getClassList().size() > 1); //if multi-classed then abbreviate
						while (classIter.hasNext())
						{
							PCClass theClass = (PCClass)classIter.next();
							if (!theClass.getName().startsWith("Domain"))
							{
								if (useAbbreviations)
								{
									classes.append(theClass.getAbbrev()).append(theClass.getLevel()).append("/");
								}
								else
								{
									classes.append(theClass.classLevelString()).append("/");
								}
							}
						}
						if (classes.toString().endsWith("/"))
						{
							//classes = classes.substring(0, classes.length() - 1);
							classes = new StringBuffer(classes.substring(0, classes.length() - 1));
						}
						retVal = classes;
					}
					break;
				case 2: //race
					if (isIncomplete) //if the character has no levels then don't bother getting data
						retVal = "";
					else
						retVal = curChar.getRace().getName();
					break;
				case 3: //Initiative
					//if the character has no levels then don't bother getting data
					if (isIncomplete || d_InitRolls.isEmpty() || d_InitRolls.size() != Globals.getPCList().size())
						retVal = new Integer(0);
					else
						retVal = d_InitRolls.get(row);
					break;
				case 4: //Round
					if (isIncomplete || d_Rounds.isEmpty() || d_Rounds.size() != Globals.getPCList().size())
					{
						retVal = new Integer(0);
					}  //if the character has no levels then don't bother getting data
					else
						retVal = d_Rounds.get(row);
					break;
				case 5: //Round
					if (d_Status.isEmpty() || d_Status.size() != Globals.getPCList().size())
					{
						initStatus();
					}  //if the character has no levels then don't bother getting data
					retVal = d_Status.get(row);
					break;
				case 6: //HP or VP
					if (isIncomplete)
					{
						retVal = "";
					}  //if the character has no levels then don't bother getting data
					else
					{
						if (d_HitPoints.size() != Globals.getPCList().size())
						{
							initDamage();
						}
						retVal = d_HitPoints.get(row) + "/" + curChar.hitPoints();
					}
					break;
				case 7: //Flat-Footed AC or WP
					if (isIncomplete)
					{
						if (Globals.isStarWarsMode() || Globals.isSidewinderMode() || Globals.isSpycraftMode())
						{
							retVal = "";
						}
						else
							retVal = new Integer(0);
					}  //if the character has no levels then don't bother getting data
					else
					{
						if (Globals.isStarWarsMode() || Globals.isSidewinderMode() || Globals.isSpycraftMode())
						{
							if (d_WoundPoints.size() != Globals.getPCList().size())
							{
								initDamage();
							}
							retVal = d_WoundPoints.get(row) + "/" + curChar.woundPoints();
						}
						if (retVal == null)
							retVal = new Integer(curChar.flatFootedAC());
					}
					break;
				case 8: //AC or Defense
					if (isIncomplete)
					{
						retVal = new Integer(0);
					}  //if the character has no levels then don't bother getting data
					else if (Globals.isStarWarsMode() || Globals.isSidewinderMode() || Globals.isSpycraftMode())
					{
						retVal = curChar.DefenseTotal();
					}
					else
						retVal = new Integer(curChar.totalAC());
					break;
				case 9: //Melee BAB
					if (isIncomplete)
					{
						retVal = new Integer(0);
					}  //if the character has no levels then don't bother getting data
					else
						retVal = curChar.getAttackString(Constants.ATTACKSTRING_MELEE, curChar.getStatBonusTo("TOHIT", "TYPE=MELEE"));
					break;
				case 10: //Ranged BAB
					if (isIncomplete)
					{
						retVal = new Integer(0);
					}  //if the character has no levels then don't bother getting data
					else
						retVal = curChar.getAttackString(Constants.ATTACKSTRING_RANGED, curChar.getStatBonusTo("TOHIT", "TYPE=RANGED"));
					break;
				case 11: //Check1
					if (isIncomplete)
					{
						retVal = new Integer(0);
					}  //if the character has no levels then don't bother getting data
					else
						retVal = new Integer(curChar.getBonus(1, true));
					break;
				case 12: //Check2
					if (isIncomplete)
					{
						retVal = new Integer(0);
					}  //if the character has no levels then don't bother getting data
					else
						retVal = new Integer(curChar.getBonus(2, true));
					break;
				case 13: //Check3
					if (isIncomplete)
					{
						retVal = new Integer(0);
					}  //if the character has no levels then don't bother getting data
					else
						retVal = new Integer(curChar.getBonus(3, true));
					break;
				case 14: //size
					if (isIncomplete)
					{
						retVal = "";
					}  //if the character has no levels then don't bother getting data
					else
						retVal = curChar.getSize();
					break;
				case 15: //Move
					if (isIncomplete)
					{
						retVal = "";
					}  //if the character has no levels then don't bother getting data
					else
					{
						StringBuffer moveLabel = new StringBuffer();
						moveLabel.append(curChar.getRace().getMovementType(0)).append(" ").append(curChar.movement(0)).append("'");
						for (int x = 1; x < curChar.getRace().getMovements().length; x++)
						{
							moveLabel.append(", ").append(curChar.getRace().getMovementType(x)).append(" ").append(curChar.movement(x)).append("'");
						}
						retVal = moveLabel.toString();
					}
					break;
				default:
					Globals.errorPrint("In ToolsCombat.CombatTableModelgetValueAt the column " + column + " is not handled.");
					break;
			}
			Globals.setCurrentPC(oldPC);
			return retVal;
		}
	}

	private class initOperationsLSListener implements ListSelectionListener
	{
		public void valueChanged(ListSelectionEvent e)
		{
			if (!e.getValueIsAdjusting())
			{
				boolean changedInitOperations = false;
				if (d_CombatTable.getSelectedRowCount() == 1)
				{
					int row = d_CombatTable.getSelectedRow();
					String status = (String)d_Status.get(getActualPCRow(row));
					if (status.compareTo(statusDelayed) == 0 ||
					  status.compareTo(statusReadied) == 0)
					{
						d_initOperations.removeAllItems();
						d_initOperations.addItem(new String(status.compareTo(statusDelayed) != 0 ? "Delay" : "Undelay"));
						d_initOperations.addItem(new String(status.compareTo(statusReadied) != 0 ? "Ready" : "Unready"));
						d_initOperations.addItem("Refocus");

						if (status.compareTo(statusDelayed) == 0)
							d_initOperations.setSelectedIndex(0);
						if (status.compareTo(statusReadied) == 0)
							d_initOperations.setSelectedIndex(1);
						changedInitOperations = true;
						isInitOperationsDirty = true;
						//Globals.debugPrint("VALUE CHANGED:" + d_CombatTable.getSelectedRow());
					}
					else if (currentPCRow >= 0 && currentPCRow != row)
					{
						//Globals.debugPrint("1 currentPCRow: " + currentPCRow);
						//Globals.debugPrint("1 row: " + row);
						d_NextChar.setText("Current");

						changedInitOperations = true;
						isInitOperationsDirty = true;
					}
					else if (status.compareTo(statusBleeding) == 0)
					{
						d_NextChar.setText("Stabilize");
						d_applyInitOp.setEnabled(false);

						changedInitOperations = true;
						isInitOperationsDirty = true;
					}
					else if (status.compareTo(statusIncapacitated) == 0 ||
					  status.compareTo(statusStabilized) == 0 ||
					  status.compareTo(statusDead) == 0)
					{
						d_applyInitOp.setEnabled(false);

						changedInitOperations = true;
						isInitOperationsDirty = true;
					}
				}
				else if (currentPCRow >= 0)
				{
					//Globals.debugPrint("2 currentPCRow: " + currentPCRow);
					d_NextChar.setText("Current");

					changedInitOperations = true;
					isInitOperationsDirty = true;
				}

				if (isInitOperationsDirty && !changedInitOperations)
				{
					d_initOperations.removeAllItems();
					d_initOperations.addItem("Delay");
					d_initOperations.addItem("Ready");
					d_initOperations.addItem("Refocus");
					d_NextChar.setText("Next");
					d_applyInitOp.setEnabled(true);
					isInitOperationsDirty = false;
				}
			}
		}
	}

	private class QSListener implements ActionListener
	{
		JFrame frame = null;

		public void actionPerformed(ActionEvent e)
		{
			if (frame == null)
			{
				frame = new JFrame("Quick Stats");
				frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
				frame.getContentPane().add(d_qsPanel);
				frame.pack();
			}
			else
			{
				d_qsPanel.refreshTable();
			}

			frame.show();
		}
	}

	private class XPListener implements ActionListener
	{
		JFrame frame = null;

		public void actionPerformed(ActionEvent e)
		{
			if (frame == null)
			{
				frame = new JFrame("XP Tracker");
				frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
				frame.getContentPane().add(d_xpPanel);
				frame.pack();
			}
			else
			{
				d_xpPanel.refreshTable();
			}

			frame.show();
		}
	}

	private class ModifyListener implements ActionListener
	{
		public void actionPerformed(ActionEvent evt)
		{
			int row = d_CombatTable.getSelectedRow();

			row = getActualPCRow(row);
			if (row >= 0 && d_Damage.getText().length() > 0)
			{
				int mod = d_Operation.getSelectedIndex() == 1 ? -1 : 1;
				int value = Integer.valueOf(d_Damage.getText()).intValue();
				int modvalue = mod * value;
				int newValue;
				String target = (String)d_Attribute.getSelectedItem();

				if (target.equals("Init") && d_InitRolls.size() == Globals.getPCList().size())
				{
					Integer oldValue = (Integer)d_InitRolls.get(row);
					if (d_Operation.getSelectedIndex() == 2)
						newValue = modvalue;
					else
						newValue = oldValue.intValue() + modvalue;
					d_InitRolls.set(row, new Integer(newValue));
				}
				else if (target.equals("WP") && d_WoundPoints.size() == Globals.getPCList().size())
				{
					Integer oldValue = (Integer)d_WoundPoints.get(row);
					if (d_Operation.getSelectedIndex() == 2)
						newValue = modvalue;
					else
						newValue = oldValue.intValue() + modvalue;
					d_WoundPoints.set(row, new Integer(newValue));
				}
				else if ((target.equals("HP") || target.equals("VP")) && d_HitPoints.size() == Globals.getPCList().size())
				{
					//HP or VP
					Integer oldValue = (Integer)d_HitPoints.get(row);
					if (d_Operation.getSelectedIndex() == 2)
						newValue = modvalue;
					else
						newValue = oldValue.intValue() + modvalue;

					// The character has fully recovered, for whatever reason.
					if (oldValue.intValue() <= 0 &&
					  newValue > 0)
						d_Status.set(row, statusNormal);

					if (newValue == 0)
					{
						// TODO: make sure incapacitated/dead people can't do initiative things.
						d_Status.set(row, statusIncapacitated);
						d_OldInitRolls.set(row, new Integer(0));
					}
					else if (newValue < 0 && newValue > -10)
					{
						// bleeding, then healed.
						if (modvalue > 0)
							d_Status.set(row, statusStabilized);
						else
							d_Status.set(row, statusBleeding);
						d_OldInitRolls.set(row, new Integer(0));
					}
					else if (newValue < -10)
					{
						d_Status.set(row, statusDead);
						d_OldInitRolls.set(row, new Integer(0));
					}

					d_HitPoints.set(row, new Integer(newValue));
					// "Hack" to handle making sure things get updated properly.
					d_CombatTable.clearSelection();
					d_CombatTable.addRowSelectionInterval(row, row);
				}
				refreshTable();
			}
		}
	}

	private class StartCombatListener implements ActionListener
	{
		public void actionPerformed(ActionEvent evt)
		{
			initRounds();
			initStatus();
			sorter.sortByColumn(3, false);
			d_CombatTable.clearSelection();
			d_CombatTable.addRowSelectionInterval(0, 0);
			currentPCRow = 0;
			for (int count = 0; count < Globals.getPCList().size(); count++)
				d_Status.set(count, statusFlatfooted);
			d_Status.set(getActualPCRow(0), statusNormal);
			refreshTable();
		}
	}

	private class NextCharListener implements ActionListener
	{
		public void actionPerformed(ActionEvent evt)
		{
			// Combat hasn't started yet - next does nothing.
			if (currentPCRow < 0)
				return;

			int curRow = d_CombatTable.getSelectedRow();
			int numRows = d_CombatTable.getSelectedRowCount();
			if (numRows > 1 || (currentPCRow >= 0 && currentPCRow != curRow))
			{
				d_CombatTable.clearSelection();
				d_CombatTable.addRowSelectionInterval(currentPCRow, currentPCRow);
				return;
			}

			int pcRow = getActualPCRow(curRow);

			// Okay, they've gone - they're no longer flatfooted.
			String curStatus = (String)d_Status.get(pcRow);
			if (curStatus.compareTo(statusFlatfooted) == 0)
			{
				d_Status.set(pcRow, statusNormal);
			}
			else if (curStatus.compareTo(statusBleeding) == 0)
			{
				int stabilizeRoll = RollingMethods.roll(1, 10, 0);
				//Globals.debugPrint("STABILIZE: " + stabilizeRoll);
				if (stabilizeRoll == 1)
					d_Status.set(pcRow, statusStabilized);
				else
				{
					Integer oldValue = (Integer)d_HitPoints.get(pcRow);
					d_HitPoints.set(pcRow, new Integer(oldValue.intValue() - 1));
					// Don't have this be >= -10 because there should be no case where
					// this code is called when oldValue >= -10 - if it does happen, it's
					// a bug, and I'd rather have something break and have to fix it.
					if (oldValue.intValue() - 1 == -10)
						d_Status.set(pcRow, statusDead);
				}
			}

			// Sets the previous person to the next round.
			d_Rounds.set(pcRow, new Integer(currentRound + 1));

			// Select the next person.
			int selected = getNextPCWithAction();

			// TODO: This is where we error out if at the end of the round there's still someone delayed.
			if (selected < 0)
			{
				for (int count = 0; count < d_Status.size(); count++)
				{
					if (((String)d_Status.get(count)).compareTo(statusDelayed) == 0)
					{
						d_CombatTable.clearSelection();
						currentPCRow = -1;
						JOptionPane.showMessageDialog(null, "You cannot go to the next round when there are still characters that have delayed - they must either undelay and take an action, or refocus.", "PCGen", JOptionPane.ERROR_MESSAGE);
						return;
					}
				}

				selected = 0;
				currentRound++;
			}

			int actualSelected = getActualPCRow(selected);

			// If we get back to a readied person (they readied an action, but didn't go), set their status back to "-")
			if (((String)d_Status.get(actualSelected)).compareTo(statusReadied) == 0)
				d_Status.set(actualSelected, statusNormal);

			// Loop through all the statuses, and if any are delayed, drop them down to the new current PC.
			Integer newInit = (Integer)d_InitRolls.get(actualSelected);
			for (int count = 0; count < d_Status.size(); count++)
				if (((String)d_Status.get(count)).compareTo(statusDelayed) == 0)
					d_InitRolls.set(count, newInit);

			//Globals.debugPrint("selected: " + selected);
			//Globals.debugPrint("actual: " + actualSelected);
			sorter.sortByColumn(3, false);

			// Make sure we have the acutal PC - the reordering for delays might screw this up.
			selected = getNextPCWithAction();
			actualSelected = getActualPCRow(selected);
			// Set the newly found PC to the current person, and select them.
			currentPCRow = selected;
			// Okay, they're going - they're no longer flatfooted.
			curStatus = (String)d_Status.get(actualSelected);
			if (curStatus.compareTo(statusFlatfooted) == 0)
			{
				d_Status.set(actualSelected, statusNormal);
			}
			//Globals.debugPrint("status: " + curStatus);
			d_CombatTable.clearSelection();
			d_CombatTable.addRowSelectionInterval(selected, selected);
			refreshTable();
		}
	}

	private class applyInitOpListener implements ActionListener
	{
		public void actionPerformed(ActionEvent evt)
		{
			int selectedRow = d_CombatTable.getSelectedRow();
			int row = getActualPCRow(selectedRow);

			// This is a flag used to determine if we should advance to the next character or not (the PC is done).
			// true means the action was delay, ready, or refocus, so we should go to the next PC.
			boolean gotoNextFlag = true;

			String curStatus = (String)d_Status.get(row);

			int operationsIndex = d_initOperations.getSelectedIndex();

			// TODO: it should error if someone tries to delay passed the end of the round? Same for readied? Find out the rules, work this out.
			switch (operationsIndex)
			{
				case 0:
					if (curStatus.compareTo(statusDelayed) == 0)
					{
						d_Status.set(row, statusNormal);
						d_OldInitRolls.set(row, new Integer(0));
						gotoNextFlag = false;
					}
					else
					{
						d_Status.set(row, statusDelayed);
						d_OldInitRolls.set(row, d_InitRolls.get(row));
						d_InitRolls.set(row, d_InitRolls.get(getActualPCRow(getNextPCWithAction())));
					}
					break;
				case 1:
					if (curStatus.compareTo(statusReadied) == 0)
					{
						d_Status.set(row, statusNormal);
						gotoNextFlag = false;
					}
					else
						d_Status.set(row, statusReadied);
					break;
				case 2:
					final PlayerCharacter oldPC = Globals.getCurrentPC();
					final PlayerCharacter curChar = (PlayerCharacter)Globals.getPCList().get(row);
					Globals.setCurrentPC(curChar);
					d_InitRolls.set(row, new Integer(curChar.initiativeMod() + 20));
					Globals.setCurrentPC(oldPC);
					break;
				default:
					Globals.errorPrint("In ToolsCombat.applyInitOpListener the operationsIndex " + operationsIndex + " is not handled.");
					break;
			}

			if (gotoNextFlag)
			{
				// If they refocused, go to the next round. Otherwise, they stay on the same round.
				if (operationsIndex == 2)
					d_Rounds.set(row, new Integer(currentRound + 1));
				selectedRow = getNextPCWithAction();
			}

			// TODO: This should have the same code that does something when someone tries to delay/ready past the end of the round.
			if (selectedRow < 0)
			{
				for (int count = 0; count < d_Status.size(); count++)
				{
					if (((String)d_Status.get(count)).compareTo(statusDelayed) == 0)
					{
						d_CombatTable.clearSelection();
						currentPCRow = -1;
						JOptionPane.showMessageDialog(null, "You cannot go to the next round when there are still characters that have delayed - they must either undelay and take an action, or refocus.", "PCGen", JOptionPane.ERROR_MESSAGE);
						return;
					}
				}

				selectedRow = 0;
				currentRound++;

			}

			currentPCRow = selectedRow;
			// Okay, they're going - they're no longer flatfooted.
			curStatus = (String)d_Status.get(getActualPCRow(selectedRow));
			if (curStatus.compareTo(statusFlatfooted) == 0)
			{
				d_Status.set(getActualPCRow(selectedRow), statusNormal);
			}
			sorter.sortByColumn(3, false);
			d_CombatTable.clearSelection();
			d_CombatTable.addRowSelectionInterval(selectedRow, selectedRow);
			refreshTable();
		}
	}

// Yanked for WotC compliance
//	private class RollListener implements ActionListener
//	{
//		public void actionPerformed(ActionEvent evt)
//		{
//			int numDice = d_Dice.getSelectedIndex() + 1;
//			int numSides = Integer.valueOf((String)d_Sides.getSelectedItem()).intValue();
//			int multMod = (d_SimpOp.getSelectedIndex() == 0) ? 1 : -1;
//			int addition = (d_Mod.getText().length() > 0) ? multMod * Integer.valueOf(d_Mod.getText()).intValue() : 0;
//			Integer result = new Integer(RollingMethods.roll(numDice, numSides, addition));
//
//			d_Results.setText(result.toString());
//			d_Damage.setText(result.toString());
//		}
//	}

	// Used to figure out what the actual row is for all the various operations.
	// Needed because when people sort the table, the order of Globals.getPCList() doesnt' change.
	// note that this will *not* work with characters that have duplicate names. (the "first" character will always win.)
	private int getActualPCRow(int inRow)
	{
		String charName = (String)d_CombatTable.getValueAt(inRow, 0);
		for (int outRow = 0; outRow < Globals.getPCList().size(); outRow++)
		{
			PlayerCharacter tempChar = (PlayerCharacter)Globals.getPCList().get(outRow);
			if (tempChar.getName().compareTo(charName) == 0)
				return outRow;
		}
		//Globals.debugPrint("NO MATCH");
		return 0;
	}

	// Used to find the next PC with an action (that's not delayed/readied or has any other status things going on.)
	// Returns the row if it finds one in this round, -1 otherwise (there's no one left to act that round.)
	// Returns the SELECTED, not actual, row.
	private int getNextPCWithAction()
	{
		sorter.sortByColumn(3, false);

		int curRound = currentRound + 1;
		int selected = -1;
		for (int count = 0; count < Globals.getPCList().size(); count++)
		{
			int actualCount = getActualPCRow(count);
			Integer countCurRound = (Integer)d_Rounds.get(actualCount);
			String countStatus = (String)d_Status.get(actualCount);
			if ((curRound > countCurRound.intValue()
			  && countStatus.compareTo(statusDelayed) != 0
			  && countStatus.compareTo(statusReadied) != 0) ||
			  (curRound > countCurRound.intValue() + 1 && countStatus.compareTo(statusReadied) == 0))
			{
				selected = count;
				curRound--;
			}
		}
		return selected;
	}

//////////////////////////////////
// Lots of redundant code, because I'm not good at java.
//////////////////////////////////

	private class CombatTableSorter extends TableMap
	{
		private int indexes[] = null;
		private int sortingColumn = -1;
		private boolean ascending = true;

		public CombatTableSorter()
		{
			indexes = new int[0]; // for consistency
		}

		public CombatTableSorter(TableModel model)
		{
			setModel(model);
		}

		public void setModel(TableModel model)
		{
			super.setModel(model);
			reallocateIndexes();
		}

		public final int compare(int row1, int row2)
		{
			final Class type = model.getColumnClass(sortingColumn);
			final int lessThan = (ascending ? -1 : 1);
			final int greaterThan = (ascending ? 1 : -1);

			// Check for nulls.

			final Object o1 = getValueAt(row1, sortingColumn);
			final Object o2 = getValueAt(row2, sortingColumn);

			// Custom initative sorter
			if (sortingColumn == 3)
			{
				final int i1 = ((Integer)o1).intValue();
				final int i2 = ((Integer)o2).intValue();
				if (i1 < i2) return lessThan;
				if (i1 > i2) return greaterThan;
				if (i1 == i2)
				{
					final int actualrow1 = getActualPCRow(row1);
					final int actualrow2 = getActualPCRow(row2);

					String status1 = (String)d_Status.get(actualrow1);
					String status2 = (String)d_Status.get(actualrow2);

					//Globals.debugPrint("TempChar1 status: " + status1);
					//Globals.debugPrint("TempChar2 status: " + status2);

					// 1 is delayed, 2 is not delayed. 2 goes first.
					if (status1.compareTo(statusDelayed) == 0 &&
					  status2.compareTo(statusDelayed) != 0)
						return lessThan;

					// 1 is not delayed, 2 is delayed. 1 goes first.
					if (status1.compareTo(statusDelayed) != 0 &&
					  status2.compareTo(statusDelayed) == 0)
						return greaterThan;

					final int oldInit1 = ((Integer)d_OldInitRolls.get(actualrow1)).intValue();
					final int oldInit2 = ((Integer)d_OldInitRolls.get(actualrow2)).intValue();

					//Globals.debugPrint("TempChar1 oldInit: " + oldInit1);
					//Globals.debugPrint("TempChar2 oldInit: " + oldInit2);

					// Compare old inits to make sure multiple people who delayed come out the same.
					if (oldInit1 < oldInit2) return lessThan;
					if (oldInit1 > oldInit2) return greaterThan;

					// Otherwise, fall back on ye old dex mod.
					// TODO: make sure to handle situations where people have the same init/dex mod - they
					// both should have a chance of being first, but once it's resolved the same person should
					// go first until one of the two of them changes their action.

					final PlayerCharacter oldPC = Globals.getCurrentPC();
					final PlayerCharacter tempChar1 = (PlayerCharacter)Globals.getPCList().get(actualrow1);
					Globals.setCurrentPC(tempChar1);
					int stat1 = tempChar1.getVariableValue("INITCOMP", "").intValue();
					Globals.setCurrentPC((PlayerCharacter)Globals.getPCList().get(actualrow2));
					int stat2 = Globals.getCurrentPC().getVariableValue("INITCOMP", "").intValue();
					Globals.setCurrentPC(oldPC);

					if (stat1 < stat2)
						return lessThan;
					if (stat1 > stat2)
						return greaterThan;
				}
				return 0;
			}

			// If both values are null, return 0.
			if (o1 == o2) return 0;
			if (o1 == null) return lessThan;
			if (o2 == null) return greaterThan;

			if (type == java.lang.Integer.class)
			{
				final int i1 = ((Integer)o1).intValue();
				final int i2 = ((Integer)o2).intValue();
				if (i1 < i2) return lessThan;
				if (i1 > i2) return greaterThan;
				return 0;
			}
			else if (type.getSuperclass() == java.lang.Number.class)
			{
				final double d1 = ((Number)o1).doubleValue();
				final double d2 = ((Number)o2).doubleValue();
				if (d1 < d2) return lessThan;
				if (d1 > d2) return greaterThan;
				return 0;
			}
			else if (type == Date.class)
			{
				final long n1 = ((Date)o1).getTime();
				final long n2 = ((Date)o2).getTime();
				if (n1 < n2) return lessThan;
				if (n1 > n2) return greaterThan;
				return 0;
			}
			else if (type == String.class)
			{
				final String s1 = (String)o1;
				final String s2 = (String)o2;
				if (ascending)
					return s1.compareToIgnoreCase(s2);
				else
					return s2.compareToIgnoreCase(s1);
			}
			else if (type == Boolean.class)
			{
				final boolean b1 = ((Boolean)o1).booleanValue();
				final boolean b2 = ((Boolean)o2).booleanValue();
				if (b1 == b2) return 0;
				if (b1) return greaterThan;
				return lessThan;
			}
			else
			{
				final String s1 = o1.toString();
				final String s2 = o2.toString();
				if (ascending)
					return s1.compareToIgnoreCase(s2);
				else
					return s2.compareToIgnoreCase(s1);
			}
		}

		public void reallocateIndexes()
		{
			final int rowCount = model.getRowCount();
			if (indexes == null || rowCount != indexes.length)
			{
				// only reallocate if necessary
				indexes = new int[rowCount];
				for (int row = 0; row < rowCount; ++row)
				{
					indexes[row] = row;
				}
			}
		}

		public void tableChanged(TableModelEvent e)
		{
			reallocateIndexes();
			if (sortingColumn >= 0)
			{
				sortByColumn(sortingColumn, ascending);
			}
			super.tableChanged(e);
		}

		public final void checkModel()
		{
			if (indexes.length != model.getRowCount())
			{
				Globals.errorPrint("Sorter not informed of a change in model.");
			}
		}

		public void sort()
		{
			checkModel();
			int[] workspace = new int[indexes.length];
			mergeSort(indexes, workspace, 0, indexes.length);
		}

		private final void mergeSort(int[] indices,
		  int[] workspace,
		  final int start,
		  final int end)
		{
			final int numElem = end - start;
			if (numElem > 1)
			{
				final int mid = (start + end) / 2;
				mergeSort(indices, workspace, start, mid);
				mergeSort(indices, workspace, mid, end);
				// join two sorted lists
				int i = start;
				int j = start;
				int k = mid;
				while ((j < mid) && (k < end))
				{
					if (compare(j, k) <= 0)
						workspace[i++] = indices[j++];
					else
						workspace[i++] = indices[k++];
				}
				if (j < mid)
				{
					final int numLeft = mid - j;
					System.arraycopy(indices, j, indices, end - numLeft, numLeft);
					System.arraycopy(workspace, start, indices, start, numElem - numLeft);
				}
				else
				{
					final int numLeft = end - k;
					System.arraycopy(workspace, start, indices, start, numElem - numLeft);
				}
			}
		}


		// The mapping only affects the contents of the data rows.
		// Pass all requests to these rows through the mapping array: "indexes".

		public Object getValueAt(int aRow, int aColumn)
		{
			checkModel();
			return model.getValueAt(indexes[aRow], aColumn);
		}

		public void setValueAt(Object aValue, int aRow, int aColumn)
		{
			checkModel();
			model.setValueAt(aValue, indexes[aRow], aColumn);
		}

		public void sortByColumn(int column)
		{
			sortByColumn(column, true);
		}

		public void sortByColumn(int column, boolean ascending)
		{
			this.ascending = ascending;
			sortingColumn = column;
			sort();
			super.tableChanged(new TableModelEvent(this));
		}

		public int getRowTranslated(int row)
		{
			return indexes[row];
		}

		/** There is no-where else to put this.
		 * Add a mouse listener to the Table to trigger a table sort
		 * when a column heading is clicked in the JTable.
		 */
		public void addMouseListenerToHeaderInTable(JTable table)
		{
			final CombatTableSorter sorter = this;
			final JTable tableView = table;
			tableView.setColumnSelectionAllowed(false);
			MouseAdapter listMouseListener = new MouseAdapter()
			{
				public void mouseClicked(MouseEvent e)
				{
					final TableColumnModel columnModel = tableView.getColumnModel();
					final int viewColumn = columnModel.getColumnIndexAtX(e.getX());
					final int column = tableView.convertColumnIndexToModel(viewColumn);
					if (e.getClickCount() == 1 && column != -1)
					{
						int shiftPressed = e.getModifiers() & InputEvent.SHIFT_MASK;
						if (shiftPressed == 0)
						{
							if (column == sortingColumn)
								sorter.sortByColumn(column, !ascending);
							else
								sorter.sortByColumn(column, true);
						}
						else
						{
							sorter.sortByColumn(column, false);
						}
					}
				}
			};
			JTableHeader th = tableView.getTableHeader();
			th.addMouseListener(listMouseListener);
		}
	}
}
