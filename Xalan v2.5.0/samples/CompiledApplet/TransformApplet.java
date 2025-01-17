/*
 * @(#)$Id: TransformApplet.java,v 1.1 2006/03/01 21:12:32 vauchers Exp $
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2001-2003 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Xalan" and "Apache Software Foundation" must
 *    not be used to endorse or promote products derived from this
 *    software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    nor may "Apache" appear in their name, without prior written
 *    permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation and was
 * originally based on software copyright (c) 2001, Sun
 * Microsystems., http://www.sun.com.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 *
 * @author Morten Jorgensen
 * @author Jacek Ambroziak
 *
 */

import java.applet.Applet;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Frame;
import java.awt.Label;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.io.PrintWriter;
import java.io.StringWriter;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerException;
import javax.xml.transform.ErrorListener;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

/**
 * This applet demonstrates how XSL transformations can be made to run in
 * browsers without native XSLT support.
 *
 * Note that the XSLTC transformation engine is invoked through the JAXP
 * interface, using the XSLTC "use-classpath" attribute.  The
 * "use-classpath" attribute specifies to the XSLTC TransformerFactory
 * that a precompiled version of the stylesheet (translet) may be available,
 * and that that should be used in preference to recompiling the stylesheet.
 */
public final class TransformApplet extends Applet {
    TransformerFactory tf;
    TransformDelegate transformThread;
    /**
     * This class implements a dialog box used for XSL messages/comments
     */
    public class MessageFrame extends Frame {

        public Frame frame;

        public class ButtonHandler implements ActionListener {
            public void actionPerformed(ActionEvent e) {
                frame.setVisible(false);
            }
        }

        /**
         * This method handles xml:message and xsl:comment by displaying
         * the message/comment in a dialog box.
         */
        public MessageFrame(String title, String message) {
            super(title);
            frame = this; // Make visible to ButtonHandler
            setSize(320,200);

            // Create a panel for the message itself
            Panel center = new Panel();
            center.add(new Label(message));

            // Create a panel for the 'OK' button
            Panel bottom = new Panel();
            Button okButton = new Button("   OK   ");
            okButton.addActionListener(new ButtonHandler());
            bottom.add(okButton);

            // Add the two panels to the window/frame
            add(center, BorderLayout.CENTER);
            add(bottom,BorderLayout.SOUTH);

            // Show the fecking thing
            setVisible(true);
        }

    }

    /**
     * The applet uses this method to display messages and comments
     * generated by xsl:message and xsl:comment elements.
     */
    public class AppletErrorListener implements ErrorListener {
        public void displayMessage(TransformerException e) {
            MessageFrame z = new MessageFrame("XSL transformation alert",
                                              e.getMessageAndLocation());
        }

        public void error(TransformerException e) {
            displayMessage(e);
        }

        public void fatalError(TransformerException e) {
            displayMessage(e);
        }

        public void warning(TransformerException e) {
                    displayMessage(e);
        }
    }

    /**
     * This method is the main body of the applet. The method is called
     * by some JavaScript code in an HTML document.
     */
    public synchronized String transform(Object arg1, Object arg2) {
        final String stylesheetURL = (String)arg1;
        final String documentURL = (String)arg2;

        transformThread.setStylesheetURL(stylesheetURL);
        transformThread.setDocumentURL(documentURL);
        transformThread.setWaiting(false);
        transformThread.wakeUp();
        try{
            wait();
        } catch (InterruptedException e){}
        return transformThread.getOutput();
    }

    public void start() {
        transform(getParameter("stylesheet-name"),
                  getParameter("input-document"));
    }
    public void destroy() {
        transformThread.destroy();
    }
    public void init() {
        tf = TransformerFactory.newInstance();
        try {
            tf.setAttribute("use-classpath", Boolean.TRUE);
        } catch (IllegalArgumentException iae) {
            System.err.println("Could not set XSLTC-specific TransformerFactory"
                               + " attributes.  Transformation failed.");
        }
        // Another thread is created to keep the context class loader
        // information.  When use JDK 1.4 plugin for browser, to get around the
        // problem with the bundled old version of xalan and endorsed class
        // loading mechanism
        transformThread = new TransformDelegate(true);
        Thread t = new Thread(transformThread);
        t.setName("transformThread");
        t.start();
    }
    public String getOutput(){
        return transformThread.getOutput();
    }
    public synchronized void wakeUp() {
        notifyAll();
    }
    class TransformDelegate implements Runnable {
        private boolean isRunning, isWaiting;
        private String stylesheetURL, documentURL;
        private String outPut;
        public TransformDelegate(boolean arg) {
            isRunning = arg;
            isWaiting = true;
        }
        public synchronized void run() {
            while(isRunning){
                while(isWaiting){
                    try {
                        wait();
                    } catch (InterruptedException e){}
                }
                transform();
                isWaiting = true;
                TransformApplet.this.wakeUp();
            }
        }

        public void setStylesheetURL(String arg){
            stylesheetURL = arg;
        }
        public void setDocumentURL(String arg) {
            documentURL = arg;
        }
        public String getStylesheetURL(){
            return stylesheetURL;
        }
        public String getDocumentURL() {
            return documentURL;
        }
        public void setWaiting(boolean arg) {
            isWaiting = arg;
        }
        public void destroy() {
            isRunning = false;
        }
        public synchronized void wakeUp() {
            notifyAll();
        }
        public String getOutput(){
            return outPut;
        }

        public void transform(){
            String xslURL = getStylesheetURL();
            String docURL = getDocumentURL();
            // Initialise the output stream
            StringWriter sout = new StringWriter();
            PrintWriter out = new PrintWriter(sout);
            // Check that the parameters are valid
            try {
                if (xslURL == null || docURL == null) {
                    out.println("<h1>Transformation error</h1>");
                    out.println("The parameters <b><tt>stylesheetURL</tt></b> "+
                                "and <b><tt>source</tt></b> must be specified");
                } else {
                    Transformer t = tf.newTransformer(new StreamSource(xslURL));
                    t.setErrorListener(new AppletErrorListener());

                    final long start = System.currentTimeMillis();

                    t.transform(new StreamSource(docURL),
                                new StreamResult(out));

                    final long done = System.currentTimeMillis() - start;
                    out.println("<!-- transformed by XSLTC in " + done
                                + "msecs -->");
                }
                // Now close up the sink, and return the HTML output in the
                // StringWrite object as a string.
                out.close();
                System.err.println("Transformation complete!");
                System.err.println(sout.toString());
                outPut = sout.toString();
                sout.close();
            } catch (RuntimeException e) {
                out.println("<h1>RTE</h1>");
                out.close();
                outPut = sout.toString();
            } catch (Exception e) {
                out.println("<h1>exception</h1>");
                out.println(e.toString());
                out.close();
                outPut = sout.toString();
            }
        }
    }
}
