package net.suberic.pooka.gui;
import net.suberic.util.gui.IconManager;
import net.suberic.pooka.*;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.net.URL;

import java.util.*;

/**
 * This shows a dialog which allows you to select addresses from an
 * AddressBook.
 */
public class AddressBookSelectionPanel extends JPanel {

  // the list of addresses available
  JTable addressTable;
  
  // the list of addresses selected
  JTable confirmedTable;

  // the filter entry
  JTextField filterField;

  // the AddressEntryTextArea that we're using.
  AddressEntryTextArea entryArea;

  // the parent frame.
  Component parentFrame;

  /**
   * Creates a new AddressBookSelectionPanel using the given 
   * AddressEntryTextArea.
   */
  public AddressBookSelectionPanel(AddressEntryTextArea entryTextArea, Component newParentFrame) {
    entryArea = entryTextArea;
    parentFrame = newParentFrame;
    configurePanel();
  }

  /**
   * Sets up the panel.  The entryArea should be set already.
   */
  void configurePanel() {
    this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

    this.add(createFilterPanel());

    Box addressBox = new Box(BoxLayout.X_AXIS);

    createAddressTable();

    JScrollPane addressPane = new JScrollPane(addressTable);
    addressPane.setPreferredSize(new java.awt.Dimension(300,200));
    addressBox.add(addressPane);

    JPanel selectionPanel = createSelectionPanel();
    addressBox.add(selectionPanel);

    createConfirmedTable();

    JScrollPane confirmedPane = new JScrollPane(confirmedTable);
    confirmedPane.setPreferredSize(new java.awt.Dimension(300,200));
    addressBox.add(confirmedPane);

    this.add(addressBox);

    Box buttonBox = createButtonBox();

    this.add(buttonBox);
  }

  /**
   * Creates the filter panel.
   */
  JPanel createFilterPanel() {
    JPanel returnValue = new JPanel();
    filterField = new JTextField(20);
    JButton filterButton = new JButton(Pooka.getProperty("AddressBookEditor.title.Search", "Search"));
    filterButton.addActionListener(new AbstractAction() {
	public void actionPerformed(ActionEvent e) {
	  doFilter(filterField.getText());
	}
      });

    returnValue.add(filterField);
    returnValue.add(filterButton);
    return returnValue;
  }

  /**
   * Creates the address table.
   */
  void createAddressTable() {
    AddressBookTableModel addressModel = new AddressBookTableModel();
    addressTable = new JTable(addressModel);
    addressTable.getColumnModel().getColumn(0).setPreferredWidth(100);
    addressTable.getColumnModel().getColumn(1).setPreferredWidth(100);
    addressTable.addMouseListener(new java.awt.event.MouseAdapter() {
	public void mouseClicked(java.awt.event.MouseEvent e) {
	  if (e.getClickCount() == 2) {
	    int rowIndex = addressTable.rowAtPoint(e.getPoint());
	    if (rowIndex != -1) {
	      addressTable.setRowSelectionInterval(rowIndex, rowIndex);
	      confirmSelectedAddresses();
	    }
	  }
	}
      });

    doFilter("");
  }

  /**
   * Creates the confirmed table.
   */
  void createConfirmedTable() {
    AddressBookTableModel confirmedModel = new AddressBookTableModel();
    confirmedTable = new JTable(confirmedModel);
    confirmedTable.getColumnModel().getColumn(0).setPreferredWidth(100);
    confirmedTable.getColumnModel().getColumn(1).setPreferredWidth(100);
  }

  /**
   * Creates the Selection Panel.
   */
  JPanel createSelectionPanel() {
    JPanel returnValue = new JPanel();
    returnValue.setLayout(new BoxLayout(returnValue, BoxLayout.Y_AXIS));

    returnValue.add(Box.createVerticalGlue());

    IconManager iconManager = Pooka.getUIFactory().getIconManager();
    
    ImageIcon addButtonIcon = iconManager.getIcon(Pooka.getProperty("NewMessage.selectionPanel.button.add", "Right"));

    JButton addButton = new JButton(addButtonIcon);
    addButton.addActionListener(new AbstractAction() {
	public void actionPerformed(ActionEvent e) {
	  confirmSelectedAddresses();
	}
      });
    returnValue.add(addButton);

    returnValue.add(Box.createVerticalGlue());

    ImageIcon removeButtonIcon = iconManager.getIcon(Pooka.getProperty("NewMessage.selectionPanel.button.remove", "Left"));

    JButton removeButton = new JButton(removeButtonIcon);
    removeButton.addActionListener(new AbstractAction() {
	public void actionPerformed(ActionEvent e) {
	  removeSelectedAddresses();
	}
      });
    returnValue.add(removeButton);

    returnValue.add(Box.createVerticalGlue());

    return returnValue;
  }

  /**
   * Creates the box with the ok and cancel buttons.
   */
  Box createButtonBox() {
    Box returnValue = Box.createHorizontalBox();

    JButton okButton = new JButton(Pooka.getProperty("button.ok", "Ok"));
    okButton.addActionListener(new AbstractAction() {
	public void actionPerformed(ActionEvent e) {
	  copySelectionsToEntry();
	  closePanel();
	}
      });
    returnValue.add(okButton);


    JButton cancelButton = new JButton(Pooka.getProperty("button.cancel", "Cancel"));
    cancelButton.addActionListener(new AbstractAction() {
	public void actionPerformed(ActionEvent e) {
	  closePanel();
	}
      });
    returnValue.add(cancelButton);

    return returnValue;
  }

  /**
   * Updates the addressTable using the results of the filter from the
   * filterField.
   */
  public void doFilter(String filterValue) {
    AddressBookEntry[] matchingValues = getAddressMatcher().match(filterValue);
    AddressBookTableModel model = (AddressBookTableModel) addressTable.getModel();
    model.setEntries(matchingValues);
  }

