/*
*  Code: BinaryBody.java
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

import net.larsan.email.stream.*;
import net.larsan.email.encoder.*;

/**
* This is the base class for all mail bodies containing a binary content which
* must be encoded before transport. This could be attachments, images
* or other binary files.<p>
*
* The class uses a Encoder object to prepare the body for transport. The
* Encoder is an abstract skeleton which developers can subclass to 
* provide their own custom transport encodings.<p>
*
* This body part contains a header object which manages the header fields. By
* default the body attempts to set the  "Content-Transfer-Encoding" header field 
* automaticly. The value of this field is fetched from the Encoder object's 
* <code>getContextName</code> method.<p>
*
* Since the body of this class must be encoded before transport, the encoding
* is by default performed in the <code>write</code> method so an Encoder object
* must be provided before attempts to write the content. If the Encoder object is
* missing the class will throw an IOException.
*
* @author Lars J. Nilsson
* @version 1.0.1 29/03/2001
*/

public class BinaryBody extends EncodedBody {
	
	/** Body content of this class. */
	
	protected byte[] body;


	/**
	* Contruct a new body without a binary content 
	* but with an Encoder object.
	*/

	public BinaryBody(Encoder encoder) { 
	   this(new byte[0], encoder);
	}

	
	/**
	* Contruct a new body with a binary content 
	* and with an Encoder object.
	*/

	public BinaryBody(byte[] body, Encoder encoder) { 
	   super(encoder);
	   this.body = body;	   
	}

    
    /**
    * Set the body's binary content.
    */

	public void setBody(byte[] body) { 
	   this.body = body;
	}

    
    /**
    * Get the body's binary content.
    */

	public byte[] getBody() { 
	   return body;
	}

    
    /**
    * This method will write the body's content including header to the provided
    * output stream. It will throw a IOException if the current Encoder reference 
    * denotes null.
    */

	public void write(OutputStream out) throws IOException { 
	
        header.write(out);
	       
	    if(encoder == null) throw new IOException("Transport encoding failed: No Encoder object available.");

        encoder.encode(out, body);
        
        out.write(LineBreak.CRLF);
        
        out.flush();

	}
}