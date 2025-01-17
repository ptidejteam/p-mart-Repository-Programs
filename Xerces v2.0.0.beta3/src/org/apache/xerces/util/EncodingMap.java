/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 1998,1999,2000 The Apache Software Foundation.  
 * All rights reserved.
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

package org.apache.xerces.util;

import java.util.Hashtable;

/**
 * EncodingMap is a convenience class which handles conversions between 
 * IANA encoding names and Java encoding names, and vice versa. The
 * encoding names used in XML instance documents <strong>must</strong>
 * be the IANA encoding names specified.
 * <p>
 * <TABLE BORDER="0" WIDTH="100%">
 *  <TR>
 *      <TD WIDTH="33%">
 *          <P ALIGN="CENTER"><B>Common Name</B>
 *      </TD>
 *      <TD WIDTH="15%">
 *          <P ALIGN="CENTER"><B>Use this name in XML files</B>
 *      </TD>
 *      <TD WIDTH="12%">
 *          <P ALIGN="CENTER"><B>Name Type</B>
 *      </TD>
 *      <TD WIDTH="31%">
 *          <P ALIGN="CENTER"><B>Xerces converts to this Java Encoder Name</B>
 *      </TD>
 *  </TR>
 *  <TR>
 *      <TD WIDTH="33%">8 bit Unicode</TD>
 *      <TD WIDTH="15%">
 *          <P ALIGN="CENTER">UTF-8
 *      </TD>
 *      <TD WIDTH="12%">
 *          <P ALIGN="CENTER">IANA
 *      </TD>
 *      <TD WIDTH="31%">
 *          <P ALIGN="CENTER">UTF8
 *      </TD>
 *  </TR>
 *  <TR>
 *      <TD WIDTH="33%">ISO Latin 1</TD>
 *      <TD WIDTH="15%">
 *          <P ALIGN="CENTER">ISO-8859-1
 *      </TD>
 *      <TD WIDTH="12%">
 *          <P ALIGN="CENTER">MIME
 *      </TD>
 *      <TD WIDTH="31%">
 *          <P ALIGN="CENTER">ISO-8859-1
 *      </TD>
 *  </TR>
 *  <TR>
 *      <TD WIDTH="33%">ISO Latin 2</TD>
 *      <TD WIDTH="15%">
 *          <P ALIGN="CENTER">ISO-8859-2
 *      </TD>
 *      <TD WIDTH="12%">
 *          <P ALIGN="CENTER">MIME
 *      </TD>
 *      <TD WIDTH="31%">
 *          <P ALIGN="CENTER">ISO-8859-2
 *      </TD>
 *  </TR>
 *  <TR>
 *      <TD WIDTH="33%">ISO Latin 3</TD>
 *      <TD WIDTH="15%">
 *          <P ALIGN="CENTER">ISO-8859-3
 *      </TD>
 *      <TD WIDTH="12%">
 *          <P ALIGN="CENTER">MIME
 *      </TD>
 *      <TD WIDTH="31%">
 *          <P ALIGN="CENTER">ISO-8859-3
 *      </TD>
 *  </TR>
 *  <TR>
 *      <TD WIDTH="33%">ISO Latin 4</TD>
 *      <TD WIDTH="15%">
 *          <P ALIGN="CENTER">ISO-8859-4
 *      </TD>
 *      <TD WIDTH="12%">
 *          <P ALIGN="CENTER">MIME
 *      </TD>
 *      <TD WIDTH="31%">
 *          <P ALIGN="CENTER">ISO-8859-4
 *      </TD>
 *  </TR>
 *  <TR>
 *      <TD WIDTH="33%">ISO Latin Cyrillic</TD>
 *      <TD WIDTH="15%">
 *          <P ALIGN="CENTER">ISO-8859-5
 *      </TD>
 *      <TD WIDTH="12%">
 *          <P ALIGN="CENTER">MIME
 *      </TD>
 *      <TD WIDTH="31%">
 *          <P ALIGN="CENTER">ISO-8859-5
 *      </TD>
 *  </TR>
 *  <TR>
 *      <TD WIDTH="33%">ISO Latin Arabic</TD>
 *      <TD WIDTH="15%">
 *          <P ALIGN="CENTER">ISO-8859-6
 *      </TD>
 *      <TD WIDTH="12%">
 *          <P ALIGN="CENTER">MIME
 *      </TD>
 *      <TD WIDTH="31%">
 *          <P ALIGN="CENTER">ISO-8859-6
 *      </TD>
 *  </TR>
 *  <TR>
 *      <TD WIDTH="33%">ISO Latin Greek</TD>
 *      <TD WIDTH="15%">
 *          <P ALIGN="CENTER">ISO-8859-7
 *      </TD>
 *      <TD WIDTH="12%">
 *          <P ALIGN="CENTER">MIME
 *      </TD>
 *      <TD WIDTH="31%">
 *          <P ALIGN="CENTER">ISO-8859-7
 *      </TD>
 *  </TR>
 *  <TR>
 *      <TD WIDTH="33%">ISO Latin Hebrew</TD>
 *      <TD WIDTH="15%">
 *          <P ALIGN="CENTER">ISO-8859-8
 *      </TD>
 *      <TD WIDTH="12%">
 *          <P ALIGN="CENTER">MIME
 *      </TD>
 *      <TD WIDTH="31%">
 *          <P ALIGN="CENTER">ISO-8859-8
 *      </TD>
 *  </TR>
 *  <TR>
 *      <TD WIDTH="33%">ISO Latin 5</TD>
 *      <TD WIDTH="15%">
 *          <P ALIGN="CENTER">ISO-8859-9
 *      </TD>
 *      <TD WIDTH="12%">
 *          <P ALIGN="CENTER">MIME
 *      </TD>
 *      <TD WIDTH="31%">
 *          <P ALIGN="CENTER">ISO-8859-9
 *      </TD>
 *  </TR>
 *  <TR>
 *      <TD WIDTH="33%">EBCDIC: US</TD>
 *      <TD WIDTH="15%">
 *          <P ALIGN="CENTER">ebcdic-cp-us
 *      </TD>
 *      <TD WIDTH="12%">
 *          <P ALIGN="CENTER">IANA
 *      </TD>
 *      <TD WIDTH="31%">
 *          <P ALIGN="CENTER">cp037
 *      </TD>
 *  </TR>
 *  <TR>
 *      <TD WIDTH="33%">EBCDIC: Canada</TD>
 *      <TD WIDTH="15%">
 *          <P ALIGN="CENTER">ebcdic-cp-ca
 *      </TD>
 *      <TD WIDTH="12%">
 *          <P ALIGN="CENTER">IANA
 *      </TD>
 *      <TD WIDTH="31%">
 *          <P ALIGN="CENTER">cp037
 *      </TD>
 *  </TR>
 *  <TR>
 *      <TD WIDTH="33%">EBCDIC: Netherlands</TD>
 *      <TD WIDTH="15%">
 *          <P ALIGN="CENTER">ebcdic-cp-nl
 *      </TD>
 *      <TD WIDTH="12%">
 *          <P ALIGN="CENTER">IANA
 *      </TD>
 *      <TD WIDTH="31%">
 *          <P ALIGN="CENTER">cp037
 *      </TD>
 *  </TR>
 *  <TR>
 *      <TD WIDTH="33%">EBCDIC: Denmark</TD>
 *      <TD WIDTH="15%">
 *          <P ALIGN="CENTER">ebcdic-cp-dk
 *      </TD>
 *      <TD WIDTH="12%">
 *          <P ALIGN="CENTER">IANA
 *      </TD>
 *      <TD WIDTH="31%">
 *          <P ALIGN="CENTER">cp277
 *      </TD>
 *  </TR>
 *  <TR>
 *      <TD WIDTH="33%">EBCDIC: Norway</TD>
 *      <TD WIDTH="15%">
 *          <P ALIGN="CENTER">ebcdic-cp-no
 *      </TD>
 *      <TD WIDTH="12%">
 *          <P ALIGN="CENTER">IANA
 *      </TD>
 *      <TD WIDTH="31%">
 *          <P ALIGN="CENTER">cp277
 *      </TD>
 *  </TR>
 *  <TR>
 *      <TD WIDTH="33%">EBCDIC: Finland</TD>
 *      <TD WIDTH="15%">
 *          <P ALIGN="CENTER">ebcdic-cp-fi
 *      </TD>
 *      <TD WIDTH="12%">
 *          <P ALIGN="CENTER">IANA
 *      </TD>
 *      <TD WIDTH="31%">
 *          <P ALIGN="CENTER">cp278
 *      </TD>
 *  </TR>
 *  <TR>
 *      <TD WIDTH="33%">EBCDIC: Sweden</TD>
 *      <TD WIDTH="15%">
 *          <P ALIGN="CENTER">ebcdic-cp-se
 *      </TD>
 *      <TD WIDTH="12%">
 *          <P ALIGN="CENTER">IANA
 *      </TD>
 *      <TD WIDTH="31%">
 *          <P ALIGN="CENTER">cp278
 *      </TD>
 *  </TR>
 *  <TR>
 *      <TD WIDTH="33%">EBCDIC: Italy</TD>
 *      <TD WIDTH="15%">
 *          <P ALIGN="CENTER">ebcdic-cp-it
 *      </TD>
 *      <TD WIDTH="12%">
 *          <P ALIGN="CENTER">IANA
 *      </TD>
 *      <TD WIDTH="31%">
 *          <P ALIGN="CENTER">cp280
 *      </TD>
 *  </TR>
 *  <TR>
 *      <TD WIDTH="33%">EBCDIC: Spain, Latin America</TD>
 *      <TD WIDTH="15%">
 *          <P ALIGN="CENTER">ebcdic-cp-es
 *      </TD>
 *      <TD WIDTH="12%">
 *          <P ALIGN="CENTER">IANA
 *      </TD>
 *      <TD WIDTH="31%">
 *          <P ALIGN="CENTER">cp284
 *      </TD>
 *  </TR>
 *  <TR>
 *      <TD WIDTH="33%">EBCDIC: Great Britain</TD>
 *      <TD WIDTH="15%">
 *          <P ALIGN="CENTER">ebcdic-cp-gb
 *      </TD>
 *      <TD WIDTH="12%">
 *          <P ALIGN="CENTER">IANA
 *      </TD>
 *      <TD WIDTH="31%">
 *          <P ALIGN="CENTER">cp285
 *      </TD>
 *  </TR>
 *  <TR>
 *      <TD WIDTH="33%">EBCDIC: France</TD>
 *      <TD WIDTH="15%">
 *          <P ALIGN="CENTER">ebcdic-cp-fr
 *      </TD>
 *      <TD WIDTH="12%">
 *          <P ALIGN="CENTER">IANA
 *      </TD>
 *      <TD WIDTH="31%">
 *          <P ALIGN="CENTER">cp297
 *      </TD>
 *  </TR>
 *  <TR>
 *      <TD WIDTH="33%">EBCDIC: Arabic</TD>
 *      <TD WIDTH="15%">
 *          <P ALIGN="CENTER">ebcdic-cp-ar1
 *      </TD>
 *      <TD WIDTH="12%">
 *          <P ALIGN="CENTER">IANA
 *      </TD>
 *      <TD WIDTH="31%">
 *          <P ALIGN="CENTER">cp420
 *      </TD>
 *  </TR>
 *  <TR>
 *      <TD WIDTH="33%">EBCDIC: Hebrew</TD>
 *      <TD WIDTH="15%">
 *          <P ALIGN="CENTER">ebcdic-cp-he
 *      </TD>
 *      <TD WIDTH="12%">
 *          <P ALIGN="CENTER">IANA
 *      </TD>
 *      <TD WIDTH="31%">
 *          <P ALIGN="CENTER">cp424
 *      </TD>
 *  </TR>
 *  <TR>
 *      <TD WIDTH="33%">EBCDIC: Switzerland</TD>
 *      <TD WIDTH="15%">
 *          <P ALIGN="CENTER">ebcdic-cp-ch
 *      </TD>
 *      <TD WIDTH="12%">
 *          <P ALIGN="CENTER">IANA
 *      </TD>
 *      <TD WIDTH="31%">
 *          <P ALIGN="CENTER">cp500
 *      </TD>
 *  </TR>
 *  <TR>
 *      <TD WIDTH="33%">EBCDIC: Roece</TD>
 *      <TD WIDTH="15%">
 *          <P ALIGN="CENTER">ebcdic-cp-roece
 *      </TD>
 *      <TD WIDTH="12%">
 *          <P ALIGN="CENTER">IANA
 *      </TD>
 *      <TD WIDTH="31%">
 *          <P ALIGN="CENTER">cp870
 *      </TD>
 *  </TR>
 *  <TR>
 *      <TD WIDTH="33%">EBCDIC: Yogoslavia</TD>
 *      <TD WIDTH="15%">
 *          <P ALIGN="CENTER">ebcdic-cp-yu
 *      </TD>
 *      <TD WIDTH="12%">
 *          <P ALIGN="CENTER">IANA
 *      </TD>
 *      <TD WIDTH="31%">
 *          <P ALIGN="CENTER">cp870
 *      </TD>
 *  </TR>
 *  <TR>
 *      <TD WIDTH="33%">EBCDIC: Iceland</TD>
 *      <TD WIDTH="15%">
 *          <P ALIGN="CENTER">ebcdic-cp-is
 *      </TD>
 *      <TD WIDTH="12%">
 *          <P ALIGN="CENTER">IANA
 *      </TD>
 *      <TD WIDTH="31%">
 *          <P ALIGN="CENTER">cp871
 *      </TD>
 *  </TR>
 *  <TR>
 *      <TD WIDTH="33%">EBCDIC: Urdu</TD>
 *      <TD WIDTH="15%">
 *          <P ALIGN="CENTER">ebcdic-cp-ar2
 *      </TD>
 *      <TD WIDTH="12%">
 *          <P ALIGN="CENTER">IANA
 *      </TD>
 *      <TD WIDTH="31%">
 *          <P ALIGN="CENTER">cp918
 *      </TD>
 *  </TR>
 *  <TR>
 *      <TD WIDTH="33%">Chinese for PRC, mixed 1/2 byte</TD>
 *      <TD WIDTH="15%">
 *          <P ALIGN="CENTER">gb2312
 *      </TD>
 *      <TD WIDTH="12%">
 *          <P ALIGN="CENTER">MIME
 *      </TD>
 *      <TD WIDTH="31%">
 *          <P ALIGN="CENTER">GB2312
 *      </TD>
 *  </TR>
 *  <TR>
 *      <TD WIDTH="33%">Extended Unix Code, packed for Japanese</TD>
 *      <TD WIDTH="15%">
 *          <P ALIGN="CENTER">euc-jp
 *      </TD>
 *      <TD WIDTH="12%">
 *          <P ALIGN="CENTER">MIME
 *      </TD>
 *      <TD WIDTH="31%">
 *          <P ALIGN="CENTER">eucjis
 *      </TD>
 *  </TR>
 *  <TR>
 *      <TD WIDTH="33%">Japanese: iso-2022-jp</TD>
 *      <TD WIDTH="15%">
 *          <P ALIGN="CENTER">iso-2020-jp
 *      </TD>
 *      <TD WIDTH="12%">
 *          <P ALIGN="CENTER">MIME
 *      </TD>
 *      <TD WIDTH="31%">
 *          <P ALIGN="CENTER">JIS
 *      </TD>
 *  </TR>
 *  <TR>
 *      <TD WIDTH="33%">Japanese: Shift JIS</TD>
 *      <TD WIDTH="15%">
 *          <P ALIGN="CENTER">Shift_JIS
 *      </TD>
 *      <TD WIDTH="12%">
 *          <P ALIGN="CENTER">MIME
 *      </TD>
 *      <TD WIDTH="31%">
 *          <P ALIGN="CENTER">SJIS
 *      </TD>
 *  </TR>
 *  <TR>
 *      <TD WIDTH="33%">Chinese: Big5</TD>
 *      <TD WIDTH="15%">
 *          <P ALIGN="CENTER">Big5
 *      </TD>
 *      <TD WIDTH="12%">
 *          <P ALIGN="CENTER">MIME
 *      </TD>
 *      <TD WIDTH="31%">
 *          <P ALIGN="CENTER">Big5
 *      </TD>
 *  </TR>
 *  <TR>
 *      <TD WIDTH="33%">Extended Unix Code, packed for Korean</TD>
 *      <TD WIDTH="15%">
 *          <P ALIGN="CENTER">euc-kr
 *      </TD>
 *      <TD WIDTH="12%">
 *          <P ALIGN="CENTER">MIME
 *      </TD>
 *      <TD WIDTH="31%">
 *          <P ALIGN="CENTER">iso2022kr
 *      </TD>
 *  </TR>
 *  <TR>
 *      <TD WIDTH="33%">Cyrillic</TD>
 *      <TD WIDTH="15%">
 *          <P ALIGN="CENTER">koi8-r
 *      </TD>
 *      <TD WIDTH="12%">
 *          <P ALIGN="CENTER">MIME
 *      </TD>
 *      <TD WIDTH="31%">
 *          <P ALIGN="CENTER">koi8-r
 *      </TD>
 *  </TR>
 * </TABLE>
 * 
 * @author TAMURA Kent, IBM
 * @author Stubs generated by DesignDoc on Wed Jun 07 11:58:44 PDT 2000
 * @author Andy Clark, IBM
 *
 * @version $Id: EncodingMap.java,v 1.2 2001/08/23 00:35:32 lehors Exp $
 */
