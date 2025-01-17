// $Id: FigMNodeInstance.java,v 1.2 2006/03/02 05:09:00 vauchers Exp $
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

package org.argouml.uml.diagram.deployment.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyVetoException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.argouml.model.Model;
import org.argouml.uml.diagram.ui.FigEdgeModelElement;
import org.argouml.uml.diagram.ui.FigNodeModelElement;
import org.argouml.uml.generator.ParserDisplay;
import org.tigris.gef.base.Geometry;
import org.tigris.gef.base.Selection;
import org.tigris.gef.graph.GraphModel;
import org.tigris.gef.presentation.Fig;
import org.tigris.gef.presentation.FigCube;
import org.tigris.gef.presentation.FigRect;
import org.tigris.gef.presentation.FigText;

/**
 * Class to display graphics for a UML NodeInstance in a diagram.<p>
 *
 * @author 5eichler@informatik.uni-hamburg.de
 */
public class FigMNodeInstance extends FigNodeModelElement {
    
    private int d = 20;
    ////////////////////////////////////////////////////////////////
    // instance variables

    private FigCube cover;

    private int x = 10;
    private int y = 10;
    private int width = 200;
    private int height = 180;
    ////////////////////////////////////////////////////////////////
    // constructors

    /**
     * Main constructor - used for file loading.
     */
    public FigMNodeInstance() {
        setBigPort(new CubePortFigRect(x, y - d, width + d, height + d, d));
        getBigPort().setFilled(false);
        getBigPort().setLineWidth(0);
        cover = new FigCube(x, y, width, height, Color.black, Color.white);
        d = 20;
        //d = cover.getDepth();

        getNameFig().setLineWidth(0);
        getNameFig().setFilled(false);
        getNameFig().setJustification(0);
        getNameFig().setUnderline(true);

        addFig(getBigPort());
        addFig(cover);
        addFig(getStereotypeFig());
        addFig(getNameFig());
    }

    /**
     * Constructor which hooks the new Fig into an existing UML element
     * @param gm ignored
     * @param node the UML element
     */
    public FigMNodeInstance(GraphModel gm, Object node) {
        this();
        setOwner(node);
        if (Model.getFacade().isAClassifier(node)
                && (Model.getFacade().getName(node) != null)) {
            getNameFig().setText(Model.getFacade().getName(node));
        }
    }

    /**
     * @see org.argouml.uml.diagram.ui.FigNodeModelElement#placeString()
     */
    public String placeString() {
        return "new NodeInstance";
    }

    /**
     * @see java.lang.Object#clone()
     */
    public Object clone() {
        FigMNodeInstance figClone = (FigMNodeInstance) super.clone();
        Iterator it = figClone.getFigs().iterator();
        figClone.setBigPort((FigRect) it.next());
        figClone.cover = (FigCube) it.next();
        it.next();
        figClone.setNameFig((FigText) it.next());
        return figClone;
    }

    ////////////////////////////////////////////////////////////////
    // acessors

    /**
     * @see org.tigris.gef.presentation.Fig#setLineColor(java.awt.Color)
     */
    public void setLineColor(Color c) {
        cover.setLineColor(c);
    }
    
    /**
     * @see org.tigris.gef.presentation.Fig#setLineWidth(int)
     */
    public void setLineWidth(int w) {
        cover.setLineWidth(w);
    }
    
    /**
     * @see org.tigris.gef.presentation.Fig#getFilled()
     */
    public boolean getFilled() {
        return cover.getFilled();
    }
    
    /**
     * @see org.tigris.gef.presentation.Fig#setFilled(boolean)
     */
    public void setFilled(boolean f) {
        cover.setFilled(f);
    }

    /**
     * @see org.tigris.gef.presentation.Fig#makeSelection()
     */
    public Selection makeSelection() {
        return new SelectionNodeInstance(this);
    }

    /**
     * @see org.tigris.gef.presentation.Fig#getMinimumSize()
     */
    public Dimension getMinimumSize() {
        Dimension stereoDim = getStereotypeFig().getMinimumSize();
        Dimension nameDim = getNameFig().getMinimumSize();
        int w = Math.max(stereoDim.width, nameDim.width + 1) + 20;
        int h = stereoDim.height + nameDim.height + 20;
        w = Math.max(3 * d, w); // so it still looks like a cube
        h = Math.max(3 * d, h);
        return new Dimension(w, h);
    }

    /**
     * @see org.tigris.gef.presentation.Fig#setBounds(int, int, int, int)
     */
    protected void setBoundsImpl(int x, int y, int w, int h) {
        if (getNameFig() == null) {
            return;
        }

        Rectangle oldBounds = getBounds();
        getBigPort().setBounds(x, y, w, h);
        cover.setBounds(x, y + d, w - d, h - d);

        Dimension stereoDim = getStereotypeFig().getMinimumSize();
        Dimension nameDim = getNameFig().getMinimumSize();
        getNameFig().setBounds(x + 4, y + d + stereoDim.height + 1,
                w - d - 8, nameDim.height);
        getStereotypeFig().setBounds(x + 1, y + d + 1, 
                w - d - 2, stereoDim.height);
        _x = x;
        _y = y;
        _w = w;
        _h = h;
        firePropChange("bounds", oldBounds, getBounds());
        updateEdges();
    }

