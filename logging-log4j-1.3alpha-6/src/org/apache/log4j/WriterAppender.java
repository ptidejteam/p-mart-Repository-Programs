/*
 * Copyright 1999,2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.log4j;

import org.apache.log4j.spi.LoggingEvent;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;


// Contibutors: Jens Uwe Pipka <jens.pipka@gmx.de>
//              Ben Sandee

/**
   WriterAppender appends log events to a {@link java.io.Writer} or an
   {@link java.io.OutputStream} depending on the user's choice.

   @author Ceki G&uuml;lc&uuml;
   @since 1.1 */
public class WriterAppender extends AppenderSkeleton {
  
  /**
   * Set to true when the appender is in functioning order.
   */
  protected boolean inOrder = false;
  
  /**
     Immediate flush means that the underlying writer or output stream
     will be flushed at the end of each append operation. Immediate
     flush is slower but ensures that each append request is actually
     written. If <code>immediateFlush</code> is set to
     <code>false</code>, then there is a good chance that the last few
     logs events are not actually written to persistent media if and
     when the application crashes.

     <p>The <code>immediateFlush</code> variable is set to
     <code>true</code> by default.

  */
  protected boolean immediateFlush = true;

  /**
     The encoding to use when opening an InputStream.  <p>The
     <code>encoding</code> variable is set to <code>null</null> by
     default which results in the utilization of the system's default
     encoding.  */
  protected String encoding;

  /**
   * This is the {@link Writer Writer} where we will write to.
  */
  protected Writer writer;

  /**
     This default constructor does nothing.  */
  public WriterAppender() {
  }

  /**
   * Instantiate a WriterAppender and set the output destination to a new 
   * {@link OutputStreamWriter} initialized with <code>os</code> as its 
   * {@link OutputStream}.  
   * 
   * @deprecated This constructor will be removed with no replacement in 
   * future log4j versions.
   * */
  public WriterAppender(Layout layout, OutputStream os) {
    this(layout, new OutputStreamWriter(os));
  }

  /**
   * Instantiate a WriterAppender and set the output destination to 
   * <code>writer</code>.
   * <p>
   * The <code>writer</code> must have been previously opened by the user. 
   * 
   * @deprecated This constructor will be removed with no replacement in 
   * future log4j versions 
   * */
  public WriterAppender(Layout layout, Writer writer) {
    this.layout = layout;
    this.setWriter(writer);
  }

  /**
     If the <b>ImmediateFlush</b> option is set to
     <code>true</code>, the appender will flush at the end of each
     write. This is the default behavior. If the option is set to
     <code>false</code>, then the underlying stream can defer writing
     to physical medium to a later time.

     <p>Avoiding the flush operation at the end of each append results in
     a performance gain of 10 to 20 percent. However, there is safety
     tradeoff involved in skipping flushing. Indeed, when flushing is
     skipped, then it is likely that the last few log events will not
     be recorded on disk when the application exits. This is a high
     price to pay even for a 20% performance gain.
   */
  public void setImmediateFlush(boolean value) {
    immediateFlush = value;
  }

  /**
     Returns value of the <b>ImmediateFlush</b> option.
   */
  public boolean getImmediateFlush() {
    return immediateFlush;
  }

  /**
   * Does nothing.
  */
  public void activateOptions() {
    if (this.layout == null) {
      getLogger().error(
        "No layout set for the appender named [{}].", name);
    }
    
    if(this.writer != null) {
      inOrder = true;
    }
  }

  /**
     This method is called by the {@link AppenderSkeleton#doAppend}
     method.

     <p>If the output stream exists and is writable then write a log
     statement to the output stream. Otherwise, write a single warning
     message to <code>System.err</code>.

     <p>The format of the output will depend on this appender's
     layout.

  */
  public void append(LoggingEvent event) {
    // Reminder: the nesting of calls is:
    //
    //    doAppend()
    //      - check threshold
    //      - filter
    //      - append();
    //        - checkEntryConditions();
    //        - subAppend();
    if (!checkEntryConditions()) {
      return;
    }

    subAppend(event);
  }

  /**
     This method determines if there is a sense in attempting to append.

     <p>It checks whether there is a set output target and also if
     there is a set layout. If these checks fail, then the boolean
     value <code>false</code> is returned. */
  protected boolean checkEntryConditions() {
    if (this.closed) {
      getLogger().warn("Not allowed to write to a closed appender.");

      return false;
    }

    if (this.writer == null) {
      getLogger().error(
        "No output stream or file set for the appender named [{}].", name);

      return false;
    }
    
    return true;
  }

