/*
*  Code: ServerInputStream.java
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
* This class models an input stream for email server components. 
* It extends a pushback input stream and ads some convenient
* methods.<p>
*
* The first method, <code>readMailData</code>, read binary
* data until a stop delimiter, ie: CRLF - '.' - CRLF.<p>
*
* A seconds method, <code>readLine</code>, read a line of
* data but return it as binary content as opposed to
* character data.<p>
*
* @author Lars J. Nilsson
* @version 1.1 02/11/2001
*/

public class ServerInputStream extends ByteArrayPushbackStream {
    
    
    /** Create a new stream. */
	
	public ServerInputStream(InputStream in) {
	    super(in);
    }
    
    
    /**
    * Read data untill a delimiter sequence
    * is found. If the stream end prematurely a EOFException
    * will be raised.
    */

    public byte[] readData(byte[] delim) throws EOFException, IOException {
        return readData(delim, false);
    }
    
    
    /**
    * Read data untill a delimiter sequence
    * is found. If the stream end prematurely a EOFException
    * will be raised.
    */

    public byte[] readData(byte[] delim, boolean includeDelim) throws EOFException, IOException {
        
        ByteArrayOutputStream ba = new ByteArrayOutputStream();
        
        int[] stack = new int[delim.length];
        
        int stackIndex = 0;
        
        int traceIndex = 0;
        
        int in = 0;
        
        while(true) {
            
            in = read();
            
            if(in == -1) throw new EOFException();
            
            if((in & 0xFF) == delim[traceIndex]) {
                
                traceIndex++;
                
                stackIndex = toStack(stack, stackIndex, in);
                
                if(traceIndex == delim.length) {
                    
                    if(checkStack(stack, stackIndex, delim)) {
                        
                        if(includeDelim) ba.write(delim);
                        
                        break;
                        
                    } else {
                        
                        traceIndex = 0;
                        
                        unread(in);
                        
                        continue;
                        
                    }
                }
                
            } else ba.write(in);
        }
        
        return ba.toByteArray();
        
    }
    
    
    /** Read binary data untill a CRLF is found. */

    public byte[] readLine() throws EOFException, IOException {
        
        ByteArrayOutputStream ba = new ByteArrayOutputStream();
        
        int in = 0;
        
        while(true) {
            
            in = read();
            
            if(in == -1) {
                
                if(ba.toByteArray().length == 0) throw new EOFException();
                
                break;
                
            }
            
            if(in == '\r') {
                
                int tmp = read();
                
                if(tmp == '\n') break;
                else {
                    
                    unread(tmp);
                    break;
                    
                }
            }
                
            if(in == '\n') break;
            
            ba.write(in);
            
        }
    
        return ba.toByteArray();
        
    }           
    
    private int toStack(int[] stack, int index, int input) {
        
        if((index + 1) < stack.length) {
            
            stack[++index] = input;
            return index;
            
        } else {
            
            stack[0] = input;
            return 0;
            
        }
    }
    
    private boolean checkStack(int[] stack, int index, byte[] delim) {

        if((index + 1) == stack.length) index = 0;
        else index++;
        
        for(int i = 0, j = index; i < delim.length; i++, j++) {
            
            if(j == stack.length) j = 0;
            
            if((stack[j] & 0xFF) != delim[i]) return false;
            
        }
        
        return true;
        
    }
}