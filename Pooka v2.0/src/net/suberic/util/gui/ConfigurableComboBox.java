package net.suberic.util.gui;
import javax.swing.*;
import net.suberic.util.VariableBundle;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.MissingResourceException;
import javax.swing.Action;
import java.awt.event.*;

/**
 * This is a JComboBox which implements the ConfigurableUI interface, and
 * therefore may be dynamically created using a VariableBundle and key,
 * and updated using an array of Actions.
 */

public class ConfigurableComboBox extends JComboBox implements ConfigurableUI {

  // the latest commands list.  i'm storing this for now because i 
  // can't do a JButton.removeActionListeners().
  
  protected HashMap selectionMap = new HashMap();

  protected Hashtable commands = new Hashtable();

  String mKey = null;

  int minWidth = -1;
  int minHeight = -1;

  public ConfigurableComboBox() {
    super();
  }
  
  /**
   * This creates a new ConfigurableComboBox using the buttonID as the
   * configuration key, and vars as the source for the values of all the
   * properties.
   *
   * If buttonID doesn't exist in vars, then this returns an empty 
   * ComboBox.
   */
  public ConfigurableComboBox(String buttonID, VariableBundle vars) {
    super();
    
    configureComponent(buttonID, vars);
  }
  
  /**
   * This configures the ComboBox using the given buttonID and 
   * VariableBundle.
   *
   * As defined in interface net.suberic.util.gui.ConfigurableUI.
   */
  public void configureComponent(String key, VariableBundle vars) {
    this.setRenderer(new ConfigurableComboRenderer());

    mKey = key;

    StringTokenizer iKeys = null;
    try {
      iKeys = new StringTokenizer(vars.getProperty(key), ":");
    } catch (MissingResourceException mre) {
      mre.printStackTrace();
      try {
	System.err.println(vars.getProperty("error.NoSuchResource") + " " + mre.getKey());
      } catch (MissingResourceException mretwo) {
	System.err.println("Unable to load resource " + mre.getKey());
	return;
      } 
      return;
    }
    String currentToken;

    while (iKeys.hasMoreTokens()) {
      currentToken=iKeys.nextToken();
      Object i = createComboBoxItem(key + "." + currentToken, vars);
      this.addItem(i);
    }

    this.addItemListener(new ItemListener() {
	public void itemStateChanged(ItemEvent e) {
	  if (e.getStateChange() == ItemEvent.SELECTED) {
	    Object selectedItem = e.getItem();
	    String cmd = (String)selectionMap.get(selectedItem);
	    if (cmd != null) {
	      Action action = getAction(cmd);
	      if (action != null) {
		action.actionPerformed(new ActionEvent(e.getSource(), e.getID(), cmd));
	      }
	    }
	  }
	}
      });

    this.setMaximumSize(this.getPreferredSize());

    String toolTip = vars.getProperty(key + ".ToolTip", "");
    if (toolTip != "") {
      setToolTipText(toolTip);
    }
  }


  /**
   * And this actually creates the menu items themselves.
   */
  protected Object createComboBoxItem(String buttonID, VariableBundle vars) {

    ImageIcon returnValue = null;

    IconManager iconManager = IconManager.getIconManager(vars, "IconManager._default");
    ImageIcon icon = iconManager.getIcon(vars.getProperty(buttonID + ".Image"));
    if (icon != null) {

      if (minWidth < 0) {
	minWidth = icon.getIconWidth();
      } else {
	minWidth = java.lang.Math.min(minWidth, icon.getIconWidth());
      }
      
      if (minHeight < 0) {
	minHeight = icon.getIconHeight();
      } else {
	minHeight = java.lang.Math.min(minHeight, icon.getIconHeight());
      }
      
      
      //returnValue.setIcon(icon);
      returnValue = icon;
    }
    
    String cmd = vars.getProperty(buttonID + ".Action", buttonID);
    
    selectionMap.put(returnValue, cmd);	
    
    return returnValue;
  }
  
  /**
   * As defined in net.suberic.util.gui.ConfigurableUI
   */
  public void setActive(javax.swing.Action[] newActions) {
    Hashtable tmpHash = new Hashtable();
    if (newActions != null && newActions.length > 0) {
      for (int i = 0; i < newActions.length; i++) {
	String cmdName = (String)newActions[i].getValue(Action.NAME);
	tmpHash.put(cmdName, newActions[i]);
      }
    }
    setActive(tmpHash);	
  }
  
  /**
   * As defined in net.suberic.util.gui.ConfigurableUI
   */
  public void setActive(Hashtable newCommands) {
    commands = newCommands;
  }
  
  /**
   * This gets an action from the supported commands.  If there is no
   * supported action, it returns null
   */
  
  public Action getAction(String command) {
    return (Action)commands.get(command);
  }

  class ConfigurableComboRenderer extends JLabel implements ListCellRenderer {

    public ConfigurableComboRenderer() {
      setOpaque(true);
      setHorizontalAlignment(LEFT);
      //setHorizontalAlignment(CENTER);
      setVerticalAlignment(CENTER);
    }
    
    public java.awt.Component getListCellRendererComponent(
	JList list,
        Object value,
        int index,
        boolean isSelected,
        boolean cellHasFocus)
    {
      if (isSelected) {
	setBackground(list.getSelectionBackground());
	setForeground(list.getSelectionForeground());
      } else {
	setBackground(list.getBackground());
	setForeground(list.getForeground());
      }
      
      ImageIcon icon = (ImageIcon)value;
      //setText(icon.getDescription());
      setIcon(icon);
      return this;
    }
  }
}
