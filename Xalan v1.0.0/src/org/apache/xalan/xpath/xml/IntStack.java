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

import java.util.EmptyStackException;

/**
 * <meta name="usage" content="internal"/>
 * Implement an array of simple integers.
 */
public class IntStack extends IntVector
{
  /**
   * Default constructor.  Note that the default 
   * block size is very small, for small lists.
   */
  public IntStack()
  {
    super(); 
  }

  /**
   * Construct a IntVector, using the given block size.
   */
  public IntStack(int blocksize)
  {
    super(blocksize); 
  }
  
  /**
   * Pushes an item onto the top of this stack. 
   *
   * @param   i   the int to be pushed onto this stack.
   * @return  the <code>item</code> argument.
   * @since   JDK1.0
   */
  public int push(int i) 
  {
    addElement(i);

    return i;
  }

  /**
   * Removes the object at the top of this stack and returns that 
   * object as the value of this function. 
   *
   * @return     The object at the top of this stack.
   * @exception  EmptyStackException  if this stack is empty.
   * @since      JDK1.0
   */
  public int pop() 
  {
    int	i;
    int	len = size();

    i = peek();
    removeElementAt(len - 1);

    return i;
  }
  
  /**
   * Quickly pops a number of items from the stack. 
   *
   * @exception  EmptyStackException  if this stack is empty.
   */
  public void quickPop(int n) 
  {
    m_firstFree -= n;
  }


  /**
   * Looks at the object at the top of this stack without removing it 
   * from the stack. 
   *
   * @return     the object at the top of this stack. 
   * @exception  EmptyStackException  if this stack is empty.
   * @since      JDK1.0
   */
  public int peek() 
  {
    int	len = size();

    if (len == 0)
      throw new EmptyStackException();
    return elementAt(len - 1);
  }

  /**
   * Sets an object at a the top of the statck 
   *
   * @exception  EmptyStackException  if this stack is empty.
   * @since      JDK1.0
   */
  public void setTop(int val) 
  {
    int	len = size();
    if (len == 0)
      throw new EmptyStackException();
    setElementAt(val, len - 1);
  }

  /**
   * Tests if this stack is empty.
   *
   * @return  <code>true</code> if this stack is empty;
   *          <code>false</code> otherwise.
   * @since   JDK1.0
   */
  public boolean empty() 
  {
    return size() == 0;
  }

  /**
   * Returns where an object is on this stack. 
   *
   * @param   o   the desired object.
   * @return  the distance from the top of the stack where the object is]
   *          located; the return value <code>-1</code> indicates that the
   *          object is not on the stack.
   * @since   JDK1.0
   */
  public int search(int o) 
  {
    int i = lastIndexOf(o);

    if (i >= 0) 
    {
      return size() - i;
    }
    return -1;
  }

}
