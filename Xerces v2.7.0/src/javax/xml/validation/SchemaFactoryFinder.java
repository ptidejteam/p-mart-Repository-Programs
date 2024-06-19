/*
 * Copyright 2003-2004 The Apache Software Foundation.
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
// $Id: SchemaFactoryFinder.java,v 1.1 2007/03/12 16:15:07 guehene Exp $

package javax.xml.validation;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Properties;

/**
 * Implementation of {@link SchemaFactory#newInstance(String)}.
 * 
 * @author <a href="Kohsuke.Kawaguchi@Sun.com">Kohsuke Kawaguchi</a>
 * @version $Revision: 1.1 $, $Date: 2007/03/12 16:15:07 $
 * @since 1.5
 */
class SchemaFactoryFinder  {

    /** debug support code. */
    private static boolean debug = false;
    /**
     *<p> Take care of restrictions imposed by java security model </p>
     */
    private static SecuritySupport ss = new SecuritySupport();
    /**
     * <p>Cache properties for performance.</p>
     */
	private static Properties cacheProps = new Properties();
    
	/**
	 * <p>First time requires initialization overhead.</p>
	 */
	private static boolean firstTime = true;
    
    /**
     * Default columns per line.
     */
    private static final int DEFAULT_LINE_LENGTH = 80;
    
    static {
        // Use try/catch block to support applets
        try {
            debug = ss.getSystemProperty("jaxp.debug") != null;
        } catch (Exception _) {
            debug = false;
        }
    }

    /**
     * <p>Conditional debug printing.</p>
     * 
     * @param msg to print
     */
    private static void debugPrintln(String msg) {
        if (debug) {
            System.err.println("JAXP: " + msg);
        }
    }
    
    /**
     * <p><code>ClassLoader</code> to use to find <code>SchemaFactory</code>.</p>
     */
    private final ClassLoader classLoader;
    
    /**
     * <p>Constructor that specifies <code>ClassLoader</code> to use
     * to find <code>SchemaFactory</code>.</p>
     * 
     * @param loader
     *      to be used to load resource, {@link SchemaFactory}, and
     *      {@link SchemaFactoryLoader} implementations during
     *      the resolution process.
     *      If this parameter is null, the default system class loader
     *      will be used.
     */
    public SchemaFactoryFinder(ClassLoader loader) {
        this.classLoader = loader;
        if( debug ) {
            debugDisplayClassLoader();
        }
    }
    
    private void debugDisplayClassLoader() {
        try {
            if( classLoader == ss.getContextClassLoader() ) {
                debugPrintln("using thread context class loader ("+classLoader+") for search");
                return;
            }
        } catch( Throwable _ ) {
            ; // getContextClassLoader() undefined in JDK1.1 
        }
        
        if( classLoader==ClassLoader.getSystemClassLoader() ) {
            debugPrintln("using system class loader ("+classLoader+") for search");
            return;
        }

        debugPrintln("using class loader ("+classLoader+") for search");
    }
    
    /**
     * <p>Creates a new {@link SchemaFactory} object for the specified
     * schema language.</p>
     * 
     * @param schemaLanguage
     *      See {@link SchemaFactory Schema Language} table in <code>SchemaFactory</code>
     *      for the list of available schema languages.
     * 
     * @return <code>null</code> if the callee fails to create one.
     * 
     * @throws NullPointerException
     *      If the <tt>schemaLanguage</tt> parameter is null.
     */
    public SchemaFactory newFactory(String schemaLanguage) {
        if(schemaLanguage==null)        throw new NullPointerException();
        SchemaFactory f = _newFactory(schemaLanguage);
        if (f != null) {
            debugPrintln("factory '" + f.getClass().getName() + "' was found for " + schemaLanguage);
        } else {
            debugPrintln("unable to find a factory for " + schemaLanguage);
        }
        return f;
    }
    
