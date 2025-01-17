/*
 * File    : PluginManager.java
 * Created : 14-Dec-2003
 * By      : parg
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

import org.gudy.azureus2.pluginsimpl.local.PluginManagerImpl;

/**
 * This class allows Azureus to be started as an embedded component and also allows plugins to
 * be dynamically registered
 * @author parg
 */


public abstract class 
PluginManager
{
  /**
   * No user interface
   */  
	public static final int	UI_NONE		= 0;
  /**
   * SWT user inferface
   */  
	public static final int	UI_SWT		= 1;
	

  /**
   * Property Key: Allow multiple instances.
   * Normally Azureus will only permit a single instance to run per machine.
   * Values for this key are: "true" or "false"
   */  
	public static final String	PR_MULTI_INSTANCE	= "MULTI_INSTANCE";
	
  /**
   * Runs Azureus
   * @param ui_type Type of user interface to provide.  See UI_* Constants
   * @param properties A list of properties to pass Azureus.  See PR_* constants.
   */  
	public static void
	startAzureus(
		int			ui_type,
		Properties	properties )
	{
		PluginManagerImpl.startAzureus( ui_type, properties );
	}
	
  /**
   * Shuts down Azureus
   * @throws PluginException
   */  
	public static void
	stopAzureus()
	
		throws PluginException
	{
		PluginManagerImpl.stopAzureus();
	}
	
	/**
	 * restarts azureus and performs any Update actions defined via the plugin "update"
	 * interface. Currently only works for SWT UIs.
	 * @throws PluginException
	 */
	
	public static void
	restartAzureus()
	
		throws PluginException
	{
		PluginManagerImpl.restartAzureus();
	}
	
	/**
	 * Programatic plugin registration interface
	 * @param plugin_class	this must implement Plugin
	 */
	
	public static void
	registerPlugin(
		Class		plugin_class )
	{
		PluginManagerImpl.registerPlugin( plugin_class );
	}
	
	/**
	 * returns the plugin interface with a given id, or null if not found
	 * @param id
	 * @return
	 */
	
	public static PluginInterface
	getPluginInterfaceByID(
		String		id )
	{
		return( PluginManagerImpl.getPluginInterfaceByID(id));
	}
	
	public static PluginInterface
	getPluginInterfaceByClass(
		Class		c )
	{
		return( PluginManagerImpl.getPluginInterfaceByClass(c));
	}

	/**
	 * Gets the current set of registered plugins. During initialisation this will probably give partial
	 * results as plugin initialisation is non-deterministic.
	 * @return
	 */
	
	public static PluginInterface[]
	getPluginInterfaces()
	{
		return( PluginManagerImpl.getPluginInterfaces());
	}
	
	/**
	 * Gets the current set of registered plugins. During initialisation this will probably give partial
	 * results as plugin initialisation is non-deterministic.
	 * @return
	 */
	
	public abstract PluginInterface[]
	getPlugins();
}
