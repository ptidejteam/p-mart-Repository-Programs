/*
*  Code: EncodedBody.java
*  Originator: Java@Larsan.Net
*  Address: www.larsan.net/java/
*  Contact: webmaster@larsan.net
*
*  Copyright (C) 2000 Lars J. Nilsson
*
*       This program is free software; you can redistribute it and/or
*       modify it under the terms of the GNU Lesser General Public License
*       as published by the Free Software Foundation; either version 2.1
*       of the License, or (at your option) any later version.
*
*       This program is distributed in the hope that it will be useful,
*       but WITHOUT ANY WARRANTY; without even the implied warranty of
*       MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
*       GNU Lesser General Public License for more details.
*
*       You should have received a copy of the GNU Lesser General Public License
*       along with this program; if not, write to the Free Software
*       Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA. 
*/

package net.larsan.email;

import java.io.OutputStream;
import java.io.IOException;

import net.larsan.email.encoder.Encoder;

/**
* This is the abstract base class for all mail bodies containing a content which
* must be encoded before transport. This could be attachments, images, text
* or binary files.<p>
*
* The class uses a Encoder object to prepare the body for transport. The
* Encoder is an abstract skeleton which developers can subclass to 
* provide their own custom transport encodings.<p>
*
* This body part contains a header object which manages the header fields. The 
* contructor will attempt to set one header field automaticly: The 
* "Content-Transfer-Encoding" field. The value of this field is fetched
* from the Encoder object's <code>getContextName</code> method. Implementors
* who whishes to set this field to something else can retrieve the header
* object through the <code>getHeader</code> method and set the field manually.
*
* @author Lars J. Nilsson
* @version 1.0.1 21/10/2001
*/

public abstract class EncodedBody extends Body {
    
    
    /** Version id. */
    
    static final long serialVersionUID = 8496800575754684460L;
    
    
    /** Encoder object. */
    
	protected Encoder encoder;
	
	
	/** 
	* Contruct a new encoded body. The "Content-Transfer-Encoding" field in the 
	* header will be set to reflect the encoder's context name.
	*/
	
	public EncodedBody(Header header, Encoder encoder) {
	    super(header);
	    
	    this.encoder = encoder;   
	    
	    header.set(new EmailHeaderField("Content-Transfer-Encoding", encoder.getContextName()));
    }
	
	
	/** 
	* Contruct a new encoded body with a new empty email header object. The 
	* "Content-Transfer-Encoding" field in the header will be set to reflect
	* the encoder's context name.
	*/

	public EncodedBody(Encoder encoder) { 
	   this(new EmailHeader(), encoder);
	}

    
    /**
    * Get a reference to the current encoder. This method might return null if no
    * encoder is set.
    */
	
	public Encoder getEncoder() { 
	   return encoder;
	}
    
    
    /**
    * Set the body's internal Encoder object. The method will automaticly update 
    * the "Content-Tranfer-Encoding" field in the header object
    * to contain the context name of the present encoder.
    */

	public void setEncoder(Encoder encoder) { 
	   
	   this.encoder = encoder;
	   
	   header.set(new EmailHeaderField("Content-Transfer-Encoding", encoder.getContextName()));
	}


    /**
    * This method will send the body's content including header to the provided
    * output stream using the provided encoder object.
    */

	public abstract void write(OutputStream out) throws IOException;
	
}