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
 */

/**
 * This class is responsible for drawing the domain related window - including
 * indicating what deity and domains are available, which ones are selected, and handling
 * the selection/de-selection of both.


 /**
 * @author  Mario Bonassin
 * @version $Revision: 1.1 $
 * modified by Bryan McRoberts (merton_monk@yahoo.com) to connect to pcgen.core package
 */
package pcgen.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.StringTokenizer;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;
import pcgen.core.CharacterDomain;
import pcgen.core.Deity;
import pcgen.core.Domain;
import pcgen.core.Globals;
import pcgen.core.PCClass;
import pcgen.core.PlayerCharacter;
import pcgen.gui.filter.FilterAdapterPanel;
import pcgen.gui.filter.FilterFactory;

public class InfoDomain extends FilterAdapterPanel
{
	private JLabel deityName;
	private JTableEx domainTable = null;
	private JTableEx deityTable = null;
	private DeityModel deityModel = new DeityModel();
	private DomainModel domModel = new DomainModel();

	private final Object[] deiLongValues = {"Namexxxxxxx", "Domainsxxxxxxxxxx", "Alignment", "Sourcexxxxxxx"};

	private TableSorter sorter = null;
	private TableSorter sorter2 = null;
	private Border etched;
	private TitledBorder titled;
	protected static PlayerCharacter aPC = null;

	private JScrollPane domainScroll = new JScrollPane();
	private JScrollPane deityScroll = new JScrollPane();
	private JLabelPane domainInfo = new JLabelPane();
	private JLabelPane deityInfo = new JLabelPane();
	private JLabel domSelected;
	private JLabel ofLabel;

	// author: Thomas Behr 08-02-02
	private JLabel domChosen = new JLabel();
	private JLabel domTotal = new JLabel();
//  	private WholeNumberField domChosen = new WholeNumberField(0, 0);
//  	private WholeNumberField domTotal = new WholeNumberField(0, 0);

	protected static ArrayList pcDomainList = new ArrayList();
	private static boolean needsUpdate = true;
	private JButton deitySelect;
	private JButton domainSelect;
	private boolean hasBeenSized = false;
	private JSplitPane splitPane;
	private JSplitPane bsplit;
	private JSplitPane asplit;
	private JPanel center = new JPanel();
	private static int splitOrientation = JSplitPane.HORIZONTAL_SPLIT;

	private static String in_tabName;
	private static String in_nameDeity;
	private static String in_tempName;
	private static String in_column1;
	private static String in_column2;
	private static String in_column3;
	private static String in_column4;
	private static String in_column5;
	private static String in_domainSelected;
	private static String in_ofString;
	private static String in_tabToolTip;
	private static String in_deityTableTip;
	private static String in_domainTableTip;
	private static String in_deityDescrip;
	private static String in_deityRequire;
	private static String in_deityFavWeap;
	private static String in_deityHolyIt;
	private static String in_domainGrant;
	private static String in_domainRequire;
	private static String in_deityString;
	private static String in_deityButTip;
	private static String in_orientation;
	private static String in_domainButTip;
	private static String in_infoScrollTip;
	private static String in_statusBar;
	private static String in_qualifyMess;
	private static String in_reqMess;
	private static String in_errorMess;
	private static String in_infoDeity;
	private static String in_infoDomain;

	private static String[] s_columnNames = {in_column1, in_column2, in_column3, in_column4};
	private static String[] s_domainColList = {in_column5, in_column4};

