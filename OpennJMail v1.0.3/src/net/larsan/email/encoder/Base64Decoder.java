/*
*  Code: Base64Decoder.java
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

package net.larsan.email.encoder;

import java.io.*;
import net.larsan.email.stream.*;

/**
* This class extends the Decoder to provide decoding for the base 64
* encoding. Please refer to the Base64InputStream and the Decoder documentation
* for more details.
*
* @author Lars J. Nilsson
* @version 1.1 08/11/01
*/

public class Base64Decoder extends Decoder {
    
    
    /**
    * Contruct a Base64Decoder with specified charset. The charset
    * will only be used if the <code>toCharArray</code> method is called.
    */
    
    public Base64Decoder(String charset) {
        super(charset);
    }

    
    /**
    * Contruct a Base64Decoder with default charset "US-ASCII". The charset
    * will only be used if the <code>toCharArray</code> method is called.
    */

    public Base64Decoder() {
        this("US-ASCII");
    }

    
    /**
    * Get the encoding's name: "Base64".
    */
	
	public String getContextName() {
	    return "Base64";
	}

    
    /** Decode the parameter input stream to a byte array. */

    public byte[] decode(InputStream in) throws IOException {

        ByteArrayOutputStream ba = new ByteArrayOutputStream();
        BufferedOutputStream baout = new BufferedOutputStream(ba);
        Base64InputStream b_in = new Base64InputStream(in);
        
        int tmp = 0;
        
        byte[] buff = new byte[2048];
        
        while((tmp = b_in.read(buff)) != -1) baout.write(buff, 0, tmp);
        
        baout.flush();
        
        return ba.toByteArray();
        
    }
}