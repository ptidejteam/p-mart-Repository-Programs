/*
 * $Header: /home/cvs/jakarta-struts/src/share/org/apache/struts/tiles/xmlDefinition/I18nFactorySet.java,v 1.7 2003/03/16 16:02:11 husted Exp $
 * $Revision: 1.7 $
 * $Date: 2003/03/16 16:02:11 $
 *
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 1999-2002 The Apache Software Foundation.  All rights
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
 * 4. The names "The Jakarta Project", "Struts", and "Apache Software
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
 *
 */


package org.apache.struts.tiles.xmlDefinition;

import org.apache.struts.tiles.DefinitionsFactoryException;
import org.apache.struts.tiles.FactoryNotFoundException;
import org.apache.struts.tiles.DefinitionsUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.ServletRequest;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpServletRequest;

import java.util.Map;
import java.util.Locale;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.io.InputStream;
import java.io.IOException;
import java.io.FileNotFoundException;

import org.xml.sax.SAXException;

/**
 * Definitions factory.
 * This implementation allows to have a set of definition factories.
 * There is a main factory and one factory for each file associated to a Locale.
 *
 * To retrieve a definition, we first search for the appropriate factory using
 * the Locale found in session context. If no factory is found, use the
 * default one. Then we ask the factory for the definition.
 *
 * A definition factory file is loaded using main filename extended with locale code
 * (ex : <code>templateDefinitions_fr.xml</code>). If no file is found under this name, use default file.
*/
public class I18nFactorySet extends FactorySet
{
    /** Commons Logging instance. */
  protected static Log log = LogFactory.getLog(I18nFactorySet.class);

    /** Config file parameter name.
     * @deprecated use DEFINITIONS_CONFIG_PARAMETER_NAME
     */
  public static final String INSTANCES_CONFIG_PARAMETER_NAME = "instances-config";

    /** Default name */
  //public static final String DEFAULT_DEFINITIONS_FILE_NAME = "/WEB-INF/componentDefinitions.xml";
    /** Config file parameter name. */
  public static final String DEFINITIONS_CONFIG_PARAMETER_NAME = "definitions-config";
    /** Config file parameter name. */
  public static final String PARSER_DETAILS_PARAMETER_NAME = "definitions-parser-details";
    /** Config file parameter name. */
  public static final String PARSER_VALIDATE_PARAMETER_NAME = "definitions-parser-validate";

    /** Possible definition filenames. */
  public static final String DEFAULT_DEFINITION_FILENAMES[] = {
                      "/WEB-INF/tileDefinitions.xml",
                      "/WEB-INF/componentDefinitions.xml",
                      "/WEB-INF/instanceDefinitions.xml"};
    /** Default factory. */
  protected DefinitionsFactory defaultFactory;
    /** Xml parser used.
     *  Attribute is transient to allow serialization. In this implementaiton,
     *  xmlParser is created each time we need it ;-(.
     */
  protected transient XmlParser xmlParser;
    /** Do we want validating parser. Default is <code>false</code>.
     *  Can be set from servlet config file.
     */
  protected boolean isValidatingParser = false;
    /** Parser detail level. Default is 0.
     *  Can be set from servlet config file.
     */
  protected int parserDetailLevel = 0;

    /**
     * Maximum length of one branch of the resource search path tree.
     * Used in getBundle().
     */
  private static final int MAX_BUNDLES_SEARCHED = 2;
    /** Default filenames extension. */
  public static final String FILENAME_EXTENSION = ".xml";

    /** Names of files containing instances descriptions. */
  private List filenames;
    /** Collection of already loaded definitions set, referenced by their suffix. */
  private Map loaded;


    /**
     * Parameterless Constructor.
     * Method {@link #initFactory} must be called prior to any use of created factory.
     */
  public I18nFactorySet()
  {
  }

    /**
     * Constructor.
     * Init the factory by reading appropriate configuration file.
     * @param servletContext Servlet context.
     * @param properties Map containing all properties.
     * @throws FactoryNotFoundException Can't find factory configuration file.
     */
  public I18nFactorySet(ServletContext servletContext, Map properties )
         throws DefinitionsFactoryException
  {
  initFactory( servletContext, properties);
  }

