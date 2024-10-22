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
 * $Id: XSLInfiniteLoopException.java,v 1.1 2006/03/01 21:15:43 vauchers Exp $
 */
package org.apache.xalan.transformer;

/**
 * Class used to create an Infinite Loop Exception 
 * @xsl.usage internal
 */
class XSLInfiniteLoopException
{

  /**
   * Constructor XSLInfiniteLoopException
   *
   */
  XSLInfiniteLoopException()
  {
    super();
  }

  /**
   * Get Message associated with the exception
   *
   *
   * @return Message associated with the exception
   */
  public String getMessage()
  {
    return "Processing Terminated.";
  }
}
