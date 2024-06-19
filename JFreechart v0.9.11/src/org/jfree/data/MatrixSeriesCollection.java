/* ======================================
 * JFreeChart : a free Java chart library
 * ======================================
 *
 * Project Info:  http://www.jfree.org/jfreechart/index.html
 * Project Lead:  David Gilbert (david.gilbert@object-refinery.com);
 *
 * (C) Copyright 2000-2003, by Object Refinery Limited and Contributors.
 *
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA 02111-1307, USA.
 *
 * ---------------------------
 * MatrixSeriesCollection.java
 * ---------------------------
 * (C) Copyright 2003 by Barak Naveh and Contributors.
 *
 * Original Author:  Barak Naveh;;
 * Contributor(s):   David Gilbert (for Object Refinery Limited);
 *
 * $Id: MatrixSeriesCollection.java,v 1.1 2007/10/10 19:09:12 vauchers Exp $
 *
 * Changes
 * -------
 * 10-Jul-2003 : Version 1 contributed by Barak Naveh (DG);
 *
 */
 
package org.jfree.data;

import java.io.Serializable;

import java.util.List;

import org.jfree.util.ObjectUtils;

/**
 * Represents a collection of MatrixSeries that can be used as a dataset.
 *
 * @author Barak Naveh
 *
 * @see org.jfree.data.MatrixSeries
 */
