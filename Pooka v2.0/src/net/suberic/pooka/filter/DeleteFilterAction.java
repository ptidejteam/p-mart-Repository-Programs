package net.suberic.pooka.filter;
import net.suberic.pooka.gui.MessageProxy;
import java.util.List;

/**
 * A FilterActions which deletes all of the given Messages.
 */
public class DeleteFilterAction implements FilterAction {

  public DeleteFilterAction() {

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
    List deleted = new java.util.LinkedList();
    for (int i = 0; i < filteredMessages.size(); i++) {
      MessageProxy current = (MessageProxy) filteredMessages.get(i);
      current.deleteMessage(false);
      deleted.add(current);
    }

    return deleted;
  }

  /**
   * Initializes the FilterAction from the sourceProperty given.
   */

  public void initializeFilter(String sourceProperty) {
    // no initialization necessary.
  }
}
