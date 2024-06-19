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
 * ---------------------
 * BaseImageServlet.java
 * ---------------------
 * (C) Copyright 2002, by Bryan Scott and Contributors.
 *
 * Original Author:  Bryan Scott;
 * Contributor(s):   -;
 *
 *
 * Changes
 * -------
 * 14-Mar-2002 : Version 1 contributed by Bryan Scott (DG);
 * 05-Apr-2002 : BRS. Downgraded all servlet 2.3 specific calls to allow compilation and running
 *               under servlet 2.2 api
 * 06-Apr-2002 : BRS. Added debug variable, statusMessage procedure and change doPost to output
 *               generated sql if debug set.
 * 29-Apr-2002 : BRS. Changed type values to include pie data sets.
 * 17-May-2002 : BRS. Did a fix for statements remaining open.
 * 17-May-2002 : BRS. Did a fix for non timeseries XY Charts
 * 11-Jun-2002 : Changed createHorizontalStackedBarChart() --> createStackedHorizontalBarChart() for
 *               consistency (DG);
 * 25-Jun-2002 : Updated import statements (DG);
 *
 */

package com.jrefinery.chart.demo.jdbc.servlet;

import com.jrefinery.chart.JFreeChart;
import com.jrefinery.chart.ChartFactory;
import com.jrefinery.chart.ChartUtilities;
import com.jrefinery.chart.Legend;
import com.jrefinery.chart.Axis;
import com.jrefinery.chart.ValueAxis;
import com.jrefinery.chart.VerticalNumberAxis;
import com.jrefinery.chart.Plot;
import com.jrefinery.chart.PiePlot;
import com.jrefinery.chart.XYPlot;
import com.jrefinery.chart.TextTitle;
import com.jrefinery.chart.data.PlotFit;
import com.jrefinery.chart.data.LinearPlotFitAlgorithm;
import com.jrefinery.chart.data.MovingAveragePlotFitAlgorithm;
import com.jrefinery.data.XYDataset;
import com.jrefinery.data.JdbcCategoryDataset;
import com.jrefinery.data.JdbcPieDataset;
import com.jrefinery.data.JdbcXYDataset;
import java.awt.Graphics2D;
import java.awt.Dimension;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Font;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import javax.servlet.*;
import javax.servlet.http.*;

/// SVG Support
import org.apache.batik.dom.GenericDOMImplementation;
import org.apache.batik.svggen.*;
import org.w3c.dom.DOMImplementation;

/**
 * A Base image servlet generator.  Used to provide common base and methods to
 * servlets which need to generate a chart from sql data sources
 *
 * To extend overwrite method : generateSQL.
 *
 * Will attempt to get configuration constants from the firstly the servlet
 * container configuration and then secondly the servlet's initialization
 * parameters.  The servlets initialization parameters taking precedence.
 * Available configuration options are:
 * <table>
 *  <tr><td>DBuser</td> <td>The database user to connect as</td></tr>
 *  <tr><td>DBpwd</td>  <td>The password for the database user</td></tr>
 *  <tr><td>DBschema</td><td>The schema to utilise</td></tr>
 *  <tr><td>DBurl</td>  <td>The connection URL to the database</td></tr>
 *  <tr><td>DBdriver</td><td>The JDBC driver to utilise to connect to the
 *      database</td></tr>
 * </table>
 *
 * The servlet will check to see if a connection is shared amongst the servlets context already. If
 * not it will create a connection and share via the servlet container.
 *
 * Supports the following url options:
 * <table>
 * <tr>
 * <th>NAME</th>
 * <th>TYPE</th>
 * <th>NOTES</th></tr>
 *
 * <tr>
 * <td>type</td>
 * <td>integer</td>
 * <td>the type of chart to generate. eg moving average, linear fit etc
 *      under development.</td>
 * </tr>
 *
 * <tr>
 * <td>width</td>
 * <td>integer</td>
 * <td>The width of output in pixels.  Clipped into the range 10-2000</td>
 * </tr>
 *
 * <tr>
 * <td>height</td>
 * <td>integer</td>
 * <td>The height of the output in pixels. Clipped into the range 10-1000</td>
 * </tr>
 *
 * <tr>
 * <td>initColor</td>
 * <td>integer</td>
 * <td>Between 0-11, used to indicate the initial shading of the background
 *      finalColor  integer  Between 0-11, used to indicate the final shading
 *      of the background</td>
 * </tr>
 *
 * <tr>
 * <td>title</td>
 * <td>String</td>
 * <td>The chart title</td>
 * </tr>
 *
 * <tr>
 * <td>xaxistitle</td>
 * <td>String</td>
 * <td>The x axis title</td>
 * </tr>
 *
 * <tr>
 * <td>yaxistitle</td>
 * <td>String</td>
 * <td>The y axis title</td>
 * </tr>
 *
 * <tr>
 * <td>zero</td>
 * <td>String</td>
 * <td>if passed a value of 'true' then the chart will include zero</td>
 * </tr>
 *
 * <tr>
 * <td>showLegend</td>
 * <td>String</td>
 * <td>if passed a value of 'false' then the chart will not include legend</td>
 * </tr>
 *
 * <tr>
 * <td>output</td>
 * <td>String</td>
 * <td>Selection of the type of output requested.  jpeg, gif, svg</td>
 * </tr>
 *
 * </table>
 *
 * CAUTION : THE FOLLOWING ARE UNTESTED / UNDER DEVELOPMENT
 * <ol>
 *  <li>SVG support using apache batik.</li>
 *  <li>PDF support</li>
 *  <li>Charting options such as moving average, linear fit.</li>
 * </ol>
 *
 * @see              JFreeChart
 * @see              com.jrefinery.chart.demo.JFreeChartServletDemo
 */
