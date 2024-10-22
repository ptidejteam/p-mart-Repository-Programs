/*
 * Copyright (c) 2002 World Wide Web Consortium,
 * (Massachusetts Institute of Technology, Institut National de
 * Recherche en Informatique et en Automatique, Keio University). All
 * Rights Reserved. This program is distributed under the W3C's Software
 * Intellectual Property License. This program is distributed in the
 * hope that it will be useful, but WITHOUT ANY WARRANTY; without even
 * the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR
 * PURPOSE.
 * See W3C License http://www.w3.org/Consortium/Legal/ for more details.
 */

package org.w3c.dom;

/**
 * DOM Level 3 WD Experimental:
 * The DOM Level 3 specification is at the stage 
 * of Working Draft, which represents work in 
 * progress and thus may be updated, replaced, 
 * or obsoleted by other documents at any time. 
 * <p>
 * The <code>Text</code> interface inherits from <code>CharacterData</code> 
 * and represents the textual content (termed character data in XML) of an 
 * <code>Element</code> or <code>Attr</code>. If there is no markup inside 
 * an element's content, the text is contained in a single object 
 * implementing the <code>Text</code> interface that is the only child of 
 * the element. If there is markup, it is parsed into the information items 
 * (elements, comments, etc.) and <code>Text</code> nodes that form the list 
 * of children of the element.
 * <p>When a document is first made available via the DOM, there is only one 
 * <code>Text</code> node for each block of text. Users may create adjacent 
 * <code>Text</code> nodes that represent the contents of a given element 
 * without any intervening markup, but should be aware that there is no way 
 * to represent the separations between these nodes in XML or HTML, so they 
 * will not (in general) persist between DOM editing sessions. The 
 * <code>normalize</code> method on <code>Node</code> merges any such 
 * adjacent <code>Text</code> objects into a single node for each block of 
 * text.
 * <p>See also the <a href='http://www.w3.org/TR/2002/WD-DOM-Level-3-Core-20020409'>Document Object Model (DOM) Level 3 Core Specification</a>.
 */
public interface Text extends CharacterData {
    /**
     * Breaks this node into two nodes at the specified <code>offset</code>, 
     * keeping both in the tree as siblings. After being split, this node 
     * will contain all the content up to the <code>offset</code> point. A 
     * new node of the same type, which contains all the content at and 
     * after the <code>offset</code> point, is returned. If the original 
     * node had a parent node, the new node is inserted as the next sibling 
     * of the original node. When the <code>offset</code> is equal to the 
     * length of this node, the new node has no data.
     * @param offset The 16-bit unit offset at which to split, starting from 
     *   <code>0</code>.
     * @return The new node, of the same type as this node.
     * @exception DOMException
     *   INDEX_SIZE_ERR: Raised if the specified offset is negative or greater 
     *   than the number of 16-bit units in <code>data</code>.
     *   <br>NO_MODIFICATION_ALLOWED_ERR: Raised if this node is readonly.
     */
    public Text splitText(int offset)
                          throws DOMException;

    /**
     * Returns whether this text node contains whitespace in element content, 
     * often abusively called "ignorable whitespace".An implementation can 
     * only return <code>true</code> if, one way or another, it has access 
     * to the relevant information (e.g., the DTD or schema).
     * <br> This attribute represents the property [element content 
     * whitespace] defined in . 
     * @since DOM Level 3
     */
    public boolean getIsWhitespaceInElementContent();

    /**
     * Returns all text of <code>Text</code> nodes logically-adjacent text 
     * nodes to this node, concatenated in document order.
     * @since DOM Level 3
     */
    public String getWholeText();

    /**
     * Substitutes the a specified text for the text of the current node and 
     * all logically-adjacent text nodes.
     * <br>This method returns the node in the hierarchy which received the 
     * replacement text, which is null if the text was empty or is the 
     * current node if the current node is not read-only or otherwise is a 
     * new node of the same type as the current node inserted at the site of 
     * the replacement. All logically-adjacent text nodes are removed 
     * including the current node unless it was the recipient of the 
     * replacement text.
     * <br>Where the nodes to be removed are read-only descendants of an 
     * <code>EntityReference</code>, the <code>EntityReference</code> must 
     * be removed instead of the read-only nodes. If any 
     * <code>EntityReference</code> to be removed has descendants that are 
     * not <code>EntityReference</code>, <code>Text</code>, or 
     * <code>CDATASection</code> nodes, the <code>replaceWholeText</code> 
     * method must fail before performing any modification of the document, 
     * raising a <code>DOMException</code> with the code 
     * <code>NO_MODIFICATION_ALLOWED_ERR</code>.
     * @param content The content of the replacing <code>Text</code> node.
     * @return The <code>Text</code> node created with the specified content.
     * @exception DOMException
     *   NO_MODIFICATION_ALLOWED_ERR: Raised if one of the <code>Text</code> 
     *   nodes being replaced is readonly.
     * @since DOM Level 3
     */
    public Text replaceWholeText(String content)
                                 throws DOMException;

}