    /**
     * Initialization method.
     * Init the factory by reading appropriate configuration file.
     * This method is called exactly once immediately after factory creation in
     * case of internal creation (by DefinitionUtil).
     * @param servletContext Servlet Context passed to newly created factory.
     * @param properties Map of name/property passed to newly created factory. Map can contains
     * more properties than requested.
     * @throws DefinitionsFactoryException An error occur during initialization.
     */
  public void initFactory(ServletContext servletContext, Map properties )
         throws DefinitionsFactoryException
  {
    // Set some property values
  String value = (String)properties.get(PARSER_VALIDATE_PARAMETER_NAME);
  if( value != null )
    {
    isValidatingParser = Boolean.valueOf( value ).booleanValue();
    }  // end if
  value = (String)properties.get(PARSER_DETAILS_PARAMETER_NAME);
  if( value != null )
    {
    try {
      parserDetailLevel = Integer.valueOf( value ).intValue();
      }
     catch( NumberFormatException ex )
      {
      log.error( "Bad format for parameter '"
                        + PARSER_DETAILS_PARAMETER_NAME
                        + "'. Integer expected.");
      }
    }  // end if

    // init factory withappropriate configuration file
    // Try to use provided filename, if any.
    // If no filename are provided, try to use default ones.
  String filename = (String)properties.get(DEFINITIONS_CONFIG_PARAMETER_NAME);
  if( filename != null )
    { // Use provided filename
    try
      {
      initFactory( servletContext, filename );
          if(log.isDebugEnabled())
            log.debug( "Factory initialized from file '" + filename + "'." );
      }
     catch( FileNotFoundException ex )
        { // A filename is specified, throw appropriate error.
          log.error( ex.getMessage() + " : Can't find file '" +filename + "'" );
          throw new FactoryNotFoundException( ex.getMessage() + " : Can't find file '" +filename + "'" ) ;
        } // end catch
      }
     else
      { // try each default file names
      for( int i=0; i<DEFAULT_DEFINITION_FILENAMES.length; i++ )
        {
        filename = DEFAULT_DEFINITION_FILENAMES[i];
        try
          {
          initFactory( servletContext, filename );
          if(log.isInfoEnabled())
            {
            log.info( "Factory initialized from file '" + filename + "'." );
            }
          }
         catch( FileNotFoundException ex )
          { // Do nothing
          } // end catch
        } // end loop
      } // end if

   }

    /**
     * Initialization method.
     * Init the factory by reading appropriate configuration file.
     * This method is called exactly once immediately after factory creation in
     * case of internal creation (by DefinitionUtil).
     * @param servletContext Servlet Context passed to newly created factory.
     * @param proposedFilename File names, comma separated, to use as  base file names.
     * @throws DefinitionsFactoryException An error occur during initialization.
     */
  protected void initFactory(ServletContext servletContext, String proposedFilename )
         throws DefinitionsFactoryException, FileNotFoundException
  {
      // Init list of filenames
    StringTokenizer tokenizer = new StringTokenizer( proposedFilename, "," );
    this.filenames = new ArrayList(tokenizer.countTokens());
    while( tokenizer.hasMoreTokens() )
      {
      this.filenames.add(tokenizer.nextToken().trim());
      }

    loaded = new HashMap();
    defaultFactory = createDefaultFactory( servletContext );
    if(log.isDebugEnabled())
      log.debug( "default factory:" + defaultFactory );
  }

  /**
   * Get default factory.
   * @return Default factory
   */
  protected DefinitionsFactory getDefaultFactory()
  {
  return defaultFactory;
  }

   /**
    * Create default factory .
   * Create InstancesMapper for specified Locale.
   * If creation failes, use default mapper and log error message.
   * @param servletContext Current servlet context. Used to open file.
   * @return Created default definition factory.
   * @throws DefinitionsFactoryException If an error occur while creating factory.
   * @throws FileNotFoundException if factory can't be loaded from filenames.
    */
  protected DefinitionsFactory createDefaultFactory(ServletContext servletContext)
    throws DefinitionsFactoryException, FileNotFoundException
    {
    XmlDefinitionsSet rootXmlConfig = parseXmlFiles( servletContext, "", null );
    if( rootXmlConfig == null )
      throw new FileNotFoundException();

    rootXmlConfig.resolveInheritances();

    if(log.isDebugEnabled())
      log.debug( rootXmlConfig );

    DefinitionsFactory factory = new DefinitionsFactory( rootXmlConfig );
    if(log.isDebugEnabled())
      log.debug( "factory loaded : " + factory );

    return factory;
    }

