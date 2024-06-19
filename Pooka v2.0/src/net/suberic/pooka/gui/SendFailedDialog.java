package net.suberic.pooka.gui;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.util.*;

import javax.mail.MessagingException;
import net.suberic.pooka.*;

/**
 * A dialog that lets you choose whehter to cancel the sending or try
 * another mailserver.
 */
public class SendFailedDialog extends JPanel {

  // the resource for this component.
  static String P_RESOURCE = "SendFailedDialog";

  // the action commands
  static String S_ABORT = "abort";
  static String S_SEND_OTHER_SERVER = "send";
  static String S_SAVE_TO_OUTBOX = "outbox";

  // the mailserver action commands
  public static String S_NOTHING = "nothing";
  public static String S_SESSION_DEFAULT = "session";
  public static String S_CHANGE_DEFAULT = "change_default";

  // the MessagingException
  MessagingException mException;

  // the original mailserver
  OutgoingMailServer mOriginalMailServer;

  // the display panel.
  JTextArea mMessageDisplay;

  // a JComboBox that shows all available mailservers.
  JComboBox mMailServerList = null;

  // a JRadioButton that shows the choices of what to do with the failed
  // send.
  ButtonGroup mActionButtons = null;

  // a JRadioButton that shows the choices of what to do with the newly
  // chosen mailserver.
  ButtonGroup mServerDefaultButtons = null;

  boolean sendButtonSelected = false;

  /**
   * Creates a new SendFailedDialog.
   */
  public SendFailedDialog(OutgoingMailServer pServer, MessagingException me) {
    mException = me;
    mOriginalMailServer = pServer;
  } 
  
  /**
   * Configures this component.
   */
  public void configureComponent() {
    this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

    JPanel messagePanel = new JPanel();
    mMessageDisplay = new JTextArea(Pooka.getProperty("error.MessageUI.sendFailed", "Failed to send Message.") + "\n" + mException.getMessage());
    JLabel testLabel = new JLabel();
    
    mMessageDisplay.setBackground(testLabel.getBackground());
    mMessageDisplay.setFont(testLabel.getFont());
    mMessageDisplay.setForeground(testLabel.getForeground());
    mMessageDisplay.setEditable(false);

    messagePanel.add(mMessageDisplay);

    JPanel buttonPanel = createActionPanel();

    JPanel actionPanel = createServerDefaultPanel();

    mMailServerList = createMailServerList();

    JPanel choicePanel = new JPanel();
    choicePanel.setBorder(BorderFactory.createEtchedBorder());
    choicePanel.setLayout(new BoxLayout(choicePanel, BoxLayout.X_AXIS));

    choicePanel.add(buttonPanel);

    Box mailServerListBox = new Box(BoxLayout.Y_AXIS);
    mailServerListBox.add(Box.createVerticalGlue());
    mailServerListBox.add(mMailServerList);
    mailServerListBox.add(Box.createVerticalGlue());
    choicePanel.add(mailServerListBox);
    choicePanel.add(actionPanel);

    this.add(messagePanel);
    this.add(choicePanel);
    this.add(new net.suberic.util.swing.ExceptionDisplayPanel(Pooka.getProperty("error.showStackTrace", "Stack Trace"), mException));

    reactivatePanels();
  }

  /**
   * Creates a JRadioButton to show the available actions.
   */
  protected JPanel createActionPanel() {
    JPanel returnValue = new JPanel();
    returnValue.setBorder(BorderFactory.createEtchedBorder());
    returnValue.setLayout(new BoxLayout(returnValue, BoxLayout.Y_AXIS));

    ButtonGroup choices = new ButtonGroup();

    JRadioButton abortButton = new JRadioButton();
    abortButton.setText(Pooka.getProperty(P_RESOURCE + ".cancel", "Cancel send"));
    abortButton.setActionCommand(S_ABORT);
    abortButton.setSelected(true);
    choices.add(abortButton);
    returnValue.add(abortButton);

    final JRadioButton sendButton = new JRadioButton();
    sendButton.setText(Pooka.getProperty(P_RESOURCE + ".send", "Send using another server"));
    sendButton.setActionCommand(S_SEND_OTHER_SERVER);
    choices.add(sendButton);
    returnValue.add(sendButton);

    JRadioButton outboxButton = new JRadioButton();
    outboxButton.setText(Pooka.getProperty(P_RESOURCE + ".outbox", "Save to outbox"));
    outboxButton.setActionCommand(S_SAVE_TO_OUTBOX);
    choices.add(outboxButton);
    returnValue.add(outboxButton);

    mActionButtons = choices;

    sendButton.addChangeListener(new ChangeListener() {
	public void stateChanged(ChangeEvent ce) {
	  synchronized(sendButton) {
	    boolean selected = sendButton.isSelected();
	    if (selected != sendButtonSelected) {
	      sendButtonSelected = selected;
	      reactivatePanels();
	    }
	  }
	}
      });
    
    return returnValue;
  }

