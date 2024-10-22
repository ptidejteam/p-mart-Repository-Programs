/*
 * $Header: /home/cvs/jakarta-struts/src/share/org/apache/struts/config/MessageResourcesConfig.java,v 1.11 2004/03/14 06:23:47 sraeburn Exp $
 * $Revision: 1.11 $
 * $Date: 2004/03/14 06:23:47 $
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
import org.apache.struts.Globals;


/**
 * <p>A JavaBean representing the configuration information of a
 * <code>&lt;message-resources&gt;</code> element in a Struts
 * configuration file.</p>
 *
 * @version $Revision: 1.11 $ $Date: 2004/03/14 06:23:47 $
 * @since Struts 1.1
 */

public class MessageResourcesConfig implements Serializable {


    // ----------------------------------------------------- Instance Variables


    /**
     * Has this component been completely configured?
     */
    protected boolean configured = false;


    // ------------------------------------------------------------- Properties


    /**
     * Fully qualified Java class name of the MessageResourcesFactory class
     * we should use.
     */
    protected String factory =
        "org.apache.struts.util.PropertyMessageResourcesFactory";

    public String getFactory() {
        return (this.factory);
    }

    public void setFactory(String factory) {
        if (configured) {
            throw new IllegalStateException("Configuration is frozen");
        }
        this.factory = factory;
    }


    /**
     * The servlet context attributes key under which this MessageResources
     * instance is stored.
     */
    protected String key = Globals.MESSAGES_KEY;

    public String getKey() {
        return (this.key);
    }

    public void setKey(String key) {
        if (configured) {
            throw new IllegalStateException("Configuration is frozen");
        }
        this.key = key;
    }


    /**
     * Should we return <code>null</code> for unknown message keys?
     */
    protected boolean nullValue = true;

    public boolean getNull() {
        return (this.nullValue);
    }

    public void setNull(boolean nullValue) {
        if (configured) {
            throw new IllegalStateException("Configuration is frozen");
        }
        this.nullValue = nullValue;
    }


    /**
     * Parameter that is passed to the <code>createResources()</code> method
     * of our MessageResourcesFactory implementation.
     */
    protected String parameter = null;

    public String getParameter() {
        return (this.parameter);
    }

    public void setParameter(String parameter) {
        if (configured) {
            throw new IllegalStateException("Configuration is frozen");
        }
        this.parameter = parameter;
    }


    // --------------------------------------------------------- Public Methods


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

        StringBuffer sb = new StringBuffer("MessageResourcesConfig[");
        sb.append("factory=");
        sb.append(this.factory);
        sb.append(",null=");
        sb.append(this.nullValue);
        sb.append(",parameter=");
        sb.append(this.parameter);
        sb.append("]");
        return (sb.toString());

    }


}
