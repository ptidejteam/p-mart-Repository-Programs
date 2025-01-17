/* -*- Mode: java; tab-width: 8; indent-tabs-mode: nil; c-basic-offset: 4 -*-
 *
 * The contents of this file are subject to the Netscape Public
 * License Version 1.1 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of
 * the License at http://www.mozilla.org/NPL/
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 * The Original Code is Rhino code, released
 * May 6, 1999.
 *
 * The Initial Developer of the Original Code is Netscape
 * Communications Corporation.  Portions created by Netscape are
 * Copyright (C) 1997-2000 Netscape Communications Corporation. All
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

// API class

package org.mozilla.javascript.debug;

import org.mozilla.javascript.*;

import java.util.Enumeration;

/**
 * This interface exposes debugging information from executable
 * code (either functions or top-level scripts).
 */
public interface DebuggableScript
{
    public boolean isTopLevel();

    /**
     * Returns true if this is a function, false if it is a script.
     */
    public boolean isFunction();

    /**
     * Get name of the function described by this script.
     * Return null or an empty string if this script is not function.
     */
    public String getFunctionName();

    /**
     * Get the name of the source (usually filename or URL)
     * of the script.
     */
    public String getSourceName();

    /**
     * Retutns true for functions constructed via <tt>Function(...)</tt>
     * or  eval scripts or any function defined by such functions or scripts
     */
    public boolean isGeneratedScript();

    /**
     * Get array containing the line numbers that
     * that can be passed to <code>DebugFrame.onLineChange()<code>.
     * Note that line order in the resulting array is arbitrary
     */
    public int[] getLineNumbers();

    public int getFunctionCount();

    public DebuggableScript getFunction(int index);

    public DebuggableScript getParent();

}
