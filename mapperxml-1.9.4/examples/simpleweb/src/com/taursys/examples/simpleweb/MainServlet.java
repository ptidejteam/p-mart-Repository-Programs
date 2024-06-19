/**
 * Example Mapper Application
 * by: Marty Phelan
 *
 * This example is free software; you can redistribute it and/or
 * modify it as you wish.  It is released to the public domain.
 *
 * This example is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 */
package com.taursys.examples.simpleweb;

import javax.servlet.ServletException;
import javax.servlet.ServletConfig;
import com.taursys.servlet.ServletApp;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import com.taursys.debug.*;

/**
 * MainServlet acts as dispatcher for application ServletForms
 */
public class MainServlet extends ServletApp {

  /**
   * Initialize ServletFormFactory
   */
  public void init(ServletConfig config) throws ServletException {
    super.init(config);
    getFactory().addPackage("/","com.taursys.examples.simpleweb");
    getFactory().setDefaultFormName("com.taursys.examples.simpleweb.FirstPage");
    // Set defaultClassLoader if mapperxml.jar is shared & not in your app's .war
    getFactory().setDefaultClassLoader(getClass().getClassLoader());
    // Set default logging
    Debug.setLoggerAdapter(new SimpleLogger(Debug.DEBUG));
  }

  /**
   * Service request but first add any shared resources
   */
  public void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    // request.setAttribute("com.yourname.resourcename", yourSharedResource);
    super.doGet(request, response);
  }

}