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
package pcgen.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.Vector;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import pcgen.core.Deity;
import pcgen.core.Domain;
import pcgen.core.Globals;

/**
 * @author  Mario Bonassin
 * @version $Revision: 1.1 $
 */
public class InfoDomain extends JPanel
{
	private JLabel deityName = new JLabel("Name");
	private JTableEx domainTable = null;
	private JTableEx deityTable = null;
	private DeityModel dataModel = new DeityModel();
	private DomainModel domModel = new DomainModel();
	private final Object[] deiLongValues = {"Qxx","Namexxxxxxx","Descriptionxxxxxxxxxxxxxxx","Domainsxxxxxxxxxx","Alignments","Alignment","Weapon","Holy Item","PreReqsxxx","Source"};
	private static String[] s_columnNames = {"Q","Name","Description","Domains","Followers AL","Deity AL","Weapon","Holy Item","PreReqs","Source"};
	public static String[] s_domainColList = {"Domain", "Power", "PreReqs","Source"};
	private final Object[] domLongValues = {"Domainxxxx","Powerxxxxxxxxxxxxx","PreReqsxxx","Source"};
	private JPanel domCenter = new JPanel();
	private JPanel domCenterEast = new JPanel();
	private JPanel domCenterWest = new JPanel();
	private JPanel domCenterCenter = new JPanel();
	private JPanel domSouth = new JPanel();
	private JPanel domSouthNorth = new JPanel();
	private JPanel domNorth = new JPanel();
	private JLabel filter = new JLabel("Filter Deities: ");
	private JLabel deityLabel = new JLabel("Deity: ");
	private JLabel domLabel = new JLabel("Domain Choices: ");
	private JComboBox filters = new JComboBox();
	private ArrayList deityQList = new ArrayList();
	private boolean ALLOW_ROW_SELECTION = true;
	private boolean listChange = false;
	private TableSorter sorter = null;
	private TableSorter sorter2 = null;

	public InfoDomain()
	{
		initComponents();
	}

	private void initComponents()
	{
		//Deity table set up
		sorter = new TableSorter(dataModel);
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
				cellWidth = 15;
			else
			{
				comp = deityTable.getDefaultRenderer(dataModel.getColumnClass(i)).getTableCellRendererComponent(deityTable, deiLongValues[i], false, false, 0, i);
				cellWidth = comp.getPreferredSize().width;
			}
			column.setPreferredWidth(cellWidth);
		}
		deityTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		deityTable.setToolTipText("Double Click to choose");


		//Domain table Setup
		sorter2 = new TableSorter(domModel);
		domainTable = new JTableEx(sorter2);
		domainTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		sorter2.addMouseListenerToHeaderInTable(domainTable);
		//Set up column sizes.
		TableColumn column2 = null;
		Component comp2 = null;
		cellWidth = 0;
		for (int i = 0; i < s_domainColList.length; i++)
		{
			column2 = domainTable.getColumnModel().getColumn(i);
			if (i != 2)
			{
				comp2 = domainTable.getDefaultRenderer(domModel.getColumnClass(i)).getTableCellRendererComponent(domainTable, domLongValues[i], false, false, 0, i);
				cellWidth = comp2.getPreferredSize().width;
			}
			else
				cellWidth = 15;
			column2.setPreferredWidth(cellWidth);
		}

		//Set up tool tips for the domain table
		domainTable.setToolTipText("Click Domains to choose");
		String[] s_list = { "" };
		updateDomainCellEditor(s_list);

		if (ALLOW_ROW_SELECTION)
		{
			// true by default
			ListSelectionModel rowSM = deityTable.getSelectionModel();
			rowSM.addListSelectionListener(new ListSelectionListener()
			{
				public int selectedRow;
				public ListSelectionModel lsm;

				public void valueChanged(ListSelectionEvent e)
				{

					//Ignore extra messages.
					if (e.getValueIsAdjusting()) return;
					lsm = (ListSelectionModel)e.getSource();
					if (!lsm.isSelectionEmpty())
					{
						InfoDomain.this.deityTable.addMouseListener(new MouseAdapter()
						{
							public void mouseClicked(MouseEvent f)
							{
								if (f.getClickCount() == 2)
								{
									int row = lsm.getMinSelectionIndex();
									if (row < 0)
										return;
									selectedRow = sorter.getRowTranslated(lsm.getMinSelectionIndex());
									if (selectedRow < 0)
										return;
									Globals.currentPC.setDirty(true);
									InfoDomain.this.selectDeityIndex(selectedRow);
								}
								else
								{
									return;
								}
							}
						});
					}
				}
			});
		}
		else
		{
			deityTable.setRowSelectionAllowed(false);
		}


