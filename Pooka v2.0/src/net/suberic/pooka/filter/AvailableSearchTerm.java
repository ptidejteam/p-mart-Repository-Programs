package net.suberic.pooka.filter;
import javax.mail.*;
import javax.mail.search.*;
import net.suberic.pooka.*;
import net.suberic.pooka.cache.*;

/**
 * This is a SearchTerm which checks for messages that are available--that
 * is, either cached, or the folder is open.
 */
public class AvailableSearchTerm extends SearchTerm {

  /**
   * Creates the given AvailableSearchTerm.  Note that you have to
   * have a FolderInfo to check for the cache.
   */
  public AvailableSearchTerm () {
  }

  /**
   * Checks to see if the given Message is available either directly from
   * the store or in cache.
   */
  public boolean match(Message m) {
    if (m instanceof CachingMimeMessage) {
      CachingMimeMessage cmm = (CachingMimeMessage) m;
      long uid = cmm.getUID();
      CachingFolderInfo folder = (CachingFolderInfo) cmm.getParent();
      return (folder.isConnected() || folder.isCached(uid));
    } else {
      return false;
    }
  }
}
