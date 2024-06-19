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
package org.apache.xalan.xslt;

import java.util.Hashtable;
import org.w3c.dom.Node;
import org.apache.xalan.xpath.xml.QName;
import org.xml.sax.SAXException;
import java.util.Vector;
import java.io.Serializable;
import org.apache.xalan.xpath.XPath;
import org.apache.xalan.xslt.res.XSLTErrorResources;

/**
 * <meta name="usage" content="advanced"/>
 * Encapsulates a template list, and helps locate individual templates.
 */
public class TemplateList implements java.io.Serializable
{
  /**
   * Construct a TemplateList object.
   */
  TemplateList(Stylesheet stylesheet)
  {
    m_stylesheet = stylesheet;
  }

  /**
   * The stylesheet owner of the list.
   */
  Stylesheet m_stylesheet;
  
  /**
   * Get the stylesheet owner of the list.
   */
  public Stylesheet getStylesheet()
  {
    return m_stylesheet;
  }

  /**
   * The first template of the template children.
   * @serial
   */
  ElemTemplateElement m_firstTemplate = null;

  /**
   * Get the first template of the template children.
   */
  public ElemTemplateElement getFirstTemplate()
  {
    return m_firstTemplate;
  }

  /**
   * Set the first template of the template children.
   */
  public void setFirstTemplate(ElemTemplateElement v)
  {
    m_firstTemplate = v;
  }

  /**
   * Keyed on string macro names, and holding values
   * that are macro elements in the XSL DOM tree.
   * Initialized in initMacroLookupTable, and used in
   * findNamedTemplate.
   * @serial
   */
  Hashtable m_namedTemplates = new Hashtable();

  /**
   * Tells if the stylesheet is without an xsl:stylesheet
   * and xsl:template wrapper.
   * @serial
   */
  private boolean m_isWrapperless = false;

  /**
   * The manufactured template if there is no wrapper.
   * @serial
   */
  private ElemTemplate m_wrapperlessTemplate = null;

  /**
   * This table is keyed on the target elements
   * of patterns, and contains linked lists of
   * the actual patterns that match the target element
   * to some degree of specifity.
   * @serial
   */
  private Hashtable m_patternTable = new Hashtable();

  /**
   * Set if the stylesheet is without an xsl:stylesheet
   * and xsl:template wrapper.
   */
  void setIsWrapperless(boolean b)
  {
    m_isWrapperless = b;
  }

  /**
   * Get if the stylesheet is without an xsl:stylesheet
   * and xsl:template wrapper.
   */
  public boolean getIsWrapperless()
  {
    return m_isWrapperless;
  }

  /**
   * Set the manufactured template if there is no wrapper.
   * and xsl:template wrapper.
   */
  void setWrapperlessTemplate(ElemTemplate t)
  {
    m_wrapperlessTemplate = t;
  }

  /**
   * Get the manufactured template if there is no wrapper.
   * and xsl:template wrapper.
   */
  public ElemTemplate getWrapperlessTemplate()
  {
    return m_wrapperlessTemplate;
  }

  /**
   * Get table of named Templates.
   * These are keyed on string macro names, and holding values
   * that are template elements in the XSL DOM tree.
   */
  public Hashtable getNamedTemplates()
  {
    return m_namedTemplates;
  }

  /**
   * Set table of named Templates.
   * These are keyed on string macro names, and holding values
   * that are template elements in the XSL DOM tree.
   */
  public void setNamedTemplates(Hashtable v)
  {
    m_namedTemplates = v;
  }

