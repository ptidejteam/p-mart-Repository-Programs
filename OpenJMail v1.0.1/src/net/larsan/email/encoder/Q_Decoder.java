/**
*  Code: Q_Decoder.java
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
* This class extend the Decoder class to provide decoding for email header
* "Q" encoded material.
*
* @author Lars J. Nilsson
* @version 1.0.1 28/03/2001
*/

public class Q_Decoder extends Decoder {
    
    /**
    * Contruct a Q_Decoder with specified charset. The charset
    * will only be used if the <code>toCharArray</code> method is called.
    */
    
    public Q_Decoder(String charset) {
        super(charset);
    }

    
    /**
    * Contruct a Q_Decoder with default charset "US-ASCII". The charset
    * will only be used if the <code>toCharArray</code> method is called.
    */

    public Q_Decoder() {
        this("US-ASCII");
    }

    
    /**
    * Get the encoding's name: "Base64".
    */
	
	public String getContextName() {
	    return "Q";
	}

    /**
    * Decode the input stream according to the "Q" encoding.
    */

    public byte[] decode(InputStream in) throws IOException {
        
        ByteArrayOutputStream ba = new ByteArrayOutputStream();
        
        Q_InputStream q_in = new Q_InputStream(in);
        
        int tmp = 0;
        
        while((tmp = q_in.read()) != -1) ba.write(tmp);
        
        return ba.toByteArray();
        
    }
}