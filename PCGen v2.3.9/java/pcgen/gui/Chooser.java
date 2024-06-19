/*
 * Chooser.java
 * Copyright 2001 (C) Bryan McRoberts <mocha@mcs.net>
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

import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;
import pcgen.core.Globals;

/**
 * This dialog type accepts a list of available items, a choice
 * limit, and some additional flags and switches. The user can
 * select and remove values until the required number of
 * choices have been made. The dialog is always modal, so a
 * call to show() will block program execution.
 *
 * @author    Matt Woodard
 */
public class Chooser extends JDialog
{
	/** The default available list column array */
	private final static List AVAILABLE_COLUMN_NAMES =
		Arrays.asList(new Object[]{"Available"});

	/** The default selected list column array */
	private final static List SELECTED_COLUMN_NAMES =
		Arrays.asList(new Object[]{"Selected"});

	/** An empty string array used for conversions */
	private final static String[] STRING_ARRAY = new String[]{};

	/** The choices remaining */
	private int mPool;

	/** Whether or not to allow duplicate choices */
	private boolean mAllowDuplicates = false;

	/** Whether or not to force mPool=0 when closing */
	private boolean mPoolFlag = true;

	/** The column containing the cost for an item */
	private int mCostColumnNumber = -1;

	/** The list of unique items */
	private List mUniqueList = new ArrayList();

	/** The JTableEx holding available items */
	private JTableEx mAvailableTable;

	/** The JTableEx holding selected items */
	private JTableEx mSelectedTable;

	/** The list of available items */
	private List mAvailableList = new ArrayList();

	/** The model table for the available item table */
	private ChooserTableModel mAvailableModel = new ChooserTableModel();

	/** The model table for the selected item table */
	private ChooserTableModel mSelectedModel = new ChooserTableModel();

	/** The list of selected items */
	private List mSelectedList = new ArrayList();

	/** The JLabel showing messages */
	private JLabel mMessageText;

	/** The JLabel showing the remaining pool */
	private JLabel mPoolText;

	/** The JButton for adding available items to the selected list */
	private JButton mAddButton;

	/** The JButton for removing selected items */
	private JButton mRemoveButton;

	/** The JButton for closing the dialog */
	private JButton mCloseButton;

	/** The available data memory structure */
	private Object mAvailableData[][];

	/** The selected data memory structure */
	private Object mSelectedData[][];

	/** The available table column names */
	private List mAvailableColumnNames;

	/** The selected table column names */
	private List mSelectedColumnNames;

	/**
	 * This table model implements those methods required to support
	 * the simple needs of the available and selected tables.
	 *
	 * @author    Matt Woodard
	 */
	private class ChooserTableModel extends AbstractTableModel
	{
		/** The column names */
		String[] mColumnNames;

		/** The data memory */
		Object[][] mData;

		/** The column classes */
		Class[] mColumnClasses;

		/** The number of columns */
		int mColumns;

		/** The number of rows */
		int mRows;

		/**
		 * Initializes the table's memory structure and notifies listeners
		 * of the insertion.
		 *
		 * @param data  The new Data value
		 * @author      Matt Woodard
		 */
		public void setData(Object[][] data)
		{
			mData = data;

			mRows = 0;

			mColumnClasses = new Class[mColumns];

			if (mData != null)
			{
				mRows = mData.length;
				if (mRows > 0)
				{
					Object[] row = data[0];
					for (int c = 0; c < mColumns; c++)
					{
						// Assume the row data isn't null
						mColumnClasses[c] = row[c].getClass();
					}
				}
			}

			fireTableChanged(new TableModelEvent(this, 0, mRows - 1,
				TableModelEvent.ALL_COLUMNS,
				TableModelEvent.INSERT));
		}

		/**
		 * Sets the column names
		 *
		 * @param names  The new ColumnsNames value
		 * @author       Matt Woodard
		 */
		public void setColumnsNames(String[] names)
		{
			mColumnNames = names;
			mColumns = mColumnNames.length;

			fireTableStructureChanged();
		}

