package net.suberic.pooka.gui;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.InternetHeaders;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextPane;
import javax.swing.JToggleButton;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.JTextComponent;
import javax.swing.text.TextAction;

import net.suberic.pooka.NewMessageInfo;
import net.suberic.pooka.Pooka;
import net.suberic.pooka.UserProfile;
import net.suberic.pooka.gui.crypto.CryptoStatusDisplay;
//import net.suberic.pooka.gui.crypto.NewMessageCryptoDisplay;
import net.suberic.util.gui.ConfigurableKeyBinding;
import net.suberic.util.gui.ConfigurablePopupMenu;
import net.suberic.util.gui.IconManager;
import net.suberic.util.swing.EntryTextArea;

/**
 * A window for entering new messages.
 */
public class NewMessageDisplayPanel extends MessageDisplayPanel implements ItemListener {
  JTabbedPane tabbedPane = null;
  Container headerPanel = null;
  boolean modified = false;
  Hashtable inputTable;

  JScrollPane headerScrollPane;

  private Action[] defaultActions;

  CryptoStatusDisplay cryptoDisplay = null;
  Container cryptoPanel = null;

  Container customHeaderPane = null;
  JTable customHeaderTable = null;
  JToggleButton customHeaderButton = null;

  /**
   * Creates a NewMessageDisplayPanel from the given Message.
   */

  public NewMessageDisplayPanel(NewMessageUI newMsgUI) {
    super(newMsgUI);
  }

