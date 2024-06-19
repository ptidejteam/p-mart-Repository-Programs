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

import java.util.*;
import org.w3c.dom.*;
import org.apache.xalan.xpath.xml.PrefixResolver;
import org.apache.xalan.xpath.res.XPATHErrorResources;
import org.apache.xalan.xpath.xml.XSLMessages;
import org.apache.xalan.xpath.xml.ProblemListener;
import org.apache.xalan.xpath.xml.StringKey;

/**
 * <meta name="usage" content="general"/>
 * Tokenizes and parses XPath expressions. This should really be named 
 * XPathParserImpl, and may be renamed in the future.
 */
public class XPathProcessorImpl implements XPathProcessor, java.io.Serializable
{
  private XPath m_xpath;
  transient private XPathSupport m_support;

  /**
   * The next token in the pattern.
   */
  private String m_token;

  /**
   * The first char in m_token, the theory being that this
   * is an optimization because we won't have to do charAt(0) as
   * often.
   */
  private char m_tokenChar = 0;

  /**
   * The position in the token queue is tracked by m_queueMark.
   */
  int m_queueMark = 0;

  /**
   * The parser constructor.
   * @param callbacks The execution context.
   */
  public XPathProcessorImpl(XPathSupport callbacks)
  {
    m_support = callbacks;
  }

  /**
   * The parser constructor.  This constructor creates 
   * it's own XPathSupportDefault object, which is only 
   * useful for some limited cases.
   */
  public XPathProcessorImpl()
  {
    m_support = new XPathSupportDefault();
  }


  /* For diagnostics */
  Hashtable m_durationsTable = new Hashtable();

  /**
   * The prefix resolver to map prefixes to namespaces in the XPath.
   */
  PrefixResolver m_namespaceContext;
  
  /**
   * Ignore this, it is going away.
   * This holds a map to the m_tokenQueue that tells where the top-level elements are.
   * It is used for pattern matching so the m_tokenQueue can be walked backwards.
   * Each element that is a 'target', (right-most top level element name) has 
   * TARGETEXTRA added to it.
   * 
   */
  int m_patternMap[] = new int[100];
  
  /**
   * Ignore this, it is going away.
   * The number of elements that m_patternMap maps;
   */
  int m_patternMapSize;

  /**
   * Given an string, init an XPath object for selections, 
   * in order that a parse doesn't 
   * have to be done each time the expression is evaluated.
   * @param pathObj The XPath object to be initialized.
   * @param expresson A String representing the XPath.
   * @param namespaceContext An object that is able to resolve prefixes in 
   * the XPath to namespaces.
   */
  public void initXPath(XPath pathObj, String expression, PrefixResolver namespaceContext)
    throws org.xml.sax.SAXException
  {
    m_xpath = pathObj;
    m_namespaceContext = namespaceContext;
    tokenize( expression );
    m_xpath.m_opMap[0] = XPath.OP_XPATH;
    m_xpath.m_opMap[XPath.MAPINDEX_LENGTH] = 2;
    nextToken();
    Expr();
    if(null != m_token)
    {
      String extraTokens = "";
      while(null != m_token)
      {
        extraTokens += "'"+m_token+"'";
        nextToken();
        if(null != m_token)
          extraTokens += ", ";
      }
      error(XPATHErrorResources.ER_EXTRA_ILLEGAL_TOKENS, new Object[] {extraTokens}); //"Extra illegal tokens: "+extraTokens);
    }
    pathObj.shrink();
    doStaticAnalysis(pathObj);
  }

  /**
   * Analyze the XPath object to give optimization information.
   */
  void doStaticAnalysis(XPath pathObj)
  {
  }

  /**
   * Given an string, init an XPath object for pattern matches, 
   * in order that a parse doesn't 
   * have to be done each time the expression is evaluated.
   * @param pathObj The XPath object to be initialized.
   * @param expresson A String representing the XPath.
   * @param namespaceContext An object that is able to resolve prefixes in 
   * the XPath to namespaces.
   */
  public void initMatchPattern(XPath pathObj, String expression, PrefixResolver namespaceContext)
    throws org.xml.sax.SAXException
  {
    m_xpath = pathObj;
    m_namespaceContext = namespaceContext;
    tokenize( expression );
    m_xpath.m_opMap[0] = XPath.OP_MATCHPATTERN;
    m_xpath.m_opMap[XPath.MAPINDEX_LENGTH] = 2;
    nextToken();
    Pattern();
    if(null != m_token)
    {
      String extraTokens = "";
      while(null != m_token)
      {
        extraTokens += "'"+m_token+"'";
        nextToken();
        if(null != m_token)
          extraTokens += ", ";
      }
      error(XPATHErrorResources.ER_EXTRA_ILLEGAL_TOKENS, new Object[] {extraTokens}); //"Extra illegal tokens: "+extraTokens);
    }
    // Terminate for safety.
    m_xpath.m_opMap[m_xpath.m_opMap[XPath.MAPINDEX_LENGTH]] = XPath.ENDOP;
    m_xpath.m_opMap[XPath.MAPINDEX_LENGTH] += 1;
    m_xpath.shrink();
   }

  /**
   * Walk through the expression and build a token queue, and a map of the top-level
   * elements.
   * @param pat XSLT Expression.
   */
  private void tokenize(String pat)
    throws org.xml.sax.SAXException
  {
    tokenize(pat, null);
  }

  /**
   * Walk through the expression and build a token queue, and a map of the top-level
   * elements.
   * @param pat XSLT Expression.
   * @param targetStrings Vector to hold Strings, may be null.
   */
  private void tokenize(String pat, Vector targetStrings)
    throws org.xml.sax.SAXException
  {
    m_xpath.m_tokenQueueSize = 0;
    m_xpath.m_currentPattern = pat;
    m_patternMapSize = 0;
    m_xpath.m_opMap = new int[XPath.MAXTOKENQUEUESIZE*5];
    int nChars = pat.length();
    int startSubstring = -1;
    int posOfNSSep = -1;
    boolean isStartOfPat = true;
    boolean isAttrName = false;
    boolean isNum = false;

    // Nesting of '[' so we can know if the given element should be
    // counted inside the m_patternMap.
    int nesting = 0;

    // char[] chars = pat.toCharArray();
    for(int i = 0; i < nChars; i++)
    {
      char c = pat.charAt(i);
      switch(c)
      {
      case '\"':
        {
          if(startSubstring != -1)
          {
            isNum = false;
            isStartOfPat = mapPatternElemPos(nesting, isStartOfPat, isAttrName);
            isAttrName = false;
            if(-1 != posOfNSSep)
            {
              posOfNSSep = mapNSTokens(pat, startSubstring, posOfNSSep, i);
            }
            else
            {
              addToTokenQueue(pat.substring(startSubstring, i));
            }
          }
          startSubstring = i;
          for(i++; (i < nChars) && ((c = pat.charAt(i)) != '\"'); i++);
          if(c == '\"')
          {
            addToTokenQueue(pat.substring(startSubstring, i+1));
            startSubstring = -1;
          }
          else
          {
            error(XPATHErrorResources.ER_EXPECTED_DOUBLE_QUOTE); //"misquoted literal... expected double quote!");
          }
        }
        break;

      case '\'':
        if(startSubstring != -1)
        {
          isNum = false;
          isStartOfPat = mapPatternElemPos(nesting, isStartOfPat, isAttrName);
          isAttrName = false;
          if(-1 != posOfNSSep)
          {
            posOfNSSep = mapNSTokens(pat, startSubstring, posOfNSSep, i);
          }
          else
          {
            addToTokenQueue(pat.substring(startSubstring, i));
          }
        }
        startSubstring = i;
        for(i++; (i < nChars) && ((c = pat.charAt(i)) != '\''); i++);
        if(c == '\'')
        {
          addToTokenQueue(pat.substring(startSubstring, i+1));
          startSubstring = -1;
        }
        else
        {
          error(XPATHErrorResources.ER_EXPECTED_SINGLE_QUOTE); //"misquoted literal... expected single quote!");
        }
        break;

      case 0x0A:
      case 0x0D:
      case ' ':
      case '\t':
        if(startSubstring != -1)
        {
          isNum = false;
          isStartOfPat = mapPatternElemPos(nesting, isStartOfPat, isAttrName);
          isAttrName = false;
          if(-1 != posOfNSSep)
          {
            posOfNSSep = mapNSTokens(pat, startSubstring, posOfNSSep, i);
          }
          else
          {
            addToTokenQueue(pat.substring(startSubstring, i));
          }
          startSubstring = -1;
        }
        break;

      case '@':
        isAttrName = true;
        // fall-through on purpose

      case '-':
        if('-' == c)
        {
          if(!(isNum || (startSubstring == -1)))
          {
            break;
          }
          isNum = false;
        }
        // fall-through on purpose

      case '(':
      case '[':
      case ')':
      case ']':
      case '|':
      case '/':
      case '*':
      case '+':
      case '=':
      case ',':
      case '\\': // Unused at the moment
      case '^': // Unused at the moment
      case '!': // Unused at the moment
      case '$':
      case '<':
      case '>':
        if(startSubstring != -1)
        {
          isNum = false;
          isStartOfPat = mapPatternElemPos(nesting, isStartOfPat, isAttrName);
          isAttrName = false;
          if(-1 != posOfNSSep)
          {
            posOfNSSep = mapNSTokens(pat, startSubstring, posOfNSSep, i);
          }
          else
          {
            addToTokenQueue(pat.substring(startSubstring, i));
          }
          startSubstring = -1;
        }
        else if(('/' == c) && isStartOfPat)
        {
          isStartOfPat = mapPatternElemPos(nesting, isStartOfPat, isAttrName);
        }
        else if('*' == c)
        {
          isStartOfPat = mapPatternElemPos(nesting, isStartOfPat, isAttrName);
          isAttrName = false;
        }

        if(0 == nesting)
        {
          if('|' == c)
          {
            if(null != targetStrings)
            {
              recordTokenString(targetStrings);
            }
            isStartOfPat = true;
          }
        }
        if((')' == c) || (']' == c))
        {
          nesting--;
        }
        else if(('(' == c) || ('[' == c))
        {
          nesting++;
        }
        addToTokenQueue(pat.substring(i, i+1));

       break;

      case ':':
       if(posOfNSSep == (i-1))
       {
         if(startSubstring != -1)
         {
           if(startSubstring < (i-1))
             addToTokenQueue(pat.substring(startSubstring, i-1));
         }
         isNum = false;
         isAttrName = false;
         startSubstring = -1;
         posOfNSSep = -1;

         addToTokenQueue(pat.substring(i-1, i+1));
         break;
       }
       else
       {
         posOfNSSep = i;
       }
        // fall through on purpose

      default:
        if(-1 == startSubstring)
        {
          startSubstring = i;
          isNum = Character.isDigit(c);
        }
        else if(isNum)
        {
          isNum = Character.isDigit(c);
        }
      }
    }
    if(startSubstring != -1)
    {
      isNum = false;
      isStartOfPat = mapPatternElemPos(nesting, isStartOfPat, isAttrName);
      if(-1 != posOfNSSep)
      {
        posOfNSSep = mapNSTokens(pat, startSubstring, posOfNSSep, nChars);
      }
      else
      {
        addToTokenQueue(pat.substring(startSubstring, nChars));
      }
    }

    if(0 == m_xpath.m_tokenQueueSize)
    {
      error(XPATHErrorResources.ER_EMPTY_EXPRESSION); //"Empty expression!");
    }
    else if(null != targetStrings)
    {
      recordTokenString(targetStrings);
    }
    m_queueMark = 0;
  }


  /**
   * Record the current position on the token queue as long as
   * this is a top-level element.  Must be called before the
   * next token is added to the m_tokenQueue.
   */
  private boolean mapPatternElemPos(int nesting, boolean isStart, boolean isAttrName)
  {
    if(0 == nesting)
    {
      if(!isStart)
      {
        m_patternMap[m_patternMapSize-1] -= TARGETEXTRA;
      }
      m_patternMap[m_patternMapSize]
        = (m_xpath.m_tokenQueueSize - (isAttrName ? 1 : 0)) + TARGETEXTRA;
      m_patternMapSize++;
      isStart = false;
    }
    return isStart;
  }

  /**
   * Record the correct token string in the passed vector.
   */
  private void recordTokenString(Vector targetStrings)
  {
    int tokPos = getTokenQueuePosFromMap(m_patternMapSize-1);
    resetTokenMark(tokPos+1);

    if(lookahead('(', 1))
    {
      int tok = getKeywordToken(m_token);
      switch(tok)
      {
      case XPath.NODETYPE_COMMENT:
        targetStrings.addElement(XPath.PSEUDONAME_COMMENT);
        break;
      case XPath.NODETYPE_TEXT:
        targetStrings.addElement(XPath.PSEUDONAME_TEXT);
        break;
      case XPath.NODETYPE_NODE:
        targetStrings.addElement(XPath.PSEUDONAME_ANY);
        break;
      case XPath.NODETYPE_ROOT:
        targetStrings.addElement(XPath.PSEUDONAME_ROOT);
        break;
      case XPath.NODETYPE_ANYELEMENT:
        targetStrings.addElement(XPath.PSEUDONAME_ANY);
        break;
      case XPath.NODETYPE_PI:
        targetStrings.addElement(XPath.PSEUDONAME_ANY);
        break;
      default:
        targetStrings.addElement(XPath.PSEUDONAME_ANY);
      }
    }
    else
    {
      if(tokenIs('@'))
      {
        tokPos++;
        resetTokenMark(tokPos+1);
      }
      if(lookahead(':', 1))
      {
        tokPos += 2;
      }
      targetStrings.addElement(m_xpath.m_tokenQueue[tokPos]);
    }
  }

  private final void addToTokenQueue(String s)
  {
    m_xpath.m_tokenQueue[m_xpath.m_tokenQueueSize++] = s;
  }

  /**
   * When a seperator token is found, see if there's a element name or
   * the like to map.
   */
  private int mapNSTokens(String pat, int startSubstring, int posOfNSSep, int posOfScan)
  {
    String prefix = pat.substring(startSubstring, posOfNSSep);
    String uName;
    if((null != m_namespaceContext) && !prefix.equals("*") && !prefix.equals("xmlns"))
    {
      try
      {
        if(prefix.length() > 0)
          uName = ((PrefixResolver)m_namespaceContext).getNamespaceForPrefix(prefix);
        else
        {
          // Assume last was wildcard. This is not legal according
          // to the draft. Set the below to true to make namespace
          // wildcards work.
          if(false)
          {
            addToTokenQueue(":");
            String s = pat.substring(posOfNSSep+1, posOfScan);
            if(s.length() > 0)
              addToTokenQueue(s);
            return -1;
          }
          else
          {
            uName = ((PrefixResolver)m_namespaceContext).getNamespaceForPrefix(prefix);
          }
        }
      }
      catch(ClassCastException cce)
      {
        uName = m_namespaceContext.getNamespaceForPrefix(prefix);
      }
    }
    else
    {
      uName = prefix;
    }
    if((null != uName) && (uName.length() > 0))
    {
      addToTokenQueue(uName);
      addToTokenQueue(":");
      String s = pat.substring(posOfNSSep+1, posOfScan);
      if(s.length() > 0)
        addToTokenQueue(s);
    }
    else
    {
      // error("Could not locate namespace for prefix: "+prefix);
      addToTokenQueue(prefix);
      addToTokenQueue(":");
      String s = pat.substring(posOfNSSep+1, posOfScan);
      if(s.length() > 0)
        addToTokenQueue(s);
    }
    return -1;
  }

  /**
   * Given a map pos, return the corresponding token queue pos.
   */
  int getTokenQueuePosFromMap(int i)
  {
    int pos = m_patternMap[i];
    return (pos >= TARGETEXTRA) ? (pos - TARGETEXTRA) : pos;
  }

