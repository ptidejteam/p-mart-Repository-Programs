package net.suberic.util.gui.propedit;
import javax.swing.*;
import net.suberic.util.*;
import net.suberic.util.gui.IconManager;
import java.util.*;
import java.awt.Container;
import java.awt.Component;
import java.awt.Frame;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Point;
import javax.help.HelpBroker;

/**
 * A factory which can be used to create PropertyEditorUI's.
 */
public class PropertyEditorFactory {
  // the property that defines the different editor classes for the
  // registry.
  public static String SOURCE_PROPERTY = "PropertyEditor";

  // the VariableBundle that holds both the properties and the editor
  // definitions.
  VariableBundle sourceBundle;

  // the IconManager used for PropertyEditors that use icons.
  IconManager iconManager;

  // the HelpBroker
  HelpBroker helpBroker;

  // the propertyType to className mapping
  Map typeToClassMap = new HashMap();

  /**
   * Creates a PropertyEditorFactory using the given VariableBundle as
   * a source.
   */
  public PropertyEditorFactory(VariableBundle bundle, IconManager manager, HelpBroker broker) {
    sourceBundle = bundle;
    iconManager = manager;
    helpBroker = broker;
    createTypeToClassMap();
  }

  /**
   * Creates the typeToClassMap.
   */
  private void createTypeToClassMap() {

    try {
      Class parentClass = Class.forName("net.suberic.util.gui.propedit.SwingPropertyEditor");

      Vector propertyTypes = sourceBundle.getPropertyAsVector(SOURCE_PROPERTY, "");
      for (int i = 0; i < propertyTypes.size(); i++) {
        String currentType = (String) propertyTypes.get(i);
        String className = sourceBundle.getProperty(SOURCE_PROPERTY + "." + currentType + ".class", "");
        try {
          Class currentClass = Class.forName(className);
          if (parentClass.isAssignableFrom(currentClass)) {
            typeToClassMap.put(currentType, currentClass);
          }
        } catch (Exception e) {
          System.out.println("error registering class for property type " + currentType + ":  " + e);
        }
      }
    } catch (Exception e) {
      System.out.println("caught exception initializing PropertyEditorFactory:  " + e);
      e.printStackTrace();
    }
  }

  /**
   * Shows an error message.
   */
  public void showError(Object component, String errorMessage) {
    String newErrorMessage = wrapText(errorMessage, 80, System.getProperty("line.separator"), 5);
    JOptionPane.showMessageDialog((Component) component, newErrorMessage);
  }

  /**
   * Shows an input dialog.
   */
  public String showInputDialog(SwingPropertyEditor dpe, String query) {
    return JOptionPane.showInputDialog(dpe, query);
  }

  /**
   * Creates and displays an editor window.
   */
  public void showNewEditorWindow(String title, String property) {
    showNewEditorWindow(title, property, property);
  }

  /**
   * Creates and displays an editor window.
   */
  public void showNewEditorWindow(String title, String property, String template) {
    showNewEditorWindow(title, property, template, new PropertyEditorManager(sourceBundle, this, iconManager));
  }

  /**
   * Creates and displays an editor window.
   */
  public void showNewEditorWindow(String title, String property, String template, PropertyEditorManager mgr) {
    showNewEditorWindow(title, property, template, mgr, null);
  }

  /**
   * Creates and displays an editor window.
   */
  public void showNewEditorWindow(String title, String property, String template, PropertyEditorManager mgr, Container window) {
    showNewEditorWindow(title, property, template, property, mgr, window);
  }

  public void showNewEditorWindow(String title, String property, String template, String propertyBase, PropertyEditorManager mgr, Container window) {
    showNewEditorWindow(title, createEditor(property, template, propertyBase, mgr), window);
  }

  /**
   * Creates and displays an editor window.
   */
  public void showNewEditorWindow(String title, PropertyEditorUI editor) {
    showNewEditorWindow(title, editor, null);
  }
  /**
   * Creates and displays an editor window.
   */
  public void showNewEditorWindow(String title, PropertyEditorUI editor, Container window) {
    JDialog jd = (JDialog) createEditorWindow(title, editor, window);
    if (window != null) {
      Point location = window.getLocationOnScreen();
      Dimension windowSize = window.getSize();
      Dimension editorWindowSize = jd.getSize();
      int yValue = ((windowSize.height - editorWindowSize.height) / 2) + location.y;
      int xValue = ((windowSize.width - editorWindowSize.width) / 2) + location.x;
      jd.setLocation(new Point(xValue, yValue));
    }
    jd.setVisible(true);
  }

  /**
   * This method returns an EditorWindow (a JDialog in this
   * implementation) which has an editor for each property in the
   * property List.  The title string is the title of the
   * JInternalFrame.
   */
  public Container createEditorWindow(String title, String property) {
    return createEditorWindow(title, property, property, new PropertyEditorManager(sourceBundle, this, iconManager));
  }

  /**
   * This method returns an EditorWindow (a JDialog in this
   * implementation) which has an editor for each property in the
   * property List.  The title string is the title of the
   * JDialog.
   */
  public Container createEditorWindow(String title, String property, String template ) {
    return createEditorWindow(title, property, template, new PropertyEditorManager(sourceBundle, this, iconManager));
  }

