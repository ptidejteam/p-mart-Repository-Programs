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
import java.text.*;
import org.apache.xalan.xpath.xml.XMLParserLiaisonDefault;

/**
 * <meta name="usage" content="general"/>
 * This class represents an XPath nodeset object, and is capable of 
 * converting the nodeset to other types, such as a string.
 */
public class XNodeSet extends XObject
{  
  /**
   * Construct a XNodeSet object.
   */
  public XNodeSet(NodeList val)
  {
    super(val);
  }
  
  /**
   * Construct an empty XNodeSet object.
   */
  public XNodeSet()
  {
    super(new MutableNodeListImpl());
  }

  /**
   * Construct a XNodeSet object for one node.
   */
  public XNodeSet(Node n)
  {
    super(new MutableNodeListImpl());
    if(null != n)
    {
      ((MutableNodeList)m_obj).addNode(n);
    }
  }
 
 
  /**
   * Tell that this is a CLASS_NODESET.
   */
  public int getType()
  {
    return CLASS_NODESET;
  }
  
  /**
   * Given a request type, return the equivalent string. 
   * For diagnostic purposes.
   */
  private String getTypeString()
  {
    return "#NODESET";
  }
  
  /**
   * Get the string conversion from a single node.
   */
  double getNumberFromNode(Node n)
  {
    return XString.castToNum(getStringFromNode(n));
  }

  /**
   * Cast result object to a number.
   */
  public double num()
  {
    NodeList nl = nodeset();
    return (nl.getLength() > 0) ? getNumberFromNode(nl.item(0)) : Double.NaN;
  }

  /**
   * Cast result object to a boolean.
   */
  public boolean bool()
  {
    return nodeset().getLength() > 0;
  }
  

  /**
   * Get the string conversion from a single node.
   */
  static String getStringFromNode(Node n)
  {
    switch(n.getNodeType())
    {
    case Node.ELEMENT_NODE:
    case Node.DOCUMENT_NODE:
      return XMLParserLiaisonDefault.getNodeData(n);
    case Node.CDATA_SECTION_NODE:
    case Node.TEXT_NODE:
      return ((Text)n).getData();
    case Node.COMMENT_NODE:
    case Node.PROCESSING_INSTRUCTION_NODE:
    case Node.ATTRIBUTE_NODE:
      return n.getNodeValue();
    default:
      return XMLParserLiaisonDefault.getNodeData(n);
    }
  }

  /**
   * Cast result object to a string.
   */
  public String str()
  {
    NodeList nl = nodeset();
    return (nl.getLength() > 0) ? getStringFromNode(nl.item(0)) : "";
  }
  
  /**
   * Cast result object to a result tree fragment.
   */
  public DocumentFragment rtree(XPathSupport support)
  {
    DocumentFragment frag = support.getDOMFactory().createDocumentFragment();
    NodeList nl = nodeset();
    int nNodes = nl.getLength();
    for(int i = 0; i < nNodes; i++)
    {
      frag.appendChild(nl.item(i).cloneNode(true));
    }
    return frag;
  }

  /**
   * Cast result object to a nodelist.
   */
  public NodeList nodeset()
  {
    return (NodeList)m_obj;
  }  

  /**
   * Cast result object to a nodelist.
   */
  public MutableNodeList mutableNodeset()
  {
    MutableNodeList mnl;
    if (m_obj instanceof MutableNodeList)
    {
      mnl = (MutableNodeList)m_obj;
    }
    else
    {
      mnl = new MutableNodeListImpl(nodeset());
      m_obj = mnl;
    }
    
    return mnl;
  }  
  
