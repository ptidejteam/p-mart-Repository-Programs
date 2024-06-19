package net.suberic.pooka;
import javax.mail.*;
import javax.mail.internet.*;
import javax.activation.DataHandler;
import java.io.*;
import java.util.*;
import javax.mail.internet.InternetHeaders;

public class Attachment {
  DataHandler handler;
  String name;
  ContentType mimeType;
  int size;
  String encoding;
  InternetHeaders headers = null;
  Vector headerLines = null;

  /**
   * Creates an Attachment out of a MimeBodyPart.
   */
  public Attachment(MimePart mp) throws MessagingException {
    handler = mp.getDataHandler();
    name = mp.getFileName();
    String type = mp.getContentType();
    try {
      mimeType = new ContentType(type);
    } catch (ParseException pe) {
      if (type.equalsIgnoreCase("text"))
        mimeType = new ContentType("text/plain");
      else if (type.length() > 0 && type.indexOf('/') == -1) {
        try {
          mimeType = new ContentType(type + "/plain");
        } catch (ParseException petwo) {
          // fall back to text/plain.
          mimeType = new ContentType("text/plain");
        }

      } else {
        // fall back to text/plain.
        mimeType = new ContentType("text/plain");
      }

    }
    size = mp.getSize();
    encoding = mp.getEncoding();
  }

  /**
   * Creates an Attachment with the given MimeBodyPart, but with
   * the attached MimePart as the source for the Headers.
   */
  public Attachment(MimePart mp, MimePart headerSource) throws MessagingException {
    handler = mp.getDataHandler();
    if (mp instanceof MimeBodyPart)
      name = mp.getFileName();
    else
      name = Pooka.getProperty("message.unknownMessage", "Message Text");

    String type = mp.getContentType();
    try {
      mimeType = new ContentType(type);
    } catch (ParseException pe) {
      if (type.equalsIgnoreCase("text"))
        mimeType = new ContentType("text/plain");
      else if (type.length() > 0 && type.indexOf('/') == -1) {
        try {
          mimeType = new ContentType(type + "/plain");
        } catch (ParseException petwo) {
          // fall back to text/plain.
          mimeType = new ContentType("text/plain");
        }
      } else {
        // fall back to text/plain.
        mimeType = new ContentType("text/plain");

      }
    }
    size = mp.getSize();
    encoding = mp.getEncoding();
    headers = parseHeaders(headerSource.getAllHeaders());
    headerLines = parseHeaderLines(headerSource.getAllHeaderLines());
  }

  /**
   * Creates an Attachment out of a MimeMessage.  This is typically
   * used when the content of a Message is too large to display, and
   * therefore it needs to be treated as an attachment rather than
   * as the text of the Message.
   */
  /*
    public Attachment(MimeMessage msg) throws MessagingException {
    handler = msg.getDataHandler();
    name = Pooka.getProperty("message.unknownMessage", "Message Text");
    String type = msg.getContentType();
    try {
    mimeType = new ContentType(type);
    } catch (ParseException pe) {
    if (type.equalsIgnoreCase("text"))
    mimeType = new ContentType("text/plain");
    else if (type.length() > 0 && type.indexOf('/') == -1) {
    try {
    mimeType = new ContentType(type + "/plain");
    } catch (ParseException petwo) {
    // fall back to text/plain.
    mimeType = new ContentType("text/plain");
    }
    } else {
    // fall back to text/plain.
    mimeType = new ContentType("text/plain");
    }
    }
    size = msg.getSize();
    encoding = msg.getEncoding();
    }
  */

  public void setHeaderSource(MimePart headerSource) throws MessagingException {
    headers = parseHeaders(headerSource.getAllHeaders());
    headerLines = parseHeaderLines(headerSource.getAllHeaderLines());
  }

  public void setHeaderSource(AttachmentBundle bundle) {
    headers = bundle.headers;
    headerLines = bundle.headerLines;
  }

  // accessor methods.

  /**
   * Returns the decoded InputStream of this Attachment.
   */
  public InputStream getInputStream() throws java.io.IOException {
    return handler.getInputStream();
  }

  /**
   * Returns the DataHandler for this Attachment.
   */
  public DataHandler getDataHandler() {
    return handler;
  }

  /**
   * Returns the content of this attachment as an Object.
   */
  public Object getContent() throws java.io.IOException {
    try {
      return getDataHandler().getContent();
    } catch (UnsupportedEncodingException uee) {
      if (isText()) {
        /**
         * Just read the InputStream directly into a byte array and
         * hope for the best.  :)
         */
        InputStream is = getDataHandler().getInputStream();
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        int b;
        while ((b = is.read()) != -1)
          bos.write(b);
        byte[] barray = bos.toByteArray();
        return new String(barray, Pooka.getProperty("Pooka.defaultCharset", "iso-8859-1"));
      } else {
        throw uee;
      }
    }
  }

  public int getSize() {
    return size;
  }

  public String getName() {
    return name;
  }

  public String getEncoding() {
    return encoding;
  }

  public ContentType getMimeType() {
    return mimeType;
  }

  public boolean isText() {
    return getMimeType().match("text/");
  }

  public boolean isPlainText() {
    return getMimeType().match("text/plain") ;
  }

  public boolean isHtml() {
    return getMimeType().match("text/html");
  }

