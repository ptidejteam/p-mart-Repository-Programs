/* =======================================
 * JFreeChart : a Java Chart Class Library
 * =======================================
 *
 * Project Info:  http://www.object-refinery.com/jfreechart
 * Project Lead:  David Gilbert (david.gilbert@jrefinery.com);
 *
 * (C) Copyright 2000-2002, by Simba Management Limited and Contributors.
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
 * --------------
 * DateTitle.java
 * --------------
 * (C) Copyright 2000-2002, by David Berry and Contributors.
 *
 * Original Author:  David Berry;
 * Contributor(s):   David Gilbert (for Simba Management Limited);
 *
 * $Id: DateTitle.java,v 1.1 2007/10/10 18:57:57 vauchers Exp $
 *
 * Changes (from 18-Sep-2001)
 * --------------------------
 * 18-Sep-2001 : Added standard header (DG);
 * 09-Jan-2002 : Updated Javadoc comments (DG);
 * 07-Feb-2002 : Changed blank space around title from Insets --> Spacer, to allow for relative
 *               or absolute spacing (DG);
 *
 */

package com.jrefinery.chart;

import java.awt.*;
import java.text.*;
import java.util.Date;
import java.util.Locale;

/**
 * DateTitle (an extension of TextTitle) is a simple convenience class to easily add the text of
 * the date to a chart.  Keep in mind that a chart can have several titles, and that they can
 * appear at the top, left, right or bottom of the chart - a DateTitle will commonly appear at
 * the bottom of a chart (although you can place it anywhere).
 * <P>
 * By specifying the locale, dates are formatted to the correct standard for the given Locale.
 * For example, a date would appear as "January 17, 2000" in the US, but "17 January 2000" in
 * most European locales.
 */
public class DateTitle extends TextTitle {

    /**
     * Constructs a new DateTitle that displays the current date in the default
     * (LONG) format for the locale, positioned to the bottom right of the chart.
     * <P>
     * The color will be black in 12 point, plain Helvetica font (maps to Arial on Win32 systems
     * without Helvetica).
     */
    public DateTitle() {

        this(DateFormat.LONG);

    }

    /**
     * Constructs a new DateTitle with the specified style.
     */
    public DateTitle(int dateStyle) {
        this(dateStyle, Locale.getDefault(), new Font("Dialog", Font.PLAIN, 12), Color.black);
    }

    /**
     * Constructs a new DateTitle with the specified attributes.
     * @param location the relative location of this title (use constants in AbstractTitle).
     * @param alignment the text alignment of this title (use constants in AbstractTitle).
     * @param dateStyle the Date style to use (SHORT, MEDIUM, LONG, or FULL constants from
     *                  java.text.DateFormat).
     * @param locale the locale to use to format this date (if you are unsure what to use here, use
     *               Locale.getDefault() for your default locale).
     * @param font the font used to display the date.
     * @param paint the paint used to display the date.
     * @param spacer Determines the blank space around the outside of the title.
     */
    public DateTitle(int dateStyle, Locale locale, Font font, Paint paint, int position,
                     int horizontalAlignment, int verticalAlignment, Spacer spacer) {

        super(DateFormat.getDateInstance(dateStyle, locale).format(new Date()),
              font, paint, position, horizontalAlignment, verticalAlignment, spacer);

    }

    /**
     * Constructs a new DateTitle object with the specified attributes and the following defaults:
     * location = BOTTOM, alignment = RIGHT, insets = new Insets(2, 2, 2, 2).
     * @param dateStyle the Date style to use (SHORT, MEDIUM, LONG, or FULL constants from
     *                  java.util.DateFormat);
     * @param locale the locale to use to format this date (if you are unsure what to use here, use
     *               Locale.getDefault() for your default locale);
     * @param font the font used to display the date;
     * @param paint the paint used to display the date;
     */
    public DateTitle(int dateStyle, Locale locale, Font font, Paint paint) {

        this(dateStyle, locale, font, paint,
             AbstractTitle.BOTTOM, AbstractTitle.RIGHT, AbstractTitle.MIDDLE,
             AbstractTitle.DEFAULT_SPACER);
    }

    /**
     * Set the format of the date.
     * @param dateStyle the Date style to use (SHORT, MEDIUM, LONG, or FULL constants from
     *                  java.util.DateFormat);
     * @param locale the locale to use to format this date (if you are unsure what to use here, use
     *               Locale.getDefault() for your default locale);
     */
    public void setDateFormat(int dateStyle, Locale locale) {

        this.setText(DateFormat.getDateInstance(dateStyle, locale).format(new Date()));

    }

}
