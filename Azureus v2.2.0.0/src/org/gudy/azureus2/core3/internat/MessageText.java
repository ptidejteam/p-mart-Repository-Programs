/*
 * Created on 24.07.2003
 * 
 * To change the template for this generated file go to Window - Preferences -
 * Java - Code Generation - Code and Comments
 */
package org.gudy.azureus2.core3.internat;

import java.io.File;
import java.io.FilenameFilter;
import java.io.LineNumberInputStream;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.gudy.azureus2.core3.util.Debug;
import org.gudy.azureus2.core3.util.SystemProperties;
import org.gudy.azureus2.core3.util.FileUtil;

/**
 * @author Arbeiten
 * 
 * To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Generation - Code and Comments
 */
public class MessageText {

  public static final Locale LOCALE_ENGLISH = new Locale("en", "");
  public static final Locale LOCALE_DEFAULT = new Locale("", ""); // == english
  private static Locale LOCALE_CURRENT = LOCALE_DEFAULT;
  private static final String BUNDLE_NAME = "org.gudy.azureus2.internat.MessagesBundle"; //$NON-NLS-1$
  private static Map pluginLocalizationPaths = new HashMap();
  private static ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME, LOCALE_DEFAULT, MessageText.class.getClassLoader());
//  private static ResourceBundle RESOURCE_BUNDLE = new IntegratedResourceBundle(ResourceBundle.getBundle(BUNDLE_NAME, LOCALE_DEFAULT), pluginLocalizationPaths);
  private static ResourceBundle DEFAULT_BUNDLE = RESOURCE_BUNDLE;

  public static boolean keyExists(String key) {
    try {
      RESOURCE_BUNDLE.getString(key);
      return true;
    } catch (MissingResourceException e) {
      return false;
    }
  }

  /**
   * @param key
   * @return
   */
  public static String getString(String key, String sDefault) {
    try {
      return RESOURCE_BUNDLE.getString(key);
    } catch (MissingResourceException e) {
      return sDefault;
    }
  }

  public static String getString(String key) {
    // TODO Auto-generated method stub
    try {
      return RESOURCE_BUNDLE.getString(key);
    } catch (MissingResourceException e) {
      return '!' + key + '!';
    }
  }
  
  
  /**
   * Process a sequence of words, and translate the ones containing at least one '.', unless it's an ending dot.
   * @param sentence 
   * @return the formated String in the current Locale
   */
  public static String getStringForSentence(String sentence) {
    StringTokenizer st = new StringTokenizer(sentence , " ");
    StringBuffer result = new StringBuffer(sentence.length());
    String separator = "";
    while(st.hasMoreTokens())
    {
      result.append(separator);
      separator = " ";
      
      String word = st.nextToken();
      int length = word.length();
      int position = word.lastIndexOf(".");
      if(position == -1 || (position+1) == length) {
        result.append(word);
      } else {
        //We have a key :
        String translated = getString(word);
        if(translated.equals("!" + word + "!")) {
          result.append(word);
        }
        else {
          result.append(translated);
        }
      }         
    }    
    return result.toString();
  }

  /**
   * Expands a message text and replaces occurrences of %1 with first param, %2 with second...
   * @param key
   * @param params
   * @return
   */
  
  public static String 
  getString(
  		String		key,
		String[]	params )
  {
  	String	res = getString(key);
  	
  	for(int i=0;i<params.length;i++){
  		
  		String	from_str 	= "%" + (i+1);
  		String	to_str		= params[i];
  		
  		res = replaceStrings( res, from_str, to_str );
  	}
  	
  	return( res );
  }
  
  protected static String
  replaceStrings(
  	String	str,
	String	f_s,
	String	t_s )
  {
  	int	pos = 0;
  	
  	String	res  = "";
  	
  	while( pos < str.length()){
  	
  		int	p1 = str.indexOf( f_s, pos );
  		
  		if ( p1 == -1 ){
  			
  			res += str.substring(pos);
  			
  			break;
  		}
  		
  		res += str.substring(pos, p1) + t_s;
  		
  		pos = p1+f_s.length();
  	}
  	
  	return( res );
  }
  
  public static String getDefaultLocaleString(String key) {
    // TODO Auto-generated method stub
    try {
      return DEFAULT_BUNDLE.getString(key);
    } catch (MissingResourceException e) {
      return '!' + key + '!';
    }
  }

  public static Locale getCurrentLocale() {
    return LOCALE_DEFAULT.equals(LOCALE_CURRENT) ? LOCALE_ENGLISH : LOCALE_CURRENT;
  }

  public static boolean isCurrentLocale(Locale locale) {
    return LOCALE_ENGLISH.equals(locale) ? LOCALE_CURRENT.equals(LOCALE_DEFAULT) : LOCALE_CURRENT.equals(locale);
  }

  public static Locale[] getLocales() {
    String bundleFolder = BUNDLE_NAME.replace('.', '/');
    final String prefix = BUNDLE_NAME.substring(BUNDLE_NAME.lastIndexOf('.') + 1);
    final String extension = ".properties";

    String urlString = MessageText.class.getClassLoader().getResource(bundleFolder.concat(extension)).toExternalForm();
    //System.out.println("urlString: " + urlString);
    String[] bundles = null;
    
    if (urlString.startsWith("jar:file:")) {
    	
        File jar = FileUtil.getJarFileFromURL( urlString );
        
        if ( jar != null ){
        	
        	try{
		        //        System.out.println("jar: " + jar.getAbsolutePath());
		        JarFile jarFile = new JarFile(jar);
		        Enumeration entries = jarFile.entries();
		        ArrayList list = new ArrayList(250);
		        while (entries.hasMoreElements()) {
		          JarEntry jarEntry = (JarEntry) entries.nextElement();
		          if (jarEntry.getName().startsWith(bundleFolder) && jarEntry.getName().endsWith(extension) && jarEntry.getName().length() < bundleFolder.length() + extension.length() + 7) {
		            //            System.out.println("jarEntry: " + jarEntry.getName());
		            list.add(jarEntry.getName().substring(bundleFolder.length() - prefix.length()));
		            // "MessagesBundle_de_DE.properties"
		          }
		        }
		        bundles = (String[]) list.toArray(new String[list.size()]);
        	} catch (Exception e) {
        		Debug.printStackTrace( e );
        	}
        }
    } else {
      File bundleDirectory = new File(URI.create(urlString)).getParentFile();
      //      System.out.println("bundleDirectory: " +
      // bundleDirectory.getAbsolutePath());

      bundles = bundleDirectory.list(new FilenameFilter() {
        public boolean accept(File dir, String name) {
          return name.startsWith(prefix) && name.endsWith(extension);
        }
      });
    }
    
    HashSet bundleSet = new HashSet();
    
    // Add local first
    File localDir = new File(SystemProperties.getUserPath());
    String localBundles[] = localDir.list(new FilenameFilter() {
      public boolean accept(File dir, String name) {
        return name.startsWith(prefix) && name.endsWith(extension);
      }
    });
    bundleSet.addAll(Arrays.asList(localBundles));
    // Add AppDir 2nd
    File appDir = new File(SystemProperties.getApplicationPath());
    String appBundles[] = appDir.list(new FilenameFilter() {
      public boolean accept(File dir, String name) {
        return name.startsWith(prefix) && name.endsWith(extension);
      }
    });
    
    if ( appBundles != null ){
    	
    	bundleSet.addAll(Arrays.asList(appBundles));
    }
    // Any duplicates will be ignored
    bundleSet.addAll(Arrays.asList(bundles));

    List foundLocalesList = new ArrayList(bundleSet.size());
    
  	foundLocalesList.add( LOCALE_ENGLISH );

    Iterator val = bundleSet.iterator();
    int i = 0;
    while (val.hasNext()) {
      String sBundle = (String)val.next();
      
      //      System.out.println("ResourceBundle: " + bundles[i]);
      if (prefix.length() + 1 < sBundle.length() - extension.length()) {
        String locale = sBundle.substring(prefix.length() + 1, sBundle.length() - extension.length());
        //      System.out.println("Locale: " + locale);
        String[] sLocalesSplit = locale.split("_", 3);
        if (sLocalesSplit.length > 0 && sLocalesSplit[0].length() == 2) {
          if (sLocalesSplit.length == 3) {
          	foundLocalesList.add( new Locale(sLocalesSplit[0], sLocalesSplit[1], sLocalesSplit[2]));
          } else if (sLocalesSplit.length == 2 && sLocalesSplit[1].length() == 2) {
          	foundLocalesList.add( new Locale(sLocalesSplit[0], sLocalesSplit[1]));
          } else {
          	foundLocalesList.add( new Locale(sLocalesSplit[0]));
          }
        } else {
          if (sLocalesSplit.length == 3 && 
              sLocalesSplit[0].length() == 0 && 
              sLocalesSplit[2].length() > 0) {
          	foundLocalesList.add( new Locale(sLocalesSplit[0], sLocalesSplit[1], sLocalesSplit[2]));
          }
        }
       }
    }

    Locale[] foundLocales = new Locale[foundLocalesList.size()];
    
    foundLocalesList.toArray( foundLocales );

    try{
	    Arrays.sort(foundLocales, new Comparator() {
	      public final int compare (Object a, Object b) {
	        return ((Locale)a).getDisplayName((Locale)a).compareToIgnoreCase(((Locale)b).getDisplayName((Locale)b));
	      }
	    });
    }catch( Throwable e ){
    	// user has a problem whereby a null-pointer exception occurs when sorting the
    	// list - I've done some fixes to the locale list construction but am
    	// putting this in here just in case
    	Debug.printStackTrace( e );
    }
    return foundLocales;
  }

  public static boolean changeLocale(Locale newLocale) {
    return changeLocale(newLocale, false);
  }

  public static boolean changeLocale(Locale newLocale, boolean force) {
    if (!LOCALE_CURRENT.equals(newLocale) || force) {
      Locale.setDefault(LOCALE_DEFAULT);
      ResourceBundle newResourceBundle = null;
      String bundleFolder = BUNDLE_NAME.replace('.', '/');
      final String prefix = BUNDLE_NAME.substring(BUNDLE_NAME.lastIndexOf('.') + 1);
      final String extension = ".properties";

      
      
      try {
        File userBundleFile = new File(SystemProperties.getUserPath());
        File appBundleFile = new File(SystemProperties.getApplicationPath());
        
        // Get the jarURL
        // XXX Is there a better way to get the JAR name?
        ClassLoader cl = MessageText.class.getClassLoader();
        String sJar = cl.getResource(bundleFolder + extension).toString();
        sJar = sJar.substring(0, sJar.length() - prefix.length() - extension.length());
        URL jarURL = new URL(sJar);

        // User dir overrides app dir which overrides jar file bundles
        URL[] urls = {userBundleFile.toURL(), appBundleFile.toURL(), jarURL};
        
        /* This is debugging code, use it when things go wrong :) The line number
         * is approximate as the input stream is buffered by the reader...
         
        {
        	LineNumberInputStream lnis	= null;
        	
            try{
                ClassLoader fff = new URLClassLoader(urls);
                
                java.io.InputStream stream = fff.getResourceAsStream("MessagesBundle_th_TH.properties");
                
                lnis = new LineNumberInputStream( stream );
                
                new java.util.PropertyResourceBundle(lnis);
            }catch( Throwable e ){
            	
            	System.out.println( lnis.getLineNumber());
            	
            	e.printStackTrace();
            }
        }
        */
        
        newResourceBundle = ResourceBundle.getBundle("MessagesBundle", newLocale, 
                                                      new URLClassLoader(urls));
        // do more searches if getBundle failed, or if the language is not the 
        // same and the user wanted a specific country
        if (newResourceBundle == null || 
            (!newResourceBundle.getLocale().getLanguage().equals(newLocale.getLanguage()) &&
             !newResourceBundle.getLocale().getCountry().equals(""))) {
          Locale foundLocale = newResourceBundle.getLocale();
          System.out.println("changeLocale: "+ 
                             (foundLocale.toString().equals("") ? "*Default Language*" : foundLocale.getDisplayLanguage()) +
                             " != "+newLocale.getDisplayName()+". Searching without country..");
          // try it without the country
          Locale localeJustLang = new Locale(newLocale.getLanguage());
          newResourceBundle = ResourceBundle.getBundle("MessagesBundle", localeJustLang, 
                                                        new URLClassLoader(urls));
          
          if (newResourceBundle == null ||
              !newResourceBundle.getLocale().getLanguage().equals(localeJustLang.getLanguage())) {
            // find first language we have in our list
            System.out.println("changeLocale: Searching for language " + newLocale.getDisplayLanguage() + " in *any* country..");
            Locale[] locales = getLocales();
            for (int i = 0; i < locales.length; i++) {
              if (locales[i].getLanguage() == newLocale.getLanguage()) {
                newResourceBundle = ResourceBundle.getBundle("MessagesBundle", locales[i], 
                                                              new URLClassLoader(urls));
                break;
              }
            }
          }
        }
      } catch (MissingResourceException e) {
        System.out.println("changeLocale: no resource bundle for " + newLocale);
        Debug.printStackTrace( e );
        return false;
      } catch (Exception e) {
      	Debug.printStackTrace( e );
      }

      if (newResourceBundle != null) {
        //
        if (!newLocale.toString().equals("en") && 
            !newResourceBundle.getLocale().equals(newLocale))
        {
          String sNewLanguage = newResourceBundle.getLocale().getDisplayName();
          if (sNewLanguage == null || sNewLanguage.trim().equals(""))
            sNewLanguage = "English (default)";
          System.out.println("changeLocale: no message properties for Locale '"+ 
                             newLocale.getDisplayName() +
                             "' (" + newLocale + "), using '" + sNewLanguage + "'");
        }
        newLocale = newResourceBundle.getLocale();
        Locale.setDefault(newLocale);
        LOCALE_CURRENT = newLocale;
        RESOURCE_BUNDLE = new IntegratedResourceBundle(newResourceBundle, pluginLocalizationPaths);
        return true;
      } else
        return false;
    }
    return false;
  }

  // TODO: This is slow. For every call, IntegratedResourceBundle creates
  //       a hashtables and fills it with the old resourceBundle, then adds
  //       the new one, and then puts it all back into a ListResourceBundle.
  //       As we get more plugins, the time to add a new plugin's language file
  //       increases dramatically (even if the language file only has 1 entry!)
  //       Fix this by:
  //         - Create only one IntegratedResourceBundle
  //         - extending ResourceBundle
  //         - override handleGetObject, store in hashtable
  //         - function to add another ResourceBundle, adds to hashtable
  public static boolean integratePluginMessages(String localizationPath,ClassLoader classLoader) {
    boolean integratedSuccessfully = false;
    if (null != localizationPath && localizationPath.length() != 0 && !pluginLocalizationPaths.containsKey(localizationPath)) {
      pluginLocalizationPaths.put(localizationPath,classLoader);
      RESOURCE_BUNDLE = new IntegratedResourceBundle(RESOURCE_BUNDLE, pluginLocalizationPaths);
      integratedSuccessfully = true;
    }
    return integratedSuccessfully;
  }
}
