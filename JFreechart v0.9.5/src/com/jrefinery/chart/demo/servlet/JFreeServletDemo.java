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
 * -----------------
 * JFreeServlet.java
 * -----------------
 * (C) Copyright 2002, by Bryan Scott and Contributors.
 *
 * Original Author:  Bryan Scott;
 * Contributor(s):   -;
 *
 *
 * Changes
 * -------
 * 27-Jul-2002 : Version 1 contributed by Bryan Scott (DG);
 * 22-Oct-2002 : Fixed errors reported by Checkstyle (DG);
 *
 */

/** @todo Simplify **/

package com.jrefinery.chart.demo.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ResourceBundle;
import java.util.Vector;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.jrefinery.chart.JFreeChart;
import com.jrefinery.chart.demo.JFreeChartDemoBase;

/**
 * A servlet view of the JFreeChart Demo.
 * <P>
 * Currently first cut.  Any suggestions cleaning up appreciated.
 *
 * @author BRS
 */
public class JFreeServletDemo extends BaseImageServlet {

    /** Base chart generator shared between Servlet and Swing demos. */
    private JFreeChartDemoBase demo = new JFreeChartDemoBase();

    /** The chart commands. */
    public static final String[][] CHART_COMMANDS = JFreeChartDemoBase.CHART_COMMANDS;

    /** Localised resources. */
    private ResourceBundle resources;

    /** The tab titles. */
    private String[] tabTitles;

    /**
     * Override init() to set up data used by invocations of this servlet.
     *
     * @param config  configuration information.
     *
     * @throws ServletException if there is a problem.
     */
    public void init(ServletConfig config) throws ServletException {

        this.resources = demo.getResources();
        setServletName("JFreeServletDemo");

        // Start : Loading tab titles from resource file.
        int tab = 1;
        Vector titles = new Vector(0);
        String title = null;

        while (tab > 0) {
            try {
                title = resources.getString("tabs." + tab);
                if (title != null) {
                    titles.add(title);
                }
                else {
                    tab = -1;
                }
                ++tab;
            }
            catch (Exception ex) {
                tab = -1;
            }
        }

        if (titles.size() == 0) {
            titles.add("Default");
        }

        tab = titles.size();
        this.tabTitles = new String[tab];

        --tab;
        for (; tab >= 0; --tab) {
            title = titles.get(tab).toString();
            this.tabTitles[tab] = title;
        }
        titles.removeAllElements();
        // Finish : Loading tab titles from resource file.

        super.init(config);
    }

    /**
     * Basic servlet method, answers requests from the browser.
     * Implementation is passed off to the doPost method.
     *
     * @param request  HTTPServletRequest
     * @param response              HTTPServletResponse
     *
     * @throws ServletException  Description of the Exception
     * @throws IOException  Description of the Exception
     */
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String url = request.getContextPath() + request.getServletPath();

        /// Find the selected tab.
        int selectedTab = 0;
        try {
            selectedTab = Integer.parseInt(request.getParameter("tab"));
        }
        catch (Exception ex) {
            // ignored
        }
        PrintWriter html = response.getWriter();

        /// Print Header
        includeUrl(html, getHeaderURL());

        /// Print Tabs
        html.println("<div align=\"center\"><center>");
        html.println("<table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"800\" "
                     + "bgcolor=\"#C0C0C0\">");
        html.println("<tr>");
        int i = 0;
        int j = tabTitles.length;
        for (i = 0; i < j; ++i) {
          html.println("<td><A href=\"?tab=" + i + "\">");
          html.println(tabTitles[i]);
          html.println("</A></td>");
        }
        html.println("</tr></table></center></div>");

        /// Print Appropriate Page for the tab.
        html.println("<div align=\"center\"><center>");
        html.println("<table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"800\" "
                     + "bgcolor=\"#C0C0C0\">");
        String usage = null;
        String title = null;
        String description = null;
        for (i = 0; i <= CHART_COMMANDS.length - 1; ++i) {
            int tab = 0;
            try {
                tab = Integer.parseInt(resources.getString(CHART_COMMANDS[i][2] + ".tab"));
                --tab;
            }
            catch (Exception ex) {
                System.err.println("Demo : Error retrieving tab identifier for chart "
                                   + CHART_COMMANDS[i][2]);
                System.err.println("Demo : Error = " + ex.getMessage());
                tab = 0;
            }
            if ((tab < 0) || (tab >= j)) {
                tab = 0;
            }

            if (tab == selectedTab) {
                try {
                    usage = resources.getString(CHART_COMMANDS[i][2] + ".usage");
                }
                catch (Exception ex) {
                    usage = null;
                }

                if ((usage == null) || usage.equalsIgnoreCase("All")
                                    || usage.equalsIgnoreCase("Servlet")) {

                    title = resources.getString(CHART_COMMANDS[i][2] + ".title");
                    description = resources.getString(CHART_COMMANDS[i][2] + ".description");
                    html.println("<tr><TD>" + title);
                    html.println("</TD><TD><textarea rows=\"2\" cols=\"60\">" + description);
                    html.println("</textarea></TD><TD>");
                    html.println("<form method=\"POST\" action=\"" + url + "\">");
                    html.println("<input type=\"hidden\" name=\"chart\" value=\"" + i + "\">");
                    html.println("<input type=\"submit\" value=\"Display\" >");
                    html.println("</form></td></tr>");

                }
            }
        }
        html.println("</table></center></div>");

        /// Print Footer
        includeUrl(html, getFooterURL());
        html.flush();
    }

    /**
     * This is used by the standard doPost method of BaseImageServlet to get the chart to display.
     *
     * @param request  the request.
     *
     * @return the chart.
     */
    protected JFreeChart createChart(HttpServletRequest request) {

        int selectedChart = 0;
        try {
            selectedChart = Integer.parseInt(request.getParameter("chart"));
        }
        catch (Exception ex) {
            // ignored
        }
        return demo.getChart(selectedChart);

    }

}
