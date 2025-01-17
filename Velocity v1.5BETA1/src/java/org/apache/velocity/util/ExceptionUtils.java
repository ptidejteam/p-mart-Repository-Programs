package org.apache.velocity.util;

/*
 * Copyright 2000-2004 The Apache Software Foundation.
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

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

/**
 * Use this to create a new Exception.  This will run under JDK 1.3 or greater.
 * However, it running under JDK 1.4 it will set the cause.
 *
 * @author <a href="mailto:isidore@setgame.com">Llewellyn Falco</a>
 */
public class ExceptionUtils
{
    private static boolean causesAllowed = true;

    /**
     * Create a new RuntimeException, setting the cause if possible.
     */
    public static RuntimeException createRuntimeException(
            String message, Throwable cause)
    {
        return (RuntimeException) createWithCause(
                RuntimeException.class, message, cause);
    }

    /**
     * Create a new Exception, setting the cause if possible.
     */
    public static Throwable createWithCause(Class clazz,
            String message, Throwable cause)
    {
        Throwable re = null;
        if (causesAllowed)
        {
            try
            {
                Constructor constructor = clazz
                        .getConstructor(new Class[]{String.class,
                                Throwable.class});
                re = (Throwable) constructor
                        .newInstance(new Object[]{message, cause});
            }
            catch (Exception e)
            {
                causesAllowed = false;
            }
        }
        if (re == null)
        {
            try
            {
                Constructor constructor = clazz
                        .getConstructor(new Class[]{String.class});
                re = (Throwable) constructor
                        .newInstance(new Object[]{message
                                + " caused by " + cause});
            }
            catch (Exception e)
            {
                throw new RuntimeException("Error caused " + e); // should be impossible
            }
        }
        return re;
    }

    /**
     * Set the cause of the Exception.  Will detect if this is not allowed.
     */
    public static void setCause(Throwable onObject, Throwable cause)
    {
        if (causesAllowed)
        {
            try
            {
                Method method = onObject.getClass().getMethod("initCause", new Class[]{Throwable.class});
                method.invoke(onObject, new Object[]{cause});
            }
            catch (Exception e)
            {
                causesAllowed = false;
            }
        }
    }
}
