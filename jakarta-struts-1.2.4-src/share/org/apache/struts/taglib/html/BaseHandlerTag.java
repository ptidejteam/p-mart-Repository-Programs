/*
 * $Header: /home/cvs/jakarta-struts/src/share/org/apache/struts/taglib/html/BaseHandlerTag.java,v 1.36 2004/03/14 06:23:46 sraeburn Exp $
 * $Revision: 1.36 $
 * $Date: 2004/03/14 06:23:46 $
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

package org.apache.struts.taglib.html;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Locale;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.Globals;
import org.apache.struts.taglib.TagUtils;
import org.apache.struts.taglib.logic.IterateTag;
import org.apache.struts.util.MessageResources;
import org.apache.struts.util.RequestUtils;

/**
 * Base class for tags that render form elements capable of including JavaScript
 * event handlers and/or CSS Style attributes. This class does not implement
 * the doStartTag() or doEndTag() methods. Subclasses should provide
 * appropriate implementations of these.
 *
 * @version $Revision: 1.36 $ $Date: 2004/03/14 06:23:46 $
 */
public abstract class BaseHandlerTag extends BodyTagSupport {

    /**
     * Commons Logging instance.
     */
    private static Log log = LogFactory.getLog(BaseHandlerTag.class);

    // ----------------------------------------------------- Instance Variables

    /**
     * The default Locale for our server.
     * @deprecated Use Locale.getDefault() directly.
     */
    protected static final Locale defaultLocale = Locale.getDefault();

    /**
     * The message resources for this package.
     */
    protected static MessageResources messages =
        MessageResources.getMessageResources(Constants.Package + ".LocalStrings");

    //  Navigation Management

    /** Access key character. */
    protected String accesskey = null;

    /** Tab index value. */
    protected String tabindex = null;

    //  Indexing ability for Iterate

    /** Whether to created indexed names for fields
     * @since Struts 1.1
     */
    protected boolean indexed = false;

    //  Mouse Events

    /** Mouse click event. */
    private String onclick = null;

    /** Mouse double click event. */
    private String ondblclick = null;

    /** Mouse over component event. */
    private String onmouseover = null;

    /** Mouse exit component event. */
    private String onmouseout = null;

    /** Mouse moved over component event. */
    private String onmousemove = null;

    /** Mouse pressed on component event. */
    private String onmousedown = null;

    /** Mouse released on component event. */
    private String onmouseup = null;

    //  Keyboard Events

    /** Key down in component event. */
    private String onkeydown = null;

    /** Key released in component event. */
    private String onkeyup = null;

    /** Key down and up together in component event. */
    private String onkeypress = null;

    // Text Events

    /** Text selected in component event. */
    private String onselect = null;

    /** Content changed after component lost focus event. */
    private String onchange = null;

    // Focus Events and States

    /** Component lost focus event. */
    private String onblur = null;

    /** Component has received focus event. */
    private String onfocus = null;

    /** Component is disabled. */
    private boolean disabled = false;

    /** Component is readonly. */
    private boolean readonly = false;

    // CSS Style Support

    /** Style attribute associated with component. */
    private String style = null;

    /** Named Style class associated with component. */
    private String styleClass = null;

    /** Identifier associated with component.  */
    private String styleId = null;

    // Other Common Attributes

    /** The alternate text of this element. */
    private String alt = null;

    /** The message resources key of the alternate text. */
    private String altKey = null;

    /** The name of the message resources bundle for message lookups. */
    private String bundle = null;

    /** The name of the session attribute key for our locale. */
    private String locale = Globals.LOCALE_KEY;

    /** The advisory title of this element. */
    private String title = null;

    /** The message resources key of the advisory title. */
    private String titleKey = null;

    // ------------------------------------------------------------- Properties

    //  Navigation Management

    /** Sets the accessKey character. */
    public void setAccesskey(String accessKey) {
        this.accesskey = accessKey;
    }

    /** Returns the accessKey character. */
    public String getAccesskey() {
        return (this.accesskey);
    }

    /** Sets the tabIndex value. */
    public void setTabindex(String tabIndex) {
        this.tabindex = tabIndex;
    }

    /** Returns the tabIndex value. */
    public String getTabindex() {
        return (this.tabindex);
    }

    //  Indexing ability for Iterate [since Struts 1.1]

