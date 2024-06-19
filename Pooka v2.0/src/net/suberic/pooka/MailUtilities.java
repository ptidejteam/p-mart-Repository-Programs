package net.suberic.pooka;

import java.util.Vector;
import java.util.StringTokenizer;
import java.util.LinkedList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;
import javax.mail.*;
import javax.mail.internet.*;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import net.suberic.crypto.EncryptionManager;
import net.suberic.crypto.EncryptionUtils;

public class MailUtilities {

  public MailUtilities() {
  }
  
  /**
   * This returns the value of an array of Address objects as a String.
   */
  public static String decodeAddressString(Address[] addresses) {
    if (addresses == null)
      return null;
    
    StringBuffer returnValue = new StringBuffer();
    for (int i = 0; i < addresses.length; i++) {
      if (addresses[i] != null) {
        if (i > 0)
          returnValue.append(", ");
        if (addresses[i] instanceof javax.mail.internet.InternetAddress)
          returnValue.append(((javax.mail.internet.InternetAddress)addresses[i]).toUnicodeString());
        else
          returnValue.append(addresses[i].toString());
      }
    }

    return returnValue.toString();
  }

  /**
   * This decoded an RFC 2047 encoded string.  If there are any errors
   * in decoding the string, the raw string is returned.
   */
  public static String decodeText(String encodedString) {
    if (encodedString == null)
      return null;

    String  value = null;
    try {
      value = javax.mail.internet.MimeUtility.decodeText(encodedString);
    } catch (UnsupportedEncodingException e) {
      // Don't care
      value = encodedString;
    }
    return value;
  }

  /**
   * This parses the message given into an AttachmentBundle.
   */
  public static AttachmentBundle parseAttachments(Message m) throws MessagingException, java.io.IOException {
    AttachmentBundle bundle = new AttachmentBundle((MimeMessage)m);

    handlePart((MimeMessage)m, bundle);

    return bundle;
  }
  
  /**
   * This parses a Mulitpart object into an AttachmentBundle.
   */
  public static AttachmentBundle parseAttachments(Multipart mp) throws MessagingException, java.io.IOException {
    AttachmentBundle bundle = new AttachmentBundle();
    for (int i = 0; i < mp.getCount(); i++) {
      MimeBodyPart mbp = (MimeBodyPart)mp.getBodyPart(i);
      
      handlePart(mbp, bundle);
    }
    return bundle;
  }

  /**
   * Handles a MimeBodyPart.
   */
  public static void handlePart(MimePart mp, AttachmentBundle bundle) throws MessagingException, java.io.IOException {
    
    String encryptionType = EncryptionManager.checkEncryptionType(mp);
    
    EncryptionUtils utils = null;
    if (encryptionType != null) {
      try {
        utils = EncryptionManager.getEncryptionUtils(encryptionType);
      } catch (java.security.NoSuchProviderException nspe) {
      }
    }
    
    if (utils != null) {
      
      int encryptionStatus = utils.getEncryptionStatus(mp);

      if (encryptionStatus == EncryptionUtils.ENCRYPTED) {
        Attachment newAttach = new net.suberic.pooka.crypto.CryptoAttachment(mp);
        bundle.addAttachment(newAttach);
      } else if (encryptionStatus == EncryptionUtils.SIGNED) {
        // in the case of signed attachments, we should get the wrapped body.
	
        Attachment newAttach = new net.suberic.pooka.crypto.SignedAttachment(mp);

        MimeBodyPart signedMbp = ((net.suberic.pooka.crypto.SignedAttachment) newAttach).getSignedPart();
	
        if (signedMbp != null) {
          handlePart(signedMbp, bundle);
        }
	
        bundle.addAttachment(newAttach);
	
      } else if (encryptionStatus == EncryptionUtils.ATTACHED_KEYS) {
        bundle.addAttachment(new net.suberic.pooka.crypto.KeyAttachment(mp));
      } else {
        // FIXME
        bundle.addAttachment(new Attachment(mp));
      }
      
    } else {
      ContentType ct = new ContentType(mp.getContentType());
      if (ct.getPrimaryType().equalsIgnoreCase("multipart")) {
        if (mp.getContent() instanceof Multipart) {
          if (ct.getSubType().equalsIgnoreCase("alternative"))
            parseAlternativeAttachment(bundle, mp);
          else 
            bundle.addAll(parseAttachments((Multipart)mp.getContent()));
        } else {
          Attachment attachment = new Attachment(mp);
          bundle.addAttachment(attachment);
        }
      } else if (ct.getPrimaryType().equalsIgnoreCase("Message")) {
        bundle.addAttachment(new Attachment(mp));
        Object msgContent;
        msgContent = mp.getContent();
	
        if (msgContent instanceof Message)
          bundle.addAll(parseAttachments((Message)msgContent));
        else if (msgContent instanceof java.io.InputStream)
          bundle.addAll(parseAttachments(new MimeMessage(Pooka.getDefaultSession(), (java.io.InputStream)msgContent)));
        else
          System.out.println("Error:  unsupported Message Type:  " + msgContent.getClass().getName());
	
      } else {
        bundle.addAttachment(new Attachment(mp), ct);
      }
    }
  }

