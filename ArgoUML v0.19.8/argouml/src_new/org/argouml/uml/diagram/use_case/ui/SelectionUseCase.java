// $Id: SelectionUseCase.java,v 1.2 2006/03/02 05:08:56 vauchers Exp $
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

package org.argouml.uml.diagram.use_case.ui;

import java.awt.Graphics;
import java.awt.Rectangle;

import javax.swing.Icon;

import org.apache.log4j.Logger;
import org.argouml.application.helpers.ResourceLoaderWrapper;
import org.argouml.model.Model;
import org.argouml.uml.diagram.ui.SelectionNodeClarifiers;
import org.tigris.gef.base.Editor;
import org.tigris.gef.base.Globals;
import org.tigris.gef.base.ModeCreateEdgeAndNode;
import org.tigris.gef.base.ModeManager;
import org.tigris.gef.base.ModeModify;
import org.tigris.gef.base.SelectionManager;
import org.tigris.gef.graph.MutableGraphModel;
import org.tigris.gef.presentation.Fig;
import org.tigris.gef.presentation.FigNode;
import org.tigris.gef.presentation.Handle;

/**
 * @author jrobbins@ics.uci.edu
 */
public class SelectionUseCase extends SelectionNodeClarifiers {
    /**
     * Logger.
     */
    private static final Logger LOG =
        Logger.getLogger(SelectionUseCase.class);
    ////////////////////////////////////////////////////////////////
    // constants
    private static Icon inherit =
        ResourceLoaderWrapper.lookupIconResource("Generalization");
    private static Icon assoc =
        ResourceLoaderWrapper.lookupIconResource("Association");

    ////////////////////////////////////////////////////////////////
    // constructors

    /**
     * Construct a new SelectionUseCase for the given Fig.
     *
     * @param f The given Fig.
     */
    public SelectionUseCase(Fig f) {
        super(f);
    }

    /**
     * Return a handle ID for the handle under the mouse, or -1 if
     * none. TODO: in the future, return a Handle instance or
     * null. <p>
     *  <pre>
     *   0-------1-------2
     *   |               |
     *   3               4
     *   |               |
     *   5-------6-------7
     * </pre>
     *
     * @see org.tigris.gef.base.Selection#hitHandle(java.awt.Rectangle,
     * org.tigris.gef.presentation.Handle)
     */
    public void hitHandle(Rectangle r, Handle h) {
        super.hitHandle(r, h);
        if (h.index != -1) {
            return;
        }
        if (!isPaintButtons()) {
            return;
        }
        Editor ce = Globals.curEditor();
        SelectionManager sm = ce.getSelectionManager();
        if (sm.size() != 1) {
            return;
        }
        ModeManager mm = ce.getModeManager();
        if (mm.includes(ModeModify.class) && getPressedButton() == -1) {
            return;
        }
        int cx = getContent().getX();
        int cy = getContent().getY();
        int cw = getContent().getWidth();
        int ch = getContent().getHeight();
        int iw = inherit.getIconWidth();
        int ih = inherit.getIconHeight();
        int aw = assoc.getIconWidth();
        int ah = assoc.getIconHeight();
        if (hitAbove(cx + cw / 2, cy, iw, ih, r)) {
            h.index = 10;
            h.instructions = "Add a more general use case";
        } else if (hitBelow(cx + cw / 2, cy + ch, iw, ih, r)) {
            h.index = 11;
            h.instructions = "Add a specialized use case";
        } else if (hitLeft(cx + cw, cy + ch / 2, aw, ah, r)) {
            h.index = 12;
            h.instructions = "Add an associated actor";
        } else if (hitRight(cx, cy + ch / 2, aw, ah, r)) {
            h.index = 13;
            h.instructions = "Add an associated actor";
        } else {
            h.index = -1;
            h.instructions = "Move object(s)";
        }
    }

    /**
     * @see org.tigris.gef.base.SelectionButtons#paintButtons(Graphics)
     */
    public void paintButtons(Graphics g) {
        int cx = getContent().getX();
        int cy = getContent().getY();
        int cw = getContent().getWidth();
        int ch = getContent().getHeight();
        paintButtonAbove(inherit, g, cx + cw / 2, cy, 10);
        paintButtonBelow(inherit, g, cx + cw / 2, cy + ch, 11);
        paintButtonLeft(assoc, g, cx + cw, cy + ch / 2, 12);
        paintButtonRight(assoc, g, cx, cy + ch / 2, 13);
    }

