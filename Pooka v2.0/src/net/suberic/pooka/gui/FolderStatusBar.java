package net.suberic.pooka.gui;
import net.suberic.pooka.*;
import javax.swing.*;
import java.awt.FlowLayout;
import net.suberic.pooka.event.*;
import javax.mail.event.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

/**
 * A status bar which shows things like the folder name, the number of
 * unread messages in a folder, etc.
 */
public class FolderStatusBar extends JPanel implements MessageCountListener, MessageChangedListener {
  FolderInfo folderInfo;
  JLabel folderLabel;
  JLabel messageCount;
  JPanel loaderPanel;
  JPanel gotoPanel;
  JTextField gotoInputField;
  LoadMessageTracker tracker = null;
  int mUnreadCount = 0;
  int mTotalCount = 0;

  public FolderStatusBar(FolderInfo newFolder) {
    folderInfo = newFolder;
    folderLabel = new JLabel(getFolderInfo().getFolderName());
    messageCount = new JLabel();
    updateMessageCount();
    loaderPanel = new JPanel();

    gotoPanel = new JPanel();
    gotoPanel.add(new JLabel(Pooka.getProperty("FolderStatusBar.goto", "Goto Message")));
    gotoInputField = new JTextField(5);
    gotoInputField.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          try {
            int msgNum = Integer.parseInt(e.getActionCommand());
            if (getFolderInfo() != null) {
              FolderDisplayUI fdui = getFolderInfo().getFolderDisplayUI();
              fdui.selectMessage(msgNum - 1);
            }
          } catch (NumberFormatException nfe) {

          }
          gotoInputField.selectAll();
        }
      });

    gotoInputField.registerKeyboardAction(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          FolderDisplayUI fdui = getFolderInfo().getFolderDisplayUI();
          if (fdui != null) {
            int nextMessage = fdui.selectNextMessage();
            gotoInputField.setText(Integer.toString(nextMessage));
            gotoInputField.selectAll();
          }
        }
      }, KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_DOWN, 0),  JComponent.WHEN_FOCUSED);

    gotoInputField.registerKeyboardAction(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          FolderDisplayUI fdui = getFolderInfo().getFolderDisplayUI();
          if (fdui != null) {
            int previousMessage = fdui.selectPreviousMessage();
            gotoInputField.setText(Integer.toString(previousMessage));
            gotoInputField.selectAll();
          }
        }
      }, KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_UP, 0),  JComponent.WHEN_FOCUSED);

    gotoPanel.add(gotoInputField);

    java.awt.GridBagConstraints constraints = new java.awt.GridBagConstraints();
    java.awt.GridBagLayout layout = new java.awt.GridBagLayout();
    constraints.weightx = 0.0;
    constraints.fill = java.awt.GridBagConstraints.VERTICAL;
    constraints.anchor = java.awt.GridBagConstraints.WEST;
    constraints.ipadx = 5;
    constraints.insets = new java.awt.Insets(0, 10, 0, 10);
    this.setLayout(layout);

    layout.setConstraints(folderLabel, constraints);
    this.add(folderLabel);
    JSeparator js = new JSeparator(SwingConstants.VERTICAL);
    layout.setConstraints(js, constraints);
    this.add(js);
    layout.setConstraints(messageCount, constraints);
    this.add(messageCount);
    js = new JSeparator(SwingConstants.VERTICAL);
    layout.setConstraints(js, constraints);
    this.add(js);
    constraints.fill = java.awt.GridBagConstraints.BOTH;
    constraints.gridwidth = java.awt.GridBagConstraints.RELATIVE;
    constraints.weightx = 1.0;
    layout.setConstraints(loaderPanel, constraints);
    this.add(loaderPanel);
    constraints.weightx = 0.0;
    constraints.fill = java.awt.GridBagConstraints.VERTICAL;
    constraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
    constraints.anchor = java.awt.GridBagConstraints.EAST;
    layout.setConstraints(gotoPanel, constraints);
    this.add(gotoPanel);
  }

  public void messageChanged(MessageChangedEvent mce) {
    if (mce.getMessageChangeType() == MessageChangedEvent.FLAGS_CHANGED) {
      updateMessageCount();
    }
  }

  public void messagesAdded(MessageCountEvent e) {
    updateMessageCount();
  }

  /**
   * Called when messages are removed from the monitored folder.
   *
   * Calls updateMessageCount().
   *
   * May be called from any thread.
   */
  public void messagesRemoved(MessageCountEvent e) {
    updateMessageCount();
  }

  /**
   * Updates the message count display on this FolderStatusBar.
   *
   * May be called from any thread.
   */
  public void updateMessageCount() {
    int newTotalCount = getFolderInfo().getMessageCount();
    int newUnreadCount = getFolderInfo().getUnreadCount();
    if (newTotalCount != mTotalCount || newUnreadCount != mUnreadCount) {
      SwingUtilities.invokeLater(new Runnable() {
          public void run() {
            int secTotalCount = getFolderInfo().getMessageCount();
            int secUnreadCount = getFolderInfo().getUnreadCount();

            if (secTotalCount != mTotalCount || secUnreadCount != mUnreadCount) {
              mTotalCount = secTotalCount;
              mUnreadCount = secUnreadCount;
              messageCount.setText(mUnreadCount + " " + Pooka.getProperty("FolderFolderStatusBar.unreadMessages", "Unread") + " / " + mTotalCount + " " + Pooka.getProperty("FolderFolderStatusBar.totalMessages", "Total"));
              messageCount.repaint();
            }
          }
        });
    }
  }

  /**
   * Activates the Goto Message dialog.  In this case, the method has
   * the JTextField with the 'Goto Message' label request focus.
   */
  public void activateGotoDialog() {
    gotoInputField.selectAll();
    gotoInputField.requestFocusInWindow();
  }

  public FolderInfo getFolderInfo() {
    return folderInfo;
  }

  public JPanel getLoaderPanel() {
    return loaderPanel;
  }

  public LoadMessageTracker getTracker() {
    return tracker;
  }

  public void setTracker(LoadMessageTracker newTracker) {
    tracker = newTracker;
  }
} // end class FolderFolderStatusBar
