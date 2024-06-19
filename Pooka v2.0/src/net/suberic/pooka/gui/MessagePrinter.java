package net.suberic.pooka.gui;
import net.suberic.pooka.*;
import java.util.*;
import java.awt.print.*;
import java.awt.*;
import javax.mail.internet.MimeMessage;
import javax.swing.*;
import javax.swing.text.*;
import javax.mail.MessagingException;

public class MessagePrinter implements Printable {

  private MessageInfo message;
  private int offset;
  private MessagePrinterDisplay mDisplay = null;

  JTextPane jtp = null;

  int mPageCount = 0;
  double[] mPageBreaks = null;
  double mScale = 1;

  String mContentType = null;
  StringBuffer mMessageText = null;

  /**
   * This creates a new MessagePrinter for the given MessageInfo.
   */
  public MessagePrinter(MessageInfo mi, int newOffset) {
    message = mi;
    offset = newOffset;
  }

  public MessagePrinter(MessageInfo mi) {
    this(mi, 0);
  }

  /**
   * This calculates the number of pages using the given PageFormat
   * that it will take to print the message.
   */
  public int getPageCount() {
    return mPageCount;
  }

  /**
   * Calculates the page breaks for this component.
   */
  public void doPageCalculation(PageFormat pageFormat) {
    double pageHeight = pageFormat.getImageableHeight();
    double pageWidth = pageFormat.getImageableWidth();

    java.awt.Dimension minSize = jtp.getMinimumSize();

    double newWidth = Math.max(minSize.getWidth(), pageWidth);

    java.awt.Dimension newSize = new java.awt.Dimension();
    newSize.setSize(newWidth, jtp.getSize().getHeight());
    jtp.setSize(newSize);

    if (jtp.getSize().getHeight() < jtp.getPreferredSize().getHeight()) {
      java.awt.Dimension finalSize = new java.awt.Dimension();
      finalSize.setSize(jtp.getSize().getWidth(), jtp.getPreferredSize().getHeight());
      jtp.setSize(finalSize);
    }

    java.awt.Dimension d = jtp.getSize();

    double panelWidth = d.getWidth();
    double panelHeight = d.getHeight();

    jtp.setVisible(true);

    // don't scale below 1.
    mScale = Math.min(1,pageWidth/panelWidth);

    //mScale = 1;

    int counter = 0;

    paginate(pageHeight);

    mPageCount = mPageBreaks.length;

    if (mDisplay != null) {
      mDisplay.setPageCount(mPageCount);
    }

  }

  double pageEnd = 0;

  /**
   * Paginates.
   */
  public void paginate(double pageHeight) {
    java.util.List breakList = new java.util.ArrayList();

    boolean pageExists = true;
    double pageStart = 0;
    pageEnd = 0;

    double scaledPageHeight = pageHeight/mScale;

    jtp.validate();

    View view = jtp.getUI().getRootView(jtp);

    double pageWidth = jtp.getSize().getWidth();

    Rectangle allocation = new Rectangle(0, 0, jtp.getSize().width, jtp.getSize().height);

    while (pageExists) {
      if (mDisplay != null) {
        // update the display
        mDisplay.setCurrentPage(breakList.size());
      }

      pageStart = pageEnd;
      pageEnd = pageStart + scaledPageHeight;

      Rectangle currentPage = new Rectangle();
      currentPage.setRect(0d, pageStart, pageWidth, scaledPageHeight);

      pageExists = calculatePageBreak(view, allocation, currentPage);

      if (pageExists) {
        breakList.add(new Double(pageStart * mScale));
      }
    }

    mPageBreaks = new double[breakList.size()];
    for (int i = 0; i < mPageBreaks.length; i++) {
      mPageBreaks[i] = (double) ((Double)breakList.get(i)).doubleValue();
    }

  }

  /**
   * Calculates the next pageBreak.
   */
  public boolean calculatePageBreak(View view, Shape allocation, Rectangle currentPage) {
    boolean returnValue = false;

    // only check leaf views--if it's a branch, get the children.
    if (view.getViewCount() > 0) {
      for (int i = 0; i < view.getViewCount(); i++) {
        Shape childAllocation = view.getChildAllocation(i,allocation);
        if (childAllocation != null) {
          View childView = view.getView(i);
          if (calculatePageBreak(childView,childAllocation,currentPage)) {
            returnValue = true;
          }
        }
      }
    } else {
      if (allocation.getBounds().getMaxY() >= currentPage.getY()) {
        returnValue = true;
        if ((allocation.getBounds().getHeight() > currentPage.getHeight()) &&
            (allocation.intersects(currentPage))) {
        } else {
          if (allocation.getBounds().getY() >= currentPage.getY() && allocation.getBounds().getY() < pageEnd) {

            if (allocation.getBounds().getMaxY() <= pageEnd) {
              // don't bother--we're fine.
            } else {
              if (allocation.getBounds().getY() < pageEnd) {
                pageEnd = allocation.getBounds().getY();
              }
            }
          }
        }
      }
    }

    return returnValue;
  }

