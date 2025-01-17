/*
 * InfoSpells.java
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
 * Written by Bryan McRoberts <merton_monk@users.sourceforge.net>,
 * Re-written by Jayme Cox <jaymecox@netscape.net>
 * Created on April 21, 2001, 2:15 PM
 * Re-created on April 1st, 2002, 2:15 am
 *
 * Current Ver: $Revision: 1.1 $
 * Last Editor: $Author: vauchers $
 * Last Edited: $Date: 2006/02/21 00:47:12 $
 *
 */

package pcgen.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;
import javax.swing.tree.TreePath;
import pcgen.core.*;
import pcgen.core.character.CharacterSpell;
import pcgen.core.character.SpellInfo;
import pcgen.core.spell.Spell;
import pcgen.gui.filter.FilterAdapterPanel;
import pcgen.gui.filter.FilterFactory;
import pcgen.gui.panes.FlippingSplitPane;

/**
 *  <code>InfoSpells</code> creates a new tabbed panel.
 *
 * @author     Bryan McRoberts <merton_monk@users.sourceforge.net>, Jayme Cox <jaymecox@netscape.net>
 * created    den 11 maj 2001
 * @version    $Revision: 1.1 $
 */
class InfoSpells extends FilterAdapterPanel
{

	protected SpellModel availableModel = null;  // Model for JTreeTable
	protected SpellModel selectedModel = null;   // Model for JTreeTable
	protected JTreeTable availableTable;         // available Spells
	protected JTreeTable selectedTable;	     // spellbook Spells
	private JTreeTableSorter availableSort = null;
	private JTreeTableSorter selectedSort = null;
	private static List bookList = new ArrayList();

	private JLabelPane infoLabel = new JLabelPane();
	private JLabelPane classLabel = new JLabelPane();
	private final JLabel avaLabel = new JLabel("Sort Spells");
	private final JLabel selLabel = new JLabel("Sort SpellBooks");

	private JCheckBox shouldAutoSpells = new JCheckBox("Add auto known spells on level/load:");
	private JTextField spellBookNameText = new JTextField();
	static String currSpellBook = Globals.getDefaultSpellBook();

	private MMFrame mmFrame = null;

	protected SpellFeatModel featModel = new SpellFeatModel();
	private JTableEx featTable = null;

	private JButton addSpellButton;
	private JButton delSpellButton;
	private JButton addBookButton;
	private JButton delBookButton;
	private JPanel topPane = new JPanel();
	private JPanel botPane = new JPanel();
	private Border etched;
	private TitledBorder titled;
	private FlippingSplitPane splitPane;
	private FlippingSplitPane bsplit;
	private FlippingSplitPane asplit;

	protected static PlayerCharacter aPC = null;

	private TreePath selPath;
	private int selRow;
	private Spell lastSpell = null;
	private String lastClass = "";

	private boolean needsUpdate = true;
	private boolean hasBeenSized = false;

	protected JComboBox viewComboBox = new JComboBox();
	protected JComboBox viewSelectComboBox = new JComboBox();
	private static int splitOrientation = FlippingSplitPane.HORIZONTAL_SPLIT;

	static int viewMode = 0;
	static int viewSelectMode = 0;

	// table model modes
	private static final int MODEL_AVAIL = 0;
	private static final int MODEL_SELECTED = 1;

	//column positions for tables
	// if you change these, you also need to change
	// the selNameList array in the SpellModel class
	private static final int COL_NAME = 0;
	private static final int COL_SCHOOL = 1;
	private static final int COL_SUBSCHOOL = 2;
	private static final int COL_DESCRIPTOR = 3;
	private static final int COL_COMPONENT = 4;
	private static final int COL_CASTTIME = 5;
	private static final int COL_RANGE = 6;
	private static final int COL_EFFECT = 7;
	private static final int COL_EFFECTTYPE = 8;
	private static final int COL_DURATION = 9;
	private static final int COL_SAVE = 10;
	private static final int COL_SR = 11;
	private static final int COL_SRC = 12;
	private static final int COL_THRESHOLD = 13;

	private class SpellPopupMenu extends JPopupMenu
	{
		private class SpellActionListener implements ActionListener
		{
			public void actionPerformed(ActionEvent evt)
			{
			}
		}

		private class AddSpellActionListener extends SpellActionListener
		{
			public void actionPerformed(ActionEvent evt)
			{
				addSpellButton(evt);
			}
		}

		private class DelSpellActionListener extends SpellActionListener
		{
			public void actionPerformed(ActionEvent evt)
			{
				delSpellButton(evt);
			}
		}

		private JMenuItem createAddMenuItem(String label, String accelerator)
		{
			return Utility.createMenuItem(label, new AddSpellActionListener(), "add 1", (char)0, accelerator, "Add Spell to Spellbook", "Add16.gif", true);
		}

		private JMenuItem createDelMenuItem(String label, String accelerator)
		{
			return Utility.createMenuItem(label, new DelSpellActionListener(), "remove 1", (char)0, accelerator, "Remove Spell from Spellbook", "Remove16.gif", true);
		}

		SpellPopupMenu(JTreeTable treeTable)
		{
			if (treeTable == availableTable)
			{
				SpellPopupMenu.this.add(createAddMenuItem("Add  Spell to Spellbook", "control EQUALS"));
				this.addSeparator();

				SpellPopupMenu.this.add(Utility.createMenuItem("Add Spell with Metamagic Feats",
				  new ActionListener()
				  {
					  public void actionPerformed(ActionEvent e)
					  {
						  metamagicButton();
					  }
				  }
				  , "Add 1 with Metamagic", (char)0, "alt C", "Add a spell to your spellbook memorized with Metamagic feats", "Add16.gif", true));
			}
			else // selectedTable
			{
				SpellPopupMenu.this.add(createDelMenuItem("Remove Spell from Spellbook", "control MINUS"));
			}
		}
	}

	private class SpellPopupListener extends MouseAdapter
	{
		private JTree tree;
		private SpellPopupMenu menu;

