/**
*  Code: Encoder.java
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
* This is an abstract class for mail encoding objects. the Encoder instances are used
* to encode content for transport by various email tokens. To achive a general 
* skeleton which can be used in all contexts the Encoder contains methods for translation
* of characters to bytes and for encoding byte to a stream. However, the character to byte 
* transition is only used in email text parts.<p>
*
* Thus the contract of the Encoder class promises:<br><br>
*
* 1) To provide an encoding name. This name should be representative in the 
* context in which it should be used. A Base64Encoder providing the base 64 
* encoding for mail systems should return "Base64" as it's name so it can be used
* in transfers and headers. The name should also conform to a decoder name, so
* that both the Base64Decoder and the Base64Encoder should return "Base64" as their
* names.<br><br>
*
* 2) To be able to convert a char array to a byte array. This should be done in
* an encoding specific manner and use a specified character set. This method
* will only be used by text tokens.<br><br>
*
* 3) To encode and send a byte array to an output stream. This is the main feature that
* both binary and textual mail contexts use. This method should perform it's duty and then 
* return, taking special interest in not leaving any stream buffers unflushed.<p>
*
* @author Lars J. Nilsson
* @version 1.2 08/11/2001
*/

public abstract class Encoder implements Serializable {
    
    /** 
    * Character set for character to byte converting. This 
    * string object should contain the name of the character encoding
    * which the Encoder object should use. An empty character encoding
    * name string should be interpreted as the "US-ASCII" encoding.
    */
    
    protected String charset;
    
    
    /**
    * Create a new Encoder with a specified encoding.
    */
    
    public Encoder(String charset) {
        this.charset = charset;
    }


    /**
    * Create a new Encoder without a specified character encoding. Implementors
    * should recognise the empty string as the "US-ASCII" encoding.
    */
    
    public Encoder() {
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
    * encoding name string should be interpreted as the "US-ASCII" encoding.
    */

    public void setCharset(String charset) {
        this.charset = charset;
    }
 
    
    /** Get the encoding context name. */
    
    public abstract String getContextName();
    
    
    /** Convert a String to a byte array according to the proper encoding. */
    
    public abstract byte[] toByteArray(char[] ch) throws UnsupportedEncodingException;
    
    
    /** Encode a byte array to the parameter output stream. */
	
	public abstract void encode(OutputStream out, byte[] object) throws IOException;
	
	
	/**
    * Validate a charset name. Will return true if the charset is known
    * and can be use in this class.
    */
    
    public static boolean validateCharset(String charsetName) {
        
        try {
            
            new String(new byte[0], charsetName);
            
        } catch(UnsupportedEncodingException e) {
            
            return false;
            
        }
    
        return true;
        
    }
}