/* -*- Mode: java; tab-width: 8; indent-tabs-mode: nil; c-basic-offset: 4 -*-
 *
 * The contents of this file are subject to the Netscape Public
 * License Version 1.1 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of
 * the License at http://www.mozilla.org/NPL/
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express oqr
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 * The Original Code is Rhino code, released
 * May 6, 1999.
 *
 * The Initial Developer of the Original Code is Netscape
 * Communications Corporation.  Portions created by Netscape are
 * Copyright (C) 1997-1999 Netscape Communications Corporation. All
 * Rights Reserved.
 *
 * Contributor(s):
 * Norris Boyd
 *
 * Alternatively, the contents of this file may be used under the
 * terms of the GNU Public License (the "GPL"), in which case the
 * provisions of the GPL are applicable instead of those above.
 * If you wish to allow use of your version of this file only
 * under the terms of the GPL and not to allow others to use your
 * version of this file under the NPL, indicate your decision by
 * deleting the provisions above and replace them with the notice
 * and other provisions required by the GPL.  If you do not delete
 * the provisions above, a recipient may use your version of this
 * file under either the NPL or the GPL.
 */

/**
 * liveConnect.js: a simple demonstration of JavaScript-to-Java connectivity
 */

// Create a new StringBuffer. Note that the class name must be fully qualified
// by its package. Packages other than "java" must start with "Packages", i.e., 
// "Packages.javax.servlet...".
var sb = new java.lang.StringBuffer();

// Now add some stuff to the buffer.
sb.append("hi, mom");
sb.append(3);	// this will add "3.0" to the buffer since all JS numbers
		// are doubles by default
sb.append(true);

// Now print it out. (The toString() method of sb is automatically called
// to convert the buffer to a string.)
// Should print "hi, mom3.0true".
print(sb);	
