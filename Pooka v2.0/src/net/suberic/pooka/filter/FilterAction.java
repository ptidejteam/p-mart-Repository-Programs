package net.suberic.pooka.filter;
import net.suberic.pooka.gui.MessageProxy;
import java.util.List;

public interface FilterAction {

  /**
   * Runs the filterAction on each MessageProxy in the filteredMessages
   * Vector.
   *
   * @param filteredMessages messages which have met the filter condition
   * and need to have the FilterAction performed on them.
   *
   * @return messages which are removed from their original folder
   * by the filter.
   */
  public List performFilter(List filteredMessages);

  /**
   * Initializes the FilterAction from the sourceProperty given.
   */

  public void initializeFilter(String sourceProperty);
}
