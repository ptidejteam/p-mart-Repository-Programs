package net.suberic.pooka.gui;
import java.util.*;

/**
 * A ConfigurableComboBox that should remain in sync with the display
 * style.
 */
public class DisplayStyleComboBox extends net.suberic.util.gui.ConfigurableComboBox {
  
  public boolean displayStyle = false;
  public boolean headerStyle = false;

  /**
   * This configures the ComboBox using the given buttonID and 
   * VariableBundle.
   *
   * As defined in interface net.suberic.util.gui.ConfigurableUI.
   */
  public void configureComponent(String key, net.suberic.util.VariableBundle vars) {
    super.configureComponent(key, vars);

    // set whether this is a header combo, a display combo, or both.

    for (int i = 0; i < getItemCount(); i++) {
      String cmd = (String) selectionMap.get(getItemAt(i));
      if (cmd.equalsIgnoreCase("file-open-textdisplay") || cmd.equalsIgnoreCase("file-open-htmldisplay") || cmd.equalsIgnoreCase("file-open-rawdisplay")) {
	displayStyle = true;
      } else if (cmd.equalsIgnoreCase("file-open-defaultdisplay") || cmd.equalsIgnoreCase("file-open-fulldisplay")) {
	headerStyle=true;
      }
    }

  } 

  /**
   * Called when either style is updated.
   */
  public void styleUpdated(int newDisplayStyle, int newHeaderStyle) {
    // find out which of the items we have corresponds to the given display
    // and/or header style.

    for (int i = 0; i < getItemCount(); i++) {
      String cmd = (String) selectionMap.get(getItemAt(i));
      if (cmd != null) {
	javax.swing.Action currentAction = getAction(cmd);
	while (currentAction instanceof net.suberic.util.thread.ActionWrapper) {
	  currentAction = ((net.suberic.util.thread.ActionWrapper) currentAction).getWrappedAction();
	}
	if (currentAction != null && currentAction instanceof MessageProxy.OpenAction) {
	  MessageProxy.OpenAction oa = (MessageProxy.OpenAction) currentAction;
	  if (((displayStyle && (oa.getDisplayModeValue() == newDisplayStyle)) || !displayStyle) && ((headerStyle && (oa.getHeaderModeValue() == newHeaderStyle)) || !headerStyle)) {
	    if (getSelectedIndex() != i) {
	      setSelectedIndex(i);
	    }
	  }
	}
      }
    }
  }

}