public class EncodingMap {

    //
    // Data
    //

    /** fIANA2JavaMap */
    protected final static Hashtable fIANA2JavaMap = new Hashtable();

    /** fJava2IANAMap */
    protected final static Hashtable fJava2IANAMap = new Hashtable();

    //
    // Static initialization
    //

    static {

        // add IANA to Java encoding mappings.
        fIANA2JavaMap.put("BIG5",            "Big5");
        fIANA2JavaMap.put("EBCDIC-CP-US",    "CP037");
        fIANA2JavaMap.put("EBCDIC-CP-CA",    "CP037");
        fIANA2JavaMap.put("EBCDIC-CP-NL",    "CP037");
        fIANA2JavaMap.put("EBCDIC-CP-DK",    "CP277");
        fIANA2JavaMap.put("EBCDIC-CP-NO",    "CP277");
        fIANA2JavaMap.put("EBCDIC-CP-FI",    "CP278");
        fIANA2JavaMap.put("EBCDIC-CP-SE",    "CP278");
        fIANA2JavaMap.put("EBCDIC-CP-IT",    "CP280");
        fIANA2JavaMap.put("EBCDIC-CP-ES",    "CP284");
        fIANA2JavaMap.put("EBCDIC-CP-GB",    "CP285");
        fIANA2JavaMap.put("EBCDIC-CP-FR",    "CP297");
        fIANA2JavaMap.put("EBCDIC-CP-AR1",   "CP420");
        fIANA2JavaMap.put("EBCDIC-CP-HE",    "CP424");
        fIANA2JavaMap.put("EBCDIC-CP-CH",    "CP500");
        fIANA2JavaMap.put("EBCDIC-CP-ROECE", "CP870");
        fIANA2JavaMap.put("EBCDIC-CP-YU",    "CP870");
        fIANA2JavaMap.put("EBCDIC-CP-IS",    "CP871");
        fIANA2JavaMap.put("EBCDIC-CP-AR2",   "CP918");
        fIANA2JavaMap.put("EUC-JP",          "EUCJIS");
        fIANA2JavaMap.put("EUC-KR",          "KSC5601");
        fIANA2JavaMap.put("GB2312",          "GB2312");
        fIANA2JavaMap.put("ISO-2022-JP",     "JIS");
        fIANA2JavaMap.put("ISO-2022-KR",     "ISO2022KR");
        fIANA2JavaMap.put("ISO-8859-1",      "8859_1");
        fIANA2JavaMap.put("ISO-8859-2",      "8859_2");
        fIANA2JavaMap.put("ISO-8859-3",      "8859_3");
        fIANA2JavaMap.put("ISO-8859-4",      "8859_4");
        fIANA2JavaMap.put("ISO-8859-5",      "8859_5");
        fIANA2JavaMap.put("ISO-8859-6",      "8859_6");
        fIANA2JavaMap.put("ISO-8859-7",      "8859_7");
        fIANA2JavaMap.put("ISO-8859-8",      "8859_8");
        fIANA2JavaMap.put("ISO-8859-9",      "8859_9");
        fIANA2JavaMap.put("KOI8-R",          "KOI8_R");
        fIANA2JavaMap.put("US-ASCII",        "8859_1"); // ?
        fIANA2JavaMap.put("UTF-8",           "UTF8");
        fIANA2JavaMap.put("UTF-16",           "Unicode");

        // REVISIT:
        //   j:CNS11643 -> EUC-TW?
        //   ISO-2022-CN? ISO-2022-CN-EXT?
                                                
        // add Java to IANA encoding mappings
        //fJava2IANAMap.put("8859_1",    "US-ASCII"); // ?
        fJava2IANAMap.put("8859_1",    "ISO-8859-1");
        fJava2IANAMap.put("8859_2",    "ISO-8859-2");
        fJava2IANAMap.put("8859_3",    "ISO-8859-3");
        fJava2IANAMap.put("8859_4",    "ISO-8859-4");
        fJava2IANAMap.put("8859_5",    "ISO-8859-5");
        fJava2IANAMap.put("8859_6",    "ISO-8859-6");
        fJava2IANAMap.put("8859_7",    "ISO-8859-7");
        fJava2IANAMap.put("8859_8",    "ISO-8859-8");
        fJava2IANAMap.put("8859_9",    "ISO-8859-9");
        fJava2IANAMap.put("BIG5",      "Big5");
        fJava2IANAMap.put("CP037",     "EBCDIC-CP-US");
        fJava2IANAMap.put("CP037",     "EBCDIC-CP-CA");
        fJava2IANAMap.put("CP037",     "EBCDIC-CP-NL");
        fJava2IANAMap.put("CP277",     "EBCDIC-CP-DK");
        fJava2IANAMap.put("CP277",     "EBCDIC-CP-NO");
        fJava2IANAMap.put("CP278",     "EBCDIC-CP-FI");
        fJava2IANAMap.put("CP278",     "EBCDIC-CP-SE");
        fJava2IANAMap.put("CP280",     "EBCDIC-CP-IT");
        fJava2IANAMap.put("CP284",     "EBCDIC-CP-ES");
        fJava2IANAMap.put("CP285",     "EBCDIC-CP-GB");
        fJava2IANAMap.put("CP297",     "EBCDIC-CP-FR");
        fJava2IANAMap.put("CP420",     "EBCDIC-CP-AR1");
        fJava2IANAMap.put("CP424",     "EBCDIC-CP-HE");
        fJava2IANAMap.put("CP500",     "EBCDIC-CP-CH");
        fJava2IANAMap.put("CP870",     "EBCDIC-CP-ROECE");
        fJava2IANAMap.put("CP870",     "EBCDIC-CP-YU");
        fJava2IANAMap.put("CP871",     "EBCDIC-CP-IS");
        fJava2IANAMap.put("CP918",     "EBCDIC-CP-AR2");
        fJava2IANAMap.put("EUCJIS",    "EUC-JP");
        fJava2IANAMap.put("GB2312",    "GB2312");
        fJava2IANAMap.put("ISO2022KR", "ISO-2022-KR");
        fJava2IANAMap.put("JIS",       "ISO-2022-JP");
        fJava2IANAMap.put("KOI8_R",    "KOI8-R");
        fJava2IANAMap.put("KSC5601",   "EUC-KR");
        fJava2IANAMap.put("SJIS",      "Shift_JIS");
        fJava2IANAMap.put("UTF8",      "UTF-8");
        fJava2IANAMap.put("Unicode",   "UTF-16");

    } // <clinit>()

