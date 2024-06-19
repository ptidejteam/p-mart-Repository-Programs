package net.suberic.util.swing;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

/**
 * A Display panel which has a button which, on pressing, will display
 * the stack trace for the given exception.
 */
public class ExceptionDisplayPanel extends JPanel {

  private static int S_INDENT = 10;

  // the Exception whose stack trace will be displayed.
  Exception mException;

  // the Button
  JButton mButton;

  /**
   * Creates the ExceptionDisplayPanel using the given text for the
   * button and the given exception.
   */
  public ExceptionDisplayPanel(String pButtonText, Exception pException) {
    super();

    this.setLayout(new CardLayout());

    mException = pException;

    mButton = new JButton(pButtonText);

    Box buttonBox = Box.createHorizontalBox();
    buttonBox.add(Box.createHorizontalGlue());
    buttonBox.add("BUTTON", mButton);
    buttonBox.add(Box.createHorizontalGlue());

    this.add("BUTTON", buttonBox);

    mButton.addActionListener(new AbstractAction() {

        public void actionPerformed(ActionEvent ae) {
          showStackTrace();
        }
      });
  }

  /**
   * Expands the display to show the stack trace for the exception.
   */
  public void showStackTrace() {
    // first make the stack trace.
    StringWriter exceptionWriter = new StringWriter();
    mException.printStackTrace(new PrintWriter(exceptionWriter));
    String exceptionString = exceptionWriter.toString();

    // now make the display location.
    //JTextArea jta = new JTextArea(exceptionString);
    //jta.setEditable(false);
    //JScrollPane jsp = new JScrollPane(jta);

    /*
    jsp.setMaximumSize(new Dimension(this.getSize().width, Integer.MAX_VALUE));
    jsp.setPreferredSize(new Dimension(Math.min(jsp.getPreferredSize().width, jsp.getMaximumSize().width), jsp.getPreferredSize().height));
    */
    //jsp.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
    //jsp.setPreferredSize(new Dimension(jsp.getPreferredSize().width, jsp.getPreferredSize().height));
    //this.add("EXCEPTION", jsp);

    Window windowAncestor = SwingUtilities.getWindowAncestor(this);
    if (windowAncestor instanceof JDialog) {
      JDialog dialog = (JDialog) windowAncestor;
      Window owner = dialog.getOwner();
      if (owner != null)
        windowAncestor = owner;
    }
    Component jsp = createMessageComponent(exceptionString,windowAncestor );
    this.add("EXCEPTION", jsp);


    ((CardLayout) getLayout()).show(this, "EXCEPTION");
    Dimension currentMinimum = getMinimumSize();
    this.setMinimumSize(new Dimension(Math.max(currentMinimum.width, 150), Math.max(currentMinimum.height, 100)));

    JInternalFrame parentIntFrame = null;
    try {
      parentIntFrame = (JInternalFrame) SwingUtilities.getAncestorOfClass(Class.forName("javax.swing.JInternalFrame"), this);
    } catch (Exception e) {
    }
    if (parentIntFrame != null) {
      // make sure we don't resize to be bigger than the JDesktopPane.
      JDesktopPane jdp = parentIntFrame.getDesktopPane();
      System.err.println("got jdp.");
      if (jdp != null) {
        System.err.println("jdp is not null.");
        Point iFrameLocation = parentIntFrame.getLocation();
        Dimension jdpSize = jdp.getSize();
        System.err.println("iFrameLocation = " + iFrameLocation + ", jdpSize = " + jdpSize + ", parentIntFrame.getMinimumSize() = " + parentIntFrame.getMinimumSize());
        parentIntFrame.setMaximumSize(new Dimension(Math.max(parentIntFrame.getMinimumSize().width, jdpSize.width - iFrameLocation.x), Math.max(parentIntFrame.getMinimumSize().height, jdpSize.height - iFrameLocation.y)));
        parentIntFrame.setPreferredSize(new Dimension(Math.max(parentIntFrame.getMinimumSize().width, jdpSize.width - iFrameLocation.x), Math.max(parentIntFrame.getMinimumSize().height, jdpSize.height - iFrameLocation.y)));
        System.err.println("parentIntFrame.maximumSize = " + parentIntFrame.getMaximumSize());
      }
      parentIntFrame.pack();
      //parentIntFrame.resize();
    } else {
      Window parentWindow = SwingUtilities.getWindowAncestor(this);
      if (parentWindow != null) {
        if (parentWindow instanceof JDialog) {
          JDialog parentDialog = (JDialog) parentWindow;
          parentDialog.setSize(parentDialog.getPreferredSize());
          Window owner = parentDialog.getOwner();
          if (owner != null) {
            Point ownerPoint = owner.getLocationOnScreen();
            Dimension ownerSize = owner.getSize();
          }
        }
        parentWindow.pack();
        //parentWindow.resize();
      }
    }
  }

