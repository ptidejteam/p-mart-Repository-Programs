/* ======================================
 * JFreeChart : a free Java chart library
 * ======================================
 *
 * Project Info:  http://www.object-refinery.com/jfreechart/index.html
 * Project Lead:  David Gilbert (david.gilbert@object-refinery.com);
 *
 * (C) Copyright 2000-2003, by Simba Management Limited and Contributors.
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
 * (C) Copyright 2002, 2003, by Bryan Scott and Contributors.
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

/**
 * Constants for the servlet demo.
 *
 * @author Bryan Scott
 */
public interface Constants {

    /** The server. */
    public static final String DB_SERVER     = "DBserver.type";

    /** The user. */
    public static final String DB_USER       = "DBuser";

    /** The password. */
    public static final String DB_PASSWORD   = "DBpwd";

    /** The schema. */
    public static final String DB_SCHEMA     = "DBschema";

    /** The URL. */
    public static final String DB_URL        = "DBurl";

    /** The driver. */
    public static final String DB_DRIVER     = "DBdriver";

    /** The connection. */
    public static final String DB_CONNECTION = "DBconnection";

    /** The organisation. */
    public static final String ORGANISATION  = "Organisation";

    /** The image URL. */
    public static final String IMAGE_URL     = "ImageURL";

    /** The header URL. */
    public static final String HEADER_URL    = "HeaderURL";

    /** The footer URL. */
    public static final String FOOTER_URL    = "FooterURL";

    /** Debug string. */
    public static final String DEBUG         = "DEBUG";

    /** The temporary directory. */
    public static final String TEMP_DIR      = "TemporaryDir";

    /** The temporary URL. */
    public static final String TEMP_URL      = "TemporaryURL";

    /** @todo - Remove the following constants. */

    /** The header. */
    public static final String HEADER        = "Header";

    /** The header file. */
    public static final String HEADER_FILE   = "Header.File";

    /** The footer. */
    public static final String FOOTER        = "Footer";

    /** The footer file. */
    public static final String FOOTER_FILE   = "Footer.File";

    /** The caution file. */
    public static final String CAUTION_FILE  = "Caution.File";

    /** The shared voyages. */
    public static final String SHARED_VOYAGES      = "SHARED_VOYAGES";

    /** The shared voyages time. */
    public static final String SHARED_VOYAGES_TIME = "SHARED_VOYAGES_TIME";

    /** The shared database. */
    public static final String SHARED_DB           = "SHARED_DB";

    /** The database opener. */
    public static final String SHARED_DB_OPENER    = "SHARED_DB_OPENER";

    /** ??. */
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

    /** Response HTML */
    public static final int RESPONSE_HTML = 0;

    /** Response JPEG */
    public static final int RESPONSE_JPEG = 1;

    /** Response PNG */
    public static final int RESPONSE_PNG  = 2;

    /** Response SVG */
    public static final int RESPONSE_SVG  = 3;

    /** Response PDF */
    public static final int RESPONSE_PDF  = 4;

    /** Response PS */
    public static final int RESPONSE_PS   = 5;

    /** Oracle database. */
    public static final int ORACLE = 0;

    /** MySQL database. */
    public static final int MYSQL  = 1;

}
