/*
*  Code: Base64Encoder.java
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

package net.larsan.email.encoder;

import java.io.*;
import net.larsan.email.stream.*;

/**
* This Encoder performs converting for byte arrays and strings in the Base 64 encoding.
* The Base64Encoder can be created with a charset value to perform convertion from
* characters to bytes according to a specified character set encoding in the 
* <code>toByteArray</code> method. The charset can be validated with the 
* <code>validateCharset</code> method which will tell if the charset is recognised
* and usable. If no charset is provided before the <code>toByteArray</code> method 
* is called the encoder will asume the text is in "US-ASCII".<p>
*
* @author Lars J. Nilsson
* @version 1.1 28/03/2001
*/

public class Base64Encoder extends Encoder {
    
    
    /**
    * Contruct a Base64Encoder with a user defined charset. The charset
    * will only be used if the <code>toByteArray</code> method is called.
    */
    
    public Base64Encoder(String charset) {
        super(charset);
    }

    
    /**
    * Contruct a Base64Encoder with default charset "US-ASCII". The charset
    * will only be used if the <code>toByteArray</code> method is called.
    */

    public Base64Encoder() {
        this("US-ASCII");
    }

    
    /**
    * Get the encoding's context name: "Base64".
    */
	
	public String getContextName() {
	    return "Base64";
	}

    
    /**
    * Perform a conversion from the character array to a byte array. This method will
    * use the charset provided upon contruction or in the <code>setCharset</code>
    * method. Default charset is "US-ASCII".
    */

	public byte[] toByteArray(char[] ch) throws UnsupportedEncodingException {
	    return (charset.length() > 0 ? (new String(ch)).getBytes(charset) : (new String(ch)).getBytes("US-ASCII"));
	}

    
    /**
    * Encode a byte array to the output stream using the Base 64 encoding
    * rules described in RFC 1521.
    */

	public void encode(OutputStream out, byte[] object) throws IOException {
	
	    Base64OutputStream b_out = new Base64OutputStream(out);
	    b_out.write(object);
	    b_out.flush();
	
	}
}