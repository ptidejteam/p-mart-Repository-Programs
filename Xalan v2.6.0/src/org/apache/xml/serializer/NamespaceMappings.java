/*
 * Copyright 2003-2004 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
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
/*
 * $Id: NamespaceMappings.java,v 1.1 2006/03/09 00:07:17 vauchers Exp $
 */
package org.apache.xml.serializer;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Stack;

import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

/**
 * This class keeps track of the currently defined namespaces. Conceptually the
 * prefix/uri/depth triplets are pushed on a stack pushed on a stack. The depth
 * indicates the nesting depth of the element for which the mapping was made.
 * 
 * <p>For example:
 * <pre>
 * <chapter xmlns:p1="def">
 *   <paragraph xmlns:p2="ghi">
 *      <sentance xmlns:p3="jkl">
 *      </sentance>
 *    </paragraph>
 *    <paragraph xlmns:p4="mno">
 *    </paragraph>
 * </chapter>
 * </pre>
 * 
 * When the <chapter> element is encounted the prefix "p1" associated with uri
 * "def" is pushed on the stack with depth 1.
 * When the first <paragraph> is encountered "p2" and "ghi" are pushed with
 * depth 2.
 * When the <sentance> is encountered "p3" and "jkl" are pushed with depth 3.
 * When </sentance> occurs the popNamespaces(3) will pop "p3"/"jkl" off the
 * stack.  Of course popNamespaces(2) would pop anything with depth 2 or
 * greater.
 * 
 * So prefix/uri pairs are pushed and poped off the stack as elements are
 * processed.  At any given moment of processing the currently visible prefixes
 * are on the stack and a prefix can be found given a uri, or a uri can be found
 * given a prefix.
 */
public class NamespaceMappings
{
    /**
     * This member is continually incremented when new prefixes need to be
     * generated. ("ns0"  "ns1" ...)
     */
    private int count = 0;

    /**
     * Stack of prefixes that have mappings
     * The top of this stack is the prefix that was last mapped to an URI
     * 
     * For every prefix pushed on this stack a corresponding integer is pushed
     * on the m_nodeStack. That way all prefixes pushed at the current depth can
     * be removed at the same time.      
     */
    private java.util.Stack m_prefixStack = new Stack();

    /**
     * Each entry (prefix) in this hashtable points to a Stack of URIs
     */
    private Hashtable m_namespaces = new Hashtable();

    /** 
     * The top of this stack contains the nested element depth
     * of the last declared a namespace.
     * Used to know how many prefix mappings to pop when leaving
     * the current element depth.
     * For every prefix mapping the current element depth is 
     * pushed on this stack, as well as the prefix on the m_prefixStack
     * That way all prefixes pushed at the current depth can be 
     * removed at the same time.
     * Used to ensure prefix/uri map scopes are closed correctly
     *
     */
    private Stack m_nodeStack = new Stack();

    private static final String EMPTYSTRING = "";
    private static final String XML_PREFIX = "xml"; // was "xmlns"

    /**
     * Default constructor
     * @see java.lang.Object#Object()
     */
    public NamespaceMappings()
    {
        initNamespaces();
    }

    /**
     * This method initializes the namespace object with appropriate stacks
     * and predefines a few prefix/uri pairs which always exist.
     */
    private void initNamespaces()
    {
 

        // Define the default namespace (initially maps to "" uri)
        Stack stack;
        m_namespaces.put(EMPTYSTRING, stack = new Stack());
        stack.push(EMPTYSTRING);
        m_prefixStack.push(EMPTYSTRING);

        m_namespaces.put(XML_PREFIX, stack = new Stack());
        stack.push("http://www.w3.org/XML/1998/namespace");
        m_prefixStack.push(XML_PREFIX);

        m_nodeStack.push(new Integer(-1));

    }

    /**
     * Use a namespace prefix to lookup a namespace URI.
     * 
     * @param prefix String the prefix of the namespace
     * @return the URI corresponding to the prefix
     */
    public String lookupNamespace(String prefix)
    {
        final Stack stack = (Stack) m_namespaces.get(prefix);
        return stack != null && !stack.isEmpty() ? (String) stack.peek() : null;
    }

