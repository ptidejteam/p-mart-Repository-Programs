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

package org.apache.xerces.validators.datatype;
import java.util.Hashtable;
import java.util.Vector;
import java.util.Enumeration;
import java.util.Locale;
import java.text.ParseException;
import java.text.Collator;
import java.util.StringTokenizer;
import java.util.NoSuchElementException;
import org.apache.xerces.validators.schema.SchemaSymbols;
import org.apache.xerces.utils.regex.RegularExpression;

/**
 * QName Validator validates a QName type.
 * QName represents XML qualified names. The value 
 * space of QName is the set of tuples
 * {namespace name, local part}, where namespace 
 * name is a uriReference and local part is an NCName.
 * The lexical space of QName is the set of strings
 * that match the QName production of [Namespaces in
 * XML]. 
 * 
 * @author Jeffrey Rodriguez
 * @author Mark Swinkles - List Validation refactoring
 * @version $Id: QNameDatatypeValidator.java,v 1.1 2006/04/26 00:03:00 vauchers Exp $
 */
public class QNameDatatypeValidator extends  AbstractDatatypeValidator {
    private Locale    fLocale          = null;
    private DatatypeValidator    fBaseValidator   = null;

    private int       fLength          = 0;
    private int       fMaxLength       = Integer.MAX_VALUE;
    private int       fMinLength       = 0;
    private String    fPattern         = null;
    private Vector    fEnumeration     = null;
    private String    fMaxInclusive    = null;
    private String    fMaxExclusive    = null;
    private String    fMinInclusive    = null;
    private String    fMinExclusive    = null;
    private int       fFacetsDefined   = 0;

    private boolean isMaxExclusiveDefined = false;
    private boolean isMaxInclusiveDefined = false;
    private boolean isMinExclusiveDefined = false;
    private boolean isMinInclusiveDefined = false;
    private RegularExpression fRegex         = null;


    public QNameDatatypeValidator () throws InvalidDatatypeFacetException {
        this( null, null, false ); // Native, No Facets defined, Restriction
    }

    public QNameDatatypeValidator ( DatatypeValidator base, Hashtable facets, 
                                    boolean derivedByList ) throws InvalidDatatypeFacetException  {

        setBasetype( base ); // Set base type 

        // Set Facets if any defined
        if ( facets != null  ){
            for (Enumeration e = facets.keys(); e.hasMoreElements();) {
                String key = (String) e.nextElement();

                if ( key.equals(SchemaSymbols.ELT_LENGTH) ) {
                    fFacetsDefined += DatatypeValidator.FACET_LENGTH;
                    String lengthValue = (String)facets.get(key);
                    try {
                        fLength     = Integer.parseInt( lengthValue );
                    } catch (NumberFormatException nfe) {
                        throw new InvalidDatatypeFacetException("Length value '"+lengthValue+"' is invalid.");
                    }
                    if ( fLength < 0 )
                        throw new InvalidDatatypeFacetException("Length value '"+lengthValue+"'  must be a nonNegativeInteger.");

                } else if (key.equals(SchemaSymbols.ELT_MINLENGTH) ) {
                    fFacetsDefined += DatatypeValidator.FACET_MINLENGTH;
                    String minLengthValue = (String)facets.get(key);
                    try {
                        fMinLength     = Integer.parseInt( minLengthValue );
                    } catch (NumberFormatException nfe) {
                        throw new InvalidDatatypeFacetException("maxLength value '"+minLengthValue+"' is invalid.");
                    }
                } else if (key.equals(SchemaSymbols.ELT_MAXLENGTH) ) {
                    fFacetsDefined += DatatypeValidator.FACET_MAXLENGTH;
                    String maxLengthValue = (String)facets.get(key);
                    try {
                        fMaxLength     = Integer.parseInt( maxLengthValue );
                    } catch (NumberFormatException nfe) {
                        throw new InvalidDatatypeFacetException("maxLength value '"+maxLengthValue+"' is invalid.");
                    }
                } else if (key.equals(SchemaSymbols.ELT_PATTERN)) {
                    fFacetsDefined += DatatypeValidator.FACET_PATTERN;
                    fPattern = (String)facets.get(key);
                    fRegex   = new RegularExpression(fPattern, "X");
                } else if (key.equals(SchemaSymbols.ELT_ENUMERATION)) {
                    fFacetsDefined += DatatypeValidator.FACET_ENUMERATION;
                    fEnumeration = (Vector)facets.get(key);
                } else if (key.equals(SchemaSymbols.ELT_MAXINCLUSIVE)) {
                    fFacetsDefined += DatatypeValidator.FACET_MAXINCLUSIVE;
                    fMaxInclusive = (String)facets.get(key);
                } else if (key.equals(SchemaSymbols.ELT_MAXEXCLUSIVE)) {
                    fFacetsDefined += DatatypeValidator.FACET_MAXEXCLUSIVE;
                    fMaxExclusive = (String)facets.get(key);
                } else if (key.equals(SchemaSymbols.ELT_MININCLUSIVE)) {
                    fFacetsDefined += DatatypeValidator.FACET_MININCLUSIVE;
                    fMinInclusive = (String)facets.get(key);
                } else if (key.equals(SchemaSymbols.ELT_MINEXCLUSIVE)) {
                    fFacetsDefined += DatatypeValidator.FACET_MINEXCLUSIVE;
                    fMinExclusive = (String)facets.get(key);
                } else {
                    throw new InvalidDatatypeFacetException();
                }
            }

            if (((fFacetsDefined & DatatypeValidator.FACET_LENGTH ) != 0 ) ) {
                if (((fFacetsDefined & DatatypeValidator.FACET_MAXLENGTH ) != 0 ) ) {
                    throw new InvalidDatatypeFacetException(
                                                            "It is an error for both length and maxLength to be members of facets." );  
                } else if (((fFacetsDefined & DatatypeValidator.FACET_MINLENGTH ) != 0 ) ) {
                    throw new InvalidDatatypeFacetException(
                                                            "It is an error for both length and minLength to be members of facets." );
                }
            }

            if ( ( (fFacetsDefined & ( DatatypeValidator.FACET_MINLENGTH |
                                        DatatypeValidator.FACET_MAXLENGTH) ) != 0 ) ) {
                if ( fMinLength > fMaxLength ) {
                    throw new InvalidDatatypeFacetException( "Value of maxLength = " + fMaxLength +
                                                                "must be greater that the value of minLength" + fMinLength );
                }
            }

            isMaxExclusiveDefined = ((fFacetsDefined & 
                                        DatatypeValidator.FACET_MAXEXCLUSIVE ) != 0 )?true:false;
            isMaxInclusiveDefined = ((fFacetsDefined & 
                                        DatatypeValidator.FACET_MAXINCLUSIVE ) != 0 )?true:false;
            isMinExclusiveDefined = ((fFacetsDefined &
                                        DatatypeValidator.FACET_MINEXCLUSIVE ) != 0 )?true:false;
            isMinInclusiveDefined = ((fFacetsDefined &
                                        DatatypeValidator.FACET_MININCLUSIVE ) != 0 )?true:false;

            if ( isMaxExclusiveDefined && isMaxInclusiveDefined ) {
                throw new InvalidDatatypeFacetException(
                                                        "It is an error for both maxInclusive and maxExclusive to be specified for the same datatype." ); 
            }
            if ( isMinExclusiveDefined && isMinInclusiveDefined ) {
                throw new InvalidDatatypeFacetException(
                                                        "It is an error for both minInclusive and minExclusive to be specified for the same datatype." ); 
            }
        }// End of Facets Setting

    }



/**
* validate that a string is a W3C string type
* 
* @param content A string containing the content to be validated
* @param list
* @exception throws InvalidDatatypeException if the content is
*                   not a W3C string type
* @exception InvalidDatatypeValueException
*/
    public Object validate(String content, Object state)  throws InvalidDatatypeValueException
    {
        checkContent( content );
        return null;
    }



/**
* set the locate to be used for error messages
*/
    public void setLocale(Locale locale) {
        fLocale = locale;
    }




