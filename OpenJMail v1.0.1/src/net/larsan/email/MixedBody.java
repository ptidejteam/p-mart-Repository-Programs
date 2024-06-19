/*
*  Code: MixedBody.java
*  Originator: Java@Larsan.Net
*  Address: www.larsan.net/java/
*  Contact: webmaster@larsan.net
*
*  Copyright (C) 2001 Lars J. Nilsson
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
* This class represents a mixed multipart body according to the MIME message 
* standard. The different parts contained in this body container is ordered by falling 
* precedence. <p>
*
* This class requires a boundary string upon creation. For a detailed discussion on
* the subject of boundaries, please refer to the MultipartBody class documentation.<p>
*
* This class will set one header field by default, the "Content-Type" header which will
* be set to "multipart/mixed". This header field should not be changed since it identifies
* the body's content type to the environment.<p>
*
* This class is not synchronized.
*
* @author Lars J. Nilsson
* @version 1.1 29/03/2001
*/

public class MixedBody extends MultipartBody {
    
    /** Body part container */
    
    protected ArrayList list;
    
    /**
    * Create a new MixedBody part. The boundary cannot exceed 70 characters
    * and must not be repeted in bodies contained within this body. The content 
    * type field of this body header will be set to "multipart/mixed".
    */
    
	public MixedBody(String boundary) { 
	   super(boundary);
	   
	   list = new ArrayList();
	   
	   ParameterField contentField = new ParameterField(new EmailHeaderField("Content-Type", ContentType.MIXED_MULTIPART));
	   contentField.addParameter("boundary", super.getBoundary());
	   header.set(contentField); 
	   
	}

    
    /**
    * Add a body part at the start of this container. It will push every
    * other body part one step down in precedence.
    */
	
	public void addHighOrderBodyPart(Body body) { 
	   list.add(0, body);
	}

    
    /**
    * Add one body part at the end of this container.
    */
	
	public void addLowOrderBodyPart(Body body) { 
	   list.add(body);
	}

    
    /**
    * Insert a body part at a specified position. The body part currently
    * at the position and every part below it will be pushed one step down 
    * in precedence.
    */
	
	public void addBodyPartAt(int position, Body body) { 
	   list.add(position, body);
	}

    
    /**
    * Remove a body part at a specific position. Every body part below
    * it will gain one step in precedence.
    */

    public void removeBodyPartAt(int position) {
       list.remove(position);
    }

    
    /**
    * Get the number of body parts in this container.
    */
	
	public int length() { 
	   return list.size();
	}

    
    /**
    * Get the body parts in falling order of precedence. Changes made
    * to the Iterator returned by this method will reflect to the
    * content of this object.
    */
	
	public Iterator getBodyParts() { 
	   return list.iterator();
	}
}