/*
 * InfoTempMod.java
 * Copyright 2001 (C) Bryan McRoberts <merton_monk@yahoo.com>
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
 * @author  Jayme Cox <jaymecox@users.sourceforge.net>
 * Created on Feb 26, 2003, 2:15 PM
 *
 * Current Ver: $Revision: 1.1 $
 * Last Editor: $Author: vauchers $
 * Last Edited: $Date: 2006/02/21 01:33:40 $
 *
 */

package pcgen.gui.tabs;

import java.awt.BorderLayout;
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
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableColumn;
import javax.swing.tree.TreePath;
import pcgen.core.Constants;
import pcgen.core.Equipment;
import pcgen.core.Feat;
import pcgen.core.Globals;
import pcgen.core.PCClass;
import pcgen.core.PCTemplate;
import pcgen.core.PObject;
import pcgen.core.PlayerCharacter;
import pcgen.core.SettingsHandler;
import pcgen.core.Skill;
import pcgen.core.bonus.Bonus;
import pcgen.core.bonus.BonusObj;
import pcgen.core.spell.Spell;
import pcgen.gui.PCGen_Frame1;
import pcgen.gui.filter.FilterAdapterPanel;
import pcgen.gui.filter.FilterFactory;
import pcgen.gui.filter.FilterConstants;
import pcgen.gui.panes.FlippingSplitPane;
import pcgen.gui.utils.AbstractTreeTableModel;
import pcgen.gui.utils.ChooserFactory;
import pcgen.gui.utils.ChooserRadio;
import pcgen.gui.utils.GuiFacade;
import pcgen.gui.utils.JLabelPane;
import pcgen.gui.utils.JTreeTable;
import pcgen.gui.utils.JTreeTableSorter;
import pcgen.gui.utils.LabelTreeCellRenderer;
import pcgen.gui.utils.PObjectNode;
import pcgen.gui.utils.ResizeColumnListener;
import pcgen.gui.utils.TreeTableModel;
import pcgen.gui.utils.Utility;
import pcgen.util.Logging;

/**
 * <code>InfoTempMod</code> creates a new tabbed panel that is used to
 * allow application of temporary modifiers to PC's and Equipment
 *
 * @author  Jayme Cox <jaymecox@users.sourceforge.net>
 * @version $Revision: 1.1 $
 **/

public class InfoTempMod extends FilterAdapterPanel
{
	private BonusModel bonusModel = null;
	private BonusModel targetModel = null;
	private JTreeTable bonusTable;         // bonus
	private JTreeTable targetTable;        // targets for bonus
	private JTreeTableSorter bonusSort = null;
	private JTreeTableSorter targetSort = null;

	private JTreeTable appliedTable;         // target+bonuses
	private AppliedModel appliedBonusModel;    // applied temp bonuses

	private List tbwList;

	private JLabelPane infoLabel = new JLabelPane();

	private JButton applyBonusButton;
	private JButton removeBonusButton;

	private JCheckBox useTempMods;

	private JPanel topPane = new JPanel();
	private JPanel botPane = new JPanel();

	private FlippingSplitPane topVertSplit;
	private FlippingSplitPane centerHorzSplit;
	private FlippingSplitPane botHorzSplit;

	private Border etched;

	private static PlayerCharacter aPC = null;

	private PObject lastAvaObject = null;

	private boolean needsUpdate = true;
	private boolean hasBeenSized = false;

	// table model modes
	private static final int MODEL_BONUS = 0;
	private static final int MODEL_TARGET = 1;

	//column positions for tables
	// if you change these, you also need to change
	// the selNameList array in the BonusModel class
	private static final int COL_NAME = 0;
	private static final int COL_TYPE = 1;
	private static final int COL_SRC = 2;

	//column position for temporary bonus table
	// if you change these, you need to change
	// the colNameList array in the AppliedModel class
	private static final int BONUS_COL_NAME = 0;
	private static final int BONUS_COL_TYPE = 1;
	private static final int BONUS_COL_TO = 2;
	private static final int BONUS_COL_VAL = 3;

	/**
	 * Temp Bonus wrapper for the appliedTable
	 * each TempWrap contains the creator and target of a bonus
	 **/
	public final class TempWrap
	{
		private Object _creator = null;
		private Object _target = null;
		private BonusObj _bonus = null;

		public TempWrap(Object aMod, Object aTarget, BonusObj aBonus)
		{
			_creator = aMod;
			_target = aTarget;
			_bonus = aBonus;
		}

		public String getName()
		{
			StringBuffer b = new StringBuffer();
			if (_creator instanceof PlayerCharacter)
			{
				b.append(((PlayerCharacter) _creator).getName());
			}
			else if (_creator instanceof PObject)
			{
				b.append(_creator.toString());
			}
			b.append(" [");
			if (_target instanceof PlayerCharacter)
			{
				b.append("Player");
			}
			else if (_target instanceof Equipment)
			{
				b.append(((Equipment) _target).getName());
			}
			b.append("]");
			return b.toString();
		}

		public Object getCreator()
		{
			return _creator;
		}

		public Object getTarget()
		{
			return _target;
		}

		public BonusObj getBonus()
		{
			return _bonus;
		}
	}

	/**
	 * Class Bonus wrapper
	 * each ClassWrap contains a Class and the level
	 **/
	public final class ClassWrap
	{
		private PCClass _class = null;
		private int _level = 0;

		public ClassWrap(PCClass aClass, int aLevel)
		{
			_class = aClass;
			_level = aLevel;
		}

		public PCClass getMyClass()
		{
			return _class;
		}

		public int getLevel()
		{
			return _level;
		}

		public String toString()
		{
			StringBuffer b = new StringBuffer();
			b.append(_class.getName());
			b.append(" (").append(_level).append(")");
			return b.toString();
		}
	}

	/**
	 * create right click menus and listeners
	 **/
	private class BonusPopupMenu extends JPopupMenu
	{
		private class BonusActionListener implements ActionListener
		{
			public void actionPerformed(ActionEvent evt)
			{
			}
		}

		private class AddBonusActionListener extends BonusActionListener
		{
			public void actionPerformed(ActionEvent evt)
			{
				applyBonusButton();
			}
		}

		private class RemoveActionListener extends BonusActionListener
		{
			public void actionPerformed(ActionEvent evt)
			{
				removeBonusButton();
			}
		}

		private class RefreshActionListener extends BonusActionListener
		{
			public void actionPerformed(ActionEvent evt)
			{
				refreshButton();
			}
		}

		private JMenuItem createAddMenuItem(String label, String accelerator)
		{
			return Utility.createMenuItem(label, new AddBonusActionListener(), "Apply bonus", (char) 0, accelerator, "Apply this Bonus", "Add16.gif", true);
		}

		private JMenuItem createRemoveMenuItem(String label, String accelerator)
		{
			return Utility.createMenuItem(label, new RemoveActionListener(), "Remove Bonus", (char) 0, accelerator, "Remove this bonus", "", true);
		}

		private JMenuItem createRefreshMenuItem(String label)
		{
			return Utility.createMenuItem(label, new RefreshActionListener(), "Redraw/Recalc Panel", (char) 0, null, "Redraw/Recalc this panels info", "", true);
		}

		BonusPopupMenu(JTreeTable treeTable)
		{
			if (treeTable == bonusTable)
			{
				BonusPopupMenu.this.add(createAddMenuItem("Apply Bonus", "shortcut EQUALS"));
				BonusPopupMenu.this.addSeparator();
				BonusPopupMenu.this.add(createRefreshMenuItem("Redraw/recalc Panel"));
			}
			else if (treeTable == targetTable)
			{
				BonusPopupMenu.this.add(createRefreshMenuItem("Redraw/recalc Panel"));

			}
			else if (treeTable == appliedTable)
			{
				BonusPopupMenu.this.add(createRemoveMenuItem("Remove Bonus", "shortcut MINUS"));
			}
		}
	}

	private class BonusPopupListener extends MouseAdapter
	{
		private JTreeTable aTreeTable;
		private JTree tree;
		private BonusPopupMenu menu;

