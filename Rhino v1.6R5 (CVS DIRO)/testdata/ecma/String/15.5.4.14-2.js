/* -*- Mode: C++; tab-width: 2; indent-tabs-mode: nil; c-basic-offset: 2 -*- */
/* ***** BEGIN LICENSE BLOCK *****
 * Version: MPL 1.1/GPL 2.0/LGPL 2.1
 *
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 *
 * The Original Code is Mozilla Communicator client code, released
 * March 31, 1998.
 *
 * The Initial Developer of the Original Code is
 * Netscape Communications Corporation.
 * Portions created by the Initial Developer are Copyright (C) 1998
 * the Initial Developer. All Rights Reserved.
 *
 * Contributor(s):
 *
 * Alternatively, the contents of this file may be used under the terms of
 * either the GNU General Public License Version 2 or later (the "GPL"), or
 * the GNU Lesser General Public License Version 2.1 or later (the "LGPL"),
 * in which case the provisions of the GPL or the LGPL are applicable instead
 * of those above. If you wish to allow use of your version of this file only
 * under the terms of either the GPL or the LGPL, and not to allow others to
 * use your version of this file under the terms of the MPL, indicate your
 * decision by deleting the provisions above and replace them with the notice
 * and other provisions required by the GPL or the LGPL. If you do not delete
 * the provisions above, a recipient may use your version of this file under
 * the terms of any one of the MPL, the GPL or the LGPL.
 *
 * ***** END LICENSE BLOCK ***** */

/**
   File Name:          15.5.4.8-2.js
   ECMA Section:       15.5.4.8 String.prototype.split( separator )
   Description:

   Returns an Array object into which substrings of the result of converting
   this object to a string have been stored. The substrings are determined by
   searching from left to right for occurrences of the given separator; these
   occurrences are not part of any substring in the returned array, but serve
   to divide up this string value. The separator may be a string of any length.

   As a special case, if the separator is the empty string, the string is split
   up into individual characters; the length of the result array equals the
   length of the string, and each substring contains one character.

   If the separator is not supplied, then the result array contains just one
   string, which is the string.

   When the split method is called with one argument separator, the following steps are taken:

   1.   Call ToString, giving it the this value as its argument.
   2.   Create a new Array object of length 0 and call it A.
   3.   If separator is not supplied, call the [[Put]] method of A with 0 and
   Result(1) as arguments, and then return A.
   4.   Call ToString(separator).
   5.   Compute the number of characters in Result(1).
   6.   Compute the number of characters in the string that is Result(4).
   7.   Let p be 0.
   8.   If Result(6) is zero (the separator string is empty), go to step 17.
   9.   Compute the smallest possible integer k not smaller than p such that
   k+Result(6) is not greater than Result(5), and for all nonnegative
   integers j less than Result(6), the character at position k+j of
   Result(1) is the same as the character at position j of Result(2);
   but if there is no such integer k, then go to step 14.
   10.   Compute a string value equal to the substring of Result(1), consisting
   of the characters at positions p through k1, inclusive.
   11.   Call the [[Put]] method of A with A.length and Result(10) as arguments.
   12.   Let p be k+Result(6).
   13.   Go to step 9.
   14.   Compute a string value equal to the substring of Result(1), consisting
   of the characters from position p to the end of Result(1).
   15.   Call the [[Put]] method of A with A.length and Result(14) as arguments.
   16.   Return A.
   17.   If p equals Result(5), return A.
   18.   Compute a string value equal to the substring of Result(1), consisting of
   the single character at position p.
   19.   Call the [[Put]] method of A with A.length and Result(18) as arguments.
   20.   Increase p by 1.
   21.   Go to step 17.

   Note that the split function is intentionally generic; it does not require that its this value be a String
   object. Therefore it can be transferred to other kinds of objects for use as a method.

   Author:             christine@netscape.com
   Date:               12 november 1997
*/

var SECTION = "15.5.4.8-2";
var VERSION = "ECMA_1";
startTest();
var TITLE   = "String.prototype.split";

writeHeaderToLog( SECTION + " "+ TITLE);

// case where separator is the empty string.

var TEST_STRING = "this is a string object";

new TestCase(   SECTION,
		"var s = new String( "+ TEST_STRING +" ); s.split('').length",
		TEST_STRING.length,
		eval("var s = new String( TEST_STRING ); s.split('').length") );

for ( var i = 0; i < TEST_STRING.length; i++ ) {

  new TestCase(   SECTION,
		  "var s = new String( "+TEST_STRING+" ); s.split('')["+i+"]",
		  TEST_STRING.charAt(i),
		  eval("var s = new String( TEST_STRING ); s.split('')["+i+"]") );
}

// case where the value of the separator is undefined.  in this case. the value of the separator
// should be ToString( separator ), or "undefined".

var TEST_STRING = "thisundefinedisundefinedaundefinedstringundefinedobject";
var EXPECT_STRING = new Array( "this", "is", "a", "string", "object" );

new TestCase(   SECTION,
		"var s = new String( "+ TEST_STRING +" ); s.split(void 0).length",
		EXPECT_STRING.length,
		eval("var s = new String( TEST_STRING ); s.split(void 0).length") );

