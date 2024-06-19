/**
 * TestDocumentAdapterBuilder
 *
 * Copyright (c) 2004-2006
 *      Marty Phelan, All rights reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */
package com.taursys.dom.test.util;

import java.io.IOException;
import java.io.InputStream;

import com.taursys.debug.Debug;
import com.taursys.dom.DocumentAdapter;
import com.taursys.dom.DocumentAdapterBuilder;
import com.taursys.dom.DocumentAdapterBuilderException;

/**
 * TestDocumentAdapterBuilder for the DocumentAdapterBuilderFactoryTest
 * 
 * @author marty
 * @version $Revision: 1.2 $
 */
public class TestDocumentAdapterBuilder extends DocumentAdapterBuilder {

  /**
   * 
   */
  public TestDocumentAdapterBuilder() {
    super();
  }

  /**
   * Test method checks that InputStream is not null and that it can read a
   * byte from the stream.
   * @param is InputStream that contains data
   * @throws DocumentAdapterBuilderException if problems
   */
  public DocumentAdapter build(InputStream is)
    throws DocumentAdapterBuilderException {
    if (is == null) {
      throw new DocumentAdapterBuilderException(MESSAGE_NULL_INPUT_STREAM);
    }
    try {
      is.read();
    } catch (IOException e) {
      Debug.debug("IO Error",e);
      throw new DocumentAdapterBuilderException("IO Error:" + e.getMessage());
    }
    return null;
  }

  /* (non-Javadoc)
   * @see com.taursys.dom.DocumentAdapterBuilder#setProperty(java.lang.String, java.lang.Object)
   */
  public void setProperty(String propertyName, Object value) {
    // TODO Auto-generated method stub
    
  }

  /* (non-Javadoc)
   * @see com.taursys.dom.DocumentAdapterBuilder#getProperty(java.lang.String)
   */
  public Object getProperty(String propertyName) {
    // TODO Auto-generated method stub
    return null;
  }

  /* (non-Javadoc)
   * @see com.taursys.dom.DocumentAdapterBuilder#setFeature(java.lang.String, boolean)
   */
  public void setFeature(String featureName, boolean value) {
    // TODO Auto-generated method stub
    
  }

  /* (non-Javadoc)
   * @see com.taursys.dom.DocumentAdapterBuilder#getFeature(java.lang.String)
   */
  public boolean getFeature(String featureName) {
    // TODO Auto-generated method stub
    return false;
  }

}
