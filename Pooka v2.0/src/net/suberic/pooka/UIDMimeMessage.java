package net.suberic.pooka;
import javax.mail.*;
import javax.mail.internet.*;
import net.suberic.pooka.FolderInfo;
import net.suberic.pooka.Pooka;
import javax.activation.DataHandler;
import java.util.Enumeration;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/**
 * A wrapper around a MimeMessage which can either work in real or
 * disconnected (cached) mode.
 */

public class UIDMimeMessage extends MimeMessage {

  long uid;
  UIDFolderInfo parent;

  public UIDMimeMessage(UIDFolderInfo parentFolderInfo, long newUid) {
    super(Pooka.getDefaultSession());
    uid = newUid;
    parent = parentFolderInfo;
    saved=true;
    modified=false;
  }

  public int getSize() throws MessagingException {
    try {
      return getMessage().getSize();
    } catch (FolderClosedException fce) {
      int status = parent.getStatus();
      if (status == FolderInfo.CONNECTED || status == FolderInfo.LOST_CONNECTION) {
        try {
          parent.openFolder(Folder.READ_WRITE);
        } catch (Exception me) {
          throw fce;
        }

        return getMessage().getSize();

      } else {
        throw fce;
      }
    }
  }

  protected InputStream getContentStream() throws MessagingException {
    // sigh.  this is pretty much taken from the javamail source.

    try {
      InputStream handlerStream = getInputStream();
      InternetHeaders tmpHeaders = new InternetHeaders(handlerStream);

      byte[] buf;

      int len;
      int size = 1024;

      if (handlerStream instanceof ByteArrayInputStream) {
        size = handlerStream.available();
        buf = new byte[size];
        len = handlerStream.read(buf, 0, size);
      }
      else {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        buf = new byte[size];
        while ((len = handlerStream.read(buf, 0, size)) != -1)
          bos.write(buf, 0, len);
        buf = bos.toByteArray();
      }

      return new ByteArrayInputStream(buf);
    } catch (java.io.IOException ioe) {
      throw new MessagingException("Error getting Content Stream", ioe);
    }

    //throw new MessagingException("No getting the content stream!  Bad code!");
  }

  public synchronized DataHandler getDataHandler()
    throws MessagingException {
    try {
      return getMessage().getDataHandler();
    } catch (FolderClosedException fce) {
      int status = parent.getStatus();
      if (status == FolderInfo.CONNECTED || status == FolderInfo.LOST_CONNECTION) {
        try {
          parent.openFolder(Folder.READ_WRITE);
        } catch (Exception me) {
          throw fce;
        }

        return getMessage().getDataHandler();
      } else {
        throw fce;
      }
    }
  }

  public String[] getHeader(String name)
    throws MessagingException {
    try {
      return getMessage().getHeader(name);
    } catch (FolderClosedException fce) {
      int status = parent.getStatus();
      if (status == FolderInfo.CONNECTED || status == FolderInfo.LOST_CONNECTION) {
        try {
          parent.openFolder(Folder.READ_WRITE);
        } catch (Exception me) {
          throw fce;
        }

        return getMessage().getHeader(name);

      } else {
        throw fce;
      }
    }
  }

  public String getHeader(String name, String delimiter)
    throws MessagingException {
    try {
      return getMessage().getHeader(name, delimiter);
    } catch (FolderClosedException fce) {
      int status = parent.getStatus();
      if (status == FolderInfo.CONNECTED || status == FolderInfo.LOST_CONNECTION) {
        try {
          parent.openFolder(Folder.READ_WRITE);
        } catch (Exception me) {
          throw fce;
        }

        return getMessage().getHeader(name, delimiter);

      } else {
        throw fce;
      }
    }
  }

  public void setHeader(String name, String value)
    throws MessagingException {
    try {
      getMessage().setHeader(name, value);
    } catch (FolderClosedException fce) {
      int status = parent.getStatus();
      if (status == FolderInfo.CONNECTED || status == FolderInfo.LOST_CONNECTION) {
        try {
          parent.openFolder(Folder.READ_WRITE);
        } catch (Exception me) {
          throw fce;
        }

        getMessage().setHeader(name, value);

      } else {
        throw fce;
      }
    }
  }

