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
 * $Id: FuncUnparsedEntityURI.java,v 1.1 2006/03/09 00:07:55 vauchers Exp $
 */
package org.apache.xpath.functions;

import org.apache.xml.dtm.DTM;
import org.apache.xpath.XPathContext;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.objects.XString;

/**
 * @xsl.usage advanced
 */
public class FuncUnparsedEntityURI extends FunctionOneArg
{

  /**
   * Execute the function.  The function must return
   * a valid object.
   * @param xctxt The current execution context.
   * @return A valid XObject.
   *
   * @throws javax.xml.transform.TransformerException
   */
  public XObject execute(XPathContext xctxt) throws javax.xml.transform.TransformerException
  {

    String name = m_arg0.execute(xctxt).str();
    int context = xctxt.getCurrentNode();
    DTM dtm = xctxt.getDTM(context);
    int doc = dtm.getDocument();
    
    String uri = dtm.getUnparsedEntityURI(name);

    return new XString(uri);
  }
}
