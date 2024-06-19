/*
*  Code: SubjectField.java
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

import net.larsan.email.encoder.*;

/**
* This class models a dedicated "Subject" header field. The
* class will set the field name to "Subject" and the field value
* to the subject string.<p>
*
* This field supports character encoding through subclassing
* the EncodedHeaderField and using Encoder objects. If you are 
* composing a RFC 822 message your should use a simple HeaderFiled.
* See the EmailHeaderField documentation for more details.<p>
*
* @author Lars J. Nilsson
* @version 1.1 29/03/2001
*/

public class SubjectField extends EmailHeaderField {
    
    /**
	* Construct a new empty subject header field with a supplied encoder.
	*/
    
    public SubjectField(Encoder encoder) {
        this("", encoder);
    }
	
	
	/**
	* Construct a new Subject header field with a supplied encoder.
	*/
	
	public SubjectField(String subject, Encoder encoder) { 
	    super("Subject", subject, encoder);
	}

    
    /** Set subject field value. */
    
    public void setSubject(String subject) {
        setValue(subject);
    }

    
    /** Get the field subject. */
	
	public String getSubject() {
	    return super.getValue();   
	}
}
