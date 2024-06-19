/*
*  Code: HTMLBody.java
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

import net.larsan.email.encoder.Encoder;

/**
* A class for HTML body parts. The HTML body part is a direct subclass
* of the encoded text body and only differs in the "Content-Type" value which
* is set to "text/html" instead of "text/plain".
*
* @author Lars J. Nilsson
* @version 1.0 08/10/00
*/

public class HTMLBody extends EncodedTextBody {
	
	/**
	* Construct a new empty HTML message body with default charset "US-ASCII".
	*/
	
	public HTMLBody() { 
	   this("", "US-ASCII", null);
	}

	/**
	* Construct a new HTML message body with default charset "US-ASCII".
	*/

	public HTMLBody(String body) {
	   this(body, "US-ASCII", null);
    }

	/**
	* Construct a new HTML message body.
	*/

	public HTMLBody(String body, String charset) {
	   this(body, charset, null);
    }

	/**
	* Construct a new HTML message body.
	*/

	public HTMLBody(String body, String charset, Encoder encoder) { 
	   super(body, charset, encoder);
	   
	   // set the content type header field
	   
	   ParameterField contentField = new ParameterField(new EmailHeaderField("Content-Type", ContentType.HTML));
	   contentField.addParameter("charset", charset);
	   
	   header.set(contentField);
	
	}
}