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

import edu.rice.cs.util.UnexpectedException;
import edu.rice.cs.drjava.model.definitions.DefinitionsDocument;
import edu.rice.cs.drjava.model.definitions.reducedmodel.*;

/**
 * Determines if the current line is starting a new statement by
 * searching backwards to see if the previous line was the end
 * of a statement. Specifically,  checks if the previous
 * non-whitespace character not on this line is one of the
 * following: ';', '{', '}', or DOCSTART.
 * <p>
 * Note that characters in comments and quotes are disregarded. 
 *
 * @version $Id: QuestionStartingNewStmt.java,v 1.1 2005/08/05 12:45:56 guehene Exp $
 */
public class QuestionStartingNewStmt extends IndentRuleQuestion {
  
  /**
   * Constructs a new rule to determine if the current line is
   * the start of a new statement.
   * @param yesRule Rule to use if this rule holds
   * @param noRule Rule to use if this rule does not hold
   */
  public QuestionStartingNewStmt(IndentRule yesRule, IndentRule noRule) {
    super(yesRule, noRule);
  }
 
  /**
   * Determines if the previous non-whitespace character not on
   * this line was one of the following: ';', '{', '}' or DOCSTART.
   * Ignores characters in quotes and comments.
   * @param doc DefinitionsDocument containing the line to be indented.
   * @return true if this node's rule holds.
   */
  boolean applyRule(DefinitionsDocument doc) {
    
    char[] delims = {';', '{', '}'};
    int lineStart = doc.getLineStartPos(doc.getCurrentLocation());
    int prevDelimiterPos;
    
    try {
      prevDelimiterPos = doc.findPrevDelimiter(lineStart, delims);
    } catch (BadLocationException e) {
      // Should not happen
      throw new UnexpectedException(e);
    }
    
    // For DOCSTART, imaginary delimiter at position -1
    if(prevDelimiterPos == DefinitionsDocument.ERROR_INDEX) {
      prevDelimiterPos = -1;
    }
    
    // Delimiter must be at the end of its line (ignoring whitespace & comments)
    int firstNonWSAfterDelimiter;
    try {
      firstNonWSAfterDelimiter = doc.getFirstNonWSCharPos(prevDelimiterPos+1);
      // will return ERROR_INDEX if we hit the end of the document
    } catch (BadLocationException e) {
      throw new UnexpectedException(e);
    }
    
    // If the first non-WS character is after the beginning of the line
    // or we reached the end of the document, then we are starting a new statement.
    return (firstNonWSAfterDelimiter >= lineStart
              || firstNonWSAfterDelimiter == DefinitionsDocument.ERROR_INDEX);
  }
}