    /** Sets the indexed value.
     * @since Struts 1.1
     */
    public void setIndexed(boolean indexed) {
        this.indexed = indexed;
    }

    /** Returns the indexed value.
     * @since Struts 1.1
     */
    public boolean getIndexed() {
        return (this.indexed);
    }

    // Mouse Events

    /** Sets the onClick event handler. */
    public void setOnclick(String onClick) {
        this.onclick = onClick;
    }

    /** Returns the onClick event handler. */
    public String getOnclick() {
        return onclick;
    }

    /** Sets the onDblClick event handler. */
    public void setOndblclick(String onDblClick) {
        this.ondblclick = onDblClick;
    }

    /** Returns the onDblClick event handler. */
    public String getOndblclick() {
        return ondblclick;
    }

    /** Sets the onMouseDown event handler. */
    public void setOnmousedown(String onMouseDown) {
        this.onmousedown = onMouseDown;
    }

    /** Returns the onMouseDown event handler. */
    public String getOnmousedown() {
        return onmousedown;
    }

    /** Sets the onMouseUp event handler. */
    public void setOnmouseup(String onMouseUp) {
        this.onmouseup = onMouseUp;
    }

    /** Returns the onMouseUp event handler. */
    public String getOnmouseup() {
        return onmouseup;
    }

    /** Sets the onMouseMove event handler. */
    public void setOnmousemove(String onMouseMove) {
        this.onmousemove = onMouseMove;
    }

    /** Returns the onMouseMove event handler. */
    public String getOnmousemove() {
        return onmousemove;
    }

    /** Sets the onMouseOver event handler. */
    public void setOnmouseover(String onMouseOver) {
        this.onmouseover = onMouseOver;
    }

    /** Returns the onMouseOver event handler. */
    public String getOnmouseover() {
        return onmouseover;
    }

    /** Sets the onMouseOut event handler. */
    public void setOnmouseout(String onMouseOut) {
        this.onmouseout = onMouseOut;
    }

    /** Returns the onMouseOut event handler. */
    public String getOnmouseout() {
        return onmouseout;
    }

    // Keyboard Events

    /** Sets the onKeyDown event handler. */
    public void setOnkeydown(String onKeyDown) {
        this.onkeydown = onKeyDown;
    }

    /** Returns the onKeyDown event handler. */
    public String getOnkeydown() {
        return onkeydown;
    }

    /** Sets the onKeyUp event handler. */
    public void setOnkeyup(String onKeyUp) {
        this.onkeyup = onKeyUp;
    }

    /** Returns the onKeyUp event handler. */
    public String getOnkeyup() {
        return onkeyup;
    }

    /** Sets the onKeyPress event handler. */
    public void setOnkeypress(String onKeyPress) {
        this.onkeypress = onKeyPress;
    }

    /** Returns the onKeyPress event handler. */
    public String getOnkeypress() {
        return onkeypress;
    }

    // Text Events

    /** Sets the onChange event handler. */
    public void setOnchange(String onChange) {
        this.onchange = onChange;
    }

    /** Returns the onChange event handler. */
    public String getOnchange() {
        return onchange;
    }

    /** Sets the onSelect event handler. */
    public void setOnselect(String onSelect) {
        this.onselect = onSelect;
    }

    /** Returns the onSelect event handler. */
    public String getOnselect() {
        return onselect;
    }

    // Focus Events and States

    /** Sets the onBlur event handler. */
    public void setOnblur(String onBlur) {
        this.onblur = onBlur;
    }

    /** Returns the onBlur event handler. */
    public String getOnblur() {
        return onblur;
    }

    /** Sets the onFocus event handler. */
    public void setOnfocus(String onFocus) {
        this.onfocus = onFocus;
    }

    /** Returns the onFocus event handler. */
    public String getOnfocus() {
        return onfocus;
    }

    /** Sets the disabled event handler. */
    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    /** Returns the disabled event handler. */
    public boolean getDisabled() {
        return disabled;
    }

    /** Sets the readonly event handler. */
    public void setReadonly(boolean readonly) {
        this.readonly = readonly;
    }

    /** Returns the readonly event handler. */
    public boolean getReadonly() {
        return readonly;
    }

    // CSS Style Support

    /** Sets the style attribute. */
    public void setStyle(String style) {
        this.style = style;
    }

    /** Returns the style attribute. */
    public String getStyle() {
        return style;
    }

