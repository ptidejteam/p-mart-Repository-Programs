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
 *
 * Current Ver: $Revision: 1.1 $
 * Last Editor: $Author: vauchers $
 * Last Edited: $Date: 2006/02/21 01:16:03 $
 *
 */

package pcgen.gui.tabs;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.EventObject;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.SortedSet;
import java.util.TreeSet;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListSelectionModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
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
import pcgen.core.pclevelinfo.PCLevelInfo;
import pcgen.gui.GuiConstants;
import pcgen.gui.InfoSkillsSorter;
import pcgen.gui.InfoSkillsSorters;
import pcgen.gui.PCGen_Frame1;
import pcgen.gui.filter.AbstractPObjectFilter;
import pcgen.gui.filter.FilterAdapterPanel;
import pcgen.gui.filter.FilterFactory;
import pcgen.gui.filter.PObjectFilter;
import pcgen.gui.panes.FlippingSplitPane;
import pcgen.gui.utils.AbstractTreeTableModel;
import pcgen.gui.utils.GuiFacade;
import pcgen.gui.utils.IconUtilitities;
import pcgen.gui.utils.JComboBoxEx;
import pcgen.gui.utils.JLabelPane;
import pcgen.gui.utils.JTreeTable;
import pcgen.gui.utils.JTreeTableSorter;
import pcgen.gui.utils.LabelTreeCellRenderer;
import pcgen.gui.utils.PObjectNode;
import pcgen.gui.utils.ResizeColumnListener;
import pcgen.gui.utils.TreeTableModel;
import pcgen.gui.utils.Utility;
import pcgen.gui.utils.WholeNumberField;
import pcgen.util.Delta;
import pcgen.util.Logging;
import pcgen.util.ResetableListIterator;
import pcgen.util.StringIgnoreCaseComparator;

/**
 * @author  Bryan McRoberts (merton_monk@yahoo.com), Jason Buchanan (lonejedi70@hotmail.com), Jayme Cox (jaymecox@users.sourceforge.net)
 * @version $Revision: 1.1 $
 */

public class InfoSkills extends FilterAdapterPanel
{
	static final long serialVersionUID = -5369872214039221832L;
	static boolean resetSelectedModel = true;
	static PCClass previouslySelectedClass = null;

	/**
	 *  a wrapper for Skill, mods and ranks
	 **/
	public static final class SkillWrapper
	{
		private Skill _aSkill = null;
		private Integer _mod;
		private Float _ranks;
		private Integer _outputIndex;

		private SkillWrapper(Skill aSkill, Integer mod, Float ranks, Integer outputIndex)
		{
			_aSkill = aSkill;
			_mod = mod;
			_ranks = ranks;
			_outputIndex = outputIndex;
		}

		public String toString()
		{
			if (_aSkill == null)
			{
				return "";
			}
			return _aSkill.piString();
		}

		private Skill getSkWrapSkill()
		{
			return _aSkill;
		}

		private Integer getSkWrapMod()
		{
			return _mod;
		}

		private Float getSkWrapRank()
		{
			return _ranks;
		}

		private Integer getSkWrapOutputIndex()
		{
			return _outputIndex;
		}
	}

	private final JLabel avaLabel = new JLabel("Display By");
	private final JLabel selLabel = new JLabel("Display By");
	private JButton leftButton;
	private JButton addButton;
	private JScrollPane cScroll = new JScrollPane();
	private JLabelPane infoLabel = new JLabelPane();
	private JPanel center = new JPanel();
	private Border etched;
	private FlippingSplitPane splitPane;
	private FlippingSplitPane bsplit;
	private FlippingSplitPane asplit;
	private JCheckBox autoSort = new JCheckBox("Auto-sort output", true);

	private SkillModel availableModel = null;
	private SkillModel selectedModel = null;
	private JTreeTable availableTable;
	private JTreeTable selectedTable;

	private JTreeTableSorter availableSort = null;
	private JTreeTableSorter selectedSort = null;

	//keep track of which skill was selected last from either table
	private Skill lastSkill = null;

	private static boolean needsUpdate = true;
	private static PlayerCharacter aPC = null;

	private JComboBoxEx viewComboBox = new JComboBoxEx();
	private JComboBoxEx viewSelectComboBox = new JComboBoxEx();
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
	private TreePath selPath;
	private boolean hasBeenSized = false;

	private JPanel jPanel1 = new JPanel();

	private JLabel jLbMaxSkill = new JLabel();
	private JLabel jLbMaxCrossSkill = new JLabel();
	private JLabel jLbClassSkillPoints = new JLabel();
	private JComboBoxEx currCharacterClass = new JComboBoxEx(); // now contains Strings of Class/lvl
	private JLabel jLbTotalSkillPointsLeft = new JLabel();
	private JLabel maxSkillRank = new JLabel();
	private JLabel maxCrossSkillRank = new JLabel();
	private WholeNumberField totalSkillPointsLeft = new WholeNumberField(0, 4);
	private WholeNumberField currCharClassSkillPnts = new WholeNumberField(0, 4);
	private JLabel includeLabel = new JLabel();
	private JComboBoxEx skillChoice = new JComboBoxEx();
	private JLabel exclusiveLabel = new JLabel();
	private JTextField exclusiveSkillCost = new JTextField();

	public static final int nCosts = 3;

	public SkillWrapper createSkillWrapper(boolean available, Skill skill)
	{
		return available ? new SkillWrapper(skill, new Integer(0), new Float(0), new Integer(0)) : new SkillWrapper(skill, skill.modifier(), skill.getTotalRank(), new Integer(skill.getOutputIndex()));
	}

	private class SkillPopupMenu extends JPopupMenu
	{
		static final long serialVersionUID = -5369872214039221832L;

		private class SkillActionListener implements ActionListener
		{
			int qty = 0;

			private SkillActionListener(int aQty)
			{
				qty = aQty;
			}

			public void actionPerformed(ActionEvent evt)
			{
			}
		}

		private class AddSkillActionListener extends SkillActionListener
		{
			private AddSkillActionListener(int qty)
			{
				super(qty);
			}

			public void actionPerformed(ActionEvent evt)
			{
				int newQty = qty;
				// Get a number from the user via a popup
				if (qty < 0)
				{
					String selectedValue = GuiFacade.showInputDialog(null, "Enter Quantity to Add", Constants.s_APPNAME, GuiFacade.QUESTION_MESSAGE);
					if (selectedValue != null)
					{
						try
						{
							//abs just in case someone types in a negative value
							newQty = Math.abs(new Integer(selectedValue.trim()).intValue());
						}
						catch (Exception e)
						{
							GuiFacade.showMessageDialog(null, "Invalid number!", Constants.s_APPNAME, GuiFacade.ERROR_MESSAGE);
							return;
						}
					}
					else
					{
						return;
					}
				}
				addSkill(newQty);
			}
		}

		private class MaxSkillActionListener extends SkillActionListener
		{
			//qty should remain unused by this derived class
			private MaxSkillActionListener(int qty)
			{
				super(qty);
			}

			public void actionPerformed(ActionEvent evt)
			{
				Skill aSkill = getSelectedSkill();
				if (aSkill == null)
				{
					return;
				}
				final PlayerCharacter currentPC = Globals.getCurrentPC();
				currentPC.setDirty(true);
				//if the PC already has this skill, then link to it so that we get accurate existing rank info
				Skill bSkill = currentPC.getSkillNamed(aSkill.getName());
				if (bSkill != null)
				{
					aSkill = bSkill;
				}
				PCClass aClass = getSelectedPCClass();
				PCLevelInfo pcl = getSelectedLevelInfo();
				double maxRank = 0.0;
				double skillPool = 0.0;
				if (aClass != null)
				{
					maxRank = currentPC.getMaxRank(aSkill.getName(), aClass).doubleValue();
					skillPool = pcl.getSkillPointsRemaining();
				}
				if (maxRank > aSkill.getRank().doubleValue() || SettingsHandler.isBoolBypassMaxSkillRank())
				{
					final int cost = aSkill.costForPCClass(getSelectedPCClass()).intValue();
					final double pointsNeeded = Math.floor((maxRank - aSkill.getTotalRank().doubleValue()) * cost);
					final Double points = new Double(Math.min(pointsNeeded, skillPool));
					addSkill(points.intValue());
				}
				else
				{
					GuiFacade.showMessageDialog(null, "Cannot raise this skill further.", "PCGen", GuiFacade.INFORMATION_MESSAGE);
				}
			}
		}

