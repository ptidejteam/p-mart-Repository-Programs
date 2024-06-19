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
import javax.swing.*;
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
class InfoSummary extends FilterAdapterPanel
{
	private static PlayerCharacter aPC = Globals.getCurrentPC();
	static boolean needsUpdate = true;
	private InfoAbilities infoAbilities = new InfoAbilities();
	private JFrame abilitiesFrame = new JFrame(CharacterInfo.in_abilities);
	private boolean abilitiesFrameHasBeenSized = false;
/*
	private static String in_purchaseModeLabel = "";
	private static String in_purchaseModePointsAvailable = "";
	private static String in_purchaseModePointsSpent = "";
*/
	private JPanel northPanel = new JPanel();
	private JPanel centerPanel = new JPanel();
	private JPanel centerNorthPanel = new JPanel();
	private JPanel centerCenterPanel = new JPanel();
	private JPanel levelPanel = new JPanel();


	private JLabel labelName = null;
	protected JTextField txtName = new JTextField(aPC.getName());
	private JTextField tabNameText = new JTextField(aPC.getTabName());
	private JTextField playerNameText = new JTextField(aPC.getPlayersName());

	private JPanel poolPanel = new JPanel();
	private JLabel poolLabel = new JLabel("Stat Cost:");
	private JLabel poolText = new JLabel();
	private JButton jButtonHP = new JButton();
	private JLabel lblHp = new JLabel();

	private JLabel labelAlignment = null;
	private JComboBox alignmentComboBox = new JComboBox();
	private JLabel labelRace = null;
	private JComboBox raceComboBox = new JComboBox();
	private JLabel labelClass = null;
	private JComboBox classComboBox = new JComboBox();

	private WholeNumberField levelText = new WholeNumberField(1, 3);
	private JButton lvlUpButton;
	private JButton lvlDownButton;

	protected RaceComboModel raceModel = null;   // Model for the race combo box.
	protected ClassComboModel classModel = null;   // Model for the race combo box.
	protected ClassModel selectedModel = null;   // Model for the JTreeTable.
	protected JTreeTable selectedTable;   // the selected Class

	private static final int COL_NAME = 0;
	private static final int COL_REQ_LEVEL = 1;
	private static final int COL_SRC = 2;

	private JButton randName;
	private JButton abilitiesButton;

	private JEditorPane infoPane = new JEditorPane();
	private JEditorPane tipsPane = new JEditorPane();
	private JEditorPane statsPane = new JEditorPane();

	private NameGui nameFrame = null;
	private Border etched;
	private TitledBorder titled;
	private TitledBorder statsBorder;

	private JTableEx statTable;
	protected StatTableModel statTableModel = new StatTableModel();
	private JLabel purchaseModeLabel = null;
	protected RendererEditor plusMinusRenderer = new RendererEditor();


	/**
	 * Resource bundles
	 */
/*
	static
	{
		ResourceBundle summaryTabProperties;
		Locale currentLocale = new Locale(Globals.getLanguage(), Globals.getCountry());
		try
		{
			summaryTabProperties = ResourceBundle.getBundle("pcgen/gui/prop/LanguageBundle", currentLocale);
			in_purchaseModeLabel = summaryTabProperties.getString("in_purchaseMode");
			in_purchaseModePointsAvailable = summaryTabProperties.getString("in_purchasePntsAvail");
			in_purchaseModePointsSpent = summaryTabProperties.getString("in_purchasePntsSpent");
		}
		catch (MissingResourceException mrex)
		{
			mrex.printStackTrace();
		}
		finally
		{
			summaryTabProperties = null;
		}

	}
*/

	// Listeners for events on text boxes, combo boxes and command buttons
	private ActionListener nameActionListener = new ActionListener()
	{
		/**
		 *  Anonymous event handler
		 *
		 * @param  evt  The ActionEvent
		 * @since
		 */
		public void actionPerformed(ActionEvent evt)
		{
			txtName_Changed(evt);
			labelName.requestFocus();
		}
	};

	private FocusAdapter nameFocusListener = new FocusAdapter()
	{
		public void focusLost(java.awt.event.FocusEvent evt)
		{
			txtName_Changed(evt);
		}
	};

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

	private ActionListener alignListener = new ActionListener()
	{
		/**
		 *  Anonymous event handler
		 *
		 * @param  evt  The ActionEvent
		 * @since
		 */
		public void actionPerformed(ActionEvent evt)
		{
			if (alignmentComboBox != null && alignmentComboBox.getSelectedItem() != null)
			{
				alignmentChanged();
			}
		}
	};

