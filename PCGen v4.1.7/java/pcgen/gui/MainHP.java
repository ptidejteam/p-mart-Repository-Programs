/**
 * MainHP.java
 * Copyright 2002 (C) Greg Bingleman <byngl@hotmail.com>
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
 * @author  Greg Bingleman <byngl@hotmail.com>
 *
 * Current Ver: $Revision: 1.1 $
 * Last Editor: $Author: vauchers $
 * Last Edited: $Date: 2006/02/21 00:57:40 $
 *
 **/

package pcgen.gui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Iterator;
import javax.swing.AbstractListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import pcgen.core.Constants;
import pcgen.core.Globals;
import pcgen.core.PCClass;
import pcgen.core.PlayerCharacter;
import pcgen.core.Race;

/**
 * Title:        MainHP.java
 * Description:  New GUI implementation for modifying PC hitpoints
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author Greg Bingleman
 * @version $Revision: 1.1 $
 */

final class MainHP extends JPanel
{
	private JTableEx currentHpTable;
	private JScrollPane currentHpPane;
	protected PCHitPointsTableModel currentHpTableModel = new PCHitPointsTableModel();
	protected RendererEditor plusMinusRenderer = new RendererEditor();

	private GridBagLayout gridBagLayout = new GridBagLayout();
	private JPanel buttonPanel = new JPanel();
	private JButton okayButton = new JButton();

	public MainHP()
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

	//
	// Set the preferred size of the HP Pane to 1 more row than it contains (including header)
	//
	public void setPSize()
	{
		Dimension preferredSize = currentHpPane.getPreferredSize();
		preferredSize.height = (currentHpTable.getRowCount() + 2) * (currentHpTable.getRowHeight() + currentHpTable.getRowMargin());
		currentHpPane.setPreferredSize(preferredSize);
	}

