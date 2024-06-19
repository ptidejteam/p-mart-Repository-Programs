/*
*  Code: Header.java
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

import java.util.ArrayList;
import java.util.Iterator;

/**
* This abstract class is a container for header fields and makes up the header object
* for messages and body parts. The class organizes the fields by their name and
* provides the ordinary number of methods to remove, get and add fields. There are
* a two things to consider when extending this class:<p>
*
* 1) The order of the fields stored in a header object is not significant. That
* means that agents that requires a significant order of the fields must parse that
* order themselves in the <code>getAll</code> method.<br><br>
*
* 2) The header object can contain multiple fields with the same name. This is not
* permitted by most message protocolls but is frequently used for transport
* information. Most implementations should only use the <code>set</code> method
* to set a header field since this method checkes for duplicated field names.<p>
*
* This class and it's fields are not synchronized.<p>
*
* @author Lars J. Nilsson
* @version 1.3 31/08/2002
*/

public abstract class Header implements Serializable {
    
    
    /** Version id. */
    
    static final long serialVersionUID = -4351584874146170881L;
    
    
    /** Header field object list. */
    
	protected ArrayList fields;
		
    
    /**
    * Construct a new empty header object.
    */
		
    public Header() { 
        this(new HeaderField[0]);
    }

    
    /**
    * Contruct a new header object using the header fields passed
    * in a parameter array.
    */

    public Header(HeaderField[] fields) {
        if(fields.length > 0) {
            this.fields = new ArrayList(fields.length);
            System.arraycopy(fields, 0, this.fields, 0, fields.length);
        } else this.fields = new ArrayList(10);
    }	
    
    
    /**
    * Get a header field by name. If the field name is mapped to multiple
    * field objects this method will return the first occurence of the field. The
    * field search is case insensitive. This method will return null if no field
    * name match is found.
    */

	public HeaderField get(String fieldName) { 
	   HeaderField answer = null;
	   for(Iterator i = fields.iterator(); i.hasNext();) {
	       HeaderField field = (HeaderField)i.next();
	       if(field.getName().equalsIgnoreCase(fieldName)) {
	           answer = field;
	           break;
	       }
        }
        return answer;
	}


    /**
    * Get the number of header fields contianed in this header.
    */
    
    public int length() {
        return fields.size();
    }


    /**
    * Get all header fields mapped to a field name. If there is no
    * fields mapped to the parameter name an empty array will be
    * returned. Field search is case insensitive. This method returns
    * an empty array if there is no fields mapped to this name.
    */
	
	public HeaderField[] getAll(String fieldName) { 
	
	   // load fields into a temporary list
	   ArrayList tmp = new ArrayList(fields.size());

	   for(Iterator i = fields.iterator(); i.hasNext();) {
	       HeaderField field = (HeaderField)i.next();
	       if(field.getName().equalsIgnoreCase(fieldName)) tmp.add(field);
	   }

       HeaderField[] answer = new HeaderField[tmp.size()];
       tmp.toArray(answer);
       return answer;
	}

    
    /**
    * Set unique header field. This method will make sure that no multiple
    * fields exist with the same name. Existing fields currently mapped to the
    * field name will be deleted. Field search is case insensitive.
    */

	public void set(HeaderField field) { 
	   removeAll(field.getName());
       fields.add(field);
	}

    
    /**
    * Add header field. This method adds the given field to the
    * header even if other fields by that name already exists.
    */
	
	public void add(HeaderField field) { 
        fields.add(field);
	}


    /**
    * Remove header field. If multiple occurences exists of this field name, only
    * the first will be removed. Field search is case insensitive.
    */

    public void remove(String fieldName) {
        for(Iterator i = fields.iterator(); i.hasNext();) {
	       HeaderField field = (HeaderField)i.next();
	       if(field.getName().equalsIgnoreCase(fieldName)) {
	           i.remove();
	           break;
	       }
	   }
    }

    /**
    * Remove all fields mapped to this field name. Field search is case insensitive.
    */
                
    public void removeAll(String fieldName) {
        for(Iterator i = fields.iterator(); i.hasNext();) {
	       HeaderField field = (HeaderField)i.next();
	       if(field.getName().equalsIgnoreCase(fieldName)) {
	           i.remove();
	       }
	   }
    }

    /**
    * Check if this field name is exists mapped to a header field object.
    * Field search is case insensitive.
    */
	
	public boolean exists(String fieldName) {
	    for(Iterator i = fields.iterator(); i.hasNext();) {    
	       HeaderField field = (HeaderField)i.next();
	       if(field.getName().equalsIgnoreCase(fieldName)) {
	           return true;
	       }
       }
	   return false;
    }
    
    
    /** 
    * Get all header fields in a significant order. 
    */
    
    public abstract Iterator iterator();

    
    /**
    * Get all header fields in a significant order, if one exists.
    */

	public abstract HeaderField[] getAll(); 

    
    /**
    * Write the fields of this header to an output stream. This should
    * be done in a context specific manner. Ie: In an public email application software
    * this method must present the header's content according to the current protocol.
    */
	
    public abstract void write(OutputStream out) throws IOException;
    
}