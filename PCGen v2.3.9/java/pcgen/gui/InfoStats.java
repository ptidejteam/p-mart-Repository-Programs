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
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import pcgen.core.Feat;
import pcgen.core.Globals;
import pcgen.core.PCClass;
import pcgen.core.PlayerCharacter;
import pcgen.core.Race;


/**
 *
 * @author  Peter Kahle <pkahle@pobox.com>
 * @version $Revision: 1.1 $
 */
public class InfoStats extends JPanel
{
	private JScrollPane statScrollPane;
	private JTableEx statTable;
	private JLabel lblPool;
	private JTextField pool;
	private JButton bRoll;
	private JScrollPane raceScrollPane;
	private JTableEx raceTable;
	private JLabel lblAlignment;
	private JComboBox alignment;
	private JLabel d_forceLabel;
	private JTextField d_forceAmount;
	private JLabel d_dsideLabel;
	private JTextField d_dsideAmount;
	protected RaceTableModel raceTableModel = new RaceTableModel();
	protected StatTableModel statTableModel = new StatTableModel();
	protected RendererEditor plusMinusRenderer = new RendererEditor();
	protected TableSorter sorter = new TableSorter();
	private JButton bZero;
	private JLabel statMinLabel;
	private JLabel statMaxLabel;
	private JTextField statMin;
	private JTextField statMax;
	private JCheckBox purchaseMethod;
	private JCheckBox unlimitedPool;
	private JLabel filter = new JLabel("Filter: ");
	private JComboBox filters = new JComboBox();

	/** Creates new form <code>InfoStats<code>
	 */