		/**
		 * Sets the value in the given location
		 *
		 * @param value   The new ValueAt value
		 * @param row     The new ValueAt value
		 * @param column  The new ValueAt value
		 * @author        Matt Woodard
		 */
		public void setValueAt(Object value, int row, int column)
		{
			mData[row][column] = value;
		}

		/**
		 * Gets the number of columns
		 *
		 * @return   The ColumnCount value
		 * @author   Matt Woodard
		 */
		public int getColumnCount()
		{
			return mColumns;
		}

		/**
		 * Gets the number of rows
		 *
		 * @return   The RowCount value
		 * @author   Matt Woodard
		 */
		public int getRowCount()
		{
			return mRows;
		}

		/**
		 * Gets the value at the given location
		 *
		 * @param row  Description of Parameter
		 * @param col  Description of Parameter
		 * @return     The ValueAt value
		 * @author     Matt Woodard
		 */
		public Object getValueAt(int row, int col)
		{
			return mData[row][col];
		}

		/**
		 * Gets the specified column name
		 *
		 * @param column  Description of Parameter
		 * @return        The ColumnName value
		 * @author        Matt Woodard
		 */
		public String getColumnName(int column)
		{
			return mColumnNames[column];
		}

		/**
		 * Gets the specified column class
		 *
		 * @param column  Description of Parameter
		 * @return        The ColumnClass value
		 * @author        Matt Woodard
		 */
		public Class getColumnClass(int column)
		{
			return mColumnClasses[column];
		}

		/**
		 * Always returns false
		 *
		 * @param row  Description of Parameter
		 * @param col  Description of Parameter
		 * @return     The CellEditable value
		 * @author     Matt Woodard
		 */
		public boolean isCellEditable(int row, int col)
		{
			return false;
		}
	}

	/**
	 * Chooser constructor.
	 *
	 * @author   Matt Woodard
	 */
	public Chooser()
	{
		super(Globals.getRootFrame());
		initComponents();
	}

	/**
	 * Chooser constructor.
	 *
	 * @param owner  java.awt.Dialog
	 * @author       Matt Woodard
	 */
	public Chooser(java.awt.Dialog owner)
	{
		super(owner);
		initComponents();
	}

	/**
	 * Chooser constructor.
	 *
	 * @param owner  java.awt.Dialog
	 * @param title  java.lang.String
	 * @author       Matt Woodard
	 */
	public Chooser(java.awt.Dialog owner, String title)
	{
		super(owner, title);
		initComponents();
	}

	/**
	 * Chooser constructor.
	 *
	 * @param owner  java.awt.Dialog
	 * @param title  java.lang.String
	 * @param modal  boolean
	 * @author       Matt Woodard
	 */
	public Chooser(java.awt.Dialog owner, String title, boolean modal)
	{
		super(owner, title, modal);
		initComponents();
	}

	/**
	 * Chooser constructor.
	 *
	 * @param owner  java.awt.Dialog
	 * @param modal  boolean
	 * @author       Matt Woodard
	 */
	public Chooser(java.awt.Dialog owner, boolean modal)
	{
		super(owner, modal);
		initComponents();
	}

	/**
	 * Chooser constructor.
	 *
	 * @param owner  java.awt.Frame
	 * @author       Matt Woodard
	 */
	public Chooser(java.awt.Frame owner)
	{
		super(owner);
		initComponents();
	}

	/**
	 * Chooser constructor.
	 *
	 * @param owner  java.awt.Frame
	 * @param title  java.lang.String
	 * @author       Matt Woodard
	 */
	public Chooser(java.awt.Frame owner, String title)
	{
		super(owner, title);
		initComponents();
	}

	/**
	 * Chooser constructor.
	 *
	 * @param owner  java.awt.Frame
	 * @param title  java.lang.String
	 * @param modal  boolean
	 * @author       Matt Woodard
	 */
	public Chooser(java.awt.Frame owner, String title, boolean modal)
	{
		super(owner, title, modal);
		initComponents();
	}

	/**
	 * Chooser constructor.
	 *
	 * @param owner  java.awt.Frame
	 * @param modal  boolean
	 * @author       Matt Woodard
	 */
	public Chooser(java.awt.Frame owner, boolean modal)
	{
		super(owner, modal);
		initComponents();
	}