  /**
   * Given a target element, find the template that best
   * matches in the given XSL document, according
   * to the rules specified in the xsl draft.
   * @param stylesheetTree Where the XSL rules are to be found.
   * @param sourceTree Where the targetElem is to be found.
   * @param targetElem The element that needs a rule.
   * @param mode A string indicating the display mode.
   * @param useImports means that this is an xsl:apply-imports commend.
   * @return Rule that best matches targetElem.
   * @exception XSLProcessorException thrown if the active ProblemListener and XMLParserLiaison decide
   * the error condition is severe enough to halt processing.
   */
  public ElemTemplate findTemplate(XSLTEngineImpl transformContext,
                                   Node sourceTree,
                                   Node targetNode,
                                   QName mode,
                                   boolean useImports)
    throws SAXException
  {
    ElemTemplate bestMatchedRule = null;
    MatchPattern2 bestMatchedPattern =null; // Syncs with bestMatchedRule
    if(m_isWrapperless)
    {
      return m_wrapperlessTemplate;
    }
    Vector conflicts = null;
    if(!useImports)
    {
      double highScore = XPath.MATCH_SCORE_NONE;

      MatchPattern2 matchPat = null;
      int targetNodeType = targetNode.getNodeType();

      switch(targetNodeType)
      {
      case Node.ELEMENT_NODE:
        // String targetName = m_parserLiaison.getExpandedElementName((Element)targetNode);
        String targetName = transformContext.m_parserLiaison.getLocalNameOfNode(targetNode);
        matchPat = locateMatchPatternList2(targetName, true);
        break;

      case Node.PROCESSING_INSTRUCTION_NODE:
      case Node.ATTRIBUTE_NODE:
        matchPat = locateMatchPatternList2(targetNode.getNodeName(), true);
        break;

      case Node.CDATA_SECTION_NODE:
      case Node.TEXT_NODE:
        matchPat = locateMatchPatternList2(XPath.PSEUDONAME_TEXT, false);
        break;

      case Node.COMMENT_NODE:
        matchPat = locateMatchPatternList2(XPath.PSEUDONAME_COMMENT, false);
        break;

      case Node.DOCUMENT_NODE:
        matchPat = locateMatchPatternList2(XPath.PSEUDONAME_ROOT, false);
        break;

      case Node.DOCUMENT_FRAGMENT_NODE:
        matchPat = locateMatchPatternList2(XPath.PSEUDONAME_ANY, false);
        break;

      default:
        {
          matchPat = locateMatchPatternList2(targetNode.getNodeName(), false);
        }
      }

      String prevPat = null;
      MatchPattern2 prevMatchPat = null;

      while(null != matchPat)
      {
        ElemTemplate rule = matchPat.getTemplate();
        // We'll be needing to match rules according to what
        // mode we're in.
        QName ruleMode = rule.m_mode;

        // The logic here should be that if we are not in a mode AND
        // the rule does not have a node, then go ahead.
        // OR if we are in a mode, AND the rule has a node,
        // AND the rules match, then go ahead.
        if(((null == mode) && (null == ruleMode)) ||
           ((null != ruleMode) && (null != mode) && ruleMode.equals(mode)))
        {
          String patterns = matchPat.getPattern();

          if((null != patterns) && !((prevPat != null) && prevPat.equals(patterns) &&
                                     (prevMatchPat.getTemplate().m_priority
                                      == matchPat.getTemplate().m_priority)) )
          {
            prevMatchPat = matchPat;
            prevPat = patterns;

            // Date date1 = new Date();
            XPath xpath = matchPat.getExpression();
            // System.out.println("Testing score for: "+targetNode.getNodeName()+
            //                   " against '"+xpath.m_currentPattern);
            double score = xpath.getMatchScore(transformContext.getExecContext(), targetNode);
            // System.out.println("Score for: "+targetNode.getNodeName()+
            //                   " against '"+xpath.m_currentPattern+
            //                   "' returned "+score);

            if(XPath.MATCH_SCORE_NONE != score)
            {
              double priorityOfRule
                = (XPath.MATCH_SCORE_NONE != rule.m_priority)
                  ? rule.m_priority : score;
              matchPat.m_priority = priorityOfRule;
              double priorityOfBestMatched = (null != bestMatchedPattern) ?
                                             bestMatchedPattern.m_priority :
                                             XPath.MATCH_SCORE_NONE;
              // System.out.println("priorityOfRule: "+priorityOfRule+", priorityOfBestMatched: "+priorityOfBestMatched);
              if(priorityOfRule > priorityOfBestMatched)
              {
                if(null != conflicts)
                  conflicts.removeAllElements();
                highScore = score;
                bestMatchedRule = rule;
                bestMatchedPattern = matchPat;
              }
              else if(priorityOfRule == priorityOfBestMatched)
              {
                if(null == conflicts)
                  conflicts = new Vector(10);
                addObjectIfNotFound(bestMatchedPattern, conflicts);
                conflicts.addElement(matchPat);
                highScore = score;
                bestMatchedRule = rule;
                bestMatchedPattern = matchPat;
              }
            }
            // Date date2 = new Date();
            // m_totalTimePatternMatching+=(date2.getTime() - date1.getTime());
          } // end if(null != patterns)
        } // end if if(targetModeString.equals(mode))

        MatchPattern2 nextMatchPat = matchPat.getNext();

        // We also have to consider wildcard matches.
        if((null == nextMatchPat) && !matchPat.m_targetString.equals("*") &&
           ((Node.ELEMENT_NODE == targetNodeType) ||
            (Node.ATTRIBUTE_NODE == targetNodeType) ||
            (Node.PROCESSING_INSTRUCTION_NODE == targetNodeType)))
        {
          nextMatchPat = (MatchPattern2)m_patternTable.get("*");
        }
        matchPat = nextMatchPat;
      }
    } // end if(!useImports)

    if(null == bestMatchedRule)
    {
      int nImports = m_stylesheet.m_imports.size();
      for(int i = 0; i < nImports; i++)
      {
        Stylesheet stylesheet = (Stylesheet)m_stylesheet.m_imports.elementAt(i);
        bestMatchedRule = stylesheet.findTemplate(transformContext, sourceTree, targetNode, mode,
                                                  false);
        if(null != bestMatchedRule)
        {
          break;
        }
      }
    }

    if(null != conflicts)
    {
      int nConflicts = conflicts.size();
      // System.out.println("nConflicts: "+nConflicts);
      String conflictsString = (!transformContext.m_quietConflictWarnings)
                               ? "" : null;
      for(int i = 0; i < nConflicts; i++)
      {
        MatchPattern2 conflictPat = (MatchPattern2)conflicts.elementAt(i);
        if(0 != i)
        {
          if(!transformContext.m_quietConflictWarnings)
            conflictsString += ", ";

          // Find the furthest one towards the bottom of the document.
          if(conflictPat.m_posInStylesheet > bestMatchedPattern.m_posInStylesheet)
          {
            bestMatchedPattern = conflictPat;
          }
        }
        else
        {
          bestMatchedPattern = conflictPat;
        }
        if(!transformContext.m_quietConflictWarnings)
          conflictsString += "\""+conflictPat.getPattern()+"\"";
      }
      bestMatchedRule = bestMatchedPattern.getTemplate();
      if(!transformContext.m_quietConflictWarnings)
      {
        //conflictsString += " ";
        //conflictsString += "Last found in stylesheet will be used.";
        transformContext.warn(XSLTErrorResources.WG_SPECIFICITY_CONFLICTS, new Object[] {conflictsString});
      }
    }

    return bestMatchedRule;
  } // end findTemplate