  /**
   * Return the index above the passed index that
   * is the target element, i.e. >= TARGETEXTRA.
   * If there is no next target, it return -1.
   * Pass -1 in to start testing from zero.
   */
  int getNextTargetIndexInMap(int i)
  {
    int next = -1;

    for(int k = i+1; k < m_patternMapSize; k++)
    {
      int pos = m_patternMap[k];
      if(pos >= TARGETEXTRA)
      {
        next = k;
        break;
      }
    }
    return next;
  }

  /**
   * Return the normalized index into the pattern
   * map above the passed index, or -1 if it is the last pattern.
   */
  int getNextIndexInMap(int i)
  {
    int next = (m_patternMap[i] >= TARGETEXTRA)
               ? -1 : (((i+1) < m_patternMapSize)
                       ? (i+1) : -1);
    return next;
  }

  /**
   * Return the next index from the passed index,
   * or -1 if it the passed index is the last index of the
   * subpattern.
   */
  int getNextPatternPos(int i)
  {
    int next = -1;
    int k = (i+1);
    int nElems = m_patternMapSize;

    if(k < nElems)
    {
      int prevPos = (i >= 0) ? m_patternMap[i] : 0;
      if(prevPos != TARGETEXTRA)
      {
        next = m_patternMap[k];
      }
    }
    return next;
  }


  /**
   * Return the previous index from the passed index,
   * or -1 if it the passed index is the first index of the
   * subpattern.
   */
  int getPrevMapIndex(int i)
  {
    int prev = -1;
    int k = (i-1);

    if(k >= 0)
    {
      int pos = m_patternMap[k];
      if(pos < TARGETEXTRA)
      {
        prev = k;
      }
    }
    return prev;
  }


  /**
   * Check whether m_token==s. If m_token is null, returns false (or true if s is also null);
   * do not throw an exception.
   */
  private final boolean tokenIs( String s )
  {
    return ( m_token!=null ) ? (m_token.equals(s)) : ( s==null );
  }

  /**
   * Check whether m_token==c. If m_token is null, returns false (or true if c is also null);
   * do not throw an exception.
   */
  private final boolean tokenIs( char c )
  {
    return ( m_token!=null ) ? (m_tokenChar == c) : false;
  }

  /**
   * Look ahead of the current token in order to
   * make a branching decision.
   * @param s the string to compare it to.
   * @param n number of tokens to look ahead.  Must be
   * greater than 1.
   */
  private final boolean lookahead( char c, int n )
  {
    int pos = (m_queueMark+n);
    boolean b;
    if((pos <= m_xpath.m_tokenQueueSize) && (pos > 0) && (m_xpath.m_tokenQueueSize != 0))
    {
      String tok = ((String)m_xpath.m_tokenQueue[pos-1]);
      b = (tok.length() == 1) ? (tok.charAt(0) == c) : false;
    }
    else
    {
      b = false;
    }
    return b;
  }

  /**
   * Look behind the first character of the current token in order to
   * make a branching decision.
   * @param c the character to compare it to.
   * @param n number of tokens to look behind.  Must be
   * greater than 1.  Note that the look behind terminates
   * at either the beginning of the string or on a '|'
   * character.  Because of this, this method should only
   * be used for pattern matching.
   */
  private final boolean lookbehind( char c, int n )
  {
    boolean isToken;
    int lookBehindPos = m_queueMark-(n+1);
    if( lookBehindPos >= 0 )
    {
      String lookbehind = (String)m_xpath.m_tokenQueue[lookBehindPos];
      if(lookbehind.length() == 1)
      {
        char c0 = ( lookbehind == null ) ? '|' : lookbehind.charAt(0);
        isToken = ( c0 == '|' ) ? false : (c0 == c);
      }
      else
      {
        isToken = false;
      }
    }
    else
    {
      isToken = false;
    }
    return isToken;
  }

  /**
   * look behind the current token in order to
   * see if there is a useable token.
   * @param n number of tokens to look behind.  Must be
   * greater than 1.  Note that the look behind terminates
   * at either the beginning of the string or on a '|'
   * character.  Because of this, this method should only
   * be used for pattern matching.
   * @return true if look behind has a token, false otherwise.
   */
  private final boolean lookbehindHasToken( int n )
  {
    boolean hasToken;
    if( (m_queueMark-n) > 0 )
    {
      String lookbehind = (String)m_xpath.m_tokenQueue[m_queueMark - (n-1)];
      char c0 = ( lookbehind == null ) ? '|' : lookbehind.charAt(0);
      hasToken = ( c0 == '|' ) ? false : true;
    }
    else
    {
      hasToken = false;
    }
    return hasToken;
  }

  /**
   * Look ahead of the current token in order to
   * make a branching decision.
   * @param s the string to compare it to.
   * @param n number of tokens to lookahead.  Must be
   * greater than 1.
   */
  private final boolean lookahead( String s, int n )
  {
    boolean isToken;
    if( (m_queueMark+n) <= m_xpath.m_tokenQueueSize )
    {
      String lookahead = (String)m_xpath.m_tokenQueue[m_queueMark + (n-1)];
      isToken = ( lookahead!=null ) ? lookahead.equals(s) : ( s==null );
    }
    else
    {
      isToken = (null == s);
    }
    return isToken;
  }

  /**
   * Retrieve the next token from the command and
   * store it in m_token string.
   */
  private final void nextToken()
  {
    if( m_queueMark < m_xpath.m_tokenQueueSize )
    {
      m_token = (String)m_xpath.m_tokenQueue[m_queueMark++];
      m_tokenChar = m_token.charAt(0);
    }
    else
    {
      m_token = null;
      m_tokenChar = 0;
    }
  }

  /**
   * Retrieve a token relative to the current token.
   * @param i Position relative to current token.
   */
  private final String getTokenRelative(int i)
  {
    String tok;
    int relative = m_queueMark+i;
    if( (relative > 0) && (relative < m_xpath.m_tokenQueueSize) )
    {
      tok = (String)m_xpath.m_tokenQueue[relative];
    }
    else
    {
      tok = null;
    }
    return tok;
  }

  /**
   * Retrieve the previous token from the command and
   * store it in m_token string.
   */
  private final void prevToken()
  {
    if( m_queueMark > 0 )
    {
      m_queueMark--;
      m_token = (String)m_xpath.m_tokenQueue[m_queueMark];
      m_tokenChar = m_token.charAt(0);
    }
    else
    {
      m_token = null;
      m_tokenChar = 0;
    }
  }

  /**
   * Reset token queue mark and m_token to a
   * given position.
   * @param mark The new position.
   */
  private final void resetTokenMark(int mark)
  {
    int qsz = m_xpath.m_tokenQueueSize;
    m_queueMark = (mark > 0) ? ((mark <= qsz) ? mark -1 : mark) : 0;
    if( m_queueMark < qsz )
    {
      m_token = (String)m_xpath.m_tokenQueue[m_queueMark++];
      m_tokenChar = m_token.charAt(0);
    }
    else
    {
      m_token = null;
      m_tokenChar = 0;
    }
  }

  /**
   * Consume an expected token, throwing an exception if it
   * isn't there.
   */
  private final void consumeExpected(String expected)
    throws org.xml.sax.SAXException
  {
    if(tokenIs(expected))
    {
      nextToken();
    }
    else
    {
		error(XPATHErrorResources.ER_EXPECTED_BUT_FOUND, new Object[] {expected, m_token}); //"Expected "+expected+", but found: "+m_token);
    }
  }

  /**
   * Consume an expected token, throwing an exception if it
   * isn't there.
   */
  private final void consumeExpected(char expected)
    throws org.xml.sax.SAXException
  {
    if(tokenIs(expected))
    {
      nextToken();
    }
    else
    {
      error(XPATHErrorResources.ER_EXPECTED_BUT_FOUND, new Object[] {String.valueOf(expected), m_token}); //"Expected "+expected+", but found: "+m_token);
    }
  }

  /**
   * If this is true, extra programmer error checks will be made.
   */
  static final boolean m_debug = false;

  /**
   * If m_trace is set to true, trace strings will be written
   * to System.out.
   */
  static final boolean m_trace = false;

  private final void trace(String s)
  {
    System.out.println(s);
  }

  /**
   * Warn the user of a problem.
   */
  void warn(int msg)
    throws XPathProcessorException
  {
    warn(null, msg, null);
  }

  /**
   * Warn the user of a problem.
   */
  void warn(int msg, Object[]args)
    throws XPathProcessorException
  {
    warn(null, msg, args);
  }

  /**
   * Warn the user of a problem.
   */
  void warn(Node sourceNode, int msg)
    throws XPathProcessorException
  {
	  warn(sourceNode, msg, null);
  }

  /**
   * Warn the user of a problem.
   */
  void warn(Node sourceNode, int msg, Object[] args)
    throws XPathProcessorException
  {
	String fmsg = XSLMessages.createXPATHWarning(msg, args);
    boolean shouldThrow = m_xpath.getProblemListener().problem(ProblemListener.XPATHPARSER,
                                                      ProblemListener.WARNING,
                                                      m_namespaceContext,
                                                      sourceNode, fmsg, null, 0, 0);
    if(shouldThrow)
    {
      throw new XPathProcessorException(fmsg);
    }
  }

  /**
   * Notify the user of an assertion error, and probably throw an
   * exception.
   */
  private void assert(boolean b, String msg)
    throws XPathProcessorException
  {
    if(!b)
      error(null, XPATHErrorResources.ER_INCORRECT_PROGRAMMER_ASSERTION, new Object[] {msg}); //"Programmer assertion is incorrect! - "+msg);
  }

  /**
   * Notify the user of an error, and probably throw an
   * exception.
   */
  void error(int msg)
    throws XPathProcessorException
  {
    error(null, msg, null);
  }

  /**
   * Notify the user of an error, and probably throw an
   * exception.
   */
  void error(int msg, Object[] args)
    throws XPathProcessorException
  {
    error(null, msg, args);
  }

  /**
   * Notify the user of an error, and probably throw an
   * exception.
   */
  void error(Node sourceNode, int msg)
    throws XPathProcessorException
  {
	  error(sourceNode, msg, null);
  }

  /**
   * Notify the user of an error, and probably throw an
   * exception.
   */
  void error(Node sourceNode, int msg, Object[] args)
    throws XPathProcessorException
  {
	String fmsg = XSLMessages.createXPATHMessage(msg, args);
    String emsg = ((null != m_xpath.m_currentPattern)
                   ? ("pattern = '"+m_xpath.m_currentPattern+"'\n") : "") +
                  fmsg + dumpRemainingTokenQueue();

    boolean shouldThrow = m_xpath.getProblemListener().problem(ProblemListener.XPATHPARSER,
                                                                      ProblemListener.ERROR,
                                                                      m_namespaceContext,
                                                                      sourceNode, emsg, null, 0, 0);
    if(shouldThrow)
    {
      throw new XPathProcessorException(emsg);
    }
  }

  /**
   * Dump the remaining token queue.
   * Thanks to Craig for this.
   */
  protected String dumpRemainingTokenQueue()
  {
    int q = m_queueMark;
    String returnMsg;
    if(q < m_xpath.m_tokenQueueSize)
    {
      String msg = "\n Remaining tokens: (";
      while (q < m_xpath.m_tokenQueueSize )
      {
        String t = (String)m_xpath.m_tokenQueue[q++];
        msg += (" '" + t + "'");
      }
      returnMsg = msg + ")";
    }
    else
    {
      returnMsg = "";
    }
    return returnMsg;
  }

  /**
   * This value is added to each element name in the TARGETEXTRA
   * that is a 'target' (right-most top-level element name).
   */
  static final int TARGETEXTRA = 10000;

  private static Hashtable m_keywords = new Hashtable();
  private static Hashtable m_axisnames = new Hashtable();
  static Hashtable m_functions = new Hashtable();
  private static Hashtable m_nodetypes = new Hashtable();

  private static final String FROM_ANCESTORS_STRING = "ancestor";
  private static final String FROM_ANCESTORS_OR_SELF_STRING = "ancestor-or-self";
  private static final String FROM_ATTRIBUTES_STRING = "attribute";
  private static final String FROM_CHILDREN_STRING = "child";
  private static final String FROM_DESCENDANTS_STRING = "descendant";
  private static final String FROM_DESCENDANTS_OR_SELF_STRING = "descendant-or-self";
  private static final String FROM_FOLLOWING_STRING = "following";
  private static final String FROM_FOLLOWING_SIBLINGS_STRING = "following-sibling";
  private static final String FROM_PARENT_STRING = "parent";
  private static final String FROM_PRECEDING_STRING = "preceding";
  private static final String FROM_PRECEDING_SIBLINGS_STRING = "preceding-sibling";
  private static final String FROM_SELF_STRING = "self";
  private static final String FROM_NAMESPACE_STRING = "namespace";

  private static final String FROM_SELF_ABBREVIATED_STRING = ".";
  private static final String NODETYPE_COMMENT_STRING = "comment";
  private static final String NODETYPE_TEXT_STRING = "text";
  private static final String NODETYPE_PI_STRING = "processing-instruction";
  private static final String NODETYPE_NODE_STRING = "node";
  private static final String FROM_ATTRIBUTE_STRING = "@";
  private static final String FROM_DOC_STRING = "document";
  private static final String FROM_DOCREF_STRING = "document";
  private static final String FROM_ID_STRING = "id";
  private static final String FROM_IDREF_STRING = "idref";
  private static final String NODETYPE_ANYELEMENT_STRING = "*";
  private static final String FUNC_CURRENT_STRING = "current";
  private static final String FUNC_LAST_STRING = "last";
  private static final String FUNC_POSITION_STRING = "position";
  private static final String FUNC_COUNT_STRING = "count";
  private static final String FUNC_ID_STRING = "id";
  private static final String FUNC_IDREF_STRING = "idref";
  private static final String FUNC_KEY_STRING = "key";
  private static final String FUNC_KEYREF_STRING = "keyref";
  private static final String FUNC_DOC_STRING = "doc";
  private static final String FUNC_DOCUMENT_STRING = "document";
  private static final String FUNC_DOCREF_STRING = "docref";
  private static final String FUNC_LOCAL_PART_STRING = "local-name";
  private static final String FUNC_NAMESPACE_STRING = "namespace-uri";
  private static final String FUNC_NAME_STRING = "name";
  private static final String FUNC_GENERATE_ID_STRING = "generate-id";
  private static final String FUNC_NOT_STRING = "not";
  private static final String FUNC_TRUE_STRING = "true";
  private static final String FUNC_FALSE_STRING = "false";
  private static final String FUNC_BOOLEAN_STRING = "boolean";
  private static final String FUNC_LANG_STRING = "lang";
  private static final String FUNC_NUMBER_STRING = "number";
  private static final String FUNC_FLOOR_STRING = "floor";
  private static final String FUNC_CEILING_STRING = "ceiling";
  private static final String FUNC_ROUND_STRING = "round";
  private static final String FUNC_SUM_STRING = "sum";
  private static final String FUNC_STRING_STRING = "string";
  private static final String FUNC_STARTS_WITH_STRING = "starts-with";
  private static final String FUNC_CONTAINS_STRING = "contains";
  private static final String FUNC_SUBSTRING_BEFORE_STRING = "substring-before";
  private static final String FUNC_SUBSTRING_AFTER_STRING = "substring-after";
  private static final String FUNC_NORMALIZE_SPACE_STRING = "normalize-space";
  private static final String FUNC_TRANSLATE_STRING = "translate";
  private static final String FUNC_CONCAT_STRING = "concat";
  //private static final String FUNC_FORMAT_NUMBER_STRING = "format-number";
  private static final String FUNC_SYSTEM_PROPERTY_STRING = "system-property";
  private static final String FUNC_EXT_FUNCTION_AVAILABLE_STRING = "function-available";
  private static final String FUNC_EXT_ELEM_AVAILABLE_STRING = "element-available";
  private static final String FUNC_SUBSTRING_STRING = "substring";
  private static final String FUNC_STRING_LENGTH_STRING = "string-length";
  private static final String FUNC_UNPARSED_ENTITY_URI_STRING = "unparsed-entity-uri";

