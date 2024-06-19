package net.suberic.pooka.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.mail.MessagingException;

import javax.swing.*;
import javax.swing.text.TextAction;

import net.suberic.pooka.MessageCryptoInfo;
import net.suberic.pooka.MessageInfo;
import net.suberic.pooka.OperationCancelledException;
import net.suberic.pooka.Pooka;
import net.suberic.pooka.gui.crypto.CryptoPanel;
import net.suberic.pooka.gui.crypto.CryptoStatusDisplay;
import net.suberic.util.gui.ConfigurableKeyBinding;
import net.suberic.util.gui.ConfigurablePopupMenu;
import net.suberic.util.swing.HyperlinkMouseHandler;

public class ReadMessageDisplayPanel extends MessageDisplayPanel {
  private JTextPane headerPane = null;
  private JScrollPane headerScrollPane = null;

  private AttachmentPane attachmentPanel = null;
  private net.suberic.pooka.gui.crypto.CryptoStatusDisplay cryptoStatusDisplay = null;

  private SpringLayout layout;

  public boolean firstShow = true;

  private DisplayStyleComboBox displayCombo = null;
  private DisplayStyleComboBox headerCombo = null;

  Action[] defaultActions = new Action[] {
    new AttachmentPanelAction(),
    new FindAction(),
    new FindNextAction()
  };

  private static int BORDER = 2;

  /**
   * Creates an empty MessageDisplayPanel.
   */
  public ReadMessageDisplayPanel() {
    this(null);
  }

  /**
   * Creates a MessageDisplayPanel from the given Message.
   */
  public ReadMessageDisplayPanel(MessageUI newMsgUI) {
    super(newMsgUI);

    layout = new SpringLayout();

    this.setLayout(layout);

    this.addFocusListener(new FocusAdapter() {
        public void focusGained(FocusEvent e) {
          editorPane.requestFocusInWindow();
        }
      });

  }

