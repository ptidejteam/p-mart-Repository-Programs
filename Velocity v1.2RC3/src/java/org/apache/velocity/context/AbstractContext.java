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

import java.io.Serializable;

import org.apache.velocity.context.Context;
import org.apache.velocity.context.InternalContextBase;

/**
 *  This class is the abstract base class for all conventional 
 *  Velocity Context  implementations.  Simply extend this class 
 *  and implement the abstract routines that access your preferred 
 *  storage method.
 *
 *  Takes care of context chaining.
 *
 *  Also handles / enforces policy on null keys and values :
 *
 *  <ul>
 *  <li> Null keys and values are accepted and basically dropped.
 *  <li> If you place an object into the context with a null key, it
 *        will be ignored and logged.
 *  <li> If you try to place a null into the context with any key, it
 *        will be dropped and logged.
 *  </ul>
 *
 *  The default implementation of this for application use is 
 *  org.apache.velocity.VelocityContext.
 *
 *  All thanks to Fedor for the chaining idea.
 *
 * @author <a href="mailto:geirm@optonline.net">Geir Magnusson Jr.</a>
 * @author <a href="mailto:fedor.karpelevitch@home.com">Fedor Karpelevitch</a>
 * @author <a href="mailto:jvanzyl@periapt.com">Jason van Zyl</a>
 * @version $Id: AbstractContext.java,v 1.7 2001/08/30 20:58:27 dlr Exp $
 */

public abstract class AbstractContext extends InternalContextBase 
    implements Context, Serializable
{
    /**
     *  the chained Context if any
     */
    private   Context  innerContext = null;
  
    /** 
     *  Implement to return a value from the context storage. 
     *  <br><br>
     *  The implementation of this method is required for proper
     *  operation of a Context implementation in general
     *  Velocity use.
     *  
     *  @param key key whose associated value is to be returned
     *  @return object stored in the context
     */
    public abstract Object internalGet( String key );

    /** 
     *  Implement to put a value into the context storage.
     *  <br><br>
     *  The implementation of this method is required for 
     *  proper operation of a Context implementation in
     *  general Velocity use.
     *
     *  @param key key with which to associate the value
     *  @param value value to be associated with the key
     *  @return previously stored value if exists, or null
     */
    public abstract Object internalPut( String key, Object value );

    /** 
     *  Implement to determine if a key is in the storage.
     *  <br><br>
     *  Currently, this method is not used internally by 
     *  the Velocity core. 
     *
     *   @param key key to test for existance 
     *   @return true if found, false if not
     */
    public abstract boolean internalContainsKey(Object key);

    /** 
     *  Implement to return an object array of key 
     *  strings from your storage.
     *  <br><br>
     *  Currently, this method is not used internally by
     *  the Velocity core.
     *
     *  @return array of keys
     */
    public abstract Object[] internalGetKeys();

    /** 
     *  I mplement to remove an item from your storage.
     *  <br><br>
     *  Currently, this method is not used internally by
     *  the Velocity core.
     *
     *  @param key key to remove
     *  @return object removed if exists, else null
     */
    public abstract Object internalRemove(Object key);

    /**
     *  default CTOR
     */
    public AbstractContext()
    {
    }        

    /**
     *  Chaining constructor accepts a Context argument.
     *  It will relay get() operations into this Context
     *  in the even the 'local' get() returns null.
     *  
     *  @param inner context to be chained
     */
    public AbstractContext( Context inner )
    {
        innerContext = inner;

        /*
         *  now, do a 'forward pull' of event cartridge so
         *  it's accessable, bringing to the top level.
         */

        if (innerContext instanceof InternalEventContext )
        {
            attachEventCartridge( ( (InternalEventContext) innerContext).getEventCartridge() );
        }
    }

    /**
     * Adds a name/value pair to the context. 
     * 
     * @param key   The name to key the provided value with.
     * @param value The corresponding value.
     * @return Object that was replaced in the the Context if
     *         applicable or null if not.
     */
    public Object put(String key, Object value)
    {
        /*
         * don't even continue if key or value is null
         */

        if (key == null)
        {
            return null;
        }
        else if (value == null)
        {
            return null;
        }
        
        return internalPut(key, value);
    }

    /**
     *  Gets the value corresponding to the provided key from the context.
     *
     *  Supports the chaining context mechanism.  If the 'local' context
     *  doesn't have the value, we try to get it from the chained context.
     *
     *  @param key The name of the desired value.
     *  @return    The value corresponding to the provided key or null if
     *             the key param is null.
     */
    public Object get(String key)
    {
        /*
         *  punt if key is null
         */

        if (key == null)
        {
            return null;
        }

        /*
         *  get the object for this key.  If null, and we are chaining another Context
         *  call the get() on it.
         */

        Object o = internalGet( key );

        if (o == null && innerContext != null)
        {
            o = innerContext.get( key );
        }
            
        return o;
    }        

    /**
     *  Indicates whether the specified key is in the context.  Provided for 
     *  debugging purposes.
     *
     * @param key The key to look for.
     * @return true if the key is in the context, false if not.
     */
    public boolean containsKey(Object key)
    {
        if (key == null)
        {
            return false;
        }

        return internalContainsKey(key);
    }        

    /**
     *  Get all the keys for the values in the context
     *  @return Object[] of keys in the Context. Does not return
     *          keys in chained context.
     */
    public Object[] getKeys()
    {
        return internalGetKeys();
    }

    /**
     * Removes the value associated with the specified key from the context.
     *
     * @param key The name of the value to remove.
     * @return    The value that the key was mapped to, or <code>null</code> 
     *            if unmapped.
     */
    public Object remove(Object key)
    {
        if (key == null)
        {
            return null;
        }

        return internalRemove(key);
    }   

    /**
     *  returns innerContext if one is chained
     *
     *  @return Context if chained, <code>null</code> if not
     */
    public Context getChainedContext()
    {
        return innerContext;
    }

}



