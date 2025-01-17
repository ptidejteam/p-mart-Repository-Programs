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
 * This interface represents an entity, either parsed or unparsed, in an XML 
 * document. Note that this models the entity itself not the entity 
 * declaration. <code>Entity</code> declaration modeling has been left for a 
 * later Level of the DOM specification.
 * <p>The <code>nodeName</code> attribute that is inherited from 
 * <code>Node</code> contains the name of the entity.
 * <p>An XML processor may choose to completely expand entities before the 
 * structure model is passed to the DOM; in this case there will be no 
 * <code>EntityReference</code> nodes in the document tree.
 * <p>XML does not mandate that a non-validating XML processor read and 
 * process entity declarations made in the external subset or declared in 
 * external parameter entities. This means that parsed entities declared in 
 * the external subset need not be expanded by some classes of applications, 
 * and that the replacement text of the entity may not be available. When 
 * the replacement text is available, the corresponding <code>Entity</code> 
 * node's child list represents the structure of that replacement value. 
 * Otherwise, the child list is empty.
 * <p>The DOM Level 2 does not support editing <code>Entity</code> nodes; if a 
 * user wants to make changes to the contents of an <code>Entity</code>, 
 * every related <code>EntityReference</code> node has to be replaced in the 
 * structure model by a clone of the <code>Entity</code>'s contents, and 
 * then the desired changes must be made to each of those clones instead. 
 * <code>Entity</code> nodes and all their descendants are readonly.
 * <p>An <code>Entity</code> node does not have any parent.If the entity 
 * contains an unbound namespace prefix, the <code>namespaceURI</code> of 
 * the corresponding node in the <code>Entity</code> node subtree is 
 * <code>null</code>. The same is true for <code>EntityReference</code> 
 * nodes that refer to this entity, when they are created using the 
 * <code>createEntityReference</code> method of the <code>Document</code> 
 * interface. The DOM Level 2 does not support any mechanism to resolve 
 * namespace prefixes. The properties [notation name] and [notation] defined 
 * in  are not accessible from DOM Level 3 Core. However,  does provide a 
 * way to access them. 
 * <p>See also the <a href='http://www.w3.org/TR/2002/WD-DOM-Level-3-Core-20020409'>Document Object Model (DOM) Level 3 Core Specification</a>.
 */
public interface Entity extends Node {
    /**
     * The public identifier associated with the entity if specified, and 
     * <code>null</code> otherwise.
     * <br> This attribute represents the property [public identifier] defined 
     * by the Unparsed Entity Information Item in . 
     */
    public String getPublicId();

    /**
     * The system identifier associated with the entity if specified, and 
     * <code>null</code> otherwise. This may be an absolute URI or not.
     * <br> This attribute represents the property [system identifier] defined 
     * by the Unparsed Entity Information Item in . 
     */
    public String getSystemId();

    /**
     * For unparsed entities, the name of the notation for the entity. For 
     * parsed entities, this is <code>null</code>.
     */
    public String getNotationName();

    /**
     * An attribute specifying the actual encoding of this entity, when it is 
     * an external parsed entity. This is <code>null</code> otherwise.
     * @since DOM Level 3
     */
    public String getActualEncoding();
    /**
     * An attribute specifying the actual encoding of this entity, when it is 
     * an external parsed entity. This is <code>null</code> otherwise.
     * @since DOM Level 3
     */
    public void setActualEncoding(String actualEncoding);

    /**
     * An attribute specifying, as part of the text declaration, the encoding 
     * of this entity, when it is an external parsed entity. This is 
     * <code>null</code> otherwise.
     * @since DOM Level 3
     */
    public String getEncoding();
    /**
     * An attribute specifying, as part of the text declaration, the encoding 
     * of this entity, when it is an external parsed entity. This is 
     * <code>null</code> otherwise.
     * @since DOM Level 3
     */
    public void setEncoding(String encoding);

    /**
     * An attribute specifying, as part of the text declaration, the version 
     * number of this entity, when it is an external parsed entity. This is 
     * <code>null</code> otherwise.
     * @since DOM Level 3
     */
    public String getVersion();
    /**
     * An attribute specifying, as part of the text declaration, the version 
     * number of this entity, when it is an external parsed entity. This is 
     * <code>null</code> otherwise.
     * @since DOM Level 3
     */
    public void setVersion(String version);

}
