/*
 * DoubleSplitPane.java
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

package pcgen.gui.panes; // hm.binkley.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Component;
import java.awt.event.MouseEvent;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JSplitPane;
import javax.swing.SwingConstants;

public class DoubleSplitPane extends SplitPane
    implements SwingConstants
{
    private class TiedSplitPane extends SplitPane
    {
	private TiedSplitPane partner;
	// This is hosing the JDK's graphics! XXX
	private boolean joined = false;

	private class JoinActionListener
	    implements ActionListener
	{
	    boolean joined;

	    JoinActionListener(boolean joined)
	    {
		this.joined = joined;
	    }

	    public void actionPerformed(ActionEvent e)
	    {
		setJoined(joined);
	    }
	}

	private class JoinMenuItem extends JMenuItem
	{
	    JoinMenuItem()
	    {
		boolean joined = isJoined();

		setText(joined ? "Unjoin" : "Join");
		setMnemonic('J');
		setIcon(Utilities.JOIN_ICON);

		addActionListener(new JoinActionListener (! joined));
	    }
	}

	protected void addPopupMenuItems(JPopupMenu popupMenu, MouseEvent e)
	{
	    if (DoubleSplitPane.this.isEnabled())
		popupMenu.add(new JoinMenuItem(), 4);
	}

	public TiedSplitPane(int newOrientation,
		      boolean newContinuousLayout,
		      Component newLeftComponent,
		      Component newRightComponent)
	{
	    super(newOrientation, newContinuousLayout, newLeftComponent,
		  newRightComponent);
	}

	public void setPartner(TiedSplitPane partner)
	{
	    this.partner = partner;
	}

	public boolean isJoined()
	{
	    return joined;
	}

	public void setJoined(boolean joined)
	{
	    if (joined == this.joined) return;

	    // Snap our partner into our position.
	    if (joined)
		partner.superSetDividerLocation(getDividerLocation());

	    partner.joined = this.joined = joined;
	}

	/** Since object.super.method() is bad syntax. */
	private void superSetDividerLocation(int location)
	{
	    super.setDividerLocation(location);
	}

	public void setDividerLocation(int location)
	{
	    super.setDividerLocation(location);
	    if (joined) partner.superSetDividerLocation(location);
	}
    }

    private void setupTiedPanes(Component newNorthWestComponent,
				Component newNorthEastComponent,
				Component newSouthWestComponent,
				Component newSouthEastComponent)
    {
	int orientation = invertOrientation(getOrientation());
	boolean continuousLayout = isContinuousLayout();

	TiedSplitPane left = new TiedSplitPane
	    (orientation, continuousLayout,
	     newNorthWestComponent, newNorthEastComponent),
	    right = new TiedSplitPane
	    (orientation, continuousLayout,
	     newSouthWestComponent, newSouthEastComponent);

	left.setPartner(right);
	right.setPartner(left);

	setLeftComponent(left);
	setRightComponent(right);
    }

    private void setupTiedPanes()
    {
	setupTiedPanes(new JButton("Northwest Button"),
		       new JButton("Northeast Button"),
		       new JButton("Southwest Button"),
		       new JButton("Southeast Button"));
    }

    public DoubleSplitPane()
    {
	setupTiedPanes();
    }

    public DoubleSplitPane(int newOrientation)
    {
	super(newOrientation);

	setupTiedPanes();
    }


    public DoubleSplitPane(int newOrientation,
			   Component newNorthWestComponent,
			   Component newNorthEastComponent,
			   Component newSouthWestComponent,
			   Component newSouthEastComponent)
    {
	super(newOrientation);

	setupTiedPanes(newNorthWestComponent, newNorthEastComponent,
		       newSouthWestComponent, newSouthEastComponent);
    }

    public DoubleSplitPane(int newOrientation, boolean newContinuousLayout)
    {
	super(newOrientation, newContinuousLayout);
    }

    public DoubleSplitPane(int newOrientation, boolean newContinuousLayout,
			   Component newNorthWestComponent,
			   Component newNorthEastComponent,
			   Component newSouthWestComponent,
			   Component newSouthEastComponent)

    {
	super(newOrientation, newContinuousLayout);

	setupTiedPanes(newNorthWestComponent, newNorthEastComponent,
		       newSouthWestComponent, newSouthEastComponent);
    }

    public SplitPane getLeftSplitPane()
    {
	return (SplitPane) getLeftComponent();
    }

    public SplitPane getTopSplitPane()
    {
	return getLeftSplitPane();
    }

    public SplitPane getRightSplitPane()
    {
	return (SplitPane) getRightComponent();
    }

    public SplitPane getBottomSplitPane()
    {
	return getRightSplitPane();
    }

    public Component getComponentAt(int location)
    {
	switch (location) {
	case NORTH_WEST: return getLeftSplitPane().getLeftComponent();
	case NORTH_EAST: return getLeftSplitPane().getRightComponent();
	case SOUTH_WEST: return getRightSplitPane().getLeftComponent();
	case SOUTH_EAST: return getRightSplitPane().getRightComponent();
	}

	throw new IllegalArgumentException();
    }

    public Component getNorthWestComponent()
    {
	return getComponentAt(NORTH_WEST);
    }

    public Component getNorthEastComponent()
    {
	return getComponentAt(NORTH_EAST);
    }

    public Component getSouthWestComponent()
    {
	return getComponentAt(SOUTH_WEST);
    }

    public Component getSouthEastComponent()
    {
	return getComponentAt(SOUTH_EAST);
    }

    public void setComponentAt(int location, Component comp)
    {
	switch (location) {
	case NORTH_WEST: getLeftSplitPane().setLeftComponent(comp); return;
	case NORTH_EAST: getLeftSplitPane().setRightComponent(comp); return;
	case SOUTH_WEST: getRightSplitPane().setLeftComponent(comp); return;
	case SOUTH_EAST: getRightSplitPane().setRightComponent(comp); return;
	}

	throw new IllegalArgumentException();
    }

    public void setNorthWestComponent(Component comp)
    {
	setComponentAt(NORTH_WEST, comp);
    }

    public void setNorthEastComponent(Component comp)
    {
	setComponentAt(NORTH_EAST, comp);
    }

    public void setSouthWestComponent(Component comp)
    {
	setComponentAt(SOUTH_WEST, comp);
    }

    public void setSouthEastComponent(Component comp)
    {
	setComponentAt(SOUTH_EAST, comp);
    }
}
