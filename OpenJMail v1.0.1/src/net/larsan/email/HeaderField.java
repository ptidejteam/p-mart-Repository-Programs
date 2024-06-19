/*
*  Code: HeaderField.java
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

import java.io.*;

/**
* This class models a header field, a simple holder for
* a name / value pair of Strings. The method is abstract to leave the 
* presentation of the content to specific subclasses.<p>
*
* @author Lars J. Nilsson
* @version 1.0.1 28/10/00
*/

public abstract class HeaderField implements Serializable {
    
    // instance data
	private String name;
	private String value;
	
	
	/**
	* Construct a new header field with a name and a value.
	*/
	
	public HeaderField(String name, String value) {
	    super();
	    
	    this.name = name;
	    this.value = value;
    }

    /** 
    * Get the heaedr field name. 
    */

	public String getName() { 
	   return name;
	}

    /** 
    * Get the header field value. 
    */

	public String getValue() { 
	   return value;
	}

    /**
    * Set the header field name.
    */

	public void setValue(String value) { 
	   this.value = value;
	}

    /**
    * Set the header field value.
    */

	public void setName(String name) { 
	   this.name = name;
	}

    /**
    * Write this field to an output stream. This should be done in a context 
    * specific manner. Ie: in an public email application software this method 
    * must present the header field's content according to the current protocol.
    */
	
    public abstract void write(OutputStream out) throws IOException;

}
