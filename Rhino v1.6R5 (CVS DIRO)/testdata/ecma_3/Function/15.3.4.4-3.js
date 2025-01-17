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
 *   jlaprise@delanotech.com,pschwartau@netscape.com
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
 * Date: 2001-07-10
 *
 * SUMMARY:  Invoking try...catch through Function.call
 * See  http://bugzilla.mozilla.org/show_bug.cgi?id=49286
 *
 * 1) Define a function with a try...catch block in it
 * 2) Invoke the function via the call method of Function
 * 3) Pass bad syntax to the try...catch block
 * 4) We should catch the error!
 */
//-------------------------------------------------------------------------------------------------
var UBound = 0;
var bug = 49286;
var summary = 'Invoking try...catch through Function.call';
var cnErrorCaught = 'Error caught';
var cnErrorNotCaught = 'Error NOT caught';
var cnGoodSyntax = '1==2';
var cnBadSyntax = '1=2';
var status = '';
var statusitems = [];
var actual = '';
var actualvalues = [];
var expect= '';
var expectedvalues = [];


var obj = new testObject();

status = 'Section A of test: direct call of f';
actual = f.call(obj);
expect = cnErrorCaught;
addThis();

status = 'Section B of test: indirect call of f';
actual = g.call(obj);
expect = cnErrorCaught;
addThis();



//-----------------------------------------
test();
//-----------------------------------------


function test()
{
  enterFunc ('test');
  printBugNumber (bug);
  printStatus (summary);

  for (var i=0; i<UBound; i++)
  {
    reportCompare(expectedvalues[i], actualvalues[i], statusitems[i]);
  }

  exitFunc ('test');
}


// An object storing bad syntax as a property -
function testObject()
{
  this.badSyntax = cnBadSyntax;
  this.goodSyntax = cnGoodSyntax;
}


// A function wrapping a try...catch block
function f()
{
  try
  {
    eval(this.badSyntax);
  }
  catch(e)
  {
    return cnErrorCaught;
  }
  return cnErrorNotCaught;
}


// A function wrapping a call to f -
function g()
{
  return f.call(this);
}


function addThis()
{
  statusitems[UBound] = status;
  actualvalues[UBound] = actual;
  expectedvalues[UBound] = expect;
  UBound++;
}
