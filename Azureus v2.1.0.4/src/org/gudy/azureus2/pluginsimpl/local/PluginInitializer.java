/*
 * File    : PluginInitializer.java
 * Created : 2 nov. 2003 18:59:17
 * By      : Olivier 
 * 
 * Azureus - a Java Bittorrent client
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details ( see the LICENSE file ).
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
 
package org.gudy.azureus2.pluginsimpl.local;

import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;

import org.gudy.azureus2.core3.download.DownloadManager;
import org.gudy.azureus2.core3.global.GlobalManager;
import org.gudy.azureus2.core3.global.GlobalManagerListener;
import org.gudy.azureus2.core3.global.removerules.DownloadRemoveRulesPlugin;
import org.gudy.azureus2.core3.internat.MessageText;
import org.gudy.azureus2.core3.util.FileUtil;
import org.gudy.azureus2.core3.tracker.host.*;
import org.gudy.azureus2.core3.logging.LGLogger;


import org.gudy.azureus2.plugins.*;
import org.gudy.azureus2.pluginsimpl.*;
import org.gudy.azureus2.pluginsimpl.local.update.*;

import org.gudy.azureus2.core3.sharing.hoster.ShareHosterPlugin;
import org.gudy.azureus2.core3.startup.STProgressListener;
import org.gudy.azureus2.ui.tracker.TrackerDefaultWeb;
import org.gudy.azureus2.core3.internat.update.UpdateLanguagePlugin;

import org.gudy.azureus2.update.CoreUpdateChecker;
import org.gudy.azureus2.update.CorePatchChecker;
import org.gudy.azureus2.update.UpdaterUpdateChecker;

import com.aelitis.azureus.plugins.upnp.UPnPPlugin;


/**
 * @author Olivier
 * 
 */
