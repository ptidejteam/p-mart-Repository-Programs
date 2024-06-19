/*
*  Code: RFC822MessageCreator.java
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
* 822, the arpa internet message standard.
*
* <pre>
*    RFC822MessageCreator mm = new RFC822MessageCreator();
*    mm.setSender(new EmailAddress("youraddress@address.com"));
*    mm.setSubject("A simple message");
*    mm.addTo(new EmailAddress("myaddress@address.com"));
*   
*    EmailMessage msg = mm.getMessage();
* </pre>
*
* @author Lars J. Nilsson
* @version 1.2 31/03/2001
*/


public class RFC822MessageCreator {
    
    // message text
  	private String text;
  	
  	// temporary header
  	private EmailHeader header;
  	
  	// recipients
	private AddressField to, cc, bcc;
	
	
	/**
	* Create a new RFC822MessageCreator.
	*/
    
    public RFC822MessageCreator() {
        
        text = new String();
        
        header = new EmailHeader();
        
        Bit7Encoder encoder = new Bit7Encoder(true);
        
        to = new AddressField("To", encoder);
        cc = new AddressField("Cc", encoder);
        bcc = new AddressField("Bcc", encoder);
    }

   /**
    * Set message sender address.
    */

	public void setSender(EmailAddress address) {
        AddressField field = new AddressField("From", new Bit7Encoder(true));
        field.addAddress(address);
        header.set(field);
	}

    /**
    * Set message subject.
    */

	public void setSubject(String subject) {
	    header.set(new EmailHeaderField("Subject", subject, new Bit7Encoder()));
	}

    /**
    * Add a recipient type TO.
    */

	public void addTo(EmailAddress address) {
	    to.addAddress(address);
	}

    /**
    * Add a recipient type CC.
    */

	public void addCc(EmailAddress address) {
	    cc.addAddress(address);
	}

    /**
    * Add a recipient type BCC.
    */

	public void addBcc(EmailAddress address) {
	    bcc.addAddress(address);
	}

    /**
    * Set the text in this message.
    */

	public void setText(String text) {
	    this.text = text;
	}

    /**
    * Reset this message.
    */
    
    public void reset() {
        
        header = new EmailHeader();
        
        text = new String();
        
        to.clearField();
        cc.clearField();
        bcc.clearField();
    }

    /**
    * Get this message.
    */
    
    public Message getMessage() {
        
        // add recipients
        if(to.length() > 0) header.set(to);
        if(cc.length() > 0) header.set(cc);
        if(bcc.length() > 0) header.set(bcc);
        
        Message answer = new DefaultMessage(new EncodedTextBody(text, "US-ASCII", new Bit7Encoder(false)), "US-ASCII");
        
        // get the new header and remove header fields we don't want:
        // MIME-Version, Content-Type and Content-Transfer-Encoding
        // is MIME (RFC 1521) specific and should be removed
        
        Header newHeader = answer.getMessageBody().getHeader();

        newHeader.remove("Content-Type");
        newHeader.remove("Content-Transfer-Encoding");
        newHeader.remove("MIME-Version");
        
        // insert new fields
        
        HeaderField[] fields = header.getAll();
        
        for(int i = 0; i < fields.length; i++) newHeader.set(fields[i]);
        
        return answer;

    }
}