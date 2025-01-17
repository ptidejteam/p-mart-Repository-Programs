/*
 * $Header: /home/cvs/jakarta-struts/src/share/org/apache/struts/config/FormPropertyConfig.java,v 1.16 2004/06/09 00:54:24 niallp Exp $
 * $Revision: 1.16 $
 * $Date: 2004/06/09 00:54:24 $
 *
 * Copyright 1999-2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package org.apache.struts.config;


import java.io.Serializable;
import java.lang.reflect.Array;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * <p>A JavaBean representing the configuration information of a
 * <code>&lt;form-property&gt;</code> element in a Struts
 * configuration file.<p>
 *
 * @version $Revision: 1.16 $ $Date: 2004/06/09 00:54:24 $
 * @since Struts 1.1
 */

public class FormPropertyConfig implements Serializable {

    /**
     * The logging instance
     */
    private static final Log log =
        LogFactory.getLog(FormPropertyConfig.class);


    // ----------------------------------------------------------- Constructors


    /**
     * Standard no-arguments constructor for dynamic instantiation.
     */
    public FormPropertyConfig() {

        super();

    }


    /**
     * Constructor that preconfigures the relevant properties.
     *
     * @param name Name of this property
     * @param type Fully qualified class name of this property
     * @param initial Initial value of this property (if any)
     */
    public FormPropertyConfig(String name, String type, String initial) {

        this(name, type, initial, 0);

    }


    /**
     * Constructor that preconfigures the relevant properties.
     *
     * @param name Name of this property
     * @param type Fully qualified class name of this property
     * @param initial Initial value of this property (if any)
     * @param size Size of the array to be created if this property is an
     *  array with no defined initial value
     */
    public FormPropertyConfig(String name, String type,
                              String initial, int size) {

        super();
        setName(name);
        setType(type);
        setInitial(initial);
        setSize(size);

    }


    // ----------------------------------------------------- Instance Variables


    /**
     * Has this component been completely configured?
     */
    protected boolean configured = false;


    // ------------------------------------------------------------- Properties


    /**
     * String representation of the initial value for this property.
     */
    protected String initial = null;

    public String getInitial() {
        return (this.initial);
    }

    public void setInitial(String initial) {
        if (configured) {
            throw new IllegalStateException("Configuration is frozen");
        }
        this.initial = initial;
    }


    /**
     * The JavaBean property name of the property described by this element.
     */
    protected String name = null;

    public String getName() {
        return (this.name);
    }

    public void setName(String name) {
        if (configured) {
            throw new IllegalStateException("Configuration is frozen");
        }
        this.name = name;
    }


    /**
     * <p>The size of the array to be created if this property is an array
     * type and there is no specified <code>initial</code> value.  This
     * value must be non-negative.</p>
     *
     * @since Struts 1.1
     */
    protected int size = 0;

    public int getSize() {
        return (this.size);
    }

    public void setSize(int size) {
        if (configured) {
            throw new IllegalStateException("Configuration is frozen");
        }
        if (size < 0) {
            throw new IllegalArgumentException("size < 0");
        }
        this.size = size;
    }
        


    /**
     * The fully qualified Java class name of the implementation class
     * of this bean property, optionally followed by <code>[]</code> to
     * indicate that the property is indexed.
     */
    protected String type = null;

    public String getType() {
        return (this.type);
    }

    public void setType(String type) {
        if (configured) {
            throw new IllegalStateException("Configuration is frozen");
        }
        this.type = type;
    }


