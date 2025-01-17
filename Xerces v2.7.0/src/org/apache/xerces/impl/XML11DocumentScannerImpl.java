/*
 * Copyright 1999-2004 The Apache Software Foundation.
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

package org.apache.xerces.impl;

import java.io.IOException;

import org.apache.xerces.impl.msg.XMLMessageFormatter;
import org.apache.xerces.util.XML11Char;
import org.apache.xerces.util.XMLChar;
import org.apache.xerces.util.XMLStringBuffer;
import org.apache.xerces.xni.XMLString;
import org.apache.xerces.xni.XNIException;

/**
 * This class is responsible for scanning XML document structure
 * and content. The scanner acts as the source for the document
 * information which is communicated to the document handler.
 * <p>
 * This component requires the following features and properties from the
 * component manager that uses it:
 * <ul>
 *  <li>http://xml.org/sax/features/namespaces</li>
 *  <li>http://xml.org/sax/features/validation</li>
 *  <li>http://apache.org/xml/features/nonvalidating/load-external-dtd</li>
 *  <li>http://apache.org/xml/features/scanner/notify-char-refs</li>
 *  <li>http://apache.org/xml/features/scanner/notify-builtin-refs</li>
 *  <li>http://apache.org/xml/properties/internal/symbol-table</li>
 *  <li>http://apache.org/xml/properties/internal/error-reporter</li>
 *  <li>http://apache.org/xml/properties/internal/entity-manager</li>
 *  <li>http://apache.org/xml/properties/internal/dtd-scanner</li>
 * </ul>
 *
 * @xerces.internal
 *
 * @author Glenn Marcy, IBM
 * @author Andy Clark, IBM
 * @author Arnaud  Le Hors, IBM
 * @author Eric Ye, IBM
 *
 * @version $Id: XML11DocumentScannerImpl.java,v 1.1 2006/02/02 00:59:17 vauchers Exp $
 */
