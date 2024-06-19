package net.suberic.pooka.gui;

import javax.mail.*;
import java.net.InetAddress;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;

import net.suberic.pooka.Pooka;

/**
 * Authenticates.
 */
public class LoginAuthenticator extends AuthenticatorUI {

  Thread mCallerThread;
  SpringLayout layout;

  JPanel mPanel;
  JDialog mDialog;
  String mUsername;
  JPasswordField mPasswordField;
  JButton mOkButton;
  JButton mCancelButton;
  JLabel mStatusDisplay;

  public LoginAuthenticator() {
  }

  protected PasswordAuthentication getPasswordAuthentication() {

    if (mDialog == null) {
      mDialog = createDialog();
    }

    if (! isCancelled())
      showAuthenticator();

    if (isCancelled()) {
      return null;
    } else {
      return new PasswordAuthentication(mUsername, new String(mPasswordField.getPassword()));
    }
  }

  public Frame getFrame() {
    MainPanel mp= net.suberic.pooka.Pooka.getMainPanel();
    if (mp != null)
      return mp.getParentFrame();
    else
      return null;
  }

  /**
   * Sets an error message to display.
   */
  public void setErrorMessage(String pMessage, Exception pException) {
    // ?? show possible stack trace?
    final String fMessage = pMessage;
    EventQueue.invokeLater(new Runnable() {
        public void run() {
          mStatusDisplay.setForeground(Color.RED);
          mStatusDisplay.setText(fMessage);
          mDialog.pack();
          setEnabled(true);
          mPasswordField.requestFocusInWindow();
        }
      });
  }

  /**
   * Shows.
   */
  public void showAuthenticator() {
    if (EventQueue.isDispatchThread()) {
      // should throw exception or error
      return;
    }

    setCancelled(false);

    if (! mShowing) {
      mCallerThread = Thread.currentThread();
      EventQueue.invokeLater(new Runnable() {
          public void run() {
            mPasswordField.requestFocusInWindow();
            setEnabled(true);
            mShowing = true;
            mDialog.setVisible(true);
          }
        });
    } else {
      EventQueue.invokeLater(new Runnable() {
          public void run() {
            setEnabled(true);
          }
        });
    }

    while (mDialog.isEnabled() && ! isCancelled()) {
      try {
        mCallerThread.sleep(30000);
      } catch (InterruptedException ie) {
        // return on interrupt.
      }
    }
  }

