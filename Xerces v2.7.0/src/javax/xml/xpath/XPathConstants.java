/*
 * Copyright 2003-2004 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
// $Id: XPathConstants.java,v 1.1 2007/03/12 16:15:08 guehene Exp $

package javax.xml.xpath;

import javax.xml.namespace.QName;

/**
 * <p>XPath constants.</p>
 *
 * @author <a href="mailto:Norman.Walsh@Sun.COM">Norman Walsh</a>
 * @author <a href="mailto:Jeff.Suttor@Sun.COM">Jeff Suttor</a>
 * @version $Revision: 1.1 $, $Date: 2007/03/12 16:15:08 $
 * @see <a href="http://www.w3.org/TR/xpath">XML Path Language (XPath) Version 1.0</a>
 * @since 1.5
 */
public class XPathConstants {
      
    /**
     * <p>Private constructor to prevent instantiation.</p>
     */
    private XPathConstants() { }
  
    /**
     * <p>The XPath 1.0 number data type.</p>
     * 
     * <p>Maps to Java {@link Double}.</p>
     */
    public static final QName NUMBER = new QName("http://www.w3.org/1999/XSL/Transform", "NUMBER");

    /**
     * <p>The XPath 1.0 string data type.</p>
     * 
     * <p>Maps to Java {@link String}.</p>
     */
    public static final QName STRING = new QName("http://www.w3.org/1999/XSL/Transform", "STRING");

    /**
     * <p>The XPath 1.0 boolean data type.</p>
     * 
     * <p>Maps to Java {@link Boolean}.</p>
     */
    public static final QName BOOLEAN = new QName("http://www.w3.org/1999/XSL/Transform", "BOOLEAN");

    /**
     * <p>The XPath 1.0 NodeSet data type.</p>
     * 
     * <p>Maps to Java {@link org.w3c.dom.NodeList}.</p>
     */
    public static final QName NODESET = new QName("http://www.w3.org/1999/XSL/Transform", "NODESET");

    /**
     * <p>The XPath 1.0 NodeSet data type.
     * 
     * <p>Maps to Java {@link org.w3c.dom.Node}.</p>
     */
    public static final QName NODE = new QName("http://www.w3.org/1999/XSL/Transform", "NODE");

    /**
     * <p>The URI for the DOM object model, "http://java.sun.com/jaxp/xpath/dom".</p>
     */
    public static final String DOM_OBJECT_MODEL = "http://java.sun.com/jaxp/xpath/dom";
}
