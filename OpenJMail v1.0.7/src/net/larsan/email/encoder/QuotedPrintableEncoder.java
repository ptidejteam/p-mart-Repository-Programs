/**
*  Code: QuotedPrintableEncoder.java
*  Originator: Java@Larsan.Net
*  Address: www.larsan.net/java/
*  Contact: webmaster@larsan.net
*
*  Copyright (C) 2001 Lars J. Nilsson
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

package net.larsan.email.encoder;

import java.io.*;
import net.larsan.email.stream.*;

/**
* This Encoder performs converting for byte arrays and strings in 
* the qouted printable encoding.<p>
*
* The QuotedPrintableEncoder can be created with a charset value to perform converting from
* characters to bytes in the <code>toByteArray</code> method. The charset can be validated
* with the <code>validateCharset</code> method which will tell if the charset is recognised
* and usable. If no charset is provided before the <code>toByteArray</code> method is called
* the encoder will asume the text is in "US-ASCII".<p>
*
* The Quoted Printable encoding is described in RFC 1521.
*
* @author Lars J. Nilsson
* @version 1.0.1 28/03/2001
*/

public class QuotedPrintableEncoder extends Encoder {
    
    
    /**
    * Contruct a QuotedPrintableEncoder with a user defined charset. The charset
    * will only be used if the <code>toByteArray</code> method is called.
    */
    
    public QuotedPrintableEncoder(String charset) {
        super(charset);
    }

    
    /**
    * Contruct a QuotedPrintableEncoder with default charset "US-ASCII". The charset
    * will only be used if the <code>toByteArray</code> method is called.
    */

    public QuotedPrintableEncoder() {
        this("US-ASCII");
    }

    
    /**
    * Get the encoding name: "Quoted-Printable".
    */
	
	public String getContextName() {
	    return "Quoted-Printable";
	}

	
	/**
	* This method checks for special characters in a character array
	* which should be encoded in in a quoted printable context and calculates
	* the encoded length of the array.<p>
	*/
	
	public static int encodedLength(char[] string) { 
	    return Q_OutputStream.encodedLength(string);
    }

    
    /**
    * Perform a conversion from the char array to a byte array. This method will
    * use the charset provided upon contruction or in the <code>setCharset</code>
    * method. Default charset is "US-ASCII".
    */

	public byte[] toByteArray(char[] ch) throws UnsupportedEncodingException {
	    return (charset.length() > 0 ? (new String(ch)).getBytes(charset) : (new String(ch)).getBytes("US-ASCII"));
	}

    
    /**
    * Encode a byte array to the output stream using the Quoted Printable encoding
    * rules described in RFC 1521.
    */

	public void encode(OutputStream out, byte[] object) throws IOException {
	
	   QuotedPrintableOutputStream q_out = new QuotedPrintableOutputStream(out);
	   q_out.write(object);
	   q_out.flush();
	
	}
}