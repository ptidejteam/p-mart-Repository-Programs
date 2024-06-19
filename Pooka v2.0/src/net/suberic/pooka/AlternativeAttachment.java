package net.suberic.pooka;
import javax.mail.*;
import javax.mail.internet.*;
import javax.activation.DataHandler;
import java.io.*;
import java.util.*;

public class AlternativeAttachment extends Attachment {
  DataHandler htmlHandler;
  
  /**
   * Creates an Attachment out of two MimeBodyParts, where one is the
   * text content and the other is the HTML content.
   */
  public AlternativeAttachment(MimeBodyPart textPart, MimeBodyPart htmlPart) throws MessagingException {
    super(textPart);
    htmlHandler = htmlPart.getDataHandler();
  }
  
  public boolean isText() {
    return true;
  }
  
  public boolean isPlainText() {
    return true;
  }
  
  public boolean isHtml() {
    return true;
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
    StringBuffer retVal = new StringBuffer();
    if (withHeaders)
      retVal.append(getHeaderInformation(showFullHeaders));
    
    retVal.append(getHtml(maxLength, truncationMessage));
    
    return retVal.toString();
  }
  
  /**
   * Returns the Html content of this message, up to maxLength 
   * bytes.
   */
  String getHtml(int maxLength, String truncationMessage) throws IOException {
    if (maxLength >= size) {
      try {
	String retVal = (String) htmlHandler.getContent();
	return retVal;
	//return (String) htmlHandler.getContent();
      } catch (UnsupportedEncodingException uee) {
	/**
	 * Just read the InputStream directly into a byte array and
	 * hope for the best.  :)
	 */
	
	InputStream is = htmlHandler.getInputStream();
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
  
}