		BonusPopupListener(JTreeTable treeTable, BonusPopupMenu aMenu)
		{
			aTreeTable = treeTable;
			tree = treeTable.getTree();
			menu = aMenu;
			KeyListener myKeyListener = new KeyListener()
			{
				public void keyTyped(KeyEvent e)
				{
					dispatchEvent(e);
				}
				//
				// Walk through the list of accelerators to see
				// if the user has pressed a sequence used by
				// the popup. This would not otherwise happen
				// unless the popup was showing
				//
				public void keyPressed(KeyEvent e)
				{
					final int keyCode = e.getKeyCode();
					if (keyCode != KeyEvent.VK_UNDEFINED)
					{
						final KeyStroke keyStroke = KeyStroke.getKeyStrokeForEvent(e);
						for (int i = 0; i < menu.getComponentCount(); ++i)
						{
							final Component menuComponent = menu.getComponent(i);
							if (menuComponent instanceof JMenuItem)
							{
								KeyStroke ks = ((JMenuItem) menuComponent).getAccelerator();
								if ((ks != null) && keyStroke.equals(ks))
								{
									((JMenuItem) menuComponent).doClick(2);
									return;
								}
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
				TreePath selPath = tree.getClosestPathForLocation(evt.getX(), evt.getY());
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
		treeTable.addMouseListener(new BonusPopupListener(treeTable, new BonusPopupMenu(treeTable)));
	}

	/**
	 *  Constructor for the InfoEquips object
	 **/
	InfoTempMod()
	{
		// do not remove this as we will use it
		// to save component specific settings
		setName(Constants.tabNames[Constants.TAB_TEMPBONUS]);

		initComponents();

		initActionListeners();

		FilterFactory.restoreFilterSettings(this);
	}

	/**
	 * Sets the update flag for this tab
	 * It's a lazy update and will only occur
	 * on other status change
	 **/
	public final void setNeedsUpdate(boolean flag)
	{
		needsUpdate = flag;
	}

	/**
	 * This method is called from within the constructor to
	 * initialize the form.
	 */
	private void initComponents()
	{
		// make sure the current PC is set
		aPC = Globals.getCurrentPC();

		// flesh out all the tree views
		createModels();

		// create tables associated with the above trees
		createTreeTables();

		// build topPane which will contain leftPane and rightPane
		// leftPane will have a scrollregion
		// rightPane will have a scrollregion

		GridBagLayout gridbag = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		topPane.setLayout(gridbag);

		JPanel leftPane = new JPanel();
		JPanel rightPane = new JPanel();
		leftPane.setLayout(gridbag);
		rightPane.setLayout(gridbag);

		applyBonusButton = new JButton("Apply Bonus");
		Utility.setDescription(applyBonusButton, "Click to add bonus to selected item");
		applyBonusButton.setEnabled(false);
		applyBonusButton.setPreferredSize(new Dimension(60, 20));
		applyBonusButton.setSize(new Dimension(60, 20));

		Utility.buildConstraints(c, 0, 0, 1, 1, 0, 0);
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.NORTH;
		c.insets = new Insets(2, 0, 2, 0);
		gridbag.setConstraints(applyBonusButton, c);
		topPane.add(applyBonusButton);

		JPanel aPanel = new JPanel();
		aPanel.setLayout(new BorderLayout());

		// Create the split between the two panels
		topVertSplit = new FlippingSplitPane(FlippingSplitPane.HORIZONTAL_SPLIT, leftPane, rightPane);
		topVertSplit.setOneTouchExpandable(true);
		topVertSplit.setDividerSize(10);
		aPanel.add(topVertSplit, BorderLayout.CENTER);

		Utility.buildConstraints(c, 0, 2, 1, 1, 10, 10);
		c.fill = GridBagConstraints.BOTH;
		c.anchor = GridBagConstraints.NORTH;
		c.ipadx = 1;
		gridbag.setConstraints(aPanel, c);
		topPane.add(aPanel);

		// build the left pane for the available bonus table

		Utility.buildConstraints(c, 0, 0, 1, 1, 10, 10);
		c.fill = GridBagConstraints.BOTH;
		c.anchor = GridBagConstraints.NORTH;
		c.ipadx = 1;
		JScrollPane scrollPane = new JScrollPane(bonusTable);
		gridbag.setConstraints(scrollPane, c);
		leftPane.add(scrollPane);

		// now build the right pane for the target table

		gridbag = new GridBagLayout();
		c = new GridBagConstraints();
		rightPane.setLayout(gridbag);

		Utility.buildConstraints(c, 0, 0, 1, 1, 10, 10);
		c.fill = GridBagConstraints.BOTH;
		c.anchor = GridBagConstraints.NORTH;
		c.ipadx = 1;
		scrollPane = new JScrollPane(targetTable);
		gridbag.setConstraints(scrollPane, c);
		targetTable.setShowHorizontalLines(true);
		rightPane.add(scrollPane);

		//bonusTable.setColAlign(COL_TYPE, SwingConstants.RIGHT);
		//bonusTable.setColAlign(COL_QTY, SwingConstants.CENTER);
		//targetTable.setColAlign(COL_TYPE, SwingConstants.RIGHT);
		//targetTable.setColAlign(COL_QTY, SwingConstants.CENTER);


		// ---------- build Bottom Panel ----------------
		// botPane will contain a bHeadPane and a bTailPane
		// bHeadPane will contain a scrollregion (Source Bonus info)
		// bTailPane will contain a scrollregion (applied Bonuses)

		botPane.setLayout(new BorderLayout());

		gridbag = new GridBagLayout();
		c = new GridBagConstraints();
		JPanel bHeadPane = new JPanel();
		JPanel bTailPane = new JPanel();
		bHeadPane.setLayout(gridbag);
		bTailPane.setLayout(gridbag);

		botHorzSplit = new FlippingSplitPane(FlippingSplitPane.VERTICAL_SPLIT, bHeadPane, bTailPane);
		botHorzSplit.setOneTouchExpandable(true);
		botHorzSplit.setDividerSize(10);

		botPane.add(botHorzSplit, BorderLayout.CENTER);

		// Bottom Head (top) panel

		// create an info scroll area
		Utility.buildConstraints(c, 0, 0, 1, 1, 2, 2);
		c.fill = GridBagConstraints.BOTH;
		c.anchor = GridBagConstraints.CENTER;
		JScrollPane sScroll = new JScrollPane();
		gridbag.setConstraints(sScroll, c);

		TitledBorder sTitle = BorderFactory.createTitledBorder(etched, "Bonus Info");
		sTitle.setTitleJustification(TitledBorder.CENTER);
		sScroll.setBorder(sTitle);
		infoLabel.setBackground(topPane.getBackground());
		sScroll.setViewportView(infoLabel);

		bHeadPane.add(sScroll);

		// Bottom Tail (bottom) panel
		// create a temproary bonus select and view panel
		Utility.buildConstraints(c, 0, 0, 1, 1, 1, 1);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.NORTH;
		JPanel rPanel = new JPanel();
		gridbag.setConstraints(rPanel, c);

		JPanel iPanel = new JPanel();
		iPanel.setLayout(new BorderLayout(8, 0));

		useTempMods = new JCheckBox("Use Temporary Bonuses");
		removeBonusButton = new JButton("Remove");
		removeBonusButton.setEnabled(false);

		iPanel.add(useTempMods, BorderLayout.WEST);
		iPanel.add(removeBonusButton, BorderLayout.CENTER);

		rPanel.add(iPanel);

		Utility.buildConstraints(c, 0, 1, 1, 1, 10, 10);
		c.fill = GridBagConstraints.BOTH;
		c.anchor = GridBagConstraints.CENTER;
		JScrollPane tbPane = new JScrollPane(appliedTable);
		gridbag.setConstraints(tbPane, c);

		bTailPane.add(rPanel);
		bTailPane.add(tbPane);

		// now split the top and bottom Panels
		centerHorzSplit = new FlippingSplitPane(FlippingSplitPane.VERTICAL_SPLIT, topPane, botPane);
		centerHorzSplit.setOneTouchExpandable(true);
		centerHorzSplit.setDividerSize(10);

		// now add the entire mess (centered of course)
		this.setLayout(new BorderLayout());
		this.add(centerHorzSplit, BorderLayout.CENTER);

		// make sure we update when switching tabs
		this.addFocusListener(new FocusAdapter()
		{
			public void focusGained(FocusEvent evt)
			{
				updateCharacterInfo();
			}
		});

		// add the sorter tables to that clicking on the TableHeader
		// actualy does something (gawd damn it's slow!)
		bonusSort = new JTreeTableSorter(bonusTable, (PObjectNode) bonusModel.getRoot(), bonusModel);
		targetSort = new JTreeTableSorter(targetTable, (PObjectNode) targetModel.getRoot(), targetModel);

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
				int s = topVertSplit.getDividerLocation();
				if (s > 0)
				{
					SettingsHandler.setPCGenOption("InfoTempMod.topVertSplit", s);
				}
				s = botHorzSplit.getDividerLocation();
				if (s > 0)
				{
					SettingsHandler.setPCGenOption("InfoTempMod.botHorzSplit", s);
				}
				s = centerHorzSplit.getDividerLocation();
				if (s > 0)
				{
					SettingsHandler.setPCGenOption("InfoTempMod.centerHorzSplit", s);
				}
			}
		});
		applyBonusButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				applyBonusButton();
			}
		});
		removeBonusButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				removeBonusButton();
			}
		});
		useTempMods.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				aPC.setUseTempMods(useTempMods.isSelected());
				aPC.setDirty(true);
			}
		});
	}

	/*
	 * set the bonus Info text to the currently selected bonus
	 */
	private void setInfoLabelText(Object anObj)
	{
		Equipment eqI = null;
		Spell aSpell = null;
		Feat aFeat = null;
		PCClass aClass = null;
		PCTemplate aTemp = null;
		Skill aSkill = null;

		if (anObj instanceof Equipment)
		{
			eqI = (Equipment) anObj;
		}
		else if (anObj instanceof Spell)
		{
			aSpell = (Spell) anObj;
		}
		else if (anObj instanceof Feat)
		{
			aFeat = (Feat) anObj;
		}
		else if (anObj instanceof PCClass)
		{
			aClass = (PCClass) anObj;
		}
		else if (anObj instanceof PCTemplate)
		{
			aTemp = (PCTemplate) anObj;
		}
		else if (anObj instanceof Skill)
		{
			aSkill = (Skill) anObj;
		}

		if (aClass != null)
		{
			StringBuffer b = new StringBuffer(300);
			b.append("<html><font size=+1><b>").append(aClass.getName()).append("</b></font>");
			String bString = aClass.getSource();
			if (bString.length() > 0)
			{
				b.append(" <b>SOURCE:</b>").append(bString);
			}
			bString = aClass.getTempDescription();
			if (bString.length() > 0)
			{
				b.append(" <br><b>Desc:</b>").append(bString);
			}
			b.append("</html>");
			infoLabel.setText(b.toString());
		}
		else if (aFeat != null)
		{
			StringBuffer b = new StringBuffer(300);
			b.append("<html><font size=+1><b>").append(aFeat.piSubString()).append("</b></font>");
			b.append(" <b>Type:</b> ").append(aFeat.getType());
			String bString = aFeat.getSource();
			if (bString.length() > 0)
			{
				b.append(" <b>SOURCE:</b>").append(bString);
			}
			bString = aFeat.getTempDescription();
			if (bString.length() > 0)
			{
				b.append(" <br><b>Desc:</b>").append(bString);
			}

			b.append("</html>");
			infoLabel.setText(b.toString());
		}
		else if (eqI != null)
		{
			StringBuffer b = new StringBuffer(300);
			b.append("<html><font size=+1><b>").append(eqI.piSubString()).append("</b></font>");
			if (!eqI.longName().equals(eqI.getName()))
			{
				b.append("(").append(eqI.longName()).append(")");
			}
			b.append(" <b>Type:</b> ").append(eqI.getType());

			String IDS = eqI.getInterestingDisplayString();
			if (IDS.length() > 0)
			{
				b.append(" <b>Properties</b>:").append(eqI.getInterestingDisplayString());
			}

			String bString = eqI.getWeight().toString();
			if (bString.length() > 0)
			{
				b.append(" <b>WT</b>:").append(bString);
			}

			Integer a = eqI.getACBonus();
			if (a.intValue() > 0)
			{
				b.append(" <b>AC</b>:").append(a.toString());
			}
			if (eqI.isArmor() || eqI.isShield())
			{
				a = eqI.getMaxDex();
				b.append(" <b>MAXDEX</b>:").append(a.toString());
				a = eqI.acCheck();
				b.append(" <b>ACCHECK</b>:").append(a.toString());
			}
			if (Globals.getGameModeShowSpellTab())
			{
				a = eqI.spellFailure();
				if (eqI.isArmor() || eqI.isShield() || (a.intValue() != 0))
				{
					b.append(" <b>Arcane Failure</b>:").append(a.toString());
				}
			}

			bString = eqI.moveString();
			if (bString.length() > 0)
			{
				b.append(" <b>Move</b>:").append(bString);
			}
			bString = eqI.getSize();
			if (bString.length() > 0)
			{
				b.append(" <b>Size</b>:").append(bString);
			}
			if (eqI.isWeapon())
			{
				b.append(" <b>Damage</b>:").append(eqI.getDamage());
				b.append(" <b>Crit Mult</b>:").append(eqI.getCritMult());
				b.append(" <b>Crit Range</b>:").append(eqI.getCritRange());
				bString = eqI.getRange().toString();
				if (bString.length() > 0)
				{
					b.append(" <b>Range</b>:").append(bString);
				}
			}
			final int charges = eqI.getRemainingCharges();
			if (charges >= 0)
			{
				b.append(" <b>Charges</b>:").append(charges);
			}

			b.append(" <b>Cost</b>:").append(eqI.getCost());
			b.append(" <b>SOURCE:</b> ").append(eqI.getSource());

			bString = eqI.getTempDescription();
			if (bString.length() > 0)
			{
				b.append(" <br><b>Desc:</b>").append(bString);
			}

			b.append("</html>");
			infoLabel.setText(b.toString());
		}
		else if (aSkill != null)
		{
			StringBuffer b = new StringBuffer(300);
			b.append("<html><font size=+1><b>").append(aSkill.getName()).append("</b></font>");
			String bString = aSkill.getSource();
			if (bString.length() > 0)
			{
				b.append(" <b>SOURCE:</b>").append(bString);
			}
			bString = aSkill.getTempDescription();
			if (bString.length() > 0)
			{
				b.append(" <br><b>Desc:</b>").append(bString);
			}
			b.append("</html>");
			infoLabel.setText(b.toString());
		}
		else if (aSpell != null)
		{
			StringBuffer b = new StringBuffer(300);
			b.append("<html><font size=+1><b>").append(aSpell.piSubString()).append("</b></font>");
			int level = aPC.getFirstSpellLevel(aSpell);
			b.append(" <b>Duration:</b> ").append(aSpell.getDuration());
			b.append(" <b>Range:</b> ").append(aSpell.getRange());
			b.append(" <b>Target:</b> ").append(aSpell.getTarget());
			b.append(" <b>Description:</b> ").append(aSpell.piDescSubString());
			String spellSource = aSpell.getSource();
			if (spellSource.length() > 0)
			{
				b.append(" <b>SOURCE:</b>").append(spellSource);
			}
			String bString = aSpell.getTempDescription();
			if (bString.length() > 0)
			{
				b.append(" <br><b>Desc:</b>").append(bString);
			}

			b.append("</html>");
			infoLabel.setText(b.toString());
		}
		else if (aTemp != null)
		{
			StringBuffer b = new StringBuffer(300);
			b.append("<html><font size=+1><b>").append(aTemp.getName()).append("</b></font>");
			String bString = aTemp.getSource();
			if (bString.length() > 0)
			{
				b.append(" <b>SOURCE:</b>").append(bString);
			}
			bString = aTemp.getTempDescription();
			if (bString.length() > 0)
			{
				b.append(" <br><b>Desc:</b>").append(bString);
			}
			b.append("</html>");
			infoLabel.setText(b.toString());
		}
	}

	private final void createTreeTables()
	{
		bonusTable = new JTreeTable(bonusModel);
		final JTree atree = bonusTable.getTree();
		atree.setRootVisible(false);
		atree.setShowsRootHandles(true);
		atree.setCellRenderer(new LabelTreeCellRenderer());
		bonusTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		bonusTable.getSelectionModel().addListSelectionListener(new ListSelectionListener()
		{
			public void valueChanged(ListSelectionEvent e)
			{
				if (!e.getValueIsAdjusting())
				{
					TreePath avaPath = atree.getSelectionPath();
					if (avaPath == null)
					{
						return;
					}

					Object temp = avaPath.getLastPathComponent();
					if (temp == null)
					{
						infoLabel.setText();
						lastAvaObject = null;
						applyBonusButton.setEnabled(false);
						return;
					}

					PObjectNode fNode = (PObjectNode) temp;
					if ((fNode.getItem() != null) && !(fNode.getItem() instanceof String))
					{
						if (fNode.getItem() instanceof ClassWrap)
						{
							ClassWrap tempObj = (ClassWrap) fNode.getItem();
							lastAvaObject = tempObj.getMyClass();
						}
						else
						{
							lastAvaObject = (PObject) fNode.getItem();
						}
						setInfoLabelText(lastAvaObject);
						updateTargetModel();
						// Default choice is first item
						TreePath initTargPath = targetTable.getTree().getPathForRow(0);
						if (initTargPath != null)
						{
							applyBonusButton.setEnabled(true);
							targetTable.getTree().setSelectionPath(initTargPath);
						}
						else
						{
							applyBonusButton.setEnabled(false);
						}
					}
					else
					{
						applyBonusButton.setEnabled(false);
					}
				}
			}
		});

		MouseListener aml = new MouseAdapter()
		{
			public void mousePressed(MouseEvent e)
			{
				final TreePath avaPath = atree.getPathForLocation(e.getX(), e.getY());
				//final int aRow = bonusTable.rowAtPoint(new Point(e.getX(), e.getY()));
				if (avaPath != null)
				{
					if (e.getClickCount() == 2)
					{
						applyBonusButton();
					}
					else if (e.getClickCount() == 1)
					{
						if (atree.isPathSelected(avaPath))
						{
							atree.removeSelectionPath(avaPath);
						}
						else
						{
							atree.addSelectionPath(avaPath);
						}
					}
				}
			}
		};
		atree.addMouseListener(aml);


		// now do the targetTable and targetTree

		targetTable = new JTreeTable(targetModel);
		final JTree stree = targetTable.getTree();
		stree.setRootVisible(false);
		stree.setShowsRootHandles(true);
		stree.setCellRenderer(new LabelTreeCellRenderer());
		targetTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		targetTable.getSelectionModel().addListSelectionListener(new ListSelectionListener()
		{
			public void valueChanged(ListSelectionEvent e)
			{
				if (!e.getValueIsAdjusting())
				{
					TreePath selPath = stree.getSelectionPath();
					if (selPath == null)
					{
						return;
					}
				}
			}
		});

		MouseListener sml = new MouseAdapter()
		{
			public void mousePressed(MouseEvent e)
			{
				final int selRow = stree.getRowForLocation(e.getX(), e.getY());
				final TreePath selPath = stree.getPathForLocation(e.getX(), e.getY());
				if (selRow != -1)
				{
					if (e.getClickCount() == 1 && selPath != null)
					{
						// Breaks multi-select
						//stree.setSelectionPath(selPath);
					}
				}
			}
		};
		stree.addMouseListener(sml);

		//
		// now do the temporary bonus table
		//
		appliedTable = new JTreeTable(appliedBonusModel);
		final JTree btree = appliedTable.getTree();
		btree.setRootVisible(false);
		btree.setShowsRootHandles(true);
		btree.setCellRenderer(new LabelTreeCellRenderer());
		appliedTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		appliedTable.setShowHorizontalLines(true);

		appliedTable.getSelectionModel().addListSelectionListener(new ListSelectionListener()
		{
			public void valueChanged(ListSelectionEvent e)
			{
				if (!e.getValueIsAdjusting())
				{
					TreePath selPath = btree.getSelectionPath();
					if (selPath == null)
					{
						return;
					}

					//final Object temp = selPath.getPathComponent(1);
					final Object temp = selPath.getLastPathComponent();
					myPONode fNode = (myPONode) temp;
					//if ((fNode.getItem() != null) && !(fNode.getItem() instanceof String))
					if (fNode.getItem() != null)
					{
						removeBonusButton.setEnabled(true);
					}
					else
					{
						removeBonusButton.setEnabled(false);
					}
				}
			}
		});

		MouseListener bml = new MouseAdapter()
		{
			public void mousePressed(MouseEvent e)
			{
				final TreePath selPath = btree.getPathForLocation(e.getX(), e.getY());
				//final int aRow = appliedTable.rowAtPoint(new Point(e.getX(), e.getY()));
				if (selPath != null)
				{
					if (e.getClickCount() == 2)
					{
						removeBonusButton();
					}
					else if (e.getClickCount() == 1)
					{
						if (btree.isPathSelected(selPath))
						{
							btree.removeSelectionPath(selPath);
						}
						else
						{
							btree.addSelectionPath(selPath);
						}
					}
				}
			}
		};
		btree.addMouseListener(bml);

		// create the rightclick popup menus
		hookupPopupMenu(bonusTable);
		hookupPopupMenu(targetTable);
		hookupPopupMenu(appliedTable);
	}

	/**
	 * Redraw/recalc everything
	 **/
	private void refreshButton()
	{
		needsUpdate = true;
		updateCharacterInfo();
	}

	/**
	 * This is called when the tab is shown
	 **/
	private void formComponentShown()
	{
		PCGen_Frame1.getStatusBar().setText("");

		updateCharacterInfo();

		int s = topVertSplit.getDividerLocation();
		int t = centerHorzSplit.getDividerLocation();
		int u = botHorzSplit.getDividerLocation();
		int width;
		if (!hasBeenSized)
		{
			hasBeenSized = true;
			Component c = getParent();
			s = SettingsHandler.getPCGenOption("InfoTempMod.topVertSplit", (c.getWidth() * 1 / 2));
			t = SettingsHandler.getPCGenOption("InfoTempMod.centerHorzSplit", (c.getHeight() * 1 / 2));
			u = SettingsHandler.getPCGenOption("InfoTempMod.botHorzSplit", (botPane.getHeight() * 1 / 2));

			// set the prefered width on targetTable
			for (int i = 0; i < targetTable.getColumnCount(); ++i)
			{
				TableColumn sCol = targetTable.getColumnModel().getColumn(i);
				width = Globals.getCustColumnWidth("TempModSel", i);
				if (width != 0)
				{
					sCol.setPreferredWidth(width);
				}
				sCol.addPropertyChangeListener(new ResizeColumnListener(targetTable, "TempModSel", i));
			}

			// set the prefered width on bonusTable
			for (int i = 0; i < bonusTable.getColumnCount(); ++i)
			{
				TableColumn sCol = bonusTable.getColumnModel().getColumn(i);
				width = Globals.getCustColumnWidth("TempModAva", i);
				if (width != 0)
				{
					sCol.setPreferredWidth(width);
				}
				sCol.addPropertyChangeListener(new ResizeColumnListener(bonusTable, "TempModAva", i));
			}
		}
		if (s > 0)
		{
			topVertSplit.setDividerLocation(s);
			SettingsHandler.setPCGenOption("InfoTempMod.topVertSplit", s);
		}
		if (t > 0)
		{
			centerHorzSplit.setDividerLocation(t);
			SettingsHandler.setPCGenOption("InfoTempMod.centerHorzSplit", t);
		}
		if (u > 0)
		{
			botHorzSplit.setDividerLocation(u);
			SettingsHandler.setPCGenOption("InfoTempMod.botHorzSplit", u);
		}

	}

	/**
	 * This recalculates the states of everything based
	 * upon the currently target character
	 * But first test to see if we need to do anything
	 **/
	public final void updateCharacterInfo()
	{
		final PlayerCharacter bPC = Globals.getCurrentPC();
		if (bPC != aPC)
		{
			needsUpdate = true;
		}
		aPC = bPC;
		if (aPC == null || !needsUpdate)
		{
			return;
		}

		updateBonusModel();
		updateTargetModel();
		updateAppliedModel();

		useTempMods.setSelected(aPC.getUseTempMods());

		needsUpdate = false;
	}

	/**
	 * Applies a temporary bonus to an Object
	 * The target can be either this PlayerCharacter
	 * or an Equipment object
	 **/
	private void applyBonusButton()
	{
		if (bonusTable.getTree().isSelectionEmpty())
		{
			GuiFacade.showMessageDialog(null, "First select a type of bonus use", Constants.s_APPNAME, GuiFacade.ERROR_MESSAGE);
			return;
		}
		if (targetTable.getTree().isSelectionEmpty())
		{
			GuiFacade.showMessageDialog(null, "First select an item to set the temporary bonus on", Constants.s_APPNAME, GuiFacade.ERROR_MESSAGE);
			return;
		}

		TreePath bonusPath = bonusTable.getTree().getSelectionPath();
		TreePath targetPath = targetTable.getTree().getSelectionPath();
		Object anObj = null;
		Object aTarget = null;
		PObject aMod = null;
		int bonusLevel = 999;

		Object endComp = targetPath.getLastPathComponent();
		PObjectNode fNode = (PObjectNode) endComp;
		if (fNode.getItem() != null)
		{
			aTarget = fNode.getItem();
		}

		if (aTarget == null || fNode == null)
		{
			GuiFacade.showMessageDialog(null, "First select an item to set the temporary bonus on", Constants.s_APPNAME, GuiFacade.ERROR_MESSAGE);
			return;
		}

		endComp = bonusPath.getLastPathComponent();
		fNode = (PObjectNode) endComp;
		if (fNode.getItem() != null)
		{
			anObj = fNode.getItem();
		}

		if (anObj == null || fNode == null)
		{
			GuiFacade.showMessageDialog(null, "First select a type of bonus use", Constants.s_APPNAME, GuiFacade.ERROR_MESSAGE);
			return;
		}

		if (anObj instanceof PObject)
		{
			aMod = (PObject) anObj;
		}
		else if (anObj instanceof ClassWrap)
		{
			ClassWrap tempObj = (ClassWrap) anObj;
			aMod = tempObj.getMyClass();
			bonusLevel = tempObj.getLevel();
		}

		Equipment aEq = null;

		if (aTarget instanceof Equipment)
		{
			// Create new Item
			Equipment eq = (Equipment) aTarget;
			aEq = (Equipment) eq.clone();
			aEq.setAppliedName(aMod.getName());
			aEq.resetTempBonusList();
		}

		String repeatValue = "";

		// get the bonus string
		for (Iterator e = aMod.getBonusList().iterator(); e.hasNext();)
		{
			BonusObj aBonus = (BonusObj) e.next();
			String aString = aBonus.toString();

			if (aString.indexOf("PREAPPLY:") >= 0)
			{
				BonusObj newB = null;
				if (aMod instanceof PCClass)
				{
					if (aBonus.getPCLevel() == bonusLevel)
					{
						int idx = aString.indexOf('|');
						newB = Bonus.newBonus(aString.substring(idx + 1));
					}
				}
				else
				{
					newB = Bonus.newBonus(aString);
				}
				if (newB != null)
				{
					// if Target was this PC, then add
					// bonus to TempBonusMap
					if (aTarget instanceof PlayerCharacter)
					{
						newB.setApplied(true);
						newB.setCreatorObject(aMod);
						newB.setTargetObject(aTarget);
						aPC.addTempBonus(newB);
					}
					else if (aEq != null)
					{
						newB.setApplied(true);
						newB.setCreatorObject(aMod);
						newB.setTargetObject(aEq);
						aEq.addTempBonus(newB);
						aPC.addTempBonus(newB);
					}
					if (aMod.getChoiceString().length() > 0)
					{
						repeatValue = getBonusChoice(newB, aMod.getChoiceString(), repeatValue);
					}
					Logging.errorPrint("adding bonus:" + newB.toString());
				}
			}
		}

		// if the Target is an Equipment item
		// then add it to the tempBonusItemList
		if (aEq != null)
		{
			aPC.addTempBonusItemList(aEq);
		}

		updateAppliedModel();

		aPC.setDirty(true);

		// Make sure bonuses are recalculated
		aPC.calcActiveBonuses();

		// now Update all the other tabs
		PCGen_Frame1.forceUpdate_InfoAbilities();
		PCGen_Frame1.forceUpdate_InfoClasses();
		PCGen_Frame1.forceUpdate_InfoFeats();
		PCGen_Frame1.forceUpdate_InfoSkills();
		PCGen_Frame1.forceUpdate_InfoSpells();
		PCGen_Frame1.forceUpdate_InfoSummary();
	}

	/**
	 * removes a bonus, target pair from the appliedTable
	 **/
	private void removeBonusButton()
	{
		if (appliedTable.getTree().isSelectionEmpty())
		{
			GuiFacade.showMessageDialog(null, "First select a bonus to remove", Constants.s_APPNAME, GuiFacade.ERROR_MESSAGE);
			return;
		}

		TreePath bonusPath = appliedTable.getTree().getSelectionPath();

		TempWrap tbWrap = null;
		Object aCreator = null;
		Object aTarget = null;

		Object endComp = bonusPath.getLastPathComponent();
		myPONode fNode = (myPONode) endComp;
		if ((fNode == null) || (fNode.getItem() == null))
		{
			Logging.errorPrint("fNode == null");
			return;
		}
		if (fNode.getItem() instanceof TempWrap)
		{
			tbWrap = (TempWrap) fNode.getItem();
		}
		else if (fNode.getItem() instanceof String)
		{
			for (Iterator tb = tbwList.iterator(); tb.hasNext();)
			{
				TempWrap tw = (TempWrap) tb.next();
				if (tw.getName().equals(fNode.getItem()))
				{
					tbWrap = tw;
				}
			}
		}
		else
		{
			Logging.errorPrint("Unknown bonus type");
			return;
		}
		if (tbWrap != null)
		{
			aCreator = tbWrap.getCreator();
			aTarget = tbWrap.getTarget();
		}

		if (aTarget == null || aCreator == null)
		{
			Logging.errorPrint("Target or Creator == null");
			return;
		}

		Equipment aEq = null;
		PlayerCharacter bPC = null;

		if (aTarget instanceof Equipment)
		{
			aEq = (Equipment) aTarget;
		}
		else if (aTarget instanceof PlayerCharacter)
		{
			bPC = (PlayerCharacter) aTarget;
		}

		List tbList = new LinkedList(aPC.getTempBonusList());
		for (Iterator e = tbList.iterator(); e.hasNext();)
		{
			BonusObj aBonus = (BonusObj) e.next();
			Object aC = aBonus.getCreatorObject();
			Object aT = aBonus.getTargetObject();

			if ((aT instanceof Equipment) && (aEq != null))
			{
				if (aEq.equals(aT) && (aCreator == aC))
				{
					aPC.removeTempBonus(aBonus);
					aPC.removeTempBonusItemList((Equipment) aT);
				}
			}
			else if ((aT instanceof PlayerCharacter) && (bPC != null))
			{
				if (((PlayerCharacter) aT == bPC) && (aCreator == aC))
				{
					aPC.removeTempBonus(aBonus);
				}
			}
		}

		updateAppliedModel();
		aPC.setDirty(true);

		// Make sure bonuses are recalculated
		aPC.calcActiveBonuses();

		// now Update all the other tabs
		PCGen_Frame1.forceUpdate_InfoAbilities();
		PCGen_Frame1.forceUpdate_InfoClasses();
		PCGen_Frame1.forceUpdate_InfoFeats();
		PCGen_Frame1.forceUpdate_InfoSkills();
		PCGen_Frame1.forceUpdate_InfoSpells();
		PCGen_Frame1.forceUpdate_InfoSummary();

	}

	/**
	 * allows user to choose value of bonus
	 **/
	private String getBonusChoice(BonusObj newB, String aChoice, String repeatValue)
	{
		newB.setChoiceString(aChoice);

		// If repeatValue is set, this is a multi BONUS and they all
		// should get the same value as the first choice
		if (repeatValue.length() > 0)
		{
			// need to parse the aChoice string
			// and replace %CHOICE with choice
			if (newB.getValue().indexOf("%CHOICE") >= 0)
			{
				String ac = pcgen.core.utils.Utility.replaceAll(newB.getValue(), "%CHOICE", repeatValue);
				newB.setValue(ac);
			}
			return repeatValue;
		}

		StringTokenizer aTok = new StringTokenizer(aChoice, "|");
		if (aChoice.startsWith("NUMBER") && (aTok.countTokens() >= 3))
		{
			int min;
			int max;
			aTok.nextToken(); // throw away "NUMBER"
			String minString = aTok.nextToken();
			String maxString = aTok.nextToken();
			String titleString = "Pick a Number";
			if (aTok.hasMoreTokens())
			{
				titleString = aTok.nextToken();
				if (titleString.startsWith("TITLE="))
				{
					// remove TITLE=
					titleString = titleString.substring(6);
				}
			}
			if (minString.startsWith("MIN="))
			{
				minString = minString.substring(4);
				min = aPC.getVariableValue(minString, "").intValue();
			}
			else
			{
				min = aPC.getVariableValue(minString, "").intValue();
			}
			if (maxString.startsWith("MAX="))
			{
				maxString = maxString.substring(4);
				max = aPC.getVariableValue(maxString, "").intValue();
			}
			else
			{
				max = aPC.getVariableValue(maxString, "").intValue();
			}
			if ((max > 0) || (min <= max))
			{
				List numberList = new LinkedList();
				for (int i = min; i <= max; i++)
				{
					final Integer anInt = new Integer(i);
					numberList.add(anInt.toString());
				}
				// let them choose the number from a radio list
				ChooserRadio c = ChooserFactory.getRadioInstance();
				c.setAvailableList(numberList);
				c.setVisible(false);
				c.setTitle("Pick a Number");
				c.setMessageText(titleString);
				c.show();

				if (c.getSelectedList().size() > 0)
				{
					String aI = (String) c.getSelectedList().get(0);
					repeatValue = aI;

					// need to parse the bonus.getValue()
					// string and replace %CHOICE
					if (newB.getValue().indexOf("%CHOICE") >= 0)
					{
						String ac = pcgen.core.utils.Utility.replaceAll(newB.getValue(), "%CHOICE", aI);
						aI = ac;
						newB.setValue(aI);
					}
					return repeatValue;
				}
				else
				{
					// they hit the cancel button
					newB.setValue("0");
				}
			}
		}
		return "";
	}

	/**
	 * Checks to see if aType is in aList
	 **/
	private boolean checkPreApplyType(String aList, Equipment anEq)
	{
		// PREAPPLY target could be a Player or Equipment types
		//
		// PREAPPLY:Ranged,Thrown
		//  -> applied to Ranged Thrown weapons
		// PREAPPLY:Melee;Ranged
		//  -> applied to Melee or Ranged weapons
		//  -> so , means AND
		//  -> so ; mean OR
		// PREAPPLY:PC
		//  -> applied to PlayerCharacter object
		// PREAPPLY:ANYPC
		//  -> applied to PlayerCharacter object
		//
		boolean flag = true;

		final StringTokenizer aTok = new StringTokenizer(aList, ",;", true);
		int iLogicType = -1;

		while (aTok.hasMoreTokens())
		{
			String aString = aTok.nextToken();
			if (",".equals(aString))
			{
				iLogicType = 0;  // AND
			}
			else if (";".equals(aString))
			{
				iLogicType = 1;  // OR
			}
			else
			{
				boolean bIsType;
				boolean bInvert = false;
				if (aString.length() > 0 && aString.charAt(0) == '[' && aString.endsWith("]"))
				{
					aString = aString.substring(1, aString.length() - 1);
					bInvert = true;
				}

				bIsType = anEq.isType(aString.toUpperCase());

				if (bInvert)
				{
					bIsType = !bIsType;
				}

				if (iLogicType == -1)
				{
					flag = bIsType;
				}
				else if (iLogicType == 0)
				{
					flag &= bIsType;
				}
				else
				{
					flag |= bIsType;
				}
			}
		}
		return flag;
	}

	private final void createModels()
	{
		createBonusModel();
		createTargetModel();
		createAppliedModel();
	}

	private final void createBonusModel()
	{
		if (bonusModel == null)
		{
			bonusModel = new BonusModel(MODEL_BONUS);
		}
		else
		{
			bonusModel.resetModel(MODEL_BONUS);
		}
		if (bonusSort != null)
		{
			bonusSort.setRoot((PObjectNode) bonusModel.getRoot());
			//bonusSort.sortNodeOnColumn();
		}
	}

	private final void createTargetModel()
	{
		if (targetModel == null)
		{
			targetModel = new BonusModel(MODEL_TARGET);
		}
		else
		{
			targetModel.resetModel(MODEL_TARGET);
		}
		if (targetSort != null)
		{
			targetSort.setRoot((PObjectNode) targetModel.getRoot());
			//targetSort.sortNodeOnColumn();
		}
	}

	/**
	 * Updates the Bonus table
	 **/
	private void updateBonusModel()
	{
		List pathList = bonusTable.getExpandedPaths();
		createBonusModel();
		bonusTable.updateUI();
		bonusTable.expandPathList(pathList);
	}

	/**
	 * Updates the Target table
	 **/
	private void updateTargetModel()
	{
		List pathList = targetTable.getExpandedPaths();
		createTargetModel();
		targetTable.updateUI();
		targetTable.expandPathList(pathList);
	}

	/**
	 * The TreeTableModel has a single <code>root</code> node
	 * This root node has a null <code>parent</code>
	 * All other nodes have a parent which points to a non-null node
	 * Parent nodes contain a list of  <code>children</code>, which
	 * are all the nodes that point to it as their parent
	 * <code>nodes</code> which have 0 children are leafs (the end of
	 * that linked list)  Nodes which have at least 1 child are not leafs
	 * Leafs are like files and non-leafs are like directories
	 * The leafs contain an Object that we want to know about (Equipment)
	 **/
	private final class BonusModel extends AbstractTreeTableModel
	{
		// there are two roots. One for bonus equipment
		// and one for target equipment profiles
		private PObjectNode avaRoot;
		private PObjectNode selRoot;

		// list of columns names
		private String[] avaNameList = {""};
		private String[] selNameList = {""};

		// Types of the columns.
		private int modelType = MODEL_BONUS;

		/**
		 * Creates a BonusModel
		 **/
		private BonusModel(int iModel)
		{
			super(null);

			//
			// if you change/add/remove entries to nameList
			// you also need to change the static COL_XXX defines
			// at the begining of this file
			//
			avaNameList = new String[]{"Name", "Source", "File"};
			selNameList = new String[]{"Name", "Target", "File"};

			modelType = iModel;
			resetModel(iModel);
		}

		/**
		 * This assumes the BonusModel exists but
		 * needs branches and nodes to be repopulated
		 **/
		private void resetModel(int argModelType)
		{
			// This is the array of all equipment types
			List eqTypeList = new LinkedList();
			List typeList = new LinkedList();

			// build the list of all equipment types
			eqTypeList.add(Constants.s_CUSTOM);
			for (Iterator iSet = aPC.getEquipmentList().iterator(); iSet.hasNext();)
			{
				final Equipment bEq = (Equipment) iSet.next();
				final StringTokenizer aTok = new StringTokenizer(bEq.getType(), ".", false);
				String aString;
				while (aTok.hasMoreTokens())
				{
					aString = aTok.nextToken();
					if (!eqTypeList.contains(aString))
					{
						eqTypeList.add(aString);
					}
				}
			}
			Collections.sort(eqTypeList);

			typeList.add("Feats");
			typeList.add("Items");
			typeList.add("Spells");
			typeList.add("Classes");
			typeList.add("Templates");
			typeList.add("Skills");

			//
			// build bonusTable (list of all equipment)
			//
			if (argModelType == MODEL_BONUS)
			{
				// this is the root node
				avaRoot = new PObjectNode();

				setRoot(avaRoot);

				// build the Type root nodes
				PObjectNode[] pNode = new PObjectNode[6];
				pNode[0] = new PObjectNode("Feat");
				pNode[1] = new PObjectNode("Spell");
				pNode[2] = new PObjectNode("Item");
				pNode[3] = new PObjectNode("Class");
				pNode[4] = new PObjectNode("Templates");
				pNode[5] = new PObjectNode("Skills");

				//
				// first do PC's feats
				for (Iterator fI = aPC.aggregateFeatList().iterator(); fI.hasNext();)
				{
					Feat aFeat = (Feat) fI.next();
					for (Iterator e = aFeat.getBonusList().iterator(); e.hasNext();)
					{
						BonusObj aBonus = (BonusObj) e.next();
						final String aString = aBonus.getPrereqString();
						if (aString.indexOf("PREAPPLY:") >= 0)
						{
							PObjectNode aFN = new PObjectNode(aFeat);
							aFN.setParent(pNode[0]);
							pNode[0].addChild(aFN, true);
							pNode[0].setParent(avaRoot);
						}
					}
				}
				//
				// next do all Feats to get PREAPPLY:ANYPC
				for (Iterator fI = Globals.getFeatList().iterator(); fI.hasNext();)
				{
					Feat aFeat = (Feat) fI.next();
					for (Iterator e = aFeat.getBonusList().iterator(); e.hasNext();)
					{
						BonusObj aBonus = (BonusObj) e.next();
						final String aString = aBonus.getPrereqString();
						if (aString.indexOf("PREAPPLY:ANYPC") >= 0)
						{
							PObjectNode aFN = new PObjectNode(aFeat);
							aFN.setParent(pNode[0]);
							pNode[0].addChild(aFN, true);
							pNode[0].setParent(avaRoot);
						}
					}
				}
				//
				// Do all the PC's spells
				for (Iterator fI = aPC.aggregateSpellList("Any", "", "", 0, 9).iterator(); fI.hasNext();)
				{
					final Spell aSpell = (Spell) fI.next();
					if (aSpell == null)
					{
						continue;
					}
					for (Iterator e = aSpell.getBonusList().iterator(); e.hasNext();)
					{
						BonusObj aBonus = (BonusObj) e.next();
						final String aString = aBonus.getPrereqString();
						if (aString.indexOf("PREAPPLY:") >= 0)
						{
							PObjectNode aFN = new PObjectNode(aSpell);
							aFN.setParent(pNode[1]);
							pNode[1].addChild(aFN, true);
							pNode[1].setParent(avaRoot);
						}
					}
				}
				//
				// Next do all spells to get PREAPPLY:ANYPC
				for (Iterator fI = Globals.getSpellMap().values().iterator(); fI.hasNext();)
				{
					final Object obj = fI.next();
					Spell aSpell = null;
					if (obj instanceof Spell)
					{
						aSpell = (Spell) obj;
					}
					else if (obj instanceof ArrayList)
					{
						continue;
					}
					if (aSpell == null)
					{
						continue;
					}
					for (Iterator e = aSpell.getBonusList().iterator(); e.hasNext();)
					{
						BonusObj aBonus = (BonusObj) e.next();
						final String aString = aBonus.getPrereqString();
						if ((aString.indexOf("PREAPPLY:") >= 0) && (aString.indexOf("PREAPPLY:PC") == -1))
						{
							PObjectNode aFN = new PObjectNode(aSpell);
							aFN.setParent(pNode[1]);
							pNode[1].addChild(aFN, true);
							pNode[1].setParent(avaRoot);
						}
					}
				}
				if (!pNode[1].isLeaf())
				{
					pNode[1].setParent(avaRoot);
				}
				//
				// iterate thru all PC's equipment objects
				for (Iterator fI = aPC.getEquipmentList().iterator(); fI.hasNext();)
				{
					final Equipment aEq = (Equipment) fI.next();
					for (Iterator e = aEq.getBonusList().iterator(); e.hasNext();)
					{
						BonusObj aBonus = (BonusObj) e.next();
						final String aString = aBonus.getPrereqString();
						if (aString.indexOf("PREAPPLY:") >= 0)
						{
							PObjectNode aFN = new PObjectNode(aEq);
							aFN.setParent(pNode[2]);
							pNode[2].addChild(aFN, true);
							pNode[2].setParent(avaRoot);
						}
					}
				}
				if (!pNode[2].isLeaf())
				{
					pNode[2].setParent(avaRoot);
				}
				//
				// iterate thru all PC's Classes
				for (Iterator fI = aPC.getClassList().iterator(); fI.hasNext();)
				{
					final PCClass aClass = (PCClass) fI.next();
					for (Iterator e = aClass.getBonusList().iterator(); e.hasNext();)
					{
						BonusObj aBonus = (BonusObj) e.next();
						final String aString = aBonus.getPrereqString();
						final int myLevel = aClass.getLevel();
						final int level = aBonus.getPCLevel();
						if ((aString.indexOf("PREAPPLY:") >= 0) && (myLevel >= level))
						{
							PObjectNode aFN = new PObjectNode(new ClassWrap(aClass, level));
							aFN.setParent(pNode[3]);
							pNode[3].addChild(aFN, true);
							pNode[3].setParent(avaRoot);
						}
					}
				}
				if (!pNode[3].isLeaf())
				{
					pNode[3].setParent(avaRoot);
				}
				//
				// Iterate through all the PC's Templates
				for (Iterator fI = aPC.getTemplateList().iterator(); fI.hasNext();)
				{
					final PCTemplate aTemp = (PCTemplate) fI.next();
					for (Iterator e = aTemp.getBonusList().iterator(); e.hasNext();)
					{
						BonusObj aBonus = (BonusObj) e.next();
						final String aString = aBonus.getPrereqString();
						if (aString.indexOf("PREAPPLY:") >= 0)
						{
							PObjectNode aFN = new PObjectNode(aTemp);
							aFN.setParent(pNode[4]);
							pNode[4].addChild(aFN, true);
							pNode[4].setParent(avaRoot);
						}
					}
				}
				// do all Templates to get PREAPPLY:ANYPC
				for (Iterator fI = Globals.getTemplateList().iterator(); fI.hasNext();)
				{
					final PCTemplate aTemp = (PCTemplate) fI.next();
					for (Iterator e = aTemp.getBonusList().iterator(); e.hasNext();)
					{
						BonusObj aBonus = (BonusObj) e.next();
						final String aString = aBonus.getPrereqString();
						if (aString.indexOf("PREAPPLY:ANYPC") >= 0)
						{
							PObjectNode aFN = new PObjectNode(aTemp);
							aFN.setParent(pNode[4]);
							pNode[4].addChild(aFN, true);
							pNode[4].setParent(avaRoot);
						}
					}
				}
				if (!pNode[4].isLeaf())
				{
					pNode[4].setParent(avaRoot);
				}
				//
				// Iterate through all the PC's Skills
				for (Iterator fI = aPC.getSkillList().iterator(); fI.hasNext();)
				{
					final Skill aSkill = (Skill) fI.next();
					for (Iterator e = aSkill.getBonusList().iterator(); e.hasNext();)
					{
						BonusObj aBonus = (BonusObj) e.next();
						final String aString = aBonus.getPrereqString();
						if (aString.indexOf("PREAPPLY:") >= 0)
						{
							PObjectNode aFN = new PObjectNode(aSkill);
							aFN.setParent(pNode[5]);
							pNode[5].addChild(aFN, true);
							pNode[5].setParent(avaRoot);
						}
					}
				}
				if (!pNode[5].isLeaf())
				{
					pNode[5].setParent(avaRoot);
				}

				// now add to the root node
				avaRoot.setChildren(pNode);

			} // end of bonusTable builder

			else

			{ // targetTable builder

				// this is the root node
				selRoot = new PObjectNode();
				setRoot(selRoot);

				if (lastAvaObject == null)
				{
					return;
				}

				boolean found = false;
				for (Iterator e = lastAvaObject.getBonusList().iterator(); e.hasNext();)
				{
					BonusObj aBonus = (BonusObj) e.next();
					if (aBonus == null)
					{
						continue;
					}
					if (aBonus.getPrereqList() == null)
					{
						continue;
					}
					for (Iterator pb = aBonus.getPrereqList().iterator(); pb.hasNext();)
					{
						String aString = (String) pb.next();
						if (aString.indexOf("PREAPPLY:") < 0)
						{
							continue;
						}
						final String aType = aString.substring(aString.indexOf("PREAPPLY:") + 9);
						if ((aType.equals("PC") || aType.equals("ANYPC")) && !found)
						{
							PObjectNode aFN = new PObjectNode(aPC);
							aFN.setParent(selRoot);
							selRoot.addChild(aFN, true);
							found = true;
						}
					}
				}
				for (Iterator fI = aPC.getEquipmentList().iterator(); fI.hasNext();)
				{
					final Equipment aEq = (Equipment) fI.next();
					found = false;
					for (Iterator e = lastAvaObject.getBonusList().iterator(); e.hasNext();)
					{
						BonusObj aBonus = (BonusObj) e.next();
						if (aBonus == null)
						{
							continue;
						}
						if (aBonus.getPrereqList() == null)
						{
							continue;
						}
						for (Iterator pb = aBonus.getPrereqList().iterator(); pb.hasNext();)
						{
							String aString = (String) pb.next();
							if (aString.indexOf("PREAPPLY:") < 0)
							{
								continue;
							}
							final String aType = aString.substring(aString.indexOf("PREAPPLY:") + 9);
							if (checkPreApplyType(aType, aEq) && !found)
							{
								PObjectNode aFN = new PObjectNode(aEq);
								aFN.setParent(selRoot);
								selRoot.addChild(aFN, true);
								found = true;
							}
						}
					}
				}
				setRoot(selRoot);

			} // end if else

			PObjectNode rootAsPObjectNode = (PObjectNode) super.getRoot();
			if ((rootAsPObjectNode != null) && (rootAsPObjectNode.getChildCount() > 0))
			{
				fireTreeNodesChanged(super.getRoot(), new TreePath(super.getRoot()));
			}

		}

		// There must be a root node, but we keep it hidden
		private void setRoot(PObjectNode aNode)
		{
			super.setRoot(aNode);
		}

		// return the root node
		public Object getRoot()
		{
			return (PObjectNode) super.getRoot();
		}

		/* The JTreeTableNode interface. */

		/**
		 * Returns int number of columns. (BonusModel)
		 **/
		public int getColumnCount()
		{
			return modelType == MODEL_BONUS ? avaNameList.length : selNameList.length;
		}

		/**
		 * Returns String name of a column. (BonusModel)
		 **/
		public String getColumnName(int column)
		{
			return modelType == MODEL_BONUS ? avaNameList[column] : selNameList[column];
		}

		/**
		 * Returns Class for the column. (BonusModel)
		 **/
		public Class getColumnClass(int column)
		{
			switch (column)
			{
				case COL_NAME:
					return TreeTableModel.class;

				case COL_TYPE:
				case COL_SRC:
					break;

				default:
					Logging.errorPrint("In InfoTempMod.getColumnClass the column " + column + " is not supported.");
					break;
			}
			return String.class;
		}

		/**
		 * Returns boolean if can edit a cell. (BonusModel)
		 **/
		public boolean isCellEditable(Object node, int column)
		{
			return column == COL_NAME;
		}

		/**
		 * Returns Object value of the column. (BonusModel)
		 **/
		public Object getValueAt(Object node, int column)
		{
			PObjectNode fn = (PObjectNode) node;
			Feat aFeat = null;
			Spell aSpell = null;
			Equipment eqI = null;
			PCClass aClass = null;
			PCTemplate aTemp = null;
			Skill aSkill = null;
			PlayerCharacter bPC = null;

			if (fn == null)
			{
				Logging.errorPrint("Somehow we have no active node when doing getValueAt in InfoTempMod.");
				return null;
			}

			if (fn.getItem() instanceof Equipment)
			{
				eqI = (Equipment) fn.getItem();
			}
			else if (fn.getItem() instanceof Feat)
			{
				aFeat = (Feat) fn.getItem();
			}
			else if (fn.getItem() instanceof Spell)
			{
				aSpell = (Spell) fn.getItem();
			}
			else if (fn.getItem() instanceof ClassWrap)
			{
				ClassWrap tempObj = (ClassWrap) fn.getItem();
				aClass = tempObj.getMyClass();
			}
			else if (fn.getItem() instanceof PCTemplate)
			{
				aTemp = (PCTemplate) fn.getItem();
			}
			else if (fn.getItem() instanceof Skill)
			{
				aSkill = (Skill) fn.getItem();
			}
			else if (fn.getItem() instanceof PlayerCharacter)
			{
				bPC = (PlayerCharacter) fn.getItem();
			}

			switch (column)
			{
				case COL_NAME:
					if (bPC != null)
					{
						return bPC.getName();
					}
					else
					{
						return fn != null ? fn.toString() : null;
					}
				case COL_TYPE:
					if (eqI != null)
					{
						return eqI.getType();
					}
					else if (aSpell != null)
					{
						return "Spell";
					}
					else if (aFeat != null)
					{
						return "Feat";
					}
					else if (aClass != null)
					{
						return "Class";
					}
					else if (aTemp != null)
					{
						return "Template";
					}
					else if (aSkill != null)
					{
						return "Skill";
					}
					else if (bPC != null)
					{
						return "Character (You)";
					}
					else
					{
						return null;
					}
				case COL_SRC:
					if (eqI != null)
					{
						return eqI.getSource();
					}
					else if (aSpell != null)
					{
						return aSpell.getSource();
					}
					else if (aFeat != null)
					{
						return aFeat.getSource();
					}
					else if (aClass != null)
					{
						return aClass.getSource();
					}
					else if (aTemp != null)
					{
						return aTemp.getSource();
					}
					else if (aSkill != null)
					{
						return aSkill.getSource();
					}
					else
					{
						return null;
					}
				default:
					if (fn != null)
					{
						return fn.toString();
					}
					else
					{
						Logging.errorPrint("Somehow we have no active node when doing getValueAt in InfoEquip.");
						return null;
					}

			}
			// return null;
		}
	}

	/**
	 * Updates the Temp Bonus table
	 **/
	private void updateAppliedModel()
	{
		List pathList = appliedTable.getExpandedPaths();
		createAppliedModel();
		appliedTable.updateUI();
		appliedTable.expandPathList(pathList);
	}

	/**
	 * Creates the Temp AppliedModel
	 **/
	private void createAppliedModel()
	{
		if (appliedBonusModel == null)
		{
			appliedBonusModel = new AppliedModel();
		}
		else
		{
			appliedBonusModel.resetModel();
		}
	}

	private final class AppliedModel extends AbstractTreeTableModel
	{
		private myPONode bonusRoot;

		// list of columns names
		private String[] colNameList = new String[]
		{
			"Name [Target]",
			"Bonus Type",
			"Bonus To",
			"Bonus Value"
		};

		/**
		 * Creates a AppliedModel
		 **/
		private AppliedModel()
		{
			super(null);
			resetModel();
		}

		/**
		 * This assumes the AppliedModel exists but
		 * needs branches and nodes to be repopulated
		 **/
		private void resetModel()
		{
			// this is the root node
			bonusRoot = new myPONode();

			// an array of TempWrap'ers
			List sList = new LinkedList();
			tbwList = new LinkedList();

			// iterate thru all PC's bonuses
			// and build an Array of TempWrap'ers
			for (Iterator fI = aPC.getTempBonusList().iterator(); fI.hasNext();)
			{
				BonusObj aBonus = (BonusObj) fI.next();
				Object aC = aBonus.getCreatorObject();
				Object aT = aBonus.getTargetObject();
				TempWrap tw = new TempWrap(aC, aT, aBonus);

				tbwList.add(tw);

				String sString = tw.getName();
				if (!sList.contains(sString))
				{
					sList.add(sString);
				}
			}

			// build the tree structure
			myPONode[] cc = new myPONode[sList.size()];
			for (int i = 0; i < sList.size(); i++)
			{
				String hString = (String) sList.get(i);
				cc[i] = new myPONode();
				cc[i].setItem(hString);

				for (int j = 0; j < tbwList.size(); j++)
				{
					TempWrap tw = (TempWrap) tbwList.get(j);
					String aString = tw.getName();
					if (hString.equals(aString))
					{
						myPONode aFN = new myPONode(tw);
						aFN.setParent(cc[i]);
						cc[i].addChild(aFN);
					}
				}
				if (!cc[i].isLeaf())
				{
					cc[i].setParent(bonusRoot);
				}
			}
			bonusRoot.setChildren(cc);

			setRoot(bonusRoot);

			myPONode rootAsPObjectNode = (myPONode) super.getRoot();
			if ((rootAsPObjectNode != null) && (rootAsPObjectNode.getChildCount() > 0))
			{
				fireTreeNodesChanged(super.getRoot(), new TreePath(super.getRoot()));
			}

		}

		// There must be a root node, but we keep it hidden
		private void setRoot(myPONode aNode)
		{
			super.setRoot(aNode);
		}

		// return the root node
		public Object getRoot()
		{
			return (myPONode) super.getRoot();
		}

		/* The JTreeTableNode interface. */

		/**
		 * Returns int number of columns
		 **/
		public int getColumnCount()
		{
			return colNameList.length;
		}

		/**
		 * Returns String name of a column
		 **/
		public String getColumnName(int column)
		{
			return colNameList[column];
		}

		/**
		 * Returns Class for the column
		 **/
		public Class getColumnClass(int column)
		{
			if (column == BONUS_COL_NAME)
			{
				return TreeTableModel.class;
			}
			return String.class;
		}

		/**
		 * Returns boolean if can edit a cell
		 **/
		public boolean isCellEditable(Object node, int column)
		{
			return column == BONUS_COL_NAME;
		}

		/**
		 * Returns Object value of the column
		 **/
		public Object getValueAt(Object node, int column)
		{
			final myPONode fn = (myPONode) node;
			TempWrap tbWrap = null;
			PObject aTarget = null;
			PlayerCharacter bPC = null;
			String nameString = "";
			String aType = "";
			String aTo = "";
			String aVal = "";

			if (fn == null)
			{
				Logging.errorPrint("Somehow we have no active node when doing getValueAt in AppliedModel");
				return null;
			}

			if (fn.getItem() instanceof TempWrap)
			{
				tbWrap = (TempWrap) fn.getItem();
				BonusObj aBonus = tbWrap.getBonus();
				Object anObj = tbWrap.getTarget();
				aType = aBonus.getTypeOfBonus();
				aTo = aBonus.getBonusInfo();
				aVal = aPC.getVariableValue(aBonus.getValue(), "").toString();
				if (anObj instanceof Equipment)
				{
					aTarget = (PObject) anObj;
				}
				else if (anObj instanceof PlayerCharacter)
				{
					bPC = (PlayerCharacter) anObj;
				}
			}
			else if (fn.getItem() instanceof String)
			{
				nameString = fn.toString();
			}

			switch (column)
			{
				case BONUS_COL_NAME:
					if (tbWrap != null)
					{
						return tbWrap;
					}
					else if (nameString.length() > 0)
					{
						return nameString;
					}
					return null;
				case BONUS_COL_TYPE:
					return aType;
				case BONUS_COL_TO:
					return aTo;
				case BONUS_COL_VAL:
					return aVal;
				default:
					if (fn != null)
					{
						return fn.toString();
					}
					else
					{
						Logging.errorPrint("Somehow we have no active node when doing getValueAt in AppliedModel");
						return null;
					}
			}
		}
	}

	/**
	 * This is an extend of PObjectNode so I can overload toString()
	 **/
	private final class myPONode extends PObjectNode
	{
		private myPONode()
		{
		}

		private myPONode(Object anItem)
		{
			super(anItem);
		}

		public String toString()
		{
			Object item = super.getItem();
			if (item == null)
			{
				return "";
			}
			if (item instanceof String)
			{
				return (String) item;
			}
			else if (item instanceof TempWrap)
			{
				return "--";
			}
			else
			{
				return super.toString();
			}
		}
	}

	/**
	 * implementation of Filterable interface
	 **/
	public final void initializeFilters()
	{
		FilterFactory.registerAllSourceFilters(this);
	}

	/**
	 * implementation of Filterable interface
	 **/
	public final void refreshFiltering()
	{
		updateBonusModel();
		updateTargetModel();
	}

	/**
	 * specifies whether the "match any" option should be allowed
	 **/
	public final boolean isMatchAnyEnabled()
	{
		return true;
	}

	/**
	 * specifies whether the "negate/reverse" option should be allowed
	 **/
	public final boolean isNegateEnabled()
	{
		return true;
	}

	/**
	 * specifies the filter selection mode
	 **/
	public final int getSelectionMode()
	{
		return FilterConstants.DISABLED_MODE;
	}
}
