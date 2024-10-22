/*
 * $Header: /home/cvs/jakarta-struts/src/share/org/apache/struts/taglib/html/CancelTag.java,v 1.16 2004/03/14 06:23:46 sraeburn Exp $
 * $Revision: 1.16 $
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


import javax.servlet.jsp.JspException;

import org.apache.struts.taglib.TagUtils;
import org.apache.struts.util.MessageResources;


/**
 * Tag for input fields of type "cancel".
 *
 * @version $Revision: 1.16 $ $Date: 2004/03/14 06:23:46 $
 */

public class CancelTag extends BaseHandlerTag {


    // ----------------------------------------------------- Instance Variables


    /**
     * The message resources for this package.
     */
    protected static MessageResources messages =
     MessageResources.getMessageResources(Constants.Package + ".LocalStrings");


    /**
     * The property name of the generated button.
     */
    protected String property = Constants.CANCEL_PROPERTY;


    /**
     * The body content of this tag (if any).
     */
    protected String text = null;


    /**
     * The value of the button label.
     */
    protected String value = null;


    // ------------------------------------------------------------- Properties


    /**
     * Return the property name.
     */
    public String getProperty() {
        return (property);
    }

    /**
     * Set the property name.
     *
     * @param property The property name
     */
    public void setProperty(String property) {
        this.property = property;
    }


    /**
     * Return the label value.
     */
    public String getValue() {
        return (value);
    }


    /**
     * Set the label value.
     * @param value The label value
     */
    public void setValue(String value) {
        this.value = value;
    }


    // --------------------------------------------------------- Public Methods


    /**
     * Process the start of this tag.
     * @exception JspException if a JSP exception has occurred
     */
    public int doStartTag() throws JspException {

        // Do nothing until doEndTag() is called
        this.text = null;
        return (EVAL_BODY_TAG);

    }


    /**
     * Save the associated label from the body content.
     *
     * @exception JspException if a JSP exception has occurred
     */
    public int doAfterBody() throws JspException {

        if (bodyContent != null) {
            String value = bodyContent.getString().trim();
            if (value.length() > 0)
                text = value;
        }
        return (SKIP_BODY);

    }


    /**
     * Process the end of this tag.
     * @exception JspException if a JSP exception has occurred
     */
    public int doEndTag() throws JspException {

        // Acquire the label value we will be generating
        String label = value;
        if ((label == null) && (text != null))
            label = text;
        if ((label == null) || (label.trim().length() < 1))
            label = "Cancel";

        // Generate an HTML element
        StringBuffer results = new StringBuffer();
        results.append("<input type=\"submit\"");
        results.append(" name=\"");
        results.append(property);
        results.append("\"");
        if (accesskey != null) {
            results.append(" accesskey=\"");
            results.append(accesskey);
            results.append("\"");
        }
        if (tabindex != null) {
            results.append(" tabindex=\"");
            results.append(tabindex);
            results.append("\"");
        }
        results.append(" value=\"");
        results.append(label);
        results.append("\"");
        results.append(prepareEventHandlers());
        results.append(prepareStyles());
        
        // if no onclick event was provided, put in the cancel script
        if(results.toString().indexOf("onclick=")==-1){
          results.append(" onclick=\"bCancel=true;\"");
        }
        
        results.append(getElementClose());

        // Render this element to our writer
        TagUtils.getInstance().write(pageContext, results.toString());

        // Evaluate the remainder of this page
        return (EVAL_PAGE);

    }


    /**
     * Release any acquired resources.
     */
    public void release() {

	super.release();
	property = Constants.CANCEL_PROPERTY;
        text = null;
	value = null;

    }


}