  /**
   * Extract key that will be used to get the sub factory.
   * @param name Name of requested definition
   * @param request Current servlet request.
   * @param servletContext Current servlet context.
   * @return the key or <code>null</code> if not found.
   */
  protected Object getDefinitionsFactoryKey(String name, ServletRequest request, ServletContext servletContext)
  {
  Locale locale = null;
  try
    {
    HttpSession session = ((HttpServletRequest)request).getSession(false);
    if (session != null)
      locale = (Locale)session.getAttribute(DefinitionsUtil.LOCALE_KEY);
    }
   catch( ClassCastException ex )
    { //
    log.error( "I18nFactorySet.getDefinitionsFactoryKey" );
    ex.printStackTrace();
    }

  return locale;
  }

   /**
    * Create a factory for specified key.
   * If creation failes, return default factory and log an error message.
   * @param key The key.
   * @param request Servlet request.
   * @param servletContext Servlet context.
   * @return Definition factory for specified key.
   * @throws DefinitionsFactoryException If an error occur while creating factory.
    */
  protected DefinitionsFactory createFactory(Object key, ServletRequest request, ServletContext servletContext)
    throws DefinitionsFactoryException
    {
    if( key == null )
      return getDefaultFactory();


      // Build possible postfixes
    List possiblePostfixes = calculatePostixes( "", (Locale)key );


      // Search last postix corresponding to a config file to load.
      // First check if something is loaded for this postfix.
      // If not, try to load its config.
    XmlDefinitionsSet lastXmlFile = null;
    DefinitionsFactory factory = null;
    String curPostfix = null;
    int i;

    for(i=possiblePostfixes.size()-1; i>=0; i-- )
      {
      curPostfix = (String)possiblePostfixes.get(i);
        // Already loaded ?
      factory = (DefinitionsFactory)loaded.get( curPostfix );
      if( factory != null )
        { // yes, stop search
        return factory;
        } // end if
        // Try to load it. If success, stop search
      lastXmlFile = parseXmlFiles( servletContext, curPostfix, null );
      if( lastXmlFile != null )
        break;
      } // end loop

      // Have we found a description file ?
      // If no, return default one
    if( lastXmlFile == null )
      {
      return getDefaultFactory();
      }

      // We found something. Need to load base and intermediate files
    String lastPostfix = curPostfix;
    XmlDefinitionsSet rootXmlConfig = parseXmlFiles( servletContext, "", null );
    for( int j=0; j<i; j++ )
      {
      curPostfix = (String)possiblePostfixes.get(j);
      parseXmlFiles( servletContext, curPostfix, rootXmlConfig);
      } // end loop

    rootXmlConfig.extend( lastXmlFile );
    rootXmlConfig.resolveInheritances();

    factory = new DefinitionsFactory(rootXmlConfig);
    loaded.put( lastPostfix, factory );
      // User help
    if(log.isDebugEnabled())
      log.debug( "factory loaded : " + factory );
      // return last available found !
    return factory;
    }

    /**
     * Calculate the postixes along the search path from the base bundle to the
     * bundle specified by baseName and locale.
     * Method copied from java.util.ResourceBundle
     * @param baseName the base bundle name
     * @param locale the locale
     */
    private static List calculatePostixes(String baseName, Locale locale) {
        final List result = new ArrayList(MAX_BUNDLES_SEARCHED);
        final String language = locale.getLanguage();
        final int languageLength = language.length();
        final String country = locale.getCountry();
        final int countryLength = country.length();
        final String variant = locale.getVariant();
        final int variantLength = variant.length();

        if (languageLength + countryLength + variantLength == 0) {
            //The locale is "", "", "".
            return result;
        }
        final StringBuffer temp = new StringBuffer(baseName);
        temp.append('_');
        temp.append(language);

        if(languageLength > 0)
            result.add(temp.toString());

        if (countryLength + variantLength == 0)
            return result;

        temp.append('_');
        temp.append(country);

        if(countryLength > 0)
            result.add(temp.toString());

        if(variantLength == 0) {
            return result;
        } else {
            temp.append('_');
            temp.append(variant);
            result.add(temp.toString());
            return result;
        }
    }

    /**
     * Parse files associated to postix if they exist.
     * For each name in filenames, append postfix before file extension,
     * then try to load the corresponding file.
     * If file doesn't exist, try next one. Each file description is added to
     * the XmlDefinitionsSet description.
     * The XmlDefinitionsSet description is created only if there is a definition file.
     * Inheritance is not resolved in the returned XmlDefinitionsSet.
     * If no description file can be opened and no definiion set is provided, return <code>null</code>.
     * @param postfix Postfix to add to each description file.
     * @param xmlDefinitions Definitions set to which definitions will be added. If <code>null</code>, a definitions
     * set is created on request.
     * @return XmlDefinitionsSet The definitions set created or passed as parameter.
     * @throws DefinitionsFactoryException On errors parsing file.
     */
  private XmlDefinitionsSet parseXmlFiles( ServletContext servletContext, String postfix, XmlDefinitionsSet xmlDefinitions )
      throws DefinitionsFactoryException
    {
    if( postfix != null && postfix.length() == 0 )
      postfix = null;

      // Iterate throw each file name in list
    Iterator i = filenames.iterator();
    while( i.hasNext() )
      {
      String filename = concatPostfix((String)i.next(), postfix) ;
      xmlDefinitions = parseXmlFile( servletContext, filename, xmlDefinitions );
      } // end loop
    return xmlDefinitions;
    }