	/**
	 * @author Matthew Woodard
	 */
	public void clearSelectedList()
	{
		mSelectedList.clear();
		updateSelectedTable();
	}

	/**
	 * Closes the dialog if the pool is satisfied
	 *
	 * @author   Matt Woodard
	 */
	private void close()
	{
		if (mPool <= 0 || mPoolFlag == false)
		{
			this.hide();
		}
		else
		{
			setMessageText("You must complete choices.");
		}
	}

	/**
	 * Returns the available column names
	 *
	 * @return   java.util.List
	 * @author   Matt Woodard
	 */
	public List getAvailableColumnNames()
	{
		return mAvailableColumnNames;
	}

	/**
	 * Returns the available item list
	 *
	 * @return   java.util.List
	 * @author   Matt Woodard
	 */
	public List getAvailableList()
	{
		return new ArrayList(mAvailableList);
	}

	/**
	 * Returns the selected column names
	 *
	 * @return   java.util.List
	 * @author   Matt Woodard
	 */
	public List getSelectedColumnNames()
	{
		return mSelectedColumnNames;
	}

	/**
	 * Returns the selected item list
	 *
	 * @return   java.util.List
	 * @author   Matt Woodard
	 */
	public List getSelectedList()
	{
		return new ArrayList(mSelectedList);
	}

