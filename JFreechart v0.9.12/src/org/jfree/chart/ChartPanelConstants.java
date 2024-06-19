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
 * ---------------
 * ChartPanel.java
 * ---------------
 * (C) Copyright 2002, 2003 by Object Refinery Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributors:     -;
 *
 * $Id: ChartPanelConstants.java,v 1.1 2007/10/10 19:12:21 vauchers Exp $
 *
 * Changes:
 * --------
 * 27-May-2002 : Version 1 (code moved from the ChartPanel class);
 * 25-Jun-2002 : Increased max draw width and height (DG);
 *
 */

package org.jfree.chart;

/**
 * Useful constants for the {@link ChartPanel} class.
 *
 * @author David Gilbert
 */
public interface ChartPanelConstants {

    /** Default setting for buffer usage. */
    boolean DEFAULT_BUFFER_USED = false;

    /** The default panel width. */
    int DEFAULT_WIDTH = 680;

    /** The default panel height. */
    int DEFAULT_HEIGHT = 420;

    /** The default limit below which chart scaling kicks in. */
    int DEFAULT_MINIMUM_DRAW_WIDTH = 300;

    /** The default limit below which chart scaling kicks in. */
    int DEFAULT_MINIMUM_DRAW_HEIGHT = 200;

    /** The default limit below which chart scaling kicks in. */
    int DEFAULT_MAXIMUM_DRAW_WIDTH = 800;

    /** The default limit below which chart scaling kicks in. */
    int DEFAULT_MAXIMUM_DRAW_HEIGHT = 600;

    /** The minimum size required to perform a zoom on a rectangle */
    int MINIMUM_DRAG_ZOOM_SIZE = 20;

    /** Properties action command. */
    String PROPERTIES_ACTION_COMMAND = "PROPERTIES";

    /** Save action command. */
    String SAVE_ACTION_COMMAND = "SAVE";

    /** Print action command. */
    String PRINT_ACTION_COMMAND = "PRINT";

    /** Zoom in (both axes) action command. */
    String ZOOM_IN_BOTH_ACTION_COMMAND = "ZOOM_IN_BOTH";

    /** Zoom in (horizontal axis only) action command. */
    String ZOOM_IN_HORIZONTAL_ACTION_COMMAND = "ZOOM_IN_HORIZONTAL";

    /** Zoom in (vertical axis only) action command. */
    String ZOOM_IN_VERTICAL_ACTION_COMMAND = "ZOOM_IN_VERTICAL";

    /** Zoom out (both axes) action command. */
    String ZOOM_OUT_BOTH_ACTION_COMMAND = "ZOOM_OUT_BOTH";

    /** Zoom out (horizontal axis only) action command. */
    String ZOOM_OUT_HORIZONTAL_ACTION_COMMAND = "ZOOM_HORIZONTAL_BOTH";

    /** Zoom out (vertical axis only) action command. */
    String ZOOM_OUT_VERTICAL_ACTION_COMMAND = "ZOOM_VERTICAL_BOTH";

    /** Zoom reset (both axes) action command. */
    String AUTO_RANGE_BOTH_ACTION_COMMAND = "AUTO_RANGE_BOTH";

    /** Zoom reset (horizontal axis only) action command. */
    String AUTO_RANGE_HORIZONTAL_ACTION_COMMAND = "AUTO_RANGE_HORIZONTAL";

    /** Zoom reset (vertical axis only) action command. */
    String AUTO_RANGE_VERTICAL_ACTION_COMMAND = "AUTO_RANGE_VERTICAL";

}
