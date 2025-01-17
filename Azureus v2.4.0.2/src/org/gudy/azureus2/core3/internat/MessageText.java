/*
 * Created on 24.07.2003
 * Copyright (C) 2003, 2004, 2005, 2006 Aelitis, All Rights Reserved.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *
 * AELITIS, SAS au capital de 46,603.30 euros
 * 8 Allee Lenotre, La Grille Royale, 78600 Le Mesnil le Roi, France.
 */
package org.gudy.azureus2.core3.internat;

import org.gudy.azureus2.core3.logging.*;
import org.gudy.azureus2.core3.util.Debug;
import org.gudy.azureus2.core3.util.FileUtil;
import org.gudy.azureus2.core3.util.SystemProperties;
import org.gudy.azureus2.core3.util.Constants;

import java.io.File;
import java.io.FilenameFilter;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * @author Arbeiten
 * 
 * @author CrazyAlchemist Added keyExistsForDefaultLocale
 */
public class MessageText {

  public static final Locale LOCALE_ENGLISH = new Locale("en", "");
  public static final Locale LOCALE_DEFAULT = new Locale("", ""); // == english
  private static Locale LOCALE_CURRENT = LOCALE_DEFAULT;
  private static final String BUNDLE_NAME = "org.gudy.azureus2.internat.MessagesBundle"; //$NON-NLS-1$
  private static Map pluginLocalizationPaths = new HashMap();
  private static ResourceBundle RESOURCE_BUNDLE;
  private static Set			platform_specific_keys	= new HashSet();

  private static int bundle_fail_count	= 0;
  
  static{
	  setResourceBundle( getResourceBundle(BUNDLE_NAME, LOCALE_DEFAULT, MessageText.class.getClassLoader()));
  }
  
  static ResourceBundle
  getResourceBundle(
	String		name,
	Locale		loc,
	ClassLoader	cl )
  {
	  try{
		  return( ResourceBundle.getBundle(name, loc, cl ));
		  
	  }catch( Throwable e ){
		  
		  bundle_fail_count++;
			  
		  if ( bundle_fail_count == 1 ){
			  
			  e.printStackTrace();
			  
			  Logger.log(new LogAlert(LogAlert.REPEATABLE, LogAlert.AT_ERROR,
						"Failed to load resource bundle. One possible cause is "
								+ "that you have installed Azureus into a directory "
								+ "with a '!' in it. If so, please remove the '!'."));
		  }
		  
		  return(
			  new ResourceBundle()
			  {
				  public Locale
				  getLocale()
				  {
					return( LOCALE_DEFAULT );
				  }

				  protected Object 
				  handleGetObject(String key)
				  {
						return( null );
				  }
	
				  public Enumeration
				  getKeys()
				  {
					return( new Vector().elements());
				  }
			  });
	  } 
  }
  
  private static ResourceBundle DEFAULT_BUNDLE = RESOURCE_BUNDLE;
  
  private static void
  setResourceBundle(
	  ResourceBundle	bundle )
  {
	  RESOURCE_BUNDLE	= bundle;
	  
	  Enumeration	keys = RESOURCE_BUNDLE.getKeys();
	  
	  String	platform_suffix = getPlatformSuffix();
	  
	  platform_specific_keys.clear();
	  
	  while( keys.hasMoreElements()){
		  
		  String	key = (String)keys.nextElement();
		  
		  if ( key.endsWith( platform_suffix )){
			  			  
			  platform_specific_keys.add( key );
		  }
	  }
  }
  
 
  public static boolean keyExists(String key) {
    try {
      RESOURCE_BUNDLE.getString(key);
      return true;
    } catch (MissingResourceException e) {
      return false;
    }
  }

    public static boolean keyExistsForDefaultLocale(final String key) {
        try {
            DEFAULT_BUNDLE.getString(key);
            return true;
        } catch (MissingResourceException e) {
            return false;
        }
    }


  /**
   * @param key
   * @return
   */
  public static String 
  getString(
	String key, 
	String sDefault) 
  {
  	if (key == null)
  		return "";

	  String	target_key = key + getPlatformSuffix();
	  
	  if ( !platform_specific_keys.contains( target_key )){
		  
		  target_key	= key;
	  }
    
	  try {
      
		  return RESOURCE_BUNDLE.getString( target_key );
    
	  }catch (MissingResourceException e) {
		  
		  return getPlatformNeutralString(key, sDefault);
	  }
  }

