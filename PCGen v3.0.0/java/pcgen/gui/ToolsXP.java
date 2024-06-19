/*
 * ToolsXP.java
 * Copyright 2001 (C) Eric Statz <statz@null.net>
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
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.TableModelEvent;
import javax.swing.table.AbstractTableModel;
import pcgen.core.Globals;
import pcgen.core.PCClass;
import pcgen.core.PlayerCharacter;


/**
 * @author  Eric Statz <statz@null.net>
 * @version $Revision: 1.1 $
 */

public class ToolsXP extends JPanel
{
	//private final String[] CRListText = {"1/10", "1/8", "1/6", "1/4", "1/3", "1/2",
	private final String[] CRListText = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10",
																			 "11", "12", "13", "14", "15", "16", "17", "18", "19", "20",
																			 "21", "22", "23", "24", "25", "26", "27", "28", "29", "30"};
	private JTableEx d_XPTable;
	private JButton d_SelectAll = new JButton("Select All");
	private JLabel d_Reward = new JLabel("Reward");
	private JTextField d_Experience = new JTextField(10);
	private JLabel d_Prep = new JLabel("XP to selected PCs");
	private JButton d_Grant = new JButton("Grant");
	private JCheckBox d_SplitXP = new JCheckBox("Split XP");
	private JLabel d_RewardCR = new JLabel("Reward CR");
	private JComboBox d_CRList = new JComboBox(CRListText);
	private JLabel d_Encounter = new JLabel("encounter to selected PCs");
	private JButton d_GrantCR = new JButton("Grant");
	private TableSorter sorter = null;

	public ToolsXP()
	{
		initComponents();
	}

	public void refreshTable()
	{
		setPreferredWidths();
		d_XPTable.updateUI();
	}

	private void setPreferredWidths()
	{
		d_XPTable.getColumnModel().getColumn(0).setPreferredWidth(140);  //name
		d_XPTable.getColumnModel().getColumn(1).setPreferredWidth(125);  //class
		d_XPTable.getColumnModel().getColumn(2).setPreferredWidth(75);   //race
		d_XPTable.getColumnModel().getColumn(3).setPreferredWidth(85);   //Current Experience
		d_XPTable.getColumnModel().getColumn(4).setPreferredWidth(95);   //Experience for next level
		d_XPTable.getColumnModel().getColumn(5).setPreferredWidth(70);   //Multiclass experience factor
		d_XPTable.getColumnModel().getColumn(6).setPreferredWidth(130);   //Multiclass experience factor

		for (int count = 0; count < 6; count++)
		{
			d_XPTable.getColumnModel().getColumn(count).setHeaderValue(d_XPTable.getColumnName(count));
		}
	}

	private void initComponents()
	{
		this.setLayout(new BorderLayout());
		sorter = new TableSorter(new XPTableModel());
		d_XPTable = new JTableEx(sorter);
		sorter.addMouseListenerToHeaderInTable(d_XPTable);
		d_XPTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		setPreferredWidths();
		JScrollPane d_StatPane = new JScrollPane(d_XPTable);
		d_StatPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		d_StatPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		d_StatPane.setDoubleBuffered(true);
		d_StatPane.setPreferredSize(new Dimension(446, 200));

		JPanel d_Interact = new JPanel(new BorderLayout());

		d_SelectAll.addActionListener(new SelectAllListener());

		d_Grant.addActionListener(new GrantListener());

		d_SplitXP.setSelected(true);

		d_GrantCR.addActionListener(new GrantCRListener());

		JPanel d_Stats = new JPanel(new FlowLayout());
		d_Stats.add(Box.createHorizontalStrut(20));
		d_Stats.add(d_SelectAll);
		d_Stats.add(d_Reward);
		d_Stats.add(d_Experience);
		d_Stats.add(d_Prep);
		d_Stats.add(d_Grant);
		d_Interact.add(d_Stats, BorderLayout.NORTH);

		JPanel d_CR = new JPanel(new FlowLayout());
		d_CR.add(d_SplitXP);
		d_CR.add(d_RewardCR);
		d_CR.add(d_CRList);
		d_CR.add(d_Encounter);
		d_CR.add(d_GrantCR);
		d_Interact.add(d_CR, BorderLayout.SOUTH);

		this.add(d_StatPane, BorderLayout.CENTER);
		this.add(d_Interact, BorderLayout.SOUTH);
	}

	public class XPTableModel extends AbstractTableModel
	{
		private final String[] ColNameList = {"Name", "Class(es)", "Race", "XP (Current)", "XP (Next Level)", "XP Factor", "Favored Class"};
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
			return ColNameList.length;
		}

		public Class getColumnClass(int columnIndex)
		{
			if (columnIndex < 3 || columnIndex == 5)
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
			return ColNameList[columnIndex];
		}

		public Object getValueAt(int row, int column)
		{
			PlayerCharacter curChar = (PlayerCharacter)Globals.getPCList().get(row);
			boolean isIncomplete = curChar.getClassList().isEmpty();  //if the character has no levels then it isn't complete enough for most stats
			switch (column)
			{
				case 0: //name
					return curChar.getName();
				case 1: //class(es)
					StringBuffer classes = new StringBuffer();
					if (isIncomplete)
					{
						return "";
					}  //if the character has no levels then don't bother getting data
					Iterator classIter = curChar.getClassList().iterator();
					while (classIter.hasNext())
					{
						PCClass theClass = (PCClass)classIter.next();
						if (!theClass.getName().startsWith("Domain"))
						{
							classes.append(theClass.classLevelString()).append("/");
						}
					}
					if (classes.substring(classes.length()).equals("/"))
					{
						return classes.substring(0, classes.length() - 1);
					}
					else
					{
						return classes.toString();
					}
				case 2: //race
					if (isIncomplete)
					{
						return "";
					}  //if the character has no levels then don't bother getting data
					return curChar.getRace().getName();
				case 3: //current experience
					if (isIncomplete)
					{
						return new Integer(0);
					}  //if the character has no levels then don't bother getting data
					return new Integer(curChar.getXP());
				case 4: //Next Level XP
					if (isIncomplete)
					{
						return new Integer(0);
					}
					return new Integer(curChar.minXPForNextECL());
				case 5: //XP Factor
					if (isIncomplete)
					{
						return "";
					}

/*					Float aFloat = new Float(curChar.multiclassXPMultiplier().floatValue() * 100.0);
					int aInt = aFloat.intValue();
					return "  " + aInt + "%";*/

					StringBuffer xpFactor = new StringBuffer(5).append(" ");
					xpFactor.append((int)(curChar.multiclassXPMultiplier() * 100.0));
					xpFactor.append("%");
					return xpFactor.toString();
				case 6: //Favored Class
					return " " + curChar.getFavoredClasses() + " ";
				default:
					Globals.errorPrint("In ToolsXP.XPTableModel.getValueAt the column " + column + " is not handled.");
					break;
			}
			return null;
		}
	}

	private class GrantListener implements ActionListener
	{
		public void actionPerformed(ActionEvent evt)
		{
			int i = d_XPTable.getSelectedRowCount();
			int[] rows = d_XPTable.getSelectedRows();
			while (i-- > 0)
			{
				if (d_Experience.getText().length() > 0)
				{
					int value = Integer.valueOf(d_Experience.getText()).intValue();

					if (d_SplitXP.isSelected())
					{
						value = (int)(value / d_XPTable.getSelectedRowCount());
					}

					final int iSelected = sorter.getRowTranslated(rows[i]);
					if (iSelected >= 0)
					{
						PlayerCharacter tempChar = (PlayerCharacter)Globals.getPCList().get(iSelected);
						CharacterInfo ci = Globals.getRootFrame().getCharacterPane();
						ci.infoClasses().setNeedsUpdate(true);
						tempChar.setDirty(true);
						Float aFloat = new Float(value * tempChar.multiclassXPMultiplier());
						tempChar.setXP(tempChar.getXP() + aFloat.intValue());
					}
				}
			}
			refreshTable();
		}
	}

	private class GrantCRListener implements ActionListener
	{
		public void actionPerformed(ActionEvent evt)
		{
			int i = d_XPTable.getSelectedRowCount();
			int[] rows = d_XPTable.getSelectedRows();
			//float selection = Float.valueOf((String)d_CRList.getSelectedItem()).floatValue();
			StringBuffer lowpclist = new StringBuffer();
			StringBuffer highpclist = new StringBuffer();
			while (i-- > 0)
			{
				int cr = Integer.valueOf((String)d_CRList.getSelectedItem()).intValue();

				final int iSelected = sorter.getRowTranslated(rows[i]);
				if (iSelected >= 0)
				{
					PlayerCharacter tempChar = (PlayerCharacter)Globals.getPCList().get(iSelected);
					// Should this be totalLevels + monsterLevels? TotalLevels excludes it... Maybe not, but it's worth checking.
					int levels = tempChar.getTotalLevels();
					if (levels == 2 || levels == 3)
						levels = 3;

					if (levels - 8 >= cr)
					{
						if (highpclist.length() != 0)
							highpclist.append(", ");
						highpclist.append(tempChar.getName());
					}

					if (levels + 8 <= cr)
					{
						if (lowpclist.length() != 0)
							lowpclist.append(", ");
						lowpclist.append(tempChar.getName());
					}
				}
			}

			StringBuffer err_msg = new StringBuffer("");
			if (lowpclist.length() != 0 || highpclist.length() != 0)
			{
				if (lowpclist.length() != 0)
				{
					err_msg.append("The following PC(s) are too low a level (8 levels lower than the CR):\n");
					err_msg.append(lowpclist).append("\n\n");
				}
				if (highpclist.length() != 0)
				{
					err_msg.append("The following PC(s) are too high a level (8 levels higher than the CR):\n");
					err_msg.append(highpclist).append("\n\n");
				}
				if (err_msg.length() != 0)
				{
					err_msg.append("The XP Award table does not support this much difference in levels -\n");
					err_msg.append("you should make your own decision on what the PCs should get.\n");
					err_msg.append("The remaining PCs can be properly rewarded, however.\n");
					err_msg.append("To continue, please either select only them, or change the CR.\n");
				}
				JOptionPane.showMessageDialog(null, err_msg.toString(), "PCGen", JOptionPane.ERROR_MESSAGE);
			}

			i = d_XPTable.getSelectedRowCount();
			while (err_msg.length() == 0 && i-- > 0)
			{
				float xp_multiplier = 1;
				int cr = Integer.valueOf((String)d_CRList.getSelectedItem()).intValue();

				final int iSelected = sorter.getRowTranslated(rows[i]);
				if (iSelected >= 0)
				{
					PlayerCharacter tempChar = (PlayerCharacter)Globals.getPCList().get(iSelected);
					tempChar.setDirty(true);

					// Should this be totalLevels + monsterLevels? TotalLevels excludes it... Maybe not, but it's worth checking.
					int levels = tempChar.getTotalLevels();
					// Correct for the first row covering levels 1-3, and the array offset.
					if (levels == 1 || levels == 2)
						levels = 0;
					else
						levels -= 3;

					cr = cr - Globals.xPLevelOffset(levels) + 1;

					while (cr > 9)
					{
						cr -= 2;
						xp_multiplier = xp_multiplier * 2;
					}

					int value = Globals.xPLevelValue(levels, cr);

					if (d_SplitXP.isSelected())
					{
						value = (int)(value / d_XPTable.getSelectedRowCount());
					}

					Float aFloat = new Float(value * tempChar.multiclassXPMultiplier() * xp_multiplier);
					tempChar.setXP(tempChar.getXP() + aFloat.intValue());
				}
			}
			refreshTable();
		}
	}

	private class SelectAllListener implements ActionListener
	{
		public void actionPerformed(ActionEvent evt)
		{
			// TODO: Ask someone about this. One would presume I really want to access XPTableModel.GetRowCount
			d_XPTable.addRowSelectionInterval(0, d_XPTable.getRowCount() - 1);
		}
	}
}
