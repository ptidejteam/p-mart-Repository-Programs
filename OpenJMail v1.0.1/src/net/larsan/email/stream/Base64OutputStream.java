/*
*  Code: Base64OutputStream.java
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
* Base 64 encoding. It is implemented as an FilterOutputStream so any OutputStream
* can be wrapped in it to provide the encoding.<p>
*
* The base 64 encoding is defined in RFC 1522. Basically it splits 3 bytes (3 * 8 bits) into
* four groups (4 * 6 bits) which is representad by a character value. This behaviour makes
* sure that no bit of a higher order than 6 is sent to a stream. <p>
*
* For email message body purposes the line length of the base 64 encoding
* is provided in the static variable DEFAULT_LINE_LENGTH.
*
* @author Lars J. Nilsson
* @version 1.0 23/10/00
*/

public class Base64OutputStream extends FilterOutputStream {
    
    /** Default line length, ie: 76. */
    
    public static final int DEFAULT_LINE_LENGTH = 76;
    
    
    // instance data
    
    private int lineLength, bufferSize, lineCount, totalCount;
    
    private byte[] buffer;
    
    
    // base 64 character scheme
    
    private static final char[] B64_STATICS = { 
        
        'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 
        'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 
        'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 
        'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', 
        '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '+', '/' 
        
    };
	
	/**
	* Contruct a new Base64OutputStream. To enable unlimited
	* line length set the <code>lineLength</code> parameter to -1.
	*/
	
	public Base64OutputStream(OutputStream out, int lineLength) {
	    super(out);
	   
	    this.lineLength = lineLength;
	    
	    bufferSize = 0;
	    lineCount = 0;
	    totalCount = 0;
	    
	    buffer = new byte[3];
    }

    
    /**
    * Contruct a new Base64OutputStream with default line length (76).
    */
	
	public Base64OutputStream(OutputStream out) {
	    this(out, DEFAULT_LINE_LENGTH);
    }

    
    /**
    * Write a byte to the output stream. The byte may be
    * kept in a buffer for encoding purposes, the buffer can be
    * cleared by flushing or closing the stream.
    */

    public void write(byte b) throws IOException {
        buffer[bufferSize] = b;
        bufferSize = bufferSize + 1;
        if(bufferSize > 2) encodeBuffer();
    }

    
    /**
    * Write and encode byte array to the stream. This stream is buffered to create the
    * encoded content, flush or close to empty the buffer.
    */

    public void write(byte[] in) throws IOException {
        write(in, 0, in.length);
    }

    
    /**
    * Write and encode a part of a byte array to the stream, starting at 
    * <code>startAt</code> and writing <code>length</code> bytes. This 
    * stream is buffered to create the encoded content, flush or close to 
    * empty the buffer.
    */

    public void write(byte[] in, int startAt, int length) throws IOException {
        for(int i = 0; i < length; i++) {
            write(in[startAt + i]);
        }
    }

    
    /**
    * Flush the stream. This method will force bytes in the buffer
    * to be encoded to the stream.
    */

    public void flush() throws IOException {
        if(bufferSize > 0) encodeBuffer();
    }

    
    /**
    * Close the stream. This method will force bytes left in the buffer
    * to be encoded to the stream.
    */

    public void close() throws IOException {
        this.flush();
        super.close();
    }

    
    /**
    * Get the number of encoded bytes written to the stream. If this method
    * is called before the stream is closed or flushed there might be
    * trailing bytes left in the buffer.
    */

    public int getWrittenLength() {
        return totalCount;
    }


    /**
    * This method performs the actual encoding upon the recieved buffer.
    */

    private void encodeBuffer() throws IOException {
        
        // check line length -> add newLine and reset count
        
        if(lineLength > 0 && (lineCount + 4 > lineLength)) {
            out.write(LineBreak.CRLF);
            lineCount = 0;
        }
    
        
        // zero fill unused buffer slots
    
        if(bufferSize < 3) {
    
            switch(bufferSize) {
                case 1: buffer[1] = 0x00;
                case 2: buffer[2] = 0x00;
            }
        }

        // encode the first byte into two characters

        out.write(B64_STATICS[(buffer[0] >>> 2) & 0x3f]);
        out.write(B64_STATICS[((buffer[0] << 4) & 0x30) | ((buffer[1] >>> 4) & 0x0f)]);

        
        // then check the number of bytes to encode and 
        // fill with '=' characters if we're at the end of the file
        // and have empty buffer slots

        switch(bufferSize) {

            case 3 :

                out.write(B64_STATICS[((buffer[1] << 2) & 0x3c) | ((buffer[2] >>> 6) & 0x03)]);
                out.write(B64_STATICS[(buffer[2] & 0x3f)]);

                break;

            case 2 :

                out.write(B64_STATICS[((buffer[1] << 2) & 0x3c) | ((buffer[2] >>> 6) & 0x03)]);
                out.write('=');
                break;

            case 1 :

                out.write('=');
                out.write('=');
                break;

        }
    
        // update counts and set buffer size to zero
    
        lineCount += 4;
        bufferSize = 0;
        totalCount += 4;
        
    }
}
