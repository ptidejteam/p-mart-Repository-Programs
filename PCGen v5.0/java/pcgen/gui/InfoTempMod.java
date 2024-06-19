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
 * Last Edited: $Date: 2006/02/21 01:07:48 $
 *
 */

package pcgen.gui;

import java.awt.BorderLayout;
import java.awt.Component;
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
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
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
import pcgen.core.PObject;
import pcgen.core.PlayerCharacter;
import pcgen.core.SettingsHandler;
import pcgen.core.bonus.Bonus;
import pcgen.core.bonus.BonusObj;
import pcgen.core.spell.Spell;
import pcgen.gui.filter.FilterAdapterPanel;
import pcgen.gui.filter.FilterFactory;
import pcgen.gui.panes.FlippingSplitPane;

/**
 * <code>InfoTempMod</code> creates a new tabbed panel that is used to
 * allow application of temporary modifiers to PC's and Equipment
 *
 * @author  Jayme Cox <jaymecox@users.sourceforge.net>
 * @version $Revision: 1.1 $
 **/

class InfoTempMod extends FilterAdapterPanel
{

	private TempModel bonusModel = null;
	private TempModel targetModel = null;
	private JTreeTable bonusTable;         // bonus
	private JTreeTable targetTable;        // targets for bonus
	private JTreeTableSorter bonusSort = null;
	private JTreeTableSorter targetSort = null;

	private JTreeTable tempTable;         // target+bonuses
	private BonusModel tempBonusModel;

	private static ArrayList equipSetList = new ArrayList();
	private static ArrayList tempSetList = new ArrayList();

	private JLabelPane infoLabel = new JLabelPane();
	private final JLabel bonusLabel = new JLabel("Sort By");
	private final JLabel targetLabel = new JLabel("Sort By");

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

	private JComboBox viewBonusBox = new JComboBox();
	private JComboBox viewTargetBox = new JComboBox();

	private int viewBonusMode = 0;
	private int viewTargetMode = 0;

	// table model modes
	private static final int MODEL_BONUS = 0;
	private static final int MODEL_TARGET = 1;

	//column positions for tables
	// if you change these, you also need to change
	// the selNameList array in the TempModel class
	private static final int COL_NAME = 0;
	private static final int COL_TYPE = 1;
	private static final int COL_SRC = 2;

	//column position for temporary bonus table
	// if you change these, you need to change
	// the colNameList array in the BonusModel class
	private static final int BONUS_COL_NAME = 0;
	private static final int BONUS_COL_TYPE = 1;
	private static final int BONUS_COL_TO = 2;
	private static final int BONUS_COL_VAL = 3;

	/**
	 * Temp Bonus wrapper for the tempTable
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

		public final Object getCreator()
		{
			return _creator;
		}

		public final Object getTarget()
		{
			return _target;
		}

		public BonusObj getBonus()
		{
			return _bonus;
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
			return Utility.createMenuItem(label, new AddBonusActionListener(), "apply bonus", (char) 0, accelerator, "Apply Bonus", "Add16.gif", true);
		}

		private JMenuItem createRemoveMenuItem(String label, String accelerator)
		{
			return Utility.createMenuItem(label, new RemoveActionListener(), "Remove Bonus", (char) 0, accelerator, "remove this bonus", "", true);
		}

		private JMenuItem createRefreshMenuItem(String label)
		{
			return Utility.createMenuItem(label, new RefreshActionListener(), "Redraw/Recalc Panel", (char) 0, null, "Redraw/Recalc this panels info", "", true);
		}

		BonusPopupMenu(JTreeTable treeTable)
		{
			if (treeTable == bonusTable)
			{
				BonusPopupMenu.this.add(createAddMenuItem("Apply Bonus", "control EQUALS"));
				BonusPopupMenu.this.addSeparator();
				BonusPopupMenu.this.add(createRefreshMenuItem("Redraw/recalc Panel"));
			}
			else if (treeTable == targetTable)
			{
				BonusPopupMenu.this.add(createRefreshMenuItem("Redraw/recalc Panel"));

			}
			else if (treeTable == tempTable)
			{
				BonusPopupMenu.this.add(createRemoveMenuItem("Remove Bonus", "control MINUS"));
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
				TreePath selPath = tree.getPathForLocation(evt.getX(), evt.getY());
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
		// do not remove this as we will use the component's name
		// to save component specific settings
		setName("TempMod");

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
		//
		// View List Sanity check
		//

		/*
		int iView = SettingsHandler.getEquipTab_BonusListMode();
		if ((iView >= GuiConstants.INFOTEMPMOD_VIEW_TYPE) && (iView <= GuiConstants.INFOTEMPMOD_VIEW_NAME))
		{
			viewBonusMode = iView;
		}
		SettingsHandler.setEquipTab_BonusListMode(viewBonusMode);
		iView = SettingsHandler.getEquipTab_TargetListMode();
		if ((iView >= GuiConstants.INFOTEMPMOD_VIEW_TYPE) && (iView <= GuiConstants.INFOTEMPMOD_VIEW_LOCATION))
		{
			viewTargetMod = iView;
		}
		SettingsHandler.setEquipTab_TargetListMode(viewTargetMod);
		*/