  public static String 
  getString(
	String key) 
  {
  	if (key == null)
  		return "";

	  String	target_key = key + getPlatformSuffix();
	  
	  if ( !platform_specific_keys.contains( target_key )){
		  
		  target_key	= key;
	  }

	  try {
	 
		  return RESOURCE_BUNDLE.getString( target_key );
		  
	  } catch (MissingResourceException e) {
		  
	      return getPlatformNeutralString(key);
	  }
  }

  public static String getPlatformNeutralString(String key) {
    try {
      return RESOURCE_BUNDLE.getString(key);
    } catch (MissingResourceException e) {
      return '!' + key + '!';
    }
  }

  public static String getPlatformNeutralString(String key, String sDefault) {
    try {
      return RESOURCE_BUNDLE.getString(key);
    } catch (MissingResourceException e) {
      return sDefault;
    }
  }

  /**
   * Gets the localization key suffix for the running platform
   * @return The suffix
   * @see Constants
   */
  private static String getPlatformSuffix() {
    if(Constants.isOSX)
        return "._mac";
    else if(Constants.isLinux)
        return "._linux";
    else if(Constants.isSolaris)
        return "._solaris";
     else if(Constants.isWindows)
       return "._windows";
     else
       return "._unknown";
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

			File jar = FileUtil.getJarFileFromURL(urlString);

			if (jar != null) {
				try {
					// System.out.println("jar: " + jar.getAbsolutePath());
					JarFile jarFile = new JarFile(jar);
					Enumeration entries = jarFile.entries();
					ArrayList list = new ArrayList(250);
					while (entries.hasMoreElements()) {
						JarEntry jarEntry = (JarEntry) entries.nextElement();
						if (jarEntry.getName().startsWith(bundleFolder)
								&& jarEntry.getName().endsWith(extension)) {
							// System.out.println("jarEntry: " + jarEntry.getName());
							list.add(jarEntry.getName().substring(
									bundleFolder.length() - prefix.length()));
							// "MessagesBundle_de_DE.properties"
						}
					}
					bundles = (String[]) list.toArray(new String[list.size()]);
				} catch (Exception e) {
					Debug.printStackTrace(e);
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
    
    	// can be null if user path is borked
    
    if ( localBundles != null ){
    	
    	bundleSet.addAll(Arrays.asList(localBundles));
    }
    
    // Add AppDir 2nd
    File appDir = new File(SystemProperties.getApplicationPath());
    String appBundles[] = appDir.list(new FilenameFilter() {
      public boolean accept(File dir, String name) {
        return name.startsWith(prefix) && name.endsWith(extension);
      }
    });
    
    	// can be null if app path is borked
    
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
      
      // System.out.println("ResourceBundle: " + bundles[i]);
      if (prefix.length() + 1 < sBundle.length() - extension.length()) {
        String locale = sBundle.substring(prefix.length() + 1, sBundle.length() - extension.length());
        //System.out.println("Locale: " + locale);
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

  private static boolean changeLocale(Locale newLocale, boolean force) {
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
        
        newResourceBundle = getResourceBundle("MessagesBundle", newLocale, 
                                                      new URLClassLoader(urls));
        // do more searches if getBundle failed, or if the language is not the 
        // same and the user wanted a specific country
        if (newResourceBundle == null || 
            (!newResourceBundle.getLocale().getLanguage().equals(newLocale.getLanguage()) &&
             !newLocale.getCountry().equals(""))) {
          Locale foundLocale = newResourceBundle.getLocale();
          System.out.println("changeLocale: "+ 
                             (foundLocale.toString().equals("") ? "*Default Language*" : foundLocale.getDisplayLanguage()) +
                             " != "+newLocale.getDisplayName()+". Searching without country..");
          // try it without the country
          Locale localeJustLang = new Locale(newLocale.getLanguage());
          newResourceBundle = getResourceBundle("MessagesBundle", localeJustLang, 
                                                        new URLClassLoader(urls));
          
          if (newResourceBundle == null ||
              !newResourceBundle.getLocale().getLanguage().equals(localeJustLang.getLanguage())) {
            // find first language we have in our list
            System.out.println("changeLocale: Searching for language " + newLocale.getDisplayLanguage() + " in *any* country..");
            Locale[] locales = getLocales();
            for (int i = 0; i < locales.length; i++) {
              if (locales[i].getLanguage() == newLocale.getLanguage()) {
                newResourceBundle = getResourceBundle("MessagesBundle", locales[i], 
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
		setResourceBundle( new IntegratedResourceBundle(newResourceBundle, pluginLocalizationPaths));
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
	  setResourceBundle( new IntegratedResourceBundle(RESOURCE_BUNDLE, pluginLocalizationPaths));
      integratedSuccessfully = true;
    }
    return integratedSuccessfully;
  }
}
