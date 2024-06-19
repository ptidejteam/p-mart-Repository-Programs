/*
*  Code: BinaryAttachment.java
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

import net.larsan.email.stream.*;
import net.larsan.email.encoder.*;

/**
* A binary attachment for email bodies. An attachment differs from the binary encoded 
* body only in content disposition: A binary content which is known to the recipient 
* will usually be handled inline the message while an attachment always is considered 
* to be a separate file.<p>
*
* The attachment sets three header fields by default: The "Content-Type" which is set
* to "unknown", the "Content-Transfer-Encoding" which takes it's value from the Encoder
* object which <b>must</b> be provided before attempts to write this class using the 
* <code>write</code> method, and the "Content-Disposition" which is set to "attachment"
* with the parameter "name" set to the attachment name.<p>
*
* Users should provide a correct "Content-Type" field before sending the attachment using
* the <code>setContentType</code> method. If the content type where set to 
* "application/octet-stream" and the file name is "myfile.exe" the header would
* look like this if printed separately with a Base64 encoder:
*
* <pre>
*   Content-Type: application/octet-stream; name="myfile.exe
*   Content-Transfer-Encoding: Base64
*   Content-Disposition: attachment; filename="myfile.exe"
* </pre>
*
* @author Lars J. Nilsson
* @version 1.0 09/10/00
*/

public class BinaryAttachment extends BinaryBody {
    
    // instance data
    private String name;
    
    
    /**
    * Contruct a new attachment with a default base 64 encoder.
    */
    
    public BinaryAttachment(byte[] file, String fileName) {
       this(file, fileName, new Base64Encoder());
    }

    
    /**
    * Contruct a new attachment using a specified encoder.
    */

	public BinaryAttachment(byte[] file, String fileName, Encoder encoder) { 
	   super(file, encoder);
	   
	   this.name = fileName;
	   
	   ParameterField contentField = new ParameterField(new EmailHeaderField("Content-Type", ContentType.UNKNOWN_APPLICATION));
	   contentField.addParameter("name", fileName);
	   header.set(contentField);
	   
	   ParameterField contentDisp = new ParameterField(new EmailHeaderField("Content-Disposition", "attachment"));
	   contentDisp.addParameter("filename", fileName);
	   header.set(contentDisp);
	   
	}

    
    /**
    * Get the current content type.
    */

	public String getContentType() { 
	   if(header.exists("Content-Type")) return (header.get("Content-Type")).getValue();
	   else return new String();
	}

    
    /**
    * Set the attachments content type.
    */

	public void setContentType(String contentType) { 
	   if(header.exists("Content-Type")) (header.get("Content-Type")).setValue(contentType);
	   else {
	       
	       ParameterField contentField = new ParameterField(new EmailHeaderField("Content-Type", contentType));
	       contentField.addParameter("name", name);
	       header.set(contentField);
        }
	}

    
    /**
    * Get the attachment's name.
    */

	public String getAttachmentName() { 
	   return name;
	}

    
    /**
    * Set attachment name. This method will automaticly update the "Content-Type" and
    * the "Content-Disposition" header fields with subtypes "name" and "filename" set
    * to the new name.
    */

	public void setAttachmentName(String name) {
	   
	   this.name = name;
	   
	   if(header.exists("Content-Type")) {
	       
	       HeaderField field = header.get("Content-Type");
	       
	       if(field instanceof ParameterField) ((ParameterField)field).addParameter("name", name);
	       else {
	           
	           ParameterField contentField = new ParameterField(new EmailHeaderField("Content-Type", field.getValue()));
	           contentField.addParameter("name", name);
	           header.set(contentField);
	           
	       }
       }
      
       if(header.exists("Content-Disposition")) {
	       
	       HeaderField field = header.get("Content-Disposition");
	       
	       if(field instanceof ParameterField) ((ParameterField)field).addParameter("filename", name);
	       else {
	           ParameterField dispField = new ParameterField(new EmailHeaderField("Content-Disposition", field.getValue()));
	           dispField.addParameter("filename", name);
	           header.set(dispField);
	       }
       }
    }
}
