/*
 * InfoSkills.java
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
 * Created on May 1, 2001, 5:57 PM
 * ReCreated on Feb 22, 2002 7:45 AM
 */

package pcgen.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.EventObject;
import java.util.Iterator;
import java.util.StringTokenizer;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.tree.TreePath;
import pcgen.core.Constants;
import pcgen.core.Globals;
import pcgen.core.PCClass;
import pcgen.core.PObject;
import pcgen.core.PlayerCharacter;
import pcgen.core.SettingsHandler;
import pcgen.core.Skill;
import pcgen.gui.filter.AbstractPObjectFilter;
import pcgen.gui.filter.FilterAdapterPanel;
import pcgen.gui.filter.FilterFactory;
import pcgen.gui.filter.PObjectFilter;
import pcgen.gui.panes.FlippingSplitPane;
import pcgen.util.Delta;

/**
 * @author  Bryan McRoberts (merton_monk@yahoo.com), Jason Buchanan (lonejedi70@hotmail.com), Jayme Cox (jaymecox@netscape.net)
 * @version $Revision: 1.1 $
 */

class InfoSkills extends FilterAdapterPanel
{
	private final JLabel avaLabel = new JLabel("Display By");
	private final JLabel selLabel = new JLabel("Display By");
	private JButton leftButton;
	private JButton rightButton;
	private JScrollPane cScroll = new JScrollPane();
	private JLabelPane infoLabel = new JLabelPane();
	private JPanel center = new JPanel();
	private Border etched;
	private TitledBorder titled;
	private FlippingSplitPane splitPane;
	private FlippingSplitPane bsplit;
	private FlippingSplitPane asplit;
	private JCheckBox autoSort = new JCheckBox("Auto-sort output", true);

	protected SkillModel availableModel = null;
	protected SkillModel selectedModel = null;
	protected JTreeTable availableTable;
	protected JTreeTable selectedTable;

	private JTreeTableSorter availableSort = null;
	private JTreeTableSorter selectedSort = null;

	//keep track of which skill was selected last from either table
	private Skill lastSkill = null;

	private static boolean needsUpdate = true;
	protected static PlayerCharacter aPC = null;

	protected JComboBox viewComboBox = new JComboBox();
	protected JComboBox viewSelectComboBox = new JComboBox();
	private static int splitOrientation = FlippingSplitPane.HORIZONTAL_SPLIT;

	//column positions for tables
	private static final int COL_NAME = 0;
	private static final int COL_MOD = 1;
	private static final int COL_RANK = 2;
	private static final int COL_TOTAL = 3;
	private static final int COL_COST = 4;
	private static final int COL_SRC = 5;
	private static final int COL_INDEX = 6;

	// keep track of view mode for Available. defaults to "Cost/Name"
	private static int viewMode = GuiConstants.INFOSKILLS_VIEW_COST_NAME;
	// keep track of view mode for Selected. defaults to "Name"
	private static int viewSelectMode = GuiConstants.INFOSKILLS_VIEW_NAME;

	//table model modes
	private static final int MODEL_AVAIL = 0;
	private static final int MODEL_SELECT = 1;

	// Right-click table item
	private int selRow;
	private TreePath selPath;
	private boolean hasBeenSized = false;

	private JPanel jPanel1 = new JPanel();

	private JLabel jLbMaxSkill = new JLabel();
	private JLabel jLbMaxCrossSkill = new JLabel();
	private JLabel jLbClassSkillPoints = new JLabel();
	private JComboBox currCharacterClass = new JComboBox();
	private JLabel jLbTotalSkillPointsLeft = new JLabel();
	private JLabel maxSkillRank = new JLabel();
	private JLabel maxCrossSkillRank = new JLabel();
	private WholeNumberField totalSkillPointsLeft = new WholeNumberField(0, 4);
	private WholeNumberField currCharClassSkillPnts = new WholeNumberField(0, 4);
	private JLabel includeLabel = new JLabel();
	private JComboBox skillChoice = new JComboBox();
	private JLabel exclusiveLabel = new JLabel();
	private JTextField exclusiveSkillCost = new JTextField();

	private static PObjectNode typeKeystatSubtypeRoot;
	private static PObjectNode typeKeystatRoot;
	private static PObjectNode typeSubtypeRoot;
	private static PObjectNode typeCostRoot;

	private SkillWrapper skillA = new SkillWrapper(null, new Integer(0), new Float(0), new Integer(0));

	/**
	 *  a wrapper for Skill, mods and ranks
	 */
	class SkillWrapper
	{
		private Skill _aSkill = null;
		private Integer _mod;
		private Float _ranks;
		private Integer _outputIndex;

		public SkillWrapper(Skill aSkill, Integer mod, Float ranks, Integer outputIndex)
		{
			_aSkill = aSkill;
			_mod = mod;
			_ranks = ranks;
			_outputIndex = outputIndex;

		}

		public String toString()
		{
			if (_aSkill == null)
				return "";
//			return _aSkill.getName();
			return _aSkill.piString();
		}

		public Skill getSkWrapSkill()
		{
			return _aSkill;
		}

		public Integer getSkWrapMod()
		{
			return _mod;
		}

		public Float getSkWrapRank()
		{
			return _ranks;
		}

		public Integer getSkWrapOutputIndex()
		{
			return _outputIndex;
		}

	}

	private class SkillPopupMenu extends JPopupMenu
	{
		private class SkillActionListener implements ActionListener
		{
			protected int qty = 0;

			protected SkillActionListener(int aQty)
			{
				qty = aQty;
			}

			public void actionPerformed(ActionEvent evt)
			{
			}
		}

		private class AddSkillActionListener extends SkillActionListener
		{
			AddSkillActionListener(int qty)
			{
				super(qty);
			}

			public void actionPerformed(ActionEvent evt)
			{
				int newQty = qty;
				// Get a number from the user via a popup
				if (qty < 0)
				{
					String selectedValue = JOptionPane.showInputDialog(null, "Enter Quantity to Add", Constants.s_APPNAME, JOptionPane.QUESTION_MESSAGE);
					if (selectedValue != null)
					{
						try
						{
							//abs just in case someone types in a negative value
							newQty = Math.abs(new Integer(selectedValue.trim()).intValue());
						}
						catch (Exception e)
						{
							JOptionPane.showMessageDialog(null, "Invalid number!", Constants.s_APPNAME, JOptionPane.ERROR_MESSAGE);
							return;
						}
					}
					else
					{
						return;
					}
				}
				addSkill(evt, newQty);
			}
		}

		private class MaxSkillActionListener extends SkillActionListener
		{
			//qty should remain unused by this derived class
			MaxSkillActionListener(int qty)
			{
				super(qty);
			}

			public void actionPerformed(ActionEvent evt)
			{
				Skill aSkill = getSelectedSkill();
				if (aSkill == null)
					return;
				final PlayerCharacter aPC = Globals.getCurrentPC();
				aPC.setDirty(true);
				//if the PC already has this skill, then link to it so that we get accurate existing rank info
				Skill bSkill = aPC.getSkillNamed(aSkill.getName());
				if (bSkill != null)
				{
					aSkill = bSkill;
					bSkill = null;
				}
				PCClass aClass = getSelectedPCClass();
				double maxRank = 0.0;
				double skillPool = 0.0;
				if (aClass != null)
				{
					maxRank = aPC.getMaxRank(aSkill.getName(), aClass).doubleValue();
					skillPool = aClass.skillPool().doubleValue();
				}
				if (maxRank > aSkill.getRank().doubleValue() || SettingsHandler.isBoolBypassMaxSkillRank())
				{
					final int cost = aSkill.costForPCClass(getSelectedPCClass()).intValue();
					final double pointsNeeded = Math.floor((maxRank - aSkill.getTotalRank().doubleValue()) * cost);
					final Double points = new Double(Math.min(pointsNeeded, skillPool));
					addSkill(null, points.intValue());
				}
				else
					JOptionPane.showMessageDialog(null, "Cannot raise this skill further.", "PCGen", JOptionPane.INFORMATION_MESSAGE);
			}
		}

		private class RemoveSkillActionListener extends SkillActionListener
		{
			RemoveSkillActionListener(int qty)
			{
				super(qty);
			}

			public void actionPerformed(ActionEvent evt)
			{
				int newQty = qty;
				// Get a number from the user via a popup
				if (qty < 0)
				{
					String selectedValue = JOptionPane.showInputDialog(null, "Enter Quantity to Remove", Constants.s_APPNAME, JOptionPane.QUESTION_MESSAGE);
					if (selectedValue != null)
					{
						try
						{
							//abs just in case someone types in a negative value
							newQty = Math.abs(new Integer(selectedValue.trim()).intValue());
						}
						catch (Exception e)
						{
							JOptionPane.showMessageDialog(null, "Invalid number!", Constants.s_APPNAME, JOptionPane.ERROR_MESSAGE);
							return;
						}
					}
					else
					{
						return;
					}
				}
				addSkill(evt, -newQty);
			}
		}

		private class ResetSkillActionListener extends SkillActionListener
		{
			//qty should remain unused by this derived class
			ResetSkillActionListener(int qty)
			{
				super(qty);
			}

			public void actionPerformed(ActionEvent evt)
			{
				Skill aSkill = aPC.getSkillNamed(getSelectedSkill().getName());
				if (aSkill != null)
				{
					//remove all ranks from this skill for all PCClasses
					for (Iterator iter = Globals.getCurrentPC().getClassList().iterator(); iter.hasNext();)
					{
						PCClass aClass = (PCClass)iter.next();
						aSkill.setZeroRanks(aClass);
					}
					//
					// Remove the skill from the skill list if we've just set the rank to zero
					// and it is not an untrained skill
					//
					if ((aSkill.getRank().doubleValue() == 0.0) && !aSkill.getUntrained().startsWith("Y"))
					{
						aPC.getSkillList().remove(aSkill);
					}
					//don't need to update availableTable
					selectedTable.updateUI(); //cause the ranks in the table display to update
					currCharacterClassActionPerformed(null); //cause the available skill points to update
				}
			}
		}

		private JMenuItem createAddMenuItem(String label, int qty, String accelerator)
		{
			return Utility.createMenuItem(label, new AddSkillActionListener(qty), "add " + qty, (char)0, accelerator, "Add " + (qty < 0 ? "n" : Integer.toString(qty)) + " skill point" + (qty == 1 ? "" : "s"), "Add16.gif", true);
		}