  /**
     Close this appender instance. The underlying stream or writer is
     also closed.

     <p>Closed appenders cannot be reused.

     @see #setWriter
     @since 0.8.4 */
  public synchronized void close() {
    if (this.closed) {
      return;
    }

    this.closed = true;
    closeWriter();
  }

  /**
   * Close the underlying {@link java.io.Writer}.
   * */
  protected void closeWriter() {
    if (this.writer != null) {
      try {
        // before closing we have to output out layout's footer
        writeFooter();
        this.writer.close();
        this.writer = null;
      } catch (IOException e) {
        getLogger().error("Could not close writer for WriterAppener named "+name, e);
      }
    }
    
  }

  /**
     Returns an OutputStreamWriter when passed an OutputStream.  The
     encoding used will depend on the value of the
     <code>encoding</code> property.  If the encoding value is
     specified incorrectly the writer will be opened using the default
     system encoding (an error message will be printed to the loglog.  */
  protected OutputStreamWriter createWriter(OutputStream os) {
    OutputStreamWriter retval = null;

    String enc = getEncoding();

    if (enc != null) {
      try {
        retval = new OutputStreamWriter(os, enc);
      } catch (IOException e) {
        getLogger().warn("Error initializing output writer.");
        getLogger().warn("Unsupported encoding?");
      }
    }

    if (retval == null) {
      retval = new OutputStreamWriter(os);
    }

    return retval;
  }

  public String getEncoding() {
    return encoding;
  }

  public void setEncoding(String value) {
    encoding = value;
  }

  /**
    <p>Sets the Writer where the log output will go. The
    specified Writer must be opened by the user and be
    writable.

    <p>The <code>java.io.Writer</code> will be closed when the
    appender instance is closed.


    <p><b>WARNING:</b> Logging to an unopened Writer will fail.
    <p>
    @param writer An already opened Writer.  */
  public synchronized void setWriter(Writer writer) {
    // close any previously opened writer
    closeWriter();
    
    this.writer = writer;
    //this.tp = new TracerPrintWriter(qw);
    writeHeader();
  }

  /**
   * Actual writing occurs here. 
   * <p>Most subclasses of <code>WriterAppender</code> will need to override 
   * this method.
   *  
   * @since 0.9.0 
   * */
  protected void subAppend(LoggingEvent event) {
    if(!this.inOrder) {
      return;
    }
    
    try {
      this.writer.write(this.layout.format(event));

      if (layout.ignoresThrowable()) {
        String[] s = event.getThrowableStrRep();

        if (s != null) {
          int len = s.length;

          for (int i = 0; i < len; i++) {
            this.writer.write(s[i]);
            this.writer.write(Layout.LINE_SEP);
          }
        }
      }

      if (this.immediateFlush) {
        this.writer.flush();
      }
    } catch(IOException ioe) {
      this.inOrder = false;
      getLogger().error("IO failure for appender named "+name, ioe);
    }
  }

  /**
     The WriterAppender requires a layout. Hence, this method returns
     <code>true</code>.
  */
  public boolean requiresLayout() {
    return true;
  }

  /**
   * Clear internal references to the writer and other variables.
   * 
   * Subclasses can override this method for an alternate closing 
   * behavior. 
   * 
   * @deprecated Use {@link #closeWriter} method instead.  
   * */
  protected void xreset() {
    closeWriter();
  }

  /**
   * Write a footer as produced by the embedded layout's {@link 
   * Layout#getFooter} method.  
   * */
  protected void writeFooter() {
    if (layout != null) {
      String f = layout.getFooter();

      if ((f != null) && (this.writer != null)) {
        try {
          this.writer.write(f);
          this.writer.flush();
        } catch(IOException ioe) {
          inOrder = false;
          getLogger().error("Failed to write footer for Appender named "+name, ioe);
        }
      }
    }
  }

  /**
   * Write a header as produced by the embedded layout's {@link 
   * Layout#getHeader} method.  
   * */
  protected void writeHeader() {
    if (layout != null) {
      String h = layout.getHeader();

      if ((h != null) && (this.writer != null)) {
        try {
          this.writer.write(h);
        } catch(IOException ioe) {
          this.inOrder = false;
          getLogger().error("Failed to write header for WriterAppender named "+name, ioe);
        }
      }
    }
  }
}
