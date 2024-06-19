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
package org.apache.xalan.xslt;

import org.w3c.dom.*;
import java.util.Vector;
import org.apache.xalan.xpath.*;
import org.apache.xalan.xpath.xml.XMLParserLiaison;
import org.apache.xalan.xslt.res.XSLTErrorResources;
import org.apache.xalan.xpath.xml.XMLParserLiaisonDefault;
import org.apache.xalan.xpath.xml.*;

/**
 * <meta name="usage" content="advanced"/>
 * Execute the Doc() function.
 * 
 * When the document function has exactly one argument and the argument 
 * is a node-set, then the result is the union, for each node in the 
 * argument node-set, of the result of calling the document function with 
 * the first argument being the string-value of the node, and the second 
 * argument being a node-set with the node as its only member. When the 
 * document function has two arguments and the first argument is a node-set, 
 * then the result is the union, for each node in the argument node-set, 
 * of the result of calling the document function with the first argument 
 * being the string-value of the node, and with the second argument being 
 * the second argument passed to the document function.
 */
public class FuncDocument extends Function
{
  /**
   * Execute the function.  The function must return 
   * a valid object.
   * @param path The executing xpath.
   * @param context The current context.
   * @param opPos The current op position.
   * @param args A list of XObject arguments.
   * @return A valid XObject.
   */
  public XObject execute(XPath path, XPathSupport execContext, Node context, int opPos, Vector args) 
    throws org.xml.sax.SAXException
  {    
    Document docContext = (Node.DOCUMENT_NODE == context.getNodeType()) 
                          ? (Document)context : context.getOwnerDocument();
    XObject arg = (XObject)args.elementAt(0);
    int nRefs = (XObject.CLASS_NODESET == arg.getType()) 
                ? arg.nodeset().getLength()
                  : 1;
    String base = "";
    if(args.size() > 1)
    {
      // The URI reference may be relative. The base URI (see [3.2 Base URI]) 
      // of the node in the second argument node-set that is first in document 
      // order is used as the base URI for resolving the 
      // relative URI into an absolute URI. 
      XObject arg2 = (XObject)args.elementAt(1);
      if(XObject.CLASS_NODESET == arg2.getType())
      {
        Node baseNode = arg2.nodeset().item(0);
        Document baseDoc = (Node.DOCUMENT_NODE == baseNode.getNodeType()) ? 
                           (Document)baseNode : baseNode.getOwnerDocument();
        
        if(baseDoc instanceof Stylesheet)
        {
          // base = ((Stylesheet)baseDoc).getBaseIdentifier();
          base = execContext.getNamespaceContext().getBaseIdentifier();   
        }
        else
          base = execContext.findURIFromDoc(baseDoc);
      }
      else
      {
        base = arg2.str();
      }
     
    }
    else
    {
      // If the second argument is omitted, then it defaults to 
      // the node in the stylesheet that contains the expression that 
      // includes the call to the document function. Note that a 
      // zero-length URI reference is a reference to the document 
      // relative to which the URI reference is being resolved; thus 
      // document("") refers to the root node of the stylesheet; 
      // the tree representation of the stylesheet is exactly 
      // the same as if the XML document containing the stylesheet 
      // was the initial source document.
      base = execContext.getNamespaceContext().getBaseIdentifier();
    }
    XNodeSet nodes = new XNodeSet();
    MutableNodeList mnl = nodes.mutableNodeset();
    for(int i = 0; i < nRefs; i++)
    {
      String ref = (XObject.CLASS_NODESET == arg.getType())
                      ? XMLParserLiaisonDefault.getNodeData(arg.nodeset().item(i))
                        : arg.str();
      if(null == ref)
        continue;
      if(null == docContext)
      {
        error(execContext, XSLTErrorResources.ER_NO_CONTEXT_OWNERDOC, null); //"context does not have an owner document!");
      }
        
      // From http://www.ics.uci.edu/pub/ietf/uri/rfc1630.txt
      // A partial form can be distinguished from an absolute form in that the
      // latter must have a colon and that colon must occur before any slash
      // characters. Systems not requiring partial forms should not use any
      // unencoded slashes in their naming schemes.  If they do, absolute URIs
      // will still work, but confusion may result.
      int indexOfColon = ref.indexOf(':');
      int indexOfSlash = ref.indexOf('/');
      if((indexOfColon != -1) && (indexOfSlash != -1) && (indexOfColon < indexOfSlash))
      {
        // The url (or filename, for that matter) is absolute.
        base = null;
      }
      
      Document newDoc = getDoc(path, execContext, context, ref, base);
      // nodes.mutableNodeset().addNode(newDoc);  
      if(null != newDoc)
        mnl.addNodeInDocOrder(newDoc, true, execContext);
    }
    return nodes;
  }
  
