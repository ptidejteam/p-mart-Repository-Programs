/*
*  Code: EmailHeader.java
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

package net.larsan.email;

import java.util.Iterator;
import java.io.OutputStream;
import java.io.IOException;

import net.larsan.email.stream.LineBreak;

/**
* This object is a header field which presents it's content fields 
* in a non-significant order. See the documentation of the Header class
* for more information.
*
* @author Lars J. Nilsson
* @version 1.0.1 21/10/2001
*/

public class EmailHeader extends Header {
    
    
    /** Version id. */
    
    static final long serialVersionUID = 63639194631511101L;
    
		
    /**
    * Construct a new Header object.
    */
		
    public EmailHeader() { 
        this(new HeaderField[0]);
    }

    
    /**
    * Contruct a new Header object with the specified header
    * fields.
    */

    public EmailHeader(HeaderField[] fields) {
        super(fields);
    }	
    
    
    /**
    * Get all header fields in a non significant order.
    */

	public HeaderField[] getAll() {
	    HeaderField[] answer = new HeaderField[fields.size()];
	    fields.toArray(answer);
	    return answer;
	}
	
	public Iterator iterator() {
	   return fields.iterator();
    }


    /**
    * Write the fields of this header to an output stream and end with
    * a canonical new line sequence. 
    */
	
    public void write(OutputStream out) throws IOException { 
    
        for(Iterator i = fields.iterator(); i.hasNext();) {
            
           HeaderField field = (HeaderField)i.next();
            
           field.write(out);
           
           out.write(LineBreak.CRLF);
           
           out.flush();
        }
    
        out.write(LineBreak.CRLF);
        
        out.flush();
    }
}