  /**
   * This actually prints the given page.
   */
  public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) {
    try {
      // load the text if we haven't already
      if (mMessageText == null) {
        loadText();
      }

      // create the JTextPage if we haven't already.
      if (jtp == null) {
        createTextPane();
      }

      // paginate.
      if (mPageBreaks == null) {
        if (mDisplay != null) {
          // update the display
          mDisplay.setStatus(MessagePrinterDisplay.PAGINATING);
        }
        doPageCalculation(pageFormat);
        if (mDisplay != null) {
          // update the display
          mDisplay.setStatus(MessagePrinterDisplay.PRINTING);
        }
      }

      // if we're done, we're done.
      if(pageIndex >= mPageCount) {
        return Printable.NO_SUCH_PAGE;
      }

      if (mDisplay != null) {
        // update the display
        mDisplay.setCurrentPage(pageIndex + 1);
      }

      Graphics2D g2 = (Graphics2D)graphics;

      // only print a single page.
      if (pageIndex + 1 < mPageCount) {
        Rectangle origClip = g2.getClipBounds();
        g2.clipRect(g2.getClipBounds().x, g2.getClipBounds().y, g2.getClipBounds().width, (int) (mPageBreaks[pageIndex + 1] - mPageBreaks[pageIndex]));
      }

      //shift Graphic to line up with beginning of print-imageable region
      g2.translate(pageFormat.getImageableX(), pageFormat.getImageableY());

      //shift Graphic to line up with beginning of next page to print
      g2.translate(0f, -mPageBreaks[pageIndex]);

      //scale the page so the width fits...
      g2.scale(mScale, mScale);

      jtp.print(g2);

      return Printable.PAGE_EXISTS;

    } catch (MessagingException me) {
      me.printStackTrace();
      return Printable.NO_SUCH_PAGE;
    } catch (OperationCancelledException oce) {
      oce.printStackTrace();
      return Printable.NO_SUCH_PAGE;
    }
  }

  /**
   * Loads the text and sets the content type.
   */
  public void loadText() throws MessagingException, OperationCancelledException {
    mMessageText = new StringBuffer();

    String content = null;

    mContentType = "text/plain";

    boolean displayHtml = false;

    int msgDisplayMode = message.getMessageProxy().getDisplayMode();

    // figure out html vs. text
    if (Pooka.getProperty("Pooka.displayHtml", "").equalsIgnoreCase("true")) {
      if (message.isHtml()) {
        if (msgDisplayMode > MessageProxy.TEXT_ONLY)
          displayHtml = true;

      } else if (message.containsHtml()) {
        if (msgDisplayMode >= MessageProxy.HTML_PREFERRED)
          displayHtml = true;

      } else {
        // if we don't have any html, just display as text.
      }
    }

    // set the content
    if (msgDisplayMode == MessageProxy.RFC_822) {
      content = message.getRawText();
    } else {
      if (displayHtml) {
        mContentType = "text/html";

        if (Pooka.getProperty("Pooka.displayTextAttachments", "").equalsIgnoreCase("true")) {
          content = message.getHtmlAndTextInlines(true, false);
        } else {
          content = message.getHtmlPart(true, false);
        }
      } else {
        if (Pooka.getProperty("Pooka.displayTextAttachments", "").equalsIgnoreCase("true")) {
          // Is there only an HTML part?  Regardless, we've determined that
          // we will still display it as text.
          if (message.isHtml())
            content = message.getHtmlAndTextInlines(true, false);
          else
            content = message.getTextAndTextInlines(true, false);
        } else {
          // Is there only an HTML part?  Regardless, we've determined that
          // we will still display it as text.
          if (message.isHtml())
            content = message.getHtmlPart(true, false);
          else
            content = message.getTextPart(true, false);
        }
      }
    }

    if (content != null)
      mMessageText.append(content);

  }

  /**
   * Creates the appropriate JTextPane.
   */
  public void createTextPane() {
    jtp = new JTextPane();

    java.awt.Insets newMargin = new java.awt.Insets(0,0,0,0);
    jtp.setMargin(newMargin);

    // pull in the correct font.
    String fontName = Pooka.getProperty("MessageWindow.editorPane.printing.font.name", "monospaced");
    int fontSize = Integer.parseInt(Pooka.getProperty("MessageWindow.editorPane.printint.font.size", "10"));

    Font f = new Font(fontName, Font.PLAIN, fontSize);

    if (f != null)
      jtp.setFont(f);

    jtp.validate();

    jtp.setContentType(mContentType);
    jtp.setText(mMessageText.toString());

    jtp.setEditable(false);

    jtp.setSize(jtp.getPreferredSize());

    //jtp.setVisible(true);

  }

  /**
   * Sets the PrinterDisplay for this MessagePrinter.
   */
  public void setDisplay(MessagePrinterDisplay pDisplay) {
    mDisplay = pDisplay;
  }

  /**
   * Returns the JTextPane for this Printer.
   */
  public JTextPane getTextPane() {
    return jtp;
  }
}
