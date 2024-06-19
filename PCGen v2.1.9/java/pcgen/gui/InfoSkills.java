/*s
 * InfoClasses.java
 * Copyright 2001 (C) Brian Forester <ysgarran@yahoo.com>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied waarranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * Created on May 1, 2001, 5:57 PM
 */

package pcgen.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.Iterator;
import java.util.Vector;
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
import javax.swing.SwingConstants;
import javax.swing.table.AbstractTableModel;
import pcgen.core.Globals;
import pcgen.core.PCClass;
import pcgen.core.PlayerCharacter;
import pcgen.core.Skill;
import pcgen.util.Delta;

/**
 *
 * @author  bforester
 * @version $Revision: 1.1 $
 */
public class InfoSkills extends JPanel
{
	// Variables declaration
	private JScrollPane jScrollPane1;
	private JPanel bottomPanel;
	private JLabel jLbMaxSkill;
	private JLabel jLbMaxCrossSkill;
	private JLabel jLbClassSkillPoints;
	private JComboBox currCharacterClass;
	private JButton raiseSkill;
	private JButton raiseToMax;
	private JButton lowerSkill;
	private JLabel jLblTotalSkillPiointsLeft;
	private JLabel maxSkillRank;
	private JLabel maxCrossSkillRank;
	private JCheckBox collapsedView;
	private JComboBox skillView;
	private WholeNumberField totalSkillPointsLeft;
	private WholeNumberField currCharClassSkillPnts;
	protected SkillTableModel skillModel = new SkillTableModel();
	private TableSorter sorter = new TableSorter(skillModel);
	private JLabel includeLabel;
	private JLabel skillLabel;
	private JComboBox skillChoice = new JComboBox();
	private JTableEx tblSkills = new JTableEx(sorter);
	private JLabel exclusiveLabel;
	private JTextField exclusiveSkillCost;

	/** Creates new form InfoSkills
	 *  The default view will be all skills for all classes.
	 *  A collapsed view will be presented for the craft, knowledge
	 *    and profession skills.
	 */
	public InfoSkills()
	{
		initComponents();
		sorter.addMouseListenerToHeaderInTable(tblSkills);
		tblSkills.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		tblSkills.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		this.addComponentListener(new ComponentAdapter()
		{
			public void componentShown(ComponentEvent evt)
			{
				final PlayerCharacter aPC = Globals.getCurrentPC();
				aPC.setAggregateFeatsStable(true);
				aPC.setAutomaticFeatsStable(true);
				aPC.setVirtualFeatsStable(true);

				requestDefaultFocus();
				refreshDisplay();
				final int[] cols = {0,1,2,3,4,5,6,7};
				tblSkills.setOptimalColumnWidths(cols);
			}
		});
	}

	public boolean isFocusTraversable()
	{
		return true;
	}