		private JMenuItem createMaxMenuItem(String label, String accelerator)
		{
			return Utility.createMenuItem(label, new MaxSkillActionListener(0), "max ranks", (char)0, accelerator, "Set to max ranks", "Add16.gif", true);
		}

		private JMenuItem createRemoveMenuItem(String label, int qty, String accelerator)
		{
			return Utility.createMenuItem(label, new RemoveSkillActionListener(qty), "remove " + qty, (char)0, accelerator, "Remove " + (qty < 0 ? "n" : Integer.toString(qty)) + " skill point" + (qty == 1 ? "" : "s"), "Remove16.gif", true);
		}

		private JMenuItem createResetMenuItem(String label, String accelerator)
		{
			return Utility.createMenuItem(label, new ResetSkillActionListener(0), "reset ranks", (char)0, accelerator, "Reset to zero ranks", "Add16.gif", true);
		}

		SkillPopupMenu(JTreeTable treeTable)
		{
			if (treeTable == availableTable)
			{
				/*
				 * jikes says:
				 *   "Ambiguous reference to member 'add' inherited from
				 *    type 'javax/swing/JPopupMenu' but also declared or
				 *    inherited in the enclosing type 'pcgen/gui/InfoInventory'.
				 *    Explicit qualification is required."
				 * Well, let's do what jikes wants us to do ;-)
				 *
				 * author: Thomas Behr 08-02-02
				 *
				 * changed accelerator from "control PLUS" to "control EQUALS" as cannot
				 * get "control PLUS" to function on standard US keyboard with Windows 98
				 */
				SkillPopupMenu.this.add(createAddMenuItem("Add  1", 1, "control EQUALS"));
				SkillPopupMenu.this.add(createAddMenuItem("Add  2", 2, null));
				SkillPopupMenu.this.add(createAddMenuItem("Add  5", 5, null));
				SkillPopupMenu.this.add(createAddMenuItem("Add  10", 10, null));
				SkillPopupMenu.this.add(createAddMenuItem("Add  n", -1, "alt A"));
				SkillPopupMenu.this.add(createMaxMenuItem("Max Ranks", "alt M"));
			}

			else // selectedTable
			{
				/*
				 * jikes says:
				 *   "Ambiguous reference to member 'add' inherited from
				 *    type 'javax/swing/JPopupMenu' but also declared or
				 *    inherited in the enclosing type 'pcgen/gui/InfoInventory'.
				 *    Explicit qualification is required."
				 * Well, let's do what jikes wants us to do ;-)
				 *
				 * author: Thomas Behr 08-02-02
				 *
				 * changed accelerator from "control PLUS" to "control EQUALS" as cannot
				 * get "control PLUS" to function on standard US keyboard with Windows 98
				 */
				SkillPopupMenu.this.add(createAddMenuItem("Add  1", 1, "control EQUALS"));
				SkillPopupMenu.this.add(createAddMenuItem("Add  2", 2, null));
				SkillPopupMenu.this.add(createAddMenuItem("Add  5", 5, null));
				SkillPopupMenu.this.add(createAddMenuItem("Add  10", 10, null));
				SkillPopupMenu.this.add(createAddMenuItem("Add  n", -1, "alt A"));
				SkillPopupMenu.this.add(createRemoveMenuItem("Remove  1", 1, "control MINUS"));
				SkillPopupMenu.this.add(createRemoveMenuItem("Remove  2", 2, null));
				SkillPopupMenu.this.add(createRemoveMenuItem("Remove  5", 5, null));
				SkillPopupMenu.this.add(createRemoveMenuItem("Remove  10", 10, null));
				SkillPopupMenu.this.add(createRemoveMenuItem("Remove  n", -1, "alt R"));
				SkillPopupMenu.this.add(createMaxMenuItem("Max Ranks", "alt M"));
				SkillPopupMenu.this.add(createResetMenuItem("Zero Ranks", "alt Z"));
			}
		}
	}

	private class SkillPopupListener extends MouseAdapter
	{
		private JTree tree;
		private SkillPopupMenu menu;

		SkillPopupListener(JTreeTable treeTable, SkillPopupMenu aMenu)
		{
			tree = treeTable.getTree();
			menu = aMenu;
			KeyListener myKeyListener = new KeyListener()
			{
				public void keyTyped(KeyEvent e)
				{
					dispatchEvent(e);
				}

				public void keyPressed(KeyEvent e)
				{
					final int keyCode = e.getKeyCode();
					if (keyCode != KeyEvent.VK_UNDEFINED)
					{
						final KeyStroke keyStroke = KeyStroke.getKeyStrokeForEvent(e);
						for (int i = 0; i < menu.getComponentCount(); i++)
						{
							final JMenuItem menuItem = (JMenuItem)menu.getComponent(i);
							javax.swing.KeyStroke ks = menuItem.getAccelerator();
							if ((ks != null) && keyStroke.equals(ks))
							{
								selPath = tree.getSelectionPath();
								menuItem.doClick(2);
								return;
							}
						}
					}
					dispatchEvent(e);
				}

				public void keyReleased(KeyEvent e)
				{
					dispatchEvent(e);
				}
			};
			treeTable.addKeyListener(myKeyListener);
		}

		public void mousePressed(MouseEvent evt)
		{
			maybeShowPopup(evt);
		}

		public void mouseReleased(MouseEvent evt)
		{
			maybeShowPopup(evt);
		}

		private void maybeShowPopup(MouseEvent evt)
		{
			if (evt.isPopupTrigger())
			{
				selRow = tree.getRowForLocation(evt.getX(), evt.getY());
				if (selRow == -1) return;
				selPath = tree.getPathForLocation(evt.getX(), evt.getY());
				if (selPath == null) return;
				tree.setSelectionPath(selPath);
				menu.show(evt.getComponent(), evt.getX(), evt.getY());
			}
		}
	}

	private void hookupPopupMenu(JTreeTable treeTable)
	{
		treeTable.addMouseListener(new SkillPopupListener(treeTable, new SkillPopupMenu(treeTable)));
	}

	public InfoSkills()
	{
		// do not remove this
		// we will use the component's name to save component specific settings
		setName("Skills");

		initComponents();
		initActionListeners();

		FilterFactory.restoreFilterSettings(this);
	}

	public void setNeedsUpdate(boolean flag)
	{
		needsUpdate = flag;
	}

	//Set up GridBag Constraints
	private void buildConstraints(GridBagConstraints gbc, int gx, int gy, int gw, int gh, int wx, int wy)
	{
		gbc.gridx = gx;
		gbc.gridy = gy;
		gbc.gridwidth = gw;
		gbc.gridheight = gh;
		gbc.weightx = wx;
		gbc.weighty = wy;
	}

