package net.suberic.pooka.filter;
import net.suberic.pooka.gui.MessageProxy;
import net.suberic.pooka.FolderInfo;
import net.suberic.pooka.Pooka;
import java.util.List;
import java.util.LinkedList;
import javax.mail.*;

/**
 * A FilterAction which sets or unsets a given flag on message(s).
 */
public class FlagFilterAction implements FilterAction {
  
  private String folderName = null;
  private Flags flagToSet;
  private boolean flagValue;
  
  public FlagFilterAction() {
  }
  
  /**
   * Runs the filterAction on each MessageProxy in the filteredMessages
   * List.
   *
   * @param filteredMessages messages which have met the filter condition
   * and need to have the FilterAction performed on them.
   *
   * @return messages which are removed from their original folder
   * by the filter.
   */
  public List performFilter(List filteredMessages) {
    for (int i = 0; i < filteredMessages.size(); i++) {
      try {
	MessageProxy current = (MessageProxy) filteredMessages.get(i);
	current.getMessageInfo().getRealMessage().setFlags(flagToSet, flagValue);
      } catch (MessagingException me) {
      }
      
    }
    
    return new LinkedList();
  }

    /**
     * Initializes the FilterAction from the sourceProperty given.
     * 
     * This takes the .flag subproperty of the given sourceProperty
     * and assigns its value to as the flagToSet.  Also takes the .value
     * subproperty and uses it as the flagValue.
     */
    
    public void initializeFilter(String sourceProperty) {
	String flagName = Pooka.getProperty(sourceProperty + ".flag", "");
	flagToSet = Pooka.getSearchManager().getFlags(flagName);
	String value = Pooka.getProperty(sourceProperty + ".value", "true");
	if (value.equalsIgnoreCase("true"))
	    flagValue = true;
	else
	    flagValue = false;
    }

}