	/**
	 * Resrouce bundles
	 */
	static
	{
		ResourceBundle domainTabProperties;
		Locale currentLocale = new Locale(Globals.getLanguage(), Globals.getCountry());
		try
		{
			domainTabProperties = ResourceBundle.getBundle("pcgen/gui/prop/LanguageBundle", currentLocale);
			in_tabName = domainTabProperties.getString("in_domains");
			in_nameDeity = domainTabProperties.getString("in_nameLabel");
			in_tempName = domainTabProperties.getString("in_tempName");
			in_column1 = domainTabProperties.getString("in_nameLabel");
			in_column2 = domainTabProperties.getString("in_domains");
			in_column3 = domainTabProperties.getString("in_alignLabel");
			in_column4 = domainTabProperties.getString("in_sourceLabel");
			in_column5 = domainTabProperties.getString("in_domains");
			in_domainSelected = domainTabProperties.getString("in_domainSelected");
			in_ofString = domainTabProperties.getString("in_ofString");
			in_tabToolTip = domainTabProperties.getString("in_tabToolTip");
			in_deityTableTip = domainTabProperties.getString("in_deityTableTip");
			in_domainTableTip = domainTabProperties.getString("in_domainTableTip");
			in_deityDescrip = domainTabProperties.getString("in_deityDescrip");
			in_deityRequire = domainTabProperties.getString("in_deityRequire");
			in_deityFavWeap = domainTabProperties.getString("in_deityFavWeap");
			in_deityHolyIt = domainTabProperties.getString("in_deityHolyIt");
			in_domainGrant = domainTabProperties.getString("in_domainGrant");
			in_domainRequire = domainTabProperties.getString("in_domainRequire");
			in_deityString = domainTabProperties.getString("in_deityString");
			in_deityButTip = domainTabProperties.getString("in_deityButTip");
			in_orientation = domainTabProperties.getString("in_orientation");
			in_domainButTip = domainTabProperties.getString("in_domainButTip");
			in_infoScrollTip = domainTabProperties.getString("in_infoScrollTip");
			in_statusBar = domainTabProperties.getString("in_statusBarDeity");
			in_qualifyMess = domainTabProperties.getString("in_qualifyMess");
			in_reqMess = domainTabProperties.getString("in_reqMess");
			in_errorMess = domainTabProperties.getString("in_errorMessDomain");
			in_infoDeity = domainTabProperties.getString("in_deityInfo");
			in_infoDomain = domainTabProperties.getString("in_domainInfo");
		}
		catch (MissingResourceException mrex)
		{
			mrex.printStackTrace();
		}
		finally
		{
			domainTabProperties = null;
		}
	}