  /**
   * Creates a JComboBox to show the choices of mailservers.
   */
  public JComboBox createMailServerList() {
    Vector v = Pooka.getOutgoingMailManager().getOutgoingMailServerList();
    Iterator it = v.iterator();
    Vector idList = new Vector();
    while (it.hasNext()) {
      OutgoingMailServer current = (OutgoingMailServer) it.next();
      idList.add(current.getItemID());
    }
    JComboBox returnValue = new JComboBox(idList);
    if (mOriginalMailServer != null)
      returnValue.setSelectedItem(mOriginalMailServer.getItemID());
    
    return returnValue;
  }

  /**
   * Creates a JRadioButton to show the choices of what to do with the 
   * newly selected mailserver.
   */
  public JPanel createServerDefaultPanel() {

    JPanel returnValue = new JPanel();
    returnValue.setBorder(BorderFactory.createEtchedBorder());
    returnValue.setLayout(new BoxLayout(returnValue, BoxLayout.Y_AXIS));

    ButtonGroup choices = new ButtonGroup();

    JRadioButton current = new JRadioButton();
    current.setText(Pooka.getProperty(P_RESOURCE + ".noDefault", "Keep default"));
    current.setActionCommand(S_NOTHING);
    current.setSelected(true);
    choices.add(current);
    returnValue.add(current);

    current = new JRadioButton();
    current.setText(Pooka.getProperty(P_RESOURCE + ".defaultThisSession", "Set as default for this session"));
    current.setActionCommand(S_SESSION_DEFAULT);
    choices.add(current);
    returnValue.add(current);
 
    current = new JRadioButton();
    current.setText(Pooka.getProperty(P_RESOURCE + ".defaultPerm", "Set as default"));
    current.setActionCommand(S_CHANGE_DEFAULT);
    choices.add(current);
    returnValue.add(current);

    mServerDefaultButtons = choices;
    return returnValue;

  }

  /**
   * Sets the appropriate panels as selected.
   */
  public void reactivatePanels() {
    String actionCommand = mActionButtons.getSelection().getActionCommand();

    if (actionCommand == S_SEND_OTHER_SERVER) {
      mMailServerList.setEnabled(true);
      Enumeration elements = mServerDefaultButtons.getElements();
      while (elements.hasMoreElements()) {
	AbstractButton ab = (AbstractButton) elements.nextElement();
	ab.setEnabled(true);
      }
    } else {
      mMailServerList.setEnabled(false);
      Enumeration elements = mServerDefaultButtons.getElements();
      while (elements.hasMoreElements()) {
	AbstractButton ab = (AbstractButton) elements.nextElement();
	ab.setEnabled(false);
      }
    }
  }

  /**
   * Whether or not to try a resend, or just fail.
   */
  public boolean resendMessage() {
    if (mActionButtons.getSelection().getActionCommand() == S_SEND_OTHER_SERVER) {
      return true;
    } else {
      return false;
    }
  }

  /**
   * Wheter or not to save this message to the outbox for use later.
   */
  public boolean getSaveToOutbox() {
    
    if (mActionButtons.getSelection().getActionCommand() == S_SAVE_TO_OUTBOX) {
      return true;
    } else {
      return false;
    }

  }

  /**
   * The MailServer selected.
   */
  public OutgoingMailServer getMailServer() {
    String selectedValue = (String) mMailServerList.getSelectedItem();
    OutgoingMailServer returnValue = Pooka.getOutgoingMailManager().getOutgoingMailServer(selectedValue);
    return returnValue;
  }

  /**
   * What to do with the selected MailServer.
   */
  public String getMailServerAction() {
    String selectedValue = mServerDefaultButtons.getSelection().getActionCommand();
    return selectedValue;
  }
}

