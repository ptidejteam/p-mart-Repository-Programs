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
 * Jeff Walden.
 * Portions created by the Initial Developer are Copyright (C) 2006
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
//-----------------------------------------------------------------------------
var bug     = "(none)";
var summary = "Iterator() test";
var actual, expect;

printBugNumber(bug);
printStatus(summary);

/**************
 * BEGIN TEST *
 **************/

var failed = false;

function Array_equals(a, b)
{
  if (!(a instanceof Array) || !(b instanceof Array))
    throw new Error("Arguments not both of type Array");
  if (a.length != b.length)
    return false;
  for (var i = 0, sz = a.length; i < sz; i++)
    if (a[i] !== b[i])
      return false;
  return true;
}

var meow = "meow", oink = "oink", baa = "baa";

var it = Iterator([meow, oink, baa]);
var it2 = Iterator([meow, oink, baa], true);

try
{
  if (!Array_equals(it.next(), [0, meow]))
    throw [0, meow];
  if (!Array_equals(it.next(), [1, oink]))
    throw [1, oink];
  if (!Array_equals(it.next(), [2, baa]))
    throw [2, baa];

  var stopPassed = false;
  try
  {
    it.next();
  }
  catch (e)
  {
    if (e === StopIteration)
      stopPassed = true;
  }

  if (!stopPassed)
    throw "it: missing or incorrect StopIteration";

  if (it2.next() != 0)
    throw "wanted key=0";
  if (it2.next() != 1)
    throw "wanted key=1";
  if (it2.next() != 2)
    throw "wanted key=2";

  var stopPassed = false;
  try
  {
    it2.next();
  }
  catch (e)
  {
    if (e === StopIteration)
      stopPassed = true;
  }

  if (!stopPassed)
    throw "it2: missing or incorrect StopIteration";
}
catch (e)
{
  failed = e;
}

expect = false;
actual = failed;

reportCompare(expect, actual, summary);