public class BaseImageServlet extends HttpServlet implements Constants {

  protected int sqlServerType = ORACLE ;

  /**  The servlets name */
  protected String servletName = "Base Chart ";

  /** Whether or not to enable debug information output **/
  protected boolean debug = false ;

  /**  Title of the chart */
  protected String chartTitle = "Data";

  /**  Title of the x axis */
  protected String xAxisTitle = "";

  /**  Title of the y axis */
  protected String yAxisTitle = "";

  final static char alphaStart = 'b';

  Connection con_;

  /// Some standard defaults but picked up from config file.
  String dbDriver_ = "oracle.jdbc.driver.OracleDriver";
  String dbUrl_ = "jdbc:oracle:thin:@yourdb:1521:prod";
  String dbUser_ = "your_user";
  String dbPwd_ = "your_password";
  String dbSchema_ = null;

  /**
   *  Gets the color attribute of the passed integer
   *
   * @param color   The colour index
   * @return        The color value
   */
  protected Color getColor(int color) {
	switch (color % 11) {
	  case 0:
		return Color.white;
	  case 1:
		return Color.black;
	  case 2:
		return Color.blue;
	  case 3:
		return Color.green;
	  case 4:
		return Color.red;
	  case 5:
		return Color.yellow;
	  case 6:
		return Color.gray;
	  case 7:
		return Color.orange;
	  case 8:
		return Color.cyan;
	  case 9:
		return Color.magenta;
	  case 10:
		return Color.pink;
	  case 11:
		return Color.getHSBColor(60.0f, 50.0f, 100.0f);
	  default:
		return Color.white;
	}
  }

  protected JFreeChart createChart(int type, String sql) {
	JFreeChart chart = null ;

	if (debug) System.out.println("Creating chart of type " + type);

	if (!testConnection()) {
	  return null;
	}

	if (type < 10)
		chart = createPieChart(type, sql) ;
	else if (type < 20)
		chart = createCategoryChart(type, sql);
	else
		chart = createXYChart(type, sql) ;
	return chart ;
  }

