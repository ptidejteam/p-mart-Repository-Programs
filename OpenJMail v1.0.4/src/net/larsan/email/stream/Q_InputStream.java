/*
*  Code: Q_InputStream.java
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
* This class provides an InputStream for reading "Q" encoded data in mail
* headers. It extends the QuotedPrintableInputStream.<p>
*
* @author Lars J. Nilsson
* @version 1.0 23/10/00
*/

public class Q_InputStream extends QuotedPrintableInputStream {
    
    
    /**
    * Contruct a new Q_InputStream wrapped around the parameter input stream.
    */
	
	public Q_InputStream(InputStream in){
	    super(in);
	}

    
    /**
    * Read a single byte from the stream. This method will return a byte value
    * as an integer or mark the end of the stream with -1.
    */

	public int read() throws IOException {
	
	    int a = in.read();
	    
	    // underscore represents spaces in this decoding
	    // and new lines or returns shouldn't exist, but if they
	    // do we'll simply ignore them
	    
	    if(a == '_') return ' ';
	    else if(a == '\n' || a == '\r') return read(); 
	    else if(a == '=') {
	       
	       int b = in.read();
	       int c = in.read();
	       
	       return decodeBytes(b, c);
	       
	   } else return a;
	}
}
