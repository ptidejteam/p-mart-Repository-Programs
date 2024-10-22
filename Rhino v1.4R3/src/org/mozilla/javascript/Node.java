/* -*- Mode: java; tab-width: 8; indent-tabs-mode: nil; c-basic-offset: 4 -*-
 *
 * The contents of this file are subject to the Netscape Public License
 * Version 1.0 (the "NPL"); you may not use this file except in
 * compliance with the NPL.  You may obtain a copy of the NPL at
 * http://www.mozilla.org/NPL/
 *
 * Software distributed under the NPL is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the NPL
 * for the specific language governing rights and limitations under the
 * NPL.
 *
 * The Initial Developer of this code under the NPL is Netscape
 * Communications Corporation.  Portions created by Netscape are
 * Copyright (C) 1997-1999 Netscape Communications Corporation.  All Rights
 * Reserved.
 */

package org.mozilla.javascript;

import java.util.*;

/**
 * This class implements the root of the intermediate representation.
 *
 * @author Norris Boyd
 * @author Mike McCabe
 */

public class Node implements Cloneable {

    public Node(int nodeType) {
        type = nodeType;
    }

    public Node(int nodeType, Node child) {
        type = nodeType;
        first = last = child;
        child.next = null;
    }

    public Node(int nodeType, Node left, Node right) {
        type = nodeType;
        first = left;
        last = right;
        left.next = right;
        right.next = null;
    }

    public Node(int nodeType, Node left, Node mid, Node right) {
        type = nodeType;
        first = left;
        last = right;
        left.next = mid;
        mid.next = right;
        right.next = null;
    }

    public Node(int nodeType, Object datum) {
        type = nodeType;
        this.datum = datum;
    }

    public Node(int nodeType, Node child, Object datum) {
        this(nodeType, child);
        this.datum = datum;
    }

