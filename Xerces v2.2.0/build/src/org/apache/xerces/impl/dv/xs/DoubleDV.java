/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2001, 2002 The Apache Software Foundation.  All rights
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
 * originally based on software copyright (c) 2001, International
 * Business Machines, Inc., http://www.apache.org.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */

package org.apache.xerces.impl.dv.xs;

import org.apache.xerces.impl.dv.InvalidDatatypeValueException;
import org.apache.xerces.impl.dv.ValidationContext;

/**
 * Represent the schema type "double"
 *
 * @author Neeraj Bajaj, Sun Microsystems, inc.
 * @author Sandy Gao, IBM
 *
 * @version $Id: DoubleDV.java,v 1.1 2006/02/02 01:38:39 vauchers Exp $
 */
public class DoubleDV extends TypeValidator {

    public short getAllowedFacets(){
        return ( XSSimpleTypeDecl.FACET_PATTERN | XSSimpleTypeDecl.FACET_WHITESPACE | XSSimpleTypeDecl.FACET_ENUMERATION |XSSimpleTypeDecl.FACET_MAXINCLUSIVE |XSSimpleTypeDecl.FACET_MININCLUSIVE | XSSimpleTypeDecl.FACET_MAXEXCLUSIVE  | XSSimpleTypeDecl.FACET_MINEXCLUSIVE  );
    }//getAllowedFacets()

    //convert a String to Double form, we have to take care of cases specified in spec like INF, -INF and NaN
    public Object getActualValue(String content, ValidationContext context) throws InvalidDatatypeValueException {
        try{
            return dValueOf(content);
        } catch (Exception ex){
            throw new InvalidDatatypeValueException("cvc-datatype-valid.1.2.1", new Object[]{content, "double"});
        }
    }//getActualValue()

    // Can't call Double#compareTo method, because it's introduced in jdk 1.2
    public int compare(Object value1, Object value2) {
        return compareDoubles((Double)value1, (Double)value2);
    }//compare()

    //
    // private methods
    //
    private static Double dValueOf(String s) throws NumberFormatException {
        Double d;
        try {
            d = Double.valueOf(s);
        }
        catch ( NumberFormatException nfe ) {
            if ( s.equals("INF") ) {
                d = new Double(Double.POSITIVE_INFINITY);
            }
            else if ( s.equals("-INF") ) {
                d = new Double (Double.NEGATIVE_INFINITY);
            }
            else if ( s.equals("NaN" ) ) {
                d = new Double (Double.NaN);
            }
            else {
                throw nfe;
            }
        }
        return d;
    }//dValueOf()

    private int compareDoubles(Double value, Double anotherValue) {
        double thisVal = value.doubleValue();
        double anotherVal = anotherValue.doubleValue();

        if (thisVal < anotherVal)
            return -1;		 // Neither val is NaN, thisVal is smaller
        if (thisVal > anotherVal)
            return 1;		 // Neither val is NaN, thisVal is larger

        long thisBits = Double.doubleToLongBits(thisVal);
        long anotherBits = Double.doubleToLongBits(anotherVal);

        return (thisBits == anotherBits ?  0 : // Values are equal
                (thisBits < anotherBits ? -1 : // (-0.0, 0.0) or (!NaN, NaN)
                 1));                          // (0.0, -0.0) or (NaN, !NaN)
    }

} // class DoubleDV