	/**
	 * Initializes the components of the dialog
	 *
	 * @author   Matt Woodard
	 */
	private void initComponents()
	{
		// Initialize basic dialog settings
		setModal(true);
		setSize(new Dimension(640, 400));
		setTitle("Chooser");

		Container contentPane = getContentPane();
		contentPane.setLayout(new GridBagLayout());

		// Create tables
		TableSorter sorter = new TableSorter(mAvailableModel = new ChooserTableModel());
		JScrollPane availableScrollPane = new JScrollPane(mAvailableTable = new JTableEx(sorter));
		availableScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		availableScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		sorter.addMouseListenerToHeaderInTable(mAvailableTable);

		sorter = new TableSorter(mSelectedModel = new ChooserTableModel());
		JScrollPane selectedScrollPane = new JScrollPane(mSelectedTable = new JTableEx(sorter));
		selectedScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		selectedScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		sorter.addMouseListenerToHeaderInTable(mSelectedTable);

		// Initialize selection types & events
		ListSelectionModel availableSelectionModel = mAvailableTable.getSelectionModel();

		// Initialize selection types & events
		ListSelectionModel
			selectedSelectionModel = mSelectedTable.getSelectionModel();

		availableSelectionModel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		selectedSelectionModel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		ListSelectionListener listSelectionListener =
			new ListSelectionListener()
			{
				/**
				 * Description of the Method
				 *
				 * @param evt  Description of Parameter
				 * @author     mwoodard
				 */
				public void valueChanged(ListSelectionEvent evt)
				{
					if (!evt.getValueIsAdjusting())
					{
						updateButtonStates();
					}
				}
			};

		availableSelectionModel.addListSelectionListener(listSelectionListener);
		selectedSelectionModel.addListSelectionListener(listSelectionListener);

		// Initialize the mouse events

		mAvailableTable.addMouseListener(
			new MouseAdapter()
			{
				public void mouseClicked(MouseEvent evt)
				{
					if (evt.getClickCount() == 2)
					{
						selectAvailable();
					}
				}
			}
		);

		mSelectedTable.addMouseListener(
			new MouseAdapter()
			{
				public void mouseClicked(MouseEvent evt)
				{
					if (evt.getClickCount() == 2)
					{
						removeSelected();
					}
				}
			}
		);

		// Create labels
		JLabel selectionRemainingLabel = new JLabel("Number of selections remaining:");

		// Create these labels with " " to force them to layout correctly
		mMessageText = new JLabel(" ");
		mPoolText = new JLabel(" ");

		// Create buttons
		mAddButton = new JButton("Add");
		mAddButton.setMnemonic(KeyEvent.VK_A);
		mCloseButton = new JButton("Close");
		mCloseButton.setMnemonic(KeyEvent.VK_C);
		mRemoveButton = new JButton("Remove");
		mRemoveButton.setMnemonic(KeyEvent.VK_R);

		ActionListener eventListener =
			new ActionListener()
			{
				/**
				 * Description of the Method
				 *
				 * @param evt  Description of Parameter
				 * @author     Matt Woodard
				 */
				public void actionPerformed(ActionEvent evt)
				{
					if (evt.getSource() == mAddButton)
					{
						selectAvailable();
					}
					else if (evt.getSource() == mRemoveButton)
					{
						removeSelected();
					}
					else if (evt.getSource() == mCloseButton)
					{
						close();
					}
				}
			};

		mAddButton.addActionListener(eventListener);
		mRemoveButton.addActionListener(eventListener);
		mCloseButton.addActionListener(eventListener);

		// Add controls to content pane
		GridBagConstraints constraints;

		// Add available list
		constraints = new GridBagConstraints();
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.gridwidth = 3;
		constraints.fill = GridBagConstraints.BOTH;
		constraints.anchor = GridBagConstraints.WEST;
		constraints.weightx = 1.0;
		constraints.weighty = 1.0;
		constraints.insets = new Insets(4, 4, 4, 4);
		contentPane.add(availableScrollPane, constraints);

		// Add 'add' button
		constraints = new GridBagConstraints();
		constraints.gridx = 0;
		constraints.gridy = 1;
		constraints.gridwidth = 3;
		constraints.weighty = 0.01;
		constraints.insets = new Insets(0, 4, 4, 4);
		contentPane.add(mAddButton, constraints);

		// Add selected list
		constraints = new GridBagConstraints();
		constraints.gridx = 0;
		constraints.gridy = 2;
		constraints.gridwidth = 3;
		constraints.fill = GridBagConstraints.BOTH;
		constraints.anchor = GridBagConstraints.WEST;
		constraints.weighty = 1.0;
		constraints.insets = new Insets(0, 4, 4, 4);
		contentPane.add(selectedScrollPane, constraints);

		// Add 'remove' button
		constraints = new GridBagConstraints();
		constraints.gridx = 0;
		constraints.gridy = 3;
		constraints.gridwidth = 3;
		constraints.weighty = 0.01;
		constraints.insets = new Insets(0, 4, 4, 4);
		contentPane.add(mRemoveButton, constraints);

		// Add message text
		constraints = new GridBagConstraints();
		constraints.gridx = 0;
		constraints.gridy = 4;
		constraints.gridwidth = 2;
		constraints.fill = GridBagConstraints.BOTH;
		constraints.anchor = GridBagConstraints.WEST;
		constraints.weighty = 0.01;
		constraints.insets = new Insets(0, 4, 4, 4);
		contentPane.add(mMessageText, constraints);

		// Add selection remaining label
		constraints = new GridBagConstraints();
		constraints.gridx = 0;
		constraints.gridy = 5;
		constraints.fill = GridBagConstraints.BOTH;
		constraints.anchor = GridBagConstraints.WEST;
		constraints.weighty = 0.01;
		constraints.insets = new Insets(0, 4, 4, 0);
		contentPane.add(selectionRemainingLabel, constraints);

		// Add selection remaining field
		constraints = new GridBagConstraints();
		constraints.gridx = 1;
		constraints.gridy = 5;
		constraints.fill = GridBagConstraints.BOTH;
		constraints.anchor = GridBagConstraints.WEST;
		constraints.weightx = 1.0;
		constraints.weighty = 0.01;
		constraints.insets = new Insets(0, 4, 4, 0);
		contentPane.add(mPoolText, constraints);

		// Add 'close' button
		constraints = new GridBagConstraints();
		constraints.gridx = 2;
		constraints.gridy = 5;
		constraints.anchor = GridBagConstraints.EAST;
		constraints.weighty = 0.01;
		constraints.insets = new Insets(0, 4, 4, 4);
		contentPane.add(mCloseButton, constraints);
	}

