/* ======================================
 * JFreeChart : a free Java chart library
 * ======================================
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
 * -----------------------
 * ImageDataAvailable.java
 * -----------------------
 * (C) Copyright 2002, by Bryan Scott and Contributors.
 *
 * Original Author:  Bryan Scott;
 * Contributor(s):   -;
 *
 *
 * Changes
 * -------
 * 14-Mar-2002 : Version 1 contributed by Bryan Scott (DG);
 * 05-Apr-2002 : Changed servletName (BRS);
 * 27-Jul-2002 : Moved package (BRS);
 * 24-Oct-2002 : Fixed errors reported by Checkstyle (DG);
 *
 */

package com.jrefinery.chart.demo.servlet;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

/**
 * ??
 *
 * @author BRS
 */
public class ImageDataAvailable extends BaseImageServlet implements Constants {

    /** ?? */
    private static final char ALPHA_START = 'b';

    /**
     * Override init() to set up data used by invocations of this servlet.
     *
     * @param config  the servlet config.
     *
     * @throws ServletException if there is a servlet related problem.
     */
    public void init(ServletConfig config) throws ServletException {
        setServletName("Servlet ImageDataAvailable");
        setDefaultChartType(20);
        super.init(config);
    }

    /**
     * Generates SQL.
     *
     * @param request  the servlet request.
     *
     * @return the SQL.
     */
    protected String generateSQL(HttpServletRequest request) {
        int voyage = 200001040;
        String[] obsCodes = null;

        try {
            voyage = Integer.parseInt(request.getParameter("voyage"));
        }
        catch (Exception e) {
            // nothing
        }

        try {
            obsCodes = request.getParameterValues("codes");
        }
        catch (Exception e) {
            // nothing
        }

        int i = 0;
        String query = null;

        if (obsCodes == null)  {
            obsCodes = new String[0];
        }


        //System.out.println("obs_codes length = " + obs_codes.length);

        if (voyage == 0) {
            query = "select min(a.timestamp), avg(a.record_count) as Avg_Track ";
            for (i = 0; i < obsCodes.length; ++i) {
                query += ", avg(" + ((char) (ALPHA_START + i)) + ".record_count) as Avg_"
                         + obsCodes[i].trim();
            }

            query += " from " + getDbSchema() + "summary_track a ";
            for (i = 0; i < obsCodes.length; ++i) {
                query += ", " + getDbSchema() + "summary_data " + ((char) (ALPHA_START + i));
            }

            for (i = 0; i < obsCodes.length; ++i) {
                if (i > 0) {
                    query += " and";
                }
                else {
                    query += " where";
                }

                query += " a.set_code  = " + ((char) (ALPHA_START + i)) + ".set_code  "
                      +  " and a.timestamp = " + ((char) (ALPHA_START + i)) + ".timestamp "
                      +  " and " + ((char) (ALPHA_START + i))
                      + ".obs_code = '" + obsCodes[i].trim() + "'";
            }

            query +=  " group by a.set_code order by a.set_code";

        }
        else {
            query = "select a.timestamp, a.record_count as Track ";

            for (i = 0; i < obsCodes.length; ++i) {
                query += "," + ((char) (ALPHA_START + i)) + ".record_count as "
                             + obsCodes[i].trim();
            }

            query += " from " + getDbSchema() + "summary_track a ";
            for (i = 0; i < obsCodes.length; ++i) {
                query += ", " + getDbSchema() + "summary_data " + ((char) (ALPHA_START + i));
            }

            query += " where a.set_code = " + voyage
                  + " and a.timestamp < (select max(timestamp) from "
                  + getDbSchema() + "summary_data where set_code = " + voyage + ")"
                  + " and a.timestamp > (select min(timestamp) from "
                  + getDbSchema() + "summary_data where set_code = " + voyage + ")";

            for (i = 0; i < obsCodes.length; ++i) {
                query += " and a.set_code  = " + ((char) (ALPHA_START + i)) + ".set_code  "
                      +  " and a.timestamp = " + ((char) (ALPHA_START + i)) + ".timestamp "
                      +  " and " + ((char) (ALPHA_START + i)) + ".obs_code = '"
                      + obsCodes[i].trim() + "'";
            }
        }
        return query;
    }

}
