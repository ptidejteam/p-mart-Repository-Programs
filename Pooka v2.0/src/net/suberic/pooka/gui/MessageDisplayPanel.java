package net.suberic.pooka.gui;
import net.suberic.pooka.*;
import net.suberic.util.gui.*;
import javax.mail.*;
import javax.mail.internet.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.text.TextAction;
import java.util.*;
import java.util.regex.*;
import javax.swing.text.JTextComponent;
import javax.swing.event.*;
import java.io.File;

/**
 * This is a JPanel which displays the content of a message.
 *
 * This component should be usable either as part of a window (internal
 * or full frame), and as part of a preview pane.
 */
public abstract class MessageDisplayPanel extends JPanel {
  protected MessageUI msgUI;
  protected JSplitPane splitPane = null;
  protected AttachmentPane attachmentPanel = null;
  protected JTextPane editorPane = null;
  protected JScrollPane editorScrollPane = null;
  protected boolean hasAttachment = false;
  protected ConfigurableKeyBinding keyBindings;

  protected HashMap mFontSetMap = new HashMap();

  protected Matcher currentMatcher = null;

  JPanel attachmentDisplayPanel;

  /**
   * Creates an empty MessageDisplayPanel.
   */
  public MessageDisplayPanel() {
    this.setLayout(new CardLayout());
  }

  /**
   * Creates a MessageDisplayPanel for the given MessageUI.
   */
  public MessageDisplayPanel(MessageUI newMsgUI) {
    msgUI = newMsgUI;

    this.setLayout(new BorderLayout());

  }

  /**
   * This method is expected to do all the implementation-specific
   * duties, like setting the editorPane, etc.
   */

  public abstract void configureMessageDisplay() throws MessagingException, OperationCancelledException;

  /**
   * Shows the current display of the encryption status.
   */
  public abstract net.suberic.pooka.gui.crypto.CryptoStatusDisplay getCryptoStatusDisplay();


  /**
   * Shows a dialog that asks for a String (ok, actually a regexp) to
   * search for, then searches for it.
   */
  public void searchMessage() {
    String searchString = getMessageUI().showInputDialog(Pooka.getProperty("message.search", "Find what"), Pooka.getProperty("message.search.title", "Find"));
    if (searchString != null && searchString.length() > 0)
      findRegexp(searchString);
  }

  /**
   * Searches for the last-sought string.  If no search has been done,
   * calls searchMessage().
   */
  public void searchAgain() {
    if (currentMatcher != null)
      findNext();
    else
      searchMessage();
  }

  /**
   * This finds the given regular expression in the displayed page.
   */
  protected void findRegexp(String regString) {
    boolean matchSucceeded = doFindRegexp(regString);
    if (matchSucceeded) {
      int start = currentMatcher.start();
      int end = currentMatcher.end();
      JTextPane currentPane = getCurrentEditorPane();
      currentPane.setCaretPosition(start);
      currentPane.moveCaretPosition(end);
    }
  }

  /**
   * This finds the given regular expression in the displayed page.
   */
  protected void findNext() {
    boolean matchSucceeded = doFindNext();
    if (matchSucceeded) {
      int start = currentMatcher.start();
      int end = currentMatcher.end();
      JTextPane currentPane = getCurrentEditorPane();
      currentPane.setCaretPosition(start);
      currentPane.moveCaretPosition(end);
    }
  }

  /**
   * Does the work of finding the next match.
   */
  protected boolean doFindRegexp(String regString) {
    if (currentMatcher != null) {
      String origString = currentMatcher.pattern().pattern();
      if (origString.equals(regString)) {
        return doFindNext();
      }
    }

    // otherwise make a new pattern/matcher.

    Pattern p = Pattern.compile(regString);
    javax.swing.text.Document currentDoc = getCurrentEditorPane().getDocument();

    try {
      currentMatcher = p.matcher(currentDoc.getText(0, currentDoc.getLength()));
      return currentMatcher.find();
    } catch (Exception e) {

    }

    return false;

  }

  /**
   * Matches the next value.
   */
  protected boolean doFindNext() {
    if (currentMatcher != null) {
      return currentMatcher.find();
    } else {
      return false;
    }
  }