    /**
     * <p>Lookup a <code>SchemaFactory</code> for the given <code>schemaLanguage</code>.</p>
     * 
     * @param schemaLanguage Schema language to lookup <code>SchemaFactory</code> for.
     *  
     * @return <code>SchemaFactory</code> for the given <code>schemaLanguage</code>.
     */
    private SchemaFactory _newFactory(String schemaLanguage) {
        SchemaFactory sf;
        String propertyName = SERVICE_CLASS.getName() + ":" + schemaLanguage;
        
        // system property look up
        try {
            debugPrintln("Looking up system property '"+propertyName+"'" );
            String r = ss.getSystemProperty(propertyName);
            if(r!=null) {
                debugPrintln("The value is '"+r+"'");
                sf = createInstance(r);
                if(sf!=null)    return sf;
            } else
                debugPrintln("The property is undefined.");
        } catch( Throwable t ) {
            if( debug ) {
                debugPrintln("failed to look up system property '"+propertyName+"'" );
                t.printStackTrace();
            }
        }

        String javah = ss.getSystemProperty( "java.home" );
        String configFile = javah + File.separator +
        "lib" + File.separator + "jaxp.properties";

        String factoryClassName = null ;

        // try to read from $java.home/lib/jaxp.properties
        try {
            if(firstTime){
                synchronized(cacheProps){
                    if(firstTime){
                        File f=new File( configFile );
                        firstTime = false;
                        if(ss.doesFileExist(f)){
                            debugPrintln("Read properties file " + f);                                
                            cacheProps.load(ss.getFileInputStream(f));
                        }
                    }
                }
            }
            factoryClassName = cacheProps.getProperty(propertyName);            
            debugPrintln("found " + factoryClassName + " in $java.home/jaxp.properties"); 

            if (factoryClassName != null) {
                sf = createInstance(factoryClassName);
                if(sf != null){
                    return sf;
                }
            }
        } catch (Exception ex) {
            if (debug) {
                ex.printStackTrace();
            } 
        }

        /**
        // try to read from $java.home/lib/jaxp.properties
        try {
            String javah = ss.getSystemProperty( "java.home" );
            String configFile = javah + File.separator +
            "lib" + File.separator + "jaxp.properties";
            File f = new File( configFile );
            if( ss.doesFileExist(f)) {
                sf = loadFromProperty(
                        propertyName,f.getAbsolutePath(), new FileInputStream(f));
                if(sf!=null)    return sf;
            } else {
                debugPrintln("Tried to read "+ f.getAbsolutePath()+", but it doesn't exist.");
            }
        } catch(Throwable e) {
            if( debug ) {
                debugPrintln("failed to read $java.home/lib/jaxp.properties");
                e.printStackTrace();
            }
        }
         */
        
        // try META-INF/services files
        Iterator sitr = createServiceFileIterator();
        while(sitr.hasNext()) {
            URL resource = (URL)sitr.next();
            debugPrintln("looking into " + resource);
            try {
                sf = loadFromServicesFile(schemaLanguage,resource.toExternalForm(),ss.getURLInputStream(resource));
                if(sf!=null)    return sf;
            } catch(IOException e) {
                if( debug ) {
                    debugPrintln("failed to read "+resource);
                    e.printStackTrace();
                }
            }
        }
        
        // platform default
        if (schemaLanguage.equals("http://www.w3.org/2001/XMLSchema")) {
            debugPrintln("attempting to use the platform default XML Schema validator");
            return createInstance("org.apache.xerces.jaxp.validation.XMLSchemaFactory");
        }
        
        debugPrintln("all things were tried, but none was found. bailing out.");
        return null;
    }
    
    /**
     * <p>Creates an instance of the specified and returns it.</p>
     * 
     * @param className
     *      fully qualified class name to be instanciated.
     * 
     * @return null
     *      if it fails. Error messages will be printed by this method. 
     */
    private SchemaFactory createInstance( String className ) {
        try {
            debugPrintln("instanciating "+className);
            Class clazz;
            if( classLoader!=null )
                clazz = classLoader.loadClass(className);
            else
                clazz = Class.forName(className);
            if(debug)       debugPrintln("loaded it from "+which(clazz));
            Object o = clazz.newInstance();
            
            if( o instanceof SchemaFactory )
                return (SchemaFactory)o;
            
            debugPrintln(className+" is not assignable to "+SERVICE_CLASS.getName());
        } catch( Throwable t ) {
            debugPrintln("failed to instanciate "+className);
            if(debug)   t.printStackTrace();
        }
        return null;
    }
    
    /** Iterator that lazily computes one value and returns it. */
    private static abstract class SingleIterator implements Iterator {
        private boolean seen = false;
        
        public final void remove() { throw new UnsupportedOperationException(); }
        public final boolean hasNext() { return !seen; }
        public final Object next() {
            if(seen)    throw new NoSuchElementException();
            seen = true;
            return value();
        }
        
        protected abstract Object value();
    }
    
    /**
     * Looks up a value in a property file
     * while producing all sorts of debug messages.
     * 
     * @return null
     *      if there was an error.
     */
    private SchemaFactory loadFromProperty( String keyName, String resourceName, InputStream in )
        throws IOException {
        debugPrintln("Reading "+resourceName );
        
        Properties props=new Properties();
        props.load(in);
        in.close();
        String factoryClassName = props.getProperty(keyName);
        if(factoryClassName != null){
            debugPrintln("found "+keyName+" = " + factoryClassName);
            return createInstance(factoryClassName);
        } else {
            debugPrintln(keyName+" is not in the property file");
            return null;
        }
    }
    