	private ActionListener abilitiesListener = new ActionListener()
	{
		/**
		 *  Anonymous event handler
		 *
		 * @param  evt  The ActionEvent
		 * @since
		 */
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
			if (raceComboBox != null && raceComboBox.getSelectedItem() != null)
			{
				final Race r = (Race)raceComboBox.getSelectedItem();
				setInfoLabelText(r);
			}
		}
	};

	private FocusAdapter raceFocusListener = new FocusAdapter()
	{
		/**
		 *  Update character's race when the user moves away from
		 * the race combo box.
		 *
		 * @param  evt  The FocusEvent
		 */
		public void focusLost(java.awt.event.FocusEvent evt)
		{
			if (evt.isTemporary())
			{
				// Temporaray focus lost means something like the drop-down has got focus
				return;
			}

			if (raceComboBox != null && raceComboBox.getSelectedItem() != null)
			{
				aPC.setDirty(true);

				Race r = (Race)raceComboBox.getSelectedItem();
				if (!r.equals(aPC.getRace()))
				{
					setInfoLabelText(r);
					aPC.setRace(r);
					Globals.getRootFrame().forceUpdate_PlayerTabs();
					Globals.getRootFrame().forceUpdate_InfoRace();
					Globals.getRootFrame().forceUpdate_InfoFeats();
					Globals.getRootFrame().forceUpdate_InfoSkills();
					Globals.getRootFrame().forceUpdate_InfoSpells();
					if (aPC.getRace().hitDice() != 0)
					{
						aPC.getRace().rollHP();
					}
					refreshDisplay();
				}
				if (r.passesPreReqTests())
					labelRace.setForeground(Color.black);
				else
					labelRace.setForeground(Color.red);
			}
		}
	};

	private ActionListener classListener = new ActionListener()
	{
		/**
		 *  Anonymous event handler
		 *
		 * @param  evt  The ActionEvent
		 * @since
		 */
		public void actionPerformed(ActionEvent evt)
		{
			if (classComboBox != null && classComboBox.getSelectedItem() != null)
			{
				final PCClass pcClass = (PCClass)classComboBox.getSelectedItem();
				setInfoLabelText(pcClass);
				if (pcClass.isQualified())
					labelClass.setForeground(Color.black);
				else
					labelClass.setForeground(Color.red);
			}
		}
	};

	private ActionListener levelCmdListener = new ActionListener()
	{
		/**
		 *  Anonymous event handler
		 *
		 * @param  evt  The ActionEvent
		 * @since
		 */
		public void actionPerformed(ActionEvent evt)
		{
			int numLevels = levelText.getValue();
			if (numLevels < 0)
			{
				JOptionPane.showMessageDialog(null,
				  "Number of levels must be positive.", Constants.s_APPNAME, JOptionPane.ERROR_MESSAGE);
				return;
			}
			PCClass pcClass = (PCClass)classModel.getSelectedItem();
			if (pcClass == null)
			{
				JOptionPane.showMessageDialog(null,
				  "You must select a class.", Constants.s_APPNAME, JOptionPane.ERROR_MESSAGE);
				return;
			}
			if (!pcClass.isQualified())
			{
				JOptionPane.showMessageDialog(null,
				  "You are not qualified to take the class " + pcClass.getName() + ".", Constants.s_APPNAME, JOptionPane.ERROR_MESSAGE);
				return;
			}

			// Now we make it negative to remove levels
			if (evt.getSource() == lvlDownButton)
				numLevels = numLevels * -1;

			addClass(pcClass, numLevels);
		}
	};

	private FocusAdapter tabNameAdapter = new FocusAdapter()
	{
		public void focusLost(FocusEvent evt)
		{
			aPC.setTabName(tabNameText.getText());
		}
	};

	private FocusAdapter playerNameAdapter = new FocusAdapter()
	{
		public void focusLost(FocusEvent evt)
		{
			aPC.setPlayersName(playerNameText.getText());
		}
	};

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

	public InfoSummary()
	{
		// do not remove this
		// we will use the component's name to save component specific settings
		setName("Summary");

		initComponents();
		refreshDisplay();

		FilterFactory.restoreFilterSettings(this);
	}

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
			col.setPreferredWidth(50);
		else
			col.setPreferredWidth(width);
		col.addPropertyChangeListener(new ResizeColumnListener(statTable, "AbilitiesS", 0));
		col.setCellRenderer(new AlignCellRenderer(SwingConstants.LEFT));
		col = statTable.getColumnModel().getColumn(1);
		width = Globals.getCustColumnWidth("AbilitiesS", 1);
		if (width == 0)
			col.setPreferredWidth(40);
		else
			col.setPreferredWidth(width);
		col.addPropertyChangeListener(new ResizeColumnListener(statTable, "AbilitiesS", 1));
		col.setCellRenderer(new AlignCellRenderer(SwingConstants.CENTER));
		col = statTable.getColumnModel().getColumn(2);
		width = Globals.getCustColumnWidth("AbilitiesS", 2);
		if (width == 0)
			col.setPreferredWidth(30);
		else
			col.setPreferredWidth(width);
		col.addPropertyChangeListener(new ResizeColumnListener(statTable, "AbilitiesS", 2));
		col.setCellRenderer(new AlignCellRenderer(SwingConstants.CENTER));
		col = statTable.getColumnModel().getColumn(3);
		width = Globals.getCustColumnWidth("AbilitiesS", 3);
		if (width == 0)
			col.setPreferredWidth(40);
		else
			col.setPreferredWidth(width);
		col.addPropertyChangeListener(new ResizeColumnListener(statTable, "AbilitiesS", 3));
		col.setCellRenderer(new AlignCellRenderer(SwingConstants.CENTER));
		col = statTable.getColumnModel().getColumn(4);
		width = Globals.getCustColumnWidth("AbilitiesS", 4);
		if (width == 0)
			col.setPreferredWidth(30);
		else
			col.setPreferredWidth(width);
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
		String tipsText;
		String tip;
		int tipNum;
		northPanel.setLayout(gridbag);
		c.insets = new Insets(2, 2, 2, 2);

		createModels();
		createTreeTables();

		// Layout the first column
		buildConstraints(c, 0, 0, 1, 1, 0, 0);
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.EAST;
		labelName = new JLabel(PropertyFactory.getString("in_sumCharString") + ": ");
		gridbag.setConstraints(labelName, c);
		northPanel.add(labelName);

		buildConstraints(c, 1, 0, 1, 1, 3, 0);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.WEST;
		gridbag.setConstraints(txtName, c);
		northPanel.add(txtName);

		buildConstraints(c, 0, 1, 1, 1, 0, 0);
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.EAST;
		//JLabel randLabel = new JLabel(PropertyFactory.getString("in_sumRandomNameString") + ": ");
		//gridbag.setConstraints(randLabel, c);
		//northPanel.add(randLabel);

		buildConstraints(c, 1, 1, 1, 1, 0, 0);
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.WEST;
		randName = new JButton(PropertyFactory.getString("in_sumRandomNameString")); //("in_randomButton"));
		gridbag.setConstraints(randName, c);
		northPanel.add(randName);
		Utility.setDescription(randName, PropertyFactory.getString("in_randNameTipString"));

		buildConstraints(c, 0, 2, 1, 1, 0, 0);
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.EAST;
		JLabel tabLabel = new JLabel(PropertyFactory.getString("in_tabString") + ": ");
		gridbag.setConstraints(tabLabel, c);
		northPanel.add(tabLabel);

		buildConstraints(c, 1, 2, 1, 1, 0, 0);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.WEST;
		gridbag.setConstraints(tabNameText, c);
		northPanel.add(tabNameText);
		tabNameText.addFocusListener(tabNameAdapter);

		buildConstraints(c, 0, 3, 1, 1, 0, 0);
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.EAST;
		JLabel label = new JLabel(PropertyFactory.getString("in_sumPlayerString") + ": ");
		gridbag.setConstraints(label, c);
		northPanel.add(label);

		buildConstraints(c, 1, 3, 1, 1, 0, 0);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.WEST;
		gridbag.setConstraints(playerNameText, c);
		northPanel.add(playerNameText);
		playerNameText.addFocusListener(playerNameAdapter);

		// Layout the second column
		buildConstraints(c, 3, 0, 1, 1, 0, 0);
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.EAST;
		labelAlignment = new JLabel(PropertyFactory.getString("in_alignString") + ": ");
		gridbag.setConstraints(labelAlignment, c);
		northPanel.add(labelAlignment);

		buildConstraints(c, 4, 0, 1, 1, 2, 0);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.WEST;
		gridbag.setConstraints(alignmentComboBox, c);
		northPanel.add(alignmentComboBox);

		alignmentComboBox.setModel(new DefaultComboBoxModel(Constants.s_ALIGNLONG));

		buildConstraints(c, 3, 1, 1, 1, 0, 0);
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.EAST;
		labelRace = new JLabel(PropertyFactory.getString("in_raceString") + ": ");
		gridbag.setConstraints(labelRace, c);
		northPanel.add(labelRace);

		buildConstraints(c, 4, 1, 1, 1, 0, 0);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.WEST;
		gridbag.setConstraints(raceComboBox, c);
		northPanel.add(raceComboBox);

		raceModel = new RaceComboModel();
		raceComboBox.setModel(raceModel);

		buildConstraints(c, 3, 2, 1, 1, 0, 0);
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.EAST;
		labelClass = new JLabel(PropertyFactory.getString("in_classString") + ": ");
		gridbag.setConstraints(labelClass, c);
		northPanel.add(labelClass);

		buildConstraints(c, 4, 2, 1, 1, 0, 0);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.WEST;
		gridbag.setConstraints(classComboBox, c);
		northPanel.add(classComboBox);

		classModel = new ClassComboModel();
		classComboBox.setModel(classModel);

		// Layout for the level panel
		buildConstraints(c, 3, 3, 2, 1, 2, 0);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.WEST;
		label = new JLabel(PropertyFactory.getString("in_levelString") + ": ");
		lvlDownButton = new JButton("-");
		Utility.setDescription(lvlDownButton, PropertyFactory.getString("in_levelDownButtonTooltip"));
		lvlUpButton = new JButton("+");
		Utility.setDescription(lvlUpButton, PropertyFactory.getString("in_levelUpButtonTooltip"));
		Utility.setDescription(levelText, PropertyFactory.getString("in_levelTextTooltip"));
		levelPanel.add(label);
		levelPanel.add(levelText);
		levelPanel.add(lvlUpButton);
		levelPanel.add(lvlDownButton);
		gridbag.setConstraints(levelPanel, c);
		northPanel.add(levelPanel);

		// Layout the Stats table
		buildConstraints(c, 0, 4, 5, 2, 0, 18);
		c.fill = GridBagConstraints.BOTH;
		c.anchor = GridBagConstraints.WEST;
		JPanel statPanel = new JPanel();
		statPanel.setLayout(new BorderLayout());
		statPanel.add(statScrollPane, BorderLayout.CENTER);

		poolPanel.add(poolLabel);
		poolText.setPreferredSize(new Dimension(60, 20));
		poolPanel.add(poolText);
		jButtonHP.setText(Globals.getGameModeHPText());
		jButtonHP.setAlignmentY(0.0F);
		jButtonHP.setHorizontalAlignment(SwingConstants.LEFT);
		jButtonHP.addActionListener(
		  new ActionListener()
		  {
			  public void actionPerformed(ActionEvent e)
			  {
				pcGenGUI.showHpFrame();
			  }
		  });
		poolPanel.add(jButtonHP);
		lblHp.setText("");
		lblHp.setForeground(Color.black);
		lblHp.setHorizontalAlignment(SwingConstants.TRAILING);
		poolPanel.add(lblHp);
		statPanel.add(poolPanel, BorderLayout.SOUTH);
		gridbag.setConstraints(statPanel, c);


		northPanel.add(statPanel);