  // Proprietary, built in functions
  private static final String FUNC_DOCLOCATION_STRING = "document-location";

  static
  {
    m_axisnames.put(new StringKey(FROM_ANCESTORS_STRING), new Integer(XPath.FROM_ANCESTORS));
    m_axisnames.put(new StringKey(FROM_ANCESTORS_OR_SELF_STRING), new Integer(XPath.FROM_ANCESTORS_OR_SELF));
    m_axisnames.put(new StringKey(FROM_ATTRIBUTES_STRING), new Integer(XPath.FROM_ATTRIBUTES));
    m_axisnames.put(new StringKey(FROM_CHILDREN_STRING), new Integer(XPath.FROM_CHILDREN));
    m_axisnames.put(new StringKey(FROM_DESCENDANTS_STRING), new Integer(XPath.FROM_DESCENDANTS));
    m_axisnames.put(new StringKey(FROM_DESCENDANTS_OR_SELF_STRING), new Integer(XPath.FROM_DESCENDANTS_OR_SELF));
    m_axisnames.put(new StringKey(FROM_FOLLOWING_STRING), new Integer(XPath.FROM_FOLLOWING));
    m_axisnames.put(new StringKey(FROM_FOLLOWING_SIBLINGS_STRING), new Integer(XPath.FROM_FOLLOWING_SIBLINGS));
    m_axisnames.put(new StringKey(FROM_PARENT_STRING), new Integer(XPath.FROM_PARENT));
    m_axisnames.put(new StringKey(FROM_PRECEDING_STRING), new Integer(XPath.FROM_PRECEDING));
    m_axisnames.put(new StringKey(FROM_PRECEDING_SIBLINGS_STRING), new Integer(XPath.FROM_PRECEDING_SIBLINGS));
    m_axisnames.put(new StringKey(FROM_SELF_STRING), new Integer(XPath.FROM_SELF));
    m_axisnames.put(new StringKey(FROM_NAMESPACE_STRING), new Integer(XPath.FROM_NAMESPACE));

    m_nodetypes.put(new StringKey(NODETYPE_COMMENT_STRING), new Integer(XPath.NODETYPE_COMMENT));
    m_nodetypes.put(new StringKey(NODETYPE_TEXT_STRING), new Integer(XPath.NODETYPE_TEXT));
    m_nodetypes.put(new StringKey(NODETYPE_PI_STRING), new Integer(XPath.NODETYPE_PI));
    m_nodetypes.put(new StringKey(NODETYPE_NODE_STRING), new Integer(XPath.NODETYPE_NODE));
    m_nodetypes.put(new StringKey(NODETYPE_ANYELEMENT_STRING), new Integer(XPath.NODETYPE_ANYELEMENT));

    m_keywords.put(new StringKey(FROM_SELF_ABBREVIATED_STRING), new Integer(XPath.FROM_SELF));
    // m_keywords.put(new StringKey(FROM_ATTRIBUTE_STRING), new Integer(XPath.FROM_ATTRIBUTE));
    // m_keywords.put(new StringKey(FROM_DOC_STRING), new Integer(XPath.FROM_DOC));
    // m_keywords.put(new StringKey(FROM_DOCREF_STRING), new Integer(XPath.FROM_DOCREF));
    // m_keywords.put(new StringKey(FROM_ID_STRING), new Integer(XPath.FROM_ID));
    // m_keywords.put(new StringKey(FROM_IDREF_STRING), new Integer(XPath.FROM_IDREF));

    m_keywords.put(new StringKey(FUNC_ID_STRING), new Integer(XPath.FUNC_ID));
    m_keywords.put(new StringKey(FUNC_KEY_STRING), new Integer(XPath.FUNC_KEY));
    // m_keywords.put(new StringKey(FUNC_DOCUMENT_STRING), new Integer(XPath.FUNC_DOC));

    m_functions.put(new StringKey(FUNC_CURRENT_STRING), new Integer(XPath.FUNC_CURRENT));
    m_functions.put(new StringKey(FUNC_LAST_STRING), new Integer(XPath.FUNC_LAST));
    m_functions.put(new StringKey(FUNC_POSITION_STRING), new Integer(XPath.FUNC_POSITION));
    m_functions.put(new StringKey(FUNC_COUNT_STRING), new Integer(XPath.FUNC_COUNT));
    m_functions.put(new StringKey(FUNC_ID_STRING), new Integer(XPath.FUNC_ID));
    m_functions.put(new StringKey(FUNC_KEY_STRING), new Integer(XPath.FUNC_KEY));
    // m_functions.put(new StringKey(FUNC_DOCUMENT_STRING), new Integer(XPath.FUNC_DOC));
    m_functions.put(new StringKey(FUNC_LOCAL_PART_STRING), new Integer(XPath.FUNC_LOCAL_PART));
    m_functions.put(new StringKey(FUNC_NAMESPACE_STRING), new Integer(XPath.FUNC_NAMESPACE));
    m_functions.put(new StringKey(FUNC_NAME_STRING), new Integer(XPath.FUNC_QNAME));
    m_functions.put(new StringKey(FUNC_GENERATE_ID_STRING), new Integer(XPath.FUNC_GENERATE_ID));
    m_functions.put(new StringKey(FUNC_NOT_STRING), new Integer(XPath.FUNC_NOT));
    m_functions.put(new StringKey(FUNC_TRUE_STRING), new Integer(XPath.FUNC_TRUE));
    m_functions.put(new StringKey(FUNC_FALSE_STRING), new Integer(XPath.FUNC_FALSE));
    m_functions.put(new StringKey(FUNC_BOOLEAN_STRING), new Integer(XPath.FUNC_BOOLEAN));
    m_functions.put(new StringKey(FUNC_LANG_STRING), new Integer(XPath.FUNC_LANG));
    m_functions.put(new StringKey(FUNC_NUMBER_STRING), new Integer(XPath.FUNC_NUMBER));
    m_functions.put(new StringKey(FUNC_FLOOR_STRING), new Integer(XPath.FUNC_FLOOR));
    m_functions.put(new StringKey(FUNC_CEILING_STRING), new Integer(XPath.FUNC_CEILING));
    m_functions.put(new StringKey(FUNC_ROUND_STRING), new Integer(XPath.FUNC_ROUND));
    m_functions.put(new StringKey(FUNC_SUM_STRING), new Integer(XPath.FUNC_SUM));
    m_functions.put(new StringKey(FUNC_STRING_STRING), new Integer(XPath.FUNC_STRING));
    m_functions.put(new StringKey(FUNC_STARTS_WITH_STRING), new Integer(XPath.FUNC_STARTS_WITH));
    m_functions.put(new StringKey(FUNC_CONTAINS_STRING), new Integer(XPath.FUNC_CONTAINS));
    m_functions.put(new StringKey(FUNC_SUBSTRING_BEFORE_STRING), new Integer(XPath.FUNC_SUBSTRING_BEFORE));
    m_functions.put(new StringKey(FUNC_SUBSTRING_AFTER_STRING), new Integer(XPath.FUNC_SUBSTRING_AFTER));
    m_functions.put(new StringKey(FUNC_NORMALIZE_SPACE_STRING), new Integer(XPath.FUNC_NORMALIZE_SPACE));
    m_functions.put(new StringKey(FUNC_TRANSLATE_STRING), new Integer(XPath.FUNC_TRANSLATE));
    m_functions.put(new StringKey(FUNC_CONCAT_STRING), new Integer(XPath.FUNC_CONCAT));
    //m_functions.put(new StringKey(FUNC_FORMAT_NUMBER_STRING), new Integer(XPath.FUNC_FORMAT_NUMBER));
    m_functions.put(new StringKey(FUNC_SYSTEM_PROPERTY_STRING), new Integer(XPath.FUNC_SYSTEM_PROPERTY));
    m_functions.put(new StringKey(FUNC_EXT_FUNCTION_AVAILABLE_STRING), new Integer(XPath.FUNC_EXT_FUNCTION_AVAILABLE));
    m_functions.put(new StringKey(FUNC_EXT_ELEM_AVAILABLE_STRING), new Integer(XPath.FUNC_EXT_ELEM_AVAILABLE));
    m_functions.put(new StringKey(FUNC_SUBSTRING_STRING), new Integer(XPath.FUNC_SUBSTRING));
    m_functions.put(new StringKey(FUNC_STRING_LENGTH_STRING), new Integer(XPath.FUNC_STRING_LENGTH));
    m_functions.put(new StringKey(FUNC_UNPARSED_ENTITY_URI_STRING), new Integer(XPath.FUNC_UNPARSED_ENTITY_URI));

    // These aren't really functions.
    m_functions.put(new StringKey(NODETYPE_COMMENT_STRING), new Integer(XPath.NODETYPE_COMMENT));
    m_functions.put(new StringKey(NODETYPE_TEXT_STRING), new Integer(XPath.NODETYPE_TEXT));
    m_functions.put(new StringKey(NODETYPE_PI_STRING), new Integer(XPath.NODETYPE_PI));
    m_functions.put(new StringKey(NODETYPE_NODE_STRING), new Integer(XPath.NODETYPE_NODE));

    m_functions.put(new StringKey(FUNC_DOCLOCATION_STRING), new Integer(XPath.FUNC_DOCLOCATION));
  }

  /**
   * Given a string, return the corresponding keyword token.
   */
  final int getKeywordToken(String key)
  {
    int tok;
    try
    {
      Integer itok = (Integer)m_keywords.get(key);
      tok = (null != itok) ? itok.intValue() : 0;
    }
    catch(NullPointerException npe)
    {
      tok = 0;
    }
    catch(ClassCastException cce)
    {
      tok = 0;
    }
    return tok;
  }

  /**
   * Given a string, return the corresponding function token.
   */
  final int getFunctionToken(String key)
  {
    int tok;
    try
    {
      tok = ((Integer)(m_functions.get(key))).intValue();
    }
    catch(NullPointerException npe)
    {
      tok = -1;
    }
    catch(ClassCastException cce)
    {
      tok = -1;
    }
    return tok;
  }

  private void ___________PARSER___________(){}

  /**
   * Insert room for operation.  This will NOT set
   * the length value of the operation, but will update
   * the length value for the total expression.
   */
  void insertOp(int pos, int length, int op)
  {
    int totalLen = m_xpath.m_opMap[XPath.MAPINDEX_LENGTH];
    for(int i = totalLen - 1; i >= pos; i--)
    {
      m_xpath.m_opMap[i+length] = m_xpath.m_opMap[i];
    }
    m_xpath.m_opMap[pos] = op;
    m_xpath.m_opMap[XPath.MAPINDEX_LENGTH] = totalLen + length;
  }

  /**
   * Insert room for operation.  This WILL set
   * the length value of the operation, and will update
   * the length value for the total expression.
   */
  void appendOp(int length, int op)
  {
    int totalLen = m_xpath.m_opMap[XPath.MAPINDEX_LENGTH];
    m_xpath.m_opMap[totalLen] = op;
    m_xpath.m_opMap[totalLen+XPath.MAPINDEX_LENGTH] = length;
    m_xpath.m_opMap[XPath.MAPINDEX_LENGTH] = totalLen + length;
  }

  // ============= EXPRESSIONS FUNCTIONS =================
  private void ____EXPRESSIONS____(){}

  /**
   *
   *
   * Expr  ::=  OrExpr
   *
   */
  protected void Expr()
    throws org.xml.sax.SAXException
  {
    OrExpr();
  }

  /**
   *
   *
   * OrExpr  ::=  AndExpr
   * | OrExpr 'or' AndExpr
   *
   */
  protected void OrExpr()
    throws org.xml.sax.SAXException
  {
    int opPos = m_xpath.m_opMap[XPath.MAPINDEX_LENGTH];
    AndExpr();
    if((null != m_token) && tokenIs("or"))
    {
      nextToken();
      insertOp(opPos, 2, XPath.OP_OR);
      OrExpr();
      m_xpath.m_opMap[opPos + XPath.MAPINDEX_LENGTH]
        = m_xpath.m_opMap[XPath.MAPINDEX_LENGTH] - opPos;
    }
  }

  /**
   *
   *
   * AndExpr  ::=  EqualityExpr
   * | AndExpr 'and' EqualityExpr
   *
   */
  protected void AndExpr()
    throws org.xml.sax.SAXException
  {
    int opPos = m_xpath.m_opMap[XPath.MAPINDEX_LENGTH];
    EqualityExpr(-1);
    if((null != m_token) && tokenIs("and"))
    {
      nextToken();
      insertOp(opPos, 2, XPath.OP_AND);
      AndExpr();
      m_xpath.m_opMap[opPos + XPath.MAPINDEX_LENGTH] = m_xpath.m_opMap[XPath.MAPINDEX_LENGTH] - opPos;
    }
  }

  /**
   *
   * @returns an Object which is either a String, a Number, a Boolean, or a vector
   * of nodes.
   *
   * EqualityExpr  ::=  RelationalExpr
   * | EqualityExpr '=' RelationalExpr
   *
   */
  protected int EqualityExpr(int addPos)
    throws org.xml.sax.SAXException
  {
    int opPos = m_xpath.m_opMap[XPath.MAPINDEX_LENGTH];
    if(-1 == addPos)
      addPos = opPos;
    RelationalExpr(-1);
    if(null != m_token)
    {
      if(tokenIs('!') && lookahead('=', 1))
      {
        nextToken();
        nextToken();
        insertOp(addPos, 2, XPath.OP_NOTEQUALS);
        int opPlusLeftHandLen = m_xpath.m_opMap[XPath.MAPINDEX_LENGTH] - addPos;
        addPos = EqualityExpr(addPos);
        m_xpath.m_opMap[addPos + XPath.MAPINDEX_LENGTH]
          = m_xpath.m_opMap[addPos+opPlusLeftHandLen+1] + opPlusLeftHandLen;
        addPos+=2;
      }
      else if(tokenIs('='))
      {
        nextToken();
        insertOp(addPos, 2, XPath.OP_EQUALS);
        int opPlusLeftHandLen = m_xpath.m_opMap[XPath.MAPINDEX_LENGTH] - addPos;
        addPos = EqualityExpr(addPos);
        m_xpath.m_opMap[addPos + XPath.MAPINDEX_LENGTH]
          = m_xpath.m_opMap[addPos+opPlusLeftHandLen+1] + opPlusLeftHandLen;
        addPos+=2;
      }
    }
    return addPos;
  }

  /**
   * .
   * @returns an Object which is either a String, a Number, a Boolean, or a vector
   * of nodes.
   *
   * RelationalExpr  ::=  AdditiveExpr
   * | RelationalExpr '<' AdditiveExpr
   * | RelationalExpr '>' AdditiveExpr
   * | RelationalExpr '<=' AdditiveExpr
   * | RelationalExpr '>=' AdditiveExpr
   *
   */
  protected int RelationalExpr(int addPos)
    throws org.xml.sax.SAXException
  {
    int opPos = m_xpath.m_opMap[XPath.MAPINDEX_LENGTH];
    if(-1 == addPos)
      addPos = opPos;
    AdditiveExpr(-1);
    if(null != m_token)
    {
      if(tokenIs('<'))
      {
        nextToken();
        if(tokenIs('='))
        {
          nextToken();
          insertOp(addPos, 2, XPath.OP_LTE);
        }
        else
        {
          insertOp(addPos, 2, XPath.OP_LT);
        }
        int opPlusLeftHandLen = m_xpath.m_opMap[XPath.MAPINDEX_LENGTH] - addPos;
        addPos = RelationalExpr(addPos);
        m_xpath.m_opMap[addPos + XPath.MAPINDEX_LENGTH]
          = m_xpath.m_opMap[addPos+opPlusLeftHandLen+1] + opPlusLeftHandLen;
        addPos+=2;
      }
      else if(tokenIs('>'))
      {
        nextToken();
        if(tokenIs('='))
        {
          nextToken();
          insertOp(addPos, 2, XPath.OP_GTE);
        }
        else
        {
          insertOp(addPos, 2, XPath.OP_GT);
        }
        int opPlusLeftHandLen = m_xpath.m_opMap[XPath.MAPINDEX_LENGTH] - addPos;
        addPos = RelationalExpr(addPos);
        m_xpath.m_opMap[addPos + XPath.MAPINDEX_LENGTH]
          = m_xpath.m_opMap[addPos+opPlusLeftHandLen+1] + opPlusLeftHandLen;
        addPos+=2;
      }
    }
    return addPos;
  }

