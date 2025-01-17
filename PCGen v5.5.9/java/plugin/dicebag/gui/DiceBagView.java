/*
 *  gmgen.plugin.dicebag - DESCRIPTION OF PACKAGE
 *  Copyright (C) 2003 RossLodge
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 *
 *  The author of this program grants you the ability to use this code
 *  in conjunction with code that is covered under the Open Gaming License
 *
 *  DiceBagView.java
 *
 *  Created on Oct 17, 2003, 2:54:09 PM
 */
package plugin.dicebag.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;
import java.util.Observable;
import java.util.Observer;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.AbstractTableModel;

/**
 * <p>The internal frame view class for the DiceBag.</p>
 *
 * @author Ross M. Lodge
 */
public class DiceBagView extends JInternalFrame implements Observer {
	/**
	 * <p>An action listener for the buttons in the GUI.</p>
	 *
	 * @author Ross M. Lodge
	 */
	private class BagListener implements ActionListener {

		/* (non-Javadoc)
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed(ActionEvent e) {
			String command = e.getActionCommand();
			if ("ROLL".equals(command)) {
				setRollResult(m_exprField.getText(), m_bag.rollDie(m_exprField.getText()));
			}
			else if ("EDIT".equals(command)) {
				handleEdit(e);
			}
			else if ("STOP_EDITING".equals(command)) {
				stopEditing(e);
			}
			else if ("DELETE".equals(command)) {
				deleteRecords(e);
			}
			else if ("MOVE_UP".equals(command)) {
				moveRecordsUp(e);
			}
			else if ("MOVE_DOWN".equals(command)) {
				moveRecordsDown(e);
			}
			else {
				try {
					int index = Integer.parseInt(command);
					setRollResult(m_bag.getDie(index), m_bag.rollDie(index));
				}
				catch (NumberFormatException ex) {
					System.err.println("Invalid command passed to BagListener.");
					ex.printStackTrace();
				}
			}
		}
	}

	/**
	 *
	 * <p>Table model for displaying/editing the dice bag information.
	 * Basically this overrides enough of <code>AbstracTableModel</code>
	 * to make the code useful.</p>
	 *
	 * @author Ross M. Lodge
	 *
	 */
	private class BagTableModel extends AbstractTableModel {

		/* (non-Javadoc)
		 * @see javax.swing.table.TableModel#getRowCount()
		 *
		 * We add one more row than the number of dice in the bag;
		 * users can type in that row to add a new die.
		 */
		public int getRowCount() {
			return m_bag.diceCount() + 1;
		}

		/* (non-Javadoc)
		 * @see javax.swing.table.TableModel#getColumnCount()
		 *
		 * Always a single column
		 */
		public int getColumnCount() {
			return 1;
		}

		/* (non-Javadoc)
		 * @see javax.swing.table.TableModel#getValueAt(int, int)
		 */
		public Object getValueAt(int rowIndex, int columnIndex) {
			String returnValue = null;
			if (rowIndex >= 0 && rowIndex < getRowCount() - 1) {
				returnValue = m_bag.getDie(rowIndex);
			}
			return returnValue;
		}

		/* (non-Javadoc)
		 * @see javax.swing.table.TableModel#getColumnClass(int)
		 */
		public Class getColumnClass(int columnIndex) {
			return String.class;
		}

		/* (non-Javadoc)
		 * @see javax.swing.table.TableModel#getColumnName(int)
		 */
		public String getColumnName(int column) {
			return "Dice Expression";
		}

		/* (non-Javadoc)
		 * @see javax.swing.table.TableModel#isCellEditable(int, int)
		 */
		public boolean isCellEditable(int rowIndex, int columnIndex) {
			return true;
		}

		/* (non-Javadoc)
		 * @see javax.swing.table.TableModel#setValueAt(java.lang.Object, int, int)
		 *
		 * Sets the value at the specified index, or adds a new die at the end.
		 */
		public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
			if (rowIndex >= 0 && rowIndex < getRowCount() - 1) {
				if (aValue == null || aValue.toString().length() == 0) {
					m_bag.removeDie(rowIndex);
					fireTableRowsDeleted(rowIndex,rowIndex);
				}
				else {
					m_bag.setDie(rowIndex, aValue.toString());
				}
			}
			else if (rowIndex == getRowCount() - 1) {
				m_bag.addDie(aValue.toString());
				fireTableRowsInserted(rowIndex,rowIndex);
			}
		}

	}

