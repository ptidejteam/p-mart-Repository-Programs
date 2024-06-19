package net.suberic.pooka.gui;
import net.suberic.pooka.*;
import javax.swing.event.*;
import java.util.StringTokenizer;
import net.suberic.util.swing.HyperlinkMouseHandler;
import javax.swing.JTextPane;
import javax.swing.text.*;

/**
 * This is a simple class which implements HyperlinkListener.
 */

public class HyperlinkDispatcher implements HyperlinkListener {

  ErrorHandler errorHandler = null;

  /**
   * Default Constructor.
   */
  public HyperlinkDispatcher() {
  }

  /**
   * Creates a HyperlinkDispatcher that uses the given ErrorHandler.
   */
  public HyperlinkDispatcher(ErrorHandler handler) {
    errorHandler = handler;
  }

  /**
   * This handles HyperlinkEvents.  For now, we're just taking
   * ACTIVATED events, and dispatching the url's to the external
   * program indicated by Pooka.urlHandler.
   *
   * Specified in javax.swing.event.HyperlinkListener.
   */
  public void hyperlinkUpdate(HyperlinkEvent e) {
    if (e.getEventType() == HyperlinkEvent.EventType.ENTERED) {
      if (e.getSource() instanceof HyperlinkMouseHandler.URLSelection) {
        HyperlinkMouseHandler.URLSelection selection = (HyperlinkMouseHandler.URLSelection) e.getSource();
        if (selection.editor instanceof JTextPane) {
          JTextPane pane = (JTextPane) selection.editor;
          StyledDocument doc = pane.getStyledDocument();
          StyledEditorKit kit = (StyledEditorKit) pane.getEditorKit();
          MutableAttributeSet attr = kit.getInputAttributes();
          SimpleAttributeSet sas = new SimpleAttributeSet();
          StyleConstants.setUnderline(sas, true);
          doc.setCharacterAttributes(selection.start, (selection.end - selection.start + 1), sas, false);
        }
      }
    } else if (e.getEventType() == HyperlinkEvent.EventType.EXITED) {
      if (e.getSource() instanceof HyperlinkMouseHandler.URLSelection) {
        HyperlinkMouseHandler.URLSelection selection = (HyperlinkMouseHandler.URLSelection) e.getSource();
        if (selection.editor instanceof JTextPane) {
          JTextPane pane = (JTextPane) selection.editor;
          StyledDocument doc = pane.getStyledDocument();
          StyledEditorKit kit = (StyledEditorKit) pane.getEditorKit();
          MutableAttributeSet attr = kit.getInputAttributes();
          SimpleAttributeSet sas = new SimpleAttributeSet();
          StyleConstants.setUnderline(sas, false);
          doc.setCharacterAttributes(selection.start, (selection.end - selection.start + 1), sas, false);
        }
      }
    } else if (e.getEventType() ==  HyperlinkEvent.EventType.ACTIVATED) {
      if (Pooka.getProperty("Pooka.url.useDefaultHandler", "true").equalsIgnoreCase("true")) {
        try {
          java.awt.Desktop.getDesktop().browse(e.getURL().toURI());
        } catch(Exception ex) {
          if (errorHandler != null) {
            errorHandler.showError(java.text.MessageFormat.format(Pooka.getProperty("error.urlHandler.openingUrl", "Error opening url {0}:  "), e.getURL()), ex);
          } else {
            System.err.println(Pooka.getProperty("error.urlHandler.openingUrl", "Error opening url {0}:  " + e.getURL()));
            ex.printStackTrace();
          }
        }
      } else {
        String parsedVerb = Pooka.getProperty("Pooka.urlHandler", "firefox %s");
        if (parsedVerb.indexOf("%s") == -1)
          parsedVerb = parsedVerb + " %s";

        String[] cmdArray;

        parsedVerb = ExternalLauncher.substituteString(parsedVerb, "%s", e.getURL().toString());

        StringTokenizer tok = new StringTokenizer(parsedVerb);
        cmdArray = new String[tok.countTokens()];
        for (int i = 0; tok.hasMoreTokens(); i++) {
          String currentString = tok.nextToken();
          cmdArray[i]=currentString;
        }
        try {
          Runtime.getRuntime().exec(cmdArray);
        } catch (java.io.IOException ioe) {
          if (errorHandler != null) {
            errorHandler.showError(java.text.MessageFormat.format(Pooka.getProperty("error.urlHandler.openingUrl", "Error opening url {0}:  "), e.getURL()), ioe);
          } else {
            System.err.println(Pooka.getProperty("error.urlHandler.openingUrl", "Error opening url {0}:  " + e.getURL()));
            ioe.printStackTrace();
          }
        }
      }
    }
  }
}

