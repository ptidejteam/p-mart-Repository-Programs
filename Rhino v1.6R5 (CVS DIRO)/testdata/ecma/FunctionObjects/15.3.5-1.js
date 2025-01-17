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
   File Name:          15.3.5-1.js
   ECMA Section:       15.3.5 Properties of Function Instances
   new Function(p1, p2, ..., pn, body )

   Description:

   15.3.5.1 length

   The value of the length property is usually an integer that indicates
   the "typical" number of arguments expected by the function. However,
   the language permits the function to be invoked with some other number
   of arguments. The behavior of a function when invoked on a number of
   arguments other than the number specified by its length property depends
   on the function.

   15.3.5.2 prototype
   The value of the prototype property is used to initialize the internal [[
   Prototype]] property of a newly created object before the Function object
   is invoked as a constructor for that newly created object.

   15.3.5.3 arguments

   The value of the arguments property is normally null if there is no
   outstanding invocation of the function in progress (that is, the function has been called
   but has not yet returned). When a non-internal Function object (15.3.2.1) is invoked, its
   arguments property is "dynamically bound" to a newly created object that contains the
   arguments on which it was invoked (see 10.1.6 and 10.1.8). Note that the use of this
   property is discouraged; it is provided principally for compatibility with existing old code.

   Author:             christine@netscape.com
   Date:               28 october 1997

*/

var SECTION = "15.3.5-1";
var VERSION = "ECMA_1";
startTest();
var TITLE   = "Properties of Function Instances";

writeHeaderToLog( SECTION + " "+TITLE);

var args = "";

for ( var i = 0; i < 2000; i++ ) {
  args += "arg"+i;
  if ( i != 1999 ) {
    args += ",";
  }
}

var s = "";

for ( var i = 0; i < 2000; i++ ) {
  s += ".0005";
  if ( i != 1999 ) {
    s += ",";
  }
}

MyFunc = new Function( args, "var r=0; for (var i = 0; i < MyFunc.length; i++ ) { if ( eval('arg'+i) == void 0) break; else r += eval('arg'+i); }; return r");
MyObject = new Function( args, "for (var i = 0; i < MyFunc.length; i++ ) { if ( eval('arg'+i) == void 0) break; eval('this.arg'+i +'=arg'+i); };");


new TestCase( SECTION, "MyFunc.length",                       2000,         MyFunc.length );
new TestCase( SECTION, "var MY_OB = eval('MyFunc(s)')",       1,            eval("var MY_OB = MyFunc("+s+"); MY_OB") );
new TestCase( SECTION, "MyFunc.prototype.toString()",       "[object Object]",  MyFunc.prototype.toString() );
new TestCase( SECTION, "typeof MyFunc.prototype",           "object",           typeof MyFunc.prototype );


new TestCase( SECTION, "MyObject.length",                       2000,         MyObject.length );

new TestCase( SECTION, "FUN1 = new Function( 'a','b','c', 'return FUN1.length' ); FUN1.length",     3, eval("FUN1 = new Function( 'a','b','c', 'return FUN1.length' ); FUN1.length") );
new TestCase( SECTION, "FUN1 = new Function( 'a','b','c', 'return FUN1.length' ); FUN1()",          3, eval("FUN1 = new Function( 'a','b','c', 'return FUN1.length' ); FUN1()") );
new TestCase( SECTION, "FUN1 = new Function( 'a','b','c', 'return FUN1.length' ); FUN1(1,2,3,4,5)", 3, eval("FUN1 = new Function( 'a','b','c', 'return FUN1.length' ); FUN1(1,2,3,4,5)") );

test();