		// make sure the current PC is set
		aPC = Globals.getCurrentPC();

		viewBonusBox.addItem("Type  ");
		viewBonusBox.addItem("Name  ");
		Utility.setDescription(viewBonusBox, "Blah Blah");
		viewBonusBox.setSelectedIndex(viewBonusMode);

		viewTargetBox.addItem("Type  ");
		viewTargetBox.addItem("Name  ");
		viewTargetBox.setSelectedIndex(viewTargetMode);

		// flesh out all the tree views
		createModels();

		// create tables associated with the above trees
		createTreeTables();

		// build topPane which will contain leftPane and rightPane
		// leftPane will have two panels and a scrollregion
		// rightPane will have one panel and a scrollregion

		topPane.setLayout(new BorderLayout());

		GridBagLayout gridbag = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		JPanel leftPane = new JPanel();
		JPanel rightPane = new JPanel();
		leftPane.setLayout(gridbag);
		rightPane.setLayout(gridbag);
		topVertSplit = new FlippingSplitPane(FlippingSplitPane.HORIZONTAL_SPLIT, leftPane, rightPane);
		topVertSplit.setOneTouchExpandable(true);
		topVertSplit.setDividerSize(10);

		topPane.add(topVertSplit, BorderLayout.CENTER);

		// build the left pane
		// for the availabe bonus table

		Utility.buildConstraints(c, 0, 0, 1, 1, 0, 0);
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.NORTH;
		JPanel aPanel = new JPanel();
		gridbag.setConstraints(aPanel, c);
		//aPanel.setBorder(BorderFactory.createEtchedBorder());
		aPanel.add(bonusLabel);
		aPanel.add(viewBonusBox);
		ImageIcon newImage;
		newImage = new ImageIcon(getClass().getResource("resource/Forward16.gif"));
		applyBonusButton = new JButton(newImage);
		Utility.setDescription(applyBonusButton, "Click to add bonus to selected item");
		applyBonusButton.setEnabled(false);
		aPanel.add(applyBonusButton);

		Utility.setDescription(aPanel, "Right click to add bonus to selected item");
		leftPane.add(aPanel);


		// the bonus panel
		Utility.buildConstraints(c, 0, 2, 1, 1, 10, 10);
		c.fill = GridBagConstraints.BOTH;
		c.anchor = GridBagConstraints.NORTH;
		c.ipadx = 1;
		JScrollPane scrollPane = new JScrollPane(bonusTable);
		gridbag.setConstraints(scrollPane, c);
		leftPane.add(scrollPane);

		// now build the right pane
		// for the target table

		gridbag = new GridBagLayout();
		c = new GridBagConstraints();
		rightPane.setLayout(gridbag);

		Utility.buildConstraints(c, 0, 0, 1, 1, 0, 0);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.NORTH;
		aPanel = new JPanel();
		gridbag.setConstraints(aPanel, c);
		//aPanel.setBorder(BorderFactory.createEtchedBorder());
		aPanel.add(targetLabel);
		aPanel.add(viewTargetBox);

