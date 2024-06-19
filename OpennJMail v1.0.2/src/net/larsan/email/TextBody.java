/*
*  Code: TextBody.java
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

import net.larsan.email.encoder.Encoder;

/**
* This is an ordinary unencoded text body part. The constructor can attempt
* to set the named "Content-Type" field in the header object to a parameter header
* field with it's value to "text/plain" and parameter "charset" value current 
* character set. Default character set for this class is "US-ASCII".<p>
*
* <b>Important!</b> Any other character sets than "US-ASCII" or "ISO-8859" and 
* their subsets are discurraged and may possibly throw an UnsupportedEncodingException
* when the <code>write</code> method is called. For other charset an encoding must be
* used - see the encoded body part.
*
* @author Lars J. Nilsson
* @version 1.0.2 21/10/2001
*/

public class TextBody extends Body {
    
    /** Version id. */
    
    static final long serialVersionUID = -1161706748175712101L;
    
    
    /** Body text. */
    
	private String body;
	
	
	/**
	* Contruct a new empty text body with the default character set "US-ASCII".
	*/
	
	public TextBody() { 
	   this(new EmailHeader(), "", "US-ASCII", true);
	}

	
	/**
	* Contruct a text body with the default character set "US-ASCII".
	*/
	
	public TextBody(String body) { 
	   this(new EmailHeader(), body, "US-ASCII", true);
	}

    /**
	* Contruct a text body with a specified character set which will be
	* inserted into the header content type field.
	*/
	
	public TextBody(String body, String charset) { 
	   this(new EmailHeader(), body, charset, true);
	}

    
    /**
    * Contruct a new text body with text content and provided charcter set. The 
    * "setContentType" parameter decides if the constructor will attempt to
    * set the "Content-Type" header field to "text/plain" with the specified
    * character set as "charset" parameter value. The character set must be one 
    * recognised by the Java output stream writer object.
    */

	public TextBody(Header header, String body, String charset, boolean setContentType) { 
	   super(header);
	   
	   if(setContentType) {
	   
    	   ParameterField contentField = new ParameterField(new EmailHeaderField("Content-Type", ContentType.PLAIN_TEXT));
    	   contentField.addParameter("charset", charset);
    	   header.set(contentField);
       }
	   
	   this.body = body;
	   
	}

    
    /**
    * Set the text content of this body.
    */
		
	public void setBody(String body) { 
	   this.body = body;
	}

    
    /**
    * Get text content of this body.
    */
	
	public String getBody() { 
	   return body;
	}


    /**
    * Write this body content to an OutputStream.
    */

	public void write(OutputStream out) throws IOException { 
	
	   String charset = "US-ASCII";
	   
	   if(header.exists("Content-Type")) {
	       
	       HeaderField field = header.get("Content-Type");
	       
	       if(field instanceof ParameterField) {
	           
	           if(((ParameterField)field).getAllParameters().length > 0) charset = ((ParameterField)field).getParameterValue("charset");

	       }
       }
	
	   header.write(out);
	   
	   OutputStreamWriter writer = (charset.length() > 0 ? new OutputStreamWriter(out, charset) : new OutputStreamWriter(out));
	   
	   writer.write(body, 0, body.length());
	   writer.flush();
	   
	   out.flush();
	   
	}
}