/*
 * InfoAbilities.java
 * Copyright 2002 (C) Bryan McRoberts
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
 * Modified June 5, 2001 by Bryan McRoberts (merton_monk@yahoo.com)
 */

/**
 * This class is responsible for drawing Stat, Special Ability and Language sections.
 */


/**
 * @author  Bryan McRoberts (merton_monk@yahoo.com)
 * @version $Revision: 1.1 $
 */
package pcgen.gui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;
import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import pcgen.core.Globals;
import pcgen.core.PlayerCharacter;

public class InfoAbilities extends JPanel
{
	static boolean needsUpdate = true;
	private JTableEx statTable;
	protected StatTableModel statTableModel = new StatTableModel();
	protected RendererEditor plusMinusRenderer = new RendererEditor();
	private JLabel lblPool = new JLabel("Pool:");
	private JLabel lblStatMin = new JLabel("Stat Min:");
	private JLabel lblStatMax = new JLabel("Stat Max:");
	private WholeNumberField statMinText = new WholeNumberField(0, 4);
	private WholeNumberField statMaxText = new WholeNumberField(0, 4);
	private JLabel lblForcePoints = new JLabel("Force Points:");
	private JLabel lblDarkSidePoints = new JLabel("Dark Side Points:");
	private JButton rollButton = new JButton("Roll");
	private JButton zeroButton = new JButton("Zero");
	private JButton languageButton = new JButton("Choose");
	private JCheckBox purchaseCheckBox = new JCheckBox("Purchase Mode:");
	private JCheckBox unlimitedCheckBox = new JCheckBox("Unlimited Stat Pool:");
	private JTextField poolText = new JTextField();
	private JTextField forceText = new JTextField();
	private JTextField darkText = new JTextField();
	private static PlayerCharacter aPC = Globals.getCurrentPC();
	private JTextArea languageText = new JTextArea();
	private JTextArea saText = new JTextArea();
	private static final String s_none = "None";
	private static final String s_delim = ", ";


	public InfoAbilities()
	{
		initComponents();
	}

	public boolean isFocusTraversable()
	{
		return true;
	}

	//Set up GridBag Constraints
	private void buildConstraints(GridBagConstraints gbc, int gx, int gy, int gw, int gh, double wx, double wy)
	{
		gbc.gridx = gx;
		gbc.gridy = gy;
		gbc.gridwidth = gw;
		gbc.gridheight = gh;
		gbc.weightx = wx;
		gbc.weighty = wy;
	}

