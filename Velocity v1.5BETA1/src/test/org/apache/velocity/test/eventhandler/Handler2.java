package org.apache.velocity.test.eventhandler;

/*
 * Copyright 2001-2004 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License")
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


import org.apache.velocity.app.event.IncludeEventHandler;
import org.apache.velocity.app.event.MethodExceptionEventHandler;
import org.apache.velocity.app.event.NullSetEventHandler;
import org.apache.velocity.app.event.ReferenceInsertionEventHandler;

/**
 * This is a test set of event handlers, used to test event handler sequences.
 *
 * @author <a href="mailto:wglass@forio.com">Will Glass-Husain</a>
 * @version $Id: Handler2.java 291585 2005-09-26 08:56:23Z henning $
 */
public class Handler2
    implements NullSetEventHandler, ReferenceInsertionEventHandler, MethodExceptionEventHandler, IncludeEventHandler {

    /**
     * always log
     */
    public boolean shouldLogOnNullSet(String lhs, String rhs)
    {
        return true;
    }

    /**
     * convert output to upper case
     */
    public Object referenceInsert(String reference, Object value)
    {
        if (value == null)
            return null;
        else
            return value.toString().toUpperCase();
    }

    /**
     * print the exception
     */
    public Object methodException(Class claz, String method, Exception e) throws Exception
    {
        return "Exception: " + e;
    }

    /*
     * redirect all requests to a new directory "subdir" (simulates localization).
     */
    public String includeEvent(
        String includeResourcePath,
        String currentResourcePath,
        String directiveName)
    {

        return "subdir/" + includeResourcePath;

    }

}
