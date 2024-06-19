/*
*  Code: ContentType.java
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

/**
* A simple interface that holds static identifiers for the different MIME content types - including 
* their subtypes.
*
* @author Lars J. Nilsson
* @version 1.0.1 29/03/2001
*/

public interface ContentType {

 /** Recipient type text/plain */

 public static final String PLAIN_TEXT = "text/plain";

 /** Content type text/html */

 public static final String HTML = "text/html";

 /** Content type image/gif */

 public static final String GIF_IMAGE = "image/gif";

 /** Content type image/jpeg */

 public static final String JPEG_IMAGE = "image/jpeg";

 /** Content type  image/bmp */

 public static final String BMP_IMAGE = "image/bmp";

 /** Content type  audio/basic */

 public static final String BASIC_AUDIO = "audio/basic";

 /** Content type video/mpeg */

 public static final String MPEG_VIDEO = "video/mpeg";

 /** Content type application/postscript */

 public static final String POSTSCRIPT_APPLICATION = "application/postscript";

 /** Content type application/octet-stream */

 public static final String OCTETSTREAM_APPLICATION = "application/octet-stream";
 
  /** Content type application/x-unknown-content */

 public static final String UNKNOWN_APPLICATION = "application/x-unknown-content";

 /** Content type multipart/mixed */

 public static final String MIXED_MULTIPART = "multipart/mixed";

 /** Content type multipart/alternative */

 public static final String ALTERNATIVE_MULTIPART = "multipart/alternative";

 /** Content type multipart/digest */

 public static final String DIGEST_MULTIPART = "multipart/digest";

 /** Content type multipart/parallel */

 public static final String PARALLEL_MULTIPART = "multipart/parallel";

 /** Content type message/rfc822 */

 public static final String RFC822_MESSAGE = "message/rfc822";

 /** Content type message/partial */

 public static final String PARTIAL_MESSAGE = "message/partial";

 /** Content type message/external-body */

 public static final String EXTERNALBODY_MESSAGE = "message/external-body";
 
}