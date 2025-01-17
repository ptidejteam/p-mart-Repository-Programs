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
   File Name:          proto_4.js
   Section:
   Description:        new PrototypeObject

   This tests Object Hierarchy and Inheritance, as described in the document
   Object Hierarchy and Inheritance in JavaScript, last modified on 12/18/97
   15:19:34 on http://devedge.netscape.com/.  Current URL:
   http://devedge.netscape.com/docs/manuals/communicator/jsobj/contents.htm

   This tests the syntax ObjectName.prototype = new PrototypeObject using the
   Employee example in the document referenced above.

   If you add a property to an object in the prototype chain, instances of
   objects that derive from that prototype should inherit that property, even
   if they were instatiated after the property was added to the prototype object.


   Author:             christine@netscape.com
   Date:               12 november 1997
*/

var SECTION = "proto_3";
var VERSION = "JS1_3";
var TITLE   = "Adding properties to the prototype";

startTest();
writeHeaderToLog( SECTION + " "+ TITLE);

function Employee () {
    this.name = "";
    this.dept = "general";
}
function Manager () {
    this.reports = [];
}
Manager.prototype = new Employee();

function WorkerBee () {
    this.projects = new Array();
}

WorkerBee.prototype = new Employee();

function SalesPerson () {
    this.dept = "sales";
    this.quota = 100;
}
SalesPerson.prototype = new WorkerBee();

function Engineer () {
    this.dept = "engineering";
    this.machine = "";
}
Engineer.prototype = new WorkerBee();

var jim = new Employee();
var terry = new Engineer();
var sean = new SalesPerson();
var wally = new Manager();

Employee.prototype.specialty = "none";

var pat = new Employee();
var leslie = new Engineer();
var bubbles = new SalesPerson();
var furry = new Manager();

Engineer.prototype.specialty = "code";

var chris = new Engineer();


new TestCase( SECTION,
	      "jim = new Employee(); jim.specialty",
	      "none",
	      jim.specialty );

new TestCase( SECTION,
	      "terry = new Engineer(); terry.specialty",
	      "code",
	      terry.specialty );

new TestCase( SECTION,
	      "sean = new SalesPerson(); sean.specialty",
	      "none",
	      sean.specialty );

new TestCase( SECTION,
	      "wally = new Manager(); wally.specialty",
	      "none",
	      wally.specialty );

new TestCase( SECTION,
	      "furry = new Manager(); furry.specialty",
	      "none",
	      furry.specialty );

new TestCase( SECTION,
	      "pat = new Employee(); pat.specialty",
	      "none",
	      pat.specialty );

new TestCase( SECTION,
	      "leslie = new Engineer(); leslie.specialty",
	      "code",
	      leslie.specialty );

new TestCase( SECTION,
	      "bubbles = new SalesPerson(); bubbles.specialty",
	      "none",
	      bubbles.specialty );


new TestCase( SECTION,
	      "chris = new Employee(); chris.specialty",
	      "code",
	      chris.specialty );
test();
