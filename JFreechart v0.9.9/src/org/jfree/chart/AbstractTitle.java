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
 * ------------------
 * AbstractTitle.java
 * ------------------
 * (C) Copyright 2000-2003, by David Berry and Contributors.
 *
 * Original Author:  David Berry;
 * Contributor(s):   David Gilbert (for Object Refinery Limited);
 *
 * $Id: AbstractTitle.java,v 1.1 2007/10/10 20:07:37 vauchers Exp $
 *
 * Changes (from 21-Aug-2001)
 * --------------------------
 * 21-Aug-2001 : Added standard header (DG);
 * 18-Sep-2001 : Updated header (DG);
 * 14-Nov-2001 : Package com.jrefinery.common.ui.* changed to com.jrefinery.ui.* (DG);
 * 07-Feb-2002 : Changed blank space around title from Insets --> Spacer, to allow for relative
 *               or absolute spacing (DG);
 * 25-Jun-2002 : Removed unnecessary imports (DG);
 * 01-Oct-2002 : Fixed errors reported by Checkstyle (DG);
 * 14-Oct-2002 : Changed the event listener storage structure (DG);
 *
 */

package org.jfree.chart;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import javax.swing.event.EventListenerList;

import org.jfree.chart.event.TitleChangeEvent;
import org.jfree.chart.event.TitleChangeListener;
import org.jfree.util.ObjectUtils;

/**
 * The base class for all chart titles.
 * <P>
 * A chart can have multiple titles, appearing at the top, bottom, left or
 * right of the chart (defined by the constants TOP, BOTTOM, LEFT and RIGHT ---
 * we also use the constants NORTH, SOUTH, EAST and WEST to remain consistent
 * with java.awt.BorderLayout, as most developers will be familiar with that
 * class).
 * <P>
 * Concrete implementations of this class will render text, images, and hence
 * do the actual work of drawing titles.
 *
 * @author David Berry
 */
public abstract class AbstractTitle extends Object implements Cloneable, Serializable {

    /** Useful constant for the title position (also used for vertical alignment). */
    public static final int TOP = 0;

    /** Useful constant for the title position (also used for vertical alignment). */
    public static final int BOTTOM = 1;

    /** Useful constant for the title position (also used for horizontal alignment). */
    public static final int RIGHT = 2;

    /** Useful constant for the title position (also used for horizontal alignment). */
    public static final int LEFT = 3;

    /** Useful constant for the title position. */
    public static final int NORTH = 0;

    /** Useful constant for the title position. */
    public static final int SOUTH = 1;

    /** Useful constant for the title position. */
    public static final int EAST = 2;

    /** Useful constant for the title position. */
    public static final int WEST = 3;

    /** Useful constant for the title alignment (horizontal or vertical). */
    public static final int CENTER = 4;

    /** Useful constant for the title alignment (horizontal or vertical). */
    public static final int MIDDLE = 4;

    /** The default title position. */
    public static final int DEFAULT_POSITION = AbstractTitle.TOP;

    /** The default horizontal alignment. */
    public static final int DEFAULT_HORIZONTAL_ALIGNMENT = AbstractTitle.CENTER;

    /** The default vertical alignment. */
    public static final int DEFAULT_VERTICAL_ALIGNMENT = AbstractTitle.MIDDLE;

    /** Default title spacer. */
    public static final Spacer DEFAULT_SPACER = new Spacer(Spacer.RELATIVE, 0.01, 0.30, 0.01, 0.15);

    /**
     * The position of the title (use the constants NORTH, SOUTH, EAST and
     * WEST, or if you prefer you can also use TOP, BOTTOM, LEFT and RIGHT).
     */
    private int position;

    /** The horizontal alignment of the title. */
    private int horizontalAlignment;

    /** The vertical alignment of the title. */
    private int verticalAlignment;

    /** The amount of blank space to leave around the title. */
    private Spacer spacer;

    /** Storage for registered change listeners. */
    private transient EventListenerList listenerList;

    /** A flag that can be used to temporarily disable the listener mechanism. */
    private boolean notify;

    /**
     * Creates a new title, using default attributes where necessary.
     */
    protected AbstractTitle() {

        this(AbstractTitle.DEFAULT_POSITION,
             AbstractTitle.DEFAULT_HORIZONTAL_ALIGNMENT,
             AbstractTitle.DEFAULT_VERTICAL_ALIGNMENT,
             AbstractTitle.DEFAULT_SPACER);

    }

    /**
     * Creates a new title, using default attributes where necessary.
     *
     * @param position  the relative position of the title (TOP, BOTTOM, RIGHT and LEFT).
     * @param horizontalAlignment  the horizontal alignment of the title (LEFT, CENTER or RIGHT).
     * @param verticalAlignment  the vertical alignment of the title (TOP, MIDDLE or BOTTOM).
     */
    protected AbstractTitle(int position, int horizontalAlignment, int verticalAlignment) {

        this(position,
             horizontalAlignment, verticalAlignment,
             AbstractTitle.DEFAULT_SPACER);

    }