	/**
	 * Parses a tab-delimited string into an array of Strings
	 *
	 * @param string the delimited string
	 * @return the embedded strings
	 * @author Matthew Woodard
	 */
	private String[] parseString(String string)
	{
		StringTokenizer tokenizer = new StringTokenizer(string, "\t");

		String[] results = new String[tokenizer.countTokens()];
		for (int s = 0; tokenizer.hasMoreTokens(); s++)
		{
			results[s] = tokenizer.nextToken();
		}
		return results;
	}

	/**
	 * Removes a selected item - invoked when the remove button is pressed
	 *
	 * @author   Matt Woodard
	 */
	private void removeSelected()
	{
		TableModel tableModel = mSelectedTable.getModel();

		setMessageText(" ");

		if (mSelectedTable.getSelectedRowCount() == 0)
		{
			setMessageText("No valid item selected.");
			return;
		}

		if (mSelectedTable.getSelectedRowCount() > 1)
		{
			setMessageText("You can only de-select one item at a time.");
			return;
		}

		int selectedRow = mSelectedTable.getSelectedRow();

		int adjustment = 1;
		if (mCostColumnNumber >= 0 && mCostColumnNumber < mSelectedTable.getColumnCount())
		{
			adjustment = Integer.parseInt(tableModel.getValueAt(selectedRow, mCostColumnNumber).toString());
		}
		mPool += adjustment;
		mPoolText.setText(String.valueOf(mPool));

		mSelectedList.remove(selectedRow);

		updateSelectedTable();
		updateButtonStates();
	}

	/**
	 * Selects an available item - invoked when the add button is pressed
	 *
	 * @author   Matt Woodard
	 */
	private void selectAvailable()
	{
		setMessageText(" ");
		if (mPool <= 0)
		{
			setMessageText("You have no selections remaining.");
			return;
		}

		int selectedRow = mAvailableTable.getSelectedRow();

		if (selectedRow < 0)
		{
			setMessageText("No valid item selected.");
			return;
		}

		if (mAvailableTable.getSelectedRowCount() > 1)
		{
			setMessageText("You can only add items one at a time.");
			return;
		}

		TableModel availableModel = mAvailableTable.getModel();

		String addString = availableModel.getValueAt(selectedRow, 0).toString();
		if (mUniqueList.contains(addString))
		{
			setMessageText("You can only select " + addString + " once.");
			return;
		}

		TableModel selectedModel = mSelectedTable.getModel();

		for (int i = 0, count = selectedModel.getRowCount(); i < count; i++)
		{
			String aString = selectedModel.getValueAt(i, 0).toString();
			if (addString.equals(aString) && mAllowDuplicates == false)
			{
				setMessageText(addString + " is already selected.");
				return;
			}
		}

		StringBuffer buffer = new StringBuffer();

		int selectedColumns = selectedModel.getColumnCount();

		if (selectedColumns > 0)
		{
			buffer.append(availableModel.getValueAt(selectedRow, 0));
			for (int i = 1; i < selectedColumns; i++)
			{
				buffer.append('\t');
				buffer.append(availableModel.getValueAt(selectedRow, i));
			}
		}
		mSelectedList.add(buffer.toString());

		updateSelectedTable();

		int adjustment = 1;
		if (mCostColumnNumber >= 0 && mCostColumnNumber < selectedColumns)
		{
			adjustment = Integer.parseInt(availableModel.getValueAt(selectedRow, mCostColumnNumber).toString());
		}
		mPool -= adjustment;
		mPoolText.setText(String.valueOf(mPool));

		updateButtonStates();
	}

	/**
	 * Sets the AllowsDups attribute of the Chooser object
	 *
	 * @param aBool  The new AllowsDups value
	 * @author       Matt Woodard
	 */
	public void setAllowsDups(boolean aBool)
	{
		mAllowDuplicates = aBool;
	}

	/**
	 * Sets the available column name list
	 *
	 * @param availableColumnNames  The new AvailableColumnNames value
	 * @author                      Matt Woodard
	 */
	public void setAvailableColumnNames(List availableColumnNames)
	{
		mAvailableColumnNames = availableColumnNames;

		mAvailableModel.setColumnsNames((availableColumnNames == null) ? STRING_ARRAY :
			(String[])availableColumnNames.toArray(STRING_ARRAY));
	}