  private JDialog createDialog() {
    // protocol
    String protocol = getRequestingProtocol();
    if (protocol == null)
      protocol = Pooka.getProperty("inof.login.unknownProtocol", "Unknown protocol");

    // get the host
    String host = null;
    InetAddress inet = getRequestingSite();
    if (inet != null)
      host = inet.getHostName();
    if (host == null)
      host = Pooka.getProperty("info.login.unknownHost", "Unknown Host");

    // port
    String port = "";
    int portnum = getRequestingPort();
    if (portnum != -1)
      port = Pooka.getResources().formatMessage("info.login.connecting.port", portnum);

    // Build the info string
    String info = Pooka.getResources().formatMessage("info.login.connecting", protocol, host, port);

    mUsername = getDefaultUserName();

    // get the appropriate info for authentication.
    String prompt = getRequestingPrompt();
    if (prompt == null)
      prompt = Pooka.getResources().formatMessage("info.login.prompt", mUsername);

    JDialog returnValue = new JDialog(getFrame(), Pooka.getProperty("button.login", "Login"), true);

    JLabel infoLabel = new JLabel(info);
    mStatusDisplay = new JLabel(prompt);

    mPasswordField = new JPasswordField("", 20);
    mPasswordField.addFocusListener(new FocusAdapter() {
        public void focusGained(FocusEvent e) {
          if (mPasswordField.getPassword() != null && mPasswordField.getPassword().length > 0) {
            mPasswordField.setSelectionStart(0);
            mPasswordField.setSelectionEnd(mPasswordField.getPassword().length);
          }
        }
      });

    mPasswordField.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          okPressed(e);
        }
      });

    JLabel passwordLabel =  new JLabel(Pooka.getProperty("Store.editor.main.password.label", "Password") + ":");

    Icon icon = UIManager.getIcon("OptionPane.questionIcon");
    JLabel iconLabel = new JLabel(icon);

    addOkButton(Pooka.getProperty("button.login", "Login"));
    addCancelButton(Pooka.getProperty("button.cancel", "Cancel"));

    mPanel = new JPanel();
    layout = new SpringLayout();
    mPanel.setLayout(layout);

    mPanel.add(iconLabel);
    mPanel.add(infoLabel);
    mPanel.add(mStatusDisplay);
    mPanel.add(passwordLabel);
    mPanel.add(mPasswordField);

    int border = 5;

    layout.putConstraint(SpringLayout.WEST, iconLabel, border, SpringLayout.WEST, mPanel);
    layout.putConstraint(SpringLayout.NORTH, iconLabel, border, SpringLayout.NORTH, mPanel);

    layout.putConstraint(SpringLayout.WEST, infoLabel, 5, SpringLayout.EAST, iconLabel);
    layout.putConstraint(SpringLayout.NORTH, infoLabel, border, SpringLayout.NORTH, mPanel);
    layout.putConstraint(SpringLayout.EAST, mPanel, Spring.constant(border, border, 1000), SpringLayout.EAST, infoLabel);
    SpringLayout.Constraints infoLabelConst = layout.getConstraints(infoLabel);
    SpringLayout.Constraints iconLabelConst = layout.getConstraints(iconLabel);

    Spring iconInfoHeight = Spring.max(infoLabelConst.getHeight(), iconLabelConst.getHeight());
    infoLabelConst.setHeight(iconInfoHeight);
    iconLabelConst.setHeight(iconInfoHeight);

    //layout.putConstraint(SpringLayout.WEST, mStatusDisplay, Spring.constant(5, 5, Integer.MAX_VALUE), SpringLayout.WEST, mPanel);
    layout.putConstraint(SpringLayout.WEST, mStatusDisplay, 0, SpringLayout.WEST, infoLabel);
    layout.putConstraint(SpringLayout.NORTH, mStatusDisplay, 5, SpringLayout.SOUTH, infoLabel);
    layout.putConstraint(SpringLayout.EAST, mStatusDisplay, Spring.minus(layout.getConstraint(SpringLayout.WEST, mStatusDisplay)), SpringLayout.EAST, mPanel);

    layout.putConstraint(SpringLayout.WEST, passwordLabel, 0, SpringLayout.WEST, infoLabel);
    //layout.putConstraint(SpringLayout.WEST, passwordLabel, Spring.constant(border, border, Integer.MAX_VALUE), SpringLayout.WEST, mPanel);
    layout.putConstraint(SpringLayout.NORTH, passwordLabel, 10, SpringLayout.SOUTH, mStatusDisplay);

    layout.putConstraint(SpringLayout.WEST, mPasswordField, 5, SpringLayout.EAST, passwordLabel);
    layout.putConstraint(SpringLayout.BASELINE, mPasswordField, 0, SpringLayout.BASELINE, passwordLabel);
    layout.putConstraint(SpringLayout.EAST, mPasswordField, Spring.minus(layout.getConstraint(SpringLayout.WEST, passwordLabel)), SpringLayout.EAST, mPanel);
    //layout.putConstraint(SpringLayout.EAST, mPasswordField, passwordCenterSpring, SpringLayout.EAST, mPanel);
    //layout.getConstraints(mPasswordField).setHeight(layout.getConstraints(passwordLabel).getHeight());
    layout.getConstraints(mPasswordField).setHeight(Spring.constant(mPasswordField.getPreferredSize().height));

    JPanel buttonPanel = new JPanel();
    buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
    buttonPanel.add(mOkButton);
    buttonPanel.add(mCancelButton);
    mPanel.add(buttonPanel);

    layout.putConstraint(SpringLayout.WEST, buttonPanel, Spring.constant(2, 2, Integer.MAX_VALUE), SpringLayout.WEST, mPanel);
    layout.putConstraint(SpringLayout.NORTH, buttonPanel, Spring.constant(2, 2, 1000), SpringLayout.SOUTH, mPasswordField);
    layout.putConstraint(SpringLayout.EAST, buttonPanel, -2, SpringLayout.EAST, mPanel);
    layout.putConstraint(SpringLayout.SOUTH, mPanel, 5, SpringLayout.SOUTH, buttonPanel);

    /*
    JButton previousButton = null;
    for (JButton button: mButtonList) {
      mPanel.add(button);
      if (previousButton == null) {
        layout.putConstraint(SpringLayout.WEST, button, Spring.constant(2, 1000, Integer.MAX_VALUE), SpringLayout.WEST, mPanel);
        layout.putConstraint(SpringLayout.NORTH, button, Spring.constant(2, 2, 1000), SpringLayout.SOUTH, mPasswordField);
        layout.putConstraint(SpringLayout.SOUTH, mPanel, border, SpringLayout.SOUTH, button);
      } else {
        layout.putConstraint(SpringLayout.WEST, button, 2, SpringLayout.EAST, previousButton);
        layout.putConstraint(SpringLayout.NORTH, button, 2, SpringLayout.SOUTH, mPasswordField);

        //Spring height = Spring.max(layout.getConstraints(previousButton).getHeight(), layout.getConstraints(button).getHeight());
        //layout.getConstraints(button).setHeight(height);
      }
      previousButton = button;
    }

    layout.putConstraint(SpringLayout.EAST, previousButton, -2, SpringLayout.EAST, mPanel);
    */

    returnValue.getContentPane().add(mPanel);

    returnValue.pack();

    Frame parentFrame = getFrame();
    if (parentFrame != null) {
      Point location = parentFrame.getLocationOnScreen();
      Dimension windowSize = parentFrame.getSize();
      Dimension editorWindowSize = returnValue.getSize();
      int yValue = ((windowSize.height - editorWindowSize.height) / 2) + location.y;
      int xValue = ((windowSize.width - editorWindowSize.width) / 2) + location.x;
      returnValue.setLocation(new Point(xValue, yValue));
    }

    returnValue.addWindowListener(new WindowAdapter() {
        public void windowClosing(WindowEvent e) {
          setCancelled(true);
          if (mDialog != null && mDialog.isEnabled() && mCallerThread != null) {
            mCallerThread.interrupt();
          }
        }
      });
    return returnValue;
  }

  /**
   * Dispose of this dialog.
   */
  public void disposeAuthenticator() {
    if (mDialog != null) {
      if (EventQueue.isDispatchThread()) {
        setEnabled(false);
        mDialog.setVisible(false);
        mDialog.dispose();
        mDialog = null;
      } else {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
              setEnabled(false);
              mDialog.setVisible(false);
              mDialog.dispose();
              mDialog = null;
            }
          });
      }
    }
    mCancelled = false;
    mShowing = false;
    mCallerThread = null;
    layout = null;
    //mButtonList = new ArrayList<JButton>();
    mOkButton = null;
    mCancelButton = null;
    mPanel = null;
    mPasswordField = null;
  }

  private void okPressed(ActionEvent e) {
    mStatusDisplay.setText(Pooka.getProperty("info.login.loginInProgress", "Logging in..."));
    mStatusDisplay.setForeground(Color.BLUE);
    setEnabled(false);
    mCallerThread.interrupt();
  }

  /**
   * Adds a default "Ok" button.
   */
  private void addOkButton(String label) {
    addOkButton(label, new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          okPressed(e);
        }
      });
  }

  /**
   * Adds an "Ok" button.
   */
  private void addOkButton(String label, ActionListener listener) {
    JButton okButton = new JButton(label);
    okButton.addActionListener(listener);
    mOkButton = okButton;
    mOkButton.setMnemonic(KeyEvent.VK_ENTER);
    //addButton(okButton);
  }

  /**
   * Adds a default "Cancel" button.
   */
  private void addCancelButton(String label) {
    JButton cancelButton = new JButton(label);
    cancelButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {

          setCancelled(true);
          mDialog.setVisible(false);

          mCallerThread.interrupt();
        }
      });
    mCancelButton = cancelButton;
    //addButton(cancelButton);
  }

  /**
   * Adds a button.
   */
  /*
  private void addButton(JButton button) {
    mButtonList.add(button);
  }
  */

  /**
   * Sets all the relevent components on this Authenticator as enabled/disabled.
   */
  public void setEnabled(boolean pEnabled) {
    if (mDialog != null)
      mDialog.setEnabled(pEnabled);
    if (mPasswordField != null)
      mPasswordField.setEnabled(pEnabled);
    /*
    if (mButtonList != null) {
      for (JButton button: mButtonList) {
        button.setEnabled(pEnabled);
      }
    }
    */
    if (mOkButton != null) {
      mOkButton.setEnabled(pEnabled);
    }
    if (mCancelButton != null) {
      mCancelButton.setEnabled(pEnabled);
    }
  }

}