  /**
   * Returns the text of the Attachment, up to maxLength bytes.  If
   * the content is truncated, then append the truncationMessage at the
   * end of the content displayed.
   *
   * If withHeaders is set, then show the Headers to go with this message.
   * If showFullHeaders is also set, then show all the headers.
   */
  public String getText(boolean withHeaders, boolean showFullHeaders, int maxLength, String truncationMessage) throws java.io.IOException {
    if (isPlainText()) {
      StringBuffer retVal = new StringBuffer();
      if (withHeaders)
        retVal.append(getHeaderInformation(showFullHeaders));

      retVal.append(getText(maxLength, truncationMessage));

      return retVal.toString();
    } else
      return null;
  }

  /**
   * Returns the String content of this message, up to maxLength
   * bytes.
   */
  String getText(int maxLength, String truncationMessage) throws IOException {
    if (maxLength > 0 && maxLength >= size) {
      try {
        Object o = getDataHandler().getContent();
        if (o instanceof String)
          return (String) o;
        else
          throw new UnsupportedEncodingException();
      } catch (UnsupportedEncodingException uee) {
        /**
         * Just read the InputStream directly into a byte array and
         * hope for the best.  :)
         */

        InputStream is = getDataHandler().getInputStream();
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        int b;
        while ((b = is.read()) != -1)
          bos.write(b);
        byte[] barray = bos.toByteArray();
        return new String(barray, Pooka.getProperty("Pooka.defaultCharset", "iso-8859-1"));
      }
    } else {
      int written = 0;
      InputStream decodedIS = null;
      ByteArrayOutputStream outStream = new ByteArrayOutputStream();

      decodedIS = getInputStream();

      int b=0;
      byte[] buf = new byte[16384];

      b = decodedIS.read(buf);
      while (b != -1 && written < maxLength) {
        if (b <= (maxLength - written)) {
          outStream.write(buf, 0, b);
          written = written + b;
        } else {
          outStream.write(buf, 0, (maxLength - written));
          written = maxLength;
        }
        b = decodedIS.read(buf);
      }

      byte[] barray = outStream.toByteArray();
      String content;
      try {
        content = new String(barray, Pooka.getProperty("Pooka.defaultCharset", "iso-8859-1"));
      } catch (UnsupportedEncodingException uee) {
        content = new String(barray, Pooka.getProperty("Pooka.defaultCharset", "iso-8859-1"));
      }

      return content + "\n" + truncationMessage + "\n";
    }

  }

  /**
   * Returns the html of the Attachment, up to maxLength bytes.  If
   * the content is truncated, then append the truncationMessage at the
   * end of the content displayed.
   *
   * If withHeaders is set, then show the Headers to go with this message.
   * If showFullHeaders is also set, then show all the headers.
   */
  public String getHtml(boolean withHeaders, boolean showFullHeaders, int maxLength, String truncationMessage) throws java.io.IOException {
    if (isHtml()) {
      StringBuffer retVal = new StringBuffer();
      if (withHeaders)
        retVal.append(getHeaderInformation(showFullHeaders));

      retVal.append(getText(maxLength, truncationMessage));

      return retVal.toString();
    } else
      return null;
  }

  /**
   * Parses the Enumeration of Header objects into a HashMap.
   */
  private InternetHeaders parseHeaders(Enumeration pHeaders) {
    InternetHeaders retVal = new InternetHeaders();
    while (pHeaders.hasMoreElements()) {
      Header hdr = (Header) pHeaders.nextElement();
      retVal.addHeader(hdr.getName(), hdr.getValue());
    }

    return retVal;
  }

  /**
   * Parses the Enumeration of header lines into a Vector.
   */
  private Vector parseHeaderLines(Enumeration pHeaderLines) {
    Vector retVal = new Vector();
    while (pHeaderLines.hasMoreElements())
      retVal.add(pHeaderLines.nextElement());
    return retVal;
  }

  /**
   * This returns the formatted header information for a message.
   */
  public StringBuffer getHeaderInformation (boolean showFullHeaders) {
    if (headers != null) {
      StringBuffer headerText = new StringBuffer();

      if (showFullHeaders) {
        Enumeration allHdrs = headers.getAllHeaderLines();
        while (allHdrs.hasMoreElements()) {
          headerText.append(MailUtilities.decodeText((String) allHdrs.nextElement()));
        }
      } else {
        StringTokenizer tokens = new StringTokenizer(Pooka.getProperty("MessageWindow.Header.DefaultHeaders", "From:To:CC:Date:Subject"), ":");
        String hdrLabel,currentHeader = null;
        String hdrValue = null;

        while (tokens.hasMoreTokens()) {
          currentHeader=tokens.nextToken();
          hdrLabel = Pooka.getProperty("MessageWindow.Header." + currentHeader + ".label", currentHeader);
          hdrValue = MailUtilities.decodeText((String) headers.getHeader(Pooka.getProperty("MessageWindow.Header." + currentHeader + ".MIMEHeader", currentHeader), ":"));
          if (hdrValue != null) {
            headerText.append(hdrLabel + ":  ");
            headerText.append(hdrValue);

            headerText.append("\n");
          }
        }
      }
      String separator = Pooka.getProperty("MessageWindow.separator", "");
      if (separator.equals(""))
        headerText.append("\n\n");
      else
        headerText.append(separator);

      return headerText;
    } else {
      return new StringBuffer();
    }
  }

  /**
   * Returns the Headers for this Attachment.
   */
  public InternetHeaders getHeaders() {
    return headers;
  }
}

