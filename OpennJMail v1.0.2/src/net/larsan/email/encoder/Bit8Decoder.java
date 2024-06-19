/*
*  Code: Bit8Decoder.java
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
* This is an empty Decoder class. It is implemented to only to form a valid
* Encoder / Decoder pair togheter with the Bit8Encoder.
*
* @author Lars J. Nilsson
* @version 1.0 17/11/00
*/

public class Bit8Decoder extends Decoder {
    
    /**
    * Contruct a Bit8Decoder with specified charset.
    */
    
    public Bit8Decoder(String charset) {
        super(charset);
    }

    
    /**
    * Contruct a Bit8Decoder with default charset "US-ASCII". 
    */

    public Bit8Decoder() {
        this("US-ASCII");
    }

    
    /**
    * Get the encoding's name: "8Bit".
    */
	
	public String getContextName() {
	    return "8Bit";
	}


    /** Decode this input stream to a byte array. */

    public byte[] decode(InputStream in) throws IOException {
        
        ByteArrayOutputStream ba = new ByteArrayOutputStream();
        BufferedOutputStream baout = new BufferedOutputStream(ba);
       
        int tmp = 0;
        
        byte[] buff = new byte[1024];
        
        while((tmp = in.read(buff)) != -1) baout.write(buff, 0, tmp);
        
        baout.flush();
       
        return ba.toByteArray();
        
    }
}