/*
*  Code: MIMEMessageCreator.java
*  Originator: Java@Larsan.Net
*  Address: www.larsan.net/java/
*  Contact: webmaster@larsan.net
*
*  Copyright (C) 2001 Lars J. Nilsson
*
*       This program is free software; you can redistribute it and/or
*       modify it under the terms of the GNU General Public License
*       as published by the Free Software Foundation; either version 2
*       of the License, or (at your option) any later version.
*
*       This program is distributed in the hope that it will be useful,
*       but WITHOUT ANY WARRANTY; without even the implied warranty of
*       MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
*       GNU General Public License for more details.
*
*       You should have received a copy of the GNU General Public License
*       along with this program; if not, write to the Free Software
*       Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA. 
*/

package net.larsan.email.util;

import java.util.*;

import net.larsan.email.*;
import net.larsan.email.stream.*;
import net.larsan.email.encoder.*;

/**
* This is a simple "factory" class for creating email messages according to the RFC 
* 1521 and 1522, the MIME standard. This class supports images, html, attachments, 
* multiple recipients and custom character sets.<p>
*
* This class works with the default character set "ISO-8859-1". This should be sufficient 
* for most western languages. If another character set is needed it should be provided
* as a parameter to the class constructor or set using the <code>setCharset</code>
* method. However, setting a new character set is not guaranteed to have any effects 
* before the <code>reset</code> method is called.<p>
*
* This class works in a linear manner, ie: The mail part added first
* will appear first in the message and so on. For example: To create a simple 
* greeting message using html with a text message on the side for those who 
* cannot parse html correctly, do like this:
*
* <pre>
*    MIMEMessageCreator mm = new MIMEMessageCreator();
*    mm.setFrom(new EmailAddress("youraddress@address.com"));
*    mm.setSubject("HTML message!");
*    mm.addTo(new EmailAddress("myaddress@address.com"));
*    mm.addHtml("<b>Hi there frood!.</b>", "Hi there frood!");
*   
*    Message msg = mm.getMessage();
* </pre>
*
* @author Lars J. Nilsson
* @version 1.3 01/12/2001
*/

public class MIMEMessageCreator {
  
    // private data
  	private MixedBody body;
	private DefaultMessage msg;
	private String charset;

    /**
    * Create a new MIMEMessageCreator. The message creator
    * will use the default character set "ISO-8859-1".
    */

	public MIMEMessageCreator() {
        this("ISO-8859-1");
	}


    /**
    * Create a new MIMEMessageCreator. The message creator
    * will use the parameter character set.
    */

	public MIMEMessageCreator(String charset) {
        this.charset = charset;
        reset();
	}

    
    /**
    * Set message sender address.
    */

	public void setFrom(EmailAddress address) {
	    AddressField field = new AddressField("From", new Q_Encoder(charset));
	    field.addAddress(address);
        msg.getMessageBody().getHeader().set(field);
	}

    
    /**
    * Set message subject.
    */

	public void setSubject(String subject){
	    HeaderField field = new EmailHeaderField("Subject", subject, new Q_Encoder(charset));
	    msg.getMessageBody().getHeader().set(field);
	}

    
    /**
    * Add a recipient type TO.
    */

	public void addTo(EmailAddress address){
	    msg.getRecipientHandler().addRecipient(new Recipient(RecipientType.TO, address));
	}

    
    /**
    * Add a recipient type CC.
    */

	public void addCc(EmailAddress address){
	    msg.getRecipientHandler().addRecipient(new Recipient(RecipientType.CC, address));
	}

    /**
    * Add a recipient type BCC.
    */

	public void addBcc(EmailAddress address){
	    msg.getRecipientHandler().addRecipient(new Recipient(RecipientType.BCC, address));
	}

    
    /**
    * Set costum charset other than the default "ISO-8859-1".
    */

	public void setCharset(String charset){
	    this.charset = charset;
	}

    
    /**
    * Get current charset.
    */

    public String getCharset() {
        return charset;
    }

    
    /**
    * Insert date.
    */
    
    public void insertDate(Date date) {
        msg.getMessageBody().getHeader().set(new EmailHeaderField("Date", TimeStamp.format(date)));
    }

    
    /**
    * Add plain text to the message.
    */

	public void addText(String text){
	    if(QuotedPrintableEncoder.encodedLength(text.toCharArray()) == -1) body.addLowOrderBodyPart(new TextBody(text, charset));
	    else body.addLowOrderBodyPart(new EncodedTextBody(text, charset, new QuotedPrintableEncoder(charset)));
	}

    
    /**
    * Add plain html to the message.
    */