	private void initComponents()
	{
		jScrollPane1 = new JScrollPane(tblSkills);
		bottomPanel = new JPanel();
		jLbMaxSkill = new JLabel();
		jLbMaxCrossSkill = new JLabel();
		jLbClassSkillPoints = new JLabel();
		currCharacterClass = new JComboBox();
		raiseSkill = new JButton();
		raiseToMax = new JButton();
		lowerSkill = new JButton();
		jLblTotalSkillPiointsLeft = new JLabel();
		maxSkillRank = new JLabel(new Integer(Globals.getCurrentPC().totalLevels() + Globals.getCurrentPC().totalHitDice() + 3).toString());
		maxCrossSkillRank = new JLabel(new Integer((Globals.getCurrentPC().totalLevels() + Globals.getCurrentPC().totalHitDice() + 3) / 2).toString());
		collapsedView = new JCheckBox();
		skillView = new JComboBox();
		totalSkillPointsLeft = new WholeNumberField(0, 4);
		currCharClassSkillPnts = new WholeNumberField(0, 4);
		exclusiveLabel = new JLabel();
		exclusiveSkillCost = new WholeNumberField(0, 4);

		setLayout(new BorderLayout());

		jScrollPane1.setBackground(Color.white);
		add(jScrollPane1, BorderLayout.CENTER);

		GridBagLayout gridbag = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		bottomPanel.setLayout(gridbag);
		add(bottomPanel, BorderLayout.SOUTH);

		jLbMaxSkill.setText("Max Class Skill Rank:  ");
		jLbMaxSkill.setToolTipText("This is the maximum skill rank the character can have.");
		jLbMaxSkill.setForeground(Color.black);
		buildConstraints(c, 0, 1, 3, 1, 0.0, 25.0);
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.EAST;
		gridbag.setConstraints(jLbMaxSkill, c);
		bottomPanel.add(jLbMaxSkill);

		jLbMaxCrossSkill.setText("Max Cross-Class Skill Rank:  ");
		jLbMaxCrossSkill.setToolTipText("This is the maximum cross-class skill rank the character can have.");
		jLbMaxCrossSkill.setForeground(Color.black);
		buildConstraints(c, 0, 2, 3, 1, 0.0, 25.0);
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.EAST;
		gridbag.setConstraints(jLbMaxCrossSkill, c);
		bottomPanel.add(jLbMaxCrossSkill);

		jLbClassSkillPoints.setText("Skill Points Left for Class:  ");
		jLbClassSkillPoints.setForeground(Color.black);
		buildConstraints(c, 3, 2, 2, 1, 0.0, 0.0);
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.EAST;
		gridbag.setConstraints(jLbClassSkillPoints, c);
		bottomPanel.add(jLbClassSkillPoints);

		for (Iterator i = Globals.getCurrentPC().getClassList().iterator(); i.hasNext();)
		{
			currCharacterClass.addItem((PCClass)i.next());
		}
		if (currCharacterClass.getItemCount() > 0)
			currCharacterClass.setSelectedIndex(0);
		currCharacterClass.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				currCharacterClassActionPerformed(evt);
			}
		});

		buildConstraints(c, 5, 2, 1, 1, 0.0, 0.0);
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.WEST;
		gridbag.setConstraints(currCharacterClass, c);
		bottomPanel.add(currCharacterClass);

		raiseSkill.setText("Raise");
		raiseSkill.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				raiseSkillActionPerformed(evt);
			}
		});

		buildConstraints(c, 1, 0, 1, 1, 14.0, 25.0);
		c.fill = GridBagConstraints.BOTH;
		c.anchor = GridBagConstraints.CENTER;
		gridbag.setConstraints(raiseSkill, c);
		bottomPanel.add(raiseSkill);

		raiseToMax.setText("Max Rank");
		raiseToMax.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				raiseToMaxActionPerformed(evt);
			}
		});

		buildConstraints(c, 3, 0, 1, 1, 14.0, 0.0);
		c.fill = GridBagConstraints.BOTH;
		c.anchor = GridBagConstraints.CENTER;
		gridbag.setConstraints(raiseToMax, c);
		bottomPanel.add(raiseToMax);

		lowerSkill.setText("Lower");
		lowerSkill.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				lowerSkillActionPerformed(evt);
			}
		});

		buildConstraints(c, 5, 0, 1, 1, 14.0, 0.0);
		c.fill = GridBagConstraints.BOTH;
		c.anchor = GridBagConstraints.CENTER;
		gridbag.setConstraints(lowerSkill, c);
		bottomPanel.add(lowerSkill);

		jLblTotalSkillPiointsLeft.setText("Total Skill Points Left:  ");
		jLblTotalSkillPiointsLeft.setForeground(Color.black);
		buildConstraints(c, 4, 1, 2, 1, 14.0, 0.0);
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.EAST;
		gridbag.setConstraints(jLblTotalSkillPiointsLeft, c);
		bottomPanel.add(jLblTotalSkillPiointsLeft);

		maxSkillRank.setText("23");
		maxSkillRank.setForeground(Color.black);
		buildConstraints(c, 3, 1, 1, 1, 14.0, 0.0);
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.WEST;
		gridbag.setConstraints(maxSkillRank, c);
		bottomPanel.add(maxSkillRank);

		maxCrossSkillRank.setText("11");
		maxCrossSkillRank.setForeground(Color.black);
		buildConstraints(c, 3, 2, 1, 1, 0.0, 0.0);
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.WEST;
		gridbag.setConstraints(maxCrossSkillRank, c);
		bottomPanel.add(maxCrossSkillRank);

		collapsedView.setToolTipText("When checked, the modifiable skills, such as craft, knowledge and profession will be collapsed.");
		collapsedView.setText("Collapsed");
		collapsedView.setHorizontalTextPosition(SwingConstants.LEFT);
		collapsedView.setEnabled(false);
		collapsedView.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				collapsedViewActionPerformed(evt);
			}
		});

		buildConstraints(c, 2, 3, 1, 1, 0.0, 0.0);
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.CENTER;
		gridbag.setConstraints(collapsedView, c);
		bottomPanel.add(collapsedView);


		skillView.addItem("All");
		skillView.addItem("Class");
		skillView.addItem("Cross-Class");
		skillView.addItem("Exclusive");
		skillView.addItem("Untrained");
		skillView.addItem("Rank > 0");
		skillView.addItem("Rank + Modifier > 0");
		skillView.setSelectedIndex(0);
		skillView.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				skillViewActionPerformed(evt);
			}
		});

		buildConstraints(c, 1, 3, 1, 1, 0.0, 0.0);
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.WEST;
		gridbag.setConstraints(skillView, c);
		bottomPanel.add(skillView);

		skillLabel = new JLabel(" Skill Filter: ");
		buildConstraints(c, 0, 3, 1, 1, 0.0, 25.0);
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.EAST;
		gridbag.setConstraints(skillLabel, c);
		bottomPanel.add(skillLabel);

		totalSkillPointsLeft.addFocusListener(new FocusAdapter()
		{
			public void focusLost(FocusEvent evt)
			{
				totalSkillPointsLeftFocusLost(evt);
			}
		});

		buildConstraints(c, 6, 1, 1, 1, 0.0, 0.0);
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.WEST;
		gridbag.setConstraints(totalSkillPointsLeft, c);
		bottomPanel.add(totalSkillPointsLeft);

		currCharClassSkillPnts.addFocusListener(new FocusAdapter()
		{
			public void focusLost(FocusEvent evt)
			{
				currCharClassSkillPntsFocusLost(evt);
			}
		});

		buildConstraints(c, 6, 2, 1, 1, 0.0, 0.0);
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.WEST;
		gridbag.setConstraints(currCharClassSkillPnts, c);
		bottomPanel.add(currCharClassSkillPnts);

		skillChoice.setModel(new DefaultComboBoxModel(new String[]{ "None", "Untrained", "All" }));
		skillChoice.setMaximumRowCount(3);
		skillChoice.setToolTipText("How many skills on character sheets?");
		skillChoice.setMinimumSize(new Dimension(98, 22));
		skillChoice.setSelectedIndex(Globals.getIncludeSkills());
		skillChoice.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				skillChoiceActionPerformed(evt);
			}
		});
		buildConstraints(c, 4, 3, 1, 1, 0.0, 0.0);
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.WEST;
		gridbag.setConstraints(skillChoice, c);
		bottomPanel.add(skillChoice);

		includeLabel = new JLabel("Include Skills: ");
		buildConstraints(c, 3, 3, 1, 1, 0.0, 0.0);
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.EAST;
		gridbag.setConstraints(includeLabel, c);
		bottomPanel.add(includeLabel);

		exclusiveLabel = new JLabel("Exclusive Skills: ");
		exclusiveLabel.setToolTipText("What it costs to buy a skill that is normally unavailable to one's class (0 means that it can't be bought)");
		buildConstraints(c, 5, 3, 1, 1, 0.0, 0.0);
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.EAST;
		gridbag.setConstraints(exclusiveLabel, c);
		bottomPanel.add(exclusiveLabel);

		exclusiveSkillCost.setColumns(3);
		exclusiveSkillCost.setText("0");
		exclusiveSkillCost.setMinimumSize(new Dimension(40, 17));
		exclusiveSkillCost.setText(Integer.toString(Globals.getExcSkillCost()));
		exclusiveSkillCost.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				Object source = evt.getSource();
				updateTextFields(source);
			}
		});
		exclusiveSkillCost.addFocusListener(new FocusAdapter()
		{
			public void focusLost(FocusEvent evt)
			{
				textFieldFocusEvent(evt);
			}
		});
		buildConstraints(c, 6, 3, 1, 1, 0.0, 0.0);
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.WEST;
		gridbag.setConstraints(exclusiveSkillCost, c);
		bottomPanel.add(exclusiveSkillCost);
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

	private final void updateTextFields(Object source)
	{
		try
		{
			Globals.setExcSkillCost(Integer.parseInt(exclusiveSkillCost.getText()));
		}
		catch (NumberFormatException nfe)
		{
			exclusiveSkillCost.setText(Integer.toString(Globals.getExcSkillCost()));
		}
	}

	private void textFieldFocusEvent(FocusEvent evt)
	{
		updateTextFields(evt.getSource());
	}

	private void skillChoiceActionPerformed(ActionEvent evt)
	{
		final int selection = skillChoice.getSelectedIndex();
		if (selection >= 0 && selection <= 2)
		{
			Globals.setIncludeSkills(selection);
		}
	}

	private void currCharClassSkillPntsFocusLost(FocusEvent evt)
	{
		Globals.getCurrentPC().setDirty(true);
		PCClass aClass = this.getSelectedPCClass();
		if (currCharClassSkillPnts.getText().length() > 0)
		{
			Integer anInt = Delta.decode(currCharClassSkillPnts.getText());
			if (aClass == null || anInt.intValue() == aClass.skillPool().intValue())
				return;
			int i = aClass.skillPool().intValue() - anInt.intValue();
			aClass.setSkillPool(Math.max(0, anInt.intValue()));
			Globals.getCurrentPC().setSkillPoints(Math.max(0, Globals.getCurrentPC().getSkillPoints() - i));
		}
		currCharClassSkillPnts.setValue(aClass.skillPool().intValue());
		totalSkillPointsLeft.setValue(Globals.getCurrentPC().getSkillPoints());
	}

	private void totalSkillPointsLeftFocusLost(FocusEvent evt)
	{
		Globals.getCurrentPC().setDirty(true);
		if (totalSkillPointsLeft.getText().length() > 0)
		{
			Integer anInt = Delta.decode(totalSkillPointsLeft.getText());
			if (anInt.intValue() == Globals.getCurrentPC().getSkillPoints())
				return;
			Globals.getCurrentPC().setSkillPoints(anInt.intValue());
			int x = Globals.getCurrentPC().getClassList().size();
			PCClass aClass = null;
			for (Iterator i = Globals.getCurrentPC().getClassList().iterator(); i.hasNext();)
			{
				aClass = (PCClass)i.next();
				aClass.setSkillPool(Math.max(0, anInt.intValue() / x));
			}
			aClass = getSelectedPCClass();
			if (aClass != null)
				currCharClassSkillPnts.setValue(aClass.skillPool().intValue());
		}
		totalSkillPointsLeft.setValue(Globals.getCurrentPC().getSkillPoints());
	}

	private void skillViewActionPerformed(ActionEvent evt)
	{
		populateSkillModel();
	}

	private void currCharacterClassActionPerformed(ActionEvent evt)
	{
		PCClass aClass = this.getSelectedPCClass();
		if (aClass != null)
		{
			currCharClassSkillPnts.setValue(aClass.skillPool().intValue());
			totalSkillPointsLeft.setValue(Globals.getCurrentPC().getSkillPoints());
		}
		else
		{
			currCharClassSkillPnts.setValue(0);
			totalSkillPointsLeft.setValue(0);
		}
		populateSkillModel();
	}

	private void collapsedViewActionPerformed(ActionEvent evt)
	{
		// Add your handling code here:
	}

	private void lowerSkillActionPerformed(ActionEvent evt)
	{
		Globals.getCurrentPC().setDirty(true);
		Skill aSkill = getSelectedSkill();
		if (aSkill == null)
			return;
		modRank(aSkill, -1.0);
	}

	private void raiseToMaxActionPerformed(ActionEvent evt)
	{
		Skill aSkill = getSelectedSkill();
		if (aSkill == null)
			return;
		Globals.getCurrentPC().setDirty(true);
		PCClass aClass = getSelectedPCClass();
		double maxRank = 0.0;
		double points = 0.0;
		double skillPool = 0.0;
		if (aClass != null)
		{
			maxRank = Globals.getCurrentPC().getMaxRank(aSkill.getName(), aClass).doubleValue();
			skillPool = aClass.skillPool().doubleValue();
		}
		if (maxRank > aSkill.getRank().doubleValue() || Globals.isBoolBypassMaxSkillRank())
		{
			String aString = sorter.getValueAt(tblSkills.getSelectedRow(), 6).toString();
			if (aString.length() < 1) // no valid skill selected
				return;
			double cost = Double.parseDouble(aString);
			points = Math.min((maxRank - aSkill.getTotalRank().doubleValue()) * cost, skillPool);
			modRank(aSkill, points);
		}
		else
			JOptionPane.showMessageDialog(null, "Cannot raise this skill further.", "PCGen", JOptionPane.INFORMATION_MESSAGE);
	}

	private void raiseSkillActionPerformed(ActionEvent evt)
	{
		Skill aSkill = getSelectedSkill();
		if (aSkill == null)
			return;
		Globals.getCurrentPC().setDirty(true);
		modRank(aSkill, 1.0);
	}

	private Skill getSelectedSkill()
	{
		int i = tblSkills.getSelectedRow();
		if (i < 0 || i > sorter.getRowCount())
		{
			JOptionPane.showMessageDialog(null, "Please select a skill.", "PCGen", JOptionPane.INFORMATION_MESSAGE);
			return null;
		}
		Skill aSkill = (Skill)
			skillModel.data().elementAt(sorter.getRowTranslated(i));
		return aSkill;
	}

	private void modRank(Skill aSkill, double points)
	{
		Skill bSkill = aSkill;
		final int selectedRow = tblSkills.getSelectedRow();
		String aString = sorter.getValueAt(selectedRow, 6).toString();
		if (aString.length() < 1) // no valid skill selected
			return;
		double cost = Double.parseDouble(aString);
		if (cost == 0.0)
		{
			JOptionPane.showMessageDialog(null, "You cannot modify this skill. A cost of 0 means it is exclusive.", "PCGen", JOptionPane.INFORMATION_MESSAGE);
			return;
		}
		double rank = points / cost;
		if (rank > 0.0 && aSkill != null)
			bSkill = (Skill)Globals.getCurrentPC().addSkill(aSkill);
		PCClass aClass = getSelectedPCClass();
		aString = "";
		if (bSkill != null)
			aString = bSkill.modRanks(rank, aClass);
		if (aString.length() > 0)
			JOptionPane.showMessageDialog(null, aString, "PCGen", JOptionPane.INFORMATION_MESSAGE);
		else if (bSkill != null)
		{
			final int transRow = sorter.getRowTranslated(selectedRow);
			Skill prevSkill = (Skill)skillModel.data().elementAt(transRow);
			if (prevSkill != null && prevSkill.getKeyName().equals(bSkill.getKeyName()))
			{
				skillModel.data().setElementAt(bSkill, transRow);
				skillModel.fireTableRowsUpdated(transRow, transRow);
				tblSkills.changeSelection(selectedRow, 0, false, false);
				PCClass currentClass = getSelectedPCClass();
				totalSkillPointsLeft.setValue(Globals.getCurrentPC().getSkillPoints());
				if (currentClass != null)
				{
					currCharClassSkillPnts.setValue(aClass.skillPool().intValue());
				}
				else
				{
					currCharClassSkillPnts.setValue(0);
				}
			}
			else
			{
				// fall back on old behavior
				refreshDisplay();
			}
		}
	}

	/**
	 * Get the currently selected Character Class.
	 * @returns   pcgen.core.PCClass
	 * @author    Brian Forester  (ysgarran@yahoo.com)
	 **/
	public PCClass getSelectedPCClass()
	{
		PCClass aClass = (PCClass)currCharacterClass.getSelectedItem();
		return aClass;
	}

	/**
	 *  Setting this property will determine whether all of the
	 *  skills that have modifiers such as craft, knowledge and
	 *  profession are displayed 'in-line' or not.
	 *
	 * @see          javax.swing.JCheckBox
	 * @author       Brian Forester
	 **/
	public void setCollapsedView(boolean bCollapse)
	{

	}

	/**
	 * Checking this box will cause the view to show only those
	 * skills that are exclusive to a character class. Setting
	 * this to true will de-select the Class View, Cross-Class,
	 * and All Skills View check boxes.
	 *
	 * @see          javax.swing.JCheckBox
	 * @author       Brian Forester
	 **/
	public void setExclusiveView(boolean bExclusive)
	{

	}

	/**
	 * This will set the InfoSkills table to show only those
	 * skills that are class skills for the character class
	 * currently selected by the pull down menu.  Setting
	 * this to true will de-select the Exclusive, Cross-Class,
	 * and All Skills View check boxes.
	 *
	 * @see          javax.swing.JCheckBox
	 * @author       Brian Forester
	 **/
	public void setClassSkillsView(boolean bClassSkill)
	{

	}

	/**
	 * This will set the InfoSkills table to show only those
	 * skills that are cross-class skills for the character
	 * class currently selected by the pull down menu. Setting
	 * this to true will de-select the Exclusive, Class View
	 * and All Skills View check boxes.
	 *
	 * @see         javax.swing.JCheckBox
	 * @author      Brian Forester
	 **/
	public void setCrossClassView(boolean bCrossClass)
	{

	}

	/**
	 * This will set the InfoSkills table to show all the
	 * skills that are known by the program.  Setting
	 * this to true will de-select the Exclusive, Class
	 * View and Cross-Class check boxes.
	 *
	 * @see         javax.swing.JCheckBox
	 * @author      Brian Forester
	 **/
	public void setAllSkillsView(boolean bAllSkills)
	{

	}

	/** <code>pcChanged</code> update data listening for a changed PC, to update
	 * various places where the screen displays stuff.
	 * NOTE: This should probably be handled by an event of some sort, but this
	 * is the quick fix.
	 */
	public void pcChanged()
	{
		Globals.getCurrentPC().setDirty(true);
		refreshDisplay();
	}

	public void refreshDisplay()
	{
		PCClass aClass = this.getSelectedPCClass();
		currCharacterClass.removeAllItems();
		for (Iterator i = Globals.getCurrentPC().getClassList().iterator(); i.hasNext();)
			currCharacterClass.addItem(i.next());
		if (aClass != null)
			currCharacterClass.setSelectedItem(aClass);
		else if (currCharacterClass.getItemCount() > 0)
			currCharacterClass.setSelectedIndex(0);
		aClass = this.getSelectedPCClass();
		if (aClass != null)
		{
			currCharClassSkillPnts.setValue(aClass.skillPool().intValue());
			totalSkillPointsLeft.setValue(Globals.getCurrentPC().getSkillPoints());
		}
		else
		{
			currCharClassSkillPnts.setValue(0);
			totalSkillPointsLeft.setValue(0);
		}
		maxSkillRank.setText(new Integer(Globals.getCurrentPC().totalLevels() + 3).toString());
		maxCrossSkillRank.setText(new Integer((Globals.getCurrentPC().totalLevels() + 3) / 2).toString());
		populateSkillModel();
	}

	private void populateSkillModel()
	{
		Vector aVector = new Vector();
		PCClass aClass = getSelectedPCClass();
		for (Iterator i = Globals.getSkillList().iterator(); i.hasNext();)
		{
			Skill aSkill = (Skill)i.next();
			Skill bSkill = Globals.getCurrentPC().getSkillNamed(aSkill.getName());
			if (bSkill != null)
				aSkill = bSkill;
			int skillViewIndex = skillView.getSelectedIndex();
			boolean addIt = false;
			switch (skillViewIndex)
			{
				case 0: // "ALL"
					addIt = true;
					break;
				case 1: // "Class"
					addIt = (aClass != null && aSkill.isClassSkill(aClass));
					break;
				case 2: // "Cross-Class"
					addIt = (aClass != null && !aSkill.isClassSkill(aClass) && !aSkill.isExclusive().startsWith("Y"));
					break;
				case 3: // "Exclusive"
					addIt = (aSkill.isExclusive().startsWith("Y"));
					break;
				case 4: //Untrained
					addIt = aSkill.untrained().startsWith("Y");
					break;
				case 5: //Rank > 0
					addIt = Math.floor(aSkill.getTotalRank().doubleValue()) > 0.0;
					break;
				case 6: //Rank + modifier > 0
					addIt = Math.floor(aSkill.getTotalRank().doubleValue()) + aSkill.modifier().doubleValue() > 0.0;
					break;
				default:
					System.err.println(skillViewIndex + " is an impossible value for skillView.");
			}
			if (addIt)
				aVector.addElement(aSkill);
		}
		skillModel.setData(aVector);
		skillModel.fireTableDataChanged();
	}

	/**
	 *  Model for the JTable containing the skills.
	 *
	 * @author     Bryan McRoberts <a href="mailto:merton_monk@users.sourceforge.net">
	 *      merton_monk@users.sourceforge.net</a>
	 * @created    26 may 2001
	 * @version    $Revision: 1.1 $
	 */
	public class SkillTableModel extends AbstractTableModel
	{
		/**
		 *  Contains the skills
		 *
		 * @since
		 */
		public Vector data = new Vector();
		final String[] nameList = {"Skill", "Key Stat", "Modifier", "Rank", "Total", "Untrained", "Cost", "Source File"};


		/**
		 *  Sets the Data attribute of the SkillTableModel object
		 *
		 * @param  aVector  The new Data value
		 * @since
		 */
		public void setData(Vector aVector)
		{
			data = aVector;
		}

		public Vector data()
		{
			return data;
		}


		/**
		 *  Gets the ColumnCount attribute of the SkillTableModel object
		 *
		 * @return    The ColumnCount value
		 * @since
		 */
		public int getColumnCount()
		{
			return nameList.length;
		}

		/**
		 *
		 *
		 *
		 */
		public Class getColumnClass(int c)
		{
			return getValueAt(0, c).getClass();
		}

		/**
		 *  Gets the RowCount attribute of the SkillTableModel object
		 *
		 * @return    The RowCount value
		 * @since
		 */
		public int getRowCount()
		{
			return data.size();
		}


		/**
		 *  Gets the ValueAt attribute of the SkillTableModel object
		 *
		 * @param  row  Description of Parameter
		 * @param  col  Description of Parameter
		 * @return      The ValueAt value
		 * @since
		 */
		public Object getValueAt(int row, int col)
		{
			Object retVal = "";
			if (row >= 0 && row < data.size())
			{
				Skill aSkill = (Skill)data.elementAt(row);
				if (aSkill == null)
					return retVal;
				switch (col)
				{
					case 0:
						retVal = aSkill.getName();
						break;
					case 1:
						retVal = aSkill.keyStat();
						break;
					case 2:
						retVal = aSkill.modifier();
						break;
					case 3:
						retVal = aSkill.getTotalRank();
						break;
					case 4:
						retVal = new Integer(aSkill.modifier().intValue() + aSkill.getTotalRank().intValue());
						break;
					case 5:
						retVal = aSkill.untrained();
						break;
					case 6:
						retVal = aSkill.costForPCClass((PCClass)currCharacterClass.getSelectedItem());
						break;
					case 7:
						retVal = aSkill.getSource();
						break;
					default:
						System.out.println("Error in SkillTableModel getValueAt(" + row + "," + col + ")");
						//XYZ Impossible, should be logged
						break;
				}
			}
			return retVal;
		}


		// The default implementations of these methods in
		// AbstractTableModel would work, but we can refine them.

		/**
		 *  Gets the ColumnName attribute of the SkillTableModel object
		 *
		 * @param  column  Description of Parameter
		 * @return         The ColumnName value
		 * @since
		 */
		public String getColumnName(int column)
		{
			return nameList[column];
		}


		/**
		 *  Gets the CellEditable attribute of the SkillTableModel object
		 *
		 * @param  row  Description of Parameter
		 * @param  col  Description of Parameter
		 * @return      The CellEditable value
		 * @since
		 */
		public boolean isCellEditable(int row, int col)
		{
			return false;
		}
	}
}
