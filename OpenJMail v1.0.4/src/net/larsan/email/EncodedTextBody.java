/*
*  Code: EncodedTextBody.java
*  Originator: Java@Larsan.Net
*  Address: www.larsan.net/java/
*  Contact: webmaster@larsan.net
*
*  Copyright (C) 2000 Lars J. Nilsson
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
import net.larsan.email.encoder.Bit7Encoder;

/**
* This is the base class for all mail bodies containing textual content
* which need to be encoded before transport. The class uses a Encoder object to 
* prepare the body for transport. The Encoder is an abstract skeleton which 
* developers can subclass to provide their own custom encodings.<p>
*
* Current implentations of the mail transport system does not require any actual
* text encodings but uses the "Content-Transfer-Encoding" as a flag indicating the
* content of the message. Those flags are:
*
* <pre>
*   <b>7Bit</b> -   Which means that the message consists of US-ASCII characters and
*                   that no lines exceeds 1000 charcters.
*
*   <b>8Bit</b> -   Means that the message can contain non US-ASCII characters but
*                   that the lines still is under 1000 characters.
*
*   <b>Binary</b> - May contain non US-ASCII characters on lines which can continue
*                   over 1000 charcters.
* </pre>
*
* None of the encoding above does actually encodes the 8 bit data but serves as
* identification of the content for the mail handling agents. For actual encoding 
* of text contents there are two alternatives: The "Quoted-Printable" and the "Base64" 
* encodings. Also note that the "Binary" content flag is illigal in all current
* internet protocolls.<p>
*
* This body part contains a header object which manages the header fields. By
* default the body attempts to set one header field automaticly: The 
* "Content-Transfer-Encoding" field. The value of this field is fetched
* from the Encoder object's <code>getContextName</code> method.<p>
*
* Since the body of this class must be encoded before transport, the encoding
* is by default performed in the <code>write</code> method and an Encoder object
* must be provided before attempts to write the content. If the Encoder object is
* missing the class will convert the text to "US-ASCII" before writing.<p>
*
* Default encoder object is the <code>Bit7Encoder</code> witch will convert
* the characters to "US-ASCII" before sending.
*
* @author Lars J. Nilsson
* @version 1.0.1 21/10/2001
*/

public class EncodedTextBody extends EncodedBody {
    
    
    /** Version id. */
    
    static final long serialVersionUID = 8935112633902109652L;
    
    
    /** Body content. */

    private String body;
    
    // size
    
    private long size = 0;
    
    
    /**
    * Create a new empty text body with the default "US-ASCII" character set.
    */
    
    public EncodedTextBody() { 
        this(new String(), "US-ASCII", new Bit7Encoder());
    }

    
    /**
    * Create a new text body with the default "US-ASCII" character set.
    */
    
    public EncodedTextBody(String body) { 
        this(body, "US-ASCII", new Bit7Encoder());
    }

    
    /**
    * Create a new text body.
    */
    
    public EncodedTextBody(String body, String charset, Encoder encoder) { 
        super(encoder);
        
        this.size = body.getBytes().length;
        this.body = body;
        
        ParameterField contentField = new ParameterField("Content-Type", ContentType.PLAIN_TEXT);
	    contentField.addParameter("charset", charset);
	    header.set(contentField);
    }

    
    /**
    * Set the header field "Content-Type" to contain a parameter character set
    * other than the default "US-ASCII", but the content type will remain the
    * same. Should the content type not be set nothing will happen.
    */

	public void setCharsetParameter(String charset) { 
	   if(header.exists("Content-Type")) {
	       HeaderField field = header.get("Content-Type");
	       if(field instanceof ParameterField) ((ParameterField)field).addParameter("charset", charset);
	       else {
        	   ParameterField contentField = new ParameterField("Content-Type", field.getValue());
        	   contentField.addParameter("charset", charset);
         	   header.set(contentField);
           }
       }
	}

    
    /**
    * Set the header field "Content-Type" to contain a value
    * other than the default "text/plain", but any subtype of an
    * existing field will remain intact. Should the content type field
    * be missing this method will restore it.
    */

	public void setContentType(String type) { 
	   if(header.exists("Content-Type")) (header.get("Content-Type")).setValue(type);
       else {
            HeaderField field = new EmailHeaderField("Content-Type", type);
            header.set(field);     
       }
	}

    
    /**
    * Set the text content of this body.
    */
		
	public void setBody(String body) { 
       this.size = body.getBytes().length;
	   this.body = body;
	}

    
    /**
    * Get text content.
    */
	
	public String getBody() { 
	   return body;
	}
	
	/**
    * Get the string byte length
    */
	
	public long getSize() {
	   return size;
    }

    
    /**
    * Write this body content to an OutputStream. If the Encoder reference is missing this method will 
    * asume the textual content does not need encoding and convert it to 
    * "US-ASCII" before writing it.
    */

	public void write(OutputStream out) throws IOException { 
	    header.write(out);
	    if(encoder != null) {
	        byte[] ch = encoder.toByteArray(body.toCharArray());
	        encoder.encode(out, ch);
	    } else {
	        byte[] ba = body.getBytes("US-ASCII");
	        out.write(ba, 0, ba.length);
	    }
        out.flush();
	}
}
