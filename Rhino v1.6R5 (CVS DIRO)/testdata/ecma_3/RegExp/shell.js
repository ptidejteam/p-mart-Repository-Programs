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
 * Date: 07 February 2001
 *
 * Functionality common to RegExp testing -
 */
//-------------------------------------------------------------------------------------------------
var MSG_PATTERN = '\nregexp = ';
var MSG_STRING = '\nstring = ';
var MSG_EXPECT = '\nExpect: ';
var MSG_ACTUAL = '\nActual: ';
var ERR_LENGTH = '\nERROR !!! match arrays have different lengths:';
var ERR_MATCH = '\nERROR !!! regexp failed to give expected match array:';
var ERR_NO_MATCH = '\nERROR !!! regexp FAILED to match anything !!!';
var ERR_UNEXP_MATCH = '\nERROR !!! regexp MATCHED when we expected it to fail !!!';
var CHAR_LBRACKET = '[';
var CHAR_RBRACKET = ']';
var CHAR_QT_DBL = '"';
var CHAR_QT = "'";
var CHAR_NL = '\n';
var CHAR_COMMA = ',';
var CHAR_SPACE = ' ';
var TYPE_STRING = typeof 'abc';



function testRegExp(statuses, patterns, strings, actualmatches, expectedmatches)
{
  var status = '';
  var pattern = new RegExp();
  var string = '';
  var actualmatch = new Array();
  var expectedmatch = new Array();
  var state = '';
  var lActual = -1;
  var lExpect = -1;


  for (var i=0; i != patterns.length; i++)
  {
    status = statuses[i];
    pattern = patterns[i];
    string = strings[i];
    actualmatch=actualmatches[i];
    expectedmatch=expectedmatches[i];
    state = getState(status, pattern, string);

    description = status;

    if(actualmatch)
    {
      actual = formatArray(actualmatch);
      if(expectedmatch)
      {
        // expectedmatch and actualmatch are arrays -
        lExpect = expectedmatch.length;
        lActual = actualmatch.length;
 
        expected = formatArray(expectedmatch);

        if (lActual != lExpect)
        {
          reportFailure(
                        state + ERR_LENGTH +
                        MSG_EXPECT + expected +
                        MSG_ACTUAL + actual +
                        CHAR_NL
                       );
          continue;
        }

        // OK, the arrays have same length -
        if (expected != actual)
        {
          reportFailure(
                        state + ERR_MATCH +
                        MSG_EXPECT + expected +
                        MSG_ACTUAL + actual +
                        CHAR_NL
                       );
        }
        else
        {
          reportCompare(expected, actual, state)
        }

      }
      else //expectedmatch is null - that is, we did not expect a match -
      {
        expected = expectedmatch;
        reportFailure(
                      state + ERR_UNEXP_MATCH +
                      MSG_EXPECT + expectedmatch +
                      MSG_ACTUAL + actual +
                      CHAR_NL
                     );
      }

    }
    else // actualmatch is null
    {
      if (expectedmatch)
      {
        actual = actualmatch;
        reportFailure(
                      state + ERR_NO_MATCH +
                      MSG_EXPECT + expectedmatch +
                      MSG_ACTUAL + actualmatch +
                      CHAR_NL
                     );
      }
      else // we did not expect a match
      {
        // Being ultra-cautious. Presumably expectedmatch===actualmatch===null
        expected = expectedmatch;
        actual   = actualmatch;
        reportCompare (expectedmatch, actualmatch, state);
      }
    }
  }
}


function getState(status, pattern, string)
{
  /*
   * Escape \n's, etc. to make them LITERAL in the presentation string.
   * We don't have to worry about this in |pattern|; such escaping is
   * done automatically by pattern.toString(), invoked implicitly below.
   *
   * One would like to simply do: string = string.replace(/(\s)/g, '\$1').
   * However, the backreference $1 is not a literal string value,
   * so this method doesn't work.
   *
   * Also tried string = string.replace(/(\s)/g, escape('$1'));
   * but this just inserts the escape of the literal '$1', i.e. '%241'.
   */
  string = string.replace(/\n/g, '\\n');
  string = string.replace(/\r/g, '\\r');
  string = string.replace(/\t/g, '\\t');
  string = string.replace(/\v/g, '\\v');
  string = string.replace(/\f/g, '\\f');

  return (status + MSG_PATTERN + pattern + MSG_STRING + singleQuote(string));
}


/*
 * If available, arr.toSource() gives more detail than arr.toString()
 *
 * var arr = Array(1,2,'3');
 *
 * arr.toSource()
 * [1, 2, "3"]
 *
 * arr.toString()
 * 1,2,3
 *
 * But toSource() doesn't exist in Rhino, so use our own imitation, below -
 *
 */
function formatArray(arr)
{
  try
  {
    return arr.toSource();
  }
  catch(e)
  {
    return toSource(arr);
  }
}


/*
 * Imitate SpiderMonkey's arr.toSource() method:
 *
 * a) Double-quote each array element that is of string type
 * b) Represent |undefined| and |null| by empty strings
 * c) Delimit elements by a comma + single space
 * d) Do not add delimiter at the end UNLESS the last element is |undefined|
 * e) Add square brackets to the beginning and end of the string
 */
function toSource(arr)
{
  var delim = CHAR_COMMA + CHAR_SPACE;
  var elt = '';
  var ret = '';
  var len = arr.length;

  for (i=0; i<len; i++)
  {
    elt = arr[i];

    switch(true)
    {
      case (typeof elt === TYPE_STRING) :
        ret += doubleQuote(elt);
        break;

      case (elt === undefined || elt === null) :
        break; // add nothing but the delimiter, below -

      default:
        ret += elt.toString();
    }

    if ((i < len-1) || (elt === undefined))
      ret += delim;
  }

  return  CHAR_LBRACKET + ret + CHAR_RBRACKET;
}


function doubleQuote(text)
{
  return CHAR_QT_DBL + text + CHAR_QT_DBL;
}


function singleQuote(text)
{
  return CHAR_QT + text + CHAR_QT;
}

