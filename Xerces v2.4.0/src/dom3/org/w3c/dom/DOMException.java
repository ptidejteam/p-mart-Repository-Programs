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

package org.w3c.dom;

/** 
 * DOM Level 3 WD Experimental:
 * The DOM Level 3 specification is at the stage 
 * of Working Draft, which represents work in 
 * progress and thus may be updated, replaced, 
 * or obsoleted by other documents at any time. 
 * 
 * DOM operations only raise exceptions in "exceptional" circumstances, i.e., 
 * when an operation is impossible to perform (either for logical reasons, 
 * because data is lost, or because the implementation has become unstable). 
 * In general, DOM methods return specific error values in ordinary 
 * processing situations, such as out-of-bound errors when using 
 * <code>NodeList</code>.
 * <p>Implementations should raise other exceptions under other circumstances. 
 * For example, implementations should raise an implementation-dependent 
 * exception if a <code>null</code> argument is passed when <code>null</code>
 *  was not expected.
 * <p>Some languages and object systems do not support the concept of 
 * exceptions. For such systems, error conditions may be indicated using 
 * native error reporting mechanisms. For some bindings, for example, 
 * methods may return error codes similar to those listed in the 
 * corresponding method descriptions.
 * <p>See also the <a href='http://www.w3.org/TR/2003/WD-DOM-Level-3-Core-20030226'>Document Object Model (DOM) Level 3 Core Specification</a>.
 */
public class DOMException extends RuntimeException {
    public DOMException(short code, String message) {
       super(message);
       this.code = code;
    }
    public short   code;
    // ExceptionCode
    /**
     * If index or size is negative, or greater than the allowed value
     */
    public static final short INDEX_SIZE_ERR            = 1;
    /**
     * If the specified range of text does not fit into a DOMString
     */
    public static final short DOMSTRING_SIZE_ERR        = 2;
    /**
     * If any node is inserted somewhere it doesn't belong
     */
    public static final short HIERARCHY_REQUEST_ERR     = 3;
    /**
     * If a node is used in a different document than the one that created it 
     * (that doesn't support it)
     */
    public static final short WRONG_DOCUMENT_ERR        = 4;
    /**
     * If an invalid or illegal character is specified, such as in a name. See <a href='http://www.w3.org/TR/2000/REC-xml-20001006#NT-Char'>production 2</a> in the XML specification for the definition of a legal character, and <a href='http://www.w3.org/TR/2000/REC-xml-20001006#NT-Name'>production 5</a> for the definition of a legal name character.
     */
    public static final short INVALID_CHARACTER_ERR     = 5;
    /**
     * If data is specified for a node which does not support data
     */
    public static final short NO_DATA_ALLOWED_ERR       = 6;
    /**
     * If an attempt is made to modify an object where modifications are not 
     * allowed
     */
    public static final short NO_MODIFICATION_ALLOWED_ERR = 7;
    /**
     * If an attempt is made to reference a node in a context where it does 
     * not exist
     */
    public static final short NOT_FOUND_ERR             = 8;
    /**
     * If the implementation does not support the requested type of object or 
     * operation.
     */
    public static final short NOT_SUPPORTED_ERR         = 9;
    /**
     * If an attempt is made to add an attribute that is already in use 
     * elsewhere
     */
    public static final short INUSE_ATTRIBUTE_ERR       = 10;
    /**
     * If an attempt is made to use an object that is not, or is no longer, 
     * usable.
     * @since DOM Level 2
     */
    public static final short INVALID_STATE_ERR         = 11;
    /**
     * If an invalid or illegal string is specified.
     * @since DOM Level 2
     */
    public static final short SYNTAX_ERR                = 12;
    /**
     * If an attempt is made to modify the type of the underlying object.
     * @since DOM Level 2
     */
    public static final short INVALID_MODIFICATION_ERR  = 13;
    /**
     * If an attempt is made to create or change an object in a way which is 
     * incorrect with regard to namespaces.
     * @since DOM Level 2
     */
    public static final short NAMESPACE_ERR             = 14;
    /**
     * If a parameter or an operation is not supported by the underlying 
     * object.
     * @since DOM Level 2
     */
    public static final short INVALID_ACCESS_ERR        = 15;
    /**
     * If a call to a method such as <code>insertBefore</code> or 
     * <code>removeChild</code> would make the <code>Node</code> invalid 
     * with respect to "partial validity", this exception would be raised 
     * and the operation would not be done. This code is used in [<a href='http://www.w3.org/TR/DOM-Level-3-Val'>DOM Level 3 Validation</a>]. 
     * Refer to this specification for further information.
     * @since DOM Level 3
     */
    public static final short VALIDATION_ERR            = 16;

}