    /**
     * Parse specified xml file and add definition to specified definitions set.
     * This method is used to load several description files in one instances list.
     * If filename exists and definition set is <code>null</code>, create a new set. Otherwise, return
     * passed definition set (can be <code>null</code>).
     * @param servletContext Current servlet context. Used to open file.
     * @param filename Name of file to parse.
     * @param xmlDefinitions Definitions set to which definitions will be added. If null, a definitions
     * set is created on request.
     * @return XmlDefinitionsSet The definitions set created or passed as parameter.
     * @throws DefinitionsFactoryException On errors parsing file.
     */
  private XmlDefinitionsSet parseXmlFile( ServletContext servletContext, String filename , XmlDefinitionsSet xmlDefinitions)
      throws DefinitionsFactoryException
    {
    try
      {
	    InputStream input = servletContext.getResourceAsStream(filename);
        // Try to load using real path.
        // This allow to load config file under websphere 3.5.x
        // Patch proposed Houston, Stephen (LIT) on 5 Apr 2002
      if (null == input)
      {
        try
         {
         input = new java.io.FileInputStream(servletContext.getRealPath(filename));
         }
         catch (Exception e)
         {
         }
     }
        // If still nothing found, this mean no config file is associated
	    if(input == null )
        {
        if(log.isDebugEnabled())
          log.debug( "Can't open file '" + filename + "'" );
        return xmlDefinitions;
        }

        // Check if parser already exist.
        // Doesn't seem to work yet.
      //if( xmlParser == null )
      if( true )
        {  // create it
        //if(log.isDebugEnabled())
          //log.debug( "Create xmlParser");
        xmlParser = new XmlParser();
        xmlParser.setValidating(isValidatingParser);
        xmlParser.setDetailLevel(parserDetailLevel);
        }
        // Check if definition set already exist.
      if( xmlDefinitions == null )
        {  // create it
        //if(log.isDebugEnabled())
          //log.debug( "Create xmlDefinitions");
        xmlDefinitions = new XmlDefinitionsSet();
        }

      xmlParser.parse( input, xmlDefinitions );
	    }
	   catch( SAXException ex )
	    {
      if(log.isDebugEnabled())
        {
        log.debug( "Error while parsing file '"  + filename + "'.");
        ex.printStackTrace();
        }
	    throw new DefinitionsFactoryException( "Error while parsing file '" + filename + "'. " + ex.getMessage(), ex );
	    }
	   catch( IOException ex )
	    {
	    throw new DefinitionsFactoryException( "IO Error while parsing file '" + filename + "'. " + ex.getMessage(), ex);
	    }

    return xmlDefinitions;
    }

    /**
     * Concat postfix to the name. Take care of existing filename extension.
     * Transform the given name "name.ext" to have "name" + "postfix" + "ext".
     * If there is no ext, return "name" + "postfix".
     * @param name Filename.
     * @param postfix Postfix to add.
     * @return Concatenated filename.
     */
  private String concatPostfix( String name, String postfix )
    {
    if( postfix == null )
      return name;

      // Search file name extension.
      // take care of Unix files starting with .
    int dotIndex = name.lastIndexOf( "." );
    int lastNameStart = name.lastIndexOf( java.io.File.pathSeparator );
    if( dotIndex < 1 || dotIndex < lastNameStart )
      return name + postfix;

    String ext = name.substring( dotIndex );
    name = name.substring( 0, dotIndex);
    return name + postfix + ext;
    }

    /**
     * Return String representation.
     * @return String representation.
     */
  public String toString()
    {
    StringBuffer buff = new StringBuffer( "I18nFactorySet : \n" );
    buff.append( "--- default factory ---\n" );
    buff.append( defaultFactory.toString() );
    buff.append( "\n--- other factories ---\n" );
    Iterator i = factories.values().iterator();
    while( i.hasNext() )
      {
      buff.append( i.next().toString() ).append("---------- \n");
      }
    return buff.toString();
    }


}
