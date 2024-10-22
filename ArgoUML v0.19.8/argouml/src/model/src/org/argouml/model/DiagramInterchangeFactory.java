// $Id: DiagramInterchangeFactory.java,v 1.2 2006/03/02 05:08:07 vauchers Exp $
// Copyright (c) 2005 The Regents of the University of California. All
// Rights Reserved. Permission to use, copy, modify, and distribute this
// software and its documentation without fee, and without a written
// agreement is hereby granted, provided that the above copyright notice
// and this paragraph appear in all copies. This software program and
// documentation are copyrighted by The Regents of the University of
// California. The software program and documentation are supplied "AS
// IS", without any accompanying services from The Regents. The Regents
// does not warrant that the operation of the program will be
// uninterrupted or error-free. The end-user understands that the program
// was developed for research purposes and is advised not to rely
// exclusively on the program for any reason. IN NO EVENT SHALL THE
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

import java.util.Collection;
import java.util.List;

public interface DiagramInterchangeFactory {

    List getModelDiagram();

    void setModelDiagram(List diagrams);

    /**
     * Return true of the given ModelElement is the owner of the diagram.
     */
    boolean isDiagramOwner(Object modelElement, Object diagram);

    /**
     * Return true of the given ModelElement is the owner of the diagram.
     */
    Object getDiagramElementOwner(Object diagram);

    /**
     *
     * @return
     */
    Object createDiagram();

    /**
     *
     * @return
     */
    Object createDiagramLink();

    /**
     * 
     * @return
     */
    Object createEllipse();

    /**
     * 
     * @return
     */
    Object createGraphConnector();

    /**
     *
     * @return
     */
    Object createGraphEdge();

    /**
     *
     * @return
     */
    Object createGraphNode();

    /**
     * 
     * @return
     */
    Object createImage();

    /**
     * 
     * @return
     */
    Object createPolyline();

    /**
     * 
     * @return
     */
    Object createProperty();

    /**
     * 
     * @return
     */
    Object createReference();

    /**
     * 
     * @return
     */
    Object createSimpleSemanticModelElement();

    /**
     * 
     * @return
     */
    Object createTextElement();

    /**
     * 
     * @return
     */
    Object createUml1SemanticModelBridge();

    /**
     * Creates an instance of BezierPoint structure type.
     * @param base
     * @param control1
     * @param control2
     * @return
     */
    Object createBezierPoint(Object base, Object control1,
            Object control2);

    /**
     * Creates an instance of Dimension structure type.
     * @param width
     * @param height
     * @return
     */
    Object createDimension(double width, double height);

    /**
     * Creates an instance of Point structure type.
     * @param x
     * @param y
     * @return
     */
    Object createPoint(double x, double y);

    /**
     *
     * @param modelElement
     * @param diagram
     * @return
     */
    Object buildUml1SemanticModelBridge(Object modelElement,
            Object diagramOrGraphElement);

    Object buildSimpleSemanticModelElement(Object grafElement,
            Object diagram, String presentation, String typeInfo);

    /**
     * Build a diagram.
     *
     * @param model
     * @return A new diagram
     */
    Object buildDiagram(Object model);

    Object buildGraphNode(Object parentGraphElement,
            Object modelElement);

    Object buildProperty(String key, String value);

    // ADiagramElementProperty delegate methods

    /**
     * @see org.omg.uml.diagraminterchange.ADiagramElementProperty#add(
     *         org.omg.uml.diagraminterchange.DiagramElement,
     *         org.omg.uml.diagraminterchange.Property)
     */
    boolean addProperty(Object arg0, Object arg1);

    /**
     * @see org.omg.uml.diagraminterchange.ADiagramElementProperty#exists(
     *         org.omg.uml.diagraminterchange.DiagramElement,
     *         org.omg.uml.diagraminterchange.Property)
     */
    boolean existsProperty(Object arg0, Object arg1);

    /**
     * @see org.omg.uml.diagraminterchange.ADiagramElementProperty#getProperty(
     *         org.omg.uml.diagraminterchange.DiagramElement)
     */
    Collection getProperties(Object arg0);

    /**
     * @see org.omg.uml.diagraminterchange.ADiagramElementProperty#remove(
     *         org.omg.uml.diagraminterchange.DiagramElement,
     *         org.omg.uml.diagraminterchange.Property)
     */
    boolean removeProperty(Object arg0, Object arg1);

    //easy interface to properties
    boolean hasProperty(Object diagramElement,
            String propertyName);

    void setProperty(Object diagramElement, String key,
            String value);

    String getProperty(Object diagramElement, String key);

}