  /**
   * Tell if one object is less than the other.
   */
  public boolean lessThan(XObject obj2)
    throws org.xml.sax.SAXException
  {
    boolean isLT = false;
    int type = obj2.getType();
    if(XObject.CLASS_NODESET == type)
    {
      // From http://www.w3.org/TR/xpath: 
      // If both objects to be compared are node-sets, then the comparison 
      // will be true if and only if there is a node in the first node-set 
      // and a node in the second node-set such that the result of performing 
      // the comparison on the string-values of the two nodes is true.

      // Note this little gem from the draft:
      // NOTE: If $x is bound to a node-set, then $x="foo" 
      // does not mean the same as not($x!="foo"): the former 
      // is true if and only if some node in $x has the string-value 
      // foo; the latter is true if and only if all nodes in $x have 
      // the string-value foo.

      NodeList list1 = nodeset();
      NodeList list2 = ((XNodeSet)obj2).nodeset();
      int len1 = list1.getLength();
      int len2 = list2.getLength();
      for(int i = 0; i < len1; i++)
      {
        String s1 = getStringFromNode(list1.item(i));
        for(int k = 0; k < len2; k++)
        {
          String s2 = getStringFromNode(list2.item(k));
          if(s1.compareTo(s2) < 0)
          {
            isLT = true;
            break;
          }
        }
      }
    }
    else if(XObject.CLASS_BOOLEAN == type)
    {
      // From http://www.w3.org/TR/xpath: 
      // If one object to be compared is a node-set and the other is a boolean, 
      // then the comparison will be true if and only if the result of 
      // performing the comparison on the boolean and on the result of 
      // converting the node-set to a boolean using the boolean function 
      // is true.
      double num1 = bool() ? 1.0 : 0.0;
      double num2 = obj2.num();
      isLT = (num1 < num2);
    }
    else if(XObject.CLASS_NUMBER == type)
    {
      // From http://www.w3.org/TR/xpath: 
      // If one object to be compared is a node-set and the other is a number, 
      // then the comparison will be true if and only if there is a 
      // node in the node-set such that the result of performing the 
      // comparison on the number to be compared and on the result of 
      // converting the string-value of that node to a number using 
      // the number function is true. 
            
      NodeList list1 = nodeset();
      int len1 = list1.getLength();
      double num2 = obj2.num();
      for(int i = 0; i < len1; i++)
      {
        double num1 = getNumberFromNode(list1.item(i));
        if(num1 < num2)
        {
          isLT = true;
          break;
        }
      }
    }
    else if(XObject.CLASS_RTREEFRAG == type)
    {
      // hmmm... 
      double num2 = obj2.num();
      if(num2 != Double.NaN)
      {
        NodeList list1 = nodeset();
        int len1 = list1.getLength();
        for(int i = 0; i < len1; i++)
        {
          double num1 = getNumberFromNode(list1.item(i));
          if(num1 < num2)
          {
            isLT = true;
            break;
          }
        }
      }
      else
      {
        NodeList list1 = nodeset();
        int len1 = list1.getLength();
        String s2 = obj2.str();
        for(int i = 0; i < len1; i++)
        {
          String s1 = getStringFromNode(list1.item(i));
          if(s1.compareTo(s2) < 0)
          {
            isLT = true;
            break;
          }
        }
      }
    }
    else if(XObject.CLASS_STRING == type)
    {
      // From http://www.w3.org/TR/xpath: 
      // If one object to be compared is a node-set and the other is a 
      // string, then the comparison will be true if and only if there 
      // is a node in the node-set such that the result of performing 
      // the comparison on the string-value of the node and the other 
      // string is true. 
      NodeList list1 = nodeset();
      int len1 = list1.getLength();
      String s2 = obj2.str();
      for(int i = 0; i < len1; i++)
      {
        String s1 = getStringFromNode(list1.item(i));
        if(s1.compareTo(s2) < 0)
        {
          isLT = true;
          break;
        }
      }
    }
    else
    {
      isLT = this.num() < obj2.num();
    }
    return isLT;
  }

