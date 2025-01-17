/* ======================================
 * JFreeChart : a free Java chart library
 * ======================================
 *
 * Project Info:  http://www.object-refinery.com/jfreechart/index.html
 * Project Lead:  David Gilbert (david.gilbert@object-refinery.com);
 *
 * (C) Copyright 2000-2003, by Object Refinery Limited and Contributors.
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
 * ServletUtilities.java
 * ---------------------
 * (C) Copyright 2002, 2003, by Richard Atkinson and Contributors.
 *
 * Original Author:  Richard Atkinson (richard_c_atkinson@ntlworld.com);
 * Contributor(s):   -;
 *
 * $Id: ServletUtilities.java,v 1.1 2007/10/10 19:22:02 vauchers Exp $
 *
 * Changes
 * -------
 * 19-Aug-2002 : Version 1;
 * 20-Apr-2003 : Added additional sendTempFile method to allow MIME type specification and
 * modified original sendTempFile method to automatically set MIME type for JPEG and PNG files
 * 23-Jun-2003 : Added additional sendTempFile method at the request of J�rgen Hoffman;
 * 07-Jul-2003 : Added more header information to streamed images;
 * 19-Aug-2003 : Forced images to be stored in the temporary directory defined by System
 * property java.io.tmpdir, rather than default (RA);
 *
 */
package org.jfree.chart.servlet;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.jfree.chart.ChartRenderingInfo;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;

/**
 * Utility class used for servlet related JFreeChart operations.
 *
 * @author Richard Atkinson
 */
public class ServletUtilities {

    /**
     * Saves the chart as a PNG format file in the temporary directory.
     *
     * @param chart  the JFreeChart to be saved.
     * @param width  the width of the chart.
     * @param height  the height of the chart.
     * @param session  the HttpSession of the client.
     *
     * @return the filename of the chart saved in the temporary directory.
     *
     * @throws IOException if there is a problem saving the file.
     */
    public static String saveChartAsPNG(JFreeChart chart, int width, int height,
                                        HttpSession session) throws IOException {

        return ServletUtilities.saveChartAsPNG(chart, width, height, null, session);
    }

    /**
     * Saves the chart as a PNG format file in the temporary directory and
     * populates the ChartRenderingInfo object which can be used to generate
     * an HTML image map.
     *
     * @param chart  the JFreeChart to be saved.
     * @param width  the width of the chart.
     * @param height  the height of the chart.
     * @param info  the ChartRenderingInfo object to be populated.
     * @param session  the HttpSession of the client.
     *
     * @return the filename of the chart saved in the temporary directory.
     *
     * @throws IOException if there is a problem saving the file.
     */
    public static String saveChartAsPNG(JFreeChart chart, int width, int height,
                                        ChartRenderingInfo info, HttpSession session)
            throws IOException {

        ServletUtilities.createTempDir();

        File tempFile = File.createTempFile("jfreechart-", ".png", 
                                            new File(System.getProperty("java.io.tmpdir")));
        ChartUtilities.saveChartAsPNG(tempFile, chart, width, height, info);

        ServletUtilities.registerChartForDeletion(tempFile, session);

        return tempFile.getName();

    }

    /**
     * Saves the chart as a JPEG format file in the temporary directory.
     *
     * @param chart  the JFreeChart to be saved.
     * @param width  the width of the chart.
     * @param height  the height of the chart.
     * @param session  the HttpSession of the client.
     *
     * @return the filename of the chart saved in the temporary directory.
     *
     * @throws IOException if there is a problem saving the file.
     */
    public static String saveChartAsJPEG(JFreeChart chart, int width, int height,
                                HttpSession session) throws IOException {

        return ServletUtilities.saveChartAsJPEG(chart, width, height, null, session);
    }

    /**
     * Saves the chart as a JPEG format file in the temporary directory and
     * populates the ChartRenderingInfo object which can be used to generate
     * an HTML image map.
     *
     * @param chart  the JFreeChart to be saved
     * @param width  the width of the chart
     * @param height  the height of the chart
     * @param info  the ChartRenderingInfo object to be populated
     * @param session  the HttpSession of the client
     *
     * @return the filename of the chart saved in the temporary directory
     *
     * @throws IOException if there is a problem saving the file.
     */
    public static String saveChartAsJPEG(JFreeChart chart, int width, int height,
                                 ChartRenderingInfo info, HttpSession session)
            throws IOException {

        ServletUtilities.createTempDir();

        File tempFile = File.createTempFile("jfreechart-", ".jpeg",
                                            new File(System.getProperty("java.io.tmpdir")));
        ChartUtilities.saveChartAsJPEG(tempFile, chart, width, height, info);

        ServletUtilities.registerChartForDeletion(tempFile, session);

        return tempFile.getName();

    }

