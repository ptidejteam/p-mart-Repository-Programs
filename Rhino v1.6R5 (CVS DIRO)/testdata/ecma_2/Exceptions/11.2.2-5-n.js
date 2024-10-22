/* -*- Mode: C++; tab-width: 2; indent-tabs-mode: nil; c-basic-offset: 2 -*- */
/**
   File Name:          expression-010.js
   Corresponds To:     11.2.2-5-n.js
   ECMA Section:       11.2.2. The new operator
   Description:
   Author:             christine@netscape.com
   Date:               12 november 1997
*/
var SECTION = "expression-010";
var VERSION = "JS1_4";
var TITLE   = "The new operator";

startTest();
writeHeaderToLog( SECTION + " "+ TITLE);

var NUMBER = 0;

var result = "Failed";
var exception = "No exception thrown";
var expect = "Passed";

try {
  result = new NUMBER();
} catch ( e ) {
  result = expect;
  exception = e.toString();
}

new TestCase(
  SECTION,
  "NUMBER=0, result = new NUMBER()" +
  " (threw " + exception +")",
  expect,
  result );

test();

