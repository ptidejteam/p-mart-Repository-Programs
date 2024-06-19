/*
 * ToolsQStats.java
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
import java.util.Iterator;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.TableModelEvent;
import javax.swing.table.AbstractTableModel;
import pcgen.core.Constants;
import pcgen.core.Globals;
import pcgen.core.PCClass;
import pcgen.core.PlayerCharacter;
import pcgen.core.Skill;

/**
 * @author  Jason Buchanan <lonejedi70@hotmail.com>
 * @version $Revision: 1.1 $
 */

public class ToolsQStats extends JPanel
{
	JTableEx d_StatTable;

	public ToolsQStats()
	{
		initComponents();
	}

	public void refreshTable()
	{
		setPreferredWidths();
		d_StatTable.updateUI();
	}

	private void setPreferredWidths()
	{
		d_StatTable.getColumnModel().getColumn(0).setPreferredWidth(150);  //name
		d_StatTable.getColumnModel().getColumn(1).setPreferredWidth(125);  //class
		d_StatTable.getColumnModel().getColumn(2).setPreferredWidth(75);   //race
		d_StatTable.getColumnModel().getColumn(3).setPreferredWidth(50);   //size
		d_StatTable.getColumnModel().getColumn(4).setPreferredWidth(50);   //alignment or WP
		d_StatTable.getColumnModel().getColumn(5).setPreferredWidth(50);   //HP or VP
		d_StatTable.getColumnModel().getColumn(6).setPreferredWidth(50);   //AC or Defense
		d_StatTable.getColumnModel().getColumn(7).setPreferredWidth(75);   //XP
		d_StatTable.getColumnModel().getColumn(8).setPreferredWidth(75);   //Fort Save
		d_StatTable.getColumnModel().getColumn(9).setPreferredWidth(75);   //Ref Save
		d_StatTable.getColumnModel().getColumn(10).setPreferredWidth(75);  //Will Save
		d_StatTable.getColumnModel().getColumn(11).setPreferredWidth(75);  //Initiative
		d_StatTable.getColumnModel().getColumn(12).setPreferredWidth(75);  //Move
		d_StatTable.getColumnModel().getColumn(13).setPreferredWidth(75);  //Listen
		d_StatTable.getColumnModel().getColumn(14).setPreferredWidth(75);  //Spot
		d_StatTable.getColumnModel().getColumn(15).setPreferredWidth(75);  //Search

		for (int count = 0; count < 16; count++)
		{
			d_StatTable.getColumnModel().getColumn(count).setHeaderValue(d_StatTable.getColumnName(count));
		}
	}

	private void initComponents()
	{
		this.setLayout(new BorderLayout());
		TableSorter sorter = new TableSorter(new QuickStatsTableModel());
		d_StatTable = new JTableEx(sorter);
		sorter.addMouseListenerToHeaderInTable(d_StatTable);
		d_StatTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		setPreferredWidths();
		JScrollPane d_StatPane = new JScrollPane(d_StatTable);
		d_StatPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		d_StatPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		d_StatPane.setDoubleBuffered(true);
		d_StatPane.setPreferredSize(new Dimension(446, 200));
		this.add(d_StatPane, BorderLayout.CENTER);
	}

	public class QuickStatsTableModel extends AbstractTableModel
	{
		private final String[] dndColNameList = {"Name", "Class(es)", "Race", "Size", "Align", "HP", "AC", "XP", "Fort Save", "Ref Save", "Will Save", "Initiative", "Move", "Listen", "Spot", "Search"};
		private final String[] wheelColNameList = {"Name", "Class(es)", "Race", "Size", "", "HP", "Defense", "XP", "Fort Save", "Ref Save", "Will Save", "Initiative", "Move", "Listen", "Spot", "Search"};
		private final String[] swColNameList = {"Name", "Class(es)", "Race", "Size", "WP", "VP", "Defense", "XP", "Fort Save", "Ref Save", "Will Save", "Initiative", "Move", "Listen", "Spot", "Search"};
		private final String[] sidewColNameList = {"Name", "Class(es)", "Race", "Size", "Body", "Vigor", "AC", "XP", "Fort Save", "Ref Save", "Will Save", "Initiative", "Move", "Listen", "Spot", "Search"};
		private int lastRowCount = 0;

		public int getRowCount()
		{
			final int pcListSize = pcgen.core.Globals.getPCList().size();
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
				return swColNameList.length;
			else if (Globals.isSidewinderMode())
				return sidewColNameList.length;
			else if (Globals.isWheelMode())
				return wheelColNameList.length;
			return dndColNameList.length;
		}

		public Class getColumnClass(int columnIndex)
		{
			if (columnIndex < 4 || ((columnIndex == 4) && Globals.isDndMode()) || columnIndex == 12)
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
			if (Globals.isStarWarsMode() || Globals.isSpycraftMode())
				return swColNameList[columnIndex];
			else if (Globals.isSidewinderMode())
				return sidewColNameList[columnIndex];
			else if (Globals.isWheelMode())
				return wheelColNameList[columnIndex];
			return dndColNameList[columnIndex];
		}