  /**
   * Create a chart
   *
   * @param  type  Type of chart to create.  Currently not supported.
   * @param  sql   SQL to execute.  1st column is x values, following columns
   *        are y values
   * @return       The chart of the data
   */
  protected JFreeChart createXYChart(int type, String sql) {
	JFreeChart chart;
	JdbcXYDataset chartData ;
	XYDataset xyData ;

	chartData = new JdbcXYDataset(con_, sql);
	try {
	  switch (type) {
		case 21:
		  // moving avg
		  MovingAveragePlotFitAlgorithm mavg =
			new MovingAveragePlotFitAlgorithm();
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
	  VerticalNumberAxis vnAxis = (VerticalNumberAxis)
		chart.getXYPlot().getRangeAxis();
	  vnAxis.setAutoRangeIncludesZero(false);
	  vnAxis.setCrosshairVisible(false);
	  vnAxis.configure();

	  /// Customise the horizontal axis
	  ValueAxis axis = (ValueAxis) chart.getXYPlot().getDomainAxis();
	  axis.setCrosshairVisible(false);
	  //hzAxis.configure();

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
	JdbcPieDataset chartData ;

	try {
	  chartData = new JdbcPieDataset(con_);
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
	JdbcCategoryDataset chartData ;

	//if (debug) System.out.println("Creating a category chart");
	try {
	  chartData = new JdbcCategoryDataset(con_);
	  chartData.executeQuery(sql);
	  //if (debug) System.out.println("  --> Factory");
	  switch (type) {
		case 11:
		  chart = ChartFactory.createVerticalBarChart3D("","","",chartData, true);
		  break ;
		case 12:
		  chart = ChartFactory.createStackedVerticalBarChart("","","",chartData, true);
		  break ;
		case 13:
		  chart = ChartFactory.createStackedVerticalBarChart3D("","","",chartData, true);
		  break ;
		case 14:
		  chart = ChartFactory.createHorizontalBarChart("","","",chartData, true);
		  break ;
		case 15:
		  chart = ChartFactory.createStackedHorizontalBarChart("","","",chartData, true);
		  break ;
		default:
		  chart = ChartFactory.createVerticalBarChart("","","",chartData, true);
		  break ;
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

	/// Load parameters, Try initially using context setting and then specific
	/// to this servlet.
	try {
	  sqlServerType = Integer.parseInt(getServletContext().getInitParameter(DB_SERVER));
	} catch (Exception e) {
	  e.printStackTrace();
	}

	try {
	  dbUser_ = getServletContext().getInitParameter(DB_USER);
	} catch (Exception e) {
	  e.printStackTrace();
	}

	try {
	  dbPwd_ = getServletContext().getInitParameter(DB_PASSWORD);
	} catch (Exception e) {
	  e.printStackTrace();
	}
	try {
	  dbUrl_ = getServletContext().getInitParameter(DB_URL);
	} catch (Exception e) {
	  e.printStackTrace();
	}
	try {
	  dbDriver_ = getServletContext().getInitParameter(DB_DRIVER);
	} catch (Exception e) {
	  e.printStackTrace();
	}
	try {
	  dbSchema_ = getServletContext().getInitParameter(DB_SCHEMA);
	} catch (Exception e) {
	  e.printStackTrace();
	}
	try {
	  test = getServletContext().getInitParameter(DEBUG) ;
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
	  dbUser_ = test;
	}
	try {
	  test = getInitParameter(DB_PASSWORD);
	} catch (Exception e) {
	  e.printStackTrace();
	}
	if ((test != null) && (test.length() > 0)) {
	  dbPwd_ = test;
	}
	try {
	  test = getInitParameter(DB_URL);
	} catch (Exception e) {
	  e.printStackTrace();
	}
	if ((test != null) && (test.length() > 0)) {
	  dbUrl_ = test;
	}
	try {
	  test = getInitParameter(DB_DRIVER);
	} catch (Exception e) {
	  e.printStackTrace();
	}
	if ((test != null) && (test.length() > 0)) {
	  dbDriver_ = test;
	}
	try {
	  test = getInitParameter(DB_SCHEMA);
	} catch (Exception e) {
	  e.printStackTrace();
	}
	if ((test != null) && (test.length() > 0)) {
	  dbSchema_ = test;
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

	if ((dbSchema_ != null) && (dbSchema_.length() > 0)) {
	  if (!dbSchema_.endsWith(".")) {
		dbSchema_ = dbSchema_ + ".";
	  }
	} else {
	  dbSchema_ = " ";
	}

	// Confirmation to screen.
	statusMessage("Debug    : " + debug);
	statusMessage("Driver   : " + dbDriver_);
	statusMessage("Database : " + dbUrl_);
	statusMessage("Schema   : " + dbSchema_);
	statusMessage("User     : " + dbUser_);
	statusMessage("Password : #######");

	openConnection();
  }

  /**
   * Generate the SQL required from the http request
   *
   * @param  request  The servlets request parameters
   * @return          The generated sql string
   */
  protected String generateSQL(HttpServletRequest request) {
	return "select 1, 10 from dual";
  }

  /**
   * Basic servlet method, answers requests from the browser.
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
   *  Process the HTTP Post request
   *
   * @param  request               Description of the Parameter
   * @param  response              Description of the Parameter
   * @exception  ServletException  Description of the Exception
   * @exception  IOException       Description of the Exception
   */
  public void doPost(HttpServletRequest request, HttpServletResponse response)
	throws ServletException, IOException {

	int initColor = 0;
	int finalColor = 0;
	int type = 0;
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
	Axis axis ;

	try {
	  type = Integer.parseInt(request.getParameter("type"));
	} catch (Exception e) {
	}

	try {
	  width = Integer.parseInt(request.getParameter("width"));
	} catch (Exception e) {
	}

	try {
	  height = Integer.parseInt(request.getParameter("height"));
	} catch (Exception e) {
	}

	try {
	  initColor = Integer.parseInt(request.getParameter("initColor"));
	} catch (Exception e) {
	}
	try {
	  finalColor = Integer.parseInt(request.getParameter("finalColor"));
	} catch (Exception e) {
	}
	try {
	  chartTitle_ = request.getParameter("title");
	} catch (Exception e) {
	}
	try {
	  xTitle_ = request.getParameter("xaxistitle");
	} catch (Exception e) {
	}
	try {
	  yTitle_ = request.getParameter("yaxistitle");
	} catch (Exception e) {
	}
	try {
	  flag = request.getParameter("zero").trim().toLowerCase().equals("true");
	} catch (Exception e) {
	  flag = true ;
	}

	try {
	  showLegend = (!(request.getParameter("legend").trim().toLowerCase().equals("false")));
	} catch (Exception e) {
	}

	try {
	  test = request.getParameter("output");
	  if (test != null) {
		test = test.trim();
		imageOutputType = Integer.parseInt(test);
	  }
	} catch (Exception e) {
	  try {
		for (int i = 1; i < CONTENT_TYPE.length; ++i) {
		  if (CONTENT_TYPE[i][0].equalsIgnoreCase(test)) {
			imageOutputType = i;
			i = CONTENT_TYPE.length;
		  }
		}
	  } catch (Exception ex) {
		type = RESPONSE_JPEG;
	  }
	}

	/// Some simple sizing checks
	if (width > 2000) {
	  width = 2000;
	}
	if (width < 10) {
	  width = 10;
	}
	if (height > 1000) {
	  height = 1000;
	}
	if (height < 10) {
	  height = 10;
	}

	sql = generateSQL(request);
	statusMessage(sql);
	///
	JFreeChart chart = createChart(type, sql);

	/// Deal with the null chart (error)
	if (chart == null) {
	  writeErrorPage(response, "No Chart returned. \nSQL : " + sql) ;
	  return ;
	}

	/// If we get to here, the chart is not null

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

	axis = null;
	if (chartPlot instanceof XYPlot) {
		axis = ((XYPlot)chartPlot).getRangeAxis();
	}

	if (axis != null) {
	  if (yTitle_ != null) {
		axis.setLabel(yTitle_);
	  }

	  if (axis instanceof VerticalNumberAxis) {
		VerticalNumberAxis vnAxis = (VerticalNumberAxis) axis ;
		vnAxis.setAutoRangeIncludesZero(flag);
		//vnAxis.autoAdjustRange();
		//vnAxis.configure();
	  }
	}

	if (chartPlot instanceof XYPlot) {
		axis = ((XYPlot)chartPlot).getDomainAxis();
	}
	if (axis != null) {
	  //chartHorzAxis.s
	  if (xTitle_ != null) {
		axis.setLabel(xTitle_);
	  }
	}

	if (chartPlot instanceof PiePlot) {
	  PiePlot pie = (PiePlot) chartPlot ;
	  double x = -1 ;

	  try {
		x = Double.parseDouble(request.getParameter("radiusPercent")) / 100 ;
		if ((x > 0) && (x <= 1)) {
		  pie.setRadiusPercent(x);
		}
	  } catch (Exception e) {
	  }


	  x = -1 ;
	  try {
	   type = Integer.parseInt(request.getParameter("explode"));
		if ((type >= 0) && (type < pie.getCategories().size())){
		  x = Double.parseDouble(request.getParameter("explodePercent")) / 100 ;
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

	//System.out.println("Auto Range Minimum = " + vnAxis.getAutoRangeMinimum());
	//System.out.println("Range Minimum = " + vnAxis.getMinimumAxisValue());
	//System.out.println("Range Maximum = " + vnAxis.getMaximumAxisValue());

	if (chartTitle_ != null) {
	  ArrayList titles = new ArrayList();
	  TextTitle subtitle = new TextTitle(chartTitle_, new Font("SansSerif", Font.BOLD, 12));
	  titles.add(subtitle);
	  chart.setTitles(titles);
	}


	modifyChart(chart, request);

	OutputStream out = response.getOutputStream();
	try {
	  response.setContentType(CONTENT_TYPE[imageOutputType][1]);
	  switch (imageOutputType) {
		case RESPONSE_PNG:
		  ChartUtilities.writeChartAsPNG(out, chart, width, height);
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
		  svgGenerator.setSVGCanvasSize(new Dimension(width,height));
		  chart.draw(svgGenerator, new Rectangle2D.Double(0, 0, width, height), null);
		  svgGenerator.stream(writer, false);
		  break;
		case RESPONSE_PDF:

		case RESPONSE_PS:

		case RESPONSE_JPEG:
		default:
		  ChartUtilities.writeChartAsJPEG(out, chart, width, height);
	  }
	} catch (Exception ex) {
	  statusMessage("Error SQL = " + sql);
	  ex.printStackTrace();
	} finally {
	  out.flush();
	  out.close();
	}
	chart = null;
  }

  /*
   renders an FO inputsource into a PDF file which is rendered
   directly to the response object's OutputStream
   public void renderFO(InputSource foFile,
	HttpServletResponse response) throws ServletException {
	try {
	  ByteArrayOutputStream out = new ByteArrayOutputStream();
	  response.setContentType("application/pdf");
	  Driver driver = new Driver(foFile, out);
	  driver.setLogger(log);
	  driver.setRenderer(Driver.RENDER_PDF);
	  driver.run();
	  byte[] content = out.toByteArray();
	  response.setContentLength(content.length);
	  response.getOutputStream().write(content);
	  response.getOutputStream().flush();
	} catch (Exception ex) {
	  throw new ServletException(ex);
	}
   }
  */

  /**
   *
   *
   *
   * @param  chart   Description of the Parameter
   * @param  width   Description of the Parameter
   * @param  height  Description of the Parameter
   * @return         Description of the Return Value
   */

  protected BufferedImage draw(JFreeChart chart, int width, int height) {
	BufferedImage img = new BufferedImage(width, height,
		BufferedImage.TYPE_INT_RGB);
	Graphics2D g2 = img.createGraphics();
	chart.draw(g2, new Rectangle2D.Double(0, 0, width, height), null);
	g2.dispose();
	return img;
  }

  /**
   * Override this method if you would like to modify the generated chart
   * parameters / options
   *
   * @param  chart  The generated chart.
   * @param request     The HTTP request.
   */
  public void modifyChart(JFreeChart chart, HttpServletRequest request) {
  }

  /**  Description of the Method */
  public void destroy() {
	// Close the connection and allow it to be garbage collected
	// closeConnection();
  }

  /**  Description of the Method */
  protected void openConnection() {
	Object obj = null;

	try {
	  obj = getServletContext().getAttribute(SHARED_DB);
	  if (obj != null) {
		con_ = (Connection) obj;
		/// generally raises an exception when sql server goes down
		con_.isClosed();
		statusMessage("Shared connection retrieved ");
	  }
	} catch (Exception e) {
	  statusMessage("Shared connection failure detected");
	  con_ = null;
	}

	if (con_ == null) {
	  try {
		Class.forName(dbDriver_);
	  } catch (ClassNotFoundException cnfe) {
		// if this happens, we're history. No database drivers found to load
		statusMessage("Cannot find database JDBC drivers using " + dbDriver_ + " - check CLASSPATH");
		return;
	  }

	  // connect to the database
	  try {
		con_ = java.sql.DriverManager.getConnection(dbUrl_, dbUser_, dbPwd_);
	  } catch (java.sql.SQLException sqle) {
		statusMessage("Cannot get database connection when instantiating class - " + sqle);
	  }

	  if (con_ == null) {
		// still!! Database not available or connection URL wrong
		statusMessage("Cannot connect to database using URL " + dbUrl_ + " as user " + dbUser_);
		return;
	  }

	  /// Place the opened connection into the servlet context and register
	  /// that this servlet opened it.
	  getServletContext().setAttribute(SHARED_DB, con_);
	  getServletContext().setAttribute(SHARED_DB_OPENER, servletName);
	  statusMessage("Shared Connection Established");
	}
  }


  /**  Description of the Method */
  private void closeConnection() {
	try {
	  if (con_ != null) {
		con_.close();
	  }
	  statusMessage("Connection Closed");
	} catch (SQLException ex) {
	  statusMessage("Error in closing the database connection: ");
	}
	con_ = null;
  }

  /**
   *  Description of the Method
   *
   * @return    Description of the Return Value
   */
  protected boolean testConnection() {
	try {
	  con_.isClosed();
	} catch (Exception r) {
	  // generally raised when sql server goes down
	  statusMessage("Connection failure detected - attempting to reconnect");
	  con_ = null;
	  getServletContext().setAttribute(SHARED_DB, null);
	  openConnection();
	} finally {
	}

	return (con_ != null);
  }

  /**
   *  Description of the Method
   *
   * @param  message  Description of the Parameter
   */
  private void statusMessage(String message) {
	if (debug) {
	  System.out.println(servletName + " - " + message);
	}
  }

  protected void writeErrorPage(HttpServletResponse response, String message) {
	System.out.println("Returning Error Page");
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
}
