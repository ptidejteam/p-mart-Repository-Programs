package org.apache.velocity.context;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.    
 */

import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.velocity.app.event.EventCartridge;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.VelocityException;
import org.apache.velocity.runtime.RuntimeServices;
import org.apache.velocity.runtime.parser.ParserTreeConstants;
import org.apache.velocity.runtime.parser.node.ASTReference;
import org.apache.velocity.runtime.parser.node.Node;
import org.apache.velocity.runtime.resource.Resource;
import org.apache.velocity.util.introspection.IntrospectionCacheData;

/**
 * Context for Velocity macro arguments.
 * 
 * This special context combines ideas of earlier VMContext and VMProxyArgs
 * by implementing routing functionality internally. This significantly
 * reduces memory allocation upon macro invocations.
 * Since the macro AST is now shared and RuntimeMacro directive is used,
 * the earlier implementation of precalculating VMProxyArgs would not work.
 * 
 * See <a href="http://issues.apache.org/jira/browse/VELOCITY-607">Issue 607</a>
 * for more info on this class.
 * @author <a href="mailto:wyla@removeme.sci.fi">Jarkko Viinamaki</a>
 * @version $Id$
 * @since 1.6
 */
public class ProxyVMContext extends ChainedInternalContextAdapter
{
    /** container for our macro AST node arguments. Size must be power of 2. */
    Map vmproxyhash = new HashMap(8, 0.8f);

    /** container for any local or constant macro arguments. Size must be power of 2. */
    Map localcontext = new HashMap(8, 0.8f);;

    /** context that we are wrapping */
    InternalContextAdapter wrappedContext;

    /** support for local context scope feature, where all references are local */
    private boolean localContextScope;

    /** needed for writing log entries. */
    private RuntimeServices rsvc;

    /**
     * @param inner Velocity context for processing
     * @param rsvc RuntimeServices provides logging reference
     * @param localContextScope if true, all references are set to be local
     */
    public ProxyVMContext(InternalContextAdapter inner,
                          RuntimeServices rsvc,
                          boolean localContextScope)
    {
        super(inner);

        this.localContextScope = localContextScope;
        this.rsvc = rsvc;

        wrappedContext = inner;
    }

    /**
     * Used to put Velocity macro arguments into this context. 
     * 
     * @param context rendering context
     * @param macroArgumentName name of the macro argument that we received
     * @param literalMacroArgumentName ".literal.$"+macroArgumentName
     * @param argumentValue actual value of the macro argument
     * 
     * @throws MethodInvocationException
     */
    public void addVMProxyArg(InternalContextAdapter context,
                              String macroArgumentName,
                              String literalMacroArgumentName,
                              Node argumentValue) throws MethodInvocationException
    {
        if (isConstant(argumentValue))
        {
            localcontext.put(macroArgumentName, argumentValue.value(context));
        }
        else
        {
            vmproxyhash.put(macroArgumentName, argumentValue);
            localcontext.put(literalMacroArgumentName, argumentValue);
        }
    }

    /**
     * AST nodes that are considered constants can be directly
     * saved into the context. Dynamic values are stored in
     * another argument hashmap.
     * 
     * @param node macro argument as AST node
     * @return true if the node is a constant value
     */
    private boolean isConstant(Node node)
    {
        switch (node.getType())
        {
            case ParserTreeConstants.JJTINTEGERRANGE:
            case ParserTreeConstants.JJTREFERENCE:
            case ParserTreeConstants.JJTOBJECTARRAY:
            case ParserTreeConstants.JJTMAP:
            case ParserTreeConstants.JJTSTRINGLITERAL:
            case ParserTreeConstants.JJTTEXT:
                return (false);
            default:
                return (true);
        }
    }

    /**
     * Impl of the Context.put() method.
     * 
     * @param key name of item to set
     * @param value object to set to key
     * @return old stored object
     */
    public Object put(final String key, final Object value)
    {
        return put(key, value, localContextScope);
    }

    /**
     * Allows callers to explicitly put objects in the local context, no matter what the
     * velocimacro.context.local setting says. Needed e.g. for loop variables in foreach.
     * 
     * @param key name of item to set.
     * @param value object to set to key.
     * @return old stored object
     */
    public Object localPut(final String key, final Object value)
    {
        return put(key, value, true);
    }

