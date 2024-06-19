/*
*  Code: Bit7Encoder.java
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
* This encoder ensures that the encoded content only uses the
* first 7 bits of each byte. It means that the <code>toByteArray</code> will
* attempt to convert the characters to the "US-ASCII" charset.<p>
*
* This encoder also can check for specials in a text token such as the name part of 
* an address field and hide critical characters with a backslash. This function is
* not set by default. The critical characters are:<br><br>
*
* &nbsp;&nbsp;()&lt;&gt;@,;:\&quot;.[]<br><br>
*
* To this list the encoder will also quote the single quote character
* for extended clarity. For more information regarding allowed characters
* in different message tokens, see RFC 822.
*
* @author Lars J. Nilsson
* @version 1.0.1 28/03/2001
*/

public class Bit7Encoder extends Encoder {
    
    // hide quotes and backslashes ?
    private boolean doQuote;
    
    
    /**
    * Create a new Bit7Encoder that does not attempt to hide
    * critical characters.
    */
    
    public Bit7Encoder() {
        this(false);
    }

    
    /**
    * Create a new Bit7Encoder which will attempt to hide critical
    * characters such as the quote character with a backslash if
    * the parameter "qouteSpecials" is set to true. For a list
    * of special characters - see above in the class definition.
    */

    public Bit7Encoder(boolean quoteSpecials) {
        super("US-ASCII");
        doQuote = quoteSpecials;
    }

    
    /**
    * Set if the encoder should attempt to hide special
    * characters such as the quote character with a backslash.
    */
    
    public void setQuoteSpecials(boolean quoteSpecials) {        
        doQuote = quoteSpecials;
    }

    
    /**
    * Check if the encoder attempts to hide special
    * characters such as the quote character with a backslash.
    */
    
    public boolean getQuoteSpecials() {        
        return doQuote;
    }
    
    
    /**
    * Get this encodings name: "7bit";
    */
    
    public String getContextName() {
        return "7bit";
    }

    
    /**
    * Convert this character array to a byte array of "US-ASCII" characters.
    */

    public byte[] toByteArray(char[] ch) throws UnsupportedEncodingException {
        
        ByteArrayOutputStream ba = new ByteArrayOutputStream();
        PrintWriter writer = new PrintWriter(new OutputStreamWriter(ba, "US-ASCII"), false);
        
        for(int i = 0; i < ch.length; i++) {
            
            if(doQuote && isSpecial(ch[i])) writer.write('\\'); 
            writer.write(ch[i]);
        }
    
        writer.close();
        
        return ba.toByteArray();
    
    }

    /**
    * Encode this byte array. Only the 7 first bit of each byte will be send.
    */

    public void encode(OutputStream out, byte[] object) throws IOException {
        
        Bit7OutputStream b_out = new Bit7OutputStream(out);
        b_out.write(object);
        
    }

	
	/**
	* Checks if a character is regarded as special and needs hiding.
	*/
	
	protected boolean isSpecial(int ch) {
	   
	   /**
	   * Here is the specials we want to look for. Usually the specials differs 
	   * between different part or "tokens" of the message to be encoded, 
	   * but since every character MAY be quoted we can simply quot every 
	   * special character and be done with it.
	   */
	   
	   switch(ch) {

	       case '(': return true;
	       case ')': return true;
	       case '<': return true;
	       case '>': return true;
	       case '@': return true;
	       case ',': return true;
	       case ';': return true;
	       case ':': return true;
	       case '.': return true;
	       case '[': return true;
	       case ']': return true;
	       case '\"': return true;
	       case '\'': return true;
	       case '\\': return true;
	       
	       default: return false;
	       
	   }
    }
}