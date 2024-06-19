package net.suberic.util.swing;

import java.awt.*;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.event.*;

/**
 * A panel which allows the user to select a Font from the list of all
 * available fonts on the system.
 */
public class JFontChooser extends JComponent {

  JList fontList = null;
  JList styleList = null;
  JList sizeList = null;

  JTextArea previewTextArea = null;

  boolean changing = false;

  Font originalFont = null;

  Font selectedFont = null;

  private static String[] allowedFontSizes = new String[] {
    "6", "8", "9", "10", "11", "12", "14", "16", "18", "20", "22", "24", "36",
    "48", "72"
  };

  private static String[] allowedFontStyles = new String[] {
    "Plain", "Italic", "Bold", "Bold Italic"
  };

  /**
   * Creates a new JFontChooser with a default font.
   */
  public JFontChooser() {
    configureChooser(new Font("monospaced", Font.PLAIN, 12), "The quick brown fox jumped over the lazy god [sic].");
  }

  /**
   * Creates a new JFontChooser with the specified font.
   */
  public JFontChooser(Font initialFont) {
    configureChooser(initialFont, "The quick brown fox jumped over the lazy god [sic].");
  }

  /**
   * Creates a new JFontChooser with the given preview phrase and a
   * default font.
   */
  public JFontChooser(String previewPhrase) {
    configureChooser(new Font("monospaced", Font.PLAIN, 12), previewPhrase);
  }

  /**
   * Creates a new JFontChooser with the specified font and given
   * preview phrase.
   */
  public JFontChooser(Font initialFont, String previewPhrase) {
    configureChooser(initialFont, previewPhrase);
  }

  /**
   * Creates and returns a new dialog containing the specified
   * FontChooser pane along with "OK", "Cancel", and "Reset"
   * buttons.
   */
  public static JDialog createDialog(Component parent, String title,
                                     boolean modal,
                                     JFontChooser chooserPane,
                                     ActionListener okListener,
                                     ActionListener cancelListener) {
    JDialog returnValue = new JDialog( JOptionPane.getFrameForComponent(parent), title, modal);

    returnValue.getContentPane().setLayout(new BoxLayout(returnValue.getContentPane(), BoxLayout.Y_AXIS));
    returnValue.getContentPane().add(chooserPane);
    returnValue.getContentPane().add(chooserPane.createButtonPanel(okListener, cancelListener, returnValue));
    Container cp = returnValue.getContentPane();
    if (cp instanceof JComponent) {
      ((JComponent) cp).setBorder(BorderFactory.createEtchedBorder());
    }
    return returnValue;
  }

  /**
   * Returns the currently selected Font.
   */
  public Font getFont() {
    return previewTextArea.getFont();
  }

  /**
   * Returns the currently selected Font in a String form which is
   * compatible with <code>Font.decode(String)</code>.
   */
  public String getFontString() {
    return encodeFont(getFont());
  }

  /**
   * Encodes the given Font in a String form which is
   * compatible with <code>Font.decode(String)</code>.
   */
  public static String encodeFont(Font f) {
    StringBuffer returnBuffer = new StringBuffer();
    returnBuffer.append(f.getName());
    returnBuffer.append('-');
    returnBuffer.append(getFontStyle(f));
    returnBuffer.append('-');
    returnBuffer.append(f.getSize());
    return returnBuffer.toString();
  }

  /**
   * Sets the currently selected Font to the given Font.
   */
  public void setFont(Font newFont) {
    changing = true;

    if (previewTextArea != null) {
      if (newFont != null) {
        if (fontList != null) {
          String fontName = newFont.getName();
          fontList.setSelectedValue(fontName, true);
        }

        if (styleList != null) {
          String style = getFontStyle(newFont);
          if (style != null && style == "BoldItalic")
            styleList.setSelectedValue("Bold Italic", true);
          else
            styleList.setSelectedValue(style, true);
        }

        if (sizeList != null) {
          String size = Integer.toString(newFont.getSize());
          sizeList.setSelectedValue(size, true);
        }

        previewTextArea.setFont(newFont);

      }
    }
    changing = false;

  }