  /**
   * Add object to vector if not already there.
   */
  private void addObjectIfNotFound(Object obj, Vector v)
  {
    int n = v.size();
    boolean addIt = true;
    for(int i = 0; i < n; i++)
    {
      if(v.elementAt(i) == obj)
      {
        addIt = false;
        break;
      }
    }
    if(addIt)
    {
      v.addElement(obj);
    }
  }

  /**
   * Given an element type, locate the start of a linked list of
   * possible template matches.
   */
  private MatchPattern2 locateMatchPatternList2(String sourceElementType, boolean tryWildCard)
  {
    MatchPattern2 startMatchList = null;
    Object val = m_patternTable.get(sourceElementType);
    if(null != val)
    {
      startMatchList = (MatchPattern2)val;
    }
    else if(tryWildCard)
    {
      val = m_patternTable.get("*");
      if(null != val)
      {
        startMatchList = (MatchPattern2)val;
      }
    }
    return startMatchList;
  }

  /**
   * Add a template to the template list.
   */
  void addTemplate(ElemTemplate template)
  {
    int pos = 0;
    if(null == m_firstTemplate)
    {
      m_firstTemplate = template;
    }
    else
    {
      ElemTemplateElement next = m_firstTemplate;
      while(null != next)
      {
        if(null == next.m_nextSibling)
        {
          next.m_nextSibling = template;
          template.m_nextSibling = null; // just to play it safe.
          break;
        }
        pos++;
        next = next.m_nextSibling;
      }
    }
    if(null != template.m_name)
    {
      m_namedTemplates.put(template.m_name, template);
    }

    if(null != template.m_matchPattern)
    {
      Vector strings = template.m_matchPattern.getTargetElementStrings();
      if(null != strings)
      {
        int nTargets = strings.size();
        for(int stringIndex = 0; stringIndex < nTargets; stringIndex++)
        {
          String target = (String)strings.elementAt(stringIndex);

          Object newMatchPat = new MatchPattern2(template.m_matchPattern.getPatternString(),
                                                 template.m_matchPattern,
                                                 template, pos,
                                                 target, m_stylesheet);

          // See if there's already one there
          Object val = m_patternTable.get(target);
          if(null == val)
          {
            // System.out.println("putting: "+target);
            m_patternTable.put(target, newMatchPat);
          }
          else
          {
            // find the tail of the list
            MatchPattern2 matchPat = (MatchPattern2)val;
            ((MatchPattern2)newMatchPat).setNext(matchPat);
            m_patternTable.put(target, newMatchPat);
            /*
            MatchPattern2 next;
            while((next = matchPat.getNext()) != null)
            {
            matchPat = next;
            }
            // System.out.println("appending: "+target+" to "+matchPat.getPattern());
            matchPat.setNext((MatchPattern2)newMatchPat);
            */
          }
        }
      }
    }
  }

