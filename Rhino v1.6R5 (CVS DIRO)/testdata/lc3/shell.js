/* -*- Mode: C++; tab-width: 8; indent-tabs-mode: nil; c-basic-offset: 4 -*-
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

var completed = false;
var testcases = new Array();
var tc = testcases.length;
var DT;

var TITLE   = "";
var SECTION = "";
var VERSION = "";
var BUGNUMBER="";

var GLOBAL= "[object global]";
var PASSED = " PASSED!"
var FAILED = " FAILED! expected: ";

var DEBUG = false;

var TT = "";
var TT_ = "";
var BR = "";
var NBSP = " ";
var CR = "\n";
var FONT = "";
var FONT_ = "";
var FONT_RED = "";
var FONT_GREEN = "";
var B = "";
var B_ = ""
var H2 = "";
var H2_ = "";
var HR = "";


/* 
 * wrapper for test case constructor that doesn't require the SECTION
 * argument.
 */

function AddTestCase( description, expect, actual ) {
  new TestCase( SECTION, description, expect, actual );
}


/*
 * TestCase constructor
 *
 */
function TestCase( d, e, a ) {
  this.path = (typeof gTestPath == 'undefined') ? '' : gTestPath;
  this.name        = d; // tests in this directory oddly depend on a 3-argument
                        // TestCase constructor, rather than the conventional 4
  this.description = d;
  this.expect      = e;
  this.actual      = a;
  this.passed      = true;
  this.reason      = "";
  this.bugnumber   = BUGNUMBER;

  this.passed = getTestCaseResult( this.expect, this.actual );
  if ( DEBUG ) {
    print( "added " + this.description );
  }
  /*
   * testcases are solely maintained in the TestCase
   * constructor. tc will _always_ point to one past the
   * last testcase. If an exception occurs during the call
   * to the constructor, then we are assured that the tc
   * index has not been incremented.
   */
  
  testcases[tc++] = this;
}

/*
 * Set up test environment.
 *
 */
function startTest() {
    testcases = new Array();
    tc = 0;

    //  JavaScript 1.3 is supposed to be compliant ecma version 1.0
    if ( VERSION == "JS1_4" ) {
        version( 140 );
    }
    if ( VERSION == "ECMA_1" ) {
        version ( "130" );
    }
    if ( VERSION == "JS1_3" ) {
        version ( "130" );
    }
    if ( VERSION == "JS1_2" ) {
        version ( "120" );
    }
    if ( VERSION  == "JS1_1" ) {
        version ( "110" );
    }

    writeHeaderToLog( SECTION + " "+ TITLE);
  if ( BUGNUMBER ) {
    print ("BUGNUMBER: " + BUGNUMBER );
  }


    // verify that DataTypeClass is on the CLASSPATH

    DT = Packages.com.netscape.javascript.qa.liveconnect.DataTypeClass;

    if ( typeof DT == "undefined" ) {
        throw "Test Exception:  "+
        "com.netscape.javascript.qa.liveconnect.DataTypeClass "+
        "is not on the CLASSPATH";
    }
}

function test() {
  for ( tc=0; tc < testcases.length; tc++ ) {
    try
    {
    testcases[tc].passed = writeTestCaseResult(
      testcases[tc].expect,
      testcases[tc].actual,
      testcases[tc].description +" = "+
      testcases[tc].actual );

    testcases[tc].reason += ( testcases[tc].passed ) ? "" : "wrong value ";
    }
    catch(e)
    {
      print('test(): empty testcase for tc = ' + tc + ' ' + e);
    }
  }
  stopTest();
  return ( testcases );
}


/*
 * Compare expected result to the actual result and figure out whether
 * the test case passed.
 */

function getTestCaseResult( expect, actual ) {
    //  because ( NaN == NaN ) always returns false, need to do
    //  a special compare to see if we got the right result.
        if ( actual != actual ) {
            if ( typeof actual == "object" ) {
                actual = "NaN object";
            } else {
                actual = "NaN number";
            }
        }
        if ( expect != expect ) {
            if ( typeof expect == "object" ) {
                expect = "NaN object";
            } else {
                expect = "NaN number";
            }
        }

        var passed = ( expect == actual ) ? true : false;

    //  if both objects are numbers
    // need to replace w/ IEEE standard for rounding
        if (    !passed
                && typeof(actual) == "number"
                && typeof(expect) == "number"
            ) {
                if ( Math.abs(actual-expect) < 0.0000001 ) {
                    passed = true;
                }
        }

    //  verify type is the same
        if ( typeof(expect) != typeof(actual) ) {
            passed = false;
        }

        return passed;
}

/*
 * Begin printing functions.  These functions use the shell's
 * print function.  When running tests in the browser, these
 * functions, override these functions with functions that use
 * document.write.
 */

function writeTestCaseResult( expect, actual, string ) {
        var passed = getTestCaseResult( expect, actual );
        writeFormattedResult( expect, actual, string, passed );
        return passed;
}
function writeFormattedResult( expect, actual, string, passed ) {
        var s = TT + string ;

        for ( k = 0;
              k <  (60 - string.length >= 0 ? 60 - string.length : 5) ;
              k++ ) {
//              s += NBSP;
        }

        s += B ;
        s += ( passed ) ? FONT_GREEN + NBSP + PASSED : FONT_RED + NBSP + FAILED + expect + TT_ ;

        print( s + FONT_ + B_ + TT_ );

        return passed;
}

function writeHeaderToLog( string ) {
    print( H2 + string + H2_ );
}

/*
 * When running in the shell, run the garbage collector after the
 * test has completed.
 */

function stopTest() {
    var gc;

    if ( gc != undefined ) {
        gc();
    }
    print( HR );
}

/*
 * Convenience function for displaying failed test cases.  Useful
 * when running tests manually.
 *
 */
function getFailedCases() {
  for ( var i = 0; i < testcases.length; i++ ) {
     if ( ! testcases[i].passed ) {
        print( testcases[i].description +" = " +testcases[i].actual +" expected: "+ testcases[i].expect );
     }
  }
}
