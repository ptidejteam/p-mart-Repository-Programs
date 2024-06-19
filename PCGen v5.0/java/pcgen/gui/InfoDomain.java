/*
 * InfoDomain.java
 * Copyright 2001 (C) Mario Bonassin
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
 * Modified Nov 14, 2002 by David Hibbs
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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.StringTokenizer;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;
import pcgen.core.CharacterDomain;
import pcgen.core.Constants;
import pcgen.core.Deity;
import pcgen.core.Domain;
import pcgen.core.Globals;
import pcgen.core.PCClass;
import pcgen.core.PlayerCharacter;
import pcgen.core.SettingsHandler;
import pcgen.gui.editor.EditorConstants;
import pcgen.gui.filter.FilterAdapterPanel;
import pcgen.gui.filter.FilterFactory;
import pcgen.gui.panes.FlippingSplitPane;
import pcgen.util.PropertyFactory;

/**
 * This class is responsible for drawing the domain related window - including
 * indicating what deity and domains are available, which ones are selected,
 * and handling the selection/de-selection of both.
 *
 * @author  Mario Bonassin
 * @version $Revision: 1.1 $
 * modified by Bryan McRoberts (merton_monk@yahoo.com) to connect to
 * pcgen.core package
 * modified by David Hibbs to use Deity and Domain objects instead of Strings
 * and to clean up the code
 */

class InfoDomain extends FilterAdapterPanel
{
	private JLabel deityName;
	private JTableEx domainTable = null;
	private JTableEx deityTable = null;
	private DeityModel deityModel = new DeityModel();
	private DomainModel domainModel = new DomainModel();

	// sage_sam updated 11/13/2002 to match a change elsewhere in the code
	private final int[] deiColumnWidth = {60, 231, 36, 71};

	private TableSorter deitySorter = null;
	private TableSorter domainSorter = null;
	private Border etched;
	private static PlayerCharacter aPC = null;

	private JScrollPane domainScroll = new JScrollPane();
	private JScrollPane deityScroll = new JScrollPane();
	private JLabelPane domainInfo = new JLabelPane();
	private JLabelPane deityInfo = new JLabelPane();
	private JLabel domSelected;
	private JLabel ofLabel;

	// author: Thomas Behr 08-02-02
	private JLabel domChosen = new JLabel();
	private JLabel domTotal = new JLabel();

	private static ArrayList selectedDomainList = new ArrayList();
	private static boolean needsUpdate = true;
	private JButton deitySelect;
	private JButton domainSelect;
	private boolean hasBeenSized = false;
	private FlippingSplitPane splitPane;
	private FlippingSplitPane bSplit;
	private FlippingSplitPane aSplit;
	private JPanel center = new JPanel();
	private static int splitOrientation = FlippingSplitPane.HORIZONTAL_SPLIT;
	private int numDomains = 0;

	// Note these arrays must be set after we have loaded the values of the
	// properties above.
	private static final String[] s_columnNames = new String[]{PropertyFactory.getString("in_nameLabel"), PropertyFactory.getString("in_domains"), PropertyFactory.getString("in_alignLabel"), PropertyFactory.getString("in_sourceLabel")};
	private static final String[] s_domainColList = new String[]{PropertyFactory.getString("in_domains"), PropertyFactory.getString("in_sourceLabel")};

	/**
	 * This class is used as a pop-up menu for the domain and deity tables.
	 */
	private final class ClassPopupMenu extends JPopupMenu
	{
		private class ClassActionListener implements ActionListener
		{
			public void actionPerformed(ActionEvent evt)
			{
			}
		}

		private class AddClassActionListener extends ClassActionListener
		{
			public void actionPerformed(ActionEvent evt)
			{
				InfoDomain.this.deitySelect.doClick();
			}
		}

		private class RemoveClassActionListener extends ClassActionListener
		{
			public void actionPerformed(ActionEvent evt)
			{
				InfoDomain.this.domainSelect.doClick();
			}
		}

		private JMenuItem createAddMenuItem(String label, String accelerator)
		{
			return Utility.createMenuItem(label, new AddClassActionListener(), PropertyFactory.getString("in_select"), (char) 0, accelerator, PropertyFactory.getString("in_selDeity"), "Add16.gif", true);
		}

		private JMenuItem createRemoveMenuItem(String label, String accelerator)
		{
			return Utility.createMenuItem(label, new RemoveClassActionListener(), PropertyFactory.getString("in_select"), (char) 0, accelerator, PropertyFactory.getString("in_selDomain"), "Add16.gif", true);
		}

