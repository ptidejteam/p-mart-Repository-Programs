/**
*  Code: MailInputStream.java
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
* This input stream performs two general email specific conversions. First, it
* makes sure that email new line character sequence - ie: the canonical form
* "\r\n" - is returned as a platform specific new line character sequence. Secondly,
* it checks for transparent dots - ie: if a dot at the beginning of the line is directly
* followed by another dot only one of them are returned.
*
* @author Lars J. Nilsson
* @version 1.0 06/11/00
*/

public class MailInputStream extends FilterInputStream {
    
    private int pos;
    private byte last;
    private boolean haveDot;
    
    private byte[] newline = LineBreak.CRLF;
    
    
    /**
    * Contruct a new MailInputStream.
    */
	
	public MailInputStream(InputStream in) {
	    super(in);
	    last = -1;
	    haveDot = false;
	    retreiveNewLine();
	    pos = newline.length;
    }

    
    /**
    * Read a single byte from the stream. This method will return a byte value
    * as an integer and mark the end of the stream with -1.
    */

    public int read() throws IOException {
        
        if(pos < newline.length) return newline[pos++];
        else {
            
            int tmp = in.read();
            
            if(tmp == '\r') {
                
                last = (byte)'\r';
                pos = 0;
                return read();
                
            } else if(tmp == '\n') {
                
                if(last != '\r') {
                    
                    last = (byte)'\r';
                    pos = 0;
                    
                } 
                
                return read();
                
            } else if(tmp == '.') {
                
                if(last == '.' && haveDot) {

                    haveDot = false;
                    return read();
                    
                } else if(last == '\r') {
                    
                    last = (byte)'.';
                    haveDot = true;
                    return '.';
                    
                } else {
                    
                    haveDot = false;
                    last = (byte)'.';
                    return '.';
                    
                }
            } else {
                
                last = (byte)tmp;
                return tmp;
            }
        }
    }

    
    /**
    * Read bytes from stream into array. Will return -1 if the stream is
    * ended or otherwise the number of bytes read.
    */

    public int read(byte[] out) throws IOException {
        return read(out, 0, out.length);
    }

    
    /**
    * Read bytes from stream into array starting at <code>startAt</code>, reading
    * <code>length</code> number of bytes. Will return -1 if the stream is
    * ended or otherwise the number of bytes read.
    */
        
    public int read(byte[] out, int startAt, int length) throws IOException {

	    int answer = 0;
	    int tmp = 0;
	
        for (int i = 0; i < length; i++) {
            
            if ((tmp = read()) == -1) {
		    
		        if(i == 0) answer = -1; 
                break;
		    
		    } else {
            
                out[startAt+i] = (byte)tmp;
                answer++;
            
            }
        }

        return answer;
    }

    /**
    * Checks if the stream supports marks, which it does not.
    */

    public boolean markSupported() {
        return false;
    }

    /**
    * Close the stream.
    */

    public void close() throws IOException {
        super.close();
    }

    /**
    * Not implemented! Will throw an IOException if used.
    */

    public int available() throws IOException {
        throw new IOException("Method not suported: available()");
    }

    /**
    * Not implemented! Will throw an IOException if used.
    */

    public long skip(long skip) throws IOException {
        throw new IOException("Method not suported: skip()");
    }

    /**
    * Not implemented!
    */

    public synchronized void mark() { }
    
    /**
    * Not implemented!
    */
    
    public synchronized void reset() { }

	
	/** This method attempts to retreive the system new line separator. */

    private void retreiveNewLine() {
        
       String tmp = null;
	   
	   try {
	       
	       tmp = System.getProperty("line.separator");
	       
	   } catch(SecurityException e) {}
	   
	   if(tmp != null) {
	       
	       newline = new byte[tmp.length()];
	       
	       for(int i = 0; i < tmp.length(); i++) newline[i] = (byte)tmp.charAt(i);
	       
	   } 
    }
}
