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

package edu.rice.cs.drjava.model;

import java.awt.print.PageFormat;
import javax.swing.text.*;
import javax.swing.ListModel;
import java.io.*;
import java.util.*;

import edu.rice.cs.util.swing.FindReplaceMachine;
import edu.rice.cs.util.swing.DocumentIterator;

import edu.rice.cs.drjava.DrJava;
import edu.rice.cs.util.UnexpectedException;
import edu.rice.cs.util.text.SwingDocumentAdapter;
import edu.rice.cs.drjava.model.definitions.*;
import edu.rice.cs.drjava.model.debug.Debugger;
import edu.rice.cs.drjava.model.repl.*;
import edu.rice.cs.drjava.model.junit.JUnitModel;
//import edu.rice.cs.drjava.model.junit.JUnitError;
//import edu.rice.cs.drjava.model.junit.JUnitErrorModel;
import edu.rice.cs.drjava.model.compiler.CompilerModel;

/**
 * Handles the bulk of DrJava's program logic.
 * The UI components interface with the GlobalModel through its public methods,
 * and GlobalModel responds via the GlobalModelListener interface.
 * This removes the dependency on the UI for the logical flow of the program's
 * features.  With the current implementation, we can finally test the compile
 * functionality of DrJava, along with many other things.
 * An ongoing refactoring effort will be moving many GlobalModel functions into
 * more specific sub-interfaces for particular behaviors.
 * @see DefaultGlobalModel, IGetDocuments, ILoadDocuments, CompilerModel,
 *      JUnitModel, JavadocModel
 *
 * @version $Id: GlobalModel.java,v 1.1 2005/08/05 12:45:06 guehene Exp $
 */
public interface GlobalModel extends IGetDocuments, ILoadDocuments {

  //-------------------------- Listener Management --------------------------//

  /**
   * Add a listener to this global model.
   * @param listener a listener that reacts on events generated by the GlobalModel
   */
  public void addListener(GlobalModelListener listener);

  /**
   * Remove a listener from this global model.
   * @param listener a listener that reacts on events generated by the GlobalModel
   */
  public void removeListener(GlobalModelListener listener);

  //------------------------ Feature Model Accessors ------------------------//

  /**
   * Returns the interactions model.
   */
  public DefaultInteractionsModel getInteractionsModel();

  /**
   * Gets the CompilerModel, which provides all methods relating to compilers.
   */
  public CompilerModel getCompilerModel();

  /**
   * Gets the JUnitModel, which provides all methods relating to JUnit testing.
   */
  public JUnitModel getJUnitModel();

  /**
   * Gets the JavadocModel, which provides all methods relating to Javadoc.
   */
  public JavadocModel getJavadocModel();

  /**
   * Gets the Debugger, which interfaces with the integrated debugger.
   */
  public Debugger getDebugger();

  //---------------------------- File Management ----------------------------//

  /**
   * Creates a new document in the definitions pane and
   * adds it to the list of open documents.
   * @return The new open document
   */
  public OpenDefinitionsDocument newFile();

  /**
   * Creates a new junit test case.
   * TODO: Move to JUnitModel?
   * @param name the name of the new test case
   * @param makeSetUp true iff an empty setUp() method should be included
   * @param makeTearDown true iff an empty tearDown() method should be included
   * @return the new open test case
   */
  public OpenDefinitionsDocument newTestCase(String name, boolean makeSetUp, boolean makeTearDown);

  /**
   * Closes an open definitions document, prompting to save if
   * the document has been changed.  Returns whether the file
   * was successfully closed.
   * @return true if the document was closed
   */
  public boolean closeFile(OpenDefinitionsDocument doc);

  /**
   * Attempts to close all open documents.
   * @return true if all documents were closed
   */
  public boolean closeAllFiles();

  /**
   * Reverts all open files.
   * (Not working yet.)
  public void revertAllFiles() throws IOException;
  */

  /**
   * Saves all open documents, prompting when necessary.
   */
  public void saveAllFiles(FileSaveSelector com) throws IOException;

  /**
   * Searches for a file with the given name on the current source roots and the
   * augmented classpath.
   * @param filename Name of the source file to look for
   * @return the file corresponding to the given name, or null if it cannot be found
   */
  public File getSourceFile(String filename);

  /**
   * Searches for a file with the given name on the provided paths.
   * Returns null if the file is not found.
   * @param filename Name of the source file to look for
   * @param paths An array of directories to search
   */
  public File getSourceFileFromPaths(String filename, Vector<File> paths);

  /**
   * Gets an array of all sourceRoots for the open definitions
   * documents, without duplicates.
   * @throws InvalidPackageException if the package statement in one
   *  of the open documents is invalid.
   */
  public File[] getSourceRootSet();

  /**
   * Return the name of the file, or "(untitled)" if no file exists.
   * Does not include the ".java" if it is present.
   */
  public String getDisplayFilename(OpenDefinitionsDocument doc);

