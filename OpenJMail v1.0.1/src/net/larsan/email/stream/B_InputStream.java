/*
*  Code: B_InputStream.java
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

package net.larsan.email.stream;

import java.io.*;

/**
* This class provides an InputStream for reading "b" encoded data in mail
* headers. The "b" encoding is a base 64 encoding so for more details, please refer
* to the Base64OutputStream documentation for more details.
*
* @author Lars J. Nilsson
* @version 1.0 23/10/00
*/

public class B_InputStream extends Base64InputStream {
    
    /**
    * Create a new "b" decoding input stream.
    */
	
	public B_InputStream(InputStream in) {
	    super(in);
	}
}