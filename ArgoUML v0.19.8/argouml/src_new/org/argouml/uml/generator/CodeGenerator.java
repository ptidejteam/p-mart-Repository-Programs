// $Id: CodeGenerator.java,v 1.2 2006/03/02 05:07:52 vauchers Exp $
// Copyright (c) 2005 The Regents of the University of California. All
// Rights Reserved. Permission to use, copy, modify, and distribute this
// software and its documentation without fee, and without a written
// agreement is hereby granted, provided that the above copyright notice
// and this paragraph appear in all copies.  This software program and
// documentation are copyrighted by The Regents of the University of
// California. The software program and documentation are supplied "AS
// IS", without any accompanying services from The Regents. The Regents
// does not warrant that the operation of the program will be
// uninterrupted or error-free. The end-user understands that the program
// was developed for research purposes and is advised not to rely
// exclusively on the program for any reason.  IN NO EVENT SHALL THE
// UNIVERSITY OF CALIFORNIA BE LIABLE TO ANY PARTY FOR DIRECT, INDIRECT,
// SPECIAL, INCIDENTAL, OR CONSEQUENTIAL DAMAGES, INCLUDING LOST PROFITS,
// ARISING OUT OF THE USE OF THIS SOFTWARE AND ITS DOCUMENTATION, EVEN IF
// THE UNIVERSITY OF CALIFORNIA HAS BEEN ADVISED OF THE POSSIBILITY OF
// SUCH DAMAGE. THE UNIVERSITY OF CALIFORNIA SPECIFICALLY DISCLAIMS ANY
// WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
// MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE SOFTWARE
// PROVIDED HEREUNDER IS ON AN "AS IS" BASIS, AND THE UNIVERSITY OF
// CALIFORNIA HAS NO OBLIGATIONS TO PROVIDE MAINTENANCE, SUPPORT,
// UPDATES, ENHANCEMENTS, OR MODIFICATIONS.

package org.argouml.uml.generator;

import java.util.Collection;

/**
 * Defines the methods to generate source code from the model.
 * Each class providing code generation functionality should implement
 * this.
 * Replaces the FileGenerator interface.
 */
public interface CodeGenerator {
    /**
     * The file seperator for this operating system.
     */
    public static final String FILE_SEPARATOR =
        System.getProperty("file.separator");

    // FIXME: maybe convert all Collections of modelelements
    // into Sets, because they shall not contain duplicate elements.
    
    /**
     * Generate code for the specified classifiers. If generation of
     * dependencies is requested, then every file the specified elements
     * depends on is generated too (e.g. if the class MyClass has an attribute
     * of type OtherClass, then files for OtherClass are generated too).
     * 
     * @param elements the UML model elements to generate code for.
     * @param deps Recursively generate dependency files too. 
     * @return A collection of SourceUnit objects. The collection may be empty
     * if no file is generated.
     */
    Collection generate(Collection elements, boolean deps);

    /**
     * Generate files for the specified classifiers.
     * @see #generate(Collection, boolean)
     * @param elements the UML model elements to generate code for.
     * @param path The source base path.
     * @param deps Recursively generate dependency files too.
     * @return The filenames (with relative path) as a collection of Strings.
     * The collection may be empty if no file will be generated.
     */
    Collection generateFiles(Collection elements, String path, boolean deps);

    /**
     * Returns a list of files that will be generated from the specified
     * modelelements.
     * @see #generate(Collection, boolean)
     * @param elements the UML model elements to generate code for.
     * @param deps Recursively generate dependency files too. 
     * @return The filenames (with relative path) as a collection of Strings.
     * The collection may be empty if no file will be generated.
     */
    Collection generateFileList(Collection elements, boolean deps);
}