		//sets up filter
		filters.addItem("All");
		filters.addItem("Qualified");
		//sort types
		for (int i = 0; i < Globals.deityList.size(); i++)
		{
			Deity bDeity = (Deity)Globals.deityList.get(i);
			deityQList.add(bDeity.alignment());
		}
		Object[] objArray = deityQList.toArray();
		Arrays.sort(objArray);
		deityQList.clear();
		Object lastObj = null;
		for (int i = 0; i < objArray.length; i++)
		{
			if (!objArray[i].equals(lastObj))
				deityQList.add(objArray[i]);
			lastObj = objArray[i];
		}
		for (int i = 0; i != deityQList.size(); i++)
			filters.addItem(deityQList.get(i));
		//select filter type and redo table
		filters.addItemListener(new ItemListener()
		{
			public void itemStateChanged(ItemEvent e)
			{
				dataModel.setFilter(e.getItem().toString(), filters.getSelectedIndex());
			}
		});
		//Setup GUI
		domCenterWest.setLayout(new BorderLayout());
		domCenterEast.setLayout(new BorderLayout());
		domCenter.setLayout(new FlowLayout());
		domSouth.setLayout(new BorderLayout());
		domSouthNorth.setLayout(new FlowLayout());
		domNorth.setLayout(new BorderLayout());
		JScrollPane scrollPane = new JScrollPane(deityTable);
		JScrollPane scrollPane2 = new JScrollPane(domainTable);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollPane2.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		scrollPane2.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollPane2.setBackground(new Color(255, 255, 255));
		scrollPane2.setPreferredSize(new Dimension(100, 75));
		domNorth.add(scrollPane, BorderLayout.CENTER);
		domCenterWest.add(filter, BorderLayout.CENTER);
		domCenterEast.add(filters, BorderLayout.CENTER);
		domCenter.add(domCenterWest);
		domCenter.add(domCenterEast);
		domNorth.add(domCenter, BorderLayout.SOUTH);
		domSouthNorth.add(deityLabel);
		domSouthNorth.add(deityName);
		domSouth.add(domSouthNorth, BorderLayout.CENTER);
		domSouth.add(scrollPane2, BorderLayout.SOUTH);
		this.setLayout(new BorderLayout());
		this.add(domNorth, BorderLayout.CENTER);
		this.add(domSouth, BorderLayout.SOUTH);
		addComponentListener(new java.awt.event.ComponentAdapter()
		{
			public void componentShown(java.awt.event.ComponentEvent evt)
			{
				// executed when the component is shown
				requestDefaultFocus();
				domModel.fireTableDataChanged();
				dataModel.fireTableDataChanged();
			}
		});
		if (Globals.currentPC.deity() != null)
			selectDeityNamed(Globals.currentPC.deity().name());