  /**
   * HandleDocExpr
   */
  Document getDoc(XPath path, XPathSupport execContext, Node context, String uri, String base)
    throws org.xml.sax.SAXException
  {
    Document newDoc = (Document)execContext.getSourceDocsTable().get(uri);
    if(null == newDoc)
    {
      if(uri.length() == 0)
      {
        if(execContext.getNamespaceContext() instanceof ElemTemplateElement)
        {
          Stylesheet ss = ((ElemTemplateElement)execContext.getNamespaceContext()).m_stylesheet;
          uri = ss.getBaseIdentifier();
        }
      }
      
      { 
        // TODO: Note the the warning calls below go through XPath, instead 
        // of XSLT, as the should.
        java.net.URL url = null;
        try
        {
          XMLParserLiaison parserLiaison = (XMLParserLiaison)execContext;
          url = parserLiaison.getURLFromString(uri, base);
          if((null != url) && (url.toString().length() > 0))
          {
            XSLTInputSource inputSource = new XSLTInputSource(url.toString());
            parserLiaison.parse(inputSource);
            newDoc = parserLiaison.getDocument();
          }
          else
          {
            warn(execContext, XSLTErrorResources.WG_CANNOT_MAKE_URL_FROM, new Object[]{((base == null) ? "" : base )+uri}); //"Can not make URL from: "+((base == null) ? "" : base )+uri);
          }
        }
        catch(Exception e)
        {
          newDoc = null;
          // path.warn(XSLTErrorResources.WG_ENCODING_NOT_SUPPORTED_USING_JAVA, new Object[]{((base == null) ? "" : base )+uri}); //"Can not load requested doc: "+((base == null) ? "" : base )+uri);
        }
        if(null == newDoc)
        {
          warn(execContext, XSLTErrorResources.WG_CANNOT_LOAD_REQUESTED_DOC, new Object[]{url== null ?((base == null) ? "" : base)+uri : url.toString()}); //"Can not load requested doc: "+((base == null) ? "" : base )+uri);
        }
        else
        {
          execContext.getSourceDocsTable().put(uri, newDoc);
        }
      }
    }
    return newDoc;
  }
  
  /**
   * Tell the user of an error, and probably throw an
   * exception.
   * @exception XSLProcessorException thrown if the active ProblemListener and XMLParserLiaison decide
   * the error condition is severe enough to halt processing.
   */
  public void error(XPathSupport execContext, int msg, Object args[])
    throws org.xml.sax.SAXException
  {	
    String formattedMsg = XSLMessages.createMessage(msg, args);
    
    XMLParserLiaison parserLiaison = (XMLParserLiaison)execContext;
    boolean shouldThrow = parserLiaison.getProblemListener().problem(ProblemListener.XSLPROCESSOR,
                                                    ProblemListener.ERROR,
                                                    null, null, formattedMsg,
                                                    null, 0, 0);                                                    
  }
  
  /**
   * Warn the user of a problem.
   * @exception XSLProcessorException thrown if the active ProblemListener and XMLParserLiaison decide
   * the error condition is severe enough to halt processing.
   */
  public void warn(XPathSupport execContext, int msg, Object args[])
    throws org.xml.sax.SAXException
  {
    String formattedMsg = XSLMessages.createWarning(msg, args);
    
    XMLParserLiaison parserLiaison = (XMLParserLiaison)execContext;
    boolean shouldThrow = parserLiaison.getProblemListener().problem(ProblemListener.XSLPROCESSOR,
                                                    ProblemListener.WARNING,
                                                    null, null, formattedMsg,
                                                    null, 0, 0); 
                                                    
    if(shouldThrow)
    {
      throw new XSLProcessorException(formattedMsg);
    }
  }

}
