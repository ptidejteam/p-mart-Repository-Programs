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
 * Created on April 21, 2001, 2:15 PM
 */

package pcgen.gui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.StringTokenizer;
import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import pcgen.core.CharacterDomain;
import pcgen.core.Feat;
import pcgen.core.Globals;
import pcgen.core.PCClass;
import pcgen.core.PlayerCharacter;
import pcgen.core.Spell;
import pcgen.gui.filter.FilterAdapterPanel;
import pcgen.gui.filter.FilterFactory;

/**
 *  <code>InfoSpells</code> creates a new tabbed panel.
 *
 * @author     Bryan McRoberts <a href="mailto:merton_monk@users.sourceforge.net">
 *      merton_monk@users.sourceforge.net</a>
 * @created    den 11 maj 2001
 * @version    $Revision: 1.1 $
 */
public class InfoSpells extends FilterAdapterPanel
{

	//view modes for tables
	public static final int VIEW_CLASS = 0;
	public static final int VIEW_LEVEL = 1;
	public static final int VIEW_TYPE = 1;
	public static final int VIEW_NAME = 1;
	private boolean needsUpdate = true;

	// matched from modelB - made by selections from tableA

	/**
	 *  Model for the JTable containing all available spells
	 *
	 * @since
	 */
	protected SpellTableModel modelA = new SpellTableModel();

	/**
	 *  Model for the JTable containing the selected spells
	 *
	 * @since
	 */
	protected SpellTableModel modelB = new SpellTableModel();

	/**
	 *  Model for the JTable containing spell options (such as level, book , etc.)
	 *
	 * @since
	 */
	protected SpellOptionsModel optionsModel = new SpellOptionsModel();

	/**
	 *  Model for the JTable containing the feat(s) selected for the spell to be
	 *  memorized.
	 *
	 * @since
	 */
	protected SpellFeatModel featModel = new SpellFeatModel();
	private JComboBox spellBookComboBox = new JComboBox();
	private JComboBox spellLevelComboBox = new JComboBox();
	private JTextField spellBookNameText = new JTextField();
	private JComboBox spellClassComboBox = new JComboBox();
	private JComboBox spellMetaComboBox = new JComboBox();
	private JLabel castableText = new JLabel();
	private JLabel spellpointText = new JLabel();
	private JLabel knownText = new JLabel();
	private JTableEx tableA = null;
	// matched with modelA
	private JTableEx tableB = null;
	private JScrollPane optionsScrollPane = null;
	private JTableEx optionsTable = null;
	private JTableEx featTable = null;
	private TableSorter sorter = null;
	private TableSorter sorter2 = null;
	private ArrayList domainList = new ArrayList();
	final ActionListener alc = new ActionListener()
	{
		/**
		 *  Anonymous event handler
		 *
		 * @param  evt  The ActionEvent
		 * @since
		 */
		public void actionPerformed(ActionEvent evt)
		{
			populateSpellClassComboBox();
			populateTables();
		}
	};
	final ActionListener all = new ActionListener()
	{
		/**
		 *  Anonymous event handler
		 *
		 * @param  evt  The ActionEvent
		 * @since
		 */
		public void actionPerformed(ActionEvent evt)
		{
			populateTables();
		}
	};
	final ActionListener alb = new ActionListener()
	{
		/**
		 *  Anonymous event handler
		 *
		 * @param  evt  The ActionEvent
		 * @since
		 */
		public void actionPerformed(ActionEvent evt)
		{
			populateSpellBookComboBox();
			populateTables();
		}
	};

	/**
	 *  Constructor for the InfoSpells object
	 *
	 * @since
	 */
	public InfoSpells()
	{
		// do not remove this
		// we will use the component's name to save component specific settings
		setName("Spells");

		initComponents();

		FilterFactory.restoreFilterSettings(this);
	}


	public void setNeedsUpdate(boolean flag)
	{
		needsUpdate = flag;
	}

