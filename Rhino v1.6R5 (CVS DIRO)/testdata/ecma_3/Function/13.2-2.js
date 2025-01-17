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
 * The Original Code is mozilla.org code.
 *
 * The Initial Developer of the Original Code is
 * Netscape Communications Corporation.
 * Portions created by the Initial Developer are Copyright (C) 1998
 * the Initial Developer. All Rights Reserved.
 *
 * Contributor(s):
 *   pschwartau@netscape.com, rogerl@netscape.com
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
 * Date: 28 May 2001
 *
 * SUMMARY:  Functions are scoped statically, not dynamically
 *
 * See ECMA Section 10.1.4 Scope Chain and Identifier Resolution
 * (This section defines the scope chain of an execution context)
 *
 * See ECMA Section 12.10 The with Statement
 *
 * See ECMA Section 13 Function Definition
 * (This section defines the scope chain of a function object as that
 *  of the running execution context when the function was declared)
 *
 * Like scope-001.js, but using assignment var f = function expression
 * instead of a function declaration: function f() {} etc.
 */
//-------------------------------------------------------------------------------------------------
var UBound = 0;
var bug = '(none)';
var summary = 'Testing that functions are scoped statically, not dynamically';
var self = this;  // capture a reference to the global object
var status = '';
var statusitems = [ ];
var actual = '';
var actualvalues = [ ];
var expect= '';
var expectedvalues = [ ];


/*
 * In this section the expected value is 1, not 2.
 *
 * Why? f captures its scope chain from when it's declared, and imposes that chain
 * when it's executed. In other words, f's scope chain is from when it was compiled.
 * Since f is a top-level function, this is the global object only. Hence 'a' resolves to 1.
 */
status = 'Section A of test';
var a = 1;
var f = function () {return a;};
var obj = {a:2};
with (obj)
{
  actual = f();
}
expect = 1;
addThis();


/*
 * In this section the expected value is 2, not 1. That is because here
 * f's associated scope chain now includes 'obj' before the global object.
 */
status = 'Section B of test';
var a = 1;
var obj = {a:2};
with (obj)
{
  var f = function () {return a;};
  actual = f();
}
expect = 2;
addThis();


/*
 * Like Section B , except that we call f outside the with block.
 * By the principles explained above, we still expect 2 -
 */
status = 'Section C of test';
var a = 1;
var obj = {a:2};
with (obj)
{
  var f = function () {return a;};
}
actual = f();
expect = 2;
addThis();


/*
 * Like Section C, but with one more level of indirection -
 */
status = 'Section D of test';
var a = 1;
var obj = {a:2, obj:{a:3}};
with (obj)
{
  with (obj)
  {
    var f = function () {return a;};
  }
}
actual = f();
expect = 3;
addThis();


/*
 * Like Section C, but here we actually delete obj before calling f.
 * We still expect 2 -
 */
status = 'Section E of test';
var a = 1;
var obj = {a:2};
with (obj)
{
  var f = function () {return a;};
}
delete obj;
actual = f();
expect = 2;
addThis();


/*
 * Like Section E. Here we redefine obj and call f under with (obj) -
 * We still expect 2 -
 */
status = 'Section F of test';
var a = 1;
var obj = {a:2};
with (obj)
{
  var f = function () {return a;};
}
delete obj;
var obj = {a:3};
with (obj)
{
  actual = f();
}
expect = 2;  // NOT 3 !!!
addThis();


/*
 * Explicitly verify that f exists at global level, even though
 * it was defined under the with(obj) block -
 */
status = 'Section G of test';
var a = 1;
var obj = {a:2};
with (obj)
{
  var f = function () {return a;};
}
actual = String([obj.hasOwnProperty('f'), self.hasOwnProperty('f')]);
expect = String([false, true]);
addThis();


/*
 * Explicitly verify that f exists at global level, even though
 * it was defined under the with(obj) block -
 */
status = 'Section H of test';
var a = 1;
var obj = {a:2};
with (obj)
{
  var f = function () {return a;};
}
actual = String(['f' in obj, 'f' in self]);
expect = String([false, true]);
addThis();



//-------------------------------------------------------------------------------------------------
test();
//-------------------------------------------------------------------------------------------------


function addThis()
{
  statusitems[UBound] = status;
  actualvalues[UBound] = actual;
  expectedvalues[UBound] = expect;
  UBound++;
  resetTestVars();
}


function resetTestVars()
{
  delete a;
  delete obj;
  delete f;
}


function test()
{
  enterFunc ('test');
  printBugNumber (bug);
  printStatus (summary);
 
  for (var i = 0; i < UBound; i++)
  {
    reportCompare(expectedvalues[i], actualvalues[i], statusitems[i]);
  }

  exitFunc ('test');
}
