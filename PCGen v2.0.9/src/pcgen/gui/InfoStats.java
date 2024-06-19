/*
 * InfoStats.java
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
 * Created on April 30, 2001, 10:16 PM
 */
package pcgen.gui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Iterator;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import pcgen.core.Globals;
import pcgen.core.PCClass;
import pcgen.core.Race;

/**
 *
 * @author  Peter Kahle <pkahle@pobox.com>
 * @version $Revision: 1.1 $
 */
public class InfoStats extends JPanel
{
	/** Creates new form <code>InfoStats<code>
	 */
	final String[] alignmentStrings = new String[]
	{"Lawful Good", "Lawful Neutral", "Lawful Evil", "Neutral Good", "Neutral ", "Neutral Evil", "Chaotic Good", "Chaotic Neutral", "Chaotic Evil",Globals.s_NONESELECTED,  };

	public InfoStats()
	{
		initComponents();
		int i = 0;
		for (i = 0; i < Globals.raceList.size(); i++)
		{
			if (Globals.currentPC.race().name().equals(Globals.raceList.get(i).toString()))
			{
				raceTable.getSelectionModel().setSelectionInterval(i, i);
				break;
			}
		}
		i = Globals.currentPC.alignment();
		if (i > -1)
		{
			alignment.setSelectedIndex(i);
		}
		this.addFocusListener(new FocusAdapter()
		{
			public void focusGained(FocusEvent evt)
			{
				pcChanged();
			}
		});
	}

	public boolean isFocusTraversable()
	{
		return true;
	}

