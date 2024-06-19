package net.suberic.pooka.gui;
import javax.swing.*;

public class InfoPanel extends JPanel {
  JLabel currentLabel = null;
  ConnectionMonitor monitor = null;
  Thread panelTimer;
  long messageTimeout = 30000;
  long clearTime = -1;
  boolean cleared= true;
  boolean exitThread = false;

  /**
   * Creates an InfoPanel and adds a ConnectionMonitor
   */
  public InfoPanel() {
    //super(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));
    super(new java.awt.BorderLayout());
    currentLabel = new JLabel();
    this.add(currentLabel, java.awt.BorderLayout.WEST);

    monitor = new ConnectionMonitor();
    monitor.monitorConnectionManager(net.suberic.pooka.Pooka.getConnectionManager());

    this.add(monitor,java.awt.BorderLayout.EAST);

    javax.swing.border.Border border = BorderFactory.createLoweredBevelBorder();

    this.setBorder(border);
    this.getInsets().top=0;
    this.getInsets().bottom=0;
    this.setMinimumSize(getPreferredSize());

    panelTimer = new Thread(new Runnable() {
        public void run() {
          waitForTimeout();
        }
      });
    panelTimer.start();
  }

  /**
   * Sets the message for the InfoPanel.
   */
  public void setMessage(String newMessage) {
    final String msg = newMessage;
    Runnable runMe = new Runnable() {
        public void run() {
          currentLabel.setText(msg);
          currentLabel.repaint();
          clearTime = System.currentTimeMillis() + messageTimeout;
          cleared = false;
          panelTimer.interrupt();
        }
      };

    if (SwingUtilities.isEventDispatchThread()) {
      runMe.run();
    } else {
      SwingUtilities.invokeLater(runMe);
    }
  }

  /**
   * Clears the InfoPanel.
   */
  public void clear() {
    Runnable runMe = new Runnable() {
        public void run() {
          currentLabel.setText("");
          currentLabel.repaint();
          cleared = true;
        }
      };
    if (SwingUtilities.isEventDispatchThread()) {
      runMe.run();
    } else {
      SwingUtilities.invokeLater(runMe);
    }
  }

  /**
   * Stops the panel thread.
   */
  public void stopThread() {
    exitThread = true;
    panelTimer.interrupt();
  }

  /**
   * Waits for the timeout given and then clears the InfoPanel.
   */
  void waitForTimeout() {
    while (! exitThread) {
      long currentTime = System.currentTimeMillis();
      long sleepTime = 30000;
      if (currentTime >= clearTime) {
        if (! cleared) {
          clear();
        }
      } else {
        sleepTime = clearTime - currentTime;
      }

      try {
        Thread.currentThread().sleep(sleepTime);
      } catch (InterruptedException ie) {

      }
    }
  }

}