	public void addHtml(String html){
	   	if(QuotedPrintableEncoder.encodedLength(html.toCharArray()) == -1) body.addLowOrderBodyPart(new HTMLBody(html, charset));
	    else body.addLowOrderBodyPart(new HTMLBody(html, charset, new QuotedPrintableEncoder(charset)));
	}
	
	
	/** 
	* Add a message to this message. This method will insert a body of content type message/rfc822
	* in the message with the parameter message as content.
	*/
	
	public void addMessage(Message msg) {
	   body.addLowOrderBodyPart(new MessageBody(msg));
    }

    
    /**
    * Add html to the message including a text version of the same information.
    * The text will not be visible for recipients who can handle the html.
    */

	public void addHtml(String html, String text) {
	   
	    AlternativeBody alt = new AlternativeBody(getBoundary(body.getBoundary()));
	    
	    if(QuotedPrintableEncoder.encodedLength(html.toCharArray()) == -1) alt.addHighOrderBodyPart(new HTMLBody(html, charset));
	    else alt.addHighOrderBodyPart(new HTMLBody(html, charset, new QuotedPrintableEncoder(charset)));
	
		if(QuotedPrintableEncoder.encodedLength(text.toCharArray()) == -1) alt.addLowOrderBodyPart(new TextBody(text, charset));
	    else alt.addLowOrderBodyPart(new EncodedTextBody(text, charset, new QuotedPrintableEncoder(charset)));

        body.addLowOrderBodyPart(alt);
	
	}

    
    /**
    * Add a GIF image to the message. This image will most probably be regarded
    * as an attachment by the recipient since it is not a part of the protocoll standard.
    */

	public void addGIFImage(byte[] image, String name) {
	    body.addLowOrderBodyPart(new ImageBody(ContentType.GIF_IMAGE, image, name));
	}

    
    /**
    * Add a JPG image to the message. This image will most probably be regarded
    * as an attachment by the recipient since it is not a part of the protocoll standard.
    */

	public void addJPEGImage(byte[] image, String name) {
	    body.addLowOrderBodyPart(new ImageBody(ContentType.JPEG_IMAGE, image, name));
	}

    
    /**
    * Add a binary attachment.
    */

	public void addAttachment(byte[] file, String name){
	    body.addLowOrderBodyPart(new BinaryAttachment(file, name));
	}

    
    /**
    * Add a text attachment.
    */

	public void addAttachment(String file, String name){
	    body.addLowOrderBodyPart(new TextAttachment(file, name, charset, new QuotedPrintableEncoder(charset)));
	}

    
    /**
    * Reset message. This method should be called between the creation of multiple 
    * messages. User's wishing to change character st should also call this method
    * to make sure the change is reflected in the message being produced.
    */

	public void reset() {
	   	body = new MixedBody(getBoundary(""));
	    msg = new DefaultMessage(body, charset);
	}

    
    /**
    * Get the finished message.
    */

	public Message getMessage() {
	    return checkMessage();
	}

    
    /** This method only check if the message contains more than one body. */

    private Message checkMessage() {
        
        int count = 0;
        
        // is there more than one body part ?
        
        for(Iterator i = body.getBodyParts(); i.hasNext();) {
            
            if(count > 1) break;
            
            i.next();
            count++;
            
        }
        
        if(count == 1) {
            
             // extract subject, sender, charset and recipients
             // and insert them into the new message object
             
             Iterator it = body.getBodyParts();

             Header header = body.getHeader();
             
             header.remove("Content-Type");

             Body newBody = (Body)it.next();
             
             HeaderField[] all = header.getAll();
             
             for(int i = 0; i < all.length; i++) newBody.getHeader().set(all[i]);
        
             return new DefaultMessage(newBody, charset);
             
        } else return msg;
    }

    
    /** Create a new boundary that does not equals the enclosing one. */

    private String getBoundary(String enclosing) {
        
        String start = "=__Next_Part_";
        
        String end = "__=";
        
        StringBuffer answer = new StringBuffer(start);
        answer.append(new Date().getTime()).append(end);
        
        // check for equality
        
        if((answer.toString()).equalsIgnoreCase(enclosing)) {
            
            answer = new StringBuffer(start);
            answer.append(new Date().getTime());
            answer.append(end);
        }
    
        return answer.toString();
    }       
}
