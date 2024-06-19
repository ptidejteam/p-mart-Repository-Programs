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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
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

	/** Creates new form <code>InfoStats<code>
	 */

	public InfoStats()
	{
		initComponents();
		int racePosition = getRacePosition();
		raceTable.getSelectionModel().setSelectionInterval(racePosition, racePosition);

		final int align = Globals.getCurrentPC().getAlignment();
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

	private int getRacePosition()
	{
		final String pcRaceName = Globals.getCurrentPC().getRace().getName();
		java.util.ArrayList array = new ArrayList(Globals.getRaceMap().values());
		//Getting race from global list, just in case the pcRace has been cloned...
		final int racePosition = array.indexOf(Globals.getRaceKeyed(pcRaceName));
		return racePosition;
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
					boolean conNonAbility = Globals.getCurrentPC().isNonability(2);
					int currentCon = Globals.getCurrentPC().adjStats(Globals.CONSTITUTION);
					int i = raceTable.getSelectedRow();
					if (i < 0)
						return;
					String raceNamed = raceTable.getValueAt(i, 0).toString();
					Race r = Globals.getRaceNamed(raceNamed);
					if (!r.equals(Globals.getCurrentPC().getRace()))
					{
						Globals.getCurrentPC().setRace(r);
						if (Globals.getCurrentPC().getRace().hitDice() != 0)
						{
							Globals.getCurrentPC().getRace().rollHp();
						}
						statTableModel.fireTableDataChanged();
					}
				}
			}
		});
		final int[] cols = {0,3,4,5,6,7,8};
		raceTable.setOptimalColumnWidths(cols);

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
		bRoll.setMnemonic('R');
		bRoll.setPreferredSize(new Dimension(75, 25));
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
		Globals.getCurrentPC().setDirty(true);
		Globals.getCurrentPC().rollStats(Globals.getRollMethod());
		updatePool();
		statTableModel.fireTableDataChanged();
	}

	private void zeroPool(ActionEvent evt)
	{
		Globals.getCurrentPC().setDirty(true);
		Globals.getCurrentPC().setPoolAmount(0);
		pool.setText(Integer.toString(Globals.getCurrentPC().getRemainingPool()));
		pcChanged();
	}

