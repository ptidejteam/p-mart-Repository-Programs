/**
 * Copyright 2005 The Apache Software Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.nutch.servlet;

import org.apache.nutch.searcher.NutchBean;
import org.apache.nutch.searcher.Hit;
import org.apache.nutch.searcher.HitDetails;

import org.apache.nutch.util.NutchConf;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.Properties;

import java.io.OutputStream;
import java.io.IOException;

/**
 * A servlet that serves raw Content of any mime type.
 *
 * If it fails with java.lang.OutOfMemoryError,
 * you may have to increase heap size when starting container,
 * such as -Xms1024M -Xmx1024M
 *
 * @author John Xing
 */

public class Cached extends HttpServlet {

  NutchBean bean = null;

  public void init() {
    try {
      bean = NutchBean.get(this.getServletContext());
    } catch (IOException e) {
      // nothing
    }
  }

  public void destroy() {
    // maybe clean bean?
    // nothing now
  }
 
  public void doGet(HttpServletRequest request, HttpServletResponse response)
    throws IOException {

    // quit if no bean
    if (bean == null)
      return;

    bean.LOG.info("request from " + request.getRemoteAddr());

    Hit hit = new Hit(Integer.parseInt(request.getParameter("idx")),
                      Integer.parseInt(request.getParameter("id")));
    HitDetails details = bean.getDetails(hit);

    // raw bytes
    byte[] bytes = bean.getContent(details);

    // pass all original headers? only these for now.
    Properties metaData = bean.getParseData(details).getMetadata();
    String contentType = (String) metaData.get("Content-Type");
    //String lastModified = (String) metaData.get("Last-Modified");
    //String contentLength = (String) metaData.get("Content-Length");
    // better use this, since it may have been truncated during fetch
    // or give warning if they don't match?
    int contentLength = bytes.length;

    // response
    response.setContentType(contentType);
    response.setContentLength(contentLength);

    OutputStream os = response.getOutputStream();
    os.write(bytes);
    // need this or flush more frequently?
    //os.flush();
    os.close();

    return;
  }

  public void doPost(HttpServletRequest request, HttpServletResponse response)
    throws IOException {
    doGet(request, response);
  }

}
