/*
 * FlippingSplitPane.java
 *
 * Copyright 2002 (C) B. K. Oxley (binkley) <binkley@bigfoot.com>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
 * USA.
 *
 * Created on August 18th, 2002.
 */

package hm.binkley.gui;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JSplitPane;
import javax.swing.SwingUtilities;
import javax.swing.plaf.SplitPaneUI;
import javax.swing.plaf.basic.BasicSplitPaneUI;
import pcgen.gui.Utility;

public class FlippingSplitPane extends JSplitPane
{
    private static final String CENTER_ICON_LOCATION = "MediaStop16.gif";
    private static final String FLIP_ICON_LOCATION = "Refresh16.gif";
    private static final String LOCK_ICON_LOCATION = "Bookmarks16.gif";
    private static final String RESET_ICON_LOCATION = "Redo16.gif";

    private boolean locked = false;
    private boolean wasContinuousLayout = false;

    private class CenterActionListener
	implements ActionListener
    {
	public void actionPerformed(ActionEvent e)
	{
	    centerDividerLocations();
	}
    }

    private class CenterMenuItem extends JMenuItem
    {
	CenterMenuItem()
	{
	    super("Center");

	    setMnemonic('C');
	    Utility.maybeSetIcon(this, CENTER_ICON_LOCATION);

	    addActionListener(new CenterActionListener());
	}
    }

    private class FlipActionListener
	implements ActionListener
    {
	public void actionPerformed(ActionEvent e)
	{
	    flipOrientation();
	}
    }

    private class FlipMenuItem extends JMenuItem
    {
	FlipMenuItem()
	{
	    super("Flip");

	    setMnemonic('F');
	    Utility.maybeSetIcon(this, FLIP_ICON_LOCATION);

	    addActionListener(new FlipActionListener());
	}
    }

    private class LockActionListener
	implements ActionListener
    {
	boolean locked;

	LockActionListener(boolean locked)
	{
	    this.locked = locked;
	}

	public void actionPerformed(ActionEvent e)
	{
	    setLocked(locked);
	}
    }

    private class LockMenuItem extends JMenuItem
    {
	LockMenuItem()
	{
	    boolean locked = isLocked();

	    setText(locked ? "Unlock" : "Lock");
	    setMnemonic('L');
	    Utility.maybeSetIcon(this, LOCK_ICON_LOCATION);

	    addActionListener(new LockActionListener(! locked));
	}
    }

    private class ResetActionListener
	implements ActionListener
    {
	public void actionPerformed(ActionEvent e)
	{
	    resetToPreferredSizes();
	}
    }

    private class ResetMenuItem extends JMenuItem
    {
	ResetMenuItem()
	{
	    super("Reset");

	    setMnemonic('R');
	    Utility.maybeSetIcon(this, RESET_ICON_LOCATION);

	    addActionListener(new ResetActionListener());
	}
    }

    private class ContinuousLayoutActionListener
	implements ActionListener
    {
	boolean continuousLayout;

	ContinuousLayoutActionListener(boolean continuousLayout)
	{
	    this.continuousLayout = continuousLayout;
	}

	public void actionPerformed(ActionEvent e)
	{
	    setContinuousLayout(continuousLayout);
	}
    }

    private class ContinuousLayoutMenuItem extends JCheckBoxMenuItem
    {
	ContinuousLayoutMenuItem()
	{
	    super("Smooth resizing");

	    boolean continuousLayout = isContinuousLayout();

	    setMnemonic('S');
	    setSelected(continuousLayout);

	    addActionListener(new ContinuousLayoutActionListener
			      (! continuousLayout));
	}
    }

    private class OneTouchExpandableActionListener
	implements ActionListener
    {
	boolean oneTouchExpandable;

	OneTouchExpandableActionListener(boolean oneTouchExpandable)
	{
	    this.oneTouchExpandable = oneTouchExpandable;
	}

	public void actionPerformed(ActionEvent e)
	{
	    setOneTouchExpandable(oneTouchExpandable);
	}
    }

    private class OneTouchExpandableMenuItem extends JCheckBoxMenuItem
    {
	OneTouchExpandableMenuItem()
	{
	    super("One touch expansion");

	    boolean oneTouchExpandable = isOneTouchExpandable();

	    setMnemonic('O');
	    setSelected(oneTouchExpandable);

	    addActionListener(new OneTouchExpandableActionListener
			      (! oneTouchExpandable));
	}
    }

    private class OptionsMenu extends JMenu
    {
	OptionsMenu()
	{
	    super("Options");

	    setMnemonic('O');

	    this.add(new OneTouchExpandableMenuItem());
	    this.add(new ContinuousLayoutMenuItem());
	}
    }

    protected void addPopupMenuItems(JPopupMenu popupMenu, MouseEvent e)
    {
    }

    private class PopupListener extends MouseAdapter
    {
	public void mousePressed(MouseEvent e)
	{
	    // Work-around: W32 returns false even on right-mouse
	    // clicks
	    if (!(e.isPopupTrigger()
		  || SwingUtilities.isRightMouseButton(e)))
		return;

	    int x = e.getX(), y = e.getY();

	    JPopupMenu popupMenu = new JPopupMenu();
	    
	    if (! isLocked()) {
		popupMenu.add(new CenterMenuItem());
		popupMenu.add(new FlipMenuItem());
		// Just single-click on the divider
		// popupMenu.add(new ResetMenuItem());
		popupMenu.addSeparator();
	    }

	    popupMenu.add(new LockMenuItem());

	    if (! isLocked()) {
		popupMenu.addSeparator();
		popupMenu.add(new OptionsMenu());
	    }
	    
	    addPopupMenuItems(popupMenu, e);

	    popupMenu.show(e.getComponent(), x, y);
	}
    }