	private class ClassPopupMenu extends JPopupMenu
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
			return Utility.createMenuItem(label, new AddClassActionListener(), "Select", (char)0, accelerator, "Select Deity", "Add16.gif", true);
		}

		private JMenuItem createRemoveMenuItem(String label, String accelerator)
		{
			return Utility.createMenuItem(label, new RemoveClassActionListener(), "Select", (char)0, accelerator, "Select Domain", "Add16.gif", true);
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
				ClassPopupMenu.this.add(createAddMenuItem("Select", "control EQUALS"));
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
				ClassPopupMenu.this.add(createRemoveMenuItem("Select", "control EQUALS"));
			}
		}
	}

	private class ClassPopupListener extends MouseAdapter
	{
		private ClassPopupMenu menu;
		JTableEx aTable;

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
							final JMenuItem menuItem = (JMenuItem)menu.getComponent(i);
							javax.swing.KeyStroke ks = menuItem.getAccelerator();
							if ((ks != null) && keyStroke.equals(ks))
							{
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
				int selRow = aTable.getSelectedRow();
				if (selRow == -1) return;
				menu.show(evt.getComponent(), evt.getX(), evt.getY());
			}
		}
	}


	private void hookupPopupMenu(JTableEx treeTable)
	{
		treeTable.addMouseListener(new ClassPopupListener(treeTable, new ClassPopupMenu(treeTable)));
	}

	public InfoDomain()
	{
		// do not remove this
		// we will use the component's name to save component specific settings
		setName(in_tabName);

		aPC = Globals.getCurrentPC();
		initComponents();

		FilterFactory.restoreFilterSettings(this);
	}

	public void setNeedsUpdate(boolean flag)
	{
		needsUpdate = flag;
	}

	//Set up GridBag Constraints
	void buildConstraints(GridBagConstraints gbc, int gx, int gy, int gw, int gh, int wx, int wy)
	{
		gbc.gridx = gx;
		gbc.gridy = gy;
		gbc.gridwidth = gw;
		gbc.gridheight = gh;
		gbc.weightx = wx;
		gbc.weighty = wy;
	}

	private void initComponents()
	{
		//Deity table set up

		Utility.setDescription(this, in_tabToolTip);
		sorter = new TableSorter(deityModel);
		deityTable = new JTableEx(sorter);
		sorter.addMouseListenerToHeaderInTable(deityTable);
		//Set up column sizes.
		TableColumn column = null;
		Component comp = null;
		int cellWidth = 0;
		for (int i = 0; i < s_columnNames.length; i++)
		{
			column = deityTable.getColumnModel().getColumn(i);
			if (i == 0)
				cellWidth = 45;
			else
			{
				comp = deityTable.getDefaultRenderer(deityModel.getColumnClass(i)).getTableCellRendererComponent(deityTable, deiLongValues[i], false, false, 0, i);
				cellWidth = comp.getPreferredSize().width;
			}
			column.setPreferredWidth(cellWidth);
		}

		ListSelectionModel rowSM = deityTable.getSelectionModel();
		rowSM.addListSelectionListener(new ListSelectionListener()
		{
			public void valueChanged(ListSelectionEvent e)
			{
				// Ignore extra messages.
				if (e.getValueIsAdjusting())
				{
					return;
				}

				final ListSelectionModel lsm = deityTable.getSelectionModel();
				final int selectedRow = sorter.getRowTranslated(lsm.getMinSelectionIndex());
				if (selectedRow >= 0)
				{
					final String selectedDeityName = deityModel.getValueAt(selectedRow, -1).toString();
					final Deity aDeity = Globals.getDeityNamed(selectedDeityName);
					setDeityInfoText(aDeity);
				}
				else
				{
					setDeityInfoText(null);
				}
			}
		});

		deityTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		Utility.setDescription(deityTable, in_deityTableTip);

		//Domain table Setup

		sorter2 = new TableSorter(domModel);
		domainTable = new JTableEx(sorter2);

		//Set up tool tips for the domain table
		Utility.setDescription(domainTable, in_domainTableTip);
		domainTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		final MouseListener deityMouse = new MouseAdapter()
		{
			public void mouseClicked(MouseEvent f)
			{
				if (f.getClickCount() == 2)
				{
					final ListSelectionModel lsm = deityTable.getSelectionModel();
					final int row = lsm.getMinSelectionIndex();
					if (row >= 0)
					{
						final int selectedRow = sorter.getRowTranslated(lsm.getMinSelectionIndex());
						if (selectedRow >= 0)
						{
							Globals.getCurrentPC().setDirty(true);
							InfoDomain.this.selectDeityIndex(selectedRow);
						}
						forceUpdates();
					}
				}
			}
		};
		deityTable.addMouseListener(deityMouse);

		final MouseListener domainMouse = new MouseAdapter()
		{
			public void mouseClicked(MouseEvent f)
			{
				ListSelectionModel lsm = domainTable.getSelectionModel();
				if (f.getClickCount() == 1)
				{
					int row = lsm.getMinSelectionIndex();
					if (row < 0)
						return;
					final int selectedRow = sorter2.getRowTranslated(row);
					if (selectedRow < 0)
						return;
					final String domainName = domModel.getValueAt(selectedRow, -1).toString();
					if (domainName != null)
					{
						final Domain aDomain = Globals.getDomainNamed(domainName);
						if (aDomain != null)
						{
							domainInfo.setText("<html><b>" + aDomain.getName() +
								" &nbsp;" + in_domainGrant + "</b>:" + aDomain.getGrantedPower() +
								" &nbsp;<b>" + in_domainRequire + "</b>:" + aDomain.preReqHTMLStrings(false) +
								"</html>");
						}
					}
					return;
				}
				if (f.getClickCount() == 2)
				{
					int row = lsm.getMinSelectionIndex();
					if (row < 0)
						return;
					final int selectedRow = sorter2.getRowTranslated(row);
					if (selectedRow < 0)
						return;
					InfoDomain.this.addDomain(selectedRow);
					forceUpdates();
					return;
				}
				else
				{
					return;
				}
			}
		};
		// true by default
		domainTable.addMouseListener(domainMouse);

		center.setLayout(new BorderLayout());

		GridBagLayout gridbag = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		JPanel leftPane = new JPanel();
		JPanel rightPane = new JPanel();
		leftPane.setLayout(gridbag);
		splitPane = new JSplitPane(splitOrientation, leftPane, rightPane);
		splitPane.setOneTouchExpandable(true);
		splitPane.setDividerSize(10);
		splitPane.setDividerLocation(350);

		center.add(splitPane, BorderLayout.CENTER);

		buildConstraints(c, 0, 0, 1, 1, 100, 5);
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.CENTER;
		JPanel aPanel = new JPanel();
		gridbag.setConstraints(aPanel, c);
		JLabel aLabel = new JLabel(in_deityString + ": ");
		aPanel.add(aLabel);
		deityName = new JLabel(in_nameDeity);
		aPanel.add(deityName);
		ImageIcon newImage;
		deitySelect = new JButton("Select");
		Utility.setDescription(deitySelect, in_deityButTip);
		deitySelect.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				ListSelectionModel lsm = deityTable.getSelectionModel();
				final int row = lsm.getMinSelectionIndex();
				if (row < 0)
					return;
				final int selectedRow = sorter.getRowTranslated(lsm.getMinSelectionIndex());
				if (selectedRow < 0)
					return;
				InfoDomain.this.selectDeityIndex(selectedRow);
				forceUpdates();
			}
		});

		aPanel.add(deitySelect);
		leftPane.add(aPanel);
		newImage = new ImageIcon(getClass().getResource("resource/Refresh16.gif"));
		JButton sButton = new JButton(newImage);
		Utility.setDescription(sButton, in_orientation);
		sButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				if (splitOrientation == JSplitPane.VERTICAL_SPLIT)
					splitOrientation = JSplitPane.HORIZONTAL_SPLIT;
				else
					splitOrientation = JSplitPane.VERTICAL_SPLIT;
				splitPane.setOrientation(splitOrientation);
				splitPane.setDividerLocation(.5);
			}
		});
		aPanel.add(sButton);


		buildConstraints(c, 0, 1, 1, 1, 0, 95);
		c.fill = GridBagConstraints.BOTH;
		c.anchor = GridBagConstraints.CENTER;
		JScrollPane scrollPane = new JScrollPane(deityTable);
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
		domSelected = new JLabel(in_domainSelected + ": ");
		ofLabel = new JLabel(in_ofString);
		aPanel.add(domSelected);
		aPanel.add(domChosen);
		aPanel.add(ofLabel);
		aPanel.add(domTotal);
		domainSelect = new JButton("Select");
		Utility.setDescription(domainSelect, in_domainButTip);
		domainSelect.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				ListSelectionModel lsm = domainTable.getSelectionModel();
				final int row = lsm.getMinSelectionIndex();
				if (row < 0)
					return;
				final int selectedRow = sorter2.getRowTranslated(lsm.getMinSelectionIndex());
				if (selectedRow < 0)
					return;
				aPC.setDirty(true);
				InfoDomain.this.addDomain(selectedRow);
			}
		});

		aPanel.add(domainSelect);
		rightPane.add(aPanel);

		buildConstraints(c, 0, 1, 1, 1, 0, 95);
		c.fill = GridBagConstraints.BOTH;
		c.anchor = GridBagConstraints.CENTER;
		scrollPane = new JScrollPane(domainTable);
		gridbag.setConstraints(scrollPane, c);
		rightPane.add(scrollPane);

		TitledBorder title1 = BorderFactory.createTitledBorder(etched, in_infoDeity);
		title1.setTitleJustification(TitledBorder.CENTER);
		deityScroll.setBorder(title1);
		deityInfo.setBackground(rightPane.getBackground());
		deityScroll.setViewportView(deityInfo);
		Utility.setDescription(deityScroll, in_infoScrollTip);

		TitledBorder title2 = BorderFactory.createTitledBorder(etched, in_infoDomain);
		title2.setTitleJustification(TitledBorder.CENTER);
		domainScroll.setBorder(title2);
		domainInfo.setBackground(rightPane.getBackground());
		domainScroll.setViewportView(domainInfo);
		Utility.setDescription(domainScroll, in_infoScrollTip);

		asplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, deityScroll, domainScroll);
		asplit.setOneTouchExpandable(true);
		asplit.setDividerSize(10);
		asplit.setDividerLocation(300);

		JPanel botPane = new JPanel();
		botPane.setLayout(new BorderLayout());
		botPane.add(asplit, BorderLayout.CENTER);
		bsplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT, center, botPane);
		bsplit.setOneTouchExpandable(true);
		bsplit.setDividerSize(10);
		bsplit.setDividerLocation(300);

		this.setLayout(new BorderLayout());
		this.add(bsplit, BorderLayout.CENTER);


		//domField.setMinimumSize(new Dimension(50, 21));
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

		addComponentListener(new java.awt.event.ComponentAdapter()
		{
			public void componentShown(java.awt.event.ComponentEvent evt)
			{
				formComponentShown(evt);
			}
		});

		hookupPopupMenu(deityTable);
		hookupPopupMenu(domainTable);
	}

	/** executed when the component is shown */
	private void formComponentShown(ComponentEvent evt)
	{
		requestFocus();
		PCGen_Frame1.getStatusBar().setText(in_statusBar);
		updateCharacterInfo();
		int s = splitPane.getDividerLocation();
		int t = bsplit.getDividerLocation();
		int u = asplit.getDividerLocation();
		if (!hasBeenSized)
		{
			hasBeenSized = true;
			s = Globals.getPCGenOption("InfoDomain.splitPane", (int)(this.getSize().getWidth() * 4 / 10));
			t = Globals.getPCGenOption("InfoDomain.bsplit", (int)(this.getSize().getHeight() * 75 / 100));
			u = Globals.getPCGenOption("InfoDomain.asplit", (int)(this.getSize().getWidth() * 5 / 10));
		}
		if (s > 0)
		{
			splitPane.setDividerLocation(s);
			Globals.setPCGenOption("InfoDomain.splitPane", s);
		}
		if (t > 0)
		{
			bsplit.setDividerLocation(t);
			Globals.setPCGenOption("InfoDomain.bsplit", t);
		}
		if (u > 0)
		{
			asplit.setDividerLocation(u);
			Globals.setPCGenOption("InfoDomain.asplit", u);
		}
	}

	private void setDeityInfoText(Deity aDeity)
	{
		if (aDeity != null)
		{
			StringBuffer infoText = new StringBuffer("<html><b>" + aDeity.getName() +
				" &nbsp;" + in_deityDescrip + "</b>:" + aDeity.getDescription());

			String aString = aDeity.preReqHTMLStrings(false);
			if (aString.length() != 0)
			{
				infoText.append(" &nbsp;<b>").append(in_deityRequire).append("</b>:").append(aString);
			}
			aString = aDeity.getFavoredWeapon();
			if (aString.length() != 0)
			{
				infoText.append(" &nbsp;<b>").append(in_deityFavWeap).append("</b>:").append(aString);
			}
			aString = aDeity.getHolyItem();
			if (aString.length() != 0)
			{
				infoText.append(" &nbsp;<b>").append(in_deityHolyIt).append("</b>:").append(aString);
			}
			infoText.append("</html>");
			deityInfo.setText(infoText.toString());
		}
		else
			deityInfo.setText();
	}

	/** <code>updateCharacterInfo</code> update data listening for a changed PC
	 */
	public void updateCharacterInfo()
	{
		aPC = Globals.getCurrentPC();
		if (aPC == null)
			return;

		// refresh Deities
		ArrayList deityList = new ArrayList();
		for (Iterator it = Globals.getDeityList().iterator(); it.hasNext();)
		{
			final Deity aDeity = (Deity)it.next();
			if (accept(aDeity))
			{
				deityList.add(aDeity);
			}
		}
		deityModel.setData(deityList);

		pcDomainList.clear();
		ArrayList dataList = new ArrayList();
		if (aPC.getDeity() != null)
			deityName.setText(aPC.getDeity().getName());
		else
			deityName.setText(in_tempName);
		for (int i = 0; i < aPC.getCharacterDomainList().size(); i++)
		{
			final CharacterDomain aCD = (CharacterDomain)aPC.getCharacterDomainList().get(i);
			// for each domain, if it's not in the list already, add it
			if (aCD != null && aCD.getDomain() != null)
				if (!dataList.contains(aCD.getDomain().getName()))
				{
					/*
					 * update for new filtering
					 * author: Thomas Behr 09-02-02
					 */
					if (accept(aCD.getDomain()))
					{
						dataList.add(aCD.getDomain().getName()); // list of available choices
					}
					pcDomainList.add(aCD.getDomain().getName()); // list of pc's choices
				}
		}
		domTotal.setText(Integer.toString(aPC.getCharacterDomainList().size()));
		domChosen.setText(Integer.toString(pcDomainList.size()));
		if (aPC.getDeity() != null)
		{
			final StringTokenizer aTok = new StringTokenizer(aPC.getDeity().domainListString(), "|,", false);
			while (aTok.hasMoreElements())
			{
				final String aString = aTok.nextToken();
				/*
				 * update for new filtering
				 * author: Thomas Behr 09-02-02
				 */
				if (accept(Globals.getDomainNamed(aString)) &&
					!dataList.contains(aString))
				{
					dataList.add(aString);
				}
			}
		}
		for (Iterator i = aPC.getClassList().iterator(); i.hasNext();)
		{
			final PCClass aClass = (PCClass)i.next();
			for (Iterator d = aClass.getAddDomains().iterator(); d.hasNext();)
			{
				final String aString = d.next().toString();
				final int x = aString.indexOf("|");
				final int level = Integer.parseInt(aString.substring(0, x));
				final String dList = aString.substring(x + 1);
				if (aClass.getLevel().intValue() >= level)
				{
					final StringTokenizer aTok = new StringTokenizer(dList, "|", false);
					while (aTok.hasMoreTokens())
					{
						final String dName = aTok.nextToken();
						if (accept(Globals.getDomainNamed(dName)) &&
							!dataList.contains(dName))
						{
							dataList.add(dName);
						}
					}
				}

			}
		}
		domModel.setData(dataList);
		sorter2.setModel(domModel);
		sorter2.tableChanged(null);
		domModel.fireTableDataChanged();
		needsUpdate = false;
	}

	public void addDomain(int selectedRow)
	{
		forceUpdates();

		final String domainName = InfoDomain.this.domModel.getValueAt(selectedRow, -1).toString();
		Domain aDomain = Globals.getDomainNamed(domainName);
		if (aDomain == null || !aDomain.qualifiesForDomain())
		{
			JOptionPane.showMessageDialog(null, in_qualifyMess + domainName, "PCGen", JOptionPane.INFORMATION_MESSAGE);
			return;
		}
		for (int i = 0; i < aPC.getCharacterDomainList().size(); i++)
		{
			final CharacterDomain aCD = (CharacterDomain)aPC.getCharacterDomainList().get(i);
			if (aCD == null)
				continue;
			final Domain bDomain = aCD.getDomain();
			if (bDomain != null && bDomain.getName().equalsIgnoreCase(domainName))
			{
				pcDomainList.remove(domainName);
				aCD.setDomain(null);
				break;
			}
			if (bDomain == null || i == aPC.getCharacterDomainList().size() - 1)
			{
				if (bDomain != null)
					pcDomainList.remove(bDomain.getName());
				aDomain = aCD.setDomain(aDomain);
				aDomain.setIsLocked(true);
				pcDomainList.add(domainName);

				break;
			}
		}
//  		domChosen.setValue(pcDomainList.size());
		domChosen.setText(Integer.toString(pcDomainList.size()));
		domModel.fireTableDataChanged();
	}

	public void selectDeityIndex(int selectedRow)
	{

		final String selectedDeityName = deityModel.getValueAt(selectedRow, -1).toString();
		ArrayList dataList = new ArrayList();

		final Deity aDeity = Globals.getDeityNamed(selectedDeityName);
		if (aDeity == null || !aPC.canSelectDeity(aDeity))
		{
			final ListSelectionModel lsm = deityTable.getSelectionModel();
			JOptionPane.showMessageDialog(null, in_reqMess + selectedDeityName + ".", "PCGen", JOptionPane.INFORMATION_MESSAGE);
			lsm.clearSelection();
			return;
		}
		aPC.setDeity(aDeity);
		deityName.setText(selectedDeityName);
		pcDomainList.clear();
		for (Iterator ii = aPC.getCharacterDomainList().iterator(); ii.hasNext();)
		{
			final CharacterDomain aCD = (CharacterDomain)ii.next();
			final Domain aDomain = aCD.getDomain();

			/*
			 * update for new filtering
			 * author: Thomas Behr 09-02-02
			 */
			if (aDomain != null && !dataList.contains(aDomain.getName()))
			{
				if (accept(aDomain))
				{
					dataList.add(aDomain.getName());
				}
				pcDomainList.add(aDomain.getName());
			}
//  			boolean addIt = (!showQualifiedOnly || (aDomain != null && aDomain.qualifiesForDomain()));
//  			if (aDomain != null && !dataList.contains(aDomain.getName()))
//  			{
//  				if (addIt)
//  					dataList.add(aDomain.getName());
//  				pcDomainList.add(aDomain.getName());
//  			}
		}

		StringTokenizer aTok = new StringTokenizer(aDeity.domainListString(), "|,", false);
		if (aDeity.domainListString().equalsIgnoreCase("ALL"))
		{
			for (Iterator i = Globals.getDomainList().iterator(); i.hasNext();)
			{
				final Domain deityDomain = (Domain)i.next();
				/*
				 * update for new filtering
				 * author: Thomas Behr 09-02-02
				 */
				if (accept(deityDomain) && !dataList.contains(deityDomain.getName()))
				{
					dataList.add(deityDomain.getName());
				}
//  				boolean addIt = (deityDomain != null) &&
//                                          (!showQualifiedOnly || deityDomain.qualifiesForDomain());
//  				if (deityDomain != null && addIt && !dataList.contains(deityDomain.getName()))
//  					dataList.add(deityDomain.getName());
			}
		}
		else
		{
			while (aTok.hasMoreTokens())
			{
				String aToken = aTok.nextToken();
				final Domain aDomain = Globals.getDomainNamed(aToken);

				/*
				 * update for new filtering
				 * author: Thomas Behr 09-02-02
				 */
				if (accept(aDomain) && !dataList.contains(aToken))
				{
					dataList.add(aToken);
				}
//  				boolean addIt = (!showQualifiedOnly || (aDomain != null && aDomain.qualifiesForDomain()));
//  				if (addIt && aDomain != null && !dataList.contains(aToken))
//  				{
//  					dataList.add(aToken);
//  				}
			}
		}
		domModel.setData(dataList);
		sorter2.setModel(domModel);
		sorter2.tableChanged(null);
		domModel.fireTableDataChanged();
		deityModel.fireTableDataChanged();
	}


	/**
	 * This is the Model that populate the table for Deities
	 */
	public static final class DeityModel extends AbstractTableModel
	{
		private ArrayList deitys = Globals.getDeityList();

		public void setData(ArrayList aList)
		{
			deitys = aList;
			fireTableDataChanged();
		}

		/**
		 * Return the value of a grid cell by using the information from the global
		 * feat list.
		 *
		 * @see pcgen.core.Globals
		 */
		public Object getValueAt(int row, int column)
		{
			Object retVal = "";
			if (deitys != null && row >= 0 && row < deitys.size())
			{
				final Deity aDeity = (Deity)deitys.get(row);
				final PlayerCharacter aPC = InfoDomain.aPC;
				switch (column)
				{
					case -1: // sneaky case to just get the name sans html tags
						return aDeity.getName();
					case 0:
						if (aPC != null && aPC.getDeity() != null && aPC.getDeity().getName().equals(aDeity.getName()))
							retVal = "<html><b>" + aDeity.getName() + "</b></html>";
						else if (aPC != null && aPC.canSelectDeity(aDeity))
							retVal = aDeity.getName();
						else
							retVal = "<html><font color=red>" + aDeity.getName() + "</font></html>";
						break;
					case 1:
						retVal = aDeity.domainListString();
						break;
					case 2:
						retVal = aDeity.getDeityAlignment();
						break;
					case 3:
						retVal = aDeity.getSource();
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
				return deitys.size();
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
	 * This is the Model that populate the table for Domains
	 */

	public class DomainModel extends AbstractTableModel
	{
		protected ArrayList dataList = new ArrayList();

		public DomainModel()
		{
		}

		/* return list of domains associated with the current deity */
		/* sets the list of appropriate choices */
		/* returns the list of selections in order of selection */
		public ArrayList dataList()
		{
			return dataList;
		}

		public void setData(ArrayList aList)
		{
			dataList = aList;
		}

		// These methods always need to be implemented.
		public int getColumnCount()
		{
			return s_domainColList.length;
		}

		public int getRowCount()
		{
			return dataList.size();
		}

		public Object getValueAt(int row, int col)
		{
			if (row < 0 || row >= dataList.size())
				return "";
			String retVal = (String)dataList.get(row);
			switch (col)
			{
				case -1: // return name of item without html tags
					break;
				case 0:
					final Domain aDomain = Globals.getDomainNamed(retVal);
					if (InfoDomain.pcDomainList.contains(dataList.get(row)))
						retVal = "<html><b>" + retVal + "</b></html>";
					else if (aDomain != null && !aDomain.qualifiesForDomain())
						retVal = "<html><font color=red>" + retVal + "</font></html>";
					break;
				case 1:
					try
					{
						retVal = Globals.getDomainNamed(retVal).getSource();
					}
					catch (Exception exc)
					{
						System.out.println(in_errorMess);
					}
					break;
			}
			return retVal;
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

	/*
	 * ##########################################################
	 * filter stuff
	 * ##########################################################
	 */

	/**
	 * implementation of Filterable interface
	 */
	public void initializeFilters()
	{
		FilterFactory.registerAllSourceFilters(this);
		FilterFactory.registerAllDeityFilters(this);
	}

	/**
	 * implementation of Filterable interface
	 */
	public void refreshFiltering()
	{
		needsUpdate = true;
		updateCharacterInfo();
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

	private void forceUpdates()
	{
		aPC.setDirty(true);
		PCGen_Frame1.forceUpdate_InfoSpells();
		PCGen_Frame1.forceUpdate_InfoFeats();
	}

}