  /**
   * XXXX.
   * @returns an Object which is either a String, a Number, a Boolean, or a vector
   * of nodes.
   * This has to handle construction of the operations so that they are evaluated
   * in pre-fix order.  So, for 9+7-6, instead of |+|9|-|7|6|, this needs to be
   * evaluated as |-|+|9|7|6|.
   * @param addPos The position where the op should be inserted.
   *
   * AdditiveExpr  ::=  MultiplicativeExpr
   * | AdditiveExpr '+' MultiplicativeExpr
   * | AdditiveExpr '-' MultiplicativeExpr
   *
   */
  protected int AdditiveExpr(int addPos)
    throws org.xml.sax.SAXException
  {
    int opPos = m_xpath.m_opMap[XPath.MAPINDEX_LENGTH];
    if(-1 == addPos)
      addPos = opPos;
    MultiplicativeExpr(-1);
    if(null != m_token)
    {
      if(tokenIs('+'))
      {
        nextToken();
        insertOp(addPos, 2, XPath.OP_PLUS);
        int opPlusLeftHandLen = m_xpath.m_opMap[XPath.MAPINDEX_LENGTH] - addPos;
        addPos = AdditiveExpr(addPos);
        m_xpath.m_opMap[addPos + XPath.MAPINDEX_LENGTH]
          = m_xpath.m_opMap[addPos+opPlusLeftHandLen+1] + opPlusLeftHandLen;
        addPos+=2;
      }
      else if(tokenIs('-'))
      {
        nextToken();
        insertOp(addPos, 2, XPath.OP_MINUS);
        int opPlusLeftHandLen = m_xpath.m_opMap[XPath.MAPINDEX_LENGTH] - addPos;
        addPos = AdditiveExpr(addPos);
        m_xpath.m_opMap[addPos + XPath.MAPINDEX_LENGTH]
          = m_xpath.m_opMap[addPos+opPlusLeftHandLen+1] + opPlusLeftHandLen;
        addPos+=2;
      }
    }
    return addPos;
  }

  /**
   * XXXX.
   * @returns an Object which is either a String, a Number, a Boolean, or a vector
   * of nodes.
   * This has to handle construction of the operations so that they are evaluated
   * in pre-fix order.  So, for 9+7-6, instead of |+|9|-|7|6|, this needs to be
   * evaluated as |-|+|9|7|6|.
   * @param addPos The position where the op should be inserted.
   *
   * MultiplicativeExpr  ::=  UnaryExpr
   * | MultiplicativeExpr MultiplyOperator UnaryExpr
   * | MultiplicativeExpr 'div' UnaryExpr
   * | MultiplicativeExpr 'mod' UnaryExpr
   * | MultiplicativeExpr 'quo' UnaryExpr
   *
   */
  protected int MultiplicativeExpr(int addPos)
    throws org.xml.sax.SAXException
  {
    int opPos = m_xpath.m_opMap[XPath.MAPINDEX_LENGTH];
    if(-1 == addPos)
      addPos = opPos;
    UnaryExpr();
    if(null != m_token)
    {
      if(tokenIs('*'))
      {
        nextToken();
        insertOp(opPos, 2, XPath.OP_MULT);
        int opPlusLeftHandLen = m_xpath.m_opMap[XPath.MAPINDEX_LENGTH] - addPos;
        addPos = MultiplicativeExpr(addPos);
        m_xpath.m_opMap[addPos + XPath.MAPINDEX_LENGTH]
          = m_xpath.m_opMap[addPos+opPlusLeftHandLen+1] + opPlusLeftHandLen;
        addPos+=2;
      }
      else if(tokenIs("div"))
      {
        nextToken();
        insertOp(opPos, 2, XPath.OP_DIV);
        int opPlusLeftHandLen = m_xpath.m_opMap[XPath.MAPINDEX_LENGTH] - addPos;
        addPos = MultiplicativeExpr(addPos);
        m_xpath.m_opMap[addPos + XPath.MAPINDEX_LENGTH]
          = m_xpath.m_opMap[addPos+opPlusLeftHandLen+1] + opPlusLeftHandLen;
        addPos+=2;
      }
      else if(tokenIs("mod"))
      {
        nextToken();
        insertOp(opPos, 2, XPath.OP_MOD);
        int opPlusLeftHandLen = m_xpath.m_opMap[XPath.MAPINDEX_LENGTH] - addPos;
        addPos = MultiplicativeExpr(addPos);
        m_xpath.m_opMap[addPos + XPath.MAPINDEX_LENGTH]
          = m_xpath.m_opMap[addPos+opPlusLeftHandLen+1] + opPlusLeftHandLen;
        addPos+=2;
      }
      else if(tokenIs("quo"))
      {
        nextToken();
        insertOp(opPos, 2, XPath.OP_QUO);
        int opPlusLeftHandLen = m_xpath.m_opMap[XPath.MAPINDEX_LENGTH] - addPos;
        addPos = MultiplicativeExpr(addPos);
        m_xpath.m_opMap[addPos + XPath.MAPINDEX_LENGTH]
          = m_xpath.m_opMap[addPos+opPlusLeftHandLen+1] + opPlusLeftHandLen;
        addPos+=2;
      }
    }
    return addPos;
  }

  /**
   * XXXX.
   * @returns an Object which is either a String, a Number, a Boolean, or a vector
   * of nodes.
   *
   * UnaryExpr  ::=  UnionExpr
   * | '-' UnaryExpr
   *
   */
  protected void UnaryExpr()
    throws org.xml.sax.SAXException
  {
    int opPos = m_xpath.m_opMap[XPath.MAPINDEX_LENGTH];
    boolean isNeg = false;
    if(m_tokenChar == '-')
    {
      nextToken();
      appendOp(2, XPath.OP_NEG);
      isNeg = true;
    }
    UnionExpr();
    if(isNeg)
      m_xpath.m_opMap[opPos + XPath.MAPINDEX_LENGTH] = m_xpath.m_opMap[XPath.MAPINDEX_LENGTH] - opPos;
  }

  /**
   *
   * StringExpr  ::=  Expr
   *
   */
  protected void StringExpr()
    throws org.xml.sax.SAXException
  {
    int opPos = m_xpath.m_opMap[XPath.MAPINDEX_LENGTH];
    appendOp(2, XPath.OP_STRING);
    Expr();
    m_xpath.m_opMap[opPos + XPath.MAPINDEX_LENGTH] = m_xpath.m_opMap[XPath.MAPINDEX_LENGTH] - opPos;
  }

  /**
   *
   *
   * StringExpr  ::=  Expr
   *
   */
  protected void BooleanExpr()
    throws org.xml.sax.SAXException
  {
    int opPos = m_xpath.m_opMap[XPath.MAPINDEX_LENGTH];
    appendOp(2, XPath.OP_BOOL);
    Expr();
    int opLen = m_xpath.m_opMap[XPath.MAPINDEX_LENGTH] - opPos;
    if(opLen == 2)
    {
      error(XPATHErrorResources.ER_BOOLEAN_ARG_NO_LONGER_OPTIONAL); //"boolean(...) argument is no longer optional with 19990709 XPath draft.");
    }
    m_xpath.m_opMap[opPos + XPath.MAPINDEX_LENGTH] = opLen;
  }

  /**
   *
   *
   * NumberExpr  ::=  Expr
   *
   */
  protected void NumberExpr()
    throws org.xml.sax.SAXException
  {
    int opPos = m_xpath.m_opMap[XPath.MAPINDEX_LENGTH];
    appendOp(2, XPath.OP_NUMBER);
    Expr();
    m_xpath.m_opMap[opPos + XPath.MAPINDEX_LENGTH] = m_xpath.m_opMap[XPath.MAPINDEX_LENGTH] - opPos;
  }

  /**
   * The context of the right hand side expressions is the context of the
   * left hand side expression. The results of the right hand side expressions
   * are node sets. The result of the left hand side UnionExpr is the union
   * of the results of the right hand side expressions.
   *
   *
   * UnionExpr    ::=    PathExpr
   * | UnionExpr '|' PathExpr
   *
   */
  protected void UnionExpr()
    throws org.xml.sax.SAXException
  {
    int opPos = m_xpath.m_opMap[XPath.MAPINDEX_LENGTH];
    boolean continueOrLoop = true;
    boolean foundUnion = false;
    do
    {
      PathExpr();

      if(tokenIs('|'))
      {
        if(false == foundUnion)
        {
          foundUnion = true;
          insertOp(opPos, 2, XPath.OP_UNION);
        }
        nextToken();
      }
      else
      {
        break;
      }

      // this.m_testForDocOrder = true;
    }
      while(continueOrLoop);
    m_xpath.m_opMap[opPos + XPath.MAPINDEX_LENGTH] = m_xpath.m_opMap[XPath.MAPINDEX_LENGTH] - opPos;
  }

  /**
   * Analyze a union pattern and tell if the axes are
   * all descendants.
   * (Move to XPath?)
   */
  private static boolean isLocationPathSimpleFollowing(XPath xpath, int opPos)
  {
    if(true)
    {
      int posOfLastOp = xpath.getNextOpPos(opPos)-1;

      opPos = xpath.getFirstChildPos(opPos);
      // step
      int stepType = xpath.m_opMap[opPos];

      // make sure all step types are going forwards
      switch(stepType)
      {
      case XPath.FROM_SELF:
      case XPath.FROM_ATTRIBUTES:
      case XPath.FROM_CHILDREN:
      case XPath.FROM_DESCENDANTS:
      case XPath.FROM_DESCENDANTS_OR_SELF:
      case XPath.FROM_FOLLOWING:
      case XPath.FROM_FOLLOWING_SIBLINGS:
        if(xpath.m_opMap[xpath.getNextOpPos(opPos)] == xpath.ENDOP)
        {
          // Add the length of the step itself, plus the length of the op,
          // and two length arguments, to the op position.
          opPos = (xpath.getArgLengthOfStep(opPos)+xpath.getFirstChildPosOfStep(opPos));
          int nextStepType = xpath.m_opMap[opPos];

          if(xpath.OP_PREDICATE == nextStepType)
          {
            int firstPredPos = opPos+2;
            int predicateType = xpath.m_opMap[firstPredPos];
            if((XPath.OP_NUMBERLIT == predicateType) || (XPath.OP_NUMBER == predicateType)
               || (XPath.FUNC_NUMBER == predicateType))
            {
              return false;
            }
            opPos = xpath.getNextOpPos(opPos);
            nextStepType = xpath.m_opMap[opPos];
            // Multiple predicates?
            if(xpath.OP_PREDICATE == nextStepType)
              return false;
          }
          return true;
        }
        break;
      }
      return false;
    }
    else
    {
      return false;
    }
  }


  /**
   *
   *
   * PathExpr  ::=  LocationPath
   * | FilterExpr
   * | FilterExpr '/' RelativeLocationPath
   * | FilterExpr '//' RelativeLocationPath
   *
   * @exception XSLProcessorException thrown if the active ProblemListener and XMLParserLiaison decide
   * the error condition is severe enough to halt processing.
   */
  protected void PathExpr()
    throws org.xml.sax.SAXException
  {
    int opPos = m_xpath.m_opMap[XPath.MAPINDEX_LENGTH];
    boolean foundLocationPath;

    FilterExpr();

    if(tokenIs('/'))
    { 
      nextToken();
      int locationPathOpPos = opPos;
      insertOp(opPos, 2, XPath.OP_LOCATIONPATH);
      RelativeLocationPath();

      // Terminate for safety.
      m_xpath.m_opMap[m_xpath.m_opMap[XPath.MAPINDEX_LENGTH]] = XPath.ENDOP;
      m_xpath.m_opMap[XPath.MAPINDEX_LENGTH] += 1;

      m_xpath.m_opMap[opPos + XPath.MAPINDEX_LENGTH] = m_xpath.m_opMap[XPath.MAPINDEX_LENGTH] - opPos;
      if(isLocationPathSimpleFollowing(m_xpath, locationPathOpPos))
      {
        m_xpath.m_opMap[locationPathOpPos] = XPath.OP_LOCATIONPATH_EX;
      }
    }
  }

  /**
   *
   *
   * FilterExpr  ::=  PrimaryExpr
   * | FilterExpr Predicate
   *
   * @exception XSLProcessorException thrown if the active ProblemListener and XMLParserLiaison decide
   * the error condition is severe enough to halt processing.
   */
  protected void FilterExpr()
    throws org.xml.sax.SAXException
  {
    int opPos = m_xpath.m_opMap[XPath.MAPINDEX_LENGTH];
    // boolean isFunc = lookahead('(', 1);
    PrimaryExpr();

    if(tokenIs('['))
    {
      int locationPathOpPos = opPos;
      insertOp(opPos, 2, XPath.OP_LOCATIONPATH);

      while(tokenIs('['))
      {
        Predicate();
      }

      if(tokenIs('/'))
      {
        nextToken();
        RelativeLocationPath();
      }

      // Terminate for safety.
      m_xpath.m_opMap[m_xpath.m_opMap[XPath.MAPINDEX_LENGTH]] = XPath.ENDOP;
      m_xpath.m_opMap[XPath.MAPINDEX_LENGTH] += 1;

      m_xpath.m_opMap[opPos + XPath.MAPINDEX_LENGTH] = m_xpath.m_opMap[XPath.MAPINDEX_LENGTH] - opPos;
      if(isLocationPathSimpleFollowing(m_xpath, locationPathOpPos))
      {
        m_xpath.m_opMap[locationPathOpPos] = XPath.OP_LOCATIONPATH_EX;
      }
    }

    /*
     * if(tokenIs('['))
     * {
     *   Predicate();
     *   m_xpath.m_opMap[opPos + XPath.MAPINDEX_LENGTH] = m_xpath.m_opMap[XPath.MAPINDEX_LENGTH] - opPos;
     * }
     */
  }

  /**
   *
   * PrimaryExpr  ::=  VariableReference
   * | '(' Expr ')'
   * | Literal
   * | Number
   * | FunctionCall
   *
   */
  protected void PrimaryExpr()
    throws org.xml.sax.SAXException
  {
    int opPos = m_xpath.m_opMap[XPath.MAPINDEX_LENGTH];
    if((m_tokenChar == '\'') || (m_tokenChar == '"'))
    {
      appendOp(2, XPath.OP_LITERAL);
      Literal();
      m_xpath.m_opMap[opPos + XPath.MAPINDEX_LENGTH] = m_xpath.m_opMap[XPath.MAPINDEX_LENGTH] - opPos;
    }
    else if(m_tokenChar == '$')
    {
      nextToken(); // consume '$'
      appendOp(2, XPath.OP_VARIABLE);

      NCName();
      m_xpath.m_opMap[opPos + XPath.MAPINDEX_LENGTH] = m_xpath.m_opMap[XPath.MAPINDEX_LENGTH] - opPos;
    }
    else if(m_tokenChar == '(')
    {
      nextToken();
      appendOp(2, XPath.OP_GROUP);
      Expr();
      consumeExpected(')');
      m_xpath.m_opMap[opPos + XPath.MAPINDEX_LENGTH] = m_xpath.m_opMap[XPath.MAPINDEX_LENGTH] - opPos;
    }
    else if((null != m_token) &&
            ((('.' == m_tokenChar) && (m_token.length() > 1) &&
              Character.isDigit( m_token.charAt(1) ))
             || Character.isDigit( m_tokenChar )))
    {
      appendOp(2, XPath.OP_NUMBERLIT);
      Number();
      m_xpath.m_opMap[opPos + XPath.MAPINDEX_LENGTH] = m_xpath.m_opMap[XPath.MAPINDEX_LENGTH] - opPos;
    }
    else if(lookahead('(', 1) || (lookahead(':', 1) && lookahead('(', 3)))
    {
      FunctionCall();
    }
    else
    {
      LocationPath();
    }
  }

