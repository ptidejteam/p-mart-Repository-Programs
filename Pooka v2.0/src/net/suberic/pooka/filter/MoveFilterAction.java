package net.suberic.pooka.filter;
import net.suberic.pooka.gui.MessageProxy;
import java.util.List;
import java.util.ArrayList;
import javax.mail.MessagingException;
import net.suberic.pooka.FolderInfo;
import net.suberic.pooka.OperationCancelledException;
import net.suberic.pooka.Pooka;

/**
 * A filter which moves the given message(s) to another folder.
 */
public class MoveFilterAction implements FilterAction {

  private FolderInfo targetFolder = null;
  private String folderName = null;

  public MoveFilterAction() {
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
    List moved = new ArrayList();
    try {
      for (int i = 0; i < filteredMessages.size(); i++) {
        MessageProxy current = (MessageProxy) filteredMessages.get(i);
        current.moveMessage(getTargetFolder(), false);
        moved.add(current);
      }
    } catch (OperationCancelledException oce) {

    } catch (MessagingException me) {
      if (Pooka.getUIFactory() != null) {
        Pooka.getUIFactory().showError(Pooka.getProperty("error.Message.MoveFilterMessage", "Error:  could not apply move filter.") +"\n", me);
      }
    }
    return moved;
  }

  /**
   * Initializes the FilterAction from the sourceProperty given.
   *
   * This takes the .targetFolder subproperty of the given sourceProperty
   * and assigns its value as the folderName String.
   */

  public void initializeFilter(String sourceProperty) {
    folderName = Pooka.getProperty(sourceProperty + ".targetFolder", "");
  }

  /**
   * Returns the targetFolder.  If the targetFolder has not yet been
   * loaded. calls Pooka.getStoreManager.getFolder(folderName) to
   * cache the targetFolder.
   */
  public FolderInfo getTargetFolder() {
    if (targetFolder == null)
      targetFolder = Pooka.getStoreManager().getFolder(folderName);

    return targetFolder;
  }

}
