/* =======================================
 * JFreeChart : a Java Chart Class Library
 * =======================================
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
 * -------------------
 * ImageSelectSQL.java
 * -------------------
 * (C) Copyright 2000-2002, by Bryan Scott and Contributors.
 *
 * Original Author:  Bryan Scott;
 * Contributor(s):   -;
 *
 *
 * Changes
 * -------
 * 14-Mar-2002 : Version 1 contributed by Bryan Scott (DG);\
 * 05-Apr-2002 : BRS. Changed servlet name
 *
 */

package com.jrefinery.chart.demo.jdbc.servlet;

import javax.servlet.*;
import javax.servlet.http.*;

/**
 * A generic image producer.  The servlet takes an sql string from the request and generates an
 * image based upon this.  The sql query should return results in the form
 * <pre>
 *   x_value_1, series_1_y_value, series_2_y_value, series_3_y_value .......etc
 *   x_value_2, series_1_y_value, series_2_y_value, series_3_y_value
 *   x_value_3, series_1_y_value, series_2_y_value, series_3_y_value
 *   x_value_4, series_1_y_value, series_2_y_value, series_3_y_value
 *   x_value_5, series_1_y_value, series_2_y_value, series_3_y_value
 *</pre>
 *
 * The supplied sql should not include the word select as this is added at the commencement.
 *
 */

public class ImageSelectSQL extends BaseImageServlet {
  /**
   * Override init() to set up data used by invocations of this servlet.
   *
   * @param  config                Description of the Parameter
   * @exception  ServletException  Description of the Exception
   */
  public void init(ServletConfig config)
    throws ServletException {
    servletName = "Servlet ImageSelectSQL";
    super.init(config);
  }

  /**
   *  Generate the sql from the request passed to it.   Only one servlet request parameter is parsed
   *  sql.  Select is prepended as a simple security constraint to prevent update attempts.
   *
   * @param  request  The html servlet request
   * @return          The generated sql
   */
  protected String generateSQL(HttpServletRequest request) {
    String query = null;
    try {
      query = request.getParameter("sql");
    } catch (Exception e) {}

    if (query != null)
      query = "select " + query;
    else
      query = "select sysdate, 2 from dual";

    //System.out.println(": " + query);
    return query;
  }

}