    /**
     * Return a Class corresponds to the value specified for the
     * <code>type</code> property, taking into account the trailing "[]"
     * for arrays (as well as the ability to specify primitive Java types).
     */
    public Class getTypeClass() {

        // Identify the base class (in case an array was specified)
        String baseType = getType();
        boolean indexed = false;
        if (baseType.endsWith("[]")) {
            baseType = baseType.substring(0, baseType.length() - 2);
            indexed = true;
        }

        // Construct an appropriate Class instance for the base class
        Class baseClass = null;
        if ("boolean".equals(baseType)) {
            baseClass = Boolean.TYPE;
        } else if ("byte".equals(baseType)) {
            baseClass = Byte.TYPE;
        } else if ("char".equals(baseType)) {
            baseClass = Character.TYPE;
        } else if ("double".equals(baseType)) {
            baseClass = Double.TYPE;
        } else if ("float".equals(baseType)) {
            baseClass = Float.TYPE;
        } else if ("int".equals(baseType)) {
            baseClass = Integer.TYPE;
        } else if ("long".equals(baseType)) {
            baseClass = Long.TYPE;
        } else if ("short".equals(baseType)) {
            baseClass = Short.TYPE;
        } else {
            ClassLoader classLoader =
                Thread.currentThread().getContextClassLoader();
            if (classLoader == null) {
                classLoader = this.getClass().getClassLoader();
            }
            try {
                baseClass = classLoader.loadClass(baseType);
            } catch (Throwable t) {
                baseClass = null;
            }
        }

        // Return the base class or an array appropriately
        if (indexed) {
            return (Array.newInstance(baseClass, 0).getClass());
        } else {
            return (baseClass);
        }

    }



    // --------------------------------------------------------- Public Methods


    /**
     * <p>Return an object representing the initial value of this property.
     * This is calculated according to the following algorithm:</p>
     * <ul>
     * <li>If the value you have specified for the <code>type</code>
     *     property represents an array (i.e. it ends with "[]"):
     *     <ul>
     *     <li>If you have specified a value for the <code>initial</code>
     *         property, <code>ConvertUtils.convert</code> will be
     *         called to convert it into an instance of the specified
     *         array type.</li>
     *     <li>If you have not specified a value for the <code>initial</code>
     *         property, an array of the length specified by the
     *         <code>size</code> property will be created. Each element
     *         of the array will be instantiated via the zero-args constructor
     *         on the specified class (if any). Otherwise, <code>null</code>
     *         will be returned.</li>
     *     </ul></li>
     * <li>If the value you have specified for the <code>type</code>
     *     property does not represent an array:
     *     <ul>
     *     <li>If you have specified a value for the <code>initial</code>
     *         property, <code>ConvertUtils.convert</code>
     *         will be called to convert it into an object instance.</li>
     *     <li>If you have not specified a value for the <code>initial</code>
     *         attribute, Struts will instantiate an instance via the
     *         zero-args constructor on the specified class (if any).
     *         Otherwise, <code>null</code> will be returned.</li>
     *     </ul></li>
     * </ul>
     */
    public Object initial() {

        Object initialValue = null;
        try {
            Class clazz = getTypeClass();
            if (clazz.isArray()) {
                if (initial != null) {
                    initialValue =
                        ConvertUtils.convert(initial, clazz);
                } else {
                    initialValue =
                        Array.newInstance(clazz.getComponentType(), size);
                    if (!(clazz.getComponentType().isPrimitive())) {
                        for (int i = 0; i < size; i++) {
                            try {
                                Array.set(initialValue, i,
                                      clazz.getComponentType().newInstance());
                            } catch (Throwable t) {
                                log.error("Unable to create instance of " + clazz.getName() +
                                                                        " for property=" + name+
                                                                        ", type=" + type +
                                                                        ", initial=" + initial +
                                                                        ", size=" + size + ".");
                                //FIXME: Should we just dump the entire application/module ?
                            }
                        }
                    }
                }
            } else {
                if (initial != null) {
                    initialValue = ConvertUtils.convert(initial, clazz);
                } else {
                    initialValue = clazz.newInstance();
                }
            }
        } catch (Throwable t) {
            initialValue = null;
        }
        return (initialValue);

    }


    /**
     * Freeze the configuration of this component.
     */
    public void freeze() {

        configured = true;

    }


    /**
     * Return a String representation of this object.
     */
    public String toString() {

        StringBuffer sb = new StringBuffer("FormPropertyConfig[");
        sb.append("name=");
        sb.append(this.name);
        sb.append(",type=");
        sb.append(this.type);
        sb.append(",initial=");
        sb.append(this.initial);
        sb.append("]");
        return (sb.toString());

    }


}
