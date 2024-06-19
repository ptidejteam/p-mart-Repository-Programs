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
 * ---------------------
 * ServletUtilities.java
 * ---------------------
 * (C) Copyright 2002, 2003, by Richard Atkinson and Contributors.
 *
 * Original Author:  Richard Atkinson (richard_c_atkinson@ntlworld.com);
 * Contributor(s):   -;
 *
 * $Id: ServletUtilities.java,v 1.1 2007/10/10 19:54:31 vauchers Exp $
 *
 * Changes
 * -------
 * 19-Aug-2002 : Version 1;
 *
 */
package com.jrefinery.chart.servlet;

import java.io.File;
import java.io.FileInputStream;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.FileNotFoundException;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpServletResponse;
import com.jrefinery.chart.JFreeChart;
import com.jrefinery.chart.ChartUtilities;
import com.jrefinery.chart.ChartRenderingInfo;

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

        File tempFile = File.createTempFile("jfreechart-", ".png");
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

        File tempFile = File.createTempFile("jfreechart-", ".jpeg");
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

        if (file.exists()) {
            BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
            BufferedOutputStream bos = new BufferedOutputStream(response.getOutputStream());
            byte[] input = new byte[1024];
            boolean eof = false;
            while (!eof) {
                int length = bis.read(input);
                if (length == -1) {
                    eof = true;
                }
                else {
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
        String r = "";
        if (i == -1) {
            return inputString;
        }

        r += inputString.substring(0, i) + replaceString;
        if (i + searchString.length() < inputString.length()) {
            r += searchReplace(inputString.substring(i + searchString.length()),
                               searchString,
                               replaceString);
        }

        return r;
    }

}