    /** Sets the style class attribute. */
    public void setStyleClass(String styleClass) {
        this.styleClass = styleClass;
    }

    /** Returns the style class attribute. */
    public String getStyleClass() {
        return styleClass;
    }

    /** Sets the style id attribute.  */
    public void setStyleId(String styleId) {
        this.styleId = styleId;
    }

    /** Returns the style id attribute.  */
    public String getStyleId() {
        return styleId;
    }

    // Other Common Elements

    /** Returns the alternate text attribute. */
    public String getAlt() {
        return alt;
    }

    /** Sets the alternate text attribute. */
    public void setAlt(String alt) {
        this.alt = alt;
    }

    /** Returns the message resources key of the alternate text. */
    public String getAltKey() {
        return altKey;
    }

    /** Sets the message resources key of the alternate text. */
    public void setAltKey(String altKey) {
        this.altKey = altKey;
    }

    /** Returns the name of the message resources bundle to use. */
    public String getBundle() {
        return bundle;
    }

    /** Sets the name of the message resources bundle to use. */
    public void setBundle(String bundle) {
        this.bundle = bundle;
    }

    /** Returns the name of the session attribute for our locale. */
    public String getLocale() {
        return locale;
    }

    /** Sets the name of the session attribute for our locale. */
    public void setLocale(String locale) {
        this.locale = locale;
    }

    /** Returns the advisory title attribute. */
    public String getTitle() {
        return title;
    }

    /** Sets the advisory title attribute. */
    public void setTitle(String title) {
        this.title = title;
    }

    /** Returns the message resources key of the advisory title. */
    public String getTitleKey() {
        return titleKey;
    }

    /** Sets the message resources key of the advisory title. */
    public void setTitleKey(String titleKey) {
        this.titleKey = titleKey;
    }

    // --------------------------------------------------------- Public Methods

    /**
     * Release any acquired resources.
     */
    public void release() {

        super.release();
        accesskey = null;
        alt = null;
        altKey = null;
        bundle = null;
        indexed = false;
        locale = Globals.LOCALE_KEY;
        onclick = null;
        ondblclick = null;
        onmouseover = null;
        onmouseout = null;
        onmousemove = null;
        onmousedown = null;
        onmouseup = null;
        onkeydown = null;
        onkeyup = null;
        onkeypress = null;
        onselect = null;
        onchange = null;
        onblur = null;
        onfocus = null;
        disabled = false;
        readonly = false;
        style = null;
        styleClass = null;
        styleId = null;
        tabindex = null;
        title = null;
        titleKey = null;

    }

    // ------------------------------------------------------ Protected Methods

    /**
     * Return the text specified by the literal value or the message resources
     * key, if any; otherwise return <code>null</code>.
     *
     * @param literal Literal text value or <code>null</code>
     * @param key Message resources key or <code>null</code>
     *
     * @exception JspException if both arguments are non-null
     */
    protected String message(String literal, String key) throws JspException {

        if (literal != null) {
            if (key != null) {
                JspException e =
                    new JspException(messages.getMessage("common.both"));
                TagUtils.getInstance().saveException(pageContext, e);
                throw e;
            } else {
                return (literal);
            }
        } else {
            if (key != null) {
                return TagUtils.getInstance().message(
                    pageContext,
                    getBundle(),
                    getLocale(),
                    key);
            } else {
                return null;
            }
        }

    }

    private Class loopTagSupportClass = null;
    private Method loopTagSupportGetStatus = null;
    private Class loopTagStatusClass = null;
    private Method loopTagStatusGetIndex = null;
    private boolean triedJstlInit = false;
    private boolean triedJstlSuccess = false;

