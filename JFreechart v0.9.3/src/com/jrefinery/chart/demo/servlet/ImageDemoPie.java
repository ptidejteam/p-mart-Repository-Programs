/* ===============
 * JFreeChart Demo
 * ===============
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
 * 27-Jul-2002 : BRS. Moved package
 */

package com.jrefinery.chart.demo.servlet;

import javax.servlet.*;
import javax.servlet.http.*;
import java.util.ArrayList;
import java.awt.Font;

import com.jrefinery.chart.*;
import com.jrefinery.data.*;

/**
 * A generic image producer.  The servlet takes an sql string from the request and generates an
 * image based upon this.  The sql query should return results in the form
 * <pre>
 *   catagory_1, series_1_y_value
 *   catagory_2, series_1_y_value
 *   catagory_3, series_1_y_value
 *   catagory_4, series_1_y_value
 *   catagory_5, series_1_y_value
 *</pre>
 *
 * The supplied sql should not include the word select as this is added at the commencement.
 *
 */

public class ImageDemoPie extends BaseImageServlet {
  /**
   * Override init() to set up data used by invocations of this servlet.
   *
   * @param  config                Description of the Parameter
   * @exception  ServletException  Description of the Exception
   */
  public void init(ServletConfig config)
    throws ServletException {
    servletName = "Servlet ImagePie";
    defaultChartType = 1 ;
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
    String query = null ;

    String plotYear   ;
    String plotMonth  ;
    String plotDay    ;
    String voyage     ;

    try {
      voyage = request.getParameter("voyage");
    } catch (Exception e) {
      voyage = "200102040" ;
    }


    try {
      plotDay = request.getParameter("date");
    } catch (Exception e) {
      plotDay = "05" ;
    }

    try {
      plotMonth = request.getParameter("month");
    } catch (Exception e) {
      plotMonth = "01" ;
    }

    try {
      plotYear = request.getParameter("year");
    } catch (Exception e) {
      plotYear = "2002";
    }

    /// Sort out date thing as different servers handle differently
    switch (sqlServerType) {
      case MYSQL:
        query = " and timestamp = '"+plotYear+"-"+plotMonth+"-"+plotDay+" 00:00:00'" ;
        break ;

      case ORACLE:
      default:
        query = " and timestamp = to_date('"+plotYear+"-"+plotMonth+"-"+plotDay+"', 'yyyy-mon-dd')" ;
        break ;
    }

    query = "select obs_code, record_count"
          + " from " + dbSchema + "summary_data"
          + " where set_code = " + voyage
          + query ;

    if (debug)
      System.out.println(": " + query);

    return query;
  }

  /// Override to create a default title
  public void modifyChart(JFreeChart chart, HttpServletRequest request) {
    String title = null ;

    String plotYear   ;
    String plotMonth  ;
    String plotDay    ;
    String voyage     ;

    try {
      title = request.getParameter("title");
    } catch (Exception e) {
    }

    if ((title == null) || (title.length() < 2)) {
      try {
        voyage = request.getParameter("voyage");
      } catch (Exception e) {
        voyage = "200102040" ;
      }

      try {
        plotDay = request.getParameter("date");
      } catch (Exception e) {
        plotDay = "05" ;
      }

      try {
        plotMonth = request.getParameter("month");
      } catch (Exception e) {
        plotMonth = "01" ;
      }

      try {
        plotYear = request.getParameter("year");
      } catch (Exception e) {
        plotYear = "2002";
      }

      title = "Voyage " + voyage + " on " + plotYear + "-" + plotMonth + "-" + plotDay ;

      if (chart != null) {
        ArrayList titles = new ArrayList();
        TextTitle subtitle = new TextTitle(title, new Font("SansSerif", Font.BOLD, 12));
        titles.add(subtitle);
        chart.setTitles(titles);
      }
    }
  }

}
