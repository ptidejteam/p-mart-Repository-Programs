package net.suberic.pooka.gui.propedit;
import net.suberic.pooka.*;
import net.suberic.util.*;
import net.suberic.util.gui.propedit.*;
import net.suberic.pooka.gui.propedit.*;
import net.suberic.util.gui.*;
import javax.swing.*;
import java.awt.event.*;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import javax.swing.Action;

/**
 * A property editor which edits an AddressBook.
 */
public class AddressBookEditorPane extends MultiEditorPane {

  AddressBook book;
  String bookName;
  JTextField searchEntryField;
  JButton searchButton;

  ConfigurablePopupMenu popupMenu;

  /**
   * @param propertyName The property to be edited.
   * @param template The property that will define the layout of the
   *                 editor.
   * @param manager The PropertyEditorManager that will manage the
   *                   changes.
   */
  public void configureEditor(String propertyName, String template, String propertyBaseName, PropertyEditorManager newManager) {
    configureBasic(propertyName, template, propertyBaseName, newManager);

    // we're going to have "AddressBook." at the beginning, so remove that.
    bookName = propertyBase.substring(12, propertyBase.length());
    book = Pooka.getAddressBookManager().getAddressBook(bookName);

    JPanel searchEntryPanel = createSearchEntryPanel();
    createAddressTable();
    buttonPanel = createButtonPanel();

    JPanel addressPanel = new JPanel();
    addressPanel.setLayout(new BoxLayout(addressPanel, BoxLayout.Y_AXIS));

    addressPanel.add(searchEntryPanel);
    JScrollPane addressScrollPane = new JScrollPane(optionTable);
    try {
      addressScrollPane.setPreferredSize(new java.awt.Dimension(Integer.parseInt(manager.getProperty("Pooka.addressBookEditor.hsize", "300")), Integer.parseInt(manager.getProperty("Pooka.addressBookEditor.vsize", "100"))));
    } catch (Exception e) {
      addressScrollPane.setPreferredSize(new java.awt.Dimension(300, 100));
    }
    addressPanel.add(addressScrollPane);

    doEditorPaneLayout(addressPanel, buttonPanel);

    popupMenu = new ConfigurablePopupMenu();
    popupMenu.configureComponent("AddressBookEditor.popupMenu", manager.getFactory().getSourceBundle());
    popupMenu.setActive(getActions());

    updateEditorEnabled();
  }

  /**
   * Creates the panel which has the entry fields -- i.e., "Enter string to
   * match", an entry field, and a search button.
   */
  public JPanel createSearchEntryPanel() {
    JPanel searchEntryPanel = new JPanel();
    searchEntryPanel.add(new JLabel(manager.getProperty("AddressBookEditor.matchString", "Match String: ")));

    searchEntryField = new JTextField(30);
    searchEntryPanel.add(searchEntryField);

    Action a = new SearchAction();

    searchButton = new JButton(manager.getProperty("AddressBookEditor.title.Search", "Search"));
    searchButton.addActionListener(a);
    searchEntryPanel.add(searchButton);

    return searchEntryPanel;
  }

  /**
   * Creates the AddressTable.
   */
  public void createAddressTable() {
    optionTable = new JTable();
    optionTable.setCellSelectionEnabled(false);
    optionTable.setColumnSelectionAllowed(false);
    optionTable.setRowSelectionAllowed(true);

    optionTable.addMouseListener(new MouseAdapter() {
        public void mouseClicked(MouseEvent e) {
          if (e.getClickCount() == 2) {
            int rowIndex = optionTable.rowAtPoint(e.getPoint());
            if (rowIndex != -1) {
              optionTable.setRowSelectionInterval(rowIndex, rowIndex);
              AddressBookEntry selectedEntry = getSelectedEntry();
              if (selectedEntry != null) {
                editEntry(selectedEntry);
              }
            }
          }
        }

        public void mousePressed(MouseEvent e) {
          if (e.isPopupTrigger()) {
            // see if anything is selected
            int rowIndex = optionTable.rowAtPoint(e.getPoint());
            if (rowIndex == -1 || !optionTable.isRowSelected(rowIndex) ) {
              optionTable.setRowSelectionInterval(rowIndex, rowIndex);
            }

            showPopupMenu(optionTable, e);
          }
        }

        public void mouseReleased(MouseEvent e) {
          if (e.isPopupTrigger()) {
            // see if anything is selected
            int rowIndex = optionTable.rowAtPoint(e.getPoint());
            if (rowIndex == -1 || !optionTable.isRowSelected(rowIndex) ) {
              optionTable.setRowSelectionInterval(rowIndex, rowIndex);
            }

            showPopupMenu(optionTable, e);
          }
        }
      });

    updateTableModel(new AddressBookEntry[0]);

  }

  /**
   * Performs a search using the string value in the searchEntryField.  Updates
   * the optionTable with the results.
   */
  public void performSearch() {
    AddressBookEntry[] matchingEntries = book.getAddressMatcher().match(searchEntryField.getText());
    updateTableModel(matchingEntries);
  }

