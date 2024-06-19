package net.suberic.pooka.gui;

import javax.swing.*;
import javax.mail.internet.*;
import javax.mail.*;
import java.util.*;
import java.io.*;
import java.awt.event.ActionEvent;
import java.awt.event.*;
import javax.activation.*;
import javax.swing.table.AbstractTableModel;

import net.suberic.util.thread.*;
import net.suberic.util.swing.*;
import net.suberic.pooka.*;
import java.awt.*;

/**
 * This class basically creates a visual list of the parts of a
 * MimeMessage.
 */
public class AttachmentPane extends JPanel {

  /**
   * The AttachmentTableModel displays the MessageProxy's attachments
   * list as a JTable.
   */
  class AttachmentTableModel extends AbstractTableModel {
    MessageProxy msg;
    Vector columnNames;

    public AttachmentTableModel(MessageProxy newMsg) {
      msg=newMsg;
      columnNames = new Vector();
      columnNames.add(Pooka.getProperty("AttachmentPane.header.name", "Filename"));
      columnNames.add(Pooka.getProperty("AttachmentPane.header.type", "Type"));
    }

    public int getRowCount() {
      try {
        return msg.getAttachments().size();
      } catch (MessagingException me) {
        return 0;
      }
    }

    /**
     * As of now, we just have two columns:  file name and file type.
     * Maybe in the future we'll have an icon, too.
     */
    public int getColumnCount() {
      return 2;
    }

    /**
     * This gets the displayed value for each column in the table.
     */
    public Object getValueAt(int row, int column) {
      java.util.List v = null;
      try {
        v = msg.getAttachments();

        if (v != null && row < v.size()) {
          if (column == 0) {
            String name = (((Attachment)v.get(row)).getName());
            if (name != null)
              return name;
            else
              return Pooka.getProperty("AttachmentPane.error.FileNameUnavailable", "Unavailable");
          } else if (column == 1) {

            String contentType = ((Attachment)v.get(row)).getMimeType().toString();
            if (contentType.indexOf(';') != -1)
              contentType = contentType.substring(0, contentType.indexOf(';'));
            return contentType;
          }
        }
      } catch (MessagingException me) {
      }
      // if it's not a valid request, just return null.

      return null;
    }

    /**
     * A convenience method to return a particular Attachment.
     *
     * Returns null if there is no entry at that row.
     */
    public Attachment getAttachmentAtRow(int row) {
      try {
        if ((row < msg.getAttachments().size()) && (row >= 0))
          return (Attachment)msg.getAttachments().get(row);
      } catch (MessagingException me) {
      }

      return null;
    }

    public String getColumnName(int columnIndex) {
      if (columnIndex >= 0 && columnIndex < columnNames.size())
        return (String)columnNames.get(columnIndex);
      else
        return null;
    }
  } // AttachmentTableModel

  JTable table;
  AttachmentTableModel tableModel;
  MessageProxy message;
  JPanel displayPanel;
  Action[] defaultActions;

