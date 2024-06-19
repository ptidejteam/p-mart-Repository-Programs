package net.suberic.pooka.gui;
import net.suberic.pooka.*;
import javax.mail.*;
import javax.mail.internet.MimeMessage;
import javax.mail.event.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.*;
import javax.swing.text.TextAction;
import java.util.*;
import net.suberic.util.gui.*;
import net.suberic.util.event.*;
import net.suberic.util.thread.*;
import net.suberic.util.swing.*;
import net.suberic.pooka.gui.dnd.FolderTransferHandler;

/**
 * This is a JPanel which contains a JTable which displays the messages in
 * the table.
 *
 * Note that this class does not actually do any real work.  It does have
 * Actions, but those are just passed on from the MessageProxy object in
 * the table.  You will need to have another component which implements
 * FolderDisplayUI to use as the actual UI object for the Folder.  That
 * component can then use the FolderDisplayPanel to display the messages.
 *
 */

public class FolderDisplayPanel extends JPanel {
  JTable messageTable = null;
  JScrollPane scrollPane = null;
  FolderInfo folderInfo = null;
  boolean enabled = true;

  boolean validated = false;
  boolean useFolderColumnSizes = true;

  public static String GLOBAL_COLUMN_PROPERTY_ROOT="PreviewFolderTable";

  /**
   * Creates an empty FolderDisplayPanel.
   */
  public FolderDisplayPanel() {
    initWindow();
    enabled=false;
  }

  /**
   * Creates a FolderDisplayPanel for the given FolderInfo.
   */
  public FolderDisplayPanel(FolderInfo newFolderInfo) {
    this(newFolderInfo, true);
  }

  /**
   * Creates a FolderDisplayPanel for the given FolderInfo.
   */
  public FolderDisplayPanel(FolderInfo newFolderInfo, boolean pUseFolderColumnSizes) {
    initWindow();
    setFolderInfo(newFolderInfo);
    addMessageTable();
    useFolderColumnSizes = pUseFolderColumnSizes;
  }

  /**
   * Initializes the window.
   */

  public void initWindow() {
    scrollPane = new JScrollPane();
    this.setLayout(new BorderLayout());
    this.add("Center", scrollPane);

    this.setPreferredSize(new Dimension(Integer.parseInt(Pooka.getProperty("folderWindow.height", "570")), Integer.parseInt(Pooka.getProperty("folderWindow.width","380"))));

    // if the FolderDisplayPanel itself gets the focus, pass it on to
    // the messageTable
    this.addFocusListener(new FocusAdapter() {
        public void focusGained(FocusEvent e) {
          java.util.logging.Logger.getLogger("Pooka.debug.gui.focus").fine("folder display panel:  gained focus.");

          if (messageTable != null) {
            messageTable.requestFocusInWindow();
          }
          Pooka.getMainPanel().refreshActiveMenus();
          if (getFolderInfo() != null && getFolderInfo().hasNewMessages()) {
            getFolderInfo().setNewMessages(false);
            FolderNode fn = getFolderInfo().getFolderNode();
            if (fn != null)
              fn.getParentContainer().repaint();
          }
        }
      });

    JScrollBar jsb = scrollPane.getVerticalScrollBar();
    if (jsb != null) {
      jsb.addAdjustmentListener(new AdjustmentListener() {
          public void adjustmentValueChanged(AdjustmentEvent e) {
            if (getFolderInfo() != null && getFolderInfo().hasNewMessages()) {
              getFolderInfo().setNewMessages(false);
              FolderNode fn = getFolderInfo().getFolderNode();
              if (fn != null)
                fn.getParentContainer().repaint();
            }
          }

        });
    }

    Pooka.getHelpBroker().enableHelpKey(this, "ui.folderWindow", Pooka.getHelpBroker().getHelpSet());

    setTransferHandler(new FolderTransferHandler());

  }