  /**
   * Configures the MessageDisplayPanel.  This includes creating all
   * the necessary panels and populating those panels with the information
   * from the MessageProxy.
   */
  public void configureMessageDisplay() throws MessagingException, OperationCancelledException {
    headerPane = new JTextPane();
    headerPane.setEditable(false);
    headerPane.setBackground(Color.LIGHT_GRAY);

    headerScrollPane = new JScrollPane(headerPane, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

    //headerScrollPane.setVisible(true);

    this.add(headerScrollPane);

    // this.add(headerScrollPane);

    layout.putConstraint(SpringLayout.WEST, headerScrollPane, BORDER, SpringLayout.WEST, this);
    layout.putConstraint(SpringLayout.NORTH, headerScrollPane, BORDER, SpringLayout.NORTH, this);

    editorPane = new JTextPane();
    editorPane.setEditable(false);
    editorPane.addHyperlinkListener(new HyperlinkDispatcher());
    HyperlinkMouseHandler hmh = new HyperlinkMouseHandler(Integer.parseInt(Pooka.getProperty("Pooka.lineLength", "80")));
    editorPane.addMouseListener(hmh);
    editorPane.addMouseMotionListener(hmh);

    editorScrollPane = new JScrollPane(editorPane, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

    setDefaultFont(editorPane);

    this.add(editorScrollPane);
    layout.putConstraint(SpringLayout.NORTH, editorScrollPane, BORDER, SpringLayout.SOUTH, headerScrollPane);
    layout.putConstraint(SpringLayout.SOUTH, this, BORDER, SpringLayout.SOUTH, editorScrollPane);
    layout.putConstraint(SpringLayout.WEST, editorScrollPane, BORDER, SpringLayout.WEST, this);
    layout.putConstraint(SpringLayout.EAST, this, BORDER, SpringLayout.EAST, editorScrollPane);

    layout.getConstraints(headerScrollPane).setWidth(layout.getConstraints(editorScrollPane).getWidth());

    keyBindings = new ConfigurableKeyBinding(this, "ReadMessageWindow.keyBindings", Pooka.getResources());
    keyBindings.setActive(getActions());

    // add up and down arrow scrolling.
    KeyStroke upArrowStroke = KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_UP, 0);
    KeyStroke downArrowStroke = KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_DOWN, 0);
    KeyStroke leftArrowStroke = KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_LEFT, 0);
    KeyStroke rightArrowStroke = KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_RIGHT, 0);

    Action upArrowAction = new AbstractAction() {
        public void actionPerformed(ActionEvent e) {
          JScrollBar jsb = editorScrollPane.getVerticalScrollBar();
          jsb.setValue(jsb.getValue() - jsb.getBlockIncrement());
        }
      };

    Action downArrowAction = new AbstractAction() {
        public void actionPerformed(ActionEvent e) {
          JScrollBar jsb = editorScrollPane.getVerticalScrollBar();
          jsb.setValue(jsb.getValue() + jsb.getBlockIncrement());
        }
      };

    Action leftArrowAction = new AbstractAction() {
        public void actionPerformed(ActionEvent e) {
          JScrollBar jsb = editorScrollPane.getHorizontalScrollBar();
          if (jsb != null)
            jsb.setValue(jsb.getValue() - jsb.getBlockIncrement());
        }
      };

    Action rightArrowAction = new AbstractAction() {
        public void actionPerformed(ActionEvent e) {
          JScrollBar jsb = editorScrollPane.getHorizontalScrollBar();
          if (jsb != null)
            jsb.setValue(jsb.getValue() + jsb.getBlockIncrement());
        }
      };

    String upArrowKey = "message-scroll-up";
    String downArrowKey = "message-scroll-down";
    String leftArrowKey = "message-scroll-left";
    String rightArrowKey = "message-scroll-right";

    // add for main panel.

    InputMap newInputMap = new InputMap();
    ActionMap newActionMap = new ActionMap();

    newInputMap.put(upArrowStroke, upArrowKey);
    newActionMap.put(upArrowKey, upArrowAction);

    newInputMap.put(downArrowStroke, downArrowKey);
    newActionMap.put(downArrowKey, downArrowAction);

    newInputMap.put(leftArrowStroke, leftArrowKey);
    newActionMap.put(leftArrowKey, leftArrowAction);

    newInputMap.put(rightArrowStroke, rightArrowKey);
    newActionMap.put(rightArrowKey, rightArrowAction);

    InputMap editorInputMap = editorPane.getInputMap();
    ActionMap editorActionMap = editorPane.getActionMap();

    newInputMap.setParent(editorInputMap);
    newActionMap.setParent(editorActionMap);

    editorPane.setInputMap(JComponent.WHEN_FOCUSED, newInputMap);
    editorPane.setActionMap(newActionMap);

    // </scrolling>

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

    if (getMessageProxy() != null) {
      resetEditorText();
    }

  }


  /**
   * This sets the text of the editorPane to the content of the current
   * message.
   *
   * Should only be called from within the FolderThread for the message.
   *
   * Also updates the current keybindings.
   */
  public void resetEditorText() throws MessagingException, OperationCancelledException {
    // ok.  here's how this has to go:  we need to load the information from
    // the message on the message editor thread, but then actually do the
    // display changing on the awt event thread.  seem simple enough?

    // assume that we're actually on the FolderThread for now.

    if (getMessageProxy() != null) {
      MessageInfo msgInfo = getMessageProxy().getMessageInfo();

      StringBuffer messageText = new StringBuffer();

      String content = null;

      String contentType = "text/plain";

      boolean displayHtml = false;

      int msgDisplayMode = getMessageProxy().getDisplayMode();

      // figure out html vs. text
      if (Pooka.getProperty("Pooka.displayHtml", "").equalsIgnoreCase("true")) {
        if (msgInfo.isHtml()) {
          if (msgDisplayMode > MessageProxy.TEXT_ONLY)
            displayHtml = true;

        } else if (msgInfo.containsHtml()) {
          if (msgDisplayMode >= MessageProxy.HTML_PREFERRED)
            displayHtml = true;

        } else {
          // if we don't have any html, just display as text.
        }
      }

      //Original was true, changed to false by Liao
      boolean includeHeaders = false;
      boolean showFullheaders = showFullHeaders();

      // Get the header Information
      String header;
      if(showFullheaders){
        header = getFullHeaderInfo(msgInfo);
      } else {
        String list = Pooka.getProperty("MessageWindow.Header.DefaultHeaders", "From:To:CC:Date:Subject");
        header = getHeaderInfo(msgInfo, list);
      }
      headerPane.setText(header);

      headerPane.repaint();

      // set the content
      if (msgDisplayMode == MessageProxy.RFC_822) {
        content = msgInfo.getRawText();
      } else {
        if (displayHtml) {
          contentType = "text/html";

          if (Pooka.getProperty("Pooka.displayTextAttachments", "").equalsIgnoreCase("true")) {
            content = msgInfo.getHtmlAndTextInlines(includeHeaders, showFullheaders);
          } else {
            content = msgInfo.getHtmlPart(includeHeaders, showFullheaders);
          }

        } else {
          if (Pooka.getProperty("Pooka.displayTextAttachments", "").equalsIgnoreCase("true")) {
            // Is there only an HTML part?  Regardless, we've determined that
            // we will still display it as text.
            if (getMessageProxy().getMessageInfo().isHtml())
              content = msgInfo.getHtmlAndTextInlines(includeHeaders, showFullheaders);
            else
              content = msgInfo.getTextAndTextInlines(includeHeaders, showFullheaders);
          }
          else {
            // Is there only an HTML part?  Regardless, we've determined that
            // we will still display it as text.
            if (getMessageProxy().getMessageInfo().isHtml())
              content = msgInfo.getHtmlPart(includeHeaders, showFullheaders);
            else
              content = msgInfo.getTextPart(includeHeaders, showFullheaders);
          }
        }
      }

      if (content != null)
        messageText.append(content);

      final String finalMessageText = messageText.toString();
      final String finalContentType = contentType;
      hasAttachment = getMessageProxy().hasAttachments(false);
      final boolean hasEncryption = (getMessageProxy().getMessageInfo() == null) ? false : getMessageProxy().getMessageInfo().hasEncryption();
      final boolean contentIsNull = (content == null);

      SwingUtilities.invokeLater(new Runnable() {
          public void run() {
            if (getDisplayCombo() != null)
              getDisplayCombo().styleUpdated(getMessageProxy().getDisplayMode(), getMessageProxy().getHeaderMode());

            if (getHeaderCombo() != null && getHeaderCombo() != getDisplayCombo()) {
              getHeaderCombo().styleUpdated(getMessageProxy().getDisplayMode(), getMessageProxy().getHeaderMode());
            }

            clearVariableComponents();

            if (! contentIsNull) {
              try {
                editorPane.setContentType(finalContentType);
              } catch (Exception e) {
                // if we can't show the html, just set the type as text/plain.
                editorPane.setEditorKit(new javax.swing.text.StyledEditorKit());
              }
              //editorPane.setEditable(false);
              editorPane.setText(finalMessageText);
              editorPane.setCaretPosition(0);
            }

            if (hasAttachment) {
              attachmentPanel = new AttachmentPane(getMessageProxy());
              layout.putConstraint(SpringLayout.NORTH, attachmentPanel, BORDER, SpringLayout.SOUTH, editorScrollPane);
              layout.putConstraint(SpringLayout.SOUTH, ReadMessageDisplayPanel.this, BORDER, SpringLayout.SOUTH, attachmentPanel);
              layout.getConstraints(attachmentPanel).setWidth(layout.getConstraints(editorScrollPane).getWidth());

              ReadMessageDisplayPanel.this.add(attachmentPanel);

              // set the theme for the attachmentpanel.
              MessageUI mui = getMessageUI();
              if (mui instanceof net.suberic.util.swing.ThemeSupporter) {
                try {
                  Pooka.getUIFactory().getPookaThemeManager().updateUI((net.suberic.util.swing.ThemeSupporter) mui, attachmentPanel, true);
                } catch (Exception etwo) {
                  java.util.logging.Logger.getLogger("Pooka.debug.gui").fine("error setting theme:  " + etwo);
                }
              }

            }

            if (hasEncryption) {
              CryptoPanel cp = new CryptoPanel();
              cryptoStatusDisplay = cp;

              layout.putConstraint(SpringLayout.WEST, cp, BORDER, SpringLayout.EAST, headerScrollPane);
              layout.putConstraint(SpringLayout.NORTH, cp, BORDER, SpringLayout.NORTH, ReadMessageDisplayPanel.this);
              layout.getConstraints(headerScrollPane).setWidth(Spring.sum(layout.getConstraints(editorScrollPane).getWidth(), Spring.minus(Spring.sum(Spring.constant(BORDER), layout.getConstraints(cp).getWidth()))));

              MessageCryptoInfo cryptoInfo = getMessageProxy().getMessageInfo().getCryptoInfo();
              if (cryptoInfo != null)
                cp.cryptoUpdated(cryptoInfo);

              ReadMessageDisplayPanel.this.add(cp);
            }
            layout.layoutContainer(ReadMessageDisplayPanel.this);
            ReadMessageDisplayPanel.this.repaint();
          }
        });
    } else {
      SwingUtilities.invokeLater(new Runnable() {
          public void run() {
            // if getMessageProxy() == null
            //editorPane.setEditable(false);
            editorPane.setText("");
            editorPane.setCaretPosition(0);

            headerPane.setText("");

            clearVariableComponents();

            layout.layoutContainer(ReadMessageDisplayPanel.this);
          }
        });
    }

    keyBindings.setActive(getActions());

    SwingUtilities.invokeLater(new Runnable() {
        public void run() {
          ReadMessageDisplayPanel.this.repaint();
        }
      });
  }

  /**
   * Shows whether or now we want to show the full headers.
   */
  public boolean showFullHeaders() {
    if (getMessageProxy() != null)
      return (getMessageProxy().getHeaderMode() == MessageProxy.HEADERS_FULL);
    else
      return false;
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

  public void registerKeyboardAction(ActionListener anAction, String aCommand, KeyStroke aKeyStroke, int aCondition) {
    super.registerKeyboardAction(anAction, aCommand, aKeyStroke, aCondition);

    if (attachmentPanel != null)
      attachmentPanel.registerKeyboardAction(anAction, aCommand, aKeyStroke, aCondition);

    editorPane.registerKeyboardAction(anAction, aCommand, aKeyStroke, aCondition);
    editorScrollPane.registerKeyboardAction(anAction, aCommand, aKeyStroke, aCondition);
    headerPane.registerKeyboardAction(anAction, aCommand, aKeyStroke, aCondition);
    headerScrollPane.registerKeyboardAction(anAction, aCommand, aKeyStroke, aCondition);
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

    if (attachmentPanel != null) {
      attachmentPanel.unregisterKeyboardAction(aKeyStroke);
    }

    editorPane.unregisterKeyboardAction(aKeyStroke);
    editorScrollPane.unregisterKeyboardAction(aKeyStroke);
    headerPane.unregisterKeyboardAction(aKeyStroke);
    headerScrollPane.unregisterKeyboardAction(aKeyStroke);
  }

  /**
   * This creates and shows a PopupMenu for this component.
   */
  public void showPopupMenu(JComponent component, MouseEvent e) {
    ConfigurablePopupMenu popupMenu = new ConfigurablePopupMenu();
    popupMenu.configureComponent("ReadMessageWindow.popupMenu", Pooka.getResources());
    popupMenu.setActive(getActions());
    MessageUI mui = getMessageUI();
    if (mui instanceof net.suberic.util.swing.ThemeSupporter) {
      try {
        Pooka.getUIFactory().getPookaThemeManager().updateUI((net.suberic.util.swing.ThemeSupporter) mui, popupMenu, true);
      } catch (Exception etwo) {
        java.util.logging.Logger.getLogger("Pooka.debug.gui").fine("error setting theme:  " + e);
      }
    }
    popupMenu.show(component, e.getX(), e.getY());

  }

  /**
   * This sets the size of the MessageDisplayPanel to a reasonable
   * default value.
   */
  public void sizeToDefault() {
    Dimension prefSize = getDefaultEditorPaneSize();
    if (editorPane != null && editorScrollPane != null) {
      JScrollBar vsb = editorScrollPane.getVerticalScrollBar();

      if (vsb != null)
        prefSize.setSize(prefSize.getWidth() + vsb.getPreferredSize().getWidth(), prefSize.getHeight());

      editorScrollPane.setPreferredSize(prefSize);

      this.setPreferredSize(prefSize);

      // Set the header pane size
      Dimension headerSize = new Dimension((int) prefSize.getWidth(), 100);
      headerPane.setPreferredSize(headerSize);

    }
    else {
      this.setSize(prefSize);
    }
  }

  /**
   * This sets the default font for the editorPane to a font determined
   * by the MessageWindow.editorPane.font (.name and .size) properties.
   *
   * I believe that if the font cannot be found or instantiated,
   * nothing should happen, but i'm not sure.  :)
   */
  public void setDefaultFont() {
    if (editorPane != null)
      setDefaultFont(editorPane);
  }


  public void addNotify() {
    super.addNotify();

    if (firstShow) {
      sizeToDefault();
      firstShow = false;
    }

  }

  public DisplayStyleComboBox getDisplayCombo() {
    return displayCombo;
  };
  public void setDisplayCombo(DisplayStyleComboBox dscb) {
    displayCombo = dscb;
  }
  public DisplayStyleComboBox getHeaderCombo() {
    return headerCombo;
  };
  public void setHeaderCombo(DisplayStyleComboBox dscb) {
    headerCombo = dscb;
  }

  /**
   * Shows the current display of the encryption status.
   */
  public net.suberic.pooka.gui.crypto.CryptoStatusDisplay getCryptoStatusDisplay() {
    return cryptoStatusDisplay;
  }

  /**
   * Sets the current display of the encryption status.
   */
  public void setCryptoStatusDisplay(net.suberic.pooka.gui.crypto.CryptoStatusDisplay newDisplay) {
    cryptoStatusDisplay = newDisplay;
  }


  private Map headerLinesToMap(Vector headerLines){
    Map map = new HashMap();
    for (int i = 0; i < headerLines.size(); i++) {
      String headerline = (String) headerLines.get(i);
      int offset = headerline.indexOf(':');
      String name = headerline.substring(0, offset).trim().toLowerCase();
      map.put(name, headerline);
    }
    return map;
  }

  protected String getFullHeaderInfo(MessageInfo msgInfo)
    throws MessagingException{
    StringBuffer sb = new StringBuffer();
    Vector headerlines = msgInfo.getHeaderLines();

    int offset;
    String name, value;
    for (int i = 0; i < headerlines.size(); i++) {
      String headerline = (String) headerlines.get(i);
      offset = headerline.indexOf(':');
      name = headerline.substring(0, offset).trim();
      value = headerline.substring(offset+1).trim();

      sb.append(name + ": " + value);
      sb.append("\r\n");
    }

    return sb.toString();
  }

  protected String getHeaderInfo(MessageInfo msgInfo, String list)
    throws MessagingException{
    StringBuffer sb = new StringBuffer();
    Map headers = headerLinesToMap(msgInfo.getHeaderLines());

    int offset;
    String name, value;

    StringTokenizer tokenizer = new StringTokenizer(list, ":");

    while(tokenizer.hasMoreTokens()){
      String token = tokenizer.nextToken();
      String headerline = (String) headers.get(token.toLowerCase());
      if(headerline != null){
        offset = headerline.indexOf(':');
        name = headerline.substring(0, offset).trim();
        value = headerline.substring(offset+1).trim();

        sb.append(name + ": " + value);
        sb.append("\r\n");
      }
    }
    return sb.toString();
  }

  private void clearVariableComponents() {
    if (attachmentPanel != null) {
      this.remove(attachmentPanel);
      layout.removeLayoutComponent(attachmentPanel);
      layout.putConstraint(SpringLayout.SOUTH, ReadMessageDisplayPanel.this, BORDER, SpringLayout.SOUTH, editorScrollPane);
      attachmentPanel = null;
    }

    if (cryptoStatusDisplay != null && cryptoStatusDisplay instanceof JComponent) {
      this.remove((JComponent) cryptoStatusDisplay);
      layout.removeLayoutComponent((JComponent) cryptoStatusDisplay);
      layout.getConstraints(headerScrollPane).setWidth(layout.getConstraints(editorScrollPane).getWidth());
      cryptoStatusDisplay = null;
    }
  }


  //------- Actions ----------//

  /**
   * Returns this panel's actions.
   */
  public Action[] getActions() {

    Action[] actionList = defaultActions;

    if (getMessageProxy() != null)
      actionList = TextAction.augmentList(actionList, getMessageProxy().getActions());

    Action[] subActions = null;

    // if we have an attachment pane, we need to check to see if the
    // attachment pane is selected or not.

    Component focusOwner = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
    if (hasAttachment && focusOwner != null && attachmentPanel != null && SwingUtilities.isDescendingFrom(focusOwner, attachmentPanel)) {
      subActions = attachmentPanel.getActions();
    } else {
      if (editorPane != null) {
        subActions = editorPane.getActions();
      }
    }

    if (subActions != null)
      return TextAction.augmentList(actionList, subActions);
    else
      return actionList;
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
        attachmentPanel.requestFocusInWindow();
      }
    }
  }

  /**
   * Finds the given String in the Message body.
   */
  public class FindAction extends AbstractAction {
    FindAction() {
      super("message-find");
    }

    public void actionPerformed(ActionEvent e) {
      searchMessage();
    }
  }

  /**
   * Finds the given String in the Message body.
   */
  public class FindNextAction extends AbstractAction {
    FindNextAction() {
      super("message-find-next");
    }

    public void actionPerformed(ActionEvent e) {
      searchAgain();
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
      if (editorPane != null)
        editorPane.requestFocusInWindow();
    }
  }

}
