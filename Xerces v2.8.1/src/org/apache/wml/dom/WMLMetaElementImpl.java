/*
 * Copyright 1999,2000,2004,2005 The Apache Software Foundation.
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
package org.apache.wml.dom;

import org.apache.wml.*;

/**
 * @xerces.internal
 * @version $Id: WMLMetaElementImpl.java 320584 2005-09-30 21:43:35Z mrglavas $
 * @author <a href="mailto:david@topware.com.tw">David Li</a>
 */
public class WMLMetaElementImpl extends WMLElementImpl implements WMLMetaElement {
    
    private static final long serialVersionUID = -2791663042188681846L;

    public WMLMetaElementImpl (WMLDocumentImpl owner, String tagName) {
        super( owner, tagName);
    }
    
    public void setForua(boolean newValue) {
        setAttribute("forua", newValue);
    }
    
    public boolean getForua() {
        return getAttribute("forua", false);
    }
    
    public void setScheme(String newValue) {
        setAttribute("scheme", newValue);
    }
    
    public String getScheme() {
        return getAttribute("scheme");
    }
    
    public void setClassName(String newValue) {
        setAttribute("class", newValue);
    }
    
    public String getClassName() {
        return getAttribute("class");
    }
    
    public void setHttpEquiv(String newValue) {
        setAttribute("http-equiv", newValue);
    }
    
    public String getHttpEquiv() {
        return getAttribute("http-equiv");
    }
    
    public void setId(String newValue) {
        setAttribute("id", newValue);
    }
    
    public String getId() {
        return getAttribute("id");
    }
    
    public void setContent(String newValue) {
        setAttribute("content", newValue);
    }
    
    public String getContent() {
        return getAttribute("content");
    }
    
    public void setName(String newValue) {
        setAttribute("name", newValue);
    }
    
    public String getName() {
        return getAttribute("name");
    }
}
