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
package org.apache.xalan.xslt.extensions;

import java.util.*;
import org.w3c.dom.*;
import org.apache.xalan.xslt.XSLProcessorContext;
import org.apache.xalan.xslt.XSLTEngineImpl;
import org.apache.xalan.xslt.ElemTemplateElement;
import org.apache.xalan.xslt.res.XSLTErrorResources;
import org.apache.xalan.xpath.XObject;
import org.apache.xalan.xslt.StylesheetRoot;
import org.apache.xalan.xslt.ElemExtensionCall;
import java.io.*;
import java.net.URL;
import org.apache.xalan.xpath.xml.*;
import org.apache.xml.serialize.*;
import org.xml.sax.DocumentHandler;

/**
 * Implements three extension elements to allow an XSLT transformation to
 * redirect its output to multiple output files.
 * You must declare the Xalan namespace (xmlns:lxslt="http://xml.apache.org/xslt"),
 * a namespace for the extension prefix (such as xmlns:redirect="org.apache.xalan.xslt.extensions.Redirect"),
 * and declare the extension namespace as an extension (extension-element-prefixes="redirect").
 * You can either just use redirect:write, in which case the file will be
 * opened and immediately closed after the write, or you can bracket the
 * write calls by redirect:open and redirect:close, in which case the
 * file will be kept open for multiple writes until the close call is
 * encountered.  Calls can be nested.  Calls can take a 'file' attribute
 * and/or a 'select' attribute in order to get the filename.  If a select
 * attribute is encountered, it will evaluate that expression for a string
 * that indicates the filename.  If the string evaluates to empty, it will
 * attempt to use the 'file' attribute as a default.  Filenames can be relative
 * or absolute.  If they are relative, the base directory will be the same as
 * the base directory for the output document (setOutputFileName(outFileName) must
 * be called first on the processor when using the API).
 *
 * <p>Example:</p>
 * <PRE>
 * &lt;?xml version="1.0"?>
 * &lt;xsl:stylesheet xmlns:xsl="http://www.w3.org/XSL/Transform/1.0"
 *                 xmlns:lxslt="http://xml.apache.org/xslt"
 *                 xmlns:redirect="org.apache.xalan.xslt.extensions.Redirect"
 *                 extension-element-prefixes="redirect">
 *
 *   &lt;xsl:template match="/">
 *     &lt;out>
 *       default output.
 *     &lt;/out>
 *     &lt;redirect:open file="doc3.out"/>
 *     &lt;redirect:write file="doc3.out">
 *       &lt;out>
 *         &lt;redirect:write file="doc1.out">
 *           &lt;out>
 *             doc1 output.
 *             &lt;redirect:write file="doc3.out">
 *               Some text to doc3
 *             &lt;/redirect:write>
 *           &lt;/out>
 *         &lt;/redirect:write>
 *         &lt;redirect:write file="doc2.out">
 *           &lt;out>
 *             doc2 output.
 *             &lt;redirect:write file="doc3.out">
 *               Some more text to doc3
 *               &lt;redirect:write select="doc/foo">
 *                 text for doc4
 *               &lt;/redirect:write>
 *             &lt;/redirect:write>
 *           &lt;/out>
 *         &lt;/redirect:write>
 *       &lt;/out>
 *     &lt;/redirect:write>
 *     &lt;redirect:close file="doc3.out"/>
 *   &lt;/xsl:template>
 *
 * &lt;/xsl:stylesheet>
 * </PRE>
 *
 * @author Scott Boag
 * @version 1.0
 * @see <a href="../../../../../../extensions.html#ex-redirect" target="_top">Example with Redirect extension</a>
 */
public class Redirect
{
  /**
   * List of formatter listeners indexed by filename.
   */
  protected Hashtable m_formatterListeners = new Hashtable ();

  /**
   * List of output streams indexed by filename.
   */
  protected Hashtable m_outputStreams = new Hashtable ();

  /**
   * Open the given file and put it in the XML, HTML, or Text formatter listener's table.
   */
  public void open(XSLProcessorContext context, Element elem)
    throws java.net.MalformedURLException,
           java.io.FileNotFoundException,
           java.io.IOException,
           org.xml.sax.SAXException
  {
    String fileName = getFilename(context, elem);
    Object flistener = m_formatterListeners.get(fileName);
    if(null == flistener)
    {
      String mkdirsExpr = ((ElemExtensionCall)elem).getAttribute ("mkdirs", context.sourceNode, context.processor);
      boolean mkdirs = (mkdirsExpr != null)
                       ? (mkdirsExpr.equals("true") || mkdirsExpr.equals("yes")) : true;
      DocumentHandler fl = makeFormatterListener(context, fileName, true, mkdirs);
      // fl.startDocument();
    }
  }

