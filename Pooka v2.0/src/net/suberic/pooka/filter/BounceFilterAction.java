package net.suberic.pooka.filter;
import net.suberic.pooka.gui.MessageProxy;
import net.suberic.pooka.Pooka;

import javax.mail.Address;
import javax.mail.internet.InternetAddress;

import java.util.List;
import java.util.ArrayList;


/**
 * A filter which bounces the given message(s) to another folder.
 */
public class BounceFilterAction implements FilterAction {
  
  private Address[] targetAddresses= null;

  private boolean removeBounced = false;

  public BounceFilterAction() {
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
    List removed = new ArrayList();
    for (int i = 0; i < filteredMessages.size(); i++) {
      MessageProxy current = (MessageProxy) filteredMessages.get(i);
      current.bounceMessage(getTargetAddresses(), removeBounced, false);
      if (removeBounced) {
	removed.add(current);
      }
    }

    return removed;
  }
  
  /**
   * Initializes the FilterAction from the sourceProperty given.
   * 
   * This takes the .targetFolder subproperty of the given sourceProperty
   * and assigns its value as the folderName String.
   */
  
  public void initializeFilter(String sourceProperty) {
    try {
      String addressString = Pooka.getProperty(sourceProperty + ".targetAddresses", "");
      targetAddresses = InternetAddress.parse(addressString, false);

      removeBounced = Pooka.getProperty(sourceProperty + ".removeBounced", "false").equalsIgnoreCase("true");
    } catch (javax.mail.MessagingException me) {
      String errorMessage = Pooka.getProperty("error.bounceMessage.addresses", "Error parsing address entry");
      if (Pooka.getUIFactory() != null) {
	Pooka.getUIFactory().showError(errorMessage + ":  " + sourceProperty, me);
      } else {
	System.err.println(errorMessage + ":  " + sourceProperty);
	me.printStackTrace();
      }
    }
  }
  
  /**
   * Returns the targetFolder.  If the targetFolder has not yet been 
   * loaded. calls Pooka.getStoreManager.getFolder(folderName) to 
   * cache the targetFolder.
   */
  public Address[] getTargetAddresses() {
    return targetAddresses;
  }

}