	private void initComponents()
	{
		populateSpellClassComboBox();
		populateSpellLevelComboBox();
		populateSpellBookComboBox();
		JPanel spellNorthNorth = new JPanel();
		JPanel spellNorthSouth = new JPanel();
		JPanel spellNorthEast = new JPanel();
		JPanel spellCenter = new JPanel();
		JPanel spellCenterNorth = new JPanel();
		JPanel spellCenterWest = new JPanel();
		JPanel spellCenterEast = new JPanel();
		JButton delBookButton = new JButton("Del");
		JLabel spellBookNamedLabel = new JLabel("Book Named:");
		delBookButton.addActionListener(
			new ActionListener()
			{
				/**
				 *  Anonymous event handler
				 *
				 * @param  evt  The ActionEvent
				 * @since
				 */
				public void actionPerformed(ActionEvent evt)
				{
					delBookButton_clicked(evt);
				}
			});
		JButton addBookButton = new JButton("Add");
		addBookButton.addActionListener(
			new ActionListener()
			{
				/**
				 *  Anonymous event handler
				 *
				 * @param  evt  The ActionEvent
				 * @since
				 */
				public void actionPerformed(ActionEvent evt)
				{
					addBookButton_clicked(evt);
				}
			});
		ArrayList aArrayList = new ArrayList();
		aArrayList.add(spellClassComboBox);
		aArrayList.add(spellLevelComboBox);
		aArrayList.add(spellBookComboBox);
		optionsModel.setData(aArrayList);
		optionsTable = new JTableEx(optionsModel);
		optionsTable.getColumnModel().getColumn(0).setCellEditor(new DefaultCellEditor(spellClassComboBox));
		optionsTable.getColumnModel().getColumn(1).setCellEditor(new DefaultCellEditor(spellLevelComboBox));
		optionsTable.getColumnModel().getColumn(2).setCellEditor(new DefaultCellEditor(spellBookComboBox));
		optionsTable.setCellSelectionEnabled(true);
		optionsTable.setRowSelectionAllowed(false);
		optionsTable.setColumnSelectionAllowed(false);
		optionsScrollPane = new JScrollPane(optionsTable);
		optionsScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
		optionsScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		optionsScrollPane.setColumnHeaderView(optionsTable.getTableHeader());
		optionsScrollPane.getViewport().setScrollMode(JViewport.BACKINGSTORE_SCROLL_MODE);
		optionsScrollPane.setBackground(new Color(255, 255, 255));
		optionsScrollPane.setPreferredSize(new Dimension(40, 40));

		DefaultTableCellRenderer renderer1 = new DefaultTableCellRenderer();
		renderer1.setToolTipText("Click for combo box");
		optionsTable.getColumnModel().getColumn(0).setCellRenderer(renderer1);
		DefaultTableCellRenderer renderer2 = new DefaultTableCellRenderer();
		renderer2.setToolTipText("Click for combo box");
		optionsTable.getColumnModel().getColumn(1).setCellRenderer(renderer2);
		DefaultTableCellRenderer renderer3 = new DefaultTableCellRenderer();
		renderer3.setToolTipText("Click for combo box");
		optionsTable.getColumnModel().getColumn(2).setCellRenderer(renderer3);
		aArrayList = new ArrayList();
		final ArrayList feats = Globals.getCurrentPC().aggregateFeatList();
		Globals.sortPObjectList(feats);
		for (Iterator i = feats.iterator(); i.hasNext();)
		{
			Feat aFeat = (Feat)i.next();
			if (aFeat.getType().lastIndexOf("Metamagic") > -1)
			{
				aArrayList.add(aFeat.getName());
			}
		}
		featModel.setData(aArrayList);
		featTable = new JTableEx(featModel);
		featTable.setEnabled(false); // out-of-scope for 2.0.0
		JScrollPane scrollPane = new JScrollPane(featTable);
		JLabel spellAvailableLabel = new JLabel("Available");
		JButton addSpellButton = new JButton("S->");
		addSpellButton.addActionListener(
			new ActionListener()
			{
				/**
				 *  Anonymous event handler
				 *
				 * @param  evt  The ActionEvent
				 * @since
				 */
				public void actionPerformed(ActionEvent evt)
				{
					addSpellButton_clicked(evt);
				}
			});
		JLabel spellSelectedLabel = new JLabel("Selected");
		JButton delSpellButton = new JButton("<-S");
		delSpellButton.addActionListener(
			new ActionListener()
			{
				/**
				 *  Anonymous event handler
				 *
				 * @param  evt  The ActionEvent
				 * @since
				 */
				public void actionPerformed(ActionEvent evt)
				{
					delSpellButton_clicked(evt);
				}
			});
		this.setLayout(new BorderLayout());
		sorter = new TableSorter(modelA);
		tableA = new JTableEx(sorter);
		sorter.addMouseListenerToHeaderInTable(tableA);
		sorter2 = new TableSorter(modelB);
		tableB = new JTableEx(sorter2);
		sorter2.addMouseListenerToHeaderInTable(tableB);
		populateTables();
		spellCenter.setLayout(new GridBagLayout());
		spellNorthNorth.setLayout(new BorderLayout());
		spellNorthSouth.setLayout(new FlowLayout(FlowLayout.CENTER));
		spellNorthEast.setLayout(new BorderLayout());
		spellCenterNorth.setLayout(new BorderLayout());
		spellCenterWest.setLayout(new BorderLayout());
		spellCenterEast.setLayout(new BorderLayout());
		spellNorthNorth.add(optionsScrollPane, BorderLayout.CENTER);
		//    scrollPane.setPreferredSize(new Dimension(100,70));
		spellNorthEast.add(scrollPane, BorderLayout.CENTER);
		spellNorthSouth.add(addBookButton, null);
		spellNorthSouth.add(delBookButton, null);
		spellNorthSouth.add(spellBookNamedLabel, null);
		spellNorthSouth.add(spellBookNameText, null);

		scrollPane = new JScrollPane(tableA);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setColumnHeaderView(tableA.getTableHeader());
		scrollPane.getViewport().setScrollMode(JViewport.BACKINGSTORE_SCROLL_MODE);
		scrollPane.setBackground(new Color(255, 255, 255));
		scrollPane.setPreferredSize(new Dimension(250, 210));
		JPanel panel = new JPanel();
		panel.setLayout(new FlowLayout(FlowLayout.LEFT));
		panel.add(spellAvailableLabel);
		if (Globals.isDeadlandsMode())
		{
			JLabel label = new JLabel("Spellpoints:");
			panel.add(label);
			spellpointText.setPreferredSize(new Dimension(42, 21));
			panel.add(spellpointText);
		}
		else
		{
			JLabel label = new JLabel("Cast:");
			panel.add(label);
			castableText.setPreferredSize(new Dimension(42, 21));
			panel.add(castableText);
		}
		JLabel label = new JLabel("Known:");
		panel.add(label);
		knownText.setPreferredSize(new Dimension(42, 21));
		panel.add(knownText);
		spellCenterWest.add(panel, BorderLayout.NORTH);
		spellCenterWest.add(scrollPane, BorderLayout.CENTER);
		scrollPane = new JScrollPane(tableB);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setColumnHeaderView(tableB.getTableHeader());
		scrollPane.getViewport().setScrollMode(JViewport.BACKINGSTORE_SCROLL_MODE);
		scrollPane.setBackground(new Color(255, 255, 255));
		scrollPane.setPreferredSize(new Dimension(250, 210));
		spellCenterEast.add(spellSelectedLabel, BorderLayout.NORTH);
		spellCenterEast.add(scrollPane, BorderLayout.CENTER);
		spellBookNameText.setMinimumSize(new Dimension(100, 21));
		spellBookNameText.setPreferredSize(new Dimension(100, 21));
		spellBookNameText.setMaximumSize(new Dimension(500, 21));
		tableA.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		tableA.setAutoCreateColumnsFromModel(false);
		panel = new JPanel();
		panel.setLayout(new BorderLayout());
		panel.add(spellNorthNorth, BorderLayout.NORTH);
		panel.add(spellNorthSouth, BorderLayout.SOUTH);

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 2;
		gbc.gridheight = 1;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.anchor = GridBagConstraints.NORTH;
		gbc.weightx = 0.25;
		gbc.weighty = 0.2;
		spellCenter.add(panel, gbc);
		gbc.gridx = 2;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.anchor = GridBagConstraints.NORTH;
		gbc.weightx = 0.5;
		gbc.weighty = 0.2;
		spellCenter.add(spellNorthEast, gbc);
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.gridwidth = 1;
		gbc.gridheight = 2;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.anchor = GridBagConstraints.CENTER;
		gbc.weightx = 0.5;
		gbc.weighty = .8;
		spellCenter.add(spellCenterWest, gbc);
		gbc.gridx = 2;
		gbc.gridy = 1;
		gbc.gridwidth = 1;
		gbc.gridheight = 2;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.anchor = GridBagConstraints.CENTER;
		gbc.weightx = 0.5;
		gbc.weighty = .8;
		spellCenter.add(spellCenterEast, gbc);
		gbc.gridx = 1;
		gbc.gridy = 1;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.fill = GridBagConstraints.NONE;
		gbc.anchor = GridBagConstraints.SOUTH;
		gbc.weightx = 0.0;
		gbc.weighty = 0.4;
		gbc.insets = new Insets(0, 4, 2, 4);
		spellCenter.add(addSpellButton, gbc);
		gbc.gridx = 1;
		gbc.gridy = 2;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.fill = GridBagConstraints.NONE;
		gbc.anchor = GridBagConstraints.NORTH;
		gbc.weightx = 0.0;
		gbc.weighty = 0.4;
		gbc.insets = new Insets(2, 4, 0, 4);
		spellCenter.add(delSpellButton, gbc);
		this.add(spellCenter, BorderLayout.CENTER);
		tableB.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		tableB.setAutoCreateColumnsFromModel(false);
		spellClassComboBox.addActionListener(alc);
		spellLevelComboBox.addActionListener(all);
		spellBookComboBox.addActionListener(alb);

		addComponentListener(new java.awt.event.ComponentAdapter()
		{
			public void componentShown(java.awt.event.ComponentEvent evt)
			{
				// this method is run when the panel becomes visible
				// it's a great place to initialize things
				final PlayerCharacter aPC = Globals.getCurrentPC();
				aPC.setAggregateFeatsStable(true);
				aPC.setAutomaticFeatsStable(true);
				aPC.setVirtualFeatsStable(true);

				requestFocus();

				populateTables();
			}
		});
	}

