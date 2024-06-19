package net.suberic.pooka.gui;
import net.suberic.util.gui.*;
import net.suberic.pooka.Pooka;
import javax.swing.*;
import java.awt.event.*;
import net.suberic.util.*;

/**
 * This is a specialized Menu which shows the currently open windows in
 * the FolderPanel.
 */
public class WindowMenu extends ConfigurableMenu {

    int originalMenuCount;
    MessagePanel messagePanel = null;
    
    public WindowMenu() {
	super();
    }

    /**
     * Overrides ConfigurableMenu.configureComponent().
     *
     * Actually just calls ConfigurableMenu.configureComponent() and then
     * sets that number of menuItems as the original values.
     */

    public void configureComponent(String key, VariableBundle vars) {
	super.configureComponent(key, vars);
	
	originalMenuCount = this.getMenuComponentCount();
	if (Pooka.getMainPanel().getContentPanel() instanceof MessagePanel)
	    messagePanel = (MessagePanel) Pooka.getMainPanel().getContentPanel();
    }

    /**
     * Overrides ConfigurableMenu.setActive(Hashtable);
     *
     * calls refreshWindows().
     */

    public void setActive(java.util.Hashtable newCommands) {
	refreshWindows();
    }

    /**
     * This method actually does all of the interesting work for this
     * component.
     */
    public void refreshWindows() {
	if (messagePanel != null) {
	    for (int k = this.getMenuComponentCount(); k > originalMenuCount; k--)
		this.remove(k-1);
	    
	    JInternalFrame[] allFrames = messagePanel.getAllFrames();
	    for(int j = 0; j < allFrames.length; j++) {
		JMenuItem mi = new JMenuItem(allFrames[j].getTitle());
		mi.addActionListener(new ActivateWindowAction());
		mi.setActionCommand(String.valueOf(messagePanel.getIndexOf(allFrames[j])));
		this.add(mi);
	    }
	}
    }

    class ActivateWindowAction extends AbstractAction {
	
	ActivateWindowAction() {
	    super("activate-window");
	}

        public void actionPerformed(ActionEvent e) {
	    if (messagePanel != null) {
		try { 
		    ((JInternalFrame)(messagePanel.getComponent(Integer.parseInt(e.getActionCommand())))).setSelected(true);
		} catch (java.beans.PropertyVetoException pve) {
		} catch (NumberFormatException nfe) {
		}
	    }
	}
    }
    

} 