  /**
   * Return the absolute path of the file, or "(untitled)" if no file exists.
   */
  public String getDisplayFullPath(OpenDefinitionsDocument doc);

  /**
   * Return the absolute path of the file with the given index,
   * or "(untitled)" if no file exists.
   */
  public String getDisplayFullPath(int index);

  //------------------------------ Definitions ------------------------------//

  /**
   * Fetches the {@link javax.swing.EditorKit} implementation for use
   * in the definitions pane.
   */
  public DefinitionsEditorKit getEditorKit();

  /**
   * Gets a DocumentIterator to allow navigating through open swing Documents.
   * TODO: remove ugly swing dependency.
   */
  public DocumentIterator getDocumentIterator();

  //---------------------------------- I/O ----------------------------------//

  /**
   * Gets the console document.
   */
  public ConsoleDocument getConsoleDocument();

  /**
   * TODO: remove this swing dependency.
   * @return SwingDocumentAdapter in use by the ConsoleDocument.
   */
  public SwingDocumentAdapter getSwingConsoleDocument();

  /**
   * Resets the console.
   * Fires consoleReset() event.
   */
  public void resetConsole();

  /**
   * Prints System.out to the DrJava console.
   */
  public void systemOutPrint(String s);

  /**
   * Prints System.err to the DrJava console.
   */
  public void systemErrPrint(String s);

  /**
   * Sets the listener for any type of single-source input event.
   * The listener can only be changed with the changeInputListener method.
   * @param listener a listener that reacts to input requests
   * @throws IllegalStateException if the input listener is locked
   */
  public void setInputListener(InputListener listener);

  /**
   * Changes the input listener. Takes in the old listener to ensure that
   * the owner of the original listener is aware that it is being changed.
   * @param oldListener the previous listener
   * @param newListener the listener to install
   * @throws IllegalArgumentException if oldListener is not the currently installed listener
   */
  public void changeInputListener(InputListener oldListener, InputListener newListener);

  //----------------------------- Interactions -----------------------------//

  /**
   * Gets the (toolkit-independent) interactions document.
   */
  public InteractionsDocument getInteractionsDocument();

  /**
   * TODO: remove this swing dependency.
   * @return SwingDocumentAdapter in use by the InteractionsDocument.
   */
  public SwingDocumentAdapter getSwingInteractionsDocument();

  /**
   * Clears and resets the interactions pane.
   * First it makes sure it's in the right package given the
   * package specified by the definitions.  If it can't,
   * the package for the interactions becomes the defualt
   * top level. In either case, this method calls a helper
   * which fires the interactionsReset() event.
   */
  public void resetInteractions();

  /**
   * Blocks until the interpreter has registered.
   */
  public void waitForInterpreter();

  /**
   * Interprets the current given text at the prompt in the interactions
   * pane.
   */
  public void interpretCurrentInteraction();

  /**
   * Returns the current classpath in use by the Interpreter JVM.
   */
  public Vector<String> getClasspath();

  // TODO: Move history methods to a more appropriate home.

  /**
   * Interprets the file selected in the FileOpenSelector. Assumes all strings
   * have no trailing whitespace. Interprets the array all at once so if there are
   * any errors, none of the statements after the first erroneous one are processed.
   */
  public void loadHistory(FileOpenSelector selector) throws IOException;

  /**
   * Loads the history/histories from the given selector.
   */
  public InteractionsScriptModel loadHistoryAsScript(FileOpenSelector selector)
    throws IOException, OperationCanceledException;

  /**
   * Clears the interactions history
   */
  public void clearHistory();

  /**
   * Saves the unedited version of the current history to a file
   * @param selector File to save to
   */
  public void saveHistory(FileSaveSelector selector) throws IOException;

  /**
   * Saves the edited version of the current history to a file
   * @param selector File to save to
   * @param editedVersion Edited verison of the history which will be
   * saved to file instead of the lines saved in the history. The saved
   * file will still include any tags needed to recognize it as a saved
   * interactions file.
   */
  public void saveHistory(FileSaveSelector selector, String editedVersion)
    throws IOException;

  /**
   * Returns the entire history as a String with semicolons as needed
   */
  public String getHistoryAsStringWithSemicolons();

  /**
   * Returns the entire history as a String
   */
  public String getHistoryAsString();

  //------------------------------- Debugger -------------------------------//

  /** Called when the debugger wants to print a message. */
  public void printDebugMessage(String s);

  /**
   * Returns an available port number to use for debugging the interactions JVM.
   * @throws IOException if unable to get a valid port number.
   */
  public int getDebugPort() throws IOException;

  //--------------------------------- Misc ---------------------------------//

  // TODO: comment
  public PageFormat getPageFormat();

  // TODO: comment
  public void setPageFormat(PageFormat format);

  /**
   * Exits the program.
   * Only quits if all documents are successfully closed.
   */
  public void quit();
}