	public void paint(Graphics g)
	{
		super.paint(g);
		pcChanged();
	}

	/** <code>pcChanged</code> update data listening for a changed PC, to update
	 * various places where the screen displays stuff.
	 * NOTE: This should probably be handled by an event of some sort, but this
	 * is the quick fix.
	 */
	public void pcChanged()
	{
		final PlayerCharacter pc = Globals.getCurrentPC();
		if (spellClassComboBox.getItemCount() <= 0 && pc.getClassList().size() > 0)
			populateSpellClassComboBox();
		populateDomainList();
	}

	private void populateDomainList()
	{
		final PlayerCharacter pc = Globals.getCurrentPC();
		domainList.clear();
		// repopulate domain list
		if (pc != null && spellClassComboBox.getItemCount() > 0)
		{
			String className = spellClassComboBox.getSelectedItem().toString();
			for (Iterator i = pc.getCharacterDomainList().iterator(); i.hasNext();)
			{
				CharacterDomain aCD = (CharacterDomain)i.next();
				if (aCD.getDomain() != null && aCD.getDomainSource().startsWith("PCClass|" + className))
					domainList.add(aCD.getDomain().getName());
			}
		}
	}


	/**
	 *  populateSpellClassComboBox() removes all items from its combobox and
	 *  rebuilds the contents by going through all the character's classes, and
	 *  adding the names of any that don't have a spellType of None. It then has
	 *  special rules for constructing the Domain class, since it's a combination
	 *  of Domain and all the domain names
	 *
	 * @since
	 */
	private void populateSpellClassComboBox()
	{
		spellClassComboBox.removeActionListener(alc);
		String selString = "";
		if (spellClassComboBox.getSelectedItem() != null)
			selString = spellClassComboBox.getSelectedItem().toString();
		spellClassComboBox.removeAllItems();
		final PlayerCharacter aPC = Globals.getCurrentPC();
		if (aPC != null)
		{
			for (Iterator e = aPC.getClassList().iterator(); e.hasNext();)
			{
				PCClass aClass = (PCClass)e.next();
				if (!aClass.getSpellType().equals("None"))
				{
					spellClassComboBox.addItem(aClass.getName());
				}
			}
		}
		if (selString.length() == 0 && spellClassComboBox.getItemCount() > 0)
			spellClassComboBox.setSelectedIndex(0);
		else
			spellClassComboBox.setSelectedItem(selString);

		spellClassComboBox.addActionListener(alc);
		populateDomainList();
	}


