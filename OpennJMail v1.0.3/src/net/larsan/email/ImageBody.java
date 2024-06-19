/*
*  Code: ImageBody.java
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

import net.larsan.email.encoder.*;

/**
* This class models an inline image body part. The image will be encoded
* using the base 64 encoding model. The must common content types (ie: image types) 
* can be found in the public fields of this class for easy access.
*
* @author Lars J. Nilsson
* @version 1.0.2 21/10/2001
*/

public class ImageBody extends BinaryBody {


    /** Version id. */
    
    static final long serialVersionUID = -2606063166288364350L;

    
     /** Content type "image/gif" identifier. */

    public static final String GIF_IMAGE = "image/gif";


    /** Content type "image/jpeg" identifier. */

    public static final String JPEG_IMAGE = "image/jpeg";


    /** Content type "image/bmp" identifier. */

    public static final String BMP_IMAGE = "image/bmp";
    
    
    /** Image name. */
    
    private String name;
    
    
    /**
    * Create a new image body with an image name. The image body header fields
    * will be encoded using "Q" encoding in the ISO-8859-1 character set.
    */
    
    public ImageBody(String contentType, byte[] image, String name) {
        this(contentType, image, name, new Q_Encoder("ISO-8859-1"));
    }
    
    
    /**
    * Create a new image body with an image name and a body header field encoder
    */
    
    public ImageBody(String contentType, byte[] image, String name, Encoder headerEncoder) {
        super(image, new Base64Encoder());
        
        this.name = name;
        
	    if(name.length() == 0) header.set(new EmailHeaderField("Content-Type", contentType));
	    else {
	       
	       ParameterField contentField = new ParameterField("Content-Type", contentType, headerEncoder);
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