/* Added by TNC 08/27/01 */
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
		Globals.getCurrentPC().setAlignment(alignment.getSelectedIndex(), false);
	}

	public void pcChanged()
	{
		if (Globals.isPurchaseStatMode())
			pool.setText(Integer.toString(Globals.getCurrentPC().getPoolAmount()));
		else
			pool.setText(Integer.toString(Globals.getCurrentPC().getRemainingPool()));
		alignment.setSelectedIndex(Globals.getCurrentPC().getAlignment());
		d_forceAmount.setText(Globals.getCurrentPC().getFPoints());
		d_dsideAmount.setText(Globals.getCurrentPC().getDPoints());
		//getRacePosition() doesn't allow for sorting, so we have to pick thr selection manually
		for (int row = 1; row < raceTable.getRowCount(); row++)
		{
			final String raceName = (String)raceTable.getValueAt(row, 0);
			if (raceName.equals(Globals.getCurrentPC().getRace().getName()))
			{
				raceTable.getSelectionModel().setSelectionInterval(row, row);
				break;
			}
		}
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
		int[] stats = Globals.getCurrentPC().getStats();
		int stat = stats[statTable.getSelectedRow()];
		boolean makeChange = false;
		int increment = 0;

		switch (statTable.columnAtPoint(evt.getPoint()))
		{
			case 5:
				increment = 1;
				if (Globals.getCurrentPC().totalLevels() < 1 && stat >= Globals.getInitialStatMax() && !Globals.isPurchaseStatMode())
					JOptionPane.showMessageDialog(null, "Cannot raise stat above " + new Integer(Globals.getInitialStatMax()).toString(), "PCGen", JOptionPane.ERROR_MESSAGE);
				else if ((Globals.isPurchaseStatMode() == false || (Globals.isPurchaseStatMode() && Globals.getCurrentPC().totalLevels() > 0)) && Globals.getCurrentPC().getPoolAmount() < 1 && !Globals.isStatPoolUnlimited())
					JOptionPane.showMessageDialog(null, "You have no pool points to spend.", "PCGen", JOptionPane.ERROR_MESSAGE);
				else if (Globals.getCurrentPC().totalLevels() < 1 && stat >= 18 && Globals.isPurchaseStatMode())
					JOptionPane.showMessageDialog(null, "Cannot raise stat above 18 in Purchase Mode", "PCGen", JOptionPane.ERROR_MESSAGE);
				else if (Globals.getCurrentPC().isNonability(statTable.getSelectedRow()))
					JOptionPane.showMessageDialog(null, "Cannot increment a nonability", "PCGen", JOptionPane.ERROR_MESSAGE);
				else
				{
					makeChange = true;
					if (Globals.isPurchaseStatMode() == false || Globals.getCurrentPC().totalLevels() > 0)
						Globals.getCurrentPC().setPoolAmount(Math.max(Globals.getCurrentPC().getPoolAmount() - 1, 0));
				}
				break;
			case 6:
				increment = -1;
				if (Globals.getCurrentPC().totalLevels() < 1 && stat <= 8 && Globals.isPurchaseStatMode())
					JOptionPane.showMessageDialog(null, "Cannot lower stat below 8 in Purchase Mode", "PCGen", JOptionPane.ERROR_MESSAGE);
				else if (Globals.getCurrentPC().totalLevels() < 1 && (stat <= Globals.getInitialStatMin() ||
					(stat <= 8 && Globals.isPurchaseStatMode())))
					JOptionPane.showMessageDialog(null, "Cannot lower stat below " + new Integer(Globals.getInitialStatMin()).toString(), "PCGen", JOptionPane.ERROR_MESSAGE);
				else if (Globals.getCurrentPC().isNonability(statTable.getSelectedRow()))
					JOptionPane.showMessageDialog(null, "Cannot decrement a nonability", "PCGen", JOptionPane.ERROR_MESSAGE);
				else
				{
					makeChange = true;
					if (Globals.isPurchaseStatMode() == false || Globals.getCurrentPC().totalLevels() > 0)
						Globals.getCurrentPC().setPoolAmount(Globals.getCurrentPC().getPoolAmount() + 1);
				}
				break;
		}
		if (makeChange)
		{
			Globals.getCurrentPC().setDirty(true);
			stats[statTable.getSelectedRow()] = stat + increment;
//			if (statTable.getValueAt(statTable.getSelectedRow(), 0).toString().equals("Constitution"))
//			{
//				// when Con is adjusted, HP for all levels may be affected
//				// if Con is added and even, then add 1 hp to every level
//				// if Con is subtracted and odd, then sub 1 hp to every level
//				if ((increment > 0 && (stat + increment) % 2 == 0) || (increment < 0 && (stat + increment) % 2 != 0))
//				{
//					Globals.getCurrentPC().adjustHpRolls(increment);
//				}
//			}
			Globals.getCurrentPC().setStats(stats);
			updatePool();
			statTableModel.fireTableRowsUpdated(statTable.getSelectedRow(), statTable.getSelectedRow());
		}
	}

	private void updatePool()
	{
		int[] stats = Globals.getCurrentPC().getStats();
		int stat = 0;
		String bString = "";
		if (Globals.isPurchaseStatMode())
		{
			if (Globals.getCurrentPC().totalLevels() == 0)
			{
				int i = 0;
				for (stat = 0; stat < 6; stat++)
					if (stats[stat] > 8 && stats[stat] < 19)
						i += new Integer(Globals.getStatCost()[stats[stat] - 9]).intValue();
				if (Globals.getCurrentPC().totalLevels() == 0)
				{
					Globals.getCurrentPC().setDirty(true);
					Globals.getCurrentPC().setCostPool(i);
					Globals.getCurrentPC().setPoolAmount(i);
				}
			}
			bString = " (" + new Integer(Globals.getCurrentPC().getCostPool()).toString() + ")";
		}
		String aString = new Integer(Globals.getCurrentPC().getPoolAmount()).toString();
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
			if (Globals.getCurrentPC() != null)
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
			final PlayerCharacter aPC = Globals.getCurrentPC();
			switch (columnIndex)
			{
				case 1:
					return new Integer(aPC.getStats()[rowIndex]);
				case 2:
					if (aPC.isNonability(rowIndex))
						return "-";
					return new Integer(aPC.adjStats(rowIndex) - aPC.getStats()[rowIndex]);
				case 3:
					if (aPC.isNonability(rowIndex))
						return "--";
					return new Integer(aPC.adjStats(rowIndex));
				case 4:
					if (aPC.isNonability(rowIndex))
						return new Integer(0);
					return new Integer(aPC.calcStatMod(rowIndex));
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
			if (Globals.getRaceMap() != null)
			{
				return Globals.getRaceMap().size();
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
			if (Globals.getRaceMap() != null)
			{
				Map raceMap = Globals.getRaceMap();
				ArrayList raceList = new ArrayList(raceMap.values());
				Race race = (Race)raceList.get(rowIndex);
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
								aString = aString + race.s_STATNAMES.substring(i * 3, i * 3 + 3) + ":Nonability";
							}
							else
							{
								if (race.getStatMod(i) != 0)
								{
									if (aString.length() > 0)
										aString = aString + " ";
									aString = aString + race.s_STATNAMES.substring(i * 3, i * 3 + 3) + ":" + new Integer(race.getStatMod(i)).toString();
								}
							}
						return aString;
					case 2:
						return race.preReqStrings();
					case 3:
						return race.getSize();
					case 4:
						if (race.getMovements() != null)
						{
							String movelabel = race.getMovementType(0) + " " + race.getMovement(0).toString() + "'";
							for (int x = 1; x < race.getMovements().length; x++)
								movelabel += ", " + race.getMovementType(x) + " " + race.getMovement(x).toString() + "'";
							return movelabel;
						}
						return "";
					case 5:
						return race.getVision();
					case 6:
						return race.getFavoredClass();
					case 7:
						return new Integer(race.getLevelAdjustment());
					case 8:
						return race.getSource();
				}
			}
			return null; // compiler error, so I need to return something.
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

		Race r = Globals.getRaceNamed(raceNamed);
		if (!r.passesPreReqTests())
		{
			JOptionPane.showMessageDialog(null, "You are not qualified for the selected race.", "PCGen", JOptionPane.ERROR_MESSAGE);
			return false;
		}
		return true;
	}

}