  /**
   *
   * Argument    ::=    Expr
   *
   */
  protected void Argument()
    throws org.xml.sax.SAXException
  {
    int opPos = m_xpath.m_opMap[XPath.MAPINDEX_LENGTH];
    appendOp(2, XPath.OP_ARGUMENT);
    Expr();
    m_xpath.m_opMap[opPos + XPath.MAPINDEX_LENGTH] = m_xpath.m_opMap[XPath.MAPINDEX_LENGTH] - opPos;
  }

  /**
   *
   * FunctionCall    ::=    FunctionName '(' ( Argument ( ',' Argument)*)? ')'
   *
   */
  protected void FunctionCall()
    throws org.xml.sax.SAXException
  {
    int opPos = m_xpath.m_opMap[XPath.MAPINDEX_LENGTH];
    if(lookahead(':',1))
    {
      appendOp(4, XPath.OP_EXTFUNCTION);
      m_xpath.m_opMap[opPos + XPath.MAPINDEX_LENGTH + 1] = m_queueMark-1;
      nextToken();
      consumeExpected(':');
      m_xpath.m_opMap[opPos + XPath.MAPINDEX_LENGTH + 2] = m_queueMark-1;
      nextToken();
    }
    else
    {
      int funcTok = getFunctionToken(m_token);
      if(-1 == funcTok)
      {
		  error(XPATHErrorResources.ER_COULDNOT_FIND_FUNCTION, new Object[] {m_token}); //"Could not find function: "+m_token+"()");
      }
      switch(funcTok)
      {
      case XPath.NODETYPE_PI:
      case XPath.NODETYPE_COMMENT:
      case XPath.NODETYPE_TEXT:
      case XPath.NODETYPE_NODE:
        LocationPath();
        return;
      default:
        appendOp(3, XPath.OP_FUNCTION);
        m_xpath.m_opMap[opPos + XPath.MAPINDEX_LENGTH + 1] = funcTok;
      }
      nextToken();
    }
    consumeExpected('(');
    while(!tokenIs(')'))
    {
      if(tokenIs(','))
      {
        error(XPATHErrorResources.ER_FOUND_COMMA_BUT_NO_PRECEDING_ARG); //"Found ',' but no preceding argument!");
      }
      Argument();
      if(!tokenIs(')'))
      {
        consumeExpected(',');
        if(tokenIs(')'))
        {
          error(XPATHErrorResources.ER_FOUND_COMMA_BUT_NO_FOLLOWING_ARG); //"Found ',' but no following argument!");
        }
      }
    }
    consumeExpected(')');

    // Terminate for safety.
    m_xpath.m_opMap[m_xpath.m_opMap[XPath.MAPINDEX_LENGTH]] = XPath.ENDOP;
    m_xpath.m_opMap[XPath.MAPINDEX_LENGTH] += 1;

    m_xpath.m_opMap[opPos + XPath.MAPINDEX_LENGTH] = m_xpath.m_opMap[XPath.MAPINDEX_LENGTH] - opPos;
  }

  // ============= GRAMMAR FUNCTIONS =================
  private void ____LOCATION_PATHS____(){}

  /**
   *
   * LocationPath ::= RelativeLocationPath
   * | AbsoluteLocationPath
   *
   */
  protected void LocationPath()
    throws org.xml.sax.SAXException
  {
    int opPos = m_xpath.m_opMap[XPath.MAPINDEX_LENGTH];
    int locationPathOpPos = opPos;
    appendOp(2, XPath.OP_LOCATIONPATH);

    if(tokenIs('/'))
    {
      appendOp(4, XPath.FROM_ROOT);
      // Tell how long the step is without the predicate
      m_xpath.m_opMap[m_xpath.m_opMap[XPath.MAPINDEX_LENGTH] - 2] = 4;
      m_xpath.m_opMap[m_xpath.m_opMap[XPath.MAPINDEX_LENGTH] - 1] = XPath.NODETYPE_ROOT;
      nextToken();
    }
    if(m_token != null)
    {
      RelativeLocationPath();
    }

    // Terminate for safety.
    m_xpath.m_opMap[m_xpath.m_opMap[XPath.MAPINDEX_LENGTH]] = XPath.ENDOP;
    m_xpath.m_opMap[XPath.MAPINDEX_LENGTH] += 1;

    m_xpath.m_opMap[opPos + XPath.MAPINDEX_LENGTH] = m_xpath.m_opMap[XPath.MAPINDEX_LENGTH] - opPos;
    if(isLocationPathSimpleFollowing(m_xpath, locationPathOpPos))
    {
      m_xpath.m_opMap[locationPathOpPos] = XPath.OP_LOCATIONPATH_EX;
    }
  }

  /**
   *
   * RelativeLocationPath ::= Step
   * | RelativeLocationPath '/' Step
   * | AbbreviatedRelativeLocationPath
   *
   */
  protected void RelativeLocationPath()
    throws org.xml.sax.SAXException
  {
    Step();
    while(tokenIs('/'))
    {
      nextToken();
      Step();
    }
  }


  /**
   *
   * Step    ::=    Basis Predicate*
   * | AbbreviatedStep
   */
  protected void Step()
    throws org.xml.sax.SAXException
  {
    int opPos = m_xpath.m_opMap[XPath.MAPINDEX_LENGTH];

    if(tokenIs("."))
    {
      nextToken();
      if(tokenIs('['))
      {
        error(XPATHErrorResources.ER_PREDICATE_ILLEGAL_SYNTAX); //"'..[predicate]' or '.[predicate]' is illegal syntax.  Use 'self::node()[predicate]' instead.");
      }
      appendOp(4, XPath.FROM_SELF);
      // Tell how long the step is without the predicate
      m_xpath.m_opMap[m_xpath.m_opMap[XPath.MAPINDEX_LENGTH] - 2] = 4;
      m_xpath.m_opMap[m_xpath.m_opMap[XPath.MAPINDEX_LENGTH] - 1] = XPath.NODETYPE_NODE;
    }
    else if(tokenIs(".."))
    {
      nextToken();
      appendOp(4, XPath.FROM_PARENT);
      // Tell how long the step is without the predicate
      m_xpath.m_opMap[m_xpath.m_opMap[XPath.MAPINDEX_LENGTH] - 2] = 4;
      m_xpath.m_opMap[m_xpath.m_opMap[XPath.MAPINDEX_LENGTH] - 1] = XPath.NODETYPE_NODE;
    }
    else
    {
      Basis();

      while(tokenIs('['))
      {
        Predicate();
      }

      // Tell how long the entire step is.
      m_xpath.m_opMap[opPos + XPath.MAPINDEX_LENGTH] = m_xpath.m_opMap[XPath.MAPINDEX_LENGTH] - opPos;
    }
  }

  /**
   *
   * Basis    ::=    AxisName '::' NodeTest
   * | AbbreviatedBasis
   */
  protected void Basis()
    throws org.xml.sax.SAXException
  {
    int opPos = m_xpath.m_opMap[XPath.MAPINDEX_LENGTH];
    int axesType;

    // The next blocks guarantee that a FROM_XXX will be added.
    if(lookahead("::", 1))
    {
      axesType = AxisName();
      nextToken();
      nextToken();
    }
    else if(tokenIs('@'))
    {
      axesType = XPath.FROM_ATTRIBUTES;
      appendOp(2, axesType);
      nextToken();
    }
    else if(tokenIs('/'))
    {
      axesType = XPath.FROM_DESCENDANTS_OR_SELF;
      appendOp(2, axesType);

      // Have to fix up for patterns such as '//@foo' or '//attribute::foo',
      // which translate to 'descendant-or-self::node()/attribute::foo'.
      // notice I leave the '/' on the queue, so the next will be processed
      // by a regular step pattern.
      // if(lookahead('@', 1) || lookahead("::", 2))
      {
        // Make room for telling how long the step is without the predicate
        m_xpath.m_opMap[XPath.MAPINDEX_LENGTH] += 1;

        m_xpath.m_opMap[m_xpath.m_opMap[XPath.MAPINDEX_LENGTH]] = XPath.NODETYPE_NODE;
        m_xpath.m_opMap[XPath.MAPINDEX_LENGTH] += 1;

        // Tell how long the step is without the predicate
        m_xpath.m_opMap[opPos + XPath.MAPINDEX_LENGTH + 1] = m_xpath.m_opMap[XPath.MAPINDEX_LENGTH] - opPos;

        return; // make a quick exit...
      }
      // else
      // {
      //  nextToken();
      // }
    }
    else
    {
      axesType = XPath.FROM_CHILDREN;
      appendOp(2, axesType);
    }

    // Make room for telling how long the step is without the predicate
    m_xpath.m_opMap[XPath.MAPINDEX_LENGTH] += 1;

    NodeTest(axesType);

    // Tell how long the step is without the predicate
    m_xpath.m_opMap[opPos + XPath.MAPINDEX_LENGTH + 1] = m_xpath.m_opMap[XPath.MAPINDEX_LENGTH] - opPos;
  }

  /**
   *
   * Basis    ::=    AxisName '::' NodeTest
   * | AbbreviatedBasis
   */
  protected int AxisName()
    throws org.xml.sax.SAXException
  {
    Object val = m_axisnames.get(m_token);
    if(null == val)
    {
      error(XPATHErrorResources.ER_ILLEGAL_AXIS_NAME, new Object[] {m_token}); //"illegal axis name: "+m_token);
    }
    int axesType = ((Integer)val).intValue();
    appendOp(2, axesType);
    return axesType;
  }


  /**
   *
   * NodeTest    ::=    WildcardName
   * | NodeType '(' ')'
   * | 'processing-instruction' '(' Literal ')'
   */
  protected void NodeTest(int axesType)
    throws org.xml.sax.SAXException
  {
    if(lookahead('(', 1))
    {
      Object nodeTestOp = m_nodetypes.get(m_token);
      if(null == nodeTestOp)
      {
        error(XPATHErrorResources.ER_UNKNOWN_NODETYPE, new Object[] {m_token}); //"Unknown nodetype: "+m_token);
      }
      else
      {
        nextToken();
        int nt = ((Integer)nodeTestOp).intValue();
        m_xpath.m_opMap[m_xpath.m_opMap[XPath.MAPINDEX_LENGTH]] = nt;
        m_xpath.m_opMap[XPath.MAPINDEX_LENGTH] += 1;

        consumeExpected('(');
        if(XPath.NODETYPE_PI == nt)
        {
          if(!tokenIs(')'))
          {
            Literal();
          }
        }
        consumeExpected(')');
      }
    }
    else
    {
      // Assume name of attribute or element.
        m_xpath.m_opMap[m_xpath.m_opMap[XPath.MAPINDEX_LENGTH]] = XPath.NODENAME;
        m_xpath.m_opMap[XPath.MAPINDEX_LENGTH] += 1;
        if(lookahead(':', 1))
        {
          if(tokenIs('*'))
          {
            m_xpath.m_opMap[m_xpath.m_opMap[XPath.MAPINDEX_LENGTH]]
              = XPath.ELEMWILDCARD;
          }
          else
          {
            m_xpath.m_opMap[m_xpath.m_opMap[XPath.MAPINDEX_LENGTH]] = m_queueMark-1;
          }
          nextToken();
          consumeExpected(':');
        }
        else
        {
          m_xpath.m_opMap[m_xpath.m_opMap[XPath.MAPINDEX_LENGTH]] = XPath.EMPTY;
        }
        m_xpath.m_opMap[XPath.MAPINDEX_LENGTH] += 1;

        if(tokenIs('*'))
        {
          m_xpath.m_opMap[m_xpath.m_opMap[XPath.MAPINDEX_LENGTH]] = XPath.ELEMWILDCARD;
        }
        else
        {
          if(XPath.FROM_NAMESPACE == axesType)
          {
            String prefix = (String)this.m_xpath.m_tokenQueue[m_queueMark-1];
            String namespace = ((PrefixResolver)m_namespaceContext).getNamespaceForPrefix(prefix);
            this.m_xpath.m_tokenQueue[m_queueMark-1] = namespace;
          }
          m_xpath.m_opMap[m_xpath.m_opMap[XPath.MAPINDEX_LENGTH]] = m_queueMark-1;
        }

        m_xpath.m_opMap[XPath.MAPINDEX_LENGTH] += 1;
        nextToken();
    }
  }

  /**
   *
   * Predicate ::= '[' PredicateExpr ']'
   *
   */
  protected void Predicate()
    throws org.xml.sax.SAXException
  {
    if(tokenIs('['))
    {
      nextToken();
      PredicateExpr();
      consumeExpected(']');
    }
  }

  /**
   *
   * PredicateExpr ::= Expr
   *
   */
  protected void PredicateExpr()
    throws org.xml.sax.SAXException
  {
    int opPos = m_xpath.m_opMap[XPath.MAPINDEX_LENGTH];
    appendOp(2, XPath.OP_PREDICATE);
    Expr();

    // Terminate for safety.
    m_xpath.m_opMap[m_xpath.m_opMap[XPath.MAPINDEX_LENGTH]] = XPath.ENDOP;
    m_xpath.m_opMap[XPath.MAPINDEX_LENGTH] += 1;

    m_xpath.m_opMap[opPos + XPath.MAPINDEX_LENGTH] = m_xpath.m_opMap[XPath.MAPINDEX_LENGTH] - opPos;
  }

  /**
   * QName ::=  (Prefix ':')? LocalPart
   * Prefix ::=  NCName
   * LocalPart ::=  NCName
   */
  protected void QName()
    throws org.xml.sax.SAXException
  {
    m_xpath.m_opMap[m_xpath.m_opMap[XPath.MAPINDEX_LENGTH]] = m_queueMark-1;
    m_xpath.m_opMap[XPath.MAPINDEX_LENGTH] += 1;
    nextToken();
    consumeExpected(':');
    m_xpath.m_opMap[m_xpath.m_opMap[XPath.MAPINDEX_LENGTH]] = m_queueMark-1;
    m_xpath.m_opMap[XPath.MAPINDEX_LENGTH] += 1;
    nextToken();
  }

  /**
   * NCName ::=  (Letter | '_') (NCNameChar)*
   * NCNameChar ::=  Letter | Digit | '.' | '-' | '_' | CombiningChar | Extender
   */
  protected void NCName()
  {
    m_xpath.m_opMap[m_xpath.m_opMap[XPath.MAPINDEX_LENGTH]] = m_queueMark-1;
    m_xpath.m_opMap[XPath.MAPINDEX_LENGTH] += 1;
    nextToken();
  }

  /**
   * The value of the Literal is the sequence of characters inside
   * the " or ' characters>.
   *
   * Literal  ::=  '"' [^"]* '"'
   * | "'" [^']* "'"
   *
   */
  protected void Literal()
    throws org.xml.sax.SAXException
  {
    int last = m_token.length()-1;
    char c0 = m_tokenChar;
    char cX = m_token.charAt(last);
    if(((c0 == '\"') && (cX == '\"')) ||
       ((c0 == '\'') && (cX == '\'')))
    {
      // Mutate the token to remove the quotes and have the XString object
      // already made.
      int tokenQueuePos = m_queueMark-1;
      m_xpath.m_tokenQueue[tokenQueuePos] = null;
      Object obj = new XString(m_token.substring(1, last));
      m_xpath.m_tokenQueue[tokenQueuePos] = obj;
      // lit = m_token.substring(1, last);
      m_xpath.m_opMap[m_xpath.m_opMap[XPath.MAPINDEX_LENGTH]] = tokenQueuePos;
      m_xpath.m_opMap[XPath.MAPINDEX_LENGTH] += 1;
      nextToken();
    }
    else
    {
      error(XPATHErrorResources.ER_PATTERN_LITERAL_NEEDS_BE_QUOTED, new Object[] {m_token}); //"Pattern literal ("+m_token+") needs to be quoted!");
    }
  }

