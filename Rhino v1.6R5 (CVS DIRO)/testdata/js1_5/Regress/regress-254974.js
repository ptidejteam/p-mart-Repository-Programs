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
 * The Original Code is JavaScript Engine testing utilities.
 *
 * The Initial Developer of the Original Code is
 * Mozilla Foundation.
 * Portions created by the Initial Developer are Copyright (C) 2005
 * the Initial Developer. All Rights Reserved.
 *
 * Contributor(s): timeless <timeless@mozdev.org>
 *                 Bob Clary <bob@bclary.com>
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
//-----------------------------------------------------------------------------
// this test originally was only seen if typed into the js shell.
// loading as a script file did not exhibit the problem. 
// this test case may not exercise the problem properly.

var bug = 254974;
var summary = 'all var and arg properties should be JSPROP_SHARED';
var actual = '';
var expect = '';

printBugNumber (bug);
printStatus (summary);

function testfunc(tokens) {
function eek(y) {} /* remove function eek and the code will change its behavior */
    return tokens.split(/\]?(?:\[|$)/).shift();
}
bad=testfunc;
function testfunc(tokens) {
    return tokens.split(/\]?(?:\[|$)/).shift();
}
good=testfunc;

var goodvalue = good("DIV[@id=\"test\"]");
var badvalue = bad("DIV[@id=\"test\"]");

printStatus(goodvalue);
printStatus(badvalue);

expect = goodvalue;
actual = badvalue;
  
reportCompare(expect, actual, summary);