  public AttachmentPane (MessageProxy msg) {
    super();

    message=msg;
    defaultActions = createDefaultActions();

    tableModel = new AttachmentTableModel(message);

    table = new JTable(tableModel);

    tableModel.addTableModelListener(table);

    table.addMouseListener(new MouseAdapter() {
        public void mousePressed(MouseEvent e) {
          if (e.getClickCount() == 2) {
            Attachment selectedAttachment = getSelectedAttachment();
            String actionCommand = Pooka.getProperty("AttachmentPane.2xClickAction", "file-open");
            if (selectedAttachment != null) {
              Action clickAction = getActionByName(actionCommand);
              if (clickAction != null) {
                clickAction.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, actionCommand));

              }
            }
          } else if (e.isPopupTrigger()) {
            // see if anything is selected
            int rowIndex = getTable().rowAtPoint(e.getPoint());
            if (rowIndex != -1) {
              if (! getTable().isRowSelected(rowIndex)) {
                getTable().setRowSelectionInterval(rowIndex, rowIndex);
              }
              createPopupMenu().show(getTable(), e.getX(), e.getY());
            }

          }

        }

        public void mouseReleased(MouseEvent e) {
          if (e.isPopupTrigger()) {
            // see if anything is selected
            int rowIndex = getTable().rowAtPoint(e.getPoint());
            if (rowIndex != -1) {
              if (! getTable().isRowSelected(rowIndex)) {
                getTable().setRowSelectionInterval(rowIndex, rowIndex);
              }
              createPopupMenu().show(getTable(), e.getX(), e.getY());
            }
          }
        }
      });

    JScrollPane jsp = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    jsp.getViewport().add(table);
    table.addNotify();
    this.add(jsp);

    // the width will be resized; the only important part here is
    // the height.

    Dimension prefSize = this.getPreferredSize();
    int defaultHeight = Integer.parseInt(Pooka.getProperty("Pooka.attachmentPanel.vsize", "100"));
    if (prefSize.getHeight() > defaultHeight) {
      this.setPreferredSize(new Dimension((int)prefSize.getWidth(), defaultHeight));
    }
    Dimension jspPrefSize = jsp.getPreferredSize();
    if (jspPrefSize.getHeight() > defaultHeight - 15) {
      jsp.setPreferredSize(new Dimension((int)prefSize.getWidth(), defaultHeight - 15));
    }

    this.addFocusListener(new FocusAdapter() {
        public void focusGained(FocusEvent e) {
          if (getTable() != null) {
            if (getSelectedAttachment() == null) {
              getTable().setRowSelectionInterval(0,0);
            }
            getTable().requestFocusInWindow();
          }
        }
      });

    createKeyBindings();

    table.setTransferHandler(new net.suberic.pooka.gui.dnd.AttachmentTransferHandler());
    table.setDragEnabled(true);
  }

  /**
   * Returns the display panel for the AttachmentPane.  This will normally
   * contain just the AttachmentTable.
   */
  public JPanel getDisplayPanel() {
    return displayPanel;
  }

  /**
   * getSelectedAttachment() will return the selected Attachment.
   */
  public Attachment getSelectedAttachment() {
    return getTableModel().getAttachmentAtRow(getTable().getSelectedRow());
  }

  /**
   * This opens up the selected Attachment using the default handler
   * for the Attachment's Mime type.
   */
  public void openSelectedAttachment() {
    Attachment attachment = getSelectedAttachment();
    AttachmentHandler ah = new AttachmentHandler(message);
    ah.openAttachment(attachment);
  }

  /**
   * This opens the Attachment with the program of the user's choice.
   */
  public void openWith() {
    Attachment attachment = getSelectedAttachment();
    AttachmentHandler ah = new AttachmentHandler(message);
    ah.openWith(attachment);
  }

  /**
   * This opens up a JFileChooser to let the user choose under what
   * name and where the selected Attachment should be saved.  It then
   * calls saveFileAs() to save the file.
   */
  public void saveAttachment() {
    Attachment attachment = getSelectedAttachment();
    AttachmentHandler ah = new AttachmentHandler(message);
    ah.saveAttachment(attachment, this);
  }

  /**
   * This opens up a JFileChooser to let the user choose under what
   * name and where the selected Attachment should be saved.  It then
   * calls saveFileAs() to save the file.
   */
  public void saveAllAttachments() {
    AttachmentHandler ah = new AttachmentHandler(message);
    ah.saveAllAttachments(this);
  }

  /**
   * This removes the Attachment from the message.
   */
  public void removeAttachment() {
    int selectedIndex = getTable().getSelectedRow();
    Attachment attachmentToRemove = getSelectedAttachment();
    ((NewMessageProxy)message).detachFile(attachmentToRemove);
  }

  public AttachmentTableModel getTableModel() {
    return tableModel;
  }

  /**
   * Gets the table with all of the attachment entries.
   */
  public JTable getTable() {
    return table;
  }

  /**
   * Creates the popup menu for this component.
   */
  protected JPopupMenu createPopupMenu() {
    net.suberic.util.gui.ConfigurablePopupMenu popupMenu = new net.suberic.util.gui.ConfigurablePopupMenu();
    String key;
    if (message instanceof NewMessageProxy)
      key = "AttachmentPane.NewMsgActions";
    else
      key = "AttachmentPane.Actions";
    popupMenu.configureComponent(key, Pooka.getResources());
    popupMenu.setActive(getActions());
    MessageUI mui = ((MessageProxy)message).getMessageUI();
    if (mui instanceof net.suberic.util.swing.ThemeSupporter) {
      try {
        Pooka.getUIFactory().getPookaThemeManager().updateUI((net.suberic.util.swing.ThemeSupporter) mui, popupMenu, true);
      } catch (Exception etwo) {
        if (Pooka.isDebug())
          System.out.println("error setting theme:  " + etwo);
      }
    }
    return popupMenu;
  }

  /**
   * Creates the ConfigurableKeyBindings for this component.
   */
  protected void createKeyBindings() {
    String key;
    if (message instanceof NewMessageProxy)
      key = "AttachmentPane.newMsgKeyBindings";
    else
      key = "AttachmentPane.keyBindings";

    net.suberic.util.gui.ConfigurableKeyBinding keyBindings = new net.suberic.util.gui.ConfigurableKeyBinding(getTable(), key, Pooka.getResources());
    keyBindings.setActive(getActions());

  }


  /**
   * Returns the given Action.
   */
  public Action getActionByName(String actionName) {
    Action[] actionList = getActions();
    for (int i = 0; i < actionList.length; i++) {
      if (actionName.equals((String)actionList[i].getValue(Action.NAME))) {
        return actionList[i];
      }
    }
    return null;

  }

  /**
   * Creates the default actions for this pane.
   */
  public Action[] createDefaultActions() {
    if (message instanceof NewMessageProxy)
      return new Action[] {
        new RemoveAction()
      };
    else {
      ActionThread storeThread = message.getFolderInfo().getParentStore().getStoreThread();
      return new Action[] {
        new ActionWrapper(new OpenAction(), storeThread),
        new ActionWrapper(new OpenWithAction(), storeThread),
        new ActionWrapper(new SaveAsAction(), storeThread),
        new ActionWrapper(new SaveAllAction(), storeThread)
      };
    }
  }

  public Action[] getActions() {
    return getDefaultActions();
  }

  public Action[] getDefaultActions() {
    return defaultActions;
  }

  public MessageUI getMessageUI() {
    return message.getMessageUI();
  }

  public MessageProxy getMessageProxy() {
    return message;
  }

  /**
   * Shows an error message, either on the MessageUI if there is one, or
   * if not, on the main Pooka frame.
   */
  public void showError(String message, Exception ioe) {
    MessageUI mui = getMessageUI();
    if (mui != null) {
      mui.showError(message,ioe);
    } else {
      Pooka.getUIFactory().showError(message,ioe);
    }
  }

  /**
   * Shows an error message, either on the MessageUI if there is one, or
   * if not, on the main Pooka frame.
   */
  public void showError(String message, String title, Exception ioe) {
    MessageUI mui = getMessageUI();
    if (mui != null) {
      mui.showError(message, title, ioe);
    } else {
      Pooka.getUIFactory().showError(message, title, ioe);
    }
  }

  //------------------------------------//

  class OpenAction extends AbstractAction {
    OpenAction() {
      super("file-open");
    }

    public void actionPerformed(ActionEvent e) {
      openSelectedAttachment();
    }
  }

  class OpenWithAction extends AbstractAction {
    OpenWithAction() {
      super("file-open-with");
    }

    public void actionPerformed(ActionEvent e) {
      openWith();
    }
  }

  class SaveAsAction extends AbstractAction {
    SaveAsAction() {
      super("file-save-as");
    }

    public void actionPerformed(ActionEvent e) {
      saveAttachment();
    }
  }

  class SaveAllAction extends AbstractAction {
    SaveAllAction() {
      super("file-save-all");
    }

    public void actionPerformed(ActionEvent e) {
      saveAllAttachments();
    }
  }

  class RemoveAction extends AbstractAction {
    RemoveAction() {
      super("file-remove");
    }

    public void actionPerformed(ActionEvent e) {
      removeAttachment();
    }
  }

}