    /**
     * Returns an {@link Iterator} that enumerates all 
     * the META-INF/services files that we care.
     */
    private Iterator createServiceFileIterator() {
        if (classLoader == null) {
            return new SingleIterator() {
                protected Object value() {
                    ClassLoader classLoader = SchemaFactoryFinder.class.getClassLoader();
                    //return (ClassLoader.getSystemResource( SERVICE_ID ));
                    return ss.getResourceAsURL(classLoader, SERVICE_ID);
                }
            };
        } else {
            try {
                //final Enumeration e = classLoader.getResources(SERVICE_ID);
                final Enumeration e = ss.getResources(classLoader, SERVICE_ID);
                if(!e.hasMoreElements()) {
                    debugPrintln("no "+SERVICE_ID+" file was found");
                }
                
                // wrap it into an Iterator.
                return new Iterator() {
                    public void remove() {
                        throw new UnsupportedOperationException();
                    }

                    public boolean hasNext() {
                        return e.hasMoreElements();
                    }

                    public Object next() {
                        return e.nextElement();
                    }
                };
            } catch (IOException e) {
                debugPrintln("failed to enumerate resources "+SERVICE_ID);
                if(debug)   e.printStackTrace();
                return new ArrayList().iterator();  // empty iterator
            }
        }
    }
    
    /** Searches for a SchemaFactory for a given schema language in a META-INF/services file. */
    private SchemaFactory loadFromServicesFile(String schemaLanguage, String resourceName, InputStream in) {

        debugPrintln("Reading "+resourceName );
        
        // Read the service provider name in UTF-8 as specified in
        // the jar spec.  Unfortunately this fails in Microsoft
        // VJ++, which does not implement the UTF-8
        // encoding. Theoretically, we should simply let it fail in
        // that case, since the JVM is obviously broken if it
        // doesn't support such a basic standard.  But since there
        // are still some users attempting to use VJ++ for
        // development, we have dropped in a fallback which makes a
        // second attempt using the platform's default encoding. In
        // VJ++ this is apparently ASCII, which is a subset of
        // UTF-8... and since the strings we'll be reading here are
        // also primarily limited to the 7-bit ASCII range (at
        // least, in English versions), this should work well
        // enough to keep us on the air until we're ready to
        // officially decommit from VJ++. [Edited comment from
        // jkesselm]
        BufferedReader rd;
        try {
            rd = new BufferedReader(new InputStreamReader(in, "UTF-8"), DEFAULT_LINE_LENGTH);
        } catch (java.io.UnsupportedEncodingException e) {
            rd = new BufferedReader(new InputStreamReader(in), DEFAULT_LINE_LENGTH);
        }
        
        String factoryClassName = null;
        SchemaFactory resultFactory = null;
        // See spec for provider-configuration files: http://java.sun.com/j2se/1.5.0/docs/guide/jar/jar.html#Provider%20Configuration%20File
        while (true) {
            try {
                factoryClassName = rd.readLine();   
            } catch (IOException x) {
                // No provider found
                break;
            }
            if (factoryClassName != null) {
                // Ignore comments in the provider-configuration file
                int hashIndex = factoryClassName.indexOf('#');
                if (hashIndex != -1) {
                    factoryClassName = factoryClassName.substring(0, hashIndex);
                }
                
                // Ignore leading and trailing whitespace
                factoryClassName = factoryClassName.trim();
                
                // If there's no text left or if this was a blank line, go to the next one.
                if (factoryClassName.length() == 0) {
                    continue;
                }
                
                try {
                    // Found the right SchemaFactory if its isSchemaLanguageSupported(schemaLanguage) method returns true.
                    SchemaFactory foundFactory = (SchemaFactory) createInstance(factoryClassName);
                    if (foundFactory.isSchemaLanguageSupported(schemaLanguage)) {
                        resultFactory = foundFactory;
                        break;
                    }
                }
                catch (Exception e) {}
            }
            else {
                break;
            }
        }
        
        try {
            // try to close the reader.
            rd.close();
        }
        // Ignore the exception.
        catch (IOException exc) {}
        
        return resultFactory;
    }
    
    private static final Class SERVICE_CLASS = SchemaFactory.class;
    private static final String SERVICE_ID = "META-INF/services/" + SERVICE_CLASS.getName();
    
    
    
    private static String which( Class clazz ) {
        return which( clazz.getName(), clazz.getClassLoader() );
    }

    /**
     * <p>Search the specified classloader for the given classname.</p>
     *
     * @param classname the fully qualified name of the class to search for
     * @param loader the classloader to search
     * 
     * @return the source location of the resource, or null if it wasn't found
     */
    private static String which(String classname, ClassLoader loader) {

        String classnameAsResource = classname.replace('.', '/') + ".class";
        
        if( loader==null )  loader = ClassLoader.getSystemClassLoader();
        
        //URL it = loader.getResource(classnameAsResource);
        URL it = ss.getResourceAsURL(loader, classnameAsResource);
        if (it != null) {
            return it.toString();
        } else {
            return null;
        }
    }
}
