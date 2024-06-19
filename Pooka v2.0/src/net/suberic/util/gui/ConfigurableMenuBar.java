package net.suberic.util.gui;
import javax.swing.*;
import net.suberic.util.VariableBundle;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.MissingResourceException;
import javax.swing.Action;

/**
 * This is a JMenuBar which implements the ConfigurableUI interface, and
 * therefore may be dynamically created using a VariableBundle and key,
 * and updated using an array of Actions.
 */

public class ConfigurableMenuBar extends JMenuBar implements ConfigurableUI {

    // the latest commands list.  i'm storing this for now because i 
    // can't do a JButton.removeActionListeners().

    private Hashtable commands = new Hashtable();

    /**
     * This creates a new ConfigurableMenuBar using the menubarID as the
     * configuration key, and vars as the source for the values of all the
     * properties.
     *
     * If menubarID doesn't exist in vars, then this returns an empty 
     * Menubar.
     */

    public ConfigurableMenuBar(String menuBarID, VariableBundle vars) {
	super();
	
	configureComponent(menuBarID, vars);
    }

    /**
     * This configures the Menubar using the given menubarID and 
     * VariableBundle.
     *
     * As defined in interface net.suberic.util.gui.ConfigurableUI.
     */

    public void configureComponent(String menubarID, VariableBundle vars) {
	if ((menubarID != null) && (vars.getProperty(menubarID, "") != "")) {
	    StringTokenizer tokens = new StringTokenizer(vars.getProperty(menubarID, ""), ":");
	    while (tokens.hasMoreTokens()) {
		String currentMenu = tokens.nextToken();
		ConfigurableMenu m;
		if (vars.getProperty(menubarID + "." + currentMenu + ".class", "").equals("")) {
		    m = new ConfigurableMenu(menubarID + "." + currentMenu, vars);
		} else {
		    // this means we're using a custom Menu.

		    try {
			Class menuClass = Class.forName(vars.getProperty(menubarID + "." + currentMenu + ".class", "net.suberic.util.gui.ConfigurableMenu"));
			m = (ConfigurableMenu) menuClass.newInstance();
			m.configureComponent(menubarID + "." + currentMenu, vars);
		    } catch (Exception e) {
			// if we get any errors, just create a plain 
			// ConfigurableMenu.
			m = new ConfigurableMenu(menubarID + "." + currentMenu, vars);
		    }
		}
		if (m != null) {
			this.add(m);
		}
	    }
	}
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
	setActiveMenus();
    }

    private void setActiveMenus() {
	for (int i = 0; i < getMenuCount(); i++) {
	    ((ConfigurableMenu)getMenu(i)).setActive(commands);
	}
    }

    /**
     * This gets an action from the supported commands.  If there is no
     * supported action, it returns null
     */
    
    public Action getAction(String command) {
	return (Action)commands.get(command);
    }


}
