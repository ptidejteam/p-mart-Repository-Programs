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
 * Portions created by the Initial Developer are Copyright (C) 2002
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
 * Date:    31 Oct 2002
 * SUMMARY: Testing script with at least 64K of different string literals
 * See http://bugzilla.mozilla.org/show_bug.cgi?id=159334
 *
 * Testing that script engine can handle scripts with at least 64K of different
 * string literals. The following will evaluate, via eval(), a script like this:
 *
 *     f('0')
 *     f('1')
 *     ...
 *     f('N - 1')
 *
 * where N is 0xFFFE
 *
 */
//-----------------------------------------------------------------------------
var UBound = 0;
var bug = 159334;
var summary = 'Testing script with at least 64K of different string literals';
var status = '';
var statusitems = [];
var actual = '';
var actualvalues = [];
var expect= '';
var expectedvalues = [];


var N = 0xFFFE;

// Create big string for eval recursively to avoid N*N behavior
// on string concatenation
var long_eval = buildEval_r(0, N);

// Run it
var test_sum = 0;
function f(str) { test_sum += Number(str); }
eval(long_eval);

status = inSection(1);
actual = (test_sum == N * (N - 1) / 2);
expect = true;
addThis();



//-----------------------------------------------------------------------------
test();
//-----------------------------------------------------------------------------



function buildEval_r(beginLine, endLine)
{
  var count = endLine - beginLine;

  if (count == 0)
    return "";

  if (count == 1)
    return "f('" + beginLine + "')\n";

  var middle = beginLine + (count >>> 1);
  return buildEval_r(beginLine, middle) + buildEval_r(middle, endLine);
}


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
