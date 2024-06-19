package net.suberic.pooka.gui;

import net.suberic.pooka.Pooka;
import net.suberic.util.FileVariableBundle;
import net.suberic.util.VariableBundle;

import javax.swing.*;
import java.awt.*;
import java.net.*;
import java.io.IOException;
import java.io.InputStream;


/**
 * Gives the option of loading configuration settings from an http file.
 */
public class LoadHttpConfigPooka {

  URL mUrl;
  JFrame mFrame;

  /**
   * Starts the dialog.
   */
  public void start() {
    Runnable runMe = new Runnable() {
        public void run() {
          mFrame = new JFrame();
          java.net.Authenticator.setDefault(new HttpAuthenticator(mFrame));
          mFrame.setVisible(true);
          showChoices();
        }
      };

    if (SwingUtilities.isEventDispatchThread())
      runMe.run();
    else {
      try {
        SwingUtilities.invokeAndWait(runMe);
      } catch (Exception ie) {
      }
    }
  }

  /**
   * Shows the choices.
   */
  public void showChoices() {
    String urlString = JOptionPane.showInputDialog("Choose a remote file.");
    if (urlString == null)
      return;

    try {
      URL configUrl = new URL(urlString);
      InputStream is = configUrl.openStream();
      VariableBundle newBundle = new FileVariableBundle(is, Pooka.getResources());
      Pooka.setResources(newBundle);
    } catch (MalformedURLException mue) {
      JOptionPane.showMessageDialog(mFrame, "Malformed URL.");
      showChoices();
    } catch (java.io.IOException ioe) {
      JOptionPane.showMessageDialog(mFrame, "Could not connect to URL:  " + ioe.toString());
      showChoices();
    }

  }

  public class HttpAuthenticator extends Authenticator {

    Frame frame;
    String username;
    String password;

    public HttpAuthenticator(Frame f) {
      this.frame = f;
    }

    protected PasswordAuthentication getPasswordAuthentication() {

      // given a prompt?
      String prompt = getRequestingPrompt();
      if (prompt == null) {
        prompt = "Enter Username and Password...";
      }

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
      String info = "Connecting to " + protocol + " resource on host " +
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
      String user = "";
      JTextField username = new JTextField(user, 20);
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
      JPasswordField password = new JPasswordField("", 20);
      d.add(constrain(password, gb, c));
      // XXX - following doesn't work
      if (user != null && user.length() > 0)
        password.requestFocusInWindow();
      else
        username.requestFocusInWindow();

      int result = JOptionPane.showConfirmDialog(frame, d, "Login", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);

      if (result == JOptionPane.OK_OPTION)
        return new PasswordAuthentication(username.getText(),
                                          password.getPassword());
      else
        return null;
    }

    private Component constrain(Component cmp,
                                GridBagLayout gb, GridBagConstraints c) {
      gb.setConstraints(cmp, c);
      return (cmp);
    }
  }
}
