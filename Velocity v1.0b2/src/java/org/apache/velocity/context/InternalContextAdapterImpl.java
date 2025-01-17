package org.apache.velocity.context;

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
 * 4. The names "The Jakarta Project", "Velocity", and "Apache Software
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

import org.apache.velocity.util.introspection.IntrospectionCacheData;

/**
 *  This adapter class is the container for all context types for internal
 *  use.  The AST now uses this class rather than the app-level Context
 *  interface to allow flexibility in the future.
 *
 *  Currently, we have two context interfaces which must be supported :
 *  <ul>
 *  <li> Context : used for application/template data access
 *  <li> InternalContext : used for internal housekeeping and caching
 *  </ul>
 *
 *  This class implements the two interfaces to ensure that all methods are 
 *  supported.  When adding to the interfaces, or adding more context 
 *  functionality, the interface is the primary definition, so alter that first
 *  and then all classes as necessary.  As of this writing, this would be 
 *  the only class affected by changes to InternalContext
 *
 *  This class ensures that an InternalContextBase is available for internal
 *  use.  If an application constructs their own Context-implementing
 *  object w/o subclassing AbstractContext, it may be that support for
 *  InternalContext is not available.  Therefore, InternalContextAdapter will
 *  create an InternalContextBase if necessary for this support.  Note that 
 *  if this is necessary, internal information such as node-cache data will be
 *  lost from use to use of the context.  This may or may not be important,
 *  depending upon application.
 * 
 *
 * @author <a href="mailto:geirm@optonline.net">Geir Magnusson Jr.</a>
 * @version $Id: InternalContextAdapterImpl.java,v 1.3 2001/03/05 11:21:42 jvanzyl Exp $
 */

public final class InternalContextAdapterImpl implements InternalContextAdapter
{
    /**  the Context that we are wrapping */
    Context context = null;
    
    /** the ICB we are wrapping.  We may need to make one */
    InternalHousekeepingContext icb = null;

    /**
     *  CTOR takes a Context and wraps it, delegating all 'data' calls 
     *  to it.
     * 
     *  For support of internal contexts, it will create an InternalContextBase
     *  if need be.
     */
    public InternalContextAdapterImpl( Context c )
    {
        context = c;

        if ( !( c instanceof InternalHousekeepingContext ))
        {
            System.out.println("Woo! no ICB!");
            icb = new InternalContextBase();
        }
        else
        {
            icb = (InternalHousekeepingContext) context;
        }
    }

    public InternalContextAdapterImpl( Context c, boolean avoid )
    {
        context = c;
        icb = (InternalHousekeepingContext) context;
    }

    public Context getInternalUserContext()
    {
        return context;
    }

    /* --- InternalContext interface methods --- */

    public void pushCurrentTemplateName( String s )
    {
        icb.pushCurrentTemplateName( s );
    }

    public void popCurrentTemplateName()
    {
        icb.popCurrentTemplateName();
    }
  
    public String getCurrentTemplateName()
    {
        return icb.getCurrentTemplateName();
    }

    public Object[] getTemplateNameStack()
    {
        return icb.getTemplateNameStack();
    }

    public IntrospectionCacheData icacheGet( Object key )
    {
        return icb.icacheGet( key );
    }
    
    public void icachePut( Object key, IntrospectionCacheData o )
    {
        icb.icachePut( key, o );
    }

    /* ---  Context interface methods --- */

    public Object put(String key, Object value)
    {
        return context.put( key , value );
    }

    public Object get(String key)
    {
        return context.get( key );
    }

    public boolean containsKey(Object key)
    {
        return context.containsKey( key );
    }

    public Object[] getKeys()
    {
        return context.getKeys();
    }

    public Object remove(Object key)
    {
        return context.remove( key );
    }

    public InternalContextAdapter getBaseContext()
    {
        return this;
    }
}
