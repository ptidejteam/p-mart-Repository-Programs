/*
 * InfoClasses.java
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
 * Created on April 19, 2001, 7:36 PM
 */
package pcgen.gui;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.ArrayList;
import java.util.Iterator;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;
import pcgen.core.Globals;
import pcgen.core.PCClass;
import pcgen.core.PlayerCharacter;

/**
 * <code>InfoClasses</code>
 *
 * @author  Peter Kahle <pkahle@pobox.com>
 * @version $Revision: 1.1 $
 */
public class InfoClasses extends JPanel
{

	// Variables declaration
	private JScrollPane allClassesPane;
	private JTableEx allClassesTable;
	private JScrollPane currentClassesPane;
	private JTableEx currentClassesTable;
	private JPanel jPanel1;
	private JLabel lblHP;
	private JLabel lblFort;
	private JLabel lblReflex;
	private JLabel lblWill;
	private JLabel lblFeats;
	private JLabel lblSkills;
	private JLabel lblBAB;
	private JLabel lblDefense;
	private JLabel lblWoundPoints;
	private JLabel lReflex;
	private JLabel lWill;
	private JLabel featCount;
	private JLabel skillCount;
	private JLabel lBAB;
	private JLabel lDefense;
	private JLabel lWP;
//	private WholeNumberField tHp;
	private JLabel lblHp;
	private JLabel lblReputation;
	private JLabel lReputation;
	private JLabel lFortitude;
	private JPanel jPanel2;
	private JButton jButtonMinus;
	private JButton jButtonPlus;
	private JLabel jLabelFilter;
	private JComboBox jcbFilter;
	private JLabel lblExperience;
	private WholeNumberField experience;
	private JButton jButtonHP;
	/** Instantiated popup frame {@link HPFrame}. */
	HPFrame hpFrame = null;

	protected AllClassesTableModel allClassesDataModel = new AllClassesTableModel();
	protected TableSorter sortedAllClassesModel = new TableSorter();
	protected PCClassesTableModel currentClassesDataModel = new PCClassesTableModel();
	protected TableSorter sortedCurrentClassesModel = new TableSorter();
	private JLabel lMaxHPPct;
	private WholeNumberField tMaxHPPct;
	private JCheckBox levelCap;

	/**
	 *
	 */
	private static final String[] ALL_CLASSES_COLUMN_NAMES = new String[]{"Q", "Name", "BAB", "HD",
		"Fort", "Reflex", "Will", "PreReqs",
		"Defense", "Reputation", "Spell Type",
		"Base Stat", "Source File"};
	/**
	 *
	 */
	private static final int FILTER_ALL = 0;
	/**
	 *
	 */
	private static final int FILTER_QUALIFIED = 1;
	/**
	 *
	 */
	private static final int FILTER_BASE = 2;
	/**
	 *
	 */
	private static final int FILTER_PRESTIGE = 3;
	private static final int FILTER_PC = 4;
	private static final int FILTER_NPC = 5;
	private static final int FILTER_MONSTER = 6;
	/** the list from which to pull the classes to use. */
	private ArrayList displayClasses = Globals.getClassList();

	/** Creates new form InfoClasses */
	public InfoClasses()
	{
		initComponents();
		pcChanged();
		allClassesDataModel.setFilter(jcbFilter.getSelectedIndex());
	}

	public void paint(Graphics g)
	{
		super.paint(g);
		pcChanged();
	}

