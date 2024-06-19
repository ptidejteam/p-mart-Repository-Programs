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
package com.taursys.examples.library;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.taursys.debug.Debug;
import com.taursys.debug.SimpleLogger;
import com.taursys.examples.library.forms.MainMenu;
import com.taursys.servlet.ServletApp;

/**
 * LibraryApp is the main ServletApp for this application.
 * It acts as dispatcher for application ServletForms.
 * @author ${author}
 * @version 1.0
 */
public class LibraryApp extends ServletApp {

  /**
   * Constructs a new LibraryApp and initializes component properties.
   */
  public LibraryApp() {
  }

  /**
   * Initialize ServletFormFactory
   */
  public void init(ServletConfig config) throws ServletException {
    super.init(config);
    getFactory().addPackage("/",MainMenu.class.getPackage().getName());
    getFactory().setDefaultFormName(MainMenu.class.getName());
    // Set defaultClassLoader if mapperxml.jar is shared & not in your app's
    // .war file
    getFactory().setDefaultClassLoader(getClass().getClassLoader());
    // Set suffix for servlet forms - can be blank - default is .sf
    getFactory().setServletFormSuffix(".html");
    // Set default logging
    Debug.setLoggerAdapter(new SimpleLogger(Debug.DEBUG));
  }

  /**
   * Service request but first add any shared applications resources
   */
  public void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    String action = request.getParameter("action");
    if (action != null && action.equalsIgnoreCase("logout")) {
      request.getSession().invalidate();
      response.sendRedirect("../index.html");
    } else {
      super.doGet(request, response);
    }
  }

}
