/*
 * $Header: /home/cvs/jakarta-struts/src/share/org/apache/struts/taglib/logic/IterateTag.java,v 1.27 2004/03/14 06:23:44 sraeburn Exp $
 * $Revision: 1.27 $
 * $Date: 2004/03/14 06:23:44 $
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

package org.apache.struts.taglib.logic;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;

import org.apache.commons.collections.IteratorUtils;
import org.apache.struts.taglib.TagUtils;
import org.apache.struts.util.MessageResources;

/**
 * Custom tag that iterates the elements of a collection, which can be
 * either an attribute or the property of an attribute.  The collection
 * can be any of the following:  an array of objects, an Enumeration,
 * an Iterator, a Collection (which includes Lists, Sets and Vectors),
 * or a Map (which includes Hashtables) whose elements will be iterated over.
 *
 * @version $Revision: 1.27 $ $Date: 2004/03/14 06:23:44 $
 */

public class IterateTag extends BodyTagSupport {

    // ----------------------------------------------------- Instance Variables

    /**
     * Iterator of the elements of this collection, while we are actually
     * running.
     */
    protected Iterator iterator = null;

    /**
     * The number of elements we have already rendered.
     */
    protected int lengthCount = 0;

    /**
     * The actual length value (calculated in the start tag).
     */
    protected int lengthValue = 0;

    /**
     * The message resources for this package.
     */
    protected static MessageResources messages =
        MessageResources.getMessageResources("org.apache.struts.taglib.logic.LocalStrings");

    /**
     * The actual offset value (calculated in the start tag).
     */
    protected int offsetValue = 0;

    /**
     * Has this tag instance been started?
     */
    protected boolean started = false;

    // ------------------------------------------------------------- Properties

    /**
     * The collection over which we will be iterating.
     */
    protected Object collection = null;

    public Object getCollection() {
        return (this.collection);
    }

    public void setCollection(Object collection) {
        this.collection = collection;
    }

    /**
     * The name of the scripting variable to be exposed.
     */
    protected String id = null;

    public String getId() {
        return (this.id);
    }

    public void setId(String id) {
        this.id = id;
    }

    /**
     * <p>Return the zero-relative index of the current iteration through the
     * loop.  If you specify an <code>offset</code>, the first iteration
     * through the loop will have that value; otherwise, the first iteration
     * will return zero.</p>
     *
     * <p>This property is read-only, and gives nested custom tags access to
     * this information.  Therefore, it is <strong>only</strong> valid in
     * between calls to <code>doStartTag()</code> and <code>doEndTag()</code>.
     * </p>
     */
    public int getIndex() {
        if (started)
            return (offsetValue + lengthCount - 1);
        else
            return (0);
    }

    /**
     * The name of the scripting variable to be exposed as the current index.
     */
    protected String indexId = null;

    public String getIndexId() {
        return (this.indexId);
    }

    public void setIndexId(String indexId) {
        this.indexId = indexId;
    }

    /**
     * The length value or attribute name (<=0 means no limit).
     */
    protected String length = null;

    public String getLength() {
        return (this.length);
    }

    public void setLength(String length) {
        this.length = length;
    }

    /**
     * The name of the collection or owning bean.
     */
    protected String name = null;