	/**
	 *  populateSpellLevelComboBox() is populated with numbers 0 to 19
	 *
	 * @since
	 */
	private void populateSpellLevelComboBox()
	{
		spellLevelComboBox.removeAllItems();
		for (int i = 0; i < 20; i++)
		{
			spellLevelComboBox.addItem(new Integer(i));
		}
		spellLevelComboBox.setSelectedIndex(0);
	}


	/**
	 *  populateSpellBookComboBox() puts all the character's spellbooks in the
	 *  list
	 *
	 * @since
	 */
	private void populateSpellBookComboBox()
	{
		spellBookComboBox.removeActionListener(alb);
		String selString = "";
		if (spellBookComboBox.getSelectedItem() != null)
			selString = spellBookComboBox.getSelectedItem().toString();
		int j = 0;
		spellBookComboBox.removeAllItems();
		int k = 0;
		final PlayerCharacter aPC = Globals.getCurrentPC();
		for (Iterator i = aPC.getSpellBooks().iterator(); i.hasNext();)
		{
			String aString = (String)i.next();
			spellBookComboBox.addItem(aString);
			if (aString.equals(selString))
				j = k;
			k++;
		}
		if (spellBookComboBox.getItemCount() >= j)
			spellBookComboBox.setSelectedIndex(j);
		spellBookComboBox.addActionListener(alb);
	}