		SpellPopupListener(JTreeTable treeTable, SpellPopupMenu aMenu)
		{
			tree = treeTable.getTree();
			menu = aMenu;
			KeyListener myKeyListener = new KeyListener()
			{
				public void keyTyped(KeyEvent e)
				{
					dispatchEvent(e);
				}

				//
				// Walk through the list of accelerators to see if the user has
				// pressed a sequence used by the popup.
				// This would not otherwise happen unless the popup was showing
				//
				public void keyPressed(KeyEvent e)
				{
					final int keyCode = e.getKeyCode();
					if (keyCode != KeyEvent.VK_UNDEFINED)
					{
						final KeyStroke keyStroke = KeyStroke.getKeyStrokeForEvent(e);
						for (int i = 0; i < menu.getComponentCount(); i++)
						{
							final Component menuComponent = menu.getComponent(i);
							if (menuComponent instanceof JMenuItem)
							{
								KeyStroke ks = ((JMenuItem)menuComponent).getAccelerator();
								if ((ks != null) && keyStroke.equals(ks))
								{
									selPath = tree.getSelectionPath();
									((JMenuItem)menuComponent).doClick(2);
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
		treeTable.addMouseListener(new SpellPopupListener(treeTable, new SpellPopupMenu(treeTable)));
	}

	/**
	 *  Constructor for the InfoSpells object
	 *
	 */
	public InfoSpells()
	{
		// do not remove this as we will use the component's name
		// to save component specific settings
		setName("Spells");

		initComponents();

		initActionListeners();

		FilterFactory.restoreFilterSettings(this);
	}

	public void setNeedsUpdate(boolean flag)
	{
		needsUpdate = flag;
	}

	public void forceUpdate()
	{
		needsUpdate = true;
		updateCharacterInfo();
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
		// View List Sanity check
		//
		int iView = SettingsHandler.getSpellsTab_AvailableListMode();
		if ((iView >= GuiConstants.INFOSPELLS_VIEW_CLASS) && (iView <= GuiConstants.INFOSPELLS_VIEW_TYPE))
			viewMode = iView;
		SettingsHandler.setSpellsTab_AvailableListMode(viewMode);
		iView = SettingsHandler.getSpellsTab_SelectedListMode();
		if ((iView >= GuiConstants.INFOSPELLS_VIEW_CLASS) && (iView <= GuiConstants.INFOSPELLS_VIEW_TYPE))
			viewSelectMode = iView;
		SettingsHandler.setSpellsTab_SelectedListMode(viewSelectMode);

		// make sure the current PC is set
		aPC = Globals.getCurrentPC();

		viewComboBox.addItem("Class/Level ");
		viewComboBox.addItem("Level/Class ");
		//viewComboBox.addItem("Name        ");
		Utility.setDescription(viewComboBox, "You can change how the Spells in the Tables are listed.");
		viewComboBox.setSelectedIndex(viewMode);
		viewSelectComboBox.addItem("Class/Level ");
		viewSelectComboBox.addItem("Level/Class ");
		Utility.setDescription(viewSelectComboBox, "You can change how the Spells in the Tables are listed.");
		viewSelectComboBox.setSelectedIndex(viewSelectMode);

		bookList.add(Globals.getDefaultSpellBook());

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
		splitPane = new FlippingSplitPane(splitOrientation, leftPane, rightPane);
		splitPane.setOneTouchExpandable(true);
		splitPane.setDividerSize(10);

		topPane.add(splitPane, BorderLayout.CENTER);

		//
		// first build the left pane
		// for the availabe spells table and info
		//

		buildConstraints(c, 0, 0, 1, 1, 0, 0);
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.NORTH;
		JPanel aPanel = new JPanel();
		gridbag.setConstraints(aPanel, c);
		//aPanel.setBorder(BorderFactory.createEtchedBorder());
		aPanel.add(avaLabel);
		aPanel.add(viewComboBox);
		ImageIcon newImage;
		newImage = new ImageIcon(getClass().getResource("resource/Forward16.gif"));
		addSpellButton = new JButton(newImage);
		Utility.setDescription(addSpellButton, "Click to add selected spell to your selected spellbook");
		addSpellButton.setEnabled(false);
		aPanel.add(addSpellButton);

		Utility.setDescription(aPanel, "Right click to add spells to your spellbooks");
		leftPane.add(aPanel);

		buildConstraints(c, 0, 1, 1, 1, 0, 0);
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.NORTH;
		JPanel bPanel = new JPanel();
		gridbag.setConstraints(bPanel, c);
		shouldAutoSpells.setSelected(aPC.getAutoSpells());
		bPanel.add(shouldAutoSpells);
		leftPane.add(bPanel);

		// the available spells panel
		buildConstraints(c, 0, 2, 1, 1, 10, 10);
		c.fill = GridBagConstraints.BOTH;
		c.anchor = GridBagConstraints.NORTH;
		c.ipadx = 1;
		JScrollPane scrollPane = new JScrollPane(availableTable);
		gridbag.setConstraints(scrollPane, c);
		leftPane.add(scrollPane);

		//
		// now build the right pane
		// for the selected (SpellBooks) table
		//

		gridbag = new GridBagLayout();
		c = new GridBagConstraints();
		rightPane.setLayout(gridbag);

		buildConstraints(c, 0, 0, 1, 1, 0, 0);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.NORTH;
		aPanel = new JPanel();
		gridbag.setConstraints(aPanel, c);
		//aPanel.setBorder(BorderFactory.createEtchedBorder());

		JLabel spellBookLabel = new JLabel("SpellBook:");
		aPanel.add(spellBookLabel);
		spellBookNameText.setEditable(true);
		spellBookNameText.setPreferredSize(new Dimension(100, 20));
		aPanel.add(spellBookNameText);
		addBookButton = new JButton("Add");
		//addBookButton.setMinimumSize(new Dimension(60, 22));
		aPanel.add(addBookButton);
		delBookButton = new JButton("Del");
		//delBookButton.setMinimumSize(new Dimension(60, 22));
		aPanel.add(delBookButton);
		rightPane.add(aPanel);

		buildConstraints(c, 0, 1, 1, 1, 0, 0);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.NORTH;
		bPanel = new JPanel();
		gridbag.setConstraints(bPanel, c);
		//bPanel.setBorder(BorderFactory.createEtchedBorder());
		//viewSelectComboBox.setPreferredSize(new Dimension(100, 20));
		bPanel.add(selLabel);
		bPanel.add(viewSelectComboBox);
		newImage = new ImageIcon(getClass().getResource("resource/Back16.gif"));
		delSpellButton = new JButton(newImage);
		Utility.setDescription(delSpellButton, "Click to remove selected spell from this spellbook");
		delSpellButton.setEnabled(false);
		bPanel.add(delSpellButton);
		rightPane.add(bPanel);

		buildConstraints(c, 0, 2, 1, 1, 10, 10);
		c.fill = GridBagConstraints.BOTH;
		c.anchor = GridBagConstraints.NORTH;
		c.ipadx = 1;
		scrollPane = new JScrollPane(selectedTable);
		gridbag.setConstraints(scrollPane, c);
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		selectedTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		selectedTable.setShowHorizontalLines(true);
		rightPane.add(scrollPane);

		//selectedTable.getColumnModel().getColumn(COL_TIMES).setCellRenderer(new AlignCellRenderer(SwingConstants.CENTER));


		// ---------- build Bottom Panel ----------------
		// botPane will contain a bLeftPane and a bRightPane
		// bLeftPane will contain a scrollregion (spell info)
		// bRightPane will contain a scrollregion (character Info)

		botPane.setLayout(new BorderLayout());

		gridbag = new GridBagLayout();
		c = new GridBagConstraints();
		JPanel bLeftPane = new JPanel();
		JPanel bRightPane = new JPanel();
		bLeftPane.setLayout(gridbag);
		bRightPane.setLayout(gridbag);

		asplit = new FlippingSplitPane(FlippingSplitPane.HORIZONTAL_SPLIT, bLeftPane, bRightPane);
		asplit.setOneTouchExpandable(true);
		asplit.setDividerSize(10);

		botPane.add(asplit, BorderLayout.CENTER);

		// create a spell info scroll area
		buildConstraints(c, 0, 0, 1, 1, 1, 1);
		c.fill = GridBagConstraints.BOTH;
		c.anchor = GridBagConstraints.CENTER;
		JScrollPane sScroll = new JScrollPane();
		gridbag.setConstraints(sScroll, c);

		TitledBorder sTitle = BorderFactory.createTitledBorder(etched, "Spell Info");
		sTitle.setTitleJustification(TitledBorder.CENTER);
		sScroll.setBorder(sTitle);
		infoLabel.setBackground(topPane.getBackground());
		sScroll.setViewportView(infoLabel);
		bLeftPane.add(sScroll);

		// create a class info scroll area
		buildConstraints(c, 0, 0, 1, 1, 1, 1);
		c.fill = GridBagConstraints.BOTH;
		c.anchor = GridBagConstraints.EAST;
		JScrollPane iScroll = new JScrollPane();

		TitledBorder iTitle = BorderFactory.createTitledBorder(etched, "Class Info");
		iTitle.setTitleJustification(TitledBorder.CENTER);
		iScroll.setBorder(iTitle);
		classLabel.setBackground(topPane.getBackground());
		iScroll.setViewportView(classLabel);
		//iScroll.setPreferredSize(new Dimension(200,80));
		iScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		gridbag.setConstraints(iScroll, c);
		bRightPane.add(iScroll);


		// now split the top and bottom Panels
		bsplit = new FlippingSplitPane(FlippingSplitPane.VERTICAL_SPLIT, topPane, botPane);
		bsplit.setOneTouchExpandable(true);
		bsplit.setDividerSize(10);

		// now add the entire mess (centered of course)
		this.setLayout(new BorderLayout());
		this.add(bsplit, BorderLayout.CENTER);

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
		availableSort = new JTreeTableSorter(availableTable, (PObjectNode)availableModel.getRoot(), availableModel);
		selectedSort = new JTreeTableSorter(selectedTable, (PObjectNode)selectedModel.getRoot(), selectedModel);

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
				int s = splitPane.getDividerLocation();
				if (s > 0)
					SettingsHandler.setPCGenOption("InfoSpells.splitPane", s);
				s = asplit.getDividerLocation();
				if (s > 0)
					SettingsHandler.setPCGenOption("InfoSpells.asplit", s);
				s = bsplit.getDividerLocation();
				if (s > 0)
					SettingsHandler.setPCGenOption("InfoSpells.bsplit", s);
			}
		});
		shouldAutoSpells.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				aPC.setAutoSpells(shouldAutoSpells.isSelected());
			}
		});
		addSpellButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				addSpellButton(evt);
			}
		});
		delSpellButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				delSpellButton(evt);
			}
		});
		spellBookNameText.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				spellBookNameTextActionPerformed();
			}
		});
		addBookButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				addBookButton(evt);
			}
		});
		delBookButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				delBookButton(evt);
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

	/*
	 * set the spell Info text in the Spell Info panel to the
	 * currently selected spell
	 */
	private void setInfoLabelText(SpellInfo si)
	{
		if (si == null)
			return;
		CharacterSpell cs = si.getOwner();
		lastSpell = cs.getSpell(); //even if that's null
		Spell aSpell = lastSpell;
		if (aSpell != null)
		{
			String typeName = "CLASS";
			if (cs.getOwner() instanceof Domain)
				typeName = "DOMAIN";
			StringBuffer b = new StringBuffer();
			b.append("<html><font size=+1><b>").append(aSpell.piSubString()).append("</b></font>");
			final String addString = si.toString(); // would add [featList]
			if (addString.length() > 0)
			{
				b.append(" &nbsp;").append(addString);
			}
			b.append(" &nbsp;<b>Level:</b>&nbsp; ").append(aSpell.levelForKey(cs.getOwner().getSpellKey()));
			b.append(" &nbsp;<b>School:</b>&nbsp; ").append(aSpell.getSchool());
			b.append(" &nbsp;<b>SubSchool:</b>&nbsp; ").append(aSpell.getSubschool());
			b.append(" &nbsp;<b>Descriptor:</b>&nbsp; ").append(aSpell.descriptor());
			b.append(" &nbsp;<b>Components:</b>&nbsp; ").append(aSpell.getComponentList());
			b.append(" &nbsp;<b>Casting Time:</b>&nbsp; ").append(aSpell.getCastingTime());
			b.append(" &nbsp;<b>Duration:</b>&nbsp; ").append(aSpell.getDuration());
			b.append(" &nbsp;<b>Range:</b>&nbsp; ").append(aSpell.getRange());
			b.append(" &nbsp;<b>Effect:</b>&nbsp; ").append(aSpell.getEffect());
			b.append(" &nbsp;<b>Effect Type:</b>&nbsp; ").append(aSpell.getEffectType());
			b.append(" &nbsp;<b>Saving Throw:</b>&nbsp; ").append(aSpell.getSaveInfo());
			b.append(" &nbsp;<b>Spell Resistance:</b>&nbsp; ").append(aSpell.getSpellResistance());
			String bString = aSpell.getSource();
			if (bString.length() > 0)
				b.append(" &nbsp;<b>SOURCE:</b>&nbsp;").append(bString);

			b.append("</html>");
			infoLabel.setText(b.toString());
		}
	}

	/*
	 * set the class info text in the Class Info panel
	 * to the currently selected Character Class
	 */
	private void setClassLabelText(PCClass aClass)
	{
		if (aClass != null)
		{
			lastClass = aClass.getName();

			if (Globals.isDeadlandsMode())
			{
				StringBuffer a = new StringBuffer();
				a.append("<html><table border=1><tr><td><font size=-1><b>").append(aClass.piSubString()).append(" SpellPoints</b></font></td>");
				for (int i = 0; i <= 9; i++)
					a.append("<td><font size=-2><b><center>&nbsp;").append(aClass.getSPForLevelString(aClass.getLevel().intValue(), i, Globals.getDefaultSpellBook())).append("&nbsp;</b></center></font></td>");
				a.append("</tr></table>");
				classLabel.setText(a.toString());
				return;
			}

			StringBuffer b = new StringBuffer();
			b.append("<html><table border=1><tr><td><font size=-2><b>").append(aClass.piSubString()).append(" [").append(String.valueOf(aClass.getLevel().intValue() + aPC.getTotalBonusTo("PCLEVEL", aClass.getName(), true))).append("]</b></font></td>");
			for (int i = 0; i <= 9; i++)
				b.append("<td><font size=-2><b><center>&nbsp;").append(i).append("&nbsp;</b></center></font></td>");
			b.append("</tr>");
			b.append("<tr><td><font size=-1><b>Cast</b></font></td>");
			for (int i = 0; i <= 9; i++)
				b.append("<td><font size=-1><center>").append(getNumCast(aClass, i)).append("</center></font></td>");

			if (aClass.getKnownForLevel(aClass.getLevel().intValue(), 0) > 0)
			{
				if (Globals.isSSd20Mode())
				{
					b.append("<tr><td><font size=-1><b>Threshold</b></font></td>");
				}
				else
				{
					b.append("<tr><td><font size=-1><b>Known</b></font></td>");
				}
				for (int i = 0; i <= 9; i++)
				{
					final int a = aClass.getKnownForLevel(aClass.getLevel().intValue(), i);
					final int bonus = aClass.getSpecialtyKnownForLevel(aClass.getLevel().intValue(), i);
					String bString = "";
					if (bonus > 0)
						bString = "+" + bonus;

					b.append("<td><font size=-1><center>").append(a).append(bString).append("</center></font></td>");
				}
			}
			b.append("<tr><td><font size=-1><b>DC</b></font></td>");
			for (int i = 0; i <= 9; i++)
				b.append("<td><font size=-1><center>").append(getDC(aClass, i)).append("</center></font></td>");
			b.append("</tr></table>");

			b.append("Spell Caster Type: <b>").append(aClass.getSpellType()).append("</b><br>");
			b.append("Primary Stat Bonus: <b>").append(aClass.getSpellBaseStat()).append("</b><br>");
			if (aClass.getSpecialtyListString().length() != 0)
				b.append("School: <b>").append(aClass.getSpecialtyListString()).append("</b><br>");
			if (aClass.getProhibitedString().length() != 0)
				b.append("Prohibited School: <b>").append(aClass.getProhibitedString()).append("</b><br>");

			String bString = aClass.getSource();
			if (bString.length() > 0)
				b.append("<b>SOURCE</b>:").append(bString);

			b.append("</html>");
			classLabel.setText(b.toString());
		}
	}

	private String getNumCast(PCClass aClass, int level)
	{
		int cLevel = aClass.getLevel().intValue();
		String sbook = Globals.getDefaultSpellBook();
		final String cast =
		  aClass.getCastForLevel(cLevel, level, sbook) +
		  aClass.getBonusCastForLevelString(cLevel, level, sbook);
		return cast;
	}

	private int getDC(PCClass aClass, int level)
	{
		int DC = 10;
		int stat = Globals.getStatFromAbbrev(aClass.getSpellBaseStat());
		String statString = "";
		int a = 0;
		if (stat >= 0)
		{
			statString = Globals.s_ATTRIBSHORT[stat];
			a = aPC.getStatList().getStatModFor(statString);
			if (statString.equals(aClass.getSpellBaseStat()))
				a += aPC.getTotalBonusTo("STAT", "BASESPELLSTAT", true) / 2;
			a += aPC.getTotalBonusTo("STAT", "CAST=" + statString, true) / 2;
		}
		DC += level + a +
		  aPC.getTotalBonusTo("STAT", aClass.getName(), true) / 2 +
		  aPC.getTotalBonusTo("SPELL", "DC", true);
		return DC;
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
		final JTree atree = availableTable.getTree();
		atree.setRootVisible(false);
		atree.setShowsRootHandles(true);
		atree.setCellRenderer(new LabelTreeCellRenderer());

		availableTable.getSelectionModel().addListSelectionListener(new ListSelectionListener()
		{
			public void valueChanged(ListSelectionEvent e)
			{
				if (!e.getValueIsAdjusting())
				{
					final int idx = getSelectedIndex(e);
					if (idx < 0)
						return;

					if (!atree.isSelectionEmpty())
					{
						TreePath avaCPath = atree.getSelectionPath();
						String className = "";
						if (viewMode == GuiConstants.INFOSPELLS_VIEW_CLASS)
							className = avaCPath.getPathComponent(1).toString();
						else if ((viewMode == GuiConstants.INFOSPELLS_VIEW_LEVEL) && (avaCPath.getPathCount() > 2))
							className = avaCPath.getPathComponent(2).toString();
						else if ((viewMode == GuiConstants.INFOSPELLS_VIEW_TYPE) && (lastClass != null))
							className = lastClass;
						//className may have HTML encoding, so get rid of it
						className = Utility.stripHTML(className);
						PCClass aClass = aPC.getClassNamed(className);

						if (!className.equalsIgnoreCase(lastClass) && className.length() > 0 && aClass != null)
							setClassLabelText(aClass);
					}

					final Object temp = atree.getPathForRow(idx).getLastPathComponent();
					if (temp == null)
					{
						lastSpell = null;
						infoLabel.setText();
						return;
					}

					PObjectNode fNode = (PObjectNode)temp;
					if (fNode.getItem() instanceof SpellInfo)
					{
						CharacterSpell spellA = ((SpellInfo)fNode.getItem()).getOwner();
						if (spellA.getSpell() instanceof Spell)
						{
							addSpellButton.setEnabled(true);
							setInfoLabelText((SpellInfo)fNode.getItem());
						}
					}
				}
			}
		});

		MouseListener aml = new MouseAdapter()
		{
			public void mousePressed(MouseEvent e)
			{
				final int avaRow = atree.getRowForLocation(e.getX(), e.getY());
				final TreePath avaPath = atree.getPathForLocation(e.getX(), e.getY());
				if (avaRow != -1)
				{
					if (e.getClickCount() == 1 && avaPath != null)
					{
						atree.setSelectionPath(avaPath);
					}
					else if (e.getClickCount() == 2)
					{
						addSpellButton(null);
					}
				}
			}
		};
		atree.addMouseListener(aml);


		// now do the selectedTable and selectedTree

		selectedTable = new JTreeTable(selectedModel);
		selectedTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		final JTree stree = selectedTable.getTree();
		stree.setRootVisible(false);
		stree.setShowsRootHandles(true);
		stree.setCellRenderer(new LabelTreeCellRenderer());

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
					TreePath selCPath = stree.getSelectionPath();
					if (!stree.isSelectionEmpty())
					{
						spellBookNameText.setText(selCPath.getPathComponent(1).toString());
						spellBookNameTextActionPerformed();
					}

					final Object temp = stree.getPathForRow(idx).getLastPathComponent();
					if (temp == null)
					{
						lastSpell = null;
						infoLabel.setText();
						return;
					}

					PObjectNode fNode = (PObjectNode)temp;
					if (fNode.getItem() instanceof SpellInfo)
					{
						CharacterSpell spellA = ((SpellInfo)fNode.getItem()).getOwner();
						if (spellA.getSpell() instanceof Spell)
						{
							delSpellButton.setEnabled(true);
							setInfoLabelText((SpellInfo)fNode.getItem());
						}
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
						stree.setSelectionPath(selPath);
					else if (e.getClickCount() == 2)
					{
						delSpellButton(null);
					}
				}
			}
		};
		stree.addMouseListener(sml);

		// create the rightclick popup menus
		hookupPopupMenu(availableTable);
		hookupPopupMenu(selectedTable);
	}

	private void viewComboBoxActionPerformed(ActionEvent evt)
	{
		final int index = viewComboBox.getSelectedIndex();
		if (index != viewMode)
		{
			viewMode = index;
			SettingsHandler.setSpellsTab_AvailableListMode(viewMode);
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
			SettingsHandler.setSpellsTab_SelectedListMode(viewSelectMode);
			createSelectedModel();
			selectedTable.updateUI();
		}
	}

	// This is called when the tab is shown.
	private void formComponentShown(ComponentEvent evt)
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
			s = SettingsHandler.getPCGenOption("InfoSpells.splitPane", (int)(this.getSize().getWidth() * 2 / 10));
			t = SettingsHandler.getPCGenOption("InfoSpells.bsplit", (int)(this.getSize().getHeight() - 101));
			u = SettingsHandler.getPCGenOption("InfoSpells.asplit", (int)(this.getSize().getWidth() - 408));

			// set the prefered width on selectedTable
			for (int i = 0; i < selectedTable.getColumnCount(); i++)
			{
				TableColumn sCol = selectedTable.getColumnModel().getColumn(i);
				width = Globals.getCustColumnWidth("SpellSel", i);
				if (width != 0)
					sCol.setPreferredWidth(width);
				sCol.addPropertyChangeListener(new ResizeColumnListener(selectedTable, "SpellSel", i));
			}

			// set the prefered width on availableTable
			for (int i = 0; i < availableTable.getColumnCount(); i++)
			{
				TableColumn sCol = availableTable.getColumnModel().getColumn(i);
				width = Globals.getCustColumnWidth("SpellAva", i);
				if (width != 0)
					sCol.setPreferredWidth(width);
				sCol.addPropertyChangeListener(new ResizeColumnListener(availableTable, "SpellAva", i));
			}
		}
		if (s > 0)
		{
			splitPane.setDividerLocation(s);
			SettingsHandler.setPCGenOption("InfoSpells.splitPane", s);
		}
		if (t > 0)
		{
			bsplit.setDividerLocation(t);
			SettingsHandler.setPCGenOption("InfoSpells.bsplit", t);
		}
		if (u > 0)
		{
			asplit.setDividerLocation(u);
			SettingsHandler.setPCGenOption("InfoSpells.asplit", u);
		}

		availableTable.updateUI();
		selectedTable.updateUI();
	}

	// This recalculates the states of everything based
	// upon the currently selected character.
	public void updateCharacterInfo()
	{
		lastClass = "";
		final PlayerCharacter bPC = Globals.getCurrentPC();
		if (bPC != aPC)
			needsUpdate = true;
		aPC = bPC;
		if (aPC == null || !needsUpdate)
			return;

		aPC.getSpellList();
		shouldAutoSpells.setSelected(aPC.getAutoSpells());
		createModels();

		availableTable.updateUI();
		selectedTable.updateUI();

		createFeatList();

		needsUpdate = false;
	}

	private void addSpellButton(ActionEvent evt)
	{
		TreePath avaCPath = availableTable.getTree().getSelectionPath();
		TreePath selCPath = selectedTable.getTree().getSelectionPath();
		String bookName = "";

		if (selCPath == null)
			bookName = spellBookNameText.getText();
		else
			bookName = selCPath.getPathComponent(1).toString();

		if (bookName.length() <= 0)
		{
			JOptionPane.showMessageDialog(null, "First select a spellbook to add the spell to",
			  Constants.s_APPNAME, JOptionPane.ERROR_MESSAGE);
			return;		// need to select a spellbook
		}

		currSpellBook = bookName;

		Spell aSpell = null;
		String className = "";
		int spLevel = 0;

		Object endComp = avaCPath.getLastPathComponent();
		PObjectNode fNode = (PObjectNode)endComp;

		if (!(fNode.getItem() instanceof SpellInfo))
		{
			return;
		}

		CharacterSpell spellA = ((SpellInfo)fNode.getItem()).getOwner();
		if (spellA.getOwner() instanceof Race)
		{
			return;
		}

		CharacterSpell cs = null;
		PCClass aClass = null;

		spLevel = ((SpellInfo)fNode.getItem()).getActualLevel();
		if (spellA.getSpell() instanceof Spell)
		{
			aSpell = (Spell)spellA.getSpell();
			while (fNode != availableModel.getRoot())
			{
				className = fNode.getItem().toString();
				aClass = (PCClass)aPC.getClassNamed(className);
				if (aClass != null)
				{
					ArrayList aList = aClass.getCharacterSpell(aSpell, bookName, spLevel);
					for (Iterator ai = aList.iterator(); ai.hasNext();)
					{
						cs = (CharacterSpell)ai.next();
						if (cs == spellA)
							break;
						if (!spellA.getOwner().equals(cs.getOwner()))
							continue;
						spellA = cs;
						break;
					}
					if (cs == null)
					{
						cs = new CharacterSpell(spellA.getOwner(), aSpell);
						cs.addInfo(spLevel, 1, bookName);
//						aClass.addCharacterSpell(cs);
						spellA = cs;
					}
					else
					{
						aClass = null;
					}
					break;
				}
				fNode = fNode.getParent();
			}
			if (className.length() == 0)
			{
				className = spellA.getOwner().getName();
			}
		}
		else
		{
			return;
		}

		ArrayList featList = new ArrayList();
		final String aString = aPC.addSpell(spellA, featList, className, bookName, spLevel, spLevel);

		if (aString.length() > 0)
		{
			JOptionPane.showMessageDialog(null, aString, Constants.s_APPNAME, JOptionPane.ERROR_MESSAGE);
			return;
		}

		// now do all the GUI update stuff
		// Remember which rows are expanded
		boolean s[] = new boolean[selectedTable.getTree().getRowCount()];
		for (int i = 0; i < s.length; i++)
		{
			s[i] = selectedTable.getTree().isExpanded(i);
		}

		// reset selected spellbook model
		aPC.setDirty(true);
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

		return;

	}

	// memorize a spell with metamagic feats applied
	private void metamagicButton()
	{
		TreePath avaCPath = availableTable.getTree().getSelectionPath();
		TreePath selCPath = selectedTable.getTree().getSelectionPath();

		String bookName = "";
		if (selCPath == null)
			bookName = spellBookNameText.getText();
		else
			bookName = selCPath.getPathComponent(1).toString();

		if (bookName.length() <= 0)
		{
			JOptionPane.showMessageDialog(null,
			  "First select a spellbook to add the spell to",
			  Constants.s_APPNAME, JOptionPane.ERROR_MESSAGE);
			return;		// need to selected a spellbook
		}

		// no adding metamagic'ed spells to the default spellbook
		if (bookName.equals(Globals.getDefaultSpellBook()))
		{
			JOptionPane.showMessageDialog(null,
			  "No memorized spells in " + bookName,
			  Constants.s_APPNAME, JOptionPane.ERROR_MESSAGE);
			return;		// need to selected a spellbook
		}

		currSpellBook = bookName;

		Spell aSpell = null;
		String className = "";
		int spLevel = 0;

		Object endComp = avaCPath.getLastPathComponent();
		PObjectNode fNode = (PObjectNode)endComp;

		if (!(fNode.getItem() instanceof SpellInfo))
			return;

		CharacterSpell spellA = ((SpellInfo)fNode.getItem()).getOwner();
		if (bookName.equals(Globals.getDefaultSpellBook()))
		{
			spellA = new CharacterSpell(spellA.getOwner(), spellA.getSpell());
		}
		if (spellA.getSpell() instanceof Spell)
		{
			aSpell = (Spell)spellA.getSpell();
			PCClass aClass = null;
			spLevel = ((SpellInfo)fNode.getItem()).getActualLevel();
			while (fNode != availableModel.getRoot())
			{
				className = fNode.getItem().toString();
				aClass = (PCClass)aPC.getClassNamed(className);
				if (aClass != null)
					break;
				fNode = fNode.getParent();
			}
			if (className.length() == 0)
				className = (String)spellA.getOwner().getName();
		}
		else
			return;

		// make sure all the feats are set
		createFeatList();

		final ArrayList featList = featModel.getData();

		ChooserInterface c = ChooserFactory.getChooserInstance();
		c.setAvailableList(featList);
		c.setVisible(false);
		c.setPoolFlag(false);
		c.setAllowsDups(true);
		c.setTitle("Add Spell with Metamagic Feats");
		c.setMessageText("Select the Metamagic feats to memorize this spell with");
		c.setPool(99);
		c.show();

		final ArrayList fList = c.getSelectedList();
		ArrayList selFeatList = new ArrayList();
		int realLevel = spLevel;

		for (int i = 0; i < fList.size(); i++)
		{
			Feat aFeat = aPC.getFeatNamed(fList.get(i).toString());
			realLevel += aFeat.getAddSpellLevel();
			selFeatList.add(aFeat);
		}


//Globals.errorPrint("MMaddSB:MMaddSpell: " + className + ":" + spLevel + ":" + aSpell.getName() + ":" + bookName + ":feats:"+selFeatList.size());

		final String aString = aPC.addSpell(spellA, selFeatList, className, bookName, realLevel, spLevel);

		if (aString.length() > 0)
		{
			JOptionPane.showMessageDialog(null, aString,
			  Constants.s_APPNAME, JOptionPane.ERROR_MESSAGE);
			return;
		}

		// now do all the GUI update stuff
		// Remember which rows are expanded
		boolean s[] = new boolean[selectedTable.getTree().getRowCount()];
		for (int i = 0; i < s.length; i++)
			s[i] = selectedTable.getTree().isExpanded(i);

		aPC.setDirty(true);
		createSelectedModel();
		selectedTable.updateUI();

		//re-expand the rows
		for (int i = 0; i < s.length; i++)
		{
			if (s[i])
				selectedTable.getTree().expandRow(i);
		}

		spellBookNameText.setText(bookName);

		return;

	}

	private void delSpellButton(ActionEvent evt)
	{
		TreePath selCPath = selectedTable.getTree().getSelectionPath();

		if (selCPath == null)
		{
			JOptionPane.showMessageDialog(null,
			  "Select the spell to remove from your spellbook.",
			  Constants.s_APPNAME, JOptionPane.ERROR_MESSAGE);
			return;		// need to selected a spellbook
		}

		PCClass aClass = null;
		Spell aSpell = null;
		String className = "";
		int spLevel = 0;
		String bookName = "";

		Object endComp = selCPath.getLastPathComponent();
		PObjectNode fNode = (PObjectNode)endComp;

		if (!(fNode.getItem() instanceof SpellInfo))
			return;
		SpellInfo si = (SpellInfo)fNode.getItem();

		CharacterSpell spellA = si.getOwner();

		// if it's a race spell, we can't delete it
		if (spellA.getOwner() instanceof Race)
			return;

		if (spellA.getSpell() instanceof Spell)
		{
			aSpell = (Spell)spellA.getSpell();
			spLevel = ((SpellInfo)fNode.getItem()).getActualLevel();
			while (fNode != availableModel.getRoot())
			{
				className = fNode.getItem().toString();
				aClass = (PCClass)aPC.getClassNamed(className);
				if (aClass != null)
					break;
				fNode = fNode.getParent();
			}
		}
		else
			return;

		bookName = selCPath.getPathComponent(1).toString();
		currSpellBook = bookName;

		final String aString = aPC.delSpell(si, aClass, bookName);

		if (aString.length() > 0)
		{
			JOptionPane.showMessageDialog(null, aString,
			  Constants.s_APPNAME, JOptionPane.ERROR_MESSAGE);
			return;
		}

		// now do all the GUI update stuff
		// Remember which rows are expanded
		boolean s[] = new boolean[selectedTable.getTree().getRowCount()];
		for (int i = 0; i < s.length; i++)
			s[i] = selectedTable.getTree().isExpanded(i);

		aPC.setDirty(true);
		createSelectedModel();
		selectedTable.updateUI();

		//re-expand the rows
		for (int i = 0; i < s.length; i++)
		{
			if (s[i])
				selectedTable.getTree().expandRow(i);
		}

		return;
	}

	/**
	 * This is used when selecting a new spellbook
	 **/
	private void spellBookNameTextActionPerformed()
	{
		final String aString = (String)spellBookNameText.getText();
		if ((aString == null) || aString.equals(currSpellBook))
		{
			return;
		}

		// if the user selects a new spellbook, we have to refresh
		// the available table because it could change the spells
		// known and memorizable
		currSpellBook = aString;
		spellBookNameText.setText(aString);
		if (!bookList.contains(aString))
		{
			bookList.add(aString);
		}
		// now do all the GUI update stuff
		// Remember which rows are expanded
		boolean s[] = new boolean[availableTable.getTree().getRowCount()];
		for (int i = 0; i < s.length; i++)
			s[i] = availableTable.getTree().isExpanded(i);

		createAvailableModel();
		availableTable.updateUI();

		//re-expand the rows
		for (int i = 0; i < s.length; i++)
		{
			if (s[i])
				availableTable.getTree().expandRow(i);
		}

		return;
	}

	/*****  **  **  **   **
	 **     **  **  ***  **
	 ***This is used to add new spellbooks when the
	 ***spellBookNameText JTextField is edited
	 **     **  **  **  ***
	 **     ******  **   **
	 */
	private void addBookButton(ActionEvent evt)
	{
		final String aString = spellBookNameText.getText();
		if (aString.equals(currSpellBook))
			return;

		if (aPC.addSpellBook(aString))
		{
			aPC.setDirty(true);
			spellBookNameText.setText(aString);
			spellBookNameTextActionPerformed();
			currSpellBook = aString;
			if (!bookList.contains(aString))
			{
				bookList.add(aString);
			}
			createSelectedModel();
			selectedTable.updateUI();
		}
		else
		{
			Globals.errorPrint("addBookButton:failed");
			return;
		}
		return;
	}

	private void delBookButton(ActionEvent evt)
	{
		String aString = spellBookNameText.getText();
		if (aString.equalsIgnoreCase(Globals.getDefaultSpellBook()))
		{
			Globals.errorPrint("You may not delete the default spellbook");
			return;
		}
		if (aPC.delSpellBook(aString))
		{
			aPC.setDirty(true);
			currSpellBook = Globals.getDefaultSpellBook();
			createModels();
			availableTable.updateUI();
			selectedTable.updateUI();
		}
		else
		{
			Globals.errorPrint("delBookButton:failed ");
			return;
		}

		return;
	}

	/**
	 *
	 * add all metamagic feats to arrayList
	 *
	 **/
	private void createFeatList()
	{
		//Calculate the aggregate feat list
		aPC.aggregateFeatList();
		aPC.setAggregateFeatsStable(true);
		aPC.setAutomaticFeatsStable(true);
		aPC.setVirtualFeatsStable(true);

		// get the list of metamagic feats for the PC
		ArrayList featList = new ArrayList();
		ArrayList feats = aPC.aggregateFeatList();
		Globals.sortPObjectList(feats);
		for (Iterator i = feats.iterator(); i.hasNext();)
		{
			Feat aFeat = (Feat)i.next();
			if (aFeat.isType("Metamagic"))
				featList.add(aFeat.getName());
		}
		featModel.setData(featList);
	}

	/**
	 *  Model for the JTable containing the spell feats.
	 *
	 * @author     Bryan McRoberts <merton_monk@users.sourceforge.net>
	 * created    den 11 maj 2001
	 */
	class SpellFeatModel extends AbstractTableModel
	{
		/**
		 *  Contains the feats
		 *
		 * @since
		 */
		public ArrayList data = new ArrayList();
		final String[] nameList = {"Metamagic Feats"};

		/**
		 *  Sets the Data attribute of the SpellFeatModel object
		 *
		 * @param  aArrayList  The new Data value
		 * @since
		 */
		public void setData(ArrayList aArrayList)
		{
			data = aArrayList;
		}

		/**
		 *  gets the ArrayList data
		 *
		 */
		public ArrayList getData()
		{
			return data;
		}

		/**
		 *  Gets the ColumnCount attribute of the SpellFeatModel object
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
		 */
		public Class getColumnClass(int c)
		{
			return String.class;
		}

		/**
		 *  Gets the RowCount attribute of the SpellFeatModel object
		 *
		 * @return    The RowCount value
		 * @since
		 */
		public int getRowCount()
		{
			return data.size();
		}

		/**
		 *  Gets the ColumnName attribute of the SpellFeatModel object
		 *
		 * @param  col  Description of Parameter
		 * @return      The ColumnName value
		 * @since
		 */
		public String getColumnName(int col)
		{
			return nameList[col];
		}

		/**
		 *  Gets the ValueAt attribute of the SpellFeatModel object
		 *
		 * @param  row  Description of Parameter
		 * @param  col  Description of Parameter
		 * @return      The ValueAt value
		 * @since
		 */
		public Object getValueAt(int row, int col)
		{
			return data.get(row);
		}
	}

	/**
	 * Creates the SpellModel that will be used.
	 **/
	protected void createModels()
	{
		createAvailableModel();
		createSelectedModel();
	}

	protected void createAvailableModel()
	{
		if (availableModel == null)
			availableModel = new SpellModel(viewMode, true);
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
			selectedModel = new SpellModel(viewSelectMode, false);
		else
			selectedModel.resetModel(viewSelectMode, false, false);
		if (selectedSort != null)
			selectedSort.setRoot((PObjectNode)selectedModel.getRoot());
		if (selectedSort != null)
			selectedSort.sortNodeOnColumn();
	}

	/**
	 *  The TreeTableModel has a single <code>root</code> node
	 *  This root node has a null <code>parent</code>.
	 *  All other nodes have a parent which points to a non-null node.
	 *  Parent nodes contain a list of  <code>children</code>, which
	 *  are all the nodes that point to it as their parent.
	 *  <code>nodes</code> which have 0 children are leafs (the end of
	 *  that linked list).  nodes which have at least 1 child are not leafs
	 *  Leafs are like files and non-leafs are like directories.
	 *  The leafs contain an Object that we want to know about (Spells)
	 **/
	class SpellModel extends AbstractTreeTableModel implements TreeTableModel
	{
		// there are two roots. One for available spells
		// and one for selected spells (spellbooks)
		private PObjectNode avaRoot;
		private PObjectNode selRoot;

		// list of columns names
		String[] availNameList = {""};
		String[] selNameList = {""};

		// Types of the columns.
		protected int modelType = MODEL_AVAIL;

		/**
		 * Creates a SpellModel
		 */
		public SpellModel(int mode, boolean available)
		{
			super(null);

			//
			// if you change/add/remove entries to nameList
			// you also need to change the static COL_XXX defines
			// at the begining of this file
			//
			if (Globals.isSSd20Mode())
			{
				availNameList = new String[]{"Name", "Element", "Source"};
				selNameList = new String[]{"Name", "Element", "Nature", "Casting Threshold", "Range", "Target/Effect/Area", "Duration", "Save Info", "SR", "Source"};
			}
			else if (Globals.isWheelMode())
			{
				availNameList = new String[]{"Name", "Element", "Source"};
				selNameList = new String[]{"Name", "Element", "Nature", "Casting Threshold", "Range", "Target/Effect/Area", "Duration", "Save Info", "SR", "Source File"};
			}
			else
			{
				availNameList = new String[]{"Name", "School", "Source"};
				selNameList = new String[]{"Name", "School", "SubSchool", "Descriptor", "Components", "Casting Time", "Range", "Effect", "Effect Type", "Duration", "Save Info", "SR", "Source File"};
			}

			if (!available)
				modelType = MODEL_SELECTED;
			resetModel(mode, available, true);
		}

		/**
		 * This assumes the SpellModel exists but
		 * needs branches and nodes to be repopulated
		 */
		public void resetModel(int mode, boolean available, boolean newCall)
		{
			ArrayList classList = new ArrayList();
			bookList.clear();

			// get the list of spell casting Classes
			for (Iterator iClass = aPC.getClassList().iterator(); iClass.hasNext();)
			{
				final PCClass aClass = (PCClass)iClass.next();
				if (!aClass.getSpellType().equals(Constants.s_NONE))
				{
					// if it's a prestige class, but it
					// uses a CastAs, then we can ignore
					// it because the spell lists are the
					// same. Otherwise, we have to add it
					// because the spell lists might !be=
					//
					// MotW Animal Lord PrC's use a non-visible class for CASTAS
					//
					if (aClass.isPrestige() && (aClass.getCastAs().length() > 0))
					{
						final PCClass castClass = Globals.getClassNamed(aClass.getCastAs());
						if ((castClass != null) && !castClass.isPrestige())
						{
							continue;
						}
					}
					classList.add(aClass);
				}
			}

			// build list of spellbooks the PC already has
			for (Iterator iBook = aPC.getSpellBooks().iterator(); iBook.hasNext();)
			{
				// build spell book list
				String sBook = (String)iBook.next();
				if (!bookList.contains(sBook))
				{
					bookList.add(sBook);
				}
			}


			//
			// build availableTable (list of all spells)
			//
			if (available)
			{
				// this is the root node
				avaRoot = new PObjectNode();

				switch (mode)
				{
					case GuiConstants.INFOSPELLS_VIEW_CLASS:     // class/level/spell
						setRoot(avaRoot);

						// build the Class root nodes
						PObjectNode cc[] = new PObjectNode[classList.size()];
						// iterate thru all the spell casting classes
						// and fill out the class/level/spell tree
						for (int iClass = 0; iClass < classList.size(); iClass++)
						{
							PCClass aClass = (PCClass)classList.get(iClass);
							aClass.getSpellKey();
//							String className = aClass.getName();
							String className = aClass.piString();
							cc[iClass] = new PObjectNode();
							cc[iClass].setItem(className);
							if (currSpellBook.equals(Globals.getDefaultSpellBook()) && aClass.getCastAs().length() > 0)
								aClass = Globals.getClassNamed(aClass.getCastAs());
							PObjectNode spLev[] = new PObjectNode[10];

							for (int iLev = 0; iLev <= 9; iLev++)
							{
								String llString = "";
								llString = llString.concat("level ").concat(Integer.toString(iLev));
								spLev[iLev] = new PObjectNode();
								spLev[iLev].setItem(llString);
								ArrayList charSpells;
								if (currSpellBook.equals(Globals.getDefaultSpellBook()))
								{
									charSpells = Globals.getSpellsIn(iLev, aClass.getSpellKeyName(), "");
									addDomainSpellsForClass(className, spLev[iLev], iLev);
								}
								else
								{
									charSpells = aClass.getCharacterSpell(null, Globals.getDefaultSpellBook(), iLev, new ArrayList());
									addDomainSpellsForClass(className, spLev[iLev], iLev);
								}
								setNodeSpells(charSpells, spLev[iLev], iLev, (PObject)aClass, Globals.getDefaultSpellBook());
								if (!spLev[iLev].isLeaf())
								{
									spLev[iLev].setParent(cc[iClass]);
									cc[iClass].addChild(spLev[iLev]);
								}
							} // end iLev 0->9 loop
							// now add to root if !empty
							if (!cc[iClass].isLeaf())
							{
								cc[iClass].setParent(avaRoot);
							}
						} // end spell casting classes loop
						// now add to the root node
						avaRoot.setChildren(cc, false);

						break; // end VIEW_CLASS


						// or do level/class/spell
					case GuiConstants.INFOSPELLS_VIEW_LEVEL:

						setRoot(avaRoot);

						// build the level root nodes
						PObjectNode ll[] = new PObjectNode[10];
						// iterate thru all the spell levels
						// and fill out the level/class/spell tree
						for (int iLev = 0; iLev <= 9; iLev++)
						{
							String llString = "";
							llString = llString.concat("level ").concat(Integer.toString(iLev));
							ll[iLev] = new PObjectNode();
							ll[iLev].setItem(llString);
							PObjectNode spClass[] = new PObjectNode[classList.size()];
							for (int iClass = 0; iClass < spClass.length; iClass++)
							{
								PCClass aClass = (PCClass)classList.get(iClass);
								aClass.getSpellKey();
//								String className = aClass.getName();
								String className = aClass.piString();
								spClass[iClass] = new PObjectNode();
								spClass[iClass].setItem(className);
								if (currSpellBook.equals(Globals.getDefaultSpellBook()) && aClass.getCastAs().length() > 0)
									aClass = Globals.getClassNamed(aClass.getCastAs());
								ArrayList charSpells;
								if (currSpellBook.equals(Globals.getDefaultSpellBook()))
								{
									charSpells = Globals.getSpellsIn(iLev, aClass.getSpellKeyName(), "");
									addDomainSpellsForClass(className, spClass[iClass], iLev);
								}
								else
								{
									charSpells = aClass.getCharacterSpell(null, Globals.getDefaultSpellBook(), iLev, new ArrayList());
									addDomainSpellsForClass(className, spClass[iClass], iLev);
								}
								setNodeSpells(charSpells, spClass[iClass], iLev, (PObject)aClass, Globals.getDefaultSpellBook());
								// add if !empty
								if (!spClass[iClass].isLeaf())
								{
									spClass[iClass].setParent(ll[iLev]);
									ll[iLev].addChild(spClass[iClass]);
								}

							} // end spell casting classes loop
							// now add to root if !empty
							if (!ll[iLev].isLeaf())
							{
								ll[iLev].setParent(avaRoot);
							}
						} // end levels loop
						// now add to the root node
						avaRoot.setChildren(ll, false);

						break; // end VIEW_LEVEL
					default:
						Globals.errorPrint("In InfoSpells.SpellModel.resetModel the mode " + mode + " is not handled (all spells).");
						break;

				} // end of switch(mode)
			} // end of availableTable builder

			else

			{ // selectedTable builder (it's a list of spellbooks)

				// this is the root node
				selRoot = new PObjectNode();

				switch (mode)
				{
					// book/class/level/spell
					case GuiConstants.INFOSPELLS_VIEW_CLASS:
						setRoot(selRoot);

						// build the spellbook root nodes
						PObjectNode cb[] = new PObjectNode[bookList.size()];
						// iterate thru all the books and fill
						// out the book/class/level/spell tree
						for (int iBook = 0; iBook < bookList.size(); iBook++)
						{
							String bookName = bookList.get(iBook).toString();
							cb[iBook] = new PObjectNode();
							cb[iBook].setItem(bookName);
							PObjectNode bookClass[] = new PObjectNode[classList.size()];
							for (int iClass = 0; iClass < classList.size(); iClass++)
							{
								PCClass aClass = (PCClass)classList.get(iClass);
//								String className = aClass.getName();
								String className = aClass.piString();
								PObjectNode sbLev[] = new PObjectNode[21];
								bookClass[iClass] = new PObjectNode();
								bookClass[iClass].setItem(className);
								for (int iLev = 0; iLev <= 20; iLev++)
								{
									String llString = "";
									llString = llString.concat("level ").concat(Integer.toString(iLev));
									sbLev[iLev] = new PObjectNode();
									sbLev[iLev].setItem(llString);

									ArrayList charSpells = aClass.getCharacterSpell(null, bookName, iLev);
									setNodeSpells(charSpells, sbLev[iLev], iLev, (PObject)aClass, bookName);
									// add if !empty
									if (!sbLev[iLev].isLeaf())
									{
										sbLev[iLev].setParent(bookClass[iClass]);
										bookClass[iClass].addChild(sbLev[iLev]);
									}
								}

								// now add to Class if !empty
								if (!bookClass[iClass].isLeaf())
								{
									bookClass[iClass].setParent(cb[iBook]);
									cb[iBook].addChild(bookClass[iClass]);
								}

							} // end of classes loop
							final ArrayList rList = aPC.getRace().getCharacterSpell(null, bookName, -1);
							if (rList.size() > 0)
							{
								PObjectNode rnode = new PObjectNode();
								rnode.setItem(aPC.getRace());
								cb[iBook].addChild(rnode);
								setNodeSpells(rList, rnode, 0, (PObject)aPC.getRace(), bookName);
							}
							// now add to root if !empty
							if (!cb[iBook].isLeaf())
							{
								cb[iBook].setParent(selRoot);
							}
						} // end of book loop
						// now add to the root node
						selRoot.setChildren(cb, true);

						break;  // end VIEW_CLASS

						// book/level/class/spell
					case GuiConstants.INFOSPELLS_VIEW_LEVEL:
						setRoot(selRoot);

						// build the spellbook root nodes
						PObjectNode lb[] = new PObjectNode[bookList.size()];
						// iterate thru all the books and fill
						// out the book/level/class/spell tree
						for (int iBook = 0; iBook < bookList.size(); iBook++)
						{
							String bookName = bookList.get(iBook).toString();
							lb[iBook] = new PObjectNode();
							lb[iBook].setItem(bookName);
							PObjectNode bookLevel[] = new PObjectNode[21];
							for (int iLev = 0; iLev <= 20; iLev++)
							{
								String llString = "";
								llString = llString.concat("level ").concat(Integer.toString(iLev));
								bookLevel[iLev] = new PObjectNode();
								bookLevel[iLev].setItem(llString);

								PObjectNode sbClass[] = new PObjectNode[classList.size()];
								for (int iClass = 0; iClass < classList.size(); iClass++)
								{
									PCClass aClass = (PCClass)classList.get(iClass);
//									String className = aClass.getName();
									String className = aClass.piString();
									sbClass[iClass] = new PObjectNode();
									sbClass[iClass].setItem(className);

									ArrayList charSpells = aClass.getCharacterSpell(null, bookName, iLev);
									setNodeSpells(charSpells, sbClass[iClass], iLev, (PObject)aClass, bookName);
									// add if !empty
									if (!sbClass[iClass].isLeaf())
									{
										sbClass[iClass].setParent(bookLevel[iLev]);
										bookLevel[iLev].addChild(sbClass[iClass]);
									}
								} // end classes loop

								// add if !empty
								if (!bookLevel[iLev].isLeaf())
								{
									bookLevel[iLev].setParent(lb[iBook]);
									lb[iBook].addChild(bookLevel[iLev]);
								}
							} // end of Level loop
							final ArrayList rList = aPC.getRace().getCharacterSpell(null, bookName, -1);
							if (rList.size() > 0)
							{
								PObjectNode rnode = new PObjectNode();
								rnode.setItem(aPC.getRace());
								lb[iBook].addChild(rnode);
								setNodeSpells(rList, rnode, 0, (PObject)aPC.getRace(), bookName);
							}
							// now add to root if !empty
							if (!lb[iBook].isLeaf())
							{
								lb[iBook].setParent(selRoot);
							}
						} // end of book loop
						// now add to the root node
						selRoot.setChildren(lb, false);

						break; // end VIEW_LEVEL
					default:
						Globals.errorPrint("In InfoSpells.SpellModel.resetModel the mode " + mode + " is not handled (spellbooks).");
						break;

				} // end of switch(mode)
			} // end if else

			PObjectNode rootAsPObjectNode = (PObjectNode)root;
			if (rootAsPObjectNode.getChildCount() > 0)
				fireTreeStructureChanged(root, rootAsPObjectNode.getChildren(), null, null);
			if (currSpellBook.equals(""))
				currSpellBook = Globals.getDefaultSpellBook();
			spellBookNameText.setText(currSpellBook);
		}

		/** this method looks for any domains associated with this class
		 * and finds any spells associated with this domain
		 * and then adds this list to a "Domains" directory
		 * to keep the Domain list in an easily distinguished place
		 */
		public void addDomainSpellsForClass(String className, PObjectNode theParent, int iLev)
		{
			int iMax = aPC.getCharacterDomainList().size();
			if (iMax == 0)
				return;
			PObjectNode p = new PObjectNode();
			p.setItem("Domains");
			boolean dom = false;
			for (int iDom = 0; iDom < aPC.getCharacterDomainList().size(); iDom++)
			{
				CharacterDomain aCD = (CharacterDomain)aPC.getCharacterDomainList().get(iDom);
				Domain aDom = (Domain)aCD.getDomain();
				// if any domains have this class as a source and a valid domain, add them
				if (aDom != null && aCD.getDomainSource().startsWith("PCClass|" + className))
				{
					ArrayList domainSpells = Globals.getSpellsIn(iLev, "", aDom.getName());
					p.setParent(theParent);
					if (!dom)
						theParent.addChild(p);
					dom = true;
					setNodeSpells(domainSpells, p, iLev, (PObject)aDom, Globals.getDefaultSpellBook());
				}
			}
		}

		// There must be a root node, but we keep it hidden
		public void setRoot(PObjectNode aNode)
		{
			root = aNode;
		}

		// return the root node
		public Object getRoot()
		{
			return (PObjectNode)root;
		}

		public void removeItemFromNodes(PObjectNode p, Object e)
		{
			if (p == null)
				p = (PObjectNode)root;

			PObjectNode pAsPObjectNode = (PObjectNode)p;
			// if no children, remove it and update parent
			if (pAsPObjectNode.getChildCount() == 0 && p.getItem().equals(e))
			{
				p.getParent().removeChild(p);
			}
			else
			{
				for (int i = 0; i < pAsPObjectNode.getChildCount(); i++)
				{
					removeItemFromNodes(pAsPObjectNode.getChild(i), e);
				}
			}
		}

		/* The JTreeTableNode interface. */

		/**
		 * Returns int number of columns. (SpellModel)
		 */
		public int getColumnCount()
		{
			if (modelType == 0) // available
				return availNameList.length;
			else
				return selNameList.length;
		}

		/**
		 * Returns String name of a column. (SpellModel)
		 */
		public String getColumnName(int column)
		{
			if (modelType == 0) // available
				return availNameList[column];
			else
				return selNameList[column];
		}

		/**
		 * Returns Class for the column. (SpellModel)
		 */
		public Class getColumnClass(int column)
		{
			switch (column)
			{
				case COL_NAME:
					return TreeTableModel.class;
				default:
					return String.class;
			}
		}

		/**
		 * Returns boolean if can edit a cell. (SpellModel)
		 */
		public boolean isCellEditable(Object node, int column)
		{
			return (column == COL_NAME);
		}

		/**
		 * changes the column order sequence and/or number of
		 * columns based on modelType (0=available, 1=selected)
		 * also adjusts for SS d20 Mode
		 **/
		public int adjustAvailColumnConst(int column)
		{
			if (Globals.isSSd20Mode())
			{
				if (column == 3)
					column = COL_THRESHOLD;
				else if (column == 4)
					column = COL_RANGE;
				else if (column == 5)
					column = COL_EFFECT;
				else if (column == 6)
					column = COL_DURATION;
				else if (column == 7)
					column = COL_SAVE;
				else if (column == 8)
					column = COL_SRC;
				else
					column = 0;
			}

			if (modelType == MODEL_AVAIL) // available
			{
				if (column > COL_SCHOOL)
					column = COL_SRC;
			}
			return column;
		}

		/**
		 * Returns Object value of the column. (SpellModel)
		 */
		public Object getValueAt(Object node, int column)
		{
			final PObjectNode fn = (PObjectNode)node;
			Spell aSpell = null;
			CharacterSpell spellA = null;
			int spLevel = 0;

			if (fn == null)
			{
				Globals.errorPrint("Somehow we have no active node when doing getValueAt in InfoSpells.");
				return null;
			}

			if (fn.getItem() instanceof SpellInfo)
			{
				spellA = ((SpellInfo)fn.getItem()).getOwner();
				aSpell = (Spell)spellA.getSpell();
				spLevel = ((SpellInfo)fn.getItem()).getActualLevel();
			}
			column = adjustAvailColumnConst(column);

			switch (column)
			{
				case COL_NAME:
					return fn.toString();
				case COL_SCHOOL:
					if (aSpell != null)
					{
						return aSpell.getSchool();
					}
					else
						return null;
				case COL_SUBSCHOOL:
					if (aSpell != null)
						return aSpell.getSubschool();
					else
						return null;
				case COL_DESCRIPTOR:
					if (aSpell != null)
						return aSpell.descriptor();
					else
						return null;
				case COL_COMPONENT:
					if (aSpell != null)
						return aSpell.getComponentList();
					else
						return null;
				case COL_CASTTIME:
					if (aSpell != null)
						return aSpell.getCastingTime();
					else
						return null;
				case COL_RANGE:
					if (aSpell != null)
						return aSpell.getRange();
					else
						return null;
				case COL_EFFECT:
					if (aSpell != null)
						return aSpell.getEffect();
					else
						return null;
				case COL_EFFECTTYPE:
					if (aSpell != null)
						return aSpell.getEffectType();
					else
						return null;
				case COL_DURATION:
					if (aSpell != null)
						return aSpell.getDuration();
					else
						return null;
				case COL_SAVE:
					if (aSpell != null)
						return aSpell.getSaveInfo();
					else
						return null;
				case COL_SR:
					if (aSpell != null)
						return aSpell.getSpellResistance();
					else
						return null;
				case COL_SRC:
					if (aSpell != null)
						return aSpell.getSource();
					else
						return null;
				case COL_THRESHOLD:
					if (aSpell != null)
						return Integer.toString(aSpell.getCastingThreshold());
					else
						return null;
				default:
					return fn.getItem();
			}
			// return null;
		}

		public void addChild(Object aChild, Object aParent)
		{
			PObjectNode aSN = new PObjectNode();
			aSN.setItem(aChild);
			aSN.setParent((PObjectNode)aParent);
			((PObjectNode)aParent).addChild(aSN);
		}

	}

	// this function takes a branch and adds the spells to it
	private void setNodeSpells(ArrayList charSpells, PObjectNode tNode, int iLev, PObject obj, String book)
	{
//		Collections.sort(charSpells);
		for (Iterator fI = charSpells.iterator(); fI.hasNext();)
		{
			Object o = fI.next();
			PObjectNode fCN;
			if (o instanceof CharacterSpell)
			{
				final CharacterSpell cs = (CharacterSpell)o;
				final SpellInfo si = cs.getSpellInfoFor(book, iLev, -1);
				if (si == null)
					continue;
				fCN = new PObjectNode();
				fCN.setItem(si);
			}
			else
			{
				Spell aSpell = (Spell)o;
				if (iLev != aSpell.levelForKey(obj.getSpellKey()))
					continue;
				CharacterSpell cs = new CharacterSpell(obj, aSpell);
				SpellInfo si = cs.addInfo(iLev, 1, book);
				fCN = new PObjectNode();
				fCN.setItem(si);
			}
			fCN.setParent(tNode);
			tNode.addChild(fCN);
		} // end spells loop
	}





	/**
	 * here is all the code for the Memorize spell with Metamagic feats
	 *
	 * this code was stolen from Greg Bingleman's EQFrame and EqBuilder
	 */