		int[] cols = {0,1};
		domainTable.calcColumnWidths(cols);

	}

	public void paint(Graphics g)
	{
		super.paint(g);
		pcChanged();
	}

	/** <code>pcChanged</code> update data listening for a changed PC
	 */
	public void pcChanged()
	{
		pcgen.core.PlayerCharacter aPC = pcgen.core.Globals.currentPC;
		if (aPC != null)
		{
			if (aPC.getDomainMax() < domModel.domainChoices().size())
			{
				Vector aVector = domModel.domainChoices();
				for (int i = domModel.domainChoices().size() - 1; i >= aPC.getDomainMax(); i--)
					aVector.removeElementAt(i);
				domModel.setDomainChoices(aVector);
				domModel.fireTableDataChanged();
			}
			else if (aPC.getDomainMax() > domModel.domainChoices().size())
			{
				Vector aVector = domModel.domainChoices();
				for (int i = domModel.domainChoices().size(); i < aPC.getDomainMax(); i++)
					aVector.addElement(null);
				domModel.setDomainChoices(aVector);
				domModel.fireTableDataChanged();
			}
		}
	}

	public void updateDomainCellEditor(String[] s_list)
	{
		//Fiddle with the Domain column's cell editors/renderers.
		TableColumn domainColumn = domainTable.getColumnModel().getColumn(0);
		//Set up the editor for the domain cells.
		domainColumn.setCellRenderer(new DomainRenderer(s_list));
		domainColumn.setCellEditor(new DomainEditor(s_list));
	}

	public void selectDeityIndex(int selectedRow)
	{
		ListSelectionModel lsm = deityTable.getSelectionModel();
		if (dataModel.getValueAt(selectedRow, 0) == "N")
		{
			JOptionPane.showMessageDialog(null, "You cannot select this deity.");
			lsm.clearSelection();
			return;
		}
		String aDeityName = dataModel.getValueAt(selectedRow, 1).toString();
		InfoDomain.this.deityName.setText(aDeityName);
		Globals.currentPC.setDeity((Deity)Globals.getDeityNamed(aDeityName));
		String domains = dataModel.getValueAt(selectedRow, 3).toString();
		StringTokenizer aTok = new StringTokenizer(domains, ",", false);
		Vector aVector = new Vector();
		Vector bVector = new Vector();
		if (domains.equals("ALL"))
		{
			for (Iterator i = Globals.domainList.iterator(); i.hasNext();)
			{
				Domain aDomain = (Domain)i.next();
				aVector.addElement(aDomain.name());
			}
		}
		else
			while (aTok.hasMoreTokens())
			{
				String aToken = aTok.nextToken();
				Domain aDomain = Globals.getDomainNamed(aToken);
				if (aDomain != null && !aVector.contains(aDomain))
				{
					aVector.addElement(aToken);
				}
			}
		String[] stringList = new String[aVector.size()];
		for (int i = 0; i < aVector.size(); i++)
		{
			stringList[i] = (String)aVector.elementAt(i);
			Domain aDomain = Globals.currentPC.getDomainNamed(stringList[i]);
			if (aDomain != null)
				bVector.addElement(aDomain);
		}
		for (int i = bVector.size(); i < Globals.currentPC.getDomainMax(); i++)
			bVector.addElement(null);
		InfoDomain.this.updateDomainCellEditor(stringList);
		domModel.setDomainList(aVector);
		domModel.setDomainChoices(bVector);
	}

	public void selectDeityNamed(String aName)
	{
		for (int i = 0; i < Globals.deityList.size(); i++)
		{
			String aString = (String)dataModel.getValueAt(i, 1);
			if (aString.equals(aName))
			{
				selectDeityIndex(i);
				break;
			}
		}
	}

	public static final class DeityModel extends AbstractTableModel
	{
		private ArrayList deitys = Globals.deityList;

		public void setFilter(String filterID, int index)
		{
			Iterator i;
			Deity theDeity;
			if (filterID.equals("All"))
				deitys = Globals.deityList;
			else if (filterID.equals("Qualified"))
			{
				deitys = new ArrayList();
				i = Globals.deityList.iterator();
				while (i.hasNext())
				{
					theDeity = (Deity)i.next();
					if (theDeity.canBeSelectedBy(Globals.currentPC.classList(), Globals.currentPC.alignment(), Globals.currentPC.race().name(), Globals.currentPC.gender()))
						deitys.add(theDeity);
				}
			}
			else if (index > 1)
			{
				deitys = new ArrayList();
				i = Globals.deityList.iterator();
				while (i.hasNext())
				{
					theDeity = (Deity)i.next();
					if (theDeity.alignment().equals(filterID))
						deitys.add(theDeity);
				}
			}
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
			Object retVal = Boolean.FALSE;
			if (deitys != null && row >= 0 && row < deitys.size())
			{
				Deity aDeity = (Deity)deitys.get(row);
				switch (column)
				{
					case 0:
						retVal = (aDeity.canBeSelectedBy(Globals.currentPC.classList(), Globals.currentPC.alignment(),
							Globals.currentPC.race().name(), Globals.currentPC.gender())) ? "Y" : "N";
						break;
					case 1:
						retVal = aDeity.name();
						break;
					case 2:
						retVal = aDeity.description();
						break;
					case 3:
						retVal = aDeity.domainListString();
						break;
					case 4:
						String aString = "";
						for (int i = 0; i < aDeity.alignments().length(); i++)
						{
							if (aString.length() > 0) aString = aString + ",";
							int x = new Integer(aDeity.alignments().substring(i, i + 1)).intValue();
							aString = aString + aDeity.s_alignStrings[x];
						}
						retVal = aString;
						break;
					case 5:
						retVal = aDeity.alignment();
						break;
					case 6:
						retVal = aDeity.favoredWeapon();
						break;
					case 7:
						retVal = aDeity.holyItem();
						break;
					case 8:
						retVal = "";
						break;
					case 9:
						retVal = aDeity.getSourceFile();

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
			if (Globals.deityList != null)
			{
				return deitys.size();
			}
			else
				return 0;
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

	public class DomainModel extends AbstractTableModel
	{
		protected Vector domainChoices = new Vector();
		protected Vector domainList = new Vector();

		public DomainModel()
		{
		}
		/* return list of domains associated with the current deity */
		/* sets the list of appropriate choices */
		public Vector domainChoices()
		{
			return domainChoices;
		}

		public void setDomainChoices(Vector aVector)
		{
			domainChoices = aVector;
			fireTableDataChanged();
		}

		public void setDomainList(Vector aVector)
		{
			domainList = aVector;
			fireTableDataChanged();
		}

		// These methods always need to be implemented.
		public int getColumnCount()
		{
			return s_domainColList.length;
		}

		public int getRowCount()
		{
			return Globals.currentPC.getDomainMax();
		}

		public void setValueAt(Object value, int row, int column)
		{
			pcgen.core.PlayerCharacter aPC = pcgen.core.Globals.currentPC;
			if (aPC == null) return;
			if (row >= domainChoices.size())
				return;
			switch (column)
			{
				case 0:
					aPC.setDirty(true);
					Integer anInt = (Integer)value;
					if (anInt.intValue() < 0 || anInt.intValue() > domainList.size())
						return;
					String aString = (String)domainList.elementAt(anInt.intValue());
					if (aString != null)
					{
						Domain aDomain = Globals.getDomainNamed(aString);
						if (!aDomain.qualifiesForDomain())
						{
							JOptionPane.showMessageDialog(null, "You don't qualify for this domain.");
							return;
						}
						if (aDomain != null)
						{

							if (aPC.getDomainNamed(aDomain.name()) != null)
							{
								aDomain = (Domain)aDomain.clone();
							}
							aPC.setDomainNumber(aDomain, row);
							aDomain.setIsLocked(true);
							domainChoices.setElementAt(aDomain, row);
							fireTableDataChanged();
						}
					}
					break;
			}
		}

		public Object getValueAt(int row, int col)
		{
			if (row >= domainChoices.size())
				return "";
			Domain aDomain = (Domain)domainChoices.elementAt(row);
			if (aDomain == null)
			{
				if (col != 0)
					return "";
				else
					return new Integer(0);
			}
			Object retVal2 = "";
			switch (col)
			{
				case 0:
					retVal2 = new Integer(domainList.indexOf(aDomain.name()));
					break;
				case 1:
					retVal2 = aDomain.grantedPower();
					break;
				case 2:
					retVal2 = aDomain.preReqString();
					break;
				case 3:
					retVal2 = aDomain.getSourceFile();
					break;
			}
			return retVal2;
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

		public boolean isCellEditable(int row, int col)
		{
			if (col < 1)
				return true;
			else
				return false;
		}
	}

	public static final class DomainRenderer extends JComboBox implements TableCellRenderer
	{

		public DomainRenderer(String[] choices)
		{
			super(choices);
		}

		public Component getTableCellRendererComponent(JTable jTable,
			Object value,
			boolean isSelected,
			boolean hasFocus,
			int row,
			int column)
		{
			if (value instanceof Integer)
			{
				setSelectedIndex(((Integer)value).intValue());
			}
			return this;
		}

	}

	private static final class DomainEditor extends JComboBox implements TableCellEditor
	{

		private final transient Vector d_listeners = new Vector();
		private transient int d_originalValue = 0;

		public DomainEditor(String[] choices)
		{
			super(choices);
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
			d_listeners.addElement(cellEditorListener);
		}

		public Component getTableCellEditorComponent(JTable jTable, Object value, boolean isSelected, int row, int column)
		{
			if (value == null) return this;
			d_originalValue = getSelectedIndex();
			if (value instanceof Integer)
			{
				setSelectedIndex(((Integer)value).intValue());
			}
			else
			{
				setSelectedIndex(0);
			}
			jTable.setRowSelectionInterval(row, row);
			jTable.setColumnSelectionInterval(column, column);
			return this;
		}

		public void cancelCellEditing()
		{
			fireEditingCanceled();
		}

		public boolean isCellEditable(java.util.EventObject eventObject)
		{
			return true;
		}

		public void removeCellEditorListener(CellEditorListener cellEditorListener)
		{
			d_listeners.removeElement(cellEditorListener);
		}

		public java.lang.Object getCellEditorValue()
		{
			return new Integer(getSelectedIndex());
		}

		public boolean stopCellEditing()
		{
			fireEditingStopped();
			return true;
		}

		public boolean shouldSelectCell(java.util.EventObject eventObject)
		{
			return true;
		}

		private void fireEditingCanceled()
		{
			setSelectedIndex(d_originalValue);
			ChangeEvent ce = new ChangeEvent(this);
			for (int i = d_listeners.size() - 1; i >= 0; --i)
			{
				((CellEditorListener)d_listeners.elementAt(i)).editingCanceled(ce);
			}
		}

		private void fireEditingStopped()
		{
			ChangeEvent ce = new ChangeEvent(this);
			for (int i = d_listeners.size() - 1; i >= 0; --i)
			{
				((CellEditorListener)d_listeners.elementAt(i)).editingStopped(ce);
			}
		}
	}

}
