/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2000 The Apache Software Foundation.  All rights 
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer. 
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:  
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Xerces" and "Apache Software Foundation" must
 *    not be used to endorse or promote products derived from this
 *    software without prior written permission. For written 
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    nor may "Apache" appear in their name, without prior written
 *    permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation and was
 * originally based on software copyright (c) 1999, International
 * Business Machines, Inc., http://www.apache.org.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */


/**
 * @version $Id: AbstractDatatypeValidator.java,v 1.1 2006/04/25 23:30:38 vauchers Exp $
 * @author  Jeffrey Rodriguez
*/



package org.apache.xerces.validators.datatype;
import java.util.Hashtable;
import org.apache.xerces.validators.datatype.DatatypeValidator;


public abstract class AbstractDatatypeValidator implements DatatypeValidator, Cloneable {

    private Hashtable fFacets; // Hashtable of Facets Strings.

    //abstract public AbstractDatatypeValidator();
    //public AbstractDatatypeValidator(){
    //}


    //abstract public AbstractDatatypeValidator ( DatatypeValidator base, Hashtable facets, 
      //                                 boolean list ); 

    //public AbstractDatatypeValidator ( DatatypeValidator base, Hashtable facets, 
    //                                                                boolean list ){ 
    //
    //
    //}

    /**
     * Checks that "content" string is valid
     * datatype.
     * If invalid a Datatype validation exception is thrown.
     *
     * @param content A string containing the content to be validated
     * @param derivedBylist
     *                Flag which is true when type
     *                is derived by list otherwise it
     *                it is derived by extension.
     *
     * @exception throws InvalidDatatypeException if the content is
     *                   invalid according to the rules for the validators
     * @exception InvalidDatatypeValueException
     * @see         org.apache.xerces.validators.datatype.InvalidDatatypeValueException
     */
    abstract public Object validate(String content, Object state) throws InvalidDatatypeValueException;

    //{ 
     //   System.out.println( "Here in AbstracDatatype " + content );
      //  return null;
    //}


    abstract public Object clone() throws CloneNotSupportedException ;


    /**
     * returns the datatype facet if any is set as a
     * Hashtable
     *
     * @return
     */
    public Hashtable getFacets() {
        return null;  // Not implemented yet
    }
    /**
     * Compares content in the Domain value vs. lexical
     * value.
     * e.g. If type is a float then 1.0 may be equivalent
     * to 1 even tough both are lexically different.
     *
     * @param value1
     * @param valu2
     * @return
     */
    public int compare(String value1, String valu2) {
        return 0;     //Not implemented yet 
    }


}
