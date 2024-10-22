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

package org.apache.log4j.helpers;

import org.apache.log4j.spi.ErrorCode;
import org.apache.log4j.spi.ErrorHandler;

import java.io.FilterWriter;
import java.io.IOException;
import java.io.Writer;


/**
 * QuietWriter does not throw exceptions when things go
 * wrong. Instead, it delegates error handling to its {@link ErrorHandler}.
 * 
 * @author Ceki G&uuml;lc&uuml;
 * 
 * @since 0.7.3
*/
public class QuietWriter extends FilterWriter {
  protected ErrorHandler errorHandler;

  public QuietWriter(Writer writer, ErrorHandler errorHandler) {
    super(writer);
    setErrorHandler(errorHandler);
  }

  public Writer getWriter() {
    return out;
  }
  
  public void write(String string) {
    try {
      out.write(string);
    } catch (IOException e) {
      errorHandler.error(
        "Failed to write [" + string + "].", e, ErrorCode.WRITE_FAILURE);
    }
  }

  public void flush() {
    try {
      out.flush();
    } catch (IOException e) {
      errorHandler.error(
        "Failed to flush writer,", e, ErrorCode.FLUSH_FAILURE);
    }
  }

  public void setErrorHandler(ErrorHandler eh) {
    if (eh == null) {
      // This is a programming error on the part of the enclosing appender.
      throw new IllegalArgumentException(
        "Attempted to set null ErrorHandler.");
    } else {
      this.errorHandler = eh;
    }
  }
}