/*
 * this code is currently not being used. A chooser is being used instead
 */


	/**
	 * Popup frame
	 */

	class MMFrame extends JFrame
	{
		MainMM mainMM = null;

		public MMFrame()
		{
			/*
			 * jikes says:
			 *   "Ambiguous reference to member named 'getClass' inherited
			 *    from type 'java/lang/Object' but also declared or
			 *    inherited in the enclosing type 'pcgen/gui/InfoSpells'.
			 *    Explicit qualification is required."
			 * Well, let's do what jikes wants us to do ;-)
			 *
			 * author: Thomas Behr 15-04-02
			 */
			ClassLoader loader = MMFrame.this.getClass().getClassLoader();
			Toolkit kit = Toolkit.getDefaultToolkit();
			// according to the API, the following should *ALWAYS* use '/'
			Image img = kit.getImage(loader.getResource("pcgen/gui/resource/PcgenIcon.gif"));
			loader = null;
			MMFrame.this.setIconImage(img);
			Dimension screenSize = kit.getScreenSize();
			int screenHeight = screenSize.height;
			int screenWidth = screenSize.width;

			MMFrame.this.setSize(screenWidth / 2, screenHeight / 2);
			MMFrame.this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			// center frame in screen
			Utility.centerFrame(this, true);
			MMFrame.this.setTitle("Add Spell with Metamagic Feats");

			mainMM = new MainMM();
			MMFrame.this.getContentPane().add(mainMM);

//  			Container contentPane = MMFrame.this.getContentPane();
//  			contentPane.add(mainMM);

			MMFrame.this.pack();
			MMFrame.this.setVisible(true);
		}

		public boolean setSpell(Spell aSpell)
		{
			if (mainMM != null)
			{
				return mainMM.setSpell(aSpell);
			}
			return false;
		}

	} //end MMFrame

	/**
	 *
	 * new JPanel that get's poped up
	 *
	 */

	class MainMM extends JPanel
	{
		private JTableEx mmTable;
		private JScrollPane mmPane;

		private GridBagLayout gridBagLayout = new GridBagLayout();
		private JPanel buttonPanel = new JPanel();
		private JButton doneButton = new JButton();

		public MainMM()
		{
			try
			{
				jbInit();
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}

		private void jbInit() throws Exception
		{
			// make sure all the feats are set
			createFeatList();

			mmTable = new JTableEx(featModel);
			mmPane = new JScrollPane(mmTable);

			GridBagConstraints c;
			MainMM.this.setLayout(gridBagLayout);

			//
			// all metamagic feats
			//
			mmTable.setEnabled(true);
			mmTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
			mmTable.setDoubleBuffered(false);
			mmPane.setViewportView(mmTable);

			MainMM.this.add(mmPane, new GridBagConstraints(0, 0, 1, 1, 1, 1, GridBagConstraints.NORTH, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
			mmTable.addMouseListener(new MouseAdapter()
			{
				public void mouseClicked(MouseEvent evt)
				{
					mmTableMouseClicked(evt);
				}
			});

			doneButton.setText("Done");
			doneButton.setMnemonic(KeyEvent.VK_O);
			doneButton.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					// mmAddSpell();
					Globals.debugPrint("DONE: " + MainMM.this.getParent());
					mmFrame.dispose();
				}
			});

			c = new GridBagConstraints();
			c.gridx = 0;
			c.gridy = 0;
			c.anchor = GridBagConstraints.NORTH;
			buttonPanel.add(doneButton, c);

			MainMM.this.add(buttonPanel, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.NORTH, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));

		}

		private void mmTableMouseClicked(MouseEvent evt)
		{
			Feat aFeat = null;
			int totalLevels = 0;

			int iRow = mmTable.getSelectedRowCount();
			int[] cList = mmTable.getSelectedRows();

			while (iRow-- > 0)
			{
				aFeat = aPC.getFeatNamed((String)mmTable.getValueAt(cList[iRow], 0));
				//aFeat = aPC.getFeatNamed(featModel.getValueAt(cList[iRow], 0));
				totalLevels += aFeat.getAddSpellLevel();
			}
			if (aFeat != null)
			{
				Globals.debugPrint("mmTableMouseClick: ", aFeat.getName());
			}
			Globals.debugPrint("mmTableMouseClick:Total: ", totalLevels);

		}

		public boolean setSpell(Spell aSpell)
		{
			Globals.debugPrint("MainMM:setSpell: ", aSpell.getName());
			return true;
		}

	} // end class MainMM

	/**
	 *
	 * all done with the add spell with Metamagic feats popup window
	 *
	 */


	/**
	 * implementation of Filterable interface
	 */
	public void initializeFilters()
	{
		FilterFactory.registerAllSourceFilters(this);
		FilterFactory.registerAllSpellFilters(this);
	}

	/**
	 * implementation of Filterable interface
	 */
	public void refreshFiltering()
	{
		createModels();
		availableTable.updateUI();
		selectedTable.updateUI();
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
		return DISABLED_MODE;
	}
}

