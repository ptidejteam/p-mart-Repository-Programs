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
package org.apache.xalan.transformer;

import org.xml.sax.ContentHandler;
import javax.xml.transform.TransformerException;

import org.apache.xalan.trace.GenerateEvent;

/**
 * Tracks the state of a queued document event.
 */
public class QueuedStartDocument extends QueuedSAXEvent
{

  /**
   * Constructor QueuedStartDocument
   *
   */
  public QueuedStartDocument()
  {
    super(DOC);
  }

  /**
   * Clear the pending event.
   */
  void clearPending()
  {
    super.clearPending();
  }

  /**
   * Set Whether this document event is pending
   *
   *
   * @param b Flag indicating whether this document event is pending
   */
  void setPending(boolean b)
  {
    super.setPending(b);
  }

  /**
   * Flush the event.
   *
   * @throws TransformerException
   */
  void flush() throws org.xml.sax.SAXException
  {

    if (isPending)
    {
      m_contentHandler.startDocument();
      
      if(null != m_traceManager)
      {
        fireGenerateEvent(GenerateEvent.EVENTTYPE_STARTDOCUMENT, null, null);
      }

      ContentHandler chandler = getContentHandler();

      if ((null != chandler) && (chandler instanceof TransformerClient))
      {
        ((TransformerClient) chandler).setTransformState(m_transformer);
      }

      super.flush();
    }
  }

  /**
   * Flag to indicate that we have some document content since the last
   * call to startDocument()
   */
  private boolean m_isTextEntity = false;

  /**
   * Set whether we have some document content since the last
   * call to startDocument()
   *
   * @param b Flag indicating whether we have some document content since the last
   * call to startDocument()
   */
  void setIsTextEntity(boolean b)
  {
    m_isTextEntity = b;
  }

  /**
   * Get whether we have some document content since the last
   * call to startDocument()
   *
   * @return Flag indicating whether we have some document content since the last
   * call to startDocument()
   */
  boolean getIsTextEntity()
  {
    return m_isTextEntity;
  }

  /**
   * Reset this event's isPending and isTextEntity flags to false 
   *
   */
  void reset()
  {

    super.reset();

    m_isTextEntity = false;
  }
}