  /**
   * This method returns an EditorWindow (a JDialog in this
   * implementation) which has an editor for each property in the
   * property List.  The title string is the title of the
   * JDialog.
   */
  public Container createEditorWindow(String title, String property, String template, Container window ) {
    return createEditorWindow(title, property, template, new PropertyEditorManager(sourceBundle, this, iconManager), window);
  }

  /**
   * This method returns an EditorWindow (a JDialog in this
   * implementation) which has an editor for each property in the
   * property Vector.  The title string is the title of the
   * JInternalFrame.
   */
  public Container createEditorWindow(String title, String property, String template, PropertyEditorManager mgr) {
    return createEditorWindow(title, property, template, mgr, null);
  }
  /**
   * This method returns an EditorWindow (a JDialog in this
   * implementation) which has an editor for each property in the
   * property Vector.  The title string is the title of the
   * JInternalFrame.
   */
  public Container createEditorWindow(String title, String property, String template, PropertyEditorManager mgr, Container window) {
    return createEditorWindow(title, property, template, property, mgr, window);
  }

  public Container createEditorWindow(String title, String property, String template, String propertyBase, PropertyEditorManager mgr, Container window) {
    return createEditorWindow(title, createEditor(property, template, propertyBase, mgr), window);
  }

  public Container createEditorWindow(String title, PropertyEditorUI editor, Container window) {
    JDialog jd = null;
    if (window instanceof Dialog) {
      jd = new JDialog((Dialog) window, title, Dialog.ModalityType.APPLICATION_MODAL);
    } else if (window instanceof Frame) {
      jd = new JDialog((Frame) window, title, Dialog.ModalityType.APPLICATION_MODAL);
    } else {
      jd = new JDialog();
      jd.setTitle(title);
      jd.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
    }
    PropertyEditorPane pep = createPropertyEditorPane(editor.getManager(), (SwingPropertyEditor) editor, jd);
    jd.getContentPane().add(pep);
    jd.getRootPane().setDefaultButton(pep.getDefaultButton());
    jd.pack();
    return jd;
  }


  /**
   * Creates an appropriate PropertyEditorUI for the given property and
   * editorTemplate, using the given PropertyEditorManager.
   */
  public PropertyEditorUI createEditor(String property, String editorTemplate, PropertyEditorManager mgr) {

    return createEditor(property, editorTemplate, editorTemplate, mgr);
  }

  /**
   * Creates an appropriate PropertyEditorUI for the given property and
   * editorTemplate, using the given PropertyEditorManager.
   */
  public PropertyEditorUI createEditor(String property, String editorTemplate, String propertyBase, PropertyEditorManager mgr) {
    String type = sourceBundle.getProperty(editorTemplate + ".propertyType", "");
    return createEditor(property, editorTemplate, propertyBase, type, mgr);
  }
  /**
   * Creates an appropriate PropertyEditorUI for the given property and
   * editorTemplate, using the given PropertyEditorManager.
   */
  public PropertyEditorUI createEditor(String property, String editorTemplate, String propertyBase, String type, PropertyEditorManager mgr) {

    //System.err.println("creating editor for property '" + property + "', template '" + editorTemplate + "', propertyBase '" + propertyBase + "', type '" + type + "'");
    Class editorClass = (Class) typeToClassMap.get(type);
    if (editorClass == null) {
      editorClass = (Class) typeToClassMap.get("String");
    }

    PropertyEditorUI returnValue = null;
    try {
      returnValue = (PropertyEditorUI) editorClass.newInstance();
    } catch (Exception e) {
      System.err.println("error creating editor for property " + property + ":  " + e);
      returnValue = new StringEditorPane();
    }
    returnValue.configureEditor(property, editorTemplate, propertyBase, mgr);
    return returnValue;
  }

  /**
   * Creates the PropertyEditoPane for this editor.
   */
  public PropertyEditorPane createPropertyEditorPane(PropertyEditorManager manager, SwingPropertyEditor editor, Container container) {
    boolean commit = ! editor.getManager().createdEditorPane;
    String template = editor.getEditorTemplate();
    PropertyEditorPane returnValue = null;
    if (manager.getProperty(template + ".editorType", "").equalsIgnoreCase("wizard")) {
      returnValue = new WizardPropertyEditor(manager,  editor, container, commit);
    } else {
      returnValue = new PropertyEditorPane(manager,  editor, container, commit);
    }
    manager.createdEditorPane = true;
    return returnValue;
  }


  /**
   * Gets the source bundle for this factory.
   */
  public VariableBundle getSourceBundle() {
    return sourceBundle;
  }

  /**
   * Gets the IconManager for this factory.
   */
  public IconManager getIconManager() {
    return iconManager;
  }

  /**
   * Returns the HelpBroker for this PropertyEditorManager.
   */
  public HelpBroker getHelpBroker() {
    return helpBroker;
  }