	private void jbInit() throws Exception
	{
		currentHpPane = new JScrollPane();
		currentHpTable = new JTableEx();

		GridBagConstraints c;
		this.setLayout(gridBagLayout);

		//
		// Hit points per level
		//
		currentHpTable.setModel(currentHpTableModel);
		currentHpTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		currentHpTable.setDoubleBuffered(false);
		currentHpPane.setViewportView(currentHpTable);

		this.add(currentHpPane, new GridBagConstraints(0, 0, 1, 7, 1.0, 1.0, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));

		TableColumn col;
		col = currentHpTable.getColumnModel().getColumn(5);
		col.setCellRenderer(plusMinusRenderer);
		col.setMaxWidth(30);
		col.setWidth(30);
		col.setMinWidth(30);

		col = currentHpTable.getColumnModel().getColumn(6);
		col.setCellRenderer(plusMinusRenderer);
		col.setMaxWidth(30);
		col.setWidth(30);
		col.setMinWidth(30);
		currentHpTable.addMouseListener(new MouseAdapter()
		{
			public void mouseClicked(MouseEvent evt)
			{
				hpTableMouseClicked(evt);
			}
		});

		okayButton.setText("Okay");
		okayButton.setMnemonic(KeyEvent.VK_O);
		okayButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				((PCGen_Frame1) Globals.getRootFrame()).hpTotal_Changed();
				JFrame parentFrame = (JFrame) Utility.getParentNamed(getParent(), "pcgen.gui.HPFrame");
				if (parentFrame != null)
				{
					parentFrame.dispose();
				}
			}
		});

		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.anchor = GridBagConstraints.NORTH;
		buttonPanel.add(okayButton, c);

		this.add(buttonPanel, new GridBagConstraints(1, 6, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHEAST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));

	}

	private void hpTableMouseClicked(MouseEvent evt)
	{
		int iRow;
		int increment;

		switch (currentHpTable.columnAtPoint(evt.getPoint()))
		{
			case 5:	// "+"
				increment = 1;
				break;

			case 6:	// "-"
				increment = -1;
				break;

			default:
				return;
		}

		iRow = currentHpTable.getSelectedRow();
		incrementForRow(iRow, increment);
	}

	private void incrementForRow(int iRow, int increment)
	{
		int iMax;
		int iRoll;
		if (iRow >= currentHpTableModel.getRowCount() - 2)
		{
			return;
		}

		iMax = ((Integer) currentHpTableModel.getValueAt(iRow, 1)).intValue();		// # of sides on die
		iRoll = ((Integer) currentHpTableModel.getValueAt(iRow, 2)).intValue();		// current value

		if (iRoll > iMax)
		{
			iRoll = iMax;
			JOptionPane.showMessageDialog(null, "Setting roll to maximum (" + iMax + ')', Constants.s_APPNAME, JOptionPane.ERROR_MESSAGE);
			increment = 0;
		}

		if ((iRoll + increment) < 1)
		{
			JOptionPane.showMessageDialog(null, "Roll must be at least the minimum (1)", Constants.s_APPNAME, JOptionPane.ERROR_MESSAGE);
		}
		else if ((iRoll + increment) > iMax)
		{
			JOptionPane.showMessageDialog(null, "Roll cannot exceed the maximum (" + iMax + ')', Constants.s_APPNAME, JOptionPane.ERROR_MESSAGE);
		}
		else
		{
			iRoll += increment;

			PCClass aClass = null;
			Race aRace = null;
			final PlayerCharacter aPC = Globals.getCurrentPC();
			if (aPC != null)
			{
				aRace = aPC.getRace();
				if (aRace != null)
				{
					if (iRow < aRace.hitDice())
					{
						aRace.setHitPoint(iRow, new Integer(iRoll));
					}
					iRow -= aRace.hitDice();
				}

				if ((iRow >= 0) && (iRow < aPC.getLevelInfoSize()))
				{
					aClass = aPC.getClassKeyed(aPC.getLevelInfoClassKeyName(iRow));
					if (aClass != null)
					{
						final int lvl = aPC.getLevelInfoClassLevel(iRow) - 1;
						aClass.setHitPoint(lvl, new Integer(iRoll));
					}
				}
				aPC.setDirty(true);
				currentHpTableModel.fireTableDataChanged();
			}

		}
	}

	private class PCListModel extends AbstractListModel
	{
		public Object getElementAt(int index)
		{
			if (index < Globals.getPCList().size())
			{
				final PlayerCharacter aPC = (PlayerCharacter) Globals.getPCList().get(index);
				return aPC.getDisplayName();
			}
			else
			{
				return null;
			}
		}

		public int getSize()
		{
			return Globals.getPCList().size();
		}
	}

	/**
	 *
	 */
	final class PCHitPointsTableModel extends AbstractTableModel
	{
		public int getColumnCount()
		{
			return 7;
		}

		public Class getColumnClass(int columnIndex)
		{
			switch (columnIndex)
			{
				case 0:	// Name
				case 5:	// +
				case 6:	// -
					return String.class;

				case 1:	// Sides
				case 2:	// Roll
				case 3:	// Con Adj
				case 4:	// Total
					return Integer.class;
				default:
					break;
			}
			return null;
		}

		public int getRowCount()
		{
			int iRows = 2;
			final PlayerCharacter aPC = Globals.getCurrentPC();
			if (aPC != null)
			{
				if (aPC.getRace() != null)
				{
					iRows += aPC.getRace().hitDice();
				}
				if (aPC.getClassList() != null)
				{
					PCClass aClass = null;
					for (Iterator e = aPC.getClassList().iterator(); e.hasNext();)
					{
						aClass = (PCClass) e.next();
						iRows += aClass.getLevel().intValue();
					}
				}
			}
			return iRows;
		}

		public boolean isCellEditable(int rowIndex, int colIndex)
		{
			return (colIndex == 2);
		}

		public String getColumnName(int columnIndex)
		{
			switch (columnIndex)
			{
				case 0:
					return "Class";
				case 1:
					return "Sides";
				case 2:
					return "Roll";
				case 3:
					return "Con Adj";
				case 4:
					return "Total";
				case 5:
					return "+";
				case 6:
					return "-";
				default:
					break;
			}
			return "Out Of Bounds";
		}

		public Object getValueAt(int rowIndex, int columnIndex)
		{
			int iHp = 0;
			int iSides = 0;

			PCClass aClass = null;
			Race aRace = null;
			final PlayerCharacter aPC = Globals.getCurrentPC();
			if (aPC != null)
			{
				aRace = aPC.getRace();
				if (aRace != null)
				{
					if (rowIndex < aRace.hitDice())
					{
						iHp = aRace.getHitPointList(rowIndex).intValue();
						iSides = aRace.getHitDiceSize();
						//
						// Sanity check
						//
						if (iHp > iSides)
						{
							aRace.setHitPoint(rowIndex, new Integer(iSides));
							iHp = iSides;
						}
					}
					rowIndex -= aRace.hitDice();
				}

				if ((rowIndex >= 0) && (rowIndex < aPC.getLevelInfoSize()))
				{
					String classKeyName = aPC.getLevelInfoClassKeyName(rowIndex);
					aClass = aPC.getClassKeyed(classKeyName);
					if (aClass != null)
					{
						final int baseSides = aClass.getHitDie();
						final int lvl = aPC.getLevelInfoClassLevel(rowIndex);
						iHp = aClass.getHitPointList(lvl - 1).intValue();
						iSides = baseSides + (int) aClass.getBonusTo("HD", "MAX", lvl);
						//
						// Sanity check
						//
						if (iHp > iSides)
						{
							aClass.setHitPoint(lvl - 1, new Integer(iSides));
							iHp = iSides;
						}
					}
				}
				rowIndex -= aPC.getLevelInfoSize();

				//
				// Done all levels from all classes, then show HP from Feats (Toughness)
				//
				if (rowIndex == 0)
				{
					switch (columnIndex)
					{
						case 0:
							return "Feats";
						case 2:
						case 4:
							Integer iBonus = new Integer((int) aPC.getTotalBonusTo("HP", "CURRENTMAX", true));
							return iBonus;

						default:
							break;
					}
					return null;
				}
				else if (rowIndex == 1)
				{
					switch (columnIndex)
					{
						case 0:
							return "Total";

						case 4:
							int iRows = getRowCount() - 1;
							iHp = 0;
							for (int i = 0; i < iRows; i++)
							{
								iHp += ((Integer) getValueAt(i, 4)).intValue();
							}
							return new Integer(iHp);
						default:
							break;
					}
					return null;
				}

				switch (columnIndex)
				{
					case 0:		// Name
						if (aClass == null)
						{
							return aRace == null ? Constants.s_NONESELECTED : aRace.getName();
						}
						else
						{
							return aClass.getName();
						}

					case 1:		// Sides
						return new Integer(iSides);

					case 2:		// Roll
						return new Integer(iHp);

					case 3:		// Con
						int iConMod = (int) aPC.getStatBonusTo("HP", "BONUS");
						return new Integer(iConMod);

					case 4:		// Total
						iHp += (int) aPC.getStatBonusTo("HP", "BONUS");
						if (iHp < 1)
						{
							iHp = 1;
						}
						return new Integer(iHp);

					case 5:
						return "+";

					case 6:
						return "-";
					default:
						break;
				}
			}
			return null;
		}

		public void setValueAt(Object aValue, int rowIndex, int columnIndex)
		{
			if (columnIndex == 2)
			{
				int increment = Integer.parseInt(aValue.toString()) - Integer.parseInt(getValueAt(rowIndex, columnIndex).toString());
				MainHP.this.incrementForRow(rowIndex, increment);
			}
		}
	}

	protected class RendererEditor implements TableCellRenderer
	{
		JButton plusButton = new JButton("+");
		JButton minusButton = new JButton("-");
		DefaultTableCellRenderer def = new DefaultTableCellRenderer();

		public RendererEditor()
		{
			def.setBackground(MainHP.this.getBackground());
			def.setAlignmentX(Component.CENTER_ALIGNMENT);
			def.setHorizontalAlignment(SwingConstants.CENTER);
			plusButton.setPreferredSize(new Dimension(30, 24));
			plusButton.setMinimumSize(new Dimension(30, 24));
			plusButton.setMaximumSize(new Dimension(30, 24));
		}

		public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column)
		{
			switch (column)
			{
				case 5:
					def.setText("+");
					return def;

				case 6:
					def.setText("-");
					return def;
				default:
					break;

			}
			return null;
		}
	}
}