  /**
   * Creates an AlternativeAttachment and adds it properly to the
   * given AttachmentBundle.
   */
  public static void parseAlternativeAttachment(AttachmentBundle bundle, MimePart mbp) throws MessagingException, java.io.IOException {
    Multipart amp = (Multipart) mbp.getContent();
    
    MimeBodyPart altTextPart = null;
    MimeBodyPart altHtmlPart = null;
    List extraList = new LinkedList();
    
    for (int j = 0; j < amp.getCount(); j++) {
      MimeBodyPart current = (MimeBodyPart)amp.getBodyPart(j);
      ContentType ct2 = new ContentType(current.getContentType());
      if (ct2.match("text/plain") && altTextPart == null)
        altTextPart = current;
      else if (ct2.match("text/html") && altHtmlPart == null)
        altHtmlPart = current;
      else
        extraList.add(new Attachment(current));
    }
    
    if (altHtmlPart != null && altTextPart != null) {
      Attachment attachment = new AlternativeAttachment(altTextPart, altHtmlPart);
      bundle.addAttachment(attachment);
      Iterator it = extraList.iterator();
      while (it.hasNext()) {
        bundle.addAttachment((Attachment) it.next());
      }
    } else {
      // hurm
      bundle.addAll(parseAttachments(amp));
    }
  }
  
  /**
   * This method takes a given String offset and returns the offset
   * position at which a line break should occur.
   *
   * If no break is necessary, the full buffer length is returned.
   * 
   */
  public static int getBreakOffset(String buffer, int breakLength, int tabSize) {
    // what we'll do is to modify the break length to make it fit tabs.
    
    int nextTab = buffer.indexOf('\t');
    int tabAccumulator = 0;
    int tabAddition = 0;
    while (nextTab >=0 && nextTab < breakLength) {
      tabAddition = tabSize - ((tabSize +  nextTab + tabAccumulator + 1) % tabSize);
      breakLength=breakLength - tabAddition;
      tabAccumulator = tabAccumulator + tabAddition;
      if (nextTab + 1 < buffer.length())
        nextTab = buffer.indexOf('\t', nextTab + 1);
      else
        nextTab = -1;
    }
    
    
    if ( buffer.length() <= breakLength ) {
      return buffer.length();
    }
    
    int breakLocation = -1;
    for (int caret = breakLength; breakLocation == -1 && caret >= 0; caret--) {
      if (Character.isWhitespace(buffer.charAt(caret))) {
        breakLocation=caret + 1;
        if (breakLocation < buffer.length()) {
          // check to see if the next character is a line feed of some sort.
          char nextChar = buffer.charAt(breakLocation);
          if (nextChar == '\n')
            breakLocation ++;
          else if (nextChar == '\r') {
            if (breakLocation + 1<  buffer.length() && buffer.charAt(breakLocation + 1) == '\n') {
              breakLocation +=2;
            } else {
              breakLocation ++;
            }
          }
        }
      } 
    }
    
    if (breakLocation == -1)
      breakLocation = breakLength;
    
    return breakLocation;
  }
  
  /**
   * This takes a String and word wraps it at length wrapLength.  It also will
   * convert any alternative linebreaks (LF, CR, or CRLF) to the 
   * <code>newLine</code> given.
   */
  public static String wrapText(String originalText, int wrapLength, String newLine, int tabSize) {
    if (originalText == null)
      return null;
    
    Logger.getLogger("Pooka.debug").finest("calling wrapText with wrapLength=" + wrapLength + " on:");
    Logger.getLogger("Pooka.debug").finest("--- begin text ---");
    Logger.getLogger("Pooka.debug").finest(originalText);
    Logger.getLogger("Pooka.debug").finest("--- end text ---");
    Logger.getLogger("Pooka.debug").finest("");

    StringBuffer wrappedText = new StringBuffer();
    
    // so the idea is that we'll get each entry denoted by a line break
    // and then add soft breaks into there.
    int currentStart = 0;
    int nextHardBreak = nextNewLine(originalText, currentStart);
    while (nextHardBreak != -1) {
      // get the current string with a newline at the end.
      String currentString = getSubstringWithNewLine(originalText, currentStart, nextHardBreak, newLine);

      Logger.getLogger("Pooka.debug").finest("current string:");
      Logger.getLogger("Pooka.debug").finest("--- begin current string ---");
      Logger.getLogger("Pooka.debug").finest(currentString);
      Logger.getLogger("Pooka.debug").finest("--- end current string ---");
      Logger.getLogger("Pooka.debug").finest("");
      
      int nextSoftBreak = getBreakOffset(currentString, wrapLength, tabSize);
      while (nextSoftBreak < currentString.length()) {
        wrappedText.append(currentString.substring(0, nextSoftBreak));
        wrappedText.append(newLine);

        Logger.getLogger("Pooka.debug").finest("appending '" + currentString.substring(0, nextSoftBreak) + "', plus newline.");

        currentString = currentString.substring(nextSoftBreak);
        Logger.getLogger("Pooka.debug").finest("in loop:  new current string:");
        Logger.getLogger("Pooka.debug").finest("--- begin current string ---");
        Logger.getLogger("Pooka.debug").finest(currentString);
        Logger.getLogger("Pooka.debug").finest("--- end current string ---");
        Logger.getLogger("Pooka.debug").finest("");
      
        nextSoftBreak = getBreakOffset(currentString, wrapLength, tabSize);
        Logger.getLogger("Pooka.debug").finest("nextSoftBreak=" + nextSoftBreak);
      }
      Logger.getLogger("Pooka.debug").finest("appending '" + currentString + "', which should include newline.");
      wrappedText.append(currentString);
      
      // get the next string including the newline.
      currentStart = afterNewLine(originalText, nextHardBreak);
      nextHardBreak= nextNewLine(originalText, currentStart);
      Logger.getLogger("Pooka.debug").finest("new currentStart = " + currentStart + ", nextHardBreak = " + nextHardBreak);
    }
	   
    return wrappedText.toString();
  } 
  