  /**
   * Tell if one object is less than or equal to the other.
   */
  public boolean lessThanOrEqual(XObject obj2)
    throws org.xml.sax.SAXException
  {
    boolean isLTE = false;
    int type = obj2.getType();
    if(XObject.CLASS_NODESET == type)
    {
      // From http://www.w3.org/TR/xpath: 
      // If both objects to be compared are node-sets, then the comparison 
      // will be true if and only if there is a node in the first node-set 
      // and a node in the second node-set such that the result of performing 
      // the comparison on the string-values of the two nodes is true.

      // Note this little gem from the draft:
      // NOTE: If $x is bound to a node-set, then $x="foo" 
      // does not mean the same as not($x!="foo"): the former 
      // is true if and only if some node in $x has the string-value 
      // foo; the latter is true if and only if all nodes in $x have 
      // the string-value foo.

      NodeList list1 = nodeset();
      NodeList list2 = ((XNodeSet)obj2).nodeset();
      int len1 = list1.getLength();
      int len2 = list2.getLength();
      for(int i = 0; i < len1; i++)
      {
        String s1 = getStringFromNode(list1.item(i));
        for(int k = 0; k < len2; k++)
        {
          String s2 = getStringFromNode(list2.item(k));
          if(s1.compareTo(s2) <= 0)
          {
            isLTE = true;
            break;
          }
        }
      }
    }
    else if(XObject.CLASS_BOOLEAN == type)
    {
      // From http://www.w3.org/TR/xpath: 
      // If one object to be compared is a node-set and the other is a boolean, 
      // then the comparison will be true if and only if the result of 
      // performing the comparison on the boolean and on the result of 
      // converting the node-set to a boolean using the boolean function 
      // is true.
      double num1 = bool() ? 1.0 : 0.0;
      double num2 = obj2.num();
      isLTE = (num1 <= num2);
    }
    else if(XObject.CLASS_NUMBER == type)
    {
      // From http://www.w3.org/TR/xpath: 
      // If one object to be compared is a node-set and the other is a number, 
      // then the comparison will be true if and only if there is a 
      // node in the node-set such that the result of performing the 
      // comparison on the number to be compared and on the result of 
      // converting the string-value of that node to a number using 
      // the number function is true. 
            
      NodeList list1 = nodeset();
      int len1 = list1.getLength();
      double num2 = obj2.num();
      for(int i = 0; i < len1; i++)
      {
        double num1 = getNumberFromNode(list1.item(i));
        if(num1 <= num2)
        {
          isLTE = true;
          break;
        }
      }
    }
    else if(XObject.CLASS_RTREEFRAG == type)
    {
      // hmmm... 
      double num2 = obj2.num();
      if(num2 != Double.NaN)
      {
        NodeList list1 = nodeset();
        int len1 = list1.getLength();
        for(int i = 0; i < len1; i++)
        {
          double num1 = getNumberFromNode(list1.item(i));
          if(num1 <= num2)
          {
            isLTE = true;
            break;
          }
        }
      }
      else
      {
        NodeList list1 = nodeset();
        int len1 = list1.getLength();
        String s2 = obj2.str();
        for(int i = 0; i < len1; i++)
        {
          String s1 = getStringFromNode(list1.item(i));
          if(s1.compareTo(s2) <= 0)
          {
            isLTE = true;
            break;
          }
        }
      }
    }
    else if(XObject.CLASS_STRING == type)
    {
      // From http://www.w3.org/TR/xpath: 
      // If one object to be compared is a node-set and the other is a 
      // string, then the comparison will be true if and only if there 
      // is a node in the node-set such that the result of performing 
      // the comparison on the string-value of the node and the other 
      // string is true. 
      NodeList list1 = nodeset();
      int len1 = list1.getLength();
      String s2 = obj2.str();
      for(int i = 0; i < len1; i++)
      {
        String s1 = getStringFromNode(list1.item(i));
        if(s1.compareTo(s2) <= 0)
        {
          isLTE = true;
          break;
        }
      }
    }
    else
    {
      isLTE = this.num() <= obj2.num();
    }
    return isLTE;
  }

