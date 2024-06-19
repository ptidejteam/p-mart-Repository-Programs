package net.suberic.util.event;
import java.util.Hashtable;
import javax.swing.Action;

/**
 * This is an event which indicates that a ConifugrableUI element should
 * update its Action list.
 */
public class UpdateActionsEvent {
    
    Hashtable commands = null;
    Action[] actions = null;
    
    /** 
     * This creates a new UpdateActionsEvents with a Hashtable of new
     * Actions.
     */
    public UpdateActionsEvent(Hashtable newCommands) {
	commands = newCommands;
    }

    /**
     * This creates a new UpdateActionsEvent from an array of Actions.
     */

    public UpdateActionsEvent(Action[] newActions) {
	actions=newActions;
    }

    public Hashtable getCommands() {
	return commands;
    }

    public Action[] getActions() {
	return actions;
    }

    public boolean hasCommands() {
	return (commands != null);
    }

    public boolean hasActions() {
	return (actions != null);
    }
}
