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
 *   pschwartau@netscape.com
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
 * Date: 09 May 2001
 *
 * SUMMARY: Regression test: we shouldn't crash on this code
 * See http://bugzilla.mozilla.org/show_bug.cgi?id=72773
 *
 * See ECMA-262 Edition 3 13-Oct-1999, Section 8.6.2 re [[Class]] property.
 *
 * Same as class-001.js - but testing user-defined types here, not native types.
 * Therefore we expect the [[Class]] property to equal 'Object' in each case -
 *
 * The getJSClass() function we use is in a utility file, e.g. "shell.js"
 */
//-------------------------------------------------------------------------------------------------
var bug = 72773;
var summary = "Regression test: we shouldn't crash on this code";
var status = '';
var actual = '';
var expect = '';
var sToEval = '';

/*
 * This code should produce an error, but not a crash.
 *  'TypeError: Function.prototype.toString called on incompatible object'
 */
sToEval += 'function Cow(name){this.name = name;}'
sToEval += 'function Calf(str){this.name = str;}'
sToEval += 'Calf.prototype = Cow;'
sToEval += 'new Calf().toString();'

status = 'Trying to catch an expected error';
try
{
  eval(sToEval);
}
catch(e)
{
  actual = getJSClass(e);
  expect = 'Error';
}


//----------------------------------------------------------------------------------------------
test();
//----------------------------------------------------------------------------------------------


function test()
{
  enterFunc ('test');
  printBugNumber (bug);
  printStatus (summary);

  reportCompare(expect, actual, status);

  exitFunc ('test');
}
