/* -*- Mode: C++; tab-width: 2; indent-tabs-mode: nil; c-basic-offset: 2 -*- */
/**
   File Name:          statement-001.js
   Corresponds To:     12.6.2-9-n.js
   ECMA Section:       12.6.2 The for Statement

   1. first expression is not present.
   2. second expression is not present
   3. third expression is not present


   Author:             christine@netscape.com
   Date:               15 september 1997
*/

var SECTION = "statement-001.js";
//     var SECTION = "12.6.2-9-n";
var VERSION = "ECMA_1";
var TITLE   = "The for statement";

startTest();
writeHeaderToLog( SECTION + " "+ TITLE);

var result = "Failed";
var exception = "No exception thrown";
var expect = "Passed";

try {
  eval("for (i) {\n}");
} catch ( e ) {
  result = expect;
  exception = e.toString();
}

new TestCase(
  SECTION,
  "for(i) {}" +
  " (threw " + exception +")",
  expect,
  result );

test();
