/*
 * Copyright 1999-2004 The Apache Software Foundation.
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
/*
 * $Id: IteratorPool.java,v 1.1 2006/03/09 00:07:16 vauchers Exp $
 */
package org.apache.xpath.axes;

import java.util.Vector;

import org.apache.xml.dtm.DTMIterator;
import org.apache.xml.utils.WrappedRuntimeException;

/**
 * Pool of object of a given type to pick from to help memory usage
 * @xsl.usage internal
 */
public class IteratorPool implements java.io.Serializable
{

  /** Type of objects in this pool.
   *  @serial          */
  private final DTMIterator m_orig;

  /** Vector of given objects this points to.
   *  @serial          */
  private final Vector m_freeStack;

  /**
   * Constructor IteratorPool
   *
   * @param original The original iterator from which all others will be cloned.
   */
  public IteratorPool(DTMIterator original)
  {
    m_orig = original;
    m_freeStack = new Vector();
  }
  
  /**
   * Get an instance of the given object in this pool 
   *
   * @return An instance of the given object
   */
  public synchronized DTMIterator getInstanceOrThrow()
    throws CloneNotSupportedException
  {
    // Check if the pool is empty.
    if (m_freeStack.isEmpty())
    {

      // Create a new object if so.
      return (DTMIterator)m_orig.clone();
    }
    else
    {
      // Remove object from end of free pool.
      DTMIterator result = (DTMIterator)m_freeStack.lastElement();

      m_freeStack.setSize(m_freeStack.size() - 1);

      return result;
    }
  }
  
  /**
   * Get an instance of the given object in this pool 
   *
   * @return An instance of the given object
   */
  public synchronized DTMIterator getInstance()
  {
    // Check if the pool is empty.
    if (m_freeStack.isEmpty())
    {

      // Create a new object if so.
      try
      {
        return (DTMIterator)m_orig.clone();
      }
      catch (Exception ex)
      {
        throw new WrappedRuntimeException(ex);
      }
    }
    else
    {
      // Remove object from end of free pool.
      DTMIterator result = (DTMIterator)m_freeStack.lastElement();

      m_freeStack.setSize(m_freeStack.size() - 1);

      return result;
    }
  }

  /**
   * Add an instance of the given object to the pool  
   *
   *
   * @param obj Object to add.
   */
  public synchronized void freeInstance(DTMIterator obj)
  {
    m_freeStack.addElement(obj);
  }
}
