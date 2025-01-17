
/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 1999-2002 The Apache Software Foundation.  All rights
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
 * originally based on software copyright (c) 2002, International
 * Business Machines, Inc., http://www.apache.org.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */

package dom;
import  org.w3c.dom.*;
import  org.w3c.dom.ls.*;

/**
 * This sample program illustrates how to use DOM L3 
 * DOMBuilder, DOMWriter and other DOM L3 functionality
 * to preparse, revalidate and safe document.
 */
public class DOM3 implements DOMErrorHandler {

    /** Default namespaces support (true). */
    protected static final boolean DEFAULT_NAMESPACES = true;

    /** Default validation support (false). */
    protected static final boolean DEFAULT_VALIDATION = false;

    /** Default Schema validation support (false). */
    protected static final boolean DEFAULT_SCHEMA_VALIDATION = false;


    public static void main( String[] argv) {

        if (argv.length == 0) {
            printUsage();
            System.exit(1);
        }


        try {

            // get DOM Implementation using DOM Registry
            System.setProperty(DOMImplementationRegistry.PROPERTY,"org.apache.xerces.dom.DOMImplementationSourceImpl");
            DOMImplementationLS impl = 
                (DOMImplementationLS)DOMImplementationRegistry.getDOMImplementation("LS-Load");

            // create DOMBuilder
            DOMBuilder builder = impl.createDOMBuilder(DOMImplementationLS.MODE_SYNCHRONOUS, null);
            

            // create Error Handler
            DOMErrorHandler errorHandler = new DOM3();

            // set error handler
            builder.setErrorHandler(errorHandler);

            // set validation feature
            builder.setFeature("validate",true);
            
            // parse document
            System.out.println("Parsing "+argv[0]+"...");
            Document doc = builder.parseURI(argv[0]);

            // set error handler on the Document
            doc.setErrorHandler(errorHandler);

            // set validation feature
            doc.setNormalizationFeature("validate", true);
            
            // remove comments from the document
            doc.setNormalizationFeature("comments", false);

            System.out.println("Normalizing document... ");
            doc.normalizeDocument();


            // create DOMWriter
            DOMWriter domWriter = impl.createDOMWriter();
            
            System.out.println("Serializing document... ");
            // serialize document to standard output
            domWriter.writeNode(System.out, doc);


        } catch ( Exception ex ) {
            ex.printStackTrace();
        }
    }


    private static void printUsage() {

        System.err.println("usage: java dom.DOM3 uri ...");
        System.err.println();
        System.err.println("NOTE: You can only validate DOM tree against XML Schemas.");

    } // printUsage()


    public boolean handleError(DOMError error){
        short severity = error.getSeverity();
        if (severity == error.SEVERITY_ERROR) {
            System.out.println("[dom3-error]: "+error.getMessage());
        }

        if (severity == error.SEVERITY_WARNING) {
            System.out.println("[dom3-warning]: "+error.getMessage());
        }
        return true;

    }
}