	public InfoStats()
	{
		initComponents();

		final PlayerCharacter aPC = Globals.getCurrentPC();
		selectRaceByName(aPC.getRace().getName());

		final int align = aPC.getAlignment();
		if (align > -1)
		{
			alignment.setSelectedIndex(align);
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
/* added by TNC 08/27/01 */
		d_forceLabel = new JLabel();
		d_forceAmount = new JTextField();
		d_dsideLabel = new JLabel();
		d_dsideAmount = new JTextField();
		/*************************/

		filters.addItem("All");
		filters.addItem("Qualified");
		//sort types
		final ArrayList raceList = new ArrayList(Globals.getRaceMap().values());

		ArrayList filterList = new ArrayList();
		filterList.add("Humanoid");
		for (int i = 0; i < raceList.size(); i++)
		{
			final Race race = (Race)raceList.get(i);
			final String sType = race.getType();
			if (!filterList.contains(sType))
			{
				filterList.add(sType);
			}
		}
		Collections.sort(filterList);
		for (int i = 0; i < filterList.size(); i++)
		{
			filters.addItem((String)filterList.get(i));
		}

		filters.addItemListener(new ItemListener()
		{
			public void itemStateChanged(ItemEvent e)
			{
				raceTableModel.setFilter(e.getItem().toString(), filters.getSelectedIndex());
				selectRaceByName(Globals.getCurrentPC().getRace().getName());
			}
		});


		GridBagConstraints gbc = new GridBagConstraints();
		GridBagLayout gridbag = new GridBagLayout();
		setLayout(gridbag);

		addComponentListener(new ComponentAdapter()
		{
			public void componentShown(ComponentEvent evt)
			{
				final PlayerCharacter aPC = Globals.getCurrentPC();
				aPC.setAggregateFeatsStable(false);
				aPC.setAutomaticFeatsStable(true);
				aPC.setVirtualFeatsStable(false);
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
					final PlayerCharacter aPC = Globals.getCurrentPC();
					boolean conNonAbility = aPC.isNonability(Globals.CONSTITUTION);
					int currentCon = aPC.adjStats(Globals.CONSTITUTION);
					int i = raceTable.getSelectedRow();
					if (i < 0)
						return;
					String raceNamed = raceTable.getValueAt(i, 0).toString();
					Race r = Globals.getRaceNamed(raceNamed);
					if (!r.equals(aPC.getRace()))
					{
						aPC.setRace(r);
						InfoFeats.needsUpdate=true;
						if (aPC.getRace().hitDice() != 0)
						{
							aPC.getRace().rollHp();
						}
						statTableModel.fireTableDataChanged();
					}
				}
			}
		});
		final int[] cols = {0, 3, 4, 5, 6, 7, 8};
		raceTable.setOptimalColumnWidths(cols);

		raceScrollPane.setViewportView(raceTable);

//		buildConstraints(gbc, 0, 4, 7, 3, 0.0, 80.0);
		buildConstraints(gbc, 0, 5, 7, 3, 0.0, 80.0);
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

		bRoll.setText("Roll Stats");
		bRoll.setMnemonic('R');
		bRoll.setPreferredSize(new Dimension(80, 25));
		bRoll.setMaximumSize(new Dimension(100, 25));
		bRoll.setMargin(new Insets(2, 5, 2, 5));
		bRoll.setMinimumSize(new Dimension(50, 25));
		bRoll.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				characterRolled(evt);
				PCGen_Frame1.getStatusBar().setText("Change Roll Methods under Options");
			}
		});
		buildConstraints(gbc, 5, 0, 1, 1, 2.0, 0.0);
		gbc.fill = GridBagConstraints.NONE;
		gbc.anchor = GridBagConstraints.WEST;
		gridbag.setConstraints(bRoll, gbc);
		add(bRoll);

		bZero.setText("Zero");
		bZero.setMnemonic('Z');
		bZero.setPreferredSize(new Dimension(75, 25));
		bZero.setMaximumSize(new Dimension(100, 25));
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

		alignment.setModel(new DefaultComboBoxModel(Globals.s_ALIGNLONG));
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

		d_forceLabel.setText("Force Points:");
		buildConstraints(gbc, 3, 1, 1, 1, 2.0, 2.0);
		gbc.fill = GridBagConstraints.NONE;
		gbc.anchor = GridBagConstraints.EAST;
		gridbag.setConstraints(d_forceLabel, gbc);
		add(d_forceLabel);

		d_forceAmount.setToolTipText("Number of Force Points for the character.");
		d_forceAmount.setText(Globals.getCurrentPC().getFPoints());
		d_forceAmount.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				forceReturnPressed(evt);
			}
		});

		d_forceAmount.addFocusListener(new FocusAdapter()
		{
			public void focusLost(FocusEvent evt)
			{
				forceLostFocus(evt);
			}
		});

		buildConstraints(gbc, 4, 1, 1, 1, 0.5, 0.0);
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.anchor = GridBagConstraints.EAST;
		gridbag.setConstraints(d_forceAmount, gbc);
		add(d_forceAmount, gbc);

		d_dsideLabel.setText("Darkside Points:");
		buildConstraints(gbc, 5, 1, 1, 1, 2.0, 2.0);
		gbc.fill = GridBagConstraints.NONE;
		gbc.anchor = GridBagConstraints.EAST;
		gridbag.setConstraints(d_dsideLabel, gbc);
		add(d_dsideLabel);

		d_dsideAmount.setToolTipText("Number of Darkside Points for the character.");
		d_dsideAmount.setText(Globals.getCurrentPC().getDPoints());
		d_dsideAmount.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				dsideReturnPressed(evt);
			}
		});

		d_dsideAmount.addFocusListener(new FocusAdapter()
		{
			public void focusLost(FocusEvent evt)
			{
				dsideLostFocus(evt);
			}
		});

		buildConstraints(gbc, 6, 1, 1, 1, 0.5, 0.0);
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.anchor = GridBagConstraints.EAST;
		gridbag.setConstraints(d_dsideAmount, gbc);
		add(d_dsideAmount, gbc);

		purchaseMethod.setToolTipText("Buy stats using a point system");
		purchaseMethod.setText("Use Purchase Method");
		purchaseMethod.setSelected(Globals.isPurchaseStatMode());
		purchaseMethod.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				Globals.setPurchaseStatMode(purchaseMethod.isSelected());
				PCGen_Frame1.getStatusBar().setText("You may alter the costs under the Options Menu.");
			}
		});
		buildConstraints(gbc, 3, 2, 2, 1, 0.0, 2.0);
		gbc.fill = GridBagConstraints.NONE;
		gbc.anchor = GridBagConstraints.CENTER;
		gridbag.setConstraints(purchaseMethod, gbc);
		add(purchaseMethod);

		unlimitedPool.setToolTipText("Allow unlimited stat editing");
		unlimitedPool.setText("Unlimited Stat Pool");
		unlimitedPool.setSelected(Globals.isStatPoolUnlimited());
		unlimitedPool.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				Globals.setStatPoolUnlimited(unlimitedPool.isSelected());
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

		statMin.setText(Integer.toString(Globals.getInitialStatMin()));
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

		statMax.setText(Integer.toString(Globals.getInitialStatMax()));
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


		buildConstraints(gbc, 0, 4, 1, 1, 0.0, 0.0);
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.anchor = GridBagConstraints.WEST;
		gridbag.setConstraints(filter, gbc);
		add(filter);

		buildConstraints(gbc, 1, 4, 1, 1, 0.0, 0.0);
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.anchor = GridBagConstraints.WEST;
		gridbag.setConstraints(filters, gbc);
		add(filters);

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

	private void textFieldFocusEvent(FocusEvent evt)
	{
		updateTextFields(evt.getSource());
	}

	private final void updateTextFields(Object source)
	{
		if (source == statMin)
		{
			try
			{
				Globals.setInitialStatMin((Integer.parseInt(statMin.getText())));
			}
			catch (NumberFormatException nfe)
			{
				statMin.setText(Integer.toString(Globals.getInitialStatMin()));
			}
		}
		else if (source == statMax)
		{
			try
			{
				Globals.setInitialStatMax(Integer.parseInt(statMax.getText()));
			}
			catch (NumberFormatException nfe)
			{
				statMax.setText(Integer.toString(Globals.getInitialStatMax()));
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
		final PlayerCharacter aPC = Globals.getCurrentPC();
		aPC.setDirty(true);
		aPC.rollStats(Globals.getRollMethod());
		updatePool();
		statTableModel.fireTableDataChanged();
	}

	private void zeroPool(ActionEvent evt)
	{
		final PlayerCharacter aPC = Globals.getCurrentPC();
		aPC.setDirty(true);
		aPC.setPoolAmount(0);
		pool.setText(Integer.toString(aPC.getRemainingPool()));
		pcChanged();
	}

	private void forceReturnPressed(ActionEvent evt)
	{
		updateCharacterForce();
	}

	private void forceLostFocus(FocusEvent evt)
	{
		updateCharacterForce();
	}

	private void dsideReturnPressed(ActionEvent evt)
	{
		updateCharacterDside();
	}

	private void dsideLostFocus(FocusEvent evt)
	{
		updateCharacterDside();
	}

	private final void updateCharacterForce()
	{
		final PlayerCharacter aPC = Globals.getCurrentPC();
		if (aPC != null)
		{
			try
			{
				String forceAmt = d_forceAmount.getText();
				if (!forceAmt.equals(aPC.getFPoints()))
				{
					System.out.println("Sensitive? " + sensitiveCheck());
					if (sensitiveCheck())
					{
						aPC.setFPoints(d_forceAmount.getText());
						aPC.setDirty(true);
					}
					else
					{
						aPC.setFPoints(d_forceAmount.getText());
						aPC.setDirty(true);
						if (Integer.parseInt(forceAmt) > 5)
						{
							aPC.setFPoints("5");
							aPC.setDirty(true);
							System.out.println("not sensitive/set to 5");
						}
					}
				}
			}
			catch (NumberFormatException nfe)
			{
				d_forceAmount.setText(aPC.getFPoints());
			}
		}
	}

	private boolean sensitiveCheck()
	{
		boolean foundIt = false;
		ArrayList aFeatList = (ArrayList)Globals.getCurrentPC().aggregateFeatList();
		Feat aFeat = null;
		for (Iterator e1 = aFeatList.iterator(); e1.hasNext();)
		{
			if (foundIt)
				break;
			aFeat = (Feat)e1.next();
			if (aFeat.getName().equals("Force Sensitive"))
			{
				foundIt = true;
			}
		}

		return foundIt;
	}

	private final void updateCharacterDside()
	{
		final PlayerCharacter aPC = Globals.getCurrentPC();
		if (aPC != null)
		{
			try
			{
				final String dsideAmt = d_dsideAmount.getText();
				if (!dsideAmt.equals(aPC.getDPoints()))
				{
					aPC.setDPoints(dsideAmt);
					aPC.setDirty(true);
				}
			}
			catch (NumberFormatException nfe)
			{
				d_dsideAmount.setText(aPC.getDPoints());
			}
		}
	}


	private void alignmentChanged(ActionEvent evt)
	{
		final PlayerCharacter aPC = Globals.getCurrentPC();
		final int newAlignment = alignment.getSelectedIndex();
		final int oldAlignment = aPC.getAlignment();
		if (newAlignment == oldAlignment)
			return;


		//
		// Get a list of classes that will become unqualified
		//
		StringBuffer unqualified = new StringBuffer();
		ArrayList classList = aPC.getClassList();
		ArrayList exclassList = new ArrayList();
		for (Iterator e = classList.iterator(); e.hasNext();)
		{
			final PCClass aClass = (PCClass)e.next();

			aPC.setAlignment(oldAlignment, false, true);
			if (aClass.isQualified())
			{
				aPC.setAlignment(newAlignment, false, true);
				if (!aClass.isQualified())
				{
					if (unqualified.length() > 0)
						unqualified.append(", ");
					unqualified.append(aClass.getName());
					exclassList.add(aClass);
				}
			}
		}

		//
		// Give the user a chance to bail
		//
		if (unqualified.length() > 0)
		{
			if (JOptionPane.showConfirmDialog(null, "This will change the following class(es) to ex-class(es):\n" + unqualified.toString(), "PCGen", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.CANCEL_OPTION)
			{
				aPC.setAlignment(oldAlignment, false, true);
				alignment.setSelectedIndex(oldAlignment);
				return;
			}
		}

		//
		// Convert the class(es)
		//
		for (Iterator e = exclassList.iterator(); e.hasNext();)
		{
			final PCClass aClass = (PCClass)e.next();
			aPC.makeIntoExClass(aClass);
		}
		aPC.setAlignment(newAlignment, false, true);

		//
		// Refresh the table if changed alignment
		//
		final String currentFilter = (String)filters.getSelectedItem();
		if (currentFilter.equals("Qualified"))
		{
			raceTableModel.setFilter("Qualified", 1);
		}
		else
		{
			raceTableModel.fireTableDataChanged();
		}

		selectRaceByName(aPC.getRace().getName());
	}

	//getRacePosition() doesn't allow for sorting, so we have to pick the selection manually
	private void selectRaceByName(String setRaceName)
	{
		raceTable.getSelectionModel().clearSelection();

		final int raceTableRowCount = raceTable.getRowCount();
		for (int row = 0; row < raceTableRowCount; row++)
		{
			final String raceName = (String)raceTable.getValueAt(row, 0);
			if (raceName.equals(setRaceName))
			{
				raceTable.getSelectionModel().setSelectionInterval(row, row);
				return;
			}
		}

		//
		// Race not found in list, select "none selected"
		//
		if (setRaceName.equals(Globals.s_NONESELECTED))
		{
			return;
		}
		selectRaceByName(Globals.s_NONESELECTED);
		Globals.getCurrentPC().setRace(Globals.s_EMPTYRACE);
	}

	public void pcChanged()
	{
		final PlayerCharacter aPC = Globals.getCurrentPC();
		if (Globals.isPurchaseStatMode())
			pool.setText(Integer.toString(aPC.getPoolAmount()));
		else
			pool.setText(Integer.toString(aPC.getRemainingPool()));
		alignment.setSelectedIndex(aPC.getAlignment());
		d_forceAmount.setText(aPC.getFPoints());
		d_dsideAmount.setText(aPC.getDPoints());
		selectRaceByName(aPC.getRace().getName());
		showAlignment();
		updatePool();
	}

	protected void showAlignment()
	{
		if (Globals.isStarWarsMode())
		{
			lblAlignment.setVisible(false);
			alignment.setVisible(false);
			d_forceLabel.setVisible(true);
			d_forceAmount.setVisible(true);
			d_dsideLabel.setVisible(true);
			d_dsideAmount.setVisible(true);
		}
		else if (Globals.isSidewinderMode())
		{
			lblAlignment.setVisible(false);
			alignment.setVisible(false);
			d_forceLabel.setVisible(false);
			d_forceAmount.setVisible(false);
			d_dsideLabel.setVisible(false);
			d_dsideAmount.setVisible(false);
		}
		else
		{
			lblAlignment.setVisible(true);
			alignment.setVisible(true);
			d_forceLabel.setVisible(false);
			d_forceAmount.setVisible(false);
			d_dsideLabel.setVisible(false);
			d_dsideAmount.setVisible(false);
		}
	}

	private void statTableMouseClicked(MouseEvent evt)
	{
		final PlayerCharacter aPC = Globals.getCurrentPC();
		int[] stats = aPC.getStats();
		final int selectedStat = statTable.getSelectedRow();
		int stat = stats[selectedStat];
		boolean makeChange = false;
		int increment = 0;

		switch (statTable.columnAtPoint(evt.getPoint()))
		{
			case 5:
				if (!Globals.s_ATTRIBROLL[selectedStat])
					return;

				increment = 1;
				if (aPC.totalLevels() < 1 && stat >= Globals.getInitialStatMax() && !Globals.isPurchaseStatMode())
					JOptionPane.showMessageDialog(null, "Cannot raise stat above " + new Integer(Globals.getInitialStatMax()).toString(), "PCGen", JOptionPane.ERROR_MESSAGE);
				else if ((Globals.isPurchaseStatMode() == false || (Globals.isPurchaseStatMode() && aPC.totalLevels() > 0)) && aPC.getPoolAmount() < 1 && !Globals.isStatPoolUnlimited())
					JOptionPane.showMessageDialog(null, "You have no pool points to spend.", "PCGen", JOptionPane.ERROR_MESSAGE);
				else if (aPC.totalLevels() < 1 && stat >= 18 && Globals.isPurchaseStatMode())
					JOptionPane.showMessageDialog(null, "Cannot raise stat above 18 in Purchase Mode", "PCGen", JOptionPane.ERROR_MESSAGE);
				else if (aPC.isNonability(selectedStat))
					JOptionPane.showMessageDialog(null, "Cannot increment a nonability", "PCGen", JOptionPane.ERROR_MESSAGE);
				else
				{
					makeChange = true;
					if (Globals.isPurchaseStatMode() == false || aPC.totalLevels() > 0)
						aPC.setPoolAmount(Math.max(aPC.getPoolAmount() - 1, 0));
				}
				break;
			case 6:
				if (!Globals.s_ATTRIBROLL[selectedStat])
					return;

				increment = -1;
				if (aPC.totalLevels() < 1 && stat <= 8 && Globals.isPurchaseStatMode())
					JOptionPane.showMessageDialog(null, "Cannot lower stat below 8 in Purchase Mode", "PCGen", JOptionPane.ERROR_MESSAGE);
				else if (aPC.totalLevels() < 1 && (stat <= Globals.getInitialStatMin() ||
					(stat <= 8 && Globals.isPurchaseStatMode())))
					JOptionPane.showMessageDialog(null, "Cannot lower stat below " + new Integer(Globals.getInitialStatMin()).toString(), "PCGen", JOptionPane.ERROR_MESSAGE);
				else if (aPC.isNonability(selectedStat))
					JOptionPane.showMessageDialog(null, "Cannot decrement a nonability", "PCGen", JOptionPane.ERROR_MESSAGE);
				else
				{
					makeChange = true;
					if (Globals.isPurchaseStatMode() == false || aPC.totalLevels() > 0)
						aPC.setPoolAmount(aPC.getPoolAmount() + 1);
				}
				break;
		}
		if (makeChange)
		{
			aPC.setDirty(true);
			stats[selectedStat] = stat + increment;
			aPC.setStats(stats);
			updatePool();
			statTableModel.fireTableRowsUpdated(selectedStat, selectedStat);
		}
	}

	private void updatePool()
	{
		final PlayerCharacter aPC = Globals.getCurrentPC();
		int[] stats = aPC.getStats();
		int stat = 0;
		String bString = "";
		if (Globals.isPurchaseStatMode())
		{
			if (aPC.totalLevels() == 0)
			{
				int i = 0;
				for (stat = 0; stat < Globals.s_ATTRIBLONG.length; stat++)
				{
					if (!Globals.s_ATTRIBROLL[stat])
						continue;
					if (stats[stat] > 8 && stats[stat] < 19)
					{
						i += Globals.getStatCost(stats[stat] - 9);
					}
				}
				if (aPC.totalLevels() == 0)
				{
					aPC.setDirty(true);
					aPC.setCostPool(i);
					aPC.setPoolAmount(i);
				}
			}
			bString = " (" + aPC.getCostPool() + ")";
		}
		String aString = String.valueOf(aPC.getPoolAmount());

		if (bString.length() > 0)
		{
			aString = aString + bString;
		}
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
		 * returns the number of rows. Gets the number of stats from Globals.s_ATTRIBLONG
		 */
		public int getRowCount()
		{
			if (Globals.getCurrentPC() != null)
			{
				return Globals.s_ATTRIBLONG.length;
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
				if ((rowIndex >= 0) && (rowIndex < Globals.s_ATTRIBLONG.length))
					return Globals.s_ATTRIBLONG[rowIndex];
				else
					return "Out of Bounds";
			}
			final PlayerCharacter aPC = Globals.getCurrentPC();
			switch (columnIndex)
			{
				case 1:
					return new Integer(aPC.getStat(rowIndex));
				case 2:
					if (aPC.isNonability(rowIndex))
						return "-";
					return new Integer(aPC.adjStats(rowIndex) - aPC.getStat(rowIndex));
				case 3:
					if (aPC.isNonability(rowIndex))
						return "--";
					return new Integer(aPC.adjStats(rowIndex));
				case 4:
					if (aPC.isNonability(rowIndex))
						return new Integer(0);
					return new Integer(aPC.calcStatMod(rowIndex));
				case 5:
					if (!Globals.s_ATTRIBROLL[rowIndex])
						return null;
					return "+";
				case 6:
					if (!Globals.s_ATTRIBROLL[rowIndex])
						return null;
					return "-";
				default:
					return "Out of Bounds";
			}
		}
	}


	protected class RaceTableModel extends AbstractTableModel
	{
		private String[] lastColValue = new String[9];
		private int lastRow = -1;
		private ArrayList raceList = new ArrayList(Globals.getRaceMap().values());

		public void fireTableDataChanged()
		{
			lastRow = -1;
			super.fireTableDataChanged();
		}

		public void setFilter(String filterID, int index)
		{
			final ArrayList races = new ArrayList(Globals.getRaceMap().values());
			Iterator i;

			if (filterID.equals("All"))
			{
				raceList = races;
			}
			else if (filterID.equals("Qualified"))
			{
				raceList = new ArrayList();
				final PlayerCharacter aPC = Globals.getCurrentPC();
				i = races.iterator();
				while (i.hasNext())
				{
					final Race theRace = (Race)i.next();
					if (theRace.passesPreReqTests())
					{
						raceList.add(theRace);
					}
				}
			}
			else if (index >= 2)
			{
				raceList = new ArrayList();
				i = races.iterator();
				while (i.hasNext())
				{
					final Race theRace = (Race)i.next();
					if (theRace.getType().equalsIgnoreCase(filterID))
						raceList.add(theRace);
				}
			}
			//
			// Make sure empty race is in all lists
			//
			if (!raceList.contains(Globals.s_EMPTYRACE))
			{
				raceList.add(0, Globals.s_EMPTYRACE);
			}
			fireTableDataChanged();
		}


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
			return raceList.size();
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
					return "PreReqs";
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
			if (raceList.size() != 0)
			{
				if ((columnIndex < 0) || (columnIndex > 9))
				{
					return "Out of Bounds";
				}

				//
				// Maintain a copy of the last data displayed for this row so we don't call the
				// functions any more than we need to. Otherwise, as you drag the mouse across a
				// cell we will repeatedly recalculate the cell contents.
				//
				if (rowIndex != lastRow)
				{
					lastColValue = new String[9];
					lastRow = rowIndex;
				}
				else if (lastColValue[columnIndex] != null)
				{
					return lastColValue[columnIndex];
				}

				final Race race = (Race)raceList.get(rowIndex);
				String sRet = "";
				switch (columnIndex)
				{
					case 0:
						sRet = race.toString();
						break;

					case 1:
						final StringBuffer aString = new StringBuffer();
						for (int i = 0; i < Globals.s_ATTRIBSHORT.length; i++)
						{
							if (race.isNonability(i))
							{
								if (aString.length() > 0)
								{
									aString.append(" ");
								}
								aString.append(Globals.s_ATTRIBSHORT[i]).append(":Nonability");
							}
							else
							{
								if (race.getStatMod(i) != 0)
								{
									if (aString.length() > 0)
									{
										aString.append(" ");
									}
									aString.append(Globals.s_ATTRIBSHORT[i]).append(":").append(race.getStatMod(i));
								}
							}
						}
						sRet = aString.toString();
						break;

					case 2:
						sRet = race.preReqHTMLStrings();
						break;

					case 3:
						sRet = race.getSize();
						break;

					case 4:
						if (race.getMovements() != null)
						{
							final StringBuffer movelabel = new StringBuffer(race.getMovementType(0)).append(" ").append(race.getMovement(0)).append(Globals.getAbbrMovementDisplay());
							for (int i = 1; i < race.getMovements().length; i++)
							{
								movelabel.append(race.getMovementType(i)).append(" ").append(race.getMovement(i)).append(Globals.getAbbrMovementDisplay());
							}
							sRet = movelabel.toString();
						}
						break;

					case 5:
						sRet = race.getVision();
						break;

					case 6:
						sRet = race.getFavoredClass();
						break;

					case 7:
						return new Integer(race.getLevelAdjustment());

					case 8:
						sRet = race.getSource();
						break;
				}
				lastColValue[columnIndex] = sRet;
				return sRet;
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
		String raceNamed = raceTable.getValueAt(raceTable.getSelectedRow(), 0).toString();
		if (raceNamed.equals(Globals.s_NONESELECTED))
		{
			JOptionPane.showMessageDialog(null, "You must select a race.", "PCGen", JOptionPane.ERROR_MESSAGE);
			return false;
		}
		else if (!Globals.isStarWarsMode() && !Globals.isSidewinderMode() && alignment.getSelectedItem().equals(Globals.s_NONESELECTED))
		{
			JOptionPane.showMessageDialog(null, "You must select an alignment.", "PCGen", JOptionPane.ERROR_MESSAGE);
			return false;
		}

		final Race r = Globals.getRaceNamed(raceNamed);
		if (!r.passesPreReqTests())
		{
			JOptionPane.showMessageDialog(null, "You are not qualified for the selected race.", "PCGen", JOptionPane.ERROR_MESSAGE);
			return false;
		}
		return true;
	}

}
