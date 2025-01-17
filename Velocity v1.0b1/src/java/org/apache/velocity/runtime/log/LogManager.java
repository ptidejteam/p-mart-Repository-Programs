package org.apache.velocity.runtime.log;

/*
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2001 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution, if
 *    any, must include the following acknowlegement:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowlegement may appear in the software itself,
 *    if and wherever such third-party acknowlegements normally appear.
 *
 * 4. The names "The Jakarta Project", "Tomcat", and "Apache Software
 *    Foundation" must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache"
 *    nor may "Apache" appear in their names without prior written
 *    permission of the Apache Group.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */

import org.apache.velocity.runtime.Runtime;

/**
 * LogManager.java
 * This class is responsible for instantiating the correct LoggingSystem
 * Right now, it is hard coded with a single Logging System. Eventually
 * we will have more LoggingSystems.
 *
 * @author <a href="mailto:jvanzyl@periapt.com">Jason van Zyl</a>
 * @author <a href="mailto:jon@latchkey.com">Jon S. Stevens</a>
 * @author <a href="mailto:geirm@optonline.net">Geir Magnusson Jr.</a>
 * @version $Id: LogManager.java,v 1.5 2001/03/19 06:15:54 jon Exp $
 */
public class LogManager
{
    /**
     *  Creates a new logging system.  Uses the property
     *  RUNTIME_LOG_LOGSYSTEM_CLASS as the class to create.
     *  Note that the class created has to do its own
     *  initialization - there is no init() method called/
     */
    public static LogSystem createLogSystem()
        throws Exception
    {
        /*
         *  if a logSystem was set as a configuation value, use that
         */
        Object o = Runtime.getProperty( Runtime.RUNTIME_LOG_LOGSYSTEM );
        if (o != null && o instanceof LogSystem)
        {
            return (LogSystem) o;
        }
  
        /*
         *  otherwise, see if a class was specified
         */
        String claz = (String) Runtime.getProperty( 
            Runtime.RUNTIME_LOG_LOGSYSTEM_CLASS );
        
        if (claz != null || claz.length() > 0 )
        {
            o = Class.forName( claz ).newInstance();
            if (o instanceof LogSystem)
            {
                return (LogSystem) o;
            }
            else
            {
                Runtime.error("The specifid logger class " + claz + 
                    " isn't a valid LogSystem\n");
            }
        }
      
        /*
         *  if the above failed, then 
         *  make an Avalon log system
         */
        AvalonLogSystem als = new AvalonLogSystem();
        return als;
    }
}