    /**
     * @see org.tigris.gef.base.Selection#dragHandle(int, int, int, int,
     * org.tigris.gef.presentation.Handle)
     */
    public void dragHandle(int mX, int mY, int anX, int anY, Handle hand) {
        if (hand.index < 10) {
            setPaintButtons(false);
            super.dragHandle(mX, mY, anX, anY, hand);
            return;
        }
        int cx = getContent().getX(), cy = getContent().getY();
        int cw = getContent().getWidth(), ch = getContent().getHeight();
        Object edgeType = null;
        Object nodeType = null;
        if (hand.index == 10 || hand.index == 11) {
            nodeType = Model.getMetaTypes().getUseCase();
        } else {
            nodeType = Model.getMetaTypes().getActor();
        }

        int bx = mX, by = mY;
        boolean reverse = false;
        switch (hand.index) {
	case 10 : //add superclass
	    edgeType = Model.getMetaTypes().getGeneralization();
	    by = cy;
	    bx = cx + cw / 2;
	    break;
	case 11 : //add subclass
	    edgeType = Model.getMetaTypes().getGeneralization();
	    reverse = true;
	    by = cy + ch;
	    bx = cx + cw / 2;
	    break;
	case 12 : //add assoc
	    edgeType = Model.getMetaTypes().getAssociation();
	    by = cy + ch / 2;
	    bx = cx + cw;
	    break;
	case 13 : // add assoc
	    edgeType = Model.getMetaTypes().getAssociation();
	    reverse = true;
	    by = cy + ch / 2;
	    bx = cx;
	    break;
	default :
	    LOG.warn("invalid handle number");
	    break;
        }
        if (edgeType != null && nodeType != null) {
            Editor ce = Globals.curEditor();
            ModeCreateEdgeAndNode m =
                new ModeCreateEdgeAndNode(ce, edgeType, nodeType, false);
            m.setup((FigNode) getContent(), getContent().getOwner(),
                    bx, by, reverse);
            ce.pushMode(m);
        }

    }




    /**
     * @see org.tigris.gef.base.SelectionButtons#createEdgeAbove(
     *         org.tigris.gef.graph.MutableGraphModel, java.lang.Object)
     */
    protected Object createEdgeAbove(MutableGraphModel gm, Object newNode) {
        return gm.connect(getContent().getOwner(), newNode,
                // TODO: Remove when GEF with this fixed and incorporated
                // http://gef.tigris.org/issues/show_bug.cgi?id=203
               (Class) Model.getMetaTypes().getGeneralization());
    }

    /**
     * @see org.tigris.gef.base.SelectionButtons#createEdgeLeft(
     *         org.tigris.gef.graph.MutableGraphModel, java.lang.Object)
     */
    protected Object createEdgeLeft(MutableGraphModel gm, Object newNode) {
        return gm.connect(newNode, getContent().getOwner(),
            // TODO: Remove (Class) when GEF with this fixed and incorporated
            // http://gef.tigris.org/issues/show_bug.cgi?id=203
            (Class) Model.getMetaTypes().getAssociation());
    }

    /**
     * @see org.tigris.gef.base.SelectionButtons#createEdgeRight(
     *         org.tigris.gef.graph.MutableGraphModel, java.lang.Object)
     */
    protected Object createEdgeRight(MutableGraphModel gm, Object newNode) {
        return gm.connect(getContent().getOwner(), newNode,
            // TODO: Remove when GEF with this fixed and incorporated
            // http://gef.tigris.org/issues/show_bug.cgi?id=203
			  (Class) Model.getMetaTypes().getAssociation());
    }

    /**
     * @see org.tigris.gef.base.SelectionButtons#createEdgeUnder(
     *         org.tigris.gef.graph.MutableGraphModel, java.lang.Object)
     */
    protected Object createEdgeUnder(MutableGraphModel gm, Object newNode) {
        return gm.connect(newNode, getContent().getOwner(),
            // TODO: Remove when GEF with this fixed and incorporated
            // http://gef.tigris.org/issues/show_bug.cgi?id=203
			  (Class) Model.getMetaTypes().getGeneralization());
    }

    /**
     * @see org.tigris.gef.base.SelectionButtons#getNewNode(int)
     */
    protected Object getNewNode(int buttonCode) {
        Object newNode = null;
        if (buttonCode == 10 || buttonCode == 11) {
            newNode = Model.getUseCasesFactory().createUseCase();
        } else {
            newNode = Model.getUseCasesFactory().createActor();
        }
        return newNode;
    }

} /* end class SelectionUseCase */