    /**
     * Internal put method to select between local and global scope.
     * 
     * @param key name of item to set
     * @param value object to set to key
     * @param forceLocal True forces the object into the local scope.
     * @return old stored object
     */
    protected Object put(final String key, final Object value, final boolean forceLocal)
    {
        Node astNode = (Node) vmproxyhash.get(key);

        if (astNode != null)
        {
            if (astNode.getType() == ParserTreeConstants.JJTREFERENCE)
            {
                ASTReference ref = (ASTReference) astNode;

                if (ref.jjtGetNumChildren() > 0)
                    ref.setValue(wrappedContext, value);
                else
                    wrappedContext.put(ref.getRootString(), value);

            }
            else
            {
                rsvc.getLog().error("ProxyVMContext.put() : New value cannot be assigned to a constant: "
                                    + key + " / " + get("$" + key + ".literal"));
            }
            return null;
        }
        else
        {
            if (forceLocal)
            {
                return localcontext.put(key, value);
            }
            else
            {
                if (localcontext.containsKey(key))
                {
                    return localcontext.put(key, value);
                }
                else
                {
                    return super.put(key, value);
                }
            }
        }
    }

    /**
     * Implementation of the Context.get() method.
     * 
     * @param key name of item to get
     * @return stored object or null
     */
    public Object get(String key)
    {
        Object o = null;

        Node astNode = (Node) vmproxyhash.get(key);

        if (astNode != null)
        {
            int type = astNode.getType();

            // if the macro argument (astNode) is a reference, we need to evaluate it
            // in case it is a multilevel node
            if (type == ParserTreeConstants.JJTREFERENCE)
            {
                ASTReference ref = (ASTReference) astNode;

                if (ref.jjtGetNumChildren() > 0)
                {
                    return ref.execute(null, wrappedContext);
                }
                else
                {
                    Object obj = wrappedContext.get(ref.getRootString());
                    if (obj == null && ref.strictRef)
                    {
                        if (!wrappedContext.containsKey(ref.getRootString()))
                        {
                            throw new MethodInvocationException("Parameter '" + ref.getRootString() 
                                + "' not defined", null, key, ref.getTemplateName(), 
                                ref.getLine(), ref.getColumn());
                        }
                    }
                    return obj;
                }
            }
            else if (type == ParserTreeConstants.JJTTEXT)
            {
                // this really shouldn't happen. text is just a throwaway arg for #foreach()
                try
                {
                    StringWriter writer = new StringWriter();
                    astNode.render(wrappedContext, writer);

                    return writer.toString();
                }
                catch (RuntimeException e)
                {
                    throw e;
                }
                catch (Exception e)
                {
                    String msg = "ProxyVMContext.get() : error rendering reference";
                    rsvc.getLog().error(msg, e);
                    throw new VelocityException(msg, e);
                }
            }
            else
            {
                // use value method to render other dynamic nodes
                return astNode.value(wrappedContext);
            }
        }
        else
        {
            o = localcontext.get(key);

            if (o == null)
            {
                o = super.get(key);
            }
        }

        return o;
    }

    /**
     * @see org.apache.velocity.context.Context#containsKey(java.lang.Object)
     */
    public boolean containsKey(Object key)
    {
      return vmproxyhash.containsKey(key)
          || localcontext.containsKey(key)
          || super.containsKey(key);
    }

    /**
     * @see org.apache.velocity.context.Context#getKeys()
     */
    public Object[] getKeys()
    {
        return vmproxyhash.keySet().toArray();
    }

    /**
     * @see org.apache.velocity.context.Context#remove(java.lang.Object)
     */
    public Object remove(Object key)
    {
        if (vmproxyhash.containsKey(key))
        {
            return vmproxyhash.remove(key);
        }
        else
        {
            if (localContextScope)
            {
                return localcontext.remove(key);
            }

            Object oldValue = localcontext.remove(key);
            if (oldValue == null)
            {
                oldValue = super.remove(key);
            }
            return oldValue;
        }
    }

}
