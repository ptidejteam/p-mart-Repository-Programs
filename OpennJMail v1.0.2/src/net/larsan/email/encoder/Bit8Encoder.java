/*
*  Code: Bit8Encoder.java
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
* This encoder ensures that the encoded content only will be sent directly to the 
* stream without any encoding. The <code>toByteArray</code> will attempt to convert 
* the characters to  the given charcter set but regardless this - the <code>encode</code> 
* method will only write the single bytes in without any encoding or preparation.<p>
*
* If no charset is provided before the <code>toByteArray</code> method is called the
* mentioned method will return an array of the low 8 bits of every unicode character
* provided in the method parameter.<p>
*
* This class should not be used so much by "message creators" as by "message parsers". 
* If characters outside the "US-ASCII" character set is needed a upon creation of 
* message a "Quoted-Printable" or a "Base64" encoding should be used. Message parsers 
* might however find this encoder useful since it will not distort any characters 
* using the last bit in a byte, thus it can be used for textual content where the 
* character set is not defined but <b>can</b> contain illegal characters due to faulty 
* message creation.
*
* @author Lars J. Nilsson
* @version 1.1 01/04/2001
*/

public class Bit8Encoder extends Encoder {
    
    
    /**
    * Create a new Bit8Encoder without a character set name.
    */
    
    public Bit8Encoder() {
        this("");
    }

    
    /**
    * Create a new Bit8Encoder with a character set name.
    */
    
    public Bit8Encoder(String charset) {
       super(charset);
    }

    
    /**
    * Get this encodings name: "8bit";
    */
    
    public String getContextName() {
        return "8bit";
    }

    
    /**
    * Perform a conversion from the char array to a byte array. This method will
    * use the charset provided upon contruction or in the <code>setCharset</code>
    * method.<p>
    *
    * If no character set is provided the method will simply write down
    * the first 8 bits of every unicode character to a byte array.
    */

	public byte[] toByteArray(char[] ch) throws UnsupportedEncodingException {
	   
	    ByteArrayOutputStream ba = new ByteArrayOutputStream();
	    
	    if(charset.length() > 0) {
	    
    	    PrintWriter writer = new PrintWriter(new OutputStreamWriter(ba, charset), false); 
    	   
    	    for(int i = 0; i < ch.length; i++) writer.write(ch[i]);
    	    
    	    writer.close();
    	    
    	} else {
    	   
    	    for(int i = 0; i < ch.length; i++) ba.write(ch[i] & 0xFF);
    	    
    	 }
	    
	    return ba.toByteArray();
	}

    /**
    * Write this byte array to output stream. No encoding is performed.
    */

    public void encode(OutputStream out, byte[] object) throws IOException {
        
        out.write(object, 0, object.length);
        out.flush();
        
    }
}