	/**
	 * Sets the AvailableList attribute of the Chooser object
	 *
	 * @param availableList  The new AvailableList value
	 * @author               Matt Woodard
	 */
	public void setAvailableList(List availableList)
	{
		List columnList = null;

		// Check for embedded column names in the list
		if (availableList != null && availableList.size() >= 2)
		{
			// Check for a blank line in the #2 spot
			String line = availableList.get(1).toString();
//			System.err.println("chooser line = "+line+"="+line.length());

			if (line.length() == 0)
			{
				// Make a new copy of the input list to avoid changing original
				availableList = new ArrayList(availableList);

				// Remove the blank line
				availableList.remove(1);

				// Remove the column name line
				line = (String)availableList.remove(0);

				// Extract the column list
				columnList = Arrays.asList(parseString(line));
			}
		}

		// Set the list and (possibly) columns
		setAvailableList(availableList, columnList);
	}

	/**
	 * Sets the AvailableList attribute of the Chooser object
	 *
	 * @param availableList  The new AvailableList value
	 * @param columnNames    The new AvailableList value
	 * @author               Matt Woodard
	 */
	public void setAvailableList(List availableList, List columnNames)
	{
		// Store the item list
		mAvailableList = availableList;

		// Determine if we can avoid setting columns
		if (columnNames != null)
		{
			if (mAvailableColumnNames != null &&
				columnNames.size() == mAvailableColumnNames.size())
			{
				for (int i = 0, length = columnNames.size(); i < length; i++)
				{
					// If a column doesn't match, set the columns and stop checking
					if (!columnNames.get(i).equals(mAvailableColumnNames.get(i)))
					{
						setAvailableColumnNames(columnNames);
						break;
					}
				}
			}
			// Set the columns if something doesn't match
			else
			{
				setAvailableColumnNames(columnNames);
			}
		}
		// Only set the column names if they haven't already defaulted
		else if (mAvailableColumnNames != AVAILABLE_COLUMN_NAMES)
		{
			setAvailableColumnNames(columnNames);
		}
	}

	/**
	 * Sets the CostColumn attribute of the Chooser object
	 *
	 * @param costColumnNumber  The new CostColumnNumber value
	 * @author                  Matt Woodard
	 */
	public void setCostColumnNumber(int costColumnNumber)
	{
		mCostColumnNumber = costColumnNumber;
	}

	/**
	 * Sets the message text
	 *
	 * @param messageText  java.lang.String
	 * @author             Matt Woodard
	 */
	public void setMessageText(String messageText)
	{
		mMessageText.setText(messageText == null ? " " :
			(messageText.trim().length() == 0 ? " " : messageText));
	}

	/**
	 * Sets the mPool attribute of the Chooser object
	 *
	 * @param anInt  The new mPool value
	 * @author       Matt Woodard
	 */
	public void setPool(int anInt)
	{
		mPool = anInt;
		mPoolText.setText(String.valueOf(mPool));
	}

	/**
	 * Returns the mPool attribute of the Chooser object.
	 * @author Dmitry Jemerov
	 */

	public int getPool()
	{
		return mPool;
	}

	/**
	 * Sets the mPoolFlag attribute of the Chooser object
	 *
	 * @param poolFlag  The new PoolFlag value
	 * @author          Matt Woodard
	 */
	public void setPoolFlag(boolean poolFlag)
	{
		mPoolFlag = poolFlag;
	}

	/**
	 * Sets the selected column name list
	 * @param selectedColumnNames  java.util.List
	 * @author                     Matt Woodard
	 */
	public void setSelectedColumnNames(List selectedColumnNames)
	{
		mSelectedColumnNames = selectedColumnNames;

		mSelectedModel.setColumnsNames((selectedColumnNames == null) ? STRING_ARRAY :
			(String[])selectedColumnNames.toArray(STRING_ARRAY));
	}

