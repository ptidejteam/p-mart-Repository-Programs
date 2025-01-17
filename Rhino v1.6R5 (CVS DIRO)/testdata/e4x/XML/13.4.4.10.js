/* -*- Mode: java; tab-width: 8; indent-tabs-mode: nil; c-basic-offset: 4 -*-
 *
 * ***** BEGIN LICENSE BLOCK *****
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
 * The Original Code is Rhino code, released
 * May 6, 1999.
 *
 * The Initial Developer of the Original Code is
 * Netscape Communications Corporation.
 * Portions created by the Initial Developer are Copyright (C) 1997-2000
 * the Initial Developer. All Rights Reserved.
 *
 * Contributor(s):
 *   Igor Bukanov
 *   Ethan Hugg
 *   Milen Nankov
 *   Martin Honnen
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

var nTest = 0;

START("13.4.4.10 - XML contains()");

TEST(++nTest, true, XML.prototype.hasOwnProperty("contains"));

emps =
<employees>
    <employee id="0"><name>Jim</name><age>25</age></employee>
    <employee id="1"><name>Joe</name><age>20</age></employee>
</employees>;

TEST(++nTest, true, emps.contains(emps));

// Martin - bug 289706

expect = 'gods.contains(\'Kibo\')==false && (gods==\'Kibo\')==false';

var gods = <gods>
  <god>Kibo</god>
  <god>Xibo</god>
</gods>;

printStatus('XML markup is:\r\n' + gods.toXMLString());

var string = 'Kibo';
actual = 'gods.contains(\'' + string + '\')==' + gods.contains(string);
actual += ' && ';
actual += '(gods==\'' + string + '\')==' + (gods == string);

TEST(++nTest, expect, actual);

// Martin - bug 289790

function containsTest(xmlObject, value) 
{
    var comparison    = (xmlObject == value);
    var containsCheck = xmlObject.contains(value);
    var result        = (comparison == containsCheck);

    printStatus('Comparing ' + xmlObject.nodeKind() + 
                ' against ' + (typeof value) + ':');

    printStatus('==: ' + comparison + '; contains: ' + containsCheck + 
                '; check ' + (result ? 'passed' : 'failed'));
    return result;
}

actual = containsTest(new XML('Kibo'), 'Kibo');
TEST(++nTest, true, actual);

actual = containsTest(<god>Kibo</god>, 'Kibo');
TEST(++nTest, true, actual);

END();
