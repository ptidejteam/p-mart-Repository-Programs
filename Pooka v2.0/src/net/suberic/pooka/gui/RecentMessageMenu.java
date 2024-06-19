package net.suberic.pooka.gui;

import java.awt.*;
import java.util.*;
import net.suberic.pooka.Pooka;
import net.suberic.pooka.FolderInfo;
import net.suberic.util.*;

/**
 * A Menu that shows the list of all new messages available.
 */
public class RecentMessageMenu extends net.suberic.util.gui.ConfigurableAwtMenu {

  /**
   * This creates a new RecentMessageMenu.
   */
  public RecentMessageMenu() {
    if (Pooka.getUIFactory() != null) {
      MessageNotificationManager mnm = Pooka.getUIFactory().getMessageNotificationManager();
      if (mnm != null)
        mnm.setRecentMessageMenu(this);
    }
  }

  /**
   * Overrides ConfigurableAwtMenu.configureComponent().
   */
  public void configureComponent(String key, VariableBundle vars) {
    try {
      setLabel(vars.getProperty(key + ".Label"));
    } catch (MissingResourceException mre) {
    }

    this.setActionCommand(vars.getProperty(key + ".Action", "message-open"));

    /*
      MessageNotificationManager mnm = Pooka.getMainPanel().getMessageNotificationManager();
      Map newMessageMap = mnm.getNewMessageMap();
      Iterator folders = newMessageMap.keySet().iterator();
      while (folders.hasNext()) {
      String current = (String) folders.next();
      buildFolderMenu(current, (List)newMessageMap.get(current));
      }
    */

  }

  /**
   * This builds the menu for each folder/message group.
   */
  protected void buildFolderMenu(String pFolderName, java.util.List pMessageList) {
    MessageNotificationManager mnm = Pooka.getUIFactory().getMessageNotificationManager();
    Menu newMenu = new Menu(pFolderName);
    for(int i = 0 ; i < pMessageList.size(); i++) {
      MenuItem mi = new MenuItem();
      net.suberic.pooka.MessageInfo messageInfo = (net.suberic.pooka.MessageInfo) pMessageList.get(i);
      javax.swing.Action oma = mnm.new OpenMessageAction(messageInfo);
      mi.setActionCommand((String) oma.getValue(javax.swing.Action.NAME));
      mi.addActionListener(oma);

      try {
        mi.setLabel(messageInfo.getMessageProperty("From") + ":  " + messageInfo.getMessageProperty("Subject"));
      } catch (Exception e) {
        mi.setLabel("new message");
      }
      newMenu.add(mi);

    }
    this.add(newMenu);
  }

  void reset() {
    Runnable runMe = new Runnable() {
        public void run() {
          removeAll();
          MessageNotificationManager mnm = Pooka.getUIFactory().getMessageNotificationManager();
          if (mnm != null) {
            Map newMessageMap = mnm.getNewMessageMap();
            Iterator folders = newMessageMap.keySet().iterator();
            while (folders.hasNext()) {
              String current = (String) folders.next();
              buildFolderMenu(current, (java.util.List)newMessageMap.get(current));
            }
          }
        }
      };

    if (javax.swing.SwingUtilities.isEventDispatchThread()) {
      runMe.run();
    } else {
      javax.swing.SwingUtilities.invokeLater(runMe);
    }

  }
}