  /**
   * Gets the Font style as a String.
   */
  public static String getFontStyle(Font f) {
    int style = f.getStyle();

    if (style == Font.PLAIN)
      return (allowedFontStyles[0]);
    else if (style == Font.BOLD)
      return (allowedFontStyles[1]);
    else if (style == Font.ITALIC)
      return (allowedFontStyles[2]);
    else if (style == Font.BOLD + Font.ITALIC)
      return (allowedFontStyles[3]);
    else
      return(allowedFontStyles[0]);
  }

  /**
   * Shows a modal font-chooser dialog and blocks until the
   * dialog is hidden.  If the user presses the "OK" button, then
   * this method hides/disposes the dialog and returns the selected font.
   * If the user presses the "Cancel" button or closes the dialog without
   * pressing "OK", then this method hides/disposes the dialog and returns
   * null.
   */
  public static Font showDialog(Component component, String title,
                                Font initialFont) {

    final JFontChooser jfc = new JFontChooser(initialFont);
    ActionListener okListener = new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          Font currentFont = jfc.getFont();
          jfc.setSelectedFont(currentFont);
        }
      };

    JDialog dialog = createDialog(component, title,
                                  true,
                                  jfc,
                                  okListener,
                                  null);

    dialog.pack();
    dialog.setVisible(true);

    return jfc.getSelectedFont();
  }

  /**
   * Shows a modal font-chooser dialog and blocks until the
   * dialog is hidden.  If the user presses the "OK" button, then
   * this method hides/disposes the dialog and returns the String
   * representation of the selected Font.
   * If the user presses the "Cancel" button or closes the dialog without
   * pressing "OK", then this method hides/disposes the dialog and returns
   * null.
   */
  public static String showStringDialog(Component component, String title,
                                        Font initialFont) {
    Font selectedFont = showDialog(component, title, initialFont);
    if (selectedFont != null) {
      return encodeFont(selectedFont);
    }

    return null;
  }

  /* private functions */

  /**
   * Configures a new JFontChooser.
   */
  private void configureChooser(Font f, String previewString) {
    originalFont = f;
    Box mainBox = Box.createVerticalBox();
    mainBox.add(createChooserPanel(f));
    mainBox.add(Box.createVerticalStrut(10));
    mainBox.add(createPreviewPanel(f, previewString));
    setFont(f);
    this.setLayout(new BorderLayout());
    this.add(mainBox, BorderLayout.CENTER);
    //this.pack();
  }

  /**
   * Creates the Chooser panel.
   */
  private Component createChooserPanel(Font f) {
    JPanel returnValue = new JPanel();

    ListSelectionListener changeListener = new ListSelectionListener() {
        public void valueChanged(ListSelectionEvent lse) {
          if (! changing)
            fontSelectionChanged();
        }
      };

    Box chooser = Box.createHorizontalBox();

    Box fontBox = Box.createVerticalBox();
    fontBox.add(new JLabel("Font"));
    fontList = new JList(getFontNames());
    fontList.addListSelectionListener(changeListener);
    JScrollPane fontNameScroller = new JScrollPane(fontList);
    fontBox.add(fontNameScroller);

    chooser.add(fontBox);
    chooser.add(Box.createHorizontalStrut(15));

    Box styleBox = Box.createVerticalBox();
    styleBox.add(new JLabel("Font Style"));
    styleList = new JList(getStyleNames());
    styleList.addListSelectionListener(changeListener);
    JScrollPane styleScroller = new JScrollPane(styleList);
    styleBox.add(styleScroller);

    chooser.add(styleBox);
    chooser.add(Box.createHorizontalStrut(15));

    Box sizeBox = Box.createVerticalBox();
    sizeBox.add(new JLabel("Size"));
    sizeList = new JList(getSizeNames());
    sizeList.addListSelectionListener(changeListener);
    JScrollPane sizeScroller = new JScrollPane(sizeList);
    sizeBox.add(sizeScroller);

    chooser.add(sizeBox);

    returnValue.add(chooser);
    returnValue.setBorder(BorderFactory.createEtchedBorder());

    return returnValue;
  }

  /**
   * Creates the preview panel.
   */
  private Component createPreviewPanel(Font f, String previewText) {
    previewTextArea = new JTextArea(previewText);
    previewTextArea.setFont(f);
    JPanel returnValue = new JPanel();
    returnValue.add(previewTextArea);

    returnValue.setBorder(BorderFactory.createEtchedBorder());
    returnValue.setMinimumSize(new java.awt.Dimension(50,50));

    return returnValue;
  }

  private Component createButtonPanel(ActionListener okListener, ActionListener cancelListener, JDialog newDialog) {
    final JDialog dialog = newDialog;
    JPanel returnValue = new JPanel();
    returnValue.setLayout(new FlowLayout(FlowLayout.CENTER));

    JButton okButton = new JButton("OK");

    if (okListener != null)
      okButton.addActionListener(okListener);

    okButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          dialog.setVisible(false);
        }
      });

    JButton cancelButton = new JButton("Cancel");
    cancelButton.addActionListener(cancelListener);

    cancelButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          dialog.setVisible(false);
        }
      });

    JButton resetButton = new JButton("Reset");
    resetButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          reset();
        }
      });

    returnValue.add(okButton);
    returnValue.add(resetButton);
    returnValue.add(cancelButton);

    getRootPane().setDefaultButton(okButton);

    returnValue.setBorder(BorderFactory.createEtchedBorder());

    return returnValue;
  }

  /**
   * Resets the font to the original font.
   */
  public void reset() {
    setFont(originalFont);
  }

  /**
   * called when any of the JLists change.
   */
  protected void fontSelectionChanged() {
    Font currentFont = previewTextArea.getFont();

    String fontName = (String) fontList.getSelectedValue();
    String styleString = (String) styleList.getSelectedValue();
    String sizeString = (String) sizeList.getSelectedValue();

    if (fontName == null || fontName.length() == 0) {
      fontName = currentFont.getFontName();
    }

    int style = currentFont.getStyle();
    if (styleString != null) {
      if (styleString.equalsIgnoreCase("plain"))
        style = Font.PLAIN;
      else if (styleString.equalsIgnoreCase("bold"))
        style = Font.BOLD;
      else if (styleString.equalsIgnoreCase("italic"))
        style = Font.ITALIC;
      else if (styleString.equalsIgnoreCase("bold italic"))
        style = Font.BOLD + Font.ITALIC;
    }

    int size = currentFont.getSize();
    if (sizeString != null) {
      try {
        size = Integer.parseInt(sizeString);
      } catch (Exception e) {
      }
    }

    Font newFont = new Font(fontName, style, size);

    previewTextArea.setFont(newFont);
    SwingUtilities.windowForComponent(this).pack();
  }

  /**
   * Gets all of the available font names.
   */
  private String[] getFontNames() {
    return GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
  }

  /**
   * Gets all of the available styles.
   */
  private String[] getStyleNames() {
    return allowedFontStyles;
  }

  /**
   * Gets all of the available sizes.
   */
  private String[] getSizeNames() {
    return allowedFontSizes;
  }

  /**
   * Gets the font selected by the chooser.  if 'cancel' was pressed,
   * then no font is selected.
   */
  public Font getSelectedFont() {
    return selectedFont;
  }

  /**
   * Sets the font selected by the chooser.  this should be called when
   * the 'ok' button is presed.
   */
  public void setSelectedFont(Font newFont) {
    selectedFont = newFont;
  }
}
