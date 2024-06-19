package net.suberic.pooka.gui;
import javax.swing.tree.*;
import javax.swing.*;
import java.util.Hashtable;
import java.awt.event.*;
import net.suberic.util.gui.ConfigurablePopupMenu;
import net.suberic.pooka.Pooka;


public class MailTreeNode extends DefaultMutableTreeNode {

  public Action[] defaultActions = null;

  public Hashtable commands;

  public JComponent parentContainer;

  // this is only used for the FolderChooser
  private boolean subscribed = false;

  public ConfigurablePopupMenu popupMenu;

  MailTreeNode(Object userObj, JComponent parent) {
    super(userObj);

    parentContainer = parent;

  }

  /**
   * This shows the PopupMenu for this component.  If no PopupMenu has
   * been created yet, then one is created and shown.
   */
  public void showPopupMenu(JComponent component, MouseEvent e) {
    configurePopupMenu();

    if (popupMenu != null)
      popupMenu.show(component, e.getX(), e.getY());

  }

  /**
   * This updates the popupMenu's Theme, if necessary.
   */
  public void updatePopupTheme() {
    if (popupMenu != null) {
      SwingUtilities.invokeLater( new Runnable() {
          public void run() {
            try {
              FolderPanel fp =  ((FolderPanel)getParentContainer());
              Pooka.getUIFactory().getPookaThemeManager().updateUI(fp, popupMenu, true);
            } catch (Exception e) {

            }
          }
        });
    }
  }

  /**
   * This creates the current PopupMenu if there is not one.  It then
   * will configure the PopupMenu with the current actions.
   *
   * This implementation simply returns; subclasses should override this
   * method to create a custom PopupMenu.
   */
  public void configurePopupMenu() {

  }

  protected void setCommands() {
    commands = new Hashtable();

    Action[] actions = getActions();
    if (actions != null) {
      for (int i = 0; i < actions.length; i++) {
        Action a = actions[i];
        commands.put(a.getValue(Action.NAME), a);
      }
    }

  }


  public Action[] getActions() {
    return getDefaultActions();
  }

  public Action getAction(String name) {
    if (commands != null)
      return (Action)commands.get(name);
    else
      return null;
  }

  public Action[] getDefaultActions() {
    return defaultActions;
  }

  public JComponent getParentContainer() {
    return parentContainer;
  }

  public boolean isSubscribed() {
    return subscribed;
  }

  public void setSubscribed(boolean newValue) {
    subscribed=newValue;
  }
}

