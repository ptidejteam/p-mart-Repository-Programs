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
 *     the documentation and/or other materials provided with the
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

import org.apache.xalan.xpath.xml.StringVector;
import org.xml.sax.AttributeList;
import java.io.Serializable;

/**
 * <meta name="usage" content="advanced"/>
 * Implement the SAX AttributeList interface, using a single array.
 */
public class MutableAttrListImpl extends StringVector implements AttributeList, Serializable
{
  static final int OFFSET_NAME = 0;
  static final int OFFSET_TYPE = 1;
  static final int OFFSET_VALUE = 2;
  static final int SLOTS_PER_ATTR = 3;
  
  /**
   * Create an empty attribute list.
   *
   * <p>This constructor is most useful for parser writers, who
   * will use it to create a single, reusable attribute list that
   * can be reset with the clear method between elements.</p>
   *
   * @see #addAttribute
   * @see #clear
   */
  public MutableAttrListImpl ()
  {
  }
  
  
  /**
   * Construct a persistent copy of an existing attribute list.
   *
   * <p>This constructor is most useful for application writers,
   * who will use it to create a persistent copy of an existing
   * attribute list.</p>
   *
   * @param atts The attribute list to copy
   * @see org.xml.sax.DocumentHandler#startElement
   */
  public MutableAttrListImpl (AttributeList atts)
  {
    setAttributeList(atts);
  }
  
  
  
  ////////////////////////////////////////////////////////////////////
  // Methods specific to this class.
  ////////////////////////////////////////////////////////////////////
  
  
  /**
   * Set the attribute list, discarding previous contents.
   *
   * <p>This method allows an application writer to reuse an
   * attribute list easily.</p>
   *
   * @param atts The attribute list to copy.
   */
  public void setAttributeList (AttributeList atts)
  {
    int count = atts.getLength();
    
    clear();
    
    for (int i = 0; i < count; i++) 
    {
      addAttribute(atts.getName(i), atts.getType(i), atts.getValue(i));
    }
  }
  
  
  /**
   * Add an attribute to an attribute list.
   *
   * <p>This method is provided for SAX parser writers, to allow them
   * to build up an attribute list incrementally before delivering
   * it to the application.</p>
   *
   * @param name The attribute name.
   * @param type The attribute type ("NMTOKEN" for an enumeration).
   * @param value The attribute value (must not be null).
   * @see #removeAttribute
   * @see org.xml.sax.DocumentHandler#startElement
   */
  public void addAttribute (String name, String type, String value)
  {
    // removeAttribute (name); // I think this is correct, but I don't think I need it
    int offset = m_firstFree;
    
    // first see if the attribute was already in the list. Increment by SLOTS_PER_ATTR
    // because names would only be in those slots. 
    for(int i = 0; i < m_firstFree; i+=SLOTS_PER_ATTR)
    {
      if(m_map[i+OFFSET_NAME].equals(name))
      {
        offset = i;
        break;
      }  
    }
    
    // If the attribute was not in the list, see if we need a bigger list 
    if (offset == m_firstFree)
    {      
      if((m_firstFree+SLOTS_PER_ATTR) >= m_mapSize)
      {
        m_mapSize+=m_blocksize;
        String newMap[] = new String[m_mapSize];
        System.arraycopy(m_map, 0, newMap, 0, m_firstFree+1);
        m_map = newMap;
      }
      m_firstFree+=SLOTS_PER_ATTR;
    }  
    // add or reset the attribute
    m_map[offset+OFFSET_NAME] = name;
    m_map[offset+OFFSET_TYPE] = type;
    m_map[offset+OFFSET_VALUE] = value;
    
  }
  
  
  /**
   * Remove an attribute from the list.
   *
   * <p>SAX application writers can use this method to filter an
   * attribute out of an AttributeList.  Note that invoking this
   * method will change the length of the attribute list and
   * some of the attribute's indices.</p>
   *
   * <p>If the requested attribute is not in the list, this is
   * a no-op.</p>
   *
   * @param name The attribute name.
   * @see #addAttribute
   */
  public void removeAttribute (String name)
  {
    for(int i = 0; i < m_firstFree; i+=SLOTS_PER_ATTR)
    {
      if(m_map[i+OFFSET_NAME].equals(name))
      {
        m_firstFree-=SLOTS_PER_ATTR;
        if(i < m_firstFree)
        {
          System.arraycopy(m_map, i+SLOTS_PER_ATTR, m_map, i, m_firstFree-i);
          m_map[m_firstFree+OFFSET_NAME] = null;
          m_map[m_firstFree+OFFSET_TYPE] = null;
          m_map[m_firstFree+OFFSET_VALUE] = null;
        }
        else
        {
          m_map[i+OFFSET_NAME] = null;
          m_map[i+OFFSET_TYPE] = null;
          m_map[i+OFFSET_VALUE] = null;
        }        
        return;
      }
    }
  }
  
  
  /**
   * Clear the attribute list.
   *
   * <p>SAX parser writers can use this method to reset the attribute
   * list between DocumentHandler.startElement events.  Normally,
   * it will make sense to reuse the same MutableAttrListImpl object
   * rather than allocating a new one each time.</p>
   *
   * @see org.xml.sax.DocumentHandler#startElement
   */
  public void clear ()
  {
    for(int i = 0; i < m_firstFree; i++)
    {
      m_map[i] = null;
    }
    m_firstFree = 0; 
  }
  
