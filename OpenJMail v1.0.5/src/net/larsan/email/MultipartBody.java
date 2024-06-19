/*
*  Code: MultipartBody.java
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

import java.io.*;
import java.util.*;

import net.larsan.email.stream.*;
import net.larsan.email.encoder.*;

/**
* This abstract class is the base for all multipart message bodies. The multipart
* body can be viewed as a container of other bodies. The bodies generally appear 
* in a List object with the body added first at the top of the document and 
* the body added last at the end. However, since a container multipart body also can
* contain other multipart bodies this does not have to be true. Cetrain multipart
* bodies store their content in a manner where the signifcant order is reversed
* or ignored, so one should have good understanding of the email protocols before
* adding a custom body to a container.<p>
*
* This class and all of it's subclasses requires a boundary string upon creation. 
* The boundary is a String object that is unlikely to appear on it's own in 
* the character content of the message and is not longer than 70 characters. 
* It is also recommended that the boundary contains the character sequence "=_" 
* since this sequence is cannot occur by it self is in content encodings like 
* the Base64 or the Quoted-Printable. A simple boundary might look like this:
*
* <pre>
*   =-=_Simple_boundary_=-=
* </pre>
*
* Boundaries longer than 70 characters will simply be cut at the max length. 
* Implementors also should take care so that boundaries which might be enclosed
* within the multipart bodies of each other, differs correctly. If an alternative 
* body part is located within a mixed multipart they must have different 
* boundaries.<p>
*
* This class requires subclasses not to implement the <code>write</code> method -
* which can be handled since every body must subclass the Body class - but to implements
* a <code>getBodyParts</code> method which is required to return the body's different
* parts in a correct order.
*
* @author Lars J. Nilsson
* @version 1.1.1 21/10/2001
*/

public abstract class MultipartBody extends Body {
    
    /** Version id. */
    
    static final long serialVersionUID = -2864451126275800341L;
    
    
    /** Body part boundary. */
    
    private String boundary;
    
    /**
    * Create a new body part with the specified boundary and a new
    * empty heaedr object.
    */
    
    public MultipartBody(String boundary) {
        this(new EmailHeader(), boundary);
    }

    
    /**
    * Create a new body part with the specified boundary and header.
    */
	
	public MultipartBody(Header header, String boundary) { 
	   super(header);
	
	   this.boundary = checkLength(boundary);
	}

    
    /**
    * Get the boundary string.
    */
	
	public String getBoundary() { 
	   return boundary;
	}

    
    /**
    * Set a new boundary for the body. A boundary cannot exceed 70 characters
    * in which case it will be cut to fit the correct length.
    */ 
	
	public void setBoundary(String boundary) { 
	   this.boundary = checkLength(boundary);
	}

    
    /**
    * Get the culminative byte size of all body parts contained within this
    * body part.
    */
    
    public long getSize() {
        long answer = 0;
        for(Iterator i = getBodyParts(); i.hasNext(); ) {
            answer += ((Body)i.next()).getSize();
        }
        return answer;
    }
    
    
    /**
    * Write the body part to an OutputStream. This method handles the
    * correct boundary syntax and thus subclasses need only to provide a 
    * <code>getBodyParts</code> method to make sure the bodies they contain is
    * written in a significant order according to the subclass rules.
    */
	
	public void write(OutputStream out) throws IOException { 
	   
	   String boundaryLimit = "--";
	   header.write(out);
	   	   
	   OutputStreamWriter writer = new OutputStreamWriter(out, "US-ASCII");
	   
	   for(Iterator i = getBodyParts(); i.hasNext();) {
	       
	       writer.write(boundaryLimit);
	       writer.write(boundary);
	       writer.flush();
	       
	       out.write(LineBreak.CRLF);
	       out.flush();
	       
	       ((Body)i.next()).write(out);
	       
	       out.write(LineBreak.CRLF);
	       out.flush();
	       
	   }

	   writer.write(boundaryLimit);
       writer.write(boundary);
       writer.write(boundaryLimit);
       writer.flush();
       
       out.write(LineBreak.CRLF);
       out.flush();

	}


    /**
    * Get the bodies different parts. Most multipart bodies handles
    * their different parts in a significant order - implementors
    * of this method must make sure the correct order is returned by this
    * method.
    */
	
	public abstract Iterator getBodyParts();
	
	
	/** Make sure the boundary is not to long. */
	
	private String checkLength(String boundary) {
	   if(boundary.length() > 70) return boundary.substring(0, 70);
	   else return boundary;
    }
}