    /**
     * Creates the temporary directory if it does not exist.
     * Throws a RuntimeException if the temporary directory is null.
     * Uses the system property java.io.tmpdir as the temporary directory.
     * Sounds like a strange thing to do but my temporary directory was not created
     * on my default Tomcat 4.0.3 installation.  Could save some questions on the
     * forum if it is created when not present.
     */
    protected static void createTempDir() {
        String tempDirName = System.getProperty("java.io.tmpdir");
        if (tempDirName == null) {
            throw new RuntimeException(
                "Temporary directory system property (java.io.tmpdir) is null");
        }

        //  Create the temporary directory if it doesn't exist
        File tempDir = new File(tempDirName);
        if (!tempDir.exists()) {
            tempDir.mkdirs();
        }
    }

    /**
     * Adds a ChartDeleter object to the session object with the name JFreeChart_Deleter
     * if there is not already one bound to the session and adds the filename to the
     * list of charts to be deleted.
     *
     * @param tempFile  the file to be deleted.
     * @param session  the HTTP session of the client.
     */
    protected static void registerChartForDeletion(File tempFile, HttpSession session) {

        //  Add chart to deletion list in session
        if (session != null) {
            ChartDeleter chartDeleter = (ChartDeleter) session.getAttribute("JFreeChart_Deleter");
            if (chartDeleter == null) {
                chartDeleter = new ChartDeleter();
                session.setAttribute("JFreeChart_Deleter", chartDeleter);
            }
            chartDeleter.addChart(tempFile.getName());
        }
        else {
            System.out.println("Session is null - chart will not be deleted");
        }
    }

    /**
     * Binary streams the specified file in the temporary directory to the
     * HTTP response in 1KB chunks
     * @param filename The name of the file in the temporary directory.
     * @param response The HTTP response object.
     * @throws IOException  if there is an I/O problem.
     * @throws FileNotFoundException  if the file is not found.
     */
    public static void sendTempFile(String filename, HttpServletResponse response)
        throws IOException, FileNotFoundException {

        File file = new File(System.getProperty("java.io.tmpdir"), filename);
        ServletUtilities.sendTempFile(file, response);
    }

    /**
     * Binary streams the specified file to the HTTP response in 1KB chunks
     *
     * @param file The file to be streamed.
     * @param response The HTTP response object.
     *
     * @throws IOException  if there is an I/O problem.
     * @throws FileNotFoundException  if the file is not found.
     */
    public static void sendTempFile(File file, HttpServletResponse response)
            throws IOException, FileNotFoundException {

        String mimeType = null;
        String filename = file.getName();
        if (filename.length() > 5) {
            if (filename.substring(filename.length() - 5, filename.length()).equals(".jpeg")) {
                mimeType = "image/jpeg";
            } 
            else if (filename.substring(filename.length() - 4, filename.length()).equals(".png")) {
                mimeType = "image/png";
            }
        }
        ServletUtilities.sendTempFile(file, response, mimeType);
    }

    /**
     * Binary streams the specified file to the HTTP response in 1KB chunks
     *
     * @param file The file to be streamed.
     * @param response The HTTP response object.
     * @param mimeType The mime type of the file, null allowed.
     *
     * @throws IOException  if there is an I/O problem.
     * @throws FileNotFoundException  if the file is not found.
     */
    public static void sendTempFile(File file, HttpServletResponse response,
                                    String mimeType)
            throws IOException, FileNotFoundException {

        if (file.exists()) {
            BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));

            //  Set HTTP headers
            if (mimeType != null) {
                response.setHeader("Content-Type", mimeType);
            }
            response.setHeader("Content-Length", String.valueOf(file.length()));
            SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z");
            response.setHeader("Last-Modified", sdf.format(new Date(file.lastModified())));

            BufferedOutputStream bos = new BufferedOutputStream(response.getOutputStream());
            byte[] input = new byte[1024];
            boolean eof = false;
            while (!eof) {
                int length = bis.read(input);
                if (length == -1) {
                    eof = true;
                } else {
                    bos.write(input, 0, length);
                }
            }
            bos.flush();
            bis.close();
            bos.close();
        }
        else {
            throw new FileNotFoundException(file.getAbsolutePath());
        }
        return;
    }

    /**
     * Perform a search/replace operation on a String
     * There are String methods to do this since (JDK 1.4)
     *
     * @param inputString  the String to have the search/replace operation.
     * @param searchString  the search String.
     * @param replaceString  the replace String.
     *
     * @return the String with the replacements made.
     */
    public static String searchReplace(String inputString,
                                       String searchString,
                                       String replaceString) {

        int i = inputString.indexOf(searchString);
        if (i == -1) {
            return inputString;
        }

        String r = "";
        r += inputString.substring(0, i) + replaceString;
        if (i + searchString.length() < inputString.length()) {
            r += searchReplace(inputString.substring(i + searchString.length()),
                               searchString,
                               replaceString);
        }

        return r;
    }

}