  /**
   * This configures the MessageDisplayPanel.  This means that here is
   * where we create the headerPanel and editorPane and add them to the
   * splitPane.
   */
  public void configureMessageDisplay() throws MessagingException {

    splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
    tabbedPane = new JTabbedPane();

    inputTable = new Hashtable();

    headerPanel = createHeaderInputPanel(getMessageProxy(), inputTable);
    editorPane = createMessagePanel(getMessageProxy());

    installTransferHandler();

    // workaround for java bug.
    editorPane.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
        public void propertyChange(java.beans.PropertyChangeEvent evt) {
          String propertyName = evt.getPropertyName();
          if (propertyName != null && propertyName.equalsIgnoreCase("ui")) {
            installTransferHandler();
          }
        }
      });


    headerScrollPane = new JScrollPane(headerPanel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    tabbedPane.add(Pooka.getProperty("MessageWindow.HeaderTab", "Headers"), headerScrollPane);

    if (getMessageProxy().getAttachments() != null && getMessageProxy().getAttachments().size() > 0) {
      addAttachmentPane();
    }

    editorScrollPane = new JScrollPane(editorPane, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

    splitPane.setTopComponent(tabbedPane);
    splitPane.setBottomComponent(editorScrollPane);

    this.add("Center", splitPane);

    editorPane.addMouseListener(new MouseAdapter() {

        public void mousePressed(MouseEvent e) {
          if (e.isPopupTrigger()) {
            showPopupMenu(editorPane, e);
          }
        }

        public void mouseReleased(MouseEvent e) {
          if (e.isPopupTrigger()) {
            showPopupMenu(editorPane, e);
          }
        }
      });

    editorPane.addKeyListener(new KeyAdapter() {
        public void keyTyped(KeyEvent e) {
          setModified(true);
        }
      });

    splitPane.resetToPreferredSizes();

    this.addFocusListener(new FocusAdapter() {
        public void focusGained(FocusEvent e) {
          // if we get focus, see what we want to select by default.
          // if there's no to: done, select to:.  if there's no
          // subject, select it.  if both of those are filled out,
          // select the message.

          Component subjectComponent = null;
          Component toComponent = null;

          boolean done = false;

          if (inputTable != null) {
            String key;
            Enumeration keys = inputTable.keys();
            while (keys.hasMoreElements()) {
              key = (String)(keys.nextElement());

              if (key.equalsIgnoreCase("subject")) {
                subjectComponent = (Component) inputTable.get(key);
              } else if (key.equalsIgnoreCase("to")) {
                toComponent = (Component) inputTable.get(key);
              }
            }

            if (toComponent != null && toComponent instanceof JTextComponent) {
              String toValue = ((JTextComponent) toComponent).getText();
              if (toValue == null || toValue.length() == 0) {
                done = true;
                toComponent.requestFocusInWindow();
              }
            }

            if (! done && subjectComponent != null && subjectComponent instanceof JTextComponent) {
              String subjectValue = ((JTextComponent) subjectComponent).getText();
              if (subjectValue == null || subjectValue.length() == 0) {
                done = true;
                subjectComponent.requestFocusInWindow();
              }
            }
          }

          if (! done) {
            if (editorPane != null)
              editorPane.requestFocusInWindow();
          }
        }
      });

    keyBindings = new ConfigurableKeyBinding(this, "NewMessageWindow.keyBindings", Pooka.getResources());
    //keyBindings.setCondition(JComponent.WHEN_IN_FOCUSED_WINDOW);

    keyBindings.setActive(getActions());

  }

  /**
   * Sets the window to its preferred size.
   */
  public void sizeToDefault() {
    Dimension prefSize = getDefaultEditorPaneSize();
    JScrollBar vsb = editorScrollPane.getVerticalScrollBar();
    if (vsb != null)
      prefSize.setSize(prefSize.getWidth() + vsb.getPreferredSize().getWidth(), prefSize.getHeight());
    editorScrollPane.setPreferredSize(prefSize);
    int width = prefSize.width;
    this.setPreferredSize(new Dimension(width, width));
    this.setSize(this.getPreferredSize());
  }

  /**
   * as defined in java.awt.event.ItemListener
   *
   * This implementation calls a refreshCurrentUser() on the MainPanel.
   *
   * It also updates the panel's interface style.
   */
  public void itemStateChanged(ItemEvent ie) {
    if (ie.getStateChange() == ItemEvent.SELECTED) {
      if (Pooka.getMainPanel() != null)
        Pooka.getMainPanel().refreshCurrentUser();
      SwingUtilities.invokeLater(new Runnable() {
          public void run() {
            NewMessageUI nmui = getNewMessageUI();
            if (nmui instanceof net.suberic.util.swing.ThemeSupporter) {
              try {
                Pooka.getUIFactory().getPookaThemeManager().updateUI((net.suberic.util.swing.ThemeSupporter) nmui, (java.awt.Component) nmui);
                Font currentFont = editorPane.getFont();
                setDefaultFont(editorPane);
                Font newFont = editorPane.getFont();
                if (currentFont != newFont) {
                  sizeToDefault();
                }
                MessageUI mui = getMessageUI();
                if (mui instanceof MessageInternalFrame) {
                  ((MessageInternalFrame) mui).resizeByWidth();
                } else if (mui instanceof MessageFrame) {
                  ((MessageFrame) mui).resizeByWidth();
                }

              } catch (Exception e) {
                if (Pooka.isDebug())
                  System.out.println("error setting theme:  " + e);
              }
            }
          }
        });
    }

  }

  /**
   * Creates the panel in which the addressing will be done, such as
   * the To: field, Subject: field, etc.
   */
  public Container createHeaderInputPanel(MessageProxy pProxy, Hashtable proptDict) {

    Box inputPanel = new Box(BoxLayout.Y_AXIS);

    Box inputRow = new Box(BoxLayout.X_AXIS);

    // Create UserProfile DropDown
    JLabel userProfileLabel = new JLabel(Pooka.getProperty("UserProfile.label","User:"), SwingConstants.RIGHT);
    userProfileLabel.setPreferredSize(new Dimension(75,userProfileLabel.getPreferredSize().height));
    JComboBox profileCombo = new JComboBox(new Vector(Pooka.getPookaManager().getUserProfileManager().getUserProfileList()));


    IconManager iconManager = Pooka.getUIFactory().getIconManager();

    ImageIcon headerIcon = iconManager.getIcon(Pooka.getProperty("NewMessage.customHeader.button", "Hammer"));
    if (headerIcon != null) {
      java.awt.Image headerImage = headerIcon.getImage();
      headerImage = headerImage.getScaledInstance(15, 15, java.awt.Image.SCALE_SMOOTH);
      headerIcon.setImage(headerImage);
      customHeaderButton = new JToggleButton(headerIcon);
      customHeaderButton.setMargin(new java.awt.Insets(1,1,1,1));
      customHeaderButton.setSize(15,15);
    } else {
      customHeaderButton = new JToggleButton();
    }

    customHeaderButton.addChangeListener(new ChangeListener() {

        public void stateChanged(ChangeEvent e) {
          if (customHeaderButton.isSelected()) {
            selectCustomHeaderPane();
          } else {
            removeCustomHeaderPane();
          }
        }

      });

    customHeaderButton.setToolTipText(Pooka.getProperty("NewMessage.customHeaders.button.Tooltip", "Edit Headers"));

    inputRow.add(userProfileLabel);
    inputRow.add(profileCombo);
    inputRow.add(customHeaderButton);

    UserProfile selectedProfile = null;
    selectedProfile = pProxy.getDefaultProfile();
    if (selectedProfile == null)
      if (Pooka.getMainPanel() != null)
        selectedProfile = Pooka.getMainPanel().getCurrentUser();

    if (selectedProfile == null)
      selectedProfile = Pooka.getPookaManager().getUserProfileManager().getDefaultProfile();

    if (selectedProfile != null)
      profileCombo.setSelectedItem(selectedProfile);

    profileCombo.addItemListener(this);

    proptDict.put("UserProfile", profileCombo);

    inputPanel.add(inputRow);

    // Create Address panel

    StringTokenizer tokens = new StringTokenizer(Pooka.getProperty("MessageWindow.Input.DefaultFields", "To:CC:BCC:Subject"), ":");
    String currentHeader = null;
    JLabel hdrLabel = null;
    EntryTextArea inputField = null;

    while (tokens.hasMoreTokens()) {
      inputRow = new Box(BoxLayout.X_AXIS);
      currentHeader=tokens.nextToken();
      hdrLabel = new JLabel(Pooka.getProperty("MessageWindow.Input.." + currentHeader + ".label", currentHeader) + ":", SwingConstants.RIGHT);
      hdrLabel.setPreferredSize(new Dimension(75,hdrLabel.getPreferredSize().height));
      inputRow.add(hdrLabel);

      if (currentHeader.equalsIgnoreCase("To") || currentHeader.equalsIgnoreCase("CC") || currentHeader.equalsIgnoreCase("BCC") ) {
        try {
          inputField = new AddressEntryTextArea(getNewMessageUI(), getNewMessageProxy().getNewMessageInfo().getHeader(Pooka.getProperty("MessageWindow.Input." + currentHeader + ".MIMEHeader", "") , ","), 1, 30);
        } catch (MessagingException me) {
          inputField = new net.suberic.util.swing.EntryTextArea(1, 30);
        }
      } else {
        try {
          inputField = new net.suberic.util.swing.EntryTextArea(getNewMessageProxy().getNewMessageInfo().getHeader(Pooka.getProperty("MessageWindow.Input." + currentHeader + ".MIMEHeader", "") , ","), 1, 30);
        } catch (MessagingException me) {
          inputField = new net.suberic.util.swing.EntryTextArea(1, 30);
        }
      }

      inputField.setLineWrap(true);
      inputField.setWrapStyleWord(true);
      inputField.setBorder(BorderFactory.createEtchedBorder());
      inputField.addKeyListener(new KeyAdapter() {
          public void keyTyped(KeyEvent e) {
            setModified(true);
          }
        });


      inputRow.add(inputField);
      if (inputField instanceof AddressEntryTextArea) {
        //int height = inputField.getPreferredSize().height;
        JButton addressButton = ((AddressEntryTextArea)inputField).createAddressButton(10, 10);
        inputRow.add(Box.createHorizontalGlue());
        inputRow.add(addressButton);
      }
      inputPanel.add(inputRow);

      proptDict.put(Pooka.getProperty("MessageWindow.Input." + currentHeader + ".value", currentHeader), inputField);
    }

    return inputPanel;
  }

  /**
   * Extends a DefaultTableModel to make it so certain columns are not
   * editable.
   */
  class CustomHeaderTableModel extends javax.swing.table.DefaultTableModel {

    // Constructor
    public CustomHeaderTableModel(Vector headers, int rows) {
      super(headers,rows);
    }

    // the number of rows that have uneditable headers in it.
    int mUneditableRows = 0;

    /**
     * Sets the number of uneditable header rows we have.
     */
    public void setUneditableRows(int pRows) {
      mUneditableRows = pRows;
    }

    /**
     * Returns the number of uneditable header rows we have.
     */
    public int getUneditableRows() {
      return mUneditableRows;
    }

    /**
     * Returns whether or not this cell it editable.
     */
    public boolean isCellEditable(int row, int column) {
      if (column == 0 && row < mUneditableRows) {
        return false;
      } else {
        return true;
      }
    }

  }

  /**
   * This creates a new JTextPane for the main text part of the new
   * message.  It will also include the current text of the message.
   */
  public JTextPane createMessagePanel(MessageProxy pProxy) {
    JTextPane retval = new net.suberic.util.swing.ExtendedEditorPane();
    retval.setEditorKit(new MailEditorKit());

    setDefaultFont(retval);

    // see if this message already has a text part, and if so,
    // include it.

    String origText = ((NewMessageInfo)getMessageProxy().getMessageInfo()).getTextPart(false);
    if (origText != null && origText.length() > 0)
      retval.setText(origText);

    UserProfile profile = getSelectedProfile();
    if (profile.autoAddSignature) {
      retval.setCaretPosition(retval.getDocument().getLength());
      if (profile.signatureFirst) {

      }
      addSignature(retval);

    }

    return retval;

  }

  TransferHandler mTransferHandler = null;
  /**
   * Installs the TransferHandler for this component.
   */
  public void installTransferHandler() {
    if (editorPane != null && mTransferHandler == null) {
      TransferHandler defaultHandler = editorPane.getTransferHandler();

      net.suberic.pooka.gui.dnd.MultipleTransferHandler multiHandler = new net.suberic.pooka.gui.dnd.MultipleTransferHandler();
      multiHandler.addTransferHandler(defaultHandler);
      multiHandler.addTransferHandler(new net.suberic.pooka.gui.dnd.NewMessageTransferHandler());
      mTransferHandler = multiHandler;
    }

    if (editorPane != null)
      editorPane.setTransferHandler(mTransferHandler);
  }

  /**
   * This adds the current user's signature to the message at the current
   * location of the cursor.
   */
  public void addSignature(JEditorPane editor) {
    String sig = getSelectedProfile().getSignature();
    if (sig != null) {
      try {
        editor.getDocument().insertString(editor.getCaretPosition(), sig, null);
      } catch (javax.swing.text.BadLocationException ble) {
        ;
      }
    }
  }

  /**
   * This returns the values in the MesssageWindow as a set of
   * InternetHeaders.
   */
  public InternetHeaders getMessageHeaders() throws MessagingException {
    InternetHeaders returnValue = new InternetHeaders();
    String key;

    Enumeration keys = inputTable.keys();
    while (keys.hasMoreElements()) {
      key = (String)(keys.nextElement());

      if (! key.equals("UserProfile")) {
        String header = new String(Pooka.getProperty("MessageWindow.Header." + key + ".MIMEHeader", key));

        EntryTextArea inputField = (EntryTextArea) inputTable.get(key);
        String value = null;
        if (inputField instanceof AddressEntryTextArea) {
          value = ((AddressEntryTextArea) inputField).getParsedAddresses();
          value = ((NewMessageInfo)getMessageProxy().getMessageInfo()).convertAddressLine(value, getSelectedProfile());
        } else {
          value = ((EntryTextArea)(inputTable.get(key))).getText();
          value = value.replaceAll("\n", " ");
        }

        returnValue.setHeader(header, value);
      }
    }

    if (customHeaderButton.isSelected()) {
      populateCustomHeaders(returnValue);
    } else {
      UserProfile p = getSelectedProfile();
      p.populateHeaders(returnValue);
      returnValue.setHeader(Pooka.getProperty("Pooka.userProfileProperty", "X-Pooka-UserProfile"), p.getName());
    }

    return returnValue;
  }

  /**
   * This notifies the MessageDisplayPanel that an attachment has been added
   * at the provided index.  This does not actually add an attachment,
   * but rather should be called by the MessageProxy when an attachment
   * has been added.
   *
   * If an AttachmentPane does not currently exist for this
   * MessageDisplayPanel, this method will call addAttachmentPane() to
   * create one.
   */
  public void attachmentAdded(int index) {
    if (getAttachmentPanel() == null)
      addAttachmentPane();
    else
      getAttachmentPanel().getTableModel().fireTableRowsInserted(index, index);
  }

  /**
   * This notifies the MessageDisplayPanel that the attachment at the
   * provided index has been removed.  This does not actually remove
   * the attachment, but rather should be called by the MessageProxy
   * when an attachment has been removed.
   *
   * If this removes the last attachment, the entire AttachmentPane
   * is removed from the MessageDisplayPanel.
   */
  public void attachmentRemoved(int index) {
    try {
      java.util.List attach = getNewMessageProxy().getAttachments();
      if (attach == null || attach.size() == 0) {
        removeAttachmentPane();
      } else {
        getAttachmentPanel().getTableModel().fireTableRowsDeleted(index, index);
      }
    } catch (MessagingException me) {
    }
  }

  /**
   * This creates the JComponent which shows the attachments, and then
   * adds it to the JTabbedPane.
   *
   */
  public void addAttachmentPane() {
    attachmentPanel = new AttachmentPane(getMessageProxy());
    attachmentDisplayPanel = new JPanel();
    attachmentDisplayPanel.add(attachmentPanel);

    NewMessageUI nmui = getNewMessageUI();
    if (nmui instanceof net.suberic.util.swing.ThemeSupporter) {
      try {
        Pooka.getUIFactory().getPookaThemeManager().updateUI((net.suberic.util.swing.ThemeSupporter) nmui, attachmentDisplayPanel, true);
      } catch (Exception e) {
        if (Pooka.isDebug())
          System.out.println("error setting theme:  " + e);
      }
    }
    tabbedPane.add(attachmentDisplayPanel, Pooka.getProperty("MessageWindow.AttachmentTab", "Attachments"), 1);
  }

  /**
   * This creates the JComponent which shows the encryption status, and then
   * adds it to the JTabbedPane.
   *
   */
  public void addEncryptionPane() {
    cryptoPanel = new JPanel();
//    NewMessageCryptoDisplay nmcd = new NewMessageCryptoDisplay(getNewMessageProxy());
//    cryptoDisplay = nmcd;

    cryptoPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
    cryptoPanel.setSize(headerPanel.getSize());

    ((JPanel)cryptoPanel).setBorder(BorderFactory.createEtchedBorder());

//    cryptoPanel.add(nmcd);

    tabbedPane.add(Pooka.getProperty("MessageWindow.EncryptionTab", "Encryption"), cryptoPanel);
  }

  /**
   * Creates a new CustomHeaderPane.
   */
  public Container createCustomHeaderPane() {

    JPanel returnValue = new JPanel();
    returnValue.setLayout(new BorderLayout());

    Box customInputPanel = new Box(BoxLayout.Y_AXIS);

    Vector headerNames = new Vector();
    headerNames.add(Pooka.getProperty("NewMessage.customHeaders.header", "Header"));
    headerNames.add(Pooka.getProperty("NewMessage.customHeaders.value", "Value"));

    CustomHeaderTableModel dtm = new CustomHeaderTableModel(headerNames, 4);

    customHeaderTable = new JTable(dtm);

    // get the preconfigured properties

    Properties headers = new Properties();

    Properties mailProperties = getSelectedProfile().getMailProperties();
    Enumeration keys = mailProperties.propertyNames();

    String fromAddr = null, fromPersonal = null, replyAddr = null, replyPersonal = null;

    // we want to put From and Reply-To first.
    java.util.List otherProps = new ArrayList();

    while (keys.hasMoreElements()) {
      String key = (String)(keys.nextElement());

      if (key.equals("FromPersonal")) {
        fromPersonal = mailProperties.getProperty(key);
      } else if (key.equals("From")) {
        fromAddr = mailProperties.getProperty(key);
      } else if (key.equals("ReplyTo")) {
        replyAddr = mailProperties.getProperty(key);
      } else if (key.equals("ReplyToPersonal")) {
        replyPersonal = mailProperties.getProperty(key);
      } else {
        otherProps.add(key);
      }
    }

    try {
      if (fromAddr != null) {
        if (fromPersonal != null && !(fromPersonal.equals("")))
          headers.setProperty("From", new InternetAddress(fromAddr, fromPersonal).toString());
        else
          headers.setProperty("From", new InternetAddress(fromAddr).toString());
      } else {
        headers.setProperty("From", "");
      }

      if (replyAddr != null && !(replyAddr.equals(""))) {
        if (replyPersonal != null)
          headers.setProperty("Reply-To", new InternetAddress(replyAddr, replyPersonal).toString());
        else
          headers.setProperty("Reply-To", new InternetAddress(replyAddr).toString());
      } else {
        headers.setProperty("Reply-To", "");
      }
    } catch (java.io.UnsupportedEncodingException uee) {
      //don't bother
    } catch (javax.mail.MessagingException me) {
      //don't bother
    }

    String currentHeader = null;
    String currentValue = null;
    JLabel hdrLabel = null;

    javax.swing.table.DefaultTableModel model = (javax.swing.table.DefaultTableModel) customHeaderTable.getModel();

    model.setValueAt("From", 0, 0);
    model.setValueAt(headers.getProperty("From", ""), 0, 1);

    model.setValueAt("Reply-To", 1, 0);
    model.setValueAt(headers.getProperty("Reply-To", ""), 1, 1);

    int row = 2;
    Iterator it = otherProps.iterator();
    while(it.hasNext()) {
      currentHeader=(String) it.next();
      currentValue = (String) headers.get(currentHeader);
      if (currentValue == null)
        currentValue = "";

      if (model.getRowCount() <= row) {
        model.addRow(new Vector());
      }

      model.setValueAt(currentHeader, row, 0);
      model.setValueAt(currentValue, row, 1);

      row++;
    }

    dtm.setUneditableRows(2 + otherProps.size());

    customInputPanel.add(new JScrollPane(customHeaderTable));

    returnValue.add(customInputPanel, BorderLayout.CENTER);

    JPanel buttonPanel = new JPanel();
    buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));

    IconManager iconManager = Pooka.getUIFactory().getIconManager();

    ImageIcon headerIcon = iconManager.getIcon(Pooka.getProperty("NewMessage.customHeader.add.button", "Plus"));

    JButton headerButton = null;

    if (headerIcon != null) {
      headerButton = new JButton(headerIcon);
      headerButton.setMargin(new java.awt.Insets(0, 0, 0, 0));
      headerButton.setSize(new java.awt.Dimension(10,10));
    } else {
      headerButton = new JButton();
    }

    headerButton.addActionListener(new AbstractAction() {
        public void actionPerformed(ActionEvent e) {
          javax.swing.table.DefaultTableModel model = (javax.swing.table.DefaultTableModel) customHeaderTable.getModel();
          model.addRow(new Vector());
        }
      });

    buttonPanel.add(new JLabel("Add Header"));
    buttonPanel.add(headerButton);
    buttonPanel.setBorder(BorderFactory.createEmptyBorder());

    buttonPanel.setSize(buttonPanel.getMinimumSize());
    returnValue.add(buttonPanel, BorderLayout.SOUTH);
    return returnValue;

  }

  /**
   * Populates an InternetHeaders object from the CustomHeaderPane.
   */
  public void populateCustomHeaders(InternetHeaders pHeaders) {
    if (customHeaderTable.isEditing()) {
      javax.swing.table.TableCellEditor tce = customHeaderTable.getCellEditor(customHeaderTable.getEditingRow(), customHeaderTable.getEditingColumn());
      if (tce != null) {
        tce.stopCellEditing();
      }
    }

    int rowCount = customHeaderTable.getRowCount();
    for (int i = 0; i < rowCount; i++) {
      String header = null;
      String value = null;
      try {
        header = (String) customHeaderTable.getValueAt(i, 0);
        value = (String) customHeaderTable.getValueAt(i, 1);
      } catch (ClassCastException cce) {
        // ignore the header.
      }

      if (header != null && value != null && header.length() > 0 && value.length() > 0) {
        pHeaders.setHeader(header, value);
      }
    }
  }

  /**
   * Selects the custom header pane.
   */
  public void selectCustomHeaderPane() {
    if (customHeaderPane == null) {
      customHeaderPane = createCustomHeaderPane();
      tabbedPane.add(Pooka.getProperty("MessageWindow.CustomHeaderTab", "Custom"), customHeaderPane);
    }
    tabbedPane.setSelectedComponent(customHeaderPane);
  }

  /**
   * Removes the custom header pane, if any.
   */
  public void removeCustomHeaderPane() {
    if (customHeaderPane != null) {
      tabbedPane.setSelectedComponent(headerScrollPane);

      tabbedPane.remove(customHeaderPane);
    }
    customHeaderPane = null;
  }

  /**
   * This removes the AttachmentPane from the JTabbedPane.
   */

  public void removeAttachmentPane() {
    if (attachmentPanel != null) {
      tabbedPane.setSelectedComponent(headerScrollPane);
      tabbedPane.remove(attachmentDisplayPanel);
    }
    attachmentPanel = null;
    attachmentDisplayPanel = null;
  }

  /**
   * This registers the Keyboard action not only for the FolderWindow
   * itself, but also for pretty much all of its children, also.  This
   * is to work around something which I think is a bug in jdk 1.2.
   * (this is not really necessary in jdk 1.3.)
   *
   * Overrides JComponent.registerKeyboardAction(ActionListener anAction,
   *            String aCommand, KeyStroke aKeyStroke, int aCondition)
   */

  public void registerKeyboardAction(ActionListener anAction,
                                     String aCommand, KeyStroke aKeyStroke, int aCondition) {
    super.registerKeyboardAction(anAction, aCommand, aKeyStroke, aCondition);

    if (attachmentPanel != null)
      attachmentPanel.registerKeyboardAction(anAction, aCommand, aKeyStroke, aCondition);
    editorPane.registerKeyboardAction(anAction, aCommand, aKeyStroke, aCondition);
    editorScrollPane.registerKeyboardAction(anAction, aCommand, aKeyStroke, aCondition);

    splitPane.registerKeyboardAction(anAction, aCommand, aKeyStroke, aCondition);
  }

  /**
   * This unregisters the Keyboard action not only for the FolderWindow
   * itself, but also for pretty much all of its children, also.  This
   * is to work around something which I think is a bug in jdk 1.2.
   * (this is not really necessary in jdk 1.3.)
   *
   * Overrides JComponent.unregisterKeyboardAction(KeyStroke aKeyStroke)
   */

  public void unregisterKeyboardAction(KeyStroke aKeyStroke) {
    super.unregisterKeyboardAction(aKeyStroke);

    if (attachmentPanel != null)
      attachmentPanel.unregisterKeyboardAction(aKeyStroke);
    editorPane.unregisterKeyboardAction(aKeyStroke);
    editorScrollPane.unregisterKeyboardAction(aKeyStroke);
    splitPane.unregisterKeyboardAction(aKeyStroke);
  }

  /**
   * This creates and shows a PopupMenu for this component.
   */
  public void showPopupMenu(JComponent component, MouseEvent e) {
    ConfigurablePopupMenu popupMenu = new ConfigurablePopupMenu();
    popupMenu.configureComponent("NewMessageWindow.popupMenu", Pooka.getResources());
    popupMenu.setActive(getActions());
    NewMessageUI nmui = getNewMessageUI();
    if (nmui instanceof net.suberic.util.swing.ThemeSupporter) {
      try {
        Pooka.getUIFactory().getPookaThemeManager().updateUI((net.suberic.util.swing.ThemeSupporter) nmui, popupMenu, true);
      } catch (Exception etwo) {
        if (Pooka.isDebug())
          System.out.println("error setting theme:  " + e);
      }
    }
    popupMenu.show(component, e.getX(), e.getY());

  }

  /**
   * As specified by interface net.suberic.pooka.UserProfileContainer.
   *
   * This implementation returns the DefaultProfile of the associated
   * MessageProxy if the MessageDisplayPanel is not editable.  If the
   * MessageDisplayPanel is editable, it returns the currently selected
   * UserProfile object.
   */
  public UserProfile getDefaultProfile() {
    if (isEditable())
      return getSelectedProfile();
    else
      return getMessageProxy().getDefaultProfile();
  }


  /**
   * This method returns the UserProfile currently selected in the
   * drop-down menu.
   */

  public UserProfile getSelectedProfile() {
    return (UserProfile)(((JComboBox)(inputTable.get("UserProfile"))).getSelectedItem());
  }

  /**
   * sets the currently selected Profile.
   */
  public void setSelectedProfile(UserProfile newProfile) {
    if (newProfile != null) {
      ((JComboBox)(inputTable.get("UserProfile"))).setSelectedItem(newProfile);
    }
  }

  /**
   * Overrides JComponent.addNotify().
   *
   * We override addNotify() here to set the proper splitPane location.
   */

  public void addNotify() {
    super.addNotify();
    splitPane.setDividerLocation(Math.min(tabbedPane.getPreferredSize().height + 1, Integer.parseInt(Pooka.getProperty("MessageWindow.headerPanel.vsize", "500"))));
  }

  public boolean isEditable() {
    return true;
  }

  public boolean isModified() {
    return modified;
  }

  public void setModified(boolean mod) {
    if (isEditable())
      modified=mod;
  }

  /**
   * Returns the MessageProxy as a NewMessageProxy.
   */
  public NewMessageProxy getNewMessageProxy() {
    return (NewMessageProxy) getMessageProxy();
  }

  /**
   * Returns the MessageUI as a NewMessageUI.
   */
  public NewMessageUI getNewMessageUI() {
    return (NewMessageUI) getMessageUI();
  }

  /**
   * Shows the current display of the encryption status.
   */
  public net.suberic.pooka.gui.crypto.CryptoStatusDisplay getCryptoStatusDisplay() {
    if (cryptoDisplay == null) {
      addEncryptionPane();
    }

    return cryptoDisplay;
  }

  //------- Actions ----------//

  /**
   * performTextAction grabs the focused component on the MessageDisplayPanel
   * and, if it is a JTextComponent, tries to get it to perform the
   * appropriate ActionEvent.
   */
  public void performTextAction(String name, ActionEvent e) {
    Action[] textActions;

    Component focusedComponent = getFocusedComponent(this);

    // this is going to suck more.

    if (focusedComponent != null) {
      if (focusedComponent instanceof JTextComponent) {
        JTextComponent fTextComp = (JTextComponent) focusedComponent;
        textActions = fTextComp.getActions();
        Action selectedAction = null;
        for (int i = 0; (selectedAction == null) && i < textActions.length; i++) {
          if (textActions[i].getValue(Action.NAME).equals(name))
            selectedAction = textActions[i];
        }

        if (selectedAction != null) {
          selectedAction.actionPerformed(e);
        }
      }
    }
  }

  private Component getFocusedComponent(Container container) {
    Component[] componentList = container.getComponents();

    Component focusedComponent = null;

    // this is going to suck.

    for (int i = 0; (focusedComponent == null) && i < componentList.length; i++) {
      if (componentList[i].hasFocus())
        focusedComponent = componentList[i];
      else if (componentList[i] instanceof Container)
        focusedComponent=getFocusedComponent((Container)componentList[i]);

    }

    return focusedComponent;

  }

  public Hashtable getInputTable() {
    return inputTable;
  }

  public void setInputTable(Hashtable newInputTable) {
    inputTable = newInputTable;
  }

  public Action[] getActions() {
    Action[] returnValue = getDefaultActions();

    if (getMessageProxy().getActions() != null) {
      if (returnValue != null) {
        returnValue = TextAction.augmentList(getMessageProxy().getActions(), returnValue);
      } else {
        returnValue = getMessageProxy().getActions();
      }
    }

    if (getEditorPane() != null && getEditorPane().getActions() != null) {
      if (returnValue != null) {
        returnValue = TextAction.augmentList(getEditorPane().getActions(), returnValue);
      } else {
        returnValue = getEditorPane().getActions();
      }
    }

    return returnValue;
  }

  /**
   * Sets this editor as enabled or disabled.
   */
  public void setEnabled(boolean enabled) {
    super.setEnabled(enabled);
    getEditorPane().setEnabled(enabled);
  }

  public Action[] getDefaultActions() {
    return defaultActions;
  }

  private void createDefaultActions() {
    // The actions supported by the window itself.

    /*defaultActions = new Action[] {
      new CloseAction(),
      new CutAction(),
      new CopyAction(),
      new PasteAction(),
      new TestAction()
      };*/

    defaultActions = new Action[] {
      new AddSignatureAction(),
      new EditorPanelAction(),
      new AttachmentPanelAction(),
      new TestAction()
    };
  }

  //-----------actions----------------

  class AddSignatureAction extends AbstractAction {

    AddSignatureAction() {
      super("message-add-signature");
    }

    public void actionPerformed(ActionEvent e) {
      addSignature(editorPane);
    }
  }

  class CutAction extends AbstractAction {

    CutAction() {
      super("cut-to-clipboard");
    }

    public void actionPerformed(ActionEvent e) {
      performTextAction((String)getValue(Action.NAME), e);
    }
  }

  class CopyAction extends AbstractAction {

    CopyAction() {
      super("copy-to-clipboard");
    }

    public void actionPerformed(ActionEvent e) {
      performTextAction((String)getValue(Action.NAME), e);
    }
  }

  class PasteAction extends AbstractAction {

    PasteAction() {
      super("paste-from-clipboard");
    }

    public void actionPerformed(ActionEvent e) {
      performTextAction((String)getValue(Action.NAME), e);
    }
  }

  class TestAction extends AbstractAction {

    TestAction() {
      super("test");
    }

    public void actionPerformed(ActionEvent e) {
      System.out.println(net.suberic.pooka.MailUtilities.wrapText(getMessageText()));
    }
  }

  /**
   * Selects the Attachment panel.
   */
  public class AttachmentPanelAction extends AbstractAction {
    AttachmentPanelAction() {
      super("message-select-attachment");
    }

    public void actionPerformed(ActionEvent e) {
      if (attachmentPanel != null) {
        tabbedPane.setSelectedComponent(attachmentDisplayPanel);
        attachmentPanel.requestFocusInWindow();
      }
    }
  }

  /**
   * Selects the Editor panel.
   */
  public class EditorPanelAction extends AbstractAction {
    EditorPanelAction() {
      super("message-select-editor");
    }

    public void actionPerformed(ActionEvent e) {
      tabbedPane.setSelectedComponent(headerScrollPane);
    }
  }

  /**
   * Creates a new CustomHeaderEditorPane if there is not one alrady, and
   * then selects it.
   */
  public class CustomHeaderPanelAction extends AbstractAction {
    CustomHeaderPanelAction() {
      super("message-custom-headers");
    }

    public void actionPerformed(ActionEvent e) {
      customHeaderButton.setSelected(true);
    }
  }

}