    private void setupExtensions()
    {
	SplitPaneUI ui = getUI();

	if (ui instanceof BasicSplitPaneUI) {
	    ((BasicSplitPaneUI) ui).getDivider().addMouseListener
		(new PopupListener());
	}
    }

    public FlippingSplitPane()
    {
	setupExtensions();
    }

    public FlippingSplitPane(int newOrientation)
    {
	super(newOrientation);

	setupExtensions();
    }

    public FlippingSplitPane(int newOrientation,
			     boolean newContinuousLayout)
    {
	super(newOrientation, newContinuousLayout);

	setupExtensions();
    }

    public FlippingSplitPane(int newOrientation,
			     Component newLeftComponent,
			     Component newRightComponent)
    {
	super(newOrientation, newLeftComponent, newRightComponent);

	setupExtensions();
    }

    public FlippingSplitPane(int newOrientation,
			     boolean newContinuousLayout,
			     Component newLeftComponent,
			     Component newRightComponent)
    {
	super(newOrientation, newContinuousLayout, newLeftComponent,
	      newRightComponent);

	setupExtensions();
    }

    private static void maybeCenterDividerLocationsComponent(Component c)
    {
	if (c instanceof FlippingSplitPane)
	    ((FlippingSplitPane) c).centerDividerLocations();
    }

    public void centerDividerLocations()
    {
	setDividerLocation(0.5);
	maybeCenterDividerLocationsComponent(getLeftComponent());
	maybeCenterDividerLocationsComponent(getRightComponent());
    }

    private static void maybeResetToPreferredSizesComponent(Component c)
    {
	if (c instanceof FlippingSplitPane)
	    ((FlippingSplitPane) c).resetToPreferredSizes();
    }

    public void resetToPreferredSizes()
    {
	super.resetToPreferredSizes();
	maybeResetToPreferredSizesComponent(getLeftComponent());
	maybeResetToPreferredSizesComponent(getRightComponent());
    }

    public static int invertOrientation(int orientation)
    {
	return orientation == HORIZONTAL_SPLIT
	    ? VERTICAL_SPLIT : HORIZONTAL_SPLIT;
    }

    private static void maybeFlipComponent(Component c)
    {
	if (! (c instanceof FlippingSplitPane)) return;

	FlippingSplitPane pane = (FlippingSplitPane) c;

	pane.setOrientation(invertOrientation(pane.getOrientation()));
    }

    public void flipOrientation()
    {
	super.setOrientation(invertOrientation(getOrientation()));
	maybeFlipComponent(getLeftComponent());
	maybeFlipComponent(getRightComponent());
    }

    private static void maybeSetOrientationComponent
	(Component c, int newOrientation)
    {
	if (c instanceof FlippingSplitPane)
	    ((FlippingSplitPane) c).setOrientation(newOrientation);
    }

    public void setOrientation(int newOrientation)
    {
	if (newOrientation == getOrientation()) return;

	super.setOrientation(newOrientation);

	int subOrientation = invertOrientation(newOrientation);
	maybeSetOrientationComponent(getLeftComponent(), subOrientation);
	maybeSetOrientationComponent(getRightComponent(), subOrientation);
    }

    private static void maybeSetOneTouchExpandableComponent
	(Component c, boolean newOneTouchExpandable)
    {
	if (c instanceof FlippingSplitPane)
	    ((FlippingSplitPane) c).setOneTouchExpandable
		(newOneTouchExpandable);
    }

    public void setOneTouchExpandable(boolean newOneTouchExpandable)
    {
	if (newOneTouchExpandable == isOneTouchExpandable()) return;

	super.setOneTouchExpandable(newOneTouchExpandable);
	maybeSetOneTouchExpandableComponent
	    (getLeftComponent(), newOneTouchExpandable);
	maybeSetOneTouchExpandableComponent
	    (getRightComponent(), newOneTouchExpandable);
    }

    private static void maybeSetContinuousLayoutComponent
	(Component c, boolean newContinuousLayout)
    {
	if (c instanceof FlippingSplitPane)
	    ((FlippingSplitPane) c).setContinuousLayout
		(newContinuousLayout);
    }

    public void setContinuousLayout(boolean newContinuousLayout)
    {
	if (newContinuousLayout == isContinuousLayout()) return;

	super.setContinuousLayout(newContinuousLayout);
	maybeSetContinuousLayoutComponent
	    (getLeftComponent(), newContinuousLayout);
	maybeSetContinuousLayoutComponent
	    (getRightComponent(), newContinuousLayout);
    }

    public boolean isLocked()
    {
	return locked;
    }

    private static void maybeSetLockedComponent(Component c,
						boolean locked)
    {
	if (c instanceof FlippingSplitPane)
	    ((FlippingSplitPane) c).setLocked(locked);
    }

    public void setLocked(boolean locked)
    {
	if (locked == isLocked()) return;

	// Workaround so that you can't drag the divider when locked.
	if (this.locked = locked) {
	    wasContinuousLayout = isContinuousLayout();
	    setContinuousLayout(true);
	}

	else setContinuousLayout(wasContinuousLayout);

	maybeSetLockedComponent(getLeftComponent(), isLocked());
	maybeSetLockedComponent(getRightComponent(), isLocked());
    }

    public void setDividerLocation(int location)
    {
	if (isLocked())
	    super.setDividerLocation(getLastDividerLocation());
	else super.setDividerLocation(location);
    }
}
