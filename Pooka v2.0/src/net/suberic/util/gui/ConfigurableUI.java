package net.suberic.util.gui;
import java.util.Hashtable;
import net.suberic.util.VariableBundle;
import javax.swing.Action;

/**
 * This defines a UI component which may be built dynamically using a 
 * set of properties in a VariableBundle, and then may have the Actions
 * associated with the individual buttons/menu items/whatevers updated
 * dynamically to reflect the new values.
 *
 * In general, the format for the properties which define a ConfigurableUI
 * component are as follows:
 * 
 * MenuBar=File:Edit:Mail:Window:Help
 *
 * MenuBar.File=NewFolder:NewMail:OpenFolder:OpenMessage:Close:SaveAs:Print:Exit
 * MenuBar.File.Label=File
 * 
 * MenuBar.File.NewFolder.Action=folder-new
 * MenuBar.File.NewFolder.Image=images/New.gif
 * MenuBar.File.NewFolder.KeyBinding=F
 * MenuBar.File.NewFolder.Label=New Folder
 *
 * where MenuBar would be the name of the 'root' configuration property,
 * 'MenuBar.File' is the first submenu, and 'MenuBar.File.NewFolder' is 
 * the first actual 'button' configured.  On the NewFolder MenuItem, the
 * 'Action' is the name of the Action which will be run, and is the 
 * central part of the configuration.  The rest (Image, KeyBinding, and 
 * Label) just control how the item is displayed and invoked.  The
 * 'KeyBinding' and 'Label' items should probably be put in a localized
 * file if you want to internationalize your application.
 */

public interface ConfigurableUI {
    
    /**
     * This configures the UI Component with the given ID and 
     * VariableBundle.
     */ 
    public void configureComponent(String ID, VariableBundle vars);

    /**
     * This updates the Actions on the UI Component.
     *
     * The commands Hashtable is expected to be a table with the Action
     * names as keys, and the Actions themselves as values.
     */

    public void setActive(Hashtable commands);

    /**
     * This updates the Actions on the UI Component.
     *
     */

    public void setActive(Action[] newActions);
}
    
