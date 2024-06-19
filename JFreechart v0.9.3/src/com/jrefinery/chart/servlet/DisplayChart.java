/* ============================================
 * JFreeChart : a free Java chart class library
 * ============================================
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
 * DisplayChart.java
 * -------------------
 * (C) Copyright 2002, by Richard Atkinson and Contributors.
 *
 * Original Author:  Richard Atkinson (richard_c_atkinson@ntlworld.com);
 * Contributor(s):   -;
 *
 * $Id: DisplayChart.java,v 1.1 2007/10/10 19:52:24 vauchers Exp $
 *
 * Changes
 * -------
 * 19-Aug-2002 : Version 1;
 *
 */
package com.jrefinery.chart.servlet;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.File;

/**
 * Servlet used for streaming charts to the client browser from the temporary
 * directory.  You need to add this servlet to your deployment descriptor (web.xml)
 * in order to get it to work.  The syntax is as follows:
 * <xmp>
 * <servlet>
 *    <servlet-name>DisplayChart</servlet-name>
 *    <servlet-class>com.jrefinery.chart.servlet.DisplayChart</servlet-class>
 * </servlet>
 * </xmp>
 */
public class DisplayChart extends javax.servlet.http.HttpServlet {

    public DisplayChart() {
		super();
    }

	public void init() throws ServletException {
		return;
	}

	public void service(javax.servlet.http.HttpServletRequest request,
						javax.servlet.http.HttpServletResponse response)
			throws javax.servlet.ServletException, java.io.IOException {

		HttpSession session = request.getSession();
		String filename = request.getParameter("filename");

		if (filename == null) {
			throw new ServletException("Parameter 'filename' must be supplied");
		}

		//  Replace ".." with ""
		//  This is to prevent access to the rest of the file system
		filename = ServletUtilities.searchReplace(filename, "..", "");

		//  Check the file exists
		File file = new File(System.getProperty("java.io.tmpdir"),filename);
		if (!file.exists()) {
			throw new ServletException("File '" + file.getAbsolutePath() + "' does not exist");
		}

		//  Check that the graph being served was created by the current user
		//  or that it begins with "public"
		boolean isChartInUserList = false;
		ChartDeleter chartDeleter = (ChartDeleter)session.getAttribute("JFreeChart_Deleter");
		if (chartDeleter != null) {
			isChartInUserList = chartDeleter.isChartAvailable(filename);
		}

		boolean isChartPublic = false;
		if (filename.length() >= 6) {
			if (filename.substring(0,6).equals("public")) {
				isChartPublic = true;
			}
		}

		if (isChartInUserList || isChartPublic) {
			//  Serve it up
			ServletUtilities.sendTempFile(file,response);
		} else {
			throw new ServletException("Chart image not found");
		}
		return;
	}

}