    public String getName() {
        return (this.name);
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * The starting offset (zero relative).
     */
    protected String offset = null;

    public String getOffset() {
        return (this.offset);
    }

    public void setOffset(String offset) {
        this.offset = offset;
    }

    /**
     * The property name containing the collection.
     */
    protected String property = null;

    public String getProperty() {
        return (this.property);
    }

    public void setProperty(String property) {
        this.property = property;
    }

    /**
     * The scope of the bean specified by the name property, if any.
     */
    protected String scope = null;

    public String getScope() {
        return (this.scope);
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    /**
     * The Java class of each exposed element of the collection.
     */
    protected String type = null;

    public String getType() {
        return (this.type);
    }

    public void setType(String type) {
        this.type = type;
    }

    // --------------------------------------------------------- Public Methods

    /**
     * Construct an iterator for the specified collection, and begin
     * looping through the body once per element.
     *
     * @exception JspException if a JSP exception has occurred
     */
    public int doStartTag() throws JspException {

        // Acquire the collection we are going to iterate over
        Object collection = this.collection;
        if (collection == null) {
            collection = TagUtils.getInstance().lookup(pageContext, name, property, scope);
        }

        if (collection == null) {
            JspException e = new JspException(messages.getMessage("iterate.collection"));
            TagUtils.getInstance().saveException(pageContext, e);
            throw e;
        }

        // Construct an iterator for this collection
        if (collection.getClass().isArray()) {
            try {
                // If we're lucky, it is an array of objects
                // that we can iterate over with no copying
                iterator = Arrays.asList((Object[]) collection).iterator();
            } catch (ClassCastException e) {
                // Rats -- it is an array of primitives
                int length = Array.getLength(collection);
                ArrayList c = new ArrayList(length);
                for (int i = 0; i < length; i++) {
                    c.add(Array.get(collection, i));
                }
                iterator = c.iterator();
            }
        } else if (collection instanceof Collection) {
            iterator = ((Collection) collection).iterator();
        } else if (collection instanceof Iterator) {
            iterator = (Iterator) collection;
        } else if (collection instanceof Map) {
            iterator = ((Map) collection).entrySet().iterator();
        } else if (collection instanceof Enumeration) {
            iterator = IteratorUtils.asIterator((Enumeration) collection);
        } else {
            JspException e = new JspException(messages.getMessage("iterate.iterator"));
            TagUtils.getInstance().saveException(pageContext, e);
            throw e;
        }

        // Calculate the starting offset
        if (offset == null) {
            offsetValue = 0;
        } else {
            try {
                offsetValue = Integer.parseInt(offset);
            } catch (NumberFormatException e) {
                Integer offsetObject = (Integer) TagUtils.getInstance().lookup(pageContext, offset, null);
                if (offsetObject == null) {
                    offsetValue = 0;
                } else {
                    offsetValue = offsetObject.intValue();
                }
            }
        }
        if (offsetValue < 0) {
            offsetValue = 0;
        }

        // Calculate the rendering length
        if (length == null) {
            lengthValue = 0;
        } else {
            try {
                lengthValue = Integer.parseInt(length);
            } catch (NumberFormatException e) {
                Integer lengthObject = (Integer) TagUtils.getInstance().lookup(pageContext, length, null);
                if (lengthObject == null) {
                    lengthValue = 0;
                } else {
                    lengthValue = lengthObject.intValue();
                }
            }
        }
        if (lengthValue < 0) {
            lengthValue = 0;
        }
        lengthCount = 0;

        // Skip the leading elements up to the starting offset
        for (int i = 0; i < offsetValue; i++) {
            if (iterator.hasNext()) {
                iterator.next();
            }
        }

        // Store the first value and evaluate, or skip the body if none
        if (iterator.hasNext()) {
            Object element = iterator.next();
            if (element == null) {
                pageContext.removeAttribute(id);
            } else {
                pageContext.setAttribute(id, element);
            }
            lengthCount++;
            started = true;
            if (indexId != null) {
                pageContext.setAttribute(indexId, new Integer(getIndex()));
            }
            return (EVAL_BODY_TAG);
        } else {
            return (SKIP_BODY);
        }

    }

    /**
     * Make the next collection element available and loop, or
     * finish the iterations if there are no more elements.
     *
     * @exception JspException if a JSP exception has occurred
     */
    public int doAfterBody() throws JspException {

        // Render the output from this iteration to the output stream
        if (bodyContent != null) {
            TagUtils.getInstance().writePrevious(pageContext, bodyContent.getString());
            bodyContent.clearBody();
        }

        // Decide whether to iterate or quit
        if ((lengthValue > 0) && (lengthCount >= lengthValue)) {
            return (SKIP_BODY);
        }

        if (iterator.hasNext()) {
            Object element = iterator.next();
            if (element == null) {
                pageContext.removeAttribute(id);
            } else {
                pageContext.setAttribute(id, element);
            }
            lengthCount++;
            if (indexId != null) {
                pageContext.setAttribute(indexId, new Integer(getIndex()));
            }
            return (EVAL_BODY_TAG);
        } else {
            return (SKIP_BODY);
        }

    }

    /**
     * Clean up after processing this enumeration.
     *
     * @exception JspException if a JSP exception has occurred
     */
    public int doEndTag() throws JspException {

        // Clean up our started state
        started = false;
        iterator = null;

        // Continue processing this page
        return (EVAL_PAGE);

    }

    /**
     * Release all allocated resources.
     */
    public void release() {

        super.release();

        iterator = null;
        lengthCount = 0;
        lengthValue = 0;
        offsetValue = 0;

        id = null;
        collection = null;
        length = null;
        name = null;
        offset = null;
        property = null;
        scope = null;
        started = false;

    }

}
