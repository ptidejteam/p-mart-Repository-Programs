/*
*  Code: ParameterField.java
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

import java.io.*;
import java.util.*;

/**
* This is a header field containing multiple subtype parameter name / value pairs. 
* The subtype parameter field is frequently used in multipart email messages and 
* header fields such as the "Content-Type" field.<p>
*
* The subtype parameter name / value pairs is presented by the <code>write</code> method
* separated by a "=" character unlike the main field which uses a colon and a
* space. Parameters pairs are separated by a semi colon. The parameter value is 
* also surrounded by qoutes. A complete field with one paramater takes the form:
*
* <pre>
*   <i>name</i>: <i>value</i>; <i>parameterName</i>="<i>parameterValue</i>"
* </pre>
*
* The Parameter field is created with a header field object containing the field
* name / value pair, and additional parameters are added separately later. Use of
* this class for the "Content-Type" header field, may look like this:
*
* <pre>
*   HeaderField field = new HeaderField("Content-Type", "text/plain");
*   ParameterField full_field = new ParameterField(field);
*   full_field.addParameter("charset", "iso-8859-1");
* </pre>
*
*
* This field does not check for multiple occurences of parameter names. Users will 
* have to make sure they do not duplicate names themselves.<p>
*
* This field only supports US-ASCII characters according to the RFC rules.<p>
*
* This field is not synchronized.
*
* @author Lars J. Nilsson
* @version 1.1 29/03/2001
*/

public class ParameterField extends EmailHeaderField {
    
    /** Parameters in the form af a List containing header field objects. */
    
    protected ArrayList parameters;
    
    
    /**
    * Contruct a new parameter field.
    */

	public ParameterField(HeaderField field) { 
	   super(field.getName(), field.getValue());
	   
	   parameters = new ArrayList();
	}

    
    /**
    * Add a parameter name and value.
    */

	public void addParameter(String name, String value) {
	    parameters.add(new EmailHeaderField(name, value));
    }

    
    /**
    * Get the value linked to the parameter name. Will return an empty string if
    * the parameter name is not mapped to a value. If the parameter is overloaded 
    * and occurs multiple times, the first occurence will be returned. Name
    * search is case insensitive.
    */

    public String getParameterValue(String name) {
        
        String answer = "";
        
        for(int i = 0; i < parameters.size(); i++) {
            
            if(((HeaderField)parameters.get(i)).getName().equalsIgnoreCase(name)) {
                
                answer = ((HeaderField)parameters.get(i)).getValue();
                break;
                
            }
        }
    
        return answer;
    }

    
    /**
    * Remove the parameter with the specified name. If there's many parameters with the
    * same name only the first one will be deleted. Name search is case insensitive.
    */

    public void removeParameter(String name) {
        
        for(int i = 0; i < parameters.size(); i++) {
            
            if(((HeaderField)parameters.get(i)).getValue().equalsIgnoreCase(name)) {
                
                parameters.remove(i);
                break;
                
            }
        }
    }

    /**
    * Get all parameters translated to a header field array.
    */
    
    public HeaderField[] getAllParameters() {
        HeaderField[] answer = new HeaderField[parameters.size()];
        parameters.toArray(answer);
        return answer;
    }

    
    /**
    * Write this field to an output stream.
    */
	
	public void write(OutputStream out) throws IOException { 
	
	   super.write(out);
	   
	   OutputStreamWriter writer = new OutputStreamWriter(out, "US-ASCII");
	   
	   for(int i = 0; i < parameters.size(); i++) {
	       
	       String name = ((HeaderField)parameters.get(i)).getName();
	       String value = ((HeaderField)parameters.get(i)).getValue();
	       
	       writer.write(';');
	       writer.write(' ');
	       writer.write(name, 0, name.length());
	       writer.write('=');
	       writer.write('\"');
	       writer.write(value, 0, value.length());
	       writer.write('\"');
	       
	   }
       
       writer.flush();
       
	}
}