  /**
   * Creates the JTable for the FolderInfo and adds it to the component.
   */
  public void addMessageTable() {
    if (folderInfo != null) {
      createMessageTable();
      scrollPane.getViewport().add(messageTable);
    }

  }

  /**
   * This creates the messageTable.
   */
  public void createMessageTable() {
    messageTable=new JTable(getFolderInfo().getFolderTableModel()) {
        public String getToolTipText(MouseEvent event) {
          int rowIndex = rowAtPoint(event.getPoint());
          int columnIndex = columnAtPoint(event.getPoint());
          Object value = getValueAt(rowIndex, columnIndex);
          if (value != null) {
            return value.toString();
          } else {
            return null;
          }
        }
      };

    if (!Pooka.getProperty("FolderTable.showLines", "true").equals("true")) {
      messageTable.setShowVerticalLines(false);
      messageTable.setShowHorizontalLines(false);
    }

    FolderTableModel ftm = getFolderInfo().getFolderTableModel();
    for (int i = 0; i < messageTable.getColumnCount(); i++) {
      if (useFolderColumnSizes) {
        messageTable.getColumnModel().getColumn(i).setPreferredWidth(ftm.getColumnSize(i));
      } else {
        int colSize = 10;
        try {
          colSize = Integer.parseInt(Pooka.getProperty(GLOBAL_COLUMN_PROPERTY_ROOT + "." + ftm.getColumnId(i) + ".size", "10"));
        } catch (Exception e) {
          // just use the default.
        }
        messageTable.getColumnModel().getColumn(i).setPreferredWidth(colSize);
      }
    }

    messageTable.setAutoResizeMode(JTable.AUTO_RESIZE_NEXT_COLUMN);

    messageTable.setDefaultRenderer(Object.class, new FilterFolderCellRenderer());
    messageTable.setDefaultRenderer(Number.class, new FilterFolderCellRenderer());

    messageTable.setCellSelectionEnabled(false);
    messageTable.setColumnSelectionAllowed(false);
    messageTable.setRowSelectionAllowed(true);
    addListeners();

    messageTable.setTransferHandler(new FolderTransferHandler());

    messageTable.setDragEnabled(true);

  }

  /**
   * This removes the current message table.
   */
  public void removeMessageTable() {
    if (messageTable != null) {
      scrollPane.getViewport().remove(messageTable);
      if (getFolderInfo() != null)
        getFolderInfo().getFolderTableModel().removeTableModelListener(messageTable);
      messageTable = null;
    }
  }

  /**
   * This removes rows from the FolderTableModel.  This is the preferred
   * way to remove rows from the FolderTableModel.
   *
   * Called from within the FolderThread.
   */
  public void removeRows(java.util.List removedProxies) {
    //This is here so that we can select the next row and remove the
    //removed rows together in one call to the AWTEventThread.
    final java.util.List removedProxiesTmp = removedProxies;

    try {
      SwingUtilities.invokeAndWait(new Runnable() {
          public void run() {
            moveSelectionOnRemoval(removedProxiesTmp);

            getFolderTableModel().removeRows(removedProxiesTmp);
          }
        });
    } catch (Exception e) {
    }
  }

