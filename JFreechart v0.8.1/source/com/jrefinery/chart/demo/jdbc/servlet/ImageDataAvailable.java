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
 *
 */

package com.jrefinery.chart.demo.jdbc.servlet;

import javax.servlet.*;
import javax.servlet.http.*;

public class ImageDataAvailable extends BaseImageServlet implements Constants {

   final static char alphaStart = 'b' ;

  /**
    * Override init() to set up data used by invocations of this servlet.
    */
  public void init(ServletConfig config) throws ServletException {
    super.init(config);
    servletName = "Marine Voyage Information ";
  }

  protected String generateSQL(HttpServletRequest request) {
    int voyage     = 200001040 ;
    String[] obs_codes = null ;
    try{ voyage = Integer.parseInt( request.getParameter("voyage")); } catch(Exception e) {}
    try{ obs_codes  = request.getParameterValues("codes");            } catch(Exception e) {}

    int i = 0 ;
    String query = null ;

    if (obs_codes == null)  {
        obs_codes = new String[0] ;
    }


    //System.out.println("obs_codes length = " + obs_codes.length);

    if (voyage == 0) {
      query = "select min(a.timestamp), avg(a.record_count) as Avg_Track " ;
      for (i = 0; i < obs_codes.length; ++i) {
        query += ", avg(" + ((char)(alphaStart+i)) + ".record_count) as Avg_"
              + obs_codes[i].trim();
      }

      query += " from "+ dbSchema_ +"summary_track a " ;
      for (i = 0; i < obs_codes.length; ++i) {
        query += ", "+ dbSchema_ +"summary_data " + ((char)(alphaStart+i)) ;
      }

      for (i = 0; i < obs_codes.length; ++i) {
        if (i > 0)
          query += " and" ;
        else
          query += " where";

        query += " a.set_code  = " + ((char)(alphaStart+i)) + ".set_code  "
              +  " and a.timestamp = " + ((char)(alphaStart+i)) + ".timestamp "
              +  " and " + ((char)(alphaStart+i))
              + ".obs_code = '"+obs_codes[i].trim()+"'";
      }

      query +=  " group by a.set_code order by a.set_code" ;

    } else {
      query = "select a.timestamp, a.record_count as Track " ;

      for (i = 0; i < obs_codes.length; ++i) {
        query += "," + ((char)(alphaStart+i)) + ".record_count as " + obs_codes[i].trim();
      }

      query += " from "+ dbSchema_ +"summary_track a " ;
      for (i = 0; i < obs_codes.length; ++i) {
        query += ", "+ dbSchema_ +"summary_data " + ((char)(alphaStart+i)) ;
      }

      query += " where a.set_code = " + voyage
            + " and a.timestamp < (select max(timestamp) from "
            + dbSchema_ +"summary_data where set_code = " + voyage + ")"
            + " and a.timestamp > (select min(timestamp) from "
            + dbSchema_ +"summary_data where set_code = " + voyage + ")" ;

      for (i = 0; i < obs_codes.length; ++i) {
        query += " and a.set_code  = " + ((char)(alphaStart+i)) + ".set_code  "
              +  " and a.timestamp = " + ((char)(alphaStart+i)) + ".timestamp "
              +  " and " + ((char)(alphaStart+i)) + ".obs_code = '"+obs_codes[i].trim()+"'";
      }
    }
    return query ;
 }


}