	/**
	 * Sets the SelectedList attribute of the Chooser object
	 *
	 * @param selectedList  The new SelectedList value
	 * @author              Matt Woodard
	 */
	public void setSelectedList(List selectedList)
	{
		setSelectedList(selectedList, null);
	}

	/**
	 * Sets the SelectedList attribute of the Chooser object
	 *
	 * @param selectedList  The new SelectedList value
	 * @param columnNames   The new SelectedList value
	 * @author              Matt Woodard
	 */
	public void setSelectedList(List selectedList, List columnNames)
	{
		// Store the selected items
		mSelectedList = selectedList;

		// Determine if we can avoid setting columns
		if (columnNames != null)
		{
			if (mSelectedColumnNames != null &&
				columnNames.size() == mSelectedColumnNames.size())
			{
				for (int i = 0, length = columnNames.size(); i < length; i++)
				{
					// If a column doesn't match, set the columns and stop checking
					if (!columnNames.get(i).equals(mSelectedColumnNames.get(i)))
					{
						setSelectedColumnNames(columnNames);
						break;
					}
				}
			}
			// Set the columns if something doesn't match
			else
			{
				setSelectedColumnNames(columnNames);
			}
		}
		// Only set the column names if they haven't already defaulted
		else if (mSelectedColumnNames != SELECTED_COLUMN_NAMES)
		{
			setSelectedColumnNames(columnNames);
		}
	}

	/**
	 * Sets the UniqueList attribute of the Chooser object
	 *
	 * @param uniqueList  The new UniqueList value
	 * @author            Matt Woodard
	 */
	public void setUniqueList(List uniqueList)
	{
		mUniqueList = uniqueList;
	}

	/**
	 * Overrides the default show method to ensure controls
	 * are updated before showing the dialog.
	 *
	 * @author   Matt Woodard
	 */
	public void show()
	{
		updateAvailableTable();
		updateSelectedTable();
		updateButtonStates();

		Window owner = getOwner();
		Rectangle ownerBounds = owner.getBounds(),
			bounds = getBounds();

		int width = (int)bounds.getWidth(),
			height = (int)bounds.getHeight();

		setBounds((int)(owner.getX() + (ownerBounds.getWidth() - width) / 2),
			(int)(owner.getY() + (ownerBounds.getHeight() - height) / 2),
			width, height);

		super.show();
	}

	/**
	 * Updates the available table entries
	 *
	 * @author   Matt Woodard
	 */
	private void updateAvailableTable()
	{
		// If the columns haven't been initialized, do so now using the default
		if (mAvailableColumnNames == null)
		{
			setAvailableColumnNames(AVAILABLE_COLUMN_NAMES);
		}

		String selectedValue = null;

		// Look for a previously selected value only if previous data existed
		if (mAvailableData != null && mAvailableData.length > 0)
		{
			int selected = mAvailableTable.getSelectedRow();
			if (selected >= 0)
			{
				selectedValue = mAvailableData[selected][0].toString();
			}
		}

		ListSelectionModel listSelectionModel = mAvailableTable.getSelectionModel();
		listSelectionModel.clearSelection();

		mAvailableData = new Object[mAvailableList.size()][];

		int row = 0;
		for (Iterator it = mAvailableList.iterator(); it.hasNext();)
		{
			mAvailableData[row++] = parseString(it.next().toString());
		}

		mAvailableModel.setData(mAvailableData);

		// Reselect any previously selected item
		if (selectedValue != null)
		{
			for (int i = 0, length = mAvailableModel.getRowCount(); i < length; i++)
			{
				String string = mAvailableModel.getValueAt(i, 0).toString();
				if (selectedValue.equals(string))
				{
					listSelectionModel.setSelectionInterval(i, i);
					break;
				}
			}
		}
	}

