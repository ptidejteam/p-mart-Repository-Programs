/*
 * File    : PluginInterface.java
 * Created : 2 nov. 2003 18:48:47
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
 
package org.gudy.azureus2.plugins;

import java.util.Properties;

import org.gudy.azureus2.plugins.sharing.ShareManager;
import org.gudy.azureus2.plugins.sharing.ShareException;
import org.gudy.azureus2.plugins.tracker.Tracker;
import org.gudy.azureus2.plugins.utils.Utilities;
import org.gudy.azureus2.plugins.logging.Logger;
import org.gudy.azureus2.plugins.messaging.MessageManager;
import org.gudy.azureus2.plugins.network.ConnectionManager;
import org.gudy.azureus2.plugins.ipfilter.IPFilter;
import org.gudy.azureus2.plugins.ddb.DistributedDatabase;
import org.gudy.azureus2.plugins.download.DownloadManager;
import org.gudy.azureus2.plugins.torrent.TorrentManager;
import org.gudy.azureus2.plugins.peers.protocol.PeerProtocolManager;
import org.gudy.azureus2.plugins.ui.*;
import org.gudy.azureus2.plugins.ui.config.Parameter;
import org.gudy.azureus2.plugins.ui.config.ConfigSection;
import org.gudy.azureus2.plugins.ui.config.PluginConfigUIFactory;
import org.gudy.azureus2.plugins.ui.tables.peers.PluginPeerItemFactory;
import org.gudy.azureus2.plugins.ui.tables.mytorrents.PluginMyTorrentsItemFactory;
import org.gudy.azureus2.plugins.update.UpdateManager;
import org.gudy.azureus2.plugins.utils.ShortCuts;
import org.gudy.azureus2.plugins.clientid.*;


/**
 * Defines the communication interface between Azureus and Plugins
 * @author Olivier
 */
public interface PluginInterface {  
	
	/** Retrieve the Application's name
   *
   * @return the Application's name
   *
   * @since 2.1.0.0
   */
	public String
	getAzureusName();
	
	/** Retrieve the Application's version as a string.
	 *
	 * @return Application's version.  Typically in the following formats (regexp):<br>
	 *         [0-9]+\.[0-9]+\.[0-9]+\.[0-9]+<br>
	 *         [0-9]+\.[0-9]+\.[0-9]+\.[0-9]+_CVS<br>
	 *         [0-9]+\.[0-9]+\.[0-9]+\.[0-9]+_B[0-9]+
   *
   * @since 2.1.0.0
	 */
	public String
	getAzureusVersion();
	
  /**
   * A Plugin might call this method to add a View to Azureus's views
   * The View will be accessible from View > Plugins > View name
   * @param view The PluginView to be added
   *
   * @since 2.0.4.0
   *
   * @deprecated use use {@link org.gudy.azureus2.plugins.ui.SWT.SWTManager#addView}
   */
  public void addView(PluginView view);

  /**
   * adds a tab under the 'plugins' tab in the config view.<br>
   * Use {@link #getPluginConfigUIFactory()} to get the 
   * {@link PluginConfigUIFactory} class, from which you can create different
   * types of parameters.
   *
   * @param parameters the Parameter(s) to be edited
   * @param displayName the under which it should display.<br>
   * Azureus will look-up for ConfigView.section.plugins.<i>displayName</i>; into the lang files
   * in order to find the localized displayName. (see i18n)
   *
   * @since 2.0.6.0
   */
  public void addConfigUIParameters(Parameter[] parameters, String displayName);
  
  /** (DEPRECATED) Adds a column to the peers table.
   *
   * @param columnName the key name of the column
   * @param factory the factory responsible of creating items.
   * Azureus will look-up for PeersView.<i>columnName</i> into the lang files
   * in order to find the localized displayName. (see i18n)
   *
   * @since 2.0.6.0
   *
   * @deprecated use {@link org.gudy.azureus2.plugins.ui.tables.TableManager}
   */
  public void addColumnToPeersTable(String columnName,PluginPeerItemFactory factory);
  
  /** (DEPRECATED) Adds a column to the My Torrents table.
   *
   * @param columnName the key name of the column
   * @param factory the factory responsible of creating items.
   * Azureus will look-up for MyTorrentsView.<i>columnName</i> into the lang files
   * in order to find the localized displayName. (see i18n)
   *
   * @since 2.0.8.0
   *
   * @deprecated use {@link org.gudy.azureus2.plugins.ui.tables.TableManager}
   */
  public void addColumnToMyTorrentsTable(String columnName, PluginMyTorrentsItemFactory factory);

  /**
   * adds a ConfigSection to the config view.<p>
   * In contrast to addConfigUIParameters, this gives you total control over
   * a tab.  Please be kind and use localizable text.<BR>
   *
   * @param section ConfigSection to be added to the Config view
   *
   * @since 2.0.8.0
   */
	public void addConfigSection(ConfigSection section);

  /**
   * Gives access to the tracker functionality
   * @return The tracker
   *
   * @since 2.0.6.0
   */
  public Tracker getTracker();
  
  /**
   * Gives access to the logger
   * @return The logger
   *
   * @since 2.0.7.0
   */
  public Logger getLogger();
  
  /**
   * Gives access to the IP filter
   * @return An object that allows access to IP Filtering
   *
   * @since 2.0.8.0
   */
  public IPFilter
  getIPFilter();
  
  /**
   * Gives access to the download manager
   * @return An object that allows management of downloads
   *
   * @since 2.0.7.0
   */
  public DownloadManager
  getDownloadManager();
  
  /**
   * Gives access to the peer protocol manager
   * @return An object that allows management of Peer Protocols
   *
   * @since 2.0.7.0
   */
  public PeerProtocolManager
  getPeerProtocolManager();
  
