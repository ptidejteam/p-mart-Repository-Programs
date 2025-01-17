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
    File Name:          15.9.5.6.js
    ECMA Section: 15.9.5.6 Date.prototype.toLocaleDateString()
    Description:
    This function returns a string value. The contents of the string are
    implementation dependent, but are intended to represent the "date"
    portion of the Date in the current time zone in a convenient,
    human-readable form.   We can't test the content of the string,  
    but can verify that the string is parsable by Date.parse

    The toLocaleDateString function is not generic; it generates a runtime error
    if its 'this' value is not a Date object. Therefore it cannot be transferred
    to other kinds of objects for use as a method.

    Note: This test isn't supposed to work with a non-English locale per spec.

    Author:  pschwartau@netscape.com
    Date:      14 november 2000
*/

   var SECTION = "15.9.5.6";
   var VERSION = "ECMA_3";  
   var TITLE   = "Date.prototype.toLocaleDateString()";  
  
   var status = '';
   var actual = '';  
   var expect = '';


   startTest();
   writeHeaderToLog( SECTION + " "+ TITLE);


//-----------------------------------------------------------------------------------------------------
   var testcases = new Array();
//-----------------------------------------------------------------------------------------------------


   // first, some generic tests -

   status = "typeof (now.toLocaleDateString())";  
   actual =   typeof (now.toLocaleDateString());
   expect = "string";
   addTestCase();

   status = "Date.prototype.toLocaleDateString.length";   
   actual =  Date.prototype.toLocaleDateString.length;
   expect =  0;   
   addTestCase();

   /* Date.parse is accurate to the second;  valueOf() to the millisecond.
        Here we expect them to coincide, as we expect a time of exactly midnight -  */
   status = "(Date.parse(now.toLocaleDateString()) - (midnight(now)).valueOf()) == 0";   
   actual =   (Date.parse(now.toLocaleDateString()) - (midnight(now)).valueOf()) == 0;
   expect = true;
   addTestCase();



   // 1970
   addDateTestCase(0);
   addDateTestCase(TZ_ADJUST);   

   
   // 1900
   addDateTestCase(TIME_1900); 
   addDateTestCase(TIME_1900 - TZ_ADJUST);

   
   // 2000
   addDateTestCase(TIME_2000);
   addDateTestCase(TIME_2000 - TZ_ADJUST);

    
   // 29 Feb 2000
   addDateTestCase(UTC_29_FEB_2000);
   addDateTestCase(UTC_29_FEB_2000 - 1000);    
   addDateTestCase(UTC_29_FEB_2000 - TZ_ADJUST);
 

   // 2005
   addDateTestCase(UTC_1_JAN_2005);
   addDateTestCase(UTC_1_JAN_2005 - 1000);
   addDateTestCase(UTC_1_JAN_2005 - TZ_ADJUST);
   


//-----------------------------------------------------------------------------------------------------
   test();
//-----------------------------------------------------------------------------------------------------


function addTestCase()
{
  new TestCase( SECTION, status, expect, actual); 
}


function addDateTestCase(date_given_in_milliseconds)
{
  var givenDate = new Date(date_given_in_milliseconds);

  status = 'Date.parse('   +   givenDate   +   ').toLocaleDateString())';   
  actual =  Date.parse(givenDate.toLocaleDateString());
  expect = Date.parse(midnight(givenDate));
  addTestCase();
}


function midnight(givenDate) 
{
  // midnight on the given date -
  return new Date(givenDate.getFullYear(), givenDate.getMonth(), givenDate.getDate());
}


function test() 
{
  for ( tc=0; tc < testcases.length; tc++ ) 
  {
    testcases[tc].passed = writeTestCaseResult(
                                               testcases[tc].expect,
                                               testcases[tc].actual,
                                               testcases[tc].description  +  " = "  +  testcases[tc].actual );

    testcases[tc].reason += ( testcases[tc].passed ) ? "" : "wrong value ";
  }
  stopTest();
  return (testcases);
}
