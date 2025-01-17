/*BEGIN_COPYRIGHT_BLOCK
 *
 * This file is a part of DrJava. Current versions of this project are available
 * at http://sourceforge.net/projects/drjava
 *
 * Copyright (C) 2001-2002 JavaPLT group at Rice University (javaplt@rice.edu)
 * 
 * DrJava is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * DrJava is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 * or see http://www.gnu.org/licenses/gpl.html
 *
 * In addition, as a special exception, the JavaPLT group at Rice University
 * (javaplt@rice.edu) gives permission to link the code of DrJava with
 * the classes in the gj.util package, even if they are provided in binary-only
 * form, and distribute linked combinations including the DrJava and the
 * gj.util package. You must obey the GNU General Public License in all
 * respects for all of the code used other than these classes in the gj.util
 * package: Dictionary, HashtableEntry, ValueEnumerator, Enumeration,
 * KeyEnumerator, Vector, Hashtable, Stack, VectorEnumerator.
 *
 * If you modify this file, you may extend this exception to your version of the
 * file, but you are not obligated to do so. If you do not wish to
 * do so, delete this exception statement from your version. (However, the
 * present version of DrJava depends on these classes, so you'd want to
 * remove the dependency first!)
 *
END_COPYRIGHT_BLOCK*/

package edu.rice.cs.drjava.model.definitions.indent;

import javax.swing.text.BadLocationException;
import edu.rice.cs.drjava.model.definitions.DefinitionsDocument;
import edu.rice.cs.drjava.model.definitions.reducedmodel.*;
import edu.rice.cs.util.UnexpectedException;

/**
 * Determines whether or not the closest non-whitespace character
 * previous to the start of the current line (excluding any characters
 * inside comments or strings) is an open brace.
 *
 * @version $Id: QuestionStartAfterOpenBrace.java,v 1.1 2005/08/05 12:45:56 guehene Exp $
 */
public class QuestionStartAfterOpenBrace extends IndentRuleQuestion 
{
  /**
  * @param yesRule The decision subtree for the case that this rule applies 
  * in the current context.
  * @param noRule The decision subtree for the case that this rule does not
  * apply in the current context.
  */
  public QuestionStartAfterOpenBrace(IndentRule yesRule, IndentRule noRule)
  {
    super(yesRule, noRule);
  }
  
  /**
   * @param doc The DefinitionsDocument containing the current line.
   * @return True the closest non-whitespace character
   * previous to the start of the current line (excluding any characters
   * inside comments or strings) is an open brace.
   */
  boolean applyRule(DefinitionsDocument doc)
  {

    int here = doc.getCurrentLocation();
    int origin = doc.getReduced().absOffset();
    int lineStart = doc.getLineStartPos(doc.getCurrentLocation());
    
    // Get brace for start of line
    doc.getReduced().move(lineStart - origin);
    IndentInfo info = doc.getReduced().getIndentInformation();
    doc.getReduced().move(origin - lineStart);
    
    if ((!info.braceType.equals(IndentInfo.openSquiggly)) ||
        (info.distToBrace < 0)) {
      // Precondition not met: we should have a brace
      return false;
    }
    int bracePos = lineStart - info.distToBrace;    
    
    // Get brace's end of line
    int braceEndLinePos = doc.getLineEndPos(bracePos);
    
    // Get position of next non-WS char (not in comments)
    int nextNonWS = -1;
    try {
      nextNonWS = doc.getFirstNonWSCharPos(braceEndLinePos);
    }
    catch (BadLocationException e) {
      // This shouldn't happen
      throw new UnexpectedException(e);
    }
    
    if (nextNonWS == DefinitionsDocument.ERROR_INDEX) {
      return true;
    }
    
    return (nextNonWS >= lineStart);
  }
}
