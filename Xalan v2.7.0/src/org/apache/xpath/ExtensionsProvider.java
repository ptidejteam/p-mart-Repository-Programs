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
 * $Id: ExtensionsProvider.java,v 1.1 2006/03/01 21:15:09 vauchers Exp $
 */
package org.apache.xpath;

import java.util.Vector;

import org.apache.xpath.functions.FuncExtFunction;

/**
 * Interface that XPath objects can call to obtain access to an 
 * ExtensionsTable.
 * 
 */
public interface ExtensionsProvider
{
  /**
   * Is the extension function available?
   */
  
  public boolean functionAvailable(String ns, String funcName)
          throws javax.xml.transform.TransformerException;
  
  /**
   * Is the extension element available?
   */
  public boolean elementAvailable(String ns, String elemName)
          throws javax.xml.transform.TransformerException;
   
  /**
   * Execute the extension function.
   */
  public Object extFunction(String ns, String funcName, 
                            Vector argVec, Object methodKey)
            throws javax.xml.transform.TransformerException;

  /**
   * Execute the extension function.
   */
  public Object extFunction(FuncExtFunction extFunction, 
                            Vector argVec)
            throws javax.xml.transform.TransformerException;
}