  /**
   * Gets the currently selected addresses in the addressTable.
   */
  public AddressBookEntry[] getSelectedAddresses() {
    if (addressTable != null) {
      AddressBookTableModel model = (AddressBookTableModel) addressTable.getModel();
      int[] selectedRows = addressTable.getSelectedRows();
      if (selectedRows != null) {
	AddressBookEntry[] returnValue = new AddressBookEntry[selectedRows.length];
	for (int i = 0; i < selectedRows.length; i++) {
	  returnValue[i] = model.getEntryAt(selectedRows[i]);
	}
	return returnValue;
      }
    }
    return new AddressBookEntry[0];
  }

  /**
   * Gets all of the confirmed list of addresses.
   */
  public AddressBookEntry[] getConfirmedAddresses() {
    if (confirmedTable != null) {
      AddressBookTableModel model = (AddressBookTableModel) confirmedTable.getModel();
      AddressBookEntry[] returnValue = new AddressBookEntry[model.getRowCount()];
      for (int i = 0; i < returnValue.length; i++) {
	returnValue[i] = model.getEntryAt(i);
      }
      return returnValue;
    }

    return new AddressBookEntry[0];
  }

  /**
   * Adds the currently selected address(es) in the addressTable to the
   * confirmedTable.
   */
  public void confirmSelectedAddresses() {
    AddressBookEntry[] selectedValues = getSelectedAddresses();
    AddressBookTableModel model = (AddressBookTableModel) confirmedTable.getModel();
    model.addEntries(selectedValues);
  }

  /**
   * Removed the currently selected address(es) in the confirmedTable from the
   * confirmedTable.
   */
  public void removeSelectedAddresses() {
    AddressBookTableModel model = (AddressBookTableModel) confirmedTable.getModel();
    int[] selectedRows = confirmedTable.getSelectedRows();
    AddressBookEntry[] removedEntries = new AddressBookEntry[selectedRows.length];
    for (int i = 0; i < selectedRows.length; i++) {
      removedEntries[i] = model.getEntryAt(selectedRows[i]);
    }
    model.removeEntries(removedEntries);
  }

  /**
   * Copies the entries from the selection list to the AddressEntryTextArea.
   */
  public void copySelectionsToEntry() {
    AddressBookEntry[] confirmedEntries = getConfirmedAddresses();
    entryArea.addAddresses(confirmedEntries);
  }

  /**
   * Closes this panel.
   */
  public void closePanel() {
    if (parentFrame instanceof JInternalFrame) {
      try {
	((JInternalFrame) parentFrame).setClosed(true);
      } catch (java.beans.PropertyVetoException e) {
      }
    } else {
      ((JFrame) parentFrame).dispose();
    }
  }
  
  /**
   * Gets the appropriate AddressMatcher.
   */
  public AddressMatcher getAddressMatcher() {
    return entryArea.getNewMessageUI().getSelectedProfile().getAddressMatcher();
  }

  /**
   * A TableModel for Address Books.
   */
  class AddressBookTableModel extends AbstractTableModel {
    
    List addressList = new ArrayList();

    /**
     * Creates an AddressBookTableModel.
     */
    public AddressBookTableModel() {
      
    }

    /**
     * Returns the row count.
     */
    public int getRowCount() {
      return addressList.size();
    }

    /**
     * Returns the column count.
     */
    public int getColumnCount() {
      return 2;
    }

    
    /**
     * Returns the value at the given row and column.
     */
    public Object getValueAt(int row, int column) {
      if (row < 0 || row >= getRowCount() || column < 0 || column >= 2)
	return null;

      AddressBookEntry entry = getEntryAt(row);
      if (column == 0) {
	return entry.getID();
      } 

      if (column == 1) {
	return entry.getAddressString();
      }

      return null;
    }

    /**
     * Returns the AddressBookEntry for the given row.
     */
    public AddressBookEntry getEntryAt(int row) {
      return (AddressBookEntry) addressList.get(row);
    }

    /**
     * Adds the given AddressBookEntries to the table.
     */
    public void addEntries(AddressBookEntry[] newEntries) {
      // make sure that each entry is unique.
      if (newEntries != null && newEntries.length > 0) {
	int firstNew = addressList.size();
	for (int i = 0 ; i < newEntries.length; i++) {
	  if ( ! addressList.contains(newEntries[i])) {
	    addressList.add(newEntries[i]);
	  }
	}
	
	if (firstNew != addressList.size()) 
	  fireTableRowsInserted(firstNew, addressList.size() -1);
      }
    }

    /**
     * Removes the given AddressBookEntries from the table.
     */
    public void removeEntries(AddressBookEntry[] removedEntries) {
      for (int i = 0; i < removedEntries.length; i++) {
	int index = addressList.indexOf(removedEntries[i]);
	if (index >=0) {
	  addressList.remove(index);
	  fireTableRowsDeleted(index, index);
	}
      }
    }

    /**
     * Sets the given AddressBookEntries as the values for the table.
     */
    public void setEntries(AddressBookEntry[] newEntries) {
      ArrayList newList = new ArrayList();
      for (int i = 0; i < newEntries.length; i++) {
	newList.add(newEntries[i]);
      }
      addressList=newList;
      fireTableDataChanged();
    }

    /**
     * Returns the name for the given column.
     */
    public String getColumnName(int col) {
      if (col == 0)
	return Pooka.getProperty("AddressBookTable.personalName", "Name");
      
      if (col == 1)
	return Pooka.getProperty("AddressBookTable.address", "Email Address");

      return "";
    } 
  }
}
