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
 * <code>DOMError</code> is an interface that describes an error.
 * <p>See also the <a href='http://www.w3.org/TR/2003/WD-DOM-Level-3-Core-20030609'>Document Object Model (DOM) Level 3 Core Specification</a>.
 * @since DOM Level 3
 */
public interface DOMError {
    // ErrorSeverity
    /**
     * The severity of the error described by the <code>DOMError</code> is 
     * warning
     */
    public static final short SEVERITY_WARNING          = 0;
    /**
     * The severity of the error described by the <code>DOMError</code> is 
     * error
     */
    public static final short SEVERITY_ERROR            = 1;
    /**
     * The severity of the error described by the <code>DOMError</code> is 
     * fatal error
     */
    public static final short SEVERITY_FATAL_ERROR      = 2;

    /**
     * The severity of the error, either <code>SEVERITY_WARNING</code>, 
     * <code>SEVERITY_ERROR</code>, or <code>SEVERITY_FATAL_ERROR</code>.
     */
    public short getSeverity();

    /**
     * An implementation specific string describing the error that occurred.
     */
    public String getMessage();

    /**
     *  A <code>DOMString</code> indicating which related data is expected in 
     * <code>relatedData</code>. Users should refer to the specification of 
     * the error in order to find its <code>DOMString</code> type and 
     * <code>relatedData</code> definitions if any. 
     * <p ><b>Note:</b>  As an example, 
     * <code>Document.normalizeDocument()</code> does generate warnings when 
     * the "split-cdata-sections" parameter is in use. Therefore, the method 
     * generates a <code>SEVERITY_WARNING</code> with <code>type</code> 
     * <code>"cdata-section-splitted"</code> and the first 
     * <code>CDATASection</code> node in document order resulting from the 
     * split is returned by the <code>relatedData</code> attribute. 
     */
    public String getType();

    /**
     * The related platform dependent exception if any.
     */
    public Object getRelatedException();

    /**
     *  The related <code>DOMError.type</code> dependent data if any. 
     */
    public Object getRelatedData();

    /**
     * The location of the error.
     */
    public DOMLocator getLocation();

}