  public void addHeader(String name, String value)
    throws MessagingException {
    try {
      getMessage().addHeader(name, value);
    } catch (FolderClosedException fce) {
      int status = parent.getStatus();
      if (status == FolderInfo.CONNECTED || status == FolderInfo.LOST_CONNECTION) {
        try {
          parent.openFolder(Folder.READ_WRITE);
        } catch (Exception me) {
          throw fce;
        }

        getMessage().addHeader(name, value);

      } else {
        throw fce;
      }
    }
  }

  public void removeHeader(String name)
    throws MessagingException {
    try {
      getMessage().removeHeader(name);
    } catch (FolderClosedException fce) {
      int status = parent.getStatus();
      if (status == FolderInfo.CONNECTED || status == FolderInfo.LOST_CONNECTION) {
        try {
          parent.openFolder(Folder.READ_WRITE);
        } catch (Exception me) {
          throw fce;
        }

        getMessage().removeHeader(name);

      } else {
        throw fce;
      }
    }
  }

  public Enumeration getAllHeaders() throws MessagingException {
    try {
      return getMessage().getAllHeaders();
    } catch (FolderClosedException fce) {
      if (Pooka.isDebug())
        System.out.println("debug:  caught FolderClosedException.");
      int status = parent.getStatus();
      if (status == FolderInfo.CONNECTED || status == FolderInfo.LOST_CONNECTION) {
        if (Pooka.isDebug())
          System.out.println("debug:  folder should be open.  trying to re-open folder.");
        try {
          parent.openFolder(Folder.READ_WRITE);
        } catch (Exception me) {
          throw fce;
        }

        return getMessage().getAllHeaders();

      } else {
        throw fce;
      }
    }
  }

  public Enumeration getMatchingHeaders(String[] names)
    throws MessagingException {
    try {
      return getMessage().getMatchingHeaders(names);
    } catch (FolderClosedException fce) {
      int status = parent.getStatus();
      if (status == FolderInfo.CONNECTED || status == FolderInfo.LOST_CONNECTION) {
        try {
          parent.openFolder(Folder.READ_WRITE);
        } catch (Exception me) {
          throw fce;
        }

        return getMessage().getMatchingHeaders(names);

      } else {
        throw fce;
      }
    }
  }


  /**
   * Return non-matching headers from this Message as an
   * Enumeration of Header objects. This implementation
   * obtains the header from the <code>headers</code> InternetHeaders object.
   *
   * @exception  MessagingException
   */
  public Enumeration getNonMatchingHeaders(String[] names)
    throws MessagingException {
    try {
      return getMessage().getNonMatchingHeaders(names);
    } catch (FolderClosedException fce) {
      int status = parent.getStatus();
      if (status == FolderInfo.CONNECTED || status == FolderInfo.LOST_CONNECTION) {
        try {
          parent.openFolder(Folder.READ_WRITE);
        } catch (Exception me) {
          throw fce;
        }

        return getMessage().getNonMatchingHeaders(names);

      } else {
        throw fce;
      }
    }
  }

  /**
   * Add a raw RFC 822 header-line.
   *
   * @exceptionIllegalWriteException if the underlying
   *implementation does not support modification
   * @exceptionIllegalStateException if this message is
   *obtained from a READ_ONLY folder.
   * @exception  MessagingException
   */
  public void addHeaderLine(String line) throws MessagingException {
    try {
      getMessage().addHeaderLine(line);
    } catch (FolderClosedException fce) {
      int status = parent.getStatus();
      if (status == FolderInfo.CONNECTED || status == FolderInfo.LOST_CONNECTION) {
        try {
          parent.openFolder(Folder.READ_WRITE);
        } catch (Exception me) {
          throw fce;
        }

        getMessage().addHeaderLine(line);

      } else {
        throw fce;
      }
    }
  }

