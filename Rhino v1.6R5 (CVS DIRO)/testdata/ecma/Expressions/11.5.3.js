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
    File Name:          11.5.3.js
    ECMA Section:       11.5.3 Applying the % operator
    Description:

    The binary % operator is said to yield the remainder of its operands from
    an implied division; the left operand is the dividend and the right operand
    is the divisor. In C and C++, the remainder operator accepts only integral
    operands, but in ECMAScript, it also accepts floating-point operands.

    The result of a floating-point remainder operation as computed by the %
    operator is not the same as the "remainder" operation defined by IEEE 754.
    The IEEE 754 "remainder" operation computes the remainder from a rounding
    division, not a truncating division, and so its behavior is not analogous
    to that of the usual integer remainder operator. Instead the ECMAScript
    language defines % on floating-point operations to behave in a manner
    analogous to that of the Java integer remainder operator; this may be
    compared with the C library function fmod.

    The result of a ECMAScript floating-point remainder operation is determined by the rules of IEEE arithmetic:

      If either operand is NaN, the result is NaN.
      The sign of the result equals the sign of the dividend.
      If the dividend is an infinity, or the divisor is a zero, or both, the result is NaN.
      If the dividend is finite and the divisor is an infinity, the result equals the dividend.
      If the dividend is a zero and the divisor is finite, the result is the same as the dividend.
      In the remaining cases, where neither an infinity, nor a zero, nor NaN is involved, the floating-point remainder r
      from a dividend n and a divisor d is defined by the mathematical relation r = n (d * q) where q is an integer that
      is negative only if n/d is negative and positive only if n/d is positive, and whose magnitude is as large as
      possible without exceeding the magnitude of the true mathematical quotient of n and d.

    Author:             christine@netscape.com
    Date:               12 november 1997
*/
    var SECTION = "11.5.3";
    var VERSION = "ECMA_1";
    var BUGNUMBER="111202";
    startTest();


    writeHeaderToLog( SECTION + " Applying the % operator");

   // if either operand is NaN, the result is NaN.

new TestCase( SECTION,    "Number.NaN % Number.NaN",    Number.NaN,     Number.NaN % Number.NaN );
new TestCase( SECTION,    "Number.NaN % 1",             Number.NaN,     Number.NaN % 1 );
new TestCase( SECTION,    "1 % Number.NaN",             Number.NaN,     1 % Number.NaN );

new TestCase( SECTION,    "Number.POSITIVE_INFINITY % Number.NaN",    Number.NaN,     Number.POSITIVE_INFINITY % Number.NaN );
new TestCase( SECTION,    "Number.NEGATIVE_INFINITY % Number.NaN",    Number.NaN,     Number.NEGATIVE_INFINITY % Number.NaN );

    //  If the dividend is an infinity, or the divisor is a zero, or both, the result is NaN.
    //  dividend is an infinity

new TestCase( SECTION,    "Number.NEGATIVE_INFINITY % Number.NEGATIVE_INFINITY",    Number.NaN,   Number.NEGATIVE_INFINITY % Number.NEGATIVE_INFINITY );
new TestCase( SECTION,    "Number.POSITIVE_INFINITY % Number.NEGATIVE_INFINITY",    Number.NaN,   Number.POSITIVE_INFINITY % Number.NEGATIVE_INFINITY );
new TestCase( SECTION,    "Number.NEGATIVE_INFINITY % Number.POSITIVE_INFINITY",    Number.NaN,   Number.NEGATIVE_INFINITY % Number.POSITIVE_INFINITY );
new TestCase( SECTION,    "Number.POSITIVE_INFINITY % Number.POSITIVE_INFINITY",    Number.NaN,   Number.POSITIVE_INFINITY % Number.POSITIVE_INFINITY );

new TestCase( SECTION,    "Number.POSITIVE_INFINITY % 0",   Number.NaN,     Number.POSITIVE_INFINITY % 0 );
new TestCase( SECTION,    "Number.NEGATIVE_INFINITY % 0",   Number.NaN,     Number.NEGATIVE_INFINITY % 0 );
new TestCase( SECTION,    "Number.POSITIVE_INFINITY % -0",  Number.NaN,     Number.POSITIVE_INFINITY % -0 );
new TestCase( SECTION,    "Number.NEGATIVE_INFINITY % -0",  Number.NaN,     Number.NEGATIVE_INFINITY % -0 );

new TestCase( SECTION,    "Number.NEGATIVE_INFINITY % 1 ",  Number.NaN,     Number.NEGATIVE_INFINITY % 1 );
new TestCase( SECTION,    "Number.NEGATIVE_INFINITY % -1 ", Number.NaN,     Number.NEGATIVE_INFINITY % -1 );
new TestCase( SECTION,    "Number.POSITIVE_INFINITY % 1 ",  Number.NaN,     Number.POSITIVE_INFINITY % 1 );
new TestCase( SECTION,    "Number.POSITIVE_INFINITY % -1 ", Number.NaN,     Number.POSITIVE_INFINITY % -1 );

