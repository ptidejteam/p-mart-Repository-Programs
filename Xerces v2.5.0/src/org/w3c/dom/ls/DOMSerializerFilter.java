/*
 * Copyright (c) 2003 World Wide Web Consortium,
 *
 * (Massachusetts Institute of Technology, European Research Consortium for
 * Informatics and Mathematics, Keio University). All Rights Reserved. This
 * work is distributed under the W3C(r) Software License [1] in the hope that
 * it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *
 * [1] http://www.w3.org/Consortium/Legal/2002/copyright-software-20021231
 */

package org.w3c.dom.ls;

import org.w3c.dom.traversal.NodeFilter;

/**
 *  <code>DOMSerializerFilter</code>s provide applications the ability to 
 * examine nodes as they are being serialized and decide what nodes should 
 * be serialized or not. The <code>DOMSerializerFilter</code> interface is 
 * based on the <code>NodeFilter</code> interface defined in [<a href='http://www.w3.org/TR/2000/REC-DOM-Level-2-Traversal-Range-20001113'>DOM Level 2 Traversal and      Range</a>]
 * . 
 * <p> The <code>Document</code>, <code>DocumentType</code>, 
 * <code>DocumentFragment</code>, <code>Attr</code>, <code>Notation</code>, 
 * and <code>Entity</code> nodes are not passed to the filter. 
 * <p> The result of any attempt to modify a node passed to a 
 * <code>DOMSerializerFilter</code> is implementation dependent. 
 * <p>See also the <a href='http://www.w3.org/TR/2003/WD-DOM-Level-3-LS-20030619'>Document Object Model (DOM) Level 3 Load
and Save Specification</a>.
 */
public interface DOMSerializerFilter extends NodeFilter {
    /**
     *  Tells the <code>DOMSerializer</code> what types of nodes to show to 
     * the filter. See <code>NodeFilter</code> for definition of the 
     * constants. The constants <code>SHOW_ATTRIBUTE</code>, 
     * <code>SHOW_DOCUMENT</code>, <code>SHOW_DOCUMENT_TYPE</code>, 
     * <code>SHOW_NOTATION</code>, <code>SHOW_ATTRIBUTE</code>, and 
     * <code>SHOW_DOCUMENT_FRAGMENT</code> are meaningless here, those nodes 
     * will never be passed to a <code>DOMSerializerFilter</code>. 
     * <br> The constants used here are defined in [<a href='http://www.w3.org/TR/2000/REC-DOM-Level-2-Traversal-Range-20001113'>DOM Level 2 Traversal and      Range</a>]
     * . 
     */
    public int getWhatToShow();

}
