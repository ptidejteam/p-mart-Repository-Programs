/* -*- Mode: java; tab-width: 8; indent-tabs-mode: nil; c-basic-offset: 4 -*-
 *
 * The contents of this file are subject to the Netscape Public
 * License Version 1.1 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of
 * the License at http://www.mozilla.org/NPL/
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express oqr
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 * The Original Code is Rhino code, released
 * May 6, 1998.
 *
 * The Initial Developer of the Original Code is Netscape
 * Communications Corporation.  Portions created by Netscape are
 * Copyright (C) 1999 Netscape Communications Corporation. All
 * Rights Reserved.
 *
 * Contributor(s): 
 * Norris Boyd
 *
 * Alternatively, the contents of this file may be used under the
 * terms of the GNU Public License (the "GPL"), in which case the
 * provisions of the GPL are applicable instead of those above.
 * If you wish to allow use of your version of this file only
 * under the terms of the GPL and not to allow others to use your
 * version of this file under the NPL, indicate your decision by
 * deleting the provisions above and replace them with the notice
 * and other provisions required by the GPL.  If you do not delete
 * the provisions above, a recipient may use your version of this
 * file under either the NPL or the GPL.
 */

import org.mozilla.javascript.*;

/**
 * An example illustrating how to create a JavaScript object and retrieve
 * properties and call methods.
 * <p>
 * Output should be:
 * <pre>
 * count = 0
 * count = 1
 * resetCount
 * count = 0
 * </pre>
 */
public class CounterTest {

  public static void main(String[] args) throws Exception
  {
    Context cx = Context.enter();
    Scriptable scope = cx.initStandardObjects(null);
    ScriptableObject.defineClass(scope, Counter.class);

    Scriptable testCounter = cx.newObject(scope, "Counter");
    
    Object count = ScriptableObject.getProperty(testCounter, "count");
    System.out.println("count = " + count);

    count = ScriptableObject.getProperty(testCounter, "count");
    System.out.println("count = " + count);

    ScriptableObject.callMethod(testCounter, "resetCount", new Object[0]);
    System.out.println("resetCount");

    count = ScriptableObject.getProperty(testCounter, "count");
    System.out.println("count = " + count);

    Context.exit();
  } 

}