    /**
     * Constructs a new AbstractTitle.
     * <P>
     * This class defines constants for the valid position and alignment values
     * --- an IllegalArgumentException will be thrown if invalid values are
     * passed to this constructor.
     *
     * @param position  the relative position of the title (TOP, BOTTOM, RIGHT and LEFT).
     * @param horizontalAlignment  the horizontal alignment of the title (LEFT, CENTER or RIGHT).
     * @param verticalAlignment  the vertical alignment of the title (TOP, MIDDLE or BOTTOM).
     * @param spacer  the amount of space to leave around the outside of the title.
     */
    protected AbstractTitle(int position,
                            int horizontalAlignment, int verticalAlignment,
                            Spacer spacer) {

        // check arguments...
        if (!isValidPosition(position)) {
            throw new IllegalArgumentException("AbstractTitle(): invalid position.");
        }

        if (!AbstractTitle.isValidHorizontalAlignment(horizontalAlignment)) {
            throw new IllegalArgumentException("AbstractTitle(): invalid horizontal alignment.");
        }

        if (!AbstractTitle.isValidVerticalAlignment(verticalAlignment)) {
            throw new IllegalArgumentException("AbstractTitle(): invalid vertical alignment.");
        }

        // initialise...
        this.position = position;
        this.horizontalAlignment = horizontalAlignment;
        this.verticalAlignment = verticalAlignment;
        this.spacer = spacer;
        this.listenerList = new EventListenerList();
        this.notify = true;

    }

    /**
     * Returns the relative position of the title---represented by one of four
     * integer constants defined in this class: TOP, BOTTOM, RIGHT or LEFT (or
     * the equivalent NORTH, SOUTH, EAST and WEST).
     *
     * @return The title position.
     */
    public int getPosition() {
        return this.position;
    }

    /**
     * Sets the position for the title.
     *
     * @param position  the relative position of the title (use one of the constants TOP, BOTTOM,
     *                  RIGHT and LEFT, or the equivalent NORTH, SOUTH, EAST and WEST).
     */
    public void setPosition(int position) {

        if (this.position != position) {
            // check that the position is valid
            this.position = position;
            notifyListeners(new TitleChangeEvent(this));
        }
    }

    /**
     * Returns the horizontal alignment of the title.  The constants LEFT,
     * CENTER and RIGHT (defined in this class) are used.
     *
     * @return the horizontal alignment of the title (LEFT, CENTER or RIGHT).
     */
    public int getHorizontalAlignment() {
        return this.horizontalAlignment;
    }

    /**
     * Sets the horizontal alignment for the title, and notifies any registered
     * listeners of the change.  The constants LEFT, CENTER and RIGHT (defined
     * in this class) can be used to specify the alignment.
     *
     * @param alignment  the new horizontal alignment (LEFT, CENTER or RIGHT).
     */
    public void setHorizontalAlignment(int alignment) {
        if (this.horizontalAlignment != alignment) {
            this.horizontalAlignment = alignment;
            notifyListeners(new TitleChangeEvent(this));
        }
    }

    /**
     * Returns the vertical alignment of the title.  The constants TOP, MIDDLE
     * and BOTTOM (defined in this class) are used.
     *
     * @return the vertical alignment of the title (TOP, MIDDLE or BOTTOM).
     */
    public int getVerticalAlignment() {
        return this.verticalAlignment;
    }

    /**
     * Sets the vertical alignment for the title, and notifies any registered
     * listeners of the change.
     * The constants TOP, MIDDLE and BOTTOM (defined in this class) can be used
     * to specify the alignment.
     *
     * @param alignment  the new vertical alignment (TOP, MIDDLE or BOTTOM).
     */
    public void setVerticalAlignment(int alignment) {
        if (this.verticalAlignment != alignment) {
            this.verticalAlignment = alignment;
            notifyListeners(new TitleChangeEvent(this));
        }
    }

    /**
     * Returns the spacer (determines the blank space around the edges) for
     * this title.
     *
     * @return the spacer for this title.
     */
    public Spacer getSpacer() {
        return this.spacer;
    }

    /**
     * Sets the spacer for the title, and notifies registered listeners of the
     * change.
     *
     * @param spacer  the new spacer.
     */
    public void setSpacer(Spacer spacer) {

        if (!this.spacer.equals(spacer)) {
            this.spacer = spacer;
            notifyListeners(new TitleChangeEvent(this));
        }

    }

    /**
     * Returns the flag that indicates whether or not the notification mechanism is enabled.
     *
     * @return the flag.
     */
    public boolean getNotify() {
        return this.notify;
    }

    /**
     * Sets the flag that indicates whether or not the notification mechanism
     * is enabled.  There are certain situations (such as cloning) where you
     * want to turn notification off temporarily.
     *
     * @param flag  the new value of the flag.
     */
    public void setNotify(boolean flag) {
        this.notify = flag;
    }