	/** This method is called from within the constructor to
	 * initialize the form.
	 */
	private void initComponents()
	{
		//
		// Sanity check
		//
		int iView = SettingsHandler.getSkillsTab_AvailableListMode();
		if ((iView >= GuiConstants.INFOSKILLS_VIEW_STAT_TYPE_NAME) && (iView <= GuiConstants.INFOSKILLS_VIEW_NAME))
		{
			viewMode = iView;
		}
		SettingsHandler.setSkillsTab_AvailableListMode(viewMode);
		iView = SettingsHandler.getSkillsTab_SelectedListMode();
		if ((iView >= GuiConstants.INFOSKILLS_VIEW_STAT_TYPE_NAME) && (iView <= GuiConstants.INFOSKILLS_VIEW_NAME))
		{
			viewSelectMode = iView;
		}
		SettingsHandler.setSkillsTab_SelectedListMode(viewSelectMode);

		initRootNodes();

		viewComboBox.addItem("KeyStat/SubType/Name");
		viewComboBox.addItem("KeyStat/Name");
		viewComboBox.addItem("SubType/Name");
		viewComboBox.addItem("Cost/Name");
		viewComboBox.addItem("Name");
		Utility.setDescription(viewComboBox, "You can change how the Skills in the Tables are listed.");
		viewComboBox.setSelectedIndex(viewMode);       // must be done before createModels call

		viewSelectComboBox.addItem("KeyStat/SubType/Name");
		viewSelectComboBox.addItem("KeyStat/Name");
		viewSelectComboBox.addItem("SubType/Name");
		viewSelectComboBox.addItem("Cost/Name");
		viewSelectComboBox.addItem("Name");
		Utility.setDescription(viewSelectComboBox, "You can change how the Skills in the Tables are listed.");
		viewSelectComboBox.setSelectedIndex(viewSelectMode); // must be done before createModels call

		aPC = Globals.getCurrentPC();

		createModels();

		// create available table of skills
		createTreeTables();

		center.setLayout(new BorderLayout());

		GridBagLayout gridbag = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		JPanel leftPane = new JPanel();
		JPanel rightPane = new JPanel();
		leftPane.setLayout(gridbag);
		splitPane = new FlippingSplitPane(splitOrientation, leftPane, rightPane);
		splitPane.setOneTouchExpandable(true);
		splitPane.setDividerSize(10);

		center.add(splitPane, BorderLayout.CENTER);

		buildConstraints(c, 0, 0, 1, 1, 100, 5);
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.CENTER;
		JPanel aPanel = new JPanel();
		gridbag.setConstraints(aPanel, c);
		aPanel.add(avaLabel);
		aPanel.add(viewComboBox);
		ImageIcon newImage;
		newImage = new ImageIcon(getClass().getResource("resource/Forward16.gif"));
		rightButton = new JButton(newImage);
		Utility.setDescription(rightButton, "Click to add 1 skill point to the selected Available skill");
		rightButton.setEnabled(false);
		aPanel.add(rightButton);
		leftPane.add(aPanel);

		// set the alignment on these columns to center
		// might as well set the prefered width while we're at it
		availableTable.getColumnModel().getColumn(1).setCellRenderer(new AlignCellRenderer(SwingConstants.CENTER));
		availableTable.getColumnModel().getColumn(1).setPreferredWidth(15);
		selectedTable.getColumnModel().getColumn(0).setPreferredWidth(60);
		selectedTable.getColumnModel().getColumn(1).setCellRenderer(new AlignCellRenderer(SwingConstants.CENTER));
		selectedTable.getColumnModel().getColumn(1).setPreferredWidth(15);
		selectedTable.getColumnModel().getColumn(2).setCellRenderer(new AlignCellRenderer(SwingConstants.CENTER));
		selectedTable.getColumnModel().getColumn(2).setPreferredWidth(15);
		selectedTable.getColumnModel().getColumn(3).setCellRenderer(new AlignCellRenderer(SwingConstants.CENTER));
		selectedTable.getColumnModel().getColumn(3).setPreferredWidth(15);
		selectedTable.getColumnModel().getColumn(4).setCellRenderer(new AlignCellRenderer(SwingConstants.CENTER));
		selectedTable.getColumnModel().getColumn(4).setPreferredWidth(15);
		selectedTable.getColumnModel().getColumn(5).setCellRenderer(new OutputOrderRenderer(new String[]{"Hidden"}));
		selectedTable.getColumnModel().getColumn(5).setPreferredWidth(15);

		buildConstraints(c, 0, 1, 1, 1, 0, 95);
		c.fill = GridBagConstraints.BOTH;
		c.anchor = GridBagConstraints.CENTER;
		JScrollPane scrollPane = new JScrollPane(availableTable);
		gridbag.setConstraints(scrollPane, c);
		leftPane.add(scrollPane);

		gridbag = new GridBagLayout();
		c = new GridBagConstraints();
		rightPane.setLayout(gridbag);

		buildConstraints(c, 0, 0, 1, 1, 100, 5);
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.CENTER;
		aPanel = new JPanel();
		gridbag.setConstraints(aPanel, c);
		aPanel.add(selLabel);
		aPanel.add(viewSelectComboBox);
		newImage = new ImageIcon(getClass().getResource("resource/Back16.gif"));
		leftButton = new JButton(newImage);
		Utility.setDescription(leftButton, "Click to remove 1 skill point from the Selected skill");
		leftButton.setEnabled(false);
		aPanel.add(leftButton);
		aPanel.add(autoSort);
		rightPane.add(aPanel);

		buildConstraints(c, 0, 1, 1, 1, 0, 95);
		c.fill = GridBagConstraints.BOTH;
		c.anchor = GridBagConstraints.CENTER;
		scrollPane = new JScrollPane(selectedTable);
		gridbag.setConstraints(scrollPane, c);
		rightPane.add(scrollPane);

		TitledBorder title1 = BorderFactory.createTitledBorder(etched, "Skill Info");
		title1.setTitleJustification(TitledBorder.CENTER);
		cScroll.setBorder(title1);
		infoLabel.setBackground(rightPane.getBackground());
		cScroll.setViewportView(infoLabel);
		//Utility.setDescription(cScroll, "Any requirements you don't meet are in italics.");  //no pre-reqs to show, wo not sure that to do in this tooltip

		jPanel1.setLayout(new GridBagLayout());
		GridBagConstraints gridBagConstraints2 = new GridBagConstraints();

		jLbMaxSkill.setText("Max Class Skill Rank:  ");
		Utility.setDescription(jLbMaxSkill, "This is the maximum skill rank the character can have.");
		jLbMaxSkill.setForeground(Color.black);
		buildConstraints(gridBagConstraints2, 0, 0, 1, 1, 5, 5);
		gridBagConstraints2.anchor = GridBagConstraints.EAST;
		jPanel1.add(jLbMaxSkill, gridBagConstraints2);

		maxSkillRank.setText(String.valueOf(PlayerCharacter.maxClassSkillForLevel(aPC.getTotalLevels() + aPC.totalHitDice())));
		maxSkillRank.setForeground(Color.black);
		buildConstraints(gridBagConstraints2, 1, 0, 1, 1, 5, 5);
		gridBagConstraints2.anchor = GridBagConstraints.WEST;
		jPanel1.add(maxSkillRank, gridBagConstraints2);

		jLbMaxCrossSkill.setText("Max Cross-Class Skill Rank:  ");
		Utility.setDescription(jLbMaxCrossSkill, "This is the maximum cross-class skill rank the character can have.");
		jLbMaxCrossSkill.setForeground(Color.black);
		buildConstraints(gridBagConstraints2, 0, 1, 1, 1, 5, 5);
		gridBagConstraints2.anchor = GridBagConstraints.EAST;
		jPanel1.add(jLbMaxCrossSkill, gridBagConstraints2);

		maxCrossSkillRank.setText(PlayerCharacter.maxCrossClassSkillForLevel(aPC.getTotalLevels() + aPC.totalHitDice()).toString());
		maxCrossSkillRank.setForeground(Color.black);
		buildConstraints(gridBagConstraints2, 1, 1, 1, 1, 5, 5);
		gridBagConstraints2.anchor = GridBagConstraints.WEST;
		jPanel1.add(maxCrossSkillRank, gridBagConstraints2);

		jLbClassSkillPoints.setText("Skill Points Left for Class:  ");
		jLbClassSkillPoints.setForeground(Color.black);
		buildConstraints(gridBagConstraints2, 0, 3, 2, 1, 5, 5);
		gridBagConstraints2.anchor = GridBagConstraints.EAST;
		jPanel1.add(jLbClassSkillPoints, gridBagConstraints2);

		for (Iterator i = aPC.getClassList().iterator(); i.hasNext();)
		{
			currCharacterClass.addItem(i.next());
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
		buildConstraints(gridBagConstraints2, 2, 3, 1, 1, 5, 5);
		gridBagConstraints2.anchor = GridBagConstraints.CENTER;
		final int oldFill = gridBagConstraints2.fill;
		gridBagConstraints2.fill = GridBagConstraints.HORIZONTAL;
		jPanel1.add(currCharacterClass, gridBagConstraints2);
		gridBagConstraints2.fill = oldFill;

		jLbTotalSkillPointsLeft.setText("Total Skill Points Left:  ");
		jLbTotalSkillPointsLeft.setForeground(Color.black);
		buildConstraints(gridBagConstraints2, 2, 2, 1, 1, 5, 5);
		gridBagConstraints2.anchor = GridBagConstraints.EAST;
		jPanel1.add(jLbTotalSkillPointsLeft, gridBagConstraints2);

		totalSkillPointsLeft.addFocusListener(new FocusAdapter()
		{
			public void focusLost(FocusEvent evt)
			{
				totalSkillPointsLeftFocusLost(evt);
			}
		});
		buildConstraints(gridBagConstraints2, 3, 2, 1, 1, 5, 5);
		gridBagConstraints2.anchor = GridBagConstraints.WEST;
		totalSkillPointsLeft.setMinimumSize(new Dimension(40, 17));
		totalSkillPointsLeft.setPreferredSize(new Dimension(40, 17));
		jPanel1.add(totalSkillPointsLeft, gridBagConstraints2);

		currCharClassSkillPnts.addFocusListener(new FocusAdapter()
		{
			public void focusLost(FocusEvent evt)
			{
				currCharClassSkillPntsFocusLost(evt);
			}
		});
		buildConstraints(gridBagConstraints2, 3, 3, 1, 1, 5, 5);
		gridBagConstraints2.anchor = GridBagConstraints.WEST;
		currCharClassSkillPnts.setPreferredSize(new Dimension(40, 20));
		currCharClassSkillPnts.setMinimumSize(new Dimension(40, 20));
		jPanel1.add(currCharClassSkillPnts, gridBagConstraints2);

		includeLabel = new JLabel("Include Skills: ");
		buildConstraints(gridBagConstraints2, 2, 0, 1, 1, 5, 5);
		gridBagConstraints2.anchor = GridBagConstraints.EAST;
		jPanel1.add(includeLabel, gridBagConstraints2);

		skillChoice.setModel(new DefaultComboBoxModel(new String[]{"None", "Untrained", "All"}));
		skillChoice.setMaximumRowCount(3);
		Utility.setDescription(skillChoice, "How many skills on character sheets?");
		skillChoice.setMinimumSize(new Dimension(98, 22));
		skillChoice.setSelectedIndex(SettingsHandler.getIncludeSkills());
		skillChoice.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				skillChoiceActionPerformed(evt);
			}
		});
		buildConstraints(gridBagConstraints2, 3, 0, 1, 1, 5, 5);
		gridBagConstraints2.anchor = GridBagConstraints.WEST;
		jPanel1.add(skillChoice, gridBagConstraints2);

		exclusiveLabel = new JLabel("Exclusive Skill Cost: ");
		Utility.setDescription(exclusiveLabel, "What it costs to buy a skill that is normally unavailable to one's class (0 means that it can't be bought)");
		buildConstraints(gridBagConstraints2, 2, 1, 1, 1, 5, 5);
		gridBagConstraints2.anchor = GridBagConstraints.EAST;
		jPanel1.add(exclusiveLabel, gridBagConstraints2);

		exclusiveSkillCost.setColumns(3);
		exclusiveSkillCost.setText("0");
		exclusiveSkillCost.setMinimumSize(new Dimension(40, 17));
		exclusiveSkillCost.setText(Integer.toString(SettingsHandler.getExcSkillCost()));
		exclusiveSkillCost.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				updateSkillCost();
			}
		});
		exclusiveSkillCost.addFocusListener(new FocusAdapter()
		{
			public void focusLost(FocusEvent evt)
			{
				excCostFocusEvent(evt);
			}
		});
		buildConstraints(gridBagConstraints2, 3, 1, 1, 1, 5, 5);
		gridBagConstraints2.anchor = GridBagConstraints.WEST;
		jPanel1.add(exclusiveSkillCost, gridBagConstraints2);

		asplit = new FlippingSplitPane(FlippingSplitPane.HORIZONTAL_SPLIT, cScroll, jPanel1);
		asplit.setOneTouchExpandable(true);
		asplit.setDividerSize(10);

		JPanel botPane = new JPanel();
		botPane.setLayout(new BorderLayout());
		botPane.add(asplit, BorderLayout.CENTER);
		bsplit = new FlippingSplitPane(FlippingSplitPane.VERTICAL_SPLIT, center, botPane);
		bsplit.setOneTouchExpandable(true);
		bsplit.setDividerSize(10);

		this.setLayout(new BorderLayout());
		this.add(bsplit, BorderLayout.CENTER);

		availableSort = new JTreeTableSorter(availableTable, (PObjectNode)availableModel.getRoot(), availableModel);
		selectedSort = new JTreeTableSorter(selectedTable, (PObjectNode)selectedModel.getRoot(), selectedModel);

	}

	private void initRootNodes()
	{
		typeKeystatSubtypeRoot = new PObjectNode();
		typeKeystatRoot = new PObjectNode();
		typeSubtypeRoot = new PObjectNode();
		typeCostRoot = new PObjectNode();
		ArrayList aList = new ArrayList(); //this would correspond to KeyStat
		ArrayList bList = new ArrayList(); //this, for KeyStat/Subtype
		ArrayList cList = new ArrayList(); //this, for Subtype
		ArrayList dList = new ArrayList(); //this, for Class
		for (Iterator i = Globals.getSkillList().iterator(); i.hasNext();)
		{
			final Skill aSkill = (Skill)i.next();
			if (aSkill.getMyTypeCount() > 0)
				if (!aList.contains(aSkill.getMyType(0)))
					aList.add(aSkill.getMyType(0));
			boolean pastFirst = false; //we'll use this to skip adding the first type (KeyStat) to cList (for Subtype only)
			for (int ii = 0; ii < aSkill.getMyTypeCount(); ii++)
			{
				final String aString = aSkill.getMyType(ii);
				if (!bList.contains(aString))
					bList.add(aString);
				if (pastFirst && !cList.contains(aString))
					cList.add(aString);
				pastFirst = true;
			}
		}
		Collections.sort(aList);
		Collections.sort(bList);
		Collections.sort(cList);
		dList.add(Skill.COST_CLASS);
		dList.add(Skill.COST_XCLASS);
		dList.add(Skill.COST_EXCL);

		PObjectNode[] p1 = new PObjectNode[aList.size()];
		PObjectNode[] p2 = new PObjectNode[aList.size()];
		PObjectNode[] p3 = new PObjectNode[cList.size()];
		PObjectNode[] p4 = new PObjectNode[dList.size()];
		for (int i = 0; i < aList.size(); i++)
		{
			p1[i] = new PObjectNode();
			p1[i].setItem(aList.get(i).toString());
			p1[i].setParent(typeKeystatSubtypeRoot);
			p2[i] = new PObjectNode();
			p2[i].setItem(aList.get(i).toString());
			p2[i].setParent(typeKeystatRoot);
		}
		for (int i = 0; i < cList.size(); i++)
		{
			p3[i] = new PObjectNode();
			p3[i].setItem(cList.get(i).toString());
			p3[i].setParent(typeSubtypeRoot);
		}
		for (int i = 0; i < dList.size(); i++)
		{
			p4[i] = new PObjectNode();
			p4[i].setItem(dList.get(i).toString());
			p4[i].setParent(typeCostRoot);
		}
		typeKeystatSubtypeRoot.setChildren(p1, false);
		typeKeystatRoot.setChildren(p2, false);
		typeSubtypeRoot.setChildren(p3, false);
		typeCostRoot.setChildren(p4, false);

		for (int i = 0; i < p1.length; i++)
		{
			aList.clear();
			for (int j = 0; j < Globals.getSkillList().size(); j++)
			{
				final Skill bSkill = (Skill)Globals.getSkillList().get(j);
				final String topType = p1[i].toString();
				if (!bSkill.isType(topType))
				{
					continue;
				}

				final StringTokenizer aTok = new StringTokenizer(bSkill.getType(), ".", false);
				//String aString = aTok.nextToken(); // skip first one, already in top-level
				while (aTok.hasMoreTokens())
				{
					final String aString = aTok.nextToken();
					if (!aString.equals(topType) && !aList.contains(aString))
					{
						aList.add(aString);
					}
				}
			}
			Collections.sort(aList);
			for (Iterator lI = aList.iterator(); lI.hasNext();)
			{
				String aString = (String)lI.next();
				PObjectNode d = new PObjectNode();
				d.setParent(p1[i]);
				p1[i].addChild(d);
				d.setItem(aString);
			}
		}
	}

	private void initActionListeners()
	{
		addComponentListener(new ComponentAdapter()
		{
			public void componentShown(ComponentEvent evt)
			{
				formComponentShown(evt);
			}
		});
		addComponentListener(new ComponentAdapter()
		{
			public void componentResized(ComponentEvent e)
			{
//				bsplit.setDividerLocation((int)(InfoSkills.this.getSize().getHeight() - 101));
//				asplit.setDividerLocation((int)(InfoSkills.this.getSize().getWidth() - 408));
				int s = splitPane.getDividerLocation();
				if (s > 0)
					SettingsHandler.setPCGenOption("InfoSkills.splitPane", s);
				s = asplit.getDividerLocation();
				if (s > 0)
					SettingsHandler.setPCGenOption("InfoSkills.asplit", s);
				s = bsplit.getDividerLocation();
				if (s > 0)
					SettingsHandler.setPCGenOption("InfoSkills.bsplit", s);
			}
		});
		leftButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				addSkill(evt, -1);
			}
		});
		rightButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				addSkill(evt, 1);
			}
		});
		viewComboBox.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				viewComboBoxActionPerformed(evt);
			}
		});
		viewSelectComboBox.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				viewSelectComboBoxActionPerformed(evt);
			}
		});
	}

	private void currCharacterClassActionPerformed(ActionEvent evt)
	{
//Globals.errorPrint("     --start-- cCCAP: ");
		final PCClass aClass = this.getSelectedPCClass();
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

		// create models will get called from updateCharacterInfo
		// if needsUpdate is true, so don't do it here
		if (needsUpdate)
			return;

		//we need to do this in order for class/xclass views to re-sort
		createModels();
		selectedTable.updateUI();
		availableTable.updateUI();

//Globals.errorPrint("     --done-- cCCAP: ");
	}

	private void totalSkillPointsLeftFocusLost(FocusEvent evt)
	{
		final PlayerCharacter aPC = Globals.getCurrentPC();
		aPC.setDirty(true);
		if (totalSkillPointsLeft.getText().length() > 0)
		{
			final int anInt = Delta.decode(totalSkillPointsLeft.getText()).intValue();
			if (anInt == aPC.getSkillPoints())
				return;
			aPC.setSkillPoints(anInt);
			final int x = aPC.getClassList().size();
			final int y = anInt / x;
			PCClass aClass = null;
			for (Iterator i = aPC.getClassList().iterator(); i.hasNext();)
			{
				aClass = (PCClass)i.next();
				aClass.setSkillPool(Math.max(0, y));
			}
			aClass = getSelectedPCClass();
			if (aClass != null)
				currCharClassSkillPnts.setValue(aClass.skillPool().intValue());
		}
		totalSkillPointsLeft.setValue(aPC.getSkillPoints());
	}

	private void currCharClassSkillPntsFocusLost(FocusEvent evt)
	{
		final PlayerCharacter aPC = Globals.getCurrentPC();
		aPC.setDirty(true);
		PCClass aClass = this.getSelectedPCClass();
		if (currCharClassSkillPnts.getText().length() > 0)
		{
			final int anInt = Delta.decode(currCharClassSkillPnts.getText()).intValue();
			if (aClass == null || anInt == aClass.skillPool().intValue())
				return;
			final int i = aClass.skillPool().intValue() - anInt;
			aClass.setSkillPool(Math.max(0, anInt));
			aPC.setSkillPoints(Math.max(0, aPC.getSkillPoints() - i));
		}
		currCharClassSkillPnts.setValue(aClass.skillPool().intValue());
		totalSkillPointsLeft.setValue(aPC.getSkillPoints());
	}

	private void skillChoiceActionPerformed(ActionEvent evt)
	{
		final int selection = skillChoice.getSelectedIndex();
		if (selection >= 0 && selection <= 2)
		{
			SettingsHandler.setIncludeSkills(selection);
		}
	}

	private final void updateSkillCost()
	{
		try
		{
			SettingsHandler.setExcSkillCost(Integer.parseInt(exclusiveSkillCost.getText()));
		}
		catch (NumberFormatException nfe)
		{
			exclusiveSkillCost.setText(Integer.toString(SettingsHandler.getExcSkillCost()));
		}
	}

	private void excCostFocusEvent(FocusEvent evt)
	{
		updateSkillCost();
	}

	private void viewComboBoxActionPerformed(ActionEvent evt)
	{
		final int index = viewComboBox.getSelectedIndex();
		if (index != viewMode)
		{
			viewMode = index;
			SettingsHandler.setSkillsTab_AvailableListMode(viewMode);
			createAvailableModel();
			availableTable.updateUI();
		}
	}

	private void viewSelectComboBoxActionPerformed(ActionEvent evt)
	{
		final int index = viewSelectComboBox.getSelectedIndex();
		if (index != viewSelectMode)
		{
			viewSelectMode = index;
			SettingsHandler.setSkillsTab_SelectedListMode(viewSelectMode);
			createSelectedModel();
			selectedTable.updateUI();
		}
	}

	// This is called when the tab is shown.
	private void formComponentShown(ComponentEvent evt)
	{
//Globals.errorPrint("  --start-- fCS");
		requestFocus();
		PCGen_Frame1.getStatusBar().setText("");
		updateCharacterInfo();
		int s = splitPane.getDividerLocation();
		int t = bsplit.getDividerLocation();
		int u = asplit.getDividerLocation();
		int width;
		if (!hasBeenSized)
		{
			hasBeenSized = true;
			s = SettingsHandler.getPCGenOption("InfoSkills.splitPane", (int)(this.getSize().getWidth() * 4 / 10));
			t = SettingsHandler.getPCGenOption("InfoSkills.bsplit", (int)(this.getSize().getHeight() - 101));
			u = SettingsHandler.getPCGenOption("InfoSkills.asplit", (int)(this.getSize().getWidth() - 408));

			// set the prefered width on selectedTable
			final TableColumnModel selectedTableColumnModel = selectedTable.getColumnModel();
			for (int i = 0; i < selectedTable.getColumnCount(); i++)
			{
				TableColumn sCol = selectedTableColumnModel.getColumn(i);
				width = Globals.getCustColumnWidth("InfoSel", i);
				if (width != 0)
					sCol.setPreferredWidth(width);
				sCol.addPropertyChangeListener(new ResizeColumnListener(selectedTable, "InfoSel", i));
				if (i == 5)
				{
					sCol.setCellEditor(new OutputOrderEditor(new String[]{"First", "Last", "Hidden"}));
				}
			}
			// set the prefered width on availableTable
			for (int i = 0; i < availableTable.getColumnCount(); i++)
			{
				final TableColumnModel availableTableColumnModel = availableTable.getColumnModel();
				TableColumn sCol = availableTableColumnModel.getColumn(i);
				width = Globals.getCustColumnWidth("InfoAva", i);
				if (width != 0)
					sCol.setPreferredWidth(width);
				sCol.addPropertyChangeListener(new ResizeColumnListener(availableTable, "InfoAva", i));
			}
		}
		if (s > 0)
		{
			splitPane.setDividerLocation(s);
			SettingsHandler.setPCGenOption("InfoSkills.splitPane", s);
		}
		if (t > 0)
		{
			bsplit.setDividerLocation(t);
			SettingsHandler.setPCGenOption("InfoSkills.bsplit", t);
		}
		if (u > 0)
		{
			asplit.setDividerLocation(u);
			SettingsHandler.setPCGenOption("InfoSkills.asplit", u);
		}
//Globals.errorPrint("  --done-- fCS");
	}

	//
	// This recalculates everything for the currently selected character
	//
	public void updateCharacterInfo()
	{
//Globals.errorPrint("   --start-- uCI: needsUpdate:"+needsUpdate);

		final PlayerCharacter bPC = Globals.getCurrentPC();
		if (bPC != null)
			bPC.getAllSkillList(true); // forces refresh of skills
		if (bPC != aPC)
			needsUpdate = true;
		aPC = bPC;

		if (aPC == null || !needsUpdate)
			return;

//Globals.errorPrint("    uCI: before feats:");
		aPC.setAggregateFeatsStable(false);
		aPC.setAutomaticFeatsStable(false);
		aPC.setVirtualFeatsStable(false);

		PCClass aClass = this.getSelectedPCClass();

		currCharacterClass.removeAllItems();
		for (Iterator i = aPC.getClassList().iterator(); i.hasNext();)
			currCharacterClass.addItem(i.next());
		if (aClass != null)
			currCharacterClass.setSelectedItem(aClass);
		else if (currCharacterClass.getItemCount() > 0)
			currCharacterClass.setSelectedIndex(0);

		aClass = this.getSelectedPCClass();
		if (aClass != null)
		{
			currCharClassSkillPnts.setValue(aClass.skillPool().intValue());
			totalSkillPointsLeft.setValue(aPC.getSkillPoints());
		}
		else
		{
			currCharClassSkillPnts.setValue(0);
			totalSkillPointsLeft.setValue(0);
		}
		if (SettingsHandler.isMonsterDefault())
		{
			maxSkillRank.setText(String.valueOf(PlayerCharacter.maxClassSkillForLevel(aPC.getTotalLevels() + aPC.totalHitDice())));
			maxCrossSkillRank.setText(String.valueOf(PlayerCharacter.maxCrossClassSkillForLevel(aPC.getTotalLevels() + aPC.totalHitDice())));
		}
		else
		{
			maxSkillRank.setText(String.valueOf(PlayerCharacter.maxClassSkillForLevel(aPC.getTotalLevels())));
			maxCrossSkillRank.setText(String.valueOf(PlayerCharacter.maxCrossClassSkillForLevel(aPC.getTotalLevels())));
		}
		createModels();
		availableTable.updateUI();
		selectedTable.updateUI();

		//Calculate the aggregate feat list
		aPC.aggregateFeatList();
		aPC.setAggregateFeatsStable(true);
		aPC.setAutomaticFeatsStable(true);
		aPC.setVirtualFeatsStable(true);

		needsUpdate = false;
//Globals.errorPrint("   --done-- uCI: needsUpdate:"+needsUpdate);
	}

	private void setInfoLabelText(Skill aSkill)
	{
		lastSkill = aSkill; //even if that's null
		if (aSkill != null)
		{
			StringBuffer b = new StringBuffer();
			b.append("<html><b>").append(aSkill.piSubString()).append("</b>");
			b.append(" &nbsp;<b>MAXRANK</b>:&nbsp; ").append(aPC.getMaxRank(aSkill.getName(), getSelectedPCClass()).doubleValue());
			b.append(" &nbsp;<b>TYPE</b>:&nbsp; ").append(aSkill.getType());
			b.append(" &nbsp;<b>KEY STAT</b>:&nbsp; ").append(aSkill.getKeyStat());
			b.append(" &nbsp;<b>UNTRAINED</b>:&nbsp; ").append(aSkill.getUntrained());
			final String cString = aSkill.isExclusive();
			if (cString.length() > 0)
				b.append(" &nbsp;<b>EXCLUSIVE</b>:&nbsp; ").append(cString);
			String bString = aSkill.getSource();
			if (bString.length() > 0)
				b.append(" &nbsp;<b>SOURCE</b>:").append(bString);

			b.append("</html>");
			infoLabel.setText(b.toString());
		}
	}

	private int getSelectedIndex(ListSelectionEvent e)
	{
		final DefaultListSelectionModel model = (DefaultListSelectionModel)e.getSource();
		if (model == null)
		{
			return -1;
		}
		return model.getMinSelectionIndex();
	}

	protected void createTreeTables()
	{
		availableTable = new JTreeTable(availableModel);
		availableTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		final JTree tree = availableTable.getTree();
		tree.setRootVisible(false);
		tree.setShowsRootHandles(true);
		tree.setCellRenderer(new LabelTreeCellRenderer());

		availableTable.getSelectionModel().addListSelectionListener(new ListSelectionListener()
		{
			public void valueChanged(ListSelectionEvent e)
			{
				if (!e.getValueIsAdjusting())
				{
					final int idx = getSelectedIndex(e);
					if (idx < 0)
					{
						return;
					}

					Object temp = availableTable.getTree().getPathForRow(idx).getLastPathComponent();
					/////////////////////////
					if (temp == null)
					{
						lastSkill = null;
						JOptionPane.showMessageDialog(null,
						  "No skill selected. Try again.", Constants.s_APPNAME, JOptionPane.ERROR_MESSAGE);
						return;
					}

					Skill aSkill = null;
					if (temp instanceof PObjectNode)
					{
						temp = ((PObjectNode)temp).getItem();
						if (temp instanceof SkillWrapper)
						{
							aSkill = ((SkillWrapper)temp).getSkWrapSkill();
						}
					}
					rightButton.setEnabled(aSkill != null);
					setInfoLabelText(aSkill);
				}
			}
		});

		MouseListener ml = new MouseAdapter()
		{
			public void mousePressed(MouseEvent e)
			{
				final int selRow = tree.getRowForLocation(e.getX(), e.getY());
				final TreePath selPath = tree.getPathForLocation(e.getX(), e.getY());
				if (selRow != -1)
				{
					if (e.getClickCount() == 1 && selPath != null)
					{
						tree.setSelectionPath(selPath);
					}
					else if (e.getClickCount() == 2)
					{
						addSkill(null, 1);
					}
				}
			}
		};
		tree.addMouseListener(ml);


		//
		// now do the selectedTable
		//
		selectedTable = new JTreeTable(selectedModel);
		selectedTable.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
		final JTree btree = selectedTable.getTree();
		btree.setRootVisible(false);
		btree.setShowsRootHandles(true);
		btree.setCellRenderer(new LabelTreeCellRenderer());

		selectedTable.getSelectionModel().addListSelectionListener(new ListSelectionListener()
		{
			public void valueChanged(ListSelectionEvent e)
			{
				if (!e.getValueIsAdjusting())
				{
					final int idx = getSelectedIndex(e);
					if (idx < 0)
					{
						return;
					}

					Object temp = selectedTable.getTree().getPathForRow(idx).getLastPathComponent();
					if (temp == null)
					{
						lastSkill = null;
						infoLabel.setText();
						return;
					}

					Skill aSkill = null;
					if (temp instanceof PObjectNode)
					{
						temp = ((PObjectNode)temp).getItem();
						if (temp instanceof SkillWrapper)
						{
							aSkill = ((SkillWrapper)temp).getSkWrapSkill();
						}
					}
					leftButton.setEnabled(aSkill != null);
					setInfoLabelText(aSkill);
				}
			}
		});
		ml = new MouseAdapter()
		{
			public void mousePressed(MouseEvent e)
			{
				final int selRow = btree.getRowForLocation(e.getX(), e.getY());
				final TreePath selPath = btree.getPathForLocation(e.getX(), e.getY());
				if (selRow != -1)
				{
					if (e.getClickCount() == 1)
					{
						btree.setSelectionPath(selPath);
					}
					else if (e.getClickCount() == 2)
					{
						final Skill theSkill = getSelectedSkill();
						if (theSkill != null)
						{
							if (theSkill.getChoiceList().size() == 0)
							{
								addSkill(null, -1);
							}
							else
							{
								addSkill(null, -theSkill.costForPCClass(getSelectedPCClass()).intValue());
							}
						}
					}
				}
			}
		};
		btree.addMouseListener(ml);

		hookupPopupMenu(availableTable);
		hookupPopupMenu(selectedTable);
	}

	private boolean modRank(Skill aSkill, double points)
	{
		Iterator allPCSkills;
		int nextOutputIndex = 1;

		if (points == 0.0)
		{
			return false;
		}
		Skill bSkill = aSkill;
		// the old Skills tab used cost as a double,
		// so I'll duplicate that behavior
		final double cost = aSkill.costForPCClass(getSelectedPCClass()).doubleValue();
		if (cost == 0.0)
		{
			JOptionPane.showMessageDialog(null, "You cannot modify this skill. A cost of 0 means it is exclusive.", "PCGen", JOptionPane.INFORMATION_MESSAGE);
			return false;
		}
		double rank = points / cost;
		final PlayerCharacter aPC = Globals.getCurrentPC();
		if (aSkill != null)
		{
			bSkill = aPC.addSkill(aSkill);
			// in order to get the selected table to sort properly
			// we need to sort the PC's skill list now that the
			// new skill has been added, this won't get called
			// when adding a rank to an existing skill
			// NB: This does get called on a rank change, should it be fixed?
			Collections.sort(aPC.getSkillList(), new Comparator()
			{
				// Comparator will be specific to Skill objects
				public int compare(Object obj1, Object obj2)
				{
					return ((Skill)obj1).getName().compareTo(((Skill)obj2).getName());
				}

				/**
				 * This method isn't used, only implemented to fulfill Comparator interface
				 *
				 * @param obj which it ignores
				 * @return always false
				 */
				public boolean equals(Object obj)
				{
					return false;
				}

				/**
				 * This method isn't used, only fulfilled to get rid of jlint warning to always
				 * implement hashCode if equals is implemented
				 *
				 * @return always 0
				 */
				public int hashCode()
				{
					return 0;
				}
			});

			// Now re calc the output order
			if (autoSort.isSelected())
			{
				allPCSkills = aPC.getSkillList().iterator();
				for (; allPCSkills.hasNext();)
				{
					final Skill cSkill = (Skill)allPCSkills.next();
					if (cSkill.getOutputIndex() >= 0)
						cSkill.setOutputIndex(nextOutputIndex++);
				}
			}
			else
			{
				if (bSkill.getOutputIndex() == 0)
					bSkill.setOutputIndex(getHighestOutputIndex() + 1);
			}

		}
		PCClass aClass = getSelectedPCClass();

		String aString = "";
		if (bSkill != null)
		{
			aString = bSkill.modRanks(rank, aClass);
			//
			// Remove the skill from the skill list if we've
			// just set the rank to zero and it is not untrained
			//
			if ((bSkill.getRank().doubleValue() == 0.0) && !bSkill.getUntrained().startsWith("Y"))
			{
				aPC.getSkillList().remove(bSkill);
			}
		}

		if (aString.length() > 0)
		{
			JOptionPane.showMessageDialog(null, aString, "PCGen", JOptionPane.INFORMATION_MESSAGE);
			return false;
		}
		return true;
	}

	private Skill getSelectedSkill()
	{
//		if (lastSkill == null)
//		{
//			JOptionPane.showMessageDialog(null,
//				"No skill selected. Try again.", Constants.s_APPNAME, JOptionPane.ERROR_MESSAGE);
//		}
		return lastSkill;
	}

	private void addSkill(ActionEvent evt, int points)
	{
		aPC.setDirty(true);

		Skill theSkill = getSelectedSkill();
		if (theSkill == null)
			return;

		// modRank returns true on success and false on failure
		// if we failed to add skill points, don't do anything
		if (!modRank(theSkill, points))
			return;

		PCClass aClass = this.getSelectedPCClass();
		if (aClass != null)
		{
			currCharClassSkillPnts.setValue(aClass.skillPool().intValue());
			totalSkillPointsLeft.setValue(aPC.getSkillPoints());
		}

		refreshSelectedTable();

		// ensure that the target skill gets displayed
		// in the selectedTable if you've just added skill points
		if (points > 0)
		{
			selectedTable.expandByPObjectName(theSkill.getName());
		}
	}

	/**
	 * Retrieve the highest output index used in any of the
	 * character's skills.
	 */
	private int getHighestOutputIndex()
	{
		int maxOutputIndex = 0;

		if (aPC == null)
			return 0;

		for (Iterator i = aPC.getSkillList().iterator(); i.hasNext();)
		{
			final Skill bSkill = (Skill)i.next();
			if (bSkill.getOutputIndex() > maxOutputIndex)
			{
				maxOutputIndex = bSkill.getOutputIndex();
			}
		}

		return maxOutputIndex;
	}

	private void refreshSelectedTable()
	{
		boolean[] s = new boolean[selectedTable.getTree().getRowCount()];
		// Remember which rows are expanded
		for (int i = 0; i < s.length; i++)
		{
			s[i] = selectedTable.getTree().isExpanded(i);
		}

		createSelectedModel();
		selectedTable.updateUI();

		//re-expand the rows
		for (int i = 0; i < s.length; i++)
		{
			if (s[i])
			{
				selectedTable.getTree().expandRow(i);
			}
		}
	}

	/**
	 * Creates the ClassModel that will be used.
	 */
	protected void createModels()
	{
//Globals.errorPrint("cM: createModels");
		createAvailableModel();
		createSelectedModel();
	}

	protected void createAvailableModel()
	{
		if (availableModel == null)
			availableModel = new SkillModel(viewMode, true);
		else
			availableModel.resetModel(viewMode, true, false);
		if (availableSort != null)
			availableSort.setRoot((PObjectNode)availableModel.getRoot());
		if (availableSort != null)
			availableSort.sortNodeOnColumn();
	}

	protected void createSelectedModel()
	{
		if (selectedModel == null)
			selectedModel = new SkillModel(viewSelectMode, false);
		else
			selectedModel.resetModel(viewSelectMode, false, false);
		if (selectedSort != null)
			selectedSort.setRoot((PObjectNode)selectedModel.getRoot());
		if (selectedSort != null)
			selectedSort.sortNodeOnColumn();
	}

	/**
	 * OutputOrderRenderer is a small extension of the standard JLabel based
	 * table cell renderer that allows it to interpret a few special values.
	 * -1 shows as Hidden, and 0 is shown as blank. Any other value is
	 * displayed as is.
	 */
	private final class OutputOrderRenderer extends DefaultTableCellRenderer
	{

		public OutputOrderRenderer(String[] choices)
		{
			super();
			setHorizontalAlignment(SwingConstants.CENTER);
		}

		public Component getTableCellRendererComponent(JTable jTable,
		  Object value,
		  boolean isSelected,
		  boolean hasFocus,
		  int row,
		  int column)
		{
			JLabel comp = (JLabel)super.getTableCellRendererComponent(jTable, value, isSelected, hasFocus, row, column);

			if (value instanceof Integer)
			{
				int i = ((Integer)value).intValue();
				if (i == -1)
					comp.setText("Hidden");
				else if (i == 0)
					comp.setText("");
				else
					comp.setText(String.valueOf(i));
			}
			return comp;
		}

	}

	/**
	 * OutputOrderEditor is a JCombobox based table cell editor. It allows the user
	 * to either enter their own output order index, or to select from hidden, first
	 * or last. If first or last are selected, then special values are returned to
	 * the setValueAt method, which are actioned by that method.
	 */
	private final class OutputOrderEditor extends JComboBox implements TableCellEditor
	{

		private final transient ArrayList d_listeners = new ArrayList();
		private transient int d_originalValue = 0;

		public OutputOrderEditor(String[] choices)
		{
			super(choices);

			setEditable(true);

			addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent ae)
				{
					stopCellEditing();
				}
			}
			);
		}

		public void addCellEditorListener(CellEditorListener cellEditorListener)
		{
			d_listeners.add(cellEditorListener);
		}

		public Component getTableCellEditorComponent(JTable jTable, Object value, boolean isSelected, int row, int column)
		{
			if (value == null) return this;
			d_originalValue = this.getSelectedIndex();
			if (value instanceof Integer)
			{
				int i = ((Integer)value).intValue();
				if (i == -1)
					setSelectedItem("Hidden");
				else
					setSelectedItem(String.valueOf(i));
			}
			else
			{
				setSelectedItem("Hidden");
			}
			jTable.setRowSelectionInterval(row, row);
			jTable.setColumnSelectionInterval(column, column);
			return this;
		}

		public void cancelCellEditing()
		{
			fireEditingCanceled();
		}

		public boolean isCellEditable(EventObject eventObject)
		{
			return true;
		}

		public void removeCellEditorListener(CellEditorListener cellEditorListener)
		{
			d_listeners.remove(cellEditorListener);
		}

		public Object getCellEditorValue()
		{
			switch (this.getSelectedIndex())
			{
				case 0: // First
					return new Integer(0);

				case 1: // Last
					return new Integer(1000);

				case 2: // Hidden
					return new Integer(-1);

				default: // A number
					return new Integer((String)getSelectedItem());
			}
		}

		public boolean stopCellEditing()
		{
			fireEditingStopped();
			return true;
		}

		public boolean shouldSelectCell(EventObject eventObject)
		{
			return true;
		}

		private void fireEditingCanceled()
		{
			setSelectedIndex(d_originalValue);
			ChangeEvent ce = new ChangeEvent(this);
			for (int i = d_listeners.size() - 1; i >= 0; --i)
			{
				((CellEditorListener)d_listeners.get(i)).editingCanceled(ce);
			}
		}

		private void fireEditingStopped()
		{
			ChangeEvent ce = new ChangeEvent(this);
			for (int i = d_listeners.size() - 1; i >= 0; --i)
			{
				((CellEditorListener)d_listeners.get(i)).editingStopped(ce);
			}
		}
	} //End OutputOrderEditor classes

	/** The basic idea of the TreeTableModel is that there is a single <code>root</code>
	 *  object.  This root object has a null <code>parent</code>.  All other objects
	 *  have a parent which points to a non-null object.  parent objects contain a list of
	 *  <code>children</code>, which are all the objects that point to it as their parent.
	 *  objects (or <code>nodes</code>) which have 0 children are leafs (the end of that
	 *  linked list).  nodes which have at least 1 child are not leafs. Leafs are like files
	 *  and non-leafs are like directories.
	 */
	class SkillModel extends AbstractTreeTableModel implements TreeTableModel
	{
		// Types of the columns.
		protected int modelType = MODEL_AVAIL;
		final String[] availNameList = {"Skill", "Cost", "Source"};
		final String[] selNameList = {"Skill", "Modifier", "Rank", "Total", "Cost", "Order"};

		/**
		 * Creates a SkillModel
		 */
		public SkillModel(int mode, boolean available)
		{
			super(null);
			if (!available)
				modelType = MODEL_SELECT;
			resetModel(mode, available, true);
		}

		/**
		 * The available table has three columns removed from the middle of the selected table
		 * This function will adjust references to "Untrained" through "Source" to point to the correct column constants
		 * NOTE: when referring to actual display column you still need to use the original column #
		 */
		private int adjustAvailColumnConst(int column)
		{
			if (modelType == MODEL_AVAIL)
				if (column > COL_NAME)
					return column + 3;
			if (modelType == MODEL_SELECT)
				if (column > COL_COST)
					return column + 1;
			return column;
		}

		/**
		 * This assumes the SkillModel exists but needs to be repopulated
		 */
		public void resetModel(int mode, boolean available, boolean newCall)
		{
//Globals.errorPrint("      --start-- resetModel: available:"+available);
			Iterator fI;
			if (available)
				fI = Globals.getSkillList().iterator();
			else
				fI = aPC.getSkillList().iterator();

			switch (mode)
			{
				case GuiConstants.INFOSKILLS_VIEW_STAT_TYPE_NAME: // KeyStat/SubType/Name
					setRoot((PObjectNode)InfoSkills.typeKeystatSubtypeRoot.clone());
					for (; fI.hasNext();)
					{
						final Skill aSkill = (Skill)fI.next();
						// in the availableTable, if filtering out unqualified items
						// ignore any skill the PC doesn't qualify for
						if (!shouldDisplayThis(aSkill))
							continue;
						PObjectNode rootAsPObjectNode = (PObjectNode)root;
						boolean added = false;
						for (int i = 0; i < rootAsPObjectNode.getChildCount(); i++)
						{
							if ((!added && i == rootAsPObjectNode.getChildCount() - 1) || aSkill.isType(rootAsPObjectNode.getChild(i).getItem().toString()))
							{
								// Items with only 1 type will not show up unless we do this
								PObjectNode[] d;
								if (aSkill.getMyTypeCount() == 1)
								{
									d = new PObjectNode[1];
									d[0] = rootAsPObjectNode.getChild(i);
								}
								else
								{
									d = rootAsPObjectNode.getChild(i).getChildren();
								}
								for (int k = 0; d != null && k < d.length; k++)
								{
									// Don't add children to items (those with only 1 type)
									if (!(d[k].getItem() instanceof PObject))
									{
										if (aSkill.isType(d[k].getItem().toString()))
										{
											PObjectNode aFN = new PObjectNode();
											//aFN.setItem(aSkill);
											if (available)
												skillA = new SkillWrapper(aSkill, new Integer(0), new Float(0), new Integer(0));
											else
												skillA = new SkillWrapper(aSkill, aSkill.modifier(), aSkill.getTotalRank(), new Integer(aSkill.getOutputIndex()));
											aFN.setItem(skillA);
											aFN.setIsValid(aSkill.passesPreReqTests());
											aFN.setParent(d[k]);
											d[k].addChild(aFN);
											added = true;
										}
									}
								}
							}
						}
					}
					break;
				case GuiConstants.INFOSKILLS_VIEW_STAT_NAME: // KeyStat/Name
					setRoot((PObjectNode)InfoSkills.typeKeystatRoot.clone());
					for (; fI.hasNext();)
					{
						final Skill aSkill = (Skill)fI.next();
						// in the availableTable, if filtering out unqualified items
						// ignore any skill the PC doesn't qualify for
						if (!shouldDisplayThis(aSkill))
							continue;
						PObjectNode rootAsPObjectNode = (PObjectNode)root;
						boolean added = false;
						for (int i = 0; i < rootAsPObjectNode.getChildCount(); i++)
						{
							if ((!added && i == rootAsPObjectNode.getChildCount() - 1) ||
							  aSkill.isType(rootAsPObjectNode.getChild(i).getItem().toString()))
							{
								PObjectNode aFN = new PObjectNode();
								if (available)
									skillA = new SkillWrapper(aSkill, new Integer(0), new Float(0), new Integer(0));
								else
									skillA = new SkillWrapper(aSkill, aSkill.modifier(), aSkill.getTotalRank(), new Integer(aSkill.getOutputIndex()));
								aFN.setItem(skillA);
								aFN.setIsValid(aSkill.passesPreReqTests());
								aFN.setParent(rootAsPObjectNode.getChild(i));
								rootAsPObjectNode.getChild(i).addChild(aFN);
								added = true;
							}
						}
					}
					break;
				case GuiConstants.INFOSKILLS_VIEW_TYPE_NAME: // SubType/Name
					setRoot((PObjectNode)InfoSkills.typeSubtypeRoot.clone());
					for (; fI.hasNext();)
					{
						final Skill aSkill = (Skill)fI.next();
						// in the availableTable, if filtering out unqualified items
						// ignore any skill the PC doesn't qualify for
						if (!shouldDisplayThis(aSkill))
							continue;
						PObjectNode rootAsPObjectNode = (PObjectNode)root;
						boolean added = false;
						for (int i = 0; i < rootAsPObjectNode.getChildCount(); i++)
						{
							if (aSkill.isType(rootAsPObjectNode.getChild(i).getItem().toString()))
							{
								PObjectNode aFN = new PObjectNode();
								if (available)
									skillA = new SkillWrapper(aSkill, new Integer(0), new Float(0), new Integer(0));
								else
									skillA = new SkillWrapper(aSkill, aSkill.modifier(), aSkill.getTotalRank(), new Integer(aSkill.getOutputIndex()));
								aFN.setItem(skillA);
								aFN.setIsValid(aSkill.passesPreReqTests());
								aFN.setParent(rootAsPObjectNode.getChild(i));
								rootAsPObjectNode.getChild(i).addChild(aFN);
								added = true;
							}
						}
						if (!added) //didn't fall into a SubType, so just pop it in under the root
						{
							PObjectNode aFN = new PObjectNode();
							if (available)
								skillA = new SkillWrapper(aSkill, new Integer(0), new Float(0), new Integer(0));
							else
								skillA = new SkillWrapper(aSkill, aSkill.modifier(), aSkill.getTotalRank(), new Integer(aSkill.getOutputIndex()));
							aFN.setItem(skillA);
							aFN.setIsValid(aSkill.passesPreReqTests());
							aFN.setParent(rootAsPObjectNode);
							rootAsPObjectNode.addChild(aFN);
						}
					}
					break;
				case GuiConstants.INFOSKILLS_VIEW_COST_NAME: // Cost/Name
					setRoot((PObjectNode)InfoSkills.typeCostRoot.clone());
					for (; fI.hasNext();)
					{
						final Skill aSkill = (Skill)fI.next();
						// in the availableTable, if filtering out unqualified items
						// ignore any skill the PC doesn't qualify for
						if (!shouldDisplayThis(aSkill))
							continue;
						PObjectNode rootAsPObjectNode = (PObjectNode)root;
						boolean added = false;
						final String skillCostType = aSkill.getSkillCostType(getSelectedPCClass());
						for (int i = 0; i < rootAsPObjectNode.getChildCount(); i++)
						{
							if (skillCostType.equals(rootAsPObjectNode.getChild(i).getItem().toString()))
							{
								PObjectNode aFN = new PObjectNode();
								if (available)
									skillA = new SkillWrapper(aSkill, new Integer(0), new Float(0), new Integer(0));
								else
									skillA = new SkillWrapper(aSkill, aSkill.modifier(), aSkill.getTotalRank(), new Integer(aSkill.getOutputIndex()));
								aFN.setItem(skillA);
								aFN.setIsValid(aSkill.passesPreReqTests());
								aFN.setParent(rootAsPObjectNode.getChild(i));
								rootAsPObjectNode.getChild(i).addChild(aFN);
								added = true;
							}
						}
						if (!added) //if it didn't fall into a Cost (something's probably wrong) just pop it in under the root
						{
							PObjectNode aFN = new PObjectNode();
							if (available)
								skillA = new SkillWrapper(aSkill, new Integer(0), new Float(0), new Integer(0));
							else
								skillA = new SkillWrapper(aSkill, aSkill.modifier(), aSkill.getTotalRank(), new Integer(aSkill.getOutputIndex()));
							aFN.setItem(skillA);
							aFN.setIsValid(aSkill.passesPreReqTests());
							aFN.setParent(rootAsPObjectNode);
							rootAsPObjectNode.addChild(aFN);
						}
					}
					break;
				case GuiConstants.INFOSKILLS_VIEW_NAME: // Name
					setRoot(new PObjectNode()); // just need a blank one
					for (; fI.hasNext();)
					{
						PObjectNode rootAsPObjectNode = (PObjectNode)root;
						final Skill aSkill = (Skill)fI.next();
						// in the availableTable, if filtering out unqualified items
						// ignore any skill the PC doesn't qualify for
						if (!shouldDisplayThis(aSkill))
							continue;
						PObjectNode aFN = new PObjectNode();
						if (available)
							skillA = new SkillWrapper(aSkill, new Integer(0), new Float(0), new Integer(0));
						else
							skillA = new SkillWrapper(aSkill, aSkill.modifier(), aSkill.getTotalRank(), new Integer(aSkill.getOutputIndex()));
						aFN.setItem(skillA);
						aFN.setIsValid(aSkill.passesPreReqTests());
						aFN.setParent(rootAsPObjectNode);
						rootAsPObjectNode.addChild(aFN);
					}
					break;
				default:
					Globals.errorPrint("In InfoSkills.SkillModel.adjustAvailColumnConst the mode " + mode + " is not handled.");
					break;
			}
			if (!newCall)
			{
				PObjectNode rootAsPObjectNode = (PObjectNode)root;
				if (rootAsPObjectNode.getChildCount() > 0)
					fireTreeNodesChanged(root, rootAsPObjectNode.getChildren(), null, null);
			}
//Globals.errorPrint("      --done-- resetModel: available:"+available);
		}

		/**
		 * return a boolean to indicate if the item should be included in the list.
		 * Only Weapon, Armor and Shield type items should be checked for proficiency.
		 */
		private boolean shouldDisplayThis(final Skill aSkill)
		{
			return (modelType == MODEL_SELECT || accept(aSkill));
		}

		// "There can be only one!" There must be a root object, though it can be hidden
		// to make it's existence basically a convenient way to keep track of the objects
		public void setRoot(PObjectNode aNode)
		{
			root = aNode;
		}

		public Object getRoot()
		{
			return (PObjectNode)root;
		}

		public void removeItemFromNodes(PObjectNode p, Object e)
		{
			if (p == null)
				p = (PObjectNode)root;

			PObjectNode[] c = p.getChildren();
			// if no children, remove it and update parent
			if (c.length == 0 && p.getItem().equals(e))
			{
				p.getParent().removeChild(p);
			}
			else
			{
				for (int i = 0; i < c.length; i++)
				{
					removeItemFromNodes(c[i], e);
				}
			}
		}

		/* The JTreeTableNode interface. */

		/**
		 * Returns int number of columns.
		 */
		public int getColumnCount()
		{
			if (modelType == MODEL_AVAIL)
				return availNameList.length;
			else
				return selNameList.length;
		}

		/**
		 * Returns String name of a column.
		 */
		public String getColumnName(int column)
		{
			if (modelType == MODEL_AVAIL)
				return availNameList[column];
			else
				return selNameList[column];
		}

		/**
		 * Returns Skill for the column.
		 */
		public Class getColumnClass(int column)
		{
			column = adjustAvailColumnConst(column);
			switch (column)
			{
				case COL_NAME: //skill name
					return TreeTableModel.class;
				case COL_MOD: //skill modifier
					return Integer.class;
				case COL_RANK: //skill ranks
					return Float.class;
				case COL_TOTAL: //total skill
					return Float.class;
				case COL_COST: //skill rank cost
					return Integer.class;
				case COL_INDEX: //display index
					return Integer.class;
				case COL_SRC:
					break;
				default:
					Globals.errorPrint("In InfoSkills.SkillModel.getColumnClass the column " + column + " is not handled.");
					break;
			}
			return String.class;
		}

		// true for first column so that it highlights
		public boolean isCellEditable(Object node, int column)
		{
			return (column == 0 || (modelType == MODEL_SELECT && column == 5));
		}

		/**
		 * Returns Object value of the column.
		 */
		public Object getValueAt(Object node, int column)
		{
			final PObjectNode fn = (PObjectNode)node;
			Skill aSkill = null;
			Integer mods = new Integer(0);
			Float ranks = new Float(0);
			Integer outputIndex = new Integer(0);

			if (fn == null)
			{
				Globals.errorPrint("Somehow we have no active node when doing getValueAt in InfoSkills.");
				return null;
			}

			if (fn.getItem() instanceof SkillWrapper)
			{
				SkillWrapper skillA = (SkillWrapper)fn.getItem();
				aSkill = skillA.getSkWrapSkill();
				mods = skillA.getSkWrapMod();
				ranks = skillA.getSkWrapRank();
				outputIndex = skillA.getSkWrapOutputIndex();
			}

			column = adjustAvailColumnConst(column);
			switch (column)
			{
				case COL_NAME: // Name
					if (fn != null)
					{
						return fn.toString();
					}
					else
					{
						Globals.errorPrint("Somehow we have no active node when doing getValueAt in InfoSkills.");
						return "";
					}
				case COL_MOD: // Bonus mods
					return mods;
				case COL_RANK: // number of ranks
					return ranks;
				case COL_TOTAL: // Total skill level
					return new Integer(mods.intValue() + ranks.intValue());
				case COL_COST: // Cost to buy skill points
					if (aSkill != null)
						return aSkill.costForPCClass(getSelectedPCClass());
					return new Integer(0);
				case COL_SRC: // Source Info
					if (aSkill != null)
						return aSkill.getSource();
					else if (fn != null)
						return fn.getSource();
					return "";
				case COL_INDEX: // Output index
					return outputIndex;
				case -1:
					if (fn != null)
					{
						return fn.getItem();
					}
					else
					{
						Globals.errorPrint("Somehow we have no active node when doing getValueAt in InfoSkills.");
						return null;
					}
				default:
					Globals.errorPrint("In InfoSkills.SkillModel.getValueAt the column " + column + " is not handled.");
					break;
			}
			return null;
		}

		/**
		 * Sets the new table cell value. Currently this only deals with the
		 * output index column. Here is deals with the possible special values
		 * that could be entered. This method takes 1000 as last, and sets the
		 * value to 1 more than the maximum index currently in use. It also
		 * takes 0 as first, setting the value to 1, and shuffling up all the
		 * other indexes to be after this new one.
		 *
		 */
		public void setValueAt(Object aValue, Object node, int column)
		{
			boolean needRefresh = false;

			if (modelType != MODEL_SELECT)
				return; // can only set values for selectedTableModel
			if (!(((PObjectNode)node).getItem() instanceof SkillWrapper))
				return; // can only use rows with Skills in them

			final PObjectNode fn = (PObjectNode)node;
			SkillWrapper skillA = (SkillWrapper)fn.getItem();
			Skill aSkill = skillA.getSkWrapSkill();

			if (aSkill != null)
			{
				switch (column)
				{
					case 5:
						int outputIndex = ((Integer)aValue).intValue();
						if (outputIndex == 1000) // Last
						{
							// Set it to one higher that the highest output index so far
							outputIndex = getHighestOutputIndex() + 1;
						}
						else if (outputIndex == 0) // First
						{
							// Set it to 1 and shuffle everyone up in order
							needRefresh = true;
							outputIndex = 2;
							for (Iterator i = aPC.getSkillListInOutputOrder().iterator(); i.hasNext();)
							{
								final Skill bSkill = (Skill)i.next();
								if (bSkill.getOutputIndex() > -1 && bSkill != aSkill)
								{
									bSkill.setOutputIndex(outputIndex++);
								}
							}
							outputIndex = 1;
						}
						else if (outputIndex != -1) // A specific value
						{
							int workingIndex = 1;
							// Reorder everything so that we have a proper sequence - its the only way to be sure
							needRefresh = true;
							for (Iterator i = aPC.getSkillListInOutputOrder().iterator(); i.hasNext();)
							{
								final Skill bSkill = (Skill)i.next();
								if (workingIndex == outputIndex)
									workingIndex++;
								if (bSkill.getOutputIndex() > -1 && bSkill != aSkill)
								{
									bSkill.setOutputIndex(workingIndex++);
								}
							}
						}

						aSkill.setOutputIndex(outputIndex);
						skillA = new SkillWrapper(aSkill, aSkill.modifier(), aSkill.getTotalRank(), new Integer(aSkill.getOutputIndex()));
						fn.setItem(skillA);

						if (needRefresh)
							refreshSelectedTable();
						break;
					default:
						Globals.errorPrint("In InfoSkills.SkillModel.setValueAt the column " + column + " is not handled.");
						break;

				}
			}

			return;
		}

		public void addChild(Object aChild, Object aParent)
		{
			PObjectNode aFN = new PObjectNode();
			aFN.setItem(aChild);
			aFN.setParent((PObjectNode)aParent);
			((PObjectNode)aParent).addChild(aFN);
		}
	}

	/**
	 * implementation of Filterable interface
	 */
	public void initializeFilters()
	{
//Globals.errorPrint("--start-- initializeFilters");
		registerFilter(createClassSkillFilter());
		registerFilter(createCrossClassSkillFilter());
		registerFilter(createExclusiveSkillFilter());
		registerFilter(createQualifyFilter());

		FilterFactory.registerAllSourceFilters(this);
		FilterFactory.registerAllSkillFilters(this);
//Globals.errorPrint("--done-- initializeFilters");
	}

	/**
	 * implementation of Filterable interface
	 */
	public void refreshFiltering()
	{
//Globals.errorPrint("--start-- refreshFiltering");
		needsUpdate = true;
		updateCharacterInfo();
//Globals.errorPrint("--done-- refreshFiltering");
	}

	/**
	 * specifies wheter the "match any" option should be available
	 */
	public boolean isMatchAnyEnabled()
	{
		return true;
	}

	/**
	 * specifies wheter the "negate/reverse" option should be available
	 */
	public boolean isNegateEnabled()
	{
		return true;
	}

	/**
	 * specifies the filter selection mode
	 */
	public int getSelectionMode()
	{
		return MULTI_MULTI_MODE;
	}

	/**
	 * Get the currently selected Character Class.
	 * @return   PCClass
	 * author    Brian Forester  (ysgarran@yahoo.com)
	 **/
	private PCClass getSelectedPCClass()
	{
		PCClass aClass = (PCClass)currCharacterClass.getSelectedItem();
		return aClass;
	}

	/*
	 * ##################################################################
	 * factory methods
	 * these are needed for reflection method calls in FilterFactory!
	 * ##################################################################
	 */

	public PObjectFilter createClassSkillFilter()
	{
		return new ClassSkillFilter();
	}

	public PObjectFilter createCrossClassSkillFilter()
	{
		return new CrossClassSkillFilter();
	}

	public PObjectFilter createExclusiveSkillFilter()
	{
		return new ExclusiveSkillFilter();
	}

	public PObjectFilter createQualifyFilter()
	{
		return new QualifyFilter();
	}

	/*
	 * define ClassSkillFilter and CrossClassSkillFilter locally,
	 * these two depend on the currently selected class,
	 * so they cannot be used outside of InfoSkills
	 *
	 * I don't really like it, but I can't think of a better/cleaner
	 * solution right now
	 */

	private class QualifyFilter extends AbstractPObjectFilter
	{
		public QualifyFilter()
		{
			super("Miscellaneous", "Qualify");
			if (SettingsHandler.isToolTipTextShown())
				setDescription("Accept skill, if not exclusive to chosen class.");
		}

		public boolean accept(PObject pObject)
		{
			if (pObject == null)
			{
				return false;
			}

			if (pObject instanceof Skill)
			{
				PCClass aPCClass = getSelectedPCClass();
				Skill aSkill = (Skill)pObject;
				return (aPCClass != null) &&
				  !(aSkill.isExclusive().startsWith("Y") &&
				  !aSkill.isClassSkill(aPCClass));
			}

			return true;
		}
	}

	private class ClassSkillFilter extends AbstractPObjectFilter
	{
		public ClassSkillFilter()
		{
			super("Skill", "Class");
			if (SettingsHandler.isToolTipTextShown())
				setDescription("Accept skill, if class skill.");
		}

		public String getName()
		{
			PCClass aPCClass = getSelectedPCClass();
			if (aPCClass != null)
			{
				return super.getName() + " (" + aPCClass.getName() + ")";
			}

			return super.getName();
		}

		public boolean accept(PObject pObject)
		{
			if (pObject == null)
			{
				return false;
			}

			if (pObject instanceof Skill)
			{
				PCClass aPCClass = getSelectedPCClass();
				return (aPCClass != null) && ((Skill)pObject).isClassSkill(aPCClass);
			}

			return true;
		}
	}

	private class CrossClassSkillFilter extends AbstractPObjectFilter
	{
		public CrossClassSkillFilter()
		{
			super("Skill", "Cross-Class");
			if (SettingsHandler.isToolTipTextShown())
				setDescription("Accept skill, if cross-class skill.");
		}

		public String getName()
		{
			PCClass aPCClass = getSelectedPCClass();
			if (aPCClass != null)
			{
				return super.getName() + " (" + aPCClass.getName() + ")";
			}

			return super.getName();
		}

		public boolean accept(PObject pObject)
		{
			if (pObject == null)
			{
				return false;
			}

			if (pObject instanceof Skill)
			{
				PCClass aPCClass = getSelectedPCClass();
				Skill aSkill = (Skill)pObject;
				return (aPCClass != null) &&
				  !aSkill.isClassSkill(aPCClass) &&
				  !aSkill.isExclusive().startsWith("Y");
			}

			return true;
		}
	}

	private class ExclusiveSkillFilter extends AbstractPObjectFilter
	{
		public ExclusiveSkillFilter()
		{
			super("Skill", "Exclusive");
			if (SettingsHandler.isToolTipTextShown())
				setDescription("Accept skill, if exclusive skill.");
		}

		public String getName()
		{
			PCClass aPCClass = getSelectedPCClass();
			if (aPCClass != null)
			{
				return super.getName() + " (" + aPCClass.getName() + ")";
			}

			return super.getName();
		}

		public boolean accept(PObject pObject)
		{
			if (pObject == null)
			{
				return false;
			}

			if (pObject instanceof Skill)
			{
				PCClass aPCClass = getSelectedPCClass();
				Skill aSkill = (Skill)pObject;
				return (aPCClass != null) &&
				  !aSkill.isClassSkill(aPCClass) &&
				  aSkill.isExclusive().startsWith("Y");
			}

			return true;
		}
	}
}