  /**
   * This takes a String and word wraps it at length wrapLength.  It also will
   * convert any alternative linebreaks (LF, CR, or CRLF) to the
   * <code>newLine</code> given.
   */
  public static String wrapText(String originalText, int wrapLength, String newLine, int tabSize) {
    if (originalText == null)
      return null;

    StringBuffer wrappedText = new StringBuffer();

    // so the idea is that we'll get each entry denoted by a line break
    // and then add soft breaks into there.
    int currentStart = 0;
    int nextHardBreak = nextNewLine(originalText, currentStart);
    while (nextHardBreak != -1) {
      // get the current string with a newline at the end.
      String currentString = getSubstringWithNewLine(originalText, currentStart, nextHardBreak, newLine);

      int nextSoftBreak = getBreakOffset(currentString, wrapLength, tabSize);
      while (nextSoftBreak < currentString.length()) {
        wrappedText.append(currentString.substring(0, nextSoftBreak));
        wrappedText.append(newLine);

        currentString = currentString.substring(nextSoftBreak);

        nextSoftBreak = getBreakOffset(currentString, wrapLength, tabSize);
      }
      wrappedText.append(currentString);

      // get the next string including the newline.
      currentStart = afterNewLine(originalText, nextHardBreak);
      nextHardBreak= nextNewLine(originalText, currentStart);
    }

    return wrappedText.toString();
  }

  /**
   * Returns the next new line.
   */
  public static int nextNewLine(String text, int start) {
    if (start >= text.length())
      return -1;

    // go through each character, looking for \r or \n
    int foundIndex = -1;
    for (int i = start; foundIndex == -1 && i < text.length(); i++) {
      char current = text.charAt(i);
      if (current == '\r') {
        if (i + 1 < text.length() && text.charAt(i+1) == '\n')
          foundIndex = i+1;
        else
          foundIndex = i;
      } else if (current == '\n') {
        foundIndex = i;
      }
    }

    if (foundIndex == -1) {
      return text.length();
    } else {
      return foundIndex;
    }
  }

  /**
   * Returns the position after the newline indicated by index.  If
   * that's the end of the string, or an invalid index is given, returns
   * an index equal to the length of text (i.e. one more than the last
   * valid index in text).
   */
  public static int afterNewLine(String text, int index) {
    // if index is invalid, or if index is the last character in the
    // string, return
    if (index < 0 || index >= text.length() || index == text.length() -1)
      return text.length();

    char newLineChar = text.charAt(index);
    if (newLineChar == '\r' && text.charAt(index + 1) == '\n')
      return index + 2;
    else
      return index + 1;
  }

  /**
   * Gets the indicated substring with the given newline.
   */
  public static String getSubstringWithNewLine(String originalText, int start, int end, String newLine) {
    String origSubString = originalText.substring(start,end);

    if (origSubString.endsWith("\r\n")) {
      if (newLine.equals("\r\n"))
        return origSubString;
      else {
        return origSubString.substring(0, origSubString.length() - 2) + newLine;
      }
    } else if (origSubString.endsWith("\n")) {
      if (newLine.equals("\n"))
        return origSubString;
      else
        return origSubString.substring(0, origSubString.length() - 1) + newLine;
    } else if (origSubString.endsWith("\r")) {
      if (newLine.equals("\r"))
        return origSubString;
      else
        return origSubString.substring(0, origSubString.length() - 1) + newLine;
    } else {
      return origSubString + newLine;
    }
  }

  /**
   * This method takes a given String offset and returns the offset
   * position at which a line break should occur.
   *
   * If no break is necessary, the full buffer length is returned.
   *
   */
  public static int getBreakOffset(String buffer, int breakLength, int tabSize) {
    // what we'll do is to modify the break length to make it fit tabs.

    int nextTab = buffer.indexOf('\t');
    int tabAccumulator = 0;
    int tabAddition = 0;
    while (nextTab >=0 && nextTab < breakLength) {
      tabAddition = tabSize - ((tabSize +  nextTab + tabAccumulator + 1) % tabSize);
      breakLength=breakLength - tabAddition;
      tabAccumulator = tabAccumulator + tabAddition;
      if (nextTab + 1 < buffer.length())
        nextTab = buffer.indexOf('\t', nextTab + 1);
      else
        nextTab = -1;
    }


    if ( buffer.length() <= breakLength ) {
      return buffer.length();
    }

    int breakLocation = -1;
    for (int caret = breakLength; breakLocation == -1 && caret >= 0; caret--) {
      if (Character.isWhitespace(buffer.charAt(caret))) {
        breakLocation=caret + 1;
        if (breakLocation < buffer.length()) {
          // check to see if the next character is a line feed of some sort.
          char nextChar = buffer.charAt(breakLocation);
          if (nextChar == '\n')
            breakLocation ++;
          else if (nextChar == '\r') {
            if (breakLocation + 1<  buffer.length() && buffer.charAt(breakLocation + 1) == '\n') {
              breakLocation +=2;
            } else {
              breakLocation ++;
            }
          }
        }
      }
    }

    if (breakLocation == -1)
      breakLocation = breakLength;

    return breakLocation;
  }


}