public class MatrixSeriesCollection extends AbstractSeriesDataset
    implements XYZDataset, Serializable {
    /** The series that are included in the collection. */
    private List m_data;

    /**
     * Constructs an empty dataset.
     */
    public MatrixSeriesCollection(  ) {
        this( null );
    }


    /**
     * Constructs a dataset and populates it with a single matrix series.
     *
     * @param series the time series.
     */
    public MatrixSeriesCollection( MatrixSeries series ) {
        this.m_data = new java.util.ArrayList(  );

        if( series != null ) {
            m_data.add( series );
            series.addChangeListener( this );
        }
    }

    /**
     * Returns the number of items in the specified series.
     *
     * @param seriesIndex zero-based series index.
     *
     * @return the number of items in the specified series.
     */
    public int getItemCount( int seriesIndex ) {
        return getSeries( seriesIndex ).getItemCount(  );
    }


    /**
     * Returns the series having the specified index.
     *
     * @param seriesIndex zero-based series index.
     *
     * @return The series.
     *
     * @throws IllegalArgumentException
     */
    public MatrixSeries getSeries( int seriesIndex ) {
        if( ( seriesIndex < 0 ) || ( seriesIndex > getSeriesCount(  ) ) ) {
            throw new IllegalArgumentException(
                "MatrixSeriesCollection.getSeries(...): index outside valid range." );
        }

        MatrixSeries series = (MatrixSeries) m_data.get( seriesIndex );

        return series;
    }


    /**
     * Returns the number of series in the collection.
     *
     * @return the number of series in the collection.
     */
    public int getSeriesCount(  ) {
        return this.m_data.size(  );
    }


    /**
     * Returns the name of a series.
     *
     * @param seriesIndex zero-based series index.
     *
     * @return the name of a series.
     */
    public String getSeriesName( int seriesIndex ) {
        return getSeries( seriesIndex ).getName(  );
    }


    /**
     * Returns the j index value of the specified Mij matrix item in the
     * specified matrix series.
     *
     * @param seriesIndex zero-based series index.
     * @param itemIndex zero-based item index.
     *
     * @return the j index value for the specified matrix item.
     *
     * @see org.jfree.data.XYDataset#getXValue(int, int)
     */
    public Number getXValue( int seriesIndex, int itemIndex ) {
        MatrixSeries series = (MatrixSeries) m_data.get( seriesIndex );
        int          x = series.getItemColumn( itemIndex );

        return new Integer( x ); // I know it's bad to create object. better idea?
    }


    /**
     * Returns the i index value of the specified Mij matrix item in the
     * specified matrix series.
     *
     * @param seriesIndex zero-based series index.
     * @param itemIndex zero-based item index.
     *
     * @return the i index value for the specified matrix item.
     *
     * @see org.jfree.data.XYDataset#getYValue(int, int)
     */
    public Number getYValue( int seriesIndex, int itemIndex ) {
        MatrixSeries series = (MatrixSeries) m_data.get( seriesIndex );
        int          y = series.getItemRow( itemIndex );

        return new Integer( y ); // I know it's bad to create object. better idea?
    }


    /**
     * Returns the Mij item value of the specified Mij matrix item in the
     * specified matrix series.
     *
     * @param seriesIndex the series (zero-based index).
     * @param itemIndex zero-based item index.
     *
     * @return the Mij item value for the specified matrix item.
     *
     * @see org.jfree.data.XYZDataset#getZValue(int, int)
     */
    public Number getZValue( int seriesIndex, int itemIndex ) {
        MatrixSeries series = (MatrixSeries) m_data.get( seriesIndex );
        Number       z = series.getItem( itemIndex );

        return z;
    }


    /**
     * Adds a series to the collection.
     * <P>
     * Notifies all registered listeners that the dataset has changed.
     * </p>
     *
     * @param series the series.
     *
     * @throws IllegalArgumentException
     */
    public void addSeries( MatrixSeries series ) {
        // check arguments...
        if( series == null ) {
            throw new IllegalArgumentException(
                "MatrixSeriesCollection.addSeries(...): cannot add null series." );
        }

        // add the series...
        m_data.add( series );
        series.addChangeListener( this );
        fireDatasetChanged(  );
    }


    /**
     * Tests this collection for equality with an arbitrary object.
     *
     * @param obj the object.
     *
     * @return A boolean.
     */
    public boolean equals( Object obj ) {
        if( obj == null ) {
            return false;
        }

        if( obj == this ) {
            return true;
        }

        if( obj instanceof MatrixSeriesCollection ) {
            MatrixSeriesCollection c = (MatrixSeriesCollection) obj;

            return ObjectUtils.equalOrBothNull( m_data, c.m_data );
        }

        return false;
    }


    /**
     * Removes all the series from the collection.
     * <P>
     * Notifies all registered listeners that the dataset has changed.
     * </p>
     */
    public void removeAllSeries(  ) {
        // Unregister the collection as a change listener to each series inmthe collection.
        for( int i = 0; i < this.m_data.size(  ); i++ ) {
            MatrixSeries series = (MatrixSeries) m_data.get( i );
            series.removeChangeListener( this );
        }

        // Remove all the series from the collection and notify listeners.
        m_data.clear(  );
        fireDatasetChanged(  );
    }


    /**
     * Removes a series from the collection.
     * <P>
     * Notifies all registered listeners that the dataset has changed.
     * </p>
     *
     * @param series the series.
     *
     * @throws IllegalArgumentException
     */
    public void removeSeries( MatrixSeries series ) {
        // check arguments...
        if( series == null ) {
            throw new IllegalArgumentException(
                "MatrixSeriesCollection.removeSeries(...): cannot remove null series." );
        }

        // remove the series...
        if( m_data.contains( series ) ) {
            series.removeChangeListener( this );
            m_data.remove( series );
            fireDatasetChanged(  );
        }
    }


    /**
     * Removes a series from the collection.
     * <P>
     * Notifies all registered listeners that the dataset has changed.
     * </p>
     *
     * @param seriesIndex the series (zero based index).
     *
     * @throws IllegalArgumentException
     */
    public void removeSeries( int seriesIndex ) {
        // check arguments...
        if( ( seriesIndex < 0 ) || ( seriesIndex > getSeriesCount(  ) ) ) {
            throw new IllegalArgumentException(
                "MatrixSeriesCollection.removeSeries(...): index outside valid range." );
        }

        // fetch the series, remove the change listener, then remove the series.
        MatrixSeries series = (MatrixSeries) m_data.get( seriesIndex );
        series.removeChangeListener( this );
        m_data.remove( seriesIndex );
        fireDatasetChanged(  );
    }
    
}