	/** Label for name of dice bag */
	private JLabel m_nameFieldLabel;
	/** Text field for editing name of dice bag */
	private JTextField m_nameField;
	/** Button to move selected records down */
	private JButton m_moveDown;
	/** Button to move selected records up */
	private JButton m_moveUp;
	/** Button to delete selected records */
	private JButton m_deleteSelected;
	/** Button to stop editing and return to rolling mode */
	private JButton m_stopEditing;
	/** Scroll pane for editing table */
	private JScrollPane m_scrollPane;
	/** Label for expression editing field. */
	private JLabel m_exprFieldLabel;
	/** List for holding dice rolling buttons */
	private LinkedList m_diceButtons;
	/** Switches to editing mode */
	private JButton m_editButton;
	/** Rolls the expression in the expression field */
	private JButton m_rollButton;
	/** Result of the current roll. */
	private JLabel m_exprResult;
	/** Field for editing dice expressions. */
	private JTextField m_exprField;
	/** Listener for all buttons */
	private BagListener m_bagListener = new BagListener();
	/** Table model for editing table */
	private BagTableModel m_tableModel = new BagTableModel();
	/** Editing table */
	private JTable m_table;

	/** Bag model for this bag view */
	DiceBagModel m_bag = null;

	/** Jpanel for top (<code>BorderLayout.NORTH</code>) region */
	private JPanel m_top;
	/** JPanel for top of m_top. */
	private JPanel m_topTop;
	/** JPanel for bottom of m_top. */
	private JPanel m_topBottom;
	/** JPanel for <code>BoxLayout.CENTER</code> of content pane */
	private JPanel m_center;

	/**
	 * <p>Constructs the view; initializes the components.</p>
	 *
	 * @param bag The DiceBagModel for this view
	 */
	public DiceBagView(DiceBagModel bag) {
		m_bag = bag;
		initComponents();
	}

	/**
	 * <p>Initializes the view and all components, and starts the bag in
	 * editing mode.  It sets the size (<code>pack()</code>) of the
	 * internal frame, but does <b>not</b> show the frame.</p>
	 *
	 *
	 */
	private void initComponents() {
		/*
		 *     /////////////////////////////////
		 *     // Expr [             ] [roll] //
		 *     // |RESULT            | [edit] //
		 *     /////////////////////////////////
		 *     //  [ expr ] [ expr ] [ expr ] //
		 *     //  [ expr ] [ expr ] [ expr ] //
		 *     //  [ expr ] [ expr ] [ expr ] //
		 *     //  [ expr ] [ expr ] [ expr ] //
		 *     //  [ expr ] [ expr ] [ expr ] //
		 *     //  [ expr ] [ expr ] [ expr ] //
		 *     /////////////////////////////////
		 */
		// Set basic properties
		setTitle(m_bag.getName());
		setResizable(true);
		setClosable(true);
		setMaximizable(true);
		setIconifiable(true);
		// Assure main panes content borderlayout
		getContentPane().setLayout(new BorderLayout());
		//Create all JPanels
		m_top = new JPanel();
		m_topTop = new JPanel();
		m_topBottom = new JPanel();
		m_center = new JPanel();
		//Set Layout managers for all panels
		m_top.setLayout(new GridLayout(2,1));
		m_topTop.setLayout(new BoxLayout(m_topTop,BoxLayout.X_AXIS));
		m_topBottom.setLayout(new BoxLayout(m_topBottom,BoxLayout.X_AXIS));
		m_center.setLayout(new DiceBagGridLayout(0,3,DiceBagGridLayout.MANAGE_BY_COLUMNS,75,100));
		m_center.setBorder(new EmptyBorder(5,5,5,5));
		//Rolling mode controls
		m_rollButton = new JButton("Roll");
		m_rollButton.setActionCommand("ROLL");
		m_rollButton.addActionListener(m_bagListener);
		m_exprResult = new JLabel(" ");
		m_exprResult.setMinimumSize(new Dimension(50,m_exprResult.getMinimumSize().height));
		m_exprResult.setPreferredSize(new Dimension(50,m_exprResult.getPreferredSize().height));
		m_exprResult.setMaximumSize(new Dimension(Integer.MAX_VALUE,m_exprResult.getMaximumSize().height));
		m_editButton = new JButton("Edit");
		m_editButton.setActionCommand("EDIT");
		m_editButton.addActionListener(m_bagListener);
		m_diceButtons = new LinkedList();
		m_exprField = new JTextField();
		m_exprFieldLabel = new JLabel("Roll Expr: ");
		//Components for editing mode
		m_table = new JTable(m_tableModel);
		m_scrollPane = new JScrollPane(m_table);
		/*
		 * stop editing button
		 * delete selected button
		 * move up button
		 * move down button
		 */
		m_stopEditing = new JButton("Stop Editing");
		m_stopEditing.setActionCommand("STOP_EDITING");
		m_stopEditing.addActionListener(m_bagListener);
		m_deleteSelected = new JButton("Delete");
		m_deleteSelected.setActionCommand("DELETE");
		m_deleteSelected.addActionListener(m_bagListener);
		m_moveUp = new JButton("Move up");
		m_moveUp.setActionCommand("MOVE_UP");
		m_moveUp.addActionListener(m_bagListener);
		m_moveDown = new JButton("Move down");
		m_moveDown.setActionCommand("MOVE_DOWN");
		m_moveDown.addActionListener(m_bagListener);
		m_nameField = new JTextField();
		m_nameFieldLabel = new JLabel("Name: ");
		m_nameField.getDocument().addDocumentListener(new DocumentListener() {
			public void insertUpdate(DocumentEvent e) {
				m_bag.setName(m_nameField.getText());
				setTitle(m_nameField.getText());
			}

			public void removeUpdate(DocumentEvent e) {
				m_bag.setName(m_nameField.getText());
				setTitle(m_nameField.getText());
			}

			public void changedUpdate(DocumentEvent e) {
			}
		});
		Toolkit kit = Toolkit.getDefaultToolkit();
		Image img = kit.getImage(getClass().getResource("/pcgen/gui/resource/gmgen_icon.png"));
		setFrameIcon(new ImageIcon(img));
		//Start in rolling mode
		setupRollingMode();
		//Size and position the window
		pack();
	}

