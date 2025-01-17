/*
 * $Header: /home/cvs/jakarta-struts/src/share/org/apache/struts/tiles/beans/SimpleMenuItem.java,v 1.5 2004/03/14 06:23:53 sraeburn Exp $
 * $Revision: 1.5 $
 * $Date: 2004/03/14 06:23:53 $
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

package org.apache.struts.tiles.beans;

import java.io.Serializable;

/**
 * A MenuItem implementation.
 * Used to read menu items in definitions.
 */
public class SimpleMenuItem implements MenuItem, Serializable {

    private String value = null;

    private String link = null;

    private String icon = null;

    private String tooltip = null;

    /**
     * Constructor.
     */
    public SimpleMenuItem() {
        super();
    }

    /**
     * Set value property.
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * Get value property.
     */
    public String getValue() {
        return value;
    }

    /**
     * Set link property.
     */
    public void setLink(String link) {
        this.link = link;
    }

    /**
     * Get link property.
     */
    public String getLink() {
        return link;
    }

    /**
     * Set icon property.
     */
    public void setIcon(String icon) {
        this.icon = icon;
    }

    /**
     * Get icon property.
     */
    public String getIcon() {
        return icon;
    }

    /**
     * Set tooltip property.
     */
    public void setTooltip(String tooltip) {
        this.tooltip = tooltip;
    }

    /**
     * Get tooltip property.
     */
    public String getTooltip() {
        return tooltip;
    }

    /**
     * Return String representation.
     */
    public String toString() {
        StringBuffer buff = new StringBuffer("SimpleMenuItem[");

        if (getValue() != null) {
            buff.append("value=").append(getValue()).append(", ");
        }

        if (getLink() != null) {
            buff.append("link=").append(getLink()).append(", ");
        }

        if (getTooltip() != null) {
            buff.append("tooltip=").append(getTooltip()).append(", ");
        }

        if (getIcon() != null) {
            buff.append("icon=").append(getIcon()).append(", ");
        }

        buff.append("]");
        return buff.toString();
    }

}
