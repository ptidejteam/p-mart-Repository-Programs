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
package org.apache.xalan.xpath; 

import org.w3c.dom.*;
import java.util.Vector;
import org.apache.xalan.xpath.res.XPATHErrorResources;

/**
 * <meta name="usage" content="advanced"/>
 * Execute the Normalize-space() function.
 */
public class FuncNormalizeSpace extends Function
{
  /**
   * Execute the function.  The function must return 
   * a valid object.
   * @param path The executing xpath.
   * @param context The current context.
   * @param opPos The current op position.
   * @param args A list of XObject arguments.
   * @return A valid XObject.
   */
  public XObject execute(XPath path, XPathSupport execContext, Node context, int opPos, Vector args) 
    throws org.xml.sax.SAXException
  {    
    int nArgs = args.size();
    if(nArgs > 0)
    {
      if(nArgs > 1)
        path.error(context, XPATHErrorResources.ER_NORMALIZESPACE_HAS_TOO_MANY_ARGS); //"normalize-space() has too many arguments.");
      
      String s1 = ((XObject)args.elementAt(0)).str();
      return new XString(fixWhiteSpace(s1, true, true, false));
    }
    else
    {
      String s1 = XNodeSet.getStringFromNode(context);
      return new XString(fixWhiteSpace(s1, true, true, false));
    }
  }
  
  /**
   * Returns whether the specified <var>ch</var> conforms to the XML 1.0 definition
   * of whitespace.  Refer to <A href="http://www.w3.org/TR/1998/REC-xml-19980210#NT-S">
   * the definition of <CODE>S</CODE></A> for details.
   * @param   ch      Character to check as XML whitespace.
   * @return          =true if <var>ch</var> is XML whitespace; otherwise =false.
   */
  private static boolean isSpace(char ch) 
  {
    return Character.isWhitespace(ch); // Take the easy way out for now.
  }
  
  /**
   * (Code stolen and modified from XML4J)
   * Conditionally trim all leading and trailing whitespace in the specified String.  
   * All strings of white space are 
   * replaced by a single space character (#x20), except spaces after punctuation which 
   * receive double spaces if doublePunctuationSpaces is true.
   * This function may be useful to a formatter, but to get first class
   * results, the formatter should probably do it's own white space handling 
   * based on the semantics of the formatting object.
   * @param   string      String to be trimmed.
   * @param   trimHead    Trim leading whitespace?
   * @param   trimTail    Trim trailing whitespace?
   * @param   doublePunctuationSpaces    Use double spaces for punctuation?
   * @return              The trimmed string.
   */
  protected String fixWhiteSpace(String string, 
                              boolean trimHead, 
                              boolean trimTail, 
                              boolean doublePunctuationSpaces) 
  {
    char[] buf = string.toCharArray();
    int len = buf.length;
    boolean edit = false;
    int s;
    for (s = 0;  s < len;  s++) 
    {
      if (isSpace(buf[s]))  
      {
        break;
      }
    }
    /* replace S to ' '. and ' '+ -> single ' '. */
    int d = s;
    boolean pres = false;
    for ( ;  s < len;  s ++) 
    {
      char c = buf[s];
      if (isSpace(c)) 
      {
        if (!pres) 
        {
          if (' ' != c)  
          {
            edit = true;
          }
          buf[d++] = ' ';
          if(doublePunctuationSpaces && (s != 0))
          {
            char prevChar = buf[s-1];
            if(!((prevChar == '.') || (prevChar == '!') || (prevChar == '?')))
            {
              pres = true;
            }
          }
          else
          {
            pres = true;
          }
        }
        else
        {
          edit = true;
          pres = true;
        }
      }
      else 
      {
        buf[d++] = c;
        pres = false;
      }
    }
    if (trimTail && 1 <= d && ' ' == buf[d-1]) 
    {
      edit = true;
      d --;
    }
    int start = 0;
    if (trimHead && 0 < d && ' ' == buf[0]) 
    {
      edit = true;
      start ++;
    }
    return edit ? new String(buf, start, d-start) : string;
  }

}
