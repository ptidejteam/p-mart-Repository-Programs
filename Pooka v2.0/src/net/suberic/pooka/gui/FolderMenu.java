package net.suberic.pooka.gui;

import javax.swing.*;
import java.util.*;
import net.suberic.pooka.Pooka;
import net.suberic.pooka.FolderInfo;
import net.suberic.util.*;

/**
 * A Menu that shows the list of all folders available.
 */
public class FolderMenu extends net.suberic.util.gui.ConfigurableMenu {

  Vector folderList;
  FolderPanel fPanel;
  
  //Hashtable oldCommands = new Hashtable();
  
  /**
   * This creates a new FolderMenu.
   */
  public FolderMenu() {
  }
  
  /**
   * Overrides ConfigurableMenu.configureComponent().
   */
  public void configureComponent(String key, VariableBundle vars) {
    try {
      setText(vars.getProperty(key + ".Label"));
    } catch (MissingResourceException mre) {
    }
    
    this.setActionCommand(vars.getProperty(key + ".Action", "message-move"));
    
    fPanel = Pooka.getMainPanel().getFolderPanel();
    
    MailTreeNode root =  (MailTreeNode)fPanel.getFolderTree().getModel().getRoot();
    buildMenu(root, this);
  }
  
  /**
   * This recursively builds the menu
   */
  protected void buildMenu(MailTreeNode mtn, JMenu currentMenu)
  {
    Enumeration children = mtn.children();
    while(children.hasMoreElements())
      {
	MailTreeNode curNode = (MailTreeNode)children.nextElement();
	if(curNode.isLeaf())
	  {
	    JMenuItem mi = new FolderMenuItem(curNode.toString(), ((FolderNode)curNode).getFolderInfo());
	    mi.setActionCommand(getActionCommand());
	    currentMenu.add(mi);
	  }
	else // Create a submenu
	  {
	    JMenu newMenu = new JMenu(curNode.toString());
	    currentMenu.add(newMenu);
	    buildMenu(curNode, newMenu);
	  }
      }	
    }
  
  
  /**
   * Recursively sets the child menu items to enabled or disabled as appropriate.
   * Also sets up their command handlers for the right folder.
   */
  protected void setActiveSubMenuItems(JMenu curMenu) {
    
    for (int j = 0; j < curMenu.getItemCount(); j++) {
      if(curMenu.getItem(j) instanceof FolderMenuItem)
	{
	  JMenuItem mi = curMenu.getItem(j);
	  java.awt.event.ActionListener[] oldActionListeners = mi.getActionListeners();
	  for (int i = 0; i < oldActionListeners.length; i++) {
	    mi.removeActionListener(oldActionListeners[i]);
	  }

	  Action a = getAction(getActionCommand());
	  if (a != null) {
	    Action newAction = a;
	    if (a instanceof net.suberic.util.DynamicAbstractAction) {
	      try {
		newAction = (Action)((net.suberic.util.DynamicAbstractAction)a).cloneDynamicAction();
	      } catch (CloneNotSupportedException cnse) {
				// sigh.  this is a really bad idea.  
		
		System.out.println("cnse hit.");
	      }
	    } else {
	      System.out.println("action is not a DynamicAbstractAction.");
	    }
	    newAction.putValue("target", ((FolderMenuItem)mi).getFolderInfo());
	    /*
	      Object o = oldCommands.get(mi);
	      if (o != null)
	      mi.removeActionListener((Action)o);
	    */
	    mi.addActionListener(newAction);

	    //oldCommands.put(mi, newAction);

	    mi.setEnabled(true);
	  } else {
	    mi.setEnabled(false);
	  } 
	}
      else
	setActiveSubMenuItems((JMenu)curMenu.getItem(j));
    }
  }
  
  public void setActiveMenuItems()
  {
    setActiveSubMenuItems(this);
  }
  
  /**
   * Your basic menu item plus room to hang a FolderInfo class.
   */
  class FolderMenuItem extends JMenuItem
  {
    public FolderMenuItem(String text, FolderInfo fi)
    {
      super(text);
      folderInfo = fi;
    }
    
    public FolderInfo getFolderInfo() { return folderInfo; }
    
    private FolderInfo folderInfo;
  }
}