	/**
	 * <p>Sets up the rolling mode; first cleans out all components
	 * and then displays the components for the rolling mode.</p>
	 *
	 *
	 */
	private void setupRollingMode() {
		cleanup();
		if (m_center.getLayout() instanceof DiceBagGridLayout) {
			((DiceBagGridLayout)m_center.getLayout()).setRows(0);
			((DiceBagGridLayout)m_center.getLayout()).setColumns(3);
		}
		//Add main JPanels to content pane
		getContentPane().add(m_top,BorderLayout.NORTH);
		getContentPane().add(m_center,BorderLayout.CENTER);
		//Add secondary panels to top panel
		m_top.add(m_topTop);
		m_top.add(m_topBottom);
		//Add components to topTop panel
		m_topTop.add(m_exprFieldLabel);
		m_exprField.setPreferredSize(new Dimension(50,m_exprField.getPreferredSize().height));
		m_topTop.add(m_exprField);
		m_topTop.add(Box.createHorizontalStrut(10));
		m_topTop.add(m_rollButton);
		//Add components to topBottom panel
		m_topBottom.add(m_exprResult);
		m_topBottom.add(Box.createHorizontalStrut(10));
		m_topBottom.add(m_editButton);
		//Add components to the center panel
		for (int i = 0; i < m_bag.diceCount(); i++) {
			JButton button = null;
			if (m_diceButtons.size() > i) {
				button = (JButton)m_diceButtons.get(i);
			}
			else {
				button = new JButton(" ");
				button.setPreferredSize(new Dimension(100,button.getPreferredSize().height));
				button.addActionListener(m_bagListener);
				button.setActionCommand(Integer.toString(i));
				m_diceButtons.add(button);
			}
			button.setText(m_bag.getDie(i));
			m_center.add(button);
		}
		getContentPane().validate();
		pack();
	}

	/* (non-Javadoc)
	 * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
	 *
	 * Does nothing
	 */
	public void update(Observable o, Object arg) {
	}

	/**
	 * <p>Returns this view's bag model.</p>
	 *
	 * @return The model for this view.
	 */
	public DiceBagModel getBag() {
		return m_bag;
	}

	/**
	 *
	 * <p>Handles the press of the editing button; calls
	 * <code>setupEditMode()</code>.</p>
	 *
	 * @param e Event that fired handler.
	 */
	public void handleEdit(ActionEvent e) {
		setupEditMode();
	}

