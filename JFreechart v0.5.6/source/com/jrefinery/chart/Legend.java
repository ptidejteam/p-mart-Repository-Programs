/* =======================================
 * JFreeChart : a Java Chart Class Library
 * =======================================
 * Version:         0.5.6;
 * Project Lead:    David Gilbert (david.gilbert@bigfoot.com);
 *
 * File:            Legend.java
 * Author:          David Gilbert;
 * Contributor(s):  -;
 *
 * (C) Copyright 2000, Simba Management Limited;
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
 * $Id: Legend.java,v 1.1 2007/10/10 18:52:16 vauchers Exp $
 */

package com.jrefinery.chart;

import java.awt.*;
import java.awt.font.*;
import java.awt.geom.*;

import javax.swing.*;

import com.jrefinery.chart.event.*;

/**
 * A chart legend shows the names and visual representations of the series that are plotted in a
 * chart.
 * @see StandardLegend
 */
public abstract class Legend {

  /**
   * A reference to the chart that the legend belongs to (used for access to the DataSource).
   */
  protected JFreeChart chart;

  /**
   * The amount of blank space around the legend.
   */
  protected int outerGap;

  /**
   * Storage for registered change listeners.
   */
  protected java.util.List listeners;

  /**
   * Static factory method that returns a concrete subclass of Legend.
   * @param chart The JFreeChart that the legend belongs to.
   */
  public static Legend createInstance(JFreeChart chart) {
    return new StandardLegend(chart);
  }

  /**
   * Default constructor: returns a new Legend.
   */
  public Legend(JFreeChart chart, int outerGap) {
    this.chart = chart;
    this.outerGap = outerGap;
    this.listeners = new java.util.ArrayList();
  }

  /**
   * Draws the plot on a Java 2D graphics device (such as the screen or a printer).
   * @param g2 The graphics device;
   * @param drawArea The area within which the plot should be drawn;
   * @return The area used by the legend;
   */
  public abstract Rectangle2D draw(Graphics2D g2, Rectangle2D nonTitleArea);

  /**
   * Registers an object for notification of changes to the legend.
   * @param listener The object that is being registered.
   */
  public void addChangeListener(LegendChangeListener listener) {
    listeners.add(listener);
  }

  /**
   * Unregisters an object for notification of changes to the legend.
   * @param listener The object that is being unregistered.
   */
  public void removeChangeListener(LegendChangeListener listener) {
    listeners.remove(listener);
  }

  /**
   * Notifies all registered listeners that the chart legend has changed in some way.
   * @param event An object that contains information about the change to the legend.
   */
  protected void notifyListeners(LegendChangeEvent event) {
    java.util.Iterator iterator = listeners.iterator();
    while (iterator.hasNext()) {
      LegendChangeListener listener = (LegendChangeListener)iterator.next();
      listener.legendChanged(event);
    }
  }

}