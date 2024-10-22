// $Id: GraphChangeAdapter.java,v 1.2 2006/03/02 05:08:21 vauchers Exp $
// Copyright (c) 1996-2005 The Regents of the University of California. All
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

package org.argouml.ui;

import org.argouml.model.DiDiagram;
import org.argouml.model.DiElement;
import org.argouml.model.Model;
import org.argouml.uml.diagram.UMLMutableGraphSupport;
import org.tigris.gef.graph.GraphEvent;
import org.tigris.gef.graph.GraphListener;
import org.tigris.gef.graph.GraphModel;
import org.tigris.gef.presentation.Fig;

/**
 * Adapts changes in the Diagram subsystem (the graph presentation layer)
 * to changes in the Model subsyetm (diagram interchange model).
 * The curent implementaion does this by listeneing to graph events and
 * forwarding those as specific calls to the DiagramInterchangeModel.
 * This should be changed to a more standard Adapter architecture that
 * provides an interface for Figs and GraphModels to call only when required.
 * 
 * @author Bob Tarling
 * @stereotype singleton
 */
public class GraphChangeAdapter implements GraphListener {

    private static final GraphChangeAdapter INSTANCE =
        new GraphChangeAdapter();
    
    public static GraphChangeAdapter getInstance() {
        return INSTANCE;
    }
    
    /**
     * The constructor of a singleton is private
     */
    private GraphChangeAdapter() {
    }
    
    public DiDiagram createDiagram(Class type, Object owner) {
        if (Model.getDiagramInterchangeModel() != null) {
            return Model.getDiagramInterchangeModel()
                .createDiagram(type, owner);
        } 
        return null;
    }
    
    
    public void removeDiagram(DiDiagram dd) {
        if (Model.getDiagramInterchangeModel() != null) {
            Model.getDiagramInterchangeModel().deleteDiagram(dd);
        }
    }
    
    public DiElement createElement(GraphModel gm, Object node) {
        if (Model.getDiagramInterchangeModel() != null) {
            return Model.getDiagramInterchangeModel().createElement(
                ((UMLMutableGraphSupport) gm).getDiDiagram(), node);
        }
        return null;
    }
    
    public void removeElement(DiElement element) {
        if (Model.getDiagramInterchangeModel() != null) {
            Model.getDiagramInterchangeModel().deleteElement(element);
        }
    }
    
    

    
    public void nodeAdded(GraphEvent e) {
        Object source = e.getSource();
        Object arg = e.getArg();
        if (source instanceof Fig) source = ((Fig) source).getOwner();
        if (arg instanceof Fig) arg = ((Fig) arg).getOwner();
        Model.getDiagramInterchangeModel().nodeAdded(source, arg);
    }

    public void edgeAdded(GraphEvent e) {
        Object source = e.getSource();
        Object arg = e.getArg();
        if (source instanceof Fig) source = ((Fig) source).getOwner();
        if (arg instanceof Fig) arg = ((Fig) arg).getOwner();
        Model.getDiagramInterchangeModel().edgeAdded(source, arg);
    }

    public void nodeRemoved(GraphEvent e) {
        Object source = e.getSource();
        Object arg = e.getArg();
        if (source instanceof Fig) source = ((Fig) source).getOwner();
        if (arg instanceof Fig) arg = ((Fig) arg).getOwner();
        Model.getDiagramInterchangeModel().nodeRemoved(source, arg);
    }

    public void edgeRemoved(GraphEvent e) {
        Object source = e.getSource();
        Object arg = e.getArg();
        if (source instanceof Fig) source = ((Fig) source).getOwner();
        if (arg instanceof Fig) arg = ((Fig) arg).getOwner();
        Model.getDiagramInterchangeModel().edgeRemoved(source, arg);
    }

    public void graphChanged(GraphEvent e) {
        Object source = e.getSource();
        Object arg = e.getArg();
        if (source instanceof Fig) source = ((Fig) source).getOwner();
        if (arg instanceof Fig) arg = ((Fig) arg).getOwner();
        Model.getDiagramInterchangeModel().graphChanged(source, arg);
    }
}