	/** This method is called from within the constructor to
	 * initialize the form.
	 */
	private void initComponents()
	{
		allClassesPane = new JScrollPane();
		sortedAllClassesModel.setModel(allClassesDataModel);
		allClassesTable = new JTableEx();
		currentClassesPane = new JScrollPane();
		sortedCurrentClassesModel.setModel(currentClassesDataModel);
		currentClassesTable = new JTableEx();
		sortedCurrentClassesModel.addMouseListenerToHeaderInTable(currentClassesTable);

		jPanel1 = new JPanel();
		lblHP = new JLabel();
		lMaxHPPct = new JLabel();
		lblFort = new JLabel();
		lblReflex = new JLabel();
		lblWill = new JLabel();
		lblFeats = new JLabel();
		lblSkills = new JLabel();
		lblBAB = new JLabel();
		lblDefense = new JLabel();
		lblWoundPoints = new JLabel();
		lReflex = new JLabel();
		lWill = new JLabel();
		featCount = new JLabel();
		skillCount = new JLabel();
		lBAB = new JLabel();
		lDefense = new JLabel();
		lWP = new JLabel();
//		tHp = new WholeNumberField();
		lblHp = new JLabel();
		tMaxHPPct = new WholeNumberField();
		lblReputation = new JLabel();
		lReputation = new JLabel();
		lFortitude = new JLabel();
		jPanel2 = new JPanel();
		jButtonMinus = new JButton();
		jButtonPlus = new JButton();
		jLabelFilter = new JLabel();
		jcbFilter = new JComboBox();
		lblExperience = new JLabel();
		experience = new WholeNumberField();
		levelCap = new JCheckBox("Ignore Level Cap");
		jButtonHP = new JButton();

		setLayout(new GridBagLayout());
		GridBagConstraints gridBagConstraints1;

		addComponentListener(new ComponentAdapter()
		{
			public void componentShown(ComponentEvent evt)
			{
				final PlayerCharacter aPC = Globals.getCurrentPC();
				aPC.setAggregateFeatsStable(false);
				aPC.setAutomaticFeatsStable(false);
				aPC.setVirtualFeatsStable(false);
				formComponentShown(evt);
			}
		});

		allClassesPane.setMinimumSize(new Dimension(220, 300));
		allClassesTable.setModel(sortedAllClassesModel);
		//allClassesTable.setDoubleBuffered(false);
		allClassesTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		allClassesTable.setShowVerticalLines(false);
		allClassesTable.setMinimumSize(new Dimension(200, 240));
		allClassesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		final int[] cols = {0,1,2,3,4,5,6,8,9,10};
		allClassesTable.setOptimalColumnWidths(cols);
		TableColumn col = allClassesTable.getColumnModel().getColumn(0);
		col.setMaxWidth(15);
		col.setMinWidth(15);
		col = allClassesTable.getColumnModel().getColumn(2);
		col.setMaxWidth(45);
		col = allClassesTable.getColumnModel().getColumn(4);
		col.setMaxWidth(60);
		col = allClassesTable.getColumnModel().getColumn(5);
		col.setMaxWidth(60);
		col = allClassesTable.getColumnModel().getColumn(6);
		col.setMaxWidth(60);
		sortedAllClassesModel.addMouseListenerToHeaderInTable(allClassesTable);

		allClassesPane.setViewportView(allClassesTable);

		gridBagConstraints1 = new GridBagConstraints();
		gridBagConstraints1.gridx = 0;
		gridBagConstraints1.gridy = 0;
		gridBagConstraints1.gridwidth = 3;
		gridBagConstraints1.gridheight = 5;
		gridBagConstraints1.fill = GridBagConstraints.BOTH;
		gridBagConstraints1.ipadx = 17;
		gridBagConstraints1.anchor = GridBagConstraints.SOUTH;
		gridBagConstraints1.weightx = 2.0;
		gridBagConstraints1.weighty = 25.0;
		add(allClassesPane, gridBagConstraints1);

		currentClassesTable.setModel(sortedCurrentClassesModel);
		currentClassesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		currentClassesTable.setDoubleBuffered(false);
		currentClassesPane.setViewportView(currentClassesTable);

		gridBagConstraints1 = new GridBagConstraints();
		gridBagConstraints1.gridx = 0;
		gridBagConstraints1.gridy = 5;
		gridBagConstraints1.gridheight = 7;
		gridBagConstraints1.fill = GridBagConstraints.BOTH;
		gridBagConstraints1.weightx = 0.5;
		gridBagConstraints1.weighty = 0.84;
		add(currentClassesPane, gridBagConstraints1);

		jPanel1.setLayout(new GridBagLayout());
		GridBagConstraints gridBagConstraints2;


		jButtonHP.setText("HP");
		jButtonHP.setAlignmentY(0.0F);
		jButtonHP.setHorizontalAlignment(SwingConstants.LEFT);
		jButtonHP.addActionListener(
			new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					if (hpFrame == null)
					{
						hpFrame = new HPFrame();
					}
					hpFrame.setPSize();
					hpFrame.pack();
					hpFrame.setVisible(true);
				}
			});
		gridBagConstraints2 = new GridBagConstraints();
		gridBagConstraints2.gridx = GridBagConstraints.RELATIVE;
		gridBagConstraints2.gridy = 0;
		gridBagConstraints2.insets = new Insets(0, 0, 0, 5);
		gridBagConstraints2.anchor = GridBagConstraints.EAST;
		gridBagConstraints2.weightx = 0.3;
		gridBagConstraints2.weighty = 1.0;
		jPanel1.add(jButtonHP, gridBagConstraints2);


		lblFort.setText("Fort");
		lblFort.setForeground(Color.black);
		gridBagConstraints2 = new GridBagConstraints();
		gridBagConstraints2.gridx = 2;
		gridBagConstraints2.gridy = 1;
		gridBagConstraints2.anchor = GridBagConstraints.EAST;
		gridBagConstraints2.weightx = 0.3;
		gridBagConstraints2.weighty = 1.0;
		jPanel1.add(lblFort, gridBagConstraints2);

		lblReflex.setText("Reflex");
		lblReflex.setForeground(Color.black);
		gridBagConstraints2 = new GridBagConstraints();
		gridBagConstraints2.gridx = 2;
		gridBagConstraints2.gridy = 2;
		gridBagConstraints2.anchor = GridBagConstraints.EAST;
		gridBagConstraints2.weighty = 1.0;
		jPanel1.add(lblReflex, gridBagConstraints2);

		lblWill.setText("Will");
		lblWill.setForeground(Color.black);
		gridBagConstraints2 = new GridBagConstraints();
		gridBagConstraints2.gridx = 2;
		gridBagConstraints2.gridy = 3;
		gridBagConstraints2.anchor = GridBagConstraints.EAST;
		gridBagConstraints2.weighty = 1.0;
		jPanel1.add(lblWill, gridBagConstraints2);

		lblFeats.setText("Feats");
		lblFeats.setForeground(Color.black);
		gridBagConstraints2 = new GridBagConstraints();
		gridBagConstraints2.gridx = 0;
		gridBagConstraints2.gridy = 1;
		gridBagConstraints2.anchor = GridBagConstraints.EAST;
		gridBagConstraints2.weighty = 1.0;
		jPanel1.add(lblFeats, gridBagConstraints2);

		lblSkills.setText("Skills");
		lblSkills.setForeground(Color.black);
		gridBagConstraints2 = new GridBagConstraints();
		gridBagConstraints2.gridx = 0;
		gridBagConstraints2.gridy = 2;
		gridBagConstraints2.anchor = GridBagConstraints.EAST;
		gridBagConstraints2.weighty = 1.0;
		jPanel1.add(lblSkills, gridBagConstraints2);

		lblBAB.setText("BAB");
		lblBAB.setForeground(Color.black);
		gridBagConstraints2 = new GridBagConstraints();
		gridBagConstraints2.gridx = 0;
		gridBagConstraints2.gridy = 3;
		gridBagConstraints2.anchor = GridBagConstraints.EAST;
		gridBagConstraints2.weighty = 1.0;
		jPanel1.add(lblBAB, gridBagConstraints2);

		lblDefense.setText("Defense");
		lblDefense.setForeground(Color.black);
		gridBagConstraints2 = new GridBagConstraints();
		gridBagConstraints2.gridx = 0;
		gridBagConstraints2.gridy = 4;
		gridBagConstraints2.anchor = GridBagConstraints.EAST;
		gridBagConstraints2.weighty = 1.0;
		jPanel1.add(lblDefense, gridBagConstraints2);

		lblWoundPoints.setText("Wound Points");
		lblWoundPoints.setForeground(Color.black);
		gridBagConstraints2 = new GridBagConstraints();
		gridBagConstraints2.gridx = 2;
		gridBagConstraints2.gridy = 5;
		gridBagConstraints2.insets = new Insets(0, 10, 0, 0);
		gridBagConstraints2.anchor = GridBagConstraints.EAST;
		gridBagConstraints2.weighty = 1.0;
		jPanel1.add(lblWoundPoints, gridBagConstraints2);

		lReflex.setText("+1");
		lReflex.setForeground(Color.black);
		gridBagConstraints2 = new GridBagConstraints();
		gridBagConstraints2.gridx = 3;
		gridBagConstraints2.gridy = 2;
		gridBagConstraints2.insets = new Insets(0, 4, 0, 4);
		gridBagConstraints2.anchor = GridBagConstraints.EAST;
		jPanel1.add(lReflex, gridBagConstraints2);

		lWill.setText("+2");
		lWill.setForeground(Color.black);
		gridBagConstraints2 = new GridBagConstraints();
		gridBagConstraints2.gridx = 3;
		gridBagConstraints2.gridy = 3;
		gridBagConstraints2.insets = new Insets(0, 4, 0, 4);
		gridBagConstraints2.anchor = GridBagConstraints.EAST;
		jPanel1.add(lWill, gridBagConstraints2);

		featCount.setText("2");
		featCount.setForeground(Color.black);
		featCount.setHorizontalAlignment(SwingConstants.TRAILING);
		featCount.setPreferredSize(new Dimension(33, 15));
		featCount.setHorizontalTextPosition(SwingConstants.CENTER);
		gridBagConstraints2 = new GridBagConstraints();
		gridBagConstraints2.gridx = 1;
		gridBagConstraints2.gridy = 1;
		gridBagConstraints2.anchor = GridBagConstraints.EAST;
		jPanel1.add(featCount, gridBagConstraints2);

		skillCount.setText("8");
		skillCount.setForeground(Color.black);
		skillCount.setHorizontalAlignment(SwingConstants.TRAILING);
		gridBagConstraints2 = new GridBagConstraints();
		gridBagConstraints2.gridx = 1;
		gridBagConstraints2.gridy = 2;
		gridBagConstraints2.anchor = GridBagConstraints.EAST;
		jPanel1.add(skillCount, gridBagConstraints2);

		lBAB.setText("+2");
		lBAB.setForeground(Color.black);
		gridBagConstraints2 = new GridBagConstraints();
		gridBagConstraints2.gridx = 1;
		gridBagConstraints2.gridy = 3;
		gridBagConstraints2.anchor = GridBagConstraints.EAST;
		jPanel1.add(lBAB, gridBagConstraints2);

		lDefense.setText("4");
		lDefense.setForeground(Color.black);
		gridBagConstraints2 = new GridBagConstraints();
		gridBagConstraints2.gridx = 1;
		gridBagConstraints2.gridy = 4;
		gridBagConstraints2.anchor = GridBagConstraints.EAST;
		jPanel1.add(lDefense, gridBagConstraints2);

		lWP.setText("14");
		lWP.setForeground(Color.black);
		gridBagConstraints2 = new GridBagConstraints();
		gridBagConstraints2.gridx = 3;
		gridBagConstraints2.gridy = 5;
		gridBagConstraints2.insets = new Insets(0, 4, 0, 4);
		gridBagConstraints2.anchor = GridBagConstraints.EAST;
		jPanel1.add(lWP, gridBagConstraints2);

		lblHp.setText("");
		lblHp.setForeground(Color.black);
		lblHp.setHorizontalAlignment(SwingConstants.TRAILING);
		gridBagConstraints2 = new GridBagConstraints();
		gridBagConstraints2.gridx = 1;
		gridBagConstraints2.gridy = 0;
		gridBagConstraints2.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints2.anchor = GridBagConstraints.EAST;
		gridBagConstraints2.weightx = 0.2;
		jPanel1.add(lblHp, gridBagConstraints2);