  /**
   *
   * Number ::= [0-9]+('.'[0-9]+)? | '.'[0-9]+
   *
   */
  protected void Number()
    throws org.xml.sax.SAXException
  {
    if(null != m_token)
    {
      // Mutate the token to remove the quotes and have the XNumber object
      // already made.
      double num;
      try
      {
        num = Double.valueOf(m_token).doubleValue();
      }
      catch(NumberFormatException nfe)
      {
        num = 0.0; // to shut up compiler.
        error(XPATHErrorResources.ER_COULDNOT_BE_FORMATTED_TO_NUMBER, new Object[] {m_token}); //m_token+" could not be formatted to a number!");
      }
      m_xpath.m_tokenQueue[m_queueMark-1]
        = new XNumber(num);

      m_xpath.m_opMap[m_xpath.m_opMap[XPath.MAPINDEX_LENGTH]] = m_queueMark - 1;
      m_xpath.m_opMap[XPath.MAPINDEX_LENGTH] += 1;
      nextToken();
    }
  }

  // ============= PATTERN FUNCTIONS =================
  private void ____PATTERNS____(){}

  /**
   *
   * Pattern  ::=  LocationPathPattern
   * | Pattern '|' LocationPathPattern
   *
   */
  protected void Pattern()
    throws org.xml.sax.SAXException
  {
    while(true)
    {
      LocationPathPattern();

      if(tokenIs('|'))
      {
        nextToken();
      }
      else
      {
        break;
      }
    }
  }

  /**
   *
   *
   * LocationPathPattern  ::=  '/' RelativePathPattern?
   * | IdKeyPattern (('/' | '//') RelativePathPattern)?
   * | '//'? RelativePathPattern
   *
   */
  protected void LocationPathPattern()
    throws org.xml.sax.SAXException
  {
    int opPos = m_xpath.m_opMap[XPath.MAPINDEX_LENGTH];
    appendOp(2, XPath.OP_LOCATIONPATHPATTERN);

    if(lookahead('(', 1) && (tokenIs(FUNC_ID_STRING) || tokenIs(FUNC_KEY_STRING)))
    {
      IdKeyPattern();
      if(tokenIs('/') && lookahead('/', 1))
      {
        appendOp(4, XPath.MATCH_ANY_ANCESTOR);
        // Tell how long the step is without the predicate
        m_xpath.m_opMap[m_xpath.m_opMap[XPath.MAPINDEX_LENGTH] - 2] = 4;
        m_xpath.m_opMap[m_xpath.m_opMap[XPath.MAPINDEX_LENGTH] - 1] = XPath.NODETYPE_ROOT;
        nextToken();
      }
    }
    else if(tokenIs('/'))
    {
      if(lookahead('/', 1))
      {
        appendOp(4, XPath.MATCH_ANY_ANCESTOR);
      }
      else
      {
        appendOp(4, XPath.FROM_ROOT);
      }
      // Tell how long the step is without the predicate
      m_xpath.m_opMap[m_xpath.m_opMap[XPath.MAPINDEX_LENGTH] - 2] = 4;
      m_xpath.m_opMap[m_xpath.m_opMap[XPath.MAPINDEX_LENGTH] - 1] = XPath.NODETYPE_ROOT;
      nextToken();
    }

    if(!tokenIs('|') && (null != m_token))
    {
      RelativePathPattern();
    }

    // Terminate for safety.
    m_xpath.m_opMap[m_xpath.m_opMap[XPath.MAPINDEX_LENGTH]] = XPath.ENDOP;
    m_xpath.m_opMap[XPath.MAPINDEX_LENGTH] += 1;

    m_xpath.m_opMap[opPos + XPath.MAPINDEX_LENGTH] = m_xpath.m_opMap[XPath.MAPINDEX_LENGTH] - opPos;
  }

  /**
   *
   * IdKeyPattern  ::=  'id' '(' Literal ')'
   * | 'key' '(' Literal ',' Literal ')'
   * (Also handle doc())
   *
   */
  protected void IdKeyPattern()
    throws org.xml.sax.SAXException
  {
    FunctionCall();
  }

  /**
   *
   * RelativePathPattern  ::=  StepPattern
   * | RelativePathPattern '/' StepPattern
   * | RelativePathPattern '//' StepPattern
   *
   */
  protected void RelativePathPattern()
    throws org.xml.sax.SAXException
  {
    StepPattern();
    while(tokenIs('/'))
    {
      nextToken();
      StepPattern();
    }
  }

  /**
   *
   * StepPattern  ::=  AbbreviatedNodeTestStep
   *
   */
  protected void StepPattern()
    throws org.xml.sax.SAXException
  {
    AbbreviatedNodeTestStep(  );
  }

  /**
   *
   * AbbreviatedNodeTestStep    ::=    '@'? NodeTest Predicate*
   *
   */
  protected void AbbreviatedNodeTestStep()
    throws org.xml.sax.SAXException
  {
    int opPos = m_xpath.m_opMap[XPath.MAPINDEX_LENGTH];
    int axesType;

    // The next blocks guarantee that a MATCH_XXX will be added.
    int matchTypePos = -1;
    if(tokenIs('@'))
    {
      axesType = XPath.MATCH_ATTRIBUTE;
      appendOp(2, axesType);
      nextToken();
    }
    else if(this.lookahead("::", 1))
    {
      if(tokenIs("attribute"))
      {
        axesType = XPath.MATCH_ATTRIBUTE;
        appendOp(2, axesType);
      }
      else if(tokenIs("child"))
      {
        axesType = XPath.MATCH_IMMEDIATE_ANCESTOR;
        appendOp(2, axesType);
      }
      else
      {
        axesType = -1;
        this.error(XPATHErrorResources.ER_AXES_NOT_ALLOWED, new Object[] {this.m_token});
      }
      nextToken();
      nextToken();
    }
    else if(tokenIs('/'))
    {
      axesType = XPath.MATCH_ANY_ANCESTOR;
      appendOp(2, axesType);
      nextToken();
    }
    else
    {
      if(tokenIs('/'))
      {
        nextToken();
      }
      matchTypePos = m_xpath.m_opMap[XPath.MAPINDEX_LENGTH];
      axesType = XPath.MATCH_IMMEDIATE_ANCESTOR;
      appendOp(2, axesType);
    }

    // Make room for telling how long the step is without the predicate
    m_xpath.m_opMap[XPath.MAPINDEX_LENGTH] += 1;

    NodeTest(axesType);

    // Tell how long the step is without the predicate
    m_xpath.m_opMap[opPos + XPath.MAPINDEX_LENGTH + 1] = m_xpath.m_opMap[XPath.MAPINDEX_LENGTH] - opPos;

    while(tokenIs('['))
    {
      Predicate();
    }
    if((matchTypePos > -1) && tokenIs('/') && lookahead('/', 1))
    {
      m_xpath.m_opMap[matchTypePos] = XPath.MATCH_ANY_ANCESTOR;
      nextToken();
    }

    // Tell how long the entire step is.
    m_xpath.m_opMap[opPos + XPath.MAPINDEX_LENGTH] = m_xpath.m_opMap[XPath.MAPINDEX_LENGTH] - opPos;
  }

  /**
   * Dump an XPath string to System.out.
   */
  public static void diagnoseXPathString( String str )
    throws org.xml.sax.SAXException
  {
    XPathSupport callbacks = new XPathSupportDefault();
    XPathProcessorImpl processor = new XPathProcessorImpl(callbacks);
    XPath xpath = new XPath(new org.apache.xalan.xpath.xml.ProblemListenerDefault());
    processor.initXPath(xpath, str, null);
    processor.diagnoseXPath(xpath, 0, 0);
  }

  static int diagnoseXPathBinaryOperation(String op, XPath xpath, int opPos, int indent)
  {
    System.out.println(op+" {");
    opPos+=2;

    opPos = diagnoseXPath(xpath, opPos, indent+1);

    opPos = diagnoseXPath(xpath, opPos, indent+1);

    indent(indent);
    System.out.println("}");
    return opPos;
  }

  static int diagnoseXPathUnaryOperation(String op, XPath xpath, int opPos, int indent)
  {
    System.out.println(op+" {");
    opPos+=2;
    opPos = diagnoseXPath(xpath, opPos, indent+1);
    indent(indent);
    System.out.println("}");
    return opPos;
  }

  private static int diagnoseXPathMultiOperation(String op, int multiOp, XPath xpath, int opPos, int indent)
  {
    System.out.println(op+" {");
    opPos+=2;
    while(xpath.m_opMap[opPos] == multiOp)
    {
      indent(indent+1);
      System.out.println("{");
      opPos = diagnoseXPath(xpath, opPos, indent+2);
      indent(indent+1);
      System.out.println("}");
    }
    indent(indent);
    System.out.println("}");
    return opPos;
  }

  private static int diagnoseToken(XPath xpath, int opPos)
  {
    System.out.print("{");
    System.out.print(xpath.m_tokenQueue[xpath.m_opMap[opPos]]);
    System.out.print("}");
    return opPos+1;
  }

  private static int diagnoseXPathSimpleOperation(String op, XPath xpath, int opPos, int indent)
  {
    opPos+=2;
    System.out.print(op);
    opPos = diagnoseToken(xpath, opPos);
    System.out.println("");
    return opPos;
  }

  private static int diagnoseXPathLocationStep(String op, XPath xpath, int opPos, int indent)
  {
    // int opLen = xpath.m_opMap[opPos+xpath.MAPINDEX_LENGTH];
    int stepLen = xpath.m_opMap[opPos+xpath.MAPINDEX_LENGTH+1];
    opPos+=3;
    System.out.print(op);
    if(stepLen > 3)
    {
      opPos = diagnoseXPath(xpath, opPos, 1);
    }
    System.out.println("");
    return opPos;
  }