	/**
	 *  populateTables() determines what the contents of tableA and tableB should
	 *  be based upon the selected class, book, level and metamagic feat
	 *
	 * @since
	 */
	// some of this logic needs to be moved to core API
	private void populateTables()
	{
		ArrayList vectorA = new ArrayList();
		// matched with tableA and modelA
		ArrayList vectorB = new ArrayList();
		// matched with tableB and modelB
		castableText.setText("0");
		spellpointText.setText("0");
		knownText.setText("0");
		if (spellClassComboBox.getItemCount() == 0)
		{
			return;
		}
		String className = null;
		int i = spellClassComboBox.getSelectedIndex();
		if (i == -1)
		{
			return;
		}
		int j = spellLevelComboBox.getSelectedIndex();
		if (j == -1)
		{
			return;
		}
		j += getLevelsForSelectedFeats();
		className = new String(spellClassComboBox.getSelectedItem().toString());
		PCClass aClass = Globals.getCurrentPC().getClassNamed(className);
/*		// if no class by that name, check for virtual class (Domain)
*		if (aClass == null)
*		{
*			int k = className.lastIndexOf("(");
*			if (k > 0)
*			{
*				aClass = Globals.getCurrentPC().getClassNamed(className.substring(0, k).trim());
*			}
*		}
*/
		if (aClass == null)
		{
			return;
		}
		if (i >= 0 && i < spellClassComboBox.getItemCount() && j >= 0 && j < 10)
		{
			Iterator e = null;
			Spell aSpell = null;
			if (aClass.getCastAs().length() > 0)
			{
				className = aClass.getCastAs();
			}
			if (spellBookComboBox.getSelectedItem().toString().equals(Globals.getDefaultSpellBook()))
			{
				for (e = Globals.getSpellMap().values().iterator(); e.hasNext();)
				{
					aSpell = (Spell)e.next();
					boolean addIt = false;
					StringTokenizer aTok = null;
					aTok = new StringTokenizer(className, "|", false);
					while (aTok.hasMoreTokens() && !addIt)
					{
						String aString = aTok.nextToken().trim();
						addIt = aSpell.levelForClass(aString, aClass.getName()).lastIndexOf("," + String.valueOf(spellLevelComboBox.getSelectedIndex())) > -1;
					}
					if (addIt && accept(aSpell))
					{
						vectorA.add(aSpell);
					}
				}
			}
			else
			{
				for (e = aClass.spellList().iterator(); e.hasNext();)
				{
					aSpell = (Spell)e.next();
					if ((aSpell.levelForClass(className, aClass.getName()).lastIndexOf("," + String.valueOf(spellLevelComboBox.getSelectedIndex())) > -1) && accept(aSpell))
					{
						vectorA.add(aSpell);
					}
				}
			}
			if (aClass != null)
			{
				String bookName = spellBookComboBox.getSelectedItem().toString();
				String mytemp = String.valueOf(aClass.getKnownForLevel(aClass.getLevel().intValue(), j, bookName));
				if (aClass.getSpecialtyKnownForLevel(aClass.getLevel().intValue(), j, bookName) > 0)
				{
					mytemp += "+" + aClass.getSpecialtyKnownForLevel(aClass.getLevel().intValue(), j, bookName);
				}
				knownText.setText(mytemp);
				if (Globals.isSSd20Mode() && bookName.equals(Globals.getDefaultSpellBook()))
				{
					knownText.setText("Unlim");
				}
				int cast = aClass.getCastForLevel(aClass.getLevel().intValue(), j, bookName, false);
				String bString = aClass.getBonusCastForLevelString(aClass.getLevel().intValue(), j, bookName);
//				if (aClass.getSpellType().equalsIgnoreCase("Divine") && !bookName.equals("Known Spells") && bString.equals("+1"))
//					cast--;
				castableText.setText(String.valueOf(cast) + bString);
				spellpointText.setText(aClass.getSPForLevelString(aClass.getLevel().intValue(), j, spellBookComboBox.getSelectedItem().toString()));
				Globals.setSpellPoints(aClass.getSPForLevelString(aClass.getLevel().intValue(), j, spellBookComboBox.getSelectedItem().toString()));
				for (e = aClass.spellList().iterator(); e.hasNext();)
				{
					aSpell = (Spell)e.next();
					int m = spellLevelComboBox.getSelectedIndex();
					String bName = (String)spellBookComboBox.getSelectedItem();
					if (aSpell.levelForClass(className, aClass.getName()).lastIndexOf("," + String.valueOf(m)) > -1 && aSpell.getSpellBooks().contains(bName))
						vectorB.add(aSpell);
				}
			}
		}
		modelA.setData(vectorA);
		modelA.fireTableDataChanged();
		modelB.setData(vectorB);
		modelB.fireTableDataChanged();
	}

