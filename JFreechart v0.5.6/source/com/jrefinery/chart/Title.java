/* =======================================
 * JFreeChart : a Java Chart Class Library
 * =======================================
 * Version:         0.5.6;
 * Project Lead:    David Gilbert (david.gilbert@bigfoot.com);
 *
 * File:            Title.java
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
 * $Id: Title.java,v 1.1 2007/10/10 18:52:16 vauchers Exp $
 */

package com.jrefinery.chart;

import java.awt.*;
import java.awt.geom.*;
import javax.swing.*;
import com.jrefinery.chart.event.*;

/**
 * A title displays a description for a chart.
 * @see StandardTitle
 */
public abstract class Title {

  /** The text of the chart title. */
  protected String title;

  /** Storage for registered change listeners. */
  protected java.util.List listeners;

  /**
   * Static factory method that returns a concrete subclass of Title.
   * @param title The chart title;
   * @param font The font for displaying the chart title;
   */
  public static Title createInstance(String title, Font font) {
    return new StandardTitle(title, font);
  }

  /**
   * Standard constructor - builds a Title object.
   */
  protected Title(String title) {
    this.title = title;
    this.listeners = new java.util.ArrayList();
  }

  /**
   * Returns the title text.
   */
  public String getTitle() {
    return this.title;
  }

  /**
   * Sets the title to the specified text and notifies registered listeners that the title has
   * been modified.
   * @param text The new chart title;
   */
  public void setTitle(String text) {
    this.title = text;
    notifyListeners(new TitleChangeEvent(this));
  }

  /**
   * Registers an object for notification of changes to the title.
   * @param listener The object that is being registered.
   */
  public void addChangeListener(TitleChangeListener listener) {
    listeners.add(listener);
  }

  /**
   * Unregisters an object for notification of changes to the chart title.
   * @param listener The object that is being unregistered.
   */
  public void removeChangeListener(TitleChangeListener listener) {
    listeners.remove(listener);
  }

  /**
   * Draws the title on a Java 2D graphics device (such as the screen or a printer).
   * @param g2 The graphics device;
   * @param chartArea The area within which the chart will be drawn (the title must choose a
   *                  subset of this area in which to draw itself);
   * @return The area used to draw the title;
   */
  public abstract Rectangle2D draw(Graphics2D g2, Rectangle2D chartArea);

  /**
   * Notifies all registered listeners that the chart title has changed in some way.
   * @param event An object that contains information about the change to the title.
   */
  protected void notifyListeners(TitleChangeEvent event) {
    java.util.Iterator iterator = listeners.iterator();
    while (iterator.hasNext()) {
      TitleChangeListener listener = (TitleChangeListener)iterator.next();
      listener.titleChanged(event);
    }
  }

}