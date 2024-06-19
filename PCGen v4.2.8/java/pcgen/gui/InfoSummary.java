/*
 * InfoSummary.java
 * Copyright 2002 (C) James Dempsey
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
 * Created on June 22, 2002, 4:00 PM
 */

package pcgen.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Iterator;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JViewport;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import pcgen.core.Constants;
import pcgen.core.Equipment;
import pcgen.core.Globals;
import pcgen.core.PCClass;
import pcgen.core.PCStat;
import pcgen.core.PlayerCharacter;
import pcgen.core.Race;
import pcgen.core.SettingsHandler;
import pcgen.core.StatList;
import pcgen.gui.filter.FilterAdapterPanel;
import pcgen.gui.filter.FilterFactory;
import pcgen.util.Delta;
import pcgen.util.PropertyFactory;

/**
 * <code>InfoSummary</code> is a panel which allows the user to enter
 * basic data about a character.
 *
 * @author James Dempsey <jdempsey@users.sourceforge.net>
 * @version $Revision: 1.1 $
 */
final class InfoSummary extends FilterAdapterPanel
{
	private static PlayerCharacter aPC = Globals.getCurrentPC();
	static boolean needsUpdate = true;
	private InfoAbilities infoAbilities = new InfoAbilities();
	private JFrame abilitiesFrame = new JFrame(PropertyFactory.getString("in_abilities"));
	private boolean abilitiesFrameHasBeenSized = false;
	private JPanel northPanel = new JPanel();
	private JPanel levelPanel = new JPanel();

	private JLabel labelName = null;
	protected JTextField pcNameText = new JTextField("");
	private JTextField tabNameText = new JTextField("");
	private JTextField playerNameText = new JTextField("");

	private JPanel poolPanel = new JPanel();
	private JLabel poolLabel = new JLabel("Stat Cost:");
	private JLabel poolText = new JLabel();
	private JButton jButtonHP = new JButton();
	private JLabel labelHP = new JLabel();

	private JLabel labelAlignment = null;
	private JComboBox alignmentComboBox = new JComboBox();
	private String[] alignmentStrings;
	private JLabel labelRace = null;
	private JComboBox raceComboBox = new JComboBox();
	private JLabel labelClass = null;
	private JComboBox classComboBox = new JComboBox();

	private WholeNumberField levelText = new WholeNumberField(1, 3);
	private JButton lvlUpButton = new JButton("+");
	private JButton lvlDownButton = new JButton("-");

	private RaceComboModel raceComboModel = null;		// Model for the race combo box.
	private ClassComboModel classComboModel = null;	// Model for the race combo box.
	private ClassModel pcClassTreeModel = null;		// Model for the pcClassTable.
	private JTableEx pcClassTable;				// Contains the PC's current classes and levels

	private static final int COL_PCLEVEL = 0;
	private static final int COL_CLASSNAME = 1;
	private static final int COL_SRC = 2;

	private JButton randName;
	private JButton abilitiesButton;

	private JLabelPane infoPane = new JLabelPane();
	private JLabelPane tipsPane = new JLabelPane();
	private JLabelPane statPane = new JLabelPane();

	private NameGui nameFrame = null;
	private Border etched;

	private JTableEx statTable;
	private StatTableModel statTableModel = new StatTableModel();
	private RendererEditor plusMinusRenderer = new RendererEditor();

	private JLabel lblMonsterlHD = new JLabel();
	private JLabel txtMonsterlHD = new JLabel();
	private JLabel lblHDModify = new JLabel();
	private JPanel pnlHD = new JPanel();
	private WholeNumberField txtHD = new WholeNumberField(1, 3);
	private JButton btnAddHD = new JButton("+");
	private JButton btnRemoveHD = new JButton("-");

	/**
	 * The listener for when the PC name has been changed so the
	 * PC can be updated.
	 */
	private FocusAdapter pcNameAdapter = new FocusAdapter()
	{
		public void focusLost(FocusEvent evt)
		{
			final String entry = pcNameText.getText();
			if ((entry != null) && (!entry.equals(aPC.getName())))
			{
				aPC.setDirty(true);
				aPC.setName(entry);
				Globals.getRootFrame().forceUpdate_PlayerTabs();
			}
		}
	};

	/**
	 * The listener for when the random button is pressed to generate
	 * a random name.
	 */
	private ActionListener randNameListener = new ActionListener()
	{
		public void actionPerformed(ActionEvent evt)
		{
			if (nameFrame == null)
			{
				nameFrame = new NameGui();
			}
			nameFrame.setVisible(true);
		}
	};

	/**
	 * The listener for when the PC alignment has been changed so the
	 * PC can be updated.
	 */
	private ActionListener alignmentListener = new ActionListener()
	{
		public void actionPerformed(ActionEvent evt)
		{
			if (alignmentComboBox.getSelectedItem() != null)
			{
				alignmentChanged();
			}
		}
	};

	/**
	 * The listener for when the PC abilities have been changed so the
	 * PC can be updated.
	 */
	private ActionListener abilitiesListener = new ActionListener()
	{
		public void actionPerformed(ActionEvent evt)
		{
			if (!abilitiesFrameHasBeenSized)
			{
				Dimension screenSize = PCGen_Frame1.getCharacterPane().getParent().getParent().getSize();
				int screenHeight = screenSize.height;
				int screenWidth = screenSize.width;

				abilitiesFrame.setSize(screenWidth, screenHeight);
				abilitiesFrameHasBeenSized = true;
			}
			abilitiesFrame.setVisible(true);
			infoAbilities.updateCharacterInfo();
		}
	};

	/**
	 * The listener for when the user moves through the race list
	 * so the description text can be updated.
	 */
	private ActionListener raceListener = new ActionListener()
	{
		/**
		 *  Update the info label when the user changes the race that is
		 * selected in the combo box. Setting the character's race is
		 * handled on a lost focus event now.
		 *
		 * @param  evt  The ActionEvent
		 */
		public void actionPerformed(ActionEvent evt)
		{
			final Race race = (Race) raceComboBox.getSelectedItem();
			if (race != null)
			{
				setInfoLabelText(race);
			}
		}
	};

	/**
	 * The listener for when the PC race has been changed so the
	 * PC can be updated.
	 */
	private FocusAdapter raceFocusListener = new FocusAdapter()
	{
		/**
		 *  Update character's race when the user moves away from
		 * the race combo box.
		 *
		 * @param  evt  The FocusEvent
		 */
		public void focusLost(FocusEvent evt)
		{
			// Temporary focus lost means something like the drop-down has
			// got focus
			if (evt.isTemporary())
			{
				return;
			}

			// Focus was really lost; update the race

			///////////////////////////////
			// If user needs to select a hitpoint value from the popup list, then
			// when running Java 1.3 racecombo doesn't loose focus. This causes
			// the race to revert to the previously selected value upon return to
			// the summary tab. Running updateRace in a thread appears to fix this...
			//
			// Byngl - November 19, 2002
			//
			(new Thread(new Runnable()
			{
				public void run()
				{
					updateRace();
				}
			})).start();
			///////////////////////////////
		}
	};

	/**
	 * The listener for when the PC classes has been changed so the
	 * PC can be updated.
	 */
	private ActionListener classListener = new ActionListener()
	{
		public void actionPerformed(ActionEvent evt)
		{
			if (classComboBox.getSelectedItem() != null)
			{
				final PCClass pcClass = (PCClass) classComboBox.getSelectedItem();
				setInfoLabelText(pcClass);
				if (pcClass.isQualified())
				{
					labelClass.setForeground(Color.black);
				}
				else
				{
					labelClass.setForeground(Color.red);
				}
			}
		}
	};

	/**
	 * The listener for when a level is added to or removed from the
	 * PC controlling whether it can be updated.
	 */
	private ActionListener levelCmdListener = new ActionListener()
	{
		public void actionPerformed(ActionEvent evt)
		{
			int numLevels = levelText.getValue();
			if (numLevels <= 0)
			{
				JOptionPane.showMessageDialog(null, "Number of levels must be positive.", Constants.s_APPNAME, JOptionPane.ERROR_MESSAGE);
				return;
			}
			PCClass pcClass = (PCClass) classComboBox.getSelectedItem();
			if (pcClass == null)
			{
				JOptionPane.showMessageDialog(null, "You must select a class.", Constants.s_APPNAME, JOptionPane.ERROR_MESSAGE);
				return;
			}
			if (!pcClass.isQualified())
			{
				JOptionPane.showMessageDialog(null, "You are not qualified to take the class " + pcClass.getName() + ".", Constants.s_APPNAME, JOptionPane.ERROR_MESSAGE);
				return;
			}

			// Now we make it negative to remove levels
			if (evt.getSource() == lvlDownButton)
			{
				numLevels = numLevels * -1;
			}

			addClass(pcClass, numLevels);
			pcClassTreeModel.fireTableDataChanged();
		}
	};