		public Object getValueAt(int row, int column)
		{
			final PlayerCharacter curChar = (PlayerCharacter)Globals.getPCList().get(row);
			final PlayerCharacter oldChar = Globals.getCurrentPC(); //keep char of current char in case it gets changed during Skill lookup
			Globals.setCurrentPC(curChar);
			Iterator skillIter;
			boolean isIncomplete = (curChar.hitPoints() < 1);  //if the character hasn't acquired HP, then it isn't complete enough for most stats
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
				case 3: //size
					if (isIncomplete)
					{
						return "";
					}  //if the character has no levels then don't bother getting data
					return curChar.getSize();
				case 4: //alignment or WP
					if (isIncomplete)
					{
						if (Globals.isStarWarsMode() || Globals.isSidewinderMode() || Globals.isSpycraftMode())
						{
							return new Integer(0);
						}
						return "";
					}  //if the character has no levels then don't bother getting data
					if (Globals.isStarWarsMode() || Globals.isSidewinderMode() || Globals.isSpycraftMode())
					{
						return curChar.woundPoints();
					}
					if (Globals.isWheelMode()) return "";
					return Constants.s_ALIGNSHORT[curChar.getAlignment()];
				case 5: //HP or VP
					if (isIncomplete)
					{
						return new Integer(0);
					}  //if the character has no levels then don't bother getting data
					return new Integer(curChar.hitPoints());
				case 6: //AC or Defense
					if (isIncomplete)
					{
						return new Integer(0);
					}  //if the character has no levels then don't bother getting data
					if (Globals.isStarWarsMode() || Globals.isSidewinderMode() || Globals.isWheelMode() || Globals.isSpycraftMode())
					{
						return curChar.defense();
					}
					return new Integer(curChar.totalAC());
				case 7: //XP
					if (isIncomplete)
					{
						return new Integer(0);
					}  //if the character has no levels then don't bother getting data
					return new Integer(curChar.getXP());
				case 8: //Fort Save
					if (isIncomplete)
					{
						return new Integer(0);
					}  //if the character has no levels then don't bother getting data
					return new Integer(curChar.getBonus(1, true) + curChar.calcStatMod(Constants.CONSTITUTION));
				case 9: //Ref Save
					if (isIncomplete)
					{
						return new Integer(0);
					}  //if the character has no levels then don't bother getting data
					return new Integer(curChar.getBonus(2, true) + curChar.calcStatMod(Constants.DEXTERITY));
				case 10: //Will Save
					if (isIncomplete)
					{
						return new Integer(0);
					}  //if the character has no levels then don't bother getting data
					return new Integer(curChar.getBonus(3, true) + curChar.calcStatMod(Constants.WISDOM));
				case 11: //Initiative
					if (isIncomplete)
					{
						return new Integer(0);
					}  //if the character has no levels then don't bother getting data
					return new Integer(curChar.initiativeMod());
				case 12: //Move
					if (isIncomplete)
					{
						return "";
					}  //if the character has no levels then don't bother getting data
					StringBuffer movelabel = new StringBuffer().append(curChar.getRace().getMovementType(0)).append(" ").append(curChar.movement(0)).append("'");
					for (int i = 1; i < curChar.getRace().getMovements().length; i++)
					{
						movelabel.append(", ").append(curChar.getRace().getMovementType(i)).append(" ").append(curChar.movement(i)).append("'");
					}
					return movelabel;
				case 13: //Listen
					if (isIncomplete)
					{
						return new Integer(0);
					}  //if the character has no levels then don't bother getting data
					skillIter = curChar.getSkillList().iterator();
					while (skillIter.hasNext())
					{
						Skill aSkill = (Skill)skillIter.next();
						if (aSkill.qualifiedName().equals("Listen"))
						{
							Globals.setCurrentPC(curChar);  //in order for skill bonuses to calculate correctly we have to point currentPC to this char
							Integer skillVal = new Integer(aSkill.modifier().intValue() + aSkill.getTotalRank().intValue());
							Globals.setCurrentPC(oldChar);  //reset the currentPC
							return skillVal;
						}
					}
					return new Integer(0);
				case 14: //Spot
					if (isIncomplete)
					{
						return new Integer(0);
					}  //if the character has no levels then don't bother getting data
					skillIter = curChar.getSkillList().iterator();
					while (skillIter.hasNext())
					{
						Skill aSkill = (Skill)skillIter.next();
						if (aSkill.qualifiedName().equals("Spot"))
						{
							Globals.setCurrentPC(curChar);  //in order for skill bonuses to calculate correctly we have to point currentPC to this char
							Integer skillVal = new Integer(aSkill.modifier().intValue() + aSkill.getTotalRank().intValue());
							Globals.setCurrentPC(oldChar);  //reset the currentPC
							return skillVal;
						}
					}
					return new Integer(0);
				case 15: //Search
					if (isIncomplete)
					{
						return new Integer(0);
					}  //if the character has no levels then don't bother getting data
					skillIter = curChar.getSkillList().iterator();
					while (skillIter.hasNext())
					{
						Skill aSkill = (Skill)skillIter.next();
						if (aSkill.qualifiedName().equals("Search"))
						{
							Globals.setCurrentPC(curChar);  //in order for skill bonuses to calculate correctly we have to point currentPC to this char
							Integer skillVal = new Integer(aSkill.modifier().intValue() + aSkill.getTotalRank().intValue());
							Globals.setCurrentPC(oldChar);  //reset the currentPC
							return skillVal;
						}
					}
					return new Integer(0);
				default:
					Globals.errorPrint("In ToolsQStats.QuickStatsTableModel.getValueAt the column " + column + " is not handled.");
					break;
			}
			return null;
		}
	}
}
