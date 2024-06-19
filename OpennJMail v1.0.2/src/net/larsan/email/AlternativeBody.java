/*
*  Code: AlternativeBody.java
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

import java.util.ArrayList;
import java.util.Iterator;

/**
* This class represents an alternative multipart body according to the MIME message 
* standard. The alternative body part is presumed to contain different body parts all
* displaying the same information but in different formats. The most common
* example of this is the client who wants to send an HTML message but also provide
* a plain text version of the same messagefor those who may not be able to display 
* HTML correctly. That client can use an alternative body part with both an HTML body 
* part and a text body part in it. The different bodies contained within the 
* alternative body part is interpreted to be presented in ascending order of presedence. 
* The client would probably like to display the HTML version first if possible and 
* if that one failed he would want the text - then the HTML version will appear after 
* the text version in the message code.<p>
*
* See the MultipartBody documentation for a discussion of boundaries.<p>
*
* This class will set one header field by default, the "Content-Type" header which will
* be set to "multipart/alternative". This header field identifies the type of body is a
* MIME context and should not be changed,<p>
*
* This class is not synchronized.
*
* @author Lars J. Nilsson
* @version 1.1 29/03/2001
*/

public class AlternativeBody extends MixedBody {
    
    /** Version id. */
    
    static final long serialVersionUID = 2878883002962542462L;
    
    /**
    * Create a new AlternativeBody part. The boundary cannot exceed 70 characters
    * and must unique for this message. The content type field of this body header 
    * will be set to "multipart/alternative".
    */
    
	public AlternativeBody(String boundary) { 
	   super(boundary);
	   
	   ParameterField contentField = new ParameterField("Content-Type", ContentType.ALTERNATIVE_MULTIPART);
	   contentField.addParameter("boundary", getBoundary());
	   header.set(contentField); 
	   
	}

    
    /**
    * Get the body parts in ascending order of precedence. The Iterator returned by
    * this method will not direct reflect changes in the body container.
    */
	
	public Iterator getBodyParts() { 
	
	   ArrayList tmp = new ArrayList(list.size());

	   for(int i = 0; i < list.size(); i++) {
	       tmp.add((Body)list.get((list.size() - 1) - i));
	   }
	   
	   return tmp.iterator();
	}
}