	private int getLevelsForSelectedFeats()
	{
		int retVal = 0;
		int[] cList = featTable.getSelectedRows();
		int i = featTable.getSelectedRowCount();
		final PlayerCharacter aPC = Globals.getCurrentPC();
		while (i-- > 0)
		{
			Feat aFeat = aPC.getFeatNamed((String)featTable.getValueAt(cList[i], 0));
			retVal += aFeat.getAddSpellLevel();
		}
		return retVal;
	}


	private void addSpellButton_clicked(ActionEvent evt)
	{
		final PlayerCharacter aPC = Globals.getCurrentPC();
		aPC.setDirty(true);
		int j = spellLevelComboBox.getSelectedIndex();
		if (j == -1)
		{
			return;
		}
		j += getLevelsForSelectedFeats();
		String bookName = spellBookComboBox.getSelectedItem().toString();
		String className = new String(spellClassComboBox.getSelectedItem().toString());
		PCClass aClass = aPC.getClassNamed(className); //Null means domain.
		String mytemp1 = String.valueOf(aClass.getKnownForLevel(aClass.getLevel().intValue(), j, bookName));
		String mytemp2 = String.valueOf(aClass.getSpecialtyKnownForLevel(aClass.getLevel().intValue(), j, bookName));
		String mytemptotal = "0";
		Globals.debugPrint("mytemp1 at Initial:" + mytemp1);
		Globals.debugPrint("mytemp2 at Initial:" + mytemp2);
		if (aClass.getSpecialtyKnownForLevel(aClass.getLevel().intValue(), j, bookName) > 0)
		{
			mytemptotal = String.valueOf((Integer.parseInt(mytemp1) + Integer.parseInt(mytemp2)));
			Globals.debugPrint("mytemptotal with spec after:" + mytemptotal);
		}
		else
		{
			mytemptotal = String.valueOf(Integer.parseInt(mytemp1));
			Globals.debugPrint("mytemptotal after:" + mytemptotal);
		}
		if (aClass == null || aClass.memorizesSpells() || (!aClass.memorizesSpells() && (tableB.getRowCount() < Integer.parseInt(mytemptotal))))
		{
			int[] cList = tableA.getSelectedRows();
			int i = tableA.getSelectedRowCount();
			java.util.ArrayList featList = new java.util.ArrayList();
			while (i-- > 0)
			{
				String aString = aPC.addSpell(spellClassComboBox.getSelectedItem().toString(), spellLevelComboBox.getSelectedIndex(),
					featList, (String)modelA.getValueAt(sorter.getRowTranslated(cList[i]), 0), spellBookComboBox.getSelectedItem().toString());
				if (aString.length() > 0)
				{
					JOptionPane.showMessageDialog(null, aString, "PCGen", JOptionPane.INFORMATION_MESSAGE);
					break;
				}
			}
			populateTables();
		}
		else
		{
			JOptionPane.showMessageDialog(null, "Cannot add more spells.", "PCGen", JOptionPane.INFORMATION_MESSAGE);
		}
	}


