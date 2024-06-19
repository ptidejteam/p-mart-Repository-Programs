package net.suberic.pooka.filter;
import javax.mail.*;
import javax.mail.search.*;
import net.suberic.pooka.*;
import net.suberic.pooka.cache.*;

/**
 * This is a SearchTerm which checks for messages that are in the process
 * of being removed.
 * 
 * This is actually just a place holder and a big hack.  sigh.
 */
public class DeleteInProgressSearchTerm extends SearchTerm {

  /**
   * Creates the given DeleteInProgressSearchTerm.
   */
  public DeleteInProgressSearchTerm () {
  }

  /**
   * Checks to see if the given Message is being removed or not.
   */
  public boolean match(Message m) {
    return false;
  }
}
