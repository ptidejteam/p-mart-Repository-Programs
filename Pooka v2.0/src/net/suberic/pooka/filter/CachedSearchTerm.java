package net.suberic.pooka.filter;
import javax.mail.*;
import javax.mail.search.*;
import net.suberic.pooka.*;
import net.suberic.pooka.cache.*;

/**
 * This is a SearchTerm which checks for messages that are not cached.
 */
public class CachedSearchTerm extends SearchTerm {

  /**
   * Creates the given CachedSearchTerm.  Note that you have to
   * have a FolderInfo to check for the cache.
   */
  public CachedSearchTerm () {
  }

  /**
   * Checks to see if the given Message is cached or not.
   */
  public boolean match(Message m) {
    if (m instanceof CachingMimeMessage) {
      CachingMimeMessage cmm = (CachingMimeMessage) m;
      long uid = cmm.getUID();
      CachingFolderInfo folder = (CachingFolderInfo) cmm.getParent();
      return (folder.isCached(uid));
    } else {
      return false;
    }
  }
}