  /**
   * Calculates a Dimension which defines a reasonably sized dialog window.
   */
  public Dimension calculateDisplaySize(Component parentComponent, Component displayComponent) {
    //Point parentLocation = parentComponent.getLocationOnScreen();
    Dimension parentSize = parentComponent.getSize();
    // width and height should be mo more than 80%
    int maxWidth = Math.max(30, (int) (parentSize.width * 0.8));
    int maxHeight = Math.max(30, (int) (parentSize.height * 0.8));

    Dimension displayPrefSize = displayComponent.getPreferredSize();

    int newWidth = Math.min(maxWidth, displayPrefSize.width);
    int newHeight = Math.min(maxHeight, displayPrefSize.height);
    return new Dimension(newWidth +5, newHeight+5);
  }

  /**
   * Returns either a properly-sized JLabel, or a JLabel inside of a
   * properly-sized JScrollPane.
   */
  public Component createMessageComponent(String message, Component parentComponent) {
    Component labelComponent = createLabel(message);
    Dimension displaySize = calculateDisplaySize(parentComponent, labelComponent);
    JScrollPane jsp = new JScrollPane(labelComponent);
    // add on space for the scrollbar.
    JScrollBar jsb = jsp.getVerticalScrollBar();
    if (jsb != null) {
      displaySize = new Dimension(displaySize.width + jsb.getPreferredSize().width, displaySize.height);
    } else {
      jsb = new JScrollBar(JScrollBar.VERTICAL);
      displaySize = new Dimension(displaySize.width + jsb.getPreferredSize().width, displaySize.height);

    }
    jsp.setPreferredSize(displaySize);
    return jsp;
  }

  /**
   * Breaks up a String into proper JLabels.
   */
  public Component createLabel(String s) {
    Container c = Box.createVerticalBox();

    addMessageComponents(c, s, 160);

    return c;
  }

  private void addMessageComponents(Container c, String s, int maxll) {
    // taken from BasicOptionPaneUI
    int nl = -1;
    int nll = 0;

    if ((nl = s.indexOf("\r\n")) >= 0) {
      nll = 2;
    } else if ((nl = s.indexOf('\n')) >= 0) {
      nll = 1;
    }

    if (nl >= 0) {
      // break up newlines
      if (nl == 0) {
        JPanel breakPanel = new JPanel() {
            public Dimension getPreferredSize() {
              Font f = getFont();

              if (f != null) {
                return new Dimension(1, f.getSize() + 2);
              }
              return new Dimension(0, 0);
            }
          };
        breakPanel.setName("OptionPane.break");
        c.add(breakPanel);
      } else {
        addMessageComponents(c, s.substring(0, nl), maxll);
      }
      addMessageComponents(c, s.substring(nl + nll), maxll);

      /*
    } else if (len > maxll) {
      Container c = Box.createVerticalBox();
      c.setName("OptionPane.verticalBox");
      burstStringInto(c, s, maxll);
      addMessageComponents(container, cons, c, maxll, true );
      */
    } else {
      JLabel label;
      label = new JLabel(s, JLabel.LEADING );
      label.setName("OptionPane.label");
      c.add(label);
    }
  }

  /**
   * Recursively creates new JLabel instances to represent <code>d</code>.
   * Each JLabel instance is added to <code>c</code>.
   */
  private void burstStringInto(Container c, String d, int maxll) {
    // Primitive line wrapping
    int len = d.length();
    if (len <= 0)
      return;
    if (len > maxll) {
      int p = d.lastIndexOf(' ', maxll);
      if (p <= 0)
        p = d.indexOf(' ', maxll);
      if (p > 0 && p < len) {
        burstStringInto(c, d.substring(0, p), maxll);
        burstStringInto(c, d.substring(p + 1), maxll);
        return;
      }
    }
    JLabel label = new JLabel(d, JLabel.LEFT);
    label.setName("OptionPane.label");
    c.add(label);
  }

}

