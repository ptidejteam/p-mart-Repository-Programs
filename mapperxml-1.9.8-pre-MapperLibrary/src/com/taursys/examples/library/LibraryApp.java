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

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

//import oracle.jdbc.pool.OracleConnectionPoolDataSource;

import com.taursys.debug.Debug;
import com.taursys.debug.SimpleLogger;
import com.taursys.servlet.ServletApp;

/**
 * LibraryApp is the main ServletApp for this application.
 * It acts as dispatcher for application ServletForms.
 * @author ${author}
 * @version 1.0
 */
public class LibraryApp extends ServletApp {
//  private OracleConnectionPoolDataSource dataSource;
  private DataSource dataSource;

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
    getFactory().addPackage("/","com.taursys.examples.library");
    getFactory().setDefaultFormName("com.taursys.examples.library.MainMenu");
    getFactory().setDefaultClassLoader(getClass().getClassLoader());
    // Set default logging
    Debug.setLoggerAdapter(new SimpleLogger(Debug.DEBUG));
    try {
      // Either create the datasource or fetch from container context

      // Create new pooled DataSource      
//      dataSource = new OracleConnectionPoolDataSource();
//      dataSource.setURL("jdbc:oracle:thin:@192.168.5.1:1521:ORCL");
//      dataSource.setUser("librarian");
//      dataSource.setPassword("shelves");

      // Fetch DataSource from container context
      Context lContext = new InitialContext();
      dataSource = (DataSource) lContext.lookup(
         "java:/LibraryDS"
      );

    } catch (Exception ex) {
      throw new ServletException("Cannot open database connection pool", ex);
    }
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
      // Share dataSource with ServletForms
      request.setAttribute("dataSource", dataSource);
      super.doGet(request, response);
    }
  }

}
