/*
*  Code: MailOutputStream.java
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
* This OutputStream controls the formation of the output stream according to 
* the rules of the SMTP protocoll described in RFC 821. It promise that:
*
* 1) No bare line feed will occur. All new lines will be send in the canonical 
* form with a '\r' followed by a '\n'.
*
* 2) All dots ('.') that start a new line will be presented transparent 
* by adding another dot before it. This procedure is described in RFC 821 
* to make sure no messages are prematurely ended. To end a message or send a 
* non-transparent dot the <code>writeDot</code> method must be used.
*
* @author Lars J. Nilsson
* @version 1.0.1 29/10/00
*/

public class MailOutputStream extends FilterOutputStream {
    
    // char buffer
    private int lastChar;
    
    
    /**
    * Create a new MailOutputStream.
    */
    
    public MailOutputStream(OutputStream out) {
	   super(out);
	   
	   lastChar = 0;
	}

    
    /**
    * Write a single byte to the stream. This method will search
    * for new line characters and make sure they get printed as specified
    * by the <code>getNewLine()</code> method.
    */
	
	public void write(int b) throws IOException {
	   
        if(b == '\r') writeln();
        else if(b == '\n') {
            
            if(lastChar != '\r') writeln();
            
    	} else if(b == '.' && (lastChar == '\r' || lastChar == '\n')) {
    	       
    	       // last sequence was a line feed so make the dot transparent
    	       // by adding another dot before it: two dots at the start of a
    	       // line will be interpreted as one by the SMTP service
    	       
            out.write('.');
    	    out.write('.');
    	       
    	} else out.write(b);
            
        lastChar = b;
	}

    
    /**
    * Write a byte array to the stream.
    */

	public void write(byte[] b) throws IOException{
	    write(b, 0, b.length);
	}

    
    /**
    * Write a part of a byte array to the stream, starting at <code>startAt</code>
    * and writing <code>length</code> bytes.
    */

    public void write(byte[] b, int startAt, int length) throws IOException {
        for(int i = 0; i < length; i++) {
            write(b[startAt + i]);
        }
    }

    
    /**
    * Write an a dot ('.') to the output stream without making it
    * transparent. This method can be used to end a SMTP DATA command.
    */
    
    public void writeDot() throws IOException {
        out.write('.');
    }

    
    /**
    * Write a canonical new line sequence to the output stream.
    */

    public void writeln() throws IOException {
        out.write(getNewLine());
    }

    
    /**
    * Return the canonical new line character sequence. Ie:
    * a '\r' followed by a '\n' in a byte array.
    */

	public static byte[] getNewLine() {
	   return LineBreak.CRLF;
	}
}
