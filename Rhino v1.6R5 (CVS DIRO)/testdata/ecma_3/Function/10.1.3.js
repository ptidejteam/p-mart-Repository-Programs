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
 * Netscape Communications Corp.
 * Portions created by the Initial Developer are Copyright (C) 2003
 * the Initial Developer. All Rights Reserved.
 *
 * Contributor(s):
 *   igor@icesoft.no, pschwartau@netscape.com
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
 * ***** END LICENSE BLOCK *****
 *
 *
 * Date:    17 February 2003
 * SUMMARY: Testing access to function name from inside function
 *
 * See http://bugzilla.mozilla.org/show_bug.cgi?id=193555
 *
 */
//-----------------------------------------------------------------------------
var UBound = 0;
var bug = 193555;
var summary = 'Testing access to function name from inside function';
var status = '';
var statusitems = [];
var actual = '';
var actualvalues = [];
var expect= '';
var expectedvalues = [];


// test via function statement
status = inSection(1);
function f() {return f.toString();};
actual = f();
expect = f.toString();
addThis();

// test via function expression
status = inSection(2);
var x = function g() {return g.toString();};
actual = x();
expect = x.toString();
addThis();

// test via eval() outside function
status = inSection(3);
eval ('function a() {return a.toString();}');
actual = a();
expect = a.toString();
addThis();

status = inSection(4);
eval ('var y = function b() {return b.toString();}');
actual = y();
expect = y.toString();
addThis();

// test via eval() inside function
status = inSection(5);
function c() {return eval('c').toString();};
actual = c();
expect = c.toString();
addThis();

status = inSection(6);
var z = function d() {return eval('d').toString();};
actual = z();
expect = z.toString();
addThis();

// test via two evals!
status = inSection(7);
eval('var w = function e() {return eval("e").toString();}');
actual = w();
expect = w.toString();
addThis();



//-----------------------------------------------------------------------------
test();
//-----------------------------------------------------------------------------



function addThis()
{
  statusitems[UBound] = status;
  actualvalues[UBound] = actual;
  expectedvalues[UBound] = expect;
  UBound++;
}


function test()
{
  enterFunc('test');
  printBugNumber(bug);
  printStatus(summary);

  for (var i=0; i<UBound; i++)
  {
    reportCompare(expectedvalues[i], actualvalues[i], statusitems[i]);
  }

  exitFunc ('test');
}
