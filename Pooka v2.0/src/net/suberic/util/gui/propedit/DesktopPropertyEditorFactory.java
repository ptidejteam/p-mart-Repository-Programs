package net.suberic.util.gui.propedit;
import javax.swing.*;
import net.suberic.util.*;
import net.suberic.util.gui.IconManager;
import java.util.*;
import java.awt.Container;
import java.awt.Component;
import javax.help.HelpBroker;

/**
 * A factory which can be used to create PropertyEditorUI's.
 */
public class DesktopPropertyEditorFactory extends PropertyEditorFactory {
  JDesktopPane desktop;

  /**
   * Creates a PropertyEditorFactory using the given VariableBundle as
   * a source.
   */
  public DesktopPropertyEditorFactory(VariableBundle bundle, JDesktopPane newDesktop, IconManager manager, HelpBroker broker) {
    super(bundle, manager, broker);
    desktop = newDesktop;
  }

  /**
   * Creates a PropertyEditorFactory using the given VariableBundle as
   * a source.
   */
  public DesktopPropertyEditorFactory(VariableBundle bundle, IconManager manager, HelpBroker broker) {
    this(bundle, null, manager, broker);
  }

  /**
   * Returns the desktop.
   */
  public JDesktopPane getDesktop() {
    return desktop;
  }

  /**
   * Sets the desktop.
   */
  public void setDesktop(JDesktopPane newDesktop) {
    desktop = newDesktop;
  }

  /**
   * Shows an error message.
   */
  public void showError(Object component, String errorMessage) {
    JOptionPane.showInternalMessageDialog(desktop, errorMessage);
  }

  /**
   * Shows an input dialog.
   */
  public String showInputDialog(SwingPropertyEditor dpe, String query) {
    return JOptionPane.showInternalInputDialog(desktop, query);
  }

  /**
   * Creates and displays an editor window.
   */
  public void showNewEditorWindow(String title, PropertyEditorUI editor, Container window) {
    JInternalFrame jif = (JInternalFrame) createEditorWindow(title, editor, window);

    //jif.pack();
    jif.setSize(jif.getPreferredSize());
    if (window != null && window instanceof JInternalFrame) {
      jif.setLocation(Math.max(0, ((window.getWidth() - jif.getWidth()) / 2) + window.getX()), Math.max(0, ((window.getHeight() - jif.getHeight()) / 2) + window.getY()));
    }
    desktop.add(jif);
    jif.setVisible(true);
    try {
      jif.setSelected(true);
    } catch (java.beans.PropertyVetoException pve) {
    }
  }

  public void showNewEditorWindow(String title, String property, String template, String propertyBase, PropertyEditorManager mgr, Container window) {
    JInternalFrame jif = (JInternalFrame) createEditorWindow(title, property, template, propertyBase, mgr, window);
    //jif.pack();
    jif.setSize(jif.getPreferredSize());
    if (window != null && window instanceof JInternalFrame) {
      jif.setLocation(Math.max(0, ((window.getWidth() - jif.getWidth()) / 2) + window.getX()), Math.max(0, ((window.getHeight() - jif.getHeight()) / 2) + window.getY()));
    }
    desktop.add(jif);
    jif.setVisible(true);
    try {
      jif.setSelected(true);
    } catch (java.beans.PropertyVetoException pve) {
    }
  }

  /**
   * This method returns an EditorWindow (a JFrame in this
   * implementation) which has an editor for each property in the
   * properties Vector.  The title string is the title of the
   * JInternalFrame.
   */
  public Container createEditorWindow(String title, PropertyEditorUI editor, Container window) {
    JInternalFrame jif = new JInternalFrame(title, true, true);
    PropertyEditorPane pep = createPropertyEditorPane(editor.getManager(), (SwingPropertyEditor) editor, jif);
    jif.getContentPane().add(pep);
    jif.pack();
    jif.setSize(jif.getPreferredSize());
    return jif;
  }

}
