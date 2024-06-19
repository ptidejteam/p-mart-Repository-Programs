/*
*  Code: ImageBody.java
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
* This class model an inline image body part. The image will be encoded
* using the base 64 encoding model. The must common content types (ie: image types) 
* can be found in the public fields of this class for easy access.
*
* @author Lars J. Nilsson
* @version 1.0 10/10/00
*/

public class ImageBody extends BinaryBody {
    
     /** Content type "image/gif". */

    public static final String GIF_IMAGE = "image/gif";

    /** Content type "image/jpeg". */

    public static final String JPEG_IMAGE = "inage/jpeg";

    /** Content type "image/bmp". */

    public static final String BMP_IMAGE = "image/bmp";
    
    
    // image name
    private String name;
    
    
    /**
    * Create a new image body with an image name.
    */
    
    public ImageBody(String contentType, byte[] image, String name) {
        super(image, new Base64Encoder());
        
        this.name = name;
        
	    if(name.length() == 0) header.set(new EmailHeaderField("Content-Type", contentType));
	    else {
	       
	       ParameterField contentField = new ParameterField(new EmailHeaderField("Content-Type", contentType));
	       contentField.addParameter("name", name);
	       header.set(contentField);
        }
    }

    
    /**
    * Create a new image body without an image name.
    */

    public ImageBody(String contentType, byte[] image) {
        this(contentType, image, "");
    }
}