/*
 * @(#)SimpleAuthenticator.java 1.3 98/03/20
 *
 * Copyright (c) 1996-1998 by Sun Microsystems, Inc.
 * All Rights Reserved.
 */
package net.suberic.pooka.gui;

import javax.mail.*;
import java.net.InetAddress;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 * Simple Authenticator for requesting password information.
 *
 * @version 1.3, 98/03/20
 * @author  Christopher Cotton
 * @author  Bill Shannon
 */

public class SimpleAuthenticator extends Authenticator {

  String username;
  String password;

  boolean mCancelled = false;

  public SimpleAuthenticator() {
  }

  protected PasswordAuthentication getPasswordAuthentication() {

    // given a prompt?
    String prompt = getRequestingPrompt();
    if (prompt == null)
      prompt = "Please login...";

    // protocol
    String protocol = getRequestingProtocol();
    if (protocol == null)
      protocol = "Unknown protocol";

    // get the host
    String host = null;
    InetAddress inet = getRequestingSite();
    if (inet != null)
      host = inet.getHostName();
    if (host == null)
      host = "Unknown host";

    // port
    String port = "";
    int portnum = getRequestingPort();
    if (portnum != -1)
      port = ", port " + portnum + " ";

    // Build the info string
    String info = "Connecting to " + protocol + " mail service on host " +
      host + port;

    //JPanel d = new JPanel();
    // XXX - for some reason using a JPanel here causes JOptionPane
    // to display incorrectly, so we workaround the problem using
    // an anonymous JComponent.
    JComponent d = new JComponent() { };

    GridBagLayout gb = new GridBagLayout();
    GridBagConstraints c = new GridBagConstraints();
    d.setLayout(gb);
    c.insets = new Insets(2, 2, 2, 2);

    c.anchor = GridBagConstraints.WEST;
    c.gridwidth = GridBagConstraints.REMAINDER;
    c.weightx = 0.0;
    d.add(constrain(new JLabel(info), gb, c));
    d.add(constrain(new JLabel(prompt), gb, c));

    c.gridwidth = 1;
    c.anchor = GridBagConstraints.EAST;
    c.fill = GridBagConstraints.NONE;
    c.weightx = 0.0;
    d.add(constrain(new JLabel("Username:"), gb, c));

    c.anchor = GridBagConstraints.EAST;
    c.fill = GridBagConstraints.HORIZONTAL;
    c.gridwidth = GridBagConstraints.REMAINDER;
    c.weightx = 1.0;
    final String user = getDefaultUserName();
    final JTextField username = new JTextField(user, 20);
    d.add(constrain(username, gb, c));

    c.gridwidth = 1;
    c.fill = GridBagConstraints.NONE;
    c.anchor = GridBagConstraints.EAST;
    c.weightx = 0.0;
    d.add(constrain(new JLabel("Password:"), gb, c));

    c.anchor = GridBagConstraints.EAST;
    c.fill = GridBagConstraints.HORIZONTAL;
    c.gridwidth = GridBagConstraints.REMAINDER;
    c.weightx = 1.0;
    final JPasswordField password = new JPasswordField("", 20);
    d.add(constrain(password, gb, c));

    final JOptionPane authPane = new JOptionPane(d, JOptionPane.QUESTION_MESSAGE, JOptionPane.OK_CANCEL_OPTION) {
        public void selectInitialValue() {
          if (user != null && user.length() > 0) {
            password.requestFocus();
          }
          else {
            username.requestFocus();
          }
        }
      };

    mCancelled = false;

    final JDialog dialog = authPane.createDialog(getFrame(), "Login");

    authPane.selectInitialValue();

    dialog.setVisible(true);

    Object result = authPane.getValue();

    if (result instanceof Integer) {
      int resultInt = ((Integer) result).intValue();
      if (resultInt == JOptionPane.OK_OPTION)
        return new PasswordAuthentication(username.getText(), new String(password.getPassword()));
    }

    mCancelled = true;

    return null;
  }

  private Component constrain(Component cmp, GridBagLayout gb, GridBagConstraints c) {
    gb.setConstraints(cmp, c);
    return (cmp);
  }

  public Frame getFrame() {
    MainPanel mp= net.suberic.pooka.Pooka.getMainPanel();
    if (mp != null)
      return mp.getParentFrame();
    else
      return null;
  }

  /**
   * Returns whether or not this process was cancelled.
   */
  public boolean getCancelled() {
    return mCancelled;
  }
}