	private void initComponents()
	{
		GridBagConstraints gbc = new GridBagConstraints();
		GridBagLayout gridbag = new GridBagLayout();
		gbc.fill = GridBagConstraints.BOTH;
		gbc.anchor = GridBagConstraints.NORTH;
		setLayout(gridbag);

		JScrollPane statScrollPane = new JScrollPane();
		statTable = new JTableEx();

		statTable.setModel(statTableModel);
		statTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		statTable.addMouseListener(new MouseAdapter()
		{
			public void mouseClicked(MouseEvent evt)
			{
				statTableMouseClicked(evt);
			}
		});
		TableColumn col = statTable.getColumnModel().getColumn(0);
		col.setMinWidth(50);
		col = statTable.getColumnModel().getColumn(1);
		col.setMinWidth(40);
		col = statTable.getColumnModel().getColumn(2);
		col.setMinWidth(30);
		col = statTable.getColumnModel().getColumn(3);
		col.setMinWidth(40);
		col = statTable.getColumnModel().getColumn(4);
		col.setMinWidth(30);
		col = statTable.getColumnModel().getColumn(5);
		col.setCellRenderer(plusMinusRenderer);
		col.setMaxWidth(30);
		col.setMinWidth(30);
		col = statTable.getColumnModel().getColumn(6);
		col.setCellRenderer(plusMinusRenderer); 
		col.setMaxWidth(30);
		col.setMinWidth(30);

		statScrollPane.setViewportView(statTable);

		buildConstraints(gbc, 0, 0, 1, 4, .7, 0);
		gridbag.setConstraints(statScrollPane, gbc);
		add(statScrollPane);

		JPanel subPanel = new JPanel();
		subPanel.setLayout(new FlowLayout());
		poolText.setPreferredSize(new Dimension(60, 15));
		subPanel.add(lblPool);
		subPanel.add(poolText);
		subPanel.add(rollButton);
		subPanel.add(zeroButton);
		buildConstraints(gbc, 1, 0, 1, 1, .3, .1);
		gridbag.setConstraints(subPanel, gbc);
		rollButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				characterRolled();
				PCGen_Frame1.getStatusBar().setText("Change Roll Methods under Options");
			}
		});
		zeroButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				zeroPool();
			}
		});
		add(subPanel);

		subPanel = new JPanel();
		subPanel.setLayout(new FlowLayout());
		subPanel.add(lblForcePoints);
		forceText.setPreferredSize(new Dimension(30, 15));
		darkText.setPreferredSize(new Dimension(30, 15));
		subPanel.add(forceText);
		subPanel.add(lblDarkSidePoints);
		subPanel.add(darkText);
		buildConstraints(gbc, 1, 1, 1, 1, 0, .1);
		gridbag.setConstraints(subPanel, gbc);
		forceText.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				updateCharacterForce();
			}
		});

		forceText.addFocusListener(new FocusAdapter()
		{
			public void focusLost(FocusEvent evt)
			{
				updateCharacterForce();
			}
		});
		darkText.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				updateCharacterDside();
			}
		});

		darkText.addFocusListener(new FocusAdapter()
		{
			public void focusLost(FocusEvent evt)
			{
				updateCharacterDside();
			}
		});

		add(subPanel);

		subPanel = new JPanel();
		subPanel.setLayout(new FlowLayout());
		subPanel.add(purchaseCheckBox);
		subPanel.add(unlimitedCheckBox);
		buildConstraints(gbc, 1, 2, 1, 1, 0, .1);
		gridbag.setConstraints(subPanel, gbc);
		purchaseCheckBox.setToolTipText("Buy stats using a point system");
		purchaseCheckBox.setSelected(Globals.isPurchaseStatMode());
		purchaseCheckBox.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				Globals.setPurchaseStatMode(purchaseCheckBox.isSelected());
				PCGen_Frame1.getStatusBar().setText("You may alter the costs under the Options Menu.");
			}
		});
		unlimitedCheckBox.setToolTipText("Allow unlimited stat editing");
		unlimitedCheckBox.setSelected(Globals.isStatPoolUnlimited());
		unlimitedCheckBox.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				Globals.setStatPoolUnlimited(unlimitedCheckBox.isSelected());
			}
		});
		add(subPanel);

		subPanel = new JPanel();
		subPanel.setLayout(new FlowLayout());
		statMinText.setPreferredSize(new Dimension(30, 15));
		statMaxText.setPreferredSize(new Dimension(30, 15));
		subPanel.add(lblStatMin);
		subPanel.add(statMinText);
		subPanel.add(lblStatMax);
		subPanel.add(statMaxText);
		statMinText.setText(Integer.toString(Globals.getInitialStatMin()));
		statMinText.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				Object source = evt.getSource();
				updateTextFields(source);
			}
		});
		statMinText.addFocusListener(new FocusAdapter()
		{
			public void focusLost(FocusEvent evt)
			{
				Object source = evt.getSource();
				updateTextFields(source);
			}
		});
		statMaxText.setText(Integer.toString(Globals.getInitialStatMax()));
		statMaxText.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				Object source = evt.getSource();
				updateTextFields(source);
			}
		});
		statMaxText.addFocusListener(new FocusAdapter()
		{
			public void focusLost(FocusEvent evt)
			{
				Object source = evt.getSource();
				updateTextFields(source);
			}
		});

		buildConstraints(gbc, 1, 3, 1, 1, 0, .1);
		gridbag.setConstraints(subPanel, gbc);
		add(subPanel);

		subPanel = new JPanel();
		subPanel.setLayout(new FlowLayout());
		JLabel lblLanguage = new JLabel("Languages:");
		subPanel.add(lblLanguage);
		JButton langButton = new JButton("Choose");
		subPanel.add(langButton);
		buildConstraints(gbc, 0, 4, 2, 1, 0, .1);
		gridbag.setConstraints(subPanel, gbc);
		add(subPanel);
		langButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				languageSelectPressed(evt);
			}
		});


		JScrollPane languageScroll = new JScrollPane();
		languageText.setLineWrap(true);
		languageText.setWrapStyleWord(true);
		languageText.setEditable(false);
		languageScroll.setViewportView(languageText);
		buildConstraints(gbc, 0, 5, 2, 1, 0, .2);
		gridbag.setConstraints(languageScroll, gbc);
		add(languageScroll);

		subPanel = new JPanel();
		subPanel.setLayout(new FlowLayout());
		JLabel lblSA = new JLabel("Special Abilities:");
		subPanel.add(lblSA);
		buildConstraints(gbc, 0, 6, 2, 1, 0, .1);
		gridbag.setConstraints(subPanel, gbc);
		add(subPanel);

		JScrollPane saScroll = new JScrollPane();
		saText.setLineWrap(true);
		saText.setWrapStyleWord(true);
		saText.setEditable(false);
		saScroll.setViewportView(saText);
		buildConstraints(gbc, 0, 7, 2, 1, 0, .2);
		gridbag.setConstraints(saScroll, gbc);
		add(saScroll);

		addComponentListener(new ComponentAdapter()
		{
			public void componentShown(ComponentEvent evt)
			{
				formComponentShown(evt);
			}
		});
	}

	private final void updateTextFields(Object source)
	{
		if (source == statMinText)
		{
			try
			{
				Globals.setInitialStatMin((Integer.parseInt(statMinText.getText())));
				Options.initializeStatRollingMethod8();
			}
			catch (NumberFormatException nfe)
			{
				statMinText.setText(Integer.toString(Globals.getInitialStatMin()));
			}
		}
		else if (source == statMaxText)
		{
			try
			{
				Globals.setInitialStatMax(Integer.parseInt(statMaxText.getText()));
				Options.initializeStatRollingMethod8();
			}
			catch (NumberFormatException nfe)
			{
				statMaxText.setText(Integer.toString(Globals.getInitialStatMax()));
			}
		}
	}

	// This is called when the tab is shown.
	private void formComponentShown(ComponentEvent evt)
	{
		requestFocus();
		if (Globals.isStarWarsMode())
		{
			lblForcePoints.setVisible(true);
			forceText.setVisible(true);
			lblDarkSidePoints.setVisible(true);
			darkText.setVisible(true);
		}
		else
		{
			lblForcePoints.setVisible(false);
			forceText.setVisible(false);
			lblDarkSidePoints.setVisible(false);
			darkText.setVisible(false);
		}
		PCGen_Frame1.getStatusBar().setText("Level 0 characters cannot raise stats " +
			"above the StatMax value. Click on Choose if you have any bonus langauges to select.");
		updateCharacterInfo();
	}


	private void characterRolled()
	{
		aPC.setDirty(true);
		aPC.rollStats(Globals.getRollMethod());
		updatePool();
		statTableModel.fireTableDataChanged();
	}

	private void zeroPool()
	{
		aPC.setDirty(true);
		aPC.setPoolAmount(0);
		poolText.setText(Integer.toString(aPC.getRemainingPool()));
		updateCharacterInfo();
	}

	/** <code>updateCharacterInfo</code> update data listening for a changed PC
	 */
	public void updateCharacterInfo()
	{
		aPC = Globals.getCurrentPC();
		ArrayList specialAbilities = aPC.getSpecialAbilityTimesList();
		SortedSet languages = aPC.getLanguagesList();
		if (specialAbilities.size() > 0)
		{
			saText.setText
				(pcgen.core.Utility.stringForList(specialAbilities.iterator(), s_delim));
		}
		else
		{
			saText.setText(s_none);
		}
		if (languages.size() > 0)
		{
			languageText.setText
				(pcgen.core.Utility.stringForList(languages.iterator(), s_delim));
		}
		else
		{
			languageText.setText(s_none);
		}
		if (Globals.isPurchaseStatMode())
			poolText.setText(Integer.toString(aPC.getPoolAmount()));
		else
			poolText.setText(Integer.toString(aPC.getRemainingPool()));
		forceText.setText(aPC.getFPoints());
		darkText.setText(aPC.getDPoints());
		updatePool();
	}

	private void languageSelectPressed(ActionEvent evt)
	{
		if (aPC != null)
		{
			aPC.setDirty(true);
			Chooser lc = new Chooser();
			lc.setVisible(false);
			SortedSet autoLangs = aPC.getAutoLanguages();
			SortedSet langs = aPC.getLanguagesList();
			ArrayList selected = new ArrayList(langs.size());
			for (Iterator i = langs.iterator(); i.hasNext();)
			{
				String lang = i.next().toString();
				if (!autoLangs.contains(lang))
					selected.add(lang);
			}
			Collections.sort(selected);
			lc.setSelectedList(selected);
			lc.setPool(aPC.languageNum() - selected.size());
			SortedSet bonusLangs = new TreeSet();
			for (Iterator i = aPC.getBonusLanguages().iterator(); i.hasNext();)
			{
				bonusLangs.add(i.next().toString());
			}
			lc.setAvailableList(new ArrayList(bonusLangs));
			lc.show();
			langs.clear();
			aPC.getAutoLanguages();
			langs.addAll(lc.getSelectedList());
			updateCharacterInfo();
		}
	}


	private final void updateCharacterForce()
	{
		if (aPC != null)
		{
			try
			{
				String forceAmt = forceText.getText();
				if (!forceAmt.equals(aPC.getFPoints()))
				{
					System.out.println("Sensitive? " + sensitiveCheck());
					if (sensitiveCheck())
					{
						aPC.setFPoints(forceAmt);
						aPC.setDirty(true);
					}
					else
					{
						aPC.setFPoints(forceAmt);
						aPC.setDirty(true);
						if (Integer.parseInt(forceAmt) > 5)
						{
							aPC.setFPoints("5");
							forceText.setText("5");
							aPC.setDirty(true);
							System.out.println("not sensitive/set to 5");
						}
					}
				}
			}
			catch (NumberFormatException nfe)
			{
				forceText.setText(aPC.getFPoints());
			}
		}
	}

	private boolean sensitiveCheck()
	{
		final String sens = "Force Sensitive";
		return (aPC.hasFeat(sens) || aPC.hasFeatAutomatic(sens) ||
			aPC.hasFeatVirtual(sens));
	}

	private final void updateCharacterDside()
	{
		if (aPC != null)
		{
			try
			{
				final String dsideAmt = darkText.getText();
				if (!dsideAmt.equals(aPC.getDPoints()))
				{
					aPC.setDPoints(dsideAmt);
					aPC.setDirty(true);
				}
			}
			catch (NumberFormatException nfe)
			{
				darkText.setText(aPC.getDPoints());
			}
		}
	}

	private void statTableMouseClicked(MouseEvent evt)
	{
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
			InfoSkills.needsUpdate = true; //if INT changed then skill points need to be recalculated
		}
	}

	private void updatePool()
	{
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
		poolText.setText(aString);
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

	protected class RendererEditor implements TableCellRenderer
	{
		JButton plusButton = new JButton("+");
		JButton minusButton = new JButton("-");
		DefaultTableCellRenderer def = new DefaultTableCellRenderer();

		public RendererEditor()
		{
			def.setBackground(InfoAbilities.this.getBackground());
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
}
