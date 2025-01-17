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

// API class

package org.mozilla.javascript;

import java.util.Hashtable;
import java.util.Enumeration;

/**
 * Manipulate a Scriptable object as if its prototype chain were flattened.
 * <p>
 * Compared to the Scriptable interface, FlattenedObject provides a view of
 * Scriptable objects that is easier to use and more closely matches a script
 * writer's view of a JavaScript object. <p>
 *
 * A FlattenedObject is "flattened" in the sense that multiple objects in a
 * prototype chain appear to have their properties all appear in the
 * FlattenedObject. <p>
 *
 * Another convenience provided by Flattened object is the ability to access
 * properties by a single java.lang.Object id. This id is then converted into
 * a String or an int before methods of the Scriptable object are called.
 *
 * @see org.mozilla.javascript.Scriptable
 *
 * @author Norris Boyd
 */

public class FlattenedObject {

    /**
     * Construct a new FlattenedObject.
     *
     * @param object the object to be viewed with flattened properties
     */
    public FlattenedObject(Scriptable object) {
        this.obj = object;
    }

    /**
     * Get the associated Scriptable object.
     */
    public Scriptable getObject() {
        return obj;
    }

    /**
     * Determine if a property exists in an object.
     *
     * This is a more convenient (and less efficient) form than
     * <code>Scriptable.has()</code>.
     * It returns true if and only if the property
     * exists in this object or any of the objects in its prototype
     * chain.
     *
     * @param id the property index, which may be either a String or a
     *           Number
     * @return true if and only if the property exists in the prototype
     *         chain
     * @see org.mozilla.javascript.Scriptable#has
     */
    public boolean hasProperty(Object id) {
        String stringId = ScriptRuntime.toString(id);
        String s = ScriptRuntime.getStringId(stringId);
        if (s == null)
            return getBase(obj, ScriptRuntime.getIntId(stringId)) != null;
        return getBase(obj, s) != null;
    }

    /**
     * Get a property of an object.
     * <p>
     * This is a more convenient (and less efficient) form than
     * <code>Scriptable.get()</code>. It corresponds exactly to the
     * expression <code>obj[id]</code> in JavaScript. This method
     * will traverse the prototype chain of an object to find the
     * property.<p>
     *
     * If the property does not exist in the object or its prototype
     * chain, the undefined value will be returned.
     *
     * @param id the property index; can be a String or a Number; the
     *           String may contain characters representing a number
     * @return the value of the property or the undefined value
     * @see org.mozilla.javascript.Scriptable#get
     * @see org.mozilla.javascript.Context#getUndefinedValue
     */
    public Object getProperty(Object id) {
        String s = ScriptRuntime.getStringId(id);
        int index = s == null ? ScriptRuntime.getIntId(id) : 0;
        Scriptable m = obj;
        Object result;
        for(;;) {
            result = s == null ? m.get(index, obj) : m.get(s, obj);
            if (result != Scriptable.NOT_FOUND)
                break;
            m = m.getPrototype();
            if (m == null)
                return Undefined.instance;
        }
        if (result instanceof Scriptable)
            return new FlattenedObject((Scriptable) result);
        return result;
    }

    /**
     * Set a property of an object.
     *
     * This is a more convenient (and less efficient) form than that
     * provided in Scriptable. It corresponds exactly to the
     * expression <code>obj[id] = val</code> in JavaScript.<p>
     *
     * @param id the property index, which may be either a String or
     *           a Number
     * @param value the value of the property
     * @see org.mozilla.javascript.Scriptable#put
     */
    public void putProperty(Object id, Object value) {
        String s = ScriptRuntime.getStringId(id);
        if (value instanceof FlattenedObject)
            value = ((FlattenedObject) value).getObject();
        Scriptable x;
        if (s == null) {
            int index = ScriptRuntime.getIntId(id);
            x = getBase(obj, index);
            if (x == null)
                x = obj;
            x.put(index, obj, value);
            return;
        }
        x = getBase(obj, s);
        if (x == null)
            x = obj;
        x.put(s, obj, value);
    }

    /**
     * Remove a property.
     *
     * This method provides the functionality of the <code>delete</code>
     * operator in JavaScript.
     *
     * @param id the property index, which may be either a String or
     *           a Number
     * @return true if the property didn't exist, or existed and was removed
     * @see org.mozilla.javascript.Scriptable#delete
     */
    public boolean deleteProperty(Object id) {
        String s = ScriptRuntime.getStringId(id);
        if (s == null) {
            int index = ScriptRuntime.getIntId(id);
            Scriptable base = getBase(obj, index);
            if (base == null)
                return true;
            base.delete(index);
            return !base.has(index, base);
        }
        Scriptable base = getBase(obj, s);
        if (base == null)
            return true;
        base.delete(s);
        return !base.has(s, base);
    }