  /**
   * Returns the next new line.
   */
  public static int nextNewLine(String text, int start) {
    if (start >= text.length())
      return -1;

    // go through each character, looking for \r or \n
    int foundIndex = -1;
    for (int i = start; foundIndex == -1 && i < text.length(); i++) {
      char current = text.charAt(i);
      if (current == '\r') {
        if (i + 1 < text.length() && text.charAt(i+1) == '\n')
          foundIndex = i+1;
        else
          foundIndex = i;
      } else if (current == '\n') {
        foundIndex = i;
      }
    }

    if (foundIndex == -1) {
      return text.length();
    } else {
      return foundIndex;
    }
  }

  /**
   * Returns the position after the newline indicated by index.  If
   * that's the end of the string, or an invalid index is given, returns
   * an index equal to the length of text (i.e. one more than the last
   * valid index in text).
   */
  public static int afterNewLine(String text, int index) {
    // if index is invalid, or if index is the last character in the 
    // string, return 
    if (index < 0 || index >= text.length() || index == text.length() -1)
      return text.length();

    char newLineChar = text.charAt(index);
    if (newLineChar == '\r' && text.charAt(index + 1) == '\n')
      return index + 2;
    else
      return index + 1;
  }

  /**
   * Gets the indicated substring with the given newline.
   */
  public static String getSubstringWithNewLine(String originalText, int start, int end, String newLine) {
    String origSubString = originalText.substring(start,end);
    Logger.getLogger("Pooka.debug").finest("getSubStringWtihNewLine:  origSubString='" + origSubString + "'");

    if (origSubString.endsWith("\r\n")) {
      if (newLine.equals("\r\n"))
        return origSubString;
      else {
        return origSubString.substring(0, origSubString.length() - 2) + newLine;
      }
    } else if (origSubString.endsWith("\n")) {
      if (newLine.equals("\n"))
        return origSubString;
      else 
        return origSubString.substring(0, origSubString.length() - 1) + newLine;
    } else if (origSubString.endsWith("\r")) {
      if (newLine.equals("\r"))
        return origSubString;
      else 
        return origSubString.substring(0, origSubString.length() - 1) + newLine;
    } else {
      return origSubString + newLine;
    }
  }

  /**
   * A convenience method which wraps the given string using the
   * length specified by Pooka.lineLength.
   */
  public static String wrapText(String originalText) {
    int wrapLength;
    int tabSize;
    try {
      String wrapLengthString = Pooka.getProperty("Pooka.lineLength");
      wrapLength = Integer.parseInt(wrapLengthString);
    } catch (Exception e) {
      wrapLength = 72;
    }
    
    try {
      String tabSizeString = Pooka.getProperty("Pooka.tabSize", "8");
      tabSize = Integer.parseInt(tabSizeString);
    } catch (Exception e) {
      tabSize = 8;
    }
    return wrapText(originalText, wrapLength, "\r\n", tabSize);
  }
  
  /**
   * Escapes html special characters.
   */
  public static String escapeHtml(String input) {
    char[] characters = input.toCharArray();
    StringBuffer retVal = new StringBuffer();
    for (int i = 0; i < characters.length; i++) {
      if (characters[i] == '&')
        retVal.append("&amp;");
      else if (characters[i] == '<')
        retVal.append("&lt;");
      else if (characters[i] == '>')
        retVal.append("&gt;");
      else
        retVal.append(characters[i]);
    }
    return retVal.toString();
  }

}


