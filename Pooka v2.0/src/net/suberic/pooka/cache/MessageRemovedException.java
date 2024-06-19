package net.suberic.pooka.cache;

import javax.mail.*;

/**
 * Shows that a message has been removed from the cache and, if the
 * server is connected, is also not available on the server.
 */
public class MessageRemovedException extends MessagingException {

  public MessageRemovedException(String errorMessage) {
    super(errorMessage);
  }
}
