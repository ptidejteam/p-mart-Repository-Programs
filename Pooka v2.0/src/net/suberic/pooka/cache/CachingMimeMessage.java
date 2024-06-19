package net.suberic.pooka.cache;
import javax.mail.*;
import javax.mail.internet.*;
import net.suberic.pooka.FolderInfo;
import net.suberic.pooka.Pooka;
import net.suberic.pooka.UIDMimeMessage;
import net.suberic.pooka.UIDFolderInfo;
import javax.activation.DataHandler;
import java.util.Enumeration;
import java.io.InputStream;
import java.io.ByteArrayInputStream;

/**
 * A wrapper around a MimeMessage which can either work in real or 
 * disconnected (cached) mode.
 */

public class CachingMimeMessage extends UIDMimeMessage {
  
  public CachingMimeMessage(CachingFolderInfo parentFolderInfo, long newUid) {
    super(parentFolderInfo, newUid);
  }
  
  public int getSize() throws MessagingException {
    return getCache().getSize(getUID());
    /*
      try {
      if (getContent() != null) {
      //return getContent().length;
      return 1;
      }
      else
      return -1;
      } catch (java.io.IOException ioe) {
      throw new MessagingException(ioe.getMessage(), ioe);
      }
    */
  }
  
  public synchronized DataHandler getDataHandler() 
    throws MessagingException {
    return getCache().getDataHandler(getUID(), getUIDValidity(), ! getCacheHeadersOnly());
  }
  
  public String[] getHeader(String name)
    throws MessagingException {
    return getHeaders().getHeader(name);
  }
  
  public String getHeader(String name, String delimiter)
    throws MessagingException {
    return getHeaders().getHeader(name, delimiter);
  }
  
  public void setHeader(String name, String value)
    throws MessagingException {
    throw new IllegalWriteException(Pooka.getProperty("error.cache.illegalWrite", "Cannot write to an existing message."));
  }
  
  public void addHeader(String name, String value)
    throws MessagingException {
    throw new IllegalWriteException(Pooka.getProperty("error.cache.illegalWrite", "Cannot write to an existing message."));
  }
  
  public void removeHeader(String name)
    throws MessagingException {
    throw new IllegalWriteException(Pooka.getProperty("error.cache.illegalWrite", "Cannot write to an existing message."));
  }
  
  public Enumeration getAllHeaders() throws MessagingException {
    return getHeaders().getAllHeaders();	
  }
  
  public Enumeration getMatchingHeaders(String[] names)
    throws MessagingException {
    return getHeaders().getMatchingHeaders(names);
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
    return getHeaders().getNonMatchingHeaders(names);
  }
  
  /**
   * Add a raw RFC 822 header-line. 
   *
   * @exception	IllegalWriteException if the underlying
   *			implementation does not support modification
   * @exception	IllegalStateException if this message is
   *			obtained from a READ_ONLY folder.
   * @exception  	MessagingException
   */
  public void addHeaderLine(String line) throws MessagingException {
    throw new IllegalWriteException(Pooka.getProperty("error.cache.illegalWrite", "Cannot write to an existing message."));
  }
  
  /**
   * Get all header lines as an Enumeration of Strings. A Header
   * line is a raw RFC 822 header-line, containing both the "name" 
   * and "value" field. 
   *
   * @exception  	MessagingException
   */
  public Enumeration getAllHeaderLines() throws MessagingException {
    return getHeaders().getAllHeaderLines();
  }
  
  /**
   * Get matching header lines as an Enumeration of Strings. 
   * A Header line is a raw RFC 822 header-line, containing both 
   * the "name" and "value" field.
   *
   * @exception  	MessagingException
   */
  public Enumeration getMatchingHeaderLines(String[] names)
    throws MessagingException {
    return getHeaders().getMatchingHeaderLines(names);
  }
  
  /**
   * Get non-matching header lines as an Enumeration of Strings. 
   * A Header line is a raw RFC 822 header-line, containing both 
   * the "name" and "value" field.
   *
   * @exception  	MessagingException
   */
  public Enumeration getNonMatchingHeaderLines(String[] names)
    throws MessagingException {
    return getHeaders().getNonMatchingHeaderLines(names);
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
   * @exception  	MessagingException
   * @see 		javax.mail.Flags
   */
  public synchronized Flags getFlags() throws MessagingException {
    try {
      return (Flags) getCache().getFlags(getUID(), getUIDValidity()).clone();
    } catch (MessagingException me) {
      if (me instanceof MessageRemovedException) {
        return new Flags();
      } else {
        throw me;
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
   * @param flag	the flag
   * @return		value of the specified flag for this message
   * @see 		javax.mail.Flags.Flag
   * @see		javax.mail.Flags.Flag#ANSWERED
   * @see		javax.mail.Flags.Flag#DELETED
   * @see		javax.mail.Flags.Flag#DRAFT
   * @see		javax.mail.Flags.Flag#FLAGGED
   * @see		javax.mail.Flags.Flag#RECENT
   * @see		javax.mail.Flags.Flag#SEEN
   * @exception       MessagingException
   */
  public synchronized boolean isSet(Flags.Flag flag)
    throws MessagingException {
    try {
      return getFlags().contains(flag);
    } catch (MessagingException me) {
      System.out.println("caught exception:  " + me);
      me.printStackTrace();
      throw me;
    }
  }
  
  /**
   * Set the flags for this message. <p>
   *
   * This implementation modifies the <code>flags</code> field.
   *
   * @exception	IllegalWriteException if the underlying
   *			implementation does not support modification
   * @exception	IllegalStateException if this message is
   *			obtained from a READ_ONLY folder.
   * @exception  	MessagingException
   */
  public synchronized void setFlags(Flags flag, boolean set)
    throws MessagingException {
    if (set)
      getCache().addFlag(getUID(), getUIDValidity(), flag);
    else
      getCache().removeFlag(getUID(), getUIDValidity(), flag);
  }

  public MessageCache getCache() {
    return ((CachingFolderInfo)getParent()).getCache();
  }

  public void setExpungedValue(boolean newValue) {
    expunged=newValue;
  }

  /**
   * Returns whether this message is expunged or not.
   */
  public boolean isExpunged() {
    return expunged;
  }

  public InternetHeaders getHeaders() throws MessagingException {
    try {
      return getCache().getHeaders(getUID(), getUIDValidity());
    } catch (MessagingException me) {
      if (me instanceof MessageRemovedException) {
        return new InternetHeaders();
      } else {
        throw me;
      }
    }
  }

  public void writeTo(java.io.OutputStream os, String[] ignoreList)
    throws java.io.IOException, MessagingException {
    getCache().getMessageRepresentation(getUID(), getUIDValidity(), ! getCacheHeadersOnly()).writeTo(os, ignoreList);
  }
 
  /**
   * Returns if we're caching only headers.
   */
  public boolean getCacheHeadersOnly() {
    return ((CachingFolderInfo)getParent()).getCacheHeadersOnly();
  }
  
}




