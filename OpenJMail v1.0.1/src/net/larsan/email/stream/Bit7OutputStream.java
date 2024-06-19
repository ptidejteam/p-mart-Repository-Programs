/*
*  Code: Bit7OutputStream.java
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

package net.larsan.email.stream;

import java.io.*;

/**
* This class confines an output stream to only send the seven first bits in every byte. 
*
* @author Lars J. Nilsson
* @version 1.0 23/10/00
*/

public class Bit7OutputStream extends FilterOutputStream {
	
	
	/**
    * Contruct a new Bit7OutputStream.
    */
	
	public Bit7OutputStream(OutputStream out) {
	    super(out);
    }

    
    /**
    * Write a the seven first bits of the provided byte to the 
    * output stream.
    */

    public void write(int b) throws IOException {
        out.write(b & 0x7F);
    }

    
    /**
    * Write a byte array to the stream.
    */

    public void write(byte[] in) throws IOException {
        write(in, 0, in.length);
    }

    
    /**
    * Write a part of a byte array to the stream, starting at <code>startAt</code>
    * and writing <code>length</code> bytes.
    */

    public void write(byte[] in, int startAt, int length) throws IOException {
        for(int i = 0; i < length; i++) {
            write(in[startAt + i]);
        }
    }
}