		ClassPopupMenu(JTableEx treeTable)
		{
			if (treeTable == deityTable)
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
				ClassPopupMenu.this.add(createAddMenuItem(PropertyFactory.getString("in_select"), "control EQUALS"));
				this.addSeparator();
				ClassPopupMenu.this.add(Utility.createMenuItem(PropertyFactory.getString("in_editDeity"), new ActionListener()
				{
					public void actionPerformed(ActionEvent e)
					{
						editDeityButtonClick();
					}
				}, "editDeity", (char) 0, "alt E", PropertyFactory.getString("in_editDeity"), null, true));
				ClassPopupMenu.this.add(Utility.createMenuItem(PropertyFactory.getString("in_createDeity"), new ActionListener()
				{
					public void actionPerformed(ActionEvent e)
					{
						createDeityButtonClick();
					}
				}, "createDeity", (char) 0, "alt C", PropertyFactory.getString("in_createDeity"), null, true));
				ClassPopupMenu.this.add(Utility.createMenuItem(PropertyFactory.getString("in_delDeity"), new ActionListener()
				{
					public void actionPerformed(ActionEvent e)
					{
						deleteDeityButtonClick();
					}
				}, "deleteDeity", (char) 0, "DELETE", PropertyFactory.getString("in_delDeity"), null, true));
			}

