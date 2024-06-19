/*
*  Code: Body.java
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

package net.larsan.email;

import java.io.OutputStream;
import java.io.IOException;
import java.io.Serializable;

/**
* This is the abstract base for a message body part. A body typically contain
* a header followed by a body. The header can contain an arbitrary number of lines
* depending on the body content. The body can be one of the following: 1) A 
* un-encoded content body (usually text); 2) An encoded content body; 3) A multipart body container or; 
* 4) A email message container body. This diversity also reflects in this class direct
* subclasses.
*
* @author Lars J. Nilsson
* @version 1.0 08/10/00
*/

public abstract class Body implements Serializable {
    
    
    /** Version id. */
    
    static final long serialVersionUID = -8075490101207094146L;
    
  
    /** Header object containing header fields. */
    
	protected Header header;

	
	/**
	* Construct a new body part with a specified header. A body must contain
	* a header object, even if one is not needed.
	*/

    public Body(Header header) { 
        this.header = header;
    }

    
    /**
    * Get the body part header object.
    */

	public Header getHeader() { 
	   return header;
	}

    
    /**
    * Set the body part header object.
    */

	public void setHeader(Header header) { 
	   this.header = header;
	}
	
	
	/**
    * Get the binary size of this body. This size should not include any encodings
    * but be reported as the content would be represented on the current platform. Should
    * a body not be able to report its size it should return 0.
    */
    
    public abstract long getSize();

    
    /**
    * Abstract method for writing the body content to an output stream. This should
    * be done in a context specific manner. Ie: In an public email application software
    * this method must present the body's content according to the current protocol.
    */

	public abstract void write(OutputStream out) throws IOException;
	
}
