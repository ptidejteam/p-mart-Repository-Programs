/**
*  Code: MessageBody.java
*  Originator: Java@Larsan.Net
*  Address: www.larsan.net/java/
*  Contact: webmaster@larsan.net
*
*  Copyright (C) 2000 Lars J. Nilsson
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

package net.larsan.email;

import java.io.*;

import net.larsan.email.stream.LineBreak;

/**
* This is a simple Body part containing a Message object for implementation of
* the "message/rfc822" content type body part. The constructor sets the content 
* of the body part header object to "Content-Type: message/rfc822".
*
* @author Lars J. Nilsson
* @version 1.0 08/11/00
*/

public class MessageBody extends Body {
    
    // message
	private Message message;
	
	
	/**
	* Create a new message body object.
	*/
	
	public MessageBody(Message message) {
	    super(new EmailHeader());
	    getHeader().set(new EmailHeaderField("Content-Type", "message/rfc822"));
	    this.message = message;
    }

    
    /**
    * Get the message contained in this body.
    */

	public Message getMessage() {
	    return message;
	}

    
    /**
    * Set the message in this body.
    */

	public void setMessage(Message message) {
	    this.message = message;
	}

    
    /**
    * Write this body to an output stream.
    */

    public void write(OutputStream out) throws IOException {
        
        header.write(out);
        out.write(LineBreak.CRLF);
        message.write(out);
        
    }
}
