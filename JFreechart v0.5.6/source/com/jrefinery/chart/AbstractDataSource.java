/* =======================================
 * JFreeChart : a Java Chart Class Library
 * =======================================
 * Version:         0.5.6;
 * Project Lead:    David Gilbert (david.gilbert@bigfoot.com);
 *
 * File:            AbstractDataSource.java
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
 * $Id: AbstractDataSource.java,v 1.1 2007/10/10 18:52:16 vauchers Exp $
 */

package com.jrefinery.chart;

import java.util.*;
import com.jrefinery.chart.event.*;

/**
 * A base implementation of the DataSource interface that contains a mechanism for registering
 * DataSourceListeners.
 */
public abstract class AbstractDataSource implements DataSource {

  /** Storage for registered change listeners. */
  protected List listeners;

  /**
   * Default constructor - note that this class should not be instantiated directly - use a
   * subclass.
   */
  public AbstractDataSource() {
    this.listeners = new ArrayList();
  }

  /**
   * Returns the number of series in the data source.  This method MUST be overridden by
   * subclasses.
   */
  public abstract int getSeriesCount(); // { return 0; }

  /**
   * Returns the name of the specified series.  This method MUST be overridden by subclasses.
   * @param seriesIndex The index of the required series (zero-based).
   */
  public abstract String getSeriesName(int seriesIndex); //{ return ""; }

  /**
   * Registers an object for notification of changes to the data source.
   * @param listener The object being registered.
   */
  public void addChangeListener(DataSourceChangeListener listener) {
    listeners.add(listener);
  }

  /**
   * Unregisters an object for notification of changes to the data source.
   * @param listener The object being unregistered.
   */
  public void removeChangeListener(DataSourceChangeListener listener) {
    listeners.remove(listener);
  }

  /**
   * Notifies all registered listeners that this data source has changed in some way.
   */
  protected void fireDataSourceChanged() {
    notifyListeners(new DataSourceChangeEvent(this));
  }

  /**
   * Notifies all registered listeners that the data source has been modified.
   * @param event Contains information about the event that triggered the notification.
   */
  protected void notifyListeners(DataSourceChangeEvent event) {
    Iterator iterator = listeners.iterator();
    while (iterator.hasNext()) {
      DataSourceChangeListener listener = (DataSourceChangeListener)iterator.next();
      listener.dataSourceChanged(event);
    }
  }

}