  /**
   * Adds a new entry.
   */
  public void performAdd() {
    AddressBookEntry newEntry = new net.suberic.pooka.vcard.Vcard(new java.util.Properties());
    try {
      newEntry.setAddresses(new javax.mail.internet.InternetAddress[] { new javax.mail.internet.InternetAddress("example@example.com") });
    } catch (Exception e) { }
    if (newEntry.getAddresses() != null) {
      book.addAddress(newEntry);
      ((AddressBookTableModel)optionTable.getModel()).addEntry(newEntry);
    }
    editEntry(newEntry);
  }

  /**
   * Edits the current entry.
   */
  protected void editSelectedValue(Container container) {
    AddressBookEntry e = getSelectedEntry();
    if (e != null) {
      String newValueTemplate = manager.getProperty(editorTemplate + "._addValueTemplate", "");
      if (newValueTemplate.length() > 0) {
        PropertyEditorUI editor = manager.getFactory().createEditor(newValueTemplate, newValueTemplate, manager);
        AddressEntryController aec = null;
        if (editor instanceof WizardEditorPane && ((WizardEditorPane)editor).getController() instanceof AddressEntryController) {
          aec = (AddressEntryController) ((WizardEditorPane) editor).getController();
          aec.setAddressBook(book);
          aec.loadEntry(e);
        }
        manager.getFactory().showNewEditorWindow(manager.getProperty(newValueTemplate + ".label", newValueTemplate), editor, getPropertyEditorPane().getContainer());

        if (aec != null) {
          AddressBookEntry editedEntry = aec.getEntry();
          if (editedEntry == e) {
            ((AddressBookTableModel)optionTable.getModel()).updateEntry(editedEntry);
          } else {
            book.addAddress(editedEntry);
            ((AddressBookTableModel)optionTable.getModel()).addEntry(editedEntry);
          }
        }
      } else {
        editEntry(e);
      }
    }
  }

  /**
   * Deletes the current entry.
   */
  public void removeSelectedValue() {
    AddressBookEntry e = getSelectedEntry();
    if (e != null) {
      book.removeAddress(e);
      ((AddressBookTableModel)optionTable.getModel()).removeEntry(e);
    }
  }

  /**
   * Gets the currently selected entry.
   */
  public AddressBookEntry getSelectedEntry() {
    int index = optionTable.getSelectedRow();
    if (index > -1)
      return ((AddressBookTableModel)optionTable.getModel()).getEntryAt(index);
    else
      return null;
  }

  /**
   * Brings up an editor for the current entry.
   */
  public void editEntry(AddressBookEntry entry) {
    /*
      AddressEntryEditor editor = new AddressEntryEditor(manager, entry);
      manager.getFactory().showNewEditorWindow(manager.getProperty("AddressEntryEditor.title", "Address Entry"), editor);
    */
  }

  /**
   * Brings up the current popup menu.
   */
  public void showPopupMenu(JComponent component, MouseEvent e) {
    popupMenu.show(component, e.getX(), e.getY());
  }

  /**
   * Updates the TableModel with the new entries.
   */
  public void updateTableModel(AddressBookEntry[] entries) {
    AddressBookTableModel newTableModel = new AddressBookTableModel(entries);
    optionTable.setModel(newTableModel);
  }

  public void setValue() {
    if (book != null) {
      try {
        book.saveAddressBook();
      } catch (Exception e) {
        Pooka.getUIFactory().showError(Pooka.getProperty("error.AddressBook.saveAddressBook", "Error saving Address Book:  ") + e.getMessage());
        e.printStackTrace();
      }
    } else {
      // if we're setting the value on this editor, then we might also be
      // creating the edited AddressBook.  see if that's true.
      SwingUtilities.invokeLater(new Runnable() {
          public void run() {
            AddressBook newBook = Pooka.getAddressBookManager().getAddressBook(bookName);
            if (newBook != null) {
              book = newBook;
              updateEditorEnabled();
            }
          }
        });
    }
  }

  public void validateProperty() {

  }

  public java.util.Properties getValue() {
    return new java.util.Properties();
  }

  public void resetDefaultValue() {
    try {
      book.loadAddressBook();
    } catch (Exception e) {
      Pooka.getUIFactory().showError(Pooka.getProperty("error.AddressBook.loadAddressBook", "Error reloading Address Book:  ") + e.getMessage());
      e.printStackTrace();
    }
    performSearch();
  }

  public boolean isChanged() {
    return false;
  }

  /**
   * Run when the PropertyEditor may have changed enabled states.
   */
  protected void updateEditorEnabled() {
    super.updateEditorEnabled();
    searchButton.setEnabled(isEditorEnabled());
    searchEntryField.setEnabled(isEditorEnabled());
  }


  public void setBusy(boolean newValue) {
    if (newValue)
      this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
    else
      this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
  }

  public class AddressBookTableModel extends javax.swing.table.AbstractTableModel {

    AddressBookEntry[] entries;

    public AddressBookTableModel(AddressBookEntry[] newEntries) {
      entries = newEntries;
    }

