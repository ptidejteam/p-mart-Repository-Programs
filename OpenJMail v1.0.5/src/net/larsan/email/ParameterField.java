/*
*  Code: ParameterField.java
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

import net.larsan.email.encoder.Q_Encoder;
import net.larsan.email.encoder.Encoder;
import net.larsan.email.stream.LineBreak;

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
* This field can encode the parameter values if provided with an encoder. But it can
* be noted that it is not always preffered. For example, boundaries are usually
* a parameter to the content type field and usually contains illegal characters in most
* encoding for just the reason that the boundary shoiuld be distinguishable from the
* content body. Thus boundaries should as such not be encoded.<p>
*
* This field is not synchronized.
*
* @author Lars J. Nilsson
* @version 1.2 21/10/2001
*/

public class ParameterField extends EmailHeaderField {
    
    /** Version id. */
    
    static final long serialVersionUID = -1657370090534622113L;
    
    
    /** Parameters in the form af a List containing header field objects. */
    
    protected ArrayList parameters;
    
    
    /** 
    * New header field with an encoder. Please make sure you want to encode
    * the parameter values of this field.
    */
    
    public ParameterField(String name, String value, Encoder encoder) {
        super(name, value, encoder);
        parameters = new ArrayList(4);
    }
    
    /**
    * Contruct a new parameter field.
    */
    
    public ParameterField(String name, String value) {
        this(name, value, null);
    }
    
    
    /**
    * Contruct a new parameter field.
    */

	public ParameterField(HeaderField field) { 
	   this(field.getName(), field.getValue());
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
    * Check for parameter.
    */
    
    public boolean containsParameter(String name) {
        
        for(int i = 0; i < parameters.size(); i++) {
            
            if(((HeaderField)parameters.get(i)).getValue().equalsIgnoreCase(name)) {
                
                return true;
                
            }
        }
        
        return false;
        
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
    * Get an Iterator over the parameters as HeaderFields. This Iterator will 
    * be backed upp by the parameter collection and thus reflect
    * changes in the field.
    */
    
    public Iterator parameterIterator() {
        return parameters.iterator();
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
	       
	       if(getEncoder() == null) writer.write(value, 0, value.length());
           else {
            
              char[] chArr = value.toCharArray();
            
              //if(chArr.length == Q_Encoder.encodedLength(chArr)) {
                
                //  writer.write(chArr);
                  
              //} else {
            
                  writer.flush(); 
           
                  getEncoder().encode(out, getEncoder().toByteArray(chArr));
                  
              //}
              
           }
	       
	       writer.write('\"');
	       
	   }
       
       writer.flush();
       
    }
}