package org.apache.velocity.runtime.resource.loader;

/*
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2001-2002 The Apache Software Foundation.  All rights
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
 * 3. The end-user documentation included with the redistribution, if
 *    any, must include the following acknowlegement:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowlegement may appear in the software itself,
 *    if and wherever such third-party acknowlegements normally appear.
 *
 * 4. The names "The Jakarta Project", "Velocity", and "Apache Software
 *    Foundation" must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache"
 *    nor may "Apache" appear in their names without prior written
 *    permission of the Apache Group.
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
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */

import java.io.InputStream;
import java.io.BufferedInputStream;
import java.util.Hashtable;

import javax.sql.DataSource;
import javax.naming.InitialContext;

import org.apache.velocity.runtime.Runtime;
import org.apache.velocity.runtime.resource.Resource;

import org.apache.velocity.exception.ResourceNotFoundException;

import org.apache.commons.collections.ExtendedProperties;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;

/**
 * This is a simple template file loader that loads templates
 * from a DataSource instead of plain files.
 *
 * It can be configured with a datasource name, a table name,
 * id column (name), content column (the template body) and a
 * datetime column (for last modification info).
 * <br>
 * <br>
 * Example configuration snippet for velocity.properties:
 * <br>
 * <br>
 * resource.loader = file, ds <br>
 * <br>
 * ds.resource.loader.public.name = DataSource <br>
 * ds.resource.loader.description = Velocity DataSource Resource Loader <br>
 * ds.resource.loader.class = org.apache.velocity.runtime.resource.loader.DataSourceResourceLoader <br>
 * ds.resource.loader.resource.datasource = java:comp/env/jdbc/Velocity <br>
 * ds.resource.loader.resource.table = tb_velocity_template <br>
 * ds.resource.loader.resource.keycolumn = id_template <br>
 * ds.resource.loader.resource.templatecolumn = template_definition <br>
 * ds.resource.loader.resource.timestampcolumn = template_timestamp <br>
 * ds.resource.loader.cache = false <br>
 * ds.resource.loader.modificationCheckInterval = 60 <br>
 * <br>
 * Example WEB-INF/web.xml: <br>
 * <br>
 *	<resource-ref> <br>
 *	 <description>Velocity template DataSource</description> <br>
 *	 <res-ref-name>jdbc/Velocity</res-ref-name> <br>
 *	 <res-type>javax.sql.DataSource</res-type> <br>
 *	 <res-auth>Container</res-auth> <br>
 *	</resource-ref> <br>
 * <br>
 *  <br>
 * and Tomcat 4 server.xml file: <br>
 *  [...] <br>
 *  <Context path="/exampleVelocity" docBase="exampleVelocity" debug="0"> <br>
 *  [...] <br>
 *   <ResourceParams name="jdbc/Velocity"> <br>
 *    <parameter> <br>
 *      <name>driverClassName</name> <br>
 *      <value>org.hsql.jdbcDriver</value> <br>
 *    </parameter> <br>
 *    <parameter> <br>
 *     <name>driverName</name> <br>
 *     <value>jdbc:HypersonicSQL:database</value> <br>
 *    </parameter> <br>
 *    <parameter> <br>
 *     <name>user</name> <br>
 *     <value>database_username</value> <br>
 *    </parameter> <br>
 *    <parameter> <br>
 *     <name>password</name> <br>
 *     <value>database_password</value> <br>
 *    </parameter> <br>
 *   </ResourceParams> <br>
 *  [...] <br>
 *  </Context> <br>
 *  [...] <br>
 * <br>
 *  Example sql script:<br>
 *  CREATE TABLE tb_velocity_template ( <br>
 *	id_template varchar (40) NOT NULL , <br>
 *	template_definition text (16) NOT NULL , <br>
 *	template_timestamp datetime NOT NULL  <br>
 *	) <br>
 *
 * @author <a href="mailto:david.kinnvall@alertir.com">David Kinnvall</a>
 * @author <a href="mailto:paulo.gaspar@krankikom.de">Paulo Gaspar</a>
 * @author <a href="mailto:lachiewicz@plusnet.pl">Sylwester Lachiewicz</a>
 * @version $Id: DataSourceResourceLoader.java,v 1.8 2002/02/10 18:46:58 geirm Exp $
 */
public class DataSourceResourceLoader extends ResourceLoader
{
     private String dataSourceName;
     private String tableName;
     private String keyColumn;
     private String templateColumn;
     private String timestampColumn;
     private InitialContext ctx;
     private DataSource dataSource;