    /**
     * Return an array that contains the ids of the properties.
     *
     * <p>This method will walk the prototype chain and collect the
     * ids of all objects in the prototype chain.<p>
     *
     * If an id appears in more than one object in the prototype chain,
     * it will only be in the array once. (So all the entries in the
     * array will be unique respective to equals().)
     *
     * @see org.mozilla.javascript.Scriptable#getIds
     */
    public Object[] getIds() {
        Hashtable h = new Hashtable(11);
        Scriptable m = obj;
        while (m != null) {
            Object[] e = m.getIds();
            for (int i=0; i < e.length; i++) {
                h.put(e[i], Boolean.TRUE);
            }
            m = m.getPrototype();
        }
        Enumeration keys = h.keys();
        Object elem;
        Object[] result = new Object[h.size()];
        int index = 0;
        while (keys.hasMoreElements()) {
            elem = keys.nextElement();
            result[index++] = elem;
        }
        return result;
    }

    /**
     * Consider this object to be a function, and call it.
     *
     * @param cx the current Context for this thread
     * @param thisObj the JavaScript 'this' for the call
     * @param args the arguments for the call
     * @return the result of the JavaScript function call
     * @exception NotAFunctionException if this object is not a function
     * @exception JavaScriptException if an uncaught JavaScript exception
     *            occurred while executing the function
     * @see org.mozilla.javascript.Function#call
     */
    public Object call(Context cx, Scriptable thisObj, Object[] args)
        throws NotAFunctionException,
               JavaScriptException
    {
        if (!(obj instanceof Function)) {
            throw new NotAFunctionException();
        }
        return ScriptRuntime.call(cx, obj, thisObj, args);
    }

    /**
     * Consider this object to be a function, and invoke it as a
     * constructor call.
     *
     * @param cx the current Context for this thread
     * @param args the arguments for the constructor call
     * @return the allocated object
     * @exception NotAFunctionException if this object is not a function
     * @exception JavaScriptException if an uncaught JavaScript exception
     *            occurred while executing the constructor
     * @see org.mozilla.javascript.Function#construct
     */
    public Scriptable construct(Context cx, Object[] args)
        throws NotAFunctionException,
               JavaScriptException
    {
        if (!(obj instanceof Function)) {
            throw new NotAFunctionException();
        }
        return ScriptRuntime.newObject(cx, obj, args);
    }

    /**
     * Get the property indicated by the id, and invoke it with the
     * specified arguments.
     * <p>
     * For example, for a FlattenedObject <code>obj</code>,
     * and a Java array <code>a</code> consisting of a single string
     * <code>"hi"</code>, the call <pre>
     * obj.callMethod("m", a)</pre>
     * is equivalent to the JavaScript code <code>obj.m("hi")</code>.<p>
     *
     * If the property is not found or is not a function, an
     * exception will be thrown.
     *
     * @param id the Number or String to use to find the function property
     *           to call
     * @param args the arguments for the constructor call
     * @return the result of the call
     * @exception PropertyException if the designated property
     *            was not found
     * @exception NotAFunctionException if this object is not a function
     * @exception JavaScriptException if an uncaught JavaScript exception
     *            occurred while executing the method
     * @see org.mozilla.javascript.Function#call
     */
    public Object callMethod(Object id, Object[] args)
        throws PropertyException,
               NotAFunctionException,
               JavaScriptException
    {
        if (!hasProperty(id)) {
            throw new PropertyException(
                Context.getMessage("msg.prop.not.found", null));
        }
        Object o = getProperty(id);
        if (o instanceof FlattenedObject)
            return ((FlattenedObject) o).call(Context.getContext(), obj, args);
        throw new NotAFunctionException();
    }

    /****** End of API *******/

    private static Scriptable getBase(Scriptable obj, String s) {
        Scriptable m = obj;
        while (m != null) {
            if (m.has(s, obj))
                return m;
            m = m.getPrototype();
        }
        return null;
    }

    private static Scriptable getBase(Scriptable obj, int index) {
        Scriptable m = obj;
        while (m != null) {
            if (m.has(index, obj))
                return m;
            m = m.getPrototype();
        }
        return null;
    }

    private Scriptable obj;
}

