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
 * $Id: FuncRound.java,v 1.1 2006/03/09 00:07:55 vauchers Exp $
 */
package org.apache.xpath.functions;

import org.apache.xpath.XPathContext;
import org.apache.xpath.objects.XNumber;
import org.apache.xpath.objects.XObject;

/**
 * Execute the round() function.
 * @xsl.usage advanced
 */
public class FuncRound extends FunctionOneArg
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
          final XObject obj = m_arg0.execute(xctxt);
          final double val= obj.num();
          if (val >= -0.5 && val < 0) return new XNumber(-0.0);
          if (val == 0.0) return new XNumber(val);
          return new XNumber(java.lang.Math.floor(val
                                            + 0.5));
  }
}
