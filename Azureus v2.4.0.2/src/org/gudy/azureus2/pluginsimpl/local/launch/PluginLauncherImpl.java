/*
 * Created on 25-Jul-2005
 * Created by Paul Gardner
 * Copyright (C) 2005, 2006 Aelitis, All Rights Reserved.
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
 *
 */

package org.gudy.azureus2.pluginsimpl.local.launch;

import java.io.*;
import java.net.*;
import java.util.*;

import org.gudy.azureus2.core3.config.COConfigurationManager;
import org.gudy.azureus2.core3.util.Constants;
import org.gudy.azureus2.core3.util.SystemProperties;
import org.gudy.azureus2.plugins.LaunchablePlugin;
import org.gudy.azureus2.plugins.Plugin;
import org.gudy.azureus2.plugins.logging.LoggerChannel;
import org.gudy.azureus2.plugins.logging.LoggerChannelListener;
import org.gudy.azureus2.pluginsimpl.PluginUtils;


import com.aelitis.azureus.core.AzureusCore;
import com.aelitis.azureus.core.AzureusCoreFactory;

public class 
PluginLauncherImpl 
{
	private static Map		preloaded_plugins	= new HashMap();

	public static void
	launch(
		String[]		args )
	{
			// This *has* to be done first as it sets system properties that are read and cached by Java
  		
  		COConfigurationManager.preInitialise();
 
			// try and infer the application name. this is only required on OSX as the app name
			// is a component of the "application path" used to find plugins etc.
		
		if ( Constants.isOSX ){
			
			/* example class path
			 
			 /Applications/Utilities/Azureus.app/Contents/Resources/ 
			Java/swt.jar:/Applications/Utilities/Azureus.app/Contents/Resources/ 
			Java/swt-pi.jar:/Applications/Utilities/Azureus.app/Contents/Resources/ 
			Java/Azureus2.jar:/System/Library/Java
			*/
			
			String	classpath = System.getProperty("java.class.path");
			
			if ( classpath == null ){
				
				System.out.println( "classpath is null!!!!" );
				
			}else{
				
				int	dot_pos = classpath.indexOf( ".app/Contents" );
				
				if ( dot_pos == -1 ){
					
					// System.out.println( "can't find .app/Contents" );
					
				}else{
					
					int	start_pos = dot_pos;
					
					while( start_pos >= 0 && classpath.charAt(start_pos) != '/' ){
						
						start_pos--;
					}
					
					String	app_name = classpath.substring( start_pos+1, dot_pos );
					
					SystemProperties.setApplicationName( app_name );
				}
			}
		}
		
		final LoggerChannelListener	listener =
			new LoggerChannelListener()
			{
				public void
				messageLogged(
					int		type,
					String	content )
				{
					log(  content, false );
				}
				
				public void
				messageLogged(
					String		str,
					Throwable	error )
				{
					log(  str, true );
					
					StringWriter	sw = new StringWriter();
					
					PrintWriter		pw = new PrintWriter( sw );
					
					error.printStackTrace( pw );
					
					pw.flush();
					
					log( sw.toString(), true );
				}
				
				protected synchronized void
				log(
					String	str,
					boolean	stdout )
				{
				    File	log_file	 = getApplicationFile("launch.log");

				    PrintWriter	pw = null;
				    
				    try{
						pw = new PrintWriter(new FileWriter( log_file, true ));

						if ( str.endsWith( "\n" )){
							
							if ( stdout ){
								System.err.print( "PluginLauncher: " + str );
							}
							
							pw.print( str );
							
						}else{
							
							if ( stdout ){
								System.err.println( "PluginLauncher: " + str );
							}
							
							pw.println( str );
						}
						
				    }catch( Throwable e ){
				    	
				    }finally{
				    	
				    	if ( pw != null ){
				    		
				    		pw.close();
				    	}
				    }
				}
			};
			
		LaunchablePlugin[]	launchables = findLaunchablePlugins(listener);
		
		if ( launchables.length == 0 ){
			
			listener.messageLogged( LoggerChannel.LT_ERROR, "No launchable plugins found" );
			
			return;
			
		}else if ( launchables.length > 1 ){
			
			listener.messageLogged( LoggerChannel.LT_ERROR, "Multiple launchable plugins found, running first" );
		}
		
		try{
				// set default details for restarter
			
			SystemProperties.setApplicationEntryPoint( "org.gudy.azureus2.plugins.PluginLauncher" );

			launchables[0].setDefaults( args );			

				// see if we're a secondary instance
			
			if ( PluginSingleInstanceHandler.process( listener, args )){
				
				return;
			}
				// we have to run the core startup on a separate thread and then effectively pass "this thread"
				// through to the launchable "process" method
			
			Thread core_thread = 
				new Thread( "PluginLauncher" )
				{
					public void
					run()
					{
						try{
								// give 'process' call below some time to start up
							
							Thread.sleep(500);
							
							AzureusCore azureus_core = AzureusCoreFactory.create();

							azureus_core.start();
							
						}catch( Throwable e ){
							
							listener.messageLogged( "PluginLauncher: launch fails", e );
						}					
					}
				};
			
			core_thread.setDaemon( true );
			
			core_thread.start();
			
			boolean	restart = false;
			
			boolean	process_succeeded	= false;
			
			try{
				restart = launchables[0].process();
				
				process_succeeded	= true;
				
			}finally{
				
				try{
					if ( restart ){
						
						AzureusCoreFactory.getSingleton().restart();
	
					}else{
						
						AzureusCoreFactory.getSingleton().stop();
					}
				}catch( Throwable e ){
					
						// only report this exception if we're not already failing
					
					if ( process_succeeded ){
						
						throw( e );
					}
				}
			}
			
		}catch( Throwable e ){
			
			listener.messageLogged( "PluginLauncher: launch fails", e );
		}
	}
	
 	private static LaunchablePlugin[]
	findLaunchablePlugins(
		LoggerChannelListener	listener )
	{
				// CAREFUL - this is called BEFORE any AZ initialisation has been performed and must
				// therefore NOT use anything that relies on this (such as logging, debug....)
			
		List	res = new ArrayList();
		
	    File	app_dir	 = getApplicationFile("plugins");
	    
	    if ( !( app_dir.exists()) && app_dir.isDirectory()){
	    	
	    	listener.messageLogged( LoggerChannel.LT_ERROR, "Application dir '" + app_dir + "' not found" );
	    	
	    	return( new LaunchablePlugin[0] );
	    }
	    
	    File[] plugins = app_dir.listFiles();
	
	    if ( plugins == null || plugins.length == 0 ){
	    	
	    	listener.messageLogged( LoggerChannel.LT_ERROR, "Application dir '" + app_dir + "' empty" );
	
	    	return( new LaunchablePlugin[0] );
	    }
	    
	    for ( int i=0;i<plugins.length;i++ ) {
	        
	    	File	plugin_dir = plugins[i];
	    	
	  	    if( !plugin_dir.isDirectory()){
	  	    	
	  	    	continue;
	  	    }
	  	    
		    try{
		    	    	    
		      	ClassLoader classLoader = PluginLauncherImpl.class.getClassLoader();
		    	    		    	    
		    	File[] contents = plugin_dir.listFiles();
		    	    
	    	    if ( contents == null || contents.length == 0){
	    	    	
	    	    	continue;
	    	    }
		    	    		    	    		    	    	
		    	    // take only the highest version numbers of jars that look versioned
		    	    
	    	    String[]	plugin_version 	= {null};
	    	    String[]	plugin_id 		= {null};
		    	    
	    	    contents	= getHighestJarVersions( contents, plugin_version, plugin_id );
		    	    
	    	    for( int j = 0 ; j < contents.length ; j++){
		    	    			    	    	
		    	    classLoader = addFileToClassPath(classLoader, contents[j]);
	    	    }
		    	        		    	    
	    	    Properties props = new Properties();
	    	    
	    	    File	properties_file = new File( plugin_dir, "plugin.properties");
		    	 
	    	    	// if properties file exists on its own then override any properties file
	    	    	// potentially held within a jar	    	      	
		    	   
	  	    	if ( properties_file.exists()){
	    	      	
	  	    		FileInputStream	fis = null;
	    	      		
	  	    		try{
	  	    			fis = new FileInputStream( properties_file );
	    	      		
	  	    			props.load( fis );
	    	      			
	    	      	}finally{
	    	      			
	    	      		if ( fis != null ){
	    	      				
	    	      			fis.close();
	    	      		}
	    	      	}  		
	  	    	}else{
	    	      		
	    	    	if ( classLoader instanceof URLClassLoader ){
	    	      			
	    	    		URLClassLoader	current = (URLClassLoader)classLoader;
	    	      		    			
	    	      		URL url = current.findResource("plugin.properties");
	    	      		
	    	      		if ( url != null ){
	    	      				
	    	      			props.load(url.openStream());
	    	      		}
	    	      	}
	  	    	}
	
		    	String plugin_class = (String)props.get( "plugin.class");
		    	      	    	      		    	      
		    		// don't support multiple launchable plugins
		    	
			    if ( plugin_class == null || plugin_class.indexOf(';') != -1 ){
			    		
			    	continue;
			    }
		    	    	    		      
		    	Class c = classLoader.loadClass(plugin_class);
		    			      
		    	Plugin	    plugin	= (Plugin) c.newInstance();
		   
		    	if ( plugin instanceof LaunchablePlugin ){
		    		
		    		preloaded_plugins.put( plugin_class, plugin );
		    		
		    		res.add( plugin );
		    	}
		    }catch( Throwable e ){
		    	
		    	listener.messageLogged( "Load of plugin in '" + plugin_dir + "' fails", e );
		    }
	    }
	    
	    LaunchablePlugin[]	x = new LaunchablePlugin[res.size()];
	    
	    res.toArray( x );
	    
	    return( x );
	}
 	
 	public static Plugin
 	getPreloadedPlugin(
 		String	cla )
 	{
 		return((Plugin)preloaded_plugins.get( cla ));
 	}
 	
 	private static File 
 	getApplicationFile(
 		String filename) 
 	{      
 	    String path = SystemProperties.getApplicationPath();
 	      
 	    if (Constants.isOSX ){
 	    	
 	        path = path + "/" + SystemProperties.getApplicationName() + ".app/Contents/";
 	    }
 	      
 	    return new File(path, filename);
 	}
 	
  	public static File[]
	getHighestJarVersions(
		File[]		files,
		String[]	version_out ,
		String[]	id_out )	// currently the version of last versioned jar found...
	{
  			// WARNING!!!!
  			// don't use Debug/lglogger here as we can be called before AZ has been initialised
  		
  		List	res 		= new ArrayList();
  		Map		version_map	= new HashMap();
  		
  		for (int i=0;i<files.length;i++){
  			
  			File	f = files[i];
  			
  			String	name = f.getName().toLowerCase();
  			
  			if ( name.endsWith(".jar")){
  				
  				int cvs_pos = name.lastIndexOf("_cvs");
  				
  				int sep_pos;
  				
  				if (cvs_pos <= 0)
  					sep_pos = name.lastIndexOf("_");
  				else
  					sep_pos = name.lastIndexOf("_", cvs_pos - 1);
   				
  				if ( 	sep_pos == -1 || 
  						sep_pos == name.length()-1 ||
						!Character.isDigit(name.charAt(sep_pos+1))){
  					
  						// not a versioned jar
  					
  					res.add( f );
  					
  				}else{
  					
  					String	prefix = name.substring(0,sep_pos);
					
					String	version = name.substring(sep_pos+1, (cvs_pos <= 0) ? name.length()-4 : cvs_pos);
					
					String	prev_version = (String)version_map.get(prefix);
					
					if ( prev_version == null ){
						
						version_map.put( prefix, version );
						
					}else{
					
						if ( PluginUtils.comparePluginVersions( prev_version, version ) < 0 ){
														
							version_map.put( prefix, version );
						}							
					}
  				}
   			}
  		}
  		
  			// If any of the jars are versioned then the assumption is that all of them are
  			// For migration purposes (i.e. on the first real introduction of the update versioning
  			// system) we drop all non-versioned jars from the set
  		
  		if ( version_map.size() > 0 ){
  			
  			res.clear();
  		}
  		
			// fix a problem we had with the rating plugin. It went out as rating_x.jar when it should
			// have been azrating_x.jar. If there are any azrating entries then we remove any rating ones
			// to avoid load problems
		
  		if ( version_map.containsKey( "azrating" )){
  			 			
  			version_map.remove( "rating" );
  		}
  		
  		Iterator it = version_map.keySet().iterator();
  		
  		while(it.hasNext()){
  			
  			String	prefix 	= (String)it.next();
  			String	version	= (String)version_map.get(prefix);
  			
  			String	target = prefix + "_" + version;
  			
  			version_out[0] 	= version;
  			id_out[0]		= prefix;
  			
  			for (int i=0;i<files.length;i++){
  				
  				File	f = files[i];
  				
  				String	lc_name = f.getName().toLowerCase();
  				
  				if ( lc_name.equals( target + ".jar" ) ||
  					 lc_name.equals( target + "_cvs.jar" )){
  					  					
  					res.add( f );
  					
  					break;
  				}
  			}
  		}
  		
  		
  		
  		File[]	res_array = new File[res.size()];
  		
  		res.toArray( res_array );
  		
  		return( res_array );
  	}
  	
    public static ClassLoader 
    addFileToClassPath(
    	ClassLoader		classLoader,
    	File 			f) 
    {
      if ( 	f.exists() &&
      		(!f.isDirectory())&&
      		f.getName().endsWith(".jar")){
      
      	try {
      			
      			// URL classloader doesn't seem to delegate to parent classloader properly
      			// so if you get a chain of them then it fails to find things. Here we
      			// make sure that all of our added URLs end up within a single URLClassloader
      			// with its parent being the one that loaded this class itself
      		
      		if ( classLoader instanceof URLClassLoader ){
      			
      			URL[]	old = ((URLClassLoader)classLoader).getURLs();
    
      			URL[]	new_urls = new URL[old.length+1];
      			
      			System.arraycopy( old, 0, new_urls, 0, old.length );
      			
      			new_urls[new_urls.length-1]= f.toURL();
      			
      			classLoader = new URLClassLoader(
      								new_urls,
  									classLoader==PluginLauncherImpl.class.getClassLoader()?
  											classLoader:
  											classLoader.getParent());
      		}else{
      			  		
      			classLoader = new URLClassLoader(new URL[]{f.toURL()},classLoader);
      		}
      	}catch( Exception e){
      		
      			// don't use Debug/lglogger here as we can be called before AZ has been initialised
      		
      		e.printStackTrace();
      	}
     	}
      
      return( classLoader );
    }
}
