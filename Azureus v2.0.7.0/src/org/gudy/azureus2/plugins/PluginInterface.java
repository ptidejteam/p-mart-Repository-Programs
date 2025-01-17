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
import org.gudy.azureus2.plugins.logging.Logger;
import org.gudy.azureus2.plugins.download.DownloadManager;
import org.gudy.azureus2.plugins.peers.protocol.PeerProtocolManager;
import org.gudy.azureus2.plugins.ui.config.Parameter;
import org.gudy.azureus2.plugins.ui.config.PluginConfigUIFactory;
import org.gudy.azureus2.plugins.ui.tables.peers.PluginPeerItemFactory;


/**
 * Defines the communication interface between Azureus and Plugins
 * @author Olivier
 */
public interface PluginInterface {  
	
  /**
   * A Plugin might call this method to add a View to Azureus's views
   * The View will be accessible from View > Plugins > View name
   * @param view The PluginView to be added
   */
  public void addView(PluginView view);
  
  /**
   * adds a tab under the 'plugins' tab in the config view.<br>
   * @param parameters the Parameter(s) to be edited
   * @param displayName the under which it should display.<br>
   * Azureus will look-up for ConfigView.plugins.displayName into the lang files
   * in order to find the localized displayName. (see i18n)
   */
  public void addConfigUIParameters(Parameter[] parameters, String displayName);
  
  /**
   * adds a column to the peers table.<br>
   * @param columnName the key name of the column
   * @param factory the factory responsible of creating items.
   * Azureus will look-up for PeersView.columnName into the lang files
   * in order to find the localized displayName. (see i18n)
   */
  public void addColumnToPeersTable(String columnName,PluginPeerItemFactory factory);
  
  
  /**
   * Gives access to the tracker functionality
   * @return The tracker
   */
  
  public Tracker getTracker();
  
  /**
   * Gives access to the logger
   * @return The logger
   */
  
  public Logger getLogger();
  
  /**
   * Gives access to the download manager
   * @return
   */
  
  public DownloadManager
  getDownloadManager();
  
  /**
   * Gives access to the peer protocol manager
   * @return
   */
  
  public PeerProtocolManager
  getPeerProtocolManager();
  
  /**
   * Gives access to the sharing functionality
   * @return
   */
  
  public ShareManager
  getShareManager()
  
  	throws ShareException;
  
  /**
   * opens a torrent file given its name
   * @param fileName The Name of the file that azureus must open
   * @deprecated Use getDownloadManager().addDownload()
   */
  public void openTorrentFile(String fileName);
  
  /**
   * opens a torrent file given the url it's at
   * @param url The String representation of the url pointing to a torrent file
    *@deprecated Use getDownloadManager().addDownload()
  */
  public void openTorrentURL(String url);
  
  /**
   * gives access to the plugin properties
   * @return the properties from the file plugin.properties
   */
  public Properties getPluginProperties();
  
  /**
   * gives access to the plugin installation path
   * @return the full path the plugin is installed in
   */
  public String getPluginDirectoryName();
  
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
  
  public void
  addListener(
  	PluginListener	l );
  
  public void
  removeListener(
  	PluginListener	l );
}
