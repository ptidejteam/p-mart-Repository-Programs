/**
*  Code: QuotedPrintableDecoder.java
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

package net.larsan.email.encoder;

import java.io.*;
import net.larsan.email.stream.*;

/**
* This Decoder class decode content according to the quoted printable encoding
* as descibed in RFC 1521.
*
* @author Lars J. Nilsson
* @version 1.0 25/10/00
*/

public class QuotedPrintableDecoder extends Decoder {
    
    /**
    * Contruct a QuotedPrintableDecoder with specified charset. The charset
    * will only be used if the <code>toCharArray</code> method is called.
    */
    
    public QuotedPrintableDecoder(String charset) {
        super(charset);
    }

    
    /**
    * Contruct a QuotedPrintableDecoder with default charset "US-ASCII". The charset
    * will only be used if the <code>toCharArray</code> method is called.
    */

    public QuotedPrintableDecoder() {
        this("US-ASCII");
    }

    
    /**
    * Get the encoding's name: "Quoted-Printable".
    */
	
	public String getContextName() {
	    return "Quoted-Printable";
	}

    
    /**
    * Decode the input from the provided stream, using quoted printable encoding.
    */

    public byte[] decode(InputStream in) throws IOException {
        
        ByteArrayOutputStream ba = new ByteArrayOutputStream();
        
        QuotedPrintableInputStream q_in = new QuotedPrintableInputStream(in);
        
        int tmp = 0;
        
        while((tmp = q_in.read()) != -1) ba.write(tmp);
        
        return ba.toByteArray();
        
    }
}