		private class RemoveSkillActionListener extends SkillActionListener
		{
			private RemoveSkillActionListener(int qty)
			{
				super(qty);
			}

			public void actionPerformed(ActionEvent evt)
			{
				int newQty = qty;
				// Get a number from the user via a popup
				if (qty < 0)
				{
					String selectedValue = GuiFacade.showInputDialog(null, "Enter Quantity to Remove", Constants.s_APPNAME, GuiFacade.QUESTION_MESSAGE);
					if (selectedValue != null)
					{
						try
						{
							//abs just in case someone types in a negative value
							newQty = Math.abs(new Integer(selectedValue.trim()).intValue());
						}
						catch (Exception e)
						{
							GuiFacade.showMessageDialog(null, "Invalid number!", Constants.s_APPNAME, GuiFacade.ERROR_MESSAGE);
							return;
						}
					}
					else
					{
						return;
					}
				}
				addSkill(-newQty);
			}
		}

		private class ResetSkillActionListener extends SkillActionListener
		{
			//qty should remain unused by this derived class
			private ResetSkillActionListener(int qty)
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
						PCClass aClass = (PCClass) iter.next();
						aSkill.setZeroRanks(aClass);
					}
					//
					// Remove the skill from the skill list if we've just set the rank to zero
					// and it is not an untrained skill
					//
					if ((pcgen.core.utils.Utility.doublesEqual(aSkill.getRank().doubleValue(), 0.0)) && !(aSkill.getUntrained().charAt(0) == 'Y'))
					{
						aPC.getSkillList().remove(aSkill);
					}
					// don't need to update availableTable
					updateSelectedModel();
					// available skill points need update
					currCharacterClassActionPerformed();
				}
			}
		}

		private JMenuItem createAddMenuItem(String label, int qty, String accelerator)
		{
			return Utility.createMenuItem(label, new AddSkillActionListener(qty), "add " + qty, (char) 0, accelerator, "Add " + (qty < 0 ? "n" : Integer.toString(qty)) + " skill point" + (qty == 1 ? "" : "s"), "Add16.gif", true);
		}

		private JMenuItem createMaxMenuItem(String label, String accelerator)
		{
			return Utility.createMenuItem(label, new MaxSkillActionListener(0), "max ranks", (char) 0, accelerator, "Set to max ranks", "Add16.gif", true);
		}

		private JMenuItem createRemoveMenuItem(String label, int qty, String accelerator)
		{
			return Utility.createMenuItem(label, new RemoveSkillActionListener(qty), "remove " + qty, (char) 0, accelerator, "Remove " + (qty < 0 ? "n" : Integer.toString(qty)) + " skill point" + (qty == 1 ? "" : "s"), "Remove16.gif", true);
		}

		private JMenuItem createResetMenuItem(String label, String accelerator)
		{
			return Utility.createMenuItem(label, new ResetSkillActionListener(0), "reset ranks", (char) 0, accelerator, "Reset to zero ranks", "Add16.gif", true);
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

				this.addSeparator();

				JMenu resortMenu = Utility.createMenu("Output Order", (char) 0, "Output Order", null, true);

				SkillPopupMenu.this.add(resortMenu);

				resortMenu.add(Utility.createMenuItem("By name (ascending)",
					new ResortActionListener(ResortComparator.RESORT_NAME, ResortComparator.RESORT_ASCENDING),
					"sortOutput", (char) 0, null, "Sort skill list by name in ascending alphabetical order", null, true));
				resortMenu.add(Utility.createMenuItem("By name (descending)",
					new ResortActionListener(ResortComparator.RESORT_NAME, ResortComparator.RESORT_DESCENDING),
					"sortOutput", (char) 0, null, "Sort skill list by name in descending alphabetical order", null, true));
				resortMenu.add(Utility.createMenuItem("By trained then untrained",
					new ResortActionListener(ResortComparator.RESORT_TRAINED, ResortComparator.RESORT_ASCENDING),
					"sortOutput", (char) 0, null, "Sort skill list by trained then untrained.", null, true));
				resortMenu.add(Utility.createMenuItem("By untrained then untrained",
					new ResortActionListener(ResortComparator.RESORT_TRAINED, ResortComparator.RESORT_DESCENDING),
					"sortOutput", (char) 0, null, "Sort skill list by untrained then trained.", null, true));
			}
		}
	}

	private class ResortActionListener implements ActionListener
	{
		int sort;
		boolean sortOrder;

		public ResortActionListener(int i, boolean aBool)
		{
			sort = i;
			sortOrder = aBool;
		}

		public void actionPerformed(ActionEvent e)
		{
			resortSelected(sort, sortOrder);
		}
	}

	private void resortSelected(int sort, boolean sortOrder)
	{
		ResortComparator comparator = new ResortComparator(sort, sortOrder);
		int nextOutputIndex = 1;
		List skillList = aPC.getSkillList();
		Collections.sort(skillList, comparator);
		for (Iterator sI = skillList.iterator(); sI.hasNext();)
		{
			final Skill aSkill = (Skill) sI.next();
			if (aSkill.getOutputIndex() >= 0)
			{
				aSkill.setOutputIndex(nextOutputIndex++);
			}
		}
		updateSelectedModel();
		aPC.setDirty(true);
	}

	private static class ResortComparator implements Comparator
	{
		public static final int RESORT_NAME = 0;
		public static final int RESORT_TRAINED = 1;

		public static final boolean RESORT_ASCENDING = true;
		public static final boolean RESORT_DESCENDING = false;

		private int sort = RESORT_NAME;
		private boolean sortOrder = RESORT_ASCENDING;

		public ResortComparator(int sort, boolean sortOrder)
		{
			this.sort = sort;
			this.sortOrder = sortOrder;
		}

		// Comparator will be specific to Equipment objects
		public int compare(Object obj1, Object obj2)
		{
			Skill s1;
			Skill s2;
			if (sortOrder == RESORT_ASCENDING || sort == RESORT_TRAINED)
			{
				s1 = (Skill) obj1;
				s2 = (Skill) obj2;
			}
			else
			{
				s1 = (Skill) obj2;
				s2 = (Skill) obj1;
			}

			switch (sort)
			{
				case RESORT_TRAINED:
					if (s1.getRank().floatValue() > 0.0f
						&& s2.getRank().floatValue() <= 0.0f)
					{
						return (sortOrder == RESORT_ASCENDING ? -1 : 1);
					}
					else if (
						s1.getRank().floatValue() <= 0.0f
						&& s2.getRank().floatValue() > 0.0f)
					{
						return (sortOrder == RESORT_ASCENDING ? 1 : -1);
					}
					else
					{
						return s1.getName().compareToIgnoreCase(s2.getName());
					}
				case RESORT_NAME:
				default :
					return s1.getName().compareToIgnoreCase(s2.getName());
			}

		}
	}

	private class SkillPopupListener extends MouseAdapter
	{
		private JTree tree;
		private SkillPopupMenu menu;

		private SkillPopupListener(JTreeTable treeTable, SkillPopupMenu aMenu)
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
							final JMenuItem menuItem = (JMenuItem) menu.getComponent(i);
							KeyStroke ks = menuItem.getAccelerator();
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
				selPath = tree.getClosestPathForLocation(evt.getX(), evt.getY());
				if (selPath == null)
				{
					return;
				}
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
		setName(Constants.tabNames[Constants.TAB_SKILLS]);

		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				initComponents();
				initActionListeners();
			}
		});

	}

	public static final void setNeedsUpdate(boolean flag)
	{
		needsUpdate = flag;
	}

	/**
	 * This method is called from within the constructor to
	 * initialize the form.
	 **/
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

		viewComboBox.addItem("KeyStat/SubType/Name");
		viewComboBox.addItem("KeyStat/Name");
		viewComboBox.addItem("SubType/Name");
		viewComboBox.addItem("Cost/SubType/Name");
		viewComboBox.addItem("Cost/Name");
		viewComboBox.addItem("Name");
		Utility.setDescription(viewComboBox, "You can change how the Skills in the Tables are listed.");
		viewComboBox.setSelectedIndex(viewMode);       // must be done before createModels call

		viewSelectComboBox.addItem("KeyStat/SubType/Name");
		viewSelectComboBox.addItem("KeyStat/Name");
		viewSelectComboBox.addItem("SubType/Name");
		viewSelectComboBox.addItem("Cost/SubType/Name");
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

		Utility.buildConstraints(c, 0, 0, 1, 1, 100, 5);
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.CENTER;
		JPanel aPanel = new JPanel();
		gridbag.setConstraints(aPanel, c);
		aPanel.add(avaLabel);
		aPanel.add(viewComboBox);
		ImageIcon newImage;
		newImage = IconUtilitities.getImageIcon("Forward16.gif");
		addButton = new JButton(newImage);
		Utility.setDescription(addButton, "Click to add 1 skill point to the selected Available skill");
		addButton.setEnabled(false);
		aPanel.add(addButton);
		leftPane.add(aPanel);

		// set the alignment on these columns to center
		// might as well set the prefered width while we're at it
		availableTable.setColAlign(COL_MOD, SwingConstants.CENTER);
		availableTable.getColumnModel().getColumn(COL_MOD).setPreferredWidth(15);
		selectedTable.getColumnModel().getColumn(COL_NAME).setPreferredWidth(60);
		selectedTable.setColAlign(COL_MOD, SwingConstants.CENTER);
		selectedTable.getColumnModel().getColumn(COL_MOD).setPreferredWidth(15);
		selectedTable.setColAlign(COL_RANK, SwingConstants.CENTER);
		selectedTable.getColumnModel().getColumn(COL_RANK).setPreferredWidth(15);
		selectedTable.setColAlign(COL_TOTAL, SwingConstants.CENTER);
		selectedTable.getColumnModel().getColumn(COL_TOTAL).setPreferredWidth(15);
		selectedTable.setColAlign(COL_COST, SwingConstants.CENTER);
		selectedTable.getColumnModel().getColumn(COL_COST).setPreferredWidth(15);
		selectedTable.getColumnModel().getColumn(COL_SRC).setCellRenderer(new OutputOrderRenderer());
		selectedTable.getColumnModel().getColumn(COL_SRC).setPreferredWidth(15);

		Utility.buildConstraints(c, 0, 1, 1, 1, 0, 95);
		c.fill = GridBagConstraints.BOTH;
		c.anchor = GridBagConstraints.CENTER;
		JScrollPane scrollPane = new JScrollPane(availableTable);
		gridbag.setConstraints(scrollPane, c);
		leftPane.add(scrollPane);

		gridbag = new GridBagLayout();
		c = new GridBagConstraints();
		rightPane.setLayout(gridbag);

		Utility.buildConstraints(c, 0, 0, 1, 1, 100, 5);
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.CENTER;
		aPanel = new JPanel();
		gridbag.setConstraints(aPanel, c);
		aPanel.add(selLabel);
		aPanel.add(viewSelectComboBox);
		newImage = IconUtilitities.getImageIcon("Back16.gif");
		leftButton = new JButton(newImage);
		Utility.setDescription(leftButton, "Click to remove 1 skill point from the Selected skill");
		leftButton.setEnabled(false);
		aPanel.add(leftButton);
		aPanel.add(autoSort);
		rightPane.add(aPanel);

		Utility.buildConstraints(c, 0, 1, 1, 1, 0, 95);
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
		Utility.buildConstraints(gridBagConstraints2, 0, 0, 1, 1, 5, 5);
		gridBagConstraints2.anchor = GridBagConstraints.EAST;
		jPanel1.add(jLbMaxSkill, gridBagConstraints2);

		maxSkillRank.setText(String.valueOf(PlayerCharacter.maxClassSkillForLevel(aPC.getTotalLevels() + aPC.totalHitDice())));
		maxSkillRank.setForeground(Color.black);
		Utility.buildConstraints(gridBagConstraints2, 1, 0, 1, 1, 5, 5);
		gridBagConstraints2.anchor = GridBagConstraints.WEST;
		jPanel1.add(maxSkillRank, gridBagConstraints2);

		jLbMaxCrossSkill.setText("Max Cross-Class Skill Rank:  ");
		Utility.setDescription(jLbMaxCrossSkill, "This is the maximum cross-class skill rank the character can have.");
		jLbMaxCrossSkill.setForeground(Color.black);
		Utility.buildConstraints(gridBagConstraints2, 0, 1, 1, 1, 5, 5);
		gridBagConstraints2.anchor = GridBagConstraints.EAST;
		jPanel1.add(jLbMaxCrossSkill, gridBagConstraints2);

		maxCrossSkillRank.setText(PlayerCharacter.maxCrossClassSkillForLevel(aPC.getTotalLevels() + aPC.totalHitDice()).toString());
		maxCrossSkillRank.setForeground(Color.black);
		Utility.buildConstraints(gridBagConstraints2, 1, 1, 1, 1, 5, 5);
		gridBagConstraints2.anchor = GridBagConstraints.WEST;
		jPanel1.add(maxCrossSkillRank, gridBagConstraints2);

		jLbClassSkillPoints.setText("Skill Points Left for Class:  ");
		jLbClassSkillPoints.setForeground(Color.black);
		Utility.buildConstraints(gridBagConstraints2, 0, 3, 2, 1, 5, 5);
		gridBagConstraints2.anchor = GridBagConstraints.EAST;
		jPanel1.add(jLbClassSkillPoints, gridBagConstraints2);

		updateClassSelection();
		currCharacterClass.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				currCharacterClassActionPerformed();
			}
		});
		Utility.buildConstraints(gridBagConstraints2, 2, 3, 1, 1, 5, 5);
		gridBagConstraints2.anchor = GridBagConstraints.CENTER;
		final int oldFill = gridBagConstraints2.fill;
		gridBagConstraints2.fill = GridBagConstraints.HORIZONTAL;
		jPanel1.add(currCharacterClass, gridBagConstraints2);
		gridBagConstraints2.fill = oldFill;

		jLbTotalSkillPointsLeft.setText("Total Skill Points Left:  ");
		jLbTotalSkillPointsLeft.setForeground(Color.black);
		Utility.buildConstraints(gridBagConstraints2, 2, 2, 1, 1, 5, 5);
		gridBagConstraints2.anchor = GridBagConstraints.EAST;
		jPanel1.add(jLbTotalSkillPointsLeft, gridBagConstraints2);

		totalSkillPointsLeft.addFocusListener(new FocusAdapter()
		{
			public void focusLost(FocusEvent evt)
			{
				totalSkillPointsLeftFocusLost();
			}
		});
		Utility.buildConstraints(gridBagConstraints2, 3, 2, 1, 1, 5, 5);
		gridBagConstraints2.anchor = GridBagConstraints.WEST;
		totalSkillPointsLeft.setMinimumSize(new Dimension(40, 17));
		totalSkillPointsLeft.setPreferredSize(new Dimension(40, 17));
		jPanel1.add(totalSkillPointsLeft, gridBagConstraints2);

		currCharClassSkillPnts.addFocusListener(new FocusAdapter()
		{
			public void focusLost(FocusEvent evt)
			{
				currCharClassSkillPntsFocusLost();
			}
		});
		Utility.buildConstraints(gridBagConstraints2, 3, 3, 1, 1, 5, 5);
		gridBagConstraints2.anchor = GridBagConstraints.WEST;
		currCharClassSkillPnts.setPreferredSize(new Dimension(40, 20));
		currCharClassSkillPnts.setMinimumSize(new Dimension(40, 20));
		jPanel1.add(currCharClassSkillPnts, gridBagConstraints2);

		includeLabel = new JLabel("Include Skills: ");
		Utility.buildConstraints(gridBagConstraints2, 2, 0, 1, 1, 5, 5);
		gridBagConstraints2.anchor = GridBagConstraints.EAST;
		jPanel1.add(includeLabel, gridBagConstraints2);

		skillChoice.setModel(new DefaultComboBoxModel(new String[]{"None", "Untrained", "All"}));
		skillChoice.setMaximumRowCount(3);
		Utility.setDescription(skillChoice, "How many skills on character sheets?");
		skillChoice.setMinimumSize(new Dimension(98, 22));
		skillChoice.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				skillChoiceActionPerformed();
			}
		});
		boolean oldFlag = resetSelectedModel;
		resetSelectedModel = true;
		skillChoice.setSelectedIndex(SettingsHandler.getIncludeSkills());
		resetSelectedModel = oldFlag;
		Utility.buildConstraints(gridBagConstraints2, 3, 0, 1, 1, 5, 5);
		gridBagConstraints2.anchor = GridBagConstraints.WEST;
		jPanel1.add(skillChoice, gridBagConstraints2);

		exclusiveLabel = new JLabel("Exclusive Skill Cost: ");
		Utility.setDescription(exclusiveLabel, "What it costs to buy a skill that is normally unavailable to one's class (0 means that it can't be bought)");
		Utility.buildConstraints(gridBagConstraints2, 2, 1, 1, 1, 5, 5);
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
				excCostFocusEvent();
			}
		});
		Utility.buildConstraints(gridBagConstraints2, 3, 1, 1, 1, 5, 5);
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

		availableSort = new JTreeTableSorter(availableTable, (PObjectNode) availableModel.getRoot(), availableModel);
		selectedSort = new JTreeTableSorter(selectedTable, (PObjectNode) selectedModel.getRoot(), selectedModel);

	}

	/**
	 * Here we want to select the first class to have remaining skill
	 * points or if none then the last class added
	 * Note: Currently if you add a new level of an older class, there is no
	 * way to tell if the skill points come from the new level or the old level
	 * eg: lvl 1 class a, lvl2 class b, lvl 3 class a
	 * If all points for level 1 have been spent, but points for levels 2
	 * and 3 remain to be spent, class a will erroneously be selected
	 * This can be corrected once skill points are tracked by PCLevelInfo
	 */
	private void setCurrentClassCombo()
	{
		boolean oldFlag = resetSelectedModel;
		PCClass aClass = null;
		// Search for a class with remaining points.
		// Search is done in the order levels are assigned
		// to hopefully get the earliest class with remaining points
		int idx = 0;
		for (; idx < aPC.getLevelInfoSize() - 1; ++idx)
		{
			PCLevelInfo pcl = (PCLevelInfo) aPC.getLevelInfo().get(idx);
			if (pcl.getSkillPointsRemaining() > 0)
			{
				aClass = aPC.getClassNamed(pcl.getClassKeyName());
				break;
			}
		}
		if (idx < aPC.getLevelInfoSize())
		{
			resetSelectedModel = !(previouslySelectedClass == aClass);
			currCharacterClass.setSelectedIndex(idx);
			previouslySelectedClass = aClass;
			resetSelectedModel = oldFlag;
		}
	}

	private void initActionListeners()
	{
		addComponentListener(new ComponentAdapter()
		{
			public void componentShown(ComponentEvent evt)
			{
				formComponentShown();
			}
		});
		addComponentListener(new ComponentAdapter()
		{
			public void componentResized(ComponentEvent e)
			{
				saveDividerLocations();
			}
		});
		asplit.addComponentListener(new ComponentAdapter()
		{
			public void componentResized(ComponentEvent e)
			{
				saveDividerLocations();
			}
		});
		bsplit.addComponentListener(new ComponentAdapter()
		{
			public void componentResized(ComponentEvent e)
			{
				saveDividerLocations();
			}
		});
		splitPane.addComponentListener(new ComponentAdapter()
		{
			public void componentResized(ComponentEvent e)
			{
				saveDividerLocations();
			}
		});
		leftButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				addSkill(-1);
			}
		});
		addButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				addSkill(1);
			}
		});
		viewComboBox.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				viewComboBoxActionPerformed();
			}
		});
		viewSelectComboBox.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				viewSelectComboBoxActionPerformed();
			}
		});
		autoSort.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				if (aPC != null)
				{
					aPC.setDirty(true);
					aPC.setAutoSortSkills(autoSort.isSelected());
				}
			}
		});

		FilterFactory.restoreFilterSettings(this);
	}

	private void saveDividerLocations()
	{
		int s = splitPane.getDividerLocation();
		if (s > 0)
		{
			SettingsHandler.setPCGenOption("InfoSkills.splitPane", s);
		}
		s = asplit.getDividerLocation();
		if (s > 0)
		{
			SettingsHandler.setPCGenOption("InfoSkills.asplit", s);
		}
		s = bsplit.getDividerLocation();
		if (s > 0)
		{
			SettingsHandler.setPCGenOption("InfoSkills.bsplit", s);
		}
	}

	private void currCharacterClassActionPerformed()
	{
		PCLevelInfo pcl = this.getSelectedLevelInfo();
		boolean oldFlag = resetSelectedModel;
		PCClass aClass = null;
		if (pcl != null)
		{
			currCharClassSkillPnts.setValue(pcl.getSkillPointsRemaining());
			totalSkillPointsLeft.setValue(Globals.getCurrentPC().getSkillPoints());
			aClass = aPC.getClassNamed(pcl.getClassKeyName());
			resetSelectedModel = !(aClass == previouslySelectedClass);
		}
		else
		{
			currCharClassSkillPnts.setValue(0);
			totalSkillPointsLeft.setValue(0);
		}

		//we need to do this in order for class/xclass views to re-sort

		updateSelectedModel();
		updateAvailableModel();
		previouslySelectedClass = aClass;
		resetSelectedModel = oldFlag;
	}

	private void totalSkillPointsLeftFocusLost()
	{
		final PlayerCharacter currentPC = Globals.getCurrentPC();
		currentPC.setDirty(true);
		if (totalSkillPointsLeft.getText().length() > 0)
		{
			final int anInt = Delta.decode(totalSkillPointsLeft.getText()).intValue();
			if (anInt == currentPC.getSkillPoints())
			{
				return;
			}
			currentPC.setSkillPoints(anInt);
			final int x = currentPC.getClassList().size();
			final int y = anInt / x;
			PCClass aClass;
			for (Iterator i = currentPC.getClassList().iterator(); i.hasNext();)
			{
				aClass = (PCClass) i.next();
				aClass.setSkillPool(Math.max(0, y));
			}
			PCLevelInfo pcl = getSelectedLevelInfo();
			int skillPool = pcl.getSkillPointsRemaining();
			aClass = getSelectedPCClass();
			if (aClass != null)
			{
				currCharClassSkillPnts.setValue(skillPool);
			}
		}
		totalSkillPointsLeft.setValue(currentPC.getSkillPoints());
	}

	private void currCharClassSkillPntsFocusLost()
	{
		final PlayerCharacter currentPC = Globals.getCurrentPC();
		currentPC.setDirty(true);
		PCClass aClass = this.getSelectedPCClass();
		PCLevelInfo pcl = getSelectedLevelInfo();
		int skillPool = pcl.getSkillPointsRemaining();
		if (currCharClassSkillPnts.getText().length() > 0)
		{
			final int anInt = Delta.decode(currCharClassSkillPnts.getText()).intValue();
			if (aClass == null || anInt == skillPool)
			{
				return;
			}
			final int i = skillPool - anInt;
			pcl.setSkillPointsRemaining(anInt);
			aClass.setSkillPool(Math.max(0, aClass.getSkillPool().intValue() - i));
			currentPC.setSkillPoints(Math.max(0, currentPC.getSkillPoints() - i));
		}
		currCharClassSkillPnts.setValue(pcl.getSkillPointsRemaining());
		totalSkillPointsLeft.setValue(currentPC.getSkillPoints());
	}

	private void skillChoiceActionPerformed()
	{
		final int selection = skillChoice.getSelectedIndex();
		final int oldSelection = SettingsHandler.getIncludeSkills();
		if (selection >= 0 && selection <= 2 && (selection != oldSelection || resetSelectedModel))
		{
			SettingsHandler.setIncludeSkills(selection);
			aPC.populateSkills(SettingsHandler.getIncludeSkills());

			// resort the skills
			if (autoSort.isSelected())
			{
				int nI = 1;
				Iterator sI = aPC.getSkillList().iterator();
				while (sI.hasNext())
				{
					final Skill aSkill = (Skill) sI.next();
					if (aSkill.getOutputIndex() >= 0)
					{
						aSkill.setOutputIndex(nI++);
					}
				}
			}

			// update the TreeTable
			updateSelectedModel();
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

	private void excCostFocusEvent()
	{
		updateSkillCost();
	}

	private void viewComboBoxActionPerformed()
	{
		final int index = viewComboBox.getSelectedIndex();
		if (index != viewMode)
		{
			viewMode = index;
			SettingsHandler.setSkillsTab_AvailableListMode(viewMode);
			updateAvailableModel();
		}
	}

	private void viewSelectComboBoxActionPerformed()
	{
		final int index = viewSelectComboBox.getSelectedIndex();
		if (index != viewSelectMode)
		{
			viewSelectMode = index;
			SettingsHandler.setSkillsTab_SelectedListMode(viewSelectMode);
			updateSelectedModel();
		}
	}

	// This is called when the tab is shown.
	private void formComponentShown()
	{
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
			s = SettingsHandler.getPCGenOption("InfoSkills.splitPane", (int) (this.getSize().getWidth() * 4 / 10));
			t = SettingsHandler.getPCGenOption("InfoSkills.bsplit", (int) (this.getSize().getHeight() - 101));
			u = SettingsHandler.getPCGenOption("InfoSkills.asplit", (int) (this.getSize().getWidth() - 408));

			// set the prefered width on selectedTable
			final TableColumnModel selectedTableColumnModel = selectedTable.getColumnModel();
			for (int i = 0; i < selectedTable.getColumnCount(); ++i)
			{
				TableColumn sCol = selectedTableColumnModel.getColumn(i);
				width = Globals.getCustColumnWidth("InfoSel", i);
				if (width != 0)
				{
					sCol.setPreferredWidth(width);
				}
				sCol.addPropertyChangeListener(new ResizeColumnListener(selectedTable, "InfoSel", i));
				if (i == 5)
				{
					sCol.setCellEditor(new OutputOrderEditor(new String[]{"First", "Last", "Hidden"}));
				}
			}
			// set the prefered width on availableTable
			for (int i = 0; i < availableTable.getColumnCount(); ++i)
			{
				final TableColumnModel availableTableColumnModel = availableTable.getColumnModel();
				TableColumn sCol = availableTableColumnModel.getColumn(i);
				width = Globals.getCustColumnWidth("InfoAva", i);
				if (width != 0)
				{
					sCol.setPreferredWidth(width);
				}
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
	}

	private final void updateClassSelection()
	{
		resetSelectedModel = false;
		currCharacterClass.removeAllItems();
		for (Iterator i = aPC.getLevelInfo().iterator(); i.hasNext();)
		{
			PCLevelInfo pcl = (PCLevelInfo) i.next();
			currCharacterClass.addItem(pcl.getClassKeyName() + "/" + pcl.getLevel() + " [" + pcl.getSkillPointsRemaining() + "/" + pcl.getSkillPointsGained() + "]");
		}
		resetSelectedModel = true;
		if (currCharacterClass.getItemCount() > 0)
		{
			setCurrentClassCombo();
		}
	}

	//
	// This recalculates everything for the currently selected character
	//
	private final void updateCharacterInfo()
	{
		final PlayerCharacter bPC = Globals.getCurrentPC();
		if (bPC != aPC)
		{
			needsUpdate = true;
		}
		if ((bPC != null) && needsUpdate)
		{
			bPC.getAllSkillList(true); // forces refresh of skills
			resetSelectedModel = true;
			previouslySelectedClass = null;
		}
		aPC = bPC;

		if (aPC == null || !needsUpdate)
		{
			return;
		}

		autoSort.setSelected(aPC.isAutoSortSkills());
		aPC.setAggregateFeatsStable(false);
		aPC.setAutomaticFeatsStable(false);
		aPC.setVirtualFeatsStable(false);

		updateClassSelection();

		PCLevelInfo pcl = getSelectedLevelInfo();
		if (pcl != null)
		{
			currCharClassSkillPnts.setValue(pcl.getSkillPointsRemaining());
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
		resetSelectedModel = true;
		updateAvailableModel();
		updateSelectedModel();
		//Calculate the aggregate feat list
		aPC.aggregateFeatList();
		aPC.setAggregateFeatsStable(true);
		aPC.setAutomaticFeatsStable(true);
		aPC.setVirtualFeatsStable(true);

		needsUpdate = false;
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
			b.append(" &nbsp;<b>EXCLUSIVE</b>:&nbsp; ").append(aSkill.getExclusive());
			String bString = aSkill.getSource();
			if (bString.length() > 0)
			{
				b.append(" &nbsp;<b>SOURCE</b>:").append(bString);
			}

			b.append("</html>");
			infoLabel.setText(b.toString());
		}
	}

	private static int getSelectedIndex(ListSelectionEvent e)
	{
		final DefaultListSelectionModel model = (DefaultListSelectionModel) e.getSource();
		if (model == null)
		{
			return -1;
		}
		return model.getMinSelectionIndex();
	}

	private final void createTreeTables()
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
						GuiFacade.showMessageDialog(null, "No skill selected. Try again.", Constants.s_APPNAME, GuiFacade.ERROR_MESSAGE);
						return;
					}

					Skill aSkill = null;
					if (temp instanceof PObjectNode)
					{
						temp = ((PObjectNode) temp).getItem();
						if (temp instanceof SkillWrapper)
						{
							aSkill = ((SkillWrapper) temp).getSkWrapSkill();
						}
					}
					addButton.setEnabled(aSkill != null);
					setInfoLabelText(aSkill);
				}
			}
		});

		MouseListener ml = new MouseAdapter()
		{
			public void mousePressed(MouseEvent e)
			{
				final int selRow = tree.getRowForLocation(e.getX(), e.getY());
				final TreePath mlSelPath = tree.getPathForLocation(e.getX(), e.getY());
				if (selRow != -1)
				{
					if (e.getClickCount() == 1 && mlSelPath != null)
					{
						tree.setSelectionPath(mlSelPath);
					}
					else if (e.getClickCount() == 2)
					{
						addSkill(1);
					}
				}
			}
		};
		tree.addMouseListener(ml);


		//
		// now do the selectedTable
		//
		selectedTable = new JTreeTable(selectedModel);
		selectedTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
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
						temp = ((PObjectNode) temp).getItem();
						if (temp instanceof SkillWrapper)
						{
							aSkill = ((SkillWrapper) temp).getSkWrapSkill();
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
				final TreePath mlSelPath = btree.getPathForLocation(e.getX(), e.getY());
				if (selRow != -1)
				{
					if (e.getClickCount() == 1)
					{
						btree.setSelectionPath(mlSelPath);
					}
					else if (e.getClickCount() == 2)
					{
						// We run this after the event has been processed so that
						// we don't confuse the table when we change its contents
						SwingUtilities.invokeLater(new Runnable()
						{
							public void run()
							{
								final Skill theSkill = getSelectedSkill();
								if (theSkill != null)
								{
									if (theSkill.getChoiceString() == null)
									{
										addSkill(-1);
									}
									else
									{
										addSkill(-theSkill.costForPCClass(getSelectedPCClass()).intValue());
									}
								}
							}
						});
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
		if (pcgen.core.utils.Utility.doublesEqual(points, 0.0))
		{
			return false;
		}
		int ix = currCharacterClass.getSelectedIndex();
		if (points > 0)
		{
			updateClassSelection();
		}
		PCLevelInfo pcl = getSelectedLevelInfo();
		if (ix != currCharacterClass.getSelectedIndex())
		{
			GuiFacade.showMessageDialog(null, "You must complete selections for " + pcl.getClassKeyName() + "/" + pcl.getLevel() + " before selecting skills for another class/level", "PCGen", GuiFacade.INFORMATION_MESSAGE);
			return false;
		}
		int skillPool = pcl.getSkillPointsRemaining();
		if (points > 0.0 && points > skillPool)
		{
			GuiFacade.showMessageDialog(null, "You only have " + skillPool + " points to spend", "PCGen", GuiFacade.INFORMATION_MESSAGE);
			return false;
		}
		if (points < 0.0 && skillPool - points > pcl.getSkillPointsGained())
		{
			GuiFacade.showMessageDialog(null, pcl.getClassKeyName() + "/" + pcl.getLevel() + " can have at most " + pcl.getSkillPointsGained() + " skill points.", "PCGen", GuiFacade.INFORMATION_MESSAGE);
			return false;
		}
		Skill bSkill = aSkill;
		// the old Skills tab used cost as a double,
		// so I'll duplicate that behavior
		PCClass aClass = getSelectedPCClass();
		final double cost = aSkill.costForPCClass(aClass).doubleValue();
		if (pcgen.core.utils.Utility.doublesEqual(cost, 0.0))
		{
			GuiFacade.showMessageDialog(null, "You cannot modify this skill. A cost of 0 means it is exclusive.", "PCGen", GuiFacade.INFORMATION_MESSAGE);
			return false;
		}
		double rank = points / cost;
		final PlayerCharacter currentPC = Globals.getCurrentPC();
		if (aSkill != null)
		{
			//bSkill.activateBonuses();
			bSkill = currentPC.addSkill(aSkill);
			// in order to get the selected table to sort properly
			// we need to sort the PC's skill list now that the
			// new skill has been added, this won't get called
			// when adding a rank to an existing skill
			// NB: This does get called on a rank change, should it be fixed?

			Collections.sort(currentPC.getSkillList(), new StringIgnoreCaseComparator());

			// Now re calc the output order
			if (autoSort.isSelected())
			{
				allPCSkills = currentPC.getSkillList().iterator();
				while (allPCSkills.hasNext())
				{
					final Skill cSkill = (Skill) allPCSkills.next();
					if (cSkill.getOutputIndex() >= 0)
					{
						cSkill.setOutputIndex(nextOutputIndex++);
					}
				}
			}
			else
			{
				if (bSkill.getOutputIndex() == 0)
				{
					bSkill.setOutputIndex(getHighestOutputIndex() + 1);
				}
			}

		}

		String aString = "";
		if (bSkill != null)
		{
			aString = bSkill.modRanks(rank, aClass);
			if ("".equals(aString))
			{
				pcl.setSkillPointsRemaining(skillPool - (int) points);
				//bSkill.activateBonuses();
			}
			updateClassSelection();
			//
			// Remove the skill from the skill list if we've
			// just set the rank to zero and it is not untrained
			//
			if (pcgen.core.utils.Utility.doublesEqual(bSkill.getRank().doubleValue(), 0.0) && !(bSkill.getUntrained().charAt(0) == 'Y'))
			{
				currentPC.getSkillList().remove(bSkill);
			}
		}

		if (aString.length() > 0)
		{
			GuiFacade.showMessageDialog(null, aString, "PCGen", GuiFacade.INFORMATION_MESSAGE);
			return false;
		}
		return true;
	}

	private Skill getSelectedSkill()
	{
//		if (lastSkill == null)
//		{
//			GuiFacade.showMessageDialog(null,
//				"No skill selected. Try again.", Constants.s_APPNAME, GuiFacade.ERROR_MESSAGE);
//		}
		return lastSkill;
	}

	private void addSkill(int points)
	{
		final Skill theSkill = getSelectedSkill();
		if (theSkill == null)
		{
			return;
		}

		// modRank returns true on success and false on failure
		// if we failed to add skill points, don't do anything
		if (!modRank(theSkill, points))
		{
			return;
		}

		aPC.setDirty(true);

		PCLevelInfo pcl = getSelectedLevelInfo();
		if (pcl != null)
		{
			currCharClassSkillPnts.setValue(pcl.getSkillPointsRemaining());
			totalSkillPointsLeft.setValue(aPC.getSkillPoints());
		}

		updateSelectedModel();

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
	private static int getHighestOutputIndex()
	{
		int maxOutputIndex = 0;

		if (aPC == null)
		{
			return 0;
		}

		for (Iterator i = aPC.getSkillList().iterator(); i.hasNext();)
		{
			final Skill bSkill = (Skill) i.next();
			if (bSkill.getOutputIndex() > maxOutputIndex)
			{
				maxOutputIndex = bSkill.getOutputIndex();
			}
		}

		return maxOutputIndex;
	}

	/**
	 * Updates the Available table
	 **/
	private void updateAvailableModel()
	{
		List pathList = availableTable.getExpandedPaths();
		createAvailableModel();
		availableTable.updateUI();
		availableTable.expandPathList(pathList);
	}

	/**
	 * Updates the Selected table
	 **/
	private void updateSelectedModel()
	{
		List pathList = selectedTable.getExpandedPaths();
		createSelectedModel();
		selectedTable.updateUI();
		selectedTable.expandPathList(pathList);
	}

	/**
	 * Creates the ClassModel that will be used.
	 */
	private final void createModels()
	{
		createAvailableModel();
		createSelectedModel();
	}

	private final void createAvailableModel()
	{
		if (availableModel == null)
		{
			availableModel = new SkillModel(viewMode, true);
		}
		else
		{
			availableModel.resetModel(viewMode, true);
		}
		if (availableSort != null)
		{
			availableSort.setRoot((PObjectNode) availableModel.getRoot());
			availableSort.sortNodeOnColumn();
		}
	}

	private final void createSelectedModel()
	{
		if (selectedModel == null)
		{
			selectedModel = new SkillModel(viewSelectMode, false);
		}
		else
		{
			if (resetSelectedModel)
			{
				selectedModel.resetModel(viewSelectMode, false);
			}
		}
		if (selectedSort != null)
		{
			selectedSort.setRoot((PObjectNode) selectedModel.getRoot());
			selectedSort.sortNodeOnColumn();
		}
	}

	/**
	 * OutputOrderRenderer is a small extension of the standard JLabel based
	 * table cell renderer that allows it to interpret a few special values.
	 * -1 shows as Hidden, and 0 is shown as blank. Any other value is
	 * displayed as is.
	 */
	private static final class OutputOrderRenderer extends DefaultTableCellRenderer
	{

		private OutputOrderRenderer()
		{
			super();
			setHorizontalAlignment(SwingConstants.CENTER);
		}

		public Component getTableCellRendererComponent(JTable jTable, Object value, boolean isSelected, boolean hasFocus, int row, int column)
		{
			JLabel comp = (JLabel) super.getTableCellRendererComponent(jTable, value, isSelected, hasFocus, row, column);

			if (value instanceof Integer)
			{
				int i = ((Integer) value).intValue();
				if (i == -1)
				{
					comp.setText("Hidden");
				}
				else if (i == 0)
				{
					comp.setText("");
				}
				else
				{
					comp.setText(String.valueOf(i));
				}
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
	private static final class OutputOrderEditor extends JComboBoxEx implements TableCellEditor
	{

		private final transient List d_listeners = new ArrayList();
		private transient int d_originalValue = 0;

		private OutputOrderEditor(String[] choices)
		{
			super(choices);

			setEditable(true);

			addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent ae)
				{
					stopCellEditing();
				}
			});
		}

		public void addCellEditorListener(CellEditorListener cellEditorListener)
		{
			d_listeners.add(cellEditorListener);
		}

		public Component getTableCellEditorComponent(JTable jTable, Object value, boolean isSelected, int row, int column)
		{
			if (value == null)
			{
				return this;
			}
			d_originalValue = this.getSelectedIndex();
			if (value instanceof Integer)
			{
				int i = ((Integer) value).intValue();
				if (i == -1)
				{
					setSelectedItem("Hidden");
				}
				else
				{
					setSelectedItem(String.valueOf(i));
				}
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
					return new Integer((String) getSelectedItem());
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
				((CellEditorListener) d_listeners.get(i)).editingCanceled(ce);
			}
		}

		private void fireEditingStopped()
		{
			ChangeEvent ce = new ChangeEvent(this);
			for (int i = d_listeners.size() - 1; i >= 0; --i)
			{
				((CellEditorListener) d_listeners.get(i)).editingStopped(ce);
			}
		}
	} //End OutputOrderEditor classes

	/**
	 * The basic idea of the TreeTableModel is that there is a
	 *  single <code>root</code> object.  This root object has a
	 *  null <code>parent</code>.  All other objects have a parent
	 *  which points to a non-null object.  parent objects contain
	 *  a list of <code>children</code>, which are all the objects
	 *  that point to it as their parent.  objects (or
	 *  <code>nodes</code>) which have 0 children are leafs (the
	 *  end of that linked list).  nodes which have at least 1
	 *  child are not leafs. Leafs are like files and non-leafs
	 *  are like directories.
	 *
	 * TODO: This class implements the java.util.Iterator interface.  However, its next() method is not capable of throwing java.util.NoSuchElementException.  The next() method should be changed so it throws NoSuchElementException if is called when there are no more elements to return.
	 **/
	private final class SkillModel extends AbstractTreeTableModel
	{
		// Types of the columns.
		private int modelType = MODEL_AVAIL;
		private final String[] availNameList = {"Skill", "Cost", "Source"};
		private final String[] selNameList = {"Skill", "Modifier", "Rank", "Total", "Cost", "Order"};

		/**
		 * Creates a SkillModel
		 **/
		private SkillModel(int mode, boolean available)
		{
			super(null);
			if (!available)
			{
				modelType = MODEL_SELECT;
			}
			resetModel(mode, available);
		}

		/**
		 * The available table has three columns removed from
		 * the middle of the selected table This function will
		 * adjust references to "Untrained" through "Source"
		 * to point to the correct column constants NOTE: when
		 * referring to actual display column you still need
		 * to use the original column #
		 **/
		private int adjustAvailColumnConst(int column)
		{
			if (modelType == MODEL_AVAIL)
			{
				if (column > COL_NAME)
				{
					return column + 3;
				}
			}
			if (modelType == MODEL_SELECT)
			{
				if (column > COL_COST)
				{
					return column + 1;
				}
			}
			return column;
		}

		/**
		 * In the availableTable, if filtering out unqualified
		 * items ignore any skill the PC doesn't qualify for
		 **/
		private class DisplayableSkillsIterator implements ResetableListIterator
		{
			private boolean available;
			private ListIterator it;
			private Skill peek; // since we can't "unnext"
			private int index;

			private DisplayableSkillsIterator()
			{
			}

			public DisplayableSkillsIterator(boolean available)
			{
				this.available = available;
				reset();
			}

			public boolean hasNext()
			{
				while (it.hasNext())
				{
					peek = (Skill) it.next();
					if (shouldDisplayThis(peek))
					{
						++index;
						return true;
					}
				}

				return false;
			}

			public boolean hasPrevious()
			{
				while (it.hasPrevious())
				{
					peek = (Skill) it.next();
					if (shouldDisplayThis(peek))
					{
						--index;
						return true;
					}
				}

				return false;
			}

			public Object next()
			{
				return peek; // not allowed to call next without hasNext() first :-)
			}

			public Object previous()
			{
				return peek; // not allowed to call next without hasNext() first :-)
			}

			public int nextIndex()
			{
				return index;
			}

			public int previousIndex()
			{
				return index - 1;
			}

			public void add(Object obj)
			{
				throw new UnsupportedOperationException();
			}

			public void remove()
			{
				throw new UnsupportedOperationException();
			}

			public void set(Object obj)
			{
				throw new UnsupportedOperationException();
			}

			public void reset()
			{
				it = available ? Globals.getSkillList().listIterator() : aPC.getSkillList().listIterator();
				peek = null;
			}
		}

		/**
		 * Conditionally add selected parts of a sequence of
		 * Skills as the children of a PObjectNode.
		 *
		 * THE BIG IDEA: generalize the populating of sort
		 * order for skills so that new UI and custom sort
		 * orders fall out naturally.  You may then make
		 * subsorts or custom sorts to your heart's content.
		 * Are you happy now?
		 *
		 * No assumptions about the skills are made, so if you
		 * want to filter them (say with
		 * <code>shouldDisplayThis(skill)</code>) you need to
		 * do so in <code>does</code>.
		 *
		 * @param node add children here
		 * @param skillsIt an iterator over the skills
		 * @param sorter does the new node go here? what part(s)?
		 * @param available availabl or selected tree model?
		 **/
		private void populateNode(PObjectNode node, ResetableListIterator skillsIt, InfoSkillsSorter sorter, boolean available)
		{
			final SortedSet set = new TreeSet(new StringIgnoreCaseComparator());

			set.clear();
			skillsIt.reset();

			while (skillsIt.hasNext())
			{
				Skill skill = (Skill) skillsIt.next();

				if (!sorter.nodeGoHere(node, skill))
				{
					continue;
				}

				Object part = sorter.whatPart(available, skill);

				if (part instanceof Iterator)
				{
					for (Iterator partIt = (Iterator) part; partIt.hasNext();)
					{
						set.add(new PObjectNode(partIt.next()));
					}
				}

				else
				{
					PObjectNode nameNode = new PObjectNode(part);
					nameNode.setIsValid(skill.passesPreReqToGain());
					set.add(nameNode);
				}
			}

			for (Iterator nodeIt = set.iterator(); nodeIt.hasNext();)
			{
				node.addChild((PObjectNode) nodeIt.next());
			}
		}

		/**
		 * The real work.  Since all the node sorting for
		 * skills is now in data structures, the driver is
		 * trivial.
		 *
		 * @param node the current node to populate
		 * @param skillsIt a resetable skills iterator
		 * @param sorter the real work
		 * @param available available or selected tree model?
		 *
		 * @see InfoSkillsSorter
		 **/
		private void createRootNode(PObjectNode node, ResetableListIterator skillsIt, InfoSkillsSorter sorter, boolean available)
		{
			populateNode(node, skillsIt, sorter, available);

			if (sorter.nodeHaveNext())
			{
				while (node.hasNext())
				{
					createRootNode((PObjectNode) node.next(), skillsIt, sorter.nextSorter(), available);
				}
			}
		}

		private void initRoot(boolean available, InfoSkillsSorter sorter)
		{
			PObjectNode root = new PObjectNode();
			createRootNode(root, new DisplayableSkillsIterator(available), sorter, available);
			setRoot(sorter.finalPass(root));
		}

		/**
		 * This assumes the SkillModel exists
		 * but needs to be repopulated
		 **/
		private void resetModel(int mode, boolean available)
		{

			switch (mode)
			{
				case GuiConstants.INFOSKILLS_VIEW_STAT_TYPE_NAME: // KeyStat/SubType/Name
					initRoot(available, new InfoSkillsSorters.KeystatSubtypeName_Primary(InfoSkills.this));
					break;

				case GuiConstants.INFOSKILLS_VIEW_STAT_NAME: // KeyStat/Name
					initRoot(available, new InfoSkillsSorters.KeystatName_Primary(InfoSkills.this));
					break;

				case GuiConstants.INFOSKILLS_VIEW_TYPE_NAME: // SubType/Name
					initRoot(available, new InfoSkillsSorters.SubtypeName_Primary(InfoSkills.this));
					break;

				case GuiConstants.INFOSKILLS_VIEW_COST_TYPE_NAME: // Cost/SubType/Name
					initRoot(available, new InfoSkillsSorters.CostSubtypeName_Primary(InfoSkills.this));
					break;

				case GuiConstants.INFOSKILLS_VIEW_COST_NAME: // Cost/Name
					initRoot(available, new InfoSkillsSorters.CostName_Primary(InfoSkills.this));
					break;

				case GuiConstants.INFOSKILLS_VIEW_NAME: // Name
					initRoot(available, new InfoSkillsSorters.Name_Primary(InfoSkills.this));
					break;

				default:
					Logging.errorPrint("In InfoSkills.SkillModel.adjustAvailColumnConst the mode " + mode + " is not handled.");
					break;
			}
			PObjectNode rootAsPObjectNode = (PObjectNode) super.getRoot();
			if (rootAsPObjectNode.getChildCount() > 0)
			{
				fireTreeNodesChanged(super.getRoot(), new TreePath(super.getRoot()));
			}
		}

		/**
		 * return a boolean to indicate if the item should be
		 * included in the list
		 **/
		private boolean shouldDisplayThis(final Skill aSkill)
		{
			return (modelType == MODEL_SELECT || accept(aSkill));
		}

		// "There can be only one!" There must be a root
		// object, though it can be hidden to make it's
		// existence basically a convenient way to keep track
		// of the objects
		private void setRoot(PObjectNode aNode)
		{
			super.setRoot(aNode);
		}

		public Object getRoot()
		{
			return (PObjectNode) super.getRoot();
		}

		/* The JTreeTableNode interface. */

		/**
		 * Returns int number of columns.
		 */
		public int getColumnCount()
		{
			return modelType == MODEL_AVAIL ? availNameList.length : selNameList.length;
		}

		/**
		 * Returns String name of a column.
		 */
		public String getColumnName(int column)
		{
			return modelType == MODEL_AVAIL ? availNameList[column] : selNameList[column];
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
					Logging.errorPrint("In InfoSkills.SkillModel.getColumnClass the column " + column + " is not handled.");
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
			final PObjectNode fn = (PObjectNode) node;
			Skill aSkill = null;
			Integer mods = new Integer(0);
			Float ranks = new Float(0);
			Integer outputIndex = new Integer(0);

			if (fn == null)
			{
				Logging.errorPrint("Somehow we have no active node when doing getValueAt in InfoSkills.");
				return null;
			}

			if (fn.getItem() instanceof SkillWrapper)
			{
				SkillWrapper skillA = (SkillWrapper) fn.getItem();
				aSkill = skillA.getSkWrapSkill();
				mods = skillA.getSkWrapMod();
				ranks = skillA.getSkWrapRank();
				outputIndex = skillA.getSkWrapOutputIndex();
			}

			column = adjustAvailColumnConst(column);
			switch (column)
			{
				case COL_NAME: // Name
					return fn.toString();
				case COL_MOD: // Bonus mods
					return mods;
				case COL_RANK: // number of ranks
					return ranks;
				case COL_TOTAL: // Total skill level
					return new Integer(mods.intValue() + ranks.intValue());
				case COL_COST: // Cost to buy skill points
					if (aSkill != null)
					{
						return aSkill.costForPCClass(getSelectedPCClass());
					}
					return new Integer(0);
				case COL_SRC: // Source Info
					if (aSkill != null)
					{
						return aSkill.getSource();
					}
					else
					{
						return fn.getSource();
					}
				case COL_INDEX: // Output index
					return outputIndex;
				case -1:
					return fn.getItem();
				default:
					Logging.errorPrint("In InfoSkills.SkillModel.getValueAt the column " + column + " is not handled.");
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
			{
				return; // can only set values for selectedTableModel
			}
			if (!(((PObjectNode) node).getItem() instanceof SkillWrapper))
			{
				return; // can only use rows with Skills in them
			}

			final PObjectNode fn = (PObjectNode) node;
			SkillWrapper skillA = (SkillWrapper) fn.getItem();
			Skill aSkill = skillA.getSkWrapSkill();

			if (aSkill != null)
			{
				switch (column)
				{
					case 5:
						int outputIndex = ((Integer) aValue).intValue();
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
								final Skill bSkill = (Skill) i.next();
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
								final Skill bSkill = (Skill) i.next();
								if (workingIndex == outputIndex)
								{
									workingIndex++;
								}
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
						{
							updateSelectedModel();
						}
						break;
					default:
						Logging.errorPrint("In InfoSkills.SkillModel.setValueAt the column " + column + " is not handled.");
						break;

				}
			}

		}

	}

	/**
	 * implementation of Filterable interface
	 */
	public final void initializeFilters()
	{
		registerFilter(createClassSkillFilter());
		registerFilter(createCrossClassSkillFilter());
		registerFilter(createExclusiveSkillFilter());
		registerFilter(createQualifyFilter());

		FilterFactory.registerAllSourceFilters(this);
		FilterFactory.registerAllSkillFilters(this);

		setKitFilter("SKILL");
	}

	/**
	 * implementation of Filterable interface
	 */
	public final void refreshFiltering()
	{
		updateAvailableModel();
	}

	/**
	 * specifies whether the "match any" option should be available
	 */
	public final boolean isMatchAnyEnabled()
	{
		return true;
	}

	/**
	 * specifies whether the "negate/reverse" option should be available
	 */
	public final boolean isNegateEnabled()
	{
		return true;
	}

	/**
	 * specifies the filter selection mode
	 */
	public final int getSelectionMode()
	{
		return MULTI_MULTI_MODE;
	}

	/**
	 * Get the currently selected Character Class.
	 * @return   PCClass
	 * author    Brian Forester  (ysgarran@yahoo.com)
	 **/
	public PCClass getSelectedPCClass()
	{
		PCLevelInfo pcl = getSelectedLevelInfo();
		if (pcl != null)
		{
			return aPC.getClassNamed(pcl.getClassKeyName());
		}
//		PCClass aClass = (PCClass) currCharacterClass.getSelectedItem();
		return null;
	}

	public PCLevelInfo getSelectedLevelInfo()
	{
		int i = Math.max(0, currCharacterClass.getSelectedIndex());
		try
		{
			return (PCLevelInfo) aPC.getLevelInfo().get(i);
		}
		catch (Exception e)
		{
		}
		return null;
	}

	/*
	 * ##################################################################
	 * factory methods
	 * these are needed for reflection method calls in FilterFactory!
	 * ##################################################################
	 */

	private final PObjectFilter createClassSkillFilter()
	{
		return new ClassSkillFilter();
	}

	private final PObjectFilter createCrossClassSkillFilter()
	{
		return new CrossClassSkillFilter();
	}

	private final PObjectFilter createExclusiveSkillFilter()
	{
		return new ExclusiveSkillFilter();
	}

	private final PObjectFilter createQualifyFilter()
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
		private QualifyFilter()
		{
			super("Miscellaneous", "Qualify");
			if (SettingsHandler.isToolTipTextShown())
			{
				setDescription("Accept skill, if not exclusive to chosen class.");
			}
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
				Skill aSkill = (Skill) pObject;
				return (aPCClass != null) && !(aSkill.isExclusive() && !aSkill.isClassSkill(aPCClass));
			}

			return true;
		}
	}

	private class ClassSkillFilter extends AbstractPObjectFilter
	{
		private ClassSkillFilter()
		{
			super("Skill", "Class");
			if (SettingsHandler.isToolTipTextShown())
			{
				setDescription("Accept skill, if class skill.");
			}
		}

		public final String getName()
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
				return (aPCClass != null) && ((Skill) pObject).isClassSkill(aPCClass);
			}

			return true;
		}
	}

	private class CrossClassSkillFilter extends AbstractPObjectFilter
	{
		private CrossClassSkillFilter()
		{
			super("Skill", "Cross-Class");
			if (SettingsHandler.isToolTipTextShown())
			{
				setDescription("Accept skill, if cross-class skill.");
			}
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
				Skill aSkill = (Skill) pObject;
				return (aPCClass != null) && !aSkill.isClassSkill(aPCClass) && !aSkill.isExclusive();
			}

			return true;
		}
	}

	private class ExclusiveSkillFilter extends AbstractPObjectFilter
	{
		private ExclusiveSkillFilter()
		{
			super("Skill", "Exclusive");
			if (SettingsHandler.isToolTipTextShown())
			{
				setDescription("Accept skill, if exclusive skill.");
			}
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
				Skill aSkill = (Skill) pObject;
				return (aPCClass != null) && !aSkill.isClassSkill(aPCClass) && aSkill.isExclusive();
			}

			return true;
		}
	}
}