    /**
     * @see org.argouml.uml.diagram.ui.FigNodeModelElement#updateStereotypeText()
     */
    protected void updateStereotypeText() {
        getStereotypeFig().setOwner(getOwner());
    }

    ////////////////////////////////////////////////////////////////
    // user interaction methods

    /**
     * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
     */
    public void mouseClicked(MouseEvent me) {
        super.mouseClicked(me);
        setLineColor(Color.black);
    }

    /**
     * @see org.tigris.gef.presentation.Fig#setEnclosingFig(org.tigris.gef.presentation.Fig)
     */
    public void setEnclosingFig(Fig encloser) {
        if (getOwner() != null) {
            Object nod = /*(MNodeInstance)*/ getOwner();
            if (encloser != null) {
                Object comp = /*(MComponentInstance)*/ encloser.getOwner();
                if (Model.getFacade().isAComponentInstance(comp)) {
                    if (Model.getFacade().getComponentInstance(nod) != comp) {
                        Model.getCommonBehaviorHelper()
                                .setComponentInstance(nod, comp);
                        super.setEnclosingFig(encloser);
                    }
                }
                else if (Model.getFacade().isANode(comp)) {
                    super.setEnclosingFig(encloser);
                }
            }
            else if (encloser == null) {
                if (Model.getFacade().getComponentInstance(nod) != null) {
                    Model.getCommonBehaviorHelper()
                            .setComponentInstance(nod, null);
                    super.setEnclosingFig(encloser);
                }
            }
        }

        if (getLayer() != null) {
            // elementOrdering(figures);
            List contents = getLayer().getContents();
            Iterator it = contents.iterator();
            while (it.hasNext()) {
                Object o = it.next();
                if (o instanceof FigEdgeModelElement) {
                    FigEdgeModelElement figedge = (FigEdgeModelElement) o;
                    figedge.getLayer().bringToFront(figedge);
                }
            }
        }
    }

    /**
     * @see org.argouml.uml.diagram.ui.FigNodeModelElement#textEdited(org.tigris.gef.presentation.FigText)
     */
    protected void textEdited(FigText ft) throws PropertyVetoException {
        // super.textEdited(ft);
        Object noi = /*(MNodeInstance)*/ getOwner();
        if (ft == getNameFig()) {
            String s = ft.getText().trim();
            // why ever...
            //       if (s.length()>0) {
            //         s = s.substring(0, (s.length() - 1));
            //      }
            ParserDisplay.SINGLETON.parseNodeInstance(noi, s);
        }
    }

    /**
     * @see org.argouml.uml.diagram.ui.FigNodeModelElement#textEditStarted(org.tigris.gef.presentation.FigText)
     */
    protected void textEditStarted(FigText ft) {
        if (ft == getNameFig()) {
            showHelp("parsing.help.fig-nodeinstance");
        }
    }

    /**
     * @see org.tigris.gef.presentation.Fig#getUseTrapRect()
     */
    public boolean getUseTrapRect() {
        return true;
    }

    static final long serialVersionUID = 8822005566372687713L;
    
    protected void modelChanged(PropertyChangeEvent mee) {
        super.modelChanged(mee);
        Object nodeInst =  getOwner();
        if (nodeInst == null) return;
        if ("classifier".equals(mee.getPropertyName())
                && mee.getSource() == nodeInst) {
            updateNameText();
            damage();
        }
    }

    /**
     * @see org.argouml.uml.diagram.ui.FigNodeModelElement#updateNameText()
     */
    protected void updateNameText() {
        Object noi = /*(MNodeInstance)*/ getOwner();
        if (noi == null)
            return;
        String nameStr = "";
        if (Model.getFacade().getName(noi) != null) {
            nameStr = Model.getFacade().getName(noi).trim();
        }
        // construct bases string (comma separated)
        String baseStr = "";
        Collection col = Model.getFacade().getClassifiers(noi);
        if (col != null && col.size() > 0) {
            Iterator it = col.iterator();
            baseStr = Model.getFacade().getName(it.next());
            while (it.hasNext()) {
                baseStr += ", " + Model.getFacade().getName(it.next());
            }
        }

        if (isReadyToEdit()) {
            if ((nameStr.length() == 0) && (baseStr.length() == 0)) {
                getNameFig().setText("");
            } else {
                getNameFig().setText(nameStr.trim() + " : " + baseStr);
            }
        }
        Rectangle r = getBounds();
        setBounds(r.x, r.y, r.width, r.height);
    }

    /**
     * @see org.tigris.gef.presentation.Fig#getClosestPoint(java.awt.Point)
     */
    public Point getClosestPoint(Point anotherPt) {
        Rectangle r = getBounds();
        int xs[] = {r.x,     
                    r.x + d, 
                    r.x + r.width, 
                    r.x + r.width,
                    r.x + r.width - d, 
                    r.x,     
                    r.x};
        int ys[] = {r.y + d, 
                    r.y,    
                    r.y,      
                    r.y + r.height - d,
                    r.y + r.height, 
                    r.y + r.height, 
                    r.y + d};
        Point p = Geometry.ptClosestTo(
                xs, 
                ys,
                7 , anotherPt);
        return p;
    }
} /* end class FigMNodeInstance */