/*
		purchaseModeLabel = new JLabel(in_purchaseModeLabel);
		buildConstraints(c, 0, 5, 5, 1, 0, 0);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.WEST;
		gridbag.setConstraints(purchaseModeLabel, c);
		northPanel.add(purchaseModeLabel);
*/
		// Layout the tips pane
		tipsPane.setBackground(northPanel.getBackground());
		tipsPane.setContentType("text/html");
		tipsText = "<html><body><UL>";
		tipNum = 1;
		tip = PropertyFactory.getString("in_summaryTip" + String.valueOf(tipNum));
		while (tip != null && !tip.equals(PropertyFactory.UNDEFINED))
		{
			tipsText += "<LI>" + tip;
			tip = PropertyFactory.getString("in_summaryTip" + String.valueOf(++tipNum));
		}
		tipsText += "</UL></body></html>";
		tipsPane.setText(tipsText);
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

		buildConstraints(c, 0, 6, 5, 1, 0, 18);
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

		buildConstraints(c, 5, 0, 1, 4, 9, 0);
		c.fill = GridBagConstraints.BOTH;
		c.anchor = GridBagConstraints.CENTER;
		gridbag.setConstraints(pane2, c);
		northPanel.add(pane2);

		// Layout for the Classes table
		selectedTable.getColumnModel().getColumn(1).setCellRenderer(new AlignCellRenderer(SwingConstants.CENTER));
		selectedTable.getColumnModel().getColumn(1).setPreferredWidth(15);

		buildConstraints(c, 5, 4, 1, 1, 0, 18);
		c.fill = GridBagConstraints.BOTH;
		c.anchor = GridBagConstraints.CENTER;
		JScrollPane scrollPane = new JScrollPane(selectedTable);
		JPanel pane3 = new JPanel();
		pane3.add(scrollPane);
		TitledBorder title3 = BorderFactory.createTitledBorder(etched, PropertyFactory.getString("in_classesString"));
		title3.setTitleJustification(TitledBorder.CENTER);
		pane3.setBorder(title3);
		pane3.setLayout(new BoxLayout(pane3, BoxLayout.X_AXIS));
		gridbag.setConstraints(pane3, c);
		northPanel.add(pane3);

		// Abilities button
		buildConstraints(c, 5, 5, 1, 1, 0, 0);
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



		// Layout the stats pane
		statsPane.setBackground(northPanel.getBackground());
		statsPane.setContentType("text/html");
		statsPane.setText("");
		statsPane.setEditable(false);
		JScrollPane statsScroll = new JScrollPane();
		statsScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		statsScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		statsScroll.setViewportView(statsPane);
		statsScroll.getViewport().setScrollMode(JViewport.BLIT_SCROLL_MODE);
		statsScroll.setBackground(new Color(255, 255, 255));
		JPanel pane4 = new JPanel();
		pane4.add(statsScroll);
		TitledBorder title4 = BorderFactory.createTitledBorder(etched, PropertyFactory.getString("in_statsString"));
		title4.setTitleJustification(TitledBorder.CENTER);
		pane4.setBorder(title4);
		pane4.setLayout(new BoxLayout(pane4, BoxLayout.Y_AXIS));
		buildConstraints(c, 5, 6, 1, 1, 0, 0);
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

	// This is called when the tab is shown.
	private void formComponentShown(ComponentEvent evt)
	{
		requestFocus();
		Globals.getRootFrame().getStatusBar().setText("");

		updateCharacterInfo();
	}

	public void updateCharacterInfo()
	{
		aPC = Globals.getCurrentPC();
		updatePool(false);
		lblHp.setText(new Integer(aPC.hitPoints()).toString());
		//if (Globals.isStarWarsMode() || Globals.isSpycraftMode())
		//{
		//labelAlignment.setVisible(false);
		//alignmentComboBox.setVisible(false);
		//}
		//else
		//{
		//labelAlignment.setVisible(!Globals.isSSd20Mode());
		//alignmentComboBox.setVisible(!Globals.isSSd20Mode());
		//}
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

		if (aPC == null)
		{
			return;
		}
		refreshDisplay();
		levelText.setValue(1);
		needsUpdate = false;
	}

	public void setNeedsUpdate(boolean flag)
	{
		needsUpdate = flag;
	}

	/**
	 *  This method takes the name entered in the txtName field and makes it the
	 *  name of the active tab.
	 *
	 * @param  e  The ActionEvent
	 */
	void txtName_Changed(java.awt.AWTEvent e)
	{
		if (aPC != null)
		{
			aPC.setName(txtName.getText());
			Globals.getRootFrame().forceUpdate_PlayerTabs();
		}
	}

	public void startListeners()
	{
		txtName.addActionListener(nameActionListener);
		txtName.addFocusListener(nameFocusListener);
		randName.addActionListener(randNameListener);
		alignmentComboBox.addActionListener(alignListener);
		raceComboBox.addActionListener(raceListener);
		raceComboBox.addFocusListener(raceFocusListener);
		classComboBox.addActionListener(classListener);
		tabNameText.addFocusListener(tabNameAdapter);
		playerNameText.addFocusListener(playerNameAdapter);
		lvlDownButton.addActionListener(levelCmdListener);
		lvlUpButton.addActionListener(levelCmdListener);
		abilitiesButton.addActionListener(abilitiesListener);
	}

	public void stopListeners()
	{
		txtName.removeActionListener(nameActionListener);
		txtName.removeFocusListener(nameFocusListener);
		randName.removeActionListener(randNameListener);
		alignmentComboBox.removeActionListener(alignListener);
		raceComboBox.removeActionListener(raceListener);
		raceComboBox.removeFocusListener(raceFocusListener);
		classComboBox.removeActionListener(classListener);
		tabNameText.removeFocusListener(tabNameAdapter);
		playerNameText.removeFocusListener(playerNameAdapter);
		lvlDownButton.removeActionListener(levelCmdListener);
		lvlUpButton.removeActionListener(levelCmdListener);
		abilitiesButton.removeActionListener(abilitiesListener);
	}

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
//				if (!Globals.s_ATTRIBROLL[selectedStat])
//					return;

				increment = 1;
				final int pcTotalLevels = aPC.getTotalLevels();
				final boolean isPurchaseMode = SettingsHandler.isPurchaseStatMode();
				if (aPC.isNonability(selectedStat))
				{
					JOptionPane.showMessageDialog(null, "Cannot increment a nonability", Constants.s_APPNAME, JOptionPane.ERROR_MESSAGE);
				}
				//else if (pcTotalLevels < 1 && stat >= SettingsHandler.getInitialStatMax() && !isPurchaseMode)
				//{
					//JOptionPane.showMessageDialog(null, "Cannot raise stat above " + new Integer(SettingsHandler.getInitialStatMax()).toString(), Constants.s_APPNAME, JOptionPane.ERROR_MESSAGE);
				//}
				//else if ((!isPurchaseMode || (isPurchaseMode && pcTotalLevels > 0)) && aPC.getPoolAmount() < 1 && !SettingsHandler.isStatPoolUnlimited())
				//{
					//JOptionPane.showMessageDialog(null, "You have no pool points to spend.", Constants.s_APPNAME, JOptionPane.ERROR_MESSAGE);
				//}
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
//				if (!Globals.s_ATTRIBROLL[selectedStat])
//					return;

				increment = -1;