  /**
   * Tell if one object is less than the other.
   */
  public boolean greaterThan(XObject obj2)
    throws org.xml.sax.SAXException
  {
    boolean isGT = false;
    int type = obj2.getType();
    if(XObject.CLASS_NODESET == type)
    {
      // From http://www.w3.org/TR/xpath: 
      // If both objects to be compared are node-sets, then the comparison 
      // will be true if and only if there is a node in the first node-set 
      // and a node in the second node-set such that the result of performing 
      // the comparison on the string-values of the two nodes is true.

      // Note this little gem from the draft:
      // NOTE: If $x is bound to a node-set, then $x="foo" 
      // does not mean the same as not($x!="foo"): the former 
      // is true if and only if some node in $x has the string-value 
      // foo; the latter is true if and only if all nodes in $x have 
      // the string-value foo.

      NodeList list1 = nodeset();
      NodeList list2 = ((XNodeSet)obj2).nodeset();
      int len1 = list1.getLength();
      int len2 = list2.getLength();
      for(int i = 0; i < len1; i++)
      {
        String s1 = getStringFromNode(list1.item(i));
        for(int k = 0; k < len2; k++)
        {
          String s2 = getStringFromNode(list2.item(k));
          if(s1.compareTo(s2) > 0)
          {
            isGT = true;
            break;
          }
        }
      }
    }
    else if(XObject.CLASS_BOOLEAN == type)
    {
      // From http://www.w3.org/TR/xpath: 
      // If one object to be compared is a node-set and the other is a boolean, 
      // then the comparison will be true if and only if the result of 
      // performing the comparison on the boolean and on the result of 
      // converting the node-set to a boolean using the boolean function 
      // is true.
      double num1 = bool() ? 1.0 : 0.0;
      double num2 = obj2.num();
      isGT = (num1 > num2);
    }
    else if(XObject.CLASS_NUMBER == type)
    {
      // From http://www.w3.org/TR/xpath: 
      // If one object to be compared is a node-set and the other is a number, 
      // then the comparison will be true if and only if there is a 
      // node in the node-set such that the result of performing the 
      // comparison on the number to be compared and on the result of 
      // converting the string-value of that node to a number using 
      // the number function is true. 
            
      NodeList list1 = nodeset();
      int len1 = list1.getLength();
      double num2 = obj2.num();
      for(int i = 0; i < len1; i++)
      {
        double num1 = getNumberFromNode(list1.item(i));
        if(num1 > num2)
        {
          isGT = true;
          break;
        }
      }
    }
    else if(XObject.CLASS_RTREEFRAG == type)
    {
      // hmmm... 
      double num2 = obj2.num();
      if(num2 != Double.NaN)
      {
        NodeList list1 = nodeset();
        int len1 = list1.getLength();
        for(int i = 0; i < len1; i++)
        {
          double num1 = getNumberFromNode(list1.item(i));
          if(num1 > num2)
          {
            isGT = true;
            break;
          }
        }
      }
      else
      {
        NodeList list1 = nodeset();
        int len1 = list1.getLength();
        String s2 = obj2.str();
        for(int i = 0; i < len1; i++)
        {
          String s1 = getStringFromNode(list1.item(i));
          if(s1.compareTo(s2) > 0)
          {
            isGT = true;
            break;
          }
        }
      }
    }
    else if(XObject.CLASS_STRING == type)
    {
      // From http://www.w3.org/TR/xpath: 
      // If one object to be compared is a node-set and the other is a 
      // string, then the comparison will be true if and only if there 
      // is a node in the node-set such that the result of performing 
      // the comparison on the string-value of the node and the other 
      // string is true. 
      NodeList list1 = nodeset();
      int len1 = list1.getLength();
      String s2 = obj2.str();
      for(int i = 0; i < len1; i++)
      {
        String s1 = getStringFromNode(list1.item(i));
        if(s1.compareTo(s2) > 0)
        {
          isGT = true;
          break;
        }
      }
    }
    else
    {
      isGT = this.num() > obj2.num();
    }
    return isGT;
  }

  /**
   * Tell if one object is less than the other.
   */
  public boolean greaterThanOrEqual(XObject obj2)
    throws org.xml.sax.SAXException
  {
    boolean isGTE = false;
    int type = obj2.getType();
    if(XObject.CLASS_NODESET == type)
    {
      // From http://www.w3.org/TR/xpath: 
      // If both objects to be compared are node-sets, then the comparison 
      // will be true if and only if there is a node in the first node-set 
      // and a node in the second node-set such that the result of performing 
      // the comparison on the string-values of the two nodes is true.

      // Note this little gem from the draft:
      // NOTE: If $x is bound to a node-set, then $x="foo" 
      // does not mean the same as not($x!="foo"): the former 
      // is true if and only if some node in $x has the string-value 
      // foo; the latter is true if and only if all nodes in $x have 
      // the string-value foo.

      NodeList list1 = nodeset();
      NodeList list2 = ((XNodeSet)obj2).nodeset();
      int len1 = list1.getLength();
      int len2 = list2.getLength();
      for(int i = 0; i < len1; i++)
      {
        String s1 = getStringFromNode(list1.item(i));
        for(int k = 0; k < len2; k++)
        {
          String s2 = getStringFromNode(list2.item(k));
          if(s1.compareTo(s2) >= 0)
          {
            isGTE = true;
            break;
          }
        }
      }
    }
    else if(XObject.CLASS_BOOLEAN == type)
    {
      // From http://www.w3.org/TR/xpath: 
      // If one object to be compared is a node-set and the other is a boolean, 
      // then the comparison will be true if and only if the result of 
      // performing the comparison on the boolean and on the result of 
      // converting the node-set to a boolean using the boolean function 
      // is true.
      double num1 = bool() ? 1.0 : 0.0;
      double num2 = obj2.num();
      isGTE = (num1 >= num2);
    }
    else if(XObject.CLASS_NUMBER == type)
    {
      // From http://www.w3.org/TR/xpath: 
      // If one object to be compared is a node-set and the other is a number, 
      // then the comparison will be true if and only if there is a 
      // node in the node-set such that the result of performing the 
      // comparison on the number to be compared and on the result of 
      // converting the string-value of that node to a number using 
      // the number function is true. 
            
      NodeList list1 = nodeset();
      int len1 = list1.getLength();
      double num2 = obj2.num();
      for(int i = 0; i < len1; i++)
      {
        double num1 = getNumberFromNode(list1.item(i));
        if(num1 >= num2)
        {
          isGTE = true;
          break;
        }
      }
    }
    else if(XObject.CLASS_RTREEFRAG == type)
    {
      // hmmm... 
      double num2 = obj2.num();
      if(num2 != Double.NaN)
      {
        NodeList list1 = nodeset();
        int len1 = list1.getLength();
        for(int i = 0; i < len1; i++)
        {
          double num1 = getNumberFromNode(list1.item(i));
          if(num1 >= num2)
          {
            isGTE = true;
            break;
          }
        }
      }
      else
      {
        NodeList list1 = nodeset();
        int len1 = list1.getLength();
        String s2 = obj2.str();
        for(int i = 0; i < len1; i++)
        {
          String s1 = getStringFromNode(list1.item(i));
          if(s1.compareTo(s2) >= 0)
          {
            isGTE = true;
            break;
          }
        }
      }
    }
    else if(XObject.CLASS_STRING == type)
    {
      // From http://www.w3.org/TR/xpath: 
      // If one object to be compared is a node-set and the other is a 
      // string, then the comparison will be true if and only if there 
      // is a node in the node-set such that the result of performing 
      // the comparison on the string-value of the node and the other 
      // string is true. 
      NodeList list1 = nodeset();
      int len1 = list1.getLength();
      String s2 = obj2.str();
      for(int i = 0; i < len1; i++)
      {
        String s1 = getStringFromNode(list1.item(i));
        if(s1.compareTo(s2) >= 0)
        {
          isGTE = true;
          break;
        }
      }
    }
    else
    {
      isGTE = this.num() >= obj2.num();
    }
    return isGTE;
  }