public class XML11DocumentScannerImpl
    extends XMLDocumentScannerImpl {


    /** Array of 3 strings. */
    private String[] fStrings = new String[3];

    /** String. */
    private XMLString fString = new XMLString();

    /** String buffer. */
    private XMLStringBuffer fStringBuffer = new XMLStringBuffer();
    private XMLStringBuffer fStringBuffer2 = new XMLStringBuffer();
    private XMLStringBuffer fStringBuffer3 = new XMLStringBuffer();

    //
    // Constructors
    //

    /** Default constructor. */
    public XML11DocumentScannerImpl() {super();} // <init>()

    //
    // overridden methods
    //

    // XMLDocumentFragmentImpl methods

    /**
     * Scans element content.
     *
     * @return Returns the next character on the stream.
     */
    protected int scanContent() throws IOException, XNIException {

        XMLString content = fString;
        int c = fEntityScanner.scanContent(content);
        if (c == '\r' || c == 0x85 || c == 0x2028) {
            // happens when there is the character reference &#13;
            // but scanContent doesn't do entity expansions...
            // is this *really* necessary???  - NG
            fEntityScanner.scanChar();
            fStringBuffer.clear();
            fStringBuffer.append(fString);
            fStringBuffer.append((char)c);
            content = fStringBuffer;
            c = -1;
        }
        if (fDocumentHandler != null && content.length > 0) {
            fDocumentHandler.characters(content, null);
        }

        if (c == ']' && fString.length == 0) {
            fStringBuffer.clear();
            fStringBuffer.append((char)fEntityScanner.scanChar());
            // remember where we are in case we get an endEntity before we
            // could flush the buffer out - this happens when we're parsing an
            // entity which ends with a ]
            fInScanContent = true;
            //
            // We work on a single character basis to handle cases such as:
            // ']]]>' which we might otherwise miss.
            //
            if (fEntityScanner.skipChar(']')) {
                fStringBuffer.append(']');
                while (fEntityScanner.skipChar(']')) {
                    fStringBuffer.append(']');
                }
                if (fEntityScanner.skipChar('>')) {
                    reportFatalError("CDEndInContent", null);
                }
            }
            if (fDocumentHandler != null && fStringBuffer.length != 0) {
                fDocumentHandler.characters(fStringBuffer, null);
            }
            fInScanContent = false;
            c = -1;
        }
        return c;

    } // scanContent():int

    /**
     * Scans an attribute value and normalizes whitespace converting all
     * whitespace characters to space characters.
     * 
     * [10] AttValue ::= '"' ([^<&"] | Reference)* '"' | "'" ([^<&'] | Reference)* "'"
     *
     * @param value The XMLString to fill in with the value.
     * @param nonNormalizedValue The XMLString to fill in with the 
     *                           non-normalized value.
     * @param atName The name of the attribute being parsed (for error msgs).
     * @param checkEntities true if undeclared entities should be reported as VC violation,  
     *                      false if undeclared entities should be reported as WFC violation.
     * @param eleName The name of element to which this attribute belongs.
     *
     * @return true if the non-normalized and normalized value are the same
     * 
     * <strong>Note:</strong> This method uses fStringBuffer2, anything in it
     * at the time of calling is lost.
     **/
    protected boolean scanAttributeValue(XMLString value, 
                                      XMLString nonNormalizedValue,
                                      String atName,
                                      boolean checkEntities,String eleName)
        throws IOException, XNIException
    {
        // quote
        int quote = fEntityScanner.peekChar();
        if (quote != '\'' && quote != '"') {
            reportFatalError("OpenQuoteExpected", new Object[]{eleName,atName});
        }

        fEntityScanner.scanChar();
        int entityDepth = fEntityDepth;

        int c = fEntityScanner.scanLiteral(quote, value);
        if (DEBUG_ATTR_NORMALIZATION) {
            System.out.println("** scanLiteral -> \""
                               + value.toString() + "\"");
        }
        
        int fromIndex = 0;
        if (c == quote && (fromIndex = isUnchangedByNormalization(value)) == -1) {
            /** Both the non-normalized and normalized attribute values are equal. **/
            nonNormalizedValue.setValues(value);
            int cquote = fEntityScanner.scanChar();
            if (cquote != quote) {
                reportFatalError("CloseQuoteExpected", new Object[]{eleName,atName});
            }
            return true;
        }
        fStringBuffer2.clear();
        fStringBuffer2.append(value);
        normalizeWhitespace(value, fromIndex);
        if (DEBUG_ATTR_NORMALIZATION) {
            System.out.println("** normalizeWhitespace -> \""
                               + value.toString() + "\"");
        }
        if (c != quote) {
            fScanningAttribute = true;
            fStringBuffer.clear();
            do {
                fStringBuffer.append(value);
                if (DEBUG_ATTR_NORMALIZATION) {
                    System.out.println("** value2: \""
                                       + fStringBuffer.toString() + "\"");
                }
                if (c == '&') {
                    fEntityScanner.skipChar('&');
                    if (entityDepth == fEntityDepth) {
                        fStringBuffer2.append('&');
                    }
                    if (fEntityScanner.skipChar('#')) {
                        if (entityDepth == fEntityDepth) {
                            fStringBuffer2.append('#');
                        }
                        int ch = scanCharReferenceValue(fStringBuffer, fStringBuffer2);
                        if (ch != -1) {
                            if (DEBUG_ATTR_NORMALIZATION) {
                                System.out.println("** value3: \""
                                                   + fStringBuffer.toString()
                                                   + "\"");
                            }
                        }
                    }
                    else {
                        String entityName = fEntityScanner.scanName();
                        if (entityName == null) {
                            reportFatalError("NameRequiredInReference", null);
                        }
                        else if (entityDepth == fEntityDepth) {
                            fStringBuffer2.append(entityName);
                        }
                        if (!fEntityScanner.skipChar(';')) {
                            reportFatalError("SemicolonRequiredInReference",
                                             new Object []{entityName});
                        }
                        else if (entityDepth == fEntityDepth) {
                            fStringBuffer2.append(';');
                        }
                        if (entityName == fAmpSymbol) {
                            fStringBuffer.append('&');
                            if (DEBUG_ATTR_NORMALIZATION) {
                                System.out.println("** value5: \""
                                                   + fStringBuffer.toString()
                                                   + "\"");
                            }
                        }
                        else if (entityName == fAposSymbol) {
                            fStringBuffer.append('\'');
                            if (DEBUG_ATTR_NORMALIZATION) {
                                System.out.println("** value7: \""
                                                   + fStringBuffer.toString()
                                                   + "\"");
                            }
                        }
                        else if (entityName == fLtSymbol) {
                            fStringBuffer.append('<');
                            if (DEBUG_ATTR_NORMALIZATION) {
                                System.out.println("** value9: \""
                                                   + fStringBuffer.toString()
                                                   + "\"");
                            }
                        }
                        else if (entityName == fGtSymbol) {
                            fStringBuffer.append('>');
                            if (DEBUG_ATTR_NORMALIZATION) {
                                System.out.println("** valueB: \""
                                                   + fStringBuffer.toString()
                                                   + "\"");
                            }
                        }
                        else if (entityName == fQuotSymbol) {
                            fStringBuffer.append('"');
                            if (DEBUG_ATTR_NORMALIZATION) {
                                System.out.println("** valueD: \""
                                                   + fStringBuffer.toString()
                                                   + "\"");
                            }
                        }
                        else {
                            if (fEntityManager.isExternalEntity(entityName)) {
                                reportFatalError("ReferenceToExternalEntity",
                                                 new Object[] { entityName });
                            }
                            else {
                                if (!fEntityManager.isDeclaredEntity(entityName)) {
                                    //WFC & VC: Entity Declared
                                    if (checkEntities) {
                                        if (fValidation) {
                                            fErrorReporter.reportError(XMLMessageFormatter.XML_DOMAIN,
                                                                       "EntityNotDeclared",
                                                                       new Object[]{entityName},
                                                                       XMLErrorReporter.SEVERITY_ERROR);
                                        }
                                    }
                                    else {
                                        reportFatalError("EntityNotDeclared",
                                                         new Object[]{entityName});
                                    }
                                }
                                fEntityManager.startEntity(entityName, true);
                            }
                        }
                    }
                }
                else if (c == '<') {
                    reportFatalError("LessthanInAttValue",
                                     new Object[] { eleName, atName });
                    fEntityScanner.scanChar();
                    if (entityDepth == fEntityDepth) {
                        fStringBuffer2.append((char)c);
                    }
                }
                else if (c == '%' || c == ']') {
                    fEntityScanner.scanChar();
                    fStringBuffer.append((char)c);
                    if (entityDepth == fEntityDepth) {
                        fStringBuffer2.append((char)c);
                    }
                    if (DEBUG_ATTR_NORMALIZATION) {
                        System.out.println("** valueF: \""
                                           + fStringBuffer.toString() + "\"");
                    }
                }
                // note that none of these characters should ever get through
                // XML11EntityScanner.  Not sure why
                // this check was originally necessary.  - NG
                else if (c == '\n' || c == '\r' || c == 0x85 || c == 0x2028) {
                    fEntityScanner.scanChar();
                    fStringBuffer.append(' ');
                    if (entityDepth == fEntityDepth) {
                        fStringBuffer2.append('\n');
                    }
                }
                else if (c != -1 && XMLChar.isHighSurrogate(c)) {
                    fStringBuffer3.clear();
                    if (scanSurrogates(fStringBuffer3)) {
                        fStringBuffer.append(fStringBuffer3);
                        if (entityDepth == fEntityDepth) {
                            fStringBuffer2.append(fStringBuffer3);
                        }
                        if (DEBUG_ATTR_NORMALIZATION) {
                            System.out.println("** valueI: \""
                                               + fStringBuffer.toString()
                                               + "\"");
                        }
                    }
                }
                else if (c != -1 && XML11Char.isXML11Invalid(c)) {
                    reportFatalError("InvalidCharInAttValue",
                                     new Object[] {eleName, atName, Integer.toString(c, 16)});
                    fEntityScanner.scanChar();
                    if (entityDepth == fEntityDepth) {
                        fStringBuffer2.append((char)c);
                    }
                }
                c = fEntityScanner.scanLiteral(quote, value);
                if (entityDepth == fEntityDepth) {
                    fStringBuffer2.append(value);
                }
                normalizeWhitespace(value);
            } while (c != quote || entityDepth != fEntityDepth);
            fStringBuffer.append(value);
            if (DEBUG_ATTR_NORMALIZATION) {
                System.out.println("** valueN: \""
                                   + fStringBuffer.toString() + "\"");
            }
            value.setValues(fStringBuffer);
            fScanningAttribute = false;
        }
        nonNormalizedValue.setValues(fStringBuffer2);

        // quote
        int cquote = fEntityScanner.scanChar();
        if (cquote != quote) {
            reportFatalError("CloseQuoteExpected", new Object[]{eleName,atName});
        }
        return nonNormalizedValue.equals(value.ch, value.offset, value.length);
    } // scanAttributeValue()

    //
    // XMLScanner methods
    //
    // NOTE:  this is a carbon copy of the code in XML11DTDScannerImpl;
    // we need to override these methods in both places.  
    // this needs to be refactored!!!  - NG
    /**
     * Scans public ID literal.
     *
     * [12] PubidLiteral ::= '"' PubidChar* '"' | "'" (PubidChar - "'")* "'" 
     * [13] PubidChar::= #x20 | #xD | #xA | [a-zA-Z0-9] | [-'()+,./:=?;!*#@$_%]
     *
     * The returned string is normalized according to the following rule,
     * from http://www.w3.org/TR/REC-xml#dt-pubid:
     *
     * Before a match is attempted, all strings of white space in the public
     * identifier must be normalized to single space characters (#x20), and
     * leading and trailing white space must be removed.
     *
     * @param literal The string to fill in with the public ID literal.
     * @return True on success.
     *
     * <strong>Note:</strong> This method uses fStringBuffer, anything in it at
     * the time of calling is lost.
     */
    protected boolean scanPubidLiteral(XMLString literal)
        throws IOException, XNIException
    {
        int quote = fEntityScanner.scanChar();
        if (quote != '\'' && quote != '"') {
            reportFatalError("QuoteRequiredInPublicID", null);
            return false;
        }

        fStringBuffer.clear();
        // skip leading whitespace
        boolean skipSpace = true;
        boolean dataok = true;
        while (true) {
            int c = fEntityScanner.scanChar();
            // REVISIT:  none of these except \n and 0x20 should make it past the entity scanner
            if (c == ' ' || c == '\n' || c == '\r' || c == 0x85 || c == 0x2028) {
                if (!skipSpace) {
                    // take the first whitespace as a space and skip the others
                    fStringBuffer.append(' ');
                    skipSpace = true;
                }
            }
            else if (c == quote) {
                if (skipSpace) {
                    // if we finished on a space let's trim it
                    fStringBuffer.length--;
                }
                literal.setValues(fStringBuffer);
                break;
            }
            else if (XMLChar.isPubid(c)) {
                fStringBuffer.append((char)c);
                skipSpace = false;
            }
            else if (c == -1) {
                reportFatalError("PublicIDUnterminated", null);
                return false;
            }
            else {
                dataok = false;
                reportFatalError("InvalidCharInPublicID",
                                 new Object[]{Integer.toHexString(c)});
            }
        }
        return dataok;
   }
   
    /**
     * Normalize whitespace in an XMLString converting all whitespace
     * characters to space characters.
     */
    protected void normalizeWhitespace(XMLString value) {
        int end = value.offset + value.length;
	    for (int i = value.offset; i < end; ++i) {
           int c = value.ch[i];
           if (XMLChar.isSpace(c)) {
               value.ch[i] = ' ';
           }
       }
    }
    
    /**
     * Normalize whitespace in an XMLString converting all whitespace
     * characters to space characters.
     */
    protected void normalizeWhitespace(XMLString value, int fromIndex) {
        int end = value.offset + value.length;
        for (int i = value.offset + fromIndex; i < end; ++i) {
            int c = value.ch[i];
            if (XMLChar.isSpace(c)) {
                value.ch[i] = ' ';
            }
        }
    }
    
    /**
     * Checks whether this string would be unchanged by normalization.
     * 
     * @return -1 if the value would be unchanged by normalization,
     * otherwise the index of the first whitespace character which
     * would be transformed.
     */
    protected int isUnchangedByNormalization(XMLString value) {
        int end = value.offset + value.length;
        for (int i = value.offset; i < end; ++i) {
            int c = value.ch[i];
            if (XMLChar.isSpace(c)) {
                return i - value.offset;
            }
        }
        return -1;
    }

    // returns true if the given character is not
    // valid with respect to the version of
    // XML understood by this scanner.
    protected boolean isInvalid(int value) {
        return (XML11Char.isXML11Invalid(value)); 
    } // isInvalid(int):  boolean 

    // returns true if the given character is not
    // valid or may not be used outside a character reference 
    // with respect to the version of XML understood by this scanner.
    protected boolean isInvalidLiteral(int value) {
        return (!XML11Char.isXML11ValidLiteral(value)); 
    } // isInvalidLiteral(int):  boolean

    // returns true if the given character is 
    // a valid nameChar with respect to the version of
    // XML understood by this scanner.
    protected boolean isValidNameChar(int value) {
        return (XML11Char.isXML11Name(value)); 
    } // isValidNameChar(int):  boolean

    // returns true if the given character is 
    // a valid nameStartChar with respect to the version of
    // XML understood by this scanner.
    protected boolean isValidNameStartChar(int value) {
        return (XML11Char.isXML11NameStart(value)); 
    } // isValidNameStartChar(int):  boolean
    
    // returns true if the given character is
    // a valid NCName character with respect to the version of
    // XML understood by this scanner.
    protected boolean isValidNCName(int value) {
        return (XML11Char.isXML11NCName(value));
    } // isValidNCName(int):  boolean
    
    // returns true if the given character is 
    // a valid high surrogate for a nameStartChar 
    // with respect to the version of XML understood 
    // by this scanner.
    protected boolean isValidNameStartHighSurrogate(int value) {
        return XML11Char.isXML11NameHighSurrogate(value); 
    } // isValidNameStartHighSurrogate(int):  boolean

    protected boolean versionSupported(String version) {
        return (version.equals("1.1") || version.equals("1.0"));
    } // versionSupported(String):  boolean
    
    // returns the error message key for unsupported
    // versions of XML with respect to the version of
    // XML understood by this scanner.
    protected String getVersionNotSupportedKey () {
        return "VersionNotSupported11";
    } // getVersionNotSupportedKey: String

} // class XML11DocumentScannerImpl
