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
 *   okin7@yahoo.fr, pschwartau@netscape.com
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
 * Date:    19 August 2003
 * SUMMARY: Regexp conformance test
 *
 * See http://bugzilla.mozilla.org/show_bug.cgi?id=216591
 *
 */
//-----------------------------------------------------------------------------
var i = 0;
var bug = 216591;
var summary = 'Regexp conformance test';
var status = '';
var statusmessages = new Array();
var pattern = '';
var patterns = new Array();
var string = '';
var strings = new Array();
var actualmatch = '';
var actualmatches = new Array();
var expectedmatch = '';
var expectedmatches = new Array();


status = inSection(1);
string = 'a {result.data.DATA} b';
pattern = /\{(([a-z0-9\-_]+?\.)+?)([a-z0-9\-_]+?)\}/i;
actualmatch = string.match(pattern);
expectedmatch = Array('{result.data.DATA}', 'result.data.', 'data.', 'DATA');
addThis();

/*
 * Add a global flag to the regexp. In Perl 5, this gives the same results as above. Compare:
 *
 * [ ] perl -e '"a {result.data.DATA} b" =~ /\{(([a-z0-9\-_]+?\.)+?)([a-z0-9\-_]+?)\}/i;  print("$&, $1, $2, $3");'
 * {result.data.DATA}, result.data., data., DATA
 *
 * [ ] perl -e '"a {result.data.DATA} b" =~ /\{(([a-z0-9\-_]+?\.)+?)([a-z0-9\-_]+?)\}/gi; print("$&, $1, $2, $3");'
 * {result.data.DATA}, result.data., data., DATA
 *
 *
 * But in JavaScript, there will no longer be any sub-captures:
 */
status = inSection(2);
string = 'a {result.data.DATA} b';
pattern = /\{(([a-z0-9\-_]+?\.)+?)([a-z0-9\-_]+?)\}/gi;
actualmatch = string.match(pattern);
expectedmatch = Array('{result.data.DATA}');
addThis();




//-----------------------------------------------------------------------------
test();
//-----------------------------------------------------------------------------



function addThis()
{
  statusmessages[i] = status;
  patterns[i] = pattern;
  strings[i] = string;
  actualmatches[i] = actualmatch;
  expectedmatches[i] = expectedmatch;
  i++;
}


function test()
{
  enterFunc ('test');
  printBugNumber (bug);
  printStatus (summary);
  testRegExp(statusmessages, patterns, strings, actualmatches, expectedmatches);
  exitFunc ('test');
}