  /**
   * Tell if two objects are functionally equal.
   */
  public boolean equals(XObject obj2)
    throws org.xml.sax.SAXException
  {
    boolean isEqual = false;
    int type = obj2.getType();
    if(XObject.CLASS_NODESET == type)
    {
      // From http://www.w3.org/TR/xpath: 
      // If both objects to be compared are node-sets, then the comparison 
      // will be true if and only if there is a node in the first node-set 
      // and a node in the second node-set such that the result of performing 
      // the comparison on the string-values of the two nodes is true.

      // Note this little gem from the draft:
      // NOTE: If $x is bound to a node-set, then $x="foo" 
      // does not mean the same as not($x!="foo"): the former 
      // is true if and only if some node in $x has the string-value 
      // foo; the latter is true if and only if all nodes in $x have 
      // the string-value foo.

      NodeList list1 = nodeset();
      NodeList list2 = ((XNodeSet)obj2).nodeset();
      int len1 = list1.getLength();
      int len2 = list2.getLength();
      for(int i = 0; i < len1; i++)
      {
        String s1 = getStringFromNode(list1.item(i));
        for(int k = 0; k < len2; k++)
        {
          String s2 = getStringFromNode(list2.item(k));
          if(s2.equals(s1))
          {
            isEqual = true;
            break;
          }
        }
      }
    }
    else if(XObject.CLASS_BOOLEAN == type)
    {
      // From http://www.w3.org/TR/xpath: 
      // If one object to be compared is a node-set and the other is a boolean, 
      // then the comparison will be true if and only if the result of 
      // performing the comparison on the boolean and on the result of 
      // converting the node-set to a boolean using the boolean function 
      // is true.
      isEqual = (bool() == obj2.bool());
    }
    else if(XObject.CLASS_NUMBER == type)
    {
      // From http://www.w3.org/TR/xpath: 
      // If one object to be compared is a node-set and the other is a number, 
      // then the comparison will be true if and only if there is a 
      // node in the node-set such that the result of performing the 
      // comparison on the number to be compared and on the result of 
      // converting the string-value of that node to a number using 
      // the number function is true. 
            
      NodeList list1 = nodeset();
      int len1 = list1.getLength();
      double num2 = obj2.num();
      for(int i = 0; i < len1; i++)
      {
        double num1 = getNumberFromNode(list1.item(i));
        if(num1 == num2)
        {
          isEqual = true;
          break;
        }
      }
    }
    else if(XObject.CLASS_RTREEFRAG == type)
    {
      NodeList list1 = nodeset();
      int len1 = list1.getLength();
      String s2 = obj2.str();
      for(int i = 0; i < len1; i++)
      {
        String s1 = getStringFromNode(list1.item(i));
        if(s1.equals(s2))
        {
          isEqual = true;
          break;
        }
      }
    }
    else if(XObject.CLASS_STRING == type)
    {
      // From http://www.w3.org/TR/xpath: 
      // If one object to be compared is a node-set and the other is a 
      // string, then the comparison will be true if and only if there 
      // is a node in the node-set such that the result of performing 
      // the comparison on the string-value of the node and the other 
      // string is true. 
      NodeList list1 = nodeset();
      int len1 = list1.getLength();
      String s2 = obj2.str();
      for(int i = 0; i < len1; i++)
      {
        String s1 = getStringFromNode(list1.item(i));
        if(s1.equals(s2))
        {
          isEqual = true;
          break;
        }
      }
    }
    else
    {
      isEqual = super.equals(obj2);
    }
    return isEqual;
  }
  
