/**
*  Code: B_Encoder.java
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
* This Encoder performs converting for byte arrays and strings in the "b" encoding used
* in email header fields.The B_Encoder must be created with a charset value to 
* perform convertions from characters to bytes in the <code>toByteArray</code> method. 
* The charset can be validated with the <code>validateCharset</code> method which
* will return true the charset is recognised and usable.
*
* @author Lars J. Nilsson
* @version 1.1 28/03/2001
*/

public class B_Encoder extends Base64Encoder {

    /**
    * Contruct a B_Encoder with a user defined charset. The charset
    * will be used when the <code>toByteArray</code> method is called.
    */
	
	public B_Encoder(String charset) {
	    super(charset);
	}

    
    /**
    * Get the encoding's name: "B".
    */

    public String getContextName() {
        return "B";
    }
    
    
    /**
    * Encode a byte array to the output stream using the "B" encoding
    * rules described in RFC 1522.
    */

    public void encode(OutputStream out, byte[] object) throws IOException {
        
        char[] charset = super.getCharset().toCharArray();
        
        char[] start = { '=', '?' };
        char[] middle = { '?', 'B', '?' };
        char[] end = { '?', '=' };
        
        send(out, start);
        send(out, charset);
        send(out, middle);
        
        B_OutputStream b_out = new B_OutputStream(out);
        
        int count = 1; // count lines
        int line = 70; // line length
        
        for(int i = 0; i < object.length; i++) {
            
            // if written bytes plus the number of control characters written is
            // more then the line length mulitplied with the line count...
            
            // there must be an easier way to do this...
        
            if((b_out.getWrittenLength() + ((7 + charset.length) * count)) > (count * line)) {
            
                send(out, end);
                
                if(i < object.length - 1) { // fold and start over
                    
                    out.write(LineBreak.CRLF);
                    
                    out.write('\t');
                    
                    send(out, start);
                    send(out, charset);
                    send(out, middle);
                }
            
                count += 1;
            }
                
            b_out.write(object[i]);
 
        }
    
        b_out.flush();
        send(out, end);
        
    }

    /**
    * Send and flush characters.
    */
    
    private void send(OutputStream out, char[] tmp) throws IOException {
        
        for(int i = 0; i < tmp.length; i++) out.write(tmp[i]);
        
        out.flush();
    }
}