	/**
	 * <p>Sets up the editing mode; cleans components and then
	 * displays the editing component setup.</p>
	 */
	private void setupEditMode() {
		cleanup();
		getContentPane().add(m_center,BorderLayout.CENTER);
		getContentPane().add(m_top,BorderLayout.NORTH);
		m_top.add(m_topTop);
		m_top.add(m_topBottom);
		m_nameField.setPreferredSize(new Dimension(100,m_nameField.getPreferredSize().height));
		m_topBottom.add(m_nameFieldLabel);
		m_topBottom.add(m_nameField);
		m_nameField.setText(m_bag.getName());
		if (m_center.getLayout() instanceof DiceBagGridLayout) {
			((DiceBagGridLayout)m_center.getLayout()).setRows(1);
			((DiceBagGridLayout)m_center.getLayout()).setColumns(1);
		}
		m_center.add(m_scrollPane);
		m_topTop.add(m_stopEditing);
		m_topTop.add(Box.createHorizontalStrut(5));
		m_topTop.add(m_deleteSelected);
		m_topTop.add(Box.createHorizontalStrut(5));
		m_topTop.add(m_moveUp);
		m_topTop.add(Box.createHorizontalStrut(5));
		m_topTop.add(m_moveDown);
		m_table.setPreferredScrollableViewportSize(new Dimension(m_table.getPreferredScrollableViewportSize().width,200));
		getContentPane().validate();
		pack();
	}

	/**
	 * <p>Removes all components from the various containers.  It does not
	 * dereference the components, so they can be re-used.</p>
	 */
	private void cleanup() {
		m_top.removeAll();
		m_topTop.removeAll();
		m_topBottom.removeAll();
		m_center.removeAll();
		getContentPane().removeAll();
	}

	/**
	 * <p>The handler for the move down button.  Moves the selected
	 * records down one step.</p>
	 *
	 * @param e The event that fired this handler.
	 */
	private void moveRecordsDown(ActionEvent e) {
		for (int row = m_table.getRowCount() - 2; row >= 0; row--) {
			if (row < m_table.getRowCount() - 2 && m_table.isRowSelected(row) && !m_table.isRowSelected(row + 1)) {
				final String die1 = m_bag.getDie(row);
				final String die2 = m_bag.getDie(row + 1);
				m_bag.setDie(row + 1, die1);
				m_bag.setDie(row, die2);
				m_tableModel.fireTableRowsUpdated(row, row + 1);
				m_table.getSelectionModel().addSelectionInterval(row + 1,row + 1);
				m_table.getSelectionModel().removeSelectionInterval(row,row);
			}
		}
	}

	/**
	 * <p>The handler for the move up button.  Moves the selected
	 * records up one step.</p>
	 *
	 * @param e The event that fired this handler.
	 */
	private void moveRecordsUp(ActionEvent e) {
		for (int row = 0; row < m_table.getRowCount(); row++) {
			if (row > 0 && m_table.isRowSelected(row) && !m_table.isRowSelected(row - 1)) {
				final String die1 = m_bag.getDie(row);
				final String die2 = m_bag.getDie(row - 1);
				m_bag.setDie(row - 1, die1);
				m_bag.setDie(row, die2);
				m_tableModel.fireTableRowsUpdated(row - 1, row);
				m_table.getSelectionModel().addSelectionInterval(row - 1,row - 1);
				m_table.getSelectionModel().removeSelectionInterval(row,row);
			}
		}
	}

	/**
	 * <p>The handler for deleting records; deletes the selected
	 * dice from the bag.</p>
	 *
	 * @param e The event that fired this handler.
	 */
	private void deleteRecords(ActionEvent e) {
		while (m_table.getSelectedRow() >= 0 && m_table.getSelectedRow() < m_bag.diceCount()) {
			m_bag.removeDie(m_table.getSelectedRow());
			m_tableModel.fireTableRowsDeleted(m_table.getSelectedRow(),m_table.getSelectedRow());
		}
	}

	/**
	 * <p>Stops editing and goes back to rolling mode.</p>
	 *
	 * @param e The event that fired this handler.
	 */
	private void stopEditing(ActionEvent e) {
		setupRollingMode();
	}
	
	/**
	 * 
	 * <p>Sets the result display.</p>
	 * 
	 * @param diceRoll The die string utilized
	 * @param result The double value to display
	 */
	private void setRollResult(String diceRoll, double result) {
		String resultString = null;
		if (result % 1 == 0)
		{
			resultString = Integer.toString((int)Math.round(result));
		}
		else
		{
			resultString = Double.toString(result);
		}
		m_exprResult.setText("<html><b>" + diceRoll + ": " + resultString + "</b></html>");
	}
}
