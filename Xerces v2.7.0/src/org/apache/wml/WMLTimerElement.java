/*
 * Copyright 1999,2000,2004 The Apache Software Foundation.
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
package org.apache.wml;

/**
 * <p>The interface is modeled after DOM1 Spec for HTML from W3C.
 * The DTD used in this DOM model is from 
 * <a href="http://www.wapforum.org/DTD/wml_1.1.xml">
 * http://www.wapforum.org/DTD/wml_1.1.xml</a></p>
 *
 * <p>'timer' elements declares a card timer.
 * (Section 11.6.7, WAP WML Version 16-Jun-1999)</p>
 *
 * @version $Id: WMLTimerElement.java,v 1.1 2006/02/02 01:00:02 vauchers Exp $
 * @author <a href="mailto:david@topware.com.tw">David Li</a>
 */

public interface WMLTimerElement extends WMLElement {
    /**
     * 'name' specifies the name of variable ot be set with the value
     * of the timer.
     * (Section 11.6.7, WAP WML Version 16-Jun-1999)
     */
    public void setName(String newValue);
    public String getName();

    /**
     * 'value' indicates teh default of the variable 'name'
     * (Section 11.6.7, WAP WML Version 16-Jun-1999)
     */
    public void setValue(String newValue);
    public String getValue();
}