	/**
	 * The listener for when the PC tab name has been changed so the
	 * PC can be updated.
	 */
	private FocusAdapter tabNameAdapter = new FocusAdapter()
	{
		public void focusLost(FocusEvent evt)
		{
			String entry = tabNameText.getText();
			if ((entry != null) && (!entry.equals(aPC.getTabName())))
			{
				aPC.setDirty(true);
				aPC.setTabName(entry);
			}
		}
	};

	/**
	 * The listener for when the player name has been changed so the
	 * PC can be updated.
	 */
	private FocusAdapter playerNameAdapter = new FocusAdapter()
	{
		public void focusLost(FocusEvent evt)
		{
			String entry = playerNameText.getText();
			if ((entry != null) && (!entry.equals(aPC.getPlayersName())))
			{
				aPC.setDirty(true);
				aPC.setPlayersName(entry);
			}
		}
	};

	/**
	 * InfoSummary default constructor.
	 */
	public InfoSummary()
	{
		// do not remove this
		// we will use the component's name to save component specific settings
		setName("Summary");

		// Build the GUI components
		initComponents();

		// Restore filter settings.  Note that this will register listeners.
		FilterFactory.restoreFilterSettings(this);
	}

	/**
	 * This method converts the global alignment list into an array of Strings
	 * to be used on the alignment menu.
	 * @return String[] containing the names of the alignments
	 */
	private String[] populateAlignmentStrings()
	{
		alignmentStrings = new String[Globals.getAlignmentList().size()];
		for (int i = 0; i < Globals.getAlignmentList().size(); ++i)
		{
			alignmentStrings[i] = Globals.getLongAlignmentAtIndex(i);
		}
		return alignmentStrings;
	}

	/**
	 * This method initializes the GUI components.
	 */
	private void initComponents()
	{
		// Layout the stats table
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
		int width = Globals.getCustColumnWidth("AbilitiesS", 0);
		if (width == 0)
		{
			col.setPreferredWidth(50);
		}
		else
		{
			col.setPreferredWidth(width);
		}
		col.addPropertyChangeListener(new ResizeColumnListener(statTable, "AbilitiesS", 0));
		col.setCellRenderer(new AlignCellRenderer(SwingConstants.LEFT));
		col = statTable.getColumnModel().getColumn(1);
		width = Globals.getCustColumnWidth("AbilitiesS", 1);
		if (width == 0)
		{
			col.setPreferredWidth(40);
		}
		else
		{
			col.setPreferredWidth(width);
		}
		col.addPropertyChangeListener(new ResizeColumnListener(statTable, "AbilitiesS", 1));
		col.setCellRenderer(new AlignCellRenderer(SwingConstants.CENTER));
		col = statTable.getColumnModel().getColumn(2);
		width = Globals.getCustColumnWidth("AbilitiesS", 2);
		if (width == 0)
		{
			col.setPreferredWidth(30);
		}
		else
		{
			col.setPreferredWidth(width);
		}
		col.addPropertyChangeListener(new ResizeColumnListener(statTable, "AbilitiesS", 2));
		col.setCellRenderer(new AlignCellRenderer(SwingConstants.CENTER));
		col = statTable.getColumnModel().getColumn(3);
		width = Globals.getCustColumnWidth("AbilitiesS", 3);
		if (width == 0)
		{
			col.setPreferredWidth(40);
		}
		else
		{
			col.setPreferredWidth(width);
		}
		col.addPropertyChangeListener(new ResizeColumnListener(statTable, "AbilitiesS", 3));
		col.setCellRenderer(new AlignCellRenderer(SwingConstants.CENTER));
		col = statTable.getColumnModel().getColumn(4);
		width = Globals.getCustColumnWidth("AbilitiesS", 4);
		if (width == 0)
		{
			col.setPreferredWidth(30);
		}
		else
		{
			col.setPreferredWidth(width);
		}
		col.addPropertyChangeListener(new ResizeColumnListener(statTable, "AbilitiesS", 4));
		col.setCellRenderer(new AlignCellRenderer(SwingConstants.CENTER));
		col = statTable.getColumnModel().getColumn(5);
		col.setCellRenderer(plusMinusRenderer);
		col.setMaxWidth(30);
		col.setMinWidth(30);
		col = statTable.getColumnModel().getColumn(6);
		col.setCellRenderer(plusMinusRenderer);
		col.setMaxWidth(30);
		col.setMinWidth(30);

		statScrollPane.setViewportView(statTable);

		GridBagLayout gridbag = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		StringBuffer tipsText;
		String tip;
		int tipNum;
		northPanel.setLayout(gridbag);
		c.insets = new Insets(2, 2, 2, 2);

		createModels();
		createTreeTables();

		// Layout the first column
		Utility.buildConstraints(c, 0, 0, 1, 1, 0, 0);
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.EAST;
		labelName = new JLabel(PropertyFactory.getString("in_sumCharString") + ": ");
		gridbag.setConstraints(labelName, c);
		northPanel.add(labelName);

		Utility.buildConstraints(c, 1, 0, 1, 1, 3, 0);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.WEST;
		gridbag.setConstraints(pcNameText, c);
		northPanel.add(pcNameText);

		//Utility.buildConstraints(c, 0, 1, 1, 1, 0, 0);
		//c.fill = GridBagConstraints.NONE;
		//c.anchor = GridBagConstraints.EAST;

		Utility.buildConstraints(c, 1, 1, 1, 1, 0, 0);
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.WEST;
		randName = new JButton(PropertyFactory.getString("in_sumRandomNameString"));
		gridbag.setConstraints(randName, c);
		northPanel.add(randName);
		Utility.setDescription(randName, PropertyFactory.getString("in_randNameTipString"));

		Utility.buildConstraints(c, 0, 2, 1, 1, 0, 0);
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.EAST;
		JLabel tabLabel = new JLabel(PropertyFactory.getString("in_tabString") + ": ");
		gridbag.setConstraints(tabLabel, c);
		northPanel.add(tabLabel);

		Utility.buildConstraints(c, 1, 2, 1, 1, 0, 0);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.WEST;
		gridbag.setConstraints(tabNameText, c);
		northPanel.add(tabNameText);
		tabNameText.addFocusListener(tabNameAdapter);

		Utility.buildConstraints(c, 0, 3, 1, 1, 0, 0);
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.EAST;
		JLabel label = new JLabel(PropertyFactory.getString("in_sumPlayerString") + ": ");
		gridbag.setConstraints(label, c);
		northPanel.add(label);

		Utility.buildConstraints(c, 1, 3, 1, 1, 0, 0);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.WEST;
		gridbag.setConstraints(playerNameText, c);
		northPanel.add(playerNameText);
		playerNameText.addFocusListener(playerNameAdapter);

		// Layout the second column
		Utility.buildConstraints(c, 3, 0, 1, 1, 0, 0);
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.EAST;
		labelAlignment = new JLabel(PropertyFactory.getString("in_alignString") + ": ");
		gridbag.setConstraints(labelAlignment, c);
		northPanel.add(labelAlignment);

		Utility.buildConstraints(c, 4, 0, 1, 1, 2, 0);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.WEST;
		gridbag.setConstraints(alignmentComboBox, c);
		northPanel.add(alignmentComboBox);

		alignmentComboBox.setModel(new DefaultComboBoxModel(populateAlignmentStrings()));

		Utility.buildConstraints(c, 3, 1, 1, 1, 0, 0);
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.EAST;
		labelRace = new JLabel(PropertyFactory.getString("in_raceString") + ": ");
		gridbag.setConstraints(labelRace, c);
		northPanel.add(labelRace);

		Utility.buildConstraints(c, 4, 1, 1, 1, 0, 0);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.WEST;
		gridbag.setConstraints(raceComboBox, c);
		northPanel.add(raceComboBox);

		raceComboModel = new RaceComboModel();
		raceComboBox.setModel(raceComboModel);

		Utility.buildConstraints(c, 3, 2, 1, 1, 0, 0);
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.EAST;
		labelClass = new JLabel(PropertyFactory.getString("in_classString") + ": ");
		gridbag.setConstraints(labelClass, c);
		northPanel.add(labelClass);

		Utility.buildConstraints(c, 4, 2, 1, 1, 0, 0);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.WEST;
		gridbag.setConstraints(classComboBox, c);
		northPanel.add(classComboBox);

		classComboModel = new ClassComboModel();
		classComboBox.setModel(classComboModel);

		// Layout for the level panel
		Utility.buildConstraints(c, 3, 3, 1, 1, 0, 0);
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.EAST;
		label = new JLabel(PropertyFactory.getString("in_levelString") + ": ");
		gridbag.setConstraints(label, c);
		northPanel.add(label);

		Utility.buildConstraints(c, 4, 3, 1, 1, 0, 0);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.WEST;
		//label = new JLabel(PropertyFactory.getString("in_levelString") + ": ");
		Utility.setDescription(lvlDownButton, PropertyFactory.getString("in_levelDownButtonTooltip"));
		Utility.setDescription(lvlUpButton, PropertyFactory.getString("in_levelUpButtonTooltip"));
		Utility.setDescription(levelText, PropertyFactory.getString("in_levelTextTooltip"));
		//levelPanel.add(label);
		levelPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		levelPanel.add(levelText);
		levelPanel.add(lvlUpButton);
		levelPanel.add(lvlDownButton);
		gridbag.setConstraints(levelPanel, c);
		northPanel.add(levelPanel);

/////////////////////////
		Utility.buildConstraints(c, 0, 4, 1, 1, 0, 0);
		lblMonsterlHD.setText(PropertyFactory.getString("in_sumMonsterHitDice"));
		c.anchor = GridBagConstraints.EAST;
		northPanel.add(lblMonsterlHD, c);

		Utility.buildConstraints(c, 1, 4, 1, 1, 0, 0);
		txtMonsterlHD.setText("0");
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.WEST;
		northPanel.add(txtMonsterlHD, c);

		Utility.buildConstraints(c, 3, 4, 1, 1, 0, 0);
		lblHDModify.setText(PropertyFactory.getString("in_sumHDToAddRem"));
		c.anchor = GridBagConstraints.EAST;
		northPanel.add(lblHDModify, c);

		pnlHD.setLayout(new FlowLayout(FlowLayout.LEFT));
		pnlHD.add(txtHD);
		pnlHD.add(btnAddHD);
		pnlHD.add(btnRemoveHD);

		Utility.buildConstraints(c, 4, 4, 1, 1, 0, 0);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.WEST;
		northPanel.add(pnlHD, c);




		//pnlHD.add(lblHD);

		btnAddHD.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				addMonsterHD(1);
			}
		});
		btnRemoveHD.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				addMonsterHD(-1);
			}
		});