    public int getRowCount() {
      return entries.length;
    }

    public int getColumnCount() {
      return 4;
    }

    public String getColumnName(int index) {
      if (index == 0) {
        return Pooka.getProperty("AddressBookTable.personalName", "Name");
      } else if (index == 1) {
        return Pooka.getProperty("AddressBookTable.firstName", "First Name");
      } else if (index == 2) {
        return Pooka.getProperty("AddressBookTable.lastName", "Last Name");
      } else if (index == 3) {
        return Pooka.getProperty("AddressBookTable.address", "Email Address");
      } else {
        return null;
      }
    }

    public Object getValueAt(int row, int column) {
      if (row < 0 || column < 0 || row >= getRowCount() || column >= getColumnCount())
        return null;

      AddressBookEntry currentEntry = entries[row];

      if (column == 0) {
        return currentEntry.getID();
      }
      if (column == 1) {
        return currentEntry.getFirstName();
      }
      if (column == 2) {
        return currentEntry.getLastName();
      }
      if (column == 3) {
        return currentEntry.getAddressString();
      }

      return null;
    }

    /**
     * Returns the AddressBookEntry at the given index.
     */
    public AddressBookEntry getEntryAt(int index) {
      return entries[index];
    }

    /**
     * Adds the given AddressBookEntry to the end of the table.
     */
    public void addEntry(AddressBookEntry e) {
      AddressBookEntry[] newEntries;
      int length;

      if (entries != null) {
        length = entries.length;
        newEntries = new AddressBookEntry[length + 1];
        System.arraycopy(entries, 0, newEntries, 1, length);
      } else {
        length = 0;
        newEntries = new AddressBookEntry[1];
      }
      newEntries[0] = e;

      entries = newEntries;

      fireTableRowsInserted(length, length);

      optionTable.clearSelection();
      optionTable.addRowSelectionInterval(0,0);
      optionTable.scrollRectToVisible(optionTable.getCellRect(0, 1, true));
  }

    /**
     * Removes the given AddressBookEntry from the table, if present.
     */
    public void removeEntry(AddressBookEntry e) {
      boolean found = false;

      for (int i = 0; !found && i < entries.length; i++) {
        if (e == entries[i]) {
          found = true;
          int removedRow = i;
          AddressBookEntry[] newEntries = new AddressBookEntry[entries.length - 1];
          if (removedRow != 0)
            System.arraycopy(entries, 0, newEntries, 0, removedRow);

          if (removedRow != entries.length -1)
            System.arraycopy(entries, removedRow + 1, newEntries, removedRow, entries.length - removedRow - 1);

          entries = newEntries;
          fireTableRowsDeleted(removedRow, removedRow);
        }
      }
    }

    /**
     * Updates the given AddressBookEntry in the table.
     */
    public void updateEntry(AddressBookEntry e) {
      boolean found = false;

      for (int i = 0; !found && i < entries.length; i++) {
        if (e == entries[i]) {
          found = true;
          fireTableRowsUpdated(i,i);
        }
      }
    }
  }

  public class SearchAction extends AbstractAction {
    public SearchAction() {
      super("address-search");
    }

    public void actionPerformed(ActionEvent e) {
      setBusy(true);
      performSearch();
      setBusy(false);
    }
  }

  /**
   * Gets the parent PropertyEditorPane for the given component.
   */
  public PropertyEditorPane getPropertyEditorPane() {
    return getPropertyEditorPane(this);
  }

  /**
   * Returns the display value for this property.
   */
  public String getDisplayValue() {
    return bookName;
  }

  /**
   * Creates the actions for this editor.
   */
  protected void createActions() {
    mDefaultActions = new Action[] {
      new SearchAction(),
      new AddAddressAction(),
      new EditAction(),
      new DeleteAction()
    };
  }

  public class AddAddressAction extends AbstractAction {
    public AddAddressAction() {
      //super("address-add");
      super("editor-add");
    }

    public void actionPerformed(ActionEvent e) {
      // check to see if we want to add a new value using a
      // wizard
      String newValueTemplate = manager.getProperty(editorTemplate + "._addValueTemplate", "");
      if (newValueTemplate.length() > 0) {
        PropertyEditorUI editor = manager.getFactory().createEditor(newValueTemplate, newValueTemplate, manager);
        AddressEntryController aec = null;
        if (editor instanceof WizardEditorPane && ((WizardEditorPane)editor).getController() instanceof AddressEntryController) {
          aec = (AddressEntryController) ((WizardEditorPane) editor).getController();
          aec.setAddressBook(book);
        }
        manager.getFactory().showNewEditorWindow(manager.getProperty(newValueTemplate + ".label", newValueTemplate), editor, getPropertyEditorPane().getContainer());

        if (aec != null) {
          AddressBookEntry newEntry = aec.getEntry();
          if (newEntry != null) {
            ((AddressBookTableModel)optionTable.getModel()).addEntry(newEntry);
          }
        }
      } else {
        addNewValue(getNewValueName(), getPropertyEditorPane().getContainer());
      }

    }
  }



}