    private Integer getJstlLoopIndex() {
        if (!triedJstlInit) {
            triedJstlInit = true;
            try {
                loopTagSupportClass =
                    RequestUtils.applicationClass(
                        "javax.servlet.jsp.jstl.core.LoopTagSupport");

                loopTagSupportGetStatus =
                    loopTagSupportClass.getDeclaredMethod("getLoopStatus", null);

                loopTagStatusClass =
                    RequestUtils.applicationClass(
                        "javax.servlet.jsp.jstl.core.LoopTagStatus");

                loopTagStatusGetIndex =
                    loopTagStatusClass.getDeclaredMethod("getIndex", null);

                triedJstlSuccess = true;

            } catch (ClassNotFoundException ex) {
                // These just mean that JSTL isn't loaded, so ignore
            } catch (NoSuchMethodException ex) {
            }
        }

        if (triedJstlSuccess) {
            try {
                Object loopTag = findAncestorWithClass(this, loopTagSupportClass);
                if (loopTag == null) {
                    return null;
                }

                Object status = loopTagSupportGetStatus.invoke(loopTag, null);
                return (Integer) loopTagStatusGetIndex.invoke(status, null);

            } catch (IllegalAccessException ex) {
                log.error(ex.getMessage(), ex);

            } catch (IllegalArgumentException ex) {
                log.error(ex.getMessage(), ex);

            } catch (InvocationTargetException ex) {
                log.error(ex.getMessage(), ex);

            } catch (NullPointerException ex) {
                log.error(ex.getMessage(), ex);

            } catch (ExceptionInInitializerError ex) {
                log.error(ex.getMessage(), ex);
            }
        }
        return null;
    }

    /**
     *  Appends bean name with index in brackets for tags with
     *  'true' value in 'indexed' attribute.
     *  @param handlers The StringBuffer that output will be appended to.
     *  @exception JspException if 'indexed' tag used outside of iterate tag.
     */
    protected void prepareIndex(StringBuffer handlers, String name)
        throws JspException {
        int index = 0;
        boolean found = false;

        // look for outer iterate tag
        IterateTag iterateTag =
            (IterateTag) findAncestorWithClass(this, IterateTag.class);

        // Look for JSTL loops
        if (iterateTag == null) {
            Integer i = getJstlLoopIndex();
            if (i != null) {
                index = i.intValue();
                found = true;
            }

        } else {
            index = iterateTag.getIndex();
            found = true;
        }

        if (!found) {
            // this tag should only be nested in iteratetag, if it's not, throw exception
            JspException e =
                new JspException(messages.getMessage("indexed.noEnclosingIterate"));
            TagUtils.getInstance().saveException(pageContext, e);
            throw e;
        }

        if (name != null) {
            handlers.append(name);
        }

        handlers.append("[");
        handlers.append(index);
        handlers.append("]");
        if (name != null) {
            handlers.append(".");
        }
    }

    /**
     * Prepares the style attributes for inclusion in the component's HTML tag.
     * @return The prepared String for inclusion in the HTML tag.
     * @exception JspException if invalid attributes are specified
     */
    protected String prepareStyles() throws JspException {
        String value = null;
        StringBuffer styles = new StringBuffer();
        if (getStyle() != null) {
            styles.append(" style=\"");
            styles.append(getStyle());
            styles.append("\"");
        }
        if (getStyleClass() != null) {
            styles.append(" class=\"");
            styles.append(getStyleClass());
            styles.append("\"");
        }
        if (getStyleId() != null) {
            styles.append(" id=\"");
            styles.append(getStyleId());
            styles.append("\"");
        }
        value = message(this.getTitle(), this.getTitleKey());
        if (value != null) {
            styles.append(" title=\"");
            styles.append(value);
            styles.append("\"");
        }
        value = message(this.getAlt(), this.getAltKey());
        if (value != null) {
            styles.append(" alt=\"");
            styles.append(value);
            styles.append("\"");
        }
        return styles.toString();
    }

    /**
     * Prepares the event handlers for inclusion in the component's HTML tag.
     * @return The prepared String for inclusion in the HTML tag.
     */
    protected String prepareEventHandlers() {
        StringBuffer handlers = new StringBuffer();
        prepareMouseEvents(handlers);
        prepareKeyEvents(handlers);
        prepareTextEvents(handlers);
        prepareFocusEvents(handlers);
        return handlers.toString();
    }

    /**
     * Prepares the mouse event handlers, appending them to the the given
     * StringBuffer.
     * @param handlers The StringBuffer that output will be appended to.
     */
    protected void prepareMouseEvents(StringBuffer handlers) {
        if (getOnclick() != null) {
            handlers.append(" onclick=\"");
            handlers.append(getOnclick());
            handlers.append("\"");
        }

        if (getOndblclick() != null) {
            handlers.append(" ondblclick=\"");
            handlers.append(getOndblclick());
            handlers.append("\"");
        }

        if (getOnmouseover() != null) {
            handlers.append(" onmouseover=\"");
            handlers.append(getOnmouseover());
            handlers.append("\"");
        }

        if (getOnmouseout() != null) {
            handlers.append(" onmouseout=\"");
            handlers.append(getOnmouseout());
            handlers.append("\"");
        }

        if (getOnmousemove() != null) {
            handlers.append(" onmousemove=\"");
            handlers.append(getOnmousemove());
            handlers.append("\"");
        }

        if (getOnmousedown() != null) {
            handlers.append(" onmousedown=\"");
            handlers.append(getOnmousedown());
            handlers.append("\"");
        }

        if (getOnmouseup() != null) {
            handlers.append(" onmouseup=\"");
            handlers.append(getOnmouseup());
            handlers.append("\"");
        }
    }