/////////////////////////

		// Layout the Stats table
		Utility.buildConstraints(c, 0, 5, 5, 2, 0, 18);
		c.fill = GridBagConstraints.BOTH;
		c.anchor = GridBagConstraints.WEST;
		JPanel statPanel = new JPanel();
		statPanel.setLayout(new BorderLayout());
		statPanel.add(statScrollPane, BorderLayout.CENTER);

		poolPanel.add(poolLabel);
		poolText.setPreferredSize(new Dimension(60, 20));
		poolPanel.add(poolText);
		jButtonHP.setText(Globals.getGameModeHPAbbrev());
		jButtonHP.setAlignmentY(0.0F);
		jButtonHP.setHorizontalAlignment(SwingConstants.LEFT);
		jButtonHP.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				pcGenGUI.showHpFrame();
			}
		});
		poolPanel.add(jButtonHP);
		labelHP.setText("");
		labelHP.setHorizontalAlignment(SwingConstants.TRAILING);
		poolPanel.add(labelHP);
		statPanel.add(poolPanel, BorderLayout.SOUTH);
		gridbag.setConstraints(statPanel, c);

		northPanel.add(statPanel);

		// Layout the tips pane
		tipsPane.setBackground(northPanel.getBackground());
		tipsPane.setContentType("text/html");
		tipsText = new StringBuffer("<html><body><UL>");
		tipNum = 1;
		String tipName = "in_summaryTip" + String.valueOf(tipNum);
		tip = PropertyFactory.getString(tipName);
		while ((tip != null) && !tip.equals(tipName + PropertyFactory.UNDEFINED))
		{
			tipsText.append("<LI>").append(tip);
			tipName = "in_summaryTip" + String.valueOf(++tipNum);
			tip = PropertyFactory.getString(tipName);
		}
		tipsText.append("</UL></body></html>");
		tipsPane.setText(tipsText.toString());
		tipsPane.setEditable(false);
		JScrollPane scroll = new JScrollPane();
		scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scroll.setViewportView(tipsPane);
		scroll.getViewport().setScrollMode(JViewport.BLIT_SCROLL_MODE);
		scroll.setBackground(new Color(255, 255, 255));
		JPanel pane1 = new JPanel();
		pane1.add(scroll);
		TitledBorder title1 = BorderFactory.createTitledBorder(etched, PropertyFactory.getString("in_tipsString"));
		title1.setTitleJustification(TitledBorder.CENTER);
		pane1.setBorder(title1);
		pane1.setLayout(new BoxLayout(pane1, BoxLayout.Y_AXIS));

		Utility.buildConstraints(c, 0, 7, 5, 1, 0, 18);
		c.fill = GridBagConstraints.BOTH;
		c.anchor = GridBagConstraints.CENTER;
		gridbag.setConstraints(pane1, c);
		northPanel.add(pane1);

		// Layout the info pane
		infoPane.setBackground(northPanel.getBackground());
		infoPane.setContentType("text/html");
		infoPane.setText("");
		infoPane.setEditable(false);
		JScrollPane scrol2 = new JScrollPane();
		scrol2.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		scrol2.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrol2.setViewportView(infoPane);
		scrol2.getViewport().setScrollMode(JViewport.BLIT_SCROLL_MODE);
		scrol2.setBackground(northPanel.getBackground());
		Utility.setDescription(infoPane, "Any requirements you don't meet are in italics.");
		JPanel pane2 = new JPanel();
		pane2.add(scrol2);
		TitledBorder title2 = BorderFactory.createTitledBorder(etched, PropertyFactory.getString("in_infoString"));
		title2.setTitleJustification(TitledBorder.CENTER);
		pane2.setBorder(title2);
		pane2.setLayout(new BoxLayout(pane2, BoxLayout.X_AXIS));

		Utility.buildConstraints(c, 5, 0, 1, 5, 9, 0);
		c.fill = GridBagConstraints.BOTH;
		c.anchor = GridBagConstraints.CENTER;
		gridbag.setConstraints(pane2, c);
		northPanel.add(pane2);

		// Layout for the Classes table
		col = pcClassTable.getColumnModel().getColumn(COL_PCLEVEL);
		col.setPreferredWidth(15);
		col.setCellRenderer(new AlignCellRenderer(SwingConstants.CENTER));

		Utility.buildConstraints(c, 5, 5, 1, 1, 0, 18);
		c.fill = GridBagConstraints.BOTH;
		c.anchor = GridBagConstraints.CENTER;
		JScrollPane scrollPane = new JScrollPane(pcClassTable);
		JPanel pane3 = new JPanel();
		pane3.add(scrollPane);
		TitledBorder title3 = BorderFactory.createTitledBorder(etched, PropertyFactory.getString("in_classesString"));
		title3.setTitleJustification(TitledBorder.CENTER);
		pane3.setBorder(title3);
		pane3.setLayout(new BoxLayout(pane3, BoxLayout.X_AXIS));
		gridbag.setConstraints(pane3, c);
		northPanel.add(pane3);

		// Abilities button
		if (!SettingsHandler.isAbilitiesShownAsATab())
		{
			Utility.buildConstraints(c, 5, 6, 1, 1, 0, 0);
			c.fill = GridBagConstraints.NONE;
			c.anchor = GridBagConstraints.CENTER;
			abilitiesButton = new JButton(PropertyFactory.getString("in_abilities"));
			gridbag.setConstraints(abilitiesButton, c);
			northPanel.add(abilitiesButton);

			abilitiesFrame.getContentPane().setLayout(new BorderLayout());
			abilitiesFrame.getContentPane().add(infoAbilities, BorderLayout.CENTER);
			ClassLoader loader = getClass().getClassLoader();
			Toolkit kit = Toolkit.getDefaultToolkit();
			// according to the API, the following should *ALWAYS* use '/'
			Image img = kit.getImage(loader.getResource("pcgen/gui/resource/PcgenIcon.gif"));
			loader = null;
			abilitiesFrame.setIconImage(img);
		}

		// Layout the stats pane
		statPane.setBackground(northPanel.getBackground());
		statPane.setContentType("text/html");
		statPane.setText("");
		statPane.setEditable(false);
		JScrollPane statsScroll = new JScrollPane();
		statsScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		statsScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		statsScroll.setViewportView(statPane);
		statsScroll.getViewport().setScrollMode(JViewport.BLIT_SCROLL_MODE);
		statsScroll.setBackground(new Color(255, 255, 255));
		JPanel pane4 = new JPanel();
		pane4.add(statsScroll);
		TitledBorder title4 = BorderFactory.createTitledBorder(etched, PropertyFactory.getString("in_statsString"));
		title4.setTitleJustification(TitledBorder.CENTER);
		pane4.setBorder(title4);
		pane4.setLayout(new BoxLayout(pane4, BoxLayout.Y_AXIS));
		Utility.buildConstraints(c, 5, 7, 1, 1, 0, 0);
		c.fill = GridBagConstraints.BOTH;
		c.anchor = GridBagConstraints.CENTER;
		gridbag.setConstraints(pane4, c);
		northPanel.add(pane4);

		this.setLayout(new BorderLayout());
		this.add(northPanel, BorderLayout.CENTER);

		addComponentListener(new ComponentAdapter()
		{
			public void componentShown(ComponentEvent evt)
			{
				formComponentShown(evt);
			}
		});
	}

	/**
	 * This is called when the tab is shown.
	 */
	private void formComponentShown(ComponentEvent evt)
	{
		requestFocus();
		Globals.getRootFrame().getStatusBar().setText("");

		updateCharacterInfo();
	}

	/**
	 * This method updates the display label containing the current HP.
	 */
	public void updateHP()
	{
		labelHP.setText(String.valueOf(aPC.hitPoints()));
	}

	/**
	 * This method updates the local reference to the currently selected
	 * character and updates the displayed information.
	 */
	private void updateCharacterInfo()
	{
		lblMonsterlHD.setVisible(SettingsHandler.hideMonsterClasses());
		txtMonsterlHD.setVisible(SettingsHandler.hideMonsterClasses());
		lblHDModify.setVisible(SettingsHandler.hideMonsterClasses());
		pnlHD.setVisible(SettingsHandler.hideMonsterClasses());

		aPC = Globals.getCurrentPC();
		if (aPC == null)
		{
			return;
		}

		updateHP();
		if (Globals.getGameModeAlignmentText().length() == 0)
		{
			labelAlignment.setVisible(false);
			alignmentComboBox.setVisible(false);
		}
		else
		{
			labelAlignment.setVisible(true);
			alignmentComboBox.setVisible(true);
		}

		if (pnlHD.isVisible())
		{
			updateHD();
			txtHD.setValue(1);
		}

		levelText.setValue(1);
		needsUpdate = false;

		refreshDisplay();
	}

	private void updateHD()
	{
		int monsterHD = -1;
		int minLevel = 0;
		if (aPC != null)
		{
			final String monsterClass = aPC.getRace().getMonsterClass(false);
			if (monsterClass != null)
			{
				monsterHD = aPC.getRace().hitDice();
				minLevel = aPC.getRace().hitDice() + aPC.getRace().getMonsterClassLevels();
				final PCClass aClass = aPC.getClassNamed(monsterClass);
				if (aClass != null)
				{
					monsterHD += aClass.getLevel();
				}
			}
		}
		btnAddHD.setEnabled(aPC.getRace().hasAdvancement() && (monsterHD >= 0));
		btnRemoveHD.setEnabled(monsterHD > minLevel);
		if (monsterHD < 0)
		{
			monsterHD = 0;
		}
		txtMonsterlHD.setText(Integer.toString(monsterHD));
		txtHD.setEnabled(btnAddHD.isEnabled() | btnRemoveHD.isEnabled());
	}

	public static void setNeedsUpdate(boolean flag)
	{
		needsUpdate = flag;
	}

	/**
	 * This method adds the listeners to the components on the tab.
	 */
	private void startListeners()
	{
		pcNameText.addFocusListener(pcNameAdapter);
		randName.addActionListener(randNameListener);
		alignmentComboBox.addActionListener(alignmentListener);
		raceComboBox.addActionListener(raceListener);
		raceComboBox.addFocusListener(raceFocusListener);
		classComboBox.addActionListener(classListener);
		tabNameText.addFocusListener(tabNameAdapter);
		playerNameText.addFocusListener(playerNameAdapter);
		lvlDownButton.addActionListener(levelCmdListener);
		lvlUpButton.addActionListener(levelCmdListener);
		if (!SettingsHandler.isAbilitiesShownAsATab())
		{
			abilitiesButton.addActionListener(abilitiesListener);
		}
	}

	/**
	 * This method removes the listeners from the components on the tab.
	 */
	private void stopListeners()
	{
		pcNameText.removeFocusListener(pcNameAdapter);
		randName.removeActionListener(randNameListener);
		alignmentComboBox.removeActionListener(alignmentListener);
		raceComboBox.removeActionListener(raceListener);
		raceComboBox.removeFocusListener(raceFocusListener);
		classComboBox.removeActionListener(classListener);
		tabNameText.removeFocusListener(tabNameAdapter);
		playerNameText.removeFocusListener(playerNameAdapter);
		lvlDownButton.removeActionListener(levelCmdListener);
		lvlUpButton.removeActionListener(levelCmdListener);
		if (!SettingsHandler.isAbilitiesShownAsATab())
		{
			abilitiesButton.removeActionListener(abilitiesListener);
		}
	}

	/**
	 * This method is invoked when the mouse is clicked on the stat table.
	 * If the requested change is valid based on the rules mode selected,
	 * it performs the update on the character stat and forces the rest of
	 * the connected items to update.
	 */
	private void statTableMouseClicked(MouseEvent evt)
	{
		final int selectedStat = statTable.getSelectedRow();
		int stat = aPC.getStatList().getBaseStatFor(Globals.s_ATTRIBSHORT[selectedStat]);
		boolean makeChange = false;
		boolean checkPurchase = false;
		int increment = 0;

		final int column = statTable.columnAtPoint(evt.getPoint());
		switch (column)
		{
			case 0:
			case 1:
			case 2:
			case 3:
			case 4:
				break;

			case 5:
				{
					increment = 1;
					final int pcTotalLevels = aPC.getTotalLevels();
					final boolean isPurchaseMode = SettingsHandler.isPurchaseStatMode();
					if (aPC.isNonability(selectedStat))
					{
						JOptionPane.showMessageDialog(null, "Cannot increment a nonability", Constants.s_APPNAME, JOptionPane.ERROR_MESSAGE);
					}
					else if ((pcTotalLevels < 1) && (stat >= SettingsHandler.getPurchaseScoreMax()) && isPurchaseMode)
					{
						JOptionPane.showMessageDialog(null, "Cannot raise stat above " + SettingsHandler.getPurchaseScoreMax() + " in Purchase Mode", Constants.s_APPNAME, JOptionPane.ERROR_MESSAGE);
					}
					else
					{
						makeChange = true;
						if (isPurchaseMode && (pcTotalLevels == 0))
						{
							checkPurchase = true;
						}
						else if (!isPurchaseMode || pcTotalLevels > 0)
						{
							aPC.setPoolAmount(Math.max(aPC.getPoolAmount() - 1, 0));
						}
					}
				}
				break;

			case 6:
				{
					increment = -1;
					final int minPurchaseScore = SettingsHandler.getPurchaseModeBaseStatScore();
					final int pcTotalLevels = aPC.getTotalLevels();
					final boolean isPurchaseMode = SettingsHandler.isPurchaseStatMode();
					if (stat <= 0)
					{
						JOptionPane.showMessageDialog(null, "Cannot lower stat below 0", Constants.s_APPNAME, JOptionPane.ERROR_MESSAGE);
					}
					else if (aPC.isNonability(selectedStat))
					{
						JOptionPane.showMessageDialog(null, "Cannot decrement a nonability", Constants.s_APPNAME, JOptionPane.ERROR_MESSAGE);
					}
					else if ((pcTotalLevels < 1) && (stat <= minPurchaseScore) && isPurchaseMode)
					{
						JOptionPane.showMessageDialog(null, "Cannot lower stat below " + minPurchaseScore + " in Purchase Mode", Constants.s_APPNAME, JOptionPane.ERROR_MESSAGE);
					}
					else
					{
						makeChange = true;
						if (!isPurchaseMode || pcTotalLevels > 0)
						{
							aPC.setPoolAmount(aPC.getPoolAmount() + 1);
						}
					}
				}
				break;
			default:
				Globals.errorPrint("In InfoSummary.statTableMouseClicked the column " + column + " is not handled.");
				break;
		}

		if (makeChange)
		{
			aPC.setDirty(true);
			final PCStat aStat = (PCStat) aPC.getStatList().getStats().get(selectedStat);
			aStat.setBaseScore(stat + increment);
			aPC.saveStatIncrease(aStat.getAbb(), increment, false);

			updatePool(increment > 0);
			statTableModel.fireTableRowsUpdated(selectedStat, selectedStat);
			setStatLabelText();
			final PCGen_Frame1 rootFrame = Globals.getRootFrame();
			if ((int) aPC.getStatBonusTo("HP", "BONUS") != 0)
			{
				rootFrame.hpTotal_Changed();
			}

			// if INT changed then skill points need recalc
			// and there are all kinds of skills that have bonus
			// associated with stats, so just update the mother
			Globals.getRootFrame().forceUpdate_InfoSkills();

			// I could check for INT, WIS and CHA here, but then
			// there would probably be some custom class that uses
			// DEX or CON for spell info and that would be wack
			// so just update the mother
			Globals.getRootFrame().forceUpdate_InfoSpells();
		}
	}

	/**
	 * This method gets the number of stat points used in the pool
	 */
	private static int getUsedStatPool()
	{
		int i = 0;
		for (int stat = 0; stat < Globals.s_ATTRIBLONG.length; ++stat)
		{
			int statValue = aPC.getStatList().getBaseStatFor(Globals.s_ATTRIBSHORT[stat]);
			if (!Globals.s_ATTRIBROLL[stat])
			{
				continue;
			}
			if (statValue > SettingsHandler.getPurchaseScoreMax())
			{
				statValue = SettingsHandler.getPurchaseScoreMax();
			}
			if (statValue >= SettingsHandler.getPurchaseScoreMin())
			{
				i += SettingsHandler.getAbilityScoreCost(statValue - SettingsHandler.getPurchaseScoreMin());
			}
		}
		return i;
	}

	/**
	 * This method updates the purchase point pool.
	 * @param checkPurchasePoints boolean true if the pool should be checked
	 * for available points before doing the update.
	 */
	private void updatePool(boolean checkPurchasePoints)
	{
		final int usedStatPool = getUsedStatPool();
		final int pcTotalLevels = aPC.getTotalLevels();
		if (pcTotalLevels < 2)
		{
			aPC.setDirty(true);
			aPC.setCostPool(usedStatPool);
			aPC.setPoolAmount(usedStatPool);
		}

		// Handle purchase mode for stats
		if (SettingsHandler.isPurchaseStatMode())
		{
			final String bString = String.valueOf(aPC.getCostPool());
			poolLabel.setText("Stat Cost:");
			poolText.setText(bString);

			if (checkPurchasePoints)
			{
				//
				// Let the user know that they've exceded their goal, but allow them to keep going if they want...
				// Only do this at 1st level or lower
				//
				final int availablePool = SettingsHandler.getPurchaseModeMethodPool();
				if ((pcTotalLevels < 2) && (availablePool > 0) && (usedStatPool > availablePool))
				{
					JOptionPane.showMessageDialog(null, "You have exceded the maximum points of " + availablePool + " as specified by the method \"" + SettingsHandler.getPurchaseModeMethodName() + "\"", Constants.s_APPNAME, JOptionPane.INFORMATION_MESSAGE);
				}
			}

		}
		// Non-purchase mode for stats
		else
		{
			poolLabel.setText("Stat Total:");
			int statTotal = 0;
			int modTotal = 0;
			final StatList statList = aPC.getStatList();
			for (int i = 0; i < Globals.s_ATTRIBLONG.length; ++i)
			{
				if (aPC.isNonability(i) || !Globals.s_ATTRIBROLL[i])
				{
					continue;
				}
				final int currentStat = statList.getBaseStatFor(Globals.s_ATTRIBSHORT[i]);
				final int currentMod = statList.getStatModFor(Globals.s_ATTRIBSHORT[i]);

				statTotal += currentStat;
				modTotal += currentMod;
			}
			poolLabel.setText("Stat Total: " + Integer.toString(statTotal) + "  Modifier Total: " + Integer.toString(modTotal));
			poolText.setText("");
		}

	}

	/**
	 * This method is called by the listeners on the race combo box
	 * to handle a change.  It will only force updates to the GUI if
	 * a change was actually made to the character's race.
	 * author dhibbs aka Sage Sam
	 * @since 28 Oct 2002
	 */
	private void updateRace()
	{
		if (raceComboBox.getSelectedItem() != null)
		{
			final Race r = (Race) raceComboBox.getSelectedItem();
			final Race oldRace = aPC.getRace();

			if (!r.equals(oldRace))
			{
				//
				// Remove any monster class levels associated with old race (in excess of freebies)
				//
				if (pnlHD.isVisible())
				{
					final String monsterClass = oldRace.getMonsterClass(false);
					if (monsterClass != null)
					{
						final PCClass aClass = aPC.getClassNamed(monsterClass);
						if (aClass != null)
						{
							final int numLevels = aClass.getLevel() - oldRace.getMonsterClassLevels();
							if (numLevels > 0)
							{
								addClass(aClass, -numLevels);
							}
						}
					}
				}

				aPC.setDirty(true);
				aPC.setRace(r);
				if (pnlHD.isVisible())
				{
					updateHD();
				}
				Globals.getRootFrame().forceUpdate_PlayerTabs();
				Globals.getRootFrame().forceUpdate_InfoRace();
				Globals.getRootFrame().forceUpdate_InfoFeats();
				Globals.getRootFrame().forceUpdate_InfoSkills();
				Globals.getRootFrame().forceUpdate_InfoSpells();

				// If the either race was monstrous, natural weapons in the gear need
				// updated.  sage_sam 02 Dec 2002
				final boolean updateEquip = (((aPC.getRace() != null) && (aPC.getRace().getMonsterClass(false) != null)) || (r.getMonsterClass(false) != null));
				if (updateEquip)
				{
					Globals.getRootFrame().forceUpdate_InfoGear();
				}

				if (aPC.getRace().hitDice() != 0)
				{
					aPC.getRace().rollHP();
				}
				updateHP();
				refreshDisplay();
			}
		}
	}

	/**
	 * This method refreshes the display and everything shown in it.
	 */
	private void refreshDisplay()
	{
		if (aPC == null)
		{
			return;
		}

		stopListeners();

		pcNameText.setText(aPC.getName());
		tabNameText.setText(aPC.getTabName());
		playerNameText.setText(aPC.getPlayersName());

		boolean rebuild = false;
		if (alignmentStrings.length != Globals.getAlignmentList().size()) // - 1 DRH
		{
			rebuild = true;
		}
		else
		{
			String[] al = Globals.getAlignmentListStrings(true);
			for (int i = 0; i < Math.min(alignmentStrings.length, al.length); ++i)
			{
				if (!alignmentStrings[i].equals(al[i]))
				{
					rebuild = true;
				}
			}
		}

		if (rebuild)
		{
			alignmentComboBox.setModel(new DefaultComboBoxModel(populateAlignmentStrings()));
		}

		final int align = aPC.getAlignment();
		if (align > -1 && align < alignmentStrings.length)
		{
			alignmentComboBox.setSelectedIndex(align);
		}

		final Race pcRace = aPC.getRace();
		raceComboModel.setSelectedItem(pcRace);

		if (pcRace.passesPreReqTests())
		{
			labelRace.setForeground(Color.black);
		}
		else
		{
			labelRace.setForeground(Color.red);
		}
		setInfoLabelText(pcRace);

		labelClass.setForeground(Color.black);

		//
		// select the last class levelled
		//
		if (aPC.getLevelInfoSize() != 0)
		{
			final Object lastSelection = classComboBox.getSelectedItem();
			for (int idx = aPC.getLevelInfoSize() - 1; idx >= 0; --idx)
			{
				final PCClass pcClass = aPC.getClassKeyed(aPC.getLevelInfoClassKeyName(idx));
				if (pcClass != null)
				{
					classComboBox.setSelectedItem(Globals.getClassKeyed(pcClass.getKeyName()));
					if (classComboBox.getSelectedIndex() >= 0)
					{
						break;
					}
				}
			}
			//
			// If couldn't find a selection, then default back to the previous choice
			//
			if ((classComboBox.getSelectedIndex() < 0) && (lastSelection != null))
			{
				classComboBox.setSelectedItem(lastSelection);
			}
		}

		final PCClass pcSelectedClass = (PCClass) classComboBox.getSelectedItem();
		if ((pcSelectedClass != null) && !pcSelectedClass.isQualified())
		{
			labelClass.setForeground(Color.red);
		}

		createModels();
		statTableModel.fireTableDataChanged();
		pcClassTable.updateUI();

		updatePool(false);

		setStatLabelText();

		enableControls(!alignmentComboBox.isVisible() || align != Globals.getIndexOfAlignment(Constants.s_NONE));
		startListeners();
	}

	/**
	 * This method enables the controls that make changes to the character
	 */
	private final void enableControls(boolean enable)
	{
		raceComboBox.setEnabled(enable);
		classComboBox.setEnabled(enable);
		levelText.setEnabled(enable);
		lvlUpButton.setEnabled(enable);
		lvlDownButton.setEnabled(enable);
	}

	/**
	 * This method is called when a character's alignment is changed to validate
	 * the alignment matches those allowed for the character's classes
	 */
	private void alignmentChanged()
	{
		final int newAlignment = alignmentComboBox.getSelectedIndex();
		final int oldAlignment = aPC.getAlignment();
		if (newAlignment == oldAlignment)
		{
			return;
		}


		//
		// Get a list of classes that will become unqualified (and have an ex-class)
		//
		StringBuffer unqualified = new StringBuffer();
		ArrayList classList = aPC.getClassList();
		ArrayList exclassList = new ArrayList();
		for (Iterator e = classList.iterator(); e.hasNext();)
		{
			final PCClass aClass = (PCClass) e.next();

			aPC.setAlignment(oldAlignment, false, true);
			if (aClass.isQualified())
			{
				aPC.setAlignment(newAlignment, false, true);
				if (!aClass.isQualified() && (aClass.getExClass().length() != 0))
				{
					if (unqualified.length() > 0)
					{
						unqualified.append(", ");
					}
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
			if (JOptionPane.showConfirmDialog(null, "This will change the following class(es) to ex-class(es):" + Constants.s_LINE_SEP + unqualified.toString(), Constants.s_APPNAME, JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.CANCEL_OPTION)
			{
				aPC.setAlignment(oldAlignment, false, true);
				alignmentComboBox.setSelectedIndex(oldAlignment);
				return;
			}
		}

		//
		// Convert the class(es)
		//
		for (Iterator e = exclassList.iterator(); e.hasNext();)
		{
			final PCClass aClass = (PCClass) e.next();
			aPC.makeIntoExClass(aClass);
		}
		aPC.setDirty(true);
		aPC.setAlignment(newAlignment, false, true);
		needsUpdate = true;
		updateCharacterInfo();
		enableControls(newAlignment != Globals.getIndexOfAlignment(Constants.s_NONE));
	}

	/**
	 * This method sets the text in the race description field based on the race
	 * selected.
	 */
	private void setInfoLabelText(Race aRace)
	{
		StringBuffer b = new StringBuffer();
		b.append("<html>");
		if ((aRace != null) && !aRace.getName().startsWith("<none"))
		{
			b.append("<b>Race: ").append(aRace.getName()).append("</b>");
			b.append(" &nbsp;<b>TYPE</b>:").append(aRace.getType());
			final String cString = aRace.preReqHTMLStrings(false);
			if (cString.length() > 0)
			{
				b.append(" &nbsp;<b>Requirements</b>:").append(cString);
			}
			String bString = aRace.getSource();
			if (bString.length() > 0)
			{
				b.append(" &nbsp;<b>SOURCE</b>:").append(bString);
			}
			final StringBuffer aString = new StringBuffer();
			for (int i = 0; i < Globals.s_ATTRIBSHORT.length; ++i)
			{
				if (aRace.isNonability(i))
				{
					if (aString.length() > 0)
					{
						aString.append(' ');
					}
					aString.append(Globals.s_ATTRIBSHORT[i]).append(":Nonability");
				}
				else
				{
					if (aRace.getStatMod(i) != 0)
					{
						if (aString.length() > 0)
						{
							aString.append(' ');
						}
						aString.append(Globals.s_ATTRIBSHORT[i]).append(':').append(aRace.getStatMod(i));
					}
				}
			}
			if (aString.length() > 0)
			{
				b.append(" &nbsp;<b>STAT ADJ:</b>");
				b.append(String.valueOf(aString));
			}
			b.append(" &nbsp;<b>SIZE:</b>");
			b.append(aRace.getSize());
			if (aRace.getMovements() != null)
			{
				final StringBuffer movelabel = new StringBuffer(aRace.getMovementType(0)).append(' ').append(aRace.getMovement(0)).append(Globals.getAbbrMovementDisplay());
				for (int i = 1; i < aRace.getMovements().length; ++i)
				{
					movelabel.append(aRace.getMovementType(i));
					movelabel.append(' ').append(aRace.getMovement(i));
					movelabel.append(Globals.getAbbrMovementDisplay());
				}
				b.append(" &nbsp;<b>MOVE:</b>").append(String.valueOf(movelabel));
			}
			b.append(" &nbsp;<b>VISION:</b>").append(aRace.getDisplayVision());
			if (aRace.getFavoredClass().length() != 0)
			{
				b.append(" &nbsp;<b>FAVORED CLASS:</b>").append((!aRace.getFavoredClass().equals(".")) ? aRace.getFavoredClass() : "Various");
			}
			if (aRace.getLevelAdjustment() > 0)
			{
				b.append(" &nbsp;<b>LEVEL ADJ:</b>:").append(String.valueOf(aRace.getLevelAdjustment()));
			}
		}
		b.append("</html>");
		infoPane.setText(b.toString());
	}

	/**
	 * This method sets the text in the class description field based on the class
	 * selected.
	 */
	private void setInfoLabelText(PCClass aClass)
	{
		if (aClass != null)
		{
			StringBuffer b = new StringBuffer();
			b.append("<html><b>Class: ").append(aClass.getName()).append("</b>");
			b.append(" &nbsp;<b>TYPE</b>:").append(aClass.getType());
			final String cString = aClass.preReqHTMLStrings(false);
			if (cString.length() > 0)
			{
				b.append(" &nbsp;<b>Requirements</b>:").append(cString);
			}
			String bString = aClass.getSource();
			if (bString.length() > 0)
			{
				b.append(" <b>SOURCE</b>:").append(bString);
			}
			b.append(" <b>BAB:</b>").append(aClass.getAttackBonusType());
			b.append(" <b>HD:</b>1D").append(aClass.getHitDie());
			if (Globals.getGameModeDefenseText().length() != 0)
			{
				b.append(" <b>Defense:</b>").append(aClass.defenseString());
			}
			if (Globals.getGameModeReputationText().length() != 0)
			{
				b.append(" <b>Reputation:</b>").append(aClass.getReputationString());
			}
			if (Globals.getGameModeShowSpellTab())
			{
				b.append(" <b>SPELLTYPE:</b>").append(aClass.getSpellType());
				b.append(" <b>Base Stat:</b>").append(aClass.getSpellBaseStat());
			}

			b.append("</html>");
			infoPane.setText(b.toString());
		}
	}

	/**
	 * This method sets the HTML text used to display calculated stats such
	 * as AC, BAB, saves, etc.
	 */
	private void setStatLabelText()
	{
		int bonus = 0;
		StringBuffer statBuf = new StringBuffer();

		statBuf.append("<html>");
		if (aPC != null)
		{
			if (Globals.getGameModeDefenseText().length() != 0)
			{
				statBuf.append("<b>" + Globals.getGameModeDefenseAbbrev() + "</b> ");
				statBuf.append("<i>Total</i>: ").append(aPC.getDefenseTotal());
				statBuf.append(" <i>Flatfooted</i>: ").append(aPC.flatfootedDefense());
				statBuf.append(" <i>Touch</i>: ").append(aPC.touchDefense());
				statBuf.append("<br>");
			}
			else
			{
				statBuf.append("<b>Total AC:</b> ").append((int) aPC.getTotalBonusTo("COMBAT", "AC", true));
				statBuf.append("<br>");
			}

			final int initMod = aPC.initiativeMod();
			statBuf.append("<b>Init</b>: ").append(Delta.toString(initMod));
			bonus = aPC.baseAttackBonus();
			statBuf.append(" <b>BAB</b>: ").append(Delta.toString(bonus));
			statBuf.append(" <b>").append(Globals.getGameModeHPAbbrev()).append("</b>: ").append(aPC.hitPoints());
			if (Globals.getGameModeDefenseText().length() != 0)
			{
				statBuf.append(" <b>").append(Globals.getGameModeWoundPointsAbbrev()).append("</b>: ").append(aPC.woundPoints());
			}
			statBuf.append("<br>");

			statBuf.append("<b>Saves</b>: ");
			for (int z = 0; z < Globals.getCheckList().size(); ++z)
			{
				bonus = (int) aPC.getBonus(z + 1, true);
				statBuf.append(" <i>").append(Globals.getCheckList().get(z).toString()).append("</i>: ").append(Delta.toString(bonus));
			}

			if (Globals.isStarWarsMode())
			{
				statBuf.append("<br>");
				statBuf.append("<b>Force Points</b>: ").append(aPC.getStrFPoints());
				statBuf.append(" <b>Darkside Points</b>: ").append(aPC.getDPoints());
				statBuf.append(" <b>Reputation</b>: ").append(aPC.reputation());
			}
			//
			// Show character's current size
			//
			statBuf.append("<br><b>Size</b>: ").append(aPC.getSize());

		}
		statBuf.append("</html>");
		statPane.setText(statBuf.toString());
	}

	/**
	 * This method is called to add 1+ levels to a character.
	 * @param theClass PCClass to add to the character
	 * @param levels int number of levels of the class to add
	 */
	private void addClass(PCClass theClass, int levels)
	{
		if (Globals.getGameModeAlignmentText().length() != 0)
		{
			if ((levels > 0) && (aPC.getAlignment() == Globals.getIndexOfAlignment(Constants.s_NONE)))
			{
				JOptionPane.showMessageDialog(null, "You must select an Alignment before adding classes.", Constants.s_APPNAME, JOptionPane.ERROR_MESSAGE);
				return;
			}
		}
		if ((theClass == null) || !theClass.isQualified())
		{
			return;
		}

		final PCClass aClass = aPC.getClassNamed(theClass.getName());
		if (!SettingsHandler.isIgnoreLevelCap() && (levels > theClass.getMaxLevel() || (aClass != null && aClass.getLevel() + levels > aClass.getMaxLevel())))
		{
			JOptionPane.showMessageDialog(null, "Maximum level is " + theClass.getMaxLevel(), Constants.s_APPNAME, JOptionPane.INFORMATION_MESSAGE);
			return;
		}

		aPC.setDirty(true);

		Globals.getRootFrame().forceUpdate_PlayerTabs();
		Globals.getRootFrame().forceUpdate_InfoClasses();
		Globals.getRootFrame().forceUpdate_InfoSkills();
		Globals.getRootFrame().forceUpdate_InfoFeats();
		Globals.getRootFrame().forceUpdate_InfoDomain();
		Globals.getRootFrame().forceUpdate_InfoSpells();
		Globals.getRootFrame().forceUpdate_InfoGear();

		aPC.incrementClassLevel(levels, theClass);

		statTable.invalidate();
		statTable.updateUI();
		setStatLabelText();

		//
		// If we've just added the first non-monster level,
		// ask to choose free item of clothing if haven't already
		//
		if (levels > 0)
		{
			if (SettingsHandler.isFreeClothesAtFirst() && ((aPC.totalPCLevels() + aPC.totalNPCLevels()) == 1))
			{
				//
				// See what the PC is already carrying
				//
				ArrayList clothes = Globals.getEquipmentOfType(aPC.getEquipmentList(), "Clothing.Resizable", "Magic");
				//
				// Check to see if any of the clothing the PC
				// is carrying will actually fit and
				// has a zero price attached
				//
				boolean hasClothes = false;
				final String pcSize = aPC.getSize();
				if (clothes.size() != 0)
				{
					for (Iterator e = clothes.iterator(); e.hasNext();)
					{
						final Equipment eq = (Equipment) e.next();
						if ((eq.getCost().doubleValue() == 0.0) && pcSize.equals(eq.getSize()))
						{
							hasClothes = true;
							break;
						}
					}
				}
				//
				// If the PC has no clothing items, or none that
				// are sized to fit, then allow them to pick
				// a free set
				//
				if (!hasClothes)
				{
					clothes = Globals.getEquipmentOfType(Globals.getEquipmentList(), "Clothing.Resizable", "Magic.Custom.Auto_Gen");
					ArrayList selectedClothes = new ArrayList();
					Globals.chooseFromList("Select a free set of clothing", clothes, selectedClothes, 1);
					if (selectedClothes.size() != 0)
					{
						String aString = (String) selectedClothes.get(0);
						Equipment eq = Globals.getEquipmentNamed(aString);
						if (eq != null)
						{
							eq = (Equipment) eq.clone();
							//
							// Need to resize to fit?
							//
							if (!pcSize.equals(eq.getSize()))
							{
								eq.resizeItem(pcSize);
							}
							eq.setCostMod('-' + eq.getCost().toString());		// make cost 0
							//
							// Can't add if already own one with this name.
							//
							if (aPC.getEquipmentNamed(eq.nameItemFromModifiers()) == null)
							{
								aPC.addEquipment(eq);
							}
							else
							{
								Globals.errorPrint("Cannot add duplicate equipment to PC");
							}
						}
					}
				}
			}
		}

		needsUpdate = true;
		updateCharacterInfo();
	}

	/**
	 * Creates or refreshes the Models that will be used
	 */
	private void createModels()
	{
		createClassTreeModel();
		createRaceComboModel();
		createClassComboModel();
	}

	/**
	 * This method creates or refreshes the model for the PC Class tree
	 */
	private void createClassTreeModel()
	{
		if (pcClassTreeModel == null)
		{
			pcClassTreeModel = new ClassModel();
		}
		else
		{
			pcClassTreeModel.resetModel(false);
		}
	}

	/**
	 * This method creates or refreshes the model for the PC race menu
	 */
	private void createRaceComboModel()
	{
		if (raceComboModel == null)
		{
			raceComboModel = new RaceComboModel();
		}
		else
		{
			raceComboModel.updateModel();
		}
	}

	/**
	 * This method creates or refreshes the model for the PC Class menu
	 */
	private void createClassComboModel()
	{
		if (classComboModel == null)
		{
			classComboModel = new ClassComboModel();
		}
		else
		{
			classComboModel.updateModel();
		}
	}

	/**
	 * This method creates the table containing the PC Class list/tree
	 */
	private void createTreeTables()
	{
		pcClassTable = new JTableEx(new TableSorter(pcClassTreeModel));
		pcClassTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		pcClassTable.addMouseListener(new MouseAdapter()
		{
			public void mouseClicked(MouseEvent evt)
			{
				final String aString = aPC.getLevelInfoClassKeyName(pcClassTable.getSelectedRow());
				final PCClass aClass = Globals.getClassKeyed(aString);
				if (aClass != null)
				{
					classComboBox.setSelectedItem(aClass);
				}
			}
		});
	}

	/**
	 * This class is the model for the stat table
	 */
	private final class StatTableModel extends AbstractTableModel
	{
		public int getColumnCount()
		{
			return 7;
		}

		public Class getColumnClass(int columnIndex)
		{
			return getValueAt(0, columnIndex).getClass();
		}

		public boolean isCellEditable(int row, int col)
		{
			return (col == 1);
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
					return "Score [Editable]";
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
				{
					return Globals.s_ATTRIBLONG[rowIndex];
				}
				else
				{
					return "Out of Bounds";
				}
			}
			final PlayerCharacter aPC = Globals.getCurrentPC();
			switch (columnIndex)
			{
				case 1:	// Score
					if (aPC.isNonability(rowIndex))
					{
						return "*";
					}
					return new Integer(aPC.getStatList().getBaseStatFor(Globals.s_ATTRIBSHORT[rowIndex]));
				case 2:	// Adj
					if (aPC.isNonability(rowIndex))
					{
						return "*";
					}
					return new Integer(aPC.getStatList().getTotalStatFor(Globals.s_ATTRIBSHORT[rowIndex]) - aPC.getStatList().getBaseStatFor(Globals.s_ATTRIBSHORT[rowIndex]));
				case 3:	// Total
					if (aPC.isNonability(rowIndex))
					{
						return "*";
					}
					return new Integer(aPC.getStatList().getTotalStatFor(Globals.s_ATTRIBSHORT[rowIndex]));
				case 4:	// Mod
					if (aPC.isNonability(rowIndex))
					{
						return new Integer(0);
					}
					return new Integer(aPC.getStatList().getStatModFor(Globals.s_ATTRIBSHORT[rowIndex]));
				case 5:
					if (!Globals.s_ATTRIBROLL[rowIndex])
					{
						return null;
					}
					return "+";
				case 6:
					if (!Globals.s_ATTRIBROLL[rowIndex])
					{
						return null;
					}
					return "-";
				default:
					return "Out of Bounds";
			}
		}

		public void setValueAt(Object obj, int rowIndex, int columnIndex)
		{
			if ((rowIndex >= 0) && (rowIndex < aPC.getStatList().getStats().size()) && (columnIndex == 1))
			{
				if (obj == null)
				{
					return;
				}

				final int statVal = Delta.parseInt(obj.toString());
				if (aPC.isNonability(rowIndex))
				{
					JOptionPane.showMessageDialog(null, "Cannot modify a nonability", Constants.s_APPNAME, JOptionPane.ERROR_MESSAGE);
					return;
				}
				else if (statVal < 0)
				{
					JOptionPane.showMessageDialog(null, "Cannot lower stat below 0", Constants.s_APPNAME, JOptionPane.ERROR_MESSAGE);
					return;
				}
				else if ((aPC.getTotalLevels() < 1) && SettingsHandler.isPurchaseStatMode())
				{
					final int minPurchaseScore = SettingsHandler.getPurchaseModeBaseStatScore();
					final int maxPurchaseScore = SettingsHandler.getPurchaseScoreMax();
					if (statVal > maxPurchaseScore)
					{
						JOptionPane.showMessageDialog(null, "Cannot raise stat above " + maxPurchaseScore + " in Purchase Mode", Constants.s_APPNAME, JOptionPane.ERROR_MESSAGE);
						return;
					}
					else if (statVal < minPurchaseScore)
					{
						JOptionPane.showMessageDialog(null, "Cannot lower stat below " + minPurchaseScore + " in Purchase Mode", Constants.s_APPNAME, JOptionPane.ERROR_MESSAGE);
						return;
					}
				}
				final PCStat aStat = (PCStat) aPC.getStatList().getStats().get(rowIndex);
				aStat.setBaseScore(statVal);
				aPC.saveStatIncrease(aStat.getAbb(), statVal - aStat.getBaseScore(), false);
				setStatLabelText();

				updatePool(true);
			}
		}
	}

	private final class ClassModel extends AbstractTableModel
	{
		private void resetModel(boolean newCall)
		{
			fireTableDataChanged();
		}

		public int getColumnCount()
		{
			return 3;
		}

		public Class getColumnClass(int column)
		{
			return String.class;
		}

		/** <code>getRowCount()
		 * returns the number of rows. Gets the number of stats from Globals.s_ATTRIBLONG
		 */
		public int getRowCount()
		{
			int iCount = 0;
			if (Globals.getCurrentPC() != null)
			{
				PCClass aClass = null;
				for (int idx = 0; idx < aPC.getLevelInfoSize(); ++idx)
				{
					aClass = aPC.getClassKeyed(aPC.getLevelInfoClassKeyName(idx));
					if ((aClass != null) && !shouldDisplayThis(aClass))
					{
						continue;
					}
					++iCount;
				}
			}
			return iCount;
		}

		public String getColumnName(int columnIndex)
		{
			switch (columnIndex)
			{
				case COL_PCLEVEL:
					return "Level";

				case COL_CLASSNAME:
					return "Class(Lvl)";

				case COL_SRC:
					return "Source";

				default:
					return "Out of Bounds";
			}
		}

		public Object getValueAt(int rowIndex, int columnIndex)
		{
			final PlayerCharacter aPC = Globals.getCurrentPC();
			String retStr = "";
			switch (columnIndex)
			{
				case COL_PCLEVEL:
					retStr = Integer.toString(rowIndex + 1);
					break;

				case COL_CLASSNAME:
					if (aPC != null)
					{
						PCClass aClass = null;
						int lvl = 0;
						for (int idx = 0; idx < aPC.getLevelInfoSize(); ++idx)
						{
							final String classKeyName = aPC.getLevelInfoClassKeyName(idx);
							aClass = aPC.getClassKeyed(classKeyName);
							if ((aClass != null) && !shouldDisplayThis(aClass))
							{
								continue;
							}
							if (rowIndex-- == 0)
							{
								retStr = aClass.getName();
								final String subClass = aClass.getDisplayClassName();
								if (!retStr.equals(subClass))
								{
									retStr = retStr + "/" + subClass;
								}
								lvl = aPC.getLevelInfoClassLevel(idx);
								break;
							}
						}

						if ((aClass == null) || (aClass.getLevel() == lvl))
						{
							retStr += " (" + Integer.toString(lvl) + ')';
						}
					}
					break;

				case COL_SRC:
					if (aPC != null)
					{
						PCClass aClass = null;
						for (int idx = 0; idx < aPC.getLevelInfoSize(); ++idx)
						{
							final String classKey = aPC.getLevelInfoClassKeyName(idx);
							aClass = aPC.getClassKeyed(classKey);
							if ((aClass != null) && !shouldDisplayThis(aClass))
							{
								continue;
							}
							if (rowIndex-- == 0)
							{
								break;
							}
						}

						if (aClass != null)
						{
							retStr = aClass.getSource();
						}
					}
					break;

				default:
					retStr = "Out of Bounds";
			}
			return retStr;
		}

		/**
		 * return a boolean to indicate if the item should be included in the list.
		 * Only Weapon, Armor and Shield type items should be checked for proficiency.
		 */
		private boolean shouldDisplayThis(final PCClass aClass)
		{
			if (SettingsHandler.hideMonsterClasses() && aClass.isMonster())
			{
				return false;
			}
			return true;
		}

	}

	/**
	 * ComboBox model to manage the list of races. This model supports
	 * filtering in addition to the usual combo box things.
	 */
	private final class RaceComboModel extends DefaultComboBoxModel
	{
		private RaceComboModel()
		{
			updateModel();
		}

		private void updateModel()
		{
			final Object pcRace = getSelectedItem();
			removeAllElements();
			for (Iterator it = Globals.getRaceMap().values().iterator(); it.hasNext();)
			{
				final Race aRace = (Race) it.next();
				if (accept(aRace))
				{
					addElement(aRace);
				}
			}

			// Make sure empty race is in all lists
			if (getIndexOf(Globals.s_EMPTYRACE) < 0)
			{
				insertElementAt(Globals.s_EMPTYRACE, 0);
			}
			// Make sure the currently selected race is still available.
			// This is done to ensure that filtering doesn't change the PC's race.
			if ((pcRace != null) && (getIndexOf(pcRace) < 0))
			{
				insertElementAt(pcRace, 1);
			}

			setSelectedItem(pcRace);
		}
	}

	/**
	 * ComboBox model to manage the list of classes. This model supports
	 * filtering in addition to the usual combo box things.
	 */
	private final class ClassComboModel extends DefaultComboBoxModel
	{
		private ClassComboModel()
		{
			updateModel();
		}

		private void updateModel()
		{
			final Object pcClass = getSelectedItem();
			removeAllElements();
			for (Iterator it = Globals.getClassList().iterator(); it.hasNext();)
			{
				final PCClass aClass = (PCClass) it.next();
				if (SettingsHandler.hideMonsterClasses() && aClass.isMonster())
				{
					continue;
				}
				if (aClass.isVisible() && accept(aClass))
				{
					addElement(aClass);
				}

			}
			setSelectedItem(pcClass);
		}

	}

	/**
	 * implementation of Filterable interface
	 */
	public void initializeFilters()
	{
		this.registerFilter(FilterFactory.createPCClassFilter());
		FilterFactory.registerAllSourceFilters(this);
		FilterFactory.registerAllSizeFilters(this);
		FilterFactory.registerAllRaceFilters(this);
		FilterFactory.registerAllClassFilters(this);
		FilterFactory.registerAllPrereqAlignmentFilters(this);
	}

	/**
	 * implementation of Filterable interface
	 */
	public void refreshFiltering()
	{
		stopListeners();
		if (raceComboModel != null)
		{
			raceComboModel.updateModel();
		}
		if (classComboModel != null)
		{
			classComboModel.updateModel();
		}
		startListeners();
	}

	/**
	 * specifies whether the "match any" option should be available
	 */
	public boolean isMatchAnyEnabled()
	{
		return true;
	}

	/**
	 * specifies whether the "negate/reverse" option should be available
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

	private final class RendererEditor implements TableCellRenderer
	{
		private JButton plusButton = new JButton("+");
		private DefaultTableCellRenderer def = new DefaultTableCellRenderer();

		private RendererEditor()
		{
			def.setBackground(InfoSummary.this.getBackground());
			def.setAlignmentX(Component.CENTER_ALIGNMENT);
			def.setHorizontalAlignment(SwingConstants.CENTER);
			plusButton.setPreferredSize(new Dimension(30, 24));
			plusButton.setMinimumSize(new Dimension(30, 24));
			plusButton.setMaximumSize(new Dimension(30, 24));
		}

		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
		{
			if (column == 5)
			{
				def.setText("+");
				def.setBorder(BorderFactory.createEtchedBorder());
				return def;
			}
			else if (column == 6)
			{
				def.setText("-");
				def.setBorder(BorderFactory.createEtchedBorder());
				return def;
			}
			return null;
		}
	}

	public void addMonsterHD(int direction)
	{
		int numHD = txtHD.getValue();
		if (numHD <= 0)
		{
			JOptionPane.showMessageDialog(null, "Number of hit dice must be positive.", Constants.s_APPNAME, JOptionPane.ERROR_MESSAGE);
			return;
		}
		numHD *= direction;

		//
		// Race needs to have a MONSTERCLASS:<class>,<levels> tag
		//
		final String monsterClass = aPC.getRace().getMonsterClass(false);
		if (monsterClass != null)
		{
			//
			// Class must exist in Global list
			//
			final PCClass aClass = Globals.getClassNamed(monsterClass);
			if (aClass != null)
			{
				//
				// Can't allow HD to drop below racial minimum
				//
				if (numHD < 0)
				{
					final int minHD = aPC.getRace().getMonsterClassLevels() + aPC.getRace().hitDice();
					final PCClass pcClass = aPC.getClassNamed(monsterClass);
					int currentHD = aPC.getRace().hitDice();
					if (pcClass != null)
					{
						currentHD += pcClass.getLevel();
					}
					//
					// Don't allow a number so big it causes us to drop below minimum level
					//
					Globals.errorPrint("minHD=" + minHD + "  currentHD=" + currentHD + "  numHD=" + numHD);
					if ((currentHD + numHD) < minHD)
					{
						numHD = minHD - currentHD;
						Globals.errorPrint("numHD modified to: " + numHD);
					}
					if ((pcClass == null) || (numHD == 0) || ((currentHD + numHD) < minHD))
					{
						JOptionPane.showMessageDialog(null, "Cannot lower hit dice any more.", Constants.s_APPNAME, JOptionPane.ERROR_MESSAGE);
						return;
					}
				}
				addClass(aClass, numHD);
			}
		}
	}
}