    private void checkContent( String content )throws InvalidDatatypeValueException
    {
        if ( (fFacetsDefined & DatatypeValidator.FACET_MAXLENGTH) != 0 ) {
            if ( content.length() > fMaxLength ) {
                throw new InvalidDatatypeValueException("Value '"+content+
                                                        "' with length '"+content.length()+
                                                        "' exceeds maximum length of "+fMaxLength+".");
            }
        }
        if ( (fFacetsDefined & DatatypeValidator.FACET_ENUMERATION) != 0 ) {
            if ( fEnumeration.contains( content ) == false )
                throw new InvalidDatatypeValueException("Value '"+content+"' must be one of "+fEnumeration);
        }


        if ( isMaxExclusiveDefined == true ) {
            int comparisonResult;
            comparisonResult  = compare( content, fMaxExclusive );
            //System.out.println( "maxExc = " + comparisonResult );

            if ( comparisonResult >= 0 ) {
                throw new InvalidDatatypeValueException( "Value '"+content+ "'  must be " +
                                  "lexicographically less than '" + fMaxExclusive + "'."  );

            }

        }
        if ( isMaxInclusiveDefined == true ) {
            int comparisonResult;
            comparisonResult  = compare( content, fMaxInclusive );
            if ( comparisonResult > 0 )
                throw new InvalidDatatypeValueException( "Value '"+content+ "' must be " +
                                  "lexicographically less or equal than '" + fMaxInclusive +"'." );
        }

        if ( isMinExclusiveDefined == true ) {
            int comparisonResult;
            comparisonResult  = compare( content, fMinExclusive );
            //System.out.println( "minExc = " + comparisonResult );

            if ( comparisonResult <= 0 )
                throw new InvalidDatatypeValueException( "Value '"+content+ "' must be " +
                                                         "lexicographically greater than '" + fMinExclusive + "'." );
        }
        if ( isMinInclusiveDefined == true ) {
            int comparisonResult;
            comparisonResult = compare( content, fMinInclusive );
            //System.out.println( "minInc = " + comparisonResult );
            if ( comparisonResult < 0 )
                throw new InvalidDatatypeValueException( "Value '"+content+ "' must be " +
                                                         "lexicographically greater or equal than '" + fMinInclusive  + "'." );
        }


        if ( (fFacetsDefined & DatatypeValidator.FACET_PATTERN ) != 0 ) {
            if ( fRegex == null || fRegex.matches( content) == false )
                throw new InvalidDatatypeValueException("Value '"+content+
                                                        "' does not match regular expression facet '" + fPattern + "'." );
        }
    }

    public Hashtable getFacets(){
        return null;
    }

    public int compare( String content, String facetValue ){
        Locale    loc       = Locale.getDefault();
        Collator  collator  = Collator.getInstance( loc );
        return collator.compare( content, facetValue );
    }



    /**
       * Returns a copy of this object.
       */
    public Object clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException("clone() is not supported in "+this.getClass().getName());
    }

    private void setBasetype( DatatypeValidator base) {
        fBaseValidator = base;
    }
}

