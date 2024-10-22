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
package org.apache.xalan.xpath;

import org.w3c.dom.*;
import java.io.Serializable;
import org.apache.xalan.xpath.res.XPATHErrorResources;
import org.apache.xalan.xpath.xml.XSLMessages;

/**
 * <meta name="usage" content="general"/>
 * This class represents an XPath object, and is capable of 
 * converting the object to various types, such as a string.
 * This class acts as the base class to other XPath type objects, 
 * such as XString, and provides polymorphic casting capabilities.
 */
public class XObject extends Object implements Serializable
{
  protected Object m_obj; // This may be NULL!!!
  
  /**
   * Create an XObject.
   */
  public XObject()
  {
  }

  /**
   * Create an XObject.
   */
  public XObject(Object obj)
  {
    m_obj = obj;
  }
  
  public static final int CLASS_NULL = -1;
  public static final int CLASS_UNKNOWN = 0;
  public static final int CLASS_BOOLEAN = 1;
  public static final int CLASS_NUMBER = 2;
  public static final int CLASS_STRING = 3;
  public static final int CLASS_NODESET = 4;
  public static final int CLASS_RTREEFRAG = 5;
  
  /**
   * Tell what kind of class this is.
   */
  public int getType()
  {
    return CLASS_UNKNOWN;
  }

  /**
   * Given a request type, return the equivalent string. 
   * For diagnostic purposes.
   */
  private String getTypeString()
  {
    return "#UNKNOWN";
  }
  
  /**
   * Cast result object to a number.
   */
  public double num()
    throws org.xml.sax.SAXException
  {
	  error(XPATHErrorResources.ER_CANT_CONVERT_TO_NUMBER, new Object[] {getTypeString()}); //"Can not convert "+getTypeString()+" to a number");

    return 0.0;
  }

  /**
   * Cast result object to a boolean.
   */
  public boolean bool()
    throws org.xml.sax.SAXException
  {
    error(XPATHErrorResources.ER_CANT_CONVERT_TO_NUMBER, new Object[] {getTypeString()}); //"Can not convert "+getTypeString()+" to a number");

    return false;
  }

  /**
   * Cast result object to a string.
   */
  public String str()
  {
    return m_obj.toString();
  }
  
  public String toString()
  {
    return str();
  }
  
  /**
   * Cast result object to a result tree fragment.
   */
  public DocumentFragment rtree(XPathSupport support)
  {
    DocumentFragment result = rtree();
    if(null == result)
    {
      result = support.getDOMFactory().createDocumentFragment();
      Text textNode = support.getDOMFactory().createTextNode(str());
      result.appendChild(textNode);
    }
    return result;
  }
  
  /**
   * For functions to override.
   */
  public DocumentFragment rtree()
  {
    return null;
  }
  
  /**
   * Return a java object that's closes to the represenation 
   * that should be handed to an extension.
   */
  public Object object()
  {
    return m_obj;
  }

  /**
   * Cast result object to a nodelist.
   */
  public NodeList nodeset()
    throws org.xml.sax.SAXException
  {
    error(XPATHErrorResources.ER_CANT_CONVERT_TO_NODELIST, new Object[] {getTypeString()}); //"Can not convert "+getTypeString()+" to a NodeList!");
    return null;
  }  
  
  /**
   * Cast result object to a nodelist.
   */
  public MutableNodeList mutableNodeset()
    throws org.xml.sax.SAXException
  {
    error(XPATHErrorResources.ER_CANT_CONVERT_TO_MUTABLENODELIST, new Object[] {getTypeString()}); //"Can not convert "+getTypeString()+" to a MutableNodeList!");
    return (MutableNodeList)m_obj;
  }  
 
