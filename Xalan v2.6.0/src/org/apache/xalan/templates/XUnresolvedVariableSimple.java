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
 * $Id: XUnresolvedVariableSimple.java,v 1.1 2006/03/09 00:07:21 vauchers Exp $
 */
package org.apache.xalan.templates;

import org.apache.xpath.Expression;
import org.apache.xpath.XPathContext;
import org.apache.xpath.objects.XObject;


/**
 * This is the same as XUnresolvedVariable, but it assumes that the 
 * context is already set up.  For use with psuedo variables.
 * Also, it holds an Expression object, instead of an ElemVariable.
 * It must only hold static context, since a single copy will be 
 * held in the template.
 */
public class XUnresolvedVariableSimple extends XObject
{
  public XUnresolvedVariableSimple(ElemVariable obj)
  {
    super(obj);
  }
    
	
  /**
   * For support of literal objects in xpaths.
   *
   * @param xctxt The XPath execution context.
   *
   * @return This object.
   *
   * @throws javax.xml.transform.TransformerException
   */
  public XObject execute(XPathContext xctxt) throws javax.xml.transform.TransformerException
  {
  	Expression expr = ((ElemVariable)m_obj).getSelect().getExpression();
    XObject xobj = expr.execute(xctxt);
    xobj.allowDetachToRelease(false);
    return xobj;
  }
  
  /**
   * Tell what kind of class this is.
   *
   * @return CLASS_UNRESOLVEDVARIABLE
   */
  public int getType()
  {
    return CLASS_UNRESOLVEDVARIABLE;
  }
  
  /**
   * Given a request type, return the equivalent string.
   * For diagnostic purposes.
   *
   * @return An informational string.
   */
  public String getTypeString()
  {
    return "XUnresolvedVariableSimple (" + object().getClass().getName() + ")";
  }


}