    /**
     * Given a namespace uri, and the namespaces mappings for the 
     * current element, return the current prefix for that uri.
     * 
     * @param uri the namespace URI to be search for
     * @return an existing prefix that maps to the given URI, null if no prefix
     * maps to the given namespace URI.
     */
    public String lookupPrefix(String uri)
    {
        String foundPrefix = null;
        Enumeration prefixes = m_namespaces.keys();
        while (prefixes.hasMoreElements())
        {
            String prefix = (String) prefixes.nextElement();
            String uri2 = lookupNamespace(prefix);
            if (uri2 != null && uri2.equals(uri))
            {
                foundPrefix = prefix;
                break;
            }
        }
        return foundPrefix;
    }

    /**
     * Undeclare the namespace that is currently pointed to by a given prefix
     */
    public boolean popNamespace(String prefix)
    {
        // Prefixes "xml" and "xmlns" cannot be redefined
        if (prefix.startsWith(XML_PREFIX))
        {
            return false;
        }

        Stack stack;
        if ((stack = (Stack) m_namespaces.get(prefix)) != null)
        {
            stack.pop();
            return true;
        }
        return false;
    }

    /**
     * Declare a prefix to point to a namespace URI
     * @param prefix a String with the prefix for a qualified name
     * @param uri a String with the uri to which the prefix is to map
     * @param elemDepth the depth of current declaration
     */
    public boolean pushNamespace(String prefix, String uri, int elemDepth)
    {
        // Prefixes "xml" and "xmlns" cannot be redefined
        if (prefix.startsWith(XML_PREFIX))
        {
            return false;
        }

        Stack stack;
        // Get the stack that contains URIs for the specified prefix
        if ((stack = (Stack) m_namespaces.get(prefix)) == null)
        {
            m_namespaces.put(prefix, stack = new Stack());
        }

        if (!stack.empty() && uri.equals(stack.peek()))
        {
            return false;
        }

        stack.push(uri);
        m_prefixStack.push(prefix);
        m_nodeStack.push(new Integer(elemDepth));
        return true;
    }

    /**
     * Pop, or undeclare all namespace definitions that are currently
     * declared at the given element depth, or deepter.
     * @param elemDepth the element depth for which mappings declared at this
     * depth or deeper will no longer be valid
     * @param saxHandler The ContentHandler to notify of any endPrefixMapping()
     * calls.  This parameter can be null.
     */
    public void popNamespaces(int elemDepth, ContentHandler saxHandler)
    {
        while (true)
        {
            if (m_nodeStack.isEmpty())
                return;
            Integer i = (Integer) (m_nodeStack.peek());
            if (i.intValue() < elemDepth)
                return;
            /* the depth of the declared mapping is elemDepth or deeper
             * so get rid of it
             */

            m_nodeStack.pop();
            final String prefix = (String) m_prefixStack.pop(); 
            popNamespace(prefix);
            if (saxHandler != null)
            {
                try
                {
                    saxHandler.endPrefixMapping(prefix);
                }
                catch (SAXException e)
                {
                    // not much we can do if they aren't willing to listen
                }
            }
               
        }
    }

    /**
     * Generate a new namespace prefix ( ns0, ns1 ...) not used before
     * @return String a new namespace prefix ( ns0, ns1, ns2 ...)
     */
    public String generateNextPrefix()
    {
        return "ns" + (count++);
    }

 
    /**
     * This method makes a clone of this object.
     *
     */
    public Object clone() throws CloneNotSupportedException {
        NamespaceMappings clone = new NamespaceMappings();
        clone.m_prefixStack = (Stack)m_prefixStack.clone();
        clone.m_nodeStack = (Stack) m_nodeStack.clone();
        clone.m_namespaces = (Hashtable) m_namespaces.clone();
        
        clone.count = count;
        return clone;
        
    }
    
    public final void reset()
    {
        this.count = 0;
        this.m_namespaces.clear();
        this.m_nodeStack.clear();
        this.m_prefixStack.clear();
        
        initNamespaces();
    }

}