  /**
   * Locate a macro via the "name" attribute.
   * @exception XSLProcessorException thrown if the active ProblemListener and XMLParserLiaison decide
   * the error condition is severe enough to halt processing.
   */
  ElemTemplateElement findNamedTemplate(String name)
    throws XSLProcessorException
  {
    QName qname = new QName(name, m_stylesheet.m_namespaces);

    return findNamedTemplate(qname);
  }

  /**
   * Locate a macro via the "name" attribute.
   * @exception XSLProcessorException thrown if the active ProblemListener and XMLParserLiaison decide
   * the error condition is severe enough to halt processing.
   */
  ElemTemplateElement findNamedTemplate(QName qname)
    throws XSLProcessorException
  {
    ElemTemplateElement namedTemplate = null;
    Object obj = m_namedTemplates.get(qname);
    if(null == obj)
    {
      int nImports = m_stylesheet.m_imports.size();
      for(int i = 0; i < nImports; i++)
      {
        Stylesheet stylesheet = (Stylesheet)m_stylesheet.m_imports.elementAt(i);
        namedTemplate = stylesheet.findNamedTemplate(qname);
        if(null != namedTemplate)
        {
          break;
        }
      }
      if((null == namedTemplate) && (null != m_stylesheet.m_stylesheetParent))
      {
        Stylesheet stylesheet = m_stylesheet.m_stylesheetParent.getPreviousImport(m_stylesheet);
        if(null != stylesheet)
        {
          namedTemplate = stylesheet.findNamedTemplate(qname);
        }
      }
    }
    else
    {
      namedTemplate = (ElemTemplateElement)obj;
    }

    return namedTemplate;
  }

  /**
   * A class to contain a match pattern and it's corresponding template.
   * This class also defines a node in a match pattern linked list.
   */
  class MatchPattern2 implements Serializable
  {
    /**
     * Construct a match pattern from a pattern and template.
     * @param pat For now a Nodelist that contains old-style element patterns.
     * @param template The node that contains the template for this pattern.
     * @param isMatchPatternsOnly tells if pat param contains only match
     * patterns (for compatibility with old syntax).
     */
    MatchPattern2(String pat, XPath exp, ElemTemplate template, int posInStylesheet,
                  String targetString, Stylesheet stylesheet)
    {
      m_pattern = pat;
      m_template = template;
      m_posInStylesheet = posInStylesheet;
      m_targetString = targetString;
      m_stylesheet = stylesheet;
      m_expression = exp;
    }

    Stylesheet m_stylesheet;

    String m_targetString;

    XPath m_expression;
    public XPath getExpression() { return m_expression; }

    int m_posInStylesheet;

    /**
     * Transient... only used to track priority while
     * processing.
     */
    double m_priority = XPath.MATCH_SCORE_NONE;

    private String m_pattern;
    public String getPattern() { return m_pattern; }

    private ElemTemplate m_template; // ref to the corrisponding template
    public ElemTemplate getTemplate() { return m_template; }

    private MatchPattern2 m_next = null; // null when at end of list.
    public MatchPattern2 getNext() { return m_next; }
    public void setNext(MatchPattern2 mp) { m_next = mp; }
  }

}
