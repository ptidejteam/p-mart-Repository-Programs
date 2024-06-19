package net.suberic.pooka.filter;
import javax.mail.Message;

/**
 * A Spam filter interace.
 */
public interface SpamFilter {

  /**
   * Idenfies whether or not the given message is spam.  If so, marks the
   * message as such and returns true.
   */
  public boolean isSpam(Message msg);

  /**
   * Mark this message as not being spam.  Called to correct a message that
   * has been misidentified as spam.
   */
  public void isNotSpam(Message msg);

}