			else // domainTable
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
				ClassPopupMenu.this.add(createRemoveMenuItem(PropertyFactory.getString("in_select"), "control EQUALS"));
			}
		}
	}

	/**
	 * This class is a listener for the pop-up menus on the domain and
	 * deity tables.
	 */
	private final class ClassPopupListener extends MouseAdapter
	{
		private ClassPopupMenu menu;
		private JTableEx aTable;

		ClassPopupListener(JTableEx treeTable, ClassPopupMenu aMenu)
		{
			menu = aMenu;
			aTable = treeTable;
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
				int selRow = aTable.getSelectedRow();
				if (selRow == -1)
				{
					return;
				}
				menu.show(evt.getComponent(), evt.getX(), evt.getY());
			}
		}
	}

	private void hookupPopupMenu(JTableEx treeTable)
	{
		treeTable.addMouseListener(new ClassPopupListener(treeTable, new ClassPopupMenu(treeTable)));
	}

	/**
	 * Default constructor for this tab.
	 */
	InfoDomain()
	{
		// do not remove this
		// we will use the component's name to save
		// component specific settings
		setName(Constants.tabNames[Constants.TAB_DOMAINS]);

		aPC = Globals.getCurrentPC();
		initComponents();

		FilterFactory.restoreFilterSettings(this);
	}

	/**
	 * This method sets that this tab needs to update its displayed information
	 * the next time that it is selected.
	 */
	public static void setNeedsUpdate(boolean flag)
	{
		needsUpdate = flag;
	}

	/**
	 * This method builds the GUI components.
	 */
	private void initComponents()
	{
		// Set the tab description
		Utility.setDescription(this, PropertyFactory.getString("in_tabToolTip"));

		// Deity table set up
		deitySorter = new TableSorter(deityModel);
		deityTable = new JTableEx(deitySorter);
		deitySorter.addMouseListenerToHeaderInTable(deityTable);
		deityTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		// Deity table tooltip
		Utility.setDescription(deityTable, PropertyFactory.getString("in_deityTableTip"));

		// Set up Deity table column sizes.
		TableColumn column = null;
		for (int i = 0; i < s_columnNames.length; i++)
		{
			column = deityTable.getColumnModel().getColumn(i);
			column.setPreferredWidth(deiColumnWidth[i]);
		}

		// Deity table mouse listener
		final DeityMouseAdapter deityMouse = new DeityMouseAdapter();
		deityTable.addMouseListener(deityMouse);

		// Domain table Setup
		domainSorter = new TableSorter(domainModel);
		domainTable = new JTableEx(domainSorter);
		domainSorter.addMouseListenerToHeaderInTable(domainTable);
		domainTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		// Domain table tooltip
		Utility.setDescription(domainTable, PropertyFactory.getString("in_domainTableTip"));

		// Domain table mouse listener
		final DomainMouseAdapter domainMouse = new DomainMouseAdapter();
		domainTable.addMouseListener(domainMouse);

		center.setLayout(new BorderLayout());

		GridBagLayout gridbag = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		JPanel leftPane = new JPanel();
		JPanel rightPane = new JPanel();
		leftPane.setLayout(gridbag);
		splitPane = new FlippingSplitPane(splitOrientation, leftPane, rightPane);
		splitPane.setOneTouchExpandable(true);
		splitPane.setDividerSize(10);
		splitPane.setDividerLocation(350);
		center.add(splitPane, BorderLayout.CENTER);

		Utility.buildConstraints(c, 0, 0, 1, 1, 100, 5);
		c.fill = GridBagConstraints.VERTICAL;
		c.anchor = GridBagConstraints.CENTER;
		JPanel aPanel = new JPanel();
		gridbag.setConstraints(aPanel, c);
		JLabel aLabel = new JLabel(PropertyFactory.getString("in_deity") + ": ");
		aPanel.add(aLabel);
		deityName = new JLabel(PropertyFactory.getString("in_nameLabel"));
		aPanel.add(deityName);
		deitySelect = new JButton(PropertyFactory.getString("in_select"));
		Utility.setDescription(deitySelect, PropertyFactory.getString("in_deityButTip"));
		deitySelect.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				final ListSelectionModel lsm = deityTable.getSelectionModel();
				final int selectedRow = deitySorter.getRowTranslated(lsm.getMinSelectionIndex());
				InfoDomain.this.selectDeityIndex(selectedRow);
			}
		});

		aPanel.add(deitySelect);
		leftPane.add(aPanel);

		Utility.buildConstraints(c, 0, 1, 1, 1, 0, 95);
		c.fill = GridBagConstraints.BOTH;
		c.anchor = GridBagConstraints.CENTER;
		JScrollPane scrollPane = new JScrollPane(deityTable);
		gridbag.setConstraints(scrollPane, c);
		leftPane.add(scrollPane);

		gridbag = new GridBagLayout();
		c = new GridBagConstraints();
		rightPane.setLayout(gridbag);

		Utility.buildConstraints(c, 0, 0, 1, 1, 100, 5);
		c.fill = GridBagConstraints.VERTICAL;
		c.anchor = GridBagConstraints.CENTER;
		aPanel = new JPanel();
		gridbag.setConstraints(aPanel, c);
		domSelected = new JLabel(PropertyFactory.getString("in_domainSelected") + ": ");
		ofLabel = new JLabel(PropertyFactory.getString("in_ofString"));
		aPanel.add(domSelected);
		aPanel.add(domChosen);
		aPanel.add(ofLabel);
		aPanel.add(domTotal);
		domainSelect = new JButton(PropertyFactory.getString("in_select"));
		Utility.setDescription(domainSelect, PropertyFactory.getString("in_domainButTip"));
		domainSelect.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				final ListSelectionModel lsm = domainTable.getSelectionModel();
				final int selectedRow = domainSorter.getRowTranslated(lsm.getMinSelectionIndex());
				InfoDomain.this.selectDomainIndex(selectedRow);
			}
		});

		aPanel.add(domainSelect);
		rightPane.add(aPanel);

		Utility.buildConstraints(c, 0, 1, 1, 1, 0, 95);
		c.fill = GridBagConstraints.BOTH;
		c.anchor = GridBagConstraints.CENTER;
		scrollPane = new JScrollPane(domainTable);
		gridbag.setConstraints(scrollPane, c);
		rightPane.add(scrollPane);

		TitledBorder title1 = BorderFactory.createTitledBorder(etched, PropertyFactory.getString("in_deityInfo"));
		title1.setTitleJustification(TitledBorder.CENTER);
		deityScroll.setBorder(title1);
		deityInfo.setBackground(rightPane.getBackground());
		deityScroll.setViewportView(deityInfo);
		Utility.setDescription(deityScroll, PropertyFactory.getString("in_infoScrollTip"));

		TitledBorder title2 = BorderFactory.createTitledBorder(etched, PropertyFactory.getString("in_domainInfo"));
		title2.setTitleJustification(TitledBorder.CENTER);
		domainScroll.setBorder(title2);
		domainInfo.setBackground(rightPane.getBackground());
		domainScroll.setViewportView(domainInfo);
		Utility.setDescription(domainScroll, PropertyFactory.getString("in_infoScrollTip"));

		aSplit = new FlippingSplitPane(FlippingSplitPane.HORIZONTAL_SPLIT, deityScroll, domainScroll);
		aSplit.setOneTouchExpandable(true);
		aSplit.setDividerSize(10);
		aSplit.setDividerLocation(300);

		JPanel botPane = new JPanel();
		botPane.setLayout(new BorderLayout());
		botPane.add(aSplit, BorderLayout.CENTER);
		bSplit = new FlippingSplitPane(FlippingSplitPane.VERTICAL_SPLIT, center, botPane);
		bSplit.setOneTouchExpandable(true);
		bSplit.setDividerSize(10);
		bSplit.setDividerLocation(300);

		this.setLayout(new BorderLayout());
		this.add(bSplit, BorderLayout.CENTER);

		// Make sure it updates when switching tabs
		this.addFocusListener(new FocusAdapter()
		{
			public void focusGained(FocusEvent evt)
			{
				updateCharacterInfo();
			}
		});

		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

		addComponentListener(new ComponentAdapter()
		{
			public void componentShown(ComponentEvent evt)
			{
				formComponentShown();
			}
		});

		hookupPopupMenu(deityTable);
		hookupPopupMenu(domainTable);
	}

	/**
	 * executed when the component is shown
	 */
	private void formComponentShown()
	{
		int width;

		requestFocus();
		PCGen_Frame1.getStatusBar().setText(PropertyFactory.getString("in_statusBarDeity"));
		updateCharacterInfo();
		int splitPaneDividerLocation = splitPane.getDividerLocation();
		int bSplitDividerLocation = bSplit.getDividerLocation();
		int aSplitDividerLocation = aSplit.getDividerLocation();
		if (!hasBeenSized)
		{
			hasBeenSized = true;
			final double thisWidth = this.getSize().getWidth();
			splitPaneDividerLocation = SettingsHandler.getPCGenOption("InfoDomain.splitPane", (int) (thisWidth * 4 / 10));
			bSplitDividerLocation = SettingsHandler.getPCGenOption("InfoDomain.bSplit", (int) (this.getSize().getHeight() * 75 / 100));
			aSplitDividerLocation = SettingsHandler.getPCGenOption("InfoDomain.aSplit", (int) (thisWidth * 5 / 10));

			// set the prefered width on deityTable
			for (int i = 0; i < deityTable.getColumnCount(); i++)
			{
				TableColumn sCol = deityTable.getColumnModel().getColumn(i);
				width = Globals.getCustColumnWidth(PropertyFactory.getString("in_deity"), i);
				if (width != 0)
				{
					sCol.setPreferredWidth(width);
				}
				sCol.addPropertyChangeListener(new ResizeColumnListener(deityTable, PropertyFactory.getString("in_deity"), i));
			}

			// set the prefered width on domainTable
			for (int i = 0; i < domainTable.getColumnCount(); i++)
			{
				TableColumn sCol = domainTable.getColumnModel().getColumn(i);
				width = Globals.getCustColumnWidth(PropertyFactory.getString("in_domains"), i);
				if (width != 0)
				{
					sCol.setPreferredWidth(width);
				}
				sCol.addPropertyChangeListener(new ResizeColumnListener(domainTable, PropertyFactory.getString("in_domains"), i));
			}

		}
		if (splitPaneDividerLocation > 0)
		{
			splitPane.setDividerLocation(splitPaneDividerLocation);
			SettingsHandler.setPCGenOption("InfoDomain.splitPane", splitPaneDividerLocation);
		}
		if (bSplitDividerLocation > 0)
		{
			bSplit.setDividerLocation(bSplitDividerLocation);
			SettingsHandler.setPCGenOption("InfoDomain.bSplit", bSplitDividerLocation);
		}
		if (aSplitDividerLocation > 0)
		{
			aSplit.setDividerLocation(aSplitDividerLocation);
			SettingsHandler.setPCGenOption("InfoDomain.aSplit", aSplitDividerLocation);
		}
	}

	/**
	 * This method displays the descriptive information about the
	 * selected domain.
	 */
	private void setDomainInfoText(Domain aDomain)
	{
		StringBuffer infoText = new StringBuffer().append("<html>");
		if (aDomain != null)
		{
			infoText.append("<b>").append(aDomain.piSubString());
			String aString = aDomain.getDescription();
			if (aString.length() != 0)
			{
				infoText.append(" &nbsp;").append(PropertyFactory.getString("in_domainGrant")).append("</b>:").append(aString);
			}
			aString = aDomain.preReqHTMLStrings(false);
			if (aString.length() != 0)
			{
				infoText.append(" &nbsp;<b>").append(PropertyFactory.getString("in_requirements")).append("</b>:").append(aString);
			}
		}
		infoText.append("</html>");
		domainInfo.setText(infoText.toString());
	}

	/**
	 * This method displays the descriptive information about the
	 * selected deity.
	 */
	private void setDeityInfoText(Deity aDeity)
	{
		if (aDeity != null)
		{
			StringBuffer infoText = new StringBuffer().append("<html><b>").append(aDeity.piSubString());
			infoText.append(" &nbsp;").append(PropertyFactory.getString("in_descrip")).append("</b>:").append(aDeity.piDescString());

			String aString = aDeity.preReqHTMLStrings(false);
			if (aString.length() != 0)
			{
				infoText.append(" &nbsp;<b>").append(PropertyFactory.getString("in_requirements")).append("</b>:").append(aString);
			}
			aString = aDeity.getFavoredWeapon();
			if (aString.length() != 0)
			{
				infoText.append(" &nbsp;<b>").append(PropertyFactory.getString("in_deityFavWeap")).append("</b>:").append(aString);
			}
			aString = aDeity.getHolyItem();
			if (aString.length() != 0)
			{
				infoText.append(" &nbsp;<b>").append(PropertyFactory.getString("in_deityHolyIt")).append("</b>:").append(aString);
			}
			infoText.append("</html>");
			deityInfo.setText(infoText.toString());
		}
		else
		{
			deityInfo.setText();
		}
	}

	/**
	 * <code>updateCharacterInfo</code> update data for a changed PC
	 **/
	private final void updateCharacterInfo()
	{
		if (aPC != null && numDomains != aPC.getCharacterDomainList().size())
		{
			needsUpdate = true;
			numDomains = aPC.getCharacterDomainList().size();
		}
		if (needsUpdate || aPC == null || !aPC.equals(Globals.getCurrentPC()))
		{
			aPC = Globals.getCurrentPC();
			if (aPC == null)
			{
				return;
			}

			// Update the list of deities
			ArrayList deityList = deityModel.getData();
			deityList.clear();
			for (Iterator it = Globals.getDeityList().iterator(); it.hasNext();)
			{
				final Deity aDeity = (Deity) it.next();
				if (accept(aDeity))
				{
					deityList.add(aDeity);
				}
			}
			deityModel.fireTableDataChanged();

			// Set the displayed deity name
			if (aPC.getDeity() != null)
			{
				deityName.setText(aPC.getDeity().piString());
			}
			else
			{
				deityName.setText(PropertyFactory.getString("in_tempName"));
			}

			// Display the deity description
			setDeityInfoText(aPC.getDeity());

			// Build the domain lists
			buildDomainLists();

			needsUpdate = false;
		}
	}

	/**
	 * This method builds the lists of domains.  The lists built by this method
	 * include the list of available domains and the list of domains currently
	 * selected for the character.
	 */
	private void buildDomainLists()
	{

		// Init the lists
		ArrayList availDomainList = domainModel.getAvailDomainList();
		selectedDomainList.clear();
		availDomainList.clear();

		// Get all available domains and filter them
		addUnfilteredDomains(availDomainList, aPC.getDeity());

		// Loop through the character's selected domains
		for (int i = 0; i < aPC.getCharacterDomainList().size(); i++)
		{
			final CharacterDomain aCD = (CharacterDomain) aPC.getCharacterDomainList().get(i);

			if (aCD != null && aCD.getDomain() != null)
			{
				// Get the selected domain
				final Domain aCDDomain = aCD.getDomain();

				// Remove domains that are not available from the selected list
				if (!availDomainList.contains(aCDDomain))
				{
					aCD.setDomain(null);
					forceUpdates();
				}
				else
				{
					// The selected list should contain the selected domains (duh)
					if (!selectedDomainList.contains(aCDDomain))
					{
						selectedDomainList.add(aCDDomain); // list of pc's choices
					}
				}
			}
		}

		// Filter the available domains
		Iterator domainIter = availDomainList.iterator();
		while (domainIter.hasNext())
		{
			Domain domain = (Domain) domainIter.next();
			if (!(accept(domain) || selectedDomainList.contains(domain)))
			{
				domainIter.remove();
			}
		}

		// Update the display of available/selected domain counts
		domTotal.setText(Integer.toString(aPC.getCharacterDomainList().size()));
		// use star (*) to identify which are chosen in the table
		domChosen.setText(Integer.toString(selectedDomainList.size()) + "*");

		// Notify the table and sorter that the table data has changed
		domainSorter.tableChanged(null);
		domainModel.fireTableDataChanged();
	}

	/**
	 * This method adds all available domains to the given list, without
	 * filtering.
	 * @param pcDeity Deity selected for the current character
	 */
	private static final void addUnfilteredDomains(final ArrayList availDomainList, final Deity pcDeity)
	{

		availDomainList.clear();

		if (pcDeity != null)
		{
			Iterator iter = pcDeity.getDomainList().iterator();
			Domain aDomain = null;
			while (iter.hasNext())
			{
				aDomain = (Domain) iter.next();
				if (!availDomainList.contains(aDomain))
				{
					availDomainList.add(aDomain);
				}
			}
		}

		// Loop through the available prestige domains
		for (Iterator i = aPC.getClassList().iterator(); i.hasNext();)
		{
			final PCClass aClass = (PCClass) i.next();
			for (Iterator d = aClass.getAddDomains().iterator(); d.hasNext();)
			{
				final String prestigeString = d.next().toString();
				final StringTokenizer domainTok = new StringTokenizer(prestigeString, "|", false);

				final int level = Integer.parseInt(domainTok.nextToken());
				if (aClass.getLevel() >= level)
				{
					while (domainTok.hasMoreTokens())
					{
						final String prestigeName = domainTok.nextToken();
						final Domain prestigeDomain = Globals.getDomainNamed(prestigeName);

						if (!availDomainList.contains(prestigeDomain))
						{
							availDomainList.add(prestigeDomain);
						}
					}
				}
			}
		}
	}

	/**
	 * This method is called when a domain is selected from the list of displayed
	 * domains.
	 * @param selectedRow int row in the domain table model where the selected
	 * domain can be found.
	 */
	private final void selectDomainIndex(int selectedRow)
	{
		final Domain addedDomain = (Domain) domainModel.getValueAt(selectedRow, -1);
		if (addedDomain == null)
		{
			return;
		}

		// Make sure a valid domain was selected
		if (!addedDomain.qualifiesForDomain())
		{
			JOptionPane.showMessageDialog(null, PropertyFactory.getString("in_qualifyMess") + addedDomain.getName(), Constants.s_APPNAME, JOptionPane.INFORMATION_MESSAGE);
			return;
		}

		// Loop through slots available on character to select domains
		final int characterDomains = aPC.getCharacterDomainList().size();
		for (int i = 0; i < characterDomains; i++)
		{
			final CharacterDomain aCD = (CharacterDomain) aPC.getCharacterDomainList().get(i);
			if (aCD == null)
			{
				continue;
			}

			// If adding a domain already selected, remove the domain
			final Domain existingDomain = aCD.getDomain();
			if (existingDomain != null && existingDomain.equals(addedDomain))
			{
				selectedDomainList.remove(existingDomain);
				aCD.setDomain(null);
				break;
			}

			// If space remains for another domain, add it
			if (existingDomain == null || i == characterDomains - 1)
			{
				// if end is reached and previous domain is found, no
				// space is left for new domains
				if (existingDomain != null)
				{
					JOptionPane.showMessageDialog(null, PropertyFactory.getString("in_errorNoDomains") + " " + addedDomain.getName(), Constants.s_APPNAME, JOptionPane.INFORMATION_MESSAGE);
					return;
				}
				// otherwise, add the domain.
				else
				{
					aCD.setDomain(addedDomain).setIsLocked(true);
					if (!selectedDomainList.contains(addedDomain))
					{
						selectedDomainList.add(addedDomain);
					}
				}
				break;
			}
		}

		// Update the displayed domain count, using star (*) to indicate selected domains
		domChosen.setText(Integer.toString(selectedDomainList.size()) + "*");
		domainSorter.tableChanged(null);
		domainModel.fireTableDataChanged();
		forceUpdates();
	}

	/**
	 * This method is called when a deity is selected from the list of displayed
	 * deities.
	 * @param selectedRow int row in the deity table model where the selected
	 * deity can be found.
	 */
	private final void selectDeityIndex(int selectedRow)
	{
		final Deity aDeity = (Deity) deityModel.getValueAt(selectedRow, -1);
		if (aDeity == null)
		{
			return;
		}

		// Don't do anything if the same deity was selected
		if ((aPC.getDeity() != null) && (aDeity.equals(aPC.getDeity())))
		{
			return;
		}

		if (!aPC.canSelectDeity(aDeity))
		{
			final ListSelectionModel lsm = deityTable.getSelectionModel();
			JOptionPane.showMessageDialog(null, PropertyFactory.getString("in_reqMess") + aDeity.getName() + ".", Constants.s_APPNAME, JOptionPane.INFORMATION_MESSAGE);
			lsm.clearSelection();
			return;
		}

		ArrayList potentialDomains = new ArrayList();
		addUnfilteredDomains(potentialDomains, aDeity);

		// Validate that no domains will be lost when changing deities
		Iterator selectedIter = selectedDomainList.iterator();
		boolean allDomainsAvailable = true;
		while (selectedIter.hasNext())
		{
			Object domain = selectedIter.next();
			if (!potentialDomains.contains(domain))
			{
				allDomainsAvailable = false;
				break;
			}
		}

		if (!allDomainsAvailable)
		{
			final int areYouSure = JOptionPane.showConfirmDialog(null, PropertyFactory.getString("in_confDomLost1") + " " + aDeity.getName() + System.getProperty("line.separator") + PropertyFactory.getString("in_confDomLost2"), Constants.s_APPNAME, JOptionPane.OK_CANCEL_OPTION);
			if (areYouSure != JOptionPane.OK_OPTION)
			{
				return;
			}
		}

		aPC.setDeity(aDeity);
		deityName.setText(aDeity.piString());

		buildDomainLists();

		deityModel.fireTableDataChanged();
	}

	/**
	 * This is the Model that populates the table for Deities
	 */
	private static final class DeityModel extends AbstractTableModel
	{
		private ArrayList deities = new ArrayList();

		public final ArrayList getData()
		{
			return deities;
		}

		/**
		 * Return the value of a grid cell by using the information from the global
		 * deity list.
		 *
		 * @see pcgen.core.Globals
		 */
		public Object getValueAt(int row, int column)
		{
			Object retVal = "";
			if (deities != null && row >= 0 && row < deities.size())
			{
				final Deity aDeity = (Deity) deities.get(row);
				final PlayerCharacter aPC = InfoDomain.aPC;
				switch (column)
				{
					case -1: // sneaky case to just get the deity
						return aDeity;
					case 0:
						//removed the case for currently selected deity getting bolded, since it would conflict
						// with NAMEISPI bold-italics... the currently selected deity is already shown above the table
						if (aPC != null && aPC.canSelectDeity(aDeity))
						{
							retVal = aDeity.piString();
						}
						else
						{
							retVal = new StringBuffer().append("<html>").append(SettingsHandler.getPrereqFailColorAsHtml()).append(aDeity.piSubString()).append("</font></html>").toString();
						}
						break;
					case 1:
						retVal = aDeity.getDomainListPIString();
						break;
					case 2:
						retVal = aDeity.getAlignment();
						break;
					case 3:
						retVal = aDeity.getSource();
						break;
					default :
						Globals.errorPrint(PropertyFactory.getString("in_domIDEr1") + " " + column + " " + PropertyFactory.getString("in_domIDEr2"));
						break;
				}
			}
			return retVal;
		}

		/**
		 * Return the current number of rows in the table based on the value from
		 * the global feat list.
		 *
		 * @return the number of rows
		 */
		public int getRowCount()
		{
			if (Globals.getDeityList() != null)
			{
				return deities.size();
			}
			else
			{
				return 0;
			}
		}

		/**
		 * Return the column name.
		 *
		 * @param column the number of the column 0...getColumnCount()-1.
		 * @return the name of the column
		 */
		public String getColumnName(int column)
		{
			return s_columnNames[column];
		}

		/**
		 * Return the number of columns in the table.
		 *
		 * @return the number of columns
		 */
		public int getColumnCount()
		{
			return s_columnNames.length;
		}

		public Class getColumnClass(int columnIndex)
		{
			return String.class;
		}

		public boolean isCellEditable(int row, int column)
		{
			return false;
		}
	}

	/**
	 * This class is a MouseAdapter to handle mouse clickes on the deity table.
	 * Double-Clicks select the deity while single clicks simply display the
	 * information about the selected deity.
	 */
	private final class DeityMouseAdapter extends MouseAdapter
	{
		public void mouseClicked(MouseEvent f)
		{
			final ListSelectionModel lsm = deityTable.getSelectionModel();
			final int selectedRow = deitySorter.getRowTranslated(lsm.getMinSelectionIndex());

			switch (f.getClickCount())
			{
				case (1):
					if (selectedRow >= 0)
					{
						final Deity aDeity = (Deity) deityModel.getValueAt(selectedRow, -1);
						setDeityInfoText(aDeity);
					}
					else
					{
						setDeityInfoText(null);
					}
					break;

				case (2):
					if (selectedRow >= 0)
					{
						InfoDomain.this.selectDeityIndex(selectedRow);
					}
					break;
				default:
					//Do nothing
					break;
			} // end of switch
		}
	}

	/**
	 * This is the Model that populate the table for Domains
	 */
	private final class DomainModel extends AbstractTableModel
	{
		private ArrayList availDomainList = new ArrayList();

		private DomainModel()
		{
		}

		/* return list of domains associated with the current deity */
		/* sets the list of appropriate choices */
		/* returns the list of selections in order of selection */
		public final ArrayList getAvailDomainList()
		{
			return availDomainList;
		}

		public int getColumnCount()
		{
			return s_domainColList.length;
		}

		public int getRowCount()
		{
			return availDomainList.size();
		}

		public Object getValueAt(int row, int col)
		{
			if (row < 0 || row >= availDomainList.size())
			{
				return "";
			}
			final Domain aDomain = (Domain) availDomainList.get(row);
			if (aDomain == null)
			{
				return null;
			}

			StringBuffer retVal = new StringBuffer(80);
			switch (col)
			{
				case -1: // return domain object for the row selected
					return aDomain;
				case 0:
					// the case where selected domains are bolded is insufficent becuase it conflicts
					// with PI-formatting (bold-italic), so I added an asterisk
					if (InfoDomain.selectedDomainList.contains(availDomainList.get(row)))
					{
						retVal.append("<html><b>").append(aDomain.piSubString()).append("*</b></html>");
					}
					else if (aDomain != null && !aDomain.qualifiesForDomain())
					{
						retVal.append("<html>").append(SettingsHandler.getPrereqFailColorAsHtml()).append(aDomain.piSubString()).append("</font></html>");
					}
					else
					{
						retVal.append(aDomain.piString());
					}
					break;
				case 1:
					try
					{
						retVal.append(aDomain.getSource());
					}
					catch (Exception exc)
					{
						Globals.errorPrint(PropertyFactory.getString("in_errorMess"), exc);
					}
					break;
				default :
					Globals.errorPrint(PropertyFactory.getString("in_domIDEr1") + " " + col + " " + PropertyFactory.getString("in_domIDEr3"));
					break;
			}
			return retVal.toString();
		}

		// The default implementations of these methods in
		// AbstractTableModel would work, but we can refine them.
		public String getColumnName(int column)
		{
			return s_domainColList[column];
		}

		public Class getColumnClass(int col)
		{
			return getValueAt(0, col).getClass();
		}

	}

	/**
	 * This class is a MouseAdapter to handle mouse clickes on the domain table.
	 * Double-Clicks select the domain while single clicks simply display the
	 * information about the selected domain.
	 */
	private final class DomainMouseAdapter extends MouseAdapter
	{
		private final ListSelectionModel lsm = domainTable.getSelectionModel();

		public final void mouseClicked(MouseEvent f)
		{
			final int selectedRow = domainSorter.getRowTranslated(lsm.getMinSelectionIndex());

			if (selectedRow < 0)
			{
				return;
			}

			switch (f.getClickCount())
			{
				case (1):
					final String domainName = domainModel.getValueAt(selectedRow, -1).toString();
					if (domainName != null)
					{
						final Domain aDomain = Globals.getDomainNamed(domainName);
						setDomainInfoText(aDomain);
					}
					break;

				case (2):
					InfoDomain.this.selectDomainIndex(selectedRow);
					break;
			}
		}
	}

	/*
	 * ##########################################################
	 * filter stuff
	 * ##########################################################
	 */

	/**
	 * implementation of Filterable interface
	 */
	public final void initializeFilters()
	{
		FilterFactory.registerAllSourceFilters(this);
		FilterFactory.registerAllDeityFilters(this);
	}

	/**
	 * implementation of Filterable interface
	 */
	public final void refreshFiltering()
	{
		needsUpdate = true;
		updateCharacterInfo();
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

	private static void forceUpdates()
	{
		aPC.setDirty(true);
		needsUpdate = true;
		PCGen_Frame1.forceUpdate_InfoSpells();
		PCGen_Frame1.forceUpdate_InfoFeats();
	}

	private void deleteDeityButtonClick()
	{
		Deity aDeity = null;
		final ListSelectionModel lsm = deityTable.getSelectionModel();
		final int selectedRow = deitySorter.getRowTranslated(lsm.getMinSelectionIndex());
		if (selectedRow >= 0)
		{
			aDeity = (Deity) deityModel.getValueAt(selectedRow, -1);
			if (aDeity != null)
			{
				if (aDeity.isType(Constants.s_CUSTOM))
				{
					final int areYouSure = JOptionPane.showConfirmDialog(null, PropertyFactory.getString("in_delDeity") + aDeity.getName() + "?", Constants.s_APPNAME, JOptionPane.OK_CANCEL_OPTION);
					if (areYouSure != JOptionPane.OK_OPTION)
					{
						return;
					}
					Globals.getDeityList().remove(aDeity);
				}
				else
				{
					JOptionPane.showMessageDialog(null, PropertyFactory.getString("in_domIDEr4"), Constants.s_APPNAME, JOptionPane.ERROR_MESSAGE);
				}
			}
		}
	}

	private static void createDeityButtonClick()
	{
		//new DeityEditorMain(Globals.getRootFrame(), null).show();
		LstEditorMain lem = new LstEditorMain();
		lem.show();
		lem.editIt(null, EditorConstants.EDIT_DEITY);
	}

	private void editDeityButtonClick()
	{
		Deity aDeity = null;
		final ListSelectionModel lsm = deityTable.getSelectionModel();
		final int row = lsm.getMinSelectionIndex();
		if (row >= 0)
		{
			final int selectedRow = deitySorter.getRowTranslated(lsm.getMinSelectionIndex());
			if (selectedRow >= 0)
			{
				aDeity = (Deity) deityModel.getValueAt(selectedRow, -1);
			}
		}
		//new DeityEditorMain(Globals.getRootFrame(), aDeity).show();
		LstEditorMain lem = new LstEditorMain();
		lem.show();
		lem.editIt(aDeity, EditorConstants.EDIT_DEITY);
	}

}