    //
    // Constructors
    //

    /** Default constructor. */
    public EncodingMap() {}

    //
    // Public static methods
    //

    /**
     * Adds an IANA to Java encoding name mapping.
     * 
     * @param ianaEncoding The IANA encoding name.
     * @param javaEncoding The Java encoding name.
     */
    public static void putIANA2JavaMapping(String ianaEncoding, 
                                           String javaEncoding) {
        fIANA2JavaMap.put(ianaEncoding, javaEncoding);
    } // putIANA2JavaMapping(String,String)

    /**
     * Returns the Java encoding name for the specified IANA encoding name.
     * 
     * @param ianaEncoding The IANA encoding name.
     */
    public static String getIANA2JavaMapping(String ianaEncoding) {
        return (String)fIANA2JavaMap.get(ianaEncoding);
    } // getIANA2JavaMapping(String):String

    /**
     * Removes an IANA to Java encoding name mapping.
     * 
     * @param ianaEncoding The IANA encoding name.
     */
    public static String removeIANA2JavaMapping(String ianaEncoding) {
        return (String)fIANA2JavaMap.remove(ianaEncoding);
    } // removeIANA2JavaMapping(String):String

    /**
     * Adds a Java to IANA encoding name mapping.
     * 
     * @param javaEncoding The Java encoding name.
     * @param ianaEncoding The IANA encoding name.
     */
    public static void putJava2IANAMapping(String javaEncoding, 
                                           String ianaEncoding) {
        fJava2IANAMap.put(javaEncoding, ianaEncoding);
    } // putJava2IANAMapping(String,String)

    /**
     * Returns the IANA encoding name for the specified Java encoding name.
     * 
     * @param javaEncoding The Java encoding name.
     */
    public static String getJava2IANAMapping(String javaEncoding) {
        return (String)fJava2IANAMap.get(javaEncoding);
    } // getJava2IANAMapping(String):String

    /**
     * Removes a Java to IANA encoding name mapping.
     * 
     * @param javaEncoding The Java encoding name.
     */
    public static String removeJava2IANAMapping(String javaEncoding) {
        return (String)fJava2IANAMap.remove(javaEncoding);
    } // removeJava2IANAMapping

} // class EncodingMap