  /**
   * Get all header lines as an Enumeration of Strings. A Header
   * line is a raw RFC 822 header-line, containing both the "name"
   * and "value" field.
   *
   * @exception  MessagingException
   */
  public Enumeration getAllHeaderLines() throws MessagingException {
    try {
      return getMessage().getAllHeaderLines();
    } catch (FolderClosedException fce) {
      int status = parent.getStatus();
      if (status == FolderInfo.CONNECTED || status == FolderInfo.LOST_CONNECTION) {
        try {
          parent.openFolder(Folder.READ_WRITE);
        } catch (Exception me) {
          throw fce;
        }

        return getMessage().getAllHeaderLines();

      } else {
        throw fce;
      }
    }
  }

  /**
   * Get matching header lines as an Enumeration of Strings.
   * A Header line is a raw RFC 822 header-line, containing both
   * the "name" and "value" field.
   *
   * @exception  MessagingException
   */
  public Enumeration getMatchingHeaderLines(String[] names)
    throws MessagingException {
    try {
      return getMessage().getMatchingHeaderLines(names);
    } catch (FolderClosedException fce) {
      int status = parent.getStatus();
      if (status == FolderInfo.CONNECTED || status == FolderInfo.LOST_CONNECTION) {
        try {
          parent.openFolder(Folder.READ_WRITE);
        } catch (Exception me) {
          throw fce;
        }

        return getMessage().getMatchingHeaderLines(names);

      } else {
        throw fce;
      }
    }
  }

  /**
   * Get non-matching header lines as an Enumeration of Strings.
   * A Header line is a raw RFC 822 header-line, containing both
   * the "name" and "value" field.
   *
   * @exception  MessagingException
   */
  public Enumeration getNonMatchingHeaderLines(String[] names)
    throws MessagingException {
    try {
      return getMessage().getNonMatchingHeaderLines(names);
    } catch (FolderClosedException fce) {
      int status = parent.getStatus();
      if (status == FolderInfo.CONNECTED || status == FolderInfo.LOST_CONNECTION) {
        try {
          parent.openFolder(Folder.READ_WRITE);
        } catch (Exception me) {
          throw fce;
        }

        return getMessage().getNonMatchingHeaderLines(names);

      } else {
        throw fce;
      }
    }
  }

  /**
   * Return a <code>Flags</code> object containing the flags for
   * this message. <p>
   *
   * Note that a clone of the internal Flags object is returned, so
   * modifying the returned Flags object will not affect the flags
   * of this message.
   *
   * @return          Flags object containing the flags for this message
   * @exception  MessagingException
   * @see javax.mail.Flags
   */
  public synchronized Flags getFlags() throws MessagingException {
    try {
      Message m = getMessage();
      if (m != null)
        return m.getFlags();
      else
        throw new MessageRemovedException();
    } catch (FolderClosedException fce) {
      int status = parent.getStatus();
      if (status == FolderInfo.CONNECTED || status == FolderInfo.LOST_CONNECTION) {
        try {
          parent.openFolder(Folder.READ_WRITE);
        } catch (Exception me) {
          throw fce;
        }

        return getMessage().getFlags();

      } else {
        throw fce;
      }
    }
  }

  /**
   * Check whether the flag specified in the <code>flag</code>
   * argument is set in this message. <p>
   *
   * This implementation checks this message's internal
   * <code>flags</code> object.
   *
   * @param flagthe flag
   * @returnvalue of the specified flag for this message
   * @see javax.mail.Flags.Flag
   * @seejavax.mail.Flags.Flag#ANSWERED
   * @seejavax.mail.Flags.Flag#DELETED
   * @seejavax.mail.Flags.Flag#DRAFT
   * @seejavax.mail.Flags.Flag#FLAGGED
   * @seejavax.mail.Flags.Flag#RECENT
   * @seejavax.mail.Flags.Flag#SEEN
   * @exception       MessagingException
   */
  public synchronized boolean isSet(Flags.Flag flag)
    throws MessagingException {
    try {
      return getFlags().contains(flag);
    } catch (FolderClosedException fce) {
      int status = parent.getStatus();
      if (status == FolderInfo.CONNECTED || status == FolderInfo.LOST_CONNECTION) {
        try {
          parent.openFolder(Folder.READ_WRITE);
        } catch (Exception me) {
          throw fce;
        }

        return getFlags().contains(flag);

      } else {
        throw fce;
      }
    }
  }