  static int diagnoseXPath(XPath xpath, int opPos, int indent)
  {
    indent(indent);
    switch(xpath.m_opMap[opPos])
    {
    case XPath.OP_XPATH:
      opPos = diagnoseXPathUnaryOperation("OP_XPATH", xpath, opPos, indent);
      break;
    case XPath.EMPTY:
      System.out.println("{EMPTY}");
      opPos++;
      break;
    case XPath.OP_OR:
      opPos = diagnoseXPathBinaryOperation("OP_OR", xpath, opPos, indent);
      break;
    case XPath.OP_AND:
      opPos = diagnoseXPathBinaryOperation("OP_AND", xpath, opPos, indent);
      break;
    case XPath.OP_NOTEQUALS:
      opPos = diagnoseXPathBinaryOperation("OP_NOTEQUALS", xpath, opPos, indent);
      break;
    case XPath.OP_EQUALS:
      opPos = diagnoseXPathBinaryOperation("OP_EQUALS", xpath, opPos, indent);
      break;
    case XPath.OP_LTE:
      opPos = diagnoseXPathBinaryOperation("OP_LTE", xpath, opPos, indent);
      break;
    case XPath.OP_LT:
      opPos = diagnoseXPathBinaryOperation("OP_LT", xpath, opPos, indent);
      break;
    case XPath.OP_GTE:
      opPos = diagnoseXPathBinaryOperation("OP_GTE", xpath, opPos, indent);
      break;
    case XPath.OP_GT:
      opPos = diagnoseXPathBinaryOperation("OP_GT", xpath, opPos, indent);
      break;
    case XPath.OP_PLUS:
      opPos = diagnoseXPathBinaryOperation("OP_PLUS", xpath, opPos, indent);
      break;
    case XPath.OP_MINUS:
      opPos = diagnoseXPathBinaryOperation("OP_MINUS", xpath, opPos, indent);
      break;
    case XPath.OP_MULT:
      opPos = diagnoseXPathBinaryOperation("OP_MULT", xpath, opPos, indent);
      break;
    case XPath.OP_DIV:
      opPos = diagnoseXPathBinaryOperation("OP_DIV", xpath, opPos, indent);
      break;
    case XPath.OP_MOD:
      opPos = diagnoseXPathBinaryOperation("OP_MOD", xpath, opPos, indent);
      break;
    case XPath.OP_QUO:
      opPos = diagnoseXPathBinaryOperation("OP_QUO", xpath, opPos, indent);
      break;
    case XPath.OP_NEG:
      opPos = diagnoseXPathUnaryOperation("OP_NEG", xpath, opPos, indent);
      break;
    case XPath.OP_STRING:
      opPos = diagnoseXPathUnaryOperation("OP_STRING", xpath, opPos, indent);
      break;
    case XPath.OP_BOOL:
      opPos = diagnoseXPathUnaryOperation("OP_BOOL", xpath, opPos, indent);
      break;
    case XPath.OP_NUMBER:
      opPos = diagnoseXPathUnaryOperation("OP_NUMBER", xpath, opPos, indent);
      break;
    case XPath.OP_UNION:
      opPos = diagnoseXPathMultiOperation("OP_UNION", xpath.OP_LOCATIONPATH, xpath, opPos, indent);
      break;
    case XPath.OP_LITERAL:
      opPos = diagnoseXPathSimpleOperation("OP_LITERAL", xpath, opPos, indent);
      break;
    case XPath.OP_VARIABLE:
      opPos = diagnoseXPathSimpleOperation("OP_VARIABLE", xpath, opPos, indent);
      break;
    case XPath.OP_GROUP:
      opPos = diagnoseXPathUnaryOperation("OP_GROUP", xpath, opPos, indent);
      break;
    case XPath.OP_NUMBERLIT:
      opPos = diagnoseXPathSimpleOperation("OP_NUMBERLIT", xpath, opPos, indent);
      break;
    case XPath.OP_ARGUMENT:
      opPos = diagnoseXPathUnaryOperation("OP_ARGUMENT", xpath, opPos, indent);
      break;
    case XPath.OP_EXTFUNCTION:
      {
        System.out.println("OP_EXTFUNCTION {");
        int endExtFunc = opPos+xpath.m_opMap[opPos+1]-1;
        opPos+=2;
        indent(indent+1);
        opPos = diagnoseToken(xpath, opPos);
        System.out.print(":");
        opPos = diagnoseToken(xpath, opPos);
        System.out.println("");
        while(opPos < endExtFunc)
        {
          indent(indent+1);
          System.out.println("{");
          opPos = diagnoseXPath(xpath, opPos, indent+2);
          indent(indent+1);
          System.out.println("}");
        }
        indent(indent);
        System.out.println("}");
        if(xpath.m_opMap[opPos] != xpath.ENDOP)
        {
          System.out.println(XSLMessages.createXPATHMessage(XPATHErrorResources.ER_COULDNOT_FIND_ENDOP_AFTER_OPLOCATIONPATH, null)); //"ERROR! Could not find ENDOP after OP_LOCATIONPATH");
        }
        opPos++;
      }
      break;
    case XPath.OP_FUNCTION:
      {
        System.out.println("OP_FUNCTION {");
        int endFunc = opPos+xpath.m_opMap[opPos+1]-1;
        opPos+=2;
        indent(indent+1);
        int funcID = xpath.m_opMap[opPos];
        switch(funcID)
        {
        case XPath.FUNC_LAST: System.out.print("FUNC_LAST"); break;
        case XPath.FUNC_POSITION: System.out.print("FUNC_POSITION"); break;
        case XPath.FUNC_COUNT: System.out.print("FUNC_COUNT"); break;
        case XPath.FUNC_ID: System.out.print("FUNC_ID"); break;
        case XPath.FUNC_KEY: System.out.print("FUNC_KEY"); break;
        // case XPath.FUNC_DOC: System.out.print("FUNC_DOC"); break;
        case XPath.FUNC_LOCAL_PART: System.out.print("FUNC_LOCAL_PART"); break;
        case XPath.FUNC_NAMESPACE: System.out.print("FUNC_NAMESPACE"); break;
        case XPath.FUNC_QNAME: System.out.print("FUNC_QNAME"); break;
        case XPath.FUNC_GENERATE_ID: System.out.print("FUNC_GENERATE_ID"); break;
        case XPath.FUNC_NOT: System.out.print("FUNC_NOT"); break;
        case XPath.FUNC_TRUE: System.out.print("FUNC_TRUE"); break;
        case XPath.FUNC_FALSE: System.out.print("FUNC_FALSE"); break;
        case XPath.FUNC_BOOLEAN: System.out.print("FUNC_BOOLEAN"); break;
        case XPath.FUNC_LANG: System.out.print("FUNC_LANG"); break;
        case XPath.FUNC_NUMBER: System.out.print("FUNC_NUMBER"); break;
        case XPath.FUNC_FLOOR: System.out.print("FUNC_FLOOR"); break;
        case XPath.FUNC_CEILING: System.out.print("FUNC_CEILING"); break;
        case XPath.FUNC_ROUND: System.out.print("FUNC_ROUND"); break;
        case XPath.FUNC_SUM: System.out.print("FUNC_SUM"); break;
        case XPath.FUNC_STRING: System.out.print("FUNC_STRING"); break;
        case XPath.FUNC_STARTS_WITH: System.out.print("FUNC_STARTS_WITH"); break;
        case XPath.FUNC_CONTAINS: System.out.print("FUNC_CONTAINS"); break;
        case XPath.FUNC_SUBSTRING_BEFORE: System.out.print("FUNC_SUBSTRING_BEFORE"); break;
        case XPath.FUNC_SUBSTRING_AFTER: System.out.print("FUNC_SUBSTRING_AFTER"); break;
        case XPath.FUNC_NORMALIZE_SPACE: System.out.print("FUNC_NORMALIZE_SPACE"); break;
        case XPath.FUNC_TRANSLATE: System.out.print("FUNC_TRANSLATE"); break;
        case XPath.FUNC_CONCAT: System.out.print("FUNC_CONCAT"); break;
        //case xpath.FUNC_FORMAT_NUMBER: System.out.print("FUNC_FORMAT_NUMBER"); break;
        case XPath.FUNC_SYSTEM_PROPERTY: System.out.print("FUNC_SYSTEM_PROPERTY"); break;
        case XPath.FUNC_EXT_FUNCTION_AVAILABLE: System.out.print("FUNC_EXT_FUNCTION_AVAILABLE"); break;
        case XPath.FUNC_EXT_ELEM_AVAILABLE: System.out.print("FUNC_EXT_ELEM_AVAILABLE"); break;
        case XPath.FUNC_SUBSTRING: System.out.print("FUNC_SUBSTRING"); break;
        case XPath.FUNC_STRING_LENGTH: System.out.print("FUNC_STRING_LENGTH"); break;
        case XPath.FUNC_DOCLOCATION: System.out.print("FUNC_DOCLOCATION"); break;
        }
        opPos++;
        System.out.println("");
        while(opPos < endFunc)
        {
          indent(indent+1);
          System.out.println("{");
          opPos = diagnoseXPath(xpath, opPos, indent+2);
          indent(indent+1);
          System.out.println("}");
        }
        indent(indent);
        System.out.println("}");
        if(xpath.m_opMap[opPos] != xpath.ENDOP)
        {
          System.out.println(XSLMessages.createXPATHMessage(XPATHErrorResources.ER_COULDNOT_FIND_ENDOP_AFTER_OPLOCATIONPATH, null)); //"ERROR! Could not find ENDOP after OP_LOCATIONPATH");
        }
        opPos++;
      }
      break;
    case XPath.OP_LOCATIONPATH:
    case XPath.OP_LOCATIONPATH_EX:
      System.out.println("OP_LOCATIONPATH"+" {");
      int endPath = opPos+xpath.m_opMap[opPos+1]-1;
      opPos+=2;
      while(opPos < endPath)
      {
        opPos = diagnoseXPath(xpath, opPos, indent+1);
      }
      indent(indent);
      System.out.println("}");
      if(xpath.m_opMap[opPos] != xpath.ENDOP)
      {
        System.out.println(XSLMessages.createXPATHMessage(XPATHErrorResources.ER_COULDNOT_FIND_ENDOP_AFTER_OPLOCATIONPATH, null)); //"ERROR! Could not find ENDOP after OP_LOCATIONPATH");
      }
      opPos++;
      break;
    case XPath.OP_PREDICATE:
      opPos = diagnoseXPathUnaryOperation("OP_PREDICATE", xpath, opPos, indent);
      if(xpath.m_opMap[opPos] != xpath.ENDOP)
      {
        System.out.println("ERROR! Could not find ENDOP after OP_LOCATIONPATH");
      }
      opPos++;
      break;
    case XPath.FROM_ANCESTORS:
      opPos = diagnoseXPathLocationStep("FROM_ANCESTORS", xpath, opPos, 1);
      break;
    case XPath.FROM_ANCESTORS_OR_SELF:
      opPos = diagnoseXPathLocationStep("FROM_ANCESTORS_OR_SELF", xpath, opPos, 1);
      break;
    case XPath.FROM_ATTRIBUTES:
      opPos = diagnoseXPathLocationStep("FROM_ATTRIBUTES", xpath, opPos, 1);
      break;
    case XPath.FROM_CHILDREN:
      opPos = diagnoseXPathLocationStep("FROM_CHILDREN", xpath, opPos, 1);
      break;
    case XPath.FROM_DESCENDANTS:
      opPos = diagnoseXPathLocationStep("FROM_DESCENDANTS", xpath, opPos, 1);
      break;
    case XPath.FROM_DESCENDANTS_OR_SELF:
      opPos = diagnoseXPathLocationStep("FROM_DESCENDANTS_OR_SELF", xpath, opPos, 1);
      break;
    case XPath.FROM_FOLLOWING:
      opPos = diagnoseXPathLocationStep("FROM_FOLLOWING", xpath, opPos, indent);
      break;
    case XPath.FROM_FOLLOWING_SIBLINGS:
      opPos = diagnoseXPathLocationStep("FROM_FOLLOWING_SIBLINGS", xpath, opPos, indent);
      break;
    case XPath.FROM_PARENT:
      opPos = diagnoseXPathLocationStep("FROM_PARENT", xpath, opPos, indent);
      break;
    case XPath.FROM_PRECEDING:
      opPos = diagnoseXPathLocationStep("FROM_PRECEDING", xpath, opPos, indent);
      break;
    case XPath.FROM_PRECEDING_SIBLINGS:
      opPos = diagnoseXPathLocationStep("FROM_PRECEDING_SIBLINGS", xpath, opPos, indent);
      break;
    case XPath.FROM_SELF:
      opPos = diagnoseXPathLocationStep("FROM_SELF", xpath, opPos, indent);
      break;
    case XPath.FROM_NAMESPACE:
      opPos = diagnoseXPathLocationStep("FROM_NAMESPACE", xpath, opPos, indent);
      break;
    // case XPath.FROM_ATTRIBUTE:
    //   opPos = diagnoseXPathLocationStep("FROM_ATTRIBUTE", xpath, opPos, indent);
    //  break;
    // case XPath.FROM_DOC:
    //  opPos = diagnoseXPathLocationStep("FROM_DOC", xpath, opPos, indent);
    //  break;
    // case XPath.FROM_DOCREF:
    //  opPos = diagnoseXPathLocationStep("FROM_DOCREF", xpath, opPos, indent);
    //  break;
    // case XPath.FROM_ID:
    //  opPos = diagnoseXPathLocationStep("FROM_ID", xpath, opPos, indent);
    //  break;
    // case XPath.FROM_IDREF:
    //  opPos = diagnoseXPathLocationStep("FROM_IDREF", xpath, opPos, indent);
    //  break;
    case XPath.FROM_ROOT:
      opPos = diagnoseXPathLocationStep("FROM_ROOT", xpath, opPos, indent);
      break;
    case XPath.NODETYPE_COMMENT:
      System.out.println("{NODETYPE_COMMENT}");
      opPos++;
      break;
    case XPath.NODETYPE_TEXT:
      System.out.println("{NODETYPE_TEXT}");
      opPos++;
      break;
    case XPath.NODETYPE_PI:
      int piLen = xpath.m_opMap[opPos-1];
      System.out.println("{NODETYPE_PI ");
      opPos++;
      if(piLen > 3)
      {
        opPos = diagnoseToken(xpath, opPos);
      }
      break;
    case XPath.NODETYPE_NODE:
      System.out.println("{NODETYPE_NODE}");
      opPos++;
      break;
    case XPath.NODETYPE_ROOT:
      System.out.println("{NODETYPE_ROOT}");
      opPos++;
      break;
    case XPath.NODETYPE_ANYELEMENT:
      System.out.println("{NODETYPE_ANYELEMENT}");
      opPos++;
      break;
    case XPath.NODENAME:
      System.out.print("{NODENAME ");
      opPos++;
      if(xpath.m_opMap[opPos] < 0)
      {
        System.out.print("{EMPTY}");
        opPos++;
      }
      else
      {
        opPos = diagnoseToken(xpath, opPos);
      }
      System.out.print(":");
      opPos = diagnoseToken(xpath, opPos);
      break;
    default:
	  System.out.println(XSLMessages.createXPATHMessage(XPATHErrorResources.ER_UNKNOWN_OPCODE, new Object[] {Integer.toString(xpath.m_opMap[opPos])})); //"ERROR! Unknown op code: "+xpath.m_opMap[opPos]);
    }
    return opPos;
  }

  static void indent(int amount)
  {
    int n = amount * 3;
    for(int i = 0;  i < n; i ++)
    {
      System.out.print(" ");
    }
  }

  private static String m_opLabel     = "[";
  private static String m_lenLabel    = "[";
  private static String m_arglenLabel = "[";
  private static String m_noLabel     = "[";
  private static String m_nTestLabel  = "[";
  private static String m_open = "[";
  private static String m_close = "]";

  /**
   * Dump an XPath string to System.out.
   */
  public static void diagnoseXPathString2( String str )
    throws org.xml.sax.SAXException
  {
    XPathSupport callbacks = new XPathSupportDefault();
    XPathProcessorImpl processor = new XPathProcessorImpl(callbacks);
    XPath xpath = new XPath(new org.apache.xalan.xpath.xml.ProblemListenerDefault());
    processor.initXPath(xpath, str, null);
    processor.diagnoseXPath2(xpath, 0, 0);
  }

  /**
   * Dump an XPath string to System.out.
   */
  public static void diagnoseXPathString3( String str )
    throws org.xml.sax.SAXException
  {
    XPathSupport callbacks = new XPathSupportDefault();
    XPathProcessorImpl processor = new XPathProcessorImpl(callbacks);
    XPath xpath = new XPath(new org.apache.xalan.xpath.xml.ProblemListenerDefault());
    processor.initXPath(xpath, str, null);
    int len = xpath.m_opMap[xpath.MAPINDEX_LENGTH];
    for(int i = 0; i < len; i++)
    {
      System.out.println("["+xpath.m_opMap[i]+"]");
    }
  }

  private static void diagnoseNodeTest2(int opPos, String op)
  {
    System.out.print(m_nTestLabel+op+m_close);
  }

  private static void diagnoseOpNoLable2(int opPos, String op)
  {
    System.out.println(m_noLabel+op+m_close);
  }

  private static void diagnoseOpOnly2(int opPos, String op)
  {
    System.out.println(m_opLabel+op+m_close);
  }

  private static void diagnoseOp2(String op, XPath xpath, int opPos)
  {
    System.out.print(m_opLabel+op+m_close);
    int opLen = xpath.m_opMap[opPos+xpath.MAPINDEX_LENGTH];
    System.out.println(m_open+opLen+m_close);
  }

  private static void diagnoseOp2SameLine(String op, XPath xpath, int opPos)
  {
    System.out.print(m_opLabel+op+m_close);
    int opLen = xpath.m_opMap[opPos+xpath.MAPINDEX_LENGTH];
    System.out.print(m_open+opLen+m_close);
  }

  private static int diagnoseXPathBinaryOperation2(String op, XPath xpath, int opPos, int indent)
  {
    diagnoseOp2(op, xpath, opPos);
    opPos+=2;

    opPos = diagnoseXPath2(xpath, opPos, indent+1);

    opPos = diagnoseXPath2(xpath, opPos, indent+1);

    return opPos;
  }

  private static int diagnoseXPathUnaryOperation2(String op, XPath xpath, int opPos, int indent)
  {
    diagnoseOp2(op, xpath, opPos);
    opPos+=2;
    opPos = diagnoseXPath2(xpath, opPos, indent+1);
    return opPos;
  }

  private static int diagnoseXPathMultiOperation2(String op, int multiOp, XPath xpath, int opPos, int indent)
  {
    diagnoseOp2(op, xpath, opPos);
    opPos+=2;
    while(xpath.m_opMap[opPos] == multiOp)
    {
      opPos = diagnoseXPath2(xpath, opPos, indent+2);
    }
    return opPos;
  }

  private static int diagnoseToken2(XPath xpath, int opPos)
  {
    int tokenPos = xpath.m_opMap[opPos];
    String token = (tokenPos >= 0) ? xpath.m_tokenQueue[tokenPos].toString() :
                                     (tokenPos == xpath.ELEMWILDCARD) ?
                                     "*" : (tokenPos == xpath.EMPTY) ?
                                           "EMPTY" : "UNKNOWN";
    System.out.println(m_noLabel+token+m_close);
    return opPos+1;
  }

  private static int diagnoseToken2SameLine(XPath xpath, int opPos)
  {
    System.out.print(m_noLabel+xpath.m_tokenQueue[xpath.m_opMap[opPos]]+m_close);
    return opPos+1;
  }

  private static int diagnoseXPathSimpleOperation2(String op, XPath xpath, int opPos, int indent)
  {
    diagnoseOp2SameLine(op, xpath, opPos);
    opPos+=2;
    opPos = diagnoseToken2(xpath, opPos);
    return opPos;
  }

  private static int diagnoseXPathLocationStep2(String op, XPath xpath, int opPos, int indent)
  {
    int opLen = xpath.m_opMap[opPos+xpath.MAPINDEX_LENGTH];
    int stepLen = xpath.m_opMap[opPos+xpath.MAPINDEX_LENGTH+1];
    System.out.print(m_opLabel+op+m_close);
    System.out.print(m_open+opLen+m_close);
    System.out.print(m_open+stepLen+m_close);
    opPos+=3;
    if(stepLen > 3)
    {
      opPos = diagnoseXPath2(xpath, opPos, 0);
    }
    return opPos;
  }

