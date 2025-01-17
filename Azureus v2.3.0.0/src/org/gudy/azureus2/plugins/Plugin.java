/*
 * File    : Plugin.java
 * Created : 2 nov. 2003 18:43:21
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

/**
 * Defines the plugin interface to implement in order to create a Plugin
 * @author Olivier
 */

public interface 
Plugin 
{  
	/**
	 * This method is called when the Plugin is loaded by Azureus
	 * @param pluginInterface the interface that the plugin must use to communicate with Azureus
   *
   * @since 2.0.4.0
	 */
	
  public void 
  initialize(
  		PluginInterface pluginInterface )
  
  	throws PluginException;
  
  /**
   * This method is invoke by reflection, if it exists. It couldn't be added to the interface as
   * doing so would have broken existing plugins.
   * It is called at load time, and as such *most* of the plugin framework is unavailable. Therefore
   * it should only be used for bootstrap situations where the plugin must perform some action before
   * the rest of Azureus initialises.
   * The only framework guaranteed to be available is the plugin-configuration.  
   * 
   * public void
   * load(
   * 	PluginInterface	pluginInterface )
   * 
   * 	throws PluginException;
   * @author parg
   * @since 2.2.0.3
   */
}
