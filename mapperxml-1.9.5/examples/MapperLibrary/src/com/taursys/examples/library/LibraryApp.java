package com.taursys.examples.library;

import com.taursys.servlet.*;
import com.taursys.debug.*;
import javax.servlet.ServletException;
import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import javax.sql.DataSource;
import oracle.jdbc.pool.OracleConnectionPoolDataSource;

/**
 * LibraryApp is the main ServletApp for this application.
 * It acts as dispatcher for application ServletForms.
 * @author ${author}
 * @version 1.0
 */
public class LibraryApp extends ServletApp {
  private OracleConnectionPoolDataSource dataSource;

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
      dataSource = new OracleConnectionPoolDataSource();
      dataSource.setURL("jdbc:oracle:thin:@192.168.5.1:1521:ORCL");
      dataSource.setUser("librarian");
      dataSource.setPassword("shelves");
    } catch (Exception ex) {
      throw new ServletException("Cannot open database connection pool", ex);
    }
  }

  /**
   * Service request but first add any applications resources
   */
  public void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    // Share dataSource with ServletForms
    request.setAttribute("dataSource", dataSource);
    super.doGet(request, response);
  }
}
