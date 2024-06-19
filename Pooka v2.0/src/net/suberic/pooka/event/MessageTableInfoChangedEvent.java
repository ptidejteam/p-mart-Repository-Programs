package net.suberic.pooka.event;

import javax.mail.*;
import javax.mail.event.*;

/**
 * A subclass of MessageChangedEvent that indicates that a Message's
 * TableInfo (display values) has been reloaded.
 */
public class MessageTableInfoChangedEvent extends MessageChangedEvent {
  public MessageTableInfoChangedEvent(java.lang.Object source,
				      int type,
				      Message msg) {
    super(source, type, msg);
  }
}