/////////////////////////////////////////////////
// Yanked for WotC compliance
//				final int minPurchaseScore = SettingsHandler.getPurchaseScoreMin();
				final int minPurchaseScore = SettingsHandler.getPurchaseModeBaseStatScore();
/////////////////////////////////////////////////
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
			((PCStat)aPC.getStatList().getStats().get(selectedStat)).setBaseScore(stat + increment);
			updatePool(increment > 0);
			statTableModel.fireTableRowsUpdated(selectedStat, selectedStat);
			setStatLabelText();
			final PCGen_Frame1 rootFrame = (PCGen_Frame1)Globals.getRootFrame();
			if (aPC.getStatBonusTo("HP", "BONUS") != 0)
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

	private int getUsedStatPool()
	{
		int i = 0;
		for (int stat = 0; stat < Globals.s_ATTRIBLONG.length; stat++)
		{
			int statValue = aPC.getStatList().getBaseStatFor(Globals.s_ATTRIBSHORT[stat]);
			if (!Globals.s_ATTRIBROLL[stat])
			{
				continue;
			}
			if (statValue>SettingsHandler.getPurchaseScoreMax())
				statValue = SettingsHandler.getPurchaseScoreMax();
			if (statValue >= SettingsHandler.getPurchaseScoreMin())
			{
				i += SettingsHandler.getAbilityScoreCost(statValue - SettingsHandler.getPurchaseScoreMin());
			}
		}
		return i;
	}

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
					JOptionPane.showMessageDialog(null,
					  "You have exceded the maximum points of " + availablePool + " as specified by the method \"" + SettingsHandler.getPurchaseModeMethodName() + "\"",
					  Constants.s_APPNAME,
					  JOptionPane.INFORMATION_MESSAGE);
				}
			}

		}
		else
		{
			poolLabel.setText("Stat Total:");
			int statTotal = 0;
			int modTotal = 0;
			final StatList statList = aPC.getStatList();
			for(int i = 0; i < Globals.s_ATTRIBLONG.length; ++i)
			{
				if (aPC.isNonability(i) || !Globals.s_ATTRIBROLL[i])
				{
					continue;
				}
				final int currentStat = statList.getBaseStatFor(Globals.s_ATTRIBSHORT[i]);
				final int currentMod  = statList.getStatModFor(Globals.s_ATTRIBSHORT[i]);

				statTotal += currentStat;
				modTotal  += currentMod;
			}
			poolLabel.setText("Stat Total: " + Integer.toString(statTotal) + "  Modifier Total: " + Integer.toString(modTotal));
			poolText.setText("");
		}

	}