for ( var i = 0; i < EXPECT_STRING.length; i++ ) {
  new TestCase(   SECTION,
		  "var s = new String( "+TEST_STRING+" ); s.split(void 0)["+i+"]",
		  EXPECT_STRING[i],
		  eval("var s = new String( TEST_STRING ); s.split(void 0)["+i+"]") );
}

// case where the value of the separator is null.  in this case the value of the separator is "null".
TEST_STRING = "thisnullisnullanullstringnullobject";
var EXPECT_STRING = new Array( "this", "is", "a", "string", "object" );

new TestCase(   SECTION,
		"var s = new String( "+ TEST_STRING +" ); s.split(null).length",
		EXPECT_STRING.length,
		eval("var s = new String( TEST_STRING ); s.split(null).length") );

for ( var i = 0; i < EXPECT_STRING.length; i++ ) {
  new TestCase(   SECTION,
		  "var s = new String( "+TEST_STRING+" ); s.split(null)["+i+"]",
		  EXPECT_STRING[i],
		  eval("var s = new String( TEST_STRING ); s.split(null)["+i+"]") );
}

// case where the value of the separator is a boolean.
TEST_STRING = "thistrueistrueatruestringtrueobject";
var EXPECT_STRING = new Array( "this", "is", "a", "string", "object" );

new TestCase(   SECTION,
		"var s = new String( "+ TEST_STRING +" ); s.split(true).length",
		EXPECT_STRING.length,
		eval("var s = new String( TEST_STRING ); s.split(true).length") );

for ( var i = 0; i < EXPECT_STRING.length; i++ ) {
  new TestCase(   SECTION,
		  "var s = new String( "+TEST_STRING+" ); s.split(true)["+i+"]",
		  EXPECT_STRING[i],
		  eval("var s = new String( TEST_STRING ); s.split(true)["+i+"]") );
}

// case where the value of the separator is a number
TEST_STRING = "this123is123a123string123object";
var EXPECT_STRING = new Array( "this", "is", "a", "string", "object" );

new TestCase(   SECTION,
		"var s = new String( "+ TEST_STRING +" ); s.split(123).length",
		EXPECT_STRING.length,
		eval("var s = new String( TEST_STRING ); s.split(123).length") );

for ( var i = 0; i < EXPECT_STRING.length; i++ ) {
  new TestCase(   SECTION,
		  "var s = new String( "+TEST_STRING+" ); s.split(123)["+i+"]",
		  EXPECT_STRING[i],
		  eval("var s = new String( TEST_STRING ); s.split(123)["+i+"]") );
}


// case where the value of the separator is a number
TEST_STRING = "this123is123a123string123object";
var EXPECT_STRING = new Array( "this", "is", "a", "string", "object" );

new TestCase(   SECTION,
		"var s = new String( "+ TEST_STRING +" ); s.split(123).length",
		EXPECT_STRING.length,
		eval("var s = new String( TEST_STRING ); s.split(123).length") );

for ( var i = 0; i < EXPECT_STRING.length; i++ ) {
  new TestCase(   SECTION,
		  "var s = new String( "+TEST_STRING+" ); s.split(123)["+i+"]",
		  EXPECT_STRING[i],
		  eval("var s = new String( TEST_STRING ); s.split(123)["+i+"]") );
}

// case where the separator is not in the string
TEST_STRING = "this is a string";
EXPECT_STRING = new Array( "this is a string" );

new TestCase(   SECTION,
		"var s = new String( " + TEST_STRING + " ); s.split(':').length",
		1,
		eval("var s = new String( TEST_STRING ); s.split(':').length") );

new TestCase(   SECTION,
		"var s = new String( " + TEST_STRING + " ); s.split(':')[0]",
		TEST_STRING,
		eval("var s = new String( TEST_STRING ); s.split(':')[0]") );

// case where part but not all of separator is in the string.
TEST_STRING = "this is a string";
EXPECT_STRING = new Array( "this is a string" );
new TestCase(   SECTION,
		"var s = new String( " + TEST_STRING + " ); s.split('strings').length",
		1,
		eval("var s = new String( TEST_STRING ); s.split('strings').length") );

new TestCase(   SECTION,
		"var s = new String( " + TEST_STRING + " ); s.split('strings')[0]",
		TEST_STRING,
		eval("var s = new String( TEST_STRING ); s.split('strings')[0]") );

// case where the separator is at the end of the string
TEST_STRING = "this is a string";
EXPECT_STRING = new Array( "this is a " );
new TestCase(   SECTION,
		"var s = new String( " + TEST_STRING + " ); s.split('string').length",
		2,
		eval("var s = new String( TEST_STRING ); s.split('string').length") );

for ( var i = 0; i < EXPECT_STRING.length; i++ ) {
  new TestCase(   SECTION,
		  "var s = new String( "+TEST_STRING+" ); s.split('string')["+i+"]",
		  EXPECT_STRING[i],
		  eval("var s = new String( TEST_STRING ); s.split('string')["+i+"]") );
}

test();
