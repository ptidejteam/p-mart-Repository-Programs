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
 * ------------------
 * BaseImageServlet.java
 * ------------------
 * (C) Copyright 2002, by Bryan Scott and Contributors.
 *
 * Original Author:  Bryan Scott;
 * Contributor(s):   -;
 *
 *
 * Changes
 * -------
 * 14-Mar-2002 : Version 1 contributed by Bryan Scott (DG);
 * 05-Apr-2002 : Downgraded all servlet 2.3 specific calls to allow compilation
 *               and running under servlet 2.2 api (BRS)
 * 06-Apr-2002 : Added debug variable, statusMessage procedure and change doPost
 *               to output generated sql if debug set. (BRS)
 * 29-Apr-2002 : Changed type values to include pie data sets. (BRS)
 * 17-May-2002 : Did a fix for statements remaining open. (BRS)
 * 17-May-2002 : Did a fix for non timeseries XY Charts (BRS)
 * 28-Jun-2002 : Updated to include html generating code. This involved a reorg
 *               of the code. (BRS)
 * 27-Jul-2002 : BRS. Moved package
 */

/** @todo Baseimageservlet
 *  Remove jdbc specific code, then extend to create baseimagejdbcservlet
 **/

package com.jrefinery.chart.demo.servlet;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.image.*;
import java.io.*;
import java.sql.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.net.*;
import java.text.SimpleDateFormat;
import javax.swing.*;

import com.jrefinery.data.*;
import com.jrefinery.chart.*;
import com.jrefinery.chart.ui.*;
import com.jrefinery.chart.data.*;
import com.jrefinery.chart.entity.*;

/// SVG Support
import org.apache.batik.dom.GenericDOMImplementation;
import org.apache.batik.svggen.*;
import org.w3c.dom.*;
import org.w3c.dom.DOMImplementation;

/**
 * A Base image servlet generator.  Used to provide common base and methods to servlets which
 * need to generate a chart from sql data sources
 *
 * To extend overwrite method : generateSQL.
 *
 * Will attempt to get configuration constants from the firstly the servlet container configuration
 * and then secondly the servlet's initialization parameters.  The servlets initialization
 * parameters taking precedence.  Available configuration options are
 *  a. DBuser   - The database user to connect as
 *  b. DBpwd    - The password for the database user
 *  c. DBschema - The schema to utilise
 *  d. DBurl    - The connection URL to the database
 *  e. DBdriver - The JDBC driver to utilise to connect to the database
 *  f. CacheType- Specifies the type of chart cache to utilise when generating
 *                HTML. (Optional, default of Dynamic)
 *  g. TemporaryDir - Specifies the directory to locate chart cache (Optional
 *                    for Dynamic CacheTypes, mandatory for other types)
 *  h. TemporaryUrl - Specifies the url to the temporaryDir (Mandatory for
 *                    Temp_Url CacheType, Optional for other types)
 *  i. HeaderUrl    - The url for the header included in html page.
 *                    may be file:// (Optional)
 *  j. FooterUrl    - The url for the footer included in html page.
 *                    may be file:// (Optional)
 *
 * The servlet will check to see if a connection is shared amongst the servlets context already. If
 * not it will create a connection and share via the servlet container.
 *
 * Supports the following url options
 * NAME        TYPE     NOTES
 * type        integer  the type of chart to generate. eg moving average, linear fit etc
 *                      under development.
 * width       integer  The width of output in pixels.  Clipped into the range 10-2000
 * height      integer  The height of the output in pixels. Clipped into the range 10-1000
 * initColor   integer  Between 0-11, used to indicate the initial shading of the background
 * finalColor  integer  Between 0-11, used to indicate the final  shading of the background
 * title       String   The chart title
 * xaxistitle  String   The x axis title
 * yaxistitle  String   The y axis title
 * zero        String   if passed a value of 'true' then the chart will include zero
 * showLegend  String   if passed a value of 'false' then the chart will not include legend
 * output      String   Selection of the type of output requested.  jpeg, gif, svg
 *
 *
 * CAUTION : THE FOLLOWING ARE UNTESTED / UNDER DEVELOPMENT
 *  1. SVG support using apache batik.
 *  2. PDF support
 *  3. Charting options such as moving average, linear fit.
 *
 * @see              JFreeChart
 * @see              JFreeChartServletDemo
 */
public class BaseImageServlet extends HttpServlet implements Constants {

  static final protected SimpleDateFormat df = new SimpleDateFormat("ddMMMyyyy-HHmmss");

