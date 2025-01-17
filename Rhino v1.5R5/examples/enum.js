/* -*- Mode: java; tab-width: 8; indent-tabs-mode: nil; c-basic-offset: 4 -*-
 *
 * The contents of this file are subject to the Netscape Public
 * License Version 1.1 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of
 * the License at http://www.mozilla.org/NPL/
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
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
 * Patrick Beard
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

/*
	enum.js

	Implementing the interface java.util.Enumeration using the new syntax.
	Note that this syntax is experimental only, and hasn't been approved
	by ECMA.
	The same functionality can be had without the new syntax using the
	uglier syntax:

		var elements = new JavaAdapter(java.util.Enumeration, {
						index: 0, elements: array,
						hasMoreElements: function ...
						nextElement: function ...
		});

	by Patrick C. Beard.
 */

// an array to enumerate.
var array = [0, 1, 2];

// create an array enumeration.
var elements = new java.util.Enumeration() {
	index: 0, elements: array,
	hasMoreElements: function() {
		return (this.index < this.elements.length);
	},
	nextElement: function() {
		return this.elements[this.index++];
	}
};

// now print out the array by enumerating through the Enumeration
while (elements.hasMoreElements())
	print(elements.nextElement());