  /**
   * Cast object to type t.
   */
  public Object castToType(int t, XPathSupport support)
    throws org.xml.sax.SAXException
  {
    Object result;
    switch(t)
    {
    case CLASS_STRING:
      result = str();
      break;
    case CLASS_NUMBER:
      result = new Double(num());
      break;
    case CLASS_NODESET:
      result = nodeset();
      break;
    case CLASS_BOOLEAN:
      result = new Boolean( bool() );
      break;
    case CLASS_UNKNOWN:
      result = m_obj;
      break;
    case CLASS_RTREEFRAG:
      result = rtree(support);
      break;
    default:
      error(XPATHErrorResources.ER_CANT_CONVERT_TO_TYPE, new Object[] {getTypeString(), Integer.toString(t)}); //"Can not convert "+getTypeString()+" to a type#"+t);
      result = null;
    }
    return result;
  }

  /**
   * Tell if one object is less than the other.
   */
  public boolean lessThan(XObject obj2)
    throws org.xml.sax.SAXException
  {
    // In order to handle the 'all' semantics of 
    // nodeset comparisons, we always call the 
    // nodeset function.  Because the arguments 
    // are backwards, we call the opposite comparison
    // function.
    if(obj2.getType() == XObject.CLASS_NODESET)
      return obj2.greaterThan(this);

    return this.num() < obj2.num();
  }

  /**
   * Tell if one object is less than or equal to the other.
   */
  public boolean lessThanOrEqual(XObject obj2)
    throws org.xml.sax.SAXException
  {
    // In order to handle the 'all' semantics of 
    // nodeset comparisons, we always call the 
    // nodeset function.  Because the arguments 
    // are backwards, we call the opposite comparison
    // function.
    if(obj2.getType() == XObject.CLASS_NODESET)
      return obj2.greaterThanOrEqual(this);
    
    return this.num() <= obj2.num();
  }

  /**
   * Tell if one object is less than the other.
   */
  public boolean greaterThan(XObject obj2)
    throws org.xml.sax.SAXException
  {
    // In order to handle the 'all' semantics of 
    // nodeset comparisons, we always call the 
    // nodeset function.  Because the arguments 
    // are backwards, we call the opposite comparison
    // function.
    if(obj2.getType() == XObject.CLASS_NODESET)
      return obj2.lessThan(this);

    return this.num() > obj2.num();
  }

  /**
   * Tell if one object is less than the other.
   */
  public boolean greaterThanOrEqual(XObject obj2)
    throws org.xml.sax.SAXException
  {
    // In order to handle the 'all' semantics of 
    // nodeset comparisons, we always call the 
    // nodeset function.  Because the arguments 
    // are backwards, we call the opposite comparison
    // function.
    if(obj2.getType() == XObject.CLASS_NODESET)
      return obj2.lessThanOrEqual(this);

    return this.num() >= obj2.num();
  }

  /**
   * Tell if two objects are functionally equal.
   */
  public boolean equals(XObject obj2)
    throws org.xml.sax.SAXException
  {
    // In order to handle the 'all' semantics of 
    // nodeset comparisons, we always call the 
    // nodeset function.
    if(obj2.getType() == XObject.CLASS_NODESET)
      return obj2.equals(this);

    return m_obj.equals(obj2.m_obj);
  }
  
  /**
   * Tell if two objects are functionally not equal.
   */
  public boolean notEquals(XObject obj2)
    throws org.xml.sax.SAXException
  {
    // In order to handle the 'all' semantics of 
    // nodeset comparisons, we always call the 
    // nodeset function.
    if(obj2.getType() == XObject.CLASS_NODESET)
      return obj2.notEquals(this);

    return !equals(obj2);
  }

  /**
   * Tell the user of an error, and probably throw an 
   * exception.
   */
  protected void error(int msg)
    throws org.xml.sax.SAXException
  {
	  error (msg, null);
  }	   

  /**
   * Tell the user of an error, and probably throw an 
   * exception.
   */
  protected void error(int msg, Object[] args)
    throws org.xml.sax.SAXException
  {
    String fmsg = XSLMessages.createXPATHMessage(msg, args);  
    // boolean shouldThrow = support.problem(m_support.XPATHPROCESSOR, 
    //                                      m_support.ERROR,
    //                                      null, 
    //                                      null, fmsg, 0, 0);
    // if(shouldThrow)
    {
      throw new XPathException(fmsg);
    }
  }

}