  protected int sqlServerType = ORACLE;

  /**  The servlets name */
  protected String servletName = "Base Chart ";

  /** Whether or not to enable debug information output **/
  protected boolean debug = false;

  /**  Title of the chart */
  protected String chartTitle = "Data";

  /**  Title of the x axis */
  protected String xAxisTitle = "";

  /**  Title of the y axis */
  protected String yAxisTitle = "";

  final static char alphaStart = 'b';

  protected int defaultChartType = 20;

  Connection con;

  /// Some standard defaults but picked up from config file.
  String dbDriver = "NONE";
  String dbUrl = "jdbc:oracle:thin:@yourdb:1521:prod";
  String dbUser = "your_user";
  String dbPwd = "your_password";
  public String dbSchema = null;


  /// HTML Generation variables
  String headerUrl = null;
  String footerUrl = null;
  String tempDir   = null;
  String tempUrl   = null;

  /**
   * Cache Types :
   *
   * 0 : include through a servlet request to cache, do delete file
   *     after initial retrival
   * 1 : include through a servlet request to cache, do not delete file
   *     after initial retrival
   * 2 : include image through url reference, , do not delete file
   *     after initial retrival
   * 3 : Dynamic (No ImageMap Support)
   * 4 : Dynamic with imagemap support (Note requires chart to be
   *     generated twice)
   **/
  public final static int IMAGE_CACHE_DELETE = 0;
  public final static int IMAGE_CACHE_NO_DELETE = 1;
  public final static int IMAGE_TEMP_URL = 2;
  public final static int IMAGE_DYNAMIC_IMAGEMAP = 3;
  public final static int IMAGE_DYNAMIC_NO_IMAGEMAP = 4;
  int includeType = IMAGE_TEMP_URL;