    /**
     * Prepares the keyboard event handlers, appending them to the the given
     * StringBuffer.
     * @param handlers The StringBuffer that output will be appended to.
     */
    protected void prepareKeyEvents(StringBuffer handlers) {

        if (getOnkeydown() != null) {
            handlers.append(" onkeydown=\"");
            handlers.append(getOnkeydown());
            handlers.append("\"");
        }

        if (getOnkeyup() != null) {
            handlers.append(" onkeyup=\"");
            handlers.append(getOnkeyup());
            handlers.append("\"");
        }

        if (getOnkeypress() != null) {
            handlers.append(" onkeypress=\"");
            handlers.append(getOnkeypress());
            handlers.append("\"");
        }
    }

    /**
     * Prepares the text event handlers, appending them to the the given
     * StringBuffer.
     * @param handlers The StringBuffer that output will be appended to.
     */
    protected void prepareTextEvents(StringBuffer handlers) {

        if (getOnselect() != null) {
            handlers.append(" onselect=\"");
            handlers.append(getOnselect());
            handlers.append("\"");
        }

        if (getOnchange() != null) {
            handlers.append(" onchange=\"");
            handlers.append(getOnchange());
            handlers.append("\"");
        }
    }

    /**
     * Prepares the focus event handlers, appending them to the the given
     * StringBuffer.
     * @param handlers The StringBuffer that output will be appended to.
     */
    protected void prepareFocusEvents(StringBuffer handlers) {

        if (getOnblur() != null) {
            handlers.append(" onblur=\"");
            handlers.append(getOnblur());
            handlers.append("\"");
        }

        if (getOnfocus() != null) {
            handlers.append(" onfocus=\"");
            handlers.append(getOnfocus());
            handlers.append("\"");
        }

        if (getDisabled()) {
            handlers.append(" disabled=\"disabled\"");
        }

        if (getReadonly()) {
            handlers.append(" readonly=\"readonly\"");
        }

    }

    /**
     * Allows HTML tags to find out if they're nested within an %lt;html:html&gt; tag that
     * has xhtml set to true.
     * @return true if the tag is nested within an html tag with xhtml set to true, false
     * otherwise.
     * @since Struts 1.1
     */
    protected boolean isXhtml() {
        return TagUtils.getInstance().isXhtml(this.pageContext);
    }

    /**
     * Returns the closing brace for an input element depending on xhtml status.  The tag
     * must be nested within an %lt;html:html&gt; tag that has xhtml set to true.
     * @return String - &gt; if xhtml is false, /&gt; if xhtml is true
     * @since Struts 1.1
     */
    protected String getElementClose() {
        return this.isXhtml() ? " />" : ">";
    }

    /**
     * Searches all scopes for the bean and calls BeanUtils.getProperty() with the 
     * given arguments and converts any exceptions into JspException.
     * 
     * @param beanName The name of the object to get the property from.
     * @param property The name of the property to get.
     * @return The value of the property.
     * @throws JspException
     * @since Struts 1.1
     */
    protected String lookupProperty(String beanName, String property)
        throws JspException {

        Object bean = TagUtils.getInstance().lookup(this.pageContext, beanName, null);
        if (bean == null) {
            throw new JspException(messages.getMessage("getter.bean", beanName));
        }

        try {
            return BeanUtils.getProperty(bean, property);

        } catch (IllegalAccessException e) {
            throw new JspException(
                messages.getMessage("getter.access", property, beanName));

        } catch (InvocationTargetException e) {
            Throwable t = e.getTargetException();
            throw new JspException(
                messages.getMessage("getter.result", property, t.toString()));

        } catch (NoSuchMethodException e) {
            throw new JspException(
                messages.getMessage("getter.method", property, beanName));
        }
    }

}