  /**
   * Write the evalutation of the element children to the given file. Then close the file
   * unless it was opened with the open extension element and is in the formatter listener's table.
   */
  public void write(XSLProcessorContext context, Element elem)
    throws java.net.MalformedURLException,
           java.io.FileNotFoundException,
           java.io.IOException,
           org.xml.sax.SAXException
  {
    String fileName = getFilename(context, elem);
    Object flObject = m_formatterListeners.get(fileName);
    DocumentHandler formatter;
    boolean inTable = false;
    if(null == flObject)
    {
      String mkdirsExpr = ((ElemExtensionCall)elem).getAttribute ("mkdirs", context.sourceNode, context.processor);
      boolean mkdirs = (mkdirsExpr != null)
                       ? (mkdirsExpr.equals("true") || mkdirsExpr.equals("yes")) : true;
      formatter = makeFormatterListener(context, fileName, true, mkdirs);
    }
    else
    {
      inTable = true;
      formatter = (DocumentHandler)flObject;
    }

    context.processor.writeChildren( formatter, context.stylesheetTree,
                                     (ElemTemplateElement)elem,
                                     context.sourceTree, context.sourceNode, context.mode);
    if(!inTable)
    {
      OutputStream ostream = (OutputStream)m_outputStreams.get(fileName);
      if(null != ostream)
      {
        formatter.endDocument();
        ostream.close();
        m_outputStreams.remove(fileName);
        m_formatterListeners.remove(fileName);
      }
    }
  }


  /**
   * Close the given file and remove it from the formatter listener's table.
   */
  public void close(XSLProcessorContext context, Element elem)
    throws java.net.MalformedURLException,
    java.io.FileNotFoundException,
    java.io.IOException,
    org.xml.sax.SAXException
  {
    String fileName = getFilename(context, elem);
    Object formatterObj = m_formatterListeners.get(fileName);
    if(null != formatterObj)
    {
      DocumentHandler fl = (DocumentHandler)formatterObj;
      fl.endDocument();
      OutputStream ostream = (OutputStream)m_outputStreams.get(fileName);
      if(null != ostream)
      {
        ostream.close();
        m_outputStreams.remove(fileName);
      }
      m_formatterListeners.remove(fileName);
    }
  }

  /**
   * Get the filename from the 'select' or the 'file' attribute.
   */
  private String getFilename(XSLProcessorContext context, Element elem)
    throws java.net.MalformedURLException,
    java.io.FileNotFoundException,
    java.io.IOException,
    org.xml.sax.SAXException
  {
    String fileName;
    String fileNameExpr = ((ElemExtensionCall)elem).getAttribute ("select", context.sourceNode, context.processor);
    if(null != fileNameExpr)
    {
      org.apache.xalan.xpath.XPathSupport execContext = context.processor.getExecContext();
      XObject xobj = context.processor.getStylesheet().evalXPathStr(execContext, fileNameExpr,
                                                                    context.sourceNode,
                                                                    execContext.getNamespaceContext());
      fileName = xobj.str();
      if((null == fileName) || (fileName.length() == 0))
      {
        fileName = ((ElemExtensionCall)elem).getAttribute ("file", context.sourceNode, context.processor);
      }
    }
    else
    {
      fileName = ((ElemExtensionCall)elem).getAttribute ("file", context.sourceNode, context.processor);
    }
    if(null == fileName)
    {
      context.processor.error(elem, context.sourceNode, XSLTErrorResources.ER_REDIRECT_COULDNT_GET_FILENAME);
                              //"Redirect extension: Could not get filename - file or select attribute must return vald string.");
    }
    return fileName;
  }

  /**
   * Create a new DocumentHandler, based on attributes of the current DocumentHandler.
   */
  private DocumentHandler makeFormatterListener(XSLProcessorContext context,
                                                String fileName,
                                                boolean shouldPutInTable,
                                                boolean mkdirs)
    throws java.net.MalformedURLException,
    java.io.FileNotFoundException,
    java.io.IOException,
    org.xml.sax.SAXException
  {
    File file = new File(fileName);
    if(!file.isAbsolute())
    {
      if(null != context.processor.getOutputFileName())
      {
        File baseFile = new File(context.processor.getOutputFileName());
        file = new File(baseFile.getParent(), fileName);
      }
    }

    if(mkdirs)
    {
      String dirStr = file.getParent();
      if((null != dirStr) && (dirStr.length() > 0))
      {
        File dir = new File(dirStr);
        dir.mkdirs();
      }
    }

    StylesheetRoot sr = context.stylesheetTree.m_stylesheetRoot;
    OutputFormat formatter = sr.getOutputFormat();

    FileOutputStream ostream = new FileOutputStream(file);

    DocumentHandler flistener
      = sr.makeSAXSerializer(ostream, formatter);

    flistener.startDocument();
    if(shouldPutInTable)
    {
      m_outputStreams.put(fileName, ostream);
      m_formatterListeners.put(fileName, flistener);
    }
    return flistener;
  }
}
