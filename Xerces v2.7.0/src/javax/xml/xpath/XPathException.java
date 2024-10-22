/*
 * Copyright 2003-2004 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
// $Id: XPathException.java,v 1.1 2007/03/12 16:15:08 guehene Exp $

package javax.xml.xpath;

import java.io.PrintWriter;

/**
 * <code>XPathException</code> represents a generic XPath exception.</p>
 *
 * @author  <a href="Norman.Walsh@Sun.com">Norman Walsh</a>
 * @author <a href="mailto:Jeff.Suttor@Sun.COM">Jeff Suttor</a>
 * @version $Revision: 1.1 $, $Date: 2007/03/12 16:15:08 $
 * @since 1.5
 */
public class XPathException extends Exception {

    private final Throwable cause;
    
    /**
     * <p>Stream Unique Identifier.</p>
     */
    private static final long serialVersionUID = -1837080260374986980L;

    /**
     * <p>Constructs a new <code>XPathException</code> with the specified detail <code>message</code>.</p>
     *
     * <p>The <code>cause</code> is not initialized.</p>
     *
     * <p>If <code>message</code> is <code>null</code>, then a <code>NullPointerException</code> is thrown.</p>
     *
     * @param message The detail message.
     */
    public XPathException(String message) {
        super(message);
        if ( message == null ) {
            throw new NullPointerException ( "message can't be null");
        }
        this.cause = null;
    }

    /**
     * <p>Constructs a new <code>XPathException</code> with the specified <code>cause</code>.</p>
     *
     * <p>If <code>cause</code> is <code>null</code>, then a <code>NullPointerException</code> is thrown.</p>
     *
     * @param cause The cause.
     *
     * @throws NullPointerException if <code>cause</code> is <code>null</code>.
     */
    public XPathException(Throwable cause) {
        super(cause == null ? null : cause.toString());
        this.cause = cause;
        if ( cause == null ) {
            throw new NullPointerException ( "cause can't be null");
        }
    }
    
    public Throwable getCause() {
        return cause;
    }

    public void printStackTrace( java.io.PrintStream s ) {
        if( getCause() != null ) {
            getCause().printStackTrace(s);
          s.println("--------------- linked to ------------------");
        }

        super.printStackTrace(s);
    }
 
    public void printStackTrace() {
        printStackTrace(System.err);
    }

    public void printStackTrace(PrintWriter s) {
        if( getCause() != null ) {
            getCause().printStackTrace(s);
          s.println("--------------- linked to ------------------");
        }

        super.printStackTrace(s);
    }
}