  /**
   * Basic servlet method, answers requests from the browser.
   * Implementation is passed off to the doPost method.
   *
   * @param  request               HTTPServletRequest
   * @param  response              HTTPServletResponse
   * @exception  ServletException  Description of the Exception
   * @exception  IOException       Description of the Exception
   */
  public void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    doPost(request, response);
  }

  /**
   * Processes the HTTP request.
   * Supports different types of output requests, which are specified through
   * the 'output' parameter of the request. The types of output requests
   * supported are:
   * HTML  - Generates the HTML output
   * CACHE - Retrieves an image from the cache
   * IMAGE - Generates and returns the image. (The default if none specified)
   *
   * Each output type is handled by calling the appropriate method: doHTML,
   * doCache, and doImage.
   *
   * @param  request               Description of the Parameter
   * @param  response              Description of the Parameter
   * @exception  ServletException  Description of the Exception
   * @exception  IOException       Description of the Exception
   */
  public void doPost(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {

    String type = null;
    try {
      type = request.getParameter("output").trim().toUpperCase();
    } catch (Exception ex) {
      type = "Image";
    }

    statusMessage(" : generating output " + type);

    if (type == null) {
      doImage(request, response);
    } else if (type.equals("HTML")) {
      doHtml(request, response);
    } else if (type.equals("CACHE")) {
      doCache(request, response);
    } else {
      doImage(request, response);
    }
  }


  /**
   * Modify a chart based on request parameters
   * Override this method if you would like to modify the generated chart,
   * Generally for chart look and feel.
   *
   * @param  chart  The Chart
   */
  protected void modifyChart(JFreeChart chart, HttpServletRequest request) {
  }

  /**
   * Generate the SQL required from the http request
   * Override this method to generate the appropriate sql for a request.
   *
   * @param  request  The servlets request parameters
   * @return          The generated sql string
   */
  protected String generateSQL(HttpServletRequest request) {
    return "select 1, 10 from dual";
  }


  /**
   * Create an XY chart
   *
   * @param  type  Type of chart to create.  Currently not supported.
   * @param  sql   SQL to execute.  1st column is x values, following columns are y values
   * @return       The chart of the data
   */
  protected JFreeChart createXYChart(int type, String sql) {
    JFreeChart chart;
    JdbcXYDataset chartData;
    XYDataset xyData;

    chartData = new JdbcXYDataset(con, sql);
    try {
      switch (type) {
        case 21:
          // moving avg
          MovingAveragePlotFitAlgorithm mavg = new MovingAveragePlotFitAlgorithm();
          mavg.setPeriod(30);
          PlotFit pf = new PlotFit(chartData, mavg);
          xyData = pf.getFit();
          break;
        case 22:
          // linear fit
          pf = new PlotFit(chartData, new LinearPlotFitAlgorithm());
          xyData = pf.getFit();
          break;
        case 0:
        default:
          xyData = (XYDataset) chartData;
        break;
      }

      //if (debug) {
      //  System.err.println("Data series count="+xyData.getSeriesCount()
      //         + ", item count (series 0) " + xyData.getItemCount(0));
      //}

      if (chartData.isTimeSeries)
        chart = ChartFactory.createTimeSeriesChart("", "", "", xyData, true);
      else
        chart = ChartFactory.createXYChart("", "", "", xyData, true);


      /// Customise the vertical axis
      VerticalNumberAxis vnAxis = (VerticalNumberAxis) chart.getXYPlot().getRangeAxis();
      vnAxis.setAutoRangeIncludesZero(false);
      vnAxis.setCrosshairVisible(false);
      vnAxis.configure();

      /// Customise the horizontal axis
      ValueAxis axis = (ValueAxis) chart.getXYPlot().getDomainAxis();
      axis.setCrosshairVisible(false);
      axis.configure();

      return chart;
    } catch (Exception e) {
      System.out.println(e.getMessage());
      if (debug)
        e.printStackTrace();
      return null;
    }

  }

  /**
   * Create a chart
   *
   * @param  type  Type of chart to create.  Currently not supported.
   * @param  sql   SQL to execute.  1st column is category, followed by value
   * @return       The chart of the data
   */
  protected JFreeChart createPieChart(int type, String sql) {
    JFreeChart chart;
    JdbcPieDataset chartData;

    try {
      chartData = new JdbcPieDataset(con);
      chartData.executeQuery(sql);
      chart = ChartFactory.createPieChart("", chartData, true);
      return chart;
    } catch (Exception e) {
      System.out.println(e.getMessage());
      return null;
    }
  }

  /**
   * Create a chart
   *
   * @param  type  Type of chart to create.  Currently not supported.
   * @param  sql   SQL to execute.  1st column is category, followed by value
   * @return       The chart of the data
   */
  protected JFreeChart createCategoryChart(int type, String sql) {
    JFreeChart chart;
    JdbcCategoryDataset chartData;

    //if (debug) System.out.println("Creating a category chart");
    try {
      chartData = new JdbcCategoryDataset(con);
      chartData.executeQuery(sql);
      //if (debug) System.out.println("  --> Factory");
      switch (type) {
        case 11:

          chart = ChartFactory.createVerticalBarChart3D("","","",chartData, true);
          break;
        case 12:
          chart = ChartFactory.createStackedVerticalBarChart("","","",chartData, true);
          break;
        case 13:
          chart = ChartFactory.createStackedVerticalBarChart3D("","","",chartData, true);
          break;
        case 14:
          chart = ChartFactory.createHorizontalBarChart("","","",chartData, true);
          break;
        case 15:
          chart = ChartFactory.createHorizontalBarChart3D("","","",chartData, true);
          break;
        default:
          chart = ChartFactory.createVerticalBarChart("","","",chartData, true);
        break;
      }
      return chart;
    } catch (Exception e) {
      System.out.println(e.getMessage());
      e.printStackTrace();
      return null;
    }
  }
  /**
   *  Override init() to set up data used by invocations of this servlet.
   *
   * @param  config                Description of the Parameter
   * @exception  ServletException  Description of the Exception
   */
  public void init(ServletConfig config)
      throws ServletException {
    String test = null;

    super.init(config);

    /// Load parameters, Try initially using context setting and then specific to this servlet.
    try { sqlServerType = Integer.parseInt(getServletContext().getInitParameter(DB_SERVER));  } catch (Exception e) { e.printStackTrace(); }
    try { dbUser = getServletContext().getInitParameter(DB_USER); } catch (Exception e) { e.printStackTrace(); }
    try { dbPwd  = getServletContext().getInitParameter(DB_PASSWORD); } catch (Exception e) { e.printStackTrace(); }
    try { dbUrl  = getServletContext().getInitParameter(DB_URL); } catch (Exception e) { e.printStackTrace(); }
    try { tempUrl = getServletContext().getInitParameter(TEMP_URL); } catch (Exception e) { e.printStackTrace(); }
    try { tempDir = getServletContext().getInitParameter(TEMP_DIR); } catch (Exception e) { e.printStackTrace(); }
    try { dbDriver  = getServletContext().getInitParameter(DB_DRIVER); } catch (Exception e) { e.printStackTrace(); }
    try { dbSchema  = getServletContext().getInitParameter(DB_SCHEMA); } catch (Exception e) { e.printStackTrace(); }
    try { headerUrl = getServletContext().getInitParameter(HEADER_URL); } catch (Exception e) { e.printStackTrace(); }
    try { footerUrl = getServletContext().getInitParameter(FOOTER_URL); } catch (Exception e) { e.printStackTrace(); }

    try {
      test = getServletContext().getInitParameter("CacheType");
      if (test != null) {
        test = test.trim().toUpperCase();
        statusMessage("Setting include type to '"+test+"'");
        if (test.equals("CACHE_DELETE")) {
          includeType = IMAGE_CACHE_DELETE;
        } else if (test.equals("CACHE_NO_DELETE")) {
          includeType = IMAGE_CACHE_NO_DELETE;
        } else if (test.equals("TEMP_URL")) {
          includeType = IMAGE_TEMP_URL;
        } else if (test.equals("DYNAMIC_IMAGEMAP")) {
          includeType = IMAGE_DYNAMIC_IMAGEMAP;
        } else if (test.equals("DYNAMIC_NO_IMAGEMAP")) {
          includeType = IMAGE_DYNAMIC_NO_IMAGEMAP;
        } else if (test.equals("TEMP_URL")) {
          includeType = IMAGE_TEMP_URL;
        } else {
          includeType = IMAGE_TEMP_URL;
        }

        if ((includeType == IMAGE_TEMP_URL)
            && ((tempDir == null) || (tempDir == null))) {
          statusMessage("Cannot set image cache type to TEMP_URL");
          statusMessage("Temporary directory and url not specified correctly");
          statusMessage("Reverting to CACHE_DELETE");
          includeType = IMAGE_CACHE_DELETE;
        }
      }
    } catch (Exception e) {
    }

    try {
      test = getServletContext().getInitParameter(DEBUG);
      if (test != null) {
        test = test.trim().toUpperCase();
        statusMessage("Setting debug to '"+test+"'");
        debug = test.equals("TRUE");
      }
    } catch (Exception e) {
      e.printStackTrace();
    }

    try {
      test = getInitParameter(DB_USER);
    } catch (Exception e) {
      e.printStackTrace();
    }
    if ((test != null) && (test.length() > 0)) {
      dbUser = test;
    }
    try {
      test = getInitParameter(DB_PASSWORD);
    } catch (Exception e) {
      e.printStackTrace();
    }
    if ((test != null) && (test.length() > 0)) {
      dbPwd = test;
    }
    try {
      test = getInitParameter(DB_URL);
    } catch (Exception e) {
      e.printStackTrace();
    }
    if ((test != null) && (test.length() > 0)) {
      dbUrl = test;
    }
    try {
      test = getInitParameter(DB_DRIVER);
    } catch (Exception e) {
      e.printStackTrace();
    }
    if ((test != null) && (test.length() > 0)) {
      dbDriver = test;
    }
    try {
      test = getInitParameter(DB_SCHEMA);
    } catch (Exception e) {
      e.printStackTrace();
    }
    if ((test != null) && (test.length() > 0)) {
      dbSchema = test;
    }

    try {
      test = getInitParameter("ChartTitle");
    } catch (Exception e) {
      e.printStackTrace();
    }
    if ((test != null) && (test.length() > 0)) {
      chartTitle = test;
    }
    try {
      test = getInitParameter("xAxisTitle");
    } catch (Exception e) {
      e.printStackTrace();
    }
    if ((test != null) && (test.length() > 0)) {
      xAxisTitle = test;
    }

    try {
      test = getInitParameter("yAxisTitle");
    } catch (Exception e) {
      e.printStackTrace();
    }
    if ((test != null) && (test.length() > 0)) {
      yAxisTitle = test;
    }

    try {
      test = getInitParameter(HEADER_URL);
    } catch (Exception e) {
      e.printStackTrace();
    }
    if ((test != null) && (test.length() > 0)) {
      headerUrl = test;
    }

    try {
      test = getInitParameter(FOOTER_URL);
    } catch (Exception e) {
      e.printStackTrace();
    }
    if ((test != null) && (test.length() > 0)) {
      footerUrl = test;
    }


    if ((dbSchema != null) && (dbSchema.length() > 0)) {
      if (!dbSchema.endsWith(".")) {
        dbSchema = dbSchema + ".";
      }
    } else {
      dbSchema = " ";
    }

    // Confirmation to screen.
    statusMessage("Debug    : " + debug);

    if ((sqlServerType >= 0) && (!dbDriver.equalsIgnoreCase("None"))) {
      statusMessage("Driver   : " + dbDriver);
      statusMessage("Database : " + dbUrl);
      statusMessage("Schema   : " + dbSchema);
      statusMessage("User     : " + dbUser);
      statusMessage("Password : #######");
      openConnection();
    }
  }

  /**
   *
   * @param request
   * @param response
   * @throws ServletException
   * @throws IOException
   */
  protected void doHtml(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {

    statusMessage("doHTML output starting, temp URL = " + tempUrl);
    int type = getImageOutputType(request);
    int outputType = getImageOutputType(request);
    Dimension size = getImageSize(request);

    String tempFile = null;
    File outputFile = null;
    JFreeChart chart = null;
    ChartRenderingInfo info = null;

    /// Every output type but 'Dynamic with no imagemap' requires the chart
    /// to be generated and saved locally first.
    if (includeType != IMAGE_DYNAMIC_NO_IMAGEMAP) {
      tempFile = request.getRemoteHost().replace('.','x')
               + df.format(new java.util.Date())
               + '.' + CONTENT_TYPE[type][0];
      outputFile = new File(tempDir + tempFile);
      chart = createChart(request);
      OutputStream out = new BufferedOutputStream(new FileOutputStream(outputFile));
      info = writeChart(chart, out, outputType, size);
    }

    /// Have we generated valid output?
    if ((chart != null) || (includeType == IMAGE_DYNAMIC_NO_IMAGEMAP)) {
      response.setContentType(CONTENT_TYPE[RESPONSE_HTML][1]);
      PrintWriter html = response.getWriter();
      includeUrl(html, headerUrl);

      if (info != null)
        ChartUtilities.writeImageMap(html, "chart", info);

      switch (includeType) {
        case IMAGE_CACHE_DELETE:
        case IMAGE_CACHE_NO_DELETE:
          html.print("<IMG SRC=\""+ request.getContextPath() + request.getServletPath()
                     + "?output=CACHE&imagetype=" + outputType + "&file=" + tempFile );
          break;
        case IMAGE_TEMP_URL:
          html.print("<IMG SRC=\""+ tempUrl + tempFile);
          break;
        case IMAGE_DYNAMIC_NO_IMAGEMAP:
        case IMAGE_DYNAMIC_IMAGEMAP:
          html.print("<IMG SRC=\"" + request.getContextPath() + request.getServletPath());
          html.print("?OUTPUT=IMAGE");
          Enumeration attr = request.getAttributeNames();
          while (attr.hasMoreElements()) {
            String attribute = attr.nextElement().toString();
            if (!attribute.trim().equalsIgnoreCase("OUTPUT"))
              html.print('&' + response.encodeURL(attribute + '=' + request.getAttribute(attribute)));
          }
          break;
      }
      html.print("\" WIDTH=\""+ size.width +
                 "\" HEIGHT=\""+ size.height +
                 "\" BORDER=\"0\"" +
                 " ALT=\"Chart\"");
      if (info != null)
        html.print(" USEMAP=\"#chart\"");
      html.println(">");
      includeUrl(html, footerUrl);
      html.flush();
    } else {
      doErrorPage(response, "Server Temporary Directories not set!");
    }
    statusMessage("doHTML output complete");
  }

  protected void doCache(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    statusMessage("doCache output starting");

    int outputType = getImageOutputType(request);
    String file = request.getParameter("file");

    statusMessage(" : file = " + tempDir + file);
    statusMessage(" : type = " + CONTENT_TYPE[outputType][1]);

    File output = null;

    if (file != null) {
      output = new File(tempDir + file);
    }

    if ((output == null) || (!output.exists())) {
      System.err.println(servletName + "File Not Found : " + tempDir + file);
      this.doErrorPage(response, "Requested chart not found in cache.  Please request again");
    } else {
      response.setContentType(CONTENT_TYPE[outputType][1]);
      includeFile(response.getOutputStream(), output);
      response.flushBuffer();
      if (includeType == IMAGE_CACHE_DELETE)
        output.delete();
    }
    statusMessage("doCache output complete");
  }


  /**
   *
   * @param request
   * @param response
   * @throws ServletException
   * @throws IOException
   */
  protected void doImage(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    statusMessage("doImage output starting");
    int outputType = getImageOutputType(request);
    Dimension size = getImageSize(request);

    response.setContentType(CONTENT_TYPE[outputType][1]);
    JFreeChart chart = createChart(request);
    if (chart != null) {
      OutputStream out = response.getOutputStream();
      writeChart(chart, out, outputType, size);
    }
    statusMessage("doImage output complete");
  }


  protected ChartRenderingInfo writeChart(JFreeChart chart, OutputStream out, int outputType, Dimension size) {
    ChartRenderingInfo info = new ChartRenderingInfo(new StandardEntityCollection());
    try {
      switch (outputType) {
        case RESPONSE_PNG:
          ChartUtilities.writeChartAsPNG(out, chart, size.width, size.height, info);
          break;
        case RESPONSE_SVG:
          OutputStreamWriter writer = new OutputStreamWriter(out);

          // Get a DOMImplementation
          DOMImplementation domImpl = GenericDOMImplementation.getDOMImplementation();
          // Create an instance of org.w3c.dom.Document
          org.w3c.dom.Document document = domImpl.createDocument(null, "svg", null);

          /// Create an SVG Context to customise
          SVGGeneratorContext ctx = SVGGeneratorContext.createDefault(document);
          ctx.setComment("Generated by JFreeServlet using Batik SVG Generator");

          // Create an instance of the SVG Generator
          SVGGraphics2D svgGenerator = new SVGGraphics2D(ctx, false);
          svgGenerator.setSVGCanvasSize(size);
          chart.draw(svgGenerator, new Rectangle2D.Double(0, 0, size.width, size.height), null);
          svgGenerator.stream(writer, false);
          break;
        case RESPONSE_PDF:

        case RESPONSE_PS:

        case RESPONSE_JPEG:
        default:
          ChartUtilities.writeChartAsJPEG(out, chart, size.width, size.height);
      }
      out.flush();
      out.close();
    } catch (Exception ex) {
      statusMessage("Error Writing Chart = " + ex.getMessage());
      ex.printStackTrace();
    }
    return info;
  }

  protected JFreeChart createChart(HttpServletRequest request) {
    int initColor = 0;
    int finalColor = 0;
    int width = 700;
    int height = 400;
    boolean flag = false;
    boolean showLegend = true;
    int imageOutputType = RESPONSE_JPEG;
    String chartTitle_ = chartTitle;
    String xTitle_ = xAxisTitle;
    String yTitle_ = yAxisTitle;
    String sql = "";
    String test = "";
    Axis axis;
    int type = 0;
    JFreeChart chart = null;

    try { type = Integer.parseInt(request.getParameter("type")); } catch (Exception e) { }
    try { initColor = Integer.parseInt(request.getParameter("initColor")); } catch (Exception e) { }
    try { finalColor = Integer.parseInt(request.getParameter("finalColor")); } catch (Exception e) { }
    try { chartTitle_ = request.getParameter("title"); } catch (Exception e) { }
    try { xTitle_ = request.getParameter("xaxistitle"); } catch (Exception e) { }
    try { yTitle_ = request.getParameter("yaxistitle"); } catch (Exception e) { }
    try { showLegend = (!(request.getParameter("legend").trim().toLowerCase().equals("false"))); } catch (Exception e) { }
    try { flag = request.getParameter("zero").trim().toLowerCase().equals("true"); } catch (Exception e) { flag = true; }

    sql = generateSQL(request);
    statusMessage(sql);

    if (debug) System.out.println("Creating chart of type " + type);
    if (type < 10)
      chart = createPieChart(type, sql);
    else if (type < 20)
      chart = createCategoryChart(type, sql);
    else
      chart = createXYChart(type, sql);

    if (chart != null) {
      /// Set the background
      chart.setBackgroundPaint(new GradientPaint(0, 0, getColor(initColor),
          0, height, getColor(finalColor)));

      if (showLegend) {
        Legend legend = chart.getLegend();
        legend.setAnchor(legend.EAST);
      } else {
        chart.setLegend(null);
      }

      Plot chartPlot = chart.getPlot();

      /// Customise XY Plots
      if (chartPlot instanceof XYPlot) {
        axis = ((XYPlot)chartPlot).getRangeAxis();
        if (axis != null) {
          if (yTitle_ != null) {
            axis.setLabel(yTitle_);
          }

          if (axis instanceof VerticalNumberAxis) {
            VerticalNumberAxis vnAxis = (VerticalNumberAxis) axis;
            vnAxis.setAutoRangeIncludesZero(flag);
            //vnAxis.autoAdjustRange();
            //vnAxis.configure();
          }
        }

        axis = ((XYPlot)chartPlot).getDomainAxis();
        if (axis != null) {
          //chartHorzAxis.s
          if (xTitle_ != null) {
            axis.setLabel(xTitle_);
          }
        }
        /// Customise Pie Plots
      } else if (chartPlot instanceof PiePlot) {
        PiePlot pie = (PiePlot) chartPlot;
        double x = -1;

        try {
          x = Double.parseDouble(request.getParameter("radiusPercent")) / 100;
          if ((x > 0) && (x <= 1)) {
            pie.setRadiusPercent(x);
          }
        } catch (Exception e) {
        }

        x = -1;
        try {
          type = Integer.parseInt(request.getParameter("explode"));
          if ((type >= 0) && (type < pie.getCategories().size())){
            x = Double.parseDouble(request.getParameter("explodePercent")) / 100;
            pie.setExplodePercent(type, x);
          } else {
            statusMessage("invalid explosion chosen : " + type + ", valid range (0-"
                          +pie.getCategories().size() + ")");
          }
        } catch (Exception e) {
        }

        try {
          flag = request.getParameter("circular").trim().toLowerCase().equals("false");
          pie.setCircular(flag);
        } catch (Exception ex) {
        }

        try {
          type = Integer.parseInt(request.getParameter("labelType"));
          pie.setSectionLabelType(type);
        } catch (Exception ex) {
        }
      }

      if (chartTitle_ != null) {
        ArrayList titles = new ArrayList();
        TextTitle subtitle = new TextTitle(chartTitle_, new Font("SansSerif", Font.BOLD, 12));
        titles.add(subtitle);
        chart.setTitles(titles);
      }
      modifyChart(chart, request);
    }

    return chart;
  }


  /**  Shutdown the servlet */
  public void destroy() {
    // Close the connection and allow it to be garbage collected
    // closeConnection();
  }

  /**  Open the database connection */
  protected void openConnection() {
    Object obj = null;

    try {
      obj = getServletContext().getAttribute(SHARED_DB);
      if (obj != null) {
        con = (Connection) obj;
        /// generally raises an exception when sql server goes down
        con.isClosed();
        statusMessage("Shared connection retrieved ");
      }
    } catch (Exception e) {
      statusMessage("Shared connection failure detected");
      con = null;
    }

    if (con == null) {
      try {
        Class.forName(dbDriver);
      } catch (ClassNotFoundException cnfe) {
        // if this happens, we're history. No database drivers found to load
        statusMessage("Cannot find database JDBC drivers using " + dbDriver + " - check CLASSPATH");
        return;
      }

      // connect to the database
      try {
        con = java.sql.DriverManager.getConnection(dbUrl, dbUser, dbPwd);
      } catch (java.sql.SQLException sqle) {
        statusMessage("Cannot get database connection when instantiating class - " + sqle);
      }

      if (con == null) {
        // still!! Database not available or connection URL wrong
        statusMessage("Cannot connect to database using URL " + dbUrl + " as user " + dbUser);
        return;
      }

      /// Place the opened connection into the servlet context and register
      /// that this servlet opened it.
      getServletContext().setAttribute(SHARED_DB, con);
      getServletContext().setAttribute(SHARED_DB_OPENER, servletName);
      statusMessage("Shared Connection Established");
    }
  }


  /**  Close the database connection */
  private void closeConnection() {
    try {
      if (con != null) {
        con.close();
      }
      statusMessage("Connection Closed");
    } catch (SQLException ex) {
      statusMessage("Error in closing the database connection: ");
    }
    con = null;
  }

  /**
   * Test the database connection is available
   *
   * @return    Status of database connection
   */
  protected boolean testConnection() {
    try {
      con.isClosed();
    } catch (Exception r) {
      // generally raised when sql server goes down
      statusMessage("Connection failure detected - attempting to reconnect");
      con = null;
      getServletContext().setAttribute(SHARED_DB, null);
      openConnection();
    } finally {
    }

    return (con != null);
  }

  /**
   * Write a status message to the server console (stdout) if debug enabled
   *
   * @param  message  the message to be written
   */
  protected void statusMessage(String message) {
    if (debug) {
      System.out.println(servletName + " - " + message);
    }
  }

  /**
   * Write an error page as the reponse.
   *
   * @param response The servlet response
   * @param message  The error message to be displayed
   */
  static public void doErrorPage(HttpServletResponse response, String message) {
    System.out.println("Returning Error Page : " + message);
    try {
      response.setContentType(CONTENT_TYPE[RESPONSE_HTML][1]);
      OutputStreamWriter writer = new OutputStreamWriter(response.getOutputStream());
      writer.write("<H1>ERROR</H1>");
      writer.write("<P>An error has occured processing your image request.</P>");
      writer.write("<P>"+message+"</P>");
      writer.write("<P>More information may be available from the server console.</P>");
      writer.flush();
      writer.close();

    } catch (Exception ex) {
      System.err.println("Error writing error message : " + ex.getMessage());
    }
  }

  /** @todo - What about time outs? */
  /**
   * Include contents of a given url in output
   *
   * @param out  the stream to be written to
   * @param url  the url to be included
   */
  static public void includeUrl(PrintWriter out, String url) {
    if (url == null)
      return;

    String inputLine;
    BufferedReader in = null;

    try {
      URL url_ = new URL(url);
      URLConnection urlc = url_.openConnection();
      in = new BufferedReader(new InputStreamReader( urlc.getInputStream()));

      while ((inputLine = in.readLine()) != null)
        out.println(inputLine);
      in.close();
    } catch (Exception e) {
      System.err.println(" Base Image Servlet : Error including URL : " + url);
      System.err.println(" Base Image Servlet : " + e.getMessage());
    }
  }

  /**
   * write the contents of a file to an output stream.
   *
   * @param out  the stream to be written to
   * @param file the file to be included in the output
   */
  static public void includeFile(OutputStream out, File file) {
    if ((file == null) || (!file.exists()))
      return;

    try {
      FileReader in = new FileReader(file);
      int c;

      while ((c = in.read()) != -1)
        out.write(c);

      in.close();
    } catch (Exception e) {
      System.err.println(" Base Image Servlet : Error including file : " + file );
      System.err.println(" Base Image Servlet : " + e.getMessage());
    }
  }

  /**
   * This function will return true if a given URL exists
   * by using the HTTP status code. This is useful to bypass
   * an error page generated by a Web server when trying to
   * a non-existing page.
   */
  static public boolean existsURL(String URLName){
    try {
      HttpURLConnection.setFollowRedirects(true);
      HttpURLConnection con =
          (HttpURLConnection) new URL(URLName).openConnection();
      con.setRequestMethod("HEAD");
      if (con.getResponseCode() == HttpURLConnection.HTTP_OK)
        return true;
    } catch (Exception e) {
    }
    return false;
  }

  /**
   *  Gets the color attribute of the passed integer
   *
   * @param  color  The colour index
   * @return        The color value
   */
  static public Color getColor(int color) {
    switch (color % 11) {
      case 0:  return Color.white;
      case 1:  return Color.black;
      case 2:  return Color.blue;
      case 3:  return Color.green;
      case 4:  return Color.red;
      case 5:  return Color.yellow;
      case 6:  return Color.gray;
      case 7:  return Color.orange;
      case 8:  return Color.cyan;
      case 9:  return Color.magenta;
      case 10: return Color.pink;
      case 11: return Color.getHSBColor(60.0f, 50.0f, 100.0f);
      default: return Color.white;
    }
  }

  static protected int getImageOutputType(HttpServletRequest request) {
    int type = RESPONSE_JPEG;
    String test = null;
    try {
      test = request.getParameter("imagetype");
      if (test != null) {
        test = test.trim();
        type = Integer.parseInt(test);
      }
    } catch (Exception e) {
      try {
        for (int i = 1; i < CONTENT_TYPE.length; ++i) {
          if (CONTENT_TYPE[i][0].equalsIgnoreCase(test)) {
            type = i;
            i = CONTENT_TYPE.length;
          }
        }
      } catch (Exception ex) {
      }
    }
    return type;
  }

  static protected Dimension getImageSize(HttpServletRequest request) {
    Dimension d = new Dimension(800, 600);
    try {
      d.width = Integer.parseInt(request.getParameter("width"));
    } catch (Exception e) {
    }

    try {
      d.height = Integer.parseInt(request.getParameter("height"));
    } catch (Exception e) {
    }

    /// Some simple sizing checks
    if (d.width > 2000)  { d.width  = 2000; }
    if (d.width < 10)    { d.width  = 10;   }
    if (d.height > 1000) { d.height = 1000; }
    if (d.height < 10)   { d.height = 10;   }

    return d;
  }

}
