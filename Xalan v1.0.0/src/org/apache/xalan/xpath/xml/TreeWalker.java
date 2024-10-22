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
import org.xml.sax.*;
import org.xml.sax.ext.LexicalHandler;

/**
 * <meta name="usage" content="advanced"/>
 * This class does a pre-order walk of the DOM tree, calling the FormatterListener
 * interface as it goes.
 */
public class TreeWalker
{
  private DocumentHandler m_formatterListener =   null;
  
  /**
   * Get the DocumentHandler used for the tree walk.
   */
  public DocumentHandler getFormatterListener()
  {
    return m_formatterListener;
  }
  
  /**
   * Constructor.
   * @param   formatterListener The implemention of the 
   * FormatterListener operation (toXMLString, digest, ...)
   */
  public TreeWalker(DocumentHandler formatterListener) 
  {
    this.m_formatterListener = formatterListener;
  }

  /**
   * Perform a pre-order traversal non-recursive style.
   */
  public void traverse(Node pos) throws SAXException 
  {
    Node top = pos;
    while(null != pos)
    {     
      startNode(pos);
      
      Node nextNode = pos.getFirstChild();
      while(null == nextNode)
      {
        endNode(pos);
        if(top.equals( pos ))
          break;
        nextNode = pos.getNextSibling();
        if(null == nextNode)
        {
          pos = pos.getParentNode();
          if((null == pos) || (top.equals( pos )))
          {
            if(null != pos)
              endNode(pos);
            nextNode = null;
            break;
          }
        }
      }
      pos = nextNode;
    }
  }

  /**
   * Perform a pre-order traversal non-recursive style.
   */
  public void traverse(Node pos, Node top) throws SAXException 
  {
    while(null != pos)
    {     
      startNode(pos);
      
      Node nextNode = pos.getFirstChild();
      while(null == nextNode)
      {
        endNode(pos);
        if((null != top) && top.equals( pos ))
          break;
        nextNode = pos.getNextSibling();
        if(null == nextNode)
        {
          pos = pos.getParentNode();
          if((null == pos) || ((null != top) && top.equals( pos )))
          {
            nextNode = null;
            break;
          }
        }
      }
      pos = nextNode;
    }
  }
  
  /*
  public void traverse(Node pos) throws SAXException 
  {
    startNode(pos);
    NodeList children = pos.getChildNodes();
    if(null != children)
    {
      int nChildren = children.getLength();
      for(int i = 0; i < nChildren; i++)
      {
        traverse(children.item(i));
      }
    }
    endNode(pos);
  }
  */
  
  boolean nextIsRaw = false;
  
  protected void startNode(Node node)
    throws SAXException 
  {
    switch(node.getNodeType())
    {
    case Node.COMMENT_NODE:
      {
        String data = ((Comment)node).getData();
        if(m_formatterListener instanceof LexicalHandler)
        {
          LexicalHandler lh = ((LexicalHandler)this.m_formatterListener);
          lh.comment(data.toCharArray(), 0, data.length());
        }
      }
      break;
    case Node.DOCUMENT_FRAGMENT_NODE:
      // ??;
      break;
    case Node.DOCUMENT_NODE:
      this.m_formatterListener.startDocument();
      break;
    case Node.ELEMENT_NODE:
      NamedNodeMap atts = ((Element)node).getAttributes();
      this.m_formatterListener.startElement (node.getNodeName(), new AttList(atts));
      break;
    case Node.PROCESSING_INSTRUCTION_NODE:
      {
        ProcessingInstruction pi = (ProcessingInstruction)node;
        String name = pi.getNodeName();
        String data = pi.getData();
        if(name.equals("xslt-next-is-raw") && name.equals("formatter-to-dom"))
        {
          nextIsRaw = true;
        }
        else
        {
          this.m_formatterListener.processingInstruction(pi.getNodeName(), pi.getData());
        }
      }
      break;
    case Node.CDATA_SECTION_NODE:
      {
        String data = ((Text)node).getData();
        boolean isLexH = (m_formatterListener instanceof LexicalHandler);
        LexicalHandler lh = isLexH ? ((LexicalHandler)this.m_formatterListener) : null;
        if(isLexH)
        {
          lh.startCDATA();
        }
        this.m_formatterListener.characters(data.toCharArray(), 0, data.length());
        {
          if(isLexH)
          {
            lh.endCDATA();
          }
        }
      }
      break;
    case Node.TEXT_NODE:
      {
        String data = ((Text)node).getData();
        if(nextIsRaw)
        {
          nextIsRaw = false;
          if(this.m_formatterListener instanceof RawCharacterHandler)
          {
            ((RawCharacterHandler)this.m_formatterListener).charactersRaw(data.toCharArray(), 0, data.length());
          }
          else
          {
            System.out.println("Warning: can't output raw characters!");
            this.m_formatterListener.characters(data.toCharArray(), 0, data.length());
          }
        }
        else
        {
          this.m_formatterListener.characters(data.toCharArray(), 0, data.length());
        }
      }
      break;
    case Node.ENTITY_REFERENCE_NODE:
      {
        EntityReference eref = (EntityReference)node;
        if(m_formatterListener instanceof LexicalHandler)
        {
          ((LexicalHandler)this.m_formatterListener).startEntity(eref.getNodeName());
        }
        else
        {
          // warning("Can not output entity to a pure SAX DocumentHandler");
        }
      }
      break;
    default:
    }
  }

  protected void endNode(Node node)
    throws SAXException 
  {
    switch(node.getNodeType())
    {
    case Node.DOCUMENT_NODE:
      this.m_formatterListener.endDocument();
      break;
    case Node.ELEMENT_NODE:
      this.m_formatterListener.endElement(node.getNodeName());
      break;
    case Node.CDATA_SECTION_NODE:
      break;
    case Node.ENTITY_REFERENCE_NODE:
      {
        EntityReference eref = (EntityReference)node;
        if(m_formatterListener instanceof LexicalHandler)
        {
          LexicalHandler lh = ((LexicalHandler)this.m_formatterListener);
          lh.endEntity(eref.getNodeName());
        }
      }
      break;
    default:
    }
  }
  
}  //TreeWalker
