// $Id: MultiEditorPane.java,v 1.2 2006/03/02 05:08:21 vauchers Exp $
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

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Vector;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.apache.log4j.Logger;
import org.argouml.application.api.QuadrantPanel;
import org.argouml.ui.targetmanager.TargetEvent;
import org.argouml.ui.targetmanager.TargetListener;
import org.argouml.ui.targetmanager.TargetManager;
import org.argouml.uml.ui.TabModelTarget;
import org.argouml.util.ConfigLoader;
import org.tigris.swidgets.Horizontal;

/**
 * The upper right pane in the ArgoUML user interface.  It may have several
 * tabs with different kinds of "major" editors that allow the user to
 * edit whatever is selected in the ExplorerPane. <p>
 * Currently, there is only the Diagram tab.
 */
public class MultiEditorPane
    extends JPanel
    implements ChangeListener, MouseListener, QuadrantPanel, TargetListener {

    /** logger */
    private static final Logger LOG = Logger.getLogger(MultiEditorPane.class);

    ////////////////////////////////////////////////////////////////
    // instance variables

    private JTabbedPane tabs = new JTabbedPane(SwingConstants.BOTTOM);

    private Vector tabPanels = new Vector();
    private Component lastTab;

    ////////////////////////////////////////////////////////////////
    // constructors

    /**
     * Constructs the MultiEditorPane. This is the pane in which the tabs with
     * the diagrams are drawn in ArgoUML. The MultiEditorPane is a JTabbedPane
     * that holds 0-* JPanels that can show several editors but only show one
     * editor at the moment (argouml version 0.13.3). With this editor diagrams
     * can be edited.
     */
    public MultiEditorPane() {
        LOG.info("making MultiEditorPane");
        ConfigLoader.loadTabs(tabPanels, "multi", Horizontal.getInstance());

        setLayout(new BorderLayout());
        add(tabs, BorderLayout.CENTER);

        // _tabs.addChangeListener(this);
        for (int i = 0; i < tabPanels.size(); i++) {
            String title = "tab";
            JPanel t = (JPanel) tabPanels.elementAt(i);
            if (t instanceof AbstractArgoJPanel) {
                title = ((AbstractArgoJPanel) t).getTitle();
            }
            tabs.addTab("As " + title, t);
            tabs.setEnabledAt(i, false);
            if (t instanceof TargetListener) {
                TargetManager.getInstance()
		    .addTargetListener((TargetListener) t);
            }
        } /* end for */

        tabs.addChangeListener(this);
        tabs.addMouseListener(this);
        setTarget(null);
    }

    /**
     * @see java.awt.Component#getPreferredSize()
     */
    public Dimension getPreferredSize() {
        return new Dimension(400, 500);
    }

    /**
     * @see java.awt.Component#getMinimumSize()
     */
    public Dimension getMinimumSize() {
        return new Dimension(100, 100);
    }

    /**
     * Sets the target of the multieditorpane. The multieditorpane can
     * have several tabs. If a tab is an instance of tabmodeltarget
     * (that is a tab that displays model elements) that tab should
     * display the target if the target is an ArgoDiagram.
     * @param t the target
     */
    private void setTarget(Object t) {
        enableTabs(t);
        for (int i = 0; i < tabs.getTabCount(); i++) {
            Component tab = tabs.getComponentAt(i);
            if (tab.isEnabled()) {
                tabs.setSelectedComponent(tab);
                break;
            }
        }
    }

    /**
     * Enables the tabs on the MultiEditorPane depending on the result of its
     * shouldBeEnabled method.
     * @param t The target for which the shouldBeEnabled test
     * should hold true.
     */
    private void enableTabs(Object t) {
        for (int i = 0; i < tabs.getTabCount(); i++) {
            Component tab = tabs.getComponentAt(i);
            if (tab instanceof TabTarget) {
                TabTarget targetTab = (TabTarget) tab;
                boolean shouldBeEnabled = targetTab.shouldBeEnabled(t);
                tabs.setEnabledAt(i, shouldBeEnabled);
            }
        }
    }

    ////////////////////////////////////////////////////////////////
    // actions

    /**
     * Returns the index of a tab with a certain name in the JTabbedPane which
     * is the component shown by the multieditorpane. At the moment (version
     * 0.13.3 of ArgoUML) there is only 1 tab, the Diagram tab.
     *
     * @param tabName the given tab name
     * @return The index.
     */
    public int getIndexOfNamedTab(String tabName) {
        for (int i = 0; i < tabPanels.size(); i++) {
            String title = tabs.getTitleAt(i);
            if (title != null && title.equals(tabName))
                return i;
        }
        return -1;
    }

    /**
     * Selects a certain tab and shows it. At the moment (version
     * 0.13.3 of ArgoUML) there is only 1 tab, the Diagram tab.
     * @param tabName the name of the tab
     */
    public void selectTabNamed(String tabName) {
        int index = getIndexOfNamedTab(tabName);
        if (index != -1)
            tabs.setSelectedIndex(index);
    }

    /**
     * Selects the next tab in the JTabbedPane. At the moment (version 0.13.3 of
     * ArgoUML) there is only 1 tab, the Diagram tab.
     */
    public void selectNextTab() {
        int size = tabPanels.size();
        int currentTab = tabs.getSelectedIndex();
        for (int i = 1; i < tabPanels.size(); i++) {
            int newTab = (currentTab + i) % size;
            if (tabs.isEnabledAt(newTab)) {
                tabs.setSelectedIndex(newTab);
                return;
            }
        }
    }


    /**
     * Called when the user selects a tab, by clicking or otherwise.
     *
     * @see javax.swing.event.ChangeListener#stateChanged(javax.swing.event.ChangeEvent)
     */
    public void stateChanged(ChangeEvent  e) {
        //TODO: should fire its own event and ProjectBrowser
        //should register a listener
        if (lastTab != null) {
            lastTab.setVisible(false);
        }
        lastTab = tabs.getSelectedComponent();
        LOG.debug(
            "MultiEditorPane state changed:" + lastTab.getClass().getName());
        lastTab.setVisible(true);
        if (lastTab instanceof TabModelTarget)
             ((TabModelTarget) lastTab).refresh();
    }

    /**
     * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
     */
    public void mousePressed(MouseEvent me) {
    }

    /**
     * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
     */
    public void mouseReleased(MouseEvent me) {
    }

    /**
     * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
     */
    public void mouseEntered(MouseEvent me) {
    }

    /**
     * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
     */
    public void mouseExited(MouseEvent me) {
    }

    /**
     * Catches a mouseevent and calls mySingleClick and myDoubleClick if a tab
     * is clicked which is selected.
     * @see java.awt.event.MouseListener#mouseClicked(MouseEvent)
     */
    public void mouseClicked(MouseEvent me) {
        int tab = tabs.getSelectedIndex();
        if (tab != -1) {
            Rectangle tabBounds = tabs.getBoundsAt(tab);
            if (!tabBounds.contains(me.getX(), me.getY()))
                return;
            if (me.getClickCount() == 1) {
                mySingleClick(tab);
                me.consume();
            } else if (me.getClickCount() >= 2) {
                myDoubleClick(tab);
                me.consume();
            }
        }
    }

    /**
     * Called when the user clicks once on a tab.
     *
     * @param tab the tab that was clicked on
     */
    public void mySingleClick(int tab) {
        //TODO: should fire its own event and ProjectBrowser
        //should register a listener
        LOG.debug("single: " + tabs.getComponentAt(tab).toString());
    }

    /**
     * When the user double clicks on a tab, this tab is spawned by this method
     * if it is selected.
     *
     * @param tab The index of the tab.
     */
    public void myDoubleClick(int tab) {
        //TODO: should fire its own event and ProjectBrowser
        //should register a listener
        LOG.debug("double: " + tabs.getComponentAt(tab).toString());
//        JPanel t = (JPanel) tabPanels.elementAt(tab);
        // Currently this feature is disabled for ArgoUML.
//        if (t instanceof AbstractArgoJPanel)
//             ((AbstractArgoJPanel) t).spawn();
    }

    /**
     * @see org.argouml.application.api.QuadrantPanel#getQuadrant()
     */
    public int getQuadrant() {
        return Q_TOP_RIGHT;
    }

    /**
     * @see org.argouml.ui.targetmanager.TargetListener#targetAdded(
     *         org.argouml.ui.targetmanager.TargetEvent)
     */
    public void targetAdded(TargetEvent e) {
	setTarget(e.getNewTarget());
    }

    /**
     * @see org.argouml.ui.targetmanager.TargetListener#targetRemoved(
     *         org.argouml.ui.targetmanager.TargetEvent)
     */
    public void targetRemoved(TargetEvent e) {
	// how to handle empty target lists?  probably the
	// MultiEditorPane should only show an empty pane in that case
	setTarget(e.getNewTarget());
    }

    /**
     * @see org.argouml.ui.targetmanager.TargetListener#targetSet(
     *         org.argouml.ui.targetmanager.TargetEvent)
     */
    public void targetSet(TargetEvent e) {
	setTarget(e.getNewTarget());
    }

    /**
     * @return returns the upper right panel tabs (the diagram tabs)
     */
    protected JTabbedPane getTabs() {
        return tabs;
    }

}

/* end class MultiEditorPane */