  /**
   * Set the flags for this message. <p>
   *
   * This implementation modifies the <code>flags</code> field.
   *
   * @exceptionIllegalWriteException if the underlying
   *implementation does not support modification
   * @exceptionIllegalStateException if this message is
   *obtained from a READ_ONLY folder.
   * @exception  MessagingException
   */
  public synchronized void setFlags(Flags flag, boolean set)
    throws MessagingException {
    try {
      getMessage().setFlags(flag, set);
    } catch (FolderClosedException fce) {
      int status = parent.getStatus();
      if (status == FolderInfo.CONNECTED || status == FolderInfo.LOST_CONNECTION) {
        try {
          parent.openFolder(Folder.READ_WRITE);
        } catch (Exception me) {
          throw fce;
        }

        getMessage().setFlags(flag, set);

      } else {
        throw fce;
      }
    }
  }

  /**
   * Checks whether this message is expunged. All other methods except
   * <code>getMessageNumber()</code> are invalid on an expunged
   * Message object. <p>
   *
   * Messages that are expunged due to an explict <code>expunge()</code>
   * request on the containing Folder are removed from the Folder
   * immediately. Messages that are externally expunged by another source
   * are marked "expunged" and return true for the isExpunged() method,
   * but they are not removed from the Folder until an explicit
   * <code>expunge()</code> is done on the Folder. <p>
   *
   * See the description of <code>expunge()</code> for more details on
   * expunge handling.
   *
   * @seeFolder#expunge
   */
  public boolean isExpunged() {
    try {
      try {
        return (getMessage() == null);
      } catch (FolderClosedException fce) {
        int status = parent.getStatus();
        if (status == FolderInfo.CONNECTED || status == FolderInfo.LOST_CONNECTION) {
          try {
            parent.openFolder(Folder.READ_WRITE);
          } catch (Exception me) {
            throw fce;
          }

          return (getMessage() == null);

        } else {
          throw fce;
        }
      }
    } catch (Exception me) {
      return false;
    }
  }

  public void writeTo(java.io.OutputStream os, java.lang.String[] ignoreList)
    throws java.io.IOException, MessagingException {
    try {
      getMessage().writeTo(os, ignoreList);
    } catch (FolderClosedException fce) {
      int status = parent.getStatus();
      if (status == FolderInfo.CONNECTED || status == FolderInfo.LOST_CONNECTION) {
        try {
          parent.openFolder(Folder.READ_WRITE);
        } catch (Exception me) {
          throw fce;
        }

        getMessage().writeTo(os, ignoreList);
      } else {
        throw fce;
      }
    }
  }

  public long getUID() {
    return uid;
  }

  public long getUIDValidity() {
    return parent.getUIDValidity();
  }

  public MimeMessage getMessage() throws MessagingException {
    MimeMessage returnValue = parent.getRealMessageById(uid);
    if (returnValue == null) {
      throw new MessageRemovedException("Message with UID " + uid + " does not exist in Folder " + parent.getFolderID());
    } else {
      return returnValue;
    }
  }

  public UIDFolderInfo getParent() {
    return parent;
  }

  /**
   * Overrides equals to make it so that any two MimeMessages who are
   * from the same folder and have the same UID are considered equal.
   */
  public boolean equals(Object o) {
    if (o instanceof Message) {
      try {
        UIDMimeMessage uidMsg = parent.getUIDMimeMessage((Message) o);
        if (uidMsg != null)
          if (uidMsg.getUID() == getUID())
            return true;
          else
            return false;
      } catch (Exception me) {
        return false;
      }
    }

    return false;
  }
}




