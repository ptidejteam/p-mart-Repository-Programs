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
 * $Id: TransformerClient.java,v 1.1 2006/03/01 21:15:42 vauchers Exp $
 */
package org.apache.xalan.transformer;

/**
 * A content handler can get a reference
 * to a TransformState by implementing
 * the TransformerClient interface.  Xalan will check for
 * that interface before it calls startDocument, and, if it
 * is implemented, pass in a TransformState reference to the
 * setTransformState method.
 */
public interface TransformerClient
{

  /**
   * Pass in a reference to a TransformState object, which
   * can be used during SAX ContentHandler events to obtain
   * information about he state of the transformation. This
   * method will be called  before each startDocument event.
   *
   * @param ts A reference to a TransformState object
   */
  void setTransformState(TransformState ts);
}
