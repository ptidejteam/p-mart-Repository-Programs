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
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
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


public class MainHP extends JPanel
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
				JFrame parentFrame = (JFrame)getParent().getParent().getParent().getParent();  //ugly, but effective...
				parentFrame.dispose();
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
		int iMax;
		int iRoll;
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
		if (iRow >= currentHpTableModel.getRowCount() - 2)
		{
			return;
		}


		iMax = ((Integer)currentHpTableModel.getValueAt(iRow, 1)).intValue();		// # of sides on die
		iRoll = ((Integer)currentHpTableModel.getValueAt(iRow, 2)).intValue();		// current value

		if (iRoll > iMax)
		{
			iRoll = iMax;
			JOptionPane.showMessageDialog(null, "Setting roll to maximum (" + iMax + ")", "PCGen", JOptionPane.ERROR_MESSAGE);
			increment = 0;
		}


		if ((iRoll + increment) < 1)
		{
			JOptionPane.showMessageDialog(null, "Roll is already at minimum (1)", "PCGen", JOptionPane.ERROR_MESSAGE);
		}
		else if ((iRoll + increment) > iMax)
		{
			JOptionPane.showMessageDialog(null, "Roll is already at maximum (" + iMax + ")", "PCGen", JOptionPane.ERROR_MESSAGE);
		}
		else
		{
			iRoll += increment;

			PCClass aClass = null;
			Race aRace = null;
			if (Globals.getCurrentPC() != null)
			{
				aRace = Globals.getCurrentPC().getRace();
				if (aRace != null)
				{
					if (iRow < aRace.hitDice())
					{
						aRace.setHitPoint(iRow, new Integer(iRoll));
					}
					iRow -= aRace.hitDice();
				}

				if (Globals.getCurrentPC().getClassList() != null)
				{
					for (Iterator e = Globals.getCurrentPC().getClassList().iterator(); e.hasNext();)
					{
						if (iRow < 0)
						{
							break;
						}

						aClass = (PCClass)e.next();
						//
						// Ignore if no levels
						//
						if (aClass.getLevel().intValue() < 1)
						{
							continue;
						}
						//
						// Walk through the levels for this class
						//
						for (int j = 0; j < aClass.getLevel().intValue(); j++)
						{
							if (iRow-- == 0)
							{
								aClass.setHitPoint(j, new Integer(iRoll));
								break;
							}
						}
					}
				}
				Globals.getCurrentPC().setDirty(true);
				currentHpTableModel.fireTableDataChanged();
			}

		}
	}


	private class PCListModel extends AbstractListModel
	{
		public Object getElementAt(int index)
		{
			if (index < Globals.getPcList().size())
			{
				final PlayerCharacter aPC = (PlayerCharacter)Globals.getPcList().get(index);
				return aPC.getName();
			}
			else
			{
				return null;
			}
		}

		public int getSize()
		{
			return Globals.getPcList().size();
		}
	}

	/**
	 *
	 */
	public class PCHitPointsTableModel extends AbstractTableModel
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
			}
			return null;
		}

		public int getRowCount()
		{
			int iRows = 2;
			if (Globals.getCurrentPC() != null)
			{
				if (Globals.getCurrentPC().getRace() != null)
				{
					iRows += Globals.getCurrentPC().getRace().hitDice();
				}
				if (Globals.getCurrentPC().getClassList() != null)
				{
					PCClass aClass = null;
					for (Iterator e = Globals.getCurrentPC().getClassList().iterator(); e.hasNext();)
					{
						aClass = (PCClass)e.next();
						iRows += aClass.getLevel().intValue();
					}
				}
			}
			return iRows;
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
				if (aPC.getClassList() != null)
				{
					for (Iterator e = aPC.getClassList().iterator(); e.hasNext();)
					{
						if (rowIndex < 0)
						{
							break;
						}

						aClass = (PCClass)e.next();
						//
						// Ignore if no levels
						//
						if (aClass.getLevel().intValue() < 1)
						{
							continue;
						}

						//
						// Walk through the levels for this class
						//
						iSides = aClass.getHitDie();
						for (int j = 0; j < aClass.getLevel().intValue(); j++)
						{
							if (rowIndex < 0)
							{
								//
								// Sanity check
								//
								if (iHp > iSides)
								{
									aClass.setHitPoint(rowIndex, new Integer(iSides));
									iHp = iSides;
								}
								break;
							}
							iHp = aClass.getHitPointList(j).intValue();
							rowIndex -= 1;
						}
					}
				}
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
							Integer iBonus = new Integer(aPC.getTotalBonusTo("HP", "CURRENTMAX", true));
							return iBonus;
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
								iHp += ((Integer)getValueAt(i, 4)).intValue();
							}
							return new Integer(iHp);
					}
					return null;
				}


				switch (columnIndex)
				{
					case 0:		// Name
						if (aClass == null)
						{
							return aRace.getName();
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
						int iConMod = aPC.calcStatMod(Globals.CONSTITUTION);
						return new Integer(iConMod);

					case 4:		// Total
						iHp += aPC.calcStatMod(Globals.CONSTITUTION);
						if (iHp < 1)
						{
							iHp = 1;
						}
						return new Integer(iHp);

					case 5:
						return "+";

					case 6:
						return "-";
				}
			}
			return null;
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
			def.setAlignmentX(def.CENTER_ALIGNMENT);
			def.setHorizontalAlignment(def.CENTER);
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
			}
			return null;
		}
	}
}