  private static int diagnoseXPath2(XPath xpath, int opPos, int indent)
  {
    indent(indent);
    switch(xpath.m_opMap[opPos])
    {
    case XPath.OP_XPATH:
      opPos = diagnoseXPathUnaryOperation2("OP_XPATH", xpath, opPos, indent);
      break;
    case XPath.EMPTY:
      diagnoseOpOnly2(opPos, "EMPTY");
      opPos++;
      break;
    case XPath.OP_OR:
      opPos = diagnoseXPathBinaryOperation2("OP_OR", xpath, opPos, indent);
      break;
    case XPath.OP_AND:
      opPos = diagnoseXPathBinaryOperation2("OP_AND", xpath, opPos, indent);
      break;
    case XPath.OP_NOTEQUALS:
      opPos = diagnoseXPathBinaryOperation2("OP_NOTEQUALS", xpath, opPos, indent);
      break;
    case XPath.OP_EQUALS:
      opPos = diagnoseXPathBinaryOperation2("OP_EQUALS", xpath, opPos, indent);
      break;
    case XPath.OP_LTE:
      opPos = diagnoseXPathBinaryOperation2("OP_LTE", xpath, opPos, indent);
      break;
    case XPath.OP_LT:
      opPos = diagnoseXPathBinaryOperation2("OP_LT", xpath, opPos, indent);
      break;
    case XPath.OP_GTE:
      opPos = diagnoseXPathBinaryOperation2("OP_GTE", xpath, opPos, indent);
      break;
    case XPath.OP_GT:
      opPos = diagnoseXPathBinaryOperation2("OP_GT", xpath, opPos, indent);
      break;
    case XPath.OP_PLUS:
      opPos = diagnoseXPathBinaryOperation2("OP_PLUS", xpath, opPos, indent);
      break;
    case XPath.OP_MINUS:
      opPos = diagnoseXPathBinaryOperation2("OP_MINUS", xpath, opPos, indent);
      break;
    case XPath.OP_MULT:
      opPos = diagnoseXPathBinaryOperation2("OP_MULT", xpath, opPos, indent);
      break;
    case XPath.OP_DIV:
      opPos = diagnoseXPathBinaryOperation2("OP_DIV", xpath, opPos, indent);
      break;
    case XPath.OP_MOD:
      opPos = diagnoseXPathBinaryOperation2("OP_MOD", xpath, opPos, indent);
      break;
    case XPath.OP_QUO:
      opPos = diagnoseXPathBinaryOperation2("OP_QUO", xpath, opPos, indent);
      break;
    case XPath.OP_NEG:
      opPos = diagnoseXPathUnaryOperation2("OP_NEG", xpath, opPos, indent);
      break;
    case XPath.OP_STRING:
      opPos = diagnoseXPathUnaryOperation2("OP_STRING", xpath, opPos, indent);
      break;
    case XPath.OP_BOOL:
      opPos = diagnoseXPathUnaryOperation2("OP_BOOL", xpath, opPos, indent);
      break;
    case XPath.OP_NUMBER:
      opPos = diagnoseXPathUnaryOperation2("OP_NUMBER", xpath, opPos, indent);
      break;
    case XPath.OP_UNION:
      opPos = diagnoseXPathMultiOperation2("OP_UNION", xpath.OP_LOCATIONPATH, xpath, opPos, indent);
      break;
    case XPath.OP_LITERAL:
      opPos = diagnoseXPathSimpleOperation2("OP_LITERAL", xpath, opPos, indent);
      break;
    case XPath.OP_VARIABLE:
      opPos = diagnoseXPathSimpleOperation2("OP_VARIABLE", xpath, opPos, indent);
      break;
    case XPath.OP_GROUP:
      opPos = diagnoseXPathUnaryOperation2("OP_GROUP", xpath, opPos, indent);
      break;
    case XPath.OP_NUMBERLIT:
      opPos = diagnoseXPathSimpleOperation2("OP_NUMBERLIT", xpath, opPos, indent);
      break;
    case XPath.OP_ARGUMENT:
      opPos = diagnoseXPathUnaryOperation2("OP_ARGUMENT", xpath, opPos, indent);
      break;
    case XPath.OP_EXTFUNCTION:
      {
        diagnoseOp2SameLine("OP_EXTFUNCTION", xpath, opPos);
        int endExtFunc = opPos+xpath.m_opMap[opPos+1]-1;
        opPos+=2;
        opPos = diagnoseToken2SameLine(xpath, opPos);
        opPos = diagnoseToken2(xpath, opPos);
        while(opPos < endExtFunc)
        {
          opPos = diagnoseXPath2(xpath, opPos, indent+2);
        }
        if(xpath.m_opMap[opPos] != xpath.ENDOP)
        {
          System.out.println("ERROR! Could not find ENDOP after OP_LOCATIONPATH");
        }
        indent(indent+1);
        diagnoseOpOnly2(opPos, "ENDOP");
        opPos++;
      }
      break;
    case XPath.OP_FUNCTION:
      {
        diagnoseOp2SameLine("OP_FUNCTION", xpath, opPos);
        int endFunc = opPos+xpath.m_opMap[opPos+1]-1;
        opPos+=2;
        int funcID = xpath.m_opMap[opPos];
        switch(funcID)
        {
        case XPath.FUNC_LAST: diagnoseOpNoLable2(opPos, "FUNC_LAST"); break;
        case XPath.FUNC_POSITION: diagnoseOpNoLable2(opPos, "FUNC_POSITION"); break;
        case XPath.FUNC_COUNT: diagnoseOpNoLable2(opPos, "FUNC_COUNT"); break;
        case XPath.FUNC_ID: diagnoseOpNoLable2(opPos, "FUNC_ID"); break;
        case XPath.FUNC_KEY: diagnoseOpNoLable2(opPos, "FUNC_KEY"); break;
        // case xpath.FUNC_DOC: diagnoseOpNoLable2(opPos, "FUNC_DOC"); break;
        case XPath.FUNC_LOCAL_PART: diagnoseOpNoLable2(opPos, "FUNC_LOCAL_PART"); break;
        case XPath.FUNC_NAMESPACE: diagnoseOpNoLable2(opPos, "FUNC_NAMESPACE"); break;
        case XPath.FUNC_QNAME: diagnoseOpNoLable2(opPos, "FUNC_QNAME"); break;
        case XPath.FUNC_GENERATE_ID: diagnoseOpNoLable2(opPos, "FUNC_GENERATE_ID"); break;
        case XPath.FUNC_NOT: diagnoseOpNoLable2(opPos, "FUNC_NOT"); break;
        case XPath.FUNC_TRUE: diagnoseOpNoLable2(opPos, "FUNC_TRUE"); break;
        case XPath.FUNC_FALSE: diagnoseOpNoLable2(opPos, "FUNC_FALSE"); break;
        case XPath.FUNC_BOOLEAN: diagnoseOpNoLable2(opPos, "FUNC_BOOLEAN"); break;
        case XPath.FUNC_LANG: diagnoseOpNoLable2(opPos, "FUNC_LANG"); break;
        case XPath.FUNC_NUMBER: diagnoseOpNoLable2(opPos, "FUNC_NUMBER"); break;
        case XPath.FUNC_FLOOR: diagnoseOpNoLable2(opPos, "FUNC_FLOOR"); break;
        case XPath.FUNC_CEILING: diagnoseOpNoLable2(opPos, "FUNC_CEILING"); break;
        case XPath.FUNC_ROUND: diagnoseOpNoLable2(opPos, "FUNC_ROUND"); break;
        case XPath.FUNC_SUM: diagnoseOpNoLable2(opPos, "FUNC_SUM"); break;
        case XPath.FUNC_STRING: diagnoseOpNoLable2(opPos, "FUNC_STRING"); break;
        case XPath.FUNC_STARTS_WITH: diagnoseOpNoLable2(opPos, "FUNC_STARTS_WITH"); break;
        case XPath.FUNC_CONTAINS: diagnoseOpNoLable2(opPos, "FUNC_CONTAINS"); break;
        case XPath.FUNC_SUBSTRING_BEFORE: diagnoseOpNoLable2(opPos, "FUNC_SUBSTRING_BEFORE"); break;
        case XPath.FUNC_SUBSTRING_AFTER: diagnoseOpNoLable2(opPos, "FUNC_SUBSTRING_AFTER"); break;
        case XPath.FUNC_NORMALIZE_SPACE: diagnoseOpNoLable2(opPos, "FUNC_NORMALIZE_SPACE"); break;
        case XPath.FUNC_TRANSLATE: diagnoseOpNoLable2(opPos, "FUNC_TRANSLATE"); break;
        case XPath.FUNC_CONCAT: diagnoseOpNoLable2(opPos, "FUNC_CONCAT"); break;
        //case XPath.FUNC_FORMAT_NUMBER: diagnoseOpNoLable2(opPos, "FUNC_FORMAT_NUMBER"); break;
        case XPath.FUNC_SYSTEM_PROPERTY: diagnoseOpNoLable2(opPos, "FUNC_SYSTEM_PROPERTY"); break;
        case XPath.FUNC_EXT_FUNCTION_AVAILABLE: diagnoseOpNoLable2(opPos, "FUNC_EXT_FUNCTION_AVAILABLE"); break;
        case XPath.FUNC_EXT_ELEM_AVAILABLE: diagnoseOpNoLable2(opPos, "FUNC_EXT_ELEM_AVAILABLE"); break;
        case XPath.FUNC_SUBSTRING: diagnoseOpNoLable2(opPos, "FUNC_SUBSTRING"); break;
        case XPath.FUNC_STRING_LENGTH: diagnoseOpNoLable2(opPos, "FUNC_STRING_LENGTH"); break;
        case XPath.FUNC_DOCLOCATION: diagnoseOpNoLable2(opPos, "FUNC_DOCLOCATION"); break;
        }
        opPos++;
        while(opPos < endFunc)
        {
          // indent(indent+1);
          opPos = diagnoseXPath2(xpath, opPos, indent+2);
        }
        indent(indent);
        if(xpath.m_opMap[opPos] != xpath.ENDOP)
        {
          System.out.println(XSLMessages.createXPATHMessage(XPATHErrorResources.ER_COULDNOT_FIND_ENDOP_AFTER_OPLOCATIONPATH, null)); //"ERROR! Could not find ENDOP after OP_LOCATIONPATH");
        }
        indent(indent+1);
        diagnoseOpOnly2(opPos, "ENDOP");
        opPos++;
      }
      break;
    case XPath.OP_LOCATIONPATH_EX:
    case XPath.OP_LOCATIONPATH:
      diagnoseOp2("OP_LOCATIONPATH", xpath, opPos);
      int endPath = opPos+xpath.m_opMap[opPos+1]-1;
      opPos+=2;
      while(opPos < endPath)
      {
        opPos = diagnoseXPath2(xpath, opPos, indent+1);
      }
      if(xpath.m_opMap[opPos] != xpath.ENDOP)
      {
        System.out.println(XSLMessages.createXPATHMessage(XPATHErrorResources.ER_COULDNOT_FIND_ENDOP_AFTER_OPLOCATIONPATH, null)); //"ERROR! Could not find ENDOP after OP_LOCATIONPATH");
      }
      indent(indent+1);
      diagnoseOpOnly2(opPos, "ENDOP");
      opPos++;
      break;
    case XPath.OP_PREDICATE:
      indent(1);
      opPos = diagnoseXPathUnaryOperation2("OP_PREDICATE", xpath, opPos, indent+1);
      if(xpath.m_opMap[opPos] != xpath.ENDOP)
      {
        System.out.println("ERROR! Could not find ENDOP after OP_LOCATIONPATH");
      }
      indent(indent+2);
      diagnoseOpOnly2(opPos, "ENDOP");
      opPos++;
      break;
    case XPath.FROM_ANCESTORS:
      opPos = diagnoseXPathLocationStep2("FROM_ANCESTORS", xpath, opPos, 1);
      break;
    case XPath.FROM_ANCESTORS_OR_SELF:
      opPos = diagnoseXPathLocationStep2("FROM_ANCESTORS_OR_SELF", xpath, opPos, 1);
      break;
    case XPath.FROM_ATTRIBUTES:
      opPos = diagnoseXPathLocationStep2("FROM_ATTRIBUTES", xpath, opPos, 1);
      break;
    case XPath.FROM_CHILDREN:
      opPos = diagnoseXPathLocationStep2("FROM_CHILDREN", xpath, opPos, 1);
      break;
    case XPath.FROM_DESCENDANTS:
      opPos = diagnoseXPathLocationStep2("FROM_DESCENDANTS", xpath, opPos, 1);
      break;
    case XPath.FROM_DESCENDANTS_OR_SELF:
      opPos = diagnoseXPathLocationStep2("FROM_DESCENDANTS_OR_SELF", xpath, opPos, 1);
      break;
    case XPath.FROM_FOLLOWING:
      opPos = diagnoseXPathLocationStep2("FROM_FOLLOWING", xpath, opPos, indent);
      break;
    case XPath.FROM_FOLLOWING_SIBLINGS:
      opPos = diagnoseXPathLocationStep2("FROM_FOLLOWING_SIBLINGS", xpath, opPos, indent);
      break;
    case XPath.FROM_PARENT:
      opPos = diagnoseXPathLocationStep2("FROM_PARENT", xpath, opPos, indent);
      break;
    case XPath.FROM_PRECEDING:
      opPos = diagnoseXPathLocationStep2("FROM_PRECEDING", xpath, opPos, indent);
      break;
    case XPath.FROM_PRECEDING_SIBLINGS:
      opPos = diagnoseXPathLocationStep2("FROM_PRECEDING_SIBLINGS", xpath, opPos, indent);
      break;
    case XPath.FROM_SELF:
      opPos = diagnoseXPathLocationStep2("FROM_SELF", xpath, opPos, indent);
      break;
    case XPath.FROM_NAMESPACE:
      opPos = diagnoseXPathLocationStep2("FROM_NAMESPACE", xpath, opPos, indent);
      break;
    // case XPath.FROM_ATTRIBUTE:
    //   opPos = diagnoseXPathLocationStep("FROM_ATTRIBUTE", xpath, opPos, indent);
    //  break;
    // case XPath.FROM_DOC:
    //  opPos = diagnoseXPathLocationStep("FROM_DOC", xpath, opPos, indent);
    //  break;
    // case XPath.FROM_DOCREF:
    //  opPos = diagnoseXPathLocationStep("FROM_DOCREF", xpath, opPos, indent);
    //  break;
    // case XPath.FROM_ID:
    //  opPos = diagnoseXPathLocationStep("FROM_ID", xpath, opPos, indent);
    //  break;
    // case XPath.FROM_IDREF:
    //  opPos = diagnoseXPathLocationStep("FROM_IDREF", xpath, opPos, indent);
    //  break;
    case XPath.FROM_ROOT:
      opPos = diagnoseXPathLocationStep2("FROM_ROOT", xpath, opPos, indent);
      // opPos++;
      break;
    case XPath.NODETYPE_COMMENT:
      diagnoseNodeTest2(opPos, "NODETYPE_COMMENT");
      System.out.println();
      opPos++;
      break;
    case XPath.NODETYPE_TEXT:
      diagnoseNodeTest2(opPos, "NODETYPE_TEXT");
      System.out.println();
      opPos++;
      break;
    case XPath.NODETYPE_PI:
      int piLen = xpath.m_opMap[opPos-1];
      diagnoseNodeTest2(opPos, "NODETYPE_PI");
      opPos++;
      if(piLen > 3)
      {
        opPos = diagnoseToken(xpath, opPos);
      }
      break;
    case XPath.NODETYPE_NODE:
      diagnoseNodeTest2(opPos, "NODETYPE_NODE");
      System.out.println();
      opPos++;
      break;
    case XPath.NODETYPE_ROOT:
      diagnoseNodeTest2(opPos, "NODETYPE_ROOT");
      System.out.println();
      opPos++;
      break;
    case XPath.NODETYPE_ANYELEMENT:
      diagnoseNodeTest2(opPos, "NODETYPE_ANYELEMENT");
      System.out.println();
      opPos++;
      break;
    case XPath.NODENAME:
      diagnoseNodeTest2(opPos, "NODENAME");
      opPos++;
      if(xpath.m_opMap[opPos] < 0)
      {
        System.out.print(m_noLabel+"EMPTY"+m_close);
        opPos++;
      }
      else
      {
        opPos = diagnoseToken2(xpath, opPos);
      }
      opPos = diagnoseToken2(xpath, opPos);
      break;
    default:
      System.out.println(XSLMessages.createXPATHMessage(XPATHErrorResources.ER_UNKNOWN_OPCODE, new Object[] {Integer.toString(xpath.m_opMap[opPos])})); //"ERROR! Unknown op code: "+xpath.m_opMap[opPos]);
    }
    return opPos;
  }

}