  /**
   * This calculates the default size for the EditorPane.
   *
   * Here, we use the MessageWindow.editorPane.* properties to determine
   * the size.  Specifically, we check for the hsizeByCharLength
   * property.  If this is set to true, then we dynamically determine
   * the appropriate width using the current font of the editorPane
   * along with the charLength property.
   *
   * If hsizeByCharLength is set to false, or if for whatever reason we
   * find that we're unable to determine an appropriate size, then we just
   * use the vsize and hsize properties.
   */
  public Dimension getDefaultEditorPaneSize() {
    int hsize = 500;
    int vsize = 500;

    try {
      vsize = Integer.parseInt(Pooka.getProperty("MessageWindow.editorPane.vsize", "500"));
    } catch (NumberFormatException nfe) {
      vsize=500;
    }

    try {
      if (Pooka.getProperty("MessageWindow.editorPane.hsizeByCharLength", "false").equalsIgnoreCase("true") && editorPane != null) {
        int charLength = Integer.parseInt(Pooka.getProperty("MessageWindow.editorPane.charLength", "80"));
        Font currentFont = editorPane.getFont();
        if (currentFont != null) {
          FontMetrics fm = this.getFontMetrics(currentFont);
          Insets margin = editorPane.getMargin();
          int scrollBarWidth = 0;
          if (editorScrollPane != null && editorScrollPane.getVerticalScrollBar() != null) {
            scrollBarWidth = editorScrollPane.getVerticalScrollBar().getPreferredSize().width;
          }
          hsize = ((int)(charLength * fm.getStringBounds("Remember when you were young?  You shone like the sun.  Shine on you crazy diamo", editorPane.getGraphics()).getWidth() / 80)) + margin.left + margin.right + scrollBarWidth;
        }
      } else {
        hsize = Integer.parseInt(Pooka.getProperty("MessageWindow.editorPane.hsize", "500"));
      }
    } catch (NumberFormatException nfe) {
      hsize=500;
    }

    Dimension retval = new Dimension(hsize, vsize);
    return retval;
  }


  /**
   * This sets the default font for the editorPane to a font determined
   * by the MessageWindow.editorPane.font property.
   *
   * I believe that if the font cannot be found or instantiated,
   * nothing should happen, but i'm not sure.  :)
   */
  public void setDefaultFont() {
    setDefaultFont(getEditorPane());
  }

  /**
   * This sets the default font for the editorPane to a font determined
   * by the MessageWindow.editorPane.font property.
   *
   * I believe that if the font cannot be found or instantiated,
   * nothing should happen, but i'm not sure.  :)
   */
  protected void setDefaultFont(JEditorPane jep) {
    Font f = null;
    try {
      net.suberic.util.swing.ThemeSupporter ts = (net.suberic.util.swing.ThemeSupporter)getMessageUI();
      net.suberic.util.swing.ConfigurableMetalTheme cmt = (net.suberic.util.swing.ConfigurableMetalTheme) ts.getTheme(Pooka.getUIFactory().getPookaThemeManager());
      if (cmt != null) {
        f = cmt.getMonospacedFont();
      }
    } catch (Exception e) {
      // if we get an exception, just ignore it and use the default.
    }

    if (f == null && mFontSetMap.get(jep) == null) {
      String fontString = Pooka.getProperty("MessageWindow.editorPane.font", "Monospaced-Plain-11");

      f = Font.decode(fontString);
    }

    if (f != null) {
      jep.setFont(f);
      mFontSetMap.put(jep, new Boolean(true));
    }
  }

  /**
   * This sets the size of the MessageDisplayPanel to a reasonable
   * default value.
   *
   * This method should be implemented by subclasses.
   */
  public abstract void sizeToDefault();

  public UserProfile getDefaultProfile() {
    if (getMessageProxy() != null)
      return getMessageProxy().getDefaultProfile();
    else
      return null;
  }

  public JTextPane getEditorPane() {
    return editorPane;
  }


  /**
   * Returns the current EditorPane being used.
   */
  public JTextPane getCurrentEditorPane() {
    return editorPane;
  }

  public MessageProxy getMessageProxy() {
    if (msgUI != null)
      return msgUI.getMessageProxy();
    else
      return null;
  }

  public MessageUI getMessageUI() {
    return msgUI;
  }

  public void setMessageUI(MessageUI newValue) {
    msgUI = newValue;
  }

  public String getMessageText() {
    return getEditorPane().getText();
  }

  public String getMessageContentType() {
    return getEditorPane().getContentType();
  }

  public AttachmentPane getAttachmentPanel() {
    return attachmentPanel;
  }

  public Action[] getActions() {
    return null;
  }

  public JSplitPane getSplitPane() {
    return splitPane;
  }
}