  ////////////////////////////////////////////////////////////////////
  // Implementation of org.xml.sax.AttributeList
  ////////////////////////////////////////////////////////////////////
  
  
  /**
   * Return the number of attributes in the list.
   *
   * @return The number of attributes in the list.
   * @see org.xml.sax.AttributeList#getLength
   */
  public int getLength ()
  {
    return (m_firstFree > 0) ? (m_firstFree/SLOTS_PER_ATTR) : 0;
  }
  
  
  /**
   * Get the name of an attribute (by position).
   *
   * @param i The position of the attribute in the list.
   * @return The attribute name as a string, or null if there
   *         is no attribute at that position.
   * @see org.xml.sax.AttributeList#getName(int)
   */
  public String getName (int i)
  {
    if (i < 0) 
    {
      return null;
    }
    try 
    {
      return m_map[(i*SLOTS_PER_ATTR)+OFFSET_NAME];
    }
    catch (ArrayIndexOutOfBoundsException e) 
    {
      return null;
    }
  }
  
  
  /**
   * Get the type of an attribute (by position).
   *
   * @param i The position of the attribute in the list.
   * @return The attribute type as a string ("NMTOKEN" for an
   *         enumeration, and "CDATA" if no declaration was
   *         read), or null if there is no attribute at
   *         that position.
   * @see org.xml.sax.AttributeList#getType(int)
   */
  public String getType (int i)
  {
    if (i < 0) 
    {
      return null;
    }
    try 
    {
      return m_map[(i*SLOTS_PER_ATTR)+OFFSET_TYPE];
    }
    catch (ArrayIndexOutOfBoundsException e) 
    {
      return null;
    }
  }
  
  
  /**
   * Get the value of an attribute (by position).
   *
   * @param i The position of the attribute in the list.
   * @return The attribute value as a string, or null if
   *         there is no attribute at that position.
   * @see org.xml.sax.AttributeList#getValue(int)
   */
  public String getValue (int i)
  {
    if (i < 0) 
    {
      return null;
    }
    try 
    {
      return m_map[(i*SLOTS_PER_ATTR)+OFFSET_VALUE];
    }
    catch (ArrayIndexOutOfBoundsException e) 
    {
      return null;
    }
  }
  
  
  /**
   * Get the type of an attribute (by name).
   *
   * @param name The attribute name.
   * @return The attribute type as a string ("NMTOKEN" for an
   *         enumeration, and "CDATA" if no declaration was
   *         read).
   * @see org.xml.sax.AttributeList#getType(java.lang.String)
   */
  public String getType (String name)
  {
    for(int i = 0; i <= m_firstFree; i++)
    {
      if(m_map[i+OFFSET_NAME].equals(name))
      {
        return m_map[i+OFFSET_TYPE];
      }
    }
    return "CDATA";
  }
  
  
  /**
   * Get the value of an attribute (by name).
   *
   * @param name The attribute name.
   * @see org.xml.sax.AttributeList#getValue(java.lang.String)
   */
  public String getValue (String name)
  {
    for(int i = 0; i < m_firstFree; i+=SLOTS_PER_ATTR)
    {
      if(m_map[i+OFFSET_NAME].equals(name))
      {
        return m_map[i+OFFSET_VALUE];
      }
    }
    return null;
  }
  
}

// end of MutableAttrListImpl.java
