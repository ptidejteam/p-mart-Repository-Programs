/**
*  Code: Q_OutputStream.java
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

package net.larsan.email.stream;

import java.io.*;

/**
* This class provides an OutputStream that encodes it's content to the 
* "Q" encoding for email headers. It extends the QuotedPrintableOutputStream.<p>
*
* @author Lars J. Nilsson
* @version 1.0.1 28/03/2001
*/

public class Q_OutputStream extends QuotedPrintableOutputStream {
	
	
	/**
	* Contruct a new Q_OutputStream wrapped around the provided output stream.
	*/
	
	public Q_OutputStream(OutputStream out) {
	   super(out, -1);
    }

    
    /**
    * Write and encode a byte to the output stream.
    */

    public void write(int b) throws IOException {
        
        // check the byte range
        
        b &= 0xFF;
        
        // we'll send the space character as an underscore
        // which is permitted to make the encoded line easier to
        // understand for mail readers who can't decode it
        
        if(b == ' ') super.send('_');
        else if(isSpecial(b)) super.encode(b); // special character
        else if(b < 0x20 || b > 0x7E) super.encode(b); // outside ACII range
        else super.send(b); // just send
    }


    /**
	* This method checks for special characters in a byte array
	* which should be encoded in in a Quoted Printable Header and calculates
	* the encoded length of the array.<p>
	*/
	
	public static int encodedLength(byte[] input) {
	    
	    char[] send = new char[input.length];
	    
	    for(int i = 0; i < send.length; i++) {
	        send[i] = (char)(input[i] & 0xFF);
	    }
	 
	    return encodedLength(send);
	    
	}
	
	
	/**
	* This method checks for special characters in a char array
	* which should be encoded in in a Quoted Printable Header and calculates
	* the encoded length of the array.<p>
	*/
	
	public static int encodedLength(char[] input) {
	   
	    int count = 0;
	   
	    for(int i = 0; i < input.length; i++) {
	       
	       int ch = input[i] & 0xFF;
	       
	       if(isSpecial(ch) || ch < 0x20 || ch > 0x7E) count += 3;
	       else count++;
	    }

        return count;
    }
	
	/**
	* This is where we check every character to see if it is
	* special enough to be encoded. Usually the specials differs between 
	* different parts or "tokens" of the message to be encoded, but since 
	* every character MAY be encoded and the restriction is linear in the 
	* case that it only adds characters to the forbidden list we can 
	* simply encode every special character and be done with it.
	*/
	
	protected static boolean isSpecial(int ch) {
	   
	   switch(ch) {
	       case '=': return true;
	       case '_': return true;
	       case '?': return true;
	       case '(': return true;
	       case ')': return true;
	       case '<': return true;
	       case '>': return true;
	       case '@': return true;
	       case '\"': return true;
	       case '\'': return true;
	       case ',': return true;
	       case ';': return true;
	       case ':': return true;
	       case '\\': return true;
	       case '.': return true;
	       case '[': return true;
	       case ']': return true;
	       
	       default: return false;
	   }
    }
}