    public Node(int nodeType, Node left, Node right, Object datum) {
        this(nodeType, left, right);
        this.datum = datum;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public boolean hasChildren() {
        return first != null;
    }

    public Node getFirstChild() {
        return first;
    }

    public Node getLastChild() {
        return last;
    }

    public Node getNextSibling() {
        return next;
    }

    public Node getChildBefore(Node child) {
        if (child == first)
            return null;
        Node n = first;
        while (n.next != child) {
            n = n.next;
            if (n == null)
                throw new RuntimeException("node is not a child");
        }
        return n;
    }

    public Node getLastSibling() {
        Node n = this;
        while (n.next != null) {
            n = n.next;
        }
        return n;
    }

    public ShallowNodeIterator getChildIterator() {
        return new ShallowNodeIterator(first);
    }

    public PreorderNodeIterator getPreorderIterator() {
        return new PreorderNodeIterator(this);
    }

    public void addChildToFront(Node child) {
        child.next = first;
        first = child;
        if (last == null) {
            last = child;
        }
    }

    public void addChildToBack(Node child) {
        child.next = null;
        if (last == null) {
            first = last = child;
            return;
        }
        last.next = child;
        last = child;
    }

    public void addChildrenToFront(Node children) {
        Node lastSib = children.getLastSibling();
        lastSib.next = first;
        first = children;
        if (last == null) {
            last = lastSib;
        }
    }

    public void addChildrenToBack(Node children) {
        if (last != null) {
            last.next = children;
        }
        last = children.getLastSibling();
        if (first == null) {
            first = children;
        }
    }

    /**
     * Add 'child' before 'node'.
     */
    public void addChildBefore(Node newChild, Node node) {
        if (newChild.next != null)
            throw new RuntimeException(
                      "newChild had siblings in addChildBefore");
        if (first == node) {
            newChild.next = first;
            first = newChild;
            return;
        }
        Node prev = getChildBefore(node);
        addChildAfter(newChild, prev);
    }

    /**
     * Add 'child' after 'node'.
     */
    public void addChildAfter(Node newChild, Node node) {
        if (newChild.next != null)
            throw new RuntimeException(
                      "newChild had siblings in addChildAfter");
        newChild.next = node.next;
        node.next = newChild;
        if (last == node)
            last = newChild;
    }

    public void removeChild(Node child) {
        Node prev = getChildBefore(child);
        if (prev == null)
            first = first.next;
        else
            prev.next = child.next;
        if (child == last) last = prev;
        child.next = null;
    }

    public void replaceChild(Node child, Node newChild) {
        newChild.next = child.next;
        if (child == first) {
            first = newChild;
        } else {
            Node prev = getChildBefore(child);
            prev.next = newChild;
        }
        if (child == last)
            last = newChild;
        child.next = null;
    }

    public static final int
        TARGET_PROP       =  1,
        BREAK_PROP        =  2,
        CONTINUE_PROP     =  3,
        ENUM_PROP         =  4,
        FUNCTION_PROP     =  5,
        TEMP_PROP         =  6,
        LOCAL_PROP        =  7,
        CODEOFFSET_PROP   =  8,
        FIXUPS_PROP       =  9,
        VARS_PROP         = 10,
        USES_PROP         = 11,
        REGEXP_PROP       = 12,
        CASES_PROP        = 13,
        DEFAULT_PROP      = 14,
        CASEARRAY_PROP    = 15,
        SOURCENAME_PROP   = 16,
        SOURCE_PROP       = 17,
        TYPE_PROP         = 18,
        SPECIAL_PROP_PROP = 19,
        LABEL_PROP        = 20,
        FINALLY_PROP      = 21,
        LOCALCOUNT_PROP   = 22,
    /*
        the following properties are defined and manipulated by the
        optimizer -
        TARGETBLOCK_PROP - the block referenced by a branch node
        VARIABLE_PROP - the variable referenced by a BIND or NAME node
        LASTUSE_PROP - that variable node is the last reference before
                        a new def or the end of the block
        ISNUMBER_PROP - this node generates code on Number children and
                        delivers a Number result (as opposed to Objects)
        DIRECTCALL_PROP - this call node should emit code to test the function
                          object against the known class and call diret if it
                          matches.
    */

        TARGETBLOCK_PROP  = 23,
        VARIABLE_PROP     = 24,
        LASTUSE_PROP      = 25,
        ISNUMBER_PROP     = 26,
        DIRECTCALL_PROP   = 27,

        BASE_LINENO_PROP  = 28,
        END_LINENO_PROP   = 29,
        SPECIALCALL_PROP  = 30;

    public static final int    // this value of the ISNUMBER_PROP specifies
        BOTH = 0,               // which of the children are Number types
        LEFT = 1,
        RIGHT = 2;

    private static String propNames[];
    
    private static final String propToString(int propType) {
        if (Context.printTrees && propNames == null) {
            // If Context.printTrees is false, the compiler
            // can remove all these strings.
            String[] a = {
                "TARGET",
                "BREAK",
                "CONTINUE",
                "ENUM",
                "FUNCTION",
                "TEMP",
                "LOCAL",
                "CODEOFFSET",
                "FIXUPS",
                "VARS",
                "USES",
                "REGEXP",
                "SWITCHES",
                "CASES",
                "DEFAULT",
                "CASEARRAY",
                "SOURCENAME",
                "SOURCE",
                "TYPE",
                "SPECIAL_PROP",
                "LABEL",
                "FINALLY",
                "LOCALCOUNT",
                "TARGETBLOCK",
                "VARIABLE",
                "LASTUSE",
                "ISNUMBER",
                "DIRECTCALL",
                "BASE_LINENO",
                "END_LINENO",
                "SPECIALCALL"
            };
            propNames = a;
        }
        return propNames[propType];
    }

    public Object getProp(int propType) {
        if (props == null)
            return null;
        return props.get(new Integer(propType));
    }

    public void putProp(int propType, Object prop) {
        if (props == null)
            props = new Hashtable(2);
        if (prop == null)
            props.remove(new Integer(propType));
        else
            props.put(new Integer(propType), prop);
    }

    public Object getDatum() {
        return datum;
    }

    public void setDatum(Object datum) {
        this.datum = datum;
    }

    public int getInt() {
        return ((Number) datum).intValue();
    }

    public double getDouble() {
        return ((Number) datum).doubleValue();
    }

    public long getLong() {
        return ((Number) datum).longValue();
    }

    public String getString() {
        return (String) datum;
    }

    public Node cloneNode() {
        Node result;
        try {
            result = (Node) super.clone();
            result.next = null;
            result.first = null;
            result.last = null;
        }
        catch (CloneNotSupportedException e) {
            throw new RuntimeException(e.getMessage());
        }
        return result;
    }

    public String toString() {
        if (Context.printTrees) {
            StringBuffer sb = new StringBuffer(TokenStream.tokenToName(type));
            if (type == TokenStream.TARGET) {
                sb.append(" ");
                sb.append(hashCode());
            }
            if (datum != null) {
                sb.append(' ');
                sb.append(datum.toString());
            }
            if (props == null)
                return sb.toString();

            Enumeration keys = props.keys();
            Enumeration elems = props.elements();
            while (keys.hasMoreElements()) {
                Integer key = (Integer) keys.nextElement();
                Object elem = elems.nextElement();
                sb.append(" [");
                sb.append(propToString(key.intValue()));
                sb.append(": ");
                switch (key.intValue()) {
                    case FIXUPS_PROP :      // can't add this as it recurses
                        sb.append("fixups property");
                        break;
                    case SOURCE_PROP :      // can't add this as it has unprintables
                        sb.append("source property");
                        break;
                    case TARGETBLOCK_PROP : // can't add this as it recurses
                        sb.append("target block property");
                        break;
                    case LASTUSE_PROP :     // can't add this as it is dull
                        sb.append("last use property");
                        break;
                    default :
                        sb.append(elem.toString());
                        break;
                }
                sb.append("]");
            }
            return sb.toString();
        }
        return null;
    }

    public String toStringTree() {
        return toStringTreeHelper(0);
    }
    

    private String toStringTreeHelper(int level) {
        if (Context.printTrees) {
            StringBuffer s = new StringBuffer();
            for (int i=0; i < level; i++) {
                s.append("    ");
            }
            s.append(toString());
            s.append('\n');
            ShallowNodeIterator iterator = getChildIterator();
            if (iterator != null) {
                while (iterator.hasMoreElements()) {
                    Node n = (Node) iterator.nextElement();
                    if (n.getType() == TokenStream.FUNCTION) {
                        Node p = (Node) n.getProp(Node.FUNCTION_PROP);
                        if (p != null)
                            n = p;
                    }
                    s.append(n.toStringTreeHelper(level+1));
                }
            }
            return s.toString();
        }
        return "";
    }

    public Node getFirst()  { return first; }
    public Node getNext()   { return next; }

    protected int type;         // type of the node; TokenStream.NAME for example
    protected Node next;        // next sibling
    protected Node first;       // first element of a linked list of children
    protected Node last;        // last element of a linked list of children
    protected Hashtable props;
    protected Object datum;     // encapsulated data; depends on type
}