	private void delSpellButton_clicked(ActionEvent evt)
	{
		final PlayerCharacter aPC = Globals.getCurrentPC();
		aPC.setDirty(true);
		int[] cList = tableB.getSelectedRows();
		int i = tableB.getSelectedRowCount();
		java.util.ArrayList featList = new java.util.ArrayList();
		while (i-- > 0)
		{
			String aString = aPC.delSpell(spellClassComboBox.getSelectedItem().toString(), spellLevelComboBox.getSelectedIndex(),
				featList, (String)modelB.getValueAt(sorter2.getRowTranslated(cList[i]), 0), spellBookComboBox.getSelectedItem().toString());
			if (aString.length() > 0)
			{
				JOptionPane.showMessageDialog(null, aString, "PCGen", JOptionPane.INFORMATION_MESSAGE);
				break;
			}
		}
		populateTables();
	}


	private void addBookButton_clicked(ActionEvent evt)
	{
		final PlayerCharacter aPC = Globals.getCurrentPC();
		aPC.setDirty(true);
		String aString = spellBookNameText.getText();
		if (aPC.addSpellBook(aString))
		{
			populateSpellBookComboBox();
			populateTables();
		}
	}


	private void delBookButton_clicked(ActionEvent evt)
	{
		final PlayerCharacter aPC = Globals.getCurrentPC();
		aPC.setDirty(true);
		String aString = spellBookNameText.getText();
		if (aPC.delSpellBook(aString))
		{
			populateSpellBookComboBox();
			populateTables();
		}
	}


	/**
	 *  Model for the JTable containing the spells.
	 *
	 * @author     Bryan McRoberts <a href="mailto:merton_monk@users.sourceforge.net">
	 *      merton_monk@users.sourceforge.net</a>
	 * @created    den 11 maj 2001
	 * @version    $Revision: 1.1 $
	 */
	public class SpellTableModel extends AbstractTableModel
	{
		/**
		 *  Contains the spells
		 *
		 * @since
		 */
		public ArrayList data = new ArrayList();
		String[] nameList = {""};

		public SpellTableModel()
		{
			if (Globals.isSSd20Mode())
			{
				nameList = new String[]{"Name", "Element", "Nature", "Casting Threshold", "Range", "Target/Effect/Area", "Duration", "Save Info", "SR", "Source File"};
			}
			else if (Globals.isWheelMode())
			{
				nameList = new String[]{"Name", "Element", "Nature", "Casting Threshold", "Range", "Target/Effect/Area", "Duration", "Save Info", "SR", "Source File"};
			}
			else
			{
				nameList = new String[]{"Name", "Times", "School", "SubSchool", "Descriptor", "Components", "Casting Time",
																"Range", "Effect", "Effect Type", "Duration", "Save Info", "SR", "Source File"};
			}
		}

		/**
		 *  Sets the Data attribute of the SpellTableModel object
		 *
		 * @param  aArrayList  The new Data value
		 * @since
		 */
		public void setData(ArrayList aArrayList)
		{
			data = aArrayList;
		}


		/**
		 *  Gets the ColumnCount attribute of the SpellTableModel object
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
		 *  Gets the RowCount attribute of the SpellTableModel object
		 *
		 * @return    The RowCount value
		 * @since
		 */
		public int getRowCount()
		{
			return data.size();
		}


