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
   File Name:          15.9.5.37-1.js
   ECMA Section:       15.9.5.37 Date.prototype.setUTCFullYear(year [, mon [, date ]] )
   Description:

   If mon is not specified, this behaves as if mon were specified with the
   value getUTCMonth( ).  If date is not specified, this behaves as if date
   were specified with the value getUTCDate( ).

   1.   Let t be this time value; but if this time value is NaN, let t be +0.
   2.   Call ToNumber(year).
   3.   If mon is not specified, compute MonthFromTime(t); otherwise, call
   ToNumber(mon).
   4.   If date is not specified, compute DateFromTime(t); otherwise, call
   ToNumber(date).
   5.   Compute MakeDay(Result(2), Result(3), Result(4)).
   6.   Compute MakeDate(Result(5), TimeWithinDay(t)).
   7.   Set the [[Value]] property of the this value to TimeClip(Result(6)).
   8.   Return the value of the [[Value]] property of the this value.

   Author:             christine@netscape.com
   Date:               12 november 1997

   Added some Year 2000 test cases.
*/
var SECTION = "15.9.5.37-1";
var VERSION = "ECMA_1";
startTest();

writeHeaderToLog( SECTION + " Date.prototype.setUTCFullYear(year [, mon [, date ]] )");


// Dates around 29 February 2000

var UTC_FEB_29_1972 = TIME_1970 + TimeInYear(1970) + TimeInYear(1971) +
31*msPerDay + 28*msPerDay;

var PST_FEB_29_1972 = UTC_FEB_29_1972 - TZ_DIFF * msPerHour;

addNewTestCase( "TDATE = new Date("+UTC_FEB_29_1972+"); "+
		"TDATE.setUTCFullYear(2000);TDATE",
		UTCDateFromTime(SetUTCFullYear(UTC_FEB_29_1972,2000)),
		LocalDateFromTime(SetUTCFullYear(UTC_FEB_29_1972,2000)) );

addNewTestCase( "TDATE = new Date("+PST_FEB_29_1972+"); "+
		"TDATE.setUTCFullYear(2000);TDATE",
		UTCDateFromTime(SetUTCFullYear(PST_FEB_29_1972,2000)),
		LocalDateFromTime(SetUTCFullYear(PST_FEB_29_1972,2000)) );

test();

function addNewTestCase( DateString, UTCDate, LocalDate) {
  DateCase = eval( DateString );

  new TestCase( SECTION, DateString+".getTime()",             UTCDate.value,       DateCase.getTime() );
  new TestCase( SECTION, DateString+".valueOf()",             UTCDate.value,       DateCase.valueOf() );

  new TestCase( SECTION, DateString+".getUTCFullYear()",      UTCDate.year,    DateCase.getUTCFullYear() );
  new TestCase( SECTION, DateString+".getUTCMonth()",         UTCDate.month,  DateCase.getUTCMonth() );
  new TestCase( SECTION, DateString+".getUTCDate()",          UTCDate.date,   DateCase.getUTCDate() );
  new TestCase( SECTION, DateString+".getUTCDay()",           UTCDate.day,    DateCase.getUTCDay() );
  new TestCase( SECTION, DateString+".getUTCHours()",         UTCDate.hours,  DateCase.getUTCHours() );
  new TestCase( SECTION, DateString+".getUTCMinutes()",       UTCDate.minutes,DateCase.getUTCMinutes() );
  new TestCase( SECTION, DateString+".getUTCSeconds()",       UTCDate.seconds,DateCase.getUTCSeconds() );
  new TestCase( SECTION, DateString+".getUTCMilliseconds()",  UTCDate.ms,     DateCase.getUTCMilliseconds() );

  new TestCase( SECTION, DateString+".getFullYear()",         LocalDate.year,       DateCase.getFullYear() );
  new TestCase( SECTION, DateString+".getMonth()",            LocalDate.month,      DateCase.getMonth() );
  new TestCase( SECTION, DateString+".getDate()",             LocalDate.date,       DateCase.getDate() );
  new TestCase( SECTION, DateString+".getDay()",              LocalDate.day,        DateCase.getDay() );
  new TestCase( SECTION, DateString+".getHours()",            LocalDate.hours,      DateCase.getHours() );
  new TestCase( SECTION, DateString+".getMinutes()",          LocalDate.minutes,    DateCase.getMinutes() );
  new TestCase( SECTION, DateString+".getSeconds()",          LocalDate.seconds,    DateCase.getSeconds() );
  new TestCase( SECTION, DateString+".getMilliseconds()",     LocalDate.ms,         DateCase.getMilliseconds() );

  DateCase.toString = Object.prototype.toString;

  new TestCase( SECTION,
		DateString+".toString=Object.prototype.toString;"+DateString+".toString()",
		"[object Date]",
		DateCase.toString() );
}

function MyDate() {
  this.year = 0;
  this.month = 0;
  this.date = 0;
  this.hours = 0;
  this.minutes = 0;
  this.seconds = 0;
  this.ms = 0;
}
function LocalDateFromTime(t) {
  t = LocalTime(t);
  return ( MyDateFromTime(t) );
}
function UTCDateFromTime(t) {
  return ( MyDateFromTime(t) );
}
function MyDateFromTime( t ) {
  var d = new MyDate();
  d.year = YearFromTime(t);
  d.month = MonthFromTime(t);
  d.date = DateFromTime(t);
  d.hours = HourFromTime(t);
  d.minutes = MinFromTime(t);
  d.seconds = SecFromTime(t);
  d.ms = msFromTime(t);

  d.time = MakeTime( d.hours, d.minutes, d.seconds, d.ms );
  d.value = TimeClip( MakeDate( MakeDay( d.year, d.month, d.date ), d.time ) );
  d.day = WeekDay( d.value );

  return (d);
}
function SetUTCFullYear( t, year, mon, date ) {
  var T = ( t != t ) ? 0 : t;
  var YEAR = Number(year);
  var MONTH = ( mon == void 0 ) ?     MonthFromTime(T) : Number( mon );
  var DATE  = ( date == void 0 ) ?    DateFromTime(T)  : Number( date );
  var DAY = MakeDay( YEAR, MONTH, DATE );

  return ( TimeClip(MakeDate(DAY, TimeWithinDay(T))) );
}