  /**
   * Tell if two objects are functionally not equal.
   */
  public boolean notEquals(XObject obj2)
    throws org.xml.sax.SAXException
  {
    boolean notEqual = false;
    int type = obj2.getType();
    if(XObject.CLASS_NODESET == type)
    {
      // From http://www.w3.org/TR/xpath: 
      // If both objects to be compared are node-sets, then the comparison 
      // will be true if and only if there is a node in the first node-set 
      // and a node in the second node-set such that the result of performing 
      // the comparison on the string-values of the two nodes is true.
      
      // Note this little gem from the draft:
      // NOTE: If $x is bound to a node-set, then $x="foo" 
      // does not mean the same as not($x!="foo"): the former 
      // is true if and only if some node in $x has the string-value 
      // foo; the latter is true if and only if all nodes in $x have 
      // the string-value foo.

      NodeList list1 = nodeset();
      NodeList list2 = ((XNodeSet)obj2).nodeset();
      int len1 = list1.getLength();
      int len2 = list2.getLength();
      for(int i = 0; i < len1; i++)
      {
        String s1 = getStringFromNode(list1.item(i));
        for(int k = 0; k < len2; k++)
        {
          String s2 = getStringFromNode(list2.item(k));
          if(!s2.equals(s1))
          {
            notEqual = true;
            break;
          }
        }
      }
    }
    else if(XObject.CLASS_BOOLEAN == type)
    {
      // From http://www.w3.org/TR/xpath: 
      // If one object to be compared is a node-set and the other is a boolean, 
      // then the comparison will be true if and only if the result of 
      // performing the comparison on the boolean and on the result of 
      // converting the node-set to a boolean using the boolean function 
      // is true.
      notEqual = (bool() != obj2.bool());
    }
    else if(XObject.CLASS_NUMBER == type)
    {
      // From http://www.w3.org/TR/xpath: 
      // If one object to be compared is a node-set and the other is a number, 
      // then the comparison will be true if and only if there is a 
      // node in the node-set such that the result of performing the 
      // comparison on the number to be compared and on the result of 
      // converting the string-value of that node to a number using 
      // the number function is true. 
            
      NodeList list1 = nodeset();
      int len1 = list1.getLength();
      double num2 = obj2.num();
      for(int i = 0; i < len1; i++)
      {
        double num1 = getNumberFromNode(list1.item(i));
        if(num1 != num2)
        {
          notEqual = true;
          break;
        }
      }
    }
    else if(XObject.CLASS_RTREEFRAG == type)
    {
      NodeList list1 = nodeset();
      int len1 = list1.getLength();
      String s2 = obj2.str();
      for(int i = 0; i < len1; i++)
      {
        String s1 = getStringFromNode(list1.item(i));
        if(!s1.equals(s2))
        {
          notEqual = true;
          break;
        }
      }
    }
    else if(XObject.CLASS_STRING == type)
    {
      // From http://www.w3.org/TR/xpath: 
      // If one object to be compared is a node-set and the other is a 
      // string, then the comparison will be true if and only if there 
      // is a node in the node-set such that the result of performing 
      // the comparison on the string-value of the node and the other 
      // string is true. 
      NodeList list1 = nodeset();
      int len1 = list1.getLength();
      String s2 = obj2.str();
      for(int i = 0; i < len1; i++)
      {
        String s1 = getStringFromNode(list1.item(i));
        if(!s1.equals(s2))
        {
          notEqual = true;
          break;
        }
      }
    }
    else
    {
      notEqual = super.notEquals(obj2);
    }
    return notEqual;
  }

}