		/**
		 *  Gets the ValueAt attribute of the SpellTableModel object
		 *
		 * @param  row  Description of Parameter
		 * @param  col  Description of Parameter
		 * @return      The ValueAt value
		 * @since
		 */
		public Object getValueAt(int row, int col)
		{
			Object retVal = "";
			if (row < data.size())
			{
				Spell aSpell = (Spell)data.get(row);
				if (aSpell == null)
					return retVal;
				if (Globals.isSSd20Mode())
				{
					if (col == 1)
						col = 2;
					else if (col == 2)
						col = 3;
					else if (col == 3)
						col = 14;
					else if (col == 4)
						col = 7;
					else if (col == 5)
						col = 8;
					else if (col == 6)
						col = 10;
					else if (col == 7)
						col = 11;
					else if (col == 8)
						col = 12;
					else if (col == 9)
						col = 13;
					else
						col = 0;
				}

				switch (col)
				{
					case 0:
						retVal = aSpell.getName();
						break;
					case 1:
						retVal = aSpell.timesForSpellBook(spellBookComboBox.getSelectedItem().toString());
						break;
					case 2:
						retVal = aSpell.getSchool();
						String addString = "";
						addString = aSpell.getDomainString(" [", ",", "]", spellLevelComboBox.getSelectedIndex(), domainList);
						retVal = retVal + addString;
						break;
					case 3:
						retVal = aSpell.getSubschool();
						break;
					case 4:
						retVal = aSpell.descriptor();
						break;
					case 5:
						retVal = aSpell.getComponentList();
						break;
					case 6:
						retVal = aSpell.getCastingTime();
						break;
					case 7:
						retVal = aSpell.getRange();
						break;
					case 8:
						retVal = aSpell.getEffect();
						break;
					case 9:
						retVal = aSpell.getEffectType();
						break;
					case 10:
						retVal = aSpell.getDuration();
						break;
					case 11:
						retVal = aSpell.getSaveInfo();
						break;
					case 12:
						retVal = aSpell.getSR();
						break;
					case 13:
						retVal = aSpell.getSource();
						break;
					case 14:
						retVal = aSpell.getCastingThreshold() + "";
						break;
					default:
						//XYZ Impossible, should be logged
						break;
				}
			}
			return retVal;
		}


		// The default implementations of these methods in

		// AbstractTableModel would work, but we can refine them.

		/**
		 *  Gets the ColumnName attribute of the SpellTableModel object
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
		 *  Gets the CellEditable attribute of the SpellTableModel object
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


	/**
	 *  Model for the JTable containing the spell options
	 *
	 * @author     Bryan McRoberts <a href="mailto:merton_monk@users.sourceforge.net">
	 *      merton_monk@users.sourceforge.net</a>
	 * @created    den 11 maj 2001
	 */
	public class SpellOptionsModel extends AbstractTableModel
	{
		/**
		 *  Contains the options
		 *
		 * @since
		 */
		public ArrayList data = new ArrayList();
		String[] nameList = {""};

		public SpellOptionsModel()
		{
			if (Globals.isSSd20Mode())
			{
				nameList = new String[]{"Class", "", "Book"};
			}
			else if (Globals.isWheelMode())
			{
				nameList = new String[]{"Talent", "", ""};
			}
			else
			{
				nameList = new String[]{"Class", "Level", "Book"};
			}
		}

		/**
		 *  Sets the Data attribute of the SpellOptionsModel object
		 *
		 * @param  aArrayList  The new Data value
		 * @since
		 */
		public void setData(ArrayList aArrayList)
		{
			data = aArrayList;
		}


		/**
		 *  Gets the ColumnCount attribute of the SpellOptionsModel object
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
		 *  Gets the RowCount attribute of the SpellOptionsModel object
		 *
		 * @return    The RowCount value
		 * @since
		 */
		public int getRowCount()
		{
			return 1;
		}


		/**
		 *  Gets the ColumnName attribute of the SpellOptionsModel object
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
		 *  Gets the ValueAt attribute of the SpellOptionsModel object
		 *
		 * @param  row  Description of Parameter
		 * @param  col  Description of Parameter
		 * @return      The ValueAt value
		 * @since
		 */
		public Object getValueAt(int row, int col)
		{
			JComboBox aBox = (JComboBox)data.get(col);
			return aBox.getSelectedItem();
		}


		/**
		 *  Gets the CellEditable attribute of the SpellOptionsModel object
		 *
		 * @param  row  Description of Parameter
		 * @param  col  Description of Parameter
		 * @return      The CellEditable value
		 * @since
		 */
		public boolean isCellEditable(int row, int col)
		{
			return true;
		}
	}


	/**
	 *  Model for the JTable containing the spell feats.
	 *
	 * @author     Bryan McRoberts <a href="mailto:merton_monk@users.sourceforge.net">
	 *      merton_monk@users.sourceforge.net</a>
	 * @created    den 11 maj 2001
	 */
	public class SpellFeatModel extends AbstractTableModel
	{
		/**
		 *  Contains the feats
		 *
		 * @since
		 */
		public ArrayList data = new ArrayList();
		final String[] nameList = {"Feat"};


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
		 *
		 *
		 */
		public Class getColumnClass(int c)
		{
			return getValueAt(0, c).getClass();
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
		populateTables();
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
}