  /**
   * Gives access to the sharing functionality
   * @return
   *
   * @since 2.0.7.0
   */
  public ShareManager
  getShareManager()
  
  	throws ShareException;
  
  /**
   * Gives access to the torrent manager
   * @return An object to manage torrents
   *
   * @since 2.0.8.0
   */
  public TorrentManager
  getTorrentManager();
  
  /**
   * access to various utility functions
   * @return
   *
   * @since 2.1.0.0
   */
  public Utilities
  getUtilities();
  
  /**
   * access to a set of convenience routines for doing things in a quicker, although less
   * structured, fashion
   * @return
   *
   * @since 2.1.0.0
   */
  public ShortCuts
  getShortCuts();
  
  /**
   * access to UI extension features 
   * @return
   *
   * @since 2.1.0.0
   */
  public UIManager
  getUIManager();
  
  /**
   * access to the update manager used to update plugins. required for non-Azureus SF hosted
   * plugins (SF ones are managed automatically)
   * @return
   *
   * @since 2.1.0.0
   */
  public UpdateManager
  getUpdateManager();
  
  /**
   * opens a torrent file given its name
   * @param fileName The Name of the file that azureus must open
   *
   * @since 2.0.4.0
   *
   * @deprecated Use {@link DownloadManager#addDownload}
   */
  public void openTorrentFile(String fileName);
  
  /**
   * opens a torrent file given the url it's at
   * @param url The String representation of the url pointing to a torrent file
   *
   * @since 2.0.4.0
   *
   * @deprecated Use {@link DownloadManager#addDownload}
   */
  public void openTorrentURL(String url);
  
  /**
   * gives access to the plugin properties
   * @return the properties from the file plugin.properties
   *
   * @since 2.0.4.0
   */
  public Properties getPluginProperties();
  
  /**
   * gives access to the plugin installation path
   * @return the full path the plugin is installed in
   *
   * @since 2.0.4.0
   */
  public String getPluginDirectoryName();
  
  /**
   * Returns the value of plugin.name if it exists in the properties file, dirctory name otherwise
   * @return
   *
   * @since 2.1.0.0
   */
  public String getPluginName();
  
  /**
   * Returns the version number of the plugin it if can be deduced from either the name of
   * the jar file it is loaded from or the properties file. null otherwise
   *
   * @return Version number as a string, or null
   *
   * @since 2.1.0.0
   */
  public String
  getPluginVersion();
  
  /**
   * Returns an identifier used to identify this particular plugin 
   * @return
   *
   * @since 2.1.0.0
   */
  public String
  getPluginID();
  
  	/**
  	 * Whether or not this is a mandatory plugin. Mandatory plugins take priority over update checks, for example,
  	 * over optional ones.
  	 */
  
  public boolean
  isMandatory();
  
  	/**
  	 * Built-in plugins are those used internally by Azureus, for example the UPnP plugin
  	 * @return
  	 */
  
  public boolean
  isBuiltIn();
  
  /**
   * gives access to the plugin config interface
   * @return the PluginConfig object associated with this plugin
   */
  public PluginConfig getPluginconfig();
  
  
  /**
   * gives acess to the plugin Config UI Factory
   * @return the PluginConfigUIFactory associated with this plugin
   */
  public PluginConfigUIFactory getPluginConfigUIFactory();
  
  /**
   * gives access to the ClassLoader used to load the plugin
   * @return
   *
   * @since 2.0.8.0
   */
  public ClassLoader
  getPluginClassLoader();
  
  /**
   * Gives access to the plugin itself
   * @return
   *
   * @since 2.1.0.0
   */
  public Plugin
  getPlugin();
  
  /**
   * If a plugin fails to load properly (i.e. the construction of the plugin object
   * fails) it is marked as non-operational (rather than not being present at all) 
   * @return whether or not the plugin is operational or not
   *
   * @since 2.1.0.0
   */
  public boolean
  isOperational();

  /**
   *
   * @since 2.1.0.0
   */
  public boolean
  isUnloadable();

  /**
   *
   * @since 2.1.0.0
   */  
  public void
  unload()
  
  	throws PluginException;

  /**
   *
   * @since 2.1.0.0
   */
  public void
  reload()
  
  	throws PluginException;
  
  	/**
  	 * Uninstall this plugin if it has been loaded from a plugin directory. Deletes the
  	 * plugin directory 
  	 * @throws PluginException
  	 */
  
  public void
  uninstall()
  
  	throws PluginException;
  
  /**
   * gives access to the plugin manager
   * @return
   *
   * @since 2.1.0.0
   */
  public PluginManager
  getPluginManager();
  
  	/**
  	 * 
  	 * @return
  	 * @since 2.2.0.3
  	 */
  
  public ClientIDManager
  getClientIDManager();
  
  
  /**
   * Get the connection manager.
   * @since 2.2.0.3
   * @return manager
   */
  public ConnectionManager getConnectionManager(); 
  
  
  /**
   * Get the peer messaging manager.
   * @since 2.2.0.3
   * @return manager
   */
  public MessageManager getMessageManager();
  
  
  /**
   * Get th edistributed database
   * @since 2.2.0.3
   * @return
   */
  public DistributedDatabase
  getDistributedDatabase();
  
  /**
   *
   * @since 2.0.7.0
   */
  public void
  addListener(
  	PluginListener	l );
  
  /**
   *
   * @since 2.0.7.0
   */
  public void
  removeListener(
  	PluginListener	l );
  
  /**
   *
   * @since 2.0.8.0
   */
  public void
  addEventListener(
  	PluginEventListener	l );
  
  /**
   *
   * @since 2.0.8.0
   */
  public void
  removeEventListener(
  	PluginEventListener	l );
}
