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
 * $Id: Gte.java,v 1.1 2006/03/01 21:16:05 vauchers Exp $
 */
package org.apache.xpath.operations;

import org.apache.xpath.objects.XBoolean;
import org.apache.xpath.objects.XObject;

/**
 * The '>=' operation expression executer.
 */
public class Gte extends Operation
{
    static final long serialVersionUID = 9142945909906680220L;

  /**
   * Apply the operation to two operands, and return the result.
   *
   *
   * @param left non-null reference to the evaluated left operand.
   * @param right non-null reference to the evaluated right operand.
   *
   * @return non-null reference to the XObject that represents the result of the operation.
   *
   * @throws javax.xml.transform.TransformerException
   */
  public XObject operate(XObject left, XObject right)
          throws javax.xml.transform.TransformerException
  {
    return left.greaterThanOrEqual(right)
           ? XBoolean.S_TRUE : XBoolean.S_FALSE;
  }
}