new TestCase( SECTION,    "Number.NEGATIVE_INFINITY % Number.MAX_VALUE ",   Number.NaN,   Number.NEGATIVE_INFINITY % Number.MAX_VALUE );
new TestCase( SECTION,    "Number.NEGATIVE_INFINITY % -Number.MAX_VALUE ",  Number.NaN,   Number.NEGATIVE_INFINITY % -Number.MAX_VALUE );
new TestCase( SECTION,    "Number.POSITIVE_INFINITY % Number.MAX_VALUE ",   Number.NaN,   Number.POSITIVE_INFINITY % Number.MAX_VALUE );
new TestCase( SECTION,    "Number.POSITIVE_INFINITY % -Number.MAX_VALUE ",  Number.NaN,   Number.POSITIVE_INFINITY % -Number.MAX_VALUE );

    // divisor is 0
new TestCase( SECTION,    "0 % -0",                         Number.NaN,     0 % -0 );
new TestCase( SECTION,    "-0 % 0",                         Number.NaN,     -0 % 0 );
new TestCase( SECTION,    "-0 % -0",                        Number.NaN,     -0 % -0 );
new TestCase( SECTION,    "0 % 0",                          Number.NaN,     0 % 0 );

new TestCase( SECTION,    "1 % 0",                          Number.NaN,   1%0 );
new TestCase( SECTION,    "1 % -0",                         Number.NaN,   1%-0 );
new TestCase( SECTION,    "-1 % 0",                         Number.NaN,   -1%0 );
new TestCase( SECTION,    "-1 % -0",                        Number.NaN,   -1%-0 );

new TestCase( SECTION,    "Number.MAX_VALUE % 0",           Number.NaN,   Number.MAX_VALUE%0 );
new TestCase( SECTION,    "Number.MAX_VALUE % -0",          Number.NaN,   Number.MAX_VALUE%-0 );
new TestCase( SECTION,    "-Number.MAX_VALUE % 0",          Number.NaN,   -Number.MAX_VALUE%0 );
new TestCase( SECTION,    "-Number.MAX_VALUE % -0",         Number.NaN,   -Number.MAX_VALUE%-0 );

    // If the dividend is finite and the divisor is an infinity, the result equals the dividend.

new TestCase( SECTION,    "1 % Number.NEGATIVE_INFINITY",   1,              1 % Number.NEGATIVE_INFINITY );
new TestCase( SECTION,    "1 % Number.POSITIVE_INFINITY",   1,              1 % Number.POSITIVE_INFINITY );
new TestCase( SECTION,    "-1 % Number.POSITIVE_INFINITY",  -1,             -1 % Number.POSITIVE_INFINITY );
new TestCase( SECTION,    "-1 % Number.NEGATIVE_INFINITY",  -1,             -1 % Number.NEGATIVE_INFINITY );

new TestCase( SECTION,    "Number.MAX_VALUE % Number.NEGATIVE_INFINITY",   Number.MAX_VALUE,    Number.MAX_VALUE % Number.NEGATIVE_INFINITY );
new TestCase( SECTION,    "Number.MAX_VALUE % Number.POSITIVE_INFINITY",   Number.MAX_VALUE,    Number.MAX_VALUE % Number.POSITIVE_INFINITY );
new TestCase( SECTION,    "-Number.MAX_VALUE % Number.POSITIVE_INFINITY",  -Number.MAX_VALUE,   -Number.MAX_VALUE % Number.POSITIVE_INFINITY );
new TestCase( SECTION,    "-Number.MAX_VALUE % Number.NEGATIVE_INFINITY",  -Number.MAX_VALUE,   -Number.MAX_VALUE % Number.NEGATIVE_INFINITY );

new TestCase( SECTION,    "0 % Number.POSITIVE_INFINITY",   0, 0 % Number.POSITIVE_INFINITY );
new TestCase( SECTION,    "0 % Number.NEGATIVE_INFINITY",   0, 0 % Number.NEGATIVE_INFINITY );
new TestCase( SECTION,    "-0 % Number.POSITIVE_INFINITY",  -0,   -0 % Number.POSITIVE_INFINITY );
new TestCase( SECTION,    "-0 % Number.NEGATIVE_INFINITY",  -0,   -0 % Number.NEGATIVE_INFINITY );

    // If the dividend is a zero and the divisor is finite, the result is the same as the dividend.

new TestCase( SECTION,    "0 % 1",                          0,              0 % 1 );
new TestCase( SECTION,    "0 % -1",                        -0,              0 % -1 );
new TestCase( SECTION,    "-0 % 1",                        -0,              -0 % 1 );
new TestCase( SECTION,    "-0 % -1",                       0,               -0 % -1 );

//        In the remaining cases, where neither an infinity, nor a zero, nor NaN is involved, the floating-point remainder r
//      from a dividend n and a divisor d is defined by the mathematical relation r = n (d * q) where q is an integer that
//      is negative only if n/d is negative and positive only if n/d is positive, and whose magnitude is as large as
//      possible without exceeding the magnitude of the true mathematical quotient of n and d.

test();