     public void init( ExtendedProperties configuration)
     {
         dataSourceName  = configuration.getString("resource.datasource");
         tableName       = configuration.getString("resource.table");
         keyColumn       = configuration.getString("resource.keycolumn");
         templateColumn  = configuration.getString("resource.templatecolumn");
         timestampColumn = configuration.getString("resource.timestampcolumn");

         Runtime.info("Resources Loaded From: " + dataSourceName + "/" + tableName);
         Runtime.info( "Resource Loader using columns: " + keyColumn + ", "
                       + templateColumn + " and " + timestampColumn);
         Runtime.info("Resource Loader Initalized.");
     }

     public boolean isSourceModified(Resource resource)
     {
         return (resource.getLastModified() !=
                 readLastModified(resource, "checking timestamp"));
     }

     public long getLastModified(Resource resource)
     {
         return readLastModified(resource, "getting timestamp");
     }

     /**
      * Get an InputStream so that the Runtime can build a
      * template with it.
      *
      *  @param name name of template
      *  @return InputStream containing template
      */
     public synchronized InputStream getResourceStream( String name )
         throws ResourceNotFoundException
     {
         if (name == null || name.length() == 0)
         {
             throw new ResourceNotFoundException ("Need to specify a template name!");
         }

         try
         {
             Connection conn = openDbConnection();

             try
             {
                 ResultSet rs = readData(conn, templateColumn, name);

                 try
                 {
                     if (rs.next())
                     {
                         return new
                             BufferedInputStream(rs.getAsciiStream(templateColumn));
                     }
                     else
                     {
                         String msg = "DataSourceResourceLoader Error: cannot find resource "
                             + name;
                         Runtime.error(msg );

                         throw new ResourceNotFoundException (msg);
                     }
                 }
                 finally
                 {
                     rs.close();
                 }
             }
             finally
             {
                 closeDbConnection(conn);
             }
         }
         catch(Exception e)
         {
             String msg =  "DataSourceResourceLoader Error: database problem trying to load resource "
                 + name + ": " + e.toString();

             Runtime.error( msg );

             throw new ResourceNotFoundException (msg);

         }

     }

    /**
     *  Fetches the last modification time of the resource
     *
     *  @param resource Resource object we are finding timestamp of
     *  @param i_operation string for logging, indicating caller's intention
     *
     *  @return timestamp as long
     */
     private long readLastModified(Resource resource, String i_operation)
     {
         /*
          *  get the template name from the resource
          */

         String name = resource.getName();
         try
         {
             Connection conn = openDbConnection();

             try
             {
                 ResultSet rs = readData(conn, timestampColumn, name);
                 try
                 {
                     if (rs.next())
                     {
	                     return rs.getTimestamp(timestampColumn).getTime();
                     }
                     else
                     {
                         Runtime.error("DataSourceResourceLoader Error: while "
                                       + i_operation
                                       + " could not find resource " + name);
                     }
                 }
                 finally
                 {
                     rs.close();
                 }
             }
             finally
             {
                 closeDbConnection(conn);
             }
         }
         catch(Exception e)
         {
             Runtime.error( "DataSourceResourceLoader Error: error while "
                 + i_operation + " when trying to load resource "
                 + name + ": " + e.toString() );
         }
         return 0;
     }

    /**
     *   gets connection to the datasource specified through the configuration
     *  parameters.
     *
     *  @return connection
     */
     private Connection openDbConnection()
         throws Exception
    {
         if(ctx == null)
         {
             ctx = new InitialContext();
         }

         if(dataSource == null)
         {
             dataSource = (DataSource)ctx.lookup(dataSourceName);
         }

         return dataSource.getConnection();
     }

    /**
     *  Closes connection to the datasource
     */
     private void closeDbConnection(Connection conn)
     {
         try
         {
             conn.close();
         }
         catch (Exception e)
         {
             Runtime.info(
                 "DataSourceResourceLoader Quirk: problem when closing connection: "
                 + e.toString());
         }
     }

    /**
     *  Reads the data from the datasource.  It simply does the following query :
     *  <br>
     *   SELECT <i>columnNames</i> FROM <i>tableName</i> WHERE <i>keyColumn</i>
     *      = '<i>templateName</i>'
     *  <br>
     *  where <i>keyColumn</i> is a class member set in init()
     *
     *  @param conn connection to datasource
     *  @param columnNames columns to fetch from datasource
     *  @param templateName name of template to fetch
     *  @return result set from query
     */
     private ResultSet readData(Connection conn, String columnNames, String templateName)
         throws SQLException
     {
         Statement stmt = conn.createStatement();

         String sql = "SELECT " + columnNames
                      + " FROM " + tableName
                      + " WHERE " + keyColumn + " = '" + templateName + "'";

         return stmt.executeQuery(sql);
     }
}
