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
 * $Id: ResultNameSpace.java,v 1.1 2006/03/09 00:07:33 vauchers Exp $
 */
package org.apache.xalan.transformer;

/**
 * A representation of a result namespace.  One of these will
 * be pushed on the result tree namespace stack for each
 * result tree element.
 * @xsl.usage internal
 */
public class ResultNameSpace
{

  /** Pointer to next ResultNameSpace          */
  public ResultNameSpace m_next = null;

  /** Prefix of namespace         */
  public String m_prefix;

  /** Namespace URI          */
  public String m_uri;  // if null, then Element namespace is empty.

  /**
   * Construct a namespace for placement on the
   * result tree namespace stack.
   *
   * @param prefix of result namespace
   * @param uri URI of result namespace
   */
  public ResultNameSpace(String prefix, String uri)
  {
    m_prefix = prefix;
    m_uri = uri;
  }
}