/*
	private boolean updatePool(boolean checkPurchasePoints)
	{
		final int usedStatPool = getUsedStatPool();
  	final int availablePool = SettingsHandler.getPurchaseModeMethodPool();

		if (SettingsHandler.isPurchaseStatMode())
		{
			if (checkPurchasePoints)
			{
				if (checkPurchasePoints && aPC.getTotalLevels() > 0)
				{
					JOptionPane.showMessageDialog(null,
						"When in purchase mode you may not manually alter your stats after your character has a level.",
						Constants.s_APPNAME,
						JOptionPane.INFORMATION_MESSAGE);
					return false;
				}

				if (checkPurchasePoints && (availablePool > 0) && (usedStatPool > availablePool))
				{
					JOptionPane.showMessageDialog(null,
						"You have attempted to spend more than the maximum of " + availablePool + " points, as specified by the method \"" + SettingsHandler.getPurchaseModeMethodName() + "\"",
						Constants.s_APPNAME,
						JOptionPane.INFORMATION_MESSAGE);
					return false;
				}
			}

			aPC.setDirty(true);
			aPC.setCostPool(usedStatPool);
			aPC.setPoolAmount(usedStatPool);
			purchaseModeLabel.setText(in_purchaseModeLabel + ": " + availablePool + " " + in_purchaseModePointsAvailable +
					",  " + aPC.getCostPool() + " " + in_purchaseModePointsSpent + ".");
		}
		else
		{
			purchaseModeLabel.setText(in_purchaseModeLabel + ": Off.");
		}

		return true;

	}
*/
	public void refreshDisplay()
	{
		stopListeners();

		txtName.setText(aPC.getName());
		tabNameText.setText(aPC.getTabName());
		playerNameText.setText(aPC.getPlayersName());

		final int align = aPC.getAlignment();
		if (align > -1)
		{
			alignmentComboBox.setSelectedIndex(align);
		}

		final Race pcRace = aPC.getRace();
		if (pcRace.equals(Globals.s_EMPTYRACE))
		{
			raceModel.setSelectedItem(Globals.s_EMPTYRACE.getName());
		}
		else
		{
			raceModel.setSelectedItem(aPC.getRace().getName());
		}
		if (pcRace.passesPreReqTests())
			labelRace.setForeground(Color.black);
		else
			labelRace.setForeground(Color.red);
		setInfoLabelText(pcRace);

		labelClass.setForeground(Color.black);
		final ArrayList allPCClasses = aPC.getClassList();
		if (allPCClasses.size() > 0)
		{
			final PCClass pcClass = (PCClass)allPCClasses.get(allPCClasses.size() - 1);
			classModel.setSelectedItem(pcClass.getName());
			if (!pcClass.isQualified())
				labelClass.setForeground(Color.red);
		}
		createModels();
		selectedTable.updateUI();

		updatePool(false);

		setStatLabelText();

		enableControls(!alignmentComboBox.isVisible() || align != Constants.ALIGNMENT_NONE);
		startListeners();
	}

	private final void enableControls(boolean enable)
	{
		raceComboBox.setEnabled(enable);
		classComboBox.setEnabled(enable);
		levelText.setEnabled(enable);
		lvlUpButton.setEnabled(enable);
		lvlDownButton.setEnabled(enable);
	}

	private void alignmentChanged()
	{
		final int newAlignment = alignmentComboBox.getSelectedIndex();
		final int oldAlignment = aPC.getAlignment();
		if (newAlignment == oldAlignment)
			return;


		//
		// Get a list of classes that will become unqualified
		//
		StringBuffer unqualified = new StringBuffer();
		ArrayList classList = aPC.getClassList();
		ArrayList exclassList = new ArrayList();
		for (Iterator e = classList.iterator(); e.hasNext();)
		{
			final PCClass aClass = (PCClass)e.next();

			aPC.setAlignment(oldAlignment, false, true);
			if (aClass.isQualified())
			{
				aPC.setAlignment(newAlignment, false, true);
				if (!aClass.isQualified())
				{
					if (unqualified.length() > 0)
						unqualified.append(", ");
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
			if (JOptionPane.showConfirmDialog(null, "This will change the following class(es) to ex-class(es):\n" + unqualified.toString(), "PCGen", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.CANCEL_OPTION)
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
			final PCClass aClass = (PCClass)e.next();
			aPC.makeIntoExClass(aClass);
		}
		aPC.setDirty(true);
		aPC.setAlignment(newAlignment, false, true);
		needsUpdate = true;
		updateCharacterInfo();
		enableControls(newAlignment != Constants.ALIGNMENT_NONE);
	}

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
				b.append(" &nbsp;<b>Requirements</b>:").append(cString);
			String bString = aRace.getSource();
			if (bString.length() > 0)
			{
				b.append(" &nbsp;<b>SOURCE</b>:").append(bString);
			}
			final StringBuffer aString = new StringBuffer();
			for (int i = 0; i < Globals.s_ATTRIBSHORT.length; i++)
			{
				if (aRace.isNonability(i))
				{
					if (aString.length() > 0)
					{
						aString.append(" ");
					}
					aString.append(Globals.s_ATTRIBSHORT[i]).append(":Nonability");
				}
				else
				{
					if (aRace.getStatMod(i) != 0)
					{
						if (aString.length() > 0)
						{
							aString.append(" ");
						}
						aString.append(Globals.s_ATTRIBSHORT[i]).append(":").append(aRace.getStatMod(i));
					}
				}
			}
			if (aString.length() > 0)
			{
				b.append(" &nbsp;<b>STAT ADJ:</b>").append(aString);
			}
			b.append(" &nbsp;<b>SIZE:</b>").append(aRace.getSize());
			if (aRace.getMovements() != null)
			{
				final StringBuffer movelabel = new StringBuffer(aRace.getMovementType(0)).append(" ").append(aRace.getMovement(0)).append(Globals.getAbbrMovementDisplay());
				for (int i = 1; i < aRace.getMovements().length; i++)
				{
					movelabel.append(aRace.getMovementType(i)).append(" ").append(aRace.getMovement(i)).append(Globals.getAbbrMovementDisplay());
				}
				b.append(" &nbsp;<b>MOVE:</b>").append(movelabel);
			}
			b.append(" &nbsp;<b>VISION:</b>").append(aRace.getDisplayVision());
			b.append(" &nbsp;<b>FAVORED CLASS:</b>").append((!aRace.getFavoredClass().equals("."))
			  ? aRace.getFavoredClass()
			  : "Various");
			if (aRace.getLevelAdjustment() > 0)
			{
				b.append(" &nbsp;<b>LEVEL ADJ:</b>:").append(String.valueOf(aRace.getLevelAdjustment()));
			}
		}
		b.append("</html>");
		infoPane.setText(b.toString());
	}

	private void setInfoLabelText(PCClass aClass)
	{
		if (aClass != null)
		{
			StringBuffer b = new StringBuffer();
			b.append("<html><b>Class: ").append(aClass.getName()).append("</b>");
			b.append(" &nbsp;<b>TYPE</b>:").append(aClass.getType());
			final String cString = aClass.preReqHTMLStrings(false);
			if (cString.length() > 0)
				b.append(" &nbsp;<b>Requirements</b>:").append(cString);
			String bString = aClass.getSource();
			if (bString.length() > 0)
				b.append(" <b>SOURCE</b>:").append(bString);
			b.append(" <b>BAB:</b>").append(aClass.getAttackBonusType());
			b.append(" <b>HD:</b>1D").append(aClass.getHitDie());
			if (Globals.isStarWarsMode() || Globals.isSidewinderMode() || Globals.isWheelMode())
			{
				b.append(" <b>Defense:</b>").append(aClass.defenseString());
				b.append(" <b>Reputation:</b>").append(aClass.getReputationString());
			}
			else if (Globals.isSpycraftMode())
			{
				b.append(" <b>Defense:</b>").append(aClass.defenseString());
			}
			else
			{
				b.append(" <b>SPELLTYPE:</b>").append(aClass.getSpellType());
				b.append(" <b>Base Stat:</b>").append(aClass.getSpellBaseStat());
			}

			b.append("</html>");
			infoPane.setText(b.toString());
		}
	}

	private void setStatLabelText()
	{
		int bonus = 0;
		StringBuffer statBuf = new StringBuffer();

		statBuf.append("<html>");
		if (aPC != null)
		{
			if (Globals.isStarWarsMode() || Globals.isWheelMode() || Globals.isSpycraftMode())
			{
				statBuf.append("<b>Defense</b> ");
				statBuf.append("<i>Total</i>: ").append(aPC.DefenseTotal());
				statBuf.append(" <i>Flatfooted</i>: ").append(aPC.flatfootedDefense());
				statBuf.append(" <i>Touch</i>: ").append(aPC.touchDefense());
				statBuf.append("<br>");
			}
			else
			{
				statBuf.append("<b>AC</b> ");
				statBuf.append("<i>Total</i>: ").append(aPC.totalAC());
				statBuf.append(" <i>Flatfooted</i>: ").append(aPC.flatFootedAC());
				statBuf.append(" <i>Touch</i>: ").append(aPC.touchAC());
				statBuf.append("<br>");
			}

			final int initMod = aPC.initiativeMod();
			statBuf.append("<b>Init</b>: ").append(Delta.toString(initMod));
			bonus = aPC.getBonus(0, true);
			statBuf.append(" <b>BAB</b>: ").append(Delta.toString(bonus));
			if (Globals.isStarWarsMode() || Globals.isSpycraftMode())
			{
				statBuf.append(" <b>VP</b>: ").append(aPC.hitPoints());
				statBuf.append(" <b>WP</b>: ").append(aPC.woundPoints());
			}
			else if (Globals.isSidewinderMode())
			{
				statBuf.append(" <b>Vigor</b>: ").append(aPC.hitPoints());
				statBuf.append(" <b>Body Points</b>: ").append(aPC.woundPoints());
			}
			else
			{
				statBuf.append(" <b>HP</b>: ").append(aPC.hitPoints());
			}
			statBuf.append("<br>");

			statBuf.append("<b>Saves</b>: ");
			for (int z = 0; z < Globals.getCheckList().size(); z++)
			{
				bonus = aPC.getBonus(z + 1, true);
				statBuf.append(" <i>").append(Globals.getCheckList().get(z).toString()).append("</i>: ").append(Delta.toString(bonus));
			}

			if (Globals.isStarWarsMode())
			{
				statBuf.append("<br>");
				statBuf.append("<b>Force Points</b>: ").append(aPC.getStrFPoints());
				statBuf.append(" <b>Darkside Points</b>: ").append(aPC.getDPoints());
				statBuf.append(" <b>Reputation</b>: ").append(aPC.reputation());
			}

		}
		statBuf.append("</html>");
		statsPane.setText(statBuf.toString());
	}

	private void addClass(PCClass theClass, int levels)
	{
		if ((levels > 0) && (aPC.getAlignment() == Constants.ALIGNMENT_NONE) && Globals.isDndMode())
		{
			JOptionPane.showMessageDialog(null,
			  "You must select an Alignment before adding classes.", Constants.s_APPNAME, JOptionPane.ERROR_MESSAGE);
			return;
		}
		if ((theClass == null) || !theClass.isQualified())
		{
			return;
		}

		aPC.setDirty(true);

		Globals.getRootFrame().forceUpdate_PlayerTabs();
		Globals.getRootFrame().forceUpdate_InfoFeats();
		Globals.getRootFrame().forceUpdate_InfoDomain();
		Globals.getRootFrame().forceUpdate_InfoSkills();
		Globals.getRootFrame().forceUpdate_InfoSpells();

		final PCClass aClass = aPC.getClassNamed(theClass.getName());
		if (!SettingsHandler.isIgnoreLevelCap() && 
			(levels>theClass.getMaxLevel() || (aClass!=null && aClass.getLevel().intValue() + levels > aClass.getMaxLevel())))
		{
			JOptionPane.showMessageDialog(null, "Maximum level is "+theClass.getMaxLevel(), Constants.s_APPNAME, JOptionPane.INFORMATION_MESSAGE);
			return;
		}

		Globals.getRootFrame().forceUpdate_InfoInventory();
		aPC.incrementClassLevel(levels, theClass);

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
						final Equipment eq = (Equipment)e.next();
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
						String aString = (String)selectedClothes.get(0);
						Equipment eq = Globals.getEquipmentNamed(aString);
						if (eq != null)
						{
							eq = (Equipment)eq.clone();
							//
							// Need to resize to fit?
							//
							if (!pcSize.equals(eq.getSize()))
							{
								eq.resizeItem(pcSize);
							}
							eq.setCostMod("-" + eq.getCost().toString());		// make cost 0
							//
							// Can't add if already own one with this name.
							//
							if (aPC.getEquipmentNamed(eq.nameItemFromModifiers()) == null)
							{
								aPC.getEquipmentList().add(eq);
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
	 * Creates the ClassModel that will be used.
	 */
	protected void createModels()
	{
		createSelectedModel();
		createRaceModel();
		createClassModel();
	}

	protected void createSelectedModel()
	{
		if (selectedModel == null)
			selectedModel = new ClassModel();
		else
			selectedModel.resetModel(false);
	}

	protected void createRaceModel()
	{
		if (raceModel == null)
			raceModel = new RaceComboModel();
		else
			raceModel.updateModel();
	}

	protected void createClassModel()
	{
		if (classModel == null)
			classModel = new ClassComboModel();
		else
			classModel.updateModel();
	}

	protected void createTreeTables()
	{
		selectedTable = new JTreeTable(selectedModel);
		selectedTable.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
		final JTree btree = selectedTable.getTree();
		btree.setRootVisible(false);
		btree.setShowsRootHandles(true);
		btree.setCellRenderer(new LabelTreeCellRenderer());
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
					return Globals.s_ATTRIBLONG[rowIndex];
				else
					return "Out of Bounds";
			}
			final PlayerCharacter aPC = Globals.getCurrentPC();
			switch (columnIndex)
			{
				case 1:
					return new Integer(aPC.getStatList().getBaseStatFor(Globals.s_ATTRIBSHORT[rowIndex]));
				case 2:
					if (aPC.isNonability(rowIndex))
						return "-";
					return new Integer(aPC.getStatList().getTotalStatFor(Globals.s_ATTRIBSHORT[rowIndex]) - aPC.getStatList().getBaseStatFor(Globals.s_ATTRIBSHORT[rowIndex]));
				case 3:
					if (aPC.isNonability(rowIndex))
						return "--";
					return new Integer(aPC.getStatList().getTotalStatFor(Globals.s_ATTRIBSHORT[rowIndex]));
				case 4:
					if (aPC.isNonability(rowIndex))
						return new Integer(0);
					return new Integer(aPC.getStatList().getStatModFor(Globals.s_ATTRIBSHORT[rowIndex]));
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
				((PCStat)aPC.getStatList().getStats().get(rowIndex)).setBaseScore(statVal);
				setStatLabelText();
				updatePool(true);
			}
		}
/*
		public void setValueAt(Object obj, int rowIndex, int columnIndex)
		{
			if (rowIndex>=0 && rowIndex<aPC.getStatList().getStats().size() && columnIndex==1)
			{
				PCStat targetStat = (PCStat) aPC.getStatList().getStats().get(rowIndex);
				int newBaseScore = Integer.parseInt(obj.toString());
				int oldBaseScore = targetStat.getBaseScore();

				// Check ranges
				if (SettingsHandler.isPurchaseStatMode() && (newBaseScore > SettingsHandler.getPurchaseScoreMax()))
				{
					JOptionPane.showMessageDialog(null,
						"You may not set a stat to above " + SettingsHandler.getPurchaseScoreMax() + ", as specified by the purchase mode method \"" + SettingsHandler.getPurchaseModeMethodName() + "\"",
						Constants.s_APPNAME,
						JOptionPane.INFORMATION_MESSAGE);
					return;
				}

				if (SettingsHandler.isPurchaseStatMode() && (newBaseScore < SettingsHandler.getPurchaseScoreMin()))
				{
					JOptionPane.showMessageDialog(null,
						"You may not set a stat to below " + SettingsHandler.getPurchaseScoreMin() + ", as specified by the purchase mode method \"" + SettingsHandler.getPurchaseModeMethodName() + "\"",
						Constants.s_APPNAME,
						JOptionPane.INFORMATION_MESSAGE);
					return;
				}

				// Need to put purchase mode stuff here
				targetStat.setBaseScore(newBaseScore);
				if (!updatePool(true))
				{
					// Reset the score and ignore the update.
					targetStat.setBaseScore(oldBaseScore);
					return;
				}

				setStatLabelText();

				aPC.setDirty(true);
				final PCGen_Frame1 rootFrame = (PCGen_Frame1)Globals.getRootFrame();
				if (aPC.getStatBonusTo("HP", "BONUS") != 0)
				{
					rootFrame.hpTotal_Changed();
				}
				Globals.getRootFrame().forceUpdate_InfoSkills();
				Globals.getRootFrame().forceUpdate_InfoSpells();
			}
		}
*/
	}

	/** The basic idea of the TreeTableModel is that there is a single <code>root</code>
	 *  object.  This root object has a null <code>parent</code>.  All other objects
	 *  have a parent which points to a non-null object.  parent objects contain a list of
	 *  <code>children</code>, which are all the objects that point to it as their parent.
	 *  objects (or <code>nodes</code>) which have 0 children are leafs (the end of that
	 *  linked list).  nodes which have at least 1 child are not leafs. Leafs are like files
	 *  and non-leafs are like directories.
	 */
	class ClassModel extends AbstractTreeTableModel implements TreeTableModel
	{
		/**
		 * Creates a ClassModel
		 */
		public ClassModel()
		{
			super(null);
			resetModel(true);
		}

		/**
		 * This assumes the ClassModel exists but needs to be repopulated
		 */
		public void resetModel(boolean newCall)
		{
			Iterator fI;
			fI = aPC.getClassList().iterator();

			setRoot(new PObjectNode()); // just need a blank one
			for (; fI.hasNext();)
			{
				final PCClass aClass = (PCClass)fI.next();
				// in the availableTable, if filtering out unqualified items
				// ignore any class the PC doesn't qualify for
				if (!shouldDisplayThis(aClass))
					continue;
				PObjectNode aFN = new PObjectNode();
				aFN.setParent((PObjectNode)root);
				aFN.setItem(aClass);
				aFN.setIsValid(aClass.passesPreReqTests());
				((PObjectNode)root).addChild(aFN);
			}

			PObjectNode rootAsPObjectNode = (PObjectNode)root;
			if (!newCall && rootAsPObjectNode.getChildCount() > 0)
				fireTreeNodesChanged(root, rootAsPObjectNode.getChildren(), null, null);
		}

		/**
		 * return a boolean to indicate if the item should be included in the list.
		 * Only Weapon, Armor and Shield type items should be checked for proficiency.
		 */
		private boolean shouldDisplayThis(final PCClass aClass)
		{
			return true;
		}

		// "There can be only one!" There must be a root object, though it can be hidden
		// to make it's existence basically a convenient way to keep track of the objects
		public void setRoot(PObjectNode aNode)
		{
			root = aNode;
		}

		public void removeItemFromNodes(PObjectNode p, Object e)
		{
			if (p == null)
				p = (PObjectNode)root;

			// if no children, remove it and update parent
			if (p.getChildCount() == 0 && p.getItem().equals(e))
			{
				p.getParent().removeChild(p);
			}
			else
			{
				for (int i = 0; i < p.getChildCount(); i++)
				{
					removeItemFromNodes(p.getChild(i), e);
				}
			}
		}

		/* The JTreeTableNode interface. */

		/**
		 * Returns int number of columns.
		 */
		public int getColumnCount()
		{
			return 3;
		}

		/**
		 * Returns String name of a column.
		 */
		public String getColumnName(int column)
		{
			switch (column)
			{
				case COL_NAME:
					return "Name";
				case COL_REQ_LEVEL:
					return "Level";
				case COL_SRC:
					return "Source";
				default:
					Globals.errorPrint("In InfoSummary.ClassModel.getColumnName the column " + column + " is not handled.");
					break;
			}
			return "";
		}

		/**
		 * Returns Class for the column.
		 */
		public Class getColumnClass(int column)
		{
			switch (column)
			{
				case COL_NAME:
					return TreeTableModel.class;
				case COL_REQ_LEVEL:
					return Integer.class;
				case COL_SRC:
					return String.class;
				default:
					Globals.errorPrint("In InfoSummary.ClassModel.getColumnClass the column " + column + " is not handled.");
					break;
			}
			return String.class;
		}

		public boolean isCellEditable(Object node, int column)
		{
			return (column == COL_NAME);
		}

		/**
		 * Returns Object value of the column.
		 */
		public Object getValueAt(Object node, int column)
		{
			final PObjectNode fn = (PObjectNode)node;
			PCClass aClass = null;
			if (fn != null && (fn.getItem() instanceof PCClass))
				aClass = (PCClass)fn.getItem();

			final Integer c = new Integer(0);
			switch (column)
			{
				case COL_NAME: // Name
					if (fn != null)
					{
						return fn.toString();
					}
					else
					{
						Globals.errorPrint("Somehow we have no active node when doing getValueAt in InfoSummaryy.");
						return "";
					}
				case COL_REQ_LEVEL: // Cost
					if (aClass != null)
						return aClass.getLevel();
					return c;
				case COL_SRC: // Source or Qty
					if (fn != null)
						return fn.getSource();
					return "";
				case -1:
					if (fn != null)
					{
						return fn.getItem();
					}
					else
					{
						Globals.errorPrint("Somehow we have no active node when doing getValueAt in InfoSummary.");
						return null;
					}
				default:
					Globals.errorPrint("In InfoSummary.ClassModel.getValueAt the column " + column + " is not handled.");
					break;
			}
			return null;
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
	 * ComboBox model to manage the list of races. This model supports
	 * filtering in addition to the usual combo box things.
	 */
	protected class RaceComboModel extends AbstractListModel implements ComboBoxModel
	{
		private ArrayList raceList = new ArrayList();
		private String lastRowValue = null;
		private int lastRow = -1;
		private int selectedIndex = -1;

		public RaceComboModel()
		{
			updateModel();
		}

		public void updateModel()
		{
			Object pcRace = getSelectedItem();
			raceList.clear();
			for (Iterator it = Globals.getRaceMap().values().iterator(); it.hasNext();)
			{
				final Race aRace = (Race)it.next();
				if (accept(aRace))
				{
					raceList.add(aRace);
				}

			}
			// Make sure empty race is in all lists
			if (!raceList.contains(Globals.s_EMPTYRACE))
			{
				raceList.add(0, Globals.s_EMPTYRACE);
			}
			// Make sure the currently selected race is still available.
			// This is done to ensure that filtering doesn't change the PC's race.
			if (pcRace != null && !raceList.contains(pcRace))
			{
				raceList.add(1, pcRace);
			}
			setSelectedItem(pcRace);

			fireContentsChanged(this, 0, this.getSize());
		}

		public int getSize()
		{
			return raceList.size();
		}

		public void setSelectedItem(Object anItem)
		{
			String raceName = null;
			int index = 0;

			if (anItem instanceof String)
				raceName = (String)anItem;
			else if (anItem instanceof Race)
				raceName = anItem.toString();
			else
				return;

			Iterator i = raceList.iterator();
			while (i.hasNext())
			{
				final Race theRace = (Race)i.next();
				if (theRace.toString().equals(raceName))
				{
					selectedIndex = index;
					return;
				}
				index++;
			}

			// Couldn't find the race, so unselect it
			selectedIndex = -1;
		}

		public Object getSelectedItem()
		{
			if (selectedIndex > -1)
			{
				final Race race = (Race)raceList.get(selectedIndex);
				return race;
			}
			else
			{
				return null;
			}

		}

		public Object getElementAt(int index)
		{
			if (raceList.size() != 0)
			{
				final Race race = (Race)raceList.get(index);
				return race;
			}
			return null;
		}
	}

	/**
	 * ComboBox model to manage the list of classes. This model supports
	 * filtering in addition to the usual combo box things.
	 */
	protected class ClassComboModel extends AbstractListModel implements ComboBoxModel
	{
		private ArrayList classList = new ArrayList();
		private int selectedIndex = -1;

		public ClassComboModel()
		{
			updateModel();
		}

		public void updateModel()
		{
			Object pcClass = getSelectedItem();
			classList.clear();
			for (Iterator it = Globals.getClassList().iterator(); it.hasNext();)
			{
				final PCClass aClass = (PCClass)it.next();
				if (aClass.isVisible() && accept(aClass))
				{
					classList.add(aClass);
				}

			}
			setSelectedItem(pcClass);

			fireContentsChanged(this, 0, this.getSize());
		}

		public int getSize()
		{
			return classList.size();
		}

		public void setSelectedItem(Object anItem)
		{
			String className = null;
			int index = 0;

			if (anItem instanceof String)
				className = (String)anItem;
			else if (anItem instanceof PCClass)
				className = anItem.toString();
			else
				return;

			Iterator i = classList.iterator();
			while (i.hasNext())
			{
				final PCClass theClass = (PCClass)i.next();
				if (theClass.toString().equals(className))
				{
					selectedIndex = index;
					return;
				}
				index++;
			}
			selectedIndex = -1;
		}

		public Object getSelectedItem()
		{
			if (selectedIndex > -1)
			{
				final PCClass theClass = (PCClass)classList.get(selectedIndex);
				return theClass;
			}
			else
			{
				return null;
			}

		}

		public Object getElementAt(int index)
		{
			if (classList.size() != 0)
			{
				final PCClass theClass = (PCClass)classList.get(index);
				return theClass;
			}
			return null;
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
		if (raceModel != null)
			raceModel.updateModel();
		if (classModel != null)
			classModel.updateModel();
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

	protected class RendererEditor implements TableCellRenderer
	{
		JButton plusButton = new JButton("+");
		JButton minusButton = new JButton("-");
		DefaultTableCellRenderer def = new DefaultTableCellRenderer();

		public RendererEditor()
		{
			def.setBackground(InfoSummary.this.getBackground());
			def.setAlignmentX(Component.CENTER_ALIGNMENT);
			def.setHorizontalAlignment(SwingConstants.CENTER);
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
}