	/** This method is called from within the constructor to
	 * initialize the form.
	 */
	private void initComponents()
	{
		statScrollPane = new JScrollPane();
		statTable = new JTableEx();
		lblPool = new JLabel();
		pool = new JTextField();
		bRoll = new JButton();
		bZero = new JButton();
		raceScrollPane = new JScrollPane();
		sorter.setModel(raceTableModel);
		raceTable = new JTableEx();
		sorter.addMouseListenerToHeaderInTable(raceTable);
		lblAlignment = new JLabel();
		alignment = new JComboBox();
		purchaseMethod = new JCheckBox();
		statMinLabel = new JLabel();
		statMaxLabel = new JLabel();
		statMin = new WholeNumberField(0, 4);
		statMax = new WholeNumberField(0, 4);
		unlimitedPool = new JCheckBox();


		GridBagConstraints gbc = new GridBagConstraints();
		GridBagLayout gridbag = new GridBagLayout();
		setLayout(gridbag);

		addComponentListener(new ComponentAdapter()
		{
			public void componentShown(ComponentEvent evt)
			{
				formComponentShown(evt);
			}
		});

		statScrollPane.setPreferredSize(new Dimension(20, 20));
		statTable.setModel(statTableModel);
		statTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		statTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		TableColumn col = statTable.getColumnModel().getColumn(1);
		col.setMaxWidth(40);
		col.setMinWidth(40);
		col = statTable.getColumnModel().getColumn(2);
		col.setMaxWidth(30);
		col.setMinWidth(30);
		col = statTable.getColumnModel().getColumn(3);
		col.setMaxWidth(40);
		col.setMinWidth(40);
		col = statTable.getColumnModel().getColumn(4);
		col.setMaxWidth(40);
		col.setMinWidth(40);
		col = statTable.getColumnModel().getColumn(5);
		col.setCellRenderer(plusMinusRenderer);
		col.setMaxWidth(30);
		col.setMinWidth(30);
		col = statTable.getColumnModel().getColumn(6);
		col.setCellRenderer(plusMinusRenderer);
		col.setMaxWidth(30);
		col.setWidth(30);
		col.setMinWidth(30);
		statTable.addMouseListener(new MouseAdapter()
		{
			public void mouseClicked(MouseEvent evt)
			{
				statTableMouseClicked(evt);
			}
		});

		statScrollPane.setViewportView(statTable);

		buildConstraints(gbc, 0, 0, 3, 4, 80.0, 20.0);
		gbc.fill = GridBagConstraints.BOTH;
		gbc.anchor = GridBagConstraints.WEST;
		gridbag.setConstraints(statScrollPane, gbc);
		add(statScrollPane);

		raceTable.setModel(sorter);
		raceTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		raceTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		raceScrollPane.setPreferredSize(new Dimension(20, 20));
		raceTable.getSelectionModel().addListSelectionListener(new ListSelectionListener()
		{
			public void valueChanged(ListSelectionEvent e)
			{
				if (!e.getValueIsAdjusting())
				{
					boolean conNonAbility = Globals.currentPC.race().isNonability(2);
					int currentCon = Globals.currentPC.adjStats(2);
					int hpadjust = 0;
					int i = raceTable.getSelectedRow();
					if (i < 0)
						return;
					String raceNamed = raceTable.getValueAt(i, 0).toString();
					Race r = Globals.getRaceNamed(raceNamed);
					if (!r.equals(Globals.currentPC.race()))
					{
						Globals.currentPC.setRace(r);
						if (Globals.currentPC.race().hitDice() != 0)
							Globals.currentPC.race().rollHp();
						statTableModel.fireTableDataChanged();
						if (conNonAbility && !Globals.currentPC.race().isNonability(2) && Globals.currentPC.adjStats(2) > 10)
							hpadjust = (Globals.currentPC.adjStats(2) - 10) / 2;
						else if (conNonAbility && !Globals.currentPC.race().isNonability(2) && Globals.currentPC.adjStats(2) < 10)
							hpadjust = (Globals.currentPC.adjStats(2) - 11) / 2;
						else if (Globals.currentPC.race().isNonability(2) && !conNonAbility && currentCon > 10)
							hpadjust = (10 - currentCon) / 2;
						else if (Globals.currentPC.race().isNonability(2) && !conNonAbility && currentCon < 10)
							hpadjust = (11 - currentCon) / 2;
						if (hpadjust != 0)
						{
							PCClass aClass;
							for (Iterator classes = Globals.currentPC.classList().iterator(); classes.hasNext();)
							{
								aClass = (PCClass)classes.next();
								aClass.adjustHpRolls(hpadjust);
							}
						}
					}
				}
			}
		});
		int[] cols = {0,3,4,5,6,7,8};
		raceTable.calcColumnWidths(cols);

		raceScrollPane.setViewportView(raceTable);

		buildConstraints(gbc, 0, 4, 7, 3, 0.0, 80.0);
		gbc.fill = GridBagConstraints.BOTH;
		gbc.anchor = GridBagConstraints.WEST;
		gridbag.setConstraints(raceScrollPane, gbc);
		add(raceScrollPane);

		lblPool.setText(" Pool: ");
		buildConstraints(gbc, 3, 0, 1, 1, 2.0, 2.0);
		gbc.fill = GridBagConstraints.NONE;
		gbc.anchor = GridBagConstraints.EAST;
		gridbag.setConstraints(lblPool, gbc);
		add(lblPool);

		pool.setEditable(false);
		buildConstraints(gbc, 4, 0, 1, 1, 2.0, 0.0);
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.anchor = GridBagConstraints.WEST;
		gridbag.setConstraints(pool, gbc);
		add(pool);

		bRoll.setText("Roll");
		bRoll.setPreferredSize(new Dimension(50, 25));
		bRoll.setMaximumSize(new Dimension(70, 25));
		bRoll.setMargin(new Insets(2, 5, 2, 5));
		bRoll.setMinimumSize(new Dimension(50, 25));
		bRoll.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				characterRolled(evt);
				PCGen_Frame1.statusBar.setText("Change Roll Methods under Options");
			}
		});
		buildConstraints(gbc, 5, 0, 1, 1, 2.0, 0.0);
		gbc.fill = GridBagConstraints.NONE;
		gbc.anchor = GridBagConstraints.WEST;
		gridbag.setConstraints(bRoll, gbc);
		add(bRoll);

		bZero.setText("Zero");
		bZero.setPreferredSize(new Dimension(50, 25));
		bZero.setMaximumSize(new Dimension(70, 25));
		bZero.setMargin(new Insets(2, 5, 2, 5));
		bZero.setMinimumSize(new Dimension(50, 25));
		bZero.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				zeroPool(evt);
			}
		});
		buildConstraints(gbc, 6, 0, 1, 1, 2.0, 0.0);
		gbc.fill = GridBagConstraints.NONE;
		gbc.anchor = GridBagConstraints.WEST;
		gridbag.setConstraints(bZero, gbc);
		add(bZero);

		lblAlignment.setText("Alignment: ");
		buildConstraints(gbc, 4, 1, 1, 1, 2.0, 2.0);
		gbc.fill = GridBagConstraints.NONE;
		gbc.anchor = GridBagConstraints.EAST;
		gridbag.setConstraints(lblAlignment, gbc);
		add(lblAlignment);

		alignment.setModel(new DefaultComboBoxModel(alignmentStrings));
		alignment.setToolTipText("You must select an alignment.");
		alignment.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				alignmentChanged(evt);
			}
		});

		buildConstraints(gbc, 5, 1, 1, 1, 0.0, 0.0);
		gbc.fill = GridBagConstraints.NONE;
		gbc.anchor = GridBagConstraints.WEST;
		gridbag.setConstraints(alignment, gbc);
		add(alignment, gbc);

		purchaseMethod.setToolTipText("Buy stats using a point system");
		purchaseMethod.setText("Use Purchase Method");
		purchaseMethod.setSelected(Globals.purchaseStatMode);
		purchaseMethod.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				Globals.purchaseStatMode = purchaseMethod.isSelected();
				PCGen_Frame1.statusBar.setText("You may alter the costs under the Options Menu.");
			}
		});
		buildConstraints(gbc, 3, 2, 2, 1, 0.0, 2.0);
		gbc.fill = GridBagConstraints.NONE;
		gbc.anchor = GridBagConstraints.CENTER;
		gridbag.setConstraints(purchaseMethod, gbc);
		add(purchaseMethod);

		unlimitedPool.setToolTipText("Allow unlimited stat editing");
		unlimitedPool.setText("Unlimited Stat Pool");
		unlimitedPool.setSelected(Globals.unlimitedStatPool);
		unlimitedPool.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				Globals.unlimitedStatPool = unlimitedPool.isSelected();
			}
		});
		buildConstraints(gbc, 5, 2, 2, 1, 0.0, 0.0);
		gbc.fill = GridBagConstraints.NONE;
		gbc.anchor = GridBagConstraints.CENTER;
		gridbag.setConstraints(unlimitedPool, gbc);
		add(unlimitedPool);

		statMinLabel.setText("Stat Min: ");
		statMinLabel.setToolTipText("The lowest value one can lower a stat to, excluding race bonuses");
		buildConstraints(gbc, 3, 3, 1, 1, 0.0, 0.0);
		gbc.fill = GridBagConstraints.NONE;
		gbc.anchor = GridBagConstraints.EAST;
		gridbag.setConstraints(statMinLabel, gbc);
		add(statMinLabel);

		statMaxLabel.setText("Stat Max: ");
		statMaxLabel.setToolTipText("The highest value one can raise a stat to, excluding race bonuses");
		buildConstraints(gbc, 5, 3, 1, 1, 0.0, 2.0);
		gbc.fill = GridBagConstraints.NONE;
		gbc.anchor = GridBagConstraints.EAST;
		gridbag.setConstraints(statMaxLabel, gbc);
		add(statMaxLabel);

		statMin.setText(Integer.toString(Globals.initialStatMin));
		statMin.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				Object source = evt.getSource();
				updateTextFields(source);
			}
		});
		statMin.addFocusListener(new FocusAdapter()
		{
			public void focusLost(FocusEvent evt)
			{
				textFieldFocusEvent(evt);
			}
		});

		buildConstraints(gbc, 4, 3, 1, 1, 0.0, 0.0);
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.anchor = GridBagConstraints.WEST;
		gridbag.setConstraints(statMin, gbc);
		add(statMin);

		statMax.setText(Integer.toString(Globals.initialStatMax));
		statMax.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				Object source = evt.getSource();
				updateTextFields(source);
			}
		});
		statMax.addFocusListener(new FocusAdapter()
		{
			public void focusLost(FocusEvent evt)
			{
				textFieldFocusEvent(evt);
			}
		});

		buildConstraints(gbc, 6, 3, 1, 1, 0.0, 0.0);
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.anchor = GridBagConstraints.WEST;
		gridbag.setConstraints(statMax, gbc);
		add(statMax);


	}

	private void buildConstraints(GridBagConstraints gbc, int gx, int gy, int gw, int gh, double wx, double wy)
	{
		gbc.gridx = gx;
		gbc.gridy = gy;
		gbc.gridwidth = gw;
		gbc.gridheight = gh;
		gbc.weightx = wx;
		gbc.weighty = wy;
	}

	private void textFieldFocusEvent(FocusEvent evt)//GEN-FIRST:event_textFieldFocusEvent
	{
		updateTextFields(evt.getSource());
	}

	private final void updateTextFields(Object source)
	{
		if (source == statMin)
		{
			try
			{
				Globals.initialStatMin = Integer.parseInt(statMin.getText());
			}
			catch (NumberFormatException nfe)
			{
				statMin.setText(Integer.toString(Globals.initialStatMin));
			}
		}
		else if (source == statMax)
		{
			try
			{
				Globals.initialStatMax = Integer.parseInt(statMax.getText());
			}
			catch (NumberFormatException nfe)
			{
				statMax.setText(Integer.toString(Globals.initialStatMax));
			}
		}
	}


	private void formComponentShown(ComponentEvent evt)
	{
		requestDefaultFocus();
		showAlignment();
		updatePool();
	}

	private void characterRolled(ActionEvent evt)
	{
		Globals.currentPC.setDirty(true);
		Globals.currentPC.rollStats(Globals.rollMethod);
		updatePool();
		statTableModel.fireTableDataChanged();
	}

	private void zeroPool(ActionEvent evt)
	{
		Globals.currentPC.setDirty(true);
		Globals.currentPC.setPoolAmount(0);
		pool.setText(Integer.toString(Globals.currentPC.remainingPool()));
		pcChanged();
	}

	private void alignmentChanged(ActionEvent evt)
	{
		Globals.currentPC.setAlignment(alignment.getSelectedIndex());
	}

	public void pcChanged()
	{
		if (Globals.purchaseStatMode == true)
			pool.setText(Integer.toString(Globals.currentPC.poolAmount()));
		else
			pool.setText(Integer.toString(Globals.currentPC.remainingPool()));
		alignment.setSelectedIndex(Globals.currentPC.alignment());
		raceTable.getSelectionModel().setSelectionInterval(
			Globals.raceList.indexOf(Globals.currentPC.race()),
			Globals.raceList.indexOf(Globals.currentPC.race()));
		showAlignment();
		updatePool();
	}

	protected void showAlignment()
	{
		if (pcgen.core.Globals.starWarsMode)
		{
			lblAlignment.setVisible(false);
			alignment.setVisible(false);
		}
		else
		{
			lblAlignment.setVisible(true);
			alignment.setVisible(true);
		}
	}

	private void statTableMouseClicked(MouseEvent evt)
	{
		int[] stats = Globals.currentPC.stats();
		int stat = stats[statTable.getSelectedRow()];
		boolean makeChange = false;
		int increment = 0;

		switch (statTable.columnAtPoint(evt.getPoint()))
		{
			case 5:
				increment = 1;
				if (Globals.currentPC.totalLevels() < 1 && stat >= Globals.initialStatMax && !Globals.purchaseStatMode)
					JOptionPane.showMessageDialog(null, "Cannot raise stat above " + new Integer(Globals.initialStatMax).toString());
				else if ((Globals.purchaseStatMode == false || (Globals.purchaseStatMode == true && Globals.currentPC.totalLevels() > 0)) && Globals.currentPC.poolAmount() < 1 && !Globals.unlimitedStatPool)
					JOptionPane.showMessageDialog(null, "You have no pool points to spend.");
				else if (Globals.currentPC.totalLevels() < 1 && stat >= 18 && Globals.purchaseStatMode)
					JOptionPane.showMessageDialog(null, "Cannot raise stat above 18 in Purchase Mode");
				else if (Globals.currentPC.race().isNonability(statTable.getSelectedRow()))
					JOptionPane.showMessageDialog(null, "Cannot increment a nonability");
				else
				{
					makeChange = true;
					if (Globals.purchaseStatMode == false || Globals.currentPC.totalLevels() > 0)
						Globals.currentPC.setPoolAmount(Math.max(Globals.currentPC.poolAmount() - 1, 0));
				}
				break;
			case 6:
				increment = -1;
				if (Globals.currentPC.totalLevels() < 1 && stat <= 8 && Globals.purchaseStatMode)
					JOptionPane.showMessageDialog(null, "Cannot lower stat below 8 in Purchase Mode");
				else if (Globals.currentPC.totalLevels() < 1 && (stat <= Globals.initialStatMin ||
					(stat <= 8 && Globals.purchaseStatMode)))
					JOptionPane.showMessageDialog(null, "Cannot lower stat below " + new Integer(Globals.initialStatMin).toString());
				else if (Globals.currentPC.race().isNonability(statTable.getSelectedRow()))
					JOptionPane.showMessageDialog(null, "Cannot decrement a nonability");
				else
				{
					makeChange = true;
					if (Globals.purchaseStatMode == false || Globals.currentPC.totalLevels() > 0)
						Globals.currentPC.setPoolAmount(Globals.currentPC.poolAmount() + 1);
				}
				break;
		}
		if (makeChange)
		{
			Globals.currentPC.setDirty(true);
			stats[statTable.getSelectedRow()] = stat + increment;
			if (statTable.getValueAt(statTable.getSelectedRow(), 0).toString().equals("Constitution"))
			{
				// when Con is adjusted, HP for all levels may be affected
				// if Con is added and even, then add 1 hp to every level
				// if Con is subtracted and odd, then sub 1 hp to every level
				if ((increment > 0 && (stat + increment) % 2 == 0) || (increment < 0 && (stat + increment) % 2 != 0))
				{
					Globals.currentPC.adjustHpRolls(increment);
				}
			}
			Globals.currentPC.setStats(stats);
			updatePool();
			statTableModel.fireTableRowsUpdated(statTable.getSelectedRow(), statTable.getSelectedRow());
		}
	}

	private void updatePool()
	{
		int[] stats = Globals.currentPC.stats();
		int stat = 0;
		String bString = "";
		if (Globals.purchaseStatMode == true)
		{
			if (Globals.currentPC.totalLevels() == 0)
			{
				int i = 0;
				for (stat = 0; stat < 6; stat++)
					if (stats[stat] > 8 && stats[stat] < 19)
						i += new Integer(Globals.statCost[stats[stat] - 9]).intValue();
				if (Globals.currentPC.totalLevels() == 0)
				{
					Globals.currentPC.setDirty(true);
					Globals.currentPC.setCostPool(i);
					Globals.currentPC.setPoolAmount(i);
				}
			}
			bString = " (" + new Integer(Globals.currentPC.costPool()).toString() + ")";
		}
		String aString = new Integer(Globals.currentPC.poolAmount()).toString();
		if (bString.length() > 0)
			aString = aString + bString;
		pool.setText(aString);
	}

	protected class StatTableModel extends AbstractTableModel
	{
		public int getColumnCount()
		{
			return 7;
		}

		public Class getColumnClass(int columnIndex)
		{
			return getValueAt(0, columnIndex).getClass();
		}

		/** <code>getRowCount()
		 * returns the number of rows. In the current version, this is fixed at 6.
		 * once we have a location from which to pull the list of stats, we can
		 * change it to use that instead
		 * TODO: fix to pull the stat list from somewhere.
		 */
		public int getRowCount()
		{
			if (Globals.currentPC != null)
			{
				return 6;
			}
			else
			{
				return 0;
			}
		}

		public String getColumnName(int columnIndex)
		{
			switch (columnIndex)
			{
				case 0:
					return "Stat";
				case 1:
					return "Score";
				case 2:
					return "Adj";
				case 3:
					return "Total";
				case 4:
					return "Mod";
				case 5:
					return "+";
				case 6:
					return "-";
				default:
					return "Out of Bounds";
			}
		}

		public Object getValueAt(int rowIndex, int columnIndex)
		{
			if (columnIndex == 0)
			{
				switch (rowIndex)
				{
					case 0:
						return "Strength";
					case 1:
						return "Dexterity";
					case 2:
						return "Constitution";
					case 3:
						return "Intelligence";
					case 4:
						return "Wisdom";
					case 5:
						return "Charisma";
					default:
						return "Out of Bounds";
				}
			}
			switch (columnIndex)
			{
				case 1:
					return new Integer(Globals.currentPC.stats()[rowIndex]);
				case 2:
					if (Globals.currentPC.race().isNonability(rowIndex))
						return "-";
					return new Integer(Globals.currentPC.adjStats(rowIndex) - Globals.currentPC.stats()[rowIndex]);
				case 3:
					if (Globals.currentPC.race().isNonability(rowIndex))
						return "--";
					return new Integer(Globals.currentPC.adjStats(rowIndex));
				case 4:
					if (Globals.currentPC.race().isNonability(rowIndex))
						return new Integer(0);
					return new Integer(Globals.currentPC.adjStats(rowIndex) / 2 - 5);
				case 5:
					return "+";
				case 6:
					return "-";
				default:
					return "Out of Bounds";
			}
		}
	}

	protected class RaceTableModel extends AbstractTableModel
	{
		public int getColumnCount()
		{
			return 9;
		}

		public Class getColumnClass(int columnIndex)
		{
			return getValueAt(0, columnIndex).getClass();
		}

		public int getRowCount()
		{
			if (Globals.raceList != null)
			{
				return Globals.raceList.size();
			}
			else
				return 0;
		}

		public String getColumnName(int columnIndex)
		{
			switch (columnIndex)
			{
				case 0:
					return "Name";
				case 1:
					return "Stat Adjustments";
				case 2:
					return "AL Req";
				case 3:
					return "Size";
				case 4:
					return "Speed";
				case 5:
					return "Vision";
				case 6:
					return "Favored Class";
				case 7:
					return "Lvl Adj";
				case 8:
					return "Source";
			}
			return "Out Of Bounds";
		}

		public Object getValueAt(int rowIndex, int columnIndex)
		{
			if (Globals.raceList != null)
			{
				Race race = (Race)Globals.raceList.get(rowIndex);
				switch (columnIndex)
				{
					case 0:
						return race.toString();
					case 1:
						String aString = "";
						for (int i = 0; i < 6; i++)
							if (race.isNonability(i))
							{
								if (aString.length() > 0)
									aString = aString + " ";
								aString = aString + race.statNames.substring(i * 3, i * 3 + 3) + ":Nonability";
							}
							else
							{
								if (race.statMod(i) != 0)
								{
									if (aString.length() > 0)
										aString = aString + " ";
									aString = aString + race.statNames.substring(i * 3, i * 3 + 3) + ":" + new Integer(race.statMod(i)).toString();
								}
							}
						return aString;
					case 2:
						return race.getAlignments();
					case 3:
						return race.size();
					case 4:
						if (race.movements() != null)
						{
							String movelabel = race.movementType(0) + " " + race.movement(0).toString() + "'";
							for (int x = 1; x < race.movements().length; x++)
								movelabel += ", " + race.movementType(x) + " " + race.movement(x).toString() + "'";
							return movelabel;
						}
						return "";
					case 5:
						return race.vision();
					case 6:
						return race.favoredClass();
					case 7:
						return new Integer(race.LevelAdjustment());
					case 8:
						return race.getSourceFile();
				}
			}
			return Boolean.FALSE; // compiler error, so I need to return something.
		}
	}

	protected class RendererEditor implements TableCellRenderer
	{
		JButton plusButton = new JButton("+");
		JButton minusButton = new JButton("-");
		DefaultTableCellRenderer def = new DefaultTableCellRenderer();

		public RendererEditor()
		{
			def.setBackground(InfoStats.this.getBackground());
			def.setAlignmentX(def.CENTER_ALIGNMENT);
			def.setHorizontalAlignment(def.CENTER);
			plusButton.setPreferredSize(new Dimension(30, 24));
			plusButton.setMinimumSize(new Dimension(30, 24));
			plusButton.setMaximumSize(new Dimension(30, 24));
		}

		public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column)
		{
			if (column == 5)
			{
				def.setText("+");
				return def;
			}
			else if (column == 6)
			{
				def.setText("-");
				return def;
			}
			return null;
		}
	}

	/** Checks if race and alignment have been chosen. */
	boolean allowLeaving()
	{
		if (raceTable.getValueAt(raceTable.getSelectedRow(), 0).equals(Globals.s_NONESELECTED))
		{
			JOptionPane.showMessageDialog(null, "You must select a race.");
			return false;
		}
		else if (!Globals.starWarsMode && alignment.getSelectedItem().equals(Globals.s_NONESELECTED))
		{
			JOptionPane.showMessageDialog(null, "You must select an alignment.");
			return false;
		}
		return true;
	}


	// Variables declaration
	private JScrollPane statScrollPane;
	private JTableEx statTable;
	private JLabel lblPool;
	private JTextField pool;
	private JButton bRoll;
	private JScrollPane raceScrollPane;
	private JTableEx raceTable;
	private JLabel lblAlignment;
	private JComboBox alignment;

	RaceTableModel raceTableModel = new RaceTableModel();
	StatTableModel statTableModel = new StatTableModel();
	RendererEditor plusMinusRenderer = new RendererEditor();
	protected TableSorter sorter = new TableSorter();
	private JButton bZero;
	private JLabel statMinLabel;
	private JLabel statMaxLabel;
	private JTextField statMin;
	private JTextField statMax;
	private JCheckBox purchaseMethod;
	private JCheckBox unlimitedPool;

}
