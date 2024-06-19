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

package hm.binkley.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Component;
import java.awt.event.MouseEvent;
import javax.swing.JButton;
import javax.swing.JSplitPane;
import javax.swing.SwingConstants;

public class DoubleSplitPane extends FlippingSplitPane
    implements SwingConstants
{
    public DoubleSplitPane()
    {
	this(VERTICAL_SPLIT, new JButton("Northwest Button"),
	     new JButton("Northeast Button"),
	     new JButton("Southwest Button"),
	     new JButton("Southeast Button"));
    }

    public DoubleSplitPane(int newOrientation)
    {
	this(newOrientation, false, new JButton("Northwest Button"),
	     new JButton("Northeast Button"),
	     new JButton("Southwest Button"),
	     new JButton("Southeast Button"));
    }


    public DoubleSplitPane(int newOrientation,
			   Component newNorthWestComponent,
			   Component newNorthEastComponent,
			   Component newSouthWestComponent,
			   Component newSouthEastComponent)
    {
	this(newOrientation, false, newNorthWestComponent,
	     newNorthEastComponent, newSouthWestComponent,
	     newSouthEastComponent);
    }

    public DoubleSplitPane(int newOrientation, boolean newContinuousLayout)
    {
	this(newOrientation, newContinuousLayout,
	     new JButton("Northwest Button"),
	     new JButton("Northeast Button"),
	     new JButton("Southwest Button"),
	     new JButton("Southeast Button"));
    }

    public DoubleSplitPane(int newOrientation, boolean newContinuousLayout,
			   Component newNorthWestComponent,
			   Component newNorthEastComponent,
			   Component newSouthWestComponent,
			   Component newSouthEastComponent)

    {
	super(newOrientation, newContinuousLayout,
	      new FlippingSplitPane(invertOrientation(newOrientation),
				    newContinuousLayout,
				    newNorthWestComponent,
				    newNorthEastComponent),
	      new FlippingSplitPane(invertOrientation(newOrientation),
				    newContinuousLayout,
				    newSouthWestComponent,
				    newSouthEastComponent));
    }

    public JSplitPane getLeftSplitPane()
    {
	return (JSplitPane) getLeftComponent();
    }

    public JSplitPane getTopSplitPane()
    {
	return getLeftSplitPane();
    }

    public JSplitPane getRightSplitPane()
    {
	return (JSplitPane) getRightComponent();
    }

    public JSplitPane getBottomSplitPane()
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
