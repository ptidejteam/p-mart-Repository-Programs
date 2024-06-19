/*
*  Code: ParallelBody.java
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

package net.larsan.email;

import java.util.*;

/**
* This class represents a parallel multipart body according to the MIME message 
* standard. The parallel body part contains different parts of content that is considered
* to be of equal precedence and should be displayed parallel. Not many client handles
* this body part and it is not widely used.<p>
*
* This class requires a boundary string upon creation. See the documentation of the 
* MultipartBody class for more information.<p>
*
* This class will set one header field by default, the "Content-Type" header which will
* be set to "multipart/parallel". This header field should not be changed in most cases.<p>
*
* This body part is not synchronized.
*
* @author Lars J. Nilsson
* @version 1.1 29/03/2001
*/

public class ParallelBody extends MultipartBody {
    
    // instance data
    private ArrayList list;
    
    
    /**
    * Create a new parallel body part. The content type header field
    * of this body will be set to "multipart/parallel".
    */
    
	public ParallelBody(String boundary) { 
	   super(boundary);
	   list = new ArrayList();
	   
	   ParameterField contentField = new ParameterField(new EmailHeaderField("Content-Type", ContentType.PARALLEL_MULTIPART));
	   contentField.addParameter("boundary", getBoundary());
	   header.set(contentField); 
	   
	}

    
    /**
    * Add a body to this container.
    */
	
	public void addBodyPart(Body body) { 
	   list.add(body);
	}
	
    
    /**
    * Get the number of body parts in this container.
    */
	
	public int length() { 
	   return list.size();
	}
	
    /**
    * Get the body parts. Order of precedence is not significant. This
    * iterator reflects the bodies contained within this multipart body,
    * changes made through the iterator will affect the content of this
    * object.
    */
	
	public Iterator getBodyParts() { 
	   return list.iterator();
	}
}