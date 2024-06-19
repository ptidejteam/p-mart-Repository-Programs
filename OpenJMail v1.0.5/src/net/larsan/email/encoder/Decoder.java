/*
*  Code: Decoder.java
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

/**
* This is an abstract class for mail decoding objects. The Encoder / Decoder
* design provides a context general skeleton for custom transport encodings and
* are very alike in contract.<p>
*
* A Decoder class promises three things:<br><br>
*
* 1) To provide an encoding name. This name should be representative in the 
* context in which it should be used. Thus a Base64Decoder providing the base 64 
* decoding for mail systems should return "Base64" as it's name so it can be used
* in transfers and headers. The name should also conform to a encoder name, so
* that both the Base64Decoder and the Base64Encoder return "Base64" as their
* names.<br><br>
*
* 2) To be able to convert a byte array to a char array. This should be done in
* an encoding specific manner and use a specified character set. This method
* will only be used when a mail parser encounters an encoded textual content.<br><br>
*
* 3) To read and decode an input stream to a byte array. Implementing classes should
* remember to flush all it's stream buffers.<p>
*
* @author Lars J. Nilsson
* @version 1.1 08/11/01
*/

public abstract class Decoder implements Serializable {
    
    
    /** 
    * Character set for character to byte converting. This 
    * string object should contain the name of the character encoding
    * which the Decoder object should use. An empty character encoding
    * name string should be interpreted as the "US-ASCII" encoding.
    */
    
    protected String charset;
    
    
    /**
    * Create a new Decoder with a specified character set name.
    */
    
    public Decoder(String charset) {
        this.charset = charset;
    }

    
    /**
    * Create a new Decoder without a specified character encoding. Implementors
    * should recognise the empty string as the "US-ASCII" encoding.
    */
    
    public Decoder() {
        this("");
    }

    
    /**
    * Get character set name. An empty character encoding
    * name string should be interpreted as the "US-ASCII" encoding.
    */

    public String getCharset() {
        return charset;
    }

    
    /**
    * Set charset to use in th <code>toByteArray</code> method. An empty character 
    * encoding name string will be interpreted as the "US-ASCII" encoding.
    */

    public void setCharset(String charset) {
        this.charset = charset;
    }
 
    
    /** Get the encoding context name. */
    
    public abstract String getContextName();
    
    
    /** 
    * Convert a byte array to a char array according to the current character set 
    * encoding. This method will throw an UnsupportedEncodingException if the 
    * character encoding is not known.
    */
    
    public char[] toCharArray(byte[] ba) throws UnsupportedEncodingException {
        return (charset.length() > 0 ? (new String(ba, charset)).toCharArray() : (new String(ba)).toCharArray());
    }
    
    
    /** Decode this input stream into a byte array. */
	
	public abstract byte[] decode(InputStream out) throws IOException;
	
	
    /**
    * Validate a character set name. Will return true if the charset is known
    * and can be use in this class.
    */
    
    public static boolean validateCharset(String charset) {
        
        try {
            
            new String(new byte[0], charset);
            
        } catch(UnsupportedEncodingException e) {
            
            return false;
            
        }
    
        return true;
        
    }
}