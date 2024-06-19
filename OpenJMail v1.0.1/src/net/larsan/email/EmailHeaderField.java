/*
*  Code: EmailHeader.java
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

import net.larsan.email.encoder.*;

/**
* This class is a handler for email header fields. It extends the header field
* class and provides additional encoding of it's content. Encoders used with this 
* class for use in email systems must conform to the rules set up by RFC 1522.<p>
*
* Email headers for RFC 822 messages can not be encoded and should only consist
* of "US-ASCII" characters. To achieve this implementors should call the constructor
* without an encoder object or with an Bit7Encoder object.<p>
*
* If the Encoder object belonging to this class is set to null - the content will
* be written to the output stream in pure "US-ASCII" characters.<p>
*
* This class encodes the full field value - it is up to the subclasses or the 
* encoder implementors to distinguish the encoding rules for the value. For example,
* the encoded header fields used in RFC 1522 limits encoded word length to 75 characters -
* thus encoders attempting the "B" or the "Q" encoding must make sure that limit is
* followed.
*
* @author Lars J. Nilsson
* @version 1.1 02/04/2001
*/

public class EmailHeaderField extends HeaderField {
    
    // encoder
	private Encoder encoder;
	
	
	/**
	* Contruct a new email header field with a name and value but without an
	* Encoder.
	*/
	
	public EmailHeaderField(String name, String value) {
	    this(name, value, new Bit7Encoder());
    }

	
	/**
	* Contruct a new encoded header field with a name and a value. 
	*/

	public EmailHeaderField(String name, String value, Encoder encoder) {
	    super(name, value);
	   
	    this.encoder = encoder;
	}

    
    /**
    * Set the encoder used by this field. If the Encoder is set
	* to null the content of the field will be written in the "US-ASCII"
	* character set.
    */

    public void setEncoder(Encoder encoder) {
        this.encoder = encoder;
    }

    
    /**
    * Get the encoder used by this field, can return null if the encoder is not set.
    */

    public Encoder getEncoder() {
        return encoder;
    }

    
    /**
    * Write the encoded content of this field to an output stream.
    */
	
    public void write(OutputStream out) throws IOException { 

        OutputStreamWriter writer = new OutputStreamWriter(out, "US-ASCII");
        
        writer.write(getName(), 0, getName().length());
        writer.write(": ");
        
        writer.flush();
        
        if(encoder != null) encoder.encode(out, encoder.toByteArray(getValue().toCharArray()));
        else {

            writer.write(getValue(), 0, getName().length());
            writer.flush();
                
        }
    }
}
