/*
*  Code: QuotedPrintableOutputStream.java
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
* This class provides an OutputStream that encodes it's content to the 
* Quoted Printable encoding. It is implemented as an FilterOutputStream so 
* that any OutputStream can be wrapped in it to provide the encoding.<p>
*
* For email message body purposes the line length of the Quoted Printable encoding
* is provided in the static DEFAULT_LINE_LENGTH.<p>
*
* This class separates the different lines by a CRLF sequence according to
* RFC 1521.<p>
*
* @author Lars J. Nilsson
* @version 1.0 23/10/00
*/

public class QuotedPrintableOutputStream extends FilterOutputStream {
    
    /** Default line length for must implementations */
    
    public static final int DEFAULT_LINE_LENGTH = 76;

    
    // this is the hex notation characters to use
    
    private static final char[] HEX_STATICS = { 
    
        '0', '1', '2', '3', '4', '5', '6', '7', 
        '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' 
    
     };


    // instance data
    private int lineLength, lineCount, lastChar, totalCount;

    
    /**
    * Contruct a new quoted printable output stream wrapped around an 
    * input stream using a custom line length.
    */

    public QuotedPrintableOutputStream(OutputStream out, int lineLength) {
        super(out);
        
        this.lineLength = lineLength - 1;
        
        lineCount = 0;
        lastChar = -1;
        totalCount = 0;
    }

    
    /**
    * Contruct a new quoted printable output stream wrapped around an input stream
    * using the default line length (76 charcters).
    */

    public QuotedPrintableOutputStream(OutputStream out) {
        this(out, DEFAULT_LINE_LENGTH);
    }

    
    /**
    * Write a byte to the output stream.
    */

    public void write(int b) throws IOException {
        
        // Make sure the int is in range
        
        b &= 0xFF; 
        
        
        // We use the last character as an indicator of what to do next, 
        // if the last character was a space and it is followed by a line
        // break it must be encoded before we send the next
        
        if(lastChar == ' ') {
        
            if(b == '\r' || b == '\n') encode(lastChar);
            else send(lastChar);
        }
    
        try {
    
            if(b == ' ') return; // ignore the space for now
            else if(b == '\r') {
                
                // send a line break here
                
                out.write(LineBreak.CRLF);
                
                lineCount = 0;
                
            } else if(b == '\n') {
                
                if(lastChar == '\r') return; // we've already done this
                else {
                    
                    // send a line break here
                    
                    out.write(LineBreak.CRLF);
                    
                    lineCount = 0;
                }
            
            } else if(b < 0x20 || b > 0x7E || b == 0x3D) {
                
                // is the byte is outside the allowed range
                // it must be decoded
                
                encode(b); 
                
            } else send(b); // allowed charcter, just send it
            
        } finally {
            
            // the last character is buffered to make sure we 
            // handle spaces and new lines correctly
            
            lastChar = b; 
        }
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


    /**
    * Get the number of encoded bytes written to the stream.
    */

    public int getWrittenLength() {
        return totalCount;
    }

    
    /** 
    * This method encodes a byte to hex notation and writes
    * it to the stream.
    */

    protected void encode(int b) throws IOException {
        
        // check for space on the line
        checkLineLength(3);
        
        // write hex characters
        out.write('=');
        out.write(HEX_STATICS[(b & 0xF0) >>> 4]);
        out.write(HEX_STATICS[b & 0xF]);
        
        totalCount += 3;
    }

    
    /**
    * This method writes a byte to the stream and check the line length.
    */

    protected void send(int b) throws IOException {
        
        checkLineLength(1);
        
        out.write(b);
        
        totalCount += 1;
    }

    
    /** This method checks the line length. */
    
    private void checkLineLength(int i) throws IOException {
        
        if(lineLength > 0 && (lineCount + i > lineLength)) {
            out.write('=');
            out.write(LineBreak.CRLF);
            lineCount = 0;
        }
    
        lineCount += i;
    }
}