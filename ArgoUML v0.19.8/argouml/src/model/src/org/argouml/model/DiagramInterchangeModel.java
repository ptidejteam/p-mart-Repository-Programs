// $Id: DiagramInterchangeModel.java,v 1.2 2006/03/02 05:08:07 vauchers Exp $
// Copyright (c) 2004-2005 The Regents of the University of California. All
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

package org.argouml.model;

/**
 * An interface to the OMG Diagram Interchange Model. Only implemented
 * by model implementations that wrap a repository that is aware of such
 * a model.<p>
 * 
 * This interface is open for change as DI requirements become more clearly
 * understood.
 * @author Bob Tarling
 */
public interface DiagramInterchangeModel {
    
    public DiDiagram createDiagram(Class type, Object owner);
    public void deleteDiagram(DiDiagram diagram);
    
    public DiElement createElement(DiDiagram diagram, Object modelElement);
    public void deleteElement(DiElement diagram);
    
    // These methods are based on the GraphEvents. These need replacing by
    // more specic meaningful requests of the diagram interface model
    public void nodeAdded(Object source, Object arg);
    public void edgeAdded(Object source, Object arg);
    public void nodeRemoved(Object source, Object arg);
    public void edgeRemoved(Object source, Object arg);
    public void graphChanged(Object source, Object arg);
}