	/**
	 * Makes a number of checks to determine when to enable buttons
	 *
	 * @author   Matt Woodard
	 */
	private void updateButtonStates()
	{
		boolean addEnabled = false, removeEnabled = false, closeEnabled = false;

		String addToolTip, removeToolTip, closeToolTip;

		if (mPool > 0)
		{
			if (!mPoolFlag)
			{
				closeEnabled = true;
				closeToolTip = "Press to accept selections.";
			}
			else
			{
				closeToolTip = "You must complete choices.";
			}

			int count = mAvailableTable.getSelectedRowCount();
			if (count == 1)
			{
				int availableRow = mAvailableTable.getSelectedRow();
				if (availableRow >= 0 && availableRow < mAvailableTable.getRowCount())
				{
					TableModel tableModel = mAvailableTable.getModel();

					String addString = tableModel.getValueAt(availableRow, 0).toString();
					if (!mUniqueList.contains(addString))
					{
						addEnabled = true;
						addToolTip = "Press to add " + addString;
						for (int i = 0, length = mSelectedTable.getRowCount(); i < length; i++)
						{
							String string = mSelectedModel.getValueAt(i, 0).toString();
							if (addString.equals(string) && mAllowDuplicates == false)
							{
								addEnabled = false;
								addToolTip = addString + " is already selected.";
							}
						}
					}
					else
					{
						addToolTip = "You can only select " + addString + " once.";
					}
				}
				else
				{
					addToolTip = "No valid item is selected.";
				}
			}
			else if (count == 0)
			{
				addToolTip = "No valid item is selected.";
			}
			else
			{
				addToolTip = "You can only add one item at a time.";
			}
		}
		else
		{
			addToolTip = "You have no selections remaining.";
			closeToolTip = "Press to accept selections.";
			closeEnabled = true;
		}

		int count = mSelectedTable.getSelectedRowCount();
		if (count == 1)
		{
			int selectedRow = mSelectedTable.getSelectedRow();
			if (selectedRow >= 0 && selectedRow < mSelectedTable.getRowCount())
			{
				removeEnabled = true;
				removeToolTip = "Press to remove " + mSelectedTable.getModel().getValueAt(selectedRow, 0) + ".";
			}
			else
			{
				removeToolTip = "No valid item is selected.";
			}
		}
		else if (count == 0)
		{
			removeToolTip = "No valid item is selected.";
		}
		else
		{
			removeToolTip = "You can only remove one item at a time.";
		}

		mAddButton.setEnabled(addEnabled);
		mCloseButton.setEnabled(closeEnabled);
		mRemoveButton.setEnabled(removeEnabled);

		mAddButton.setToolTipText(addToolTip);
		mCloseButton.setToolTipText(closeToolTip);
		mRemoveButton.setToolTipText(removeToolTip);
	}

	/**
	 * Updates the selected table
	 *
	 * @author   Matt Woodard
	 */
	private void updateSelectedTable()
	{
		String selectedValue = null;

		// Look for a previously selected value only if previous data existed
		if (mSelectedData != null && mSelectedData.length > 0)
		{
			int selected = mSelectedTable.getSelectedRow();
			if (selected >= 0)
			{
				selectedValue = mSelectedData[selected][0].toString();
			}
		}

		// Clear the selection prior to updating the data
		ListSelectionModel listSelectionModel = mSelectedTable.getSelectionModel();
		listSelectionModel.clearSelection();

		// If the columns haven't been initialized, set the names to the default
		if (mSelectedColumnNames == null)
		{
			// If the available columns aren't default columns, use the same here
			setSelectedColumnNames(mAvailableColumnNames == AVAILABLE_COLUMN_NAMES ?
				SELECTED_COLUMN_NAMES : mAvailableColumnNames);
		}

		// Construct the data structure
		mSelectedData = new Object[mSelectedList.size()][];

		int row = 0;
		for (Iterator it = mSelectedList.iterator(); it.hasNext();)
		{
			mSelectedData[row++] = parseString(it.next().toString());
		}

		// Populate the selected table's model
		mSelectedModel.setData(mSelectedData);

		// Reselect any previously selected item
		if (selectedValue != null)
		{
			for (int i = 0, length = mSelectedModel.getRowCount(); i < length; i++)
			{
				String string = mSelectedModel.getValueAt(i, 0).toString();
				if (selectedValue.equals(string))
				{
					listSelectionModel.setSelectionInterval(i, i);
					break;
				}
			}
		}
	}
}