public class 
PluginInitializer
	implements GlobalManagerListener
{

	// class name, plugin id, plugin key (key used for config props so if you change
	// it you'll need to migrate the config)
	// "id" is used when checking for updates
	
  private Object[][]	builtin_plugins = { 
   			{	 org.gudy.azureus2.core3.global.startstoprules.defaultplugin.StartStopRulesDefaultPlugin.class, "<internal>", "" },
   			{	 DownloadRemoveRulesPlugin.class, "<internal>", "" },
    		{	 ShareHosterPlugin.class, "<internal>", "ShareHoster" },
    		{    TrackerDefaultWeb.class, "<internal>", "TrackerDefault", },
    		{    UpdateLanguagePlugin.class, "<internal>", "UpdateLanguagePlugin" },
    		{	 org.gudy.azureus2.pluginsimpl.update.PluginUpdatePlugin.class, "<internal>", "PluginUpdate" },
	   		{	 CoreUpdateChecker.class, "<internal>", "CoreUpdater" },
			{	 CorePatchChecker.class, "<internal>", "CorePatcher" },
	   		{	 org.gudy.azureus2.platform.win32.PlatformManagerUpdateChecker.class, "azplatform", "azplatform" },
			{	 UPnPPlugin.class, "<internal>", "UPnP" },
        };
 
  
  	// these can be removed one day
  
  private static String[][]default_version_details =
  {
  		{ "org.gudy.azureus2.ui.webplugin.remoteui.servlet.RemoteUIServlet", 	
  				"webui", 			"Swing Web Interface",	"1.2.3" },
  		{ "org.ludo.plugins.azureus.AzureusIpFilterExporter", 					
  				"safepeer", 		"SafePeer",				"1.2.4" },
  		{ "org.gudy.azureus2.countrylocator.Plugin", 
  				"CountryLocator", 	"Country Locator",		"1.0" },
		{ "org.gudy.azureus2.ui.webplugin.remoteui.xml.server.XMLHTTPServerPlugin", 
  				"xml_http_if",		"XML over HTTP",		"1.0" },
		{ "org.cneclipse.bdcc.BDCCPlugin", 
  				"bdcc", 			"BitTorrent IRC Bot",	"2.1" },
		{ "org.cneclipse.multiport.MultiPortPlugin", 
  				"multi-ports", 		"Mutli-Port Trackers",	"1.0" },
		{ "i18nPlugin.i18nPlugin", 
  				"i18nAZ", 			"i18nAZ",				"1.0" },
		{ "info.baeker.markus.plugins.azureus.RSSImport", 
  				"RSSImport", 		"RSS Importer", 		"1.0" },
  };
  
  private static PluginInitializer	singleton;
  
  private static List		registration_queue = new ArrayList();
   
  private STProgressListener listener;
  
  private TRHost		tracker_host;
  private GlobalManager	global_manager;
  
  private PluginInterface	default_plugin;
  private PluginManager		plugin_manager;
  
  private List		plugins				= new ArrayList();
  private List		plugin_interfaces	= new ArrayList();
  
  public static synchronized PluginInitializer
  getSingleton(
  	GlobalManager 	gm,
	STProgressListener 	listener )
  {
  	if ( singleton == null ){
  		
  		singleton = new PluginInitializer( gm, listener );
  		
  		for (int i=0;i<registration_queue.size();i++){
  			
  			try{
  				Class cla = (Class)registration_queue.get(i);
  				
  				singleton.initializePluginFromClass(cla, "<internal>", cla.getName());
  				
  			}catch(PluginException e ){
  				
  			}
  		}
  		
  		registration_queue.clear();
  	}
  	
  	return( singleton );
  }
  
  protected static synchronized void
  queueRegistration(
  	Class	_class )
  {
  	if ( singleton == null ){
  		
  		registration_queue.add( _class );
 
  	}else{
  		
  		try{
  			singleton.initializePluginFromClass( _class, "<internal>", _class.getName());
  			
		}catch(PluginException e ){
  				
  		}
  	}
  }
  
  protected 
  PluginInitializer(
  	GlobalManager gm,
	STProgressListener listener) 
  {
  	global_manager	= gm;
  	
  	global_manager.addListener( this );
  	
    this.listener 	= listener;
    
    UpdateManagerImpl.getSingleton();	// initialise the update manager
    
    tracker_host	= TRHostFactory.getSingleton();
    
    plugin_manager = PluginManagerImpl.getSingleton( this );
    
    UpdaterUpdateChecker.checkPlugin();
  }
  
  public void 
  initializePlugins(
  		int		ui_type ) 
  {
    PluginManagerImpl.setStartType( ui_type );
    
  		// first do explicit plugins
  	  	
    File	user_dir = FileUtil.getUserFile("plugins");
    File	app_dir	 = FileUtil.getApplicationFile("plugins");
    
    	// user ones first so they override app ones if present
    
    initializePluginsFromDir( user_dir );
    
    if ( !user_dir.equals( app_dir )){
    	
    	initializePluginsFromDir(app_dir );
    }
 
    	// some plugins try and steal the logger stdout redirects. re-establish them if needed
    
    LGLogger.checkRedirection();
    
    	// now do built in ones
    
    LGLogger.log("Initializing built-in plugins");
    
    for (int i=0;i<builtin_plugins.length;i++){
    	
    	try{
     		initializePluginFromClass(
     				(Class)builtin_plugins[i][0], 
					(String)builtin_plugins[i][1],
					(String)builtin_plugins[i][2] );
     		
		}catch(PluginException e ){
  				
  		}
     }
  }
 
  private void
  initializePluginsFromDir(
  	File	pluginDirectory )
  {
    LGLogger.log("Plugin Directory is " + pluginDirectory);
    
    if ( !pluginDirectory.exists() ) {
      pluginDirectory.mkdirs();
    }
    
    if( pluginDirectory.isDirectory()){
    	
	    File[] pluginsDirectory = pluginDirectory.listFiles();
	    
	    for(int i = 0 ; i < pluginsDirectory.length ; i++) {
	    	
	      if(listener != null) {
          LGLogger.log("Initializing plugin " + pluginsDirectory[i].getName());

	      	
	        listener.reportCurrentTask(MessageText.getString("splash.plugin") + pluginsDirectory[i].getName());
	      }
	      
	      try{
	      	initializePluginFromDir(pluginsDirectory[i]);
	      	
	      }catch( PluginException e ){
	      	
	      }
	      
	      if(listener != null) {
	        listener.reportPercent( 100 * i / pluginsDirectory.length);
	      }
	    }
    } 
  }
  
  private void 
  initializePluginFromDir(
  	File directory)
  
  	throws PluginException
  {
  	
  	ClassLoader classLoader = getClass().getClassLoader();
  	
    if(!directory.isDirectory()) return;
    String pluginName = directory.getName();
    File[] pluginContents = directory.listFiles();
    if (pluginContents.length <= 0) return;
    
    	// take only the highest version numbers of jars that look versioned
    
    String[]	plugin_version = {null};
    String[]	plugin_id = {null};
    
    pluginContents	= getHighestJarVersions( pluginContents, plugin_version, plugin_id );
    
    for( int i = 0 ; i < pluginContents.length ; i++){
    	
    	File	jar_file = pluginContents[i];
    	
    		// migration hack for i18nAZ_1.0.jar
    	
    	if ( pluginContents.length > 1 ){
    		
    		String	name = jar_file.getName();
    		
    		if ( name.startsWith( "i18nPlugin_" )){
    			
    				// non-versioned version still there, rename it
    			
    			LGLogger.log( "renaming '" + name + "' to conform with versioning system" );
    			
    			jar_file.renameTo( new File( jar_file.getParent(), "i18nAZ_0.1.jar  " ));
    			
    			continue;
    		}
    	}
    	
        classLoader = addFileToClassPath(classLoader, jar_file);
    }
        
    String plugin_class_string = null;
    
    try {
      PluginException	last_load_failure	= null;
    	
      Properties props = new Properties();
      
      File	properties_file = new File(directory.toString() + File.separator + "plugin.properties");
 
      try {
      	
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
      				
      			}else{
      				
      				throw( new Exception( "failed to load plugin.properties from jars"));
      			}
      		}else{
      			
 				throw( new Exception( "failed to load plugin.properties from dir or jars"));
 				      			
      		}
      	}
      }catch( Throwable e ){
      	
      	e.printStackTrace();
      	String	msg =  "Can't read 'plugin.properties' for plugin '" + pluginName + "': file may be missing";
      	
      	LGLogger.logAlert( LGLogger.AT_ERROR, msg );
      	  
        System.out.println( msg );
        
        throw( new PluginException( msg, e ));
      }

      plugin_class_string = (String)props.get( "plugin.class");
      
      if ( plugin_class_string == null ){
      	
      	plugin_class_string = (String)props.get( "plugin.classes");
      }
      
      String	plugin_name_string = (String)props.get( "plugin.name");
      
      if ( plugin_name_string == null ){
      	
      	plugin_name_string = (String)props.get( "plugin.names");
      }
 
      int	pos1 = 0;
      int	pos2 = 0;
      
      while(true){
      		int	p1 = plugin_class_string.indexOf( ";", pos1 );
      	
      		String	plugin_class;
      	
      		if ( p1 == -1 ){
      			plugin_class = plugin_class_string.substring(pos1).trim();
      		}else{
      			plugin_class	= plugin_class_string.substring(pos1,p1).trim();
      			pos1 = p1+1;
      		}
      
      		PluginInterfaceImpl existing_pi = getPluginFromClass( plugin_class );
      		
      		if ( existing_pi != null ){
      				
      				// allow user dir entries to override app dir entries without warning
      			
      			File	this_parent 	= directory.getParentFile();
      			File	existing_parent = null;
      			
      			if ( existing_pi.getInitializerKey() instanceof File ){
      				
      				existing_parent	= ((File)existing_pi.getInitializerKey()).getParentFile();
      			}
      			
      			if ( 	this_parent.equals( FileUtil.getApplicationFile("plugins")) &&
      					existing_parent	!= null &&
      					existing_parent.equals( FileUtil.getUserFile( "plugins" ))){
      				
      			}else{
      			
      				LGLogger.logAlert( LGLogger.AT_WARNING, "plugin class '" + plugin_class + "' is already loaded" );
      			}

      		}else{
      			
      		  String	plugin_name = null;
      		  
      		  if ( plugin_name_string != null ){
      		  	
      		  	int	p2 = plugin_name_string.indexOf( ";", pos2 );
              	
          	
          		if ( p2 == -1 ){
          			plugin_name = plugin_name_string.substring(pos2).trim();
          		}else{
          			plugin_name	= plugin_name_string.substring(pos2,p2).trim();
          			pos2 = p2+1;
          		}    
      		  }
      		  
      		  Properties new_props = (Properties)props.clone();
      		  
      		  for (int j=0;j<default_version_details.length;j++){
      		  	
      		  	if ( plugin_class.equals( default_version_details[j][0] )){
      		  
    		  		if ( new_props.get( "plugin.id") == null ){
      		  			
    		  			new_props.put( "plugin.id", default_version_details[j][1]);  
      		  		}
    		  		
    		  		if ( plugin_name == null ){
    		  			
    		  			plugin_name	= default_version_details[j][2];
    		  		}
    		  		
    		  		if ( new_props.get( "plugin.version") == null ){
	
    		  				// no explicit version. If we've derived one then use that, otherwise defaults
     		  			
    		  			if ( plugin_version[0] != null ){
    		  
    		  				new_props.put( "plugin.version", plugin_version[0]);
    		     		    		  				
    		  			}else{
    		  				
    		  				new_props.put( "plugin.version", default_version_details[j][3]);
    		  			}
      		  		}
      		  	}
      		  }
      		  
      		  new_props.put( "plugin.class", plugin_class );
      		  
      		  if ( plugin_name != null ){
      		  	
      		  	new_props.put( "plugin.name", plugin_name );
      		  }
      		       		       		  
	 	      // System.out.println( "loading plugin '" + plugin_class + "' using cl " + classLoader);
		      
		      	// if the plugin load fails we still need to generate a plugin entry
      		  	// as this drives the upgrade process
      		  
		      Plugin plugin = null;
		      
		      Throwable	load_failure	= null;
		      
		      try{
			      Class c = classLoader.loadClass(plugin_class);
			      
		      	  plugin	= (Plugin) c.newInstance();
		      
		      }catch( Throwable e ){
		      	
		      	load_failure	= e;
		      	
		      	plugin = new loadFailedPlugin();
		      }

		      MessageText.integratePluginMessages((String)props.get("plugin.langfile"),classLoader);
		      
		      PluginInterfaceImpl plugin_interface = 
		      		new PluginInterfaceImpl(
		      					plugin, 
								this, 
								directory, 
								classLoader,
								directory.getName(),	// key for config values
								new_props,
								directory.getAbsolutePath(),
								plugin_id[0],
								plugin_version[0] );
		      
		      try{
		      	
		      	plugin.initialize(plugin_interface);
		      	
		      }catch( Throwable e ){
		      	
		      	load_failure	= e;
		      }
		
		      plugin_interface.setOperational( load_failure == null );
		      
		      plugins.add( plugin );
		      
		      plugin_interfaces.add( plugin_interface );
		      
		      if ( load_failure != null ){
		      	
		      	load_failure.printStackTrace();
		        
		      	String	msg = "Error loading plugin '" + pluginName + "' / '" + plugin_class_string + "'";
		   	 
		      	LGLogger.logAlert( msg, load_failure );

		      	System.out.println( msg + " : " + load_failure);
		      	
		      	last_load_failure = new PluginException( msg, load_failure );
		      }
      		}
	      
	      if ( p1 == -1 ){
	      	break;
	      	
	      }
      }
      
      if ( last_load_failure != null ){
      	
      	throw( last_load_failure );
      }
    } catch(Throwable e) {
    	
    	if ( e instanceof PluginException ){
    		
    		throw((PluginException)e);
    	}
   
    	e.printStackTrace();
      
    	String	msg = "Error loading plugin '" + pluginName + "' / '" + plugin_class_string + "'";
 	 
    	LGLogger.logAlert( msg, e );

    	System.out.println( msg + " : " + e);
    	
    	throw( new PluginException( msg, e ));
    }
  }
  
  private ClassLoader 
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
									classLoader==getClass().getClassLoader()?
											classLoader:
											classLoader.getParent());
    		}else{
    			  		
    			classLoader = new URLClassLoader(new URL[]{f.toURL()},classLoader);
    		}
    	}catch(Exception e){
    		
    		e.printStackTrace();
    	}
   	}
    
    return( classLoader );
  }
  
  protected void 
  initializePluginFromClass(
  	Class 	plugin_class,
	String	plugin_id,
	String	plugin_config_key )
  
  	throws PluginException
  {
  
  	if ( getPluginFromClass( plugin_class ) != null ){
  	
  		LGLogger.logAlert( LGLogger.AT_WARNING, "plugin class '" + plugin_class.getName() + "' is already loaded" );
  		
  		return;
  	}
  	
  	try{
  		Plugin plugin = (Plugin) plugin_class.newInstance();
  		
  		PluginInterfaceImpl plugin_interface = 
  			new PluginInterfaceImpl(
  						plugin, 
						this,
						plugin_class,
						plugin_class.getClassLoader(),
						plugin_config_key,
						new Properties(),
						"",
						plugin_id,
						null );
  		
  		plugin.initialize(plugin_interface);
  		
   		plugins.add( plugin );
   		
   		plugin_interfaces.add( plugin_interface );
   		
  	}catch(Throwable e){
  		
  		e.printStackTrace();
  		
  		String	msg = "Error loading internal plugin '" + plugin_class.getName() + "'";
  		
    	LGLogger.logAlert( msg, e );

  		System.out.println(msg + " : " + e);
  		
  		throw( new PluginException( msg, e ));
  	}
  }
  
  protected void
  unloadPlugin(
  	PluginInterfaceImpl		pi )
  {
  	plugins.remove( pi.getPlugin());
  	
  	plugin_interfaces.remove( pi );
  }
  
  protected void
  reloadPlugin(
  	PluginInterfaceImpl		pi )
  
  	throws PluginException
  {
  	unloadPlugin( pi );
  	
  	Object key 			= pi.getInitializerKey();
  	String config_key	= pi.getPluginConfigKey();
  	
  	if ( key instanceof File ){
  		
  		initializePluginFromDir( (File)key );
  		
  	}else{
  		
  		initializePluginFromClass( (Class) key, pi.getPluginID(), config_key );
  	}
  }
 	
  protected TRHost
  getTrackerHost()
  {
  	return( tracker_host );
  }
  
  protected GlobalManager
  getGlobalManager()
  {
  	return( global_manager );
  }
  
  public static PluginInterface
  getDefaultInterface()
  {
  	return( singleton.getDefaultInterfaceSupport());
  }
  
  protected PluginInterface
  getDefaultInterfaceSupport()
  {
  
  	if ( default_plugin == null ){
  		
  		default_plugin = 
  			new PluginInterfaceImpl(
  					null,
					this,
					getClass(),
					getClass().getClassLoader(),
					"default",
					new Properties(),
					null,
					"<internal>",
					null );
  	}
  	
  	return( default_plugin );
  }
  
  public void
  downloadManagerAdded(
  	DownloadManager	dm )
  {
  }
  
  public void
  downloadManagerRemoved(
  	DownloadManager	dm )
  {
  }
  
  public void
  destroyInitiated()
  {	
  	for (int i=0;i<plugin_interfaces.size();i++){
  		
  		((PluginInterfaceImpl)plugin_interfaces.get(i)).closedownInitiated();
  	} 
  }
  
  public void
  destroyed()
  {
  	for (int i=0;i<plugin_interfaces.size();i++){
  		
  		((PluginInterfaceImpl)plugin_interfaces.get(i)).closedownComplete();
  	}  	
  }
  
  protected void
  fireEventSupport(
  	final int	type )
  {
  	PluginEvent	ev = new PluginEvent(){ public int getType(){ return( type );}};
  	
  	for (int i=0;i<plugin_interfaces.size();i++){
  		
  		((PluginInterfaceImpl)plugin_interfaces.get(i)).fireEvent(ev);
  	}  	
  }
  
  public static void
  fireEvent(
  	int		type )
  {
  	singleton.fireEventSupport(type);
  }
  
  protected void
  initialisationCompleteSupport()
  {
  	for (int i=0;i<plugin_interfaces.size();i++){
  		
  		((PluginInterfaceImpl)plugin_interfaces.get(i)).initialisationComplete();
  	}
  }
  
  
  public static void
  initialisationComplete()
  {
  	singleton.initialisationCompleteSupport();
  }
  
  public static List getPluginInterfaces() {
  	return singleton.getPluginInterfacesSupport();
  }

  protected List getPluginInterfacesSupport() {
  	return plugin_interfaces;
  }
  
  protected PluginInterface[]
  getPlugins()
  {
  	List	pis = getPluginInterfacesSupport();
  	
  	PluginInterface[]	res = new 	PluginInterface[pis.size()];
  	
  	pis.toArray(res);
  	
  	return( res );
  }
  
  protected PluginManager
  getPluginManager()
  {
  	return( plugin_manager );
  }
  
  protected PluginInterfaceImpl
  getPluginFromClass(
  	Class	cla )
  {
  	return( getPluginFromClass( cla.getName()));
  }
  
  protected PluginInterfaceImpl
  getPluginFromClass(
  	String	class_name )
  {  	
  	for (int i=0;i<plugin_interfaces.size();i++){
  		
  		PluginInterfaceImpl	pi = (PluginInterfaceImpl)plugin_interfaces.get(i);
  		
  		if ( pi.getPlugin().getClass().getName().equals( class_name )){
  		
  			return( pi );
  		}
  	}
  	
  	return( null );
  }
  
  	protected File[]
	getHighestJarVersions(
		File[]		files,
		String[]	version_out ,
		String[]	id_out )	// currently the version of last versioned jar found...
	{
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
  		
  		Iterator	it = version_map.keySet().iterator();
  		
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
  	
  	protected class
	loadFailedPlugin
		implements UnloadablePlugin
	{
  		 public void 
		  initialize(
		  	PluginInterface pluginInterface )
		  
		  	throws PluginException
		{
  		 	
  		}
  		 
  		public void
		unload()
		{
  		}
  	}
}
