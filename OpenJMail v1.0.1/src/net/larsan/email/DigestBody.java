/*
*  Code: DigestBody.java
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
* This class represents a digest multipart body according to the MIME message 
* standard. The digest body and syntax is equal that of the mixed multipart but
* every part is presumed to be by the content type "message/rfc822". A
* complete multipart digest then might look like this (copied from 
* RFC 1521 part 7.2.4):
*
* <pre>
*   From: Moderator-Address
*   To: Recipient-List
*   MIME-Version: 1.0
*   Subject:  Internet Digest, volume 42
*   Content-Type: multipart/digest; boundary="---- next message ----"
*
*   ------ next message ----
*
*   From: someone-else
*   Subject: my opinion
*
*   ...body goes here ...
*
*   ------ next message ----
*
*   From: someone-else-again
*   Subject: my different opinion
*
*   ... another body goes here...
*
*   ------ next message ------
* </pre>
*
* This class requires a boundary string upon creation. For a discussion of the boundary,
* please refer to the MultipartBody documentation.<p>
*
* This class will set one header field by default, the "Content-Type" header which will
* be set to "multipart/digest". <This header field identifies the content type pf the
* body and should normally not be changed.<p>
*
* At the moment this class does not contain any modifiers for the messages in the digest,
* implementors wishing to use such a functionallity should sublass this class and
* create their own modifiers.
*
* @author Lars J. Nilsson
* @version 1.1 29/03/2001
*/


public class DigestBody extends MultipartBody {
    
    /** List containing the digest's messages. */
    
    protected ArrayList messages;


    /** Contruct a new digest body using the parameter boundary. */
    
	public DigestBody(String boundary) { 
	   super(boundary);
	   
	   ParameterField contentField = new ParameterField(new EmailHeaderField("Content-Type", ContentType.DIGEST_MULTIPART));
	   contentField.addParameter("boundary", getBoundary());
	   header.set(contentField);
	   
	   messages = new ArrayList();
	   
	}

    
    /**
    * Add message to digest.
    */

    public void addMessage(Message msg) {
        messages.add(msg);
    }

    
    /**
    * Get the messages in order of occurence. The iterator returned by this method
    * reflects the messages contained within this multipart body, changes made 
    * through the iterator will affect the content of this object.
    */
	
	public Iterator getBodyParts() { 
	    return messages.iterator();
	}
}