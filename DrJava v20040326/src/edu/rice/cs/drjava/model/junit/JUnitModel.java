/*BEGIN_COPYRIGHT_BLOCK
 *
 * This file is part of DrJava.  Download the current version of this project:
 * http://sourceforge.net/projects/drjava/ or http://www.drjava.org/
 *
 * DrJava Open Source License
 * 
 * Copyright (C) 2001-2003 JavaPLT group at Rice University (javaplt@rice.edu)
 * All rights reserved.
 *
 * Developed by:   Java Programming Languages Team
 *                 Rice University
 *                 http://www.cs.rice.edu/~javaplt/
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a 
 * copy of this software and associated documentation files (the "Software"),
 * to deal with the Software without restriction, including without 
 * limitation the rights to use, copy, modify, merge, publish, distribute, 
 * sublicense, and/or sell copies of the Software, and to permit persons to 
 * whom the Software is furnished to do so, subject to the following 
 * conditions:
 * 
 *     - Redistributions of source code must retain the above copyright 
 *       notice, this list of conditions and the following disclaimers.
 *     - Redistributions in binary form must reproduce the above copyright 
 *       notice, this list of conditions and the following disclaimers in the
 *       documentation and/or other materials provided with the distribution.
 *     - Neither the names of DrJava, the JavaPLT, Rice University, nor the
 *       names of its contributors may be used to endorse or promote products
 *       derived from this Software without specific prior written permission.
 *     - Products derived from this software may not be called "DrJava" nor
 *       use the term "DrJava" as part of their names without prior written
 *       permission from the JavaPLT group.  For permission, write to
 *       javaplt@rice.edu.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR 
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, 
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL 
 * THE CONTRIBUTORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR 
 * OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, 
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR 
 * OTHER DEALINGS WITH THE SOFTWARE.
 * 
END_COPYRIGHT_BLOCK*/

package edu.rice.cs.drjava.model.junit;

import java.io.IOException;
import edu.rice.cs.drjava.model.OpenDefinitionsDocument;

// TODO: remove this gratuitous swing dependency!
import javax.swing.text.StyledDocument;

public interface JUnitModel {

  //-------------------------- Listener Management --------------------------//

  /**
   * Add a JUnitListener to the model.
   * @param listener a listener that reacts to JUnit events
   */
  public void addListener(JUnitListener listener);

  /**
   * Remove a JUnitListener from the model.  If the listener is not currently
   * listening to this model, this method has no effect.
   * @param listener a listener that reacts to JUnit events
   */
  public void removeListener(JUnitListener listener);

  /**
   * Removes all JUnitListeners from this model.
   */
  public void removeAllListeners();

  //-------------------------------- Triggers --------------------------------//

  /**
   * This is used by test cases and perhaps other things.  We should kill it.
   * TODO: remove this gratuitous swing dependency!
   */
  public StyledDocument getJUnitDocument();

  /**
   * Creates a JUnit test suite over all currently open documents and runs it.
   * If the class file associated with a file is not a test case, it will be
   * ignored.  Synchronized against the compiler model to prevent testing and
   * compiling at the same time, which would create invalid results.
   */
  public void junitAll();

  /**
   * Runs JUnit over a single document.  Synchronized against the compiler model
   * to prevent testing and compiling at the same time, which would create
   * invalid results.
   * @param doc the document to be run under JUnit
   */
  public void junit(OpenDefinitionsDocument doc)
      throws ClassNotFoundException, IOException;

  //----------------------------- Error Results -----------------------------//

  /**
   * Gets the JUnitErrorModel, which contains error info for the last test run.
   */
  public JUnitErrorModel getJUnitErrorModel();

  /**
   * Resets the junit error state to have no errors.
   */
  public void resetJUnitErrors();
}
