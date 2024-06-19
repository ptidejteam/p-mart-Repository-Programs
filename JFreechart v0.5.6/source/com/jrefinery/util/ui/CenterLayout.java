/* ================================
 * JRefinery Utility Class Library;
 * ================================
 * Version 0.20;
 * (C) Copyright 2000, Simba Management Limited;
 * Contact: David Gilbert (david.gilbert@bigfoot.com);
 *
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this library;
 * if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston,
 * MA 02111-1307, USA.
 *
 * $Id: CenterLayout.java,v 1.1 2007/10/10 18:52:17 vauchers Exp $
 */

package com.jrefinery.util.ui;

import java.awt.*;
import java.io.Serializable;

/** A layout manager that displays a single component in the center of its container. */
public class CenterLayout implements LayoutManager, java.io.Serializable {

  /** Standard constructor. */
  public CenterLayout() {
  }

  /** Returns the preferred size... */
  public Dimension preferredLayoutSize(Container parent) {
    Dimension d=null;
    synchronized (parent.getTreeLock()) {
      Insets insets = parent.getInsets();
      if (parent.getComponentCount()>0) {
        Component component = parent.getComponent(0);
        d = component.getPreferredSize();
      }
      return new Dimension((int)d.getWidth()+insets.left+insets.right, (int)d.getHeight()+insets.top
                           +insets.bottom);
    }
  }

  /** Returns the minimum size... */
  public Dimension minimumLayoutSize(Container parent) {
    Dimension d=null;
    synchronized (parent.getTreeLock()) {
      Insets insets = parent.getInsets();
      if (parent.getComponentCount()>0) {
        Component component = parent.getComponent(0);
        d = component.getMinimumSize();
      }
      return new Dimension((int)d.getWidth()+insets.left+insets.right, (int)d.getHeight()+insets.top
                           +insets.bottom);
    }
  }

  /** Lays out the components... */
  public void layoutContainer(Container parent) {

    synchronized (parent.getTreeLock()) {
      if (parent.getComponentCount()>0) {
        Insets insets = parent.getInsets();
        Dimension parentSize = parent.getSize();
        Component component = parent.getComponent(0);
        Dimension componentSize = component.getPreferredSize();
        int xx = insets.left+(Math.max((parentSize.width-insets.left-insets.right
                                        -componentSize.width)/2, 0));
        int yy = insets.top+(Math.max((parentSize.height-insets.top-insets.bottom
                                        -componentSize.height)/2, 0));
        component.setBounds(xx, yy, componentSize.width, componentSize.height);
      }
    }

  }

  /** Not used. */
  public void addLayoutComponent(Component comp) {
  }

  /** Not used. */
  public void removeLayoutComponent(Component comp) {
  }

  /** Not used. */
  public void addLayoutComponent(String name, Component comp) {
  }

  /** Not used. */
  public void removeLayoutComponent(String name, Component comp) {
  }

}