    /**
     * Returns true if the title can assume the specified location, and false otherwise.
     *
     * @param position  the position.
     *
     * @return <code>true</code> if the title can assume the specified position.
     */
    public abstract boolean isValidPosition(int position);

    /**
     * Returns the preferred width of the title.  When a title is displayed at
     * the left or right of a chart, the chart will attempt to give the title
     * enough space for it's preferred width.
     *
     * @param g2  the graphics device.
     *
     * @return the preferred width of the title.
     */
    public abstract double getPreferredWidth(Graphics2D g2);

    /**
     * Returns the preferred height of the title.  When a title is displayed at
     * the top or bottom of a chart, the chart will attempt to give the title
     * enough space for it's preferred height.
     *
     * @param g2  the graphics device.
     *
     * @return the preferred height of the title.
     */
    public abstract double getPreferredHeight(Graphics2D g2);

    /**
     * Draws the title on a Java 2D graphics device (such as the screen or a printer).
     *
     * @param g2  the graphics device.
     * @param titleArea  the area for drawing the title.
     */
    public abstract void draw(Graphics2D g2, Rectangle2D titleArea);

    /**
     * Returns a clone of the title.
     * <P>
     * One situation when this is useful is when editing the title properties -
     * you can edit a clone, and then it is easier to cancel the changes if
     * necessary.
     *
     * @return a clone of the title.
     *
     */
    public Object clone() {

        AbstractTitle duplicate = null;

        try {
            duplicate = (AbstractTitle) super.clone();
        }
        catch (CloneNotSupportedException e) {
            // this should never happen because Cloneable is implemented
            throw new RuntimeException("AbstractTitle.clone()");
        }

        return duplicate;

    }

    /**
     * Registers an object for notification of changes to the title.
     *
     * @param listener  the object that is being registered.
     */
    public void addChangeListener(TitleChangeListener listener) {
        this.listenerList.add(TitleChangeListener.class, listener);
    }

    /**
     * Unregisters an object for notification of changes to the chart title.
     *
     * @param listener  the object that is being unregistered.
     */
    public void removeChangeListener(TitleChangeListener listener) {
        this.listenerList.remove(TitleChangeListener.class, listener);
    }

    /**
     * Notifies all registered listeners that the chart title has changed in some way.
     *
     * @param event  an object that contains information about the change to the title.
     */
    protected void notifyListeners(TitleChangeEvent event) {

        if (this.notify) {

            Object[] listeners = this.listenerList.getListenerList();
            for (int i = listeners.length - 2; i >= 0; i -= 2) {
                if (listeners[i] == TitleChangeListener.class) {
                    ((TitleChangeListener) listeners[i + 1]).titleChanged(event);
                }
            }
        }

    }

    /**
     * Utility method for checking a horizontal alignment code.
     *
     * @param code  the alignment code.
     *
     * @return <code>true</code> if alignment is <code>LEFT|MIDDLE|RIGHT</code>.
     */
    protected static boolean isValidHorizontalAlignment(int code) {

        switch(code) {
            case AbstractTitle.LEFT:   return true;
            case AbstractTitle.MIDDLE: return true;
            case AbstractTitle.RIGHT:  return true;
            default: return false;
        }

    }

    /**
     * Utility method for checking a vertical alignment code.
     *
     * @param code  the alignment code.
     *
     * @return <code>true</code>, if alignment is <code>TOP|MIDDLE|BOTTOM</code>.
     */
    protected static boolean isValidVerticalAlignment(int code) {

        switch(code) {
            case AbstractTitle.TOP:    return true;
            case AbstractTitle.MIDDLE: return true;
            case AbstractTitle.BOTTOM: return true;
            default: return false;
        }

    }

    /**
     * Tests an object for equality with this title.
     *
     * @param obj  the object.
     *
     * @return <code>true</code> or <code>false</code>.
     */
    public boolean equals(Object obj) {

        if (obj == null) {
            return false;
        }

        if (obj == this) {
            return true;
        }

        if (obj instanceof AbstractTitle) {

            AbstractTitle t = (AbstractTitle) obj;
 //           if (super.equals(obj)) {
                boolean b0 = (this.position == t.position);
                boolean b1 = (this.horizontalAlignment == t.horizontalAlignment);
                boolean b2 = (this.verticalAlignment == t.verticalAlignment);
                boolean b3 = ObjectUtils.equalOrBothNull(this.spacer, t.spacer);
                boolean b4 = (this.notify == t.notify);

                return b0 && b1 && b2 && b3 && b4;
//            }
        }

        return false;

    }

    /**
     * Provides serialization support.
     *
     * @param stream  the output stream.
     *
     * @throws IOException  if there is an I/O error.
     */
    private void writeObject(ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
    }

    /**
     * Provides serialization support.
     *
     * @param stream  the input stream.
     *
     * @throws IOException  if there is an I/O error.
     * @throws ClassNotFoundException  if there is a classpath problem.
     */
    private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        this.listenerList = new EventListenerList();
    }

}
