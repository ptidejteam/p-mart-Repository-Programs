/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 1999 The Apache Software Foundation.  All rights 
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
 * 4. The names "Xalan" and "Apache Software Foundation" must
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
 * originally based on software copyright (c) 1999, Lotus
 * Development Corporation., http://www.lotus.com.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */
package org.apache.xalan.xpath.xml;

import org.w3c.dom.*;

/**
 * <meta name="usage" content="general"/>
 * The implementation of the default error handling for 
 * Xalan.
 */
public class ProblemListenerDefault implements ProblemListener
{
  private java.io.PrintWriter out = null;

  private final static java.io.PrintWriter defaultOut = new java.io.PrintWriter(  System.err, true );  

  private static final String errorHeader = "Error: ";
  private static final String warningHeader = "Warning: ";
  private static final String messageHeader = "";

  private static final String xslHeader = "XSL ";
  private static final String xmlHeader = "XML ";
  private static final String queryHeader = "PATTERN ";

  /**
   * Create a ProblemListenerDefault using the default output stream (System.err)
   */

  public ProblemListenerDefault()
  {
	out = defaultOut;
  }
  
  /**
   * The default problem listener.
   */
  public boolean problem(short where, short classification, 
                     Object styleNode, Node sourceNode,
                     String msg, String id, int lineNo, int charOffset)
  {
    if(null != out)
    {
      synchronized(this)
      {  
        out.println(((XMLPARSER == where)
                   ? xmlHeader : (QUERYENGINE == where) 
                                 ? queryHeader : xslHeader)+
                  ((ERROR == classification)
                   ? errorHeader : (WARNING == classification) 
                                   ? warningHeader : messageHeader)+
                  msg+
                  ((null == styleNode)? "" : (", style tree node: "+styleNode.toString())) +
                  ((null == sourceNode)? "" : (", source tree node: "+sourceNode.getNodeName()))+
                  ((null == id)? "" : (", Location "+id))+
                  ((0 == lineNo)? "" : (", line "+lineNo))+
                  ((0 == charOffset)? "" : (", offset "+charOffset)));
      }
    }
    return classification == ERROR;
  }
  
  /**
   * Function that is called to issue a message.
   * @param   msg               A string message to output.
   */
  public boolean message(String msg)
  {
    if(null != out)
    {  
      synchronized (this)
      {  
        out.println( msg );
      } 
    }
    return false;                    // we don't know this is an error 
  }
  
  /**
   * Set where diagnostics will be written. If
   * the value is null, then diagnostics will be turned
   * off.
   */
  public void setDiagnosticsOutput(java.io.PrintWriter pw)
  {
    out = pw;
  }  
	  
}