  /**
   * This checks to see if the message which has been removed is
   * currently selected.  If so, we unselect it and select the next
   * row.
   */
  public void moveSelectionOnRemoval(MessageChangedEvent e) {
    try {
      // don't bother if we're just going to autoexpunge it...
      if ((!Pooka.getProperty("Pooka.autoExpunge", "true").equalsIgnoreCase("true")) && e.getMessageChangeType() == MessageChangedEvent.FLAGS_CHANGED && (e.getMessage().isExpunged() || e.getMessage().getFlags().contains(Flags.Flag.DELETED))) {
        final Message changedMessage = e.getMessage();
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
              MessageProxy selectedProxy = getSelectedMessage();

              if ( selectedProxy != null && (! (selectedProxy instanceof MultiMessageProxy)) && selectedProxy.getMessageInfo().getMessage().equals(changedMessage)) {
                selectNextMessage();
              }
            }
          });
      }
    } catch (MessagingException me) {
    }
  }

  /**
   * This checks to see if the message which has been removed is
   * currently selected.  If so, we unselect it and select the next
   * row.
   */
  public void moveSelectionOnRemoval(MessageCountEvent e) {
    final Message[] removedMsgs = e.getMessages();

    SwingUtilities.invokeLater(new Runnable() {
        public void run() {
          MessageProxy selectedProxy = getSelectedMessage();
          if (selectedProxy != null)  {
            boolean found = false;
            Message currentMsg = selectedProxy.getMessageInfo().getMessage();
            for (int i = 0; (currentMsg != null && found == false && i < removedMsgs.length); i++) {
              if (currentMsg.equals(removedMsgs[i])) {
                found = true;
              }
            }

            if (found) {
              selectNextMessage();
            }
          }
        }
      });

  }

  /**
   * This checks to see if the message which has been removed is
   * currently selected.  If so, we unselect it and select the next
   * row.
   *
   * Should be called on the AWTEventThread while the FolderThread
   * is locked.
   */
  void moveSelectionOnRemoval(java.util.List removedProxies) {
    MessageProxy selectedProxy = getSelectedMessage();
    if (selectedProxy != null)  {
      boolean selectNextMessage = false;
      if (selectedProxy instanceof MultiMessageProxy) {
        MultiMessageInfo mmi = (MultiMessageInfo) selectedProxy.getMessageInfo();
        int messageCount = mmi.getMessageCount();
        selectNextMessage = true;
        for (int i = 0; selectNextMessage && i < messageCount; i++) {
          MessageProxy currentProxy = mmi.getMessageInfo(i).getMessageProxy();
          if (! removedProxies.contains(currentProxy))
            selectNextMessage=false;
        }

      } else {
        if (removedProxies.contains(selectedProxy)) {
          selectNextMessage = true;
        }

      }

      if (selectNextMessage) {
        int currentlySelected = messageTable.getSelectedRow();
        int nextValue = getNextSelectableMessage(currentlySelected, removedProxies);
        if (nextValue >= messageTable.getRowCount()) {
          // in that case, check for a selectable message before this one.
          nextValue = getPreviousSelectableMessage(nextValue, removedProxies);
        }

        if (nextValue < 0) {
          // if we're removing all of the messages, then we should just
          // be able to unselect everything.
          int[] rowSelection = messageTable.getSelectedRows();
          messageTable.removeRowSelectionInterval(rowSelection[0], rowSelection[rowSelection.length - 1]);
        } else {
          selectMessage(nextValue);
        }
      }
    }
  }

  /**
   * This recreates the message table with a new FolderTableModel.
   */
  public void resetFolderTableModel(FolderTableModel newModel) {
    if (messageTable != null) {
      FolderTableModel oldFtm = (FolderTableModel) messageTable.getModel();
      oldFtm.removeTableModelListener(messageTable);
      //newModel.addTableModelListener(messageTable);
      messageTable.setModel(newModel);
    }
  }

  /**
   * This adds all the listeners to the current FolderDisplayPanel.
   */
  public void addListeners() {
    // add a mouse listener

    messageTable.addMouseListener(new MouseAdapter() {
        public void mouseClicked(MouseEvent e) {
          if (e.getClickCount() == 2) {
            int rowIndex = getMessageTable().rowAtPoint(e.getPoint());
            if (rowIndex != -1) {
              getMessageTable().setRowSelectionInterval(rowIndex, rowIndex);
              MessageProxy selectedMessage = getSelectedMessage();
              String actionCommand = Pooka.getProperty("MessagePanel.2xClickAction", "file-open");
              if (selectedMessage != null) {
                Action clickAction = selectedMessage.getAction(actionCommand);
                if (clickAction != null && isEnabled()) {
                  clickAction.actionPerformed (new ActionEvent(this, ActionEvent.ACTION_PERFORMED, actionCommand));

                }
              }
            }
          }
        }

        public void mousePressed(MouseEvent e) {
          if (e.isPopupTrigger()) {
            // see if anything is selected
            int rowIndex = getMessageTable().rowAtPoint(e.getPoint());
            int columnIndex = getMessageTable().columnAtPoint(e.getPoint());
            if (rowIndex == -1 || !getMessageTable().isRowSelected(rowIndex) ) {
              getMessageTable().setRowSelectionInterval(rowIndex, rowIndex);
            }

            MessageProxy selectedMessage = getSelectedMessage();
            if (selectedMessage != null && isEnabled()) {
              Object o = getMessageTable().getValueAt(rowIndex, columnIndex);
              if (o != null && o instanceof BooleanIcon) {
                BooleanIcon bi = (BooleanIcon) o;
                if (bi.getIconId().equalsIgnoreCase("attachments") && bi.iconValue()) {
                  selectedMessage.showAttachmentPopupMenu(getMessageTable(), e);
                } else {
                  selectedMessage.showPopupMenu(getMessageTable(), e);

                }
              } else {
                selectedMessage.showPopupMenu(getMessageTable(), e);
              }
            }
          }
        }

        public void mouseReleased(MouseEvent e) {
          if (e.isPopupTrigger()) {
            // see if anything is selected
            int rowIndex = getMessageTable().rowAtPoint(e.getPoint());
            int columnIndex = getMessageTable().columnAtPoint(e.getPoint());
            if (rowIndex == -1 || !getMessageTable().isRowSelected(rowIndex) ) {
              getMessageTable().setRowSelectionInterval(rowIndex, rowIndex);
            }

            MessageProxy selectedMessage = getSelectedMessage();
            if (selectedMessage != null && isEnabled())
              if (columnIndex == 2)
                selectedMessage.showAttachmentPopupMenu(getMessageTable(), e);
              else
                selectedMessage.showPopupMenu(getMessageTable(), e);
          }
        }
      });

    messageTable.getSelectionModel().addListSelectionListener(new SelectionListener());

    // add sorting by header.

    messageTable.getTableHeader().addMouseListener(new MouseAdapter() {
        public void mousePressed(MouseEvent e) {
          // show a wait cursor if we're not done loading the messages yet.
          boolean allLoaded = true;
          java.util.List data = ((FolderTableModel)messageTable.getModel()).getAllProxies();
          java.util.Iterator it = data.iterator();
          while (allLoaded && it.hasNext()) {
            MessageProxy current = (MessageProxy) it.next();
            if (! current.isLoaded())
              allLoaded = false;
          }

          if (! allLoaded) {
            messageTable.getTableHeader().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
          }

        }

        public void mouseReleased(MouseEvent e) {
          // clear the wait cursor, if any.
          messageTable.getTableHeader().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        }

        public void mouseClicked(MouseEvent e) {
          TableColumnModel columnModel = messageTable.getColumnModel();
          int viewColumn = columnModel.getColumnIndexAtX(e.getX());
          int column = messageTable.convertColumnIndexToModel(viewColumn);
          if (e.getClickCount() == 1 && column != -1) {
            // check to make sure that all messages are loaded.
            boolean allLoaded = true;
            java.util.List data = ((FolderTableModel)messageTable.getModel()).getAllProxies();
            java.util.Iterator it = data.iterator();
            while (allLoaded && it.hasNext()) {
              MessageProxy current = (MessageProxy) it.next();
              if (! current.isLoaded())
                allLoaded = false;
            }

            if (allLoaded) {
              java.util.logging.Logger.getLogger("Pooka.debug").fine("Sorting ...");

              int shiftPressed = e.getModifiers()&InputEvent.SHIFT_MASK;
              boolean ascending = (shiftPressed == 0);

              MessageProxy selectedMessage = null;

              int rowsSelected = messageTable.getSelectedRowCount();

              if (rowsSelected == 1)
                selectedMessage = getFolderInfo().getMessageProxy(messageTable.getSelectedRow());
              else if (rowsSelected > 1)
                selectedMessage = getFolderInfo().getMessageProxy(messageTable.getSelectedRows()[0]);

              if (! ascending) {
                ((FolderTableModel)messageTable.getModel()).sortByColumn(column, ascending);
              } else {
                ((FolderTableModel)messageTable.getModel()).sortByColumn(column );
              }

              if (selectedMessage != null) {
                int selectedIndex = ((FolderTableModel)messageTable.getModel()).getRowForMessage(selectedMessage);
                messageTable.setRowSelectionInterval(selectedIndex, selectedIndex);
                makeSelectionVisible(selectedIndex);
              }
            }
          }
        }
      });

    messageTable.registerKeyboardAction(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          FolderDisplayUI fdui = getFolderInfo().getFolderDisplayUI();
          if (fdui != null) {
            fdui.selectNextMessage();
          }
        }
      }, KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_DOWN, 0),  JComponent.WHEN_FOCUSED);

    messageTable.registerKeyboardAction(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          FolderDisplayUI fdui = getFolderInfo().getFolderDisplayUI();
          if (fdui != null) {
            fdui.selectPreviousMessage();
          }
        }
      }, KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_UP, 0),  JComponent.WHEN_FOCUSED);

    messageTable.registerKeyboardAction(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          MessageProxy selectedMessage = getSelectedMessage();
          if (selectedMessage != null) {
            Action defaultOpenAction = selectedMessage.getAction("file-default-open");
            if (defaultOpenAction != null) {
              defaultOpenAction.actionPerformed(e);
            } else {
              Pooka.getUIFactory().doDefaultOpen(selectedMessage);
            }
          }
        }
      }, KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ENTER, 0),  JComponent.WHEN_FOCUSED);

    messageTable.registerKeyboardAction(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          MessageProxy selectedMessage = getSelectedMessage();
          if (selectedMessage != null) {
            Action defaultOpenAction = selectedMessage.getAction("file-default-open");
            if (defaultOpenAction != null) {
              defaultOpenAction.actionPerformed(e);
            } else {
              Pooka.getUIFactory().doDefaultOpen(selectedMessage);
            }
          }
        }
      }, KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_SPACE, 0),  JComponent.WHEN_FOCUSED);

    messageTable.registerKeyboardAction(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          selectNextUnreadMessage();
        }
      }, KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_TAB, 0),  JComponent.WHEN_FOCUSED);

    messageTable.registerKeyboardAction(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          selectPreviousUnreadMessage();
        }
      }, KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_TAB, java.awt.Event.SHIFT_MASK),  JComponent.WHEN_FOCUSED);

  }

  /**
   * This finds the first unread message (if any) and sets that message
   * to selected, and returns that index.
   */
  public void selectFirstUnread() {

    // sigh.
    getFolderInfo().getFolderThread().addToQueue(new javax.swing.AbstractAction() {
        public void actionPerformed(java.awt.event.ActionEvent ae) {
          final int firstUnread = getFolderInfo().getFirstUnreadMessage();
          SwingUtilities.invokeLater(new Runnable() {
              public void run() {
                int useFirstUnread = firstUnread;
                if (useFirstUnread < 0 || useFirstUnread > messageTable.getRowCount()) {
                  useFirstUnread = messageTable.getRowCount() - 1;
                } else {
                  messageTable.setRowSelectionInterval(useFirstUnread, useFirstUnread);
                }
                makeSelectionVisible(useFirstUnread);
              }
            });
        }
      }, new java.awt.event.ActionEvent(this, 0, "folder-select-first-unread"));

  }

  /**
   * This scrolls the given row number to visible.
   */
  public void makeSelectionVisible(int rowNumber) {
    messageTable.scrollRectToVisible(messageTable.getCellRect(rowNumber, 1, true));

  }


  /**
   * This selects the next message.  If no message is selected, then
   * the first message is selected.
   */
  public int selectNextMessage() {
    int selectedRow = messageTable.getSelectedRow();
    int nextSelectable = getNextSelectableMessage(selectedRow, null);
    return selectMessage(nextSelectable);
  }

  /**
   * This selects the next unread message.  If no message is selected, then
   * the first unread message is selected.  If no unread messages exist, this
   * does nothing.
   */
  public int selectNextUnreadMessage() {
    int selectedRow = messageTable.getSelectedRow();
    int nextSelectable = getNextSelectableMessage(selectedRow, null, true);
    return selectMessage(nextSelectable);
  }


  /**
   * Determines which message is the next selectable message.  If no
   * messages past this one are selectable (i.e. not deleted or about to
   * be deleted), returns messageTable.getRowCount() (i.e. an unused
   * row.
   *
   * Since we're polling flags on the Messages, this probably should be
   * called on the FolderThread.  The change of selection itself, of course,
   * should be done on the AWTEventThread.
   */
  public int getNextSelectableMessage(int selectedRow, java.util.List removedProxies) {
    return getNextSelectableMessage(selectedRow, removedProxies, false);
  }

  /**
   * Determines which message is the next selectable message.  If no
   * messages past this one are selectable (i.e. not deleted or about to
   * be deleted), returns messageTable.getRowCount() (i.e. an unused
   * row.
   *
   * Since we're polling flags on the Messages, this probably should be
   * called on the FolderThread.  The change of selection itself, of course,
   * should be done on the AWTEventThread.
   */
  public int getNextSelectableMessage(int selectedRow, java.util.List removedProxies, boolean unread) {
    int newRow = selectedRow + 1;
    boolean done = false;
    while (! done && newRow < messageTable.getRowCount() ) {
      MessageProxy mp = getFolderInfo().getMessageProxy(newRow);
      try {
        //if ((removedProxies != null && removedProxies.contains(mp)) || mp.getMessageInfo().getFlags().contains(Flags.Flag.DELETED) || (unread && mp.getMessageInfo().getFlags().contains(Flags.Flag.SEEN))) {
        if ((removedProxies != null && removedProxies.contains(mp)) || mp.isDeleted() || (unread && mp.isSeen())) {
          newRow ++;
        } else {
          done = true;
        }
      } catch (MessagingException me) {
        newRow ++;
      }
    }

    return newRow;
  }


  /**
   * This selects the previous message.  If no message is selected, then
   * the last message is selected.
   */
  public int selectPreviousMessage() {
    int[] rowsSelected = messageTable.getSelectedRows();
    int selectedRow = 0;
    if (rowsSelected.length > 0)
      selectedRow = rowsSelected[0];
    else
      selectedRow = messageTable.getRowCount();

    int previousSelectable = getPreviousSelectableMessage(selectedRow, null, false);
    return selectMessage(previousSelectable);
  }

  /**
   * This selects the previous unread message.  If no message is selected, then
   * the first message is selected.
   */
  public int selectPreviousUnreadMessage() {
    int[] rowsSelected = messageTable.getSelectedRows();
    int selectedRow = 0;
    if (rowsSelected.length > 0)
      selectedRow = rowsSelected[0];
    else
      selectedRow = messageTable.getRowCount();

    int previousSelectable = getPreviousSelectableMessage(selectedRow, null, true);
    return selectMessage(previousSelectable);
  }

  /**
   * Determines which message is the previous selectable message.  If no
   * messages before this one are selectable (i.e. not deleted or about to
   * be deleted), returns -1.
   *
   * Since we're polling flags on the Messages, this probably should be
   * called on the FolderThread.  The change of selection itself, of course,
   * should be done on the AWTEventThread.
   */
  public int getPreviousSelectableMessage(int selectedRow, java.util.List removedProxies) {
    return getPreviousSelectableMessage(selectedRow, removedProxies, false);
  }

  /**
   * Determines which message is the previous selectable message.  If no
   * messages before this one are selectable (i.e. not deleted or about to
   * be deleted), returns -1.
   *
   * Since we're polling flags on the Messages, this probably should be
   * called on the FolderThread.  The change of selection itself, of course,
   * should be done on the AWTEventThread.
   */
  public int getPreviousSelectableMessage(int selectedRow, java.util.List removedProxies, boolean unread) {
    int newRow = selectedRow - 1;
    boolean done = false;
    while (! done && newRow >= 0 ) {
      MessageProxy mp = getFolderInfo().getMessageProxy(newRow);
      try {
        //if ((removedProxies != null && removedProxies.contains(mp)) || mp.getMessageInfo().getFlags().contains(Flags.Flag.DELETED) || (unread && mp.getMessageInfo().getFlags().contains(Flags.Flag.SEEN))) {
        if ((removedProxies != null && removedProxies.contains(mp)) || mp.isDeleted() || (unread && mp.isSeen())) {
          newRow--;
        } else {
          done = true;
        }
      } catch (MessagingException me) {
        newRow--;
      }
    }

    return newRow;

  }

  /**
   * Selects all of the messages in the FolderTable.
   */
  public void selectAll() {
    messageTable.selectAll();
  }

  /**
   * This selects the message at the given row, and also scrolls the
   * MessageTable to make the given row visible.
   *
   * If the number entered is below the range of available messages, then
   * the first message is selected.  If the number entered is above that
   * range, then the last message is selected.  If the MessageTable
   * contains no messages, nothing happens.
   *
   * @return  the index of the newly selected row.
   */
  public int selectMessage(int messageNumber) {
    int rowCount = messageTable.getRowCount();

    if (rowCount > 0) {
      int numberToSet = messageNumber;

      if (messageNumber < 0) {
        numberToSet = 0;
      } else if (messageNumber >= rowCount) {
        numberToSet = rowCount - 1;
      }
      messageTable.setRowSelectionInterval(numberToSet, numberToSet);
      makeSelectionVisible(numberToSet);
      return numberToSet;
    } else {
      return -1;
    }
  }

  /**
   * This method takes the currently selected row(s) and returns the
   * appropriate MessageProxy object.
   *
   * If no rows are selected, null is returned.
   */
  public MessageProxy getSelectedMessage() {
    if (messageTable != null) {
      int rowsSelected = messageTable.getSelectedRowCount();

      if (rowsSelected == 1)
        return getFolderInfo().getMessageProxy(messageTable.getSelectedRow());
      else if (rowsSelected < 1)
        return null;
      else {
        int[] selectedRows = messageTable.getSelectedRows();
        MessageProxy[] msgSelected= new MessageProxy[selectedRows.length];
        for (int i = 0; i < selectedRows.length; i++)
          msgSelected[i] = getFolderInfo().getMessageProxy(selectedRows[i]);
        return new MultiMessageProxy(selectedRows, msgSelected, this.getFolderInfo());
      }
    } else {
      return null;
    }
  }

  /**
   * This updates the entry for the given message, if that message is
   * visible.
   */
  public void repaintMessage(MessageProxy mp) {
    int row = getFolderTableModel().getRowForMessage(mp);
    if (row >=0) {
      getFolderTableModel().fireTableRowsUpdated(row, row);
    }
  }

  /**
   * Saves state for this display panel.
   */
  public void saveTableSettings() {
    if (getFolderInfo() != null) {
      String key = getFolderInfo().getFolderProperty();
      // save the column information.
      FolderTableModel ftm = getFolderTableModel();
      for (int i = 0; i < ftm.getColumnCount(); i++) {
        Pooka.setProperty(key + ".columnsize." + ftm.getColumnId(i) + ".value", Integer.toString(messageTable.getColumnModel().getColumn(i).getWidth()));

      }
    }
  }

  /**
   * This resets the size to that of the parent component.
   */
  public void resize() {
    this.setSize(getParent().getSize());
  }

  // Accessor methods.

  public JTable getMessageTable() {
    return messageTable;
  }

  /**
   * This sets the FolderInfo.
   */
  public void setFolderInfo(FolderInfo newValue) {
    folderInfo=newValue;
  }

  public FolderInfo getFolderInfo() {
    return folderInfo;
  }

  /**
   * Returns the FolderTableModel for this FolderDisplayPanel.
   */
  public FolderTableModel getFolderTableModel() {
    if (getFolderInfo() != null)
      return getFolderInfo().getFolderTableModel();
    else
      return null;
  }

  /**
   * gets the actions handled both by the FolderDisplayPanel and the
   * selected Message(s).
   */

  public class SelectionListener implements javax.swing.event.ListSelectionListener {
    SelectionListener() {
    }

    public void valueChanged(javax.swing.event.ListSelectionEvent e) {
      Pooka.getMainPanel().refreshActiveMenus();
      getFolderInfo().setNewMessages(false);
      FolderNode fn = getFolderInfo().getFolderNode();
      if (fn != null)
        fn.getParentContainer().repaint();
    }
  }

  /**
   * This registers the Keyboard action not only for the FolderDisplayPanel
   * itself, but also for pretty much all of its children, also.  This
   * is to work around something which I think is a bug in jdk 1.2.
   * (this is not really necessary in jdk 1.3.)
   *
   * Overrides JComponent.registerKeyboardAction(ActionListener anAction,
   *            String aCommand, KeyStroke aKeyStroke, int aCondition)
   */

  public void registerKeyboardAction(ActionListener anAction,
                                     String aCommand, KeyStroke aKeyStroke,
                                     int aCondition) {
    super.registerKeyboardAction(anAction, aCommand, aKeyStroke, aCondition);

    if (messageTable != null)
      messageTable.registerKeyboardAction(anAction, aCommand, aKeyStroke, aCondition);
  }

  /**
   * This unregisters the Keyboard action not only for the FolderDisplayPanel
   * itself, but also for pretty much all of its children, also.  This
   * is to work around something which I think is a bug in jdk 1.2.
   * (this is not really necessary in jdk 1.3.)
   *
   * Overrides JComponent.unregisterKeyboardAction(KeyStroke aKeyStroke)
   */

  public void unregisterKeyboardAction(KeyStroke aKeyStroke) {
    super.unregisterKeyboardAction(aKeyStroke);

    messageTable.unregisterKeyboardAction(aKeyStroke);
  }

  /**
   * Returns whether or not this window is enabled.  This should be true
   * just about all of the time.  The only time it won't be true is if
   * the Folder is closed or disconnected, and the mail store isn't set
   * up to work in disconnected mode.
   */
  public boolean isEnabled() {
    return enabled;
  }

  /**
   * This sets whether or not the window is enabled.  This should only
   * be set to false when the Folder is no longer available.
   */
  public void setEnabled(boolean newValue) {
    enabled = newValue;
  }

  public Action[] getActions() {
    if (isEnabled()) {
      Action[] returnValue = null;
      MessageProxy m = getSelectedMessage();

      if (m != null)
        returnValue = m.getActions();

      if (folderInfo.getActions() != null) {
        if (returnValue != null) {
          returnValue = TextAction.augmentList(folderInfo.getActions(), returnValue);
        } else {
          returnValue = folderInfo.getActions();
        }
      }

      if (messageTable != null) {
        Action[] defaultActions = new Action[] {
          FolderTransferHandler.getCutAction(messageTable),
          FolderTransferHandler.getCopyAction(messageTable),
          FolderTransferHandler.getPasteAction(messageTable)
        };
        if (returnValue != null) {
          returnValue = TextAction.augmentList(defaultActions, returnValue);
        } else {
          returnValue = defaultActions;
        }
      }

      return returnValue;
    } else {
      return null;
    }
  }
}