		rightPane.add(aPanel);

		Utility.buildConstraints(c, 0, 1, 1, 1, 10, 10);
		c.fill = GridBagConstraints.BOTH;
		c.anchor = GridBagConstraints.NORTH;
		c.ipadx = 1;
		scrollPane = new JScrollPane(targetTable);
		gridbag.setConstraints(scrollPane, c);
		//scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		targetTable.setShowHorizontalLines(true);
		rightPane.add(scrollPane);

		//bonusTable.setColAlign(COL_TYPE, SwingConstants.RIGHT);
		//bonusTable.setColAlign(COL_QTY, SwingConstants.CENTER);
		//targetTable.setColAlign(COL_TYPE, SwingConstants.RIGHT);
		//targetTable.setColAlign(COL_QTY, SwingConstants.CENTER);


		// ---------- build Bottom Panel ----------------
		// botPane will contain a bHeadPane and a bTailPane
		// bHeadPane will contain a scrollregion (equipment info)
		// bTailPane will contain a scrollregion (character Info)

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
		// create a template select and view panel
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
		JScrollPane tbPane = new JScrollPane(tempTable);
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
		viewBonusBox.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				viewBonusBoxActionPerformed();
			}
		});
		viewTargetBox.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				viewTargetBoxActionPerformed();
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

		if (aFeat != null)
		{
			StringBuffer b = new StringBuffer(300);
			b.append("<html><font size=+1><b>").append(aFeat.piSubString()).append("</b></font>");
			b.append(" &nbsp;<b>Type:</b>&nbsp; ").append(aFeat.getType());
			String bString = aFeat.getSource();
			if (bString.length() > 0)
			{
				b.append(" &nbsp;<b>SOURCE:</b>&nbsp;").append(bString);
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

					final Object temp = avaPath.getLastPathComponent();
					if (temp == null)
					{
						infoLabel.setText();
						lastAvaObject = null;
						return;
					}

					PObjectNode fNode = (PObjectNode) temp;
					if ((fNode.getItem() != null) && !(fNode.getItem() instanceof String))
					{
						applyBonusButton.setEnabled(true);
						setInfoLabelText(fNode.getItem());
						lastAvaObject = (PObject) fNode.getItem();
						updateTargetModel();
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
		tempTable = new JTreeTable(tempBonusModel);
		final JTree btree = tempTable.getTree();
		btree.setRootVisible(false);
		btree.setShowsRootHandles(true);
		btree.setCellRenderer(new LabelTreeCellRenderer());
		tempTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		tempTable.setShowHorizontalLines(true);

		tempTable.getSelectionModel().addListSelectionListener(new ListSelectionListener()
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
					if ((fNode.getItem() != null) && !(fNode.getItem() instanceof String))
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
				//final int aRow = tempTable.rowAtPoint(new Point(e.getX(), e.getY()));
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
		hookupPopupMenu(tempTable);
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
	 * Changed the view Sort for bonuses
	 **/
	private void viewBonusBoxActionPerformed()
	{
		final int index = viewBonusBox.getSelectedIndex();
		if (index != viewBonusMode)
		{
			viewBonusMode = index;
			//SettingsHandler.setTempModTab_BonusListMode(viewBonusMode);
			updateBonusModel();
		}
	}

	/**
	 * Changed the view Sort for target Equipment
	 **/
	private void viewTargetBoxActionPerformed()
	{
		final int index = viewTargetBox.getSelectedIndex();
		if (index != viewTargetMode)
		{
			viewTargetMode = index;
			//SettingsHandler.setTempModTab_TargetListMode(viewTargetMode);
			updateTargetModel();
		}
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
	 * upon the currently target character.
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
		updateTempModel();

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
			JOptionPane.showMessageDialog(null, "First select a type of bonus use", Constants.s_APPNAME, JOptionPane.ERROR_MESSAGE);
			return;
		}
		if (targetTable.getTree().isSelectionEmpty())
		{
			JOptionPane.showMessageDialog(null, "First select an item to set the temporary bonus on", Constants.s_APPNAME, JOptionPane.ERROR_MESSAGE);
			return;
		}

		TreePath bonusPath = bonusTable.getTree().getSelectionPath();
		TreePath targetPath = targetTable.getTree().getSelectionPath();
		Object anObj = null;
		Object aTarget = null;
		PObject aMod = null;

		Object endComp = targetPath.getLastPathComponent();
		PObjectNode fNode = (PObjectNode) endComp;
		if (fNode.getItem() != null)
		{
			aTarget = fNode.getItem();
		}

		if (aTarget == null || fNode == null)
		{
			JOptionPane.showMessageDialog(null, "First select an item to set the temporary bonus on", Constants.s_APPNAME, JOptionPane.ERROR_MESSAGE);
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
			JOptionPane.showMessageDialog(null, "First select a type of bonus use", Constants.s_APPNAME, JOptionPane.ERROR_MESSAGE);
			return;
		}

		if (anObj instanceof PObject)
		{
			aMod = (PObject) anObj;
		}
		else if (anObj instanceof PCClass)
		{
			aMod = (PCClass) anObj;
		}

		BonusObj newB = null;
		Equipment aEq = null;

		if (aTarget instanceof Equipment)
		{
			// Create new Item
			Equipment eq = (Equipment) aTarget;
			aEq = (Equipment) eq.clone();
			aEq.setWeight("0");
			aEq.setAppliedName(aMod.getName());
			aEq.resetTempBonusList();
		}

		// get the bonus string
		for (Iterator e = aMod.getBonusList().iterator(); e.hasNext();)
		{
			String aString = (String) e.next();

			if (aString.indexOf("PREAPPLY:") >= 0)
			{
				if (aMod instanceof PCClass)
				{
					int idx = aString.indexOf('|');
					newB = Bonus.newBonus(aString.substring(idx + 1));
				}
				else
				{
					newB = Bonus.newBonus(aString);
				}
				if (newB != null)
				{
					Globals.errorPrint("adding bonus:" + newB.toString());
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
				}
			}
		}

		if ((newB != null) && (aMod.getChoiceString().length() > 0))
		{
			String aChoice = aMod.getChoiceString();
			newB.setChoiceString(aChoice);
			StringTokenizer aTok = new StringTokenizer(aChoice, "|", false);
			if (aChoice.startsWith("NUMBER") && (aTok.countTokens() >= 3))
			{
				int min = 0;
				int max = 0;
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
					ArrayList numberList = new ArrayList();
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
						newB.setValue(aI);
					}
					else
					{
						// the hit the cancel button
						newB.setValue("0");
					}
				}
			}
		}

		// if the Target is an Equipment item
		// then add it to the tempBonusItemList
		if (aEq != null)
		{
			aPC.addTempBonusItemList(aEq);
		}

		updateTempModel();

		aPC.setDirty(true);

		// now Update all the other tabs
		PCGen_Frame1.forceUpdate_InfoAbilities();
		PCGen_Frame1.forceUpdate_InfoClasses();
		PCGen_Frame1.forceUpdate_InfoFeats();
		PCGen_Frame1.forceUpdate_InfoSkills();
		PCGen_Frame1.forceUpdate_InfoSpells();
		PCGen_Frame1.forceUpdate_InfoSummary();
	}

	/**
	 * removes a bonus, target pair from the tempTable
	 **/
	private void removeBonusButton()
	{
		if (tempTable.getTree().isSelectionEmpty())
		{
			JOptionPane.showMessageDialog(null, "First select a bonus to remove", Constants.s_APPNAME, JOptionPane.ERROR_MESSAGE);
			return;
		}

		TreePath bonusPath = tempTable.getTree().getSelectionPath();

		TempWrap tbWrap = null;
		Object aCreator = null;
		Object aTarget = null;
		PObject aMod = null;

		Object endComp = bonusPath.getLastPathComponent();
		myPONode fNode = (myPONode) endComp;
		if (fNode == null)
		{
			Globals.errorPrint("fNode == null");
			return;
		}
		if ((fNode.getItem() != null) && (fNode.getItem() instanceof TempWrap))
		{
			tbWrap = (TempWrap) fNode.getItem();
		}
		if (tbWrap != null)
		{
			aCreator = tbWrap.getCreator();
			aTarget = tbWrap.getTarget();
		}

		if (aTarget == null || aCreator == null)
		{
			Globals.errorPrint("Target or Creator == null");
			return;
		}

		if (aCreator instanceof PObject)
		{
			aMod = (PObject) aCreator;
		}
		else if (aCreator instanceof PCClass)
		{
			aMod = (PCClass) aCreator;
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

		ArrayList tbList = new ArrayList(aPC.getTempBonusList());
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

		updateTempModel();
		aPC.setDirty(true);

		// now Update all the other tabs
		PCGen_Frame1.forceUpdate_InfoAbilities();
		PCGen_Frame1.forceUpdate_InfoClasses();
		PCGen_Frame1.forceUpdate_InfoFeats();
		PCGen_Frame1.forceUpdate_InfoSkills();
		PCGen_Frame1.forceUpdate_InfoSpells();
		PCGen_Frame1.forceUpdate_InfoSummary();

		return;
	}

	/**
	 * Checks to see if aType is in aList
	 **/
	private boolean checkPreApplyType(String aList, Equipment anEq)
	{
		// PREAPPLY target could be a Player or Equipment types
		//
		// PREAPPLY:Thrown|Melee,Ranged
		//  -> applied to Ranged Thrown or Ranged Melee weapons
		//  -> so , means AND
		//  -> so | mean OR
		// PREAPPLY:PC
		//  -> applied to PlayerCharacter object
		//
		boolean flag = true;

		final StringTokenizer aTok = new StringTokenizer(aList, ",|", true);
		int iLogicType = 0; // AND

		while (aTok.hasMoreTokens())
		{
			String aString = aTok.nextToken();
			if (",".equals(aString))
			{
				iLogicType = 0;
			}
			else if ("|".equals(aString))
			{
				iLogicType = 1;
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

				if (iLogicType == 0)
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
		createTempModel();
	}

	private final void createBonusModel()
	{
		if (bonusModel == null)
		{
			bonusModel = new TempModel(viewBonusMode, MODEL_BONUS);
		}
		else
		{
			bonusModel.resetModel(viewBonusMode, MODEL_BONUS);
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
			targetModel = new TempModel(viewTargetMode, MODEL_TARGET);
		}
		else
		{
			targetModel.resetModel(viewTargetMode, MODEL_TARGET);
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
	 * This root node has a null <code>parent</code>.
	 * All other nodes have a parent which points to a non-null node.
	 * Parent nodes contain a list of  <code>children</code>, which
	 * are all the nodes that point to it as their parent.
	 * <code>nodes</code> which have 0 children are leafs (the end of
	 * that linked list).  nodes which have at least 1 child are not leafs
	 * Leafs are like files and non-leafs are like directories.
	 * The leafs contain an Object that we want to know about (Equipment)
	 **/
	private final class TempModel extends AbstractTreeTableModel
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
		 * Creates a TempModel
		 **/
		private TempModel(int mode, int iModel)
		{
			super(null);

			//
			// if you change/add/remove entries to nameList
			// you also need to change the static COL_XXX defines
			// at the begining of this file
			//
			avaNameList = new String[]{"Name", "Type", "Source"};
			selNameList = new String[]{"Name", "Type", "Source"};

			modelType = iModel;
			resetModel(mode, iModel);
		}

		/**
		 * This assumes the TempModel exists but
		 * needs branches and nodes to be repopulated
		 **/
		private void resetModel(int mode, int modelType)
		{
			// This is the array of all equipment types
			ArrayList eqTypeList = new ArrayList();
			ArrayList typeList = new ArrayList();

			// build the list of all equipment types
			eqTypeList.add(Constants.s_CUSTOM);
			for (Iterator iSet = aPC.getEquipmentList().iterator(); iSet.hasNext();)
			{
				final Equipment bEq = (Equipment) iSet.next();
				final StringTokenizer aTok = new StringTokenizer(bEq.getType(), ".", false);
				String aString = "";
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

			//
			// build bonusTable (list of all equipment)
			//
			if (modelType == MODEL_BONUS)
			{
				// this is the root node
				avaRoot = new PObjectNode();

				switch (mode)
				{
					// by Type
					//case GuiConstants.INFOTEMPMOD_VIEW_TYPE:
					case 0:
						setRoot(avaRoot);

						// build the Type root nodes
						PObjectNode eq[] = new PObjectNode[5];
						eq[0] = new PObjectNode("Feat");
						eq[1] = new PObjectNode("Spell");
						eq[2] = new PObjectNode("Item");
						eq[3] = new PObjectNode("Class");
						eq[4] = new PObjectNode("Templates");
						// first do feats
						for (Iterator fI = aPC.getFeatList().iterator(); fI.hasNext();)
						{
							Feat aFeat = (Feat) fI.next();
							for (Iterator e = aFeat.getBonusList().iterator(); e.hasNext();)
							{
								final String aString = (String) e.next();
								if (aString.indexOf("PREAPPLY:") > 0)
								{
									PObjectNode aFN = new PObjectNode(aFeat);
									aFN.setParent(eq[0]);
									eq[0].addChild(aFN, true);
									eq[0].setParent(avaRoot);
								}
							}
						}
						//
						// now do all spells
						//
						for (Iterator fI = aPC.aggregateSpellList("Any", "", "", 0, 9).iterator(); fI.hasNext();)
						{
							final Spell aSpell = (Spell) fI.next();
							for (Iterator e = aSpell.getBonusList().iterator(); e.hasNext();)
							{
								final String aString = (String) e.next();
								if (aString.indexOf("PREAPPLY:") > 0)
								{
									PObjectNode aFN = new PObjectNode(aSpell);
									aFN.setParent(eq[1]);
									eq[1].addChild(aFN, true);
									eq[1].setParent(avaRoot);
								}
							}
						}
						/*
						for (Iterator iClass = aPC.getClassList().iterator(); iClass.hasNext();)
						{
							final PCClass aClass = (PCClass) iClass.next();
							final ArrayList bList = aClass.getCharacterSpell(null, Globals.getDefaultSpellBook(), -1);
							for (Iterator bi = bList.iterator(); bi.hasNext();)
							{
								final CharacterSpell cs = (CharacterSpell) bi.next();
								for (Iterator fI = cs.getBonusList().iterator(); fI.hasNext();)
								{
								}
							}
						}
						*/
						if (!eq[1].isLeaf())
						{
							eq[1].setParent(avaRoot);
						}
						//
						// iterate thru all PC's items
						// and fill out the tree
						//
						for (Iterator fI = aPC.getEquipmentList().iterator(); fI.hasNext();)
						{
							final Equipment aEq = (Equipment) fI.next();
							for (Iterator e = aEq.getBonusList().iterator(); e.hasNext();)
							{
								final String aString = (String) e.next();
								if (aString.indexOf("PREAPPLY:") > 0)
								{
									PObjectNode aFN = new PObjectNode(aEq);
									aFN.setParent(eq[2]);
									eq[2].addChild(aFN, true);
									eq[2].setParent(avaRoot);
								}
							}
						}
						if (!eq[2].isLeaf())
						{
							eq[2].setParent(avaRoot);
						}
						//
						// iterate thru all PC's Classes
						// and fill out the tree
						//
						for (Iterator fI = aPC.getClassList().iterator(); fI.hasNext();)
						{
							final PCClass aClass = (PCClass) fI.next();
							for (Iterator e = aClass.getBonusList().iterator(); e.hasNext();)
							{
								final String aString = (String) e.next();
								final int myLevel = aClass.getLevel();
								final int idx = aString.indexOf('|');
								final int level = Integer.parseInt(aString.substring(0, idx));
								if ((aString.indexOf("PREAPPLY:") > 0) && (myLevel >= level))
								{
									PObjectNode aFN = new PObjectNode(aClass);
									aFN.setParent(eq[3]);
									eq[3].addChild(aFN, true);
									eq[3].setParent(avaRoot);
								}
							}
						}
						if (!eq[3].isLeaf())
						{
							eq[3].setParent(avaRoot);
						}

						// now add to the root node
						avaRoot.setChildren(eq, true);

						break; // end VIEW_TYPE

						// just by name
						//case GuiConstants.INFOTEMPMOD_VIEW_NAME:
					case 1:
						setRoot(avaRoot);

						break; // end VIEW_NAME

					default:
						Globals.errorPrint("In InfoTempMod.resetModel the mode " + mode + " is not supported.");
						break;

				} // end of switch(mode)
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
					String aString = (String) e.next();
					if (aString.indexOf("PREAPPLY:") < 0)
					{
						continue;
					}
					StringTokenizer aTok = new StringTokenizer(aString, "|", false);
					if (aTok.countTokens() < 4)
					{
						continue;
					}
					aString = aTok.nextToken();
					aString = aTok.nextToken();
					aString = aTok.nextToken();
					aString = aTok.nextToken();
					if (lastAvaObject instanceof PCClass)
					{
						aString = aTok.nextToken();
					}
					final String aType = aString.substring(aString.indexOf("PREAPPLY:") + 9);
					if (aType.equals("PC") && !found)
					{
						PObjectNode aFN = new PObjectNode(aPC);
						aFN.setParent(selRoot);
						selRoot.addChild(aFN, true);
						found = true;
					}
				}
				for (Iterator fI = aPC.getEquipmentList().iterator(); fI.hasNext();)
				{
					final Equipment aEq = (Equipment) fI.next();
					found = false;
					for (Iterator e = lastAvaObject.getBonusList().iterator(); e.hasNext();)
					{
						String aString = (String) e.next();
						if (aString.indexOf("PREAPPLY:") < 0)
						{
							continue;
						}
						StringTokenizer aTok = new StringTokenizer(aString, "|", false);
						if (aTok.countTokens() < 4)
						{
							continue;
						}
						aString = aTok.nextToken();
						aString = aTok.nextToken();
						aString = aTok.nextToken();
						aString = aTok.nextToken();
						if (lastAvaObject instanceof PCClass)
						{
							aString = aTok.nextToken();
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
				setRoot(selRoot);

			} // end if else

			PObjectNode rootAsPObjectNode = (PObjectNode) root;
			if ((rootAsPObjectNode != null) && (rootAsPObjectNode.getChildCount() > 0))
			{
				fireTreeNodesChanged(root, new TreePath(root));
			}

		}

		// There must be a root node, but we keep it hidden
		private void setRoot(PObjectNode aNode)
		{
			root = aNode;
		}

		// return the root node
		public final Object getRoot()
		{
			return (PObjectNode) root;
		}

		/* The JTreeTableNode interface. */

		/**
		 * Returns int number of columns. (TempModel)
		 **/
		public int getColumnCount()
		{
			return modelType == MODEL_BONUS ? avaNameList.length : selNameList.length;
		}

		/**
		 * Returns String name of a column. (TempModel)
		 **/
		public String getColumnName(int column)
		{
			return modelType == MODEL_BONUS ? avaNameList[column] : selNameList[column];
		}

		/**
		 * Returns Class for the column. (TempModel)
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
					Globals.errorPrint("In InfoTempMod.getColumnClass the column " + column + " is not supported.");
					break;
			}
			return String.class;
		}

		/**
		 * Returns boolean if can edit a cell. (TempModel)
		 **/
		public boolean isCellEditable(Object node, int column)
		{
			return column == COL_NAME;
		}

		/**
		 * Returns Object value of the column. (TempModel)
		 **/
		public Object getValueAt(Object node, int column)
		{
			final PObjectNode fn = (PObjectNode) node;
			Feat aFeat = null;
			Spell aSpell = null;
			Equipment eqI = null;
			PCClass aClass = null;
			PlayerCharacter bPC = null;

			if (fn == null)
			{
				Globals.errorPrint("Somehow we have no active node when doing getValueAt in InfoTempMod.");
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
			else if (fn.getItem() instanceof PCClass)
			{
				aClass = (PCClass) fn.getItem();
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
						return "Class Bonus";
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
					return eqI != null ? eqI.getSource() : null;
				default:
					if (fn != null)
					{
						return fn.toString();
					}
					else
					{
						Globals.errorPrint("Somehow we have no active node when doing getValueAt in InfoEquip.");
						return null;
					}

			}
			// return null;
		}
	}

	/**
	 * Updates the Temp Bonus table
	 **/
	private void updateTempModel()
	{
		List pathList = tempTable.getExpandedPaths();
		createTempModel();
		tempTable.updateUI();
		tempTable.expandPathList(pathList);
	}

	/**
	 * Creates the Temp BonusModel
	 **/
	private void createTempModel()
	{
		if (tempBonusModel == null)
		{
			tempBonusModel = new BonusModel();
		}
		else
		{
			tempBonusModel.resetModel();
		}
	}

	private final class BonusModel extends AbstractTreeTableModel
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
		 * Creates a BonusModel
		 **/
		private BonusModel()
		{
			super(null);
			resetModel();
		}

		/**
		 * This assumes the BonusModel exists but
		 * needs branches and nodes to be repopulated
		 **/
		private void resetModel()
		{
			// this is the root node
			bonusRoot = new myPONode();

			// an array of TempWrap'ers
			ArrayList tList = new ArrayList();
			ArrayList sList = new ArrayList();

			// iterate thru all PC's bonuses
			// and build an Array of TempWrap'ers
			for (Iterator fI = aPC.getTempBonusList().iterator(); fI.hasNext();)
			{
				final BonusObj aBonus = (BonusObj) fI.next();
				final Object aC = aBonus.getCreatorObject();
				final Object aT = aBonus.getTargetObject();
				TempWrap tw = new TempWrap(aC, aT, aBonus);
				tList.add(tw);

				String sString = tw.getName();
				if (!sList.contains(sString))
				{
					sList.add(sString);
				}
			}

			// build the tree structure
			myPONode cc[] = new myPONode[sList.size()];
			for (int i = 0; i < sList.size(); i++)
			{
				String hString = (String) sList.get(i);
				cc[i] = new myPONode();
				cc[i].setItem(hString);

				for (int j = 0; j < tList.size(); j++)
				{
					TempWrap tw = (TempWrap) tList.get(j);
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
			bonusRoot.setChildren(cc, true);

			setRoot(bonusRoot);

			myPONode rootAsPObjectNode = (myPONode) root;
			if ((rootAsPObjectNode != null) && (rootAsPObjectNode.getChildCount() > 0))
			{
				fireTreeNodesChanged(root, new TreePath(root));
			}

		}

		// There must be a root node, but we keep it hidden
		private void setRoot(myPONode aNode)
		{
			root = aNode;
		}

		// return the root node
		public final Object getRoot()
		{
			return (myPONode) root;
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
			PCClass aClass = null;
			PlayerCharacter bPC = null;
			String nameString = "";
			String aType = "";
			String aTo = "";
			String aVal = "";

			if (fn == null)
			{
				Globals.errorPrint("Somehow we have no active node when doing getValueAt in BonusModel");
				return null;
			}

			if (fn.getItem() instanceof TempWrap)
			{
				tbWrap = (TempWrap) fn.getItem();
				BonusObj aBonus = tbWrap.getBonus();
				PObject aCreator = (PObject) tbWrap.getCreator();
				Object anObj = tbWrap.getTarget();
				StringTokenizer aTok = new StringTokenizer(aBonus.toString(), "|", false);
				aType = aTok.nextToken();
				aTo = aTok.nextToken();
				aVal = aTok.nextToken();

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
						Globals.errorPrint("Somehow we have no active node when doing getValueAt in BonusModel");
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
			final Object item = super.getItem();
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
		return DISABLED_MODE;
	}
}