//		tHp.setHorizontalAlignment(JTextField.TRAILING);
//		tHp.addFocusListener(new FocusAdapter()
//		{
//			public void focusLost(FocusEvent evt)
//			{
//				tHpFocusLost(evt);
//			}
//		});
//
//		gridBagConstraints2 = new GridBagConstraints();
//		gridBagConstraints2.gridx = 1;
//		gridBagConstraints2.gridy = 0;
//		gridBagConstraints2.fill = GridBagConstraints.HORIZONTAL;
//		gridBagConstraints2.anchor = GridBagConstraints.EAST;
//		gridBagConstraints2.weightx = 0.2;
//		jPanel1.add(tHp, gridBagConstraints2);

		lblReputation.setText("Reputation");
		lblReputation.setForeground(Color.black);
		gridBagConstraints2 = new GridBagConstraints();
		gridBagConstraints2.gridx = 2;
		gridBagConstraints2.gridy = 4;
		gridBagConstraints2.anchor = GridBagConstraints.EAST;
		jPanel1.add(lblReputation, gridBagConstraints2);

		lReputation.setText("0");
		lReputation.setForeground(Color.black);
		gridBagConstraints2 = new GridBagConstraints();
		gridBagConstraints2.gridx = 3;
		gridBagConstraints2.gridy = 4;
		gridBagConstraints2.insets = new Insets(0, 4, 0, 4);
		gridBagConstraints2.anchor = GridBagConstraints.EAST;
		jPanel1.add(lReputation, gridBagConstraints2);

		lFortitude.setText("+0");
		lFortitude.setForeground(Color.black);
		gridBagConstraints2 = new GridBagConstraints();
		gridBagConstraints2.gridx = 3;
		gridBagConstraints2.gridy = 1;
		gridBagConstraints2.insets = new Insets(0, 4, 0, 4);
		gridBagConstraints2.anchor = GridBagConstraints.EAST;
		gridBagConstraints2.weightx = 0.2;
		jPanel1.add(lFortitude, gridBagConstraints2);

		lMaxHPPct.setText("Max HP %");
		lMaxHPPct.setForeground(Color.black);
		gridBagConstraints2 = new GridBagConstraints();
		gridBagConstraints2.gridx = 2;
		gridBagConstraints2.gridy = 0;
		gridBagConstraints2.anchor = GridBagConstraints.EAST;
		gridBagConstraints2.weightx = 0.3;
		gridBagConstraints2.weighty = 1.0;
		jPanel1.add(lMaxHPPct, gridBagConstraints2);

		tMaxHPPct.setHorizontalAlignment(JTextField.TRAILING);
		tMaxHPPct.addFocusListener(new FocusAdapter()
		{
			public void focusLost(FocusEvent evt)
			{
				tMaxHPPctFocusLost(evt);
			}
		});

		gridBagConstraints2 = new GridBagConstraints();
		gridBagConstraints2.gridx = 3;
		gridBagConstraints2.gridy = 0;
		gridBagConstraints2.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints2.anchor = GridBagConstraints.EAST;
		gridBagConstraints2.weightx = 0.2;
		jPanel1.add(tMaxHPPct, gridBagConstraints2);

		gridBagConstraints1 = new GridBagConstraints();
		gridBagConstraints1.gridx = 1;
		gridBagConstraints1.gridy = 5;
		gridBagConstraints1.gridwidth = 2;
		gridBagConstraints1.gridheight = 7;
		gridBagConstraints1.fill = GridBagConstraints.BOTH;
		add(jPanel1, gridBagConstraints1);

		jPanel2.setLayout(new GridBagLayout());
		GridBagConstraints gridBagConstraints3;

		jPanel2.setPreferredSize(new Dimension(400, 27));
		jPanel2.setMinimumSize(new Dimension(234, 27));
		jPanel2.setAlignmentY(0.0F);
		jPanel2.setAlignmentX(0.0F);
		jPanel2.setMaximumSize(new Dimension(1000, 27));
		jButtonMinus.setText("-");
		jButtonMinus.setAlignmentY(0.0F);
		jButtonMinus.setHorizontalAlignment(SwingConstants.LEFT);
		jButtonMinus.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				buttonMinusActionListener(evt);
			}
		});


		gridBagConstraints3 = new GridBagConstraints();
		gridBagConstraints3.gridx = 0;
		gridBagConstraints3.gridy = 0;
		gridBagConstraints3.insets = new Insets(0, 0, 0, 5);
		jPanel2.add(jButtonMinus, gridBagConstraints3);

		jButtonPlus.setText("+");
		jButtonPlus.setAlignmentY(0.0F);
		jButtonPlus.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				buttonPlusActionPerformed(evt);
			}
		});

		gridBagConstraints3 = new GridBagConstraints();
		gridBagConstraints3.gridx = 1;
		gridBagConstraints3.gridy = 0;
		gridBagConstraints3.insets = new Insets(0, 0, 0, 5);
		jPanel2.add(jButtonPlus, gridBagConstraints3);

		jLabelFilter.setText("Filter");
		jLabelFilter.setForeground(Color.black);
		jLabelFilter.setHorizontalAlignment(SwingConstants.TRAILING);
		jLabelFilter.setAlignmentY(1.0F);
		gridBagConstraints3 = new GridBagConstraints();
		gridBagConstraints3.gridx = 2;
		gridBagConstraints3.gridy = 0;
		gridBagConstraints3.insets = new Insets(0, 0, 0, 5);
		jPanel2.add(jLabelFilter, gridBagConstraints3);

		jcbFilter.setModel(new DefaultComboBoxModel(new String[]{ "All", "Qualified", "Base", "Prestige", "PC", "NPC", "Monster" }));
		jcbFilter.setAlignmentY(0.0F);
		jcbFilter.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				filterActionPerformed(evt);
			}
		});

		gridBagConstraints3 = new GridBagConstraints();
		gridBagConstraints3.gridx = 3;
		gridBagConstraints3.gridy = 0;
		gridBagConstraints3.fill = GridBagConstraints.NONE;
		gridBagConstraints3.anchor = GridBagConstraints.WEST;
		gridBagConstraints3.weightx = 0.8;
		jPanel2.add(jcbFilter, gridBagConstraints3);

		levelCap.setSelected(Globals.isIgnoreLevelCap());
		levelCap.setToolTipText("Don't stop at level 20");
		levelCap.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				Object source = evt.getSource();
				Globals.setIgnoreLevelCap(levelCap.isSelected());
			}
		});
		gridBagConstraints3 = new GridBagConstraints();
		gridBagConstraints3.gridx = 4;
		gridBagConstraints3.gridy = 0;
		jPanel2.add(levelCap, gridBagConstraints3);

		gridBagConstraints1 = new GridBagConstraints();
		gridBagConstraints1.gridx = 0;
		gridBagConstraints1.gridy = 12;
		gridBagConstraints1.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints1.insets = new Insets(5, 0, 5, 0);
		add(jPanel2, gridBagConstraints1);

		lblExperience.setText("Experience");
		gridBagConstraints1 = new GridBagConstraints();
		gridBagConstraints1.gridx = 1;
		gridBagConstraints1.insets = new Insets(0, 0, 0, 5);
		gridBagConstraints1.anchor = GridBagConstraints.EAST;
		gridBagConstraints1.weightx = 0.5;
		add(lblExperience, gridBagConstraints1);

		experience.addFocusListener(new FocusAdapter()
		{
			public void focusLost(FocusEvent evt)
			{
				experienceFocusLost(evt);
			}
		});

		gridBagConstraints1 = new GridBagConstraints();
		gridBagConstraints1.gridx = 2;
		gridBagConstraints1.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints1.anchor = GridBagConstraints.WEST;
		gridBagConstraints1.weightx = 0.5;
		add(experience, gridBagConstraints1);

	}

	private void formComponentShown(ComponentEvent evt)
	{
		PCGen_Frame1.getStatusBar().setText("If the Max HP % is set to 0, random HP's will be generated, " +
			"otherwise the specified % of max HP will be used.");
	}

	private void tMaxHPPctFocusLost(FocusEvent evt)
	{
		Globals.setHpPct(tMaxHPPct.getValue());
	}

	private void experienceFocusLost(FocusEvent evt)
	{
		if (Globals.getCurrentPC().totalLevels() == 0)
			return;
		Globals.getCurrentPC().setDirty(true);
		PCClass aClass = (PCClass)Globals.getCurrentPC().getClassList().get(0);
		Integer min = aClass.minExpForLevel(Globals.getCurrentPC().totalLevels() + Globals.getCurrentPC().levelAdjustment());
		Integer anInt = new Integer(experience.getText());
		if (anInt.intValue() < min.intValue())
		{
			JOptionPane.showMessageDialog(null, "To be your level (" + new Integer(Globals.getCurrentPC().totalLevels()).toString() +
				") you must have at least " + min + " experience", "PCGen", JOptionPane.ERROR_MESSAGE);
			experience.setText(min.toString());
			Globals.getCurrentPC().setExperience(min);
			return;
		}
		min = aClass.minExpForLevel(Globals.getCurrentPC().totalLevels() + 1 + Globals.getCurrentPC().levelAdjustment());
		if (anInt.intValue() >= min.intValue())
			JOptionPane.showMessageDialog(null, "You can advance a level with that much experience!", "PCGen", JOptionPane.INFORMATION_MESSAGE);
		Globals.getCurrentPC().setExperience(anInt);
	}

	private void filterActionPerformed(ActionEvent evt)
	{
		allClassesDataModel.setFilter(jcbFilter.getSelectedIndex());
	}

	private void buttonHPActionListener(ActionEvent evt)
	{
	}

	private void buttonMinusActionListener(ActionEvent evt)
	{
		if (allClassesTable.getSelectedRowCount() <= 0)
			return;
		Globals.getCurrentPC().setDirty(true);
		PCClass theClass = (PCClass)displayClasses.get(sortedAllClassesModel.getRowTranslated(allClassesTable.getSelectedRow()));
		if (theClass != null)
		{
			Globals.getCurrentPC().incrementClassLevel(-1, theClass);
		}
		pcChanged();
	}

	private void buttonPlusActionPerformed(ActionEvent evt)
	{
		if (allClassesTable.getSelectedRowCount() <= 0)
			return;
		Globals.getCurrentPC().setDirty(true);
		PCClass theClass = (PCClass)displayClasses.get(sortedAllClassesModel.getRowTranslated(allClassesTable.getSelectedRow()));
		if ((theClass != null) && theClass.isQualified())
		{
			PCClass aClass = Globals.getCurrentPC().getClassNamed(theClass.getName());
			if (aClass == null || Globals.isIgnoreLevelCap() || (!Globals.isIgnoreLevelCap() && aClass.getLevel().intValue() < aClass.getMaxLevel()))
				Globals.getCurrentPC().incrementClassLevel(1, theClass);
			else
				JOptionPane.showMessageDialog(null, "Maximum level reached.", "PCGen", JOptionPane.INFORMATION_MESSAGE);
		}
		pcChanged();
	}

	/** <code>pcChanged</code> update data listening for a changed PC, to update
	 * various places where the screen displays stuff.
	 * NOTE: This should probably be handled by an event of some sort, but this
	 * is the quick fix.
	 */
	public void pcChanged()
	{
		PlayerCharacter pc = Globals.getCurrentPC();
		//Calculate the aggregate feat list
		pc.aggregateFeatList();
		pc.setAggregateFeatsStable(true);
		pc.setAutomaticFeatsStable(true);
		pc.setVirtualFeatsStable(true);

		if (pc != null)
		{
			lblHp.setText(new Integer(pc.hitPoints()).toString());
//			tHp.setValue(pc.hitPoints());
			featCount.setText(Integer.toString(pc.getFeats()));
			skillCount.setText(Integer.toString(pc.getSkillPoints()));
			lBAB.setText(Integer.toString(pc.baseAttackBonus(0)));
			lDefense.setText(pc.defense().toString());
			lFortitude.setText(Integer.toString(pc.getBonus(1, true)));
			lReflex.setText(Integer.toString(pc.getBonus(2, true)));
			lWill.setText(Integer.toString(pc.getBonus(3, true)));
			lWP.setText(pc.woundPoints().toString());
			experience.setValue(pc.getExperience().intValue());
			lReputation.setText(pc.reputation().toString());
			int[] selrows = allClassesTable.getSelectedRows();
			if (selrows != null && selrows.length > 0)
			{
				allClassesDataModel.fireTableDataChanged();
				allClassesTable.setRowSelectionInterval(selrows[0], selrows[0]);
			}
			currentClassesDataModel.fireTableDataChanged();
		}
		if (!Globals.isStarWarsMode())
		{
			lblDefense.setVisible(false);
			lDefense.setVisible(false);
			lblWoundPoints.setVisible(false);
			lWP.setVisible(false);
			lblReputation.setVisible(false);
			lReputation.setVisible(false);
			TableColumn col = allClassesTable.getColumnModel().getColumn(8);
			col.setHeaderValue("Spell Type");
			col = allClassesTable.getColumnModel().getColumn(9);
			col.setHeaderValue("Base Stat");
		}
		else
		{
			lblDefense.setVisible(true);
			lDefense.setVisible(true);
			lblWoundPoints.setVisible(true);
			lWP.setVisible(true);
			lblReputation.setVisible(true);
			lReputation.setVisible(true);
			TableColumn col = allClassesTable.getColumnModel().getColumn(8);
			col.setHeaderValue("Defense");
			col = allClassesTable.getColumnModel().getColumn(9);
			col.setHeaderValue("Reputation");
		}
	}

	/** TableModel to handle the full list of classes.
	 *  It pulls its data straight from the Globals.getClassList() Vector.
	 */
	public class AllClassesTableModel extends AbstractTableModel
	{
		private int curFilter;
		private int prevGlobalClassCount;

		/**
		 *
		 */
		public AllClassesTableModel()
		{
			setFilter(0);
		}

		/**
		 * Uses the ID from the jcbFilter, so any change to the list of filters
		 * will require a modification of this method.
		 * at the moment:
		 * 0: All
		 * 1: Qualified
		 * 2: Base
		 * 3: Prestige
		 * @param filterID the filter type
		 */
		public void setFilter(int filterID)
		{
			prevGlobalClassCount = Globals.getClassList().size();
			displayClasses = new ArrayList(prevGlobalClassCount);

			switch (filterID)
			{
				case FILTER_ALL: // All
					for (Iterator it = Globals.getClassList().iterator(); it.hasNext();)
					{
						PCClass pcClass = (PCClass)it.next();
						displayClasses.add(pcClass);
					}
					break;

				case FILTER_QUALIFIED: // Qualified
					for (Iterator it = Globals.getClassList().iterator(); it.hasNext();)
					{
						PCClass pcClass = (PCClass)it.next();
						if (pcClass.isQualified())
							displayClasses.add(pcClass);
					}
					break;

				case FILTER_BASE: // Base
					for (Iterator it = Globals.getClassList().iterator(); it.hasNext();)
					{
						PCClass pcClass = (PCClass)it.next();
						if (!pcClass.isPrestige() && (!pcClass.isMonster() || pcClass.isQualified()))
							displayClasses.add(pcClass);
					}
					break;

				case FILTER_PRESTIGE: // Prestige
					for (Iterator it = Globals.getClassList().iterator(); it.hasNext();)
					{
						PCClass pcClass = (PCClass)it.next();
						if (pcClass.isPrestige())
							displayClasses.add(pcClass);
					}
					break;

				case FILTER_PC: // All
					for (Iterator it = Globals.getClassList().iterator(); it.hasNext();)
					{
						PCClass pcClass = (PCClass)it.next();
						if (pcClass.isPC())
							displayClasses.add(pcClass);
					}
					break;

				case FILTER_NPC: // All
					for (Iterator it = Globals.getClassList().iterator(); it.hasNext();)
					{
						PCClass pcClass = (PCClass)it.next();
						if (pcClass.isNPC())
							displayClasses.add(pcClass);
					}
					break;

				case FILTER_MONSTER: // All
					for (Iterator it = Globals.getClassList().iterator(); it.hasNext();)
					{
						PCClass pcClass = (PCClass)it.next();
						if (pcClass.isMonster() && pcClass.isQualified())
							displayClasses.add(pcClass);
					}
					break;

			}

			displayClasses.remove(Globals.getClassNamed("Domain"));

			fireTableDataChanged();
			curFilter = filterID;
		}

		/**
		 * Re-fetches and re-filters the data from the global class list.
		 */

		public void updateFilter()
		{
			setFilter(curFilter);
		}

		/**
		 * @return the number of columns
		 */
		public int getColumnCount()
		{
			return ALL_CLASSES_COLUMN_NAMES.length - 2;
		}

		/**
		 * @param columnIndex the index of the column to retrieve
		 * @return the type of the specified column
		 */
		public Class getColumnClass(int columnIndex)
		{
			return String.class;
		}

		/**
		 * @return the number of rows in the model
		 */
		public int getRowCount()
		{
			if (prevGlobalClassCount != Globals.getClassList().size())
				updateFilter();
			return (displayClasses != null) ? displayClasses.size() : 0;
		}

		/**
		 * @param columnIndex the index of the column name to retrieve
		 * @return the name of the specified column
		 */
		public String getColumnName(int columnIndex)
		{
			if (Globals.isStarWarsMode() && columnIndex > 9)
				columnIndex += 2;
			else if (!Globals.isStarWarsMode() && columnIndex > 7)
				columnIndex += 2;
			return (columnIndex >= 0 && columnIndex < ALL_CLASSES_COLUMN_NAMES.length) ?
				ALL_CLASSES_COLUMN_NAMES[columnIndex] : "Out Of Bounds";
		}

		/**
		 * @param rowIndex the row of the cell to retrieve
		 * @param columnIndex the column of the cell to retrieve
		 * @return the value of the cell
		 */
		public Object getValueAt(int rowIndex, int columnIndex)
		{
			if (displayClasses != null)
			{
				PCClass c = (PCClass)displayClasses.get(rowIndex),
					pc = Globals.getCurrentPC().getClassNamed(c.toString());
				if (pc != null)
				{
					c = pc;
				}

				switch (columnIndex)
				{
					case 0:
						return c.isQualified() ? "Y" : "N";

					case 1:
						return c.toString();

					case 2:
						return c.getAttackBonusType();

					case 3:
						return "1D" + c.getHitDie();

					case 4:
						return c.getFortitudeCheckType();

					case 5:
						return c.getReflexCheckType();

					case 6:
						return c.getWillCheckType();

					case 7:
						return c.prestigeString();

					case 8:
						return (Globals.isStarWarsMode()) ? c.defenseString() : c.getSpellType();

					case 9:
						return (Globals.isStarWarsMode()) ? c.reputationString() : c.getSpellBaseStat();

					case 10:
						return c.getSource();
				}
			}

			return null;
		}
	}

	/**
	 *
	 */
	public class PCClassesTableModel extends AbstractTableModel
	{
		public int getColumnCount()
		{
			return 2;
		}

		public Class getColumnClass(int columnIndex)
		{
			if (columnIndex == 0)
			{
				return String.class;
			}
			return Integer.class;
		}

		public int getRowCount()
		{
			if ((Globals.getCurrentPC() != null) && (Globals.getCurrentPC().getClassList() != null))
			{
				return filterDomain().size();
			}
			else
				return 0;
		}

		public String getColumnName(int columnIndex)
		{
			switch (columnIndex)
			{
				case 0:
					return "Class";
				case 1:
					return "Level";
			}
			return "Out Of Bounds";
		}

		public Object getValueAt(int rowIndex, int columnIndex)
		{
			if ((Globals.getCurrentPC() != null) && (Globals.getCurrentPC().getClassList() != null))
			{
				PCClass c = (PCClass)filterDomain().get(rowIndex);
				switch (columnIndex)
				{
					case 0:
						return c.getDisplayClassName();
					case 1:
						return c.getLevel();
				}
			}
			return null;
		}

		ArrayList filterDomain()
		{
			ArrayList result = (ArrayList)Globals.getCurrentPC().getClassList().clone();
			Iterator i = result.iterator();
			while (i.hasNext())
			{
				Object o = i.next();
				if (o.toString().equals("Domain"))
				{
					result.remove(o);
					break;
				}
			}
			return result;
		}
	}


}

/**
 * Popup frame with export options
 */
class HPFrame extends JFrame
{
	MainHP mainHP = null;

	public HPFrame()
	{
		super("Adjust PC's Hit Points");
		ClassLoader loader = getClass().getClassLoader();
		Toolkit kit = Toolkit.getDefaultToolkit();
		// according to the API, the following should *ALWAYS* use '/'
		Image img = kit.getImage(loader.getResource("pcgen/gui/PcgenIcon.gif"));
		loader = null;
		this.setIconImage(img);
		Dimension screenSize = kit.getScreenSize();
		int screenHeight = screenSize.height;
		int screenWidth = screenSize.width;

		// center frame in screen
		setSize(screenWidth / 2, screenHeight / 2);
		setLocation(screenWidth / 4, screenHeight / 4);

		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		mainHP = new MainHP();
		Container contentPane = getContentPane();
		contentPane.add(mainHP);
		setVisible(true);
	}

	public void setPSize()
	{
		if (mainHP != null)
		{
			mainHP.setPSize();
		}
	}

}//end HPFrame
