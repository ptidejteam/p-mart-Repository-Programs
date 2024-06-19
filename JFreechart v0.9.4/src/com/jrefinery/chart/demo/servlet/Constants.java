/** =======================================
 *  JFreeChart : a Java Chart Class Library
 *  =======================================
 *
 * Project Info:  http://www.object-refinery.com/jfreechart/index.html
 * Project Lead:  David Gilbert (david.gilbert@object-refinery.com);
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
 * Constants.java
 * --------------
 * (C) Copyright 2002, by Bryan Scott and Contributors.
 *
 * Original Author:  Bryan Scott;
 * Contributor(s):   -;
 *
 *
 * Changes
 * -------
 * 14-Mar-2002 : Version 1 contributed by Bryan Scott (DG);
 * 27-Jul-2002 : BRS. Moved package
 *
 */

package com.jrefinery.chart.demo.servlet;


public interface Constants {

    public static final String DB_SERVER     = "DBserver.type";
    public static final String DB_USER       = "DBuser";
    public static final String DB_PASSWORD   = "DBpwd";
    public static final String DB_SCHEMA     = "DBschema";
    public static final String DB_URL        = "DBurl";
    public static final String DB_DRIVER     = "DBdriver";
    public static final String DB_CONNECTION = "DBconnection";
    public static final String ORGANISATION  = "Organisation";
    public static final String IMAGE_URL     = "ImageURL";
    public static final String HEADER_URL    = "HeaderURL";
    public static final String FOOTER_URL    = "FooterURL";
    public static final String DEBUG         = "DEBUG";
    public static final String TEMP_DIR      = "TemporaryDir";
    public static final String TEMP_URL      = "TemporaryURL";

    /** @todo - Remove the following constants. */
    public static final String HEADER        = "Header";
    public static final String HEADER_FILE   = "Header.File";
    public static final String FOOTER        = "Footer";
    public static final String FOOTER_FILE   = "Footer.File";
    public static final String CAUTION_FILE  = "Caution.File";
    public static final String SHARED_VOYAGES      = "SHARED_VOYAGES";
    public static final String SHARED_VOYAGES_TIME = "SHARED_VOYAGES_TIME";
    public static final String SHARED_DB           = "SHARED_DB";
    public static final String SHARED_DB_OPENER    = "SHARED_DB_OPENER";
    public static final String REPLACE_SCHEMA      = "XXSCHEMA.";

    /**
     * Servlet content type responses
     */
    public static final String[][] CONTENT_TYPE = {
      {"html", "text/html" },
      {"jpeg", "image/jpeg" },
      {"png",  "image/png" },
      {"svg",  "image/svg" },
      {"pdf",  "application/pdf" },
      {"postscript", "application/postscript" }
    };

    /**  Description of the Field */
    public final static int RESPONSE_HTML = 0;
    public final static int RESPONSE_JPEG = 1;
    public final static int RESPONSE_PNG  = 2;
    public final static int RESPONSE_SVG  = 3;
    public final static int RESPONSE_PDF  = 4;
    public final static int RESPONSE_PS   = 5;

    /// SQL Server Types
    public final static int ORACLE = 0;
    public final static int MYSQL  = 1;

}
